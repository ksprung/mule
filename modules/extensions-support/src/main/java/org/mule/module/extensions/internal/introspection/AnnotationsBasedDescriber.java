/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.mule.module.extensions.internal.introspection.MuleExtensionAnnotationParser.getDefaultValue;
import static org.mule.module.extensions.internal.introspection.MuleExtensionAnnotationParser.getExtension;
import static org.mule.util.Preconditions.checkArgument;
import org.mule.extensions.annotation.Configurable;
import org.mule.extensions.annotation.Configuration;
import org.mule.extensions.annotation.Configurations;
import org.mule.extensions.annotation.Extension;
import org.mule.extensions.annotation.Operation;
import org.mule.extensions.annotation.Operations;
import org.mule.extensions.annotation.param.Optional;
import org.mule.extensions.introspection.DataType;
import org.mule.extensions.introspection.Describer;
import org.mule.extensions.introspection.declaration.ConfigurationConstruct;
import org.mule.extensions.introspection.declaration.Construct;
import org.mule.extensions.introspection.declaration.DeclarationConstruct;
import org.mule.extensions.introspection.declaration.OperationConstruct;
import org.mule.extensions.introspection.declaration.ParameterConstruct;
import org.mule.module.extensions.internal.util.IntrospectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Implementation of {@link Describer} which generates a {@link Construct} by
 * scanning annotations on a type returned by the abstract method
 * {@link #getExtensionType()}
 *
 * @since 3.7.0
 */
public abstract class AnnotationsBasedDescriber implements Describer
{
    private CapabilitiesResolver capabilitiesResolver = new CapabilitiesResolver();


    protected abstract Class<?> getExtensionType();

    /**
     * {@inheritDoc}
     */
    @Override
    public final Construct describe()
    {
        final Class<?> extensionType = getExtensionType();
        checkArgument(extensionType != null, String.format("activator %s does not specify an extension type", getClass().getName()));

        Extension extension = getExtension(getExtensionType());
        DeclarationConstruct declaration = newDeclarationConstruct(extension);
        declareConfigurations(declaration, extensionType);
        declareOperations(declaration, extensionType);
        describeCapabilities(declaration, extensionType);

        return declaration;
    }

    private DeclarationConstruct newDeclarationConstruct(Extension extension)
    {
        return new DeclarationConstruct(extension.name(), extension.version()).describedAs(extension.description());
    }

    private void declareConfigurations(DeclarationConstruct declaration, Class<?> extensionType)
    {
        Configurations configurations = extensionType.getAnnotation(Configurations.class);
        if (configurations != null)
        {
            for (Class<?> declaringClass : configurations.value())
            {
                declareConfiguration(declaration, declaringClass);
            }
        }
        else
        {
            declareConfiguration(declaration, extensionType);
        }
    }

    private void declareConfiguration(DeclarationConstruct declaration, Class<?> extensionType)
    {
        ConfigurationConstruct configuration;

        Configuration configurationAnnotation = extensionType.getAnnotation(Configuration.class);
        if (configurationAnnotation != null)
        {
            configuration = declaration.withConfig(configurationAnnotation.name()).describedAs(configurationAnnotation.description());
        }
        else
        {
            configuration = declaration.withConfig(Extension.DEFAULT_CONFIG_NAME).describedAs(Extension.DEFAULT_CONFIG_DESCRIPTION);
        }

        for (Field field : MuleExtensionAnnotationParser.getConfigurableFields(extensionType))
        {
            Configurable configurable = field.getAnnotation(Configurable.class);
            Optional optional = field.getAnnotation(Optional.class);

            ParameterConstruct parameter;
            DataType dataType = IntrospectionUtils.getFieldDataType(field);
            if (optional == null)
            {
                parameter = configuration.with().requiredParameter(field.getName());
            }
            else
            {
                parameter = configuration.with().optionalParameter(field.getName()).defaultingTo(getDefaultValue(optional, dataType));
            }

            parameter.ofType(dataType);

            if (!configurable.isDynamic())
            {
                parameter.whichIsNotDynamic();
            }
        }
    }

    private void declareOperations(DeclarationConstruct declaration, Class<?> extensionType)
    {
        Operations operations = extensionType.getAnnotation(Operations.class);
        if (operations != null)
        {
            for (Class<?> declaringClass : operations.value())
            {
                declareOperation(declaration, declaringClass);
            }
        }
        else
        {
            declareOperation(declaration, extensionType);
        }
    }

    private void declareOperation(DeclarationConstruct declaration, Class<?> extensionType)
    {
        for (Method method : MuleExtensionAnnotationParser.getOperationMethods(extensionType))
        {
            Operation annotation = method.getAnnotation(Operation.class);
            declareOperationParameters(method, declaration.withOperation(resolveOperationName(method, annotation)));
        }
    }

    private void declareOperationParameters(Method method, OperationConstruct operation)
    {
        List<ParameterDescriptor> descriptors = MuleExtensionAnnotationParser.parseParameter(method);

        for (ParameterDescriptor parameterDescriptor : descriptors)
        {
            ParameterConstruct parameter = parameterDescriptor.isRequired()
                                           ? operation.with().requiredParameter(parameterDescriptor.getName())
                                           : operation.with().optionalParameter(parameterDescriptor.getName()).defaultingTo(parameterDescriptor.getDefaultValue());

            parameter.describedAs(EMPTY).ofType(parameterDescriptor.getType());
        }
    }

    private String resolveOperationName(Method method, Operation operation)
    {
        return StringUtils.isBlank(operation.name()) ? method.getName() : operation.name();
    }



    private void describeCapabilities(DeclarationConstruct declaration, Class<?> extensionType)
    {
        capabilitiesResolver.resolveCapabilities(declaration, extensionType, declaration);
    }
}
