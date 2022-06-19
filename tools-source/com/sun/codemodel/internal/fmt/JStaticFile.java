package com.sun.codemodel.internal.fmt;

import com.sun.codemodel.internal.JResourceFile;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class JStaticFile extends JResourceFile {
   private final ClassLoader classLoader;
   private final String resourceName;
   private final boolean isResource;

   public JStaticFile(String _resourceName) {
      this(_resourceName, !_resourceName.endsWith(".java"));
   }

   public JStaticFile(String _resourceName, boolean isResource) {
      this(SecureLoader.getClassClassLoader(JStaticFile.class), _resourceName, isResource);
   }

   public JStaticFile(ClassLoader _classLoader, String _resourceName, boolean isResource) {
      super(_resourceName.substring(_resourceName.lastIndexOf(47) + 1));
      this.classLoader = _classLoader;
      this.resourceName = _resourceName;
      this.isResource = isResource;
   }

   protected boolean isResource() {
      return this.isResource;
   }

   protected void build(OutputStream os) throws IOException {
      DataInputStream dis = new DataInputStream(this.classLoader.getResourceAsStream(this.resourceName));
      byte[] buf = new byte[256];

      int sz;
      while((sz = dis.read(buf)) > 0) {
         os.write(buf, 0, sz);
      }

      dis.close();
   }
}
