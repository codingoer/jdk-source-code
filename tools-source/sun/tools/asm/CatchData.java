package sun.tools.asm;

public final class CatchData {
   Object type;
   Label label;

   CatchData(Object var1) {
      this.type = var1;
      this.label = new Label();
   }

   public Label getLabel() {
      return this.label;
   }

   public Object getType() {
      return this.type;
   }
}
