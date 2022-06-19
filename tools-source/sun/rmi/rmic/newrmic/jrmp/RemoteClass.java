package sun.rmi.rmic.newrmic.jrmp;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.rmi.rmic.newrmic.BatchEnvironment;

final class RemoteClass {
   private final BatchEnvironment env;
   private final ClassDoc implClass;
   private ClassDoc[] remoteInterfaces;
   private Method[] remoteMethods;
   private long interfaceHash;

   static RemoteClass forClass(BatchEnvironment var0, ClassDoc var1) {
      RemoteClass var2 = new RemoteClass(var0, var1);
      return var2.init() ? var2 : null;
   }

   private RemoteClass(BatchEnvironment var1, ClassDoc var2) {
      this.env = var1;
      this.implClass = var2;
   }

   ClassDoc classDoc() {
      return this.implClass;
   }

   ClassDoc[] remoteInterfaces() {
      return (ClassDoc[])this.remoteInterfaces.clone();
   }

   Method[] remoteMethods() {
      return (Method[])this.remoteMethods.clone();
   }

   long interfaceHash() {
      return this.interfaceHash;
   }

   private boolean init() {
      if (this.implClass.isInterface()) {
         this.env.error("rmic.cant.make.stubs.for.interface", this.implClass.qualifiedName());
         return false;
      } else {
         ArrayList var1 = new ArrayList();

         int var5;
         for(ClassDoc var2 = this.implClass; var2 != null; var2 = var2.superclass()) {
            ClassDoc[] var3 = var2.interfaces();
            int var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               ClassDoc var6 = var3[var5];
               if (!var1.contains(var6) && var6.subclassOf(this.env.docRemote())) {
                  var1.add(var6);
                  if (this.env.verbose()) {
                     this.env.output("[found remote interface: " + var6.qualifiedName() + "]");
                  }
               }
            }

            if (var2 == this.implClass && var1.isEmpty()) {
               if (this.implClass.subclassOf(this.env.docRemote())) {
                  this.env.error("rmic.must.implement.remote.directly", this.implClass.qualifiedName());
               } else {
                  this.env.error("rmic.must.implement.remote", this.implClass.qualifiedName());
               }

               return false;
            }
         }

         this.remoteInterfaces = (ClassDoc[])var1.toArray(new ClassDoc[var1.size()]);
         HashMap var9 = new HashMap();
         boolean var10 = false;
         Iterator var11 = var1.iterator();

         while(var11.hasNext()) {
            ClassDoc var13 = (ClassDoc)var11.next();
            if (!this.collectRemoteMethods(var13, var9)) {
               var10 = true;
            }
         }

         if (var10) {
            return false;
         } else {
            String[] var12 = (String[])var9.keySet().toArray(new String[var9.size()]);
            Arrays.sort(var12);
            this.remoteMethods = new Method[var9.size()];

            for(var5 = 0; var5 < this.remoteMethods.length; ++var5) {
               this.remoteMethods[var5] = (Method)var9.get(var12[var5]);
               if (this.env.verbose()) {
                  String var14 = "[found remote method <" + var5 + ">: " + this.remoteMethods[var5].operationString();
                  ClassDoc[] var7 = this.remoteMethods[var5].exceptionTypes();
                  if (var7.length > 0) {
                     var14 = var14 + " throws ";

                     for(int var8 = 0; var8 < var7.length; ++var8) {
                        if (var8 > 0) {
                           var14 = var14 + ", ";
                        }

                        var14 = var14 + var7[var8].qualifiedName();
                     }
                  }

                  var14 = var14 + "\n\tname and descriptor = \"" + this.remoteMethods[var5].nameAndDescriptor();
                  var14 = var14 + "\n\tmethod hash = " + this.remoteMethods[var5].methodHash() + "]";
                  this.env.output(var14);
               }
            }

            this.interfaceHash = this.computeInterfaceHash();
            return true;
         }
      }
   }

   private boolean collectRemoteMethods(ClassDoc var1, Map var2) {
      if (!var1.isInterface()) {
         throw new AssertionError(var1.qualifiedName() + " not an interface");
      } else {
         boolean var3 = false;
         MethodDoc[] var4 = var1.methods();
         int var5 = var4.length;

         int var6;
         label69:
         for(var6 = 0; var6 < var5; ++var6) {
            MethodDoc var7 = var4[var6];
            boolean var8 = false;
            ClassDoc[] var9 = var7.thrownExceptions();
            int var10 = var9.length;

            int var11;
            for(var11 = 0; var11 < var10; ++var11) {
               ClassDoc var12 = var9[var11];
               if (this.env.docRemoteException().subclassOf(var12)) {
                  var8 = true;
                  break;
               }
            }

            if (!var8) {
               this.env.error("rmic.must.throw.remoteexception", var1.qualifiedName(), var7.name() + var7.signature());
               var3 = true;
            } else {
               MethodDoc var16 = this.findImplMethod(var7);
               if (var16 != null) {
                  ClassDoc[] var17 = var16.thrownExceptions();
                  var11 = var17.length;

                  for(int var20 = 0; var20 < var11; ++var20) {
                     ClassDoc var13 = var17[var20];
                     if (!var13.subclassOf(this.env.docException())) {
                        this.env.error("rmic.must.only.throw.exception", var16.name() + var16.signature(), var13.qualifiedName());
                        var3 = true;
                        continue label69;
                     }
                  }
               }

               Method var18 = new Method(var7);
               String var19 = var18.nameAndDescriptor();
               Method var21 = (Method)var2.get(var19);
               if (var21 != null) {
                  var18 = var18.mergeWith(var21);
               }

               var2.put(var19, var18);
            }
         }

         ClassDoc[] var14 = var1.interfaces();
         var5 = var14.length;

         for(var6 = 0; var6 < var5; ++var6) {
            ClassDoc var15 = var14[var6];
            if (!this.collectRemoteMethods(var15, var2)) {
               var3 = true;
            }
         }

         return !var3;
      }
   }

   private MethodDoc findImplMethod(MethodDoc var1) {
      String var2 = var1.name();
      String var3 = Util.methodDescriptorOf(var1);
      MethodDoc[] var4 = this.implClass.methods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         MethodDoc var7 = var4[var6];
         if (var2.equals(var7.name()) && var3.equals(Util.methodDescriptorOf(var7))) {
            return var7;
         }
      }

      return null;
   }

   private long computeInterfaceHash() {
      long var1 = 0L;
      ByteArrayOutputStream var3 = new ByteArrayOutputStream(512);

      try {
         MessageDigest var4 = MessageDigest.getInstance("SHA");
         DataOutputStream var5 = new DataOutputStream(new DigestOutputStream(var3, var4));
         var5.writeInt(1);
         Method[] var6 = this.remoteMethods;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Method var9 = var6[var8];
            MethodDoc var10 = var9.methodDoc();
            var5.writeUTF(var10.name());
            var5.writeUTF(Util.methodDescriptorOf(var10));
            ClassDoc[] var11 = var10.thrownExceptions();
            Arrays.sort(var11, new ClassDocComparator());
            ClassDoc[] var12 = var11;
            int var13 = var11.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               ClassDoc var15 = var12[var14];
               var5.writeUTF(Util.binaryNameOf(var15));
            }
         }

         var5.flush();
         byte[] var18 = var4.digest();

         for(var7 = 0; var7 < Math.min(8, var18.length); ++var7) {
            var1 += (long)(var18[var7] & 255) << var7 * 8;
         }

         return var1;
      } catch (IOException var16) {
         throw new AssertionError(var16);
      } catch (NoSuchAlgorithmException var17) {
         throw new AssertionError(var17);
      }
   }

   final class Method implements Cloneable {
      private final MethodDoc methodDoc;
      private final String operationString;
      private final String nameAndDescriptor;
      private final long methodHash;
      private ClassDoc[] exceptionTypes;

      Method(MethodDoc var2) {
         this.methodDoc = var2;
         this.exceptionTypes = var2.thrownExceptions();
         Arrays.sort(this.exceptionTypes, new ClassDocComparator());
         this.operationString = this.computeOperationString();
         this.nameAndDescriptor = var2.name() + Util.methodDescriptorOf(var2);
         this.methodHash = this.computeMethodHash();
      }

      MethodDoc methodDoc() {
         return this.methodDoc;
      }

      Type[] parameterTypes() {
         Parameter[] var1 = this.methodDoc.parameters();
         Type[] var2 = new Type[var1.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = var1[var3].type();
         }

         return var2;
      }

      ClassDoc[] exceptionTypes() {
         return (ClassDoc[])this.exceptionTypes.clone();
      }

      long methodHash() {
         return this.methodHash;
      }

      String operationString() {
         return this.operationString;
      }

      String nameAndDescriptor() {
         return this.nameAndDescriptor;
      }

      Method mergeWith(Method var1) {
         if (!this.nameAndDescriptor().equals(var1.nameAndDescriptor())) {
            throw new AssertionError("attempt to merge method \"" + var1.nameAndDescriptor() + "\" with \"" + this.nameAndDescriptor());
         } else {
            ArrayList var2 = new ArrayList();
            this.collectCompatibleExceptions(var1.exceptionTypes, this.exceptionTypes, var2);
            this.collectCompatibleExceptions(this.exceptionTypes, var1.exceptionTypes, var2);
            Method var3 = this.clone();
            var3.exceptionTypes = (ClassDoc[])var2.toArray(new ClassDoc[var2.size()]);
            return var3;
         }
      }

      protected Method clone() {
         try {
            return (Method)super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new AssertionError(var2);
         }
      }

      private void collectCompatibleExceptions(ClassDoc[] var1, ClassDoc[] var2, List var3) {
         ClassDoc[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ClassDoc var7 = var4[var6];
            if (!var3.contains(var7)) {
               ClassDoc[] var8 = var2;
               int var9 = var2.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  ClassDoc var11 = var8[var10];
                  if (var7.subclassOf(var11)) {
                     var3.add(var7);
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
            String var6 = this.nameAndDescriptor();
            var5.writeUTF(var6);
            var5.flush();
            byte[] var7 = var4.digest();

            for(int var8 = 0; var8 < Math.min(8, var7.length); ++var8) {
               var1 += (long)(var7[var8] & 255) << var8 * 8;
            }

            return var1;
         } catch (IOException var9) {
            throw new AssertionError(var9);
         } catch (NoSuchAlgorithmException var10) {
            throw new AssertionError(var10);
         }
      }

      private String computeOperationString() {
         Type var1 = this.methodDoc.returnType();
         String var2 = var1.qualifiedTypeName() + " " + this.methodDoc.name() + "(";
         Parameter[] var3 = this.methodDoc.parameters();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var4 > 0) {
               var2 = var2 + ", ";
            }

            var2 = var2 + var3[var4].type().toString();
         }

         var2 = var2 + ")" + var1.dimension();
         return var2;
      }
   }

   private static class ClassDocComparator implements Comparator {
      private ClassDocComparator() {
      }

      public int compare(ClassDoc var1, ClassDoc var2) {
         return Util.binaryNameOf(var1).compareTo(Util.binaryNameOf(var2));
      }

      // $FF: synthetic method
      ClassDocComparator(Object var1) {
         this();
      }
   }
}
