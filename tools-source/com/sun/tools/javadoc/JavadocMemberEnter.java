package com.sun.tools.javadoc;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public class JavadocMemberEnter extends MemberEnter {
   final DocEnv docenv;

   public static JavadocMemberEnter instance0(Context var0) {
      Object var1 = (MemberEnter)var0.get(memberEnterKey);
      if (var1 == null) {
         var1 = new JavadocMemberEnter(var0);
      }

      return (JavadocMemberEnter)var1;
   }

   public static void preRegister(Context var0) {
      var0.put(memberEnterKey, new Context.Factory() {
         public MemberEnter make(Context var1) {
            return new JavadocMemberEnter(var1);
         }
      });
   }

   protected JavadocMemberEnter(Context var1) {
      super(var1);
      this.docenv = DocEnv.instance(var1);
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      super.visitMethodDef(var1);
      Symbol.MethodSymbol var2 = var1.sym;
      if (var2 != null && var2.kind == 16) {
         TreePath var3 = this.docenv.getTreePath(this.env.toplevel, this.env.enclClass, var1);
         if (var2.isConstructor()) {
            this.docenv.makeConstructorDoc(var2, var3);
         } else if (isAnnotationTypeElement(var2)) {
            this.docenv.makeAnnotationTypeElementDoc(var2, var3);
         } else {
            this.docenv.makeMethodDoc(var2, var3);
         }

         var1.body = null;
      }
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      if (var1.init != null) {
         boolean var2 = (var1.mods.flags & 16L) != 0L || (this.env.enclClass.mods.flags & 512L) != 0L;
         if (!var2 || containsNonConstantExpression(var1.init)) {
            var1.init = null;
         }
      }

      super.visitVarDef(var1);
      if (var1.sym != null && var1.sym.kind == 4 && !isParameter(var1.sym)) {
         this.docenv.makeFieldDoc(var1.sym, this.docenv.getTreePath(this.env.toplevel, this.env.enclClass, var1));
      }

   }

   private static boolean isAnnotationTypeElement(Symbol.MethodSymbol var0) {
      return ClassDocImpl.isAnnotationType(var0.enclClass());
   }

   private static boolean isParameter(Symbol.VarSymbol var0) {
      return (var0.flags() & 8589934592L) != 0L;
   }

   private static boolean containsNonConstantExpression(JCTree.JCExpression var0) {
      return (new MaybeConstantExpressionScanner()).containsNonConstantExpression(var0);
   }

   private static class MaybeConstantExpressionScanner extends JCTree.Visitor {
      boolean maybeConstantExpr;

      private MaybeConstantExpressionScanner() {
         this.maybeConstantExpr = true;
      }

      public boolean containsNonConstantExpression(JCTree.JCExpression var1) {
         this.scan(var1);
         return !this.maybeConstantExpr;
      }

      public void scan(JCTree var1) {
         if (this.maybeConstantExpr && var1 != null) {
            var1.accept(this);
         }

      }

      public void visitTree(JCTree var1) {
         this.maybeConstantExpr = false;
      }

      public void visitBinary(JCTree.JCBinary var1) {
         switch (var1.getTag()) {
            default:
               this.maybeConstantExpr = false;
            case MUL:
            case DIV:
            case MOD:
            case PLUS:
            case MINUS:
            case SL:
            case SR:
            case USR:
            case LT:
            case LE:
            case GT:
            case GE:
            case EQ:
            case NE:
            case BITAND:
            case BITXOR:
            case BITOR:
            case AND:
            case OR:
         }
      }

      public void visitConditional(JCTree.JCConditional var1) {
         this.scan(var1.cond);
         this.scan(var1.truepart);
         this.scan(var1.falsepart);
      }

      public void visitIdent(JCTree.JCIdent var1) {
      }

      public void visitLiteral(JCTree.JCLiteral var1) {
      }

      public void visitParens(JCTree.JCParens var1) {
         this.scan(var1.expr);
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         this.scan(var1.selected);
      }

      public void visitTypeCast(JCTree.JCTypeCast var1) {
         this.scan(var1.clazz);
         this.scan(var1.expr);
      }

      public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
      }

      public void visitUnary(JCTree.JCUnary var1) {
         switch (var1.getTag()) {
            default:
               this.maybeConstantExpr = false;
            case POS:
            case NEG:
            case COMPL:
            case NOT:
         }
      }

      // $FF: synthetic method
      MaybeConstantExpressionScanner(Object var1) {
         this();
      }
   }
}
