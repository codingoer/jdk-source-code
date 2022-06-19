package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.javac.nio.PathFileManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.DocumentationTool.Location;

class PathDocFileFactory extends DocFileFactory {
   private final PathFileManager fileManager;
   private final Path destDir;

   public PathDocFileFactory(Configuration var1) {
      super(var1);
      this.fileManager = (PathFileManager)var1.getFileManager();
      if (!var1.destDirName.isEmpty() || !this.fileManager.hasLocation(Location.DOCUMENTATION_OUTPUT)) {
         try {
            String var2 = var1.destDirName.isEmpty() ? "." : var1.destDirName;
            Path var3 = this.fileManager.getDefaultFileSystem().getPath(var2);
            this.fileManager.setLocation(Location.DOCUMENTATION_OUTPUT, Arrays.asList(var3));
         } catch (IOException var4) {
            throw new DocletAbortException(var4);
         }
      }

      this.destDir = (Path)this.fileManager.getLocation(Location.DOCUMENTATION_OUTPUT).iterator().next();
   }

   public DocFile createFileForDirectory(String var1) {
      return new StandardDocFile(this.fileManager.getDefaultFileSystem().getPath(var1));
   }

   public DocFile createFileForInput(String var1) {
      return new StandardDocFile(this.fileManager.getDefaultFileSystem().getPath(var1));
   }

   public DocFile createFileForOutput(DocPath var1) {
      return new StandardDocFile(Location.DOCUMENTATION_OUTPUT, var1);
   }

   Iterable list(JavaFileManager.Location var1, DocPath var2) {
      if (var1 != StandardLocation.SOURCE_PATH) {
         throw new IllegalArgumentException();
      } else {
         LinkedHashSet var3 = new LinkedHashSet();
         if (this.fileManager.hasLocation(var1)) {
            Iterator var4 = this.fileManager.getLocation(var1).iterator();

            while(var4.hasNext()) {
               Path var5 = (Path)var4.next();
               if (Files.isDirectory(var5, new LinkOption[0])) {
                  var5 = var5.resolve(var2.getPath());
                  if (Files.exists(var5, new LinkOption[0])) {
                     var3.add(new StandardDocFile(var5));
                  }
               }
            }
         }

         return var3;
      }
   }

   class StandardDocFile extends DocFile {
      private Path file;

      private StandardDocFile(Path var2) {
         super(PathDocFileFactory.this.configuration);
         this.file = var2;
      }

      private StandardDocFile(JavaFileManager.Location var2, DocPath var3) {
         super(PathDocFileFactory.this.configuration, var2, var3);
         this.file = PathDocFileFactory.this.destDir.resolve(var3.getPath());
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
            return PathDocFileFactory.this.configuration.docencoding == null ? new BufferedWriter(new OutputStreamWriter(var1)) : new BufferedWriter(new OutputStreamWriter(var1, PathDocFileFactory.this.configuration.docencoding));
         }
      }

      public boolean canRead() {
         return Files.isReadable(this.file);
      }

      public boolean canWrite() {
         return Files.isWritable(this.file);
      }

      public boolean exists() {
         return Files.exists(this.file, new LinkOption[0]);
      }

      public String getName() {
         return this.file.getFileName().toString();
      }

      public String getPath() {
         return this.file.toString();
      }

      public boolean isAbsolute() {
         return this.file.isAbsolute();
      }

      public boolean isDirectory() {
         return Files.isDirectory(this.file, new LinkOption[0]);
      }

      public boolean isFile() {
         return Files.isRegularFile(this.file, new LinkOption[0]);
      }

      public boolean isSameFile(DocFile var1) {
         if (!(var1 instanceof StandardDocFile)) {
            return false;
         } else {
            try {
               return Files.isSameFile(this.file, ((StandardDocFile)var1).file);
            } catch (IOException var3) {
               return false;
            }
         }
      }

      public Iterable list() throws IOException {
         ArrayList var1 = new ArrayList();
         DirectoryStream var2 = Files.newDirectoryStream(this.file);
         Throwable var3 = null;

         try {
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               Path var5 = (Path)var4.next();
               var1.add(PathDocFileFactory.this.new StandardDocFile(var5));
            }
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var1;
      }

      public boolean mkdirs() {
         try {
            Files.createDirectories(this.file);
            return true;
         } catch (IOException var2) {
            return false;
         }
      }

      public DocFile resolve(DocPath var1) {
         return this.resolve(var1.getPath());
      }

      public DocFile resolve(String var1) {
         return this.location == null && this.path == null ? PathDocFileFactory.this.new StandardDocFile(this.file.resolve(var1)) : PathDocFileFactory.this.new StandardDocFile(this.location, this.path.resolve(var1));
      }

      public DocFile resolveAgainst(JavaFileManager.Location var1) {
         if (var1 != Location.DOCUMENTATION_OUTPUT) {
            throw new IllegalArgumentException();
         } else {
            return PathDocFileFactory.this.new StandardDocFile(PathDocFileFactory.this.destDir.resolve(this.file));
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("PathDocFile[");
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

      private JavaFileObject getJavaFileObjectForInput(Path var1) {
         return (JavaFileObject)PathDocFileFactory.this.fileManager.getJavaFileObjects(var1).iterator().next();
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
         return PathDocFileFactory.this.fileManager.getFileForOutput(this.location, var6, var7, (FileObject)null);
      }

      // $FF: synthetic method
      StandardDocFile(Path var2, Object var3) {
         this(var2);
      }

      // $FF: synthetic method
      StandardDocFile(JavaFileManager.Location var2, DocPath var3, Object var4) {
         this((JavaFileManager.Location)var2, (DocPath)var3);
      }
   }
}
