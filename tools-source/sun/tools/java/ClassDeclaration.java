package sun.tools.java;

public final class ClassDeclaration implements Constants {
   int status;
   Type type;
   ClassDefinition definition;
   private boolean found = false;

   public ClassDeclaration(Identifier var1) {
      this.type = Type.tClass(var1);
   }

   public int getStatus() {
      return this.status;
   }

   public Identifier getName() {
      return this.type.getClassName();
   }

   public Type getType() {
      return this.type;
   }

   public boolean isDefined() {
      switch (this.status) {
         case 2:
         case 4:
         case 5:
         case 6:
            return true;
         case 3:
         default:
            return false;
      }
   }

   public ClassDefinition getClassDefinition() {
      return this.definition;
   }

   public ClassDefinition getClassDefinition(Environment var1) throws ClassNotFound {
      var1.dtEvent("getClassDefinition: " + this.getName() + ", status " + this.getStatus());
      if (this.found) {
         return this.definition;
      } else {
         while(true) {
            switch (this.status) {
               case 0:
               case 1:
               case 3:
                  var1.loadDefinition(this);
                  break;
               case 2:
               case 4:
                  if (!this.definition.isInsideLocal()) {
                     this.definition.basicCheck(var1);
                  }

                  this.found = true;
                  return this.definition;
               case 5:
               case 6:
                  this.found = true;
                  return this.definition;
               default:
                  throw new ClassNotFound(this.getName());
            }
         }
      }
   }

   public ClassDefinition getClassDefinitionNoCheck(Environment var1) throws ClassNotFound {
      var1.dtEvent("getClassDefinition: " + this.getName() + ", status " + this.getStatus());

      while(true) {
         switch (this.status) {
            case 0:
            case 1:
            case 3:
               var1.loadDefinition(this);
               break;
            case 2:
            case 4:
            case 5:
            case 6:
               return this.definition;
            default:
               throw new ClassNotFound(this.getName());
         }
      }
   }

   public void setDefinition(ClassDefinition var1, int var2) {
      if (var1 != null && !this.getName().equals(var1.getName())) {
         throw new CompilerError("setDefinition: name mismatch: " + this + ", " + var1);
      } else {
         this.definition = var1;
         this.status = var2;
      }
   }

   public boolean equals(Object var1) {
      return var1 instanceof ClassDeclaration ? this.type.equals(((ClassDeclaration)var1).type) : false;
   }

   public int hashCode() {
      return this.type.hashCode();
   }

   public String toString() {
      String var1 = this.getName().toString();
      String var2 = "type ";
      String var3 = this.getName().isInner() ? "nested " : "";
      if (this.getClassDefinition() != null) {
         if (this.getClassDefinition().isInterface()) {
            var2 = "interface ";
         } else {
            var2 = "class ";
         }

         if (!this.getClassDefinition().isTopLevel()) {
            var3 = "inner ";
            if (this.getClassDefinition().isLocal()) {
               var3 = "local ";
               if (!this.getClassDefinition().isAnonymous()) {
                  var1 = this.getClassDefinition().getLocalName() + " (" + var1 + ")";
               }
            }
         }
      }

      return var3 + var2 + var1;
   }
}
