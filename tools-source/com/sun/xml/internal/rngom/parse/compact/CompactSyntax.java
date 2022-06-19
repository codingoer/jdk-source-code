package com.sun.xml.internal.rngom.parse.compact;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.builder.DataPatternBuilder;
import com.sun.xml.internal.rngom.ast.builder.Div;
import com.sun.xml.internal.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.xml.internal.rngom.ast.builder.Grammar;
import com.sun.xml.internal.rngom.ast.builder.GrammarSection;
import com.sun.xml.internal.rngom.ast.builder.Include;
import com.sun.xml.internal.rngom.ast.builder.IncludedGrammar;
import com.sun.xml.internal.rngom.ast.builder.NameClassBuilder;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedNameClass;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.Context;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;
import com.sun.xml.internal.rngom.util.Localizer;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class CompactSyntax implements Context, CompactSyntaxConstants {
   private static final int IN_ELEMENT = 0;
   private static final int IN_ATTRIBUTE = 1;
   private static final int IN_ANY_NAME = 2;
   private static final int IN_NS_NAME = 4;
   private String defaultNamespace;
   private String compatibilityPrefix;
   private SchemaBuilder sb;
   private NameClassBuilder ncb;
   private String sourceUri;
   private CompactParseable parseable;
   private ErrorHandler eh;
   private final Hashtable namespaceTable;
   private final Hashtable datatypesTable;
   private boolean hadError;
   private static final Localizer localizer = new Localizer(new Localizer(Parseable.class), CompactSyntax.class);
   private final Hashtable attributeNameTable;
   private boolean annotationsIncludeElements;
   private String inheritedNs;
   private CommentList topLevelComments;
   private Token lastCommentSourceToken;
   public CompactSyntaxTokenManager token_source;
   JavaCharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private Token jj_scanpos;
   private Token jj_lastpos;
   private int jj_la;
   private int jj_gen;
   private final int[] jj_la1;
   private static int[] jj_la1_0;
   private static int[] jj_la1_1;
   private final JJCalls[] jj_2_rtns;
   private boolean jj_rescan;
   private int jj_gc;
   private final LookaheadSuccess jj_ls;
   private List jj_expentries;
   private int[] jj_expentry;
   private int jj_kind;
   private int[] jj_lasttokens;
   private int jj_endpos;

   public CompactSyntax(CompactParseable parseable, Reader r, String sourceUri, SchemaBuilder sb, ErrorHandler eh, String inheritedNs) {
      this(r);
      this.sourceUri = sourceUri;
      this.parseable = parseable;
      this.sb = sb;
      this.ncb = sb.getNameClassBuilder();
      this.eh = eh;
      this.topLevelComments = sb.makeCommentList();
      this.inheritedNs = this.defaultNamespace = new String(inheritedNs);
   }

   ParsedPattern parse(Scope scope) throws IllegalSchemaException {
      try {
         ParsedPattern p = this.Input(scope);
         if (!this.hadError) {
            return p;
         }
      } catch (ParseException var3) {
         this.error("syntax_error", var3.getMessage(), var3.currentToken.next);
      } catch (EscapeSyntaxException var4) {
         this.reportEscapeSyntaxException(var4);
      }

      throw new IllegalSchemaException();
   }

   ParsedPattern parseInclude(IncludedGrammar g) throws IllegalSchemaException {
      try {
         ParsedPattern p = this.IncludedGrammar(g);
         if (!this.hadError) {
            return p;
         }
      } catch (ParseException var3) {
         this.error("syntax_error", var3.getMessage(), var3.currentToken.next);
      } catch (EscapeSyntaxException var4) {
         this.reportEscapeSyntaxException(var4);
      }

      throw new IllegalSchemaException();
   }

   private void checkNsName(int context, LocatedString ns) {
      if ((context & 4) != 0) {
         this.error("ns_name_except_contains_ns_name", ns.getToken());
      }

   }

   private void checkAnyName(int context, Token t) {
      if ((context & 4) != 0) {
         this.error("ns_name_except_contains_any_name", t);
      }

      if ((context & 2) != 0) {
         this.error("any_name_except_contains_any_name", t);
      }

   }

   private void error(String key, Token tok) {
      this.doError(localizer.message(key), tok);
   }

   private void error(String key, String arg, Token tok) {
      this.doError(localizer.message(key, (Object)arg), tok);
   }

   private void error(String key, String arg1, String arg2, Token tok) {
      this.doError(localizer.message(key, arg1, arg2), tok);
   }

   private void doError(String message, Token tok) {
      this.hadError = true;
      if (this.eh != null) {
         LocatorImpl loc = new LocatorImpl();
         loc.setLineNumber(tok.beginLine);
         loc.setColumnNumber(tok.beginColumn);
         loc.setSystemId(this.sourceUri);

         try {
            this.eh.error(new SAXParseException(message, loc));
         } catch (SAXException var5) {
            throw new BuildException(var5);
         }
      }

   }

   private void reportEscapeSyntaxException(EscapeSyntaxException e) {
      if (this.eh != null) {
         LocatorImpl loc = new LocatorImpl();
         loc.setLineNumber(e.getLineNumber());
         loc.setColumnNumber(e.getColumnNumber());
         loc.setSystemId(this.sourceUri);

         try {
            this.eh.error(new SAXParseException(localizer.message(e.getKey()), loc));
         } catch (SAXException var4) {
            throw new BuildException(var4);
         }
      }

   }

   private static String unquote(String s) {
      if (s.length() >= 6 && s.charAt(0) == s.charAt(1)) {
         s = s.replace('\u0000', '\n');
         return s.substring(3, s.length() - 3);
      } else {
         return s.substring(1, s.length() - 1);
      }
   }

   Location makeLocation(Token t) {
      return this.sb.makeLocation(this.sourceUri, t.beginLine, t.beginColumn);
   }

   private static ParsedPattern[] addPattern(ParsedPattern[] patterns, int i, ParsedPattern p) {
      if (i >= patterns.length) {
         ParsedPattern[] oldPatterns = patterns;
         patterns = new ParsedPattern[patterns.length * 2];
         System.arraycopy(oldPatterns, 0, patterns, 0, oldPatterns.length);
      }

      patterns[i] = p;
      return patterns;
   }

   String getCompatibilityPrefix() {
      if (this.compatibilityPrefix == null) {
         for(this.compatibilityPrefix = "a"; this.namespaceTable.get(this.compatibilityPrefix) != null; this.compatibilityPrefix = this.compatibilityPrefix + "a") {
         }
      }

      return this.compatibilityPrefix;
   }

   public String resolveNamespacePrefix(String prefix) {
      String result = (String)this.namespaceTable.get(prefix);
      return result.length() == 0 ? null : result;
   }

   public Enumeration prefixes() {
      return this.namespaceTable.keys();
   }

   public String getBaseUri() {
      return this.sourceUri;
   }

   public boolean isUnparsedEntity(String entityName) {
      return false;
   }

   public boolean isNotation(String notationName) {
      return false;
   }

   public Context copy() {
      return this;
   }

   private Context getContext() {
      return this;
   }

   private CommentList getComments() {
      return this.getComments(this.getTopLevelComments());
   }

   private CommentList getTopLevelComments() {
      CommentList tem = this.topLevelComments;
      this.topLevelComments = null;
      return tem;
   }

   private void noteTopLevelComments() {
      this.topLevelComments = this.getComments(this.topLevelComments);
   }

   private void topLevelComments(GrammarSection section) {
      section.topLevelComment(this.getComments((CommentList)null));
   }

   private CommentList getComments(CommentList comments) {
      Token nextToken = this.getToken(1);
      if (this.lastCommentSourceToken != nextToken) {
         if (this.lastCommentSourceToken == null) {
            this.lastCommentSourceToken = this.token;
         }

         do {
            this.lastCommentSourceToken = this.lastCommentSourceToken.next;
            Token t = this.lastCommentSourceToken.specialToken;
            if (t != null) {
               while(t.specialToken != null) {
                  t = t.specialToken;
               }

               if (comments == null) {
                  comments = this.sb.makeCommentList();
               }

               while(t != null) {
                  String s = mungeComment(t.image);
                  Location loc = this.makeLocation(t);
                  if (t.next != null && t.next.kind == 44) {
                     StringBuffer buf = new StringBuffer(s);

                     do {
                        t = t.next;
                        buf.append('\n');
                        buf.append(mungeComment(t.image));
                     } while(t.next != null && t.next.kind == 44);

                     s = buf.toString();
                  }

                  comments.addComment(s, loc);
                  t = t.next;
               }
            }
         } while(this.lastCommentSourceToken != nextToken);
      }

      return comments;
   }

   private ParsedPattern afterComments(ParsedPattern p) {
      CommentList comments = this.getComments((CommentList)null);
      return comments == null ? p : this.sb.commentAfter(p, comments);
   }

   private ParsedNameClass afterComments(ParsedNameClass nc) {
      CommentList comments = this.getComments((CommentList)null);
      return comments == null ? nc : this.ncb.commentAfter(nc, comments);
   }

   private static String mungeComment(String image) {
      int i;
      for(i = image.indexOf(35) + 1; i < image.length() && image.charAt(i) == '#'; ++i) {
      }

      if (i < image.length() && image.charAt(i) == ' ') {
         ++i;
      }

      return image.substring(i);
   }

   private Annotations getCommentsAsAnnotations() {
      CommentList comments = this.getComments();
      return comments == null ? null : this.sb.makeAnnotations(comments, this.getContext());
   }

   private Annotations addCommentsToChildAnnotations(Annotations a) {
      CommentList comments = this.getComments();
      if (comments == null) {
         return a;
      } else {
         if (a == null) {
            a = this.sb.makeAnnotations((CommentList)null, this.getContext());
         }

         a.addComment(comments);
         return a;
      }
   }

   private Annotations addCommentsToLeadingAnnotations(Annotations a) {
      CommentList comments = this.getComments();
      if (comments == null) {
         return a;
      } else if (a == null) {
         return this.sb.makeAnnotations(comments, this.getContext());
      } else {
         a.addLeadingComment(comments);
         return a;
      }
   }

   private Annotations getTopLevelCommentsAsAnnotations() {
      CommentList comments = this.getTopLevelComments();
      return comments == null ? null : this.sb.makeAnnotations(comments, this.getContext());
   }

   private void clearAttributeList() {
      this.attributeNameTable.clear();
   }

   private void addAttribute(Annotations a, String ns, String localName, String prefix, String value, Token tok) {
      String key = ns + "#" + localName;
      if (this.attributeNameTable.get(key) != null) {
         this.error("duplicate_attribute", ns, localName, tok);
      } else {
         this.attributeNameTable.put(key, key);
         a.addAttribute(ns, localName, prefix, value, this.makeLocation(tok));
      }

   }

   private void checkExcept(Token[] except) {
      if (except[0] != null) {
         this.error("except_missing_parentheses", except[0]);
      }

   }

   private String lookupPrefix(String prefix, Token t) {
      String ns = (String)this.namespaceTable.get(prefix);
      if (ns == null) {
         this.error("undeclared_prefix", prefix, t);
         return "#error";
      } else {
         return ns;
      }
   }

   private String lookupDatatype(String prefix, Token t) {
      String ns = (String)this.datatypesTable.get(prefix);
      if (ns == null) {
         this.error("undeclared_prefix", prefix, t);
         return "";
      } else {
         return ns;
      }
   }

   private String resolve(String str) {
      try {
         return (new URL(new URL(this.sourceUri), str)).toString();
      } catch (MalformedURLException var3) {
         return str;
      }
   }

   public final ParsedPattern Input(Scope scope) throws ParseException {
      this.Preamble();
      ParsedPattern p;
      if (this.jj_2_1(Integer.MAX_VALUE)) {
         p = this.TopLevelGrammar(scope);
      } else {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1:
            case 10:
            case 17:
            case 18:
            case 19:
            case 26:
            case 27:
            case 28:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 40:
            case 43:
            case 54:
            case 55:
            case 57:
            case 58:
               p = this.Expr(true, scope, (Token)null, (Annotations)null);
               p = this.afterComments(p);
               this.jj_consume_token(0);
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 29:
            case 30:
            case 37:
            case 38:
            case 39:
            case 41:
            case 42:
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
            case 56:
            default:
               this.jj_la1[0] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }
      }

      return p;
   }

   public final void TopLevelLookahead() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 1:
            this.LookaheadBody();
            this.LookaheadAfterAnnotations();
            break;
         case 5:
         case 6:
         case 7:
            this.LookaheadGrammarKeyword();
            break;
         case 40:
         case 43:
            this.LookaheadDocumentation();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 1:
                  this.LookaheadBody();
                  break;
               default:
                  this.jj_la1[2] = this.jj_gen;
            }

            this.LookaheadAfterAnnotations();
            break;
         case 54:
         case 55:
            this.Identifier();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 1:
                  this.jj_consume_token(1);
                  return;
               case 2:
                  this.jj_consume_token(2);
                  return;
               case 3:
                  this.jj_consume_token(3);
                  return;
               case 4:
                  this.jj_consume_token(4);
                  return;
               default:
                  this.jj_la1[1] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }
         case 57:
            this.jj_consume_token(57);
            this.jj_consume_token(1);
            break;
         default:
            this.jj_la1[3] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final void LookaheadAfterAnnotations() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
            this.LookaheadGrammarKeyword();
            return;
         case 54:
         case 55:
            this.Identifier();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 2:
                  this.jj_consume_token(2);
                  return;
               case 3:
                  this.jj_consume_token(3);
                  return;
               case 4:
                  this.jj_consume_token(4);
                  return;
               default:
                  this.jj_la1[4] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }
         default:
            this.jj_la1[5] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }
   }

   public final void LookaheadGrammarKeyword() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
            this.jj_consume_token(5);
            break;
         case 6:
            this.jj_consume_token(6);
            break;
         case 7:
            this.jj_consume_token(7);
            break;
         default:
            this.jj_la1[6] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final void LookaheadDocumentation() throws ParseException {
      label37:
      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 40:
               this.jj_consume_token(40);
               break;
            case 43:
               this.jj_consume_token(43);
               break;
            default:
               this.jj_la1[7] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }

         while(true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 41:
                  this.jj_consume_token(41);
                  break;
               default:
                  this.jj_la1[8] = this.jj_gen;
                  switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                     case 40:
                     case 43:
                        continue label37;
                     default:
                        this.jj_la1[9] = this.jj_gen;
                        return;
                  }
            }
         }
      }
   }

   public final void LookaheadBody() throws ParseException {
      this.jj_consume_token(1);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 26:
            case 27:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 54:
            case 55:
            case 57:
            case 58:
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 1:
                     this.LookaheadBody();
                     continue;
                  case 2:
                     this.jj_consume_token(2);
                     continue;
                  case 3:
                  case 4:
                  case 9:
                  case 11:
                  case 12:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 28:
                  case 29:
                  case 30:
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
                  case 56:
                  default:
                     this.jj_la1[11] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
                  case 5:
                  case 6:
                  case 7:
                  case 10:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 26:
                  case 27:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 54:
                  case 55:
                     this.UnprefixedName();
                     continue;
                  case 8:
                     this.jj_consume_token(8);
                     continue;
                  case 57:
                     this.jj_consume_token(57);
                     continue;
                  case 58:
                     this.jj_consume_token(58);
                     continue;
               }
            case 3:
            case 4:
            case 9:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
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
            case 56:
            default:
               this.jj_la1[10] = this.jj_gen;
               this.jj_consume_token(9);
               return;
         }
      }
   }

   public final ParsedPattern IncludedGrammar(IncludedGrammar g) throws ParseException {
      this.Preamble();
      Annotations a;
      if (this.jj_2_2(Integer.MAX_VALUE)) {
         a = this.GrammarBody(g, g, this.getTopLevelCommentsAsAnnotations());
      } else {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1:
            case 10:
            case 40:
            case 43:
               a = this.Annotations();
               this.jj_consume_token(10);
               this.jj_consume_token(11);
               a = this.GrammarBody(g, g, a);
               this.topLevelComments(g);
               this.jj_consume_token(12);
               break;
            default:
               this.jj_la1[12] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }
      }

      ParsedPattern p = this.afterComments(g.endIncludedGrammar(this.sb.makeLocation(this.sourceUri, 1, 1), a));
      this.jj_consume_token(0);
      return p;
   }

   public final ParsedPattern TopLevelGrammar(Scope scope) throws ParseException {
      Annotations a = this.getTopLevelCommentsAsAnnotations();
      Grammar g = this.sb.makeGrammar(scope);
      a = this.GrammarBody(g, g, a);
      ParsedPattern p = this.afterComments(g.endGrammar(this.sb.makeLocation(this.sourceUri, 1, 1), a));
      this.jj_consume_token(0);
      return p;
   }

   public final void Preamble() throws ParseException {
      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
            case 14:
            case 16:
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 13:
                  case 14:
                     this.NamespaceDecl();
                     continue;
                  case 15:
                  default:
                     this.jj_la1[14] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
                  case 16:
                     this.DatatypesDecl();
                     continue;
               }
            case 15:
            default:
               this.jj_la1[13] = this.jj_gen;
               this.namespaceTable.put("xml", "http://www.w3.org/XML/1998/namespace");
               if (this.datatypesTable.get("xsd") == null) {
                  this.datatypesTable.put("xsd", "http://www.w3.org/2001/XMLSchema-datatypes");
               }

               return;
         }
      }
   }

   public final void NamespaceDecl() throws ParseException {
      LocatedString prefix;
      boolean isDefault;
      prefix = null;
      isDefault = false;
      this.noteTopLevelComments();
      label43:
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 13:
            this.jj_consume_token(13);
            prefix = this.UnprefixedName();
            break;
         case 14:
            this.jj_consume_token(14);
            isDefault = true;
            this.jj_consume_token(13);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 5:
               case 6:
               case 7:
               case 10:
               case 13:
               case 14:
               case 15:
               case 16:
               case 17:
               case 18:
               case 19:
               case 26:
               case 27:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
               case 36:
               case 54:
               case 55:
                  prefix = this.UnprefixedName();
                  break label43;
               case 8:
               case 9:
               case 11:
               case 12:
               case 20:
               case 21:
               case 22:
               case 23:
               case 24:
               case 25:
               case 28:
               case 29:
               case 30:
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
                  this.jj_la1[15] = this.jj_gen;
                  break label43;
            }
         default:
            this.jj_la1[16] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      this.jj_consume_token(2);
      String namespaceName = this.NamespaceName();
      if (isDefault) {
         this.defaultNamespace = namespaceName;
      }

      if (prefix != null) {
         if (prefix.getString().equals("xmlns")) {
            this.error("xmlns_prefix", prefix.getToken());
         } else if (prefix.getString().equals("xml")) {
            if (!namespaceName.equals("http://www.w3.org/XML/1998/namespace")) {
               this.error("xml_prefix_bad_uri", prefix.getToken());
            }
         } else if (namespaceName.equals("http://www.w3.org/XML/1998/namespace")) {
            this.error("xml_uri_bad_prefix", prefix.getToken());
         } else {
            if (namespaceName.equals("http://relaxng.org/ns/compatibility/annotations/1.0")) {
               this.compatibilityPrefix = prefix.getString();
            }

            this.namespaceTable.put(prefix.getString(), namespaceName);
         }
      }

   }

   public final String NamespaceName() throws ParseException {
      String r;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 15:
            this.jj_consume_token(15);
            r = this.inheritedNs;
            break;
         case 58:
            r = this.Literal();
            break;
         default:
            this.jj_la1[17] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      return r;
   }

   public final void DatatypesDecl() throws ParseException {
      this.noteTopLevelComments();
      this.jj_consume_token(16);
      LocatedString prefix = this.UnprefixedName();
      this.jj_consume_token(2);
      String uri = this.Literal();
      this.datatypesTable.put(prefix.getString(), uri);
   }

   public final ParsedPattern AnnotatedPrimaryExpr(boolean topLevel, Scope scope, Token[] except) throws ParseException {
      Annotations a = this.Annotations();
      ParsedPattern p = this.PrimaryExpr(topLevel, scope, a, except);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 59:
               Token t = this.jj_consume_token(59);
               ParsedElementAnnotation e = this.AnnotationElement(false);
               if (topLevel) {
                  this.error("top_level_follow_annotation", t);
               } else {
                  p = this.sb.annotateAfter(p, e);
               }
               break;
            default:
               this.jj_la1[18] = this.jj_gen;
               return p;
         }
      }
   }

   public final ParsedPattern PrimaryExpr(boolean topLevel, Scope scope, Annotations a, Token[] except) throws ParseException {
      ParsedPattern p;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 10:
            p = this.GrammarExpr(scope, a);
            break;
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 29:
         case 30:
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
         case 56:
         default:
            this.jj_la1[19] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 17:
            p = this.EmptyExpr(a);
            break;
         case 18:
            p = this.TextExpr(a);
            break;
         case 19:
            p = this.NotAllowedExpr(a);
            break;
         case 26:
            p = this.ElementExpr(scope, a);
            break;
         case 27:
            p = this.AttributeExpr(scope, a);
            break;
         case 28:
            p = this.ParenExpr(topLevel, scope, a);
            break;
         case 31:
            p = this.ListExpr(scope, a);
            break;
         case 32:
            p = this.MixedExpr(scope, a);
            break;
         case 33:
            p = this.ExternalRefExpr(scope, a);
            break;
         case 34:
            p = this.ParentExpr(scope, a);
            break;
         case 35:
         case 36:
         case 57:
            p = this.DataExpr(topLevel, scope, a, except);
            break;
         case 54:
         case 55:
            p = this.IdentifierExpr(scope, a);
            break;
         case 58:
            p = this.ValueExpr(topLevel, a);
      }

      return p;
   }

   public final ParsedPattern EmptyExpr(Annotations a) throws ParseException {
      Token t = this.jj_consume_token(17);
      return this.sb.makeEmpty(this.makeLocation(t), a);
   }

   public final ParsedPattern TextExpr(Annotations a) throws ParseException {
      Token t = this.jj_consume_token(18);
      return this.sb.makeText(this.makeLocation(t), a);
   }

   public final ParsedPattern NotAllowedExpr(Annotations a) throws ParseException {
      Token t = this.jj_consume_token(19);
      return this.sb.makeNotAllowed(this.makeLocation(t), a);
   }

   public final ParsedPattern Expr(boolean topLevel, Scope scope, Token t, Annotations a) throws ParseException {
      ArrayList patterns;
      ParsedPattern p;
      boolean[] hadOccur;
      patterns = new ArrayList();
      hadOccur = new boolean[1];
      Token[] except = new Token[1];
      p = this.UnaryExpr(topLevel, scope, hadOccur, except);
      patterns.add(p);
      label81:
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 20:
         case 21:
         case 22:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 20:
                  this.checkExcept(except);

                  while(true) {
                     t = this.jj_consume_token(20);
                     p = this.UnaryExpr(topLevel, scope, (boolean[])null, except);
                     patterns.add(p);
                     this.checkExcept(except);
                     switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 20:
                           break;
                        default:
                           this.jj_la1[20] = this.jj_gen;
                           p = this.sb.makeChoice(patterns, this.makeLocation(t), a);
                           break label81;
                     }
                  }
               case 21:
                  while(true) {
                     t = this.jj_consume_token(21);
                     p = this.UnaryExpr(topLevel, scope, (boolean[])null, except);
                     patterns.add(p);
                     this.checkExcept(except);
                     switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 21:
                           break;
                        default:
                           this.jj_la1[21] = this.jj_gen;
                           p = this.sb.makeInterleave(patterns, this.makeLocation(t), a);
                           break label81;
                     }
                  }
               case 22:
                  while(true) {
                     t = this.jj_consume_token(22);
                     p = this.UnaryExpr(topLevel, scope, (boolean[])null, except);
                     patterns.add(p);
                     this.checkExcept(except);
                     switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 22:
                           break;
                        default:
                           this.jj_la1[22] = this.jj_gen;
                           p = this.sb.makeGroup(patterns, this.makeLocation(t), a);
                           break label81;
                     }
                  }
               default:
                  this.jj_la1[23] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }
         default:
            this.jj_la1[24] = this.jj_gen;
      }

      if (patterns.size() == 1 && a != null) {
         if (hadOccur[0]) {
            p = this.sb.annotate(p, a);
         } else {
            p = this.sb.makeGroup(patterns, this.makeLocation(t), a);
         }
      }

      return p;
   }

   public final ParsedPattern UnaryExpr(boolean topLevel, Scope scope, boolean[] hadOccur, Token[] except) throws ParseException {
      ParsedPattern p = this.AnnotatedPrimaryExpr(topLevel, scope, except);
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 23:
         case 24:
         case 25:
            if (hadOccur != null) {
               hadOccur[0] = true;
            }

            p = this.afterComments(p);
            Token t;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 23:
                  t = this.jj_consume_token(23);
                  this.checkExcept(except);
                  p = this.sb.makeOneOrMore(p, this.makeLocation(t), (Annotations)null);
                  break;
               case 24:
                  t = this.jj_consume_token(24);
                  this.checkExcept(except);
                  p = this.sb.makeOptional(p, this.makeLocation(t), (Annotations)null);
                  break;
               case 25:
                  t = this.jj_consume_token(25);
                  this.checkExcept(except);
                  p = this.sb.makeZeroOrMore(p, this.makeLocation(t), (Annotations)null);
                  break;
               default:
                  this.jj_la1[25] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }

            while(true) {
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 59:
                     t = this.jj_consume_token(59);
                     ParsedElementAnnotation e = this.AnnotationElement(false);
                     if (topLevel) {
                        this.error("top_level_follow_annotation", t);
                     } else {
                        p = this.sb.annotateAfter(p, e);
                     }
                     break;
                  default:
                     this.jj_la1[26] = this.jj_gen;
                     return p;
               }
            }
         default:
            this.jj_la1[27] = this.jj_gen;
            return p;
      }
   }

   public final ParsedPattern ElementExpr(Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(26);
      ParsedNameClass nc = this.NameClass(0, (Annotations[])null);
      this.jj_consume_token(11);
      ParsedPattern p = this.Expr(false, scope, (Token)null, (Annotations)null);
      p = this.afterComments(p);
      this.jj_consume_token(12);
      return this.sb.makeElement(nc, p, this.makeLocation(t), a);
   }

   public final ParsedPattern AttributeExpr(Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(27);
      ParsedNameClass nc = this.NameClass(1, (Annotations[])null);
      this.jj_consume_token(11);
      ParsedPattern p = this.Expr(false, scope, (Token)null, (Annotations)null);
      p = this.afterComments(p);
      this.jj_consume_token(12);
      return this.sb.makeAttribute(nc, p, this.makeLocation(t), a);
   }

   public final ParsedNameClass NameClass(int context, Annotations[] pa) throws ParseException {
      Annotations a = this.Annotations();
      ParsedNameClass nc;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 28:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 54:
         case 55:
         case 57:
            nc = this.PrimaryNameClass(context, a);
            nc = this.AnnotateAfter(nc);
            nc = this.NameClassAlternatives(context, nc, pa);
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 29:
         case 30:
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
            this.jj_la1[28] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 25:
            nc = this.AnyNameExceptClass(context, a, pa);
            break;
         case 56:
            nc = this.NsNameExceptClass(context, a, pa);
      }

      return nc;
   }

   public final ParsedNameClass AnnotateAfter(ParsedNameClass nc) throws ParseException {
      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 59:
               this.jj_consume_token(59);
               ParsedElementAnnotation e = this.AnnotationElement(false);
               nc = this.ncb.annotateAfter(nc, e);
               break;
            default:
               this.jj_la1[29] = this.jj_gen;
               return nc;
         }
      }
   }

   public final ParsedNameClass NameClassAlternatives(int context, ParsedNameClass nc, Annotations[] pa) throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 20:
            ParsedNameClass[] nameClasses = new ParsedNameClass[]{nc, null};
            int nNameClasses = 1;

            while(true) {
               Token t = this.jj_consume_token(20);
               nc = this.BasicNameClass(context);
               nc = this.AnnotateAfter(nc);
               if (nNameClasses >= nameClasses.length) {
                  ParsedNameClass[] oldNameClasses = nameClasses;
                  nameClasses = new ParsedNameClass[nameClasses.length * 2];
                  System.arraycopy(oldNameClasses, 0, nameClasses, 0, oldNameClasses.length);
               }

               nameClasses[nNameClasses++] = nc;
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 20:
                     break;
                  default:
                     this.jj_la1[30] = this.jj_gen;
                     Annotations a;
                     if (pa == null) {
                        a = null;
                     } else {
                        a = pa[0];
                        pa[0] = null;
                     }

                     nc = this.ncb.makeChoice(Arrays.asList(nameClasses).subList(0, nNameClasses), this.makeLocation(t), a);
                     return nc;
               }
            }
         default:
            this.jj_la1[31] = this.jj_gen;
            return nc;
      }
   }

   public final ParsedNameClass BasicNameClass(int context) throws ParseException {
      Annotations a = this.Annotations();
      ParsedNameClass nc;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 28:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 54:
         case 55:
         case 57:
            nc = this.PrimaryNameClass(context, a);
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 29:
         case 30:
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
            this.jj_la1[32] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 25:
         case 56:
            nc = this.OpenNameClass(context, a);
      }

      return nc;
   }

   public final ParsedNameClass PrimaryNameClass(int context, Annotations a) throws ParseException {
      ParsedNameClass nc;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 54:
         case 55:
            nc = this.UnprefixedNameClass(context, a);
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 29:
         case 30:
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
         case 56:
         default:
            this.jj_la1[33] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 28:
            nc = this.ParenNameClass(context, a);
            break;
         case 57:
            nc = this.PrefixedNameClass(a);
      }

      return nc;
   }

   public final ParsedNameClass OpenNameClass(int context, Annotations a) throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 25:
            Token t = this.jj_consume_token(25);
            this.checkAnyName(context, t);
            return this.ncb.makeAnyName(this.makeLocation(t), a);
         case 56:
            LocatedString ns = this.NsName();
            this.checkNsName(context, ns);
            return this.ncb.makeNsName(ns.getString(), ns.getLocation(), a);
         default:
            this.jj_la1[34] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }
   }

   public final ParsedNameClass UnprefixedNameClass(int context, Annotations a) throws ParseException {
      LocatedString name = this.UnprefixedName();
      String ns;
      if ((context & 1) == 1) {
         ns = "";
      } else {
         ns = this.defaultNamespace;
      }

      return this.ncb.makeName(ns, name.getString(), (String)null, name.getLocation(), a);
   }

   public final ParsedNameClass PrefixedNameClass(Annotations a) throws ParseException {
      Token t = this.jj_consume_token(57);
      String qn = t.image;
      int colon = qn.indexOf(58);
      String prefix = qn.substring(0, colon);
      return this.ncb.makeName(this.lookupPrefix(prefix, t), qn.substring(colon + 1), prefix, this.makeLocation(t), a);
   }

   public final ParsedNameClass NsNameExceptClass(int context, Annotations a, Annotations[] pa) throws ParseException {
      LocatedString ns = this.NsName();
      this.checkNsName(context, ns);
      ParsedNameClass nc;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 30:
            nc = this.ExceptNameClass(context | 4);
            nc = this.ncb.makeNsName(ns.getString(), nc, ns.getLocation(), a);
            nc = this.AnnotateAfter(nc);
            break;
         default:
            this.jj_la1[35] = this.jj_gen;
            nc = this.ncb.makeNsName(ns.getString(), ns.getLocation(), a);
            nc = this.AnnotateAfter(nc);
            nc = this.NameClassAlternatives(context, nc, pa);
      }

      return nc;
   }

   public final LocatedString NsName() throws ParseException {
      Token t = this.jj_consume_token(56);
      String qn = t.image;
      String prefix = qn.substring(0, qn.length() - 2);
      return new LocatedString(this.lookupPrefix(prefix, t), t);
   }

   public final ParsedNameClass AnyNameExceptClass(int context, Annotations a, Annotations[] pa) throws ParseException {
      Token t = this.jj_consume_token(25);
      this.checkAnyName(context, t);
      ParsedNameClass nc;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 30:
            nc = this.ExceptNameClass(context | 2);
            nc = this.ncb.makeAnyName(nc, this.makeLocation(t), a);
            nc = this.AnnotateAfter(nc);
            break;
         default:
            this.jj_la1[36] = this.jj_gen;
            nc = this.ncb.makeAnyName(this.makeLocation(t), a);
            nc = this.AnnotateAfter(nc);
            nc = this.NameClassAlternatives(context, nc, pa);
      }

      return nc;
   }

   public final ParsedNameClass ParenNameClass(int context, Annotations a) throws ParseException {
      Annotations[] pa = new Annotations[]{a};
      Token t = this.jj_consume_token(28);
      ParsedNameClass nc = this.NameClass(context, pa);
      nc = this.afterComments(nc);
      this.jj_consume_token(29);
      if (pa[0] != null) {
         nc = this.ncb.makeChoice(Collections.singletonList(nc), this.makeLocation(t), pa[0]);
      }

      return nc;
   }

   public final ParsedNameClass ExceptNameClass(int context) throws ParseException {
      this.jj_consume_token(30);
      ParsedNameClass nc = this.BasicNameClass(context);
      return nc;
   }

   public final ParsedPattern ListExpr(Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(31);
      this.jj_consume_token(11);
      ParsedPattern p = this.Expr(false, scope, (Token)null, (Annotations)null);
      p = this.afterComments(p);
      this.jj_consume_token(12);
      return this.sb.makeList(p, this.makeLocation(t), a);
   }

   public final ParsedPattern MixedExpr(Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(32);
      this.jj_consume_token(11);
      ParsedPattern p = this.Expr(false, scope, (Token)null, (Annotations)null);
      p = this.afterComments(p);
      this.jj_consume_token(12);
      return this.sb.makeMixed(p, this.makeLocation(t), a);
   }

   public final ParsedPattern GrammarExpr(Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(10);
      Grammar g = this.sb.makeGrammar(scope);
      this.jj_consume_token(11);
      a = this.GrammarBody(g, g, a);
      this.topLevelComments(g);
      this.jj_consume_token(12);
      return g.endGrammar(this.makeLocation(t), a);
   }

   public final ParsedPattern ParenExpr(boolean topLevel, Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(28);
      ParsedPattern p = this.Expr(topLevel, scope, t, a);
      p = this.afterComments(p);
      this.jj_consume_token(29);
      return p;
   }

   public final Annotations GrammarBody(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      ParsedElementAnnotation e;
      for(; this.jj_2_3(2); a.addElement(e)) {
         e = this.AnnotationElementNotKeyword();
         if (a == null) {
            a = this.sb.makeAnnotations((CommentList)null, this.getContext());
         }
      }

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1:
            case 5:
            case 6:
            case 7:
            case 40:
            case 43:
            case 54:
            case 55:
               this.GrammarComponent(section, scope);
               break;
            default:
               this.jj_la1[37] = this.jj_gen;
               return a;
         }
      }
   }

   public final void GrammarComponent(GrammarSection section, Scope scope) throws ParseException {
      Annotations a = this.Annotations();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 54:
         case 55:
            this.Definition(section, scope, a);
            break;
         case 6:
            this.Div(section, scope, a);
            break;
         case 7:
            this.Include(section, scope, a);
            break;
         default:
            this.jj_la1[38] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      while(this.jj_2_4(2)) {
         ParsedElementAnnotation e = this.AnnotationElementNotKeyword();
         section.topLevelAnnotation(e);
      }

   }

   public final void Definition(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
            this.Start(section, scope, a);
            break;
         case 54:
         case 55:
            this.Define(section, scope, a);
            break;
         default:
            this.jj_la1[39] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final void Start(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(5);
      GrammarSection.Combine combine = this.AssignOp();
      ParsedPattern p = this.Expr(false, scope, (Token)null, (Annotations)null);
      section.define("\u0000#start\u0000", combine, p, this.makeLocation(t), a);
   }

   public final void Define(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      LocatedString name = this.Identifier();
      GrammarSection.Combine combine = this.AssignOp();
      ParsedPattern p = this.Expr(false, scope, (Token)null, (Annotations)null);
      section.define(name.getString(), combine, p, name.getLocation(), a);
   }

   public final GrammarSection.Combine AssignOp() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 2:
            this.jj_consume_token(2);
            return null;
         case 3:
            this.jj_consume_token(3);
            return GrammarSection.COMBINE_INTERLEAVE;
         case 4:
            this.jj_consume_token(4);
            return GrammarSection.COMBINE_CHOICE;
         default:
            this.jj_la1[40] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }
   }

   public final void Include(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      Include include = section.makeInclude();
      Token t = this.jj_consume_token(7);
      String href = this.Literal();
      String ns = this.Inherit();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 11:
            this.jj_consume_token(11);
            a = this.IncludeBody(include, scope, a);
            this.topLevelComments(include);
            this.jj_consume_token(12);
            break;
         default:
            this.jj_la1[41] = this.jj_gen;
      }

      try {
         include.endInclude(this.parseable, this.resolve(href), ns, this.makeLocation(t), a);
      } catch (IllegalSchemaException var9) {
      }

   }

   public final Annotations IncludeBody(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      ParsedElementAnnotation e;
      for(; this.jj_2_5(2); a.addElement(e)) {
         e = this.AnnotationElementNotKeyword();
         if (a == null) {
            a = this.sb.makeAnnotations((CommentList)null, this.getContext());
         }
      }

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1:
            case 5:
            case 6:
            case 40:
            case 43:
            case 54:
            case 55:
               this.IncludeComponent(section, scope);
               break;
            default:
               this.jj_la1[42] = this.jj_gen;
               return a;
         }
      }
   }

   public final void IncludeComponent(GrammarSection section, Scope scope) throws ParseException {
      Annotations a = this.Annotations();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 54:
         case 55:
            this.Definition(section, scope, a);
            break;
         case 6:
            this.IncludeDiv(section, scope, a);
            break;
         default:
            this.jj_la1[43] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      while(this.jj_2_6(2)) {
         ParsedElementAnnotation e = this.AnnotationElementNotKeyword();
         section.topLevelAnnotation(e);
      }

   }

   public final void Div(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      Div div = section.makeDiv();
      Token t = this.jj_consume_token(6);
      this.jj_consume_token(11);
      a = this.GrammarBody(div, scope, a);
      this.topLevelComments(div);
      this.jj_consume_token(12);
      div.endDiv(this.makeLocation(t), a);
   }

   public final void IncludeDiv(GrammarSection section, Scope scope, Annotations a) throws ParseException {
      Div div = section.makeDiv();
      Token t = this.jj_consume_token(6);
      this.jj_consume_token(11);
      a = this.IncludeBody(div, scope, a);
      this.topLevelComments(div);
      this.jj_consume_token(12);
      div.endDiv(this.makeLocation(t), a);
   }

   public final ParsedPattern ExternalRefExpr(Scope scope, Annotations a) throws ParseException {
      Token t = this.jj_consume_token(33);
      String href = this.Literal();
      String ns = this.Inherit();

      try {
         return this.sb.makeExternalRef(this.parseable, this.resolve(href), ns, scope, this.makeLocation(t), a);
      } catch (IllegalSchemaException var7) {
         return this.sb.makeErrorPattern();
      }
   }

   public final String Inherit() throws ParseException {
      String ns = null;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 15:
            this.jj_consume_token(15);
            this.jj_consume_token(2);
            ns = this.Prefix();
            break;
         default:
            this.jj_la1[44] = this.jj_gen;
      }

      if (ns == null) {
         ns = this.defaultNamespace;
      }

      return ns;
   }

   public final ParsedPattern ParentExpr(Scope scope, Annotations a) throws ParseException {
      this.jj_consume_token(34);
      a = this.addCommentsToChildAnnotations(a);
      LocatedString name = this.Identifier();
      if (scope == null) {
         this.error("parent_ref_outside_grammar", name.getToken());
         return this.sb.makeErrorPattern();
      } else {
         return scope.makeParentRef(name.getString(), name.getLocation(), a);
      }
   }

   public final ParsedPattern IdentifierExpr(Scope scope, Annotations a) throws ParseException {
      LocatedString name = this.Identifier();
      if (scope == null) {
         this.error("ref_outside_grammar", name.getToken());
         return this.sb.makeErrorPattern();
      } else {
         return scope.makeRef(name.getString(), name.getLocation(), a);
      }
   }

   public final ParsedPattern ValueExpr(boolean topLevel, Annotations a) throws ParseException {
      LocatedString s = this.LocatedLiteral();
      if (topLevel && this.annotationsIncludeElements) {
         this.error("top_level_follow_annotation", s.getToken());
         a = null;
      }

      return this.sb.makeValue("", "token", s.getString(), this.getContext(), this.defaultNamespace, s.getLocation(), a);
   }

   public final ParsedPattern DataExpr(boolean topLevel, Scope scope, Annotations a, Token[] except) throws ParseException {
      String datatypeUri = null;
      String s = null;
      ParsedPattern e = null;
      Token datatypeToken = this.DatatypeName();
      String datatype = datatypeToken.image;
      Location loc = this.makeLocation(datatypeToken);
      int colon = datatype.indexOf(58);
      if (colon < 0) {
         datatypeUri = "";
      } else {
         String prefix = datatype.substring(0, colon);
         datatypeUri = this.lookupDatatype(prefix, datatypeToken);
         datatype = datatype.substring(colon + 1);
      }

      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 58:
            s = this.Literal();
            if (topLevel && this.annotationsIncludeElements) {
               this.error("top_level_follow_annotation", datatypeToken);
               a = null;
            }

            return this.sb.makeValue(datatypeUri, datatype, s, this.getContext(), this.defaultNamespace, loc, a);
         default:
            this.jj_la1[48] = this.jj_gen;
            DataPatternBuilder dpb = this.sb.makeDataPatternBuilder(datatypeUri, datatype, loc);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 11:
                  this.Params(dpb);
                  switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                     case 30:
                        e = this.Except(scope, except);
                        return e == null ? dpb.makePattern(loc, a) : dpb.makePattern(e, loc, a);
                     default:
                        this.jj_la1[45] = this.jj_gen;
                        return e == null ? dpb.makePattern(loc, a) : dpb.makePattern(e, loc, a);
                  }
               default:
                  this.jj_la1[47] = this.jj_gen;
                  switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                     case 30:
                        e = this.Except(scope, except);
                        break;
                     default:
                        this.jj_la1[46] = this.jj_gen;
                  }
            }

            return e == null ? dpb.makePattern(loc, a) : dpb.makePattern(e, loc, a);
      }
   }

   public final Token DatatypeName() throws ParseException {
      Token t;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 35:
            t = this.jj_consume_token(35);
            break;
         case 36:
            t = this.jj_consume_token(36);
            break;
         case 57:
            t = this.jj_consume_token(57);
            break;
         default:
            this.jj_la1[49] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      return t;
   }

   public final LocatedString Identifier() throws ParseException {
      LocatedString s;
      Token t;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 54:
            t = this.jj_consume_token(54);
            s = new LocatedString(t.image, t);
            break;
         case 55:
            t = this.jj_consume_token(55);
            s = new LocatedString(t.image.substring(1), t);
            break;
         default:
            this.jj_la1[50] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      return s;
   }

   public final String Prefix() throws ParseException {
      Token t;
      String prefix;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
            t = this.Keyword();
            prefix = t.image;
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 28:
         case 29:
         case 30:
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
            this.jj_la1[51] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 54:
            t = this.jj_consume_token(54);
            prefix = t.image;
            break;
         case 55:
            t = this.jj_consume_token(55);
            prefix = t.image.substring(1);
      }

      return this.lookupPrefix(prefix, t);
   }

   public final LocatedString UnprefixedName() throws ParseException {
      LocatedString s;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
            Token t = this.Keyword();
            s = new LocatedString(t.image, t);
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 28:
         case 29:
         case 30:
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
            this.jj_la1[52] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 54:
         case 55:
            s = this.Identifier();
      }

      return s;
   }

   public final void Params(DataPatternBuilder dpb) throws ParseException {
      this.jj_consume_token(11);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1:
            case 5:
            case 6:
            case 7:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 26:
            case 27:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 40:
            case 43:
            case 54:
            case 55:
               this.Param(dpb);
               break;
            case 2:
            case 3:
            case 4:
            case 8:
            case 9:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
            case 37:
            case 38:
            case 39:
            case 41:
            case 42:
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
               this.jj_la1[53] = this.jj_gen;
               this.jj_consume_token(12);
               return;
         }
      }
   }

   public final void Param(DataPatternBuilder dpb) throws ParseException {
      Annotations a = this.Annotations();
      LocatedString name = this.UnprefixedName();
      this.jj_consume_token(2);
      a = this.addCommentsToLeadingAnnotations(a);
      String value = this.Literal();
      dpb.addParam(name.getString(), value, this.getContext(), this.defaultNamespace, name.getLocation(), a);
   }

   public final ParsedPattern Except(Scope scope, Token[] except) throws ParseException {
      Token[] innerExcept = new Token[1];
      Token t = this.jj_consume_token(30);
      Annotations a = this.Annotations();
      ParsedPattern p = this.PrimaryExpr(false, scope, a, innerExcept);
      this.checkExcept(innerExcept);
      except[0] = t;
      return p;
   }

   public final ParsedElementAnnotation Documentation() throws ParseException {
      CommentList comments = this.getComments();
      Token t;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 40:
            t = this.jj_consume_token(40);
            break;
         case 43:
            t = this.jj_consume_token(43);
            break;
         default:
            this.jj_la1[54] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      ElementAnnotationBuilder eab = this.sb.makeElementAnnotationBuilder("http://relaxng.org/ns/compatibility/annotations/1.0", "documentation", this.getCompatibilityPrefix(), this.makeLocation(t), comments, this.getContext());
      eab.addText(mungeComment(t.image), this.makeLocation(t), (CommentList)null);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 41:
               t = this.jj_consume_token(41);
               eab.addText("\n" + mungeComment(t.image), this.makeLocation(t), (CommentList)null);
               break;
            default:
               this.jj_la1[55] = this.jj_gen;
               return eab.makeElementAnnotation();
         }
      }
   }

   public final Annotations Annotations() throws ParseException {
      CommentList comments;
      Annotations a;
      ParsedElementAnnotation e;
      comments = this.getComments();
      a = null;
      label76:
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 40:
         case 43:
            a = this.sb.makeAnnotations(comments, this.getContext());

            while(true) {
               e = this.Documentation();
               a.addElement(e);
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 40:
                  case 43:
                     break;
                  default:
                     this.jj_la1[56] = this.jj_gen;
                     comments = this.getComments();
                     if (comments != null) {
                        a.addLeadingComment(comments);
                     }
                     break label76;
               }
            }
         default:
            this.jj_la1[57] = this.jj_gen;
      }

      label63:
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 1:
            this.jj_consume_token(1);
            if (a == null) {
               a = this.sb.makeAnnotations(comments, this.getContext());
            }

            this.clearAttributeList();
            this.annotationsIncludeElements = false;

            while(this.jj_2_7(2)) {
               this.PrefixedAnnotationAttribute(a, false);
            }

            while(true) {
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 5:
                  case 6:
                  case 7:
                  case 10:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 26:
                  case 27:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 54:
                  case 55:
                  case 57:
                     e = this.AnnotationElement(false);
                     a.addElement(e);
                     this.annotationsIncludeElements = true;
                     break;
                  case 8:
                  case 9:
                  case 11:
                  case 12:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 28:
                  case 29:
                  case 30:
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
                  case 56:
                  default:
                     this.jj_la1[58] = this.jj_gen;
                     a.addComment(this.getComments());
                     this.jj_consume_token(9);
                     break label63;
               }
            }
         default:
            this.jj_la1[59] = this.jj_gen;
      }

      if (a == null && comments != null) {
         a = this.sb.makeAnnotations(comments, this.getContext());
      }

      return a;
   }

   public final void AnnotationAttribute(Annotations a) throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 54:
         case 55:
            this.UnprefixedAnnotationAttribute(a);
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 28:
         case 29:
         case 30:
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
         case 56:
         default:
            this.jj_la1[60] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 57:
            this.PrefixedAnnotationAttribute(a, true);
      }

   }

   public final void PrefixedAnnotationAttribute(Annotations a, boolean nested) throws ParseException {
      Token t = this.jj_consume_token(57);
      this.jj_consume_token(2);
      String value = this.Literal();
      String qn = t.image;
      int colon = qn.indexOf(58);
      String prefix = qn.substring(0, colon);
      String ns = this.lookupPrefix(prefix, t);
      if (ns == this.inheritedNs) {
         this.error("inherited_annotation_namespace", t);
      } else if (ns.length() == 0 && !nested) {
         this.error("unqualified_annotation_attribute", t);
      } else if (ns.equals("http://relaxng.org/ns/structure/1.0") && !nested) {
         this.error("relax_ng_namespace", t);
      } else if (ns.equals("http://www.w3.org/2000/xmlns")) {
         this.error("xmlns_annotation_attribute_uri", t);
      } else {
         if (ns.length() == 0) {
            prefix = null;
         }

         this.addAttribute(a, ns, qn.substring(colon + 1), prefix, value, t);
      }

   }

   public final void UnprefixedAnnotationAttribute(Annotations a) throws ParseException {
      LocatedString name = this.UnprefixedName();
      this.jj_consume_token(2);
      String value = this.Literal();
      if (name.getString().equals("xmlns")) {
         this.error("xmlns_annotation_attribute", name.getToken());
      } else {
         this.addAttribute(a, "", name.getString(), (String)null, value, name.getToken());
      }

   }

   public final ParsedElementAnnotation AnnotationElement(boolean nested) throws ParseException {
      ParsedElementAnnotation a;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
         case 6:
         case 7:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 26:
         case 27:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 54:
         case 55:
            a = this.UnprefixedAnnotationElement();
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 28:
         case 29:
         case 30:
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
         case 56:
         default:
            this.jj_la1[61] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 57:
            a = this.PrefixedAnnotationElement(nested);
      }

      return a;
   }

   public final ParsedElementAnnotation AnnotationElementNotKeyword() throws ParseException {
      ParsedElementAnnotation a;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 54:
         case 55:
            a = this.IdentifierAnnotationElement();
            break;
         case 56:
         default:
            this.jj_la1[62] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 57:
            a = this.PrefixedAnnotationElement(false);
      }

      return a;
   }

   public final ParsedElementAnnotation PrefixedAnnotationElement(boolean nested) throws ParseException {
      CommentList comments = this.getComments();
      Token t = this.jj_consume_token(57);
      String qn = t.image;
      int colon = qn.indexOf(58);
      String prefix = qn.substring(0, colon);
      String ns = this.lookupPrefix(prefix, t);
      if (ns == this.inheritedNs) {
         this.error("inherited_annotation_namespace", t);
         ns = "";
      } else if (!nested && ns.equals("http://relaxng.org/ns/structure/1.0")) {
         this.error("relax_ng_namespace", t);
         ns = "";
      } else if (ns.length() == 0) {
         prefix = null;
      }

      ElementAnnotationBuilder eab = this.sb.makeElementAnnotationBuilder(ns, qn.substring(colon + 1), prefix, this.makeLocation(t), comments, this.getContext());
      this.AnnotationElementContent(eab);
      return eab.makeElementAnnotation();
   }

   public final ParsedElementAnnotation UnprefixedAnnotationElement() throws ParseException {
      CommentList comments = this.getComments();
      LocatedString name = this.UnprefixedName();
      ElementAnnotationBuilder eab = this.sb.makeElementAnnotationBuilder("", name.getString(), (String)null, name.getLocation(), comments, this.getContext());
      this.AnnotationElementContent(eab);
      return eab.makeElementAnnotation();
   }

   public final ParsedElementAnnotation IdentifierAnnotationElement() throws ParseException {
      CommentList comments = this.getComments();
      LocatedString name = this.Identifier();
      ElementAnnotationBuilder eab = this.sb.makeElementAnnotationBuilder("", name.getString(), (String)null, name.getLocation(), comments, this.getContext());
      this.AnnotationElementContent(eab);
      return eab.makeElementAnnotation();
   }

   public final void AnnotationElementContent(ElementAnnotationBuilder eab) throws ParseException {
      this.jj_consume_token(1);
      this.clearAttributeList();

      while(this.jj_2_8(2)) {
         this.AnnotationAttribute(eab);
      }

      label44:
      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 5:
            case 6:
            case 7:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 26:
            case 27:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 54:
            case 55:
            case 57:
            case 58:
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 5:
                  case 6:
                  case 7:
                  case 10:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 26:
                  case 27:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 54:
                  case 55:
                  case 57:
                     ParsedElementAnnotation e = this.AnnotationElement(true);
                     eab.addElement(e);
                     continue;
                  case 8:
                  case 9:
                  case 11:
                  case 12:
                  case 20:
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                  case 28:
                  case 29:
                  case 30:
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
                  case 56:
                  default:
                     this.jj_la1[65] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
                  case 58:
                     this.AnnotationElementLiteral(eab);

                     while(true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                           case 8:
                              this.jj_consume_token(8);
                              this.AnnotationElementLiteral(eab);
                              break;
                           default:
                              this.jj_la1[64] = this.jj_gen;
                              continue label44;
                        }
                     }
               }
            case 8:
            case 9:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
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
            case 56:
            default:
               this.jj_la1[63] = this.jj_gen;
               eab.addComment(this.getComments());
               this.jj_consume_token(9);
               return;
         }
      }
   }

   public final void AnnotationElementLiteral(ElementAnnotationBuilder eab) throws ParseException {
      CommentList comments = this.getComments();
      Token t = this.jj_consume_token(58);
      eab.addText(unquote(t.image), this.makeLocation(t), comments);
   }

   public final String Literal() throws ParseException {
      Token t = this.jj_consume_token(58);
      String s = unquote(t.image);
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 8:
            StringBuffer buf = new StringBuffer(s);

            while(true) {
               this.jj_consume_token(8);
               t = this.jj_consume_token(58);
               buf.append(unquote(t.image));
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 8:
                     break;
                  default:
                     this.jj_la1[66] = this.jj_gen;
                     s = buf.toString();
                     return s;
               }
            }
         default:
            this.jj_la1[67] = this.jj_gen;
            return s;
      }
   }

   public final LocatedString LocatedLiteral() throws ParseException {
      Token t = this.jj_consume_token(58);
      String s = unquote(t.image);
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 8:
            StringBuffer buf = new StringBuffer(s);

            while(true) {
               this.jj_consume_token(8);
               Token t2 = this.jj_consume_token(58);
               buf.append(unquote(t2.image));
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 8:
                     break;
                  default:
                     this.jj_la1[68] = this.jj_gen;
                     s = buf.toString();
                     return new LocatedString(s, t);
               }
            }
         default:
            this.jj_la1[69] = this.jj_gen;
            return new LocatedString(s, t);
      }
   }

   public final Token Keyword() throws ParseException {
      Token t;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
            t = this.jj_consume_token(5);
            break;
         case 6:
            t = this.jj_consume_token(6);
            break;
         case 7:
            t = this.jj_consume_token(7);
            break;
         case 8:
         case 9:
         case 11:
         case 12:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 28:
         case 29:
         case 30:
         default:
            this.jj_la1[70] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 10:
            t = this.jj_consume_token(10);
            break;
         case 13:
            t = this.jj_consume_token(13);
            break;
         case 14:
            t = this.jj_consume_token(14);
            break;
         case 15:
            t = this.jj_consume_token(15);
            break;
         case 16:
            t = this.jj_consume_token(16);
            break;
         case 17:
            t = this.jj_consume_token(17);
            break;
         case 18:
            t = this.jj_consume_token(18);
            break;
         case 19:
            t = this.jj_consume_token(19);
            break;
         case 26:
            t = this.jj_consume_token(26);
            break;
         case 27:
            t = this.jj_consume_token(27);
            break;
         case 31:
            t = this.jj_consume_token(31);
            break;
         case 32:
            t = this.jj_consume_token(32);
            break;
         case 33:
            t = this.jj_consume_token(33);
            break;
         case 34:
            t = this.jj_consume_token(34);
            break;
         case 35:
            t = this.jj_consume_token(35);
            break;
         case 36:
            t = this.jj_consume_token(36);
      }

      return t;
   }

   private boolean jj_2_1(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_1();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(0, xla);
      }

      return var3;
   }

   private boolean jj_2_2(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_2();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(1, xla);
      }

      return var3;
   }

   private boolean jj_2_3(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_3();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(2, xla);
      }

      return var3;
   }

   private boolean jj_2_4(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_4();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(3, xla);
      }

      return var3;
   }

   private boolean jj_2_5(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_5();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(4, xla);
      }

      return var3;
   }

   private boolean jj_2_6(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_6();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(5, xla);
      }

      return var3;
   }

   private boolean jj_2_7(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_7();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(6, xla);
      }

      return var3;
   }

   private boolean jj_2_8(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_8();
         return var2;
      } catch (LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(7, xla);
      }

      return var3;
   }

   private boolean jj_3R_43() {
      if (this.jj_scan_token(1)) {
         return true;
      } else {
         Token xsp;
         do {
            xsp = this.jj_scanpos;
         } while(!this.jj_3R_52());

         this.jj_scanpos = xsp;
         return this.jj_scan_token(9);
      }
   }

   private boolean jj_3R_51() {
      return this.jj_scan_token(55);
   }

   private boolean jj_3R_50() {
      return this.jj_scan_token(54);
   }

   private boolean jj_3R_41() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_50()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_51()) {
            return true;
         }
      }

      return false;
   }

   private boolean jj_3R_47() {
      if (this.jj_scan_token(57)) {
         return true;
      } else {
         return this.jj_3R_56();
      }
   }

   private boolean jj_3R_55() {
      Token xsp = this.jj_scanpos;
      if (this.jj_scan_token(40)) {
         this.jj_scanpos = xsp;
         if (this.jj_scan_token(43)) {
            return true;
         }
      }

      do {
         xsp = this.jj_scanpos;
      } while(!this.jj_scan_token(41));

      this.jj_scanpos = xsp;
      return false;
   }

   private boolean jj_3R_45() {
      if (this.jj_3R_55()) {
         return true;
      } else {
         Token xsp;
         do {
            xsp = this.jj_scanpos;
         } while(!this.jj_3R_55());

         this.jj_scanpos = xsp;
         return false;
      }
   }

   private boolean jj_3R_38() {
      return this.jj_3R_48();
   }

   private boolean jj_3R_42() {
      Token xsp = this.jj_scanpos;
      if (this.jj_scan_token(5)) {
         this.jj_scanpos = xsp;
         if (this.jj_scan_token(6)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(7)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean jj_3R_37() {
      return this.jj_3R_47();
   }

   private boolean jj_3R_54() {
      return this.jj_3R_42();
   }

   private boolean jj_3R_29() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_37()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_38()) {
            return true;
         }
      }

      return false;
   }

   private boolean jj_3R_44() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_53()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_54()) {
            return true;
         }
      }

      return false;
   }

   private boolean jj_3R_53() {
      if (this.jj_3R_41()) {
         return true;
      } else {
         Token xsp = this.jj_scanpos;
         if (this.jj_scan_token(2)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(3)) {
               this.jj_scanpos = xsp;
               if (this.jj_scan_token(4)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean jj_3R_36() {
      if (this.jj_3R_45()) {
         return true;
      } else {
         Token xsp = this.jj_scanpos;
         if (this.jj_3R_46()) {
            this.jj_scanpos = xsp;
         }

         return this.jj_3R_44();
      }
   }

   private boolean jj_3R_35() {
      if (this.jj_3R_43()) {
         return true;
      } else {
         return this.jj_3R_44();
      }
   }

   private boolean jj_3R_34() {
      return this.jj_3R_42();
   }

   private boolean jj_3R_33() {
      if (this.jj_3R_41()) {
         return true;
      } else {
         Token xsp = this.jj_scanpos;
         if (this.jj_scan_token(1)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(2)) {
               this.jj_scanpos = xsp;
               if (this.jj_scan_token(3)) {
                  this.jj_scanpos = xsp;
                  if (this.jj_scan_token(4)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private boolean jj_3_1() {
      return this.jj_3R_28();
   }

   private boolean jj_3R_32() {
      if (this.jj_scan_token(57)) {
         return true;
      } else {
         return this.jj_scan_token(1);
      }
   }

   private boolean jj_3R_28() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_32()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_33()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_34()) {
               this.jj_scanpos = xsp;
               if (this.jj_3R_35()) {
                  this.jj_scanpos = xsp;
                  if (this.jj_3R_36()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean jj_3R_59() {
      return this.jj_3R_43();
   }

   private boolean jj_3_8() {
      return this.jj_3R_31();
   }

   private boolean jj_3R_56() {
      return this.jj_scan_token(1);
   }

   private boolean jj_3R_49() {
      if (this.jj_3R_57()) {
         return true;
      } else {
         return this.jj_scan_token(2);
      }
   }

   private boolean jj_3R_40() {
      return this.jj_3R_49();
   }

   private boolean jj_3_4() {
      return this.jj_3R_29();
   }

   private boolean jj_3R_48() {
      if (this.jj_3R_41()) {
         return true;
      } else {
         return this.jj_3R_56();
      }
   }

   private boolean jj_3_3() {
      return this.jj_3R_29();
   }

   private boolean jj_3_6() {
      return this.jj_3R_29();
   }

   private boolean jj_3R_62() {
      Token xsp = this.jj_scanpos;
      if (this.jj_scan_token(26)) {
         this.jj_scanpos = xsp;
         if (this.jj_scan_token(27)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(13)) {
               this.jj_scanpos = xsp;
               if (this.jj_scan_token(31)) {
                  this.jj_scanpos = xsp;
                  if (this.jj_scan_token(32)) {
                     this.jj_scanpos = xsp;
                     if (this.jj_scan_token(10)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(17)) {
                           this.jj_scanpos = xsp;
                           if (this.jj_scan_token(18)) {
                              this.jj_scanpos = xsp;
                              if (this.jj_scan_token(34)) {
                                 this.jj_scanpos = xsp;
                                 if (this.jj_scan_token(33)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(19)) {
                                       this.jj_scanpos = xsp;
                                       if (this.jj_scan_token(5)) {
                                          this.jj_scanpos = xsp;
                                          if (this.jj_scan_token(7)) {
                                             this.jj_scanpos = xsp;
                                             if (this.jj_scan_token(14)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(15)) {
                                                   this.jj_scanpos = xsp;
                                                   if (this.jj_scan_token(35)) {
                                                      this.jj_scanpos = xsp;
                                                      if (this.jj_scan_token(36)) {
                                                         this.jj_scanpos = xsp;
                                                         if (this.jj_scan_token(16)) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_scan_token(6)) {
                                                               return true;
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean jj_3R_61() {
      return this.jj_3R_62();
   }

   private boolean jj_3_2() {
      return this.jj_3R_28();
   }

   private boolean jj_3R_30() {
      if (this.jj_scan_token(57)) {
         return true;
      } else {
         return this.jj_scan_token(2);
      }
   }

   private boolean jj_3R_60() {
      return this.jj_3R_41();
   }

   private boolean jj_3R_58() {
      return this.jj_3R_57();
   }

   private boolean jj_3R_57() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_60()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_61()) {
            return true;
         }
      }

      return false;
   }

   private boolean jj_3_5() {
      return this.jj_3R_29();
   }

   private boolean jj_3R_31() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_39()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_40()) {
            return true;
         }
      }

      return false;
   }

   private boolean jj_3R_39() {
      return this.jj_3R_30();
   }

   private boolean jj_3_7() {
      return this.jj_3R_30();
   }

   private boolean jj_3R_46() {
      return this.jj_3R_43();
   }

   private boolean jj_3R_52() {
      Token xsp = this.jj_scanpos;
      if (this.jj_scan_token(57)) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_58()) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(2)) {
               this.jj_scanpos = xsp;
               if (this.jj_scan_token(58)) {
                  this.jj_scanpos = xsp;
                  if (this.jj_scan_token(8)) {
                     this.jj_scanpos = xsp;
                     if (this.jj_3R_59()) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private static void jj_la1_init_0() {
      jj_la1_0 = new int[]{-1676803070, 30, 2, 226, 28, 224, 224, 0, 0, 0, -1945115162, -1945115162, 1026, 90112, 90112, -1945115424, 24576, 32768, 0, -1676803072, 1048576, 2097152, 4194304, 7340032, 7340032, 58720256, 0, 58720256, -1643125536, 0, 1048576, 1048576, -1643125536, -1676679968, 33554432, 1073741824, 1073741824, 226, 224, 32, 28, 2048, 98, 96, 32768, 1073741824, 1073741824, 2048, 0, 0, 0, -1945115424, -1945115424, -1945115422, 0, 0, 0, 0, -1945115424, 2, -1945115424, -1945115424, 0, -1945115424, 256, -1945115424, 256, 256, 256, 256, -1945115424};
   }

   private static void jj_la1_init_1() {
      jj_la1_1 = new int[]{113248543, 0, 0, 46139648, 0, 12582912, 0, 2304, 512, 2304, 113246239, 113246239, 2304, 0, 0, 12582943, 0, 67108864, 134217728, 113246239, 0, 0, 0, 0, 0, 0, 134217728, 0, 62914591, 134217728, 0, 0, 62914591, 46137375, 16777216, 0, 0, 12585216, 12582912, 12582912, 0, 0, 12585216, 12582912, 0, 0, 0, 0, 67108864, 33554456, 12582912, 12582943, 12582943, 12585247, 2304, 512, 2304, 2304, 46137375, 0, 46137375, 46137375, 46137344, 113246239, 0, 113246239, 0, 0, 0, 0, 31};
   }

   public CompactSyntax(InputStream stream) {
      this(stream, (String)null);
   }

   public CompactSyntax(InputStream stream, String encoding) {
      this.compatibilityPrefix = null;
      this.namespaceTable = new Hashtable();
      this.datatypesTable = new Hashtable();
      this.hadError = false;
      this.attributeNameTable = new Hashtable();
      this.annotationsIncludeElements = false;
      this.lastCommentSourceToken = null;
      this.jj_la1 = new int[71];
      this.jj_2_rtns = new JJCalls[8];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];

      try {
         this.jj_input_stream = new JavaCharStream(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source = new CompactSyntaxTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 71; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public void ReInit(InputStream stream) {
      this.ReInit(stream, (String)null);
   }

   public void ReInit(InputStream stream, String encoding) {
      try {
         this.jj_input_stream.ReInit(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 71; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public CompactSyntax(Reader stream) {
      this.compatibilityPrefix = null;
      this.namespaceTable = new Hashtable();
      this.datatypesTable = new Hashtable();
      this.hadError = false;
      this.attributeNameTable = new Hashtable();
      this.annotationsIncludeElements = false;
      this.lastCommentSourceToken = null;
      this.jj_la1 = new int[71];
      this.jj_2_rtns = new JJCalls[8];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];
      this.jj_input_stream = new JavaCharStream(stream, 1, 1);
      this.token_source = new CompactSyntaxTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 71; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public void ReInit(Reader stream) {
      this.jj_input_stream.ReInit((Reader)stream, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 71; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public CompactSyntax(CompactSyntaxTokenManager tm) {
      this.compatibilityPrefix = null;
      this.namespaceTable = new Hashtable();
      this.datatypesTable = new Hashtable();
      this.hadError = false;
      this.attributeNameTable = new Hashtable();
      this.annotationsIncludeElements = false;
      this.lastCommentSourceToken = null;
      this.jj_la1 = new int[71];
      this.jj_2_rtns = new JJCalls[8];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 71; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   public void ReInit(CompactSyntaxTokenManager tm) {
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 71; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new JJCalls();
      }

   }

   private Token jj_consume_token(int kind) throws ParseException {
      Token oldToken;
      if ((oldToken = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind != kind) {
         this.token = oldToken;
         this.jj_kind = kind;
         throw this.generateParseException();
      } else {
         ++this.jj_gen;
         if (++this.jj_gc > 100) {
            this.jj_gc = 0;

            for(int i = 0; i < this.jj_2_rtns.length; ++i) {
               for(JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                  if (c.gen < this.jj_gen) {
                     c.first = null;
                  }
               }
            }
         }

         return this.token;
      }
   }

   private boolean jj_scan_token(int kind) {
      if (this.jj_scanpos == this.jj_lastpos) {
         --this.jj_la;
         if (this.jj_scanpos.next == null) {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
         } else {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
         }
      } else {
         this.jj_scanpos = this.jj_scanpos.next;
      }

      if (this.jj_rescan) {
         int i = 0;

         Token tok;
         for(tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
            ++i;
         }

         if (tok != null) {
            this.jj_add_error_token(kind, i);
         }
      }

      if (this.jj_scanpos.kind != kind) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         throw this.jj_ls;
      } else {
         return false;
      }
   }

   public final Token getNextToken() {
      if (this.token.next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      ++this.jj_gen;
      return this.token;
   }

   public final Token getToken(int index) {
      Token t = this.token;

      for(int i = 0; i < index; ++i) {
         if (t.next != null) {
            t = t.next;
         } else {
            t = t.next = this.token_source.getNextToken();
         }
      }

      return t;
   }

   private int jj_ntk() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   private void jj_add_error_token(int kind, int pos) {
      if (pos < 100) {
         if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
         } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];

            for(int i = 0; i < this.jj_endpos; ++i) {
               this.jj_expentry[i] = this.jj_lasttokens[i];
            }

            Iterator it = this.jj_expentries.iterator();

            label41:
            while(true) {
               int[] oldentry;
               do {
                  if (!it.hasNext()) {
                     break label41;
                  }

                  oldentry = (int[])((int[])it.next());
               } while(oldentry.length != this.jj_expentry.length);

               for(int i = 0; i < this.jj_expentry.length; ++i) {
                  if (oldentry[i] != this.jj_expentry[i]) {
                     continue label41;
                  }
               }

               this.jj_expentries.add(this.jj_expentry);
               break;
            }

            if (pos != 0) {
               this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
         }

      }
   }

   public ParseException generateParseException() {
      this.jj_expentries.clear();
      boolean[] la1tokens = new boolean[61];
      if (this.jj_kind >= 0) {
         la1tokens[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int i;
      int j;
      for(i = 0; i < 71; ++i) {
         if (this.jj_la1[i] == this.jj_gen) {
            for(j = 0; j < 32; ++j) {
               if ((jj_la1_0[i] & 1 << j) != 0) {
                  la1tokens[j] = true;
               }

               if ((jj_la1_1[i] & 1 << j) != 0) {
                  la1tokens[32 + j] = true;
               }
            }
         }
      }

      for(i = 0; i < 61; ++i) {
         if (la1tokens[i]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
         }
      }

      this.jj_endpos = 0;
      this.jj_rescan_token();
      this.jj_add_error_token(0, 0);
      int[][] exptokseq = new int[this.jj_expentries.size()][];

      for(j = 0; j < this.jj_expentries.size(); ++j) {
         exptokseq[j] = (int[])this.jj_expentries.get(j);
      }

      return new ParseException(this.token, exptokseq, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   private void jj_rescan_token() {
      this.jj_rescan = true;

      for(int i = 0; i < 8; ++i) {
         try {
            JJCalls p = this.jj_2_rtns[i];

            do {
               if (p.gen > this.jj_gen) {
                  this.jj_la = p.arg;
                  this.jj_lastpos = this.jj_scanpos = p.first;
                  switch (i) {
                     case 0:
                        this.jj_3_1();
                        break;
                     case 1:
                        this.jj_3_2();
                        break;
                     case 2:
                        this.jj_3_3();
                        break;
                     case 3:
                        this.jj_3_4();
                        break;
                     case 4:
                        this.jj_3_5();
                        break;
                     case 5:
                        this.jj_3_6();
                        break;
                     case 6:
                        this.jj_3_7();
                        break;
                     case 7:
                        this.jj_3_8();
                  }
               }

               p = p.next;
            } while(p != null);
         } catch (LookaheadSuccess var3) {
         }
      }

      this.jj_rescan = false;
   }

   private void jj_save(int index, int xla) {
      JJCalls p;
      for(p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
         if (p.next == null) {
            p = p.next = new JJCalls();
            break;
         }
      }

      p.gen = this.jj_gen + xla - this.jj_la;
      p.first = this.token;
      p.arg = xla;
   }

   static {
      jj_la1_init_0();
      jj_la1_init_1();
   }

   static final class JJCalls {
      int gen;
      Token first;
      int arg;
      JJCalls next;
   }

   private static final class LookaheadSuccess extends Error {
      private LookaheadSuccess() {
      }

      // $FF: synthetic method
      LookaheadSuccess(Object x0) {
         this();
      }
   }

   final class LocatedString {
      private final String str;
      private final Token tok;

      LocatedString(String str, Token tok) {
         this.str = str;
         this.tok = tok;
      }

      String getString() {
         return this.str;
      }

      Location getLocation() {
         return CompactSyntax.this.makeLocation(this.tok);
      }

      Token getToken() {
         return this.tok;
      }
   }
}
