package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import java.util.Iterator;

class AllClassesQuery extends QueryHandler {
   boolean excludePlatform;
   boolean oqlSupported;

   public AllClassesQuery(boolean var1, boolean var2) {
      this.excludePlatform = var1;
      this.oqlSupported = var2;
   }

   public void run() {
      if (this.excludePlatform) {
         this.startHtml("All Classes (excluding platform)");
      } else {
         this.startHtml("All Classes (including platform)");
      }

      Iterator var1 = this.snapshot.getClasses();
      String var2 = null;

      while(true) {
         JavaClass var3;
         do {
            if (!var1.hasNext()) {
               this.out.println("<h2>Other Queries</h2>");
               this.out.println("<ul>");
               this.out.println("<li>");
               this.printAnchorStart();
               if (this.excludePlatform) {
                  this.out.print("allClassesWithPlatform/\">");
                  this.print("All classes including platform");
               } else {
                  this.out.print("\">");
                  this.print("All classes excluding platform");
               }

               this.out.println("</a>");
               this.out.println("<li>");
               this.printAnchorStart();
               this.out.print("showRoots/\">");
               this.print("Show all members of the rootset");
               this.out.println("</a>");
               this.out.println("<li>");
               this.printAnchorStart();
               this.out.print("showInstanceCounts/includePlatform/\">");
               this.print("Show instance counts for all classes (including platform)");
               this.out.println("</a>");
               this.out.println("<li>");
               this.printAnchorStart();
               this.out.print("showInstanceCounts/\">");
               this.print("Show instance counts for all classes (excluding platform)");
               this.out.println("</a>");
               this.out.println("<li>");
               this.printAnchorStart();
               this.out.print("histo/\">");
               this.print("Show heap histogram");
               this.out.println("</a>");
               this.out.println("<li>");
               this.printAnchorStart();
               this.out.print("finalizerSummary/\">");
               this.print("Show finalizer summary");
               this.out.println("</a>");
               if (this.oqlSupported) {
                  this.out.println("<li>");
                  this.printAnchorStart();
                  this.out.print("oql/\">");
                  this.print("Execute Object Query Language (OQL) query");
                  this.out.println("</a>");
               }

               this.out.println("</ul>");
               this.endHtml();
               return;
            }

            var3 = (JavaClass)var1.next();
         } while(this.excludePlatform && PlatformClasses.isPlatformClass(var3));

         String var4 = var3.getName();
         int var5 = var4.lastIndexOf(".");
         String var6;
         if (var4.startsWith("[")) {
            var6 = "<Arrays>";
         } else if (var5 == -1) {
            var6 = "<Default Package>";
         } else {
            var6 = var4.substring(0, var5);
         }

         if (!var6.equals(var2)) {
            this.out.print("<h2>Package ");
            this.print(var6);
            this.out.println("</h2>");
         }

         var2 = var6;
         this.printClass(var3);
         if (var3.getId() != -1L) {
            this.print(" [" + var3.getIdString() + "]");
         }

         this.out.println("<br>");
      }
   }
}
