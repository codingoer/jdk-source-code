package com.sun.tools.classfile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Attributes implements Iterable {
   public final Attribute[] attrs;
   public final Map map;

   Attributes(ClassReader var1) throws IOException {
      this.map = new HashMap();
      int var2 = var1.readUnsignedShort();
      this.attrs = new Attribute[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         Attribute var4 = Attribute.read(var1);
         this.attrs[var3] = var4;

         try {
            this.map.put(var4.getName(var1.getConstantPool()), var4);
         } catch (ConstantPoolException var6) {
         }
      }

   }

   public Attributes(ConstantPool var1, Attribute[] var2) {
      this.attrs = var2;
      this.map = new HashMap();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Attribute var4 = var2[var3];

         try {
            this.map.put(var4.getName(var1), var4);
         } catch (ConstantPoolException var6) {
         }
      }

   }

   public Iterator iterator() {
      return Arrays.asList(this.attrs).iterator();
   }

   public Attribute get(int var1) {
      return this.attrs[var1];
   }

   public Attribute get(String var1) {
      return (Attribute)this.map.get(var1);
   }

   public int getIndex(ConstantPool var1, String var2) {
      for(int var3 = 0; var3 < this.attrs.length; ++var3) {
         Attribute var4 = this.attrs[var3];

         try {
            if (var4 != null && var4.getName(var1).equals(var2)) {
               return var3;
            }
         } catch (ConstantPoolException var6) {
         }
      }

      return -1;
   }

   public int size() {
      return this.attrs.length;
   }

   public int byteLength() {
      int var1 = 2;
      Attribute[] var2 = this.attrs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Attribute var5 = var2[var4];
         var1 += var5.byteLength();
      }

      return var1;
   }
}
