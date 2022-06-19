package sun.tools.tree;

import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;

public class Context implements Constants {
   Context prev;
   Node node;
   int varNumber;
   LocalMember locals;
   LocalMember classes;
   MemberDefinition field;
   int scopeNumber;
   int frameNumber;

   public Context(Context var1, MemberDefinition var2) {
      this.field = var2;
      if (var1 == null) {
         this.frameNumber = 1;
         this.scopeNumber = 2;
         this.varNumber = 0;
      } else {
         this.prev = var1;
         this.locals = var1.locals;
         this.classes = var1.classes;
         if (var2 == null || !var2.isVariable() && !var2.isInitializer()) {
            this.frameNumber = var1.scopeNumber + 1;
            this.scopeNumber = this.frameNumber + 1;
         } else {
            this.frameNumber = var1.frameNumber;
            this.scopeNumber = var1.scopeNumber + 1;
         }

         this.varNumber = var1.varNumber;
      }

   }

   public Context(Context var1, ClassDefinition var2) {
      this(var1, (MemberDefinition)null);
   }

   Context(Context var1, Node var2) {
      if (var1 == null) {
         this.frameNumber = 1;
         this.scopeNumber = 2;
         this.varNumber = 0;
      } else {
         this.prev = var1;
         this.locals = var1.locals;
         this.classes = var1.classes;
         this.varNumber = var1.varNumber;
         this.field = var1.field;
         this.frameNumber = var1.frameNumber;
         this.scopeNumber = var1.scopeNumber + 1;
         this.node = var2;
      }

   }

   public Context(Context var1) {
      this(var1, (Node)null);
   }

   public int declare(Environment var1, LocalMember var2) {
      var2.scopeNumber = this.scopeNumber;
      if (this.field == null && idThis.equals(var2.getName())) {
         ++var2.scopeNumber;
      }

      if (var2.isInnerClass()) {
         var2.prev = this.classes;
         this.classes = var2;
         return 0;
      } else {
         var2.prev = this.locals;
         this.locals = var2;
         var2.number = this.varNumber;
         this.varNumber += var2.getType().stackSize();
         return var2.number;
      }
   }

   public LocalMember getLocalField(Identifier var1) {
      for(LocalMember var2 = this.locals; var2 != null; var2 = var2.prev) {
         if (var1.equals(var2.getName())) {
            return var2;
         }
      }

      return null;
   }

   public int getScopeNumber(ClassDefinition var1) {
      for(Context var2 = this; var2 != null; var2 = var2.prev) {
         if (var2.field != null && var2.field.getClassDefinition() == var1) {
            return var2.frameNumber;
         }
      }

      return -1;
   }

   private MemberDefinition getFieldCommon(Environment var1, Identifier var2, boolean var3) throws AmbiguousMember, ClassNotFound {
      LocalMember var4 = this.getLocalField(var2);
      int var5 = var4 == null ? -2 : var4.scopeNumber;
      ClassDefinition var6 = this.field.getClassDefinition();

      for(ClassDefinition var7 = var6; var7 != null; var7 = var7.getOuterClass()) {
         MemberDefinition var8 = var7.getVariable(var1, var2, var6);
         if (var8 != null && this.getScopeNumber(var7) > var5 && (!var3 || var8.getClassDefinition() == var7)) {
            return var8;
         }
      }

      return var4;
   }

   public int declareFieldNumber(MemberDefinition var1) {
      return this.declare((Environment)null, new LocalMember(var1));
   }

   public int getFieldNumber(MemberDefinition var1) {
      for(LocalMember var2 = this.locals; var2 != null; var2 = var2.prev) {
         if (var2.getMember() == var1) {
            return var2.number;
         }
      }

      return -1;
   }

   public MemberDefinition getElement(int var1) {
      for(LocalMember var2 = this.locals; var2 != null; var2 = var2.prev) {
         if (var2.number == var1) {
            MemberDefinition var3 = var2.getMember();
            return (MemberDefinition)(var3 != null ? var3 : var2);
         }
      }

      return null;
   }

   public LocalMember getLocalClass(Identifier var1) {
      for(LocalMember var2 = this.classes; var2 != null; var2 = var2.prev) {
         if (var1.equals(var2.getName())) {
            return var2;
         }
      }

      return null;
   }

   private MemberDefinition getClassCommon(Environment var1, Identifier var2, boolean var3) throws ClassNotFound {
      LocalMember var4 = this.getLocalClass(var2);
      int var5 = var4 == null ? -2 : var4.scopeNumber;

      for(ClassDefinition var6 = this.field.getClassDefinition(); var6 != null; var6 = var6.getOuterClass()) {
         MemberDefinition var7 = var6.getInnerClass(var1, var2);
         if (var7 != null && this.getScopeNumber(var6) > var5 && (!var3 || var7.getClassDefinition() == var6)) {
            return var7;
         }
      }

      return var4;
   }

   public final MemberDefinition getField(Environment var1, Identifier var2) throws AmbiguousMember, ClassNotFound {
      return this.getFieldCommon(var1, var2, false);
   }

   public final MemberDefinition getApparentField(Environment var1, Identifier var2) throws AmbiguousMember, ClassNotFound {
      return this.getFieldCommon(var1, var2, true);
   }

   public boolean isInScope(LocalMember var1) {
      for(LocalMember var2 = this.locals; var2 != null; var2 = var2.prev) {
         if (var1 == var2) {
            return true;
         }
      }

      return false;
   }

   public UplevelReference noteReference(Environment var1, LocalMember var2) {
      int var3 = !this.isInScope(var2) ? -1 : var2.scopeNumber;
      UplevelReference var4 = null;
      int var5 = -1;

      for(Context var6 = this; var6 != null; var6 = var6.prev) {
         if (var5 != var6.frameNumber) {
            var5 = var6.frameNumber;
            if (var3 >= var5) {
               break;
            }

            ClassDefinition var7 = var6.field.getClassDefinition();
            UplevelReference var8 = var7.getReference(var2);
            var8.noteReference(var1, var6);
            if (var4 == null) {
               var4 = var8;
            }
         }
      }

      return var4;
   }

   public Expression makeReference(Environment var1, LocalMember var2) {
      UplevelReference var3 = this.noteReference(var1, var2);
      if (var3 != null) {
         return var3.makeLocalReference(var1, this);
      } else {
         return (Expression)(idThis.equals(var2.getName()) ? new ThisExpression(0L, var2) : new IdentifierExpression(0L, var2));
      }
   }

   public Expression findOuterLink(Environment var1, long var2, MemberDefinition var4) {
      ClassDefinition var5 = var4.getClassDefinition();
      ClassDefinition var6 = var4.isStatic() ? null : (!var4.isConstructor() ? var5 : (var5.isTopLevel() ? null : var5.getOuterClass()));
      return var6 == null ? null : this.findOuterLink(var1, var2, var6, var4, false);
   }

   private static boolean match(Environment var0, ClassDefinition var1, ClassDefinition var2) {
      try {
         return var1 == var2 || var2.implementedBy(var0, var1.getClassDeclaration());
      } catch (ClassNotFound var4) {
         return false;
      }
   }

   public Expression findOuterLink(Environment var1, long var2, ClassDefinition var4, MemberDefinition var5, boolean var6) {
      if (this.field.isStatic()) {
         if (var5 == null) {
            Identifier var16 = var4.getName().getFlatName().getName();
            var1.error(var2, "undef.var", Identifier.lookup(var16, idThis));
         } else if (var5.isConstructor()) {
            var1.error(var2, "no.outer.arg", var4, var5.getClassDeclaration());
         } else if (var5.isMethod()) {
            var1.error(var2, "no.static.meth.access", var5, var5.getClassDeclaration());
         } else {
            var1.error(var2, "no.static.field.access", var5.getName(), var5.getClassDeclaration());
         }

         ThisExpression var17 = new ThisExpression(var2, this);
         var17.type = var4.getType();
         return var17;
      } else {
         LocalMember var7 = this.locals;
         Object var8 = null;
         Object var9 = null;
         ClassDefinition var10 = null;
         ClassDefinition var11 = null;
         if (this.field.isConstructor()) {
            var11 = this.field.getClassDefinition();
         }

         if (!this.field.isMethod()) {
            var10 = this.field.getClassDefinition();
            var8 = new ThisExpression(var2, this);
         }

         label75:
         while(true) {
            if (var8 == null) {
               while(true) {
                  if (var7 == null || idThis.equals(var7.getName())) {
                     if (var7 == null) {
                        break label75;
                     }

                     var8 = new ThisExpression(var2, var7);
                     var10 = var7.getClassDefinition();
                     var7 = var7.prev;
                     break;
                  }

                  var7 = var7.prev;
               }
            }

            if (var10 == var4 || !var6 && match(var1, var10, var4)) {
               break;
            }

            MemberDefinition var12 = var10.findOuterMember();
            if (var12 == null) {
               var8 = null;
            } else {
               ClassDefinition var13 = var10;
               var10 = var10.getOuterClass();
               if (var13 == var11) {
                  Identifier var14 = var12.getName();
                  IdentifierExpression var15 = new IdentifierExpression(var2, var14);
                  var15.bind(var1, this);
                  var8 = var15;
               } else {
                  var8 = new FieldExpression(var2, (Expression)var8, var12);
               }
            }
         }

         if (var8 != null) {
            return (Expression)var8;
         } else {
            if (var5 == null) {
               Identifier var18 = var4.getName().getFlatName().getName();
               var1.error(var2, "undef.var", Identifier.lookup(var18, idThis));
            } else if (var5.isConstructor()) {
               var1.error(var2, "no.outer.arg", var4, var5.getClassDefinition());
            } else {
               var1.error(var2, "no.static.field.access", var5, this.field);
            }

            ThisExpression var19 = new ThisExpression(var2, this);
            var19.type = var4.getType();
            return var19;
         }
      }
   }

   public static boolean outerLinkExists(Environment var0, ClassDefinition var1, ClassDefinition var2) {
      while(!match(var0, var2, var1)) {
         if (var2.isTopLevel()) {
            return false;
         }

         var2 = var2.getOuterClass();
      }

      return true;
   }

   public ClassDefinition findScope(Environment var1, ClassDefinition var2) {
      ClassDefinition var3;
      for(var3 = this.field.getClassDefinition(); var3 != null && !match(var1, var3, var2); var3 = var3.getOuterClass()) {
      }

      return var3;
   }

   Identifier resolveName(Environment var1, Identifier var2) {
      if (var2.isQualified()) {
         Identifier var7 = this.resolveName(var1, var2.getHead());
         if (var7.hasAmbigPrefix()) {
            return var7;
         } else if (!var1.classExists(var7)) {
            return var1.resolvePackageQualifiedName(var2);
         } else {
            try {
               return var1.getClassDefinition(var7).resolveInnerClass(var1, var2.getTail());
            } catch (ClassNotFound var5) {
               return Identifier.lookupInner(var7, var2.getTail());
            }
         }
      } else {
         try {
            MemberDefinition var3 = this.getClassCommon(var1, var2, false);
            if (var3 != null) {
               return var3.getInnerClass().getName();
            }
         } catch (ClassNotFound var6) {
         }

         return var1.resolveName(var2);
      }
   }

   public Identifier getApparentClassName(Environment var1, Identifier var2) {
      Identifier var5;
      if (var2.isQualified()) {
         var5 = this.getApparentClassName(var1, var2.getHead());
         return var5 == null ? idNull : Identifier.lookup(var5, var2.getTail());
      } else {
         try {
            MemberDefinition var3 = this.getClassCommon(var1, var2, true);
            if (var3 != null) {
               return var3.getInnerClass().getName();
            }
         } catch (ClassNotFound var4) {
         }

         var5 = this.field.getClassDefinition().getTopClass().getName();
         return var5.getName().equals(var2) ? var5 : idNull;
      }
   }

   public void checkBackBranch(Environment var1, Statement var2, Vset var3, Vset var4) {
      for(LocalMember var5 = this.locals; var5 != null; var5 = var5.prev) {
         if (var5.isBlankFinal() && var3.testVarUnassigned(var5.number) && !var4.testVarUnassigned(var5.number)) {
            var1.error(var2.where, "assign.to.blank.final.in.loop", var5.getName());
         }
      }

   }

   public boolean canReach(Environment var1, MemberDefinition var2) {
      return this.field.canReach(var1, var2);
   }

   public Context getLabelContext(Identifier var1) {
      for(Context var2 = this; var2 != null; var2 = var2.prev) {
         if (var2.node != null && var2.node instanceof Statement && ((Statement)((Statement)var2.node)).hasLabel(var1)) {
            return var2;
         }
      }

      return null;
   }

   public Context getBreakContext(Identifier var1) {
      if (var1 != null) {
         return this.getLabelContext(var1);
      } else {
         for(Context var2 = this; var2 != null; var2 = var2.prev) {
            if (var2.node != null) {
               switch (var2.node.op) {
                  case 92:
                  case 93:
                  case 94:
                  case 95:
                     return var2;
               }
            }
         }

         return null;
      }
   }

   public Context getContinueContext(Identifier var1) {
      if (var1 != null) {
         return this.getLabelContext(var1);
      } else {
         for(Context var2 = this; var2 != null; var2 = var2.prev) {
            if (var2.node != null) {
               switch (var2.node.op) {
                  case 92:
                  case 93:
                  case 94:
                     return var2;
               }
            }
         }

         return null;
      }
   }

   public CheckContext getReturnContext() {
      for(Context var1 = this; var1 != null; var1 = var1.prev) {
         if (var1.node != null && var1.node.op == 47) {
            return (CheckContext)var1;
         }
      }

      return null;
   }

   public CheckContext getTryExitContext() {
      for(Context var1 = this; var1 != null && var1.node != null && var1.node.op != 47; var1 = var1.prev) {
         if (var1.node.op == 101) {
            return (CheckContext)var1;
         }
      }

      return null;
   }

   Context getInlineContext() {
      for(Context var1 = this; var1 != null; var1 = var1.prev) {
         if (var1.node != null) {
            switch (var1.node.op) {
               case 150:
               case 151:
                  return var1;
            }
         }
      }

      return null;
   }

   Context getInlineMemberContext(MemberDefinition var1) {
      for(Context var2 = this; var2 != null; var2 = var2.prev) {
         if (var2.node != null) {
            switch (var2.node.op) {
               case 150:
                  if (((InlineMethodExpression)var2.node).field.equals(var1)) {
                     return var2;
                  }
                  break;
               case 151:
                  if (((InlineNewInstanceExpression)var2.node).field.equals(var1)) {
                     return var2;
                  }
            }
         }
      }

      return null;
   }

   public final Vset removeAdditionalVars(Vset var1) {
      return var1.removeAdditionalVars(this.varNumber);
   }

   public final int getVarNumber() {
      return this.varNumber;
   }

   public int getThisNumber() {
      LocalMember var1 = this.getLocalField(idThis);
      return var1 != null && var1.getClassDefinition() == this.field.getClassDefinition() ? var1.number : this.varNumber;
   }

   public final MemberDefinition getField() {
      return this.field;
   }

   public static Environment newEnvironment(Environment var0, Context var1) {
      return new ContextEnvironment(var0, var1);
   }
}
