package com.sun.tools.javadoc;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import javax.tools.JavaFileObject.Kind;

public class JavadocEnter extends Enter {
   final Messager messager;
   final DocEnv docenv;

   public static JavadocEnter instance0(Context var0) {
      Object var1 = (Enter)var0.get(enterKey);
      if (var1 == null) {
         var1 = new JavadocEnter(var0);
      }

      return (JavadocEnter)var1;
   }

   public static void preRegister(Context var0) {
      var0.put(enterKey, new Context.Factory() {
         public Enter make(Context var1) {
            return new JavadocEnter(var1);
         }
      });
   }

   protected JavadocEnter(Context var1) {
      super(var1);
      this.messager = Messager.instance0(var1);
      this.docenv = DocEnv.instance(var1);
   }

   public void main(List var1) {
      int var2 = this.messager.nerrors;
      super.main(var1);
      Messager var10000 = this.messager;
      var10000.nwarnings += this.messager.nerrors - var2;
      this.messager.nerrors = var2;
   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      super.visitTopLevel(var1);
      if (var1.sourcefile.isNameCompatible("package-info", Kind.SOURCE)) {
         this.docenv.makePackageDoc(var1.packge, this.docenv.getTreePath(var1));
      }

   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      super.visitClassDef(var1);
      if (var1.sym != null) {
         if (var1.sym.kind == 2 || var1.sym.kind == 63) {
            Symbol.ClassSymbol var2 = var1.sym;
            this.docenv.makeClassDoc(var2, this.docenv.getTreePath(this.env.toplevel, var1));
         }

      }
   }

   protected void duplicateClass(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
   }
}
