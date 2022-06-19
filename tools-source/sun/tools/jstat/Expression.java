package sun.tools.jstat;

public class Expression {
   private static int nextOrdinal;
   private boolean debug = Boolean.getBoolean("Expression.debug");
   private Expression left;
   private Expression right;
   private Operator operator;
   private int ordinal;

   Expression() {
      this.ordinal = nextOrdinal++;
      if (this.debug) {
         System.out.println("Expression " + this.ordinal + " created");
      }

   }

   void setLeft(Expression var1) {
      if (this.debug) {
         System.out.println("Setting left on " + this.ordinal + " to " + var1);
      }

      this.left = var1;
   }

   Expression getLeft() {
      return this.left;
   }

   void setRight(Expression var1) {
      if (this.debug) {
         System.out.println("Setting right on " + this.ordinal + " to " + var1);
      }

      this.right = var1;
   }

   Expression getRight() {
      return this.right;
   }

   void setOperator(Operator var1) {
      if (this.debug) {
         System.out.println("Setting operator on " + this.ordinal + " to " + var1);
      }

      this.operator = var1;
   }

   Operator getOperator() {
      return this.operator;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('(');
      if (this.left != null) {
         var1.append(this.left.toString());
      }

      if (this.operator != null) {
         var1.append(this.operator.toString());
         if (this.right != null) {
            var1.append(this.right.toString());
         }
      }

      var1.append(')');
      return var1.toString();
   }
}
