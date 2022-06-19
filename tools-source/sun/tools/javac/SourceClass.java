package sun.tools.javac;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import sun.tools.asm.Assembler;
import sun.tools.asm.ConstantPool;
import sun.tools.java.AmbiguousClass;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.Imports;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;
import sun.tools.tree.AssignExpression;
import sun.tools.tree.CatchStatement;
import sun.tools.tree.CompoundStatement;
import sun.tools.tree.Context;
import sun.tools.tree.Expression;
import sun.tools.tree.ExpressionStatement;
import sun.tools.tree.FieldExpression;
import sun.tools.tree.IdentifierExpression;
import sun.tools.tree.LocalMember;
import sun.tools.tree.MethodExpression;
import sun.tools.tree.NewInstanceExpression;
import sun.tools.tree.Node;
import sun.tools.tree.ReturnStatement;
import sun.tools.tree.Statement;
import sun.tools.tree.StringExpression;
import sun.tools.tree.SuperExpression;
import sun.tools.tree.ThisExpression;
import sun.tools.tree.ThrowStatement;
import sun.tools.tree.TryStatement;
import sun.tools.tree.TypeExpression;
import sun.tools.tree.UplevelReference;
import sun.tools.tree.Vset;

/** @deprecated */
@Deprecated
public class SourceClass extends ClassDefinition {
   Environment toplevelEnv;
   SourceMember defConstructor;
   ConstantPool tab = new ConstantPool();
   Hashtable deps = new Hashtable(11);
   LocalMember thisArg;
   long endPosition;
   private Type dummyArgumentType = null;
   private boolean sourceFileChecked = false;
   private boolean supersChecked = false;
   private boolean basicChecking = false;
   private boolean basicCheckDone = false;
   private boolean resolving = false;
   private boolean inlinedLocalClass = false;
   private static int[] classModifierBits = new int[]{1, 2, 4, 8, 16, 512, 1024, 32, 65536, 131072, 2097152, 2048};
   private static String[] classModifierNames = new String[]{"PUBLIC", "PRIVATE", "PROTECTED", "STATIC", "FINAL", "INTERFACE", "ABSTRACT", "SUPER", "ANONYMOUS", "LOCAL", "STRICTFP", "STRICT"};
   private MemberDefinition lookup = null;
   private static Vector active = new Vector();

   public SourceClass(Environment var1, long var2, ClassDeclaration var4, String var5, int var6, IdentifierToken var7, IdentifierToken[] var8, SourceClass var9, Identifier var10) {
      super(var1.getSource(), var2, var4, var6, var7, var8);
      this.setOuterClass(var9);
      this.toplevelEnv = var1;
      this.documentation = var5;
      if (ClassDefinition.containsDeprecated(var5)) {
         this.modifiers |= 262144;
      }

      if (this.isStatic() && var9 == null) {
         var1.error(var2, "static.class", this);
         this.modifiers &= -9;
      }

      if (this.isLocal() || var9 != null && !var9.isTopLevel()) {
         if (this.isInterface()) {
            var1.error(var2, "inner.interface");
         } else if (this.isStatic()) {
            var1.error(var2, "static.inner.class", this);
            this.modifiers &= -9;
            if (this.innerClassMember != null) {
               this.innerClassMember.subModifiers(8);
            }
         }
      }

      if (this.isPrivate() && var9 == null) {
         var1.error(var2, "private.class", this);
         this.modifiers &= -3;
      }

      if (this.isProtected() && var9 == null) {
         var1.error(var2, "protected.class", this);
         this.modifiers &= -5;
      }

      if (!this.isTopLevel() && !this.isLocal()) {
         LocalMember var11 = var9.getThisArgument();
         UplevelReference var12 = this.getReference(var11);
         this.setOuterMember(var12.getLocalField(var1));
      }

      if (var10 != null) {
         this.setLocalName(var10);
      }

      Identifier var14 = this.getLocalName();
      if (var14 != idNull) {
         for(Object var15 = var9; var15 != null; var15 = ((ClassDefinition)var15).getOuterClass()) {
            Identifier var13 = ((ClassDefinition)var15).getLocalName();
            if (var14.equals(var13)) {
               var1.error(var2, "inner.redefined", var14);
            }
         }
      }

   }

   public long getEndPosition() {
      return this.endPosition;
   }

   public void setEndPosition(long var1) {
      this.endPosition = var1;
   }

   public String getAbsoluteName() {
      String var1 = ((ClassFile)this.getSource()).getAbsoluteName();
      return var1;
   }

   public Imports getImports() {
      return this.toplevelEnv.getImports();
   }

   public LocalMember getThisArgument() {
      if (this.thisArg == null) {
         this.thisArg = new LocalMember(this.where, this, 0, this.getType(), idThis);
      }

      return this.thisArg;
   }

   public void addDependency(ClassDeclaration var1) {
      if (this.tab != null) {
         this.tab.put(var1);
      }

      if (this.toplevelEnv.print_dependencies() && var1 != this.getClassDeclaration()) {
         this.deps.put(var1, var1);
      }

   }

   public void addMember(Environment var1, MemberDefinition var2) {
      switch (var2.getModifiers() & 7) {
         case 0:
         case 1:
         case 2:
         case 4:
            break;
         case 3:
         default:
            var1.error(var2.getWhere(), "inconsistent.modifier", var2);
            if (var2.isPublic()) {
               var2.subModifiers(6);
            } else {
               var2.subModifiers(2);
            }
      }

      if (var2.isStatic() && !this.isTopLevel() && !var2.isSynthetic()) {
         if (var2.isMethod()) {
            var1.error(var2.getWhere(), "static.inner.method", var2, this);
            var2.subModifiers(8);
         } else if (var2.isVariable()) {
            if (!var2.isFinal() || var2.isBlankFinal()) {
               var1.error(var2.getWhere(), "static.inner.field", var2.getName(), this);
               var2.subModifiers(8);
            }
         } else {
            var2.subModifiers(8);
         }
      }

      if (var2.isMethod()) {
         if (var2.isConstructor()) {
            if (var2.getClassDefinition().isInterface()) {
               var1.error(var2.getWhere(), "intf.constructor");
               return;
            }

            if (var2.isNative() || var2.isAbstract() || var2.isStatic() || var2.isSynchronized() || var2.isFinal()) {
               var1.error(var2.getWhere(), "constr.modifier", var2);
               var2.subModifiers(1336);
            }
         } else if (var2.isInitializer() && var2.getClassDefinition().isInterface()) {
            var1.error(var2.getWhere(), "intf.initializer");
            return;
         }

         if (var2.getType().getReturnType().isVoidArray()) {
            var1.error(var2.getWhere(), "void.array");
         }

         if (var2.getClassDefinition().isInterface() && (var2.isStatic() || var2.isSynchronized() || var2.isNative() || var2.isFinal() || var2.isPrivate() || var2.isProtected())) {
            var1.error(var2.getWhere(), "intf.modifier.method", var2);
            var2.subModifiers(314);
         }

         if (var2.isTransient()) {
            var1.error(var2.getWhere(), "transient.meth", var2);
            var2.subModifiers(128);
         }

         if (var2.isVolatile()) {
            var1.error(var2.getWhere(), "volatile.meth", var2);
            var2.subModifiers(64);
         }

         if (var2.isAbstract()) {
            if (var2.isPrivate()) {
               var1.error(var2.getWhere(), "abstract.private.modifier", var2);
               var2.subModifiers(2);
            }

            if (var2.isStatic()) {
               var1.error(var2.getWhere(), "abstract.static.modifier", var2);
               var2.subModifiers(8);
            }

            if (var2.isFinal()) {
               var1.error(var2.getWhere(), "abstract.final.modifier", var2);
               var2.subModifiers(16);
            }

            if (var2.isNative()) {
               var1.error(var2.getWhere(), "abstract.native.modifier", var2);
               var2.subModifiers(256);
            }

            if (var2.isSynchronized()) {
               var1.error(var2.getWhere(), "abstract.synchronized.modifier", var2);
               var2.subModifiers(32);
            }
         }

         if (!var2.isAbstract() && !var2.isNative()) {
            if (var2.getValue() == null) {
               if (var2.isConstructor()) {
                  var1.error(var2.getWhere(), "no.constructor.body", var2);
               } else {
                  var1.error(var2.getWhere(), "no.meth.body", var2);
               }

               var2.addModifiers(1024);
            }
         } else if (var2.getValue() != null) {
            var1.error(var2.getWhere(), "invalid.meth.body", var2);
            var2.setValue((Node)null);
         }

         Vector var3 = var2.getArguments();
         if (var3 != null) {
            int var4 = var3.size();
            Type[] var5 = var2.getType().getArgumentTypes();

            for(int var6 = 0; var6 < var5.length; ++var6) {
               Object var7 = var3.elementAt(var6);
               long var8 = var2.getWhere();
               if (var7 instanceof MemberDefinition) {
                  var8 = ((MemberDefinition)var7).getWhere();
                  var7 = ((MemberDefinition)var7).getName();
               }

               if (var5[var6].isType(11) || var5[var6].isVoidArray()) {
                  var1.error(var8, "void.argument", var7);
               }
            }
         }
      } else if (var2.isInnerClass()) {
         if (var2.isVolatile() || var2.isTransient() || var2.isNative() || var2.isSynchronized()) {
            var1.error(var2.getWhere(), "inner.modifier", var2);
            var2.subModifiers(480);
         }

         if (var2.getClassDefinition().isInterface() && (var2.isPrivate() || var2.isProtected())) {
            var1.error(var2.getWhere(), "intf.modifier.field", var2);
            var2.subModifiers(6);
            var2.addModifiers(1);
            ClassDefinition var10 = var2.getInnerClass();
            var10.subModifiers(6);
            var10.addModifiers(1);
         }
      } else {
         if (var2.getType().isType(11) || var2.getType().isVoidArray()) {
            var1.error(var2.getWhere(), "void.inst.var", var2.getName());
            return;
         }

         if (var2.isSynchronized() || var2.isAbstract() || var2.isNative()) {
            var1.error(var2.getWhere(), "var.modifier", var2);
            var2.subModifiers(1312);
         }

         if (var2.isStrict()) {
            var1.error(var2.getWhere(), "var.floatmodifier", var2);
            var2.subModifiers(2097152);
         }

         if (var2.isTransient() && this.isInterface()) {
            var1.error(var2.getWhere(), "transient.modifier", var2);
            var2.subModifiers(128);
         }

         if (var2.isVolatile() && (this.isInterface() || var2.isFinal())) {
            var1.error(var2.getWhere(), "volatile.modifier", var2);
            var2.subModifiers(64);
         }

         if (var2.isFinal() && var2.getValue() == null && this.isInterface()) {
            var1.error(var2.getWhere(), "initializer.needed", var2);
            var2.subModifiers(16);
         }

         if (var2.getClassDefinition().isInterface() && (var2.isPrivate() || var2.isProtected())) {
            var1.error(var2.getWhere(), "intf.modifier.field", var2);
            var2.subModifiers(6);
            var2.addModifiers(1);
         }
      }

      if (!var2.isInitializer()) {
         for(MemberDefinition var11 = this.getFirstMatch(var2.getName()); var11 != null; var11 = var11.getNextMatch()) {
            if (var2.isVariable() && var11.isVariable()) {
               var1.error(var2.getWhere(), "var.multidef", var2, var11);
               return;
            }

            if (var2.isInnerClass() && var11.isInnerClass() && !var2.getInnerClass().isLocal() && !var11.getInnerClass().isLocal()) {
               var1.error(var2.getWhere(), "inner.class.multidef", var2);
               return;
            }
         }
      }

      super.addMember(var1, var2);
   }

   public Environment setupEnv(Environment var1) {
      return new Environment(this.toplevelEnv, this);
   }

   public boolean reportDeprecated(Environment var1) {
      return false;
   }

   public void noteUsedBy(ClassDefinition var1, long var2, Environment var4) {
      super.noteUsedBy(var1, var2, var4);

      Object var5;
      for(var5 = this; ((ClassDefinition)var5).isInnerClass(); var5 = ((ClassDefinition)var5).getOuterClass()) {
      }

      if (!((ClassDefinition)var5).isPublic()) {
         while(var1.isInnerClass()) {
            var1 = var1.getOuterClass();
         }

         if (!((ClassDefinition)var5).getSource().equals(var1.getSource())) {
            ((SourceClass)var5).checkSourceFile(var4, var2);
         }
      }
   }

   public void check(Environment var1) throws ClassNotFound {
      var1.dtEnter("SourceClass.check: " + this.getName());
      if (this.isInsideLocal()) {
         var1.dtEvent("SourceClass.check: INSIDE LOCAL " + this.getOuterClass().getName());
         this.getOuterClass().check(var1);
      } else {
         if (this.isInnerClass()) {
            var1.dtEvent("SourceClass.check: INNER CLASS " + this.getOuterClass().getName());
            ((SourceClass)this.getOuterClass()).maybeCheck(var1);
         }

         Vset var2 = new Vset();
         Object var3 = null;
         var1.dtEvent("SourceClass.check: CHECK INTERNAL " + this.getName());
         this.checkInternal(this.setupEnv(var1), (Context)var3, var2);
      }

      var1.dtExit("SourceClass.check: " + this.getName());
   }

   private void maybeCheck(Environment var1) throws ClassNotFound {
      var1.dtEvent("SourceClass.maybeCheck: " + this.getName());
      ClassDeclaration var2 = this.getClassDeclaration();
      if (var2.getStatus() == 4) {
         var2.setDefinition(this, 5);
         this.check(var1);
      }

   }

   private Vset checkInternal(Environment var1, Context var2, Vset var3) throws ClassNotFound {
      Identifier var4 = this.getClassDeclaration().getName();
      if (var1.verbose()) {
         var1.output("[checking class " + var4 + "]");
      }

      this.classContext = var2;
      this.basicCheck(Context.newEnvironment(var1, var2));
      ClassDeclaration var5 = this.getSuperClass();
      if (var5 != null) {
         long var6 = this.getWhere();
         var6 = IdentifierToken.getWhere(this.superClassId, var6);
         var1.resolveExtendsByName(var6, this, var5.getName());
      }

      for(int var12 = 0; var12 < this.interfaces.length; ++var12) {
         ClassDeclaration var7 = this.interfaces[var12];
         long var8 = this.getWhere();
         if (this.interfaceIds != null && this.interfaceIds.length == this.interfaces.length) {
            var8 = IdentifierToken.getWhere(this.interfaceIds[var12], var8);
         }

         var1.resolveExtendsByName(var8, this, var7.getName());
      }

      if (!this.isInnerClass() && !this.isInsideLocal()) {
         Identifier var13 = var4.getName();

         Identifier var15;
         try {
            Imports var14 = this.toplevelEnv.getImports();
            var15 = var14.resolve(var1, var13);
            if (var15 != this.getName()) {
               var1.error(this.where, "class.multidef.import", var13, var15);
            }
         } catch (AmbiguousClass var10) {
            var15 = var10.name1 != this.getName() ? var10.name1 : var10.name2;
            var1.error(this.where, "class.multidef.import", var13, var15);
         } catch (ClassNotFound var11) {
         }

         if (this.isPublic()) {
            this.checkSourceFile(var1, this.getWhere());
         }
      }

      var3 = this.checkMembers(var1, var2, var3);
      return var3;
   }

   public void checkSourceFile(Environment var1, long var2) {
      if (!this.sourceFileChecked) {
         this.sourceFileChecked = true;
         String var4 = this.getName().getName() + ".java";
         String var5 = ((ClassFile)this.getSource()).getName();
         if (!var5.equals(var4)) {
            if (this.isPublic()) {
               var1.error(var2, "public.class.file", this, var4);
            } else {
               var1.error(var2, "warn.package.class.file", this, var5, var4);
            }
         }

      }
   }

   public ClassDeclaration getSuperClass(Environment var1) {
      var1.dtEnter("SourceClass.getSuperClass: " + this);
      if (this.superClass == null && this.superClassId != null && !this.supersChecked) {
         this.resolveTypeStructure(var1);
      }

      var1.dtExit("SourceClass.getSuperClass: " + this);
      return this.superClass;
   }

   private void checkSupers(Environment var1) throws ClassNotFound {
      this.supersCheckStarted = true;
      var1.dtEnter("SourceClass.checkSupers: " + this);
      if (this.isInterface()) {
         if (this.isFinal()) {
            Identifier var2 = this.getClassDeclaration().getName();
            var1.error(this.getWhere(), "final.intf", var2);
         }
      } else if (this.getSuperClass(var1) != null) {
         long var14 = this.getWhere();
         var14 = IdentifierToken.getWhere(this.superClassId, var14);

         ClassNotFound var4;
         try {
            ClassDefinition var16 = this.getSuperClass().getClassDefinition(var1);
            var16.resolveTypeStructure(var1);
            if (!this.extendsCanAccess(var1, this.getSuperClass())) {
               var1.error(var14, "cant.access.class", this.getSuperClass());
               this.superClass = null;
            } else if (var16.isFinal()) {
               var1.error(var14, "super.is.final", this.getSuperClass());
               this.superClass = null;
            } else if (var16.isInterface()) {
               var1.error(var14, "super.is.intf", this.getSuperClass());
               this.superClass = null;
            } else if (this.superClassOf(var1, this.getSuperClass())) {
               var1.error(var14, "cyclic.super");
               this.superClass = null;
            } else {
               var16.noteUsedBy(this, var14, var1);
            }

            if (this.superClass == null) {
               var4 = null;
            } else {
               ClassDefinition var5 = var16;

               while(true) {
                  if (this.enclosingClassOf(var5)) {
                     var1.error(var14, "super.is.inner");
                     this.superClass = null;
                     break;
                  }

                  ClassDeclaration var6 = var5.getSuperClass(var1);
                  if (var6 == null) {
                     break;
                  }

                  var5 = var6.getClassDefinition(var1);
               }
            }
         } catch (ClassNotFound var13) {
            label121: {
               var4 = var13;

               try {
                  var1.resolve(var4.name);
               } catch (AmbiguousClass var11) {
                  var1.error(var14, "ambig.class", var11.name1, var11.name2);
                  this.superClass = null;
                  break label121;
               } catch (ClassNotFound var12) {
               }

               var1.error(var14, "super.not.found", var13.name, this);
               this.superClass = null;
            }
         }
      } else {
         if (this.isAnonymous()) {
            throw new CompilerError("anonymous super");
         }

         if (!this.getName().equals(idJavaLangObject)) {
            throw new CompilerError("unresolved super");
         }
      }

      this.supersChecked = true;

      for(int var15 = 0; var15 < this.interfaces.length; ++var15) {
         ClassDeclaration var3 = this.interfaces[var15];
         long var17 = this.getWhere();
         if (this.interfaceIds != null && this.interfaceIds.length == this.interfaces.length) {
            var17 = IdentifierToken.getWhere(this.interfaceIds[var15], var17);
         }

         try {
            ClassDefinition var19 = var3.getClassDefinition(var1);
            var19.resolveTypeStructure(var1);
            if (!this.extendsCanAccess(var1, var3)) {
               var1.error(var17, "cant.access.class", var3);
            } else if (!var3.getClassDefinition(var1).isInterface()) {
               var1.error(var17, "not.intf", var3);
            } else {
               if (!this.isInterface() || !this.implementedBy(var1, var3)) {
                  var19.noteUsedBy(this, var17, var1);
                  continue;
               }

               var1.error(var17, "cyclic.intf", var3);
            }
         } catch (ClassNotFound var10) {
            label124: {
               ClassNotFound var18 = var10;

               try {
                  var1.resolve(var18.name);
               } catch (AmbiguousClass var8) {
                  var1.error(var17, "ambig.class", var8.name1, var8.name2);
                  this.superClass = null;
                  break label124;
               } catch (ClassNotFound var9) {
               }

               var1.error(var17, "intf.not.found", var10.name, this);
               this.superClass = null;
            }
         }

         ClassDeclaration[] var20 = new ClassDeclaration[this.interfaces.length - 1];
         System.arraycopy(this.interfaces, 0, var20, 0, var15);
         System.arraycopy(this.interfaces, var15 + 1, var20, var15, var20.length - var15);
         this.interfaces = var20;
         --var15;
      }

      var1.dtExit("SourceClass.checkSupers: " + this);
   }

   private Vset checkMembers(Environment var1, Context var2, Vset var3) throws ClassNotFound {
      if (this.getError()) {
         return var3;
      } else {
         for(MemberDefinition var4 = this.getFirstMember(); var4 != null; var4 = var4.getNextMember()) {
            if (var4.isInnerClass()) {
               SourceClass var5 = (SourceClass)var4.getInnerClass();
               if (var5.isMember()) {
                  var5.basicCheck(var1);
               }
            }
         }

         if (this.isFinal() && this.isAbstract()) {
            var1.error(this.where, "final.abstract", this.getName().getName());
         }

         if (!this.isInterface() && !this.isAbstract() && this.mustBeAbstract(var1)) {
            this.modifiers |= 1024;
            Iterator var14 = this.getPermanentlyAbstractMethods();

            MemberDefinition var16;
            while(var14.hasNext()) {
               var16 = (MemberDefinition)var14.next();
               var1.error(this.where, "abstract.class.cannot.override", this.getClassDeclaration(), var16, var16.getDefiningClassDeclaration());
            }

            var14 = this.getMethods(var1);

            while(var14.hasNext()) {
               var16 = (MemberDefinition)var14.next();
               if (var16.isAbstract()) {
                  var1.error(this.where, "abstract.class", this.getClassDeclaration(), var16, var16.getDefiningClassDeclaration());
               }
            }
         }

         Context var15 = new Context(var2);
         Vset var17 = var3.copy();
         Vset var6 = var3.copy();

         for(MemberDefinition var7 = this.getFirstMember(); var7 != null; var7 = var7.getNextMember()) {
            if (var7.isVariable() && var7.isBlankFinal()) {
               int var8 = var15.declareFieldNumber(var7);
               if (var7.isStatic()) {
                  var6 = var6.addVarUnassigned(var8);
                  var17 = var17.addVar(var8);
               } else {
                  var17 = var17.addVarUnassigned(var8);
                  var6 = var6.addVar(var8);
               }
            }
         }

         Context var18 = new Context(var15, this);
         LocalMember var19 = this.getThisArgument();
         int var9 = var18.declare(var1, var19);
         var17 = var17.addVar(var9);

         MemberDefinition var10;
         for(var10 = this.getFirstMember(); var10 != null; var10 = var10.getNextMember()) {
            try {
               if (var10.isVariable() || var10.isInitializer()) {
                  if (var10.isStatic()) {
                     var6 = var10.check(var1, var15, var6);
                  } else {
                     var17 = var10.check(var1, var18, var17);
                  }
               }
            } catch (ClassNotFound var13) {
               var1.error(var10.getWhere(), "class.not.found", var13.name, this);
            }
         }

         this.checkBlankFinals(var1, var15, var6, true);

         for(var10 = this.getFirstMember(); var10 != null; var10 = var10.getNextMember()) {
            try {
               if (var10.isConstructor()) {
                  Vset var11 = var10.check(var1, var15, var17.copy());
                  this.checkBlankFinals(var1, var15, var11, false);
               } else {
                  var10.check(var1, var2, var3.copy());
               }
            } catch (ClassNotFound var12) {
               var1.error(var10.getWhere(), "class.not.found", var12.name, this);
            }
         }

         this.getClassDeclaration().setDefinition(this, 5);

         for(var10 = this.getFirstMember(); var10 != null; var10 = var10.getNextMember()) {
            if (var10.isInnerClass()) {
               SourceClass var20 = (SourceClass)var10.getInnerClass();
               if (!var20.isInsideLocal()) {
                  var20.maybeCheck(var1);
               }
            }
         }

         return var3;
      }
   }

   private void checkBlankFinals(Environment var1, Context var2, Vset var3, boolean var4) {
      for(int var5 = 0; var5 < var2.getVarNumber(); ++var5) {
         if (!var3.testVar(var5)) {
            MemberDefinition var6 = var2.getElement(var5);
            if (var6 != null && var6.isBlankFinal() && var6.isStatic() == var4 && var6.getClassDefinition() == this) {
               var1.error(var6.getWhere(), "final.var.not.initialized", var6.getName());
            }
         }
      }

   }

   protected void basicCheck(Environment var1) throws ClassNotFound {
      var1.dtEnter("SourceClass.basicCheck: " + this.getName());
      super.basicCheck(var1);
      if (!this.basicChecking && !this.basicCheckDone) {
         var1.dtEvent("SourceClass.basicCheck: CHECKING " + this.getName());
         this.basicChecking = true;
         var1 = this.setupEnv(var1);
         Imports var2 = var1.getImports();
         if (var2 != null) {
            var2.resolve(var1);
         }

         this.resolveTypeStructure(var1);
         if (!this.isInterface() && !this.hasConstructor()) {
            CompoundStatement var3 = new CompoundStatement(this.getWhere(), new Statement[0]);
            Type var4 = Type.tMethod(Type.tVoid);
            int var5 = this.getModifiers() & (this.isInnerClass() ? 5 : 1);
            var1.makeMemberDefinition(var1, this.getWhere(), this, (String)null, var5, var4, idInit, (IdentifierToken[])null, (IdentifierToken[])null, var3);
         }

         if (doInheritanceChecks) {
            this.collectInheritedMethods(var1);
         }

         this.basicChecking = false;
         this.basicCheckDone = true;
         var1.dtExit("SourceClass.basicCheck: " + this.getName());
      } else {
         var1.dtExit("SourceClass.basicCheck: OK " + this.getName());
      }
   }

   protected void addMirandaMethods(Environment var1, Iterator var2) {
      while(var2.hasNext()) {
         MemberDefinition var3 = (MemberDefinition)var2.next();
         this.addMember(var3);
      }

   }

   public void resolveTypeStructure(Environment var1) {
      var1.dtEnter("SourceClass.resolveTypeStructure: " + this.getName());
      ClassDefinition var2 = this.getOuterClass();
      if (var2 != null && var2 instanceof SourceClass && !((SourceClass)var2).resolved) {
         ((SourceClass)var2).resolveTypeStructure(var1);
      }

      if (!this.resolved && !this.resolving) {
         this.resolving = true;
         var1.dtEvent("SourceClass.resolveTypeStructure: RESOLVING " + this.getName());
         var1 = this.setupEnv(var1);
         this.resolveSupers(var1);

         try {
            this.checkSupers(var1);
         } catch (ClassNotFound var5) {
            var1.error(this.where, "class.not.found", var5.name, this);
         }

         MemberDefinition var3;
         for(var3 = this.getFirstMember(); var3 != null; var3 = var3.getNextMember()) {
            if (var3 instanceof SourceMember) {
               ((SourceMember)var3).resolveTypeStructure(var1);
            }
         }

         this.resolving = false;
         this.resolved = true;

         for(var3 = this.getFirstMember(); var3 != null; var3 = var3.getNextMember()) {
            if (!var3.isInitializer() && var3.isMethod()) {
               MemberDefinition var4 = var3;

               while((var4 = var4.getNextMatch()) != null) {
                  if (var4.isMethod()) {
                     if (var3.getType().equals(var4.getType())) {
                        var1.error(var3.getWhere(), "meth.multidef", var3);
                     } else if (var3.getType().equalArguments(var4.getType())) {
                        var1.error(var3.getWhere(), "meth.redef.rettype", var3, var4);
                     }
                  }
               }
            }
         }

         var1.dtExit("SourceClass.resolveTypeStructure: " + this.getName());
      } else {
         var1.dtExit("SourceClass.resolveTypeStructure: OK " + this.getName());
      }
   }

   protected void resolveSupers(Environment var1) {
      var1.dtEnter("SourceClass.resolveSupers: " + this);
      if (this.superClassId != null && this.superClass == null) {
         this.superClass = this.resolveSuper(var1, this.superClassId);
         if (this.superClass == this.getClassDeclaration() && this.getName().equals(idJavaLangObject)) {
            this.superClass = null;
            this.superClassId = null;
         }
      }

      if (this.interfaceIds != null && this.interfaces == null) {
         this.interfaces = new ClassDeclaration[this.interfaceIds.length];

         for(int var2 = 0; var2 < this.interfaces.length; ++var2) {
            this.interfaces[var2] = this.resolveSuper(var1, this.interfaceIds[var2]);

            for(int var3 = 0; var3 < var2; ++var3) {
               if (this.interfaces[var2] == this.interfaces[var3]) {
                  Identifier var4 = this.interfaceIds[var2].getName();
                  long var5 = this.interfaceIds[var3].getWhere();
                  var1.error(var5, "intf.repeated", var4);
               }
            }
         }
      }

      var1.dtExit("SourceClass.resolveSupers: " + this);
   }

   private ClassDeclaration resolveSuper(Environment var1, IdentifierToken var2) {
      Identifier var3 = var2.getName();
      var1.dtEnter("SourceClass.resolveSuper: " + var3);
      if (this.isInnerClass()) {
         var3 = this.outerClass.resolveName(var1, var3);
      } else {
         var3 = var1.resolveName(var3);
      }

      ClassDeclaration var4 = var1.getClassDeclaration(var3);
      var1.dtExit("SourceClass.resolveSuper: " + var3);
      return var4;
   }

   public Vset checkLocalClass(Environment var1, Context var2, Vset var3, ClassDefinition var4, Expression[] var5, Type[] var6) throws ClassNotFound {
      var1 = this.setupEnv(var1);
      if (var4 != null != this.isAnonymous()) {
         throw new CompilerError("resolveAnonymousStructure");
      } else {
         if (this.isAnonymous()) {
            this.resolveAnonymousStructure(var1, var4, var5, var6);
         }

         var3 = this.checkInternal(var1, var2, var3);
         return var3;
      }
   }

   public void inlineLocalClass(Environment var1) {
      MemberDefinition var2;
      for(var2 = this.getFirstMember(); var2 != null; var2 = var2.getNextMember()) {
         if (!var2.isVariable() && !var2.isInitializer() || var2.isStatic()) {
            try {
               ((SourceMember)var2).inline(var1);
            } catch (ClassNotFound var4) {
               var1.error(var2.getWhere(), "class.not.found", var4.name, this);
            }
         }
      }

      if (this.getReferencesFrozen() != null && !this.inlinedLocalClass) {
         this.inlinedLocalClass = true;

         for(var2 = this.getFirstMember(); var2 != null; var2 = var2.getNextMember()) {
            if (var2.isConstructor()) {
               ((SourceMember)var2).addUplevelArguments();
            }
         }
      }

   }

   public Vset checkInsideClass(Environment var1, Context var2, Vset var3) throws ClassNotFound {
      if (this.isInsideLocal() && !this.isLocal()) {
         return this.checkInternal(var1, var2, var3);
      } else {
         throw new CompilerError("checkInsideClass");
      }
   }

   private void resolveAnonymousStructure(Environment var1, ClassDefinition var2, Expression[] var3, Type[] var4) throws ClassNotFound {
      var1.dtEvent("SourceClass.resolveAnonymousStructure: " + this + ", super " + var2);
      if (var2.isInterface()) {
         int var5 = this.interfaces == null ? 0 : this.interfaces.length;
         ClassDeclaration[] var6 = new ClassDeclaration[1 + var5];
         if (var5 > 0) {
            System.arraycopy(this.interfaces, 0, var6, 1, var5);
            if (this.interfaceIds != null && this.interfaceIds.length == var5) {
               IdentifierToken[] var7 = new IdentifierToken[1 + var5];
               System.arraycopy(this.interfaceIds, 0, var7, 1, var5);
               var7[0] = new IdentifierToken(var2.getName());
            }
         }

         var6[0] = var2.getClassDeclaration();
         this.interfaces = var6;
         var2 = this.toplevelEnv.getClassDefinition(idJavaLangObject);
      }

      this.superClass = var2.getClassDeclaration();
      if (this.hasConstructor()) {
         throw new CompilerError("anonymous constructor");
      } else {
         Type var16 = Type.tMethod(Type.tVoid, var4);
         IdentifierToken[] var17 = new IdentifierToken[var4.length];

         int var18;
         for(var18 = 0; var18 < var17.length; ++var18) {
            var17[var18] = new IdentifierToken(var3[var18].getWhere(), Identifier.lookup("$" + var18));
         }

         var18 = !var2.isTopLevel() && !var2.isLocal() ? 1 : 0;
         Expression[] var8 = new Expression[-var18 + var3.length];

         for(int var9 = var18; var9 < var3.length; ++var9) {
            var8[-var18 + var9] = new IdentifierExpression(var17[var9]);
         }

         long var19 = this.getWhere();
         SuperExpression var11;
         if (var18 == 0) {
            var11 = new SuperExpression(var19);
         } else {
            var11 = new SuperExpression(var19, new IdentifierExpression(var17[0]));
         }

         MethodExpression var12 = new MethodExpression(var19, var11, idInit, var8);
         Statement[] var13 = new Statement[]{new ExpressionStatement(var19, var12)};
         CompoundStatement var14 = new CompoundStatement(var19, var13);
         int var15 = 524288;
         var1.makeMemberDefinition(var1, var19, this, (String)null, var15, var16, idInit, var17, (IdentifierToken[])null, var14);
      }
   }

   static String classModifierString(int var0) {
      String var1 = "";

      for(int var2 = 0; var2 < classModifierBits.length; ++var2) {
         if ((var0 & classModifierBits[var2]) != 0) {
            var1 = var1 + " " + classModifierNames[var2];
            var0 &= ~classModifierBits[var2];
         }
      }

      if (var0 != 0) {
         var1 = var1 + " ILLEGAL:" + Integer.toHexString(var0);
      }

      return var1;
   }

   public MemberDefinition getAccessMember(Environment var1, Context var2, MemberDefinition var3, boolean var4) {
      return this.getAccessMember(var1, var2, var3, false, var4);
   }

   public MemberDefinition getUpdateMember(Environment var1, Context var2, MemberDefinition var3, boolean var4) {
      if (!var3.isVariable()) {
         throw new CompilerError("method");
      } else {
         return this.getAccessMember(var1, var2, var3, true, var4);
      }
   }

   private MemberDefinition getAccessMember(Environment var1, Context var2, MemberDefinition var3, boolean var4, boolean var5) {
      boolean var6 = var3.isStatic();
      boolean var7 = var3.isMethod();

      MemberDefinition var8;
      for(var8 = this.getFirstMember(); var8 != null; var8 = var8.getNextMember()) {
         if (var8.getAccessMethodTarget() == var3) {
            if (var7 && var8.isSuperAccessMethod() == var5) {
               break;
            }

            int var9 = var8.getType().getArgumentTypes().length;
            if (var9 == (var6 ? 0 : 1)) {
               break;
            }
         }
      }

      if (var8 != null) {
         if (!var4) {
            return var8;
         }

         MemberDefinition var28 = var8.getAccessUpdateMember();
         if (var28 != null) {
            return var28;
         }
      } else if (var4) {
         var8 = this.getAccessMember(var1, var2, var3, false, var5);
      }

      Type var10 = null;
      int var15;
      Type[] var18;
      ClassDefinition var19;
      Identifier var29;
      if (var3.isConstructor()) {
         var29 = idInit;
         SourceClass var11 = (SourceClass)this.getTopClass();
         var10 = var11.dummyArgumentType;
         if (var10 == null) {
            IdentifierToken var12 = new IdentifierToken(0L, idJavaLangObject);
            IdentifierToken[] var13 = new IdentifierToken[0];
            IdentifierToken var14 = new IdentifierToken(0L, idNull);
            var15 = 589832;
            if (var11.isInterface()) {
               var15 |= 1;
            }

            ClassDefinition var16 = this.toplevelEnv.makeClassDefinition(this.toplevelEnv, 0L, var14, (String)null, var15, var12, var13, var11);
            var16.getClassDeclaration().setDefinition(var16, 4);
            Expression[] var17 = new Expression[0];
            var18 = new Type[0];

            try {
               var19 = this.toplevelEnv.getClassDefinition(idJavaLangObject);
               var16.checkLocalClass(this.toplevelEnv, (Context)null, new Vset(), var19, var17, var18);
            } catch (ClassNotFound var27) {
            }

            var10 = var16.getType();
            var11.dummyArgumentType = var10;
         }
      } else {
         int var30 = 0;

         while(true) {
            var29 = Identifier.lookup("access$" + var30);
            if (this.getFirstMatch(var29) == null) {
               break;
            }

            ++var30;
         }
      }

      Type var32 = var3.getType();
      Type[] var31;
      int var41;
      if (var6) {
         if (!var7) {
            Type[] var33;
            if (!var4) {
               var33 = new Type[0];
               var31 = var33;
               var32 = Type.tMethod(var32);
            } else {
               var33 = new Type[]{var32};
               var31 = var33;
               var32 = Type.tMethod(Type.tVoid, var33);
            }
         } else {
            var31 = var32.getArgumentTypes();
         }
      } else {
         Type var34 = this.getType();
         Type[] var36;
         if (!var7) {
            if (!var4) {
               var36 = new Type[]{var34};
               var31 = var36;
               var32 = Type.tMethod(var32, var36);
            } else {
               var36 = new Type[]{var34, var32};
               var31 = var36;
               var32 = Type.tMethod(Type.tVoid, var36);
            }
         } else {
            var36 = var32.getArgumentTypes();
            var15 = var36.length;
            if (var3.isConstructor()) {
               LocalMember var39 = ((SourceMember)var3).getOuterThisArg();
               if (var39 != null) {
                  if (var36[0] != var39.getType()) {
                     throw new CompilerError("misplaced outer this");
                  }

                  var31 = new Type[var15];
                  var31[0] = var10;

                  for(var41 = 1; var41 < var15; ++var41) {
                     var31[var41] = var36[var41];
                  }
               } else {
                  var31 = new Type[var15 + 1];
                  var31[0] = var10;

                  for(var41 = 0; var41 < var15; ++var41) {
                     var31[var41 + 1] = var36[var41];
                  }
               }
            } else {
               var31 = new Type[var15 + 1];
               var31[0] = var34;

               for(int var38 = 0; var38 < var15; ++var38) {
                  var31[var38 + 1] = var36[var38];
               }
            }

            var32 = Type.tMethod(var32.getReturnType(), var31);
         }
      }

      int var35 = var31.length;
      long var37 = var3.getWhere();
      IdentifierToken[] var40 = new IdentifierToken[var35];

      for(var41 = 0; var41 < var35; ++var41) {
         var40[var41] = new IdentifierToken(var37, Identifier.lookup("$" + var41));
      }

      Object var43 = null;
      var18 = null;
      var19 = null;
      int var20;
      Expression[] var44;
      if (var6) {
         var44 = new Expression[var35];

         for(var20 = 0; var20 < var35; ++var20) {
            var44[var20] = new IdentifierExpression(var40[var20]);
         }
      } else {
         Object var42;
         if (var3.isConstructor()) {
            var42 = new ThisExpression(var37);
            var44 = new Expression[var35 - 1];

            for(var20 = 1; var20 < var35; ++var20) {
               var44[var20 - 1] = new IdentifierExpression(var40[var20]);
            }
         } else {
            var42 = new IdentifierExpression(var40[0]);
            var44 = new Expression[var35 - 1];

            for(var20 = 1; var20 < var35; ++var20) {
               var44[var20 - 1] = new IdentifierExpression(var40[var20]);
            }
         }

         var43 = var42;
      }

      if (!var7) {
         var43 = new FieldExpression(var37, (Expression)var43, var3);
         if (var4) {
            var43 = new AssignExpression(var37, (Expression)var43, var44[0]);
         }
      } else {
         var43 = new MethodExpression(var37, (Expression)var43, var3, var44, var5);
      }

      Object var45;
      if (var32.getReturnType().isType(11)) {
         var45 = new ExpressionStatement(var37, (Expression)var43);
      } else {
         var45 = new ReturnStatement(var37, (Expression)var43);
      }

      Statement[] var21 = new Statement[]{(Statement)var45};
      CompoundStatement var46 = new CompoundStatement(var37, var21);
      int var22 = 524288;
      if (!var3.isConstructor()) {
         var22 |= 8;
      }

      SourceMember var23 = (SourceMember)var1.makeMemberDefinition(var1, var37, this, (String)null, var22, var32, var29, var40, var3.getExceptionIds(), var46);
      var23.setExceptions(var3.getExceptions(var1));
      var23.setAccessMethodTarget(var3);
      if (var4) {
         var8.setAccessUpdateMember(var23);
      }

      var23.setIsSuperAccessMethod(var5);
      Context var24 = var23.getClassDefinition().getClassContext();
      if (var24 != null) {
         try {
            var23.check(var1, var24, new Vset());
         } catch (ClassNotFound var26) {
            var1.error(var37, "class.not.found", var26.name, this);
         }
      }

      return var23;
   }

   SourceClass findLookupContext() {
      MemberDefinition var1;
      SourceClass var2;
      for(var1 = this.getFirstMember(); var1 != null; var1 = var1.getNextMember()) {
         if (var1.isInnerClass()) {
            var2 = (SourceClass)var1.getInnerClass();
            if (!var2.isInterface()) {
               return var2;
            }
         }
      }

      for(var1 = this.getFirstMember(); var1 != null; var1 = var1.getNextMember()) {
         if (var1.isInnerClass()) {
            var2 = ((SourceClass)var1.getInnerClass()).findLookupContext();
            if (var2 != null) {
               return var2;
            }
         }
      }

      return null;
   }

   public MemberDefinition getClassLiteralLookup(long var1) {
      if (this.lookup != null) {
         return this.lookup;
      } else if (this.outerClass != null) {
         this.lookup = this.outerClass.getClassLiteralLookup(var1);
         return this.lookup;
      } else {
         SourceClass var3 = this;
         boolean var4 = false;
         if (this.isInterface()) {
            var3 = this.findLookupContext();
            if (var3 == null) {
               var4 = true;
               IdentifierToken var5 = new IdentifierToken(var1, idJavaLangObject);
               IdentifierToken[] var6 = new IdentifierToken[0];
               IdentifierToken var7 = new IdentifierToken(var1, idNull);
               int var8 = 589833;
               var3 = (SourceClass)this.toplevelEnv.makeClassDefinition(this.toplevelEnv, var1, var7, (String)null, var8, var5, var6, this);
            }
         }

         Identifier var28 = Identifier.lookup("class$");
         Type[] var29 = new Type[]{Type.tString};
         long var30 = var3.getWhere();
         IdentifierToken var9 = new IdentifierToken(var30, var28);
         IdentifierExpression var10 = new IdentifierExpression(var9);
         Expression[] var11 = new Expression[]{var10};
         Identifier var12 = Identifier.lookup("forName");
         MethodExpression var31 = new MethodExpression(var30, new TypeExpression(var30, Type.tClassDesc), var12, var11);
         ReturnStatement var13 = new ReturnStatement(var30, var31);
         Identifier var14 = Identifier.lookup("java.lang.ClassNotFoundException");
         Identifier var15 = Identifier.lookup("java.lang.NoClassDefFoundError");
         Type var16 = Type.tClass(var14);
         Type var17 = Type.tClass(var15);
         Identifier var18 = Identifier.lookup("getMessage");
         var10 = new IdentifierExpression(var30, var12);
         var31 = new MethodExpression(var30, var10, var18, new Expression[0]);
         Expression[] var19 = new Expression[]{var31};
         NewInstanceExpression var32 = new NewInstanceExpression(var30, new TypeExpression(var30, var17), var19);
         CatchStatement var20 = new CatchStatement(var30, new TypeExpression(var30, var16), new IdentifierToken(var12), new ThrowStatement(var30, var32));
         Statement[] var21 = new Statement[]{var20};
         TryStatement var33 = new TryStatement(var30, var13, var21);
         Type var22 = Type.tMethod(Type.tClassDesc, var29);
         IdentifierToken[] var23 = new IdentifierToken[]{var9};
         this.lookup = this.toplevelEnv.makeMemberDefinition(this.toplevelEnv, var30, var3, (String)null, 524296, var22, var28, var23, (IdentifierToken[])null, var33);
         if (var4) {
            if (var3.getClassDeclaration().getStatus() == 5) {
               throw new CompilerError("duplicate check");
            }

            var3.getClassDeclaration().setDefinition(var3, 4);
            Expression[] var24 = new Expression[0];
            Type[] var25 = new Type[0];

            try {
               ClassDefinition var26 = this.toplevelEnv.getClassDefinition(idJavaLangObject);
               var3.checkLocalClass(this.toplevelEnv, (Context)null, new Vset(), var26, var24, var25);
            } catch (ClassNotFound var27) {
            }
         }

         return this.lookup;
      }
   }

   public void compile(OutputStream var1) throws InterruptedException, IOException {
      Environment var2 = this.toplevelEnv;
      synchronized(active) {
         while(active.contains(this.getName())) {
            active.wait();
         }

         active.addElement(this.getName());
      }

      boolean var14 = false;

      try {
         var14 = true;
         this.compileClass(var2, var1);
         var14 = false;
      } catch (ClassNotFound var17) {
         throw new CompilerError(var17);
      } finally {
         if (var14) {
            synchronized(active) {
               active.removeElement(this.getName());
               active.notifyAll();
            }
         }
      }

      synchronized(active) {
         active.removeElement(this.getName());
         active.notifyAll();
      }
   }

   private static void assertModifiers(int var0, int var1) {
      if ((var0 & var1) != var1) {
         throw new CompilerError("illegal class modifiers");
      }
   }

   protected void compileClass(Environment var1, OutputStream var2) throws IOException, ClassNotFound {
      Vector var3 = new Vector();
      Vector var4 = new Vector();
      Vector var5 = new Vector();
      CompilerMember var6 = new CompilerMember(new MemberDefinition(this.getWhere(), this, 8, Type.tMethod(Type.tVoid), idClassInit, (IdentifierToken[])null, (Node)null), new Assembler());
      Context var7 = new Context((Context)null, var6.field);

      for(Object var8 = this; ((ClassDefinition)var8).isInnerClass(); var8 = ((ClassDefinition)var8).getOuterClass()) {
         var5.addElement(var8);
      }

      int var34 = var5.size();
      int var9 = var34;

      while(true) {
         --var9;
         if (var9 < 0) {
            var9 = var34;

            while(true) {
               --var9;
               if (var9 < 0) {
                  boolean var35 = this.isDeprecated();
                  boolean var10 = this.isSynthetic();
                  boolean var11 = false;
                  boolean var12 = false;

                  for(SourceMember var13 = (SourceMember)this.getFirstMember(); var13 != null; var13 = (SourceMember)var13.getNextMember()) {
                     var35 |= var13.isDeprecated();
                     var10 |= var13.isSynthetic();

                     try {
                        CompilerMember var14;
                        if (var13.isMethod()) {
                           var12 |= var13.getExceptions(var1).length > 0;
                           if (var13.isInitializer()) {
                              if (var13.isStatic()) {
                                 var13.code(var1, var6.asm);
                              }
                           } else {
                              var14 = new CompilerMember(var13, new Assembler());
                              var13.code(var1, var14.asm);
                              var4.addElement(var14);
                           }
                        } else if (var13.isInnerClass()) {
                           var5.addElement(var13.getInnerClass());
                        } else if (var13.isVariable()) {
                           var13.inline(var1);
                           var14 = new CompilerMember(var13, (Assembler)null);
                           var3.addElement(var14);
                           if (var13.isStatic()) {
                              var13.codeInit(var1, var7, var6.asm);
                           }

                           var11 |= var13.getInitialValue() != null;
                        }
                     } catch (CompilerError var32) {
                        var32.printStackTrace();
                        var1.error(var13, 0L, "generic", var13.getClassDeclaration() + ":" + var13 + "@" + var32.toString(), (Object)null, (Object)null);
                     }
                  }

                  if (!var6.asm.empty()) {
                     var6.asm.add(this.getWhere(), 177, true);
                     var4.addElement(var6);
                  }

                  if (this.getNestError()) {
                     return;
                  } else {
                     int var36 = 0;
                     if (var4.size() > 0) {
                        this.tab.put("Code");
                     }

                     if (var11) {
                        this.tab.put("ConstantValue");
                     }

                     String var37 = null;
                     if (var1.debug_source()) {
                        var37 = ((ClassFile)this.getSource()).getName();
                        this.tab.put("SourceFile");
                        this.tab.put(var37);
                        ++var36;
                     }

                     if (var12) {
                        this.tab.put("Exceptions");
                     }

                     if (var1.debug_lines()) {
                        this.tab.put("LineNumberTable");
                     }

                     if (var35) {
                        this.tab.put("Deprecated");
                        if (this.isDeprecated()) {
                           ++var36;
                        }
                     }

                     if (var10) {
                        this.tab.put("Synthetic");
                        if (this.isSynthetic()) {
                           ++var36;
                        }
                     }

                     if (var1.coverage()) {
                        var36 += 2;
                        this.tab.put("AbsoluteSourcePath");
                        this.tab.put("TimeStamp");
                        this.tab.put("CoverageTable");
                     }

                     if (var1.debug_vars()) {
                        this.tab.put("LocalVariableTable");
                     }

                     if (var5.size() > 0) {
                        this.tab.put("InnerClasses");
                        ++var36;
                     }

                     String var15 = "";
                     long var16 = 0L;
                     if (var1.coverage()) {
                        var15 = this.getAbsoluteName();
                        var16 = System.currentTimeMillis();
                        this.tab.put(var15);
                     }

                     this.tab.put(this.getClassDeclaration());
                     if (this.getSuperClass() != null) {
                        this.tab.put(this.getSuperClass());
                     }

                     for(int var18 = 0; var18 < this.interfaces.length; ++var18) {
                        this.tab.put(this.interfaces[var18]);
                     }

                     CompilerMember[] var38 = new CompilerMember[var4.size()];
                     var4.copyInto(var38);
                     Arrays.sort(var38);

                     for(int var19 = 0; var19 < var4.size(); ++var19) {
                        var4.setElementAt(var38[var19], var19);
                     }

                     Enumeration var39 = var4.elements();

                     CompilerMember var20;
                     while(var39.hasMoreElements()) {
                        var20 = (CompilerMember)var39.nextElement();

                        try {
                           var20.asm.optimize(var1);
                           var20.asm.collect(var1, var20.field, this.tab);
                           this.tab.put(var20.name);
                           this.tab.put(var20.sig);
                           ClassDeclaration[] var21 = var20.field.getExceptions(var1);

                           for(int var22 = 0; var22 < var21.length; ++var22) {
                              this.tab.put(var21[var22]);
                           }
                        } catch (Exception var33) {
                           var33.printStackTrace();
                           var1.error(var20.field, -1L, "generic", var20.field.getName() + "@" + var33.toString(), (Object)null, (Object)null);
                           var20.asm.listing(System.out);
                        }
                     }

                     var39 = var3.elements();

                     while(var39.hasMoreElements()) {
                        var20 = (CompilerMember)var39.nextElement();
                        this.tab.put(var20.name);
                        this.tab.put(var20.sig);
                        Object var43 = var20.field.getInitialValue();
                        if (var43 != null) {
                           this.tab.put(var43 instanceof String ? new StringExpression(var20.field.getWhere(), (String)var43) : var43);
                        }
                     }

                     var39 = var5.elements();

                     Identifier var45;
                     while(var39.hasMoreElements()) {
                        ClassDefinition var41 = (ClassDefinition)var39.nextElement();
                        this.tab.put(var41.getClassDeclaration());
                        if (!var41.isLocal()) {
                           ClassDefinition var44 = var41.getOuterClass();
                           this.tab.put(var44.getClassDeclaration());
                        }

                        var45 = var41.getLocalName();
                        if (var45 != idNull) {
                           this.tab.put(var45.toString());
                        }
                     }

                     DataOutputStream var40 = new DataOutputStream(var2);
                     var40.writeInt(-889275714);
                     var40.writeShort(this.toplevelEnv.getMinorVersion());
                     var40.writeShort(this.toplevelEnv.getMajorVersion());
                     this.tab.write(var1, var40);
                     int var42 = this.getModifiers() & 2098705;
                     if (this.isInterface()) {
                        assertModifiers(var42, 1024);
                     } else {
                        var42 |= 32;
                     }

                     if (this.outerClass != null) {
                        if (this.isProtected()) {
                           var42 |= 1;
                        }

                        if (this.outerClass.isInterface()) {
                           assertModifiers(var42, 1);
                        }
                     }

                     var40.writeShort(var42);
                     if (var1.dumpModifiers()) {
                        var45 = this.getName();
                        Identifier var46 = Identifier.lookup(var45.getQualifier(), var45.getFlatName());
                        System.out.println();
                        System.out.println("CLASSFILE  " + var46);
                        System.out.println("---" + classModifierString(var42));
                     }

                     var40.writeShort(this.tab.index(this.getClassDeclaration()));
                     var40.writeShort(this.getSuperClass() != null ? this.tab.index(this.getSuperClass()) : 0);
                     var40.writeShort(this.interfaces.length);

                     for(int var47 = 0; var47 < this.interfaces.length; ++var47) {
                        var40.writeShort(this.tab.index(this.interfaces[var47]));
                     }

                     ByteArrayOutputStream var49 = new ByteArrayOutputStream(256);
                     ByteArrayOutputStream var48 = new ByteArrayOutputStream(256);
                     DataOutputStream var23 = new DataOutputStream(var49);
                     var40.writeShort(var3.size());
                     Enumeration var24 = var3.elements();

                     CompilerMember var25;
                     int var27;
                     boolean var29;
                     while(var24.hasMoreElements()) {
                        var25 = (CompilerMember)var24.nextElement();
                        Object var26 = var25.field.getInitialValue();
                        var40.writeShort(var25.field.getModifiers() & 223);
                        var40.writeShort(this.tab.index(var25.name));
                        var40.writeShort(this.tab.index(var25.sig));
                        var27 = var26 != null ? 1 : 0;
                        boolean var28 = var25.field.isDeprecated();
                        var29 = var25.field.isSynthetic();
                        var27 += (var28 ? 1 : 0) + (var29 ? 1 : 0);
                        var40.writeShort(var27);
                        if (var26 != null) {
                           var40.writeShort(this.tab.index("ConstantValue"));
                           var40.writeInt(2);
                           var40.writeShort(this.tab.index(var26 instanceof String ? new StringExpression(var25.field.getWhere(), (String)var26) : var26));
                        }

                        if (var28) {
                           var40.writeShort(this.tab.index("Deprecated"));
                           var40.writeInt(0);
                        }

                        if (var29) {
                           var40.writeShort(this.tab.index("Synthetic"));
                           var40.writeInt(0);
                        }
                     }

                     var40.writeShort(var4.size());
                     var24 = var4.elements();

                     while(var24.hasMoreElements()) {
                        var25 = (CompilerMember)var24.nextElement();
                        int var52 = var25.field.getModifiers() & 2098495;
                        if ((var52 & 2097152) == 0 && (var42 & 2097152) == 0) {
                           if (var1.strictdefault()) {
                              var52 |= 2048;
                           }
                        } else {
                           var52 |= 2048;
                        }

                        var40.writeShort(var52);
                        var40.writeShort(this.tab.index(var25.name));
                        var40.writeShort(this.tab.index(var25.sig));
                        ClassDeclaration[] var53 = var25.field.getExceptions(var1);
                        int var56 = var53.length > 0 ? 1 : 0;
                        var29 = var25.field.isDeprecated();
                        boolean var30 = var25.field.isSynthetic();
                        var56 += (var29 ? 1 : 0) + (var30 ? 1 : 0);
                        int var31;
                        if (!var25.asm.empty()) {
                           var40.writeShort(var56 + 1);
                           var25.asm.write(var1, var23, var25.field, this.tab);
                           var31 = 0;
                           if (var1.debug_lines()) {
                              ++var31;
                           }

                           if (var1.coverage()) {
                              ++var31;
                           }

                           if (var1.debug_vars()) {
                              ++var31;
                           }

                           var23.writeShort(var31);
                           if (var1.debug_lines()) {
                              var25.asm.writeLineNumberTable(var1, new DataOutputStream(var48), this.tab);
                              var23.writeShort(this.tab.index("LineNumberTable"));
                              var23.writeInt(var48.size());
                              var48.writeTo(var49);
                              var48.reset();
                           }

                           if (var1.coverage()) {
                              var25.asm.writeCoverageTable(var1, this, new DataOutputStream(var48), this.tab, var25.field.getWhere());
                              var23.writeShort(this.tab.index("CoverageTable"));
                              var23.writeInt(var48.size());
                              var48.writeTo(var49);
                              var48.reset();
                           }

                           if (var1.debug_vars()) {
                              var25.asm.writeLocalVariableTable(var1, var25.field, new DataOutputStream(var48), this.tab);
                              var23.writeShort(this.tab.index("LocalVariableTable"));
                              var23.writeInt(var48.size());
                              var48.writeTo(var49);
                              var48.reset();
                           }

                           var40.writeShort(this.tab.index("Code"));
                           var40.writeInt(var49.size());
                           var49.writeTo(var40);
                           var49.reset();
                        } else {
                           if (var1.coverage() && (var25.field.getModifiers() & 256) > 0) {
                              var25.asm.addNativeToJcovTab(var1, this);
                           }

                           var40.writeShort(var56);
                        }

                        if (var53.length > 0) {
                           var40.writeShort(this.tab.index("Exceptions"));
                           var40.writeInt(2 + var53.length * 2);
                           var40.writeShort(var53.length);

                           for(var31 = 0; var31 < var53.length; ++var31) {
                              var40.writeShort(this.tab.index(var53[var31]));
                           }
                        }

                        if (var29) {
                           var40.writeShort(this.tab.index("Deprecated"));
                           var40.writeInt(0);
                        }

                        if (var30) {
                           var40.writeShort(this.tab.index("Synthetic"));
                           var40.writeInt(0);
                        }
                     }

                     var40.writeShort(var36);
                     if (var1.debug_source()) {
                        var40.writeShort(this.tab.index("SourceFile"));
                        var40.writeInt(2);
                        var40.writeShort(this.tab.index(var37));
                     }

                     if (this.isDeprecated()) {
                        var40.writeShort(this.tab.index("Deprecated"));
                        var40.writeInt(0);
                     }

                     if (this.isSynthetic()) {
                        var40.writeShort(this.tab.index("Synthetic"));
                        var40.writeInt(0);
                     }

                     if (var1.coverage()) {
                        var40.writeShort(this.tab.index("AbsoluteSourcePath"));
                        var40.writeInt(2);
                        var40.writeShort(this.tab.index(var15));
                        var40.writeShort(this.tab.index("TimeStamp"));
                        var40.writeInt(8);
                        var40.writeLong(var16);
                     }

                     if (var5.size() > 0) {
                        var40.writeShort(this.tab.index("InnerClasses"));
                        var40.writeInt(2 + 8 * var5.size());
                        var40.writeShort(var5.size());
                        var24 = var5.elements();

                        while(var24.hasMoreElements()) {
                           ClassDefinition var51 = (ClassDefinition)var24.nextElement();
                           var40.writeShort(this.tab.index(var51.getClassDeclaration()));
                           if (!var51.isLocal() && !var51.isAnonymous()) {
                              ClassDefinition var54 = var51.getOuterClass();
                              var40.writeShort(this.tab.index(var54.getClassDeclaration()));
                           } else {
                              var40.writeShort(0);
                           }

                           Identifier var55 = var51.getLocalName();
                           if (var55 == idNull) {
                              if (!var51.isAnonymous()) {
                                 throw new CompilerError("compileClass(), anonymous");
                              }

                              var40.writeShort(0);
                           } else {
                              var40.writeShort(this.tab.index(var55.toString()));
                           }

                           var27 = var51.getInnerClassMember().getModifiers() & 3615;
                           if (var51.isInterface()) {
                              assertModifiers(var27, 1032);
                           }

                           if (var51.getOuterClass().isInterface()) {
                              var27 &= -7;
                              assertModifiers(var27, 9);
                           }

                           var40.writeShort(var27);
                           if (var1.dumpModifiers()) {
                              Identifier var57 = var51.getInnerClassMember().getName();
                              Identifier var58 = Identifier.lookup(var57.getQualifier(), var57.getFlatName());
                              System.out.println("INNERCLASS " + var58);
                              System.out.println("---" + classModifierString(var27));
                           }
                        }
                     }

                     var40.flush();
                     this.tab = null;
                     if (var1.covdata()) {
                        Assembler var50 = new Assembler();
                        var50.GenVecJCov(var1, this, var16);
                     }

                     return;
                  }
               }

               var5.removeElementAt(var9);
            }
         }

         var5.addElement(var5.elementAt(var9));
      }
   }

   public void printClassDependencies(Environment var1) {
      if (this.toplevelEnv.print_dependencies()) {
         String var2 = ((ClassFile)this.getSource()).getAbsoluteName();
         String var3 = Type.mangleInnerType(this.getName()).toString();
         long var4 = this.getWhere() >> 32;
         long var6 = this.getEndPosition() >> 32;
         System.out.println("CLASS:" + var2 + "," + var4 + "," + var6 + "," + var3);
         Enumeration var8 = this.deps.elements();

         while(var8.hasMoreElements()) {
            ClassDeclaration var9 = (ClassDeclaration)var8.nextElement();
            String var10 = Type.mangleInnerType(var9.getName()).toString();
            var1.output("CLDEP:" + var3 + "," + var10);
         }
      }

   }
}
