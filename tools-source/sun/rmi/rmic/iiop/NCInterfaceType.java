package sun.rmi.rmic.iiop;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;

public class NCInterfaceType extends InterfaceType {
   public static NCInterfaceType forNCInterface(ClassDefinition var0, ContextStack var1) {
      if (var1.anyErrors()) {
         return null;
      } else {
         boolean var2 = false;

         try {
            sun.tools.java.Type var3 = var0.getType();
            Type var4 = getType(var3, var1);
            if (var4 != null) {
               return !(var4 instanceof NCInterfaceType) ? null : (NCInterfaceType)var4;
            } else {
               NCInterfaceType var5 = new NCInterfaceType(var1, var0);
               putType(var3, var5, var1);
               var1.push(var5);
               var2 = true;
               if (var5.initialize(var1)) {
                  var1.pop(true);
                  return var5;
               } else {
                  removeType(var3, var1);
                  var1.pop(false);
                  return null;
               }
            }
         } catch (CompilerError var6) {
            if (var2) {
               var1.pop(false);
            }

            return null;
         }
      }
   }

   public String getTypeDescription() {
      return "Non-conforming interface";
   }

   private NCInterfaceType(ContextStack var1, ClassDefinition var2) {
      super(var1, var2, 167788544);
   }

   private boolean initialize(ContextStack var1) {
      if (var1.getEnv().getParseNonConforming()) {
         Vector var2 = new Vector();
         Vector var3 = new Vector();
         Vector var4 = new Vector();

         try {
            this.addNonRemoteInterfaces(var2, var1);
            return this.addAllMethods(this.getClassDefinition(), var3, false, false, var1) == null || !this.addConformingConstants(var4, false, var1) || this.initialize(var2, var3, var4, var1, false);
         } catch (ClassNotFound var6) {
            classNotFound(var1, var6);
            return false;
         }
      } else {
         return this.initialize((Vector)null, (Vector)null, (Vector)null, var1, false);
      }
   }
}
