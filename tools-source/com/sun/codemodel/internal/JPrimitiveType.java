package com.sun.codemodel.internal;

public final class JPrimitiveType extends JType {
   private final String typeName;
   private final JCodeModel owner;
   private final JClass wrapperClass;
   private JClass arrayClass;

   JPrimitiveType(JCodeModel owner, String typeName, Class wrapper) {
      this.owner = owner;
      this.typeName = typeName;
      this.wrapperClass = owner.ref(wrapper);
   }

   public JCodeModel owner() {
      return this.owner;
   }

   public String fullName() {
      return this.typeName;
   }

   public String name() {
      return this.fullName();
   }

   public boolean isPrimitive() {
      return true;
   }

   public JClass array() {
      if (this.arrayClass == null) {
         this.arrayClass = new JArrayClass(this.owner, this);
      }

      return this.arrayClass;
   }

   public JClass boxify() {
      return this.wrapperClass;
   }

   /** @deprecated */
   public JType unboxify() {
      return this;
   }

   /** @deprecated */
   public JClass getWrapperClass() {
      return this.boxify();
   }

   public JExpression wrap(JExpression exp) {
      return JExpr._new(this.boxify()).arg(exp);
   }

   public JExpression unwrap(JExpression exp) {
      return exp.invoke(this.typeName + "Value");
   }

   public void generate(JFormatter f) {
      f.p(this.typeName);
   }
}
