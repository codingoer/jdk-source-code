package com.sun.codemodel.internal;

import java.io.IOException;
import java.io.OutputStream;

public abstract class JResourceFile {
   private final String name;

   protected JResourceFile(String name) {
      this.name = name;
   }

   public String name() {
      return this.name;
   }

   protected boolean isResource() {
      return true;
   }

   protected abstract void build(OutputStream var1) throws IOException;
}
