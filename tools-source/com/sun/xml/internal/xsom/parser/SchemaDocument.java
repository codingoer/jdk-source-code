package com.sun.xml.internal.xsom.parser;

import com.sun.xml.internal.xsom.XSSchema;
import java.util.Set;

public interface SchemaDocument {
   String getSystemId();

   String getTargetNamespace();

   XSSchema getSchema();

   Set getReferencedDocuments();

   Set getIncludedDocuments();

   Set getImportedDocuments(String var1);

   boolean includes(SchemaDocument var1);

   boolean imports(SchemaDocument var1);

   Set getReferers();
}
