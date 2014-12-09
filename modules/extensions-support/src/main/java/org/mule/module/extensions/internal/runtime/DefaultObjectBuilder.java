/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.runtime;

import org.mule.api.MuleRuntimeException;
import org.mule.config.i18n.MessageFactory;
import org.mule.module.extensions.internal.util.IntrospectionUtils;
import org.mule.util.ClassUtils;

/**
 * Default implementation of {@link ObjectBuilder}
 *
 * @since 3.7.0
 */
public class DefaultObjectBuilder extends BaseObjectBuilder
{

    private Class<?> prototypeClass;

    public DefaultObjectBuilder(Class<?> prototypeClass)
    {
        IntrospectionUtils.checkInstantiable(prototypeClass);
        this.prototypeClass = prototypeClass;
    }

    @Override
    protected Object instantiateObject()
    {
        try
        {
            return ClassUtils.instanciateClass(prototypeClass);
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(MessageFactory.createStaticMessage("Could not create instance of " + prototypeClass), e);
        }
    }
}
