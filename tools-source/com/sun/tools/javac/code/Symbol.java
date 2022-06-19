package com.sun.tools.javac.code;

import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.jvm.Code;
import com.sun.tools.javac.jvm.Pool;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Constants;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

public abstract class Symbol extends AnnoConstruct implements Element {
   public int kind;
   public long flags_field;
   public Name name;
   public Type type;
   public Symbol owner;
   public Completer completer;
   public Type erasure_field;
   protected SymbolMetadata metadata;

   public long flags() {
      return this.flags_field;
   }

   public List getRawAttributes() {
      return this.metadata == null ? List.nil() : this.metadata.getDeclarationAttributes();
   }

   public List getRawTypeAttributes() {
      return this.metadata == null ? List.nil() : this.metadata.getTypeAttributes();
   }

   public Attribute.Compound attribute(Symbol var1) {
      Iterator var2 = this.getRawAttributes().iterator();

      Attribute.Compound var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Attribute.Compound)var2.next();
      } while(var3.type.tsym != var1);

      return var3;
   }

   public boolean annotationsPendingCompletion() {
      return this.metadata == null ? false : this.metadata.pendingCompletion();
   }

   public void appendAttributes(List var1) {
      if (var1.nonEmpty()) {
         this.initedMetadata().append(var1);
      }

   }

   public void appendClassInitTypeAttributes(List var1) {
      if (var1.nonEmpty()) {
         this.initedMetadata().appendClassInitTypeAttributes(var1);
      }

   }

   public void appendInitTypeAttributes(List var1) {
      if (var1.nonEmpty()) {
         this.initedMetadata().appendInitTypeAttributes(var1);
      }

   }

   public void appendTypeAttributesWithCompletion(Annotate.AnnotateRepeatedContext var1) {
      this.initedMetadata().appendTypeAttributesWithCompletion(var1);
   }

   public void appendUniqueTypeAttributes(List var1) {
      if (var1.nonEmpty()) {
         this.initedMetadata().appendUniqueTypes(var1);
      }

   }

   public List getClassInitTypeAttributes() {
      return this.metadata == null ? List.nil() : this.metadata.getClassInitTypeAttributes();
   }

   public List getInitTypeAttributes() {
      return this.metadata == null ? List.nil() : this.metadata.getInitTypeAttributes();
   }

   public List getDeclarationAttributes() {
      return this.metadata == null ? List.nil() : this.metadata.getDeclarationAttributes();
   }

   public boolean hasAnnotations() {
      return this.metadata != null && !this.metadata.isEmpty();
   }

   public boolean hasTypeAnnotations() {
      return this.metadata != null && !this.metadata.isTypesEmpty();
   }

   public void prependAttributes(List var1) {
      if (var1.nonEmpty()) {
         this.initedMetadata().prepend(var1);
      }

   }

   public void resetAnnotations() {
      this.initedMetadata().reset();
   }

   public void setAttributes(Symbol var1) {
      if (this.metadata != null || var1.metadata != null) {
         this.initedMetadata().setAttributes(var1.metadata);
      }

   }

   public void setDeclarationAttributes(List var1) {
      if (this.metadata != null || var1.nonEmpty()) {
         this.initedMetadata().setDeclarationAttributes(var1);
      }

   }

   public void setDeclarationAttributesWithCompletion(Annotate.AnnotateRepeatedContext var1) {
      this.initedMetadata().setDeclarationAttributesWithCompletion(var1);
   }

   public void setTypeAttributes(List var1) {
      if (this.metadata != null || var1.nonEmpty()) {
         if (this.metadata == null) {
            this.metadata = new SymbolMetadata(this);
         }

         this.metadata.setTypeAttributes(var1);
      }

   }

   private SymbolMetadata initedMetadata() {
      if (this.metadata == null) {
         this.metadata = new SymbolMetadata(this);
      }

      return this.metadata;
   }

   public SymbolMetadata getMetadata() {
      return this.metadata;
   }

   public Symbol(int var1, long var2, Name var4, Type var5, Symbol var6) {
      this.kind = var1;
      this.flags_field = var2;
      this.type = var5;
      this.owner = var6;
      this.completer = null;
      this.erasure_field = null;
      this.name = var4;
   }

   public Symbol clone(Symbol var1) {
      throw new AssertionError();
   }

   public Object accept(Visitor var1, Object var2) {
      return var1.visitSymbol(this, var2);
   }

   public String toString() {
      return this.name.toString();
   }

   public Symbol location() {
      return this.owner.name != null && (!this.owner.name.isEmpty() || (this.owner.flags() & 1048576L) != 0L || this.owner.kind == 1 || this.owner.kind == 2) ? this.owner : null;
   }

   public Symbol location(Type var1, Types var2) {
      if (this.owner.name != null && !this.owner.name.isEmpty()) {
         if (this.owner.type.hasTag(TypeTag.CLASS)) {
            Type var3 = var2.asOuterSuper(var1, this.owner);
            if (var3 != null) {
               return var3.tsym;
            }
         }

         return this.owner;
      } else {
         return this.location();
      }
   }

   public Symbol baseSymbol() {
      return this;
   }

   public Type erasure(Types var1) {
      if (this.erasure_field == null) {
         this.erasure_field = var1.erasure(this.type);
      }

      return this.erasure_field;
   }

   public Type externalType(Types var1) {
      Type var2 = this.erasure(var1);
      if (this.name == this.name.table.names.init && this.owner.hasOuterInstance()) {
         Type var3 = var1.erasure(this.owner.type.getEnclosingType());
         return new Type.MethodType(var2.getParameterTypes().prepend(var3), var2.getReturnType(), var2.getThrownTypes(), var2.tsym);
      } else {
         return var2;
      }
   }

   public boolean isDeprecated() {
      return (this.flags_field & 131072L) != 0L;
   }

   public boolean isStatic() {
      return (this.flags() & 8L) != 0L || (this.owner.flags() & 512L) != 0L && this.kind != 16 && this.name != this.name.table.names._this;
   }

   public boolean isInterface() {
      return (this.flags() & 512L) != 0L;
   }

   public boolean isPrivate() {
      return (this.flags_field & 7L) == 2L;
   }

   public boolean isEnum() {
      return (this.flags() & 16384L) != 0L;
   }

   public boolean isLocal() {
      return (this.owner.kind & 20) != 0 || this.owner.kind == 2 && this.owner.isLocal();
   }

   public boolean isAnonymous() {
      return this.name.isEmpty();
   }

   public boolean isConstructor() {
      return this.name == this.name.table.names.init;
   }

   public Name getQualifiedName() {
      return this.name;
   }

   public Name flatName() {
      return this.getQualifiedName();
   }

   public Scope members() {
      return null;
   }

   public boolean isInner() {
      return this.kind == 2 && this.type.getEnclosingType().hasTag(TypeTag.CLASS);
   }

   public boolean hasOuterInstance() {
      return this.type.getEnclosingType().hasTag(TypeTag.CLASS) && (this.flags() & 4194816L) == 0L;
   }

   public ClassSymbol enclClass() {
      Symbol var1;
      for(var1 = this; var1 != null && ((var1.kind & 2) == 0 || !var1.type.hasTag(TypeTag.CLASS)); var1 = var1.owner) {
      }

      return (ClassSymbol)var1;
   }

   public ClassSymbol outermostClass() {
      Symbol var1 = this;

      Symbol var2;
      for(var2 = null; var1.kind != 1; var1 = var1.owner) {
         var2 = var1;
      }

      return (ClassSymbol)var2;
   }

   public PackageSymbol packge() {
      Symbol var1;
      for(var1 = this; var1.kind != 1; var1 = var1.owner) {
      }

      return (PackageSymbol)var1;
   }

   public boolean isSubClass(Symbol var1, Types var2) {
      throw new AssertionError("isSubClass " + this);
   }

   public boolean isMemberOf(TypeSymbol var1, Types var2) {
      return this.owner == var1 || var1.isSubClass(this.owner, var2) && this.isInheritedIn(var1, var2) && !this.hiddenIn((ClassSymbol)var1, var2);
   }

   public boolean isEnclosedBy(ClassSymbol var1) {
      for(Symbol var2 = this; var2.kind != 1; var2 = var2.owner) {
         if (var2 == var1) {
            return true;
         }
      }

      return false;
   }

   private boolean hiddenIn(ClassSymbol var1, Types var2) {
      Symbol var3 = this.hiddenInInternal(var1, var2);
      Assert.check(var3 != null, "the result of hiddenInInternal() can't be null");
      return var3 != this;
   }

   private Symbol hiddenInInternal(ClassSymbol var1, Types var2) {
      if (var1 == this.owner) {
         return this;
      } else {
         for(Scope.Entry var3 = var1.members().lookup(this.name); var3.scope != null; var3 = var3.next()) {
            if (var3.sym.kind == this.kind && (this.kind != 16 || (var3.sym.flags() & 8L) != 0L && var2.isSubSignature(var3.sym.type, this.type))) {
               return var3.sym;
            }
         }

         Symbol var4 = null;
         Iterator var5 = var2.interfaces(var1.type).prepend(var2.supertype(var1.type)).iterator();

         while(var5.hasNext()) {
            Type var6 = (Type)var5.next();
            if (var6 != null && var6.hasTag(TypeTag.CLASS)) {
               Symbol var7 = this.hiddenInInternal((ClassSymbol)var6.tsym, var2);
               if (var7 == this) {
                  return this;
               }

               if (var7 != null) {
                  var4 = var7;
               }
            }
         }

         return var4;
      }
   }

   public boolean isInheritedIn(Symbol var1, Types var2) {
      switch ((int)(this.flags_field & 7L)) {
         case 0:
            PackageSymbol var3 = this.packge();

            for(Object var4 = var1; var4 != null && var4 != this.owner; var4 = var2.supertype(((Symbol)var4).type).tsym) {
               while(((Symbol)var4).type.hasTag(TypeTag.TYPEVAR)) {
                  var4 = ((Symbol)var4).type.getUpperBound().tsym;
               }

               if (((Symbol)var4).type.isErroneous()) {
                  return true;
               }

               if ((((Symbol)var4).flags() & 16777216L) == 0L && ((Symbol)var4).packge() != var3) {
                  return false;
               }
            }

            return (var1.flags() & 512L) == 0L;
         case 1:
         case 3:
         default:
            return true;
         case 2:
            return this.owner == var1;
         case 4:
            return (var1.flags() & 512L) == 0L;
      }
   }

   public Symbol asMemberOf(Type var1, Types var2) {
      throw new AssertionError();
   }

   public boolean overrides(Symbol var1, TypeSymbol var2, Types var3, boolean var4) {
      return false;
   }

   public void complete() throws CompletionFailure {
      if (this.completer != null) {
         Completer var1 = this.completer;
         this.completer = null;
         var1.complete(this);
      }

   }

   public boolean exists() {
      return true;
   }

   public Type asType() {
      return this.type;
   }

   public Symbol getEnclosingElement() {
      return this.owner;
   }

   public ElementKind getKind() {
      return ElementKind.OTHER;
   }

   public Set getModifiers() {
      return Flags.asModifierSet(this.flags());
   }

   public Name getSimpleName() {
      return this.name;
   }

   public List getAnnotationMirrors() {
      return this.getRawAttributes();
   }

   public java.util.List getEnclosedElements() {
      return List.nil();
   }

   public List getTypeParameters() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.type.getTypeArguments().iterator();

      while(var2.hasNext()) {
         Type var3 = (Type)var2.next();
         Assert.check(var3.tsym.getKind() == ElementKind.TYPE_PARAMETER);
         var1.append((TypeVariableSymbol)var3.tsym);
      }

      return var1.toList();
   }

   public interface Visitor {
      Object visitClassSymbol(ClassSymbol var1, Object var2);

      Object visitMethodSymbol(MethodSymbol var1, Object var2);

      Object visitPackageSymbol(PackageSymbol var1, Object var2);

      Object visitOperatorSymbol(OperatorSymbol var1, Object var2);

      Object visitVarSymbol(VarSymbol var1, Object var2);

      Object visitTypeSymbol(TypeSymbol var1, Object var2);

      Object visitSymbol(Symbol var1, Object var2);
   }

   public static class CompletionFailure extends RuntimeException {
      private static final long serialVersionUID = 0L;
      public Symbol sym;
      public JCDiagnostic diag;
      /** @deprecated */
      @Deprecated
      public String errmsg;

      public CompletionFailure(Symbol var1, String var2) {
         this.sym = var1;
         this.errmsg = var2;
      }

      public CompletionFailure(Symbol var1, JCDiagnostic var2) {
         this.sym = var1;
         this.diag = var2;
      }

      public JCDiagnostic getDiagnostic() {
         return this.diag;
      }

      public String getMessage() {
         return this.diag != null ? this.diag.getMessage((Locale)null) : this.errmsg;
      }

      public Object getDetailValue() {
         return this.diag != null ? this.diag : this.errmsg;
      }

      public CompletionFailure initCause(Throwable var1) {
         super.initCause(var1);
         return this;
      }
   }

   public interface Completer {
      void complete(Symbol var1) throws CompletionFailure;
   }

   public static class OperatorSymbol extends MethodSymbol {
      public int opcode;

      public OperatorSymbol(Name var1, Type var2, int var3, Symbol var4) {
         super(9L, var1, var2, var4);
         this.opcode = var3;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitOperatorSymbol(this, var2);
      }
   }

   public static class DynamicMethodSymbol extends MethodSymbol {
      public Object[] staticArgs;
      public Symbol bsm;
      public int bsmKind;

      public DynamicMethodSymbol(Name var1, Symbol var2, int var3, MethodSymbol var4, Type var5, Object[] var6) {
         super(0L, var1, var5, var2);
         this.bsm = var4;
         this.bsmKind = var3;
         this.staticArgs = var6;
      }

      public boolean isDynamic() {
         return true;
      }
   }

   public static class MethodSymbol extends Symbol implements ExecutableElement {
      public Code code = null;
      public List extraParams = List.nil();
      public List capturedLocals = List.nil();
      public List params = null;
      public List savedParameterNames;
      public Attribute defaultValue = null;
      public static final Filter implementation_filter = new Filter() {
         public boolean accepts(Symbol var1) {
            return var1.kind == 16 && (var1.flags() & 4096L) == 0L;
         }
      };

      public MethodSymbol(long var1, Name var3, Type var4, Symbol var5) {
         super(16, var1, var3, var4, var5);
         if (var5.type.hasTag(TypeTag.TYPEVAR)) {
            Assert.error(var5 + "." + var3);
         }

      }

      public MethodSymbol clone(Symbol var1) {
         MethodSymbol var2 = new MethodSymbol(this.flags_field, this.name, this.type, var1) {
            public Symbol baseSymbol() {
               return MethodSymbol.this;
            }
         };
         var2.code = this.code;
         return var2;
      }

      public Set getModifiers() {
         long var1 = this.flags();
         return Flags.asModifierSet((var1 & 8796093022208L) != 0L ? var1 & -1025L : var1);
      }

      public String toString() {
         if ((this.flags() & 1048576L) != 0L) {
            return this.owner.name.toString();
         } else {
            String var1 = this.name == this.name.table.names.init ? this.owner.name.toString() : this.name.toString();
            if (this.type != null) {
               if (this.type.hasTag(TypeTag.FORALL)) {
                  var1 = "<" + ((Type.ForAll)this.type).getTypeArguments() + ">" + var1;
               }

               var1 = var1 + "(" + this.type.argtypes((this.flags() & 17179869184L) != 0L) + ")";
            }

            return var1;
         }
      }

      public boolean isDynamic() {
         return false;
      }

      public Symbol implemented(TypeSymbol var1, Types var2) {
         Symbol var3 = null;

         for(List var4 = var2.interfaces(var1.type); var3 == null && var4.nonEmpty(); var4 = var4.tail) {
            TypeSymbol var5 = ((Type)var4.head).tsym;
            var3 = this.implementedIn(var5, var2);
            if (var3 == null) {
               var3 = this.implemented(var5, var2);
            }
         }

         return var3;
      }

      public Symbol implementedIn(TypeSymbol var1, Types var2) {
         Symbol var3 = null;

         for(Scope.Entry var4 = var1.members().lookup(this.name); var3 == null && var4.scope != null; var4 = var4.next()) {
            if (this.overrides(var4.sym, (TypeSymbol)this.owner, var2, true) && var2.isSameType(this.type.getReturnType(), var2.memberType(this.owner.type, var4.sym).getReturnType())) {
               var3 = var4.sym;
            }
         }

         return var3;
      }

      public boolean binaryOverrides(Symbol var1, TypeSymbol var2, Types var3) {
         if (!this.isConstructor() && var1.kind == 16) {
            if (this == var1) {
               return true;
            } else {
               MethodSymbol var4 = (MethodSymbol)var1;
               if (var4.isOverridableIn((TypeSymbol)this.owner) && var3.asSuper(this.owner.type, var4.owner) != null && var3.isSameType(this.erasure(var3), var4.erasure(var3))) {
                  return true;
               } else {
                  return (this.flags() & 1024L) == 0L && var4.isOverridableIn(var2) && this.isMemberOf(var2, var3) && var3.isSameType(this.erasure(var3), var4.erasure(var3));
               }
            }
         } else {
            return false;
         }
      }

      public MethodSymbol binaryImplementation(ClassSymbol var1, Types var2) {
         for(Object var3 = var1; var3 != null; var3 = var2.supertype(((TypeSymbol)var3).type).tsym) {
            for(Scope.Entry var4 = ((TypeSymbol)var3).members().lookup(this.name); var4.scope != null; var4 = var4.next()) {
               if (var4.sym.kind == 16 && ((MethodSymbol)var4.sym).binaryOverrides(this, var1, var2)) {
                  return (MethodSymbol)var4.sym;
               }
            }
         }

         return null;
      }

      public boolean overrides(Symbol var1, TypeSymbol var2, Types var3, boolean var4) {
         if (!this.isConstructor() && var1.kind == 16) {
            if (this == var1) {
               return true;
            } else {
               MethodSymbol var5 = (MethodSymbol)var1;
               Type var6;
               Type var7;
               if (var5.isOverridableIn((TypeSymbol)this.owner) && var3.asSuper(this.owner.type, var5.owner) != null) {
                  var6 = var3.memberType(this.owner.type, this);
                  var7 = var3.memberType(this.owner.type, var5);
                  if (var3.isSubSignature(var6, var7)) {
                     if (!var4) {
                        return true;
                     }

                     if (var3.returnTypeSubstitutable(var6, var7)) {
                        return true;
                     }
                  }
               }

               if ((this.flags() & 1024L) == 0L && ((var5.flags() & 1024L) != 0L || (var5.flags() & 8796093022208L) != 0L) && var5.isOverridableIn(var2) && this.isMemberOf(var2, var3)) {
                  var6 = var3.memberType(var2.type, this);
                  var7 = var3.memberType(var2.type, var5);
                  return var3.isSubSignature(var6, var7) && (!var4 || var3.resultSubtype(var6, var7, var3.noWarnings));
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }

      private boolean isOverridableIn(TypeSymbol var1) {
         switch ((int)(this.flags_field & 7L)) {
            case 0:
               return this.packge() == var1.packge() && (var1.flags() & 512L) == 0L;
            case 1:
               return !this.owner.isInterface() || (this.flags_field & 8L) == 0L;
            case 2:
               return false;
            case 3:
            default:
               return false;
            case 4:
               return (var1.flags() & 512L) == 0L;
         }
      }

      public boolean isInheritedIn(Symbol var1, Types var2) {
         switch ((int)(this.flags_field & 7L)) {
            case 1:
               return !this.owner.isInterface() || var1 == this.owner || (this.flags_field & 8L) == 0L;
            default:
               return super.isInheritedIn(var1, var2);
         }
      }

      public MethodSymbol implementation(TypeSymbol var1, Types var2, boolean var3) {
         return this.implementation(var1, var2, var3, implementation_filter);
      }

      public MethodSymbol implementation(TypeSymbol var1, Types var2, boolean var3, Filter var4) {
         MethodSymbol var5 = var2.implementation(this, var1, var3, var4);
         if (var5 != null) {
            return var5;
         } else {
            return var2.isDerivedRaw(var1.type) && !var1.isInterface() ? this.implementation(var2.supertype(var1.type).tsym, var2, var3) : null;
         }
      }

      public List params() {
         this.owner.complete();
         if (this.params == null) {
            List var1 = this.savedParameterNames;
            this.savedParameterNames = null;
            if (var1 == null || var1.size() != this.type.getParameterTypes().size()) {
               var1 = List.nil();
            }

            ListBuffer var2 = new ListBuffer();
            List var3 = var1;
            int var4 = 0;

            for(Iterator var5 = this.type.getParameterTypes().iterator(); var5.hasNext(); ++var4) {
               Type var6 = (Type)var5.next();
               Name var7;
               if (var3.isEmpty()) {
                  var7 = this.createArgName(var4, var1);
               } else {
                  var7 = (Name)var3.head;
                  var3 = var3.tail;
                  if (var7.isEmpty()) {
                     var7 = this.createArgName(var4, var1);
                  }
               }

               var2.append(new VarSymbol(8589934592L, var7, var6, this));
            }

            this.params = var2.toList();
         }

         return this.params;
      }

      private Name createArgName(int var1, List var2) {
         String var3 = "arg";

         while(true) {
            Name var4 = this.name.table.fromString(var3 + var1);
            if (!var2.contains(var4)) {
               return var4;
            }

            var3 = var3 + "$";
         }
      }

      public Symbol asMemberOf(Type var1, Types var2) {
         return new MethodSymbol(this.flags_field, this.name, var2.memberType(var1, this), this.owner);
      }

      public ElementKind getKind() {
         if (this.name == this.name.table.names.init) {
            return ElementKind.CONSTRUCTOR;
         } else if (this.name == this.name.table.names.clinit) {
            return ElementKind.STATIC_INIT;
         } else if ((this.flags() & 1048576L) != 0L) {
            return this.isStatic() ? ElementKind.STATIC_INIT : ElementKind.INSTANCE_INIT;
         } else {
            return ElementKind.METHOD;
         }
      }

      public boolean isStaticOrInstanceInit() {
         return this.getKind() == ElementKind.STATIC_INIT || this.getKind() == ElementKind.INSTANCE_INIT;
      }

      public Attribute getDefaultValue() {
         return this.defaultValue;
      }

      public List getParameters() {
         return this.params();
      }

      public boolean isVarArgs() {
         return (this.flags() & 17179869184L) != 0L;
      }

      public boolean isDefault() {
         return (this.flags() & 8796093022208L) != 0L;
      }

      public Object accept(ElementVisitor var1, Object var2) {
         return var1.visitExecutable(this, var2);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitMethodSymbol(this, var2);
      }

      public Type getReceiverType() {
         return this.asType().getReceiverType();
      }

      public Type getReturnType() {
         return this.asType().getReturnType();
      }

      public List getThrownTypes() {
         return this.asType().getThrownTypes();
      }
   }

   public static class VarSymbol extends Symbol implements VariableElement {
      public int pos = -1;
      public int adr = -1;
      private Object data;

      public VarSymbol(long var1, Name var3, Type var4, Symbol var5) {
         super(4, var1, var3, var4, var5);
      }

      public VarSymbol clone(Symbol var1) {
         VarSymbol var2 = new VarSymbol(this.flags_field, this.name, this.type, var1) {
            public Symbol baseSymbol() {
               return VarSymbol.this;
            }
         };
         var2.pos = this.pos;
         var2.adr = this.adr;
         var2.data = this.data;
         return var2;
      }

      public String toString() {
         return this.name.toString();
      }

      public Symbol asMemberOf(Type var1, Types var2) {
         return new VarSymbol(this.flags_field, this.name, var2.memberType(var1, this), this.owner);
      }

      public ElementKind getKind() {
         long var1 = this.flags();
         if ((var1 & 8589934592L) != 0L) {
            return this.isExceptionParameter() ? ElementKind.EXCEPTION_PARAMETER : ElementKind.PARAMETER;
         } else if ((var1 & 16384L) != 0L) {
            return ElementKind.ENUM_CONSTANT;
         } else if (this.owner.kind != 2 && this.owner.kind != 63) {
            return this.isResourceVariable() ? ElementKind.RESOURCE_VARIABLE : ElementKind.LOCAL_VARIABLE;
         } else {
            return ElementKind.FIELD;
         }
      }

      public Object accept(ElementVisitor var1, Object var2) {
         return var1.visitVariable(this, var2);
      }

      public Object getConstantValue() {
         return Constants.decode(this.getConstValue(), this.type);
      }

      public void setLazyConstValue(final Env var1, final Attr var2, final JCTree.JCVariableDecl var3) {
         this.setData(new Callable() {
            public Object call() {
               return var2.attribLazyConstantValue(var1, var3, VarSymbol.this.type);
            }
         });
      }

      public boolean isExceptionParameter() {
         return this.data == ElementKind.EXCEPTION_PARAMETER;
      }

      public boolean isResourceVariable() {
         return this.data == ElementKind.RESOURCE_VARIABLE;
      }

      public Object getConstValue() {
         if (this.data != ElementKind.EXCEPTION_PARAMETER && this.data != ElementKind.RESOURCE_VARIABLE) {
            if (this.data instanceof Callable) {
               Callable var1 = (Callable)this.data;
               this.data = null;

               try {
                  this.data = var1.call();
               } catch (Exception var3) {
                  throw new AssertionError(var3);
               }
            }

            return this.data;
         } else {
            return null;
         }
      }

      public void setData(Object var1) {
         Assert.check(!(var1 instanceof Env), (Object)this);
         this.data = var1;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitVarSymbol(this, var2);
      }
   }

   public static class ClassSymbol extends TypeSymbol implements TypeElement {
      public Scope members_field;
      public Name fullname;
      public Name flatname;
      public JavaFileObject sourcefile;
      public JavaFileObject classfile;
      public List trans_local;
      public Pool pool;

      public ClassSymbol(long var1, Name var3, Type var4, Symbol var5) {
         super(2, var1, var3, var4, var5);
         this.members_field = null;
         this.fullname = formFullName(var3, var5);
         this.flatname = formFlatName(var3, var5);
         this.sourcefile = null;
         this.classfile = null;
         this.pool = null;
      }

      public ClassSymbol(long var1, Name var3, Symbol var4) {
         this(var1, var3, new Type.ClassType(Type.noType, (List)null, (TypeSymbol)null), var4);
         this.type.tsym = this;
      }

      public String toString() {
         return this.className();
      }

      public long flags() {
         if (this.completer != null) {
            this.complete();
         }

         return this.flags_field;
      }

      public Scope members() {
         if (this.completer != null) {
            this.complete();
         }

         return this.members_field;
      }

      public List getRawAttributes() {
         if (this.completer != null) {
            this.complete();
         }

         return super.getRawAttributes();
      }

      public List getRawTypeAttributes() {
         if (this.completer != null) {
            this.complete();
         }

         return super.getRawTypeAttributes();
      }

      public Type erasure(Types var1) {
         if (this.erasure_field == null) {
            this.erasure_field = new Type.ClassType(var1.erasure(this.type.getEnclosingType()), List.nil(), this);
         }

         return this.erasure_field;
      }

      public String className() {
         return this.name.isEmpty() ? Log.getLocalizedString("anonymous.class", this.flatname) : this.fullname.toString();
      }

      public Name getQualifiedName() {
         return this.fullname;
      }

      public Name flatName() {
         return this.flatname;
      }

      public boolean isSubClass(Symbol var1, Types var2) {
         if (this == var1) {
            return true;
         } else {
            Type var3;
            if ((var1.flags() & 512L) != 0L) {
               for(var3 = this.type; var3.hasTag(TypeTag.CLASS); var3 = var2.supertype(var3)) {
                  for(List var4 = var2.interfaces(var3); var4.nonEmpty(); var4 = var4.tail) {
                     if (((Type)var4.head).tsym.isSubClass(var1, var2)) {
                        return true;
                     }
                  }
               }
            } else {
               for(var3 = this.type; var3.hasTag(TypeTag.CLASS); var3 = var2.supertype(var3)) {
                  if (var3.tsym == var1) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public void complete() throws CompletionFailure {
         try {
            super.complete();
         } catch (CompletionFailure var2) {
            this.flags_field |= 9L;
            this.type = new Type.ErrorType(this, Type.noType);
            throw var2;
         }
      }

      public List getInterfaces() {
         this.complete();
         if (this.type instanceof Type.ClassType) {
            Type.ClassType var1 = (Type.ClassType)this.type;
            if (var1.interfaces_field == null) {
               var1.interfaces_field = List.nil();
            }

            return var1.all_interfaces_field != null ? Type.getModelTypes(var1.all_interfaces_field) : var1.interfaces_field;
         } else {
            return List.nil();
         }
      }

      public Type getSuperclass() {
         this.complete();
         if (this.type instanceof Type.ClassType) {
            Type.ClassType var1 = (Type.ClassType)this.type;
            if (var1.supertype_field == null) {
               var1.supertype_field = Type.noType;
            }

            return (Type)(var1.isInterface() ? Type.noType : var1.supertype_field.getModelType());
         } else {
            return Type.noType;
         }
      }

      private ClassSymbol getSuperClassToSearchForAnnotations() {
         Type var1 = this.getSuperclass();
         return var1.hasTag(TypeTag.CLASS) && !var1.isErroneous() ? (ClassSymbol)var1.tsym : null;
      }

      protected Annotation[] getInheritedAnnotations(Class var1) {
         ClassSymbol var2 = this.getSuperClassToSearchForAnnotations();
         return var2 == null ? super.getInheritedAnnotations(var1) : var2.getAnnotationsByType(var1);
      }

      public ElementKind getKind() {
         long var1 = this.flags();
         if ((var1 & 8192L) != 0L) {
            return ElementKind.ANNOTATION_TYPE;
         } else if ((var1 & 512L) != 0L) {
            return ElementKind.INTERFACE;
         } else {
            return (var1 & 16384L) != 0L ? ElementKind.ENUM : ElementKind.CLASS;
         }
      }

      public Set getModifiers() {
         long var1 = this.flags();
         return Flags.asModifierSet(var1 & -8796093022209L);
      }

      public NestingKind getNestingKind() {
         this.complete();
         if (this.owner.kind == 1) {
            return NestingKind.TOP_LEVEL;
         } else if (this.name.isEmpty()) {
            return NestingKind.ANONYMOUS;
         } else {
            return this.owner.kind == 16 ? NestingKind.LOCAL : NestingKind.MEMBER;
         }
      }

      protected Attribute.Compound getAttribute(Class var1) {
         Attribute.Compound var2 = super.getAttribute(var1);
         boolean var3 = var1.isAnnotationPresent(Inherited.class);
         if (var2 == null && var3) {
            ClassSymbol var4 = this.getSuperClassToSearchForAnnotations();
            return var4 == null ? null : var4.getAttribute(var1);
         } else {
            return var2;
         }
      }

      public Object accept(ElementVisitor var1, Object var2) {
         return var1.visitType(this, var2);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitClassSymbol(this, var2);
      }

      public void markAbstractIfNeeded(Types var1) {
         if (var1.enter.getEnv(this) != null && (this.flags() & 16384L) != 0L && var1.supertype(this.type).tsym == var1.syms.enumSym && (this.flags() & 1040L) == 0L && var1.firstUnimplementedAbstract(this) != null) {
            this.flags_field |= 1024L;
         }

      }
   }

   public static class PackageSymbol extends TypeSymbol implements PackageElement {
      public Scope members_field;
      public Name fullname;
      public ClassSymbol package_info;

      public PackageSymbol(Name var1, Type var2, Symbol var3) {
         super(1, 0L, var1, var2, var3);
         this.members_field = null;
         this.fullname = formFullName(var1, var3);
      }

      public PackageSymbol(Name var1, Symbol var2) {
         this(var1, (Type)null, var2);
         this.type = new Type.PackageType(this);
      }

      public String toString() {
         return this.fullname.toString();
      }

      public Name getQualifiedName() {
         return this.fullname;
      }

      public boolean isUnnamed() {
         return this.name.isEmpty() && this.owner != null;
      }

      public Scope members() {
         if (this.completer != null) {
            this.complete();
         }

         return this.members_field;
      }

      public long flags() {
         if (this.completer != null) {
            this.complete();
         }

         return this.flags_field;
      }

      public List getRawAttributes() {
         if (this.completer != null) {
            this.complete();
         }

         if (this.package_info != null && this.package_info.completer != null) {
            this.package_info.complete();
            this.mergeAttributes();
         }

         return super.getRawAttributes();
      }

      private void mergeAttributes() {
         if (this.metadata == null && this.package_info.metadata != null) {
            this.metadata = new SymbolMetadata(this);
            this.metadata.setAttributes(this.package_info.metadata);
         }

      }

      public boolean exists() {
         return (this.flags_field & 8388608L) != 0L;
      }

      public ElementKind getKind() {
         return ElementKind.PACKAGE;
      }

      public Symbol getEnclosingElement() {
         return null;
      }

      public Object accept(ElementVisitor var1, Object var2) {
         return var1.visitPackage(this, var2);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitPackageSymbol(this, var2);
      }
   }

   public static class TypeVariableSymbol extends TypeSymbol implements TypeParameterElement {
      public TypeVariableSymbol(long var1, Name var3, Type var4, Symbol var5) {
         super(2, var1, var3, var4, var5);
      }

      public ElementKind getKind() {
         return ElementKind.TYPE_PARAMETER;
      }

      public Symbol getGenericElement() {
         return this.owner;
      }

      public List getBounds() {
         Type.TypeVar var1 = (Type.TypeVar)this.type;
         Type var2 = var1.getUpperBound();
         if (!var2.isCompound()) {
            return List.of(var2);
         } else {
            Type.ClassType var3 = (Type.ClassType)var2;
            return !var3.tsym.erasure_field.isInterface() ? var3.interfaces_field.prepend(var3.supertype_field) : var3.interfaces_field;
         }
      }

      public List getAnnotationMirrors() {
         List var1 = this.owner.getRawTypeAttributes();
         int var2 = this.owner.getTypeParameters().indexOf(this);
         List var3 = List.nil();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Attribute.TypeCompound var5 = (Attribute.TypeCompound)var4.next();
            if (this.isCurrentSymbolsAnnotation(var5, var2)) {
               var3 = var3.prepend(var5);
            }
         }

         return var3.reverse();
      }

      public Attribute.Compound getAttribute(Class var1) {
         String var2 = var1.getName();
         List var3 = this.owner.getRawTypeAttributes();
         int var4 = this.owner.getTypeParameters().indexOf(this);
         Iterator var5 = var3.iterator();

         Attribute.TypeCompound var6;
         do {
            if (!var5.hasNext()) {
               return null;
            }

            var6 = (Attribute.TypeCompound)var5.next();
         } while(!this.isCurrentSymbolsAnnotation(var6, var4) || !var2.contentEquals(var6.type.tsym.flatName()));

         return var6;
      }

      boolean isCurrentSymbolsAnnotation(Attribute.TypeCompound var1, int var2) {
         return (var1.position.type == TargetType.CLASS_TYPE_PARAMETER || var1.position.type == TargetType.METHOD_TYPE_PARAMETER) && var1.position.parameter_index == var2;
      }

      public Object accept(ElementVisitor var1, Object var2) {
         return var1.visitTypeParameter(this, var2);
      }
   }

   public abstract static class TypeSymbol extends Symbol {
      public TypeSymbol(int var1, long var2, Name var4, Type var5, Symbol var6) {
         super(var1, var2, var4, var5, var6);
      }

      public static Name formFullName(Name var0, Symbol var1) {
         if (var1 == null) {
            return var0;
         } else if (var1.kind != 63 && ((var1.kind & 20) != 0 || var1.kind == 2 && var1.type.hasTag(TypeTag.TYPEVAR))) {
            return var0;
         } else {
            Name var2 = var1.getQualifiedName();
            return var2 != null && var2 != var2.table.names.empty ? var2.append('.', var0) : var0;
         }
      }

      public static Name formFlatName(Name var0, Symbol var1) {
         if (var1 == null || (var1.kind & 20) != 0 || var1.kind == 2 && var1.type.hasTag(TypeTag.TYPEVAR)) {
            return var0;
         } else {
            int var2 = var1.kind == 2 ? 36 : 46;
            Name var3 = var1.flatName();
            return var3 != null && var3 != var3.table.names.empty ? var3.append((char)var2, var0) : var0;
         }
      }

      public final boolean precedes(TypeSymbol var1, Types var2) {
         if (this == var1) {
            return false;
         } else {
            if (this.type.hasTag(var1.type.getTag())) {
               if (this.type.hasTag(TypeTag.CLASS)) {
                  return var2.rank(var1.type) < var2.rank(this.type) || var2.rank(var1.type) == var2.rank(this.type) && var1.getQualifiedName().compareTo(this.getQualifiedName()) < 0;
               }

               if (this.type.hasTag(TypeTag.TYPEVAR)) {
                  return var2.isSubtype(this.type, var1.type);
               }
            }

            return this.type.hasTag(TypeTag.TYPEVAR);
         }
      }

      public java.util.List getEnclosedElements() {
         List var1 = List.nil();
         if (this.kind == 2 && this.type.hasTag(TypeTag.TYPEVAR)) {
            return var1;
         } else {
            for(Scope.Entry var2 = this.members().elems; var2 != null; var2 = var2.sibling) {
               if (var2.sym != null && (var2.sym.flags() & 4096L) == 0L && var2.sym.owner == this) {
                  var1 = var1.prepend(var2.sym);
               }
            }

            return var1;
         }
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitTypeSymbol(this, var2);
      }
   }

   public static class DelegatedSymbol extends Symbol {
      protected Symbol other;

      public DelegatedSymbol(Symbol var1) {
         super(var1.kind, var1.flags_field, var1.name, var1.type, var1.owner);
         this.other = var1;
      }

      public String toString() {
         return this.other.toString();
      }

      public Symbol location() {
         return this.other.location();
      }

      public Symbol location(Type var1, Types var2) {
         return this.other.location(var1, var2);
      }

      public Symbol baseSymbol() {
         return this.other;
      }

      public Type erasure(Types var1) {
         return this.other.erasure(var1);
      }

      public Type externalType(Types var1) {
         return this.other.externalType(var1);
      }

      public boolean isLocal() {
         return this.other.isLocal();
      }

      public boolean isConstructor() {
         return this.other.isConstructor();
      }

      public Name getQualifiedName() {
         return this.other.getQualifiedName();
      }

      public Name flatName() {
         return this.other.flatName();
      }

      public Scope members() {
         return this.other.members();
      }

      public boolean isInner() {
         return this.other.isInner();
      }

      public boolean hasOuterInstance() {
         return this.other.hasOuterInstance();
      }

      public ClassSymbol enclClass() {
         return this.other.enclClass();
      }

      public ClassSymbol outermostClass() {
         return this.other.outermostClass();
      }

      public PackageSymbol packge() {
         return this.other.packge();
      }

      public boolean isSubClass(Symbol var1, Types var2) {
         return this.other.isSubClass(var1, var2);
      }

      public boolean isMemberOf(TypeSymbol var1, Types var2) {
         return this.other.isMemberOf(var1, var2);
      }

      public boolean isEnclosedBy(ClassSymbol var1) {
         return this.other.isEnclosedBy(var1);
      }

      public boolean isInheritedIn(Symbol var1, Types var2) {
         return this.other.isInheritedIn(var1, var2);
      }

      public Symbol asMemberOf(Type var1, Types var2) {
         return this.other.asMemberOf(var1, var2);
      }

      public void complete() throws CompletionFailure {
         this.other.complete();
      }

      public Object accept(ElementVisitor var1, Object var2) {
         return this.other.accept(var1, var2);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitSymbol(this.other, var2);
      }

      public Symbol getUnderlyingSymbol() {
         return this.other;
      }
   }
}
