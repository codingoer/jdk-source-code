package com.sun.tools.corba.se.idl;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GenFileStream extends PrintWriter {
   private CharArrayWriter charArrayWriter;
   private static CharArrayWriter tmpCharArrayWriter;
   private String name;

   public GenFileStream(String var1) {
      super(tmpCharArrayWriter = new CharArrayWriter());
      this.charArrayWriter = tmpCharArrayWriter;
      this.name = var1;
   }

   public void close() {
      File var1 = new File(this.name);

      try {
         if (this.checkError()) {
            throw new IOException();
         }

         FileWriter var2 = new FileWriter(var1);
         var2.write(this.charArrayWriter.toCharArray());
         var2.close();
      } catch (IOException var4) {
         String[] var3 = new String[]{this.name, var4.toString()};
         System.err.println(Util.getMessage("GenFileStream.1", var3));
      }

      super.close();
   }

   public String name() {
      return this.name;
   }
}
