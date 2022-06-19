package com.sun.tools.javac.tree;

import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import java.util.Iterator;
import javax.tools.JavaFileObject;

public class TreeMaker implements JCTree.Factory {
   protected static final Context.Key treeMakerKey = new Context.Key();
   public int pos = -1;
   public JCTree.JCCompilationUnit toplevel;
   Names names;
   Types types;
   Symtab syms;
   AnnotationBuilder annotationBuilder = new AnnotationBuilder();

   public static TreeMaker instance(Context var0) {
      TreeMaker var1 = (TreeMaker)var0.get(treeMakerKey);
      if (var1 == null) {
         var1 = new TreeMaker(var0);
      }

      return var1;
   }

   protected TreeMaker(Context var1) {
      var1.put((Context.Key)treeMakerKey, (Object)this);
      this.pos = -1;
      this.toplevel = null;
      this.names = Names.instance(var1);
      this.syms = Symtab.instance(var1);
      this.types = Types.instance(var1);
   }

   protected TreeMaker(JCTree.JCCompilationUnit var1, Names var2, Types var3, Symtab var4) {
      this.pos = 0;
      this.toplevel = var1;
      this.names = var2;
      this.types = var3;
      this.syms = var4;
   }

   public TreeMaker forToplevel(JCTree.JCCompilationUnit var1) {
      return new TreeMaker(var1, this.names, this.types, this.syms);
   }

   public TreeMaker at(int var1) {
      this.pos = var1;
      return this;
   }

   public TreeMaker at(JCDiagnostic.DiagnosticPosition var1) {
      this.pos = var1 == null ? -1 : var1.getStartPosition();
      return this;
   }

   public JCTree.JCCompilationUnit TopLevel(List var1, JCTree.JCExpression var2, List var3) {
      Assert.checkNonNull(var1);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         JCTree var5 = (JCTree)var4.next();
         Assert.check(var5 instanceof JCTree.JCClassDecl || var5 instanceof JCTree.JCImport || var5 instanceof JCTree.JCSkip || var5 instanceof JCTree.JCErroneous || var5 instanceof JCTree.JCExpressionStatement && ((JCTree.JCExpressionStatement)var5).expr instanceof JCTree.JCErroneous, var5.getClass().getSimpleName());
      }

      JCTree.JCCompilationUnit var6 = new JCTree.JCCompilationUnit(var1, var2, var3, (JavaFileObject)null, (Symbol.PackageSymbol)null, (Scope.ImportScope)null, (Scope.StarImportScope)null);
      var6.pos = this.pos;
      return var6;
   }

   public JCTree.JCImport Import(JCTree var1, boolean var2) {
      JCTree.JCImport var3 = new JCTree.JCImport(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCClassDecl ClassDef(JCTree.JCModifiers var1, Name var2, List var3, JCTree.JCExpression var4, List var5, List var6) {
      JCTree.JCClassDecl var7 = new JCTree.JCClassDecl(var1, var2, var3, var4, var5, var6, (Symbol.ClassSymbol)null);
      var7.pos = this.pos;
      return var7;
   }

   public JCTree.JCMethodDecl MethodDef(JCTree.JCModifiers var1, Name var2, JCTree.JCExpression var3, List var4, List var5, List var6, JCTree.JCBlock var7, JCTree.JCExpression var8) {
      return this.MethodDef(var1, var2, var3, var4, (JCTree.JCVariableDecl)null, var5, var6, var7, var8);
   }

   public JCTree.JCMethodDecl MethodDef(JCTree.JCModifiers var1, Name var2, JCTree.JCExpression var3, List var4, JCTree.JCVariableDecl var5, List var6, List var7, JCTree.JCBlock var8, JCTree.JCExpression var9) {
      JCTree.JCMethodDecl var10 = new JCTree.JCMethodDecl(var1, var2, var3, var4, var5, var6, var7, var8, var9, (Symbol.MethodSymbol)null);
      var10.pos = this.pos;
      return var10;
   }

   public JCTree.JCVariableDecl VarDef(JCTree.JCModifiers var1, Name var2, JCTree.JCExpression var3, JCTree.JCExpression var4) {
      JCTree.JCVariableDecl var5 = new JCTree.JCVariableDecl(var1, var2, var3, var4, (Symbol.VarSymbol)null);
      var5.pos = this.pos;
      return var5;
   }

   public JCTree.JCVariableDecl ReceiverVarDef(JCTree.JCModifiers var1, JCTree.JCExpression var2, JCTree.JCExpression var3) {
      JCTree.JCVariableDecl var4 = new JCTree.JCVariableDecl(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCSkip Skip() {
      JCTree.JCSkip var1 = new JCTree.JCSkip();
      var1.pos = this.pos;
      return var1;
   }

   public JCTree.JCBlock Block(long var1, List var3) {
      JCTree.JCBlock var4 = new JCTree.JCBlock(var1, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCDoWhileLoop DoLoop(JCTree.JCStatement var1, JCTree.JCExpression var2) {
      JCTree.JCDoWhileLoop var3 = new JCTree.JCDoWhileLoop(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCWhileLoop WhileLoop(JCTree.JCExpression var1, JCTree.JCStatement var2) {
      JCTree.JCWhileLoop var3 = new JCTree.JCWhileLoop(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCForLoop ForLoop(List var1, JCTree.JCExpression var2, List var3, JCTree.JCStatement var4) {
      JCTree.JCForLoop var5 = new JCTree.JCForLoop(var1, var2, var3, var4);
      var5.pos = this.pos;
      return var5;
   }

   public JCTree.JCEnhancedForLoop ForeachLoop(JCTree.JCVariableDecl var1, JCTree.JCExpression var2, JCTree.JCStatement var3) {
      JCTree.JCEnhancedForLoop var4 = new JCTree.JCEnhancedForLoop(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCLabeledStatement Labelled(Name var1, JCTree.JCStatement var2) {
      JCTree.JCLabeledStatement var3 = new JCTree.JCLabeledStatement(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCSwitch Switch(JCTree.JCExpression var1, List var2) {
      JCTree.JCSwitch var3 = new JCTree.JCSwitch(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCCase Case(JCTree.JCExpression var1, List var2) {
      JCTree.JCCase var3 = new JCTree.JCCase(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCSynchronized Synchronized(JCTree.JCExpression var1, JCTree.JCBlock var2) {
      JCTree.JCSynchronized var3 = new JCTree.JCSynchronized(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCTry Try(JCTree.JCBlock var1, List var2, JCTree.JCBlock var3) {
      return this.Try(List.nil(), var1, var2, var3);
   }

   public JCTree.JCTry Try(List var1, JCTree.JCBlock var2, List var3, JCTree.JCBlock var4) {
      JCTree.JCTry var5 = new JCTree.JCTry(var1, var2, var3, var4);
      var5.pos = this.pos;
      return var5;
   }

   public JCTree.JCCatch Catch(JCTree.JCVariableDecl var1, JCTree.JCBlock var2) {
      JCTree.JCCatch var3 = new JCTree.JCCatch(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCConditional Conditional(JCTree.JCExpression var1, JCTree.JCExpression var2, JCTree.JCExpression var3) {
      JCTree.JCConditional var4 = new JCTree.JCConditional(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCIf If(JCTree.JCExpression var1, JCTree.JCStatement var2, JCTree.JCStatement var3) {
      JCTree.JCIf var4 = new JCTree.JCIf(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCExpressionStatement Exec(JCTree.JCExpression var1) {
      JCTree.JCExpressionStatement var2 = new JCTree.JCExpressionStatement(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCBreak Break(Name var1) {
      JCTree.JCBreak var2 = new JCTree.JCBreak(var1, (JCTree)null);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCContinue Continue(Name var1) {
      JCTree.JCContinue var2 = new JCTree.JCContinue(var1, (JCTree)null);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCReturn Return(JCTree.JCExpression var1) {
      JCTree.JCReturn var2 = new JCTree.JCReturn(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCThrow Throw(JCTree.JCExpression var1) {
      JCTree.JCThrow var2 = new JCTree.JCThrow(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCAssert Assert(JCTree.JCExpression var1, JCTree.JCExpression var2) {
      JCTree.JCAssert var3 = new JCTree.JCAssert(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCMethodInvocation Apply(List var1, JCTree.JCExpression var2, List var3) {
      JCTree.JCMethodInvocation var4 = new JCTree.JCMethodInvocation(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCNewClass NewClass(JCTree.JCExpression var1, List var2, JCTree.JCExpression var3, List var4, JCTree.JCClassDecl var5) {
      JCTree.JCNewClass var6 = new JCTree.JCNewClass(var1, var2, var3, var4, var5);
      var6.pos = this.pos;
      return var6;
   }

   public JCTree.JCNewArray NewArray(JCTree.JCExpression var1, List var2, List var3) {
      JCTree.JCNewArray var4 = new JCTree.JCNewArray(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCLambda Lambda(List var1, JCTree var2) {
      JCTree.JCLambda var3 = new JCTree.JCLambda(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCParens Parens(JCTree.JCExpression var1) {
      JCTree.JCParens var2 = new JCTree.JCParens(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCAssign Assign(JCTree.JCExpression var1, JCTree.JCExpression var2) {
      JCTree.JCAssign var3 = new JCTree.JCAssign(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCAssignOp Assignop(JCTree.Tag var1, JCTree var2, JCTree var3) {
      JCTree.JCAssignOp var4 = new JCTree.JCAssignOp(var1, var2, var3, (Symbol)null);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCUnary Unary(JCTree.Tag var1, JCTree.JCExpression var2) {
      JCTree.JCUnary var3 = new JCTree.JCUnary(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCBinary Binary(JCTree.Tag var1, JCTree.JCExpression var2, JCTree.JCExpression var3) {
      JCTree.JCBinary var4 = new JCTree.JCBinary(var1, var2, var3, (Symbol)null);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCTypeCast TypeCast(JCTree var1, JCTree.JCExpression var2) {
      JCTree.JCTypeCast var3 = new JCTree.JCTypeCast(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCInstanceOf TypeTest(JCTree.JCExpression var1, JCTree var2) {
      JCTree.JCInstanceOf var3 = new JCTree.JCInstanceOf(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCArrayAccess Indexed(JCTree.JCExpression var1, JCTree.JCExpression var2) {
      JCTree.JCArrayAccess var3 = new JCTree.JCArrayAccess(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCFieldAccess Select(JCTree.JCExpression var1, Name var2) {
      JCTree.JCFieldAccess var3 = new JCTree.JCFieldAccess(var1, var2, (Symbol)null);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCMemberReference Reference(MemberReferenceTree.ReferenceMode var1, Name var2, JCTree.JCExpression var3, List var4) {
      JCTree.JCMemberReference var5 = new JCTree.JCMemberReference(var1, var2, var3, var4);
      var5.pos = this.pos;
      return var5;
   }

   public JCTree.JCIdent Ident(Name var1) {
      JCTree.JCIdent var2 = new JCTree.JCIdent(var1, (Symbol)null);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCLiteral Literal(TypeTag var1, Object var2) {
      JCTree.JCLiteral var3 = new JCTree.JCLiteral(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCPrimitiveTypeTree TypeIdent(TypeTag var1) {
      JCTree.JCPrimitiveTypeTree var2 = new JCTree.JCPrimitiveTypeTree(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCArrayTypeTree TypeArray(JCTree.JCExpression var1) {
      JCTree.JCArrayTypeTree var2 = new JCTree.JCArrayTypeTree(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCTypeApply TypeApply(JCTree.JCExpression var1, List var2) {
      JCTree.JCTypeApply var3 = new JCTree.JCTypeApply(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCTypeUnion TypeUnion(List var1) {
      JCTree.JCTypeUnion var2 = new JCTree.JCTypeUnion(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCTypeIntersection TypeIntersection(List var1) {
      JCTree.JCTypeIntersection var2 = new JCTree.JCTypeIntersection(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCTypeParameter TypeParameter(Name var1, List var2) {
      return this.TypeParameter(var1, var2, List.nil());
   }

   public JCTree.JCTypeParameter TypeParameter(Name var1, List var2, List var3) {
      JCTree.JCTypeParameter var4 = new JCTree.JCTypeParameter(var1, var2, var3);
      var4.pos = this.pos;
      return var4;
   }

   public JCTree.JCWildcard Wildcard(JCTree.TypeBoundKind var1, JCTree var2) {
      JCTree.JCWildcard var3 = new JCTree.JCWildcard(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.TypeBoundKind TypeBoundKind(BoundKind var1) {
      JCTree.TypeBoundKind var2 = new JCTree.TypeBoundKind(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.JCAnnotation Annotation(JCTree var1, List var2) {
      JCTree.JCAnnotation var3 = new JCTree.JCAnnotation(JCTree.Tag.ANNOTATION, var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCAnnotation TypeAnnotation(JCTree var1, List var2) {
      JCTree.JCAnnotation var3 = new JCTree.JCAnnotation(JCTree.Tag.TYPE_ANNOTATION, var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCModifiers Modifiers(long var1, List var3) {
      JCTree.JCModifiers var4 = new JCTree.JCModifiers(var1, var3);
      boolean var5 = (var1 & 8796093033983L) == 0L;
      var4.pos = var5 && var3.isEmpty() ? -1 : this.pos;
      return var4;
   }

   public JCTree.JCModifiers Modifiers(long var1) {
      return this.Modifiers(var1, List.nil());
   }

   public JCTree.JCAnnotatedType AnnotatedType(List var1, JCTree.JCExpression var2) {
      JCTree.JCAnnotatedType var3 = new JCTree.JCAnnotatedType(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCErroneous Erroneous() {
      return this.Erroneous(List.nil());
   }

   public JCTree.JCErroneous Erroneous(List var1) {
      JCTree.JCErroneous var2 = new JCTree.JCErroneous(var1);
      var2.pos = this.pos;
      return var2;
   }

   public JCTree.LetExpr LetExpr(List var1, JCTree var2) {
      JCTree.LetExpr var3 = new JCTree.LetExpr(var1, var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCClassDecl AnonymousClassDef(JCTree.JCModifiers var1, List var2) {
      return this.ClassDef(var1, this.names.empty, List.nil(), (JCTree.JCExpression)null, List.nil(), var2);
   }

   public JCTree.LetExpr LetExpr(JCTree.JCVariableDecl var1, JCTree var2) {
      JCTree.LetExpr var3 = new JCTree.LetExpr(List.of(var1), var2);
      var3.pos = this.pos;
      return var3;
   }

   public JCTree.JCIdent Ident(Symbol var1) {
      return (JCTree.JCIdent)(new JCTree.JCIdent(var1.name != this.names.empty ? var1.name : var1.flatName(), var1)).setPos(this.pos).setType(var1.type);
   }

   public JCTree.JCExpression Select(JCTree.JCExpression var1, Symbol var2) {
      return (new JCTree.JCFieldAccess(var1, var2.name, var2)).setPos(this.pos).setType(var2.type);
   }

   public JCTree.JCExpression QualIdent(Symbol var1) {
      return (JCTree.JCExpression)(this.isUnqualifiable(var1) ? this.Ident(var1) : this.Select(this.QualIdent(var1.owner), var1));
   }

   public JCTree.JCExpression Ident(JCTree.JCVariableDecl var1) {
      return this.Ident((Symbol)var1.sym);
   }

   public List Idents(List var1) {
      ListBuffer var2 = new ListBuffer();

      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         var2.append(this.Ident((JCTree.JCVariableDecl)var3.head));
      }

      return var2.toList();
   }

   public JCTree.JCExpression This(Type var1) {
      return this.Ident((Symbol)(new Symbol.VarSymbol(16L, this.names._this, var1, var1.tsym)));
   }

   public JCTree.JCExpression QualThis(Type var1) {
      return this.Select(this.Type(var1), (Symbol)(new Symbol.VarSymbol(16L, this.names._this, var1, var1.tsym)));
   }

   public JCTree.JCExpression ClassLiteral(Symbol.ClassSymbol var1) {
      return this.ClassLiteral(var1.type);
   }

   public JCTree.JCExpression ClassLiteral(Type var1) {
      Symbol.VarSymbol var2 = new Symbol.VarSymbol(25L, this.names._class, var1, var1.tsym);
      return this.Select(this.Type(var1), (Symbol)var2);
   }

   public JCTree.JCIdent Super(Type var1, Symbol.TypeSymbol var2) {
      return this.Ident((Symbol)(new Symbol.VarSymbol(16L, this.names._super, var1, var2)));
   }

   public JCTree.JCMethodInvocation App(JCTree.JCExpression var1, List var2) {
      return this.Apply((List)null, var1, var2).setType(var1.type.getReturnType());
   }

   public JCTree.JCMethodInvocation App(JCTree.JCExpression var1) {
      return this.Apply((List)null, var1, List.nil()).setType(var1.type.getReturnType());
   }

   public JCTree.JCExpression Create(Symbol var1, List var2) {
      Type var3 = var1.owner.erasure(this.types);
      JCTree.JCNewClass var4 = this.NewClass((JCTree.JCExpression)null, (List)null, this.Type(var3), var2, (JCTree.JCClassDecl)null);
      var4.constructor = var1;
      var4.setType(var3);
      return var4;
   }

   public JCTree.JCExpression Type(Type var1) {
      if (var1 == null) {
         return null;
      } else {
         Object var2;
         switch (var1.getTag()) {
            case BYTE:
            case CHAR:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
            case VOID:
               var2 = this.TypeIdent(var1.getTag());
               break;
            case TYPEVAR:
               var2 = this.Ident((Symbol)var1.tsym);
               break;
            case WILDCARD:
               Type.WildcardType var5 = (Type.WildcardType)var1;
               var2 = this.Wildcard(this.TypeBoundKind(var5.kind), this.Type(var5.type));
               break;
            case CLASS:
               Type var3 = var1.getEnclosingType();
               JCTree.JCExpression var4 = var3.hasTag(TypeTag.CLASS) && var1.tsym.owner.kind == 2 ? this.Select(this.Type(var3), (Symbol)var1.tsym) : this.QualIdent(var1.tsym);
               var2 = var1.getTypeArguments().isEmpty() ? var4 : this.TypeApply(var4, this.Types(var1.getTypeArguments()));
               break;
            case ARRAY:
               var2 = this.TypeArray(this.Type(this.types.elemtype(var1)));
               break;
            case ERROR:
               var2 = this.TypeIdent(TypeTag.ERROR);
               break;
            default:
               throw new AssertionError("unexpected type: " + var1);
         }

         return ((JCTree.JCExpression)var2).setType(var1);
      }
   }

   public List Types(List var1) {
      ListBuffer var2 = new ListBuffer();

      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         var2.append(this.Type((Type)var3.head));
      }

      return var2.toList();
   }

   public JCTree.JCVariableDecl VarDef(Symbol.VarSymbol var1, JCTree.JCExpression var2) {
      return (JCTree.JCVariableDecl)(new JCTree.JCVariableDecl(this.Modifiers(var1.flags(), this.Annotations(var1.getRawAttributes())), var1.name, this.Type(var1.type), var2, var1)).setPos(this.pos).setType(var1.type);
   }

   public List Annotations(List var1) {
      if (var1 == null) {
         return List.nil();
      } else {
         ListBuffer var2 = new ListBuffer();

         for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
            Attribute var4 = (Attribute)var3.head;
            var2.append(this.Annotation(var4));
         }

         return var2.toList();
      }
   }

   public JCTree.JCLiteral Literal(Object var1) {
      JCTree.JCLiteral var2 = null;
      if (var1 instanceof String) {
         var2 = this.Literal(TypeTag.CLASS, var1).setType(this.syms.stringType.constType(var1));
      } else if (var1 instanceof Integer) {
         var2 = this.Literal(TypeTag.INT, var1).setType(this.syms.intType.constType(var1));
      } else if (var1 instanceof Long) {
         var2 = this.Literal(TypeTag.LONG, var1).setType(this.syms.longType.constType(var1));
      } else if (var1 instanceof Byte) {
         var2 = this.Literal(TypeTag.BYTE, var1).setType(this.syms.byteType.constType(var1));
      } else {
         int var3;
         if (var1 instanceof Character) {
            var3 = ((Character)var1).toString().charAt(0);
            var2 = this.Literal(TypeTag.CHAR, var1).setType(this.syms.charType.constType(var3));
         } else if (var1 instanceof Double) {
            var2 = this.Literal(TypeTag.DOUBLE, var1).setType(this.syms.doubleType.constType(var1));
         } else if (var1 instanceof Float) {
            var2 = this.Literal(TypeTag.FLOAT, var1).setType(this.syms.floatType.constType(var1));
         } else if (var1 instanceof Short) {
            var2 = this.Literal(TypeTag.SHORT, var1).setType(this.syms.shortType.constType(var1));
         } else {
            if (!(var1 instanceof Boolean)) {
               throw new AssertionError(var1);
            }

            var3 = (Boolean)var1 ? 1 : 0;
            var2 = this.Literal(TypeTag.BOOLEAN, var3).setType(this.syms.booleanType.constType(var3));
         }
      }

      return var2;
   }

   public JCTree.JCAnnotation Annotation(Attribute var1) {
      return this.annotationBuilder.translate((Attribute.Compound)var1);
   }

   public JCTree.JCAnnotation TypeAnnotation(Attribute var1) {
      return this.annotationBuilder.translate((Attribute.TypeCompound)var1);
   }

   public JCTree.JCMethodDecl MethodDef(Symbol.MethodSymbol var1, JCTree.JCBlock var2) {
      return this.MethodDef(var1, var1.type, var2);
   }

   public JCTree.JCMethodDecl MethodDef(Symbol.MethodSymbol var1, Type var2, JCTree.JCBlock var3) {
      return (JCTree.JCMethodDecl)(new JCTree.JCMethodDecl(this.Modifiers(var1.flags(), this.Annotations(var1.getRawAttributes())), var1.name, this.Type(var2.getReturnType()), this.TypeParams(var2.getTypeArguments()), (JCTree.JCVariableDecl)null, this.Params(var2.getParameterTypes(), var1), this.Types(var2.getThrownTypes()), var3, (JCTree.JCExpression)null, var1)).setPos(this.pos).setType(var2);
   }

   public JCTree.JCTypeParameter TypeParam(Name var1, Type.TypeVar var2) {
      return (JCTree.JCTypeParameter)this.TypeParameter(var1, this.Types(this.types.getBounds(var2))).setPos(this.pos).setType(var2);
   }

   public List TypeParams(List var1) {
      ListBuffer var2 = new ListBuffer();

      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         var2.append(this.TypeParam(((Type)var3.head).tsym.name, (Type.TypeVar)var3.head));
      }

      return var2.toList();
   }

   public JCTree.JCVariableDecl Param(Name var1, Type var2, Symbol var3) {
      return this.VarDef(new Symbol.VarSymbol(8589934592L, var1, var2, var3), (JCTree.JCExpression)null);
   }

   public List Params(List var1, Symbol var2) {
      ListBuffer var3 = new ListBuffer();
      Symbol.MethodSymbol var4 = var2.kind == 16 ? (Symbol.MethodSymbol)var2 : null;
      if (var4 != null && var4.params != null && var1.length() == var4.params.length()) {
         Iterator var7 = ((Symbol.MethodSymbol)var2).params.iterator();

         while(var7.hasNext()) {
            Symbol.VarSymbol var8 = (Symbol.VarSymbol)var7.next();
            var3.append(this.VarDef(var8, (JCTree.JCExpression)null));
         }
      } else {
         int var5 = 0;

         for(List var6 = var1; var6.nonEmpty(); var6 = var6.tail) {
            var3.append(this.Param(this.paramName(var5++), (Type)var6.head, var2));
         }
      }

      return var3.toList();
   }

   public JCTree.JCStatement Call(JCTree.JCExpression var1) {
      return (JCTree.JCStatement)(var1.type.hasTag(TypeTag.VOID) ? this.Exec(var1) : this.Return(var1));
   }

   public JCTree.JCStatement Assignment(Symbol var1, JCTree.JCExpression var2) {
      return this.Exec(this.Assign(this.Ident(var1), var2).setType(var1.type));
   }

   public JCTree.JCArrayAccess Indexed(Symbol var1, JCTree.JCExpression var2) {
      JCTree.JCArrayAccess var3 = new JCTree.JCArrayAccess(this.QualIdent(var1), var2);
      var3.type = ((Type.ArrayType)var1.type).elemtype;
      return var3;
   }

   public JCTree.JCTypeCast TypeCast(Type var1, JCTree.JCExpression var2) {
      return (JCTree.JCTypeCast)this.TypeCast((JCTree)this.Type(var1), var2).setType(var1);
   }

   boolean isUnqualifiable(Symbol var1) {
      if (var1.name != this.names.empty && var1.owner != null && var1.owner != this.syms.rootPackage && var1.owner.kind != 16 && var1.owner.kind != 4) {
         if (var1.kind == 2 && this.toplevel != null) {
            Scope.Entry var2 = this.toplevel.namedImportScope.lookup(var1.name);
            if (var2.scope != null) {
               return var2.sym == var1 && var2.next().scope == null;
            }

            var2 = this.toplevel.packge.members().lookup(var1.name);
            if (var2.scope != null) {
               return var2.sym == var1 && var2.next().scope == null;
            }

            var2 = this.toplevel.starImportScope.lookup(var1.name);
            if (var2.scope != null) {
               return var2.sym == var1 && var2.next().scope == null;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public Name paramName(int var1) {
      return this.names.fromString("x" + var1);
   }

   public Name typaramName(int var1) {
      return this.names.fromString("A" + var1);
   }

   class AnnotationBuilder implements Attribute.Visitor {
      JCTree.JCExpression result = null;

      public void visitConstant(Attribute.Constant var1) {
         this.result = TreeMaker.this.Literal(var1.type.getTag(), var1.value);
      }

      public void visitClass(Attribute.Class var1) {
         this.result = TreeMaker.this.ClassLiteral(var1.classType).setType(TreeMaker.this.syms.classType);
      }

      public void visitEnum(Attribute.Enum var1) {
         this.result = TreeMaker.this.QualIdent(var1.value);
      }

      public void visitError(Attribute.Error var1) {
         this.result = TreeMaker.this.Erroneous();
      }

      public void visitCompound(Attribute.Compound var1) {
         if (var1 instanceof Attribute.TypeCompound) {
            this.result = this.visitTypeCompoundInternal((Attribute.TypeCompound)var1);
         } else {
            this.result = this.visitCompoundInternal(var1);
         }

      }

      public JCTree.JCAnnotation visitCompoundInternal(Attribute.Compound var1) {
         ListBuffer var2 = new ListBuffer();

         for(List var3 = var1.values; var3.nonEmpty(); var3 = var3.tail) {
            Pair var4 = (Pair)var3.head;
            JCTree.JCExpression var5 = this.translate((Attribute)var4.snd);
            var2.append(TreeMaker.this.Assign(TreeMaker.this.Ident((Symbol)var4.fst), var5).setType(var5.type));
         }

         return TreeMaker.this.Annotation(TreeMaker.this.Type(var1.type), var2.toList());
      }

      public JCTree.JCAnnotation visitTypeCompoundInternal(Attribute.TypeCompound var1) {
         ListBuffer var2 = new ListBuffer();

         for(List var3 = var1.values; var3.nonEmpty(); var3 = var3.tail) {
            Pair var4 = (Pair)var3.head;
            JCTree.JCExpression var5 = this.translate((Attribute)var4.snd);
            var2.append(TreeMaker.this.Assign(TreeMaker.this.Ident((Symbol)var4.fst), var5).setType(var5.type));
         }

         return TreeMaker.this.TypeAnnotation(TreeMaker.this.Type(var1.type), var2.toList());
      }

      public void visitArray(Attribute.Array var1) {
         ListBuffer var2 = new ListBuffer();

         for(int var3 = 0; var3 < var1.values.length; ++var3) {
            var2.append(this.translate(var1.values[var3]));
         }

         this.result = TreeMaker.this.NewArray((JCTree.JCExpression)null, List.nil(), var2.toList()).setType(var1.type);
      }

      JCTree.JCExpression translate(Attribute var1) {
         var1.accept(this);
         return this.result;
      }

      JCTree.JCAnnotation translate(Attribute.Compound var1) {
         return this.visitCompoundInternal(var1);
      }

      JCTree.JCAnnotation translate(Attribute.TypeCompound var1) {
         return this.visitTypeCompoundInternal(var1);
      }
   }
}
