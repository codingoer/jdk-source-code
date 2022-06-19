package com.sun.tools.javac.code;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Pair;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lint {
   protected static final Context.Key lintKey = new Context.Key();
   private final AugmentVisitor augmentor;
   private final EnumSet values;
   private final EnumSet suppressedValues;
   private static final Map map = new ConcurrentHashMap(20);

   public static Lint instance(Context var0) {
      Lint var1 = (Lint)var0.get(lintKey);
      if (var1 == null) {
         var1 = new Lint(var0);
      }

      return var1;
   }

   public Lint augment(Attribute.Compound var1) {
      return this.augmentor.augment(this, var1);
   }

   public Lint augment(Symbol var1) {
      Lint var2 = this.augmentor.augment(this, var1.getDeclarationAttributes());
      if (var1.isDeprecated()) {
         if (var2 == this) {
            var2 = new Lint(this);
         }

         var2.values.remove(Lint.LintCategory.DEPRECATION);
         var2.suppressedValues.add(Lint.LintCategory.DEPRECATION);
      }

      return var2;
   }

   protected Lint(Context var1) {
      Options var2 = Options.instance(var1);
      this.values = EnumSet.noneOf(LintCategory.class);
      Iterator var3 = map.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         if (var2.lint((String)var4.getKey())) {
            this.values.add(var4.getValue());
         }
      }

      this.suppressedValues = EnumSet.noneOf(LintCategory.class);
      var1.put((Context.Key)lintKey, (Object)this);
      this.augmentor = new AugmentVisitor(var1);
   }

   protected Lint(Lint var1) {
      this.augmentor = var1.augmentor;
      this.values = var1.values.clone();
      this.suppressedValues = var1.suppressedValues.clone();
   }

   public String toString() {
      return "Lint:[values" + this.values + " suppressedValues" + this.suppressedValues + "]";
   }

   public boolean isEnabled(LintCategory var1) {
      return this.values.contains(var1);
   }

   public boolean isSuppressed(LintCategory var1) {
      return this.suppressedValues.contains(var1);
   }

   protected static class AugmentVisitor implements Attribute.Visitor {
      private final Context context;
      private Symtab syms;
      private Lint parent;
      private Lint lint;

      AugmentVisitor(Context var1) {
         this.context = var1;
      }

      Lint augment(Lint var1, Attribute.Compound var2) {
         this.initSyms();
         this.parent = var1;
         this.lint = null;
         var2.accept(this);
         return this.lint == null ? var1 : this.lint;
      }

      Lint augment(Lint var1, List var2) {
         this.initSyms();
         this.parent = var1;
         this.lint = null;
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Attribute.Compound var4 = (Attribute.Compound)var3.next();
            var4.accept(this);
         }

         return this.lint == null ? var1 : this.lint;
      }

      private void initSyms() {
         if (this.syms == null) {
            this.syms = Symtab.instance(this.context);
         }

      }

      private void suppress(LintCategory var1) {
         if (this.lint == null) {
            this.lint = new Lint(this.parent);
         }

         this.lint.suppressedValues.add(var1);
         this.lint.values.remove(var1);
      }

      public void visitConstant(Attribute.Constant var1) {
         if (var1.type.tsym == this.syms.stringType.tsym) {
            LintCategory var2 = Lint.LintCategory.get((String)((String)var1.value));
            if (var2 != null) {
               this.suppress(var2);
            }
         }

      }

      public void visitClass(Attribute.Class var1) {
      }

      public void visitCompound(Attribute.Compound var1) {
         if (var1.type.tsym == this.syms.suppressWarningsType.tsym) {
            for(List var2 = var1.values; var2.nonEmpty(); var2 = var2.tail) {
               Pair var3 = (Pair)var2.head;
               if (((Symbol.MethodSymbol)var3.fst).name.toString().equals("value")) {
                  ((Attribute)var3.snd).accept(this);
               }
            }
         }

      }

      public void visitArray(Attribute.Array var1) {
         Attribute[] var2 = var1.values;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Attribute var5 = var2[var4];
            var5.accept(this);
         }

      }

      public void visitEnum(Attribute.Enum var1) {
      }

      public void visitError(Attribute.Error var1) {
      }
   }

   public static enum LintCategory {
      AUXILIARYCLASS("auxiliaryclass"),
      CAST("cast"),
      CLASSFILE("classfile"),
      DEPRECATION("deprecation"),
      DEP_ANN("dep-ann"),
      DIVZERO("divzero"),
      EMPTY("empty"),
      FALLTHROUGH("fallthrough"),
      FINALLY("finally"),
      OPTIONS("options"),
      OVERLOADS("overloads"),
      OVERRIDES("overrides"),
      PATH("path"),
      PROCESSING("processing"),
      RAW("rawtypes"),
      SERIAL("serial"),
      STATIC("static"),
      SUNAPI("sunapi", true),
      TRY("try"),
      UNCHECKED("unchecked"),
      VARARGS("varargs");

      public final String option;
      public final boolean hidden;

      private LintCategory(String var3) {
         this(var3, false);
      }

      private LintCategory(String var3, boolean var4) {
         this.option = var3;
         this.hidden = var4;
         Lint.map.put(var3, this);
      }

      static LintCategory get(String var0) {
         return (LintCategory)Lint.map.get(var0);
      }
   }
}
