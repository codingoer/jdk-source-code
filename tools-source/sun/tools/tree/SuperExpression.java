package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class SuperExpression extends ThisExpression {
   public SuperExpression(long var1) {
      super(83, var1);
   }

   public SuperExpression(long var1, Expression var3) {
      super(var1, var3);
      this.op = 83;
   }

   public SuperExpression(long var1, Context var3) {
      super(var1, var3);
      this.op = 83;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.checkCommon(var1, var2, var3, var4);
      if (this.type != Type.tError) {
         var1.error(this.where, "undef.var.super", idSuper);
      }

      return var3;
   }

   public Vset checkAmbigName(Environment var1, Context var2, Vset var3, Hashtable var4, UnaryExpression var5) {
      return this.checkCommon(var1, var2, var3, var4);
   }

   private Vset checkCommon(Environment var1, Context var2, Vset var3, Hashtable var4) {
      ClassDeclaration var5 = var2.field.getClassDefinition().getSuperClass();
      if (var5 == null) {
         var1.error(this.where, "undef.var", idSuper);
         this.type = Type.tError;
         return var3;
      } else {
         var3 = super.checkValue(var1, var2, var3, var4);
         this.type = var5.getType();
         return var3;
      }
   }
}
