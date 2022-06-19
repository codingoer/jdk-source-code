package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class JArray extends JExpressionImpl {
   private final JType type;
   private final JExpression size;
   private List exprs = null;

   public JArray add(JExpression e) {
      if (this.exprs == null) {
         this.exprs = new ArrayList();
      }

      this.exprs.add(e);
      return this;
   }

   JArray(JType type, JExpression size) {
      this.type = type;
      this.size = size;
   }

   public void generate(JFormatter f) {
      int arrayCount = 0;

      JType t;
      for(t = this.type; t.isArray(); ++arrayCount) {
         t = t.elementType();
      }

      f.p("new").g((JGenerable)t).p('[');
      if (this.size != null) {
         f.g((JGenerable)this.size);
      }

      f.p(']');

      for(int i = 0; i < arrayCount; ++i) {
         f.p("[]");
      }

      if (this.size == null || this.exprs != null) {
         f.p('{');
      }

      if (this.exprs != null) {
         f.g((Collection)this.exprs);
      } else {
         f.p(' ');
      }

      if (this.size == null || this.exprs != null) {
         f.p('}');
      }

   }
}
