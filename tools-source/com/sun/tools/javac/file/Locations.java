package com.sun.tools.javac.file;

import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.StringUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

public class Locations {
   private Log log;
   private Options options;
   private Lint lint;
   private FSInfo fsInfo;
   private boolean warn;
   private boolean inited = false;
   Map handlersForLocation;
   Map handlersForOption;

   public Locations() {
      this.initHandlers();
   }

   public void update(Log var1, Options var2, Lint var3, FSInfo var4) {
      this.log = var1;
      this.options = var2;
      this.lint = var3;
      this.fsInfo = var4;
   }

   public Collection bootClassPath() {
      return this.getLocation(StandardLocation.PLATFORM_CLASS_PATH);
   }

   public boolean isDefaultBootClassPath() {
      BootClassPathLocationHandler var1 = (BootClassPathLocationHandler)this.getHandler(StandardLocation.PLATFORM_CLASS_PATH);
      return var1.isDefault();
   }

   boolean isDefaultBootClassPathRtJar(File var1) {
      BootClassPathLocationHandler var2 = (BootClassPathLocationHandler)this.getHandler(StandardLocation.PLATFORM_CLASS_PATH);
      return var2.isDefaultRtJar(var1);
   }

   public Collection userClassPath() {
      return this.getLocation(StandardLocation.CLASS_PATH);
   }

   public Collection sourcePath() {
      Collection var1 = this.getLocation(StandardLocation.SOURCE_PATH);
      return var1 != null && !var1.isEmpty() ? var1 : null;
   }

   private static Iterable getPathEntries(String var0) {
      return getPathEntries(var0, (File)null);
   }

   private static Iterable getPathEntries(String var0, File var1) {
      ListBuffer var2 = new ListBuffer();

      int var4;
      for(int var3 = 0; var3 <= var0.length(); var3 = var4 + 1) {
         var4 = var0.indexOf(File.pathSeparatorChar, var3);
         if (var4 == -1) {
            var4 = var0.length();
         }

         if (var3 < var4) {
            var2.add(new File(var0.substring(var3, var4)));
         } else if (var1 != null) {
            var2.add(var1);
         }
      }

      return var2;
   }

   void initHandlers() {
      this.handlersForLocation = new HashMap();
      this.handlersForOption = new EnumMap(Option.class);
      LocationHandler[] var1 = new LocationHandler[]{new BootClassPathLocationHandler(), new ClassPathLocationHandler(), new SimpleLocationHandler(StandardLocation.SOURCE_PATH, new Option[]{Option.SOURCEPATH}), new SimpleLocationHandler(StandardLocation.ANNOTATION_PROCESSOR_PATH, new Option[]{Option.PROCESSORPATH}), new OutputLocationHandler(StandardLocation.CLASS_OUTPUT, new Option[]{Option.D}), new OutputLocationHandler(StandardLocation.SOURCE_OUTPUT, new Option[]{Option.S}), new OutputLocationHandler(StandardLocation.NATIVE_HEADER_OUTPUT, new Option[]{Option.H})};
      LocationHandler[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LocationHandler var5 = var2[var4];
         this.handlersForLocation.put(var5.location, var5);
         Iterator var6 = var5.options.iterator();

         while(var6.hasNext()) {
            Option var7 = (Option)var6.next();
            this.handlersForOption.put(var7, var5);
         }
      }

   }

   boolean handleOption(Option var1, String var2) {
      LocationHandler var3 = (LocationHandler)this.handlersForOption.get(var1);
      return var3 == null ? false : var3.handleOption(var1, var2);
   }

   Collection getLocation(JavaFileManager.Location var1) {
      LocationHandler var2 = this.getHandler(var1);
      return var2 == null ? null : var2.getLocation();
   }

   File getOutputLocation(JavaFileManager.Location var1) {
      if (!var1.isOutputLocation()) {
         throw new IllegalArgumentException();
      } else {
         LocationHandler var2 = this.getHandler(var1);
         return ((OutputLocationHandler)var2).outputDir;
      }
   }

   void setLocation(JavaFileManager.Location var1, Iterable var2) throws IOException {
      Object var3 = this.getHandler(var1);
      if (var3 == null) {
         if (var1.isOutputLocation()) {
            var3 = new OutputLocationHandler(var1, new Option[0]);
         } else {
            var3 = new SimpleLocationHandler(var1, new Option[0]);
         }

         this.handlersForLocation.put(var1, var3);
      }

      ((LocationHandler)var3).setLocation(var2);
   }

   protected LocationHandler getHandler(JavaFileManager.Location var1) {
      var1.getClass();
      this.lazy();
      return (LocationHandler)this.handlersForLocation.get(var1);
   }

   protected void lazy() {
      if (!this.inited) {
         this.warn = this.lint.isEnabled(Lint.LintCategory.PATH);
         Iterator var1 = this.handlersForLocation.values().iterator();

         while(var1.hasNext()) {
            LocationHandler var2 = (LocationHandler)var1.next();
            var2.update(this.options);
         }

         this.inited = true;
      }

   }

   private boolean isArchive(File var1) {
      String var2 = StringUtils.toLowerCase(var1.getName());
      return this.fsInfo.isFile(var1) && (var2.endsWith(".jar") || var2.endsWith(".zip"));
   }

   public static URL[] pathToURLs(String var0) {
      StringTokenizer var1 = new StringTokenizer(var0, File.pathSeparator);
      URL[] var2 = new URL[var1.countTokens()];
      int var3 = 0;

      while(var1.hasMoreTokens()) {
         URL var4 = fileToURL(new File(var1.nextToken()));
         if (var4 != null) {
            var2[var3++] = var4;
         }
      }

      var2 = (URL[])Arrays.copyOf(var2, var3);
      return var2;
   }

   private static URL fileToURL(File var0) {
      String var1;
      try {
         var1 = var0.getCanonicalPath();
      } catch (IOException var4) {
         var1 = var0.getAbsolutePath();
      }

      var1 = var1.replace(File.separatorChar, '/');
      if (!var1.startsWith("/")) {
         var1 = "/" + var1;
      }

      if (!var0.isFile()) {
         var1 = var1 + "/";
      }

      try {
         return new URL("file", "", var1);
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException(var0.toString());
      }
   }

   private class BootClassPathLocationHandler extends LocationHandler {
      private Collection searchPath;
      final Map optionValues = new EnumMap(Option.class);
      private File defaultBootClassPathRtJar = null;
      private boolean isDefaultBootClassPath;

      BootClassPathLocationHandler() {
         super(StandardLocation.PLATFORM_CLASS_PATH, Option.BOOTCLASSPATH, Option.XBOOTCLASSPATH, Option.XBOOTCLASSPATH_PREPEND, Option.XBOOTCLASSPATH_APPEND, Option.ENDORSEDDIRS, Option.DJAVA_ENDORSED_DIRS, Option.EXTDIRS, Option.DJAVA_EXT_DIRS);
      }

      boolean isDefault() {
         this.lazy();
         return this.isDefaultBootClassPath;
      }

      boolean isDefaultRtJar(File var1) {
         this.lazy();
         return var1.equals(this.defaultBootClassPathRtJar);
      }

      boolean handleOption(Option var1, String var2) {
         if (!this.options.contains(var1)) {
            return false;
         } else {
            var1 = this.canonicalize(var1);
            this.optionValues.put(var1, var2);
            if (var1 == Option.BOOTCLASSPATH) {
               this.optionValues.remove(Option.XBOOTCLASSPATH_PREPEND);
               this.optionValues.remove(Option.XBOOTCLASSPATH_APPEND);
            }

            this.searchPath = null;
            return true;
         }
      }

      private Option canonicalize(Option var1) {
         switch (var1) {
            case XBOOTCLASSPATH:
               return Option.BOOTCLASSPATH;
            case DJAVA_ENDORSED_DIRS:
               return Option.ENDORSEDDIRS;
            case DJAVA_EXT_DIRS:
               return Option.EXTDIRS;
            default:
               return var1;
         }
      }

      Collection getLocation() {
         this.lazy();
         return this.searchPath;
      }

      void setLocation(Iterable var1) {
         if (var1 == null) {
            this.searchPath = null;
         } else {
            this.defaultBootClassPathRtJar = null;
            this.isDefaultBootClassPath = false;
            Path var2 = (Locations.this.new Path()).addFiles(var1, false);
            this.searchPath = Collections.unmodifiableCollection(var2);
            this.optionValues.clear();
         }

      }

      Path computePath() {
         this.defaultBootClassPathRtJar = null;
         Path var1 = Locations.this.new Path();
         String var2 = (String)this.optionValues.get(Option.BOOTCLASSPATH);
         String var3 = (String)this.optionValues.get(Option.ENDORSEDDIRS);
         String var4 = (String)this.optionValues.get(Option.EXTDIRS);
         String var5 = (String)this.optionValues.get(Option.XBOOTCLASSPATH_PREPEND);
         String var6 = (String)this.optionValues.get(Option.XBOOTCLASSPATH_APPEND);
         var1.addFiles(var5);
         if (var3 != null) {
            var1.addDirectories(var3);
         } else {
            var1.addDirectories(System.getProperty("java.endorsed.dirs"), false);
         }

         if (var2 != null) {
            var1.addFiles(var2);
         } else {
            String var7 = System.getProperty("sun.boot.class.path");
            var1.addFiles(var7, false);
            File var8 = new File("rt.jar");
            Iterator var9 = Locations.getPathEntries(var7).iterator();

            while(var9.hasNext()) {
               File var10 = (File)var9.next();
               if ((new File(var10.getName())).equals(var8)) {
                  this.defaultBootClassPathRtJar = var10;
               }
            }
         }

         var1.addFiles(var6);
         if (var4 != null) {
            var1.addDirectories(var4);
         } else {
            var1.addDirectories(System.getProperty("java.ext.dirs"), false);
         }

         this.isDefaultBootClassPath = var5 == null && var2 == null && var6 == null;
         return var1;
      }

      private void lazy() {
         if (this.searchPath == null) {
            this.searchPath = Collections.unmodifiableCollection(this.computePath());
         }

      }
   }

   private class ClassPathLocationHandler extends SimpleLocationHandler {
      ClassPathLocationHandler() {
         super(StandardLocation.CLASS_PATH, Option.CLASSPATH, Option.CP);
      }

      Collection getLocation() {
         this.lazy();
         return this.searchPath;
      }

      protected Path computePath(String var1) {
         String var2 = var1;
         if (var1 == null) {
            var2 = System.getProperty("env.class.path");
         }

         if (var2 == null && System.getProperty("application.home") == null) {
            var2 = System.getProperty("java.class.path");
         }

         if (var2 == null) {
            var2 = ".";
         }

         return this.createPath().addFiles(var2);
      }

      protected Path createPath() {
         return (Locations.this.new Path()).expandJarClassPaths(true).emptyPathDefault(new File("."));
      }

      private void lazy() {
         if (this.searchPath == null) {
            this.setLocation((Iterable)null);
         }

      }
   }

   private class SimpleLocationHandler extends LocationHandler {
      protected Collection searchPath;

      SimpleLocationHandler(JavaFileManager.Location var2, Option... var3) {
         super(var2, var3);
      }

      boolean handleOption(Option var1, String var2) {
         if (!this.options.contains(var1)) {
            return false;
         } else {
            this.searchPath = var2 == null ? null : Collections.unmodifiableCollection(this.createPath().addFiles(var2));
            return true;
         }
      }

      Collection getLocation() {
         return this.searchPath;
      }

      void setLocation(Iterable var1) {
         Path var2;
         if (var1 == null) {
            var2 = this.computePath((String)null);
         } else {
            var2 = this.createPath().addFiles(var1);
         }

         this.searchPath = Collections.unmodifiableCollection(var2);
      }

      protected Path computePath(String var1) {
         return this.createPath().addFiles(var1);
      }

      protected Path createPath() {
         return Locations.this.new Path();
      }
   }

   private class OutputLocationHandler extends LocationHandler {
      private File outputDir;

      OutputLocationHandler(JavaFileManager.Location var2, Option... var3) {
         super(var2, var3);
      }

      boolean handleOption(Option var1, String var2) {
         if (!this.options.contains(var1)) {
            return false;
         } else {
            this.outputDir = new File(var2);
            return true;
         }
      }

      Collection getLocation() {
         return this.outputDir == null ? null : Collections.singleton(this.outputDir);
      }

      void setLocation(Iterable var1) throws IOException {
         if (var1 == null) {
            this.outputDir = null;
         } else {
            Iterator var2 = var1.iterator();
            if (!var2.hasNext()) {
               throw new IllegalArgumentException("empty path for directory");
            }

            File var3 = (File)var2.next();
            if (var2.hasNext()) {
               throw new IllegalArgumentException("path too long for directory");
            }

            if (!var3.exists()) {
               throw new FileNotFoundException(var3 + ": does not exist");
            }

            if (!var3.isDirectory()) {
               throw new IOException(var3 + ": not a directory");
            }

            this.outputDir = var3;
         }

      }
   }

   protected abstract class LocationHandler {
      final JavaFileManager.Location location;
      final Set options;

      protected LocationHandler(JavaFileManager.Location var2, Option... var3) {
         this.location = var2;
         this.options = var3.length == 0 ? EnumSet.noneOf(Option.class) : EnumSet.copyOf(Arrays.asList(var3));
      }

      void update(Options var1) {
         Iterator var2 = this.options.iterator();

         while(var2.hasNext()) {
            Option var3 = (Option)var2.next();
            String var4 = var1.get(var3);
            if (var4 != null) {
               this.handleOption(var3, var4);
            }
         }

      }

      abstract boolean handleOption(Option var1, String var2);

      abstract Collection getLocation();

      abstract void setLocation(Iterable var1) throws IOException;
   }

   private class Path extends LinkedHashSet {
      private static final long serialVersionUID = 0L;
      private boolean expandJarClassPaths = false;
      private Set canonicalValues = new HashSet();
      private File emptyPathDefault = null;

      public Path expandJarClassPaths(boolean var1) {
         this.expandJarClassPaths = var1;
         return this;
      }

      public Path emptyPathDefault(File var1) {
         this.emptyPathDefault = var1;
         return this;
      }

      public Path() {
      }

      public Path addDirectories(String var1, boolean var2) {
         boolean var3 = this.expandJarClassPaths;
         this.expandJarClassPaths = true;

         Path var9;
         try {
            if (var1 != null) {
               Iterator var4 = Locations.getPathEntries(var1).iterator();

               while(var4.hasNext()) {
                  File var5 = (File)var4.next();
                  this.addDirectory(var5, var2);
               }
            }

            var9 = this;
         } finally {
            this.expandJarClassPaths = var3;
         }

         return var9;
      }

      public Path addDirectories(String var1) {
         return this.addDirectories(var1, Locations.this.warn);
      }

      private void addDirectory(File var1, boolean var2) {
         if (!var1.isDirectory()) {
            if (var2) {
               Locations.this.log.warning(Lint.LintCategory.PATH, "dir.path.element.not.found", new Object[]{var1});
            }

         } else {
            File[] var3 = var1.listFiles();
            if (var3 != null) {
               File[] var4 = var3;
               int var5 = var3.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  File var7 = var4[var6];
                  if (Locations.this.isArchive(var7)) {
                     this.addFile(var7, var2);
                  }
               }

            }
         }
      }

      public Path addFiles(String var1, boolean var2) {
         if (var1 != null) {
            this.addFiles(Locations.getPathEntries(var1, this.emptyPathDefault), var2);
         }

         return this;
      }

      public Path addFiles(String var1) {
         return this.addFiles(var1, Locations.this.warn);
      }

      public Path addFiles(Iterable var1, boolean var2) {
         if (var1 != null) {
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
               File var4 = (File)var3.next();
               this.addFile(var4, var2);
            }
         }

         return this;
      }

      public Path addFiles(Iterable var1) {
         return this.addFiles(var1, Locations.this.warn);
      }

      public void addFile(File var1, boolean var2) {
         if (!this.contains(var1)) {
            if (!Locations.this.fsInfo.exists(var1)) {
               if (var2) {
                  Locations.this.log.warning(Lint.LintCategory.PATH, "path.element.not.found", new Object[]{var1});
               }

               super.add(var1);
            } else {
               File var3 = Locations.this.fsInfo.getCanonicalFile(var1);
               if (!this.canonicalValues.contains(var3)) {
                  if (Locations.this.fsInfo.isFile(var1) && !Locations.this.isArchive(var1)) {
                     try {
                        ZipFile var4 = new ZipFile(var1);
                        var4.close();
                        if (var2) {
                           Locations.this.log.warning(Lint.LintCategory.PATH, "unexpected.archive.file", new Object[]{var1});
                        }
                     } catch (IOException var5) {
                        if (var2) {
                           Locations.this.log.warning(Lint.LintCategory.PATH, "invalid.archive.file", new Object[]{var1});
                        }

                        return;
                     }
                  }

                  super.add(var1);
                  this.canonicalValues.add(var3);
                  if (this.expandJarClassPaths && Locations.this.fsInfo.isFile(var1)) {
                     this.addJarClassPath(var1, var2);
                  }

               }
            }
         }
      }

      private void addJarClassPath(File var1, boolean var2) {
         try {
            Iterator var3 = Locations.this.fsInfo.getJarClassPath(var1).iterator();

            while(var3.hasNext()) {
               File var4 = (File)var3.next();
               this.addFile(var4, var2);
            }
         } catch (IOException var5) {
            Locations.this.log.error("error.reading.file", new Object[]{var1, JavacFileManager.getMessage(var5)});
         }

      }
   }
}
