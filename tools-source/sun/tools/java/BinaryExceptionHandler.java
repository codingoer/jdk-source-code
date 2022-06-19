package sun.tools.java;

public class BinaryExceptionHandler {
   public int startPC;
   public int endPC;
   public int handlerPC;
   public ClassDeclaration exceptionClass;

   BinaryExceptionHandler(int var1, int var2, int var3, ClassDeclaration var4) {
      this.startPC = var1;
      this.endPC = var2;
      this.handlerPC = var3;
      this.exceptionClass = var4;
   }
}
