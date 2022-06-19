package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaHeapObject;
import java.util.Enumeration;

public class FinalizerObjectsQuery extends QueryHandler {
   public void run() {
      Enumeration var1 = this.snapshot.getFinalizerObjects();
      this.startHtml("Objects pending finalization");
      this.out.println("<a href='/finalizerSummary/'>Finalizer summary</a>");
      this.out.println("<h1>Objects pending finalization</h1>");

      while(var1.hasMoreElements()) {
         this.printThing((JavaHeapObject)var1.nextElement());
         this.out.println("<br>");
      }

      this.endHtml();
   }
}
