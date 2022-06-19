package com.sun.tools.jdi;

import java.io.IOException;

public class Packet {
   public static final short NoFlags = 0;
   public static final short Reply = 128;
   public static final short ReplyNoError = 0;
   static int uID = 1;
   static final byte[] nullData = new byte[0];
   int id = uniqID();
   short flags = 0;
   short cmdSet;
   short cmd;
   short errorCode;
   byte[] data;
   volatile boolean replied = false;

   public byte[] toByteArray() {
      int var1 = this.data.length + 11;
      byte[] var2 = new byte[var1];
      var2[0] = (byte)(var1 >>> 24 & 255);
      var2[1] = (byte)(var1 >>> 16 & 255);
      var2[2] = (byte)(var1 >>> 8 & 255);
      var2[3] = (byte)(var1 >>> 0 & 255);
      var2[4] = (byte)(this.id >>> 24 & 255);
      var2[5] = (byte)(this.id >>> 16 & 255);
      var2[6] = (byte)(this.id >>> 8 & 255);
      var2[7] = (byte)(this.id >>> 0 & 255);
      var2[8] = (byte)this.flags;
      if ((this.flags & 128) == 0) {
         var2[9] = (byte)this.cmdSet;
         var2[10] = (byte)this.cmd;
      } else {
         var2[9] = (byte)(this.errorCode >>> 8 & 255);
         var2[10] = (byte)(this.errorCode >>> 0 & 255);
      }

      if (this.data.length > 0) {
         System.arraycopy(this.data, 0, var2, 11, this.data.length);
      }

      return var2;
   }

   public static Packet fromByteArray(byte[] var0) throws IOException {
      if (var0.length < 11) {
         throw new IOException("packet is insufficient size");
      } else {
         int var1 = var0[0] & 255;
         int var2 = var0[1] & 255;
         int var3 = var0[2] & 255;
         int var4 = var0[3] & 255;
         int var5 = var1 << 24 | var2 << 16 | var3 << 8 | var4 << 0;
         if (var5 != var0.length) {
            throw new IOException("length size mis-match");
         } else {
            int var6 = var0[4] & 255;
            int var7 = var0[5] & 255;
            int var8 = var0[6] & 255;
            int var9 = var0[7] & 255;
            Packet var10 = new Packet();
            var10.id = var6 << 24 | var7 << 16 | var8 << 8 | var9 << 0;
            var10.flags = (short)(var0[8] & 255);
            if ((var10.flags & 128) == 0) {
               var10.cmdSet = (short)(var0[9] & 255);
               var10.cmd = (short)(var0[10] & 255);
            } else {
               short var11 = (short)(var0[9] & 255);
               short var12 = (short)(var0[10] & 255);
               var10.errorCode = (short)((var11 << 8) + (var12 << 0));
            }

            var10.data = new byte[var0.length - 11];
            System.arraycopy(var0, 11, var10.data, 0, var10.data.length);
            return var10;
         }
      }
   }

   Packet() {
      this.data = nullData;
   }

   private static synchronized int uniqID() {
      return uID++;
   }
}
