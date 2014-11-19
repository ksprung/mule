/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import static org.mule.module.extensions.internal.util.IntrospectionUtils.checkInstantiable;
import static org.mule.util.Preconditions.checkArgument;
import org.mule.api.MuleRuntimeException;
import org.mule.extensions.introspection.ConfigurationInstantiator;

final class TypeAwareConfigurationInstantiator implements ConfigurationInstantiator
{

    private final Class<?> configurationType;

    TypeAwareConfigurationInstantiator(Class<?> configurationType)
    {
        checkArgument(configurationType != null, "configuration type cannot be null");
        checkInstantiable(configurationType);
        this.configurationType = configurationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object newInstance()
    {
        try
        {
            return configurationType.newInstance();
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(createStaticMessage("Could not instantiate configuration of type " + configurationType.getName()), e);
        }
    }
}
