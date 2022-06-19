package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BinaryExpression extends UnaryExpression {
   Expression left;

   BinaryExpression(int var1, long var2, Type var4, Expression var5, Expression var6) {
      super(var1, var2, var4, var6);
      this.left = var5;
   }

   public Expression order() {
      if (this.precedence() > this.left.precedence()) {
         UnaryExpression var1 = (UnaryExpression)this.left;
         this.left = var1.right;
         var1.right = this.order();
         return var1;
      } else {
         return this;
      }
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.left.checkValue(var1, var2, var3, var4);
      var3 = this.right.checkValue(var1, var2, var3, var4);
      int var5 = this.left.type.getTypeMask() | this.right.type.getTypeMask();
      if ((var5 & 8192) != 0) {
         return var3;
      } else {
         this.selectType(var1, var2, var5);
         if (this.type.isType(13)) {
            var1.error(this.where, "invalid.args", opNames[this.op]);
         }

         return var3;
      }
   }

   public boolean isConstant() {
      switch (this.op) {
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
            return this.left.isConstant() && this.right.isConstant();
         case 25:
         default:
            return false;
      }
   }

   Expression eval(int var1, int var2) {
      return this;
   }

   Expression eval(long var1, long var3) {
      return this;
   }

   Expression eval(float var1, float var2) {
      return this;
   }

   Expression eval(double var1, double var3) {
      return this;
   }

   Expression eval(boolean var1, boolean var2) {
      return this;
   }

   Expression eval(String var1, String var2) {
      return this;
   }

   Expression eval() {
      if (this.left.op == this.right.op) {
         switch (this.left.op) {
            case 61:
               return this.eval(((BooleanExpression)this.left).value, ((BooleanExpression)this.right).value);
            case 62:
            case 63:
            case 64:
            case 65:
               return this.eval(((IntegerExpression)this.left).value, ((IntegerExpression)this.right).value);
            case 66:
               return this.eval(((LongExpression)this.left).value, ((LongExpression)this.right).value);
            case 67:
               return this.eval(((FloatExpression)this.left).value, ((FloatExpression)this.right).value);
            case 68:
               return this.eval(((DoubleExpression)this.left).value, ((DoubleExpression)this.right).value);
            case 69:
               return this.eval(((StringExpression)this.left).value, ((StringExpression)this.right).value);
         }
      }

      return this;
   }

   public Expression inline(Environment var1, Context var2) {
      this.left = this.left.inline(var1, var2);
      this.right = this.right.inline(var1, var2);
      return (Expression)(this.left == null ? this.right : new CommaExpression(this.where, this.left, this.right));
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.left = this.left.inlineValue(var1, var2);
      this.right = this.right.inlineValue(var1, var2);

      try {
         return this.eval().simplify();
      } catch (ArithmeticException var4) {
         return this;
      }
   }

   public Expression copyInline(Context var1) {
      BinaryExpression var2 = (BinaryExpression)this.clone();
      if (this.left != null) {
         var2.left = this.left.copyInline(var1);
      }

      if (this.right != null) {
         var2.right = this.right.copyInline(var1);
      }

      return var2;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + (this.left != null ? this.left.costInline(var1, var2, var3) : 0) + (this.right != null ? this.right.costInline(var1, var2, var3) : 0);
   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      throw new CompilerError("codeOperation: " + opNames[this.op]);
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
         this.left.codeValue(var1, var2, var3);
         this.right.codeValue(var1, var2, var3);
         this.codeOperation(var1, var2, var3);
      }

   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      if (this.left != null) {
         this.left.print(var1);
      } else {
         var1.print("<null>");
      }

      var1.print(" ");
      if (this.right != null) {
         this.right.print(var1);
      } else {
         var1.print("<null>");
      }

      var1.print(")");
   }
}
