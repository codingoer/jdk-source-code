package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaField;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.JavaObject;
import com.sun.tools.hat.internal.model.JavaObjectArray;
import com.sun.tools.hat.internal.model.JavaThing;
import com.sun.tools.hat.internal.model.JavaValueArray;
import com.sun.tools.hat.internal.model.StackTrace;
import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;

class ObjectQuery extends ClassQuery {
   public ObjectQuery() {
   }

   public void run() {
      this.startHtml("Object at " + this.query);
      long var1 = this.parseHex(this.query);
      JavaHeapObject var3 = this.snapshot.findThing(var1);
      if (var3 == null) {
         this.error("object not found");
      } else if (var3 instanceof JavaClass) {
         this.printFullClass((JavaClass)var3);
      } else if (var3 instanceof JavaValueArray) {
         this.print(((JavaValueArray)var3).valueString(true));
         this.printAllocationSite(var3);
         this.printReferencesTo(var3);
      } else if (var3 instanceof JavaObjectArray) {
         this.printFullObjectArray((JavaObjectArray)var3);
         this.printAllocationSite(var3);
         this.printReferencesTo(var3);
      } else if (var3 instanceof JavaObject) {
         this.printFullObject((JavaObject)var3);
         this.printAllocationSite(var3);
         this.printReferencesTo(var3);
      } else {
         this.print(var3.toString());
         this.printReferencesTo(var3);
      }

      this.endHtml();
   }

   private void printFullObject(JavaObject var1) {
      this.out.print("<h1>instance of ");
      this.print(var1.toString());
      this.out.print(" <small>(" + var1.getSize() + " bytes)</small>");
      this.out.println("</h1>\n");
      this.out.println("<h2>Class:</h2>");
      this.printClass(var1.getClazz());
      this.out.println("<h2>Instance data members:</h2>");
      JavaThing[] var2 = var1.getFields();
      final JavaField[] var3 = var1.getClazz().getFieldsForInstance();
      Integer[] var4 = new Integer[var2.length];

      int var5;
      for(var5 = 0; var5 < var2.length; ++var5) {
         var4[var5] = new Integer(var5);
      }

      ArraySorter.sort(var4, new Comparer() {
         public int compare(Object var1, Object var2) {
            JavaField var3x = var3[(Integer)var1];
            JavaField var4 = var3[(Integer)var2];
            return var3x.getName().compareTo(var4.getName());
         }
      });

      for(var5 = 0; var5 < var2.length; ++var5) {
         int var6 = var4[var5];
         this.printField(var3[var6]);
         this.out.print(" : ");
         this.printThing(var2[var6]);
         this.out.println("<br>");
      }

   }

   private void printFullObjectArray(JavaObjectArray var1) {
      JavaThing[] var2 = var1.getElements();
      this.out.println("<h1>Array of " + var2.length + " objects</h1>");
      this.out.println("<h2>Class:</h2>");
      this.printClass(var1.getClazz());
      this.out.println("<h2>Values</h2>");

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.out.print("" + var3 + " : ");
         this.printThing(var2[var3]);
         this.out.println("<br>");
      }

   }

   private void printAllocationSite(JavaHeapObject var1) {
      StackTrace var2 = var1.getAllocatedFrom();
      if (var2 != null && var2.getFrames().length != 0) {
         this.out.println("<h2>Object allocated from:</h2>");
         this.printStackTrace(var2);
      }
   }
}
