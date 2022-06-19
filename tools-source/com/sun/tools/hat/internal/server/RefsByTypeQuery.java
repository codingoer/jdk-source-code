package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.AbstractJavaHeapObjectVisitor;
import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RefsByTypeQuery extends QueryHandler {
   public void run() {
      JavaClass var1 = this.snapshot.findClass(this.query);
      if (var1 == null) {
         this.error("class not found: " + this.query);
      } else {
         HashMap var2 = new HashMap();
         final HashMap var3 = new HashMap();
         Enumeration var4 = var1.getInstances(false);

         while(true) {
            JavaHeapObject var5;
            do {
               if (!var4.hasMoreElements()) {
                  this.startHtml("References by Type");
                  this.out.println("<p align='center'>");
                  this.printClass(var1);
                  if (var1.getId() != -1L) {
                     this.println("[" + var1.getIdString() + "]");
                  }

                  this.out.println("</p>");
                  if (var2.size() != 0) {
                     this.out.println("<h3 align='center'>Referrers by Type</h3>");
                     this.print(var2);
                  }

                  if (var3.size() != 0) {
                     this.out.println("<h3 align='center'>Referees by Type</h3>");
                     this.print(var3);
                  }

                  this.endHtml();
                  return;
               }

               var5 = (JavaHeapObject)var4.nextElement();
            } while(var5.getId() == -1L);

            Enumeration var6 = var5.getReferers();

            while(var6.hasMoreElements()) {
               JavaHeapObject var7 = (JavaHeapObject)var6.nextElement();
               JavaClass var8 = var7.getClazz();
               if (var8 == null) {
                  System.out.println("null class for " + var7);
               } else {
                  Long var9 = (Long)var2.get(var8);
                  if (var9 == null) {
                     var9 = new Long(1L);
                  } else {
                     var9 = new Long(var9 + 1L);
                  }

                  var2.put(var8, var9);
               }
            }

            var5.visitReferencedObjects(new AbstractJavaHeapObjectVisitor() {
               public void visit(JavaHeapObject var1) {
                  JavaClass var2 = var1.getClazz();
                  Long var3x = (Long)var3.get(var2);
                  if (var3x == null) {
                     var3x = new Long(1L);
                  } else {
                     var3x = new Long(var3x + 1L);
                  }

                  var3.put(var2, var3x);
               }
            });
         }
      }
   }

   private void print(final Map var1) {
      this.out.println("<table border='1' align='center'>");
      Set var2 = var1.keySet();
      JavaClass[] var3 = new JavaClass[var2.size()];
      var2.toArray(var3);
      Arrays.sort(var3, new Comparator() {
         public int compare(JavaClass var1x, JavaClass var2) {
            Long var3 = (Long)var1.get(var1x);
            Long var4 = (Long)var1.get(var2);
            return var4.compareTo(var3);
         }
      });
      this.out.println("<tr><th>Class</th><th>Count</th></tr>");

      for(int var4 = 0; var4 < var3.length; ++var4) {
         JavaClass var5 = var3[var4];
         this.out.println("<tr><td>");
         this.out.print("<a href='/refsByType/");
         this.print(var5.getIdString());
         this.out.print("'>");
         this.print(var5.getName());
         this.out.println("</a>");
         this.out.println("</td><td>");
         this.out.println(var1.get(var5));
         this.out.println("</td></tr>");
      }

      this.out.println("</table>");
   }
}
