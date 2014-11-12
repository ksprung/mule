/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent;

import org.mule.extensions.introspection.fluent.internal.OperationDeclarationImpl;

public class DeclarationConstruct
{

    private final Declaration declaration;

    public DeclarationConstruct(Declaration declaration)
    {
        this.declaration = declaration;
    }

    public DeclarationConstruct describedAs(String description) {
        declaration.setDescription(description);
        return this;
    }

    public ConfigDeclarationConstruct withConfig(String name) {
        ConfigDeclaration config = new ConfigDeclaration(name);
        declaration.getConfigs().add(config);

        return new ConfigDeclarationConstruct(config);
    }

    public OperationDeclarationConstruct withOperation(String name)
    {
        OperationDeclaration operation = new OperationDeclarationImpl(name, this);
        declaration.getOperations().add(operation);

        return operation;
    }
}
