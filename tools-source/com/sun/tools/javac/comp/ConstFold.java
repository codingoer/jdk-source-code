package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

class ConstFold {
   protected static final Context.Key constFoldKey = new Context.Key();
   private Symtab syms;
   static final Integer minusOne = -1;
   static final Integer zero = 0;
   static final Integer one = 1;

   public static strictfp ConstFold instance(Context var0) {
      ConstFold var1 = (ConstFold)var0.get(constFoldKey);
      if (var1 == null) {
         var1 = new ConstFold(var0);
      }

      return var1;
   }

   private strictfp ConstFold(Context var1) {
      var1.put((Context.Key)constFoldKey, (Object)this);
      this.syms = Symtab.instance(var1);
   }

   private static strictfp Integer b2i(boolean var0) {
      return var0 ? one : zero;
   }

   private static strictfp int intValue(Object var0) {
      return ((Number)var0).intValue();
   }

   private static strictfp long longValue(Object var0) {
      return ((Number)var0).longValue();
   }

   private static strictfp float floatValue(Object var0) {
      return ((Number)var0).floatValue();
   }

   private static strictfp double doubleValue(Object var0) {
      return ((Number)var0).doubleValue();
   }

   strictfp Type fold(int var1, List var2) {
      int var3 = var2.length();
      if (var3 == 1) {
         return this.fold1(var1, (Type)var2.head);
      } else if (var3 == 2) {
         return this.fold2(var1, (Type)var2.head, (Type)var2.tail.head);
      } else {
         throw new AssertionError();
      }
   }

   strictfp Type fold1(int var1, Type var2) {
      try {
         Object var3 = var2.constValue();
         switch (var1) {
            case 0:
               return var2;
            case 116:
               return this.syms.intType.constType(-intValue(var3));
            case 117:
               return this.syms.longType.constType(new Long(-longValue(var3)));
            case 118:
               return this.syms.floatType.constType(new Float(-floatValue(var3)));
            case 119:
               return this.syms.doubleType.constType(new Double(-doubleValue(var3)));
            case 130:
               return this.syms.intType.constType(~intValue(var3));
            case 131:
               return this.syms.longType.constType(new Long(~longValue(var3)));
            case 153:
               return this.syms.booleanType.constType(b2i(intValue(var3) == 0));
            case 154:
               return this.syms.booleanType.constType(b2i(intValue(var3) != 0));
            case 155:
               return this.syms.booleanType.constType(b2i(intValue(var3) < 0));
            case 156:
               return this.syms.booleanType.constType(b2i(intValue(var3) >= 0));
            case 157:
               return this.syms.booleanType.constType(b2i(intValue(var3) > 0));
            case 158:
               return this.syms.booleanType.constType(b2i(intValue(var3) <= 0));
            case 257:
               return this.syms.booleanType.constType(b2i(intValue(var3) == 0));
            default:
               return null;
         }
      } catch (ArithmeticException var4) {
         return null;
      }
   }

   strictfp Type fold2(int var1, Type var2, Type var3) {
      try {
         if (var1 > 511) {
            Type var7 = this.fold2(var1 >> 9, var2, var3);
            return var7.constValue() == null ? var7 : this.fold1(var1 & 511, var7);
         } else {
            Object var4 = var2.constValue();
            Object var5 = var3.constValue();
            switch (var1) {
               case 96:
                  return this.syms.intType.constType(intValue(var4) + intValue(var5));
               case 97:
                  return this.syms.longType.constType(new Long(longValue(var4) + longValue(var5)));
               case 98:
                  return this.syms.floatType.constType(new Float(floatValue(var4) + floatValue(var5)));
               case 99:
                  return this.syms.doubleType.constType(new Double(doubleValue(var4) + doubleValue(var5)));
               case 100:
                  return this.syms.intType.constType(intValue(var4) - intValue(var5));
               case 101:
                  return this.syms.longType.constType(new Long(longValue(var4) - longValue(var5)));
               case 102:
                  return this.syms.floatType.constType(new Float(floatValue(var4) - floatValue(var5)));
               case 103:
                  return this.syms.doubleType.constType(new Double(doubleValue(var4) - doubleValue(var5)));
               case 104:
                  return this.syms.intType.constType(intValue(var4) * intValue(var5));
               case 105:
                  return this.syms.longType.constType(new Long(longValue(var4) * longValue(var5)));
               case 106:
                  return this.syms.floatType.constType(new Float(floatValue(var4) * floatValue(var5)));
               case 107:
                  return this.syms.doubleType.constType(new Double(doubleValue(var4) * doubleValue(var5)));
               case 108:
                  return this.syms.intType.constType(intValue(var4) / intValue(var5));
               case 109:
                  return this.syms.longType.constType(new Long(longValue(var4) / longValue(var5)));
               case 110:
                  return this.syms.floatType.constType(new Float(floatValue(var4) / floatValue(var5)));
               case 111:
                  return this.syms.doubleType.constType(new Double(doubleValue(var4) / doubleValue(var5)));
               case 112:
                  return this.syms.intType.constType(intValue(var4) % intValue(var5));
               case 113:
                  return this.syms.longType.constType(new Long(longValue(var4) % longValue(var5)));
               case 114:
                  return this.syms.floatType.constType(new Float(floatValue(var4) % floatValue(var5)));
               case 115:
                  return this.syms.doubleType.constType(new Double(doubleValue(var4) % doubleValue(var5)));
               case 116:
               case 117:
               case 118:
               case 119:
               case 132:
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
               case 153:
               case 154:
               case 155:
               case 156:
               case 157:
               case 158:
               case 167:
               case 168:
               case 169:
               case 170:
               case 171:
               case 172:
               case 173:
               case 174:
               case 175:
               case 176:
               case 177:
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
               case 190:
               case 191:
               case 192:
               case 193:
               case 194:
               case 195:
               case 196:
               case 197:
               case 198:
               case 199:
               case 200:
               case 201:
               case 202:
               case 203:
               case 204:
               case 205:
               case 206:
               case 207:
               case 208:
               case 209:
               case 210:
               case 211:
               case 212:
               case 213:
               case 214:
               case 215:
               case 216:
               case 217:
               case 218:
               case 219:
               case 220:
               case 221:
               case 222:
               case 223:
               case 224:
               case 225:
               case 226:
               case 227:
               case 228:
               case 229:
               case 230:
               case 231:
               case 232:
               case 233:
               case 234:
               case 235:
               case 236:
               case 237:
               case 238:
               case 239:
               case 240:
               case 241:
               case 242:
               case 243:
               case 244:
               case 245:
               case 246:
               case 247:
               case 248:
               case 249:
               case 250:
               case 251:
               case 252:
               case 253:
               case 254:
               case 255:
               case 257:
               case 260:
               case 261:
               case 262:
               case 263:
               case 264:
               case 265:
               case 266:
               case 267:
               case 268:
               case 269:
               default:
                  return null;
               case 120:
               case 270:
                  return this.syms.intType.constType(intValue(var4) << intValue(var5));
               case 121:
               case 271:
                  return this.syms.longType.constType(new Long(longValue(var4) << intValue(var5)));
               case 122:
               case 272:
                  return this.syms.intType.constType(intValue(var4) >> intValue(var5));
               case 123:
               case 273:
                  return this.syms.longType.constType(new Long(longValue(var4) >> intValue(var5)));
               case 124:
               case 274:
                  return this.syms.intType.constType(intValue(var4) >>> intValue(var5));
               case 125:
                  return this.syms.longType.constType(new Long(longValue(var4) >>> intValue(var5)));
               case 126:
                  return (var2.hasTag(TypeTag.BOOLEAN) ? this.syms.booleanType : this.syms.intType).constType(intValue(var4) & intValue(var5));
               case 127:
                  return this.syms.longType.constType(new Long(longValue(var4) & longValue(var5)));
               case 128:
                  return (var2.hasTag(TypeTag.BOOLEAN) ? this.syms.booleanType : this.syms.intType).constType(intValue(var4) | intValue(var5));
               case 129:
                  return this.syms.longType.constType(new Long(longValue(var4) | longValue(var5)));
               case 130:
                  return (var2.hasTag(TypeTag.BOOLEAN) ? this.syms.booleanType : this.syms.intType).constType(intValue(var4) ^ intValue(var5));
               case 131:
                  return this.syms.longType.constType(new Long(longValue(var4) ^ longValue(var5)));
               case 148:
                  if (longValue(var4) < longValue(var5)) {
                     return this.syms.intType.constType(minusOne);
                  } else {
                     if (longValue(var4) > longValue(var5)) {
                        return this.syms.intType.constType(one);
                     }

                     return this.syms.intType.constType(zero);
                  }
               case 149:
               case 150:
                  if (floatValue(var4) < floatValue(var5)) {
                     return this.syms.intType.constType(minusOne);
                  } else if (floatValue(var4) > floatValue(var5)) {
                     return this.syms.intType.constType(one);
                  } else if (floatValue(var4) == floatValue(var5)) {
                     return this.syms.intType.constType(zero);
                  } else {
                     if (var1 == 150) {
                        return this.syms.intType.constType(one);
                     }

                     return this.syms.intType.constType(minusOne);
                  }
               case 151:
               case 152:
                  if (doubleValue(var4) < doubleValue(var5)) {
                     return this.syms.intType.constType(minusOne);
                  } else if (doubleValue(var4) > doubleValue(var5)) {
                     return this.syms.intType.constType(one);
                  } else if (doubleValue(var4) == doubleValue(var5)) {
                     return this.syms.intType.constType(zero);
                  } else {
                     if (var1 == 152) {
                        return this.syms.intType.constType(one);
                     }

                     return this.syms.intType.constType(minusOne);
                  }
               case 159:
                  return this.syms.booleanType.constType(b2i(intValue(var4) == intValue(var5)));
               case 160:
                  return this.syms.booleanType.constType(b2i(intValue(var4) != intValue(var5)));
               case 161:
                  return this.syms.booleanType.constType(b2i(intValue(var4) < intValue(var5)));
               case 162:
                  return this.syms.booleanType.constType(b2i(intValue(var4) >= intValue(var5)));
               case 163:
                  return this.syms.booleanType.constType(b2i(intValue(var4) > intValue(var5)));
               case 164:
                  return this.syms.booleanType.constType(b2i(intValue(var4) <= intValue(var5)));
               case 165:
                  return this.syms.booleanType.constType(b2i(var4.equals(var5)));
               case 166:
                  return this.syms.booleanType.constType(b2i(!var4.equals(var5)));
               case 256:
                  return this.syms.stringType.constType(var2.stringValue() + var3.stringValue());
               case 258:
                  return this.syms.booleanType.constType(b2i((intValue(var4) & intValue(var5)) != 0));
               case 259:
                  return this.syms.booleanType.constType(b2i((intValue(var4) | intValue(var5)) != 0));
            }
         }
      } catch (ArithmeticException var6) {
         return null;
      }
   }

   strictfp Type coerce(Type var1, Type var2) {
      if (var1.tsym.type == var2.tsym.type) {
         return var1;
      } else {
         if (var1.isNumeric()) {
            Object var3 = var1.constValue();
            switch (var2.getTag()) {
               case BYTE:
                  return this.syms.byteType.constType(0 + (byte)intValue(var3));
               case CHAR:
                  return this.syms.charType.constType(0 + (char)intValue(var3));
               case SHORT:
                  return this.syms.shortType.constType(0 + (short)intValue(var3));
               case INT:
                  return this.syms.intType.constType(intValue(var3));
               case LONG:
                  return this.syms.longType.constType(longValue(var3));
               case FLOAT:
                  return this.syms.floatType.constType(floatValue(var3));
               case DOUBLE:
                  return this.syms.doubleType.constType(doubleValue(var3));
            }
         }

         return var2;
      }
   }
}
