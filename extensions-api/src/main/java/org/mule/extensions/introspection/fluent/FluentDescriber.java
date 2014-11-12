/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.fluent;

import org.mule.extensions.introspection.fluent.internal.Declaration;

public class FluentDescriber
{



    public void test() {

        new Declaration("ws-consumer", "1.0").describedAs("Web Service Consumer")
                .withConfig("config").declaredIn(String.class)
                    .with().requiredParameter("wsdl-location").describedAs("uri to find the wsdl").ofType(String.class).whichIsNotDynamic()
                    .with().requiredParameter("service").describedAs("serviceName").ofType(String.class)
                .withConfig("newConnector")
                    .with().requiredParameter("wsdl-location").describedAs("uri to find the wsdl").ofType(String.class).whichIsDynamic()
                    .with().requiredParameter("service").describedAs("serviceName").ofType(String.class)
                    .with().optionalParameter("connector").describedAs("The connector to use").ofType(Object.class)
                .withOperation("consume").describedAs("Go get them tiger!")
                    .with().requiredParameter("operation").describedAs("the operation to execute").ofType(String.class)
                    .with().optionalParameter("mtomEnabled").describedAs("Whether to use mtom or not").ofType(boolean.class)








    }

}
