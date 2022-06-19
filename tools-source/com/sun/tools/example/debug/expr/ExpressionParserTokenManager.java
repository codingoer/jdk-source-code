package com.sun.tools.example.debug.expr;

import java.io.IOException;

public class ExpressionParserTokenManager implements ExpressionParserConstants {
   static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
   static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
   static final long[] jjbitVec3 = new long[]{2301339413881290750L, -16384L, 4294967295L, 432345564227567616L};
   static final long[] jjbitVec4 = new long[]{0L, 0L, 0L, -36028797027352577L};
   static final long[] jjbitVec5 = new long[]{0L, -1L, -1L, -1L};
   static final long[] jjbitVec6 = new long[]{-1L, -1L, 65535L, 0L};
   static final long[] jjbitVec7 = new long[]{-1L, -1L, 0L, 0L};
   static final long[] jjbitVec8 = new long[]{70368744177663L, 0L, 0L, 0L};
   static final int[] jjnextStates = new int[]{30, 31, 36, 37, 40, 41, 8, 49, 60, 61, 19, 20, 22, 10, 12, 45, 47, 2, 50, 51, 53, 4, 5, 8, 19, 20, 24, 22, 32, 33, 8, 40, 41, 8, 56, 57, 59, 63, 64, 66, 6, 7, 13, 14, 16, 21, 23, 25, 34, 35, 38, 39, 42, 43};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", null, null, null, null, null, null, null, null, null, null, null, "(", ")", "{", "}", "[", "]", ";", ",", ".", "=", ">", "<", "!", "~", "?", ":", "==", "<=", ">=", "!=", "||", "&&", "++", "--", "+", "-", "*", "/", "&", "|", "^", "%", "<<", ">>", ">>>", "+=", "-=", "*=", "/=", "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>="};
   public static final String[] lexStateNames = new String[]{"DEFAULT"};
   static final long[] jjtoToken = new long[]{-8070450532247929343L, 4503599627370446L};
   static final long[] jjtoSkip = new long[]{510L, 0L};
   static final long[] jjtoSpecial = new long[]{448L, 0L};
   private ASCII_UCodeESC_CharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   protected char curChar;
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   private final int jjStopStringLiteralDfa_0(int var1, long var2, long var4) {
      switch (var1) {
         case 0:
            if ((var4 & 16384L) != 0L) {
               return 4;
            } else if ((var2 & 576460752303422976L) != 0L) {
               this.jjmatchedKind = 67;
               return 28;
            } else {
               if ((var4 & 17600775979008L) != 0L) {
                  return 49;
               }

               return -1;
            }
         case 1:
            if ((var2 & 576460751226535424L) != 0L) {
               if (this.jjmatchedPos != 1) {
                  this.jjmatchedKind = 67;
                  this.jjmatchedPos = 1;
               }

               return 28;
            } else {
               if ((var2 & 1076887552L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 2:
            if ((var2 & 540431627523718656L) != 0L) {
               if (this.jjmatchedPos != 2) {
                  this.jjmatchedKind = 67;
                  this.jjmatchedPos = 2;
               }

               return 28;
            } else {
               if ((var2 & 36029123704913920L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 3:
            if ((var2 & 449233150412803584L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 3;
               return 28;
            } else {
               if ((var2 & 91198511470653440L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 4:
            if ((var2 & 154071452707718656L) != 0L) {
               if (this.jjmatchedPos != 4) {
                  this.jjmatchedKind = 67;
                  this.jjmatchedPos = 4;
               }

               return 28;
            } else {
               if ((var2 & 295161697705084928L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 5:
            if ((var2 & 153693079038854656L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 5;
               return 28;
            } else {
               if ((var2 & 4881973363343360L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 6:
            if ((var2 & 153689780427948544L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 6;
               return 28;
            } else {
               if ((var2 & 3298610906112L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 7:
            if ((var2 & 9574592351830016L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 7;
               return 28;
            } else {
               if ((var2 & 144115188076118528L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 8:
            if ((var2 & 562960690839552L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 8;
               return 28;
            } else {
               if ((var2 & 9011631660990464L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 9:
            if ((var2 & 562949953421312L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 9;
               return 28;
            } else {
               if ((var2 & 10737418240L) != 0L) {
                  return 28;
               }

               return -1;
            }
         case 10:
            if ((var2 & 562949953421312L) != 0L) {
               this.jjmatchedKind = 67;
               this.jjmatchedPos = 10;
               return 28;
            }

            return -1;
         default:
            return -1;
      }
   }

   private final int jjStartNfa_0(int var1, long var2, long var4) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(var1, var2, var4), var1 + 1);
   }

   private final int jjStopAtPos(int var1, int var2) {
      this.jjmatchedKind = var2;
      this.jjmatchedPos = var1;
      return var1 + 1;
   }

   private final int jjStartNfaWithStates_0(int var1, int var2, int var3) {
      this.jjmatchedKind = var2;
      this.jjmatchedPos = var1;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return var1 + 1;
      }

      return this.jjMoveNfa_0(var3, var1 + 1);
   }

   private final int jjMoveStringLiteralDfa0_0() {
      switch (this.curChar) {
         case '!':
            this.jjmatchedKind = 82;
            return this.jjMoveStringLiteralDfa1_0(0L, 33554432L);
         case '"':
         case '#':
         case '$':
         case '\'':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '\\':
         case '_':
         case '`':
         case 'h':
         case 'j':
         case 'k':
         case 'm':
         case 'o':
         case 'q':
         case 'u':
         case 'x':
         case 'y':
         case 'z':
         default:
            return this.jjMoveNfa_0(0, 0);
         case '%':
            this.jjmatchedKind = 101;
            return this.jjMoveStringLiteralDfa1_0(0L, 281474976710656L);
         case '&':
            this.jjmatchedKind = 98;
            return this.jjMoveStringLiteralDfa1_0(0L, 35184506306560L);
         case '(':
            return this.jjStopAtPos(0, 70);
         case ')':
            return this.jjStopAtPos(0, 71);
         case '*':
            this.jjmatchedKind = 96;
            return this.jjMoveStringLiteralDfa1_0(0L, 8796093022208L);
         case '+':
            this.jjmatchedKind = 94;
            return this.jjMoveStringLiteralDfa1_0(0L, 2199291691008L);
         case ',':
            return this.jjStopAtPos(0, 77);
         case '-':
            this.jjmatchedKind = 95;
            return this.jjMoveStringLiteralDfa1_0(0L, 4398583382016L);
         case '.':
            return this.jjStartNfaWithStates_0(0, 78, 4);
         case '/':
            this.jjmatchedKind = 97;
            return this.jjMoveStringLiteralDfa1_0(0L, 17592186044416L);
         case ':':
            return this.jjStopAtPos(0, 85);
         case ';':
            return this.jjStopAtPos(0, 76);
         case '<':
            this.jjmatchedKind = 81;
            return this.jjMoveStringLiteralDfa1_0(0L, 563224839716864L);
         case '=':
            this.jjmatchedKind = 79;
            return this.jjMoveStringLiteralDfa1_0(0L, 4194304L);
         case '>':
            this.jjmatchedKind = 80;
            return this.jjMoveStringLiteralDfa1_0(0L, 3379349004746752L);
         case '?':
            return this.jjStopAtPos(0, 84);
         case '[':
            return this.jjStopAtPos(0, 74);
         case ']':
            return this.jjStopAtPos(0, 75);
         case '^':
            this.jjmatchedKind = 100;
            return this.jjMoveStringLiteralDfa1_0(0L, 140737488355328L);
         case 'a':
            return this.jjMoveStringLiteralDfa1_0(512L, 0L);
         case 'b':
            return this.jjMoveStringLiteralDfa1_0(7168L, 0L);
         case 'c':
            return this.jjMoveStringLiteralDfa1_0(516096L, 0L);
         case 'd':
            return this.jjMoveStringLiteralDfa1_0(3670016L, 0L);
         case 'e':
            return this.jjMoveStringLiteralDfa1_0(12582912L, 0L);
         case 'f':
            return this.jjMoveStringLiteralDfa1_0(520093696L, 0L);
         case 'g':
            return this.jjMoveStringLiteralDfa1_0(536870912L, 0L);
         case 'i':
            return this.jjMoveStringLiteralDfa1_0(67645734912L, 0L);
         case 'l':
            return this.jjMoveStringLiteralDfa1_0(68719476736L, 0L);
         case 'n':
            return this.jjMoveStringLiteralDfa1_0(962072674304L, 0L);
         case 'p':
            return this.jjMoveStringLiteralDfa1_0(16492674416640L, 0L);
         case 'r':
            return this.jjMoveStringLiteralDfa1_0(17592186044416L, 0L);
         case 's':
            return this.jjMoveStringLiteralDfa1_0(1090715534753792L, 0L);
         case 't':
            return this.jjMoveStringLiteralDfa1_0(70931694131085312L, 0L);
         case 'v':
            return this.jjMoveStringLiteralDfa1_0(216172782113783808L, 0L);
         case 'w':
            return this.jjMoveStringLiteralDfa1_0(288230376151711744L, 0L);
         case '{':
            return this.jjStopAtPos(0, 72);
         case '|':
            this.jjmatchedKind = 99;
            return this.jjMoveStringLiteralDfa1_0(0L, 70368811286528L);
         case '}':
            return this.jjStopAtPos(0, 73);
         case '~':
            return this.jjStopAtPos(0, 83);
      }
   }

   private final int jjMoveStringLiteralDfa1_0(long var1, long var3) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var6) {
         this.jjStopStringLiteralDfa_0(0, var1, var3);
         return 1;
      }

      switch (this.curChar) {
         case '&':
            if ((var3 & 134217728L) != 0L) {
               return this.jjStopAtPos(1, 91);
            }
         case '\'':
         case '(':
         case ')':
         case '*':
         case ',':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'c':
         case 'd':
         case 'g':
         case 'j':
         case 'k':
         case 'p':
         case 'q':
         case 's':
         case 'v':
         case 'z':
         case '{':
         default:
            break;
         case '+':
            if ((var3 & 268435456L) != 0L) {
               return this.jjStopAtPos(1, 92);
            }
            break;
         case '-':
            if ((var3 & 536870912L) != 0L) {
               return this.jjStopAtPos(1, 93);
            }
            break;
         case '<':
            if ((var3 & 274877906944L) != 0L) {
               this.jjmatchedKind = 102;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_0(var1, 0L, var3, 562949953421312L);
         case '=':
            if ((var3 & 4194304L) != 0L) {
               return this.jjStopAtPos(1, 86);
            }

            if ((var3 & 8388608L) != 0L) {
               return this.jjStopAtPos(1, 87);
            }

            if ((var3 & 16777216L) != 0L) {
               return this.jjStopAtPos(1, 88);
            }

            if ((var3 & 33554432L) != 0L) {
               return this.jjStopAtPos(1, 89);
            }

            if ((var3 & 2199023255552L) != 0L) {
               return this.jjStopAtPos(1, 105);
            }

            if ((var3 & 4398046511104L) != 0L) {
               return this.jjStopAtPos(1, 106);
            }

            if ((var3 & 8796093022208L) != 0L) {
               return this.jjStopAtPos(1, 107);
            }

            if ((var3 & 17592186044416L) != 0L) {
               return this.jjStopAtPos(1, 108);
            }

            if ((var3 & 35184372088832L) != 0L) {
               return this.jjStopAtPos(1, 109);
            }

            if ((var3 & 70368744177664L) != 0L) {
               return this.jjStopAtPos(1, 110);
            }

            if ((var3 & 140737488355328L) != 0L) {
               return this.jjStopAtPos(1, 111);
            }

            if ((var3 & 281474976710656L) != 0L) {
               return this.jjStopAtPos(1, 112);
            }
            break;
         case '>':
            if ((var3 & 549755813888L) != 0L) {
               this.jjmatchedKind = 103;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_0(var1, 0L, var3, 3378799232155648L);
         case 'a':
            return this.jjMoveStringLiteralDfa2_0(var1, 1236967383040L, var3, 0L);
         case 'b':
            return this.jjMoveStringLiteralDfa2_0(var1, 512L, var3, 0L);
         case 'e':
            return this.jjMoveStringLiteralDfa2_0(var1, 17867064475648L, var3, 0L);
         case 'f':
            if ((var1 & 1073741824L) != 0L) {
               return this.jjStartNfaWithStates_0(1, 30, 28);
            }
            break;
         case 'h':
            return this.jjMoveStringLiteralDfa2_0(var1, 296146859871731712L, var3, 0L);
         case 'i':
            return this.jjMoveStringLiteralDfa2_0(var1, 100663296L, var3, 0L);
         case 'l':
            return this.jjMoveStringLiteralDfa2_0(var1, 138477568L, var3, 0L);
         case 'm':
            return this.jjMoveStringLiteralDfa2_0(var1, 6442450944L, var3, 0L);
         case 'n':
            return this.jjMoveStringLiteralDfa2_0(var1, 60129542144L, var3, 0L);
         case 'o':
            if ((var1 & 1048576L) != 0L) {
               this.jjmatchedKind = 20;
               this.jjmatchedPos = 1;
            }

            return this.jjMoveStringLiteralDfa2_0(var1, 216172851641058304L, var3, 0L);
         case 'r':
            return this.jjMoveStringLiteralDfa2_0(var1, 63056991852955648L, var3, 0L);
         case 't':
            return this.jjMoveStringLiteralDfa2_0(var1, 70368744177664L, var3, 0L);
         case 'u':
            return this.jjMoveStringLiteralDfa2_0(var1, 150083337191424L, var3, 0L);
         case 'w':
            return this.jjMoveStringLiteralDfa2_0(var1, 281474976710656L, var3, 0L);
         case 'x':
            return this.jjMoveStringLiteralDfa2_0(var1, 8388608L, var3, 0L);
         case 'y':
            return this.jjMoveStringLiteralDfa2_0(var1, 562949953425408L, var3, 0L);
         case '|':
            if ((var3 & 67108864L) != 0L) {
               return this.jjStopAtPos(1, 90);
            }
      }

      return this.jjStartNfa_0(0, var1, var3);
   }

   private final int jjMoveStringLiteralDfa2_0(long var1, long var3, long var5, long var7) {
      if (((var3 &= var1) | (var7 &= var5)) == 0L) {
         return this.jjStartNfa_0(0, var1, var5);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_0(1, var3, var7);
            return 2;
         }

         switch (this.curChar) {
            case '=':
               if ((var7 & 562949953421312L) != 0L) {
                  return this.jjStopAtPos(2, 113);
               }

               if ((var7 & 1125899906842624L) != 0L) {
                  return this.jjStopAtPos(2, 114);
               }
               break;
            case '>':
               if ((var7 & 1099511627776L) != 0L) {
                  this.jjmatchedKind = 104;
                  this.jjmatchedPos = 2;
               }

               return this.jjMoveStringLiteralDfa3_0(var3, 0L, var7, 2251799813685248L);
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'd':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'q':
            case 'v':
            case 'x':
            default:
               break;
            case 'a':
               return this.jjMoveStringLiteralDfa3_0(var3, 9077567999016960L, var7, 0L);
            case 'b':
               return this.jjMoveStringLiteralDfa3_0(var3, 8796093022208L, var7, 0L);
            case 'c':
               return this.jjMoveStringLiteralDfa3_0(var3, 1099511627776L, var7, 0L);
            case 'e':
               return this.jjMoveStringLiteralDfa3_0(var3, 2048L, var7, 0L);
            case 'f':
               return this.jjMoveStringLiteralDfa3_0(var3, 524288L, var7, 0L);
            case 'i':
               return this.jjMoveStringLiteralDfa3_0(var3, 361697544096448512L, var7, 0L);
            case 'l':
               return this.jjMoveStringLiteralDfa3_0(var3, 144115737848446976L, var7, 0L);
            case 'n':
               return this.jjMoveStringLiteralDfa3_0(var3, 563018773954560L, var7, 0L);
            case 'o':
               return this.jjMoveStringLiteralDfa3_0(var3, 39582552818688L, var7, 0L);
            case 'p':
               return this.jjMoveStringLiteralDfa3_0(var3, 140743930806272L, var7, 0L);
            case 'r':
               if ((var3 & 268435456L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 28, 28);
               }

               return this.jjMoveStringLiteralDfa3_0(var3, 6755399441055744L, var7, 0L);
            case 's':
               return this.jjMoveStringLiteralDfa3_0(var3, 8594137600L, var7, 0L);
            case 't':
               if ((var3 & 17179869184L) != 0L) {
                  this.jjmatchedKind = 34;
                  this.jjmatchedPos = 2;
               }

               return this.jjMoveStringLiteralDfa3_0(var3, 17764530016256L, var7, 0L);
            case 'u':
               return this.jjMoveStringLiteralDfa3_0(var3, 18014398511579136L, var7, 0L);
            case 'w':
               if ((var3 & 274877906944L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 38, 28);
               }
               break;
            case 'y':
               if ((var3 & 36028797018963968L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 55, 28);
               }
         }

         return this.jjStartNfa_0(1, var3, var7);
      }
   }

   private final int jjMoveStringLiteralDfa3_0(long var1, long var3, long var5, long var7) {
      if (((var3 &= var1) | (var7 &= var5)) == 0L) {
         return this.jjStartNfa_0(1, var1, var5);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_0(2, var3, var7);
            return 3;
         }

         switch (this.curChar) {
            case '=':
               if ((var7 & 2251799813685248L) != 0L) {
                  return this.jjStopAtPos(3, 115);
               }
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'f':
            case 'h':
            case 'j':
            case 'm':
            case 'p':
            case 'q':
            default:
               break;
            case 'a':
               return this.jjMoveStringLiteralDfa4_0(var3, 144115188311263232L, var7, 0L);
            case 'b':
               return this.jjMoveStringLiteralDfa4_0(var3, 2097152L, var7, 0L);
            case 'c':
               return this.jjMoveStringLiteralDfa4_0(var3, 562949953437696L, var7, 0L);
            case 'd':
               if ((var3 & 72057594037927936L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 56, 28);
               }
               break;
            case 'e':
               if ((var3 & 4096L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 12, 28);
               }

               if ((var3 & 8192L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 13, 28);
               }

               if ((var3 & 4194304L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 22, 28);
               }

               if ((var3 & 18014398509481984L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 54, 28);
               }

               return this.jjMoveStringLiteralDfa4_0(var3, 140771856482304L, var7, 0L);
            case 'g':
               if ((var3 & 68719476736L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 36, 28);
               }
               break;
            case 'i':
               return this.jjMoveStringLiteralDfa4_0(var3, 137438953472L, var7, 0L);
            case 'k':
               return this.jjMoveStringLiteralDfa4_0(var3, 1099511627776L, var7, 0L);
            case 'l':
               if ((var3 & 549755813888L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 39, 28);
               }

               return this.jjMoveStringLiteralDfa4_0(var3, 288239174392218624L, var7, 0L);
            case 'n':
               return this.jjMoveStringLiteralDfa4_0(var3, 9007199254740992L, var7, 0L);
            case 'o':
               if ((var3 & 536870912L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 29, 28);
               }

               return this.jjMoveStringLiteralDfa4_0(var3, 6755403736023040L, var7, 0L);
            case 'r':
               if ((var3 & 32768L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 15, 28);
               }

               return this.jjMoveStringLiteralDfa4_0(var3, 35184372088832L, var7, 0L);
            case 's':
               if ((var3 & 1125899906842624L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 50, 28);
               }

               return this.jjMoveStringLiteralDfa4_0(var3, 16973824L, var7, 0L);
            case 't':
               return this.jjMoveStringLiteralDfa4_0(var3, 356250357596672L, var7, 0L);
            case 'u':
               return this.jjMoveStringLiteralDfa4_0(var3, 17592186044416L, var7, 0L);
            case 'v':
               return this.jjMoveStringLiteralDfa4_0(var3, 2199023255552L, var7, 0L);
         }

         return this.jjStartNfa_0(2, var3, var7);
      }
   }

   private final int jjMoveStringLiteralDfa4_0(long var1, long var3, long var5, long var7) {
      if (((var3 &= var1) | var7 & var5) == 0L) {
         return this.jjStartNfa_0(2, var1, var5);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var10) {
            this.jjStopStringLiteralDfa_0(3, var3, 0L);
            return 4;
         }

         switch (this.curChar) {
            case 'a':
               return this.jjMoveStringLiteralDfa5_0(var3, 3307124817920L);
            case 'c':
               return this.jjMoveStringLiteralDfa5_0(var3, 281474976710656L);
            case 'e':
               if ((var3 & 16777216L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 24, 28);
               } else {
                  if ((var3 & 288230376151711744L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 58, 28);
                  }

                  return this.jjMoveStringLiteralDfa5_0(var3, 4400193995776L);
               }
            case 'h':
               if ((var3 & 16384L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 14, 28);
               }

               return this.jjMoveStringLiteralDfa5_0(var3, 562949953421312L);
            case 'i':
               return this.jjMoveStringLiteralDfa5_0(var3, 79164837462016L);
            case 'k':
               if ((var3 & 2048L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 11, 28);
               }
            case 'b':
            case 'd':
            case 'f':
            case 'g':
            case 'j':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            default:
               return this.jjStartNfa_0(3, var3, 0L);
            case 'l':
               if ((var3 & 33554432L) != 0L) {
                  this.jjmatchedKind = 25;
                  this.jjmatchedPos = 4;
               }

               return this.jjMoveStringLiteralDfa5_0(var3, 69206016L);
            case 'n':
               return this.jjMoveStringLiteralDfa5_0(var3, 8388608L);
            case 'r':
               if ((var3 & 140737488355328L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 47, 28);
               }

               return this.jjMoveStringLiteralDfa5_0(var3, 17630840750592L);
            case 's':
               if ((var3 & 65536L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 16, 28);
               }

               return this.jjMoveStringLiteralDfa5_0(var3, 9007199254740992L);
            case 't':
               if ((var3 & 131072L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 17, 28);
               } else if ((var3 & 134217728L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 27, 28);
               } else {
                  if ((var3 & 35184372088832L) != 0L) {
                     return this.jjStartNfaWithStates_0(4, 45, 28);
                  }

                  return this.jjMoveStringLiteralDfa5_0(var3, 144115188075855872L);
               }
            case 'u':
               return this.jjMoveStringLiteralDfa5_0(var3, 524288L);
            case 'v':
               return this.jjMoveStringLiteralDfa5_0(var3, 137438953472L);
            case 'w':
               if ((var3 & 2251799813685248L) != 0L) {
                  this.jjmatchedKind = 51;
                  this.jjmatchedPos = 4;
               }

               return this.jjMoveStringLiteralDfa5_0(var3, 4503599627370496L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa5_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(3, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(4, var3, 0L);
            return 5;
         }

         switch (this.curChar) {
            case 'a':
               return this.jjMoveStringLiteralDfa6_0(var3, 1536L);
            case 'b':
            case 'j':
            case 'k':
            case 'o':
            case 'p':
            case 'q':
            default:
               break;
            case 'c':
               if ((var3 & 8796093022208L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 43, 28);
               }

               if ((var3 & 70368744177664L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 46, 28);
               }

               return this.jjMoveStringLiteralDfa6_0(var3, 4398046511104L);
            case 'd':
               return this.jjMoveStringLiteralDfa6_0(var3, 8388608L);
            case 'e':
               if ((var3 & 2097152L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 21, 28);
               }

               if ((var3 & 137438953472L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 37, 28);
               }
               break;
            case 'f':
               return this.jjMoveStringLiteralDfa6_0(var3, 34359738368L);
            case 'g':
               return this.jjMoveStringLiteralDfa6_0(var3, 1099511627776L);
            case 'h':
               if ((var3 & 281474976710656L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 48, 28);
               }
               break;
            case 'i':
               return this.jjMoveStringLiteralDfa6_0(var3, 153122387330596864L);
            case 'l':
               return this.jjMoveStringLiteralDfa6_0(var3, 67633152L);
            case 'm':
               return this.jjMoveStringLiteralDfa6_0(var3, 2147483648L);
            case 'n':
               if ((var3 & 17592186044416L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 44, 28);
               }

               return this.jjMoveStringLiteralDfa6_0(var3, 8590196736L);
            case 'r':
               return this.jjMoveStringLiteralDfa6_0(var3, 562949953421312L);
            case 's':
               if ((var3 & 4503599627370496L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 52, 28);
               }
               break;
            case 't':
               if ((var3 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 32, 28);
               }

               return this.jjMoveStringLiteralDfa6_0(var3, 2199023255552L);
         }

         return this.jjStartNfa_0(4, var3, 0L);
      }
   }

   private final int jjMoveStringLiteralDfa6_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(4, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(5, var3, 0L);
            return 6;
         }

         switch (this.curChar) {
            case 'a':
               return this.jjMoveStringLiteralDfa7_0(var3, 34359738368L);
            case 'b':
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'p':
            case 'q':
            case 'r':
            case 'v':
            case 'w':
            case 'x':
            default:
               break;
            case 'c':
               return this.jjMoveStringLiteralDfa7_0(var3, 8589935104L);
            case 'e':
               if ((var3 & 1099511627776L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 40, 28);
               }

               if ((var3 & 2199023255552L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 41, 28);
               }

               return this.jjMoveStringLiteralDfa7_0(var3, 9007201402224640L);
            case 'l':
               return this.jjMoveStringLiteralDfa7_0(var3, 144115188075855872L);
            case 'n':
               if ((var3 & 1024L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 10, 28);
               }
               break;
            case 'o':
               return this.jjMoveStringLiteralDfa7_0(var3, 562949953421312L);
            case 's':
               if ((var3 & 8388608L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 23, 28);
               }
               break;
            case 't':
               if ((var3 & 524288L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 19, 28);
               }

               return this.jjMoveStringLiteralDfa7_0(var3, 4398046511104L);
            case 'u':
               return this.jjMoveStringLiteralDfa7_0(var3, 262144L);
            case 'y':
               if ((var3 & 67108864L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 26, 28);
               }
         }

         return this.jjStartNfa_0(5, var3, 0L);
      }
   }

   private final int jjMoveStringLiteralDfa7_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(5, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(6, var3, 0L);
            return 7;
         }

         switch (this.curChar) {
            case 'c':
               return this.jjMoveStringLiteralDfa8_0(var3, 34359738368L);
            case 'e':
               if ((var3 & 262144L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 18, 28);
               } else {
                  if ((var3 & 144115188075855872L) != 0L) {
                     return this.jjStartNfaWithStates_0(7, 57, 28);
                  }

                  return this.jjMoveStringLiteralDfa8_0(var3, 4406636445696L);
               }
            case 'n':
               return this.jjMoveStringLiteralDfa8_0(var3, 9570151355645952L);
            case 't':
               if ((var3 & 512L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 9, 28);
               }
            default:
               return this.jjStartNfa_0(6, var3, 0L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa8_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(6, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(7, var3, 0L);
            return 8;
         }

         switch (this.curChar) {
            case 'd':
               if ((var3 & 4398046511104L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 42, 28);
               }
               break;
            case 'e':
               if ((var3 & 34359738368L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 35, 28);
               }
               break;
            case 'i':
               return this.jjMoveStringLiteralDfa9_0(var3, 562949953421312L);
            case 'o':
               return this.jjMoveStringLiteralDfa9_0(var3, 8589934592L);
            case 't':
               if ((var3 & 9007199254740992L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 53, 28);
               }

               return this.jjMoveStringLiteralDfa9_0(var3, 2147483648L);
         }

         return this.jjStartNfa_0(7, var3, 0L);
      }
   }

   private final int jjMoveStringLiteralDfa9_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(7, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(8, var3, 0L);
            return 9;
         }

         switch (this.curChar) {
            case 'f':
               if ((var3 & 8589934592L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 33, 28);
               }
               break;
            case 's':
               if ((var3 & 2147483648L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 31, 28);
               }
               break;
            case 'z':
               return this.jjMoveStringLiteralDfa10_0(var3, 562949953421312L);
         }

         return this.jjStartNfa_0(8, var3, 0L);
      }
   }

   private final int jjMoveStringLiteralDfa10_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(8, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(9, var3, 0L);
            return 10;
         }

         switch (this.curChar) {
            case 'e':
               return this.jjMoveStringLiteralDfa11_0(var3, 562949953421312L);
            default:
               return this.jjStartNfa_0(9, var3, 0L);
         }
      }
   }

   private final int jjMoveStringLiteralDfa11_0(long var1, long var3) {
      if ((var3 &= var1) == 0L) {
         return this.jjStartNfa_0(9, var1, 0L);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(10, var3, 0L);
            return 11;
         }

         switch (this.curChar) {
            case 'd':
               if ((var3 & 562949953421312L) != 0L) {
                  return this.jjStartNfaWithStates_0(11, 49, 28);
               }
            default:
               return this.jjStartNfa_0(10, var3, 0L);
         }
      }
   }

   private final void jjCheckNAdd(int var1) {
      if (this.jjrounds[var1] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = var1;
         this.jjrounds[var1] = this.jjround;
      }

   }

   private final void jjAddStates(int var1, int var2) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[var1];
      } while(var1++ != var2);

   }

   private final void jjCheckNAddTwoStates(int var1, int var2) {
      this.jjCheckNAdd(var1);
      this.jjCheckNAdd(var2);
   }

   private final void jjCheckNAddStates(int var1, int var2) {
      do {
         this.jjCheckNAdd(jjnextStates[var1]);
      } while(var1++ != var2);

   }

   private final void jjCheckNAddStates(int var1) {
      this.jjCheckNAdd(jjnextStates[var1]);
      this.jjCheckNAdd(jjnextStates[var1 + 1]);
   }

   private final int jjMoveNfa_0(int var1, int var2) {
      int var3 = 0;
      this.jjnewStateCnt = 67;
      int var4 = 1;
      this.jjstateSet[0] = var1;
      int var5 = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long var14;
         if (this.curChar < '@') {
            var14 = 1L << this.curChar;

            do {
               --var4;
               switch (this.jjstateSet[var4]) {
                  case 0:
                     if ((287948901175001088L & var14) != 0L) {
                        this.jjCheckNAddStates(0, 6);
                     } else if (this.curChar == '/') {
                        this.jjAddStates(7, 9);
                     } else if (this.curChar == '$') {
                        if (var5 > 67) {
                           var5 = 67;
                        }

                        this.jjCheckNAdd(28);
                     } else if (this.curChar == '"') {
                        this.jjCheckNAddStates(10, 12);
                     } else if (this.curChar == '\'') {
                        this.jjAddStates(13, 14);
                     } else if (this.curChar == '.') {
                        this.jjCheckNAdd(4);
                     }

                     if ((287667426198290432L & var14) != 0L) {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddTwoStates(1, 2);
                     } else if (this.curChar == '0') {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddStates(15, 17);
                     }
                     break;
                  case 1:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddTwoStates(1, 2);
                     }
                  case 2:
                  case 5:
                  case 8:
                  case 12:
                  case 20:
                  case 33:
                  case 37:
                  case 41:
                  case 45:
                  default:
                     break;
                  case 3:
                     if (this.curChar == '.') {
                        this.jjCheckNAdd(4);
                     }
                     break;
                  case 4:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 63) {
                           var5 = 63;
                        }

                        this.jjCheckNAddStates(21, 23);
                     }
                     break;
                  case 6:
                     if ((43980465111040L & var14) != 0L) {
                        this.jjCheckNAdd(7);
                     }
                     break;
                  case 7:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 63) {
                           var5 = 63;
                        }

                        this.jjCheckNAddTwoStates(7, 8);
                     }
                     break;
                  case 9:
                     if (this.curChar == '\'') {
                        this.jjAddStates(13, 14);
                     }
                     break;
                  case 10:
                     if ((-549755823105L & var14) != 0L) {
                        this.jjCheckNAdd(11);
                     }
                     break;
                  case 11:
                     if (this.curChar == '\'' && var5 > 65) {
                        var5 = 65;
                     }
                     break;
                  case 13:
                     if ((566935683072L & var14) != 0L) {
                        this.jjCheckNAdd(11);
                     }
                     break;
                  case 14:
                     if ((71776119061217280L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(15, 11);
                     }
                     break;
                  case 15:
                     if ((71776119061217280L & var14) != 0L) {
                        this.jjCheckNAdd(11);
                     }
                     break;
                  case 16:
                     if ((4222124650659840L & var14) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 17;
                     }
                     break;
                  case 17:
                     if ((71776119061217280L & var14) != 0L) {
                        this.jjCheckNAdd(15);
                     }
                     break;
                  case 18:
                     if (this.curChar == '"') {
                        this.jjCheckNAddStates(10, 12);
                     }
                     break;
                  case 19:
                     if ((-17179878401L & var14) != 0L) {
                        this.jjCheckNAddStates(10, 12);
                     }
                     break;
                  case 21:
                     if ((566935683072L & var14) != 0L) {
                        this.jjCheckNAddStates(10, 12);
                     }
                     break;
                  case 22:
                     if (this.curChar == '"' && var5 > 66) {
                        var5 = 66;
                     }
                     break;
                  case 23:
                     if ((71776119061217280L & var14) != 0L) {
                        this.jjCheckNAddStates(24, 27);
                     }
                     break;
                  case 24:
                     if ((71776119061217280L & var14) != 0L) {
                        this.jjCheckNAddStates(10, 12);
                     }
                     break;
                  case 25:
                     if ((4222124650659840L & var14) != 0L) {
                        this.jjstateSet[this.jjnewStateCnt++] = 26;
                     }
                     break;
                  case 26:
                     if ((71776119061217280L & var14) != 0L) {
                        this.jjCheckNAdd(24);
                     }
                     break;
                  case 27:
                     if (this.curChar == '$') {
                        if (var5 > 67) {
                           var5 = 67;
                        }

                        this.jjCheckNAdd(28);
                     }
                     break;
                  case 28:
                     if ((287948969894477824L & var14) != 0L) {
                        if (var5 > 67) {
                           var5 = 67;
                        }

                        this.jjCheckNAdd(28);
                     }
                     break;
                  case 29:
                     if ((287948901175001088L & var14) != 0L) {
                        this.jjCheckNAddStates(0, 6);
                     }
                     break;
                  case 30:
                     if ((287948901175001088L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(30, 31);
                     }
                     break;
                  case 31:
                     if (this.curChar == '.') {
                        if (var5 > 63) {
                           var5 = 63;
                        }

                        this.jjCheckNAddStates(28, 30);
                     }
                     break;
                  case 32:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 63) {
                           var5 = 63;
                        }

                        this.jjCheckNAddStates(28, 30);
                     }
                     break;
                  case 34:
                     if ((43980465111040L & var14) != 0L) {
                        this.jjCheckNAdd(35);
                     }
                     break;
                  case 35:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 63) {
                           var5 = 63;
                        }

                        this.jjCheckNAddTwoStates(35, 8);
                     }
                     break;
                  case 36:
                     if ((287948901175001088L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(36, 37);
                     }
                     break;
                  case 38:
                     if ((43980465111040L & var14) != 0L) {
                        this.jjCheckNAdd(39);
                     }
                     break;
                  case 39:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 63) {
                           var5 = 63;
                        }

                        this.jjCheckNAddTwoStates(39, 8);
                     }
                     break;
                  case 40:
                     if ((287948901175001088L & var14) != 0L) {
                        this.jjCheckNAddStates(31, 33);
                     }
                     break;
                  case 42:
                     if ((43980465111040L & var14) != 0L) {
                        this.jjCheckNAdd(43);
                     }
                     break;
                  case 43:
                     if ((287948901175001088L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(43, 8);
                     }
                     break;
                  case 44:
                     if (this.curChar == '0') {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddStates(15, 17);
                     }
                     break;
                  case 46:
                     if ((287948901175001088L & var14) != 0L) {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddTwoStates(46, 2);
                     }
                     break;
                  case 47:
                     if ((71776119061217280L & var14) != 0L) {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddTwoStates(47, 2);
                     }
                     break;
                  case 48:
                     if (this.curChar == '/') {
                        this.jjAddStates(7, 9);
                     }
                     break;
                  case 49:
                     if (this.curChar == '*') {
                        this.jjCheckNAddTwoStates(62, 63);
                     } else if (this.curChar == '/') {
                        this.jjCheckNAddStates(18, 20);
                     }

                     if (this.curChar == '*') {
                        this.jjstateSet[this.jjnewStateCnt++] = 54;
                     }
                     break;
                  case 50:
                     if ((-9217L & var14) != 0L) {
                        this.jjCheckNAddStates(18, 20);
                     }
                     break;
                  case 51:
                     if ((9216L & var14) != 0L && var5 > 6) {
                        var5 = 6;
                     }
                     break;
                  case 52:
                     if (this.curChar == '\n' && var5 > 6) {
                        var5 = 6;
                     }
                     break;
                  case 53:
                     if (this.curChar == '\r') {
                        this.jjstateSet[this.jjnewStateCnt++] = 52;
                     }
                     break;
                  case 54:
                     if (this.curChar == '*') {
                        this.jjCheckNAddTwoStates(55, 56);
                     }
                     break;
                  case 55:
                     if ((-4398046511105L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(55, 56);
                     }
                     break;
                  case 56:
                     if (this.curChar == '*') {
                        this.jjCheckNAddStates(34, 36);
                     }
                     break;
                  case 57:
                     if ((-145135534866433L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(58, 56);
                     }
                     break;
                  case 58:
                     if ((-4398046511105L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(58, 56);
                     }
                     break;
                  case 59:
                     if (this.curChar == '/' && var5 > 7) {
                        var5 = 7;
                     }
                     break;
                  case 60:
                     if (this.curChar == '*') {
                        this.jjstateSet[this.jjnewStateCnt++] = 54;
                     }
                     break;
                  case 61:
                     if (this.curChar == '*') {
                        this.jjCheckNAddTwoStates(62, 63);
                     }
                     break;
                  case 62:
                     if ((-4398046511105L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(62, 63);
                     }
                     break;
                  case 63:
                     if (this.curChar == '*') {
                        this.jjCheckNAddStates(37, 39);
                     }
                     break;
                  case 64:
                     if ((-145135534866433L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(65, 63);
                     }
                     break;
                  case 65:
                     if ((-4398046511105L & var14) != 0L) {
                        this.jjCheckNAddTwoStates(65, 63);
                     }
                     break;
                  case 66:
                     if (this.curChar == '/' && var5 > 8) {
                        var5 = 8;
                     }
               }
            } while(var4 != var3);
         } else if (this.curChar < 128) {
            var14 = 1L << (this.curChar & 63);

            do {
               --var4;
               switch (this.jjstateSet[var4]) {
                  case 0:
                  case 28:
                     if ((576460745995190270L & var14) != 0L) {
                        if (var5 > 67) {
                           var5 = 67;
                        }

                        this.jjCheckNAdd(28);
                     }
                  case 1:
                  case 3:
                  case 4:
                  case 6:
                  case 7:
                  case 9:
                  case 11:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 26:
                  case 27:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 34:
                  case 35:
                  case 36:
                  case 38:
                  case 39:
                  case 40:
                  case 42:
                  case 43:
                  case 44:
                  case 47:
                  case 48:
                  case 49:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 56:
                  case 59:
                  case 60:
                  case 61:
                  case 63:
                  default:
                     break;
                  case 2:
                     if ((17592186048512L & var14) != 0L && var5 > 59) {
                        var5 = 59;
                     }
                     break;
                  case 5:
                     if ((137438953504L & var14) != 0L) {
                        this.jjAddStates(40, 41);
                     }
                     break;
                  case 8:
                     if ((343597383760L & var14) != 0L && var5 > 63) {
                        var5 = 63;
                     }
                     break;
                  case 10:
                     if ((-268435457L & var14) != 0L) {
                        this.jjCheckNAdd(11);
                     }
                     break;
                  case 12:
                     if (this.curChar == '\\') {
                        this.jjAddStates(42, 44);
                     }
                     break;
                  case 13:
                     if ((5700160604602368L & var14) != 0L) {
                        this.jjCheckNAdd(11);
                     }
                     break;
                  case 19:
                     if ((-268435457L & var14) != 0L) {
                        this.jjCheckNAddStates(10, 12);
                     }
                     break;
                  case 20:
                     if (this.curChar == '\\') {
                        this.jjAddStates(45, 47);
                     }
                     break;
                  case 21:
                     if ((5700160604602368L & var14) != 0L) {
                        this.jjCheckNAddStates(10, 12);
                     }
                     break;
                  case 33:
                     if ((137438953504L & var14) != 0L) {
                        this.jjAddStates(48, 49);
                     }
                     break;
                  case 37:
                     if ((137438953504L & var14) != 0L) {
                        this.jjAddStates(50, 51);
                     }
                     break;
                  case 41:
                     if ((137438953504L & var14) != 0L) {
                        this.jjAddStates(52, 53);
                     }
                     break;
                  case 45:
                     if ((72057594054705152L & var14) != 0L) {
                        this.jjCheckNAdd(46);
                     }
                     break;
                  case 46:
                     if ((541165879422L & var14) != 0L) {
                        if (var5 > 59) {
                           var5 = 59;
                        }

                        this.jjCheckNAddTwoStates(46, 2);
                     }
                     break;
                  case 50:
                     this.jjAddStates(18, 20);
                     break;
                  case 55:
                     this.jjCheckNAddTwoStates(55, 56);
                     break;
                  case 57:
                  case 58:
                     this.jjCheckNAddTwoStates(58, 56);
                     break;
                  case 62:
                     this.jjCheckNAddTwoStates(62, 63);
                     break;
                  case 64:
                  case 65:
                     this.jjCheckNAddTwoStates(65, 63);
               }
            } while(var4 != var3);
         } else {
            int var6 = this.curChar >> 8;
            int var7 = var6 >> 6;
            long var8 = 1L << (var6 & 63);
            int var10 = (this.curChar & 255) >> 6;
            long var11 = 1L << (this.curChar & 63);

            do {
               --var4;
               switch (this.jjstateSet[var4]) {
                  case 0:
                  case 28:
                     if (jjCanMove_1(var6, var7, var10, var8, var11)) {
                        if (var5 > 67) {
                           var5 = 67;
                        }

                        this.jjCheckNAdd(28);
                     }
                     break;
                  case 10:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjstateSet[this.jjnewStateCnt++] = 11;
                     }
                     break;
                  case 19:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjAddStates(10, 12);
                     }
                     break;
                  case 50:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjAddStates(18, 20);
                     }
                     break;
                  case 55:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjCheckNAddTwoStates(55, 56);
                     }
                     break;
                  case 57:
                  case 58:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjCheckNAddTwoStates(58, 56);
                     }
                     break;
                  case 62:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjCheckNAddTwoStates(62, 63);
                     }
                     break;
                  case 64:
                  case 65:
                     if (jjCanMove_0(var6, var7, var10, var8, var11)) {
                        this.jjCheckNAddTwoStates(65, 63);
                     }
               }
            } while(var4 != var3);
         }

         if (var5 != Integer.MAX_VALUE) {
            this.jjmatchedKind = var5;
            this.jjmatchedPos = var2;
            var5 = Integer.MAX_VALUE;
         }

         ++var2;
         if ((var4 = this.jjnewStateCnt) == (var3 = 67 - (this.jjnewStateCnt = var3))) {
            return var2;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return var2;
         }
      }
   }

   private static final boolean jjCanMove_0(int var0, int var1, int var2, long var3, long var5) {
      switch (var0) {
         case 0:
            return (jjbitVec2[var2] & var5) != 0L;
         default:
            return (jjbitVec0[var1] & var3) != 0L;
      }
   }

   private static final boolean jjCanMove_1(int var0, int var1, int var2, long var3, long var5) {
      switch (var0) {
         case 0:
            return (jjbitVec4[var2] & var5) != 0L;
         case 48:
            return (jjbitVec5[var2] & var5) != 0L;
         case 49:
            return (jjbitVec6[var2] & var5) != 0L;
         case 51:
            return (jjbitVec7[var2] & var5) != 0L;
         case 61:
            return (jjbitVec8[var2] & var5) != 0L;
         default:
            return (jjbitVec3[var1] & var3) != 0L;
      }
   }

   public ExpressionParserTokenManager(ASCII_UCodeESC_CharStream var1) {
      this.jjrounds = new int[67];
      this.jjstateSet = new int[134];
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = var1;
   }

   public ExpressionParserTokenManager(ASCII_UCodeESC_CharStream var1, int var2) {
      this(var1);
      this.SwitchTo(var2);
   }

   public void ReInit(ASCII_UCodeESC_CharStream var1) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = var1;
      this.ReInitRounds();
   }

   private final void ReInitRounds() {
      this.jjround = -2147483647;

      for(int var1 = 67; var1-- > 0; this.jjrounds[var1] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(ASCII_UCodeESC_CharStream var1, int var2) {
      this.ReInit(var1);
      this.SwitchTo(var2);
   }

   public void SwitchTo(int var1) {
      if (var1 < 1 && var1 >= 0) {
         this.curLexState = var1;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + var1 + ". State unchanged.", 2);
      }
   }

   private final Token jjFillToken() {
      Token var1 = Token.newToken(this.jjmatchedKind);
      var1.kind = this.jjmatchedKind;
      String var2 = jjstrLiteralImages[this.jjmatchedKind];
      var1.image = var2 == null ? this.input_stream.GetImage() : var2;
      var1.beginLine = this.input_stream.getBeginLine();
      var1.beginColumn = this.input_stream.getBeginColumn();
      var1.endLine = this.input_stream.getEndLine();
      var1.endColumn = this.input_stream.getEndColumn();
      return var1;
   }

   public final Token getNextToken() {
      Token var1 = null;
      boolean var3 = false;

      while(true) {
         Token var2;
         label74:
         while(true) {
            try {
               this.curChar = this.input_stream.BeginToken();
            } catch (IOException var9) {
               this.jjmatchedKind = 0;
               var2 = this.jjFillToken();
               var2.specialToken = var1;
               return var2;
            }

            try {
               while(true) {
                  if (this.curChar > ' ' || (4294981120L & 1L << this.curChar) == 0L) {
                     break label74;
                  }

                  this.curChar = this.input_stream.BeginToken();
               }
            } catch (IOException var11) {
            }
         }

         this.jjmatchedKind = Integer.MAX_VALUE;
         this.jjmatchedPos = 0;
         int var12 = this.jjMoveStringLiteralDfa0_0();
         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int var4 = this.input_stream.getEndLine();
            int var5 = this.input_stream.getEndColumn();
            String var6 = null;
            boolean var7 = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var10) {
               var7 = true;
               var6 = var12 <= 1 ? "" : this.input_stream.GetImage();
               if (this.curChar != '\n' && this.curChar != '\r') {
                  ++var5;
               } else {
                  ++var4;
                  var5 = 0;
               }
            }

            if (!var7) {
               this.input_stream.backup(1);
               var6 = var12 <= 1 ? "" : this.input_stream.GetImage();
            }

            throw new TokenMgrError(var7, this.curLexState, var4, var5, var6, this.curChar, 0);
         }

         if (this.jjmatchedPos + 1 < var12) {
            this.input_stream.backup(var12 - this.jjmatchedPos - 1);
         }

         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            var2 = this.jjFillToken();
            var2.specialToken = var1;
            return var2;
         }

         if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            var2 = this.jjFillToken();
            if (var1 == null) {
               var1 = var2;
            } else {
               var2.specialToken = var1;
               var1 = var1.next = var2;
            }
         }
      }
   }
}
