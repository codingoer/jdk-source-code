package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import java.util.Arrays;
import java.util.Comparator;

public class HistogramQuery extends QueryHandler {
   public void run() {
      JavaClass[] var1 = this.snapshot.getClassesArray();
      Comparator var2;
      if (this.query.equals("count")) {
         var2 = new Comparator() {
            public int compare(JavaClass var1, JavaClass var2) {
               long var3 = (long)(var2.getInstancesCount(false) - var1.getInstancesCount(false));
               return var3 == 0L ? 0 : (var3 < 0L ? -1 : 1);
            }
         };
      } else if (this.query.equals("class")) {
         var2 = new Comparator() {
            public int compare(JavaClass var1, JavaClass var2) {
               return var1.getName().compareTo(var2.getName());
            }
         };
      } else {
         var2 = new Comparator() {
            public int compare(JavaClass var1, JavaClass var2) {
               long var3 = var2.getTotalInstanceSize() - var1.getTotalInstanceSize();
               return var3 == 0L ? 0 : (var3 < 0L ? -1 : 1);
            }
         };
      }

      Arrays.sort(var1, var2);
      this.startHtml("Heap Histogram");
      this.out.println("<p align='center'>");
      this.out.println("<b><a href='/'>All Classes (excluding platform)</a></b>");
      this.out.println("</p>");
      this.out.println("<table align=center border=1>");
      this.out.println("<tr><th><a href='/histo/class'>Class</a></th>");
      this.out.println("<th><a href='/histo/count'>Instance Count</a></th>");
      this.out.println("<th><a href='/histo/size'>Total Size</a></th></tr>");

      for(int var3 = 0; var3 < var1.length; ++var3) {
         JavaClass var4 = var1[var3];
         this.out.println("<tr><td>");
         this.printClass(var4);
         this.out.println("</td>");
         this.out.println("<td>");
         this.out.println(var4.getInstancesCount(false));
         this.out.println("</td>");
         this.out.println("<td>");
         this.out.println(var4.getTotalInstanceSize());
         this.out.println("</td></tr>");
      }

      this.out.println("</table>");
      this.endHtml();
   }
}
