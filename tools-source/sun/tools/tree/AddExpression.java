package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.AmbiguousMember;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

public class AddExpression extends BinaryArithmeticExpression {
   public AddExpression(long var1, Expression var3, Expression var4) {
      super(29, var1, var3, var4);
   }

   void selectType(Environment var1, Context var2, int var3) {
      if (this.left.type == Type.tString && !this.right.type.isType(11)) {
         this.type = Type.tString;
      } else if (this.right.type == Type.tString && !this.left.type.isType(11)) {
         this.type = Type.tString;
      } else {
         super.selectType(var1, var2, var3);
      }
   }

   public boolean isNonNull() {
      return true;
   }

   Expression eval(int var1, int var2) {
      return new IntExpression(this.where, var1 + var2);
   }

   Expression eval(long var1, long var3) {
      return new LongExpression(this.where, var1 + var3);
   }

   Expression eval(float var1, float var2) {
      return new FloatExpression(this.where, var1 + var2);
   }

   Expression eval(double var1, double var3) {
      return new DoubleExpression(this.where, var1 + var3);
   }

   Expression eval(String var1, String var2) {
      return new StringExpression(this.where, var1 + var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.type == Type.tString && this.isConstant()) {
         StringBuffer var3 = this.inlineValueSB(var1, var2, new StringBuffer());
         if (var3 != null) {
            return new StringExpression(this.where, var3.toString());
         }
      }

      return super.inlineValue(var1, var2);
   }

   protected StringBuffer inlineValueSB(Environment var1, Context var2, StringBuffer var3) {
      if (this.type != Type.tString) {
         return super.inlineValueSB(var1, var2, var3);
      } else {
         var3 = this.left.inlineValueSB(var1, var2, var3);
         if (var3 != null) {
            var3 = this.right.inlineValueSB(var1, var2, var3);
         }

         return var3;
      }
   }

   Expression simplify() {
      if (!this.type.isType(10)) {
         if (this.type.inMask(62)) {
            if (this.left.equals(0)) {
               return this.right;
            }

            if (this.right.equals(0)) {
               return this.left;
            }
         }
      } else if (this.right.type.isType(8)) {
         this.right = new StringExpression(this.right.where, "null");
      } else if (this.left.type.isType(8)) {
         this.left = new StringExpression(this.left.where, "null");
      }

      return this;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return (this.type.isType(10) ? 12 : 1) + this.left.costInline(var1, var2, var3) + this.right.costInline(var1, var2, var3);
   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 96 + this.type.getTypeCodeOffset());
   }

   void codeAppend(Environment var1, Context var2, Assembler var3, ClassDeclaration var4, boolean var5) throws ClassNotFound, AmbiguousMember {
      if (this.type.isType(10)) {
         this.left.codeAppend(var1, var2, var3, var4, var5);
         this.right.codeAppend(var1, var2, var3, var4, false);
      } else {
         super.codeAppend(var1, var2, var3, var4, var5);
      }

   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      if (this.type.isType(10)) {
         try {
            if (this.left.equals("")) {
               this.right.codeValue(var1, var2, var3);
               this.right.ensureString(var1, var2, var3);
               return;
            }

            if (this.right.equals("")) {
               this.left.codeValue(var1, var2, var3);
               this.left.ensureString(var1, var2, var3);
               return;
            }

            ClassDeclaration var4 = var1.getClassDeclaration(idJavaLangStringBuffer);
            ClassDefinition var5 = var2.field.getClassDefinition();
            this.codeAppend(var1, var2, var3, var4, true);
            MemberDefinition var6 = var4.getClassDefinition(var1).matchMethod(var1, var5, idToString);
            var3.add(this.where, 182, var6);
         } catch (ClassNotFound var7) {
            throw new CompilerError(var7);
         } catch (AmbiguousMember var8) {
            throw new CompilerError(var8);
         }
      } else {
         super.codeValue(var1, var2, var3);
      }

   }
}
