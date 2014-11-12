/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.mule.module.extensions.internal.util.IntrospectionUtils.checkInstantiable;
import static org.mule.module.extensions.internal.util.MuleExtensionUtils.immutableList;
import static org.mule.util.Preconditions.checkArgument;
import static org.mule.util.Preconditions.checkState;
import org.mule.extensions.introspection.DataType;
import org.mule.extensions.introspection.Operation;
import org.mule.extensions.introspection.Parameter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Immutable concrete implementation of {@link Operation}
 *
 * @since 3.7.0
 */
final class ImmutableOperation extends AbstractImmutableDescribed implements Operation
{

    private final List<DataType> inputTypes;
    private final DataType outputType;
    private final List<Parameter> parameters;
    private final Class<?> declaringClass;

    ImmutableOperation(String name,
                       String description,
                       Class<?> declaringClass,
                       List<DataType> inputTypes,
                       DataType outputType,
                       List<Parameter> parameters)
    {
        super(name, description);

        checkInstantiable(declaringClass);
        checkArgument(!CollectionUtils.isEmpty(inputTypes), "Must provide at least one input type");
        checkState(outputType != null, "Must provide an output type");

        this.declaringClass = declaringClass;
        this.inputTypes = immutableList(inputTypes);
        this.outputType = outputType;
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
    public List<DataType> getInputTypes()
    {
        return inputTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getOutputType()
    {
        return outputType;
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
