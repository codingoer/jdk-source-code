package com.sun.tools.javac.file;

import com.sun.tools.javac.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class ZipArchive implements JavacFileManager.Archive {
   protected JavacFileManager fileManager;
   protected final Map map;
   protected final ZipFile zfile;
   protected Reference absFileRef;

   public ZipArchive(JavacFileManager var1, ZipFile var2) throws IOException {
      this(var1, var2, true);
   }

   protected ZipArchive(JavacFileManager var1, ZipFile var2, boolean var3) throws IOException {
      this.fileManager = var1;
      this.zfile = var2;
      this.map = new HashMap();
      if (var3) {
         this.initMap();
      }

   }

   protected void initMap() throws IOException {
      ZipEntry var2;
      for(Enumeration var1 = this.zfile.entries(); var1.hasMoreElements(); this.addZipEntry(var2)) {
         try {
            var2 = (ZipEntry)var1.nextElement();
         } catch (InternalError var5) {
            IOException var4 = new IOException();
            var4.initCause(var5);
            throw var4;
         }
      }

   }

   void addZipEntry(ZipEntry var1) {
      String var2 = var1.getName();
      int var3 = var2.lastIndexOf(47);
      RelativePath.RelativeDirectory var4 = new RelativePath.RelativeDirectory(var2.substring(0, var3 + 1));
      String var5 = var2.substring(var3 + 1);
      if (var5.length() != 0) {
         List var6 = (List)this.map.get(var4);
         if (var6 == null) {
            var6 = List.nil();
         }

         var6 = var6.prepend(var5);
         this.map.put(var4, var6);
      }
   }

   public boolean contains(RelativePath var1) {
      RelativePath.RelativeDirectory var2 = var1.dirname();
      String var3 = var1.basename();
      if (var3.length() == 0) {
         return false;
      } else {
         List var4 = (List)this.map.get(var2);
         return var4 != null && var4.contains(var3);
      }
   }

   public List getFiles(RelativePath.RelativeDirectory var1) {
      return (List)this.map.get(var1);
   }

   public JavaFileObject getFileObject(RelativePath.RelativeDirectory var1, String var2) {
      ZipEntry var3 = (new RelativePath.RelativeFile(var1, var2)).getZipEntry(this.zfile);
      return new ZipFileObject(this, var2, var3);
   }

   public Set getSubdirectories() {
      return this.map.keySet();
   }

   public void close() throws IOException {
      this.zfile.close();
   }

   public String toString() {
      return "ZipArchive[" + this.zfile.getName() + "]";
   }

   private File getAbsoluteFile() {
      File var1 = this.absFileRef == null ? null : (File)this.absFileRef.get();
      if (var1 == null) {
         var1 = (new File(this.zfile.getName())).getAbsoluteFile();
         this.absFileRef = new SoftReference(var1);
      }

      return var1;
   }

   public static class ZipFileObject extends BaseFileObject {
      private String name;
      ZipArchive zarch;
      ZipEntry entry;

      protected ZipFileObject(ZipArchive var1, String var2, ZipEntry var3) {
         super(var1.fileManager);
         this.zarch = var1;
         this.name = var2;
         this.entry = var3;
      }

      public URI toUri() {
         File var1 = new File(this.zarch.zfile.getName());
         return createJarUri(var1, this.entry.getName());
      }

      public String getName() {
         return this.zarch.zfile.getName() + "(" + this.entry.getName() + ")";
      }

      public String getShortName() {
         return (new File(this.zarch.zfile.getName())).getName() + "(" + this.entry + ")";
      }

      public JavaFileObject.Kind getKind() {
         return getKind(this.entry.getName());
      }

      public InputStream openInputStream() throws IOException {
         return this.zarch.zfile.getInputStream(this.entry);
      }

      public OutputStream openOutputStream() throws IOException {
         throw new UnsupportedOperationException();
      }

      public CharBuffer getCharContent(boolean var1) throws IOException {
         CharBuffer var2 = this.fileManager.getCachedContent(this);
         if (var2 == null) {
            InputStream var3 = this.zarch.zfile.getInputStream(this.entry);

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
         return this.entry.getTime();
      }

      public boolean delete() {
         throw new UnsupportedOperationException();
      }

      protected CharsetDecoder getDecoder(boolean var1) {
         return this.fileManager.getDecoder(this.fileManager.getEncodingName(), var1);
      }

      protected String inferBinaryName(Iterable var1) {
         String var2 = this.entry.getName();
         return removeExtension(var2).replace('/', '.');
      }

      public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
         var1.getClass();
         return var2 == Kind.OTHER && this.getKind() != var2 ? false : this.name.equals(var1 + var2.extension);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ZipFileObject)) {
            return false;
         } else {
            ZipFileObject var2 = (ZipFileObject)var1;
            return this.zarch.getAbsoluteFile().equals(var2.zarch.getAbsoluteFile()) && this.name.equals(var2.name);
         }
      }

      public int hashCode() {
         return this.zarch.getAbsoluteFile().hashCode() + this.name.hashCode();
      }
   }
}
