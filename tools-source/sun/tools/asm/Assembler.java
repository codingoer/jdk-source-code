package sun.tools.asm;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;
import sun.tools.javac.SourceClass;

public final class Assembler implements Constants {
   static final int NOTREACHED = 0;
   static final int REACHED = 1;
   static final int NEEDED = 2;
   Label first = new Label();
   Instruction last;
   int maxdepth;
   int maxvar;
   int maxpc;
   static Vector SourceClassList = new Vector();
   static Vector TmpCovTable = new Vector();
   static int[] JcovClassCountArray = new int[9];
   static String JcovMagicLine = "JCOV-DATA-FILE-VERSION: 2.0";
   static String JcovClassLine = "CLASS: ";
   static String JcovSrcfileLine = "SRCFILE: ";
   static String JcovTimestampLine = "TIMESTAMP: ";
   static String JcovDataLine = "DATA: ";
   static String JcovHeadingLine = "#kind\tcount";
   static int[] arrayModifiers = new int[]{1, 2, 4, 1024, 16, 512};
   static int[] arrayModifiersOpc = new int[]{121, 120, 122, 130, 128, 114};

   public Assembler() {
      this.last = this.first;
   }

   public void add(Instruction var1) {
      if (var1 != null) {
         this.last.next = var1;
         this.last = var1;
      }

   }

   public void add(long var1, int var3) {
      this.add(new Instruction(var1, var3, (Object)null));
   }

   public void add(long var1, int var3, Object var4) {
      this.add(new Instruction(var1, var3, var4));
   }

   public void add(long var1, int var3, Object var4, boolean var5) {
      this.add(new Instruction(var1, var3, var4, var5));
   }

   public void add(boolean var1, long var2, int var4, Object var5) {
      this.add(new Instruction(var1, var2, var4, var5));
   }

   public void add(long var1, int var3, boolean var4) {
      this.add(new Instruction(var1, var3, var4));
   }

   void optimize(Environment var1, Label var2) {
      var2.pc = 1;

      label49:
      for(Instruction var3 = var2.next; var3 != null; var3 = var3.next) {
         switch (var3.pc) {
            case 0:
               var3.optimize(var1);
               var3.pc = 1;
               break;
            case 1:
               return;
            case 2:
         }

         Enumeration var5;
         switch (var3.opc) {
            case -3:
               TryData var7 = (TryData)var3.value;
               var7.getEndLabel().pc = 2;
               var5 = var7.catches.elements();

               while(true) {
                  if (!var5.hasMoreElements()) {
                     continue label49;
                  }

                  CatchData var6 = (CatchData)var5.nextElement();
                  this.optimize(var1, var6.getLabel());
               }
            case -2:
            case -1:
               if (var3.pc == 1) {
                  var3.pc = 0;
               }
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
            case 198:
            case 199:
               this.optimize(var1, (Label)var3.value);
               break;
            case 167:
               this.optimize(var1, (Label)var3.value);
               return;
            case 168:
               this.optimize(var1, (Label)var3.value);
               break;
            case 169:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 191:
               return;
            case 170:
            case 171:
               SwitchData var4 = (SwitchData)var3.value;
               this.optimize(var1, var4.defaultLabel);
               var5 = var4.tab.elements();

               while(var5.hasMoreElements()) {
                  this.optimize(var1, (Label)var5.nextElement());
               }

               return;
         }
      }

   }

   boolean eliminate() {
      boolean var1 = false;
      Object var2 = this.first;

      for(Instruction var3 = this.first.next; var3 != null; var3 = var3.next) {
         if (var3.pc != 0) {
            ((Instruction)var2).next = var3;
            var2 = var3;
            var3.pc = 0;
         } else {
            var1 = true;
         }
      }

      this.first.pc = 0;
      ((Instruction)var2).next = null;
      return var1;
   }

   public void optimize(Environment var1) {
      do {
         this.optimize(var1, this.first);
      } while(this.eliminate() && var1.opt());

   }

   public void collect(Environment var1, MemberDefinition var2, ConstantPool var3) {
      if (var2 != null && var1.debug_vars()) {
         Vector var4 = var2.getArguments();
         if (var4 != null) {
            Enumeration var5 = var4.elements();

            while(var5.hasMoreElements()) {
               MemberDefinition var6 = (MemberDefinition)var5.nextElement();
               var3.put(var6.getName().toString());
               var3.put(var6.getType().getTypeSignature());
            }
         }
      }

      for(Object var7 = this.first; var7 != null; var7 = ((Instruction)var7).next) {
         ((Instruction)var7).collect(var3);
      }

   }

   void balance(Label var1, int var2) {
      label75:
      for(Object var3 = var1; var3 != null; var3 = ((Instruction)var3).next) {
         var2 += ((Instruction)var3).balance();
         if (var2 < 0) {
            throw new CompilerError("stack under flow: " + ((Instruction)var3).toString() + " = " + var2);
         }

         if (var2 > this.maxdepth) {
            this.maxdepth = var2;
         }

         Enumeration var5;
         int var7;
         switch (((Instruction)var3).opc) {
            case -3:
               TryData var8 = (TryData)((Instruction)var3).value;
               var5 = var8.catches.elements();

               while(true) {
                  if (!var5.hasMoreElements()) {
                     continue label75;
                  }

                  CatchData var6 = (CatchData)var5.nextElement();
                  this.balance(var6.getLabel(), var2 + 1);
               }
            case -1:
               var1 = (Label)var3;
               if (((Instruction)var3).pc == 1) {
                  if (var1.depth != var2) {
                     throw new CompilerError("stack depth error " + var2 + "/" + var1.depth + ": " + ((Instruction)var3).toString());
                  }

                  return;
               }

               var1.pc = 1;
               var1.depth = var2;
               break;
            case 21:
            case 23:
            case 25:
            case 54:
            case 56:
            case 58:
               var7 = (((Instruction)var3).value instanceof Number ? ((Number)((Instruction)var3).value).intValue() : ((LocalVariable)((Instruction)var3).value).slot) + 1;
               if (var7 > this.maxvar) {
                  this.maxvar = var7;
               }
               break;
            case 22:
            case 24:
            case 55:
            case 57:
               var7 = (((Instruction)var3).value instanceof Number ? ((Number)((Instruction)var3).value).intValue() : ((LocalVariable)((Instruction)var3).value).slot) + 2;
               if (var7 > this.maxvar) {
                  this.maxvar = var7;
               }
               break;
            case 132:
               var7 = ((int[])((int[])((Instruction)var3).value))[0] + 1;
               if (var7 > this.maxvar) {
                  this.maxvar = var7 + 1;
               }
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
            case 198:
            case 199:
               this.balance((Label)((Instruction)var3).value, var2);
               break;
            case 167:
               this.balance((Label)((Instruction)var3).value, var2);
               return;
            case 168:
               this.balance((Label)((Instruction)var3).value, var2 + 1);
               break;
            case 169:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 191:
               return;
            case 170:
            case 171:
               SwitchData var4 = (SwitchData)((Instruction)var3).value;
               this.balance(var4.defaultLabel, var2);
               var5 = var4.tab.elements();

               while(var5.hasMoreElements()) {
                  this.balance((Label)var5.nextElement(), var2);
               }

               return;
         }
      }

   }

   public void write(Environment var1, DataOutputStream var2, MemberDefinition var3, ConstantPool var4) throws IOException {
      int var5;
      if (var3 != null && var3.getArguments() != null) {
         var5 = 0;
         Vector var6 = var3.getArguments();

         MemberDefinition var8;
         for(Enumeration var7 = var6.elements(); var7.hasMoreElements(); var5 += var8.getType().stackSize()) {
            var8 = (MemberDefinition)var7.nextElement();
         }

         this.maxvar = var5;
      }

      try {
         this.balance(this.first, 0);
      } catch (CompilerError var9) {
         System.out.println("ERROR: " + var9);
         this.listing(System.out);
         throw var9;
      }

      var5 = 0;
      int var10 = 0;

      for(Object var11 = this.first; var11 != null; var11 = ((Instruction)var11).next) {
         ((Instruction)var11).pc = var5;
         int var13 = ((Instruction)var11).size(var4);
         if (var5 < 65536 && var5 + var13 >= 65536) {
            var1.error(((Instruction)var11).where, "warn.method.too.long");
         }

         var5 += var13;
         if (((Instruction)var11).opc == -3) {
            var10 += ((TryData)((Instruction)var11).value).catches.size();
         }
      }

      var2.writeShort(this.maxdepth);
      var2.writeShort(this.maxvar);
      var2.writeInt(this.maxpc = var5);

      for(Instruction var12 = this.first.next; var12 != null; var12 = var12.next) {
         var12.write(var2, var4);
      }

      var2.writeShort(var10);
      if (var10 > 0) {
         this.writeExceptions(var1, var2, var4, this.first, this.last);
      }

   }

   void writeExceptions(Environment var1, DataOutputStream var2, ConstantPool var3, Instruction var4, Instruction var5) throws IOException {
      for(Object var6 = var4; var6 != var5.next; var6 = ((Instruction)var6).next) {
         if (((Instruction)var6).opc == -3) {
            TryData var7 = (TryData)((Instruction)var6).value;
            this.writeExceptions(var1, var2, var3, ((Instruction)var6).next, var7.getEndLabel());
            Enumeration var8 = var7.catches.elements();

            while(var8.hasMoreElements()) {
               CatchData var9 = (CatchData)var8.nextElement();
               var2.writeShort(((Instruction)var6).pc);
               var2.writeShort(var7.getEndLabel().pc);
               var2.writeShort(var9.getLabel().pc);
               if (var9.getType() != null) {
                  var2.writeShort(var3.index(var9.getType()));
               } else {
                  var2.writeShort(0);
               }
            }

            var6 = var7.getEndLabel();
         }
      }

   }

   public void writeCoverageTable(Environment var1, ClassDefinition var2, DataOutputStream var3, ConstantPool var4, long var5) throws IOException {
      Vector var7 = new Vector();
      boolean var8 = false;
      boolean var9 = false;
      long var10 = ((SourceClass)var2).getWhere();
      Vector var12 = new Vector();
      boolean var13 = false;
      int var14 = 0;

      long var16;
      for(Object var15 = this.first; var15 != null; var15 = ((Instruction)var15).next) {
         var16 = ((Instruction)var15).where >> 32;
         Enumeration var19;
         if (var16 > 0L && ((Instruction)var15).opc != -1) {
            if (!var9) {
               if (var10 == ((Instruction)var15).where) {
                  var7.addElement(new Cover(2, var5, ((Instruction)var15).pc));
               } else {
                  var7.addElement(new Cover(1, var5, ((Instruction)var15).pc));
               }

               ++var14;
               var9 = true;
            }

            if (!var8 && !((Instruction)var15).flagNoCovered) {
               boolean var18 = false;
               var19 = var12.elements();

               while(var19.hasMoreElements()) {
                  if ((Long)var19.nextElement() == ((Instruction)var15).where) {
                     var18 = true;
                     break;
                  }
               }

               if (!var18) {
                  var7.addElement(new Cover(3, ((Instruction)var15).where, ((Instruction)var15).pc));
                  ++var14;
                  var8 = true;
               }
            }
         }

         SwitchData var22;
         switch (((Instruction)var15).opc) {
            case -3:
               var12.addElement(((Instruction)var15).where);
               var8 = false;
               break;
            case -1:
               var8 = false;
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
            case 198:
            case 199:
               if (((Instruction)var15).flagCondInverted) {
                  var7.addElement(new Cover(7, ((Instruction)var15).where, ((Instruction)var15).pc));
                  var7.addElement(new Cover(8, ((Instruction)var15).where, ((Instruction)var15).pc));
               } else {
                  var7.addElement(new Cover(8, ((Instruction)var15).where, ((Instruction)var15).pc));
                  var7.addElement(new Cover(7, ((Instruction)var15).where, ((Instruction)var15).pc));
               }

               var14 += 2;
               var8 = false;
               break;
            case 167:
               var8 = false;
            case 169:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 191:
            default:
               break;
            case 170:
               var22 = (SwitchData)((Instruction)var15).value;

               for(int var24 = var22.minValue; var24 <= var22.maxValue; ++var24) {
                  var7.addElement(new Cover(5, var22.whereCase(new Integer(var24)), ((Instruction)var15).pc));
                  ++var14;
               }

               if (!var22.getDefault()) {
                  var7.addElement(new Cover(6, ((Instruction)var15).where, ((Instruction)var15).pc));
                  ++var14;
               } else {
                  var7.addElement(new Cover(5, var22.whereCase("default"), ((Instruction)var15).pc));
                  ++var14;
               }

               var8 = false;
               break;
            case 171:
               var22 = (SwitchData)((Instruction)var15).value;

               for(var19 = var22.sortedKeys(); var19.hasMoreElements(); ++var14) {
                  Integer var20 = (Integer)var19.nextElement();
                  var7.addElement(new Cover(5, var22.whereCase(var20), ((Instruction)var15).pc));
               }

               if (!var22.getDefault()) {
                  var7.addElement(new Cover(6, ((Instruction)var15).where, ((Instruction)var15).pc));
                  ++var14;
               } else {
                  var7.addElement(new Cover(5, var22.whereCase("default"), ((Instruction)var15).pc));
                  ++var14;
               }

               var8 = false;
         }
      }

      var3.writeShort(var14);

      for(int var25 = 0; var25 < var14; ++var25) {
         Cover var21 = (Cover)var7.elementAt(var25);
         var16 = var21.Addr >> 32;
         long var23 = var21.Addr << 32 >> 32;
         var3.writeShort(var21.NumCommand);
         var3.writeShort(var21.Type);
         var3.writeInt((int)var16);
         var3.writeInt((int)var23);
         if (var21.Type != 5 || var21.Addr != 0L) {
            int var10002 = JcovClassCountArray[var21.Type]++;
         }
      }

   }

   public void addNativeToJcovTab(Environment var1, ClassDefinition var2) {
      int var10002 = JcovClassCountArray[1]++;
   }

   private String createClassJcovElement(Environment var1, ClassDefinition var2) {
      String var3 = Type.mangleInnerType(var2.getClassDeclaration().getName()).toString();
      SourceClassList.addElement(var3);
      String var4 = var3.replace('.', '/');
      String var5 = JcovClassLine + var4;
      var5 = var5 + " [";
      String var6 = "";

      for(int var7 = 0; var7 < arrayModifiers.length; ++var7) {
         if ((var2.getModifiers() & arrayModifiers[var7]) != 0) {
            var5 = var5 + var6 + opNames[arrayModifiersOpc[var7]];
            var6 = " ";
         }
      }

      var5 = var5 + "]";
      return var5;
   }

   public void GenVecJCov(Environment var1, ClassDefinition var2, long var3) {
      String var5 = ((SourceClass)var2).getAbsoluteName();
      TmpCovTable.addElement(this.createClassJcovElement(var1, var2));
      TmpCovTable.addElement(JcovSrcfileLine + var5);
      TmpCovTable.addElement(JcovTimestampLine + var3);
      TmpCovTable.addElement(JcovDataLine + "A");
      TmpCovTable.addElement(JcovHeadingLine);

      for(int var6 = 1; var6 <= 8; ++var6) {
         if (JcovClassCountArray[var6] != 0) {
            TmpCovTable.addElement(new String(var6 + "\t" + JcovClassCountArray[var6]));
            JcovClassCountArray[var6] = 0;
         }
      }

   }

   public void GenJCov(Environment var1) {
      try {
         File var2 = var1.getcovFile();
         if (var2.exists()) {
            DataInputStream var3 = new DataInputStream(new BufferedInputStream(new FileInputStream(var2)));
            String var4 = null;
            boolean var5 = true;
            var4 = var3.readLine();
            if (var4 != null && var4.startsWith(JcovMagicLine)) {
               while((var4 = var3.readLine()) != null) {
                  if (var4.startsWith(JcovClassLine)) {
                     var5 = true;
                     Enumeration var7 = SourceClassList.elements();

                     while(var7.hasMoreElements()) {
                        String var8 = var4.substring(JcovClassLine.length());
                        int var9 = var8.indexOf(32);
                        if (var9 != -1) {
                           var8 = var8.substring(0, var9);
                        }

                        String var6 = (String)var7.nextElement();
                        if (var6.compareTo(var8) == 0) {
                           var5 = false;
                           break;
                        }
                     }
                  }

                  if (var5) {
                     TmpCovTable.addElement(var4);
                  }
               }
            }

            var3.close();
         }

         PrintStream var12 = new PrintStream(new DataOutputStream(new FileOutputStream(var2)));
         var12.println(JcovMagicLine);
         Enumeration var13 = TmpCovTable.elements();

         while(var13.hasMoreElements()) {
            var12.println((String)var13.nextElement());
         }

         var12.close();
      } catch (FileNotFoundException var10) {
         System.out.println("ERROR: " + var10);
      } catch (IOException var11) {
         System.out.println("ERROR: " + var11);
      }

   }

   public void writeLineNumberTable(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      long var4 = -1L;
      int var6 = 0;

      Object var7;
      long var8;
      for(var7 = this.first; var7 != null; var7 = ((Instruction)var7).next) {
         var8 = ((Instruction)var7).where >> 32;
         if (var8 > 0L && var4 != var8) {
            var4 = var8;
            ++var6;
         }
      }

      var4 = -1L;
      var2.writeShort(var6);

      for(var7 = this.first; var7 != null; var7 = ((Instruction)var7).next) {
         var8 = ((Instruction)var7).where >> 32;
         if (var8 > 0L && var4 != var8) {
            var4 = var8;
            var2.writeShort(((Instruction)var7).pc);
            var2.writeShort((int)var8);
         }
      }

   }

   void flowFields(Environment var1, Label var2, MemberDefinition[] var3) {
      MemberDefinition[] var4;
      if (var2.locals != null) {
         var4 = var2.locals;

         for(int var9 = 0; var9 < this.maxvar; ++var9) {
            if (var4[var9] != var3[var9]) {
               var4[var9] = null;
            }
         }

      } else {
         var2.locals = new MemberDefinition[this.maxvar];
         System.arraycopy(var3, 0, var2.locals, 0, this.maxvar);
         var4 = new MemberDefinition[this.maxvar];
         System.arraycopy(var3, 0, var4, 0, this.maxvar);
         var3 = var4;

         for(Instruction var5 = var2.next; var5 != null; var5 = var5.next) {
            Enumeration var7;
            switch (var5.opc) {
               case -3:
                  Vector var11 = ((TryData)var5.value).catches;
                  var7 = var11.elements();

                  while(var7.hasMoreElements()) {
                     CatchData var8 = (CatchData)var7.nextElement();
                     this.flowFields(var1, var8.getLabel(), var3);
                  }
               case -2:
               case 0:
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
               case 148:
               case 149:
               case 150:
               case 151:
               case 152:
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
               case 192:
               case 193:
               case 194:
               case 195:
               case 196:
               case 197:
               default:
                  break;
               case -1:
                  this.flowFields(var1, (Label)var5, var3);
                  return;
               case 54:
               case 55:
               case 56:
               case 57:
               case 58:
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
                  if (var5.value instanceof LocalVariable) {
                     LocalVariable var10 = (LocalVariable)var5.value;
                     var3[var10.slot] = var10.field;
                  }
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
               case 168:
               case 198:
               case 199:
                  this.flowFields(var1, (Label)var5.value, var3);
                  break;
               case 167:
                  this.flowFields(var1, (Label)var5.value, var3);
                  return;
               case 169:
               case 172:
               case 173:
               case 174:
               case 175:
               case 176:
               case 177:
               case 191:
                  return;
               case 170:
               case 171:
                  SwitchData var6 = (SwitchData)var5.value;
                  this.flowFields(var1, var6.defaultLabel, var3);
                  var7 = var6.tab.elements();

                  while(var7.hasMoreElements()) {
                     this.flowFields(var1, (Label)var7.nextElement(), var3);
                  }

                  return;
            }
         }

      }
   }

   public void writeLocalVariableTable(Environment var1, MemberDefinition var2, DataOutputStream var3, ConstantPool var4) throws IOException {
      MemberDefinition[] var5 = new MemberDefinition[this.maxvar];
      boolean var6 = false;
      if (var2 != null && var2.getArguments() != null) {
         int var7 = 0;
         Vector var8 = var2.getArguments();

         MemberDefinition var10;
         for(Enumeration var9 = var8.elements(); var9.hasMoreElements(); var7 += var10.getType().stackSize()) {
            var10 = (MemberDefinition)var9.nextElement();
            var5[var7] = var10;
         }
      }

      this.flowFields(var1, this.first, var5);
      LocalVariableTable var13 = new LocalVariableTable();

      int var12;
      for(var12 = 0; var12 < this.maxvar; ++var12) {
         var5[var12] = null;
      }

      if (var2 != null && var2.getArguments() != null) {
         int var14 = 0;
         Vector var16 = var2.getArguments();

         MemberDefinition var11;
         for(Enumeration var18 = var16.elements(); var18.hasMoreElements(); var14 += var11.getType().stackSize()) {
            var11 = (MemberDefinition)var18.nextElement();
            var5[var14] = var11;
            var13.define(var11, var14, 0, this.maxpc);
         }
      }

      int[] var15 = new int[this.maxvar];

      for(Object var17 = this.first; var17 != null; var17 = ((Instruction)var17).next) {
         switch (((Instruction)var17).opc) {
            case -1:
               var12 = 0;

               for(; var12 < this.maxvar; ++var12) {
                  if (var5[var12] != null) {
                     var13.define(var5[var12], var12, var15[var12], ((Instruction)var17).pc);
                  }
               }

               int var20 = ((Instruction)var17).pc;
               MemberDefinition[] var22 = ((Label)var17).locals;
               if (var22 == null) {
                  for(var12 = 0; var12 < this.maxvar; ++var12) {
                     var5[var12] = null;
                  }
               } else {
                  System.arraycopy(var22, 0, var5, 0, this.maxvar);
               }

               for(var12 = 0; var12 < this.maxvar; ++var12) {
                  var15[var12] = var20;
               }
            case 0:
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
            default:
               break;
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
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
               if (((Instruction)var17).value instanceof LocalVariable) {
                  LocalVariable var19 = (LocalVariable)((Instruction)var17).value;
                  int var21 = ((Instruction)var17).next != null ? ((Instruction)var17).next.pc : ((Instruction)var17).pc;
                  if (var5[var19.slot] != null) {
                     var13.define(var5[var19.slot], var19.slot, var15[var19.slot], var21);
                  }

                  var15[var19.slot] = var21;
                  var5[var19.slot] = var19.field;
               }
         }
      }

      for(var12 = 0; var12 < this.maxvar; ++var12) {
         if (var5[var12] != null) {
            var13.define(var5[var12], var12, var15[var12], this.maxpc);
         }
      }

      var13.write(var1, var3, var4);
   }

   public boolean empty() {
      return this.first == this.last;
   }

   public void listing(PrintStream var1) {
      var1.println("-- listing --");

      for(Object var2 = this.first; var2 != null; var2 = ((Instruction)var2).next) {
         var1.println(((Instruction)var2).toString());
      }

   }
}
