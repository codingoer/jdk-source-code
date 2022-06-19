package sun.rmi.rmic.iiop;

class TypeContext {
   private int code = 0;
   private ContextElement element = null;
   private boolean isValue = false;

   public void set(int var1, ContextElement var2) {
      this.code = var1;
      this.element = var2;
      if (var2 instanceof ValueType) {
         this.isValue = true;
      } else {
         this.isValue = false;
      }

   }

   public int getCode() {
      return this.code;
   }

   public String getName() {
      return this.element.getElementName();
   }

   public Type getCandidateType() {
      return this.element instanceof Type ? (Type)this.element : null;
   }

   public String getTypeDescription() {
      return this.element instanceof Type ? ((Type)this.element).getTypeDescription() : "[unknown type]";
   }

   public String toString() {
      return this.element != null ? ContextStack.getContextCodeString(this.code) + this.element.getElementName() : ContextStack.getContextCodeString(this.code) + "null";
   }

   public boolean isValue() {
      return this.isValue;
   }

   public boolean isConstant() {
      return this.code == 7;
   }

   public void destroy() {
      if (this.element instanceof Type) {
         ((Type)this.element).destroy();
      }

      this.element = null;
   }
}
