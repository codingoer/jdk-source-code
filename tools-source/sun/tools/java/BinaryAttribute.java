package sun.tools.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class BinaryAttribute implements Constants {
   Identifier name;
   byte[] data;
   BinaryAttribute next;

   BinaryAttribute(Identifier var1, byte[] var2, BinaryAttribute var3) {
      this.name = var1;
      this.data = var2;
      this.next = var3;
   }

   public static BinaryAttribute load(DataInputStream var0, BinaryConstantPool var1, int var2) throws IOException {
      BinaryAttribute var3 = null;
      int var4 = var0.readUnsignedShort();

      for(int var5 = 0; var5 < var4; ++var5) {
         Identifier var6 = var1.getIdentifier(var0.readUnsignedShort());
         int var7 = var0.readInt();
         if (var6.equals(idCode) && (var2 & 2) == 0) {
            var0.skipBytes(var7);
         } else {
            byte[] var8 = new byte[var7];
            var0.readFully(var8);
            var3 = new BinaryAttribute(var6, var8, var3);
         }
      }

      return var3;
   }

   static void write(BinaryAttribute var0, DataOutputStream var1, BinaryConstantPool var2, Environment var3) throws IOException {
      int var4 = 0;

      BinaryAttribute var5;
      for(var5 = var0; var5 != null; var5 = var5.next) {
         ++var4;
      }

      var1.writeShort(var4);

      for(var5 = var0; var5 != null; var5 = var5.next) {
         Identifier var6 = var5.name;
         byte[] var7 = var5.data;
         var1.writeShort(var2.indexString(var6.toString(), var3));
         var1.writeInt(var7.length);
         var1.write(var7, 0, var7.length);
      }

   }

   public Identifier getName() {
      return this.name;
   }

   public byte[] getData() {
      return this.data;
   }

   public BinaryAttribute getNextAttribute() {
      return this.next;
   }
}
