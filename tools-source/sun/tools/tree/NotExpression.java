package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class NotExpression extends UnaryExpression {
   public NotExpression(long var1, Expression var3) {
      super(37, var1, Type.tBoolean, var3);
   }

   void selectType(Environment var1, Context var2, int var3) {
      this.right = this.convert(var1, var2, Type.tBoolean, this.right);
   }

   public void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5) {
      this.right.checkCondition(var1, var2, var3, var4, var5);
      this.right = this.convert(var1, var2, Type.tBoolean, this.right);
      Vset var6 = var5.vsFalse;
      var5.vsFalse = var5.vsTrue;
      var5.vsTrue = var6;
   }

   Expression eval(boolean var1) {
      return new BooleanExpression(this.where, !var1);
   }

   Expression simplify() {
      switch (this.right.op) {
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
            BinaryExpression var1 = (BinaryExpression)this.right;
            if (var1.left.type.inMask(192)) {
               return this;
            } else {
               switch (this.right.op) {
                  case 19:
                     return new EqualExpression(this.where, var1.left, var1.right);
                  case 20:
                     return new NotEqualExpression(this.where, var1.left, var1.right);
                  case 21:
                     return new LessExpression(this.where, var1.left, var1.right);
                  case 22:
                     return new LessOrEqualExpression(this.where, var1.left, var1.right);
                  case 23:
                     return new GreaterExpression(this.where, var1.left, var1.right);
                  case 24:
                     return new GreaterOrEqualExpression(this.where, var1.left, var1.right);
                  default:
                     return this;
               }
            }
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         default:
            return this;
         case 37:
            return ((NotExpression)this.right).right;
      }
   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      this.right.codeBranch(var1, var2, var3, var4, !var5);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.right.codeValue(var1, var2, var3);
      var3.add(this.where, 18, new Integer(1));
      var3.add(this.where, 130);
   }
}
