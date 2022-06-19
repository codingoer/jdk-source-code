package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.ArrayData;
import sun.tools.asm.Assembler;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class NewArrayExpression extends NaryExpression {
   Expression init;

   public NewArrayExpression(long var1, Expression var3, Expression[] var4) {
      super(41, var1, Type.tError, var3, var4);
   }

   public NewArrayExpression(long var1, Expression var3, Expression[] var4, Expression var5) {
      this(var1, var3, var4);
      this.init = var5;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.type = this.right.toType(var1, var2);
      boolean var5 = this.init != null;

      for(int var6 = 0; var6 < this.args.length; ++var6) {
         Expression var7 = this.args[var6];
         if (var7 == null) {
            if (var6 == 0 && !var5) {
               var1.error(this.where, "array.dim.missing");
            }

            var5 = true;
         } else {
            if (var5) {
               var1.error(var7.where, "invalid.array.dim");
            }

            var3 = var7.checkValue(var1, var2, var3, var4);
            this.args[var6] = this.convert(var1, var2, Type.tInt, var7);
         }

         this.type = Type.tArray(this.type);
      }

      if (this.init != null) {
         var3 = this.init.checkInitializer(var1, var2, var3, this.type, var4);
         this.init = this.convert(var1, var2, this.type, this.init);
      }

      return var3;
   }

   public Expression copyInline(Context var1) {
      NewArrayExpression var2 = (NewArrayExpression)super.copyInline(var1);
      if (this.init != null) {
         var2.init = this.init.copyInline(var1);
      }

      return var2;
   }

   public Expression inline(Environment var1, Context var2) {
      Object var3 = null;

      for(int var4 = 0; var4 < this.args.length; ++var4) {
         if (this.args[var4] != null) {
            var3 = var3 != null ? new CommaExpression(this.where, (Expression)var3, this.args[var4]) : this.args[var4];
         }
      }

      if (this.init != null) {
         var3 = var3 != null ? new CommaExpression(this.where, (Expression)var3, this.init) : this.init;
      }

      return var3 != null ? ((Expression)var3).inline(var1, var2) : null;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.init != null) {
         return this.init.inlineValue(var1, var2);
      } else {
         for(int var3 = 0; var3 < this.args.length; ++var3) {
            if (this.args[var3] != null) {
               this.args[var3] = this.args[var3].inlineValue(var1, var2);
            }
         }

         return this;
      }
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < this.args.length; ++var5) {
         if (this.args[var5] != null) {
            this.args[var5].codeValue(var1, var2, var3);
            ++var4;
         }
      }

      if (this.args.length > 1) {
         var3.add(this.where, 197, new ArrayData(this.type, var4));
      } else {
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

      }
   }
}
