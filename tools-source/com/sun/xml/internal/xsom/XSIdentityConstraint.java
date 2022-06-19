package com.sun.xml.internal.xsom;

import java.util.List;

public interface XSIdentityConstraint extends XSComponent {
   short KEY = 0;
   short KEYREF = 1;
   short UNIQUE = 2;

   XSElementDecl getParent();

   String getName();

   String getTargetNamespace();

   short getCategory();

   XSXPath getSelector();

   List getFields();

   XSIdentityConstraint getReferencedKey();
}
