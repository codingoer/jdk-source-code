package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.javac.util.Assert;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.DocumentationTool.Location;

class StandardDocFileFactory extends DocFileFactory {
   private final StandardJavaFileManager fileManager;
   private File destDir;

   public StandardDocFileFactory(Configuration var1) {
      super(var1);
      this.fileManager = (StandardJavaFileManager)var1.getFileManager();
   }

   private File getDestDir() {
      if (this.destDir == null) {
         if (!this.configuration.destDirName.isEmpty() || !this.fileManager.hasLocation(Location.DOCUMENTATION_OUTPUT)) {
            try {
               String var1 = this.configuration.destDirName.isEmpty() ? "." : this.configuration.destDirName;
               File var2 = new File(var1);
               this.fileManager.setLocation(Location.DOCUMENTATION_OUTPUT, Arrays.asList(var2));
            } catch (IOException var3) {
               throw new DocletAbortException(var3);
            }
         }

         this.destDir = (File)this.fileManager.getLocation(Location.DOCUMENTATION_OUTPUT).iterator().next();
      }

      return this.destDir;
   }

   public DocFile createFileForDirectory(String var1) {
      return new StandardDocFile(new File(var1));
   }

   public DocFile createFileForInput(String var1) {
      return new StandardDocFile(new File(var1));
   }

   public DocFile createFileForOutput(DocPath var1) {
      return new StandardDocFile(Location.DOCUMENTATION_OUTPUT, var1);
   }

   Iterable list(JavaFileManager.Location var1, DocPath var2) {
      if (var1 != StandardLocation.SOURCE_PATH) {
         throw new IllegalArgumentException();
      } else {
         LinkedHashSet var3 = new LinkedHashSet();
         StandardLocation var4 = this.fileManager.hasLocation(StandardLocation.SOURCE_PATH) ? StandardLocation.SOURCE_PATH : StandardLocation.CLASS_PATH;
         Iterator var5 = this.fileManager.getLocation(var4).iterator();

         while(var5.hasNext()) {
            File var6 = (File)var5.next();
            if (var6.isDirectory()) {
               var6 = new File(var6, var2.getPath());
               if (var6.exists()) {
                  var3.add(new StandardDocFile(var6));
               }
            }
         }

         return var3;
      }
   }

   private static File newFile(File var0, String var1) {
      return var0 == null ? new File(var1) : new File(var0, var1);
   }

   class StandardDocFile extends DocFile {
      private File file;

      private StandardDocFile(File var2) {
         super(StandardDocFileFactory.this.configuration);
         this.file = var2;
      }

      private StandardDocFile(JavaFileManager.Location var2, DocPath var3) {
         super(StandardDocFileFactory.this.configuration, var2, var3);
         Assert.check(var2 == Location.DOCUMENTATION_OUTPUT);
         this.file = StandardDocFileFactory.newFile(StandardDocFileFactory.this.getDestDir(), var3.getPath());
      }

      public InputStream openInputStream() throws IOException {
         JavaFileObject var1 = this.getJavaFileObjectForInput(this.file);
         return new BufferedInputStream(var1.openInputStream());
      }

      public OutputStream openOutputStream() throws IOException, UnsupportedEncodingException {
         if (this.location != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalStateException();
         } else {
            OutputStream var1 = this.getFileObjectForOutput(this.path).openOutputStream();
            return new BufferedOutputStream(var1);
         }
      }

      public Writer openWriter() throws IOException, UnsupportedEncodingException {
         if (this.location != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalStateException();
         } else {
            OutputStream var1 = this.getFileObjectForOutput(this.path).openOutputStream();
            return StandardDocFileFactory.this.configuration.docencoding == null ? new BufferedWriter(new OutputStreamWriter(var1)) : new BufferedWriter(new OutputStreamWriter(var1, StandardDocFileFactory.this.configuration.docencoding));
         }
      }

      public boolean canRead() {
         return this.file.canRead();
      }

      public boolean canWrite() {
         return this.file.canWrite();
      }

      public boolean exists() {
         return this.file.exists();
      }

      public String getName() {
         return this.file.getName();
      }

      public String getPath() {
         return this.file.getPath();
      }

      public boolean isAbsolute() {
         return this.file.isAbsolute();
      }

      public boolean isDirectory() {
         return this.file.isDirectory();
      }

      public boolean isFile() {
         return this.file.isFile();
      }

      public boolean isSameFile(DocFile var1) {
         if (!(var1 instanceof StandardDocFile)) {
            return false;
         } else {
            try {
               return this.file.exists() && this.file.getCanonicalFile().equals(((StandardDocFile)var1).file.getCanonicalFile());
            } catch (IOException var3) {
               return false;
            }
         }
      }

      public Iterable list() {
         ArrayList var1 = new ArrayList();
         File[] var2 = this.file.listFiles();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            File var5 = var2[var4];
            var1.add(StandardDocFileFactory.this.new StandardDocFile(var5));
         }

         return var1;
      }

      public boolean mkdirs() {
         return this.file.mkdirs();
      }

      public DocFile resolve(DocPath var1) {
         return this.resolve(var1.getPath());
      }

      public DocFile resolve(String var1) {
         return this.location == null && this.path == null ? StandardDocFileFactory.this.new StandardDocFile(new File(this.file, var1)) : StandardDocFileFactory.this.new StandardDocFile(this.location, this.path.resolve(var1));
      }

      public DocFile resolveAgainst(JavaFileManager.Location var1) {
         if (var1 != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalArgumentException();
         } else {
            return StandardDocFileFactory.this.new StandardDocFile(StandardDocFileFactory.newFile(StandardDocFileFactory.this.getDestDir(), this.file.getPath()));
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("StandardDocFile[");
         if (this.location != null) {
            var1.append("locn:").append(this.location).append(",");
         }

         if (this.path != null) {
            var1.append("path:").append(this.path.getPath()).append(",");
         }

         var1.append("file:").append(this.file);
         var1.append("]");
         return var1.toString();
      }

      private JavaFileObject getJavaFileObjectForInput(File var1) {
         return (JavaFileObject)StandardDocFileFactory.this.fileManager.getJavaFileObjects(new File[]{var1}).iterator().next();
      }

      private FileObject getFileObjectForOutput(DocPath var1) throws IOException {
         String var2 = var1.getPath();
         int var3 = -1;

         for(int var4 = 0; var4 < var2.length(); ++var4) {
            char var5 = var2.charAt(var4);
            if (var5 == '/') {
               var3 = var4;
            } else if (var4 == var3 + 1 && !Character.isJavaIdentifierStart(var5) || !Character.isJavaIdentifierPart(var5)) {
               break;
            }
         }

         String var6 = var3 == -1 ? "" : var2.substring(0, var3);
         String var7 = var2.substring(var3 + 1);
         return StandardDocFileFactory.this.fileManager.getFileForOutput(this.location, var6, var7, (FileObject)null);
      }

      // $FF: synthetic method
      StandardDocFile(File var2, Object var3) {
         this(var2);
      }

      // $FF: synthetic method
      StandardDocFile(JavaFileManager.Location var2, DocPath var3, Object var4) {
         this((JavaFileManager.Location)var2, (DocPath)var3);
      }
   }
}
