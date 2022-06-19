package com.sun.tools.doclets.internal.toolkit;

import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class Content {
   public String toString() {
      StringWriter var1 = new StringWriter();

      try {
         this.write(var1, true);
      } catch (IOException var3) {
         throw new DocletAbortException(var3);
      }

      return var1.toString();
   }

   public abstract void addContent(Content var1);

   public abstract void addContent(String var1);

   public abstract boolean write(Writer var1, boolean var2) throws IOException;

   public abstract boolean isEmpty();

   public boolean isValid() {
      return !this.isEmpty();
   }

   public int charCount() {
      return 0;
   }

   protected static Object nullCheck(Object var0) {
      var0.getClass();
      return var0;
   }
}
