/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.runtime;

import org.mule.extensions.introspection.Configuration;
import org.mule.extensions.introspection.Parameter;
import org.mule.module.extensions.internal.runtime.resolver.ResolverSet;
import org.mule.module.extensions.internal.runtime.resolver.ValueResolver;
import org.mule.module.extensions.internal.util.IntrospectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class ConfigurationResolverSetObjectBuilder extends BaseObjectBuilder
{

    private final Configuration configuration;

    public ConfigurationResolverSetObjectBuilder(Configuration configuration, ResolverSet resolverSet)
    {
        this.configuration = configuration;
        final Class<?> prototypeClass = configuration.getInstantiator().getObjectType();

        for (Map.Entry<Parameter, ValueResolver> entry : resolverSet.getResolvers().entrySet())
        {
            Method setter = IntrospectionUtils.getSetter(prototypeClass, entry.getKey());
            addPropertyResolver(setter, entry.getValue());
        }
    }

    @Override
    protected Object instantiateObject()
    {
        return configuration.getInstantiator().newInstance();
    }
}
