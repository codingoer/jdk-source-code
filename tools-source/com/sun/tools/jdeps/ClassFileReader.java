package com.sun.tools.jdeps;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Dependencies;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ClassFileReader {
   protected final Path path;
   protected final String baseFileName;
   protected final List skippedEntries = new ArrayList();

   public static ClassFileReader newInstance(Path var0) throws IOException {
      if (!Files.exists(var0, new LinkOption[0])) {
         throw new FileNotFoundException(var0.toString());
      } else if (Files.isDirectory(var0, new LinkOption[0])) {
         return new DirectoryReader(var0);
      } else {
         return (ClassFileReader)(var0.getFileName().toString().endsWith(".jar") ? new JarFileReader(var0) : new ClassFileReader(var0));
      }
   }

   public static ClassFileReader newInstance(Path var0, JarFile var1) throws IOException {
      return new JarFileReader(var0, var1);
   }

   protected ClassFileReader(Path var1) {
      this.path = var1;
      this.baseFileName = var1.getFileName() != null ? var1.getFileName().toString() : var1.toString();
   }

   public String getFileName() {
      return this.baseFileName;
   }

   public List skippedEntries() {
      return this.skippedEntries;
   }

   public ClassFile getClassFile(String var1) throws IOException {
      if (var1.indexOf(46) > 0) {
         int var2 = var1.lastIndexOf(46);
         String var3 = var1.replace('.', File.separatorChar) + ".class";
         if (this.baseFileName.equals(var3) || this.baseFileName.equals(var3.substring(0, var2) + "$" + var3.substring(var2 + 1, var3.length()))) {
            return this.readClassFile(this.path);
         }
      } else if (this.baseFileName.equals(var1.replace('/', File.separatorChar) + ".class")) {
         return this.readClassFile(this.path);
      }

      return null;
   }

   public Iterable getClassFiles() throws IOException {
      return new Iterable() {
         public Iterator iterator() {
            return ClassFileReader.this.new FileIterator();
         }
      };
   }

   protected ClassFile readClassFile(Path var1) throws IOException {
      InputStream var2 = null;

      ClassFile var3;
      try {
         var2 = Files.newInputStream(var1);
         var3 = ClassFile.read(var2);
      } catch (ConstantPoolException var7) {
         throw new Dependencies.ClassFileError(var7);
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

      return var3;
   }

   public boolean isMultiReleaseJar() throws IOException {
      return false;
   }

   public String toString() {
      return this.path.toString();
   }

   class JarFileIterator implements Iterator {
      protected final JarFileReader reader;
      protected Enumeration entries;
      protected JarFile jf;
      protected JarEntry nextEntry;
      protected ClassFile cf;

      JarFileIterator(JarFileReader var2) {
         this(var2, (JarFile)null);
      }

      JarFileIterator(JarFileReader var2, JarFile var3) {
         this.reader = var2;
         this.setJarFile(var3);
      }

      void setJarFile(JarFile var1) {
         if (var1 != null) {
            this.jf = var1;
            this.entries = this.jf.entries();
            this.nextEntry = this.nextEntry();
         }
      }

      public boolean hasNext() {
         if (this.nextEntry != null && this.cf != null) {
            return true;
         } else {
            while(this.nextEntry != null) {
               try {
                  this.cf = this.reader.readClassFile(this.jf, this.nextEntry);
                  return true;
               } catch (IOException | Dependencies.ClassFileError var2) {
                  ClassFileReader.this.skippedEntries.add(this.nextEntry.getName());
                  this.nextEntry = this.nextEntry();
               }
            }

            return false;
         }
      }

      public ClassFile next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            ClassFile var1 = this.cf;
            this.cf = null;
            this.nextEntry = this.nextEntry();
            return var1;
         }
      }

      protected JarEntry nextEntry() {
         while(true) {
            if (this.entries.hasMoreElements()) {
               JarEntry var1 = (JarEntry)this.entries.nextElement();
               String var2 = var1.getName();
               if (!var2.endsWith(".class")) {
                  continue;
               }

               return var1;
            }

            return null;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   static class JarFileReader extends ClassFileReader {
      private final JarFile jarfile;

      JarFileReader(Path var1) throws IOException {
         this(var1, new JarFile(var1.toFile(), false));
      }

      JarFileReader(Path var1, JarFile var2) throws IOException {
         super(var1);
         this.jarfile = var2;
      }

      public ClassFile getClassFile(String var1) throws IOException {
         if (var1.indexOf(46) > 0) {
            int var2 = var1.lastIndexOf(46);
            String var3 = var1.replace('.', '/') + ".class";
            JarEntry var4 = this.jarfile.getJarEntry(var3);
            if (var4 == null) {
               var4 = this.jarfile.getJarEntry(var3.substring(0, var2) + "$" + var3.substring(var2 + 1, var3.length()));
            }

            if (var4 != null) {
               return this.readClassFile(this.jarfile, var4);
            }
         } else {
            JarEntry var5 = this.jarfile.getJarEntry(var1 + ".class");
            if (var5 != null) {
               return this.readClassFile(this.jarfile, var5);
            }
         }

         return null;
      }

      protected ClassFile readClassFile(JarFile var1, JarEntry var2) throws IOException {
         InputStream var3 = null;

         ClassFile var4;
         try {
            var3 = var1.getInputStream(var2);
            var4 = ClassFile.read(var3);
         } catch (ConstantPoolException var8) {
            throw new Dependencies.ClassFileError(var8);
         } finally {
            if (var3 != null) {
               var3.close();
            }

         }

         return var4;
      }

      public Iterable getClassFiles() throws IOException {
         final JarFileIterator var1 = new JarFileIterator(this, this.jarfile);
         return new Iterable() {
            public Iterator iterator() {
               return var1;
            }
         };
      }

      public boolean isMultiReleaseJar() throws IOException {
         Manifest var1 = this.jarfile.getManifest();
         if (var1 != null) {
            Attributes var2 = var1.getMainAttributes();
            return "true".equalsIgnoreCase(var2.getValue("Multi-Release"));
         } else {
            return false;
         }
      }
   }

   private static class DirectoryReader extends ClassFileReader {
      DirectoryReader(Path var1) throws IOException {
         super(var1);
      }

      public ClassFile getClassFile(String var1) throws IOException {
         if (var1.indexOf(46) > 0) {
            int var2 = var1.lastIndexOf(46);
            String var3 = var1.replace('.', File.separatorChar) + ".class";
            Path var4 = this.path.resolve(var3);
            if (!Files.exists(var4, new LinkOption[0])) {
               var4 = this.path.resolve(var3.substring(0, var2) + "$" + var3.substring(var2 + 1, var3.length()));
            }

            if (Files.exists(var4, new LinkOption[0])) {
               return this.readClassFile(var4);
            }
         } else {
            Path var5 = this.path.resolve(var1 + ".class");
            if (Files.exists(var5, new LinkOption[0])) {
               return this.readClassFile(var5);
            }
         }

         return null;
      }

      public Iterable getClassFiles() throws IOException {
         final DirectoryIterator var1 = new DirectoryIterator();
         return new Iterable() {
            public Iterator iterator() {
               return var1;
            }
         };
      }

      private List walkTree(Path var1) throws IOException {
         final ArrayList var2 = new ArrayList();
         Files.walkFileTree(var1, new SimpleFileVisitor() {
            public FileVisitResult visitFile(Path var1, BasicFileAttributes var2x) throws IOException {
               if (var1.getFileName().toString().endsWith(".class")) {
                  var2.add(var1);
               }

               return FileVisitResult.CONTINUE;
            }
         });
         return var2;
      }

      class DirectoryIterator implements Iterator {
         private List entries;
         private int index = 0;

         DirectoryIterator() throws IOException {
            this.entries = DirectoryReader.this.walkTree(DirectoryReader.this.path);
            this.index = 0;
         }

         public boolean hasNext() {
            return this.index != this.entries.size();
         }

         public ClassFile next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Path var1 = (Path)this.entries.get(this.index++);

               try {
                  return DirectoryReader.this.readClassFile(var1);
               } catch (IOException var3) {
                  throw new Dependencies.ClassFileError(var3);
               }
            }
         }

         public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
         }
      }
   }

   class FileIterator implements Iterator {
      int count = 0;

      public boolean hasNext() {
         return this.count == 0 && ClassFileReader.this.baseFileName.endsWith(".class");
      }

      public ClassFile next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            try {
               ClassFile var1 = ClassFileReader.this.readClassFile(ClassFileReader.this.path);
               ++this.count;
               return var1;
            } catch (IOException var2) {
               throw new Dependencies.ClassFileError(var2);
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
