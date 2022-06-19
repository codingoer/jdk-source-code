package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeAnnotationPosition;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.util.ArrayUtils;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Bits;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Position;
import java.util.ArrayList;
import java.util.Iterator;

public class Code {
   public final boolean debugCode;
   public final boolean needStackMap;
   final Types types;
   final Symtab syms;
   public int max_stack = 0;
   public int max_locals = 0;
   public byte[] code = new byte[64];
   public int cp = 0;
   ListBuffer catchInfo = new ListBuffer();
   List lineInfo = List.nil();
   public CRTable crt;
   public boolean fatcode;
   private boolean alive = true;
   State state;
   private boolean fixedPc = false;
   public int nextreg = 0;
   Chain pendingJumps = null;
   int pendingStatPos = -1;
   boolean pendingStackMap = false;
   StackMapFormat stackMap;
   boolean varDebugInfo;
   boolean lineDebugInfo;
   Position.LineMap lineMap;
   final Pool pool;
   final Symbol.MethodSymbol meth;
   StackMapFrame[] stackMapBuffer = null;
   ClassWriter.StackMapTableFrame[] stackMapTableBuffer = null;
   int stackMapBufferSize = 0;
   int lastStackMapPC = -1;
   StackMapFrame lastFrame = null;
   StackMapFrame frameBeforeLast = null;
   static final Type jsrReturnValue;
   LocalVar[] lvar;
   LocalVar[] varBuffer;
   int varBufferSize;

   public boolean checkLimits(JCDiagnostic.DiagnosticPosition var1, Log var2) {
      if (this.cp > 65535) {
         var2.error(var1, "limit.code", new Object[0]);
         return true;
      } else if (this.max_locals > 65535) {
         var2.error(var1, "limit.locals", new Object[0]);
         return true;
      } else if (this.max_stack > 65535) {
         var2.error(var1, "limit.stack", new Object[0]);
         return true;
      } else {
         return false;
      }
   }

   public Code(Symbol.MethodSymbol var1, boolean var2, Position.LineMap var3, boolean var4, StackMapFormat var5, boolean var6, CRTable var7, Symtab var8, Types var9, Pool var10) {
      this.meth = var1;
      this.fatcode = var2;
      this.lineMap = var3;
      this.lineDebugInfo = var3 != null;
      this.varDebugInfo = var4;
      this.crt = var7;
      this.syms = var8;
      this.types = var9;
      this.debugCode = var6;
      this.stackMap = var5;
      switch (var5) {
         case CLDC:
         case JSR202:
            this.needStackMap = true;
            break;
         default:
            this.needStackMap = false;
      }

      this.state = new State();
      this.lvar = new LocalVar[20];
      this.pool = var10;
   }

   public static int typecode(Type var0) {
      switch (var0.getTag()) {
         case BYTE:
            return 5;
         case SHORT:
            return 7;
         case CHAR:
            return 6;
         case INT:
            return 0;
         case LONG:
            return 1;
         case FLOAT:
            return 2;
         case DOUBLE:
            return 3;
         case BOOLEAN:
            return 5;
         case VOID:
            return 8;
         case CLASS:
         case ARRAY:
         case METHOD:
         case BOT:
         case TYPEVAR:
         case UNINITIALIZED_THIS:
         case UNINITIALIZED_OBJECT:
            return 4;
         default:
            throw new AssertionError("typecode " + var0.getTag());
      }
   }

   public static int truncate(int var0) {
      switch (var0) {
         case 5:
         case 6:
         case 7:
            return 0;
         default:
            return var0;
      }
   }

   public static int width(int var0) {
      switch (var0) {
         case 1:
         case 3:
            return 2;
         case 8:
            return 0;
         default:
            return 1;
      }
   }

   public static int width(Type var0) {
      return var0 == null ? 1 : width(typecode(var0));
   }

   public static int width(List var0) {
      int var1 = 0;

      for(List var2 = var0; var2.nonEmpty(); var2 = var2.tail) {
         var1 += width((Type)var2.head);
      }

      return var1;
   }

   public static int arraycode(Type var0) {
      switch (var0.getTag()) {
         case BYTE:
            return 8;
         case SHORT:
            return 9;
         case CHAR:
            return 5;
         case INT:
            return 10;
         case LONG:
            return 11;
         case FLOAT:
            return 6;
         case DOUBLE:
            return 7;
         case BOOLEAN:
            return 4;
         case VOID:
         default:
            throw new AssertionError("arraycode " + var0);
         case CLASS:
            return 0;
         case ARRAY:
            return 1;
      }
   }

   public int curCP() {
      if (this.pendingJumps != null) {
         this.resolvePending();
      }

      if (this.pendingStatPos != -1) {
         this.markStatBegin();
      }

      this.fixedPc = true;
      return this.cp;
   }

   private void emit1(int var1) {
      if (this.alive) {
         this.code = ArrayUtils.ensureCapacity(this.code, this.cp);
         this.code[this.cp++] = (byte)var1;
      }
   }

   private void emit2(int var1) {
      if (this.alive) {
         if (this.cp + 2 > this.code.length) {
            this.emit1(var1 >> 8);
            this.emit1(var1);
         } else {
            this.code[this.cp++] = (byte)(var1 >> 8);
            this.code[this.cp++] = (byte)var1;
         }

      }
   }

   public void emit4(int var1) {
      if (this.alive) {
         if (this.cp + 4 > this.code.length) {
            this.emit1(var1 >> 24);
            this.emit1(var1 >> 16);
            this.emit1(var1 >> 8);
            this.emit1(var1);
         } else {
            this.code[this.cp++] = (byte)(var1 >> 24);
            this.code[this.cp++] = (byte)(var1 >> 16);
            this.code[this.cp++] = (byte)(var1 >> 8);
            this.code[this.cp++] = (byte)var1;
         }

      }
   }

   private void emitop(int var1) {
      if (this.pendingJumps != null) {
         this.resolvePending();
      }

      if (this.alive) {
         if (this.pendingStatPos != -1) {
            this.markStatBegin();
         }

         if (this.pendingStackMap) {
            this.pendingStackMap = false;
            this.emitStackMap();
         }

         if (this.debugCode) {
            System.err.println("emit@" + this.cp + " stack=" + this.state.stacksize + ": " + mnem(var1));
         }

         this.emit1(var1);
      }

   }

   void postop() {
      Assert.check(this.alive || this.state.stacksize == 0);
   }

   public void emitLdc(int var1) {
      if (var1 <= 255) {
         this.emitop1(18, var1);
      } else {
         this.emitop2(19, var1);
      }

   }

   public void emitMultianewarray(int var1, int var2, Type var3) {
      this.emitop(197);
      if (this.alive) {
         this.emit2(var2);
         this.emit1(var1);
         this.state.pop(var1);
         this.state.push(var3);
      }
   }

   public void emitNewarray(int var1, Type var2) {
      this.emitop(188);
      if (this.alive) {
         this.emit1(var1);
         this.state.pop(1);
         this.state.push(var2);
      }
   }

   public void emitAnewarray(int var1, Type var2) {
      this.emitop(189);
      if (this.alive) {
         this.emit2(var1);
         this.state.pop(1);
         this.state.push(var2);
      }
   }

   public void emitInvokeinterface(int var1, Type var2) {
      int var3 = width(var2.getParameterTypes());
      this.emitop(185);
      if (this.alive) {
         this.emit2(var1);
         this.emit1(var3 + 1);
         this.emit1(0);
         this.state.pop(var3 + 1);
         this.state.push(var2.getReturnType());
      }
   }

   public void emitInvokespecial(int var1, Type var2) {
      int var3 = width(var2.getParameterTypes());
      this.emitop(183);
      if (this.alive) {
         this.emit2(var1);
         Symbol var4 = (Symbol)this.pool.pool[var1];
         this.state.pop(var3);
         if (var4.isConstructor()) {
            this.state.markInitialized((UninitializedType)this.state.peek());
         }

         this.state.pop(1);
         this.state.push(var2.getReturnType());
      }
   }

   public void emitInvokestatic(int var1, Type var2) {
      int var3 = width(var2.getParameterTypes());
      this.emitop(184);
      if (this.alive) {
         this.emit2(var1);
         this.state.pop(var3);
         this.state.push(var2.getReturnType());
      }
   }

   public void emitInvokevirtual(int var1, Type var2) {
      int var3 = width(var2.getParameterTypes());
      this.emitop(182);
      if (this.alive) {
         this.emit2(var1);
         this.state.pop(var3 + 1);
         this.state.push(var2.getReturnType());
      }
   }

   public void emitInvokedynamic(int var1, Type var2) {
      int var3 = width(var2.getParameterTypes());
      this.emitop(186);
      if (this.alive) {
         this.emit2(var1);
         this.emit2(0);
         this.state.pop(var3);
         this.state.push(var2.getReturnType());
      }
   }

   public void emitop0(int var1) {
      this.emitop(var1);
      if (this.alive) {
         Type var2;
         Type var3;
         Type var4;
         switch (var1) {
            case 0:
            case 116:
            case 117:
            case 118:
            case 119:
            case 145:
            case 146:
            case 147:
               break;
            case 1:
               this.state.push(this.syms.botType);
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 26:
            case 27:
            case 28:
            case 29:
               this.state.push(this.syms.intType);
               break;
            case 9:
            case 10:
            case 30:
            case 31:
            case 32:
            case 33:
               this.state.push(this.syms.longType);
               break;
            case 11:
            case 12:
            case 13:
            case 34:
            case 35:
            case 36:
            case 37:
               this.state.push(this.syms.floatType);
               break;
            case 14:
            case 15:
            case 38:
            case 39:
            case 40:
            case 41:
               this.state.push(this.syms.doubleType);
               break;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 132:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 168:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 192:
            case 193:
            default:
               throw new AssertionError(mnem(var1));
            case 42:
               this.state.push(this.lvar[0].sym.type);
               break;
            case 43:
               this.state.push(this.lvar[1].sym.type);
               break;
            case 44:
               this.state.push(this.lvar[2].sym.type);
               break;
            case 45:
               this.state.push(this.lvar[3].sym.type);
               break;
            case 46:
            case 51:
            case 52:
            case 53:
               this.state.pop(2);
               this.state.push(this.syms.intType);
               break;
            case 47:
               this.state.pop(2);
               this.state.push(this.syms.longType);
               break;
            case 48:
               this.state.pop(2);
               this.state.push(this.syms.floatType);
               break;
            case 49:
               this.state.pop(2);
               this.state.push(this.syms.doubleType);
               break;
            case 50:
               this.state.pop(1);
               var2 = this.state.stack[this.state.stacksize - 1];
               this.state.pop(1);
               var3 = var2.hasTag(TypeTag.BOT) ? this.syms.objectType : this.types.erasure(this.types.elemtype(var2));
               this.state.push(var3);
               break;
            case 59:
            case 60:
            case 61:
            case 62:
            case 67:
            case 68:
            case 69:
            case 70:
            case 75:
            case 76:
            case 77:
            case 78:
            case 87:
            case 121:
            case 123:
            case 125:
               this.state.pop(1);
               break;
            case 63:
            case 64:
            case 65:
            case 66:
            case 71:
            case 72:
            case 73:
            case 74:
            case 88:
               this.state.pop(2);
               break;
            case 79:
            case 81:
            case 85:
            case 86:
               this.state.pop(3);
               break;
            case 80:
            case 82:
               this.state.pop(4);
               break;
            case 83:
               this.state.pop(3);
               break;
            case 84:
               this.state.pop(3);
               break;
            case 89:
               this.state.push(this.state.stack[this.state.stacksize - 1]);
               break;
            case 90:
               var2 = this.state.pop1();
               var3 = this.state.pop1();
               this.state.push(var2);
               this.state.push(var3);
               this.state.push(var2);
               break;
            case 91:
               var2 = this.state.pop1();
               if (this.state.stack[this.state.stacksize - 1] != null) {
                  var3 = this.state.pop1();
                  var4 = this.state.pop1();
                  this.state.push(var2);
                  this.state.push(var4);
                  this.state.push(var3);
                  this.state.push(var2);
               } else {
                  var3 = this.state.pop2();
                  this.state.push(var2);
                  this.state.push(var3);
                  this.state.push(var2);
               }
               break;
            case 92:
               if (this.state.stack[this.state.stacksize - 1] != null) {
                  var2 = this.state.pop1();
                  var3 = this.state.pop1();
                  this.state.push(var3);
                  this.state.push(var2);
                  this.state.push(var3);
                  this.state.push(var2);
               } else {
                  var2 = this.state.pop2();
                  this.state.push(var2);
                  this.state.push(var2);
               }
               break;
            case 93:
               if (this.state.stack[this.state.stacksize - 1] != null) {
                  var2 = this.state.pop1();
                  var3 = this.state.pop1();
                  var4 = this.state.pop1();
                  this.state.push(var3);
                  this.state.push(var2);
                  this.state.push(var4);
                  this.state.push(var3);
                  this.state.push(var2);
               } else {
                  var2 = this.state.pop2();
                  var3 = this.state.pop1();
                  this.state.push(var2);
                  this.state.push(var3);
                  this.state.push(var2);
               }
               break;
            case 94:
               if (this.state.stack[this.state.stacksize - 1] != null) {
                  var2 = this.state.pop1();
                  var3 = this.state.pop1();
                  if (this.state.stack[this.state.stacksize - 1] != null) {
                     var4 = this.state.pop1();
                     Type var5 = this.state.pop1();
                     this.state.push(var3);
                     this.state.push(var2);
                     this.state.push(var5);
                     this.state.push(var4);
                     this.state.push(var3);
                     this.state.push(var2);
                  } else {
                     var4 = this.state.pop2();
                     this.state.push(var3);
                     this.state.push(var2);
                     this.state.push(var4);
                     this.state.push(var3);
                     this.state.push(var2);
                  }
               } else {
                  var2 = this.state.pop2();
                  if (this.state.stack[this.state.stacksize - 1] != null) {
                     var3 = this.state.pop1();
                     var4 = this.state.pop1();
                     this.state.push(var2);
                     this.state.push(var4);
                     this.state.push(var3);
                     this.state.push(var2);
                  } else {
                     var3 = this.state.pop2();
                     this.state.push(var2);
                     this.state.push(var3);
                     this.state.push(var2);
                  }
               }
               break;
            case 95:
               var2 = this.state.pop1();
               var3 = this.state.pop1();
               this.state.push(var2);
               this.state.push(var3);
               break;
            case 96:
            case 100:
            case 104:
            case 108:
            case 112:
            case 120:
            case 122:
            case 124:
            case 126:
            case 128:
            case 130:
               this.state.pop(1);
               break;
            case 97:
            case 101:
            case 105:
            case 109:
            case 113:
            case 127:
            case 129:
            case 131:
               this.state.pop(2);
               break;
            case 98:
            case 102:
            case 106:
            case 110:
            case 114:
               this.state.pop(1);
               break;
            case 99:
            case 103:
            case 107:
            case 111:
            case 115:
               this.state.pop(2);
               break;
            case 133:
               this.state.pop(1);
               this.state.push(this.syms.longType);
               break;
            case 134:
               this.state.pop(1);
               this.state.push(this.syms.floatType);
               break;
            case 135:
               this.state.pop(1);
               this.state.push(this.syms.doubleType);
               break;
            case 136:
               this.state.pop(2);
               this.state.push(this.syms.intType);
               break;
            case 137:
               this.state.pop(2);
               this.state.push(this.syms.floatType);
               break;
            case 138:
               this.state.pop(2);
               this.state.push(this.syms.doubleType);
               break;
            case 139:
               this.state.pop(1);
               this.state.push(this.syms.intType);
               break;
            case 140:
               this.state.pop(1);
               this.state.push(this.syms.longType);
               break;
            case 141:
               this.state.pop(1);
               this.state.push(this.syms.doubleType);
               break;
            case 142:
               this.state.pop(2);
               this.state.push(this.syms.intType);
               break;
            case 143:
               this.state.pop(2);
               this.state.push(this.syms.longType);
               break;
            case 144:
               this.state.pop(2);
               this.state.push(this.syms.floatType);
               break;
            case 148:
               this.state.pop(4);
               this.state.push(this.syms.intType);
               break;
            case 149:
            case 150:
               this.state.pop(2);
               this.state.push(this.syms.intType);
               break;
            case 151:
            case 152:
               this.state.pop(4);
               this.state.push(this.syms.intType);
               break;
            case 167:
               this.markDead();
               break;
            case 169:
               this.markDead();
               break;
            case 170:
            case 171:
               this.state.pop(1);
               break;
            case 172:
            case 174:
            case 176:
               Assert.check(this.state.nlocks == 0);
               this.state.pop(1);
               this.markDead();
               break;
            case 173:
            case 175:
               Assert.check(this.state.nlocks == 0);
               this.state.pop(2);
               this.markDead();
               break;
            case 177:
               Assert.check(this.state.nlocks == 0);
               this.markDead();
               break;
            case 190:
               this.state.pop(1);
               this.state.push(this.syms.intType);
               break;
            case 191:
               this.state.pop(1);
               this.markDead();
               break;
            case 194:
            case 195:
               this.state.pop(1);
               break;
            case 196:
               return;
         }

         this.postop();
      }
   }

   public void emitop1(int var1, int var2) {
      this.emitop(var1);
      if (this.alive) {
         this.emit1(var2);
         switch (var1) {
            case 16:
               this.state.push(this.syms.intType);
               break;
            case 18:
               this.state.push(this.typeForPool(this.pool.pool[var2]));
               break;
            default:
               throw new AssertionError(mnem(var1));
         }

         this.postop();
      }
   }

   private Type typeForPool(Object var1) {
      if (var1 instanceof Integer) {
         return this.syms.intType;
      } else if (var1 instanceof Float) {
         return this.syms.floatType;
      } else if (var1 instanceof String) {
         return this.syms.stringType;
      } else if (var1 instanceof Long) {
         return this.syms.longType;
      } else if (var1 instanceof Double) {
         return this.syms.doubleType;
      } else if (var1 instanceof Symbol.ClassSymbol) {
         return this.syms.classType;
      } else if (var1 instanceof Pool.MethodHandle) {
         return this.syms.methodHandleType;
      } else if (var1 instanceof Types.UniqueType) {
         return this.typeForPool(((Types.UniqueType)var1).type);
      } else {
         if (var1 instanceof Type) {
            Type var2 = ((Type)var1).unannotatedType();
            if (var2 instanceof Type.ArrayType) {
               return this.syms.classType;
            }

            if (var2 instanceof Type.MethodType) {
               return this.syms.methodTypeType;
            }
         }

         throw new AssertionError("Invalid type of constant pool entry: " + var1.getClass());
      }
   }

   public void emitop1w(int var1, int var2) {
      if (var2 > 255) {
         this.emitop(196);
         this.emitop(var1);
         this.emit2(var2);
      } else {
         this.emitop(var1);
         this.emit1(var2);
      }

      if (this.alive) {
         switch (var1) {
            case 21:
               this.state.push(this.syms.intType);
               break;
            case 22:
               this.state.push(this.syms.longType);
               break;
            case 23:
               this.state.push(this.syms.floatType);
               break;
            case 24:
               this.state.push(this.syms.doubleType);
               break;
            case 25:
               this.state.push(this.lvar[var2].sym.type);
               break;
            case 54:
            case 56:
            case 58:
               this.state.pop(1);
               break;
            case 55:
            case 57:
               this.state.pop(2);
               break;
            case 169:
               this.markDead();
               break;
            default:
               throw new AssertionError(mnem(var1));
         }

         this.postop();
      }
   }

   public void emitop1w(int var1, int var2, int var3) {
      if (var2 <= 255 && var3 >= -128 && var3 <= 127) {
         this.emitop(var1);
         this.emit1(var2);
         this.emit1(var3);
      } else {
         this.emitop(196);
         this.emitop(var1);
         this.emit2(var2);
         this.emit2(var3);
      }

      if (this.alive) {
         switch (var1) {
            case 132:
               return;
            default:
               throw new AssertionError(mnem(var1));
         }
      }
   }

   public void emitop2(int var1, int var2) {
      this.emitop(var1);
      if (this.alive) {
         this.emit2(var2);
         switch (var1) {
            case 17:
               this.state.push(this.syms.intType);
               break;
            case 19:
               this.state.push(this.typeForPool(this.pool.pool[var2]));
               break;
            case 20:
               this.state.push(this.typeForPool(this.pool.pool[var2]));
               break;
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 198:
            case 199:
               this.state.pop(1);
               break;
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
               this.state.pop(2);
               break;
            case 167:
               this.markDead();
            case 168:
               break;
            case 178:
               this.state.push(((Symbol)((Symbol)this.pool.pool[var2])).erasure(this.types));
               break;
            case 179:
               this.state.pop(((Symbol)((Symbol)this.pool.pool[var2])).erasure(this.types));
               break;
            case 180:
               this.state.pop(1);
               this.state.push(((Symbol)((Symbol)this.pool.pool[var2])).erasure(this.types));
               break;
            case 181:
               this.state.pop(((Symbol)((Symbol)this.pool.pool[var2])).erasure(this.types));
               this.state.pop(1);
               break;
            case 187:
               Object var3;
               if (this.pool.pool[var2] instanceof Types.UniqueType) {
                  var3 = ((Types.UniqueType)((Types.UniqueType)this.pool.pool[var2])).type.tsym;
               } else {
                  var3 = (Symbol)((Symbol)this.pool.pool[var2]);
               }

               this.state.push(UninitializedType.uninitializedObject(((Symbol)var3).erasure(this.types), this.cp - 3));
               break;
            case 192:
               this.state.pop(1);
               Object var4 = this.pool.pool[var2];
               Type var5 = var4 instanceof Symbol ? ((Symbol)var4).erasure(this.types) : this.types.erasure(((Types.UniqueType)var4).type);
               this.state.push(var5);
               break;
            case 193:
               this.state.pop(1);
               this.state.push(this.syms.intType);
               break;
            default:
               throw new AssertionError(mnem(var1));
         }

      }
   }

   public void emitop4(int var1, int var2) {
      this.emitop(var1);
      if (this.alive) {
         this.emit4(var2);
         switch (var1) {
            case 200:
               this.markDead();
            case 201:
               return;
            default:
               throw new AssertionError(mnem(var1));
         }
      }
   }

   public void align(int var1) {
      if (this.alive) {
         while(this.cp % var1 != 0) {
            this.emitop0(0);
         }
      }

   }

   private void put1(int var1, int var2) {
      this.code[var1] = (byte)var2;
   }

   private void put2(int var1, int var2) {
      this.put1(var1, var2 >> 8);
      this.put1(var1 + 1, var2);
   }

   public void put4(int var1, int var2) {
      this.put1(var1, var2 >> 24);
      this.put1(var1 + 1, var2 >> 16);
      this.put1(var1 + 2, var2 >> 8);
      this.put1(var1 + 3, var2);
   }

   private int get1(int var1) {
      return this.code[var1] & 255;
   }

   private int get2(int var1) {
      return this.get1(var1) << 8 | this.get1(var1 + 1);
   }

   public int get4(int var1) {
      return this.get1(var1) << 24 | this.get1(var1 + 1) << 16 | this.get1(var1 + 2) << 8 | this.get1(var1 + 3);
   }

   public boolean isAlive() {
      return this.alive || this.pendingJumps != null;
   }

   public void markDead() {
      this.alive = false;
   }

   public int entryPoint() {
      int var1 = this.curCP();
      this.alive = true;
      this.pendingStackMap = this.needStackMap;
      return var1;
   }

   public int entryPoint(State var1) {
      int var2 = this.curCP();
      this.alive = true;
      State var3 = var1.dup();
      this.setDefined(var3.defined);
      this.state = var3;
      Assert.check(var1.stacksize <= this.max_stack);
      if (this.debugCode) {
         System.err.println("entry point " + var1);
      }

      this.pendingStackMap = this.needStackMap;
      return var2;
   }

   public int entryPoint(State var1, Type var2) {
      int var3 = this.curCP();
      this.alive = true;
      State var4 = var1.dup();
      this.setDefined(var4.defined);
      this.state = var4;
      Assert.check(var1.stacksize <= this.max_stack);
      this.state.push(var2);
      if (this.debugCode) {
         System.err.println("entry point " + var1);
      }

      this.pendingStackMap = this.needStackMap;
      return var3;
   }

   public void emitStackMap() {
      int var1 = this.curCP();
      if (this.needStackMap) {
         switch (this.stackMap) {
            case CLDC:
               this.emitCLDCStackMap(var1, this.getLocalsSize());
               break;
            case JSR202:
               this.emitStackMapFrame(var1, this.getLocalsSize());
               break;
            default:
               throw new AssertionError("Should have chosen a stackmap format");
         }

         if (this.debugCode) {
            this.state.dump(var1);
         }

      }
   }

   private int getLocalsSize() {
      int var1 = 0;

      for(int var2 = this.max_locals - 1; var2 >= 0; --var2) {
         if (this.state.defined.isMember(var2) && this.lvar[var2] != null) {
            var1 = var2 + width(this.lvar[var2].sym.erasure(this.types));
            break;
         }
      }

      return var1;
   }

   void emitCLDCStackMap(int var1, int var2) {
      if (this.lastStackMapPC == var1) {
         this.stackMapBuffer[--this.stackMapBufferSize] = null;
      }

      this.lastStackMapPC = var1;
      if (this.stackMapBuffer == null) {
         this.stackMapBuffer = new StackMapFrame[20];
      } else {
         this.stackMapBuffer = (StackMapFrame[])ArrayUtils.ensureCapacity((Object[])this.stackMapBuffer, this.stackMapBufferSize);
      }

      StackMapFrame var3 = this.stackMapBuffer[this.stackMapBufferSize++] = new StackMapFrame();
      var3.pc = var1;
      var3.locals = new Type[var2];

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         if (this.state.defined.isMember(var4) && this.lvar[var4] != null) {
            Type var5 = this.lvar[var4].sym.type;
            if (!(var5 instanceof UninitializedType)) {
               var5 = this.types.erasure(var5);
            }

            var3.locals[var4] = var5;
         }
      }

      var3.stack = new Type[this.state.stacksize];

      for(var4 = 0; var4 < this.state.stacksize; ++var4) {
         var3.stack[var4] = this.state.stack[var4];
      }

   }

   void emitStackMapFrame(int var1, int var2) {
      if (this.lastFrame == null) {
         this.lastFrame = this.getInitialFrame();
      } else if (this.lastFrame.pc == var1) {
         this.stackMapTableBuffer[--this.stackMapBufferSize] = null;
         this.lastFrame = this.frameBeforeLast;
         this.frameBeforeLast = null;
      }

      StackMapFrame var3 = new StackMapFrame();
      var3.pc = var1;
      int var4 = 0;
      Type[] var5 = new Type[var2];

      int var6;
      for(var6 = 0; var6 < var2; ++var4) {
         if (this.state.defined.isMember(var6) && this.lvar[var6] != null) {
            Type var7 = this.lvar[var6].sym.type;
            if (!(var7 instanceof UninitializedType)) {
               var7 = this.types.erasure(var7);
            }

            var5[var6] = var7;
            if (width(var7) > 1) {
               ++var6;
            }
         }

         ++var6;
      }

      var3.locals = new Type[var4];
      var6 = 0;

      int var8;
      for(var8 = 0; var6 < var2; ++var8) {
         Assert.check(var8 < var4);
         var3.locals[var8] = var5[var6];
         if (width(var5[var6]) > 1) {
            ++var6;
         }

         ++var6;
      }

      var6 = 0;

      for(var8 = 0; var8 < this.state.stacksize; ++var8) {
         if (this.state.stack[var8] != null) {
            ++var6;
         }
      }

      var3.stack = new Type[var6];
      var6 = 0;

      for(var8 = 0; var8 < this.state.stacksize; ++var8) {
         if (this.state.stack[var8] != null) {
            var3.stack[var6++] = this.types.erasure(this.state.stack[var8]);
         }
      }

      if (this.stackMapTableBuffer == null) {
         this.stackMapTableBuffer = new ClassWriter.StackMapTableFrame[20];
      } else {
         this.stackMapTableBuffer = (ClassWriter.StackMapTableFrame[])ArrayUtils.ensureCapacity((Object[])this.stackMapTableBuffer, this.stackMapBufferSize);
      }

      this.stackMapTableBuffer[this.stackMapBufferSize++] = ClassWriter.StackMapTableFrame.getInstance(var3, this.lastFrame.pc, this.lastFrame.locals, this.types);
      this.frameBeforeLast = this.lastFrame;
      this.lastFrame = var3;
   }

   StackMapFrame getInitialFrame() {
      StackMapFrame var1 = new StackMapFrame();
      List var2 = ((Type.MethodType)this.meth.externalType(this.types)).argtypes;
      int var3 = var2.length();
      int var4 = 0;
      if (!this.meth.isStatic()) {
         Type var5 = this.meth.owner.type;
         var1.locals = new Type[var3 + 1];
         if (this.meth.isConstructor() && var5 != this.syms.objectType) {
            var1.locals[var4++] = UninitializedType.uninitializedThis(var5);
         } else {
            var1.locals[var4++] = this.types.erasure(var5);
         }
      } else {
         var1.locals = new Type[var3];
      }

      Type var6;
      for(Iterator var7 = var2.iterator(); var7.hasNext(); var1.locals[var4++] = this.types.erasure(var6)) {
         var6 = (Type)var7.next();
      }

      var1.pc = -1;
      var1.stack = null;
      return var1;
   }

   public static int negate(int var0) {
      if (var0 == 198) {
         return 199;
      } else {
         return var0 == 199 ? 198 : (var0 + 1 ^ 1) - 1;
      }
   }

   public int emitJump(int var1) {
      if (!this.fatcode) {
         this.emitop2(var1, 0);
         return this.cp - 3;
      } else {
         if (var1 != 167 && var1 != 168) {
            this.emitop2(negate(var1), 8);
            this.emitop4(200, 0);
            this.alive = true;
            this.pendingStackMap = this.needStackMap;
         } else {
            this.emitop4(var1 + 200 - 167, 0);
         }

         return this.cp - 5;
      }
   }

   public Chain branch(int var1) {
      Chain var2 = null;
      if (var1 == 167) {
         var2 = this.pendingJumps;
         this.pendingJumps = null;
      }

      if (var1 != 168 && this.isAlive()) {
         var2 = new Chain(this.emitJump(var1), var2, this.state.dup());
         this.fixedPc = this.fatcode;
         if (var1 == 167) {
            this.alive = false;
         }
      }

      return var2;
   }

   public void resolve(Chain var1, int var2) {
      boolean var3 = false;

      State var4;
      for(var4 = this.state; var1 != null; var1 = var1.next) {
         Assert.check(this.state != var1.state && (var2 > var1.pc || this.state.stacksize == 0));
         if (var2 >= this.cp) {
            var2 = this.cp;
         } else if (this.get1(var2) == 167) {
            if (this.fatcode) {
               var2 += this.get4(var2 + 1);
            } else {
               var2 += this.get2(var2 + 1);
            }
         }

         if (this.get1(var1.pc) == 167 && var1.pc + 3 == var2 && var2 == this.cp && !this.fixedPc) {
            if (this.varDebugInfo) {
               this.adjustAliveRanges(this.cp, -3);
            }

            this.cp -= 3;
            var2 -= 3;
            if (var1.next == null) {
               this.alive = true;
               break;
            }
         } else {
            if (this.fatcode) {
               this.put4(var1.pc + 1, var2 - var1.pc);
            } else if (var2 - var1.pc >= -32768 && var2 - var1.pc <= 32767) {
               this.put2(var1.pc + 1, var2 - var1.pc);
            } else {
               this.fatcode = true;
            }

            Assert.check(!this.alive || var1.state.stacksize == var4.stacksize && var1.state.nlocks == var4.nlocks);
         }

         this.fixedPc = true;
         if (this.cp == var2) {
            var3 = true;
            if (this.debugCode) {
               System.err.println("resolving chain state=" + var1.state);
            }

            if (this.alive) {
               var4 = var1.state.join(var4);
            } else {
               var4 = var1.state;
               this.alive = true;
            }
         }
      }

      Assert.check(!var3 || this.state != var4);
      if (this.state != var4) {
         this.setDefined(var4.defined);
         this.state = var4;
         this.pendingStackMap = this.needStackMap;
      }

   }

   public void resolve(Chain var1) {
      Assert.check(!this.alive || var1 == null || this.state.stacksize == var1.state.stacksize && this.state.nlocks == var1.state.nlocks);
      this.pendingJumps = mergeChains(var1, this.pendingJumps);
   }

   public void resolvePending() {
      Chain var1 = this.pendingJumps;
      this.pendingJumps = null;
      this.resolve(var1, this.cp);
   }

   public static Chain mergeChains(Chain var0, Chain var1) {
      if (var1 == null) {
         return var0;
      } else if (var0 == null) {
         return var1;
      } else {
         Assert.check(var0.state.stacksize == var1.state.stacksize && var0.state.nlocks == var1.state.nlocks);
         return var0.pc < var1.pc ? new Chain(var1.pc, mergeChains(var0, var1.next), var1.state) : new Chain(var0.pc, mergeChains(var0.next, var1), var0.state);
      }
   }

   public void addCatch(char var1, char var2, char var3, char var4) {
      this.catchInfo.append(new char[]{var1, var2, var3, var4});
   }

   public void compressCatchTable() {
      ListBuffer var1 = new ListBuffer();
      List var2 = List.nil();

      Iterator var3;
      char[] var4;
      for(var3 = this.catchInfo.iterator(); var3.hasNext(); var2 = var2.prepend(Integer.valueOf(var4[2]))) {
         var4 = (char[])var3.next();
      }

      var3 = this.catchInfo.iterator();

      while(true) {
         char var5;
         char var6;
         do {
            do {
               if (!var3.hasNext()) {
                  this.catchInfo = var1;
                  return;
               }

               var4 = (char[])var3.next();
               var5 = var4[0];
               var6 = var4[1];
            } while(var5 == var6);
         } while(var5 == var6 - 1 && var2.contains(Integer.valueOf(var5)));

         var1.append(var4);
      }
   }

   public void addLineNumber(char var1, char var2) {
      if (this.lineDebugInfo) {
         if (this.lineInfo.nonEmpty() && ((char[])this.lineInfo.head)[0] == var1) {
            this.lineInfo = this.lineInfo.tail;
         }

         if (this.lineInfo.isEmpty() || ((char[])this.lineInfo.head)[1] != var2) {
            this.lineInfo = this.lineInfo.prepend(new char[]{var1, var2});
         }
      }

   }

   public void statBegin(int var1) {
      if (var1 != -1) {
         this.pendingStatPos = var1;
      }

   }

   public void markStatBegin() {
      if (this.alive && this.lineDebugInfo) {
         int var1 = this.lineMap.getLineNumber(this.pendingStatPos);
         char var2 = (char)this.cp;
         char var3 = (char)var1;
         if (var2 == this.cp && var3 == var1) {
            this.addLineNumber(var2, var3);
         }
      }

      this.pendingStatPos = -1;
   }

   private void addLocalVar(Symbol.VarSymbol var1) {
      int var2 = var1.adr;
      this.lvar = (LocalVar[])ArrayUtils.ensureCapacity((Object[])this.lvar, var2 + 1);
      Assert.checkNull(this.lvar[var2]);
      if (this.pendingJumps != null) {
         this.resolvePending();
      }

      this.lvar[var2] = new LocalVar(var1);
      this.state.defined.excl(var2);
   }

   void adjustAliveRanges(int var1, int var2) {
      LocalVar[] var3 = this.lvar;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         LocalVar var6 = var3[var5];
         if (var6 != null) {
            Iterator var7 = var6.aliveRanges.iterator();

            while(var7.hasNext()) {
               LocalVar.Range var8 = (LocalVar.Range)var7.next();
               if (var8.closed() && var8.start_pc + var8.length >= var1) {
                  var8.length = (char)(var8.length + var2);
               }
            }
         }
      }

   }

   public int getLVTSize() {
      int var1 = this.varBufferSize;

      for(int var2 = 0; var2 < this.varBufferSize; ++var2) {
         LocalVar var3 = this.varBuffer[var2];
         var1 += var3.aliveRanges.size() - 1;
      }

      return var1;
   }

   public void setDefined(Bits var1) {
      if (this.alive && var1 != this.state.defined) {
         Bits var2 = (new Bits(this.state.defined)).xorSet(var1);

         for(int var3 = var2.nextBit(0); var3 >= 0; var3 = var2.nextBit(var3 + 1)) {
            if (var3 >= this.nextreg) {
               this.state.defined.excl(var3);
            } else if (this.state.defined.isMember(var3)) {
               this.setUndefined(var3);
            } else {
               this.setDefined(var3);
            }
         }
      }

   }

   public void setDefined(int var1) {
      LocalVar var2 = this.lvar[var1];
      if (var2 == null) {
         this.state.defined.excl(var1);
      } else {
         this.state.defined.incl(var1);
         if (this.cp < 65535) {
            var2.openRange((char)this.cp);
         }
      }

   }

   public void setUndefined(int var1) {
      this.state.defined.excl(var1);
      if (var1 < this.lvar.length && this.lvar[var1] != null && this.lvar[var1].isLastRangeInitialized()) {
         LocalVar var2 = this.lvar[var1];
         char var3 = (char)(this.curCP() - var2.lastRange().start_pc);
         if (var3 < '\uffff') {
            this.lvar[var1] = var2.dup();
            var2.closeRange(var3);
            this.putVar(var2);
         } else {
            var2.removeLastRange();
         }
      }

   }

   private void endScope(int var1) {
      LocalVar var2 = this.lvar[var1];
      if (var2 != null) {
         if (var2.isLastRangeInitialized()) {
            char var3 = (char)(this.curCP() - var2.lastRange().start_pc);
            if (var3 < '\uffff') {
               var2.closeRange(var3);
               this.putVar(var2);
               this.fillLocalVarPosition(var2);
            }
         }

         this.lvar[var1] = null;
      }

      this.state.defined.excl(var1);
   }

   private void fillLocalVarPosition(LocalVar var1) {
      if (var1 != null && var1.sym != null && var1.sym.hasTypeAnnotations()) {
         TypeAnnotationPosition var4;
         for(Iterator var2 = var1.sym.getRawTypeAttributes().iterator(); var2.hasNext(); var4.isValidOffset = true) {
            Attribute.TypeCompound var3 = (Attribute.TypeCompound)var2.next();
            var4 = var3.position;
            LocalVar.Range var5 = var1.getWidestRange();
            var4.lvarOffset = new int[]{var5.start_pc};
            var4.lvarLength = new int[]{var5.length};
            var4.lvarIndex = new int[]{var1.reg};
         }

      }
   }

   public void fillExceptionParameterPositions() {
      for(int var1 = 0; var1 < this.varBufferSize; ++var1) {
         LocalVar var2 = this.varBuffer[var1];
         if (var2 != null && var2.sym != null && var2.sym.hasTypeAnnotations() && var2.sym.isExceptionParameter()) {
            Iterator var3 = var2.sym.getRawTypeAttributes().iterator();

            while(var3.hasNext()) {
               Attribute.TypeCompound var4 = (Attribute.TypeCompound)var3.next();
               TypeAnnotationPosition var5 = var4.position;
               if (var5.type_index != -666) {
                  var5.exception_index = this.findExceptionIndex(var5.type_index);
                  var5.type_index = -666;
               }
            }
         }
      }

   }

   private int findExceptionIndex(int var1) {
      if (var1 == Integer.MIN_VALUE) {
         return -1;
      } else {
         List var2 = this.catchInfo.toList();
         int var3 = this.catchInfo.length();

         for(int var4 = 0; var4 < var3; ++var4) {
            char[] var5 = (char[])var2.head;
            var2 = var2.tail;
            char var6 = var5[3];
            if (var1 == var6) {
               return var4;
            }
         }

         return -1;
      }
   }

   void putVar(LocalVar var1) {
      boolean var2 = this.varDebugInfo || var1.sym.isExceptionParameter() && var1.sym.hasTypeAnnotations();
      if (var2) {
         boolean var3 = (var1.sym.flags() & 4096L) != 0L && ((var1.sym.owner.flags() & 562949953421312L) == 0L || (var1.sym.flags() & 8589934592L) == 0L);
         if (!var3) {
            if (this.varBuffer == null) {
               this.varBuffer = new LocalVar[20];
            } else {
               this.varBuffer = (LocalVar[])ArrayUtils.ensureCapacity((Object[])this.varBuffer, this.varBufferSize);
            }

            this.varBuffer[this.varBufferSize++] = var1;
         }
      }
   }

   private int newLocal(int var1) {
      int var2 = this.nextreg;
      int var3 = width(var1);
      this.nextreg = var2 + var3;
      if (this.nextreg > this.max_locals) {
         this.max_locals = this.nextreg;
      }

      return var2;
   }

   private int newLocal(Type var1) {
      return this.newLocal(typecode(var1));
   }

   public int newLocal(Symbol.VarSymbol var1) {
      int var2 = var1.adr = this.newLocal(var1.erasure(this.types));
      this.addLocalVar(var1);
      return var2;
   }

   public void newRegSegment() {
      this.nextreg = this.max_locals;
   }

   public void endScopes(int var1) {
      int var2 = this.nextreg;
      this.nextreg = var1;

      for(int var3 = this.nextreg; var3 < var2; ++var3) {
         this.endScope(var3);
      }

   }

   public static String mnem(int var0) {
      return Code.Mneumonics.mnem[var0];
   }

   static {
      jsrReturnValue = new Type.JCPrimitiveType(TypeTag.INT, (Symbol.TypeSymbol)null);
   }

   private static class Mneumonics {
      private static final String[] mnem = new String[203];

      static {
         mnem[0] = "nop";
         mnem[1] = "aconst_null";
         mnem[2] = "iconst_m1";
         mnem[3] = "iconst_0";
         mnem[4] = "iconst_1";
         mnem[5] = "iconst_2";
         mnem[6] = "iconst_3";
         mnem[7] = "iconst_4";
         mnem[8] = "iconst_5";
         mnem[9] = "lconst_0";
         mnem[10] = "lconst_1";
         mnem[11] = "fconst_0";
         mnem[12] = "fconst_1";
         mnem[13] = "fconst_2";
         mnem[14] = "dconst_0";
         mnem[15] = "dconst_1";
         mnem[16] = "bipush";
         mnem[17] = "sipush";
         mnem[18] = "ldc1";
         mnem[19] = "ldc2";
         mnem[20] = "ldc2w";
         mnem[21] = "iload";
         mnem[22] = "lload";
         mnem[23] = "fload";
         mnem[24] = "dload";
         mnem[25] = "aload";
         mnem[26] = "iload_0";
         mnem[30] = "lload_0";
         mnem[34] = "fload_0";
         mnem[38] = "dload_0";
         mnem[42] = "aload_0";
         mnem[27] = "iload_1";
         mnem[31] = "lload_1";
         mnem[35] = "fload_1";
         mnem[39] = "dload_1";
         mnem[43] = "aload_1";
         mnem[28] = "iload_2";
         mnem[32] = "lload_2";
         mnem[36] = "fload_2";
         mnem[40] = "dload_2";
         mnem[44] = "aload_2";
         mnem[29] = "iload_3";
         mnem[33] = "lload_3";
         mnem[37] = "fload_3";
         mnem[41] = "dload_3";
         mnem[45] = "aload_3";
         mnem[46] = "iaload";
         mnem[47] = "laload";
         mnem[48] = "faload";
         mnem[49] = "daload";
         mnem[50] = "aaload";
         mnem[51] = "baload";
         mnem[52] = "caload";
         mnem[53] = "saload";
         mnem[54] = "istore";
         mnem[55] = "lstore";
         mnem[56] = "fstore";
         mnem[57] = "dstore";
         mnem[58] = "astore";
         mnem[59] = "istore_0";
         mnem[63] = "lstore_0";
         mnem[67] = "fstore_0";
         mnem[71] = "dstore_0";
         mnem[75] = "astore_0";
         mnem[60] = "istore_1";
         mnem[64] = "lstore_1";
         mnem[68] = "fstore_1";
         mnem[72] = "dstore_1";
         mnem[76] = "astore_1";
         mnem[61] = "istore_2";
         mnem[65] = "lstore_2";
         mnem[69] = "fstore_2";
         mnem[73] = "dstore_2";
         mnem[77] = "astore_2";
         mnem[62] = "istore_3";
         mnem[66] = "lstore_3";
         mnem[70] = "fstore_3";
         mnem[74] = "dstore_3";
         mnem[78] = "astore_3";
         mnem[79] = "iastore";
         mnem[80] = "lastore";
         mnem[81] = "fastore";
         mnem[82] = "dastore";
         mnem[83] = "aastore";
         mnem[84] = "bastore";
         mnem[85] = "castore";
         mnem[86] = "sastore";
         mnem[87] = "pop";
         mnem[88] = "pop2";
         mnem[89] = "dup";
         mnem[90] = "dup_x1";
         mnem[91] = "dup_x2";
         mnem[92] = "dup2";
         mnem[93] = "dup2_x1";
         mnem[94] = "dup2_x2";
         mnem[95] = "swap";
         mnem[96] = "iadd";
         mnem[97] = "ladd";
         mnem[98] = "fadd";
         mnem[99] = "dadd";
         mnem[100] = "isub";
         mnem[101] = "lsub";
         mnem[102] = "fsub";
         mnem[103] = "dsub";
         mnem[104] = "imul";
         mnem[105] = "lmul";
         mnem[106] = "fmul";
         mnem[107] = "dmul";
         mnem[108] = "idiv";
         mnem[109] = "ldiv";
         mnem[110] = "fdiv";
         mnem[111] = "ddiv";
         mnem[112] = "imod";
         mnem[113] = "lmod";
         mnem[114] = "fmod";
         mnem[115] = "dmod";
         mnem[116] = "ineg";
         mnem[117] = "lneg";
         mnem[118] = "fneg";
         mnem[119] = "dneg";
         mnem[120] = "ishl";
         mnem[121] = "lshl";
         mnem[122] = "ishr";
         mnem[123] = "lshr";
         mnem[124] = "iushr";
         mnem[125] = "lushr";
         mnem[126] = "iand";
         mnem[127] = "land";
         mnem[128] = "ior";
         mnem[129] = "lor";
         mnem[130] = "ixor";
         mnem[131] = "lxor";
         mnem[132] = "iinc";
         mnem[133] = "i2l";
         mnem[134] = "i2f";
         mnem[135] = "i2d";
         mnem[136] = "l2i";
         mnem[137] = "l2f";
         mnem[138] = "l2d";
         mnem[139] = "f2i";
         mnem[140] = "f2l";
         mnem[141] = "f2d";
         mnem[142] = "d2i";
         mnem[143] = "d2l";
         mnem[144] = "d2f";
         mnem[145] = "int2byte";
         mnem[146] = "int2char";
         mnem[147] = "int2short";
         mnem[148] = "lcmp";
         mnem[149] = "fcmpl";
         mnem[150] = "fcmpg";
         mnem[151] = "dcmpl";
         mnem[152] = "dcmpg";
         mnem[153] = "ifeq";
         mnem[154] = "ifne";
         mnem[155] = "iflt";
         mnem[156] = "ifge";
         mnem[157] = "ifgt";
         mnem[158] = "ifle";
         mnem[159] = "if_icmpeq";
         mnem[160] = "if_icmpne";
         mnem[161] = "if_icmplt";
         mnem[162] = "if_icmpge";
         mnem[163] = "if_icmpgt";
         mnem[164] = "if_icmple";
         mnem[165] = "if_acmpeq";
         mnem[166] = "if_acmpne";
         mnem[167] = "goto_";
         mnem[168] = "jsr";
         mnem[169] = "ret";
         mnem[170] = "tableswitch";
         mnem[171] = "lookupswitch";
         mnem[172] = "ireturn";
         mnem[173] = "lreturn";
         mnem[174] = "freturn";
         mnem[175] = "dreturn";
         mnem[176] = "areturn";
         mnem[177] = "return_";
         mnem[178] = "getstatic";
         mnem[179] = "putstatic";
         mnem[180] = "getfield";
         mnem[181] = "putfield";
         mnem[182] = "invokevirtual";
         mnem[183] = "invokespecial";
         mnem[184] = "invokestatic";
         mnem[185] = "invokeinterface";
         mnem[186] = "invokedynamic";
         mnem[187] = "new_";
         mnem[188] = "newarray";
         mnem[189] = "anewarray";
         mnem[190] = "arraylength";
         mnem[191] = "athrow";
         mnem[192] = "checkcast";
         mnem[193] = "instanceof_";
         mnem[194] = "monitorenter";
         mnem[195] = "monitorexit";
         mnem[196] = "wide";
         mnem[197] = "multianewarray";
         mnem[198] = "if_acmp_null";
         mnem[199] = "if_acmp_nonnull";
         mnem[200] = "goto_w";
         mnem[201] = "jsr_w";
         mnem[202] = "breakpoint";
      }
   }

   static class LocalVar {
      final Symbol.VarSymbol sym;
      final char reg;
      java.util.List aliveRanges = new ArrayList();

      LocalVar(Symbol.VarSymbol var1) {
         this.sym = var1;
         this.reg = (char)var1.adr;
      }

      public LocalVar dup() {
         return new LocalVar(this.sym);
      }

      Range firstRange() {
         return this.aliveRanges.isEmpty() ? null : (Range)this.aliveRanges.get(0);
      }

      Range lastRange() {
         return this.aliveRanges.isEmpty() ? null : (Range)this.aliveRanges.get(this.aliveRanges.size() - 1);
      }

      void removeLastRange() {
         Range var1 = this.lastRange();
         if (var1 != null) {
            this.aliveRanges.remove(var1);
         }

      }

      public String toString() {
         if (this.aliveRanges == null) {
            return "empty local var";
         } else {
            StringBuilder var1 = (new StringBuilder()).append(this.sym).append(" in register ").append(this.reg).append(" \n");
            Iterator var2 = this.aliveRanges.iterator();

            while(var2.hasNext()) {
               Range var3 = (Range)var2.next();
               var1.append(" starts at pc=").append(Integer.toString(var3.start_pc)).append(" length=").append(Integer.toString(var3.length)).append("\n");
            }

            return var1.toString();
         }
      }

      public void openRange(char var1) {
         if (!this.hasOpenRange()) {
            this.aliveRanges.add(new Range(var1));
         }

      }

      public void closeRange(char var1) {
         if (this.isLastRangeInitialized() && var1 > 0) {
            Range var2 = this.lastRange();
            if (var2 != null && var2.length == '\uffff') {
               var2.length = var1;
            }
         } else {
            this.removeLastRange();
         }

      }

      public boolean hasOpenRange() {
         if (this.aliveRanges.isEmpty()) {
            return false;
         } else {
            return this.lastRange().length == '\uffff';
         }
      }

      public boolean isLastRangeInitialized() {
         if (this.aliveRanges.isEmpty()) {
            return false;
         } else {
            return this.lastRange().start_pc != '\uffff';
         }
      }

      public Range getWidestRange() {
         if (this.aliveRanges.isEmpty()) {
            return new Range();
         } else {
            Range var1 = this.firstRange();
            Range var2 = this.lastRange();
            char var3 = (char)(var2.length + (var2.start_pc - var1.start_pc));
            return new Range(var1.start_pc, var3);
         }
      }

      class Range {
         char start_pc = '\uffff';
         char length = '\uffff';

         Range() {
         }

         Range(char var2) {
            this.start_pc = var2;
         }

         Range(char var2, char var3) {
            this.start_pc = var2;
            this.length = var3;
         }

         boolean closed() {
            return this.start_pc != '\uffff' && this.length != '\uffff';
         }

         public String toString() {
            char var1 = this.start_pc;
            char var2 = this.length;
            return "startpc = " + var1 + " length " + var2;
         }
      }
   }

   class State implements Cloneable {
      Bits defined = new Bits();
      Type[] stack = new Type[16];
      int stacksize;
      int[] locks;
      int nlocks;

      State dup() {
         try {
            State var1 = (State)super.clone();
            var1.defined = new Bits(this.defined);
            var1.stack = (Type[])this.stack.clone();
            if (this.locks != null) {
               var1.locks = (int[])this.locks.clone();
            }

            if (Code.this.debugCode) {
               System.err.println("duping state " + this);
               this.dump();
            }

            return var1;
         } catch (CloneNotSupportedException var2) {
            throw new AssertionError(var2);
         }
      }

      void lock(int var1) {
         if (this.locks == null) {
            this.locks = new int[20];
         } else {
            this.locks = ArrayUtils.ensureCapacity(this.locks, this.nlocks);
         }

         this.locks[this.nlocks] = var1;
         ++this.nlocks;
      }

      void unlock(int var1) {
         --this.nlocks;
         Assert.check(this.locks[this.nlocks] == var1);
         this.locks[this.nlocks] = -1;
      }

      void push(Type var1) {
         // $FF: Couldn't be decompiled
      }

      Type pop1() {
         if (Code.this.debugCode) {
            System.err.println("   popping 1");
         }

         --this.stacksize;
         Type var1 = this.stack[this.stacksize];
         this.stack[this.stacksize] = null;
         Assert.check(var1 != null && Code.width(var1) == 1);
         return var1;
      }

      Type peek() {
         return this.stack[this.stacksize - 1];
      }

      Type pop2() {
         if (Code.this.debugCode) {
            System.err.println("   popping 2");
         }

         this.stacksize -= 2;
         Type var1 = this.stack[this.stacksize];
         this.stack[this.stacksize] = null;
         Assert.check(this.stack[this.stacksize + 1] == null && var1 != null && Code.width(var1) == 2);
         return var1;
      }

      void pop(int var1) {
         if (Code.this.debugCode) {
            System.err.println("   popping " + var1);
         }

         while(var1 > 0) {
            this.stack[--this.stacksize] = null;
            --var1;
         }

      }

      void pop(Type var1) {
         this.pop(Code.width(var1));
      }

      void forceStackTop(Type var1) {
         if (Code.this.alive) {
            switch (var1.getTag()) {
               case CLASS:
               case ARRAY:
                  int var2 = Code.width(var1);
                  Type var3 = this.stack[this.stacksize - var2];
                  Assert.check(Code.this.types.isSubtype(Code.this.types.erasure(var3), Code.this.types.erasure(var1)));
                  this.stack[this.stacksize - var2] = var1;
               default:
            }
         }
      }

      void markInitialized(UninitializedType var1) {
         Type var2 = var1.initializedType();

         int var3;
         for(var3 = 0; var3 < this.stacksize; ++var3) {
            if (this.stack[var3] == var1) {
               this.stack[var3] = var2;
            }
         }

         for(var3 = 0; var3 < Code.this.lvar.length; ++var3) {
            LocalVar var4 = Code.this.lvar[var3];
            if (var4 != null && var4.sym.type == var1) {
               Symbol.VarSymbol var5 = var4.sym;
               var5 = var5.clone(var5.owner);
               var5.type = var2;
               LocalVar var6 = Code.this.lvar[var3] = new LocalVar(var5);
               var6.aliveRanges = var4.aliveRanges;
            }
         }

      }

      State join(State var1) {
         this.defined.andSet(var1.defined);
         Assert.check(this.stacksize == var1.stacksize && this.nlocks == var1.nlocks);

         int var6;
         for(int var2 = 0; var2 < this.stacksize; var2 += var6) {
            Type var3 = this.stack[var2];
            Type var4 = var1.stack[var2];
            Type var5 = var3 == var4 ? var3 : (Code.this.types.isSubtype(var3, var4) ? var4 : (Code.this.types.isSubtype(var4, var3) ? var3 : this.error()));
            var6 = Code.width(var5);
            this.stack[var2] = var5;
            if (var6 == 2) {
               Assert.checkNull(this.stack[var2 + 1]);
            }
         }

         return this;
      }

      Type error() {
         throw new AssertionError("inconsistent stack types at join point");
      }

      void dump() {
         this.dump(-1);
      }

      void dump(int var1) {
         System.err.print("stackMap for " + Code.this.meth.owner + "." + Code.this.meth);
         if (var1 == -1) {
            System.out.println();
         } else {
            System.out.println(" at " + var1);
         }

         System.err.println(" stack (from bottom):");

         int var2;
         for(var2 = 0; var2 < this.stacksize; ++var2) {
            System.err.println("  " + var2 + ": " + this.stack[var2]);
         }

         var2 = 0;

         int var3;
         for(var3 = Code.this.max_locals - 1; var3 >= 0; --var3) {
            if (this.defined.isMember(var3)) {
               var2 = var3;
               break;
            }
         }

         if (var2 >= 0) {
            System.err.println(" locals:");
         }

         for(var3 = 0; var3 <= var2; ++var3) {
            System.err.print("  " + var3 + ": ");
            if (this.defined.isMember(var3)) {
               LocalVar var4 = Code.this.lvar[var3];
               if (var4 == null) {
                  System.err.println("(none)");
               } else if (var4.sym == null) {
                  System.err.println("UNKNOWN!");
               } else {
                  System.err.println("" + var4.sym + " of type " + var4.sym.erasure(Code.this.types));
               }
            } else {
               System.err.println("undefined");
            }
         }

         if (this.nlocks != 0) {
            System.err.print(" locks:");

            for(var3 = 0; var3 < this.nlocks; ++var3) {
               System.err.print(" " + this.locks[var3]);
            }

            System.err.println();
         }

      }
   }

   public static class Chain {
      public final int pc;
      State state;
      public final Chain next;

      public Chain(int var1, Chain var2, State var3) {
         this.pc = var1;
         this.next = var2;
         this.state = var3;
      }
   }

   static class StackMapFrame {
      int pc;
      Type[] locals;
      Type[] stack;
   }

   public static enum StackMapFormat {
      NONE,
      CLDC {
         Name getAttributeName(Names var1) {
            return var1.StackMap;
         }
      },
      JSR202 {
         Name getAttributeName(Names var1) {
            return var1.StackMapTable;
         }
      };

      private StackMapFormat() {
      }

      Name getAttributeName(Names var1) {
         return var1.empty;
      }

      // $FF: synthetic method
      StackMapFormat(Object var3) {
         this();
      }
   }
}
