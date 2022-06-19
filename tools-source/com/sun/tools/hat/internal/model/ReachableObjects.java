package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;
import java.util.Enumeration;
import java.util.Hashtable;

public class ReachableObjects {
   private JavaHeapObject root;
   private JavaThing[] reachables;
   private String[] excludedFields;
   private String[] usedFields;
   private long totalSize;

   public ReachableObjects(JavaHeapObject var1, final ReachableExcludes var2) {
      this.root = var1;
      final Hashtable var3 = new Hashtable();
      final Hashtable var4 = new Hashtable();
      final Hashtable var5 = new Hashtable();
      AbstractJavaHeapObjectVisitor var6 = new AbstractJavaHeapObjectVisitor() {
         public void visit(JavaHeapObject var1) {
            if (var1 != null && var1.getSize() > 0 && var3.get(var1) == null) {
               var3.put(var1, var1);
               var1.visitReferencedObjects(this);
            }

         }

         public boolean mightExclude() {
            return var2 != null;
         }

         public boolean exclude(JavaClass var1, JavaField var2x) {
            if (var2 == null) {
               return false;
            } else {
               String var3x = var1.getName() + "." + var2x.getName();
               if (var2.isExcluded(var3x)) {
                  var4.put(var3x, var3x);
                  return true;
               } else {
                  var5.put(var3x, var3x);
                  return false;
               }
            }
         }
      };
      var6.visit(var1);
      var3.remove(var1);
      JavaThing[] var7 = new JavaThing[var3.size()];
      int var8 = 0;

      for(Enumeration var9 = var3.elements(); var9.hasMoreElements(); var7[var8++] = (JavaThing)var9.nextElement()) {
      }

      ArraySorter.sort(var7, new Comparer() {
         public int compare(Object var1, Object var2) {
            JavaThing var3 = (JavaThing)var1;
            JavaThing var4 = (JavaThing)var2;
            int var5 = var4.getSize() - var3.getSize();
            return var5 != 0 ? var5 : var3.compareTo(var4);
         }
      });
      this.reachables = var7;
      this.totalSize = (long)var1.getSize();

      for(var8 = 0; var8 < var7.length; ++var8) {
         this.totalSize += (long)var7[var8].getSize();
      }

      this.excludedFields = this.getElements(var4);
      this.usedFields = this.getElements(var5);
   }

   public JavaHeapObject getRoot() {
      return this.root;
   }

   public JavaThing[] getReachables() {
      return this.reachables;
   }

   public long getTotalSize() {
      return this.totalSize;
   }

   public String[] getExcludedFields() {
      return this.excludedFields;
   }

   public String[] getUsedFields() {
      return this.usedFields;
   }

   private String[] getElements(Hashtable var1) {
      Object[] var2 = var1.keySet().toArray();
      int var3 = var2.length;
      String[] var4 = new String[var3];
      System.arraycopy(var2, 0, var4, 0, var3);
      ArraySorter.sortArrayOfStrings(var4);
      return var4;
   }
}
