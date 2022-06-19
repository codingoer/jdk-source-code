package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.DocumentationTool.Location;

class SimpleDocFileFactory extends DocFileFactory {
   public SimpleDocFileFactory(Configuration var1) {
      super(var1);
   }

   public DocFile createFileForDirectory(String var1) {
      return new SimpleDocFile(new File(var1));
   }

   public DocFile createFileForInput(String var1) {
      return new SimpleDocFile(new File(var1));
   }

   public DocFile createFileForOutput(DocPath var1) {
      return new SimpleDocFile(Location.DOCUMENTATION_OUTPUT, var1);
   }

   Iterable list(JavaFileManager.Location var1, DocPath var2) {
      if (var1 != StandardLocation.SOURCE_PATH) {
         throw new IllegalArgumentException();
      } else {
         LinkedHashSet var3 = new LinkedHashSet();
         String[] var4 = this.configuration.sourcepath.split(File.pathSeparator);
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (!var7.isEmpty()) {
               File var8 = new File(var7);
               if (var8.isDirectory()) {
                  var8 = new File(var8, var2.getPath());
                  if (var8.exists()) {
                     var3.add(new SimpleDocFile(var8));
                  }
               }
            }
         }

         return var3;
      }
   }

   class SimpleDocFile extends DocFile {
      private File file;

      private SimpleDocFile(File var2) {
         super(SimpleDocFileFactory.this.configuration);
         this.file = var2;
      }

      private SimpleDocFile(JavaFileManager.Location var2, DocPath var3) {
         super(SimpleDocFileFactory.this.configuration, var2, var3);
         String var4 = SimpleDocFileFactory.this.configuration.destDirName;
         this.file = var4.isEmpty() ? new File(var3.getPath()) : new File(var4, var3.getPath());
      }

      public InputStream openInputStream() throws FileNotFoundException {
         return new BufferedInputStream(new FileInputStream(this.file));
      }

      public OutputStream openOutputStream() throws IOException, UnsupportedEncodingException {
         if (this.location != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalStateException();
         } else {
            this.createDirectoryForFile(this.file);
            return new BufferedOutputStream(new FileOutputStream(this.file));
         }
      }

      public Writer openWriter() throws IOException, UnsupportedEncodingException {
         if (this.location != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalStateException();
         } else {
            this.createDirectoryForFile(this.file);
            FileOutputStream var1 = new FileOutputStream(this.file);
            return SimpleDocFileFactory.this.configuration.docencoding == null ? new BufferedWriter(new OutputStreamWriter(var1)) : new BufferedWriter(new OutputStreamWriter(var1, SimpleDocFileFactory.this.configuration.docencoding));
         }
      }

      public boolean canRead() {
         return this.file.canRead();
      }

      public boolean canWrite() {
         return this.file.canRead();
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
         if (!(var1 instanceof SimpleDocFile)) {
            return false;
         } else {
            try {
               return this.file.exists() && this.file.getCanonicalFile().equals(((SimpleDocFile)var1).file.getCanonicalFile());
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
            var1.add(SimpleDocFileFactory.this.new SimpleDocFile(var5));
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
         return this.location == null && this.path == null ? SimpleDocFileFactory.this.new SimpleDocFile(new File(this.file, var1)) : SimpleDocFileFactory.this.new SimpleDocFile(this.location, this.path.resolve(var1));
      }

      public DocFile resolveAgainst(JavaFileManager.Location var1) {
         if (var1 != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalArgumentException();
         } else {
            return SimpleDocFileFactory.this.new SimpleDocFile(new File(SimpleDocFileFactory.this.configuration.destDirName, this.file.getPath()));
         }
      }

      private void createDirectoryForFile(File var1) {
         File var2 = var1.getParentFile();
         if (var2 != null && !var2.exists() && !var2.mkdirs()) {
            SimpleDocFileFactory.this.configuration.message.error("doclet.Unable_to_create_directory_0", var2.getPath());
            throw new DocletAbortException("can't create directory");
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("DocFile[");
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

      // $FF: synthetic method
      SimpleDocFile(File var2, Object var3) {
         this(var2);
      }

      // $FF: synthetic method
      SimpleDocFile(JavaFileManager.Location var2, DocPath var3, Object var4) {
         this((JavaFileManager.Location)var2, (DocPath)var3);
      }
   }
}
