package sun.rmi.rmic.iiop;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Identifier;

public class SpecialClassType extends ClassType {
   public static SpecialClassType forSpecial(ClassDefinition var0, ContextStack var1) {
      if (var1.anyErrors()) {
         return null;
      } else {
         sun.tools.java.Type var2 = var0.getType();
         String var3 = var2.toString() + var1.getContextCodeString();
         Type var4 = getType(var3, var1);
         if (var4 != null) {
            return !(var4 instanceof SpecialClassType) ? null : (SpecialClassType)var4;
         } else {
            int var5 = getTypeCode(var2, var0, var1);
            if (var5 != 0) {
               SpecialClassType var6 = new SpecialClassType(var1, var5, var0);
               putType(var3, var6, var1);
               var1.push(var6);
               var1.pop(true);
               return var6;
            } else {
               return null;
            }
         }
      }
   }

   public String getTypeDescription() {
      return "Special class";
   }

   private SpecialClassType(ContextStack var1, int var2, ClassDefinition var3) {
      super(var1, var2 | 268435456 | 67108864 | 33554432, var3);
      Identifier var4 = var3.getName();
      String var5 = null;
      String[] var6 = null;
      boolean var7 = var1.size() > 0 && var1.getContext().isConstant();
      switch (var2) {
         case 512:
            var5 = IDLNames.getTypeName(var2, var7);
            if (!var7) {
               var6 = IDL_CORBA_MODULE;
            }
            break;
         case 1024:
            var5 = "_Object";
            var6 = IDL_JAVA_LANG_MODULE;
      }

      this.setNames(var4, var6, var5);
      if (!this.initParents(var1)) {
         throw new CompilerError("SpecialClassType found invalid parent.");
      } else {
         this.initialize((Vector)null, (Vector)null, (Vector)null, var1, false);
      }
   }

   private static int getTypeCode(sun.tools.java.Type var0, ClassDefinition var1, ContextStack var2) {
      if (var0.isType(10)) {
         Identifier var3 = var0.getClassName();
         if (var3 == idJavaLangString) {
            return 512;
         }

         if (var3 == idJavaLangObject) {
            return 1024;
         }
      }

      return 0;
   }
}
