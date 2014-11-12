package org.mule.extensions.introspection.fluent;

import java.util.LinkedList;
import java.util.List;

public final class ConfigDeclaration
{

    private final String name;
    private String description;
    private Class<?> declaringClass;
    private List<ParameterDeclaration> parameters = new LinkedList<>();

    public ConfigDeclaration(String name)
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

    public Class<?> getDeclaringClass()
    {
        return declaringClass;
    }

    public void setDeclaringClass(Class<?> declaringClass)
    {
        this.declaringClass = declaringClass;
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
