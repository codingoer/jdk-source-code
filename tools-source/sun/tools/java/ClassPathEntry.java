package sun.tools.java;

import java.io.File;
import java.util.Hashtable;
import java.util.zip.ZipFile;

class ClassPathEntry {
   File dir;
   ZipFile zip;
   Hashtable subdirs = new Hashtable(29);

   String[] getFiles(String var1) {
      String[] var2 = (String[])((String[])this.subdirs.get(var1));
      if (var2 == null) {
         File var3 = new File(this.dir.getPath(), var1);
         if (var3.isDirectory()) {
            var2 = var3.list();
            if (var2 == null) {
               var2 = new String[0];
            }

            if (var2.length == 0) {
               String[] var4 = new String[]{""};
               var2 = var4;
            }
         } else {
            var2 = new String[0];
         }

         this.subdirs.put(var1, var2);
      }

      return var2;
   }
}
