package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.Root;
import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;

class AllRootsQuery extends QueryHandler {
   public AllRootsQuery() {
   }

   public void run() {
      this.startHtml("All Members of the Rootset");
      Root[] var1 = this.snapshot.getRootsArray();
      ArraySorter.sort(var1, new Comparer() {
         public int compare(Object var1, Object var2) {
            Root var3 = (Root)var1;
            Root var4 = (Root)var2;
            int var5 = var3.getType() - var4.getType();
            return var5 != 0 ? -var5 : var3.getDescription().compareTo(var4.getDescription());
         }
      });
      int var2 = 0;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         Root var4 = var1[var3];
         if (var4.getType() != var2) {
            var2 = var4.getType();
            this.out.print("<h2>");
            this.print(var4.getTypeName() + " References");
            this.out.println("</h2>");
         }

         this.printRoot(var4);
         if (var4.getReferer() != null) {
            this.out.print("<small> (from ");
            this.printThingAnchorTag(var4.getReferer().getId());
            this.print(var4.getReferer().toString());
            this.out.print(")</a></small>");
         }

         this.out.print(" :<br>");
         JavaHeapObject var5 = this.snapshot.findThing(var4.getId());
         if (var5 != null) {
            this.print("--> ");
            this.printThing(var5);
            this.out.println("<br>");
         }
      }

      this.out.println("<h2>Other Queries</h2>");
      this.out.println("<ul>");
      this.out.println("<li>");
      this.printAnchorStart();
      this.out.print("\">");
      this.print("Show All Classes");
      this.out.println("</a>");
      this.out.println("</ul>");
      this.endHtml();
   }
}
