package com.sun.xml.internal.xsom;

import org.relaxng.datatype.ValidationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public interface ForeignAttributes extends Attributes {
   ValidationContext getContext();

   Locator getLocator();
}
