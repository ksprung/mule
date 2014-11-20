/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ElementDescriptor
{

    private final String name;
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, ElementDescriptor> childs = new HashMap<>();

    public ElementDescriptor(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean hasAttribute(String attributeName)
    {
        return attributes.containsKey(attributeName);
    }

    public ElementDescriptor addAttribute(String attributeName, String value)
    {
        attributes.put(attributeName, value);
        return this;
    }

    public Collection<ElementDescriptor> getChilds()
    {
        return childs.values();
    }

    public ElementDescriptor getChildByName(String childName)
    {
        return childs.get(childName);
    }

    public ElementDescriptor addChild(ElementDescriptor child)
    {
        childs.put(child.getName(), child);
        return this;
    }
}
