/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class Declaration
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

    public String getVersion()
    {
        return version;
    }

    public String getName()
    {
        return name;
    }

    public List<ConfigDeclaration> getConfigs()
    {
        return configs;
    }

    public List<OperationDeclaration> getOperations()
    {
        return operations;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
