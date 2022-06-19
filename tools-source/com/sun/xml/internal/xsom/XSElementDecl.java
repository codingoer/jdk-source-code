package com.sun.xml.internal.xsom;

import java.util.List;
import java.util.Set;

public interface XSElementDecl extends XSDeclaration, XSTerm {
   XSType getType();

   boolean isNillable();

   XSElementDecl getSubstAffiliation();

   List getIdentityConstraints();

   boolean isSubstitutionExcluded(int var1);

   boolean isSubstitutionDisallowed(int var1);

   boolean isAbstract();

   /** @deprecated */
   XSElementDecl[] listSubstitutables();

   Set getSubstitutables();

   boolean canBeSubstitutedBy(XSElementDecl var1);

   XmlString getDefaultValue();

   XmlString getFixedValue();

   Boolean getForm();
}
