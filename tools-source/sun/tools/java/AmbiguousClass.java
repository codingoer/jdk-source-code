package sun.tools.java;

public class AmbiguousClass extends ClassNotFound {
   public Identifier name1;
   public Identifier name2;

   public AmbiguousClass(Identifier var1, Identifier var2) {
      super(var1.getName());
      this.name1 = var1;
      this.name2 = var2;
   }
}
