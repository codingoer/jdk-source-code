package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import java.util.Collection;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public interface BIDeclaration {
   void setParent(BindInfo var1);

   QName getName();

   Locator getLocation();

   void markAsAcknowledged();

   boolean isAcknowledged();

   void onSetOwner();

   Collection getChildren();
}
