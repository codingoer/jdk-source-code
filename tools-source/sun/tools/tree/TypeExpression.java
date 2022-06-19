package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class TypeExpression extends Expression {
   public TypeExpression(long var1, Type var3) {
      super(147, var1, var3);
   }

   Type toType(Environment var1, Context var2) {
      return this.type;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var1.error(this.where, "invalid.term");
      this.type = Type.tError;
      return var3;
   }

   public Vset checkAmbigName(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      return var3;
   }

   public Expression inline(Environment var1, Context var2) {
      return null;
   }

   public void print(PrintStream var1) {
      var1.print(this.type.toString());
   }
}
