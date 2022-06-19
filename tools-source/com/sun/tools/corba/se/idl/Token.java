package com.sun.tools.corba.se.idl;

class Token {
   static final int Any = 0;
   static final int Attribute = 1;
   static final int Boolean = 2;
   static final int Case = 3;
   static final int Char = 4;
   static final int Const = 5;
   static final int Context = 6;
   static final int Default = 7;
   static final int Double = 8;
   static final int Enum = 9;
   static final int Exception = 10;
   static final int FALSE = 11;
   static final int Fixed = 12;
   static final int Float = 13;
   static final int In = 14;
   static final int Inout = 15;
   static final int Interface = 16;
   static final int Long = 17;
   static final int Module = 18;
   static final int Native = 19;
   static final int Object = 20;
   static final int Octet = 21;
   static final int Oneway = 22;
   static final int Out = 23;
   static final int Raises = 24;
   static final int Readonly = 25;
   static final int Sequence = 26;
   static final int Short = 27;
   static final int String = 28;
   static final int Struct = 29;
   static final int Switch = 30;
   static final int TRUE = 31;
   static final int Typedef = 32;
   static final int Unsigned = 33;
   static final int Union = 34;
   static final int Void = 35;
   static final int Wchar = 36;
   static final int Wstring = 37;
   static final int Init = 38;
   static final int Abstract = 39;
   static final int Custom = 40;
   static final int Private = 41;
   static final int Public = 42;
   static final int Supports = 43;
   static final int Truncatable = 44;
   static final int ValueBase = 45;
   static final int Valuetype = 46;
   static final int Factory = 47;
   static final int Component = 48;
   static final int Consumes = 49;
   static final int Emits = 50;
   static final int Finder = 51;
   static final int GetRaises = 52;
   static final int Home = 53;
   static final int Import = 54;
   static final int Local = 55;
   static final int Manages = 56;
   static final int Multiple = 57;
   static final int PrimaryKey = 58;
   static final int Provides = 59;
   static final int Publishes = 60;
   static final int SetRaises = 61;
   static final int TypeId = 62;
   static final int TypePrefix = 63;
   static final int Uses = 64;
   static final int Identifier = 80;
   static final int MacroIdentifier = 81;
   static final int Semicolon = 100;
   static final int LeftBrace = 101;
   static final int RightBrace = 102;
   static final int Colon = 103;
   static final int Comma = 104;
   static final int Equal = 105;
   static final int Plus = 106;
   static final int Minus = 107;
   static final int LeftParen = 108;
   static final int RightParen = 109;
   static final int LessThan = 110;
   static final int GreaterThan = 111;
   static final int LeftBracket = 112;
   static final int RightBracket = 113;
   static final int Apostrophe = 114;
   static final int Quote = 115;
   static final int Backslash = 116;
   static final int Bar = 117;
   static final int Carat = 118;
   static final int Ampersand = 119;
   static final int Star = 120;
   static final int Slash = 121;
   static final int Percent = 122;
   static final int Tilde = 123;
   static final int DoubleColon = 124;
   static final int ShiftLeft = 125;
   static final int ShiftRight = 126;
   static final int Period = 127;
   static final int Hash = 128;
   static final int Exclamation = 129;
   static final int DoubleEqual = 130;
   static final int NotEqual = 131;
   static final int GreaterEqual = 132;
   static final int LessEqual = 133;
   static final int DoubleBar = 134;
   static final int DoubleAmpersand = 135;
   static final int BooleanLiteral = 200;
   static final int CharacterLiteral = 201;
   static final int IntegerLiteral = 202;
   static final int FloatingPointLiteral = 203;
   static final int StringLiteral = 204;
   static final int Literal = 205;
   static final int Define = 300;
   static final int Undef = 301;
   static final int If = 302;
   static final int Ifdef = 303;
   static final int Ifndef = 304;
   static final int Else = 305;
   static final int Elif = 306;
   static final int Include = 307;
   static final int Endif = 308;
   static final int Line = 309;
   static final int Error = 310;
   static final int Pragma = 311;
   static final int Null = 312;
   static final int Unknown = 313;
   static final int Defined = 400;
   static final int EOF = 999;
   static final String[] Keywords = new String[]{"any", "attribute", "boolean", "case", "char", "const", "context", "default", "double", "enum", "exception", "FALSE", "fixed", "float", "in", "inout", "interface", "long", "module", "native", "Object", "octet", "oneway", "out", "raises", "readonly", "sequence", "short", "string", "struct", "switch", "TRUE", "typedef", "unsigned", "union", "void", "wchar", "wstring", "init", "abstract", "custom", "private", "public", "supports", "truncatable", "ValueBase", "valuetype", "factory", "component", "consumes", "emits", "finder", "getRaises", "home", "import", "local", "manages", "multiple", "primaryKey", "provides", "publishes", "setRaises", "supports", "typeId", "typePrefix", "uses"};
   private static final int FirstKeyword = 0;
   private static final int LastKeyword = 64;
   private static final int First22Keyword = 0;
   private static final int Last22Keyword = 37;
   private static final int First23Keyword = 38;
   private static final int Last23Keyword = 46;
   private static final int First24rtfKeyword = 39;
   private static final int Last24rtfKeyword = 47;
   private static final int First30Keyword = 48;
   private static final int Last30Keyword = 64;
   private static final int CORBA_LEVEL_22 = 0;
   private static final int CORBA_LEVEL_23 = 1;
   private static final int CORBA_LEVEL_24RTF = 2;
   private static final int CORBA_LEVEL_30 = 3;
   static final int FirstSymbol = 100;
   static final int LastSymbol = 199;
   static final String[] Symbols = new String[]{";", "{", "}", ":", ",", "=", "+", "-", "(", ")", "<", ">", "[", "]", "'", "\"", "\\", "|", "^", "&", "*", "/", "%", "~", "::", "<<", ">>", ".", "#", "!", "==", "!=", ">=", "<=", "||", "&&"};
   static final int FirstLiteral = 200;
   static final int LastLiteral = 299;
   static final String[] Literals = new String[]{Util.getMessage("Token.boolLit"), Util.getMessage("Token.charLit"), Util.getMessage("Token.intLit"), Util.getMessage("Token.floatLit"), Util.getMessage("Token.stringLit"), Util.getMessage("Token.literal")};
   static final int FirstDirective = 300;
   static final int LastDirective = 399;
   static final String[] Directives = new String[]{"define", "undef", "if", "ifdef", "ifndef", "else", "elif", "include", "endif", "line", "error", "pragma", ""};
   static final int FirstSpecial = 400;
   static final int LastSpecial = 499;
   static final String[] Special = new String[]{"defined"};
   int type;
   String name;
   Comment comment;
   boolean isEscaped;
   boolean collidesWithKeyword;
   boolean isDeprecated;
   boolean isWide;

   boolean isKeyword() {
      return this.type >= 0 && this.type <= 64;
   }

   private static int getLevel(float var0) {
      if (var0 < 2.3F) {
         return 0;
      } else if (Util.absDelta(var0, 2.3F) < 0.001F) {
         return 1;
      } else {
         return var0 < 3.0F ? 2 : 3;
      }
   }

   private static int getLastKeyword(int var0) {
      if (var0 == 0) {
         return 37;
      } else if (var0 == 1) {
         return 46;
      } else {
         return var0 == 2 ? 47 : 64;
      }
   }

   public static Token makeKeywordToken(String var0, float var1, boolean var2, boolean[] var3) {
      int var4 = getLevel(var1);
      int var5 = getLastKeyword(var4);
      boolean var6 = false;
      var3[0] = false;

      for(int var7 = 0; var7 <= 64; ++var7) {
         if (var0.equals(Keywords[var7])) {
            if (var7 == 38) {
               if (var4 != 1) {
                  break;
               }

               var6 = true;
            }

            if (var7 <= var5) {
               if (!var0.equals("TRUE") && !var0.equals("FALSE")) {
                  return new Token(var7, var6);
               }

               return new Token(200, var0);
            }

            var3[0] |= var2;
            break;
         }

         if (var0.equalsIgnoreCase(Keywords[var7])) {
            var3[0] |= true;
            break;
         }
      }

      return null;
   }

   boolean isDirective() {
      return this.type >= 300 && this.type <= 399;
   }

   Token(int var1) {
      this.name = null;
      this.comment = null;
      this.isEscaped = false;
      this.collidesWithKeyword = false;
      this.isDeprecated = false;
      this.isWide = false;
      this.type = var1;
   }

   Token(int var1, boolean var2) {
      this.name = null;
      this.comment = null;
      this.isEscaped = false;
      this.collidesWithKeyword = false;
      this.isDeprecated = false;
      this.isWide = false;
      this.type = var1;
      this.isDeprecated = var2;
   }

   Token(int var1, String var2) {
      this.name = null;
      this.comment = null;
      this.isEscaped = false;
      this.collidesWithKeyword = false;
      this.isDeprecated = false;
      this.isWide = false;
      this.type = var1;
      this.name = var2;
   }

   Token(int var1, String var2, boolean var3) {
      this(var1, var2);
      this.isWide = var3;
   }

   Token(int var1, String var2, boolean var3, boolean var4, boolean var5) {
      this(var1, var2);
      this.isEscaped = var3;
      this.collidesWithKeyword = var4;
      this.isDeprecated = var5;
   }

   public String toString() {
      if (this.type == 80) {
         return this.name;
      } else {
         return this.type == 81 ? this.name + '(' : toString(this.type);
      }
   }

   static String toString(int var0) {
      if (var0 <= 64) {
         return Keywords[var0];
      } else if (var0 != 80 && var0 != 81) {
         if (var0 <= 199) {
            return Symbols[var0 - 100];
         } else if (var0 <= 299) {
            return Literals[var0 - 200];
         } else if (var0 <= 399) {
            return Directives[var0 - 300];
         } else if (var0 <= 499) {
            return Special[var0 - 400];
         } else {
            return var0 == 999 ? Util.getMessage("Token.endOfFile") : Util.getMessage("Token.unknown");
         }
      } else {
         return Util.getMessage("Token.identifier");
      }
   }

   boolean equals(Token var1) {
      if (this.type == var1.type) {
         if (this.name == null) {
            return var1.name == null;
         } else {
            return this.name.equals(var1.name);
         }
      } else {
         return false;
      }
   }

   boolean equals(int var1) {
      return this.type == var1;
   }

   boolean equals(String var1) {
      return this.type == 80 && this.name.equals(var1);
   }

   public boolean isEscaped() {
      return this.type == 80 && this.isEscaped;
   }

   public boolean collidesWithKeyword() {
      return this.collidesWithKeyword;
   }

   public boolean isDeprecated() {
      return this.isDeprecated;
   }

   public boolean isWide() {
      return this.isWide;
   }
}
