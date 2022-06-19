package com.sun.tools.javac.code;

import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import java.util.Iterator;
import java.util.Map;
import javax.tools.JavaFileObject;

public class SymbolMetadata {
   private static final List DECL_NOT_STARTED = List.of((Object)null);
   private static final List DECL_IN_PROGRESS = List.of((Object)null);
   private List attributes;
   private List type_attributes;
   private List init_type_attributes;
   private List clinit_type_attributes;
   private final Symbol sym;

   public SymbolMetadata(Symbol var1) {
      this.attributes = DECL_NOT_STARTED;
      this.type_attributes = List.nil();
      this.init_type_attributes = List.nil();
      this.clinit_type_attributes = List.nil();
      this.sym = var1;
   }

   public List getDeclarationAttributes() {
      return this.filterDeclSentinels(this.attributes);
   }

   public List getTypeAttributes() {
      return this.type_attributes;
   }

   public List getInitTypeAttributes() {
      return this.init_type_attributes;
   }

   public List getClassInitTypeAttributes() {
      return this.clinit_type_attributes;
   }

   public void setDeclarationAttributes(List var1) {
      Assert.check(this.pendingCompletion() || !this.isStarted());
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.attributes = var1;
      }
   }

   public void setTypeAttributes(List var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.type_attributes = var1;
      }
   }

   public void setInitTypeAttributes(List var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.init_type_attributes = var1;
      }
   }

   public void setClassInitTypeAttributes(List var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.clinit_type_attributes = var1;
      }
   }

   public void setAttributes(SymbolMetadata var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.setDeclarationAttributes(var1.getDeclarationAttributes());
         if ((this.sym.flags() & 2147483648L) != 0L) {
            Assert.check(var1.sym.kind == 16);
            ListBuffer var2 = new ListBuffer();
            Iterator var3 = var1.getTypeAttributes().iterator();

            while(var3.hasNext()) {
               Attribute.TypeCompound var4 = (Attribute.TypeCompound)var3.next();
               if (!var4.position.type.isLocal()) {
                  var2.append(var4);
               }
            }

            this.setTypeAttributes(var2.toList());
         } else {
            this.setTypeAttributes(var1.getTypeAttributes());
         }

         if (this.sym.kind == 2) {
            this.setInitTypeAttributes(var1.getInitTypeAttributes());
            this.setClassInitTypeAttributes(var1.getClassInitTypeAttributes());
         }

      }
   }

   public void setDeclarationAttributesWithCompletion(Annotate.AnnotateRepeatedContext var1) {
      Assert.check(this.pendingCompletion() || !this.isStarted() && this.sym.kind == 1);
      this.setDeclarationAttributes(this.getAttributesForCompletion(var1));
   }

   public void appendTypeAttributesWithCompletion(Annotate.AnnotateRepeatedContext var1) {
      this.appendUniqueTypes(this.getAttributesForCompletion(var1));
   }

   private List getAttributesForCompletion(final Annotate.AnnotateRepeatedContext var1) {
      Map var2 = var1.annotated;
      boolean var3 = false;
      List var4 = List.nil();
      Iterator var5 = var2.values().iterator();

      while(var5.hasNext()) {
         ListBuffer var6 = (ListBuffer)var5.next();
         if (var6.size() == 1) {
            var4 = var4.prepend(var6.first());
         } else {
            Placeholder var8 = new Placeholder(var1, var6.toList(), this.sym);
            var4 = var4.prepend(var8);
            var3 = true;
         }
      }

      if (var3) {
         var1.annotateRepeated(new Annotate.Worker() {
            public String toString() {
               return "repeated annotation pass of: " + SymbolMetadata.this.sym + " in: " + SymbolMetadata.this.sym.owner;
            }

            public void run() {
               SymbolMetadata.this.complete(var1);
            }
         });
      }

      return var4.reverse();
   }

   public SymbolMetadata reset() {
      this.attributes = DECL_IN_PROGRESS;
      return this;
   }

   public boolean isEmpty() {
      return !this.isStarted() || this.pendingCompletion() || this.attributes.isEmpty();
   }

   public boolean isTypesEmpty() {
      return this.type_attributes.isEmpty();
   }

   public boolean pendingCompletion() {
      return this.attributes == DECL_IN_PROGRESS;
   }

   public SymbolMetadata append(List var1) {
      this.attributes = this.filterDeclSentinels(this.attributes);
      if (!var1.isEmpty()) {
         if (this.attributes.isEmpty()) {
            this.attributes = var1;
         } else {
            this.attributes = this.attributes.appendList(var1);
         }
      }

      return this;
   }

   public SymbolMetadata appendUniqueTypes(List var1) {
      if (!var1.isEmpty()) {
         if (this.type_attributes.isEmpty()) {
            this.type_attributes = var1;
         } else {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Attribute.TypeCompound var3 = (Attribute.TypeCompound)var2.next();
               if (!this.type_attributes.contains(var3)) {
                  this.type_attributes = this.type_attributes.append(var3);
               }
            }
         }
      }

      return this;
   }

   public SymbolMetadata appendInitTypeAttributes(List var1) {
      if (!var1.isEmpty()) {
         if (this.init_type_attributes.isEmpty()) {
            this.init_type_attributes = var1;
         } else {
            this.init_type_attributes = this.init_type_attributes.appendList(var1);
         }
      }

      return this;
   }

   public SymbolMetadata appendClassInitTypeAttributes(List var1) {
      if (!var1.isEmpty()) {
         if (this.clinit_type_attributes.isEmpty()) {
            this.clinit_type_attributes = var1;
         } else {
            this.clinit_type_attributes = this.clinit_type_attributes.appendList(var1);
         }
      }

      return this;
   }

   public SymbolMetadata prepend(List var1) {
      this.attributes = this.filterDeclSentinels(this.attributes);
      if (!var1.isEmpty()) {
         if (this.attributes.isEmpty()) {
            this.attributes = var1;
         } else {
            this.attributes = this.attributes.prependList(var1);
         }
      }

      return this;
   }

   private List filterDeclSentinels(List var1) {
      return var1 != DECL_IN_PROGRESS && var1 != DECL_NOT_STARTED ? var1 : List.nil();
   }

   private boolean isStarted() {
      return this.attributes != DECL_NOT_STARTED;
   }

   private List getPlaceholders() {
      List var1 = List.nil();
      Iterator var2 = this.filterDeclSentinels(this.attributes).iterator();

      while(var2.hasNext()) {
         Attribute.Compound var3 = (Attribute.Compound)var2.next();
         if (var3 instanceof Placeholder) {
            var1 = var1.prepend(var3);
         }
      }

      return var1.reverse();
   }

   private List getTypePlaceholders() {
      List var1 = List.nil();
      Iterator var2 = this.type_attributes.iterator();

      while(var2.hasNext()) {
         Attribute.TypeCompound var3 = (Attribute.TypeCompound)var2.next();
         if (var3 instanceof Placeholder) {
            var1 = var1.prepend(var3);
         }
      }

      return var1.reverse();
   }

   private void complete(Annotate.AnnotateRepeatedContext var1) {
      Log var2 = var1.log;
      Env var3 = var1.env;
      JavaFileObject var4 = var2.useSource(var3.toplevel.sourcefile);

      try {
         List var5;
         Iterator var6;
         if (var1.isTypeCompound) {
            Assert.check(!this.isTypesEmpty());
            if (this.isTypesEmpty()) {
               return;
            }

            var5 = List.nil();
            var6 = this.getTypeAttributes().iterator();

            while(var6.hasNext()) {
               Attribute.TypeCompound var7 = (Attribute.TypeCompound)var6.next();
               if (var7 instanceof Placeholder) {
                  Placeholder var8 = (Placeholder)var7;
                  Attribute.TypeCompound var9 = (Attribute.TypeCompound)this.replaceOne(var8, var8.getRepeatedContext());
                  if (null != var9) {
                     var5 = var5.prepend(var9);
                  }
               } else {
                  var5 = var5.prepend(var7);
               }
            }

            this.type_attributes = var5.reverse();
            Assert.check(this.getTypePlaceholders().isEmpty());
         } else {
            Assert.check(!this.pendingCompletion());
            if (this.isEmpty()) {
               return;
            }

            var5 = List.nil();
            var6 = this.getDeclarationAttributes().iterator();

            while(var6.hasNext()) {
               Attribute.Compound var13 = (Attribute.Compound)var6.next();
               if (var13 instanceof Placeholder) {
                  Attribute.Compound var14 = this.replaceOne((Placeholder)var13, var1);
                  if (null != var14) {
                     var5 = var5.prepend(var14);
                  }
               } else {
                  var5 = var5.prepend(var13);
               }
            }

            this.attributes = var5.reverse();
            Assert.check(this.getPlaceholders().isEmpty());
         }
      } finally {
         var2.useSource(var4);
      }

   }

   private Attribute.Compound replaceOne(Placeholder var1, Annotate.AnnotateRepeatedContext var2) {
      Log var3 = var2.log;
      Attribute.Compound var4 = var2.processRepeatedAnnotations(var1.getPlaceholderFor(), this.sym);
      if (var4 != null) {
         ListBuffer var5 = (ListBuffer)var2.annotated.get(var4.type.tsym);
         if (var5 != null) {
            var3.error((JCDiagnostic.DiagnosticPosition)var2.pos.get(var5.first()), "invalid.repeatable.annotation.repeated.and.container.present", new Object[]{((Attribute.Compound)var5.first()).type.tsym});
         }
      }

      return var4;
   }

   private static class Placeholder extends Attribute.TypeCompound {
      private final Annotate.AnnotateRepeatedContext ctx;
      private final List placeholderFor;
      private final Symbol on;

      public Placeholder(Annotate.AnnotateRepeatedContext var1, List var2, Symbol var3) {
         super(var3.type, List.nil(), var1.isTypeCompound ? ((Attribute.TypeCompound)var2.head).position : new TypeAnnotationPosition());
         this.ctx = var1;
         this.placeholderFor = var2;
         this.on = var3;
      }

      public String toString() {
         return "<placeholder: " + this.placeholderFor + " on: " + this.on + ">";
      }

      public List getPlaceholderFor() {
         return this.placeholderFor;
      }

      public Annotate.AnnotateRepeatedContext getRepeatedContext() {
         return this.ctx;
      }
   }
}
