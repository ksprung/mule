package org.mule.extensions.introspection.fluent;

public interface WithParameters
{

    ParameterDeclaration requiredParameter(String name);

    OptionalParameterDeclaration optionalParameter(String name);


}
