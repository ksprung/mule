/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent.internal;

import org.mule.extensions.introspection.fluent.ConfigDeclaration;
import org.mule.extensions.introspection.fluent.ParameterDeclaration;
import org.mule.extensions.introspection.fluent.WithParameters;

import java.util.LinkedList;
import java.util.List;

public final class ConfigDeclarationImpl implements HasParameters, ConfigDeclaration
{

    private final String name;
    private final Declaration declaration;
    private String description;
    private Class<?> declaringClass;
    private List<ParameterDeclaration> parameters = new LinkedList<>();

    public ConfigDeclarationImpl(String name, Declaration declaration)
    {
        this.name = name;
        this.declaration = declaration;
    }

    @Override
    public ConfigDeclaration describedAs(String description)
    {
        this.description = description;
        return this;
    }

    @Override
    public ConfigDeclaration declaredIn(Class<?> declaringClass)
    {
        this.declaringClass = declaringClass;
        return this;
    }

    @Override
    public void addParameter(ParameterDeclaration parameter)
    {
        parameters.add(parameter);
    }

    @Override
    public Declaration getDeclaration()
    {
        return declaration;
    }

    @Override
    public WithParameters with()
    {
        return new WithParametersImpl(this);
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getDeclaringClass()
    {
        return declaringClass;
    }

    public List<ParameterDeclaration> getParameters()
    {
        return parameters;
    }

    public String getDescription()
    {
        return description;
    }
}
