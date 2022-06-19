package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class UnaryExpression extends Expression {
   Expression right;

   UnaryExpression(int var1, long var2, Type var4, Expression var5) {
      super(var1, var2, var4);
      this.right = var5;
   }

   public Expression order() {
      if (this.precedence() > this.right.precedence()) {
         UnaryExpression var1 = (UnaryExpression)this.right;
         this.right = var1.right;
         var1.right = this.order();
         return var1;
      } else {
         return this;
      }
   }

   void selectType(Environment var1, Context var2, int var3) {
      throw new CompilerError("selectType: " + opNames[this.op]);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.right.checkValue(var1, var2, var3, var4);
      int var5 = this.right.type.getTypeMask();
      this.selectType(var1, var2, var5);
      if ((var5 & 8192) == 0 && this.type.isType(13)) {
         var1.error(this.where, "invalid.arg", opNames[this.op]);
      }

      return var3;
   }

   public boolean isConstant() {
      switch (this.op) {
         case 35:
         case 36:
         case 37:
         case 38:
         case 55:
         case 56:
            return this.right.isConstant();
         default:
            return false;
      }
   }

   Expression eval(int var1) {
      return this;
   }

   Expression eval(long var1) {
      return this;
   }

   Expression eval(float var1) {
      return this;
   }

   Expression eval(double var1) {
      return this;
   }

   Expression eval(boolean var1) {
      return this;
   }

   Expression eval(String var1) {
      return this;
   }

   Expression eval() {
      switch (this.right.op) {
         case 61:
            return this.eval(((BooleanExpression)this.right).value);
         case 62:
         case 63:
         case 64:
         case 65:
            return this.eval(((IntegerExpression)this.right).value);
         case 66:
            return this.eval(((LongExpression)this.right).value);
         case 67:
            return this.eval(((FloatExpression)this.right).value);
         case 68:
            return this.eval(((DoubleExpression)this.right).value);
         case 69:
            return this.eval(((StringExpression)this.right).value);
         default:
            return this;
      }
   }

   public Expression inline(Environment var1, Context var2) {
      return this.right.inline(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.right = this.right.inlineValue(var1, var2);

      try {
         return this.eval().simplify();
      } catch (ArithmeticException var4) {
         return this;
      }
   }

   public Expression copyInline(Context var1) {
      UnaryExpression var2 = (UnaryExpression)this.clone();
      if (this.right != null) {
         var2.right = this.right.copyInline(var1);
      }

      return var2;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + this.right.costInline(var1, var2, var3);
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      this.right.print(var1);
      var1.print(")");
   }
}
