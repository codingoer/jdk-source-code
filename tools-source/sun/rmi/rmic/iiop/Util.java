package sun.rmi.rmic.iiop;

import com.sun.corba.se.impl.util.PackagePrefixChecker;
import java.io.File;
import sun.tools.java.Identifier;

public final class Util implements sun.rmi.rmic.Constants {
   public static String packagePrefix() {
      return PackagePrefixChecker.packagePrefix();
   }

   private static File getOutputDirectoryFor(Identifier var0, File var1, BatchEnvironment var2, boolean var3) {
      File var4 = null;
      String var5 = var0.getFlatName().toString().replace('.', '$');
      String var7 = null;
      String var8 = var0.getQualifier().toString();
      var8 = correctPackageName(var8, var3, var2.getStandardPackage());
      if (var8.length() > 0) {
         (new StringBuilder()).append(var8).append(".").append(var5).toString();
         var7 = var8.replace('.', File.separatorChar);
      }

      if (var1 != null) {
         if (var7 != null) {
            var4 = new File(var1, var7);
            ensureDirectory(var4, var2);
         } else {
            var4 = var1;
         }
      } else {
         String var9 = System.getProperty("user.dir");
         File var10 = new File(var9);
         if (var7 == null) {
            var4 = var10;
         } else {
            var4 = new File(var10, var7);
            ensureDirectory(var4, var2);
         }
      }

      return var4;
   }

   public static File getOutputDirectoryForIDL(Identifier var0, File var1, BatchEnvironment var2) {
      return getOutputDirectoryFor(var0, var1, var2, true);
   }

   public static File getOutputDirectoryForStub(Identifier var0, File var1, BatchEnvironment var2) {
      return getOutputDirectoryFor(var0, var1, var2, false);
   }

   private static void ensureDirectory(File var0, BatchEnvironment var1) {
      if (!var0.exists()) {
         var0.mkdirs();
         if (!var0.exists()) {
            var1.error(0L, "rmic.cannot.create.dir", var0.getAbsolutePath());
            throw new InternalError();
         }
      }

   }

   public static String correctPackageName(String var0, boolean var1, boolean var2) {
      if (var1) {
         return var0;
      } else {
         return var2 ? var0 : PackagePrefixChecker.correctPackageName(var0);
      }
   }

   public static boolean isOffendingPackage(String var0) {
      return PackagePrefixChecker.isOffendingPackage(var0);
   }

   public static boolean hasOffendingPrefix(String var0) {
      return PackagePrefixChecker.hasOffendingPrefix(var0);
   }
}
