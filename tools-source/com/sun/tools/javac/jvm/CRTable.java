package com.sun.tools.javac.jvm;

import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.ByteBuffer;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Position;
import java.util.HashMap;
import java.util.Map;

public class CRTable implements CRTFlags {
   private final boolean crtDebug = false;
   private ListBuffer entries = new ListBuffer();
   private Map positions = new HashMap();
   private EndPosTable endPosTable;
   JCTree.JCMethodDecl methodTree;

   public CRTable(JCTree.JCMethodDecl var1, EndPosTable var2) {
      this.methodTree = var1;
      this.endPosTable = var2;
   }

   public void put(Object var1, int var2, int var3, int var4) {
      this.entries.append(new CRTEntry(var1, var2, var3, var4));
   }

   public int writeCRT(ByteBuffer var1, Position.LineMap var2, Log var3) {
      int var4 = 0;
      (new SourceComputer()).csp((JCTree)this.methodTree);

      for(List var5 = this.entries.toList(); var5.nonEmpty(); var5 = var5.tail) {
         CRTEntry var6 = (CRTEntry)var5.head;
         if (var6.startPc != var6.endPc) {
            SourceRange var7 = (SourceRange)this.positions.get(var6.tree);
            Assert.checkNonNull(var7, "CRT: tree source positions are undefined");
            if (var7.startPos != -1 && var7.endPos != -1) {
               int var8 = this.encodePosition(var7.startPos, var2, var3);
               if (var8 != -1) {
                  int var9 = this.encodePosition(var7.endPos, var2, var3);
                  if (var9 != -1) {
                     var1.appendChar(var6.startPc);
                     var1.appendChar(var6.endPc - 1);
                     var1.appendInt(var8);
                     var1.appendInt(var9);
                     var1.appendChar(var6.flags);
                     ++var4;
                  }
               }
            }
         }
      }

      return var4;
   }

   public int length() {
      return this.entries.length();
   }

   private String getTypes(int var1) {
      String var2 = "";
      if ((var1 & 1) != 0) {
         var2 = var2 + " CRT_STATEMENT";
      }

      if ((var1 & 2) != 0) {
         var2 = var2 + " CRT_BLOCK";
      }

      if ((var1 & 4) != 0) {
         var2 = var2 + " CRT_ASSIGNMENT";
      }

      if ((var1 & 8) != 0) {
         var2 = var2 + " CRT_FLOW_CONTROLLER";
      }

      if ((var1 & 16) != 0) {
         var2 = var2 + " CRT_FLOW_TARGET";
      }

      if ((var1 & 32) != 0) {
         var2 = var2 + " CRT_INVOKE";
      }

      if ((var1 & 64) != 0) {
         var2 = var2 + " CRT_CREATE";
      }

      if ((var1 & 128) != 0) {
         var2 = var2 + " CRT_BRANCH_TRUE";
      }

      if ((var1 & 256) != 0) {
         var2 = var2 + " CRT_BRANCH_FALSE";
      }

      return var2;
   }

   private int encodePosition(int var1, Position.LineMap var2, Log var3) {
      int var4 = var2.getLineNumber(var1);
      int var5 = var2.getColumnNumber(var1);
      int var6 = Position.encodePosition(var4, var5);
      if (var6 == -1) {
         var3.warning(var1, "position.overflow", new Object[]{var4});
      }

      return var6;
   }

   static class SourceRange {
      int startPos;
      int endPos;

      SourceRange() {
         this.startPos = -1;
         this.endPos = -1;
      }

      SourceRange(int var1, int var2) {
         this.startPos = var1;
         this.endPos = var2;
      }

      SourceRange mergeWith(SourceRange var1) {
         if (var1 == null) {
            return this;
         } else {
            if (this.startPos == -1) {
               this.startPos = var1.startPos;
            } else if (var1.startPos != -1) {
               this.startPos = this.startPos < var1.startPos ? this.startPos : var1.startPos;
            }

            if (this.endPos == -1) {
               this.endPos = var1.endPos;
            } else if (var1.endPos != -1) {
               this.endPos = this.endPos > var1.endPos ? this.endPos : var1.endPos;
            }

            return this;
         }
      }
   }

   static class CRTEntry {
      Object tree;
      int flags;
      int startPc;
      int endPc;

      CRTEntry(Object var1, int var2, int var3, int var4) {
         this.tree = var1;
         this.flags = var2;
         this.startPc = var3;
         this.endPc = var4;
      }
   }

   class SourceComputer extends JCTree.Visitor {
      SourceRange result;

      public SourceRange csp(JCTree var1) {
         if (var1 == null) {
            return null;
         } else {
            var1.accept(this);
            if (this.result != null) {
               CRTable.this.positions.put(var1, this.result);
            }

            return this.result;
         }
      }

      public SourceRange csp(List var1) {
         if (var1 != null && var1.nonEmpty()) {
            SourceRange var2 = new SourceRange();

            for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
               var2.mergeWith(this.csp((JCTree)var3.head));
            }

            CRTable.this.positions.put(var1, var2);
            return var2;
         } else {
            return null;
         }
      }

      public SourceRange cspCases(List var1) {
         if (var1 != null && var1.nonEmpty()) {
            SourceRange var2 = new SourceRange();

            for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
               var2.mergeWith(this.csp((JCTree)var3.head));
            }

            CRTable.this.positions.put(var1, var2);
            return var2;
         } else {
            return null;
         }
      }

      public SourceRange cspCatchers(List var1) {
         if (var1 != null && var1.nonEmpty()) {
            SourceRange var2 = new SourceRange();

            for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
               var2.mergeWith(this.csp((JCTree)var3.head));
            }

            CRTable.this.positions.put(var1, var2);
            return var2;
         } else {
            return null;
         }
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.csp((JCTree)var1.vartype);
         var2.mergeWith(this.csp((JCTree)var1.init));
         this.result = var2;
      }

      public void visitSkip(JCTree.JCSkip var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.startPos(var1));
         this.result = var2;
      }

      public void visitBlock(JCTree.JCBlock var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.csp(var1.stats);
         this.result = var2;
      }

      public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.body));
         var2.mergeWith(this.csp((JCTree)var1.cond));
         this.result = var2;
      }

      public void visitWhileLoop(JCTree.JCWhileLoop var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.cond));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitForLoop(JCTree.JCForLoop var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp(var1.init));
         var2.mergeWith(this.csp((JCTree)var1.cond));
         var2.mergeWith(this.csp(var1.step));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.var));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitLabelled(JCTree.JCLabeledStatement var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitSwitch(JCTree.JCSwitch var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.selector));
         var2.mergeWith(this.cspCases(var1.cases));
         this.result = var2;
      }

      public void visitCase(JCTree.JCCase var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.pat));
         var2.mergeWith(this.csp(var1.stats));
         this.result = var2;
      }

      public void visitSynchronized(JCTree.JCSynchronized var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.lock));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitTry(JCTree.JCTry var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp(var1.resources));
         var2.mergeWith(this.csp((JCTree)var1.body));
         var2.mergeWith(this.cspCatchers(var1.catchers));
         var2.mergeWith(this.csp((JCTree)var1.finalizer));
         this.result = var2;
      }

      public void visitCatch(JCTree.JCCatch var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.param));
         var2.mergeWith(this.csp((JCTree)var1.body));
         this.result = var2;
      }

      public void visitConditional(JCTree.JCConditional var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.cond));
         var2.mergeWith(this.csp((JCTree)var1.truepart));
         var2.mergeWith(this.csp((JCTree)var1.falsepart));
         this.result = var2;
      }

      public void visitIf(JCTree.JCIf var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.cond));
         var2.mergeWith(this.csp((JCTree)var1.thenpart));
         var2.mergeWith(this.csp((JCTree)var1.elsepart));
         this.result = var2;
      }

      public void visitExec(JCTree.JCExpressionStatement var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         this.result = var2;
      }

      public void visitBreak(JCTree.JCBreak var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.result = var2;
      }

      public void visitContinue(JCTree.JCContinue var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.result = var2;
      }

      public void visitReturn(JCTree.JCReturn var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         this.result = var2;
      }

      public void visitThrow(JCTree.JCThrow var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         this.result = var2;
      }

      public void visitAssert(JCTree.JCAssert var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.cond));
         var2.mergeWith(this.csp((JCTree)var1.detail));
         this.result = var2;
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.meth));
         var2.mergeWith(this.csp(var1.args));
         this.result = var2;
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.encl));
         var2.mergeWith(this.csp((JCTree)var1.clazz));
         var2.mergeWith(this.csp(var1.args));
         var2.mergeWith(this.csp((JCTree)var1.def));
         this.result = var2;
      }

      public void visitNewArray(JCTree.JCNewArray var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.elemtype));
         var2.mergeWith(this.csp(var1.dims));
         var2.mergeWith(this.csp(var1.elems));
         this.result = var2;
      }

      public void visitParens(JCTree.JCParens var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         this.result = var2;
      }

      public void visitAssign(JCTree.JCAssign var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.lhs));
         var2.mergeWith(this.csp((JCTree)var1.rhs));
         this.result = var2;
      }

      public void visitAssignop(JCTree.JCAssignOp var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.lhs));
         var2.mergeWith(this.csp((JCTree)var1.rhs));
         this.result = var2;
      }

      public void visitUnary(JCTree.JCUnary var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.arg));
         this.result = var2;
      }

      public void visitBinary(JCTree.JCBinary var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.lhs));
         var2.mergeWith(this.csp((JCTree)var1.rhs));
         this.result = var2;
      }

      public void visitTypeCast(JCTree.JCTypeCast var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp(var1.clazz));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         this.result = var2;
      }

      public void visitTypeTest(JCTree.JCInstanceOf var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.expr));
         var2.mergeWith(this.csp(var1.clazz));
         this.result = var2;
      }

      public void visitIndexed(JCTree.JCArrayAccess var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.indexed));
         var2.mergeWith(this.csp((JCTree)var1.index));
         this.result = var2;
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.selected));
         this.result = var2;
      }

      public void visitIdent(JCTree.JCIdent var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.result = var2;
      }

      public void visitLiteral(JCTree.JCLiteral var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.result = var2;
      }

      public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         this.result = var2;
      }

      public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.elemtype));
         this.result = var2;
      }

      public void visitTypeApply(JCTree.JCTypeApply var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp((JCTree)var1.clazz));
         var2.mergeWith(this.csp(var1.arguments));
         this.result = var2;
      }

      public void visitLetExpr(JCTree.LetExpr var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp(var1.defs));
         var2.mergeWith(this.csp(var1.expr));
         this.result = var2;
      }

      public void visitTypeParameter(JCTree.JCTypeParameter var1) {
         SourceRange var2 = new SourceRange(this.startPos(var1), this.endPos(var1));
         var2.mergeWith(this.csp(var1.bounds));
         this.result = var2;
      }

      public void visitWildcard(JCTree.JCWildcard var1) {
         this.result = null;
      }

      public void visitErroneous(JCTree.JCErroneous var1) {
         this.result = null;
      }

      public void visitTree(JCTree var1) {
         Assert.error();
      }

      public int startPos(JCTree var1) {
         return var1 == null ? -1 : TreeInfo.getStartPos(var1);
      }

      public int endPos(JCTree var1) {
         return var1 == null ? -1 : TreeInfo.getEndPos(var1, CRTable.this.endPosTable);
      }
   }
}
