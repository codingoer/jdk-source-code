package com.sun.tools.javac.comp;

import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.Pool;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.lang.model.type.TypeKind;

public class LambdaToMethod extends TreeTranslator {
   private Attr attr;
   private JCDiagnostic.Factory diags;
   private Log log;
   private Lower lower;
   private Names names;
   private Symtab syms;
   private Resolve rs;
   private TreeMaker make;
   private Types types;
   private TransTypes transTypes;
   private Env attrEnv;
   private LambdaAnalyzerPreprocessor analyzer;
   private Map contextMap;
   private LambdaAnalyzerPreprocessor.TranslationContext context;
   private KlassInfo kInfo;
   private boolean dumpLambdaToMethodStats;
   private final boolean forceSerializable;
   public static final int FLAG_SERIALIZABLE = 1;
   public static final int FLAG_MARKERS = 2;
   public static final int FLAG_BRIDGES = 4;
   protected static final Context.Key unlambdaKey = new Context.Key();

   public static LambdaToMethod instance(Context var0) {
      LambdaToMethod var1 = (LambdaToMethod)var0.get(unlambdaKey);
      if (var1 == null) {
         var1 = new LambdaToMethod(var0);
      }

      return var1;
   }

   private LambdaToMethod(Context var1) {
      var1.put((Context.Key)unlambdaKey, (Object)this);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.log = Log.instance(var1);
      this.lower = Lower.instance(var1);
      this.names = Names.instance(var1);
      this.syms = Symtab.instance(var1);
      this.rs = Resolve.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.types = Types.instance(var1);
      this.transTypes = TransTypes.instance(var1);
      this.analyzer = new LambdaAnalyzerPreprocessor();
      Options var2 = Options.instance(var1);
      this.dumpLambdaToMethodStats = var2.isSet("dumpLambdaToMethodStats");
      this.attr = Attr.instance(var1);
      this.forceSerializable = var2.isSet("forceSerializable");
   }

   public JCTree translate(JCTree var1) {
      LambdaAnalyzerPreprocessor.TranslationContext var2 = (LambdaAnalyzerPreprocessor.TranslationContext)this.contextMap.get(var1);
      return this.translate(var1, var2 != null ? var2 : this.context);
   }

   JCTree translate(JCTree var1, LambdaAnalyzerPreprocessor.TranslationContext var2) {
      LambdaAnalyzerPreprocessor.TranslationContext var3 = this.context;

      JCTree var4;
      try {
         this.context = var2;
         var4 = super.translate(var1);
      } finally {
         this.context = var3;
      }

      return var4;
   }

   List translate(List var1, LambdaAnalyzerPreprocessor.TranslationContext var2) {
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         JCTree var5 = (JCTree)var4.next();
         var3.append(this.translate(var5, var2));
      }

      return var3.toList();
   }

   public JCTree translateTopLevelClass(Env var1, JCTree var2, TreeMaker var3) {
      this.make = var3;
      this.attrEnv = var1;
      this.context = null;
      this.contextMap = new HashMap();
      return this.translate(var2);
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      if (var1.sym.owner.kind == 1) {
         var1 = this.analyzer.analyzeAndPreprocessClass(var1);
      }

      KlassInfo var2 = this.kInfo;

      try {
         this.kInfo = new KlassInfo(var1);
         super.visitClassDef(var1);
         if (!this.kInfo.deserializeCases.isEmpty()) {
            int var3 = this.make.pos;

            try {
               this.make.at(var1);
               this.kInfo.addMethod(this.makeDeserializeMethod(var1.sym));
            } finally {
               this.make.at(var3);
            }
         }

         List var13 = this.kInfo.appendedMethodList.toList();
         var1.defs = var1.defs.appendList(var13);
         Iterator var4 = var13.iterator();

         while(var4.hasNext()) {
            JCTree var5 = (JCTree)var4.next();
            var1.sym.members().enter(((JCTree.JCMethodDecl)var5).sym);
         }

         this.result = var1;
      } finally {
         this.kInfo = var2;
      }
   }

   public void visitLambda(JCTree.JCLambda var1) {
      LambdaAnalyzerPreprocessor.LambdaTranslationContext var2 = (LambdaAnalyzerPreprocessor.LambdaTranslationContext)this.context;
      Symbol.MethodSymbol var3 = var2.translatedSym;
      Type.MethodType var4 = (Type.MethodType)var3.type;
      Symbol var5 = var2.owner;
      ListBuffer var6 = new ListBuffer();
      ListBuffer var7 = new ListBuffer();
      Iterator var8 = var5.getRawTypeAttributes().iterator();

      while(var8.hasNext()) {
         Attribute.TypeCompound var9 = (Attribute.TypeCompound)var8.next();
         if (var9.position.onLambda == var1) {
            var7.append(var9);
         } else {
            var6.append(var9);
         }
      }

      if (var7.nonEmpty()) {
         var5.setTypeAttributes(var6.toList());
         var3.setTypeAttributes(var7.toList());
      }

      JCTree.JCMethodDecl var10 = this.make.MethodDef(this.make.Modifiers(var3.flags_field), var3.name, this.make.QualIdent(var4.getReturnType().tsym), List.nil(), var2.syntheticParams, var4.getThrownTypes() == null ? List.nil() : this.make.Types(var4.getThrownTypes()), (JCTree.JCBlock)null, (JCTree.JCExpression)null);
      var10.sym = var3;
      var10.type = var4;
      var10.body = (JCTree.JCBlock)this.translate(this.makeLambdaBody(var1, var10));
      this.kInfo.addMethod(var10);
      var6 = new ListBuffer();
      if (var2.methodReferenceReceiver != null) {
         var6.append(var2.methodReferenceReceiver);
      } else if (!var3.isStatic()) {
         var6.append(this.makeThis(var3.owner.enclClass().asType(), var2.owner.enclClass()));
      }

      Iterator var11 = var2.getSymbolMap(LambdaToMethod.LambdaSymbolKind.CAPTURED_VAR).keySet().iterator();

      Symbol var13;
      JCTree.JCExpression var15;
      while(var11.hasNext()) {
         var13 = (Symbol)var11.next();
         if (var13 != var2.self) {
            var15 = this.make.Ident(var13).setType(var13.type);
            var6.append((JCTree.JCExpression)var15);
         }
      }

      var11 = var2.getSymbolMap(LambdaToMethod.LambdaSymbolKind.CAPTURED_OUTER_THIS).keySet().iterator();

      while(var11.hasNext()) {
         var13 = (Symbol)var11.next();
         var15 = this.make.QualThis(var13.type);
         var6.append((JCTree.JCExpression)var15);
      }

      List var12 = this.translate(var6.toList(), var2.prev);
      int var14 = this.referenceKind(var3);
      this.result = this.makeMetafactoryIndyCall(this.context, var14, var3, var12);
   }

   private JCTree.JCIdent makeThis(Type var1, Symbol var2) {
      Symbol.VarSymbol var3 = new Symbol.VarSymbol(8589938704L, this.names._this, var1, var2);
      return this.make.Ident((Symbol)var3);
   }

   public void visitReference(JCTree.JCMemberReference var1) {
      LambdaAnalyzerPreprocessor.ReferenceTranslationContext var2 = (LambdaAnalyzerPreprocessor.ReferenceTranslationContext)this.context;
      Symbol var3 = var2.isSignaturePolymorphic() ? var2.sigPolySym : var1.sym;
      Object var4;
      switch (var1.kind) {
         case IMPLICIT_INNER:
         case SUPER:
            var4 = this.makeThis(var2.owner.enclClass().asType(), var2.owner.enclClass());
            break;
         case BOUND:
            JCTree.JCExpression var6 = var1.getQualifierExpression();
            var4 = this.attr.makeNullCheck(var6);
            break;
         case UNBOUND:
         case STATIC:
         case TOPLEVEL:
         case ARRAY_CTOR:
            var4 = null;
            break;
         default:
            throw new InternalError("Should not have an invalid kind");
      }

      List var5 = var4 == null ? List.nil() : this.translate(List.of(var4), var2.prev);
      this.result = this.makeMetafactoryIndyCall(var2, var2.referenceKind(), var3, var5);
   }

   public void visitIdent(JCTree.JCIdent var1) {
      if (this.context != null && this.analyzer.lambdaIdentSymbolFilter(var1.sym)) {
         int var2 = this.make.pos;

         try {
            this.make.at(var1);
            LambdaAnalyzerPreprocessor.LambdaTranslationContext var3 = (LambdaAnalyzerPreprocessor.LambdaTranslationContext)this.context;
            JCTree var4 = var3.translate(var1);
            if (var4 != null) {
               this.result = var4;
            } else {
               super.visitIdent(var1);
            }
         } finally {
            this.make.at(var2);
         }
      } else {
         super.visitIdent(var1);
      }

   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      if (this.context != null && this.analyzer.lambdaFieldAccessFilter(var1)) {
         int var2 = this.make.pos;

         try {
            this.make.at(var1);
            LambdaAnalyzerPreprocessor.LambdaTranslationContext var3 = (LambdaAnalyzerPreprocessor.LambdaTranslationContext)this.context;
            JCTree var4 = var3.translate(var1);
            if (var4 != null) {
               this.result = var4;
            } else {
               super.visitSelect(var1);
            }
         } finally {
            this.make.at(var2);
         }
      } else {
         super.visitSelect(var1);
      }

   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      LambdaAnalyzerPreprocessor.LambdaTranslationContext var2 = (LambdaAnalyzerPreprocessor.LambdaTranslationContext)this.context;
      if (this.context != null && var2.getSymbolMap(LambdaToMethod.LambdaSymbolKind.LOCAL_VAR).containsKey(var1.sym)) {
         var1.init = (JCTree.JCExpression)this.translate(var1.init);
         var1.sym = (Symbol.VarSymbol)var2.getSymbolMap(LambdaToMethod.LambdaSymbolKind.LOCAL_VAR).get(var1.sym);
         this.result = var1;
      } else if (this.context != null && var2.getSymbolMap(LambdaToMethod.LambdaSymbolKind.TYPE_VAR).containsKey(var1.sym)) {
         JCTree.JCExpression var3 = (JCTree.JCExpression)this.translate(var1.init);
         Symbol.VarSymbol var4 = (Symbol.VarSymbol)var2.getSymbolMap(LambdaToMethod.LambdaSymbolKind.TYPE_VAR).get(var1.sym);
         int var5 = this.make.pos;

         try {
            this.result = this.make.at(var1).VarDef(var4, var3);
         } finally {
            this.make.at(var5);
         }

         Scope var6 = var1.sym.owner.members();
         if (var6 != null) {
            var6.remove(var1.sym);
            var6.enter(var4);
         }
      } else {
         super.visitVarDef(var1);
      }

   }

   private JCTree.JCBlock makeLambdaBody(JCTree.JCLambda var1, JCTree.JCMethodDecl var2) {
      return var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION ? this.makeLambdaExpressionBody((JCTree.JCExpression)var1.body, var2) : this.makeLambdaStatementBody((JCTree.JCBlock)var1.body, var2, var1.canCompleteNormally);
   }

   private JCTree.JCBlock makeLambdaExpressionBody(JCTree.JCExpression var1, JCTree.JCMethodDecl var2) {
      Type var3 = var2.type.getReturnType();
      boolean var4 = var1.type.hasTag(TypeTag.VOID);
      boolean var5 = var3.hasTag(TypeTag.VOID);
      boolean var6 = this.types.isSameType(var3, this.types.boxedClass(this.syms.voidType).type);
      int var7 = this.make.pos;

      JCTree.JCBlock var9;
      try {
         if (var5) {
            JCTree.JCExpressionStatement var14 = this.make.at(var1).Exec(var1);
            var9 = this.make.Block(0L, List.of(var14));
            return var9;
         }

         if (var4 && var6) {
            ListBuffer var13 = new ListBuffer();
            var13.append(this.make.at(var1).Exec(var1));
            var13.append(this.make.Return(this.make.Literal(TypeTag.BOT, (Object)null).setType(this.syms.botType)));
            var9 = this.make.Block(0L, var13.toList());
            return var9;
         }

         JCTree.JCExpression var8 = this.transTypes.coerce(this.attrEnv, var1, var3);
         var9 = this.make.at(var8).Block(0L, List.of(this.make.Return(var8)));
      } finally {
         this.make.at(var7);
      }

      return var9;
   }

   private JCTree.JCBlock makeLambdaStatementBody(JCTree.JCBlock var1, final JCTree.JCMethodDecl var2, boolean var3) {
      final Type var4 = var2.type.getReturnType();
      final boolean var5 = var4.hasTag(TypeTag.VOID);
      boolean var6 = this.types.isSameType(var4, this.types.boxedClass(this.syms.voidType).type);

      class LambdaBodyTranslator extends TreeTranslator {
         public void visitClassDef(JCTree.JCClassDecl var1) {
            this.result = var1;
         }

         public void visitLambda(JCTree.JCLambda var1) {
            this.result = var1;
         }

         public void visitReturn(JCTree.JCReturn var1) {
            boolean var2x = var1.expr == null;
            if (var5 && !var2x) {
               Symbol.VarSymbol var3 = LambdaToMethod.this.makeSyntheticVar(0L, (Name)LambdaToMethod.this.names.fromString("$loc"), var1.expr.type, var2.sym);
               JCTree.JCVariableDecl var4x = LambdaToMethod.this.make.VarDef(var3, var1.expr);
               this.result = LambdaToMethod.this.make.Block(0L, List.of(var4x, LambdaToMethod.this.make.Return((JCTree.JCExpression)null)));
            } else if (var5 && var2x) {
               this.result = var1;
            } else {
               var1.expr = LambdaToMethod.this.transTypes.coerce(LambdaToMethod.this.attrEnv, var1.expr, var4);
               this.result = var1;
            }

         }
      }

      JCTree.JCBlock var7 = (JCTree.JCBlock)(new LambdaBodyTranslator()).translate(var1);
      if (var3 && var6) {
         var7.stats = var7.stats.append(this.make.Return(this.make.Literal(TypeTag.BOT, (Object)null).setType(this.syms.botType)));
      }

      return var7;
   }

   private JCTree.JCMethodDecl makeDeserializeMethod(Symbol var1) {
      ListBuffer var2 = new ListBuffer();
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = this.kInfo.deserializeCases.entrySet().iterator();

      JCTree.JCBreak var6;
      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         var6 = this.make.Break((Name)null);
         var3.add(var6);
         List var7 = ((ListBuffer)var5.getValue()).append(var6).toList();
         var2.add(this.make.Case(this.make.Literal(var5.getKey()), var7));
      }

      JCTree.JCSwitch var8 = this.make.Switch(this.deserGetter("getImplMethodName", this.syms.stringType), var2.toList());

      for(Iterator var9 = var3.iterator(); var9.hasNext(); var6.target = var8) {
         var6 = (JCTree.JCBreak)var9.next();
      }

      JCTree.JCBlock var10 = this.make.Block(0L, List.of(var8, this.make.Throw(this.makeNewClass(this.syms.illegalArgumentExceptionType, List.of(this.make.Literal("Invalid lambda deserialization"))))));
      JCTree.JCMethodDecl var11 = this.make.MethodDef(this.make.Modifiers(this.kInfo.deserMethodSym.flags()), this.names.deserializeLambda, this.make.QualIdent(this.kInfo.deserMethodSym.getReturnType().tsym), List.nil(), List.of(this.make.VarDef(this.kInfo.deserParamSym, (JCTree.JCExpression)null)), List.nil(), var10, (JCTree.JCExpression)null);
      var11.sym = this.kInfo.deserMethodSym;
      var11.type = this.kInfo.deserMethodSym.type;
      return var11;
   }

   JCTree.JCNewClass makeNewClass(Type var1, List var2, Symbol var3) {
      JCTree.JCNewClass var4 = this.make.NewClass((JCTree.JCExpression)null, (List)null, this.make.QualIdent(var1.tsym), var2, (JCTree.JCClassDecl)null);
      var4.constructor = var3;
      var4.type = var1;
      return var4;
   }

   JCTree.JCNewClass makeNewClass(Type var1, List var2) {
      return this.makeNewClass(var1, var2, this.rs.resolveConstructor((JCDiagnostic.DiagnosticPosition)null, this.attrEnv, var1, TreeInfo.types(var2), List.nil()));
   }

   private void addDeserializationCase(int var1, Symbol var2, Type var3, Symbol.MethodSymbol var4, JCDiagnostic.DiagnosticPosition var5, List var6, Type.MethodType var7) {
      String var8 = this.classSig(var3);
      String var9 = var4.getSimpleName().toString();
      String var10 = this.typeSig(this.types.erasure(var4.type));
      String var11 = this.classSig(this.types.erasure(var2.owner.type));
      String var12 = var2.getQualifiedName().toString();
      String var13 = this.typeSig(this.types.erasure(var2.type));
      JCTree.JCExpression var14 = this.eqTest(this.syms.intType, this.deserGetter("getImplMethodKind", this.syms.intType), this.make.Literal(var1));
      ListBuffer var15 = new ListBuffer();
      int var16 = 0;

      for(Iterator var17 = var7.getParameterTypes().iterator(); var17.hasNext(); ++var16) {
         Type var18 = (Type)var17.next();
         List var19 = (new ListBuffer()).append(this.make.Literal(var16)).toList();
         List var20 = (new ListBuffer()).append(this.syms.intType).toList();
         var15.add(this.make.TypeCast(this.types.erasure(var18), this.deserGetter("getCapturedArg", this.syms.objectType, var20, var19)));
      }

      JCTree.JCIf var21 = this.make.If(this.deserTest(this.deserTest(this.deserTest(this.deserTest(this.deserTest(var14, "getFunctionalInterfaceClass", var8), "getFunctionalInterfaceMethodName", var9), "getFunctionalInterfaceMethodSignature", var10), "getImplClass", var11), "getImplMethodSignature", var13), this.make.Return(this.makeIndyCall(var5, this.syms.lambdaMetafactory, this.names.altMetafactory, var6, var7, var15.toList(), var4.name)), (JCTree.JCStatement)null);
      ListBuffer var22 = (ListBuffer)this.kInfo.deserializeCases.get(var12);
      if (var22 == null) {
         var22 = new ListBuffer();
         this.kInfo.deserializeCases.put(var12, var22);
      }

      var22.append(var21);
   }

   private JCTree.JCExpression eqTest(Type var1, JCTree.JCExpression var2, JCTree.JCExpression var3) {
      JCTree.JCBinary var4 = this.make.Binary(JCTree.Tag.EQ, var2, var3);
      var4.operator = this.rs.resolveBinaryOperator((JCDiagnostic.DiagnosticPosition)null, JCTree.Tag.EQ, this.attrEnv, var1, var1);
      var4.setType(this.syms.booleanType);
      return var4;
   }

   private JCTree.JCExpression deserTest(JCTree.JCExpression var1, String var2, String var3) {
      Type.MethodType var4 = new Type.MethodType(List.of(this.syms.objectType), this.syms.booleanType, List.nil(), this.syms.methodClass);
      Symbol var5 = this.rs.resolveQualifiedMethod((JCDiagnostic.DiagnosticPosition)null, this.attrEnv, this.syms.objectType, this.names.equals, List.of(this.syms.objectType), List.nil());
      JCTree.JCMethodInvocation var6 = this.make.Apply(List.nil(), this.make.Select(this.deserGetter(var2, this.syms.stringType), var5).setType(var4), List.of(this.make.Literal(var3)));
      var6.setType(this.syms.booleanType);
      JCTree.JCBinary var7 = this.make.Binary(JCTree.Tag.AND, var1, var6);
      var7.operator = this.rs.resolveBinaryOperator((JCDiagnostic.DiagnosticPosition)null, JCTree.Tag.AND, this.attrEnv, this.syms.booleanType, this.syms.booleanType);
      var7.setType(this.syms.booleanType);
      return var7;
   }

   private JCTree.JCExpression deserGetter(String var1, Type var2) {
      return this.deserGetter(var1, var2, List.nil(), List.nil());
   }

   private JCTree.JCExpression deserGetter(String var1, Type var2, List var3, List var4) {
      Type.MethodType var5 = new Type.MethodType(var3, var2, List.nil(), this.syms.methodClass);
      Symbol var6 = this.rs.resolveQualifiedMethod((JCDiagnostic.DiagnosticPosition)null, this.attrEnv, this.syms.serializedLambdaType, this.names.fromString(var1), var3, List.nil());
      return this.make.Apply(List.nil(), this.make.Select(this.make.Ident((Symbol)this.kInfo.deserParamSym).setType(this.syms.serializedLambdaType), var6).setType(var5), var4).setType(var2);
   }

   private Symbol.MethodSymbol makePrivateSyntheticMethod(long var1, Name var3, Type var4, Symbol var5) {
      return new Symbol.MethodSymbol(var1 | 4096L | 2L, var3, var4, var5);
   }

   private Symbol.VarSymbol makeSyntheticVar(long var1, String var3, Type var4, Symbol var5) {
      return this.makeSyntheticVar(var1, this.names.fromString(var3), var4, var5);
   }

   private Symbol.VarSymbol makeSyntheticVar(long var1, Name var3, Type var4, Symbol var5) {
      return new Symbol.VarSymbol(var1 | 4096L, var3, var4, var5);
   }

   private void setVarargsIfNeeded(JCTree var1, Type var2) {
      if (var2 != null) {
         switch (var1.getTag()) {
            case APPLY:
               ((JCTree.JCMethodInvocation)var1).varargsElement = var2;
               break;
            case NEWCLASS:
               ((JCTree.JCNewClass)var1).varargsElement = var2;
               break;
            default:
               throw new AssertionError();
         }
      }

   }

   private List convertArgs(Symbol var1, List var2, Type var3) {
      Assert.check(var1.kind == 16);
      List var4 = this.types.erasure(var1.type).getParameterTypes();
      if (var3 != null) {
         Assert.check((var1.flags() & 17179869184L) != 0L);
      }

      return this.transTypes.translateArgs(var2, var4, var3, this.attrEnv);
   }

   private Type.MethodType typeToMethodType(Type var1) {
      Type var2 = this.types.erasure(var1);
      return new Type.MethodType(var2.getParameterTypes(), var2.getReturnType(), var2.getThrownTypes(), this.syms.methodClass);
   }

   private JCTree.JCExpression makeMetafactoryIndyCall(LambdaAnalyzerPreprocessor.TranslationContext var1, int var2, Symbol var3, List var4) {
      JCTree.JCFunctionalExpression var5 = var1.tree;
      Symbol.MethodSymbol var6 = (Symbol.MethodSymbol)this.types.findDescriptorSymbol(var5.type.tsym);
      List var7 = List.of(this.typeToMethodType(var6.type), new Pool.MethodHandle(var2, var3, this.types), this.typeToMethodType(var5.getDescriptorType(this.types)));
      ListBuffer var8 = new ListBuffer();
      Iterator var9 = var4.iterator();

      while(var9.hasNext()) {
         JCTree.JCExpression var10 = (JCTree.JCExpression)var9.next();
         var8.append(var10.type);
      }

      Type.MethodType var21 = new Type.MethodType(var8.toList(), var5.type, List.nil(), this.syms.methodClass);
      Name var22 = var1.needsAltMetafactory() ? this.names.altMetafactory : this.names.metafactory;
      if (var1.needsAltMetafactory()) {
         ListBuffer var11 = new ListBuffer();
         Iterator var12 = var5.targets.tail.iterator();

         while(var12.hasNext()) {
            Type var13 = (Type)var12.next();
            if (var13.tsym != this.syms.serializableType.tsym) {
               var11.append(var13.tsym);
            }
         }

         int var23 = var1.isSerializable() ? 1 : 0;
         boolean var24 = var11.nonEmpty();
         boolean var14 = var1.bridges.nonEmpty();
         if (var24) {
            var23 |= 2;
         }

         if (var14) {
            var23 |= 4;
         }

         var7 = var7.append(var23);
         if (var24) {
            var7 = var7.append(var11.length());
            var7 = var7.appendList(var11.toList());
         }

         if (var14) {
            var7 = var7.append(var1.bridges.length() - 1);
            Iterator var15 = var1.bridges.iterator();

            while(var15.hasNext()) {
               Symbol var16 = (Symbol)var15.next();
               Type var17 = var16.erasure(this.types);
               if (!this.types.isSameType(var17, var6.erasure(this.types))) {
                  var7 = var7.append(var16.erasure(this.types));
               }
            }
         }

         if (var1.isSerializable()) {
            int var25 = this.make.pos;

            try {
               this.make.at(this.kInfo.clazz);
               this.addDeserializationCase(var2, var3, var5.type, var6, var5, var7, var21);
            } finally {
               this.make.at(var25);
            }
         }
      }

      return this.makeIndyCall(var5, this.syms.lambdaMetafactory, var22, var7, var21, var4, var6.name);
   }

   private JCTree.JCExpression makeIndyCall(JCDiagnostic.DiagnosticPosition var1, Type var2, Name var3, List var4, Type.MethodType var5, List var6, Name var7) {
      int var8 = this.make.pos;

      JCTree.JCMethodInvocation var14;
      try {
         this.make.at(var1);
         List var9 = List.of(this.syms.methodHandleLookupType, this.syms.stringType, this.syms.methodTypeType).appendList(this.bsmStaticArgToTypes(var4));
         Symbol.MethodSymbol var10 = this.rs.resolveInternalMethod(var1, this.attrEnv, var2, var3, var9, List.nil());
         Symbol.DynamicMethodSymbol var11 = new Symbol.DynamicMethodSymbol(var7, this.syms.noSymbol, var10.isStatic() ? 6 : 5, (Symbol.MethodSymbol)var10, var5, var4.toArray());
         JCTree.JCFieldAccess var12 = this.make.Select(this.make.QualIdent(var2.tsym), var3);
         var12.sym = var11;
         var12.type = var5.getReturnType();
         JCTree.JCMethodInvocation var13 = this.make.Apply(List.nil(), var12, var6);
         var13.type = var5.getReturnType();
         var14 = var13;
      } finally {
         this.make.at(var8);
      }

      return var14;
   }

   private List bsmStaticArgToTypes(List var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2.append(this.bsmStaticArgToType(var4));
      }

      return var2.toList();
   }

   private Type bsmStaticArgToType(Object var1) {
      Assert.checkNonNull(var1);
      if (var1 instanceof Symbol.ClassSymbol) {
         return this.syms.classType;
      } else if (var1 instanceof Integer) {
         return this.syms.intType;
      } else if (var1 instanceof Long) {
         return this.syms.longType;
      } else if (var1 instanceof Float) {
         return this.syms.floatType;
      } else if (var1 instanceof Double) {
         return this.syms.doubleType;
      } else if (var1 instanceof String) {
         return this.syms.stringType;
      } else if (var1 instanceof Pool.MethodHandle) {
         return this.syms.methodHandleType;
      } else if (var1 instanceof Type.MethodType) {
         return this.syms.methodTypeType;
      } else {
         Assert.error("bad static arg " + var1.getClass());
         return null;
      }
   }

   private int referenceKind(Symbol var1) {
      if (var1.isConstructor()) {
         return 8;
      } else if (var1.isStatic()) {
         return 6;
      } else if ((var1.flags() & 2L) != 0L) {
         return 7;
      } else {
         return var1.enclClass().isInterface() ? 9 : 5;
      }
   }

   private String typeSig(Type var1) {
      L2MSignatureGenerator var2 = new L2MSignatureGenerator();
      var2.assembleSig(var1);
      return var2.toString();
   }

   private String classSig(Type var1) {
      L2MSignatureGenerator var2 = new L2MSignatureGenerator();
      var2.assembleClassSig(var1);
      return var2.toString();
   }

   private class L2MSignatureGenerator extends Types.SignatureGenerator {
      StringBuilder sb = new StringBuilder();

      L2MSignatureGenerator() {
         super(LambdaToMethod.this.types);
      }

      protected void append(char var1) {
         this.sb.append(var1);
      }

      protected void append(byte[] var1) {
         this.sb.append(new String(var1));
      }

      protected void append(Name var1) {
         this.sb.append(var1.toString());
      }

      public String toString() {
         return this.sb.toString();
      }
   }

   static enum LambdaSymbolKind {
      PARAM,
      LOCAL_VAR,
      CAPTURED_VAR,
      CAPTURED_THIS,
      CAPTURED_OUTER_THIS,
      TYPE_VAR;

      boolean propagateAnnotations() {
         switch (this) {
            case CAPTURED_THIS:
            case CAPTURED_VAR:
            case CAPTURED_OUTER_THIS:
               return false;
            case TYPE_VAR:
            default:
               return true;
         }
      }
   }

   class LambdaAnalyzerPreprocessor extends TreeTranslator {
      private List frameStack;
      private int lambdaCount = 0;
      private List typesUnderConstruction;
      private SyntheticMethodNameCounter syntheticMethodNameCounts = new SyntheticMethodNameCounter();
      private Map localClassDefs;
      private Map clinits = new HashMap();

      private JCTree.JCClassDecl analyzeAndPreprocessClass(JCTree.JCClassDecl var1) {
         this.frameStack = List.nil();
         this.typesUnderConstruction = List.nil();
         this.localClassDefs = new HashMap();
         return (JCTree.JCClassDecl)this.translate(var1);
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         List var2 = this.typesUnderConstruction;

         try {
            Name var3 = TreeInfo.name(var1.meth);
            if (var3 == LambdaToMethod.this.names._this || var3 == LambdaToMethod.this.names._super) {
               this.typesUnderConstruction = this.typesUnderConstruction.prepend(this.currentClass());
            }

            super.visitApply(var1);
         } finally {
            this.typesUnderConstruction = var2;
         }

      }

      private Symbol.ClassSymbol currentClass() {
         Iterator var1 = this.frameStack.iterator();

         Frame var2;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            var2 = (Frame)var1.next();
         } while(!var2.tree.hasTag(JCTree.Tag.CLASSDEF));

         JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)var2.tree;
         return var3.sym;
      }

      public void visitBlock(JCTree.JCBlock var1) {
         List var2 = this.frameStack;

         try {
            if (this.frameStack.nonEmpty() && ((Frame)this.frameStack.head).tree.hasTag(JCTree.Tag.CLASSDEF)) {
               this.frameStack = this.frameStack.prepend(new Frame(var1));
            }

            super.visitBlock(var1);
         } finally {
            this.frameStack = var2;
         }

      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         List var2 = this.frameStack;
         int var3 = this.lambdaCount;
         SyntheticMethodNameCounter var4 = this.syntheticMethodNameCounts;
         Object var5 = this.clinits;
         DiagnosticSource var6 = LambdaToMethod.this.log.currentSource();

         try {
            LambdaToMethod.this.log.useSource(var1.sym.sourcefile);
            this.lambdaCount = 0;
            this.syntheticMethodNameCounts = new SyntheticMethodNameCounter();
            var5 = new HashMap();
            if (var1.sym.owner.kind == 16) {
               this.localClassDefs.put(var1.sym, var1);
            }

            if (this.directlyEnclosingLambda() != null) {
               var1.sym.owner = this.owner();
               if (var1.sym.hasOuterInstance()) {
                  for(TranslationContext var7 = this.context(); var7 != null; var7 = var7.prev) {
                     if (var7.tree.getTag() == JCTree.Tag.LAMBDA) {
                        ((LambdaTranslationContext)var7).addSymbol(var1.sym.type.getEnclosingType().tsym, LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS);
                     }
                  }
               }
            }

            this.frameStack = this.frameStack.prepend(new Frame(var1));
            super.visitClassDef(var1);
         } finally {
            LambdaToMethod.this.log.useSource(var6.getFile());
            this.frameStack = var2;
            this.lambdaCount = var3;
            this.syntheticMethodNameCounts = var4;
            this.clinits = (Map)var5;
         }

      }

      public void visitIdent(JCTree.JCIdent var1) {
         if (this.context() != null && this.lambdaIdentSymbolFilter(var1.sym)) {
            TranslationContext var2;
            JCTree var3;
            if (var1.sym.kind == 4 && var1.sym.owner.kind == 16 && var1.type.constValue() == null) {
               for(var2 = this.context(); var2 != null; var2 = var2.prev) {
                  if (var2.tree.getTag() == JCTree.Tag.LAMBDA) {
                     var3 = this.capturedDecl(var2.depth, var1.sym);
                     if (var3 == null) {
                        break;
                     }

                     ((LambdaTranslationContext)var2).addSymbol(var1.sym, LambdaToMethod.LambdaSymbolKind.CAPTURED_VAR);
                  }
               }
            } else if (var1.sym.owner.kind == 2) {
               for(var2 = this.context(); var2 != null; var2 = var2.prev) {
                  if (var2.tree.hasTag(JCTree.Tag.LAMBDA)) {
                     var3 = this.capturedDecl(var2.depth, var1.sym);
                     if (var3 == null) {
                        break;
                     }

                     switch (var3.getTag()) {
                        case CLASSDEF:
                           JCTree.JCClassDecl var4 = (JCTree.JCClassDecl)var3;
                           ((LambdaTranslationContext)var2).addSymbol(var4.sym, LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS);
                           break;
                        default:
                           Assert.error("bad block kind");
                     }
                  }
               }
            }
         }

         super.visitIdent(var1);
      }

      public void visitLambda(JCTree.JCLambda var1) {
         this.analyzeLambda(var1, "lambda.stat");
      }

      private void analyzeLambda(JCTree.JCLambda var1, JCTree.JCExpression var2) {
         JCTree.JCExpression var3 = (JCTree.JCExpression)this.translate(var2);
         LambdaTranslationContext var4 = this.analyzeLambda(var1, "mref.stat.1");
         if (var3 != null) {
            var4.methodReferenceReceiver = var3;
         }

      }

      private LambdaTranslationContext analyzeLambda(JCTree.JCLambda var1, String var2) {
         List var3 = this.frameStack;

         try {
            LambdaTranslationContext var4 = new LambdaTranslationContext(var1);
            if (LambdaToMethod.this.dumpLambdaToMethodStats) {
               LambdaToMethod.this.log.note(var1, var2, new Object[]{var4.needsAltMetafactory(), var4.translatedSym});
            }

            this.frameStack = this.frameStack.prepend(new Frame(var1));
            Iterator var5 = var1.params.iterator();

            while(var5.hasNext()) {
               JCTree.JCVariableDecl var6 = (JCTree.JCVariableDecl)var5.next();
               var4.addSymbol(var6.sym, LambdaToMethod.LambdaSymbolKind.PARAM);
               ((Frame)this.frameStack.head).addLocal(var6.sym);
            }

            LambdaToMethod.this.contextMap.put(var1, var4);
            super.visitLambda(var1);
            var4.complete();
            LambdaTranslationContext var10 = var4;
            return var10;
         } finally {
            this.frameStack = var3;
         }
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         List var2 = this.frameStack;

         try {
            this.frameStack = this.frameStack.prepend(new Frame(var1));
            super.visitMethodDef(var1);
         } finally {
            this.frameStack = var2;
         }

      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         Symbol.TypeSymbol var2 = var1.type.tsym;
         boolean var3 = this.currentlyInClass(var2);
         boolean var4 = var2.isLocal();
         if (var3 && var4 || this.lambdaNewClassFilter(this.context(), var1)) {
            for(TranslationContext var5 = this.context(); var5 != null; var5 = var5.prev) {
               if (var5.tree.getTag() == JCTree.Tag.LAMBDA) {
                  ((LambdaTranslationContext)var5).addSymbol(var1.type.getEnclosingType().tsym, LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS);
               }
            }
         }

         if (this.context() != null && !var3 && var4) {
            LambdaTranslationContext var6 = (LambdaTranslationContext)this.context();
            this.captureLocalClassDefs(var2, var6);
         }

         super.visitNewClass(var1);
      }

      void captureLocalClassDefs(Symbol var1, final LambdaTranslationContext var2) {
         JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)this.localClassDefs.get(var1);
         if (var3 != null && var2.freeVarProcessedLocalClasses.add(var1)) {
            Lower var10003 = LambdaToMethod.this.lower;
            var10003.getClass();
            Lower.BasicFreeVarCollector var4 = new Lower.BasicFreeVarCollector(var10003) {
               {
                  var2x.getClass();
               }

               void addFreeVars(Symbol.ClassSymbol var1) {
                  LambdaAnalyzerPreprocessor.this.captureLocalClassDefs(var1, var2);
               }

               void visitSymbol(Symbol var1) {
                  if (var1.kind == 4 && var1.owner.kind == 16 && ((Symbol.VarSymbol)var1).getConstValue() == null) {
                     for(TranslationContext var2x = LambdaAnalyzerPreprocessor.this.context(); var2x != null; var2x = var2x.prev) {
                        if (var2x.tree.getTag() == JCTree.Tag.LAMBDA) {
                           JCTree var3 = LambdaAnalyzerPreprocessor.this.capturedDecl(var2x.depth, var1);
                           if (var3 == null) {
                              break;
                           }

                           ((LambdaTranslationContext)var2x).addSymbol(var1, LambdaToMethod.LambdaSymbolKind.CAPTURED_VAR);
                        }
                     }
                  }

               }
            };
            var4.scan(var3);
         }

      }

      boolean currentlyInClass(Symbol var1) {
         Iterator var2 = this.frameStack.iterator();

         while(var2.hasNext()) {
            Frame var3 = (Frame)var2.next();
            if (var3.tree.hasTag(JCTree.Tag.CLASSDEF)) {
               JCTree.JCClassDecl var4 = (JCTree.JCClassDecl)var3.tree;
               if (var4.sym == var1) {
                  return true;
               }
            }
         }

         return false;
      }

      public void visitReference(JCTree.JCMemberReference var1) {
         ReferenceTranslationContext var2 = new ReferenceTranslationContext(var1);
         LambdaToMethod.this.contextMap.put(var1, var2);
         if (var2.needsConversionToLambda()) {
            MemberReferenceToLambda var3 = LambdaToMethod.this.new MemberReferenceToLambda(var1, var2, this.owner());
            this.analyzeLambda(var3.lambda(), var3.getReceiverExpression());
         } else {
            super.visitReference(var1);
            if (LambdaToMethod.this.dumpLambdaToMethodStats) {
               LambdaToMethod.this.log.note(var1, "mref.stat", new Object[]{var2.needsAltMetafactory(), null});
            }
         }

      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         if (this.context() != null && var1.sym.kind == 4 && (var1.sym.name == LambdaToMethod.this.names._this || var1.sym.name == LambdaToMethod.this.names._super)) {
            for(TranslationContext var2 = this.context(); var2 != null; var2 = var2.prev) {
               if (var2.tree.hasTag(JCTree.Tag.LAMBDA)) {
                  JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)this.capturedDecl(var2.depth, var1.sym);
                  if (var3 == null) {
                     break;
                  }

                  ((LambdaTranslationContext)var2).addSymbol(var3.sym, LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS);
               }
            }
         }

         super.visitSelect(var1);
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         TranslationContext var2 = this.context();
         LambdaTranslationContext var3 = var2 != null && var2 instanceof LambdaTranslationContext ? (LambdaTranslationContext)var2 : null;
         if (var3 != null) {
            if (((Frame)this.frameStack.head).tree.hasTag(JCTree.Tag.LAMBDA)) {
               var3.addSymbol(var1.sym, LambdaToMethod.LambdaSymbolKind.LOCAL_VAR);
            }

            Type var4 = var1.sym.asType();
            if (this.inClassWithinLambda() && !LambdaToMethod.this.types.isSameType(LambdaToMethod.this.types.erasure(var4), var4)) {
               var3.addSymbol(var1.sym, LambdaToMethod.LambdaSymbolKind.TYPE_VAR);
            }
         }

         List var8 = this.frameStack;

         try {
            if (var1.sym.owner.kind == 16) {
               ((Frame)this.frameStack.head).addLocal(var1.sym);
            }

            this.frameStack = this.frameStack.prepend(new Frame(var1));
            super.visitVarDef(var1);
         } finally {
            this.frameStack = var8;
         }

      }

      private Symbol owner() {
         return this.owner(false);
      }

      private Symbol owner(boolean var1) {
         List var2 = this.frameStack;

         while(var2.nonEmpty()) {
            switch (((Frame)var2.head).tree.getTag()) {
               case CLASSDEF:
                  return ((JCTree.JCClassDecl)((Frame)var2.head).tree).sym;
               case VARDEF:
                  if (!((JCTree.JCVariableDecl)((Frame)var2.head).tree).sym.isLocal()) {
                     JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)((Frame)var2.tail.head).tree;
                     return this.initSym(var3.sym, ((JCTree.JCVariableDecl)((Frame)var2.head).tree).sym.flags() & 8L);
                  }

                  var2 = var2.tail;
                  break;
               case BLOCK:
                  JCTree.JCClassDecl var4 = (JCTree.JCClassDecl)((Frame)var2.tail.head).tree;
                  return this.initSym(var4.sym, ((JCTree.JCBlock)((Frame)var2.head).tree).flags & 8L);
               case METHODDEF:
                  return ((JCTree.JCMethodDecl)((Frame)var2.head).tree).sym;
               case LAMBDA:
                  if (!var1) {
                     return ((LambdaTranslationContext)LambdaToMethod.this.contextMap.get(((Frame)var2.head).tree)).translatedSym;
                  }
               default:
                  var2 = var2.tail;
            }
         }

         Assert.error();
         return null;
      }

      private Symbol initSym(Symbol.ClassSymbol var1, long var2) {
         boolean var4 = (var2 & 8L) != 0L;
         if (var4) {
            Symbol.MethodSymbol var7 = LambdaToMethod.this.attr.removeClinit(var1);
            if (var7 != null) {
               this.clinits.put(var1, var7);
               return var7;
            } else {
               var7 = (Symbol.MethodSymbol)this.clinits.get(var1);
               if (var7 == null) {
                  var7 = LambdaToMethod.this.makePrivateSyntheticMethod(8L, LambdaToMethod.this.names.clinit, new Type.MethodType(List.nil(), LambdaToMethod.this.syms.voidType, List.nil(), LambdaToMethod.this.syms.methodClass), var1);
                  this.clinits.put(var1, var7);
               }

               return var7;
            }
         } else {
            Iterator var5 = var1.members_field.getElementsByName(LambdaToMethod.this.names.init).iterator();
            if (var5.hasNext()) {
               Symbol var6 = (Symbol)var5.next();
               return var6;
            } else {
               Assert.error("init not found");
               return null;
            }
         }
      }

      private JCTree directlyEnclosingLambda() {
         if (this.frameStack.isEmpty()) {
            return null;
         } else {
            List var1 = this.frameStack;

            while(var1.nonEmpty()) {
               switch (((Frame)var1.head).tree.getTag()) {
                  case CLASSDEF:
                  case METHODDEF:
                     return null;
                  case VARDEF:
                  case BLOCK:
                  default:
                     var1 = var1.tail;
                     break;
                  case LAMBDA:
                     return ((Frame)var1.head).tree;
               }
            }

            Assert.error();
            return null;
         }
      }

      private boolean inClassWithinLambda() {
         if (this.frameStack.isEmpty()) {
            return false;
         } else {
            List var1 = this.frameStack;
            boolean var2 = false;

            while(var1.nonEmpty()) {
               switch (((Frame)var1.head).tree.getTag()) {
                  case CLASSDEF:
                     var2 = true;
                     var1 = var1.tail;
                     break;
                  case LAMBDA:
                     return var2;
                  default:
                     var1 = var1.tail;
               }
            }

            return false;
         }
      }

      private JCTree capturedDecl(int var1, Symbol var2) {
         int var3 = this.frameStack.size() - 1;

         for(Iterator var4 = this.frameStack.iterator(); var4.hasNext(); --var3) {
            Frame var5 = (Frame)var4.next();
            switch (var5.tree.getTag()) {
               case CLASSDEF:
                  Symbol.ClassSymbol var6 = ((JCTree.JCClassDecl)var5.tree).sym;
                  if (var2.isMemberOf(var6, LambdaToMethod.this.types)) {
                     return var3 > var1 ? null : var5.tree;
                  }
                  break;
               case VARDEF:
                  if (((JCTree.JCVariableDecl)var5.tree).sym == var2 && var2.owner.kind == 16) {
                     return var3 > var1 ? null : var5.tree;
                  }
                  break;
               case BLOCK:
               case METHODDEF:
               case LAMBDA:
                  if (var5.locals != null && var5.locals.contains(var2)) {
                     return var3 > var1 ? null : var5.tree;
                  }
                  break;
               default:
                  Assert.error("bad decl kind " + var5.tree.getTag());
            }
         }

         return null;
      }

      private TranslationContext context() {
         Iterator var1 = this.frameStack.iterator();

         TranslationContext var3;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            Frame var2 = (Frame)var1.next();
            var3 = (TranslationContext)LambdaToMethod.this.contextMap.get(var2.tree);
         } while(var3 == null);

         return var3;
      }

      private boolean lambdaIdentSymbolFilter(Symbol var1) {
         return (var1.kind == 4 || var1.kind == 16) && !var1.isStatic() && var1.name != LambdaToMethod.this.names.init;
      }

      private boolean lambdaFieldAccessFilter(JCTree.JCFieldAccess var1) {
         LambdaTranslationContext var2 = LambdaToMethod.this.context instanceof LambdaTranslationContext ? (LambdaTranslationContext)LambdaToMethod.this.context : null;
         return var2 != null && !var1.sym.isStatic() && var1.name == LambdaToMethod.this.names._this && var1.sym.owner.kind == 2 && !((Map)var2.translatedSymbols.get(LambdaToMethod.LambdaSymbolKind.CAPTURED_OUTER_THIS)).isEmpty();
      }

      private boolean lambdaNewClassFilter(TranslationContext var1, JCTree.JCNewClass var2) {
         if (var1 != null && var2.encl == null && var2.def == null && !var2.type.getEnclosingType().hasTag(TypeTag.NONE)) {
            Type var3 = var2.type.getEnclosingType();

            for(Type var4 = var1.owner.enclClass().type; !var4.hasTag(TypeTag.NONE); var4 = var4.getEnclosingType()) {
               if (var4.tsym.isSubClass(var3.tsym, LambdaToMethod.this.types)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      private final class ReferenceTranslationContext extends TranslationContext {
         final boolean isSuper;
         final Symbol sigPolySym;

         ReferenceTranslationContext(JCTree.JCMemberReference var2) {
            super(var2);
            this.isSuper = var2.hasKind(JCTree.JCMemberReference.ReferenceKind.SUPER);
            this.sigPolySym = this.isSignaturePolymorphic() ? LambdaToMethod.this.makePrivateSyntheticMethod(var2.sym.flags(), var2.sym.name, this.bridgedRefSig(), var2.sym.enclClass()) : null;
         }

         int referenceKind() {
            return LambdaToMethod.this.referenceKind(((JCTree.JCMemberReference)this.tree).sym);
         }

         boolean needsVarArgsConversion() {
            return ((JCTree.JCMemberReference)this.tree).varargsElement != null;
         }

         boolean isArrayOp() {
            return ((JCTree.JCMemberReference)this.tree).sym.owner == LambdaToMethod.this.syms.arrayClass;
         }

         boolean receiverAccessible() {
            return ((JCTree.JCMemberReference)this.tree).ownerAccessible;
         }

         boolean isPrivateInOtherClass() {
            return (((JCTree.JCMemberReference)this.tree).sym.flags() & 2L) != 0L && !LambdaToMethod.this.types.isSameType(LambdaToMethod.this.types.erasure(((JCTree.JCMemberReference)this.tree).sym.enclClass().asType()), LambdaToMethod.this.types.erasure(this.owner.enclClass().asType()));
         }

         final boolean isSignaturePolymorphic() {
            return ((JCTree.JCMemberReference)this.tree).sym.kind == 16 && LambdaToMethod.this.types.isSignaturePolymorphic((Symbol.MethodSymbol)((JCTree.JCMemberReference)this.tree).sym);
         }

         boolean interfaceParameterIsIntersectionType() {
            List var1 = ((JCTree.JCMemberReference)this.tree).getDescriptorType(LambdaToMethod.this.types).getParameterTypes();
            if (((JCTree.JCMemberReference)this.tree).kind == JCTree.JCMemberReference.ReferenceKind.UNBOUND) {
               var1 = var1.tail;
            }

            for(; var1.nonEmpty(); var1 = var1.tail) {
               Type var2 = (Type)var1.head;
               if (var2.getKind() == TypeKind.TYPEVAR) {
                  Type.TypeVar var3 = (Type.TypeVar)var2;
                  if (var3.bound.getKind() == TypeKind.INTERSECTION) {
                     return true;
                  }
               }
            }

            return false;
         }

         final boolean needsConversionToLambda() {
            return this.interfaceParameterIsIntersectionType() || this.isSuper || this.needsVarArgsConversion() || this.isArrayOp() || this.isPrivateInOtherClass() || !this.receiverAccessible() || ((JCTree.JCMemberReference)this.tree).getMode() == MemberReferenceTree.ReferenceMode.NEW && ((JCTree.JCMemberReference)this.tree).kind != JCTree.JCMemberReference.ReferenceKind.ARRAY_CTOR && (((JCTree.JCMemberReference)this.tree).sym.owner.isLocal() || ((JCTree.JCMemberReference)this.tree).sym.owner.isInner());
         }

         Type generatedRefSig() {
            return LambdaToMethod.this.types.erasure(((JCTree.JCMemberReference)this.tree).sym.type);
         }

         Type bridgedRefSig() {
            return LambdaToMethod.this.types.erasure(LambdaToMethod.this.types.findDescriptorSymbol(((Type)((JCTree.JCMemberReference)this.tree).targets.head).tsym).type);
         }
      }

      private class LambdaTranslationContext extends TranslationContext {
         final Symbol self;
         final Symbol assignedTo;
         Map translatedSymbols;
         Symbol.MethodSymbol translatedSym;
         List syntheticParams;
         final Set freeVarProcessedLocalClasses;
         JCTree.JCExpression methodReferenceReceiver;

         LambdaTranslationContext(JCTree.JCLambda var2) {
            super(var2);
            Frame var3 = (Frame)LambdaAnalyzerPreprocessor.this.frameStack.head;
            switch (var3.tree.getTag()) {
               case VARDEF:
                  this.assignedTo = this.self = ((JCTree.JCVariableDecl)var3.tree).sym;
                  break;
               case ASSIGN:
                  this.self = null;
                  this.assignedTo = TreeInfo.symbol(((JCTree.JCAssign)var3.tree).getVariable());
                  break;
               default:
                  this.assignedTo = this.self = null;
            }

            this.translatedSym = LambdaToMethod.this.makePrivateSyntheticMethod(0L, (Name)null, (Type)null, this.owner.enclClass());
            this.translatedSymbols = new EnumMap(LambdaSymbolKind.class);
            this.translatedSymbols.put(LambdaToMethod.LambdaSymbolKind.PARAM, new LinkedHashMap());
            this.translatedSymbols.put(LambdaToMethod.LambdaSymbolKind.LOCAL_VAR, new LinkedHashMap());
            this.translatedSymbols.put(LambdaToMethod.LambdaSymbolKind.CAPTURED_VAR, new LinkedHashMap());
            this.translatedSymbols.put(LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS, new LinkedHashMap());
            this.translatedSymbols.put(LambdaToMethod.LambdaSymbolKind.CAPTURED_OUTER_THIS, new LinkedHashMap());
            this.translatedSymbols.put(LambdaToMethod.LambdaSymbolKind.TYPE_VAR, new LinkedHashMap());
            this.freeVarProcessedLocalClasses = new HashSet();
         }

         private String serializedLambdaDisambiguation() {
            StringBuilder var1 = new StringBuilder();
            Assert.check(this.owner.type != null || LambdaAnalyzerPreprocessor.this.directlyEnclosingLambda() != null);
            if (this.owner.type != null) {
               var1.append(LambdaToMethod.this.typeSig(this.owner.type));
               var1.append(":");
            }

            var1.append(LambdaToMethod.this.types.findDescriptorSymbol(((JCTree.JCLambda)this.tree).type.tsym).owner.flatName());
            var1.append(" ");
            if (this.assignedTo != null) {
               var1.append(this.assignedTo.flatName());
               var1.append("=");
            }

            Iterator var2 = this.getSymbolMap(LambdaToMethod.LambdaSymbolKind.CAPTURED_VAR).keySet().iterator();

            while(var2.hasNext()) {
               Symbol var3 = (Symbol)var2.next();
               if (var3 != this.self) {
                  var1.append(LambdaToMethod.this.typeSig(var3.type));
                  var1.append(" ");
                  var1.append(var3.flatName());
                  var1.append(",");
               }
            }

            return var1.toString();
         }

         private Name lambdaName() {
            return LambdaToMethod.this.names.lambda.append(LambdaToMethod.this.names.fromString(this.enclosingMethodName() + "$" + LambdaAnalyzerPreprocessor.this.lambdaCount++));
         }

         private Name serializedLambdaName() {
            StringBuilder var1 = new StringBuilder();
            var1.append(LambdaToMethod.this.names.lambda);
            var1.append(this.enclosingMethodName());
            var1.append('$');
            String var2 = this.serializedLambdaDisambiguation();
            var1.append(Integer.toHexString(var2.hashCode()));
            var1.append('$');
            var1.append(LambdaAnalyzerPreprocessor.this.syntheticMethodNameCounts.getIndex(var1));
            String var3 = var1.toString();
            return LambdaToMethod.this.names.fromString(var3);
         }

         Symbol translate(final Symbol var1, LambdaSymbolKind var2) {
            Object var3;
            switch (var2) {
               case CAPTURED_THIS:
                  var3 = var1;
                  break;
               case TYPE_VAR:
                  var3 = new Symbol.VarSymbol(var1.flags(), var1.name, LambdaToMethod.this.types.erasure(var1.type), var1.owner);
                  ((Symbol.VarSymbol)var3).pos = ((Symbol.VarSymbol)var1).pos;
                  break;
               case CAPTURED_VAR:
                  var3 = new Symbol.VarSymbol(8589938704L, var1.name, LambdaToMethod.this.types.erasure(var1.type), this.translatedSym) {
                     public Symbol baseSymbol() {
                        return var1;
                     }
                  };
                  break;
               case CAPTURED_OUTER_THIS:
                  Name var4 = LambdaToMethod.this.names.fromString(new String(var1.flatName().toString() + LambdaToMethod.this.names.dollarThis));
                  var3 = new Symbol.VarSymbol(8589938704L, var4, LambdaToMethod.this.types.erasure(var1.type), this.translatedSym) {
                     public Symbol baseSymbol() {
                        return var1;
                     }
                  };
                  break;
               case LOCAL_VAR:
                  var3 = new Symbol.VarSymbol(var1.flags() & 16L, var1.name, var1.type, this.translatedSym);
                  ((Symbol.VarSymbol)var3).pos = ((Symbol.VarSymbol)var1).pos;
                  break;
               case PARAM:
                  var3 = new Symbol.VarSymbol(var1.flags() & 16L | 8589934592L, var1.name, LambdaToMethod.this.types.erasure(var1.type), this.translatedSym);
                  ((Symbol.VarSymbol)var3).pos = ((Symbol.VarSymbol)var1).pos;
                  break;
               default:
                  Assert.error(var2.name());
                  throw new AssertionError();
            }

            if (var3 != var1 && var2.propagateAnnotations()) {
               ((Symbol)var3).setDeclarationAttributes(var1.getRawAttributes());
               ((Symbol)var3).setTypeAttributes(var1.getRawTypeAttributes());
            }

            return (Symbol)var3;
         }

         void addSymbol(Symbol var1, LambdaSymbolKind var2) {
            if (var2 == LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS && var1 != null && var1.kind == 2 && !LambdaAnalyzerPreprocessor.this.typesUnderConstruction.isEmpty()) {
               Symbol.ClassSymbol var3 = LambdaAnalyzerPreprocessor.this.currentClass();
               if (var3 != null && LambdaAnalyzerPreprocessor.this.typesUnderConstruction.contains(var3)) {
                  Assert.check(var1 != var3);
                  var2 = LambdaToMethod.LambdaSymbolKind.CAPTURED_OUTER_THIS;
               }
            }

            Map var4 = this.getSymbolMap(var2);
            if (!var4.containsKey(var1)) {
               var4.put(var1, this.translate(var1, var2));
            }

         }

         Map getSymbolMap(LambdaSymbolKind var1) {
            Map var2 = (Map)this.translatedSymbols.get(var1);
            Assert.checkNonNull(var2);
            return var2;
         }

         JCTree translate(JCTree.JCIdent var1) {
            LambdaSymbolKind[] var2 = LambdaToMethod.LambdaSymbolKind.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               LambdaSymbolKind var5 = var2[var4];
               Map var6 = this.getSymbolMap(var5);
               Symbol var7;
               JCTree.JCExpression var8;
               switch (var5) {
                  case CAPTURED_OUTER_THIS:
                     if (var1.sym.owner.kind == 2 && var6.containsKey(var1.sym.owner)) {
                        var7 = (Symbol)var6.get(var1.sym.owner);
                        var8 = LambdaToMethod.this.make.Ident(var7).setType(var1.sym.owner.type);
                        JCTree.JCFieldAccess var9 = LambdaToMethod.this.make.Select(var8, var1.name);
                        var9.setType(var1.type);
                        TreeInfo.setSymbol(var9, var1.sym);
                        return var9;
                     }
                     break;
                  default:
                     if (var6.containsKey(var1.sym)) {
                        var7 = (Symbol)var6.get(var1.sym);
                        var8 = LambdaToMethod.this.make.Ident(var7).setType(var1.type);
                        return var8;
                     }
               }
            }

            return null;
         }

         public JCTree translate(JCTree.JCFieldAccess var1) {
            Assert.check(var1.name == LambdaToMethod.this.names._this);
            Map var2 = (Map)this.translatedSymbols.get(LambdaToMethod.LambdaSymbolKind.CAPTURED_OUTER_THIS);
            if (var2.containsKey(var1.sym.owner)) {
               Symbol var3 = (Symbol)var2.get(var1.sym.owner);
               JCTree.JCExpression var4 = LambdaToMethod.this.make.Ident(var3).setType(var1.sym.owner.type);
               return var4;
            } else {
               return null;
            }
         }

         void complete() {
            if (this.syntheticParams == null) {
               boolean var1 = this.translatedSym.owner.isInterface();
               boolean var2 = !this.getSymbolMap(LambdaToMethod.LambdaSymbolKind.CAPTURED_THIS).isEmpty();
               this.translatedSym.flags_field = 562949953425408L | this.owner.flags_field & 2048L | this.owner.owner.flags_field & 2048L | 2L | (var2 ? (var1 ? 8796093022208L : 0L) : 8L);
               ListBuffer var3 = new ListBuffer();
               ListBuffer var4 = new ListBuffer();
               Iterator var5 = this.getSymbolMap(LambdaToMethod.LambdaSymbolKind.CAPTURED_VAR).values().iterator();

               Symbol var6;
               while(var5.hasNext()) {
                  var6 = (Symbol)var5.next();
                  var3.append(LambdaToMethod.this.make.VarDef((Symbol.VarSymbol)var6, (JCTree.JCExpression)null));
                  var4.append((Symbol.VarSymbol)var6);
               }

               var5 = this.getSymbolMap(LambdaToMethod.LambdaSymbolKind.CAPTURED_OUTER_THIS).values().iterator();

               while(var5.hasNext()) {
                  var6 = (Symbol)var5.next();
                  var3.append(LambdaToMethod.this.make.VarDef((Symbol.VarSymbol)var6, (JCTree.JCExpression)null));
                  var4.append((Symbol.VarSymbol)var6);
               }

               var5 = this.getSymbolMap(LambdaToMethod.LambdaSymbolKind.PARAM).values().iterator();

               while(var5.hasNext()) {
                  var6 = (Symbol)var5.next();
                  var3.append(LambdaToMethod.this.make.VarDef((Symbol.VarSymbol)var6, (JCTree.JCExpression)null));
                  var4.append((Symbol.VarSymbol)var6);
               }

               this.syntheticParams = var3.toList();
               this.translatedSym.params = var4.toList();
               this.translatedSym.name = this.isSerializable() ? this.serializedLambdaName() : this.lambdaName();
               this.translatedSym.type = LambdaToMethod.this.types.createMethodTypeWithParameters(this.generatedLambdaSig(), TreeInfo.types(this.syntheticParams));
            }
         }

         Type generatedLambdaSig() {
            return LambdaToMethod.this.types.erasure(((JCTree.JCLambda)this.tree).getDescriptorType(LambdaToMethod.this.types));
         }
      }

      private abstract class TranslationContext {
         final JCTree.JCFunctionalExpression tree;
         final Symbol owner;
         final int depth;
         final TranslationContext prev;
         final List bridges;

         TranslationContext(JCTree.JCFunctionalExpression var2) {
            this.tree = var2;
            this.owner = LambdaAnalyzerPreprocessor.this.owner();
            this.depth = LambdaAnalyzerPreprocessor.this.frameStack.size() - 1;
            this.prev = LambdaAnalyzerPreprocessor.this.context();
            Symbol.ClassSymbol var3 = LambdaToMethod.this.types.makeFunctionalInterfaceClass(LambdaToMethod.this.attrEnv, LambdaToMethod.this.names.empty, var2.targets, 1536L);
            this.bridges = LambdaToMethod.this.types.functionalInterfaceBridges(var3);
         }

         boolean needsAltMetafactory() {
            return this.tree.targets.length() > 1 || this.isSerializable() || this.bridges.length() > 1;
         }

         boolean isSerializable() {
            if (LambdaToMethod.this.forceSerializable) {
               return true;
            } else {
               Iterator var1 = this.tree.targets.iterator();

               Type var2;
               do {
                  if (!var1.hasNext()) {
                     return false;
                  }

                  var2 = (Type)var1.next();
               } while(LambdaToMethod.this.types.asSuper(var2, LambdaToMethod.this.syms.serializableType.tsym) == null);

               return true;
            }
         }

         String enclosingMethodName() {
            return this.syntheticMethodNameComponent(this.owner.name);
         }

         String syntheticMethodNameComponent(Name var1) {
            if (var1 == null) {
               return "null";
            } else {
               String var2 = var1.toString();
               if (var2.equals("<clinit>")) {
                  var2 = "static";
               } else if (var2.equals("<init>")) {
                  var2 = "new";
               }

               return var2;
            }
         }
      }

      private class Frame {
         final JCTree tree;
         List locals;

         public Frame(JCTree var2) {
            this.tree = var2;
         }

         void addLocal(Symbol var1) {
            if (this.locals == null) {
               this.locals = List.nil();
            }

            this.locals = this.locals.prepend(var1);
         }
      }

      private class SyntheticMethodNameCounter {
         private Map map;

         private SyntheticMethodNameCounter() {
            this.map = new HashMap();
         }

         int getIndex(StringBuilder var1) {
            String var2 = var1.toString();
            Integer var3 = (Integer)this.map.get(var2);
            if (var3 == null) {
               var3 = 0;
            }

            var3 = var3 + 1;
            this.map.put(var2, var3);
            return var3;
         }

         // $FF: synthetic method
         SyntheticMethodNameCounter(Object var2) {
            this();
         }
      }
   }

   private class MemberReferenceToLambda {
      private final JCTree.JCMemberReference tree;
      private final LambdaAnalyzerPreprocessor.ReferenceTranslationContext localContext;
      private final Symbol owner;
      private final ListBuffer args = new ListBuffer();
      private final ListBuffer params = new ListBuffer();
      private JCTree.JCExpression receiverExpression = null;

      MemberReferenceToLambda(JCTree.JCMemberReference var2, LambdaAnalyzerPreprocessor.ReferenceTranslationContext var3, Symbol var4) {
         this.tree = var2;
         this.localContext = var3;
         this.owner = var4;
      }

      JCTree.JCLambda lambda() {
         int var1 = LambdaToMethod.this.make.pos;

         JCTree.JCLambda var5;
         try {
            LambdaToMethod.this.make.at(this.tree);
            Symbol.VarSymbol var2 = this.addParametersReturnReceiver();
            JCTree.JCExpression var3 = this.tree.getMode() == MemberReferenceTree.ReferenceMode.INVOKE ? this.expressionInvoke(var2) : this.expressionNew();
            JCTree.JCLambda var4 = LambdaToMethod.this.make.Lambda(this.params.toList(), var3);
            var4.targets = this.tree.targets;
            var4.type = this.tree.type;
            var4.pos = this.tree.pos;
            var5 = var4;
         } finally {
            LambdaToMethod.this.make.at(var1);
         }

         return var5;
      }

      Symbol.VarSymbol addParametersReturnReceiver() {
         Type var1 = this.localContext.bridgedRefSig();
         List var2 = var1.getParameterTypes();
         List var3 = this.tree.getDescriptorType(LambdaToMethod.this.types).getParameterTypes();
         Symbol.VarSymbol var4;
         switch (this.tree.kind) {
            case BOUND:
               var4 = this.addParameter("rec$", this.tree.getQualifierExpression().type, false);
               this.receiverExpression = LambdaToMethod.this.attr.makeNullCheck(this.tree.getQualifierExpression());
               break;
            case UNBOUND:
               var4 = this.addParameter("rec$", (Type)var1.getParameterTypes().head, false);
               var2 = var2.tail;
               var3 = var3.tail;
               break;
            default:
               var4 = null;
         }

         List var5 = this.tree.sym.type.getParameterTypes();
         int var6 = var5.size();
         int var7 = var2.size();
         int var8 = this.localContext.needsVarArgsConversion() ? var6 - 1 : var6;
         boolean var9 = this.tree.varargsElement != null || var6 == var3.size();

         int var10;
         for(var10 = 0; var5.nonEmpty() && var10 < var8; ++var10) {
            Type var11 = (Type)var5.head;
            if (var9 && ((Type)var3.head).getKind() == TypeKind.TYPEVAR) {
               Type.TypeVar var12 = (Type.TypeVar)var3.head;
               if (var12.bound.getKind() == TypeKind.INTERSECTION) {
                  var11 = (Type)var2.head;
               }
            }

            this.addParameter("x$" + var10, var11, true);
            var5 = var5.tail;
            var2 = var2.tail;
            var3 = var3.tail;
         }

         for(var10 = var8; var10 < var7; ++var10) {
            this.addParameter("xva$" + var10, this.tree.varargsElement, true);
         }

         return var4;
      }

      JCTree.JCExpression getReceiverExpression() {
         return this.receiverExpression;
      }

      private JCTree.JCExpression makeReceiver(Symbol.VarSymbol var1) {
         if (var1 == null) {
            return null;
         } else {
            Object var2 = LambdaToMethod.this.make.Ident((Symbol)var1);
            Type var3 = this.tree.ownerAccessible ? this.tree.sym.enclClass().type : this.tree.expr.type;
            if (var3 == LambdaToMethod.this.syms.arrayClass.type) {
               var3 = this.tree.getQualifierExpression().type;
            }

            if (!var1.type.tsym.isSubClass(var3.tsym, LambdaToMethod.this.types)) {
               var2 = LambdaToMethod.this.make.TypeCast((JCTree)LambdaToMethod.this.make.Type(var3), (JCTree.JCExpression)var2).setType(var3);
            }

            return (JCTree.JCExpression)var2;
         }
      }

      private JCTree.JCExpression expressionInvoke(Symbol.VarSymbol var1) {
         JCTree.JCExpression var2 = var1 != null ? this.makeReceiver(var1) : this.tree.getQualifierExpression();
         JCTree.JCFieldAccess var3 = LambdaToMethod.this.make.Select(var2, this.tree.sym.name);
         var3.sym = this.tree.sym;
         var3.type = this.tree.sym.erasure(LambdaToMethod.this.types);
         JCTree.JCMethodInvocation var4 = LambdaToMethod.this.make.Apply(List.nil(), var3, LambdaToMethod.this.convertArgs(this.tree.sym, this.args.toList(), this.tree.varargsElement)).setType(this.tree.sym.erasure(LambdaToMethod.this.types).getReturnType());
         JCTree.JCExpression var5 = LambdaToMethod.this.transTypes.coerce(var4, this.localContext.generatedRefSig().getReturnType());
         LambdaToMethod.this.setVarargsIfNeeded(var5, this.tree.varargsElement);
         return var5;
      }

      private JCTree.JCExpression expressionNew() {
         if (this.tree.kind == JCTree.JCMemberReference.ReferenceKind.ARRAY_CTOR) {
            JCTree.JCNewArray var2 = LambdaToMethod.this.make.NewArray(LambdaToMethod.this.make.Type(LambdaToMethod.this.types.elemtype(this.tree.getQualifierExpression().type)), List.of(LambdaToMethod.this.make.Ident((JCTree.JCVariableDecl)this.params.first())), (List)null);
            var2.type = this.tree.getQualifierExpression().type;
            return var2;
         } else {
            JCTree.JCNewClass var1 = LambdaToMethod.this.make.NewClass((JCTree.JCExpression)null, List.nil(), LambdaToMethod.this.make.Type(this.tree.getQualifierExpression().type), LambdaToMethod.this.convertArgs(this.tree.sym, this.args.toList(), this.tree.varargsElement), (JCTree.JCClassDecl)null);
            var1.constructor = this.tree.sym;
            var1.constructorType = this.tree.sym.erasure(LambdaToMethod.this.types);
            var1.type = this.tree.getQualifierExpression().type;
            LambdaToMethod.this.setVarargsIfNeeded(var1, this.tree.varargsElement);
            return var1;
         }
      }

      private Symbol.VarSymbol addParameter(String var1, Type var2, boolean var3) {
         Symbol.VarSymbol var4 = new Symbol.VarSymbol(8589938688L, LambdaToMethod.this.names.fromString(var1), var2, this.owner);
         var4.pos = this.tree.pos;
         this.params.append(LambdaToMethod.this.make.VarDef(var4, (JCTree.JCExpression)null));
         if (var3) {
            this.args.append(LambdaToMethod.this.make.Ident((Symbol)var4));
         }

         return var4;
      }
   }

   private class KlassInfo {
      private ListBuffer appendedMethodList;
      private final Map deserializeCases;
      private final Symbol.MethodSymbol deserMethodSym;
      private final Symbol.VarSymbol deserParamSym;
      private final JCTree.JCClassDecl clazz;

      private KlassInfo(JCTree.JCClassDecl var2) {
         this.clazz = var2;
         this.appendedMethodList = new ListBuffer();
         this.deserializeCases = new HashMap();
         Type.MethodType var3 = new Type.MethodType(List.of(LambdaToMethod.this.syms.serializedLambdaType), LambdaToMethod.this.syms.objectType, List.nil(), LambdaToMethod.this.syms.methodClass);
         this.deserMethodSym = LambdaToMethod.this.makePrivateSyntheticMethod(8L, LambdaToMethod.this.names.deserializeLambda, var3, var2.sym);
         this.deserParamSym = new Symbol.VarSymbol(16L, LambdaToMethod.this.names.fromString("lambda"), LambdaToMethod.this.syms.serializedLambdaType, this.deserMethodSym);
      }

      private void addMethod(JCTree var1) {
         this.appendedMethodList = this.appendedMethodList.prepend(var1);
      }

      // $FF: synthetic method
      KlassInfo(JCTree.JCClassDecl var2, Object var3) {
         this(var2);
      }
   }
}
