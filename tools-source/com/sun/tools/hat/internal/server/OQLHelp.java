package com.sun.tools.hat.internal.server;

import java.io.BufferedInputStream;
import java.io.InputStream;

class OQLHelp extends QueryHandler {
   public OQLHelp() {
   }

   public void run() {
      InputStream var1 = this.getClass().getResourceAsStream("/com/sun/tools/hat/resources/oqlhelp.html");
      boolean var2 = true;

      try {
         BufferedInputStream var5 = new BufferedInputStream(var1);

         int var6;
         while((var6 = var5.read()) != -1) {
            this.out.print((char)var6);
         }
      } catch (Exception var4) {
         this.printException(var4);
      }

   }
}
