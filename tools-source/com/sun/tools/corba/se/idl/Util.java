package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.som.cff.FileLocator;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class Util {
   private static Properties messages = null;
   private static String defaultKey = "default";
   private static Vector msgFiles = new Vector();
   static RepositoryID emptyID;

   public static String getVersion() {
      return getVersion("com/sun/tools/corba/se/idl/idl.prp");
   }

   public static String getVersion(String var0) {
      String var1 = "";
      if (messages == null) {
         Vector var2 = msgFiles;
         if (var0 == null || var0.equals("")) {
            var0 = "com/sun/tools/corba/se/idl/idl.prp";
         }

         var0 = var0.replace('/', File.separatorChar);
         registerMessageFile(var0);
         var1 = getMessage("Version.product", getMessage("Version.number"));
         msgFiles = var2;
         messages = null;
      } else {
         var1 = getMessage("Version.product", getMessage("Version.number"));
      }

      return var1;
   }

   public static boolean isAttribute(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof AttributeEntry;
   }

   public static boolean isConst(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof ConstEntry;
   }

   public static boolean isEnum(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof EnumEntry;
   }

   public static boolean isException(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof ExceptionEntry;
   }

   public static boolean isInterface(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof InterfaceEntry;
   }

   public static boolean isMethod(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof MethodEntry;
   }

   public static boolean isModule(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof ModuleEntry;
   }

   public static boolean isParameter(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof ParameterEntry;
   }

   public static boolean isPrimitive(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      if (var2 == null) {
         int var3 = var0.indexOf(40);
         if (var3 >= 0) {
            var2 = (SymtabEntry)var1.get(var0.substring(0, var3));
         }
      }

      return var2 == null ? false : var2 instanceof PrimitiveEntry;
   }

   public static boolean isSequence(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof SequenceEntry;
   }

   public static boolean isStruct(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof StructEntry;
   }

   public static boolean isString(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof StringEntry;
   }

   public static boolean isTypedef(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof TypedefEntry;
   }

   public static boolean isUnion(String var0, Hashtable var1) {
      SymtabEntry var2 = (SymtabEntry)var1.get(var0);
      return var2 == null ? false : var2 instanceof UnionEntry;
   }

   public static String getMessage(String var0) {
      if (messages == null) {
         readMessages();
      }

      String var1 = messages.getProperty(var0);
      if (var1 == null) {
         var1 = getDefaultMessage(var0);
      }

      return var1;
   }

   public static String getMessage(String var0, String var1) {
      if (messages == null) {
         readMessages();
      }

      String var2 = messages.getProperty(var0);
      if (var2 == null) {
         var2 = getDefaultMessage(var0);
      } else {
         int var3 = var2.indexOf("%0");
         if (var3 >= 0) {
            var2 = var2.substring(0, var3) + var1 + var2.substring(var3 + 2);
         }
      }

      return var2;
   }

   public static String getMessage(String var0, String[] var1) {
      if (messages == null) {
         readMessages();
      }

      String var2 = messages.getProperty(var0);
      if (var2 == null) {
         var2 = getDefaultMessage(var0);
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            int var4 = var2.indexOf("%" + var3);
            if (var4 >= 0) {
               var2 = var2.substring(0, var4) + var1[var3] + var2.substring(var4 + 2);
            }
         }
      }

      return var2;
   }

   private static String getDefaultMessage(String var0) {
      String var1 = messages.getProperty(defaultKey);
      int var2 = var1.indexOf("%0");
      if (var2 > 0) {
         var1 = var1.substring(0, var2) + var0;
      }

      return var1;
   }

   private static void readMessages() {
      messages = new Properties();
      Enumeration var0 = msgFiles.elements();

      while(var0.hasMoreElements()) {
         try {
            DataInputStream var1 = FileLocator.locateLocaleSpecificFileInClassPath((String)var0.nextElement());
            messages.load(var1);
         } catch (IOException var3) {
         }
      }

      if (messages.size() == 0) {
         messages.put(defaultKey, "Error reading Messages File.");
      }

   }

   public static void registerMessageFile(String var0) {
      if (var0 != null) {
         if (messages == null) {
            msgFiles.addElement(var0);
         } else {
            try {
               DataInputStream var1 = FileLocator.locateLocaleSpecificFileInClassPath(var0);
               messages.load(var1);
            } catch (IOException var2) {
            }
         }
      }

   }

   public static String capitalize(String var0) {
      String var1 = new String(var0.substring(0, 1));
      var1 = var1.toUpperCase();
      return var1 + var0.substring(1);
   }

   public static String getAbsolutePath(String var0, Vector var1) throws FileNotFoundException {
      String var2 = null;
      File var3 = new File(var0);
      if (var3.canRead()) {
         var2 = var3.getAbsolutePath();
      } else {
         String var4 = null;

         for(Enumeration var5 = var1.elements(); !var3.canRead() && var5.hasMoreElements(); var3 = new File(var4)) {
            var4 = (String)var5.nextElement() + File.separatorChar + var0;
         }

         if (!var3.canRead()) {
            throw new FileNotFoundException(var0);
         }

         var2 = var3.getPath();
      }

      return var2;
   }

   public static float absDelta(float var0, float var1) {
      double var2 = (double)(var0 - var1);
      return (float)(var2 < 0.0 ? var2 * -1.0 : var2);
   }

   static {
      msgFiles.addElement("com/sun/tools/corba/se/idl/idl.prp");
      emptyID = new RepositoryID();
   }
}
