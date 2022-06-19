package sun.rmi.rmic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class RemoteClass implements RMIConstants {
   private BatchEnvironment env;
   private ClassDefinition implClassDef;
   private ClassDefinition[] remoteInterfaces;
   private Method[] remoteMethods;
   private long interfaceHash;
   private ClassDefinition defRemote;
   private ClassDefinition defException;
   private ClassDefinition defRemoteException;

   public static RemoteClass forClass(BatchEnvironment var0, ClassDefinition var1) {
      RemoteClass var2 = new RemoteClass(var0, var1);
      return var2.initialize() ? var2 : null;
   }

   public ClassDefinition getClassDefinition() {
      return this.implClassDef;
   }

   public Identifier getName() {
      return this.implClassDef.getName();
   }

   public ClassDefinition[] getRemoteInterfaces() {
      return (ClassDefinition[])this.remoteInterfaces.clone();
   }

   public Method[] getRemoteMethods() {
      return (Method[])this.remoteMethods.clone();
   }

   public long getInterfaceHash() {
      return this.interfaceHash;
   }

   public String toString() {
      return "remote class " + this.implClassDef.getName().toString();
   }

   private RemoteClass(BatchEnvironment var1, ClassDefinition var2) {
      this.env = var1;
      this.implClassDef = var2;
   }

   private boolean initialize() {
      if (this.implClassDef.isInterface()) {
         this.env.error(0L, "rmic.cant.make.stubs.for.interface", this.implClassDef.getName());
         return false;
      } else {
         try {
            this.defRemote = this.env.getClassDeclaration(idRemote).getClassDefinition(this.env);
            this.defException = this.env.getClassDeclaration(idJavaLangException).getClassDefinition(this.env);
            this.defRemoteException = this.env.getClassDeclaration(idRemoteException).getClassDefinition(this.env);
         } catch (ClassNotFound var10) {
            this.env.error(0L, "rmic.class.not.found", var10.name);
            return false;
         }

         Vector var1 = new Vector();
         ClassDefinition var2 = this.implClassDef;

         ClassDefinition var5;
         while(var2 != null) {
            try {
               ClassDeclaration[] var3 = var2.getInterfaces();

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  var5 = var3[var4].getClassDefinition(this.env);
                  if (!var1.contains(var5) && this.defRemote.implementedBy(this.env, var3[var4])) {
                     var1.addElement(var5);
                     if (this.env.verbose()) {
                        System.out.println("[found remote interface: " + var5.getName() + "]");
                     }
                  }
               }

               if (var2 == this.implClassDef && var1.isEmpty()) {
                  if (this.defRemote.implementedBy(this.env, this.implClassDef.getClassDeclaration())) {
                     this.env.error(0L, "rmic.must.implement.remote.directly", this.implClassDef.getName());
                  } else {
                     this.env.error(0L, "rmic.must.implement.remote", this.implClassDef.getName());
                  }

                  return false;
               }

               var2 = var2.getSuperClass() != null ? var2.getSuperClass().getClassDefinition(this.env) : null;
            } catch (ClassNotFound var11) {
               this.env.error(0L, "class.not.found", var11.name, var2.getName());
               return false;
            }
         }

         Hashtable var12 = new Hashtable();
         boolean var13 = false;
         Enumeration var14 = var1.elements();

         while(var14.hasMoreElements()) {
            var5 = (ClassDefinition)var14.nextElement();
            if (!this.collectRemoteMethods(var5, var12)) {
               var13 = true;
            }
         }

         if (var13) {
            return false;
         } else {
            this.remoteInterfaces = new ClassDefinition[var1.size()];
            var1.copyInto(this.remoteInterfaces);
            String[] var15 = new String[var12.size()];
            int var16 = 0;

            for(Enumeration var6 = var12.elements(); var6.hasMoreElements(); ++var16) {
               Method var7 = (Method)var6.nextElement();
               String var8 = var7.getNameAndDescriptor();

               int var9;
               for(var9 = var16; var9 > 0 && var8.compareTo(var15[var9 - 1]) < 0; --var9) {
                  var15[var9] = var15[var9 - 1];
               }

               var15[var9] = var8;
            }

            this.remoteMethods = new Method[var12.size()];

            for(int var17 = 0; var17 < this.remoteMethods.length; ++var17) {
               this.remoteMethods[var17] = (Method)var12.get(var15[var17]);
               if (this.env.verbose()) {
                  System.out.print("[found remote method <" + var17 + ">: " + this.remoteMethods[var17].getOperationString());
                  ClassDeclaration[] var18 = this.remoteMethods[var17].getExceptions();
                  if (var18.length > 0) {
                     System.out.print(" throws ");
                  }

                  for(int var19 = 0; var19 < var18.length; ++var19) {
                     if (var19 > 0) {
                        System.out.print(", ");
                     }

                     System.out.print(var18[var19].getName());
                  }

                  System.out.println("]");
               }
            }

            this.interfaceHash = this.computeInterfaceHash();
            return true;
         }
      }
   }

   private boolean collectRemoteMethods(ClassDefinition var1, Hashtable var2) {
      if (!var1.isInterface()) {
         throw new Error("expected interface, not class: " + var1.getName());
      } else {
         boolean var3 = false;

         label103:
         for(MemberDefinition var4 = var1.getFirstMember(); var4 != null; var4 = var4.getNextMember()) {
            if (var4.isMethod() && !var4.isConstructor() && !var4.isInitializer()) {
               ClassDeclaration[] var5 = var4.getExceptions(this.env);
               boolean var6 = false;

               for(int var7 = 0; var7 < var5.length; ++var7) {
                  try {
                     if (this.defRemoteException.subClassOf(this.env, var5[var7])) {
                        var6 = true;
                        break;
                     }
                  } catch (ClassNotFound var12) {
                     this.env.error(0L, "class.not.found", var12.name, var1.getName());
                     continue label103;
                  }
               }

               if (!var6) {
                  this.env.error(0L, "rmic.must.throw.remoteexception", var1.getName(), var4.toString());
                  var3 = true;
               } else {
                  try {
                     MemberDefinition var16 = this.implClassDef.findMethod(this.env, var4.getName(), var4.getType());
                     if (var16 != null) {
                        var5 = var16.getExceptions(this.env);

                        for(int var8 = 0; var8 < var5.length; ++var8) {
                           if (!this.defException.superClassOf(this.env, var5[var8])) {
                              this.env.error(0L, "rmic.must.only.throw.exception", var16.toString(), var5[var8].getName());
                              var3 = true;
                              continue label103;
                           }
                        }
                     }
                  } catch (ClassNotFound var11) {
                     this.env.error(0L, "class.not.found", var11.name, this.implClassDef.getName());
                     continue;
                  }

                  Method var17 = new Method(var4);
                  String var18 = var17.getNameAndDescriptor();
                  Method var9 = (Method)var2.get(var18);
                  if (var9 != null) {
                     var17 = var17.mergeWith(var9);
                     if (var17 == null) {
                        var3 = true;
                        continue;
                     }
                  }

                  var2.put(var18, var17);
               }
            }
         }

         try {
            ClassDeclaration[] var13 = var1.getInterfaces();

            for(int var14 = 0; var14 < var13.length; ++var14) {
               ClassDefinition var15 = var13[var14].getClassDefinition(this.env);
               if (!this.collectRemoteMethods(var15, var2)) {
                  var3 = true;
               }
            }
         } catch (ClassNotFound var10) {
            this.env.error(0L, "class.not.found", var10.name, var1.getName());
            return false;
         }

         return !var3;
      }
   }

   private long computeInterfaceHash() {
      long var1 = 0L;
      ByteArrayOutputStream var3 = new ByteArrayOutputStream(512);

      try {
         MessageDigest var4 = MessageDigest.getInstance("SHA");
         DataOutputStream var5 = new DataOutputStream(new DigestOutputStream(var3, var4));
         var5.writeInt(1);

         for(int var6 = 0; var6 < this.remoteMethods.length; ++var6) {
            MemberDefinition var7 = this.remoteMethods[var6].getMemberDefinition();
            Identifier var8 = var7.getName();
            Type var9 = var7.getType();
            var5.writeUTF(var8.toString());
            var5.writeUTF(var9.getTypeSignature());
            ClassDeclaration[] var10 = var7.getExceptions(this.env);
            this.sortClassDeclarations(var10);

            for(int var11 = 0; var11 < var10.length; ++var11) {
               var5.writeUTF(Names.mangleClass(var10[var11].getName()).toString());
            }
         }

         var5.flush();
         byte[] var14 = var4.digest();

         for(int var15 = 0; var15 < Math.min(8, var14.length); ++var15) {
            var1 += (long)(var14[var15] & 255) << var15 * 8;
         }

         return var1;
      } catch (IOException var12) {
         throw new Error("unexpected exception computing intetrface hash: " + var12);
      } catch (NoSuchAlgorithmException var13) {
         throw new Error("unexpected exception computing intetrface hash: " + var13);
      }
   }

   private void sortClassDeclarations(ClassDeclaration[] var1) {
      for(int var2 = 1; var2 < var1.length; ++var2) {
         ClassDeclaration var3 = var1[var2];
         String var4 = Names.mangleClass(var3.getName()).toString();

         int var5;
         for(var5 = var2; var5 > 0 && var4.compareTo(Names.mangleClass(var1[var5 - 1].getName()).toString()) < 0; --var5) {
            var1[var5] = var1[var5 - 1];
         }

         var1[var5] = var3;
      }

   }

   public class Method implements Cloneable {
      private MemberDefinition memberDef;
      private long methodHash;
      private ClassDeclaration[] exceptions;

      public MemberDefinition getMemberDefinition() {
         return this.memberDef;
      }

      public Identifier getName() {
         return this.memberDef.getName();
      }

      public Type getType() {
         return this.memberDef.getType();
      }

      public ClassDeclaration[] getExceptions() {
         return (ClassDeclaration[])this.exceptions.clone();
      }

      public long getMethodHash() {
         return this.methodHash;
      }

      public String toString() {
         return this.memberDef.toString();
      }

      public String getOperationString() {
         return this.memberDef.toString();
      }

      public String getNameAndDescriptor() {
         return this.memberDef.getName().toString() + this.memberDef.getType().getTypeSignature();
      }

      Method(MemberDefinition var2) {
         this.memberDef = var2;
         this.exceptions = var2.getExceptions(RemoteClass.this.env);
         this.methodHash = this.computeMethodHash();
      }

      protected Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new Error("clone failed");
         }
      }

      private Method mergeWith(Method var1) {
         if (this.getName().equals(var1.getName()) && this.getType().equals(var1.getType())) {
            Vector var2 = new Vector();

            try {
               this.collectCompatibleExceptions(var1.exceptions, this.exceptions, var2);
               this.collectCompatibleExceptions(this.exceptions, var1.exceptions, var2);
            } catch (ClassNotFound var4) {
               RemoteClass.this.env.error(0L, "class.not.found", var4.name, RemoteClass.this.getClassDefinition().getName());
               return null;
            }

            Method var3 = (Method)this.clone();
            var3.exceptions = new ClassDeclaration[var2.size()];
            var2.copyInto(var3.exceptions);
            return var3;
         } else {
            throw new Error("attempt to merge method \"" + var1.getNameAndDescriptor() + "\" with \"" + this.getNameAndDescriptor());
         }
      }

      private void collectCompatibleExceptions(ClassDeclaration[] var1, ClassDeclaration[] var2, Vector var3) throws ClassNotFound {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            ClassDefinition var5 = var1[var4].getClassDefinition(RemoteClass.this.env);
            if (!var3.contains(var1[var4])) {
               for(int var6 = 0; var6 < var2.length; ++var6) {
                  if (var5.subClassOf(RemoteClass.this.env, var2[var6])) {
                     var3.addElement(var1[var4]);
                     break;
                  }
               }
            }
         }

      }

      private long computeMethodHash() {
         long var1 = 0L;
         ByteArrayOutputStream var3 = new ByteArrayOutputStream(512);

         try {
            MessageDigest var4 = MessageDigest.getInstance("SHA");
            DataOutputStream var5 = new DataOutputStream(new DigestOutputStream(var3, var4));
            String var6 = this.getNameAndDescriptor();
            if (RemoteClass.this.env.verbose()) {
               System.out.println("[string used for method hash: \"" + var6 + "\"]");
            }

            var5.writeUTF(var6);
            var5.flush();
            byte[] var7 = var4.digest();

            for(int var8 = 0; var8 < Math.min(8, var7.length); ++var8) {
               var1 += (long)(var7[var8] & 255) << var8 * 8;
            }

            return var1;
         } catch (IOException var9) {
            throw new Error("unexpected exception computing intetrface hash: " + var9);
         } catch (NoSuchAlgorithmException var10) {
            throw new Error("unexpected exception computing intetrface hash: " + var10);
         }
      }
   }
}
