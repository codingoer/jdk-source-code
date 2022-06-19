package com.sun.codemodel.internal.fmt;

import com.sun.codemodel.internal.JResourceFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class JBinaryFile extends JResourceFile {
   private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

   public JBinaryFile(String name) {
      super(name);
   }

   public OutputStream getDataStore() {
      return this.baos;
   }

   public void build(OutputStream os) throws IOException {
      os.write(this.baos.toByteArray());
   }
}
