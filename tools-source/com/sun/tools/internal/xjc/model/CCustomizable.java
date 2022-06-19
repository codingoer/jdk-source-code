package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.xsom.XSComponent;
import org.xml.sax.Locator;

public interface CCustomizable {
   CCustomizations getCustomizations();

   Locator getLocator();

   XSComponent getSchemaComponent();
}
