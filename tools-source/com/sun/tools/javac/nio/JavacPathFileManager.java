package com.sun.tools.javac.nio;

import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.BaseFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class JavacPathFileManager extends BaseFileManager implements PathFileManager {
   protected FileSystem defaultFileSystem;
   private boolean inited = false;
   private Map pathsForLocation;
   private Map fileSystems;

   public JavacPathFileManager(Context var1, boolean var2, Charset var3) {
      super(var3);
      if (var2) {
         var1.put((Class)JavaFileManager.class, (Object)this);
      }

      this.pathsForLocation = new HashMap();
      this.fileSystems = new HashMap();
      this.setContext(var1);
   }

   public void setContext(Context var1) {
      super.setContext(var1);
   }

   public FileSystem getDefaultFileSystem() {
      if (this.defaultFileSystem == null) {
         this.defaultFileSystem = FileSystems.getDefault();
      }

      return this.defaultFileSystem;
   }

   public void setDefaultFileSystem(FileSystem var1) {
      this.defaultFileSystem = var1;
   }

   public void flush() throws IOException {
      this.contentCache.clear();
   }

   public void close() throws IOException {
      Iterator var1 = this.fileSystems.values().iterator();

      while(var1.hasNext()) {
         FileSystem var2 = (FileSystem)var1.next();
         var2.close();
      }

   }

   public ClassLoader getClassLoader(JavaFileManager.Location var1) {
      nullCheck(var1);
      Iterable var2 = this.getLocation(var1);
      if (var2 == null) {
         return null;
      } else {
         ListBuffer var3 = new ListBuffer();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Path var5 = (Path)var4.next();

            try {
               var3.append(var5.toUri().toURL());
            } catch (MalformedURLException var7) {
               throw new AssertionError(var7);
            }
         }

         return this.getClassLoader((URL[])var3.toArray(new URL[var3.size()]));
      }
   }

   public boolean isDefaultBootClassPath() {
      return this.locations.isDefaultBootClassPath();
   }

   public boolean hasLocation(JavaFileManager.Location var1) {
      return this.getLocation(var1) != null;
   }

   public Iterable getLocation(JavaFileManager.Location var1) {
      nullCheck(var1);
      this.lazyInitSearchPaths();
      PathsForLocation var2 = (PathsForLocation)this.pathsForLocation.get(var1);
      if (var2 == null && !this.pathsForLocation.containsKey(var1)) {
         this.setDefaultForLocation(var1);
         var2 = (PathsForLocation)this.pathsForLocation.get(var1);
      }

      return var2;
   }

   private Path getOutputLocation(JavaFileManager.Location var1) {
      Iterable var2 = this.getLocation(var1);
      return var2 == null ? null : (Path)var2.iterator().next();
   }

   public void setLocation(JavaFileManager.Location var1, Iterable var2) throws IOException {
      nullCheck(var1);
      this.lazyInitSearchPaths();
      if (var2 == null) {
         this.setDefaultForLocation(var1);
      } else {
         if (var1.isOutputLocation()) {
            this.checkOutputPath(var2);
         }

         PathsForLocation var3 = new PathsForLocation();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Path var5 = (Path)var4.next();
            var3.add(var5);
         }

         this.pathsForLocation.put(var1, var3);
      }

   }

   private void checkOutputPath(Iterable var1) throws IOException {
      Iterator var2 = var1.iterator();
      if (!var2.hasNext()) {
         throw new IllegalArgumentException("empty path for directory");
      } else {
         Path var3 = (Path)var2.next();
         if (var2.hasNext()) {
            throw new IllegalArgumentException("path too long for directory");
         } else if (!isDirectory(var3)) {
            throw new IOException(var3 + ": not a directory");
         }
      }
   }

   private void setDefaultForLocation(JavaFileManager.Location var1) {
      Object var2 = null;
      if (var1 instanceof StandardLocation) {
         String var3;
         switch ((StandardLocation)var1) {
            case CLASS_PATH:
               var2 = this.locations.userClassPath();
               break;
            case PLATFORM_CLASS_PATH:
               var2 = this.locations.bootClassPath();
               break;
            case SOURCE_PATH:
               var2 = this.locations.sourcePath();
               break;
            case CLASS_OUTPUT:
               var3 = this.options.get(Option.D);
               var2 = var3 == null ? null : Collections.singleton(new File(var3));
               break;
            case SOURCE_OUTPUT:
               var3 = this.options.get(Option.S);
               var2 = var3 == null ? null : Collections.singleton(new File(var3));
         }
      }

      PathsForLocation var6 = new PathsForLocation();
      if (var2 != null) {
         Iterator var4 = ((Collection)var2).iterator();

         while(var4.hasNext()) {
            File var5 = (File)var4.next();
            var6.add(var5.toPath());
         }
      }

      if (!var6.isEmpty()) {
         this.pathsForLocation.put(var1, var6);
      }

   }

   private void lazyInitSearchPaths() {
      if (!this.inited) {
         this.setDefaultForLocation(StandardLocation.PLATFORM_CLASS_PATH);
         this.setDefaultForLocation(StandardLocation.CLASS_PATH);
         this.setDefaultForLocation(StandardLocation.SOURCE_PATH);
         this.inited = true;
      }

   }

   public Path getPath(FileObject var1) {
      nullCheck(var1);
      if (!(var1 instanceof PathFileObject)) {
         throw new IllegalArgumentException();
      } else {
         return ((PathFileObject)var1).getPath();
      }
   }

   public boolean isSameFile(FileObject var1, FileObject var2) {
      nullCheck(var1);
      nullCheck(var2);
      if (!(var1 instanceof PathFileObject)) {
         throw new IllegalArgumentException("Not supported: " + var1);
      } else if (!(var2 instanceof PathFileObject)) {
         throw new IllegalArgumentException("Not supported: " + var2);
      } else {
         return ((PathFileObject)var1).isSameFile((PathFileObject)var2);
      }
   }

   public Iterable list(JavaFileManager.Location var1, String var2, Set var3, boolean var4) throws IOException {
      nullCheck(var2);
      nullCheck(var3);
      Iterable var5 = this.getLocation(var1);
      if (var5 == null) {
         return List.nil();
      } else {
         ListBuffer var6 = new ListBuffer();
         Iterator var7 = var5.iterator();

         while(var7.hasNext()) {
            Path var8 = (Path)var7.next();
            this.list(var8, var2, var3, var4, var6);
         }

         return var6.toList();
      }
   }

   private void list(Path var1, String var2, final Set var3, boolean var4, final ListBuffer var5) throws IOException {
      if (Files.exists(var1, new LinkOption[0])) {
         final Path var6;
         if (isDirectory(var1)) {
            var6 = var1;
         } else {
            FileSystem var7 = this.getFileSystem(var1);
            if (var7 == null) {
               return;
            }

            var6 = (Path)var7.getRootDirectories().iterator().next();
         }

         String var11 = var1.getFileSystem().getSeparator();
         Path var8 = var2.isEmpty() ? var6 : var6.resolve(var2.replace(".", var11));
         if (Files.exists(var8, new LinkOption[0])) {
            int var9 = var4 ? Integer.MAX_VALUE : 1;
            EnumSet var10 = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            Files.walkFileTree(var8, var10, var9, new SimpleFileVisitor() {
               public FileVisitResult preVisitDirectory(Path var1, BasicFileAttributes var2) {
                  Path var3x = var1.getFileName();
                  return var3x != null && !SourceVersion.isIdentifier(var3x.toString()) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
               }

               public FileVisitResult visitFile(Path var1, BasicFileAttributes var2) {
                  if (var2.isRegularFile() && var3.contains(BaseFileManager.getKind(var1.getFileName().toString()))) {
                     PathFileObject var3x = PathFileObject.createDirectoryPathFileObject(JavacPathFileManager.this, var1, var6);
                     var5.append(var3x);
                  }

                  return FileVisitResult.CONTINUE;
               }
            });
         }
      }
   }

   public Iterable getJavaFileObjectsFromPaths(Iterable var1) {
      ArrayList var2;
      if (var1 instanceof Collection) {
         var2 = new ArrayList(((Collection)var1).size());
      } else {
         var2 = new ArrayList();
      }

      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Path var4 = (Path)var3.next();
         var2.add(PathFileObject.createSimplePathFileObject(this, (Path)nullCheck(var4)));
      }

      return var2;
   }

   public Iterable getJavaFileObjects(Path... var1) {
      return this.getJavaFileObjectsFromPaths(Arrays.asList((Object[])nullCheck(var1)));
   }

   public JavaFileObject getJavaFileForInput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3) throws IOException {
      return this.getFileForInput(var1, getRelativePath(var2, var3));
   }

   public FileObject getFileForInput(JavaFileManager.Location var1, String var2, String var3) throws IOException {
      return this.getFileForInput(var1, getRelativePath(var2, var3));
   }

   private JavaFileObject getFileForInput(JavaFileManager.Location var1, String var2) throws IOException {
      Iterator var3 = this.getLocation(var1).iterator();

      while(var3.hasNext()) {
         Path var4 = (Path)var3.next();
         if (isDirectory(var4)) {
            Path var5 = resolve(var4, var2);
            if (Files.exists(var5, new LinkOption[0])) {
               return PathFileObject.createDirectoryPathFileObject(this, var5, var4);
            }
         } else {
            FileSystem var7 = this.getFileSystem(var4);
            if (var7 != null) {
               Path var6 = getPath(var7, var2);
               if (Files.exists(var6, new LinkOption[0])) {
                  return PathFileObject.createJarPathFileObject(this, var6);
               }
            }
         }
      }

      return null;
   }

   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException {
      return this.getFileForOutput(var1, getRelativePath(var2, var3), var4);
   }

   public FileObject getFileForOutput(JavaFileManager.Location var1, String var2, String var3, FileObject var4) throws IOException {
      return this.getFileForOutput(var1, getRelativePath(var2, var3), var4);
   }

   private JavaFileObject getFileForOutput(JavaFileManager.Location var1, String var2, FileObject var3) {
      Path var4 = this.getOutputLocation(var1);
      Path var5;
      if (var4 == null) {
         if (var1 == StandardLocation.CLASS_OUTPUT) {
            var5 = null;
            if (var3 != null && var3 instanceof PathFileObject) {
               var5 = ((PathFileObject)var3).getPath().getParent();
            }

            return PathFileObject.createSiblingPathFileObject(this, var5.resolve(getBaseName(var2)), var2);
         }

         if (var1 == StandardLocation.SOURCE_OUTPUT) {
            var4 = this.getOutputLocation(StandardLocation.CLASS_OUTPUT);
         }
      }

      if (var4 != null) {
         var5 = resolve(var4, var2);
         return PathFileObject.createDirectoryPathFileObject(this, var5, var4);
      } else {
         var5 = getPath(this.getDefaultFileSystem(), var2);
         return PathFileObject.createSimplePathFileObject(this, var5);
      }
   }

   public String inferBinaryName(JavaFileManager.Location var1, JavaFileObject var2) {
      nullCheck(var2);
      Iterable var3 = this.getLocation(var1);
      if (var3 == null) {
         return null;
      } else if (!(var2 instanceof PathFileObject)) {
         throw new IllegalArgumentException(var2.getClass().getName());
      } else {
         return ((PathFileObject)var2).inferBinaryName(var3);
      }
   }

   private FileSystem getFileSystem(Path var1) throws IOException {
      FileSystem var2 = (FileSystem)this.fileSystems.get(var1);
      if (var2 == null) {
         var2 = FileSystems.newFileSystem(var1, (ClassLoader)null);
         this.fileSystems.put(var1, var2);
      }

      return var2;
   }

   private static String getRelativePath(String var0, JavaFileObject.Kind var1) {
      return var0.replace(".", "/") + var1.extension;
   }

   private static String getRelativePath(String var0, String var1) {
      return var0.isEmpty() ? var1 : var0.replace(".", "/") + "/" + var1;
   }

   private static String getBaseName(String var0) {
      int var1 = var0.lastIndexOf("/");
      return var0.substring(var1 + 1);
   }

   private static boolean isDirectory(Path var0) throws IOException {
      BasicFileAttributes var1 = Files.readAttributes(var0, BasicFileAttributes.class);
      return var1.isDirectory();
   }

   private static Path getPath(FileSystem var0, String var1) {
      return var0.getPath(var1.replace("/", var0.getSeparator()));
   }

   private static Path resolve(Path var0, String var1) {
      FileSystem var2 = var0.getFileSystem();
      Path var3 = var2.getPath(var1.replace("/", var2.getSeparator()));
      return var0.resolve(var3);
   }

   private static class PathsForLocation extends LinkedHashSet {
      private static final long serialVersionUID = 6788510222394486733L;

      private PathsForLocation() {
      }

      // $FF: synthetic method
      PathsForLocation(Object var1) {
         this();
      }
   }
}
