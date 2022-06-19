package com.sun.codemodel.internal.fmt;

import com.sun.codemodel.internal.JResourceFile;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class JSerializedObject extends JResourceFile {
   private final Object obj;

   public JSerializedObject(String name, Object obj) throws IOException {
      super(name);
      this.obj = obj;
   }

   protected void build(OutputStream os) throws IOException {
      ObjectOutputStream oos = new ObjectOutputStream(os);
      oos.writeObject(this.obj);
      oos.close();
   }
}
