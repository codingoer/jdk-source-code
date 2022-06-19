package com.sun.tools.javac.comp;

import com.sun.tools.javac.util.Context;
import java.util.HashMap;

public class CompileStates extends HashMap {
   protected static final Context.Key compileStatesKey = new Context.Key();
   private static final long serialVersionUID = 1812267524140424433L;
   protected Context context;

   public static CompileStates instance(Context var0) {
      CompileStates var1 = (CompileStates)var0.get(compileStatesKey);
      if (var1 == null) {
         var1 = new CompileStates(var0);
      }

      return var1;
   }

   public CompileStates(Context var1) {
      this.context = var1;
      var1.put((Context.Key)compileStatesKey, (Object)this);
   }

   public boolean isDone(Env var1, CompileState var2) {
      CompileState var3 = (CompileState)this.get(var1);
      return var3 != null && !var2.isAfter(var3);
   }

   public static enum CompileState {
      INIT(0),
      PARSE(1),
      ENTER(2),
      PROCESS(3),
      ATTR(4),
      FLOW(5),
      TRANSTYPES(6),
      UNLAMBDA(7),
      LOWER(8),
      GENERATE(9);

      private final int value;

      private CompileState(int var3) {
         this.value = var3;
      }

      public boolean isAfter(CompileState var1) {
         return this.value > var1.value;
      }

      public static CompileState max(CompileState var0, CompileState var1) {
         return var0.value > var1.value ? var0 : var1;
      }
   }
}
