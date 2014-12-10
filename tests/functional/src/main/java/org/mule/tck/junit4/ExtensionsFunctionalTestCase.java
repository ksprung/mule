/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tck.junit4;

import org.mule.api.MuleContext;
import org.mule.api.registry.SPIServiceRegistry;
import org.mule.api.registry.ServiceRegistry;
import org.mule.extensions.introspection.Describer;
import org.mule.extensions.introspection.Extension;
import org.mule.extensions.resources.GenerableResource;
import org.mule.extensions.resources.ResourcesGenerator;
import org.mule.extensions.resources.spi.GenerableResourceContributor;
import org.mule.module.extensions.internal.introspection.AnnotationsBasedDescriber;
import org.mule.module.extensions.internal.introspection.DefaultExtensionFactory;
import org.mule.module.extensions.internal.introspection.ExtensionFactory;
import org.mule.module.extensions.internal.resources.AbstractResourcesGenerator;
import org.mule.util.ArrayUtils;
import org.mule.util.IOUtils;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Base test class for {@link org.mule.tck.junit4.FunctionalTestCase}s
 * that make use of components generated through the extensions API.
 * <p/>
 * The value added by this class in comparison to a traditional
 * {@link org.mule.tck.junit4.FunctionalTestCase} is that before creating
 * the {@link org.mule.api.MuleContext}, it scans certain packages of the
 * classpath and discovers extensions. Once those are discovered and described,
 * a {@link ResourcesGenerator} is used to automatically
 * generate any backing resources needed (for example, XSD schemas, spring bundles,
 * service registration files, etc).
 * <p/>
 * In this way, the user experience is greatly simplified when running the test
 * either through an IDE or build tool such as maven or gradle.
 * <p/>
 * By default, the only packaged scanned for extensions is
 * &quot;org.mule.extension&quot; but that can be customized
 * by overriding the {@link #getDiscoverablePackages()} method
 *
 * @since 3.6.0
 */
public abstract class ExtensionsFunctionalTestCase extends FunctionalTestCase
{

    private final ServiceRegistry serviceRegistry = new SPIServiceRegistry();
    private final ExtensionFactory extensionFactory = new DefaultExtensionFactory(serviceRegistry);

    @Override
    protected MuleContext createMuleContext() throws Exception
    {
        MuleContext muleContext = super.createMuleContext();
        discoverExtensions();

        return muleContext;
    }


    protected Class<?>[] getManagedExtensionTypes()
    {
        return null;
    }

    private List<GenerableResourceContributor> getGenerableResourceContributors()
    {
        return ImmutableList.copyOf(serviceRegistry.lookupProviders(GenerableResourceContributor.class));
    }

    private void discoverExtensions() throws Exception
    {
        List<Extension> extensions = new LinkedList<>();
        Class<?>[] managedExtensionTypes = getManagedExtensionTypes();

        if (ArrayUtils.isEmpty(managedExtensionTypes))
        {
            loadExtensionsFromDiscoveredDescribers(extensions);
        }
        else
        {
            loadExtensionsFromManagedTypes(extensions, managedExtensionTypes);
        }

        File targetDirectory = getGenerationTargetDirectory();

        ResourcesGenerator generator = new ExtensionsTestInfrastructureResourcesGenerator(serviceRegistry, targetDirectory);

        List<GenerableResourceContributor> resourceContributors = getGenerableResourceContributors();
        for (Extension extension : extensions)
        {
            for (GenerableResourceContributor contributor : resourceContributors)
            {
                contributor.contribute(extension, generator);
            }
        }

        generateResourcesAndAddToClasspath(generator);
    }

    private void loadExtensionsFromManagedTypes(List<Extension> extensions, Class<?>[] managedExtensionTypes)
    {
        for (Class<?> extensionType : managedExtensionTypes)
        {
            extensions.add(extensionFactory.createFrom(new AnnotationsBasedDescriber(extensionType).describe()));
        }
    }

    private void loadExtensionsFromDiscoveredDescribers(List<Extension> extensions)
    {
        for (Describer describer : serviceRegistry.lookupProviders(Describer.class, muleContext.getExecutionClassLoader()))
        {
            extensions.add(extensionFactory.createFrom(describer.describe()));
        }
    }

    private void generateResourcesAndAddToClasspath(ResourcesGenerator generator) throws Exception
    {
        ClassLoader cl = getClass().getClassLoader();
        Method method = org.springframework.util.ReflectionUtils.findMethod(cl.getClass(), "addURL", URL.class);
        method.setAccessible(true);

        for (GenerableResource resource : generator.dumpAll())
        {
            URL generatedResourceURL = new File(resource.getFilePath()).toURI().toURL();
            method.invoke(cl, generatedResourceURL);
        }
    }

    private File getGenerationTargetDirectory()
    {
        URL url = IOUtils.getResourceAsUrl(getEffectiveConfigFile(), getClass(), true, true);
        File targetDirectory = new File(FileUtils.toFile(url).getParentFile(), "META-INF");

        if (!targetDirectory.exists() && !targetDirectory.mkdir())
        {
            throw new RuntimeException("Could not create target directory " + targetDirectory.getAbsolutePath());
        }

        return targetDirectory;
    }

    private String getEffectiveConfigFile()
    {
        String configFile = getConfigFile();
        if (configFile != null)
        {
            return configFile;
        }

        configFile = getConfigFileFromSplittable(getConfigurationResources());
        if (configFile != null)
        {
            return configFile;
        }

        configFile = getConfigFileFromSplittable(getConfigResources());
        if (configFile != null)
        {
            return configFile;
        }

        String[] configFiles = getConfigFiles();
        if (!ArrayUtils.isEmpty(configFiles))
        {
            return configFiles[0].trim();
        }

        throw new IllegalArgumentException("No valid config file was specified");
    }

    private String getConfigFileFromSplittable(String configFile)
    {
        if (configFile != null)
        {
            return configFile.split(",")[0].trim();
        }

        return null;
    }


    private class ExtensionsTestInfrastructureResourcesGenerator extends AbstractResourcesGenerator
    {

        private File targetDirectory;

        private ExtensionsTestInfrastructureResourcesGenerator(ServiceRegistry serviceRegistry, File targetDirectory)
        {
            super(serviceRegistry);
            this.targetDirectory = targetDirectory;
        }

        @Override
        protected void write(GenerableResource resource)
        {
            File targetFile = new File(targetDirectory, resource.getFilePath());
            try
            {
                FileUtils.write(targetFile, resource.getContentBuilder().toString());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
    }
}
