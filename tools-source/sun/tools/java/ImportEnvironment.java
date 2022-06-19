package sun.tools.java;

final class ImportEnvironment extends Environment {
   Imports imports;

   ImportEnvironment(Environment var1, Imports var2) {
      super(var1, var1.getSource());
      this.imports = var2;
   }

   public Identifier resolve(Identifier var1) throws ClassNotFound {
      return this.imports.resolve(this, var1);
   }

   public Imports getImports() {
      return this.imports;
   }
}
