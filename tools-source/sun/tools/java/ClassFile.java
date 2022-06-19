package sun.tools.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ClassFile {
   private File file;
   private ZipFile zipFile;
   private ZipEntry zipEntry;

   public ClassFile(File var1) {
      this.file = var1;
   }

   public ClassFile(ZipFile var1, ZipEntry var2) {
      this.zipFile = var1;
      this.zipEntry = var2;
   }

   public boolean isZipped() {
      return this.zipFile != null;
   }

   public InputStream getInputStream() throws IOException {
      if (this.file != null) {
         return new FileInputStream(this.file);
      } else {
         try {
            return this.zipFile.getInputStream(this.zipEntry);
         } catch (ZipException var2) {
            throw new IOException(var2.getMessage());
         }
      }
   }

   public boolean exists() {
      return this.file != null ? this.file.exists() : true;
   }

   public boolean isDirectory() {
      return this.file != null ? this.file.isDirectory() : this.zipEntry.getName().endsWith("/");
   }

   public long lastModified() {
      return this.file != null ? this.file.lastModified() : this.zipEntry.getTime();
   }

   public String getPath() {
      return this.file != null ? this.file.getPath() : this.zipFile.getName() + "(" + this.zipEntry.getName() + ")";
   }

   public String getName() {
      return this.file != null ? this.file.getName() : this.zipEntry.getName();
   }

   public String getAbsoluteName() {
      String var1;
      if (this.file != null) {
         try {
            var1 = this.file.getCanonicalPath();
         } catch (IOException var3) {
            var1 = this.file.getAbsolutePath();
         }
      } else {
         var1 = this.zipFile.getName() + "(" + this.zipEntry.getName() + ")";
      }

      return var1;
   }

   public long length() {
      return this.file != null ? this.file.length() : this.zipEntry.getSize();
   }

   public String toString() {
      return this.file != null ? this.file.toString() : this.zipEntry.toString();
   }
}
