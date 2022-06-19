package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationValue;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.TypeTag;

public class AnnotationValueImpl implements AnnotationValue {
   private final DocEnv env;
   private final Attribute attr;

   AnnotationValueImpl(DocEnv var1, Attribute var2) {
      this.env = var1;
      this.attr = var2;
   }

   public Object value() {
      ValueVisitor var1 = new ValueVisitor();
      this.attr.accept(var1);
      return var1.value;
   }

   public String toString() {
      ToStringVisitor var1 = new ToStringVisitor();
      this.attr.accept(var1);
      return var1.toString();
   }

   private class ToStringVisitor implements Attribute.Visitor {
      private final StringBuilder sb;

      private ToStringVisitor() {
         this.sb = new StringBuilder();
      }

      public String toString() {
         return this.sb.toString();
      }

      public void visitConstant(Attribute.Constant var1) {
         if (var1.type.hasTag(TypeTag.BOOLEAN)) {
            this.sb.append((Integer)var1.value != 0);
         } else {
            this.sb.append(FieldDocImpl.constantValueExpression(var1.value));
         }

      }

      public void visitClass(Attribute.Class var1) {
         this.sb.append(var1);
      }

      public void visitEnum(Attribute.Enum var1) {
         this.sb.append(var1);
      }

      public void visitCompound(Attribute.Compound var1) {
         this.sb.append(new AnnotationDescImpl(AnnotationValueImpl.this.env, var1));
      }

      public void visitArray(Attribute.Array var1) {
         if (var1.values.length != 1) {
            this.sb.append('{');
         }

         boolean var2 = true;
         Attribute[] var3 = var1.values;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Attribute var6 = var3[var5];
            if (var2) {
               var2 = false;
            } else {
               this.sb.append(", ");
            }

            var6.accept(this);
         }

         if (var1.values.length != 1) {
            this.sb.append('}');
         }

      }

      public void visitError(Attribute.Error var1) {
         this.sb.append("<error>");
      }

      // $FF: synthetic method
      ToStringVisitor(Object var2) {
         this();
      }
   }

   private class ValueVisitor implements Attribute.Visitor {
      public Object value;

      private ValueVisitor() {
      }

      public void visitConstant(Attribute.Constant var1) {
         if (var1.type.hasTag(TypeTag.BOOLEAN)) {
            this.value = (Integer)var1.value != 0;
         } else {
            this.value = var1.value;
         }

      }

      public void visitClass(Attribute.Class var1) {
         this.value = TypeMaker.getType(AnnotationValueImpl.this.env, AnnotationValueImpl.this.env.types.erasure(var1.classType));
      }

      public void visitEnum(Attribute.Enum var1) {
         this.value = AnnotationValueImpl.this.env.getFieldDoc(var1.value);
      }

      public void visitCompound(Attribute.Compound var1) {
         this.value = new AnnotationDescImpl(AnnotationValueImpl.this.env, var1);
      }

      public void visitArray(Attribute.Array var1) {
         AnnotationValue[] var2 = new AnnotationValue[var1.values.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = new AnnotationValueImpl(AnnotationValueImpl.this.env, var1.values[var3]);
         }

         this.value = var2;
      }

      public void visitError(Attribute.Error var1) {
         this.value = "<error>";
      }

      // $FF: synthetic method
      ValueVisitor(Object var2) {
         this();
      }
   }
}
