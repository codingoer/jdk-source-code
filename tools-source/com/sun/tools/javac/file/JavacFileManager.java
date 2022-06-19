package com.sun.tools.javac.file;

import com.sun.tools.javac.util.BaseFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import javax.lang.model.SourceVersion;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class JavacFileManager extends BaseFileManager implements StandardJavaFileManager {
   private FSInfo fsInfo;
   private boolean contextUseOptimizedZip;
   private ZipFileIndexCache zipFileIndexCache;
   private final Set sourceOrClass;
   protected boolean mmappedIO;
   protected boolean symbolFileEnabled;
   protected SortFiles sortFiles;
   private static final boolean fileSystemIsCaseSensitive;
   Map archives;
   private static final String[] symbolFileLocation;
   private static final RelativePath.RelativeDirectory symbolFilePrefix;
   private String defaultEncodingName;

   public static char[] toArray(CharBuffer var0) {
      return var0.hasArray() ? ((CharBuffer)var0.compact().flip()).array() : var0.toString().toCharArray();
   }

   public static void preRegister(Context var0) {
      var0.put(JavaFileManager.class, new Context.Factory() {
         public JavaFileManager make(Context var1) {
            return new JavacFileManager(var1, true, (Charset)null);
         }
      });
   }

   public JavacFileManager(Context var1, boolean var2, Charset var3) {
      super(var3);
      this.sourceOrClass = EnumSet.of(Kind.SOURCE, Kind.CLASS);
      this.archives = new HashMap();
      if (var2) {
         var1.put((Class)JavaFileManager.class, (Object)this);
      }

      this.setContext(var1);
   }

   public void setContext(Context var1) {
      super.setContext(var1);
      this.fsInfo = FSInfo.instance(var1);
      this.contextUseOptimizedZip = this.options.getBoolean("useOptimizedZip", true);
      if (this.contextUseOptimizedZip) {
         this.zipFileIndexCache = ZipFileIndexCache.getSharedInstance();
      }

      this.mmappedIO = this.options.isSet("mmappedIO");
      this.symbolFileEnabled = !this.options.isSet("ignore.symbol.file");
      String var2 = this.options.get("sortFiles");
      if (var2 != null) {
         this.sortFiles = var2.equals("reverse") ? JavacFileManager.SortFiles.REVERSE : JavacFileManager.SortFiles.FORWARD;
      }

   }

   public void setSymbolFileEnabled(boolean var1) {
      this.symbolFileEnabled = var1;
   }

   public boolean isDefaultBootClassPath() {
      return this.locations.isDefaultBootClassPath();
   }

   public JavaFileObject getFileForInput(String var1) {
      return this.getRegularFile(new File(var1));
   }

   public JavaFileObject getRegularFile(File var1) {
      return new RegularFileObject(this, var1);
   }

   public JavaFileObject getFileForOutput(String var1, JavaFileObject.Kind var2, JavaFileObject var3) throws IOException {
      return this.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, var1, var2, var3);
   }

   public Iterable getJavaFileObjectsFromStrings(Iterable var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.append(new File((String)nullCheck(var4)));
      }

      return this.getJavaFileObjectsFromFiles(var2.toList());
   }

   public Iterable getJavaFileObjects(String... var1) {
      return this.getJavaFileObjectsFromStrings(Arrays.asList((Object[])nullCheck(var1)));
   }

   private static boolean isValidName(String var0) {
      String[] var1 = var0.split("\\.", -1);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (!SourceVersion.isIdentifier(var4)) {
            return false;
         }
      }

      return true;
   }

   private static void validateClassName(String var0) {
      if (!isValidName(var0)) {
         throw new IllegalArgumentException("Invalid class name: " + var0);
      }
   }

   private static void validatePackageName(String var0) {
      if (var0.length() > 0 && !isValidName(var0)) {
         throw new IllegalArgumentException("Invalid packageName name: " + var0);
      }
   }

   public static void testName(String var0, boolean var1, boolean var2) {
      try {
         validatePackageName(var0);
         if (!var1) {
            throw new AssertionError("Invalid package name accepted: " + var0);
         }

         printAscii("Valid package name: \"%s\"", var0);
      } catch (IllegalArgumentException var5) {
         if (var1) {
            throw new AssertionError("Valid package name rejected: " + var0);
         }

         printAscii("Invalid package name: \"%s\"", var0);
      }

      try {
         validateClassName(var0);
         if (!var2) {
            throw new AssertionError("Invalid class name accepted: " + var0);
         }

         printAscii("Valid class name: \"%s\"", var0);
      } catch (IllegalArgumentException var4) {
         if (var2) {
            throw new AssertionError("Valid class name rejected: " + var0);
         }

         printAscii("Invalid class name: \"%s\"", var0);
      }

   }

   private static void printAscii(String var0, Object... var1) {
      String var2;
      try {
         var2 = new String(String.format((Locale)null, var0, var1).getBytes("US-ASCII"), "US-ASCII");
      } catch (UnsupportedEncodingException var4) {
         throw new AssertionError(var4);
      }

      System.out.println(var2);
   }

   private void listDirectory(File var1, RelativePath.RelativeDirectory var2, Set var3, boolean var4, ListBuffer var5) {
      File var6 = var2.getFile(var1);
      if (this.caseMapCheck(var6, var2)) {
         File[] var7 = var6.listFiles();
         if (var7 != null) {
            if (this.sortFiles != null) {
               Arrays.sort(var7, this.sortFiles);
            }

            File[] var8 = var7;
            int var9 = var7.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               File var11 = var8[var10];
               String var12 = var11.getName();
               if (var11.isDirectory()) {
                  if (var4 && SourceVersion.isIdentifier(var12)) {
                     this.listDirectory(var1, new RelativePath.RelativeDirectory(var2, var12), var3, var4, var5);
                  }
               } else if (this.isValidFile(var12, var3)) {
                  RegularFileObject var13 = new RegularFileObject(this, var12, new File(var6, var12));
                  var5.append(var13);
               }
            }

         }
      }
   }

   private void listArchive(Archive var1, RelativePath.RelativeDirectory var2, Set var3, boolean var4, ListBuffer var5) {
      List var6 = var1.getFiles(var2);
      if (var6 != null) {
         for(; !var6.isEmpty(); var6 = var6.tail) {
            String var7 = (String)var6.head;
            if (this.isValidFile(var7, var3)) {
               var5.append(var1.getFileObject(var2, var7));
            }
         }
      }

      if (var4) {
         Iterator var9 = var1.getSubdirectories().iterator();

         while(var9.hasNext()) {
            RelativePath.RelativeDirectory var8 = (RelativePath.RelativeDirectory)var9.next();
            if (var2.contains(var8)) {
               this.listArchive(var1, var8, var3, false, var5);
            }
         }
      }

   }

   private void listContainer(File var1, RelativePath.RelativeDirectory var2, Set var3, boolean var4, ListBuffer var5) {
      Archive var6 = (Archive)this.archives.get(var1);
      if (var6 == null) {
         if (this.fsInfo.isDirectory(var1)) {
            this.listDirectory(var1, var2, var3, var4, var5);
            return;
         }

         try {
            var6 = this.openArchive(var1);
         } catch (IOException var8) {
            this.log.error("error.reading.file", new Object[]{var1, getMessage(var8)});
            return;
         }
      }

      this.listArchive(var6, var2, var3, var4, var5);
   }

   private boolean isValidFile(String var1, Set var2) {
      JavaFileObject.Kind var3 = getKind(var1);
      return var2.contains(var3);
   }

   private boolean caseMapCheck(File var1, RelativePath var2) {
      if (fileSystemIsCaseSensitive) {
         return true;
      } else {
         String var3;
         try {
            var3 = var1.getCanonicalPath();
         } catch (IOException var8) {
            return false;
         }

         char[] var4 = var3.toCharArray();
         char[] var5 = var2.path.toCharArray();
         int var6 = var4.length - 1;
         int var7 = var5.length - 1;

         while(var6 >= 0 && var7 >= 0) {
            while(var6 >= 0 && var4[var6] == File.separatorChar) {
               --var6;
            }

            while(var7 >= 0 && var5[var7] == '/') {
               --var7;
            }

            if (var6 >= 0 && var7 >= 0) {
               if (var4[var6] != var5[var7]) {
                  return false;
               }

               --var6;
               --var7;
            }
         }

         return var7 < 0;
      }
   }

   protected Archive openArchive(File var1) throws IOException {
      try {
         return this.openArchive(var1, this.contextUseOptimizedZip);
      } catch (IOException var3) {
         if (var3 instanceof ZipFileIndex.ZipFormatException) {
            return this.openArchive(var1, false);
         } else {
            throw var3;
         }
      }
   }

   private Archive openArchive(File var1, boolean var2) throws IOException {
      File var3 = var1;
      String var8;
      if (this.symbolFileEnabled && this.locations.isDefaultBootClassPathRtJar(var1)) {
         File var4 = var1.getParentFile().getParentFile();
         if ((new File(var4.getName())).equals(new File("jre"))) {
            var4 = var4.getParentFile();
         }

         String[] var5 = symbolFileLocation;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            var4 = new File(var4, var8);
         }

         if (var4.exists()) {
            var1 = var4;
         }
      }

      Object var14;
      try {
         ZipFile var13 = null;
         boolean var15 = false;
         String var16 = null;
         if (!var2) {
            var13 = new ZipFile(var1);
         } else {
            var15 = this.options.isSet("usezipindex");
            var16 = this.options.get("java.io.tmpdir");
            var8 = this.options.get("cachezipindexdir");
            if (var8 != null && var8.length() != 0) {
               if (var8.startsWith("\"")) {
                  if (var8.endsWith("\"")) {
                     var8 = var8.substring(1, var8.length() - 1);
                  } else {
                     var8 = var8.substring(1);
                  }
               }

               File var9 = new File(var8);
               if (var9.exists() && var9.canWrite()) {
                  var16 = var8;
                  if (!var8.endsWith("/") && !var8.endsWith(File.separator)) {
                     var16 = var8 + File.separator;
                  }
               }
            }
         }

         if (var3 == var1) {
            if (!var2) {
               var14 = new ZipArchive(this, var13);
            } else {
               var14 = new ZipFileIndexArchive(this, this.zipFileIndexCache.getZipFileIndex(var1, (RelativePath.RelativeDirectory)null, var15, var16, this.options.isSet("writezipindexfiles")));
            }
         } else if (!var2) {
            var14 = new SymbolArchive(this, var3, var13, symbolFilePrefix);
         } else {
            var14 = new ZipFileIndexArchive(this, this.zipFileIndexCache.getZipFileIndex(var1, symbolFilePrefix, var15, var16, this.options.isSet("writezipindexfiles")));
         }
      } catch (FileNotFoundException var10) {
         var14 = new MissingArchive(var1);
      } catch (ZipFileIndex.ZipFormatException var11) {
         throw var11;
      } catch (IOException var12) {
         if (var1.exists()) {
            this.log.error("error.reading.file", new Object[]{var1, getMessage(var12)});
         }

         var14 = new MissingArchive(var1);
      }

      this.archives.put(var3, var14);
      return (Archive)var14;
   }

   public void flush() {
      this.contentCache.clear();
   }

   public void close() {
      Iterator var1 = this.archives.values().iterator();

      while(var1.hasNext()) {
         Archive var2 = (Archive)var1.next();
         var1.remove();

         try {
            var2.close();
         } catch (IOException var4) {
         }
      }

   }

   private String getDefaultEncodingName() {
      if (this.defaultEncodingName == null) {
         this.defaultEncodingName = (new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding();
      }

      return this.defaultEncodingName;
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
            File var5 = (File)var4.next();

            try {
               var3.append(var5.toURI().toURL());
            } catch (MalformedURLException var7) {
               throw new AssertionError(var7);
            }
         }

         return this.getClassLoader((URL[])var3.toArray(new URL[var3.size()]));
      }
   }

   public Iterable list(JavaFileManager.Location var1, String var2, Set var3, boolean var4) throws IOException {
      nullCheck(var2);
      nullCheck(var3);
      Iterable var5 = this.getLocation(var1);
      if (var5 == null) {
         return List.nil();
      } else {
         RelativePath.RelativeDirectory var6 = RelativePath.RelativeDirectory.forPackage(var2);
         ListBuffer var7 = new ListBuffer();
         Iterator var8 = var5.iterator();

         while(var8.hasNext()) {
            File var9 = (File)var8.next();
            this.listContainer(var9, var6, var3, var4, var7);
         }

         return var7.toList();
      }
   }

   public String inferBinaryName(JavaFileManager.Location var1, JavaFileObject var2) {
      var2.getClass();
      var1.getClass();
      Iterable var3 = this.getLocation(var1);
      if (var3 == null) {
         return null;
      } else if (var2 instanceof BaseFileObject) {
         return ((BaseFileObject)var2).inferBinaryName(var3);
      } else {
         throw new IllegalArgumentException(var2.getClass().getName());
      }
   }

   public boolean isSameFile(FileObject var1, FileObject var2) {
      nullCheck(var1);
      nullCheck(var2);
      if (!(var1 instanceof BaseFileObject)) {
         throw new IllegalArgumentException("Not supported: " + var1);
      } else if (!(var2 instanceof BaseFileObject)) {
         throw new IllegalArgumentException("Not supported: " + var2);
      } else {
         return var1.equals(var2);
      }
   }

   public boolean hasLocation(JavaFileManager.Location var1) {
      return this.getLocation(var1) != null;
   }

   public JavaFileObject getJavaFileForInput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3) throws IOException {
      nullCheck(var1);
      nullCheck(var2);
      nullCheck(var3);
      if (!this.sourceOrClass.contains(var3)) {
         throw new IllegalArgumentException("Invalid kind: " + var3);
      } else {
         return this.getFileForInput(var1, RelativePath.RelativeFile.forClass(var2, var3));
      }
   }

   public FileObject getFileForInput(JavaFileManager.Location var1, String var2, String var3) throws IOException {
      nullCheck(var1);
      nullCheck(var2);
      if (!isRelativeUri(var3)) {
         throw new IllegalArgumentException("Invalid relative name: " + var3);
      } else {
         RelativePath.RelativeFile var4 = var2.length() == 0 ? new RelativePath.RelativeFile(var3) : new RelativePath.RelativeFile(RelativePath.RelativeDirectory.forPackage(var2), var3);
         return this.getFileForInput(var1, var4);
      }
   }

   private JavaFileObject getFileForInput(JavaFileManager.Location var1, RelativePath.RelativeFile var2) throws IOException {
      Iterable var3 = this.getLocation(var1);
      if (var3 == null) {
         return null;
      } else {
         Iterator var4 = var3.iterator();

         File var7;
         label30:
         do {
            Archive var6;
            do {
               if (!var4.hasNext()) {
                  return null;
               }

               File var5 = (File)var4.next();
               var6 = (Archive)this.archives.get(var5);
               if (var6 == null) {
                  if (this.fsInfo.isDirectory(var5)) {
                     var7 = var2.getFile(var5);
                     continue label30;
                  }

                  var6 = this.openArchive(var5);
               }
            } while(!var6.contains(var2));

            return var6.getFileObject(var2.dirname(), var2.basename());
         } while(!var7.exists());

         return new RegularFileObject(this, var7);
      }
   }

   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException {
      nullCheck(var1);
      nullCheck(var2);
      nullCheck(var3);
      if (!this.sourceOrClass.contains(var3)) {
         throw new IllegalArgumentException("Invalid kind: " + var3);
      } else {
         return this.getFileForOutput(var1, RelativePath.RelativeFile.forClass(var2, var3), var4);
      }
   }

   public FileObject getFileForOutput(JavaFileManager.Location var1, String var2, String var3, FileObject var4) throws IOException {
      nullCheck(var1);
      nullCheck(var2);
      if (!isRelativeUri(var3)) {
         throw new IllegalArgumentException("Invalid relative name: " + var3);
      } else {
         RelativePath.RelativeFile var5 = var2.length() == 0 ? new RelativePath.RelativeFile(var3) : new RelativePath.RelativeFile(RelativePath.RelativeDirectory.forPackage(var2), var3);
         return this.getFileForOutput(var1, var5, var4);
      }
   }

   private JavaFileObject getFileForOutput(JavaFileManager.Location var1, RelativePath.RelativeFile var2, FileObject var3) throws IOException {
      File var4;
      File var5;
      if (var1 == StandardLocation.CLASS_OUTPUT) {
         if (this.getClassOutDir() == null) {
            var5 = null;
            if (var3 != null && var3 instanceof RegularFileObject) {
               var5 = ((RegularFileObject)var3).file.getParentFile();
            }

            return new RegularFileObject(this, new File(var5, var2.basename()));
         }

         var4 = this.getClassOutDir();
      } else if (var1 == StandardLocation.SOURCE_OUTPUT) {
         var4 = this.getSourceOutDir() != null ? this.getSourceOutDir() : this.getClassOutDir();
      } else {
         Collection var8 = this.locations.getLocation(var1);
         var4 = null;
         Iterator var6 = var8.iterator();
         if (var6.hasNext()) {
            File var7 = (File)var6.next();
            var4 = var7;
         }
      }

      var5 = var2.getFile(var4);
      return new RegularFileObject(this, var5);
   }

   public Iterable getJavaFileObjectsFromFiles(Iterable var1) {
      ArrayList var2;
      if (var1 instanceof Collection) {
         var2 = new ArrayList(((Collection)var1).size());
      } else {
         var2 = new ArrayList();
      }

      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         File var4 = (File)var3.next();
         var2.add(new RegularFileObject(this, (File)nullCheck(var4)));
      }

      return var2;
   }

   public Iterable getJavaFileObjects(File... var1) {
      return this.getJavaFileObjectsFromFiles(Arrays.asList((Object[])nullCheck(var1)));
   }

   public void setLocation(JavaFileManager.Location var1, Iterable var2) throws IOException {
      nullCheck(var1);
      this.locations.setLocation(var1, var2);
   }

   public Iterable getLocation(JavaFileManager.Location var1) {
      nullCheck(var1);
      return this.locations.getLocation(var1);
   }

   private File getClassOutDir() {
      return this.locations.getOutputLocation(StandardLocation.CLASS_OUTPUT);
   }

   private File getSourceOutDir() {
      return this.locations.getOutputLocation(StandardLocation.SOURCE_OUTPUT);
   }

   protected static boolean isRelativeUri(URI var0) {
      if (var0.isAbsolute()) {
         return false;
      } else {
         String var1 = var0.normalize().getPath();
         if (var1.length() == 0) {
            return false;
         } else if (!var1.equals(var0.getPath())) {
            return false;
         } else {
            return !var1.startsWith("/") && !var1.startsWith("./") && !var1.startsWith("../");
         }
      }
   }

   protected static boolean isRelativeUri(String var0) {
      try {
         return isRelativeUri(new URI(var0));
      } catch (URISyntaxException var2) {
         return false;
      }
   }

   public static String getRelativeName(File var0) {
      if (!var0.isAbsolute()) {
         String var1 = var0.getPath().replace(File.separatorChar, '/');
         if (isRelativeUri(var1)) {
            return var1;
         }
      }

      throw new IllegalArgumentException("Invalid relative path: " + var0);
   }

   public static String getMessage(IOException var0) {
      String var1 = var0.getLocalizedMessage();
      if (var1 != null) {
         return var1;
      } else {
         var1 = var0.getMessage();
         return var1 != null ? var1 : var0.toString();
      }
   }

   static {
      fileSystemIsCaseSensitive = File.separatorChar == '/';
      symbolFileLocation = new String[]{"lib", "ct.sym"};
      symbolFilePrefix = new RelativePath.RelativeDirectory("META-INF/sym/rt.jar/");
   }

   public class MissingArchive implements Archive {
      final File zipFileName;

      public MissingArchive(File var2) {
         this.zipFileName = var2;
      }

      public boolean contains(RelativePath var1) {
         return false;
      }

      public void close() {
      }

      public JavaFileObject getFileObject(RelativePath.RelativeDirectory var1, String var2) {
         return null;
      }

      public List getFiles(RelativePath.RelativeDirectory var1) {
         return List.nil();
      }

      public Set getSubdirectories() {
         return Collections.emptySet();
      }

      public String toString() {
         return "MissingArchive[" + this.zipFileName + "]";
      }
   }

   public interface Archive {
      void close() throws IOException;

      boolean contains(RelativePath var1);

      JavaFileObject getFileObject(RelativePath.RelativeDirectory var1, String var2);

      List getFiles(RelativePath.RelativeDirectory var1);

      Set getSubdirectories();
   }

   protected static enum SortFiles implements Comparator {
      FORWARD {
         public int compare(File var1, File var2) {
            return var1.getName().compareTo(var2.getName());
         }
      },
      REVERSE {
         public int compare(File var1, File var2) {
            return -var1.getName().compareTo(var2.getName());
         }
      };

      private SortFiles() {
      }

      // $FF: synthetic method
      SortFiles(Object var3) {
         this();
      }
   }
}
