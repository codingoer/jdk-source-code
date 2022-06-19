package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ThisExpression extends Expression {
   LocalMember field;
   Expression implementation;
   Expression outerArg;

   public ThisExpression(long var1) {
      super(82, var1, Type.tObject);
   }

   protected ThisExpression(int var1, long var2) {
      super(var1, var2, Type.tObject);
   }

   public ThisExpression(long var1, LocalMember var3) {
      super(82, var1, Type.tObject);
      this.field = var3;
      ++var3.readcount;
   }

   public ThisExpression(long var1, Context var3) {
      super(82, var1, Type.tObject);
      this.field = var3.getLocalField(idThis);
      ++this.field.readcount;
   }

   public ThisExpression(long var1, Expression var3) {
      this(var1);
      this.outerArg = var3;
   }

   public Expression getImplementation() {
      return (Expression)(this.implementation != null ? this.implementation : this);
   }

   public Expression getOuterArg() {
      return this.outerArg;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      if (var2.field.isStatic()) {
         var1.error(this.where, "undef.var", opNames[this.op]);
         this.type = Type.tError;
         return var3;
      } else {
         if (this.field == null) {
            this.field = var2.getLocalField(idThis);
            ++this.field.readcount;
         }

         if (this.field.scopeNumber < var2.frameNumber) {
            this.implementation = var2.makeReference(var1, this.field);
         }

         if (!var3.testVar(this.field.number)) {
            var1.error(this.where, "access.inst.before.super", opNames[this.op]);
         }

         if (this.field == null) {
            this.type = var2.field.getClassDeclaration().getType();
         } else {
            this.type = this.field.getType();
         }

         return var3;
      }
   }

   public boolean isNonNull() {
      return true;
   }

   public FieldUpdater getAssigner(Environment var1, Context var2) {
      return null;
   }

   public FieldUpdater getUpdater(Environment var1, Context var2) {
      return null;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineValue(var1, var2);
      } else {
         if (this.field != null && this.field.isInlineable(var1, false)) {
            Expression var3 = (Expression)this.field.getValue(var1);
            if (var3 != null) {
               var3 = var3.copyInline(var2);
               var3.type = this.type;
               return var3;
            }
         }

         return this;
      }
   }

   public Expression copyInline(Context var1) {
      if (this.implementation != null) {
         return this.implementation.copyInline(var1);
      } else {
         ThisExpression var2 = (ThisExpression)this.clone();
         if (this.field == null) {
            var2.field = var1.getLocalField(idThis);
            ++var2.field.readcount;
         } else {
            var2.field = this.field.getCurrentInlineCopy(var1);
         }

         if (this.outerArg != null) {
            var2.outerArg = this.outerArg.copyInline(var1);
         }

         return var2;
      }
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 25, new Integer(this.field.number));
   }

   public void print(PrintStream var1) {
      if (this.outerArg != null) {
         var1.print("(outer=");
         this.outerArg.print(var1);
         var1.print(" ");
      }

      String var2 = this.field == null ? "" : this.field.getClassDefinition().getName().getFlatName().getName() + ".";
      var2 = var2 + opNames[this.op];
      var1.print(var2 + "#" + (this.field != null ? this.field.hashCode() : 0));
      if (this.outerArg != null) {
         var1.print(")");
      }

   }
}
