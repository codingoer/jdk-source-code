package com.sun.tools.javac.nio;

import com.sun.tools.javac.util.BaseFileManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

abstract class PathFileObject implements JavaFileObject {
   private JavacPathFileManager fileManager;
   private Path path;

   static PathFileObject createDirectoryPathFileObject(JavacPathFileManager var0, final Path var1, final Path var2) {
      return new PathFileObject(var0, var1) {
         String inferBinaryName(Iterable var1x) {
            return toBinaryName(var2.relativize(var1));
         }
      };
   }

   static PathFileObject createJarPathFileObject(JavacPathFileManager var0, final Path var1) {
      return new PathFileObject(var0, var1) {
         String inferBinaryName(Iterable var1x) {
            return toBinaryName(var1);
         }
      };
   }

   static PathFileObject createSiblingPathFileObject(JavacPathFileManager var0, Path var1, final String var2) {
      return new PathFileObject(var0, var1) {
         String inferBinaryName(Iterable var1) {
            return toBinaryName(var2, "/");
         }
      };
   }

   static PathFileObject createSimplePathFileObject(JavacPathFileManager var0, final Path var1) {
      return new PathFileObject(var0, var1) {
         String inferBinaryName(Iterable var1x) {
            Path var2 = var1.toAbsolutePath();
            Iterator var3 = var1x.iterator();

            while(var3.hasNext()) {
               Path var4 = (Path)var3.next();
               Path var5 = var4.toAbsolutePath();
               if (var2.startsWith(var5)) {
                  try {
                     Path var6 = var5.relativize(var2);
                     if (var6 != null) {
                        return toBinaryName(var6);
                     }
                  } catch (IllegalArgumentException var7) {
                  }
               }
            }

            return null;
         }
      };
   }

   protected PathFileObject(JavacPathFileManager var1, Path var2) {
      var1.getClass();
      var2.getClass();
      this.fileManager = var1;
      this.path = var2;
   }

   abstract String inferBinaryName(Iterable var1);

   Path getPath() {
      return this.path;
   }

   public JavaFileObject.Kind getKind() {
      return BaseFileManager.getKind(this.path.getFileName().toString());
   }

   public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
      var1.getClass();
      if (var2 == Kind.OTHER && this.getKind() != var2) {
         return false;
      } else {
         String var3 = var1 + var2.extension;
         String var4 = this.path.getFileName().toString();
         if (var4.equals(var3)) {
            return true;
         } else {
            if (var4.equalsIgnoreCase(var3)) {
               try {
                  return this.path.toRealPath(LinkOption.NOFOLLOW_LINKS).getFileName().toString().equals(var3);
               } catch (IOException var6) {
               }
            }

            return false;
         }
      }
   }

   public NestingKind getNestingKind() {
      return null;
   }

   public Modifier getAccessLevel() {
      return null;
   }

   public URI toUri() {
      return this.path.toUri();
   }

   public String getName() {
      return this.path.toString();
   }

   public InputStream openInputStream() throws IOException {
      return Files.newInputStream(this.path);
   }

   public OutputStream openOutputStream() throws IOException {
      this.fileManager.flushCache(this);
      this.ensureParentDirectoriesExist();
      return Files.newOutputStream(this.path);
   }

   public Reader openReader(boolean var1) throws IOException {
      CharsetDecoder var2 = this.fileManager.getDecoder(this.fileManager.getEncodingName(), var1);
      return new InputStreamReader(this.openInputStream(), var2);
   }

   public CharSequence getCharContent(boolean var1) throws IOException {
      CharBuffer var2 = this.fileManager.getCachedContent(this);
      if (var2 == null) {
         InputStream var3 = this.openInputStream();

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
      return new OutputStreamWriter(Files.newOutputStream(this.path), this.fileManager.getEncodingName());
   }

   public long getLastModified() {
      try {
         return Files.getLastModifiedTime(this.path).toMillis();
      } catch (IOException var2) {
         return -1L;
      }
   }

   public boolean delete() {
      try {
         Files.delete(this.path);
         return true;
      } catch (IOException var2) {
         return false;
      }
   }

   public boolean isSameFile(PathFileObject var1) {
      try {
         return Files.isSameFile(this.path, var1.path);
      } catch (IOException var3) {
         return false;
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof PathFileObject && this.path.equals(((PathFileObject)var1).path);
   }

   public int hashCode() {
      return this.path.hashCode();
   }

   public String toString() {
      return this.getClass().getSimpleName() + "[" + this.path + "]";
   }

   private void ensureParentDirectoriesExist() throws IOException {
      Path var1 = this.path.getParent();
      if (var1 != null) {
         Files.createDirectories(var1);
      }

   }

   private long size() {
      try {
         return Files.size(this.path);
      } catch (IOException var2) {
         return -1L;
      }
   }

   protected static String toBinaryName(Path var0) {
      return toBinaryName(var0.toString(), var0.getFileSystem().getSeparator());
   }

   protected static String toBinaryName(String var0, String var1) {
      return removeExtension(var0).replace(var1, ".");
   }

   protected static String removeExtension(String var0) {
      int var1 = var0.lastIndexOf(".");
      return var1 == -1 ? var0 : var0.substring(0, var1);
   }
}
