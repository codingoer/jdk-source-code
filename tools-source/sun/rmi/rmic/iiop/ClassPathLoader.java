package sun.rmi.rmic.iiop;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassPath;

public class ClassPathLoader extends ClassLoader {
   private ClassPath classPath;

   public ClassPathLoader(ClassPath var1) {
      this.classPath = var1;
   }

   protected Class findClass(String var1) throws ClassNotFoundException {
      byte[] var2 = this.loadClassData(var1);
      return this.defineClass(var1, var2, 0, var2.length);
   }

   private byte[] loadClassData(String var1) throws ClassNotFoundException {
      String var2 = var1.replace('.', File.separatorChar) + ".class";
      ClassFile var3 = this.classPath.getFile(var2);
      if (var3 != null) {
         IOException var4 = null;
         byte[] var5 = null;

         try {
            DataInputStream var6 = new DataInputStream(var3.getInputStream());
            var5 = new byte[(int)var3.length()];

            try {
               var6.readFully(var5);
            } catch (IOException var17) {
               var5 = null;
               var4 = var17;
            } finally {
               try {
                  var6.close();
               } catch (IOException var16) {
               }

            }
         } catch (IOException var19) {
            var4 = var19;
         }

         if (var5 == null) {
            throw new ClassNotFoundException(var1, var4);
         } else {
            return var5;
         }
      } else {
         throw new ClassNotFoundException(var1);
      }
   }
}
