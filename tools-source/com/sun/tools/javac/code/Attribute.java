package com.sun.tools.javac.code;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Constants;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Pair;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.type.DeclaredType;

public abstract class Attribute implements AnnotationValue {
   public Type type;

   public Attribute(Type var1) {
      this.type = var1;
   }

   public abstract void accept(Visitor var1);

   public Object getValue() {
      throw new UnsupportedOperationException();
   }

   public Object accept(AnnotationValueVisitor var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   public boolean isSynthesized() {
      return false;
   }

   public TypeAnnotationPosition getPosition() {
      return null;
   }

   public static enum RetentionPolicy {
      SOURCE,
      CLASS,
      RUNTIME;
   }

   public interface Visitor {
      void visitConstant(Constant var1);

      void visitClass(Class var1);

      void visitCompound(Compound var1);

      void visitArray(Array var1);

      void visitEnum(Enum var1);

      void visitError(Error var1);
   }

   public static class UnresolvedClass extends Error {
      public Type classType;

      public UnresolvedClass(Type var1, Type var2) {
         super(var1);
         this.classType = var2;
      }
   }

   public static class Error extends Attribute {
      public Error(Type var1) {
         super(var1);
      }

      public void accept(Visitor var1) {
         var1.visitError(this);
      }

      public String toString() {
         return "<error>";
      }

      public String getValue() {
         return this.toString();
      }

      public Object accept(AnnotationValueVisitor var1, Object var2) {
         return var1.visitString(this.toString(), var2);
      }
   }

   public static class Enum extends Attribute {
      public Symbol.VarSymbol value;

      public Enum(Type var1, Symbol.VarSymbol var2) {
         super(var1);
         this.value = (Symbol.VarSymbol)Assert.checkNonNull(var2);
      }

      public void accept(Visitor var1) {
         var1.visitEnum(this);
      }

      public String toString() {
         return this.value.enclClass() + "." + this.value;
      }

      public Symbol.VarSymbol getValue() {
         return this.value;
      }

      public Object accept(AnnotationValueVisitor var1, Object var2) {
         return var1.visitEnumConstant(this.value, var2);
      }
   }

   public static class Array extends Attribute {
      public final Attribute[] values;

      public Array(Type var1, Attribute[] var2) {
         super(var1);
         this.values = var2;
      }

      public Array(Type var1, List var2) {
         super(var1);
         this.values = (Attribute[])var2.toArray(new Attribute[var2.size()]);
      }

      public void accept(Visitor var1) {
         var1.visitArray(this);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append('{');
         boolean var2 = true;
         Attribute[] var3 = this.values;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Attribute var6 = var3[var5];
            if (!var2) {
               var1.append(", ");
            }

            var2 = false;
            var1.append(var6);
         }

         var1.append('}');
         return var1.toString();
      }

      public List getValue() {
         return List.from((Object[])this.values);
      }

      public Object accept(AnnotationValueVisitor var1, Object var2) {
         return var1.visitArray(this.getValue(), var2);
      }

      public TypeAnnotationPosition getPosition() {
         return this.values.length != 0 ? this.values[0].getPosition() : null;
      }
   }

   public static class TypeCompound extends Compound {
      public TypeAnnotationPosition position;

      public TypeCompound(Compound var1, TypeAnnotationPosition var2) {
         this(var1.type, var1.values, var2);
      }

      public TypeCompound(Type var1, List var2, TypeAnnotationPosition var3) {
         super(var1, var2);
         this.position = var3;
      }

      public TypeAnnotationPosition getPosition() {
         if (this.hasUnknownPosition()) {
            this.position = super.getPosition();
         }

         return this.position;
      }

      public boolean hasUnknownPosition() {
         return this.position.type == TargetType.UNKNOWN;
      }

      public boolean isContainerTypeCompound() {
         if (this.isSynthesized() && this.values.size() == 1) {
            return this.getFirstEmbeddedTC() != null;
         } else {
            return false;
         }
      }

      private TypeCompound getFirstEmbeddedTC() {
         if (this.values.size() == 1) {
            Pair var1 = (Pair)this.values.get(0);
            if (((Symbol.MethodSymbol)var1.fst).getSimpleName().contentEquals("value") && var1.snd instanceof Array) {
               Array var2 = (Array)var1.snd;
               if (var2.values.length != 0 && var2.values[0] instanceof TypeCompound) {
                  return (TypeCompound)var2.values[0];
               }
            }
         }

         return null;
      }

      public boolean tryFixPosition() {
         if (!this.isContainerTypeCompound()) {
            return false;
         } else {
            TypeCompound var1 = this.getFirstEmbeddedTC();
            if (var1 != null && var1.position != null && var1.position.type != TargetType.UNKNOWN) {
               this.position = var1.position;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public static class Compound extends Attribute implements AnnotationMirror {
      public final List values;
      private boolean synthesized = false;

      public boolean isSynthesized() {
         return this.synthesized;
      }

      public void setSynthesized(boolean var1) {
         this.synthesized = var1;
      }

      public Compound(Type var1, List var2) {
         super(var1);
         this.values = var2;
      }

      public void accept(Visitor var1) {
         var1.visitCompound(this);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("@");
         var1.append(this.type);
         int var2 = this.values.length();
         if (var2 > 0) {
            var1.append('(');
            boolean var3 = true;

            Pair var5;
            for(Iterator var4 = this.values.iterator(); var4.hasNext(); var1.append(var5.snd)) {
               var5 = (Pair)var4.next();
               if (!var3) {
                  var1.append(", ");
               }

               var3 = false;
               Name var6 = ((Symbol.MethodSymbol)var5.fst).name;
               if (var2 > 1 || var6 != var6.table.names.value) {
                  var1.append(var6);
                  var1.append('=');
               }
            }

            var1.append(')');
         }

         return var1.toString();
      }

      public Attribute member(Name var1) {
         Pair var2 = this.getElemPair(var1);
         return var2 == null ? null : (Attribute)var2.snd;
      }

      private Pair getElemPair(Name var1) {
         Iterator var2 = this.values.iterator();

         Pair var3;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            var3 = (Pair)var2.next();
         } while(((Symbol.MethodSymbol)var3.fst).name != var1);

         return var3;
      }

      public Compound getValue() {
         return this;
      }

      public Object accept(AnnotationValueVisitor var1, Object var2) {
         return var1.visitAnnotation(this, var2);
      }

      public DeclaredType getAnnotationType() {
         return (DeclaredType)this.type;
      }

      public TypeAnnotationPosition getPosition() {
         if (this.values.size() != 0) {
            Name var1 = ((Symbol.MethodSymbol)((Pair)this.values.head).fst).name.table.names.value;
            Pair var2 = this.getElemPair(var1);
            return var2 == null ? null : ((Attribute)var2.snd).getPosition();
         } else {
            return null;
         }
      }

      public Map getElementValues() {
         LinkedHashMap var1 = new LinkedHashMap();
         Iterator var2 = this.values.iterator();

         while(var2.hasNext()) {
            Pair var3 = (Pair)var2.next();
            var1.put(var3.fst, var3.snd);
         }

         return var1;
      }
   }

   public static class Class extends Attribute {
      public final Type classType;

      public void accept(Visitor var1) {
         var1.visitClass(this);
      }

      public Class(Types var1, Type var2) {
         super(makeClassType(var1, var2));
         this.classType = var2;
      }

      static Type makeClassType(Types var0, Type var1) {
         Type var2 = var1.isPrimitive() ? var0.boxedClass(var1).type : var0.erasure(var1);
         return new Type.ClassType(var0.syms.classType.getEnclosingType(), List.of(var2), var0.syms.classType.tsym);
      }

      public String toString() {
         return this.classType + ".class";
      }

      public Type getValue() {
         return this.classType;
      }

      public Object accept(AnnotationValueVisitor var1, Object var2) {
         return var1.visitType(this.classType, var2);
      }
   }

   public static class Constant extends Attribute {
      public final Object value;

      public void accept(Visitor var1) {
         var1.visitConstant(this);
      }

      public Constant(Type var1, Object var2) {
         super(var1);
         this.value = var2;
      }

      public String toString() {
         return Constants.format(this.value, this.type);
      }

      public Object getValue() {
         return Constants.decode(this.value, this.type);
      }

      public Object accept(AnnotationValueVisitor var1, Object var2) {
         if (this.value instanceof String) {
            return var1.visitString((String)this.value, var2);
         } else {
            if (this.value instanceof Integer) {
               int var3 = (Integer)this.value;
               switch (this.type.getTag()) {
                  case BOOLEAN:
                     return var1.visitBoolean(var3 != 0, var2);
                  case CHAR:
                     return var1.visitChar((char)var3, var2);
                  case BYTE:
                     return var1.visitByte((byte)var3, var2);
                  case SHORT:
                     return var1.visitShort((short)var3, var2);
                  case INT:
                     return var1.visitInt(var3, var2);
               }
            }

            switch (this.type.getTag()) {
               case LONG:
                  return var1.visitLong((Long)this.value, var2);
               case FLOAT:
                  return var1.visitFloat((Float)this.value, var2);
               case DOUBLE:
                  return var1.visitDouble((Double)this.value, var2);
               default:
                  throw new AssertionError("Bad annotation element value: " + this.value);
            }
         }
      }
   }
}
