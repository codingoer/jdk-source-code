package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;

public class FinalizerSummaryQuery extends QueryHandler {
   public void run() {
      Enumeration var1 = this.snapshot.getFinalizerObjects();
      this.startHtml("Finalizer Summary");
      this.out.println("<p align='center'>");
      this.out.println("<b><a href='/'>All Classes (excluding platform)</a></b>");
      this.out.println("</p>");
      this.printFinalizerSummary(var1);
      this.endHtml();
   }

   private void printFinalizerSummary(Enumeration var1) {
      int var2 = 0;
      HashMap var3 = new HashMap();

      while(var1.hasMoreElements()) {
         JavaHeapObject var4 = (JavaHeapObject)var1.nextElement();
         ++var2;
         JavaClass var5 = var4.getClazz();
         if (!var3.containsKey(var5)) {
            var3.put(var5, new HistogramElement(var5));
         }

         HistogramElement var6 = (HistogramElement)var3.get(var5);
         var6.updateCount();
      }

      this.out.println("<p align='center'>");
      this.out.println("<b>");
      this.out.println("Total ");
      if (var2 != 0) {
         this.out.print("<a href='/finalizerObjects/'>instances</a>");
      } else {
         this.out.print("instances");
      }

      this.out.println(" pending finalization: ");
      this.out.print(var2);
      this.out.println("</b></p><hr>");
      if (var2 != 0) {
         HistogramElement[] var7 = new HistogramElement[var3.size()];
         var3.values().toArray(var7);
         Arrays.sort(var7, new Comparator() {
            public int compare(HistogramElement var1, HistogramElement var2) {
               return var1.compare(var2);
            }
         });
         this.out.println("<table border=1 align=center>");
         this.out.println("<tr><th>Count</th><th>Class</th></tr>");

         for(int var8 = 0; var8 < var7.length; ++var8) {
            this.out.println("<tr><td>");
            this.out.println(var7[var8].getCount());
            this.out.println("</td><td>");
            this.printClass(var7[var8].getClazz());
            this.out.println("</td><tr>");
         }

         this.out.println("</table>");
      }
   }

   private static class HistogramElement {
      private JavaClass clazz;
      private long count;

      public HistogramElement(JavaClass var1) {
         this.clazz = var1;
      }

      public void updateCount() {
         ++this.count;
      }

      public int compare(HistogramElement var1) {
         long var2 = var1.count - this.count;
         return var2 == 0L ? 0 : (var2 > 0L ? 1 : -1);
      }

      public JavaClass getClazz() {
         return this.clazz;
      }

      public long getCount() {
         return this.count;
      }
   }
}
