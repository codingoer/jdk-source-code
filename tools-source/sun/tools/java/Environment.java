package sun.tools.java;

import java.io.File;
import java.io.IOException;

public class Environment implements Constants {
   Environment env;
   String encoding;
   Object source;
   private static boolean debugging = System.getProperty("javac.debug") != null;
   private static boolean dependtrace = System.getProperty("javac.trace.depend") != null;
   private static boolean dumpmodifiers = System.getProperty("javac.dump.modifiers") != null;

   public Environment(Environment var1, Object var2) {
      if (var1 != null && var1.env != null && var1.getClass() == this.getClass()) {
         var1 = var1.env;
      }

      this.env = var1;
      this.source = var2;
   }

   public Environment() {
      this((Environment)null, (Object)null);
   }

   public boolean isExemptPackage(Identifier var1) {
      return this.env.isExemptPackage(var1);
   }

   public ClassDeclaration getClassDeclaration(Identifier var1) {
      return this.env.getClassDeclaration(var1);
   }

   public final ClassDefinition getClassDefinition(Identifier var1) throws ClassNotFound {
      if (!var1.isInner()) {
         return this.getClassDeclaration(var1).getClassDefinition(this);
      } else {
         ClassDefinition var2 = this.getClassDefinition(var1.getTopName());
         Identifier var3 = var1.getFlatName();

         label36:
         while(var3.isQualified()) {
            var3 = var3.getTail();
            Identifier var4 = var3.getHead();
            String var5 = var4.toString();
            if (var5.length() > 0 && Character.isDigit(var5.charAt(0))) {
               ClassDefinition var7 = var2.getLocalClass(var5);
               if (var7 == null) {
                  throw new ClassNotFound(Identifier.lookupInner(var2.getName(), var4));
               }

               var2 = var7;
            } else {
               for(MemberDefinition var6 = var2.getFirstMatch(var4); var6 != null; var6 = var6.getNextMatch()) {
                  if (var6.isInnerClass()) {
                     var2 = var6.getInnerClass();
                     continue label36;
                  }
               }

               throw new ClassNotFound(Identifier.lookupInner(var2.getName(), var4));
            }
         }

         return var2;
      }
   }

   public ClassDeclaration getClassDeclaration(Type var1) {
      return this.getClassDeclaration(var1.getClassName());
   }

   public final ClassDefinition getClassDefinition(Type var1) throws ClassNotFound {
      return this.getClassDefinition(var1.getClassName());
   }

   public boolean classExists(Identifier var1) {
      return this.env.classExists(var1);
   }

   public final boolean classExists(Type var1) {
      return !var1.isType(10) || this.classExists(var1.getClassName());
   }

   public Package getPackage(Identifier var1) throws IOException {
      return this.env.getPackage(var1);
   }

   public void loadDefinition(ClassDeclaration var1) {
      this.env.loadDefinition(var1);
   }

   public final Object getSource() {
      return this.source;
   }

   public boolean resolve(long var1, ClassDefinition var3, Type var4) {
      switch (var4.getTypeCode()) {
         case 9:
            return this.resolve(var1, var3, var4.getElementType());
         case 10:
            try {
               Identifier var13 = var4.getClassName();
               if (!var13.isQualified() && !var13.isInner() && !this.classExists(var13)) {
                  this.resolve(var13);
               }

               ClassDefinition var11 = this.getQualifiedClassDefinition(var1, var13, var3, false);
               if (!var3.canAccess(this, var11.getClassDeclaration())) {
                  this.error(var1, "cant.access.class", var11);
                  return true;
               }

               var11.noteUsedBy(var3, var1, this.env);
               return true;
            } catch (AmbiguousClass var9) {
               this.error(var1, "ambig.class", var9.name1, var9.name2);
               return false;
            } catch (ClassNotFound var10) {
               ClassNotFound var12 = var10;

               try {
                  if (var12.name.isInner() && this.getPackage(var12.name.getTopName()).exists()) {
                     this.env.error(var1, "class.and.package", var12.name.getTopName());
                  }
               } catch (IOException var8) {
                  this.env.error(var1, "io.exception", "package check");
               }

               this.error(var1, "class.not.found.no.context", var10.name);
               return false;
            }
         case 11:
         default:
            return true;
         case 12:
            boolean var5 = this.resolve(var1, var3, var4.getReturnType());
            Type[] var6 = var4.getArgumentTypes();

            for(int var7 = var6.length; var7-- > 0; var5 &= this.resolve(var1, var3, var6[var7])) {
            }

            return var5;
      }
   }

   public boolean resolveByName(long var1, ClassDefinition var3, Identifier var4) {
      return this.resolveByName(var1, var3, var4, false);
   }

   public boolean resolveExtendsByName(long var1, ClassDefinition var3, Identifier var4) {
      return this.resolveByName(var1, var3, var4, true);
   }

   private boolean resolveByName(long var1, ClassDefinition var3, Identifier var4, boolean var5) {
      try {
         if (!var4.isQualified() && !var4.isInner() && !this.classExists(var4)) {
            this.resolve(var4);
         }

         ClassDefinition var6 = this.getQualifiedClassDefinition(var1, var4, var3, var5);
         ClassDeclaration var12 = var6.getClassDeclaration();
         if ((var5 || !var3.canAccess(this, var12)) && (!var5 || !var3.extendsCanAccess(this, var12))) {
            this.error(var1, "cant.access.class", var6);
            return true;
         } else {
            return true;
         }
      } catch (AmbiguousClass var10) {
         this.error(var1, "ambig.class", var10.name1, var10.name2);
         return false;
      } catch (ClassNotFound var11) {
         ClassNotFound var7 = var11;

         try {
            if (var7.name.isInner() && this.getPackage(var7.name.getTopName()).exists()) {
               this.env.error(var1, "class.and.package", var7.name.getTopName());
            }
         } catch (IOException var9) {
            this.env.error(var1, "io.exception", "package check");
         }

         this.error(var1, "class.not.found", var11.name, "type name");
         return false;
      }
   }

   public final ClassDefinition getQualifiedClassDefinition(long var1, Identifier var3, ClassDefinition var4, boolean var5) throws ClassNotFound {
      if (!var3.isInner()) {
         return this.getClassDeclaration(var3).getClassDefinition(this);
      } else {
         ClassDefinition var6 = this.getClassDefinition(var3.getTopName());
         Identifier var7 = var3.getFlatName();

         label48:
         while(var7.isQualified()) {
            var7 = var7.getTail();
            Identifier var8 = var7.getHead();
            String var9 = var8.toString();
            if (var9.length() > 0 && Character.isDigit(var9.charAt(0))) {
               ClassDefinition var13 = var6.getLocalClass(var9);
               if (var13 == null) {
                  throw new ClassNotFound(Identifier.lookupInner(var6.getName(), var8));
               }

               var6 = var13;
            } else {
               for(MemberDefinition var10 = var6.getFirstMatch(var8); var10 != null; var10 = var10.getNextMatch()) {
                  if (var10.isInnerClass()) {
                     ClassDeclaration var11 = var6.getClassDeclaration();
                     var6 = var10.getInnerClass();
                     ClassDeclaration var12 = var6.getClassDeclaration();
                     if (!var5 && !var4.canAccess(this.env, var12) || var5 && !var4.extendsCanAccess(this.env, var12)) {
                        this.env.error(var1, "no.type.access", var8, var11, var4);
                     }
                     continue label48;
                  }
               }

               throw new ClassNotFound(Identifier.lookupInner(var6.getName(), var8));
            }
         }

         return var6;
      }
   }

   public Type resolveNames(ClassDefinition var1, Type var2, boolean var3) {
      this.dtEvent("Environment.resolveNames: " + var1 + ", " + var2);
      switch (var2.getTypeCode()) {
         case 9:
            var2 = Type.tArray(this.resolveNames(var1, var2.getElementType(), var3));
            break;
         case 10:
            Identifier var12 = var2.getClassName();
            Identifier var13;
            if (var3) {
               var13 = this.resolvePackageQualifiedName(var12);
            } else {
               var13 = var1.resolveName(this, var12);
            }

            if (var12 != var13) {
               var2 = Type.tClass(var13);
            }
         case 11:
         default:
            break;
         case 12:
            Type var4 = var2.getReturnType();
            Type var5 = this.resolveNames(var1, var4, var3);
            Type[] var6 = var2.getArgumentTypes();
            Type[] var7 = new Type[var6.length];
            boolean var8 = var4 != var5;
            int var9 = var6.length;

            while(var9-- > 0) {
               Type var10 = var6[var9];
               Type var11 = this.resolveNames(var1, var10, var3);
               var7[var9] = var11;
               if (var10 != var11) {
                  var8 = true;
               }
            }

            if (var8) {
               var2 = Type.tMethod(var5, var7);
            }
      }

      return var2;
   }

   public Identifier resolveName(Identifier var1) {
      if (var1.isQualified()) {
         Identifier var2 = this.resolveName(var1.getHead());
         if (var2.hasAmbigPrefix()) {
            return var2;
         } else if (!this.classExists(var2)) {
            return this.resolvePackageQualifiedName(var1);
         } else {
            try {
               return this.getClassDefinition(var2).resolveInnerClass(this, var1.getTail());
            } catch (ClassNotFound var4) {
               return Identifier.lookupInner(var2, var1.getTail());
            }
         }
      } else {
         try {
            return this.resolve(var1);
         } catch (AmbiguousClass var5) {
            return var1.hasAmbigPrefix() ? var1 : var1.addAmbigPrefix();
         } catch (ClassNotFound var6) {
            Imports var3 = this.getImports();
            return var3 != null ? var3.forceResolve(this, var1) : var1;
         }
      }
   }

   public final Identifier resolvePackageQualifiedName(Identifier var1) {
      Identifier var2;
      for(var2 = null; !this.classExists(var1); var1 = var1.getQualifier()) {
         if (!var1.isQualified()) {
            var1 = var2 == null ? var1 : Identifier.lookup(var1, var2);
            var2 = null;
            break;
         }

         Identifier var3 = var1.getName();
         var2 = var2 == null ? var3 : Identifier.lookup(var3, var2);
      }

      if (var2 != null) {
         var1 = Identifier.lookupInner(var1, var2);
      }

      return var1;
   }

   public Identifier resolve(Identifier var1) throws ClassNotFound {
      return this.env == null ? var1 : this.env.resolve(var1);
   }

   public Imports getImports() {
      return this.env == null ? null : this.env.getImports();
   }

   public ClassDefinition makeClassDefinition(Environment var1, long var2, IdentifierToken var4, String var5, int var6, IdentifierToken var7, IdentifierToken[] var8, ClassDefinition var9) {
      return this.env == null ? null : this.env.makeClassDefinition(var1, var2, var4, var5, var6, var7, var8, var9);
   }

   public MemberDefinition makeMemberDefinition(Environment var1, long var2, ClassDefinition var4, String var5, int var6, Type var7, Identifier var8, IdentifierToken[] var9, IdentifierToken[] var10, Object var11) {
      return this.env == null ? null : this.env.makeMemberDefinition(var1, var2, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public boolean isApplicable(MemberDefinition var1, Type[] var2) throws ClassNotFound {
      Type var3 = var1.getType();
      if (!var3.isType(12)) {
         return false;
      } else {
         Type[] var4 = var3.getArgumentTypes();
         if (var2.length != var4.length) {
            return false;
         } else {
            int var5 = var2.length;

            do {
               --var5;
               if (var5 < 0) {
                  return true;
               }
            } while(this.isMoreSpecific(var2[var5], var4[var5]));

            return false;
         }
      }
   }

   public boolean isMoreSpecific(MemberDefinition var1, MemberDefinition var2) throws ClassNotFound {
      Type var3 = var1.getClassDeclaration().getType();
      Type var4 = var2.getClassDeclaration().getType();
      boolean var5 = this.isMoreSpecific(var3, var4) && this.isApplicable(var2, var1.getType().getArgumentTypes());
      return var5;
   }

   public boolean isMoreSpecific(Type var1, Type var2) throws ClassNotFound {
      return this.implicitCast(var1, var2);
   }

   public boolean implicitCast(Type var1, Type var2) throws ClassNotFound {
      if (var1 == var2) {
         return true;
      } else {
         int var3 = var2.getTypeCode();
         switch (var1.getTypeCode()) {
            case 1:
               if (var3 == 3) {
                  return true;
               }
            case 2:
            case 3:
               if (var3 == 4) {
                  return true;
               }
            case 4:
               if (var3 == 5) {
                  return true;
               }
            case 5:
               if (var3 == 6) {
                  return true;
               }
            case 6:
               if (var3 == 7) {
                  return true;
               }
            case 7:
            default:
               return false;
            case 8:
               return var2.inMask(1792);
            case 9:
               if (!var2.isType(9)) {
                  return var2 == Type.tObject || var2 == Type.tCloneable || var2 == Type.tSerializable;
               } else {
                  do {
                     var1 = var1.getElementType();
                     var2 = var2.getElementType();
                  } while(var1.isType(9) && var2.isType(9));

                  if (var1.inMask(1536) && var2.inMask(1536)) {
                     return this.isMoreSpecific(var1, var2);
                  } else {
                     return var1.getTypeCode() == var2.getTypeCode();
                  }
               }
            case 10:
               if (var3 == 10) {
                  ClassDefinition var4 = this.getClassDefinition(var1);
                  ClassDefinition var5 = this.getClassDefinition(var2);
                  return var5.implementedBy(this, var4.getClassDeclaration());
               } else {
                  return false;
               }
         }
      }
   }

   public boolean explicitCast(Type var1, Type var2) throws ClassNotFound {
      if (this.implicitCast(var1, var2)) {
         return true;
      } else if (var1.inMask(254)) {
         return var2.inMask(254);
      } else if (var1.isType(10) && var2.isType(10)) {
         ClassDefinition var5 = this.getClassDefinition(var1);
         ClassDefinition var6 = this.getClassDefinition(var2);
         if (var6.isFinal()) {
            return var5.implementedBy(this, var6.getClassDeclaration());
         } else if (var5.isFinal()) {
            return var6.implementedBy(this, var5.getClassDeclaration());
         } else if (var6.isInterface() && var5.isInterface()) {
            return var6.couldImplement(var5);
         } else {
            return var6.isInterface() || var5.isInterface() || var5.superClassOf(this, var6.getClassDeclaration());
         }
      } else {
         if (var2.isType(9)) {
            if (var1.isType(9)) {
               Type var3 = var1.getElementType();

               Type var4;
               for(var4 = var2.getElementType(); var3.getTypeCode() == 9 && var4.getTypeCode() == 9; var4 = var4.getElementType()) {
                  var3 = var3.getElementType();
               }

               if (var3.inMask(1536) && var4.inMask(1536)) {
                  return this.explicitCast(var3, var4);
               }
            } else if (var1 == Type.tObject || var1 == Type.tCloneable || var1 == Type.tSerializable) {
               return true;
            }
         }

         return false;
      }
   }

   public int getFlags() {
      return this.env.getFlags();
   }

   public final boolean debug_lines() {
      return (this.getFlags() & 4096) != 0;
   }

   public final boolean debug_vars() {
      return (this.getFlags() & 8192) != 0;
   }

   public final boolean debug_source() {
      return (this.getFlags() & 262144) != 0;
   }

   public final boolean opt() {
      return (this.getFlags() & 16384) != 0;
   }

   public final boolean opt_interclass() {
      return (this.getFlags() & '耀') != 0;
   }

   public final boolean verbose() {
      return (this.getFlags() & 1) != 0;
   }

   public final boolean dump() {
      return (this.getFlags() & 2) != 0;
   }

   public final boolean warnings() {
      return (this.getFlags() & 4) != 0;
   }

   public final boolean dependencies() {
      return (this.getFlags() & 32) != 0;
   }

   public final boolean print_dependencies() {
      return (this.getFlags() & 1024) != 0;
   }

   public final boolean deprecation() {
      return (this.getFlags() & 512) != 0;
   }

   public final boolean version12() {
      return (this.getFlags() & 2048) != 0;
   }

   public final boolean strictdefault() {
      return (this.getFlags() & 131072) != 0;
   }

   public void shutdown() {
      if (this.env != null) {
         this.env.shutdown();
      }

   }

   public void error(Object var1, long var2, String var4, Object var5, Object var6, Object var7) {
      this.env.error(var1, var2, var4, var5, var6, var7);
   }

   public final void error(long var1, String var3, Object var4, Object var5, Object var6) {
      this.error(this.source, var1, var3, var4, var5, var6);
   }

   public final void error(long var1, String var3, Object var4, Object var5) {
      this.error(this.source, var1, var3, var4, var5, (Object)null);
   }

   public final void error(long var1, String var3, Object var4) {
      this.error(this.source, var1, var3, var4, (Object)null, (Object)null);
   }

   public final void error(long var1, String var3) {
      this.error(this.source, var1, var3, (Object)null, (Object)null, (Object)null);
   }

   public void output(String var1) {
      this.env.output(var1);
   }

   public static void debugOutput(Object var0) {
      if (debugging) {
         System.out.println(var0.toString());
      }

   }

   public void setCharacterEncoding(String var1) {
      this.encoding = var1;
   }

   public String getCharacterEncoding() {
      return this.encoding;
   }

   public short getMajorVersion() {
      return this.env == null ? 45 : this.env.getMajorVersion();
   }

   public short getMinorVersion() {
      return this.env == null ? 3 : this.env.getMinorVersion();
   }

   public final boolean coverage() {
      return (this.getFlags() & 64) != 0;
   }

   public final boolean covdata() {
      return (this.getFlags() & 128) != 0;
   }

   public File getcovFile() {
      return this.env.getcovFile();
   }

   public void dtEnter(String var1) {
      if (dependtrace) {
         System.out.println(">>> " + var1);
      }

   }

   public void dtExit(String var1) {
      if (dependtrace) {
         System.out.println("<<< " + var1);
      }

   }

   public void dtEvent(String var1) {
      if (dependtrace) {
         System.out.println(var1);
      }

   }

   public boolean dumpModifiers() {
      return dumpmodifiers;
   }
}
