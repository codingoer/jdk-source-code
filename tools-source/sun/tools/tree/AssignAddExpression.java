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

public class AssignAddExpression extends AssignOpExpression {
   public AssignAddExpression(long var1, Expression var3, Expression var4) {
      super(5, var1, var3, var4);
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return this.type.isType(10) ? 25 : super.costInline(var1, var2, var3);
   }

   void code(Environment var1, Context var2, Assembler var3, boolean var4) {
      if (this.itype.isType(10)) {
         try {
            Type[] var5 = new Type[]{Type.tString};
            ClassDeclaration var6 = var1.getClassDeclaration(idJavaLangStringBuffer);
            if (this.updater == null) {
               var3.add(this.where, 187, var6);
               var3.add(this.where, 89);
               int var7 = this.left.codeLValue(var1, var2, var3);
               this.codeDup(var1, var2, var3, var7, 2);
               this.left.codeLoad(var1, var2, var3);
               this.left.ensureString(var1, var2, var3);
               ClassDefinition var8 = var2.field.getClassDefinition();
               MemberDefinition var9 = var6.getClassDefinition(var1).matchMethod(var1, var8, idInit, var5);
               var3.add(this.where, 183, var9);
               this.right.codeAppend(var1, var2, var3, var6, false);
               var9 = var6.getClassDefinition(var1).matchMethod(var1, var8, idToString);
               var3.add(this.where, 182, var9);
               if (var4) {
                  this.codeDup(var1, var2, var3, Type.tString.stackSize(), var7);
               }

               this.left.codeStore(var1, var2, var3);
            } else {
               this.updater.startUpdate(var1, var2, var3, false);
               this.left.ensureString(var1, var2, var3);
               var3.add(this.where, 187, var6);
               var3.add(this.where, 90);
               var3.add(this.where, 95);
               ClassDefinition var12 = var2.field.getClassDefinition();
               MemberDefinition var13 = var6.getClassDefinition(var1).matchMethod(var1, var12, idInit, var5);
               var3.add(this.where, 183, var13);
               this.right.codeAppend(var1, var2, var3, var6, false);
               var13 = var6.getClassDefinition(var1).matchMethod(var1, var12, idToString);
               var3.add(this.where, 182, var13);
               this.updater.finishUpdate(var1, var2, var3, var4);
            }
         } catch (ClassNotFound var10) {
            throw new CompilerError(var10);
         } catch (AmbiguousMember var11) {
            throw new CompilerError(var11);
         }
      } else {
         super.code(var1, var2, var3, var4);
      }

   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 96 + this.itype.getTypeCodeOffset());
   }
}
