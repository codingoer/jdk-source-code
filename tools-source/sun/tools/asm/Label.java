package sun.tools.asm;

import sun.tools.java.MemberDefinition;

public final class Label extends Instruction {
   static int labelCount = 0;
   int ID;
   int depth;
   MemberDefinition[] locals;

   public Label() {
      super(0L, -1, (Object)null);
      this.ID = ++labelCount;
   }

   Label getDestination() {
      Label var1 = this;
      if (this.next != null && this.next != this && this.depth == 0) {
         this.depth = 1;
         switch (this.next.opc) {
            case -1:
               var1 = ((Label)this.next).getDestination();
               break;
            case 18:
            case 19:
               if (this.next.value instanceof Integer) {
                  Instruction var2 = this.next.next;
                  if (var2.opc == -1) {
                     var2 = ((Label)var2).getDestination().next;
                  }

                  if (var2.opc == 153) {
                     if ((Integer)this.next.value == 0) {
                        var1 = (Label)var2.value;
                     } else {
                        var1 = new Label();
                        var1.next = var2.next;
                        var2.next = var1;
                     }

                     var1 = var1.getDestination();
                  } else if (var2.opc == 154) {
                     if ((Integer)this.next.value == 0) {
                        var1 = new Label();
                        var1.next = var2.next;
                        var2.next = var1;
                     } else {
                        var1 = (Label)var2.value;
                     }

                     var1 = var1.getDestination();
                  }
               }
               break;
            case 167:
               var1 = ((Label)this.next.value).getDestination();
         }

         this.depth = 0;
      }

      return var1;
   }

   public String toString() {
      String var1 = "$" + this.ID + ":";
      if (this.value != null) {
         var1 = var1 + " stack=" + this.value;
      }

      return var1;
   }
}
