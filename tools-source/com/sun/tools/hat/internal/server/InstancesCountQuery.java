package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;
import java.util.Enumeration;

class InstancesCountQuery extends QueryHandler {
   private boolean excludePlatform;

   public InstancesCountQuery(boolean var1) {
      this.excludePlatform = var1;
   }

   public void run() {
      if (this.excludePlatform) {
         this.startHtml("Instance Counts for All Classes (excluding platform)");
      } else {
         this.startHtml("Instance Counts for All Classes (including platform)");
      }

      JavaClass[] var1 = this.snapshot.getClassesArray();
      if (this.excludePlatform) {
         int var2 = 0;

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (!PlatformClasses.isPlatformClass(var1[var3])) {
               var1[var2++] = var1[var3];
            }
         }

         JavaClass[] var14 = new JavaClass[var2];
         System.arraycopy(var1, 0, var14, 0, var14.length);
         var1 = var14;
      }

      ArraySorter.sort(var1, new Comparer() {
         public int compare(Object var1, Object var2) {
            JavaClass var3 = (JavaClass)var1;
            JavaClass var4 = (JavaClass)var2;
            int var5 = var3.getInstancesCount(false) - var4.getInstancesCount(false);
            if (var5 != 0) {
               return -var5;
            } else {
               String var6 = var3.getName();
               String var7 = var4.getName();
               if (var6.startsWith("[") != var7.startsWith("[")) {
                  return var6.startsWith("[") ? 1 : -1;
               } else {
                  return var6.compareTo(var7);
               }
            }
         }
      });
      Object var13 = null;
      long var15 = 0L;
      long var5 = 0L;

      for(int var7 = 0; var7 < var1.length; ++var7) {
         JavaClass var8 = var1[var7];
         int var9 = var8.getInstancesCount(false);
         this.print("" + var9);
         this.printAnchorStart();
         this.print("instances/" + this.encodeForURL(var1[var7]));
         this.out.print("\"> ");
         if (var9 == 1) {
            this.print("instance");
         } else {
            this.print("instances");
         }

         this.out.print("</a> ");
         if (this.snapshot.getHasNewSet()) {
            Enumeration var10 = var8.getInstances(false);
            int var11 = 0;

            while(var10.hasMoreElements()) {
               JavaHeapObject var12 = (JavaHeapObject)var10.nextElement();
               if (var12.isNew()) {
                  ++var11;
               }
            }

            this.print("(");
            this.printAnchorStart();
            this.print("newInstances/" + this.encodeForURL(var1[var7]));
            this.out.print("\">");
            this.print("" + var11 + " new");
            this.out.print("</a>) ");
         }

         this.print("of ");
         this.printClass(var1[var7]);
         this.out.println("<br>");
         var5 += (long)var9;
         var15 += var1[var7].getTotalInstanceSize();
      }

      this.out.println("<h2>Total of " + var5 + " instances occupying " + var15 + " bytes.</h2>");
      this.out.println("<h2>Other Queries</h2>");
      this.out.println("<ul>");
      this.out.print("<li>");
      this.printAnchorStart();
      if (!this.excludePlatform) {
         this.out.print("showInstanceCounts/\">");
         this.print("Show instance counts for all classes (excluding platform)");
      } else {
         this.out.print("showInstanceCounts/includePlatform/\">");
         this.print("Show instance counts for all classes (including platform)");
      }

      this.out.println("</a>");
      this.out.print("<li>");
      this.printAnchorStart();
      this.out.print("allClassesWithPlatform/\">");
      this.print("Show All Classes (including platform)");
      this.out.println("</a>");
      this.out.print("<li>");
      this.printAnchorStart();
      this.out.print("\">");
      this.print("Show All Classes (excluding platform)");
      this.out.println("</a>");
      this.out.println("</ul>");
      this.endHtml();
   }
}
