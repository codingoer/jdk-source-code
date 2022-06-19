package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JTryBlock implements JStatement {
   private JBlock body = new JBlock();
   private List catches = new ArrayList();
   private JBlock _finally = null;

   JTryBlock() {
   }

   public JBlock body() {
      return this.body;
   }

   public JCatchBlock _catch(JClass exception) {
      JCatchBlock cb = new JCatchBlock(exception);
      this.catches.add(cb);
      return cb;
   }

   public JBlock _finally() {
      if (this._finally == null) {
         this._finally = new JBlock();
      }

      return this._finally;
   }

   public void state(JFormatter f) {
      f.p("try").g((JGenerable)this.body);
      Iterator var2 = this.catches.iterator();

      while(var2.hasNext()) {
         JCatchBlock cb = (JCatchBlock)var2.next();
         f.g((JGenerable)cb);
      }

      if (this._finally != null) {
         f.p("finally").g((JGenerable)this._finally);
      }

      f.nl();
   }
}
