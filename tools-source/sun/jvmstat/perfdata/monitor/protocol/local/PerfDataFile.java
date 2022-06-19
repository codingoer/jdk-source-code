package sun.jvmstat.perfdata.monitor.protocol.local;

import java.io.File;
import java.io.FilenameFilter;
import sun.misc.VMSupport;

public class PerfDataFile {
   public static final String tmpDirName;
   public static final String dirNamePrefix = "hsperfdata_";
   public static final String userDirNamePattern = "hsperfdata_\\S*";
   public static final String fileNamePattern = "^[0-9]+$";
   public static final String tmpFileNamePattern = "^hsperfdata_[0-9]+(_[1-2]+)?$";

   private PerfDataFile() {
   }

   public static File getFile(int var0) {
      if (var0 == 0) {
         return null;
      } else {
         File var1 = new File(tmpDirName);
         String[] var2 = var1.list(new FilenameFilter() {
            public boolean accept(File var1, String var2) {
               if (!var2.startsWith("hsperfdata_")) {
                  return false;
               } else {
                  File var3 = new File(var1, var2);
                  return (var3.isDirectory() || var3.isFile()) && var3.canRead();
               }
            }
         });
         long var3 = 0L;
         File var5 = null;

         for(int var6 = 0; var6 < var2.length; ++var6) {
            File var7 = new File(tmpDirName + var2[var6]);
            File var8 = null;
            if (var7.exists() && var7.isDirectory()) {
               String var9 = Integer.toString(var0);
               var8 = new File(var7.getName(), var9);
            } else if (var7.exists() && var7.isFile()) {
               var8 = var7;
            } else {
               var8 = var7;
            }

            if (var8.exists() && var8.isFile() && var8.canRead()) {
               long var11 = var8.lastModified();
               if (var11 >= var3) {
                  var3 = var11;
                  var5 = var8;
               }
            }
         }

         return var5;
      }
   }

   public static File getFile(String var0, int var1) {
      if (var1 == 0) {
         return null;
      } else {
         String var2 = getTempDirectory(var0) + Integer.toString(var1);
         File var3 = new File(var2);
         if (var3.exists() && var3.isFile() && var3.canRead()) {
            return var3;
         } else {
            long var4 = 0L;
            File var6 = null;

            for(int var7 = 0; var7 < 2; ++var7) {
               if (var7 == 0) {
                  var2 = getTempDirectory() + Integer.toString(var1);
               } else {
                  var2 = getTempDirectory() + Integer.toString(var1) + Integer.toString(var7);
               }

               var3 = new File(var2);
               if (var3.exists() && var3.isFile() && var3.canRead()) {
                  long var8 = var3.lastModified();
                  if (var8 >= var4) {
                     var4 = var8;
                     var6 = var3;
                  }
               }
            }

            return var6;
         }
      }
   }

   public static int getLocalVmId(File var0) {
      try {
         return Integer.parseInt(var0.getName());
      } catch (NumberFormatException var6) {
         String var1 = var0.getName();
         if (var1.startsWith("hsperfdata_")) {
            int var2 = var1.indexOf(95);
            int var3 = var1.lastIndexOf(95);

            try {
               if (var2 == var3) {
                  return Integer.parseInt(var1.substring(var2 + 1));
               }

               return Integer.parseInt(var1.substring(var2 + 1, var3));
            } catch (NumberFormatException var5) {
            }
         }

         throw new IllegalArgumentException("file name does not match pattern");
      }
   }

   public static String getTempDirectory() {
      return tmpDirName;
   }

   public static String getTempDirectory(String var0) {
      return tmpDirName + "hsperfdata_" + var0 + File.separator;
   }

   static {
      String var0 = VMSupport.getVMTemporaryDirectory();
      if (var0.lastIndexOf(File.separator) != var0.length() - 1) {
         var0 = var0 + File.separator;
      }

      tmpDirName = var0;
   }
}
