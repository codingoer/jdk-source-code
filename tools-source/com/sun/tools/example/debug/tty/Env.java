package com.sun.tools.example.debug.tty;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

class Env {
   static EventRequestSpecList specList = new EventRequestSpecList();
   private static VMConnection connection;
   private static SourceMapper sourceMapper = new SourceMapper("");
   private static List excludes;
   private static final int SOURCE_CACHE_SIZE = 5;
   private static List sourceCache = new LinkedList();
   private static HashMap savedValues = new HashMap();
   private static Method atExitMethod;

   static void init(String var0, boolean var1, int var2) {
      connection = new VMConnection(var0, var2);
      if (!connection.isLaunch() || var1) {
         connection.open();
      }

   }

   static VMConnection connection() {
      return connection;
   }

   static VirtualMachine vm() {
      return connection.vm();
   }

   static void shutdown() {
      shutdown((String)null);
   }

   static void shutdown(String var0) {
      if (connection != null) {
         try {
            connection.disposeVM();
         } catch (VMDisconnectedException var2) {
         }
      }

      if (var0 != null) {
         MessageOutput.lnprint(var0);
         MessageOutput.println();
      }

      System.exit(0);
   }

   static void setSourcePath(String var0) {
      sourceMapper = new SourceMapper(var0);
      sourceCache.clear();
   }

   static void setSourcePath(List var0) {
      sourceMapper = new SourceMapper(var0);
      sourceCache.clear();
   }

   static String getSourcePath() {
      return sourceMapper.getSourcePath();
   }

   private static List excludes() {
      if (excludes == null) {
         setExcludes("java.*, javax.*, sun.*, com.sun.*");
      }

      return excludes;
   }

   static String excludesString() {
      StringBuffer var0 = new StringBuffer();
      Iterator var1 = excludes().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         var0.append(var2);
         var0.append(",");
      }

      return var0.toString();
   }

   static void addExcludes(StepRequest var0) {
      Iterator var1 = excludes().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         var0.addClassExclusionFilter(var2);
      }

   }

   static void addExcludes(MethodEntryRequest var0) {
      Iterator var1 = excludes().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         var0.addClassExclusionFilter(var2);
      }

   }

   static void addExcludes(MethodExitRequest var0) {
      Iterator var1 = excludes().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         var0.addClassExclusionFilter(var2);
      }

   }

   static void setExcludes(String var0) {
      StringTokenizer var1 = new StringTokenizer(var0, " ,;");
      ArrayList var2 = new ArrayList();

      while(var1.hasMoreTokens()) {
         var2.add(var1.nextToken());
      }

      excludes = var2;
   }

   static Method atExitMethod() {
      return atExitMethod;
   }

   static void setAtExitMethod(Method var0) {
      atExitMethod = var0;
   }

   static BufferedReader sourceReader(Location var0) {
      return sourceMapper.sourceReader(var0);
   }

   static synchronized String sourceLine(Location var0, int var1) throws IOException {
      if (var1 == -1) {
         throw new IllegalArgumentException();
      } else {
         try {
            String var2 = var0.sourceName();
            Iterator var3 = sourceCache.iterator();
            SourceCode var4 = null;

            while(var3.hasNext()) {
               SourceCode var5 = (SourceCode)var3.next();
               if (var5.fileName().equals(var2)) {
                  var4 = var5;
                  var3.remove();
                  break;
               }
            }

            if (var4 == null) {
               BufferedReader var7 = sourceReader(var0);
               if (var7 == null) {
                  throw new FileNotFoundException(var2);
               }

               var4 = new SourceCode(var2, var7);
               if (sourceCache.size() == 5) {
                  sourceCache.remove(sourceCache.size() - 1);
               }
            }

            sourceCache.add(0, var4);
            return var4.sourceLine(var1);
         } catch (AbsentInformationException var6) {
            throw new IllegalArgumentException();
         }
      }
   }

   static String description(ObjectReference var0) {
      ReferenceType var1 = var0.referenceType();
      long var2 = var0.uniqueID();
      return var1 == null ? toHex(var2) : MessageOutput.format("object description and hex id", new Object[]{var1.name(), toHex(var2)});
   }

   static String toHex(long var0) {
      char[] var2 = new char[16];
      char[] var3 = new char[18];
      int var4 = 0;

      do {
         long var5 = var0 & 15L;
         var2[var4++] = (char)((int)(var5 < 10L ? 48L + var5 : 97L + var5 - 10L));
      } while((var0 >>>= 4) > 0L);

      var3[0] = '0';
      var3[1] = 'x';
      int var7 = 2;

      while(true) {
         --var4;
         if (var4 < 0) {
            return new String(var3, 0, var7);
         }

         var3[var7++] = var2[var4];
      }
   }

   static long fromHex(String var0) {
      String var1 = var0.startsWith("0x") ? var0.substring(2).toLowerCase() : var0.toLowerCase();
      if (var0.length() == 0) {
         throw new NumberFormatException();
      } else {
         long var2 = 0L;

         for(int var4 = 0; var4 < var1.length(); ++var4) {
            char var5 = var1.charAt(var4);
            if (var5 >= '0' && var5 <= '9') {
               var2 = var2 * 16L + (long)(var5 - 48);
            } else {
               if (var5 < 'a' || var5 > 'f') {
                  throw new NumberFormatException();
               }

               var2 = var2 * 16L + (long)(var5 - 97 + 10);
            }
         }

         return var2;
      }
   }

   static ReferenceType getReferenceTypeFromToken(String var0) {
      ReferenceType var1 = null;
      if (Character.isDigit(var0.charAt(0))) {
         var1 = null;
      } else if (var0.startsWith("*.")) {
         var0 = var0.substring(1);
         Iterator var2 = vm().allClasses().iterator();

         while(var2.hasNext()) {
            ReferenceType var3 = (ReferenceType)var2.next();
            if (var3.name().endsWith(var0)) {
               var1 = var3;
               break;
            }
         }
      } else {
         List var4 = vm().classesByName(var0);
         if (var4.size() > 0) {
            var1 = (ReferenceType)var4.get(0);
         }
      }

      return var1;
   }

   static Set getSaveKeys() {
      return savedValues.keySet();
   }

   static Value getSavedValue(String var0) {
      return (Value)savedValues.get(var0);
   }

   static void setSavedValue(String var0, Value var1) {
      savedValues.put(var0, var1);
   }

   static class SourceCode {
      private String fileName;
      private List sourceLines = new ArrayList();

      SourceCode(String var1, BufferedReader var2) throws IOException {
         this.fileName = var1;

         try {
            for(String var3 = var2.readLine(); var3 != null; var3 = var2.readLine()) {
               this.sourceLines.add(var3);
            }
         } finally {
            var2.close();
         }

      }

      String fileName() {
         return this.fileName;
      }

      String sourceLine(int var1) {
         int var2 = var1 - 1;
         return var2 >= this.sourceLines.size() ? null : (String)this.sourceLines.get(var2);
      }
   }
}
