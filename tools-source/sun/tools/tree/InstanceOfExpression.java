package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class InstanceOfExpression extends BinaryExpression {
   public InstanceOfExpression(long var1, Expression var3, Expression var4) {
      super(25, var1, Type.tBoolean, var3, var4);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.left.checkValue(var1, var2, var3, var4);
      this.right = new TypeExpression(this.right.where, this.right.toType(var1, var2));
      if (!this.right.type.isType(13) && !this.left.type.isType(13)) {
         if (!this.right.type.inMask(1536)) {
            var1.error(this.right.where, "invalid.arg.type", this.right.type, opNames[this.op]);
            return var3;
         } else {
            try {
               if (!var1.explicitCast(this.left.type, this.right.type)) {
                  var1.error(this.where, "invalid.instanceof", this.left.type, this.right.type);
               }
            } catch (ClassNotFound var6) {
               var1.error(this.where, "class.not.found", var6.name, opNames[this.op]);
            }

            return var3;
         }
      } else {
         return var3;
      }
   }

   public Expression inline(Environment var1, Context var2) {
      return this.left.inline(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.left = this.left.inlineValue(var1, var2);
      return this;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (var3 == null) {
         return 1 + this.left.costInline(var1, var2, var3);
      } else {
         ClassDefinition var4 = var3.field.getClassDefinition();

         try {
            if (this.right.type.isType(9) || var4.permitInlinedAccess(var2, var2.getClassDeclaration(this.right.type))) {
               return 1 + this.left.costInline(var1, var2, var3);
            }
         } catch (ClassNotFound var6) {
         }

         return var1;
      }
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.left.codeValue(var1, var2, var3);
      if (this.right.type.isType(10)) {
         var3.add(this.where, 193, var1.getClassDeclaration(this.right.type));
      } else {
         var3.add(this.where, 193, this.right.type);
      }

   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      this.codeValue(var1, var2, var3);
      var3.add(this.where, var5 ? 154 : 153, var4, var5);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.left.code(var1, var2, var3);
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      this.left.print(var1);
      var1.print(" ");
      if (this.right.op == 147) {
         var1.print(this.right.type.toString());
      } else {
         this.right.print(var1);
      }

      var1.print(")");
   }
}
