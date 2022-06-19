package sun.tools.java;

public class AmbiguousMember extends Exception {
   public MemberDefinition field1;
   public MemberDefinition field2;

   public AmbiguousMember(MemberDefinition var1, MemberDefinition var2) {
      super(var1.getName() + " + " + var2.getName());
      this.field1 = var1;
      this.field2 = var2;
   }
}
