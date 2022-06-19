package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.ReferenceChain;
import com.sun.tools.hat.internal.model.Root;
import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;

class RootsQuery extends QueryHandler {
   private boolean includeWeak;

   public RootsQuery(boolean var1) {
      this.includeWeak = var1;
   }

   public void run() {
      long var1 = this.parseHex(this.query);
      JavaHeapObject var3 = this.snapshot.findThing(var1);
      if (var3 == null) {
         this.startHtml("Object not found for rootset");
         this.error("object not found");
         this.endHtml();
      } else {
         if (this.includeWeak) {
            this.startHtml("Rootset references to " + var3 + " (includes weak refs)");
         } else {
            this.startHtml("Rootset references to " + var3 + " (excludes weak refs)");
         }

         this.out.flush();
         ReferenceChain[] var4 = this.snapshot.rootsetReferencesTo(var3, this.includeWeak);
         ArraySorter.sort(var4, new Comparer() {
            public int compare(Object var1, Object var2) {
               ReferenceChain var3 = (ReferenceChain)var1;
               ReferenceChain var4 = (ReferenceChain)var2;
               Root var5 = var3.getObj().getRoot();
               Root var6 = var4.getObj().getRoot();
               int var7 = var5.getType() - var6.getType();
               return var7 != 0 ? -var7 : var3.getDepth() - var4.getDepth();
            }
         });
         this.out.print("<h1>References to ");
         this.printThing(var3);
         this.out.println("</h1>");
         int var5 = 0;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            ReferenceChain var7 = var4[var6];
            Root var8 = var7.getObj().getRoot();
            if (var8.getType() != var5) {
               var5 = var8.getType();
               this.out.print("<h2>");
               this.print(var8.getTypeName() + " References");
               this.out.println("</h2>");
            }

            this.out.print("<h3>");
            this.printRoot(var8);
            if (var8.getReferer() != null) {
               this.out.print("<small> (from ");
               this.printThingAnchorTag(var8.getReferer().getId());
               this.print(var8.getReferer().toString());
               this.out.print(")</a></small>");
            }

            this.out.print(" :</h3>");

            while(var7 != null) {
               ReferenceChain var9 = var7.getNext();
               JavaHeapObject var10 = var7.getObj();
               this.print("--> ");
               this.printThing(var10);
               if (var9 != null) {
                  this.print(" (" + var10.describeReferenceTo(var9.getObj(), this.snapshot) + ":)");
               }

               this.out.println("<br>");
               var7 = var9;
            }
         }

         this.out.println("<h2>Other queries</h2>");
         if (this.includeWeak) {
            this.printAnchorStart();
            this.out.print("roots/");
            this.printHex(var1);
            this.out.print("\">");
            this.out.println("Exclude weak refs</a><br>");
            this.endHtml();
         }

         if (!this.includeWeak) {
            this.printAnchorStart();
            this.out.print("allRoots/");
            this.printHex(var1);
            this.out.print("\">");
            this.out.println("Include weak refs</a><br>");
         }

      }
   }
}
