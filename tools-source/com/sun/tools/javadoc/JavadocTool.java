package com.sun.tools.javadoc;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Abort;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class JavadocTool extends JavaCompiler {
   DocEnv docenv;
   final Messager messager;
   final JavadocClassReader javadocReader;
   final JavadocEnter javadocEnter;
   final Set uniquefiles;
   static final boolean surrogatesSupported = surrogatesSupported();

   protected JavadocTool(Context var1) {
      super(var1);
      this.messager = Messager.instance0(var1);
      this.javadocReader = JavadocClassReader.instance0(var1);
      this.javadocEnter = JavadocEnter.instance0(var1);
      this.uniquefiles = new HashSet();
   }

   protected boolean keepComments() {
      return true;
   }

   public static JavadocTool make0(Context var0) {
      Messager var1 = null;

      try {
         JavadocClassReader.preRegister(var0);
         JavadocEnter.preRegister(var0);
         JavadocMemberEnter.preRegister(var0);
         JavadocTodo.preRegister(var0);
         var1 = Messager.instance0(var0);
         return new JavadocTool(var0);
      } catch (Symbol.CompletionFailure var3) {
         var1.error(-1, var3.getMessage(), new Object[0]);
         return null;
      }
   }

   public RootDocImpl getRootDocImpl(String var1, String var2, ModifierFilter var3, List var4, List var5, Iterable var6, boolean var7, List var8, List var9, boolean var10, boolean var11, boolean var12) throws IOException {
      this.docenv = DocEnv.instance(this.context);
      this.docenv.showAccess = var3;
      this.docenv.quiet = var12;
      this.docenv.breakiterator = var7;
      this.docenv.setLocale(var1);
      this.docenv.setEncoding(var2);
      this.docenv.docClasses = var10;
      this.docenv.legacyDoclet = var11;
      this.javadocReader.sourceCompleter = var10 ? null : this.thisCompleter;
      ListBuffer var13 = new ListBuffer();
      ListBuffer var14 = new ListBuffer();
      ListBuffer var15 = new ListBuffer();

      try {
         StandardJavaFileManager var16 = this.docenv.fileManager instanceof StandardJavaFileManager ? (StandardJavaFileManager)this.docenv.fileManager : null;

         for(List var17 = var4; var17.nonEmpty(); var17 = var17.tail) {
            String var18 = (String)var17.head;
            if (!var10 && var16 != null && var18.endsWith(".java") && (new File(var18)).exists()) {
               JavaFileObject var19 = (JavaFileObject)var16.getJavaFileObjects(new String[]{var18}).iterator().next();
               this.parse(var19, var14, true);
            } else if (this.isValidPackageName(var18)) {
               var13 = var13.append(var18);
            } else if (var18.endsWith(".java")) {
               if (var16 == null) {
                  throw new IllegalArgumentException();
               }

               this.docenv.error((DocImpl)null, "main.file_not_found", var18);
            } else {
               this.docenv.error((DocImpl)null, "main.illegal_package_name", var18);
            }
         }

         Iterator var21 = var6.iterator();

         while(var21.hasNext()) {
            JavaFileObject var23 = (JavaFileObject)var21.next();
            this.parse(var23, var14, true);
         }

         if (!var10) {
            Map var22 = this.searchSubPackages(var8, var13, var9);

            for(List var24 = var13.toList(); var24.nonEmpty(); var24 = var24.tail) {
               String var25 = (String)var24.head;
               this.parsePackageClasses(var25, (List)var22.get(var25), var15, var9);
            }

            if (this.messager.nerrors() != 0) {
               return null;
            }

            this.docenv.notice("main.Building_tree");
            this.javadocEnter.main(var14.toList().appendList(var15.toList()));
         }
      } catch (Abort var20) {
      }

      if (this.messager.nerrors() != 0) {
         return null;
      } else {
         return var10 ? new RootDocImpl(this.docenv, var4, var5) : new RootDocImpl(this.docenv, this.listClasses(var14.toList()), var13.toList(), var5);
      }
   }

   boolean isValidPackageName(String var1) {
      int var2;
      while((var2 = var1.indexOf(46)) != -1) {
         if (!isValidClassName(var1.substring(0, var2))) {
            return false;
         }

         var1 = var1.substring(var2 + 1);
      }

      return isValidClassName(var1);
   }

   private void parsePackageClasses(String var1, List var2, ListBuffer var3, List var4) throws IOException {
      if (!var4.contains(var1)) {
         this.docenv.notice("main.Loading_source_files_for_package", var1);
         if (var2 == null) {
            StandardLocation var5 = this.docenv.fileManager.hasLocation(StandardLocation.SOURCE_PATH) ? StandardLocation.SOURCE_PATH : StandardLocation.CLASS_PATH;
            ListBuffer var6 = new ListBuffer();
            Iterator var7 = this.docenv.fileManager.list(var5, var1, EnumSet.of(Kind.SOURCE), false).iterator();

            while(var7.hasNext()) {
               JavaFileObject var8 = (JavaFileObject)var7.next();
               String var9 = this.docenv.fileManager.inferBinaryName(var5, var8);
               String var10 = this.getSimpleName(var9);
               if (isValidClassName(var10)) {
                  var6.append(var8);
               }
            }

            var2 = var6.toList();
         }

         if (var2.nonEmpty()) {
            Iterator var11 = var2.iterator();

            while(var11.hasNext()) {
               JavaFileObject var12 = (JavaFileObject)var11.next();
               this.parse(var12, var3, false);
            }
         } else {
            this.messager.warning(Messager.NOPOS, "main.no_source_files_for_package", var1.replace(File.separatorChar, '.'));
         }

      }
   }

   private void parse(JavaFileObject var1, ListBuffer var2, boolean var3) {
      if (this.uniquefiles.add(var1)) {
         if (var3) {
            this.docenv.notice("main.Loading_source_file", var1.getName());
         }

         var2.append(this.parse(var1));
      }

   }

   private Map searchSubPackages(List var1, ListBuffer var2, List var3) throws IOException {
      HashMap var4 = new HashMap();
      HashMap var5 = new HashMap();
      var5.put("", true);
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         var5.put(var7, false);
      }

      StandardLocation var8 = this.docenv.fileManager.hasLocation(StandardLocation.SOURCE_PATH) ? StandardLocation.SOURCE_PATH : StandardLocation.CLASS_PATH;
      this.searchSubPackages(var1, var5, var2, var4, var8, EnumSet.of(Kind.SOURCE));
      return var4;
   }

   private void searchSubPackages(List var1, Map var2, ListBuffer var3, Map var4, StandardLocation var5, Set var6) throws IOException {
      Iterator var7 = var1.iterator();

      while(true) {
         String var8;
         do {
            if (!var7.hasNext()) {
               return;
            }

            var8 = (String)var7.next();
         } while(!this.isIncluded(var8, var2));

         Iterator var9 = this.docenv.fileManager.list(var5, var8, var6, true).iterator();

         while(var9.hasNext()) {
            JavaFileObject var10 = (JavaFileObject)var9.next();
            String var11 = this.docenv.fileManager.inferBinaryName(var5, var10);
            String var12 = this.getPackageName(var11);
            String var13 = this.getSimpleName(var11);
            if (this.isIncluded(var12, var2) && isValidClassName(var13)) {
               List var14 = (List)var4.get(var12);
               var14 = var14 == null ? List.of(var10) : var14.prepend(var10);
               var4.put(var12, var14);
               if (!var3.contains(var12)) {
                  var3.add(var12);
               }
            }
         }
      }
   }

   private String getPackageName(String var1) {
      int var2 = var1.lastIndexOf(".");
      return var2 == -1 ? "" : var1.substring(0, var2);
   }

   private String getSimpleName(String var1) {
      int var2 = var1.lastIndexOf(".");
      return var2 == -1 ? var1 : var1.substring(var2 + 1);
   }

   private boolean isIncluded(String var1, Map var2) {
      Boolean var3 = (Boolean)var2.get(var1);
      if (var3 == null) {
         var3 = this.isIncluded(this.getPackageName(var1), var2);
         var2.put(var1, var3);
      }

      return var3;
   }

   private void searchSubPackage(String var1, ListBuffer var2, List var3, Collection var4) {
      if (!var3.contains(var1)) {
         String var5 = var1.replace('.', File.separatorChar);
         boolean var6 = false;
         Iterator var7 = var4.iterator();

         while(true) {
            File var9;
            String[] var10;
            do {
               if (!var7.hasNext()) {
                  return;
               }

               File var8 = (File)var7.next();
               var9 = new File(var8, var5);
               var10 = var9.list();
            } while(var10 == null);

            String[] var11 = var10;
            int var12 = var10.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               String var14 = var11[var13];
               if (!var6 && (isValidJavaSourceFile(var14) || isValidJavaClassFile(var14)) && !var2.contains(var1)) {
                  var2.append(var1);
                  var6 = true;
               } else if (isValidClassName(var14) && (new File(var9, var14)).isDirectory()) {
                  this.searchSubPackage(var1 + "." + var14, var2, var3, var4);
               }
            }
         }
      }
   }

   private static boolean isValidJavaClassFile(String var0) {
      if (!var0.endsWith(".class")) {
         return false;
      } else {
         String var1 = var0.substring(0, var0.length() - ".class".length());
         return isValidClassName(var1);
      }
   }

   private static boolean isValidJavaSourceFile(String var0) {
      if (!var0.endsWith(".java")) {
         return false;
      } else {
         String var1 = var0.substring(0, var0.length() - ".java".length());
         return isValidClassName(var1);
      }
   }

   private static boolean surrogatesSupported() {
      try {
         boolean var0 = Character.isHighSurrogate('a');
         return true;
      } catch (NoSuchMethodError var1) {
         return false;
      }
   }

   public static boolean isValidClassName(String var0) {
      if (var0.length() < 1) {
         return false;
      } else if (var0.equals("package-info")) {
         return true;
      } else {
         int var1;
         if (surrogatesSupported) {
            var1 = var0.codePointAt(0);
            if (!Character.isJavaIdentifierStart(var1)) {
               return false;
            }

            for(int var2 = Character.charCount(var1); var2 < var0.length(); var2 += Character.charCount(var1)) {
               var1 = var0.codePointAt(var2);
               if (!Character.isJavaIdentifierPart(var1)) {
                  return false;
               }
            }
         } else {
            if (!Character.isJavaIdentifierStart(var0.charAt(0))) {
               return false;
            }

            for(var1 = 1; var1 < var0.length(); ++var1) {
               if (!Character.isJavaIdentifierPart(var0.charAt(var1))) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   List listClasses(List var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var3.next();
         Iterator var5 = var4.defs.iterator();

         while(var5.hasNext()) {
            JCTree var6 = (JCTree)var5.next();
            if (var6.hasTag(JCTree.Tag.CLASSDEF)) {
               var2.append((JCTree.JCClassDecl)var6);
            }
         }
      }

      return var2.toList();
   }
}
