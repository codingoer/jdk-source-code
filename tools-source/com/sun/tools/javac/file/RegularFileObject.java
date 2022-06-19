package com.sun.tools.javac.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Iterator;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

class RegularFileObject extends BaseFileObject {
   private boolean hasParents;
   private String name;
   final File file;
   private Reference absFileRef;
   static final boolean isMacOS = System.getProperty("os.name", "").contains("OS X");

   public RegularFileObject(JavacFileManager var1, File var2) {
      this(var1, var2.getName(), var2);
   }

   public RegularFileObject(JavacFileManager var1, String var2, File var3) {
      super(var1);
      this.hasParents = false;
      if (var3.isDirectory()) {
         throw new IllegalArgumentException("directories not supported");
      } else {
         this.name = var2;
         this.file = var3;
      }
   }

   public URI toUri() {
      return this.file.toURI().normalize();
   }

   public String getName() {
      return this.file.getPath();
   }

   public String getShortName() {
      return this.name;
   }

   public JavaFileObject.Kind getKind() {
      return getKind(this.name);
   }

   public InputStream openInputStream() throws IOException {
      return new FileInputStream(this.file);
   }

   public OutputStream openOutputStream() throws IOException {
      this.fileManager.flushCache(this);
      this.ensureParentDirectoriesExist();
      return new FileOutputStream(this.file);
   }

   public CharBuffer getCharContent(boolean var1) throws IOException {
      CharBuffer var2 = this.fileManager.getCachedContent(this);
      if (var2 == null) {
         FileInputStream var3 = new FileInputStream(this.file);

         try {
            ByteBuffer var4 = this.fileManager.makeByteBuffer(var3);
            JavaFileObject var5 = this.fileManager.log.useSource(this);

            try {
               var2 = this.fileManager.decode(var4, var1);
            } finally {
               this.fileManager.log.useSource(var5);
            }

            this.fileManager.recycleByteBuffer(var4);
            if (!var1) {
               this.fileManager.cache(this, var2);
            }
         } finally {
            var3.close();
         }
      }

      return var2;
   }

   public Writer openWriter() throws IOException {
      this.fileManager.flushCache(this);
      this.ensureParentDirectoriesExist();
      return new OutputStreamWriter(new FileOutputStream(this.file), this.fileManager.getEncodingName());
   }

   public long getLastModified() {
      return this.file.lastModified();
   }

   public boolean delete() {
      return this.file.delete();
   }

   protected CharsetDecoder getDecoder(boolean var1) {
      return this.fileManager.getDecoder(this.fileManager.getEncodingName(), var1);
   }

   protected String inferBinaryName(Iterable var1) {
      String var2 = this.file.getPath();
      Iterator var3 = var1.iterator();

      String var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         File var4 = (File)var3.next();
         var5 = var4.getPath();
         if (var5.length() == 0) {
            var5 = System.getProperty("user.dir");
         }

         if (!var5.endsWith(File.separator)) {
            var5 = var5 + File.separator;
         }
      } while(!var2.regionMatches(true, 0, var5, 0, var5.length()) || !(new File(var2.substring(0, var5.length()))).equals(new File(var5)));

      String var6 = var2.substring(var5.length());
      return removeExtension(var6).replace(File.separatorChar, '.');
   }

   public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
      var1.getClass();
      if (var2 == Kind.OTHER && this.getKind() != var2) {
         return false;
      } else {
         String var3 = var1 + var2.extension;
         if (this.name.equals(var3)) {
            return true;
         } else {
            if (isMacOS && Normalizer.isNormalized(this.name, Form.NFD) && Normalizer.isNormalized(var3, Form.NFC)) {
               String var4 = Normalizer.normalize(this.name, Form.NFC);
               if (var4.equals(var3)) {
                  this.name = var4;
                  return true;
               }
            }

            if (this.name.equalsIgnoreCase(var3)) {
               try {
                  return this.file.getCanonicalFile().getName().equals(var3);
               } catch (IOException var5) {
               }
            }

            return false;
         }
      }
   }

   private void ensureParentDirectoriesExist() throws IOException {
      if (!this.hasParents) {
         File var1 = this.file.getParentFile();
         if (var1 != null && !var1.exists() && !var1.mkdirs() && (!var1.exists() || !var1.isDirectory())) {
            throw new IOException("could not create parent directories");
         }

         this.hasParents = true;
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof RegularFileObject)) {
         return false;
      } else {
         RegularFileObject var2 = (RegularFileObject)var1;
         return this.getAbsoluteFile().equals(var2.getAbsoluteFile());
      }
   }

   public int hashCode() {
      return this.getAbsoluteFile().hashCode();
   }

   private File getAbsoluteFile() {
      File var1 = this.absFileRef == null ? null : (File)this.absFileRef.get();
      if (var1 == null) {
         var1 = this.file.getAbsoluteFile();
         this.absFileRef = new SoftReference(var1);
      }

      return var1;
   }
}
