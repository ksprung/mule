/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent.internal;

import org.mule.extensions.introspection.fluent.ConfigDeclaration;
import org.mule.extensions.introspection.fluent.OperationDeclaration;
import org.mule.extensions.introspection.fluent.ParameterDeclaration;
import org.mule.extensions.introspection.fluent.WithParameters;

public class ParameterDeclarationImpl<T extends ParameterDeclaration> implements ParameterDeclaration<T>
{
    private final String name;
    private final boolean required;
    private final WithParameters withParameters;
    private boolean dynamic = true;
    private Class<?> type;
    private Class<?>[] parametrizedTypes;

    protected ParameterDeclarationImpl(String name, boolean required, WithParameters withParameters)
    {
        this.name = name;
        this.required = required;
        this.withParameters = withParameters;
    }

    @Override
    public T ofType(Class<?> type, Class<?>... parametrizedTypes)
    {
        this.type = type;
        this.parametrizedTypes = parametrizedTypes;

        return (T) this;
    }

    @Override
    public T whichIsNotDynamic() {
        dynamic = false;
        return (T) this;
    }

    @Override
    public T whichIsDynamic() {
        dynamic = true;
        return (T) this;
    }

    @Override
    public ConfigDeclaration withConfig(String name)
    {
        return withParameters.withConfig(name);
    }

    @Override
    public OperationDeclaration withOperation(String name)
    {
        return withParameters.
    }

    public String getName()
    {
        return name;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isDynamic()
    {
        return dynamic;
    }

    @Override
    public WithParameters with() {
        return withParameters;
    }
}
