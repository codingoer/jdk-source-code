package sun.tools.asm;

import java.util.Vector;

public final class TryData {
   Vector catches = new Vector();
   Label endLabel = new Label();

   public CatchData add(Object var1) {
      CatchData var2 = new CatchData(var1);
      this.catches.addElement(var2);
      return var2;
   }

   public CatchData getCatch(int var1) {
      return (CatchData)this.catches.elementAt(var1);
   }

   public Label getEndLabel() {
      return this.endLabel;
   }
}
