package com.sun.tools.javac.model;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.PrintingProcessor;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Constants;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

public class JavacElements implements Elements {
   private JavaCompiler javaCompiler;
   private Symtab syms;
   private Names names;
   private Types types;
   private Enter enter;

   public static JavacElements instance(Context var0) {
      JavacElements var1 = (JavacElements)var0.get(JavacElements.class);
      if (var1 == null) {
         var1 = new JavacElements(var0);
      }

      return var1;
   }

   protected JavacElements(Context var1) {
      this.setContext(var1);
   }

   public void setContext(Context var1) {
      var1.put((Class)JavacElements.class, (Object)this);
      this.javaCompiler = JavaCompiler.instance(var1);
      this.syms = Symtab.instance(var1);
      this.names = Names.instance(var1);
      this.types = Types.instance(var1);
      this.enter = Enter.instance(var1);
   }

   public Symbol.PackageSymbol getPackageElement(CharSequence var1) {
      String var2 = var1.toString();
      if (var2.equals("")) {
         return this.syms.unnamedPackage;
      } else {
         return SourceVersion.isName(var2) ? (Symbol.PackageSymbol)this.nameToSymbol(var2, Symbol.PackageSymbol.class) : null;
      }
   }

   public Symbol.ClassSymbol getTypeElement(CharSequence var1) {
      String var2 = var1.toString();
      return SourceVersion.isName(var2) ? (Symbol.ClassSymbol)this.nameToSymbol(var2, Symbol.ClassSymbol.class) : null;
   }

   private Symbol nameToSymbol(String var1, Class var2) {
      Name var3 = this.names.fromString(var1);
      Symbol var4 = var2 == Symbol.ClassSymbol.class ? (Symbol)this.syms.classes.get(var3) : (Symbol)this.syms.packages.get(var3);

      try {
         if (var4 == null) {
            var4 = this.javaCompiler.resolveIdent(var1);
         }

         var4.complete();
         return var4.kind != 63 && var4.exists() && var2.isInstance(var4) && var3.equals(var4.getQualifiedName()) ? (Symbol)var2.cast(var4) : null;
      } catch (Symbol.CompletionFailure var6) {
         return null;
      }
   }

   public JavacSourcePosition getSourcePosition(Element var1) {
      Pair var2 = this.getTreeAndTopLevel(var1);
      if (var2 == null) {
         return null;
      } else {
         JCTree var3 = (JCTree)var2.fst;
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var2.snd;
         JavaFileObject var5 = var4.sourcefile;
         return var5 == null ? null : new JavacSourcePosition(var5, var3.pos, var4.lineMap);
      }
   }

   public JavacSourcePosition getSourcePosition(Element var1, AnnotationMirror var2) {
      Pair var3 = this.getTreeAndTopLevel(var1);
      if (var3 == null) {
         return null;
      } else {
         JCTree var4 = (JCTree)var3.fst;
         JCTree.JCCompilationUnit var5 = (JCTree.JCCompilationUnit)var3.snd;
         JavaFileObject var6 = var5.sourcefile;
         if (var6 == null) {
            return null;
         } else {
            JCTree var7 = this.matchAnnoToTree(var2, var1, var4);
            return var7 == null ? null : new JavacSourcePosition(var6, var7.pos, var5.lineMap);
         }
      }
   }

   public JavacSourcePosition getSourcePosition(Element var1, AnnotationMirror var2, AnnotationValue var3) {
      return this.getSourcePosition(var1, var2);
   }

   private JCTree matchAnnoToTree(AnnotationMirror var1, Element var2, JCTree var3) {
      Symbol var4 = (Symbol)cast(Symbol.class, var2);

      class Vis extends JCTree.Visitor {
         List result = null;

         public void visitTopLevel(JCTree.JCCompilationUnit var1) {
            this.result = var1.packageAnnotations;
         }

         public void visitClassDef(JCTree.JCClassDecl var1) {
            this.result = var1.mods.annotations;
         }

         public void visitMethodDef(JCTree.JCMethodDecl var1) {
            this.result = var1.mods.annotations;
         }

         public void visitVarDef(JCTree.JCVariableDecl var1) {
            this.result = var1.mods.annotations;
         }
      }

      Vis var5 = new Vis();
      var3.accept(var5);
      if (var5.result == null) {
         return null;
      } else {
         List var6 = var4.getRawAttributes();
         return this.matchAnnoToTree((Attribute.Compound)cast(Attribute.Compound.class, var1), var6, var5.result);
      }
   }

   private JCTree matchAnnoToTree(Attribute.Compound var1, List var2, List var3) {
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Attribute.Compound var5 = (Attribute.Compound)var4.next();
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            JCTree.JCAnnotation var7 = (JCTree.JCAnnotation)var6.next();
            JCTree var8 = this.matchAnnoToTree((Attribute.Compound)var1, (Attribute)var5, (JCTree)var7);
            if (var8 != null) {
               return var8;
            }
         }
      }

      return null;
   }

   private JCTree matchAnnoToTree(final Attribute.Compound var1, Attribute var2, final JCTree var3) {
      if (var2 == var1) {
         return var3.type.tsym == var1.type.tsym ? var3 : null;
      } else {
         class Vis implements Attribute.Visitor {
            JCTree result = null;

            public void visitConstant(Attribute.Constant var1x) {
            }

            public void visitClass(Attribute.Class var1x) {
            }

            public void visitCompound(Attribute.Compound var1x) {
               Iterator var2 = var1x.values.iterator();

               while(var2.hasNext()) {
                  Pair var3x = (Pair)var2.next();
                  JCTree.JCExpression var4 = JavacElements.this.scanForAssign((Symbol.MethodSymbol)var3x.fst, var3);
                  if (var4 != null) {
                     JCTree var5 = JavacElements.this.matchAnnoToTree((Attribute.Compound)var1, (Attribute)((Attribute)var3x.snd), (JCTree)var4);
                     if (var5 != null) {
                        this.result = var5;
                        return;
                     }
                  }
               }

            }

            public void visitArray(Attribute.Array var1x) {
               if (var3.hasTag(JCTree.Tag.NEWARRAY) && JavacElements.this.types.elemtype(var1x.type).tsym == var1.type.tsym) {
                  List var2 = ((JCTree.JCNewArray)var3).elems;
                  Attribute[] var3x = var1x.values;
                  int var4 = var3x.length;

                  for(int var5 = 0; var5 < var4; ++var5) {
                     Attribute var6 = var3x[var5];
                     if (var6 == var1) {
                        this.result = (JCTree)var2.head;
                        return;
                     }

                     var2 = var2.tail;
                  }
               }

            }

            public void visitEnum(Attribute.Enum var1x) {
            }

            public void visitError(Attribute.Error var1x) {
            }
         }

         Vis var4 = new Vis();
         var2.accept(var4);
         return var4.result;
      }
   }

   private JCTree.JCExpression scanForAssign(final Symbol.MethodSymbol var1, final JCTree var2) {
      class TS extends TreeScanner {
         JCTree.JCExpression result = null;

         public void scan(JCTree var1x) {
            if (var1x != null && this.result == null) {
               var1x.accept(this);
            }

         }

         public void visitAnnotation(JCTree.JCAnnotation var1x) {
            if (var1x == var2) {
               this.scan(var1x.args);
            }

         }

         public void visitAssign(JCTree.JCAssign var1x) {
            if (var1x.lhs.hasTag(JCTree.Tag.IDENT)) {
               JCTree.JCIdent var2x = (JCTree.JCIdent)var1x.lhs;
               if (var2x.sym == var1) {
                  this.result = var1x.rhs;
               }
            }

         }
      }

      TS var3 = new TS();
      var2.accept(var3);
      return var3.result;
   }

   public JCTree getTree(Element var1) {
      Pair var2 = this.getTreeAndTopLevel(var1);
      return var2 != null ? (JCTree)var2.fst : null;
   }

   public String getDocComment(Element var1) {
      Pair var2 = this.getTreeAndTopLevel(var1);
      if (var2 == null) {
         return null;
      } else {
         JCTree var3 = (JCTree)var2.fst;
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var2.snd;
         return var4.docComments == null ? null : var4.docComments.getCommentText(var3);
      }
   }

   public PackageElement getPackageOf(Element var1) {
      return ((Symbol)cast(Symbol.class, var1)).packge();
   }

   public boolean isDeprecated(Element var1) {
      Symbol var2 = (Symbol)cast(Symbol.class, var1);
      return (var2.flags() & 131072L) != 0L;
   }

   public Name getBinaryName(TypeElement var1) {
      return ((Symbol.TypeSymbol)cast(Symbol.TypeSymbol.class, var1)).flatName();
   }

   public Map getElementValuesWithDefaults(AnnotationMirror var1) {
      Attribute.Compound var2 = (Attribute.Compound)cast(Attribute.Compound.class, var1);
      DeclaredType var3 = var1.getAnnotationType();
      Map var4 = var2.getElementValues();
      Iterator var5 = ElementFilter.methodsIn(var3.asElement().getEnclosedElements()).iterator();

      while(var5.hasNext()) {
         ExecutableElement var6 = (ExecutableElement)var5.next();
         Symbol.MethodSymbol var7 = (Symbol.MethodSymbol)var6;
         Attribute var8 = var7.getDefaultValue();
         if (var8 != null && !var4.containsKey(var7)) {
            var4.put(var7, var8);
         }
      }

      return var4;
   }

   public FilteredMemberList getAllMembers(TypeElement var1) {
      Symbol var2 = (Symbol)cast(Symbol.class, var1);
      Scope var3 = var2.members().dupUnshared();
      List var4 = this.types.closure(var2.asType());
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Type var6 = (Type)var5.next();
         this.addMembers(var3, var6);
      }

      return new FilteredMemberList(var3);
   }

   private void addMembers(Scope var1, Type var2) {
      label56:
      for(Scope.Entry var3 = var2.asElement().members().elems; var3 != null; var3 = var3.sibling) {
         for(Scope.Entry var4 = var1.lookup(var3.sym.getSimpleName()); var4.scope != null; var4 = var4.next()) {
            if (var4.sym.kind == var3.sym.kind && (var4.sym.flags() & 4096L) == 0L && var4.sym.getKind() == ElementKind.METHOD && this.overrides((ExecutableElement)var4.sym, (ExecutableElement)var3.sym, (TypeElement)var2.asElement())) {
               continue label56;
            }
         }

         boolean var5 = var3.sym.getEnclosingElement() != var1.owner;
         ElementKind var6 = var3.sym.getKind();
         boolean var7 = var6 == ElementKind.CONSTRUCTOR || var6 == ElementKind.INSTANCE_INIT || var6 == ElementKind.STATIC_INIT;
         if (!var5 || !var7 && var3.sym.isInheritedIn(var1.owner, this.types)) {
            var1.enter(var3.sym);
         }
      }

   }

   public List getAllAnnotationMirrors(Element var1) {
      Object var2 = (Symbol)cast(Symbol.class, var1);
      List var3 = ((Symbol)var2).getAnnotationMirrors();

      while(((Symbol)var2).getKind() == ElementKind.CLASS) {
         Type var4 = ((Symbol.ClassSymbol)var2).getSuperclass();
         if (!var4.hasTag(TypeTag.CLASS) || var4.isErroneous() || var4.tsym == this.syms.objectType.tsym) {
            break;
         }

         var2 = var4.tsym;
         List var5 = var3;
         List var6 = ((Symbol)var2).getAnnotationMirrors();
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            Attribute.Compound var8 = (Attribute.Compound)var7.next();
            if (this.isInherited(var8.type) && !containsAnnoOfType(var5, var8.type)) {
               var3 = var3.prepend(var8);
            }
         }
      }

      return var3;
   }

   private boolean isInherited(Type var1) {
      return var1.tsym.attribute(this.syms.inheritedType.tsym) != null;
   }

   private static boolean containsAnnoOfType(List var0, Type var1) {
      Iterator var2 = var0.iterator();

      Attribute.Compound var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Attribute.Compound)var2.next();
      } while(var3.type.tsym != var1.tsym);

      return true;
   }

   public boolean hides(Element var1, Element var2) {
      Symbol var3 = (Symbol)cast(Symbol.class, var1);
      Symbol var4 = (Symbol)cast(Symbol.class, var2);
      if (var3 != var4 && var3.kind == var4.kind && var3.name == var4.name) {
         if (var3.kind == 16 && (!var3.isStatic() || !this.types.isSubSignature(var3.type, var4.type))) {
            return false;
         } else {
            Symbol.ClassSymbol var5 = var3.owner.enclClass();
            Symbol.ClassSymbol var6 = var4.owner.enclClass();
            return var5 != null && var6 != null && var5.isSubClass(var6, this.types) ? var4.isInheritedIn(var5, this.types) : false;
         }
      } else {
         return false;
      }
   }

   public boolean overrides(ExecutableElement var1, ExecutableElement var2, TypeElement var3) {
      Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)cast(Symbol.MethodSymbol.class, var1);
      Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)cast(Symbol.MethodSymbol.class, var2);
      Symbol.ClassSymbol var6 = (Symbol.ClassSymbol)cast(Symbol.ClassSymbol.class, var3);
      return var4.name == var5.name && var4 != var5 && !var4.isStatic() && var5.isMemberOf(var6, this.types) && var4.overrides(var5, var6, this.types, false);
   }

   public String getConstantExpression(Object var1) {
      return Constants.format(var1);
   }

   public void printElements(Writer var1, Element... var2) {
      Element[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Element var6 = var3[var5];
         ((PrintingProcessor.PrintingElementVisitor)(new PrintingProcessor.PrintingElementVisitor(var1, this)).visit(var6)).flush();
      }

   }

   public Name getName(CharSequence var1) {
      return this.names.fromString(var1.toString());
   }

   public boolean isFunctionalInterface(TypeElement var1) {
      if (var1.getKind() != ElementKind.INTERFACE) {
         return false;
      } else {
         Symbol.TypeSymbol var2 = (Symbol.TypeSymbol)cast(Symbol.TypeSymbol.class, var1);
         return this.types.isFunctionalInterface(var2);
      }
   }

   private Pair getTreeAndTopLevel(Element var1) {
      Symbol var2 = (Symbol)cast(Symbol.class, var1);
      Env var3 = this.getEnterEnv(var2);
      if (var3 == null) {
         return null;
      } else {
         JCTree var4 = TreeInfo.declarationFor(var2, var3.tree);
         return var4 != null && var3.toplevel != null ? new Pair(var4, var3.toplevel) : null;
      }
   }

   public Pair getTreeAndTopLevel(Element var1, AnnotationMirror var2, AnnotationValue var3) {
      if (var1 == null) {
         return null;
      } else {
         Pair var4 = this.getTreeAndTopLevel(var1);
         if (var4 == null) {
            return null;
         } else if (var2 == null) {
            return var4;
         } else {
            JCTree var5 = this.matchAnnoToTree(var2, var1, (JCTree)var4.fst);
            return var5 == null ? var4 : new Pair(var5, var4.snd);
         }
      }
   }

   private Env getEnterEnv(Symbol var1) {
      Object var2 = var1.kind != 1 ? var1.enclClass() : (Symbol.PackageSymbol)var1;
      return var2 != null ? this.enter.getEnv((Symbol.TypeSymbol)var2) : null;
   }

   private static Object cast(Class var0, Object var1) {
      if (!var0.isInstance(var1)) {
         throw new IllegalArgumentException(var1.toString());
      } else {
         return var0.cast(var1);
      }
   }
}
