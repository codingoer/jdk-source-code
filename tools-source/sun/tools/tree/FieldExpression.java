package sun.tools.tree;

import java.io.PrintStream;
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
import sun.tools.java.IdentifierToken;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class FieldExpression extends UnaryExpression {
   Identifier id;
   MemberDefinition field;
   Expression implementation;
   ClassDefinition clazz;
   private ClassDefinition superBase;

   public FieldExpression(long var1, Expression var3, Identifier var4) {
      super(46, var1, Type.tError, var3);
      this.id = var4;
   }

   public FieldExpression(long var1, Expression var3, MemberDefinition var4) {
      super(46, var1, var4.getType(), var3);
      this.id = var4.getName();
      this.field = var4;
   }

   public Expression getImplementation() {
      return (Expression)(this.implementation != null ? this.implementation : this);
   }

   private boolean isQualSuper() {
      return this.superBase != null;
   }

   public static Identifier toIdentifier(Expression var0) {
      StringBuffer var1;
      FieldExpression var2;
      for(var1 = new StringBuffer(); var0.op == 46; var0 = var2.right) {
         var2 = (FieldExpression)var0;
         if (var2.id == idThis || var2.id == idClass) {
            return null;
         }

         var1.insert(0, var2.id);
         var1.insert(0, '.');
      }

      if (var0.op != 60) {
         return null;
      } else {
         var1.insert(0, ((IdentifierExpression)var0).id);
         return Identifier.lookup(var1.toString());
      }
   }

   Type toType(Environment var1, Context var2) {
      Identifier var3 = toIdentifier(this);
      if (var3 == null) {
         var1.error(this.where, "invalid.type.expr");
         return Type.tError;
      } else {
         Type var4 = Type.tClass(var2.resolveName(var1, var3));
         return var1.resolve(this.where, var2.field.getClassDefinition(), var4) ? var4 : Type.tError;
      }
   }

   public Vset checkAmbigName(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      if (this.id == idThis || this.id == idClass) {
         var5 = null;
      }

      return this.checkCommon(var1, var2, var3, var4, var5, false);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.checkCommon(var1, var2, var3, var4, (UnaryExpression)null, false);
      if (this.id == idSuper && this.type != Type.tError) {
         var1.error(this.where, "undef.var.super", idSuper);
      }

      return var3;
   }

   static void reportFailedPackagePrefix(Environment var0, Expression var1) {
      reportFailedPackagePrefix(var0, var1, false);
   }

   static void reportFailedPackagePrefix(Environment var0, Expression var1, boolean var2) {
      Expression var3;
      for(var3 = var1; var3 instanceof UnaryExpression; var3 = ((UnaryExpression)var3).right) {
      }

      IdentifierExpression var4 = (IdentifierExpression)var3;

      try {
         var0.resolve(var4.id);
      } catch (AmbiguousClass var6) {
         var0.error(var1.where, "ambig.class", var6.name1, var6.name2);
         return;
      } catch (ClassNotFound var7) {
      }

      if (var3 == var1) {
         if (var2) {
            var0.error(var4.where, "undef.class", var4.id);
         } else {
            var0.error(var4.where, "undef.var.or.class", var4.id);
         }
      } else if (var2) {
         var0.error(var4.where, "undef.class.or.package", var4.id);
      } else {
         var0.error(var4.where, "undef.var.class.or.package", var4.id);
      }

   }

   private Expression implementFieldAccess(Environment var1, Context var2, Expression var3, boolean var4) {
      ClassDefinition var5 = this.accessBase(var1, var2);
      if (var5 != null) {
         if (this.field.isFinal()) {
            Expression var6 = (Expression)this.field.getValue();
            if (var6 != null && var6.isConstant() && !var4) {
               return var6.copyInline(var2);
            }
         }

         MemberDefinition var9 = var5.getAccessMember(var1, var2, this.field, this.isQualSuper());
         if (!var4) {
            Expression[] var7;
            if (this.field.isStatic()) {
               var7 = new Expression[0];
               MethodExpression var8 = new MethodExpression(this.where, (Expression)null, var9, var7);
               return new CommaExpression(this.where, var3, var8);
            }

            var7 = new Expression[]{var3};
            return new MethodExpression(this.where, (Expression)null, var9, var7);
         }
      }

      return null;
   }

   private ClassDefinition accessBase(Environment var1, Context var2) {
      ClassDefinition var3;
      ClassDefinition var4;
      if (this.field.isPrivate()) {
         var3 = this.field.getClassDefinition();
         var4 = var2.field.getClassDefinition();
         return var3 == var4 ? null : var3;
      } else if (this.field.isProtected()) {
         if (this.superBase == null) {
            return null;
         } else {
            var3 = this.field.getClassDefinition();
            var4 = var2.field.getClassDefinition();
            return var3.inSamePackage(var4) ? null : this.superBase;
         }
      } else {
         return null;
      }
   }

   static boolean isTypeAccessible(long var0, Environment var2, Type var3, ClassDefinition var4) {
      switch (var3.getTypeCode()) {
         case 9:
            return isTypeAccessible(var0, var2, var3.getElementType(), var4);
         case 10:
            try {
               Identifier var5 = var3.getClassName();
               ClassDefinition var6 = var2.getClassDefinition(var3);
               return var4.canAccess(var2, var6.getClassDeclaration());
            } catch (ClassNotFound var7) {
               return true;
            }
         default:
            return true;
      }
   }

   private Vset checkCommon(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5, boolean var6) {
      ClassDefinition var20;
      ClassDefinition var26;
      if (this.id == idClass) {
         Type var19 = this.right.toType(var1, var2);
         if (!var19.isType(10) && !var19.isType(9)) {
            if (var19.isType(13)) {
               this.type = Type.tClassDesc;
               return var3;
            } else {
               var20 = null;
               String var22;
               switch (var19.getTypeCode()) {
                  case 0:
                     var22 = "Boolean";
                     break;
                  case 1:
                     var22 = "Byte";
                     break;
                  case 2:
                     var22 = "Character";
                     break;
                  case 3:
                     var22 = "Short";
                     break;
                  case 4:
                     var22 = "Integer";
                     break;
                  case 5:
                     var22 = "Long";
                     break;
                  case 6:
                     var22 = "Float";
                     break;
                  case 7:
                     var22 = "Double";
                     break;
                  case 8:
                  case 9:
                  case 10:
                  default:
                     var1.error(this.right.where, "invalid.type.expr");
                     return var3;
                  case 11:
                     var22 = "Void";
               }

               Identifier var25 = Identifier.lookup(idJavaLang + "." + var22);
               TypeExpression var27 = new TypeExpression(this.where, Type.tClass(var25));
               this.implementation = new FieldExpression(this.where, var27, idTYPE);
               var3 = this.implementation.checkValue(var1, var2, var3, var4);
               this.type = this.implementation.type;
               return var3;
            }
         } else if (var19.isVoidArray()) {
            this.type = Type.tClassDesc;
            var1.error(this.right.where, "void.array");
            return var3;
         } else {
            long var21 = var2.field.getWhere();
            var26 = var2.field.getClassDefinition();
            MemberDefinition var11 = var26.getClassLiteralLookup(var21);
            String var12 = var19.getTypeSignature();
            String var13;
            if (var19.isType(10)) {
               var13 = var12.substring(1, var12.length() - 1).replace('/', '.');
            } else {
               var13 = var12.replace('/', '.');
            }

            if (var26.isInterface()) {
               this.implementation = this.makeClassLiteralInlineRef(var1, var2, var11, var13);
            } else {
               ClassDefinition var14 = var11.getClassDefinition();
               MemberDefinition var15 = getClassLiteralCache(var1, var2, var13, var14);
               this.implementation = this.makeClassLiteralCacheRef(var1, var2, var11, var15, var13);
            }

            var3 = this.implementation.checkValue(var1, var2, var3, var4);
            this.type = this.implementation.type;
            return var3;
         }
      } else if (this.field != null) {
         this.implementation = this.implementFieldAccess(var1, var2, this.right, var6);
         return this.right == null ? var3 : this.right.checkAmbigName(var1, var2, var3, var4, this);
      } else {
         var3 = this.right.checkAmbigName(var1, var2, var3, var4, this);
         if (this.right.type == Type.tPackage) {
            if (var5 == null) {
               reportFailedPackagePrefix(var1, this.right);
               return var3;
            } else {
               Identifier var18 = toIdentifier(this);
               if (var18 != null && var1.classExists(var18)) {
                  var5.right = new TypeExpression(this.where, Type.tClass(var18));
                  var20 = var2.field.getClassDefinition();
                  var1.resolve(this.where, var20, var5.right.type);
                  return var3;
               } else {
                  this.type = Type.tPackage;
                  return var3;
               }
            }
         } else {
            ClassDefinition var7 = var2.field.getClassDefinition();
            boolean var8 = this.right instanceof TypeExpression;

            try {
               if (!this.right.type.isType(10)) {
                  if (this.right.type.isType(9) && this.id.equals(idLength)) {
                     if (!isTypeAccessible(this.where, var1, this.right.type, var7)) {
                        ClassDeclaration var23 = var7.getClassDeclaration();
                        if (var8) {
                           var1.error(this.where, "no.type.access", this.id, this.right.type.toString(), var23);
                        } else {
                           var1.error(this.where, "cant.access.member.type", this.id, this.right.type.toString(), var23);
                        }
                     }

                     this.type = Type.tInt;
                     this.implementation = new LengthExpression(this.where, this.right);
                     return var3;
                  }

                  if (!this.right.type.isType(13)) {
                     var1.error(this.where, "invalid.field.reference", this.id, this.right.type);
                  }

                  return var3;
               }

               ClassDefinition var9 = var7;
               if (this.right instanceof FieldExpression) {
                  Identifier var10 = ((FieldExpression)this.right).id;
                  if (var10 == idThis) {
                     var9 = ((FieldExpression)this.right).clazz;
                  } else if (var10 == idSuper) {
                     var9 = ((FieldExpression)this.right).clazz;
                     this.superBase = var9;
                  }
               }

               this.clazz = var1.getClassDefinition(this.right.type);
               if (this.id == idThis || this.id == idSuper) {
                  if (!var8) {
                     var1.error(this.right.where, "invalid.type.expr");
                  }

                  if (var2.field.isSynthetic()) {
                     throw new CompilerError("synthetic qualified this");
                  }

                  this.implementation = var2.findOuterLink(var1, this.where, this.clazz, (MemberDefinition)null, true);
                  var3 = this.implementation.checkValue(var1, var2, var3, var4);
                  if (this.id == idSuper) {
                     this.type = this.clazz.getSuperClass().getType();
                  } else {
                     this.type = this.clazz.getType();
                  }

                  return var3;
               }

               this.field = this.clazz.getVariable(var1, this.id, var9);
               if (this.field == null && var8 && var5 != null) {
                  this.field = this.clazz.getInnerClass(var1, this.id);
                  if (this.field != null) {
                     return this.checkInnerClass(var1, var2, var3, var4, var5);
                  }
               }

               if (this.field == null) {
                  if ((this.field = this.clazz.findAnyMethod(var1, this.id)) != null) {
                     var1.error(this.where, "invalid.field", this.id, this.field.getClassDeclaration());
                  } else {
                     var1.error(this.where, "no.such.field", this.id, this.clazz);
                  }

                  return var3;
               }

               if (!isTypeAccessible(this.where, var1, this.right.type, var9)) {
                  ClassDeclaration var24 = var9.getClassDeclaration();
                  if (var8) {
                     var1.error(this.where, "no.type.access", this.id, this.right.type.toString(), var24);
                  } else {
                     var1.error(this.where, "cant.access.member.type", this.id, this.right.type.toString(), var24);
                  }
               }

               this.type = this.field.getType();
               if (!var9.canAccess(var1, this.field)) {
                  var1.error(this.where, "no.field.access", this.id, this.clazz, var9.getClassDeclaration());
                  return var3;
               }

               if (var8 && !this.field.isStatic()) {
                  var1.error(this.where, "no.static.field.access", this.id, this.clazz);
                  return var3;
               }

               this.implementation = this.implementFieldAccess(var1, var2, this.right, var6);
               if (this.field.isProtected() && !(this.right instanceof SuperExpression) && (!(this.right instanceof FieldExpression) || ((FieldExpression)this.right).id != idSuper) && !var9.protectedAccess(var1, this.field, this.right.type)) {
                  var1.error(this.where, "invalid.protected.field.use", this.field.getName(), this.field.getClassDeclaration(), this.right.type);
                  return var3;
               }

               if (!this.field.isStatic() && this.right.op == 82 && !var3.testVar(var2.getThisNumber())) {
                  var1.error(this.where, "access.inst.before.super", this.id);
               }

               if (this.field.reportDeprecated(var1)) {
                  var1.error(this.where, "warn.field.is.deprecated", this.id, this.field.getClassDefinition());
               }

               if (var9 == var7) {
                  var26 = this.field.getClassDefinition();
                  if (var26.isPackagePrivate() && !var26.getName().getQualifier().equals(var9.getName().getQualifier())) {
                     this.field = MemberDefinition.makeProxyMember(this.field, this.clazz, var1);
                  }
               }

               var9.addDependency(this.field.getClassDeclaration());
            } catch (ClassNotFound var16) {
               var1.error(this.where, "class.not.found", var16.name, var2.field);
            } catch (AmbiguousMember var17) {
               var1.error(this.where, "ambig.field", this.id, var17.field1.getClassDeclaration(), var17.field2.getClassDeclaration());
            }

            return var3;
         }
      }
   }

   public FieldUpdater getAssigner(Environment var1, Context var2) {
      if (this.field == null) {
         return null;
      } else {
         ClassDefinition var3 = this.accessBase(var1, var2);
         if (var3 != null) {
            MemberDefinition var4 = var3.getUpdateMember(var1, var2, this.field, this.isQualSuper());
            Expression var5 = this.right == null ? null : this.right.copyInline(var2);
            return new FieldUpdater(this.where, this.field, var5, (MemberDefinition)null, var4);
         } else {
            return null;
         }
      }
   }

   public FieldUpdater getUpdater(Environment var1, Context var2) {
      if (this.field == null) {
         return null;
      } else {
         ClassDefinition var3 = this.accessBase(var1, var2);
         if (var3 != null) {
            MemberDefinition var4 = var3.getAccessMember(var1, var2, this.field, this.isQualSuper());
            MemberDefinition var5 = var3.getUpdateMember(var1, var2, this.field, this.isQualSuper());
            Expression var6 = this.right == null ? null : this.right.copyInline(var2);
            return new FieldUpdater(this.where, this.field, var6, var4, var5);
         } else {
            return null;
         }
      }
   }

   private Vset checkInnerClass(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      ClassDefinition var6 = this.field.getInnerClass();
      this.type = var6.getType();
      if (!var6.isTopLevel()) {
         var1.error(this.where, "inner.static.ref", var6.getName());
      }

      TypeExpression var7 = new TypeExpression(this.where, this.type);
      ClassDefinition var8 = var2.field.getClassDefinition();

      try {
         if (!var8.canAccess(var1, this.field)) {
            ClassDefinition var9 = var1.getClassDefinition(this.right.type);
            var1.error(this.where, "no.type.access", this.id, var9, var8.getClassDeclaration());
            return var3;
         }

         if (this.field.isProtected() && !(this.right instanceof SuperExpression) && (!(this.right instanceof FieldExpression) || ((FieldExpression)this.right).id != idSuper) && !var8.protectedAccess(var1, this.field, this.right.type)) {
            var1.error(this.where, "invalid.protected.field.use", this.field.getName(), this.field.getClassDeclaration(), this.right.type);
            return var3;
         }

         var6.noteUsedBy(var8, this.where, var1);
      } catch (ClassNotFound var10) {
         var1.error(this.where, "class.not.found", var10.name, var2.field);
      }

      var8.addDependency(this.field.getClassDeclaration());
      if (var5 == null) {
         return var7.checkValue(var1, var2, var3, var4);
      } else {
         var5.right = var7;
         return var3;
      }
   }

   public Vset checkLHS(Environment var1, Context var2, Vset var3, Hashtable var4) {
      boolean var5 = this.field != null;
      this.checkCommon(var1, var2, var3, var4, (UnaryExpression)null, true);
      if (this.implementation != null) {
         return super.checkLHS(var1, var2, var3, var4);
      } else {
         if (this.field != null && this.field.isFinal() && !var5) {
            if (this.field.isBlankFinal()) {
               if (this.field.isStatic()) {
                  if (this.right != null) {
                     var1.error(this.where, "qualified.static.final.assign");
                  }
               } else if (this.right != null && this.right.op != 82) {
                  var1.error(this.where, "bad.qualified.final.assign", this.field.getName());
                  return var3;
               }

               var3 = checkFinalAssign(var1, var2, var3, this.where, this.field);
            } else {
               var1.error(this.where, "assign.to.final", this.id);
            }
         }

         return var3;
      }
   }

   public Vset checkAssignOp(Environment var1, Context var2, Vset var3, Hashtable var4, Expression var5) {
      this.checkCommon(var1, var2, var3, var4, (UnaryExpression)null, true);
      if (this.implementation != null) {
         return super.checkLHS(var1, var2, var3, var4);
      } else {
         if (this.field != null && this.field.isFinal()) {
            var1.error(this.where, "assign.to.final", this.id);
         }

         return var3;
      }
   }

   public static Vset checkFinalAssign(Environment var0, Context var1, Vset var2, long var3, MemberDefinition var5) {
      if (var5.isBlankFinal() && var5.getClassDefinition() == var1.field.getClassDefinition()) {
         int var8 = var1.getFieldNumber(var5);
         if (var8 >= 0 && var2.testVarUnassigned(var8)) {
            var2 = var2.addVar(var8);
         } else {
            Identifier var7 = var5.getName();
            var0.error(var3, "assign.to.blank.final", var7);
         }
      } else {
         Identifier var6 = var5.getName();
         var0.error(var3, "assign.to.final", var6);
      }

      return var2;
   }

   private static MemberDefinition getClassLiteralCache(Environment var0, Context var1, String var2, ClassDefinition var3) {
      String var4;
      if (!var2.startsWith("[")) {
         var4 = "class$" + var2.replace('.', '$');
      } else {
         var4 = "array$" + var2.substring(1);
         var4 = var4.replace('[', '$');
         if (var2.endsWith(";")) {
            var4 = var4.substring(0, var4.length() - 1);
            var4 = var4.replace('.', '$');
         }
      }

      Identifier var5 = Identifier.lookup(var4);

      MemberDefinition var6;
      try {
         var6 = var3.getVariable(var0, var5, var3);
      } catch (ClassNotFound var8) {
         return null;
      } catch (AmbiguousMember var9) {
         return null;
      }

      return var6 != null && var6.getClassDefinition() == var3 ? var6 : var0.makeMemberDefinition(var0, var3.getWhere(), var3, (String)null, 524296, Type.tClassDesc, var5, (IdentifierToken[])null, (IdentifierToken[])null, (Object)null);
   }

   private Expression makeClassLiteralCacheRef(Environment var1, Context var2, MemberDefinition var3, MemberDefinition var4, String var5) {
      TypeExpression var6 = new TypeExpression(this.where, var4.getClassDefinition().getType());
      FieldExpression var7 = new FieldExpression(this.where, var6, var4);
      NotEqualExpression var8 = new NotEqualExpression(this.where, var7.copyInline(var2), new NullExpression(this.where));
      TypeExpression var9 = new TypeExpression(this.where, var3.getClassDefinition().getType());
      StringExpression var10 = new StringExpression(this.where, var5);
      Expression[] var11 = new Expression[]{var10};
      MethodExpression var12 = new MethodExpression(this.where, var9, var3, var11);
      AssignExpression var13 = new AssignExpression(this.where, var7.copyInline(var2), var12);
      return new ConditionalExpression(this.where, var8, var7, var13);
   }

   private Expression makeClassLiteralInlineRef(Environment var1, Context var2, MemberDefinition var3, String var4) {
      TypeExpression var5 = new TypeExpression(this.where, var3.getClassDefinition().getType());
      StringExpression var6 = new StringExpression(this.where, var4);
      Expression[] var7 = new Expression[]{var6};
      MethodExpression var8 = new MethodExpression(this.where, var5, var3, var7);
      return var8;
   }

   public boolean isConstant() {
      if (this.implementation != null) {
         return this.implementation.isConstant();
      } else {
         return this.field == null || this.right != null && !(this.right instanceof TypeExpression) && (this.right.op != 82 || this.right.where != this.where) ? false : this.field.isConstant();
      }
   }

   public Expression inline(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inline(var1, var2);
      } else {
         Expression var3 = this.inlineValue(var1, var2);
         if (var3 instanceof FieldExpression) {
            FieldExpression var4 = (FieldExpression)var3;
            if (var4.right != null && var4.right.op == 82) {
               return null;
            }
         }

         return var3;
      }
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineValue(var1, var2);
      } else {
         try {
            if (this.field == null) {
               return this;
            } else {
               Expression var3;
               if (this.field.isFinal()) {
                  var3 = (Expression)this.field.getValue(var1);
                  if (var3 != null && var3.isConstant()) {
                     var3 = var3.copyInline(var2);
                     var3.where = this.where;
                     return (new CommaExpression(this.where, this.right, var3)).inlineValue(var1, var2);
                  }
               }

               if (this.right != null) {
                  if (this.field.isStatic()) {
                     var3 = this.right.inline(var1, var2);
                     this.right = null;
                     if (var3 != null) {
                        return new CommaExpression(this.where, var3, this);
                     }
                  } else {
                     this.right = this.right.inlineValue(var1, var2);
                  }
               }

               return this;
            }
         } catch (ClassNotFound var4) {
            throw new CompilerError(var4);
         }
      }
   }

   public Expression inlineLHS(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineLHS(var1, var2);
      } else {
         if (this.right != null) {
            if (this.field.isStatic()) {
               Expression var3 = this.right.inline(var1, var2);
               this.right = null;
               if (var3 != null) {
                  return new CommaExpression(this.where, var3, this);
               }
            } else {
               this.right = this.right.inlineValue(var1, var2);
            }
         }

         return this;
      }
   }

   public Expression copyInline(Context var1) {
      return this.implementation != null ? this.implementation.copyInline(var1) : super.copyInline(var1);
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (this.implementation != null) {
         return this.implementation.costInline(var1, var2, var3);
      } else if (var3 == null) {
         return 3 + (this.right == null ? 0 : this.right.costInline(var1, var2, var3));
      } else {
         ClassDefinition var4 = var3.field.getClassDefinition();

         try {
            if (var4.permitInlinedAccess(var2, this.field.getClassDeclaration()) && var4.permitInlinedAccess(var2, this.field)) {
               if (this.right == null) {
                  return 3;
               }

               ClassDeclaration var5 = var2.getClassDeclaration(this.right.type);
               if (var4.permitInlinedAccess(var2, var5)) {
                  return 3 + this.right.costInline(var1, var2, var3);
               }
            }
         } catch (ClassNotFound var6) {
         }

         return var1;
      }
   }

   int codeLValue(Environment var1, Context var2, Assembler var3) {
      if (this.implementation != null) {
         throw new CompilerError("codeLValue");
      } else if (this.field.isStatic()) {
         if (this.right != null) {
            this.right.code(var1, var2, var3);
            return 1;
         } else {
            return 0;
         }
      } else {
         this.right.codeValue(var1, var2, var3);
         return 1;
      }
   }

   void codeLoad(Environment var1, Context var2, Assembler var3) {
      if (this.field == null) {
         throw new CompilerError("should not be null");
      } else {
         if (this.field.isStatic()) {
            var3.add(this.where, 178, this.field);
         } else {
            var3.add(this.where, 180, this.field);
         }

      }
   }

   void codeStore(Environment var1, Context var2, Assembler var3) {
      if (this.field.isStatic()) {
         var3.add(this.where, 179, this.field);
      } else {
         var3.add(this.where, 181, this.field);
      }

   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeLValue(var1, var2, var3);
      this.codeLoad(var1, var2, var3);
   }

   public void print(PrintStream var1) {
      var1.print("(");
      if (this.right != null) {
         this.right.print(var1);
      } else {
         var1.print("<empty>");
      }

      var1.print("." + this.id + ")");
      if (this.implementation != null) {
         var1.print("/IMPL=");
         this.implementation.print(var1);
      }

   }
}
