package com.sun.tools.internal.xjc.model.nav;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;

final class NParameterizedType implements NClass {
   final NClass rawType;
   final NType[] args;

   NParameterizedType(NClass rawType, NType[] args) {
      this.rawType = rawType;
      this.args = args;

      assert args.length > 0;

   }

   public JClass toType(Outline o, Aspect aspect) {
      JClass r = this.rawType.toType(o, aspect);
      NType[] var4 = this.args;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         NType arg = var4[var6];
         r = r.narrow(arg.toType(o, aspect).boxify());
      }

      return r;
   }

   public boolean isAbstract() {
      return this.rawType.isAbstract();
   }

   public boolean isBoxedType() {
      return false;
   }

   public String fullName() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.rawType.fullName());
      buf.append('<');

      for(int i = 0; i < this.args.length; ++i) {
         if (i != 0) {
            buf.append(',');
         }

         buf.append(this.args[i].fullName());
      }

      buf.append('>');
      return buf.toString();
   }
}
