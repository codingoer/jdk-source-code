package com.sun.xml.internal.xsom;

import org.relaxng.datatype.ValidationContext;

public final class XmlString {
   public final String value;
   public final ValidationContext context;
   private static final ValidationContext NULL_CONTEXT = new ValidationContext() {
      public String resolveNamespacePrefix(String s) {
         if (s.length() == 0) {
            return "";
         } else {
            return s.equals("xml") ? "http://www.w3.org/XML/1998/namespace" : null;
         }
      }

      public String getBaseUri() {
         return null;
      }

      public boolean isUnparsedEntity(String s) {
         return false;
      }

      public boolean isNotation(String s) {
         return false;
      }
   };

   public XmlString(String value, ValidationContext context) {
      this.value = value;
      this.context = context;
      if (context == null) {
         throw new IllegalArgumentException();
      }
   }

   public XmlString(String value) {
      this(value, NULL_CONTEXT);
   }

   public final String resolvePrefix(String prefix) {
      return this.context.resolveNamespacePrefix(prefix);
   }

   public String toString() {
      return this.value;
   }
}
