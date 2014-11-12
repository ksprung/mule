/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.mule.module.extensions.internal.util.IntrospectionUtils.checkInstantiable;
import static org.mule.module.extensions.internal.util.MuleExtensionUtils.immutableList;
import org.mule.extensions.introspection.Operation;
import org.mule.extensions.introspection.Parameter;

import java.util.List;

/**
 * Immutable concrete implementation of {@link Operation}
 *
 * @since 3.7.0
 */
final class ImmutableOperation extends AbstractImmutableDescribed implements Operation
{

    private final List<Parameter> parameters;
    private final Class<?> declaringClass;

    ImmutableOperation(String name,
                       String description,
                       Class<?> declaringClass,
                       List<Parameter> parameters)
    {
        super(name, description);

        checkInstantiable(declaringClass);

        this.declaringClass = declaringClass;
        this.parameters = immutableList(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Parameter> getParameters()
    {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDeclaringClass()
    {
        return this.declaringClass;
    }
}
