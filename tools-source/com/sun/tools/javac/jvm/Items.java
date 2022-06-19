package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Assert;

public class Items {
   Pool pool;
   Code code;
   Symtab syms;
   Types types;
   private final Item voidItem;
   private final Item thisItem;
   private final Item superItem;
   private final Item[] stackItem = new Item[9];

   public Items(Pool var1, Code var2, Symtab var3, Types var4) {
      this.code = var2;
      this.pool = var1;
      this.types = var4;
      this.voidItem = new Item(8) {
         public String toString() {
            return "void";
         }
      };
      this.thisItem = new SelfItem(false);
      this.superItem = new SelfItem(true);

      for(int var5 = 0; var5 < 8; ++var5) {
         this.stackItem[var5] = new StackItem(var5);
      }

      this.stackItem[8] = this.voidItem;
      this.syms = var3;
   }

   Item makeVoidItem() {
      return this.voidItem;
   }

   Item makeThisItem() {
      return this.thisItem;
   }

   Item makeSuperItem() {
      return this.superItem;
   }

   Item makeStackItem(Type var1) {
      return this.stackItem[Code.typecode(var1)];
   }

   Item makeDynamicItem(Symbol var1) {
      return new DynamicItem(var1);
   }

   Item makeIndexedItem(Type var1) {
      return new IndexedItem(var1);
   }

   LocalItem makeLocalItem(Symbol.VarSymbol var1) {
      return new LocalItem(var1.erasure(this.types), var1.adr);
   }

   private LocalItem makeLocalItem(Type var1, int var2) {
      return new LocalItem(var1, var2);
   }

   Item makeStaticItem(Symbol var1) {
      return new StaticItem(var1);
   }

   Item makeMemberItem(Symbol var1, boolean var2) {
      return new MemberItem(var1, var2);
   }

   Item makeImmediateItem(Type var1, Object var2) {
      return new ImmediateItem(var1, var2);
   }

   Item makeAssignItem(Item var1) {
      return new AssignItem(var1);
   }

   CondItem makeCondItem(int var1, Code.Chain var2, Code.Chain var3) {
      return new CondItem(var1, var2, var3);
   }

   CondItem makeCondItem(int var1) {
      return this.makeCondItem(var1, (Code.Chain)null, (Code.Chain)null);
   }

   class CondItem extends Item {
      Code.Chain trueJumps;
      Code.Chain falseJumps;
      int opcode;
      JCTree tree;

      CondItem(int var2, Code.Chain var3, Code.Chain var4) {
         super(5);
         this.opcode = var2;
         this.trueJumps = var3;
         this.falseJumps = var4;
      }

      Item load() {
         Code.Chain var1 = null;
         Code.Chain var2 = this.jumpFalse();
         if (!this.isFalse()) {
            Items.this.code.resolve(this.trueJumps);
            Items.this.code.emitop0(4);
            var1 = Items.this.code.branch(167);
         }

         if (var2 != null) {
            Items.this.code.resolve(var2);
            Items.this.code.emitop0(3);
         }

         Items.this.code.resolve(var1);
         return Items.this.stackItem[this.typecode];
      }

      void duplicate() {
         this.load().duplicate();
      }

      void drop() {
         this.load().drop();
      }

      void stash(int var1) {
         Assert.error();
      }

      CondItem mkCond() {
         return this;
      }

      Code.Chain jumpTrue() {
         if (this.tree == null) {
            return Code.mergeChains(this.trueJumps, Items.this.code.branch(this.opcode));
         } else {
            int var1 = Items.this.code.curCP();
            Code.Chain var2 = Code.mergeChains(this.trueJumps, Items.this.code.branch(this.opcode));
            Items.this.code.crt.put(this.tree, 128, var1, Items.this.code.curCP());
            return var2;
         }
      }

      Code.Chain jumpFalse() {
         if (this.tree == null) {
            return Code.mergeChains(this.falseJumps, Items.this.code.branch(Code.negate(this.opcode)));
         } else {
            int var1 = Items.this.code.curCP();
            Code.Chain var2 = Code.mergeChains(this.falseJumps, Items.this.code.branch(Code.negate(this.opcode)));
            Items.this.code.crt.put(this.tree, 256, var1, Items.this.code.curCP());
            return var2;
         }
      }

      CondItem negate() {
         CondItem var1 = Items.this.new CondItem(Code.negate(this.opcode), this.falseJumps, this.trueJumps);
         var1.tree = this.tree;
         return var1;
      }

      int width() {
         throw new AssertionError();
      }

      boolean isTrue() {
         return this.falseJumps == null && this.opcode == 167;
      }

      boolean isFalse() {
         return this.trueJumps == null && this.opcode == 168;
      }

      public String toString() {
         return "cond(" + Code.mnem(this.opcode) + ")";
      }
   }

   class AssignItem extends Item {
      Item lhs;

      AssignItem(Item var2) {
         super(var2.typecode);
         this.lhs = var2;
      }

      Item load() {
         this.lhs.stash(this.typecode);
         this.lhs.store();
         return Items.this.stackItem[this.typecode];
      }

      void duplicate() {
         this.load().duplicate();
      }

      void drop() {
         this.lhs.store();
      }

      void stash(int var1) {
         Assert.error();
      }

      int width() {
         return this.lhs.width() + Code.width(this.typecode);
      }

      public String toString() {
         return "assign(lhs = " + this.lhs + ")";
      }
   }

   class ImmediateItem extends Item {
      Object value;

      ImmediateItem(Type var2, Object var3) {
         super(Code.typecode(var2));
         this.value = var3;
      }

      private void ldc() {
         int var1 = Items.this.pool.put(this.value);
         if (this.typecode != 1 && this.typecode != 3) {
            Items.this.code.emitLdc(var1);
         } else {
            Items.this.code.emitop2(20, var1);
         }

      }

      Item load() {
         switch (this.typecode) {
            case 0:
            case 5:
            case 6:
            case 7:
               int var1 = ((Number)this.value).intValue();
               if (-1 <= var1 && var1 <= 5) {
                  Items.this.code.emitop0(3 + var1);
               } else if (-128 <= var1 && var1 <= 127) {
                  Items.this.code.emitop1(16, var1);
               } else if (-32768 <= var1 && var1 <= 32767) {
                  Items.this.code.emitop2(17, var1);
               } else {
                  this.ldc();
               }
               break;
            case 1:
               long var2 = ((Number)this.value).longValue();
               if (var2 != 0L && var2 != 1L) {
                  this.ldc();
               } else {
                  Items.this.code.emitop0(9 + (int)var2);
               }
               break;
            case 2:
               float var4 = ((Number)this.value).floatValue();
               if (!this.isPosZero(var4) && (double)var4 != 1.0 && (double)var4 != 2.0) {
                  this.ldc();
               } else {
                  Items.this.code.emitop0(11 + (int)var4);
               }
               break;
            case 3:
               double var5 = ((Number)this.value).doubleValue();
               if (!this.isPosZero(var5) && var5 != 1.0) {
                  this.ldc();
               } else {
                  Items.this.code.emitop0(14 + (int)var5);
               }
               break;
            case 4:
               this.ldc();
               break;
            default:
               Assert.error();
         }

         return Items.this.stackItem[this.typecode];
      }

      private boolean isPosZero(float var1) {
         return var1 == 0.0F && 1.0F / var1 > 0.0F;
      }

      private boolean isPosZero(double var1) {
         return var1 == 0.0 && 1.0 / var1 > 0.0;
      }

      CondItem mkCond() {
         int var1 = ((Number)this.value).intValue();
         return Items.this.makeCondItem(var1 != 0 ? 167 : 168);
      }

      Item coerce(int var1) {
         if (this.typecode == var1) {
            return this;
         } else {
            switch (var1) {
               case 0:
                  if (Code.truncate(this.typecode) == 0) {
                     return this;
                  }

                  return Items.this.new ImmediateItem(Items.this.syms.intType, ((Number)this.value).intValue());
               case 1:
                  return Items.this.new ImmediateItem(Items.this.syms.longType, ((Number)this.value).longValue());
               case 2:
                  return Items.this.new ImmediateItem(Items.this.syms.floatType, ((Number)this.value).floatValue());
               case 3:
                  return Items.this.new ImmediateItem(Items.this.syms.doubleType, ((Number)this.value).doubleValue());
               case 4:
               default:
                  return super.coerce(var1);
               case 5:
                  return Items.this.new ImmediateItem(Items.this.syms.byteType, Integer.valueOf((byte)((Number)this.value).intValue()));
               case 6:
                  return Items.this.new ImmediateItem(Items.this.syms.charType, Integer.valueOf((char)((Number)this.value).intValue()));
               case 7:
                  return Items.this.new ImmediateItem(Items.this.syms.shortType, Integer.valueOf((short)((Number)this.value).intValue()));
            }
         }
      }

      public String toString() {
         return "immediate(" + this.value + ")";
      }
   }

   class MemberItem extends Item {
      Symbol member;
      boolean nonvirtual;

      MemberItem(Symbol var2, boolean var3) {
         super(Code.typecode(var2.erasure(Items.this.types)));
         this.member = var2;
         this.nonvirtual = var3;
      }

      Item load() {
         Items.this.code.emitop2(180, Items.this.pool.put(this.member));
         return Items.this.stackItem[this.typecode];
      }

      void store() {
         Items.this.code.emitop2(181, Items.this.pool.put(this.member));
      }

      Item invoke() {
         Type.MethodType var1 = (Type.MethodType)this.member.externalType(Items.this.types);
         int var2 = Code.typecode(var1.restype);
         if ((this.member.owner.flags() & 512L) != 0L && !this.nonvirtual) {
            Items.this.code.emitInvokeinterface(Items.this.pool.put(this.member), var1);
         } else if (this.nonvirtual) {
            Items.this.code.emitInvokespecial(Items.this.pool.put(this.member), var1);
         } else {
            Items.this.code.emitInvokevirtual(Items.this.pool.put(this.member), var1);
         }

         return Items.this.stackItem[var2];
      }

      void duplicate() {
         Items.this.stackItem[4].duplicate();
      }

      void drop() {
         Items.this.stackItem[4].drop();
      }

      void stash(int var1) {
         Items.this.stackItem[4].stash(var1);
      }

      int width() {
         return 1;
      }

      public String toString() {
         return "member(" + this.member + (this.nonvirtual ? " nonvirtual)" : ")");
      }
   }

   class DynamicItem extends StaticItem {
      DynamicItem(Symbol var2) {
         super(var2);
      }

      Item load() {
         assert false;

         return null;
      }

      void store() {
         assert false;

      }

      Item invoke() {
         Type.MethodType var1 = (Type.MethodType)this.member.erasure(Items.this.types);
         int var2 = Code.typecode(var1.restype);
         Items.this.code.emitInvokedynamic(Items.this.pool.put(this.member), var1);
         return Items.this.stackItem[var2];
      }

      public String toString() {
         return "dynamic(" + this.member + ")";
      }
   }

   class StaticItem extends Item {
      Symbol member;

      StaticItem(Symbol var2) {
         super(Code.typecode(var2.erasure(Items.this.types)));
         this.member = var2;
      }

      Item load() {
         Items.this.code.emitop2(178, Items.this.pool.put(this.member));
         return Items.this.stackItem[this.typecode];
      }

      void store() {
         Items.this.code.emitop2(179, Items.this.pool.put(this.member));
      }

      Item invoke() {
         Type.MethodType var1 = (Type.MethodType)this.member.erasure(Items.this.types);
         int var2 = Code.typecode(var1.restype);
         Items.this.code.emitInvokestatic(Items.this.pool.put(this.member), var1);
         return Items.this.stackItem[var2];
      }

      public String toString() {
         return "static(" + this.member + ")";
      }
   }

   class LocalItem extends Item {
      int reg;
      Type type;

      LocalItem(Type var2, int var3) {
         super(Code.typecode(var2));
         Assert.check(var3 >= 0);
         this.type = var2;
         this.reg = var3;
      }

      Item load() {
         if (this.reg <= 3) {
            Items.this.code.emitop0(26 + Code.truncate(this.typecode) * 4 + this.reg);
         } else {
            Items.this.code.emitop1w(21 + Code.truncate(this.typecode), this.reg);
         }

         return Items.this.stackItem[this.typecode];
      }

      void store() {
         if (this.reg <= 3) {
            Items.this.code.emitop0(59 + Code.truncate(this.typecode) * 4 + this.reg);
         } else {
            Items.this.code.emitop1w(54 + Code.truncate(this.typecode), this.reg);
         }

         Items.this.code.setDefined(this.reg);
      }

      void incr(int var1) {
         if (this.typecode == 0 && var1 >= -32768 && var1 <= 32767) {
            Items.this.code.emitop1w(132, this.reg, var1);
         } else {
            this.load();
            if (var1 >= 0) {
               Items.this.makeImmediateItem(Items.this.syms.intType, var1).load();
               Items.this.code.emitop0(96);
            } else {
               Items.this.makeImmediateItem(Items.this.syms.intType, -var1).load();
               Items.this.code.emitop0(100);
            }

            Items.this.makeStackItem(Items.this.syms.intType).coerce(this.typecode);
            this.store();
         }

      }

      public String toString() {
         return "localItem(type=" + this.type + "; reg=" + this.reg + ")";
      }
   }

   class SelfItem extends Item {
      boolean isSuper;

      SelfItem(boolean var2) {
         super(4);
         this.isSuper = var2;
      }

      Item load() {
         Items.this.code.emitop0(42);
         return Items.this.stackItem[this.typecode];
      }

      public String toString() {
         return this.isSuper ? "super" : "this";
      }
   }

   class IndexedItem extends Item {
      IndexedItem(Type var2) {
         super(Code.typecode(var2));
      }

      Item load() {
         Items.this.code.emitop0(46 + this.typecode);
         return Items.this.stackItem[this.typecode];
      }

      void store() {
         Items.this.code.emitop0(79 + this.typecode);
      }

      void duplicate() {
         Items.this.code.emitop0(92);
      }

      void drop() {
         Items.this.code.emitop0(88);
      }

      void stash(int var1) {
         Items.this.code.emitop0(91 + 3 * (Code.width(var1) - 1));
      }

      int width() {
         return 2;
      }

      public String toString() {
         return "indexed(" + ByteCodes.typecodeNames[this.typecode] + ")";
      }
   }

   class StackItem extends Item {
      StackItem(int var2) {
         super(var2);
      }

      Item load() {
         return this;
      }

      void duplicate() {
         Items.this.code.emitop0(this.width() == 2 ? 92 : 89);
      }

      void drop() {
         Items.this.code.emitop0(this.width() == 2 ? 88 : 87);
      }

      void stash(int var1) {
         Items.this.code.emitop0((this.width() == 2 ? 91 : 90) + 3 * (Code.width(var1) - 1));
      }

      int width() {
         return Code.width(this.typecode);
      }

      public String toString() {
         return "stack(" + ByteCodes.typecodeNames[this.typecode] + ")";
      }
   }

   abstract class Item {
      int typecode;

      Item(int var2) {
         this.typecode = var2;
      }

      Item load() {
         throw new AssertionError();
      }

      void store() {
         throw new AssertionError("store unsupported: " + this);
      }

      Item invoke() {
         throw new AssertionError(this);
      }

      void duplicate() {
      }

      void drop() {
      }

      void stash(int var1) {
         Items.this.stackItem[var1].duplicate();
      }

      CondItem mkCond() {
         this.load();
         return Items.this.makeCondItem(154);
      }

      Item coerce(int var1) {
         if (this.typecode == var1) {
            return this;
         } else {
            this.load();
            int var2 = Code.truncate(this.typecode);
            int var3 = Code.truncate(var1);
            if (var2 != var3) {
               int var4 = var3 > var2 ? var3 - 1 : var3;
               Items.this.code.emitop0(133 + var2 * 3 + var4);
            }

            if (var1 != var3) {
               Items.this.code.emitop0(145 + var1 - 5);
            }

            return Items.this.stackItem[var1];
         }
      }

      Item coerce(Type var1) {
         return this.coerce(Code.typecode(var1));
      }

      int width() {
         return 0;
      }

      public abstract String toString();
   }
}
