package com.sun.tools.example.debug.tty;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

class SourceMapper {
   private final String[] dirs;

   SourceMapper(List var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (!var4.endsWith(".jar") && !var4.endsWith(".zip")) {
            var2.add(var4);
         }
      }

      this.dirs = (String[])var2.toArray(new String[0]);
   }

   SourceMapper(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, File.pathSeparator);
      ArrayList var3 = new ArrayList();

      while(var2.hasMoreTokens()) {
         String var4 = var2.nextToken();
         if (!var4.endsWith(".jar") && !var4.endsWith(".zip")) {
            var3.add(var4);
         }
      }

      this.dirs = (String[])var3.toArray(new String[0]);
   }

   String getSourcePath() {
      int var1 = 0;
      if (this.dirs.length < 1) {
         return "";
      } else {
         StringBuffer var2;
         for(var2 = new StringBuffer(this.dirs[var1++]); var1 < this.dirs.length; ++var1) {
            var2.append(File.pathSeparator);
            var2.append(this.dirs[var1]);
         }

         return var2.toString();
      }
   }

   File sourceFile(Location var1) {
      try {
         String var2 = var1.sourceName();
         String var3 = var1.declaringType().name();
         int var4 = var3.lastIndexOf(46);
         String var5 = var4 >= 0 ? var3.substring(0, var4 + 1) : "";
         String var6 = var5.replace('.', File.separatorChar) + var2;

         for(int var7 = 0; var7 < this.dirs.length; ++var7) {
            File var8 = new File(this.dirs[var7], var6);
            if (var8.exists()) {
               return var8;
            }
         }

         return null;
      } catch (AbsentInformationException var9) {
         return null;
      }
   }

   BufferedReader sourceReader(Location var1) {
      File var2 = this.sourceFile(var1);
      if (var2 == null) {
         return null;
      } else {
         try {
            return new BufferedReader(new FileReader(var2));
         } catch (IOException var4) {
            return null;
         }
      }
   }
}
