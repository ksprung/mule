/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent;

public class ConfigDeclarationConstruct implements HasParameters
{

    private final ConfigDeclaration config;
    private final DeclarationConstruct declaration;

    public ConfigDeclarationConstruct(ConfigDeclaration config, DeclarationConstruct declaration)
    {
        this.config = config;
        this.declaration = declaration;
    }

    public WithParameters with() {
        return new WithParameters(this, declaration);
    }

    @Override
    public void addParameter(ParameterDeclaration parameter)
    {
        config.getParameters().add(parameter);
    }


}
