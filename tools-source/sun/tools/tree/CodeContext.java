package sun.tools.tree;

import sun.tools.asm.Label;

class CodeContext extends Context {
   Label breakLabel;
   Label contLabel;

   CodeContext(Context var1, Node var2) {
      super(var1, var2);
      switch (var2.op) {
         case 92:
         case 93:
         case 94:
         case 103:
         case 126:
            this.breakLabel = new Label();
            this.contLabel = new Label();
            break;
         case 95:
         case 101:
         case 150:
         case 151:
            this.breakLabel = new Label();
            break;
         default:
            if (var2 instanceof Statement && ((Statement)var2).labels != null) {
               this.breakLabel = new Label();
            }
      }

   }
}
