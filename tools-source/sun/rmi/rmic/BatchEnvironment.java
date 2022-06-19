package sun.rmi.rmic;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import sun.tools.java.ClassPath;

public class BatchEnvironment extends sun.tools.javac.BatchEnvironment {
   private Main main;
   private Vector generatedFiles = new Vector();

   public static ClassPath createClassPath(String var0) {
      ClassPath[] var1 = classPaths((String)null, var0, (String)null, (String)null);
      return var1[1];
   }

   public static ClassPath createClassPath(String var0, String var1, String var2) {
      Path var3 = new Path();
      if (var1 == null) {
         var1 = System.getProperty("sun.boot.class.path");
      }

      if (var1 != null) {
         var3.addFiles(var1);
      }

      var3.expandJarClassPaths(true);
      if (var2 == null) {
         var2 = System.getProperty("java.ext.dirs");
      }

      if (var2 != null) {
         var3.addDirectories(var2);
      }

      var3.emptyPathDefault(".");
      if (var0 == null) {
         var0 = System.getProperty("env.class.path");
         if (var0 == null) {
            var0 = ".";
         }
      }

      var3.addFiles(var0);
      return new ClassPath((String[])var3.toArray(new String[var3.size()]));
   }

   public BatchEnvironment(OutputStream var1, ClassPath var2, Main var3) {
      super(var1, new ClassPath(""), var2);
      this.main = var3;
   }

   public Main getMain() {
      return this.main;
   }

   public ClassPath getClassPath() {
      return this.binaryPath;
   }

   public void addGeneratedFile(File var1) {
      this.generatedFiles.addElement(var1);
   }

   public void deleteGeneratedFiles() {
      synchronized(this.generatedFiles) {
         Enumeration var2 = this.generatedFiles.elements();

         while(var2.hasMoreElements()) {
            File var3 = (File)var2.nextElement();
            var3.delete();
         }

         this.generatedFiles.removeAllElements();
      }
   }

   public void shutdown() {
      this.main = null;
      this.generatedFiles = null;
      super.shutdown();
   }

   public String errorString(String var1, Object var2, Object var3, Object var4) {
      if (!var1.startsWith("rmic.") && !var1.startsWith("warn.rmic.")) {
         return super.errorString(var1, var2, var3, var4);
      } else {
         String var5 = Main.getText(var1, var2 != null ? var2.toString() : null, var3 != null ? var3.toString() : null, var4 != null ? var4.toString() : null);
         if (var1.startsWith("warn.")) {
            var5 = "warning: " + var5;
         }

         return var5;
      }
   }

   public void reset() {
   }

   private static class Path extends LinkedHashSet {
      private static final long serialVersionUID = 0L;
      private static final boolean warn = false;
      private boolean expandJarClassPaths = false;
      private String emptyPathDefault = null;

      private static boolean isZip(String var0) {
         return (new File(var0)).isFile();
      }

      public Path expandJarClassPaths(boolean var1) {
         this.expandJarClassPaths = var1;
         return this;
      }

      public Path emptyPathDefault(String var1) {
         this.emptyPathDefault = var1;
         return this;
      }

      public Path() {
      }

      public Path addDirectories(String var1, boolean var2) {
         if (var1 != null) {
            Iterator var3 = (new PathIterator(var1)).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               this.addDirectory(var4, var2);
            }
         }

         return this;
      }

      public Path addDirectories(String var1) {
         return this.addDirectories(var1, false);
      }

      private void addDirectory(String var1, boolean var2) {
         if ((new File(var1)).isDirectory()) {
            String[] var3 = (new File(var1)).list();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String var6 = var3[var5];
               String var7 = var6.toLowerCase();
               if (var7.endsWith(".jar") || var7.endsWith(".zip")) {
                  this.addFile(var1 + File.separator + var6, var2);
               }
            }

         }
      }

      public Path addFiles(String var1, boolean var2) {
         if (var1 != null) {
            Iterator var3 = (new PathIterator(var1, this.emptyPathDefault)).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               this.addFile(var4, var2);
            }
         }

         return this;
      }

      public Path addFiles(String var1) {
         return this.addFiles(var1, false);
      }

      private void addFile(String var1, boolean var2) {
         if (!this.contains(var1)) {
            File var3 = new File(var1);
            if (var3.exists() || !var2) {
               if (var3.isFile()) {
                  String var4 = var1.toLowerCase();
                  if (!var4.endsWith(".zip") && !var4.endsWith(".jar")) {
                     return;
                  }
               }

               super.add(var1);
               if (this.expandJarClassPaths && isZip(var1)) {
                  this.addJarClassPath(var1, var2);
               }

            }
         }
      }

      private void addJarClassPath(String var1, boolean var2) {
         try {
            String var3 = (new File(var1)).getParent();
            JarFile var4 = new JarFile(var1);

            try {
               Manifest var5 = var4.getManifest();
               if (var5 == null) {
                  return;
               }

               Attributes var6 = var5.getMainAttributes();
               if (var6 != null) {
                  String var7 = var6.getValue(Name.CLASS_PATH);
                  if (var7 == null) {
                     return;
                  }

                  String var9;
                  for(StringTokenizer var8 = new StringTokenizer(var7); var8.hasMoreTokens(); this.addFile(var9, var2)) {
                     var9 = var8.nextToken();
                     if (var3 != null) {
                        var9 = (new File(var3, var9)).getCanonicalPath();
                     }
                  }

                  return;
               }
            } finally {
               var4.close();
            }

         } catch (IOException var14) {
         }
      }

      private static class PathIterator implements Collection {
         private int pos;
         private final String path;
         private final String emptyPathDefault;

         public PathIterator(String var1, String var2) {
            this.pos = 0;
            this.path = var1;
            this.emptyPathDefault = var2;
         }

         public PathIterator(String var1) {
            this(var1, (String)null);
         }

         public Iterator iterator() {
            return new Iterator() {
               public boolean hasNext() {
                  return PathIterator.this.pos <= PathIterator.this.path.length();
               }

               public String next() {
                  int var1 = PathIterator.this.pos;
                  int var2 = PathIterator.this.path.indexOf(File.pathSeparator, var1);
                  if (var2 == -1) {
                     var2 = PathIterator.this.path.length();
                  }

                  PathIterator.this.pos = var2 + 1;
                  return var1 == var2 && PathIterator.this.emptyPathDefault != null ? PathIterator.this.emptyPathDefault : PathIterator.this.path.substring(var1, var2);
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         public int size() {
            throw new UnsupportedOperationException();
         }

         public boolean isEmpty() {
            throw new UnsupportedOperationException();
         }

         public boolean contains(Object var1) {
            throw new UnsupportedOperationException();
         }

         public Object[] toArray() {
            throw new UnsupportedOperationException();
         }

         public Object[] toArray(Object[] var1) {
            throw new UnsupportedOperationException();
         }

         public boolean add(String var1) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object var1) {
            throw new UnsupportedOperationException();
         }

         public boolean containsAll(Collection var1) {
            throw new UnsupportedOperationException();
         }

         public boolean addAll(Collection var1) {
            throw new UnsupportedOperationException();
         }

         public boolean removeAll(Collection var1) {
            throw new UnsupportedOperationException();
         }

         public boolean retainAll(Collection var1) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            throw new UnsupportedOperationException();
         }

         public boolean equals(Object var1) {
            throw new UnsupportedOperationException();
         }

         public int hashCode() {
            throw new UnsupportedOperationException();
         }
      }
   }
}
