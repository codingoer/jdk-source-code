package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeAnnotationPosition;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import java.util.Iterator;
import java.util.Map;

public class Annotate {
   protected static final Context.Key annotateKey = new Context.Key();
   final Attr attr;
   final TreeMaker make;
   final Log log;
   final Symtab syms;
   final Names names;
   final Resolve rs;
   final Types types;
   final ConstFold cfolder;
   final Check chk;
   private int enterCount = 0;
   ListBuffer q = new ListBuffer();
   ListBuffer typesQ = new ListBuffer();
   ListBuffer repeatedQ = new ListBuffer();
   ListBuffer afterRepeatedQ = new ListBuffer();
   ListBuffer validateQ = new ListBuffer();

   public static Annotate instance(Context var0) {
      Annotate var1 = (Annotate)var0.get(annotateKey);
      if (var1 == null) {
         var1 = new Annotate(var0);
      }

      return var1;
   }

   protected Annotate(Context var1) {
      var1.put((Context.Key)annotateKey, (Object)this);
      this.attr = Attr.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.names = Names.instance(var1);
      this.rs = Resolve.instance(var1);
      this.types = Types.instance(var1);
      this.cfolder = ConstFold.instance(var1);
      this.chk = Check.instance(var1);
   }

   public void earlier(Worker var1) {
      this.q.prepend(var1);
   }

   public void normal(Worker var1) {
      this.q.append(var1);
   }

   public void typeAnnotation(Worker var1) {
      this.typesQ.append(var1);
   }

   public void repeated(Worker var1) {
      this.repeatedQ.append(var1);
   }

   public void afterRepeated(Worker var1) {
      this.afterRepeatedQ.append(var1);
   }

   public void validate(Worker var1) {
      this.validateQ.append(var1);
   }

   public void enterStart() {
      ++this.enterCount;
   }

   public void enterDone() {
      --this.enterCount;
      this.flush();
   }

   public void enterDoneWithoutFlush() {
      --this.enterCount;
   }

   public void flush() {
      if (this.enterCount == 0) {
         ++this.enterCount;

         try {
            while(this.q.nonEmpty()) {
               ((Worker)this.q.next()).run();
            }

            while(this.typesQ.nonEmpty()) {
               ((Worker)this.typesQ.next()).run();
            }

            while(this.repeatedQ.nonEmpty()) {
               ((Worker)this.repeatedQ.next()).run();
            }

            while(this.afterRepeatedQ.nonEmpty()) {
               ((Worker)this.afterRepeatedQ.next()).run();
            }

            while(this.validateQ.nonEmpty()) {
               ((Worker)this.validateQ.next()).run();
            }
         } finally {
            --this.enterCount;
         }

      }
   }

   Attribute.Compound enterAnnotation(JCTree.JCAnnotation var1, Type var2, Env var3) {
      return this.enterAnnotation(var1, var2, var3, false);
   }

   Attribute.TypeCompound enterTypeAnnotation(JCTree.JCAnnotation var1, Type var2, Env var3) {
      return (Attribute.TypeCompound)this.enterAnnotation(var1, var2, var3, true);
   }

   Attribute.Compound enterAnnotation(JCTree.JCAnnotation var1, Type var2, Env var3, boolean var4) {
      Type var5 = var1.annotationType.type != null ? var1.annotationType.type : this.attr.attribType(var1.annotationType, var3);
      var1.type = this.chk.checkType(var1.annotationType.pos(), var5, var2);
      if (var1.type.isErroneous()) {
         this.attr.postAttr(var1);
         return (Attribute.Compound)(var4 ? new Attribute.TypeCompound(var1.type, List.nil(), new TypeAnnotationPosition()) : new Attribute.Compound(var1.type, List.nil()));
      } else if ((var1.type.tsym.flags() & 8192L) == 0L) {
         this.log.error(var1.annotationType.pos(), "not.annotation.type", new Object[]{var1.type.toString()});
         this.attr.postAttr(var1);
         return (Attribute.Compound)(var4 ? new Attribute.TypeCompound(var1.type, List.nil(), (TypeAnnotationPosition)null) : new Attribute.Compound(var1.type, List.nil()));
      } else {
         List var6 = var1.args;
         if (var6.length() == 1 && !((JCTree.JCExpression)var6.head).hasTag(JCTree.Tag.ASSIGN)) {
            var6.head = this.make.at(((JCTree.JCExpression)var6.head).pos).Assign(this.make.Ident(this.names.value), (JCTree.JCExpression)var6.head);
         }

         ListBuffer var7 = new ListBuffer();

         for(List var8 = var6; var8.nonEmpty(); var8 = var8.tail) {
            JCTree.JCExpression var9 = (JCTree.JCExpression)var8.head;
            if (!var9.hasTag(JCTree.Tag.ASSIGN)) {
               this.log.error(var9.pos(), "annotation.value.must.be.name.value", new Object[0]);
            } else {
               JCTree.JCAssign var10 = (JCTree.JCAssign)var9;
               if (!var10.lhs.hasTag(JCTree.Tag.IDENT)) {
                  this.log.error(var9.pos(), "annotation.value.must.be.name.value", new Object[0]);
               } else {
                  JCTree.JCIdent var11 = (JCTree.JCIdent)var10.lhs;
                  Symbol var12 = this.rs.resolveQualifiedMethod(var10.rhs.pos(), var3, var1.type, var11.name, List.nil(), (List)null);
                  var11.sym = var12;
                  var11.type = var12.type;
                  if (var12.owner != var1.type.tsym) {
                     this.log.error(var11.pos(), "no.annotation.member", new Object[]{var11.name, var1.type});
                  }

                  Type var13 = var12.type.getReturnType();
                  Attribute var14 = this.enterAttributeValue(var13, var10.rhs, var3);
                  if (!var12.type.isErroneous()) {
                     var7.append(new Pair((Symbol.MethodSymbol)var12, var14));
                  }

                  var9.type = var13;
               }
            }
         }

         if (var4) {
            if (var1.attribute != null && var1.attribute instanceof Attribute.TypeCompound) {
               return var1.attribute;
            } else {
               Attribute.TypeCompound var16 = new Attribute.TypeCompound(var1.type, var7.toList(), new TypeAnnotationPosition());
               var1.attribute = var16;
               return var16;
            }
         } else {
            Attribute.Compound var15 = new Attribute.Compound(var1.type, var7.toList());
            var1.attribute = var15;
            return var15;
         }
      }
   }

   Attribute enterAttributeValue(Type var1, JCTree.JCExpression var2, Env var3) {
      try {
         var1.tsym.complete();
      } catch (Symbol.CompletionFailure var7) {
         this.log.error(((JCTree.JCExpression)var2).pos(), "cant.resolve", new Object[]{Kinds.kindName(var7.sym), var7.sym});
         var1 = this.syms.errType;
      }

      JCTree.JCNewArray var4;
      if (var1.hasTag(TypeTag.ARRAY)) {
         if (!((JCTree.JCExpression)var2).hasTag(JCTree.Tag.NEWARRAY)) {
            var2 = this.make.at(((JCTree.JCExpression)var2).pos).NewArray((JCTree.JCExpression)null, List.nil(), List.of(var2));
         }

         var4 = (JCTree.JCNewArray)var2;
         if (var4.elemtype != null) {
            this.log.error(var4.elemtype.pos(), "new.not.allowed.in.annotation", new Object[0]);
         }

         ListBuffer var11 = new ListBuffer();

         for(List var12 = var4.elems; var12.nonEmpty(); var12 = var12.tail) {
            var11.append(this.enterAttributeValue(this.types.elemtype(var1), (JCTree.JCExpression)var12.head, var3));
         }

         var4.type = var1;
         return new Attribute.Array(var1, (Attribute[])var11.toArray(new Attribute[var11.length()]));
      } else if (!((JCTree.JCExpression)var2).hasTag(JCTree.Tag.NEWARRAY)) {
         if ((var1.tsym.flags() & 8192L) != 0L) {
            if (((JCTree.JCExpression)var2).hasTag(JCTree.Tag.ANNOTATION)) {
               return this.enterAnnotation((JCTree.JCAnnotation)var2, var1, var3);
            }

            this.log.error(((JCTree.JCExpression)var2).pos(), "annotation.value.must.be.annotation", new Object[0]);
            var1 = this.syms.errType;
         }

         if (((JCTree.JCExpression)var2).hasTag(JCTree.Tag.ANNOTATION)) {
            if (!var1.isErroneous()) {
               this.log.error(((JCTree.JCExpression)var2).pos(), "annotation.not.valid.for.type", new Object[]{var1});
            }

            this.enterAnnotation((JCTree.JCAnnotation)var2, this.syms.errType, var3);
            return new Attribute.Error(((JCTree.JCAnnotation)var2).annotationType.type);
         } else {
            Type var8;
            if (!var1.isPrimitive() && !this.types.isSameType(var1, this.syms.stringType)) {
               if (var1.tsym == this.syms.classType.tsym) {
                  var8 = this.attr.attribExpr((JCTree)var2, var3, var1);
                  if (var8.isErroneous()) {
                     if (TreeInfo.name((JCTree)var2) == this.names._class && ((JCTree.JCFieldAccess)var2).selected.type.isErroneous()) {
                        Name var10 = ((JCTree.JCFieldAccess)var2).selected.type.tsym.flatName();
                        return new Attribute.UnresolvedClass(var1, this.types.createErrorType(var10, this.syms.unknownSymbol, this.syms.classType));
                     } else {
                        return new Attribute.Error(var8.getOriginalType());
                     }
                  } else if (TreeInfo.name((JCTree)var2) != this.names._class) {
                     this.log.error(((JCTree.JCExpression)var2).pos(), "annotation.value.must.be.class.literal", new Object[0]);
                     return new Attribute.Error(this.syms.errType);
                  } else {
                     return new Attribute.Class(this.types, ((JCTree.JCFieldAccess)var2).selected.type);
                  }
               } else if (var1.hasTag(TypeTag.CLASS) && (var1.tsym.flags() & 16384L) != 0L) {
                  var8 = this.attr.attribExpr((JCTree)var2, var3, var1);
                  Symbol var9 = TreeInfo.symbol((JCTree)var2);
                  if (var9 != null && !TreeInfo.nonstaticSelect((JCTree)var2) && var9.kind == 4 && (var9.flags() & 16384L) != 0L) {
                     Symbol.VarSymbol var6 = (Symbol.VarSymbol)var9;
                     return new Attribute.Enum(var1, var6);
                  } else {
                     this.log.error(((JCTree.JCExpression)var2).pos(), "enum.annotation.must.be.enum.constant", new Object[0]);
                     return new Attribute.Error(var8.getOriginalType());
                  }
               } else {
                  if (!var1.isErroneous()) {
                     this.log.error(((JCTree.JCExpression)var2).pos(), "annotation.value.not.allowable.type", new Object[0]);
                  }

                  return new Attribute.Error(this.attr.attribExpr((JCTree)var2, var3, var1));
               }
            } else {
               var8 = this.attr.attribExpr((JCTree)var2, var3, var1);
               if (var8.isErroneous()) {
                  return new Attribute.Error(var8.getOriginalType());
               } else if (var8.constValue() == null) {
                  this.log.error(((JCTree.JCExpression)var2).pos(), "attribute.value.must.be.constant", new Object[0]);
                  return new Attribute.Error(var1);
               } else {
                  var8 = this.cfolder.coerce(var8, var1);
                  return new Attribute.Constant(var1, var8.constValue());
               }
            }
         }
      } else {
         if (!var1.isErroneous()) {
            this.log.error(((JCTree.JCExpression)var2).pos(), "annotation.value.not.allowable.type", new Object[0]);
         }

         var4 = (JCTree.JCNewArray)var2;
         if (var4.elemtype != null) {
            this.log.error(var4.elemtype.pos(), "new.not.allowed.in.annotation", new Object[0]);
         }

         for(List var5 = var4.elems; var5.nonEmpty(); var5 = var5.tail) {
            this.enterAttributeValue(this.syms.errType, (JCTree.JCExpression)var5.head, var3);
         }

         return new Attribute.Error(this.syms.errType);
      }
   }

   private Attribute.Compound processRepeatedAnnotations(List var1, AnnotateRepeatedContext var2, Symbol var3) {
      Attribute.Compound var4 = (Attribute.Compound)var1.head;
      List var5 = List.nil();
      Type var6 = null;
      Type.ArrayType var7 = null;
      Type var8 = null;
      Symbol.MethodSymbol var9 = null;
      Assert.check(!var1.isEmpty() && !var1.tail.isEmpty());
      int var10 = 0;

      for(List var11 = var1; !var11.isEmpty(); var11 = var11.tail) {
         ++var10;
         Assert.check(var10 > 1 || !var11.tail.isEmpty());
         Attribute.Compound var12 = (Attribute.Compound)var11.head;
         var6 = var12.type;
         if (var7 == null) {
            var7 = this.types.makeArrayType(var6);
         }

         boolean var13 = var10 > 1;
         Type var14 = this.getContainingType(var12, (JCDiagnostic.DiagnosticPosition)var2.pos.get(var12), var13);
         if (var14 != null) {
            Assert.check(var8 == null || var14 == var8);
            var8 = var14;
            var9 = this.validateContainer(var14, var6, (JCDiagnostic.DiagnosticPosition)var2.pos.get(var12));
            if (var9 != null) {
               var5 = var5.prepend(var12);
            }
         }
      }

      if (!var5.isEmpty()) {
         var5 = var5.reverse();
         TreeMaker var16 = this.make.at((JCDiagnostic.DiagnosticPosition)var2.pos.get(var4));
         Pair var17 = new Pair(var9, new Attribute.Array(var7, var5));
         if (var2.isTypeCompound) {
            Attribute.TypeCompound var19 = new Attribute.TypeCompound(var8, List.of(var17), ((Attribute.TypeCompound)var1.head).position);
            var19.setSynthesized(true);
            return var19;
         } else {
            Attribute.Compound var18 = new Attribute.Compound(var8, List.of(var17));
            JCTree.JCAnnotation var20 = var16.Annotation(var18);
            if (!this.chk.annotationApplicable(var20, var3)) {
               this.log.error(var20.pos(), "invalid.repeatable.annotation.incompatible.target", new Object[]{var8, var6});
            }

            if (!this.chk.validateAnnotationDeferErrors(var20)) {
               this.log.error(var20.pos(), "duplicate.annotation.invalid.repeated", new Object[]{var6});
            }

            var18 = this.enterAnnotation(var20, var8, var2.env);
            var18.setSynthesized(true);
            return var18;
         }
      } else {
         return null;
      }
   }

   private Type getContainingType(Attribute.Compound var1, JCDiagnostic.DiagnosticPosition var2, boolean var3) {
      Type var4 = var1.type;
      Symbol.TypeSymbol var5 = var4.tsym;
      Attribute.Compound var6 = var5.attribute(this.syms.repeatableType.tsym);
      if (var6 == null) {
         if (var3) {
            this.log.error(var2, "duplicate.annotation.missing.container", new Object[]{var4, this.syms.repeatableType});
         }

         return null;
      } else {
         return this.filterSame(this.extractContainingType(var6, var2, var5), var4);
      }
   }

   private Type filterSame(Type var1, Type var2) {
      if (var1 != null && var2 != null) {
         return this.types.isSameType(var1, var2) ? null : var1;
      } else {
         return var1;
      }
   }

   private Type extractContainingType(Attribute.Compound var1, JCDiagnostic.DiagnosticPosition var2, Symbol.TypeSymbol var3) {
      if (var1.values.isEmpty()) {
         this.log.error(var2, "invalid.repeatable.annotation", new Object[]{var3});
         return null;
      } else {
         Pair var4 = (Pair)var1.values.head;
         Name var5 = ((Symbol.MethodSymbol)var4.fst).name;
         if (var5 != this.names.value) {
            this.log.error(var2, "invalid.repeatable.annotation", new Object[]{var3});
            return null;
         } else if (!(var4.snd instanceof Attribute.Class)) {
            this.log.error(var2, "invalid.repeatable.annotation", new Object[]{var3});
            return null;
         } else {
            return ((Attribute.Class)var4.snd).getValue();
         }
      }
   }

   private Symbol.MethodSymbol validateContainer(Type var1, Type var2, JCDiagnostic.DiagnosticPosition var3) {
      Symbol.MethodSymbol var4 = null;
      boolean var5 = false;
      Scope var6 = var1.tsym.members();
      int var7 = 0;
      boolean var8 = false;
      Iterator var9 = var6.getElementsByName(this.names.value).iterator();

      while(true) {
         while(var9.hasNext()) {
            Symbol var10 = (Symbol)var9.next();
            ++var7;
            if (var7 == 1 && var10.kind == 16) {
               var4 = (Symbol.MethodSymbol)var10;
            } else {
               var8 = true;
            }
         }

         if (var8) {
            this.log.error(var3, "invalid.repeatable.annotation.multiple.values", new Object[]{var1, var7});
            return null;
         }

         if (var7 == 0) {
            this.log.error(var3, "invalid.repeatable.annotation.no.value", new Object[]{var1});
            return null;
         }

         if (var4.kind != 16) {
            this.log.error(var3, "invalid.repeatable.annotation.invalid.value", new Object[]{var1});
            var5 = true;
         }

         Type var11 = var4.type.getReturnType();
         Type.ArrayType var12 = this.types.makeArrayType(var2);
         if (!this.types.isArray(var11) || !this.types.isSameType(var12, var11)) {
            this.log.error(var3, "invalid.repeatable.annotation.value.return", new Object[]{var1, var11, var12});
            var5 = true;
         }

         if (var8) {
            var5 = true;
         }

         return var5 ? null : var4;
      }
   }

   public class AnnotateRepeatedContext {
      public final Env env;
      public final Map annotated;
      public final Map pos;
      public final Log log;
      public final boolean isTypeCompound;

      public AnnotateRepeatedContext(Env var2, Map var3, Map var4, Log var5, boolean var6) {
         Assert.checkNonNull(var2);
         Assert.checkNonNull(var3);
         Assert.checkNonNull(var4);
         Assert.checkNonNull(var5);
         this.env = var2;
         this.annotated = var3;
         this.pos = var4;
         this.log = var5;
         this.isTypeCompound = var6;
      }

      public Attribute.Compound processRepeatedAnnotations(List var1, Symbol var2) {
         return Annotate.this.processRepeatedAnnotations(var1, this, var2);
      }

      public void annotateRepeated(Worker var1) {
         Annotate.this.repeated(var1);
      }
   }

   public interface Worker {
      void run();

      String toString();
   }
}
