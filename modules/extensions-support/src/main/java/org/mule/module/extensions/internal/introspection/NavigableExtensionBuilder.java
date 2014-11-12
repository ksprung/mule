/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import org.mule.extensions.introspection.ExtensionBuilder;
import org.mule.extensions.introspection.ExtensionConfigurationBuilder;
import org.mule.extensions.introspection.ExtensionOperationBuilder;

import java.util.List;

public interface NavigableExtensionBuilder extends ExtensionBuilder
{

    String getName();

    String getDescription();

    String getVersion();

    Class<?> getDeclaringClass();

    List<ExtensionConfigurationBuilder> getConfigurations();

    List<ExtensionOperationBuilder> getOperations();

}
