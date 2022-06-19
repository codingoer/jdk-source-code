package sun.tools.java;

public final class ClassType extends Type {
   Identifier className;

   ClassType(String var1, Identifier var2) {
      super(10, var1);
      this.className = var2;
   }

   public Identifier getClassName() {
      return this.className;
   }

   public String typeString(String var1, boolean var2, boolean var3) {
      String var4 = (var2 ? this.getClassName().getFlatName() : Identifier.lookup(this.getClassName().getQualifier(), this.getClassName().getFlatName())).toString();
      return var1.length() > 0 ? var4 + " " + var1 : var4;
   }
}
