package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.parser.ReadBuffer;
import java.io.IOException;

public class JavaObjectArray extends JavaLazyReadObject {
   private Object clazz;

   public JavaObjectArray(long var1, long var3) {
      super(var3);
      this.clazz = makeId(var1);
   }

   public JavaClass getClazz() {
      return (JavaClass)this.clazz;
   }

   public void resolve(Snapshot var1) {
      if (!(this.clazz instanceof JavaClass)) {
         long var2 = getIdValue((Number)this.clazz);
         JavaHeapObject var4;
         if (var1.isNewStyleArrayClass()) {
            var4 = var1.findThing(var2);
            if (var4 instanceof JavaClass) {
               this.clazz = (JavaClass)var4;
            }
         }

         if (!(this.clazz instanceof JavaClass)) {
            var4 = var1.findThing(var2);
            if (var4 != null && var4 instanceof JavaClass) {
               JavaClass var5 = (JavaClass)var4;
               String var6 = var5.getName();
               if (!var6.startsWith("[")) {
                  var6 = "L" + var5.getName() + ";";
               }

               this.clazz = var1.getArrayClass(var6);
            }
         }

         if (!(this.clazz instanceof JavaClass)) {
            this.clazz = var1.getOtherArrayType();
         }

         ((JavaClass)this.clazz).addInstance(this);
         super.resolve(var1);
      }
   }

   public JavaThing[] getValues() {
      return this.getElements();
   }

   public JavaThing[] getElements() {
      Snapshot var1 = this.getClazz().getSnapshot();
      byte[] var2 = this.getValue();
      int var3 = var1.getIdentifierSize();
      int var4 = var2.length / var3;
      JavaThing[] var5 = new JavaThing[var4];
      int var6 = 0;

      for(int var7 = 0; var7 < var5.length; ++var7) {
         long var8 = this.objectIdAt(var6, var2);
         var6 += var3;
         var5[var7] = var1.findThing(var8);
      }

      return var5;
   }

   public int compareTo(JavaThing var1) {
      return var1 instanceof JavaObjectArray ? 0 : super.compareTo(var1);
   }

   public int getLength() {
      return this.getValueLength() / this.getClazz().getIdentifierSize();
   }

   public void visitReferencedObjects(JavaHeapObjectVisitor var1) {
      super.visitReferencedObjects(var1);
      JavaThing[] var2 = this.getElements();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] != null && var2[var3] instanceof JavaHeapObject) {
            var1.visit((JavaHeapObject)var2[var3]);
         }
      }

   }

   public String describeReferenceTo(JavaThing var1, Snapshot var2) {
      JavaThing[] var3 = this.getElements();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == var1) {
            return "Element " + var4 + " of " + this;
         }
      }

      return super.describeReferenceTo(var1, var2);
   }

   protected final int readValueLength() throws IOException {
      JavaClass var1 = this.getClazz();
      ReadBuffer var2 = var1.getReadBuffer();
      int var3 = var1.getIdentifierSize();
      long var4 = this.getOffset() + (long)var3 + 4L;
      int var6 = var2.getInt(var4);
      return var6 * var1.getIdentifierSize();
   }

   protected final byte[] readValue() throws IOException {
      JavaClass var1 = this.getClazz();
      ReadBuffer var2 = var1.getReadBuffer();
      int var3 = var1.getIdentifierSize();
      long var4 = this.getOffset() + (long)var3 + 4L;
      int var6 = var2.getInt(var4);
      if (var6 == 0) {
         return Snapshot.EMPTY_BYTE_ARRAY;
      } else {
         byte[] var7 = new byte[var6 * var3];
         var2.get(var4 + 4L + (long)var3, var7);
         return var7;
      }
   }
}
