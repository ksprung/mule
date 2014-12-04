/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.runtime.resolver;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.NamedObject;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.extensions.introspection.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * A {@link ValueResolver} for returning configurations of module extensions.
 * It supports both static and dynamic configurations (understanding by static
 * that non of its parameters have expressions, and dynamic that at least one of them does).
 * <p/>
 * In the case of static configurations, it will always return the same instance, in the case of
 * dynamic, it will evaluate those expressions and only return the same instance for equivalent
 * instances of {@link ResolverSetResult}.
 * <p/>
 * A {@link ResolverSet} is used for evaluating the attributes and creating new instances.
 * It also implements {@link NamedObject} since configurations are named and unique from a user's
 * point of view. Notice however that the named object is this resolver and in the case of
 * dynamic configurations instances are not likely to be unique
 * <p/>
 * Finally, this class implements {@link MuleContextAware} and {@link Lifecycle}. All the invocations
 * associated to those interfaces will be propagated to the underlying {@link #resolverSet} and
 * all generated instances
 *
 * @since 3.7.0
 */
public class ModuleConfigurationValueResolver extends ConfigurationValueResolver
{

    private final ResolverSet resolverSet;
    private final Configuration configuration;
    private ValueResolver resolver;

    public ModuleConfigurationValueResolver(String name, Configuration configuration, ResolverSet resolverSet)
    {
        super(name);
        this.configuration = configuration;
        this.resolverSet = resolverSet;

        if (resolverSet.isDynamic())
        {
            resolver = new CachedResolverSetValueResolver(moduleClass, resolverSet, 1, TimeUnit.MINUTES);
        }
        else
        {
            resolver = new ObjectBuilderValueResolver(resolverSet.toObjectBuilderOf(moduleClass));
        }
    }

    /**
     * Returns an instance associated with the given {@code event}
     *
     * @param event a {@link MuleEvent}
     * @return a configuration instance
     * @throws Exception
     */
    @Override
    public Object resolve(MuleEvent event) throws Exception
    {
        return resolver.resolve(event);
    }

    /**
     * Whether the generated configurations are dynamic or not
     */
    @Override
    public boolean isDynamic()
    {
        return resolver.isDynamic();
    }

    /**
     * Initialises this instance and propagates the event and the {@link MuleContext}
     * to the underlying {@code resolverSet}
     *
     * @throws InitialisationException
     */
    @Override
    public void initialise() throws InitialisationException
    {
        injectMuleContextIfNeeded(resolverSet);
        injectMuleContextIfNeeded(resolver);
        resolverSet.initialise();

        if (resolver instanceof Initialisable)
        {
            ((Initialisable) resolver).initialise();
        }
    }

    /**
     * Starts this instance and propagates the event to the underlying {@code resolverSet}
     *
     * @throws MuleException
     */
    @Override
    public void start() throws MuleException
    {
        resolverSet.start();

        if (resolver instanceof Startable)
        {
            ((Startable) resolver).start();
        }

        resolver = new InitialLifecycleValueResolver(resolver, muleContext);

        if (!resolver.isDynamic())
        {
            resolver = new CachingValueResolverWrapper(resolver);
        }
    }

    /**
     * Stop this instance and propagates the event to the underlying {@code resolverSet}
     *
     * @throws MuleException
     */
    @Override
    public void stop() throws MuleException
    {
        if (resolver instanceof Stoppable)
        {
            ((Stoppable) resolver).stop();
        }

        resolverSet.stop();
    }

    /**
     * Disposes this instance and propagates the event to the underlying {@code resolverSet}
     */
    @Override
    public void dispose()
    {
        if (resolver instanceof Disposable)
        {
            ((Disposable) resolver).dispose();
        }

        resolverSet.dispose();
    }
}