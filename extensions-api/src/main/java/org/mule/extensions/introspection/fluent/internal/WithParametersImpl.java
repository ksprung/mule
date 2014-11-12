/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent.internal;

import org.mule.extensions.introspection.fluent.ConfigDeclaration;
import org.mule.extensions.introspection.fluent.OperationDeclaration;
import org.mule.extensions.introspection.fluent.OptionalParameterDeclaration;
import org.mule.extensions.introspection.fluent.ParameterDeclaration;
import org.mule.extensions.introspection.fluent.WithParameters;

final class WithParametersImpl implements WithParameters
{
    private final HasParameters target;

    protected WithParametersImpl(HasParameters target)
    {
        this.target = target;
    }

    @Override
    public ParameterDeclaration requiredParameter(String name) {
        return addParameter(new ParameterDeclarationImpl(name, true, this));
    }

    @Override
    public OptionalParameterDeclaration optionalParameter(String name) {
        return addParameter(new OptionalParameterDeclarationImpl(name, this));
    }

    @Override
    public ConfigDeclaration withConfig(String name)
    {
        return target.getDeclaration().withConfig(name);
    }

    @Override
    public OperationDeclaration withOperation(String name)
    {
        return target.getDeclaration().withOperation(name);
    }

    private <T extends ParameterDeclarationImpl> T addParameter(T parameter) {
        target.addParameter(parameter);
        return parameter;
    }
}
