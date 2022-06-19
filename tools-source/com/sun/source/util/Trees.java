package com.sun.source.util;

import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import java.lang.reflect.Method;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import jdk.Exported;

@Exported
public abstract class Trees {
   public static Trees instance(JavaCompiler.CompilationTask var0) {
      String var1 = var0.getClass().getName();
      if (!var1.equals("com.sun.tools.javac.api.JavacTaskImpl") && !var1.equals("com.sun.tools.javac.api.BasicJavacTask")) {
         throw new IllegalArgumentException();
      } else {
         return getJavacTrees(JavaCompiler.CompilationTask.class, var0);
      }
   }

   public static Trees instance(ProcessingEnvironment var0) {
      if (!var0.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
         throw new IllegalArgumentException();
      } else {
         return getJavacTrees(ProcessingEnvironment.class, var0);
      }
   }

   static Trees getJavacTrees(Class var0, Object var1) {
      try {
         ClassLoader var2 = var1.getClass().getClassLoader();
         Class var3 = Class.forName("com.sun.tools.javac.api.JavacTrees", false, var2);
         var0 = Class.forName(var0.getName(), false, var2);
         Method var4 = var3.getMethod("instance", var0);
         return (Trees)var4.invoke((Object)null, var1);
      } catch (Throwable var5) {
         throw new AssertionError(var5);
      }
   }

   public abstract SourcePositions getSourcePositions();

   public abstract Tree getTree(Element var1);

   public abstract ClassTree getTree(TypeElement var1);

   public abstract MethodTree getTree(ExecutableElement var1);

   public abstract Tree getTree(Element var1, AnnotationMirror var2);

   public abstract Tree getTree(Element var1, AnnotationMirror var2, AnnotationValue var3);

   public abstract TreePath getPath(CompilationUnitTree var1, Tree var2);

   public abstract TreePath getPath(Element var1);

   public abstract TreePath getPath(Element var1, AnnotationMirror var2);

   public abstract TreePath getPath(Element var1, AnnotationMirror var2, AnnotationValue var3);

   public abstract Element getElement(TreePath var1);

   public abstract TypeMirror getTypeMirror(TreePath var1);

   public abstract Scope getScope(TreePath var1);

   public abstract String getDocComment(TreePath var1);

   public abstract boolean isAccessible(Scope var1, TypeElement var2);

   public abstract boolean isAccessible(Scope var1, Element var2, DeclaredType var3);

   public abstract TypeMirror getOriginalType(ErrorType var1);

   public abstract void printMessage(Diagnostic.Kind var1, CharSequence var2, Tree var3, CompilationUnitTree var4);

   public abstract TypeMirror getLub(CatchTree var1);
}
