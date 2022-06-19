package com.sun.tools.hat.internal.model;

public class JavaStatic {
   private JavaField field;
   private JavaThing value;

   public JavaStatic(JavaField var1, JavaThing var2) {
      this.field = var1;
      this.value = var2;
   }

   public void resolve(JavaClass var1, Snapshot var2) {
      long var3 = -1L;
      if (this.value instanceof JavaObjectRef) {
         var3 = ((JavaObjectRef)this.value).getId();
      }

      this.value = this.value.dereference(var2, this.field);
      if (this.value.isHeapAllocated() && var1.getLoader() == var2.getNullThing()) {
         JavaHeapObject var5 = (JavaHeapObject)this.value;
         String var6 = "Static reference from " + var1.getName() + "." + this.field.getName();
         var2.addRoot(new Root(var3, var1.getId(), 9, var6));
      }

   }

   public JavaField getField() {
      return this.field;
   }

   public JavaThing getValue() {
      return this.value;
   }
}
