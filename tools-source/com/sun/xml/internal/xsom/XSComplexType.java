package com.sun.xml.internal.xsom;

import java.util.List;

public interface XSComplexType extends XSType, XSAttContainer {
   boolean isAbstract();

   boolean isFinal(int var1);

   boolean isSubstitutionProhibited(int var1);

   XSElementDecl getScope();

   XSContentType getContentType();

   XSContentType getExplicitContent();

   boolean isMixed();

   XSComplexType getRedefinedBy();

   List getSubtypes();

   List getElementDecls();
}
