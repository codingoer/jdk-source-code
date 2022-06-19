package sun.tools.javac;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.tools.asm.Assembler;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;
import sun.tools.tree.Context;
import sun.tools.tree.Expression;
import sun.tools.tree.ExpressionStatement;
import sun.tools.tree.LocalMember;
import sun.tools.tree.MethodExpression;
import sun.tools.tree.Node;
import sun.tools.tree.NullExpression;
import sun.tools.tree.Statement;
import sun.tools.tree.SuperExpression;
import sun.tools.tree.UplevelReference;
import sun.tools.tree.Vset;

/** @deprecated */
@Deprecated
public class SourceMember extends MemberDefinition implements Constants {
   Vector args;
   MemberDefinition abstractSource;
   int status;
   static final int PARSED = 0;
   static final int CHECKING = 1;
   static final int CHECKED = 2;
   static final int INLINING = 3;
   static final int INLINED = 4;
   static final int ERROR = 5;
   LocalMember outerThisArg;
   public boolean resolved;

   public Vector getArguments() {
      return this.args;
   }

   public SourceMember(long var1, ClassDefinition var3, String var4, int var5, Type var6, Identifier var7, Vector var8, IdentifierToken[] var9, Node var10) {
      super(var1, var3, var5, var6, var7, var9, var10);
      this.outerThisArg = null;
      this.resolved = false;
      this.documentation = var4;
      this.args = var8;
      if (ClassDefinition.containsDeprecated(this.documentation)) {
         this.modifiers |= 262144;
      }

   }

   void createArgumentFields(Vector var1) {
      if (this.isMethod()) {
         this.args = new Vector();
         if (this.isConstructor() || !this.isStatic() && !this.isInitializer()) {
            this.args.addElement(((SourceClass)this.clazz).getThisArgument());
         }

         if (var1 != null) {
            Enumeration var2 = var1.elements();
            Type[] var3 = this.getType().getArgumentTypes();

            for(int var4 = 0; var4 < var3.length; ++var4) {
               Object var5 = var2.nextElement();
               if (var5 instanceof LocalMember) {
                  this.args = var1;
                  return;
               }

               Identifier var6;
               int var7;
               long var8;
               if (var5 instanceof Identifier) {
                  var6 = (Identifier)var5;
                  var7 = 0;
                  var8 = this.getWhere();
               } else {
                  IdentifierToken var10 = (IdentifierToken)var5;
                  var6 = var10.getName();
                  var7 = var10.getModifiers();
                  var8 = var10.getWhere();
               }

               this.args.addElement(new LocalMember(var8, this.clazz, var7, var3[var4], var6));
            }
         }
      }

   }

   public LocalMember getOuterThisArg() {
      return this.outerThisArg;
   }

   void addOuterThis() {
      UplevelReference var1;
      for(var1 = this.clazz.getReferences(); var1 != null && !var1.isClientOuterField(); var1 = var1.getNext()) {
      }

      if (var1 != null) {
         Type[] var2 = this.type.getArgumentTypes();
         Type[] var3 = new Type[var2.length + 1];
         LocalMember var4 = var1.getLocalArgument();
         this.outerThisArg = var4;
         this.args.insertElementAt(var4, 1);
         var3[0] = var4.getType();

         for(int var5 = 0; var5 < var2.length; ++var5) {
            var3[var5 + 1] = var2[var5];
         }

         this.type = Type.tMethod(this.type.getReturnType(), var3);
      }
   }

   void addUplevelArguments() {
      UplevelReference var1 = this.clazz.getReferences();
      this.clazz.getReferencesFrozen();
      int var2 = 0;

      for(UplevelReference var3 = var1; var3 != null; var3 = var3.getNext()) {
         if (!var3.isClientOuterField()) {
            ++var2;
         }
      }

      if (var2 != 0) {
         Type[] var8 = this.type.getArgumentTypes();
         Type[] var4 = new Type[var8.length + var2];
         int var5 = 0;

         for(UplevelReference var6 = var1; var6 != null; var6 = var6.getNext()) {
            if (!var6.isClientOuterField()) {
               LocalMember var7 = var6.getLocalArgument();
               this.args.insertElementAt(var7, 1 + var5);
               var4[var5] = var7.getType();
               ++var5;
            }
         }

         for(int var9 = 0; var9 < var8.length; ++var9) {
            var4[var5 + var9] = var8[var9];
         }

         this.type = Type.tMethod(this.type.getReturnType(), var4);
      }
   }

   public SourceMember(ClassDefinition var1) {
      super(var1);
      this.outerThisArg = null;
      this.resolved = false;
   }

   public SourceMember(MemberDefinition var1, ClassDefinition var2, Environment var3) {
      this(var1.getWhere(), var2, var1.getDocumentation(), var1.getModifiers() | 1024, var1.getType(), var1.getName(), (Vector)null, var1.getExceptionIds(), (Node)null);
      this.args = var1.getArguments();
      this.abstractSource = var1;
      this.exp = var1.getExceptions(var3);
   }

   public ClassDeclaration[] getExceptions(Environment var1) {
      if (this.isMethod() && this.exp == null) {
         if (this.expIds == null) {
            this.exp = new ClassDeclaration[0];
            return this.exp;
         } else {
            var1 = ((SourceClass)this.getClassDefinition()).setupEnv(var1);
            this.exp = new ClassDeclaration[this.expIds.length];

            for(int var2 = 0; var2 < this.exp.length; ++var2) {
               Identifier var3 = this.expIds[var2].getName();
               Identifier var4 = this.getClassDefinition().resolveName(var1, var3);
               this.exp[var2] = var1.getClassDeclaration(var4);
            }

            return this.exp;
         }
      } else {
         return this.exp;
      }
   }

   public void setExceptions(ClassDeclaration[] var1) {
      this.exp = var1;
   }

   public void resolveTypeStructure(Environment var1) {
      var1.dtEnter("SourceMember.resolveTypeStructure: " + this);
      if (this.resolved) {
         var1.dtEvent("SourceMember.resolveTypeStructure: OK " + this);
         throw new CompilerError("multiple member type resolution");
      } else {
         var1.dtEvent("SourceMember.resolveTypeStructure: RESOLVING " + this);
         this.resolved = true;
         super.resolveTypeStructure(var1);
         if (this.isInnerClass()) {
            ClassDefinition var2 = this.getInnerClass();
            if (var2 instanceof SourceClass && !var2.isLocal()) {
               ((SourceClass)var2).resolveTypeStructure(var1);
            }

            this.type = this.innerClass.getType();
         } else {
            this.type = var1.resolveNames(this.getClassDefinition(), this.type, this.isSynthetic());
            this.getExceptions(var1);
            if (this.isMethod()) {
               Vector var3 = this.args;
               this.args = null;
               this.createArgumentFields(var3);
               if (this.isConstructor()) {
                  this.addOuterThis();
               }
            }
         }

         var1.dtExit("SourceMember.resolveTypeStructure: " + this);
      }
   }

   public ClassDeclaration getDefiningClassDeclaration() {
      return this.abstractSource == null ? super.getDefiningClassDeclaration() : this.abstractSource.getDefiningClassDeclaration();
   }

   public boolean reportDeprecated(Environment var1) {
      return false;
   }

   public void check(Environment var1) throws ClassNotFound {
      var1.dtEnter("SourceMember.check: " + this.getName() + ", status = " + this.status);
      if (this.status == 0) {
         if (this.isSynthetic() && this.getValue() == null) {
            this.status = 2;
            var1.dtExit("SourceMember.check: BREAKING CYCLE");
            return;
         }

         var1.dtEvent("SourceMember.check: CHECKING CLASS");
         this.clazz.check(var1);
         if (this.status == 0) {
            if (!this.getClassDefinition().getError()) {
               var1.dtExit("SourceMember.check: CHECK FAILED");
               throw new CompilerError("check failed");
            }

            this.status = 5;
         }
      }

      var1.dtExit("SourceMember.check: DONE " + this.getName() + ", status = " + this.status);
   }

   public Vset check(Environment var1, Context var2, Vset var3) throws ClassNotFound {
      var1.dtEvent("SourceMember.check: MEMBER " + this.getName() + ", status = " + this.status);
      if (this.status == 0) {
         if (this.isInnerClass()) {
            ClassDefinition var23 = this.getInnerClass();
            if (var23 instanceof SourceClass && !var23.isLocal() && var23.isInsideLocal()) {
               this.status = 1;
               var3 = ((SourceClass)var23).checkInsideClass(var1, var2, var3);
            }

            this.status = 2;
            return var3;
         }

         if (var1.dump()) {
            System.out.println("[check field " + this.getClassDeclaration().getName() + "." + this.getName() + "]");
            if (this.getValue() != null) {
               this.getValue().print(System.out);
               System.out.println();
            }
         }

         var1 = new Environment(var1, this);
         var1.resolve(this.where, this.getClassDefinition(), this.getType());
         ClassDeclaration[] var5;
         int var6;
         if (this.isMethod()) {
            ClassDeclaration var4 = var1.getClassDeclaration(idJavaLangThrowable);
            var5 = this.getExceptions(var1);

            for(var6 = 0; var6 < var5.length; ++var6) {
               long var8 = this.getWhere();
               if (this.expIds != null && var6 < this.expIds.length) {
                  var8 = IdentifierToken.getWhere(this.expIds[var6], var8);
               }

               ClassDefinition var7;
               try {
                  var7 = var5[var6].getClassDefinition(var1);
                  var1.resolveByName(var8, this.getClassDefinition(), var7.getName());
               } catch (ClassNotFound var18) {
                  var1.error(var8, "class.not.found", var18.name, "throws");
                  break;
               }

               var7.noteUsedBy(this.getClassDefinition(), var8, var1);
               if (!this.getClassDefinition().canAccess(var1, var7.getClassDeclaration())) {
                  var1.error(var8, "cant.access.class", var7);
               } else if (!var7.subClassOf(var1, var4)) {
                  var1.error(var8, "throws.not.throwable", var7);
               }
            }
         }

         this.status = 1;
         LocalMember var26;
         if (this.isMethod() && this.args != null) {
            int var19 = this.args.size();

            label186:
            for(int var21 = 0; var21 < var19; ++var21) {
               var26 = (LocalMember)((LocalMember)this.args.elementAt(var21));
               Identifier var27 = var26.getName();

               for(int var32 = var21 + 1; var32 < var19; ++var32) {
                  LocalMember var9 = (LocalMember)((LocalMember)this.args.elementAt(var32));
                  Identifier var10 = var9.getName();
                  if (var27.equals(var10)) {
                     var1.error(var9.getWhere(), "duplicate.argument", var27);
                     break label186;
                  }
               }
            }
         }

         if (this.getValue() != null) {
            var2 = new Context(var2, this);
            Expression var25;
            ClassDeclaration var35;
            if (this.isMethod()) {
               Statement var20 = (Statement)this.getValue();
               Enumeration var24 = this.args.elements();

               while(var24.hasMoreElements()) {
                  var26 = (LocalMember)var24.nextElement();
                  var3.addVar(var2.declare(var1, var26));
               }

               if (this.isConstructor()) {
                  var3.clearVar(var2.getThisNumber());
                  var25 = var20.firstConstructor();
                  if (var25 == null && this.getClassDefinition().getSuperClass() != null) {
                     var25 = this.getDefaultSuperCall(var1);
                     ExpressionStatement var28 = new ExpressionStatement(this.where, var25);
                     var20 = Statement.insertStatement(var28, var20);
                     this.setValue(var20);
                  }
               }

               var5 = this.getExceptions(var1);
               var6 = var5.length > 3 ? 17 : 7;
               Hashtable var29 = new Hashtable(var6);
               var3 = var20.checkMethod(var1, var2, var3, var29);
               var35 = var1.getClassDeclaration(idJavaLangError);
               ClassDeclaration var33 = var1.getClassDeclaration(idJavaLangRuntimeException);
               Enumeration var36 = var29.keys();

               label159:
               while(true) {
                  ClassDeclaration var11;
                  ClassDefinition var12;
                  do {
                     do {
                        if (!var36.hasMoreElements()) {
                           break label159;
                        }

                        var11 = (ClassDeclaration)var36.nextElement();
                        var12 = var11.getClassDefinition(var1);
                     } while(var12.subClassOf(var1, var35));
                  } while(var12.subClassOf(var1, var33));

                  boolean var13 = false;
                  if (!this.isInitializer()) {
                     for(int var14 = 0; var14 < var5.length; ++var14) {
                        if (var12.subClassOf(var1, var5[var14])) {
                           var13 = true;
                        }
                     }
                  }

                  if (!var13) {
                     Node var40 = (Node)var29.get(var11);
                     long var15 = var40.getWhere();
                     String var17;
                     if (this.isConstructor()) {
                        if (var15 == this.getClassDefinition().getWhere()) {
                           var17 = "def.constructor.exception";
                        } else {
                           var17 = "constructor.exception";
                        }
                     } else if (this.isInitializer()) {
                        var17 = "initializer.exception";
                     } else {
                        var17 = "uncaught.exception";
                     }

                     var1.error(var15, var17, var11.getName());
                  }
               }
            } else {
               Hashtable var22 = new Hashtable(3);
               var25 = (Expression)this.getValue();
               var3 = var25.checkInitializer(var1, var2, var3, this.getType(), var22);
               this.setValue(var25.convert(var1, var2, this.getType(), var25));
               if (this.isStatic() && this.isFinal() && !this.clazz.isTopLevel() && !((Expression)this.getValue()).isConstant()) {
                  var1.error(this.where, "static.inner.field", this.getName(), this);
                  this.setValue((Node)null);
               }

               ClassDeclaration var30 = var1.getClassDeclaration(idJavaLangThrowable);
               ClassDeclaration var31 = var1.getClassDeclaration(idJavaLangError);
               var35 = var1.getClassDeclaration(idJavaLangRuntimeException);
               Enumeration var34 = var22.keys();

               while(var34.hasMoreElements()) {
                  ClassDeclaration var37 = (ClassDeclaration)var34.nextElement();
                  ClassDefinition var38 = var37.getClassDefinition(var1);
                  if (!var38.subClassOf(var1, var31) && !var38.subClassOf(var1, var35) && var38.subClassOf(var1, var30)) {
                     Node var39 = (Node)var22.get(var37);
                     var1.error(var39.getWhere(), "initializer.exception", var37.getName());
                  }
               }
            }

            if (var1.dump()) {
               this.getValue().print(System.out);
               System.out.println();
            }
         }

         this.status = this.getClassDefinition().getError() ? 5 : 2;
      }

      if (this.isInitializer() && var3.isDeadEnd()) {
         var1.error(this.where, "init.no.normal.completion");
         var3 = var3.clearDeadEnd();
      }

      return var3;
   }

   private Expression getDefaultSuperCall(Environment var1) {
      SuperExpression var2 = null;
      ClassDefinition var3 = this.getClassDefinition().getSuperClass().getClassDefinition();
      ClassDefinition var4 = var3 == null ? null : (var3.isTopLevel() ? null : var3.getOuterClass());
      ClassDefinition var5 = this.getClassDefinition();
      if (var4 != null && !Context.outerLinkExists(var1, var4, var5)) {
         var2 = new SuperExpression(this.where, new NullExpression(this.where));
         var1.error(this.where, "no.default.outer.arg", var4, this.getClassDefinition());
      }

      if (var2 == null) {
         var2 = new SuperExpression(this.where);
      }

      return new MethodExpression(this.where, var2, idInit, new Expression[0]);
   }

   void inline(Environment var1) throws ClassNotFound {
      switch (this.status) {
         case 0:
            this.check(var1);
            this.inline(var1);
            break;
         case 2:
            if (var1.dump()) {
               System.out.println("[inline field " + this.getClassDeclaration().getName() + "." + this.getName() + "]");
            }

            this.status = 3;
            var1 = new Environment(var1, this);
            Context var3;
            if (this.isMethod()) {
               if (!this.isNative() && !this.isAbstract()) {
                  Statement var2 = (Statement)this.getValue();
                  var3 = new Context((Context)null, this);
                  Enumeration var4 = this.args.elements();

                  while(var4.hasMoreElements()) {
                     LocalMember var5 = (LocalMember)var4.nextElement();
                     var3.declare(var1, var5);
                  }

                  this.setValue(var2.inline(var1, var3));
               }
            } else {
               if (this.isInnerClass()) {
                  ClassDefinition var7 = this.getInnerClass();
                  if (var7 instanceof SourceClass && !var7.isLocal() && var7.isInsideLocal()) {
                     this.status = 3;
                     ((SourceClass)var7).inlineLocalClass(var1);
                  }

                  this.status = 4;
                  return;
               }

               if (this.getValue() != null) {
                  Context var6 = new Context((Context)null, this);
                  if (!this.isStatic()) {
                     var3 = new Context(var6, this);
                     LocalMember var8 = ((SourceClass)this.clazz).getThisArgument();
                     var3.declare(var1, var8);
                     this.setValue(((Expression)this.getValue()).inlineValue(var1, var3));
                  } else {
                     this.setValue(((Expression)this.getValue()).inlineValue(var1, var6));
                  }
               }
            }

            if (var1.dump()) {
               System.out.println("[inlined field " + this.getClassDeclaration().getName() + "." + this.getName() + "]");
               if (this.getValue() != null) {
                  this.getValue().print(System.out);
                  System.out.println();
               } else {
                  System.out.println("<empty>");
               }
            }

            this.status = 4;
      }

   }

   public Node getValue(Environment var1) throws ClassNotFound {
      Node var2 = this.getValue();
      if (var2 != null && this.status != 4) {
         var1 = ((SourceClass)this.clazz).setupEnv(var1);
         this.inline(var1);
         var2 = this.status == 4 ? this.getValue() : null;
      }

      return var2;
   }

   public boolean isInlineable(Environment var1, boolean var2) throws ClassNotFound {
      if (!super.isInlineable(var1, var2)) {
         return false;
      } else {
         this.getValue(var1);
         return this.status == 4 && !this.getClassDefinition().getError();
      }
   }

   public Object getInitialValue() {
      return !this.isMethod() && this.getValue() != null && this.isFinal() && this.status == 4 ? ((Expression)this.getValue()).getValue() : null;
   }

   public void code(Environment var1, Assembler var2) throws ClassNotFound {
      switch (this.status) {
         case 0:
            this.check(var1);
            this.code(var1, var2);
            return;
         case 1:
         case 3:
         default:
            return;
         case 2:
            this.inline(var1);
            this.code(var1, var2);
            return;
         case 4:
            if (var1.dump()) {
               System.out.println("[code field " + this.getClassDeclaration().getName() + "." + this.getName() + "]");
            }

            if (this.isMethod() && !this.isNative() && !this.isAbstract()) {
               var1 = new Environment(var1, this);
               Context var3 = new Context((Context)null, this);
               Statement var4 = (Statement)this.getValue();
               Enumeration var5 = this.args.elements();

               while(var5.hasMoreElements()) {
                  LocalMember var6 = (LocalMember)var5.nextElement();
                  var3.declare(var1, var6);
               }

               if (var4 != null) {
                  var4.code(var1, var3, var2);
               }

               if (this.getType().getReturnType().isType(11) && !this.isInitializer()) {
                  var2.add(this.getWhere(), 177, true);
               }
            }

      }
   }

   public void codeInit(Environment var1, Context var2, Assembler var3) throws ClassNotFound {
      if (!this.isMethod()) {
         switch (this.status) {
            case 0:
               this.check(var1);
               this.codeInit(var1, var2, var3);
               return;
            case 1:
            case 3:
            default:
               return;
            case 2:
               this.inline(var1);
               this.codeInit(var1, var2, var3);
               return;
            case 4:
               if (var1.dump()) {
                  System.out.println("[code initializer  " + this.getClassDeclaration().getName() + "." + this.getName() + "]");
               }

               if (this.getValue() != null) {
                  Expression var4 = (Expression)this.getValue();
                  if (this.isStatic()) {
                     if (this.getInitialValue() == null) {
                        var4.codeValue(var1, var2, var3);
                        var3.add(this.getWhere(), 179, this);
                     }
                  } else {
                     var3.add(this.getWhere(), 25, new Integer(0));
                     var4.codeValue(var1, var2, var3);
                     var3.add(this.getWhere(), 181, this);
                  }
               }

         }
      }
   }

   public void print(PrintStream var1) {
      super.print(var1);
      if (this.getValue() != null) {
         this.getValue().print(var1);
         var1.println();
      }

   }
}
