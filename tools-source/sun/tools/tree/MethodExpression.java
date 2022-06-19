package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class MethodExpression extends NaryExpression {
   Identifier id;
   ClassDefinition clazz;
   MemberDefinition field;
   Expression implementation;
   private boolean isSuper;
   static final int MAXINLINECOST;

   public MethodExpression(long var1, Expression var3, Identifier var4, Expression[] var5) {
      super(47, var1, Type.tError, var3, var5);
      this.id = var4;
   }

   public MethodExpression(long var1, Expression var3, MemberDefinition var4, Expression[] var5) {
      super(47, var1, var4.getType().getReturnType(), var3, var5);
      this.id = var4.getName();
      this.field = var4;
      this.clazz = var4.getClassDefinition();
   }

   public MethodExpression(long var1, Expression var3, MemberDefinition var4, Expression[] var5, boolean var6) {
      this(var1, var3, var4, var5);
      this.isSuper = var6;
   }

   public Expression getImplementation() {
      return (Expression)(this.implementation != null ? this.implementation : this);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      ClassDeclaration var5 = null;
      boolean var6 = false;
      boolean var7 = false;
      MemberDefinition var8 = null;
      ClassDefinition var9 = var2.field.getClassDefinition();
      Expression[] var10 = this.args;
      if (this.id.equals(idInit)) {
         ClassDefinition var11 = var9;

         try {
            Expression var12 = null;
            if (this.right instanceof SuperExpression) {
               var11 = var11.getSuperClass().getClassDefinition(var1);
               var12 = ((SuperExpression)this.right).outerArg;
            } else if (this.right instanceof ThisExpression) {
               var12 = ((ThisExpression)this.right).outerArg;
            }

            var10 = NewInstanceExpression.insertOuterLink(var1, var2, this.where, var11, var12, var10);
         } catch (ClassNotFound var18) {
         }
      }

      Type[] var21 = new Type[var10.length];
      ClassDefinition var22 = var9;

      int var23;
      int var27;
      ClassDefinition var28;
      try {
         if (this.right != null) {
            if (this.id.equals(idInit)) {
               var23 = var2.getThisNumber();
               if (!var2.field.isConstructor()) {
                  var1.error(this.where, "invalid.constr.invoke");
                  return var3.addVar(var23);
               }

               if (!var3.isReallyDeadEnd() && var3.testVar(var23)) {
                  var1.error(this.where, "constr.invoke.not.first");
                  return var3;
               }

               var3 = var3.addVar(var23);
               if (this.right instanceof SuperExpression) {
                  var3 = this.right.checkAmbigName(var1, var2, var3, var4, this);
               } else {
                  var3 = this.right.checkValue(var1, var2, var3, var4);
               }
            } else {
               var3 = this.right.checkAmbigName(var1, var2, var3, var4, this);
               if (this.right.type == Type.tPackage) {
                  FieldExpression.reportFailedPackagePrefix(var1, this.right);
                  return var3;
               }

               if (this.right instanceof TypeExpression) {
                  var7 = true;
               }
            }

            if (this.right.type.isType(10)) {
               var5 = var1.getClassDeclaration(this.right.type);
            } else {
               if (!this.right.type.isType(9)) {
                  if (!this.right.type.isType(13)) {
                     var1.error(this.where, "invalid.method.invoke", this.right.type);
                  }

                  return var3;
               }

               var6 = true;
               var5 = var1.getClassDeclaration(Type.tObject);
            }

            if (this.right instanceof FieldExpression) {
               Identifier var24 = ((FieldExpression)this.right).id;
               if (var24 == idThis) {
                  var22 = ((FieldExpression)this.right).clazz;
               } else if (var24 == idSuper) {
                  this.isSuper = true;
                  var22 = ((FieldExpression)this.right).clazz;
               }
            } else if (this.right instanceof SuperExpression) {
               this.isSuper = true;
            }

            if (this.id != idInit && !FieldExpression.isTypeAccessible(this.where, var1, this.right.type, var22)) {
               ClassDeclaration var25 = var22.getClassDeclaration();
               if (var7) {
                  var1.error(this.where, "no.type.access", this.id, this.right.type.toString(), var25);
               } else {
                  var1.error(this.where, "cant.access.member.type", this.id, this.right.type.toString(), var25);
               }
            }
         } else {
            var7 = var2.field.isStatic();
            ClassDefinition var13 = var9;

            MemberDefinition var14;
            for(var14 = null; var13 != null; var13 = var13.getOuterClass()) {
               var14 = var13.findAnyMethod(var1, this.id);
               if (var14 != null) {
                  break;
               }
            }

            if (var14 == null) {
               var5 = var2.field.getClassDeclaration();
            } else {
               var5 = var13.getClassDeclaration();
               if (var14.getClassDefinition() != var13) {
                  ClassDefinition var15 = var13;

                  while((var15 = var15.getOuterClass()) != null) {
                     MemberDefinition var16 = var15.findAnyMethod(var1, this.id);
                     if (var16 != null && var16.getClassDefinition() == var15) {
                        var1.error(this.where, "inherited.hides.method", this.id, var13.getClassDeclaration(), var15.getClassDeclaration());
                        break;
                     }
                  }
               }
            }
         }

         boolean var26 = false;
         if (this.id.equals(idInit)) {
            var3 = var3.clearVar(var2.getThisNumber());
         }

         for(var27 = 0; var27 < var10.length; ++var27) {
            var3 = var10[var27].checkValue(var1, var2, var3, var4);
            var21[var27] = var10[var27].type;
            var26 = var26 || var21[var27].isType(13);
         }

         if (this.id.equals(idInit)) {
            var3 = var3.addVar(var2.getThisNumber());
         }

         if (var26) {
            return var3;
         }

         this.clazz = var5.getClassDefinition(var1);
         if (this.field == null) {
            this.field = this.clazz.matchMethod(var1, var22, this.id, var21);
            if (this.field == null) {
               String var39;
               if (this.id.equals(idInit)) {
                  if (this.diagnoseMismatch(var1, var10, var21)) {
                     return var3;
                  }

                  var39 = this.clazz.getName().getName().toString();
                  var39 = Type.tMethod(Type.tError, var21).typeString(var39, false, false);
                  var1.error(this.where, "unmatched.constr", var39, var5);
                  return var3;
               }

               var39 = this.id.toString();
               var39 = Type.tMethod(Type.tError, var21).typeString(var39, false, false);
               if (this.clazz.findAnyMethod(var1, this.id) == null) {
                  if (var2.getField(var1, this.id) != null) {
                     var1.error(this.where, "invalid.method", this.id, var5);
                  } else {
                     var1.error(this.where, "undef.meth", var39, var5);
                  }
               } else if (!this.diagnoseMismatch(var1, var10, var21)) {
                  var1.error(this.where, "unmatched.meth", var39, var5);
               }

               return var3;
            }
         }

         this.type = this.field.getType().getReturnType();
         if (var7 && !this.field.isStatic()) {
            var1.error(this.where, "no.static.meth.access", this.field, this.field.getClassDeclaration());
            return var3;
         }

         if (this.field.isProtected() && this.right != null && !(this.right instanceof SuperExpression) && (!(this.right instanceof FieldExpression) || ((FieldExpression)this.right).id != idSuper) && !var22.protectedAccess(var1, this.field, this.right.type)) {
            var1.error(this.where, "invalid.protected.method.use", this.field.getName(), this.field.getClassDeclaration(), this.right.type);
            return var3;
         }

         if (this.right instanceof FieldExpression && ((FieldExpression)this.right).id == idSuper && !this.field.isPrivate() && var22 != var9) {
            var8 = var22.getAccessMember(var1, var2, this.field, true);
         }

         if (var8 == null && this.field.isPrivate()) {
            var28 = this.field.getClassDefinition();
            if (var28 != var9) {
               var8 = var28.getAccessMember(var1, var2, this.field, false);
            }
         }

         if (this.field.isAbstract() && this.right != null && this.right.op == 83) {
            var1.error(this.where, "invoke.abstract", this.field, this.field.getClassDeclaration());
            return var3;
         }

         if (this.field.reportDeprecated(var1)) {
            if (this.field.isConstructor()) {
               var1.error(this.where, "warn.constr.is.deprecated", this.field);
            } else {
               var1.error(this.where, "warn.meth.is.deprecated", this.field, this.field.getClassDefinition());
            }
         }

         if (this.field.isConstructor() && var2.field.equals(this.field)) {
            var1.error(this.where, "recursive.constr", this.field);
         }

         if (var22 == var9) {
            var28 = this.field.getClassDefinition();
            if (!this.field.isConstructor() && var28.isPackagePrivate() && !var28.getName().getQualifier().equals(var22.getName().getQualifier())) {
               this.field = MemberDefinition.makeProxyMember(this.field, this.clazz, var1);
            }
         }

         var22.addDependency(this.field.getClassDeclaration());
         if (var22 != var9) {
            var9.addDependency(this.field.getClassDeclaration());
         }
      } catch (ClassNotFound var19) {
         var1.error(this.where, "class.not.found", var19.name, var2.field);
         return var3;
      } catch (AmbiguousMember var20) {
         var1.error(this.where, "ambig.field", this.id, var20.field1, var20.field2);
         return var3;
      }

      if (this.right == null && !this.field.isStatic()) {
         this.right = var2.findOuterLink(var1, this.where, this.field);
         var3 = this.right.checkValue(var1, var2, var3, var4);
      }

      var21 = this.field.getType().getArgumentTypes();

      for(var23 = 0; var23 < var10.length; ++var23) {
         var10[var23] = this.convert(var1, var2, var21[var23], var10[var23]);
      }

      Expression[] var29;
      int var34;
      if (this.field.isConstructor()) {
         MemberDefinition var30 = this.field;
         if (var8 != null) {
            var30 = var8;
         }

         var27 = var10.length;
         var29 = var10;
         if (var27 > this.args.length) {
            Object var31;
            if (this.right instanceof SuperExpression) {
               var31 = new SuperExpression(this.right.where, var2);
               ((SuperExpression)this.right).outerArg = var10[0];
            } else {
               if (!(this.right instanceof ThisExpression)) {
                  throw new CompilerError("this.init");
               }

               var31 = new ThisExpression(this.right.where, var2);
            }

            int var17;
            if (var8 != null) {
               var29 = new Expression[var27 + 1];
               this.args = new Expression[var27];
               var29[0] = var10[0];
               this.args[0] = var29[1] = new NullExpression(this.where);

               for(var17 = 1; var17 < var27; ++var17) {
                  this.args[var17] = var29[var17 + 1] = var10[var17];
               }
            } else {
               for(var17 = 1; var17 < var27; ++var17) {
                  this.args[var17 - 1] = var10[var17];
               }
            }

            this.implementation = new MethodExpression(this.where, (Expression)var31, var30, var29);
            this.implementation.type = this.type;
         } else {
            if (var8 != null) {
               var29 = new Expression[var27 + 1];
               var29[0] = new NullExpression(this.where);

               for(var34 = 0; var34 < var27; ++var34) {
                  var29[var34 + 1] = var10[var34];
               }
            }

            this.implementation = new MethodExpression(this.where, this.right, var30, var29);
         }
      } else {
         if (var10.length > this.args.length) {
            throw new CompilerError("method arg");
         }

         if (var8 != null) {
            Expression[] var33 = this.args;
            if (this.field.isStatic()) {
               MethodExpression var32 = new MethodExpression(this.where, (Expression)null, var8, var33);
               this.implementation = new CommaExpression(this.where, this.right, var32);
            } else {
               var27 = var33.length;
               var29 = new Expression[var27 + 1];
               var29[0] = this.right;

               for(var34 = 0; var34 < var27; ++var34) {
                  var29[var34 + 1] = var33[var34];
               }

               this.implementation = new MethodExpression(this.where, (Expression)null, var8, var29);
            }
         }
      }

      if (var2.field.isConstructor() && this.field.isConstructor() && this.right != null && this.right.op == 83) {
         Expression var36 = this.makeVarInits(var1, var2);
         if (var36 != null) {
            if (this.implementation == null) {
               this.implementation = (Expression)this.clone();
            }

            this.implementation = new CommaExpression(this.where, this.implementation, var36);
         }
      }

      ClassDeclaration[] var38 = this.field.getExceptions(var1);
      if (var6 && this.field.getName() == idClone && this.field.getType().getArgumentTypes().length == 0) {
         var38 = new ClassDeclaration[0];

         for(Context var35 = var2; var35 != null; var35 = var35.prev) {
            if (var35.node != null && var35.node.op == 101) {
               ((TryStatement)var35.node).arrayCloneWhere = this.where;
            }
         }
      }

      for(var27 = 0; var27 < var38.length; ++var27) {
         if (var4.get(var38[var27]) == null) {
            var4.put(var38[var27], this);
         }
      }

      if (var2.field.isConstructor() && this.field.isConstructor() && this.right != null && this.right.op == 82) {
         var28 = this.field.getClassDefinition();

         for(MemberDefinition var37 = var28.getFirstMember(); var37 != null; var37 = var37.getNextMember()) {
            if (var37.isVariable() && var37.isBlankFinal() && !var37.isStatic()) {
               var3 = var3.addVar(var2.getFieldNumber(var37));
            }
         }
      }

      return var3;
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.checkValue(var1, var2, var3, var4);
   }

   boolean diagnoseMismatch(Environment var1, Expression[] var2, Type[] var3) throws ClassNotFound {
      Type[] var4 = new Type[1];
      boolean var5 = false;

      int var9;
      for(int var6 = 0; var6 < var3.length; var6 = var9 + 1) {
         int var7 = this.clazz.diagnoseMismatch(var1, this.id, var3, var6, var4);
         String var8 = this.id.equals(idInit) ? "constructor" : opNames[this.op];
         if (var7 == -2) {
            var1.error(this.where, "wrong.number.args", var8);
            var5 = true;
         }

         if (var7 < 0) {
            break;
         }

         var9 = var7 >> 2;
         boolean var10 = (var7 & 2) != 0;
         boolean var11 = (var7 & 1) != 0;
         Type var12 = var4[0];
         String var13 = "" + var12;
         if (var10) {
            var1.error(var2[var9].where, "explicit.cast.needed", var8, var3[var9], var13);
         } else {
            var1.error(var2[var9].where, "incompatible.type", var8, var3[var9], var13);
         }

         var5 = true;
      }

      return var5;
   }

   private Expression inlineMethod(Environment var1, Context var2, Statement var3, boolean var4) {
      if (var1.dump()) {
         System.out.println("INLINE METHOD " + this.field + " in " + var2.field);
      }

      LocalMember[] var5 = LocalMember.copyArguments(var2, this.field);
      Statement[] var6 = new Statement[var5.length + 2];
      int var7 = 0;
      if (this.field.isStatic()) {
         var6[0] = new ExpressionStatement(this.where, this.right);
      } else {
         if (this.right != null && this.right.op == 83) {
            this.right = new ThisExpression(this.right.where, var2);
         }

         var6[0] = new VarDeclarationStatement(this.where, var5[var7++], this.right);
      }

      for(int var8 = 0; var8 < this.args.length; ++var8) {
         var6[var8 + 1] = new VarDeclarationStatement(this.where, var5[var7++], this.args[var8]);
      }

      var6[var6.length - 1] = var3 != null ? var3.copyInline(var2, var4) : null;
      LocalMember.doneWithArguments(var2, var5);
      Type var10 = var4 ? this.type : Type.tVoid;
      InlineMethodExpression var9 = new InlineMethodExpression(this.where, var10, this.field, new CompoundStatement(this.where, var6));
      return var4 ? var9.inlineValue(var1, var2) : var9.inline(var1, var2);
   }

   public Expression inline(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inline(var1, var2);
      } else {
         try {
            if (this.right != null) {
               this.right = this.field.isStatic() ? this.right.inline(var1, var2) : this.right.inlineValue(var1, var2);
            }

            for(int var3 = 0; var3 < this.args.length; ++var3) {
               this.args[var3] = this.args[var3].inlineValue(var1, var2);
            }

            ClassDefinition var7 = var2.field.getClassDefinition();
            Object var4 = this;
            if (var1.opt() && this.field.isInlineable(var1, this.clazz.isFinal()) && (this.right == null || this.right.op == 82 || this.field.isStatic()) && var7.permitInlinedAccess(var1, this.field.getClassDeclaration()) && var7.permitInlinedAccess(var1, this.field) && (this.right == null || var7.permitInlinedAccess(var1, var1.getClassDeclaration(this.right.type))) && (this.id == null || !this.id.equals(idInit)) && !var2.field.isInitializer() && var2.field.isMethod() && var2.getInlineMemberContext(this.field) == null) {
               Statement var5 = (Statement)this.field.getValue(var1);
               if (var5 == null || var5.costInline(MAXINLINECOST, var1, var2) < MAXINLINECOST) {
                  var4 = this.inlineMethod(var1, var2, var5, false);
               }
            }

            return (Expression)var4;
         } catch (ClassNotFound var6) {
            throw new CompilerError(var6);
         }
      }
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineValue(var1, var2);
      } else {
         try {
            if (this.right != null) {
               this.right = this.field.isStatic() ? this.right.inline(var1, var2) : this.right.inlineValue(var1, var2);
            }

            ClassDefinition var3;
            if (this.field.getName().equals(idInit)) {
               var3 = this.field.getClassDefinition();
               UplevelReference var4 = var3.getReferencesFrozen();
               if (var4 != null) {
                  var4.willCodeArguments(var1, var2);
               }
            }

            for(int var6 = 0; var6 < this.args.length; ++var6) {
               this.args[var6] = this.args[var6].inlineValue(var1, var2);
            }

            var3 = var2.field.getClassDefinition();
            if (var1.opt() && this.field.isInlineable(var1, this.clazz.isFinal()) && (this.right == null || this.right.op == 82 || this.field.isStatic()) && var3.permitInlinedAccess(var1, this.field.getClassDeclaration()) && var3.permitInlinedAccess(var1, this.field) && (this.right == null || var3.permitInlinedAccess(var1, var1.getClassDeclaration(this.right.type))) && !var2.field.isInitializer() && var2.field.isMethod() && var2.getInlineMemberContext(this.field) == null) {
               Statement var7 = (Statement)this.field.getValue(var1);
               if (var7 == null || var7.costInline(MAXINLINECOST, var1, var2) < MAXINLINECOST) {
                  return this.inlineMethod(var1, var2, var7, true);
               }
            }

            return this;
         } catch (ClassNotFound var5) {
            throw new CompilerError(var5);
         }
      }
   }

   public Expression copyInline(Context var1) {
      return this.implementation != null ? this.implementation.copyInline(var1) : super.copyInline(var1);
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (this.implementation != null) {
         return this.implementation.costInline(var1, var2, var3);
      } else {
         return this.right != null && this.right.op == 83 ? var1 : super.costInline(var1, var2, var3);
      }
   }

   private Expression makeVarInits(Environment var1, Context var2) {
      ClassDefinition var3 = var2.field.getClassDefinition();
      Object var4 = null;

      for(MemberDefinition var5 = var3.getFirstMember(); var5 != null; var5 = var5.getNextMember()) {
         if ((var5.isVariable() || var5.isInitializer()) && !var5.isStatic()) {
            try {
               var5.check(var1);
            } catch (ClassNotFound var11) {
               var1.error(var5.getWhere(), "class.not.found", var11.name, var5.getClassDefinition());
            }

            Object var6 = null;
            if (var5.isUplevelValue()) {
               if (var5 != var3.findOuterMember()) {
                  continue;
               }

               IdentifierExpression var7 = new IdentifierExpression(this.where, var5.getName());
               if (!var7.bind(var1, var2)) {
                  throw new CompilerError("bind " + var7.id);
               }

               var6 = var7;
            } else if (var5.isInitializer()) {
               Statement var13 = (Statement)var5.getValue();
               var6 = new InlineMethodExpression(this.where, Type.tVoid, var5, var13);
            } else {
               var6 = (Expression)var5.getValue();
            }

            if (var6 != null) {
               long var14 = var5.getWhere();
               Expression var15 = ((Expression)var6).copyInline(var2);
               Object var9 = var15;
               if (var5.isVariable()) {
                  ThisExpression var10 = new ThisExpression(var14, var2);
                  FieldExpression var12 = new FieldExpression(var14, var10, var5);
                  var9 = new AssignExpression(var14, var12, var15);
               }

               var4 = var4 == null ? var9 : new CommaExpression(var14, (Expression)var4, (Expression)var9);
            }
         }
      }

      return (Expression)var4;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      if (this.implementation != null) {
         throw new CompilerError("codeValue");
      } else {
         int var4 = 0;
         ClassDefinition var5;
         UplevelReference var6;
         if (this.field.isStatic()) {
            if (this.right != null) {
               this.right.code(var1, var2, var3);
            }
         } else if (this.right == null) {
            var3.add(this.where, 25, new Integer(0));
         } else if (this.right.op == 83) {
            this.right.codeValue(var1, var2, var3);
            if (idInit.equals(this.id)) {
               var5 = this.field.getClassDefinition();
               var6 = var5.getReferencesFrozen();
               if (var6 != null) {
                  if (var6.isClientOuterField()) {
                     this.args[var4++].codeValue(var1, var2, var3);
                  }

                  var6.codeArguments(var1, var2, var3, this.where, this.field);
               }
            }
         } else {
            this.right.codeValue(var1, var2, var3);
         }

         while(var4 < this.args.length) {
            this.args[var4].codeValue(var1, var2, var3);
            ++var4;
         }

         if (this.field.isStatic()) {
            var3.add(this.where, 184, this.field);
         } else if (!this.field.isConstructor() && !this.field.isPrivate() && !this.isSuper) {
            if (this.field.getClassDefinition().isInterface()) {
               var3.add(this.where, 185, this.field);
            } else {
               var3.add(this.where, 182, this.field);
            }
         } else {
            var3.add(this.where, 183, this.field);
         }

         if (this.right != null && this.right.op == 83 && idInit.equals(this.id)) {
            var5 = var2.field.getClassDefinition();
            var6 = var5.getReferencesFrozen();
            if (var6 != null) {
               var6.codeInitialization(var1, var2, var3, this.where, this.field);
            }
         }

      }
   }

   public Expression firstConstructor() {
      return this.id.equals(idInit) ? this : null;
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op]);
      if (this.right != null) {
         var1.print(" ");
         this.right.print(var1);
      }

      var1.print(" " + (this.id == null ? idInit : this.id));

      for(int var2 = 0; var2 < this.args.length; ++var2) {
         var1.print(" ");
         if (this.args[var2] != null) {
            this.args[var2].print(var1);
         } else {
            var1.print("<null>");
         }
      }

      var1.print(")");
      if (this.implementation != null) {
         var1.print("/IMPL=");
         this.implementation.print(var1);
      }

   }

   static {
      MAXINLINECOST = Statement.MAXINLINECOST;
   }
}
