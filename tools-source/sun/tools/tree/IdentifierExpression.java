package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.LocalVariable;
import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class IdentifierExpression extends Expression {
   Identifier id;
   MemberDefinition field;
   Expression implementation;

   public IdentifierExpression(long var1, Identifier var3) {
      super(60, var1, Type.tError);
      this.id = var3;
   }

   public IdentifierExpression(IdentifierToken var1) {
      this(var1.getWhere(), var1.getName());
   }

   public IdentifierExpression(long var1, MemberDefinition var3) {
      super(60, var1, var3.getType());
      this.id = var3.getName();
      this.field = var3;
   }

   public Expression getImplementation() {
      return (Expression)(this.implementation != null ? this.implementation : this);
   }

   public boolean equals(Identifier var1) {
      return this.id.equals(var1);
   }

   private Vset assign(Environment var1, Context var2, Vset var3) {
      if (this.field.isLocal()) {
         LocalMember var4 = (LocalMember)this.field;
         if (var4.scopeNumber < var2.frameNumber) {
            var1.error(this.where, "assign.to.uplevel", this.id);
         }

         if (var4.isFinal()) {
            if (!var4.isBlankFinal()) {
               var1.error(this.where, "assign.to.final", this.id);
            } else if (!var3.testVarUnassigned(var4.number)) {
               var1.error(this.where, "assign.to.blank.final", this.id);
            }
         }

         var3.addVar(var4.number);
         ++var4.writecount;
      } else if (this.field.isFinal()) {
         var3 = FieldExpression.checkFinalAssign(var1, var2, var3, this.where, this.field);
      }

      return var3;
   }

   private Vset get(Environment var1, Context var2, Vset var3) {
      if (this.field.isLocal()) {
         LocalMember var4 = (LocalMember)this.field;
         if (var4.scopeNumber < var2.frameNumber && !var4.isFinal()) {
            var1.error(this.where, "invalid.uplevel", this.id);
         }

         if (!var3.testVar(var4.number)) {
            var1.error(this.where, "var.not.initialized", this.id);
            var3.addVar(var4.number);
         }

         ++var4.readcount;
      } else {
         if (!this.field.isStatic() && !var3.testVar(var2.getThisNumber())) {
            var1.error(this.where, "access.inst.before.super", this.id);
            this.implementation = null;
         }

         if (this.field.isBlankFinal()) {
            int var5 = var2.getFieldNumber(this.field);
            if (var5 >= 0 && !var3.testVar(var5)) {
               var1.error(this.where, "var.not.initialized", this.id);
            }
         }
      }

      return var3;
   }

   boolean bind(Environment var1, Context var2) {
      try {
         this.field = var2.getField(var1, this.id);
         if (this.field == null) {
            for(ClassDefinition var10 = var2.field.getClassDefinition(); var10 != null; var10 = var10.getOuterClass()) {
               if (var10.findAnyMethod(var1, this.id) != null) {
                  var1.error(this.where, "invalid.var", this.id, var2.field.getClassDeclaration());
                  return false;
               }
            }

            var1.error(this.where, "undef.var", this.id);
            return false;
         }

         this.type = this.field.getType();
         if (!var2.field.getClassDefinition().canAccess(var1, this.field)) {
            var1.error(this.where, "no.field.access", this.id, this.field.getClassDeclaration(), var2.field.getClassDeclaration());
            return false;
         }

         if (this.field.isLocal()) {
            LocalMember var3 = (LocalMember)this.field;
            if (var3.scopeNumber < var2.frameNumber) {
               this.implementation = var2.makeReference(var1, var3);
            }
         } else {
            MemberDefinition var9 = this.field;
            if (var9.reportDeprecated(var1)) {
               var1.error(this.where, "warn.field.is.deprecated", this.id, var9.getClassDefinition());
            }

            ClassDefinition var4 = var9.getClassDefinition();
            if (var4 != var2.field.getClassDefinition()) {
               MemberDefinition var5 = var2.getApparentField(var1, this.id);
               if (var5 != null && var5 != var9) {
                  ClassDefinition var6 = var2.findScope(var1, var4);
                  if (var6 == null) {
                     var6 = var9.getClassDefinition();
                  }

                  if (var5.isLocal()) {
                     var1.error(this.where, "inherited.hides.local", this.id, var6.getClassDeclaration());
                  } else {
                     var1.error(this.where, "inherited.hides.field", this.id, var6.getClassDeclaration(), var5.getClassDeclaration());
                  }
               }
            }

            if (var9.isStatic()) {
               new TypeExpression(this.where, var9.getClassDeclaration().getType());
               this.implementation = new FieldExpression(this.where, (Expression)null, var9);
            } else {
               Expression var11 = var2.findOuterLink(var1, this.where, var9);
               if (var11 != null) {
                  this.implementation = new FieldExpression(this.where, var11, var9);
               }
            }
         }

         if (!var2.canReach(var1, this.field)) {
            var1.error(this.where, "forward.ref", this.id, this.field.getClassDeclaration());
            return false;
         }

         return true;
      } catch (ClassNotFound var7) {
         var1.error(this.where, "class.not.found", var7.name, var2.field);
      } catch (AmbiguousMember var8) {
         var1.error(this.where, "ambig.field", this.id, var8.field1.getClassDeclaration(), var8.field2.getClassDeclaration());
      }

      return false;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      if (this.field != null) {
         return var3;
      } else {
         if (this.bind(var1, var2)) {
            var3 = this.get(var1, var2, var3);
            var2.field.getClassDefinition().addDependency(this.field.getClassDeclaration());
            if (this.implementation != null) {
               var3 = this.implementation.checkValue(var1, var2, var3, var4);
            }
         }

         return var3;
      }
   }

   public Vset checkLHS(Environment var1, Context var2, Vset var3, Hashtable var4) {
      if (!this.bind(var1, var2)) {
         return var3;
      } else {
         var3 = this.assign(var1, var2, var3);
         if (this.implementation != null) {
            var3 = this.implementation.checkValue(var1, var2, var3, var4);
         }

         return var3;
      }
   }

   public Vset checkAssignOp(Environment var1, Context var2, Vset var3, Hashtable var4, Expression var5) {
      if (!this.bind(var1, var2)) {
         return var3;
      } else {
         var3 = this.assign(var1, var2, this.get(var1, var2, var3));
         if (this.implementation != null) {
            var3 = this.implementation.checkValue(var1, var2, var3, var4);
         }

         return var3;
      }
   }

   public FieldUpdater getAssigner(Environment var1, Context var2) {
      return this.implementation != null ? this.implementation.getAssigner(var1, var2) : null;
   }

   public FieldUpdater getUpdater(Environment var1, Context var2) {
      return this.implementation != null ? this.implementation.getUpdater(var1, var2) : null;
   }

   public Vset checkAmbigName(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      try {
         if (var2.getField(var1, this.id) != null) {
            return this.checkValue(var1, var2, var3, var4);
         }
      } catch (ClassNotFound var7) {
      } catch (AmbiguousMember var8) {
      }

      ClassDefinition var6 = this.toResolvedType(var1, var2, true);
      if (var6 != null) {
         var5.right = new TypeExpression(this.where, var6.getType());
         return var3;
      } else {
         this.type = Type.tPackage;
         return var3;
      }
   }

   private ClassDefinition toResolvedType(Environment var1, Context var2, boolean var3) {
      Identifier var4 = var2.resolveName(var1, this.id);
      Type var5 = Type.tClass(var4);
      if (var3 && !var1.classExists(var5)) {
         return null;
      } else {
         if (var1.resolve(this.where, var2.field.getClassDefinition(), var5)) {
            try {
               ClassDefinition var6 = var1.getClassDefinition(var5);
               if (var6.isMember()) {
                  ClassDefinition var7 = var2.findScope(var1, var6.getOuterClass());
                  if (var7 != var6.getOuterClass()) {
                     Identifier var8 = var2.getApparentClassName(var1, this.id);
                     if (!var8.equals(idNull) && !var8.equals(var4)) {
                        var1.error(this.where, "inherited.hides.type", this.id, var7.getClassDeclaration());
                     }
                  }
               }

               if (!var6.getLocalName().equals(this.id.getFlatName().getName())) {
                  var1.error(this.where, "illegal.mangled.name", this.id, var6);
               }

               return var6;
            } catch (ClassNotFound var9) {
            }
         }

         return null;
      }
   }

   Type toType(Environment var1, Context var2) {
      ClassDefinition var3 = this.toResolvedType(var1, var2, false);
      return var3 != null ? var3.getType() : Type.tError;
   }

   public boolean isConstant() {
      if (this.implementation != null) {
         return this.implementation.isConstant();
      } else {
         return this.field != null ? this.field.isConstant() : false;
      }
   }

   public Expression inline(Environment var1, Context var2) {
      return null;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineValue(var1, var2);
      } else if (this.field == null) {
         return this;
      } else {
         try {
            if (this.field.isLocal()) {
               if (this.field.isInlineable(var1, false)) {
                  Expression var3 = (Expression)this.field.getValue(var1);
                  return (Expression)(var3 == null ? this : var3.inlineValue(var1, var2));
               } else {
                  return this;
               }
            } else {
               return this;
            }
         } catch (ClassNotFound var4) {
            throw new CompilerError(var4);
         }
      }
   }

   public Expression inlineLHS(Environment var1, Context var2) {
      return (Expression)(this.implementation != null ? this.implementation.inlineLHS(var1, var2) : this);
   }

   public Expression copyInline(Context var1) {
      if (this.implementation != null) {
         return this.implementation.copyInline(var1);
      } else {
         IdentifierExpression var2 = (IdentifierExpression)super.copyInline(var1);
         if (this.field != null && this.field.isLocal()) {
            var2.field = ((LocalMember)this.field).getCurrentInlineCopy(var1);
         }

         return var2;
      }
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return this.implementation != null ? this.implementation.costInline(var1, var2, var3) : super.costInline(var1, var2, var3);
   }

   int codeLValue(Environment var1, Context var2, Assembler var3) {
      return 0;
   }

   void codeLoad(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 21 + this.type.getTypeCodeOffset(), new Integer(((LocalMember)this.field).number));
   }

   void codeStore(Environment var1, Context var2, Assembler var3) {
      LocalMember var4 = (LocalMember)this.field;
      var3.add(this.where, 54 + this.type.getTypeCodeOffset(), new LocalVariable(var4, var4.number));
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeLValue(var1, var2, var3);
      this.codeLoad(var1, var2, var3);
   }

   public void print(PrintStream var1) {
      var1.print(this.id + "#" + (this.field != null ? this.field.hashCode() : 0));
      if (this.implementation != null) {
         var1.print("/IMPL=");
         this.implementation.print(var1);
      }

   }
}
