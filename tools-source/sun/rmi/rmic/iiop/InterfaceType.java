package sun.rmi.rmic.iiop;

import java.io.IOException;
import sun.rmi.rmic.IndentingWriter;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;

public abstract class InterfaceType extends CompoundType {
   public void print(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      if (this.isInner()) {
         var1.p("// " + this.getTypeDescription() + " (INNER)");
      } else {
         var1.p("// " + this.getTypeDescription() + "");
      }

      var1.pln(" (" + this.getRepositoryID() + ")\n");
      this.printPackageOpen(var1, var3);
      if (!var3) {
         var1.p("public ");
      }

      var1.p("interface " + this.getTypeName(false, var3, false));
      this.printImplements(var1, "", var2, var3, var4);
      var1.plnI(" {");
      this.printMembers(var1, var2, var3, var4);
      var1.pln();
      this.printMethods(var1, var2, var3, var4);
      var1.pln();
      if (var3) {
         var1.pOln("};");
      } else {
         var1.pOln("}");
      }

      this.printPackageClose(var1, var3);
   }

   protected InterfaceType(ContextStack var1, int var2, ClassDefinition var3) {
      super(var1, var2, var3);
      if ((var2 & 134217728) == 0 || !var3.isInterface()) {
         throw new CompilerError("Not an interface");
      }
   }

   protected InterfaceType(ContextStack var1, ClassDefinition var2, int var3) {
      super(var1, var2, var3);
      if ((var3 & 134217728) == 0 || !var2.isInterface()) {
         throw new CompilerError("Not an interface");
      }
   }
}
