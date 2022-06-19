package sun.rmi.rmic;

import sun.tools.java.Identifier;

public class Names {
   public static final Identifier stubFor(Identifier var0) {
      return Identifier.lookup(var0 + "_Stub");
   }

   public static final Identifier skeletonFor(Identifier var0) {
      return Identifier.lookup(var0 + "_Skel");
   }

   public static final Identifier mangleClass(Identifier var0) {
      if (!var0.isInner()) {
         return var0;
      } else {
         Identifier var1 = Identifier.lookup(var0.getFlatName().toString().replace('.', '$'));
         if (var1.isInner()) {
            throw new Error("failed to mangle inner class name");
         } else {
            return Identifier.lookup(var0.getQualifier(), var1);
         }
      }
   }
}
