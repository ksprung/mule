/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.common.MuleVersion;
import org.mule.extensions.introspection.api.Described;
import org.mule.extensions.introspection.api.Extension;
import org.mule.extensions.introspection.api.ExtensionBuilder;
import org.mule.extensions.introspection.api.Configuration;
import org.mule.extensions.introspection.api.ExtensionConfigurationBuilder;
import org.mule.extensions.introspection.api.ExtensionOperationBuilder;
import org.mule.extensions.introspection.api.ExtensionParameterBuilder;
import org.mule.module.extensions.internal.util.MuleExtensionUtils;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of {@link org.mule.extensions.introspection.api.ExtensionBuilder}
 * which builds instances of {@link ImmutableExtension}
 *
 * @since 3.7.0
 */
public final class DefaultExtensionBuilder extends AbstractCapabilityAwareBuilder<Extension, ExtensionBuilder>
        implements NavigableExtensionBuilder
{

    private String name;
    private String description;
    private String version;
    private Class<?> declaringClass;
    private List<ExtensionConfigurationBuilder> configurations = new LinkedList<>();
    private List<ExtensionOperationBuilder> operations = new LinkedList<>();

    public static ExtensionBuilder newBuilder()
    {
        return new DefaultExtensionBuilder();
    }

    private DefaultExtensionBuilder()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionBuilder setDescription(String description)
    {
        this.description = description;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription()
    {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionBuilder setVersion(String version)
    {
        this.version = version;
        return this;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionBuilder setDeclaringClass(Class<?> declaringClass)
    {
        this.declaringClass = declaringClass;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDeclaringClass()
    {
        return declaringClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionBuilder addConfiguration(ExtensionConfigurationBuilder configuration)
    {
        checkArgument(configuration != null, "cannot add a null configuration builder");
        configurations.add(configuration);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExtensionConfigurationBuilder> getConfigurations()
    {
        return ImmutableList.copyOf(configurations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionBuilder addOperation(ExtensionOperationBuilder operation)
    {
        checkArgument(operation != null, "Cannot add a null operation builder");
        operations.add(operation);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExtensionOperationBuilder> getOperations()
    {
        return ImmutableList.copyOf(operations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Extension build()
    {
        validateMuleVersion();
        return new ImmutableExtension(name,
                                      description,
                                      version,
                                      declaringClass,
                                      sortConfigurations(MuleExtensionUtils.build(configurations)),
                                      sort(MuleExtensionUtils.build(operations)),
                                      capabilities);
    }

    private void validateMuleVersion()
    {
        // make sure version is valid
        try
        {
            new MuleVersion(version);
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException(String.format("Invalid %s version: %s", description, version));
        }
    }

    private List<Configuration> sortConfigurations(List<Configuration> configurations)
    {
        List<Configuration> sorted = new ArrayList<>(configurations.size());
        sorted.add(configurations.get(0));

        if (configurations.size() > 1)
        {
            sorted.addAll(sort(configurations.subList(1, configurations.size())));
        }

        return sorted;
    }

    private <T extends Described> List<T> sort(List<T> list)
    {
        Collections.sort(list, new DescribedComparator());
        return list;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionConfigurationBuilder newConfiguration()
    {
        return new DefaultExtensionConfigurationBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionOperationBuilder newOperation()
    {
        return new DefaultExtensionOperationBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionParameterBuilder newParameter()
    {
        return new DefaultExtensionParameterBuilder();
    }

    private class DescribedComparator implements Comparator<Described>
    {

        @Override
        public int compare(Described o1, Described o2)
        {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
