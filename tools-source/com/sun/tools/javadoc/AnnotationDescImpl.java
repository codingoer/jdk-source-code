package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;
import java.util.Iterator;

public class AnnotationDescImpl implements AnnotationDesc {
   private final DocEnv env;
   private final Attribute.Compound annotation;

   AnnotationDescImpl(DocEnv var1, Attribute.Compound var2) {
      this.env = var1;
      this.annotation = var2;
   }

   public AnnotationTypeDoc annotationType() {
      Symbol.ClassSymbol var1 = (Symbol.ClassSymbol)this.annotation.type.tsym;
      if (this.annotation.type.isErroneous()) {
         this.env.warning((DocImpl)null, "javadoc.class_not_found", this.annotation.type.toString());
         return new AnnotationTypeDocImpl(this.env, var1);
      } else {
         return (AnnotationTypeDoc)this.env.getClassDoc(var1);
      }
   }

   public AnnotationDesc.ElementValuePair[] elementValues() {
      List var1 = this.annotation.values;
      AnnotationDesc.ElementValuePair[] var2 = new AnnotationDesc.ElementValuePair[var1.length()];
      int var3 = 0;

      Pair var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = new ElementValuePairImpl(this.env, (Symbol.MethodSymbol)var5.fst, (Attribute)var5.snd)) {
         var5 = (Pair)var4.next();
      }

      return var2;
   }

   public boolean isSynthesized() {
      return this.annotation.isSynthesized();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("@");
      var1.append(this.annotation.type.tsym);
      AnnotationDesc.ElementValuePair[] var2 = this.elementValues();
      if (var2.length > 0) {
         var1.append('(');
         boolean var3 = true;
         AnnotationDesc.ElementValuePair[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            AnnotationDesc.ElementValuePair var7 = var4[var6];
            if (!var3) {
               var1.append(", ");
            }

            var3 = false;
            String var8 = var7.element().name();
            if (var2.length == 1 && var8.equals("value")) {
               var1.append(var7.value());
            } else {
               var1.append(var7);
            }
         }

         var1.append(')');
      }

      return var1.toString();
   }

   public static class ElementValuePairImpl implements AnnotationDesc.ElementValuePair {
      private final DocEnv env;
      private final Symbol.MethodSymbol meth;
      private final Attribute value;

      ElementValuePairImpl(DocEnv var1, Symbol.MethodSymbol var2, Attribute var3) {
         this.env = var1;
         this.meth = var2;
         this.value = var3;
      }

      public AnnotationTypeElementDoc element() {
         return this.env.getAnnotationTypeElementDoc(this.meth);
      }

      public AnnotationValue value() {
         return new AnnotationValueImpl(this.env, this.value);
      }

      public String toString() {
         return this.meth.name + "=" + this.value();
      }
   }
}
