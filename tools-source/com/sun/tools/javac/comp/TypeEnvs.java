package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.Context;
import java.util.Collection;
import java.util.HashMap;

class TypeEnvs {
   private static final long serialVersionUID = 571524752489954631L;
   protected static final Context.Key typeEnvsKey = new Context.Key();
   private HashMap map = new HashMap();

   public static TypeEnvs instance(Context var0) {
      TypeEnvs var1 = (TypeEnvs)var0.get(typeEnvsKey);
      if (var1 == null) {
         var1 = new TypeEnvs(var0);
      }

      return var1;
   }

   protected TypeEnvs(Context var1) {
      var1.put((Context.Key)typeEnvsKey, (Object)this);
   }

   Env get(Symbol.TypeSymbol var1) {
      return (Env)this.map.get(var1);
   }

   Env put(Symbol.TypeSymbol var1, Env var2) {
      return (Env)this.map.put(var1, var2);
   }

   Env remove(Symbol.TypeSymbol var1) {
      return (Env)this.map.remove(var1);
   }

   Collection values() {
      return this.map.values();
   }

   void clear() {
      this.map.clear();
   }
}
