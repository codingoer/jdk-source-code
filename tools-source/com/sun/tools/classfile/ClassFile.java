package com.sun.tools.classfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassFile {
   public final int magic;
   public final int minor_version;
   public final int major_version;
   public final ConstantPool constant_pool;
   public final AccessFlags access_flags;
   public final int this_class;
   public final int super_class;
   public final int[] interfaces;
   public final Field[] fields;
   public final Method[] methods;
   public final Attributes attributes;

   public static ClassFile read(File var0) throws IOException, ConstantPoolException {
      return read(var0.toPath(), new Attribute.Factory());
   }

   public static ClassFile read(Path var0) throws IOException, ConstantPoolException {
      return read(var0, new Attribute.Factory());
   }

   public static ClassFile read(Path var0, Attribute.Factory var1) throws IOException, ConstantPoolException {
      InputStream var2 = Files.newInputStream(var0);
      Throwable var3 = null;

      ClassFile var4;
      try {
         var4 = new ClassFile(var2, var1);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               var2.close();
            }
         }

      }

      return var4;
   }

   public static ClassFile read(File var0, Attribute.Factory var1) throws IOException, ConstantPoolException {
      return read(var0.toPath(), var1);
   }

   public static ClassFile read(InputStream var0) throws IOException, ConstantPoolException {
      return new ClassFile(var0, new Attribute.Factory());
   }

   public static ClassFile read(InputStream var0, Attribute.Factory var1) throws IOException, ConstantPoolException {
      return new ClassFile(var0, var1);
   }

   ClassFile(InputStream var1, Attribute.Factory var2) throws IOException, ConstantPoolException {
      ClassReader var3 = new ClassReader(this, var1, var2);
      this.magic = var3.readInt();
      this.minor_version = var3.readUnsignedShort();
      this.major_version = var3.readUnsignedShort();
      this.constant_pool = new ConstantPool(var3);
      this.access_flags = new AccessFlags(var3);
      this.this_class = var3.readUnsignedShort();
      this.super_class = var3.readUnsignedShort();
      int var4 = var3.readUnsignedShort();
      this.interfaces = new int[var4];

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         this.interfaces[var5] = var3.readUnsignedShort();
      }

      var5 = var3.readUnsignedShort();
      this.fields = new Field[var5];

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         this.fields[var6] = new Field(var3);
      }

      var6 = var3.readUnsignedShort();
      this.methods = new Method[var6];

      for(int var7 = 0; var7 < var6; ++var7) {
         this.methods[var7] = new Method(var3);
      }

      this.attributes = new Attributes(var3);
   }

   public ClassFile(int var1, int var2, int var3, ConstantPool var4, AccessFlags var5, int var6, int var7, int[] var8, Field[] var9, Method[] var10, Attributes var11) {
      this.magic = var1;
      this.minor_version = var2;
      this.major_version = var3;
      this.constant_pool = var4;
      this.access_flags = var5;
      this.this_class = var6;
      this.super_class = var7;
      this.interfaces = var8;
      this.fields = var9;
      this.methods = var10;
      this.attributes = var11;
   }

   public String getName() throws ConstantPoolException {
      return this.constant_pool.getClassInfo(this.this_class).getName();
   }

   public String getSuperclassName() throws ConstantPoolException {
      return this.constant_pool.getClassInfo(this.super_class).getName();
   }

   public String getInterfaceName(int var1) throws ConstantPoolException {
      return this.constant_pool.getClassInfo(this.interfaces[var1]).getName();
   }

   public Attribute getAttribute(String var1) {
      return this.attributes.get(var1);
   }

   public boolean isClass() {
      return !this.isInterface();
   }

   public boolean isInterface() {
      return this.access_flags.is(512);
   }

   public int byteLength() {
      return 8 + this.constant_pool.byteLength() + 2 + 2 + 2 + this.byteLength(this.interfaces) + this.byteLength(this.fields) + this.byteLength(this.methods) + this.attributes.byteLength();
   }

   private int byteLength(int[] var1) {
      return 2 + 2 * var1.length;
   }

   private int byteLength(Field[] var1) {
      int var2 = 2;
      Field[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field var6 = var3[var5];
         var2 += var6.byteLength();
      }

      return var2;
   }

   private int byteLength(Method[] var1) {
      int var2 = 2;
      Method[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method var6 = var3[var5];
         var2 += var6.byteLength();
      }

      return var2;
   }
}
