package sun.tools.tree;

import sun.tools.java.Environment;
import sun.tools.java.Identifier;

final class ContextEnvironment extends Environment {
   Context ctx;
   Environment innerEnv;

   ContextEnvironment(Environment var1, Context var2) {
      super(var1, var1.getSource());
      this.ctx = var2;
      this.innerEnv = var1;
   }

   public Identifier resolveName(Identifier var1) {
      return this.ctx.resolveName(this.innerEnv, var1);
   }
}
