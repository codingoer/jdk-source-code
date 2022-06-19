package com.sun.tools.classfile;

import java.io.IOException;

public class Exceptions_attribute extends Attribute {
   public final int number_of_exceptions;
   public final int[] exception_index_table;

   Exceptions_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.number_of_exceptions = var1.readUnsignedShort();
      this.exception_index_table = new int[this.number_of_exceptions];

      for(int var4 = 0; var4 < this.number_of_exceptions; ++var4) {
         this.exception_index_table[var4] = var1.readUnsignedShort();
      }

   }

   public Exceptions_attribute(ConstantPool var1, int[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("Exceptions"), var2);
   }

   public Exceptions_attribute(int var1, int[] var2) {
      super(var1, 2 + 2 * var2.length);
      this.number_of_exceptions = var2.length;
      this.exception_index_table = var2;
   }

   public String getException(int var1, ConstantPool var2) throws ConstantPoolException {
      int var3 = this.exception_index_table[var1];
      return var2.getClassInfo(var3).getName();
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitExceptions(this, var2);
   }
}
