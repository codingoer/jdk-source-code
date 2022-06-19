package com.sun.tools.example.debug.tty;

abstract class WatchpointSpec extends EventRequestSpec {
   final String fieldId;

   WatchpointSpec(ReferenceTypeSpec var1, String var2) throws MalformedMemberNameException {
      super(var1);
      this.fieldId = var2;
      if (!this.isJavaIdentifier(var2)) {
         throw new MalformedMemberNameException(var2);
      }
   }

   public int hashCode() {
      return this.refSpec.hashCode() + this.fieldId.hashCode() + this.getClass().hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof WatchpointSpec)) {
         return false;
      } else {
         WatchpointSpec var2 = (WatchpointSpec)var1;
         return this.fieldId.equals(var2.fieldId) && this.refSpec.equals(var2.refSpec) && this.getClass().equals(var2.getClass());
      }
   }

   String errorMessageFor(Exception var1) {
      return var1 instanceof NoSuchFieldException ? MessageOutput.format("No field in", new Object[]{this.fieldId, this.refSpec.toString()}) : super.errorMessageFor(var1);
   }
}
