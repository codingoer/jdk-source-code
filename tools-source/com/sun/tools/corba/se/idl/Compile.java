package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.DefaultExprFactory;
import com.sun.tools.corba.se.idl.constExpr.ExprFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Compile {
   public Arguments arguments = null;
   protected Hashtable overrideNames = new Hashtable();
   protected Hashtable symbolTable = new Hashtable();
   protected Vector includes = new Vector();
   protected Vector includeEntries = new Vector();
   static Noop noop = new Noop();
   private GenFactory genFactory = null;
   private SymtabFactory symtabFactory = null;
   private ExprFactory exprFactory = null;
   private Parser parser = null;
   Preprocessor preprocessor = new Preprocessor();
   private NoPragma noPragma = new NoPragma();
   private Enumeration emitList = null;
   private String[] keywords = null;

   public Compile() {
      this.noPragma.init(this.preprocessor);
      this.preprocessor.registerPragma(this.noPragma);
      ParseException.detected = false;
      SymtabEntry.includeStack = new Stack();
      SymtabEntry.setEmit = true;
      Parser.repIDStack = new Stack();
   }

   public static void main(String[] var0) {
      (new Compile()).start(var0);
   }

   protected Factories factories() {
      return new Factories();
   }

   protected void registerPragma(PragmaHandler var1) {
      var1.init(this.preprocessor);
      this.preprocessor.registerPragma(var1);
   }

   protected void init(String[] var1) throws InvalidArgument {
      this.initFactories();
      this.arguments.parseArgs(var1);
      this.initGenerators();
      this.parser = new Parser(this.preprocessor, this.arguments, this.overrideNames, this.symbolTable, this.symtabFactory, this.exprFactory, this.keywords);
      this.preprocessor.init(this.parser);
      this.parser.includes = this.includes;
      this.parser.includeEntries = this.includeEntries;
   }

   protected Enumeration parse() throws IOException {
      if (this.arguments.verbose) {
         System.out.println(Util.getMessage("Compile.parsing", this.arguments.file));
      }

      this.parser.parse(this.arguments.file);
      if (!ParseException.detected) {
         this.parser.forwardEntryCheck();
      }

      if (this.arguments.verbose) {
         System.out.println(Util.getMessage("Compile.parseDone", this.arguments.file));
      }

      if (ParseException.detected) {
         this.symbolTable = null;
         this.emitList = null;
      } else {
         Parser var10001 = this.parser;
         this.symbolTable = Parser.symbolTable;
         this.emitList = this.parser.emitList.elements();
      }

      return this.emitList;
   }

   protected void generate() throws IOException {
      if (ParseException.detected) {
         this.emitList = null;
      } else {
         this.emitList = this.parser.emitList.elements();
      }

      if (this.emitList != null) {
         if (this.arguments.verbose) {
            System.out.println();
         }

         while(this.emitList.hasMoreElements()) {
            SymtabEntry var1 = (SymtabEntry)this.emitList.nextElement();
            if (this.arguments.verbose && !(var1.generator() instanceof Noop)) {
               if (var1.module().equals("")) {
                  System.out.println(Util.getMessage("Compile.generating", var1.name()));
               } else {
                  System.out.println(Util.getMessage("Compile.generating", var1.module() + '/' + var1.name()));
               }
            }

            var1.generate(this.symbolTable, (PrintWriter)null);
            if (this.arguments.verbose && !(var1.generator() instanceof Noop)) {
               if (var1.module().equals("")) {
                  System.out.println(Util.getMessage("Compile.genDone", var1.name()));
               } else {
                  System.out.println(Util.getMessage("Compile.genDone", var1.module() + '/' + var1.name()));
               }
            }
         }
      }

   }

   public void start(String[] var1) {
      try {
         this.init(var1);
         if (this.arguments.versionRequest) {
            this.displayVersion();
         } else {
            this.parse();
            this.generate();
         }
      } catch (InvalidArgument var3) {
         System.err.println(var3);
      } catch (IOException var4) {
         System.err.println(var4);
      }

   }

   private void initFactories() {
      Factories var1 = this.factories();
      if (var1 == null) {
         var1 = new Factories();
      }

      Arguments var2 = var1.arguments();
      if (var2 == null) {
         this.arguments = new Arguments();
      } else {
         this.arguments = var2;
      }

      SymtabFactory var3 = var1.symtabFactory();
      if (var3 == null) {
         this.symtabFactory = new DefaultSymtabFactory();
      } else {
         this.symtabFactory = var3;
      }

      ExprFactory var4 = var1.exprFactory();
      if (var4 == null) {
         this.exprFactory = new DefaultExprFactory();
      } else {
         this.exprFactory = var4;
      }

      GenFactory var5 = var1.genFactory();
      if (var5 == null) {
         this.genFactory = noop;
      } else {
         this.genFactory = var5;
      }

      this.keywords = var1.languageKeywords();
      if (this.keywords == null) {
         this.keywords = new String[0];
      }

   }

   private void initGenerators() {
      AttributeGen var1 = this.genFactory.createAttributeGen();
      AttributeEntry.attributeGen = (AttributeGen)(var1 == null ? noop : var1);
      ConstGen var2 = this.genFactory.createConstGen();
      ConstEntry.constGen = (ConstGen)(var2 == null ? noop : var2);
      EnumGen var3 = this.genFactory.createEnumGen();
      EnumEntry.enumGen = (EnumGen)(var3 == null ? noop : var3);
      ExceptionGen var4 = this.genFactory.createExceptionGen();
      ExceptionEntry.exceptionGen = (ExceptionGen)(var4 == null ? noop : var4);
      ForwardGen var5 = this.genFactory.createForwardGen();
      ForwardEntry.forwardGen = (ForwardGen)(var5 == null ? noop : var5);
      ForwardValueGen var6 = this.genFactory.createForwardValueGen();
      ForwardValueEntry.forwardValueGen = (ForwardValueGen)(var6 == null ? noop : var6);
      IncludeGen var7 = this.genFactory.createIncludeGen();
      IncludeEntry.includeGen = (IncludeGen)(var7 == null ? noop : var7);
      InterfaceGen var8 = this.genFactory.createInterfaceGen();
      InterfaceEntry.interfaceGen = (InterfaceGen)(var8 == null ? noop : var8);
      ValueGen var9 = this.genFactory.createValueGen();
      ValueEntry.valueGen = (ValueGen)(var9 == null ? noop : var9);
      ValueBoxGen var10 = this.genFactory.createValueBoxGen();
      ValueBoxEntry.valueBoxGen = (ValueBoxGen)(var10 == null ? noop : var10);
      MethodGen var11 = this.genFactory.createMethodGen();
      MethodEntry.methodGen = (MethodGen)(var11 == null ? noop : var11);
      ModuleGen var12 = this.genFactory.createModuleGen();
      ModuleEntry.moduleGen = (ModuleGen)(var12 == null ? noop : var12);
      NativeGen var13 = this.genFactory.createNativeGen();
      NativeEntry.nativeGen = (NativeGen)(var13 == null ? noop : var13);
      ParameterGen var14 = this.genFactory.createParameterGen();
      ParameterEntry.parameterGen = (ParameterGen)(var14 == null ? noop : var14);
      PragmaGen var15 = this.genFactory.createPragmaGen();
      PragmaEntry.pragmaGen = (PragmaGen)(var15 == null ? noop : var15);
      PrimitiveGen var16 = this.genFactory.createPrimitiveGen();
      PrimitiveEntry.primitiveGen = (PrimitiveGen)(var16 == null ? noop : var16);
      SequenceGen var17 = this.genFactory.createSequenceGen();
      SequenceEntry.sequenceGen = (SequenceGen)(var17 == null ? noop : var17);
      StringGen var18 = this.genFactory.createStringGen();
      StringEntry.stringGen = (StringGen)(var18 == null ? noop : var18);
      StructGen var19 = this.genFactory.createStructGen();
      StructEntry.structGen = (StructGen)(var19 == null ? noop : var19);
      TypedefGen var20 = this.genFactory.createTypedefGen();
      TypedefEntry.typedefGen = (TypedefGen)(var20 == null ? noop : var20);
      UnionGen var21 = this.genFactory.createUnionGen();
      UnionEntry.unionGen = (UnionGen)(var21 == null ? noop : var21);
   }

   protected void displayVersion() {
      String var1 = Util.getMessage("Version.product", Util.getMessage("Version.number"));
      System.out.println(var1);
   }
}
