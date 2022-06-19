package com.sun.tools.internal.xjc.model;

public interface CPropertyVisitor {
   Object onElement(CElementPropertyInfo var1);

   Object onAttribute(CAttributePropertyInfo var1);

   Object onValue(CValuePropertyInfo var1);

   Object onReference(CReferencePropertyInfo var1);
}
