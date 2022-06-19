package com.sun.tools.javac.code;

import com.sun.tools.javac.api.Messages;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.util.Iterator;
import java.util.Locale;

public abstract class Printer implements Type.Visitor, Symbol.Visitor {
   List seenCaptured = List.nil();
   static final int PRIME = 997;

   protected Printer() {
   }

   protected abstract String localize(Locale var1, String var2, Object... var3);

   protected abstract String capturedVarId(Type.CapturedType var1, Locale var2);

   public static Printer createStandardPrinter(final Messages var0) {
      return new Printer() {
         protected String localize(Locale var1, String var2, Object... var3) {
            return var0.getLocalizedString(var1, var2, var3);
         }

         protected String capturedVarId(Type.CapturedType var1, Locale var2) {
            return ((long)var1.hashCode() & 4294967295L) % 997L + "";
         }
      };
   }

   public String visitTypes(List var1, Locale var2) {
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Type var5 = (Type)var4.next();
         var3.append(this.visit(var5, var2));
      }

      return var3.toList().toString();
   }

   public String visitSymbols(List var1, Locale var2) {
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Symbol var5 = (Symbol)var4.next();
         var3.append(this.visit(var5, var2));
      }

      return var3.toList().toString();
   }

   public String visit(Type var1, Locale var2) {
      return (String)var1.accept((Type.Visitor)this, var2);
   }

   public String visit(Symbol var1, Locale var2) {
      return (String)var1.accept(this, var2);
   }

   public String visitCapturedType(Type.CapturedType var1, Locale var2) {
      if (this.seenCaptured.contains(var1)) {
         return this.localize(var2, "compiler.misc.type.captureof.1", this.capturedVarId(var1, var2));
      } else {
         String var3;
         try {
            this.seenCaptured = this.seenCaptured.prepend(var1);
            var3 = this.localize(var2, "compiler.misc.type.captureof", this.capturedVarId(var1, var2), this.visit((Type)var1.wildcard, var2));
         } finally {
            this.seenCaptured = this.seenCaptured.tail;
         }

         return var3;
      }
   }

   public String visitForAll(Type.ForAll var1, Locale var2) {
      return "<" + this.visitTypes(var1.tvars, var2) + ">" + this.visit(var1.qtype, var2);
   }

   public String visitUndetVar(Type.UndetVar var1, Locale var2) {
      return var1.inst != null ? this.visit(var1.inst, var2) : this.visit(var1.qtype, var2) + "?";
   }

   public String visitArrayType(Type.ArrayType var1, Locale var2) {
      StringBuilder var3 = new StringBuilder();
      this.printBaseElementType(var1, var3, var2);
      this.printBrackets(var1, var3, var2);
      return var3.toString();
   }

   void printBaseElementType(Type var1, StringBuilder var2, Locale var3) {
      Type var4;
      for(var4 = var1; var4.hasTag(TypeTag.ARRAY); var4 = ((Type.ArrayType)var4).elemtype) {
         var4 = var4.unannotatedType();
      }

      var2.append(this.visit(var4, var3));
   }

   void printBrackets(Type var1, StringBuilder var2, Locale var3) {
      for(Type var4 = var1; var4.hasTag(TypeTag.ARRAY); var4 = ((Type.ArrayType)var4).elemtype) {
         if (var4.isAnnotated()) {
            var2.append(' ');
            var2.append(var4.getAnnotationMirrors());
            var2.append(' ');
         }

         var2.append("[]");
         var4 = var4.unannotatedType();
      }

   }

   public String visitClassType(Type.ClassType var1, Locale var2) {
      StringBuilder var3 = new StringBuilder();
      if (var1.getEnclosingType().hasTag(TypeTag.CLASS) && var1.tsym.owner.kind == 2) {
         var3.append(this.visit(var1.getEnclosingType(), var2));
         var3.append('.');
         var3.append(this.className(var1, false, var2));
      } else {
         var3.append(this.className(var1, true, var2));
      }

      if (var1.getTypeArguments().nonEmpty()) {
         var3.append('<');
         var3.append(this.visitTypes(var1.getTypeArguments(), var2));
         var3.append('>');
      }

      return var3.toString();
   }

   public String visitMethodType(Type.MethodType var1, Locale var2) {
      return "(" + this.printMethodArgs(var1.argtypes, false, var2) + ")" + this.visit(var1.restype, var2);
   }

   public String visitPackageType(Type.PackageType var1, Locale var2) {
      return var1.tsym.getQualifiedName().toString();
   }

   public String visitWildcardType(Type.WildcardType var1, Locale var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append(var1.kind);
      if (var1.kind != BoundKind.UNBOUND) {
         var3.append(this.visit(var1.type, var2));
      }

      return var3.toString();
   }

   public String visitErrorType(Type.ErrorType var1, Locale var2) {
      return this.visitType(var1, (Locale)var2);
   }

   public String visitTypeVar(Type.TypeVar var1, Locale var2) {
      return this.visitType(var1, (Locale)var2);
   }

   public String visitAnnotatedType(Type.AnnotatedType var1, Locale var2) {
      if (var1.getAnnotationMirrors().nonEmpty()) {
         if (var1.unannotatedType().hasTag(TypeTag.ARRAY)) {
            StringBuilder var3 = new StringBuilder();
            this.printBaseElementType(var1, var3, var2);
            this.printBrackets(var1, var3, var2);
            return var3.toString();
         } else {
            return var1.unannotatedType().hasTag(TypeTag.CLASS) && var1.unannotatedType().getEnclosingType() != Type.noType ? this.visit(var1.unannotatedType().getEnclosingType(), var2) + ". " + var1.getAnnotationMirrors() + " " + this.className((Type.ClassType)var1.unannotatedType(), false, var2) : var1.getAnnotationMirrors() + " " + this.visit(var1.unannotatedType(), var2);
         }
      } else {
         return this.visit(var1.unannotatedType(), var2);
      }
   }

   public String visitType(Type var1, Locale var2) {
      String var3 = var1.tsym != null && var1.tsym.name != null ? var1.tsym.name.toString() : this.localize(var2, "compiler.misc.type.none");
      return var3;
   }

   protected String className(Type.ClassType var1, boolean var2, Locale var3) {
      Symbol.TypeSymbol var4 = var1.tsym;
      if (var4.name.length() == 0 && (var4.flags() & 16777216L) != 0L) {
         StringBuilder var7 = new StringBuilder(this.visit(var1.supertype_field, var3));

         for(List var8 = var1.interfaces_field; var8.nonEmpty(); var8 = var8.tail) {
            var7.append('&');
            var7.append(this.visit((Type)var8.head, var3));
         }

         return var7.toString();
      } else if (var4.name.length() != 0) {
         return var2 ? var4.getQualifiedName().toString() : var4.name.toString();
      } else {
         Type.ClassType var6 = (Type.ClassType)var1.tsym.type;
         String var5;
         if (var6 == null) {
            var5 = this.localize(var3, "compiler.misc.anonymous.class", null);
         } else if (var6.interfaces_field != null && var6.interfaces_field.nonEmpty()) {
            var5 = this.localize(var3, "compiler.misc.anonymous.class", this.visit((Type)var6.interfaces_field.head, var3));
         } else {
            var5 = this.localize(var3, "compiler.misc.anonymous.class", this.visit(var6.supertype_field, var3));
         }

         return var5;
      }
   }

   protected String printMethodArgs(List var1, boolean var2, Locale var3) {
      if (!var2) {
         return this.visitTypes(var1, var3);
      } else {
         StringBuilder var4 = new StringBuilder();

         while(var1.tail.nonEmpty()) {
            var4.append(this.visit((Type)var1.head, var3));
            var1 = var1.tail;
            var4.append(',');
         }

         if (((Type)var1.head).unannotatedType().hasTag(TypeTag.ARRAY)) {
            var4.append(this.visit(((Type.ArrayType)((Type)var1.head).unannotatedType()).elemtype, var3));
            if (((Type)var1.head).getAnnotationMirrors().nonEmpty()) {
               var4.append(' ');
               var4.append(((Type)var1.head).getAnnotationMirrors());
               var4.append(' ');
            }

            var4.append("...");
         } else {
            var4.append(this.visit((Type)var1.head, var3));
         }

         return var4.toString();
      }
   }

   public String visitClassSymbol(Symbol.ClassSymbol var1, Locale var2) {
      return var1.name.isEmpty() ? this.localize(var2, "compiler.misc.anonymous.class", var1.flatname) : var1.fullname.toString();
   }

   public String visitMethodSymbol(Symbol.MethodSymbol var1, Locale var2) {
      if (var1.isStaticOrInstanceInit()) {
         return var1.owner.name.toString();
      } else {
         String var3 = var1.name == var1.name.table.names.init ? var1.owner.name.toString() : var1.name.toString();
         if (var1.type != null) {
            if (var1.type.hasTag(TypeTag.FORALL)) {
               var3 = "<" + this.visitTypes(var1.type.getTypeArguments(), var2) + ">" + var3;
            }

            var3 = var3 + "(" + this.printMethodArgs(var1.type.getParameterTypes(), (var1.flags() & 17179869184L) != 0L, var2) + ")";
         }

         return var3;
      }
   }

   public String visitOperatorSymbol(Symbol.OperatorSymbol var1, Locale var2) {
      return this.visitMethodSymbol(var1, (Locale)var2);
   }

   public String visitPackageSymbol(Symbol.PackageSymbol var1, Locale var2) {
      return var1.isUnnamed() ? this.localize(var2, "compiler.misc.unnamed.package") : var1.fullname.toString();
   }

   public String visitTypeSymbol(Symbol.TypeSymbol var1, Locale var2) {
      return this.visitSymbol(var1, (Locale)var2);
   }

   public String visitVarSymbol(Symbol.VarSymbol var1, Locale var2) {
      return this.visitSymbol(var1, (Locale)var2);
   }

   public String visitSymbol(Symbol var1, Locale var2) {
      return var1.name.toString();
   }
}
