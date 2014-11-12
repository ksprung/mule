/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent;

public final class OperationDeclarationConstruct implements HasParameters
{

    private final OperationDeclaration operation;
    private final DeclarationConstruct declaration;

    public OperationDeclarationConstruct(OperationDeclaration operation, DeclarationConstruct declaration)
    {
        this.operation = operation;
        this.declaration = declaration;
    }

    public OperationDeclarationConstruct describedAs(String description)
    {
        operation.setDescription(description);
        return this;
    }

    public WithParameters with()
    {
        return new WithParameters(this, declaration);
    }

    @Override
    public void addParameter(ParameterDeclaration parameter)
    {
        operation.getParameters().add(parameter);
    }

    public OperationDeclarationConstruct withOperation(String name)
    {
        return declaration.withOperation(name);
    }

    public ConfigDeclarationConstruct withConfig(String name)
    {
        return declaration.withConfig(name);
    }
}
