package com.sun.tools.javac.api;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTreeScanner;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.comp.Resolve;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.DCTree;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Abort;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;

public class JavacTrees extends DocTrees {
   private Resolve resolve;
   private Enter enter;
   private Log log;
   private MemberEnter memberEnter;
   private Attr attr;
   private TreeMaker treeMaker;
   private JavacElements elements;
   private JavacTaskImpl javacTaskImpl;
   private Names names;
   private Types types;
   Types.TypeRelation fuzzyMatcher = new Types.TypeRelation() {
      public Boolean visitType(Type var1, Type var2) {
         if (var1 == var2) {
            return true;
         } else if (var2.isPartial()) {
            return (Boolean)this.visit(var2, var1);
         } else {
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
               case BOT:
               case NONE:
                  return var1.hasTag(var2.getTag());
               default:
                  throw new AssertionError("fuzzyMatcher " + var1.getTag());
            }
         }
      }

      public Boolean visitArrayType(Type.ArrayType var1, Type var2) {
         if (var1 == var2) {
            return true;
         } else {
            return var2.isPartial() ? (Boolean)this.visit(var2, var1) : var2.hasTag(TypeTag.ARRAY) && (Boolean)this.visit(var1.elemtype, JavacTrees.this.types.elemtype(var2));
         }
      }

      public Boolean visitClassType(Type.ClassType var1, Type var2) {
         if (var1 == var2) {
            return true;
         } else {
            return var2.isPartial() ? (Boolean)this.visit(var2, var1) : var1.tsym == var2.tsym;
         }
      }

      public Boolean visitErrorType(Type.ErrorType var1, Type var2) {
         return var2.hasTag(TypeTag.CLASS) && var1.tsym.name == ((Type.ClassType)var2).tsym.name;
      }
   };

   public static JavacTrees instance(JavaCompiler.CompilationTask var0) {
      if (!(var0 instanceof BasicJavacTask)) {
         throw new IllegalArgumentException();
      } else {
         return instance(((BasicJavacTask)var0).getContext());
      }
   }

   public static JavacTrees instance(ProcessingEnvironment var0) {
      if (!(var0 instanceof JavacProcessingEnvironment)) {
         throw new IllegalArgumentException();
      } else {
         return instance(((JavacProcessingEnvironment)var0).getContext());
      }
   }

   public static JavacTrees instance(Context var0) {
      JavacTrees var1 = (JavacTrees)var0.get(JavacTrees.class);
      if (var1 == null) {
         var1 = new JavacTrees(var0);
      }

      return var1;
   }

   protected JavacTrees(Context var1) {
      var1.put((Class)JavacTrees.class, (Object)this);
      this.init(var1);
   }

   public void updateContext(Context var1) {
      this.init(var1);
   }

   private void init(Context var1) {
      this.attr = Attr.instance(var1);
      this.enter = Enter.instance(var1);
      this.elements = JavacElements.instance(var1);
      this.log = Log.instance(var1);
      this.resolve = Resolve.instance(var1);
      this.treeMaker = TreeMaker.instance(var1);
      this.memberEnter = MemberEnter.instance(var1);
      this.names = Names.instance(var1);
      this.types = Types.instance(var1);
      JavacTask var2 = (JavacTask)var1.get(JavacTask.class);
      if (var2 instanceof JavacTaskImpl) {
         this.javacTaskImpl = (JavacTaskImpl)var2;
      }

   }

   public DocSourcePositions getSourcePositions() {
      return new DocSourcePositions() {
         public long getStartPosition(CompilationUnitTree var1, Tree var2) {
            return (long)TreeInfo.getStartPos((JCTree)var2);
         }

         public long getEndPosition(CompilationUnitTree var1, Tree var2) {
            EndPosTable var3 = ((JCTree.JCCompilationUnit)var1).endPositions;
            return (long)TreeInfo.getEndPos((JCTree)var2, var3);
         }

         public long getStartPosition(CompilationUnitTree var1, DocCommentTree var2, DocTree var3) {
            return ((DCTree)var3).getSourcePosition((DCTree.DCDocComment)var2);
         }

         public long getEndPosition(CompilationUnitTree var1, DocCommentTree var2, DocTree var3) {
            DCTree.DCDocComment var4 = (DCTree.DCDocComment)var2;
            if (var3 instanceof DCTree.DCEndPosTree) {
               int var5 = ((DCTree.DCEndPosTree)var3).getEndPos(var4);
               if (var5 != -1) {
                  return (long)var5;
               }
            }

            byte var12 = 0;
            DocTree var10;
            switch (var3.getKind()) {
               case TEXT:
                  DCTree.DCText var6 = (DCTree.DCText)var3;
                  return (long)var4.comment.getSourcePos(var6.pos + var6.text.length());
               case ERRONEOUS:
                  DCTree.DCErroneous var7 = (DCTree.DCErroneous)var3;
                  return (long)var4.comment.getSourcePos(var7.pos + var7.body.length());
               case IDENTIFIER:
                  DCTree.DCIdentifier var8 = (DCTree.DCIdentifier)var3;
                  return (long)var4.comment.getSourcePos(var8.pos + (var8.name != JavacTrees.this.names.error ? var8.name.length() : 0));
               case PARAM:
                  DCTree.DCParam var9 = (DCTree.DCParam)var3;
                  if (var9.isTypeParameter && var9.getDescription().isEmpty()) {
                     var12 = 1;
                  }
               case AUTHOR:
               case DEPRECATED:
               case RETURN:
               case SEE:
               case SERIAL:
               case SERIAL_DATA:
               case SERIAL_FIELD:
               case SINCE:
               case THROWS:
               case UNKNOWN_BLOCK_TAG:
               case VERSION:
                  var10 = JavacTrees.this.getLastChild(var3);
                  if (var10 != null) {
                     return this.getEndPosition(var1, var2, var10) + (long)var12;
                  }

                  DCTree.DCBlockTag var11 = (DCTree.DCBlockTag)var3;
                  return (long)var4.comment.getSourcePos(var11.pos + var11.getTagName().length() + 1);
               default:
                  var10 = JavacTrees.this.getLastChild(var3);
                  return var10 != null ? this.getEndPosition(var1, var2, var10) : -1L;
            }
         }
      };
   }

   private DocTree getLastChild(DocTree var1) {
      final DocTree[] var2 = new DocTree[]{null};
      var1.accept(new DocTreeScanner() {
         public Void scan(DocTree var1, Void var2x) {
            if (var1 != null) {
               var2[0] = var1;
            }

            return null;
         }
      }, (Object)null);
      return var2[0];
   }

   public JCTree.JCClassDecl getTree(TypeElement var1) {
      return (JCTree.JCClassDecl)this.getTree((Element)var1);
   }

   public JCTree.JCMethodDecl getTree(ExecutableElement var1) {
      return (JCTree.JCMethodDecl)this.getTree((Element)var1);
   }

   public JCTree getTree(Element var1) {
      Symbol var2 = (Symbol)var1;
      Symbol.ClassSymbol var3 = var2.enclClass();
      Env var4 = this.enter.getEnv(var3);
      if (var4 == null) {
         return null;
      } else {
         JCTree.JCClassDecl var5 = var4.enclClass;
         if (var5 != null) {
            if (TreeInfo.symbolFor(var5) == var1) {
               return var5;
            }

            Iterator var6 = var5.getMembers().iterator();

            while(var6.hasNext()) {
               JCTree var7 = (JCTree)var6.next();
               if (TreeInfo.symbolFor(var7) == var1) {
                  return var7;
               }
            }
         }

         return null;
      }
   }

   public JCTree getTree(Element var1, AnnotationMirror var2) {
      return this.getTree(var1, var2, (AnnotationValue)null);
   }

   public JCTree getTree(Element var1, AnnotationMirror var2, AnnotationValue var3) {
      Pair var4 = this.elements.getTreeAndTopLevel(var1, var2, var3);
      return var4 == null ? null : (JCTree)var4.fst;
   }

   public TreePath getPath(CompilationUnitTree var1, Tree var2) {
      return TreePath.getPath(var1, var2);
   }

   public TreePath getPath(Element var1) {
      return this.getPath(var1, (AnnotationMirror)null, (AnnotationValue)null);
   }

   public TreePath getPath(Element var1, AnnotationMirror var2) {
      return this.getPath(var1, var2, (AnnotationValue)null);
   }

   public TreePath getPath(Element var1, AnnotationMirror var2, AnnotationValue var3) {
      Pair var4 = this.elements.getTreeAndTopLevel(var1, var2, var3);
      return var4 == null ? null : TreePath.getPath((CompilationUnitTree)var4.snd, (Tree)var4.fst);
   }

   public Symbol getElement(TreePath var1) {
      JCTree var2 = (JCTree)var1.getLeaf();
      Symbol var3 = TreeInfo.symbolFor(var2);
      if (var3 == null && TreeInfo.isDeclaration(var2)) {
         for(TreePath var4 = var1; var4 != null; var4 = var4.getParentPath()) {
            JCTree var5 = (JCTree)var4.getLeaf();
            if (var5.hasTag(JCTree.Tag.CLASSDEF)) {
               JCTree.JCClassDecl var6 = (JCTree.JCClassDecl)var5;
               if (var6.sym != null) {
                  if ((var6.sym.flags_field & 268435456L) != 0L) {
                     this.attr.attribClass(var6.pos(), var6.sym);
                     var3 = TreeInfo.symbolFor(var2);
                  }
                  break;
               }
            }
         }
      }

      return var3;
   }

   public Element getElement(DocTreePath var1) {
      DocTree var2 = var1.getLeaf();
      if (var2 instanceof DCTree.DCReference) {
         return this.attributeDocReference(var1.getTreePath(), (DCTree.DCReference)var2);
      } else {
         return var2 instanceof DCTree.DCIdentifier && var1.getParentPath().getLeaf() instanceof DCTree.DCParam ? this.attributeParamIdentifier(var1.getTreePath(), (DCTree.DCParam)var1.getParentPath().getLeaf()) : null;
      }
   }

   private Symbol attributeDocReference(TreePath var1, DCTree.DCReference var2) {
      Env var3 = this.getAttrContext(var1);
      Log.DeferredDiagnosticHandler var4 = new Log.DeferredDiagnosticHandler(this.log);

      Symbol.VarSymbol var25;
      try {
         Name var6;
         try {
            Object var5;
            if (var2.qualifierExpression == null) {
               var5 = var3.enclClass.sym;
               var6 = var2.memberName;
            } else {
               Type var7 = this.attr.attribType(var2.qualifierExpression, var3);
               if (var7.isErroneous()) {
                  Symbol.PackageSymbol var8;
                  if (var2.memberName != null) {
                     var8 = null;
                     return var8;
                  }

                  var8 = this.elements.getPackageElement(var2.qualifierExpression.toString());
                  Symbol.PackageSymbol var9;
                  if (var8 != null) {
                     var9 = var8;
                     return var9;
                  }

                  if (!var2.qualifierExpression.hasTag(JCTree.Tag.IDENT)) {
                     var9 = null;
                     return var9;
                  }

                  var5 = var3.enclClass.sym;
                  var6 = ((JCTree.JCIdent)var2.qualifierExpression).name;
               } else {
                  var5 = var7.tsym;
                  var6 = var2.memberName;
               }
            }

            if (var6 == null) {
               Object var19 = var5;
               return (Symbol)var19;
            }

            List var17;
            if (var2.paramTypes == null) {
               var17 = null;
            } else {
               ListBuffer var18 = new ListBuffer();
               List var21 = var2.paramTypes;

               while(true) {
                  if (!var21.nonEmpty()) {
                     var17 = var18.toList();
                     break;
                  }

                  JCTree var10 = (JCTree)var21.head;
                  Type var11 = this.attr.attribType(var10, var3);
                  var18.add(var11);
                  var21 = var21.tail;
               }
            }

            Symbol.ClassSymbol var20 = (Symbol.ClassSymbol)this.types.cvarUpperBound(((Symbol.TypeSymbol)var5).type).tsym;
            Symbol.MethodSymbol var22 = var6 == var20.name ? this.findConstructor(var20, var17) : this.findMethod(var20, var6, var17);
            if (var17 != null) {
               Symbol.MethodSymbol var24 = var22;
               return var24;
            }

            Symbol.VarSymbol var23 = var2.paramTypes != null ? null : this.findField(var20, var6);
            if (var23 == null || var22 != null && !this.types.isSubtypeUnchecked(var23.enclClass().asType(), var22.enclClass().asType())) {
               Symbol.MethodSymbol var26 = var22;
               return var26;
            }

            var25 = var23;
         } catch (Abort var15) {
            var6 = null;
            return var6;
         }
      } finally {
         this.log.popDiagnosticHandler(var4);
      }

      return var25;
   }

   private Symbol attributeParamIdentifier(TreePath var1, DCTree.DCParam var2) {
      Symbol var3 = this.getElement(var1);
      if (var3 == null) {
         return null;
      } else {
         ElementKind var4 = var3.getKind();
         List var5 = List.nil();
         if (var4 != ElementKind.METHOD && var4 != ElementKind.CONSTRUCTOR) {
            if (var4.isClass() || var4.isInterface()) {
               Symbol.ClassSymbol var8 = (Symbol.ClassSymbol)var3;
               var5 = var8.getTypeParameters();
            }
         } else {
            Symbol.MethodSymbol var6 = (Symbol.MethodSymbol)var3;
            var5 = var2.isTypeParameter() ? var6.getTypeParameters() : var6.getParameters();
         }

         Iterator var9 = var5.iterator();

         Symbol var7;
         do {
            if (!var9.hasNext()) {
               return null;
            }

            var7 = (Symbol)var9.next();
         } while(var7.getSimpleName() != var2.getName().getName());

         return var7;
      }
   }

   private Symbol.VarSymbol findField(Symbol.ClassSymbol var1, Name var2) {
      return this.searchField(var1, var2, new HashSet());
   }

   private Symbol.VarSymbol searchField(Symbol.ClassSymbol var1, Name var2, Set var3) {
      if (var3.contains(var1)) {
         return null;
      } else {
         var3.add(var1);

         for(Scope.Entry var4 = var1.members().lookup(var2); var4.scope != null; var4 = var4.next()) {
            if (var4.sym.kind == 4) {
               return (Symbol.VarSymbol)var4.sym;
            }
         }

         Symbol.ClassSymbol var10 = var1.owner.enclClass();
         if (var10 != null) {
            Symbol.VarSymbol var5 = this.searchField(var10, var2, var3);
            if (var5 != null) {
               return var5;
            }
         }

         Type var11 = var1.getSuperclass();
         if (var11.tsym != null) {
            Symbol.VarSymbol var6 = this.searchField((Symbol.ClassSymbol)var11.tsym, var2, var3);
            if (var6 != null) {
               return var6;
            }
         }

         List var12 = var1.getInterfaces();

         for(List var7 = var12; var7.nonEmpty(); var7 = var7.tail) {
            Type var8 = (Type)var7.head;
            if (!var8.isErroneous()) {
               Symbol.VarSymbol var9 = this.searchField((Symbol.ClassSymbol)var8.tsym, var2, var3);
               if (var9 != null) {
                  return var9;
               }
            }
         }

         return null;
      }
   }

   Symbol.MethodSymbol findConstructor(Symbol.ClassSymbol var1, List var2) {
      for(Scope.Entry var3 = var1.members().lookup(this.names.init); var3.scope != null; var3 = var3.next()) {
         if (var3.sym.kind == 16 && this.hasParameterTypes((Symbol.MethodSymbol)var3.sym, var2)) {
            return (Symbol.MethodSymbol)var3.sym;
         }
      }

      return null;
   }

   private Symbol.MethodSymbol findMethod(Symbol.ClassSymbol var1, Name var2, List var3) {
      return this.searchMethod(var1, var2, var3, new HashSet());
   }

   private Symbol.MethodSymbol searchMethod(Symbol.ClassSymbol var1, Name var2, List var3, Set var4) {
      if (var2 == this.names.init) {
         return null;
      } else if (var4.contains(var1)) {
         return null;
      } else {
         var4.add(var1);
         Scope.Entry var5 = var1.members().lookup(var2);
         if (var3 == null) {
            Symbol.MethodSymbol var6;
            for(var6 = null; var5.scope != null; var5 = var5.next()) {
               if (var5.sym.kind == 16 && var5.sym.name == var2) {
                  var6 = (Symbol.MethodSymbol)var5.sym;
               }
            }

            if (var6 != null) {
               return var6;
            }
         } else {
            while(var5.scope != null) {
               if (var5.sym != null && var5.sym.kind == 16 && this.hasParameterTypes((Symbol.MethodSymbol)var5.sym, var3)) {
                  return (Symbol.MethodSymbol)var5.sym;
               }

               var5 = var5.next();
            }
         }

         Type var11 = var1.getSuperclass();
         if (var11.tsym != null) {
            Symbol.MethodSymbol var7 = this.searchMethod((Symbol.ClassSymbol)var11.tsym, var2, var3, var4);
            if (var7 != null) {
               return var7;
            }
         }

         List var12 = var1.getInterfaces();

         for(List var8 = var12; var8.nonEmpty(); var8 = var8.tail) {
            Type var9 = (Type)var8.head;
            if (!var9.isErroneous()) {
               Symbol.MethodSymbol var10 = this.searchMethod((Symbol.ClassSymbol)var9.tsym, var2, var3, var4);
               if (var10 != null) {
                  return var10;
               }
            }
         }

         Symbol.ClassSymbol var13 = var1.owner.enclClass();
         if (var13 != null) {
            Symbol.MethodSymbol var14 = this.searchMethod(var13, var2, var3, var4);
            if (var14 != null) {
               return var14;
            }
         }

         return null;
      }
   }

   private boolean hasParameterTypes(Symbol.MethodSymbol var1, List var2) {
      if (var2 == null) {
         return true;
      } else if (var1.params().size() != var2.size()) {
         return false;
      } else {
         List var3 = this.types.erasureRecursive(var1.asType()).getParameterTypes();
         return Type.isErroneous(var2) ? this.fuzzyMatch(var2, var3) : this.types.isSameTypes(var2, var3);
      }
   }

   boolean fuzzyMatch(List var1, List var2) {
      List var3 = var1;

      for(List var4 = var2; var3.nonEmpty(); var4 = var4.tail) {
         if (!this.fuzzyMatch((Type)var3.head, (Type)var4.head)) {
            return false;
         }

         var3 = var3.tail;
      }

      return true;
   }

   boolean fuzzyMatch(Type var1, Type var2) {
      Boolean var3 = (Boolean)this.fuzzyMatcher.visit(var1, var2);
      return var3 == Boolean.TRUE;
   }

   public TypeMirror getTypeMirror(TreePath var1) {
      Tree var2 = var1.getLeaf();
      return ((JCTree)var2).type;
   }

   public JavacScope getScope(TreePath var1) {
      return new JavacScope(this.getAttrContext(var1));
   }

   public String getDocComment(TreePath var1) {
      CompilationUnitTree var2 = var1.getCompilationUnit();
      Tree var3 = var1.getLeaf();
      if (var2 instanceof JCTree.JCCompilationUnit && var3 instanceof JCTree) {
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var2;
         if (var4.docComments != null) {
            return var4.docComments.getCommentText((JCTree)var3);
         }
      }

      return null;
   }

   public DocCommentTree getDocCommentTree(TreePath var1) {
      CompilationUnitTree var2 = var1.getCompilationUnit();
      Tree var3 = var1.getLeaf();
      if (var2 instanceof JCTree.JCCompilationUnit && var3 instanceof JCTree) {
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var2;
         if (var4.docComments != null) {
            return var4.docComments.getCommentTree((JCTree)var3);
         }
      }

      return null;
   }

   public boolean isAccessible(com.sun.source.tree.Scope var1, TypeElement var2) {
      if (var1 instanceof JavacScope && var2 instanceof Symbol.ClassSymbol) {
         Env var3 = ((JavacScope)var1).env;
         return this.resolve.isAccessible(var3, (Symbol.TypeSymbol)((Symbol.ClassSymbol)var2), true);
      } else {
         return false;
      }
   }

   public boolean isAccessible(com.sun.source.tree.Scope var1, Element var2, DeclaredType var3) {
      if (var1 instanceof JavacScope && var2 instanceof Symbol && var3 instanceof Type) {
         Env var4 = ((JavacScope)var1).env;
         return this.resolve.isAccessible(var4, (Type)var3, (Symbol)var2, true);
      } else {
         return false;
      }
   }

   private Env getAttrContext(TreePath var1) {
      if (!(var1.getLeaf() instanceof JCTree)) {
         throw new IllegalArgumentException();
      } else {
         if (this.javacTaskImpl != null) {
            try {
               this.javacTaskImpl.enter((Iterable)null);
            } catch (IOException var14) {
               throw new Error("unexpected error while entering symbols: " + var14);
            }
         }

         JCTree.JCCompilationUnit var2 = (JCTree.JCCompilationUnit)var1.getCompilationUnit();
         Copier var3 = this.createCopier(this.treeMaker.forToplevel(var2));
         Env var4 = null;
         JCTree.JCMethodDecl var5 = null;
         JCTree.JCVariableDecl var6 = null;
         List var7 = List.nil();

         for(TreePath var8 = var1; var8 != null; var8 = var8.getParentPath()) {
            var7 = var7.prepend(var8.getLeaf());
         }

         for(; var7.nonEmpty(); var7 = var7.tail) {
            Tree var9 = (Tree)var7.head;
            switch (var9.getKind()) {
               case COMPILATION_UNIT:
                  var4 = this.enter.getTopLevelEnv((JCTree.JCCompilationUnit)var9);
                  break;
               case ANNOTATION_TYPE:
               case CLASS:
               case ENUM:
               case INTERFACE:
                  var4 = this.enter.getClassEnv(((JCTree.JCClassDecl)var9).sym);
                  break;
               case METHOD:
                  var5 = (JCTree.JCMethodDecl)var9;
                  var4 = this.memberEnter.getMethodEnv(var5, var4);
                  break;
               case VARIABLE:
                  var6 = (JCTree.JCVariableDecl)var9;
                  break;
               case BLOCK:
                  if (var5 != null) {
                     try {
                        Assert.check(var5.body == var9);
                        var5.body = (JCTree.JCBlock)var3.copy((JCTree.JCBlock)var9, (JCTree)((JCTree)var1.getLeaf()));
                        var4 = this.attribStatToTree(var5.body, var4, var3.leafCopy);
                     } finally {
                        var5.body = (JCTree.JCBlock)var9;
                     }
                  } else {
                     JCTree.JCBlock var10 = (JCTree.JCBlock)var3.copy((JCTree.JCBlock)var9, (JCTree)((JCTree)var1.getLeaf()));
                     var4 = this.attribStatToTree(var10, var4, var3.leafCopy);
                  }

                  return var4;
               default:
                  if (var6 != null && var6.getInitializer() == var9) {
                     var4 = this.memberEnter.getInitEnv(var6, var4);
                     JCTree.JCExpression var15 = (JCTree.JCExpression)var3.copy((JCTree.JCExpression)var9, (JCTree)((JCTree)var1.getLeaf()));
                     var4 = this.attribExprToTree(var15, var4, var3.leafCopy);
                     return var4;
                  }
            }
         }

         return var6 != null ? this.memberEnter.getInitEnv(var6, var4) : var4;
      }
   }

   private Env attribStatToTree(JCTree var1, Env var2, JCTree var3) {
      JavaFileObject var4 = this.log.useSource(var2.toplevel.sourcefile);

      Env var5;
      try {
         var5 = this.attr.attribStatToTree(var1, var2, var3);
      } finally {
         this.log.useSource(var4);
      }

      return var5;
   }

   private Env attribExprToTree(JCTree.JCExpression var1, Env var2, JCTree var3) {
      JavaFileObject var4 = this.log.useSource(var2.toplevel.sourcefile);

      Env var5;
      try {
         var5 = this.attr.attribExprToTree(var1, var2, var3);
      } finally {
         this.log.useSource(var4);
      }

      return var5;
   }

   protected Copier createCopier(TreeMaker var1) {
      return new Copier(var1);
   }

   public TypeMirror getOriginalType(ErrorType var1) {
      return (TypeMirror)(var1 instanceof Type.ErrorType ? ((Type.ErrorType)var1).getOriginalType() : Type.noType);
   }

   public void printMessage(Diagnostic.Kind var1, CharSequence var2, Tree var3, CompilationUnitTree var4) {
      this.printMessage(var1, var2, ((JCTree)var3).pos(), var4);
   }

   public void printMessage(Diagnostic.Kind var1, CharSequence var2, DocTree var3, DocCommentTree var4, CompilationUnitTree var5) {
      this.printMessage(var1, var2, ((DCTree)var3).pos((DCTree.DCDocComment)var4), var5);
   }

   private void printMessage(Diagnostic.Kind var1, CharSequence var2, JCDiagnostic.DiagnosticPosition var3, CompilationUnitTree var4) {
      JavaFileObject var5 = null;
      JavaFileObject var6 = null;
      var6 = var4.getSourceFile();
      if (var6 == null) {
         var3 = null;
      } else {
         var5 = this.log.useSource(var6);
      }

      try {
         switch (var1) {
            case ERROR:
               boolean var7 = this.log.multipleErrors;

               try {
                  this.log.error(var3, "proc.messager", new Object[]{var2.toString()});
                  break;
               } finally {
                  this.log.multipleErrors = var7;
               }
            case WARNING:
               this.log.warning(var3, "proc.messager", new Object[]{var2.toString()});
               break;
            case MANDATORY_WARNING:
               this.log.mandatoryWarning(var3, "proc.messager", new Object[]{var2.toString()});
               break;
            default:
               this.log.note(var3, "proc.messager", new Object[]{var2.toString()});
         }
      } finally {
         if (var5 != null) {
            this.log.useSource(var5);
         }

      }

   }

   public TypeMirror getLub(CatchTree var1) {
      JCTree.JCCatch var2 = (JCTree.JCCatch)var1;
      JCTree.JCVariableDecl var3 = var2.param;
      if (var3.type != null && var3.type.getKind() == TypeKind.UNION) {
         Type.UnionClassType var4 = (Type.UnionClassType)var3.type;
         return var4.getLub();
      } else {
         return var3.type;
      }
   }

   protected static class Copier extends TreeCopier {
      JCTree leafCopy = null;

      protected Copier(TreeMaker var1) {
         super(var1);
      }

      public JCTree copy(JCTree var1, JCTree var2) {
         JCTree var3 = super.copy((JCTree)var1, var2);
         if (var1 == var2) {
            this.leafCopy = var3;
         }

         return var3;
      }
   }
}
