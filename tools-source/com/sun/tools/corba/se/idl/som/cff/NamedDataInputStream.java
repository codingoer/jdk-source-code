package com.sun.tools.corba.se.idl.som.cff;

import java.io.DataInputStream;
import java.io.InputStream;

class NamedDataInputStream extends DataInputStream {
   public String fullyQualifiedFileName;
   public boolean inZipFile;

   protected NamedDataInputStream(InputStream var1, String var2, boolean var3) {
      super(var1);
      this.fullyQualifiedFileName = var2;
      this.inZipFile = var3;
   }
}
