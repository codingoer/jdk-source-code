package com.sun.codemodel.internal;

public class JCatchBlock implements JGenerable {
   JClass exception;
   private JVar var = null;
   private JBlock body = new JBlock();

   JCatchBlock(JClass exception) {
      this.exception = exception;
   }

   public JVar param(String name) {
      if (this.var != null) {
         throw new IllegalStateException();
      } else {
         this.var = new JVar(JMods.forVar(0), this.exception, name, (JExpression)null);
         return this.var;
      }
   }

   public JBlock body() {
      return this.body;
   }

   public void generate(JFormatter f) {
      if (this.var == null) {
         this.var = new JVar(JMods.forVar(0), this.exception, "_x", (JExpression)null);
      }

      f.p("catch (").b(this.var).p(')').g((JGenerable)this.body);
   }
}
