package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.LocalVariable;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;

public class VarDeclarationStatement extends Statement {
   LocalMember field;
   Expression expr;

   public VarDeclarationStatement(long var1, Expression var3) {
      super(108, var1);
      this.expr = var3;
   }

   public VarDeclarationStatement(long var1, LocalMember var3, Expression var4) {
      super(108, var1);
      this.field = var3;
      this.expr = var4;
   }

   Vset checkDeclaration(Environment var1, Context var2, Vset var3, int var4, Type var5, Hashtable var6) {
      if (this.labels != null) {
         var1.error(this.where, "declaration.with.label", this.labels[0]);
      }

      if (this.field != null) {
         if (var2.getLocalClass(this.field.getName()) != null && this.field.isInnerClass()) {
            var1.error(this.where, "local.class.redefined", this.field.getName());
         }

         var2.declare(var1, this.field);
         if (this.field.isInnerClass()) {
            ClassDefinition var11 = this.field.getInnerClass();

            try {
               var3 = var11.checkLocalClass(var1, var2, var3, (ClassDefinition)null, (Expression[])null, (Type[])null);
            } catch (ClassNotFound var10) {
               var1.error(this.where, "class.not.found", var10.name, opNames[this.op]);
            }

            return var3;
         } else {
            var3.addVar(this.field.number);
            return this.expr != null ? this.expr.checkValue(var1, var2, var3, var6) : var3;
         }
      } else {
         Expression var7 = this.expr;
         if (var7.op == 1) {
            this.expr = ((AssignExpression)var7).right;
            var7 = ((AssignExpression)var7).left;
         } else {
            this.expr = null;
         }

         boolean var8;
         for(var8 = var5.isType(13); var7.op == 48; var5 = Type.tArray(var5)) {
            ArrayAccessExpression var9 = (ArrayAccessExpression)var7;
            if (var9.index != null) {
               var1.error(var9.index.where, "array.dim.in.type");
               var8 = true;
            }

            var7 = var9.right;
         }

         if (var7.op == 60) {
            Identifier var12 = ((IdentifierExpression)var7).id;
            if (var2.getLocalField(var12) != null) {
               var1.error(this.where, "local.redefined", var12);
            }

            this.field = new LocalMember(var7.where, var2.field.getClassDefinition(), var4, var5, var12);
            var2.declare(var1, this.field);
            if (this.expr != null) {
               var3 = this.expr.checkInitializer(var1, var2, var3, var5, var6);
               this.expr = this.convert(var1, var2, var5, this.expr);
               this.field.setValue(this.expr);
               if (this.field.isConstant()) {
                  this.field.addModifiers(1048576);
               }

               var3.addVar(this.field.number);
            } else if (var8) {
               var3.addVar(this.field.number);
            } else {
               var3.addVarUnassigned(this.field.number);
            }

            return var3;
         } else {
            var1.error(var7.where, "invalid.decl");
            return var3;
         }
      }
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.field.isInnerClass()) {
         ClassDefinition var4 = this.field.getInnerClass();
         var4.inlineLocalClass(var1);
         return null;
      } else if (var1.opt() && !this.field.isUsed()) {
         return (new ExpressionStatement(this.where, this.expr)).inline(var1, var2);
      } else {
         var2.declare(var1, this.field);
         if (this.expr != null) {
            this.expr = this.expr.inlineValue(var1, var2);
            this.field.setValue(this.expr);
            if (var1.opt() && this.field.writecount == 0) {
               if (this.expr.op == 60) {
                  IdentifierExpression var3 = (IdentifierExpression)this.expr;
                  if (var3.field.isLocal() && (var2 = var2.getInlineContext()) != null && ((LocalMember)var3.field).number < var2.varNumber) {
                     this.field.setValue(this.expr);
                     this.field.addModifiers(1048576);
                  }
               }

               if (this.expr.isConstant() || this.expr.op == 82 || this.expr.op == 83) {
                  this.field.setValue(this.expr);
                  this.field.addModifiers(1048576);
               }
            }
         }

         return this;
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      VarDeclarationStatement var3 = (VarDeclarationStatement)this.clone();
      if (this.expr != null) {
         var3.expr = this.expr.copyInline(var1);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (this.field != null && this.field.isInnerClass()) {
         return var1;
      } else {
         return this.expr != null ? this.expr.costInline(var1, var2, var3) : 0;
      }
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      if (this.expr != null && !this.expr.type.isType(11)) {
         var2.declare(var1, this.field);
         this.expr.codeValue(var1, var2, var3);
         var3.add(this.where, 54 + this.field.getType().getTypeCodeOffset(), new LocalVariable(this.field, this.field.number));
      } else {
         var2.declare(var1, this.field);
         if (this.expr != null) {
            this.expr.code(var1, var2, var3);
         }
      }

   }

   public void print(PrintStream var1, int var2) {
      var1.print("local ");
      if (this.field != null) {
         var1.print(this.field + "#" + this.field.hashCode());
         if (this.expr != null) {
            var1.print(" = ");
            this.expr.print(var1);
         }
      } else {
         this.expr.print(var1);
         var1.print(";");
      }

   }
}
