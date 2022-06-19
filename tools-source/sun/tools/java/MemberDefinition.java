package sun.tools.java;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import sun.tools.asm.Assembler;
import sun.tools.javac.SourceMember;
import sun.tools.tree.Context;
import sun.tools.tree.Expression;
import sun.tools.tree.Node;
import sun.tools.tree.Statement;
import sun.tools.tree.Vset;

public class MemberDefinition implements Constants {
   protected long where;
   protected int modifiers;
   protected Type type;
   protected String documentation;
   protected IdentifierToken[] expIds;
   protected ClassDeclaration[] exp;
   protected Node value;
   protected ClassDefinition clazz;
   protected Identifier name;
   protected ClassDefinition innerClass;
   protected MemberDefinition nextMember;
   protected MemberDefinition nextMatch;
   protected MemberDefinition accessPeer;
   protected boolean superAccessMethod;
   private static Map proxyCache;
   static final int PUBLIC_ACCESS = 1;
   static final int PROTECTED_ACCESS = 2;
   static final int PACKAGE_ACCESS = 3;
   static final int PRIVATE_ACCESS = 4;

   public MemberDefinition(long var1, ClassDefinition var3, int var4, Type var5, Identifier var6, IdentifierToken[] var7, Node var8) {
      if (var7 == null) {
         var7 = new IdentifierToken[0];
      }

      this.where = var1;
      this.clazz = var3;
      this.modifiers = var4;
      this.type = var5;
      this.name = var6;
      this.expIds = var7;
      this.value = var8;
   }

   public MemberDefinition(ClassDefinition var1) {
      this(var1.getWhere(), var1.getOuterClass(), var1.getModifiers(), var1.getType(), var1.getName().getFlatName().getName(), (IdentifierToken[])null, (Node)null);
      this.innerClass = var1;
   }

   public static MemberDefinition makeProxyMember(MemberDefinition var0, ClassDefinition var1, Environment var2) {
      if (proxyCache == null) {
         proxyCache = new HashMap();
      }

      String var3 = var0.toString() + "@" + var1.toString();
      MemberDefinition var4 = (MemberDefinition)proxyCache.get(var3);
      if (var4 != null) {
         return var4;
      } else {
         var4 = new MemberDefinition(var0.getWhere(), var1, var0.getModifiers(), var0.getType(), var0.getName(), var0.getExceptionIds(), (Node)null);
         var4.exp = var0.getExceptions(var2);
         proxyCache.put(var3, var4);
         return var4;
      }
   }

   public final long getWhere() {
      return this.where;
   }

   public final ClassDeclaration getClassDeclaration() {
      return this.clazz.getClassDeclaration();
   }

   public void resolveTypeStructure(Environment var1) {
   }

   public ClassDeclaration getDefiningClassDeclaration() {
      return this.getClassDeclaration();
   }

   public final ClassDefinition getClassDefinition() {
      return this.clazz;
   }

   public final ClassDefinition getTopClass() {
      return this.clazz.getTopClass();
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

   public final Type getType() {
      return this.type;
   }

   public final Identifier getName() {
      return this.name;
   }

   public Vector getArguments() {
      return this.isMethod() ? new Vector() : null;
   }

   public ClassDeclaration[] getExceptions(Environment var1) {
      if (this.expIds != null && this.exp == null) {
         if (this.expIds.length != 0) {
            throw new CompilerError("getExceptions " + this);
         }

         this.exp = new ClassDeclaration[0];
      }

      return this.exp;
   }

   public final IdentifierToken[] getExceptionIds() {
      return this.expIds;
   }

   public ClassDefinition getInnerClass() {
      return this.innerClass;
   }

   public boolean isUplevelValue() {
      if (this.isSynthetic() && this.isVariable() && !this.isStatic()) {
         String var1 = this.name.toString();
         return var1.startsWith("val$") || var1.startsWith("loc$") || var1.startsWith("this$");
      } else {
         return false;
      }
   }

   public boolean isAccessMethod() {
      return this.isSynthetic() && this.isMethod() && this.accessPeer != null;
   }

   public MemberDefinition getAccessMethodTarget() {
      if (this.isAccessMethod()) {
         for(MemberDefinition var1 = this.accessPeer; var1 != null; var1 = var1.accessPeer) {
            if (!var1.isAccessMethod()) {
               return var1;
            }
         }
      }

      return null;
   }

   public void setAccessMethodTarget(MemberDefinition var1) {
      if (this.getAccessMethodTarget() != var1) {
         if (this.accessPeer != null || var1.accessPeer != null) {
            throw new CompilerError("accessPeer");
         }

         this.accessPeer = var1;
      }

   }

   public MemberDefinition getAccessUpdateMember() {
      if (this.isAccessMethod()) {
         for(MemberDefinition var1 = this.accessPeer; var1 != null; var1 = var1.accessPeer) {
            if (var1.isAccessMethod()) {
               return var1;
            }
         }
      }

      return null;
   }

   public void setAccessUpdateMember(MemberDefinition var1) {
      if (this.getAccessUpdateMember() != var1) {
         if (!this.isAccessMethod() || var1.getAccessMethodTarget() != this.getAccessMethodTarget()) {
            throw new CompilerError("accessPeer");
         }

         var1.accessPeer = this.accessPeer;
         this.accessPeer = var1;
      }

   }

   public final boolean isSuperAccessMethod() {
      return this.superAccessMethod;
   }

   public final void setIsSuperAccessMethod(boolean var1) {
      this.superAccessMethod = var1;
   }

   public final boolean isBlankFinal() {
      return this.isFinal() && !this.isSynthetic() && this.getValue() == null;
   }

   public boolean isNeverNull() {
      if (this.isUplevelValue()) {
         return !this.name.toString().startsWith("val$");
      } else {
         return false;
      }
   }

   public Node getValue(Environment var1) throws ClassNotFound {
      return this.value;
   }

   public final Node getValue() {
      return this.value;
   }

   public final void setValue(Node var1) {
      this.value = var1;
   }

   public Object getInitialValue() {
      return null;
   }

   public final MemberDefinition getNextMember() {
      return this.nextMember;
   }

   public final MemberDefinition getNextMatch() {
      return this.nextMatch;
   }

   public String getDocumentation() {
      return this.documentation;
   }

   public void check(Environment var1) throws ClassNotFound {
   }

   public Vset check(Environment var1, Context var2, Vset var3) throws ClassNotFound {
      return var3;
   }

   public void code(Environment var1, Assembler var2) throws ClassNotFound {
      throw new CompilerError("code");
   }

   public void codeInit(Environment var1, Context var2, Assembler var3) throws ClassNotFound {
      throw new CompilerError("codeInit");
   }

   public boolean reportDeprecated(Environment var1) {
      return this.isDeprecated() || this.clazz.reportDeprecated(var1);
   }

   public final boolean canReach(Environment var1, MemberDefinition var2) {
      if (!var2.isLocal() && var2.isVariable() && (this.isVariable() || this.isInitializer())) {
         if (this.getClassDeclaration().equals(var2.getClassDeclaration()) && this.isStatic() == var2.isStatic()) {
            while((var2 = var2.getNextMember()) != null && var2 != this) {
            }

            return var2 != null;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private int getAccessLevel() {
      if (this.isPublic()) {
         return 1;
      } else if (this.isProtected()) {
         return 2;
      } else if (this.isPackagePrivate()) {
         return 3;
      } else if (this.isPrivate()) {
         return 4;
      } else {
         throw new CompilerError("getAccessLevel()");
      }
   }

   private void reportError(Environment var1, String var2, ClassDeclaration var3, MemberDefinition var4) {
      if (var3 == null) {
         var1.error(this.getWhere(), var2, this, this.getClassDeclaration(), var4.getClassDeclaration());
      } else {
         var1.error(var3.getClassDefinition().getWhere(), var2, this, this.getClassDeclaration(), var4.getClassDeclaration());
      }

   }

   public boolean sameReturnType(MemberDefinition var1) {
      if (this.isMethod() && var1.isMethod()) {
         Type var2 = this.getType().getReturnType();
         Type var3 = var1.getType().getReturnType();
         return var2 == var3;
      } else {
         throw new CompilerError("sameReturnType: not method");
      }
   }

   public boolean checkOverride(Environment var1, MemberDefinition var2) {
      return this.checkOverride(var1, var2, (ClassDeclaration)null);
   }

   private boolean checkOverride(Environment var1, MemberDefinition var2, ClassDeclaration var3) {
      boolean var4 = true;
      if (!this.isMethod()) {
         throw new CompilerError("checkOverride(), expected method");
      } else if (this.isSynthetic()) {
         if (!var2.isFinal() && !var2.isConstructor() && !var2.isStatic() && !this.isStatic()) {
         }

         return true;
      } else if (this.getName() == var2.getName() && this.getType().equalArguments(var2.getType())) {
         if (var2.isStatic() && !this.isStatic()) {
            this.reportError(var1, "override.static.with.instance", var3, var2);
            var4 = false;
         }

         if (!var2.isStatic() && this.isStatic()) {
            this.reportError(var1, "hide.instance.with.static", var3, var2);
            var4 = false;
         }

         if (var2.isFinal()) {
            this.reportError(var1, "override.final.method", var3, var2);
            var4 = false;
         }

         if (var2.reportDeprecated(var1) && !this.isDeprecated() && this instanceof SourceMember) {
            this.reportError(var1, "warn.override.is.deprecated", var3, var2);
         }

         if (this.getAccessLevel() > var2.getAccessLevel()) {
            this.reportError(var1, "override.more.restrictive", var3, var2);
            var4 = false;
         }

         if (!this.sameReturnType(var2)) {
         }

         if (!this.exceptionsFit(var1, var2)) {
            this.reportError(var1, "override.incompatible.exceptions", var3, var2);
            var4 = false;
         }

         return var4;
      } else {
         throw new CompilerError("checkOverride(), signature mismatch");
      }
   }

   public boolean checkMeet(Environment var1, MemberDefinition var2, ClassDeclaration var3) {
      if (!this.isMethod()) {
         throw new CompilerError("checkMeet(), expected method");
      } else if (!this.isAbstract() && !var2.isAbstract()) {
         throw new CompilerError("checkMeet(), no abstract method");
      } else if (!this.isAbstract()) {
         return this.checkOverride(var1, var2, var3);
      } else if (!var2.isAbstract()) {
         return var2.checkOverride(var1, this, var3);
      } else if (this.getName() == var2.getName() && this.getType().equalArguments(var2.getType())) {
         if (!this.sameReturnType(var2)) {
            var1.error(var3.getClassDefinition().getWhere(), "meet.different.return", this, this.getClassDeclaration(), var2.getClassDeclaration());
            return false;
         } else {
            return true;
         }
      } else {
         throw new CompilerError("checkMeet(), signature mismatch");
      }
   }

   public boolean couldOverride(Environment var1, MemberDefinition var2) {
      if (!this.isMethod()) {
         throw new CompilerError("coulcOverride(), expected method");
      } else if (!var2.isAbstract()) {
         return false;
      } else if (this.getAccessLevel() > var2.getAccessLevel()) {
         return false;
      } else {
         return this.exceptionsFit(var1, var2);
      }
   }

   private boolean exceptionsFit(Environment var1, MemberDefinition var2) {
      ClassDeclaration[] var3 = this.getExceptions(var1);
      ClassDeclaration[] var4 = var2.getExceptions(var1);

      label39:
      for(int var5 = 0; var5 < var3.length; ++var5) {
         try {
            ClassDefinition var6 = var3[var5].getClassDefinition(var1);

            for(int var7 = 0; var7 < var4.length; ++var7) {
               if (var6.subClassOf(var1, var4[var7])) {
                  continue label39;
               }
            }

            if (!var6.subClassOf(var1, var1.getClassDeclaration(idJavaLangError)) && !var6.subClassOf(var1, var1.getClassDeclaration(idJavaLangRuntimeException))) {
               return false;
            }
         } catch (ClassNotFound var8) {
            var1.error(this.getWhere(), "class.not.found", var8.name, var2.getClassDeclaration());
         }
      }

      return true;
   }

   public final boolean isPublic() {
      return (this.modifiers & 1) != 0;
   }

   public final boolean isPrivate() {
      return (this.modifiers & 2) != 0;
   }

   public final boolean isProtected() {
      return (this.modifiers & 4) != 0;
   }

   public final boolean isPackagePrivate() {
      return (this.modifiers & 7) == 0;
   }

   public final boolean isFinal() {
      return (this.modifiers & 16) != 0;
   }

   public final boolean isStatic() {
      return (this.modifiers & 8) != 0;
   }

   public final boolean isSynchronized() {
      return (this.modifiers & 32) != 0;
   }

   public final boolean isAbstract() {
      return (this.modifiers & 1024) != 0;
   }

   public final boolean isNative() {
      return (this.modifiers & 256) != 0;
   }

   public final boolean isVolatile() {
      return (this.modifiers & 64) != 0;
   }

   public final boolean isTransient() {
      return (this.modifiers & 128) != 0;
   }

   public final boolean isMethod() {
      return this.type.isType(12);
   }

   public final boolean isVariable() {
      return !this.type.isType(12) && this.innerClass == null;
   }

   public final boolean isSynthetic() {
      return (this.modifiers & 524288) != 0;
   }

   public final boolean isDeprecated() {
      return (this.modifiers & 262144) != 0;
   }

   public final boolean isStrict() {
      return (this.modifiers & 2097152) != 0;
   }

   public final boolean isInnerClass() {
      return this.innerClass != null;
   }

   public final boolean isInitializer() {
      return this.getName().equals(idClassInit);
   }

   public final boolean isConstructor() {
      return this.getName().equals(idInit);
   }

   public boolean isLocal() {
      return false;
   }

   public boolean isInlineable(Environment var1, boolean var2) throws ClassNotFound {
      return (this.isStatic() || this.isPrivate() || this.isFinal() || this.isConstructor() || var2) && !this.isSynchronized() && !this.isNative();
   }

   public boolean isConstant() {
      if (this.isFinal() && this.isVariable() && this.value != null) {
         boolean var1;
         try {
            this.modifiers &= -17;
            var1 = ((Expression)this.value).isConstant();
         } finally {
            this.modifiers |= 16;
         }

         return var1;
      } else {
         return false;
      }
   }

   public String toString() {
      Identifier var1 = this.getClassDefinition().getName();
      if (this.isInitializer()) {
         return this.isStatic() ? "static {}" : "instance {}";
      } else if (this.isConstructor()) {
         StringBuffer var2 = new StringBuffer();
         var2.append(var1);
         var2.append('(');
         Type[] var3 = this.getType().getArgumentTypes();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var4 > 0) {
               var2.append(',');
            }

            var2.append(var3[var4].toString());
         }

         var2.append(')');
         return var2.toString();
      } else {
         return this.isInnerClass() ? this.getInnerClass().toString() : this.type.typeString(this.getName().toString());
      }
   }

   public void print(PrintStream var1) {
      if (this.isPublic()) {
         var1.print("public ");
      }

      if (this.isPrivate()) {
         var1.print("private ");
      }

      if (this.isProtected()) {
         var1.print("protected ");
      }

      if (this.isFinal()) {
         var1.print("final ");
      }

      if (this.isStatic()) {
         var1.print("static ");
      }

      if (this.isSynchronized()) {
         var1.print("synchronized ");
      }

      if (this.isAbstract()) {
         var1.print("abstract ");
      }

      if (this.isNative()) {
         var1.print("native ");
      }

      if (this.isVolatile()) {
         var1.print("volatile ");
      }

      if (this.isTransient()) {
         var1.print("transient ");
      }

      var1.println(this.toString() + ";");
   }

   public void cleanup(Environment var1) {
      this.documentation = null;
      if (this.isMethod() && this.value != null) {
         int var2 = 0;
         if (!this.isPrivate() && !this.isInitializer()) {
            if ((var2 = ((Statement)this.value).costInline(Statement.MAXINLINECOST, (Environment)null, (Context)null)) >= Statement.MAXINLINECOST) {
               this.value = Statement.empty;
            } else {
               try {
                  if (!this.isInlineable((Environment)null, true)) {
                     this.value = Statement.empty;
                  }
               } catch (ClassNotFound var4) {
               }
            }
         } else {
            this.value = Statement.empty;
         }

         if (this.value != Statement.empty && var1.dump()) {
            var1.output("[after cleanup of " + this.getName() + ", " + var2 + " expression cost units remain]");
         }
      } else if (this.isVariable() && (this.isPrivate() || !this.isFinal() || this.type.isType(9))) {
         this.value = null;
      }

   }
}
