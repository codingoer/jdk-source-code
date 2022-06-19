package sun.rmi.rmic;

import java.io.File;
import sun.tools.java.Identifier;

public class Util implements Constants {
   public static File getOutputDirectoryFor(Identifier var0, File var1, BatchEnvironment var2) {
      File var3 = null;
      String var4 = var0.getFlatName().toString().replace('.', '$');
      String var6 = null;
      String var7 = var0.getQualifier().toString();
      if (var7.length() > 0) {
         (new StringBuilder()).append(var7).append(".").append(var4).toString();
         var6 = var7.replace('.', File.separatorChar);
      }

      if (var1 != null) {
         if (var6 != null) {
            var3 = new File(var1, var6);
            ensureDirectory(var3, var2);
         } else {
            var3 = var1;
         }
      } else {
         String var8 = System.getProperty("user.dir");
         File var9 = new File(var8);
         if (var6 == null) {
            var3 = var9;
         } else {
            var3 = new File(var9, var6);
            ensureDirectory(var3, var2);
         }
      }

      return var3;
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
}
