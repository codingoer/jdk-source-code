package org.relaxng.datatype;

public interface ValidationContext {
   String resolveNamespacePrefix(String var1);

   String getBaseUri();

   boolean isUnparsedEntity(String var1);

   boolean isNotation(String var1);
}
