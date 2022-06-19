package com.sun.tools.javac.processing;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Pair;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

public class JavacMessager implements Messager {
   Log log;
   JavacProcessingEnvironment processingEnv;
   int errorCount = 0;
   int warningCount = 0;

   JavacMessager(Context var1, JavacProcessingEnvironment var2) {
      this.log = Log.instance(var1);
      this.processingEnv = var2;
   }

   public void printMessage(Diagnostic.Kind var1, CharSequence var2) {
      this.printMessage(var1, var2, (Element)null, (AnnotationMirror)null, (AnnotationValue)null);
   }

   public void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3) {
      this.printMessage(var1, var2, var3, (AnnotationMirror)null, (AnnotationValue)null);
   }

   public void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3, AnnotationMirror var4) {
      this.printMessage(var1, var2, var3, var4, (AnnotationValue)null);
   }

   public void printMessage(Diagnostic.Kind var1, CharSequence var2, Element var3, AnnotationMirror var4, AnnotationValue var5) {
      JavaFileObject var6 = null;
      JavaFileObject var7 = null;
      JCDiagnostic.DiagnosticPosition var8 = null;
      JavacElements var9 = this.processingEnv.getElementUtils();
      Pair var10 = var9.getTreeAndTopLevel(var3, var4, var5);
      if (var10 != null) {
         var7 = ((JCTree.JCCompilationUnit)var10.snd).sourcefile;
         if (var7 != null) {
            var6 = this.log.useSource(var7);
            var8 = ((JCTree)var10.fst).pos();
         }
      }

      try {
         switch (var1) {
            case ERROR:
               ++this.errorCount;
               boolean var11 = this.log.multipleErrors;
               this.log.multipleErrors = true;

               try {
                  this.log.error(var8, "proc.messager", new Object[]{var2.toString()});
                  break;
               } finally {
                  this.log.multipleErrors = var11;
               }
            case WARNING:
               ++this.warningCount;
               this.log.warning(var8, "proc.messager", new Object[]{var2.toString()});
               break;
            case MANDATORY_WARNING:
               ++this.warningCount;
               this.log.mandatoryWarning(var8, "proc.messager", new Object[]{var2.toString()});
               break;
            default:
               this.log.note(var8, "proc.messager", new Object[]{var2.toString()});
         }
      } finally {
         if (var7 != null) {
            this.log.useSource(var6);
         }

      }

   }

   public void printError(String var1) {
      this.printMessage(Kind.ERROR, var1);
   }

   public void printWarning(String var1) {
      this.printMessage(Kind.WARNING, var1);
   }

   public void printNotice(String var1) {
      this.printMessage(Kind.NOTE, var1);
   }

   public boolean errorRaised() {
      return this.errorCount > 0;
   }

   public int errorCount() {
      return this.errorCount;
   }

   public int warningCount() {
      return this.warningCount;
   }

   public void newRound(Context var1) {
      this.log = Log.instance(var1);
      this.errorCount = 0;
   }

   public String toString() {
      return "javac Messager";
   }
}
