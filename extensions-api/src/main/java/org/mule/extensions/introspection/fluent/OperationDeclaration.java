package org.mule.extensions.introspection.fluent;

public interface OperationDeclaration
{

    OperationDeclaration describedAs(String description);

    WithParameters with();
}
