package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.AmbiguousClass;
import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class NewInstanceExpression extends NaryExpression {
   MemberDefinition field;
   Expression outerArg;
   ClassDefinition body;
   MemberDefinition implMethod;
   final int MAXINLINECOST;

   public NewInstanceExpression(long var1, Expression var3, Expression[] var4) {
      super(42, var1, Type.tError, var3, var4);
      this.implMethod = null;
      this.MAXINLINECOST = Statement.MAXINLINECOST;
   }

   public NewInstanceExpression(long var1, Expression var3, Expression[] var4, Expression var5, ClassDefinition var6) {
      this(var1, var3, var4);
      this.outerArg = var5;
      this.body = var6;
   }

   public Expression getOuterArg() {
      return this.outerArg;
   }

   int precedence() {
      return 100;
   }

   public Expression order() {
      if (this.outerArg != null && opPrecedence[46] > this.outerArg.precedence()) {
         UnaryExpression var1 = (UnaryExpression)this.outerArg;
         this.outerArg = var1.right;
         var1.right = this.order();
         return var1;
      } else {
         return this;
      }
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      ClassDefinition var5 = null;
      Expression var6 = null;

      try {
         if (this.outerArg != null) {
            var3 = this.outerArg.checkValue(var1, var2, var3, var4);
            var6 = this.outerArg;
            Identifier var7 = FieldExpression.toIdentifier(this.right);
            if (var7 != null && var7.isQualified()) {
               var1.error(this.where, "unqualified.name.required", var7);
            }

            if (var7 != null && this.outerArg.type.isType(10)) {
               ClassDefinition var8 = var1.getClassDefinition(this.outerArg.type);
               Identifier var9 = var8.resolveInnerClass(var1, var7);
               this.right = new TypeExpression(this.right.where, Type.tClass(var9));
               var1.resolve(this.right.where, var2.field.getClassDefinition(), this.right.type);
            } else {
               if (!this.outerArg.type.isType(13)) {
                  var1.error(this.where, "invalid.field.reference", idNew, this.outerArg.type);
               }

               this.outerArg = null;
            }
         }

         if (!(this.right instanceof TypeExpression)) {
            this.right = new TypeExpression(this.right.where, this.right.toType(var1, var2));
         }

         if (this.right.type.isType(10)) {
            var5 = var1.getClassDefinition(this.right.type);
         }
      } catch (AmbiguousClass var18) {
         var1.error(this.where, "ambig.class", var18.name1, var18.name2);
      } catch (ClassNotFound var19) {
         var1.error(this.where, "class.not.found", var19.name, var2.field);
      }

      Type var20 = this.right.type;
      boolean var21 = var20.isType(13);
      if (!var20.isType(10) && !var21) {
         var1.error(this.where, "invalid.arg.type", var20, opNames[this.op]);
         var21 = true;
      }

      if (var5 == null) {
         this.type = Type.tError;
         return var3;
      } else {
         Expression[] var22 = this.args;
         var22 = insertOuterLink(var1, var2, this.where, var5, this.outerArg, var22);
         if (var22.length > this.args.length) {
            this.outerArg = var22[0];
         } else if (this.outerArg != null) {
            this.outerArg = new CommaExpression(this.outerArg.where, this.outerArg, (Expression)null);
         }

         Type[] var10 = new Type[var22.length];

         int var11;
         for(var11 = 0; var11 < var22.length; ++var11) {
            if (var22[var11] != var6) {
               var3 = var22[var11].checkValue(var1, var2, var3, var4);
            }

            var10[var11] = var22[var11].type;
            var21 = var21 || var10[var11].isType(13);
         }

         try {
            if (var21) {
               this.type = Type.tError;
               return var3;
            }

            ClassDefinition var23 = var2.field.getClassDefinition();
            ClassDeclaration var12 = var1.getClassDeclaration(var20);
            if (this.body != null) {
               Identifier var13 = var23.getName().getQualifier();
               ClassDefinition var14 = null;
               if (var5.isInterface()) {
                  var14 = var1.getClassDefinition(idJavaLangObject);
               } else {
                  var14 = var5;
               }

               MemberDefinition var15 = var14.matchAnonConstructor(var1, var13, var10);
               if (var15 != null) {
                  var1.dtEvent("NewInstanceExpression.checkValue: ANON CLASS " + this.body + " SUPER " + var5);
                  var3 = this.body.checkLocalClass(var1, var2, var3, var5, var22, var15.getType().getArgumentTypes());
                  var20 = this.body.getClassDeclaration().getType();
                  var5 = this.body;
               }
            } else {
               if (var5.isInterface()) {
                  var1.error(this.where, "new.intf", var12);
                  return var3;
               }

               if (var5.mustBeAbstract(var1)) {
                  var1.error(this.where, "new.abstract", var12);
                  return var3;
               }
            }

            this.field = var5.matchMethod(var1, var23, idInit, var10);
            if (this.field == null) {
               MemberDefinition var27 = var5.findAnyMethod(var1, idInit);
               if (var27 != null && (new MethodExpression(this.where, this.right, var27, var22)).diagnoseMismatch(var1, var22, var10)) {
                  return var3;
               }

               String var28 = var12.getName().getName().toString();
               var28 = Type.tMethod(Type.tError, var10).typeString(var28, false, false);
               var1.error(this.where, "unmatched.constr", var28, var12);
               return var3;
            }

            if (this.field.isPrivate()) {
               ClassDefinition var25 = this.field.getClassDefinition();
               if (var25 != var23) {
                  this.implMethod = var25.getAccessMember(var1, var2, this.field, false);
               }
            }

            if (var5.mustBeAbstract(var1)) {
               var1.error(this.where, "new.abstract", var12);
               return var3;
            }

            if (this.field.reportDeprecated(var1)) {
               var1.error(this.where, "warn.constr.is.deprecated", this.field, this.field.getClassDefinition());
            }

            if (this.field.isProtected() && !var23.getName().getQualifier().equals(this.field.getClassDeclaration().getName().getQualifier())) {
               var1.error(this.where, "invalid.protected.constructor.use", var23);
            }
         } catch (ClassNotFound var16) {
            var1.error(this.where, "class.not.found", var16.name, opNames[this.op]);
            return var3;
         } catch (AmbiguousMember var17) {
            var1.error(this.where, "ambig.constr", var17.field1, var17.field2);
            return var3;
         }

         var10 = this.field.getType().getArgumentTypes();

         for(var11 = 0; var11 < var22.length; ++var11) {
            var22[var11] = this.convert(var1, var2, var10[var11], var22[var11]);
         }

         if (var22.length > this.args.length) {
            this.outerArg = var22[0];

            for(var11 = 1; var11 < var22.length; ++var11) {
               this.args[var11 - 1] = var22[var11];
            }
         }

         ClassDeclaration[] var26 = this.field.getExceptions(var1);

         for(int var24 = 0; var24 < var26.length; ++var24) {
            if (var4.get(var26[var24]) == null) {
               var4.put(var26[var24], this);
            }
         }

         this.type = var20;
         return var3;
      }
   }

   public static Expression[] insertOuterLink(Environment var0, Context var1, long var2, ClassDefinition var4, Expression var5, Expression[] var6) {
      if (!var4.isTopLevel() && !var4.isLocal()) {
         Expression[] var7 = new Expression[1 + var6.length];
         System.arraycopy(var6, 0, var7, 1, var6.length);

         try {
            if (var5 == null) {
               var5 = var1.findOuterLink(var0, var2, var4.findAnyMethod(var0, idInit));
            }
         } catch (ClassNotFound var9) {
         }

         var7[0] = var5;
         var6 = var7;
      }

      return var6;
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.checkValue(var1, var2, var3, var4);
   }

   public Expression copyInline(Context var1) {
      NewInstanceExpression var2 = (NewInstanceExpression)super.copyInline(var1);
      if (this.outerArg != null) {
         var2.outerArg = this.outerArg.copyInline(var1);
      }

      return var2;
   }

   Expression inlineNewInstance(Environment var1, Context var2, Statement var3) {
      if (var1.dump()) {
         System.out.println("INLINE NEW INSTANCE " + this.field + " in " + var2.field);
      }

      LocalMember[] var4 = LocalMember.copyArguments(var2, this.field);
      Statement[] var5 = new Statement[var4.length + 2];
      byte var6 = 1;
      if (this.outerArg != null && !this.outerArg.type.isType(11)) {
         var6 = 2;
         var5[1] = new VarDeclarationStatement(this.where, var4[1], this.outerArg);
      } else if (this.outerArg != null) {
         var5[0] = new ExpressionStatement(this.where, this.outerArg);
      }

      for(int var7 = 0; var7 < this.args.length; ++var7) {
         var5[var7 + var6] = new VarDeclarationStatement(this.where, var4[var7 + var6], this.args[var7]);
      }

      var5[var5.length - 1] = var3 != null ? var3.copyInline(var2, false) : null;
      LocalMember.doneWithArguments(var2, var4);
      return (new InlineNewInstanceExpression(this.where, this.type, this.field, new CompoundStatement(this.where, var5))).inline(var1, var2);
   }

   public Expression inline(Environment var1, Context var2) {
      return this.inlineValue(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.body != null) {
         this.body.inlineLocalClass(var1);
      }

      ClassDefinition var3 = this.field.getClassDefinition();
      UplevelReference var4 = var3.getReferencesFrozen();
      if (var4 != null) {
         var4.willCodeArguments(var1, var2);
      }

      try {
         if (this.outerArg != null) {
            if (this.outerArg.type.isType(11)) {
               this.outerArg = this.outerArg.inline(var1, var2);
            } else {
               this.outerArg = this.outerArg.inlineValue(var1, var2);
            }
         }

         for(int var5 = 0; var5 < this.args.length; ++var5) {
            this.args[var5] = this.args[var5].inlineValue(var1, var2);
         }
      } catch (ClassNotFound var6) {
         throw new CompilerError(var6);
      }

      if (this.outerArg != null && this.outerArg.type.isType(11)) {
         Expression var7 = this.outerArg;
         this.outerArg = null;
         return new CommaExpression(this.where, var7, this);
      } else {
         return this;
      }
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (this.body != null) {
         return var1;
      } else if (var3 == null) {
         return 2 + super.costInline(var1, var2, var3);
      } else {
         ClassDefinition var4 = var3.field.getClassDefinition();

         try {
            if (var4.permitInlinedAccess(var2, this.field.getClassDeclaration()) && var4.permitInlinedAccess(var2, this.field)) {
               return 2 + super.costInline(var1, var2, var3);
            }
         } catch (ClassNotFound var6) {
         }

         return var1;
      }
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.codeCommon(var1, var2, var3, false);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeCommon(var1, var2, var3, true);
   }

   private void codeCommon(Environment var1, Context var2, Assembler var3, boolean var4) {
      var3.add(this.where, 187, this.field.getClassDeclaration());
      if (var4) {
         var3.add(this.where, 89);
      }

      ClassDefinition var5 = this.field.getClassDefinition();
      UplevelReference var6 = var5.getReferencesFrozen();
      if (var6 != null) {
         var6.codeArguments(var1, var2, var3, this.where, this.field);
      }

      if (this.outerArg != null) {
         this.outerArg.codeValue(var1, var2, var3);
         switch (this.outerArg.op) {
            case 46:
               MemberDefinition var7 = ((FieldExpression)this.outerArg).field;
               if (var7 != null && var7.isNeverNull()) {
                  break;
               }
            default:
               try {
                  ClassDefinition var10 = var1.getClassDefinition(idJavaLangObject);
                  MemberDefinition var8 = var10.getFirstMatch(idGetClass);
                  var3.add(this.where, 89);
                  var3.add(this.where, 182, var8);
                  var3.add(this.where, 87);
               } catch (ClassNotFound var9) {
               }
            case 49:
            case 82:
            case 83:
         }
      }

      if (this.implMethod != null) {
         var3.add(this.where, 1);
      }

      for(int var11 = 0; var11 < this.args.length; ++var11) {
         this.args[var11].codeValue(var1, var2, var3);
      }

      var3.add(this.where, 183, this.implMethod != null ? this.implMethod : this.field);
   }
}
