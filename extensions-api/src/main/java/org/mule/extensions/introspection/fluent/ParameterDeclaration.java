package org.mule.extensions.introspection.fluent;

public interface ParameterDeclaration<T extends ParameterDeclaration> extends WithConfig, WithOperation, Describable<ParameterDeclaration>
{

    T ofType(Class<?> type, Class<?>... parametrizedTypes);

    T whichIsNotDynamic();

    T whichIsDynamic();

    WithParameters with();
}
