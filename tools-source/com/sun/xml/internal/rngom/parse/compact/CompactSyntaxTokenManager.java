package com.sun.xml.internal.rngom.parse.compact;

import java.io.IOException;
import java.io.PrintStream;

public class CompactSyntaxTokenManager implements CompactSyntaxConstants {
   public PrintStream debugStream;
   static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
   static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
   static final long[] jjbitVec3 = new long[]{0L, -16384L, -17590038560769L, 8388607L};
   static final long[] jjbitVec4 = new long[]{0L, 0L, 0L, -36028797027352577L};
   static final long[] jjbitVec5 = new long[]{9219994337134247935L, 9223372036854775294L, -1L, -274156627316187121L};
   static final long[] jjbitVec6 = new long[]{16777215L, -65536L, -576458553280167937L, 3L};
   static final long[] jjbitVec7 = new long[]{0L, 0L, -17179879616L, 4503588160110591L};
   static final long[] jjbitVec8 = new long[]{-8194L, -536936449L, -65533L, 234134404065073567L};
   static final long[] jjbitVec9 = new long[]{-562949953421312L, -8547991553L, 127L, 1979120929931264L};
   static final long[] jjbitVec10 = new long[]{576460743713488896L, -562949953419266L, 9007199254740991999L, 412319973375L};
   static final long[] jjbitVec11 = new long[]{2594073385365405664L, 17163091968L, 271902628478820320L, 844440767823872L};
   static final long[] jjbitVec12 = new long[]{247132830528276448L, 7881300924956672L, 2589004636761075680L, 4294967296L};
   static final long[] jjbitVec13 = new long[]{2579997437506199520L, 15837691904L, 270153412153034720L, 0L};
   static final long[] jjbitVec14 = new long[]{283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L};
   static final long[] jjbitVec15 = new long[]{288228177128316896L, 12884901888L, 0L, 0L};
   static final long[] jjbitVec16 = new long[]{3799912185593854L, 63L, 2309621682768192918L, 31L};
   static final long[] jjbitVec17 = new long[]{0L, 4398046510847L, 0L, 0L};
   static final long[] jjbitVec18 = new long[]{0L, 0L, -4294967296L, 36028797018898495L};
   static final long[] jjbitVec19 = new long[]{5764607523034749677L, 12493387738468353L, -756383734487318528L, 144405459145588743L};
   static final long[] jjbitVec20 = new long[]{-1L, -1L, -4026531841L, 288230376151711743L};
   static final long[] jjbitVec21 = new long[]{-3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L};
   static final long[] jjbitVec22 = new long[]{83837761617920L, 0L, 7L, 0L};
   static final long[] jjbitVec23 = new long[]{4389456576640L, -2L, -8587837441L, 576460752303423487L};
   static final long[] jjbitVec24 = new long[]{35184372088800L, 0L, 0L, 0L};
   static final long[] jjbitVec25 = new long[]{-1L, -1L, 274877906943L, 0L};
   static final long[] jjbitVec26 = new long[]{-1L, -1L, 68719476735L, 0L};
   static final long[] jjbitVec27 = new long[]{0L, 0L, 36028797018963968L, -36028797027352577L};
   static final long[] jjbitVec28 = new long[]{16777215L, -65536L, -576458553280167937L, 196611L};
   static final long[] jjbitVec29 = new long[]{-1L, 12884901951L, -17179879488L, 4503588160110591L};
   static final long[] jjbitVec30 = new long[]{-8194L, -536936449L, -65413L, 234134404065073567L};
   static final long[] jjbitVec31 = new long[]{-562949953421312L, -8547991553L, -4899916411759099777L, 1979120929931286L};
   static final long[] jjbitVec32 = new long[]{576460743713488896L, -277081224642561L, 9007199254740991999L, 288017070894841855L};
   static final long[] jjbitVec33 = new long[]{-864691128455135250L, 281268803485695L, -3186861885341720594L, 1125692414638495L};
   static final long[] jjbitVec34 = new long[]{-3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393786303L};
   static final long[] jjbitVec35 = new long[]{-878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L};
   static final long[] jjbitVec36 = new long[]{-4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L};
   static final long[] jjbitVec37 = new long[]{-4323457841299070996L, 281212992110031L, 0L, 0L};
   static final long[] jjbitVec38 = new long[]{576320014815068158L, 67076095L, 4323293666156225942L, 67059551L};
   static final long[] jjbitVec39 = new long[]{-4422530440275951616L, -558551906910465L, 215680200883507167L, 0L};
   static final long[] jjbitVec40 = new long[]{0L, 0L, 0L, 9126739968L};
   static final long[] jjbitVec41 = new long[]{17732914942836896L, -2L, -6876561409L, 8646911284551352319L};
   static final int[] jjnextStates = new int[]{16, 17, 18, 19, 21, 25, 26, 27, 28, 30, 35, 36, 38, 39, 40, 10, 11, 13, 14, 3, 6, 7, 8};
   public static final String[] jjstrLiteralImages = new String[]{"", "[", "=", "&=", "|=", "start", "div", "include", "~", "]", "grammar", "{", "}", "namespace", "default", "inherit", "datatypes", "empty", "text", "notAllowed", "|", "&", ",", "+", "?", "*", "element", "attribute", "(", ")", "-", "list", "mixed", "external", "parent", "string", "token", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ">>", null};
   public static final String[] lexStateNames = new String[]{"DEFAULT", "AFTER_SINGLE_LINE_COMMENT", "AFTER_DOCUMENTATION"};
   public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 2, -1, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
   static final long[] jjtoToken = new long[]{2287840842771070975L};
   static final long[] jjtoSkip = new long[]{22539988369408L};
   static final long[] jjtoSpecial = new long[]{21990232555520L};
   protected JavaCharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   private final StringBuilder jjimage;
   private StringBuilder image;
   private int jjimageLen;
   private int lengthOfMatch;
   protected char curChar;
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   public void setDebugStream(PrintStream ds) {
      this.debugStream = ds;
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0) {
      switch (pos) {
         case 0:
            if ((active0 & 135493838048L) != 0L) {
               this.jjmatchedKind = 54;
               return 43;
            } else {
               if ((active0 & 576460752303423488L) != 0L) {
                  this.jjmatchedKind = 60;
                  return -1;
               }

               return -1;
            }
         case 1:
            if ((active0 & 135493838048L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 1;
               return 43;
            } else {
               if ((active0 & 576460752303423488L) != 0L) {
                  if (this.jjmatchedPos == 0) {
                     this.jjmatchedKind = 60;
                     this.jjmatchedPos = 0;
                  }

                  return -1;
               }

               return -1;
            }
         case 2:
            if ((active0 & 135493837984L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 2;
               return 43;
            } else {
               if ((active0 & 64L) != 0L) {
                  return 43;
               }

               return -1;
            }
         case 3:
            if ((active0 & 133346092192L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 3;
               return 43;
            } else {
               if ((active0 & 2147745792L) != 0L) {
                  return 43;
               }

               return -1;
            }
         case 4:
            if ((active0 & 60331517056L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 4;
               return 43;
            } else {
               if ((active0 & 73014575136L) != 0L) {
                  return 43;
               }

               return -1;
            }
         case 5:
            if ((active0 & 8791909504L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 5;
               return 43;
            } else {
               if ((active0 & 51539607552L) != 0L) {
                  return 43;
               }

               return -1;
            }
         case 6:
            if ((active0 & 8724750336L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 6;
               return 43;
            } else {
               if ((active0 & 67159168L) != 0L) {
                  return 43;
               }

               return -1;
            }
         case 7:
            if ((active0 & 134815744L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 7;
               return 43;
            } else {
               if ((active0 & 8589934592L) != 0L) {
                  return 43;
               }

               return -1;
            }
         case 8:
            if ((active0 & 524288L) != 0L) {
               this.jjmatchedKind = 54;
               this.jjmatchedPos = 8;
               return 43;
            } else {
               if ((active0 & 134291456L) != 0L) {
                  return 43;
               }

               return -1;
            }
         default:
            return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch (this.curChar) {
         case '&':
            this.jjmatchedKind = 21;
            return this.jjMoveStringLiteralDfa1_0(8L);
         case '\'':
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
         case '<':
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
         case '^':
         case '_':
         case '`':
         case 'b':
         case 'c':
         case 'f':
         case 'h':
         case 'j':
         case 'k':
         case 'o':
         case 'q':
         case 'r':
         case 'u':
         case 'v':
         case 'w':
         case 'x':
         case 'y':
         case 'z':
         default:
            return this.jjMoveNfa_0(3, 0);
         case '(':
            return this.jjStopAtPos(0, 28);
         case ')':
            return this.jjStopAtPos(0, 29);
         case '*':
            return this.jjStopAtPos(0, 25);
         case '+':
            return this.jjStopAtPos(0, 23);
         case ',':
            return this.jjStopAtPos(0, 22);
         case '-':
            return this.jjStopAtPos(0, 30);
         case '=':
            return this.jjStopAtPos(0, 2);
         case '>':
            return this.jjMoveStringLiteralDfa1_0(576460752303423488L);
         case '?':
            return this.jjStopAtPos(0, 24);
         case '[':
            return this.jjStopAtPos(0, 1);
         case ']':
            return this.jjStopAtPos(0, 9);
         case 'a':
            return this.jjMoveStringLiteralDfa1_0(134217728L);
         case 'd':
            return this.jjMoveStringLiteralDfa1_0(81984L);
         case 'e':
            return this.jjMoveStringLiteralDfa1_0(8657174528L);
         case 'g':
            return this.jjMoveStringLiteralDfa1_0(1024L);
         case 'i':
            return this.jjMoveStringLiteralDfa1_0(32896L);
         case 'l':
            return this.jjMoveStringLiteralDfa1_0(2147483648L);
         case 'm':
            return this.jjMoveStringLiteralDfa1_0(4294967296L);
         case 'n':
            return this.jjMoveStringLiteralDfa1_0(532480L);
         case 'p':
            return this.jjMoveStringLiteralDfa1_0(17179869184L);
         case 's':
            return this.jjMoveStringLiteralDfa1_0(34359738400L);
         case 't':
            return this.jjMoveStringLiteralDfa1_0(68719738880L);
         case '{':
            return this.jjStopAtPos(0, 11);
         case '|':
            this.jjmatchedKind = 20;
            return this.jjMoveStringLiteralDfa1_0(16L);
         case '}':
            return this.jjStopAtPos(0, 12);
         case '~':
            return this.jjStopAtPos(0, 8);
      }
   }

   private int jjMoveStringLiteralDfa1_0(long active0) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_0(0, active0);
         return 1;
      }

      switch (this.curChar) {
         case '=':
            if ((active0 & 8L) != 0L) {
               return this.jjStopAtPos(1, 3);
            }

            if ((active0 & 16L) != 0L) {
               return this.jjStopAtPos(1, 4);
            }
            break;
         case '>':
            if ((active0 & 576460752303423488L) != 0L) {
               return this.jjStopAtPos(1, 59);
            }
            break;
         case 'a':
            return this.jjMoveStringLiteralDfa2_0(active0, 17179942912L);
         case 'e':
            return this.jjMoveStringLiteralDfa2_0(active0, 278528L);
         case 'i':
            return this.jjMoveStringLiteralDfa2_0(active0, 6442451008L);
         case 'l':
            return this.jjMoveStringLiteralDfa2_0(active0, 67108864L);
         case 'm':
            return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
         case 'n':
            return this.jjMoveStringLiteralDfa2_0(active0, 32896L);
         case 'o':
            return this.jjMoveStringLiteralDfa2_0(active0, 68720001024L);
         case 'r':
            return this.jjMoveStringLiteralDfa2_0(active0, 1024L);
         case 't':
            return this.jjMoveStringLiteralDfa2_0(active0, 34493956128L);
         case 'x':
            return this.jjMoveStringLiteralDfa2_0(active0, 8589934592L);
      }

      return this.jjStartNfa_0(0, active0);
   }

   private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(0, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
         }

         switch (this.curChar) {
            case 'a':
               return this.jjMoveStringLiteralDfa3_0(active0, 1056L);
            case 'c':
               return this.jjMoveStringLiteralDfa3_0(active0, 128L);
            case 'e':
               return this.jjMoveStringLiteralDfa3_0(active0, 67108864L);
            case 'f':
               return this.jjMoveStringLiteralDfa3_0(active0, 16384L);
            case 'h':
               return this.jjMoveStringLiteralDfa3_0(active0, 32768L);
            case 'k':
               return this.jjMoveStringLiteralDfa3_0(active0, 68719476736L);
            case 'm':
               return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
            case 'p':
               return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            case 'r':
               return this.jjMoveStringLiteralDfa3_0(active0, 51539607552L);
            case 's':
               return this.jjMoveStringLiteralDfa3_0(active0, 2147483648L);
            case 't':
               return this.jjMoveStringLiteralDfa3_0(active0, 8724742144L);
            case 'v':
               if ((active0 & 64L) != 0L) {
                  return this.jjStartNfaWithStates_0(2, 6, 43);
               }
            case 'b':
            case 'd':
            case 'g':
            case 'i':
            case 'j':
            case 'l':
            case 'n':
            case 'o':
            case 'q':
            case 'u':
            case 'w':
            default:
               return this.jjStartNfa_0(1, active0);
            case 'x':
               return this.jjMoveStringLiteralDfa3_0(active0, 4295229440L);
         }
      }
   }

   private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(1, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
         }

         switch (this.curChar) {
            case 'A':
               return this.jjMoveStringLiteralDfa4_0(active0, 524288L);
            case 'a':
               return this.jjMoveStringLiteralDfa4_0(active0, 81920L);
            case 'e':
               return this.jjMoveStringLiteralDfa4_0(active0, 98784288768L);
            case 'i':
               return this.jjMoveStringLiteralDfa4_0(active0, 34359738368L);
            case 'l':
               return this.jjMoveStringLiteralDfa4_0(active0, 128L);
            case 'm':
               return this.jjMoveStringLiteralDfa4_0(active0, 67109888L);
            case 'r':
               return this.jjMoveStringLiteralDfa4_0(active0, 134217760L);
            case 't':
               if ((active0 & 262144L) != 0L) {
                  return this.jjStartNfaWithStates_0(3, 18, 43);
               } else {
                  if ((active0 & 2147483648L) != 0L) {
                     return this.jjStartNfaWithStates_0(3, 31, 43);
                  }

                  return this.jjMoveStringLiteralDfa4_0(active0, 131072L);
               }
            default:
               return this.jjStartNfa_0(2, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(2, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
         }

         switch (this.curChar) {
            case 'd':
               if ((active0 & 4294967296L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 32, 43);
               }
               break;
            case 'e':
               return this.jjMoveStringLiteralDfa5_0(active0, 67108864L);
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'o':
            case 'p':
            case 'q':
            case 'v':
            case 'w':
            case 'x':
            default:
               break;
            case 'i':
               return this.jjMoveStringLiteralDfa5_0(active0, 134217728L);
            case 'l':
               return this.jjMoveStringLiteralDfa5_0(active0, 524288L);
            case 'm':
               return this.jjMoveStringLiteralDfa5_0(active0, 1024L);
            case 'n':
               if ((active0 & 68719476736L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 36, 43);
               }

               return this.jjMoveStringLiteralDfa5_0(active0, 51539607552L);
            case 'r':
               return this.jjMoveStringLiteralDfa5_0(active0, 8589967360L);
            case 's':
               return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
            case 't':
               if ((active0 & 32L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 5, 43);
               }

               return this.jjMoveStringLiteralDfa5_0(active0, 65536L);
            case 'u':
               return this.jjMoveStringLiteralDfa5_0(active0, 16512L);
            case 'y':
               if ((active0 & 131072L) != 0L) {
                  return this.jjStartNfaWithStates_0(4, 17, 43);
               }
         }

         return this.jjStartNfa_0(3, active0);
      }
   }

   private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(3, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
         }

         switch (this.curChar) {
            case 'a':
               return this.jjMoveStringLiteralDfa6_0(active0, 1024L);
            case 'b':
               return this.jjMoveStringLiteralDfa6_0(active0, 134217728L);
            case 'c':
            case 'e':
            case 'f':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'o':
            case 'q':
            case 'r':
            case 's':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            default:
               break;
            case 'd':
               return this.jjMoveStringLiteralDfa6_0(active0, 128L);
            case 'g':
               if ((active0 & 34359738368L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 35, 43);
               }
               break;
            case 'i':
               return this.jjMoveStringLiteralDfa6_0(active0, 32768L);
            case 'l':
               return this.jjMoveStringLiteralDfa6_0(active0, 540672L);
            case 'n':
               return this.jjMoveStringLiteralDfa6_0(active0, 8657043456L);
            case 'p':
               return this.jjMoveStringLiteralDfa6_0(active0, 8192L);
            case 't':
               if ((active0 & 17179869184L) != 0L) {
                  return this.jjStartNfaWithStates_0(5, 34, 43);
               }
               break;
            case 'y':
               return this.jjMoveStringLiteralDfa6_0(active0, 65536L);
         }

         return this.jjStartNfa_0(4, active0);
      }
   }

   private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(4, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
         }

         switch (this.curChar) {
            case 'a':
               return this.jjMoveStringLiteralDfa7_0(active0, 8589942784L);
            case 'b':
            case 'c':
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'q':
            case 's':
            default:
               break;
            case 'e':
               if ((active0 & 128L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 7, 43);
               }
               break;
            case 'o':
               return this.jjMoveStringLiteralDfa7_0(active0, 524288L);
            case 'p':
               return this.jjMoveStringLiteralDfa7_0(active0, 65536L);
            case 'r':
               if ((active0 & 1024L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 10, 43);
               }
               break;
            case 't':
               if ((active0 & 16384L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 14, 43);
               }

               if ((active0 & 32768L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 15, 43);
               }

               if ((active0 & 67108864L) != 0L) {
                  return this.jjStartNfaWithStates_0(6, 26, 43);
               }
               break;
            case 'u':
               return this.jjMoveStringLiteralDfa7_0(active0, 134217728L);
         }

         return this.jjStartNfa_0(5, active0);
      }
   }

   private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(5, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(6, active0);
            return 7;
         }

         switch (this.curChar) {
            case 'c':
               return this.jjMoveStringLiteralDfa8_0(active0, 8192L);
            case 'e':
               return this.jjMoveStringLiteralDfa8_0(active0, 65536L);
            case 'l':
               if ((active0 & 8589934592L) != 0L) {
                  return this.jjStartNfaWithStates_0(7, 33, 43);
               }
            default:
               return this.jjStartNfa_0(6, active0);
            case 't':
               return this.jjMoveStringLiteralDfa8_0(active0, 134217728L);
            case 'w':
               return this.jjMoveStringLiteralDfa8_0(active0, 524288L);
         }
      }
   }

   private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(6, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(7, active0);
            return 8;
         }

         switch (this.curChar) {
            case 'e':
               if ((active0 & 8192L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 13, 43);
               } else {
                  if ((active0 & 134217728L) != 0L) {
                     return this.jjStartNfaWithStates_0(8, 27, 43);
                  }

                  return this.jjMoveStringLiteralDfa9_0(active0, 524288L);
               }
            case 's':
               if ((active0 & 65536L) != 0L) {
                  return this.jjStartNfaWithStates_0(8, 16, 43);
               }
            default:
               return this.jjStartNfa_0(7, active0);
         }
      }
   }

   private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(7, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(8, active0);
            return 9;
         }

         switch (this.curChar) {
            case 'd':
               if ((active0 & 524288L) != 0L) {
                  return this.jjStartNfaWithStates_0(9, 19, 43);
               }
            default:
               return this.jjStartNfa_0(8, active0);
         }
      }
   }

   private int jjStartNfaWithStates_0(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_0(state, pos + 1);
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 43;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294968833L & l) != 0L) {
                        if (kind > 39) {
                           kind = 39;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if (this.curChar == '#') {
                        if (kind > 40) {
                           kind = 40;
                        }

                        this.jjCheckNAdd(2);
                     }
                     break;
                  case 2:
                     if ((-1026L & l) != 0L) {
                        if (kind > 40) {
                           kind = 40;
                        }

                        this.jjCheckNAdd(2);
                     }
                     break;
                  case 3:
                     if ((-1537L & l) != 0L && kind > 60) {
                        kind = 60;
                     }

                     if ((4294968833L & l) != 0L) {
                        if (kind > 39) {
                           kind = 39;
                        }

                        this.jjCheckNAdd(0);
                     } else if (this.curChar == '\'') {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     } else if (this.curChar == '"') {
                        this.jjstateSet[this.jjnewStateCnt++] = 22;
                     } else if (this.curChar == '#') {
                        if (kind > 42) {
                           kind = 42;
                        }

                        this.jjCheckNAdd(5);
                     }

                     if (this.curChar == '\'') {
                        this.jjCheckNAddTwoStates(13, 14);
                     } else if (this.curChar == '"') {
                        this.jjCheckNAddTwoStates(10, 11);
                     } else if (this.curChar == '#') {
                        this.jjstateSet[this.jjnewStateCnt++] = 1;
                     }
                     break;
                  case 4:
                     if (this.curChar == '#') {
                        if (kind > 42) {
                           kind = 42;
                        }

                        this.jjCheckNAdd(5);
                     }
                     break;
                  case 5:
                     if ((-1026L & l) != 0L) {
                        if (kind > 42) {
                           kind = 42;
                        }

                        this.jjCheckNAdd(5);
                     }
                  case 6:
                  case 7:
                  case 34:
                  case 41:
                  default:
                     break;
                  case 8:
                     if ((288054454291267584L & l) != 0L) {
                        if (kind > 55) {
                           kind = 55;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 8;
                     }
                     break;
                  case 9:
                     if (this.curChar == '"') {
                        this.jjCheckNAddTwoStates(10, 11);
                     }
                     break;
                  case 10:
                     if ((-17179869186L & l) != 0L) {
                        this.jjCheckNAddTwoStates(10, 11);
                     }
                     break;
                  case 11:
                  case 20:
                     if (this.curChar == '"' && kind > 58) {
                        kind = 58;
                     }
                     break;
                  case 12:
                     if (this.curChar == '\'') {
                        this.jjCheckNAddTwoStates(13, 14);
                     }
                     break;
                  case 13:
                     if ((-549755813890L & l) != 0L) {
                        this.jjCheckNAddTwoStates(13, 14);
                     }
                     break;
                  case 14:
                  case 29:
                     if (this.curChar == '\'' && kind > 58) {
                        kind = 58;
                     }
                     break;
                  case 15:
                     if (this.curChar == '"') {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 16:
                     if ((-17179869185L & l) != 0L) {
                        this.jjCheckNAddStates(0, 2);
                     }
                     break;
                  case 17:
                  case 19:
                     if (this.curChar == '"') {
                        this.jjCheckNAdd(16);
                     }
                     break;
                  case 18:
                     if (this.curChar == '"') {
                        this.jjAddStates(3, 4);
                     }
                     break;
                  case 21:
                     if (this.curChar == '"') {
                        this.jjstateSet[this.jjnewStateCnt++] = 20;
                     }
                     break;
                  case 22:
                     if (this.curChar == '"') {
                        this.jjstateSet[this.jjnewStateCnt++] = 15;
                     }
                     break;
                  case 23:
                     if (this.curChar == '"') {
                        this.jjstateSet[this.jjnewStateCnt++] = 22;
                     }
                     break;
                  case 24:
                     if (this.curChar == '\'') {
                        this.jjCheckNAddStates(5, 7);
                     }
                     break;
                  case 25:
                     if ((-549755813889L & l) != 0L) {
                        this.jjCheckNAddStates(5, 7);
                     }
                     break;
                  case 26:
                  case 28:
                     if (this.curChar == '\'') {
                        this.jjCheckNAdd(25);
                     }
                     break;
                  case 27:
                     if (this.curChar == '\'') {
                        this.jjAddStates(8, 9);
                     }
                     break;
                  case 30:
                     if (this.curChar == '\'') {
                        this.jjstateSet[this.jjnewStateCnt++] = 29;
                     }
                     break;
                  case 31:
                     if (this.curChar == '\'') {
                        this.jjstateSet[this.jjnewStateCnt++] = 24;
                     }
                     break;
                  case 32:
                     if (this.curChar == '\'') {
                        this.jjstateSet[this.jjnewStateCnt++] = 31;
                     }
                     break;
                  case 33:
                     if ((-1537L & l) != 0L && kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 35:
                     if ((288054454291267584L & l) != 0L) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAdd(35);
                     }
                     break;
                  case 36:
                     if ((288054454291267584L & l) != 0L) {
                        this.jjCheckNAddTwoStates(36, 38);
                     }
                     break;
                  case 37:
                     if (this.curChar == '*' && kind > 56) {
                        kind = 56;
                     }
                     break;
                  case 38:
                     if (this.curChar == ':') {
                        this.jjstateSet[this.jjnewStateCnt++] = 37;
                     }
                     break;
                  case 39:
                     if ((288054454291267584L & l) != 0L) {
                        this.jjCheckNAddTwoStates(39, 40);
                     }
                     break;
                  case 40:
                     if (this.curChar == ':') {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     }
                     break;
                  case 42:
                     if ((288054454291267584L & l) != 0L) {
                        if (kind > 57) {
                           kind = 57;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 42;
                     }
                     break;
                  case 43:
                     if ((288054454291267584L & l) != 0L) {
                        this.jjCheckNAddTwoStates(39, 40);
                     } else if (this.curChar == ':') {
                        this.jjstateSet[this.jjnewStateCnt++] = 41;
                     }

                     if ((288054454291267584L & l) != 0L) {
                        this.jjCheckNAddTwoStates(36, 38);
                     } else if (this.curChar == ':') {
                        this.jjstateSet[this.jjnewStateCnt++] = 37;
                     }

                     if ((288054454291267584L & l) != 0L) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAdd(35);
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 2:
                     if (kind > 40) {
                        kind = 40;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 2;
                     break;
                  case 3:
                     if (kind > 60) {
                        kind = 60;
                     }

                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAddStates(10, 14);
                     } else if (this.curChar == '\\') {
                        this.jjstateSet[this.jjnewStateCnt++] = 7;
                     }
                  case 4:
                  case 9:
                  case 11:
                  case 12:
                  case 14:
                  case 15:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 37:
                  case 38:
                  case 40:
                  default:
                     break;
                  case 5:
                     if (kind > 42) {
                        kind = 42;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 5;
                     break;
                  case 6:
                     if (this.curChar == '\\') {
                        this.jjstateSet[this.jjnewStateCnt++] = 7;
                     }
                     break;
                  case 7:
                  case 8:
                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 55) {
                           kind = 55;
                        }

                        this.jjCheckNAdd(8);
                     }
                     break;
                  case 10:
                     this.jjAddStates(15, 16);
                     break;
                  case 13:
                     this.jjAddStates(17, 18);
                     break;
                  case 16:
                     this.jjAddStates(0, 2);
                     break;
                  case 25:
                     this.jjAddStates(5, 7);
                     break;
                  case 33:
                     if (kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 34:
                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAddStates(10, 14);
                     }
                     break;
                  case 35:
                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAdd(35);
                     }
                     break;
                  case 36:
                     if ((576460745995190270L & l) != 0L) {
                        this.jjCheckNAddTwoStates(36, 38);
                     }
                     break;
                  case 39:
                     if ((576460745995190270L & l) != 0L) {
                        this.jjCheckNAddTwoStates(39, 40);
                     }
                     break;
                  case 41:
                  case 42:
                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 57) {
                           kind = 57;
                        }

                        this.jjCheckNAdd(42);
                     }
                     break;
                  case 43:
                     if ((576460745995190270L & l) != 0L) {
                        this.jjCheckNAddTwoStates(39, 40);
                     }

                     if ((576460745995190270L & l) != 0L) {
                        this.jjCheckNAddTwoStates(36, 38);
                     }

                     if ((576460745995190270L & l) != 0L) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAdd(35);
                     }
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 2:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 40) {
                           kind = 40;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 2;
                     }
                     break;
                  case 3:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 60) {
                        kind = 60;
                     }

                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAddStates(10, 14);
                     }
                  case 4:
                  case 6:
                  case 9:
                  case 11:
                  case 12:
                  case 14:
                  case 15:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 37:
                  case 38:
                  case 40:
                  default:
                     break;
                  case 5:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 42) {
                           kind = 42;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 5;
                     }
                     break;
                  case 7:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 55) {
                           kind = 55;
                        }

                        this.jjCheckNAdd(8);
                     }
                     break;
                  case 8:
                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        if (kind > 55) {
                           kind = 55;
                        }

                        this.jjCheckNAdd(8);
                     }
                     break;
                  case 10:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(15, 16);
                     }
                     break;
                  case 13:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(17, 18);
                     }
                     break;
                  case 16:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(0, 2);
                     }
                     break;
                  case 25:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        this.jjAddStates(5, 7);
                     }
                     break;
                  case 33:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 34:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAddStates(10, 14);
                     }
                     break;
                  case 35:
                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAdd(35);
                     }
                     break;
                  case 36:
                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        this.jjCheckNAddTwoStates(36, 38);
                     }
                     break;
                  case 39:
                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        this.jjCheckNAddTwoStates(39, 40);
                     }
                     break;
                  case 41:
                     if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                        if (kind > 57) {
                           kind = 57;
                        }

                        this.jjCheckNAdd(42);
                     }
                     break;
                  case 42:
                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        if (kind > 57) {
                           kind = 57;
                        }

                        this.jjCheckNAdd(42);
                     }
                     break;
                  case 43:
                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        if (kind > 54) {
                           kind = 54;
                        }

                        this.jjCheckNAdd(35);
                     }

                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        this.jjCheckNAddTwoStates(36, 38);
                     }

                     if (jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                        this.jjCheckNAddTwoStates(39, 40);
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 43 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private int jjMoveStringLiteralDfa0_1() {
      return this.jjMoveNfa_1(1, 0);
   }

   private int jjMoveNfa_1(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 10;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294968833L & l) != 0L) {
                        if (kind > 39) {
                           kind = 39;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((-1537L & l) != 0L && kind > 60) {
                        kind = 60;
                     }

                     if ((4294968833L & l) != 0L) {
                        if (kind > 39) {
                           kind = 39;
                        }

                        this.jjCheckNAdd(0);
                     }

                     if ((1025L & l) != 0L) {
                        this.jjCheckNAddStates(19, 22);
                     }
                     break;
                  case 2:
                     if ((1025L & l) != 0L) {
                        this.jjCheckNAddStates(19, 22);
                     }
                     break;
                  case 3:
                     if ((4294967808L & l) != 0L) {
                        this.jjCheckNAddTwoStates(3, 6);
                     }
                     break;
                  case 4:
                     if (this.curChar == '#') {
                        if (kind > 43) {
                           kind = 43;
                        }

                        this.jjCheckNAdd(5);
                     }
                     break;
                  case 5:
                     if ((-1026L & l) != 0L) {
                        if (kind > 43) {
                           kind = 43;
                        }

                        this.jjCheckNAdd(5);
                     }
                     break;
                  case 6:
                     if (this.curChar == '#') {
                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     }
                     break;
                  case 7:
                     if ((4294967808L & l) != 0L) {
                        this.jjCheckNAddTwoStates(7, 8);
                     }
                     break;
                  case 8:
                     if (this.curChar == '#') {
                        if (kind > 44) {
                           kind = 44;
                        }

                        this.jjCheckNAdd(9);
                     }
                     break;
                  case 9:
                     if ((-1026L & l) != 0L) {
                        if (kind > 44) {
                           kind = 44;
                        }

                        this.jjCheckNAdd(9);
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 5:
                     if (kind > 43) {
                        kind = 43;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 5;
                     break;
                  case 9:
                     if (kind > 44) {
                        kind = 44;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 9;
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 5:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 43) {
                           kind = 43;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 5;
                     }
                     break;
                  case 9:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 44) {
                           kind = 44;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 9;
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 10 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private int jjMoveStringLiteralDfa0_2() {
      return this.jjMoveNfa_2(1, 0);
   }

   private int jjMoveNfa_2(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 7;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 0:
                     if ((4294968833L & l) != 0L) {
                        if (kind > 39) {
                           kind = 39;
                        }

                        this.jjCheckNAdd(0);
                     }
                     break;
                  case 1:
                     if ((-1537L & l) != 0L && kind > 60) {
                        kind = 60;
                     }

                     if ((4294968833L & l) != 0L) {
                        if (kind > 39) {
                           kind = 39;
                        }

                        this.jjCheckNAdd(0);
                     }

                     if ((1025L & l) != 0L) {
                        this.jjCheckNAddTwoStates(2, 5);
                     }
                     break;
                  case 2:
                     if ((4294967808L & l) != 0L) {
                        this.jjCheckNAddTwoStates(2, 5);
                     }
                     break;
                  case 3:
                     if (this.curChar == '#') {
                        if (kind > 41) {
                           kind = 41;
                        }

                        this.jjCheckNAdd(4);
                     }
                     break;
                  case 4:
                     if ((-1026L & l) != 0L) {
                        if (kind > 41) {
                           kind = 41;
                        }

                        this.jjCheckNAdd(4);
                     }
                     break;
                  case 5:
                     if (this.curChar == '#') {
                        this.jjstateSet[this.jjnewStateCnt++] = 3;
                     }
                     break;
                  case 6:
                     if ((-1537L & l) != 0L && kind > 60) {
                        kind = 60;
                     }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 4:
                     if (kind > 41) {
                        kind = 41;
                     }

                     this.jjstateSet[this.jjnewStateCnt++] = 4;
               }
            } while(i != startsAt);
         } else {
            int hiByte = this.curChar >> 8;
            int i1 = hiByte >> 6;
            long l1 = 1L << (hiByte & 63);
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch (this.jjstateSet[i]) {
                  case 1:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 60) {
                        kind = 60;
                     }
                     break;
                  case 4:
                     if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                        if (kind > 41) {
                           kind = 41;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 4;
                     }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 7 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var13) {
            return curPos;
         }
      }
   }

   private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
      switch (hiByte) {
         case 0:
            return (jjbitVec2[i2] & l2) != 0L;
         default:
            return (jjbitVec0[i1] & l1) != 0L;
      }
   }

   private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
      switch (hiByte) {
         case 0:
            return (jjbitVec4[i2] & l2) != 0L;
         case 1:
            return (jjbitVec5[i2] & l2) != 0L;
         case 2:
            return (jjbitVec6[i2] & l2) != 0L;
         case 3:
            return (jjbitVec7[i2] & l2) != 0L;
         case 4:
            return (jjbitVec8[i2] & l2) != 0L;
         case 5:
            return (jjbitVec9[i2] & l2) != 0L;
         case 6:
            return (jjbitVec10[i2] & l2) != 0L;
         case 9:
            return (jjbitVec11[i2] & l2) != 0L;
         case 10:
            return (jjbitVec12[i2] & l2) != 0L;
         case 11:
            return (jjbitVec13[i2] & l2) != 0L;
         case 12:
            return (jjbitVec14[i2] & l2) != 0L;
         case 13:
            return (jjbitVec15[i2] & l2) != 0L;
         case 14:
            return (jjbitVec16[i2] & l2) != 0L;
         case 15:
            return (jjbitVec17[i2] & l2) != 0L;
         case 16:
            return (jjbitVec18[i2] & l2) != 0L;
         case 17:
            return (jjbitVec19[i2] & l2) != 0L;
         case 30:
            return (jjbitVec20[i2] & l2) != 0L;
         case 31:
            return (jjbitVec21[i2] & l2) != 0L;
         case 33:
            return (jjbitVec22[i2] & l2) != 0L;
         case 48:
            return (jjbitVec23[i2] & l2) != 0L;
         case 49:
            return (jjbitVec24[i2] & l2) != 0L;
         case 159:
            return (jjbitVec25[i2] & l2) != 0L;
         case 215:
            return (jjbitVec26[i2] & l2) != 0L;
         default:
            return (jjbitVec3[i1] & l1) != 0L;
      }
   }

   private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2) {
      switch (hiByte) {
         case 0:
            return (jjbitVec27[i2] & l2) != 0L;
         case 1:
            return (jjbitVec5[i2] & l2) != 0L;
         case 2:
            return (jjbitVec28[i2] & l2) != 0L;
         case 3:
            return (jjbitVec29[i2] & l2) != 0L;
         case 4:
            return (jjbitVec30[i2] & l2) != 0L;
         case 5:
            return (jjbitVec31[i2] & l2) != 0L;
         case 6:
            return (jjbitVec32[i2] & l2) != 0L;
         case 9:
            return (jjbitVec33[i2] & l2) != 0L;
         case 10:
            return (jjbitVec34[i2] & l2) != 0L;
         case 11:
            return (jjbitVec35[i2] & l2) != 0L;
         case 12:
            return (jjbitVec36[i2] & l2) != 0L;
         case 13:
            return (jjbitVec37[i2] & l2) != 0L;
         case 14:
            return (jjbitVec38[i2] & l2) != 0L;
         case 15:
            return (jjbitVec39[i2] & l2) != 0L;
         case 16:
            return (jjbitVec18[i2] & l2) != 0L;
         case 17:
            return (jjbitVec19[i2] & l2) != 0L;
         case 30:
            return (jjbitVec20[i2] & l2) != 0L;
         case 31:
            return (jjbitVec21[i2] & l2) != 0L;
         case 32:
            return (jjbitVec40[i2] & l2) != 0L;
         case 33:
            return (jjbitVec22[i2] & l2) != 0L;
         case 48:
            return (jjbitVec41[i2] & l2) != 0L;
         case 49:
            return (jjbitVec24[i2] & l2) != 0L;
         case 159:
            return (jjbitVec25[i2] & l2) != 0L;
         case 215:
            return (jjbitVec26[i2] & l2) != 0L;
         default:
            return (jjbitVec3[i1] & l1) != 0L;
      }
   }

   public CompactSyntaxTokenManager(JavaCharStream stream) {
      this.debugStream = System.out;
      this.jjrounds = new int[43];
      this.jjstateSet = new int[86];
      this.jjimage = new StringBuilder();
      this.image = this.jjimage;
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = stream;
   }

   public CompactSyntaxTokenManager(JavaCharStream stream, int lexState) {
      this(stream);
      this.SwitchTo(lexState);
   }

   public void ReInit(JavaCharStream stream) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = stream;
      this.ReInitRounds();
   }

   private void ReInitRounds() {
      this.jjround = -2147483647;

      for(int i = 43; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(JavaCharStream stream, int lexState) {
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void SwitchTo(int lexState) {
      if (lexState < 3 && lexState >= 0) {
         this.curLexState = lexState;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
      }
   }

   protected Token jjFillToken() {
      String im = jjstrLiteralImages[this.jjmatchedKind];
      String curTokenImage = im == null ? this.input_stream.GetImage() : im;
      int beginLine = this.input_stream.getBeginLine();
      int beginColumn = this.input_stream.getBeginColumn();
      int endLine = this.input_stream.getEndLine();
      int endColumn = this.input_stream.getEndColumn();
      Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
      t.beginLine = beginLine;
      t.endLine = endLine;
      t.beginColumn = beginColumn;
      t.endColumn = endColumn;
      return t;
   }

   public Token getNextToken() {
      Token specialToken = null;
      int curPos = 0;

      while(true) {
         Token matchedToken;
         try {
            this.curChar = this.input_stream.BeginToken();
         } catch (IOException var9) {
            this.jjmatchedKind = 0;
            matchedToken = this.jjFillToken();
            matchedToken.specialToken = specialToken;
            return matchedToken;
         }

         this.image = this.jjimage;
         this.image.setLength(0);
         this.jjimageLen = 0;
         switch (this.curLexState) {
            case 0:
               this.jjmatchedKind = Integer.MAX_VALUE;
               this.jjmatchedPos = 0;
               curPos = this.jjMoveStringLiteralDfa0_0();
               break;
            case 1:
               this.jjmatchedKind = Integer.MAX_VALUE;
               this.jjmatchedPos = 0;
               curPos = this.jjMoveStringLiteralDfa0_1();
               break;
            case 2:
               this.jjmatchedKind = Integer.MAX_VALUE;
               this.jjmatchedPos = 0;
               curPos = this.jjMoveStringLiteralDfa0_2();
         }

         if (this.jjmatchedKind == Integer.MAX_VALUE) {
            int error_line = this.input_stream.getEndLine();
            int error_column = this.input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
               this.input_stream.readChar();
               this.input_stream.backup(1);
            } catch (IOException var10) {
               EOFSeen = true;
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
               if (this.curChar != '\n' && this.curChar != '\r') {
                  ++error_column;
               } else {
                  ++error_line;
                  error_column = 0;
               }
            }

            if (!EOFSeen) {
               this.input_stream.backup(1);
               error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            }

            throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
         }

         if (this.jjmatchedPos + 1 < curPos) {
            this.input_stream.backup(curPos - this.jjmatchedPos - 1);
         }

         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            matchedToken.specialToken = specialToken;
            if (jjnewLexState[this.jjmatchedKind] != -1) {
               this.curLexState = jjnewLexState[this.jjmatchedKind];
            }

            return matchedToken;
         }

         if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
            matchedToken = this.jjFillToken();
            if (specialToken == null) {
               specialToken = matchedToken;
            } else {
               matchedToken.specialToken = specialToken;
               specialToken = specialToken.next = matchedToken;
            }

            this.SkipLexicalActions(matchedToken);
         } else {
            this.SkipLexicalActions((Token)null);
         }

         if (jjnewLexState[this.jjmatchedKind] != -1) {
            this.curLexState = jjnewLexState[this.jjmatchedKind];
         }
      }
   }

   void SkipLexicalActions(Token matchedToken) {
      switch (this.jjmatchedKind) {
         default:
      }
   }

   private void jjCheckNAdd(int state) {
      if (this.jjrounds[state] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = state;
         this.jjrounds[state] = this.jjround;
      }

   }

   private void jjAddStates(int start, int end) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
      } while(start++ != end);

   }

   private void jjCheckNAddTwoStates(int state1, int state2) {
      this.jjCheckNAdd(state1);
      this.jjCheckNAdd(state2);
   }

   private void jjCheckNAddStates(int start, int end) {
      do {
         this.jjCheckNAdd(jjnextStates[start]);
      } while(start++ != end);

   }
}
