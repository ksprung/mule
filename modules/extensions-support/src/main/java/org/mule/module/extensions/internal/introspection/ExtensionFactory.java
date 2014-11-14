package org.mule.module.extensions.internal.introspection;

import org.mule.extensions.introspection.Extension;
import org.mule.extensions.introspection.declaration.Construct;

public interface ExtensionFactory
{

    Extension createFrom(Construct construct);
}
