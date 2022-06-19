package com.sun.codemodel.internal.fmt;

import com.sun.codemodel.internal.JResourceFile;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class JTextFile extends JResourceFile {
   private String contents = null;

   public JTextFile(String name) {
      super(name);
   }

   public void setContents(String _contents) {
      this.contents = _contents;
   }

   public void build(OutputStream out) throws IOException {
      Writer w = new OutputStreamWriter(out);
      w.write(this.contents);
      w.close();
   }
}
