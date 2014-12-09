/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.runtime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static org.mule.module.extensions.internal.util.ExtensionsTestUtils.getParameter;
import org.mule.api.MuleEvent;
import org.mule.extensions.introspection.Configuration;
import org.mule.extensions.introspection.Parameter;
import org.mule.module.extensions.HeisenbergExtension;
import org.mule.module.extensions.internal.runtime.resolver.ResolverSet;
import org.mule.module.extensions.internal.runtime.resolver.StaticValueResolver;
import org.mule.module.extensions.internal.runtime.resolver.ValueResolver;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationResolverSetObjectBuilderTestCase extends AbstractMuleTestCase
{

    private static final String MY_NAME = "heisenberg";
    private static final int AGE = 50;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private Configuration configuration;

    @Mock
    private ResolverSet resolverSet;

    @Mock
    private MuleEvent event;

    private ConfigurationResolverSetObjectBuilder builder;

    @Before
    public void before() throws Exception
    {
        when(configuration.getInstantiator().getObjectType()).thenReturn((Class) HeisenbergExtension.class);
        when(configuration.getInstantiator().newInstance()).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return new HeisenbergExtension();
            }
        });

        Map<Parameter, ValueResolver> parameters = new HashMap<>();
        parameters.put(getParameter("myName", String.class), new StaticValueResolver(MY_NAME));
        parameters.put(getParameter("age", Integer.class), new StaticValueResolver(AGE));
        when(resolverSet.getResolvers()).thenReturn(parameters);

        builder = new ConfigurationResolverSetObjectBuilder(configuration, resolverSet);
    }

    @Test
    public void toObjectBuilder() throws Exception
    {
        HeisenbergExtension heisenberg = (HeisenbergExtension) builder.build(event);
        assertThat(heisenberg, is(notNullValue()));
        assertThat(heisenberg.getMyName(), is(MY_NAME));
        assertThat(heisenberg.getAge(), is(AGE));
    }

}
