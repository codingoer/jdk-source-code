package sun.tools.java;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BinaryCode implements Constants {
   int maxStack;
   int maxLocals;
   BinaryExceptionHandler[] exceptionHandlers;
   BinaryAttribute atts;
   BinaryConstantPool cpool;
   byte[] code;

   public BinaryCode(byte[] var1, BinaryConstantPool var2, Environment var3) {
      DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(var1));

      try {
         this.cpool = var2;
         this.maxStack = var4.readUnsignedShort();
         this.maxLocals = var4.readUnsignedShort();
         int var5 = var4.readInt();
         this.code = new byte[var5];
         var4.read(this.code);
         int var6 = var4.readUnsignedShort();
         this.exceptionHandlers = new BinaryExceptionHandler[var6];

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var4.readUnsignedShort();
            int var9 = var4.readUnsignedShort();
            int var10 = var4.readUnsignedShort();
            ClassDeclaration var11 = var2.getDeclaration(var3, var4.readUnsignedShort());
            this.exceptionHandlers[var7] = new BinaryExceptionHandler(var8, var9, var10, var11);
         }

         this.atts = BinaryAttribute.load(var4, var2, -1);
         if (var4.available() != 0) {
            System.err.println("Should have exhausted input stream!");
         }

      } catch (IOException var12) {
         throw new CompilerError(var12);
      }
   }

   public BinaryExceptionHandler[] getExceptionHandlers() {
      return this.exceptionHandlers;
   }

   public byte[] getCode() {
      return this.code;
   }

   public int getMaxStack() {
      return this.maxStack;
   }

   public int getMaxLocals() {
      return this.maxLocals;
   }

   public BinaryAttribute getAttributes() {
      return this.atts;
   }

   public static BinaryCode load(BinaryMember var0, BinaryConstantPool var1, Environment var2) {
      byte[] var3 = var0.getAttribute(idCode);
      return var3 != null ? new BinaryCode(var3, var1, var2) : null;
   }
}
