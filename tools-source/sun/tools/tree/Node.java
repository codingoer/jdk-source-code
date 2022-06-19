package sun.tools.tree;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class Node implements Constants, Cloneable {
   int op;
   long where;

   Node(int var1, long var2) {
      this.op = var1;
      this.where = var2;
   }

   public int getOp() {
      return this.op;
   }

   public long getWhere() {
      return this.where;
   }

   public Expression convert(Environment var1, Context var2, Type var3, Expression var4) {
      if (!var4.type.isType(13) && !var3.isType(13)) {
         if (var4.type.equals(var3)) {
            return var4;
         } else {
            try {
               if (var4.fitsType(var1, var2, var3)) {
                  return new ConvertExpression(this.where, var3, var4);
               }

               if (var1.explicitCast(var4.type, var3)) {
                  var1.error(this.where, "explicit.cast.needed", opNames[this.op], var4.type, var3);
                  return new ConvertExpression(this.where, var3, var4);
               }
            } catch (ClassNotFound var6) {
               var1.error(this.where, "class.not.found", var6.name, opNames[this.op]);
            }

            var1.error(this.where, "incompatible.type", opNames[this.op], var4.type, var3);
            return new ConvertExpression(this.where, Type.tError, var4);
         }
      } else {
         return var4;
      }
   }

   public void print(PrintStream var1) {
      throw new CompilerError("print");
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw (InternalError)(new InternalError()).initCause(var2);
      }
   }

   public String toString() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      this.print(new PrintStream(var1));
      return var1.toString();
   }
}
