package com.sun.tools.javac.tree;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Position;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;

public abstract class JCTree implements Tree, Cloneable, JCDiagnostic.DiagnosticPosition {
   public int pos;
   public Type type;

   public abstract Tag getTag();

   public boolean hasTag(Tag var1) {
      return var1 == this.getTag();
   }

   public String toString() {
      StringWriter var1 = new StringWriter();

      try {
         (new Pretty(var1, false)).printExpr(this);
      } catch (IOException var3) {
         throw new AssertionError(var3);
      }

      return var1.toString();
   }

   public JCTree setPos(int var1) {
      this.pos = var1;
      return this;
   }

   public JCTree setType(Type var1) {
      this.type = var1;
      return this;
   }

   public abstract void accept(Visitor var1);

   public abstract Object accept(TreeVisitor var1, Object var2);

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   public JCDiagnostic.DiagnosticPosition pos() {
      return this;
   }

   public JCTree getTree() {
      return this;
   }

   public int getStartPosition() {
      return TreeInfo.getStartPos(this);
   }

   public int getPreferredPosition() {
      return this.pos;
   }

   public int getEndPosition(EndPosTable var1) {
      return TreeInfo.getEndPos(this, var1);
   }

   public abstract static class Visitor {
      public void visitTopLevel(JCCompilationUnit var1) {
         this.visitTree(var1);
      }

      public void visitImport(JCImport var1) {
         this.visitTree(var1);
      }

      public void visitClassDef(JCClassDecl var1) {
         this.visitTree(var1);
      }

      public void visitMethodDef(JCMethodDecl var1) {
         this.visitTree(var1);
      }

      public void visitVarDef(JCVariableDecl var1) {
         this.visitTree(var1);
      }

      public void visitSkip(JCSkip var1) {
         this.visitTree(var1);
      }

      public void visitBlock(JCBlock var1) {
         this.visitTree(var1);
      }

      public void visitDoLoop(JCDoWhileLoop var1) {
         this.visitTree(var1);
      }

      public void visitWhileLoop(JCWhileLoop var1) {
         this.visitTree(var1);
      }

      public void visitForLoop(JCForLoop var1) {
         this.visitTree(var1);
      }

      public void visitForeachLoop(JCEnhancedForLoop var1) {
         this.visitTree(var1);
      }

      public void visitLabelled(JCLabeledStatement var1) {
         this.visitTree(var1);
      }

      public void visitSwitch(JCSwitch var1) {
         this.visitTree(var1);
      }

      public void visitCase(JCCase var1) {
         this.visitTree(var1);
      }

      public void visitSynchronized(JCSynchronized var1) {
         this.visitTree(var1);
      }

      public void visitTry(JCTry var1) {
         this.visitTree(var1);
      }

      public void visitCatch(JCCatch var1) {
         this.visitTree(var1);
      }

      public void visitConditional(JCConditional var1) {
         this.visitTree(var1);
      }

      public void visitIf(JCIf var1) {
         this.visitTree(var1);
      }

      public void visitExec(JCExpressionStatement var1) {
         this.visitTree(var1);
      }

      public void visitBreak(JCBreak var1) {
         this.visitTree(var1);
      }

      public void visitContinue(JCContinue var1) {
         this.visitTree(var1);
      }

      public void visitReturn(JCReturn var1) {
         this.visitTree(var1);
      }

      public void visitThrow(JCThrow var1) {
         this.visitTree(var1);
      }

      public void visitAssert(JCAssert var1) {
         this.visitTree(var1);
      }

      public void visitApply(JCMethodInvocation var1) {
         this.visitTree(var1);
      }

      public void visitNewClass(JCNewClass var1) {
         this.visitTree(var1);
      }

      public void visitNewArray(JCNewArray var1) {
         this.visitTree(var1);
      }

      public void visitLambda(JCLambda var1) {
         this.visitTree(var1);
      }

      public void visitParens(JCParens var1) {
         this.visitTree(var1);
      }

      public void visitAssign(JCAssign var1) {
         this.visitTree(var1);
      }

      public void visitAssignop(JCAssignOp var1) {
         this.visitTree(var1);
      }

      public void visitUnary(JCUnary var1) {
         this.visitTree(var1);
      }

      public void visitBinary(JCBinary var1) {
         this.visitTree(var1);
      }

      public void visitTypeCast(JCTypeCast var1) {
         this.visitTree(var1);
      }

      public void visitTypeTest(JCInstanceOf var1) {
         this.visitTree(var1);
      }

      public void visitIndexed(JCArrayAccess var1) {
         this.visitTree(var1);
      }

      public void visitSelect(JCFieldAccess var1) {
         this.visitTree(var1);
      }

      public void visitReference(JCMemberReference var1) {
         this.visitTree(var1);
      }

      public void visitIdent(JCIdent var1) {
         this.visitTree(var1);
      }

      public void visitLiteral(JCLiteral var1) {
         this.visitTree(var1);
      }

      public void visitTypeIdent(JCPrimitiveTypeTree var1) {
         this.visitTree(var1);
      }

      public void visitTypeArray(JCArrayTypeTree var1) {
         this.visitTree(var1);
      }

      public void visitTypeApply(JCTypeApply var1) {
         this.visitTree(var1);
      }

      public void visitTypeUnion(JCTypeUnion var1) {
         this.visitTree(var1);
      }

      public void visitTypeIntersection(JCTypeIntersection var1) {
         this.visitTree(var1);
      }

      public void visitTypeParameter(JCTypeParameter var1) {
         this.visitTree(var1);
      }

      public void visitWildcard(JCWildcard var1) {
         this.visitTree(var1);
      }

      public void visitTypeBoundKind(TypeBoundKind var1) {
         this.visitTree(var1);
      }

      public void visitAnnotation(JCAnnotation var1) {
         this.visitTree(var1);
      }

      public void visitModifiers(JCModifiers var1) {
         this.visitTree(var1);
      }

      public void visitAnnotatedType(JCAnnotatedType var1) {
         this.visitTree(var1);
      }

      public void visitErroneous(JCErroneous var1) {
         this.visitTree(var1);
      }

      public void visitLetExpr(LetExpr var1) {
         this.visitTree(var1);
      }

      public void visitTree(JCTree var1) {
         Assert.error();
      }
   }

   public interface Factory {
      JCCompilationUnit TopLevel(List var1, JCExpression var2, List var3);

      JCImport Import(JCTree var1, boolean var2);

      JCClassDecl ClassDef(JCModifiers var1, Name var2, List var3, JCExpression var4, List var5, List var6);

      JCMethodDecl MethodDef(JCModifiers var1, Name var2, JCExpression var3, List var4, JCVariableDecl var5, List var6, List var7, JCBlock var8, JCExpression var9);

      JCVariableDecl VarDef(JCModifiers var1, Name var2, JCExpression var3, JCExpression var4);

      JCSkip Skip();

      JCBlock Block(long var1, List var3);

      JCDoWhileLoop DoLoop(JCStatement var1, JCExpression var2);

      JCWhileLoop WhileLoop(JCExpression var1, JCStatement var2);

      JCForLoop ForLoop(List var1, JCExpression var2, List var3, JCStatement var4);

      JCEnhancedForLoop ForeachLoop(JCVariableDecl var1, JCExpression var2, JCStatement var3);

      JCLabeledStatement Labelled(Name var1, JCStatement var2);

      JCSwitch Switch(JCExpression var1, List var2);

      JCCase Case(JCExpression var1, List var2);

      JCSynchronized Synchronized(JCExpression var1, JCBlock var2);

      JCTry Try(JCBlock var1, List var2, JCBlock var3);

      JCTry Try(List var1, JCBlock var2, List var3, JCBlock var4);

      JCCatch Catch(JCVariableDecl var1, JCBlock var2);

      JCConditional Conditional(JCExpression var1, JCExpression var2, JCExpression var3);

      JCIf If(JCExpression var1, JCStatement var2, JCStatement var3);

      JCExpressionStatement Exec(JCExpression var1);

      JCBreak Break(Name var1);

      JCContinue Continue(Name var1);

      JCReturn Return(JCExpression var1);

      JCThrow Throw(JCExpression var1);

      JCAssert Assert(JCExpression var1, JCExpression var2);

      JCMethodInvocation Apply(List var1, JCExpression var2, List var3);

      JCNewClass NewClass(JCExpression var1, List var2, JCExpression var3, List var4, JCClassDecl var5);

      JCNewArray NewArray(JCExpression var1, List var2, List var3);

      JCParens Parens(JCExpression var1);

      JCAssign Assign(JCExpression var1, JCExpression var2);

      JCAssignOp Assignop(Tag var1, JCTree var2, JCTree var3);

      JCUnary Unary(Tag var1, JCExpression var2);

      JCBinary Binary(Tag var1, JCExpression var2, JCExpression var3);

      JCTypeCast TypeCast(JCTree var1, JCExpression var2);

      JCInstanceOf TypeTest(JCExpression var1, JCTree var2);

      JCArrayAccess Indexed(JCExpression var1, JCExpression var2);

      JCFieldAccess Select(JCExpression var1, Name var2);

      JCIdent Ident(Name var1);

      JCLiteral Literal(TypeTag var1, Object var2);

      JCPrimitiveTypeTree TypeIdent(TypeTag var1);

      JCArrayTypeTree TypeArray(JCExpression var1);

      JCTypeApply TypeApply(JCExpression var1, List var2);

      JCTypeParameter TypeParameter(Name var1, List var2);

      JCWildcard Wildcard(TypeBoundKind var1, JCTree var2);

      TypeBoundKind TypeBoundKind(BoundKind var1);

      JCAnnotation Annotation(JCTree var1, List var2);

      JCModifiers Modifiers(long var1, List var3);

      JCErroneous Erroneous(List var1);

      LetExpr LetExpr(List var1, JCTree var2);
   }

   public static class LetExpr extends JCExpression {
      public List defs;
      public JCTree expr;

      protected LetExpr(List var1, JCTree var2) {
         this.defs = var1;
         this.expr = var2;
      }

      public void accept(Visitor var1) {
         var1.visitLetExpr(this);
      }

      public Tree.Kind getKind() {
         throw new AssertionError("LetExpr is not part of a public API");
      }

      public Object accept(TreeVisitor var1, Object var2) {
         throw new AssertionError("LetExpr is not part of a public API");
      }

      public Tag getTag() {
         return JCTree.Tag.LETEXPR;
      }
   }

   public static class JCErroneous extends JCExpression implements ErroneousTree {
      public List errs;

      protected JCErroneous(List var1) {
         this.errs = var1;
      }

      public void accept(Visitor var1) {
         var1.visitErroneous(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ERRONEOUS;
      }

      public List getErrorTrees() {
         return this.errs;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitErroneous(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.ERRONEOUS;
      }
   }

   public static class JCAnnotatedType extends JCExpression implements AnnotatedTypeTree {
      public List annotations;
      public JCExpression underlyingType;

      protected JCAnnotatedType(List var1, JCExpression var2) {
         Assert.check(var1 != null && var1.nonEmpty());
         this.annotations = var1;
         this.underlyingType = var2;
      }

      public void accept(Visitor var1) {
         var1.visitAnnotatedType(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ANNOTATED_TYPE;
      }

      public List getAnnotations() {
         return this.annotations;
      }

      public JCExpression getUnderlyingType() {
         return this.underlyingType;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitAnnotatedType(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.ANNOTATED_TYPE;
      }
   }

   public static class JCModifiers extends JCTree implements ModifiersTree {
      public long flags;
      public List annotations;

      protected JCModifiers(long var1, List var3) {
         this.flags = var1;
         this.annotations = var3;
      }

      public void accept(Visitor var1) {
         var1.visitModifiers(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.MODIFIERS;
      }

      public Set getFlags() {
         return Flags.asModifierSet(this.flags);
      }

      public List getAnnotations() {
         return this.annotations;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitModifiers(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.MODIFIERS;
      }
   }

   public static class JCAnnotation extends JCExpression implements AnnotationTree {
      private Tag tag;
      public JCTree annotationType;
      public List args;
      public Attribute.Compound attribute;

      protected JCAnnotation(Tag var1, JCTree var2, List var3) {
         this.tag = var1;
         this.annotationType = var2;
         this.args = var3;
      }

      public void accept(Visitor var1) {
         var1.visitAnnotation(this);
      }

      public Tree.Kind getKind() {
         return TreeInfo.tagToKind(this.getTag());
      }

      public JCTree getAnnotationType() {
         return this.annotationType;
      }

      public List getArguments() {
         return this.args;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitAnnotation(this, var2);
      }

      public Tag getTag() {
         return this.tag;
      }
   }

   public static class TypeBoundKind extends JCTree {
      public BoundKind kind;

      protected TypeBoundKind(BoundKind var1) {
         this.kind = var1;
      }

      public void accept(Visitor var1) {
         var1.visitTypeBoundKind(this);
      }

      public Tree.Kind getKind() {
         throw new AssertionError("TypeBoundKind is not part of a public API");
      }

      public Object accept(TreeVisitor var1, Object var2) {
         throw new AssertionError("TypeBoundKind is not part of a public API");
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEBOUNDKIND;
      }
   }

   public static class JCWildcard extends JCExpression implements WildcardTree {
      public TypeBoundKind kind;
      public JCTree inner;

      protected JCWildcard(TypeBoundKind var1, JCTree var2) {
         var1.getClass();
         this.kind = var1;
         this.inner = var2;
      }

      public void accept(Visitor var1) {
         var1.visitWildcard(this);
      }

      public Tree.Kind getKind() {
         switch (this.kind.kind) {
            case UNBOUND:
               return Tree.Kind.UNBOUNDED_WILDCARD;
            case EXTENDS:
               return Tree.Kind.EXTENDS_WILDCARD;
            case SUPER:
               return Tree.Kind.SUPER_WILDCARD;
            default:
               throw new AssertionError("Unknown wildcard bound " + this.kind);
         }
      }

      public JCTree getBound() {
         return this.inner;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitWildcard(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.WILDCARD;
      }
   }

   public static class JCTypeParameter extends JCTree implements TypeParameterTree {
      public Name name;
      public List bounds;
      public List annotations;

      protected JCTypeParameter(Name var1, List var2, List var3) {
         this.name = var1;
         this.bounds = var2;
         this.annotations = var3;
      }

      public void accept(Visitor var1) {
         var1.visitTypeParameter(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.TYPE_PARAMETER;
      }

      public Name getName() {
         return this.name;
      }

      public List getBounds() {
         return this.bounds;
      }

      public List getAnnotations() {
         return this.annotations;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitTypeParameter(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEPARAMETER;
      }
   }

   public static class JCTypeIntersection extends JCExpression implements IntersectionTypeTree {
      public List bounds;

      protected JCTypeIntersection(List var1) {
         this.bounds = var1;
      }

      public void accept(Visitor var1) {
         var1.visitTypeIntersection(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.INTERSECTION_TYPE;
      }

      public List getBounds() {
         return this.bounds;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitIntersectionType(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEINTERSECTION;
      }
   }

   public static class JCTypeUnion extends JCExpression implements UnionTypeTree {
      public List alternatives;

      protected JCTypeUnion(List var1) {
         this.alternatives = var1;
      }

      public void accept(Visitor var1) {
         var1.visitTypeUnion(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.UNION_TYPE;
      }

      public List getTypeAlternatives() {
         return this.alternatives;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitUnionType(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEUNION;
      }
   }

   public static class JCTypeApply extends JCExpression implements ParameterizedTypeTree {
      public JCExpression clazz;
      public List arguments;

      protected JCTypeApply(JCExpression var1, List var2) {
         this.clazz = var1;
         this.arguments = var2;
      }

      public void accept(Visitor var1) {
         var1.visitTypeApply(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.PARAMETERIZED_TYPE;
      }

      public JCTree getType() {
         return this.clazz;
      }

      public List getTypeArguments() {
         return this.arguments;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitParameterizedType(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEAPPLY;
      }
   }

   public static class JCArrayTypeTree extends JCExpression implements ArrayTypeTree {
      public JCExpression elemtype;

      protected JCArrayTypeTree(JCExpression var1) {
         this.elemtype = var1;
      }

      public void accept(Visitor var1) {
         var1.visitTypeArray(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ARRAY_TYPE;
      }

      public JCTree getType() {
         return this.elemtype;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitArrayType(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEARRAY;
      }
   }

   public static class JCPrimitiveTypeTree extends JCExpression implements PrimitiveTypeTree {
      public TypeTag typetag;

      protected JCPrimitiveTypeTree(TypeTag var1) {
         this.typetag = var1;
      }

      public void accept(Visitor var1) {
         var1.visitTypeIdent(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.PRIMITIVE_TYPE;
      }

      public TypeKind getPrimitiveTypeKind() {
         return this.typetag.getPrimitiveTypeKind();
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitPrimitiveType(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPEIDENT;
      }
   }

   public static class JCLiteral extends JCExpression implements LiteralTree {
      public TypeTag typetag;
      public Object value;

      protected JCLiteral(TypeTag var1, Object var2) {
         this.typetag = var1;
         this.value = var2;
      }

      public void accept(Visitor var1) {
         var1.visitLiteral(this);
      }

      public Tree.Kind getKind() {
         return this.typetag.getKindLiteral();
      }

      public Object getValue() {
         switch (this.typetag) {
            case BOOLEAN:
               int var1 = (Integer)this.value;
               return var1 != 0;
            case CHAR:
               int var2 = (Integer)this.value;
               char var3 = (char)var2;
               if (var3 != var2) {
                  throw new AssertionError("bad value for char literal");
               }

               return var3;
            default:
               return this.value;
         }
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitLiteral(this, var2);
      }

      public JCLiteral setType(Type var1) {
         super.setType(var1);
         return this;
      }

      public Tag getTag() {
         return JCTree.Tag.LITERAL;
      }
   }

   public static class JCIdent extends JCExpression implements IdentifierTree {
      public Name name;
      public Symbol sym;

      protected JCIdent(Name var1, Symbol var2) {
         this.name = var1;
         this.sym = var2;
      }

      public void accept(Visitor var1) {
         var1.visitIdent(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.IDENTIFIER;
      }

      public Name getName() {
         return this.name;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitIdentifier(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.IDENT;
      }
   }

   public static class JCMemberReference extends JCFunctionalExpression implements MemberReferenceTree {
      public MemberReferenceTree.ReferenceMode mode;
      public ReferenceKind kind;
      public Name name;
      public JCExpression expr;
      public List typeargs;
      public Symbol sym;
      public Type varargsElement;
      public JCPolyExpression.PolyKind refPolyKind;
      public boolean ownerAccessible;
      public OverloadKind overloadKind;

      protected JCMemberReference(MemberReferenceTree.ReferenceMode var1, Name var2, JCExpression var3, List var4) {
         this.mode = var1;
         this.name = var2;
         this.expr = var3;
         this.typeargs = var4;
      }

      public void accept(Visitor var1) {
         var1.visitReference(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.MEMBER_REFERENCE;
      }

      public MemberReferenceTree.ReferenceMode getMode() {
         return this.mode;
      }

      public JCExpression getQualifierExpression() {
         return this.expr;
      }

      public Name getName() {
         return this.name;
      }

      public List getTypeArguments() {
         return this.typeargs;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitMemberReference(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.REFERENCE;
      }

      public boolean hasKind(ReferenceKind var1) {
         return this.kind == var1;
      }

      public static enum ReferenceKind {
         SUPER(MemberReferenceTree.ReferenceMode.INVOKE, false),
         UNBOUND(MemberReferenceTree.ReferenceMode.INVOKE, true),
         STATIC(MemberReferenceTree.ReferenceMode.INVOKE, false),
         BOUND(MemberReferenceTree.ReferenceMode.INVOKE, false),
         IMPLICIT_INNER(MemberReferenceTree.ReferenceMode.NEW, false),
         TOPLEVEL(MemberReferenceTree.ReferenceMode.NEW, false),
         ARRAY_CTOR(MemberReferenceTree.ReferenceMode.NEW, false);

         final MemberReferenceTree.ReferenceMode mode;
         final boolean unbound;

         private ReferenceKind(MemberReferenceTree.ReferenceMode var3, boolean var4) {
            this.mode = var3;
            this.unbound = var4;
         }

         public boolean isUnbound() {
            return this.unbound;
         }
      }

      public static enum OverloadKind {
         OVERLOADED,
         UNOVERLOADED;
      }
   }

   public static class JCFieldAccess extends JCExpression implements MemberSelectTree {
      public JCExpression selected;
      public Name name;
      public Symbol sym;

      protected JCFieldAccess(JCExpression var1, Name var2, Symbol var3) {
         this.selected = var1;
         this.name = var2;
         this.sym = var3;
      }

      public void accept(Visitor var1) {
         var1.visitSelect(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.MEMBER_SELECT;
      }

      public JCExpression getExpression() {
         return this.selected;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitMemberSelect(this, var2);
      }

      public Name getIdentifier() {
         return this.name;
      }

      public Tag getTag() {
         return JCTree.Tag.SELECT;
      }
   }

   public static class JCArrayAccess extends JCExpression implements ArrayAccessTree {
      public JCExpression indexed;
      public JCExpression index;

      protected JCArrayAccess(JCExpression var1, JCExpression var2) {
         this.indexed = var1;
         this.index = var2;
      }

      public void accept(Visitor var1) {
         var1.visitIndexed(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ARRAY_ACCESS;
      }

      public JCExpression getExpression() {
         return this.indexed;
      }

      public JCExpression getIndex() {
         return this.index;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitArrayAccess(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.INDEXED;
      }
   }

   public static class JCInstanceOf extends JCExpression implements InstanceOfTree {
      public JCExpression expr;
      public JCTree clazz;

      protected JCInstanceOf(JCExpression var1, JCTree var2) {
         this.expr = var1;
         this.clazz = var2;
      }

      public void accept(Visitor var1) {
         var1.visitTypeTest(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.INSTANCE_OF;
      }

      public JCTree getType() {
         return this.clazz;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitInstanceOf(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPETEST;
      }
   }

   public static class JCTypeCast extends JCExpression implements TypeCastTree {
      public JCTree clazz;
      public JCExpression expr;

      protected JCTypeCast(JCTree var1, JCExpression var2) {
         this.clazz = var1;
         this.expr = var2;
      }

      public void accept(Visitor var1) {
         var1.visitTypeCast(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.TYPE_CAST;
      }

      public JCTree getType() {
         return this.clazz;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitTypeCast(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TYPECAST;
      }
   }

   public static class JCBinary extends JCExpression implements BinaryTree {
      private Tag opcode;
      public JCExpression lhs;
      public JCExpression rhs;
      public Symbol operator;

      protected JCBinary(Tag var1, JCExpression var2, JCExpression var3, Symbol var4) {
         this.opcode = var1;
         this.lhs = var2;
         this.rhs = var3;
         this.operator = var4;
      }

      public void accept(Visitor var1) {
         var1.visitBinary(this);
      }

      public Tree.Kind getKind() {
         return TreeInfo.tagToKind(this.getTag());
      }

      public JCExpression getLeftOperand() {
         return this.lhs;
      }

      public JCExpression getRightOperand() {
         return this.rhs;
      }

      public Symbol getOperator() {
         return this.operator;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitBinary(this, var2);
      }

      public Tag getTag() {
         return this.opcode;
      }
   }

   public static class JCUnary extends JCExpression implements UnaryTree {
      private Tag opcode;
      public JCExpression arg;
      public Symbol operator;

      protected JCUnary(Tag var1, JCExpression var2) {
         this.opcode = var1;
         this.arg = var2;
      }

      public void accept(Visitor var1) {
         var1.visitUnary(this);
      }

      public Tree.Kind getKind() {
         return TreeInfo.tagToKind(this.getTag());
      }

      public JCExpression getExpression() {
         return this.arg;
      }

      public Symbol getOperator() {
         return this.operator;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitUnary(this, var2);
      }

      public Tag getTag() {
         return this.opcode;
      }

      public void setTag(Tag var1) {
         this.opcode = var1;
      }
   }

   public static class JCAssignOp extends JCExpression implements CompoundAssignmentTree {
      private Tag opcode;
      public JCExpression lhs;
      public JCExpression rhs;
      public Symbol operator;

      protected JCAssignOp(Tag var1, JCTree var2, JCTree var3, Symbol var4) {
         this.opcode = var1;
         this.lhs = (JCExpression)var2;
         this.rhs = (JCExpression)var3;
         this.operator = var4;
      }

      public void accept(Visitor var1) {
         var1.visitAssignop(this);
      }

      public Tree.Kind getKind() {
         return TreeInfo.tagToKind(this.getTag());
      }

      public JCExpression getVariable() {
         return this.lhs;
      }

      public JCExpression getExpression() {
         return this.rhs;
      }

      public Symbol getOperator() {
         return this.operator;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitCompoundAssignment(this, var2);
      }

      public Tag getTag() {
         return this.opcode;
      }
   }

   public static class JCAssign extends JCExpression implements AssignmentTree {
      public JCExpression lhs;
      public JCExpression rhs;

      protected JCAssign(JCExpression var1, JCExpression var2) {
         this.lhs = var1;
         this.rhs = var2;
      }

      public void accept(Visitor var1) {
         var1.visitAssign(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ASSIGNMENT;
      }

      public JCExpression getVariable() {
         return this.lhs;
      }

      public JCExpression getExpression() {
         return this.rhs;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitAssignment(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.ASSIGN;
      }
   }

   public static class JCParens extends JCExpression implements ParenthesizedTree {
      public JCExpression expr;

      protected JCParens(JCExpression var1) {
         this.expr = var1;
      }

      public void accept(Visitor var1) {
         var1.visitParens(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.PARENTHESIZED;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitParenthesized(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.PARENS;
      }
   }

   public static class JCLambda extends JCFunctionalExpression implements LambdaExpressionTree {
      public List params;
      public JCTree body;
      public boolean canCompleteNormally = true;
      public ParameterKind paramKind;

      public JCLambda(List var1, JCTree var2) {
         this.params = var1;
         this.body = var2;
         if (!var1.isEmpty() && ((JCVariableDecl)var1.head).vartype == null) {
            this.paramKind = JCTree.JCLambda.ParameterKind.IMPLICIT;
         } else {
            this.paramKind = JCTree.JCLambda.ParameterKind.EXPLICIT;
         }

      }

      public Tag getTag() {
         return JCTree.Tag.LAMBDA;
      }

      public void accept(Visitor var1) {
         var1.visitLambda(this);
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitLambdaExpression(this, var2);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.LAMBDA_EXPRESSION;
      }

      public JCTree getBody() {
         return this.body;
      }

      public java.util.List getParameters() {
         return this.params;
      }

      public JCLambda setType(Type var1) {
         super.setType(var1);
         return this;
      }

      public LambdaExpressionTree.BodyKind getBodyKind() {
         return this.body.hasTag(JCTree.Tag.BLOCK) ? LambdaExpressionTree.BodyKind.STATEMENT : LambdaExpressionTree.BodyKind.EXPRESSION;
      }

      public static enum ParameterKind {
         IMPLICIT,
         EXPLICIT;
      }
   }

   public static class JCNewArray extends JCExpression implements NewArrayTree {
      public JCExpression elemtype;
      public List dims;
      public List annotations;
      public List dimAnnotations;
      public List elems;

      protected JCNewArray(JCExpression var1, List var2, List var3) {
         this.elemtype = var1;
         this.dims = var2;
         this.annotations = List.nil();
         this.dimAnnotations = List.nil();
         this.elems = var3;
      }

      public void accept(Visitor var1) {
         var1.visitNewArray(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.NEW_ARRAY;
      }

      public JCExpression getType() {
         return this.elemtype;
      }

      public List getDimensions() {
         return this.dims;
      }

      public List getInitializers() {
         return this.elems;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitNewArray(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.NEWARRAY;
      }

      public List getAnnotations() {
         return this.annotations;
      }

      public List getDimAnnotations() {
         return this.dimAnnotations;
      }
   }

   public static class JCNewClass extends JCPolyExpression implements NewClassTree {
      public JCExpression encl;
      public List typeargs;
      public JCExpression clazz;
      public List args;
      public JCClassDecl def;
      public Symbol constructor;
      public Type varargsElement;
      public Type constructorType;

      protected JCNewClass(JCExpression var1, List var2, JCExpression var3, List var4, JCClassDecl var5) {
         this.encl = var1;
         this.typeargs = var2 == null ? List.nil() : var2;
         this.clazz = var3;
         this.args = var4;
         this.def = var5;
      }

      public void accept(Visitor var1) {
         var1.visitNewClass(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.NEW_CLASS;
      }

      public JCExpression getEnclosingExpression() {
         return this.encl;
      }

      public List getTypeArguments() {
         return this.typeargs;
      }

      public JCExpression getIdentifier() {
         return this.clazz;
      }

      public List getArguments() {
         return this.args;
      }

      public JCClassDecl getClassBody() {
         return this.def;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitNewClass(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.NEWCLASS;
      }
   }

   public static class JCMethodInvocation extends JCPolyExpression implements MethodInvocationTree {
      public List typeargs;
      public JCExpression meth;
      public List args;
      public Type varargsElement;

      protected JCMethodInvocation(List var1, JCExpression var2, List var3) {
         this.typeargs = var1 == null ? List.nil() : var1;
         this.meth = var2;
         this.args = var3;
      }

      public void accept(Visitor var1) {
         var1.visitApply(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.METHOD_INVOCATION;
      }

      public List getTypeArguments() {
         return this.typeargs;
      }

      public JCExpression getMethodSelect() {
         return this.meth;
      }

      public List getArguments() {
         return this.args;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitMethodInvocation(this, var2);
      }

      public JCMethodInvocation setType(Type var1) {
         super.setType(var1);
         return this;
      }

      public Tag getTag() {
         return JCTree.Tag.APPLY;
      }
   }

   public static class JCAssert extends JCStatement implements AssertTree {
      public JCExpression cond;
      public JCExpression detail;

      protected JCAssert(JCExpression var1, JCExpression var2) {
         this.cond = var1;
         this.detail = var2;
      }

      public void accept(Visitor var1) {
         var1.visitAssert(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ASSERT;
      }

      public JCExpression getCondition() {
         return this.cond;
      }

      public JCExpression getDetail() {
         return this.detail;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitAssert(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.ASSERT;
      }
   }

   public static class JCThrow extends JCStatement implements ThrowTree {
      public JCExpression expr;

      protected JCThrow(JCExpression var1) {
         this.expr = var1;
      }

      public void accept(Visitor var1) {
         var1.visitThrow(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.THROW;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitThrow(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.THROW;
      }
   }

   public static class JCReturn extends JCStatement implements ReturnTree {
      public JCExpression expr;

      protected JCReturn(JCExpression var1) {
         this.expr = var1;
      }

      public void accept(Visitor var1) {
         var1.visitReturn(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.RETURN;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitReturn(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.RETURN;
      }
   }

   public static class JCContinue extends JCStatement implements ContinueTree {
      public Name label;
      public JCTree target;

      protected JCContinue(Name var1, JCTree var2) {
         this.label = var1;
         this.target = var2;
      }

      public void accept(Visitor var1) {
         var1.visitContinue(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.CONTINUE;
      }

      public Name getLabel() {
         return this.label;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitContinue(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.CONTINUE;
      }
   }

   public static class JCBreak extends JCStatement implements BreakTree {
      public Name label;
      public JCTree target;

      protected JCBreak(Name var1, JCTree var2) {
         this.label = var1;
         this.target = var2;
      }

      public void accept(Visitor var1) {
         var1.visitBreak(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.BREAK;
      }

      public Name getLabel() {
         return this.label;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitBreak(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.BREAK;
      }
   }

   public static class JCExpressionStatement extends JCStatement implements ExpressionStatementTree {
      public JCExpression expr;

      protected JCExpressionStatement(JCExpression var1) {
         this.expr = var1;
      }

      public void accept(Visitor var1) {
         var1.visitExec(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.EXPRESSION_STATEMENT;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitExpressionStatement(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.EXEC;
      }

      public String toString() {
         StringWriter var1 = new StringWriter();

         try {
            (new Pretty(var1, false)).printStat(this);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }

         return var1.toString();
      }
   }

   public static class JCIf extends JCStatement implements IfTree {
      public JCExpression cond;
      public JCStatement thenpart;
      public JCStatement elsepart;

      protected JCIf(JCExpression var1, JCStatement var2, JCStatement var3) {
         this.cond = var1;
         this.thenpart = var2;
         this.elsepart = var3;
      }

      public void accept(Visitor var1) {
         var1.visitIf(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.IF;
      }

      public JCExpression getCondition() {
         return this.cond;
      }

      public JCStatement getThenStatement() {
         return this.thenpart;
      }

      public JCStatement getElseStatement() {
         return this.elsepart;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitIf(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.IF;
      }
   }

   public static class JCConditional extends JCPolyExpression implements ConditionalExpressionTree {
      public JCExpression cond;
      public JCExpression truepart;
      public JCExpression falsepart;

      protected JCConditional(JCExpression var1, JCExpression var2, JCExpression var3) {
         this.cond = var1;
         this.truepart = var2;
         this.falsepart = var3;
      }

      public void accept(Visitor var1) {
         var1.visitConditional(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.CONDITIONAL_EXPRESSION;
      }

      public JCExpression getCondition() {
         return this.cond;
      }

      public JCExpression getTrueExpression() {
         return this.truepart;
      }

      public JCExpression getFalseExpression() {
         return this.falsepart;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitConditionalExpression(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.CONDEXPR;
      }
   }

   public static class JCCatch extends JCTree implements CatchTree {
      public JCVariableDecl param;
      public JCBlock body;

      protected JCCatch(JCVariableDecl var1, JCBlock var2) {
         this.param = var1;
         this.body = var2;
      }

      public void accept(Visitor var1) {
         var1.visitCatch(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.CATCH;
      }

      public JCVariableDecl getParameter() {
         return this.param;
      }

      public JCBlock getBlock() {
         return this.body;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitCatch(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.CATCH;
      }
   }

   public static class JCTry extends JCStatement implements TryTree {
      public JCBlock body;
      public List catchers;
      public JCBlock finalizer;
      public List resources;
      public boolean finallyCanCompleteNormally;

      protected JCTry(List var1, JCBlock var2, List var3, JCBlock var4) {
         this.body = var2;
         this.catchers = var3;
         this.finalizer = var4;
         this.resources = var1;
      }

      public void accept(Visitor var1) {
         var1.visitTry(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.TRY;
      }

      public JCBlock getBlock() {
         return this.body;
      }

      public List getCatches() {
         return this.catchers;
      }

      public JCBlock getFinallyBlock() {
         return this.finalizer;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitTry(this, var2);
      }

      public List getResources() {
         return this.resources;
      }

      public Tag getTag() {
         return JCTree.Tag.TRY;
      }
   }

   public static class JCSynchronized extends JCStatement implements SynchronizedTree {
      public JCExpression lock;
      public JCBlock body;

      protected JCSynchronized(JCExpression var1, JCBlock var2) {
         this.lock = var1;
         this.body = var2;
      }

      public void accept(Visitor var1) {
         var1.visitSynchronized(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.SYNCHRONIZED;
      }

      public JCExpression getExpression() {
         return this.lock;
      }

      public JCBlock getBlock() {
         return this.body;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitSynchronized(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.SYNCHRONIZED;
      }
   }

   public static class JCCase extends JCStatement implements CaseTree {
      public JCExpression pat;
      public List stats;

      protected JCCase(JCExpression var1, List var2) {
         this.pat = var1;
         this.stats = var2;
      }

      public void accept(Visitor var1) {
         var1.visitCase(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.CASE;
      }

      public JCExpression getExpression() {
         return this.pat;
      }

      public List getStatements() {
         return this.stats;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitCase(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.CASE;
      }
   }

   public static class JCSwitch extends JCStatement implements SwitchTree {
      public JCExpression selector;
      public List cases;

      protected JCSwitch(JCExpression var1, List var2) {
         this.selector = var1;
         this.cases = var2;
      }

      public void accept(Visitor var1) {
         var1.visitSwitch(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.SWITCH;
      }

      public JCExpression getExpression() {
         return this.selector;
      }

      public List getCases() {
         return this.cases;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitSwitch(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.SWITCH;
      }
   }

   public static class JCLabeledStatement extends JCStatement implements LabeledStatementTree {
      public Name label;
      public JCStatement body;

      protected JCLabeledStatement(Name var1, JCStatement var2) {
         this.label = var1;
         this.body = var2;
      }

      public void accept(Visitor var1) {
         var1.visitLabelled(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.LABELED_STATEMENT;
      }

      public Name getLabel() {
         return this.label;
      }

      public JCStatement getStatement() {
         return this.body;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitLabeledStatement(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.LABELLED;
      }
   }

   public static class JCEnhancedForLoop extends JCStatement implements EnhancedForLoopTree {
      public JCVariableDecl var;
      public JCExpression expr;
      public JCStatement body;

      protected JCEnhancedForLoop(JCVariableDecl var1, JCExpression var2, JCStatement var3) {
         this.var = var1;
         this.expr = var2;
         this.body = var3;
      }

      public void accept(Visitor var1) {
         var1.visitForeachLoop(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.ENHANCED_FOR_LOOP;
      }

      public JCVariableDecl getVariable() {
         return this.var;
      }

      public JCExpression getExpression() {
         return this.expr;
      }

      public JCStatement getStatement() {
         return this.body;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitEnhancedForLoop(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.FOREACHLOOP;
      }
   }

   public static class JCForLoop extends JCStatement implements ForLoopTree {
      public List init;
      public JCExpression cond;
      public List step;
      public JCStatement body;

      protected JCForLoop(List var1, JCExpression var2, List var3, JCStatement var4) {
         this.init = var1;
         this.cond = var2;
         this.step = var3;
         this.body = var4;
      }

      public void accept(Visitor var1) {
         var1.visitForLoop(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.FOR_LOOP;
      }

      public JCExpression getCondition() {
         return this.cond;
      }

      public JCStatement getStatement() {
         return this.body;
      }

      public List getInitializer() {
         return this.init;
      }

      public List getUpdate() {
         return this.step;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitForLoop(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.FORLOOP;
      }
   }

   public static class JCWhileLoop extends JCStatement implements WhileLoopTree {
      public JCExpression cond;
      public JCStatement body;

      protected JCWhileLoop(JCExpression var1, JCStatement var2) {
         this.cond = var1;
         this.body = var2;
      }

      public void accept(Visitor var1) {
         var1.visitWhileLoop(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.WHILE_LOOP;
      }

      public JCExpression getCondition() {
         return this.cond;
      }

      public JCStatement getStatement() {
         return this.body;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitWhileLoop(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.WHILELOOP;
      }
   }

   public static class JCDoWhileLoop extends JCStatement implements DoWhileLoopTree {
      public JCStatement body;
      public JCExpression cond;

      protected JCDoWhileLoop(JCStatement var1, JCExpression var2) {
         this.body = var1;
         this.cond = var2;
      }

      public void accept(Visitor var1) {
         var1.visitDoLoop(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.DO_WHILE_LOOP;
      }

      public JCExpression getCondition() {
         return this.cond;
      }

      public JCStatement getStatement() {
         return this.body;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitDoWhileLoop(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.DOLOOP;
      }
   }

   public static class JCBlock extends JCStatement implements BlockTree {
      public long flags;
      public List stats;
      public int endpos = -1;

      protected JCBlock(long var1, List var3) {
         this.stats = var3;
         this.flags = var1;
      }

      public void accept(Visitor var1) {
         var1.visitBlock(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.BLOCK;
      }

      public List getStatements() {
         return this.stats;
      }

      public boolean isStatic() {
         return (this.flags & 8L) != 0L;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitBlock(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.BLOCK;
      }
   }

   public static class JCSkip extends JCStatement implements EmptyStatementTree {
      protected JCSkip() {
      }

      public void accept(Visitor var1) {
         var1.visitSkip(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.EMPTY_STATEMENT;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitEmptyStatement(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.SKIP;
      }
   }

   public static class JCVariableDecl extends JCStatement implements VariableTree {
      public JCModifiers mods;
      public Name name;
      public JCExpression nameexpr;
      public JCExpression vartype;
      public JCExpression init;
      public Symbol.VarSymbol sym;

      protected JCVariableDecl(JCModifiers var1, Name var2, JCExpression var3, JCExpression var4, Symbol.VarSymbol var5) {
         this.mods = var1;
         this.name = var2;
         this.vartype = var3;
         this.init = var4;
         this.sym = var5;
      }

      protected JCVariableDecl(JCModifiers var1, JCExpression var2, JCExpression var3) {
         this(var1, (Name)null, var3, (JCExpression)null, (Symbol.VarSymbol)null);
         this.nameexpr = var2;
         if (var2.hasTag(JCTree.Tag.IDENT)) {
            this.name = ((JCIdent)var2).name;
         } else {
            this.name = ((JCFieldAccess)var2).name;
         }

      }

      public void accept(Visitor var1) {
         var1.visitVarDef(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.VARIABLE;
      }

      public JCModifiers getModifiers() {
         return this.mods;
      }

      public Name getName() {
         return this.name;
      }

      public JCExpression getNameExpression() {
         return this.nameexpr;
      }

      public JCTree getType() {
         return this.vartype;
      }

      public JCExpression getInitializer() {
         return this.init;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitVariable(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.VARDEF;
      }
   }

   public static class JCMethodDecl extends JCTree implements MethodTree {
      public JCModifiers mods;
      public Name name;
      public JCExpression restype;
      public List typarams;
      public JCVariableDecl recvparam;
      public List params;
      public List thrown;
      public JCBlock body;
      public JCExpression defaultValue;
      public Symbol.MethodSymbol sym;

      protected JCMethodDecl(JCModifiers var1, Name var2, JCExpression var3, List var4, JCVariableDecl var5, List var6, List var7, JCBlock var8, JCExpression var9, Symbol.MethodSymbol var10) {
         this.mods = var1;
         this.name = var2;
         this.restype = var3;
         this.typarams = var4;
         this.params = var6;
         this.recvparam = var5;
         this.thrown = var7;
         this.body = var8;
         this.defaultValue = var9;
         this.sym = var10;
      }

      public void accept(Visitor var1) {
         var1.visitMethodDef(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.METHOD;
      }

      public JCModifiers getModifiers() {
         return this.mods;
      }

      public Name getName() {
         return this.name;
      }

      public JCTree getReturnType() {
         return this.restype;
      }

      public List getTypeParameters() {
         return this.typarams;
      }

      public List getParameters() {
         return this.params;
      }

      public JCVariableDecl getReceiverParameter() {
         return this.recvparam;
      }

      public List getThrows() {
         return this.thrown;
      }

      public JCBlock getBody() {
         return this.body;
      }

      public JCTree getDefaultValue() {
         return this.defaultValue;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitMethod(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.METHODDEF;
      }
   }

   public static class JCClassDecl extends JCStatement implements ClassTree {
      public JCModifiers mods;
      public Name name;
      public List typarams;
      public JCExpression extending;
      public List implementing;
      public List defs;
      public Symbol.ClassSymbol sym;

      protected JCClassDecl(JCModifiers var1, Name var2, List var3, JCExpression var4, List var5, List var6, Symbol.ClassSymbol var7) {
         this.mods = var1;
         this.name = var2;
         this.typarams = var3;
         this.extending = var4;
         this.implementing = var5;
         this.defs = var6;
         this.sym = var7;
      }

      public void accept(Visitor var1) {
         var1.visitClassDef(this);
      }

      public Tree.Kind getKind() {
         if ((this.mods.flags & 8192L) != 0L) {
            return Tree.Kind.ANNOTATION_TYPE;
         } else if ((this.mods.flags & 512L) != 0L) {
            return Tree.Kind.INTERFACE;
         } else {
            return (this.mods.flags & 16384L) != 0L ? Tree.Kind.ENUM : Tree.Kind.CLASS;
         }
      }

      public JCModifiers getModifiers() {
         return this.mods;
      }

      public Name getSimpleName() {
         return this.name;
      }

      public List getTypeParameters() {
         return this.typarams;
      }

      public JCExpression getExtendsClause() {
         return this.extending;
      }

      public List getImplementsClause() {
         return this.implementing;
      }

      public List getMembers() {
         return this.defs;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitClass(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.CLASSDEF;
      }
   }

   public abstract static class JCFunctionalExpression extends JCPolyExpression {
      public List targets;

      public JCFunctionalExpression() {
         this.polyKind = JCTree.JCPolyExpression.PolyKind.POLY;
      }

      public Type getDescriptorType(Types var1) {
         return this.targets.nonEmpty() ? var1.findDescriptorType((Type)this.targets.head) : var1.createErrorType((Type)null);
      }
   }

   public abstract static class JCPolyExpression extends JCExpression {
      public PolyKind polyKind;

      public boolean isPoly() {
         return this.polyKind == JCTree.JCPolyExpression.PolyKind.POLY;
      }

      public boolean isStandalone() {
         return this.polyKind == JCTree.JCPolyExpression.PolyKind.STANDALONE;
      }

      public static enum PolyKind {
         STANDALONE,
         POLY;
      }
   }

   public abstract static class JCExpression extends JCTree implements ExpressionTree {
      public JCExpression setType(Type var1) {
         super.setType(var1);
         return this;
      }

      public JCExpression setPos(int var1) {
         super.setPos(var1);
         return this;
      }

      public boolean isPoly() {
         return false;
      }

      public boolean isStandalone() {
         return true;
      }
   }

   public abstract static class JCStatement extends JCTree implements StatementTree {
      public JCStatement setType(Type var1) {
         super.setType(var1);
         return this;
      }

      public JCStatement setPos(int var1) {
         super.setPos(var1);
         return this;
      }
   }

   public static class JCImport extends JCTree implements ImportTree {
      public boolean staticImport;
      public JCTree qualid;

      protected JCImport(JCTree var1, boolean var2) {
         this.qualid = var1;
         this.staticImport = var2;
      }

      public void accept(Visitor var1) {
         var1.visitImport(this);
      }

      public boolean isStatic() {
         return this.staticImport;
      }

      public JCTree getQualifiedIdentifier() {
         return this.qualid;
      }

      public Tree.Kind getKind() {
         return Tree.Kind.IMPORT;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitImport(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.IMPORT;
      }
   }

   public static class JCCompilationUnit extends JCTree implements CompilationUnitTree {
      public List packageAnnotations;
      public JCExpression pid;
      public List defs;
      public JavaFileObject sourcefile;
      public Symbol.PackageSymbol packge;
      public Scope.ImportScope namedImportScope;
      public Scope.StarImportScope starImportScope;
      public Position.LineMap lineMap = null;
      public DocCommentTable docComments = null;
      public EndPosTable endPositions = null;

      protected JCCompilationUnit(List var1, JCExpression var2, List var3, JavaFileObject var4, Symbol.PackageSymbol var5, Scope.ImportScope var6, Scope.StarImportScope var7) {
         this.packageAnnotations = var1;
         this.pid = var2;
         this.defs = var3;
         this.sourcefile = var4;
         this.packge = var5;
         this.namedImportScope = var6;
         this.starImportScope = var7;
      }

      public void accept(Visitor var1) {
         var1.visitTopLevel(this);
      }

      public Tree.Kind getKind() {
         return Tree.Kind.COMPILATION_UNIT;
      }

      public List getPackageAnnotations() {
         return this.packageAnnotations;
      }

      public List getImports() {
         ListBuffer var1 = new ListBuffer();
         Iterator var2 = this.defs.iterator();

         while(var2.hasNext()) {
            JCTree var3 = (JCTree)var2.next();
            if (var3.hasTag(JCTree.Tag.IMPORT)) {
               var1.append((JCImport)var3);
            } else if (!var3.hasTag(JCTree.Tag.SKIP)) {
               break;
            }
         }

         return var1.toList();
      }

      public JCExpression getPackageName() {
         return this.pid;
      }

      public JavaFileObject getSourceFile() {
         return this.sourcefile;
      }

      public Position.LineMap getLineMap() {
         return this.lineMap;
      }

      public List getTypeDecls() {
         List var1;
         for(var1 = this.defs; !var1.isEmpty() && ((JCTree)var1.head).hasTag(JCTree.Tag.IMPORT); var1 = var1.tail) {
         }

         return var1;
      }

      public Object accept(TreeVisitor var1, Object var2) {
         return var1.visitCompilationUnit(this, var2);
      }

      public Tag getTag() {
         return JCTree.Tag.TOPLEVEL;
      }
   }

   public static enum Tag {
      NO_TAG,
      TOPLEVEL,
      IMPORT,
      CLASSDEF,
      METHODDEF,
      VARDEF,
      SKIP,
      BLOCK,
      DOLOOP,
      WHILELOOP,
      FORLOOP,
      FOREACHLOOP,
      LABELLED,
      SWITCH,
      CASE,
      SYNCHRONIZED,
      TRY,
      CATCH,
      CONDEXPR,
      IF,
      EXEC,
      BREAK,
      CONTINUE,
      RETURN,
      THROW,
      ASSERT,
      APPLY,
      NEWCLASS,
      NEWARRAY,
      LAMBDA,
      PARENS,
      ASSIGN,
      TYPECAST,
      TYPETEST,
      INDEXED,
      SELECT,
      REFERENCE,
      IDENT,
      LITERAL,
      TYPEIDENT,
      TYPEARRAY,
      TYPEAPPLY,
      TYPEUNION,
      TYPEINTERSECTION,
      TYPEPARAMETER,
      WILDCARD,
      TYPEBOUNDKIND,
      ANNOTATION,
      TYPE_ANNOTATION,
      MODIFIERS,
      ANNOTATED_TYPE,
      ERRONEOUS,
      POS,
      NEG,
      NOT,
      COMPL,
      PREINC,
      PREDEC,
      POSTINC,
      POSTDEC,
      NULLCHK,
      OR,
      AND,
      BITOR,
      BITXOR,
      BITAND,
      EQ,
      NE,
      LT,
      GT,
      LE,
      GE,
      SL,
      SR,
      USR,
      PLUS,
      MINUS,
      MUL,
      DIV,
      MOD,
      BITOR_ASG(BITOR),
      BITXOR_ASG(BITXOR),
      BITAND_ASG(BITAND),
      SL_ASG(SL),
      SR_ASG(SR),
      USR_ASG(USR),
      PLUS_ASG(PLUS),
      MINUS_ASG(MINUS),
      MUL_ASG(MUL),
      DIV_ASG(DIV),
      MOD_ASG(MOD),
      LETEXPR;

      private final Tag noAssignTag;
      private static final int numberOfOperators = MOD.ordinal() - POS.ordinal() + 1;

      private Tag(Tag var3) {
         this.noAssignTag = var3;
      }

      private Tag() {
         this((Tag)null);
      }

      public static int getNumberOfOperators() {
         return numberOfOperators;
      }

      public Tag noAssignOp() {
         if (this.noAssignTag != null) {
            return this.noAssignTag;
         } else {
            throw new AssertionError("noAssignOp() method is not available for non assignment tags");
         }
      }

      public boolean isPostUnaryOp() {
         return this == POSTINC || this == POSTDEC;
      }

      public boolean isIncOrDecUnaryOp() {
         return this == PREINC || this == PREDEC || this == POSTINC || this == POSTDEC;
      }

      public boolean isAssignop() {
         return this.noAssignTag != null;
      }

      public int operatorIndex() {
         return this.ordinal() - POS.ordinal();
      }
   }
}
