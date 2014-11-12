package org.mule.extensions.introspection.fluent;

public final class ParameterDeclaration
{
    private String name;
    private boolean required;
    private boolean dynamic = true;
    private Class<?> type;
    private Class<?>[] parametrizedTypes;


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public boolean isDynamic()
    {
        return dynamic;
    }

    public void setDynamic(boolean dynamic)
    {
        this.dynamic = dynamic;
    }

    public Class<?> getType()
    {
        return type;
    }

    public void setType(Class<?> type)
    {
        this.type = type;
    }

    public Class<?>[] getParametrizedTypes()
    {
        return parametrizedTypes;
    }

    public void setParametrizedTypes(Class<?>[] parametrizedTypes)
    {
        this.parametrizedTypes = parametrizedTypes;
    }
}
