package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.parser.ReadBuffer;
import java.io.IOException;

public class JavaObject extends JavaLazyReadObject {
   private Object clazz;

   public JavaObject(long var1, long var3) {
      super(var3);
      this.clazz = makeId(var1);
   }

   public void resolve(Snapshot var1) {
      if (!(this.clazz instanceof JavaClass)) {
         if (this.clazz instanceof Number) {
            long var2 = getIdValue((Number)this.clazz);
            this.clazz = var1.findThing(var2);
            if (!(this.clazz instanceof JavaClass)) {
               this.warn("Class " + Long.toHexString(var2) + " not found, adding fake class!");
               ReadBuffer var5 = var1.getReadBuffer();
               int var6 = var1.getIdentifierSize();
               long var7 = this.getOffset() + (long)(2 * var6) + 4L;

               int var4;
               try {
                  var4 = var5.getInt(var7);
               } catch (IOException var10) {
                  throw new RuntimeException(var10);
               }

               this.clazz = var1.addFakeInstanceClass(var2, var4);
            }

            JavaClass var11 = (JavaClass)this.clazz;
            var11.resolve(var1);
            this.parseFields(this.getValue(), true);
            var11.addInstance(this);
            super.resolve(var1);
         } else {
            throw new InternalError("should not reach here");
         }
      }
   }

   public boolean isSameTypeAs(JavaThing var1) {
      if (!(var1 instanceof JavaObject)) {
         return false;
      } else {
         JavaObject var2 = (JavaObject)var1;
         return this.getClazz().equals(var2.getClazz());
      }
   }

   public JavaClass getClazz() {
      return (JavaClass)this.clazz;
   }

   public JavaThing[] getFields() {
      return this.parseFields(this.getValue(), false);
   }

   public JavaThing getField(String var1) {
      JavaThing[] var2 = this.getFields();
      JavaField[] var3 = this.getClazz().getFieldsForInstance();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4].getName().equals(var1)) {
            return var2[var4];
         }
      }

      return null;
   }

   public int compareTo(JavaThing var1) {
      if (var1 instanceof JavaObject) {
         JavaObject var2 = (JavaObject)var1;
         return this.getClazz().getName().compareTo(var2.getClazz().getName());
      } else {
         return super.compareTo(var1);
      }
   }

   public void visitReferencedObjects(JavaHeapObjectVisitor var1) {
      super.visitReferencedObjects(var1);
      JavaThing[] var2 = this.getFields();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] != null && (!var1.mightExclude() || !var1.exclude(this.getClazz().getClassForField(var3), this.getClazz().getFieldForInstance(var3))) && var2[var3] instanceof JavaHeapObject) {
            var1.visit((JavaHeapObject)var2[var3]);
         }
      }

   }

   public boolean refersOnlyWeaklyTo(Snapshot var1, JavaThing var2) {
      if (var1.getWeakReferenceClass() != null) {
         int var3 = var1.getReferentFieldIndex();
         if (var1.getWeakReferenceClass().isAssignableFrom(this.getClazz())) {
            JavaThing[] var4 = this.getFields();

            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var5 != var3 && var4[var5] == var2) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   public String describeReferenceTo(JavaThing var1, Snapshot var2) {
      JavaThing[] var3 = this.getFields();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == var1) {
            JavaField var5 = this.getClazz().getFieldForInstance(var4);
            return "field " + var5.getName();
         }
      }

      return super.describeReferenceTo(var1, var2);
   }

   public String toString() {
      if (this.getClazz().isString()) {
         JavaThing var1 = this.getField("value");
         return var1 instanceof JavaValueArray ? ((JavaValueArray)var1).valueString() : "null";
      } else {
         return super.toString();
      }
   }

   protected final int readValueLength() throws IOException {
      JavaClass var1 = this.getClazz();
      int var2 = var1.getIdentifierSize();
      long var3 = this.getOffset() + (long)(2 * var2) + 4L;
      return var1.getReadBuffer().getInt(var3);
   }

   protected final byte[] readValue() throws IOException {
      JavaClass var1 = this.getClazz();
      int var2 = var1.getIdentifierSize();
      ReadBuffer var3 = var1.getReadBuffer();
      long var4 = this.getOffset() + (long)(2 * var2) + 4L;
      int var6 = var3.getInt(var4);
      if (var6 == 0) {
         return Snapshot.EMPTY_BYTE_ARRAY;
      } else {
         byte[] var7 = new byte[var6];
         var3.get(var4 + 4L, var7);
         return var7;
      }
   }

   private JavaThing[] parseFields(byte[] var1, boolean var2) {
      JavaClass var3 = this.getClazz();
      int var4 = var3.getNumFieldsForInstance();
      JavaField[] var5 = var3.getFields();
      JavaThing[] var6 = new JavaThing[var4];
      Snapshot var7 = var3.getSnapshot();
      int var8 = var7.getIdentifierSize();
      int var9 = 0;
      var4 -= var5.length;
      JavaClass var10 = var3;
      int var11 = 0;

      for(int var12 = 0; var12 < var6.length; ++var9) {
         while(var9 >= var5.length) {
            var10 = var10.getSuperclass();
            var5 = var10.getFields();
            var9 = 0;
            var4 -= var5.length;
         }

         JavaField var13 = var5[var9];
         char var14 = var13.getSignature().charAt(0);
         int var15;
         long var18;
         switch (var14) {
            case 'B':
               byte var22 = byteAt(var11, var1);
               ++var11;
               var6[var4 + var9] = new JavaByte(var22);
               break;
            case 'C':
               char var21 = charAt(var11, var1);
               var11 += 2;
               var6[var4 + var9] = new JavaChar(var21);
               break;
            case 'D':
               double var20 = doubleAt(var11, var1);
               var11 += 8;
               var6[var4 + var9] = new JavaDouble(var20);
               break;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
               throw new RuntimeException("invalid signature: " + var14);
            case 'F':
               float var19 = floatAt(var11, var1);
               var11 += 4;
               var6[var4 + var9] = new JavaFloat(var19);
               break;
            case 'I':
               var15 = intAt(var11, var1);
               var11 += 4;
               var6[var4 + var9] = new JavaInt(var15);
               break;
            case 'J':
               var18 = longAt(var11, var1);
               var11 += 8;
               var6[var4 + var9] = new JavaLong(var18);
               break;
            case 'L':
            case '[':
               var18 = this.objectIdAt(var11, var1);
               var11 += var8;
               JavaObjectRef var17 = new JavaObjectRef(var18);
               var6[var4 + var9] = var17.dereference(var7, var13, var2);
               break;
            case 'S':
               var15 = shortAt(var11, var1);
               var11 += 2;
               var6[var4 + var9] = new JavaShort((short)var15);
               break;
            case 'Z':
               var15 = byteAt(var11, var1);
               ++var11;
               var6[var4 + var9] = new JavaBoolean(var15 != 0);
         }

         ++var12;
      }

      return var6;
   }

   private void warn(String var1) {
      System.out.println("WARNING: " + var1);
   }
}
