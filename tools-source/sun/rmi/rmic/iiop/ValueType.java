package sun.rmi.rmic.iiop;

import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.util.Hashtable;
import java.util.Vector;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.MemberDefinition;

public class ValueType extends ClassType {
   private boolean isCustom = false;

   public static ValueType forValue(ClassDefinition var0, ContextStack var1, boolean var2) {
      if (var1.anyErrors()) {
         return null;
      } else {
         sun.tools.java.Type var3 = var0.getType();
         String var4 = var3.toString();
         Type var5 = getType(var4, var1);
         if (var5 != null) {
            return !(var5 instanceof ValueType) ? null : (ValueType)var5;
         } else {
            boolean var6 = false;
            if (var0.getClassDeclaration().getName() == idJavaLangClass) {
               var6 = true;
               BatchEnvironment var7 = var1.getEnv();
               ClassDeclaration var8 = var7.getClassDeclaration(idClassDesc);
               ClassDefinition var9 = null;

               try {
                  var9 = var8.getClassDefinition(var7);
               } catch (ClassNotFound var11) {
                  classNotFound(var1, var11);
                  return null;
               }

               var0 = var9;
            }

            if (couldBeValue(var1, var0)) {
               ValueType var12 = new ValueType(var0, var1, var6);
               putType(var4, var12, var1);
               var1.push(var12);
               if (var12.initialize(var1, var2)) {
                  var1.pop(true);
                  return var12;
               } else {
                  removeType(var4, var1);
                  var1.pop(false);
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }

   public String getTypeDescription() {
      String var1 = this.addExceptionDescription("Value");
      if (this.isCustom) {
         var1 = "Custom " + var1;
      }

      if (this.isIDLEntity) {
         var1 = var1 + " [IDLEntity]";
      }

      return var1;
   }

   public boolean isCustom() {
      return this.isCustom;
   }

   private ValueType(ClassDefinition var1, ContextStack var2, boolean var3) {
      super(var2, var1, 100696064);
      if (var3) {
         this.setNames(idJavaLangClass, IDL_CLASS_MODULE, "ClassDesc");
      }

   }

   private static boolean couldBeValue(ContextStack var0, ClassDefinition var1) {
      boolean var2 = false;
      ClassDeclaration var3 = var1.getClassDeclaration();
      BatchEnvironment var4 = var0.getEnv();

      try {
         if (var4.defRemote.implementedBy(var4, var3)) {
            failedConstraint(10, false, var0, var1.getName());
         } else if (!var4.defSerializable.implementedBy(var4, var3)) {
            failedConstraint(11, false, var0, var1.getName());
         } else {
            var2 = true;
         }
      } catch (ClassNotFound var6) {
         classNotFound(var0, var6);
      }

      return var2;
   }

   private boolean initialize(ContextStack var1, boolean var2) {
      ClassDefinition var3 = this.getClassDefinition();
      ClassDeclaration var4 = this.getClassDeclaration();

      try {
         if (!this.initParents(var1)) {
            failedConstraint(12, var2, var1, this.getQualifiedName());
            return false;
         }

         Vector var5 = new Vector();
         Vector var6 = new Vector();
         Vector var7 = new Vector();
         if (this.addNonRemoteInterfaces(var5, var1) != null && this.addAllMethods(var3, var6, false, false, var1) != null && this.updateParentClassMethods(var3, var6, false, var1) != null) {
            if (this.addAllMembers(var7, false, false, var1)) {
               if (!this.initialize(var5, var6, var7, var1, var2)) {
                  return false;
               }

               boolean var8 = false;
               if (!this.env.defExternalizable.implementedBy(this.env, var4)) {
                  if (!this.checkPersistentFields(this.getClassInstance(), var2)) {
                     return false;
                  }
               } else {
                  var8 = true;
               }

               if (var8) {
                  this.isCustom = true;
               } else {
                  for(MemberDefinition var9 = var3.getFirstMember(); var9 != null; var9 = var9.getNextMember()) {
                     if (var9.isMethod() && !var9.isInitializer() && var9.isPrivate() && var9.getName().toString().equals("writeObject")) {
                        sun.tools.java.Type var10 = var9.getType();
                        sun.tools.java.Type var11 = var10.getReturnType();
                        if (var11 == sun.tools.java.Type.tVoid) {
                           sun.tools.java.Type[] var12 = var10.getArgumentTypes();
                           if (var12.length == 1 && var12[0].getTypeSignature().equals("Ljava/io/ObjectOutputStream;")) {
                              this.isCustom = true;
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } catch (ClassNotFound var13) {
         classNotFound(var1, var13);
      }

      return false;
   }

   private boolean checkPersistentFields(Class var1, boolean var2) {
      for(int var3 = 0; var3 < this.methods.length; ++var3) {
         if (this.methods[var3].getName().equals("writeObject") && this.methods[var3].getArguments().length == 1) {
            Type var4 = this.methods[var3].getReturnType();
            Type var5 = this.methods[var3].getArguments()[0];
            String var6 = var5.getQualifiedName();
            if (var4.isType(1) && var6.equals("java.io.ObjectOutputStream")) {
               return true;
            }
         }
      }

      MemberDefinition var10 = null;

      for(int var11 = 0; var11 < this.members.length; ++var11) {
         if (this.members[var11].getName().equals("serialPersistentFields")) {
            CompoundType.Member var13 = this.members[var11];
            Type var15 = var13.getType();
            Type var7 = var15.getElementType();
            if (var7 != null && var7.getQualifiedName().equals("java.io.ObjectStreamField")) {
               if (!var13.isStatic() || !var13.isFinal() || !var13.isPrivate()) {
                  failedConstraint(4, var2, this.stack, this.getQualifiedName());
                  return false;
               }

               var10 = var13.getMemberDefinition();
            }
         }
      }

      if (var10 == null) {
         return true;
      } else {
         Hashtable var12 = this.getPersistentFields(var1);
         boolean var14 = true;

         for(int var16 = 0; var16 < this.members.length; ++var16) {
            String var17 = this.members[var16].getName();
            String var8 = this.members[var16].getType().getSignature();
            String var9 = (String)var12.get(var17);
            if (var9 == null) {
               this.members[var16].setTransient();
            } else if (var9.equals(var8)) {
               var12.remove(var17);
            } else {
               var14 = false;
               failedConstraint(2, var2, this.stack, var17, this.getQualifiedName());
            }
         }

         if (var14 && var12.size() > 0) {
            var14 = false;
            failedConstraint(9, var2, this.stack, this.getQualifiedName());
         }

         return var14;
      }
   }

   private Hashtable getPersistentFields(Class var1) {
      Hashtable var2 = new Hashtable();
      ObjectStreamClass var3 = ObjectStreamClass.lookup(var1);
      if (var3 != null) {
         ObjectStreamField[] var4 = var3.getFields();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            String var7 = String.valueOf(var4[var5].getTypeCode());
            String var6;
            if (var4[var5].isPrimitive()) {
               var6 = var7;
            } else {
               if (var4[var5].getTypeCode() == '[') {
                  var7 = "";
               }

               var6 = var7 + var4[var5].getType().getName().replace('.', '/');
               if (var6.endsWith(";")) {
                  var6 = var6.substring(0, var6.length() - 1);
               }
            }

            var2.put(var4[var5].getName(), var6);
         }
      }

      return var2;
   }
}
