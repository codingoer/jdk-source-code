package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class CommaExpression extends BinaryExpression {
   public CommaExpression(long var1, Expression var3, Expression var4) {
      super(0, var1, var4 != null ? var4.type : Type.tVoid, var3, var4);
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.left.check(var1, var2, var3, var4);
      var3 = this.right.check(var1, var2, var3, var4);
      return var3;
   }

   void selectType(Environment var1, Context var2, int var3) {
      this.type = this.right.type;
   }

   Expression simplify() {
      if (this.left == null) {
         return this.right;
      } else {
         return (Expression)(this.right == null ? this.left : this);
      }
   }

   public Expression inline(Environment var1, Context var2) {
      if (this.left != null) {
         this.left = this.left.inline(var1, var2);
      }

      if (this.right != null) {
         this.right = this.right.inline(var1, var2);
      }

      return this.simplify();
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.left != null) {
         this.left = this.left.inline(var1, var2);
      }

      if (this.right != null) {
         this.right = this.right.inlineValue(var1, var2);
      }

      return this.simplify();
   }

   int codeLValue(Environment var1, Context var2, Assembler var3) {
      if (this.right == null) {
         return super.codeLValue(var1, var2, var3);
      } else {
         if (this.left != null) {
            this.left.code(var1, var2, var3);
         }

         return this.right.codeLValue(var1, var2, var3);
      }
   }

   void codeLoad(Environment var1, Context var2, Assembler var3) {
      if (this.right == null) {
         super.codeLoad(var1, var2, var3);
      } else {
         this.right.codeLoad(var1, var2, var3);
      }

   }

   void codeStore(Environment var1, Context var2, Assembler var3) {
      if (this.right == null) {
         super.codeStore(var1, var2, var3);
      } else {
         this.right.codeStore(var1, var2, var3);
      }

   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      if (this.left != null) {
         this.left.code(var1, var2, var3);
      }

      this.right.codeValue(var1, var2, var3);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      if (this.left != null) {
         this.left.code(var1, var2, var3);
      }

      if (this.right != null) {
         this.right.code(var1, var2, var3);
      }

   }
}
