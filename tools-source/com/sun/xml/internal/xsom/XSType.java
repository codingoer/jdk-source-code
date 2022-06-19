package com.sun.xml.internal.xsom;

public interface XSType extends XSDeclaration {
   int EXTENSION = 1;
   int RESTRICTION = 2;
   int SUBSTITUTION = 4;

   XSType getBaseType();

   int getDerivationMethod();

   boolean isSimpleType();

   boolean isComplexType();

   XSType[] listSubstitutables();

   XSType getRedefinedBy();

   int getRedefinedCount();

   XSSimpleType asSimpleType();

   XSComplexType asComplexType();

   boolean isDerivedFrom(XSType var1);
}
