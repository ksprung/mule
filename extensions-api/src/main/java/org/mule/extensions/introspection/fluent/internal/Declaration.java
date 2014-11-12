/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent.internal;

import org.mule.extensions.introspection.fluent.ConfigDeclaration;
import org.mule.extensions.introspection.fluent.OperationDeclaration;
import org.mule.extensions.introspection.fluent.WithConfig;
import org.mule.extensions.introspection.fluent.WithOperation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Declaration implements WithConfig, WithOperation
{

    private final String name;
    private final String version;
    private String description;

    private List<ConfigDeclaration> configs = new ArrayList<>();
    private List<OperationDeclaration> operations = new LinkedList<>();

    public Declaration(String name, String version)
    {
        this.name = name;
        this.version = version;
    }

    public Declaration describedAs(String description) {
        this.description = description;
    }

    @Override
    public ConfigDeclaration withConfig(String name) {
        ConfigDeclaration config = new ConfigDeclarationImpl(name, this);
        configs.add(config);

        return config;
    }

    @Override
    public OperationDeclaration withOperation(String name)
    {
        OperationDeclaration operation = new OperationDeclarationImpl(name, this);
        operations.add(operation);

        return operation;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public String getDescription()
    {
        return description;
    }

    public List<ConfigDeclaration> getConfigs()
    {
        return configs;
    }

    public List<OperationDeclaration> getOperations()
    {
        return operations;
    }
}
