package com.sun.xml.internal.xsom;

import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public interface XSSchemaSet {
   XSSchema getSchema(String var1);

   XSSchema getSchema(int var1);

   int getSchemaSize();

   Iterator iterateSchema();

   Collection getSchemas();

   XSType getType(String var1, String var2);

   XSSimpleType getSimpleType(String var1, String var2);

   XSAttributeDecl getAttributeDecl(String var1, String var2);

   XSElementDecl getElementDecl(String var1, String var2);

   XSModelGroupDecl getModelGroupDecl(String var1, String var2);

   XSAttGroupDecl getAttGroupDecl(String var1, String var2);

   XSComplexType getComplexType(String var1, String var2);

   XSIdentityConstraint getIdentityConstraint(String var1, String var2);

   Iterator iterateElementDecls();

   Iterator iterateTypes();

   Iterator iterateAttributeDecls();

   Iterator iterateAttGroupDecls();

   Iterator iterateModelGroupDecls();

   Iterator iterateSimpleTypes();

   Iterator iterateComplexTypes();

   Iterator iterateNotations();

   Iterator iterateIdentityConstraints();

   XSComplexType getAnyType();

   XSSimpleType getAnySimpleType();

   XSContentType getEmpty();

   Collection select(String var1, NamespaceContext var2);

   XSComponent selectSingle(String var1, NamespaceContext var2);
}
