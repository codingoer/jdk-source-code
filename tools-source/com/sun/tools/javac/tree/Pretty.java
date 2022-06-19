package com.sun.tools.javac.tree;

import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.Convert;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

public class Pretty extends JCTree.Visitor {
   private final boolean sourceOutput;
   Writer out;
   public int width = 4;
   int lmargin = 0;
   Name enclClassName;
   DocCommentTable docComments = null;
   private static final String trimSequence = "[...]";
   private static final int PREFERRED_LENGTH = 20;
   String lineSep = System.getProperty("line.separator");
   int prec;

   public Pretty(Writer var1, boolean var2) {
      this.out = var1;
      this.sourceOutput = var2;
   }

   void align() throws IOException {
      for(int var1 = 0; var1 < this.lmargin; ++var1) {
         this.out.write(" ");
      }

   }

   void indent() {
      this.lmargin += this.width;
   }

   void undent() {
      this.lmargin -= this.width;
   }

   void open(int var1, int var2) throws IOException {
      if (var2 < var1) {
         this.out.write("(");
      }

   }

   void close(int var1, int var2) throws IOException {
      if (var2 < var1) {
         this.out.write(")");
      }

   }

   public void print(Object var1) throws IOException {
      this.out.write(Convert.escapeUnicode(var1.toString()));
   }

   public void println() throws IOException {
      this.out.write(this.lineSep);
   }

   public static String toSimpleString(JCTree var0) {
      return toSimpleString(var0, 20);
   }

   public static String toSimpleString(JCTree var0, int var1) {
      StringWriter var2 = new StringWriter();

      try {
         (new Pretty(var2, false)).printExpr(var0);
      } catch (IOException var6) {
         throw new AssertionError(var6);
      }

      String var3 = var2.toString().trim().replaceAll("\\s+", " ").replaceAll("/\\*missing\\*/", "");
      if (var3.length() < var1) {
         return var3;
      } else {
         int var4 = (var1 - "[...]".length()) * 2 / 3;
         int var5 = var1 - "[...]".length() - var4;
         return var3.substring(0, var4) + "[...]" + var3.substring(var3.length() - var5);
      }
   }

   public void printExpr(JCTree var1, int var2) throws IOException {
      int var3 = this.prec;

      try {
         this.prec = var2;
         if (var1 == null) {
            this.print("/*missing*/");
         } else {
            var1.accept(this);
         }
      } catch (UncheckedIOException var9) {
         IOException var5 = new IOException(var9.getMessage());
         var5.initCause(var9);
         throw var5;
      } finally {
         this.prec = var3;
      }

   }

   public void printExpr(JCTree var1) throws IOException {
      this.printExpr(var1, 0);
   }

   public void printStat(JCTree var1) throws IOException {
      this.printExpr(var1, -1);
   }

   public void printExprs(List var1, String var2) throws IOException {
      if (var1.nonEmpty()) {
         this.printExpr((JCTree)var1.head);

         for(List var3 = var1.tail; var3.nonEmpty(); var3 = var3.tail) {
            this.print(var2);
            this.printExpr((JCTree)var3.head);
         }
      }

   }

   public void printExprs(List var1) throws IOException {
      this.printExprs(var1, ", ");
   }

   public void printStats(List var1) throws IOException {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         this.align();
         this.printStat((JCTree)var2.head);
         this.println();
      }

   }

   public void printFlags(long var1) throws IOException {
      if ((var1 & 4096L) != 0L) {
         this.print("/*synthetic*/ ");
      }

      this.print(TreeInfo.flagNames(var1));
      if ((var1 & 8796093026303L) != 0L) {
         this.print(" ");
      }

      if ((var1 & 8192L) != 0L) {
         this.print("@");
      }

   }

   public void printAnnotations(List var1) throws IOException {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         this.printStat((JCTree)var2.head);
         this.println();
         this.align();
      }

   }

   public void printTypeAnnotations(List var1) throws IOException {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         this.printExpr((JCTree)var2.head);
         this.print(" ");
      }

   }

   public void printDocComment(JCTree var1) throws IOException {
      if (this.docComments != null) {
         String var2 = this.docComments.getCommentText(var1);
         if (var2 != null) {
            this.print("/**");
            this.println();
            int var3 = 0;

            for(int var4 = lineEndPos(var2, var3); var3 < var2.length(); var4 = lineEndPos(var2, var3)) {
               this.align();
               this.print(" *");
               if (var3 < var2.length() && var2.charAt(var3) > ' ') {
                  this.print(" ");
               }

               this.print(var2.substring(var3, var4));
               this.println();
               var3 = var4 + 1;
            }

            this.align();
            this.print(" */");
            this.println();
            this.align();
         }
      }

   }

   static int lineEndPos(String var0, int var1) {
      int var2 = var0.indexOf(10, var1);
      if (var2 < 0) {
         var2 = var0.length();
      }

      return var2;
   }

   public void printTypeParameters(List var1) throws IOException {
      if (var1.nonEmpty()) {
         this.print("<");
         this.printExprs(var1);
         this.print(">");
      }

   }

   public void printBlock(List var1) throws IOException {
      this.print("{");
      this.println();
      this.indent();
      this.printStats(var1);
      this.undent();
      this.align();
      this.print("}");
   }

   public void printEnumBody(List var1) throws IOException {
      this.print("{");
      this.println();
      this.indent();
      boolean var2 = true;

      List var3;
      for(var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         if (this.isEnumerator((JCTree)var3.head)) {
            if (!var2) {
               this.print(",");
               this.println();
            }

            this.align();
            this.printStat((JCTree)var3.head);
            var2 = false;
         }
      }

      this.print(";");
      this.println();

      for(var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         if (!this.isEnumerator((JCTree)var3.head)) {
            this.align();
            this.printStat((JCTree)var3.head);
            this.println();
         }
      }

      this.undent();
      this.align();
      this.print("}");
   }

   boolean isEnumerator(JCTree var1) {
      return var1.hasTag(JCTree.Tag.VARDEF) && (((JCTree.JCVariableDecl)var1).mods.flags & 16384L) != 0L;
   }

   public void printUnit(JCTree.JCCompilationUnit var1, JCTree.JCClassDecl var2) throws IOException {
      this.docComments = var1.docComments;
      this.printDocComment(var1);
      if (var1.pid != null) {
         this.print("package ");
         this.printExpr(var1.pid);
         this.print(";");
         this.println();
      }

      boolean var3 = true;

      for(List var4 = var1.defs; var4.nonEmpty() && (var2 == null || ((JCTree)var4.head).hasTag(JCTree.Tag.IMPORT)); var4 = var4.tail) {
         if (((JCTree)var4.head).hasTag(JCTree.Tag.IMPORT)) {
            JCTree.JCImport var5 = (JCTree.JCImport)var4.head;
            Name var6 = TreeInfo.name(var5.qualid);
            if (var6 == var6.table.names.asterisk || var2 == null || this.isUsed(TreeInfo.symbol(var5.qualid), var2)) {
               if (var3) {
                  var3 = false;
                  this.println();
               }

               this.printStat(var5);
            }
         } else {
            this.printStat((JCTree)var4.head);
         }
      }

      if (var2 != null) {
         this.printStat(var2);
         this.println();
      }

   }

   boolean isUsed(final Symbol var1, JCTree var2) {
      class UsedVisitor extends TreeScanner {
         boolean result = false;

         public void scan(JCTree var1x) {
            if (var1x != null && !this.result) {
               var1x.accept(this);
            }

         }

         public void visitIdent(JCTree.JCIdent var1x) {
            if (var1x.sym == var1) {
               this.result = true;
            }

         }
      }

      UsedVisitor var3 = new UsedVisitor();
      var3.scan(var2);
      return var3.result;
   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      try {
         this.printUnit(var1, (JCTree.JCClassDecl)null);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitImport(JCTree.JCImport var1) {
      try {
         this.print("import ");
         if (var1.staticImport) {
            this.print("static ");
         }

         this.printExpr(var1.qualid);
         this.print(";");
         this.println();
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      try {
         this.println();
         this.align();
         this.printDocComment(var1);
         this.printAnnotations(var1.mods.annotations);
         this.printFlags(var1.mods.flags & -513L);
         Name var2 = this.enclClassName;
         this.enclClassName = var1.name;
         if ((var1.mods.flags & 512L) != 0L) {
            this.print("interface " + var1.name);
            this.printTypeParameters(var1.typarams);
            if (var1.implementing.nonEmpty()) {
               this.print(" extends ");
               this.printExprs(var1.implementing);
            }
         } else {
            if ((var1.mods.flags & 16384L) != 0L) {
               this.print("enum " + var1.name);
            } else {
               this.print("class " + var1.name);
            }

            this.printTypeParameters(var1.typarams);
            if (var1.extending != null) {
               this.print(" extends ");
               this.printExpr(var1.extending);
            }

            if (var1.implementing.nonEmpty()) {
               this.print(" implements ");
               this.printExprs(var1.implementing);
            }
         }

         this.print(" ");
         if ((var1.mods.flags & 16384L) != 0L) {
            this.printEnumBody(var1.defs);
         } else {
            this.printBlock(var1.defs);
         }

         this.enclClassName = var2;
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      try {
         if (var1.name != var1.name.table.names.init || this.enclClassName != null || !this.sourceOutput) {
            this.println();
            this.align();
            this.printDocComment(var1);
            this.printExpr(var1.mods);
            this.printTypeParameters(var1.typarams);
            if (var1.name == var1.name.table.names.init) {
               this.print(this.enclClassName != null ? this.enclClassName : var1.name);
            } else {
               this.printExpr(var1.restype);
               this.print(" " + var1.name);
            }

            this.print("(");
            if (var1.recvparam != null) {
               this.printExpr(var1.recvparam);
               if (var1.params.size() > 0) {
                  this.print(", ");
               }
            }

            this.printExprs(var1.params);
            this.print(")");
            if (var1.thrown.nonEmpty()) {
               this.print(" throws ");
               this.printExprs(var1.thrown);
            }

            if (var1.defaultValue != null) {
               this.print(" default ");
               this.printExpr(var1.defaultValue);
            }

            if (var1.body != null) {
               this.print(" ");
               this.printStat(var1.body);
            } else {
               this.print(";");
            }

         }
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      try {
         if (this.docComments != null && this.docComments.hasComment(var1)) {
            this.println();
            this.align();
         }

         this.printDocComment(var1);
         if ((var1.mods.flags & 16384L) != 0L) {
            this.print("/*public static final*/ ");
            this.print(var1.name);
            if (var1.init != null) {
               if (this.sourceOutput && var1.init.hasTag(JCTree.Tag.NEWCLASS)) {
                  this.print(" /*enum*/ ");
                  JCTree.JCNewClass var2 = (JCTree.JCNewClass)var1.init;
                  if (var2.args != null && var2.args.nonEmpty()) {
                     this.print("(");
                     this.print(var2.args);
                     this.print(")");
                  }

                  if (var2.def != null && var2.def.defs != null) {
                     this.print(" ");
                     this.printBlock(var2.def.defs);
                  }

                  return;
               }

               this.print(" /* = ");
               this.printExpr(var1.init);
               this.print(" */");
            }
         } else {
            this.printExpr(var1.mods);
            if ((var1.mods.flags & 17179869184L) != 0L) {
               JCTree.JCExpression var5 = var1.vartype;
               List var3 = null;
               if (var5 instanceof JCTree.JCAnnotatedType) {
                  var3 = ((JCTree.JCAnnotatedType)var5).annotations;
                  var5 = ((JCTree.JCAnnotatedType)var5).underlyingType;
               }

               this.printExpr(((JCTree.JCArrayTypeTree)var5).elemtype);
               if (var3 != null) {
                  this.print(' ');
                  this.printTypeAnnotations(var3);
               }

               this.print("... " + var1.name);
            } else {
               this.printExpr(var1.vartype);
               this.print(" " + var1.name);
            }

            if (var1.init != null) {
               this.print(" = ");
               this.printExpr(var1.init);
            }

            if (this.prec == -1) {
               this.print(";");
            }
         }

      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public void visitSkip(JCTree.JCSkip var1) {
      try {
         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitBlock(JCTree.JCBlock var1) {
      try {
         this.printFlags(var1.flags);
         this.printBlock(var1.stats);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      try {
         this.print("do ");
         this.printStat(var1.body);
         this.align();
         this.print(" while ");
         if (var1.cond.hasTag(JCTree.Tag.PARENS)) {
            this.printExpr(var1.cond);
         } else {
            this.print("(");
            this.printExpr(var1.cond);
            this.print(")");
         }

         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      try {
         this.print("while ");
         if (var1.cond.hasTag(JCTree.Tag.PARENS)) {
            this.printExpr(var1.cond);
         } else {
            this.print("(");
            this.printExpr(var1.cond);
            this.print(")");
         }

         this.print(" ");
         this.printStat(var1.body);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      try {
         this.print("for (");
         if (var1.init.nonEmpty()) {
            if (((JCTree.JCStatement)var1.init.head).hasTag(JCTree.Tag.VARDEF)) {
               this.printExpr((JCTree)var1.init.head);

               for(List var2 = var1.init.tail; var2.nonEmpty(); var2 = var2.tail) {
                  JCTree.JCVariableDecl var3 = (JCTree.JCVariableDecl)var2.head;
                  this.print(", " + var3.name + " = ");
                  this.printExpr(var3.init);
               }
            } else {
               this.printExprs(var1.init);
            }
         }

         this.print("; ");
         if (var1.cond != null) {
            this.printExpr(var1.cond);
         }

         this.print("; ");
         this.printExprs(var1.step);
         this.print(") ");
         this.printStat(var1.body);
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      try {
         this.print("for (");
         this.printExpr(var1.var);
         this.print(" : ");
         this.printExpr(var1.expr);
         this.print(") ");
         this.printStat(var1.body);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitLabelled(JCTree.JCLabeledStatement var1) {
      try {
         this.print(var1.label + ": ");
         this.printStat(var1.body);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      try {
         this.print("switch ");
         if (var1.selector.hasTag(JCTree.Tag.PARENS)) {
            this.printExpr(var1.selector);
         } else {
            this.print("(");
            this.printExpr(var1.selector);
            this.print(")");
         }

         this.print(" {");
         this.println();
         this.printStats(var1.cases);
         this.align();
         this.print("}");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitCase(JCTree.JCCase var1) {
      try {
         if (var1.pat == null) {
            this.print("default");
         } else {
            this.print("case ");
            this.printExpr(var1.pat);
         }

         this.print(": ");
         this.println();
         this.indent();
         this.printStats(var1.stats);
         this.undent();
         this.align();
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitSynchronized(JCTree.JCSynchronized var1) {
      try {
         this.print("synchronized ");
         if (var1.lock.hasTag(JCTree.Tag.PARENS)) {
            this.printExpr(var1.lock);
         } else {
            this.print("(");
            this.printExpr(var1.lock);
            this.print(")");
         }

         this.print(" ");
         this.printStat(var1.body);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTry(JCTree.JCTry var1) {
      try {
         this.print("try ");
         if (var1.resources.nonEmpty()) {
            this.print("(");
            boolean var2 = true;

            for(Iterator var3 = var1.resources.iterator(); var3.hasNext(); var2 = false) {
               JCTree var4 = (JCTree)var3.next();
               if (!var2) {
                  this.println();
                  this.indent();
               }

               this.printStat(var4);
            }

            this.print(") ");
         }

         this.printStat(var1.body);

         for(List var6 = var1.catchers; var6.nonEmpty(); var6 = var6.tail) {
            this.printStat((JCTree)var6.head);
         }

         if (var1.finalizer != null) {
            this.print(" finally ");
            this.printStat(var1.finalizer);
         }

      } catch (IOException var5) {
         throw new UncheckedIOException(var5);
      }
   }

   public void visitCatch(JCTree.JCCatch var1) {
      try {
         this.print(" catch (");
         this.printExpr(var1.param);
         this.print(") ");
         this.printStat(var1.body);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitConditional(JCTree.JCConditional var1) {
      try {
         this.open(this.prec, 3);
         this.printExpr(var1.cond, 4);
         this.print(" ? ");
         this.printExpr(var1.truepart);
         this.print(" : ");
         this.printExpr(var1.falsepart, 3);
         this.close(this.prec, 3);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitIf(JCTree.JCIf var1) {
      try {
         this.print("if ");
         if (var1.cond.hasTag(JCTree.Tag.PARENS)) {
            this.printExpr(var1.cond);
         } else {
            this.print("(");
            this.printExpr(var1.cond);
            this.print(")");
         }

         this.print(" ");
         this.printStat(var1.thenpart);
         if (var1.elsepart != null) {
            this.print(" else ");
            this.printStat(var1.elsepart);
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitExec(JCTree.JCExpressionStatement var1) {
      try {
         this.printExpr(var1.expr);
         if (this.prec == -1) {
            this.print(";");
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitBreak(JCTree.JCBreak var1) {
      try {
         this.print("break");
         if (var1.label != null) {
            this.print(" " + var1.label);
         }

         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitContinue(JCTree.JCContinue var1) {
      try {
         this.print("continue");
         if (var1.label != null) {
            this.print(" " + var1.label);
         }

         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitReturn(JCTree.JCReturn var1) {
      try {
         this.print("return");
         if (var1.expr != null) {
            this.print(" ");
            this.printExpr(var1.expr);
         }

         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitThrow(JCTree.JCThrow var1) {
      try {
         this.print("throw ");
         this.printExpr(var1.expr);
         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitAssert(JCTree.JCAssert var1) {
      try {
         this.print("assert ");
         this.printExpr(var1.cond);
         if (var1.detail != null) {
            this.print(" : ");
            this.printExpr(var1.detail);
         }

         this.print(";");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      try {
         if (!var1.typeargs.isEmpty()) {
            if (var1.meth.hasTag(JCTree.Tag.SELECT)) {
               JCTree.JCFieldAccess var2 = (JCTree.JCFieldAccess)var1.meth;
               this.printExpr(var2.selected);
               this.print(".<");
               this.printExprs(var1.typeargs);
               this.print(">" + var2.name);
            } else {
               this.print("<");
               this.printExprs(var1.typeargs);
               this.print(">");
               this.printExpr(var1.meth);
            }
         } else {
            this.printExpr(var1.meth);
         }

         this.print("(");
         this.printExprs(var1.args);
         this.print(")");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitNewClass(JCTree.JCNewClass var1) {
      try {
         if (var1.encl != null) {
            this.printExpr(var1.encl);
            this.print(".");
         }

         this.print("new ");
         if (!var1.typeargs.isEmpty()) {
            this.print("<");
            this.printExprs(var1.typeargs);
            this.print(">");
         }

         if (var1.def != null && var1.def.mods.annotations.nonEmpty()) {
            this.printTypeAnnotations(var1.def.mods.annotations);
         }

         this.printExpr(var1.clazz);
         this.print("(");
         this.printExprs(var1.args);
         this.print(")");
         if (var1.def != null) {
            Name var2 = this.enclClassName;
            this.enclClassName = var1.def.name != null ? var1.def.name : (var1.type != null && var1.type.tsym.name != var1.type.tsym.name.table.names.empty ? var1.type.tsym.name : null);
            if ((var1.def.mods.flags & 16384L) != 0L) {
               this.print("/*enum*/");
            }

            this.printBlock(var1.def.defs);
            this.enclClassName = var2;
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      try {
         if (var1.elemtype != null) {
            this.print("new ");
            JCTree.JCExpression var2 = var1.elemtype;
            this.printBaseElementType(var2);
            if (!var1.annotations.isEmpty()) {
               this.print(' ');
               this.printTypeAnnotations(var1.annotations);
            }

            if (var1.elems != null) {
               this.print("[]");
            }

            int var3 = 0;
            List var4 = var1.dimAnnotations;

            for(List var5 = var1.dims; var5.nonEmpty(); var5 = var5.tail) {
               if (var4.size() > var3 && !((List)var4.get(var3)).isEmpty()) {
                  this.print(' ');
                  this.printTypeAnnotations((List)var4.get(var3));
               }

               this.print("[");
               ++var3;
               this.printExpr((JCTree)var5.head);
               this.print("]");
            }

            this.printBrackets(var2);
         }

         if (var1.elems != null) {
            this.print("{");
            this.printExprs(var1.elems);
            this.print("}");
         }

      } catch (IOException var6) {
         throw new UncheckedIOException(var6);
      }
   }

   public void visitLambda(JCTree.JCLambda var1) {
      try {
         this.print("(");
         if (var1.paramKind == JCTree.JCLambda.ParameterKind.EXPLICIT) {
            this.printExprs(var1.params);
         } else {
            String var2 = "";

            for(Iterator var3 = var1.params.iterator(); var3.hasNext(); var2 = ",") {
               JCTree.JCVariableDecl var4 = (JCTree.JCVariableDecl)var3.next();
               this.print(var2);
               this.print(var4.name);
            }
         }

         this.print(")->");
         this.printExpr(var1.body);
      } catch (IOException var5) {
         throw new UncheckedIOException(var5);
      }
   }

   public void visitParens(JCTree.JCParens var1) {
      try {
         this.print("(");
         this.printExpr(var1.expr);
         this.print(")");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitAssign(JCTree.JCAssign var1) {
      try {
         this.open(this.prec, 1);
         this.printExpr(var1.lhs, 2);
         this.print(" = ");
         this.printExpr(var1.rhs, 1);
         this.close(this.prec, 1);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public String operatorName(JCTree.Tag var1) {
      switch (var1) {
         case POS:
            return "+";
         case NEG:
            return "-";
         case NOT:
            return "!";
         case COMPL:
            return "~";
         case PREINC:
            return "++";
         case PREDEC:
            return "--";
         case POSTINC:
            return "++";
         case POSTDEC:
            return "--";
         case NULLCHK:
            return "<*nullchk*>";
         case OR:
            return "||";
         case AND:
            return "&&";
         case EQ:
            return "==";
         case NE:
            return "!=";
         case LT:
            return "<";
         case GT:
            return ">";
         case LE:
            return "<=";
         case GE:
            return ">=";
         case BITOR:
            return "|";
         case BITXOR:
            return "^";
         case BITAND:
            return "&";
         case SL:
            return "<<";
         case SR:
            return ">>";
         case USR:
            return ">>>";
         case PLUS:
            return "+";
         case MINUS:
            return "-";
         case MUL:
            return "*";
         case DIV:
            return "/";
         case MOD:
            return "%";
         default:
            throw new Error();
      }
   }

   public void visitAssignop(JCTree.JCAssignOp var1) {
      try {
         this.open(this.prec, 2);
         this.printExpr(var1.lhs, 3);
         this.print(" " + this.operatorName(var1.getTag().noAssignOp()) + "= ");
         this.printExpr(var1.rhs, 2);
         this.close(this.prec, 2);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitUnary(JCTree.JCUnary var1) {
      try {
         int var2 = TreeInfo.opPrec(var1.getTag());
         String var3 = this.operatorName(var1.getTag());
         this.open(this.prec, var2);
         if (!var1.getTag().isPostUnaryOp()) {
            this.print(var3);
            this.printExpr(var1.arg, var2);
         } else {
            this.printExpr(var1.arg, var2);
            this.print(var3);
         }

         this.close(this.prec, var2);
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public void visitBinary(JCTree.JCBinary var1) {
      try {
         int var2 = TreeInfo.opPrec(var1.getTag());
         String var3 = this.operatorName(var1.getTag());
         this.open(this.prec, var2);
         this.printExpr(var1.lhs, var2);
         this.print(" " + var3 + " ");
         this.printExpr(var1.rhs, var2 + 1);
         this.close(this.prec, var2);
      } catch (IOException var4) {
         throw new UncheckedIOException(var4);
      }
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      try {
         this.open(this.prec, 14);
         this.print("(");
         this.printExpr(var1.clazz);
         this.print(")");
         this.printExpr(var1.expr, 14);
         this.close(this.prec, 14);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeTest(JCTree.JCInstanceOf var1) {
      try {
         this.open(this.prec, 10);
         this.printExpr(var1.expr, 10);
         this.print(" instanceof ");
         this.printExpr(var1.clazz, 11);
         this.close(this.prec, 10);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      try {
         this.printExpr(var1.indexed, 15);
         this.print("[");
         this.printExpr(var1.index);
         this.print("]");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      try {
         this.printExpr(var1.selected, 15);
         this.print("." + var1.name);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitReference(JCTree.JCMemberReference var1) {
      try {
         this.printExpr(var1.expr);
         this.print("::");
         if (var1.typeargs != null) {
            this.print("<");
            this.printExprs(var1.typeargs);
            this.print(">");
         }

         this.print(var1.getMode() == MemberReferenceTree.ReferenceMode.INVOKE ? var1.name : "new");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitIdent(JCTree.JCIdent var1) {
      try {
         this.print(var1.name);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitLiteral(JCTree.JCLiteral var1) {
      try {
         switch (var1.typetag) {
            case INT:
               this.print(var1.value.toString());
               break;
            case LONG:
               this.print(var1.value + "L");
               break;
            case FLOAT:
               this.print(var1.value + "F");
               break;
            case DOUBLE:
               this.print(var1.value.toString());
               break;
            case CHAR:
               this.print("'" + Convert.quote(String.valueOf((char)((Number)var1.value).intValue())) + "'");
               break;
            case BOOLEAN:
               this.print(((Number)var1.value).intValue() == 1 ? "true" : "false");
               break;
            case BOT:
               this.print("null");
               break;
            default:
               this.print("\"" + Convert.quote(var1.value.toString()) + "\"");
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
      try {
         switch (var1.typetag) {
            case INT:
               this.print("int");
               break;
            case LONG:
               this.print("long");
               break;
            case FLOAT:
               this.print("float");
               break;
            case DOUBLE:
               this.print("double");
               break;
            case CHAR:
               this.print("char");
               break;
            case BOOLEAN:
               this.print("boolean");
               break;
            case BOT:
            default:
               this.print("error");
               break;
            case BYTE:
               this.print("byte");
               break;
            case SHORT:
               this.print("short");
               break;
            case VOID:
               this.print("void");
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
      try {
         this.printBaseElementType(var1);
         this.printBrackets(var1);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   private void printBaseElementType(JCTree var1) throws IOException {
      this.printExpr(TreeInfo.innermostType(var1));
   }

   private void printBrackets(JCTree var1) throws IOException {
      Object var2 = var1;

      while(true) {
         if (((JCTree)var2).hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
            JCTree.JCAnnotatedType var3 = (JCTree.JCAnnotatedType)var2;
            var2 = var3.underlyingType;
            if (((JCTree)var2).hasTag(JCTree.Tag.TYPEARRAY)) {
               this.print(' ');
               this.printTypeAnnotations(var3.annotations);
            }
         }

         if (!((JCTree)var2).hasTag(JCTree.Tag.TYPEARRAY)) {
            return;
         }

         this.print("[]");
         var2 = ((JCTree.JCArrayTypeTree)var2).elemtype;
      }
   }

   public void visitTypeApply(JCTree.JCTypeApply var1) {
      try {
         this.printExpr(var1.clazz);
         this.print("<");
         this.printExprs(var1.arguments);
         this.print(">");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeUnion(JCTree.JCTypeUnion var1) {
      try {
         this.printExprs(var1.alternatives, " | ");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeIntersection(JCTree.JCTypeIntersection var1) {
      try {
         this.printExprs(var1.bounds, " & ");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeParameter(JCTree.JCTypeParameter var1) {
      try {
         if (var1.annotations.nonEmpty()) {
            this.printTypeAnnotations(var1.annotations);
         }

         this.print(var1.name);
         if (var1.bounds.nonEmpty()) {
            this.print(" extends ");
            this.printExprs(var1.bounds, " & ");
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitWildcard(JCTree.JCWildcard var1) {
      try {
         this.print(var1.kind);
         if (var1.kind.kind != BoundKind.UNBOUND) {
            this.printExpr(var1.inner);
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTypeBoundKind(JCTree.TypeBoundKind var1) {
      try {
         this.print(String.valueOf(var1.kind));
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitErroneous(JCTree.JCErroneous var1) {
      try {
         this.print("(ERROR)");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitLetExpr(JCTree.LetExpr var1) {
      try {
         this.print("(let " + var1.defs + " in " + var1.expr + ")");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitModifiers(JCTree.JCModifiers var1) {
      try {
         this.printAnnotations(var1.annotations);
         this.printFlags(var1.flags);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitAnnotation(JCTree.JCAnnotation var1) {
      try {
         this.print("@");
         this.printExpr(var1.annotationType);
         this.print("(");
         this.printExprs(var1.args);
         this.print(")");
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
      try {
         if (var1.underlyingType.hasTag(JCTree.Tag.SELECT)) {
            JCTree.JCFieldAccess var2 = (JCTree.JCFieldAccess)var1.underlyingType;
            this.printExpr(var2.selected, 15);
            this.print(".");
            this.printTypeAnnotations(var1.annotations);
            this.print(var2.name);
         } else if (var1.underlyingType.hasTag(JCTree.Tag.TYPEARRAY)) {
            this.printBaseElementType(var1);
            this.printBrackets(var1);
         } else {
            this.printTypeAnnotations(var1.annotations);
            this.printExpr(var1.underlyingType);
         }

      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void visitTree(JCTree var1) {
      try {
         this.print("(UNKNOWN: " + var1 + ")");
         this.println();
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   private static class UncheckedIOException extends Error {
      static final long serialVersionUID = -4032692679158424751L;

      UncheckedIOException(IOException var1) {
         super(var1.getMessage(), var1);
      }
   }
}
