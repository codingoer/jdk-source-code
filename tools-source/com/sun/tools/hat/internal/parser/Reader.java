package com.sun.tools.hat.internal.parser;

import com.sun.tools.hat.internal.model.Snapshot;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class Reader {
   protected PositionDataInputStream in;

   protected Reader(PositionDataInputStream var1) {
      this.in = var1;
   }

   public abstract Snapshot read() throws IOException;

   public static Snapshot readFile(String var0, boolean var1, int var2) throws IOException {
      int var3 = 1;
      int var4 = var0.lastIndexOf(35);
      if (var4 > -1) {
         String var5 = var0.substring(var4 + 1, var0.length());

         try {
            var3 = Integer.parseInt(var5, 10);
         } catch (NumberFormatException var12) {
            String var7 = "In file name \"" + var0 + "\", a dump number was expected after the :, but \"" + var5 + "\" was found instead.";
            System.err.println(var7);
            throw new IOException(var7);
         }

         var0 = var0.substring(0, var4);
      }

      PositionDataInputStream var14 = new PositionDataInputStream(new BufferedInputStream(new FileInputStream(var0)));

      Snapshot var8;
      try {
         int var6 = var14.readInt();
         if (var6 != 1245795905) {
            throw new IOException("Unrecognized magic number: " + var6);
         }

         HprofReader var15 = new HprofReader(var0, var14, var3, var1, var2);
         var8 = var15.read();
      } finally {
         var14.close();
      }

      return var8;
   }
}
