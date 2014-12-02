/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.util.queue;

import org.mule.api.MuleRuntimeException;
import org.mule.util.FileUtils;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic queueing functionality with file storage.
 */
class RandomAccessFileQueueStore
{

    private final Log logger = LogFactory.getLog(this.getClass());
    protected static final int CONTROL_DATA_SIZE = 4; //just an int for the message size.
    private final QueueFileProvider queueFileProvider;
    private final QueueFileProvider queueControlFileProvider;
    private final Set<Long> entriesRemoved = new HashSet<>();

    private LinkedList<Long> orderedKeys = new LinkedList<Long>();
    private long fileTotalSpace = 0;

    public RandomAccessFileQueueStore(QueueFileProvider queueFileProvider, QueueFileProvider queueControlFileProvider)
    {
        this.queueFileProvider = queueFileProvider;
        this.queueControlFileProvider = queueControlFileProvider;
        initialise();
    }

    /**
     * @return the File where the content is stored.
     */
    public File getFile()
    {
        return this.queueFileProvider.getFile();
    }

    /**
     * Adds element at the end of the queue.
     * @param element element to add
     */
    public synchronized void addLast(byte[] element)
    {
        long filePointer = writeData(element);
        orderedKeys.addLast(filePointer);
    }

    /**
     * Remove and returns data from the queue.
     *
     * @return data from the beginning of the queue.
     * @throws InterruptedException
     */
    public synchronized byte[] removeFirst() throws InterruptedException
    {
        try
        {
            if (orderedKeys.isEmpty())
            {
                return null;
            }
            Long filePosition = orderedKeys.getFirst();
            queueFileProvider.getRandomAccessFile().seek(filePosition);
            byte[] data = readDataInCurrentPosition();
            logWriteRemoveInPosition(filePosition);
            queueControlFileProvider.getRandomAccessFile().writeLong(filePosition);
            entriesRemoved.add(filePosition);
            orderedKeys.removeFirst();
            return data;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void logWriteInPosition(Long filePosition)
    {
        logger.error("------ Writing in position " + queueFileProvider.getFile().getName() + " :" + filePosition);
    }

    private void logWriteRemoveInPosition(Long filePosition)
    {
        logger.error("------ Writing in position " + queueControlFileProvider.getFile().getName() + " :" + filePosition);
    }

    /**
     * Retrieves the first element from the queue without removing it.
     *
     * @return first element from the queue.
     * @throws InterruptedException
     */
    public synchronized byte[] getFirst() throws InterruptedException
    {
        return readFirstValue();
    }

    /**
     * Adds an element in the beginning of the queue.
     *
     * @param item element to add.
     * @throws InterruptedException
     */
    public synchronized void addFirst(byte[] item) throws InterruptedException
    {
        orderedKeys.addFirst(writeData(item));
    }

    /**
     * @return the size of the queue.
     */
    public int getSize()
    {
        return orderedKeys.size();
    }

    /**
     * removes all the elements from the queue.
     */
    public synchronized void clear()
    {
        try
        {
            logger.error("--- doing clear of file: " + queueFileProvider.getFile().getName());
            queueFileProvider.getRandomAccessFile().close();
            queueControlFileProvider.getRandomAccessFile().close();
            orderedKeys.clear();
            entriesRemoved.clear();
            fileTotalSpace = 0;
            queueFileProvider.recreate();
            queueControlFileProvider.recreate();
        }
        catch (IOException e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    /**
     * Adds a collection of elements at the end of the queue.
     * @param items collection of elements to add.
     * @return true if it were able to add them all, false otherwise.
     */
    public synchronized boolean addAll(Collection<? extends byte[]> items)
    {
        for (byte[] item : items)
        {
            addLast(item);
        }
        return true;
    }

    /**
     * Use this method carefully since it required bit amount of IO.
     *
     * @return all the elements from the queue.
     */
    public synchronized Collection<byte[]> allElements()
    {
        List<byte[]> elements = new LinkedList<byte[]>();
        try
        {
            queueFileProvider.getRandomAccessFile().seek(0);
            while (true)
            {
                long filePointer = queueFileProvider.getRandomAccessFile().getFilePointer();
                if (!entriesRemoved.contains(filePointer))
                {
                    elements.add(readDataInCurrentPosition());
                }
                else
                {
                    moveFilePointerToNextData();
                }
            }
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
        }
        return elements;
    }

    /**
     * @return true if there's no elements in the queue, false otherwise
     */
    public boolean isEmpty()
    {
        return orderedKeys.isEmpty();
    }

    /**
     * Removes data from the queue according to a {@link RawDataSelector}
     * instance that determines if a certain element must be removed.
     *
     * @param rawDataSelector to determine if the element must be removed.
     * @return true if an element was removed
     */
    public synchronized boolean remove(RawDataSelector rawDataSelector)
    {
        try
        {
            queueFileProvider.getRandomAccessFile().seek(0);
            while (true)
            {
                long currentPosition = queueFileProvider.getRandomAccessFile().getFilePointer();
                if (!entriesRemoved.contains(currentPosition))
                {
                    byte[] data = readDataInCurrentPosition();
                    if (rawDataSelector.isSelectedData(data))
                    {
                        logWriteRemoveInPosition(currentPosition);
                        queueControlFileProvider.getRandomAccessFile().writeLong(currentPosition);
                        entriesRemoved.add(currentPosition);
                        orderedKeys.remove(currentPosition);
                        return true;
                    }
                }
            }
        }
        catch (EOFException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
            return false;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Free all resources held for the queue.
     *
     * Do not removes elements from the queue.
     */
    public synchronized void close()
    {
        queueFileProvider.close();
        queueControlFileProvider.close();
    }

    private byte[] readDataInCurrentPosition() throws IOException
    {
        int serializedValueSize = queueFileProvider.getRandomAccessFile().readInt();
        byte[] data = new byte[serializedValueSize];
        queueFileProvider.getRandomAccessFile().read(data, 0, serializedValueSize);
        return data;
    }

    private long writeData(byte[] data)
    {
        try
        {
            if (getSize() > 0)
            {
                queueFileProvider.getRandomAccessFile().seek(fileTotalSpace);
            }
            long filePointer = queueFileProvider.getRandomAccessFile().getFilePointer();
            int totalBytesRequired = CONTROL_DATA_SIZE + data.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(totalBytesRequired);
            byteBuffer.putInt(data.length);
            byteBuffer.put(data);
            logWriteInPosition(filePointer);
            queueFileProvider.getRandomAccessFile().write(byteBuffer.array());
            fileTotalSpace += totalBytesRequired;
            return filePointer;
        }
        catch (IOException e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    private void initialise()
    {
        loadEntriesRemoved();
        loadDataFileEntries();
    }

    private void loadDataFileEntries()
    {
        try
        {
            if (queueFileProvider.getRandomAccessFile().length() == 0)
            {
                return;
            }
            queueFileProvider.getRandomAccessFile().seek(0);
            while (true)
            {
                long position = queueFileProvider.getRandomAccessFile().getFilePointer();
                if (!entriesRemoved.contains(position))
                {
                    byte[] value = readDataInCurrentPosition();
                    SerializationUtils.deserialize(value);
                    //only add the key if it was possible to read the data, if not it's a corrupted entry.
                    orderedKeys.add(position);
                }
                else
                {
                    readDataInCurrentPosition();
                }
            }
        }
        catch (SerializationException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
        }
        catch (NegativeArraySizeException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
        }
        catch (EOFException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage());
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
            throw new MuleRuntimeException(e);
        }
    }

    private void loadEntriesRemoved()
    {
        try
        {
            if (queueControlFileProvider.getRandomAccessFile().length() == 0)
            {
                return;
            }
            while (true)
            {
                entriesRemoved.add(queueControlFileProvider.getRandomAccessFile().readLong());
            }
        }
        catch (EOFException e)
        {
            logger.debug("EOF file reached, no more entries to read");
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    private byte[] readFirstValue()
    {
        try
        {
            if (orderedKeys.isEmpty())
            {
                return null;
            }
            Long filePointer = orderedKeys.getFirst();
            queueFileProvider.getRandomAccessFile().seek(filePointer);
            return readDataInCurrentPosition();
        }
        catch (IOException e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    private void moveFilePointerToNextData() throws IOException
    {
        int serializedValueSize = queueFileProvider.getRandomAccessFile().readInt();
        queueFileProvider.getRandomAccessFile().seek(queueFileProvider.getRandomAccessFile().getFilePointer() + serializedValueSize);
    }

    /**
     * @return the length of the file in bytes.
     */
    public long getLength()
    {
        return fileTotalSpace;
    }

    /**
     * Searches for data within the queue store using a {@link RawDataSelector}
     *
     * @param rawDataSelector to determine if the element is the one we are looking for
     * @return true if an element exists within the queue, false otherwise
     */
    public synchronized boolean contains(RawDataSelector rawDataSelector)
    {
        try
        {
            queueFileProvider.getRandomAccessFile().seek(0);
            while (true)
            {
                if (!entriesRemoved.contains(queueFileProvider.getRandomAccessFile().getFilePointer()))
                {
                    byte[] data = readDataInCurrentPosition();
                    if (rawDataSelector.isSelectedData(data))
                    {
                        return true;
                    }
                }
                else
                {
                    moveFilePointerToNextData();
                }
            }
        }
        catch (SerializationException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
            return false;
        }
        catch (EOFException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(e);
            }
            return false;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
