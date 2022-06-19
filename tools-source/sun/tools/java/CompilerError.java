package sun.tools.java;

public class CompilerError extends Error {
   Throwable e;

   public CompilerError(String var1) {
      super(var1);
      this.e = this;
   }

   public CompilerError(Exception var1) {
      super(var1.getMessage());
      this.e = var1;
   }

   public void printStackTrace() {
      if (this.e == this) {
         super.printStackTrace();
      } else {
         this.e.printStackTrace();
      }

   }
}
