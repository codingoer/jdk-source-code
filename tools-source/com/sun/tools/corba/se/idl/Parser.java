package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.And;
import com.sun.tools.corba.se.idl.constExpr.BinaryExpr;
import com.sun.tools.corba.se.idl.constExpr.Divide;
import com.sun.tools.corba.se.idl.constExpr.EvaluationException;
import com.sun.tools.corba.se.idl.constExpr.ExprFactory;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import com.sun.tools.corba.se.idl.constExpr.Minus;
import com.sun.tools.corba.se.idl.constExpr.Modulo;
import com.sun.tools.corba.se.idl.constExpr.Negative;
import com.sun.tools.corba.se.idl.constExpr.Not;
import com.sun.tools.corba.se.idl.constExpr.Or;
import com.sun.tools.corba.se.idl.constExpr.Plus;
import com.sun.tools.corba.se.idl.constExpr.Positive;
import com.sun.tools.corba.se.idl.constExpr.ShiftLeft;
import com.sun.tools.corba.se.idl.constExpr.ShiftRight;
import com.sun.tools.corba.se.idl.constExpr.Terminal;
import com.sun.tools.corba.se.idl.constExpr.Times;
import com.sun.tools.corba.se.idl.constExpr.Xor;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

class Parser {
   private boolean _isModuleLegalType = false;
   private static final int MAX_SHORT = 32767;
   private static final int MIN_SHORT = -32768;
   private static final int MAX_USHORT = 65535;
   UnionBranch defaultBranch = null;
   public static final String unknownNamePrefix = "uN__";
   static Hashtable symbolTable;
   Hashtable lcSymbolTable = new Hashtable();
   static Hashtable overrideNames;
   Vector emitList = new Vector();
   boolean emitAll;
   boolean cppModule;
   boolean noWarn;
   Scanner scanner;
   Hashtable symbols;
   Vector macros = new Vector();
   Vector paths;
   SymtabEntry currentModule = null;
   static Stack repIDStack = new Stack();
   private static int ftlKey = SymtabEntry.getVariableKey();
   int sequence = 0;
   Vector includes;
   Vector includeEntries;
   boolean parsingConditionalExpr = false;
   Token token;
   ModuleEntry topLevelModule;
   private Preprocessor prep;
   private boolean verbose;
   SymtabFactory stFactory;
   ExprFactory exprFactory;
   private String[] keywords;
   private TokenBuffer tokenHistory = new TokenBuffer();
   protected float corbaLevel;
   private Arguments arguments;

   Parser(Preprocessor var1, Arguments var2, Hashtable var3, Hashtable var4, SymtabFactory var5, ExprFactory var6, String[] var7) {
      this.arguments = var2;
      this.noWarn = var2.noWarn;
      this.corbaLevel = var2.corbaLevel;
      this.paths = var2.includePaths;
      this.symbols = var2.definedSymbols;
      this.verbose = var2.verbose;
      this.emitAll = var2.emitAll;
      this.cppModule = var2.cppModule;
      overrideNames = var3 == null ? new Hashtable() : var3;
      symbolTable = var4 == null ? new Hashtable() : var4;
      this.keywords = var7 == null ? new String[0] : var7;
      this.stFactory = var5;
      this.exprFactory = var6;
      this.currentModule = this.topLevelModule = new ModuleEntry();
      this.prep = var1;
      repIDStack.push(new IDLID());
      this.addPrimEntries();
   }

   void parse(String var1) throws IOException {
      IncludeEntry var2 = this.stFactory.includeEntry();
      var2.name('"' + var1 + '"');

      try {
         var2.absFilename(Util.getAbsolutePath(var1, this.paths));
      } catch (IOException var6) {
      }

      this.scanner = new Scanner(var2, this.keywords, this.verbose, this.emitAll, this.corbaLevel, this.arguments.scannerDebugFlag);
      this.topLevelModule.sourceFile(var2);
      this.token = new Token(0);
      this.tokenHistory.insert(this.token);

      try {
         this.match(0);
         if (this.token.equals(999)) {
            ParseException.nothing(var1);
         } else {
            this.specification(this.topLevelModule);
         }
      } catch (ParseException var4) {
      } catch (EOFException var5) {
      }

   }

   private void addPrimEntries() {
      symbolTable.put("short", this.stFactory.primitiveEntry("short"));
      symbolTable.put("long", this.stFactory.primitiveEntry("long"));
      symbolTable.put("long long", this.stFactory.primitiveEntry("long long"));
      symbolTable.put("unsigned short", this.stFactory.primitiveEntry("unsigned short"));
      symbolTable.put("unsigned long", this.stFactory.primitiveEntry("unsigned long"));
      symbolTable.put("unsigned long long", this.stFactory.primitiveEntry("unsigned long long"));
      symbolTable.put("char", this.stFactory.primitiveEntry("char"));
      symbolTable.put("wchar", this.stFactory.primitiveEntry("wchar"));
      symbolTable.put("float", this.stFactory.primitiveEntry("float"));
      symbolTable.put("double", this.stFactory.primitiveEntry("double"));
      symbolTable.put("boolean", this.stFactory.primitiveEntry("boolean"));
      symbolTable.put("octet", this.stFactory.primitiveEntry("octet"));
      symbolTable.put("any", this.stFactory.primitiveEntry("any"));
      InterfaceEntry var1 = this.stFactory.interfaceEntry();
      var1.name("Object");
      symbolTable.put("Object", var1);
      ValueEntry var2 = this.stFactory.valueEntry();
      var2.name("ValueBase");
      symbolTable.put("ValueBase", var2);
      this.lcSymbolTable.put("short", this.stFactory.primitiveEntry("short"));
      this.lcSymbolTable.put("long", this.stFactory.primitiveEntry("long"));
      this.lcSymbolTable.put("long long", this.stFactory.primitiveEntry("long long"));
      this.lcSymbolTable.put("unsigned short", this.stFactory.primitiveEntry("unsigned short"));
      this.lcSymbolTable.put("unsigned long", this.stFactory.primitiveEntry("unsigned long"));
      this.lcSymbolTable.put("unsigned long long", this.stFactory.primitiveEntry("unsigned long long"));
      this.lcSymbolTable.put("char", this.stFactory.primitiveEntry("char"));
      this.lcSymbolTable.put("wchar", this.stFactory.primitiveEntry("wchar"));
      this.lcSymbolTable.put("float", this.stFactory.primitiveEntry("float"));
      this.lcSymbolTable.put("double", this.stFactory.primitiveEntry("double"));
      this.lcSymbolTable.put("boolean", this.stFactory.primitiveEntry("boolean"));
      this.lcSymbolTable.put("octet", this.stFactory.primitiveEntry("octet"));
      this.lcSymbolTable.put("any", this.stFactory.primitiveEntry("any"));
      this.lcSymbolTable.put("object", var1);
      this.lcSymbolTable.put("valuebase", var2);
   }

   private void specification(ModuleEntry var1) throws IOException {
      while(!this.token.equals(999)) {
         this.definition(var1);
         this.addToEmitList(var1);
      }

   }

   private void addToEmitList(ModuleEntry var1) {
      Enumeration var2 = var1.contained().elements();

      while(var2.hasMoreElements()) {
         SymtabEntry var3 = (SymtabEntry)var2.nextElement();
         if (var3.emit()) {
            this.emitList.addElement(var3);
            if (var3 instanceof ModuleEntry) {
               this.checkContained((ModuleEntry)var3);
            }

            if (var3 instanceof IncludeEntry) {
               this.includes.addElement(var3.name());
               this.includeEntries.addElement(var3);
            }
         } else if (var3 instanceof ModuleEntry) {
            this.checkContained((ModuleEntry)var3);
         }
      }

      var1.contained().removeAllElements();
   }

   private void checkContained(ModuleEntry var1) {
      Enumeration var2 = var1.contained().elements();

      while(var2.hasMoreElements()) {
         SymtabEntry var3 = (SymtabEntry)var2.nextElement();
         if (var3 instanceof ModuleEntry) {
            this.checkContained((ModuleEntry)var3);
         }

         if (var3.emit()) {
            if (!this.emitList.contains(var1)) {
               this.emitList.addElement(var1);
            }

            var1.emit(true);
            break;
         }
      }

   }

   private void definition(ModuleEntry var1) throws IOException {
      try {
         switch (this.token.type) {
            case 5:
               this.constDcl(var1);
               break;
            case 6:
            case 7:
            case 8:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 17:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 30:
            case 31:
            case 33:
            case 35:
            case 36:
            case 37:
            case 38:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            default:
               throw ParseException.syntaxError(this.scanner, new int[]{32, 29, 34, 9, 5, 10, 16, 46, 18}, this.token.type);
            case 9:
            case 29:
            case 32:
            case 34:
               this.typeDcl(var1);
               break;
            case 10:
               this.exceptDcl(var1);
               break;
            case 16:
               this.interfaceProd(var1, 0);
               break;
            case 18:
               this.module(var1);
               break;
            case 19:
               this.nativeDcl(var1);
               break;
            case 39:
               this.match(39);
               if (this.token.type == 16) {
                  this.interfaceProd(var1, 1);
               } else {
                  if (this.token.type != 46) {
                     throw ParseException.syntaxError(this.scanner, new int[]{16, 46}, this.token.type);
                  }

                  this.valueProd(var1, true);
               }
               break;
            case 40:
            case 46:
               this.valueProd(var1, false);
               break;
            case 55:
               this.match(55);
               if (this.token.type != 16) {
                  throw ParseException.syntaxError(this.scanner, new int[]{16}, this.token.type);
               }

               this.interfaceProd(var1, 2);
         }

         this.match(100);
      } catch (ParseException var3) {
         this.skipToSemicolon();
      }

   }

   private void module(ModuleEntry var1) throws IOException, ParseException {
      this.match(18);
      repIDStack.push(((IDLID)repIDStack.peek()).clone());
      ModuleEntry var2 = this.newModule(var1);
      ((IDLID)repIDStack.peek()).appendToName(var2.name());
      var2.comment(this.tokenHistory.lookBack(1).comment);
      this.currentModule = var2;
      this.match(80);
      this.prep.openScope(var2);
      this.match(101);
      this.definition(var2);

      while(!this.token.equals(999) && !this.token.equals(102)) {
         this.definition(var2);
      }

      this.prep.closeScope(var2);
      this.match(102);
      this.currentModule = var1;
      repIDStack.pop();
   }

   private void interfaceProd(ModuleEntry var1, int var2) throws IOException, ParseException {
      this.match(16);
      String var3 = this.token.name;
      this.match(80);
      this.interface2(var1, var3, var2);
   }

   private void interface2(ModuleEntry var1, String var2, int var3) throws IOException, ParseException {
      if (this.token.type != 103 && this.token.type != 101) {
         ForwardEntry var5 = this.stFactory.forwardEntry(var1, (IDLID)repIDStack.peek());
         var5.sourceFile(this.scanner.fileEntry());
         var5.name(var2);
         var5.setInterfaceType(var3);
         var5.comment(this.tokenHistory.lookBack(var5.getInterfaceType() == 0 ? 2 : 3).comment);
         this.pigeonhole(var1, var5);
      } else {
         repIDStack.push(((IDLID)repIDStack.peek()).clone());
         InterfaceEntry var4 = this.stFactory.interfaceEntry(var1, (IDLID)repIDStack.peek());
         var4.sourceFile(this.scanner.fileEntry());
         var4.name(var2);
         var4.setInterfaceType(var3);
         var4.comment(this.tokenHistory.lookBack(var4.getInterfaceType() == 0 ? 2 : 3).comment);
         if (!ForwardEntry.replaceForwardDecl(var4)) {
            ParseException.badAbstract(this.scanner, var4.fullName());
         }

         this.pigeonhole(var1, var4);
         ((IDLID)repIDStack.peek()).appendToName(var2);
         this.currentModule = var4;
         this.interfaceDcl(var4);
         this.currentModule = var1;
         repIDStack.pop();
      }

   }

   private void interfaceDcl(InterfaceEntry var1) throws IOException, ParseException {
      if (this.token.type != 101) {
         this.inheritanceSpec(var1);
      } else if (!var1.isAbstract()) {
         SymtabEntry var2 = this.qualifiedEntry("Object");
         SymtabEntry var3 = typeOf(var2);
         if (var2 != null) {
            if (!this.isInterface(var3)) {
               ParseException.wrongType(this.scanner, overrideName("Object"), "interface", var2.typeName());
            } else {
               var1.derivedFromAddElement(var3, this.scanner);
            }
         }
      }

      this.prep.openScope(var1);
      this.match(101);

      while(this.token.type != 102) {
         this.export(var1);
      }

      this.prep.closeScope(var1);
      this.match(102);
   }

   private void export(InterfaceEntry var1) throws IOException {
      try {
         switch (this.token.type) {
            case 0:
            case 2:
            case 4:
            case 8:
            case 13:
            case 17:
            case 20:
            case 21:
            case 22:
            case 27:
            case 28:
            case 33:
            case 35:
            case 36:
            case 37:
            case 45:
            case 80:
            case 124:
               this.opDcl(var1);
               break;
            case 1:
            case 25:
               this.attrDcl(var1);
               break;
            case 3:
            case 6:
            case 7:
            case 11:
            case 12:
            case 14:
            case 15:
            case 16:
            case 18:
            case 23:
            case 24:
            case 26:
            case 30:
            case 31:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            default:
               throw ParseException.syntaxError(this.scanner, new int[]{32, 29, 34, 9, 5, 10, 25, 1, 22, 13, 8, 17, 27, 33, 4, 36, 2, 21, 0, 28, 37, 80, 124, 35, 45}, this.token.type);
            case 5:
               this.constDcl(var1);
               break;
            case 9:
            case 29:
            case 32:
            case 34:
               this.typeDcl(var1);
               break;
            case 10:
               this.exceptDcl(var1);
               break;
            case 19:
               this.nativeDcl(var1);
         }

         this.match(100);
      } catch (ParseException var3) {
         this.skipToSemicolon();
      }

   }

   private void inheritanceSpec(InterfaceEntry var1) throws IOException, ParseException {
      this.match(103);

      while(true) {
         SymtabEntry var2 = this.scopedName(var1.container(), this.stFactory.interfaceEntry());
         SymtabEntry var3 = typeOf(var2);
         if (this.isInterfaceOnly(var3)) {
            boolean var4 = var3 instanceof InterfaceEntry;
            if (var1.derivedFrom().contains(var3)) {
               ParseException.alreadyDerived(this.scanner, var3.fullName(), var1.fullName());
            } else if (var1.isAbstract() && ((InterfaceType)var3).getInterfaceType() != 1) {
               ParseException.nonAbstractParent(this.scanner, var1.fullName(), var2.fullName());
            } else {
               var1.derivedFromAddElement(var3, this.scanner);
            }
         } else if (this.isForward(var3)) {
            ParseException.illegalForwardInheritance(this.scanner, var1.fullName(), var2.fullName());
         } else {
            ParseException.wrongType(this.scanner, var2.fullName(), "interface", this.entryName(var2));
         }

         if (var2 instanceof InterfaceEntry && ((InterfaceEntry)var2).state() != null) {
            if (var1.state() != null) {
               throw ParseException.badState(this.scanner, var1.fullName());
            }

            var1.initState();
         }

         if (this.token.type != 104) {
            return;
         }

         this.match(104);
      }
   }

   public boolean isModuleLegalType() {
      return this._isModuleLegalType;
   }

   public void isModuleLegalType(boolean var1) {
      this._isModuleLegalType = var1;
   }

   SymtabEntry scopedName(SymtabEntry var1, SymtabEntry var2) throws IOException, ParseException {
      return this.scopedName(var1, var2, true);
   }

   SymtabEntry scopedName(SymtabEntry var1, SymtabEntry var2, boolean var3) throws IOException, ParseException {
      boolean var4 = false;
      boolean var5 = false;
      String var6 = null;
      if (this.token.type == 124) {
         var4 = true;
      } else if (this.token.type == 20) {
         var6 = "Object";
         this.match(20);
      } else if (this.token.type == 45) {
         var6 = "ValueBase";
         this.match(45);
      } else {
         var6 = this.token.name;
         this.match(80);
      }

      for(; this.token.type == 124; this.match(80)) {
         this.match(124);
         var5 = true;
         if (var6 != null) {
            var6 = var6 + '/' + this.token.name;
         } else {
            var6 = this.token.name;
         }
      }

      SymtabEntry var7 = null;
      if (var4) {
         var7 = this.qualifiedEntry(var6);
      } else if (var5) {
         var7 = this.partlyQualifiedEntry(var6, var1);
      } else {
         var7 = this.unqualifiedEntry(var6, var1);
      }

      if (var7 == null) {
         var7 = var2;
         var2.name(var6);
      } else if (!var7.isReferencable() && var3) {
         throw ParseException.illegalIncompleteTypeReference(this.scanner, var6);
      }

      return var7;
   }

   private void valueProd(ModuleEntry var1, boolean var2) throws IOException, ParseException {
      boolean var3 = this.token.type == 40;
      if (var3) {
         this.match(40);
      }

      this.match(46);
      String var4 = this.token.name;
      this.match(80);
      switch (this.token.type) {
         case 43:
         case 101:
         case 103:
            this.value2(var1, var4, var2, var3);
            return;
         case 100:
            if (!var3) {
               this.valueForwardDcl(var1, var4, var2);
               return;
            }
         default:
            if (var3) {
               throw ParseException.badCustom(this.scanner);
            } else if (var2) {
               throw ParseException.abstractValueBox(this.scanner);
            } else {
               this.valueBox(var1, var4);
            }
      }
   }

   private void value2(ModuleEntry var1, String var2, boolean var3, boolean var4) throws IOException, ParseException {
      repIDStack.push(((IDLID)repIDStack.peek()).clone());
      ValueEntry var5 = this.stFactory.valueEntry(var1, (IDLID)repIDStack.peek());
      var5.sourceFile(this.scanner.fileEntry());
      var5.name(var2);
      var5.setInterfaceType(var3 ? 1 : 0);
      var5.setCustom(var4);
      var5.comment(this.tokenHistory.lookBack(!var3 && !var4 ? 2 : 3).comment);
      if (!ForwardEntry.replaceForwardDecl(var5)) {
         ParseException.badAbstract(this.scanner, var5.fullName());
      }

      this.pigeonhole(var1, var5);
      ((IDLID)repIDStack.peek()).appendToName(var2);
      this.currentModule = var5;
      this.valueDcl(var5);
      var5.tagMethods();
      this.currentModule = var1;
      repIDStack.pop();
   }

   private void valueDcl(ValueEntry var1) throws IOException, ParseException {
      if (this.token.type == 103) {
         this.valueInheritanceSpec(var1);
      } else if (!var1.isAbstract()) {
         SymtabEntry var2 = this.qualifiedEntry("ValueBase");
         SymtabEntry var3 = typeOf(var2);
         if (var2 != null) {
            if (!this.isValue(var3)) {
               ParseException.wrongType(this.scanner, overrideName("ValueBase"), "value", var2.typeName());
            } else {
               var1.derivedFromAddElement(var3, false, this.scanner);
            }
         }
      }

      if (this.token.type == 43) {
         this.valueSupportsSpec(var1);
      }

      this.prep.openScope(var1);
      this.match(101);

      while(this.token.type != 102) {
         this.valueElement(var1);
      }

      this.prep.closeScope(var1);
      this.match(102);
   }

   private void valueInheritanceSpec(ValueEntry var1) throws IOException, ParseException {
      this.match(103);
      boolean var2 = this.token.type == 44;
      if (var2) {
         this.match(44);
      }

      while(true) {
         SymtabEntry var3 = this.scopedName(var1.container(), this.stFactory.valueEntry());
         SymtabEntry var4 = typeOf(var3);
         if (this.isValue(var4) && !(var4 instanceof ValueBoxEntry)) {
            var1.derivedFromAddElement(var4, var2, this.scanner);
         } else if (this.isForward(var4)) {
            ParseException.illegalForwardInheritance(this.scanner, var1.fullName(), var3.fullName());
         } else {
            ParseException.wrongType(this.scanner, var3.fullName(), "value", this.entryName(var3));
         }

         if (this.token.type != 104) {
            return;
         }

         this.match(104);
         var2 = false;
      }
   }

   private void valueSupportsSpec(ValueEntry var1) throws IOException, ParseException {
      this.match(43);

      while(true) {
         SymtabEntry var2 = this.scopedName(var1.container(), this.stFactory.interfaceEntry());
         SymtabEntry var3 = typeOf(var2);
         if (this.isInterface(var3)) {
            var1.derivedFromAddElement(var3, this.scanner);
         } else {
            ParseException.wrongType(this.scanner, var2.fullName(), "interface", this.entryName(var2));
         }

         if (this.token.type != 104) {
            return;
         }

         this.match(104);
      }
   }

   private void valueElement(ValueEntry var1) throws IOException, ParseException {
      if (var1.isAbstract()) {
         this.export(var1);
      } else {
         switch (this.token.type) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
            case 13:
            case 17:
            case 19:
            case 20:
            case 21:
            case 22:
            case 25:
            case 27:
            case 28:
            case 29:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 45:
            case 80:
            case 124:
               this.export(var1);
               break;
            case 3:
            case 6:
            case 7:
            case 11:
            case 12:
            case 14:
            case 15:
            case 16:
            case 18:
            case 23:
            case 24:
            case 26:
            case 30:
            case 31:
            case 39:
            case 40:
            case 43:
            case 44:
            case 46:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            default:
               throw ParseException.syntaxError(this.scanner, new int[]{41, 42, 38, 45, 32, 29, 34, 9, 5, 10, 25, 1, 22, 13, 8, 17, 27, 33, 4, 36, 2, 21, 0, 28, 37, 80, 124, 35}, this.token.type);
            case 38:
            case 47:
               this.initDcl(var1);
               break;
            case 41:
            case 42:
               this.valueStateMember(var1);
         }
      }

   }

   private void valueStateMember(ValueEntry var1) throws IOException, ParseException {
      TypedefEntry var2 = this.stFactory.typedefEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);
      boolean var3 = this.token.type == 42;
      if (var3) {
         this.match(42);
      } else {
         this.match(41);
      }

      boolean var4 = this.token.type == 29 || this.token.type == 34 || this.token.type == 9;
      var2.name("");
      var2.type(this.typeSpec(var2));
      this.addDeclarators(var1, var2, var3);
      if (var4) {
         var1.addContained(var2);
      }

      this.match(100);
   }

   private void addDeclarators(ValueEntry var1, TypedefEntry var2, boolean var3) throws IOException, ParseException {
      int var4 = var3 ? 2 : 0;

      try {
         Vector var5 = new Vector();
         this.declarators(var2, var5);
         Enumeration var6 = var5.elements();

         while(var6.hasMoreElements()) {
            var1.addStateElement(new InterfaceState(var4, (TypedefEntry)var6.nextElement()), this.scanner);
         }
      } catch (ParseException var7) {
         this.skipToSemicolon();
      }

   }

   private void initDcl(ValueEntry var1) throws IOException, ParseException {
      MethodEntry var2 = this.stFactory.methodEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);
      repIDStack.push(((IDLID)repIDStack.peek()).clone());
      ((IDLID)repIDStack.peek()).appendToName(this.token.name);
      if (this.token.type == 38) {
         var2.name("init");
         this.match(38);
         this.match(108);
      } else {
         this.match(47);
         var2.name(this.token.name);
         if (this.token.type == 81) {
            this.match(81);
         } else {
            this.match(80);
            this.match(108);
         }
      }

      if (this.token.type != 109) {
         while(true) {
            this.initParamDcl(var2);
            if (this.token.type == 109) {
               break;
            }

            this.match(104);
         }
      }

      var1.initializersAddElement(var2, this.scanner);
      this.match(109);
      this.match(100);
      repIDStack.pop();
   }

   private void initParamDcl(MethodEntry var1) throws IOException, ParseException {
      ParameterEntry var2 = this.stFactory.parameterEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);
      this.match(14);
      var2.passType(0);
      var2.type(this.paramTypeSpec(var1));
      var2.name(this.token.name);
      this.match(80);
      if (this.isntInList(var1.parameters(), var2.name())) {
         var1.addParameter(var2);
      }

   }

   private void valueBox(ModuleEntry var1, String var2) throws IOException, ParseException {
      repIDStack.push(((IDLID)repIDStack.peek()).clone());
      ValueBoxEntry var3 = this.stFactory.valueBoxEntry(var1, (IDLID)repIDStack.peek());
      var3.sourceFile(this.scanner.fileEntry());
      var3.name(var2);
      var3.comment(this.tokenHistory.lookBack(2).comment);
      SymtabEntry var4 = (SymtabEntry)symbolTable.get(var3.fullName());
      if (var4 != null && var4 instanceof ForwardEntry) {
         ParseException.forwardedValueBox(this.scanner, var3.fullName());
      }

      this.pigeonhole(var1, var3);
      ((IDLID)repIDStack.peek()).appendToName(var2);
      this.currentModule = var3;
      TypedefEntry var5 = this.stFactory.typedefEntry(var3, (IDLID)repIDStack.peek());
      var5.sourceFile(this.scanner.fileEntry());
      var5.comment(this.token.comment);
      boolean var6 = this.token.type == 29 || this.token.type == 34 || this.token.type == 9;
      var5.name("");
      var5.type(this.typeSpec(var5));
      if (var5.type() instanceof ValueBoxEntry) {
         ParseException.nestedValueBox(this.scanner);
      }

      var3.addStateElement(new InterfaceState(2, var5), this.scanner);
      if (var6) {
         var3.addContained(var5);
      }

      this.currentModule = var1;
      repIDStack.pop();
   }

   private void valueForwardDcl(ModuleEntry var1, String var2, boolean var3) throws IOException, ParseException {
      ForwardValueEntry var4 = this.stFactory.forwardValueEntry(var1, (IDLID)repIDStack.peek());
      var4.sourceFile(this.scanner.fileEntry());
      var4.name(var2);
      var4.setInterfaceType(var3 ? 1 : 0);
      var4.comment(this.tokenHistory.lookBack(var3 ? 3 : 2).comment);
      this.pigeonhole(var1, var4);
   }

   private void nativeDcl(SymtabEntry var1) throws IOException, ParseException {
      this.match(19);
      NativeEntry var2 = this.stFactory.nativeEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.tokenHistory.lookBack(1).comment);
      var2.name(this.token.name);
      this.match(80);
      this.pigeonhole(var1, var2);
   }

   private void constDcl(SymtabEntry var1) throws IOException, ParseException {
      this.match(5);
      ConstEntry var2 = this.stFactory.constEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.tokenHistory.lookBack(1).comment);
      this.constType(var2);
      var2.name(this.token.name);
      this.match(80);
      this.match(105);
      var2.value(this.constExp(var2));
      this.verifyConstType(var2.value(), typeOf(var2.type()));
      this.pigeonhole(var1, var2);
   }

   private void constType(SymtabEntry var1) throws IOException, ParseException {
      switch (this.token.type) {
         case 2:
            var1.type(this.booleanType());
            break;
         case 4:
         case 36:
            var1.type(this.charType());
            break;
         case 8:
         case 13:
            var1.type(this.floatingPtType());
            break;
         case 17:
         case 27:
         case 33:
            var1.type(this.integerType(var1));
            break;
         case 21:
            var1.type(this.octetType());
            break;
         case 28:
         case 37:
            var1.type(this.stringType(var1));
            break;
         case 80:
         case 124:
            var1.type(this.scopedName(var1.container(), this.stFactory.primitiveEntry()));
            if (this.hasArrayInfo(var1.type())) {
               ParseException.illegalArray(this.scanner, "const");
            }

            SymtabEntry var2 = typeOf(var1.type());
            if (!(var2 instanceof PrimitiveEntry) && !(var2 instanceof StringEntry)) {
               ParseException.wrongType(this.scanner, var1.fullName(), "primitive or string", this.entryName(var1.type()));
               var1.type(this.qualifiedEntry("long"));
            } else if (var2 instanceof PrimitiveEntry) {
               String var3 = overrideName("any");
               if (var2.name().equals(var3)) {
                  ParseException.wrongType(this.scanner, var1.fullName(), "primitive or string (except " + var3 + ')', var3);
                  var1.type(this.qualifiedEntry("long"));
               }
            }
            break;
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{17, 27, 33, 4, 36, 2, 13, 8, 28, 37, 80, 124}, this.token.type);
      }

   }

   private boolean hasArrayInfo(SymtabEntry var1) {
      while(var1 instanceof TypedefEntry) {
         if (((TypedefEntry)var1).arrayInfo().size() != 0) {
            return true;
         }

         var1 = var1.type();
      }

      return false;
   }

   public static String overrideName(String var0) {
      String var1 = (String)overrideNames.get(var0);
      return var1 == null ? var0 : var1;
   }

   private void verifyConstType(Expression var1, SymtabEntry var2) {
      Object var3 = var1.value();
      if (var3 instanceof BigInteger) {
         this.verifyIntegral((Number)var3, var2);
      } else if (var3 instanceof String) {
         this.verifyString(var1, var2);
      } else if (var3 instanceof Boolean) {
         this.verifyBoolean(var2);
      } else if (var3 instanceof Character) {
         this.verifyCharacter(var1, var2);
      } else if (!(var3 instanceof Float) && !(var3 instanceof Double)) {
         if (var3 instanceof ConstEntry) {
            this.verifyConstType(((ConstEntry)var3).value(), var2);
         } else {
            ParseException.wrongExprType(this.scanner, var2.fullName(), var3 == null ? "" : var3.toString());
         }
      } else {
         this.verifyFloat((Number)var3, var2);
      }

   }

   private void verifyIntegral(Number var1, SymtabEntry var2) {
      boolean var3 = false;
      if (var2 == this.qualifiedEntry("octet")) {
         if (var1.longValue() > 255L || var1.longValue() < 0L) {
            var3 = true;
         }
      } else if (var2 == this.qualifiedEntry("long")) {
         if (var1.longValue() > 2147483647L || var1.longValue() < -2147483648L) {
            var3 = true;
         }
      } else if (var2 == this.qualifiedEntry("short")) {
         if (var1.intValue() > 32767 || var1.intValue() < -32768) {
            var3 = true;
         }
      } else if (var2 == this.qualifiedEntry("unsigned long")) {
         if (var1.longValue() > 4294967295L || var1.longValue() < 0L) {
            var3 = true;
         }
      } else if (var2 == this.qualifiedEntry("unsigned short")) {
         if (var1.intValue() > 65535 || var1.intValue() < 0) {
            var3 = true;
         }
      } else {
         BigInteger var4;
         BigInteger var5;
         if (var2 == this.qualifiedEntry("long long")) {
            var4 = BigInteger.valueOf(Long.MAX_VALUE);
            var5 = BigInteger.valueOf(Long.MIN_VALUE);
            if (((BigInteger)var1).compareTo(var4) > 0 || ((BigInteger)var1).compareTo(var5) < 0) {
               var3 = true;
            }
         } else if (var2 == this.qualifiedEntry("unsigned long long")) {
            var4 = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2L)).add(BigInteger.valueOf(1L));
            var5 = BigInteger.valueOf(0L);
            if (((BigInteger)var1).compareTo(var4) > 0 || ((BigInteger)var1).compareTo(var5) < 0) {
               var3 = true;
            }
         } else {
            var4 = null;
            String var6 = "long";
            ParseException.wrongExprType(this.scanner, var2.fullName(), var6);
         }
      }

      if (var3) {
         ParseException.outOfRange(this.scanner, var1.toString(), var2.fullName());
      }

   }

   private void verifyString(Expression var1, SymtabEntry var2) {
      String var3 = (String)((String)var1.value());
      if (!(var2 instanceof StringEntry)) {
         ParseException.wrongExprType(this.scanner, var2.fullName(), var1.type());
      } else if (((StringEntry)var2).maxSize() != null) {
         Expression var4 = ((StringEntry)var2).maxSize();

         try {
            Number var5 = (Number)var4.value();
            if (var3.length() > var5.intValue()) {
               ParseException.stringTooLong(this.scanner, var3, var5.toString());
            }
         } catch (Exception var6) {
         }
      }

      if (!var1.type().equals(var2.name())) {
         ParseException.wrongExprType(this.scanner, var2.name(), var1.type());
      }

   }

   private void verifyBoolean(SymtabEntry var1) {
      if (!var1.name().equals(overrideName("boolean"))) {
         ParseException.wrongExprType(this.scanner, var1.name(), "boolean");
      }

   }

   private void verifyCharacter(Expression var1, SymtabEntry var2) {
      if (!var2.name().equals(overrideName("char")) && !var2.name().equals(overrideName("wchar")) || !var2.name().equals(var1.type())) {
         ParseException.wrongExprType(this.scanner, var2.fullName(), var1.type());
      }

   }

   private void verifyFloat(Number var1, SymtabEntry var2) {
      boolean var3 = false;
      if (var2.name().equals(overrideName("float"))) {
         double var4 = var1.doubleValue() < 0.0 ? var1.doubleValue() * -1.0 : var1.doubleValue();
         if (var4 != 0.0 && (var4 > 3.4028234663852886E38 || var4 < 1.401298464324817E-45)) {
            var3 = true;
         }
      } else if (!var2.name().equals(overrideName("double"))) {
         ParseException.wrongExprType(this.scanner, var2.fullName(), var1 instanceof Float ? "float" : "double");
      }

      if (var3) {
         ParseException.outOfRange(this.scanner, var1.toString(), var2.fullName());
      }

   }

   Expression constExp(SymtabEntry var1) throws IOException, ParseException {
      Expression var2 = this.orExpr((Expression)null, var1);
      if (var2.type() == null) {
         var2.type(var1.typeName());
      }

      try {
         var2.evaluate();
         if (var2 instanceof Terminal && var2.value() instanceof BigInteger && (overrideName(var2.type()).equals("float") || overrideName(var2.type()).indexOf("double") >= 0)) {
            var2.value(new Double(((BigInteger)var2.value()).doubleValue()));
         }
      } catch (EvaluationException var4) {
         ParseException.evaluationError(this.scanner, var4.toString());
      }

      return var2;
   }

   private Expression orExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.xorExpr((Expression)null, var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.xorExpr((Expression)null, var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(117)) {
         this.match(this.token.type);
         Or var4 = this.exprFactory.or(var1, (Expression)null);
         var4.type(var2.typeName());
         var4.rep(var1.rep() + " | ");
         return this.orExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression xorExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.andExpr((Expression)null, var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.andExpr((Expression)null, var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(118)) {
         this.match(this.token.type);
         Xor var4 = this.exprFactory.xor(var1, (Expression)null);
         var4.rep(var1.rep() + " ^ ");
         var4.type(var2.typeName());
         return this.xorExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression andExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.shiftExpr((Expression)null, var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.shiftExpr((Expression)null, var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(119)) {
         this.match(this.token.type);
         And var4 = this.exprFactory.and(var1, (Expression)null);
         var4.rep(var1.rep() + " & ");
         var4.type(var2.typeName());
         return this.andExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression shiftExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.addExpr((Expression)null, var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.addExpr((Expression)null, var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(125)) {
         this.match(this.token.type);
         ShiftLeft var5 = this.exprFactory.shiftLeft(var1, (Expression)null);
         var5.type(var2.typeName());
         var5.rep(var1.rep() + " << ");
         return this.shiftExpr(var5, var2);
      } else if (this.token.equals(126)) {
         this.match(this.token.type);
         ShiftRight var4 = this.exprFactory.shiftRight(var1, (Expression)null);
         var4.type(var2.typeName());
         var4.rep(var1.rep() + " >> ");
         return this.shiftExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression addExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.multExpr((Expression)null, var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.multExpr((Expression)null, var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(106)) {
         this.match(this.token.type);
         Plus var5 = this.exprFactory.plus(var1, (Expression)null);
         var5.type(var2.typeName());
         var5.rep(var1.rep() + " + ");
         return this.addExpr(var5, var2);
      } else if (this.token.equals(107)) {
         this.match(this.token.type);
         Minus var4 = this.exprFactory.minus(var1, (Expression)null);
         var4.type(var2.typeName());
         var4.rep(var1.rep() + " - ");
         return this.addExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression multExpr(Expression var1, SymtabEntry var2) throws IOException, ParseException {
      if (var1 == null) {
         var1 = this.unaryExpr(var2);
      } else {
         BinaryExpr var3 = (BinaryExpr)var1;
         var3.right(this.unaryExpr(var2));
         var1.rep(var1.rep() + var3.right().rep());
      }

      if (this.token.equals(120)) {
         this.match(this.token.type);
         Times var6 = this.exprFactory.times(var1, (Expression)null);
         var6.type(var2.typeName());
         var6.rep(var1.rep() + " * ");
         return this.multExpr(var6, var2);
      } else if (this.token.equals(121)) {
         this.match(this.token.type);
         Divide var5 = this.exprFactory.divide(var1, (Expression)null);
         var5.type(var2.typeName());
         var5.rep(var1.rep() + " / ");
         return this.multExpr(var5, var2);
      } else if (this.token.equals(122)) {
         this.match(this.token.type);
         Modulo var4 = this.exprFactory.modulo(var1, (Expression)null);
         var4.type(var2.typeName());
         var4.rep(var1.rep() + " % ");
         return this.multExpr(var4, var2);
      } else {
         return var1;
      }
   }

   private Expression unaryExpr(SymtabEntry var1) throws IOException, ParseException {
      Expression var2;
      if (this.token.equals(106)) {
         this.match(this.token.type);
         var2 = this.primaryExpr(var1);
         Positive var5 = this.exprFactory.positive(var2);
         var5.type(var1.typeName());
         var5.rep('+' + var2.rep());
         return var5;
      } else if (this.token.equals(107)) {
         this.match(this.token.type);
         var2 = this.primaryExpr(var1);
         Negative var4 = this.exprFactory.negative(var2);
         var4.type(var1.typeName());
         var4.rep('-' + var2.rep());
         return var4;
      } else if (this.token.equals(123)) {
         this.match(this.token.type);
         var2 = this.primaryExpr(var1);
         Not var3 = this.exprFactory.not(var2);
         var3.type(var1.typeName());
         var3.rep('~' + var2.rep());
         return var3;
      } else {
         return this.primaryExpr(var1);
      }
   }

   private Expression primaryExpr(SymtabEntry var1) throws IOException, ParseException {
      Object var2 = null;
      if (this.parsingConditionalExpr) {
         this.prep.token = this.token;
         var2 = this.prep.primaryExpr(var1);
         this.token = this.prep.token;
      } else {
         switch (this.token.type) {
            case 80:
            case 124:
               ConstEntry var3 = this.stFactory.constEntry();
               var3.value(this.exprFactory.terminal("1", BigInteger.valueOf(1L)));
               SymtabEntry var4 = this.scopedName(var1.container(), var3);
               if (!(var4 instanceof ConstEntry)) {
                  ParseException.invalidConst(this.scanner, var4.fullName());
                  var2 = this.exprFactory.terminal("1", BigInteger.valueOf(1L));
               } else {
                  var2 = this.exprFactory.terminal((ConstEntry)var4);
               }
               break;
            case 108:
               this.match(108);
               var2 = this.constExp(var1);
               this.match(109);
               ((Expression)var2).rep('(' + ((Expression)var2).rep() + ')');
               break;
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
               var2 = this.literal(var1);
               break;
            default:
               throw ParseException.syntaxError(this.scanner, new int[]{80, 124, 205, 108}, this.token.type);
         }
      }

      return (Expression)var2;
   }

   Expression literal(SymtabEntry var1) throws IOException, ParseException {
      String var2 = this.token.name;
      Object var3 = null;
      switch (this.token.type) {
         case 200:
            var3 = this.booleanLiteral();
            break;
         case 201:
            boolean var4 = this.token.isWide();
            this.match(201);
            var3 = this.exprFactory.terminal("'" + var2.substring(1) + "'", new Character(var2.charAt(0)), var4);
            break;
         case 202:
            this.match(202);

            try {
               var3 = this.exprFactory.terminal(var2, this.parseString(var2));
               ((Expression)var3).type(var1.typeName());
            } catch (NumberFormatException var7) {
               ParseException.notANumber(this.scanner, var2);
               var3 = this.exprFactory.terminal("0", BigInteger.valueOf(0L));
            }
            break;
         case 203:
            this.match(203);

            try {
               var3 = this.exprFactory.terminal(var2, new Double(var2));
               ((Expression)var3).type(var1.typeName());
            } catch (NumberFormatException var6) {
               ParseException.notANumber(this.scanner, var2);
            }
            break;
         case 204:
            var3 = this.stringLiteral();
            break;
         default:
            throw ParseException.syntaxError(this.scanner, 205, this.token.type);
      }

      return (Expression)var3;
   }

   private BigInteger parseString(String var1) throws NumberFormatException {
      byte var2 = 10;
      if (var1.length() > 1 && var1.charAt(0) == '0') {
         if (var1.charAt(1) != 'x' && var1.charAt(1) != 'X') {
            var2 = 8;
         } else {
            var1 = var1.substring(2);
            var2 = 16;
         }
      }

      return new BigInteger(var1, var2);
   }

   private Terminal booleanLiteral() throws IOException, ParseException {
      Boolean var1 = null;
      if (this.token.name.equals("TRUE")) {
         var1 = new Boolean(true);
      } else if (this.token.name.equals("FALSE")) {
         var1 = new Boolean(false);
      } else {
         ParseException.invalidConst(this.scanner, this.token.name);
         var1 = new Boolean(false);
      }

      String var2 = this.token.name;
      this.match(200);
      return this.exprFactory.terminal(var2, var1);
   }

   private Expression stringLiteral() throws IOException, ParseException {
      boolean var1 = this.token.isWide();
      String var2 = "";

      do {
         var2 = var2 + this.token.name;
         this.match(204);
      } while(this.token.equals(204));

      Terminal var3 = this.exprFactory.terminal(var2, var1);
      var3.rep('"' + var2 + '"');
      return var3;
   }

   private Expression positiveIntConst(SymtabEntry var1) throws IOException, ParseException {
      Object var2 = this.constExp(var1);

      Object var3;
      for(var3 = ((Expression)var2).value(); var3 instanceof ConstEntry; var3 = ((ConstEntry)var3).value().value()) {
      }

      if (var3 instanceof Number && !(var3 instanceof Float) && !(var3 instanceof Double)) {
         if (((BigInteger)var3).compareTo(BigInteger.valueOf(0L)) <= 0) {
            ParseException.notPositiveInt(this.scanner, var3.toString());
            var2 = this.exprFactory.terminal("1", BigInteger.valueOf(1L));
         }
      } else {
         ParseException.notPositiveInt(this.scanner, ((Expression)var2).rep());
         var2 = this.exprFactory.terminal("1", BigInteger.valueOf(1L));
      }

      return (Expression)var2;
   }

   private SymtabEntry typeDcl(SymtabEntry var1) throws IOException, ParseException {
      switch (this.token.type) {
         case 9:
            return this.enumType(var1);
         case 29:
            return this.structType(var1);
         case 32:
            this.match(32);
            return this.typeDeclarator(var1);
         case 34:
            return this.unionType(var1);
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{32, 29, 34, 9}, this.token.type);
      }
   }

   private TypedefEntry typeDeclarator(SymtabEntry var1) throws IOException, ParseException {
      TypedefEntry var2 = this.stFactory.typedefEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.tokenHistory.lookBack(1).comment);
      var2.type(this.typeSpec(var1));
      Vector var3 = new Vector();
      this.declarators(var2, var3);
      Enumeration var4 = var3.elements();

      while(var4.hasMoreElements()) {
         this.pigeonhole(var1, (SymtabEntry)var4.nextElement());
      }

      return var2;
   }

   private SymtabEntry typeSpec(SymtabEntry var1) throws IOException, ParseException {
      return this.token.type != 29 && this.token.type != 34 && this.token.type != 9 ? this.simpleTypeSpec(var1, true) : this.constrTypeSpec(var1);
   }

   private SymtabEntry simpleTypeSpec(SymtabEntry var1, boolean var2) throws IOException, ParseException {
      if (this.token.type != 80 && this.token.type != 124 && this.token.type != 20 && this.token.type != 45) {
         return this.token.type != 26 && this.token.type != 28 && this.token.type != 37 ? this.baseTypeSpec(var1) : this.templateTypeSpec(var1);
      } else {
         SymtabEntry var3 = !(var1 instanceof InterfaceEntry) && !(var1 instanceof ModuleEntry) && !(var1 instanceof StructEntry) && !(var1 instanceof UnionEntry) ? var1.container() : var1;
         return this.scopedName(var3, this.stFactory.primitiveEntry(), var2);
      }
   }

   private SymtabEntry baseTypeSpec(SymtabEntry var1) throws IOException, ParseException {
      switch (this.token.type) {
         case 0:
            return this.anyType();
         case 1:
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         case 20:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 34:
         case 35:
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{13, 8, 17, 27, 33, 4, 36, 2, 21, 0}, this.token.type);
         case 2:
            return this.booleanType();
         case 4:
         case 36:
            return this.charType();
         case 8:
         case 13:
            return this.floatingPtType();
         case 17:
         case 27:
         case 33:
            return this.integerType(var1);
         case 21:
            return this.octetType();
      }
   }

   private SymtabEntry templateTypeSpec(SymtabEntry var1) throws IOException, ParseException {
      switch (this.token.type) {
         case 26:
            return this.sequenceType(var1);
         case 28:
         case 37:
            return this.stringType(var1);
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{26, 28, 37}, this.token.type);
      }
   }

   private SymtabEntry constrTypeSpec(SymtabEntry var1) throws IOException, ParseException {
      switch (this.token.type) {
         case 9:
            return this.enumType(var1);
         case 29:
            return this.structType(var1);
         case 34:
            return this.unionType(var1);
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{29, 34, 9}, this.token.type);
      }
   }

   private void declarators(TypedefEntry var1, Vector var2) throws IOException, ParseException {
      while(true) {
         TypedefEntry var3 = (TypedefEntry)var1.clone();
         this.declarator(var3);
         if (this.isntInList(var2, var3.name())) {
            var2.addElement(var3);
         }

         if (this.token.type != 104) {
            return;
         }

         this.match(104);
      }
   }

   private void declarator(TypedefEntry var1) throws IOException, ParseException {
      var1.name(this.token.name);
      if (!this.token.comment.text().equals("")) {
         var1.comment(this.token.comment);
      }

      this.match(80);

      while(this.token.type == 112) {
         this.fixedArraySize(var1);
      }

   }

   private PrimitiveEntry floatingPtType() throws IOException, ParseException {
      String var1 = "double";
      if (this.token.type == 13) {
         this.match(13);
         var1 = "float";
      } else if (this.token.type == 8) {
         this.match(8);
      } else {
         int[] var10000 = new int[]{13, 8};
         ParseException.syntaxError(this.scanner, new int[]{13, 8}, this.token.type);
      }

      PrimitiveEntry var2 = null;

      try {
         var2 = (PrimitiveEntry)this.qualifiedEntry(var1);
      } catch (ClassCastException var4) {
         ParseException.undeclaredType(this.scanner, var1);
      }

      return var2;
   }

   private PrimitiveEntry integerType(SymtabEntry var1) throws IOException, ParseException {
      String var2 = "";
      if (this.token.type == 33) {
         this.match(33);
         var2 = "unsigned ";
      }

      var2 = var2 + this.signedInt();
      PrimitiveEntry var3 = null;

      try {
         var3 = (PrimitiveEntry)this.qualifiedEntry(var2);
      } catch (ClassCastException var5) {
         ParseException.undeclaredType(this.scanner, var2);
      }

      return var3;
   }

   private String signedInt() throws IOException, ParseException {
      String var1 = "long";
      if (this.token.type == 17) {
         this.match(17);
         if (this.token.type == 17) {
            var1 = "long long";
            this.match(17);
         }
      } else if (this.token.type == 27) {
         var1 = "short";
         this.match(27);
      } else {
         ParseException.syntaxError(this.scanner, new int[]{17, 27}, this.token.type);
      }

      return var1;
   }

   private PrimitiveEntry charType() throws IOException, ParseException {
      String var1;
      if (this.token.type == 4) {
         this.match(4);
         var1 = "char";
      } else {
         this.match(36);
         var1 = "wchar";
      }

      PrimitiveEntry var2 = null;

      try {
         var2 = (PrimitiveEntry)this.qualifiedEntry(var1);
      } catch (ClassCastException var4) {
         ParseException.undeclaredType(this.scanner, overrideName(var1));
      }

      return var2;
   }

   private PrimitiveEntry booleanType() throws IOException, ParseException {
      PrimitiveEntry var1 = null;
      this.match(2);

      try {
         var1 = (PrimitiveEntry)this.qualifiedEntry("boolean");
      } catch (ClassCastException var3) {
         ParseException.undeclaredType(this.scanner, overrideName("boolean"));
      }

      return var1;
   }

   private PrimitiveEntry octetType() throws IOException, ParseException {
      PrimitiveEntry var1 = null;
      this.match(21);

      try {
         var1 = (PrimitiveEntry)this.qualifiedEntry("octet");
      } catch (ClassCastException var3) {
         ParseException.undeclaredType(this.scanner, overrideName("octet"));
      }

      return var1;
   }

   private SymtabEntry anyType() throws IOException, ParseException {
      this.match(0);

      try {
         return this.qualifiedEntry("any");
      } catch (ClassCastException var2) {
         ParseException.undeclaredType(this.scanner, overrideName("any"));
         return null;
      }
   }

   private StructEntry structType(SymtabEntry var1) throws IOException, ParseException {
      this.match(29);
      String var2 = this.token.name;
      this.match(80);
      StructEntry var3 = null;
      if (this.token.type == 101) {
         repIDStack.push(((IDLID)repIDStack.peek()).clone());
         var3 = this.makeStructEntry(var2, var1, false);
         ((IDLID)repIDStack.peek()).appendToName(var2);
         this.prep.openScope(var3);
         this.match(101);
         this.member(var3);
         this.memberList2(var3);
         this.prep.closeScope(var3);
         this.match(102);
         repIDStack.pop();
      } else {
         if (!this.token.equals(100)) {
            throw ParseException.syntaxError(this.scanner, new int[]{100, 101}, this.token.type);
         }

         var3 = this.makeStructEntry(var2, var1, true);
      }

      return var3;
   }

   private StructEntry makeStructEntry(String var1, SymtabEntry var2, boolean var3) {
      StructEntry var4 = this.stFactory.structEntry(var2, (IDLID)repIDStack.peek());
      var4.isReferencable(!var3);
      var4.sourceFile(this.scanner.fileEntry());
      var4.name(var1);
      var4.comment(this.tokenHistory.lookBack(1).comment);
      this.pigeonhole(var2, var4);
      return var4;
   }

   private void memberList2(StructEntry var1) throws IOException {
      while(this.token.type != 102) {
         this.member(var1);
      }

   }

   private void member(StructEntry var1) throws IOException {
      TypedefEntry var2 = this.stFactory.typedefEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);

      try {
         var2.type(this.typeSpec(var1));
         if (var2.type() == var1) {
            throw ParseException.recursive(this.scanner, var1.fullName(), this.token.name == null ? "" : this.token.name);
         }

         if (typeOf(var2) instanceof ExceptionEntry) {
            throw ParseException.illegalException(this.scanner, this.entryName(var1));
         }

         this.declarators(var2, var1.members());
         this.match(100);
      } catch (ParseException var4) {
         this.skipToSemicolon();
      }

   }

   private final boolean isConstTypeSpec(Token var1) {
      return var1.type == 29 || var1.type == 34 || var1.type == 9;
   }

   private UnionEntry unionType(SymtabEntry var1) throws IOException, ParseException {
      this.match(34);
      String var2 = this.token.name;
      this.match(80);
      UnionEntry var3 = null;
      if (this.token.type == 30) {
         repIDStack.push(((IDLID)repIDStack.peek()).clone());
         var3 = this.makeUnionEntry(var2, var1, false);
         ((IDLID)repIDStack.peek()).appendToName(var2);
         this.match(30);
         this.match(108);
         var3.type(this.switchTypeSpec(var3));
         this.match(109);
         this.prep.openScope(var3);
         this.match(101);
         this.switchBody(var3);
         this.verifyUnion(var3);
         this.prep.closeScope(var3);
         this.match(102);
         repIDStack.pop();
      } else {
         if (!this.token.equals(100)) {
            throw ParseException.syntaxError(this.scanner, new int[]{100, 30}, this.token.type);
         }

         var3 = this.makeUnionEntry(var2, var1, true);
      }

      return var3;
   }

   private UnionEntry makeUnionEntry(String var1, SymtabEntry var2, boolean var3) {
      UnionEntry var4 = this.stFactory.unionEntry(var2, (IDLID)repIDStack.peek());
      var4.isReferencable(!var3);
      var4.sourceFile(this.scanner.fileEntry());
      var4.name(var1);
      var4.comment(this.tokenHistory.lookBack(1).comment);
      this.pigeonhole(var2, var4);
      return var4;
   }

   private void verifyUnion(UnionEntry var1) {
      if (var1.typeName().equals(overrideName("boolean"))) {
         if (this.caseCount(var1) > 2L) {
            ParseException.noDefault(this.scanner);
         }
      } else if (var1.type() instanceof EnumEntry && this.caseCount(var1) > (long)((EnumEntry)var1.type()).elements().size()) {
         ParseException.noDefault(this.scanner);
      }

   }

   private long caseCount(UnionEntry var1) {
      long var2 = 0L;
      Enumeration var4 = var1.branches().elements();

      while(var4.hasMoreElements()) {
         UnionBranch var5 = (UnionBranch)var4.nextElement();
         var2 += (long)var5.labels.size();
         if (var5.isDefault) {
            ++var2;
         }
      }

      return var2;
   }

   private SymtabEntry switchTypeSpec(UnionEntry var1) throws IOException, ParseException {
      SymtabEntry var2 = null;
      switch (this.token.type) {
         case 2:
            return this.booleanType();
         case 4:
         case 36:
            return this.charType();
         case 9:
            return this.enumType(var1);
         case 17:
         case 27:
         case 33:
            return this.integerType(var1);
         case 80:
         case 124:
            var2 = this.scopedName(var1, this.stFactory.primitiveEntry());
            if (this.hasArrayInfo(var1.type())) {
               ParseException.illegalArray(this.scanner, "switch");
            }

            SymtabEntry var3 = typeOf(var2);
            if (!(var3 instanceof EnumEntry) && !(var3 instanceof PrimitiveEntry)) {
               ParseException.wrongType(this.scanner, var2.fullName(), "long, unsigned long, short, unsigned short, char, boolean, enum", this.entryName(var2.type()));
            } else if (var2 instanceof PrimitiveEntry) {
               SymtabEntry var4 = this.qualifiedEntry("octet");
               SymtabEntry var5 = this.qualifiedEntry("float");
               SymtabEntry var6 = this.qualifiedEntry("double");
               if (var3 == var4 || var3 == var5 || var3 == var6) {
                  ParseException.wrongType(this.scanner, var2.fullName(), "long, unsigned long, short, unsigned short, char, boolean, enum", this.entryName(var2.type()));
               }
            }

            return var2;
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{17, 27, 33, 4, 2, 9, 80, 124}, this.token.type);
      }
   }

   private void switchBody(UnionEntry var1) throws IOException, ParseException {
      this.caseProd(var1);

      while(!this.token.equals(102)) {
         this.caseProd(var1);
      }

      var1.defaultBranch(this.defaultBranch == null ? null : this.defaultBranch.typedef);
      this.defaultBranch = null;
   }

   private void caseProd(UnionEntry var1) throws IOException, ParseException {
      UnionBranch var2 = new UnionBranch();
      var1.addBranch(var2);
      this.caseLabel(var1, var2);

      while(this.token.equals(3) || this.token.equals(7)) {
         this.caseLabel(var1, var2);
      }

      this.elementSpec(var1, var2);
      this.match(100);
   }

   private void caseLabel(UnionEntry var1, UnionBranch var2) throws IOException, ParseException {
      if (this.token.type == 3) {
         this.match(3);
         ConstEntry var3 = this.stFactory.constEntry(var1, (IDLID)repIDStack.peek());
         var3.sourceFile(this.scanner.fileEntry());
         var3.type(var1);
         SymtabEntry var5 = typeOf(var1.type());
         Expression var4;
         if (var5 instanceof EnumEntry) {
            var4 = this.matchEnum((EnumEntry)var5);
         } else {
            var4 = this.constExp(var3);
            this.verifyConstType(var4, var5);
         }

         if (var1.has(var4)) {
            ParseException.branchLabel(this.scanner, var4.rep());
         }

         var2.labels.addElement(var4);
         this.match(103);
      } else {
         if (this.token.type != 7) {
            throw ParseException.syntaxError(this.scanner, new int[]{3, 7}, this.token.type);
         }

         this.match(7);
         this.match(103);
         if (var1.defaultBranch() != null) {
            ParseException.alreadyDefaulted(this.scanner);
         }

         var2.isDefault = true;
         this.defaultBranch = var2;
      }

   }

   private Expression matchEnum(EnumEntry var1) throws IOException, ParseException {
      SymtabEntry var2 = this.scopedName(var1.container(), new SymtabEntry());
      return this.exprFactory.terminal(var2.name(), false);
   }

   private void elementSpec(UnionEntry var1, UnionBranch var2) throws IOException, ParseException {
      TypedefEntry var3 = this.stFactory.typedefEntry(var1, (IDLID)repIDStack.peek());
      var3.sourceFile(this.scanner.fileEntry());
      var3.comment(this.token.comment);
      var3.type(this.typeSpec(var1));
      if (var3.type() == var1) {
         throw ParseException.recursive(this.scanner, var1.fullName(), this.token.name == null ? "" : this.token.name);
      } else if (typeOf(var3) instanceof ExceptionEntry) {
         throw ParseException.illegalException(this.scanner, this.entryName(var1));
      } else {
         this.declarator(var3);
         var2.typedef = var3;
         if (var1.has(var3)) {
            ParseException.branchName(this.scanner, var3.name());
         }

      }
   }

   private EnumEntry enumType(SymtabEntry var1) throws IOException, ParseException {
      this.match(9);
      EnumEntry var2 = this.newEnumEntry(var1);
      var2.comment(this.tokenHistory.lookBack(1).comment);
      var2.name(this.token.name);
      this.match(80);
      this.prep.openScope(var2);
      this.match(101);
      if (this.isntInStringList(var2.elements(), this.token.name)) {
         var2.addElement(this.token.name);
         SymtabEntry var3 = new SymtabEntry(var1, (IDLID)repIDStack.peek());
         if (var3.module().equals("")) {
            var3.module(var3.name());
         } else if (!var3.name().equals("")) {
            var3.module(var3.module() + "/" + var3.name());
         }

         var3.name(this.token.name);
         this.pigeonhole(var2.container(), var3);
      }

      this.match(80);
      this.enumType2(var2);
      this.prep.closeScope(var2);
      this.match(102);
      return var2;
   }

   private void enumType2(EnumEntry var1) throws IOException, ParseException {
      while(this.token.type == 104) {
         this.match(104);
         String var2 = this.token.name;
         this.match(80);
         if (this.isntInStringList(var1.elements(), var2)) {
            var1.addElement(var2);
            SymtabEntry var3 = new SymtabEntry(var1.container(), (IDLID)repIDStack.peek());
            if (var3.module().equals("")) {
               var3.module(var3.name());
            } else if (!var3.name().equals("")) {
               var3.module(var3.module() + "/" + var3.name());
            }

            var3.name(var2);
            this.pigeonhole(var1.container(), var3);
         }
      }

   }

   private SequenceEntry sequenceType(SymtabEntry var1) throws IOException, ParseException {
      this.match(26);
      this.match(110);
      SequenceEntry var2 = this.newSequenceEntry(var1);
      SymtabEntry var3 = this.simpleTypeSpec(var2, false);
      var2.type(var3);
      if (!var3.isReferencable()) {
         try {
            Object var4 = (List)var3.dynamicVariable(ftlKey);
            if (var4 == null) {
               var4 = new ArrayList();
               var3.dynamicVariable(ftlKey, var4);
            }

            ((List)var4).add(var2);
         } catch (NoSuchFieldException var5) {
            throw new IllegalStateException();
         }
      }

      if (this.token.type == 104) {
         this.match(104);
         ConstEntry var6 = this.stFactory.constEntry(var2, (IDLID)repIDStack.peek());
         var6.sourceFile(this.scanner.fileEntry());
         var6.type(this.qualifiedEntry("long"));
         var2.maxSize(this.positiveIntConst(var6));
         this.verifyConstType(var2.maxSize(), this.qualifiedEntry("long"));
      }

      this.match(111);
      return var2;
   }

   private StringEntry stringType(SymtabEntry var1) throws IOException, ParseException {
      StringEntry var2 = this.stFactory.stringEntry();
      if (this.token.type == 28) {
         var2.name(overrideName("string"));
         this.match(28);
      } else {
         var2.name(overrideName("wstring"));
         this.match(37);
      }

      var2.maxSize(this.stringType2(var1));
      return var2;
   }

   private Expression stringType2(SymtabEntry var1) throws IOException, ParseException {
      if (this.token.type == 110) {
         this.match(110);
         ConstEntry var2 = this.stFactory.constEntry(var1, (IDLID)repIDStack.peek());
         var2.sourceFile(this.scanner.fileEntry());
         var2.type(this.qualifiedEntry("long"));
         Expression var3 = this.positiveIntConst(var2);
         this.verifyConstType(var3, this.qualifiedEntry("long"));
         this.match(111);
         return var3;
      } else {
         return null;
      }
   }

   private void fixedArraySize(TypedefEntry var1) throws IOException, ParseException {
      this.match(112);
      ConstEntry var2 = this.stFactory.constEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.type(this.qualifiedEntry("long"));
      Expression var3 = this.positiveIntConst(var2);
      var1.addArrayInfo(var3);
      this.verifyConstType(var3, this.qualifiedEntry("long"));
      this.match(113);
   }

   private void attrDcl(InterfaceEntry var1) throws IOException, ParseException {
      AttributeEntry var2 = this.stFactory.attributeEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);
      Comment var3 = var2.comment();
      if (this.token.type == 25) {
         this.match(25);
         var2.readOnly(true);
      }

      this.match(1);
      var2.type(this.paramTypeSpec(var2));
      var2.name(this.token.name);
      if (!this.token.comment.text().equals("")) {
         var2.comment(this.token.comment);
      }

      var1.methodsAddElement(var2, this.scanner);
      this.pigeonholeMethod(var1, var2);
      if (!this.token.comment.text().equals("")) {
         AttributeEntry var4 = (AttributeEntry)var2.clone();
         var4.comment(var3);
         this.match(80);
         this.attrDcl2(var1, var4);
      } else {
         this.match(80);
         this.attrDcl2(var1, var2);
      }

   }

   private void attrDcl2(InterfaceEntry var1, AttributeEntry var2) throws IOException, ParseException {
      while(this.token.type == 104) {
         this.match(104);
         AttributeEntry var3 = (AttributeEntry)var2.clone();
         var3.name(this.token.name);
         if (!this.token.comment.text().equals("")) {
            var3.comment(this.token.comment);
         }

         var1.methodsAddElement(var3, this.scanner);
         this.pigeonholeMethod(var1, var3);
         this.match(80);
      }

   }

   private void exceptDcl(SymtabEntry var1) throws IOException, ParseException {
      this.match(10);
      repIDStack.push(((IDLID)repIDStack.peek()).clone());
      ExceptionEntry var2 = this.stFactory.exceptionEntry(var1, (IDLID)repIDStack.peek());
      ((IDLID)repIDStack.peek()).appendToName(this.token.name);
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.tokenHistory.lookBack(1).comment);
      var2.name(this.token.name);
      this.match(80);
      this.pigeonhole(var1, var2);
      if (this.token.equals(101)) {
         this.prep.openScope(var2);
         this.match(101);
         this.memberList2(var2);
         this.prep.closeScope(var2);
         this.match(102);
         repIDStack.pop();
      } else {
         throw ParseException.syntaxError(this.scanner, 101, this.token.type);
      }
   }

   private void opDcl(InterfaceEntry var1) throws IOException, ParseException {
      MethodEntry var2 = this.stFactory.methodEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);
      if (this.token.type == 22) {
         this.match(22);
         var2.oneway(true);
      }

      var2.type(this.opTypeSpec(var2));
      repIDStack.push(((IDLID)repIDStack.peek()).clone());
      ((IDLID)repIDStack.peek()).appendToName(this.token.name);
      var2.name(this.token.name);
      var1.methodsAddElement(var2, this.scanner);
      this.pigeonholeMethod(var1, var2);
      this.opDcl2(var2);
      if (var2.oneway()) {
         this.checkIfOpLegalForOneway(var2);
      }

      repIDStack.pop();
   }

   private void checkIfOpLegalForOneway(MethodEntry var1) {
      boolean var2 = false;
      if (var1.type() == null && var1.exceptions().size() == 0) {
         Enumeration var3 = var1.parameters().elements();

         while(var3.hasMoreElements()) {
            if (((ParameterEntry)var3.nextElement()).passType() != 0) {
               var2 = true;
               break;
            }
         }
      } else {
         var2 = true;
      }

      if (var2) {
         ParseException.oneway(this.scanner, var1.name());
      }

   }

   private void opDcl2(MethodEntry var1) throws IOException, ParseException {
      if (this.token.equals(81)) {
         this.match(81);
         this.parameterDcls2(var1);
      } else {
         this.match(80);
         this.parameterDcls(var1);
      }

      this.opDcl3(var1);
   }

   private void opDcl3(MethodEntry var1) throws IOException, ParseException {
      if (this.token.type != 100) {
         if (!this.token.equals(24) && !this.token.equals(6)) {
            throw ParseException.syntaxError(this.scanner, new int[]{24, 6, 100}, this.token.type);
         }

         if (this.token.type == 24) {
            this.raisesExpr(var1);
         }

         if (this.token.type == 6) {
            this.contextExpr(var1);
         }
      }

   }

   private SymtabEntry opTypeSpec(SymtabEntry var1) throws IOException, ParseException {
      SymtabEntry var2 = null;
      if (this.token.type == 35) {
         this.match(35);
      } else {
         var2 = this.paramTypeSpec(var1);
      }

      return var2;
   }

   private void parameterDcls(MethodEntry var1) throws IOException, ParseException {
      this.match(108);
      this.parameterDcls2(var1);
   }

   private void parameterDcls2(MethodEntry var1) throws IOException, ParseException {
      if (this.token.type == 109) {
         this.match(109);
      } else {
         this.paramDcl(var1);

         while(this.token.type == 104) {
            this.match(104);
            this.paramDcl(var1);
         }

         this.match(109);
      }

   }

   private void paramDcl(MethodEntry var1) throws IOException, ParseException {
      ParameterEntry var2 = this.stFactory.parameterEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.comment(this.token.comment);
      this.paramAttribute(var2);
      var2.type(this.paramTypeSpec(var1));
      var2.name(this.token.name);
      this.match(80);
      if (this.isntInList(var1.parameters(), var2.name())) {
         var1.addParameter(var2);
      }

   }

   private void paramAttribute(ParameterEntry var1) throws IOException, ParseException {
      if (this.token.type == 14) {
         var1.passType(0);
         this.match(14);
      } else if (this.token.type == 23) {
         var1.passType(2);
         this.match(23);
      } else {
         if (this.token.type != 15) {
            throw ParseException.syntaxError(this.scanner, new int[]{14, 23, 15}, this.token.type);
         }

         var1.passType(1);
         this.match(15);
      }

   }

   private void raisesExpr(MethodEntry var1) throws IOException, ParseException {
      this.match(24);
      this.match(108);
      Comment var2 = this.token.comment;
      SymtabEntry var3 = this.scopedName(var1.container(), this.stFactory.exceptionEntry());
      if (typeOf(var3) instanceof ExceptionEntry) {
         var3.comment(var2);
         if (this.isntInList(var1.exceptions(), var3)) {
            var1.exceptionsAddElement((ExceptionEntry)var3);
         }
      } else {
         ParseException.wrongType(this.scanner, var3.fullName(), "exception", this.entryName(var3.type()));
      }

      this.raisesExpr2(var1);
      this.match(109);
   }

   private void raisesExpr2(MethodEntry var1) throws IOException, ParseException {
      while(this.token.type == 104) {
         this.match(104);
         Comment var2 = this.token.comment;
         SymtabEntry var3 = this.scopedName(var1.container(), this.stFactory.exceptionEntry());
         if (typeOf(var3) instanceof ExceptionEntry) {
            var3.comment(var2);
            if (this.isntInList(var1.exceptions(), var3)) {
               var1.addException((ExceptionEntry)var3);
            }
         } else {
            ParseException.wrongType(this.scanner, var3.fullName(), "exception", this.entryName(var3.type()));
         }
      }

   }

   private void contextExpr(MethodEntry var1) throws IOException, ParseException {
      this.match(6);
      this.match(108);
      String var2 = (String)this.stringLiteral().value();
      if (this.isntInStringList(var1.contexts(), var2)) {
         var1.addContext(var2);
      }

      this.contextExpr2(var1);
      this.match(109);
   }

   private void contextExpr2(MethodEntry var1) throws IOException, ParseException {
      while(this.token.type == 104) {
         this.match(104);
         String var2 = (String)this.stringLiteral().value();
         if (this.isntInStringList(var1.contexts(), var2)) {
            var1.addContext(var2);
         }
      }

   }

   private SymtabEntry paramTypeSpec(SymtabEntry var1) throws IOException, ParseException {
      SymtabEntry var2 = null;
      switch (this.token.type) {
         case 0:
         case 2:
         case 4:
         case 8:
         case 13:
         case 17:
         case 21:
         case 27:
         case 33:
         case 36:
            return this.baseTypeSpec(var1);
         case 20:
         case 45:
         case 80:
         case 124:
            var2 = this.scopedName(var1.container(), this.stFactory.primitiveEntry());
            if (typeOf(var2) instanceof AttributeEntry) {
               ParseException.attributeNotType(this.scanner, var2.name());
            } else if (typeOf(var2) instanceof MethodEntry) {
               ParseException.operationNotType(this.scanner, var2.name());
            }

            return var2;
         case 28:
         case 37:
            return this.stringType(var1);
         default:
            throw ParseException.syntaxError(this.scanner, new int[]{13, 8, 17, 27, 33, 4, 36, 2, 21, 0, 28, 37, 80, 124, 45}, this.token.type);
      }
   }

   private void match(int var1) throws IOException, ParseException {
      ParseException var2 = null;
      if (!this.token.equals(var1)) {
         var2 = ParseException.syntaxError(this.scanner, var1, this.token.type);
         if (var1 == 100) {
            return;
         }
      }

      this.token = this.scanner.getToken();
      this.issueTokenWarnings();
      this.tokenHistory.insert(this.token);

      while(this.token.isDirective()) {
         this.token = this.prep.process(this.token);
      }

      if (this.token.equals(80) || this.token.equals(81)) {
         String var3 = (String)this.symbols.get(this.token.name);
         if (var3 != null && !var3.equals("")) {
            if (this.macros.contains(this.token.name)) {
               this.scanner.scanString(this.prep.expandMacro(var3, this.token));
               this.match(this.token.type);
            } else {
               this.scanner.scanString(var3);
               this.match(this.token.type);
            }
         }
      }

      if (var2 != null) {
         throw var2;
      }
   }

   private void issueTokenWarnings() {
      if (!this.noWarn) {
         if ((this.token.equals(80) || this.token.equals(81)) && !this.token.isEscaped() && this.token.collidesWithKeyword()) {
            ParseException.warning(this.scanner, Util.getMessage("Migration.keywordCollision", this.token.name));
         }

         if (this.token.isKeyword() && this.token.isDeprecated()) {
            ParseException.warning(this.scanner, Util.getMessage("Deprecated.keyword", this.token.toString()));
         }

      }
   }

   private ModuleEntry newModule(ModuleEntry var1) {
      ModuleEntry var2 = this.stFactory.moduleEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.name(this.token.name);
      SymtabEntry var3 = (SymtabEntry)symbolTable.get(var2.fullName());
      if (!this.cppModule && var3 != null && var3 instanceof ModuleEntry) {
         var2 = (ModuleEntry)var3;
         if (var1 == this.topLevelModule) {
            if (!var2.emit()) {
               this.addToContainer(var1, var2);
            } else if (!var1.contained().contains(var2)) {
               this.addToContainer(var1, var2);
            }
         }
      } else {
         this.pigeonhole(var1, var2);
      }

      return var2;
   }

   private EnumEntry newEnumEntry(SymtabEntry var1) {
      EnumEntry var2 = this.stFactory.enumEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.name(this.token.name);
      this.pigeonhole(var1, var2);
      return var2;
   }

   private SequenceEntry newSequenceEntry(SymtabEntry var1) {
      SequenceEntry var2 = this.stFactory.sequenceEntry(var1, (IDLID)repIDStack.peek());
      var2.sourceFile(this.scanner.fileEntry());
      var2.name("");
      this.pigeonhole(var1, var2);
      return var2;
   }

   private void updateSymbolTable(String var1, SymtabEntry var2, boolean var3) {
      String var4 = var1.toLowerCase();
      if (var3 && this.lcSymbolTable.get(var4) != null) {
         ParseException.alreadyDeclared(this.scanner, var1);
      }

      symbolTable.put(var1, var2);
      this.lcSymbolTable.put(var4, var2);
      String var5 = "org/omg/CORBA";
      if (var1.startsWith(var5)) {
         overrideNames.put("CORBA" + var1.substring(var5.length()), var1);
      }

   }

   private void pigeonhole(SymtabEntry var1, SymtabEntry var2) {
      if (var2.name().equals("")) {
         var2.name("uN__" + ++this.sequence);
      }

      String var3 = var2.fullName();
      if (overrideNames.get(var3) == null) {
         this.addToContainer(var1, var2);
         SymtabEntry var4 = (SymtabEntry)symbolTable.get(var3);
         if (var4 == null) {
            this.updateSymbolTable(var3, var2, true);
         } else {
            String var5;
            String var6;
            if (var4 instanceof ForwardEntry && var2 instanceof InterfaceEntry) {
               var5 = ((IDLID)var2.repositoryID()).prefix();
               var6 = ((IDLID)var4.repositoryID()).prefix();
               if (var5.equals(var6)) {
                  this.updateSymbolTable(var3, var2, false);
               } else {
                  ParseException.badRepIDPrefix(this.scanner, var3, var6, var5);
               }
            } else if (!(var2 instanceof ForwardEntry) || !(var4 instanceof InterfaceEntry) && !(var4 instanceof ForwardEntry)) {
               if ((!this.cppModule || !(var2 instanceof ModuleEntry) || !(var4 instanceof ModuleEntry)) && !var3.startsWith("org/omg/CORBA") && !var3.startsWith("CORBA")) {
                  if (this.isForwardable(var4, var2)) {
                     if (var4.isReferencable() && var2.isReferencable()) {
                        ParseException.alreadyDeclared(this.scanner, var3);
                     }

                     if (var2.isReferencable()) {
                        var5 = var4.sourceFile().absFilename();
                        var6 = var2.sourceFile().absFilename();
                        if (!var5.equals(var6)) {
                           ParseException.declNotInSameFile(this.scanner, var3, var5);
                        } else {
                           this.updateSymbolTable(var3, var2, false);

                           List var7;
                           try {
                              var7 = (List)var4.dynamicVariable(ftlKey);
                           } catch (NoSuchFieldException var10) {
                              throw new IllegalStateException();
                           }

                           if (var7 != null) {
                              Iterator var8 = var7.iterator();

                              while(var8.hasNext()) {
                                 SymtabEntry var9 = (SymtabEntry)var8.next();
                                 var9.type(var2);
                              }
                           }
                        }
                     }
                  } else {
                     ParseException.alreadyDeclared(this.scanner, var3);
                  }
               }
            } else if (var4 instanceof ForwardEntry && var2.repositoryID() instanceof IDLID && var4.repositoryID() instanceof IDLID) {
               var5 = ((IDLID)var2.repositoryID()).prefix();
               var6 = ((IDLID)var4.repositoryID()).prefix();
               if (!var5.equals(var6)) {
                  ParseException.badRepIDPrefix(this.scanner, var3, var6, var5);
               }
            }
         }
      }

   }

   private boolean isForwardable(SymtabEntry var1, SymtabEntry var2) {
      return var1 instanceof StructEntry && var2 instanceof StructEntry || var1 instanceof UnionEntry && var2 instanceof UnionEntry;
   }

   private void pigeonholeMethod(InterfaceEntry var1, MethodEntry var2) {
      if (var2.name().equals("")) {
         var2.name("uN__" + ++this.sequence);
      }

      String var3 = var2.fullName();
      if (overrideNames.get(var3) == null) {
         this.addToContainer(var1, var2);
         String var4 = var3.toLowerCase();
         symbolTable.put(var3, var2);
         this.lcSymbolTable.put(var4, var2);
         if (var3.startsWith("org/omg/CORBA")) {
            overrideNames.put("CORBA" + var3.substring(13), var3);
         }
      }

   }

   private void addToContainer(SymtabEntry var1, SymtabEntry var2) {
      if (var1 instanceof ModuleEntry) {
         ((ModuleEntry)var1).addContained(var2);
      } else if (var1 instanceof InterfaceEntry) {
         ((InterfaceEntry)var1).addContained(var2);
      } else if (var1 instanceof StructEntry) {
         ((StructEntry)var1).addContained(var2);
      } else if (var1 instanceof UnionEntry) {
         ((UnionEntry)var1).addContained(var2);
      } else if (var1 instanceof SequenceEntry) {
         ((SequenceEntry)var1).addContained(var2);
      }

   }

   SymtabEntry qualifiedEntry(String var1) {
      SymtabEntry var2 = this.recursiveQualifiedEntry(var1);
      if (var2 == null) {
         ParseException.undeclaredType(this.scanner, var1);
      } else if (var2 instanceof ModuleEntry && !this._isModuleLegalType) {
         ParseException.moduleNotType(this.scanner, var1);
         var2 = null;
      }

      return var2;
   }

   SymtabEntry recursiveQualifiedEntry(String var1) {
      SymtabEntry var2 = null;
      if (var1 != null && !var1.equals("void")) {
         int var3 = var1.lastIndexOf(47);
         if (var3 >= 0) {
            var2 = this.recursiveQualifiedEntry(var1.substring(0, var3));
            if (var2 == null) {
               return null;
            }

            if (var2 instanceof TypedefEntry) {
               var1 = typeOf(var2).fullName() + var1.substring(var3);
            }
         }

         var2 = this.searchOverrideNames(var1);
         if (var2 == null) {
            var2 = (SymtabEntry)symbolTable.get(var1);
         }

         if (var2 == null) {
            var2 = this.searchGlobalInheritanceScope(var1);
         }
      }

      return var2;
   }

   SymtabEntry partlyQualifiedEntry(String var1, SymtabEntry var2) {
      SymtabEntry var3 = null;
      if (var1 != null) {
         int var4 = var1.lastIndexOf(47);
         var3 = this.recursivePQEntry(var1.substring(0, var4), var2);
         if (var3 instanceof TypedefEntry) {
            var1 = typeOf(var3).fullName() + var1.substring(var4);
         }

         if (var2 != null) {
            var3 = this.searchModuleScope(var1.substring(0, var1.lastIndexOf(47)), var2);
         }

         if (var3 == null) {
            var3 = this.qualifiedEntry(var1);
         } else {
            var3 = this.qualifiedEntry(var3.fullName() + var1.substring(var1.lastIndexOf(47)));
         }
      }

      return var3;
   }

   SymtabEntry recursivePQEntry(String var1, SymtabEntry var2) {
      SymtabEntry var3 = null;
      if (var1 != null) {
         int var4 = var1.lastIndexOf(47);
         if (var4 < 0) {
            var3 = this.searchModuleScope(var1, var2);
         } else {
            var3 = this.recursivePQEntry(var1.substring(0, var4), var2);
            if (var3 == null) {
               return null;
            }

            if (var3 instanceof TypedefEntry) {
               var1 = typeOf(var3).fullName() + var1.substring(var4);
            }

            if (var2 != null) {
               var3 = this.searchModuleScope(var1.substring(0, var1.lastIndexOf(47)), var2);
            }

            if (var3 == null) {
               this.recursiveQualifiedEntry(var1);
            } else {
               var3 = this.recursiveQualifiedEntry(var3.fullName() + var1.substring(var1.lastIndexOf(47)));
            }
         }
      }

      return var3;
   }

   SymtabEntry unqualifiedEntry(String var1, SymtabEntry var2) {
      SymtabEntry var3 = this.unqualifiedEntryWMod(var1, var2);
      if (var3 instanceof ModuleEntry && !this._isModuleLegalType) {
         ParseException.moduleNotType(this.scanner, var1);
         var3 = null;
      }

      return var3;
   }

   SymtabEntry unqualifiedEntryWMod(String var1, SymtabEntry var2) {
      SymtabEntry var3 = null;
      if (var1 != null && !var1.equals("void")) {
         var3 = (SymtabEntry)symbolTable.get(var2.fullName() + '/' + var1);
         if (var3 == null) {
            var3 = this.searchLocalInheritanceScope(var1, var2);
         }

         if (var3 == null) {
            var3 = this.searchOverrideNames(var1);
         }

         if (var3 == null && var2 != null) {
            var3 = this.searchModuleScope(var1, var2);
         }

         if (var3 == null) {
            var3 = this.searchParentInheritanceScope(var1, var2);
         }
      }

      if (var3 == null) {
         ParseException.undeclaredType(this.scanner, var1);
      }

      return var3;
   }

   SymtabEntry searchParentInheritanceScope(String var1, SymtabEntry var2) {
      String var3 = var2.fullName();

      while(var2 != null && !var3.equals("") && !(var2 instanceof InterfaceEntry)) {
         int var4 = var3.lastIndexOf(47);
         if (var4 < 0) {
            var3 = "";
         } else {
            var3 = var3.substring(0, var4);
            var2 = (SymtabEntry)symbolTable.get(var3);
         }
      }

      if (var2 != null && var2 instanceof InterfaceEntry) {
         String var6 = var2.fullName() + '/' + var1;
         SymtabEntry var5 = (SymtabEntry)symbolTable.get(var6);
         return var5 != null ? var5 : this.searchLocalInheritanceScope(var1, var2);
      } else {
         return null;
      }
   }

   SymtabEntry searchGlobalInheritanceScope(String var1) {
      int var2 = var1.lastIndexOf(47);
      SymtabEntry var3 = null;
      if (var2 >= 0) {
         String var4 = var1.substring(0, var2);
         var3 = (SymtabEntry)symbolTable.get(var4);
         var3 = var3 instanceof InterfaceEntry ? this.searchLocalInheritanceScope(var1.substring(var2 + 1), var3) : null;
      }

      return var3;
   }

   SymtabEntry searchLocalInheritanceScope(String var1, SymtabEntry var2) {
      return var2 instanceof InterfaceEntry ? this.searchDerivedFrom(var1, (InterfaceEntry)var2) : null;
   }

   SymtabEntry searchOverrideNames(String var1) {
      String var2 = (String)overrideNames.get(var1);
      return var2 != null ? (SymtabEntry)symbolTable.get(var2) : null;
   }

   SymtabEntry searchModuleScope(String var1, SymtabEntry var2) {
      String var3 = var2.fullName();
      String var4 = var3 + '/' + var1;
      SymtabEntry var5 = (SymtabEntry)symbolTable.get(var4);

      while(var5 == null && !var3.equals("")) {
         int var6 = var3.lastIndexOf(47);
         if (var6 < 0) {
            var3 = "";
         } else {
            var3 = var3.substring(0, var6);
            var4 = var3 + '/' + var1;
            var5 = (SymtabEntry)symbolTable.get(var4);
         }
      }

      return var5 == null ? (SymtabEntry)symbolTable.get(var1) : var5;
   }

   SymtabEntry searchDerivedFrom(String var1, InterfaceEntry var2) {
      Enumeration var3 = var2.derivedFrom().elements();

      while(var3.hasMoreElements()) {
         SymtabEntry var4 = (SymtabEntry)var3.nextElement();
         if (var4 instanceof InterfaceEntry) {
            InterfaceEntry var5 = (InterfaceEntry)var4;
            String var6 = var5.fullName() + '/' + var1;
            SymtabEntry var7 = (SymtabEntry)symbolTable.get(var6);
            if (var7 != null) {
               return var7;
            }

            var7 = this.searchDerivedFrom(var1, var5);
            if (var7 != null) {
               return var7;
            }
         }
      }

      return null;
   }

   String entryName(SymtabEntry var1) {
      if (var1 instanceof AttributeEntry) {
         return "attribute";
      } else if (var1 instanceof ConstEntry) {
         return "constant";
      } else if (var1 instanceof EnumEntry) {
         return "enumeration";
      } else if (var1 instanceof ExceptionEntry) {
         return "exception";
      } else if (var1 instanceof ValueBoxEntry) {
         return "value box";
      } else if (!(var1 instanceof ForwardValueEntry) && !(var1 instanceof ValueEntry)) {
         if (!(var1 instanceof ForwardEntry) && !(var1 instanceof InterfaceEntry)) {
            if (var1 instanceof MethodEntry) {
               return "method";
            } else if (var1 instanceof ModuleEntry) {
               return "module";
            } else if (var1 instanceof ParameterEntry) {
               return "parameter";
            } else if (var1 instanceof PrimitiveEntry) {
               return "primitive";
            } else if (var1 instanceof SequenceEntry) {
               return "sequence";
            } else if (var1 instanceof StringEntry) {
               return "string";
            } else if (var1 instanceof StructEntry) {
               return "struct";
            } else if (var1 instanceof TypedefEntry) {
               return "typedef";
            } else {
               return var1 instanceof UnionEntry ? "union" : "void";
            }
         } else {
            return "interface";
         }
      } else {
         return "value";
      }
   }

   private boolean isInterface(SymtabEntry var1) {
      return var1 instanceof InterfaceEntry || var1 instanceof ForwardEntry && !(var1 instanceof ForwardValueEntry);
   }

   private boolean isValue(SymtabEntry var1) {
      return var1 instanceof ValueEntry;
   }

   private boolean isInterfaceOnly(SymtabEntry var1) {
      return var1 instanceof InterfaceEntry;
   }

   private boolean isForward(SymtabEntry var1) {
      return var1 instanceof ForwardEntry;
   }

   private boolean isntInStringList(Vector var1, String var2) {
      boolean var3 = true;
      Enumeration var4 = var1.elements();

      while(var4.hasMoreElements()) {
         if (var2.equals((String)var4.nextElement())) {
            ParseException.alreadyDeclared(this.scanner, var2);
            var3 = false;
            break;
         }
      }

      return var3;
   }

   private boolean isntInList(Vector var1, String var2) {
      boolean var3 = true;
      Enumeration var4 = var1.elements();

      while(var4.hasMoreElements()) {
         if (var2.equals(((SymtabEntry)var4.nextElement()).name())) {
            ParseException.alreadyDeclared(this.scanner, var2);
            var3 = false;
            break;
         }
      }

      return var3;
   }

   private boolean isntInList(Vector var1, SymtabEntry var2) {
      boolean var3 = true;
      Enumeration var4 = var1.elements();

      while(var4.hasMoreElements()) {
         SymtabEntry var5 = (SymtabEntry)var4.nextElement();
         if (var2 == var5) {
            ParseException.alreadyDeclared(this.scanner, var2.fullName());
            var3 = false;
            break;
         }
      }

      return var3;
   }

   public static SymtabEntry typeOf(SymtabEntry var0) {
      while(var0 instanceof TypedefEntry) {
         var0 = var0.type();
      }

      return var0;
   }

   void forwardEntryCheck() {
      Enumeration var1 = symbolTable.elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (var2 instanceof ForwardEntry) {
            ParseException.forwardEntry(this.scanner, var2.fullName());
         }
      }

   }

   private void skipToSemicolon() throws IOException {
      while(!this.token.equals(999) && !this.token.equals(100)) {
         if (this.token.equals(101)) {
            this.skipToRightBrace();
         }

         try {
            this.match(this.token.type);
         } catch (ParseException var2) {
         }
      }

      if (this.token.equals(999)) {
         throw new EOFException();
      } else {
         try {
            this.match(100);
         } catch (Exception var3) {
         }

      }
   }

   private void skipToRightBrace() throws IOException {
      boolean var1 = true;

      while(!this.token.equals(999) && !this.token.equals(102)) {
         if (var1) {
            var1 = false;
         } else if (this.token.equals(101)) {
            this.skipToRightBrace();
         }

         try {
            this.match(this.token.type);
         } catch (ParseException var3) {
         }
      }

      if (this.token.equals(999)) {
         throw new EOFException();
      }
   }

   public static void enteringInclude() {
      repIDStack.push(new IDLID());
   }

   public static void exitingInclude() {
      repIDStack.pop();
   }
}
