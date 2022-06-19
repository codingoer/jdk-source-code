package com.sun.tools.javac.file;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.List;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.Set;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class ZipFileIndexArchive implements JavacFileManager.Archive {
   private final ZipFileIndex zfIndex;
   private JavacFileManager fileManager;

   public ZipFileIndexArchive(JavacFileManager var1, ZipFileIndex var2) throws IOException {
      this.fileManager = var1;
      this.zfIndex = var2;
   }

   public boolean contains(RelativePath var1) {
      return this.zfIndex.contains(var1);
   }

   public List getFiles(RelativePath.RelativeDirectory var1) {
      return this.zfIndex.getFiles(var1);
   }

   public JavaFileObject getFileObject(RelativePath.RelativeDirectory var1, String var2) {
      RelativePath.RelativeFile var3 = new RelativePath.RelativeFile(var1, var2);
      ZipFileIndex.Entry var4 = this.zfIndex.getZipIndexEntry(var3);
      ZipFileIndexFileObject var5 = new ZipFileIndexFileObject(this.fileManager, this.zfIndex, var4, this.zfIndex.getZipFile());
      return var5;
   }

   public Set getSubdirectories() {
      return this.zfIndex.getAllDirectories();
   }

   public void close() throws IOException {
      this.zfIndex.close();
   }

   public String toString() {
      return "ZipFileIndexArchive[" + this.zfIndex + "]";
   }

   public static class ZipFileIndexFileObject extends BaseFileObject {
      private String name;
      ZipFileIndex zfIndex;
      ZipFileIndex.Entry entry;
      InputStream inputStream = null;
      File zipName;

      ZipFileIndexFileObject(JavacFileManager var1, ZipFileIndex var2, ZipFileIndex.Entry var3, File var4) {
         super(var1);
         this.name = var3.getFileName();
         this.zfIndex = var2;
         this.entry = var3;
         this.zipName = var4;
      }

      public URI toUri() {
         return createJarUri(this.zipName, this.getPrefixedEntryName());
      }

      public String getName() {
         return this.zipName + "(" + this.getPrefixedEntryName() + ")";
      }

      public String getShortName() {
         return this.zipName.getName() + "(" + this.entry.getName() + ")";
      }

      public JavaFileObject.Kind getKind() {
         return getKind(this.entry.getName());
      }

      public InputStream openInputStream() throws IOException {
         if (this.inputStream == null) {
            Assert.checkNonNull(this.entry);
            this.inputStream = new ByteArrayInputStream(this.zfIndex.read(this.entry));
         }

         return this.inputStream;
      }

      public OutputStream openOutputStream() throws IOException {
         throw new UnsupportedOperationException();
      }

      public CharBuffer getCharContent(boolean var1) throws IOException {
         CharBuffer var2 = this.fileManager.getCachedContent(this);
         if (var2 == null) {
            ByteArrayInputStream var3 = new ByteArrayInputStream(this.zfIndex.read(this.entry));

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
         throw new UnsupportedOperationException();
      }

      public long getLastModified() {
         return this.entry.getLastModified();
      }

      public boolean delete() {
         throw new UnsupportedOperationException();
      }

      protected CharsetDecoder getDecoder(boolean var1) {
         return this.fileManager.getDecoder(this.fileManager.getEncodingName(), var1);
      }

      protected String inferBinaryName(Iterable var1) {
         String var2 = this.entry.getName();
         if (this.zfIndex.symbolFilePrefix != null) {
            String var3 = this.zfIndex.symbolFilePrefix.path;
            if (var2.startsWith(var3)) {
               var2 = var2.substring(var3.length());
            }
         }

         return removeExtension(var2).replace('/', '.');
      }

      public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
         var1.getClass();
         return var2 == Kind.OTHER && this.getKind() != var2 ? false : this.name.equals(var1 + var2.extension);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ZipFileIndexFileObject)) {
            return false;
         } else {
            ZipFileIndexFileObject var2 = (ZipFileIndexFileObject)var1;
            return this.zfIndex.getAbsoluteFile().equals(var2.zfIndex.getAbsoluteFile()) && this.name.equals(var2.name);
         }
      }

      public int hashCode() {
         return this.zfIndex.getAbsoluteFile().hashCode() + this.name.hashCode();
      }

      private String getPrefixedEntryName() {
         return this.zfIndex.symbolFilePrefix != null ? this.zfIndex.symbolFilePrefix.path + this.entry.getName() : this.entry.getName();
      }
   }
}
