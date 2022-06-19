package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.parser.SchemaDocument;
import java.util.Iterator;
import java.util.Map;

public interface XSSchema extends XSComponent {
   String getTargetNamespace();

   Map getAttributeDecls();

   Iterator iterateAttributeDecls();

   XSAttributeDecl getAttributeDecl(String var1);

   Map getElementDecls();

   Iterator iterateElementDecls();

   XSElementDecl getElementDecl(String var1);

   Map getAttGroupDecls();

   Iterator iterateAttGroupDecls();

   XSAttGroupDecl getAttGroupDecl(String var1);

   Map getModelGroupDecls();

   Iterator iterateModelGroupDecls();

   XSModelGroupDecl getModelGroupDecl(String var1);

   Map getTypes();

   Iterator iterateTypes();

   XSType getType(String var1);

   Map getSimpleTypes();

   Iterator iterateSimpleTypes();

   XSSimpleType getSimpleType(String var1);

   Map getComplexTypes();

   Iterator iterateComplexTypes();

   XSComplexType getComplexType(String var1);

   Map getNotations();

   Iterator iterateNotations();

   XSNotation getNotation(String var1);

   Map getIdentityConstraints();

   XSIdentityConstraint getIdentityConstraint(String var1);

   /** @deprecated */
   SchemaDocument getSourceDocument();

   XSSchemaSet getRoot();
}
