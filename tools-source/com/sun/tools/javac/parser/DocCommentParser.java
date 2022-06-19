package com.sun.tools.javac.parser;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocTree;
import com.sun.tools.javac.tree.DCTree;
import com.sun.tools.javac.tree.DocTreeMaker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.StringUtils;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DocCommentParser {
   final ParserFactory fac;
   final DiagnosticSource diagSource;
   final Tokens.Comment comment;
   final DocTreeMaker m;
   final Names names;
   BreakIterator sentenceBreaker;
   protected char[] buf;
   protected int bp;
   protected int buflen;
   protected char ch;
   int textStart = -1;
   int lastNonWhite = -1;
   boolean newline = true;
   Map tagParsers;
   Set htmlBlockTags = new HashSet(Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6", "p", "pre"));

   DocCommentParser(ParserFactory var1, DiagnosticSource var2, Tokens.Comment var3) {
      this.fac = var1;
      this.diagSource = var2;
      this.comment = var3;
      this.names = var1.names;
      this.m = var1.docTreeMaker;
      Locale var4 = var1.locale == null ? Locale.getDefault() : var1.locale;
      Options var5 = var1.options;
      boolean var6 = var5.isSet("breakIterator");
      if (var6 || !var4.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
         this.sentenceBreaker = BreakIterator.getSentenceInstance(var4);
      }

      this.initTagParsers();
   }

   DCTree.DCDocComment parse() {
      String var1 = this.comment.getText();
      this.buf = new char[var1.length() + 1];
      var1.getChars(0, var1.length(), this.buf, 0);
      this.buf[this.buf.length - 1] = 26;
      this.buflen = this.buf.length - 1;
      this.bp = -1;
      this.nextChar();
      List var2 = this.blockContent();
      List var3 = this.blockTags();

      ListBuffer var4;
      DCTree var5;
      label71:
      for(var4 = new ListBuffer(); var2.nonEmpty(); var2 = var2.tail) {
         var5 = (DCTree)var2.head;
         switch (var5.getKind()) {
            case TEXT:
               String var6 = ((DCTree.DCText)var5).getBody();
               int var7 = this.getSentenceBreak(var6);
               int var8;
               if (var7 > 0) {
                  for(var8 = var7; var8 > 0 && this.isWhitespace(var6.charAt(var8 - 1)); --var8) {
                  }

                  var4.add(this.m.at(var5.pos).Text(var6.substring(0, var8)));

                  int var9;
                  for(var9 = var7; var9 < var6.length() && this.isWhitespace(var6.charAt(var9)); ++var9) {
                  }

                  var2 = var2.tail;
                  if (var9 < var6.length()) {
                     var2 = var2.prepend(this.m.at(var5.pos + var9).Text(var6.substring(var9)));
                  }
                  break label71;
               }

               if (var2.tail.nonEmpty() && this.isSentenceBreak((DCTree)var2.tail.head)) {
                  for(var8 = var6.length() - 1; var8 > 0 && this.isWhitespace(var6.charAt(var8)); --var8) {
                  }

                  var4.add(this.m.at(var5.pos).Text(var6.substring(0, var8 + 1)));
                  var2 = var2.tail;
                  break label71;
               }
               break;
            case START_ELEMENT:
            case END_ELEMENT:
               if (this.isSentenceBreak(var5)) {
                  break label71;
               }
         }

         var4.add(var5);
      }

      var5 = (DCTree)this.getFirst(var4.toList(), var2, var3);
      int var10 = var5 == null ? -1 : var5.pos;
      DCTree.DCDocComment var11 = this.m.at(var10).DocComment(this.comment, var4.toList(), var2, var3);
      return var11;
   }

   void nextChar() {
      this.ch = this.buf[this.bp < this.buflen ? ++this.bp : this.buflen];
      switch (this.ch) {
         case '\n':
         case '\f':
         case '\r':
            this.newline = true;
         case '\u000b':
         default:
      }
   }

   protected List blockContent() {
      ListBuffer var1 = new ListBuffer();
      this.textStart = -1;

      label34:
      while(this.bp < this.buflen) {
         switch (this.ch) {
            case '\n':
            case '\f':
            case '\r':
               this.newline = true;
            case '\t':
            case ' ':
               this.nextChar();
               break;
            case '&':
               this.entity(var1);
               break;
            case '<':
               this.newline = false;
               this.addPendingText(var1, this.bp - 1);
               var1.add(this.html());
               if (this.textStart == -1) {
                  this.textStart = this.bp;
                  this.lastNonWhite = -1;
               }
               break;
            case '>':
               this.newline = false;
               this.addPendingText(var1, this.bp - 1);
               var1.add(this.m.at(this.bp).Erroneous(this.newString(this.bp, this.bp + 1), this.diagSource, "dc.bad.gt"));
               this.nextChar();
               if (this.textStart == -1) {
                  this.textStart = this.bp;
                  this.lastNonWhite = -1;
               }
               break;
            case '@':
               if (this.newline) {
                  this.addPendingText(var1, this.lastNonWhite);
                  break label34;
               }
            default:
               this.newline = false;
               if (this.textStart == -1) {
                  this.textStart = this.bp;
               }

               this.lastNonWhite = this.bp;
               this.nextChar();
               break;
            case '{':
               this.inlineTag(var1);
         }
      }

      if (this.lastNonWhite != -1) {
         this.addPendingText(var1, this.lastNonWhite);
      }

      return var1.toList();
   }

   protected List blockTags() {
      ListBuffer var1 = new ListBuffer();

      while(this.ch == '@') {
         var1.add(this.blockTag());
      }

      return var1.toList();
   }

   protected DCTree blockTag() {
      int var1 = this.bp;

      try {
         this.nextChar();
         if (this.isIdentifierStart(this.ch)) {
            Name var2 = this.readTagName();
            TagParser var3 = (TagParser)this.tagParsers.get(var2);
            if (var3 == null) {
               List var4 = this.blockContent();
               return this.m.at(var1).UnknownBlockTag(var2, var4);
            }

            switch (var3.getKind()) {
               case BLOCK:
                  return var3.parse(var1);
               case INLINE:
                  return this.erroneous("dc.bad.inline.tag", var1);
            }
         }

         this.blockContent();
         return this.erroneous("dc.no.tag.name", var1);
      } catch (ParseException var5) {
         this.blockContent();
         return this.erroneous(var5.getMessage(), var1);
      }
   }

   protected void inlineTag(ListBuffer var1) {
      this.newline = false;
      this.nextChar();
      if (this.ch == '@') {
         this.addPendingText(var1, this.bp - 2);
         var1.add(this.inlineTag());
         this.textStart = this.bp;
         this.lastNonWhite = -1;
      } else {
         if (this.textStart == -1) {
            this.textStart = this.bp - 1;
         }

         this.lastNonWhite = this.bp;
      }

   }

   protected DCTree inlineTag() {
      int var1 = this.bp - 1;

      try {
         this.nextChar();
         if (this.isIdentifierStart(this.ch)) {
            Name var2 = this.readTagName();
            this.skipWhitespace();
            TagParser var3 = (TagParser)this.tagParsers.get(var2);
            if (var3 == null) {
               DCTree var4 = this.inlineText();
               if (var4 != null) {
                  this.nextChar();
                  return this.m.at(var1).UnknownInlineTag(var2, List.of(var4)).setEndPos(this.bp);
               }
            } else if (var3.getKind() == DocCommentParser.TagParser.Kind.INLINE) {
               DCTree.DCEndPosTree var6 = (DCTree.DCEndPosTree)var3.parse(var1);
               if (var6 != null) {
                  return var6.setEndPos(this.bp);
               }
            } else {
               this.inlineText();
               this.nextChar();
            }
         }

         return this.erroneous("dc.no.tag.name", var1);
      } catch (ParseException var5) {
         return this.erroneous(var5.getMessage(), var1);
      }
   }

   protected DCTree inlineText() throws ParseException {
      this.skipWhitespace();
      int var1 = this.bp;

      for(int var2 = 1; this.bp < this.buflen; this.nextChar()) {
         switch (this.ch) {
            case '\t':
            case ' ':
               break;
            case '\n':
            case '\f':
            case '\r':
               this.newline = true;
               break;
            case '@':
               if (this.newline) {
                  throw new ParseException("dc.unterminated.inline.tag");
               }

               this.newline = false;
               this.lastNonWhite = this.bp;
               break;
            case '{':
               this.newline = false;
               this.lastNonWhite = this.bp;
               ++var2;
               break;
            case '}':
               --var2;
               if (var2 == 0) {
                  return this.m.at(var1).Text(this.newString(var1, this.bp));
               }

               this.newline = false;
               this.lastNonWhite = this.bp;
               break;
            default:
               this.newline = false;
               this.lastNonWhite = this.bp;
         }
      }

      throw new ParseException("dc.unterminated.inline.tag");
   }

   protected DCTree.DCReference reference(boolean var1) throws ParseException {
      int var2 = this.bp;

      int var3;
      label145:
      for(var3 = 0; this.bp < this.buflen; this.nextChar()) {
         switch (this.ch) {
            case '\n':
            case '\f':
            case '\r':
               this.newline = true;
            case '\t':
            case ' ':
               if (var3 == 0) {
                  break label145;
               }
               break;
            case '(':
            case '<':
               this.newline = false;
               ++var3;
               break;
            case ')':
            case '>':
               this.newline = false;
               --var3;
               break;
            case '@':
               if (this.newline) {
                  break label145;
               }
            default:
               this.newline = false;
               break;
            case '}':
               if (this.bp == var2) {
                  return null;
               }

               this.newline = false;
               break label145;
         }
      }

      if (var3 != 0) {
         throw new ParseException("dc.unterminated.signature");
      } else {
         String var4 = this.newString(var2, this.bp);
         Log.DeferredDiagnosticHandler var8 = new Log.DeferredDiagnosticHandler(this.fac.log);

         JCTree var5;
         Name var6;
         List var7;
         try {
            int var9 = var4.indexOf("#");
            int var10 = var4.indexOf("(", var9 + 1);
            if (var9 == -1) {
               if (var10 == -1) {
                  var5 = this.parseType(var4);
                  var6 = null;
               } else {
                  var5 = null;
                  var6 = this.parseMember(var4.substring(0, var10));
               }
            } else {
               var5 = var9 == 0 ? null : this.parseType(var4.substring(0, var9));
               if (var10 == -1) {
                  var6 = this.parseMember(var4.substring(var9 + 1));
               } else {
                  var6 = this.parseMember(var4.substring(var9 + 1, var10));
               }
            }

            if (var10 < 0) {
               var7 = null;
            } else {
               int var11 = var4.indexOf(")", var10);
               if (var11 != var4.length() - 1) {
                  throw new ParseException("dc.ref.bad.parens");
               }

               var7 = this.parseParams(var4.substring(var10 + 1, var11));
            }

            if (!var8.getDiagnostics().isEmpty()) {
               throw new ParseException("dc.ref.syntax.error");
            }
         } finally {
            this.fac.log.popDiagnosticHandler(var8);
         }

         return (DCTree.DCReference)this.m.at(var2).Reference(var4, var5, var6, var7).setEndPos(this.bp);
      }
   }

   JCTree parseType(String var1) throws ParseException {
      JavacParser var2 = this.fac.newParser(var1, false, false, false);
      JCTree.JCExpression var3 = var2.parseType();
      if (var2.token().kind != Tokens.TokenKind.EOF) {
         throw new ParseException("dc.ref.unexpected.input");
      } else {
         return var3;
      }
   }

   Name parseMember(String var1) throws ParseException {
      JavacParser var2 = this.fac.newParser(var1, false, false, false);
      Name var3 = var2.ident();
      if (var2.token().kind != Tokens.TokenKind.EOF) {
         throw new ParseException("dc.ref.unexpected.input");
      } else {
         return var3;
      }
   }

   List parseParams(String var1) throws ParseException {
      if (var1.trim().isEmpty()) {
         return List.nil();
      } else {
         JavacParser var2 = this.fac.newParser(var1.replace("...", "[]"), false, false, false);
         ListBuffer var3 = new ListBuffer();
         var3.add(var2.parseType());
         if (var2.token().kind == Tokens.TokenKind.IDENTIFIER) {
            var2.nextToken();
         }

         while(var2.token().kind == Tokens.TokenKind.COMMA) {
            var2.nextToken();
            var3.add(var2.parseType());
            if (var2.token().kind == Tokens.TokenKind.IDENTIFIER) {
               var2.nextToken();
            }
         }

         if (var2.token().kind != Tokens.TokenKind.EOF) {
            throw new ParseException("dc.ref.unexpected.input");
         } else {
            return var3.toList();
         }
      }
   }

   protected DCTree.DCIdentifier identifier() throws ParseException {
      this.skipWhitespace();
      int var1 = this.bp;
      if (this.isJavaIdentifierStart(this.ch)) {
         Name var2 = this.readJavaIdentifier();
         return this.m.at(var1).Identifier(var2);
      } else {
         throw new ParseException("dc.identifier.expected");
      }
   }

   protected DCTree.DCText quotedString() {
      int var1 = this.bp;
      this.nextChar();

      while(true) {
         label24: {
            if (this.bp < this.buflen) {
               switch (this.ch) {
                  case '\t':
                  case ' ':
                  default:
                     break label24;
                  case '\n':
                  case '\f':
                  case '\r':
                     this.newline = true;
                     break label24;
                  case '"':
                     this.nextChar();
                     return this.m.at(var1).Text(this.newString(var1, this.bp));
                  case '@':
                     if (!this.newline) {
                        break label24;
                     }
               }
            }

            return null;
         }

         this.nextChar();
      }
   }

   protected List inlineContent() {
      ListBuffer var1 = new ListBuffer();
      this.skipWhitespace();
      int var2 = this.bp;
      int var3 = 1;
      this.textStart = -1;

      while(this.bp < this.buflen) {
         switch (this.ch) {
            case '\n':
            case '\f':
            case '\r':
               this.newline = true;
            case '\t':
            case ' ':
               this.nextChar();
               break;
            case '&':
               this.entity(var1);
               break;
            case '<':
               this.newline = false;
               this.addPendingText(var1, this.bp - 1);
               var1.add(this.html());
               break;
            case '@':
               if (this.newline) {
                  return List.of(this.erroneous("dc.unterminated.inline.tag", var2));
               }
            default:
               if (this.textStart == -1) {
                  this.textStart = this.bp;
               }

               this.nextChar();
               break;
            case '{':
               this.newline = false;
               ++var3;
               this.nextChar();
               break;
            case '}':
               this.newline = false;
               --var3;
               if (var3 == 0) {
                  this.addPendingText(var1, this.bp - 1);
                  this.nextChar();
                  return var1.toList();
               }

               this.nextChar();
         }
      }

      return List.of(this.erroneous("dc.unterminated.inline.tag", var2));
   }

   protected void entity(ListBuffer var1) {
      this.newline = false;
      this.addPendingText(var1, this.bp - 1);
      var1.add(this.entity());
      if (this.textStart == -1) {
         this.textStart = this.bp;
         this.lastNonWhite = -1;
      }

   }

   protected DCTree entity() {
      int var1 = this.bp;
      this.nextChar();
      Name var2 = null;
      boolean var3 = false;
      if (this.ch == '#') {
         int var4 = this.bp;
         this.nextChar();
         if (this.isDecimalDigit(this.ch)) {
            this.nextChar();

            while(this.isDecimalDigit(this.ch)) {
               this.nextChar();
            }

            var2 = this.names.fromChars(this.buf, var4, this.bp - var4);
         } else if (this.ch == 'x' || this.ch == 'X') {
            this.nextChar();
            if (this.isHexDigit(this.ch)) {
               this.nextChar();

               while(this.isHexDigit(this.ch)) {
                  this.nextChar();
               }

               var2 = this.names.fromChars(this.buf, var4, this.bp - var4);
            }
         }
      } else if (this.isIdentifierStart(this.ch)) {
         var2 = this.readIdentifier();
      }

      if (var2 == null) {
         return this.erroneous("dc.bad.entity", var1);
      } else if (this.ch != ';') {
         return this.erroneous("dc.missing.semicolon", var1);
      } else {
         this.nextChar();
         return this.m.at(var1).Entity(var2);
      }
   }

   protected DCTree html() {
      int var1 = this.bp;
      this.nextChar();
      Name var2;
      if (this.isIdentifierStart(this.ch)) {
         var2 = this.readIdentifier();
         List var3 = this.htmlAttrs();
         if (var3 != null) {
            boolean var4 = false;
            if (this.ch == '/') {
               this.nextChar();
               var4 = true;
            }

            if (this.ch == '>') {
               this.nextChar();
               return this.m.at(var1).StartElement(var2, var3, var4).setEndPos(this.bp);
            }
         }
      } else if (this.ch == '/') {
         this.nextChar();
         if (this.isIdentifierStart(this.ch)) {
            var2 = this.readIdentifier();
            this.skipWhitespace();
            if (this.ch == '>') {
               this.nextChar();
               return this.m.at(var1).EndElement(var2);
            }
         }
      } else if (this.ch == '!') {
         this.nextChar();
         if (this.ch == '-') {
            this.nextChar();
            if (this.ch == '-') {
               this.nextChar();

               while(this.bp < this.buflen) {
                  int var5 = 0;

                  while(this.ch == '-') {
                     ++var5;
                     this.nextChar();
                  }

                  if (var5 >= 2 && this.ch == '>') {
                     this.nextChar();
                     return this.m.at(var1).Comment(this.newString(var1, this.bp));
                  }

                  this.nextChar();
               }
            }
         }
      }

      this.bp = var1 + 1;
      this.ch = this.buf[this.bp];
      return this.erroneous("dc.malformed.html", var1);
   }

   protected List htmlAttrs() {
      ListBuffer var1 = new ListBuffer();
      this.skipWhitespace();

      while(this.isIdentifierStart(this.ch)) {
         int var2 = this.bp;
         Name var3 = this.readIdentifier();
         this.skipWhitespace();
         List var4 = null;
         AttributeTree.ValueKind var5 = AttributeTree.ValueKind.EMPTY;
         if (this.ch == '=') {
            ListBuffer var6 = new ListBuffer();
            this.nextChar();
            this.skipWhitespace();
            if (this.ch != '\'' && this.ch != '"') {
               var5 = AttributeTree.ValueKind.UNQUOTED;
               this.textStart = this.bp;

               while(this.bp < this.buflen && !this.isUnquotedAttrValueTerminator(this.ch)) {
                  this.attrValueChar(var6);
               }

               this.addPendingText(var6, this.bp - 1);
            } else {
               var5 = this.ch == '\'' ? AttributeTree.ValueKind.SINGLE : AttributeTree.ValueKind.DOUBLE;
               char var7 = this.ch;
               this.nextChar();
               this.textStart = this.bp;

               while(this.bp < this.buflen && this.ch != var7) {
                  if (this.newline && this.ch == '@') {
                     var1.add(this.erroneous("dc.unterminated.string", var2));
                     return var1.toList();
                  }

                  this.attrValueChar(var6);
               }

               this.addPendingText(var6, this.bp - 1);
               this.nextChar();
            }

            this.skipWhitespace();
            var4 = var6.toList();
         }

         DCTree.DCAttribute var8 = this.m.at(var2).Attribute(var3, var5, var4);
         var1.add(var8);
      }

      return var1.toList();
   }

   protected void attrValueChar(ListBuffer var1) {
      switch (this.ch) {
         case '&':
            this.entity(var1);
            break;
         case '{':
            this.inlineTag(var1);
            break;
         default:
            this.nextChar();
      }

   }

   protected void addPendingText(ListBuffer var1, int var2) {
      if (this.textStart != -1) {
         if (this.textStart <= var2) {
            var1.add(this.m.at(this.textStart).Text(this.newString(this.textStart, var2 + 1)));
         }

         this.textStart = -1;
      }

   }

   protected DCTree.DCErroneous erroneous(String var1, int var2) {
      int var3 = this.bp - 1;

      label18:
      while(var3 > var2) {
         switch (this.buf[var3]) {
            case '\n':
            case '\f':
            case '\r':
               this.newline = true;
            case '\t':
            case ' ':
               --var3;
               break;
            default:
               break label18;
         }
      }

      this.textStart = -1;
      return this.m.at(var2).Erroneous(this.newString(var2, var3 + 1), this.diagSource, var1);
   }

   Object getFirst(List... var1) {
      List[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         List var5 = var2[var4];
         if (var5.nonEmpty()) {
            return var5.head;
         }
      }

      return null;
   }

   protected boolean isIdentifierStart(char var1) {
      return Character.isUnicodeIdentifierStart(var1);
   }

   protected Name readIdentifier() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && Character.isUnicodeIdentifierPart(this.ch)) {
         this.nextChar();
      }

      return this.names.fromChars(this.buf, var1, this.bp - var1);
   }

   protected Name readTagName() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && (Character.isUnicodeIdentifierPart(this.ch) || this.ch == '.')) {
         this.nextChar();
      }

      return this.names.fromChars(this.buf, var1, this.bp - var1);
   }

   protected boolean isJavaIdentifierStart(char var1) {
      return Character.isJavaIdentifierStart(var1);
   }

   protected Name readJavaIdentifier() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && Character.isJavaIdentifierPart(this.ch)) {
         this.nextChar();
      }

      return this.names.fromChars(this.buf, var1, this.bp - var1);
   }

   protected boolean isDecimalDigit(char var1) {
      return '0' <= var1 && var1 <= '9';
   }

   protected boolean isHexDigit(char var1) {
      return '0' <= var1 && var1 <= '9' || 'a' <= var1 && var1 <= 'f' || 'A' <= var1 && var1 <= 'F';
   }

   protected boolean isUnquotedAttrValueTerminator(char var1) {
      switch (var1) {
         case '\t':
         case '\n':
         case '\f':
         case '\r':
         case ' ':
         case '"':
         case '\'':
         case '<':
         case '=':
         case '>':
         case '`':
            return true;
         default:
            return false;
      }
   }

   protected boolean isWhitespace(char var1) {
      return Character.isWhitespace(var1);
   }

   protected void skipWhitespace() {
      while(this.isWhitespace(this.ch)) {
         this.nextChar();
      }

   }

   protected int getSentenceBreak(String var1) {
      if (this.sentenceBreaker != null) {
         this.sentenceBreaker.setText(var1);
         int var4 = this.sentenceBreaker.next();
         return var4 == var1.length() ? -1 : var4;
      } else {
         boolean var2 = false;

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            switch (var1.charAt(var3)) {
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
                  if (var2) {
                     return var3;
                  }
                  break;
               case '.':
                  var2 = true;
                  break;
               default:
                  var2 = false;
            }
         }

         return -1;
      }
   }

   protected boolean isSentenceBreak(Name var1) {
      return this.htmlBlockTags.contains(StringUtils.toLowerCase(var1.toString()));
   }

   protected boolean isSentenceBreak(DCTree var1) {
      switch (var1.getKind()) {
         case START_ELEMENT:
            return this.isSentenceBreak(((DCTree.DCStartElement)var1).getName());
         case END_ELEMENT:
            return this.isSentenceBreak(((DCTree.DCEndElement)var1).getName());
         default:
            return false;
      }
   }

   String newString(int var1, int var2) {
      return new String(this.buf, var1, var2 - var1);
   }

   private void initTagParsers() {
      TagParser[] var1 = new TagParser[]{new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.AUTHOR) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Author(var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.CODE) {
         public DCTree parse(int var1) throws ParseException {
            DCTree var2 = DocCommentParser.this.inlineText();
            DocCommentParser.this.nextChar();
            return DocCommentParser.this.m.at(var1).Code((DCTree.DCText)var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.DEPRECATED) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Deprecated(var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.DOC_ROOT) {
         public DCTree parse(int var1) throws ParseException {
            if (DocCommentParser.this.ch == '}') {
               DocCommentParser.this.nextChar();
               return DocCommentParser.this.m.at(var1).DocRoot();
            } else {
               DocCommentParser.this.inlineText();
               DocCommentParser.this.nextChar();
               throw new ParseException("dc.unexpected.content");
            }
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.EXCEPTION) {
         public DCTree parse(int var1) throws ParseException {
            DocCommentParser.this.skipWhitespace();
            DCTree.DCReference var2 = DocCommentParser.this.reference(false);
            List var3 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Exception(var2, var3);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.INHERIT_DOC) {
         public DCTree parse(int var1) throws ParseException {
            if (DocCommentParser.this.ch == '}') {
               DocCommentParser.this.nextChar();
               return DocCommentParser.this.m.at(var1).InheritDoc();
            } else {
               DocCommentParser.this.inlineText();
               DocCommentParser.this.nextChar();
               throw new ParseException("dc.unexpected.content");
            }
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.LINK) {
         public DCTree parse(int var1) throws ParseException {
            DCTree.DCReference var2 = DocCommentParser.this.reference(true);
            List var3 = DocCommentParser.this.inlineContent();
            return DocCommentParser.this.m.at(var1).Link(var2, var3);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.LINK_PLAIN) {
         public DCTree parse(int var1) throws ParseException {
            DCTree.DCReference var2 = DocCommentParser.this.reference(true);
            List var3 = DocCommentParser.this.inlineContent();
            return DocCommentParser.this.m.at(var1).LinkPlain(var2, var3);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.LITERAL) {
         public DCTree parse(int var1) throws ParseException {
            DCTree var2 = DocCommentParser.this.inlineText();
            DocCommentParser.this.nextChar();
            return DocCommentParser.this.m.at(var1).Literal((DCTree.DCText)var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.PARAM) {
         public DCTree parse(int var1) throws ParseException {
            DocCommentParser.this.skipWhitespace();
            boolean var2 = false;
            if (DocCommentParser.this.ch == '<') {
               var2 = true;
               DocCommentParser.this.nextChar();
            }

            DCTree.DCIdentifier var3 = DocCommentParser.this.identifier();
            if (var2) {
               if (DocCommentParser.this.ch != '>') {
                  throw new ParseException("dc.gt.expected");
               }

               DocCommentParser.this.nextChar();
            }

            DocCommentParser.this.skipWhitespace();
            List var4 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Param(var2, var3, var4);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.RETURN) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Return(var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.SEE) {
         public DCTree parse(int var1) throws ParseException {
            DocCommentParser.this.skipWhitespace();
            switch (DocCommentParser.this.ch) {
               case '\u001a':
                  if (DocCommentParser.this.bp == DocCommentParser.this.buf.length - 1) {
                     throw new ParseException("dc.no.content");
                  }
                  break;
               case '"':
                  DCTree.DCText var2 = DocCommentParser.this.quotedString();
                  if (var2 == null) {
                     break;
                  }

                  DocCommentParser.this.skipWhitespace();
                  if (DocCommentParser.this.ch != '@' && (DocCommentParser.this.ch != 26 || DocCommentParser.this.bp != DocCommentParser.this.buf.length - 1)) {
                     break;
                  }

                  return DocCommentParser.this.m.at(var1).See(List.of(var2));
               case '<':
                  List var3 = DocCommentParser.this.blockContent();
                  if (var3 != null) {
                     return DocCommentParser.this.m.at(var1).See(var3);
                  }
                  break;
               case '@':
                  if (DocCommentParser.this.newline) {
                     throw new ParseException("dc.no.content");
                  }
                  break;
               default:
                  if (DocCommentParser.this.isJavaIdentifierStart(DocCommentParser.this.ch) || DocCommentParser.this.ch == '#') {
                     DCTree.DCReference var4 = DocCommentParser.this.reference(true);
                     List var5 = DocCommentParser.this.blockContent();
                     return DocCommentParser.this.m.at(var1).See(var5.prepend(var4));
                  }
            }

            throw new ParseException("dc.unexpected.content");
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.SERIAL_DATA) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).SerialData(var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.SERIAL_FIELD) {
         public DCTree parse(int var1) throws ParseException {
            DocCommentParser.this.skipWhitespace();
            DCTree.DCIdentifier var2 = DocCommentParser.this.identifier();
            DocCommentParser.this.skipWhitespace();
            DCTree.DCReference var3 = DocCommentParser.this.reference(false);
            List var4 = null;
            if (DocCommentParser.this.isWhitespace(DocCommentParser.this.ch)) {
               DocCommentParser.this.skipWhitespace();
               var4 = DocCommentParser.this.blockContent();
            }

            return DocCommentParser.this.m.at(var1).SerialField(var2, var3, var4);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.SERIAL) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Serial(var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.SINCE) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Since(var2);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.THROWS) {
         public DCTree parse(int var1) throws ParseException {
            DocCommentParser.this.skipWhitespace();
            DCTree.DCReference var2 = DocCommentParser.this.reference(false);
            List var3 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Throws(var2, var3);
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.INLINE, DocTree.Kind.VALUE) {
         public DCTree parse(int var1) throws ParseException {
            DCTree.DCReference var2 = DocCommentParser.this.reference(true);
            DocCommentParser.this.skipWhitespace();
            if (DocCommentParser.this.ch == '}') {
               DocCommentParser.this.nextChar();
               return DocCommentParser.this.m.at(var1).Value(var2);
            } else {
               DocCommentParser.this.nextChar();
               throw new ParseException("dc.unexpected.content");
            }
         }
      }, new TagParser(DocCommentParser.TagParser.Kind.BLOCK, DocTree.Kind.VERSION) {
         public DCTree parse(int var1) {
            List var2 = DocCommentParser.this.blockContent();
            return DocCommentParser.this.m.at(var1).Version(var2);
         }
      }};
      this.tagParsers = new HashMap();
      TagParser[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TagParser var5 = var2[var4];
         this.tagParsers.put(this.names.fromString(var5.getTreeKind().tagName), var5);
      }

   }

   abstract static class TagParser {
      Kind kind;
      DocTree.Kind treeKind;

      TagParser(Kind var1, DocTree.Kind var2) {
         this.kind = var1;
         this.treeKind = var2;
      }

      Kind getKind() {
         return this.kind;
      }

      DocTree.Kind getTreeKind() {
         return this.treeKind;
      }

      abstract DCTree parse(int var1) throws ParseException;

      static enum Kind {
         INLINE,
         BLOCK;
      }
   }

   static class ParseException extends Exception {
      private static final long serialVersionUID = 0L;

      ParseException(String var1) {
         super(var1);
      }
   }
}
