package com.sun.tools.classfile;

import java.util.Locale;

public class Instruction {
   private byte[] bytes;
   private int pc;

   public Instruction(byte[] var1, int var2) {
      this.bytes = var1;
      this.pc = var2;
   }

   public int getPC() {
      return this.pc;
   }

   public int getByte(int var1) {
      return this.bytes[this.pc + var1];
   }

   public int getUnsignedByte(int var1) {
      return this.getByte(var1) & 255;
   }

   public int getShort(int var1) {
      return this.getByte(var1) << 8 | this.getUnsignedByte(var1 + 1);
   }

   public int getUnsignedShort(int var1) {
      return this.getShort(var1) & '\uffff';
   }

   public int getInt(int var1) {
      return this.getShort(var1) << 16 | this.getUnsignedShort(var1 + 2);
   }

   public Opcode getOpcode() {
      int var1 = this.getUnsignedByte(0);
      switch (var1) {
         case 196:
         case 254:
         case 255:
            return Opcode.get(var1, this.getUnsignedByte(1));
         default:
            return Opcode.get(var1);
      }
   }

   public String getMnemonic() {
      Opcode var1 = this.getOpcode();
      return var1 == null ? "bytecode " + this.getUnsignedByte(0) : var1.toString().toLowerCase(Locale.US);
   }

   public int length() {
      Opcode var1 = this.getOpcode();
      if (var1 == null) {
         return 1;
      } else {
         int var2;
         int var3;
         switch (var1) {
            case TABLESWITCH:
               var2 = align(this.pc + 1) - this.pc;
               var3 = this.getInt(var2 + 4);
               int var4 = this.getInt(var2 + 8);
               return var2 + 12 + 4 * (var4 - var3 + 1);
            case LOOKUPSWITCH:
               var2 = align(this.pc + 1) - this.pc;
               var3 = this.getInt(var2 + 4);
               return var2 + 8 + 8 * var3;
            default:
               return var1.kind.length;
         }
      }
   }

   public Kind getKind() {
      Opcode var1 = this.getOpcode();
      return var1 != null ? var1.kind : Instruction.Kind.UNKNOWN;
   }

   public Object accept(KindVisitor var1, Object var2) {
      switch (this.getKind()) {
         case NO_OPERANDS:
            return var1.visitNoOperands(this, var2);
         case ATYPE:
            return var1.visitArrayType(this, Instruction.TypeKind.get(this.getUnsignedByte(1)), var2);
         case BRANCH:
            return var1.visitBranch(this, this.getShort(1), var2);
         case BRANCH_W:
            return var1.visitBranch(this, this.getInt(1), var2);
         case BYTE:
            return var1.visitValue(this, this.getByte(1), var2);
         case CPREF:
            return var1.visitConstantPoolRef(this, this.getUnsignedByte(1), var2);
         case CPREF_W:
            return var1.visitConstantPoolRef(this, this.getUnsignedShort(1), var2);
         case CPREF_W_UBYTE:
         case CPREF_W_UBYTE_ZERO:
            return var1.visitConstantPoolRefAndValue(this, this.getUnsignedShort(1), this.getUnsignedByte(3), var2);
         case DYNAMIC:
            int var3;
            int var4;
            int var5;
            int[] var7;
            int var8;
            switch (this.getOpcode()) {
               case TABLESWITCH:
                  var3 = align(this.pc + 1) - this.pc;
                  var4 = this.getInt(var3);
                  var5 = this.getInt(var3 + 4);
                  int var9 = this.getInt(var3 + 8);
                  var7 = new int[var9 - var5 + 1];

                  for(var8 = 0; var8 < var7.length; ++var8) {
                     var7[var8] = this.getInt(var3 + 12 + 4 * var8);
                  }

                  return var1.visitTableSwitch(this, var4, var5, var9, var7, var2);
               case LOOKUPSWITCH:
                  var3 = align(this.pc + 1) - this.pc;
                  var4 = this.getInt(var3);
                  var5 = this.getInt(var3 + 4);
                  int[] var6 = new int[var5];
                  var7 = new int[var5];

                  for(var8 = 0; var8 < var5; ++var8) {
                     var6[var8] = this.getInt(var3 + 8 + var8 * 8);
                     var7[var8] = this.getInt(var3 + 12 + var8 * 8);
                  }

                  return var1.visitLookupSwitch(this, var4, var5, var6, var7, var2);
               default:
                  throw new IllegalStateException();
            }
         case LOCAL:
            return var1.visitLocal(this, this.getUnsignedByte(1), var2);
         case LOCAL_BYTE:
            return var1.visitLocalAndValue(this, this.getUnsignedByte(1), this.getByte(2), var2);
         case SHORT:
            return var1.visitValue(this, this.getShort(1), var2);
         case WIDE_NO_OPERANDS:
            return var1.visitNoOperands(this, var2);
         case WIDE_LOCAL:
            return var1.visitLocal(this, this.getUnsignedShort(2), var2);
         case WIDE_CPREF_W:
            return var1.visitConstantPoolRef(this, this.getUnsignedShort(2), var2);
         case WIDE_CPREF_W_SHORT:
            return var1.visitConstantPoolRefAndValue(this, this.getUnsignedShort(2), this.getUnsignedByte(4), var2);
         case WIDE_LOCAL_SHORT:
            return var1.visitLocalAndValue(this, this.getUnsignedShort(2), this.getShort(4), var2);
         case UNKNOWN:
            return var1.visitUnknown(this, var2);
         default:
            throw new IllegalStateException();
      }
   }

   private static int align(int var0) {
      return var0 + 3 & -4;
   }

   public static enum TypeKind {
      T_BOOLEAN(4, "boolean"),
      T_CHAR(5, "char"),
      T_FLOAT(6, "float"),
      T_DOUBLE(7, "double"),
      T_BYTE(8, "byte"),
      T_SHORT(9, "short"),
      T_INT(10, "int"),
      T_LONG(11, "long");

      public final int value;
      public final String name;

      private TypeKind(int var3, String var4) {
         this.value = var3;
         this.name = var4;
      }

      public static TypeKind get(int var0) {
         switch (var0) {
            case 4:
               return T_BOOLEAN;
            case 5:
               return T_CHAR;
            case 6:
               return T_FLOAT;
            case 7:
               return T_DOUBLE;
            case 8:
               return T_BYTE;
            case 9:
               return T_SHORT;
            case 10:
               return T_INT;
            case 11:
               return T_LONG;
            default:
               return null;
         }
      }
   }

   public interface KindVisitor {
      Object visitNoOperands(Instruction var1, Object var2);

      Object visitArrayType(Instruction var1, TypeKind var2, Object var3);

      Object visitBranch(Instruction var1, int var2, Object var3);

      Object visitConstantPoolRef(Instruction var1, int var2, Object var3);

      Object visitConstantPoolRefAndValue(Instruction var1, int var2, int var3, Object var4);

      Object visitLocal(Instruction var1, int var2, Object var3);

      Object visitLocalAndValue(Instruction var1, int var2, int var3, Object var4);

      Object visitLookupSwitch(Instruction var1, int var2, int var3, int[] var4, int[] var5, Object var6);

      Object visitTableSwitch(Instruction var1, int var2, int var3, int var4, int[] var5, Object var6);

      Object visitValue(Instruction var1, int var2, Object var3);

      Object visitUnknown(Instruction var1, Object var2);
   }

   public static enum Kind {
      NO_OPERANDS(1),
      ATYPE(2),
      BRANCH(3),
      BRANCH_W(5),
      BYTE(2),
      CPREF(2),
      CPREF_W(3),
      CPREF_W_UBYTE(4),
      CPREF_W_UBYTE_ZERO(5),
      DYNAMIC(-1),
      LOCAL(2),
      LOCAL_BYTE(3),
      SHORT(3),
      WIDE_NO_OPERANDS(2),
      WIDE_LOCAL(4),
      WIDE_CPREF_W(4),
      WIDE_CPREF_W_SHORT(6),
      WIDE_LOCAL_SHORT(6),
      UNKNOWN(1);

      public final int length;

      private Kind(int var3) {
         this.length = var3;
      }
   }
}
