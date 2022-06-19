package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaField;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.JavaStatic;
import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;
import java.util.Enumeration;

class ClassQuery extends QueryHandler {
   public ClassQuery() {
   }

   public void run() {
      this.startHtml("Class " + this.query);
      JavaClass var1 = this.snapshot.findClass(this.query);
      if (var1 == null) {
         this.error("class not found: " + this.query);
      } else {
         this.printFullClass(var1);
      }

      this.endHtml();
   }

   protected void printFullClass(JavaClass var1) {
      this.out.print("<h1>");
      this.print(var1.toString());
      this.out.println("</h1>");
      this.out.println("<h2>Superclass:</h2>");
      this.printClass(var1.getSuperclass());
      this.out.println("<h2>Loader Details</h2>");
      this.out.println("<h3>ClassLoader:</h3>");
      this.printThing(var1.getLoader());
      this.out.println("<h3>Signers:</h3>");
      this.printThing(var1.getSigners());
      this.out.println("<h3>Protection Domain:</h3>");
      this.printThing(var1.getProtectionDomain());
      this.out.println("<h2>Subclasses:</h2>");
      JavaClass[] var2 = var1.getSubclasses();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.out.print("    ");
         this.printClass(var2[var3]);
         this.out.println("<br>");
      }

      this.out.println("<h2>Instance Data Members:</h2>");
      JavaField[] var6 = (JavaField[])var1.getFields().clone();
      ArraySorter.sort(var6, new Comparer() {
         public int compare(Object var1, Object var2) {
            JavaField var3 = (JavaField)var1;
            JavaField var4 = (JavaField)var2;
            return var3.getName().compareTo(var4.getName());
         }
      });

      for(int var4 = 0; var4 < var6.length; ++var4) {
         this.out.print("    ");
         this.printField(var6[var4]);
         this.out.println("<br>");
      }

      this.out.println("<h2>Static Data Members:</h2>");
      JavaStatic[] var7 = var1.getStatics();

      for(int var5 = 0; var5 < var7.length; ++var5) {
         this.printStatic(var7[var5]);
         this.out.println("<br>");
      }

      this.out.println("<h2>Instances</h2>");
      this.printAnchorStart();
      this.print("instances/" + this.encodeForURL(var1));
      this.out.print("\">");
      this.out.println("Exclude subclasses</a><br>");
      this.printAnchorStart();
      this.print("allInstances/" + this.encodeForURL(var1));
      this.out.print("\">");
      this.out.println("Include subclasses</a><br>");
      if (this.snapshot.getHasNewSet()) {
         this.out.println("<h2>New Instances</h2>");
         this.printAnchorStart();
         this.print("newInstances/" + this.encodeForURL(var1));
         this.out.print("\">");
         this.out.println("Exclude subclasses</a><br>");
         this.printAnchorStart();
         this.print("allNewInstances/" + this.encodeForURL(var1));
         this.out.print("\">");
         this.out.println("Include subclasses</a><br>");
      }

      this.out.println("<h2>References summary by Type</h2>");
      this.printAnchorStart();
      this.print("refsByType/" + this.encodeForURL(var1));
      this.out.print("\">");
      this.out.println("References summary by type</a>");
      this.printReferencesTo(var1);
   }

   protected void printReferencesTo(JavaHeapObject var1) {
      if (var1.getId() != -1L) {
         this.out.println("<h2>References to this object:</h2>");
         this.out.flush();
         Enumeration var2 = var1.getReferers();

         while(var2.hasMoreElements()) {
            JavaHeapObject var3 = (JavaHeapObject)var2.nextElement();
            this.printThing(var3);
            this.print(" : " + var3.describeReferenceTo(var1, this.snapshot));
            this.out.println("<br>");
         }

         this.out.println("<h2>Other Queries</h2>");
         this.out.println("Reference Chains from Rootset");
         long var5 = var1.getId();
         this.out.print("<ul><li>");
         this.printAnchorStart();
         this.out.print("roots/");
         this.printHex(var5);
         this.out.print("\">");
         this.out.println("Exclude weak refs</a>");
         this.out.print("<li>");
         this.printAnchorStart();
         this.out.print("allRoots/");
         this.printHex(var5);
         this.out.print("\">");
         this.out.println("Include weak refs</a></ul>");
         this.printAnchorStart();
         this.out.print("reachableFrom/");
         this.printHex(var5);
         this.out.print("\">");
         this.out.println("Objects reachable from here</a><br>");
      }
   }
}
