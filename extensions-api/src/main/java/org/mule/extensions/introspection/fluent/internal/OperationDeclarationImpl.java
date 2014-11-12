/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent.internal;

import org.mule.extensions.introspection.fluent.ConfigDeclaration;
import org.mule.extensions.introspection.fluent.OperationDeclaration;
import org.mule.extensions.introspection.fluent.OptionalParameterDeclaration;
import org.mule.extensions.introspection.fluent.ParameterDeclaration;
import org.mule.extensions.introspection.fluent.WithParameters;

import java.util.List;

public final class OperationDeclarationImpl implements OperationDeclaration, HasParameters
{

    private final Declaration declaration;
    private final String name;
    private String description;
    private List<ParameterDeclaration> parameters;

    OperationDeclarationImpl(String name, Declaration declaration)
    {
        this.name = name;
        this.declaration = declaration;
    }

    @Override
    public OperationDeclaration describedAs(String description)
    {
        this.description = description;
        return this;
    }

    @Override
    public WithParameters with()
    {
        return new WithParametersImpl(this);
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
    public OperationDeclaration withOperation(String name)
    {
        return declaration.withOperation(name);
    }

    @Override
    public ParameterDeclaration requiredParameter(String name)
    {
        ParameterDeclaration parameter = new ParameterDeclarationImpl(name, true, this);
        parameters.add(parameter);

        return parameter;
    }

    @Override
    public OptionalParameterDeclaration optionalParameter(String name)
    {
        OptionalParameterDeclaration parameter = new OptionalParameterDeclarationImpl(name, this);
        parameters.add(parameter);

        return parameter;
    }

    @Override
    public ConfigDeclaration withConfig(String name)
    {
        return declaration.withConfig(name);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public List<ParameterDeclaration> getParameters()
    {
        return parameters;
    }
}
