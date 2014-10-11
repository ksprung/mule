/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.tcp.protocols;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.serialization.ObjectSerializer;
import org.mule.transformer.wire.SerializedMuleMessageWireFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This Protocol will send the actual Mule Message over the TCP channel, and in this
 * way we are preserving any headers which might be needed, for example Correlation
 * IDs in order to be able to aggregate messages after chunking.  Data are read until
 * no more are (momentarily) available.
 */
public class MuleMessageDirectProtocol extends DirectProtocol implements MuleContextAware
{

    private final SerializedMuleMessageWireFormat wireFormat = new SerializedMuleMessageWireFormat();
    private final MuleMessageWorker messageWorker = new MuleMessageWorker(wireFormat);

    public MuleMessageDirectProtocol(ObjectSerializer serializer)
    {
        super(serializer);
    }

    public MuleMessageDirectProtocol(ObjectSerializer serializer, boolean streamOk, int bufferSize)
    {
        super(serializer, streamOk, bufferSize);
    }

    @Override
    public Object read(InputStream is) throws IOException
    {
        return messageWorker.doRead(super.read(is));
    }

    @Override
    public void write(OutputStream os, Object data) throws IOException
    {
        super.write(os, messageWorker.doWrite());
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        wireFormat.setMuleContext(context);
    }
}
