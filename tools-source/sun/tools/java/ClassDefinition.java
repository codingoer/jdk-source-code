package sun.tools.java;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import sun.tools.javac.SourceClass;
import sun.tools.javac.SourceMember;
import sun.tools.tree.Context;
import sun.tools.tree.Expression;
import sun.tools.tree.LocalMember;
import sun.tools.tree.UplevelReference;
import sun.tools.tree.Vset;

public class ClassDefinition implements Constants {
   protected Object source;
   protected long where;
   protected int modifiers;
   protected Identifier localName;
   protected ClassDeclaration declaration;
   protected IdentifierToken superClassId;
   protected IdentifierToken[] interfaceIds;
   protected ClassDeclaration superClass;
   protected ClassDeclaration[] interfaces;
   protected ClassDefinition outerClass;
   protected MemberDefinition outerMember;
   protected MemberDefinition innerClassMember;
   protected MemberDefinition firstMember;
   protected MemberDefinition lastMember;
   protected boolean resolved;
   protected String documentation;
   protected boolean error;
   protected boolean nestError;
   protected UplevelReference references;
   protected boolean referencesFrozen;
   private Hashtable fieldHash = new Hashtable(31);
   private int abstr;
   private Hashtable localClasses = null;
   private final int LOCAL_CLASSES_SIZE = 31;
   protected Context classContext;
   protected boolean supersCheckStarted = !(this instanceof SourceClass);
   MethodSet allMethods = null;
   private List permanentlyAbstractMethods = new ArrayList();
   protected static boolean doInheritanceChecks = true;

   public Context getClassContext() {
      return this.classContext;
   }

   protected ClassDefinition(Object var1, long var2, ClassDeclaration var4, int var5, IdentifierToken var6, IdentifierToken[] var7) {
      this.source = var1;
      this.where = var2;
      this.declaration = var4;
      this.modifiers = var5;
      this.superClassId = var6;
      this.interfaceIds = var7;
   }

   public final Object getSource() {
      return this.source;
   }

   public final boolean getError() {
      return this.error;
   }

   public final void setError() {
      this.error = true;
      this.setNestError();
   }

   public final boolean getNestError() {
      return this.nestError || this.outerClass != null && this.outerClass.getNestError();
   }

   public final void setNestError() {
      this.nestError = true;
      if (this.outerClass != null) {
         this.outerClass.setNestError();
      }

   }

   public final long getWhere() {
      return this.where;
   }

   public final ClassDeclaration getClassDeclaration() {
      return this.declaration;
   }

   public final int getModifiers() {
      return this.modifiers;
   }

   public final void subModifiers(int var1) {
      this.modifiers &= ~var1;
   }

   public final void addModifiers(int var1) {
      this.modifiers |= var1;
   }

   public final ClassDeclaration getSuperClass() {
      if (!this.supersCheckStarted) {
         throw new CompilerError("unresolved super");
      } else {
         return this.superClass;
      }
   }

   public ClassDeclaration getSuperClass(Environment var1) {
      return this.getSuperClass();
   }

   public final ClassDeclaration[] getInterfaces() {
      if (this.interfaces == null) {
         throw new CompilerError("getInterfaces");
      } else {
         return this.interfaces;
      }
   }

   public final ClassDefinition getOuterClass() {
      return this.outerClass;
   }

   protected final void setOuterClass(ClassDefinition var1) {
      if (this.outerClass != null) {
         throw new CompilerError("setOuterClass");
      } else {
         this.outerClass = var1;
      }
   }

   protected final void setOuterMember(MemberDefinition var1) {
      if (!this.isStatic() && this.isInnerClass()) {
         if (this.outerMember != null) {
            throw new CompilerError("setOuterField");
         } else {
            this.outerMember = var1;
         }
      } else {
         throw new CompilerError("setOuterField");
      }
   }

   public final boolean isInnerClass() {
      return this.outerClass != null;
   }

   public final boolean isMember() {
      return this.outerClass != null && !this.isLocal();
   }

   public final boolean isTopLevel() {
      return this.outerClass == null || this.isStatic() || this.isInterface();
   }

   public final boolean isInsideLocal() {
      return this.isLocal() || this.outerClass != null && this.outerClass.isInsideLocal();
   }

   public final boolean isInsideLocalOrAnonymous() {
      return this.isLocal() || this.isAnonymous() || this.outerClass != null && this.outerClass.isInsideLocalOrAnonymous();
   }

   public Identifier getLocalName() {
      return this.localName != null ? this.localName : this.getName().getFlatName().getName();
   }

   public void setLocalName(Identifier var1) {
      if (this.isLocal()) {
         this.localName = var1;
      }

   }

   public final MemberDefinition getInnerClassMember() {
      if (this.outerClass == null) {
         return null;
      } else {
         if (this.innerClassMember == null) {
            Identifier var1 = this.getName().getFlatName().getName();

            for(MemberDefinition var2 = this.outerClass.getFirstMatch(var1); var2 != null; var2 = var2.getNextMatch()) {
               if (var2.isInnerClass()) {
                  this.innerClassMember = var2;
                  break;
               }
            }

            if (this.innerClassMember == null) {
               throw new CompilerError("getInnerClassField");
            }
         }

         return this.innerClassMember;
      }
   }

   public final MemberDefinition findOuterMember() {
      return this.outerMember;
   }

   public final boolean isStatic() {
      return (this.modifiers & 8) != 0;
   }

   public final ClassDefinition getTopClass() {
      ClassDefinition var1;
      ClassDefinition var2;
      for(var1 = this; (var2 = var1.outerClass) != null; var1 = var2) {
      }

      return var1;
   }

   public final MemberDefinition getFirstMember() {
      return this.firstMember;
   }

   public final MemberDefinition getFirstMatch(Identifier var1) {
      return (MemberDefinition)this.fieldHash.get(var1);
   }

   public final Identifier getName() {
      return this.declaration.getName();
   }

   public final Type getType() {
      return this.declaration.getType();
   }

   public String getDocumentation() {
      return this.documentation;
   }

   public static boolean containsDeprecated(String var0) {
      if (var0 == null) {
         return false;
      } else {
         for(int var1 = 0; (var1 = var0.indexOf("@deprecated", var1)) >= 0; var1 += "@deprecated".length()) {
            int var2 = var1 - 1;

            while(true) {
               char var3;
               if (var2 >= 0) {
                  var3 = var0.charAt(var2);
                  if (var3 != '\n' && var3 != '\r') {
                     if (!Character.isSpace(var3)) {
                        break;
                     }

                     --var2;
                     continue;
                  }
               }

               var2 = var1 + "@deprecated".length();
               if (var2 >= var0.length()) {
                  return true;
               }

               var3 = var0.charAt(var2);
               if (var3 == '\n' || var3 == '\r' || Character.isSpace(var3)) {
                  return true;
               }
               break;
            }
         }

         return false;
      }
   }

   public final boolean inSamePackage(ClassDeclaration var1) {
      return this.inSamePackage(var1.getName().getQualifier());
   }

   public final boolean inSamePackage(ClassDefinition var1) {
      return this.inSamePackage(var1.getName().getQualifier());
   }

   public final boolean inSamePackage(Identifier var1) {
      return this.getName().getQualifier().equals(var1);
   }

   public final boolean isInterface() {
      return (this.getModifiers() & 512) != 0;
   }

   public final boolean isClass() {
      return (this.getModifiers() & 512) == 0;
   }

   public final boolean isPublic() {
      return (this.getModifiers() & 1) != 0;
   }

   public final boolean isPrivate() {
      return (this.getModifiers() & 2) != 0;
   }

   public final boolean isProtected() {
      return (this.getModifiers() & 4) != 0;
   }

   public final boolean isPackagePrivate() {
      return (this.modifiers & 7) == 0;
   }

   public final boolean isFinal() {
      return (this.getModifiers() & 16) != 0;
   }

   public final boolean isAbstract() {
      return (this.getModifiers() & 1024) != 0;
   }

   public final boolean isSynthetic() {
      return (this.getModifiers() & 524288) != 0;
   }

   public final boolean isDeprecated() {
      return (this.getModifiers() & 262144) != 0;
   }

   public final boolean isAnonymous() {
      return (this.getModifiers() & 65536) != 0;
   }

   public final boolean isLocal() {
      return (this.getModifiers() & 131072) != 0;
   }

   public final boolean hasConstructor() {
      return this.getFirstMatch(idInit) != null;
   }

   public final boolean mustBeAbstract(Environment var1) {
      if (this.isAbstract()) {
         return true;
      } else {
         this.collectInheritedMethods(var1);
         Iterator var2 = this.getMethods();

         MemberDefinition var3;
         do {
            if (!var2.hasNext()) {
               return this.getPermanentlyAbstractMethods().hasNext();
            }

            var3 = (MemberDefinition)var2.next();
         } while(!var3.isAbstract());

         return true;
      }
   }

   public boolean superClassOf(Environment var1, ClassDeclaration var2) throws ClassNotFound {
      while(var2 != null) {
         if (this.getClassDeclaration().equals(var2)) {
            return true;
         }

         var2 = var2.getClassDefinition(var1).getSuperClass();
      }

      return false;
   }

   public boolean enclosingClassOf(ClassDefinition var1) {
      while(true) {
         if ((var1 = var1.getOuterClass()) != null) {
            if (this != var1) {
               continue;
            }

            return true;
         }

         return false;
      }
   }

   public boolean subClassOf(Environment var1, ClassDeclaration var2) throws ClassNotFound {
      for(ClassDeclaration var3 = this.getClassDeclaration(); var3 != null; var3 = var3.getClassDefinition(var1).getSuperClass()) {
         if (var3.equals(var2)) {
            return true;
         }
      }

      return false;
   }

   public boolean implementedBy(Environment var1, ClassDeclaration var2) throws ClassNotFound {
      while(var2 != null) {
         if (this.getClassDeclaration().equals(var2)) {
            return true;
         }

         ClassDeclaration[] var3 = var2.getClassDefinition(var1).getInterfaces();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (this.implementedBy(var1, var3[var4])) {
               return true;
            }
         }

         var2 = var2.getClassDefinition(var1).getSuperClass();
      }

      return false;
   }

   public boolean couldImplement(ClassDefinition var1) {
      if (!doInheritanceChecks) {
         throw new CompilerError("couldImplement: no checks");
      } else if (this.isInterface() && var1.isInterface()) {
         if (this.allMethods == null) {
            throw new CompilerError("couldImplement: called early");
         } else {
            Iterator var2 = var1.getMethods();

            MemberDefinition var3;
            MemberDefinition var6;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = (MemberDefinition)var2.next();
               Identifier var4 = var3.getName();
               Type var5 = var3.getType();
               var6 = this.allMethods.lookupSig(var4, var5);
            } while(var6 == null || var6.sameReturnType(var3));

            return false;
         }
      } else {
         throw new CompilerError("couldImplement: not interface");
      }
   }

   public boolean extendsCanAccess(Environment var1, ClassDeclaration var2) throws ClassNotFound {
      if (this.outerClass != null) {
         return this.outerClass.canAccess(var1, var2);
      } else {
         ClassDefinition var3 = var2.getClassDefinition(var1);
         if (var3.isLocal()) {
            throw new CompilerError("top local");
         } else if (var3.isInnerClass()) {
            MemberDefinition var4 = var3.getInnerClassMember();
            if (var4.isPublic()) {
               return true;
            } else {
               return var4.isPrivate() ? this.getClassDeclaration().equals(var4.getTopClass().getClassDeclaration()) : this.getName().getQualifier().equals(var4.getClassDeclaration().getName().getQualifier());
            }
         } else {
            return var3.isPublic() ? true : this.getName().getQualifier().equals(var2.getName().getQualifier());
         }
      }
   }

   public boolean canAccess(Environment var1, ClassDeclaration var2) throws ClassNotFound {
      ClassDefinition var3 = var2.getClassDefinition(var1);
      if (var3.isLocal()) {
         return true;
      } else if (var3.isInnerClass()) {
         return this.canAccess(var1, var3.getInnerClassMember());
      } else {
         return var3.isPublic() ? true : this.getName().getQualifier().equals(var2.getName().getQualifier());
      }
   }

   public boolean canAccess(Environment var1, MemberDefinition var2) throws ClassNotFound {
      if (var2.isPublic()) {
         return true;
      } else if (var2.isProtected() && this.subClassOf(var1, var2.getClassDeclaration())) {
         return true;
      } else {
         return var2.isPrivate() ? this.getTopClass().getClassDeclaration().equals(var2.getTopClass().getClassDeclaration()) : this.getName().getQualifier().equals(var2.getClassDeclaration().getName().getQualifier());
      }
   }

   public boolean permitInlinedAccess(Environment var1, ClassDeclaration var2) throws ClassNotFound {
      return var1.opt() && var2.equals(this.declaration) || var1.opt_interclass() && this.canAccess(var1, var2);
   }

   public boolean permitInlinedAccess(Environment var1, MemberDefinition var2) throws ClassNotFound {
      return var1.opt() && var2.clazz.getClassDeclaration().equals(this.declaration) || var1.opt_interclass() && this.canAccess(var1, var2);
   }

   public boolean protectedAccess(Environment var1, MemberDefinition var2, Type var3) throws ClassNotFound {
      return var2.isStatic() || var3.isType(9) && var2.getName() == idClone && var2.getType().getArgumentTypes().length == 0 || var3.isType(10) && var1.getClassDefinition(var3.getClassName()).subClassOf(var1, this.getClassDeclaration()) || this.getName().getQualifier().equals(var2.getClassDeclaration().getName().getQualifier());
   }

   public MemberDefinition getAccessMember(Environment var1, Context var2, MemberDefinition var3, boolean var4) {
      throw new CompilerError("binary getAccessMember");
   }

   public MemberDefinition getUpdateMember(Environment var1, Context var2, MemberDefinition var3, boolean var4) {
      throw new CompilerError("binary getUpdateMember");
   }

   public MemberDefinition getVariable(Environment var1, Identifier var2, ClassDefinition var3) throws AmbiguousMember, ClassNotFound {
      return this.getVariable0(var1, var2, var3, true, true);
   }

   private MemberDefinition getVariable0(Environment var1, Identifier var2, ClassDefinition var3, boolean var4, boolean var5) throws AmbiguousMember, ClassNotFound {
      for(MemberDefinition var6 = this.getFirstMatch(var2); var6 != null; var6 = var6.getNextMatch()) {
         if (var6.isVariable()) {
            if (!var4 && var6.isPrivate() || !var5 && var6.isPackagePrivate()) {
               return null;
            }

            return var6;
         }
      }

      ClassDeclaration var10 = this.getSuperClass();
      MemberDefinition var7 = null;
      if (var10 != null) {
         var7 = var10.getClassDefinition(var1).getVariable0(var1, var2, var3, false, var5 && this.inSamePackage(var10));
      }

      for(int var8 = 0; var8 < this.interfaces.length; ++var8) {
         MemberDefinition var9 = this.interfaces[var8].getClassDefinition(var1).getVariable0(var1, var2, var3, true, true);
         if (var9 != null) {
            if (var7 != null && var3.canAccess(var1, var7) && var9 != var7) {
               throw new AmbiguousMember(var9, var7);
            }

            var7 = var9;
         }
      }

      return var7;
   }

   public boolean reportDeprecated(Environment var1) {
      return this.isDeprecated() || this.outerClass != null && this.outerClass.reportDeprecated(var1);
   }

   public void noteUsedBy(ClassDefinition var1, long var2, Environment var4) {
      if (this.reportDeprecated(var4)) {
         var4.error(var2, "warn.class.is.deprecated", this);
      }

   }

   public MemberDefinition getInnerClass(Environment var1, Identifier var2) throws ClassNotFound {
      for(MemberDefinition var3 = this.getFirstMatch(var2); var3 != null; var3 = var3.getNextMatch()) {
         if (var3.isInnerClass() && !var3.getInnerClass().isLocal()) {
            return var3;
         }
      }

      ClassDeclaration var4 = this.getSuperClass(var1);
      if (var4 != null) {
         return var4.getClassDefinition(var1).getInnerClass(var1, var2);
      } else {
         return null;
      }
   }

   private MemberDefinition matchMethod(Environment var1, ClassDefinition var2, Identifier var3, Type[] var4, boolean var5, Identifier var6) throws AmbiguousMember, ClassNotFound {
      if (this.allMethods != null && this.allMethods.isFrozen()) {
         MemberDefinition var7 = null;
         ArrayList var8 = null;
         Iterator var9 = this.allMethods.lookupName(var3);

         while(true) {
            MemberDefinition var10;
            while(true) {
               do {
                  if (!var9.hasNext()) {
                     if (var7 != null && var8 != null) {
                        Iterator var12 = var8.iterator();

                        while(var12.hasNext()) {
                           MemberDefinition var11 = (MemberDefinition)var12.next();
                           if (!var1.isMoreSpecific(var7, var11)) {
                              throw new AmbiguousMember(var7, var11);
                           }
                        }
                     }

                     return var7;
                  }

                  var10 = (MemberDefinition)var9.next();
               } while(!var1.isApplicable(var10, var4));

               if (var2 != null) {
                  if (!var2.canAccess(var1, var10)) {
                     continue;
                  }
                  break;
               } else if (!var5 || !var10.isPrivate() && (!var10.isPackagePrivate() || var6 == null || this.inSamePackage(var6))) {
                  break;
               }
            }

            if (var7 == null) {
               var7 = var10;
            } else if (var1.isMoreSpecific(var10, var7)) {
               var7 = var10;
            } else if (!var1.isMoreSpecific(var7, var10)) {
               if (var8 == null) {
                  var8 = new ArrayList();
               }

               var8.add(var10);
            }
         }
      } else {
         throw new CompilerError("matchMethod called early");
      }
   }

   public MemberDefinition matchMethod(Environment var1, ClassDefinition var2, Identifier var3, Type[] var4) throws AmbiguousMember, ClassNotFound {
      return this.matchMethod(var1, var2, var3, var4, false, (Identifier)null);
   }

   public MemberDefinition matchMethod(Environment var1, ClassDefinition var2, Identifier var3) throws AmbiguousMember, ClassNotFound {
      return this.matchMethod(var1, var2, var3, Type.noArgs, false, (Identifier)null);
   }

   public MemberDefinition matchAnonConstructor(Environment var1, Identifier var2, Type[] var3) throws AmbiguousMember, ClassNotFound {
      return this.matchMethod(var1, (ClassDefinition)null, idInit, var3, true, var2);
   }

   public MemberDefinition findMethod(Environment var1, Identifier var2, Type var3) throws ClassNotFound {
      for(MemberDefinition var4 = this.getFirstMatch(var2); var4 != null; var4 = var4.getNextMatch()) {
         if (var4.getType().equalArguments(var3)) {
            return var4;
         }
      }

      if (var2.equals(idInit)) {
         return null;
      } else {
         ClassDeclaration var5 = this.getSuperClass();
         if (var5 == null) {
            return null;
         } else {
            return var5.getClassDefinition(var1).findMethod(var1, var2, var3);
         }
      }
   }

   protected void basicCheck(Environment var1) throws ClassNotFound {
      if (this.outerClass != null) {
         this.outerClass.basicCheck(var1);
      }

   }

   public void check(Environment var1) throws ClassNotFound {
   }

   public Vset checkLocalClass(Environment var1, Context var2, Vset var3, ClassDefinition var4, Expression[] var5, Type[] var6) throws ClassNotFound {
      throw new CompilerError("checkLocalClass");
   }

   protected Iterator getPermanentlyAbstractMethods() {
      if (this.allMethods == null) {
         throw new CompilerError("isPermanentlyAbstract() called early");
      } else {
         return this.permanentlyAbstractMethods.iterator();
      }
   }

   public static void turnOffInheritanceChecks() {
      doInheritanceChecks = false;
   }

   private void collectOneClass(Environment var1, ClassDeclaration var2, MethodSet var3, MethodSet var4, MethodSet var5) {
      try {
         ClassDefinition var6 = var2.getClassDefinition(var1);
         Iterator var7 = var6.getMethods(var1);

         while(true) {
            while(true) {
               Object var8;
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           return;
                        }

                        var8 = (MemberDefinition)var7.next();
                     } while(((MemberDefinition)var8).isPrivate());
                  } while(((MemberDefinition)var8).isConstructor());
               } while(var6.isInterface() && !((MemberDefinition)var8).isAbstract());

               Identifier var9 = ((MemberDefinition)var8).getName();
               Type var10 = ((MemberDefinition)var8).getType();
               MemberDefinition var11 = var3.lookupSig(var9, var10);
               if (((MemberDefinition)var8).isPackagePrivate() && !this.inSamePackage(((MemberDefinition)var8).getClassDeclaration())) {
                  if (var11 != null && this instanceof SourceClass) {
                     var1.error(((MemberDefinition)var8).getWhere(), "warn.no.override.access", var11, var11.getClassDeclaration(), ((MemberDefinition)var8).getClassDeclaration());
                  }

                  if (((MemberDefinition)var8).isAbstract()) {
                     this.permanentlyAbstractMethods.add(var8);
                  }
               } else if (var11 != null) {
                  var11.checkOverride(var1, (MemberDefinition)var8);
               } else {
                  MemberDefinition var12 = var4.lookupSig(var9, var10);
                  if (var12 == null) {
                     if (var5 != null && var6.isInterface() && !this.isInterface()) {
                        var8 = new SourceMember((MemberDefinition)var8, this, var1);
                        var5.add((MemberDefinition)var8);
                     }

                     var4.add((MemberDefinition)var8);
                  } else if (this.isInterface() && !var12.isAbstract() && ((MemberDefinition)var8).isAbstract()) {
                     var4.replace((MemberDefinition)var8);
                  } else if (var12.checkMeet(var1, (MemberDefinition)var8, this.getClassDeclaration()) && !var12.couldOverride(var1, (MemberDefinition)var8)) {
                     if (((MemberDefinition)var8).couldOverride(var1, var12)) {
                        if (var5 != null && var6.isInterface() && !this.isInterface()) {
                           var8 = new SourceMember((MemberDefinition)var8, this, var1);
                           var5.replace((MemberDefinition)var8);
                        }

                        var4.replace((MemberDefinition)var8);
                     } else {
                        var1.error(this.where, "nontrivial.meet", var8, var12.getClassDefinition(), ((MemberDefinition)var8).getClassDeclaration());
                     }
                  }
               }
            }
         }
      } catch (ClassNotFound var13) {
         var1.error(this.getWhere(), "class.not.found", var13.name, this);
      }
   }

   protected void collectInheritedMethods(Environment var1) {
      if (this.allMethods != null) {
         if (!this.allMethods.isFrozen()) {
            throw new CompilerError("collectInheritedMethods()");
         }
      } else {
         MethodSet var2 = new MethodSet();
         this.allMethods = new MethodSet();
         MethodSet var3;
         if (var1.version12()) {
            var3 = null;
         } else {
            var3 = new MethodSet();
         }

         for(MemberDefinition var4 = this.getFirstMember(); var4 != null; var4 = var4.nextMember) {
            if (var4.isMethod() && !var4.isInitializer()) {
               methodSetAdd(var1, var2, var4);
               methodSetAdd(var1, this.allMethods, var4);
            }
         }

         ClassDeclaration var7 = this.getSuperClass(var1);
         if (var7 != null) {
            this.collectOneClass(var1, var7, var2, this.allMethods, var3);
            ClassDefinition var5 = var7.getClassDefinition();
            Iterator var6 = var5.getPermanentlyAbstractMethods();

            while(var6.hasNext()) {
               this.permanentlyAbstractMethods.add(var6.next());
            }
         }

         for(int var8 = 0; var8 < this.interfaces.length; ++var8) {
            this.collectOneClass(var1, this.interfaces[var8], var2, this.allMethods, var3);
         }

         this.allMethods.freeze();
         if (var3 != null && var3.size() > 0) {
            this.addMirandaMethods(var1, var3.iterator());
         }

      }
   }

   private static void methodSetAdd(Environment var0, MethodSet var1, MemberDefinition var2) {
      MemberDefinition var3 = var1.lookupSig(var2.getName(), var2.getType());
      if (var3 != null) {
         Type var4 = var3.getType().getReturnType();
         Type var5 = var2.getType().getReturnType();

         try {
            if (var0.isMoreSpecific(var5, var4)) {
               var1.replace(var2);
            }
         } catch (ClassNotFound var7) {
         }
      } else {
         var1.add(var2);
      }

   }

   public Iterator getMethods(Environment var1) {
      if (this.allMethods == null) {
         this.collectInheritedMethods(var1);
      }

      return this.getMethods();
   }

   public Iterator getMethods() {
      if (this.allMethods == null) {
         throw new CompilerError("getMethods: too early");
      } else {
         return this.allMethods.iterator();
      }
   }

   protected void addMirandaMethods(Environment var1, Iterator var2) {
   }

   public void inlineLocalClass(Environment var1) {
   }

   public void resolveTypeStructure(Environment var1) {
   }

   public Identifier resolveName(Environment var1, Identifier var2) {
      var1.dtEvent("ClassDefinition.resolveName: " + var2);
      if (var2.isQualified()) {
         Identifier var9 = this.resolveName(var1, var2.getHead());
         if (var9.hasAmbigPrefix()) {
            return var9;
         } else if (!var1.classExists(var9)) {
            return var1.resolvePackageQualifiedName(var2);
         } else {
            try {
               return var1.getClassDefinition(var9).resolveInnerClass(var1, var2.getTail());
            } catch (ClassNotFound var7) {
               return Identifier.lookupInner(var9, var2.getTail());
            }
         }
      } else {
         int var3 = -2;
         LocalMember var4 = null;
         if (this.classContext != null) {
            var4 = this.classContext.getLocalClass(var2);
            if (var4 != null) {
               var3 = var4.getScopeNumber();
            }
         }

         for(ClassDefinition var5 = this; var5 != null; var5 = var5.outerClass) {
            try {
               MemberDefinition var6 = var5.getInnerClass(var1, var2);
               if (var6 != null && (var4 == null || this.classContext.getScopeNumber(var5) > var3)) {
                  return var6.getInnerClass().getName();
               }
            } catch (ClassNotFound var8) {
            }
         }

         if (var4 != null) {
            return var4.getInnerClass().getName();
         } else {
            return var1.resolveName(var2);
         }
      }
   }

   public Identifier resolveInnerClass(Environment var1, Identifier var2) {
      if (var2.isInner()) {
         throw new CompilerError("inner");
      } else if (var2.isQualified()) {
         Identifier var7 = this.resolveInnerClass(var1, var2.getHead());

         try {
            return var1.getClassDefinition(var7).resolveInnerClass(var1, var2.getTail());
         } catch (ClassNotFound var5) {
            return Identifier.lookupInner(var7, var2.getTail());
         }
      } else {
         try {
            MemberDefinition var3 = this.getInnerClass(var1, var2);
            if (var3 != null) {
               return var3.getInnerClass().getName();
            }
         } catch (ClassNotFound var6) {
         }

         return Identifier.lookupInner(this.getName(), var2);
      }
   }

   public boolean innerClassExists(Identifier var1) {
      for(MemberDefinition var2 = this.getFirstMatch(var1.getHead()); var2 != null; var2 = var2.getNextMatch()) {
         if (var2.isInnerClass() && !var2.getInnerClass().isLocal()) {
            return !var1.isQualified() || var2.getInnerClass().innerClassExists(var1.getTail());
         }
      }

      return false;
   }

   public MemberDefinition findAnyMethod(Environment var1, Identifier var2) throws ClassNotFound {
      for(MemberDefinition var3 = this.getFirstMatch(var2); var3 != null; var3 = var3.getNextMatch()) {
         if (var3.isMethod()) {
            return var3;
         }
      }

      ClassDeclaration var4 = this.getSuperClass();
      if (var4 == null) {
         return null;
      } else {
         return var4.getClassDefinition(var1).findAnyMethod(var1, var2);
      }
   }

   public int diagnoseMismatch(Environment var1, Identifier var2, Type[] var3, int var4, Type[] var5) throws ClassNotFound {
      int[] var6 = new int[var3.length];
      Type[] var7 = new Type[var3.length];
      if (!this.diagnoseMismatch(var1, var2, var3, var4, var6, var7)) {
         return -2;
      } else {
         for(int var8 = var4; var8 < var3.length; ++var8) {
            if (var6[var8] < 4) {
               var5[0] = var7[var8];
               return var8 << 2 | var6[var8];
            }
         }

         return -1;
      }
   }

   private boolean diagnoseMismatch(Environment var1, Identifier var2, Type[] var3, int var4, int[] var5, Type[] var6) throws ClassNotFound {
      boolean var7 = false;

      for(MemberDefinition var8 = this.getFirstMatch(var2); var8 != null; var8 = var8.getNextMatch()) {
         if (var8.isMethod()) {
            Type[] var9 = var8.getType().getArgumentTypes();
            if (var9.length == var3.length) {
               var7 = true;

               for(int var10 = var4; var10 < var3.length; ++var10) {
                  Type var11 = var3[var10];
                  Type var12 = var9[var10];
                  if (var1.implicitCast(var11, var12)) {
                     var5[var10] = 4;
                  } else {
                     if (var5[var10] <= 2 && var1.explicitCast(var11, var12)) {
                        if (var5[var10] < 2) {
                           var6[var10] = null;
                        }

                        var5[var10] = 2;
                     } else if (var5[var10] > 0) {
                        continue;
                     }

                     if (var6[var10] == null) {
                        var6[var10] = var12;
                     } else if (var6[var10] != var12) {
                        var5[var10] |= 1;
                     }
                  }
               }
            }
         }
      }

      if (var2.equals(idInit)) {
         return var7;
      } else {
         ClassDeclaration var13 = this.getSuperClass();
         if (var13 != null && var13.getClassDefinition(var1).diagnoseMismatch(var1, var2, var3, var4, var5, var6)) {
            var7 = true;
         }

         return var7;
      }
   }

   public void addMember(MemberDefinition var1) {
      if (this.firstMember == null) {
         this.firstMember = this.lastMember = var1;
      } else if (var1.isSynthetic() && var1.isFinal() && var1.isVariable()) {
         var1.nextMember = this.firstMember;
         this.firstMember = var1;
         var1.nextMatch = (MemberDefinition)this.fieldHash.get(var1.name);
      } else {
         this.lastMember.nextMember = var1;
         this.lastMember = var1;
         var1.nextMatch = (MemberDefinition)this.fieldHash.get(var1.name);
      }

      this.fieldHash.put(var1.name, var1);
   }

   public void addMember(Environment var1, MemberDefinition var2) {
      this.addMember(var2);
      if (this.resolved) {
         var2.resolveTypeStructure(var1);
      }

   }

   public UplevelReference getReference(LocalMember var1) {
      for(UplevelReference var2 = this.references; var2 != null; var2 = var2.getNext()) {
         if (var2.getTarget() == var1) {
            return var2;
         }
      }

      return this.addReference(var1);
   }

   protected UplevelReference addReference(LocalMember var1) {
      if (var1.getClassDefinition() == this) {
         throw new CompilerError("addReference " + var1);
      } else {
         this.referencesMustNotBeFrozen();
         UplevelReference var2 = new UplevelReference(this, var1);
         this.references = var2.insertInto(this.references);
         return var2;
      }
   }

   public UplevelReference getReferences() {
      return this.references;
   }

   public UplevelReference getReferencesFrozen() {
      this.referencesFrozen = true;
      return this.references;
   }

   public final void referencesMustNotBeFrozen() {
      if (this.referencesFrozen) {
         throw new CompilerError("referencesMustNotBeFrozen " + this);
      }
   }

   public MemberDefinition getClassLiteralLookup(long var1) {
      throw new CompilerError("binary class");
   }

   public void addDependency(ClassDeclaration var1) {
      throw new CompilerError("addDependency");
   }

   public ClassDefinition getLocalClass(String var1) {
      return this.localClasses == null ? null : (ClassDefinition)this.localClasses.get(var1);
   }

   public void addLocalClass(ClassDefinition var1, String var2) {
      if (this.localClasses == null) {
         this.localClasses = new Hashtable(31);
      }

      this.localClasses.put(var2, var1);
   }

   public void print(PrintStream var1) {
      if (this.isPublic()) {
         var1.print("public ");
      }

      if (this.isInterface()) {
         var1.print("interface ");
      } else {
         var1.print("class ");
      }

      var1.print(this.getName() + " ");
      if (this.getSuperClass() != null) {
         var1.print("extends " + this.getSuperClass().getName() + " ");
      }

      if (this.interfaces.length > 0) {
         var1.print("implements ");

         for(int var2 = 0; var2 < this.interfaces.length; ++var2) {
            if (var2 > 0) {
               var1.print(", ");
            }

            var1.print(this.interfaces[var2].getName());
            var1.print(" ");
         }
      }

      var1.println("{");

      for(MemberDefinition var3 = this.getFirstMember(); var3 != null; var3 = var3.getNextMember()) {
         var1.print("    ");
         var3.print(var1);
      }

      var1.println("}");
   }

   public String toString() {
      return this.getClassDeclaration().toString();
   }

   public void cleanup(Environment var1) {
      if (var1.dump()) {
         var1.output("[cleanup " + this.getName() + "]");
      }

      for(MemberDefinition var2 = this.getFirstMember(); var2 != null; var2 = var2.getNextMember()) {
         var2.cleanup(var1);
      }

      this.documentation = null;
   }
}
