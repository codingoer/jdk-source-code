package com.sun.tools.javac.parser;

import com.sun.tools.javac.api.Formattable;
import com.sun.tools.javac.api.Messages;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.Iterator;
import java.util.Locale;

public class Tokens {
   private final Names names;
   private final TokenKind[] key;
   private int maxKey = 0;
   private Name[] tokenName = new Name[Tokens.TokenKind.values().length];
   public static final Context.Key tokensKey = new Context.Key();
   public static final Token DUMMY;

   public static Tokens instance(Context var0) {
      Tokens var1 = (Tokens)var0.get(tokensKey);
      if (var1 == null) {
         var1 = new Tokens(var0);
      }

      return var1;
   }

   protected Tokens(Context var1) {
      var1.put((Context.Key)tokensKey, (Object)this);
      this.names = Names.instance(var1);
      TokenKind[] var2 = Tokens.TokenKind.values();
      int var3 = var2.length;

      int var4;
      TokenKind var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5.name != null) {
            this.enterKeyword(var5.name, var5);
         } else {
            this.tokenName[var5.ordinal()] = null;
         }
      }

      this.key = new TokenKind[this.maxKey + 1];

      for(int var6 = 0; var6 <= this.maxKey; ++var6) {
         this.key[var6] = Tokens.TokenKind.IDENTIFIER;
      }

      var2 = Tokens.TokenKind.values();
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5.name != null) {
            this.key[this.tokenName[var5.ordinal()].getIndex()] = var5;
         }
      }

   }

   private void enterKeyword(String var1, TokenKind var2) {
      Name var3 = this.names.fromString(var1);
      this.tokenName[var2.ordinal()] = var3;
      if (var3.getIndex() > this.maxKey) {
         this.maxKey = var3.getIndex();
      }

   }

   TokenKind lookupKind(Name var1) {
      return var1.getIndex() > this.maxKey ? Tokens.TokenKind.IDENTIFIER : this.key[var1.getIndex()];
   }

   TokenKind lookupKind(String var1) {
      return this.lookupKind(this.names.fromString(var1));
   }

   static {
      DUMMY = new Token(Tokens.TokenKind.ERROR, 0, 0, (List)null);
   }

   static final class NumericToken extends StringToken {
      public final int radix;

      public NumericToken(TokenKind var1, int var2, int var3, String var4, int var5, List var6) {
         super(var1, var2, var3, var4, var6);
         this.radix = var5;
      }

      protected void checkKind() {
         if (this.kind.tag != Tokens.Token.Tag.NUMERIC) {
            throw new AssertionError("Bad token kind - expected " + Tokens.Token.Tag.NUMERIC);
         }
      }

      public int radix() {
         return this.radix;
      }
   }

   static class StringToken extends Token {
      public final String stringVal;

      public StringToken(TokenKind var1, int var2, int var3, String var4, List var5) {
         super(var1, var2, var3, var5);
         this.stringVal = var4;
      }

      protected void checkKind() {
         if (this.kind.tag != Tokens.Token.Tag.STRING) {
            throw new AssertionError("Bad token kind - expected " + Tokens.Token.Tag.STRING);
         }
      }

      public String stringVal() {
         return this.stringVal;
      }
   }

   static final class NamedToken extends Token {
      public final Name name;

      public NamedToken(TokenKind var1, int var2, int var3, Name var4, List var5) {
         super(var1, var2, var3, var5);
         this.name = var4;
      }

      protected void checkKind() {
         if (this.kind.tag != Tokens.Token.Tag.NAMED) {
            throw new AssertionError("Bad token kind - expected " + Tokens.Token.Tag.NAMED);
         }
      }

      public Name name() {
         return this.name;
      }
   }

   public static class Token {
      public final TokenKind kind;
      public final int pos;
      public final int endPos;
      public final List comments;

      Token(TokenKind var1, int var2, int var3, List var4) {
         this.kind = var1;
         this.pos = var2;
         this.endPos = var3;
         this.comments = var4;
         this.checkKind();
      }

      Token[] split(Tokens var1) {
         if (this.kind.name.length() >= 2 && this.kind.tag == Tokens.Token.Tag.DEFAULT) {
            TokenKind var2 = var1.lookupKind(this.kind.name.substring(0, 1));
            TokenKind var3 = var1.lookupKind(this.kind.name.substring(1));
            if (var2 != null && var3 != null) {
               return new Token[]{new Token(var2, this.pos, this.pos + var2.name.length(), this.comments), new Token(var3, this.pos + var2.name.length(), this.endPos, (List)null)};
            } else {
               throw new AssertionError("Cant split - bad subtokens");
            }
         } else {
            throw new AssertionError("Cant split" + this.kind);
         }
      }

      protected void checkKind() {
         if (this.kind.tag != Tokens.Token.Tag.DEFAULT) {
            throw new AssertionError("Bad token kind - expected " + Tokens.Token.Tag.STRING);
         }
      }

      public Name name() {
         throw new UnsupportedOperationException();
      }

      public String stringVal() {
         throw new UnsupportedOperationException();
      }

      public int radix() {
         throw new UnsupportedOperationException();
      }

      public Comment comment(Comment.CommentStyle var1) {
         List var2 = this.getComments(Tokens.Comment.CommentStyle.JAVADOC);
         return var2.isEmpty() ? null : (Comment)var2.head;
      }

      public boolean deprecatedFlag() {
         Iterator var1 = this.getComments(Tokens.Comment.CommentStyle.JAVADOC).iterator();

         Comment var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (Comment)var1.next();
         } while(!var2.isDeprecated());

         return true;
      }

      private List getComments(Comment.CommentStyle var1) {
         if (this.comments == null) {
            return List.nil();
         } else {
            ListBuffer var2 = new ListBuffer();
            Iterator var3 = this.comments.iterator();

            while(var3.hasNext()) {
               Comment var4 = (Comment)var3.next();
               if (var4.getStyle() == var1) {
                  var2.add(var4);
               }
            }

            return var2.toList();
         }
      }

      static enum Tag {
         DEFAULT,
         NAMED,
         STRING,
         NUMERIC;
      }
   }

   public interface Comment {
      String getText();

      int getSourcePos(int var1);

      CommentStyle getStyle();

      boolean isDeprecated();

      public static enum CommentStyle {
         LINE,
         BLOCK,
         JAVADOC;
      }
   }

   public static enum TokenKind implements Formattable, Filter {
      EOF,
      ERROR,
      IDENTIFIER(Tokens.Token.Tag.NAMED),
      ABSTRACT("abstract"),
      ASSERT("assert", Tokens.Token.Tag.NAMED),
      BOOLEAN("boolean", Tokens.Token.Tag.NAMED),
      BREAK("break"),
      BYTE("byte", Tokens.Token.Tag.NAMED),
      CASE("case"),
      CATCH("catch"),
      CHAR("char", Tokens.Token.Tag.NAMED),
      CLASS("class"),
      CONST("const"),
      CONTINUE("continue"),
      DEFAULT("default"),
      DO("do"),
      DOUBLE("double", Tokens.Token.Tag.NAMED),
      ELSE("else"),
      ENUM("enum", Tokens.Token.Tag.NAMED),
      EXTENDS("extends"),
      FINAL("final"),
      FINALLY("finally"),
      FLOAT("float", Tokens.Token.Tag.NAMED),
      FOR("for"),
      GOTO("goto"),
      IF("if"),
      IMPLEMENTS("implements"),
      IMPORT("import"),
      INSTANCEOF("instanceof"),
      INT("int", Tokens.Token.Tag.NAMED),
      INTERFACE("interface"),
      LONG("long", Tokens.Token.Tag.NAMED),
      NATIVE("native"),
      NEW("new"),
      PACKAGE("package"),
      PRIVATE("private"),
      PROTECTED("protected"),
      PUBLIC("public"),
      RETURN("return"),
      SHORT("short", Tokens.Token.Tag.NAMED),
      STATIC("static"),
      STRICTFP("strictfp"),
      SUPER("super", Tokens.Token.Tag.NAMED),
      SWITCH("switch"),
      SYNCHRONIZED("synchronized"),
      THIS("this", Tokens.Token.Tag.NAMED),
      THROW("throw"),
      THROWS("throws"),
      TRANSIENT("transient"),
      TRY("try"),
      VOID("void", Tokens.Token.Tag.NAMED),
      VOLATILE("volatile"),
      WHILE("while"),
      INTLITERAL(Tokens.Token.Tag.NUMERIC),
      LONGLITERAL(Tokens.Token.Tag.NUMERIC),
      FLOATLITERAL(Tokens.Token.Tag.NUMERIC),
      DOUBLELITERAL(Tokens.Token.Tag.NUMERIC),
      CHARLITERAL(Tokens.Token.Tag.NUMERIC),
      STRINGLITERAL(Tokens.Token.Tag.STRING),
      TRUE("true", Tokens.Token.Tag.NAMED),
      FALSE("false", Tokens.Token.Tag.NAMED),
      NULL("null", Tokens.Token.Tag.NAMED),
      UNDERSCORE("_", Tokens.Token.Tag.NAMED),
      ARROW("->"),
      COLCOL("::"),
      LPAREN("("),
      RPAREN(")"),
      LBRACE("{"),
      RBRACE("}"),
      LBRACKET("["),
      RBRACKET("]"),
      SEMI(";"),
      COMMA(","),
      DOT("."),
      ELLIPSIS("..."),
      EQ("="),
      GT(">"),
      LT("<"),
      BANG("!"),
      TILDE("~"),
      QUES("?"),
      COLON(":"),
      EQEQ("=="),
      LTEQ("<="),
      GTEQ(">="),
      BANGEQ("!="),
      AMPAMP("&&"),
      BARBAR("||"),
      PLUSPLUS("++"),
      SUBSUB("--"),
      PLUS("+"),
      SUB("-"),
      STAR("*"),
      SLASH("/"),
      AMP("&"),
      BAR("|"),
      CARET("^"),
      PERCENT("%"),
      LTLT("<<"),
      GTGT(">>"),
      GTGTGT(">>>"),
      PLUSEQ("+="),
      SUBEQ("-="),
      STAREQ("*="),
      SLASHEQ("/="),
      AMPEQ("&="),
      BAREQ("|="),
      CARETEQ("^="),
      PERCENTEQ("%="),
      LTLTEQ("<<="),
      GTGTEQ(">>="),
      GTGTGTEQ(">>>="),
      MONKEYS_AT("@"),
      CUSTOM;

      public final String name;
      public final Token.Tag tag;

      private TokenKind() {
         this((String)null, Tokens.Token.Tag.DEFAULT);
      }

      private TokenKind(String var3) {
         this(var3, Tokens.Token.Tag.DEFAULT);
      }

      private TokenKind(Token.Tag var3) {
         this((String)null, var3);
      }

      private TokenKind(String var3, Token.Tag var4) {
         this.name = var3;
         this.tag = var4;
      }

      public String toString() {
         switch (this) {
            case IDENTIFIER:
               return "token.identifier";
            case CHARLITERAL:
               return "token.character";
            case STRINGLITERAL:
               return "token.string";
            case INTLITERAL:
               return "token.integer";
            case LONGLITERAL:
               return "token.long-integer";
            case FLOATLITERAL:
               return "token.float";
            case DOUBLELITERAL:
               return "token.double";
            case ERROR:
               return "token.bad-symbol";
            case EOF:
               return "token.end-of-input";
            case DOT:
            case COMMA:
            case SEMI:
            case LPAREN:
            case RPAREN:
            case LBRACKET:
            case RBRACKET:
            case LBRACE:
            case RBRACE:
               return "'" + this.name + "'";
            default:
               return this.name;
         }
      }

      public String getKind() {
         return "Token";
      }

      public String toString(Locale var1, Messages var2) {
         return this.name != null ? this.toString() : var2.getLocalizedString(var1, "compiler.misc." + this.toString());
      }

      public boolean accepts(TokenKind var1) {
         return this == var1;
      }
   }
}
