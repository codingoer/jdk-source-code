package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import java.util.Enumeration;

class InstancesQuery extends QueryHandler {
   private boolean includeSubclasses;
   private boolean newObjects;

   public InstancesQuery(boolean var1) {
      this.includeSubclasses = var1;
   }

   public InstancesQuery(boolean var1, boolean var2) {
      this.includeSubclasses = var1;
      this.newObjects = var2;
   }

   public void run() {
      JavaClass var1 = this.snapshot.findClass(this.query);
      String var2;
      if (this.newObjects) {
         var2 = "New instances of ";
      } else {
         var2 = "Instances of ";
      }

      if (this.includeSubclasses) {
         this.startHtml(var2 + this.query + " (including subclasses)");
      } else {
         this.startHtml(var2 + this.query);
      }

      if (var1 == null) {
         this.error("Class not found");
      } else {
         this.out.print("<strong>");
         this.printClass(var1);
         this.out.print("</strong><br><br>");
         Enumeration var3 = var1.getInstances(this.includeSubclasses);
         long var4 = 0L;
         long var6 = 0L;

         label32:
         while(true) {
            JavaHeapObject var8;
            do {
               if (!var3.hasMoreElements()) {
                  this.out.println("<h2>Total of " + var6 + " instances occupying " + var4 + " bytes.</h2>");
                  break label32;
               }

               var8 = (JavaHeapObject)var3.nextElement();
            } while(this.newObjects && !var8.isNew());

            this.printThing(var8);
            this.out.println("<br>");
            var4 += (long)var8.getSize();
            ++var6;
         }
      }

      this.endHtml();
   }
}
