package org.mule.extensions.introspection.fluent;

import java.util.List;

public final class OperationDeclaration
{

    private final String name;
    private String description;
    private List<ParameterDeclaration> parameters;

    public OperationDeclaration(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<ParameterDeclaration> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<ParameterDeclaration> parameters)
    {
        this.parameters = parameters;
    }
}
