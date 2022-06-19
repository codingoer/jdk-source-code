package com.sun.tools.javac.util;

import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Printer;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RichDiagnosticFormatter extends ForwardingDiagnosticFormatter {
   final Symtab syms;
   final Types types;
   final JCDiagnostic.Factory diags;
   final JavacMessages messages;
   protected ClassNameSimplifier nameSimplifier;
   private RichPrinter printer;
   Map whereClauses;
   protected Types.UnaryVisitor typePreprocessor = new Types.UnaryVisitor() {
      public Void visit(List var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Type var3 = (Type)var2.next();
            this.visit(var3);
         }

         return null;
      }

      public Void visitForAll(Type.ForAll var1, Void var2) {
         this.visit(var1.tvars);
         this.visit(var1.qtype);
         return null;
      }

      public Void visitMethodType(Type.MethodType var1, Void var2) {
         this.visit(var1.argtypes);
         this.visit(var1.restype);
         return null;
      }

      public Void visitErrorType(Type.ErrorType var1, Void var2) {
         Type var3 = var1.getOriginalType();
         if (var3 != null) {
            this.visit(var3);
         }

         return null;
      }

      public Void visitArrayType(Type.ArrayType var1, Void var2) {
         this.visit(var1.elemtype);
         return null;
      }

      public Void visitWildcardType(Type.WildcardType var1, Void var2) {
         this.visit(var1.type);
         return null;
      }

      public Void visitType(Type var1, Void var2) {
         return null;
      }

      public Void visitCapturedType(Type.CapturedType var1, Void var2) {
         if (RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.CAPTURED) == -1) {
            String var3 = var1.lower == RichDiagnosticFormatter.this.syms.botType ? ".1" : "";
            JCDiagnostic var4 = RichDiagnosticFormatter.this.diags.fragment("where.captured" + var3, var1, var1.bound, var1.lower, var1.wildcard);
            ((Map)RichDiagnosticFormatter.this.whereClauses.get(RichDiagnosticFormatter.WhereClauseKind.CAPTURED)).put(var1, var4);
            this.visit(var1.wildcard);
            this.visit(var1.lower);
            this.visit(var1.bound);
         }

         return null;
      }

      public Void visitClassType(Type.ClassType var1, Void var2) {
         if (var1.isCompound()) {
            if (RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.INTERSECTION) == -1) {
               Type var3 = RichDiagnosticFormatter.this.types.supertype(var1);
               List var4 = RichDiagnosticFormatter.this.types.interfaces(var1);
               JCDiagnostic var5 = RichDiagnosticFormatter.this.diags.fragment("where.intersection", var1, var4.prepend(var3));
               ((Map)RichDiagnosticFormatter.this.whereClauses.get(RichDiagnosticFormatter.WhereClauseKind.INTERSECTION)).put(var1, var5);
               this.visit(var3);
               this.visit(var4);
            }
         } else if (var1.tsym.name.isEmpty()) {
            Type.ClassType var6 = (Type.ClassType)var1.tsym.type;
            if (var6 != null) {
               if (var6.interfaces_field != null && var6.interfaces_field.nonEmpty()) {
                  this.visit((Type)var6.interfaces_field.head);
               } else {
                  this.visit(var6.supertype_field);
               }
            }
         }

         RichDiagnosticFormatter.this.nameSimplifier.addUsage(var1.tsym);
         this.visit(var1.getTypeArguments());
         if (var1.getEnclosingType() != Type.noType) {
            this.visit(var1.getEnclosingType());
         }

         return null;
      }

      public Void visitTypeVar(Type.TypeVar var1, Void var2) {
         if (RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.TYPEVAR) == -1) {
            Type var3;
            for(var3 = var1.bound; var3 instanceof Type.ErrorType; var3 = ((Type.ErrorType)var3).getOriginalType()) {
            }

            List var4 = var3 == null || !var3.hasTag(TypeTag.CLASS) && !var3.hasTag(TypeTag.TYPEVAR) ? List.nil() : RichDiagnosticFormatter.this.types.getBounds(var1);
            RichDiagnosticFormatter.this.nameSimplifier.addUsage(var1.tsym);
            boolean var5 = var4.head == null || ((Type)var4.head).hasTag(TypeTag.NONE) || ((Type)var4.head).hasTag(TypeTag.ERROR);
            JCDiagnostic var6;
            if ((var1.tsym.flags() & 4096L) == 0L) {
               var6 = RichDiagnosticFormatter.this.diags.fragment("where.typevar" + (var5 ? ".1" : ""), var1, var4, Kinds.kindName(var1.tsym.location()), var1.tsym.location());
               ((Map)RichDiagnosticFormatter.this.whereClauses.get(RichDiagnosticFormatter.WhereClauseKind.TYPEVAR)).put(var1, var6);
               RichDiagnosticFormatter.this.symbolPreprocessor.visit(var1.tsym.location(), (Object)null);
               this.visit(var4);
            } else {
               Assert.check(!var5);
               var6 = RichDiagnosticFormatter.this.diags.fragment("where.fresh.typevar", var1, var4);
               ((Map)RichDiagnosticFormatter.this.whereClauses.get(RichDiagnosticFormatter.WhereClauseKind.TYPEVAR)).put(var1, var6);
               this.visit(var4);
            }
         }

         return null;
      }
   };
   protected Types.DefaultSymbolVisitor symbolPreprocessor = new Types.DefaultSymbolVisitor() {
      public Void visitClassSymbol(Symbol.ClassSymbol var1, Void var2) {
         if (var1.type.isCompound()) {
            RichDiagnosticFormatter.this.typePreprocessor.visit(var1.type);
         } else {
            RichDiagnosticFormatter.this.nameSimplifier.addUsage(var1);
         }

         return null;
      }

      public Void visitSymbol(Symbol var1, Void var2) {
         return null;
      }

      public Void visitMethodSymbol(Symbol.MethodSymbol var1, Void var2) {
         this.visit(var1.owner, (Object)null);
         if (var1.type != null) {
            RichDiagnosticFormatter.this.typePreprocessor.visit(var1.type);
         }

         return null;
      }
   };

   public static RichDiagnosticFormatter instance(Context var0) {
      RichDiagnosticFormatter var1 = (RichDiagnosticFormatter)var0.get(RichDiagnosticFormatter.class);
      if (var1 == null) {
         var1 = new RichDiagnosticFormatter(var0);
      }

      return var1;
   }

   protected RichDiagnosticFormatter(Context var1) {
      super((AbstractDiagnosticFormatter)Log.instance(var1).getDiagnosticFormatter());
      this.setRichPrinter(new RichPrinter());
      this.syms = Symtab.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.types = Types.instance(var1);
      this.messages = JavacMessages.instance(var1);
      this.whereClauses = new EnumMap(WhereClauseKind.class);
      this.configuration = new RichConfiguration(Options.instance(var1), (AbstractDiagnosticFormatter)this.formatter);
      WhereClauseKind[] var2 = RichDiagnosticFormatter.WhereClauseKind.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WhereClauseKind var5 = var2[var4];
         this.whereClauses.put(var5, new LinkedHashMap());
      }

   }

   public String format(JCDiagnostic var1, Locale var2) {
      StringBuilder var3 = new StringBuilder();
      this.nameSimplifier = new ClassNameSimplifier();
      WhereClauseKind[] var4 = RichDiagnosticFormatter.WhereClauseKind.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WhereClauseKind var7 = var4[var6];
         ((Map)this.whereClauses.get(var7)).clear();
      }

      this.preprocessDiagnostic(var1);
      var3.append(((AbstractDiagnosticFormatter)this.formatter).format(var1, var2));
      if (this.getConfiguration().isEnabled(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.WHERE_CLAUSES)) {
         List var9 = this.getWhereClauses();
         String var10 = ((AbstractDiagnosticFormatter)this.formatter).isRaw() ? "" : ((AbstractDiagnosticFormatter)this.formatter).indentString(2);
         Iterator var11 = var9.iterator();

         while(var11.hasNext()) {
            JCDiagnostic var12 = (JCDiagnostic)var11.next();
            String var8 = ((AbstractDiagnosticFormatter)this.formatter).format(var12, var2);
            if (var8.length() > 0) {
               var3.append('\n' + var10 + var8);
            }
         }
      }

      return var3.toString();
   }

   public String formatMessage(JCDiagnostic var1, Locale var2) {
      this.nameSimplifier = new ClassNameSimplifier();
      this.preprocessDiagnostic(var1);
      return super.formatMessage(var1, var2);
   }

   protected void setRichPrinter(RichPrinter var1) {
      this.printer = var1;
      ((AbstractDiagnosticFormatter)this.formatter).setPrinter(var1);
   }

   protected RichPrinter getRichPrinter() {
      return this.printer;
   }

   protected void preprocessDiagnostic(JCDiagnostic var1) {
      Object[] var2 = var1.getArgs();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 != null) {
            this.preprocessArgument(var5);
         }
      }

      if (var1.isMultiline()) {
         Iterator var6 = var1.getSubdiagnostics().iterator();

         while(var6.hasNext()) {
            JCDiagnostic var7 = (JCDiagnostic)var6.next();
            this.preprocessDiagnostic(var7);
         }
      }

   }

   protected void preprocessArgument(Object var1) {
      if (var1 instanceof Type) {
         this.preprocessType((Type)var1);
      } else if (var1 instanceof Symbol) {
         this.preprocessSymbol((Symbol)var1);
      } else if (var1 instanceof JCDiagnostic) {
         this.preprocessDiagnostic((JCDiagnostic)var1);
      } else if (var1 instanceof Iterable) {
         Iterator var2 = ((Iterable)var1).iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            this.preprocessArgument(var3);
         }
      }

   }

   protected List getWhereClauses() {
      List var1 = List.nil();
      WhereClauseKind[] var2 = RichDiagnosticFormatter.WhereClauseKind.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WhereClauseKind var5 = var2[var4];
         List var6 = List.nil();

         Map.Entry var8;
         for(Iterator var7 = ((Map)this.whereClauses.get(var5)).entrySet().iterator(); var7.hasNext(); var6 = var6.prepend(var8.getValue())) {
            var8 = (Map.Entry)var7.next();
         }

         if (!var6.isEmpty()) {
            String var9 = var5.key();
            if (var6.size() > 1) {
               var9 = var9 + ".1";
            }

            JCDiagnostic var10 = this.diags.fragment(var9, ((Map)this.whereClauses.get(var5)).keySet());
            JCDiagnostic.MultilineDiagnostic var11 = new JCDiagnostic.MultilineDiagnostic(var10, var6.reverse());
            var1 = var1.prepend(var11);
         }
      }

      return var1.reverse();
   }

   private int indexOf(Type var1, WhereClauseKind var2) {
      int var3 = 1;
      Iterator var4 = ((Map)this.whereClauses.get(var2)).keySet().iterator();

      while(true) {
         Type var5;
         do {
            if (!var4.hasNext()) {
               return -1;
            }

            var5 = (Type)var4.next();
            if (var5.tsym == var1.tsym) {
               return var3;
            }
         } while(var2 == RichDiagnosticFormatter.WhereClauseKind.TYPEVAR && !var5.toString().equals(var1.toString()));

         ++var3;
      }
   }

   private boolean unique(Type.TypeVar var1) {
      int var2 = 0;
      Iterator var3 = ((Map)this.whereClauses.get(RichDiagnosticFormatter.WhereClauseKind.TYPEVAR)).keySet().iterator();

      while(var3.hasNext()) {
         Type var4 = (Type)var3.next();
         if (var4.toString().equals(var1.toString())) {
            ++var2;
         }
      }

      if (var2 < 1) {
         throw new AssertionError("Missing type variable in where clause " + var1);
      } else {
         return var2 == 1;
      }
   }

   protected void preprocessType(Type var1) {
      this.typePreprocessor.visit(var1);
   }

   protected void preprocessSymbol(Symbol var1) {
      this.symbolPreprocessor.visit(var1, (Object)null);
   }

   public RichConfiguration getConfiguration() {
      return (RichConfiguration)this.configuration;
   }

   public static class RichConfiguration extends ForwardingDiagnosticFormatter.ForwardingConfiguration {
      protected EnumSet features;

      public RichConfiguration(Options var1, AbstractDiagnosticFormatter var2) {
         super(var2.getConfiguration());
         this.features = var2.isRaw() ? EnumSet.noneOf(RichFormatterFeature.class) : EnumSet.of(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.SIMPLE_NAMES, RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.WHERE_CLAUSES, RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.UNIQUE_TYPEVAR_NAMES);
         String var3 = var1.get("diags");
         if (var3 != null) {
            String[] var4 = var3.split(",");
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               if (var7.equals("-where")) {
                  this.features.remove(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.WHERE_CLAUSES);
               } else if (var7.equals("where")) {
                  this.features.add(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.WHERE_CLAUSES);
               }

               if (var7.equals("-simpleNames")) {
                  this.features.remove(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.SIMPLE_NAMES);
               } else if (var7.equals("simpleNames")) {
                  this.features.add(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.SIMPLE_NAMES);
               }

               if (var7.equals("-disambiguateTvars")) {
                  this.features.remove(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.UNIQUE_TYPEVAR_NAMES);
               } else if (var7.equals("disambiguateTvars")) {
                  this.features.add(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.UNIQUE_TYPEVAR_NAMES);
               }
            }
         }

      }

      public RichFormatterFeature[] getAvailableFeatures() {
         return RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.values();
      }

      public void enable(RichFormatterFeature var1) {
         this.features.add(var1);
      }

      public void disable(RichFormatterFeature var1) {
         this.features.remove(var1);
      }

      public boolean isEnabled(RichFormatterFeature var1) {
         return this.features.contains(var1);
      }

      public static enum RichFormatterFeature {
         WHERE_CLAUSES,
         SIMPLE_NAMES,
         UNIQUE_TYPEVAR_NAMES;
      }
   }

   protected class RichPrinter extends Printer {
      public String localize(Locale var1, String var2, Object... var3) {
         return ((AbstractDiagnosticFormatter)RichDiagnosticFormatter.this.formatter).localize(var1, var2, var3);
      }

      public String capturedVarId(Type.CapturedType var1, Locale var2) {
         return RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.CAPTURED) + "";
      }

      public String visitType(Type var1, Locale var2) {
         String var3 = super.visitType(var1, var2);
         if (var1 == RichDiagnosticFormatter.this.syms.botType) {
            var3 = this.localize(var2, "compiler.misc.type.null");
         }

         return var3;
      }

      public String visitCapturedType(Type.CapturedType var1, Locale var2) {
         return RichDiagnosticFormatter.this.getConfiguration().isEnabled(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.WHERE_CLAUSES) ? this.localize(var2, "compiler.misc.captured.type", RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.CAPTURED)) : super.visitCapturedType(var1, var2);
      }

      public String visitClassType(Type.ClassType var1, Locale var2) {
         return var1.isCompound() && RichDiagnosticFormatter.this.getConfiguration().isEnabled(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.WHERE_CLAUSES) ? this.localize(var2, "compiler.misc.intersection.type", RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.INTERSECTION)) : super.visitClassType(var1, var2);
      }

      protected String className(Type.ClassType var1, boolean var2, Locale var3) {
         Symbol.TypeSymbol var4 = var1.tsym;
         if (var4.name.length() != 0 && RichDiagnosticFormatter.this.getConfiguration().isEnabled(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.SIMPLE_NAMES)) {
            return var2 ? RichDiagnosticFormatter.this.nameSimplifier.simplify(var4).toString() : var4.name.toString();
         } else {
            return super.className(var1, var2, var3);
         }
      }

      public String visitTypeVar(Type.TypeVar var1, Locale var2) {
         return !RichDiagnosticFormatter.this.unique(var1) && RichDiagnosticFormatter.this.getConfiguration().isEnabled(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.UNIQUE_TYPEVAR_NAMES) ? this.localize(var2, "compiler.misc.type.var", var1.toString(), RichDiagnosticFormatter.this.indexOf(var1, RichDiagnosticFormatter.WhereClauseKind.TYPEVAR)) : var1.toString();
      }

      public String visitClassSymbol(Symbol.ClassSymbol var1, Locale var2) {
         if (var1.type.isCompound()) {
            return this.visit(var1.type, var2);
         } else {
            String var3 = RichDiagnosticFormatter.this.nameSimplifier.simplify(var1);
            return var3.length() != 0 && RichDiagnosticFormatter.this.getConfiguration().isEnabled(RichDiagnosticFormatter.RichConfiguration.RichFormatterFeature.SIMPLE_NAMES) ? var3 : super.visitClassSymbol(var1, var2);
         }
      }

      public String visitMethodSymbol(Symbol.MethodSymbol var1, Locale var2) {
         String var3 = this.visit(var1.owner, var2);
         if (var1.isStaticOrInstanceInit()) {
            return var3;
         } else {
            String var4 = var1.name == var1.name.table.names.init ? var3 : var1.name.toString();
            if (var1.type != null) {
               if (var1.type.hasTag(TypeTag.FORALL)) {
                  var4 = "<" + this.visitTypes(var1.type.getTypeArguments(), var2) + ">" + var4;
               }

               var4 = var4 + "(" + this.printMethodArgs(var1.type.getParameterTypes(), (var1.flags() & 17179869184L) != 0L, var2) + ")";
            }

            return var4;
         }
      }
   }

   protected class ClassNameSimplifier {
      Map nameClashes = new HashMap();

      protected void addUsage(Symbol var1) {
         Name var2 = var1.getSimpleName();
         List var3 = (List)this.nameClashes.get(var2);
         if (var3 == null) {
            var3 = List.nil();
         }

         if (!var3.contains(var1)) {
            this.nameClashes.put(var2, var3.append(var1));
         }

      }

      public String simplify(Symbol var1) {
         String var2 = var1.getQualifiedName().toString();
         if (!var1.type.isCompound() && !var1.type.isPrimitive()) {
            List var3 = (List)this.nameClashes.get(var1.getSimpleName());
            if (var3 == null || var3.size() == 1 && var3.contains(var1)) {
               List var4 = List.nil();

               Symbol var5;
               for(var5 = var1; var5.type.hasTag(TypeTag.CLASS) && var5.type.getEnclosingType().hasTag(TypeTag.CLASS) && var5.owner.kind == 2; var5 = var5.owner) {
                  var4 = var4.prepend(var5.getSimpleName());
               }

               var4 = var4.prepend(var5.getSimpleName());
               StringBuilder var6 = new StringBuilder();
               String var7 = "";

               for(Iterator var8 = var4.iterator(); var8.hasNext(); var7 = ".") {
                  Name var9 = (Name)var8.next();
                  var6.append(var7);
                  var6.append(var9);
               }

               var2 = var6.toString();
            }
         }

         return var2;
      }
   }

   static enum WhereClauseKind {
      TYPEVAR("where.description.typevar"),
      CAPTURED("where.description.captured"),
      INTERSECTION("where.description.intersection");

      private final String key;

      private WhereClauseKind(String var3) {
         this.key = var3;
      }

      String key() {
         return this.key;
      }
   }
}
