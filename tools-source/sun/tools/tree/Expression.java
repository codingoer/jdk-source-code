package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class Expression extends Node {
   Type type;

   Expression(int var1, long var2, Type var4) {
      super(var1, var2);
      this.type = var4;
   }

   public Expression getImplementation() {
      return this;
   }

   public Type getType() {
      return this.type;
   }

   int precedence() {
      return this.op < opPrecedence.length ? opPrecedence[this.op] : 100;
   }

   public Expression order() {
      return this;
   }

   public boolean isConstant() {
      return false;
   }

   public Object getValue() {
      return null;
   }

   public boolean equals(int var1) {
      return false;
   }

   public boolean equals(boolean var1) {
      return false;
   }

   public boolean equals(Identifier var1) {
      return false;
   }

   public boolean equals(String var1) {
      return false;
   }

   public boolean isNull() {
      return false;
   }

   public boolean isNonNull() {
      return false;
   }

   public boolean equalsDefault() {
      return false;
   }

   Type toType(Environment var1, Context var2) {
      var1.error(this.where, "invalid.type.expr");
      return Type.tError;
   }

   public boolean fitsType(Environment var1, Context var2, Type var3) {
      try {
         if (var1.isMoreSpecific(this.type, var3)) {
            return true;
         } else {
            if (this.type.isType(4) && this.isConstant() && var2 != null) {
               Expression var4 = this.inlineValue(var1, var2);
               if (var4 != this && var4 instanceof ConstantExpression) {
                  return var4.fitsType(var1, var2, var3);
               }
            }

            return false;
         }
      } catch (ClassNotFound var5) {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean fitsType(Environment var1, Type var2) {
      return this.fitsType(var1, (Context)null, var2);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return var3;
   }

   public Vset checkInitializer(Environment var1, Context var2, Vset var3, Type var4, Hashtable var5) {
      return this.checkValue(var1, var2, var3, var5);
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      throw new CompilerError("check failed");
   }

   public Vset checkLHS(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var1.error(this.where, "invalid.lhs.assignment");
      this.type = Type.tError;
      return var3;
   }

   public FieldUpdater getAssigner(Environment var1, Context var2) {
      throw new CompilerError("getAssigner lhs");
   }

   public FieldUpdater getUpdater(Environment var1, Context var2) {
      throw new CompilerError("getUpdater lhs");
   }

   public Vset checkAssignOp(Environment var1, Context var2, Vset var3, Hashtable var4, Expression var5) {
      if (var5 instanceof IncDecExpression) {
         var1.error(this.where, "invalid.arg", opNames[var5.op]);
      } else {
         var1.error(this.where, "invalid.lhs.assignment");
      }

      this.type = Type.tError;
      return var3;
   }

   public Vset checkAmbigName(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      return this.checkValue(var1, var2, var3, var4);
   }

   public ConditionVars checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4) {
      ConditionVars var5 = new ConditionVars();
      this.checkCondition(var1, var2, var3, var4, var5);
      return var5;
   }

   public void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5) {
      var5.vsTrue = var5.vsFalse = this.checkValue(var1, var2, var3, var4);
      var5.vsFalse = var5.vsFalse.copy();
   }

   Expression eval() {
      return this;
   }

   Expression simplify() {
      return this;
   }

   public Expression inline(Environment var1, Context var2) {
      return null;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      return this;
   }

   protected StringBuffer inlineValueSB(Environment var1, Context var2, StringBuffer var3) {
      Expression var4 = this.inlineValue(var1, var2);
      Object var5 = var4.getValue();
      if (var5 == null && !var4.isNull()) {
         return null;
      } else {
         if (this.type == Type.tChar) {
            var3.append((char)(Integer)var5);
         } else if (this.type == Type.tBoolean) {
            var3.append((Integer)var5 != 0);
         } else {
            var3.append(var5);
         }

         return var3;
      }
   }

   public Expression inlineLHS(Environment var1, Context var2) {
      return null;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1;
   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      if (this.type.isType(0)) {
         this.codeValue(var1, var2, var3);
         var3.add(this.where, var5 ? 154 : 153, var4, var5);
      } else {
         throw new CompilerError("codeBranch " + opNames[this.op]);
      }
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      if (this.type.isType(0)) {
         Label var4 = new Label();
         Label var5 = new Label();
         this.codeBranch(var1, var2, var3, var4, true);
         var3.add(true, this.where, 18, new Integer(0));
         var3.add(true, this.where, 167, var5);
         var3.add(var4);
         var3.add(true, this.where, 18, new Integer(1));
         var3.add(var5);
      } else {
         throw new CompilerError("codeValue");
      }
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.codeValue(var1, var2, var3);
      switch (this.type.getTypeCode()) {
         case 5:
         case 7:
            var3.add(this.where, 88);
         case 11:
            break;
         default:
            var3.add(this.where, 87);
      }

   }

   int codeLValue(Environment var1, Context var2, Assembler var3) {
      this.print(System.out);
      throw new CompilerError("invalid lhs");
   }

   void codeLoad(Environment var1, Context var2, Assembler var3) {
      this.print(System.out);
      throw new CompilerError("invalid load");
   }

   void codeStore(Environment var1, Context var2, Assembler var3) {
      this.print(System.out);
      throw new CompilerError("invalid store");
   }

   void ensureString(Environment var1, Context var2, Assembler var3) throws ClassNotFound, AmbiguousMember {
      if (this.type != Type.tString || !this.isNonNull()) {
         ClassDefinition var4 = var2.field.getClassDefinition();
         ClassDeclaration var5 = var1.getClassDeclaration(Type.tString);
         ClassDefinition var6 = var5.getClassDefinition(var1);
         Type[] var7;
         MemberDefinition var8;
         if (this.type.inMask(1792)) {
            if (this.type != Type.tString) {
               var7 = new Type[]{Type.tObject};
               var8 = var6.matchMethod(var1, var4, idValueOf, var7);
               var3.add(this.where, 184, var8);
            }

            if (!this.type.inMask(768)) {
               var7 = new Type[]{Type.tString};
               var8 = var6.matchMethod(var1, var4, idValueOf, var7);
               var3.add(this.where, 184, var8);
            }
         } else {
            var7 = new Type[]{this.type};
            var8 = var6.matchMethod(var1, var4, idValueOf, var7);
            var3.add(this.where, 184, var8);
         }

      }
   }

   void codeAppend(Environment var1, Context var2, Assembler var3, ClassDeclaration var4, boolean var5) throws ClassNotFound, AmbiguousMember {
      ClassDefinition var6 = var2.field.getClassDefinition();
      ClassDefinition var7 = var4.getClassDefinition(var1);
      MemberDefinition var8;
      Type[] var9;
      if (var5) {
         var3.add(this.where, 187, var4);
         var3.add(this.where, 89);
         if (this.equals("")) {
            var8 = var7.matchMethod(var1, var6, idInit);
         } else {
            this.codeValue(var1, var2, var3);
            this.ensureString(var1, var2, var3);
            var9 = new Type[]{Type.tString};
            var8 = var7.matchMethod(var1, var6, idInit, var9);
         }

         var3.add(this.where, 183, var8);
      } else {
         this.codeValue(var1, var2, var3);
         var9 = new Type[]{this.type.inMask(1792) && this.type != Type.tString ? Type.tObject : this.type};
         var8 = var7.matchMethod(var1, var6, idAppend, var9);
         var3.add(this.where, 182, var8);
      }

   }

   void codeDup(Environment var1, Context var2, Assembler var3, int var4, int var5) {
      switch (var4) {
         case 0:
            return;
         case 1:
            switch (var5) {
               case 0:
                  var3.add(this.where, 89);
                  return;
               case 1:
                  var3.add(this.where, 90);
                  return;
               case 2:
                  var3.add(this.where, 91);
                  return;
               default:
                  throw new CompilerError("can't dup: " + var4 + ", " + var5);
            }
         case 2:
            switch (var5) {
               case 0:
                  var3.add(this.where, 92);
                  return;
               case 1:
                  var3.add(this.where, 93);
                  return;
               case 2:
                  var3.add(this.where, 94);
                  return;
            }
      }

      throw new CompilerError("can't dup: " + var4 + ", " + var5);
   }

   void codeConversion(Environment var1, Context var2, Assembler var3, Type var4, Type var5) {
      int var6 = var4.getTypeCode();
      int var7 = var5.getTypeCode();
      switch (var7) {
         case 0:
            if (var6 == 0) {
               return;
            }
            break;
         case 1:
            if (var6 != 1) {
               this.codeConversion(var1, var2, var3, var4, Type.tInt);
               var3.add(this.where, 145);
            }

            return;
         case 2:
            if (var6 != 2) {
               this.codeConversion(var1, var2, var3, var4, Type.tInt);
               var3.add(this.where, 146);
            }

            return;
         case 3:
            if (var6 != 3) {
               this.codeConversion(var1, var2, var3, var4, Type.tInt);
               var3.add(this.where, 147);
            }

            return;
         case 4:
            switch (var6) {
               case 1:
               case 2:
               case 3:
               case 4:
                  return;
               case 5:
                  var3.add(this.where, 136);
                  return;
               case 6:
                  var3.add(this.where, 139);
                  return;
               case 7:
                  var3.add(this.where, 142);
                  return;
               default:
                  throw new CompilerError("codeConversion: " + var6 + ", " + var7);
            }
         case 5:
            switch (var6) {
               case 1:
               case 2:
               case 3:
               case 4:
                  var3.add(this.where, 133);
                  return;
               case 5:
                  return;
               case 6:
                  var3.add(this.where, 140);
                  return;
               case 7:
                  var3.add(this.where, 143);
                  return;
               default:
                  throw new CompilerError("codeConversion: " + var6 + ", " + var7);
            }
         case 6:
            switch (var6) {
               case 1:
               case 2:
               case 3:
               case 4:
                  var3.add(this.where, 134);
                  return;
               case 5:
                  var3.add(this.where, 137);
                  return;
               case 6:
                  return;
               case 7:
                  var3.add(this.where, 144);
                  return;
               default:
                  throw new CompilerError("codeConversion: " + var6 + ", " + var7);
            }
         case 7:
            switch (var6) {
               case 1:
               case 2:
               case 3:
               case 4:
                  var3.add(this.where, 135);
                  return;
               case 5:
                  var3.add(this.where, 138);
                  return;
               case 6:
                  var3.add(this.where, 141);
                  return;
               case 7:
                  return;
            }
         case 8:
         default:
            break;
         case 9:
            switch (var6) {
               case 8:
                  return;
               case 9:
               case 10:
                  try {
                     if (!var1.implicitCast(var4, var5)) {
                        var3.add(this.where, 192, var5);
                     }

                     return;
                  } catch (ClassNotFound var10) {
                     throw new CompilerError(var10);
                  }
               default:
                  throw new CompilerError("codeConversion: " + var6 + ", " + var7);
            }
         case 10:
            switch (var6) {
               case 8:
                  return;
               case 9:
               case 10:
                  try {
                     if (!var1.implicitCast(var4, var5)) {
                        var3.add(this.where, 192, var1.getClassDeclaration(var5));
                     }

                     return;
                  } catch (ClassNotFound var9) {
                     throw new CompilerError(var9);
                  }
            }
      }

      throw new CompilerError("codeConversion: " + var6 + ", " + var7);
   }

   public Expression firstConstructor() {
      return null;
   }

   public Expression copyInline(Context var1) {
      return (Expression)this.clone();
   }

   public void print(PrintStream var1) {
      var1.print(opNames[this.op]);
   }
}
