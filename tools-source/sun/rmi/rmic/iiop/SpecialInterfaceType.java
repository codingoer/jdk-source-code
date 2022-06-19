package sun.rmi.rmic.iiop;

import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Identifier;

public class SpecialInterfaceType extends InterfaceType {
   public static SpecialInterfaceType forSpecial(ClassDefinition var0, ContextStack var1) {
      if (var1.anyErrors()) {
         return null;
      } else {
         sun.tools.java.Type var2 = var0.getType();
         Type var3 = getType(var2, var1);
         if (var3 != null) {
            return !(var3 instanceof SpecialInterfaceType) ? null : (SpecialInterfaceType)var3;
         } else if (isSpecial(var2, var0, var1)) {
            SpecialInterfaceType var4 = new SpecialInterfaceType(var1, 0, var0);
            putType(var2, var4, var1);
            var1.push(var4);
            if (var4.initialize(var2, var1)) {
               var1.pop(true);
               return var4;
            } else {
               removeType(var2, var1);
               var1.pop(false);
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public String getTypeDescription() {
      return "Special interface";
   }

   private SpecialInterfaceType(ContextStack var1, int var2, ClassDefinition var3) {
      super(var1, var2 | 536870912 | 134217728 | 33554432, var3);
      this.setNames(var3.getName(), (String[])null, (String)null);
   }

   private static boolean isSpecial(sun.tools.java.Type var0, ClassDefinition var1, ContextStack var2) {
      if (var0.isType(10)) {
         Identifier var3 = var0.getClassName();
         if (var3.equals(idRemote)) {
            return true;
         }

         if (var3 == idJavaIoSerializable) {
            return true;
         }

         if (var3 == idJavaIoExternalizable) {
            return true;
         }

         if (var3 == idCorbaObject) {
            return true;
         }

         if (var3 == idIDLEntity) {
            return true;
         }

         BatchEnvironment var4 = var2.getEnv();

         try {
            if (var4.defCorbaObject.implementedBy(var4, var1.getClassDeclaration())) {
               return true;
            }
         } catch (ClassNotFound var6) {
            classNotFound(var2, var6);
         }
      }

      return false;
   }

   private boolean initialize(sun.tools.java.Type var1, ContextStack var2) {
      int var3 = 0;
      Identifier var4 = null;
      String var5 = null;
      String[] var6 = null;
      boolean var7 = var2.size() > 0 && var2.getContext().isConstant();
      if (var1.isType(10)) {
         var4 = var1.getClassName();
         if (var4.equals(idRemote)) {
            var3 = 524288;
            var5 = "Remote";
            var6 = IDL_JAVA_RMI_MODULE;
         } else if (var4 == idJavaIoSerializable) {
            var3 = 1024;
            var5 = "Serializable";
            var6 = IDL_JAVA_IO_MODULE;
         } else if (var4 == idJavaIoExternalizable) {
            var3 = 1024;
            var5 = "Externalizable";
            var6 = IDL_JAVA_IO_MODULE;
         } else if (var4 == idIDLEntity) {
            var3 = 1024;
            var5 = "IDLEntity";
            var6 = IDL_ORG_OMG_CORBA_PORTABLE_MODULE;
         } else {
            var3 = 2048;
            if (var4 == idCorbaObject) {
               var5 = IDLNames.getTypeName(var3, var7);
               var6 = null;
            } else {
               try {
                  var5 = IDLNames.getClassOrInterfaceName(var4, this.env);
                  var6 = IDLNames.getModuleNames(var4, this.isBoxed(), this.env);
               } catch (Exception var9) {
                  failedConstraint(7, false, var2, var4.toString(), var9.getMessage());
                  throw new CompilerError("");
               }
            }
         }
      }

      if (var3 == 0) {
         return false;
      } else {
         this.setTypeCode(var3 | 536870912 | 134217728 | 33554432);
         if (var5 == null) {
            throw new CompilerError("Not a special type");
         } else {
            this.setNames(var4, var6, var5);
            return this.initialize((Vector)null, (Vector)null, (Vector)null, var2, false);
         }
      }
   }
}
