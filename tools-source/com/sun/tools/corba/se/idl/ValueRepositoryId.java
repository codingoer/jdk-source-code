package com.sun.tools.corba.se.idl;

import java.security.MessageDigest;
import java.util.Hashtable;

public class ValueRepositoryId {
   private MessageDigest sha;
   private int index;
   private Hashtable types;
   private String hashcode;

   public ValueRepositoryId() {
      try {
         this.sha = MessageDigest.getInstance("SHA-1");
      } catch (Exception var2) {
      }

      this.index = 0;
      this.types = new Hashtable();
      this.hashcode = null;
   }

   public void addValue(int var1) {
      this.sha.update((byte)(var1 >> 24 & 15));
      this.sha.update((byte)(var1 >> 16 & 15));
      this.sha.update((byte)(var1 >> 8 & 15));
      this.sha.update((byte)(var1 & 15));
      ++this.index;
   }

   public void addType(SymtabEntry var1) {
      this.types.put(var1, new Integer(this.index));
   }

   public boolean isNewType(SymtabEntry var1) {
      Object var2 = this.types.get(var1);
      if (var2 == null) {
         this.addType(var1);
         return true;
      } else {
         this.addValue(-1);
         this.addValue((Integer)var2);
         return false;
      }
   }

   public String getHashcode() {
      if (this.hashcode == null) {
         byte[] var1 = this.sha.digest();
         this.hashcode = hexOf(var1[0]) + hexOf(var1[1]) + hexOf(var1[2]) + hexOf(var1[3]) + hexOf(var1[4]) + hexOf(var1[5]) + hexOf(var1[6]) + hexOf(var1[7]);
      }

      return this.hashcode;
   }

   private static String hexOf(byte var0) {
      int var1 = var0 >> 4 & 15;
      int var2 = var0 & 15;
      return "0123456789ABCDEF".substring(var1, var1 + 1) + "0123456789ABCDEF".substring(var2, var2 + 1);
   }
}
