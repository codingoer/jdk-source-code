package sun.rmi.rmic.iiop;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

public class DirectoryLoader extends ClassLoader {
   private Hashtable cache;
   private File root;

   public DirectoryLoader(File var1) {
      this.cache = new Hashtable();
      if (var1 != null && var1.isDirectory()) {
         this.root = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private DirectoryLoader() {
   }

   public Class loadClass(String var1) throws ClassNotFoundException {
      return this.loadClass(var1, true);
   }

   public synchronized Class loadClass(String var1, boolean var2) throws ClassNotFoundException {
      Class var3 = (Class)this.cache.get(var1);
      if (var3 == null) {
         try {
            var3 = super.findSystemClass(var1);
         } catch (ClassNotFoundException var6) {
            byte[] var4 = this.getClassFileData(var1);
            if (var4 == null) {
               throw new ClassNotFoundException();
            }

            var3 = this.defineClass(var4, 0, var4.length);
            if (var3 == null) {
               throw new ClassFormatError();
            }

            if (var2) {
               this.resolveClass(var3);
            }

            this.cache.put(var1, var3);
         }
      }

      return var3;
   }

   private byte[] getClassFileData(String var1) {
      byte[] var2 = null;
      FileInputStream var3 = null;
      File var4 = new File(this.root, var1.replace('.', File.separatorChar) + ".class");

      try {
         var3 = new FileInputStream(var4);
         var2 = new byte[var3.available()];
         var3.read(var2);
      } catch (ThreadDeath var19) {
         throw var19;
      } catch (Throwable var20) {
      } finally {
         if (var3 != null) {
            try {
               var3.close();
            } catch (ThreadDeath var17) {
               throw var17;
            } catch (Throwable var18) {
            }
         }

      }

      return var2;
   }
}
