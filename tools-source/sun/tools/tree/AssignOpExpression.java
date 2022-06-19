package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public abstract class AssignOpExpression extends BinaryAssignExpression {
   protected Type itype;
   final int NOINC = Integer.MAX_VALUE;
   protected FieldUpdater updater = null;

   public AssignOpExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, var4, var5);
   }

   final void selectType(Environment var1, Context var2, int var3) {
      Type var4 = null;
      switch (this.op) {
         case 5:
            if (this.left.type == Type.tString) {
               if (this.right.type == Type.tVoid) {
                  var1.error(this.where, "incompatible.type", opNames[this.op], Type.tVoid, Type.tString);
                  this.type = Type.tError;
               } else {
                  this.type = this.itype = Type.tString;
               }

               return;
            }
         case 2:
         case 3:
         case 4:
         case 6:
            if ((var3 & 128) != 0) {
               this.itype = Type.tDouble;
            } else if ((var3 & 64) != 0) {
               this.itype = Type.tFloat;
            } else if ((var3 & 32) != 0) {
               this.itype = Type.tLong;
            } else {
               this.itype = Type.tInt;
            }
            break;
         case 7:
         case 8:
         case 9:
            var4 = Type.tInt;
            if (this.right.type.inMask(62)) {
               this.right = new ConvertExpression(this.where, Type.tInt, this.right);
            }

            if (this.left.type == Type.tLong) {
               this.itype = Type.tLong;
            } else {
               this.itype = Type.tInt;
            }
            break;
         case 10:
         case 11:
         case 12:
            if ((var3 & 1) != 0) {
               this.itype = Type.tBoolean;
            } else if ((var3 & 32) != 0) {
               this.itype = Type.tLong;
            } else {
               this.itype = Type.tInt;
            }
            break;
         default:
            throw new CompilerError("Bad assignOp type: " + this.op);
      }

      if (var4 == null) {
         var4 = this.itype;
      }

      this.right = this.convert(var1, var2, var4, this.right);
      this.type = this.left.type;
   }

   int getIncrement() {
      if (this.left.op == 60 && this.type.isType(4) && this.right.op == 65 && (this.op == 5 || this.op == 6) && ((IdentifierExpression)this.left).field.isLocal()) {
         int var1 = ((IntExpression)this.right).value;
         if (this.op == 6) {
            var1 = -var1;
         }

         if (var1 == (short)var1) {
            return var1;
         }
      }

      return Integer.MAX_VALUE;
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.left.checkAssignOp(var1, var2, var3, var4, this);
      var3 = this.right.checkValue(var1, var2, var3, var4);
      int var5 = this.left.type.getTypeMask() | this.right.type.getTypeMask();
      if ((var5 & 8192) != 0) {
         return var3;
      } else {
         this.selectType(var1, var2, var5);
         if (!this.type.isType(13)) {
            this.convert(var1, var2, this.itype, this.left);
         }

         this.updater = this.left.getUpdater(var1, var2);
         return var3;
      }
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.left = this.left.inlineValue(var1, var2);
      this.right = this.right.inlineValue(var1, var2);
      if (this.updater != null) {
         this.updater = this.updater.inline(var1, var2);
      }

      return this;
   }

   public Expression copyInline(Context var1) {
      AssignOpExpression var2 = (AssignOpExpression)this.clone();
      var2.left = this.left.copyInline(var1);
      var2.right = this.right.copyInline(var1);
      if (this.updater != null) {
         var2.updater = this.updater.copyInline(var1);
      }

      return var2;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (this.updater == null) {
         return this.getIncrement() != Integer.MAX_VALUE ? 3 : this.right.costInline(var1, var2, var3) + this.left.costInline(var1, var2, var3) + 4;
      } else {
         return this.right.costInline(var1, var2, var3) + this.updater.costInline(var1, var2, var3, true) + 1;
      }
   }

   void code(Environment var1, Context var2, Assembler var3, boolean var4) {
      int var5 = this.getIncrement();
      int var6;
      if (var5 != Integer.MAX_VALUE && this.updater == null) {
         var6 = ((LocalMember)((IdentifierExpression)this.left).field).number;
         int[] var7 = new int[]{var6, var5};
         var3.add(this.where, 132, var7);
         if (var4) {
            this.left.codeValue(var1, var2, var3);
         }

      } else {
         if (this.updater == null) {
            var6 = this.left.codeLValue(var1, var2, var3);
            this.codeDup(var1, var2, var3, var6, 0);
            this.left.codeLoad(var1, var2, var3);
            this.codeConversion(var1, var2, var3, this.left.type, this.itype);
            this.right.codeValue(var1, var2, var3);
            this.codeOperation(var1, var2, var3);
            this.codeConversion(var1, var2, var3, this.itype, this.type);
            if (var4) {
               this.codeDup(var1, var2, var3, this.type.stackSize(), var6);
            }

            this.left.codeStore(var1, var2, var3);
         } else {
            this.updater.startUpdate(var1, var2, var3, false);
            this.codeConversion(var1, var2, var3, this.left.type, this.itype);
            this.right.codeValue(var1, var2, var3);
            this.codeOperation(var1, var2, var3);
            this.codeConversion(var1, var2, var3, this.itype, this.type);
            this.updater.finishUpdate(var1, var2, var3, var4);
         }

      }
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.code(var1, var2, var3, true);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.code(var1, var2, var3, false);
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + " ");
      this.left.print(var1);
      var1.print(" ");
      this.right.print(var1);
      var1.print(")");
   }
}
