package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ArrayExpression extends NaryExpression {
   public ArrayExpression(long var1, Expression[] var3) {
      super(57, var1, Type.tError, (Expression)null, var3);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var1.error(this.where, "invalid.array.expr");
      return var3;
   }

   public Vset checkInitializer(Environment var1, Context var2, Vset var3, Type var4, Hashtable var5) {
      if (!var4.isType(9)) {
         if (!var4.isType(13)) {
            var1.error(this.where, "invalid.array.init", var4);
         }

         return var3;
      } else {
         this.type = var4;
         var4 = var4.getElementType();

         for(int var6 = 0; var6 < this.args.length; ++var6) {
            var3 = this.args[var6].checkInitializer(var1, var2, var3, var4, var5);
            this.args[var6] = this.convert(var1, var2, var4, this.args[var6]);
         }

         return var3;
      }
   }

   public Expression inline(Environment var1, Context var2) {
      Object var3 = null;

      for(int var4 = 0; var4 < this.args.length; ++var4) {
         this.args[var4] = this.args[var4].inline(var1, var2);
         if (this.args[var4] != null) {
            var3 = var3 == null ? this.args[var4] : new CommaExpression(this.where, (Expression)var3, this.args[var4]);
         }
      }

      return (Expression)var3;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      for(int var3 = 0; var3 < this.args.length; ++var3) {
         this.args[var3] = this.args[var3].inlineValue(var1, var2);
      }

      return this;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      boolean var4 = false;
      var3.add(this.where, 18, new Integer(this.args.length));
      switch (this.type.getElementType().getTypeCode()) {
         case 0:
            var3.add(this.where, 188, new Integer(4));
            break;
         case 1:
            var3.add(this.where, 188, new Integer(8));
            break;
         case 2:
            var3.add(this.where, 188, new Integer(5));
            break;
         case 3:
            var3.add(this.where, 188, new Integer(9));
            break;
         case 4:
            var3.add(this.where, 188, new Integer(10));
            break;
         case 5:
            var3.add(this.where, 188, new Integer(11));
            break;
         case 6:
            var3.add(this.where, 188, new Integer(6));
            break;
         case 7:
            var3.add(this.where, 188, new Integer(7));
            break;
         case 8:
         default:
            throw new CompilerError("codeValue");
         case 9:
            var3.add(this.where, 189, this.type.getElementType());
            break;
         case 10:
            var3.add(this.where, 189, var1.getClassDeclaration(this.type.getElementType()));
      }

      for(int var5 = 0; var5 < this.args.length; ++var5) {
         if (!this.args[var5].equalsDefault()) {
            var3.add(this.where, 89);
            var3.add(this.where, 18, new Integer(var5));
            this.args[var5].codeValue(var1, var2, var3);
            switch (this.type.getElementType().getTypeCode()) {
               case 0:
               case 1:
                  var3.add(this.where, 84);
                  break;
               case 2:
                  var3.add(this.where, 85);
                  break;
               case 3:
                  var3.add(this.where, 86);
                  break;
               default:
                  var3.add(this.where, 79 + this.type.getElementType().getTypeCodeOffset());
            }
         }
      }

   }
}
