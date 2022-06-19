package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ArrayAccessExpression extends UnaryExpression {
   Expression index;

   public ArrayAccessExpression(long var1, Expression var3, Expression var4) {
      super(48, var1, Type.tError, var3);
      this.index = var4;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.right.checkValue(var1, var2, var3, var4);
      if (this.index == null) {
         var1.error(this.where, "array.index.required");
         return var3;
      } else {
         var3 = this.index.checkValue(var1, var2, var3, var4);
         this.index = this.convert(var1, var2, Type.tInt, this.index);
         if (!this.right.type.isType(9)) {
            if (!this.right.type.isType(13)) {
               var1.error(this.where, "not.array", this.right.type);
            }

            return var3;
         } else {
            this.type = this.right.type.getElementType();
            return var3;
         }
      }
   }

   public Vset checkAmbigName(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      if (this.index == null) {
         var3 = this.right.checkAmbigName(var1, var2, var3, var4, this);
         if (this.right.type == Type.tPackage) {
            FieldExpression.reportFailedPackagePrefix(var1, this.right);
            return var3;
         } else if (this.right instanceof TypeExpression) {
            Type var6 = Type.tArray(this.right.type);
            var5.right = new TypeExpression(this.where, var6);
            return var3;
         } else {
            var1.error(this.where, "array.index.required");
            return var3;
         }
      } else {
         return super.checkAmbigName(var1, var2, var3, var4, var5);
      }
   }

   public Vset checkLHS(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.checkValue(var1, var2, var3, var4);
   }

   public Vset checkAssignOp(Environment var1, Context var2, Vset var3, Hashtable var4, Expression var5) {
      return this.checkValue(var1, var2, var3, var4);
   }

   public FieldUpdater getAssigner(Environment var1, Context var2) {
      return null;
   }

   public FieldUpdater getUpdater(Environment var1, Context var2) {
      return null;
   }

   Type toType(Environment var1, Context var2) {
      return this.toType(var1, this.right.toType(var1, var2));
   }

   Type toType(Environment var1, Type var2) {
      if (this.index != null) {
         var1.error(this.index.where, "array.dim.in.type");
      }

      return Type.tArray(var2);
   }

   public Expression inline(Environment var1, Context var2) {
      this.right = this.right.inlineValue(var1, var2);
      this.index = this.index.inlineValue(var1, var2);
      return this;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.right = this.right.inlineValue(var1, var2);
      this.index = this.index.inlineValue(var1, var2);
      return this;
   }

   public Expression inlineLHS(Environment var1, Context var2) {
      return this.inlineValue(var1, var2);
   }

   public Expression copyInline(Context var1) {
      ArrayAccessExpression var2 = (ArrayAccessExpression)this.clone();
      var2.right = this.right.copyInline(var1);
      if (this.index == null) {
         var2.index = null;
      } else {
         var2.index = this.index.copyInline(var1);
      }

      return var2;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + this.right.costInline(var1, var2, var3) + this.index.costInline(var1, var2, var3);
   }

   int codeLValue(Environment var1, Context var2, Assembler var3) {
      this.right.codeValue(var1, var2, var3);
      this.index.codeValue(var1, var2, var3);
      return 2;
   }

   void codeLoad(Environment var1, Context var2, Assembler var3) {
      switch (this.type.getTypeCode()) {
         case 0:
         case 1:
            var3.add(this.where, 51);
            break;
         case 2:
            var3.add(this.where, 52);
            break;
         case 3:
            var3.add(this.where, 53);
            break;
         default:
            var3.add(this.where, 46 + this.type.getTypeCodeOffset());
      }

   }

   void codeStore(Environment var1, Context var2, Assembler var3) {
      switch (this.type.getTypeCode()) {
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
            var3.add(this.where, 79 + this.type.getTypeCodeOffset());
      }

   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeLValue(var1, var2, var3);
      this.codeLoad(var1, var2, var3);
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      this.right.print(var1);
      var1.print(" ");
      if (this.index != null) {
         this.index.print(var1);
      } else {
         var1.print("<empty>");
      }

      var1.print(")");
   }
}
