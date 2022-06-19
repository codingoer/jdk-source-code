package com.sun.tools.javac.code;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;

public abstract class Type extends AnnoConstruct implements TypeMirror {
   public static final JCNoType noType = new JCNoType() {
      public String toString() {
         return "none";
      }
   };
   public static final JCNoType recoveryType = new JCNoType() {
      public String toString() {
         return "recovery";
      }
   };
   public static final JCNoType stuckType = new JCNoType() {
      public String toString() {
         return "stuck";
      }
   };
   public static boolean moreInfo = false;
   public Symbol.TypeSymbol tsym;

   public boolean hasTag(TypeTag var1) {
      return var1 == this.getTag();
   }

   public abstract TypeTag getTag();

   public boolean isNumeric() {
      return false;
   }

   public boolean isPrimitive() {
      return false;
   }

   public boolean isPrimitiveOrVoid() {
      return false;
   }

   public boolean isReference() {
      return false;
   }

   public boolean isNullOrReference() {
      return false;
   }

   public boolean isPartial() {
      return false;
   }

   public Object constValue() {
      return null;
   }

   public boolean isFalse() {
      return false;
   }

   public boolean isTrue() {
      return false;
   }

   public Type getModelType() {
      return this;
   }

   public static List getModelTypes(List var0) {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Type var3 = (Type)var2.next();
         var1.append(var3.getModelType());
      }

      return var1.toList();
   }

   public Type getOriginalType() {
      return this;
   }

   public Object accept(Visitor var1, Object var2) {
      return var1.visitType(this, var2);
   }

   public Type(Symbol.TypeSymbol var1) {
      this.tsym = var1;
   }

   public Type map(Mapping var1) {
      return this;
   }

   public static List map(List var0, Mapping var1) {
      if (var0.nonEmpty()) {
         List var2 = map(var0.tail, var1);
         Type var3 = var1.apply((Type)var0.head);
         if (var2 != var0.tail || var3 != var0.head) {
            return var2.prepend(var3);
         }
      }

      return var0;
   }

   public Type constType(Object var1) {
      throw new AssertionError();
   }

   public Type baseType() {
      return this;
   }

   public Type annotatedType(List var1) {
      return new AnnotatedType(var1, this);
   }

   public boolean isAnnotated() {
      return false;
   }

   public Type unannotatedType() {
      return this;
   }

   public List getAnnotationMirrors() {
      return List.nil();
   }

   public Annotation getAnnotation(Class var1) {
      return null;
   }

   public Annotation[] getAnnotationsByType(Class var1) {
      Annotation[] var2 = (Annotation[])((Annotation[])Array.newInstance(var1, 0));
      return var2;
   }

   public static List baseTypes(List var0) {
      if (var0.nonEmpty()) {
         Type var1 = ((Type)var0.head).baseType();
         List var2 = baseTypes(var0.tail);
         if (var1 != var0.head || var2 != var0.tail) {
            return var2.prepend(var1);
         }
      }

      return var0;
   }

   public String toString() {
      String var1 = this.tsym != null && this.tsym.name != null ? this.tsym.name.toString() : "<none>";
      if (moreInfo && this.hasTag(TypeTag.TYPEVAR)) {
         var1 = var1 + this.hashCode();
      }

      return var1;
   }

   public static String toString(List var0) {
      if (var0.isEmpty()) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append(((Type)var0.head).toString());

         for(List var2 = var0.tail; var2.nonEmpty(); var2 = var2.tail) {
            var1.append(",").append(((Type)var2.head).toString());
         }

         return var1.toString();
      }
   }

   public String stringValue() {
      Object var1 = Assert.checkNonNull(this.constValue());
      return var1.toString();
   }

   public boolean equals(Object var1) {
      return super.equals(var1);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String argtypes(boolean var1) {
      List var2 = this.getParameterTypes();
      if (!var1) {
         return var2.toString();
      } else {
         StringBuilder var3 = new StringBuilder();

         while(var2.tail.nonEmpty()) {
            var3.append(var2.head);
            var2 = var2.tail;
            var3.append(',');
         }

         if (((Type)var2.head).unannotatedType().hasTag(TypeTag.ARRAY)) {
            var3.append(((ArrayType)((Type)var2.head).unannotatedType()).elemtype);
            if (((Type)var2.head).getAnnotationMirrors().nonEmpty()) {
               var3.append(((Type)var2.head).getAnnotationMirrors());
            }

            var3.append("...");
         } else {
            var3.append(var2.head);
         }

         return var3.toString();
      }
   }

   public List getTypeArguments() {
      return List.nil();
   }

   public Type getEnclosingType() {
      return null;
   }

   public List getParameterTypes() {
      return List.nil();
   }

   public Type getReturnType() {
      return null;
   }

   public Type getReceiverType() {
      return null;
   }

   public List getThrownTypes() {
      return List.nil();
   }

   public Type getUpperBound() {
      return null;
   }

   public Type getLowerBound() {
      return null;
   }

   public List allparams() {
      return List.nil();
   }

   public boolean isErroneous() {
      return false;
   }

   public static boolean isErroneous(List var0) {
      for(List var1 = var0; var1.nonEmpty(); var1 = var1.tail) {
         if (((Type)var1.head).isErroneous()) {
            return true;
         }
      }

      return false;
   }

   public boolean isParameterized() {
      return false;
   }

   public boolean isRaw() {
      return false;
   }

   public boolean isCompound() {
      return this.tsym.completer == null && (this.tsym.flags() & 16777216L) != 0L;
   }

   public boolean isIntersection() {
      return false;
   }

   public boolean isUnion() {
      return false;
   }

   public boolean isInterface() {
      return (this.tsym.flags() & 512L) != 0L;
   }

   public boolean isFinal() {
      return (this.tsym.flags() & 16L) != 0L;
   }

   public boolean contains(Type var1) {
      return var1 == this;
   }

   public static boolean contains(List var0, Type var1) {
      for(List var2 = var0; var2.tail != null; var2 = var2.tail) {
         if (((Type)var2.head).contains(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsAny(List var1) {
      Iterator var2 = var1.iterator();

      Type var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Type)var2.next();
      } while(!this.contains(var3));

      return true;
   }

   public static boolean containsAny(List var0, List var1) {
      Iterator var2 = var0.iterator();

      Type var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Type)var2.next();
      } while(!var3.containsAny(var1));

      return true;
   }

   public static List filter(List var0, Filter var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         Type var4 = (Type)var3.next();
         if (var1.accepts(var4)) {
            var2.append(var4);
         }
      }

      return var2.toList();
   }

   public boolean isSuperBound() {
      return false;
   }

   public boolean isExtendsBound() {
      return false;
   }

   public boolean isUnbound() {
      return false;
   }

   public Type withTypeVar(Type var1) {
      return this;
   }

   public MethodType asMethodType() {
      throw new AssertionError();
   }

   public void complete() {
   }

   public Symbol.TypeSymbol asElement() {
      return this.tsym;
   }

   public TypeKind getKind() {
      return TypeKind.OTHER;
   }

   public Object accept(TypeVisitor var1, Object var2) {
      throw new AssertionError();
   }

   public interface Visitor {
      Object visitClassType(ClassType var1, Object var2);

      Object visitWildcardType(WildcardType var1, Object var2);

      Object visitArrayType(ArrayType var1, Object var2);

      Object visitMethodType(MethodType var1, Object var2);

      Object visitPackageType(PackageType var1, Object var2);

      Object visitTypeVar(TypeVar var1, Object var2);

      Object visitCapturedType(CapturedType var1, Object var2);

      Object visitForAll(ForAll var1, Object var2);

      Object visitUndetVar(UndetVar var1, Object var2);

      Object visitErrorType(ErrorType var1, Object var2);

      Object visitAnnotatedType(AnnotatedType var1, Object var2);

      Object visitType(Type var1, Object var2);
   }

   public static class UnknownType extends Type {
      public UnknownType() {
         super((Symbol.TypeSymbol)null);
      }

      public TypeTag getTag() {
         return TypeTag.UNKNOWN;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitUnknown(this, var2);
      }

      public boolean isPartial() {
         return true;
      }
   }

   public static class AnnotatedType extends Type implements javax.lang.model.type.ArrayType, DeclaredType, PrimitiveType, TypeVariable, javax.lang.model.type.WildcardType {
      private List typeAnnotations;
      private Type underlyingType;

      protected AnnotatedType(List var1, Type var2) {
         super(var2.tsym);
         this.typeAnnotations = var1;
         this.underlyingType = var2;
         Assert.check(var1 != null && var1.nonEmpty(), "Can't create AnnotatedType without annotations: " + var2);
         Assert.check(!var2.isAnnotated(), "Can't annotate already annotated type: " + var2 + "; adding: " + var1);
      }

      public TypeTag getTag() {
         return this.underlyingType.getTag();
      }

      public boolean isAnnotated() {
         return true;
      }

      public List getAnnotationMirrors() {
         return this.typeAnnotations;
      }

      public TypeKind getKind() {
         return this.underlyingType.getKind();
      }

      public Type unannotatedType() {
         return this.underlyingType;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitAnnotatedType(this, var2);
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return this.underlyingType.accept(var1, var2);
      }

      public Type map(Mapping var1) {
         this.underlyingType.map(var1);
         return this;
      }

      public Type constType(Object var1) {
         return this.underlyingType.constType(var1);
      }

      public Type getEnclosingType() {
         return this.underlyingType.getEnclosingType();
      }

      public Type getReturnType() {
         return this.underlyingType.getReturnType();
      }

      public List getTypeArguments() {
         return this.underlyingType.getTypeArguments();
      }

      public List getParameterTypes() {
         return this.underlyingType.getParameterTypes();
      }

      public Type getReceiverType() {
         return this.underlyingType.getReceiverType();
      }

      public List getThrownTypes() {
         return this.underlyingType.getThrownTypes();
      }

      public Type getUpperBound() {
         return this.underlyingType.getUpperBound();
      }

      public Type getLowerBound() {
         return this.underlyingType.getLowerBound();
      }

      public boolean isErroneous() {
         return this.underlyingType.isErroneous();
      }

      public boolean isCompound() {
         return this.underlyingType.isCompound();
      }

      public boolean isInterface() {
         return this.underlyingType.isInterface();
      }

      public List allparams() {
         return this.underlyingType.allparams();
      }

      public boolean isPrimitive() {
         return this.underlyingType.isPrimitive();
      }

      public boolean isPrimitiveOrVoid() {
         return this.underlyingType.isPrimitiveOrVoid();
      }

      public boolean isNumeric() {
         return this.underlyingType.isNumeric();
      }

      public boolean isReference() {
         return this.underlyingType.isReference();
      }

      public boolean isNullOrReference() {
         return this.underlyingType.isNullOrReference();
      }

      public boolean isPartial() {
         return this.underlyingType.isPartial();
      }

      public boolean isParameterized() {
         return this.underlyingType.isParameterized();
      }

      public boolean isRaw() {
         return this.underlyingType.isRaw();
      }

      public boolean isFinal() {
         return this.underlyingType.isFinal();
      }

      public boolean isSuperBound() {
         return this.underlyingType.isSuperBound();
      }

      public boolean isExtendsBound() {
         return this.underlyingType.isExtendsBound();
      }

      public boolean isUnbound() {
         return this.underlyingType.isUnbound();
      }

      public String toString() {
         return this.typeAnnotations != null && !this.typeAnnotations.isEmpty() ? "(" + this.typeAnnotations.toString() + " :: " + this.underlyingType.toString() + ")" : "({} :: " + this.underlyingType.toString() + ")";
      }

      public boolean contains(Type var1) {
         return this.underlyingType.contains(var1);
      }

      public Type withTypeVar(Type var1) {
         this.underlyingType = this.underlyingType.withTypeVar(var1);
         return this;
      }

      public Symbol.TypeSymbol asElement() {
         return this.underlyingType.asElement();
      }

      public MethodType asMethodType() {
         return this.underlyingType.asMethodType();
      }

      public void complete() {
         this.underlyingType.complete();
      }

      public TypeMirror getComponentType() {
         return ((ArrayType)this.underlyingType).getComponentType();
      }

      public Type makeVarargs() {
         return ((ArrayType)this.underlyingType).makeVarargs().annotatedType(this.typeAnnotations);
      }

      public TypeMirror getExtendsBound() {
         return ((WildcardType)this.underlyingType).getExtendsBound();
      }

      public TypeMirror getSuperBound() {
         return ((WildcardType)this.underlyingType).getSuperBound();
      }
   }

   public static class ErrorType extends ClassType implements javax.lang.model.type.ErrorType {
      private Type originalType;

      public ErrorType(Type var1, Symbol.TypeSymbol var2) {
         super(noType, List.nil(), (Symbol.TypeSymbol)null);
         this.originalType = null;
         this.tsym = var2;
         this.originalType = (Type)(var1 == null ? noType : var1);
      }

      public ErrorType(Symbol.ClassSymbol var1, Type var2) {
         this((Type)var2, (Symbol.TypeSymbol)var1);
         var1.type = this;
         var1.kind = 63;
         var1.members_field = new Scope.ErrorScope(var1);
      }

      public TypeTag getTag() {
         return TypeTag.ERROR;
      }

      public boolean isPartial() {
         return true;
      }

      public boolean isReference() {
         return true;
      }

      public boolean isNullOrReference() {
         return true;
      }

      public ErrorType(Name var1, Symbol.TypeSymbol var2, Type var3) {
         this(new Symbol.ClassSymbol(1073741833L, var1, (Type)null, var2), var3);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitErrorType(this, var2);
      }

      public Type constType(Object var1) {
         return this;
      }

      public Type getEnclosingType() {
         return this;
      }

      public Type getReturnType() {
         return this;
      }

      public Type asSub(Symbol var1) {
         return this;
      }

      public Type map(Mapping var1) {
         return this;
      }

      public boolean isGenType(Type var1) {
         return true;
      }

      public boolean isErroneous() {
         return true;
      }

      public boolean isCompound() {
         return false;
      }

      public boolean isInterface() {
         return false;
      }

      public List allparams() {
         return List.nil();
      }

      public List getTypeArguments() {
         return List.nil();
      }

      public TypeKind getKind() {
         return TypeKind.ERROR;
      }

      public Type getOriginalType() {
         return this.originalType;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitError(this, var2);
      }
   }

   static class BottomType extends Type implements NullType {
      public BottomType() {
         super((Symbol.TypeSymbol)null);
      }

      public TypeTag getTag() {
         return TypeTag.BOT;
      }

      public TypeKind getKind() {
         return TypeKind.NULL;
      }

      public boolean isCompound() {
         return false;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitNull(this, var2);
      }

      public Type constType(Object var1) {
         return this;
      }

      public String stringValue() {
         return "null";
      }

      public boolean isNullOrReference() {
         return true;
      }
   }

   public static class JCVoidType extends Type implements NoType {
      public JCVoidType() {
         super((Symbol.TypeSymbol)null);
      }

      public TypeTag getTag() {
         return TypeTag.VOID;
      }

      public TypeKind getKind() {
         return TypeKind.VOID;
      }

      public boolean isCompound() {
         return false;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitNoType(this, var2);
      }

      public boolean isPrimitiveOrVoid() {
         return true;
      }
   }

   public static class JCNoType extends Type implements NoType {
      public JCNoType() {
         super((Symbol.TypeSymbol)null);
      }

      public TypeTag getTag() {
         return TypeTag.NONE;
      }

      public TypeKind getKind() {
         return TypeKind.NONE;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitNoType(this, var2);
      }

      public boolean isCompound() {
         return false;
      }
   }

   public static class CapturedUndetVar extends UndetVar {
      public CapturedUndetVar(CapturedType var1, Types var2) {
         super(var1, var2);
         if (!var1.lower.hasTag(TypeTag.BOT)) {
            this.bounds.put(Type.UndetVar.InferenceBound.LOWER, List.of(var1.lower));
         }

      }

      public void addBound(UndetVar.InferenceBound var1, Type var2, Types var3, boolean var4) {
         if (var4) {
            super.addBound(var1, var2, var3, var4);
         }

      }

      public boolean isCaptured() {
         return true;
      }
   }

   public static class UndetVar extends DelegatedType {
      protected Map bounds = new EnumMap(InferenceBound.class);
      public Type inst = null;
      public int declaredCount;
      public UndetVarListener listener = null;
      Mapping toTypeVarMap = new Mapping("toTypeVarMap") {
         public Type apply(Type var1) {
            if (var1.hasTag(TypeTag.UNDETVAR)) {
               UndetVar var2 = (UndetVar)var1;
               return var2.inst != null ? var2.inst : var2.qtype;
            } else {
               return var1.map(this);
            }
         }
      };

      public Object accept(Visitor var1, Object var2) {
         return var1.visitUndetVar(this, var2);
      }

      public UndetVar(TypeVar var1, Types var2) {
         super(TypeTag.UNDETVAR, var1);
         List var3 = var2.getBounds(var1);
         this.declaredCount = var3.length();
         this.bounds.put(Type.UndetVar.InferenceBound.UPPER, var3);
         this.bounds.put(Type.UndetVar.InferenceBound.LOWER, List.nil());
         this.bounds.put(Type.UndetVar.InferenceBound.EQ, List.nil());
      }

      public String toString() {
         return this.inst == null ? this.qtype + "?" : this.inst.toString();
      }

      public String debugString() {
         String var1 = "inference var = " + this.qtype + "\n";
         if (this.inst != null) {
            var1 = var1 + "inst = " + this.inst + '\n';
         }

         InferenceBound[] var2 = Type.UndetVar.InferenceBound.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            InferenceBound var5 = var2[var4];
            List var6 = (List)this.bounds.get(var5);
            if (var6.size() > 0) {
               var1 = var1 + var5 + " = " + var6 + '\n';
            }
         }

         return var1;
      }

      public boolean isPartial() {
         return true;
      }

      public Type baseType() {
         return (Type)(this.inst == null ? this : this.inst.baseType());
      }

      public List getBounds(InferenceBound... var1) {
         ListBuffer var2 = new ListBuffer();
         InferenceBound[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            InferenceBound var6 = var3[var5];
            var2.appendList((List)this.bounds.get(var6));
         }

         return var2.toList();
      }

      public List getDeclaredBounds() {
         ListBuffer var1 = new ListBuffer();
         int var2 = 0;
         Iterator var3 = this.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            if (var2++ == this.declaredCount) {
               break;
            }

            var1.append(var4);
         }

         return var1.toList();
      }

      public void setBounds(InferenceBound var1, List var2) {
         this.bounds.put(var1, var2);
      }

      public final void addBound(InferenceBound var1, Type var2, Types var3) {
         this.addBound(var1, var2, var3, false);
      }

      protected void addBound(InferenceBound var1, Type var2, Types var3, boolean var4) {
         Type var5 = this.toTypeVarMap.apply(var2).baseType();
         List var6 = (List)this.bounds.get(var1);
         Iterator var7 = var6.iterator();

         Type var8;
         do {
            if (!var7.hasNext()) {
               this.bounds.put(var1, var6.prepend(var5));
               this.notifyChange(EnumSet.of(var1));
               return;
            }

            var8 = (Type)var7.next();
         } while(!var3.isSameType(var8, var5, true) && var2 != this.qtype);

      }

      public void substBounds(List var1, List var2, Types var3) {
         List var4 = var1.diff(var2);
         if (!var4.isEmpty()) {
            final EnumSet var5 = EnumSet.noneOf(InferenceBound.class);
            UndetVarListener var6 = this.listener;

            try {
               this.listener = new UndetVarListener() {
                  public void varChanged(UndetVar var1, Set var2) {
                     var5.addAll(var2);
                  }
               };
               Iterator var7 = this.bounds.entrySet().iterator();

               while(var7.hasNext()) {
                  Map.Entry var8 = (Map.Entry)var7.next();
                  InferenceBound var9 = (InferenceBound)var8.getKey();
                  List var10 = (List)var8.getValue();
                  ListBuffer var11 = new ListBuffer();
                  ListBuffer var12 = new ListBuffer();
                  Iterator var13 = var10.iterator();

                  Type var14;
                  while(var13.hasNext()) {
                     var14 = (Type)var13.next();
                     if (!var14.containsAny(var4)) {
                        var11.append(var14);
                     } else {
                        var12.append(var14);
                     }
                  }

                  this.bounds.put(var9, var11.toList());
                  var13 = var12.iterator();

                  while(var13.hasNext()) {
                     var14 = (Type)var13.next();
                     this.addBound(var9, var3.subst(var14, var1, var2), var3, true);
                  }
               }
            } finally {
               this.listener = var6;
               if (!var5.isEmpty()) {
                  this.notifyChange(var5);
               }

            }

         }
      }

      private void notifyChange(EnumSet var1) {
         if (this.listener != null) {
            this.listener.varChanged(this, var1);
         }

      }

      public boolean isCaptured() {
         return false;
      }

      public static enum InferenceBound {
         UPPER {
            public InferenceBound complement() {
               return LOWER;
            }
         },
         LOWER {
            public InferenceBound complement() {
               return UPPER;
            }
         },
         EQ {
            public InferenceBound complement() {
               return EQ;
            }
         };

         private InferenceBound() {
         }

         public abstract InferenceBound complement();

         // $FF: synthetic method
         InferenceBound(Object var3) {
            this();
         }
      }

      public interface UndetVarListener {
         void varChanged(UndetVar var1, Set var2);
      }
   }

   public static class ForAll extends DelegatedType implements ExecutableType {
      public List tvars;

      public ForAll(List var1, Type var2) {
         super(TypeTag.FORALL, (MethodType)var2);
         this.tvars = var1;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitForAll(this, var2);
      }

      public String toString() {
         return "<" + this.tvars + ">" + this.qtype;
      }

      public List getTypeArguments() {
         return this.tvars;
      }

      public boolean isErroneous() {
         return this.qtype.isErroneous();
      }

      public Type map(Mapping var1) {
         return var1.apply(this.qtype);
      }

      public boolean contains(Type var1) {
         return this.qtype.contains(var1);
      }

      public MethodType asMethodType() {
         return (MethodType)this.qtype;
      }

      public void complete() {
         for(List var1 = this.tvars; var1.nonEmpty(); var1 = var1.tail) {
            ((TypeVar)var1.head).bound.complete();
         }

         this.qtype.complete();
      }

      public List getTypeVariables() {
         return List.convert(TypeVar.class, this.getTypeArguments());
      }

      public TypeKind getKind() {
         return TypeKind.EXECUTABLE;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitExecutable(this, var2);
      }
   }

   public abstract static class DelegatedType extends Type {
      public Type qtype;
      public TypeTag tag;

      public DelegatedType(TypeTag var1, Type var2) {
         super(var2.tsym);
         this.tag = var1;
         this.qtype = var2;
      }

      public TypeTag getTag() {
         return this.tag;
      }

      public String toString() {
         return this.qtype.toString();
      }

      public List getTypeArguments() {
         return this.qtype.getTypeArguments();
      }

      public Type getEnclosingType() {
         return this.qtype.getEnclosingType();
      }

      public List getParameterTypes() {
         return this.qtype.getParameterTypes();
      }

      public Type getReturnType() {
         return this.qtype.getReturnType();
      }

      public Type getReceiverType() {
         return this.qtype.getReceiverType();
      }

      public List getThrownTypes() {
         return this.qtype.getThrownTypes();
      }

      public List allparams() {
         return this.qtype.allparams();
      }

      public Type getUpperBound() {
         return this.qtype.getUpperBound();
      }

      public boolean isErroneous() {
         return this.qtype.isErroneous();
      }
   }

   public static class CapturedType extends TypeVar {
      public WildcardType wildcard;

      public CapturedType(Name var1, Symbol var2, Type var3, Type var4, WildcardType var5) {
         super(var1, var2, var4);
         this.lower = (Type)Assert.checkNonNull(var4);
         this.bound = var3;
         this.wildcard = var5;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitCapturedType(this, var2);
      }

      public boolean isCaptured() {
         return true;
      }

      public String toString() {
         return "capture#" + ((long)this.hashCode() & 4294967295L) % 997L + " of " + this.wildcard;
      }
   }

   public static class TypeVar extends Type implements TypeVariable {
      public Type bound = null;
      public Type lower;
      int rank_field = -1;

      public TypeVar(Name var1, Symbol var2, Type var3) {
         super((Symbol.TypeSymbol)null);
         this.tsym = new Symbol.TypeVariableSymbol(0L, var1, this, var2);
         this.lower = var3;
      }

      public TypeVar(Symbol.TypeSymbol var1, Type var2, Type var3) {
         super(var1);
         this.bound = var2;
         this.lower = var3;
      }

      public TypeTag getTag() {
         return TypeTag.TYPEVAR;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitTypeVar(this, var2);
      }

      public Type getUpperBound() {
         if ((this.bound == null || this.bound.hasTag(TypeTag.NONE)) && this != this.tsym.type) {
            this.bound = this.tsym.type.getUpperBound();
         }

         return this.bound;
      }

      public Type getLowerBound() {
         return this.lower;
      }

      public TypeKind getKind() {
         return TypeKind.TYPEVAR;
      }

      public boolean isCaptured() {
         return false;
      }

      public boolean isReference() {
         return true;
      }

      public boolean isNullOrReference() {
         return true;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitTypeVariable(this, var2);
      }
   }

   public static class PackageType extends Type implements NoType {
      PackageType(Symbol.TypeSymbol var1) {
         super(var1);
      }

      public TypeTag getTag() {
         return TypeTag.PACKAGE;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitPackageType(this, var2);
      }

      public String toString() {
         return this.tsym.getQualifiedName().toString();
      }

      public TypeKind getKind() {
         return TypeKind.PACKAGE;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitNoType(this, var2);
      }
   }

   public static class MethodType extends Type implements ExecutableType {
      public List argtypes;
      public Type restype;
      public List thrown;
      public Type recvtype;

      public MethodType(List var1, Type var2, List var3, Symbol.TypeSymbol var4) {
         super(var4);
         this.argtypes = var1;
         this.restype = var2;
         this.thrown = var3;
      }

      public TypeTag getTag() {
         return TypeTag.METHOD;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitMethodType(this, var2);
      }

      public String toString() {
         return "(" + this.argtypes + ")" + this.restype;
      }

      public List getParameterTypes() {
         return this.argtypes;
      }

      public Type getReturnType() {
         return this.restype;
      }

      public Type getReceiverType() {
         return this.recvtype;
      }

      public List getThrownTypes() {
         return this.thrown;
      }

      public boolean isErroneous() {
         return isErroneous(this.argtypes) || this.restype != null && this.restype.isErroneous();
      }

      public Type map(Mapping var1) {
         List var2 = map(this.argtypes, var1);
         Type var3 = var1.apply(this.restype);
         List var4 = map(this.thrown, var1);
         return var2 == this.argtypes && var3 == this.restype && var4 == this.thrown ? this : new MethodType(var2, var3, var4, this.tsym);
      }

      public boolean contains(Type var1) {
         return var1 == this || contains(this.argtypes, var1) || this.restype.contains(var1) || contains(this.thrown, var1);
      }

      public MethodType asMethodType() {
         return this;
      }

      public void complete() {
         List var1;
         for(var1 = this.argtypes; var1.nonEmpty(); var1 = var1.tail) {
            ((Type)var1.head).complete();
         }

         this.restype.complete();
         this.recvtype.complete();

         for(var1 = this.thrown; var1.nonEmpty(); var1 = var1.tail) {
            ((Type)var1.head).complete();
         }

      }

      public List getTypeVariables() {
         return List.nil();
      }

      public Symbol.TypeSymbol asElement() {
         return null;
      }

      public TypeKind getKind() {
         return TypeKind.EXECUTABLE;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitExecutable(this, var2);
      }
   }

   public static class ArrayType extends Type implements javax.lang.model.type.ArrayType {
      public Type elemtype;

      public ArrayType(Type var1, Symbol.TypeSymbol var2) {
         super(var2);
         this.elemtype = var1;
      }

      public TypeTag getTag() {
         return TypeTag.ARRAY;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitArrayType(this, var2);
      }

      public String toString() {
         return this.elemtype + "[]";
      }

      public boolean equals(Object var1) {
         return this == var1 || var1 instanceof ArrayType && this.elemtype.equals(((ArrayType)var1).elemtype);
      }

      public int hashCode() {
         return (TypeTag.ARRAY.ordinal() << 5) + this.elemtype.hashCode();
      }

      public boolean isVarargs() {
         return false;
      }

      public List allparams() {
         return this.elemtype.allparams();
      }

      public boolean isErroneous() {
         return this.elemtype.isErroneous();
      }

      public boolean isParameterized() {
         return this.elemtype.isParameterized();
      }

      public boolean isReference() {
         return true;
      }

      public boolean isNullOrReference() {
         return true;
      }

      public boolean isRaw() {
         return this.elemtype.isRaw();
      }

      public ArrayType makeVarargs() {
         return new ArrayType(this.elemtype, this.tsym) {
            public boolean isVarargs() {
               return true;
            }
         };
      }

      public Type map(Mapping var1) {
         Type var2 = var1.apply(this.elemtype);
         return var2 == this.elemtype ? this : new ArrayType(var2, this.tsym);
      }

      public boolean contains(Type var1) {
         return var1 == this || this.elemtype.contains(var1);
      }

      public void complete() {
         this.elemtype.complete();
      }

      public Type getComponentType() {
         return this.elemtype;
      }

      public TypeKind getKind() {
         return TypeKind.ARRAY;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitArray(this, var2);
      }
   }

   public static class IntersectionClassType extends ClassType implements IntersectionType {
      public boolean allInterfaces;

      public IntersectionClassType(List var1, Symbol.ClassSymbol var2, boolean var3) {
         super(Type.noType, List.nil(), var2);
         this.allInterfaces = var3;
         Assert.check((var2.flags() & 16777216L) != 0L);
         this.supertype_field = (Type)var1.head;
         this.interfaces_field = var1.tail;
         Assert.check(this.supertype_field.tsym.completer != null || !this.supertype_field.isInterface(), (Object)this.supertype_field);
      }

      public java.util.List getBounds() {
         return Collections.unmodifiableList(this.getExplicitComponents());
      }

      public List getComponents() {
         return this.interfaces_field.prepend(this.supertype_field);
      }

      public boolean isIntersection() {
         return true;
      }

      public List getExplicitComponents() {
         return this.allInterfaces ? this.interfaces_field : this.getComponents();
      }

      public TypeKind getKind() {
         return TypeKind.INTERSECTION;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitIntersection(this, var2);
      }
   }

   public static class UnionClassType extends ClassType implements UnionType {
      final List alternatives_field;

      public UnionClassType(ClassType var1, List var2) {
         super(var1.outer_field, var1.typarams_field, var1.tsym);
         this.allparams_field = var1.allparams_field;
         this.supertype_field = var1.supertype_field;
         this.interfaces_field = var1.interfaces_field;
         this.all_interfaces_field = var1.interfaces_field;
         this.alternatives_field = var2;
      }

      public Type getLub() {
         return this.tsym.type;
      }

      public java.util.List getAlternatives() {
         return Collections.unmodifiableList(this.alternatives_field);
      }

      public boolean isUnion() {
         return true;
      }

      public TypeKind getKind() {
         return TypeKind.UNION;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitUnion(this, var2);
      }
   }

   public static class ErasedClassType extends ClassType {
      public ErasedClassType(Type var1, Symbol.TypeSymbol var2) {
         super(var1, List.nil(), var2);
      }

      public boolean hasErasedSupertypes() {
         return true;
      }
   }

   public static class ClassType extends Type implements DeclaredType {
      private Type outer_field;
      public List typarams_field;
      public List allparams_field;
      public Type supertype_field;
      public List interfaces_field;
      public List all_interfaces_field;
      int rank_field = -1;

      public ClassType(Type var1, List var2, Symbol.TypeSymbol var3) {
         super(var3);
         this.outer_field = var1;
         this.typarams_field = var2;
         this.allparams_field = null;
         this.supertype_field = null;
         this.interfaces_field = null;
      }

      public TypeTag getTag() {
         return TypeTag.CLASS;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitClassType(this, var2);
      }

      public Type constType(final Object var1) {
         return new ClassType(this.getEnclosingType(), this.typarams_field, this.tsym) {
            public Object constValue() {
               return var1;
            }

            public Type baseType() {
               return this.tsym.type;
            }
         };
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.getEnclosingType().hasTag(TypeTag.CLASS) && this.tsym.owner.kind == 2) {
            var1.append(this.getEnclosingType().toString());
            var1.append(".");
            var1.append(this.className(this.tsym, false));
         } else {
            var1.append(this.className(this.tsym, true));
         }

         if (this.getTypeArguments().nonEmpty()) {
            var1.append('<');
            var1.append(this.getTypeArguments().toString());
            var1.append(">");
         }

         return var1.toString();
      }

      private String className(Symbol var1, boolean var2) {
         if (var1.name.isEmpty() && (var1.flags() & 16777216L) != 0L) {
            StringBuilder var6 = new StringBuilder(this.supertype_field.toString());

            for(List var5 = this.interfaces_field; var5.nonEmpty(); var5 = var5.tail) {
               var6.append("&");
               var6.append(((Type)var5.head).toString());
            }

            return var6.toString();
         } else if (!var1.name.isEmpty()) {
            return var2 ? var1.getQualifiedName().toString() : var1.name.toString();
         } else {
            ClassType var4 = (ClassType)this.tsym.type.unannotatedType();
            String var3;
            if (var4 == null) {
               var3 = Log.getLocalizedString("anonymous.class", null);
            } else if (var4.interfaces_field != null && var4.interfaces_field.nonEmpty()) {
               var3 = Log.getLocalizedString("anonymous.class", var4.interfaces_field.head);
            } else {
               var3 = Log.getLocalizedString("anonymous.class", var4.supertype_field);
            }

            if (moreInfo) {
               var3 = var3 + String.valueOf(var1.hashCode());
            }

            return var3;
         }
      }

      public List getTypeArguments() {
         if (this.typarams_field == null) {
            this.complete();
            if (this.typarams_field == null) {
               this.typarams_field = List.nil();
            }
         }

         return this.typarams_field;
      }

      public boolean hasErasedSupertypes() {
         return this.isRaw();
      }

      public Type getEnclosingType() {
         return this.outer_field;
      }

      public void setEnclosingType(Type var1) {
         this.outer_field = var1;
      }

      public List allparams() {
         if (this.allparams_field == null) {
            this.allparams_field = this.getTypeArguments().prependList(this.getEnclosingType().allparams());
         }

         return this.allparams_field;
      }

      public boolean isErroneous() {
         return this.getEnclosingType().isErroneous() || isErroneous(this.getTypeArguments()) || this != this.tsym.type.unannotatedType() && this.tsym.type.isErroneous();
      }

      public boolean isParameterized() {
         return this.allparams().tail != null;
      }

      public boolean isReference() {
         return true;
      }

      public boolean isNullOrReference() {
         return true;
      }

      public boolean isRaw() {
         return this != this.tsym.type && this.tsym.type.allparams().nonEmpty() && this.allparams().isEmpty();
      }

      public Type map(Mapping var1) {
         Type var2 = this.getEnclosingType();
         Type var3 = var1.apply(var2);
         List var4 = this.getTypeArguments();
         List var5 = map(var4, var1);
         return var3 == var2 && var5 == var4 ? this : new ClassType(var3, var5, this.tsym);
      }

      public boolean contains(Type var1) {
         return var1 == this || this.isParameterized() && (this.getEnclosingType().contains(var1) || contains(this.getTypeArguments(), var1)) || this.isCompound() && (this.supertype_field.contains(var1) || contains(this.interfaces_field, var1));
      }

      public void complete() {
         if (this.tsym.completer != null) {
            this.tsym.complete();
         }

      }

      public TypeKind getKind() {
         return TypeKind.DECLARED;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitDeclared(this, var2);
      }
   }

   public static class WildcardType extends Type implements javax.lang.model.type.WildcardType {
      public Type type;
      public BoundKind kind;
      public TypeVar bound;
      boolean isPrintingBound;

      public Object accept(Visitor var1, Object var2) {
         return var1.visitWildcardType(this, var2);
      }

      public WildcardType(Type var1, BoundKind var2, Symbol.TypeSymbol var3) {
         super(var3);
         this.isPrintingBound = false;
         this.type = (Type)Assert.checkNonNull(var1);
         this.kind = var2;
      }

      public WildcardType(WildcardType var1, TypeVar var2) {
         this(var1.type, var1.kind, var1.tsym, var2);
      }

      public WildcardType(Type var1, BoundKind var2, Symbol.TypeSymbol var3, TypeVar var4) {
         this(var1, var2, var3);
         this.bound = var4;
      }

      public TypeTag getTag() {
         return TypeTag.WILDCARD;
      }

      public boolean contains(Type var1) {
         return this.kind != BoundKind.UNBOUND && this.type.contains(var1);
      }

      public boolean isSuperBound() {
         return this.kind == BoundKind.SUPER || this.kind == BoundKind.UNBOUND;
      }

      public boolean isExtendsBound() {
         return this.kind == BoundKind.EXTENDS || this.kind == BoundKind.UNBOUND;
      }

      public boolean isUnbound() {
         return this.kind == BoundKind.UNBOUND;
      }

      public boolean isReference() {
         return true;
      }

      public boolean isNullOrReference() {
         return true;
      }

      public Type withTypeVar(Type var1) {
         if (this.bound == var1) {
            return this;
         } else {
            this.bound = (TypeVar)var1;
            return this;
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.kind.toString());
         if (this.kind != BoundKind.UNBOUND) {
            var1.append(this.type);
         }

         if (moreInfo && this.bound != null && !this.isPrintingBound) {
            try {
               this.isPrintingBound = true;
               var1.append("{:").append(this.bound.bound).append(":}");
            } finally {
               this.isPrintingBound = false;
            }
         }

         return var1.toString();
      }

      public Type map(Mapping var1) {
         Type var2 = this.type;
         if (var2 != null) {
            var2 = var1.apply(var2);
         }

         return var2 == this.type ? this : new WildcardType(var2, this.kind, this.tsym, this.bound);
      }

      public Type getExtendsBound() {
         return this.kind == BoundKind.EXTENDS ? this.type : null;
      }

      public Type getSuperBound() {
         return this.kind == BoundKind.SUPER ? this.type : null;
      }

      public TypeKind getKind() {
         return TypeKind.WILDCARD;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitWildcard(this, var2);
      }
   }

   public static class JCPrimitiveType extends Type implements PrimitiveType {
      TypeTag tag;

      public JCPrimitiveType(TypeTag var1, Symbol.TypeSymbol var2) {
         super(var2);
         this.tag = var1;
         Assert.check(var1.isPrimitive);
      }

      public boolean isNumeric() {
         return this.tag != TypeTag.BOOLEAN;
      }

      public boolean isPrimitive() {
         return true;
      }

      public TypeTag getTag() {
         return this.tag;
      }

      public boolean isPrimitiveOrVoid() {
         return true;
      }

      public Type constType(final Object var1) {
         return new JCPrimitiveType(this.tag, this.tsym) {
            public Object constValue() {
               return var1;
            }

            public Type baseType() {
               return this.tsym.type;
            }
         };
      }

      public String stringValue() {
         Object var1 = Assert.checkNonNull(this.constValue());
         if (this.tag == TypeTag.BOOLEAN) {
            return (Integer)var1 == 0 ? "false" : "true";
         } else {
            return this.tag == TypeTag.CHAR ? String.valueOf((char)(Integer)var1) : var1.toString();
         }
      }

      public boolean isFalse() {
         return this.tag == TypeTag.BOOLEAN && this.constValue() != null && (Integer)this.constValue() == 0;
      }

      public boolean isTrue() {
         return this.tag == TypeTag.BOOLEAN && this.constValue() != null && (Integer)this.constValue() != 0;
      }

      public Object accept(TypeVisitor var1, Object var2) {
         return var1.visitPrimitive(this, var2);
      }

      public TypeKind getKind() {
         switch (this.tag) {
            case BYTE:
               return TypeKind.BYTE;
            case CHAR:
               return TypeKind.CHAR;
            case SHORT:
               return TypeKind.SHORT;
            case INT:
               return TypeKind.INT;
            case LONG:
               return TypeKind.LONG;
            case FLOAT:
               return TypeKind.FLOAT;
            case DOUBLE:
               return TypeKind.DOUBLE;
            case BOOLEAN:
               return TypeKind.BOOLEAN;
            default:
               throw new AssertionError();
         }
      }
   }

   public abstract static class Mapping {
      private String name;

      public Mapping(String var1) {
         this.name = var1;
      }

      public abstract Type apply(Type var1);

      public String toString() {
         return this.name;
      }
   }
}
