package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class InlineMethodExpression extends Expression {
   MemberDefinition field;
   Statement body;

   InlineMethodExpression(long var1, Type var3, MemberDefinition var4, Statement var5) {
      super(150, var1, var3);
      this.field = var4;
      this.body = var5;
   }

   public Expression inline(Environment var1, Context var2) {
      this.body = this.body.inline(var1, new Context(var2, this));
      if (this.body == null) {
         return null;
      } else if (this.body.op == 149) {
         Expression var3 = ((InlineReturnStatement)this.body).expr;
         if (var3 != null && this.type.isType(11)) {
            throw new CompilerError("value on inline-void return");
         } else {
            return var3;
         }
      } else {
         return this;
      }
   }

   public Expression inlineValue(Environment var1, Context var2) {
      return this.inline(var1, var2);
   }

   public Expression copyInline(Context var1) {
      InlineMethodExpression var2 = (InlineMethodExpression)this.clone();
      if (this.body != null) {
         var2.body = this.body.copyInline(var1, true);
      }

      return var2;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      super.code(var1, var2, var3);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      this.body.code(var1, var4, var3);
      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + "\n");
      this.body.print(var1, 1);
      var1.print(")");
   }
}
