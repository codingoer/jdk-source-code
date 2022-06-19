package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class FieldImpl extends TypeComponentImpl implements Field, ValueContainer {
   FieldImpl(VirtualMachine var1, ReferenceTypeImpl var2, long var3, String var5, String var6, String var7, int var8) {
      super(var1, var2, var3, var5, var6, var7, var8);
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof FieldImpl) {
         FieldImpl var2 = (FieldImpl)var1;
         return this.declaringType().equals(var2.declaringType()) && this.ref() == var2.ref() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)this.ref();
   }

   public int compareTo(Field var1) {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)this.declaringType();
      int var3 = var2.compareTo(var1.declaringType());
      if (var3 == 0) {
         var3 = var2.indexOf((Field)this) - var2.indexOf(var1);
      }

      return var3;
   }

   public Type type() throws ClassNotLoadedException {
      return this.findType(this.signature());
   }

   public Type findType(String var1) throws ClassNotLoadedException {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)this.declaringType();
      return var2.findType(var1);
   }

   public String typeName() {
      JNITypeParser var1 = new JNITypeParser(this.signature());
      return var1.typeName();
   }

   public boolean isTransient() {
      return this.isModifierSet(128);
   }

   public boolean isVolatile() {
      return this.isModifierSet(64);
   }

   public boolean isEnumConstant() {
      return this.isModifierSet(16384);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.declaringType().name());
      var1.append('.');
      var1.append(this.name());
      return var1.toString();
   }
}
