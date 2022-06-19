package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.InvalidArgument;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class Arguments extends com.sun.tools.corba.se.idl.Arguments {
   public Hashtable packages = new Hashtable();
   public String separator = null;
   public static final int None = 0;
   public static final int Client = 1;
   public static final int Server = 2;
   public static final int All = 3;
   public int emit = 0;
   public boolean TIEServer = false;
   public boolean POAServer = true;
   public boolean LocalOptimization = false;
   public NameModifier skeletonNameModifier = null;
   public NameModifier tieNameModifier = null;
   public Hashtable packageTranslation = new Hashtable();
   public String targetDir = "";

   public Arguments() {
      this.corbaLevel = 2.4F;
   }

   protected void parseOtherArgs(String[] var1, Properties var2) throws InvalidArgument {
      String var3 = null;
      String var4 = null;
      this.packages.put("CORBA", "org.omg");
      this.packageFromProps(var2);

      try {
         Vector var5 = new Vector();

         for(int var6 = 0; var6 < var1.length; ++var6) {
            String var7 = var1[var6].toLowerCase();
            if (var7.charAt(0) != '-' && var7.charAt(0) != '/') {
               throw new InvalidArgument(var1[var6]);
            }

            if (var7.charAt(0) == '-') {
               var7 = var7.substring(1);
            }

            if (var7.startsWith("f")) {
               if (var7.equals("f")) {
                  StringBuilder var10000 = (new StringBuilder()).append('f');
                  ++var6;
                  var7 = var10000.append(var1[var6].toLowerCase()).toString();
               }

               if (var7.equals("fclient")) {
                  this.emit = this.emit != 2 && this.emit != 3 ? 1 : 3;
               } else if (var7.equals("fserver")) {
                  this.emit = this.emit != 1 && this.emit != 3 ? 2 : 3;
                  this.TIEServer = false;
               } else if (var7.equals("fall")) {
                  this.emit = 3;
                  this.TIEServer = false;
               } else if (var7.equals("fservertie")) {
                  this.emit = this.emit != 1 && this.emit != 3 ? 2 : 3;
                  this.TIEServer = true;
               } else if (var7.equals("falltie")) {
                  this.emit = 3;
                  this.TIEServer = true;
               } else {
                  var6 = this.collectUnknownArg(var1, var6, var5);
               }
            } else {
               String var8;
               String var9;
               if (var7.equals("pkgtranslate")) {
                  if (var6 + 2 >= var1.length) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  ++var6;
                  var8 = var1[var6];
                  ++var6;
                  var9 = var1[var6];
                  this.checkPackageNameValid(var8);
                  this.checkPackageNameValid(var9);
                  if (var8.equals("org") || var8.startsWith("org.omg")) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  var8 = var8.replace('.', '/');
                  var9 = var9.replace('.', '/');
                  this.packageTranslation.put(var8, var9);
               } else if (var7.equals("pkgprefix")) {
                  if (var6 + 2 >= var1.length) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  ++var6;
                  var8 = var1[var6];
                  ++var6;
                  var9 = var1[var6];
                  this.checkPackageNameValid(var8);
                  this.checkPackageNameValid(var9);
                  this.packages.put(var8, var9);
               } else if (var7.equals("td")) {
                  if (var6 + 1 >= var1.length) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  ++var6;
                  var8 = var1[var6];
                  if (var8.charAt(0) == '-') {
                     throw new InvalidArgument(var1[var6 - 1]);
                  }

                  this.targetDir = var8.replace('/', File.separatorChar);
                  if (this.targetDir.charAt(this.targetDir.length() - 1) != File.separatorChar) {
                     this.targetDir = this.targetDir + File.separatorChar;
                  }
               } else if (var7.equals("sep")) {
                  if (var6 + 1 >= var1.length) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  ++var6;
                  this.separator = var1[var6];
               } else if (var7.equals("oldimplbase")) {
                  this.POAServer = false;
               } else if (var7.equals("skeletonname")) {
                  if (var6 + 1 >= var1.length) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  ++var6;
                  var3 = var1[var6];
               } else if (var7.equals("tiename")) {
                  if (var6 + 1 >= var1.length) {
                     throw new InvalidArgument(var1[var6]);
                  }

                  ++var6;
                  var4 = var1[var6];
               } else if (var7.equals("localoptimization")) {
                  this.LocalOptimization = true;
               } else {
                  var6 = this.collectUnknownArg(var1, var6, var5);
               }
            }
         }

         if (var5.size() > 0) {
            String[] var11 = new String[var5.size()];
            var5.copyInto(var11);
            super.parseOtherArgs(var11, var2);
         }

         this.setDefaultEmitter();
         this.setNameModifiers(var3, var4);
      } catch (ArrayIndexOutOfBoundsException var10) {
         throw new InvalidArgument(var1[var1.length - 1]);
      }
   }

   protected int collectUnknownArg(String[] var1, int var2, Vector var3) {
      var3.addElement(var1[var2]);
      ++var2;

      while(var2 < var1.length && var1[var2].charAt(0) != '-' && var1[var2].charAt(0) != '/') {
         var3.addElement(var1[var2++]);
      }

      --var2;
      return var2;
   }

   protected void packageFromProps(Properties var1) throws InvalidArgument {
      Enumeration var2 = var1.propertyNames();

      while(var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         if (var3.startsWith("PkgPrefix.")) {
            String var4 = var3.substring(10);
            String var5 = var1.getProperty(var3);
            this.checkPackageNameValid(var5);
            this.checkPackageNameValid(var4);
            this.packages.put(var4, var5);
         }
      }

   }

   protected void setDefaultEmitter() {
      if (this.emit == 0) {
         this.emit = 1;
      }

   }

   protected void setNameModifiers(String var1, String var2) {
      if (this.emit > 1) {
         String var4;
         if (var1 != null) {
            var4 = var1;
         } else if (this.POAServer) {
            var4 = "%POA";
         } else {
            var4 = "_%ImplBase";
         }

         String var3;
         if (var2 != null) {
            var3 = var2;
         } else if (this.POAServer) {
            var3 = "%POATie";
         } else {
            var3 = "%_Tie";
         }

         this.skeletonNameModifier = new NameModifierImpl(var4);
         this.tieNameModifier = new NameModifierImpl(var3);
      }

   }

   private void checkPackageNameValid(String var1) throws InvalidArgument {
      if (var1.charAt(0) == '.') {
         throw new InvalidArgument(var1);
      } else {
         int var2 = 0;

         while(true) {
            if (var2 >= var1.length()) {
               return;
            }

            if (var1.charAt(var2) == '.') {
               if (var2 == var1.length() - 1) {
                  break;
               }

               ++var2;
               if (!Character.isJavaIdentifierStart(var1.charAt(var2))) {
                  break;
               }
            } else if (!Character.isJavaIdentifierPart(var1.charAt(var2))) {
               throw new InvalidArgument(var1);
            }

            ++var2;
         }

         throw new InvalidArgument(var1);
      }
   }
}
