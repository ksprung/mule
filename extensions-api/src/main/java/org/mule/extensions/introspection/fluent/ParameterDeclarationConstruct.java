/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent;

public final class ParameterDeclarationConstruct
{

    private final ParameterDeclaration parameter;
    private final DeclarationConstruct declaration;

    public ParameterDeclarationConstruct(ParameterDeclaration parameter, DeclarationConstruct declaration)
    {
        this.parameter = parameter;
        this.declaration = declaration;
    }

    public ParameterDeclarationConstruct ofType(Class<?> type, Class<?>... parametrizedTypes)
    {
        parameter.setType(type);
        parameter.setParametrizedTypes(parametrizedTypes);

        return this;
    }

    public ParameterDeclarationConstruct whichIsNotDynamic() {
        parameter.setDynamic(false);
        return this;
    }

    public ParameterDeclarationConstruct whichIsDynamic() {
        parameter.setDynamic(true);
        return this;
    }

    public ConfigDeclarationConstruct withConfig(String name)
    {
        return declaration.withConfig(name);
    }

    public OperationDeclarationConstruct withOperation(String name)
    {
        return declaration.withOperation(name);
    }
}
