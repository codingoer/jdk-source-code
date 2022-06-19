package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.List;

abstract class JGenerifiableImpl implements JGenerifiable, JDeclaration {
   private List typeVariables = null;

   protected abstract JCodeModel owner();

   public void declare(JFormatter f) {
      if (this.typeVariables != null) {
         f.p('<');

         for(int i = 0; i < this.typeVariables.size(); ++i) {
            if (i != 0) {
               f.p(',');
            }

            f.d((JDeclaration)this.typeVariables.get(i));
         }

         f.p('>');
      }

   }

   public JTypeVar generify(String name) {
      JTypeVar v = new JTypeVar(this.owner(), name);
      if (this.typeVariables == null) {
         this.typeVariables = new ArrayList(3);
      }

      this.typeVariables.add(v);
      return v;
   }

   public JTypeVar generify(String name, Class bound) {
      return this.generify(name, this.owner().ref(bound));
   }

   public JTypeVar generify(String name, JClass bound) {
      return this.generify(name).bound(bound);
   }

   public JTypeVar[] typeParams() {
      return this.typeVariables == null ? JTypeVar.EMPTY_ARRAY : (JTypeVar[])this.typeVariables.toArray(new JTypeVar[this.typeVariables.size()]);
   }
}
