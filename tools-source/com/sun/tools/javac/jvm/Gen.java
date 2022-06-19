package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TargetType;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Check;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.Flow;
import com.sun.tools.javac.comp.Lower;
import com.sun.tools.javac.comp.Resolve;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.model.FilteredMemberList;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.lang.model.element.ElementKind;

public class Gen extends JCTree.Visitor {
   protected static final Context.Key genKey = new Context.Key();
   private final Log log;
   private final Symtab syms;
   private final Check chk;
   private final Resolve rs;
   private final TreeMaker make;
   private final Names names;
   private final Target target;
   private final Type stringBufferType;
   private final Map stringBufferAppend;
   private Name accessDollar;
   private final Types types;
   private final Lower lower;
   private final Flow flow;
   private final boolean allowGenerics;
   private final boolean generateIproxies;
   private final Code.StackMapFormat stackMap;
   private final Type methodType;
   private Pool pool;
   private final boolean typeAnnoAsserts;
   private final boolean lineDebugInfo;
   private final boolean varDebugInfo;
   private final boolean genCrt;
   private final boolean debugCode;
   private final boolean allowInvokedynamic;
   private final int jsrlimit;
   private boolean useJsrLocally;
   private Code code;
   private Items items;
   private Env attrEnv;
   private JCTree.JCCompilationUnit toplevel;
   private int nerrs = 0;
   EndPosTable endPosTable;
   Env env;
   Type pt;
   Items.Item result;
   private ClassReferenceVisitor classReferenceVisitor = new ClassReferenceVisitor();

   public static Gen instance(Context var0) {
      Gen var1 = (Gen)var0.get(genKey);
      if (var1 == null) {
         var1 = new Gen(var0);
      }

      return var1;
   }

   protected Gen(Context var1) {
      var1.put((Context.Key)genKey, (Object)this);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.chk = Check.instance(var1);
      this.rs = Resolve.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.target = Target.instance(var1);
      this.types = Types.instance(var1);
      this.methodType = new Type.MethodType((List)null, (Type)null, (List)null, this.syms.methodClass);
      this.allowGenerics = Source.instance(var1).allowGenerics();
      this.stringBufferType = this.target.useStringBuilder() ? this.syms.stringBuilderType : this.syms.stringBufferType;
      this.stringBufferAppend = new HashMap();
      this.accessDollar = this.names.fromString("access" + this.target.syntheticNameChar());
      this.flow = Flow.instance(var1);
      this.lower = Lower.instance(var1);
      Options var2 = Options.instance(var1);
      this.lineDebugInfo = var2.isUnset(Option.G_CUSTOM) || var2.isSet(Option.G_CUSTOM, "lines");
      this.varDebugInfo = var2.isUnset(Option.G_CUSTOM) ? var2.isSet(Option.G) : var2.isSet(Option.G_CUSTOM, "vars");
      this.genCrt = var2.isSet(Option.XJCOV);
      this.debugCode = var2.isSet("debugcode");
      this.allowInvokedynamic = this.target.hasInvokedynamic() || var2.isSet("invokedynamic");
      this.pool = new Pool(this.types);
      this.typeAnnoAsserts = var2.isSet("TypeAnnotationAsserts");
      this.generateIproxies = this.target.requiresIproxy() || var2.isSet("miranda");
      if (this.target.generateStackMapTable()) {
         this.stackMap = Code.StackMapFormat.JSR202;
      } else if (this.target.generateCLDCStackmap()) {
         this.stackMap = Code.StackMapFormat.CLDC;
      } else {
         this.stackMap = Code.StackMapFormat.NONE;
      }

      int var3 = 50;
      String var4 = var2.get("jsrlimit");
      if (var4 != null) {
         try {
            var3 = Integer.parseInt(var4);
         } catch (NumberFormatException var6) {
         }
      }

      this.jsrlimit = var3;
      this.useJsrLocally = false;
   }

   void loadIntConst(int var1) {
      this.items.makeImmediateItem(this.syms.intType, var1).load();
   }

   public static int zero(int var0) {
      switch (var0) {
         case 0:
         case 5:
         case 6:
         case 7:
            return 3;
         case 1:
            return 9;
         case 2:
            return 11;
         case 3:
            return 14;
         case 4:
         default:
            throw new AssertionError("zero");
      }
   }

   public static int one(int var0) {
      return zero(var0) + 1;
   }

   void emitMinusOne(int var1) {
      if (var1 == 1) {
         this.items.makeImmediateItem(this.syms.longType, new Long(-1L)).load();
      } else {
         this.code.emitop0(2);
      }

   }

   Symbol binaryQualifier(Symbol var1, Type var2) {
      if (var2.hasTag(TypeTag.ARRAY)) {
         if (var1 != this.syms.lengthVar && var1.owner == this.syms.arrayClass) {
            Object var3 = this.target.arrayBinaryCompatibility() ? new Symbol.ClassSymbol(1L, var2.tsym.name, var2, this.syms.noSymbol) : this.syms.objectType.tsym;
            return var1.clone((Symbol)var3);
         } else {
            return var1;
         }
      } else if (var1.owner != var2.tsym && (var1.flags() & 4104L) != 4104L) {
         if (!this.target.obeyBinaryCompatibility()) {
            return this.rs.isAccessible(this.attrEnv, (Symbol.TypeSymbol)var1.owner) ? var1 : var1.clone(var2.tsym);
         } else if (!this.target.interfaceFieldsBinaryCompatibility() && (var1.owner.flags() & 512L) != 0L && var1.kind == 4) {
            return var1;
         } else if (var1.owner == this.syms.objectType.tsym) {
            return var1;
         } else {
            return !this.target.interfaceObjectOverridesBinaryCompatibility() && (var1.owner.flags() & 512L) != 0L && this.syms.objectType.tsym.members().lookup(var1.name).scope != null ? var1 : var1.clone(var2.tsym);
         }
      } else {
         return var1;
      }
   }

   int makeRef(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      this.checkDimension(var1, var2);
      return var2.isAnnotated() ? this.pool.put(var2) : this.pool.put(var2.hasTag(TypeTag.CLASS) ? var2.tsym : var2);
   }

   private void checkDimension(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      switch (var2.getTag()) {
         case METHOD:
            this.checkDimension(var1, var2.getReturnType());

            for(List var3 = var2.getParameterTypes(); var3.nonEmpty(); var3 = var3.tail) {
               this.checkDimension(var1, (Type)var3.head);
            }

            return;
         case ARRAY:
            if (this.types.dimensions(var2) > 255) {
               this.log.error(var1, "limit.dimensions", new Object[0]);
               ++this.nerrs;
            }
      }

   }

   Items.LocalItem makeTemp(Type var1) {
      Symbol.VarSymbol var2 = new Symbol.VarSymbol(4096L, this.names.empty, var1, this.env.enclMethod.sym);
      this.code.newLocal(var2);
      return this.items.makeLocalItem(var2);
   }

   void callMethod(JCDiagnostic.DiagnosticPosition var1, Type var2, Name var3, List var4, boolean var5) {
      Symbol.MethodSymbol var6 = this.rs.resolveInternalMethod(var1, this.attrEnv, var2, var3, var4, (List)null);
      if (var5) {
         this.items.makeStaticItem(var6).invoke();
      } else {
         this.items.makeMemberItem(var6, var3 == this.names.init).invoke();
      }

   }

   private boolean isAccessSuper(JCTree.JCMethodDecl var1) {
      return (var1.mods.flags & 4096L) != 0L && this.isOddAccessName(var1.name);
   }

   private boolean isOddAccessName(Name var1) {
      return var1.startsWith(this.accessDollar) && (var1.getByteAt(var1.getByteLength() - 1) & 1) == 1;
   }

   void genFinalizer(Env var1) {
      if (this.code.isAlive() && ((GenContext)var1.info).finalize != null) {
         ((GenContext)var1.info).finalize.gen();
      }

   }

   Env unwind(JCTree var1, Env var2) {
      Env var3 = var2;

      while(true) {
         this.genFinalizer(var3);
         if (var3.tree == var1) {
            return var3;
         }

         var3 = var3.next;
      }
   }

   void endFinalizerGap(Env var1) {
      if (((GenContext)var1.info).gaps != null && ((GenContext)var1.info).gaps.length() % 2 == 1) {
         ((GenContext)var1.info).gaps.append(this.code.curCP());
      }

   }

   void endFinalizerGaps(Env var1, Env var2) {
      for(Env var3 = null; var3 != var2; var1 = var1.next) {
         this.endFinalizerGap(var1);
         var3 = var1;
      }

   }

   boolean hasFinally(JCTree var1, Env var2) {
      while(var2.tree != var1) {
         if (var2.tree.hasTag(JCTree.Tag.TRY) && ((GenContext)var2.info).finalize.hasFinalizer()) {
            return true;
         }

         var2 = var2.next;
      }

      return false;
   }

   List normalizeDefs(List var1, Symbol.ClassSymbol var2) {
      ListBuffer var3 = new ListBuffer();
      ListBuffer var4 = new ListBuffer();
      ListBuffer var5 = new ListBuffer();
      ListBuffer var6 = new ListBuffer();
      ListBuffer var7 = new ListBuffer();

      List var8;
      JCTree.JCBlock var10;
      for(var8 = var1; var8.nonEmpty(); var8 = var8.tail) {
         JCTree var9 = (JCTree)var8.head;
         switch (var9.getTag()) {
            case BLOCK:
               var10 = (JCTree.JCBlock)var9;
               if ((var10.flags & 8L) != 0L) {
                  var5.append(var10);
               } else if ((var10.flags & 4096L) == 0L) {
                  var3.append(var10);
               }
               break;
            case METHODDEF:
               var7.append(var9);
               break;
            case VARDEF:
               JCTree.JCVariableDecl var11 = (JCTree.JCVariableDecl)var9;
               Symbol.VarSymbol var12 = var11.sym;
               this.checkDimension(var11.pos(), var12.type);
               if (var11.init != null) {
                  JCTree.JCStatement var13;
                  if ((var12.flags() & 8L) == 0L) {
                     var13 = this.make.at(var11.pos()).Assignment(var12, var11.init);
                     var3.append(var13);
                     this.endPosTable.replaceTree(var11, var13);
                     var4.addAll(this.getAndRemoveNonFieldTAs(var12));
                  } else if (var12.getConstValue() == null) {
                     var13 = this.make.at(var11.pos).Assignment(var12, var11.init);
                     var5.append(var13);
                     this.endPosTable.replaceTree(var11, var13);
                     var6.addAll(this.getAndRemoveNonFieldTAs(var12));
                  } else {
                     this.checkStringConstant(var11.init.pos(), var12.getConstValue());
                     var11.init.accept(this.classReferenceVisitor);
                  }
               }
               break;
            default:
               Assert.error();
         }
      }

      List var15;
      if (var3.length() != 0) {
         var8 = var3.toList();
         var4.addAll(var2.getInitTypeAttributes());
         var15 = var4.toList();
         Iterator var16 = var7.iterator();

         while(var16.hasNext()) {
            JCTree var17 = (JCTree)var16.next();
            this.normalizeMethod((JCTree.JCMethodDecl)var17, var8, var15);
         }
      }

      if (var5.length() != 0) {
         Symbol.MethodSymbol var14 = new Symbol.MethodSymbol(8L | var2.flags() & 2048L, this.names.clinit, new Type.MethodType(List.nil(), this.syms.voidType, List.nil(), this.syms.methodClass), var2);
         var2.members().enter(var14);
         var15 = var5.toList();
         var10 = this.make.at(((JCTree.JCStatement)var15.head).pos()).Block(0L, var15);
         var10.endpos = TreeInfo.endPos((JCTree)var15.last());
         var7.append(this.make.MethodDef(var14, var10));
         if (!var6.isEmpty()) {
            var14.appendUniqueTypeAttributes(var6.toList());
         }

         if (!var2.getClassInitTypeAttributes().isEmpty()) {
            var14.appendUniqueTypeAttributes(var2.getClassInitTypeAttributes());
         }
      }

      return var7.toList();
   }

   private List getAndRemoveNonFieldTAs(Symbol.VarSymbol var1) {
      List var2 = var1.getRawTypeAttributes();
      ListBuffer var3 = new ListBuffer();
      ListBuffer var4 = new ListBuffer();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Attribute.TypeCompound var6 = (Attribute.TypeCompound)var5.next();
         if (var6.getPosition().type == TargetType.FIELD) {
            var3.add(var6);
         } else {
            if (this.typeAnnoAsserts) {
               Assert.error("Type annotation does not have a valid positior");
            }

            var4.add(var6);
         }
      }

      var1.setTypeAttributes(var3.toList());
      return var4.toList();
   }

   private void checkStringConstant(JCDiagnostic.DiagnosticPosition var1, Object var2) {
      if (this.nerrs == 0 && var2 != null && var2 instanceof String && ((String)var2).length() >= 65535) {
         this.log.error(var1, "limit.string", new Object[0]);
         ++this.nerrs;
      }
   }

   void normalizeMethod(JCTree.JCMethodDecl var1, List var2, List var3) {
      if (var1.name == this.names.init && TreeInfo.isInitialConstructor(var1)) {
         List var4 = var1.body.stats;
         ListBuffer var5 = new ListBuffer();
         if (var4.nonEmpty()) {
            while(TreeInfo.isSyntheticInit((JCTree)var4.head)) {
               var5.append(var4.head);
               var4 = var4.tail;
            }

            var5.append(var4.head);

            for(var4 = var4.tail; var4.nonEmpty() && TreeInfo.isSyntheticInit((JCTree)var4.head); var4 = var4.tail) {
               var5.append(var4.head);
            }

            var5.appendList(var2);

            while(var4.nonEmpty()) {
               var5.append(var4.head);
               var4 = var4.tail;
            }
         }

         var1.body.stats = var5.toList();
         if (var1.body.endpos == -1) {
            var1.body.endpos = TreeInfo.endPos((JCTree)var1.body.stats.last());
         }

         var1.sym.appendUniqueTypeAttributes(var3);
      }

   }

   void implementInterfaceMethods(Symbol.ClassSymbol var1) {
      this.implementInterfaceMethods(var1, var1);
   }

   void implementInterfaceMethods(Symbol.ClassSymbol var1, Symbol.ClassSymbol var2) {
      for(List var3 = this.types.interfaces(var1.type); var3.nonEmpty(); var3 = var3.tail) {
         Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)((Type)var3.head).tsym;

         for(Scope.Entry var5 = var4.members().elems; var5 != null; var5 = var5.sibling) {
            if (var5.sym.kind == 16 && (var5.sym.flags() & 8L) == 0L) {
               Symbol.MethodSymbol var6 = (Symbol.MethodSymbol)var5.sym;
               Symbol.MethodSymbol var7 = var6.binaryImplementation(var2, this.types);
               if (var7 == null) {
                  this.addAbstractMethod(var2, var6);
               } else if ((var7.flags() & 2097152L) != 0L) {
                  this.adjustAbstractMethod(var2, var7, var6);
               }
            }
         }

         this.implementInterfaceMethods(var4, var2);
      }

   }

   private void addAbstractMethod(Symbol.ClassSymbol var1, Symbol.MethodSymbol var2) {
      Symbol.MethodSymbol var3 = new Symbol.MethodSymbol(var2.flags() | 2097152L | 4096L, var2.name, var2.type, var1);
      var1.members().enter(var3);
   }

   private void adjustAbstractMethod(Symbol.ClassSymbol var1, Symbol.MethodSymbol var2, Symbol.MethodSymbol var3) {
      Type.MethodType var4 = (Type.MethodType)var2.type;
      Type var5 = this.types.memberType(var1.type, var3);
      var4.thrown = this.chk.intersect(var4.getThrownTypes(), var5.getThrownTypes());
   }

   public void genDef(JCTree var1, Env var2) {
      Env var3 = this.env;

      try {
         this.env = var2;
         var1.accept(this);
      } catch (Symbol.CompletionFailure var8) {
         this.chk.completionError(var1.pos(), var8);
      } finally {
         this.env = var3;
      }

   }

   public void genStat(JCTree var1, Env var2, int var3) {
      if (!this.genCrt) {
         this.genStat(var1, var2);
      } else {
         int var4 = this.code.curCP();
         this.genStat(var1, var2);
         if (var1.hasTag(JCTree.Tag.BLOCK)) {
            var3 |= 2;
         }

         this.code.crt.put(var1, var3, var4, this.code.curCP());
      }
   }

   public void genStat(JCTree var1, Env var2) {
      if (this.code.isAlive()) {
         this.code.statBegin(var1.pos);
         this.genDef(var1, var2);
      } else if (((GenContext)var2.info).isSwitch && var1.hasTag(JCTree.Tag.VARDEF)) {
         this.code.newLocal(((JCTree.JCVariableDecl)var1).sym);
      }

   }

   public void genStats(List var1, Env var2, int var3) {
      if (!this.genCrt) {
         this.genStats(var1, var2);
      } else {
         if (var1.length() == 1) {
            this.genStat((JCTree)var1.head, var2, var3 | 1);
         } else {
            int var4 = this.code.curCP();
            this.genStats(var1, var2);
            this.code.crt.put(var1, var3, var4, this.code.curCP());
         }

      }
   }

   public void genStats(List var1, Env var2) {
      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         this.genStat((JCTree)var3.head, var2, 1);
      }

   }

   public Items.CondItem genCond(JCTree var1, int var2) {
      if (!this.genCrt) {
         return this.genCond(var1, false);
      } else {
         int var3 = this.code.curCP();
         Items.CondItem var4 = this.genCond(var1, (var2 & 8) != 0);
         this.code.crt.put(var1, var2, var3, this.code.curCP());
         return var4;
      }
   }

   public Items.CondItem genCond(JCTree var1, boolean var2) {
      JCTree var3 = TreeInfo.skipParens(var1);
      if (var3.hasTag(JCTree.Tag.CONDEXPR)) {
         JCTree.JCConditional var12 = (JCTree.JCConditional)var3;
         Items.CondItem var5 = this.genCond(var12.cond, 8);
         Items.CondItem var13;
         if (var5.isTrue()) {
            this.code.resolve(var5.trueJumps);
            var13 = this.genCond(var12.truepart, 16);
            if (var2) {
               var13.tree = var12.truepart;
            }

            return var13;
         } else if (var5.isFalse()) {
            this.code.resolve(var5.falseJumps);
            var13 = this.genCond(var12.falsepart, 16);
            if (var2) {
               var13.tree = var12.falsepart;
            }

            return var13;
         } else {
            Code.Chain var6 = var5.jumpFalse();
            this.code.resolve(var5.trueJumps);
            Items.CondItem var7 = this.genCond(var12.truepart, 16);
            if (var2) {
               var7.tree = var12.truepart;
            }

            Code.Chain var8 = var7.jumpFalse();
            this.code.resolve(var7.trueJumps);
            Code.Chain var9 = this.code.branch(167);
            this.code.resolve(var6);
            Items.CondItem var10 = this.genCond(var12.falsepart, 16);
            Items.CondItem var11 = this.items.makeCondItem(var10.opcode, Code.mergeChains(var9, var10.trueJumps), Code.mergeChains(var8, var10.falseJumps));
            if (var2) {
               var11.tree = var12.falsepart;
            }

            return var11;
         }
      } else {
         Items.CondItem var4 = this.genExpr(var1, this.syms.booleanType).mkCond();
         if (var2) {
            var4.tree = var1;
         }

         return var4;
      }
   }

   public Items.Item genExpr(JCTree var1, Type var2) {
      Type var3 = this.pt;

      Items.Item var5;
      try {
         if (var1.type.constValue() != null) {
            var1.accept(this.classReferenceVisitor);
            this.checkStringConstant(var1.pos(), var1.type.constValue());
            this.result = this.items.makeImmediateItem(var1.type, var1.type.constValue());
         } else {
            this.pt = var2;
            var1.accept(this);
         }

         Items.Item var4 = this.result.coerce(var2);
         return var4;
      } catch (Symbol.CompletionFailure var9) {
         this.chk.completionError(var1.pos(), var9);
         this.code.state.stacksize = 1;
         var5 = this.items.makeStackItem(var2);
      } finally {
         this.pt = var3;
      }

      return var5;
   }

   public void genArgs(List var1, List var2) {
      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         this.genExpr((JCTree)var3.head, (Type)var2.head).load();
         var2 = var2.tail;
      }

      Assert.check(var2.isEmpty());
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      Env var2 = this.env.dup(var1);
      var2.enclMethod = var1;
      this.pt = var1.sym.erasure(this.types).getReturnType();
      this.checkDimension(var1.pos(), var1.sym.erasure(this.types));
      this.genMethod(var1, var2, false);
   }

   void genMethod(JCTree.JCMethodDecl var1, Env var2, boolean var3) {
      Symbol.MethodSymbol var4 = var1.sym;
      int var5 = 0;
      if (var4.isConstructor()) {
         ++var5;
         if (var4.enclClass().isInner() && !var4.enclClass().isStatic()) {
            ++var5;
         }
      } else if ((var1.mods.flags & 8L) == 0L) {
         ++var5;
      }

      if (Code.width(this.types.erasure(var2.enclMethod.sym.type).getParameterTypes()) + var5 > 255) {
         this.log.error(var1.pos(), "limit.parameters", new Object[0]);
         ++this.nerrs;
      } else if (var1.body != null) {
         int var6 = this.initCode(var1, var2, var3);

         try {
            this.genStat(var1.body, var2);
         } catch (CodeSizeOverflow var9) {
            var6 = this.initCode(var1, var2, var3);
            this.genStat(var1.body, var2);
         }

         if (this.code.state.stacksize != 0) {
            this.log.error(var1.body.pos(), "stack.sim.error", new Object[]{var1});
            throw new AssertionError();
         }

         if (this.code.isAlive()) {
            this.code.statBegin(TreeInfo.endPos(var1.body));
            if (var2.enclMethod != null && !var2.enclMethod.sym.type.getReturnType().hasTag(TypeTag.VOID)) {
               int var7 = this.code.entryPoint();
               Items.CondItem var8 = this.items.makeCondItem(167);
               this.code.resolve(var8.jumpTrue(), var7);
            } else {
               this.code.emitop0(177);
            }
         }

         if (this.genCrt) {
            this.code.crt.put(var1.body, 2, var6, this.code.curCP());
         }

         this.code.endScopes(0);
         if (this.code.checkLimits(var1.pos(), this.log)) {
            ++this.nerrs;
            return;
         }

         if (!var3 && this.code.fatcode) {
            this.genMethod(var1, var2, true);
         }

         if (this.stackMap == Code.StackMapFormat.JSR202) {
            this.code.lastFrame = null;
            this.code.frameBeforeLast = null;
         }

         this.code.compressCatchTable();
         this.code.fillExceptionParameterPositions();
      }

   }

   private int initCode(JCTree.JCMethodDecl var1, Env var2, boolean var3) {
      Symbol.MethodSymbol var4 = var1.sym;
      var4.code = this.code = new Code(var4, var3, this.lineDebugInfo ? this.toplevel.lineMap : null, this.varDebugInfo, this.stackMap, this.debugCode, this.genCrt ? new CRTable(var1, var2.toplevel.endPositions) : null, this.syms, this.types, this.pool);
      this.items = new Items(this.pool, this.code, this.syms, this.types);
      if (this.code.debugCode) {
         System.err.println(var4 + " for body " + var1);
      }

      if ((var1.mods.flags & 8L) == 0L) {
         Object var5 = var4.owner.type;
         if (var4.isConstructor() && var5 != this.syms.objectType) {
            var5 = UninitializedType.uninitializedThis((Type)var5);
         }

         this.code.setDefined(this.code.newLocal(new Symbol.VarSymbol(16L, this.names._this, (Type)var5, var4.owner)));
      }

      for(List var6 = var1.params; var6.nonEmpty(); var6 = var6.tail) {
         this.checkDimension(((JCTree.JCVariableDecl)var6.head).pos(), ((JCTree.JCVariableDecl)var6.head).sym.type);
         this.code.setDefined(this.code.newLocal(((JCTree.JCVariableDecl)var6.head).sym));
      }

      int var7 = this.genCrt ? this.code.curCP() : 0;
      this.code.entryPoint();
      this.code.pendingStackMap = false;
      return var7;
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      Symbol.VarSymbol var2 = var1.sym;
      this.code.newLocal(var2);
      if (var1.init != null) {
         this.checkStringConstant(var1.init.pos(), var2.getConstValue());
         if (var2.getConstValue() == null || this.varDebugInfo) {
            this.genExpr(var1.init, var2.erasure(this.types)).load();
            this.items.makeLocalItem(var2).store();
         }
      }

      this.checkDimension(var1.pos(), var2.type);
   }

   public void visitSkip(JCTree.JCSkip var1) {
   }

   public void visitBlock(JCTree.JCBlock var1) {
      int var2 = this.code.nextreg;
      Env var3 = this.env.dup(var1, new GenContext());
      this.genStats(var1.stats, var3);
      if (!this.env.tree.hasTag(JCTree.Tag.METHODDEF)) {
         this.code.statBegin(var1.endpos);
         this.code.endScopes(var2);
         this.code.pendingStatPos = -1;
      }

   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      this.genLoop(var1, var1.body, var1.cond, List.nil(), false);
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      this.genLoop(var1, var1.body, var1.cond, List.nil(), true);
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      int var2 = this.code.nextreg;
      this.genStats(var1.init, this.env);
      this.genLoop(var1, var1.body, var1.cond, var1.step, true);
      this.code.endScopes(var2);
   }

   private void genLoop(JCTree.JCStatement var1, JCTree.JCStatement var2, JCTree.JCExpression var3, List var4, boolean var5) {
      Env var6 = this.env.dup(var1, new GenContext());
      int var7 = this.code.entryPoint();
      Items.CondItem var8;
      if (var5) {
         if (var3 != null) {
            this.code.statBegin(var3.pos);
            var8 = this.genCond(TreeInfo.skipParens(var3), 8);
         } else {
            var8 = this.items.makeCondItem(167);
         }

         Code.Chain var9 = var8.jumpFalse();
         this.code.resolve(var8.trueJumps);
         this.genStat(var2, var6, 17);
         this.code.resolve(((GenContext)var6.info).cont);
         this.genStats(var4, var6);
         this.code.resolve(this.code.branch(167), var7);
         this.code.resolve(var9);
      } else {
         this.genStat(var2, var6, 17);
         this.code.resolve(((GenContext)var6.info).cont);
         this.genStats(var4, var6);
         if (var3 != null) {
            this.code.statBegin(var3.pos);
            var8 = this.genCond(TreeInfo.skipParens(var3), 8);
         } else {
            var8 = this.items.makeCondItem(167);
         }

         this.code.resolve(var8.jumpTrue(), var7);
         this.code.resolve(var8.falseJumps);
      }

      this.code.resolve(((GenContext)var6.info).exit);
      if (((GenContext)var6.info).exit != null) {
         ((GenContext)var6.info).exit.state.defined.excludeFrom(this.code.nextreg);
      }

   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      throw new AssertionError();
   }

   public void visitLabelled(JCTree.JCLabeledStatement var1) {
      Env var2 = this.env.dup(var1, new GenContext());
      this.genStat(var1.body, var2, 1);
      this.code.resolve(((GenContext)var2.info).exit);
   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      int var2 = this.code.nextreg;
      Assert.check(!var1.selector.type.hasTag(TypeTag.CLASS));
      int var3 = this.genCrt ? this.code.curCP() : 0;
      Items.Item var4 = this.genExpr(var1.selector, this.syms.intType);
      List var5 = var1.cases;
      if (var5.isEmpty()) {
         var4.load().drop();
         if (this.genCrt) {
            this.code.crt.put(TreeInfo.skipParens(var1.selector), 8, var3, this.code.curCP());
         }
      } else {
         var4.load();
         if (this.genCrt) {
            this.code.crt.put(TreeInfo.skipParens(var1.selector), 8, var3, this.code.curCP());
         }

         Env var6 = this.env.dup(var1, new GenContext());
         ((GenContext)var6.info).isSwitch = true;
         int var7 = Integer.MAX_VALUE;
         int var8 = Integer.MIN_VALUE;
         int var9 = 0;
         int[] var10 = new int[var5.length()];
         int var11 = -1;
         List var12 = var5;

         for(int var13 = 0; var13 < var10.length; ++var13) {
            if (((JCTree.JCCase)var12.head).pat != null) {
               int var14 = ((Number)((JCTree.JCCase)var12.head).pat.type.constValue()).intValue();
               var10[var13] = var14;
               if (var14 < var7) {
                  var7 = var14;
               }

               if (var8 < var14) {
                  var8 = var14;
               }

               ++var9;
            } else {
               Assert.check(var11 == -1);
               var11 = var13;
            }

            var12 = var12.tail;
         }

         long var30 = 4L + (long)var8 - (long)var7 + 1L;
         long var15 = 3L;
         long var17 = 3L + 2L * (long)var9;
         long var19 = (long)var9;
         int var21 = var9 > 0 && var30 + 3L * var15 <= var17 + 3L * var19 ? 170 : 171;
         int var22 = this.code.curCP();
         this.code.emitop0(var21);
         this.code.align(4);
         int var23 = this.code.curCP();
         int[] var24 = null;
         this.code.emit4(-1);
         if (var21 == 170) {
            this.code.emit4(var7);
            this.code.emit4(var8);

            for(long var31 = (long)var7; var31 <= (long)var8; ++var31) {
               this.code.emit4(-1);
            }
         } else {
            this.code.emit4(var9);

            for(int var25 = 0; var25 < var9; ++var25) {
               this.code.emit4(-1);
               this.code.emit4(-1);
            }

            var24 = new int[var10.length];
         }

         Code.State var32 = this.code.state.dup();
         this.code.markDead();
         var12 = var5;

         int var26;
         for(var26 = 0; var26 < var10.length; ++var26) {
            JCTree.JCCase var27 = (JCTree.JCCase)var12.head;
            var12 = var12.tail;
            int var28 = this.code.entryPoint(var32);
            if (var26 != var11) {
               if (var21 == 170) {
                  this.code.put4(var23 + 4 * (var10[var26] - var7 + 3), var28 - var22);
               } else {
                  var24[var26] = var28 - var22;
               }
            } else {
               this.code.put4(var23, var28 - var22);
            }

            this.genStats(var27.stats, var6, 16);
         }

         this.code.resolve(((GenContext)var6.info).exit);
         if (this.code.get4(var23) == -1) {
            this.code.put4(var23, this.code.entryPoint(var32) - var22);
         }

         if (var21 == 170) {
            var26 = this.code.get4(var23);

            for(long var33 = (long)var7; var33 <= (long)var8; ++var33) {
               int var29 = (int)((long)var23 + 4L * (var33 - (long)var7 + 3L));
               if (this.code.get4(var29) == -1) {
                  this.code.put4(var29, var26);
               }
            }
         } else {
            if (var11 >= 0) {
               for(var26 = var11; var26 < var10.length - 1; ++var26) {
                  var10[var26] = var10[var26 + 1];
                  var24[var26] = var24[var26 + 1];
               }
            }

            if (var9 > 0) {
               qsort2(var10, var24, 0, var9 - 1);
            }

            for(var26 = 0; var26 < var9; ++var26) {
               int var34 = var23 + 8 * (var26 + 1);
               this.code.put4(var34, var10[var26]);
               this.code.put4(var34 + 4, var24[var26]);
            }
         }
      }

      this.code.endScopes(var2);
   }

   static void qsort2(int[] var0, int[] var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var3;
      int var6 = var0[(var2 + var3) / 2];

      while(true) {
         while(var0[var4] >= var6) {
            while(var6 < var0[var5]) {
               --var5;
            }

            if (var4 <= var5) {
               int var7 = var0[var4];
               var0[var4] = var0[var5];
               var0[var5] = var7;
               int var8 = var1[var4];
               var1[var4] = var1[var5];
               var1[var5] = var8;
               ++var4;
               --var5;
            }

            if (var4 > var5) {
               if (var2 < var5) {
                  qsort2(var0, var1, var2, var5);
               }

               if (var4 < var3) {
                  qsort2(var0, var1, var4, var3);
               }

               return;
            }
         }

         ++var4;
      }
   }

   public void visitSynchronized(JCTree.JCSynchronized var1) {
      int var2 = this.code.nextreg;
      final Items.LocalItem var3 = this.makeTemp(this.syms.objectType);
      this.genExpr(var1.lock, var1.lock.type).load().duplicate();
      var3.store();
      this.code.emitop0(194);
      this.code.state.lock(var3.reg);
      final Env var4 = this.env.dup(var1, new GenContext());
      ((GenContext)var4.info).finalize = new GenFinalizer() {
         void gen() {
            this.genLast();
            Assert.check(((GenContext)var4.info).gaps.length() % 2 == 0);
            ((GenContext)var4.info).gaps.append(Gen.this.code.curCP());
         }

         void genLast() {
            if (Gen.this.code.isAlive()) {
               var3.load();
               Gen.this.code.emitop0(195);
               Gen.this.code.state.unlock(var3.reg);
            }

         }
      };
      ((GenContext)var4.info).gaps = new ListBuffer();
      this.genTry(var1.body, List.nil(), var4);
      this.code.endScopes(var2);
   }

   public void visitTry(final JCTree.JCTry var1) {
      final Env var2 = this.env.dup(var1, new GenContext());
      final Env var3 = this.env;
      if (!this.useJsrLocally) {
         this.useJsrLocally = this.stackMap == Code.StackMapFormat.NONE && (this.jsrlimit <= 0 || this.jsrlimit < 100 && this.estimateCodeComplexity(var1.finalizer) > this.jsrlimit);
      }

      ((GenContext)var2.info).finalize = new GenFinalizer() {
         void gen() {
            if (Gen.this.useJsrLocally) {
               if (var1.finalizer != null) {
                  Code.State var1x = Gen.this.code.state.dup();
                  var1x.push(Code.jsrReturnValue);
                  ((GenContext)var2.info).cont = new Code.Chain(Gen.this.code.emitJump(168), ((GenContext)var2.info).cont, var1x);
               }

               Assert.check(((GenContext)var2.info).gaps.length() % 2 == 0);
               ((GenContext)var2.info).gaps.append(Gen.this.code.curCP());
            } else {
               Assert.check(((GenContext)var2.info).gaps.length() % 2 == 0);
               ((GenContext)var2.info).gaps.append(Gen.this.code.curCP());
               this.genLast();
            }

         }

         void genLast() {
            if (var1.finalizer != null) {
               Gen.this.genStat(var1.finalizer, var3, 2);
            }

         }

         boolean hasFinalizer() {
            return var1.finalizer != null;
         }
      };
      ((GenContext)var2.info).gaps = new ListBuffer();
      this.genTry(var1.body, var1.catchers, var2);
   }

   void genTry(JCTree var1, List var2, Env var3) {
      int var4 = this.code.nextreg;
      int var5 = this.code.curCP();
      Code.State var6 = this.code.state.dup();
      this.genStat(var1, var3, 2);
      int var7 = this.code.curCP();
      boolean var8 = ((GenContext)var3.info).finalize != null && ((GenContext)var3.info).finalize.hasFinalizer();
      List var9 = ((GenContext)var3.info).gaps.toList();
      this.code.statBegin(TreeInfo.endPos(var1));
      this.genFinalizer(var3);
      this.code.statBegin(TreeInfo.endPos(var3.tree));
      Code.Chain var10 = this.code.branch(167);
      this.endFinalizerGap(var3);
      if (var5 != var7) {
         for(List var11 = var2; var11.nonEmpty(); var11 = var11.tail) {
            this.code.entryPoint(var6, ((JCTree.JCCatch)var11.head).param.sym.type);
            this.genCatch((JCTree.JCCatch)var11.head, var3, var5, var7, var9);
            this.genFinalizer(var3);
            if (var8 || var11.tail.nonEmpty()) {
               this.code.statBegin(TreeInfo.endPos(var3.tree));
               var10 = Code.mergeChains(var10, this.code.branch(167));
            }

            this.endFinalizerGap(var3);
         }
      }

      if (var8) {
         this.code.newRegSegment();
         int var15 = this.code.entryPoint(var6, this.syms.throwableType);

         int var12;
         for(var12 = var5; ((GenContext)var3.info).gaps.nonEmpty(); var12 = (Integer)((GenContext)var3.info).gaps.next()) {
            int var13 = (Integer)((GenContext)var3.info).gaps.next();
            this.registerCatch(var1.pos(), var12, var13, var15, 0);
         }

         this.code.statBegin(TreeInfo.finalizerPos(var3.tree, TreeInfo.PosKind.FIRST_STAT_POS));
         this.code.markStatBegin();
         Items.LocalItem var16 = this.makeTemp(this.syms.throwableType);
         var16.store();
         this.genFinalizer(var3);
         this.code.resolvePending();
         this.code.statBegin(TreeInfo.finalizerPos(var3.tree, TreeInfo.PosKind.END_POS));
         this.code.markStatBegin();
         var16.load();
         this.registerCatch(var1.pos(), var12, (Integer)((GenContext)var3.info).gaps.next(), var15, 0);
         this.code.emitop0(191);
         this.code.markDead();
         if (((GenContext)var3.info).cont != null) {
            this.code.resolve(((GenContext)var3.info).cont);
            this.code.statBegin(TreeInfo.finalizerPos(var3.tree, TreeInfo.PosKind.FIRST_STAT_POS));
            this.code.markStatBegin();
            Items.LocalItem var14 = this.makeTemp(this.syms.throwableType);
            var14.store();
            ((GenContext)var3.info).finalize.genLast();
            this.code.emitop1w(169, var14.reg);
            this.code.markDead();
         }
      }

      this.code.resolve(var10);
      this.code.endScopes(var4);
   }

   void genCatch(JCTree.JCCatch var1, Env var2, int var3, int var4, List var5) {
      if (var3 != var4) {
         List var6;
         Iterator var7;
         JCTree.JCExpression var8;
         int var9;
         label65:
         for(var6 = TreeInfo.isMultiCatch(var1) ? ((JCTree.JCTypeUnion)var1.param.vartype).alternatives : List.of(var1.param.vartype); var5.nonEmpty(); var5 = var5.tail) {
            var7 = var6.iterator();

            while(true) {
               do {
                  if (!var7.hasNext()) {
                     var5 = var5.tail;
                     var3 = (Integer)var5.head;
                     continue label65;
                  }

                  var8 = (JCTree.JCExpression)var7.next();
                  var9 = this.makeRef(var1.pos(), var8.type);
                  int var10 = (Integer)var5.head;
                  this.registerCatch(var1.pos(), var3, var10, this.code.curCP(), var9);
               } while(!var8.type.isAnnotated());

               Attribute.TypeCompound var12;
               for(Iterator var11 = var8.type.getAnnotationMirrors().iterator(); var11.hasNext(); var12.position.type_index = var9) {
                  var12 = (Attribute.TypeCompound)var11.next();
               }
            }
         }

         if (var3 < var4) {
            var7 = var6.iterator();

            label44:
            while(true) {
               do {
                  if (!var7.hasNext()) {
                     break label44;
                  }

                  var8 = (JCTree.JCExpression)var7.next();
                  var9 = this.makeRef(var1.pos(), var8.type);
                  this.registerCatch(var1.pos(), var3, var4, this.code.curCP(), var9);
               } while(!var8.type.isAnnotated());

               Attribute.TypeCompound var16;
               for(Iterator var15 = var8.type.getAnnotationMirrors().iterator(); var15.hasNext(); var16.position.type_index = var9) {
                  var16 = (Attribute.TypeCompound)var15.next();
               }
            }
         }

         Symbol.VarSymbol var13 = var1.param.sym;
         this.code.statBegin(var1.pos);
         this.code.markStatBegin();
         int var14 = this.code.nextreg;
         this.code.newLocal(var13);
         this.items.makeLocalItem(var13).store();
         this.code.statBegin(TreeInfo.firstStatPos(var1.body));
         this.genStat(var1.body, var2, 2);
         this.code.endScopes(var14);
         this.code.statBegin(TreeInfo.endPos(var1.body));
      }

   }

   void registerCatch(JCDiagnostic.DiagnosticPosition var1, int var2, int var3, int var4, int var5) {
      char var6 = (char)var2;
      char var7 = (char)var3;
      char var8 = (char)var4;
      if (var6 == var2 && var7 == var3 && var8 == var4) {
         this.code.addCatch(var6, var7, var8, (char)var5);
      } else {
         if (!this.useJsrLocally && !this.target.generateStackMapTable()) {
            this.useJsrLocally = true;
            throw new CodeSizeOverflow();
         }

         this.log.error(var1, "limit.code.too.large.for.try.stmt", new Object[0]);
         ++this.nerrs;
      }

   }

   int estimateCodeComplexity(JCTree var1) {
      if (var1 == null) {
         return 0;
      } else {
         class ComplexityScanner extends TreeScanner {
            int complexity = 0;

            public void scan(JCTree var1) {
               if (this.complexity <= Gen.this.jsrlimit) {
                  super.scan(var1);
               }
            }

            public void visitClassDef(JCTree.JCClassDecl var1) {
            }

            public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
               super.visitDoLoop(var1);
               ++this.complexity;
            }

            public void visitWhileLoop(JCTree.JCWhileLoop var1) {
               super.visitWhileLoop(var1);
               ++this.complexity;
            }

            public void visitForLoop(JCTree.JCForLoop var1) {
               super.visitForLoop(var1);
               ++this.complexity;
            }

            public void visitSwitch(JCTree.JCSwitch var1) {
               super.visitSwitch(var1);
               this.complexity += 5;
            }

            public void visitCase(JCTree.JCCase var1) {
               super.visitCase(var1);
               ++this.complexity;
            }

            public void visitSynchronized(JCTree.JCSynchronized var1) {
               super.visitSynchronized(var1);
               this.complexity += 6;
            }

            public void visitTry(JCTree.JCTry var1) {
               super.visitTry(var1);
               if (var1.finalizer != null) {
                  this.complexity += 6;
               }

            }

            public void visitCatch(JCTree.JCCatch var1) {
               super.visitCatch(var1);
               this.complexity += 2;
            }

            public void visitConditional(JCTree.JCConditional var1) {
               super.visitConditional(var1);
               this.complexity += 2;
            }

            public void visitIf(JCTree.JCIf var1) {
               super.visitIf(var1);
               this.complexity += 2;
            }

            public void visitBreak(JCTree.JCBreak var1) {
               super.visitBreak(var1);
               ++this.complexity;
            }

            public void visitContinue(JCTree.JCContinue var1) {
               super.visitContinue(var1);
               ++this.complexity;
            }

            public void visitReturn(JCTree.JCReturn var1) {
               super.visitReturn(var1);
               ++this.complexity;
            }

            public void visitThrow(JCTree.JCThrow var1) {
               super.visitThrow(var1);
               ++this.complexity;
            }

            public void visitAssert(JCTree.JCAssert var1) {
               super.visitAssert(var1);
               this.complexity += 5;
            }

            public void visitApply(JCTree.JCMethodInvocation var1) {
               super.visitApply(var1);
               this.complexity += 2;
            }

            public void visitNewClass(JCTree.JCNewClass var1) {
               this.scan(var1.encl);
               this.scan(var1.args);
               this.complexity += 2;
            }

            public void visitNewArray(JCTree.JCNewArray var1) {
               super.visitNewArray(var1);
               this.complexity += 5;
            }

            public void visitAssign(JCTree.JCAssign var1) {
               super.visitAssign(var1);
               ++this.complexity;
            }

            public void visitAssignop(JCTree.JCAssignOp var1) {
               super.visitAssignop(var1);
               this.complexity += 2;
            }

            public void visitUnary(JCTree.JCUnary var1) {
               ++this.complexity;
               if (var1.type.constValue() == null) {
                  super.visitUnary(var1);
               }

            }

            public void visitBinary(JCTree.JCBinary var1) {
               ++this.complexity;
               if (var1.type.constValue() == null) {
                  super.visitBinary(var1);
               }

            }

            public void visitTypeTest(JCTree.JCInstanceOf var1) {
               super.visitTypeTest(var1);
               ++this.complexity;
            }

            public void visitIndexed(JCTree.JCArrayAccess var1) {
               super.visitIndexed(var1);
               ++this.complexity;
            }

            public void visitSelect(JCTree.JCFieldAccess var1) {
               super.visitSelect(var1);
               if (var1.sym.kind == 4) {
                  ++this.complexity;
               }

            }

            public void visitIdent(JCTree.JCIdent var1) {
               if (var1.sym.kind == 4) {
                  ++this.complexity;
                  if (var1.type.constValue() == null && var1.sym.owner.kind == 2) {
                     ++this.complexity;
                  }
               }

            }

            public void visitLiteral(JCTree.JCLiteral var1) {
               ++this.complexity;
            }

            public void visitTree(JCTree var1) {
            }

            public void visitWildcard(JCTree.JCWildcard var1) {
               throw new AssertionError(this.getClass().getName());
            }
         }

         ComplexityScanner var2 = new ComplexityScanner();
         var1.accept(var2);
         return var2.complexity;
      }
   }

   public void visitIf(JCTree.JCIf var1) {
      int var2 = this.code.nextreg;
      Code.Chain var3 = null;
      Items.CondItem var4 = this.genCond(TreeInfo.skipParens(var1.cond), 8);
      Code.Chain var5 = var4.jumpFalse();
      if (!var4.isFalse()) {
         this.code.resolve(var4.trueJumps);
         this.genStat(var1.thenpart, this.env, 17);
         var3 = this.code.branch(167);
      }

      if (var5 != null) {
         this.code.resolve(var5);
         if (var1.elsepart != null) {
            this.genStat(var1.elsepart, this.env, 17);
         }
      }

      this.code.resolve(var3);
      this.code.endScopes(var2);
   }

   public void visitExec(JCTree.JCExpressionStatement var1) {
      JCTree.JCExpression var2 = var1.expr;
      switch (var2.getTag()) {
         case POSTINC:
            ((JCTree.JCUnary)var2).setTag(JCTree.Tag.PREINC);
            break;
         case POSTDEC:
            ((JCTree.JCUnary)var2).setTag(JCTree.Tag.PREDEC);
      }

      this.genExpr(var1.expr, var1.expr.type).drop();
   }

   public void visitBreak(JCTree.JCBreak var1) {
      Env var2 = this.unwind(var1.target, this.env);
      Assert.check(this.code.state.stacksize == 0);
      ((GenContext)var2.info).addExit(this.code.branch(167));
      this.endFinalizerGaps(this.env, var2);
   }

   public void visitContinue(JCTree.JCContinue var1) {
      Env var2 = this.unwind(var1.target, this.env);
      Assert.check(this.code.state.stacksize == 0);
      ((GenContext)var2.info).addCont(this.code.branch(167));
      this.endFinalizerGaps(this.env, var2);
   }

   public void visitReturn(JCTree.JCReturn var1) {
      int var2 = this.code.nextreg;
      int var4 = this.code.pendingStatPos;
      Env var3;
      if (var1.expr != null) {
         Object var5 = this.genExpr(var1.expr, this.pt).load();
         if (this.hasFinally(this.env.enclMethod, this.env)) {
            var5 = this.makeTemp(this.pt);
            ((Items.Item)var5).store();
         }

         var3 = this.unwind(this.env.enclMethod, this.env);
         this.code.pendingStatPos = var4;
         ((Items.Item)var5).load();
         this.code.emitop0(172 + Code.truncate(Code.typecode(this.pt)));
      } else {
         var3 = this.unwind(this.env.enclMethod, this.env);
         this.code.pendingStatPos = var4;
         this.code.emitop0(177);
      }

      this.endFinalizerGaps(this.env, var3);
      this.code.endScopes(var2);
   }

   public void visitThrow(JCTree.JCThrow var1) {
      this.genExpr(var1.expr, var1.expr.type).load();
      this.code.emitop0(191);
   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      this.setTypeAnnotationPositions(var1.pos);
      Items.Item var2 = this.genExpr(var1.meth, this.methodType);
      Symbol.MethodSymbol var3 = (Symbol.MethodSymbol)TreeInfo.symbol(var1.meth);
      this.genArgs(var1.args, var3.externalType(this.types).getParameterTypes());
      if (!var3.isDynamic()) {
         this.code.statBegin(var1.pos);
      }

      this.result = var2.invoke();
   }

   public void visitConditional(JCTree.JCConditional var1) {
      Code.Chain var2 = null;
      Items.CondItem var3 = this.genCond(var1.cond, 8);
      Code.Chain var4 = var3.jumpFalse();
      int var5;
      if (!var3.isFalse()) {
         this.code.resolve(var3.trueJumps);
         var5 = this.genCrt ? this.code.curCP() : 0;
         this.genExpr(var1.truepart, this.pt).load();
         this.code.state.forceStackTop(var1.type);
         if (this.genCrt) {
            this.code.crt.put(var1.truepart, 16, var5, this.code.curCP());
         }

         var2 = this.code.branch(167);
      }

      if (var4 != null) {
         this.code.resolve(var4);
         var5 = this.genCrt ? this.code.curCP() : 0;
         this.genExpr(var1.falsepart, this.pt).load();
         this.code.state.forceStackTop(var1.type);
         if (this.genCrt) {
            this.code.crt.put(var1.falsepart, 16, var5, this.code.curCP());
         }
      }

      this.code.resolve(var2);
      this.result = this.items.makeStackItem(this.pt);
   }

   private void setTypeAnnotationPositions(int var1) {
      Symbol.MethodSymbol var2 = this.code.meth;
      boolean var3 = this.code.meth.getKind() == ElementKind.CONSTRUCTOR || this.code.meth.getKind() == ElementKind.STATIC_INIT;
      Iterator var4 = var2.getRawTypeAttributes().iterator();

      Attribute.TypeCompound var5;
      while(var4.hasNext()) {
         var5 = (Attribute.TypeCompound)var4.next();
         if (var5.hasUnknownPosition()) {
            var5.tryFixPosition();
         }

         if (var5.position.matchesPos(var1)) {
            var5.position.updatePosOffset(this.code.cp);
         }
      }

      if (var3) {
         var4 = var2.owner.getRawTypeAttributes().iterator();

         while(var4.hasNext()) {
            var5 = (Attribute.TypeCompound)var4.next();
            if (var5.hasUnknownPosition()) {
               var5.tryFixPosition();
            }

            if (var5.position.matchesPos(var1)) {
               var5.position.updatePosOffset(this.code.cp);
            }
         }

         Symbol.ClassSymbol var9 = var2.enclClass();
         Iterator var10 = (new FilteredMemberList(var9.members())).iterator();

         while(true) {
            Symbol var6;
            do {
               if (!var10.hasNext()) {
                  return;
               }

               var6 = (Symbol)var10.next();
            } while(!var6.getKind().isField());

            Iterator var7 = var6.getRawTypeAttributes().iterator();

            while(var7.hasNext()) {
               Attribute.TypeCompound var8 = (Attribute.TypeCompound)var7.next();
               if (var8.hasUnknownPosition()) {
                  var8.tryFixPosition();
               }

               if (var8.position.matchesPos(var1)) {
                  var8.position.updatePosOffset(this.code.cp);
               }
            }
         }
      }
   }

   public void visitNewClass(JCTree.JCNewClass var1) {
      Assert.check(var1.encl == null && var1.def == null);
      this.setTypeAnnotationPositions(var1.pos);
      this.code.emitop2(187, this.makeRef(var1.pos(), var1.type));
      this.code.emitop0(89);
      this.genArgs(var1.args, var1.constructor.externalType(this.types).getParameterTypes());
      this.items.makeMemberItem(var1.constructor, true).invoke();
      this.result = this.items.makeStackItem(var1.type);
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      this.setTypeAnnotationPositions(var1.pos);
      if (var1.elems != null) {
         Type var2 = this.types.elemtype(var1.type);
         this.loadIntConst(var1.elems.length());
         Items.Item var3 = this.makeNewArray(var1.pos(), var1.type, 1);
         int var4 = 0;

         for(List var5 = var1.elems; var5.nonEmpty(); var5 = var5.tail) {
            var3.duplicate();
            this.loadIntConst(var4);
            ++var4;
            this.genExpr((JCTree)var5.head, var2).load();
            this.items.makeIndexedItem(var2).store();
         }

         this.result = var3;
      } else {
         for(List var6 = var1.dims; var6.nonEmpty(); var6 = var6.tail) {
            this.genExpr((JCTree)var6.head, this.syms.intType).load();
         }

         this.result = this.makeNewArray(var1.pos(), var1.type, var1.dims.length());
      }

   }

   Items.Item makeNewArray(JCDiagnostic.DiagnosticPosition var1, Type var2, int var3) {
      Type var4 = this.types.elemtype(var2);
      if (this.types.dimensions(var2) > 255) {
         this.log.error(var1, "limit.dimensions", new Object[0]);
         ++this.nerrs;
      }

      int var5 = Code.arraycode(var4);
      if (var5 != 0 && (var5 != 1 || var3 != 1)) {
         if (var5 == 1) {
            this.code.emitMultianewarray(var3, this.makeRef(var1, var2), var2);
         } else {
            this.code.emitNewarray(var5, var2);
         }
      } else {
         this.code.emitAnewarray(this.makeRef(var1, var4), var2);
      }

      return this.items.makeStackItem(var2);
   }

   public void visitParens(JCTree.JCParens var1) {
      this.result = this.genExpr(var1.expr, var1.expr.type);
   }

   public void visitAssign(JCTree.JCAssign var1) {
      Items.Item var2 = this.genExpr(var1.lhs, var1.lhs.type);
      this.genExpr(var1.rhs, var1.lhs.type).load();
      this.result = this.items.makeAssignItem(var2);
   }

   public void visitAssignop(JCTree.JCAssignOp var1) {
      Symbol.OperatorSymbol var2 = (Symbol.OperatorSymbol)var1.operator;
      Items.Item var3;
      if (var2.opcode == 256) {
         this.makeStringBuffer(var1.pos());
         var3 = this.genExpr(var1.lhs, var1.lhs.type);
         if (var3.width() > 0) {
            this.code.emitop0(90 + 3 * (var3.width() - 1));
         }

         var3.load();
         this.appendString(var1.lhs);
         this.appendStrings(var1.rhs);
         this.bufferToString(var1.pos());
      } else {
         var3 = this.genExpr(var1.lhs, var1.lhs.type);
         if ((var1.hasTag(JCTree.Tag.PLUS_ASG) || var1.hasTag(JCTree.Tag.MINUS_ASG)) && var3 instanceof Items.LocalItem && var1.lhs.type.getTag().isSubRangeOf(TypeTag.INT) && var1.rhs.type.getTag().isSubRangeOf(TypeTag.INT) && var1.rhs.type.constValue() != null) {
            int var4 = ((Number)var1.rhs.type.constValue()).intValue();
            if (var1.hasTag(JCTree.Tag.MINUS_ASG)) {
               var4 = -var4;
            }

            ((Items.LocalItem)var3).incr(var4);
            this.result = var3;
            return;
         }

         var3.duplicate();
         var3.coerce((Type)var2.type.getParameterTypes().head).load();
         this.completeBinop(var1.lhs, var1.rhs, var2).coerce(var1.lhs.type);
      }

      this.result = this.items.makeAssignItem(var3);
   }

   public void visitUnary(JCTree.JCUnary var1) {
      Symbol.OperatorSymbol var2 = (Symbol.OperatorSymbol)var1.operator;
      if (var1.hasTag(JCTree.Tag.NOT)) {
         Items.CondItem var3 = this.genCond(var1.arg, false);
         this.result = var3.negate();
      } else {
         Items.Item var5 = this.genExpr(var1.arg, (Type)var2.type.getParameterTypes().head);
         switch (var1.getTag()) {
            case POSTINC:
            case POSTDEC:
               var5.duplicate();
               Items.Item var4;
               if (var5 instanceof Items.LocalItem && (var2.opcode == 96 || var2.opcode == 100)) {
                  var4 = var5.load();
                  ((Items.LocalItem)var5).incr(var1.hasTag(JCTree.Tag.POSTINC) ? 1 : -1);
                  this.result = var4;
               } else {
                  var4 = var5.load();
                  var5.stash(var5.typecode);
                  this.code.emitop0(one(var5.typecode));
                  this.code.emitop0(var2.opcode);
                  if (var5.typecode != 0 && Code.truncate(var5.typecode) == 0) {
                     this.code.emitop0(145 + var5.typecode - 5);
                  }

                  var5.store();
                  this.result = var4;
               }
               break;
            case POS:
               this.result = var5.load();
               break;
            case NEG:
               this.result = var5.load();
               this.code.emitop0(var2.opcode);
               break;
            case COMPL:
               this.result = var5.load();
               this.emitMinusOne(var5.typecode);
               this.code.emitop0(var2.opcode);
               break;
            case PREINC:
            case PREDEC:
               var5.duplicate();
               if (var5 instanceof Items.LocalItem && (var2.opcode == 96 || var2.opcode == 100)) {
                  ((Items.LocalItem)var5).incr(var1.hasTag(JCTree.Tag.PREINC) ? 1 : -1);
                  this.result = var5;
               } else {
                  var5.load();
                  this.code.emitop0(one(var5.typecode));
                  this.code.emitop0(var2.opcode);
                  if (var5.typecode != 0 && Code.truncate(var5.typecode) == 0) {
                     this.code.emitop0(145 + var5.typecode - 5);
                  }

                  this.result = this.items.makeAssignItem(var5);
               }
               break;
            case NULLCHK:
               this.result = var5.load();
               this.code.emitop0(89);
               this.genNullCheck(var1.pos());
               break;
            default:
               Assert.error();
         }
      }

   }

   private void genNullCheck(JCDiagnostic.DiagnosticPosition var1) {
      this.callMethod(var1, this.syms.objectType, this.names.getClass, List.nil(), false);
      this.code.emitop0(87);
   }

   public void visitBinary(JCTree.JCBinary var1) {
      Symbol.OperatorSymbol var2 = (Symbol.OperatorSymbol)var1.operator;
      if (var2.opcode == 256) {
         this.makeStringBuffer(var1.pos());
         this.appendStrings(var1);
         this.bufferToString(var1.pos());
         this.result = this.items.makeStackItem(this.syms.stringType);
      } else {
         Items.CondItem var3;
         Code.Chain var4;
         Items.CondItem var5;
         if (var1.hasTag(JCTree.Tag.AND)) {
            var3 = this.genCond(var1.lhs, 8);
            if (!var3.isFalse()) {
               var4 = var3.jumpFalse();
               this.code.resolve(var3.trueJumps);
               var5 = this.genCond(var1.rhs, 16);
               this.result = this.items.makeCondItem(var5.opcode, var5.trueJumps, Code.mergeChains(var4, var5.falseJumps));
            } else {
               this.result = var3;
            }
         } else if (var1.hasTag(JCTree.Tag.OR)) {
            var3 = this.genCond(var1.lhs, 8);
            if (!var3.isTrue()) {
               var4 = var3.jumpTrue();
               this.code.resolve(var3.falseJumps);
               var5 = this.genCond(var1.rhs, 16);
               this.result = this.items.makeCondItem(var5.opcode, Code.mergeChains(var4, var5.trueJumps), var5.falseJumps);
            } else {
               this.result = var3;
            }
         } else {
            Items.Item var6 = this.genExpr(var1.lhs, (Type)var2.type.getParameterTypes().head);
            var6.load();
            this.result = this.completeBinop(var1.lhs, var1.rhs, var2);
         }
      }

   }

   void makeStringBuffer(JCDiagnostic.DiagnosticPosition var1) {
      this.code.emitop2(187, this.makeRef(var1, this.stringBufferType));
      this.code.emitop0(89);
      this.callMethod(var1, this.stringBufferType, this.names.init, List.nil(), false);
   }

   void appendString(JCTree var1) {
      Type var2 = var1.type.baseType();
      if (!var2.isPrimitive() && var2.tsym != this.syms.stringType.tsym) {
         var2 = this.syms.objectType;
      }

      this.items.makeMemberItem(this.getStringBufferAppend(var1, var2), false).invoke();
   }

   Symbol getStringBufferAppend(JCTree var1, Type var2) {
      Assert.checkNull(var2.constValue());
      Object var3 = (Symbol)this.stringBufferAppend.get(var2);
      if (var3 == null) {
         var3 = this.rs.resolveInternalMethod(var1.pos(), this.attrEnv, this.stringBufferType, this.names.append, List.of(var2), (List)null);
         this.stringBufferAppend.put(var2, var3);
      }

      return (Symbol)var3;
   }

   void appendStrings(JCTree var1) {
      var1 = TreeInfo.skipParens(var1);
      if (var1.hasTag(JCTree.Tag.PLUS) && var1.type.constValue() == null) {
         JCTree.JCBinary var2 = (JCTree.JCBinary)var1;
         if (var2.operator.kind == 16 && ((Symbol.OperatorSymbol)var2.operator).opcode == 256) {
            this.appendStrings(var2.lhs);
            this.appendStrings(var2.rhs);
            return;
         }
      }

      this.genExpr(var1, var1.type).load();
      this.appendString(var1);
   }

   void bufferToString(JCDiagnostic.DiagnosticPosition var1) {
      this.callMethod(var1, this.stringBufferType, this.names.toString, List.nil(), false);
   }

   Items.Item completeBinop(JCTree var1, JCTree var2, Symbol.OperatorSymbol var3) {
      Type.MethodType var4 = (Type.MethodType)var3.type;
      int var5 = var3.opcode;
      if (var5 >= 159 && var5 <= 164 && var2.type.constValue() instanceof Number && ((Number)var2.type.constValue()).intValue() == 0) {
         var5 += -6;
      } else if (var5 >= 165 && var5 <= 166 && TreeInfo.isNull(var2)) {
         var5 += 33;
      } else {
         Object var6 = (Type)var3.erasure(this.types).getParameterTypes().tail.head;
         if (var5 >= 270 && var5 <= 275) {
            var5 += -150;
            var6 = this.syms.intType;
         }

         this.genExpr(var2, (Type)var6).load();
         if (var5 >= 512) {
            this.code.emitop0(var5 >> 9);
            var5 &= 255;
         }
      }

      if ((var5 < 153 || var5 > 166) && var5 != 198 && var5 != 199) {
         this.code.emitop0(var5);
         return this.items.makeStackItem(var4.restype);
      } else {
         return this.items.makeCondItem(var5);
      }
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      this.setTypeAnnotationPositions(var1.pos);
      this.result = this.genExpr(var1.expr, var1.clazz.type).load();
      if (!var1.clazz.type.isPrimitive() && this.types.asSuper(var1.expr.type, var1.clazz.type.tsym) == null) {
         this.code.emitop2(192, this.makeRef(var1.pos(), var1.clazz.type));
      }

   }

   public void visitWildcard(JCTree.JCWildcard var1) {
      throw new AssertionError(this.getClass().getName());
   }

   public void visitTypeTest(JCTree.JCInstanceOf var1) {
      this.setTypeAnnotationPositions(var1.pos);
      this.genExpr(var1.expr, var1.expr.type).load();
      this.code.emitop2(193, this.makeRef(var1.pos(), var1.clazz.type));
      this.result = this.items.makeStackItem(this.syms.booleanType);
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      this.genExpr(var1.indexed, var1.indexed.type).load();
      this.genExpr(var1.index, this.syms.intType).load();
      this.result = this.items.makeIndexedItem(var1.type);
   }

   public void visitIdent(JCTree.JCIdent var1) {
      Symbol var2 = var1.sym;
      if (var1.name != this.names._this && var1.name != this.names._super) {
         if (var2.kind == 4 && var2.owner.kind == 16) {
            this.result = this.items.makeLocalItem((Symbol.VarSymbol)var2);
         } else if (this.isInvokeDynamic(var2)) {
            this.result = this.items.makeDynamicItem(var2);
         } else if ((var2.flags() & 8L) != 0L) {
            if (!this.isAccessSuper(this.env.enclMethod)) {
               var2 = this.binaryQualifier(var2, this.env.enclClass.type);
            }

            this.result = this.items.makeStaticItem(var2);
         } else {
            this.items.makeThisItem().load();
            var2 = this.binaryQualifier(var2, this.env.enclClass.type);
            this.result = this.items.makeMemberItem(var2, (var2.flags() & 2L) != 0L);
         }
      } else {
         Items.Item var3 = var1.name == this.names._this ? this.items.makeThisItem() : this.items.makeSuperItem();
         if (var2.kind == 16) {
            var3.load();
            var3 = this.items.makeMemberItem(var2, true);
         }

         this.result = var3;
      }

   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      Symbol var2 = var1.sym;
      if (var1.name == this.names._class) {
         Assert.check(this.target.hasClassLiterals());
         this.code.emitLdc(this.makeRef(var1.pos(), var1.selected.type));
         this.result = this.items.makeStackItem(this.pt);
      } else {
         Symbol var3 = TreeInfo.symbol(var1.selected);
         boolean var4 = var3 != null && (var3.kind == 2 || var3.name == this.names._super);
         boolean var5 = this.isAccessSuper(this.env.enclMethod);
         Items.Item var6 = var4 ? this.items.makeSuperItem() : this.genExpr(var1.selected, var1.selected.type);
         if (var2.kind == 4 && ((Symbol.VarSymbol)var2).getConstValue() != null) {
            if ((var2.flags() & 8L) == 0L) {
               var6.load();
               this.genNullCheck(var1.selected.pos());
            } else {
               if (!var4 && (var3 == null || var3.kind != 2)) {
                  var6 = var6.load();
               }

               var6.drop();
            }

            this.result = this.items.makeImmediateItem(var2.type, ((Symbol.VarSymbol)var2).getConstValue());
         } else {
            if (this.isInvokeDynamic(var2)) {
               this.result = this.items.makeDynamicItem(var2);
               return;
            }

            var2 = this.binaryQualifier(var2, var1.selected.type);
            if ((var2.flags() & 8L) != 0L) {
               if (!var4 && (var3 == null || var3.kind != 2)) {
                  var6 = var6.load();
               }

               var6.drop();
               this.result = this.items.makeStaticItem(var2);
            } else {
               var6.load();
               if (var2 == this.syms.lengthVar) {
                  this.code.emitop0(190);
                  this.result = this.items.makeStackItem(this.syms.intType);
               } else {
                  this.result = this.items.makeMemberItem(var2, (var2.flags() & 2L) != 0L || var4 || var5);
               }
            }
         }

      }
   }

   public boolean isInvokeDynamic(Symbol var1) {
      return var1.kind == 16 && ((Symbol.MethodSymbol)var1).isDynamic();
   }

   public void visitLiteral(JCTree.JCLiteral var1) {
      if (var1.type.hasTag(TypeTag.BOT)) {
         this.code.emitop0(1);
         if (this.types.dimensions(this.pt) > 1) {
            this.code.emitop2(192, this.makeRef(var1.pos(), this.pt));
            this.result = this.items.makeStackItem(this.pt);
         } else {
            this.result = this.items.makeStackItem(var1.type);
         }
      } else {
         this.result = this.items.makeImmediateItem(var1.type, var1.value);
      }

   }

   public void visitLetExpr(JCTree.LetExpr var1) {
      int var2 = this.code.nextreg;
      this.genStats(var1.defs, this.env);
      this.result = this.genExpr(var1.expr, var1.expr.type).load();
      this.code.endScopes(var2);
   }

   private void generateReferencesToPrunedTree(Symbol.ClassSymbol var1, Pool var2) {
      List var3 = (List)this.lower.prunedTree.get(var1);
      if (var3 != null) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            JCTree var5 = (JCTree)var4.next();
            var5.accept(this.classReferenceVisitor);
         }
      }

   }

   public boolean genClass(Env var1, JCTree.JCClassDecl var2) {
      boolean var9;
      try {
         this.attrEnv = var1;
         Symbol.ClassSymbol var3 = var2.sym;
         this.toplevel = var1.toplevel;
         this.endPosTable = this.toplevel.endPositions;
         if (this.generateIproxies && (var3.flags() & 1536L) == 1024L && !this.allowGenerics) {
            this.implementInterfaceMethods(var3);
         }

         var3.pool = this.pool;
         this.pool.reset();
         var2.defs = this.normalizeDefs(var2.defs, var3);
         this.generateReferencesToPrunedTree(var3, this.pool);
         Env var4 = new Env(var2, new GenContext());
         var4.toplevel = var1.toplevel;
         var4.enclClass = var2;

         List var5;
         for(var5 = var2.defs; var5.nonEmpty(); var5 = var5.tail) {
            this.genDef((JCTree)var5.head, var4);
         }

         if (this.pool.numEntries() > 65535) {
            this.log.error(var2.pos(), "limit.pool", new Object[0]);
            ++this.nerrs;
         }

         if (this.nerrs != 0) {
            for(var5 = var2.defs; var5.nonEmpty(); var5 = var5.tail) {
               if (((JCTree)var5.head).hasTag(JCTree.Tag.METHODDEF)) {
                  ((JCTree.JCMethodDecl)var5.head).sym.code = null;
               }
            }
         }

         var2.defs = List.nil();
         var9 = this.nerrs == 0;
      } finally {
         this.attrEnv = null;
         this.env = null;
         this.toplevel = null;
         this.endPosTable = null;
         this.nerrs = 0;
      }

      return var9;
   }

   static class GenContext {
      Code.Chain exit = null;
      Code.Chain cont = null;
      GenFinalizer finalize = null;
      boolean isSwitch = false;
      ListBuffer gaps = null;

      void addExit(Code.Chain var1) {
         this.exit = Code.mergeChains(var1, this.exit);
      }

      void addCont(Code.Chain var1) {
         this.cont = Code.mergeChains(var1, this.cont);
      }
   }

   abstract class GenFinalizer {
      abstract void gen();

      abstract void genLast();

      boolean hasFinalizer() {
         return true;
      }
   }

   public static class CodeSizeOverflow extends RuntimeException {
      private static final long serialVersionUID = 0L;
   }

   class ClassReferenceVisitor extends JCTree.Visitor {
      public void visitTree(JCTree var1) {
      }

      public void visitBinary(JCTree.JCBinary var1) {
         var1.lhs.accept(this);
         var1.rhs.accept(this);
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         if (var1.selected.type.hasTag(TypeTag.CLASS)) {
            Gen.this.makeRef(var1.selected.pos(), var1.selected.type);
         }

      }

      public void visitIdent(JCTree.JCIdent var1) {
         if (var1.sym.owner instanceof Symbol.ClassSymbol) {
            Gen.this.pool.put(var1.sym.owner);
         }

      }

      public void visitConditional(JCTree.JCConditional var1) {
         var1.cond.accept(this);
         var1.truepart.accept(this);
         var1.falsepart.accept(this);
      }

      public void visitUnary(JCTree.JCUnary var1) {
         var1.arg.accept(this);
      }

      public void visitParens(JCTree.JCParens var1) {
         var1.expr.accept(this);
      }

      public void visitTypeCast(JCTree.JCTypeCast var1) {
         var1.expr.accept(this);
      }
   }
}
