package com.sun.tools.javadoc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** @deprecated */
@Deprecated
public class JavaScriptScanner {
   private Reporter reporter;
   protected char[] buf;
   protected int bp;
   protected int buflen;
   protected char ch;
   private boolean newline = true;
   Map tagParsers;
   Set eventAttrs;
   Set uriAttrs;

   public JavaScriptScanner() {
      this.initTagParsers();
      this.initEventAttrs();
      this.initURIAttrs();
   }

   public void parse(String var1, Reporter var2) {
      this.reporter = var2;
      this.buf = new char[var1.length() + 1];
      var1.getChars(0, var1.length(), this.buf, 0);
      this.buf[this.buf.length - 1] = 26;
      this.buflen = this.buf.length - 1;
      this.bp = -1;
      this.newline = true;
      this.nextChar();
      this.blockContent();
      this.blockTags();
   }

   private void checkHtmlTag(String var1) {
      if (var1.equalsIgnoreCase("script")) {
         this.reporter.report();
      }

   }

   private void checkHtmlAttr(String var1, String var2) {
      String var3 = var1.toLowerCase(Locale.ENGLISH);
      if (this.eventAttrs.contains(var3) || this.uriAttrs.contains(var3) && var2 != null && var2.toLowerCase(Locale.ENGLISH).trim().startsWith("javascript:")) {
         this.reporter.report();
      }

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

   protected void blockContent() {
      while(true) {
         label47:
         while(true) {
            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        label37: {
                           if (this.bp < this.buflen) {
                              switch (this.ch) {
                                 case '\t':
                                 case ' ':
                                    break label47;
                                 case '\n':
                                 case '\f':
                                 case '\r':
                                    this.newline = true;
                                    break label47;
                                 case '&':
                                    this.entity((Void)null);
                                    continue;
                                 case '<':
                                    this.html();
                                    continue;
                                 case '>':
                                    this.newline = false;
                                    this.nextChar();
                                    continue;
                                 case '@':
                                    if (!this.newline) {
                                       break label37;
                                    }
                                    break;
                                 case '{':
                                    this.inlineTag((Void)null);
                                    continue;
                                 default:
                                    break label37;
                              }
                           }

                           return;
                        }

                        this.newline = false;
                        this.nextChar();
                     }
                  }
               }
            }
         }

         this.nextChar();
      }
   }

   protected void blockTags() {
      while(this.ch == '@') {
         this.blockTag();
      }

   }

   protected void blockTag() {
      int var1 = this.bp;

      try {
         this.nextChar();
         if (this.isIdentifierStart(this.ch)) {
            String var2 = this.readTagName();
            TagParser var3 = (TagParser)this.tagParsers.get(var2);
            if (var3 == null) {
               this.blockContent();
            } else {
               switch (var3.getKind()) {
                  case BLOCK:
                     var3.parse(var1);
                     return;
                  case INLINE:
                     return;
               }
            }
         }

         this.blockContent();
      } catch (ParseException var4) {
         this.blockContent();
      }

   }

   protected void inlineTag(Void var1) {
      this.newline = false;
      this.nextChar();
      if (this.ch == '@') {
         this.inlineTag();
      }

   }

   protected void inlineTag() {
      int var1 = this.bp - 1;

      try {
         this.nextChar();
         if (this.isIdentifierStart(this.ch)) {
            String var2 = this.readTagName();
            TagParser var3 = (TagParser)this.tagParsers.get(var2);
            if (var3 == null) {
               this.skipWhitespace();
               this.inlineText(JavaScriptScanner.WhitespaceRetentionPolicy.REMOVE_ALL);
               this.nextChar();
            } else {
               this.skipWhitespace();
               if (var3.getKind() == JavaScriptScanner.TagParser.Kind.INLINE) {
                  var3.parse(var1);
               } else {
                  this.inlineText(JavaScriptScanner.WhitespaceRetentionPolicy.REMOVE_ALL);
                  this.nextChar();
               }
            }
         }
      } catch (ParseException var4) {
      }

   }

   private void inlineText(WhitespaceRetentionPolicy var1) throws ParseException {
      switch (var1) {
         case REMOVE_ALL:
            this.skipWhitespace();
            break;
         case REMOVE_FIRST_SPACE:
            if (this.ch == ' ') {
               this.nextChar();
            }
         case RETAIN_ALL:
      }

      int var2 = this.bp;

      for(int var3 = 1; this.bp < this.buflen; this.nextChar()) {
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
               break;
            case '{':
               this.newline = false;
               ++var3;
               break;
            case '}':
               --var3;
               if (var3 == 0) {
                  return;
               }

               this.newline = false;
               break;
            default:
               this.newline = false;
         }
      }

      throw new ParseException("dc.unterminated.inline.tag");
   }

   protected void reference(boolean var1) throws ParseException {
      int var2 = this.bp;

      int var3;
      label33:
      for(var3 = 0; this.bp < this.buflen; this.nextChar()) {
         switch (this.ch) {
            case '\n':
            case '\f':
            case '\r':
               this.newline = true;
            case '\t':
            case ' ':
               if (var3 == 0) {
                  break label33;
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
                  break label33;
               }
            default:
               this.newline = false;
               break;
            case '}':
               if (this.bp == var2) {
                  return;
               }

               this.newline = false;
               break label33;
         }
      }

      if (var3 != 0) {
         throw new ParseException("dc.unterminated.signature");
      }
   }

   protected void identifier() throws ParseException {
      this.skipWhitespace();
      int var1 = this.bp;
      if (this.isJavaIdentifierStart(this.ch)) {
         this.readJavaIdentifier();
      } else {
         throw new ParseException("dc.identifier.expected");
      }
   }

   protected void quotedString() {
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
                     return;
                  case '@':
                     if (!this.newline) {
                        break label24;
                     }
               }
            }

            return;
         }

         this.nextChar();
      }
   }

   protected void inlineWord() {
      int var1 = this.bp;
      int var2 = 0;

      while(true) {
         label40: {
            if (this.bp < this.buflen) {
               switch (this.ch) {
                  case '\t':
                  case '\f':
                  case '\r':
                  case ' ':
                     return;
                  case '\n':
                     this.newline = true;
                     return;
                  case '@':
                     if (this.newline) {
                        break;
                     }
                  case '{':
                     ++var2;
                     break label40;
                  case '}':
                     if (var2 == 0) {
                        return;
                     }

                     --var2;
                     if (var2 == 0) {
                        return;
                     }
                  default:
                     break label40;
               }
            }

            return;
         }

         this.newline = false;
         this.nextChar();
      }
   }

   private void inlineContent() {
      this.skipWhitespace();
      int var1 = this.bp;
      int var2 = 1;

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
               this.entity((Void)null);
               break;
            case '<':
               this.newline = false;
               this.html();
               break;
            case '@':
               if (this.newline) {
                  return;
               }
            default:
               this.nextChar();
               break;
            case '{':
               this.newline = false;
               ++var2;
               this.nextChar();
               break;
            case '}':
               this.newline = false;
               --var2;
               if (var2 == 0) {
                  this.nextChar();
                  return;
               }

               this.nextChar();
         }
      }

   }

   protected void entity(Void var1) {
      this.newline = false;
      this.entity();
   }

   protected void entity() {
      this.nextChar();
      String var1 = null;
      if (this.ch == '#') {
         int var2 = this.bp;
         this.nextChar();
         if (this.isDecimalDigit(this.ch)) {
            this.nextChar();

            while(this.isDecimalDigit(this.ch)) {
               this.nextChar();
            }

            var1 = new String(this.buf, var2, this.bp - var2);
         } else if (this.ch == 'x' || this.ch == 'X') {
            this.nextChar();
            if (this.isHexDigit(this.ch)) {
               this.nextChar();

               while(this.isHexDigit(this.ch)) {
                  this.nextChar();
               }

               var1 = new String(this.buf, var2, this.bp - var2);
            }
         }
      } else if (this.isIdentifierStart(this.ch)) {
         var1 = this.readIdentifier();
      }

      if (var1 != null) {
         if (this.ch != ';') {
            return;
         }

         this.nextChar();
      }

   }

   protected void html() {
      int var1 = this.bp;
      this.nextChar();
      if (this.isIdentifierStart(this.ch)) {
         String var2 = this.readIdentifier();
         this.checkHtmlTag(var2);
         this.htmlAttrs();
         if (this.ch == '/') {
            this.nextChar();
         }

         if (this.ch == '>') {
            this.nextChar();
            return;
         }
      } else if (this.ch == '/') {
         this.nextChar();
         if (this.isIdentifierStart(this.ch)) {
            this.readIdentifier();
            this.skipWhitespace();
            if (this.ch == '>') {
               this.nextChar();
               return;
            }
         }
      } else if (this.ch == '!') {
         this.nextChar();
         if (this.ch == '-') {
            this.nextChar();
            if (this.ch == '-') {
               this.nextChar();

               while(this.bp < this.buflen) {
                  int var3 = 0;

                  while(this.ch == '-') {
                     ++var3;
                     this.nextChar();
                  }

                  if (var3 >= 2 && this.ch == '>') {
                     this.nextChar();
                     return;
                  }

                  this.nextChar();
               }
            }
         }
      }

      this.bp = var1 + 1;
      this.ch = this.buf[this.bp];
   }

   protected void htmlAttrs() {
      this.skipWhitespace();

      String var2;
      StringBuilder var3;
      for(; this.isIdentifierStart(this.ch); this.checkHtmlAttr(var2, var3.toString())) {
         int var1 = this.bp;
         var2 = this.readAttributeName();
         this.skipWhitespace();
         var3 = new StringBuilder();
         if (this.ch == '=') {
            this.nextChar();
            this.skipWhitespace();
            if (this.ch != '\'' && this.ch != '"') {
               while(this.bp < this.buflen && !this.isUnquotedAttrValueTerminator(this.ch)) {
                  var3.append(this.ch);
                  this.nextChar();
               }
            } else {
               char var4 = this.ch;
               this.nextChar();

               while(this.bp < this.buflen && this.ch != var4) {
                  if (this.newline && this.ch == '@') {
                     return;
                  }

                  var3.append(this.ch);
                  this.nextChar();
               }

               this.nextChar();
            }

            this.skipWhitespace();
         }
      }

   }

   protected void attrValueChar(Void var1) {
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

   protected boolean isIdentifierStart(char var1) {
      return Character.isUnicodeIdentifierStart(var1);
   }

   protected String readIdentifier() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && Character.isUnicodeIdentifierPart(this.ch)) {
         this.nextChar();
      }

      return new String(this.buf, var1, this.bp - var1);
   }

   protected String readAttributeName() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && (Character.isUnicodeIdentifierPart(this.ch) || this.ch == '-')) {
         this.nextChar();
      }

      return new String(this.buf, var1, this.bp - var1);
   }

   protected String readTagName() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && (Character.isUnicodeIdentifierPart(this.ch) || this.ch == '.' || this.ch == '-' || this.ch == ':')) {
         this.nextChar();
      }

      return new String(this.buf, var1, this.bp - var1);
   }

   protected boolean isJavaIdentifierStart(char var1) {
      return Character.isJavaIdentifierStart(var1);
   }

   protected String readJavaIdentifier() {
      int var1 = this.bp;
      this.nextChar();

      while(this.bp < this.buflen && Character.isJavaIdentifierPart(this.ch)) {
         this.nextChar();
      }

      return new String(this.buf, var1, this.bp - var1);
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

   String newString(int var1, int var2) {
      return new String(this.buf, var1, var2 - var1);
   }

   private void initTagParsers() {
      TagParser[] var1 = new TagParser[]{new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "author") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "code", true) {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.inlineText(JavaScriptScanner.WhitespaceRetentionPolicy.REMOVE_FIRST_SPACE);
            JavaScriptScanner.this.nextChar();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "deprecated") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "docRoot") {
         public void parse(int var1) throws ParseException {
            if (JavaScriptScanner.this.ch == '}') {
               JavaScriptScanner.this.nextChar();
            } else {
               JavaScriptScanner.this.inlineText(JavaScriptScanner.WhitespaceRetentionPolicy.REMOVE_ALL);
               JavaScriptScanner.this.nextChar();
               throw new ParseException("dc.unexpected.content");
            }
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "exception") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.skipWhitespace();
            JavaScriptScanner.this.reference(false);
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "hidden") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "index") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.skipWhitespace();
            if (JavaScriptScanner.this.ch == '}') {
               throw new ParseException("dc.no.content");
            } else {
               if (JavaScriptScanner.this.ch == '"') {
                  JavaScriptScanner.this.quotedString();
               } else {
                  JavaScriptScanner.this.inlineWord();
               }

               JavaScriptScanner.this.skipWhitespace();
               if (JavaScriptScanner.this.ch != '}') {
                  JavaScriptScanner.this.inlineContent();
               } else {
                  JavaScriptScanner.this.nextChar();
               }

            }
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "inheritDoc") {
         public void parse(int var1) throws ParseException {
            if (JavaScriptScanner.this.ch == '}') {
               JavaScriptScanner.this.nextChar();
            } else {
               JavaScriptScanner.this.inlineText(JavaScriptScanner.WhitespaceRetentionPolicy.REMOVE_ALL);
               JavaScriptScanner.this.nextChar();
               throw new ParseException("dc.unexpected.content");
            }
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "link") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.reference(true);
            JavaScriptScanner.this.inlineContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "linkplain") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.reference(true);
            JavaScriptScanner.this.inlineContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "literal", true) {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.inlineText(JavaScriptScanner.WhitespaceRetentionPolicy.REMOVE_FIRST_SPACE);
            JavaScriptScanner.this.nextChar();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "param") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.skipWhitespace();
            boolean var2 = false;
            if (JavaScriptScanner.this.ch == '<') {
               var2 = true;
               JavaScriptScanner.this.nextChar();
            }

            JavaScriptScanner.this.identifier();
            if (var2) {
               if (JavaScriptScanner.this.ch != '>') {
                  throw new ParseException("dc.gt.expected");
               }

               JavaScriptScanner.this.nextChar();
            }

            JavaScriptScanner.this.skipWhitespace();
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "return") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "see") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.skipWhitespace();
            switch (JavaScriptScanner.this.ch) {
               case '\u001a':
                  if (JavaScriptScanner.this.bp == JavaScriptScanner.this.buf.length - 1) {
                     throw new ParseException("dc.no.content");
                  }
                  break;
               case '"':
                  JavaScriptScanner.this.quotedString();
                  JavaScriptScanner.this.skipWhitespace();
                  if (JavaScriptScanner.this.ch == '@' || JavaScriptScanner.this.ch == 26 && JavaScriptScanner.this.bp == JavaScriptScanner.this.buf.length - 1) {
                     return;
                  }
                  break;
               case '<':
                  JavaScriptScanner.this.blockContent();
                  return;
               case '@':
                  if (JavaScriptScanner.this.newline) {
                     throw new ParseException("dc.no.content");
                  }
                  break;
               default:
                  if (JavaScriptScanner.this.isJavaIdentifierStart(JavaScriptScanner.this.ch) || JavaScriptScanner.this.ch == '#') {
                     JavaScriptScanner.this.reference(true);
                     JavaScriptScanner.this.blockContent();
                  }
            }

            throw new ParseException("dc.unexpected.content");
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "@serialData") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "serialField") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.skipWhitespace();
            JavaScriptScanner.this.identifier();
            JavaScriptScanner.this.skipWhitespace();
            JavaScriptScanner.this.reference(false);
            if (JavaScriptScanner.this.isWhitespace(JavaScriptScanner.this.ch)) {
               JavaScriptScanner.this.skipWhitespace();
               JavaScriptScanner.this.blockContent();
            }

         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "serial") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "since") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "throws") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.skipWhitespace();
            JavaScriptScanner.this.reference(false);
            JavaScriptScanner.this.blockContent();
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.INLINE, "value") {
         public void parse(int var1) throws ParseException {
            JavaScriptScanner.this.reference(true);
            JavaScriptScanner.this.skipWhitespace();
            if (JavaScriptScanner.this.ch == '}') {
               JavaScriptScanner.this.nextChar();
            } else {
               JavaScriptScanner.this.nextChar();
               throw new ParseException("dc.unexpected.content");
            }
         }
      }, new TagParser(JavaScriptScanner.TagParser.Kind.BLOCK, "version") {
         public void parse(int var1) {
            JavaScriptScanner.this.blockContent();
         }
      }};
      this.tagParsers = new HashMap();
      TagParser[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TagParser var5 = var2[var4];
         this.tagParsers.put(var5.getName(), var5);
      }

   }

   private void initEventAttrs() {
      this.eventAttrs = new HashSet(Arrays.asList("onabort", "onblur", "oncanplay", "oncanplaythrough", "onchange", "onclick", "oncontextmenu", "ondblclick", "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", "onfocus", "oninput", "oninvalid", "onkeydown", "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata", "onloadstart", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup", "onmousewheel", "onpause", "onplay", "onplaying", "onprogress", "onratechange", "onreadystatechange", "onreset", "onscroll", "onseeked", "onseeking", "onselect", "onshow", "onstalled", "onsubmit", "onsuspend", "ontimeupdate", "onvolumechange", "onwaiting", "onunload"));
   }

   private void initURIAttrs() {
      this.uriAttrs = new HashSet(Arrays.asList("action", "cite", "classid", "codebase", "data", "datasrc", "for", "href", "longdesc", "profile", "src", "usemap"));
   }

   abstract static class TagParser {
      final Kind kind;
      final String name;

      TagParser(Kind var1, String var2) {
         this.kind = var1;
         this.name = var2;
      }

      TagParser(Kind var1, String var2, boolean var3) {
         this(var1, var2);
      }

      Kind getKind() {
         return this.kind;
      }

      String getName() {
         return this.name;
      }

      abstract void parse(int var1) throws ParseException;

      static enum Kind {
         INLINE,
         BLOCK;
      }
   }

   private static enum WhitespaceRetentionPolicy {
      RETAIN_ALL,
      REMOVE_FIRST_SPACE,
      REMOVE_ALL;
   }

   static class ParseException extends Exception {
      private static final long serialVersionUID = 0L;

      ParseException(String var1) {
         super(var1);
      }
   }

   public interface Reporter {
      void report();
   }
}
