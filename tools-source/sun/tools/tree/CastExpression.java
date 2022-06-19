package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class CastExpression extends BinaryExpression {
   public CastExpression(long var1, Expression var3, Expression var4) {
      super(34, var1, var3.type, var3, var4);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.type = this.left.toType(var1, var2);
      var3 = this.right.checkValue(var1, var2, var3, var4);
      if (!this.type.isType(13) && !this.right.type.isType(13)) {
         if (this.type.equals(this.right.type)) {
            return var3;
         } else {
            try {
               if (var1.explicitCast(this.right.type, this.type)) {
                  this.right = new ConvertExpression(this.where, this.type, this.right);
                  return var3;
               }
            } catch (ClassNotFound var6) {
               var1.error(this.where, "class.not.found", var6.name, opNames[this.op]);
            }

            var1.error(this.where, "invalid.cast", this.right.type, this.type);
            return var3;
         }
      } else {
         return var3;
      }
   }

   public boolean isConstant() {
      return this.type.inMask(1792) && !this.type.equals(Type.tString) ? false : this.right.isConstant();
   }

   public Expression inline(Environment var1, Context var2) {
      return this.right.inline(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      return this.right.inlineValue(var1, var2);
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (var3 == null) {
         return 1 + this.right.costInline(var1, var2, var3);
      } else {
         ClassDefinition var4 = var3.field.getClassDefinition();

         try {
            if (this.left.type.isType(9) || var4.permitInlinedAccess(var2, var2.getClassDeclaration(this.left.type))) {
               return 1 + this.right.costInline(var1, var2, var3);
            }
         } catch (ClassNotFound var6) {
         }

         return var1;
      }
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      if (this.type.isType(13)) {
         this.left.print(var1);
      } else {
         var1.print(this.type);
      }

      var1.print(" ");
      this.right.print(var1);
      var1.print(")");
   }
}
