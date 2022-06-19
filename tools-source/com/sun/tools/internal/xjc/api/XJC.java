package com.sun.tools.internal.xjc.api;

import com.sun.tools.internal.xjc.api.impl.s2j.SchemaCompilerImpl;
import com.sun.xml.internal.bind.api.impl.NameConverter;

public final class XJC {
   public static SchemaCompiler createSchemaCompiler() {
      return new SchemaCompilerImpl();
   }

   public static String getDefaultPackageName(String namespaceUri) {
      if (namespaceUri == null) {
         throw new IllegalArgumentException();
      } else {
         return NameConverter.standard.toPackageName(namespaceUri);
      }
   }
}
