package com.sun.xml.internal.xsom;

import java.util.Collection;
import java.util.Iterator;

public interface XSAttContainer extends XSDeclaration {
   XSWildcard getAttributeWildcard();

   XSAttributeUse getAttributeUse(String var1, String var2);

   Iterator iterateAttributeUses();

   Collection getAttributeUses();

   XSAttributeUse getDeclaredAttributeUse(String var1, String var2);

   Iterator iterateDeclaredAttributeUses();

   Collection getDeclaredAttributeUses();

   Iterator iterateAttGroups();

   Collection getAttGroups();
}
