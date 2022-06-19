package com.sun.tools.classfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClassReader {
   private DataInputStream in;
   private ClassFile classFile;
   private Attribute.Factory attributeFactory;

   ClassReader(ClassFile var1, InputStream var2, Attribute.Factory var3) throws IOException {
      var1.getClass();
      var3.getClass();
      this.classFile = var1;
      this.in = new DataInputStream(new BufferedInputStream(var2));
      this.attributeFactory = var3;
   }

   ClassFile getClassFile() {
      return this.classFile;
   }

   ConstantPool getConstantPool() {
      return this.classFile.constant_pool;
   }

   public Attribute readAttribute() throws IOException {
      int var1 = this.readUnsignedShort();
      int var2 = this.readInt();
      byte[] var3 = new byte[var2];
      this.readFully(var3);
      DataInputStream var4 = this.in;
      this.in = new DataInputStream(new ByteArrayInputStream(var3));

      Attribute var5;
      try {
         var5 = this.attributeFactory.createAttribute(this, var1, var3);
      } finally {
         this.in = var4;
      }

      return var5;
   }

   public void readFully(byte[] var1) throws IOException {
      this.in.readFully(var1);
   }

   public int readUnsignedByte() throws IOException {
      return this.in.readUnsignedByte();
   }

   public int readUnsignedShort() throws IOException {
      return this.in.readUnsignedShort();
   }

   public int readInt() throws IOException {
      return this.in.readInt();
   }

   public long readLong() throws IOException {
      return this.in.readLong();
   }

   public float readFloat() throws IOException {
      return this.in.readFloat();
   }

   public double readDouble() throws IOException {
      return this.in.readDouble();
   }

   public String readUTF() throws IOException {
      return this.in.readUTF();
   }
}
