package com.sun.xml.internal.dtdparser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DTDParser {
   public static final String TYPE_CDATA = "CDATA";
   public static final String TYPE_ID = "ID";
   public static final String TYPE_IDREF = "IDREF";
   public static final String TYPE_IDREFS = "IDREFS";
   public static final String TYPE_ENTITY = "ENTITY";
   public static final String TYPE_ENTITIES = "ENTITIES";
   public static final String TYPE_NMTOKEN = "NMTOKEN";
   public static final String TYPE_NMTOKENS = "NMTOKENS";
   public static final String TYPE_NOTATION = "NOTATION";
   public static final String TYPE_ENUMERATION = "ENUMERATION";
   private InputEntity in;
   private StringBuffer strTmp;
   private char[] nameTmp;
   private NameCache nameCache;
   private char[] charTmp = new char[2];
   private boolean doLexicalPE;
   protected final Set declaredElements = new HashSet();
   private SimpleHashtable params = new SimpleHashtable(7);
   Hashtable notations = new Hashtable(7);
   SimpleHashtable entities = new SimpleHashtable(17);
   private SimpleHashtable ids = new SimpleHashtable();
   private DTDEventListener dtdHandler;
   private EntityResolver resolver;
   private Locale locale;
   static final String strANY = "ANY";
   static final String strEMPTY = "EMPTY";
   private static final String XmlLang = "xml:lang";
   static final Catalog messages = new Catalog();

   public void setLocale(Locale l) throws SAXException {
      if (l != null && !messages.isLocaleSupported(l.toString())) {
         throw new SAXException(messages.getMessage(this.locale, "P-078", new Object[]{l}));
      } else {
         this.locale = l;
      }
   }

   public Locale getLocale() {
      return this.locale;
   }

   public Locale chooseLocale(String[] languages) throws SAXException {
      Locale l = messages.chooseLocale(languages);
      if (l != null) {
         this.setLocale(l);
      }

      return l;
   }

   public void setEntityResolver(EntityResolver r) {
      this.resolver = r;
   }

   public EntityResolver getEntityResolver() {
      return this.resolver;
   }

   public void setDtdHandler(DTDEventListener handler) {
      this.dtdHandler = handler;
      if (handler != null) {
         handler.setDocumentLocator(new Locator() {
            public String getPublicId() {
               return DTDParser.this.getPublicId();
            }

            public String getSystemId() {
               return DTDParser.this.getSystemId();
            }

            public int getLineNumber() {
               return DTDParser.this.getLineNumber();
            }

            public int getColumnNumber() {
               return DTDParser.this.getColumnNumber();
            }
         });
      }

   }

   public DTDEventListener getDtdHandler() {
      return this.dtdHandler;
   }

   public void parse(InputSource in) throws IOException, SAXException {
      this.init();
      this.parseInternal(in);
   }

   public void parse(String uri) throws IOException, SAXException {
      this.init();
      InputSource in = this.resolver.resolveEntity((String)null, uri);
      if (in == null) {
         in = Resolver.createInputSource(new URL(uri), false);
      } else if (in.getSystemId() == null) {
         this.warning("P-065", (Object[])null);
         in.setSystemId(uri);
      }

      this.parseInternal(in);
   }

   private void init() {
      this.in = null;
      this.strTmp = new StringBuffer();
      this.nameTmp = new char[20];
      this.nameCache = new NameCache();
      this.doLexicalPE = false;
      this.entities.clear();
      this.notations.clear();
      this.params.clear();
      this.declaredElements.clear();
      this.builtin("amp", "&#38;");
      this.builtin("lt", "&#60;");
      this.builtin("gt", ">");
      this.builtin("quot", "\"");
      this.builtin("apos", "'");
      if (this.locale == null) {
         this.locale = Locale.getDefault();
      }

      if (this.resolver == null) {
         this.resolver = new Resolver();
      }

      if (this.dtdHandler == null) {
         this.dtdHandler = new DTDHandlerBase();
      }

   }

   private void builtin(String entityName, String entityValue) {
      InternalEntity entity = new InternalEntity(entityName, entityValue.toCharArray());
      this.entities.put(entityName, entity);
   }

   private void parseInternal(InputSource input) throws IOException, SAXException {
      if (input == null) {
         this.fatal("P-000");
      }

      try {
         this.in = InputEntity.getInputEntity(this.dtdHandler, this.locale);
         this.in.init((InputSource)input, (String)null, (InputEntity)null, false);
         this.dtdHandler.startDTD(this.in);
         ExternalEntity externalSubset = new ExternalEntity(this.in);
         this.externalParameterEntity(externalSubset);
         if (!this.in.isEOF()) {
            this.fatal("P-001", new Object[]{Integer.toHexString(this.getc())});
         }

         this.afterRoot();
         this.dtdHandler.endDTD();
      } catch (EndOfInputException var8) {
         if (!this.in.isDocument()) {
            String name = this.in.getName();

            do {
               this.in = this.in.pop();
            } while(this.in.isInternal());

            this.fatal("P-002", new Object[]{name});
         } else {
            this.fatal("P-003", (Object[])null);
         }
      } catch (RuntimeException var9) {
         System.err.print("Internal DTD parser error: ");
         var9.printStackTrace();
         throw new SAXParseException(var9.getMessage() != null ? var9.getMessage() : var9.getClass().getName(), this.getPublicId(), this.getSystemId(), this.getLineNumber(), this.getColumnNumber());
      } finally {
         this.strTmp = null;
         this.nameTmp = null;
         this.nameCache = null;
         if (this.in != null) {
            this.in.close();
            this.in = null;
         }

         this.params.clear();
         this.entities.clear();
         this.notations.clear();
         this.declaredElements.clear();
         this.ids.clear();
      }

   }

   void afterRoot() throws SAXException {
      Enumeration e = this.ids.keys();

      while(e.hasMoreElements()) {
         String id = (String)e.nextElement();
         Boolean value = (Boolean)this.ids.get(id);
         if (Boolean.FALSE == value) {
            this.error("V-024", new Object[]{id});
         }
      }

   }

   private void whitespace(String roleId) throws IOException, SAXException {
      if (!this.maybeWhitespace()) {
         this.fatal("P-004", new Object[]{messages.getMessage(this.locale, roleId)});
      }

   }

   private boolean maybeWhitespace() throws IOException, SAXException {
      if (!this.doLexicalPE) {
         return this.in.maybeWhitespace();
      } else {
         char c = this.getc();

         boolean saw;
         for(saw = false; c == ' ' || c == '\t' || c == '\n' || c == '\r'; c = this.getc()) {
            saw = true;
            if (this.in.isEOF() && !this.in.isInternal()) {
               return saw;
            }
         }

         this.ungetc();
         return saw;
      }
   }

   private String maybeGetName() throws IOException, SAXException {
      NameCacheEntry entry = this.maybeGetNameCacheEntry();
      return entry == null ? null : entry.name;
   }

   private NameCacheEntry maybeGetNameCacheEntry() throws IOException, SAXException {
      char c = this.getc();
      if (!XmlChars.isLetter(c) && c != ':' && c != '_') {
         this.ungetc();
         return null;
      } else {
         return this.nameCharString(c);
      }
   }

   private String getNmtoken() throws IOException, SAXException {
      char c = this.getc();
      if (!XmlChars.isNameChar(c)) {
         this.fatal("P-006", new Object[]{new Character(c)});
      }

      return this.nameCharString(c).name;
   }

   private NameCacheEntry nameCharString(char c) throws IOException, SAXException {
      int i = 1;

      for(this.nameTmp[0] = c; (c = this.in.getNameChar()) != 0; this.nameTmp[i++] = c) {
         if (i >= this.nameTmp.length) {
            char[] tmp = new char[this.nameTmp.length + 10];
            System.arraycopy(this.nameTmp, 0, tmp, 0, this.nameTmp.length);
            this.nameTmp = tmp;
         }
      }

      return this.nameCache.lookupEntry(this.nameTmp, i);
   }

   private void parseLiteral(boolean isEntityValue) throws IOException, SAXException {
      char quote = this.getc();
      InputEntity source = this.in;
      if (quote != '\'' && quote != '"') {
         this.fatal("P-007");
      }

      this.strTmp = new StringBuffer();

      while(true) {
         while(this.in == source || !this.in.isEOF()) {
            char c;
            if ((c = this.getc()) == quote && this.in == source) {
               return;
            }

            String entityName;
            if (c == '&') {
               entityName = this.maybeGetName();
               if (entityName != null) {
                  this.nextChar(';', "F-020", entityName);
                  if (isEntityValue) {
                     this.strTmp.append('&');
                     this.strTmp.append(entityName);
                     this.strTmp.append(';');
                  } else {
                     this.expandEntityInLiteral(entityName, this.entities, isEntityValue);
                  }
               } else if (this.getc() == '#') {
                  int tmp = this.parseCharNumber();
                  if (tmp > 65535) {
                     tmp = this.surrogatesToCharTmp(tmp);
                     this.strTmp.append(this.charTmp[0]);
                     if (tmp == 2) {
                        this.strTmp.append(this.charTmp[1]);
                     }
                  } else {
                     this.strTmp.append((char)tmp);
                  }
               } else {
                  this.fatal("P-009");
               }
            } else {
               if (c == '%' && isEntityValue) {
                  entityName = this.maybeGetName();
                  if (entityName != null) {
                     this.nextChar(';', "F-021", entityName);
                     this.expandEntityInLiteral(entityName, this.params, isEntityValue);
                     continue;
                  }

                  this.fatal("P-011");
               }

               if (!isEntityValue) {
                  if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                     this.strTmp.append(' ');
                     continue;
                  }

                  if (c == '<') {
                     this.fatal("P-012");
                  }
               }

               this.strTmp.append(c);
            }
         }

         this.in = this.in.pop();
      }
   }

   private void expandEntityInLiteral(String name, SimpleHashtable table, boolean isEntityValue) throws IOException, SAXException {
      Object entity = table.get(name);
      if (entity instanceof InternalEntity) {
         InternalEntity value = (InternalEntity)entity;
         this.pushReader(value.buf, name, !value.isPE);
      } else if (entity instanceof ExternalEntity) {
         if (!isEntityValue) {
            this.fatal("P-013", new Object[]{name});
         }

         this.pushReader((ExternalEntity)entity);
      } else if (entity == null) {
         this.fatal(table == this.params ? "V-022" : "P-014", new Object[]{name});
      }

   }

   private String getQuotedString(String type, String extra) throws IOException, SAXException {
      char quote = this.in.getc();
      if (quote != '\'' && quote != '"') {
         this.fatal("P-015", new Object[]{messages.getMessage(this.locale, type, new Object[]{extra})});
      }

      this.strTmp = new StringBuffer();

      char c;
      while((c = this.in.getc()) != quote) {
         this.strTmp.append(c);
      }

      return this.strTmp.toString();
   }

   private String parsePublicId() throws IOException, SAXException {
      String retval = this.getQuotedString("F-033", (String)null);

      for(int i = 0; i < retval.length(); ++i) {
         char c = retval.charAt(i);
         if (" \r\n-'()+,./:=?;!*#@$_%0123456789".indexOf(c) == -1 && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
            this.fatal("P-016", new Object[]{new Character(c)});
         }
      }

      this.strTmp = new StringBuffer();
      this.strTmp.append(retval);
      return this.normalize(false);
   }

   private boolean maybeComment(boolean skipStart) throws IOException, SAXException {
      if (!this.in.peek(skipStart ? "!--" : "<!--", (char[])null)) {
         return false;
      } else {
         boolean savedLexicalPE = this.doLexicalPE;
         this.doLexicalPE = false;
         boolean saveCommentText = false;
         if (saveCommentText) {
            this.strTmp = new StringBuffer();
         }

         label56:
         while(true) {
            try {
               while(true) {
                  while(true) {
                     int c = this.getc();
                     if (c == '-') {
                        c = this.getc();
                        if (c == '-') {
                           this.nextChar('>', "F-022", (String)null);
                           break label56;
                        }

                        if (saveCommentText) {
                           this.strTmp.append('-');
                        }

                        this.ungetc();
                     } else if (saveCommentText) {
                        this.strTmp.append((char)c);
                     }
                  }
               }
            } catch (EndOfInputException var5) {
               if (this.in.isInternal()) {
                  this.error("V-021", (Object[])null);
               }

               this.fatal("P-017");
            }
         }

         this.doLexicalPE = savedLexicalPE;
         if (saveCommentText) {
            this.dtdHandler.comment(this.strTmp.toString());
         }

         return true;
      }
   }

   private boolean maybePI(boolean skipStart) throws IOException, SAXException {
      boolean savedLexicalPE = this.doLexicalPE;
      if (!this.in.peek(skipStart ? "?" : "<?", (char[])null)) {
         return false;
      } else {
         this.doLexicalPE = false;
         String target = this.maybeGetName();
         if (target == null) {
            this.fatal("P-018");
         }

         if ("xml".equals(target)) {
            this.fatal("P-019");
         }

         if ("xml".equalsIgnoreCase(target)) {
            this.fatal("P-020", new Object[]{target});
         }

         if (this.maybeWhitespace()) {
            this.strTmp = new StringBuffer();

            try {
               while(true) {
                  char c = this.in.getc();
                  if (c == '?' && this.in.peekc('>')) {
                     break;
                  }

                  this.strTmp.append(c);
               }
            } catch (EndOfInputException var5) {
               this.fatal("P-021");
            }

            this.dtdHandler.processingInstruction(target, this.strTmp.toString());
         } else {
            if (!this.in.peek("?>", (char[])null)) {
               this.fatal("P-022");
            }

            this.dtdHandler.processingInstruction(target, "");
         }

         this.doLexicalPE = savedLexicalPE;
         return true;
      }
   }

   private String maybeReadAttribute(String name, boolean must) throws IOException, SAXException {
      if (!this.maybeWhitespace()) {
         if (!must) {
            return null;
         }

         this.fatal("P-024", new Object[]{name});
      }

      if (!this.peek(name)) {
         if (!must) {
            this.ungetc();
            return null;
         }

         this.fatal("P-024", new Object[]{name});
      }

      this.maybeWhitespace();
      this.nextChar('=', "F-023", (String)null);
      this.maybeWhitespace();
      return this.getQuotedString("F-035", name);
   }

   private void readVersion(boolean must, String versionNum) throws IOException, SAXException {
      String value = this.maybeReadAttribute("version", must);
      if (must && value == null) {
         this.fatal("P-025", new Object[]{versionNum});
      }

      if (value != null) {
         int length = value.length();

         for(int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            if ((c < '0' || c > '9') && c != '_' && c != '.' && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != ':' && c != '-') {
               this.fatal("P-026", new Object[]{value});
            }
         }
      }

      if (value != null && !value.equals(versionNum)) {
         this.error("P-027", new Object[]{versionNum, value});
      }

   }

   private String getMarkupDeclname(String roleId, boolean qname) throws IOException, SAXException {
      this.whitespace(roleId);
      String name = this.maybeGetName();
      if (name == null) {
         this.fatal("P-005", new Object[]{messages.getMessage(this.locale, roleId)});
      }

      return name;
   }

   private boolean maybeMarkupDecl() throws IOException, SAXException {
      return this.maybeElementDecl() || this.maybeAttlistDecl() || this.maybeEntityDecl() || this.maybeNotationDecl() || this.maybePI(false) || this.maybeComment(false);
   }

   private boolean isXmlLang(String value) {
      if (value.length() < 2) {
         return false;
      } else {
         char c = value.charAt(1);
         int nextSuffix;
         if (c == '-') {
            c = value.charAt(0);
            if (c != 'i' && c != 'I' && c != 'x' && c != 'X') {
               return false;
            }

            nextSuffix = 1;
         } else {
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
               return false;
            }

            c = value.charAt(0);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
               return false;
            }

            nextSuffix = 2;
         }

         label66:
         while(nextSuffix < value.length()) {
            c = value.charAt(nextSuffix);
            if (c != '-') {
               break;
            }

            while(true) {
               do {
                  ++nextSuffix;
                  if (nextSuffix >= value.length()) {
                     continue label66;
                  }

                  c = value.charAt(nextSuffix);
               } while(c >= 'a' && c <= 'z');

               if (c < 'A' || c > 'Z') {
                  break;
               }
            }
         }

         return value.length() == nextSuffix && c != '-';
      }
   }

   private boolean maybeElementDecl() throws IOException, SAXException {
      InputEntity start = this.peekDeclaration("!ELEMENT");
      if (start == null) {
         return false;
      } else {
         String name = this.getMarkupDeclname("F-015", true);
         if (this.declaredElements.contains(name)) {
            this.error("V-012", new Object[]{name});
         } else {
            this.declaredElements.add(name);
         }

         this.whitespace("F-000");
         short modelType;
         if (this.peek("EMPTY")) {
            modelType = 0;
            this.dtdHandler.startContentModel(name, (short)0);
         } else if (this.peek("ANY")) {
            modelType = 1;
            this.dtdHandler.startContentModel(name, (short)1);
         } else {
            modelType = this.getMixedOrChildren(name);
         }

         this.dtdHandler.endContentModel(name, modelType);
         this.maybeWhitespace();
         char c = this.getc();
         if (c != '>') {
            this.fatal("P-036", new Object[]{name, new Character(c)});
         }

         if (start != this.in) {
            this.error("V-013", (Object[])null);
         }

         return true;
      }
   }

   private short getMixedOrChildren(String elementName) throws IOException, SAXException {
      this.strTmp = new StringBuffer();
      this.nextChar('(', "F-028", elementName);
      InputEntity start = this.in;
      this.maybeWhitespace();
      this.strTmp.append('(');
      byte modelType;
      if (this.peek("#PCDATA")) {
         this.strTmp.append("#PCDATA");
         modelType = 2;
         this.dtdHandler.startContentModel(elementName, (short)2);
         this.getMixed(elementName, start);
      } else {
         modelType = 3;
         this.dtdHandler.startContentModel(elementName, (short)3);
         this.getcps(elementName, start);
      }

      return modelType;
   }

   private void getcps(String elementName, InputEntity start) throws IOException, SAXException {
      boolean decided = false;
      char type = 0;
      this.dtdHandler.startModelGroup();

      do {
         String tag = this.maybeGetName();
         if (tag != null) {
            this.strTmp.append(tag);
            this.dtdHandler.childElement(tag, this.getFrequency());
         } else if (this.peek("(")) {
            InputEntity next = this.in;
            this.strTmp.append('(');
            this.maybeWhitespace();
            this.getcps(elementName, next);
         } else {
            this.fatal(type == 0 ? "P-039" : (type == ',' ? "P-037" : "P-038"), new Object[]{new Character(this.getc())});
         }

         this.maybeWhitespace();
         if (decided) {
            char c = this.getc();
            if (c == type) {
               this.strTmp.append(type);
               this.maybeWhitespace();
               this.reportConnector(type);
               continue;
            }

            if (c == ')') {
               this.ungetc();
               continue;
            }

            this.fatal(type == 0 ? "P-041" : "P-040", new Object[]{new Character(c), new Character(type)});
         } else {
            type = this.getc();
            switch (type) {
               case ',':
               case '|':
                  this.reportConnector(type);
                  decided = true;
                  this.strTmp.append(type);
                  break;
               default:
                  this.ungetc();
                  continue;
            }
         }

         this.maybeWhitespace();
      } while(!this.peek(")"));

      if (this.in != start) {
         this.error("V-014", new Object[]{elementName});
      }

      this.strTmp.append(')');
      this.dtdHandler.endModelGroup(this.getFrequency());
   }

   private void reportConnector(char type) throws SAXException {
      switch (type) {
         case ',':
            this.dtdHandler.connector((short)1);
            return;
         case '|':
            this.dtdHandler.connector((short)0);
            return;
         default:
            throw new Error();
      }
   }

   private short getFrequency() throws IOException, SAXException {
      char c = this.getc();
      if (c == '?') {
         this.strTmp.append(c);
         return 2;
      } else if (c == '+') {
         this.strTmp.append(c);
         return 1;
      } else if (c == '*') {
         this.strTmp.append(c);
         return 0;
      } else {
         this.ungetc();
         return 3;
      }
   }

   private void getMixed(String elementName, InputEntity start) throws IOException, SAXException {
      this.maybeWhitespace();
      if (!this.peek(")*") && !this.peek(")")) {
         ArrayList l = new ArrayList();

         while(this.peek("|")) {
            this.strTmp.append('|');
            this.maybeWhitespace();
            this.doLexicalPE = true;
            String name = this.maybeGetName();
            if (name == null) {
               this.fatal("P-042", new Object[]{elementName, Integer.toHexString(this.getc())});
            }

            if (l.contains(name)) {
               this.error("V-015", new Object[]{name});
            } else {
               l.add(name);
               this.dtdHandler.mixedElement(name);
            }

            this.strTmp.append(name);
            this.maybeWhitespace();
         }

         if (!this.peek(")*")) {
            this.fatal("P-043", new Object[]{elementName, new Character(this.getc())});
         }

         if (this.in != start) {
            this.error("V-014", new Object[]{elementName});
         }

         this.strTmp.append(')');
      } else {
         if (this.in != start) {
            this.error("V-014", new Object[]{elementName});
         }

         this.strTmp.append(')');
      }
   }

   private boolean maybeAttlistDecl() throws IOException, SAXException {
      InputEntity start = this.peekDeclaration("!ATTLIST");
      if (start == null) {
         return false;
      } else {
         String elementName = this.getMarkupDeclname("F-016", true);

         while(true) {
            while(!this.peek(">")) {
               this.maybeWhitespace();
               char c = this.getc();
               String attName;
               if (c == '%') {
                  attName = this.maybeGetName();
                  if (attName != null) {
                     this.nextChar(';', "F-021", attName);
                     this.whitespace("F-021");
                     continue;
                  }

                  this.fatal("P-011");
               }

               this.ungetc();
               attName = this.maybeGetName();
               if (attName == null) {
                  this.fatal("P-044", new Object[]{new Character(this.getc())});
               }

               this.whitespace("F-001");
               Vector values = null;
               String typeName;
               if (this.peek("CDATA")) {
                  typeName = "CDATA";
               } else if (this.peek("IDREFS")) {
                  typeName = "IDREFS";
               } else if (this.peek("IDREF")) {
                  typeName = "IDREF";
               } else if (this.peek("ID")) {
                  typeName = "ID";
               } else if (this.peek("ENTITY")) {
                  typeName = "ENTITY";
               } else if (this.peek("ENTITIES")) {
                  typeName = "ENTITIES";
               } else if (this.peek("NMTOKENS")) {
                  typeName = "NMTOKENS";
               } else if (this.peek("NMTOKEN")) {
                  typeName = "NMTOKEN";
               } else {
                  String name;
                  if (this.peek("NOTATION")) {
                     typeName = "NOTATION";
                     this.whitespace("F-002");
                     this.nextChar('(', "F-029", (String)null);
                     this.maybeWhitespace();
                     values = new Vector();

                     do {
                        if ((name = this.maybeGetName()) == null) {
                           this.fatal("P-068");
                        }

                        if (this.notations.get(name) == null) {
                           this.notations.put(name, name);
                        }

                        values.addElement(name);
                        this.maybeWhitespace();
                        if (this.peek("|")) {
                           this.maybeWhitespace();
                        }
                     } while(!this.peek(")"));
                  } else if (this.peek("(")) {
                     typeName = "ENUMERATION";
                     this.maybeWhitespace();
                     values = new Vector();

                     do {
                        name = this.getNmtoken();
                        values.addElement(name);
                        this.maybeWhitespace();
                        if (this.peek("|")) {
                           this.maybeWhitespace();
                        }
                     } while(!this.peek(")"));
                  } else {
                     this.fatal("P-045", new Object[]{attName, new Character(this.getc())});
                     typeName = null;
                  }
               }

               String defaultValue = null;
               this.whitespace("F-003");
               byte attributeUse;
               if (this.peek("#REQUIRED")) {
                  attributeUse = 3;
               } else if (this.peek("#FIXED")) {
                  if (typeName == "ID") {
                     this.error("V-017", new Object[]{attName});
                  }

                  attributeUse = 2;
                  this.whitespace("F-004");
                  this.parseLiteral(false);
                  if (typeName == "CDATA") {
                     defaultValue = this.normalize(false);
                  } else {
                     defaultValue = this.strTmp.toString();
                  }
               } else if (!this.peek("#IMPLIED")) {
                  attributeUse = 1;
                  if (typeName == "ID") {
                     this.error("V-018", new Object[]{attName});
                  }

                  this.parseLiteral(false);
                  if (typeName == "CDATA") {
                     defaultValue = this.normalize(false);
                  } else {
                     defaultValue = this.strTmp.toString();
                  }
               } else {
                  attributeUse = 0;
               }

               if ("xml:lang".equals(attName) && defaultValue != null && !this.isXmlLang(defaultValue)) {
                  this.error("P-033", new Object[]{defaultValue});
               }

               String[] v = values != null ? (String[])((String[])values.toArray(new String[0])) : null;
               this.dtdHandler.attributeDecl(elementName, attName, typeName, v, attributeUse, defaultValue);
               this.maybeWhitespace();
            }

            if (start != this.in) {
               this.error("V-013", (Object[])null);
            }

            return true;
         }
      }
   }

   private String normalize(boolean invalidIfNeeded) {
      String s = this.strTmp.toString();
      String s2 = s.trim();
      boolean didStrip = false;
      if (s != s2) {
         s = s2;
         s2 = null;
         didStrip = true;
      }

      this.strTmp = new StringBuffer();

      for(int i = 0; i < s.length(); ++i) {
         char c = s.charAt(i);
         if (!XmlChars.isSpace(c)) {
            this.strTmp.append(c);
         } else {
            this.strTmp.append(' ');

            while(true) {
               ++i;
               if (i >= s.length() || !XmlChars.isSpace(s.charAt(i))) {
                  --i;
                  break;
               }

               didStrip = true;
            }
         }
      }

      if (didStrip) {
         return this.strTmp.toString();
      } else {
         return s;
      }
   }

   private boolean maybeConditionalSect() throws IOException, SAXException {
      if (!this.peek("<![")) {
         return false;
      } else {
         InputEntity start = this.in;
         this.maybeWhitespace();
         String keyword;
         if ((keyword = this.maybeGetName()) == null) {
            this.fatal("P-046");
         }

         this.maybeWhitespace();
         this.nextChar('[', "F-030", (String)null);
         if (!"INCLUDE".equals(keyword)) {
            if ("IGNORE".equals(keyword)) {
               int nestlevel = 1;
               this.doLexicalPE = false;

               while(nestlevel > 0) {
                  char c = this.getc();
                  if (c == '<') {
                     if (this.peek("![")) {
                        ++nestlevel;
                     }
                  } else if (c == ']' && this.peek("]>")) {
                     --nestlevel;
                  }
               }
            } else {
               this.fatal("P-048", new Object[]{keyword});
            }
         } else {
            while(true) {
               while(!this.in.isEOF() || this.in == start) {
                  if (this.in.isEOF()) {
                     this.error("V-020", (Object[])null);
                  }

                  if (this.peek("]]>")) {
                     return true;
                  }

                  this.doLexicalPE = false;
                  if (!this.maybeWhitespace() && !this.maybePEReference()) {
                     this.doLexicalPE = true;
                     if (!this.maybeMarkupDecl() && !this.maybeConditionalSect()) {
                        this.fatal("P-047");
                     }
                  }
               }

               this.in = this.in.pop();
            }
         }

         return true;
      }
   }

   private int parseCharNumber() throws IOException, SAXException {
      int retval = 0;
      char c;
      if (this.getc() != 'x') {
         this.ungetc();

         while(true) {
            while(true) {
               c = this.getc();
               if (c >= '0' && c <= '9') {
                  retval *= 10;
                  retval += c - 48;
               } else {
                  if (c == ';') {
                     return retval;
                  }

                  this.fatal("P-049");
               }
            }
         }
      } else {
         while(true) {
            while(true) {
               c = this.getc();
               if (c >= '0' && c <= '9') {
                  retval <<= 4;
                  retval += c - 48;
               } else if (c >= 'a' && c <= 'f') {
                  retval <<= 4;
                  retval += 10 + (c - 97);
               } else if (c >= 'A' && c <= 'F') {
                  retval <<= 4;
                  retval += 10 + (c - 65);
               } else {
                  if (c == ';') {
                     return retval;
                  }

                  this.fatal("P-050");
               }
            }
         }
      }
   }

   private int surrogatesToCharTmp(int ucs4) throws SAXException {
      if (ucs4 <= 65535) {
         if (XmlChars.isChar(ucs4)) {
            this.charTmp[0] = (char)ucs4;
            return 1;
         }
      } else if (ucs4 <= 1114111) {
         ucs4 -= 65536;
         this.charTmp[0] = (char)('\ud800' | ucs4 >> 10 & 1023);
         this.charTmp[1] = (char)('\udc00' | ucs4 & 1023);
         return 2;
      }

      this.fatal("P-051", new Object[]{Integer.toHexString(ucs4)});
      return -1;
   }

   private boolean maybePEReference() throws IOException, SAXException {
      if (!this.in.peekc('%')) {
         return false;
      } else {
         String name = this.maybeGetName();
         if (name == null) {
            this.fatal("P-011");
         }

         this.nextChar(';', "F-021", name);
         Object entity = this.params.get(name);
         if (entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity)entity;
            this.pushReader(value.buf, name, false);
         } else if (entity instanceof ExternalEntity) {
            this.pushReader((ExternalEntity)entity);
            this.externalParameterEntity((ExternalEntity)entity);
         } else if (entity == null) {
            this.error("V-022", new Object[]{name});
         }

         return true;
      }
   }

   private boolean maybeEntityDecl() throws IOException, SAXException {
      InputEntity start = this.peekDeclaration("!ENTITY");
      if (start == null) {
         return false;
      } else {
         this.doLexicalPE = false;
         this.whitespace("F-005");
         SimpleHashtable defns;
         if (this.in.peekc('%')) {
            this.whitespace("F-006");
            defns = this.params;
         } else {
            defns = this.entities;
         }

         this.ungetc();
         this.doLexicalPE = true;
         String entityName = this.getMarkupDeclname("F-017", false);
         this.whitespace("F-007");
         ExternalEntity externalId = this.maybeExternalID();
         boolean doStore = defns.get(entityName) == null;
         if (!doStore && defns == this.entities) {
            this.warning("P-054", new Object[]{entityName});
         }

         if (externalId == null) {
            this.doLexicalPE = false;
            this.parseLiteral(true);
            this.doLexicalPE = true;
            if (doStore) {
               char[] value = new char[this.strTmp.length()];
               if (value.length != 0) {
                  this.strTmp.getChars(0, value.length, value, 0);
               }

               InternalEntity entity = new InternalEntity(entityName, value);
               entity.isPE = defns == this.params;
               entity.isFromInternalSubset = false;
               defns.put(entityName, entity);
               if (defns == this.entities) {
                  this.dtdHandler.internalGeneralEntityDecl(entityName, new String(value));
               }
            }
         } else {
            if (defns == this.entities && this.maybeWhitespace() && this.peek("NDATA")) {
               externalId.notation = this.getMarkupDeclname("F-018", false);
               if (this.notations.get(externalId.notation) == null) {
                  this.notations.put(externalId.notation, Boolean.TRUE);
               }
            }

            externalId.name = entityName;
            externalId.isPE = defns == this.params;
            externalId.isFromInternalSubset = false;
            if (doStore) {
               defns.put(entityName, externalId);
               if (externalId.notation != null) {
                  this.dtdHandler.unparsedEntityDecl(entityName, externalId.publicId, externalId.systemId, externalId.notation);
               } else if (defns == this.entities) {
                  this.dtdHandler.externalGeneralEntityDecl(entityName, externalId.publicId, externalId.systemId);
               }
            }
         }

         this.maybeWhitespace();
         this.nextChar('>', "F-031", entityName);
         if (start != this.in) {
            this.error("V-013", (Object[])null);
         }

         return true;
      }
   }

   private ExternalEntity maybeExternalID() throws IOException, SAXException {
      String temp = null;
      if (this.peek("PUBLIC")) {
         this.whitespace("F-009");
         temp = this.parsePublicId();
      } else if (!this.peek("SYSTEM")) {
         return null;
      }

      ExternalEntity retval = new ExternalEntity(this.in);
      retval.publicId = temp;
      this.whitespace("F-008");
      retval.systemId = this.parseSystemId();
      return retval;
   }

   private String parseSystemId() throws IOException, SAXException {
      String uri = this.getQuotedString("F-034", (String)null);
      int temp = uri.indexOf(58);
      if (temp == -1 || uri.indexOf(47) < temp) {
         String baseURI = this.in.getSystemId();
         if (baseURI == null) {
            this.fatal("P-055", new Object[]{uri});
         }

         if (uri.length() == 0) {
            uri = ".";
         }

         baseURI = baseURI.substring(0, baseURI.lastIndexOf(47) + 1);
         if (uri.charAt(0) == '/') {
            throw new InternalError();
         }

         uri = baseURI + uri;
      }

      if (uri.indexOf(35) != -1) {
         this.error("P-056", new Object[]{uri});
      }

      return uri;
   }

   private void maybeTextDecl() throws IOException, SAXException {
      if (this.peek("<?xml")) {
         this.readVersion(false, "1.0");
         this.readEncoding(true);
         this.maybeWhitespace();
         if (!this.peek("?>")) {
            this.fatal("P-057");
         }
      }

   }

   private void externalParameterEntity(ExternalEntity next) throws IOException, SAXException {
      InputEntity pe = this.in;
      this.maybeTextDecl();

      while(!pe.isEOF()) {
         if (this.in.isEOF()) {
            this.in = this.in.pop();
         } else {
            this.doLexicalPE = false;
            if (!this.maybeWhitespace() && !this.maybePEReference()) {
               this.doLexicalPE = true;
               if (!this.maybeMarkupDecl() && !this.maybeConditionalSect()) {
                  break;
               }
            }
         }
      }

      if (!pe.isEOF()) {
         this.fatal("P-059", new Object[]{this.in.getName()});
      }

   }

   private void readEncoding(boolean must) throws IOException, SAXException {
      String name = this.maybeReadAttribute("encoding", must);
      if (name != null) {
         for(int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (i == 0 || (c < '0' || c > '9') && c != '-' && c != '_' && c != '.')) {
               this.fatal("P-060", new Object[]{new Character(c)});
            }
         }

         String currentEncoding = this.in.getEncoding();
         if (currentEncoding != null && !name.equalsIgnoreCase(currentEncoding)) {
            this.warning("P-061", new Object[]{name, currentEncoding});
         }

      }
   }

   private boolean maybeNotationDecl() throws IOException, SAXException {
      InputEntity start = this.peekDeclaration("!NOTATION");
      if (start == null) {
         return false;
      } else {
         String name = this.getMarkupDeclname("F-019", false);
         ExternalEntity entity = new ExternalEntity(this.in);
         this.whitespace("F-011");
         if (this.peek("PUBLIC")) {
            this.whitespace("F-009");
            entity.publicId = this.parsePublicId();
            if (this.maybeWhitespace()) {
               if (!this.peek(">")) {
                  entity.systemId = this.parseSystemId();
               } else {
                  this.ungetc();
               }
            }
         } else if (this.peek("SYSTEM")) {
            this.whitespace("F-008");
            entity.systemId = this.parseSystemId();
         } else {
            this.fatal("P-062");
         }

         this.maybeWhitespace();
         this.nextChar('>', "F-032", name);
         if (start != this.in) {
            this.error("V-013", (Object[])null);
         }

         if (entity.systemId != null && entity.systemId.indexOf(35) != -1) {
            this.error("P-056", new Object[]{entity.systemId});
         }

         Object value = this.notations.get(name);
         if (value != null && value instanceof ExternalEntity) {
            this.warning("P-063", new Object[]{name});
         } else {
            this.notations.put(name, entity);
            this.dtdHandler.notationDecl(name, entity.publicId, entity.systemId);
         }

         return true;
      }
   }

   private char getc() throws IOException, SAXException {
      char c;
      if (!this.doLexicalPE) {
         c = this.in.getc();
         return c;
      } else {
         while(true) {
            while(this.in.isEOF()) {
               if (!this.in.isInternal() && (!this.doLexicalPE || this.in.isDocument())) {
                  this.fatal("P-064", new Object[]{this.in.getName()});
               } else {
                  this.in = this.in.pop();
               }
            }

            if ((c = this.in.getc()) == '%' && this.doLexicalPE) {
               String name = this.maybeGetName();
               if (name == null) {
                  this.fatal("P-011");
               }

               this.nextChar(';', "F-021", name);
               Object entity = this.params.get(name);
               this.pushReader(" ".toCharArray(), (String)null, false);
               if (entity instanceof InternalEntity) {
                  this.pushReader(((InternalEntity)entity).buf, name, false);
               } else if (entity instanceof ExternalEntity) {
                  this.pushReader((ExternalEntity)entity);
               } else {
                  if (entity != null) {
                     throw new InternalError();
                  }

                  this.fatal("V-022");
               }

               this.pushReader(" ".toCharArray(), (String)null, false);
               return this.in.getc();
            }

            return c;
         }
      }
   }

   private void ungetc() {
      this.in.ungetc();
   }

   private boolean peek(String s) throws IOException, SAXException {
      return this.in.peek(s, (char[])null);
   }

   private InputEntity peekDeclaration(String s) throws IOException, SAXException {
      if (!this.in.peekc('<')) {
         return null;
      } else {
         InputEntity start = this.in;
         if (this.in.peek(s, (char[])null)) {
            return start;
         } else {
            this.in.ungetc();
            return null;
         }
      }
   }

   private void nextChar(char c, String location, String near) throws IOException, SAXException {
      while(this.in.isEOF() && !this.in.isDocument()) {
         this.in = this.in.pop();
      }

      if (!this.in.peekc(c)) {
         this.fatal("P-008", new Object[]{new Character(c), messages.getMessage(this.locale, location), near == null ? "" : '"' + near + '"'});
      }

   }

   private void pushReader(char[] buf, String name, boolean isGeneral) throws SAXException {
      InputEntity r = InputEntity.getInputEntity(this.dtdHandler, this.locale);
      r.init(buf, name, this.in, !isGeneral);
      this.in = r;
   }

   private boolean pushReader(ExternalEntity next) throws IOException, SAXException {
      InputEntity r = InputEntity.getInputEntity(this.dtdHandler, this.locale);

      InputSource s;
      try {
         s = next.getInputSource(this.resolver);
      } catch (IOException var7) {
         String msg = "unable to open the external entity from :" + next.systemId;
         if (next.publicId != null) {
            msg = msg + " (public id:" + next.publicId + ")";
         }

         SAXParseException spe = new SAXParseException(msg, this.getPublicId(), this.getSystemId(), this.getLineNumber(), this.getColumnNumber(), var7);
         this.dtdHandler.fatalError(spe);
         throw var7;
      }

      r.init(s, next.name, this.in, next.isPE);
      this.in = r;
      return true;
   }

   public String getPublicId() {
      return this.in == null ? null : this.in.getPublicId();
   }

   public String getSystemId() {
      return this.in == null ? null : this.in.getSystemId();
   }

   public int getLineNumber() {
      return this.in == null ? -1 : this.in.getLineNumber();
   }

   public int getColumnNumber() {
      return this.in == null ? -1 : this.in.getColumnNumber();
   }

   private void warning(String messageId, Object[] parameters) throws SAXException {
      SAXParseException e = new SAXParseException(messages.getMessage(this.locale, messageId, parameters), this.getPublicId(), this.getSystemId(), this.getLineNumber(), this.getColumnNumber());
      this.dtdHandler.warning(e);
   }

   void error(String messageId, Object[] parameters) throws SAXException {
      SAXParseException e = new SAXParseException(messages.getMessage(this.locale, messageId, parameters), this.getPublicId(), this.getSystemId(), this.getLineNumber(), this.getColumnNumber());
      this.dtdHandler.error(e);
   }

   private void fatal(String messageId) throws SAXException {
      this.fatal(messageId, (Object[])null);
   }

   private void fatal(String messageId, Object[] parameters) throws SAXException {
      SAXParseException e = new SAXParseException(messages.getMessage(this.locale, messageId, parameters), this.getPublicId(), this.getSystemId(), this.getLineNumber(), this.getColumnNumber());
      this.dtdHandler.fatalError(e);
      throw e;
   }

   static final class Catalog extends MessageCatalog {
      Catalog() {
         super(DTDParser.class);
      }
   }

   static class NameCacheEntry {
      String name;
      char[] chars;
      NameCacheEntry next;

      boolean matches(char[] value, int len) {
         if (this.chars.length != len) {
            return false;
         } else {
            for(int i = 0; i < len; ++i) {
               if (value[i] != this.chars[i]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   static class NameCache {
      NameCacheEntry[] hashtable = new NameCacheEntry[541];

      String lookup(char[] value, int len) {
         return this.lookupEntry(value, len).name;
      }

      NameCacheEntry lookupEntry(char[] value, int len) {
         int index = 0;

         for(int i = 0; i < len; ++i) {
            index = index * 31 + value[i];
         }

         index &= Integer.MAX_VALUE;
         index %= this.hashtable.length;

         NameCacheEntry entry;
         for(entry = this.hashtable[index]; entry != null; entry = entry.next) {
            if (entry.matches(value, len)) {
               return entry;
            }
         }

         entry = new NameCacheEntry();
         entry.chars = new char[len];
         System.arraycopy(value, 0, entry.chars, 0, len);
         entry.name = new String(entry.chars);
         entry.name = entry.name.intern();
         entry.next = this.hashtable[index];
         this.hashtable[index] = entry;
         return entry;
      }
   }
}
