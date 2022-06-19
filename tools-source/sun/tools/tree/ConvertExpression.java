package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ConvertExpression extends UnaryExpression {
   public ConvertExpression(long var1, Type var3, Expression var4) {
      super(55, var1, var3, var4);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.right.checkValue(var1, var2, var3, var4);
   }

   Expression simplify() {
      switch (this.right.op) {
         case 62:
         case 63:
         case 64:
         case 65:
            int var5 = ((IntegerExpression)this.right).value;
            switch (this.type.getTypeCode()) {
               case 1:
                  return new ByteExpression(this.right.where, (byte)var5);
               case 2:
                  return new CharExpression(this.right.where, (char)var5);
               case 3:
                  return new ShortExpression(this.right.where, (short)var5);
               case 4:
                  return new IntExpression(this.right.where, var5);
               case 5:
                  return new LongExpression(this.right.where, (long)var5);
               case 6:
                  return new FloatExpression(this.right.where, (float)var5);
               case 7:
                  return new DoubleExpression(this.right.where, (double)var5);
               default:
                  return this;
            }
         case 66:
            long var4 = ((LongExpression)this.right).value;
            switch (this.type.getTypeCode()) {
               case 1:
                  return new ByteExpression(this.right.where, (byte)((int)var4));
               case 2:
                  return new CharExpression(this.right.where, (char)((int)var4));
               case 3:
                  return new ShortExpression(this.right.where, (short)((int)var4));
               case 4:
                  return new IntExpression(this.right.where, (int)var4);
               case 5:
               default:
                  return this;
               case 6:
                  return new FloatExpression(this.right.where, (float)var4);
               case 7:
                  return new DoubleExpression(this.right.where, (double)var4);
            }
         case 67:
            float var3 = ((FloatExpression)this.right).value;
            switch (this.type.getTypeCode()) {
               case 1:
                  return new ByteExpression(this.right.where, (byte)((int)var3));
               case 2:
                  return new CharExpression(this.right.where, (char)((int)var3));
               case 3:
                  return new ShortExpression(this.right.where, (short)((int)var3));
               case 4:
                  return new IntExpression(this.right.where, (int)var3);
               case 5:
                  return new LongExpression(this.right.where, (long)var3);
               case 6:
               default:
                  return this;
               case 7:
                  return new DoubleExpression(this.right.where, (double)var3);
            }
         case 68:
            double var1 = ((DoubleExpression)this.right).value;
            switch (this.type.getTypeCode()) {
               case 1:
                  return new ByteExpression(this.right.where, (byte)((int)var1));
               case 2:
                  return new CharExpression(this.right.where, (char)((int)var1));
               case 3:
                  return new ShortExpression(this.right.where, (short)((int)var1));
               case 4:
                  return new IntExpression(this.right.where, (int)var1);
               case 5:
                  return new LongExpression(this.right.where, (long)var1);
               case 6:
                  return new FloatExpression(this.right.where, (float)var1);
            }
      }

      return this;
   }

   public boolean equals(int var1) {
      return this.right.equals(var1);
   }

   public boolean equals(boolean var1) {
      return this.right.equals(var1);
   }

   public Expression inline(Environment var1, Context var2) {
      if (this.right.type.inMask(1792) && this.type.inMask(1792)) {
         try {
            if (!var1.implicitCast(this.right.type, this.type)) {
               return this.inlineValue(var1, var2);
            }
         } catch (ClassNotFound var4) {
            throw new CompilerError(var4);
         }
      }

      return super.inline(var1, var2);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.right.codeValue(var1, var2, var3);
      this.codeConversion(var1, var2, var3, this.right.type, this.type);
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " " + this.type.toString() + " ");
      this.right.print(var1);
      var1.print(")");
   }
}
