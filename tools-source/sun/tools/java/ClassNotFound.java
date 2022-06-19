package sun.tools.java;

public class ClassNotFound extends Exception {
   public Identifier name;

   public ClassNotFound(Identifier var1) {
      super(var1.toString());
      this.name = var1;
   }
}
