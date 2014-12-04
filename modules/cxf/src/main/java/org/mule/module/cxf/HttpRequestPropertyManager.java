/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.cxf;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.module.http.api.HttpConstants;
import org.mule.transport.http.HttpConnector;
import org.mule.util.StringUtils;

public class HttpRequestPropertyManager
{

    public static String getRequestPath(MuleMessage message)
    {
        String requestPath = message.getInboundProperty(HttpConnector.HTTP_REQUEST_PROPERTY, StringUtils.EMPTY);
        if(requestPath.equals(StringUtils.EMPTY))
        {
            requestPath = message.getInboundProperty(HttpConstants.RequestProperties.HTTP_REQUEST_URI, StringUtils.EMPTY);
        }
        return requestPath;
    }

    public static String getScheme(MuleEvent event)
    {
        String scheme = event.getMessageSourceURI().getScheme();
        if(scheme == null)
        {
            scheme = event.getMessage().getInboundProperty(HttpConstants.RequestProperties.HTTP_SCHEME);
        }
        return scheme;
    }
}
