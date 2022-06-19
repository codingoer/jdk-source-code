package com.sun.tools.doclint;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.StringUtils;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;

public class Env {
   final Messages messages = new Messages(this);
   int implicitHeaderLevel = 0;
   Set customTags;
   DocTrees trees;
   Elements elements;
   Types types;
   TypeMirror java_lang_Error;
   TypeMirror java_lang_RuntimeException;
   TypeMirror java_lang_Throwable;
   TypeMirror java_lang_Void;
   TreePath currPath;
   Element currElement;
   DocCommentTree currDocComment;
   AccessKind currAccess;
   Set currOverriddenMethods;

   Env() {
   }

   void init(JavacTask var1) {
      this.init(DocTrees.instance((JavaCompiler.CompilationTask)var1), var1.getElements(), var1.getTypes());
   }

   void init(DocTrees var1, Elements var2, Types var3) {
      this.trees = var1;
      this.elements = var2;
      this.types = var3;
      this.java_lang_Error = var2.getTypeElement("java.lang.Error").asType();
      this.java_lang_RuntimeException = var2.getTypeElement("java.lang.RuntimeException").asType();
      this.java_lang_Throwable = var2.getTypeElement("java.lang.Throwable").asType();
      this.java_lang_Void = var2.getTypeElement("java.lang.Void").asType();
   }

   void setImplicitHeaders(int var1) {
      this.implicitHeaderLevel = var1;
   }

   void setCustomTags(String var1) {
      this.customTags = new LinkedHashSet();
      String[] var2 = var1.split(",");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!var5.isEmpty()) {
            this.customTags.add(var5);
         }
      }

   }

   void setCurrent(TreePath var1, DocCommentTree var2) {
      this.currPath = var1;
      this.currDocComment = var2;
      this.currElement = this.trees.getElement(this.currPath);
      this.currOverriddenMethods = ((JavacTypes)this.types).getOverriddenMethods(this.currElement);
      AccessKind var3 = Env.AccessKind.PUBLIC;

      for(TreePath var4 = var1; var4 != null; var4 = var4.getParentPath()) {
         Element var5 = this.trees.getElement(var4);
         if (var5 != null && var5.getKind() != ElementKind.PACKAGE) {
            var3 = (AccessKind)this.min(var3, Env.AccessKind.of(var5.getModifiers()));
         }
      }

      this.currAccess = var3;
   }

   AccessKind getAccessKind() {
      return this.currAccess;
   }

   long getPos(TreePath var1) {
      return (long)((JCTree)var1.getLeaf()).pos;
   }

   long getStartPos(TreePath var1) {
      DocSourcePositions var2 = this.trees.getSourcePositions();
      return var2.getStartPosition(var1.getCompilationUnit(), var1.getLeaf());
   }

   private Comparable min(Comparable var1, Comparable var2) {
      return var1 == null ? var2 : (var2 == null ? var1 : (var1.compareTo(var2) <= 0 ? var1 : var2));
   }

   public static enum AccessKind {
      PRIVATE,
      PACKAGE,
      PROTECTED,
      PUBLIC;

      static boolean accepts(String var0) {
         AccessKind[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            AccessKind var4 = var1[var3];
            if (var0.equals(StringUtils.toLowerCase(var4.name()))) {
               return true;
            }
         }

         return false;
      }

      static AccessKind of(Set var0) {
         if (var0.contains(Modifier.PUBLIC)) {
            return PUBLIC;
         } else if (var0.contains(Modifier.PROTECTED)) {
            return PROTECTED;
         } else {
            return var0.contains(Modifier.PRIVATE) ? PRIVATE : PACKAGE;
         }
      }
   }
}
