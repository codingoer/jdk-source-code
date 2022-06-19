package com.sun.tools.javac.tree;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.Iterator;

public class TreeInfo {
   protected static final Context.Key treeInfoKey = new Context.Key();
   private Name[] opname = new Name[JCTree.Tag.getNumberOfOperators()];
   public static final int notExpression = -1;
   public static final int noPrec = 0;
   public static final int assignPrec = 1;
   public static final int assignopPrec = 2;
   public static final int condPrec = 3;
   public static final int orPrec = 4;
   public static final int andPrec = 5;
   public static final int bitorPrec = 6;
   public static final int bitxorPrec = 7;
   public static final int bitandPrec = 8;
   public static final int eqPrec = 9;
   public static final int ordPrec = 10;
   public static final int shiftPrec = 11;
   public static final int addPrec = 12;
   public static final int mulPrec = 13;
   public static final int prefixPrec = 14;
   public static final int postfixPrec = 15;
   public static final int precCount = 16;

   public static TreeInfo instance(Context var0) {
      TreeInfo var1 = (TreeInfo)var0.get(treeInfoKey);
      if (var1 == null) {
         var1 = new TreeInfo(var0);
      }

      return var1;
   }

   private void setOpname(JCTree.Tag var1, String var2, Names var3) {
      this.setOpname(var1, var3.fromString(var2));
   }

   private void setOpname(JCTree.Tag var1, Name var2) {
      this.opname[var1.operatorIndex()] = var2;
   }

   private TreeInfo(Context var1) {
      var1.put((Context.Key)treeInfoKey, (Object)this);
      Names var2 = Names.instance(var1);
      this.setOpname(JCTree.Tag.POS, "+++", var2);
      this.setOpname(JCTree.Tag.NEG, "---", var2);
      this.setOpname(JCTree.Tag.NOT, "!", var2);
      this.setOpname(JCTree.Tag.COMPL, "~", var2);
      this.setOpname(JCTree.Tag.PREINC, "++", var2);
      this.setOpname(JCTree.Tag.PREDEC, "--", var2);
      this.setOpname(JCTree.Tag.POSTINC, "++", var2);
      this.setOpname(JCTree.Tag.POSTDEC, "--", var2);
      this.setOpname(JCTree.Tag.NULLCHK, "<*nullchk*>", var2);
      this.setOpname(JCTree.Tag.OR, "||", var2);
      this.setOpname(JCTree.Tag.AND, "&&", var2);
      this.setOpname(JCTree.Tag.EQ, "==", var2);
      this.setOpname(JCTree.Tag.NE, "!=", var2);
      this.setOpname(JCTree.Tag.LT, "<", var2);
      this.setOpname(JCTree.Tag.GT, ">", var2);
      this.setOpname(JCTree.Tag.LE, "<=", var2);
      this.setOpname(JCTree.Tag.GE, ">=", var2);
      this.setOpname(JCTree.Tag.BITOR, "|", var2);
      this.setOpname(JCTree.Tag.BITXOR, "^", var2);
      this.setOpname(JCTree.Tag.BITAND, "&", var2);
      this.setOpname(JCTree.Tag.SL, "<<", var2);
      this.setOpname(JCTree.Tag.SR, ">>", var2);
      this.setOpname(JCTree.Tag.USR, ">>>", var2);
      this.setOpname(JCTree.Tag.PLUS, "+", var2);
      this.setOpname(JCTree.Tag.MINUS, var2.hyphen);
      this.setOpname(JCTree.Tag.MUL, var2.asterisk);
      this.setOpname(JCTree.Tag.DIV, var2.slash);
      this.setOpname(JCTree.Tag.MOD, "%", var2);
   }

   public static List args(JCTree var0) {
      switch (var0.getTag()) {
         case APPLY:
            return ((JCTree.JCMethodInvocation)var0).args;
         case NEWCLASS:
            return ((JCTree.JCNewClass)var0).args;
         default:
            return null;
      }
   }

   public Name operatorName(JCTree.Tag var1) {
      return this.opname[var1.operatorIndex()];
   }

   public static boolean isConstructor(JCTree var0) {
      if (var0.hasTag(JCTree.Tag.METHODDEF)) {
         Name var1 = ((JCTree.JCMethodDecl)var0).name;
         return var1 == var1.table.names.init;
      } else {
         return false;
      }
   }

   public static boolean isReceiverParam(JCTree var0) {
      if (var0.hasTag(JCTree.Tag.VARDEF)) {
         return ((JCTree.JCVariableDecl)var0).nameexpr != null;
      } else {
         return false;
      }
   }

   public static boolean hasConstructors(List var0) {
      for(List var1 = var0; var1.nonEmpty(); var1 = var1.tail) {
         if (isConstructor((JCTree)var1.head)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isMultiCatch(JCTree.JCCatch var0) {
      return var0.param.vartype.hasTag(JCTree.Tag.TYPEUNION);
   }

   public static boolean isSyntheticInit(JCTree var0) {
      if (var0.hasTag(JCTree.Tag.EXEC)) {
         JCTree.JCExpressionStatement var1 = (JCTree.JCExpressionStatement)var0;
         if (var1.expr.hasTag(JCTree.Tag.ASSIGN)) {
            JCTree.JCAssign var2 = (JCTree.JCAssign)var1.expr;
            if (var2.lhs.hasTag(JCTree.Tag.SELECT)) {
               JCTree.JCFieldAccess var3 = (JCTree.JCFieldAccess)var2.lhs;
               if (var3.sym != null && (var3.sym.flags() & 4096L) != 0L) {
                  Name var4 = name(var3.selected);
                  if (var4 != null && var4 == var4.table.names._this) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static Name calledMethodName(JCTree var0) {
      if (var0.hasTag(JCTree.Tag.EXEC)) {
         JCTree.JCExpressionStatement var1 = (JCTree.JCExpressionStatement)var0;
         if (var1.expr.hasTag(JCTree.Tag.APPLY)) {
            Name var2 = name(((JCTree.JCMethodInvocation)var1.expr).meth);
            return var2;
         }
      }

      return null;
   }

   public static boolean isSelfCall(JCTree var0) {
      Name var1 = calledMethodName(var0);
      if (var1 == null) {
         return false;
      } else {
         Names var2 = var1.table.names;
         return var1 == var2._this || var1 == var2._super;
      }
   }

   public static boolean isSuperCall(JCTree var0) {
      Name var1 = calledMethodName(var0);
      if (var1 != null) {
         Names var2 = var1.table.names;
         return var1 == var2._super;
      } else {
         return false;
      }
   }

   public static boolean isInitialConstructor(JCTree var0) {
      JCTree.JCMethodInvocation var1 = firstConstructorCall(var0);
      if (var1 == null) {
         return false;
      } else {
         Name var2 = name(var1.meth);
         return var2 == null || var2 != var2.table.names._this;
      }
   }

   public static JCTree.JCMethodInvocation firstConstructorCall(JCTree var0) {
      if (!var0.hasTag(JCTree.Tag.METHODDEF)) {
         return null;
      } else {
         JCTree.JCMethodDecl var1 = (JCTree.JCMethodDecl)var0;
         Names var2 = var1.name.table.names;
         if (var1.name != var2.init) {
            return null;
         } else if (var1.body == null) {
            return null;
         } else {
            List var3;
            for(var3 = var1.body.stats; var3.nonEmpty() && isSyntheticInit((JCTree)var3.head); var3 = var3.tail) {
            }

            if (var3.isEmpty()) {
               return null;
            } else if (!((JCTree.JCStatement)var3.head).hasTag(JCTree.Tag.EXEC)) {
               return null;
            } else {
               JCTree.JCExpressionStatement var4 = (JCTree.JCExpressionStatement)var3.head;
               return !var4.expr.hasTag(JCTree.Tag.APPLY) ? null : (JCTree.JCMethodInvocation)var4.expr;
            }
         }
      }
   }

   public static boolean isDiamond(JCTree var0) {
      switch (var0.getTag()) {
         case NEWCLASS:
            return isDiamond(((JCTree.JCNewClass)var0).clazz);
         case TYPEAPPLY:
            return ((JCTree.JCTypeApply)var0).getTypeArguments().isEmpty();
         case ANNOTATED_TYPE:
            return isDiamond(((JCTree.JCAnnotatedType)var0).underlyingType);
         default:
            return false;
      }
   }

   public static boolean isEnumInit(JCTree var0) {
      switch (var0.getTag()) {
         case VARDEF:
            return (((JCTree.JCVariableDecl)var0).mods.flags & 16384L) != 0L;
         default:
            return false;
      }
   }

   public static void setPolyKind(JCTree var0, JCTree.JCPolyExpression.PolyKind var1) {
      switch (var0.getTag()) {
         case APPLY:
            ((JCTree.JCMethodInvocation)var0).polyKind = var1;
            break;
         case NEWCLASS:
            ((JCTree.JCNewClass)var0).polyKind = var1;
            break;
         case REFERENCE:
            ((JCTree.JCMemberReference)var0).refPolyKind = var1;
            break;
         default:
            throw new AssertionError("Unexpected tree: " + var0);
      }

   }

   public static void setVarargsElement(JCTree var0, Type var1) {
      switch (var0.getTag()) {
         case APPLY:
            ((JCTree.JCMethodInvocation)var0).varargsElement = var1;
            break;
         case NEWCLASS:
            ((JCTree.JCNewClass)var0).varargsElement = var1;
            break;
         case REFERENCE:
            ((JCTree.JCMemberReference)var0).varargsElement = var1;
            break;
         default:
            throw new AssertionError("Unexpected tree: " + var0);
      }

   }

   public static boolean isExpressionStatement(JCTree.JCExpression var0) {
      switch (var0.getTag()) {
         case APPLY:
         case NEWCLASS:
         case PREINC:
         case PREDEC:
         case POSTINC:
         case POSTDEC:
         case ASSIGN:
         case BITOR_ASG:
         case BITXOR_ASG:
         case BITAND_ASG:
         case SL_ASG:
         case SR_ASG:
         case USR_ASG:
         case PLUS_ASG:
         case MINUS_ASG:
         case MUL_ASG:
         case DIV_ASG:
         case MOD_ASG:
         case ERRONEOUS:
            return true;
         case TYPEAPPLY:
         case ANNOTATED_TYPE:
         case VARDEF:
         case REFERENCE:
         default:
            return false;
      }
   }

   public static boolean isStaticSelector(JCTree var0, Names var1) {
      if (var0 == null) {
         return false;
      } else {
         switch (var0.getTag()) {
            case TYPEAPPLY:
            case TYPEARRAY:
               return true;
            case ANNOTATED_TYPE:
               return isStaticSelector(((JCTree.JCAnnotatedType)var0).underlyingType, var1);
            case IDENT:
               JCTree.JCIdent var2 = (JCTree.JCIdent)var0;
               return var2.name != var1._this && var2.name != var1._super && isStaticSym(var0);
            case SELECT:
               return isStaticSym(var0) && isStaticSelector(((JCTree.JCFieldAccess)var0).selected, var1);
            default:
               return false;
         }
      }
   }

   private static boolean isStaticSym(JCTree var0) {
      Symbol var1 = symbol(var0);
      return var1.kind == 2 || var1.kind == 1;
   }

   public static boolean isNull(JCTree var0) {
      if (!var0.hasTag(JCTree.Tag.LITERAL)) {
         return false;
      } else {
         JCTree.JCLiteral var1 = (JCTree.JCLiteral)var0;
         return var1.typetag == TypeTag.BOT;
      }
   }

   public static boolean isInAnnotation(Env var0, JCTree var1) {
      TreePath var2 = TreePath.getPath((CompilationUnitTree)var0.toplevel, var1);
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Tree var4 = (Tree)var3.next();
            if (var4.getKind() == Tree.Kind.ANNOTATION) {
               return true;
            }
         }
      }

      return false;
   }

   public static String getCommentText(Env var0, JCTree var1) {
      DocCommentTable var2 = var1.hasTag(JCTree.Tag.TOPLEVEL) ? ((JCTree.JCCompilationUnit)var1).docComments : var0.toplevel.docComments;
      return var2 == null ? null : var2.getCommentText(var1);
   }

   public static DCTree.DCDocComment getCommentTree(Env var0, JCTree var1) {
      DocCommentTable var2 = var1.hasTag(JCTree.Tag.TOPLEVEL) ? ((JCTree.JCCompilationUnit)var1).docComments : var0.toplevel.docComments;
      return var2 == null ? null : var2.getCommentTree(var1);
   }

   public static int firstStatPos(JCTree var0) {
      return var0.hasTag(JCTree.Tag.BLOCK) && ((JCTree.JCBlock)var0).stats.nonEmpty() ? ((JCTree.JCStatement)((JCTree.JCBlock)var0).stats.head).pos : var0.pos;
   }

   public static int endPos(JCTree var0) {
      if (var0.hasTag(JCTree.Tag.BLOCK) && ((JCTree.JCBlock)var0).endpos != -1) {
         return ((JCTree.JCBlock)var0).endpos;
      } else if (var0.hasTag(JCTree.Tag.SYNCHRONIZED)) {
         return endPos(((JCTree.JCSynchronized)var0).body);
      } else if (var0.hasTag(JCTree.Tag.TRY)) {
         JCTree.JCTry var1 = (JCTree.JCTry)var0;
         return endPos(var1.finalizer != null ? var1.finalizer : (var1.catchers.nonEmpty() ? ((JCTree.JCCatch)var1.catchers.last()).body : var1.body));
      } else {
         return var0.pos;
      }
   }

   public static int getStartPos(JCTree var0) {
      if (var0 == null) {
         return -1;
      } else {
         switch (var0.getTag()) {
            case APPLY:
               return getStartPos(((JCTree.JCMethodInvocation)var0).meth);
            case NEWCLASS:
               JCTree.JCNewClass var6 = (JCTree.JCNewClass)var0;
               if (var6.encl != null) {
                  return getStartPos(var6.encl);
               }
               break;
            case TYPEAPPLY:
               return getStartPos(((JCTree.JCTypeApply)var0).clazz);
            case ANNOTATED_TYPE:
               JCTree.JCAnnotatedType var5 = (JCTree.JCAnnotatedType)var0;
               if (var5.annotations.nonEmpty()) {
                  if (!var5.underlyingType.hasTag(JCTree.Tag.TYPEARRAY) && !var5.underlyingType.hasTag(JCTree.Tag.SELECT)) {
                     return getStartPos((JCTree)var5.annotations.head);
                  }

                  return getStartPos(var5.underlyingType);
               }

               return getStartPos(var5.underlyingType);
            case VARDEF:
               JCTree.JCVariableDecl var4 = (JCTree.JCVariableDecl)var0;
               if (var4.mods.pos != -1) {
                  return var4.mods.pos;
               }

               if (var4.vartype == null) {
                  return var4.pos;
               }

               return getStartPos(var4.vartype);
            case REFERENCE:
            case PREINC:
            case PREDEC:
            case IDENT:
            default:
               break;
            case POSTINC:
            case POSTDEC:
               return getStartPos(((JCTree.JCUnary)var0).arg);
            case ASSIGN:
               return getStartPos(((JCTree.JCAssign)var0).lhs);
            case BITOR_ASG:
            case BITXOR_ASG:
            case BITAND_ASG:
            case SL_ASG:
            case SR_ASG:
            case USR_ASG:
            case PLUS_ASG:
            case MINUS_ASG:
            case MUL_ASG:
            case DIV_ASG:
            case MOD_ASG:
               return getStartPos(((JCTree.JCAssignOp)var0).lhs);
            case ERRONEOUS:
               JCTree.JCErroneous var3 = (JCTree.JCErroneous)var0;
               if (var3.errs != null && var3.errs.nonEmpty()) {
                  return getStartPos((JCTree)var3.errs.head);
               }
               break;
            case SELECT:
               return getStartPos(((JCTree.JCFieldAccess)var0).selected);
            case TYPEARRAY:
               return getStartPos(((JCTree.JCArrayTypeTree)var0).elemtype);
            case OR:
            case AND:
            case BITOR:
            case BITXOR:
            case BITAND:
            case EQ:
            case NE:
            case LT:
            case GT:
            case LE:
            case GE:
            case SL:
            case SR:
            case USR:
            case PLUS:
            case MINUS:
            case MUL:
            case DIV:
            case MOD:
               return getStartPos(((JCTree.JCBinary)var0).lhs);
            case CLASSDEF:
               JCTree.JCClassDecl var2 = (JCTree.JCClassDecl)var0;
               if (var2.mods.pos != -1) {
                  return var2.mods.pos;
               }
               break;
            case CONDEXPR:
               return getStartPos(((JCTree.JCConditional)var0).cond);
            case EXEC:
               return getStartPos(((JCTree.JCExpressionStatement)var0).expr);
            case INDEXED:
               return getStartPos(((JCTree.JCArrayAccess)var0).indexed);
            case METHODDEF:
               JCTree.JCMethodDecl var1 = (JCTree.JCMethodDecl)var0;
               if (var1.mods.pos != -1) {
                  return var1.mods.pos;
               }

               if (var1.typarams.nonEmpty()) {
                  return getStartPos((JCTree)var1.typarams.head);
               }

               return var1.restype == null ? var1.pos : getStartPos(var1.restype);
            case TYPETEST:
               return getStartPos(((JCTree.JCInstanceOf)var0).expr);
         }

         return var0.pos;
      }
   }

   public static int getEndPos(JCTree var0, EndPosTable var1) {
      if (var0 == null) {
         return -1;
      } else if (var1 == null) {
         return endPos(var0);
      } else {
         int var2 = var1.getEndPos(var0);
         if (var2 != -1) {
            return var2;
         } else {
            switch (var0.getTag()) {
               case ANNOTATED_TYPE:
                  return getEndPos(((JCTree.JCAnnotatedType)var0).underlyingType, var1);
               case PREINC:
               case PREDEC:
               case POS:
               case NEG:
               case NOT:
               case COMPL:
                  return getEndPos(((JCTree.JCUnary)var0).arg, var1);
               case BITOR_ASG:
               case BITXOR_ASG:
               case BITAND_ASG:
               case SL_ASG:
               case SR_ASG:
               case USR_ASG:
               case PLUS_ASG:
               case MINUS_ASG:
               case MUL_ASG:
               case DIV_ASG:
               case MOD_ASG:
                  return getEndPos(((JCTree.JCAssignOp)var0).rhs, var1);
               case ERRONEOUS:
                  JCTree.JCErroneous var5 = (JCTree.JCErroneous)var0;
                  if (var5.errs != null && var5.errs.nonEmpty()) {
                     return getEndPos((JCTree)var5.errs.last(), var1);
                  }
               case VARDEF:
               case REFERENCE:
               case POSTINC:
               case POSTDEC:
               case ASSIGN:
               case IDENT:
               case SELECT:
               case TYPEARRAY:
               case CLASSDEF:
               case EXEC:
               case INDEXED:
               case METHODDEF:
               default:
                  return -1;
               case OR:
               case AND:
               case BITOR:
               case BITXOR:
               case BITAND:
               case EQ:
               case NE:
               case LT:
               case GT:
               case LE:
               case GE:
               case SL:
               case SR:
               case USR:
               case PLUS:
               case MINUS:
               case MUL:
               case DIV:
               case MOD:
                  return getEndPos(((JCTree.JCBinary)var0).rhs, var1);
               case CONDEXPR:
                  return getEndPos(((JCTree.JCConditional)var0).falsepart, var1);
               case TYPETEST:
                  return getEndPos(((JCTree.JCInstanceOf)var0).clazz, var1);
               case CASE:
                  return getEndPos((JCTree)((JCTree.JCCase)var0).stats.last(), var1);
               case CATCH:
                  return getEndPos(((JCTree.JCCatch)var0).body, var1);
               case FORLOOP:
                  return getEndPos(((JCTree.JCForLoop)var0).body, var1);
               case FOREACHLOOP:
                  return getEndPos(((JCTree.JCEnhancedForLoop)var0).body, var1);
               case IF:
                  JCTree.JCIf var4 = (JCTree.JCIf)var0;
                  if (var4.elsepart == null) {
                     return getEndPos(var4.thenpart, var1);
                  }

                  return getEndPos(var4.elsepart, var1);
               case LABELLED:
                  return getEndPos(((JCTree.JCLabeledStatement)var0).body, var1);
               case MODIFIERS:
                  return getEndPos((JCTree)((JCTree.JCModifiers)var0).annotations.last(), var1);
               case SYNCHRONIZED:
                  return getEndPos(((JCTree.JCSynchronized)var0).body, var1);
               case TOPLEVEL:
                  return getEndPos((JCTree)((JCTree.JCCompilationUnit)var0).defs.last(), var1);
               case TRY:
                  JCTree.JCTry var3 = (JCTree.JCTry)var0;
                  if (var3.finalizer != null) {
                     return getEndPos(var3.finalizer, var1);
                  } else {
                     if (!var3.catchers.isEmpty()) {
                        return getEndPos((JCTree)var3.catchers.last(), var1);
                     }

                     return getEndPos(var3.body, var1);
                  }
               case WILDCARD:
                  return getEndPos(((JCTree.JCWildcard)var0).inner, var1);
               case TYPECAST:
                  return getEndPos(((JCTree.JCTypeCast)var0).expr, var1);
               case WHILELOOP:
                  return getEndPos(((JCTree.JCWhileLoop)var0).body, var1);
            }
         }
      }
   }

   public static JCDiagnostic.DiagnosticPosition diagEndPos(final JCTree var0) {
      final int var1 = endPos(var0);
      return new JCDiagnostic.DiagnosticPosition() {
         public JCTree getTree() {
            return var0;
         }

         public int getStartPosition() {
            return TreeInfo.getStartPos(var0);
         }

         public int getPreferredPosition() {
            return var1;
         }

         public int getEndPosition(EndPosTable var1x) {
            return TreeInfo.getEndPos(var0, var1x);
         }
      };
   }

   public static int finalizerPos(JCTree var0, PosKind var1) {
      if (var0.hasTag(JCTree.Tag.TRY)) {
         JCTree.JCTry var2 = (JCTree.JCTry)var0;
         Assert.checkNonNull(var2.finalizer);
         return var1.toPos(var2.finalizer);
      } else if (var0.hasTag(JCTree.Tag.SYNCHRONIZED)) {
         return endPos(((JCTree.JCSynchronized)var0).body);
      } else {
         throw new AssertionError();
      }
   }

   public static int positionFor(Symbol var0, JCTree var1) {
      JCTree var2 = declarationFor(var0, var1);
      return (var2 != null ? var2 : var1).pos;
   }

   public static JCDiagnostic.DiagnosticPosition diagnosticPositionFor(Symbol var0, JCTree var1) {
      JCTree var2 = declarationFor(var0, var1);
      return (var2 != null ? var2 : var1).pos();
   }

   public static JCTree declarationFor(final Symbol var0, JCTree var1) {
      class DeclScanner extends TreeScanner {
         JCTree result = null;

         public void scan(JCTree var1) {
            if (var1 != null && this.result == null) {
               var1.accept(this);
            }

         }

         public void visitTopLevel(JCTree.JCCompilationUnit var1) {
            if (var1.packge == var0) {
               this.result = var1;
            } else {
               super.visitTopLevel(var1);
            }

         }

         public void visitClassDef(JCTree.JCClassDecl var1) {
            if (var1.sym == var0) {
               this.result = var1;
            } else {
               super.visitClassDef(var1);
            }

         }

         public void visitMethodDef(JCTree.JCMethodDecl var1) {
            if (var1.sym == var0) {
               this.result = var1;
            } else {
               super.visitMethodDef(var1);
            }

         }

         public void visitVarDef(JCTree.JCVariableDecl var1) {
            if (var1.sym == var0) {
               this.result = var1;
            } else {
               super.visitVarDef(var1);
            }

         }

         public void visitTypeParameter(JCTree.JCTypeParameter var1) {
            if (var1.type != null && var1.type.tsym == var0) {
               this.result = var1;
            } else {
               super.visitTypeParameter(var1);
            }

         }
      }

      DeclScanner var2 = new DeclScanner();
      var1.accept(var2);
      return var2.result;
   }

   public static Env scopeFor(JCTree var0, JCTree.JCCompilationUnit var1) {
      return scopeFor(pathFor(var0, var1));
   }

   public static Env scopeFor(List var0) {
      throw new UnsupportedOperationException("not implemented yet");
   }

   public static List pathFor(final JCTree var0, JCTree.JCCompilationUnit var1) {
      try {
         class PathFinder extends TreeScanner {
            List path = List.nil();

            public void scan(JCTree var1) {
               if (var1 != null) {
                  this.path = this.path.prepend(var1);
                  if (var1 == var0) {
                     throw new Result(this.path);
                  }

                  super.scan(var1);
                  this.path = this.path.tail;
               }

            }
         }

         (new PathFinder()).scan(var1);
      } catch (Result var3) {
         class Result extends Error {
            static final long serialVersionUID = -5942088234594905625L;
            List path;

            Result(List var1) {
               this.path = var1;
            }
         }

         return var3.path;
      }

      return List.nil();
   }

   public static JCTree referencedStatement(JCTree.JCLabeledStatement var0) {
      Object var1 = var0;

      do {
         var1 = ((JCTree.JCLabeledStatement)var1).body;
      } while(((JCTree)var1).hasTag(JCTree.Tag.LABELLED));

      switch (((JCTree)var1).getTag()) {
         case FORLOOP:
         case FOREACHLOOP:
         case WHILELOOP:
         case DOLOOP:
         case SWITCH:
            return (JCTree)var1;
         default:
            return var0;
      }
   }

   public static JCTree.JCExpression skipParens(JCTree.JCExpression var0) {
      while(var0.hasTag(JCTree.Tag.PARENS)) {
         var0 = ((JCTree.JCParens)var0).expr;
      }

      return var0;
   }

   public static JCTree skipParens(JCTree var0) {
      return (JCTree)(var0.hasTag(JCTree.Tag.PARENS) ? skipParens((JCTree.JCExpression)((JCTree.JCParens)var0)) : var0);
   }

   public static List types(List var0) {
      ListBuffer var1 = new ListBuffer();

      for(List var2 = var0; var2.nonEmpty(); var2 = var2.tail) {
         var1.append(((JCTree)var2.head).type);
      }

      return var1.toList();
   }

   public static Name name(JCTree var0) {
      switch (var0.getTag()) {
         case TYPEAPPLY:
            return name(((JCTree.JCTypeApply)var0).clazz);
         case IDENT:
            return ((JCTree.JCIdent)var0).name;
         case SELECT:
            return ((JCTree.JCFieldAccess)var0).name;
         default:
            return null;
      }
   }

   public static Name fullName(JCTree var0) {
      var0 = skipParens(var0);
      switch (var0.getTag()) {
         case IDENT:
            return ((JCTree.JCIdent)var0).name;
         case SELECT:
            Name var1 = fullName(((JCTree.JCFieldAccess)var0).selected);
            return var1 == null ? null : var1.append('.', name(var0));
         default:
            return null;
      }
   }

   public static Symbol symbolFor(JCTree var0) {
      Symbol var1 = symbolForImpl(var0);
      return var1 != null ? var1.baseSymbol() : null;
   }

   private static Symbol symbolForImpl(JCTree var0) {
      var0 = skipParens(var0);
      switch (var0.getTag()) {
         case APPLY:
            return symbolFor(((JCTree.JCMethodInvocation)var0).meth);
         case NEWCLASS:
            return ((JCTree.JCNewClass)var0).constructor;
         case TYPEAPPLY:
            return symbolFor(((JCTree.JCTypeApply)var0).clazz);
         case VARDEF:
            return ((JCTree.JCVariableDecl)var0).sym;
         case REFERENCE:
            return ((JCTree.JCMemberReference)var0).sym;
         case IDENT:
            return ((JCTree.JCIdent)var0).sym;
         case SELECT:
            return ((JCTree.JCFieldAccess)var0).sym;
         case CLASSDEF:
            return ((JCTree.JCClassDecl)var0).sym;
         case METHODDEF:
            return ((JCTree.JCMethodDecl)var0).sym;
         case TOPLEVEL:
            return ((JCTree.JCCompilationUnit)var0).packge;
         case ANNOTATION:
         case TYPE_ANNOTATION:
         case TYPEPARAMETER:
            if (var0.type != null) {
               return var0.type.tsym;
            }

            return null;
         default:
            return null;
      }
   }

   public static boolean isDeclaration(JCTree var0) {
      var0 = skipParens(var0);
      switch (var0.getTag()) {
         case VARDEF:
         case CLASSDEF:
         case METHODDEF:
            return true;
         default:
            return false;
      }
   }

   public static Symbol symbol(JCTree var0) {
      var0 = skipParens(var0);
      switch (var0.getTag()) {
         case TYPEAPPLY:
            return symbol(((JCTree.JCTypeApply)var0).clazz);
         case ANNOTATED_TYPE:
            return symbol(((JCTree.JCAnnotatedType)var0).underlyingType);
         case REFERENCE:
            return ((JCTree.JCMemberReference)var0).sym;
         case IDENT:
            return ((JCTree.JCIdent)var0).sym;
         case SELECT:
            return ((JCTree.JCFieldAccess)var0).sym;
         default:
            return null;
      }
   }

   public static boolean nonstaticSelect(JCTree var0) {
      var0 = skipParens(var0);
      if (!var0.hasTag(JCTree.Tag.SELECT)) {
         return false;
      } else {
         JCTree.JCFieldAccess var1 = (JCTree.JCFieldAccess)var0;
         Symbol var2 = symbol(var1.selected);
         return var2 == null || var2.kind != 1 && var2.kind != 2;
      }
   }

   public static void setSymbol(JCTree var0, Symbol var1) {
      var0 = skipParens(var0);
      switch (var0.getTag()) {
         case IDENT:
            ((JCTree.JCIdent)var0).sym = var1;
            break;
         case SELECT:
            ((JCTree.JCFieldAccess)var0).sym = var1;
      }

   }

   public static long flags(JCTree var0) {
      switch (var0.getTag()) {
         case VARDEF:
            return ((JCTree.JCVariableDecl)var0).mods.flags;
         case CLASSDEF:
            return ((JCTree.JCClassDecl)var0).mods.flags;
         case METHODDEF:
            return ((JCTree.JCMethodDecl)var0).mods.flags;
         case BLOCK:
            return ((JCTree.JCBlock)var0).flags;
         default:
            return 0L;
      }
   }

   public static long firstFlag(long var0) {
      long var2;
      for(var2 = 1L; (var2 & var0 & 8796093026303L) == 0L; var2 <<= 1) {
      }

      return var2;
   }

   public static String flagNames(long var0) {
      return Flags.toString(var0 & 8796093026303L).trim();
   }

   public static int opPrec(JCTree.Tag var0) {
      switch (var0) {
         case PREINC:
         case PREDEC:
         case POS:
         case NEG:
         case NOT:
         case COMPL:
            return 14;
         case POSTINC:
         case POSTDEC:
         case NULLCHK:
            return 15;
         case ASSIGN:
            return 1;
         case BITOR_ASG:
         case BITXOR_ASG:
         case BITAND_ASG:
         case SL_ASG:
         case SR_ASG:
         case USR_ASG:
         case PLUS_ASG:
         case MINUS_ASG:
         case MUL_ASG:
         case DIV_ASG:
         case MOD_ASG:
            return 2;
         case ERRONEOUS:
         case IDENT:
         case SELECT:
         case TYPEARRAY:
         case CLASSDEF:
         case CONDEXPR:
         case EXEC:
         case INDEXED:
         case METHODDEF:
         case CASE:
         case CATCH:
         case FORLOOP:
         case FOREACHLOOP:
         case IF:
         case LABELLED:
         case MODIFIERS:
         case SYNCHRONIZED:
         case TOPLEVEL:
         case TRY:
         case WILDCARD:
         case TYPECAST:
         case WHILELOOP:
         case DOLOOP:
         case SWITCH:
         case ANNOTATION:
         case TYPE_ANNOTATION:
         case TYPEPARAMETER:
         case BLOCK:
         default:
            throw new AssertionError();
         case OR:
            return 4;
         case AND:
            return 5;
         case BITOR:
            return 6;
         case BITXOR:
            return 7;
         case BITAND:
            return 8;
         case EQ:
         case NE:
            return 9;
         case LT:
         case GT:
         case LE:
         case GE:
            return 10;
         case SL:
         case SR:
         case USR:
            return 11;
         case PLUS:
         case MINUS:
            return 12;
         case MUL:
         case DIV:
         case MOD:
            return 13;
         case TYPETEST:
            return 10;
      }
   }

   static Tree.Kind tagToKind(JCTree.Tag var0) {
      switch (var0) {
         case PREINC:
            return Tree.Kind.PREFIX_INCREMENT;
         case PREDEC:
            return Tree.Kind.PREFIX_DECREMENT;
         case POSTINC:
            return Tree.Kind.POSTFIX_INCREMENT;
         case POSTDEC:
            return Tree.Kind.POSTFIX_DECREMENT;
         case ASSIGN:
         case ERRONEOUS:
         case IDENT:
         case SELECT:
         case TYPEARRAY:
         case CLASSDEF:
         case CONDEXPR:
         case EXEC:
         case INDEXED:
         case METHODDEF:
         case TYPETEST:
         case CASE:
         case CATCH:
         case FORLOOP:
         case FOREACHLOOP:
         case IF:
         case LABELLED:
         case MODIFIERS:
         case SYNCHRONIZED:
         case TOPLEVEL:
         case TRY:
         case WILDCARD:
         case TYPECAST:
         case WHILELOOP:
         case DOLOOP:
         case SWITCH:
         case TYPEPARAMETER:
         case BLOCK:
         default:
            return null;
         case BITOR_ASG:
            return Tree.Kind.OR_ASSIGNMENT;
         case BITXOR_ASG:
            return Tree.Kind.XOR_ASSIGNMENT;
         case BITAND_ASG:
            return Tree.Kind.AND_ASSIGNMENT;
         case SL_ASG:
            return Tree.Kind.LEFT_SHIFT_ASSIGNMENT;
         case SR_ASG:
            return Tree.Kind.RIGHT_SHIFT_ASSIGNMENT;
         case USR_ASG:
            return Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
         case PLUS_ASG:
            return Tree.Kind.PLUS_ASSIGNMENT;
         case MINUS_ASG:
            return Tree.Kind.MINUS_ASSIGNMENT;
         case MUL_ASG:
            return Tree.Kind.MULTIPLY_ASSIGNMENT;
         case DIV_ASG:
            return Tree.Kind.DIVIDE_ASSIGNMENT;
         case MOD_ASG:
            return Tree.Kind.REMAINDER_ASSIGNMENT;
         case OR:
            return Tree.Kind.CONDITIONAL_OR;
         case AND:
            return Tree.Kind.CONDITIONAL_AND;
         case BITOR:
            return Tree.Kind.OR;
         case BITXOR:
            return Tree.Kind.XOR;
         case BITAND:
            return Tree.Kind.AND;
         case EQ:
            return Tree.Kind.EQUAL_TO;
         case NE:
            return Tree.Kind.NOT_EQUAL_TO;
         case LT:
            return Tree.Kind.LESS_THAN;
         case GT:
            return Tree.Kind.GREATER_THAN;
         case LE:
            return Tree.Kind.LESS_THAN_EQUAL;
         case GE:
            return Tree.Kind.GREATER_THAN_EQUAL;
         case SL:
            return Tree.Kind.LEFT_SHIFT;
         case SR:
            return Tree.Kind.RIGHT_SHIFT;
         case USR:
            return Tree.Kind.UNSIGNED_RIGHT_SHIFT;
         case PLUS:
            return Tree.Kind.PLUS;
         case MINUS:
            return Tree.Kind.MINUS;
         case MUL:
            return Tree.Kind.MULTIPLY;
         case DIV:
            return Tree.Kind.DIVIDE;
         case MOD:
            return Tree.Kind.REMAINDER;
         case POS:
            return Tree.Kind.UNARY_PLUS;
         case NEG:
            return Tree.Kind.UNARY_MINUS;
         case NOT:
            return Tree.Kind.LOGICAL_COMPLEMENT;
         case COMPL:
            return Tree.Kind.BITWISE_COMPLEMENT;
         case ANNOTATION:
            return Tree.Kind.ANNOTATION;
         case TYPE_ANNOTATION:
            return Tree.Kind.TYPE_ANNOTATION;
         case NULLCHK:
            return Tree.Kind.OTHER;
      }
   }

   public static JCTree.JCExpression typeIn(JCTree.JCExpression var0) {
      switch (var0.getTag()) {
         case TYPEAPPLY:
         case ERRONEOUS:
         case IDENT:
         case SELECT:
         case TYPEARRAY:
         case WILDCARD:
         case TYPEPARAMETER:
         case TYPEIDENT:
            return var0;
         case ANNOTATED_TYPE:
            return ((JCTree.JCAnnotatedType)var0).underlyingType;
         default:
            throw new AssertionError("Unexpected type tree: " + var0);
      }
   }

   public static JCTree innermostType(JCTree var0) {
      Object var1 = null;
      Object var2 = var0;

      while(true) {
         switch (((JCTree)var2).getTag()) {
            case ANNOTATED_TYPE:
               var1 = var2;
               var2 = ((JCTree.JCAnnotatedType)var2).underlyingType;
               break;
            case TYPEARRAY:
               var1 = null;
               var2 = ((JCTree.JCArrayTypeTree)var2).elemtype;
               break;
            case WILDCARD:
               var1 = null;
               var2 = ((JCTree.JCWildcard)var2).inner;
               break;
            default:
               return (JCTree)(var1 != null ? var1 : var2);
         }
      }
   }

   public static boolean containsTypeAnnotation(JCTree var0) {
      TypeAnnotationFinder var1 = new TypeAnnotationFinder();
      var1.scan(var0);
      return var1.foundTypeAnno;
   }

   private static class TypeAnnotationFinder extends TreeScanner {
      public boolean foundTypeAnno;

      private TypeAnnotationFinder() {
         this.foundTypeAnno = false;
      }

      public void scan(JCTree var1) {
         if (!this.foundTypeAnno && var1 != null) {
            super.scan(var1);
         }
      }

      public void visitAnnotation(JCTree.JCAnnotation var1) {
         this.foundTypeAnno = this.foundTypeAnno || var1.hasTag(JCTree.Tag.TYPE_ANNOTATION);
      }

      // $FF: synthetic method
      TypeAnnotationFinder(Object var1) {
         this();
      }
   }

   public static enum PosKind {
      START_POS {
         int toPos(JCTree var1) {
            return TreeInfo.getStartPos(var1);
         }
      },
      FIRST_STAT_POS {
         int toPos(JCTree var1) {
            return TreeInfo.firstStatPos(var1);
         }
      },
      END_POS {
         int toPos(JCTree var1) {
            return TreeInfo.endPos(var1);
         }
      };

      private PosKind() {
      }

      abstract int toPos(JCTree var1);

      // $FF: synthetic method
      PosKind(Object var3) {
         this();
      }
   }
}
