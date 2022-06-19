package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.parser.ReadBuffer;
import java.io.IOException;

public class JavaValueArray extends JavaLazyReadObject implements ArrayTypeCodes {
   private JavaClass clazz;
   private int data;
   private static final int SIGNATURE_MASK = 255;
   private static final int LENGTH_DIVIDER_MASK = 65280;
   private static final int LENGTH_DIVIDER_SHIFT = 8;

   private static String arrayTypeName(byte var0) {
      switch (var0) {
         case 66:
            return "byte[]";
         case 67:
            return "char[]";
         case 68:
            return "double[]";
         case 69:
         case 71:
         case 72:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         default:
            throw new RuntimeException("invalid array element sig: " + var0);
         case 70:
            return "float[]";
         case 73:
            return "int[]";
         case 74:
            return "long[]";
         case 83:
            return "short[]";
         case 90:
            return "boolean[]";
      }
   }

   private static int elementSize(byte var0) {
      switch (var0) {
         case 4:
         case 8:
            return 1;
         case 5:
         case 9:
            return 2;
         case 6:
         case 10:
            return 4;
         case 7:
         case 11:
            return 8;
         default:
            throw new RuntimeException("invalid array element type: " + var0);
      }
   }

   protected final int readValueLength() throws IOException {
      JavaClass var1 = this.getClazz();
      ReadBuffer var2 = var1.getReadBuffer();
      int var3 = var1.getIdentifierSize();
      long var4 = this.getOffset() + (long)var3 + 4L;
      int var6 = var2.getInt(var4);
      byte var7 = var2.getByte(var4 + 4L);
      return var6 * elementSize(var7);
   }

   protected final byte[] readValue() throws IOException {
      JavaClass var1 = this.getClazz();
      ReadBuffer var2 = var1.getReadBuffer();
      int var3 = var1.getIdentifierSize();
      long var4 = this.getOffset() + (long)var3 + 4L;
      int var6 = var2.getInt(var4);
      byte var7 = var2.getByte(var4 + 4L);
      if (var6 == 0) {
         return Snapshot.EMPTY_BYTE_ARRAY;
      } else {
         var6 *= elementSize(var7);
         byte[] var8 = new byte[var6];
         var2.get(var4 + 5L, var8);
         return var8;
      }
   }

   public JavaValueArray(byte var1, long var2) {
      super(var2);
      this.data = var1 & 255;
   }

   public JavaClass getClazz() {
      return this.clazz;
   }

   public void visitReferencedObjects(JavaHeapObjectVisitor var1) {
      super.visitReferencedObjects(var1);
   }

   public void resolve(Snapshot var1) {
      if (!(this.clazz instanceof JavaClass)) {
         byte var2 = this.getElementType();
         this.clazz = var1.findClass(arrayTypeName(var2));
         if (this.clazz == null) {
            this.clazz = var1.getArrayClass("" + (char)var2);
         }

         this.getClazz().addInstance(this);
         super.resolve(var1);
      }
   }

   public int getLength() {
      int var1 = (this.data & '\uff00') >>> 8;
      if (var1 == 0) {
         byte var2 = this.getElementType();
         switch (var2) {
            case 66:
            case 90:
               var1 = 1;
               break;
            case 67:
            case 83:
               var1 = 2;
               break;
            case 68:
            case 74:
               var1 = 8;
               break;
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            default:
               throw new RuntimeException("unknown primitive type: " + var2);
            case 70:
            case 73:
               var1 = 4;
         }

         this.data |= var1 << 8;
      }

      return this.getValueLength() / var1;
   }

   public Object getElements() {
      int var1 = this.getLength();
      byte var2 = this.getElementType();
      byte[] var3 = this.getValue();
      int var4 = 0;
      int var6;
      switch (var2) {
         case 66:
            byte[] var13 = new byte[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var13[var6] = byteAt(var4, var3);
               ++var4;
            }

            return var13;
         case 67:
            char[] var12 = new char[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var12[var6] = charAt(var4, var3);
               var4 += 2;
            }

            return var12;
         case 68:
            double[] var11 = new double[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var11[var6] = doubleAt(var4, var3);
               var4 += 8;
            }

            return var11;
         case 69:
         case 71:
         case 72:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         default:
            throw new RuntimeException("unknown primitive type?");
         case 70:
            float[] var10 = new float[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var10[var6] = floatAt(var4, var3);
               var4 += 4;
            }

            return var10;
         case 73:
            int[] var9 = new int[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var9[var6] = intAt(var4, var3);
               var4 += 4;
            }

            return var9;
         case 74:
            long[] var8 = new long[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var8[var6] = longAt(var4, var3);
               var4 += 8;
            }

            return var8;
         case 83:
            short[] var7 = new short[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var7[var6] = shortAt(var4, var3);
               var4 += 2;
            }

            return var7;
         case 90:
            boolean[] var5 = new boolean[var1];

            for(var6 = 0; var6 < var1; ++var6) {
               var5[var6] = booleanAt(var4, var3);
               ++var4;
            }

            return var5;
      }
   }

   public byte getElementType() {
      return (byte)(this.data & 255);
   }

   private void checkIndex(int var1) {
      if (var1 < 0 || var1 >= this.getLength()) {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   private void requireType(char var1) {
      if (this.getElementType() != var1) {
         throw new RuntimeException("not of type : " + var1);
      }
   }

   public boolean getBooleanAt(int var1) {
      this.checkIndex(var1);
      this.requireType('Z');
      return booleanAt(var1, this.getValue());
   }

   public byte getByteAt(int var1) {
      this.checkIndex(var1);
      this.requireType('B');
      return byteAt(var1, this.getValue());
   }

   public char getCharAt(int var1) {
      this.checkIndex(var1);
      this.requireType('C');
      return charAt(var1 << 1, this.getValue());
   }

   public short getShortAt(int var1) {
      this.checkIndex(var1);
      this.requireType('S');
      return shortAt(var1 << 1, this.getValue());
   }

   public int getIntAt(int var1) {
      this.checkIndex(var1);
      this.requireType('I');
      return intAt(var1 << 2, this.getValue());
   }

   public long getLongAt(int var1) {
      this.checkIndex(var1);
      this.requireType('J');
      return longAt(var1 << 3, this.getValue());
   }

   public float getFloatAt(int var1) {
      this.checkIndex(var1);
      this.requireType('F');
      return floatAt(var1 << 2, this.getValue());
   }

   public double getDoubleAt(int var1) {
      this.checkIndex(var1);
      this.requireType('D');
      return doubleAt(var1 << 3, this.getValue());
   }

   public String valueString() {
      return this.valueString(true);
   }

   public String valueString(boolean var1) {
      byte[] var3 = this.getValue();
      int var4 = var3.length;
      byte var5 = this.getElementType();
      StringBuffer var2;
      int var7;
      if (var5 == 67) {
         var2 = new StringBuffer();

         for(int var6 = 0; var6 < var3.length; var6 += 2) {
            var7 = charAt(var6, var3);
            var2.append((char)var7);
         }
      } else {
         short var11 = 8;
         if (var1) {
            var11 = 1000;
         }

         var2 = new StringBuffer("{");
         var7 = 0;
         int var8 = 0;

         while(var8 < var3.length) {
            if (var7 > 0) {
               var2.append(", ");
            }

            if (var7 >= var11) {
               var2.append("... ");
               break;
            }

            ++var7;
            int var12;
            switch (var5) {
               case 66:
                  var12 = 255 & byteAt(var8, var3);
                  var2.append("0x" + Integer.toString(var12, 16));
                  ++var8;
                  break;
               case 67:
               case 69:
               case 71:
               case 72:
               case 75:
               case 76:
               case 77:
               case 78:
               case 79:
               case 80:
               case 81:
               case 82:
               case 84:
               case 85:
               case 86:
               case 87:
               case 88:
               case 89:
               default:
                  throw new RuntimeException("unknown primitive type?");
               case 68:
                  double var15 = doubleAt(var8, var3);
                  var2.append("" + var15);
                  var8 += 8;
                  break;
               case 70:
                  float var14 = floatAt(var8, var3);
                  var2.append("" + var14);
                  var8 += 4;
                  break;
               case 73:
                  var12 = intAt(var8, var3);
                  var8 += 4;
                  var2.append("" + var12);
                  break;
               case 74:
                  long var13 = longAt(var8, var3);
                  var2.append("" + var13);
                  var8 += 8;
                  break;
               case 83:
                  var12 = shortAt(var8, var3);
                  var8 += 2;
                  var2.append("" + var12);
                  break;
               case 90:
                  boolean var9 = booleanAt(var8, var3);
                  if (var9) {
                     var2.append("true");
                  } else {
                     var2.append("false");
                  }

                  ++var8;
            }
         }

         var2.append("}");
      }

      return var2.toString();
   }
}
