package sun.tools.jstat;

public class Identifier extends Expression {
   private String name;
   private Object value;

   public Identifier(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setValue(Object var1) {
      this.value = var1;
   }

   public Object getValue() {
      return this.value;
   }

   public boolean isResolved() {
      return this.value != null;
   }

   public String toString() {
      return this.name;
   }
}
