package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.CompilerError;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;

public class Instruction implements Constants {
   long where;
   int pc;
   int opc;
   Object value;
   Instruction next;
   boolean flagCondInverted;
   boolean flagNoCovered = false;
   public static final double SWITCHRATIO;

   public Instruction(long var1, int var3, Object var4, boolean var5) {
      this.where = var1;
      this.opc = var3;
      this.value = var4;
      this.flagCondInverted = var5;
   }

   public Instruction(boolean var1, long var2, int var4, Object var5) {
      this.where = var2;
      this.opc = var4;
      this.value = var5;
      this.flagNoCovered = var1;
   }

   public Instruction(long var1, int var3, boolean var4) {
      this.where = var1;
      this.opc = var3;
      this.flagNoCovered = var4;
   }

   public Instruction(long var1, int var3, Object var4) {
      this.where = var1;
      this.opc = var3;
      this.value = var4;
   }

   public int getOpcode() {
      return this.pc;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object var1) {
      this.value = var1;
   }

   void optimize(Environment var1) {
      switch (this.opc) {
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
            if (this.value instanceof LocalVariable && !var1.debug_vars()) {
               this.value = new Integer(((LocalVariable)this.value).slot);
            }
            break;
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 198:
         case 199:
            this.value = ((Label)this.value).getDestination();
            if (this.value == this.next) {
               this.opc = 87;
            } else if (this.next.opc == 167 && this.value == this.next.next) {
               switch (this.opc) {
                  case 153:
                     this.opc = 154;
                     break;
                  case 154:
                     this.opc = 153;
                     break;
                  case 155:
                     this.opc = 156;
                     break;
                  case 156:
                     this.opc = 155;
                     break;
                  case 157:
                     this.opc = 158;
                     break;
                  case 158:
                     this.opc = 157;
                     break;
                  case 198:
                     this.opc = 199;
                     break;
                  case 199:
                     this.opc = 198;
               }

               this.flagCondInverted = !this.flagCondInverted;
               this.value = this.next.value;
               this.next.opc = -2;
            }
            break;
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
            this.value = ((Label)this.value).getDestination();
            if (this.value == this.next) {
               this.opc = 88;
            } else if (this.next.opc == 167 && this.value == this.next.next) {
               switch (this.opc) {
                  case 159:
                     this.opc = 160;
                     break;
                  case 160:
                     this.opc = 159;
                     break;
                  case 161:
                     this.opc = 162;
                     break;
                  case 162:
                     this.opc = 161;
                     break;
                  case 163:
                     this.opc = 164;
                     break;
                  case 164:
                     this.opc = 163;
                     break;
                  case 165:
                     this.opc = 166;
                     break;
                  case 166:
                     this.opc = 165;
               }

               this.flagCondInverted = !this.flagCondInverted;
               this.value = this.next.value;
               this.next.opc = -2;
            }
            break;
         case 167:
            Label var11 = (Label)this.value;
            this.value = var11 = var11.getDestination();
            if (var11 == this.next) {
               this.opc = -2;
            } else if (var11.next != null && var1.opt()) {
               switch (var11.next.opc) {
                  case 172:
                  case 173:
                  case 174:
                  case 175:
                  case 176:
                  case 177:
                     this.opc = var11.next.opc;
                     this.value = var11.next.value;
               }
            }
            break;
         case 170:
         case 171:
            SwitchData var2 = (SwitchData)this.value;
            var2.defaultLabel = var2.defaultLabel.getDestination();
            Enumeration var3 = var2.tab.keys();

            while(var3.hasMoreElements()) {
               Integer var4 = (Integer)var3.nextElement();
               Label var5 = (Label)var2.tab.get(var4);
               var2.tab.put(var4, var5.getDestination());
            }

            long var12 = (long)var2.maxValue - (long)var2.minValue + 1L;
            long var13 = (long)var2.tab.size();
            long var7 = 4L + var12;
            long var9 = 3L + 2L * var13;
            if ((double)var7 <= (double)var9 * SWITCHRATIO) {
               this.opc = 170;
            } else {
               this.opc = 171;
            }
      }

   }

   void collect(ConstantPool var1) {
      switch (this.opc) {
         case -3:
            Enumeration var8 = ((TryData)this.value).catches.elements();

            while(var8.hasMoreElements()) {
               CatchData var3 = (CatchData)var8.nextElement();
               if (var3.getType() != null) {
                  var1.put(var3.getType());
               }
            }

            return;
         case 0:
            if (this.value != null && this.value instanceof ClassDeclaration) {
               var1.put(this.value);
            }

            return;
         case 18:
         case 19:
            if (this.value instanceof Integer) {
               int var6 = (Integer)this.value;
               if (var6 >= -1 && var6 <= 5) {
                  this.opc = 3 + var6;
                  return;
               }

               if (var6 >= -128 && var6 < 128) {
                  this.opc = 16;
                  return;
               }

               if (var6 >= -32768 && var6 < 32768) {
                  this.opc = 17;
                  return;
               }
            } else if (this.value instanceof Float) {
               float var7 = (Float)this.value;
               if (var7 == 0.0F) {
                  if (Float.floatToIntBits(var7) == 0) {
                     this.opc = 11;
                     return;
                  }
               } else {
                  if (var7 == 1.0F) {
                     this.opc = 12;
                     return;
                  }

                  if (var7 == 2.0F) {
                     this.opc = 13;
                     return;
                  }
               }
            }

            var1.put(this.value);
            return;
         case 20:
            if (this.value instanceof Long) {
               long var4 = (Long)this.value;
               if (var4 == 0L) {
                  this.opc = 9;
                  return;
               }

               if (var4 == 1L) {
                  this.opc = 10;
                  return;
               }
            } else if (this.value instanceof Double) {
               double var5 = (Double)this.value;
               if (var5 == 0.0) {
                  if (Double.doubleToLongBits(var5) == 0L) {
                     this.opc = 14;
                     return;
                  }
               } else if (var5 == 1.0) {
                  this.opc = 15;
                  return;
               }
            }

            var1.put(this.value);
            return;
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
            if (this.value instanceof LocalVariable) {
               MemberDefinition var2 = ((LocalVariable)this.value).field;
               var1.put(var2.getName().toString());
               var1.put(var2.getType().getTypeSignature());
            }

            return;
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 183:
         case 184:
         case 185:
         case 187:
         case 192:
         case 193:
            var1.put(this.value);
            return;
         case 189:
            var1.put(this.value);
            return;
         case 197:
            var1.put(((ArrayData)this.value).type);
            return;
         default:
      }
   }

   int balance() {
      switch (this.opc) {
         case -3:
         case -2:
         case -1:
         case 0:
         case 47:
         case 49:
         case 95:
         case 116:
         case 117:
         case 118:
         case 119:
         case 132:
         case 134:
         case 138:
         case 139:
         case 143:
         case 145:
         case 146:
         case 147:
         case 167:
         case 168:
         case 169:
         case 177:
         case 188:
         case 189:
         case 190:
         case 192:
         case 193:
         case 200:
         case 201:
            return 0;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 11:
         case 12:
         case 13:
         case 16:
         case 17:
         case 18:
         case 19:
         case 21:
         case 23:
         case 25:
         case 89:
         case 90:
         case 91:
         case 133:
         case 135:
         case 140:
         case 141:
         case 187:
            return 1;
         case 9:
         case 10:
         case 14:
         case 15:
         case 20:
         case 22:
         case 24:
         case 92:
         case 93:
         case 94:
            return 2;
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 186:
         case 196:
         default:
            throw new CompilerError("invalid opcode: " + this.toString());
         case 46:
         case 48:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 56:
         case 58:
         case 87:
         case 96:
         case 98:
         case 100:
         case 102:
         case 104:
         case 106:
         case 108:
         case 110:
         case 112:
         case 114:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 128:
         case 130:
         case 136:
         case 137:
         case 142:
         case 144:
         case 149:
         case 150:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 170:
         case 171:
         case 172:
         case 174:
         case 176:
         case 191:
         case 194:
         case 195:
         case 198:
         case 199:
            return -1;
         case 55:
         case 57:
         case 88:
         case 97:
         case 99:
         case 101:
         case 103:
         case 105:
         case 107:
         case 109:
         case 111:
         case 113:
         case 115:
         case 127:
         case 129:
         case 131:
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 173:
         case 175:
            return -2;
         case 79:
         case 81:
         case 83:
         case 84:
         case 85:
         case 86:
         case 148:
         case 151:
         case 152:
            return -3;
         case 80:
         case 82:
            return -4;
         case 178:
            return ((MemberDefinition)this.value).getType().stackSize();
         case 179:
            return -((MemberDefinition)this.value).getType().stackSize();
         case 180:
            return ((MemberDefinition)this.value).getType().stackSize() - 1;
         case 181:
            return -1 - ((MemberDefinition)this.value).getType().stackSize();
         case 182:
         case 183:
         case 185:
            return ((MemberDefinition)this.value).getType().getReturnType().stackSize() - (((MemberDefinition)this.value).getType().stackSize() + 1);
         case 184:
            return ((MemberDefinition)this.value).getType().getReturnType().stackSize() - ((MemberDefinition)this.value).getType().stackSize();
         case 197:
            return 1 - ((ArrayData)this.value).nargs;
      }
   }

   int size(ConstantPool var1) {
      SwitchData var2;
      int var3;
      int var4;
      switch (this.opc) {
         case -3:
         case -2:
         case -1:
            return 0;
         case 0:
            if (this.value != null && !(this.value instanceof Integer)) {
               return 2;
            }

            return 1;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 172:
         case 173:
         case 174:
         case 175:
         case 176:
         case 177:
         case 186:
         case 190:
         case 191:
         case 194:
         case 195:
         case 196:
         default:
            return 1;
         case 16:
         case 188:
            return 2;
         case 17:
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
         case 167:
         case 168:
         case 198:
         case 199:
            return 3;
         case 18:
         case 19:
            if (var1.index(this.value) < 256) {
               this.opc = 18;
               return 2;
            }

            this.opc = 19;
            return 3;
         case 20:
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 183:
         case 184:
         case 187:
         case 189:
         case 192:
         case 193:
            return 3;
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
            var4 = ((Number)this.value).intValue();
            if (var4 < 4) {
               if (var4 < 0) {
                  throw new CompilerError("invalid slot: " + this.toString() + "\nThis error possibly resulted from poorly constructed class paths.");
               }

               this.opc = 26 + (this.opc - 21) * 4 + var4;
               return 1;
            } else {
               if (var4 <= 255) {
                  return 2;
               }

               this.opc += 256;
               return 4;
            }
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
            var4 = this.value instanceof Number ? ((Number)this.value).intValue() : ((LocalVariable)this.value).slot;
            if (var4 < 4) {
               if (var4 < 0) {
                  throw new CompilerError("invalid slot: " + this.toString());
               }

               this.opc = 59 + (this.opc - 54) * 4 + var4;
               return 1;
            } else {
               if (var4 <= 255) {
                  return 2;
               }

               this.opc += 256;
               return 4;
            }
         case 132:
            var4 = ((int[])((int[])this.value))[0];
            var3 = ((int[])((int[])this.value))[1];
            if (var4 < 0) {
               throw new CompilerError("invalid slot: " + this.toString());
            } else {
               if (var4 <= 255 && (byte)var3 == var3) {
                  return 3;
               }

               this.opc += 256;
               return 6;
            }
         case 169:
            var4 = ((Number)this.value).intValue();
            if (var4 <= 255) {
               if (var4 < 0) {
                  throw new CompilerError("invalid slot: " + this.toString());
               }

               return 2;
            }

            this.opc += 256;
            return 4;
         case 170:
            var2 = (SwitchData)this.value;

            for(var3 = 1; (this.pc + var3) % 4 != 0; ++var3) {
            }

            return var3 + 16 + (var2.maxValue - var2.minValue) * 4;
         case 171:
            var2 = (SwitchData)this.value;

            for(var3 = 1; (this.pc + var3) % 4 != 0; ++var3) {
            }

            return var3 + 8 + var2.tab.size() * 8;
         case 185:
         case 200:
         case 201:
            return 5;
         case 197:
            return 4;
      }
   }

   void write(DataOutputStream var1, ConstantPool var2) throws IOException {
      SwitchData var3;
      int var4;
      switch (this.opc) {
         case -3:
         case -2:
         case -1:
            break;
         case 0:
            if (this.value != null) {
               if (this.value instanceof Integer) {
                  var1.writeByte((Integer)this.value);
               } else {
                  var1.writeShort(var2.index(this.value));
               }

               return;
            }
         default:
            var1.writeByte(this.opc);
            break;
         case 16:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 169:
         case 188:
            var1.writeByte(this.opc);
            var1.writeByte(((Number)this.value).intValue());
            break;
         case 17:
            var1.writeByte(this.opc);
            var1.writeShort(((Number)this.value).intValue());
            break;
         case 18:
            var1.writeByte(this.opc);
            var1.writeByte(var2.index(this.value));
            break;
         case 19:
         case 20:
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 183:
         case 184:
         case 187:
         case 192:
         case 193:
            var1.writeByte(this.opc);
            var1.writeShort(var2.index(this.value));
            break;
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
            var1.writeByte(this.opc);
            var1.writeByte(this.value instanceof Number ? ((Number)this.value).intValue() : ((LocalVariable)this.value).slot);
            break;
         case 132:
            var1.writeByte(this.opc);
            var1.writeByte(((int[])((int[])this.value))[0]);
            var1.writeByte(((int[])((int[])this.value))[1]);
            break;
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
         case 167:
         case 168:
         case 198:
         case 199:
            var1.writeByte(this.opc);
            var1.writeShort(((Instruction)this.value).pc - this.pc);
            break;
         case 170:
            var3 = (SwitchData)this.value;
            var1.writeByte(this.opc);

            for(var4 = 1; (this.pc + var4) % 4 != 0; ++var4) {
               var1.writeByte(0);
            }

            var1.writeInt(var3.defaultLabel.pc - this.pc);
            var1.writeInt(var3.minValue);
            var1.writeInt(var3.maxValue);

            for(var4 = var3.minValue; var4 <= var3.maxValue; ++var4) {
               Label var7 = var3.get(var4);
               int var8 = var7 != null ? var7.pc : var3.defaultLabel.pc;
               var1.writeInt(var8 - this.pc);
            }

            return;
         case 171:
            var3 = (SwitchData)this.value;
            var1.writeByte(this.opc);

            for(var4 = this.pc + 1; var4 % 4 != 0; ++var4) {
               var1.writeByte(0);
            }

            var1.writeInt(var3.defaultLabel.pc - this.pc);
            var1.writeInt(var3.tab.size());
            Enumeration var5 = var3.sortedKeys();

            while(var5.hasMoreElements()) {
               Integer var6 = (Integer)var5.nextElement();
               var1.writeInt(var6);
               var1.writeInt(var3.get(var6).pc - this.pc);
            }

            return;
         case 185:
            var1.writeByte(this.opc);
            var1.writeShort(var2.index(this.value));
            var1.writeByte(((MemberDefinition)this.value).getType().stackSize() + 1);
            var1.writeByte(0);
            break;
         case 189:
            var1.writeByte(this.opc);
            var1.writeShort(var2.index(this.value));
            break;
         case 197:
            var1.writeByte(this.opc);
            var1.writeShort(var2.index(((ArrayData)this.value).type));
            var1.writeByte(((ArrayData)this.value).nargs);
            break;
         case 200:
         case 201:
            var1.writeByte(this.opc);
            var1.writeLong((long)(((Instruction)this.value).pc - this.pc));
            break;
         case 277:
         case 278:
         case 279:
         case 280:
         case 281:
         case 425:
            var1.writeByte(196);
            var1.writeByte(this.opc - 256);
            var1.writeShort(((Number)this.value).intValue());
            break;
         case 310:
         case 311:
         case 312:
         case 313:
         case 314:
            var1.writeByte(196);
            var1.writeByte(this.opc - 256);
            var1.writeShort(this.value instanceof Number ? ((Number)this.value).intValue() : ((LocalVariable)this.value).slot);
            break;
         case 388:
            var1.writeByte(196);
            var1.writeByte(this.opc - 256);
            var1.writeShort(((int[])((int[])this.value))[0]);
            var1.writeShort(((int[])((int[])this.value))[1]);
      }

   }

   public String toString() {
      String var1 = (this.where >> 32) + ":\t";
      switch (this.opc) {
         case -3:
            return var1 + "try " + ((TryData)this.value).getEndLabel().hashCode();
         case -2:
            return var1 + "dead";
         case 132:
            int var2 = ((int[])((int[])this.value))[0];
            int var3 = ((int[])((int[])this.value))[1];
            return var1 + opcNames[this.opc] + " " + var2 + ", " + var3;
         default:
            if (this.value != null) {
               if (this.value instanceof Label) {
                  return var1 + opcNames[this.opc] + " " + this.value.toString();
               } else if (this.value instanceof Instruction) {
                  return var1 + opcNames[this.opc] + " " + this.value.hashCode();
               } else {
                  return this.value instanceof String ? var1 + opcNames[this.opc] + " \"" + this.value + "\"" : var1 + opcNames[this.opc] + " " + this.value;
               }
            } else {
               return var1 + opcNames[this.opc];
            }
      }
   }

   static {
      double var0 = 1.5;
      String var2 = System.getProperty("javac.switchratio");
      if (var2 != null) {
         try {
            double var3 = Double.valueOf(var2);
            if (!Double.isNaN(var3) && !(var3 < 0.0)) {
               var0 = var3;
            }
         } catch (NumberFormatException var5) {
         }
      }

      SWITCHRATIO = var0;
   }
}
