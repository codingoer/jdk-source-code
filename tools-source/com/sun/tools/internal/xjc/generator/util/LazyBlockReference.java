package com.sun.tools.internal.xjc.generator.util;

import com.sun.codemodel.internal.JBlock;

public abstract class LazyBlockReference implements BlockReference {
   private JBlock block = null;

   protected abstract JBlock create();

   public JBlock get(boolean create) {
      if (!create) {
         return this.block;
      } else {
         if (this.block == null) {
            this.block = this.create();
         }

         return this.block;
      }
   }
}
