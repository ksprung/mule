/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.config;

import static org.mule.module.extensions.internal.config.XmlExtensionParserUtils.getResolverSet;
import org.mule.extensions.introspection.Configuration;
import org.mule.module.extensions.internal.runtime.resolver.ModuleConfigurationValueResolver;

import org.springframework.beans.factory.FactoryBean;

public final class ExtensionConfigurationFactoryBean implements FactoryBean<Object>
{
    private final String name;
    private final Configuration configuration;
    private final ElementDescriptor element;

    public ExtensionConfigurationFactoryBean(String name, Configuration configuration, ElementDescriptor element)
    {
        this.name = name;
        this.configuration = configuration;
        this.element = element;
    }

    @Override
    public Object getObject() throws Exception
    {
        return new ModuleConfigurationValueResolver(name, configuration, getResolverSet(element, configuration.getParameters()));
    }

    @Override
    public Class<?> getObjectType()
    {
        return ModuleConfigurationValueResolver.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
