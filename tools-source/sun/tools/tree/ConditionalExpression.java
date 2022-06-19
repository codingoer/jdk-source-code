package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ConditionalExpression extends BinaryExpression {
   Expression cond;

   public ConditionalExpression(long var1, Expression var3, Expression var4, Expression var5) {
      super(13, var1, Type.tError, var4, var5);
      this.cond = var3;
   }

   public Expression order() {
      if (this.precedence() > this.cond.precedence()) {
         UnaryExpression var1 = (UnaryExpression)this.cond;
         this.cond = var1.right;
         var1.right = this.order();
         return var1;
      } else {
         return this;
      }
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      ConditionVars var5 = this.cond.checkCondition(var1, var2, var3, var4);
      var3 = this.left.checkValue(var1, var2, var5.vsTrue, var4).join(this.right.checkValue(var1, var2, var5.vsFalse, var4));
      this.cond = this.convert(var1, var2, Type.tBoolean, this.cond);
      int var6 = this.left.type.getTypeMask() | this.right.type.getTypeMask();
      if ((var6 & 8192) != 0) {
         this.type = Type.tError;
         return var3;
      } else {
         if (this.left.type.equals(this.right.type)) {
            this.type = this.left.type;
         } else if ((var6 & 128) != 0) {
            this.type = Type.tDouble;
         } else if ((var6 & 64) != 0) {
            this.type = Type.tFloat;
         } else if ((var6 & 32) != 0) {
            this.type = Type.tLong;
         } else if ((var6 & 1792) != 0) {
            try {
               this.type = var1.implicitCast(this.right.type, this.left.type) ? this.left.type : this.right.type;
            } catch (ClassNotFound var8) {
               this.type = Type.tError;
            }
         } else if ((var6 & 4) != 0 && this.left.fitsType(var1, var2, Type.tChar) && this.right.fitsType(var1, var2, Type.tChar)) {
            this.type = Type.tChar;
         } else if ((var6 & 8) != 0 && this.left.fitsType(var1, var2, Type.tShort) && this.right.fitsType(var1, var2, Type.tShort)) {
            this.type = Type.tShort;
         } else if ((var6 & 2) != 0 && this.left.fitsType(var1, var2, Type.tByte) && this.right.fitsType(var1, var2, Type.tByte)) {
            this.type = Type.tByte;
         } else {
            this.type = Type.tInt;
         }

         this.left = this.convert(var1, var2, this.type, this.left);
         this.right = this.convert(var1, var2, this.type, this.right);
         return var3;
      }
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.cond.checkValue(var1, var2, var3, var4);
      this.cond = this.convert(var1, var2, Type.tBoolean, this.cond);
      return this.left.check(var1, var2, var3.copy(), var4).join(this.right.check(var1, var2, var3, var4));
   }

   public boolean isConstant() {
      return this.cond.isConstant() && this.left.isConstant() && this.right.isConstant();
   }

   Expression simplify() {
      if (this.cond.equals(true)) {
         return this.left;
      } else {
         return (Expression)(this.cond.equals(false) ? this.right : this);
      }
   }

   public Expression inline(Environment var1, Context var2) {
      this.left = this.left.inline(var1, var2);
      this.right = this.right.inline(var1, var2);
      if (this.left == null && this.right == null) {
         return this.cond.inline(var1, var2);
      } else {
         if (this.left == null) {
            this.left = this.right;
            this.right = null;
            this.cond = new NotExpression(this.where, this.cond);
         }

         this.cond = this.cond.inlineValue(var1, var2);
         return this.simplify();
      }
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.cond = this.cond.inlineValue(var1, var2);
      this.left = this.left.inlineValue(var1, var2);
      this.right = this.right.inlineValue(var1, var2);
      return this.simplify();
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + this.cond.costInline(var1, var2, var3) + this.left.costInline(var1, var2, var3) + (this.right == null ? 0 : this.right.costInline(var1, var2, var3));
   }

   public Expression copyInline(Context var1) {
      ConditionalExpression var2 = (ConditionalExpression)this.clone();
      var2.cond = this.cond.copyInline(var1);
      var2.left = this.left.copyInline(var1);
      var2.right = this.right == null ? null : this.right.copyInline(var1);
      return var2;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      Label var4 = new Label();
      Label var5 = new Label();
      this.cond.codeBranch(var1, var2, var3, var4, false);
      this.left.codeValue(var1, var2, var3);
      var3.add(this.where, 167, var5);
      var3.add(var4);
      this.right.codeValue(var1, var2, var3);
      var3.add(var5);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      Label var4 = new Label();
      this.cond.codeBranch(var1, var2, var3, var4, false);
      this.left.code(var1, var2, var3);
      if (this.right != null) {
         Label var5 = new Label();
         var3.add(this.where, 167, var5);
         var3.add(var4);
         this.right.code(var1, var2, var3);
         var3.add(var5);
      } else {
         var3.add(var4);
      }

   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      this.cond.print(var1);
      var1.print(" ");
      this.left.print(var1);
      var1.print(" ");
      if (this.right != null) {
         this.right.print(var1);
      } else {
         var1.print("<null>");
      }

      var1.print(")");
   }
}
