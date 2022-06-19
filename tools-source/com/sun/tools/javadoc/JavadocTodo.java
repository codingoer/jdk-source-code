package com.sun.tools.javadoc;

import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.util.Context;

public class JavadocTodo extends Todo {
   public static void preRegister(Context var0) {
      var0.put(todoKey, new Context.Factory() {
         public Todo make(Context var1) {
            return new JavadocTodo(var1);
         }
      });
   }

   protected JavadocTodo(Context var1) {
      super(var1);
   }

   public void append(Env var1) {
   }

   public boolean offer(Env var1) {
      return false;
   }
}
