package org.mule.extensions.introspection.fluent;

public interface OptionalParameterDeclaration extends ParameterDeclaration<OptionalParameterDeclaration>
{

    OptionalParameterDeclaration defaultingTo(Object defaultValue);
}
