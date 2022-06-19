package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.Root;
import com.sun.tools.hat.internal.model.StackTrace;

class RootStackQuery extends QueryHandler {
   public RootStackQuery() {
   }

   public void run() {
      int var1 = (int)this.parseHex(this.query);
      Root var2 = this.snapshot.getRootAt(var1);
      if (var2 == null) {
         this.error("Root at " + var1 + " not found");
      } else {
         StackTrace var3 = var2.getStackTrace();
         if (var3 != null && var3.getFrames().length != 0) {
            this.startHtml("Stack Trace for " + var2.getDescription());
            this.out.println("<p>");
            this.printStackTrace(var3);
            this.out.println("</p>");
            this.endHtml();
         } else {
            this.error("No stack trace for " + var2.getDescription());
         }
      }
   }
}
