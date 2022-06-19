package sun.rmi.rmic.iiop;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;

public class RemoteType extends InterfaceType {
   public static RemoteType forRemote(ClassDefinition var0, ContextStack var1, boolean var2) {
      if (var1.anyErrors()) {
         return null;
      } else {
         boolean var3 = false;
         RemoteType var4 = null;

         try {
            sun.tools.java.Type var5 = var0.getType();
            Type var6 = getType(var5, var1);
            if (var6 != null) {
               if (!(var6 instanceof RemoteType)) {
                  return null;
               }

               return (RemoteType)var6;
            }

            if (couldBeRemote(var2, var1, var0)) {
               RemoteType var7 = new RemoteType(var1, var0);
               putType(var5, var7, var1);
               var1.push(var7);
               var3 = true;
               if (var7.initialize(var2, var1)) {
                  var1.pop(true);
                  var4 = var7;
               } else {
                  removeType(var5, var1);
                  var1.pop(false);
               }
            }
         } catch (CompilerError var8) {
            if (var3) {
               var1.pop(false);
            }
         }

         return var4;
      }
   }

   public String getTypeDescription() {
      return "Remote interface";
   }

   protected RemoteType(ContextStack var1, ClassDefinition var2) {
      super(var1, var2, 167776256);
   }

   protected RemoteType(ContextStack var1, ClassDefinition var2, int var3) {
      super(var1, var2, var3);
   }

   private static boolean couldBeRemote(boolean var0, ContextStack var1, ClassDefinition var2) {
      boolean var3 = false;
      BatchEnvironment var4 = var1.getEnv();

      try {
         if (!var2.isInterface()) {
            failedConstraint(16, var0, var1, var2.getName());
         } else {
            var3 = var4.defRemote.implementedBy(var4, var2.getClassDeclaration());
            if (!var3) {
               failedConstraint(1, var0, var1, var2.getName());
            }
         }
      } catch (ClassNotFound var6) {
         classNotFound(var1, var6);
      }

      return var3;
   }

   private boolean initialize(boolean var1, ContextStack var2) {
      boolean var3 = false;
      Vector var4 = new Vector();
      Vector var5 = new Vector();
      Vector var6 = new Vector();
      if (this.isConformingRemoteInterface(var4, var5, var6, var1, var2)) {
         var3 = this.initialize(var4, var5, var6, var2, var1);
      }

      return var3;
   }

   private boolean isConformingRemoteInterface(Vector var1, Vector var2, Vector var3, boolean var4, ContextStack var5) {
      ClassDefinition var6 = this.getClassDefinition();

      try {
         if (this.addRemoteInterfaces(var1, false, var5) == null) {
            return false;
         } else if (!this.addAllMembers(var3, true, var4, var5)) {
            return false;
         } else if (this.addAllMethods(var6, var2, true, var4, var5) == null) {
            return false;
         } else {
            boolean var7 = true;

            for(int var8 = 0; var8 < var2.size(); ++var8) {
               if (!this.isConformingRemoteMethod((CompoundType.Method)var2.elementAt(var8), var4)) {
                  var7 = false;
               }
            }

            if (!var7) {
               return false;
            } else {
               return true;
            }
         }
      } catch (ClassNotFound var9) {
         classNotFound(var5, var9);
         return false;
      }
   }
}
