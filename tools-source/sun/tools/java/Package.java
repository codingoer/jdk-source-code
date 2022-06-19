package sun.tools.java;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class Package {
   ClassPath sourcePath;
   ClassPath binaryPath;
   String pkg;

   public Package(ClassPath var1, Identifier var2) throws IOException {
      this(var1, var1, var2);
   }

   public Package(ClassPath var1, ClassPath var2, Identifier var3) throws IOException {
      if (var3.isInner()) {
         var3 = Identifier.lookup(var3.getQualifier(), var3.getFlatName());
      }

      this.sourcePath = var1;
      this.binaryPath = var2;
      this.pkg = var3.toString().replace('.', File.separatorChar);
   }

   public boolean classExists(Identifier var1) {
      return this.getBinaryFile(var1) != null || !var1.isInner() && this.getSourceFile(var1) != null;
   }

   public boolean exists() {
      ClassFile var1 = this.binaryPath.getDirectory(this.pkg);
      if (var1 != null && var1.isDirectory()) {
         return true;
      } else {
         if (this.sourcePath != this.binaryPath) {
            var1 = this.sourcePath.getDirectory(this.pkg);
            if (var1 != null && var1.isDirectory()) {
               return true;
            }
         }

         String var2 = this.pkg + File.separator;
         return this.binaryPath.getFiles(var2, ".class").hasMoreElements() || this.sourcePath.getFiles(var2, ".java").hasMoreElements();
      }
   }

   private String makeName(String var1) {
      return this.pkg.equals("") ? var1 : this.pkg + File.separator + var1;
   }

   public ClassFile getBinaryFile(Identifier var1) {
      var1 = Type.mangleInnerType(var1);
      String var2 = var1.toString() + ".class";
      return this.binaryPath.getFile(this.makeName(var2));
   }

   public ClassFile getSourceFile(Identifier var1) {
      var1 = var1.getTopName();
      String var2 = var1.toString() + ".java";
      return this.sourcePath.getFile(this.makeName(var2));
   }

   public ClassFile getSourceFile(String var1) {
      return var1.endsWith(".java") ? this.sourcePath.getFile(this.makeName(var1)) : null;
   }

   public Enumeration getSourceFiles() {
      return this.sourcePath.getFiles(this.pkg, ".java");
   }

   public Enumeration getBinaryFiles() {
      return this.binaryPath.getFiles(this.pkg, ".class");
   }

   public String toString() {
      return this.pkg.equals("") ? "unnamed package" : "package " + this.pkg;
   }
}
