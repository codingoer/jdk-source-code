package sun.tools.jstat;

public class Literal extends Expression {
   private Object value;

   public Literal(Object var1) {
      this.value = var1;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object var1) {
      this.value = var1;
   }

   public String toString() {
      return this.value.toString();
   }
}
