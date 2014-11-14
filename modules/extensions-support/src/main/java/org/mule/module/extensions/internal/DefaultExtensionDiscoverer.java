/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.api.registry.ServiceRegistry;
import org.mule.extensions.introspection.Describer;
import org.mule.extensions.introspection.Extension;
import org.mule.module.extensions.internal.introspection.ExtensionDiscoverer;
import org.mule.module.extensions.internal.introspection.ExtensionFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class DefaultExtensionDiscoverer implements ExtensionDiscoverer
{

    private final ExtensionFactory extensionFactory;
    private final ServiceRegistry serviceRegistry;

    public DefaultExtensionDiscoverer(ExtensionFactory extensionFactory, ServiceRegistry serviceRegistry)
    {
        this.extensionFactory = extensionFactory;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Extension> discover(ClassLoader classLoader)
    {
        checkArgument(classLoader != null, "classloader cannot be null");

        Iterator<Describer> describers = serviceRegistry.lookupProviders(Describer.class, classLoader);
        List<Extension> extensions = new LinkedList<>();
        while (describers.hasNext())
        {
            Describer describer = describers.next();
            extensions.add(extensionFactory.createFrom(describer.describe()));
        }

        return extensions;
    }
}
