package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.JavaThing;
import com.sun.tools.hat.internal.model.ReachableObjects;

class ReachableQuery extends QueryHandler {
   public ReachableQuery() {
   }

   public void run() {
      this.startHtml("Objects Reachable From " + this.query);
      long var1 = this.parseHex(this.query);
      JavaHeapObject var3 = this.snapshot.findThing(var1);
      ReachableObjects var4 = new ReachableObjects(var3, this.snapshot.getReachableExcludes());
      long var5 = var4.getTotalSize();
      JavaThing[] var7 = var4.getReachables();
      long var8 = (long)var7.length;
      this.out.print("<strong>");
      this.printThing(var3);
      this.out.println("</strong><br>");
      this.out.println("<br>");

      for(int var10 = 0; var10 < var7.length; ++var10) {
         this.printThing(var7[var10]);
         this.out.println("<br>");
      }

      this.printFields(var4.getUsedFields(), "Data Members Followed");
      this.printFields(var4.getExcludedFields(), "Excluded Data Members");
      this.out.println("<h2>Total of " + var8 + " instances occupying " + var5 + " bytes.</h2>");
      this.endHtml();
   }

   private void printFields(String[] var1, String var2) {
      if (var1.length != 0) {
         this.out.print("<h3>");
         this.print(var2);
         this.out.println("</h3>");

         for(int var3 = 0; var3 < var1.length; ++var3) {
            this.print(var1[var3]);
            this.out.println("<br>");
         }

      }
   }
}
