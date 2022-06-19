package com.sun.tools.internal.xjc;

import com.sun.tools.internal.xjc.api.ErrorListener;
import com.sun.tools.internal.xjc.outline.Outline;

public abstract class XJCListener implements ErrorListener {
   /** @deprecated */
   public void generatedFile(String fileName) {
   }

   public void generatedFile(String fileName, int current, int total) {
      this.generatedFile(fileName);
   }

   public void message(String msg) {
   }

   public void compiled(Outline outline) {
   }

   public boolean isCanceled() {
      return false;
   }
}
