package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.BinaryExpr;
import com.sun.tools.corba.se.idl.constExpr.BooleanAnd;
import com.sun.tools.corba.se.idl.constExpr.BooleanNot;
import com.sun.tools.corba.se.idl.constExpr.BooleanOr;
import com.sun.tools.corba.se.idl.constExpr.Equal;
import com.sun.tools.corba.se.idl.constExpr.EvaluationException;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import com.sun.tools.corba.se.idl.constExpr.GreaterEqual;
import com.sun.tools.corba.se.idl.constExpr.GreaterThan;
import com.sun.tools.corba.se.idl.constExpr.LessEqual;
import com.sun.tools.corba.se.idl.constExpr.LessThan;
import com.sun.tools.corba.se.idl.constExpr.NotEqual;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Preprocessor {
   private Vector PragmaIDs = new Vector();
   private Vector pragmaHandlers = new Vector();
   private boolean lastWasMacroID = false;
   private Parser parser;
   private Scanner scanner;
   private Hashtable symbols;
   private Vector macros;
   private Stack alreadyProcessedABranch = new Stack();
   Token token;
   private static String indent = "";

   Preprocessor() {
   }

   void init(Parser var1) {
      this.parser = var1;
      this.symbols = var1.symbols;
      this.macros = var1.macros;
   }

   protected Object clone() {
      return new Preprocessor();
   }

   Token process(Token var1) throws IOException, ParseException {
      this.token = var1;
      this.scanner = this.parser.scanner;
      this.scanner.escapedOK = false;

      try {
         switch (this.token.type) {
            case 300:
               this.define();
               break;
            case 301:
               this.undefine();
               break;
            case 302:
               this.ifClause();
               break;
            case 303:
               this.ifdef(false);
               break;
            case 304:
               this.ifdef(true);
               break;
            case 305:
               if (this.alreadyProcessedABranch.empty()) {
                  throw ParseException.elseNoIf(this.scanner);
               }

               if ((Boolean)this.alreadyProcessedABranch.peek()) {
                  this.skipToEndif();
               } else {
                  this.alreadyProcessedABranch.pop();
                  this.alreadyProcessedABranch.push(new Boolean(true));
                  this.token = this.scanner.getToken();
               }
               break;
            case 306:
               this.elif();
               break;
            case 307:
               this.include();
               break;
            case 308:
               if (this.alreadyProcessedABranch.empty()) {
                  throw ParseException.endNoIf(this.scanner);
               }

               this.alreadyProcessedABranch.pop();
               this.token = this.scanner.getToken();
               break;
            case 311:
               this.pragma();
               break;
            case 313:
               if (!this.parser.noWarn) {
                  ParseException.warning(this.scanner, Util.getMessage("Preprocessor.unknown", this.token.name));
               }
            case 309:
            case 310:
            case 312:
            default:
               this.scanner.skipLineComment();
               this.token = this.scanner.getToken();
         }
      } catch (IOException var3) {
         this.scanner.escapedOK = true;
         throw var3;
      } catch (ParseException var4) {
         this.scanner.escapedOK = true;
         throw var4;
      }

      this.scanner.escapedOK = true;
      return this.token;
   }

   private void include() throws IOException, ParseException {
      this.match(307);
      IncludeEntry var1 = this.parser.stFactory.includeEntry(this.parser.currentModule);
      var1.sourceFile(this.scanner.fileEntry());
      this.scanner.fileEntry().addInclude(var1);
      if (this.token.type == 204) {
         this.include2(var1);
      } else {
         if (this.token.type != 110) {
            int[] var2 = new int[]{204, 110};
            throw ParseException.syntaxError(this.scanner, var2, this.token.type);
         }

         this.include3(var1);
      }

      if (this.parser.currentModule instanceof ModuleEntry) {
         ((ModuleEntry)this.parser.currentModule).addContained(var1);
      } else if (this.parser.currentModule instanceof InterfaceEntry) {
         ((InterfaceEntry)this.parser.currentModule).addContained(var1);
      }

   }

   private void include2(IncludeEntry var1) throws IOException, ParseException {
      var1.name('"' + this.token.name + '"');
      this.include4(var1, this.token.name);
      this.match(204);
   }

   private void include3(IncludeEntry var1) throws IOException, ParseException {
      if (this.token.type != 110) {
         this.match(110);
      } else {
         try {
            String var2 = this.getUntil('>');
            this.token = this.scanner.getToken();
            var1.name('<' + var2 + '>');
            this.include4(var1, var2);
            this.match(111);
         } catch (IOException var3) {
            throw ParseException.syntaxError(this.scanner, ">", "EOF");
         }
      }

   }

   private void include4(IncludeEntry var1, String var2) throws IOException, ParseException {
      try {
         boolean var3 = this.parser.currentModule == this.parser.topLevelModule;
         var1.absFilename(Util.getAbsolutePath(var2, this.parser.paths));
         this.scanner.scanIncludedFile(var1, this.getFilename(var2), var3);
      } catch (IOException var4) {
         ParseException.generic(this.scanner, var4.toString());
      }

   }

   private void define() throws IOException, ParseException {
      this.match(300);
      if (this.token.equals(80)) {
         String var1 = this.scanner.getStringToEOL();
         this.symbols.put(this.token.name, var1.trim());
         this.match(80);
      } else {
         if (!this.token.equals(81)) {
            throw ParseException.syntaxError(this.scanner, 80, this.token.type);
         }

         this.symbols.put(this.token.name, '(' + this.scanner.getStringToEOL().trim());
         this.macros.addElement(this.token.name);
         this.match(81);
      }

   }

   private void undefine() throws IOException, ParseException {
      this.match(301);
      if (this.token.equals(80)) {
         this.symbols.remove(this.token.name);
         this.macros.removeElement(this.token.name);
         this.match(80);
      } else {
         throw ParseException.syntaxError(this.scanner, 80, this.token.type);
      }
   }

   private void ifClause() throws IOException, ParseException {
      this.match(302);
      this.constExpr();
   }

   private void constExpr() throws IOException, ParseException {
      SymtabEntry var1 = new SymtabEntry(this.parser.currentModule);
      var1.container(this.parser.currentModule);
      this.parser.parsingConditionalExpr = true;
      Expression var2 = this.booleanConstExpr(var1);
      this.parser.parsingConditionalExpr = false;
      boolean var3;
      if (var2.value() instanceof Boolean) {
         var3 = (Boolean)var2.value();
      } else {
         var3 = ((Number)var2.value()).longValue() != 0L;
      }

      this.alreadyProcessedABranch.push(new Boolean(var3));
      if (!var3) {
         this.skipToEndiforElse();
      }

   }

   Expression booleanConstExpr(SymtabEntry var1) throws IOException, ParseException {
      Expression var2 = this.orExpr((Expression)null, var1);

      try {
         var2.evaluate();
      } catch (EvaluationException var4) {
         ParseException.evaluationError(this.scanner, var4.toString());
      }

      return var2;
   }

   private Expression orExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.andExpr((Expression)null, var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.andExpr((Expression)null, var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(134)) {
         this.match(this.token.type);
         BooleanOr var4 = this.parser.exprFactory.booleanOr(var1, (Expression)null);
         var4.rep(var1.rep() + " || ");
         return this.orExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression andExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.notExpr(var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.notExpr(var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(135)) {
         this.match(this.token.type);
         BooleanAnd var4 = this.parser.exprFactory.booleanAnd(var1, (Expression)null);
         var4.rep(var1.rep() + " && ");
         return this.andExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression notExpr(SymtabEntry var1) throws IOException, ParseException {
      Object var2;
      if (this.token.equals(129)) {
         this.match(129);
         var2 = this.parser.exprFactory.booleanNot(this.definedExpr(var1));
         ((Expression)var2).rep("!" + ((BooleanNot)var2).operand().rep());
      } else {
         var2 = this.definedExpr(var1);
      }

      return (Expression)var2;
   }

   private Expression definedExpr(SymtabEntry var1) throws IOException, ParseException {
      if (this.token.equals(80) && this.token.name.equals("defined")) {
         this.match(80);
      }

      return this.equalityExpr((Expression)null, var1);
   }

   private Expression equalityExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         this.parser.token = this.token;
         var1 = this.parser.constExp(var2);
         this.token = this.parser.token;
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         this.parser.token = this.token;
         Expression var4 = this.parser.constExp(var2);
         this.token = this.parser.token;
         var3.right(var4);
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(130)) {
         this.match(this.token.type);
         Equal var10 = this.parser.exprFactory.equal(var1, (Expression)null);
         var10.rep(var1.rep() + " == ");
         return this.equalityExpr(var10, var2);
      } else if (this.token.equals(131)) {
         this.match(this.token.type);
         NotEqual var9 = this.parser.exprFactory.notEqual(var1, (Expression)null);
         var9.rep(var1.rep() + " != ");
         return this.equalityExpr(var9, var2);
      } else if (this.token.equals(111)) {
         this.match(this.token.type);
         GreaterThan var8 = this.parser.exprFactory.greaterThan(var1, (Expression)null);
         var8.rep(var1.rep() + " > ");
         return this.equalityExpr(var8, var2);
      } else if (this.token.equals(132)) {
         this.match(this.token.type);
         GreaterEqual var7 = this.parser.exprFactory.greaterEqual(var1, (Expression)null);
         var7.rep(var1.rep() + " >= ");
         return this.equalityExpr(var7, var2);
      } else if (this.token.equals(110)) {
         this.match(this.token.type);
         LessThan var6 = this.parser.exprFactory.lessThan(var1, (Expression)null);
         var6.rep(var1.rep() + " < ");
         return this.equalityExpr(var6, var2);
      } else if (this.token.equals(133)) {
         this.match(this.token.type);
         LessEqual var5 = this.parser.exprFactory.lessEqual(var1, (Expression)null);
         var5.rep(var1.rep() + " <= ");
         return this.equalityExpr(var5, var2);
      } else {
         return var1;
      }
   }

   Expression primaryExpr(SymtabEntry var1) throws IOException, ParseException {
      Object var2 = null;
      switch (this.token.type) {
         case 80:
            var2 = this.parser.exprFactory.terminal("0", BigInteger.valueOf(0L));
            this.token = this.scanner.getToken();
            break;
         case 108:
            this.match(108);
            var2 = this.booleanConstExpr(var1);
            this.match(109);
            ((Expression)var2).rep('(' + ((Expression)var2).rep() + ')');
            break;
         case 200:
         case 201:
         case 202:
         case 203:
         case 204:
            var2 = this.parser.literal(var1);
            this.token = this.parser.token;
            break;
         default:
            int[] var3 = new int[]{205, 108};
            throw ParseException.syntaxError(this.scanner, var3, this.token.type);
      }

      return (Expression)var2;
   }

   private void ifDefine(boolean var1, boolean var2) throws IOException, ParseException {
      if (!this.token.equals(80)) {
         throw ParseException.syntaxError(this.scanner, 80, this.token.type);
      } else {
         if ((!var2 || !this.symbols.containsKey(this.token.name)) && (var2 || this.symbols.containsKey(this.token.name))) {
            this.alreadyProcessedABranch.push(new Boolean(true));
            this.match(80);
            if (var1) {
               this.match(109);
            }
         } else {
            this.alreadyProcessedABranch.push(new Boolean(false));
            this.skipToEndiforElse();
         }

      }
   }

   private void ifdef(boolean var1) throws IOException, ParseException {
      if (var1) {
         this.match(304);
      } else {
         this.match(303);
      }

      if (!this.token.equals(80)) {
         throw ParseException.syntaxError(this.scanner, 80, this.token.type);
      } else {
         if ((!var1 || !this.symbols.containsKey(this.token.name)) && (var1 || this.symbols.containsKey(this.token.name))) {
            this.alreadyProcessedABranch.push(new Boolean(true));
            this.match(80);
         } else {
            this.alreadyProcessedABranch.push(new Boolean(false));
            this.skipToEndiforElse();
         }

      }
   }

   private void elif() throws IOException, ParseException {
      if (this.alreadyProcessedABranch.empty()) {
         throw ParseException.elseNoIf(this.scanner);
      } else {
         if ((Boolean)this.alreadyProcessedABranch.peek()) {
            this.skipToEndif();
         } else {
            this.match(306);
            this.constExpr();
         }

      }
   }

   private void skipToEndiforElse() throws IOException, ParseException {
      while(!this.token.equals(308) && !this.token.equals(305) && !this.token.equals(306)) {
         if (!this.token.equals(303) && !this.token.equals(304)) {
            this.token = this.scanner.skipUntil('#');
         } else {
            this.alreadyProcessedABranch.push(new Boolean(true));
            this.skipToEndif();
         }
      }

      this.process(this.token);
   }

   private void skipToEndif() throws IOException, ParseException {
      while(!this.token.equals(308)) {
         this.token = this.scanner.skipUntil('#');
         if (this.token.equals(303) || this.token.equals(304)) {
            this.alreadyProcessedABranch.push(new Boolean(true));
            this.skipToEndif();
         }
      }

      this.alreadyProcessedABranch.pop();
      this.match(308);
   }

   private void pragma() throws IOException, ParseException {
      this.match(311);
      String var1 = this.token.name;
      this.scanner.escapedOK = true;
      this.match(80);
      PragmaEntry var2 = this.parser.stFactory.pragmaEntry(this.parser.currentModule);
      var2.name(var1);
      var2.sourceFile(this.scanner.fileEntry());
      var2.data(this.scanner.currentLine());
      if (this.parser.currentModule instanceof ModuleEntry) {
         ((ModuleEntry)this.parser.currentModule).addContained(var2);
      } else if (this.parser.currentModule instanceof InterfaceEntry) {
         ((InterfaceEntry)this.parser.currentModule).addContained(var2);
      }

      if (var1.equals("ID")) {
         this.idPragma();
      } else if (var1.equals("prefix")) {
         this.prefixPragma();
      } else if (var1.equals("version")) {
         this.versionPragma();
      } else if (var1.equals("sun_local")) {
         this.localPragma();
      } else if (var1.equals("sun_localservant")) {
         this.localServantPragma();
      } else {
         this.otherPragmas(var1, this.tokenToString());
         this.token = this.scanner.getToken();
      }

      this.scanner.escapedOK = false;
   }

   private void localPragma() throws IOException, ParseException {
      this.parser.token = this.token;
      SymtabEntry var1 = new SymtabEntry();
      SymtabEntry var2 = this.parser.scopedName(this.parser.currentModule, var1);
      if (var2 == var1) {
         System.out.println("Error occured ");
         this.scanner.skipLineComment();
         this.token = this.scanner.getToken();
      } else {
         if (var2 instanceof InterfaceEntry) {
            InterfaceEntry var3 = (InterfaceEntry)var2;
            var3.setInterfaceType(4);
         }

         this.token = this.parser.token;
         String var4 = this.token.name;
         this.match(204);
      }

   }

   private void localServantPragma() throws IOException, ParseException {
      this.parser.token = this.token;
      SymtabEntry var1 = new SymtabEntry();
      SymtabEntry var2 = this.parser.scopedName(this.parser.currentModule, var1);
      if (var2 == var1) {
         this.scanner.skipLineComment();
         this.token = this.scanner.getToken();
         System.out.println("Error occured ");
      } else {
         if (var2 instanceof InterfaceEntry) {
            InterfaceEntry var3 = (InterfaceEntry)var2;
            var3.setInterfaceType(3);
         }

         this.token = this.parser.token;
         String var4 = this.token.name;
         this.match(204);
      }

   }

   private void idPragma() throws IOException, ParseException {
      this.parser.token = this.token;
      this.parser.isModuleLegalType(true);
      SymtabEntry var1 = new SymtabEntry();
      SymtabEntry var2 = this.parser.scopedName(this.parser.currentModule, var1);
      this.parser.isModuleLegalType(false);
      if (var2 == var1) {
         this.scanner.skipLineComment();
         this.token = this.scanner.getToken();
      } else {
         this.token = this.parser.token;
         String var3 = this.token.name;
         if (this.PragmaIDs.contains(var2)) {
            ParseException.badRepIDAlreadyAssigned(this.scanner, var2.name());
         } else if (!RepositoryID.hasValidForm(var3)) {
            ParseException.badRepIDForm(this.scanner, var3);
         } else {
            var2.repositoryID(new RepositoryID(var3));
            this.PragmaIDs.addElement(var2);
         }

         this.match(204);
      }

   }

   private void prefixPragma() throws IOException, ParseException {
      String var1 = this.token.name;
      this.match(204);
      Parser var10000 = this.parser;
      ((IDLID)Parser.repIDStack.peek()).prefix(var1);
      var10000 = this.parser;
      ((IDLID)Parser.repIDStack.peek()).name("");
   }

   private void versionPragma() throws IOException, ParseException {
      this.parser.token = this.token;
      this.parser.isModuleLegalType(true);
      SymtabEntry var1 = new SymtabEntry();
      SymtabEntry var2 = this.parser.scopedName(this.parser.currentModule, var1);
      this.parser.isModuleLegalType(false);
      if (var2 == var1) {
         this.scanner.skipLineComment();
         this.token = this.scanner.getToken();
      } else {
         this.token = this.parser.token;
         String var3 = this.token.name;
         this.match(203);
         if (var2.repositoryID() instanceof IDLID) {
            ((IDLID)var2.repositoryID()).version(var3);
         }
      }

   }

   void registerPragma(PragmaHandler var1) {
      this.pragmaHandlers.addElement(var1);
   }

   private void otherPragmas(String var1, String var2) throws IOException {
      for(int var3 = this.pragmaHandlers.size() - 1; var3 >= 0; --var3) {
         PragmaHandler var4 = (PragmaHandler)this.pragmaHandlers.elementAt(var3);
         if (var4.process(var1, var2)) {
            break;
         }
      }

   }

   String currentToken() {
      return this.tokenToString();
   }

   SymtabEntry getEntryForName(String var1) {
      boolean var2 = false;
      boolean var3 = false;
      if (var1.startsWith("::")) {
         var3 = true;
         var1 = var1.substring(2);
      }

      for(int var4 = var1.indexOf("::"); var4 >= 0; var4 = var1.indexOf("::")) {
         var2 = true;
         var1 = var1.substring(0, var4) + '/' + var1.substring(var4 + 2);
      }

      SymtabEntry var5 = null;
      if (var3) {
         var5 = this.parser.recursiveQualifiedEntry(var1);
      } else if (var2) {
         var5 = this.parser.recursivePQEntry(var1, this.parser.currentModule);
      } else {
         var5 = this.parser.unqualifiedEntryWMod(var1, this.parser.currentModule);
      }

      return var5;
   }

   String getStringToEOL() throws IOException {
      return this.scanner.getStringToEOL();
   }

   String getUntil(char var1) throws IOException {
      return this.scanner.getUntil(var1);
   }

   private String tokenToString() {
      if (this.token.equals(81)) {
         this.lastWasMacroID = true;
         return this.token.name;
      } else {
         return this.token.equals(80) ? this.token.name : this.token.toString();
      }
   }

   String nextToken() throws IOException {
      if (this.lastWasMacroID) {
         this.lastWasMacroID = false;
         return "(";
      } else {
         this.token = this.scanner.getToken();
         return this.tokenToString();
      }
   }

   SymtabEntry scopedName() throws IOException {
      boolean var1 = false;
      boolean var2 = false;
      String var3 = null;
      SymtabEntry var4 = null;

      try {
         if (this.token.equals(124)) {
            var1 = true;
         } else if (this.token.equals(20)) {
            var3 = "Object";
            this.match(20);
         } else if (this.token.type == 45) {
            var3 = "ValueBase";
            this.match(45);
         } else {
            var3 = this.token.name;
            this.match(80);
         }

         for(; this.token.equals(124); this.match(80)) {
            this.match(124);
            var2 = true;
            if (var3 != null) {
               var3 = var3 + '/' + this.token.name;
            } else {
               var3 = this.token.name;
            }
         }

         if (var1) {
            var4 = this.parser.recursiveQualifiedEntry(var3);
         } else if (var2) {
            var4 = this.parser.recursivePQEntry(var3, this.parser.currentModule);
         } else {
            var4 = this.parser.unqualifiedEntryWMod(var3, this.parser.currentModule);
         }
      } catch (ParseException var6) {
         var4 = null;
      }

      return var4;
   }

   void skipToEOL() throws IOException {
      this.scanner.skipLineComment();
   }

   String skipUntil(char var1) throws IOException {
      if (!this.lastWasMacroID || var1 != '(') {
         this.token = this.scanner.skipUntil(var1);
      }

      return this.tokenToString();
   }

   void parseException(String var1) {
      if (!this.parser.noWarn) {
         ParseException.warning(this.scanner, var1);
      }

   }

   String expandMacro(String var1, Token var2) throws IOException, ParseException {
      this.token = var2;
      Vector var3 = this.getParmValues();
      this.scanner.scanString(var1 + '\n');
      Vector var4 = new Vector();
      this.macro(var4);
      if (var3.size() < var4.size()) {
         throw ParseException.syntaxError(this.scanner, 104, 109);
      } else if (var3.size() > var4.size()) {
         throw ParseException.syntaxError(this.scanner, 109, 104);
      } else {
         var1 = this.scanner.getStringToEOL();

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            var1 = this.replaceAll(var1, (String)var4.elementAt(var5), (String)var3.elementAt(var5));
         }

         return this.removeDoublePound(var1);
      }
   }

   private void miniMatch(int var1) throws ParseException {
      if (!this.token.equals(var1)) {
         throw ParseException.syntaxError(this.scanner, var1, this.token.type);
      }
   }

   private Vector getParmValues() throws IOException, ParseException {
      Vector var1 = new Vector();
      if (this.token.equals(80)) {
         this.match(80);
         this.miniMatch(108);
      } else if (!this.token.equals(81)) {
         throw ParseException.syntaxError(this.scanner, 80, this.token.type);
      }

      if (!this.token.equals(109)) {
         var1.addElement(this.scanner.getUntil(',', ')').trim());
         this.token = this.scanner.getToken();
         this.macroParmValues(var1);
      }

      return var1;
   }

   private void macroParmValues(Vector var1) throws IOException, ParseException {
      while(!this.token.equals(109)) {
         this.miniMatch(104);
         var1.addElement(this.scanner.getUntil(',', ')').trim());
         this.token = this.scanner.getToken();
      }

   }

   private void macro(Vector var1) throws IOException, ParseException {
      this.match(this.token.type);
      this.match(108);
      this.macroParms(var1);
      this.miniMatch(109);
   }

   private void macroParms(Vector var1) throws IOException, ParseException {
      if (!this.token.equals(109)) {
         var1.addElement(this.token.name);
         this.match(80);
         this.macroParms2(var1);
      }

   }

   private void macroParms2(Vector var1) throws IOException, ParseException {
      while(!this.token.equals(109)) {
         this.match(104);
         var1.addElement(this.token.name);
         this.match(80);
      }

   }

   private String replaceAll(String var1, String var2, String var3) {
      int var4 = 0;

      while(true) {
         do {
            if (var4 == -1) {
               return var1;
            }

            var4 = var1.indexOf(var2, var4);
         } while(var4 == -1);

         if (!this.embedded(var1, var4, var4 + var2.length())) {
            if (var4 > 0 && var1.charAt(var4) == '#') {
               var1 = var1.substring(0, var4) + '"' + var3 + '"' + var1.substring(var4 + var2.length());
            } else {
               var1 = var1.substring(0, var4) + var3 + var1.substring(var4 + var2.length());
            }
         }

         var4 += var3.length();
      }
   }

   private boolean embedded(String var1, int var2, int var3) {
      boolean var4 = false;
      char var5 = var2 == 0 ? 32 : var1.charAt(var2 - 1);
      char var6 = var3 >= var1.length() - 1 ? 32 : var1.charAt(var3);
      if (var5 >= 'a' && var5 <= 'z' || var5 >= 'A' && var5 <= 'Z') {
         var4 = true;
      } else if ((var6 < 'a' || var6 > 'z') && (var6 < 'A' || var6 > 'Z') && (var6 < '0' || var6 > '9') && var6 != '_') {
         var4 = this.inQuotes(var1, var2);
      } else {
         var4 = true;
      }

      return var4;
   }

   private boolean inQuotes(String var1, int var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var2; ++var4) {
         if (var1.charAt(var4) == '"') {
            ++var3;
         }
      }

      return var3 % 2 != 0;
   }

   private String removeDoublePound(String var1) {
      int var2 = 0;

      while(true) {
         do {
            if (var2 == -1) {
               return var1;
            }

            var2 = var1.indexOf("##", var2);
         } while(var2 == -1);

         int var3 = var2 - 1;
         int var4 = var2 + 2;
         if (var3 < 0) {
            var3 = 0;
         }

         if (var4 >= var1.length()) {
            var4 = var1.length() - 1;
         }

         while(var3 > 0 && (var1.charAt(var3) == ' ' || var1.charAt(var3) == '\t')) {
            --var3;
         }

         while(var4 < var1.length() - 1 && (var1.charAt(var4) == ' ' || var1.charAt(var4) == '\t')) {
            ++var4;
         }

         var1 = var1.substring(0, var3 + 1) + var1.substring(var4);
      }
   }

   private String getFilename(String var1) throws FileNotFoundException {
      String var2 = null;
      File var3 = new File(var1);
      if (var3.canRead()) {
         var2 = var1;
      } else {
         for(Enumeration var4 = this.parser.paths.elements(); !var3.canRead() && var4.hasMoreElements(); var3 = new File(var2)) {
            var2 = (String)var4.nextElement() + File.separatorChar + var1;
         }

         if (!var3.canRead()) {
            throw new FileNotFoundException(var1);
         }
      }

      return var2;
   }

   private void match(int var1) throws IOException, ParseException {
      if (!this.token.equals(var1)) {
         throw ParseException.syntaxError(this.scanner, var1, this.token.type);
      } else {
         this.token = this.scanner.getToken();
         if (this.token.equals(80) || this.token.equals(81)) {
            String var2 = (String)this.symbols.get(this.token.name);
            if (var2 != null && !var2.equals("")) {
               if (this.macros.contains(this.token.name)) {
                  this.scanner.scanString(this.expandMacro(var2, this.token));
                  this.token = this.scanner.getToken();
               } else {
                  this.scanner.scanString(var2);
                  this.token = this.scanner.getToken();
               }
            }
         }

      }
   }

   private void issueTokenWarnings() {
      if (!this.parser.noWarn) {
         ;
      }
   }

   void openScope(SymtabEntry var1) {
      for(int var2 = this.pragmaHandlers.size() - 1; var2 >= 0; --var2) {
         PragmaHandler var3 = (PragmaHandler)this.pragmaHandlers.elementAt(var2);
         var3.openScope(var1);
      }

   }

   void closeScope(SymtabEntry var1) {
      for(int var2 = this.pragmaHandlers.size() - 1; var2 >= 0; --var2) {
         PragmaHandler var3 = (PragmaHandler)this.pragmaHandlers.elementAt(var2);
         var3.closeScope(var1);
      }

   }
}
