package com.sun.tools.corba.se.idl;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

class Scanner {
   static final int Star = 0;
   static final int Plus = 1;
   static final int Dot = 2;
   static final int None = 3;
   private int BOL;
   private ScannerData data = new ScannerData();
   private Stack dataStack = new Stack();
   private Vector keywords = new Vector();
   private Vector openEndedKeywords = new Vector();
   private Vector wildcardKeywords = new Vector();
   private boolean verbose;
   boolean escapedOK = true;
   private boolean emitAll;
   private float corbaLevel;
   private boolean debug;

   Scanner(IncludeEntry var1, String[] var2, boolean var3, boolean var4, float var5, boolean var6) throws IOException {
      this.readFile(var1);
      this.verbose = var3;
      this.emitAll = var4;
      this.sortKeywords(var2);
      this.corbaLevel = var5;
      this.debug = var6;
   }

   void sortKeywords(String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (this.wildcardAtEitherEnd(var1[var2])) {
            this.openEndedKeywords.addElement(var1[var2]);
         } else if (this.wildcardsInside(var1[var2])) {
            this.wildcardKeywords.addElement(var1[var2]);
         } else {
            this.keywords.addElement(var1[var2]);
         }
      }

   }

   private boolean wildcardAtEitherEnd(String var1) {
      return var1.startsWith("*") || var1.startsWith("+") || var1.startsWith(".") || var1.endsWith("*") || var1.endsWith("+") || var1.endsWith(".");
   }

   private boolean wildcardsInside(String var1) {
      return var1.indexOf("*") > 0 || var1.indexOf("+") > 0 || var1.indexOf(".") > 0;
   }

   void readFile(IncludeEntry var1) throws IOException {
      String var2 = var1.name();
      var2 = var2.substring(1, var2.length() - 1);
      this.readFile(var1, var2);
   }

   void readFile(IncludeEntry var1, String var2) throws IOException {
      this.data.fileEntry = var1;
      this.data.filename = var2;
      File var3 = new File(this.data.filename);
      int var4 = (int)var3.length();
      FileReader var5 = new FileReader(var3);
      String var6 = System.getProperty("line.separator");
      this.data.fileBytes = new char[var4 + var6.length()];
      var5.read(this.data.fileBytes, 0, var4);
      var5.close();

      for(int var7 = 0; var7 < var6.length(); ++var7) {
         this.data.fileBytes[var4 + var7] = var6.charAt(var7);
      }

      this.readChar();
   }

   Token getToken() throws IOException {
      Token var1 = null;
      String var2 = new String("");

      while(var1 == null) {
         try {
            this.data.oldIndex = this.data.fileIndex;
            this.data.oldLine = this.data.line;
            if (this.data.ch <= ' ') {
               this.skipWhiteSpace();
            } else {
               if (this.data.ch == 'L') {
                  this.readChar();
                  if (this.data.ch == '\'') {
                     var1 = this.getCharacterToken(true);
                     this.readChar();
                     continue;
                  }

                  if (this.data.ch == '"') {
                     this.readChar();
                     var1 = new Token(204, this.getUntil('"'), true);
                     this.readChar();
                     continue;
                  }

                  this.unread(this.data.ch);
                  this.unread('L');
                  this.readChar();
               }

               if ((this.data.ch < 'a' || this.data.ch > 'z') && (this.data.ch < 'A' || this.data.ch > 'Z') && this.data.ch != '_' && !Character.isLetter(this.data.ch)) {
                  if ((this.data.ch < '0' || this.data.ch > '9') && this.data.ch != '.') {
                     switch (this.data.ch) {
                        case '!':
                           this.readChar();
                           if (this.data.ch == '=') {
                              var1 = new Token(131);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(129);
                           }
                           break;
                        case '"':
                           this.readChar();
                           var1 = new Token(204, this.getUntil('"', false, false, false));
                           break;
                        case '#':
                           var1 = this.getDirective();
                           break;
                        case '$':
                        case '.':
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
                        case '_':
                        case '`':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'g':
                        case 'h':
                        case 'i':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'p':
                        case 'q':
                        case 'r':
                        case 's':
                        case 't':
                        case 'u':
                        case 'v':
                        case 'w':
                        case 'x':
                        case 'y':
                        case 'z':
                        default:
                           throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
                        case '%':
                           var1 = new Token(122);
                           break;
                        case '&':
                           this.readChar();
                           if (this.data.ch == '&') {
                              var1 = new Token(135);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(119);
                           }
                           break;
                        case '\'':
                           var1 = this.getCharacterToken(false);
                           break;
                        case '(':
                           var1 = new Token(108);
                           break;
                        case ')':
                           var1 = new Token(109);
                           break;
                        case '*':
                           var1 = new Token(120);
                           break;
                        case '+':
                           var1 = new Token(106);
                           break;
                        case ',':
                           var1 = new Token(104);
                           break;
                        case '-':
                           var1 = new Token(107);
                           break;
                        case '/':
                           this.readChar();
                           if (this.data.ch == '/') {
                              var2 = this.getLineComment();
                           } else if (this.data.ch == '*') {
                              var2 = this.getBlockComment();
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(121);
                           }
                           break;
                        case ':':
                           this.readChar();
                           if (this.data.ch == ':') {
                              var1 = new Token(124);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(103);
                           }
                           break;
                        case ';':
                           var1 = new Token(100);
                           break;
                        case '<':
                           this.readChar();
                           if (this.data.ch == '<') {
                              var1 = new Token(125);
                           } else if (this.data.ch == '=') {
                              var1 = new Token(133);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(110);
                           }
                           break;
                        case '=':
                           this.readChar();
                           if (this.data.ch == '=') {
                              var1 = new Token(130);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(105);
                           }
                           break;
                        case '>':
                           this.readChar();
                           if (this.data.ch == '>') {
                              var1 = new Token(126);
                           } else if (this.data.ch == '=') {
                              var1 = new Token(132);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(111);
                           }
                           break;
                        case '?':
                           try {
                              var1 = this.replaceTrigraph();
                              break;
                           } catch (InvalidCharacter var4) {
                              throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
                           }
                        case '[':
                           var1 = new Token(112);
                           break;
                        case '\\':
                           this.readChar();
                           if (this.data.ch != '\n' && this.data.ch != '\r') {
                              var1 = new Token(116);
                           } else {
                              var1 = null;
                           }
                           break;
                        case ']':
                           var1 = new Token(113);
                           break;
                        case '^':
                           var1 = new Token(118);
                           break;
                        case '{':
                           var1 = new Token(101);
                           break;
                        case '|':
                           this.readChar();
                           if (this.data.ch == '|') {
                              var1 = new Token(134);
                           } else {
                              this.unread(this.data.ch);
                              var1 = new Token(117);
                           }
                           break;
                        case '}':
                           var1 = new Token(102);
                           break;
                        case '~':
                           var1 = new Token(123);
                     }

                     this.readChar();
                  } else {
                     var1 = this.getNumber();
                  }
               } else {
                  var1 = this.getString();
               }
            }
         } catch (EOFException var5) {
            var1 = new Token(999);
         }
      }

      var1.comment = new Comment(var2);
      if (this.debug) {
         System.out.println("Token: " + var1);
      }

      return var1;
   }

   void scanString(String var1) {
      this.dataStack.push(this.data);
      this.data = new ScannerData(this.data);
      this.data.fileIndex = 0;
      this.data.oldIndex = 0;
      int var2 = var1.length();
      this.data.fileBytes = new char[var2];
      var1.getChars(0, var2, this.data.fileBytes, 0);
      this.data.macrodata = true;

      try {
         this.readChar();
      } catch (IOException var4) {
      }

   }

   void scanIncludedFile(IncludeEntry var1, String var2, boolean var3) throws IOException {
      this.dataStack.push(this.data);
      this.data = new ScannerData();
      this.data.indent = ((ScannerData)this.dataStack.peek()).indent + ' ';
      this.data.includeIsImport = var3;

      try {
         this.readFile(var1, var2);
         if (!this.emitAll && var3) {
            SymtabEntry.enteringInclude();
         }

         Parser.enteringInclude();
         if (this.verbose) {
            System.out.println(this.data.indent + Util.getMessage("Compile.parsing", var2));
         }

      } catch (IOException var5) {
         this.data = (ScannerData)this.dataStack.pop();
         throw var5;
      }
   }

   private void unread(char var1) {
      if (var1 == '\n' && !this.data.macrodata) {
         --this.data.line;
      }

      --this.data.fileIndex;
   }

   void readChar() throws IOException {
      if (this.data.fileIndex >= this.data.fileBytes.length) {
         if (this.dataStack.empty()) {
            throw new EOFException();
         }

         if (!this.data.macrodata) {
            if (!this.emitAll && this.data.includeIsImport) {
               SymtabEntry.exitingInclude();
            }

            Parser.exitingInclude();
         }

         if (this.verbose && !this.data.macrodata) {
            System.out.println(this.data.indent + Util.getMessage("Compile.parseDone", this.data.filename));
         }

         this.data = (ScannerData)this.dataStack.pop();
      } else {
         this.data.ch = (char)(this.data.fileBytes[this.data.fileIndex++] & 255);
         if (this.data.ch == '\n' && !this.data.macrodata) {
            ++this.data.line;
         }
      }

   }

   private String getWString() throws IOException {
      this.readChar();
      StringBuffer var1 = new StringBuffer();

      while(true) {
         while(this.data.ch != '"') {
            if (this.data.ch == '\\') {
               this.readChar();
               if (this.data.ch == 'u') {
                  int var2 = this.getNDigitHexNumber(4);
                  System.out.println("Got num: " + var2);
                  System.out.println("Which is: " + (char)var2);
                  var1.append((char)var2);
                  continue;
               }

               if (this.data.ch >= '0' && this.data.ch <= '7') {
                  var1.append((char)this.get3DigitOctalNumber());
                  continue;
               }

               var1.append('\\');
               var1.append(this.data.ch);
            } else {
               var1.append(this.data.ch);
            }

            this.readChar();
         }

         return var1.toString();
      }
   }

   private Token getCharacterToken(boolean var1) throws IOException {
      Token var2 = null;
      this.readChar();
      if (this.data.ch == '\\') {
         this.readChar();
         int var3;
         if (this.data.ch != 'x' && this.data.ch != 'u') {
            if (this.data.ch >= '0' && this.data.ch <= '7') {
               var3 = this.get3DigitOctalNumber();
               return new Token(201, (char)var3 + "\\" + Integer.toString(var3, 8), var1);
            } else {
               return this.singleCharEscapeSequence(var1);
            }
         } else {
            var3 = this.data.ch;
            int var4 = this.getNDigitHexNumber(var3 == 120 ? 2 : 4);
            return new Token(201, (char)var4 + "\\" + var3 + Integer.toString(var4, 16), var1);
         }
      } else {
         var2 = new Token(201, "" + this.data.ch + this.data.ch, var1);
         this.readChar();
         return var2;
      }
   }

   private Token singleCharEscapeSequence(boolean var1) throws IOException {
      Token var2;
      if (this.data.ch == 'n') {
         var2 = new Token(201, "\n\\n", var1);
      } else if (this.data.ch == 't') {
         var2 = new Token(201, "\t\\t", var1);
      } else if (this.data.ch == 'v') {
         var2 = new Token(201, "\u000b\\v", var1);
      } else if (this.data.ch == 'b') {
         var2 = new Token(201, "\b\\b", var1);
      } else if (this.data.ch == 'r') {
         var2 = new Token(201, "\r\\r", var1);
      } else if (this.data.ch == 'f') {
         var2 = new Token(201, "\f\\f", var1);
      } else if (this.data.ch == 'a') {
         var2 = new Token(201, "\u0007\\a", var1);
      } else if (this.data.ch == '\\') {
         var2 = new Token(201, "\\\\\\", var1);
      } else if (this.data.ch == '?') {
         var2 = new Token(201, "?\\?", var1);
      } else if (this.data.ch == '\'') {
         var2 = new Token(201, "'\\'", var1);
      } else {
         if (this.data.ch != '"') {
            throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
         }

         var2 = new Token(201, "\"\\\"", var1);
      }

      this.readChar();
      return var2;
   }

   private Token getString() throws IOException {
      StringBuffer var1 = new StringBuffer();
      boolean var2 = false;
      boolean[] var3 = new boolean[]{false};
      if (this.data.ch == '_') {
         var1.append(this.data.ch);
         this.readChar();
         if ((var2 = this.escapedOK) && this.data.ch == '_') {
            throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
         }
      }

      while(Character.isLetterOrDigit(this.data.ch) || this.data.ch == '_') {
         var1.append(this.data.ch);
         this.readChar();
      }

      String var4 = var1.toString();
      if (!var2) {
         Token var5 = Token.makeKeywordToken(var4, this.corbaLevel, this.escapedOK, var3);
         if (var5 != null) {
            return var5;
         }
      }

      var4 = this.getIdentifier(var4);
      if (this.data.ch == '(') {
         this.readChar();
         return new Token(81, var4, var2, var3[0], false);
      } else {
         return new Token(80, var4, var2, var3[0], false);
      }
   }

   private boolean matchesClosedWildKeyword(String var1) {
      boolean var2 = true;
      String var3 = var1;
      Enumeration var4 = this.wildcardKeywords.elements();

      while(var4.hasMoreElements()) {
         byte var5 = 3;
         StringTokenizer var6 = new StringTokenizer((String)var4.nextElement(), "*+.", true);
         if (var6.hasMoreTokens()) {
            String var7 = var6.nextToken();
            if (var3.startsWith(var7)) {
               var3 = var3.substring(var7.length());

               while(var6.hasMoreTokens() && var2) {
                  var7 = var6.nextToken();
                  if (var7.equals("*")) {
                     var5 = 0;
                  } else if (var7.equals("+")) {
                     var5 = 1;
                  } else if (var7.equals(".")) {
                     var5 = 2;
                  } else {
                     int var8;
                     if (var5 == 0) {
                        var8 = var3.indexOf(var7);
                        if (var8 >= 0) {
                           var3 = var3.substring(var8 + var7.length());
                        } else {
                           var2 = false;
                        }
                     } else if (var5 == 1) {
                        var8 = var3.indexOf(var7);
                        if (var8 > 0) {
                           var3 = var3.substring(var8 + var7.length());
                        } else {
                           var2 = false;
                        }
                     } else if (var5 == 2) {
                        var8 = var3.indexOf(var7);
                        if (var8 == 1) {
                           var3 = var3.substring(1 + var7.length());
                        } else {
                           var2 = false;
                        }
                     }
                  }
               }

               if (var2 && var3.equals("")) {
                  break;
               }
            }
         }
      }

      return var2 && var3.equals("");
   }

   private String matchesOpenWildcard(String var1) {
      Enumeration var2 = this.openEndedKeywords.elements();
      String var3 = "";

      while(var2.hasMoreElements()) {
         byte var4 = 3;
         boolean var5 = true;
         String var6 = var1;
         StringTokenizer var7 = new StringTokenizer((String)var2.nextElement(), "*+.", true);

         while(var7.hasMoreTokens() && var5) {
            String var8 = var7.nextToken();
            if (var8.equals("*")) {
               var4 = 0;
            } else if (var8.equals("+")) {
               var4 = 1;
            } else if (var8.equals(".")) {
               var4 = 2;
            } else {
               int var9;
               if (var4 == 0) {
                  var4 = 3;
                  var9 = var6.lastIndexOf(var8);
                  if (var9 >= 0) {
                     var6 = this.blankOutMatch(var6, var9, var8.length());
                  } else {
                     var5 = false;
                  }
               } else if (var4 == 1) {
                  var4 = 3;
                  var9 = var6.lastIndexOf(var8);
                  if (var9 > 0) {
                     var6 = this.blankOutMatch(var6, var9, var8.length());
                  } else {
                     var5 = false;
                  }
               } else if (var4 == 2) {
                  var4 = 3;
                  var9 = var6.lastIndexOf(var8);
                  if (var9 == 1) {
                     var6 = this.blankOutMatch(var6, 1, var8.length());
                  } else {
                     var5 = false;
                  }
               } else if (var4 == 3) {
                  if (var6.startsWith(var8)) {
                     var6 = this.blankOutMatch(var6, 0, var8.length());
                  } else {
                     var5 = false;
                  }
               }
            }
         }

         if (var5 && var4 != 0 && (var4 != 1 || var6.lastIndexOf(32) == var6.length() - 1) && (var4 != 2 || var6.lastIndexOf(32) != var6.length() - 2) && (var4 != 3 || var6.lastIndexOf(32) != var6.length() - 1)) {
            var5 = false;
         }

         if (var5) {
            var3 = var3 + "_" + this.matchesOpenWildcard(var6.trim());
            break;
         }
      }

      return var3;
   }

   private String blankOutMatch(String var1, int var2, int var3) {
      char[] var4 = new char[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = ' ';
      }

      return var1.substring(0, var2) + new String(var4) + var1.substring(var2 + var3);
   }

   private String getIdentifier(String var1) {
      if (this.keywords.contains(var1)) {
         var1 = '_' + var1;
      } else {
         String var2 = "";
         if (this.matchesClosedWildKeyword(var1)) {
            var2 = "_";
         } else {
            var2 = this.matchesOpenWildcard(var1);
         }

         var1 = var2 + var1;
      }

      return var1;
   }

   private Token getDirective() throws IOException {
      this.readChar();
      String var1 = new String();

      while(this.data.ch >= 'a' && this.data.ch <= 'z' || this.data.ch >= 'A' && this.data.ch <= 'Z') {
         var1 = var1 + this.data.ch;
         this.readChar();
      }

      this.unread(this.data.ch);

      for(int var2 = 0; var2 < Token.Directives.length; ++var2) {
         if (var1.equals(Token.Directives[var2])) {
            return new Token(300 + var2);
         }
      }

      return new Token(313, var1);
   }

   private Token getNumber() throws IOException {
      if (this.data.ch == '.') {
         return this.getFractionNoInteger();
      } else {
         return this.data.ch == '0' ? this.isItHex() : this.getInteger();
      }
   }

   private Token getFractionNoInteger() throws IOException {
      this.readChar();
      return this.data.ch >= '0' && this.data.ch <= '9' ? this.getFraction(".") : new Token(127);
   }

   private Token getFraction(String var1) throws IOException {
      while(this.data.ch >= '0' && this.data.ch <= '9') {
         var1 = var1 + this.data.ch;
         this.readChar();
      }

      if (this.data.ch != 'e' && this.data.ch != 'E') {
         return new Token(203, var1);
      } else {
         return this.getExponent(var1 + 'E');
      }
   }

   private Token getExponent(String var1) throws IOException {
      this.readChar();
      if (this.data.ch != '+' && this.data.ch != '-') {
         if (this.data.ch < '0' || this.data.ch > '9') {
            throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
         }
      } else {
         var1 = var1 + this.data.ch;
         this.readChar();
      }

      while(this.data.ch >= '0' && this.data.ch <= '9') {
         var1 = var1 + this.data.ch;
         this.readChar();
      }

      return new Token(203, var1);
   }

   private Token isItHex() throws IOException {
      this.readChar();
      if (this.data.ch == '.') {
         this.readChar();
         return this.getFraction("0.");
      } else if (this.data.ch != 'x' && this.data.ch != 'X') {
         if (this.data.ch != '8' && this.data.ch != '9') {
            if (this.data.ch >= '0' && this.data.ch <= '7') {
               return this.getOctalNumber();
            } else {
               return this.data.ch != 'e' && this.data.ch != 'E' ? new Token(202, "0") : this.getExponent("0E");
            }
         } else {
            throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
         }
      } else {
         return this.getHexNumber("0x");
      }
   }

   private Token getOctalNumber() throws IOException {
      String var1 = "0" + this.data.ch;
      this.readChar();

      while(true) {
         if (this.data.ch >= '0' && this.data.ch <= '9') {
            if (this.data.ch != '8' && this.data.ch != '9') {
               var1 = var1 + this.data.ch;
               this.readChar();
               continue;
            }

            throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
         }

         return new Token(202, var1);
      }
   }

   private Token getHexNumber(String var1) throws IOException {
      this.readChar();
      if ((this.data.ch < '0' || this.data.ch > '9') && (this.data.ch < 'a' || this.data.ch > 'f') && (this.data.ch < 'A' || this.data.ch > 'F')) {
         throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
      } else {
         while(this.data.ch >= '0' && this.data.ch <= '9' || this.data.ch >= 'a' && this.data.ch <= 'f' || this.data.ch >= 'A' && this.data.ch <= 'F') {
            var1 = var1 + this.data.ch;
            this.readChar();
         }

         return new Token(202, var1);
      }
   }

   private int getNDigitHexNumber(int var1) throws IOException {
      this.readChar();
      if (!this.isHexChar(this.data.ch)) {
         throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
      } else {
         String var2 = "" + this.data.ch;
         this.readChar();

         for(int var3 = 2; var3 <= var1 && this.isHexChar(this.data.ch); ++var3) {
            var2 = var2 + this.data.ch;
            this.readChar();
         }

         try {
            return Integer.parseInt(var2, 16);
         } catch (NumberFormatException var4) {
            return 0;
         }
      }
   }

   private boolean isHexChar(char var1) {
      return this.data.ch >= '0' && this.data.ch <= '9' || this.data.ch >= 'a' && this.data.ch <= 'f' || this.data.ch >= 'A' && this.data.ch <= 'F';
   }

   private int get3DigitOctalNumber() throws IOException {
      char var1 = this.data.ch;
      String var2 = "" + this.data.ch;
      this.readChar();
      if (this.data.ch >= '0' && this.data.ch <= '7') {
         var2 = var2 + this.data.ch;
         this.readChar();
         if (this.data.ch >= '0' && this.data.ch <= '7') {
            var2 = var2 + this.data.ch;
            if (var1 > '3') {
               throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), var1);
            }

            this.readChar();
         }
      }

      boolean var3 = false;

      try {
         int var6 = Integer.parseInt(var2, 8);
         return var6;
      } catch (NumberFormatException var5) {
         throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), var2.charAt(0));
      }
   }

   private Token getInteger() throws IOException {
      String var1 = "" + this.data.ch;
      this.readChar();
      if (this.data.ch == '.') {
         this.readChar();
         return this.getFraction(var1 + '.');
      } else if (this.data.ch != 'e' && this.data.ch != 'E') {
         if (this.data.ch >= '0' && this.data.ch <= '9') {
            while(this.data.ch >= '0' && this.data.ch <= '9') {
               var1 = var1 + this.data.ch;
               this.readChar();
               if (this.data.ch == '.') {
                  this.readChar();
                  return this.getFraction(var1 + '.');
               }
            }
         }

         return new Token(202, var1);
      } else {
         return this.getExponent(var1 + 'E');
      }
   }

   private Token replaceTrigraph() throws IOException {
      this.readChar();
      if (this.data.ch == '?') {
         this.readChar();
         if (this.data.ch == '=') {
            this.data.ch = '#';
         } else if (this.data.ch == '/') {
            this.data.ch = '\\';
         } else if (this.data.ch == '\'') {
            this.data.ch = '^';
         } else if (this.data.ch == '(') {
            this.data.ch = '[';
         } else if (this.data.ch == ')') {
            this.data.ch = ']';
         } else if (this.data.ch == '!') {
            this.data.ch = '|';
         } else if (this.data.ch == '<') {
            this.data.ch = '{';
         } else if (this.data.ch == '>') {
            this.data.ch = '}';
         } else {
            if (this.data.ch != '-') {
               this.unread(this.data.ch);
               this.unread('?');
               throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
            }

            this.data.ch = '~';
         }

         return this.getToken();
      } else {
         this.unread('?');
         throw new InvalidCharacter(this.data.filename, this.currentLine(), this.currentLineNumber(), this.currentLinePosition(), this.data.ch);
      }
   }

   void skipWhiteSpace() throws IOException {
      while(this.data.ch <= ' ') {
         this.readChar();
      }

   }

   private void skipBlockComment() throws IOException {
      try {
         boolean var1 = false;
         this.readChar();

         while(!var1) {
            while(this.data.ch != '*') {
               this.readChar();
            }

            this.readChar();
            if (this.data.ch == '/') {
               var1 = true;
            }
         }

      } catch (EOFException var2) {
         ParseException.unclosedComment(this.data.filename);
         throw var2;
      }
   }

   void skipLineComment() throws IOException {
      while(this.data.ch != '\n') {
         this.readChar();
      }

   }

   private String getLineComment() throws IOException {
      StringBuffer var1;
      for(var1 = new StringBuffer("/"); this.data.ch != '\n'; this.readChar()) {
         if (this.data.ch != '\r') {
            var1.append(this.data.ch);
         }
      }

      return var1.toString();
   }

   private String getBlockComment() throws IOException {
      StringBuffer var1 = new StringBuffer("/*");

      try {
         boolean var2 = false;
         this.readChar();
         var1.append(this.data.ch);

         while(!var2) {
            while(this.data.ch != '*') {
               this.readChar();
               var1.append(this.data.ch);
            }

            this.readChar();
            var1.append(this.data.ch);
            if (this.data.ch == '/') {
               var2 = true;
            }
         }
      } catch (EOFException var3) {
         ParseException.unclosedComment(this.data.filename);
         throw var3;
      }

      return var1.toString();
   }

   Token skipUntil(char var1) throws IOException {
      while(true) {
         if (this.data.ch != var1) {
            if (this.data.ch != '/') {
               this.readChar();
               continue;
            }

            this.readChar();
            if (this.data.ch != '/') {
               if (this.data.ch == '*') {
                  this.skipBlockComment();
               }
               continue;
            }

            this.skipLineComment();
            if (var1 != '\n') {
               continue;
            }
         }

         return this.getToken();
      }
   }

   String getUntil(char var1) throws IOException {
      return this.getUntil(var1, true, true, true);
   }

   String getUntil(char var1, boolean var2, boolean var3, boolean var4) throws IOException {
      String var5;
      for(var5 = ""; this.data.ch != var1; var5 = this.appendToString(var5, var2, var3, var4)) {
      }

      return var5;
   }

   String getUntil(char var1, char var2) throws IOException {
      String var3;
      for(var3 = ""; this.data.ch != var1 && this.data.ch != var2; var3 = this.appendToString(var3, false, false, false)) {
      }

      return var3;
   }

   private String appendToString(String var1, boolean var2, boolean var3, boolean var4) throws IOException {
      if (var4 && this.data.ch == '/') {
         this.readChar();
         if (this.data.ch == '/') {
            this.skipLineComment();
         } else if (this.data.ch == '*') {
            this.skipBlockComment();
         } else {
            var1 = var1 + '/';
         }
      } else if (this.data.ch == '\\') {
         this.readChar();
         if (this.data.ch == '\n') {
            this.readChar();
         } else if (this.data.ch == '\r') {
            this.readChar();
            if (this.data.ch == '\n') {
               this.readChar();
            }
         } else {
            var1 = var1 + '\\' + this.data.ch;
            this.readChar();
         }
      } else {
         if (var3 && this.data.ch == '"') {
            this.readChar();

            for(var1 = var1 + '"'; this.data.ch != '"'; var1 = this.appendToString(var1, true, false, var4)) {
            }
         } else if (var2 && var3 && this.data.ch == '(') {
            this.readChar();

            for(var1 = var1 + '('; this.data.ch != ')'; var1 = this.appendToString(var1, false, false, var4)) {
            }
         } else if (var2 && this.data.ch == '\'') {
            this.readChar();

            for(var1 = var1 + "'"; this.data.ch != '\''; var1 = this.appendToString(var1, false, true, var4)) {
            }
         }

         var1 = var1 + this.data.ch;
         this.readChar();
      }

      return var1;
   }

   String getStringToEOL() throws IOException {
      String var1 = new String();

      while(this.data.ch != '\n') {
         if (this.data.ch == '\\') {
            this.readChar();
            if (this.data.ch == '\n') {
               this.readChar();
            } else if (this.data.ch == '\r') {
               this.readChar();
               if (this.data.ch == '\n') {
                  this.readChar();
               }
            } else {
               var1 = var1 + this.data.ch;
               this.readChar();
            }
         } else {
            var1 = var1 + this.data.ch;
            this.readChar();
         }
      }

      return var1;
   }

   String filename() {
      return this.data.filename;
   }

   IncludeEntry fileEntry() {
      return this.data.fileEntry;
   }

   int currentLineNumber() {
      return this.data.line;
   }

   int lastTokenLineNumber() {
      return this.data.oldLine;
   }

   String currentLine() {
      this.BOL = this.data.fileIndex - 1;

      try {
         if (this.data.fileBytes[this.BOL - 1] == '\r' && this.data.fileBytes[this.BOL] == '\n') {
            this.BOL -= 2;
         } else if (this.data.fileBytes[this.BOL] == '\n') {
            --this.BOL;
         }

         while(this.data.fileBytes[this.BOL] != '\n') {
            --this.BOL;
         }
      } catch (ArrayIndexOutOfBoundsException var4) {
         this.BOL = -1;
      }

      ++this.BOL;
      int var1 = this.data.fileIndex - 1;

      try {
         while(this.data.fileBytes[var1] != '\n' && this.data.fileBytes[var1] != '\r') {
            ++var1;
         }
      } catch (ArrayIndexOutOfBoundsException var3) {
         var1 = this.data.fileBytes.length;
      }

      return this.BOL < var1 ? new String(this.data.fileBytes, this.BOL, var1 - this.BOL) : "";
   }

   String lastTokenLine() {
      int var1 = this.data.fileIndex;
      this.data.fileIndex = this.data.oldIndex;
      String var2 = this.currentLine();
      this.data.fileIndex = var1;
      return var2;
   }

   int currentLinePosition() {
      return this.data.fileIndex - this.BOL;
   }

   int lastTokenLinePosition() {
      return this.data.oldIndex - this.BOL;
   }
}
