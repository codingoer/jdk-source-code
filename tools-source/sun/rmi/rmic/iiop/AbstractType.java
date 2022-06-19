package sun.rmi.rmic.iiop;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;

public class AbstractType extends RemoteType {
   public static AbstractType forAbstract(ClassDefinition var0, ContextStack var1, boolean var2) {
      boolean var3 = false;
      AbstractType var4 = null;

      try {
         sun.tools.java.Type var5 = var0.getType();
         Type var6 = getType(var5, var1);
         if (var6 != null) {
            if (!(var6 instanceof AbstractType)) {
               return null;
            }

            return (AbstractType)var6;
         }

         if (couldBeAbstract(var1, var0, var2)) {
            AbstractType var7 = new AbstractType(var1, var0);
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

   public String getTypeDescription() {
      return "Abstract interface";
   }

   private AbstractType(ContextStack var1, ClassDefinition var2) {
      super(var1, var2, 167780352);
   }

   private static boolean couldBeAbstract(ContextStack var0, ClassDefinition var1, boolean var2) {
      boolean var3 = false;
      if (var1.isInterface()) {
         BatchEnvironment var4 = var0.getEnv();

         try {
            var3 = !var4.defRemote.implementedBy(var4, var1.getClassDeclaration());
            if (!var3) {
               failedConstraint(15, var2, var0, var1.getName());
            }
         } catch (ClassNotFound var6) {
            classNotFound(var0, var6);
         }
      } else {
         failedConstraint(14, var2, var0, var1.getName());
      }

      return var3;
   }

   private boolean initialize(boolean var1, ContextStack var2) {
      boolean var3 = false;
      ClassDefinition var4 = this.getClassDefinition();

      try {
         Vector var5 = new Vector();
         if (this.addAllMethods(var4, var5, true, var1, var2) != null) {
            boolean var6 = true;
            if (var5.size() > 0) {
               for(int var7 = 0; var7 < var5.size(); ++var7) {
                  if (!this.isConformingRemoteMethod((CompoundType.Method)var5.elementAt(var7), true)) {
                     var6 = false;
                  }
               }
            }

            if (var6) {
               var3 = this.initialize((Vector)null, var5, (Vector)null, var2, var1);
            }
         }
      } catch (ClassNotFound var8) {
         classNotFound(var2, var8);
      }

      return var3;
   }
}
