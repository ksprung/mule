/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import org.mule.extensions.introspection.ExtensionOperationBuilder;
import org.mule.extensions.introspection.ExtensionParameterBuilder;
import org.mule.extensions.introspection.Operation;
import org.mule.module.extensions.internal.util.MuleExtensionUtils;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

final class DefaultExtensionOperationBuilder implements NavigableExtensionOperationBuilder
{

    private String name;
    private String description = StringUtils.EMPTY;
    private Class<?> declaringClass;
    private List<ExtensionParameterBuilder> parameters = new LinkedList<>();

    DefaultExtensionOperationBuilder()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionOperationBuilder setName(String name)
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
    public ExtensionOperationBuilder setDescription(String description)
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
    public Class<?> getDeclaringClass()
    {
        return declaringClass;
    }

    @Override
    public ExtensionOperationBuilder setDeclaringClass(Class<?> declaringClass)
    {
        this.declaringClass = declaringClass;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionOperationBuilder addParameter(ExtensionParameterBuilder parameter)
    {
        parameters.add(parameter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExtensionParameterBuilder> getParameters()
    {
        return ImmutableList.copyOf(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Operation build()
    {
        return new ImmutableOperation(name,
                                      description,
                                      declaringClass,
                                      MuleExtensionUtils.build(parameters));
    }
}
