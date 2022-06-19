package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.som.cff.FileLocator;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class Arguments {
   public String file = null;
   public boolean verbose = false;
   public boolean keepOldFiles = false;
   public boolean emitAll = false;
   public Vector includePaths = new Vector();
   public Hashtable definedSymbols = new Hashtable();
   public boolean cppModule = false;
   public boolean versionRequest = false;
   public float corbaLevel = 2.2F;
   public boolean noWarn = false;
   public boolean scannerDebugFlag = false;
   public boolean tokenDebugFlag = false;

   protected void parseOtherArgs(String[] var1, Properties var2) throws InvalidArgument {
      if (var1.length > 0) {
         throw new InvalidArgument(var1[0]);
      }
   }

   protected void setDebugFlags(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, ",");

      while(var2.hasMoreTokens()) {
         String var3 = var2.nextToken();

         try {
            Field var4 = this.getClass().getField(var3 + "DebugFlag");
            int var5 = var4.getModifiers();
            if (Modifier.isPublic(var5) && !Modifier.isStatic(var5) && var4.getType() == Boolean.TYPE) {
               var4.setBoolean(this, true);
            }
         } catch (Exception var6) {
         }
      }

   }

   void parseArgs(String[] var1) throws InvalidArgument {
      Vector var2 = new Vector();
      boolean var3 = false;

      int var10;
      try {
         for(var10 = 0; var10 < var1.length - 1; ++var10) {
            String var4 = var1[var10].toLowerCase();
            if (var4.charAt(0) != '-' && var4.charAt(0) != '/') {
               throw new InvalidArgument(var1[var10]);
            }

            if (var4.charAt(0) == '-') {
               var4 = var4.substring(1);
            }

            if (var4.equals("i")) {
               ++var10;
               this.includePaths.addElement(var1[var10]);
            } else if (var4.startsWith("i")) {
               this.includePaths.addElement(var1[var10].substring(2));
            } else if (!var4.equals("v") && !var4.equals("verbose")) {
               if (var4.equals("d")) {
                  ++var10;
                  this.definedSymbols.put(var1[var10], "");
               } else if (var4.equals("debug")) {
                  ++var10;
                  this.setDebugFlags(var1[var10]);
               } else if (var4.startsWith("d")) {
                  this.definedSymbols.put(var1[var10].substring(2), "");
               } else if (var4.equals("emitall")) {
                  this.emitAll = true;
               } else if (var4.equals("keep")) {
                  this.keepOldFiles = true;
               } else if (var4.equals("nowarn")) {
                  this.noWarn = true;
               } else if (var4.equals("trace")) {
                  Runtime.getRuntime().traceMethodCalls(true);
               } else if (var4.equals("cppmodule")) {
                  this.cppModule = true;
               } else if (var4.equals("version")) {
                  this.versionRequest = true;
               } else if (var4.equals("corba")) {
                  if (var10 + 1 >= var1.length) {
                     throw new InvalidArgument(var1[var10]);
                  }

                  ++var10;
                  String var5 = var1[var10];
                  if (var5.charAt(0) == '-') {
                     throw new InvalidArgument(var1[var10 - 1]);
                  }

                  try {
                     this.corbaLevel = new Float(var5);
                  } catch (NumberFormatException var8) {
                     throw new InvalidArgument(var1[var10]);
                  }
               } else {
                  var2.addElement(var1[var10]);
                  ++var10;

                  while(var10 < var1.length - 1 && var1[var10].charAt(0) != '-' && var1[var10].charAt(0) != '/') {
                     var2.addElement(var1[var10++]);
                  }

                  --var10;
               }
            } else {
               this.verbose = true;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var9) {
         throw new InvalidArgument(var1[var1.length - 1]);
      }

      if (var10 == var1.length - 1) {
         if (var1[var10].toLowerCase().equals("-version")) {
            this.versionRequest = true;
         } else {
            this.file = var1[var10];
         }

         Properties var11 = new Properties();

         try {
            DataInputStream var12 = FileLocator.locateFileInClassPath("idl.config");
            var11.load(var12);
            this.addIncludePaths(var11);
         } catch (IOException var7) {
         }

         String[] var13;
         if (var2.size() > 0) {
            var13 = new String[var2.size()];
            var2.copyInto(var13);
         } else {
            var13 = new String[0];
         }

         this.parseOtherArgs(var13, var11);
      } else {
         throw new InvalidArgument();
      }
   }

   private void addIncludePaths(Properties var1) {
      String var2 = var1.getProperty("includes");
      if (var2 != null) {
         String var3 = System.getProperty("path.separator");
         int var4 = -var3.length();

         do {
            var2 = var2.substring(var4 + var3.length());
            var4 = var2.indexOf(var3);
            if (var4 < 0) {
               var4 = var2.length();
            }

            this.includePaths.addElement(var2.substring(0, var4));
         } while(var4 != var2.length());
      }

   }
}
