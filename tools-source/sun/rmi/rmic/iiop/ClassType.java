package sun.rmi.rmic.iiop;

import java.io.IOException;
import sun.rmi.rmic.IndentingWriter;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;

public abstract class ClassType extends CompoundType {
   private ClassType parent;

   public ClassType getSuperclass() {
      return this.parent;
   }

   public void print(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      if (this.isInner()) {
         var1.p("// " + this.getTypeDescription() + " (INNER)");
      } else {
         var1.p("// " + this.getTypeDescription());
      }

      var1.pln(" (" + this.getRepositoryID() + ")\n");
      this.printPackageOpen(var1, var3);
      if (!var3) {
         var1.p("public ");
      }

      String var5 = "";
      var1.p("class " + this.getTypeName(false, var3, false));
      if (this.printExtends(var1, var2, var3, var4)) {
         var5 = ",";
      }

      this.printImplements(var1, var5, var2, var3, var4);
      var1.plnI(" {");
      this.printMembers(var1, var2, var3, var4);
      var1.pln();
      this.printMethods(var1, var2, var3, var4);
      if (var3) {
         var1.pOln("};");
      } else {
         var1.pOln("}");
      }

      this.printPackageClose(var1, var3);
   }

   protected void destroy() {
      if (!this.destroyed) {
         super.destroy();
         if (this.parent != null) {
            this.parent.destroy();
            this.parent = null;
         }
      }

   }

   protected ClassType(ContextStack var1, int var2, ClassDefinition var3) {
      super(var1, var2, var3);
      if ((var2 & 67108864) == 0 && var3.isInterface()) {
         throw new CompilerError("Not a class");
      } else {
         this.parent = null;
      }
   }

   protected ClassType(int var1, ClassDefinition var2, ContextStack var3) {
      super(var3, var2, var1);
      if ((var1 & 67108864) == 0 && var2.isInterface()) {
         throw new CompilerError("Not a class");
      } else {
         this.parent = null;
      }
   }

   protected ClassType(ContextStack var1, ClassDefinition var2, int var3) {
      super(var1, var2, var3);
      if ((var3 & 67108864) == 0 && var2.isInterface()) {
         throw new CompilerError("Not a class");
      } else {
         this.parent = null;
      }
   }

   protected void swapInvalidTypes() {
      super.swapInvalidTypes();
      if (this.parent != null && this.parent.getStatus() != 1) {
         this.parent = (ClassType)this.getValidType(this.parent);
      }

   }

   public String addExceptionDescription(String var1) {
      if (this.isException) {
         if (this.isCheckedException) {
            var1 = var1 + " - Checked Exception";
         } else {
            var1 = var1 + " - Unchecked Exception";
         }
      }

      return var1;
   }

   protected boolean initParents(ContextStack var1) {
      var1.setNewContextCode(11);
      BatchEnvironment var2 = var1.getEnv();
      boolean var3 = true;

      try {
         ClassDeclaration var4 = this.getClassDefinition().getSuperClass(var2);
         if (var4 != null) {
            ClassDefinition var5 = var4.getClassDefinition(var2);
            this.parent = (ClassType)makeType(var5.getType(), var5, var1);
            if (this.parent == null) {
               var3 = false;
            }
         }

         return var3;
      } catch (ClassNotFound var6) {
         classNotFound(var1, var6);
         throw new CompilerError("ClassType constructor");
      }
   }
}
