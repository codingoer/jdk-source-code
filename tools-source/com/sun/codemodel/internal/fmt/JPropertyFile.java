package com.sun.codemodel.internal.fmt;

import com.sun.codemodel.internal.JResourceFile;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class JPropertyFile extends JResourceFile {
   private final Properties data = new Properties();

   public JPropertyFile(String name) {
      super(name);
   }

   public void add(String key, String value) {
      this.data.put(key, value);
   }

   public void build(OutputStream out) throws IOException {
      this.data.store(out, (String)null);
   }
}
