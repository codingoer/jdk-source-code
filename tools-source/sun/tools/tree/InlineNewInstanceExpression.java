package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class InlineNewInstanceExpression extends Expression {
   MemberDefinition field;
   Statement body;

   InlineNewInstanceExpression(long var1, Type var3, MemberDefinition var4, Statement var5) {
      super(151, var1, var3);
      this.field = var4;
      this.body = var5;
   }

   public Expression inline(Environment var1, Context var2) {
      return this.inlineValue(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.body != null) {
         LocalMember var3 = (LocalMember)this.field.getArguments().elementAt(0);
         Context var4 = new Context(var2, this);
         var4.declare(var1, var3);
         this.body = this.body.inline(var1, var4);
      }

      if (this.body != null && this.body.op == 149) {
         this.body = null;
      }

      return this;
   }

   public Expression copyInline(Context var1) {
      InlineNewInstanceExpression var2 = (InlineNewInstanceExpression)this.clone();
      var2.body = this.body.copyInline(var1, true);
      return var2;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.codeCommon(var1, var2, var3, false);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeCommon(var1, var2, var3, true);
   }

   private void codeCommon(Environment var1, Context var2, Assembler var3, boolean var4) {
      var3.add(this.where, 187, this.field.getClassDeclaration());
      if (this.body != null) {
         LocalMember var5 = (LocalMember)this.field.getArguments().elementAt(0);
         CodeContext var6 = new CodeContext(var2, this);
         var6.declare(var1, var5);
         var3.add(this.where, 58, new Integer(var5.number));
         this.body.code(var1, var6, var3);
         var3.add(var6.breakLabel);
         if (var4) {
            var3.add(this.where, 25, new Integer(var5.number));
         }
      }

   }

   public void print(PrintStream var1) {
      LocalMember var2 = (LocalMember)this.field.getArguments().elementAt(0);
      var1.println("(" + opNames[this.op] + "#" + var2.hashCode() + "=" + this.field.hashCode());
      if (this.body != null) {
         this.body.print(var1, 1);
      } else {
         var1.print("<empty>");
      }

      var1.print(")");
   }
}
