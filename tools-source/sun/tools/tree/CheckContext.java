package sun.tools.tree;

public class CheckContext extends Context {
   public Vset vsBreak;
   public Vset vsContinue;
   public Vset vsTryExit;

   CheckContext(Context var1, Statement var2) {
      super(var1, (Node)var2);
      this.vsBreak = Vset.DEAD_END;
      this.vsContinue = Vset.DEAD_END;
      this.vsTryExit = Vset.DEAD_END;
   }
}
