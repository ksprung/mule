/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mule.extensions.annotation.Extension.DEFAULT_CONFIG_NAME;
import static org.mule.extensions.introspection.DataQualifier.BOOLEAN;
import static org.mule.extensions.introspection.DataQualifier.DATE_TIME;
import static org.mule.extensions.introspection.DataQualifier.DECIMAL;
import static org.mule.extensions.introspection.DataQualifier.ENUM;
import static org.mule.extensions.introspection.DataQualifier.INTEGER;
import static org.mule.extensions.introspection.DataQualifier.LIST;
import static org.mule.extensions.introspection.DataQualifier.MAP;
import static org.mule.extensions.introspection.DataQualifier.OPERATION;
import static org.mule.extensions.introspection.DataQualifier.POJO;
import static org.mule.extensions.introspection.DataQualifier.STRING;
import static org.mule.module.extensions.HeisenbergExtension.AGE;
import static org.mule.module.extensions.HeisenbergExtension.EXTENSION_DESCRIPTION;
import static org.mule.module.extensions.HeisenbergExtension.EXTENSION_NAME;
import static org.mule.module.extensions.HeisenbergExtension.EXTENSION_VERSION;
import static org.mule.module.extensions.HeisenbergExtension.HEISENBERG;
import static org.mule.module.extensions.HeisenbergExtension.NAMESPACE;
import static org.mule.module.extensions.HeisenbergExtension.SCHEMA_LOCATION;
import static org.mule.module.extensions.HeisenbergExtension.SCHEMA_VERSION;
import org.mule.extensions.annotation.Configurable;
import org.mule.extensions.annotation.Configurations;
import org.mule.extensions.annotation.capability.Xml;
import org.mule.extensions.introspection.DataQualifier;
import org.mule.extensions.introspection.DataType;
import org.mule.extensions.introspection.Describer;
import org.mule.extensions.introspection.Operation;
import org.mule.extensions.introspection.declaration.ConfigurationDeclaration;
import org.mule.extensions.introspection.declaration.Construct;
import org.mule.extensions.introspection.declaration.Declaration;
import org.mule.extensions.introspection.declaration.OperationDeclaration;
import org.mule.extensions.introspection.declaration.ParameterDeclaration;
import org.mule.module.extensions.Door;
import org.mule.module.extensions.HealthStatus;
import org.mule.module.extensions.HeisenbergExtension;
import org.mule.module.extensions.internal.introspection.AnnotationsBasedDescriber;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class AnnotationsBasedDescriberTestCase extends AbstractMuleTestCase
{

    private static final String EXTENDED_CONFIG_NAME = "extendedConfig";
    private static final String EXTENDED_CONFIG_DESCRIPTION = "extendedDescription";

    private static final String SAY_MY_NAME_OPERATION = "sayMyName";
    private static final String GET_ENEMY_OPERATION = "getEnemy";
    private static final String KILL_OPERATION = "kill";
    private static final String KILL_CUSTOM_OPERATION = "killWithCustomMessage";
    private static final String HIDE_METH_IN_EVENT_OPERATION = "hideMethInEvent";
    private static final String HIDE_METH_IN_MESSAGE_OPERATION = "hideMethInMessage";

    private Describer describer;

    @Before
    public void setUp()
    {
        describer = describerFor(HeisenbergExtension.class);
    }

    protected Describer describerFor(final Class<?> type)
    {
        return new AnnotationsBasedDescriber(type);
    }

    @Test
    public void describeTestModule() throws Exception
    {
        Construct construct = describer.describe();

        Declaration declaration = construct.getRootConstruct().getDeclaration();
        assertExtensionProperties(declaration);

        assertTestModuleConfiguration(declaration);
        assertTestModuleOperations(declaration);

        assertCapabilities(declaration);
    }

    @Test
    public void heisengergPointer() throws Exception
    {
        describer = describerFor(HeisenbergPointer.class);
        describeTestModule();
    }

    @Test
    public void heisengergPointerPlusExternalConfig() throws Exception
    {
        describer = describerFor(HeisengergPointerPlusExternalConfig.class);
        Declaration declaration = describer.describe().getRootConstruct().getDeclaration();

        assertExtensionProperties(declaration);
        assertThat(declaration.getConfigurations().size(), equalTo(2));

        ConfigurationDeclaration configuration = declaration.getConfigurations().get(1);
        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getName(), equalTo(EXTENDED_CONFIG_NAME));
        assertThat(configuration.getParameters(), hasSize(1));
        assertParameter(configuration.getParameters().get(0), "extendedProperty", "", String.class, STRING, true, true, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void heisengergPointerPlusUnnamedExternalConfig() throws Exception
    {
        describer = describerFor(HeisengergPointerPlusUnnamedExternalConfig.class);
        describer.describe();
    }


    private void assertTestModuleConfiguration(Declaration declaration) throws Exception
    {
        assertThat(declaration.getConfigurations(), hasSize(1));
        ConfigurationDeclaration conf = declaration.getConfigurations().get(0);
        assertThat(conf.getName(), equalTo(DEFAULT_CONFIG_NAME));

        List<ParameterDeclaration> parameters = conf.getParameters();
        assertThat(parameters, hasSize(13));

        assertParameter(parameters.get(0), "myName", "", String.class, STRING, false, true, HEISENBERG);
        assertParameter(parameters.get(1), "age", "", Integer.class, INTEGER, false, true, AGE);
        assertParameter(parameters.get(2), "enemies", "", List.class, LIST, true, true, null);
        assertParameter(parameters.get(3), "money", "", BigDecimal.class, DECIMAL, true, true, null);
        assertParameter(parameters.get(4), "cancer", "", boolean.class, BOOLEAN, true, true, null);
        assertParameter(parameters.get(4), "cancer", "", boolean.class, BOOLEAN, true, true, null);
        assertParameter(parameters.get(5), "dateOfBirth", "", Date.class, DATE_TIME, true, true, null);
        assertParameter(parameters.get(6), "dateOfDeath", "", Calendar.class, DATE_TIME, true, true, null);
        assertParameter(parameters.get(7), "recipe", "", Map.class, MAP, false, true, null);
        assertParameter(parameters.get(8), "ricinPacks", "", Set.class, LIST, false, true, null);
        assertParameter(parameters.get(9), "nextDoor", "", Door.class, POJO, false, true, null);
        assertParameter(parameters.get(10), "candidateDoors", "", Map.class, MAP, false, true, null);
        assertParameter(parameters.get(11), "initialHealth", "", HealthStatus.class, ENUM, true, true, null);
        assertParameter(parameters.get(12), "finalHealth", "", HealthStatus.class, ENUM, true, true, null);

    }

    private void assertExtensionProperties(Declaration declaration)
    {
        assertThat(declaration, is(notNullValue()));

        assertThat(declaration.getName(), is(EXTENSION_NAME));
        assertThat(declaration.getDescription(), is(EXTENSION_DESCRIPTION));
        assertThat(declaration.getVersion(), is(EXTENSION_VERSION));
    }

    private void assertTestModuleOperations(Declaration declaration) throws Exception
    {
        assertThat(declaration.getOperations(), hasSize(6));
        assertOperation(declaration, SAY_MY_NAME_OPERATION, "");
        assertOperation(declaration, GET_ENEMY_OPERATION, "");
        assertOperation(declaration, KILL_OPERATION, "");
        assertOperation(declaration, KILL_CUSTOM_OPERATION, "");
        assertOperation(declaration, HIDE_METH_IN_EVENT_OPERATION, "");
        assertOperation(declaration, HIDE_METH_IN_MESSAGE_OPERATION, "");

        OperationDeclaration operation = getOperation(declaration, SAY_MY_NAME_OPERATION);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters().isEmpty(), is(true));

        operation = getOperation(declaration, GET_ENEMY_OPERATION);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters(), hasSize(1));
        assertParameter(operation.getParameters().get(0), "index", "", int.class, INTEGER, true, true, null);

        operation = getOperation(declaration, KILL_OPERATION);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters(), hasSize(1));
        assertParameter(operation.getParameters().get(0), "enemiesLookup", "", Operation.class, OPERATION, true, true, null);

        operation = getOperation(declaration, KILL_CUSTOM_OPERATION);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters(), hasSize(2));
        assertParameter(operation.getParameters().get(0), "goodbyeMessage", "", String.class, STRING, false, true, "#[payload]");
        assertParameter(operation.getParameters().get(1), "enemiesLookup", "", Operation.class, OPERATION, true, true, null);

        operation = getOperation(declaration, HIDE_METH_IN_EVENT_OPERATION);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters().isEmpty(), is(true));

        operation = getOperation(declaration, HIDE_METH_IN_MESSAGE_OPERATION);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters().isEmpty(), is(true));
    }

    private void assertOperation(Declaration declaration,
                                 String operationName,
                                 String operationDescription) throws Exception
    {

        OperationDeclaration operation = getOperation(declaration, operationName);
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getDescription(), equalTo(operationDescription));
    }

    private OperationDeclaration getOperation(Declaration declaration, final String operationName)
    {
        return (OperationDeclaration) CollectionUtils.find(declaration.getOperations(), new Predicate()
        {
            @Override
            public boolean evaluate(Object object)
            {
                return ((OperationDeclaration) object).getName().equals(operationName);
            }
        });
    }

    private void assertParameter(ParameterDeclaration param,
                                 String name,
                                 String description,
                                 Class<?> type,
                                 DataQualifier qualifier,
                                 boolean required,
                                 boolean dynamic,
                                 Object defaultValue)
    {
        assertThat(param.getName(), equalTo(name));
        assertThat(param.getDescription(), equalTo(description));
        assertThat(param.getType(), equalTo(DataType.of(type)));
        assertThat(param.getType().getQualifier(), is(qualifier));
        assertThat(param.isRequired(), is(required));
        assertThat(param.isDynamic(), is(dynamic));
        assertThat(param.getDefaultValue(), equalTo(defaultValue));
    }

    protected void assertCapabilities(Declaration declaration)
    {
        // template method for asserting custom capabilities in modules that define them
    }

    @org.mule.extensions.annotation.Extension(name = EXTENSION_NAME, description = EXTENSION_DESCRIPTION, version = EXTENSION_VERSION)
    @Xml(schemaLocation = SCHEMA_LOCATION, namespace = NAMESPACE, schemaVersion = SCHEMA_VERSION)
    @Configurations(HeisenbergExtension.class)
    public static class HeisenbergPointer extends HeisenbergExtension
    {

    }

    @org.mule.extensions.annotation.Extension(name = EXTENSION_NAME, description = EXTENSION_DESCRIPTION, version = EXTENSION_VERSION)
    @Xml(schemaLocation = SCHEMA_LOCATION, namespace = NAMESPACE, schemaVersion = SCHEMA_VERSION)
    @Configurations({HeisenbergExtension.class, NamedHeisenbergAlternateConfig.class})
    public static class HeisengergPointerPlusExternalConfig
    {

    }

    @org.mule.extensions.annotation.Extension(name = EXTENSION_NAME, description = EXTENSION_DESCRIPTION, version = EXTENSION_VERSION)
    @Xml(schemaLocation = SCHEMA_LOCATION, namespace = NAMESPACE, schemaVersion = SCHEMA_VERSION)
    @Configurations({HeisenbergExtension.class, HeisenbergAlternateConfig.class})
    public static class HeisengergPointerPlusUnnamedExternalConfig
    {

    }

    @org.mule.extensions.annotation.Configuration(name = EXTENDED_CONFIG_NAME, description = EXTENDED_CONFIG_DESCRIPTION)
    public static class NamedHeisenbergAlternateConfig extends HeisenbergAlternateConfig
    {

    }

    public static class HeisenbergAlternateConfig
    {

        @Configurable
        private String extendedProperty;

        public String getExtendedProperty()
        {
            return extendedProperty;
        }

        public void setExtendedProperty(String extendedProperty)
        {
            this.extendedProperty = extendedProperty;
        }
    }

}
