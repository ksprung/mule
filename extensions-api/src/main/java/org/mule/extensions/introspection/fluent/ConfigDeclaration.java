package org.mule.extensions.introspection.fluent;

public interface ConfigDeclaration
{

    ConfigDeclaration describedAs(String description);

    ConfigDeclaration declaredIn(Class<?> declaringClass);

    WithParameters with();
}
