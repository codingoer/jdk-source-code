package sun.rmi.rmic.iiop;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.MemberDefinition;

public class ImplementationType extends ClassType {
   public static ImplementationType forImplementation(ClassDefinition var0, ContextStack var1, boolean var2) {
      if (var1.anyErrors()) {
         return null;
      } else {
         boolean var3 = false;
         ImplementationType var4 = null;

         try {
            sun.tools.java.Type var5 = var0.getType();
            Type var6 = getType(var5, var1);
            if (var6 != null) {
               if (!(var6 instanceof ImplementationType)) {
                  return null;
               }

               return (ImplementationType)var6;
            }

            if (couldBeImplementation(var2, var1, var0)) {
               ImplementationType var7 = new ImplementationType(var1, var0);
               putType(var5, var7, var1);
               var1.push(var7);
               var3 = true;
               if (var7.initialize(var1, var2)) {
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
      return "Implementation";
   }

   private ImplementationType(ContextStack var1, ClassDefinition var2) {
      super(100728832, var2, var1);
   }

   private static boolean couldBeImplementation(boolean var0, ContextStack var1, ClassDefinition var2) {
      boolean var3 = false;
      BatchEnvironment var4 = var1.getEnv();

      try {
         if (!var2.isClass()) {
            failedConstraint(17, var0, var1, var2.getName());
         } else {
            var3 = var4.defRemote.implementedBy(var4, var2.getClassDeclaration());
            if (!var3) {
               failedConstraint(8, var0, var1, var2.getName());
            }
         }
      } catch (ClassNotFound var6) {
         classNotFound(var1, var6);
      }

      return var3;
   }

   private boolean initialize(ContextStack var1, boolean var2) {
      boolean var3 = false;
      ClassDefinition var4 = this.getClassDefinition();
      if (this.initParents(var1)) {
         Vector var5 = new Vector();
         Vector var6 = new Vector();

         try {
            if (this.addRemoteInterfaces(var5, true, var1) != null) {
               boolean var7 = false;

               for(int var8 = 0; var8 < var5.size(); ++var8) {
                  InterfaceType var9 = (InterfaceType)var5.elementAt(var8);
                  if (var9.isType(4096) || var9.isType(524288)) {
                     var7 = true;
                  }

                  copyRemoteMethods(var9, var6);
               }

               if (!var7) {
                  failedConstraint(8, var2, var1, this.getQualifiedName());
                  return false;
               }

               if (this.checkMethods(var4, var6, var1, var2)) {
                  var3 = this.initialize(var5, var6, (Vector)null, var1, var2);
               }
            }
         } catch (ClassNotFound var10) {
            classNotFound(var1, var10);
         }
      }

      return var3;
   }

   private static void copyRemoteMethods(InterfaceType var0, Vector var1) {
      if (var0.isType(4096)) {
         CompoundType.Method[] var2 = var0.getMethods();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            CompoundType.Method var4 = var2[var3];
            if (!var1.contains(var4)) {
               var1.addElement(var4);
            }
         }

         InterfaceType[] var5 = var0.getInterfaces();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            copyRemoteMethods(var5[var6], var1);
         }
      }

   }

   private boolean checkMethods(ClassDefinition var1, Vector var2, ContextStack var3, boolean var4) {
      CompoundType.Method[] var5 = new CompoundType.Method[var2.size()];
      var2.copyInto(var5);

      for(MemberDefinition var6 = var1.getFirstMember(); var6 != null; var6 = var6.getNextMember()) {
         if (var6.isMethod() && !var6.isConstructor() && !var6.isInitializer() && !this.updateExceptions(var6, var5, var3, var4)) {
            return false;
         }
      }

      return true;
   }

   private boolean updateExceptions(MemberDefinition var1, CompoundType.Method[] var2, ContextStack var3, boolean var4) {
      int var5 = var2.length;
      String var6 = var1.toString();

      for(int var7 = 0; var7 < var5; ++var7) {
         CompoundType.Method var8 = var2[var7];
         MemberDefinition var9 = var8.getMemberDefinition();
         if (var6.equals(var9.toString())) {
            try {
               ValueType[] var10 = this.getMethodExceptions(var1, var4, var3);
               var8.setImplExceptions(var10);
            } catch (Exception var11) {
               return false;
            }
         }
      }

      return true;
   }
}
