/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent.internal;

import org.mule.extensions.introspection.fluent.OptionalParameterDeclaration;
import org.mule.extensions.introspection.fluent.ParameterDeclaration;
import org.mule.extensions.introspection.fluent.WithParameters;

public final class OptionalParameterDeclarationImpl extends ParameterDeclarationImpl<OptionalParameterDeclaration> implements OptionalParameterDeclaration
{
    private Object defaultValue = null;

    OptionalParameterDeclarationImpl(String name, WithParameters withParameters) {
        super(name, false, withParameters);
    }

    @Override
    public OptionalParameterDeclaration defaultingTo(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public ParameterDeclaration describedAs(String description)
    {
        return null;
    }
}
