/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import org.mule.extensions.introspection.OperationImplementation;

import java.util.concurrent.Future;

final class TypeAwareOperationImplementation implements OperationImplementation
{

    private final Class<?> actingClass;

    TypeAwareOperationImplementation(Class<?> actingClass)
    {
        this.actingClass = actingClass;
    }

    @Override
    public Future<Object> tbdHowToExecute()
    {
        throw new UnsupportedOperationException("coming soon!");
    }
}
