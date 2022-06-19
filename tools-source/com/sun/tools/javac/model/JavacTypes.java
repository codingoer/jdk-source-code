package com.sun.tools.javac.model;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;

public class JavacTypes implements Types {
   private Symtab syms;
   private com.sun.tools.javac.code.Types types;
   private static final Set EXEC_OR_PKG;

   public static JavacTypes instance(Context var0) {
      JavacTypes var1 = (JavacTypes)var0.get(JavacTypes.class);
      if (var1 == null) {
         var1 = new JavacTypes(var0);
      }

      return var1;
   }

   protected JavacTypes(Context var1) {
      this.setContext(var1);
   }

   public void setContext(Context var1) {
      var1.put((Class)JavacTypes.class, (Object)this);
      this.syms = Symtab.instance(var1);
      this.types = com.sun.tools.javac.code.Types.instance(var1);
   }

   public Element asElement(TypeMirror var1) {
      switch (var1.getKind()) {
         case DECLARED:
         case INTERSECTION:
         case ERROR:
         case TYPEVAR:
            Type var2 = (Type)cast(Type.class, var1);
            return var2.asElement();
         default:
            return null;
      }
   }

   public boolean isSameType(TypeMirror var1, TypeMirror var2) {
      return this.types.isSameType((Type)var1, (Type)var2);
   }

   public boolean isSubtype(TypeMirror var1, TypeMirror var2) {
      this.validateTypeNotIn(var1, EXEC_OR_PKG);
      this.validateTypeNotIn(var2, EXEC_OR_PKG);
      return this.types.isSubtype((Type)var1, (Type)var2);
   }

   public boolean isAssignable(TypeMirror var1, TypeMirror var2) {
      this.validateTypeNotIn(var1, EXEC_OR_PKG);
      this.validateTypeNotIn(var2, EXEC_OR_PKG);
      return this.types.isAssignable((Type)var1, (Type)var2);
   }

   public boolean contains(TypeMirror var1, TypeMirror var2) {
      this.validateTypeNotIn(var1, EXEC_OR_PKG);
      this.validateTypeNotIn(var2, EXEC_OR_PKG);
      return this.types.containsType((Type)var1, (Type)var2);
   }

   public boolean isSubsignature(ExecutableType var1, ExecutableType var2) {
      return this.types.isSubSignature((Type)var1, (Type)var2);
   }

   public List directSupertypes(TypeMirror var1) {
      this.validateTypeNotIn(var1, EXEC_OR_PKG);
      return this.types.directSupertypes((Type)var1);
   }

   public TypeMirror erasure(TypeMirror var1) {
      if (var1.getKind() == TypeKind.PACKAGE) {
         throw new IllegalArgumentException(var1.toString());
      } else {
         return this.types.erasure((Type)var1);
      }
   }

   public TypeElement boxedClass(PrimitiveType var1) {
      return this.types.boxedClass((Type)var1);
   }

   public PrimitiveType unboxedType(TypeMirror var1) {
      if (var1.getKind() != TypeKind.DECLARED) {
         throw new IllegalArgumentException(var1.toString());
      } else {
         Type var2 = this.types.unboxedType((Type)var1);
         if (!var2.isPrimitive()) {
            throw new IllegalArgumentException(var1.toString());
         } else {
            return (PrimitiveType)var2;
         }
      }
   }

   public TypeMirror capture(TypeMirror var1) {
      this.validateTypeNotIn(var1, EXEC_OR_PKG);
      return this.types.capture((Type)var1);
   }

   public PrimitiveType getPrimitiveType(TypeKind var1) {
      switch (var1) {
         case BOOLEAN:
            return this.syms.booleanType;
         case BYTE:
            return this.syms.byteType;
         case SHORT:
            return this.syms.shortType;
         case INT:
            return this.syms.intType;
         case LONG:
            return this.syms.longType;
         case CHAR:
            return this.syms.charType;
         case FLOAT:
            return this.syms.floatType;
         case DOUBLE:
            return this.syms.doubleType;
         default:
            throw new IllegalArgumentException("Not a primitive type: " + var1);
      }
   }

   public NullType getNullType() {
      return (NullType)this.syms.botType;
   }

   public NoType getNoType(TypeKind var1) {
      switch (var1) {
         case VOID:
            return this.syms.voidType;
         case NONE:
            return Type.noType;
         default:
            throw new IllegalArgumentException(var1.toString());
      }
   }

   public ArrayType getArrayType(TypeMirror var1) {
      switch (var1.getKind()) {
         case VOID:
         case EXECUTABLE:
         case WILDCARD:
         case PACKAGE:
            throw new IllegalArgumentException(var1.toString());
         case NONE:
         default:
            return new Type.ArrayType((Type)var1, this.syms.arrayClass);
      }
   }

   public WildcardType getWildcardType(TypeMirror var1, TypeMirror var2) {
      BoundKind var3;
      Type var4;
      if (var1 == null && var2 == null) {
         var3 = BoundKind.UNBOUND;
         var4 = this.syms.objectType;
      } else if (var2 == null) {
         var3 = BoundKind.EXTENDS;
         var4 = (Type)var1;
      } else {
         if (var1 != null) {
            throw new IllegalArgumentException("Extends and super bounds cannot both be provided");
         }

         var3 = BoundKind.SUPER;
         var4 = (Type)var2;
      }

      switch (var4.getKind()) {
         case DECLARED:
         case ERROR:
         case TYPEVAR:
         case ARRAY:
            return new Type.WildcardType(var4, var3, this.syms.boundClass);
         default:
            throw new IllegalArgumentException(var4.toString());
      }
   }

   public DeclaredType getDeclaredType(TypeElement var1, TypeMirror... var2) {
      Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var1;
      if (var2.length == 0) {
         return (DeclaredType)var3.erasure(this.types);
      } else if (var3.type.getEnclosingType().isParameterized()) {
         throw new IllegalArgumentException(var3.toString());
      } else {
         return this.getDeclaredType0(var3.type.getEnclosingType(), var3, var2);
      }
   }

   public DeclaredType getDeclaredType(DeclaredType var1, TypeElement var2, TypeMirror... var3) {
      if (var1 == null) {
         return this.getDeclaredType(var2, var3);
      } else {
         Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)var2;
         Type var5 = (Type)var1;
         if (var5.tsym != var4.owner.enclClass()) {
            throw new IllegalArgumentException(var1.toString());
         } else {
            return !var5.isParameterized() ? this.getDeclaredType(var2, var3) : this.getDeclaredType0(var5, var4, var3);
         }
      }
   }

   private DeclaredType getDeclaredType0(Type var1, Symbol.ClassSymbol var2, TypeMirror... var3) {
      if (var3.length != var2.type.getTypeArguments().length()) {
         throw new IllegalArgumentException("Incorrect number of type arguments");
      } else {
         ListBuffer var4 = new ListBuffer();
         TypeMirror[] var5 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            TypeMirror var8 = var5[var7];
            if (!(var8 instanceof ReferenceType) && !(var8 instanceof WildcardType)) {
               throw new IllegalArgumentException(var8.toString());
            }

            var4.append((Type)var8);
         }

         return new Type.ClassType(var1, var4.toList(), var2);
      }
   }

   public TypeMirror asMemberOf(DeclaredType var1, Element var2) {
      Type var3 = (Type)var1;
      Symbol var4 = (Symbol)var2;
      if (this.types.asSuper(var3, var4.getEnclosingElement()) == null) {
         throw new IllegalArgumentException(var4 + "@" + var3);
      } else {
         return this.types.memberType(var3, var4);
      }
   }

   private void validateTypeNotIn(TypeMirror var1, Set var2) {
      if (var2.contains(var1.getKind())) {
         throw new IllegalArgumentException(var1.toString());
      }
   }

   private static Object cast(Class var0, Object var1) {
      if (!var0.isInstance(var1)) {
         throw new IllegalArgumentException(var1.toString());
      } else {
         return var0.cast(var1);
      }
   }

   public Set getOverriddenMethods(Element var1) {
      if (var1.getKind() == ElementKind.METHOD && !var1.getModifiers().contains(Modifier.STATIC) && !var1.getModifiers().contains(Modifier.PRIVATE)) {
         if (!(var1 instanceof Symbol.MethodSymbol)) {
            throw new IllegalArgumentException();
         } else {
            Symbol.MethodSymbol var2 = (Symbol.MethodSymbol)var1;
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.owner;
            LinkedHashSet var4 = new LinkedHashSet();
            Iterator var5 = this.types.closure(var3.type).iterator();

            while(true) {
               Type var6;
               do {
                  if (!var5.hasNext()) {
                     return var4;
                  }

                  var6 = (Type)var5.next();
               } while(var6 == var3.type);

               Symbol.ClassSymbol var7 = (Symbol.ClassSymbol)var6.tsym;

               for(Scope.Entry var8 = var7.members().lookup(var2.name); var8.scope != null; var8 = var8.next()) {
                  if (var8.sym.kind == 16 && var2.overrides(var8.sym, var3, this.types, true)) {
                     var4.add((Symbol.MethodSymbol)var8.sym);
                  }
               }
            }
         }
      } else {
         return Collections.emptySet();
      }
   }

   static {
      EXEC_OR_PKG = EnumSet.of(TypeKind.EXECUTABLE, TypeKind.PACKAGE);
   }
}
