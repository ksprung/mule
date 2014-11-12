package org.mule.extensions.introspection.fluent;

public final class WithParameters
{
    private final HasParameters target;
    private final DeclarationConstruct declaration;

    protected WithParameters(HasParameters target, DeclarationConstruct declaration)
    {
        this.target = target;
        this.declaration = declaration;
    }

    public ParameterDeclarationConstruct requiredParameter(String name) {
        return addParameter(name, true);
    }

    public ParameterDeclarationConstruct optionalParameter(String name) {
        return addParameter(name, false);
    }

    public ConfigDeclarationConstruct withConfig(String name)
    {
        return declaration.withConfig(name);
    }

    public OperationDeclarationConstruct withOperation(String name)
    {
        return declaration.withOperation(name);
    }

    private ParameterDeclarationConstruct addParameter(String name, boolean required) {
        ParameterDeclaration parameter = new ParameterDeclaration();
        parameter.setName(name);
        parameter.setRequired(required);
        target.addParameter(parameter);

        return new ParameterDeclarationConstruct(parameter, declaration);
    }

}
