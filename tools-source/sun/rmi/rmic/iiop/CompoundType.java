package sun.rmi.rmic.iiop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;
import sun.rmi.rmic.IndentingWriter;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.tree.IntegerExpression;
import sun.tools.tree.LocalMember;
import sun.tools.tree.Node;

public abstract class CompoundType extends Type {
   protected Method[] methods;
   protected InterfaceType[] interfaces;
   protected Member[] members;
   protected ClassDefinition classDef;
   protected ClassDeclaration classDecl;
   protected boolean isCORBAObject = false;
   protected boolean isIDLEntity = false;
   protected boolean isAbstractBase = false;
   protected boolean isValueBase = false;
   protected boolean isCORBAUserException = false;
   protected boolean isException = false;
   protected boolean isCheckedException = false;
   protected boolean isRemoteExceptionOrSubclass = false;
   protected String idlExceptionName;
   protected String qualifiedIDLExceptionName;

   public boolean isCORBAObject() {
      return this.isCORBAObject;
   }

   public boolean isIDLEntity() {
      return this.isIDLEntity;
   }

   public boolean isValueBase() {
      return this.isValueBase;
   }

   public boolean isAbstractBase() {
      return this.isAbstractBase;
   }

   public boolean isException() {
      return this.isException;
   }

   public boolean isCheckedException() {
      return this.isCheckedException;
   }

   public boolean isRemoteExceptionOrSubclass() {
      return this.isRemoteExceptionOrSubclass;
   }

   public boolean isCORBAUserException() {
      return this.isCORBAUserException;
   }

   public boolean isIDLEntityException() {
      return this.isIDLEntity() && this.isException();
   }

   public boolean isBoxed() {
      return this.isIDLEntity() && !this.isValueBase() && !this.isAbstractBase() && !this.isCORBAObject() && !this.isIDLEntityException();
   }

   public String getIDLExceptionName() {
      return this.idlExceptionName;
   }

   public String getQualifiedIDLExceptionName(boolean var1) {
      return this.qualifiedIDLExceptionName != null && var1 && this.getIDLModuleNames().length > 0 ? "::" + this.qualifiedIDLExceptionName : this.qualifiedIDLExceptionName;
   }

   public String getSignature() {
      String var1 = this.classDecl.getType().getTypeSignature();
      if (var1.endsWith(";")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1;
   }

   public ClassDeclaration getClassDeclaration() {
      return this.classDecl;
   }

   public ClassDefinition getClassDefinition() {
      return this.classDef;
   }

   public ClassType getSuperclass() {
      return null;
   }

   public InterfaceType[] getInterfaces() {
      return this.interfaces != null ? (InterfaceType[])((InterfaceType[])this.interfaces.clone()) : null;
   }

   public Method[] getMethods() {
      return this.methods != null ? (Method[])((Method[])this.methods.clone()) : null;
   }

   public Member[] getMembers() {
      return this.members != null ? (Member[])((Member[])this.members.clone()) : null;
   }

   public static CompoundType forCompound(ClassDefinition var0, ContextStack var1) {
      CompoundType var2 = null;

      try {
         var2 = (CompoundType)makeType(var0.getType(), var0, var1);
      } catch (ClassCastException var4) {
      }

      return var2;
   }

   protected void destroy() {
      if (!this.destroyed) {
         super.destroy();
         int var1;
         if (this.methods != null) {
            for(var1 = 0; var1 < this.methods.length; ++var1) {
               if (this.methods[var1] != null) {
                  this.methods[var1].destroy();
               }
            }

            this.methods = null;
         }

         if (this.interfaces != null) {
            for(var1 = 0; var1 < this.interfaces.length; ++var1) {
               if (this.interfaces[var1] != null) {
                  this.interfaces[var1].destroy();
               }
            }

            this.interfaces = null;
         }

         if (this.members != null) {
            for(var1 = 0; var1 < this.members.length; ++var1) {
               if (this.members[var1] != null) {
                  this.members[var1].destroy();
               }
            }

            this.members = null;
         }

         this.classDef = null;
         this.classDecl = null;
      }

   }

   protected Class loadClass() {
      Class var1 = null;

      try {
         this.env.getMain().compileAllClasses(this.env);
      } catch (Exception var9) {
         ClassDeclaration var4;
         for(Enumeration var3 = this.env.getClasses(); var3.hasMoreElements(); var4 = (ClassDeclaration)var3.nextElement()) {
         }

         failedConstraint(26, false, this.stack, "required classes");
         this.env.flushErrors();
      }

      try {
         ClassLoader var2 = Thread.currentThread().getContextClassLoader();
         var1 = var2.loadClass(this.getQualifiedName());
      } catch (ClassNotFoundException var8) {
         try {
            var1 = this.env.classPathLoader.loadClass(this.getQualifiedName());
         } catch (NullPointerException var6) {
         } catch (ClassNotFoundException var7) {
         }
      }

      if (var1 == null) {
         if (this.env.loader == null) {
            File var10 = this.env.getMain().getDestinationDir();
            if (var10 == null) {
               var10 = new File(".");
            }

            this.env.loader = new DirectoryLoader(var10);
         }

         try {
            var1 = this.env.loader.loadClass(this.getQualifiedName());
         } catch (Exception var5) {
         }
      }

      return var1;
   }

   protected boolean printExtends(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      ClassType var5 = this.getSuperclass();
      if (var5 == null || var3 && (var5.isType(1024) || var5.isType(2048))) {
         return false;
      } else {
         var1.p(" extends ");
         var5.printTypeName(var1, var2, var3, var4);
         return true;
      }
   }

   protected void printImplements(IndentingWriter var1, String var2, boolean var3, boolean var4, boolean var5) throws IOException {
      InterfaceType[] var6 = this.getInterfaces();
      String var7 = " implements";
      if (this.isInterface()) {
         var7 = " extends";
      }

      if (var4) {
         var7 = ":";
      }

      for(int var8 = 0; var8 < var6.length; ++var8) {
         if (!var4 || !var6[var8].isType(1024) && !var6[var8].isType(2048)) {
            if (var8 == 0) {
               var1.p(var2 + var7 + " ");
            } else {
               var1.p(", ");
            }

            var6[var8].printTypeName(var1, var3, var4, var5);
         }
      }

   }

   protected void printMembers(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      Member[] var5 = this.getMembers();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         if (!var5[var6].isInnerClassDeclaration()) {
            Type var7 = var5[var6].getType();
            String var8 = var5[var6].getVisibility();
            String var9;
            if (var3) {
               var9 = var5[var6].getIDLName();
            } else {
               var9 = var5[var6].getName();
            }

            String var10 = var5[var6].getValue();
            var1.p(var8);
            if (var8.length() > 0) {
               var1.p(" ");
            }

            var7.printTypeName(var1, var2, var3, var4);
            var1.p(" " + var9);
            if (var10 != null) {
               var1.pln(" = " + var10 + ";");
            } else {
               var1.pln(";");
            }
         }
      }

   }

   protected void printMethods(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      Method[] var5 = this.getMethods();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         Method var7 = var5[var6];
         this.printMethod(var7, var1, var2, var3, var4);
      }

   }

   protected void printMethod(Method var1, IndentingWriter var2, boolean var3, boolean var4, boolean var5) throws IOException {
      String var6 = var1.getVisibility();
      var2.p(var6);
      if (var6.length() > 0) {
         var2.p(" ");
      }

      var1.getReturnType().printTypeName(var2, var3, var4, var5);
      if (var4) {
         var2.p(" " + var1.getIDLName());
      } else {
         var2.p(" " + var1.getName());
      }

      var2.p(" (");
      Type[] var7 = var1.getArguments();
      String[] var8 = var1.getArgumentNames();

      for(int var9 = 0; var9 < var7.length; ++var9) {
         if (var9 > 0) {
            var2.p(", ");
         }

         if (var4) {
            var2.p("in ");
         }

         var7[var9].printTypeName(var2, var3, var4, var5);
         var2.p(" " + var8[var9]);
      }

      var2.p(")");
      ValueType[] var11;
      if (this.isType(65536)) {
         var11 = var1.getImplExceptions();
      } else {
         var11 = var1.getExceptions();
      }

      for(int var10 = 0; var10 < var11.length; ++var10) {
         if (var10 == 0) {
            if (var4) {
               var2.p(" raises (");
            } else {
               var2.p(" throws ");
            }
         } else {
            var2.p(", ");
         }

         if (var4) {
            if (var3) {
               var2.p(var11[var10].getQualifiedIDLExceptionName(var5));
            } else {
               var2.p(var11[var10].getIDLExceptionName());
            }

            var2.p(" [a.k.a. ");
            var11[var10].printTypeName(var2, var3, var4, var5);
            var2.p("]");
         } else {
            var11[var10].printTypeName(var2, var3, var4, var5);
         }
      }

      if (var4 && var11.length > 0) {
         var2.p(")");
      }

      if (var1.isInherited()) {
         var2.p(" // Inherited from ");
         var2.p((Object)var1.getDeclaredBy());
      }

      var2.pln(";");
   }

   protected CompoundType(ContextStack var1, int var2, ClassDefinition var3) {
      super(var1, var2);
      this.classDef = var3;
      this.classDecl = var3.getClassDeclaration();
      this.interfaces = new InterfaceType[0];
      this.methods = new Method[0];
      this.members = new Member[0];
      if (var3.isInnerClass()) {
         this.setTypeCode(var2 | Integer.MIN_VALUE);
      }

      this.setFlags();
   }

   private void setFlags() {
      try {
         this.isCORBAObject = this.env.defCorbaObject.implementedBy(this.env, this.classDecl);
         this.isIDLEntity = this.env.defIDLEntity.implementedBy(this.env, this.classDecl);
         this.isValueBase = this.env.defValueBase.implementedBy(this.env, this.classDecl);
         this.isAbstractBase = this.isInterface() && this.isIDLEntity && !this.isValueBase && !this.isCORBAObject;
         this.isCORBAUserException = this.classDecl.getName() == idCorbaUserException;
         if (this.env.defThrowable.implementedBy(this.env, this.classDecl)) {
            this.isException = true;
            if (!this.env.defRuntimeException.implementedBy(this.env, this.classDecl) && !this.env.defError.implementedBy(this.env, this.classDecl)) {
               this.isCheckedException = true;
            } else {
               this.isCheckedException = false;
            }

            if (this.env.defRemoteException.implementedBy(this.env, this.classDecl)) {
               this.isRemoteExceptionOrSubclass = true;
            } else {
               this.isRemoteExceptionOrSubclass = false;
            }
         } else {
            this.isException = false;
         }
      } catch (ClassNotFound var2) {
         classNotFound(this.stack, var2);
      }

   }

   protected CompoundType(ContextStack var1, ClassDefinition var2, int var3) {
      super(var1, var3);
      this.classDef = var2;
      this.classDecl = var2.getClassDeclaration();
      if (var2.isInnerClass()) {
         this.setTypeCode(var3 | Integer.MIN_VALUE);
      }

      this.setFlags();
      Identifier var4 = var2.getName();

      try {
         String var5 = IDLNames.getClassOrInterfaceName(var4, this.env);
         String[] var6 = IDLNames.getModuleNames(var4, this.isBoxed(), this.env);
         this.setNames(var4, var6, var5);
         if (this.isException()) {
            this.isException = true;
            this.idlExceptionName = IDLNames.getExceptionName(this.getIDLName());
            this.qualifiedIDLExceptionName = IDLNames.getQualifiedName(this.getIDLModuleNames(), this.idlExceptionName);
         }

         this.interfaces = null;
         this.methods = null;
         this.members = null;
      } catch (Exception var8) {
         failedConstraint(7, false, var1, var4.toString(), var8.getMessage());
         throw new CompilerError("");
      }
   }

   protected boolean initialize(Vector var1, Vector var2, Vector var3, ContextStack var4, boolean var5) {
      boolean var6 = true;
      if (var1 != null && var1.size() > 0) {
         this.interfaces = new InterfaceType[var1.size()];
         var1.copyInto(this.interfaces);
      } else {
         this.interfaces = new InterfaceType[0];
      }

      if (var2 != null && var2.size() > 0) {
         this.methods = new Method[var2.size()];
         var2.copyInto(this.methods);

         try {
            IDLNames.setMethodNames(this, this.methods, this.env);
         } catch (Exception var10) {
            failedConstraint(13, var5, var4, this.getQualifiedName(), var10.getMessage());
            var6 = false;
         }
      } else {
         this.methods = new Method[0];
      }

      if (var3 != null && var3.size() > 0) {
         this.members = new Member[var3.size()];
         var3.copyInto(this.members);

         for(int var7 = 0; var7 < this.members.length; ++var7) {
            if (this.members[var7].isInnerClassDeclaration()) {
               try {
                  this.members[var7].init(var4, this);
               } catch (CompilerError var9) {
                  return false;
               }
            }
         }

         try {
            IDLNames.setMemberNames(this, this.members, this.methods, this.env);
         } catch (Exception var11) {
            int var8 = this.classDef.isInterface() ? 19 : 20;
            failedConstraint(var8, var5, var4, this.getQualifiedName(), var11.getMessage());
            var6 = false;
         }
      } else {
         this.members = new Member[0];
      }

      if (var6) {
         var6 = this.setRepositoryID();
      }

      return var6;
   }

   protected static Type makeType(sun.tools.java.Type var0, ClassDefinition var1, ContextStack var2) {
      if (var2.anyErrors()) {
         return null;
      } else {
         String var3 = var0.toString();
         Type var4 = getType(var3, var2);
         if (var4 != null) {
            return var4;
         } else {
            Object var11 = getType(var3 + var2.getContextCodeString(), var2);
            if (var11 != null) {
               return (Type)var11;
            } else {
               BatchEnvironment var5 = var2.getEnv();
               int var6 = var0.getTypeCode();
               switch (var6) {
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                     var11 = PrimitiveType.forPrimitive(var0, var2);
                     break;
                  case 8:
                  default:
                     throw new CompilerError("Unknown typecode (" + var6 + ") for " + var0.getTypeSignature());
                  case 9:
                     var11 = ArrayType.forArray(var0, var2);
                     break;
                  case 10:
                     try {
                        ClassDefinition var7 = var1;
                        if (var1 == null) {
                           var7 = var5.getClassDeclaration(var0).getClassDefinition(var5);
                        }

                        if (var7.isInterface()) {
                           var11 = SpecialInterfaceType.forSpecial(var7, var2);
                           if (var11 == null) {
                              if (var5.defRemote.implementedBy(var5, var7.getClassDeclaration())) {
                                 boolean var8 = var2.isParentAValue();
                                 var11 = RemoteType.forRemote(var7, var2, var8);
                                 if (var11 == null && var8) {
                                    var11 = NCInterfaceType.forNCInterface(var7, var2);
                                 }
                              } else {
                                 var11 = AbstractType.forAbstract(var7, var2, true);
                                 if (var11 == null) {
                                    var11 = NCInterfaceType.forNCInterface(var7, var2);
                                 }
                              }
                           }
                        } else {
                           var11 = SpecialClassType.forSpecial(var7, var2);
                           if (var11 == null) {
                              ClassDeclaration var12 = var7.getClassDeclaration();
                              if (var5.defRemote.implementedBy(var5, var12)) {
                                 boolean var9 = var2.isParentAValue();
                                 var11 = ImplementationType.forImplementation(var7, var2, var9);
                                 if (var11 == null && var9) {
                                    var11 = NCClassType.forNCClass(var7, var2);
                                 }
                              } else {
                                 if (var5.defSerializable.implementedBy(var5, var12)) {
                                    var11 = ValueType.forValue(var7, var2, true);
                                 }

                                 if (var11 == null) {
                                    var11 = NCClassType.forNCClass(var7, var2);
                                 }
                              }
                           }
                        }
                     } catch (ClassNotFound var10) {
                        classNotFound(var2, var10);
                     }
               }

               return (Type)var11;
            }
         }
      }
   }

   public static boolean isRemoteException(ClassType var0, BatchEnvironment var1) {
      sun.tools.java.Type var2 = var0.getClassDeclaration().getType();
      return var2.equals(var1.typeRemoteException) || var2.equals(var1.typeIOException) || var2.equals(var1.typeException) || var2.equals(var1.typeThrowable);
   }

   protected boolean isConformingRemoteMethod(Method var1, boolean var2) throws ClassNotFound {
      boolean var3 = false;
      ValueType[] var4 = var1.getExceptions();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (isRemoteException(var4[var5], this.env)) {
            var3 = true;
            break;
         }
      }

      if (!var3) {
         failedConstraint(5, var2, this.stack, var1.getEnclosing(), var1.toString());
      }

      boolean var8 = !this.isIDLEntityException(var1.getReturnType(), var1, var2);
      if (var8) {
         Type[] var6 = var1.getArguments();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            if (this.isIDLEntityException(var6[var7], var1, var2)) {
               var8 = false;
               break;
            }
         }
      }

      return var3 && var8;
   }

   protected boolean isIDLEntityException(Type var1, Method var2, boolean var3) throws ClassNotFound {
      if (var1.isArray()) {
         var1 = var1.getElementType();
      }

      if (var1.isCompound() && ((CompoundType)var1).isIDLEntityException()) {
         failedConstraint(18, var3, this.stack, var2.getEnclosing(), var2.toString());
         return true;
      } else {
         return false;
      }
   }

   protected void swapInvalidTypes() {
      int var1;
      for(var1 = 0; var1 < this.interfaces.length; ++var1) {
         if (this.interfaces[var1].getStatus() != 1) {
            this.interfaces[var1] = (InterfaceType)this.getValidType(this.interfaces[var1]);
         }
      }

      for(var1 = 0; var1 < this.methods.length; ++var1) {
         this.methods[var1].swapInvalidTypes();
      }

      for(var1 = 0; var1 < this.members.length; ++var1) {
         this.members[var1].swapInvalidTypes();
      }

   }

   protected boolean addTypes(int var1, HashSet var2, Vector var3) {
      boolean var4 = super.addTypes(var1, var2, var3);
      if (var4) {
         ClassType var5 = this.getSuperclass();
         if (var5 != null) {
            var5.addTypes(var1, var2, var3);
         }

         int var6;
         for(var6 = 0; var6 < this.interfaces.length; ++var6) {
            this.interfaces[var6].addTypes(var1, var2, var3);
         }

         for(var6 = 0; var6 < this.methods.length; ++var6) {
            this.methods[var6].getReturnType().addTypes(var1, var2, var3);
            Type[] var7 = this.methods[var6].getArguments();

            for(int var8 = 0; var8 < var7.length; ++var8) {
               Type var9 = var7[var8];
               var9.addTypes(var1, var2, var3);
            }

            ValueType[] var12 = this.methods[var6].getExceptions();

            for(int var13 = 0; var13 < var12.length; ++var13) {
               ValueType var10 = var12[var13];
               var10.addTypes(var1, var2, var3);
            }
         }

         for(var6 = 0; var6 < this.members.length; ++var6) {
            Type var11 = this.members[var6].getType();
            var11.addTypes(var1, var2, var3);
         }
      }

      return var4;
   }

   private boolean isConformingConstantType(MemberDefinition var1) {
      return this.isConformingConstantType(var1.getType(), var1);
   }

   private boolean isConformingConstantType(sun.tools.java.Type var1, MemberDefinition var2) {
      boolean var3 = true;
      int var4 = var1.getTypeCode();
      switch (var4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            break;
         case 8:
         default:
            throw new Error("unexpected type code: " + var4);
         case 9:
            failedConstraint(3, false, this.stack, var2.getClassDefinition(), var2.getName());
            var3 = false;
            break;
         case 10:
            if (var1.getClassName() != idJavaLangString) {
               failedConstraint(3, false, this.stack, var2.getClassDefinition(), var2.getName());
               var3 = false;
            }
      }

      return var3;
   }

   protected Vector updateParentClassMethods(ClassDefinition var1, Vector var2, boolean var3, ContextStack var4) throws ClassNotFound {
      ClassDefinition var6;
      for(ClassDeclaration var5 = var1.getSuperClass(this.env); var5 != null; var5 = var6.getSuperClass(this.env)) {
         var6 = var5.getClassDefinition(this.env);
         Identifier var7 = var5.getName();
         if (var7 == idJavaLangObject) {
            break;
         }

         for(MemberDefinition var8 = var6.getFirstMember(); var8 != null; var8 = var8.getNextMember()) {
            if (var8.isMethod() && !var8.isInitializer() && !var8.isConstructor() && !var8.isPrivate()) {
               Method var9;
               try {
                  var9 = new Method(this, var8, var3, var4);
               } catch (Exception var12) {
                  return null;
               }

               int var10 = var2.indexOf(var9);
               if (var10 >= 0) {
                  Method var11 = (Method)var2.elementAt(var10);
                  var11.setDeclaredBy(var7);
               } else {
                  var2.addElement(var9);
               }
            }
         }
      }

      return var2;
   }

   protected Vector addAllMethods(ClassDefinition var1, Vector var2, boolean var3, boolean var4, ContextStack var5) throws ClassNotFound {
      ClassDeclaration[] var6 = var1.getInterfaces();

      for(int var7 = 0; var7 < var6.length; ++var7) {
         Vector var8 = this.addAllMethods(var6[var7].getClassDefinition(this.env), var2, var3, var4, var5);
         if (var8 == null) {
            return null;
         }
      }

      for(MemberDefinition var13 = var1.getFirstMember(); var13 != null; var13 = var13.getNextMember()) {
         if (var13.isMethod() && !var13.isInitializer() && !var13.isPrivate()) {
            Method var14;
            try {
               var14 = new Method(this, var13, var4, var5);
            } catch (Exception var12) {
               return null;
            }

            if (!var2.contains(var14)) {
               var2.addElement(var14);
            } else {
               if (var3 && var1 != this.classDef && !var5.isParentAValue() && !var5.getContext().isValue()) {
                  Method var9 = (Method)var2.elementAt(var2.indexOf(var14));
                  ClassDefinition var10 = var9.getMemberDefinition().getClassDefinition();
                  if (var1 != var10 && !this.inheritsFrom(var1, var10) && !this.inheritsFrom(var10, var1)) {
                     String var17 = var10.getName() + " and " + var1.getName();
                     failedConstraint(6, var4, var5, this.classDef, var17, var14);
                     return null;
                  }
               }

               int var15 = var2.indexOf(var14);
               Method var16 = (Method)var2.get(var15);
               Method var11 = var14.mergeWith(var16);
               var2.set(var15, var11);
            }
         }
      }

      return var2;
   }

   protected boolean inheritsFrom(ClassDefinition var1, ClassDefinition var2) {
      if (var1 == var2) {
         return true;
      } else {
         ClassDefinition var3;
         if (var1.getSuperClass() != null) {
            var3 = var1.getSuperClass().getClassDefinition();
            if (this.inheritsFrom(var3, var2)) {
               return true;
            }
         }

         ClassDeclaration[] var4 = var1.getInterfaces();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var3 = var4[var5].getClassDefinition();
            if (this.inheritsFrom(var3, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   protected Vector addRemoteInterfaces(Vector var1, boolean var2, ContextStack var3) throws ClassNotFound {
      ClassDefinition var4 = this.getClassDefinition();
      ClassDeclaration[] var5 = var4.getInterfaces();
      var3.setNewContextCode(10);

      for(int var6 = 0; var6 < var5.length; ++var6) {
         ClassDefinition var7 = var5[var6].getClassDefinition(this.env);
         Object var8 = SpecialInterfaceType.forSpecial(var7, var3);
         if (var8 == null) {
            if (this.env.defRemote.implementedBy(this.env, var5[var6])) {
               var8 = RemoteType.forRemote(var7, var3, false);
            } else {
               var8 = AbstractType.forAbstract(var7, var3, true);
               if (var8 == null && var2) {
                  var8 = NCInterfaceType.forNCInterface(var7, var3);
               }
            }
         }

         if (var8 == null) {
            return null;
         }

         var1.addElement(var8);
      }

      return var1;
   }

   protected Vector addNonRemoteInterfaces(Vector var1, ContextStack var2) throws ClassNotFound {
      ClassDefinition var3 = this.getClassDefinition();
      ClassDeclaration[] var4 = var3.getInterfaces();
      var2.setNewContextCode(10);

      for(int var5 = 0; var5 < var4.length; ++var5) {
         ClassDefinition var6 = var4[var5].getClassDefinition(this.env);
         Object var7 = SpecialInterfaceType.forSpecial(var6, var2);
         if (var7 == null) {
            var7 = AbstractType.forAbstract(var6, var2, true);
            if (var7 == null) {
               var7 = NCInterfaceType.forNCInterface(var6, var2);
            }
         }

         if (var7 == null) {
            return null;
         }

         var1.addElement(var7);
      }

      return var1;
   }

   protected boolean addAllMembers(Vector var1, boolean var2, boolean var3, ContextStack var4) {
      boolean var5 = true;

      for(MemberDefinition var6 = this.getClassDefinition().getFirstMember(); var6 != null && var5; var6 = var6.getNextMember()) {
         if (!var6.isMethod()) {
            try {
               String var7 = null;
               var6.getValue(this.env);
               Node var8 = var6.getValue();
               if (var8 != null) {
                  if (var6.getType().getTypeCode() == 2) {
                     Integer var9 = (Integer)((IntegerExpression)var8).getValue();
                     var7 = "L'" + String.valueOf((char)var9) + "'";
                  } else {
                     var7 = var8.toString();
                  }
               }

               if (var2 && var6.getInnerClass() == null && (var7 == null || !this.isConformingConstantType(var6))) {
                  failedConstraint(3, var3, var4, var6.getClassDefinition(), var6.getName());
                  var5 = false;
                  break;
               }

               try {
                  Member var12 = new Member(var6, var7, var4, this);
                  var1.addElement(var12);
               } catch (CompilerError var10) {
                  var5 = false;
               }
            } catch (ClassNotFound var11) {
               classNotFound(var4, var11);
               var5 = false;
            }
         }
      }

      return var5;
   }

   protected boolean addConformingConstants(Vector var1, boolean var2, ContextStack var3) {
      boolean var4 = true;

      for(MemberDefinition var5 = this.getClassDefinition().getFirstMember(); var5 != null && var4; var5 = var5.getNextMember()) {
         if (!var5.isMethod()) {
            try {
               String var6 = null;
               var5.getValue(this.env);
               Node var7 = var5.getValue();
               if (var7 != null) {
                  var6 = var7.toString();
               }

               if (var6 != null) {
                  if (!this.isConformingConstantType(var5)) {
                     failedConstraint(3, var2, var3, var5.getClassDefinition(), var5.getName());
                     var4 = false;
                     break;
                  }

                  try {
                     Member var8 = new Member(var5, var6, var3, this);
                     var1.addElement(var8);
                  } catch (CompilerError var9) {
                     var4 = false;
                  }
               }
            } catch (ClassNotFound var10) {
               classNotFound(var3, var10);
               var4 = false;
            }
         }
      }

      return var4;
   }

   protected ValueType[] getMethodExceptions(MemberDefinition var1, boolean var2, ContextStack var3) throws Exception {
      boolean var4 = true;
      var3.setNewContextCode(5);
      ClassDeclaration[] var5 = var1.getExceptions(this.env);
      ValueType[] var6 = new ValueType[var5.length];

      int var7;
      try {
         for(var7 = 0; var7 < var5.length; ++var7) {
            ClassDefinition var8 = var5[var7].getClassDefinition(this.env);

            try {
               ValueType var9 = ValueType.forValue(var8, var3, false);
               if (var9 != null) {
                  var6[var7] = var9;
               } else {
                  var4 = false;
               }
            } catch (ClassCastException var11) {
               failedConstraint(22, var2, var3, this.getQualifiedName());
               throw new CompilerError("Method: exception " + var8.getName() + " not a class type!");
            } catch (NullPointerException var12) {
               failedConstraint(23, var2, var3, this.getQualifiedName());
               throw new CompilerError("Method: caught null pointer exception");
            }
         }
      } catch (ClassNotFound var13) {
         classNotFound(var2, var3, var13);
         var4 = false;
      }

      if (!var4) {
         throw new Exception();
      } else {
         var7 = 0;

         int var14;
         for(var14 = 0; var14 < var6.length; ++var14) {
            for(int var15 = 0; var15 < var6.length; ++var15) {
               if (var14 != var15 && var6[var14] != null && var6[var14] == var6[var15]) {
                  var6[var15] = null;
                  ++var7;
               }
            }
         }

         if (var7 > 0) {
            var14 = 0;
            ValueType[] var16 = new ValueType[var6.length - var7];

            for(int var10 = 0; var10 < var6.length; ++var10) {
               if (var6[var10] != null) {
                  var16[var14++] = var6[var10];
               }
            }

            var6 = var16;
         }

         return var6;
      }
   }

   protected static String getVisibilityString(MemberDefinition var0) {
      String var1 = "";
      String var2 = "";
      if (var0.isPublic()) {
         var1 = var1 + "public";
         var2 = " ";
      } else if (var0.isProtected()) {
         var1 = var1 + "protected";
         var2 = " ";
      } else if (var0.isPrivate()) {
         var1 = var1 + "private";
         var2 = " ";
      }

      if (var0.isStatic()) {
         var1 = var1 + var2;
         var1 = var1 + "static";
         var2 = " ";
      }

      if (var0.isFinal()) {
         var1 = var1 + var2;
         var1 = var1 + "final";
         var2 = " ";
      }

      return var1;
   }

   protected boolean assertNotImpl(Type var1, boolean var2, ContextStack var3, CompoundType var4, boolean var5) {
      if (var1.isType(65536)) {
         int var6 = var5 ? 28 : 21;
         failedConstraint(var6, var2, var3, var1, var4.getName());
         return false;
      } else {
         return true;
      }
   }

   public class Member implements ContextElement, Cloneable {
      private Type type;
      private String vis;
      private String value;
      private String name;
      private String idlName;
      private boolean innerClassDecl;
      private boolean constant;
      private MemberDefinition member;
      private boolean forceTransient;

      public String getElementName() {
         return "\"" + this.getName() + "\"";
      }

      public Type getType() {
         return this.type;
      }

      public String getName() {
         return this.name;
      }

      public String getIDLName() {
         return this.idlName;
      }

      public String getVisibility() {
         return this.vis;
      }

      public boolean isPublic() {
         return this.member.isPublic();
      }

      public boolean isPrivate() {
         return this.member.isPrivate();
      }

      public boolean isStatic() {
         return this.member.isStatic();
      }

      public boolean isFinal() {
         return this.member.isFinal();
      }

      public boolean isTransient() {
         return this.forceTransient ? true : this.member.isTransient();
      }

      public String getValue() {
         return this.value;
      }

      public boolean isInnerClassDeclaration() {
         return this.innerClassDecl;
      }

      public boolean isConstant() {
         return this.constant;
      }

      public String toString() {
         String var1 = this.type.toString();
         if (this.value != null) {
            var1 = var1 + " = " + this.value;
         }

         return var1;
      }

      protected void swapInvalidTypes() {
         if (this.type.getStatus() != 1) {
            this.type = CompoundType.this.getValidType(this.type);
         }

      }

      protected void setTransient() {
         if (!this.isTransient()) {
            this.forceTransient = true;
            if (this.vis.length() > 0) {
               this.vis = this.vis + " transient";
            } else {
               this.vis = "transient";
            }
         }

      }

      protected MemberDefinition getMemberDefinition() {
         return this.member;
      }

      public void destroy() {
         if (this.type != null) {
            this.type.destroy();
            this.type = null;
            this.vis = null;
            this.value = null;
            this.name = null;
            this.idlName = null;
            this.member = null;
         }

      }

      public Member(MemberDefinition var2, String var3, ContextStack var4, CompoundType var5) {
         this.member = var2;
         this.value = var3;
         this.forceTransient = false;
         this.innerClassDecl = var2.getInnerClass() != null;
         if (!this.innerClassDecl) {
            this.init(var4, var5);
         }

      }

      public void init(ContextStack var1, CompoundType var2) {
         this.constant = false;
         this.name = this.member.getName().toString();
         this.vis = CompoundType.getVisibilityString(this.member);
         this.idlName = null;
         byte var3 = 6;
         var1.setNewContextCode(var3);
         if (this.member.isVariable()) {
            if (this.value != null && this.member.isConstant()) {
               var3 = 7;
               this.constant = true;
            } else if (this.member.isStatic()) {
               var3 = 8;
            } else if (this.member.isTransient()) {
               var3 = 9;
            }
         }

         var1.setNewContextCode(var3);
         var1.push(this);
         this.type = CompoundType.makeType(this.member.getType(), (ClassDefinition)null, var1);
         if (this.type == null || !this.innerClassDecl && !this.member.isStatic() && !this.member.isTransient() && !CompoundType.this.assertNotImpl(this.type, false, var1, var2, true)) {
            var1.pop(false);
            throw new CompilerError("");
         } else {
            if (this.constant && this.type.isPrimitive()) {
               if (!this.type.isType(64) && !this.type.isType(128) && !this.type.isType(256)) {
                  if (this.type.isType(2)) {
                     this.value = this.value.toUpperCase();
                  }
               } else {
                  int var4 = this.value.length();
                  char var5 = this.value.charAt(var4 - 1);
                  if (!Character.isDigit(var5)) {
                     this.value = this.value.substring(0, var4 - 1);
                  }
               }
            }

            if (this.constant && this.type.isType(512)) {
               this.value = "L" + this.value;
            }

            var1.pop(true);
         }
      }

      public void setIDLName(String var1) {
         this.idlName = var1;
      }

      protected Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new Error("clone failed");
         }
      }
   }

   public class Method implements ContextElement, Cloneable {
      private MemberDefinition memberDef;
      private CompoundType enclosing;
      private ValueType[] exceptions;
      private ValueType[] implExceptions;
      private Type returnType;
      private Type[] arguments;
      private String[] argumentNames;
      private String vis;
      private String name;
      private String idlName;
      private String stringRep = null;
      private int attributeKind = 0;
      private String attributeName = null;
      private int attributePairIndex = -1;
      private Identifier declaredBy = null;

      public boolean isInherited() {
         return this.declaredBy != this.enclosing.getIdentifier();
      }

      public boolean isAttribute() {
         return this.attributeKind != 0;
      }

      public boolean isReadWriteAttribute() {
         return this.attributeKind == 3 || this.attributeKind == 4;
      }

      public int getAttributeKind() {
         return this.attributeKind;
      }

      public String getAttributeName() {
         return this.attributeName;
      }

      public int getAttributePairIndex() {
         return this.attributePairIndex;
      }

      public String getElementName() {
         return this.memberDef.toString();
      }

      public boolean equals(Object var1) {
         Method var2 = (Method)var1;
         if (this.getName().equals(var2.getName()) && this.arguments.length == var2.arguments.length) {
            for(int var3 = 0; var3 < this.arguments.length; ++var3) {
               if (!this.arguments[var3].equals(var2.arguments[var3])) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.getName().hashCode() ^ Arrays.hashCode(this.arguments);
      }

      public Method mergeWith(Method var1) {
         if (!this.equals(var1)) {
            CompoundType.this.env.error(0L, "attempt to merge method failed:", this.getName(), this.enclosing.getClassDefinition().getName());
         }

         Vector var2 = new Vector();

         try {
            this.collectCompatibleExceptions(var1.exceptions, this.exceptions, var2);
            this.collectCompatibleExceptions(this.exceptions, var1.exceptions, var2);
         } catch (ClassNotFound var4) {
            CompoundType.this.env.error(0L, "class.not.found", var4.name, this.enclosing.getClassDefinition().getName());
            return null;
         }

         Method var3 = (Method)this.clone();
         var3.exceptions = new ValueType[var2.size()];
         var2.copyInto(var3.exceptions);
         var3.implExceptions = var3.exceptions;
         return var3;
      }

      private void collectCompatibleExceptions(ValueType[] var1, ValueType[] var2, Vector var3) throws ClassNotFound {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            ClassDefinition var5 = var1[var4].getClassDefinition();
            if (!var3.contains(var1[var4])) {
               for(int var6 = 0; var6 < var2.length; ++var6) {
                  if (var5.subClassOf(this.enclosing.getEnv(), var2[var6].getClassDeclaration())) {
                     var3.addElement(var1[var4]);
                     break;
                  }
               }
            }
         }

      }

      public CompoundType getEnclosing() {
         return this.enclosing;
      }

      public Identifier getDeclaredBy() {
         return this.declaredBy;
      }

      public String getVisibility() {
         return this.vis;
      }

      public boolean isPublic() {
         return this.memberDef.isPublic();
      }

      public boolean isProtected() {
         return this.memberDef.isPrivate();
      }

      public boolean isPrivate() {
         return this.memberDef.isPrivate();
      }

      public boolean isStatic() {
         return this.memberDef.isStatic();
      }

      public String getName() {
         return this.name;
      }

      public String getIDLName() {
         return this.idlName;
      }

      public sun.tools.java.Type getType() {
         return this.memberDef.getType();
      }

      public boolean isConstructor() {
         return this.memberDef.isConstructor();
      }

      public boolean isNormalMethod() {
         return !this.memberDef.isConstructor() && this.attributeKind == 0;
      }

      public Type getReturnType() {
         return this.returnType;
      }

      public Type[] getArguments() {
         return (Type[])((Type[])this.arguments.clone());
      }

      public String[] getArgumentNames() {
         return this.argumentNames;
      }

      public MemberDefinition getMemberDefinition() {
         return this.memberDef;
      }

      public ValueType[] getExceptions() {
         return (ValueType[])((ValueType[])this.exceptions.clone());
      }

      public ValueType[] getImplExceptions() {
         return (ValueType[])((ValueType[])this.implExceptions.clone());
      }

      public ValueType[] getUniqueCatchList(ValueType[] var1) {
         int var3 = var1.length;

         int var6;
         try {
            int var4;
            for(var4 = 0; var4 < var1.length; ++var4) {
               ClassDeclaration var5 = var1[var4].getClassDeclaration();
               if (CompoundType.this.env.defRemoteException.superClassOf(CompoundType.this.env, var5) || CompoundType.this.env.defRuntimeException.superClassOf(CompoundType.this.env, var5) || CompoundType.this.env.defError.superClassOf(CompoundType.this.env, var5)) {
                  var1[var4] = null;
                  --var3;
               }
            }

            for(var4 = 0; var4 < var1.length; ++var4) {
               if (var1[var4] != null) {
                  ClassDefinition var9 = var1[var4].getClassDefinition();

                  for(var6 = 0; var6 < var1.length; ++var6) {
                     if (var6 != var4 && var1[var4] != null && var1[var6] != null && var9.superClassOf(CompoundType.this.env, var1[var6].getClassDeclaration())) {
                        var1[var6] = null;
                        --var3;
                     }
                  }
               }
            }
         } catch (ClassNotFound var7) {
            Type.classNotFound(CompoundType.this.stack, var7);
         }

         if (var3 < var1.length) {
            ValueType[] var8 = new ValueType[var3];
            int var10 = 0;

            for(var6 = 0; var6 < var1.length; ++var6) {
               if (var1[var6] != null) {
                  var8[var10++] = var1[var6];
               }
            }

            var1 = var8;
         }

         return var1.length == 0 ? null : var1;
      }

      public ValueType[] getFilteredStubExceptions(ValueType[] var1) {
         int var3 = var1.length;

         try {
            for(int var4 = 0; var4 < var1.length; ++var4) {
               ClassDeclaration var5 = var1[var4].getClassDeclaration();
               if (CompoundType.this.env.defRemoteException.superClassOf(CompoundType.this.env, var5) && !CompoundType.this.env.defRemoteException.getClassDeclaration().equals(var5) || CompoundType.this.env.defRuntimeException.superClassOf(CompoundType.this.env, var5) || CompoundType.this.env.defError.superClassOf(CompoundType.this.env, var5)) {
                  var1[var4] = null;
                  --var3;
               }
            }
         } catch (ClassNotFound var7) {
            Type.classNotFound(CompoundType.this.stack, var7);
         }

         if (var3 < var1.length) {
            ValueType[] var8 = new ValueType[var3];
            int var9 = 0;

            for(int var6 = 0; var6 < var1.length; ++var6) {
               if (var1[var6] != null) {
                  var8[var9++] = var1[var6];
               }
            }

            var1 = var8;
         }

         return var1;
      }

      public String toString() {
         if (this.stringRep == null) {
            StringBuffer var1 = new StringBuffer(this.returnType.toString());
            var1.append(" ");
            var1.append(this.getName());
            var1.append(" (");

            int var2;
            for(var2 = 0; var2 < this.arguments.length; ++var2) {
               if (var2 > 0) {
                  var1.append(", ");
               }

               var1.append(this.arguments[var2]);
               var1.append(" ");
               var1.append(this.argumentNames[var2]);
            }

            var1.append(")");

            for(var2 = 0; var2 < this.exceptions.length; ++var2) {
               if (var2 == 0) {
                  var1.append(" throws ");
               } else {
                  var1.append(", ");
               }

               var1.append(this.exceptions[var2]);
            }

            var1.append(";");
            this.stringRep = var1.toString();
         }

         return this.stringRep;
      }

      public void setAttributeKind(int var1) {
         this.attributeKind = var1;
      }

      public void setAttributePairIndex(int var1) {
         this.attributePairIndex = var1;
      }

      public void setAttributeName(String var1) {
         this.attributeName = var1;
      }

      public void setIDLName(String var1) {
         this.idlName = var1;
      }

      public void setImplExceptions(ValueType[] var1) {
         this.implExceptions = var1;
      }

      public void setDeclaredBy(Identifier var1) {
         this.declaredBy = var1;
      }

      protected void swapInvalidTypes() {
         if (this.returnType.getStatus() != 1) {
            this.returnType = CompoundType.this.getValidType(this.returnType);
         }

         int var1;
         for(var1 = 0; var1 < this.arguments.length; ++var1) {
            if (this.arguments[var1].getStatus() != 1) {
               this.arguments[var1] = CompoundType.this.getValidType(this.arguments[var1]);
            }
         }

         for(var1 = 0; var1 < this.exceptions.length; ++var1) {
            if (this.exceptions[var1].getStatus() != 1) {
               this.exceptions[var1] = (ValueType)CompoundType.this.getValidType(this.exceptions[var1]);
            }
         }

         for(var1 = 0; var1 < this.implExceptions.length; ++var1) {
            if (this.implExceptions[var1].getStatus() != 1) {
               this.implExceptions[var1] = (ValueType)CompoundType.this.getValidType(this.implExceptions[var1]);
            }
         }

      }

      public void destroy() {
         if (this.memberDef != null) {
            this.memberDef = null;
            this.enclosing = null;
            int var1;
            if (this.exceptions != null) {
               for(var1 = 0; var1 < this.exceptions.length; ++var1) {
                  if (this.exceptions[var1] != null) {
                     this.exceptions[var1].destroy();
                  }

                  this.exceptions[var1] = null;
               }

               this.exceptions = null;
            }

            if (this.implExceptions != null) {
               for(var1 = 0; var1 < this.implExceptions.length; ++var1) {
                  if (this.implExceptions[var1] != null) {
                     this.implExceptions[var1].destroy();
                  }

                  this.implExceptions[var1] = null;
               }

               this.implExceptions = null;
            }

            if (this.returnType != null) {
               this.returnType.destroy();
            }

            this.returnType = null;
            if (this.arguments != null) {
               for(var1 = 0; var1 < this.arguments.length; ++var1) {
                  if (this.arguments[var1] != null) {
                     this.arguments[var1].destroy();
                  }

                  this.arguments[var1] = null;
               }

               this.arguments = null;
            }

            if (this.argumentNames != null) {
               for(var1 = 0; var1 < this.argumentNames.length; ++var1) {
                  this.argumentNames[var1] = null;
               }

               this.argumentNames = null;
            }

            this.vis = null;
            this.name = null;
            this.idlName = null;
            this.stringRep = null;
            this.attributeName = null;
            this.declaredBy = null;
         }

      }

      private String makeArgName(int var1, Type var2) {
         return "arg" + var1;
      }

      public Method(CompoundType var2, MemberDefinition var3, boolean var4, ContextStack var5) throws Exception {
         this.enclosing = var2;
         this.memberDef = var3;
         this.vis = CompoundType.getVisibilityString(var3);
         this.idlName = null;
         boolean var6 = true;
         this.declaredBy = var3.getClassDeclaration().getName();
         this.name = var3.getName().toString();
         var5.setNewContextCode(2);
         var5.push(this);
         var5.setNewContextCode(3);
         sun.tools.java.Type var7 = var3.getType();
         sun.tools.java.Type var8 = var7.getReturnType();
         if (var8 == sun.tools.java.Type.tVoid) {
            this.returnType = PrimitiveType.forPrimitive(var8, var5);
         } else {
            this.returnType = CompoundType.makeType(var8, (ClassDefinition)null, var5);
            if (this.returnType == null || !CompoundType.this.assertNotImpl(this.returnType, var4, var5, var2, false)) {
               var6 = false;
               Type.failedConstraint(24, var4, var5, var2.getName());
            }
         }

         var5.setNewContextCode(4);
         sun.tools.java.Type[] var9 = var3.getType().getArgumentTypes();
         this.arguments = new Type[var9.length];
         this.argumentNames = new String[var9.length];
         Vector var10 = var3.getArguments();

         for(int var11 = 0; var11 < var9.length; ++var11) {
            Type var12 = null;

            try {
               var12 = CompoundType.makeType(var9[var11], (ClassDefinition)null, var5);
            } catch (Exception var15) {
            }

            if (var12 != null) {
               if (!CompoundType.this.assertNotImpl(var12, var4, var5, var2, false)) {
                  var6 = false;
               } else {
                  this.arguments[var11] = var12;
                  if (var10 != null) {
                     LocalMember var13 = (LocalMember)var10.elementAt(var11 + 1);
                     this.argumentNames[var11] = var13.getName().toString();
                  } else {
                     this.argumentNames[var11] = this.makeArgName(var11, var12);
                  }
               }
            } else {
               var6 = false;
               Type.failedConstraint(25, false, var5, var2.getQualifiedName(), this.name);
            }
         }

         if (!var6) {
            var5.pop(false);
            throw new Exception();
         } else {
            try {
               this.exceptions = var2.getMethodExceptions(var3, var4, var5);
               this.implExceptions = this.exceptions;
               var5.pop(true);
            } catch (Exception var14) {
               var5.pop(false);
               throw new Exception();
            }
         }
      }

      protected Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new Error("clone failed");
         }
      }
   }
}
