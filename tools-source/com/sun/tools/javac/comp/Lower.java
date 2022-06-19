package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.jvm.ClassWriter;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Convert;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class Lower extends TreeTranslator {
   protected static final Context.Key lowerKey = new Context.Key();
   private Names names;
   private Log log;
   private Symtab syms;
   private Resolve rs;
   private Check chk;
   private Attr attr;
   private TreeMaker make;
   private JCDiagnostic.DiagnosticPosition make_pos;
   private ClassWriter writer;
   private ClassReader reader;
   private ConstFold cfolder;
   private Target target;
   private Source source;
   private final TypeEnvs typeEnvs;
   private boolean allowEnums;
   private final Name dollarAssertionsDisabled;
   private final Name classDollar;
   private Types types;
   private boolean debugLower;
   private Option.PkgInfo pkginfoOpt;
   Symbol.ClassSymbol currentClass;
   ListBuffer translated;
   Env attrEnv;
   EndPosTable endPosTable;
   Map classdefs;
   public Map prunedTree = new WeakHashMap();
   Map actualSymbols;
   JCTree.JCMethodDecl currentMethodDef;
   Symbol.MethodSymbol currentMethodSym;
   JCTree.JCClassDecl outermostClassDef;
   JCTree outermostMemberDef;
   Map lambdaTranslationMap = null;
   ClassMap classMap = new ClassMap();
   Map freevarCache;
   Map enumSwitchMap = new LinkedHashMap();
   JCTree.Visitor conflictsChecker = new TreeScanner() {
      Symbol.TypeSymbol currentClass;

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         Lower.this.chk.checkConflicts(var1.pos(), var1.sym, this.currentClass);
         super.visitMethodDef(var1);
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         if (var1.sym.owner.kind == 2) {
            Lower.this.chk.checkConflicts(var1.pos(), var1.sym, this.currentClass);
         }

         super.visitVarDef(var1);
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         Symbol.TypeSymbol var2 = this.currentClass;
         this.currentClass = var1.sym;

         try {
            super.visitClassDef(var1);
         } finally {
            this.currentClass = var2;
         }

      }
   };
   private static final int DEREFcode = 0;
   private static final int ASSIGNcode = 2;
   private static final int PREINCcode = 4;
   private static final int PREDECcode = 6;
   private static final int POSTINCcode = 8;
   private static final int POSTDECcode = 10;
   private static final int FIRSTASGOPcode = 12;
   private static final int NCODES = accessCode(275) + 2;
   private Map accessNums;
   private Map accessSyms;
   private Map accessConstrs;
   private List accessConstrTags;
   private ListBuffer accessed;
   Scope proxies;
   Scope twrVars;
   List outerThisStack;
   private Symbol.ClassSymbol assertionsDisabledClassCache;
   private JCTree.JCExpression enclOp;
   private Symbol.MethodSymbol systemArraycopyMethod;

   public static Lower instance(Context var0) {
      Lower var1 = (Lower)var0.get(lowerKey);
      if (var1 == null) {
         var1 = new Lower(var0);
      }

      return var1;
   }

   protected Lower(Context var1) {
      var1.put((Context.Key)lowerKey, (Object)this);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.rs = Resolve.instance(var1);
      this.chk = Check.instance(var1);
      this.attr = Attr.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.writer = ClassWriter.instance(var1);
      this.reader = ClassReader.instance(var1);
      this.cfolder = ConstFold.instance(var1);
      this.target = Target.instance(var1);
      this.source = Source.instance(var1);
      this.typeEnvs = TypeEnvs.instance(var1);
      this.allowEnums = this.source.allowEnums();
      this.dollarAssertionsDisabled = this.names.fromString(this.target.syntheticNameChar() + "assertionsDisabled");
      this.classDollar = this.names.fromString("class" + this.target.syntheticNameChar());
      this.types = Types.instance(var1);
      Options var2 = Options.instance(var1);
      this.debugLower = var2.isSet("debuglower");
      this.pkginfoOpt = Option.PkgInfo.get(var2);
   }

   JCTree.JCClassDecl classDef(Symbol.ClassSymbol var1) {
      JCTree.JCClassDecl var2 = (JCTree.JCClassDecl)this.classdefs.get(var1);
      if (var2 == null && this.outermostMemberDef != null) {
         this.classMap.scan(this.outermostMemberDef);
         var2 = (JCTree.JCClassDecl)this.classdefs.get(var1);
      }

      if (var2 == null) {
         this.classMap.scan(this.outermostClassDef);
         var2 = (JCTree.JCClassDecl)this.classdefs.get(var1);
      }

      return var2;
   }

   Symbol.ClassSymbol ownerToCopyFreeVarsFrom(Symbol.ClassSymbol var1) {
      if (!var1.isLocal()) {
         return null;
      } else {
         Symbol var2;
         for(var2 = var1.owner; (var2.owner.kind & 2) != 0 && var2.isLocal(); var2 = var2.owner) {
         }

         return (var2.owner.kind & 20) != 0 && var1.isSubClass(var2, this.types) ? (Symbol.ClassSymbol)var2 : null;
      }
   }

   List freevars(Symbol.ClassSymbol var1) {
      List var2 = (List)this.freevarCache.get(var1);
      if (var2 != null) {
         return var2;
      } else if ((var1.owner.kind & 20) != 0) {
         FreeVarCollector var4 = new FreeVarCollector(var1);
         var4.scan(this.classDef(var1));
         var2 = var4.fvs;
         this.freevarCache.put(var1, var2);
         return var2;
      } else {
         Symbol.ClassSymbol var3 = this.ownerToCopyFreeVarsFrom(var1);
         if (var3 != null) {
            var2 = (List)this.freevarCache.get(var3);
            this.freevarCache.put(var1, var2);
            return var2;
         } else {
            return List.nil();
         }
      }
   }

   EnumMapping mapForEnum(JCDiagnostic.DiagnosticPosition var1, Symbol.TypeSymbol var2) {
      EnumMapping var3 = (EnumMapping)this.enumSwitchMap.get(var2);
      if (var3 == null) {
         this.enumSwitchMap.put(var2, var3 = new EnumMapping(var1, var2));
      }

      return var3;
   }

   TreeMaker make_at(JCDiagnostic.DiagnosticPosition var1) {
      this.make_pos = var1;
      return this.make.at(var1);
   }

   JCTree.JCExpression makeLit(Type var1, Object var2) {
      return this.make.Literal(var1.getTag(), var2).setType(var1.constType(var2));
   }

   JCTree.JCExpression makeNull() {
      return this.makeLit(this.syms.botType, (Object)null);
   }

   JCTree.JCNewClass makeNewClass(Type var1, List var2) {
      JCTree.JCNewClass var3 = this.make.NewClass((JCTree.JCExpression)null, (List)null, this.make.QualIdent(var1.tsym), var2, (JCTree.JCClassDecl)null);
      var3.constructor = this.rs.resolveConstructor(this.make_pos, this.attrEnv, var1, TreeInfo.types(var2), List.nil());
      var3.type = var1;
      return var3;
   }

   JCTree.JCUnary makeUnary(JCTree.Tag var1, JCTree.JCExpression var2) {
      JCTree.JCUnary var3 = this.make.Unary(var1, var2);
      var3.operator = this.rs.resolveUnaryOperator(this.make_pos, var1, this.attrEnv, var2.type);
      var3.type = var3.operator.type.getReturnType();
      return var3;
   }

   JCTree.JCBinary makeBinary(JCTree.Tag var1, JCTree.JCExpression var2, JCTree.JCExpression var3) {
      JCTree.JCBinary var4 = this.make.Binary(var1, var2, var3);
      var4.operator = this.rs.resolveBinaryOperator(this.make_pos, var1, this.attrEnv, var2.type, var3.type);
      var4.type = var4.operator.type.getReturnType();
      return var4;
   }

   JCTree.JCAssignOp makeAssignop(JCTree.Tag var1, JCTree var2, JCTree var3) {
      JCTree.JCAssignOp var4 = this.make.Assignop(var1, var2, var3);
      var4.operator = this.rs.resolveBinaryOperator(this.make_pos, var4.getTag().noAssignOp(), this.attrEnv, var2.type, var3.type);
      var4.type = var2.type;
      return var4;
   }

   JCTree.JCExpression makeString(JCTree.JCExpression var1) {
      if (!var1.type.isPrimitiveOrVoid()) {
         return var1;
      } else {
         Symbol.MethodSymbol var2 = this.lookupMethod(var1.pos(), this.names.valueOf, this.syms.stringType, List.of(var1.type));
         return this.make.App(this.make.QualIdent(var2), List.of(var1));
      }
   }

   JCTree.JCClassDecl makeEmptyClass(long var1, Symbol.ClassSymbol var3) {
      return this.makeEmptyClass(var1, var3, (Name)null, true);
   }

   JCTree.JCClassDecl makeEmptyClass(long var1, Symbol.ClassSymbol var3, Name var4, boolean var5) {
      Symbol.ClassSymbol var6 = this.reader.defineClass(this.names.empty, var3);
      if (var4 != null) {
         var6.flatname = var4;
      } else {
         var6.flatname = this.chk.localClassName(var6);
      }

      var6.sourcefile = var3.sourcefile;
      var6.completer = null;
      var6.members_field = new Scope(var6);
      var6.flags_field = var1;
      Type.ClassType var7 = (Type.ClassType)var6.type;
      var7.supertype_field = this.syms.objectType;
      var7.interfaces_field = List.nil();
      JCTree.JCClassDecl var8 = this.classDef(var3);
      this.enterSynthetic(var8.pos(), var6, var3.members());
      this.chk.compiled.put(var6.flatname, var6);
      JCTree.JCClassDecl var9 = this.make.ClassDef(this.make.Modifiers(var1), this.names.empty, List.nil(), (JCTree.JCExpression)null, List.nil(), List.nil());
      var9.sym = var6;
      var9.type = var6.type;
      if (var5) {
         var8.defs = var8.defs.prepend(var9);
      }

      return var9;
   }

   private void enterSynthetic(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Scope var3) {
      var3.enter(var2);
   }

   private Name makeSyntheticName(Name var1, Scope var2) {
      do {
         var1 = var1.append(this.target.syntheticNameChar(), this.names.empty);
      } while(this.lookupSynthetic(var1, var2) != null);

      return var1;
   }

   void checkConflicts(List var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         JCTree var3 = (JCTree)var2.next();
         var3.accept(this.conflictsChecker);
      }

   }

   private Symbol lookupSynthetic(Name var1, Scope var2) {
      Symbol var3 = var2.lookup(var1).sym;
      return var3 != null && (var3.flags() & 4096L) != 0L ? var3 : null;
   }

   private Symbol.MethodSymbol lookupMethod(JCDiagnostic.DiagnosticPosition var1, Name var2, Type var3, List var4) {
      return this.rs.resolveInternalMethod(var1, this.attrEnv, var3, var2, var4, List.nil());
   }

   private Symbol.MethodSymbol lookupConstructor(JCDiagnostic.DiagnosticPosition var1, Type var2, List var3) {
      return this.rs.resolveInternalConstructor(var1, this.attrEnv, var2, var3, (List)null);
   }

   private Symbol.VarSymbol lookupField(JCDiagnostic.DiagnosticPosition var1, Type var2, Name var3) {
      return this.rs.resolveInternalField(var1, this.attrEnv, var2, var3);
   }

   private void checkAccessConstructorTags() {
      for(List var1 = this.accessConstrTags; var1.nonEmpty(); var1 = var1.tail) {
         Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1.head;
         if (!this.isTranslatedClassAvailable(var2)) {
            JCTree.JCClassDecl var3 = this.makeEmptyClass(4104L, var2.outermostClass(), var2.flatname, false);
            this.swapAccessConstructorTag(var2, var3.sym);
            this.translated.append(var3);
         }
      }

   }

   private boolean isTranslatedClassAvailable(Symbol.ClassSymbol var1) {
      Iterator var2 = this.translated.iterator();

      JCTree var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (JCTree)var2.next();
      } while(!var3.hasTag(JCTree.Tag.CLASSDEF) || ((JCTree.JCClassDecl)var3).sym != var1);

      return true;
   }

   void swapAccessConstructorTag(Symbol.ClassSymbol var1, Symbol.ClassSymbol var2) {
      Iterator var3 = this.accessConstrs.values().iterator();

      while(var3.hasNext()) {
         Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)var3.next();
         Assert.check(var4.type.hasTag(TypeTag.METHOD));
         Type.MethodType var5 = (Type.MethodType)var4.type;
         if (((Type)var5.argtypes.head).tsym == var1) {
            var4.type = this.types.createMethodTypeWithParameters(var5, var5.getParameterTypes().tail.prepend(var2.erasure(this.types)));
         }
      }

   }

   private static int accessCode(int var0) {
      if (96 <= var0 && var0 <= 131) {
         return (var0 - 96) * 2 + 12;
      } else if (var0 == 256) {
         return 84;
      } else {
         return 270 <= var0 && var0 <= 275 ? (var0 - 270 + 131 + 2 - 96) * 2 + 12 : -1;
      }
   }

   private static int accessCode(JCTree var0, JCTree var1) {
      if (var1 == null) {
         return 0;
      } else if (var1.hasTag(JCTree.Tag.ASSIGN) && var0 == TreeInfo.skipParens(((JCTree.JCAssign)var1).lhs)) {
         return 2;
      } else if (var1.getTag().isIncOrDecUnaryOp() && var0 == TreeInfo.skipParens(((JCTree.JCUnary)var1).arg)) {
         return mapTagToUnaryOpCode(var1.getTag());
      } else {
         return var1.getTag().isAssignop() && var0 == TreeInfo.skipParens(((JCTree.JCAssignOp)var1).lhs) ? accessCode(((Symbol.OperatorSymbol)((JCTree.JCAssignOp)var1).operator).opcode) : 0;
      }
   }

   private Symbol.OperatorSymbol binaryAccessOperator(int var1) {
      for(Scope.Entry var2 = this.syms.predefClass.members().elems; var2 != null; var2 = var2.sibling) {
         if (var2.sym instanceof Symbol.OperatorSymbol) {
            Symbol.OperatorSymbol var3 = (Symbol.OperatorSymbol)var2.sym;
            if (accessCode(var3.opcode) == var1) {
               return var3;
            }
         }
      }

      return null;
   }

   private static JCTree.Tag treeTag(Symbol.OperatorSymbol var0) {
      switch (var0.opcode) {
         case 96:
         case 97:
         case 98:
         case 99:
         case 256:
            return JCTree.Tag.PLUS_ASG;
         case 100:
         case 101:
         case 102:
         case 103:
            return JCTree.Tag.MINUS_ASG;
         case 104:
         case 105:
         case 106:
         case 107:
            return JCTree.Tag.MUL_ASG;
         case 108:
         case 109:
         case 110:
         case 111:
            return JCTree.Tag.DIV_ASG;
         case 112:
         case 113:
         case 114:
         case 115:
            return JCTree.Tag.MOD_ASG;
         case 116:
         case 117:
         case 118:
         case 119:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 167:
         case 168:
         case 169:
         case 170:
         case 171:
         case 172:
         case 173:
         case 174:
         case 175:
         case 176:
         case 177:
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 183:
         case 184:
         case 185:
         case 186:
         case 187:
         case 188:
         case 189:
         case 190:
         case 191:
         case 192:
         case 193:
         case 194:
         case 195:
         case 196:
         case 197:
         case 198:
         case 199:
         case 200:
         case 201:
         case 202:
         case 203:
         case 204:
         case 205:
         case 206:
         case 207:
         case 208:
         case 209:
         case 210:
         case 211:
         case 212:
         case 213:
         case 214:
         case 215:
         case 216:
         case 217:
         case 218:
         case 219:
         case 220:
         case 221:
         case 222:
         case 223:
         case 224:
         case 225:
         case 226:
         case 227:
         case 228:
         case 229:
         case 230:
         case 231:
         case 232:
         case 233:
         case 234:
         case 235:
         case 236:
         case 237:
         case 238:
         case 239:
         case 240:
         case 241:
         case 242:
         case 243:
         case 244:
         case 245:
         case 246:
         case 247:
         case 248:
         case 249:
         case 250:
         case 251:
         case 252:
         case 253:
         case 254:
         case 255:
         case 257:
         case 258:
         case 259:
         case 260:
         case 261:
         case 262:
         case 263:
         case 264:
         case 265:
         case 266:
         case 267:
         case 268:
         case 269:
         default:
            throw new AssertionError();
         case 120:
         case 121:
         case 270:
         case 271:
            return JCTree.Tag.SL_ASG;
         case 122:
         case 123:
         case 272:
         case 273:
            return JCTree.Tag.SR_ASG;
         case 124:
         case 125:
         case 274:
         case 275:
            return JCTree.Tag.USR_ASG;
         case 126:
         case 127:
            return JCTree.Tag.BITAND_ASG;
         case 128:
         case 129:
            return JCTree.Tag.BITOR_ASG;
         case 130:
         case 131:
            return JCTree.Tag.BITXOR_ASG;
      }
   }

   Name accessName(int var1, int var2) {
      return this.names.fromString("access" + this.target.syntheticNameChar() + var1 + var2 / 10 + var2 % 10);
   }

   Symbol.MethodSymbol accessSymbol(Symbol var1, JCTree var2, JCTree var3, boolean var4, boolean var5) {
      Symbol.ClassSymbol var6 = var5 && var4 ? (Symbol.ClassSymbol)((JCTree.JCFieldAccess)var2).selected.type.tsym : this.accessClass(var1, var4, var2);
      Symbol var7 = var1;
      if (var1.owner != var6) {
         var7 = var1.clone(var6);
         this.actualSymbols.put(var7, var1);
      }

      Integer var8 = (Integer)this.accessNums.get(var7);
      if (var8 == null) {
         var8 = this.accessed.length();
         this.accessNums.put(var7, var8);
         this.accessSyms.put(var7, new Symbol.MethodSymbol[NCODES]);
         this.accessed.append(var7);
      }

      int var9;
      List var10;
      Type var11;
      List var12;
      switch (var7.kind) {
         case 4:
            var9 = accessCode(var2, var3);
            if (var9 >= 12) {
               Symbol.OperatorSymbol var13 = this.binaryAccessOperator(var9);
               if (var13.opcode == 256) {
                  var10 = List.of(this.syms.objectType);
               } else {
                  var10 = var13.type.getParameterTypes().tail;
               }
            } else if (var9 == 2) {
               var10 = List.of(var7.erasure(this.types));
            } else {
               var10 = List.nil();
            }

            var11 = var7.erasure(this.types);
            var12 = List.nil();
            break;
         case 16:
            var9 = 0;
            var10 = var7.erasure(this.types).getParameterTypes();
            var11 = var7.erasure(this.types).getReturnType();
            var12 = var7.type.getThrownTypes();
            break;
         default:
            throw new AssertionError();
      }

      if (var4 && var5) {
         ++var9;
      }

      if ((var7.flags() & 8L) == 0L) {
         var10 = var10.prepend(var7.owner.erasure(this.types));
      }

      Symbol.MethodSymbol[] var15 = (Symbol.MethodSymbol[])this.accessSyms.get(var7);
      Symbol.MethodSymbol var14 = var15[var9];
      if (var14 == null) {
         var14 = new Symbol.MethodSymbol(4104L, this.accessName(var8, var9), new Type.MethodType(var10, var11, var12, this.syms.methodClass), var6);
         this.enterSynthetic(var2.pos(), var14, var6.members());
         var15[var9] = var14;
      }

      return var14;
   }

   JCTree.JCExpression accessBase(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      return (var2.flags() & 8L) != 0L ? this.access(this.make.at(var1.getStartPosition()).QualIdent(var2.owner)) : this.makeOwnerThis(var1, var2, true);
   }

   boolean needsPrivateAccess(Symbol var1) {
      if ((var1.flags() & 2L) != 0L && var1.owner != this.currentClass) {
         if (var1.name == this.names.init && var1.owner.isLocal()) {
            var1.flags_field &= -3L;
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   boolean needsProtectedAccess(Symbol var1, JCTree var2) {
      if ((var1.flags() & 4L) != 0L && var1.owner.owner != this.currentClass.owner && var1.packge() != this.currentClass.packge()) {
         if (!this.currentClass.isSubClass(var1.owner, this.types)) {
            return true;
         } else if ((var1.flags() & 8L) == 0L && var2.hasTag(JCTree.Tag.SELECT) && TreeInfo.name(((JCTree.JCFieldAccess)var2).selected) != this.names._super) {
            return !((JCTree.JCFieldAccess)var2).selected.type.tsym.isSubClass(this.currentClass, this.types);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   Symbol.ClassSymbol accessClass(Symbol var1, boolean var2, JCTree var3) {
      if (!var2) {
         return var1.owner.enclClass();
      } else {
         Symbol.TypeSymbol var4 = null;
         Symbol.ClassSymbol var5 = this.currentClass;
         if (var3.hasTag(JCTree.Tag.SELECT) && (var1.flags() & 8L) == 0L) {
            for(var4 = ((JCTree.JCFieldAccess)var3).selected.type.tsym; !var4.isSubClass(var5, this.types); var5 = var5.owner.enclClass()) {
            }

            return var5;
         } else {
            while(!var5.isSubClass(var1.owner, this.types)) {
               var5 = var5.owner.enclClass();
            }

            return var5;
         }
      }
   }

   private void addPrunedInfo(JCTree var1) {
      List var2 = (List)this.prunedTree.get(this.currentClass);
      var2 = var2 == null ? List.of(var1) : var2.prepend(var1);
      this.prunedTree.put(this.currentClass, var2);
   }

   JCTree.JCExpression access(Symbol var1, JCTree.JCExpression var2, JCTree.JCExpression var3, boolean var4) {
      while(var1.kind == 4 && var1.owner.kind == 16 && var1.owner.enclClass() != this.currentClass) {
         Object var5 = ((Symbol.VarSymbol)var1).getConstValue();
         if (var5 != null) {
            this.make.at(((JCTree.JCExpression)var2).pos);
            return this.makeLit(var1.type, var5);
         }

         var1 = this.proxies.lookup(this.proxyName(var1.name)).sym;
         Assert.check(var1 != null && (var1.flags_field & 16L) != 0L);
         var2 = this.make.at(((JCTree.JCExpression)var2).pos).Ident(var1);
      }

      JCTree.JCExpression var12 = ((JCTree.JCExpression)var2).hasTag(JCTree.Tag.SELECT) ? ((JCTree.JCFieldAccess)var2).selected : null;
      switch (var1.kind) {
         case 2:
            if (var1.owner.kind != 1) {
               Name var14;
               for(var14 = Convert.shortName(var1.flatName()); var12 != null && TreeInfo.symbol(var12) != null && TreeInfo.symbol(var12).kind != 1; var12 = var12.hasTag(JCTree.Tag.SELECT) ? ((JCTree.JCFieldAccess)var12).selected : null) {
               }

               if (((JCTree.JCExpression)var2).hasTag(JCTree.Tag.IDENT)) {
                  ((JCTree.JCIdent)var2).name = var14;
               } else if (var12 == null) {
                  var2 = this.make.at(((JCTree.JCExpression)var2).pos).Ident(var1);
                  ((JCTree.JCIdent)var2).name = var14;
               } else {
                  ((JCTree.JCFieldAccess)var2).selected = var12;
                  ((JCTree.JCFieldAccess)var2).name = var14;
               }
            }
            break;
         case 4:
         case 16:
            if (var1.owner.kind == 2) {
               boolean var6 = var4 && !this.needsPrivateAccess(var1) || this.needsProtectedAccess(var1, (JCTree)var2);
               boolean var7 = var6 || this.needsPrivateAccess(var1);
               boolean var8 = var12 == null && var1.owner != this.syms.predefClass && !var1.isMemberOf(this.currentClass, this.types);
               if (var7 || var8) {
                  this.make.at(((JCTree.JCExpression)var2).pos);
                  if (var1.kind == 4) {
                     Object var9 = ((Symbol.VarSymbol)var1).getConstValue();
                     if (var9 != null) {
                        this.addPrunedInfo((JCTree)var2);
                        return this.makeLit(var1.type, var9);
                     }
                  }

                  if (var7) {
                     List var15 = List.nil();
                     if ((var1.flags() & 8L) == 0L) {
                        if (var12 == null) {
                           var12 = this.makeOwnerThis(((JCTree.JCExpression)var2).pos(), var1, true);
                        }

                        var15 = var15.prepend(var12);
                        var12 = null;
                     }

                     Symbol.MethodSymbol var10 = this.accessSymbol(var1, (JCTree)var2, var3, var6, var4);
                     JCTree.JCExpression var11 = this.make.Select(var12 != null ? var12 : this.make.QualIdent(var10.owner), (Symbol)var10);
                     return this.make.App(var11, var15);
                  }

                  if (var8) {
                     return this.make.at(((JCTree.JCExpression)var2).pos).Select(this.accessBase(((JCTree.JCExpression)var2).pos(), var1), var1).setType(((JCTree.JCExpression)var2).type);
                  }
               }
            } else if (var1.owner.kind == 16 && this.lambdaTranslationMap != null) {
               Symbol var13 = (Symbol)this.lambdaTranslationMap.get(var1);
               if (var13 != null) {
                  var2 = this.make.at(((JCTree.JCExpression)var2).pos).Ident(var13);
               }
            }
      }

      return (JCTree.JCExpression)var2;
   }

   JCTree.JCExpression access(JCTree.JCExpression var1) {
      Symbol var2 = TreeInfo.symbol(var1);
      return var2 == null ? var1 : this.access(var2, var1, (JCTree.JCExpression)null, false);
   }

   Symbol accessConstructor(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if (this.needsPrivateAccess(var2)) {
         Symbol.ClassSymbol var3 = var2.owner.enclClass();
         Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)this.accessConstrs.get(var2);
         if (var4 == null) {
            List var5 = var2.type.getParameterTypes();
            if ((var3.flags_field & 16384L) != 0L) {
               var5 = var5.prepend(this.syms.intType).prepend(this.syms.stringType);
            }

            var4 = new Symbol.MethodSymbol(4096L, this.names.init, new Type.MethodType(var5.append(this.accessConstructorTag().erasure(this.types)), var2.type.getReturnType(), var2.type.getThrownTypes(), this.syms.methodClass), var3);
            this.enterSynthetic(var1, var4, var3.members());
            this.accessConstrs.put(var2, var4);
            this.accessed.append(var2);
         }

         return var4;
      } else {
         return var2;
      }
   }

   Symbol.ClassSymbol accessConstructorTag() {
      Symbol.ClassSymbol var1 = this.currentClass.outermostClass();
      Name var2 = this.names.fromString("" + var1.getQualifiedName() + this.target.syntheticNameChar() + "1");
      Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)this.chk.compiled.get(var2);
      if (var3 == null) {
         var3 = this.makeEmptyClass(4104L, var1).sym;
      }

      this.accessConstrTags = this.accessConstrTags.prepend(var3);
      return var3;
   }

   void makeAccessible(Symbol var1) {
      JCTree.JCClassDecl var2 = this.classDef(var1.owner.enclClass());
      if (var2 == null) {
         Assert.error("class def not found: " + var1 + " in " + var1.owner);
      }

      if (var1.name == this.names.init) {
         var2.defs = var2.defs.prepend(this.accessConstructorDef(var2.pos, var1, (Symbol.MethodSymbol)this.accessConstrs.get(var1)));
      } else {
         Symbol.MethodSymbol[] var3 = (Symbol.MethodSymbol[])this.accessSyms.get(var1);

         for(int var4 = 0; var4 < NCODES; ++var4) {
            if (var3[var4] != null) {
               var2.defs = var2.defs.prepend(this.accessDef(var2.pos, var1, var3[var4], var4));
            }
         }
      }

   }

   private static JCTree.Tag mapUnaryOpCodeToTag(int var0) {
      switch (var0) {
         case 4:
            return JCTree.Tag.PREINC;
         case 5:
         case 7:
         case 9:
         default:
            return JCTree.Tag.NO_TAG;
         case 6:
            return JCTree.Tag.PREDEC;
         case 8:
            return JCTree.Tag.POSTINC;
         case 10:
            return JCTree.Tag.POSTDEC;
      }
   }

   private static int mapTagToUnaryOpCode(JCTree.Tag var0) {
      switch (var0) {
         case PREINC:
            return 4;
         case PREDEC:
            return 6;
         case POSTINC:
            return 8;
         case POSTDEC:
            return 10;
         default:
            return -1;
      }
   }

   JCTree accessDef(int var1, Symbol var2, Symbol.MethodSymbol var3, int var4) {
      this.currentClass = var2.owner.enclClass();
      this.make.at(var1);
      JCTree.JCMethodDecl var5 = this.make.MethodDef(var3, (JCTree.JCBlock)null);
      Symbol var6 = (Symbol)this.actualSymbols.get(var2);
      if (var6 == null) {
         var6 = var2;
      }

      Object var7;
      List var8;
      if ((var6.flags() & 8L) != 0L) {
         var7 = this.make.Ident(var6);
         var8 = this.make.Idents(var5.params);
      } else {
         JCTree.JCExpression var9 = this.make.Ident((JCTree.JCVariableDecl)var5.params.head);
         if (var4 % 2 != 0) {
            var9.setType(this.types.erasure(this.types.supertype(var2.owner.enclClass().type)));
         }

         var7 = this.make.Select(var9, var6);
         var8 = this.make.Idents(var5.params.tail);
      }

      Object var13;
      if (var6.kind == 4) {
         int var10 = var4 - (var4 & 1);
         Object var11;
         switch (var10) {
            case 0:
               var11 = var7;
               break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            default:
               var11 = this.make.Assignop(treeTag(this.binaryAccessOperator(var10)), (JCTree)var7, (JCTree)var8.head);
               ((JCTree.JCAssignOp)var11).operator = this.binaryAccessOperator(var10);
               break;
            case 2:
               var11 = this.make.Assign((JCTree.JCExpression)var7, (JCTree.JCExpression)var8.head);
               break;
            case 4:
            case 6:
            case 8:
            case 10:
               var11 = this.makeUnary(mapUnaryOpCodeToTag(var10), (JCTree.JCExpression)var7);
         }

         var13 = this.make.Return(((JCTree.JCExpression)var11).setType(var6.type));
      } else {
         var13 = this.make.Call(this.make.App((JCTree.JCExpression)var7, var8));
      }

      var5.body = this.make.Block(0L, List.of(var13));

      List var12;
      for(var12 = var5.params; var12.nonEmpty(); var12 = var12.tail) {
         ((JCTree.JCVariableDecl)var12.head).vartype = this.access(((JCTree.JCVariableDecl)var12.head).vartype);
      }

      var5.restype = this.access(var5.restype);

      for(var12 = var5.thrown; var12.nonEmpty(); var12 = var12.tail) {
         var12.head = this.access((JCTree.JCExpression)var12.head);
      }

      return var5;
   }

   JCTree accessConstructorDef(int var1, Symbol var2, Symbol.MethodSymbol var3) {
      this.make.at(var1);
      JCTree.JCMethodDecl var4 = this.make.MethodDef(var3, var3.externalType(this.types), (JCTree.JCBlock)null);
      JCTree.JCIdent var5 = this.make.Ident(this.names._this);
      var5.sym = var2;
      var5.type = var2.type;
      var4.body = this.make.Block(0L, List.of(this.make.Call(this.make.App(var5, this.make.Idents(var4.params.reverse().tail.reverse())))));
      return var4;
   }

   Name proxyName(Name var1) {
      return this.names.fromString("val" + this.target.syntheticNameChar() + var1);
   }

   List freevarDefs(int var1, List var2, Symbol var3) {
      return this.freevarDefs(var1, var2, var3, 0L);
   }

   List freevarDefs(int var1, List var2, Symbol var3, long var4) {
      long var6 = 4112L | var4;
      if (var3.kind == 2 && this.target.usePrivateSyntheticFields()) {
         var6 |= 2L;
      }

      List var8 = List.nil();

      for(List var9 = var2; var9.nonEmpty(); var9 = var9.tail) {
         Symbol.VarSymbol var10 = (Symbol.VarSymbol)var9.head;
         Symbol.VarSymbol var11 = new Symbol.VarSymbol(var6, this.proxyName(var10.name), var10.erasure(this.types), var3);
         this.proxies.enter(var11);
         JCTree.JCVariableDecl var12 = this.make.at(var1).VarDef(var11, (JCTree.JCExpression)null);
         var12.vartype = this.access(var12.vartype);
         var8 = var8.prepend(var12);
      }

      return var8;
   }

   Name outerThisName(Type var1, Symbol var2) {
      Type var3 = var1.getEnclosingType();

      int var4;
      for(var4 = 0; var3.hasTag(TypeTag.CLASS); ++var4) {
         var3 = var3.getEnclosingType();
      }

      Name var5;
      for(var5 = this.names.fromString("this" + this.target.syntheticNameChar() + var4); var2.kind == 2 && ((Symbol.ClassSymbol)var2).members().lookup(var5).scope != null; var5 = this.names.fromString(var5.toString() + this.target.syntheticNameChar())) {
      }

      return var5;
   }

   private Symbol.VarSymbol makeOuterThisVarSymbol(Symbol var1, long var2) {
      if (var1.kind == 2 && this.target.usePrivateSyntheticFields()) {
         var2 |= 2L;
      }

      Type var4 = this.types.erasure(var1.enclClass().type.getEnclosingType());
      Symbol.VarSymbol var5 = new Symbol.VarSymbol(var2, this.outerThisName(var4, var1), var4, var1);
      this.outerThisStack = this.outerThisStack.prepend(var5);
      return var5;
   }

   private JCTree.JCVariableDecl makeOuterThisVarDecl(int var1, Symbol.VarSymbol var2) {
      JCTree.JCVariableDecl var3 = this.make.at(var1).VarDef(var2, (JCTree.JCExpression)null);
      var3.vartype = this.access(var3.vartype);
      return var3;
   }

   JCTree.JCVariableDecl outerThisDef(int var1, Symbol.MethodSymbol var2) {
      Symbol.ClassSymbol var3 = var2.enclClass();
      boolean var4 = var2.isConstructor() && var2.isAnonymous() || var2.isConstructor() && var3.isInner() && !var3.isPrivate() && !var3.isStatic();
      long var5 = (long)(16 | (var4 ? '耀' : 4096)) | 8589934592L;
      Symbol.VarSymbol var7 = this.makeOuterThisVarSymbol(var2, var5);
      var2.extraParams = var2.extraParams.prepend(var7);
      return this.makeOuterThisVarDecl(var1, var7);
   }

   JCTree.JCVariableDecl outerThisDef(int var1, Symbol.ClassSymbol var2) {
      Symbol.VarSymbol var3 = this.makeOuterThisVarSymbol(var2, 4112L);
      return this.makeOuterThisVarDecl(var1, var3);
   }

   List loadFreevars(JCDiagnostic.DiagnosticPosition var1, List var2) {
      List var3 = List.nil();

      for(List var4 = var2; var4.nonEmpty(); var4 = var4.tail) {
         var3 = var3.prepend(this.loadFreevar(var1, (Symbol.VarSymbol)var4.head));
      }

      return var3;
   }

   JCTree.JCExpression loadFreevar(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2) {
      return this.access(var2, this.make.at(var1).Ident((Symbol)var2), (JCTree.JCExpression)null, false);
   }

   JCTree.JCExpression makeThis(JCDiagnostic.DiagnosticPosition var1, Symbol.TypeSymbol var2) {
      return this.currentClass == var2 ? this.make.at(var1).This(var2.erasure(this.types)) : this.makeOuterThis(var1, var2);
   }

   JCTree makeTwrTry(JCTree.JCTry var1) {
      this.make_at(var1.pos());
      this.twrVars = this.twrVars.dup();
      JCTree.JCBlock var2 = this.makeTwrBlock(var1.resources, var1.body, var1.finallyCanCompleteNormally, 0);
      if (var1.catchers.isEmpty() && var1.finalizer == null) {
         this.result = this.translate(var2);
      } else {
         this.result = this.translate(this.make.Try(var2, var1.catchers, var1.finalizer));
      }

      this.twrVars = this.twrVars.leave();
      return this.result;
   }

   private JCTree.JCBlock makeTwrBlock(List var1, JCTree.JCBlock var2, boolean var3, int var4) {
      if (var1.isEmpty()) {
         return var2;
      } else {
         ListBuffer var5 = new ListBuffer();
         JCTree var6 = (JCTree)var1.head;
         Object var7 = null;
         JCTree.JCVariableDecl var9;
         Symbol.VarSymbol var20;
         if (var6 instanceof JCTree.JCVariableDecl) {
            JCTree.JCVariableDecl var8 = (JCTree.JCVariableDecl)var6;
            var7 = this.make.Ident((Symbol)var8.sym).setType(var6.type);
            var5.add(var8);
         } else {
            Assert.check(var6 instanceof JCTree.JCExpression);
            var20 = new Symbol.VarSymbol(4112L, this.makeSyntheticName(this.names.fromString("twrVar" + var4), this.twrVars), var6.type.hasTag(TypeTag.BOT) ? this.syms.autoCloseableType : var6.type, this.currentMethodSym);
            this.twrVars.enter(var20);
            var9 = this.make.VarDef(var20, (JCTree.JCExpression)var6);
            var7 = this.make.Ident((Symbol)var20);
            var5.add(var9);
         }

         var20 = new Symbol.VarSymbol(4096L, this.makeSyntheticName(this.names.fromString("primaryException" + var4), this.twrVars), this.syms.throwableType, this.currentMethodSym);
         this.twrVars.enter(var20);
         var9 = this.make.VarDef(var20, this.makeNull());
         var5.add(var9);
         Symbol.VarSymbol var10 = new Symbol.VarSymbol(4112L, this.names.fromString("t" + this.target.syntheticNameChar()), this.syms.throwableType, this.currentMethodSym);
         JCTree.JCVariableDecl var11 = this.make.VarDef(var10, (JCTree.JCExpression)null);
         JCTree.JCStatement var12 = this.make.Assignment(var20, this.make.Ident((Symbol)var10));
         JCTree.JCThrow var13 = this.make.Throw(this.make.Ident((Symbol)var10));
         JCTree.JCBlock var14 = this.make.Block(0L, List.of(var12, var13));
         JCTree.JCCatch var15 = this.make.Catch(var11, var14);
         int var16 = this.make.pos;
         this.make.at(TreeInfo.endPos(var2));
         JCTree.JCBlock var17 = this.makeTwrFinallyClause(var20, (JCTree.JCExpression)var7);
         this.make.at(var16);
         JCTree.JCTry var18 = this.make.Try(this.makeTwrBlock(var1.tail, var2, var3, var4 + 1), List.of(var15), var17);
         var18.finallyCanCompleteNormally = var3;
         var5.add(var18);
         JCTree.JCBlock var19 = this.make.Block(0L, var5.toList());
         return var19;
      }
   }

   private JCTree.JCBlock makeTwrFinallyClause(Symbol var1, JCTree.JCExpression var2) {
      Symbol.VarSymbol var3 = new Symbol.VarSymbol(4096L, this.make.paramName(2), this.syms.throwableType, this.currentMethodSym);
      JCTree.JCExpressionStatement var4 = this.make.Exec(this.makeCall(this.make.Ident(var1), this.names.addSuppressed, List.of(this.make.Ident((Symbol)var3))));
      JCTree.JCBlock var5 = this.make.Block(0L, List.of(this.makeResourceCloseInvocation(var2)));
      JCTree.JCVariableDecl var6 = this.make.VarDef(var3, (JCTree.JCExpression)null);
      JCTree.JCBlock var7 = this.make.Block(0L, List.of(var4));
      List var8 = List.of(this.make.Catch(var6, var7));
      JCTree.JCTry var9 = this.make.Try(var5, var8, (JCTree.JCBlock)null);
      var9.finallyCanCompleteNormally = true;
      JCTree.JCIf var10 = this.make.If(this.makeNonNullCheck(this.make.Ident(var1)), var9, this.makeResourceCloseInvocation(var2));
      return this.make.Block(0L, List.of(this.make.If(this.makeNonNullCheck(var2), var10, (JCTree.JCStatement)null)));
   }

   private JCTree.JCStatement makeResourceCloseInvocation(JCTree.JCExpression var1) {
      if (this.types.asSuper(var1.type, this.syms.autoCloseableType.tsym) == null) {
         var1 = (JCTree.JCExpression)this.convert(var1, this.syms.autoCloseableType);
      }

      JCTree.JCMethodInvocation var2 = this.makeCall(var1, this.names.close, List.nil());
      return this.make.Exec(var2);
   }

   private JCTree.JCExpression makeNonNullCheck(JCTree.JCExpression var1) {
      return this.makeBinary(JCTree.Tag.NE, var1, this.makeNull());
   }

   JCTree.JCExpression makeOuterThis(JCDiagnostic.DiagnosticPosition var1, Symbol.TypeSymbol var2) {
      List var3 = this.outerThisStack;
      if (var3.isEmpty()) {
         this.log.error(var1, "no.encl.instance.of.type.in.scope", new Object[]{var2});
         Assert.error();
         return this.makeNull();
      } else {
         Symbol.VarSymbol var4 = (Symbol.VarSymbol)var3.head;
         JCTree.JCExpression var5 = this.access(this.make.at(var1).Ident((Symbol)var4));

         for(Symbol.TypeSymbol var6 = var4.type.tsym; var6 != var2; var6 = var4.type.tsym) {
            do {
               var3 = var3.tail;
               if (var3.isEmpty()) {
                  this.log.error(var1, "no.encl.instance.of.type.in.scope", new Object[]{var2});
                  Assert.error();
                  return var5;
               }

               var4 = (Symbol.VarSymbol)var3.head;
            } while(var4.owner != var6);

            if (var6.owner.kind != 1 && !var6.hasOuterInstance()) {
               this.chk.earlyRefError(var1, var2);
               Assert.error();
               return this.makeNull();
            }

            var5 = this.access(this.make.at(var1).Select(var5, (Symbol)var4));
         }

         return var5;
      }
   }

   JCTree.JCExpression makeOwnerThis(JCDiagnostic.DiagnosticPosition var1, Symbol var2, boolean var3) {
      Symbol var4 = var2.owner;
      if (var3) {
         if (var2.isMemberOf(this.currentClass, this.types)) {
            return this.make.at(var1).This(var4.erasure(this.types));
         }
      } else if (this.currentClass.isSubClass(var2.owner, this.types)) {
         return this.make.at(var1).This(var4.erasure(this.types));
      }

      return this.makeOwnerThisN(var1, var2, var3);
   }

   JCTree.JCExpression makeOwnerThisN(JCDiagnostic.DiagnosticPosition var1, Symbol var2, boolean var3) {
      Symbol var4 = var2.owner;
      List var5 = this.outerThisStack;
      if (var5.isEmpty()) {
         this.log.error(var1, "no.encl.instance.of.type.in.scope", new Object[]{var4});
         Assert.error();
         return this.makeNull();
      } else {
         Symbol.VarSymbol var6 = (Symbol.VarSymbol)var5.head;
         JCTree.JCExpression var7 = this.access(this.make.at(var1).Ident((Symbol)var6));
         Symbol.TypeSymbol var8 = var6.type.tsym;

         while(true) {
            if (var3) {
               if (var2.isMemberOf(var8, this.types)) {
                  break;
               }
            } else if (var8.isSubClass(var2.owner, this.types)) {
               break;
            }

            do {
               var5 = var5.tail;
               if (var5.isEmpty()) {
                  this.log.error(var1, "no.encl.instance.of.type.in.scope", new Object[]{var4});
                  Assert.error();
                  return var7;
               }

               var6 = (Symbol.VarSymbol)var5.head;
            } while(var6.owner != var8);

            var7 = this.access(this.make.at(var1).Select(var7, (Symbol)var6));
            var8 = var6.type.tsym;
         }

         return var7;
      }
   }

   JCTree.JCStatement initField(int var1, Name var2) {
      Scope.Entry var3 = this.proxies.lookup(var2);
      Symbol var4 = var3.sym;
      Assert.check(var4.owner.kind == 16);
      Symbol var5 = var3.next().sym;
      Assert.check(var4.owner.owner == var5.owner);
      this.make.at(var1);
      return this.make.Exec(this.make.Assign(this.make.Select(this.make.This(var5.owner.erasure(this.types)), var5), this.make.Ident(var4)).setType(var5.erasure(this.types)));
   }

   JCTree.JCStatement initOuterThis(int var1) {
      Symbol.VarSymbol var2 = (Symbol.VarSymbol)this.outerThisStack.head;
      Assert.check(var2.owner.kind == 16);
      Symbol.VarSymbol var3 = (Symbol.VarSymbol)this.outerThisStack.tail.head;
      Assert.check(var2.owner.owner == var3.owner);
      this.make.at(var1);
      return this.make.Exec(this.make.Assign(this.make.Select(this.make.This(var3.owner.erasure(this.types)), (Symbol)var3), this.make.Ident((Symbol)var2)).setType(var3.erasure(this.types)));
   }

   private Symbol.ClassSymbol outerCacheClass() {
      Symbol.ClassSymbol var1 = this.outermostClassDef.sym;
      if ((var1.flags() & 512L) == 0L && !this.target.useInnerCacheClass()) {
         return var1;
      } else {
         Scope var2 = var1.members();

         for(Scope.Entry var3 = var2.elems; var3 != null; var3 = var3.sibling) {
            if (var3.sym.kind == 2 && var3.sym.name == this.names.empty && (var3.sym.flags() & 512L) == 0L) {
               return (Symbol.ClassSymbol)var3.sym;
            }
         }

         return this.makeEmptyClass(4104L, var1).sym;
      }
   }

   private Symbol.MethodSymbol classDollarSym(JCDiagnostic.DiagnosticPosition var1) {
      Symbol.ClassSymbol var2 = this.outerCacheClass();
      Symbol.MethodSymbol var3 = (Symbol.MethodSymbol)this.lookupSynthetic(this.classDollar, var2.members());
      if (var3 == null) {
         var3 = new Symbol.MethodSymbol(4104L, this.classDollar, new Type.MethodType(List.of(this.syms.stringType), this.types.erasure(this.syms.classType), List.nil(), this.syms.methodClass), var2);
         this.enterSynthetic(var1, var3, var2.members());
         JCTree.JCMethodDecl var4 = this.make.MethodDef(var3, (JCTree.JCBlock)null);

         try {
            var4.body = this.classDollarSymBody(var1, var4);
         } catch (Symbol.CompletionFailure var6) {
            var4.body = this.make.Block(0L, List.nil());
            this.chk.completionError(var1, var6);
         }

         JCTree.JCClassDecl var5 = this.classDef(var2);
         var5.defs = var5.defs.prepend(var4);
      }

      return var3;
   }

   JCTree.JCBlock classDollarSymBody(JCDiagnostic.DiagnosticPosition var1, JCTree.JCMethodDecl var2) {
      Symbol.MethodSymbol var3 = var2.sym;
      Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)var3.owner;
      JCTree.JCBlock var5;
      Symbol.VarSymbol var6;
      if (this.target.classLiteralsNoInit()) {
         var6 = new Symbol.VarSymbol(4104L, this.names.fromString("cl" + this.target.syntheticNameChar()), this.syms.classLoaderType, var4);
         this.enterSynthetic(var1, var6, var4.members());
         JCTree.JCVariableDecl var7 = this.make.VarDef(var6, (JCTree.JCExpression)null);
         JCTree.JCClassDecl var8 = this.classDef(var4);
         var8.defs = var8.defs.prepend(var7);
         JCTree.JCNewArray var9 = this.make.NewArray(this.make.Type(var4.type), List.of(this.make.Literal(TypeTag.INT, 0).setType(this.syms.intType)), (List)null);
         var9.type = new Type.ArrayType(this.types.erasure(var4.type), this.syms.arrayClass);
         Symbol.MethodSymbol var10 = this.lookupMethod(this.make_pos, this.names.forName, this.types.erasure(this.syms.classType), List.of(this.syms.stringType, this.syms.booleanType, this.syms.classLoaderType));
         JCTree.JCExpression var11 = this.make.Conditional(this.makeBinary(JCTree.Tag.EQ, this.make.Ident((Symbol)var6), this.makeNull()), this.make.Assign(this.make.Ident((Symbol)var6), this.makeCall(this.makeCall(this.makeCall(var9, this.names.getClass, List.nil()), this.names.getComponentType, List.nil()), this.names.getClassLoader, List.nil())).setType(this.syms.classLoaderType), this.make.Ident((Symbol)var6)).setType(this.syms.classLoaderType);
         List var12 = List.of(this.make.Ident((Symbol)((JCTree.JCVariableDecl)var2.params.head).sym), this.makeLit(this.syms.booleanType, 0), var11);
         var5 = this.make.Block(0L, List.of(this.make.Call(this.make.App(this.make.Ident((Symbol)var10), var12))));
      } else {
         Symbol.MethodSymbol var13 = this.lookupMethod(this.make_pos, this.names.forName, this.types.erasure(this.syms.classType), List.of(this.syms.stringType));
         var5 = this.make.Block(0L, List.of(this.make.Call(this.make.App(this.make.QualIdent(var13), List.of(this.make.Ident((Symbol)((JCTree.JCVariableDecl)var2.params.head).sym))))));
      }

      var6 = new Symbol.VarSymbol(4096L, this.make.paramName(1), this.syms.classNotFoundExceptionType, var3);
      JCTree.JCThrow var14;
      if (this.target.hasInitCause()) {
         JCTree.JCMethodInvocation var15 = this.makeCall(this.makeNewClass(this.syms.noClassDefFoundErrorType, List.nil()), this.names.initCause, List.of(this.make.Ident((Symbol)var6)));
         var14 = this.make.Throw(var15);
      } else {
         Symbol.MethodSymbol var16 = this.lookupMethod(this.make_pos, this.names.getMessage, this.syms.classNotFoundExceptionType, List.nil());
         var14 = this.make.Throw(this.makeNewClass(this.syms.noClassDefFoundErrorType, List.of(this.make.App(this.make.Select(this.make.Ident((Symbol)var6), (Symbol)var16), List.nil()))));
      }

      JCTree.JCBlock var17 = this.make.Block(0L, List.of(var14));
      JCTree.JCCatch var18 = this.make.Catch(this.make.VarDef(var6, (JCTree.JCExpression)null), var17);
      JCTree.JCTry var19 = this.make.Try(var5, List.of(var18), (JCTree.JCBlock)null);
      return this.make.Block(0L, List.of(var19));
   }

   private JCTree.JCMethodInvocation makeCall(JCTree.JCExpression var1, Name var2, List var3) {
      Assert.checkNonNull(var1.type);
      Symbol.MethodSymbol var4 = this.lookupMethod(this.make_pos, var2, var1.type, TreeInfo.types(var3));
      return this.make.App(this.make.Select(var1, (Symbol)var4), var3);
   }

   private Name cacheName(String var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1.startsWith("[")) {
         for(var2 = var2.append("array"); var1.startsWith("["); var1 = var1.substring(1)) {
            var2 = var2.append(this.target.syntheticNameChar());
         }

         if (var1.startsWith("L")) {
            var1 = var1.substring(0, var1.length() - 1);
         }
      } else {
         var2 = var2.append("class" + this.target.syntheticNameChar());
      }

      var2 = var2.append(var1.replace('.', this.target.syntheticNameChar()));
      return this.names.fromString(var2.toString());
   }

   private Symbol.VarSymbol cacheSym(JCDiagnostic.DiagnosticPosition var1, String var2) {
      Symbol.ClassSymbol var3 = this.outerCacheClass();
      Name var4 = this.cacheName(var2);
      Symbol.VarSymbol var5 = (Symbol.VarSymbol)this.lookupSynthetic(var4, var3.members());
      if (var5 == null) {
         var5 = new Symbol.VarSymbol(4104L, var4, this.types.erasure(this.syms.classType), var3);
         this.enterSynthetic(var1, var5, var3.members());
         JCTree.JCVariableDecl var6 = this.make.VarDef(var5, (JCTree.JCExpression)null);
         JCTree.JCClassDecl var7 = this.classDef(var3);
         var7.defs = var7.defs.prepend(var6);
      }

      return var5;
   }

   private JCTree.JCExpression classOf(JCTree var1) {
      return this.classOfType(var1.type, var1.pos());
   }

   private JCTree.JCExpression classOfType(Type var1, JCDiagnostic.DiagnosticPosition var2) {
      switch (var1.getTag()) {
         case BYTE:
         case SHORT:
         case CHAR:
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
         case BOOLEAN:
         case VOID:
            Symbol.ClassSymbol var3 = this.types.boxedClass(var1);
            Symbol var4 = this.rs.accessBase(this.rs.findIdentInType(this.attrEnv, var3.type, this.names.TYPE, 4), var2, var3.type, this.names.TYPE, true);
            if (var4.kind == 4) {
               ((Symbol.VarSymbol)var4).getConstValue();
            }

            return this.make.QualIdent(var4);
         case CLASS:
         case ARRAY:
            if (this.target.hasClassLiterals()) {
               Symbol.VarSymbol var7 = new Symbol.VarSymbol(25L, this.names._class, this.syms.classType, var1.tsym);
               return this.make_at(var2).Select(this.make.Type(var1), (Symbol)var7);
            }

            String var5 = this.writer.xClassName(var1).toString().replace('/', '.');
            Symbol.VarSymbol var6 = this.cacheSym(var2, var5);
            return this.make_at(var2).Conditional(this.makeBinary(JCTree.Tag.EQ, this.make.Ident((Symbol)var6), this.makeNull()), this.make.Assign(this.make.Ident((Symbol)var6), this.make.App(this.make.Ident((Symbol)this.classDollarSym(var2)), List.of(this.make.Literal(TypeTag.CLASS, var5).setType(this.syms.stringType)))).setType(this.types.erasure(this.syms.classType)), this.make.Ident((Symbol)var6)).setType(this.types.erasure(this.syms.classType));
         default:
            throw new AssertionError();
      }
   }

   private Symbol.ClassSymbol assertionsDisabledClass() {
      if (this.assertionsDisabledClassCache != null) {
         return this.assertionsDisabledClassCache;
      } else {
         this.assertionsDisabledClassCache = this.makeEmptyClass(4104L, this.outermostClassDef.sym).sym;
         return this.assertionsDisabledClassCache;
      }
   }

   private JCTree.JCExpression assertFlagTest(JCDiagnostic.DiagnosticPosition var1) {
      Symbol.ClassSymbol var2 = this.outermostClassDef.sym;
      Symbol.ClassSymbol var3 = !this.currentClass.isInterface() ? this.currentClass : this.assertionsDisabledClass();
      Symbol.VarSymbol var4 = (Symbol.VarSymbol)this.lookupSynthetic(this.dollarAssertionsDisabled, var3.members());
      if (var4 == null) {
         var4 = new Symbol.VarSymbol(4120L, this.dollarAssertionsDisabled, this.syms.booleanType, var3);
         this.enterSynthetic(var1, var4, var3.members());
         Symbol.MethodSymbol var5 = this.lookupMethod(var1, this.names.desiredAssertionStatus, this.types.erasure(this.syms.classType), List.nil());
         JCTree.JCClassDecl var6 = this.classDef(var3);
         this.make_at(var6.pos());
         JCTree.JCUnary var7 = this.makeUnary(JCTree.Tag.NOT, this.make.App(this.make.Select(this.classOfType(this.types.erasure(var2.type), var6.pos()), (Symbol)var5)));
         JCTree.JCVariableDecl var8 = this.make.VarDef(var4, var7);
         var6.defs = var6.defs.prepend(var8);
         if (this.currentClass.isInterface()) {
            JCTree.JCClassDecl var9 = this.classDef(this.currentClass);
            this.make_at(var9.pos());
            JCTree.JCIf var10 = this.make.If(this.make.QualIdent(var4), this.make.Skip(), (JCTree.JCStatement)null);
            JCTree.JCBlock var11 = this.make.Block(8L, List.of(var10));
            var9.defs = var9.defs.prepend(var11);
         }
      }

      this.make_at(var1);
      return this.makeUnary(JCTree.Tag.NOT, this.make.Ident((Symbol)var4));
   }

   JCTree abstractRval(JCTree var1, Type var2, TreeBuilder var3) {
      var1 = TreeInfo.skipParens(var1);
      switch (var1.getTag()) {
         case LITERAL:
            return var3.build(var1);
         case IDENT:
            JCTree.JCIdent var4 = (JCTree.JCIdent)var1;
            if ((var4.sym.flags() & 16L) != 0L && var4.sym.owner.kind == 16) {
               return var3.build(var1);
            }
         default:
            Symbol.VarSymbol var8 = new Symbol.VarSymbol(4112L, this.names.fromString(this.target.syntheticNameChar() + "" + var1.hashCode()), var2, this.currentMethodSym);
            var1 = this.convert(var1, var2);
            JCTree.JCVariableDecl var5 = this.make.VarDef(var8, (JCTree.JCExpression)var1);
            JCTree var6 = var3.build(this.make.Ident((Symbol)var8));
            JCTree.LetExpr var7 = this.make.LetExpr(var5, var6);
            var7.type = var6.type;
            return var7;
      }
   }

   JCTree abstractRval(JCTree var1, TreeBuilder var2) {
      return this.abstractRval(var1, var1.type, var2);
   }

   JCTree abstractLval(JCTree var1, final TreeBuilder var2) {
      var1 = TreeInfo.skipParens(var1);
      switch (var1.getTag()) {
         case IDENT:
            return var2.build(var1);
         case SELECT:
            final JCTree.JCFieldAccess var6 = (JCTree.JCFieldAccess)var1;
            JCTree.JCExpression var4 = TreeInfo.skipParens(var6.selected);
            Symbol var5 = TreeInfo.symbol(var6.selected);
            if (var5 != null && var5.kind == 2) {
               return var2.build(var1);
            }

            return this.abstractRval(var6.selected, new TreeBuilder() {
               public JCTree build(JCTree var1) {
                  return var2.build(Lower.this.make.Select((JCTree.JCExpression)var1, var6.sym));
               }
            });
         case INDEXED:
            final JCTree.JCArrayAccess var3 = (JCTree.JCArrayAccess)var1;
            return this.abstractRval(var3.indexed, new TreeBuilder() {
               public JCTree build(final JCTree var1) {
                  return Lower.this.abstractRval(var3.index, Lower.this.syms.intType, new TreeBuilder() {
                     public JCTree build(JCTree var1x) {
                        JCTree.JCArrayAccess var2x = Lower.this.make.Indexed((JCTree.JCExpression)var1, (JCTree.JCExpression)var1x);
                        var2x.setType(var3.type);
                        return var2.build(var2x);
                     }
                  });
               }
            });
         case TYPECAST:
            return this.abstractLval(((JCTree.JCTypeCast)var1).expr, var2);
         default:
            throw new AssertionError(var1);
      }
   }

   JCTree makeComma(JCTree var1, final JCTree var2) {
      return this.abstractRval(var1, new TreeBuilder() {
         public JCTree build(JCTree var1) {
            return var2;
         }
      });
   }

   public JCTree translate(JCTree var1) {
      if (var1 == null) {
         return null;
      } else {
         this.make_at(var1.pos());
         JCTree var2 = super.translate(var1);
         if (this.endPosTable != null && var2 != var1) {
            this.endPosTable.replaceTree(var1, var2);
         }

         return var2;
      }
   }

   public JCTree translate(JCTree var1, Type var2) {
      return var1 == null ? null : this.boxIfNeeded(this.translate(var1), var2);
   }

   public JCTree translate(JCTree var1, JCTree.JCExpression var2) {
      JCTree.JCExpression var3 = this.enclOp;
      this.enclOp = var2;
      JCTree var4 = this.translate(var1);
      this.enclOp = var3;
      return var4;
   }

   public List translate(List var1, JCTree.JCExpression var2) {
      JCTree.JCExpression var3 = this.enclOp;
      this.enclOp = var2;
      List var4 = this.translate(var1);
      this.enclOp = var3;
      return var4;
   }

   public List translate(List var1, Type var2) {
      if (var1 == null) {
         return null;
      } else {
         for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
            var3.head = this.translate((JCTree)var3.head, var2);
         }

         return var1;
      }
   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      if (this.needPackageInfoClass(var1)) {
         Name var2 = this.names.package_info;
         long var3 = 1536L;
         if (this.target.isPackageInfoSynthetic()) {
            var3 |= 4096L;
         }

         JCTree.JCClassDecl var5 = this.make.ClassDef(this.make.Modifiers(var3, var1.packageAnnotations), var2, List.nil(), (JCTree.JCExpression)null, List.nil(), List.nil());
         Symbol.ClassSymbol var6 = var1.packge.package_info;
         var6.flags_field |= var3;
         var6.setAttributes(var1.packge);
         Type.ClassType var7 = (Type.ClassType)var6.type;
         var7.supertype_field = this.syms.objectType;
         var7.interfaces_field = List.nil();
         var5.sym = var6;
         this.translated.append(var5);
      }

   }

   private boolean needPackageInfoClass(JCTree.JCCompilationUnit var1) {
      switch (this.pkginfoOpt) {
         case ALWAYS:
            return true;
         case LEGACY:
            return var1.packageAnnotations.nonEmpty();
         case NONEMPTY:
            Iterator var2 = var1.packge.getDeclarationAttributes().iterator();

            Attribute.RetentionPolicy var4;
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               Attribute.Compound var3 = (Attribute.Compound)var2.next();
               var4 = this.types.getRetention(var3);
            } while(var4 == Attribute.RetentionPolicy.SOURCE);

            return true;
         default:
            throw new AssertionError();
      }
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      Env var2 = this.attrEnv;
      Symbol.ClassSymbol var3 = this.currentClass;
      Symbol.MethodSymbol var4 = this.currentMethodSym;
      this.currentClass = var1.sym;
      this.currentMethodSym = null;
      this.attrEnv = this.typeEnvs.remove(this.currentClass);
      if (this.attrEnv == null) {
         this.attrEnv = var2;
      }

      this.classdefs.put(this.currentClass, var1);
      this.proxies = this.proxies.dup(this.currentClass);
      List var5 = this.outerThisStack;
      if ((var1.mods.flags & 16384L) != 0L && (this.types.supertype(this.currentClass.type).tsym.flags() & 16384L) == 0L) {
         this.visitEnumDef(var1);
      }

      JCTree.JCVariableDecl var6 = null;
      if (this.currentClass.hasOuterInstance()) {
         var6 = this.outerThisDef(var1.pos, this.currentClass);
      }

      List var7 = this.freevarDefs(var1.pos, this.freevars(this.currentClass), this.currentClass);
      var1.extending = (JCTree.JCExpression)this.translate(var1.extending);
      var1.implementing = this.translate(var1.implementing);
      if (this.currentClass.isLocal()) {
         Symbol.ClassSymbol var8 = this.currentClass.owner.enclClass();
         if (var8.trans_local == null) {
            var8.trans_local = List.nil();
         }

         var8.trans_local = var8.trans_local.prepend(this.currentClass);
      }

      List var9;
      for(List var12 = List.nil(); var1.defs != var12; var12 = var9) {
         var9 = var1.defs;

         for(List var10 = var9; var10.nonEmpty() && var10 != var12; var10 = var10.tail) {
            JCTree var11 = this.outermostMemberDef;
            if (var11 == null) {
               this.outermostMemberDef = (JCTree)var10.head;
            }

            var10.head = this.translate((JCTree)var10.head);
            this.outermostMemberDef = var11;
         }
      }

      JCTree.JCModifiers var10000;
      if ((var1.mods.flags & 4L) != 0L) {
         var10000 = var1.mods;
         var10000.flags |= 1L;
      }

      var10000 = var1.mods;
      var10000.flags &= 32273L;
      var1.name = Convert.shortName(this.currentClass.flatName());

      for(var9 = var7; var9.nonEmpty(); var9 = var9.tail) {
         var1.defs = var1.defs.prepend(var9.head);
         this.enterSynthetic(var1.pos(), ((JCTree.JCVariableDecl)var9.head).sym, this.currentClass.members());
      }

      if (this.currentClass.hasOuterInstance()) {
         var1.defs = var1.defs.prepend(var6);
         this.enterSynthetic(var1.pos(), var6.sym, this.currentClass.members());
      }

      this.proxies = this.proxies.leave();
      this.outerThisStack = var5;
      this.translated.append(var1);
      this.attrEnv = var2;
      this.currentClass = var3;
      this.currentMethodSym = var4;
      this.result = this.make_at(var1.pos()).Block(4096L, List.nil());
   }

   private void visitEnumDef(JCTree.JCClassDecl var1) {
      this.make_at(var1.pos());
      if (var1.extending == null) {
         var1.extending = this.make.Type(this.types.supertype(var1.type));
      }

      JCTree.JCExpression var2 = this.classOfType(var1.sym.type, var1.pos()).setType(this.types.erasure(this.syms.classType));
      int var3 = 0;
      ListBuffer var4 = new ListBuffer();
      ListBuffer var5 = new ListBuffer();
      ListBuffer var6 = new ListBuffer();

      for(List var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
         if (((JCTree)var7.head).hasTag(JCTree.Tag.VARDEF) && (((JCTree.JCVariableDecl)var7.head).mods.flags & 16384L) != 0L) {
            JCTree.JCVariableDecl var8 = (JCTree.JCVariableDecl)var7.head;
            this.visitEnumConstantDef(var8, var3++);
            var4.append(this.make.QualIdent(var8.sym));
            var5.append(var8);
         } else {
            var6.append(var7.head);
         }
      }

      Name var19;
      for(var19 = this.names.fromString(this.target.syntheticNameChar() + "VALUES"); var1.sym.members().lookup(var19).scope != null; var19 = this.names.fromString(var19 + "" + this.target.syntheticNameChar())) {
      }

      Type.ArrayType var20 = new Type.ArrayType(this.types.erasure(var1.type), this.syms.arrayClass);
      Symbol.VarSymbol var9 = new Symbol.VarSymbol(4122L, var19, var20, var1.type.tsym);
      JCTree.JCNewArray var10 = this.make.NewArray(this.make.Type(this.types.erasure(var1.type)), List.nil(), var4.toList());
      var10.type = var20;
      var5.append(this.make.VarDef(var9, var10));
      var1.sym.members().enter(var9);
      Symbol.MethodSymbol var11 = this.lookupMethod(var1.pos(), this.names.values, var1.type, List.nil());
      List var12;
      if (this.useClone()) {
         JCTree.JCTypeCast var13 = this.make.TypeCast((Type)var11.type.getReturnType(), this.make.App(this.make.Select(this.make.Ident((Symbol)var9), (Symbol)this.syms.arrayCloneMethod)));
         var12 = List.of(this.make.Return(var13));
      } else {
         Name var21;
         for(var21 = this.names.fromString(this.target.syntheticNameChar() + "result"); var1.sym.members().lookup(var21).scope != null; var21 = this.names.fromString(var21 + "" + this.target.syntheticNameChar())) {
         }

         Symbol.VarSymbol var14 = new Symbol.VarSymbol(4112L, var21, var20, var11);
         JCTree.JCNewArray var15 = this.make.NewArray(this.make.Type(this.types.erasure(var1.type)), List.of(this.make.Select(this.make.Ident((Symbol)var9), (Symbol)this.syms.lengthVar)), (List)null);
         var15.type = var20;
         JCTree.JCVariableDecl var16 = this.make.VarDef(var14, var15);
         if (this.systemArraycopyMethod == null) {
            this.systemArraycopyMethod = new Symbol.MethodSymbol(9L, this.names.fromString("arraycopy"), new Type.MethodType(List.of(this.syms.objectType, this.syms.intType, this.syms.objectType, this.syms.intType, this.syms.intType), this.syms.voidType, List.nil(), this.syms.methodClass), this.syms.systemType.tsym);
         }

         JCTree.JCExpressionStatement var17 = this.make.Exec(this.make.App(this.make.Select(this.make.Ident((Symbol)this.syms.systemType.tsym), (Symbol)this.systemArraycopyMethod), List.of(this.make.Ident((Symbol)var9), this.make.Literal(0), this.make.Ident((Symbol)var14), this.make.Literal(0), this.make.Select(this.make.Ident((Symbol)var9), (Symbol)this.syms.lengthVar))));
         JCTree.JCReturn var18 = this.make.Return(this.make.Ident((Symbol)var14));
         var12 = List.of(var16, var17, var18);
      }

      JCTree.JCMethodDecl var22 = this.make.MethodDef((Symbol.MethodSymbol)var11, this.make.Block(0L, var12));
      var5.append(var22);
      if (this.debugLower) {
         System.err.println(var1.sym + ".valuesDef = " + var22);
      }

      Symbol.MethodSymbol var23 = this.lookupMethod(var1.pos(), this.names.valueOf, var1.sym.type, List.of(this.syms.stringType));
      Assert.check((var23.flags() & 8L) != 0L);
      Symbol.VarSymbol var24 = (Symbol.VarSymbol)var23.params.head;
      JCTree.JCIdent var25 = this.make.Ident((Symbol)var24);
      JCTree.JCReturn var26 = this.make.Return(this.make.TypeCast((Type)var1.sym.type, this.makeCall(this.make.Ident((Symbol)this.syms.enumSym), this.names.valueOf, List.of(var2, var25))));
      JCTree.JCMethodDecl var27 = this.make.MethodDef(var23, this.make.Block(0L, List.of(var26)));
      var25.sym = ((JCTree.JCVariableDecl)var27.params.head).sym;
      if (this.debugLower) {
         System.err.println(var1.sym + ".valueOf = " + var27);
      }

      var5.append(var27);
      var5.appendList(var6.toList());
      var1.defs = var5.toList();
   }

   private boolean useClone() {
      try {
         Scope.Entry var1 = this.syms.objectType.tsym.members().lookup(this.names.clone);
         return var1.sym != null;
      } catch (Symbol.CompletionFailure var2) {
         return false;
      }
   }

   private void visitEnumConstantDef(JCTree.JCVariableDecl var1, int var2) {
      JCTree.JCNewClass var3 = (JCTree.JCNewClass)var1.init;
      var3.args = var3.args.prepend(this.makeLit(this.syms.intType, var2)).prepend(this.makeLit(this.syms.stringType, var1.name.toString()));
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      if (var1.name == this.names.init && (this.currentClass.flags_field & 16384L) != 0L) {
         JCTree.JCVariableDecl var2 = this.make_at(var1.pos()).Param(this.names.fromString(this.target.syntheticNameChar() + "enum" + this.target.syntheticNameChar() + "name"), this.syms.stringType, var1.sym);
         JCTree.JCModifiers var10000 = var2.mods;
         var10000.flags |= 4096L;
         Symbol.VarSymbol var11 = var2.sym;
         var11.flags_field |= 4096L;
         JCTree.JCVariableDecl var3 = this.make.Param(this.names.fromString(this.target.syntheticNameChar() + "enum" + this.target.syntheticNameChar() + "ordinal"), this.syms.intType, var1.sym);
         var10000 = var3.mods;
         var10000.flags |= 4096L;
         var11 = var3.sym;
         var11.flags_field |= 4096L;
         var1.params = var1.params.prepend(var3).prepend(var2);
         Symbol.MethodSymbol var4 = var1.sym;
         var4.extraParams = var4.extraParams.prepend(var3.sym);
         var4.extraParams = var4.extraParams.prepend(var2.sym);
         Type var5 = var4.erasure(this.types);
         var4.erasure_field = new Type.MethodType(var5.getParameterTypes().prepend(this.syms.intType).prepend(this.syms.stringType), var5.getReturnType(), var5.getThrownTypes(), this.syms.methodClass);
      }

      JCTree.JCMethodDecl var9 = this.currentMethodDef;
      Symbol.MethodSymbol var10 = this.currentMethodSym;

      try {
         this.currentMethodDef = var1;
         this.currentMethodSym = var1.sym;
         this.visitMethodDefInternal(var1);
      } finally {
         this.currentMethodDef = var9;
         this.currentMethodSym = var10;
      }

   }

   private void visitMethodDefInternal(JCTree.JCMethodDecl var1) {
      if (var1.name == this.names.init && (this.currentClass.isInner() || this.currentClass.isLocal())) {
         Symbol.MethodSymbol var15 = var1.sym;
         this.proxies = this.proxies.dup(var15);
         List var3 = this.outerThisStack;
         List var4 = this.freevars(this.currentClass);
         JCTree.JCVariableDecl var5 = null;
         if (this.currentClass.hasOuterInstance()) {
            var5 = this.outerThisDef(var1.pos, var15);
         }

         List var6 = this.freevarDefs(var1.pos, var4, var15, 8589934592L);
         var1.restype = (JCTree.JCExpression)this.translate(var1.restype);
         var1.params = this.translateVarDefs(var1.params);
         var1.thrown = this.translate(var1.thrown);
         if (var1.body == null) {
            this.result = var1;
            return;
         }

         var1.params = var1.params.appendList(var6);
         if (this.currentClass.hasOuterInstance()) {
            var1.params = var1.params.prepend(var5);
         }

         JCTree.JCStatement var7 = (JCTree.JCStatement)this.translate((JCTree)var1.body.stats.head);
         List var8 = List.nil();
         List var9;
         if (var4.nonEmpty()) {
            var9 = List.nil();

            for(List var10 = var4; var10.nonEmpty(); var10 = var10.tail) {
               if (TreeInfo.isInitialConstructor(var1)) {
                  Name var11 = this.proxyName(((Symbol.VarSymbol)var10.head).name);
                  var15.capturedLocals = var15.capturedLocals.append((Symbol.VarSymbol)((Symbol.VarSymbol)this.proxies.lookup(var11).sym));
                  var8 = var8.prepend(this.initField(var1.body.pos, var11));
               }

               var9 = var9.prepend(((Symbol.VarSymbol)var10.head).erasure(this.types));
            }

            Type var16 = var15.erasure(this.types);
            var15.erasure_field = new Type.MethodType(var16.getParameterTypes().appendList(var9), var16.getReturnType(), var16.getThrownTypes(), this.syms.methodClass);
         }

         if (this.currentClass.hasOuterInstance() && TreeInfo.isInitialConstructor(var1)) {
            var8 = var8.prepend(this.initOuterThis(var1.body.pos));
         }

         this.proxies = this.proxies.leave();
         var9 = this.translate(var1.body.stats.tail);
         if (this.target.initializeFieldsBeforeSuper()) {
            var1.body.stats = var9.prepend(var7).prependList(var8);
         } else {
            var1.body.stats = var9.prependList(var8).prepend(var7);
         }

         this.outerThisStack = var3;
      } else {
         Map var2 = this.lambdaTranslationMap;

         try {
            this.lambdaTranslationMap = (var1.sym.flags() & 4096L) != 0L && var1.sym.name.startsWith(this.names.lambda) ? this.makeTranslationMap(var1) : null;
            super.visitMethodDef(var1);
         } finally {
            this.lambdaTranslationMap = var2;
         }
      }

      this.result = var1;
   }

   private Map makeTranslationMap(JCTree.JCMethodDecl var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = var1.params.iterator();

      while(var3.hasNext()) {
         JCTree.JCVariableDecl var4 = (JCTree.JCVariableDecl)var3.next();
         Symbol.VarSymbol var5 = var4.sym;
         if (var5 != var5.baseSymbol()) {
            var2.put(var5.baseSymbol(), var5);
         }
      }

      return var2;
   }

   public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
      var1.annotations = List.nil();
      var1.underlyingType = (JCTree.JCExpression)this.translate(var1.underlyingType);
      if (var1.type.isAnnotated()) {
         var1.type = var1.underlyingType.type.unannotatedType().annotatedType(var1.type.getAnnotationMirrors());
      } else if (var1.underlyingType.type.isAnnotated()) {
         var1.type = var1.underlyingType.type;
      }

      this.result = var1;
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      var1.clazz = this.translate(var1.clazz);
      if (var1.type.isPrimitive() != var1.expr.type.isPrimitive()) {
         var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, (Type)var1.type);
      } else {
         var1.expr = (JCTree.JCExpression)this.translate(var1.expr);
      }

      this.result = var1;
   }

   public void visitNewClass(JCTree.JCNewClass var1) {
      Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1.constructor.owner;
      boolean var3 = (var1.constructor.owner.flags() & 16384L) != 0L;
      List var4 = var1.constructor.type.getParameterTypes();
      if (var3) {
         var4 = var4.prepend(this.syms.intType).prepend(this.syms.stringType);
      }

      var1.args = this.boxArgs(var4, var1.args, var1.varargsElement);
      var1.varargsElement = null;
      if (var2.isLocal()) {
         var1.args = var1.args.appendList(this.loadFreevars(var1.pos(), this.freevars(var2)));
      }

      Symbol var5 = this.accessConstructor(var1.pos(), var1.constructor);
      if (var5 != var1.constructor) {
         var1.args = var1.args.append(this.makeNull());
         var1.constructor = var5;
      }

      if (var2.hasOuterInstance()) {
         JCTree.JCExpression var6;
         if (var1.encl != null) {
            var6 = this.attr.makeNullCheck((JCTree.JCExpression)this.translate(var1.encl));
            var6.type = var1.encl.type;
         } else if (var2.isLocal()) {
            var6 = this.makeThis(var1.pos(), var2.type.getEnclosingType().tsym);
         } else {
            var6 = this.makeOwnerThis(var1.pos(), var2, false);
         }

         var1.args = var1.args.prepend(var6);
      }

      var1.encl = null;
      if (var1.def != null) {
         this.translate(var1.def);
         var1.clazz = this.access(this.make_at(var1.clazz.pos()).Ident((Symbol)var1.def.sym));
         var1.def = null;
      } else {
         var1.clazz = this.access(var2, var1.clazz, this.enclOp, false);
      }

      this.result = var1;
   }

   public void visitConditional(JCTree.JCConditional var1) {
      JCTree.JCExpression var2 = var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, (Type)this.syms.booleanType);
      if (var2.type.isTrue()) {
         this.result = this.convert(this.translate((JCTree)var1.truepart, (Type)var1.type), var1.type);
         this.addPrunedInfo(var2);
      } else if (var2.type.isFalse()) {
         this.result = this.convert(this.translate((JCTree)var1.falsepart, (Type)var1.type), var1.type);
         this.addPrunedInfo(var2);
      } else {
         var1.truepart = (JCTree.JCExpression)this.translate((JCTree)var1.truepart, (Type)var1.type);
         var1.falsepart = (JCTree.JCExpression)this.translate((JCTree)var1.falsepart, (Type)var1.type);
         this.result = var1;
      }

   }

   private JCTree convert(JCTree var1, Type var2) {
      if (var1.type != var2 && !var1.type.hasTag(TypeTag.BOT)) {
         JCTree.JCTypeCast var3 = this.make_at(var1.pos()).TypeCast((JCTree)this.make.Type(var2), (JCTree.JCExpression)var1);
         var3.type = var1.type.constValue() != null ? this.cfolder.coerce(var1.type, var2) : var2;
         return var3;
      } else {
         return var1;
      }
   }

   public void visitIf(JCTree.JCIf var1) {
      JCTree.JCExpression var2 = var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, (Type)this.syms.booleanType);
      if (var2.type.isTrue()) {
         this.result = this.translate(var1.thenpart);
         this.addPrunedInfo(var2);
      } else if (var2.type.isFalse()) {
         if (var1.elsepart != null) {
            this.result = this.translate(var1.elsepart);
         } else {
            this.result = this.make.Skip();
         }

         this.addPrunedInfo(var2);
      } else {
         var1.thenpart = (JCTree.JCStatement)this.translate(var1.thenpart);
         var1.elsepart = (JCTree.JCStatement)this.translate(var1.elsepart);
         this.result = var1;
      }

   }

   public void visitAssert(JCTree.JCAssert var1) {
      if (var1.detail == null) {
         var1.pos();
      } else {
         var1.detail.pos();
      }

      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, (Type)this.syms.booleanType);
      if (!var1.cond.type.isTrue()) {
         Object var3 = this.assertFlagTest(var1.pos());
         List var4 = var1.detail == null ? List.nil() : List.of(this.translate(var1.detail));
         if (!var1.cond.type.isFalse()) {
            var3 = this.makeBinary(JCTree.Tag.AND, (JCTree.JCExpression)var3, this.makeUnary(JCTree.Tag.NOT, var1.cond));
         }

         this.result = this.make.If((JCTree.JCExpression)var3, this.make_at(var1).Throw(this.makeNewClass(this.syms.assertionErrorType, var4)), (JCTree.JCStatement)null);
      } else {
         this.result = this.make.Skip();
      }

   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      Symbol var2 = TreeInfo.symbol(var1.meth);
      List var3 = var2.type.getParameterTypes();
      if (this.allowEnums && var2.name == this.names.init && var2.owner == this.syms.enumSym) {
         var3 = var3.tail.tail;
      }

      var1.args = this.boxArgs(var3, var1.args, var1.varargsElement);
      var1.varargsElement = null;
      Name var4 = TreeInfo.name(var1.meth);
      if (var2.name == this.names.init) {
         Symbol var5 = this.accessConstructor(var1.pos(), var2);
         if (var5 != var2) {
            var1.args = var1.args.append(this.makeNull());
            TreeInfo.setSymbol(var1.meth, var5);
         }

         Symbol.ClassSymbol var6 = (Symbol.ClassSymbol)var5.owner;
         if (var6.isLocal()) {
            var1.args = var1.args.appendList(this.loadFreevars(var1.pos(), this.freevars(var6)));
         }

         if ((var6.flags_field & 16384L) != 0L || var6.getQualifiedName() == this.names.java_lang_Enum) {
            List var7 = this.currentMethodDef.params;
            if (this.currentMethodSym.owner.hasOuterInstance()) {
               var7 = var7.tail;
            }

            var1.args = var1.args.prepend(this.make_at(var1.pos()).Ident((Symbol)((JCTree.JCVariableDecl)var7.tail.head).sym)).prepend(this.make.Ident((Symbol)((JCTree.JCVariableDecl)var7.head).sym));
         }

         if (var6.hasOuterInstance()) {
            JCTree.JCExpression var9;
            if (var1.meth.hasTag(JCTree.Tag.SELECT)) {
               var9 = this.attr.makeNullCheck((JCTree.JCExpression)this.translate(((JCTree.JCFieldAccess)var1.meth).selected));
               var1.meth = this.make.Ident(var5);
               ((JCTree.JCIdent)var1.meth).name = var4;
            } else if (!var6.isLocal() && var4 != this.names._this) {
               var9 = this.makeOwnerThisN(var1.meth.pos(), var6, false);
            } else {
               var9 = this.makeThis(var1.meth.pos(), var6.type.getEnclosingType().tsym);
            }

            var1.args = var1.args.prepend(var9);
         }
      } else {
         var1.meth = (JCTree.JCExpression)this.translate(var1.meth);
         if (var1.meth.hasTag(JCTree.Tag.APPLY)) {
            JCTree.JCMethodInvocation var8 = (JCTree.JCMethodInvocation)var1.meth;
            var8.args = var1.args.prependList(var8.args);
            this.result = var8;
            return;
         }
      }

      this.result = var1;
   }

   List boxArgs(List var1, List var2, Type var3) {
      List var4 = var2;
      if (var1.isEmpty()) {
         return var2;
      } else {
         boolean var5 = false;

         ListBuffer var6;
         for(var6 = new ListBuffer(); var1.tail.nonEmpty(); var1 = var1.tail) {
            JCTree.JCExpression var7 = (JCTree.JCExpression)this.translate((JCTree)var4.head, (Type)var1.head);
            var5 |= var7 != var4.head;
            var6.append(var7);
            var4 = var4.tail;
         }

         Type var10 = (Type)var1.head;
         if (var3 != null) {
            var5 = true;

            ListBuffer var8;
            for(var8 = new ListBuffer(); var4.nonEmpty(); var4 = var4.tail) {
               JCTree.JCExpression var9 = (JCTree.JCExpression)this.translate((JCTree)var4.head, var3);
               var8.append(var9);
            }

            JCTree.JCNewArray var12 = this.make.NewArray(this.make.Type(var3), List.nil(), var8.toList());
            var12.type = new Type.ArrayType(var3, this.syms.arrayClass);
            var6.append(var12);
         } else {
            if (var4.length() != 1) {
               throw new AssertionError(var4);
            }

            JCTree.JCExpression var11 = (JCTree.JCExpression)this.translate((JCTree)var4.head, var10);
            var5 |= var11 != var4.head;
            var6.append(var11);
            if (!var5) {
               return var2;
            }
         }

         return var6.toList();
      }
   }

   JCTree boxIfNeeded(JCTree var1, Type var2) {
      boolean var3 = var1.type.isPrimitive();
      if (var3 == var2.isPrimitive()) {
         return var1;
      } else {
         JCTree.JCExpression var5;
         if (var3) {
            Type var4 = this.types.unboxedType(var2);
            if (!var4.hasTag(TypeTag.NONE)) {
               if (!this.types.isSubtype(var1.type, var4)) {
                  var1.type = var4.constType(var1.type.constValue());
               }

               return this.boxPrimitive((JCTree.JCExpression)var1, var2);
            }

            var5 = this.boxPrimitive((JCTree.JCExpression)var1);
         } else {
            var5 = this.unbox((JCTree.JCExpression)var1, var2);
         }

         return var5;
      }
   }

   JCTree.JCExpression boxPrimitive(JCTree.JCExpression var1) {
      return this.boxPrimitive(var1, this.types.boxedClass(var1.type).type);
   }

   JCTree.JCExpression boxPrimitive(JCTree.JCExpression var1, Type var2) {
      this.make_at(var1.pos());
      Symbol.MethodSymbol var3;
      if (this.target.boxWithConstructors()) {
         var3 = this.lookupConstructor(var1.pos(), var2, List.nil().prepend(var1.type));
         return this.make.Create(var3, List.of(var1));
      } else {
         var3 = this.lookupMethod(var1.pos(), this.names.valueOf, var2, List.nil().prepend(var1.type));
         return this.make.App(this.make.QualIdent(var3), List.of(var1));
      }
   }

   JCTree.JCExpression unbox(JCTree.JCExpression var1, Type var2) {
      Type var3 = this.types.unboxedType(((JCTree.JCExpression)var1).type);
      if (var3.hasTag(TypeTag.NONE)) {
         var3 = var2;
         if (!var2.isPrimitive()) {
            throw new AssertionError(var2);
         }

         this.make_at(((JCTree.JCExpression)var1).pos());
         var1 = this.make.TypeCast((Type)this.types.boxedClass(var2).type, (JCTree.JCExpression)var1);
      } else if (!this.types.isSubtype(var3, var2)) {
         throw new AssertionError(var1);
      }

      this.make_at(((JCTree.JCExpression)var1).pos());
      Symbol.MethodSymbol var4 = this.lookupMethod(((JCTree.JCExpression)var1).pos(), var3.tsym.name.append(this.names.Value), ((JCTree.JCExpression)var1).type, List.nil());
      return this.make.App(this.make.Select((JCTree.JCExpression)var1, (Symbol)var4));
   }

   public void visitParens(JCTree.JCParens var1) {
      JCTree var2 = this.translate(var1.expr);
      this.result = (JCTree)(var2 == var1.expr ? var1 : var2);
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      var1.indexed = (JCTree.JCExpression)this.translate(var1.indexed);
      var1.index = (JCTree.JCExpression)this.translate((JCTree)var1.index, (Type)this.syms.intType);
      this.result = var1;
   }

   public void visitAssign(JCTree.JCAssign var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs, (JCTree.JCExpression)var1);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs, (Type)var1.lhs.type);
      if (var1.lhs.hasTag(JCTree.Tag.APPLY)) {
         JCTree.JCMethodInvocation var2 = (JCTree.JCMethodInvocation)var1.lhs;
         var2.args = List.of(var1.rhs).prependList(var2.args);
         this.result = var2;
      } else {
         this.result = var1;
      }

   }

   public void visitAssignop(final JCTree.JCAssignOp var1) {
      JCTree.JCExpression var2 = this.access(TreeInfo.skipParens(var1.lhs));
      final boolean var3 = !var1.lhs.type.isPrimitive() && var1.operator.type.getReturnType().isPrimitive();
      if (!var3 && !var2.hasTag(JCTree.Tag.APPLY)) {
         var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs, (JCTree.JCExpression)var1);
         var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs, (Type)((Type)var1.operator.type.getParameterTypes().tail.head));
         if (var1.lhs.hasTag(JCTree.Tag.APPLY)) {
            JCTree.JCMethodInvocation var6 = (JCTree.JCMethodInvocation)var1.lhs;
            JCTree.JCExpression var5 = ((Symbol.OperatorSymbol)var1.operator).opcode == 256 ? this.makeString(var1.rhs) : var1.rhs;
            var6.args = List.of(var5).prependList(var6.args);
            this.result = var6;
         } else {
            this.result = var1;
         }

      } else {
         JCTree var4 = this.abstractLval(var1.lhs, new TreeBuilder() {
            public JCTree build(JCTree var1x) {
               JCTree.Tag var2 = var1.getTag().noAssignOp();
               Symbol var3x = Lower.this.rs.resolveBinaryOperator(var1.pos(), var2, Lower.this.attrEnv, var1.type, var1.rhs.type);
               Object var4 = (JCTree.JCExpression)var1x;
               if (((JCTree.JCExpression)var4).type != var1.type) {
                  var4 = Lower.this.make.TypeCast((Type)var1.type, (JCTree.JCExpression)var4);
               }

               JCTree.JCBinary var5 = Lower.this.make.Binary(var2, (JCTree.JCExpression)var4, var1.rhs);
               var5.operator = var3x;
               var5.type = var3x.type.getReturnType();
               Object var6 = var3 ? Lower.this.make.TypeCast((Type)Lower.this.types.unboxedType(var1.type), var5) : var5;
               return Lower.this.make.Assign((JCTree.JCExpression)var1x, (JCTree.JCExpression)var6).setType(var1.type);
            }
         });
         this.result = this.translate(var4);
      }
   }

   JCTree lowerBoxedPostop(final JCTree.JCUnary var1) {
      final boolean var2 = TreeInfo.skipParens(var1.arg).hasTag(JCTree.Tag.TYPECAST);
      return this.abstractLval(var1.arg, new TreeBuilder() {
         public JCTree build(final JCTree var1x) {
            return Lower.this.abstractRval(var1x, var1.arg.type, new TreeBuilder() {
               public JCTree build(JCTree var1xx) {
                  JCTree.Tag var2x = var1.hasTag(JCTree.Tag.POSTINC) ? JCTree.Tag.PLUS_ASG : JCTree.Tag.MINUS_ASG;
                  Object var3 = var2 ? Lower.this.make.TypeCast(var1.arg.type, (JCTree.JCExpression)var1x) : var1x;
                  JCTree.JCAssignOp var4 = Lower.this.makeAssignop(var2x, (JCTree)var3, Lower.this.make.Literal(1));
                  return Lower.this.makeComma(var4, var1xx);
               }
            });
         }
      });
   }

   public void visitUnary(JCTree.JCUnary var1) {
      boolean var2 = var1.getTag().isIncOrDecUnaryOp();
      if (var2 && !var1.arg.type.isPrimitive()) {
         switch (var1.getTag()) {
            case PREINC:
            case PREDEC:
               JCTree.Tag var3 = var1.hasTag(JCTree.Tag.PREINC) ? JCTree.Tag.PLUS_ASG : JCTree.Tag.MINUS_ASG;
               JCTree.JCAssignOp var4 = this.makeAssignop(var3, var1.arg, this.make.Literal(1));
               this.result = this.translate((JCTree)var4, (Type)var1.type);
               return;
            case POSTINC:
            case POSTDEC:
               this.result = this.translate(this.lowerBoxedPostop(var1), var1.type);
               return;
            default:
               throw new AssertionError(var1);
         }
      } else {
         var1.arg = (JCTree.JCExpression)this.boxIfNeeded(this.translate((JCTree)var1.arg, (JCTree.JCExpression)var1), var1.type);
         if (var1.hasTag(JCTree.Tag.NOT) && var1.arg.type.constValue() != null) {
            var1.type = this.cfolder.fold1(257, var1.arg.type);
         }

         if (var2 && var1.arg.hasTag(JCTree.Tag.APPLY)) {
            this.result = var1.arg;
         } else {
            this.result = var1;
         }

      }
   }

   public void visitBinary(JCTree.JCBinary var1) {
      List var2 = var1.operator.type.getParameterTypes();
      JCTree.JCExpression var3 = var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs, (Type)((Type)var2.head));
      switch (var1.getTag()) {
         case OR:
            if (var3.type.isTrue()) {
               this.result = var3;
               return;
            }

            if (var3.type.isFalse()) {
               this.result = this.translate((JCTree)var1.rhs, (Type)((Type)var2.tail.head));
               return;
            }
            break;
         case AND:
            if (var3.type.isFalse()) {
               this.result = var3;
               return;
            }

            if (var3.type.isTrue()) {
               this.result = this.translate((JCTree)var1.rhs, (Type)((Type)var2.tail.head));
               return;
            }
      }

      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs, (Type)((Type)var2.tail.head));
      this.result = var1;
   }

   public void visitIdent(JCTree.JCIdent var1) {
      this.result = this.access(var1.sym, var1, this.enclOp, false);
   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      if (this.types.elemtype(var1.expr.type) == null) {
         this.visitIterableForeachLoop(var1);
      } else {
         this.visitArrayForeachLoop(var1);
      }

   }

   private void visitArrayForeachLoop(JCTree.JCEnhancedForLoop var1) {
      this.make_at(var1.expr.pos());
      Symbol.VarSymbol var2 = new Symbol.VarSymbol(4096L, this.names.fromString("arr" + this.target.syntheticNameChar()), var1.expr.type, this.currentMethodSym);
      JCTree.JCVariableDecl var3 = this.make.VarDef(var2, var1.expr);
      Symbol.VarSymbol var4 = new Symbol.VarSymbol(4096L, this.names.fromString("len" + this.target.syntheticNameChar()), this.syms.intType, this.currentMethodSym);
      JCTree.JCVariableDecl var5 = this.make.VarDef(var4, this.make.Select(this.make.Ident((Symbol)var2), (Symbol)this.syms.lengthVar));
      Symbol.VarSymbol var6 = new Symbol.VarSymbol(4096L, this.names.fromString("i" + this.target.syntheticNameChar()), this.syms.intType, this.currentMethodSym);
      JCTree.JCVariableDecl var7 = this.make.VarDef(var6, this.make.Literal(TypeTag.INT, 0));
      var7.init.type = var7.type = this.syms.intType.constType(0);
      List var8 = List.of(var3, var5, var7);
      JCTree.JCBinary var9 = this.makeBinary(JCTree.Tag.LT, this.make.Ident((Symbol)var6), this.make.Ident((Symbol)var4));
      JCTree.JCExpressionStatement var10 = this.make.Exec(this.makeUnary(JCTree.Tag.PREINC, this.make.Ident((Symbol)var6)));
      Type var11 = this.types.elemtype(var1.expr.type);
      JCTree.JCExpression var12 = this.make.Indexed((JCTree.JCExpression)this.make.Ident((Symbol)var2), this.make.Ident((Symbol)var6)).setType(var11);
      JCTree.JCVariableDecl var13 = (JCTree.JCVariableDecl)this.make.VarDef(var1.var.mods, var1.var.name, var1.var.vartype, var12).setType(var1.var.type);
      var13.sym = var1.var.sym;
      JCTree.JCBlock var14 = this.make.Block(0L, List.of(var13, var1.body));
      this.result = this.translate(this.make.ForLoop(var8, var9, List.of(var10), var14));
      this.patchTargets(var14, var1, this.result);
   }

   private void patchTargets(JCTree var1, final JCTree var2, final JCTree var3) {
      class Patcher extends TreeScanner {
         public void visitBreak(JCTree.JCBreak var1) {
            if (var1.target == var2) {
               var1.target = var3;
            }

         }

         public void visitContinue(JCTree.JCContinue var1) {
            if (var1.target == var2) {
               var1.target = var3;
            }

         }

         public void visitClassDef(JCTree.JCClassDecl var1) {
         }
      }

      (new Patcher()).scan(var1);
   }

   private void visitIterableForeachLoop(JCTree.JCEnhancedForLoop var1) {
      this.make_at(var1.expr.pos());
      Type var2 = this.syms.objectType;
      Type var3 = this.types.asSuper(this.types.cvarUpperBound(var1.expr.type), this.syms.iterableType.tsym);
      if (var3.getTypeArguments().nonEmpty()) {
         var2 = this.types.erasure((Type)var3.getTypeArguments().head);
      }

      Type var4;
      for(var4 = var1.expr.type; var4.hasTag(TypeTag.TYPEVAR); var4 = var4.getUpperBound()) {
      }

      var1.expr.type = this.types.erasure(var4);
      if (var4.isCompound()) {
         var1.expr = this.make.TypeCast(this.types.erasure(var3), var1.expr);
      }

      Symbol.MethodSymbol var5 = this.lookupMethod(var1.expr.pos(), this.names.iterator, var4, List.nil());
      Symbol.VarSymbol var6 = new Symbol.VarSymbol(4096L, this.names.fromString("i" + this.target.syntheticNameChar()), this.types.erasure(this.types.asSuper(var5.type.getReturnType(), this.syms.iteratorType.tsym)), this.currentMethodSym);
      JCTree.JCVariableDecl var7 = this.make.VarDef(var6, this.make.App(this.make.Select(var1.expr, (Symbol)var5).setType(this.types.erasure(var5.type))));
      Symbol.MethodSymbol var8 = this.lookupMethod(var1.expr.pos(), this.names.hasNext, var6.type, List.nil());
      JCTree.JCMethodInvocation var9 = this.make.App(this.make.Select(this.make.Ident((Symbol)var6), (Symbol)var8));
      Symbol.MethodSymbol var10 = this.lookupMethod(var1.expr.pos(), this.names.next, var6.type, List.nil());
      JCTree.JCMethodInvocation var11 = this.make.App(this.make.Select(this.make.Ident((Symbol)var6), (Symbol)var10));
      JCTree.JCTypeCast var14;
      if (var1.var.type.isPrimitive()) {
         var14 = this.make.TypeCast((Type)this.types.cvarUpperBound(var2), var11);
      } else {
         var14 = this.make.TypeCast((Type)var1.var.type, var11);
      }

      JCTree.JCVariableDecl var12 = (JCTree.JCVariableDecl)this.make.VarDef(var1.var.mods, var1.var.name, var1.var.vartype, var14).setType(var1.var.type);
      var12.sym = var1.var.sym;
      JCTree.JCBlock var13 = this.make.Block(0L, List.of(var12, var1.body));
      var13.endpos = TreeInfo.endPos(var1.body);
      this.result = this.translate(this.make.ForLoop(List.of(var7), var9, List.nil(), var13));
      this.patchTargets(var13, var1, this.result);
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      Symbol.MethodSymbol var2 = this.currentMethodSym;
      var1.mods = (JCTree.JCModifiers)this.translate(var1.mods);
      var1.vartype = (JCTree.JCExpression)this.translate(var1.vartype);
      if (this.currentMethodSym == null) {
         this.currentMethodSym = new Symbol.MethodSymbol(var1.mods.flags & 8L | 1048576L, this.names.empty, (Type)null, this.currentClass);
      }

      if (var1.init != null) {
         var1.init = (JCTree.JCExpression)this.translate((JCTree)var1.init, (Type)var1.type);
      }

      this.result = var1;
      this.currentMethodSym = var2;
   }

   public void visitBlock(JCTree.JCBlock var1) {
      Symbol.MethodSymbol var2 = this.currentMethodSym;
      if (this.currentMethodSym == null) {
         this.currentMethodSym = new Symbol.MethodSymbol(var1.flags | 1048576L, this.names.empty, (Type)null, this.currentClass);
      }

      super.visitBlock(var1);
      this.currentMethodSym = var2;
   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, (Type)this.syms.booleanType);
      this.result = var1;
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, (Type)this.syms.booleanType);
      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      this.result = var1;
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      var1.init = this.translate(var1.init);
      if (var1.cond != null) {
         var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, (Type)this.syms.booleanType);
      }

      var1.step = this.translate(var1.step);
      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      this.result = var1;
   }

   public void visitReturn(JCTree.JCReturn var1) {
      if (var1.expr != null) {
         var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, (Type)this.types.erasure(this.currentMethodDef.restype.type));
      }

      this.result = var1;
   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      Type var2 = this.types.supertype(var1.selector.type);
      boolean var3 = var2 != null && (var1.selector.type.tsym.flags() & 16384L) != 0L;
      boolean var4 = var2 != null && this.types.isSameType(var1.selector.type, this.syms.stringType);
      Object var5 = var3 ? var1.selector.type : (var4 ? this.syms.stringType : this.syms.intType);
      var1.selector = (JCTree.JCExpression)this.translate((JCTree)var1.selector, (Type)var5);
      var1.cases = this.translateCases(var1.cases);
      if (var3) {
         this.result = this.visitEnumSwitch(var1);
      } else if (var4) {
         this.result = this.visitStringSwitch(var1);
      } else {
         this.result = var1;
      }

   }

   public JCTree visitEnumSwitch(JCTree.JCSwitch var1) {
      Symbol.TypeSymbol var2 = var1.selector.type.tsym;
      EnumMapping var3 = this.mapForEnum(var1.pos(), var2);
      this.make_at(var1.pos());
      Symbol.MethodSymbol var4 = this.lookupMethod(var1.pos(), this.names.ordinal, var1.selector.type, List.nil());
      JCTree.JCArrayAccess var5 = this.make.Indexed((Symbol)var3.mapVar, this.make.App(this.make.Select(var1.selector, (Symbol)var4)));
      ListBuffer var6 = new ListBuffer();
      Iterator var7 = var1.cases.iterator();

      while(var7.hasNext()) {
         JCTree.JCCase var8 = (JCTree.JCCase)var7.next();
         if (var8.pat != null) {
            Symbol.VarSymbol var9 = (Symbol.VarSymbol)TreeInfo.symbol(var8.pat);
            JCTree.JCLiteral var10 = var3.forConstant(var9);
            var6.append(this.make.Case(var10, var8.stats));
         } else {
            var6.append(var8);
         }
      }

      JCTree.JCSwitch var11 = this.make.Switch(var5, var6.toList());
      this.patchTargets(var11, var1, var11);
      return var11;
   }

   public JCTree visitStringSwitch(JCTree.JCSwitch var1) {
      List var2 = var1.getCases();
      int var3 = var2.size();
      if (var3 == 0) {
         return this.make.at(var1.pos()).Exec(this.attr.makeNullCheck(var1.getExpression()));
      } else {
         ListBuffer var4 = new ListBuffer();
         LinkedHashMap var5 = new LinkedHashMap(var3 + 1, 1.0F);
         LinkedHashMap var6 = new LinkedHashMap(var3 + 1, 1.0F);
         int var7 = 0;

         for(Iterator var8 = var2.iterator(); var8.hasNext(); ++var7) {
            JCTree.JCCase var9 = (JCTree.JCCase)var8.next();
            JCTree.JCExpression var10 = var9.getExpression();
            if (var10 != null) {
               String var11 = (String)var10.type.constValue();
               Integer var12 = (Integer)var5.put(var11, var7);
               Assert.checkNull(var12);
               int var13 = var11.hashCode();
               Set var14 = (Set)var6.get(var13);
               if (var14 == null) {
                  LinkedHashSet var28 = new LinkedHashSet(1, 1.0F);
                  var28.add(var11);
                  var6.put(var13, var28);
               } else {
                  boolean var15 = var14.add(var11);
                  Assert.check(var15);
               }
            }
         }

         Symbol.VarSymbol var22 = new Symbol.VarSymbol(4112L, this.names.fromString("s" + var1.pos + this.target.syntheticNameChar()), this.syms.stringType, this.currentMethodSym);
         var4.append(this.make.at(var1.pos()).VarDef(var22, var1.getExpression()).setType(var22.type));
         Symbol.VarSymbol var23 = new Symbol.VarSymbol(4096L, this.names.fromString("tmp" + var1.pos + this.target.syntheticNameChar()), this.syms.intType, this.currentMethodSym);
         JCTree.JCVariableDecl var24 = (JCTree.JCVariableDecl)this.make.VarDef(var23, this.make.Literal(TypeTag.INT, -1)).setType(var23.type);
         var24.init.type = var23.type = this.syms.intType;
         var4.append(var24);
         ListBuffer var25 = new ListBuffer();
         JCTree.JCMethodInvocation var26 = this.makeCall(this.make.Ident((Symbol)var22), this.names.hashCode, List.nil()).setType(this.syms.intType);
         JCTree.JCSwitch var27 = this.make.Switch(var26, var25.toList());
         Iterator var29 = var6.entrySet().iterator();

         while(var29.hasNext()) {
            Map.Entry var31 = (Map.Entry)var29.next();
            int var16 = (Integer)var31.getKey();
            Set var17 = (Set)var31.getValue();
            Assert.check(var17.size() >= 1);
            JCTree.JCIf var18 = null;

            String var20;
            JCTree.JCMethodInvocation var21;
            for(Iterator var19 = var17.iterator(); var19.hasNext(); var18 = this.make.If(var21, this.make.Exec(this.make.Assign(this.make.Ident((Symbol)var23), this.make.Literal(var5.get(var20))).setType(var23.type)), var18)) {
               var20 = (String)var19.next();
               var21 = this.makeCall(this.make.Ident((Symbol)var22), this.names.equals, List.of(this.make.Literal(var20)));
            }

            ListBuffer var36 = new ListBuffer();
            JCTree.JCBreak var38 = this.make.Break((Name)null);
            var38.target = var27;
            var36.append(var18).append(var38);
            var25.append(this.make.Case(this.make.Literal(var16), var36.toList()));
         }

         var27.cases = var25.toList();
         var4.append(var27);
         ListBuffer var30 = new ListBuffer();
         JCTree.JCSwitch var32 = this.make.Switch(this.make.Ident((Symbol)var23), var30.toList());

         JCTree.JCCase var34;
         JCTree.JCLiteral var37;
         for(Iterator var33 = var2.iterator(); var33.hasNext(); var30.append(this.make.Case(var37, var34.getStatements()))) {
            var34 = (JCTree.JCCase)var33.next();
            this.patchTargets(var34, var1, var32);
            boolean var35 = var34.getExpression() == null;
            if (var35) {
               var37 = null;
            } else {
               var37 = this.make.Literal(var5.get((String)TreeInfo.skipParens(var34.getExpression()).type.constValue()));
            }
         }

         var32.cases = var30.toList();
         var4.append(var32);
         return this.make.Block(0L, var4.toList());
      }
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      var1.elemtype = (JCTree.JCExpression)this.translate(var1.elemtype);

      for(List var2 = var1.dims; var2.tail != null; var2 = var2.tail) {
         if (var2.head != null) {
            var2.head = this.translate((JCTree)((JCTree)var2.head), (Type)this.syms.intType);
         }
      }

      var1.elems = this.translate(var1.elems, this.types.elemtype(var1.type));
      this.result = var1;
   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      boolean var2 = var1.selected.hasTag(JCTree.Tag.SELECT) && TreeInfo.name(var1.selected) == this.names._super && !this.types.isDirectSuperInterface(((JCTree.JCFieldAccess)var1.selected).selected.type.tsym, this.currentClass);
      var1.selected = (JCTree.JCExpression)this.translate(var1.selected);
      if (var1.name == this.names._class) {
         this.result = this.classOf(var1.selected);
      } else if (var1.name == this.names._super && this.types.isDirectSuperInterface(var1.selected.type.tsym, this.currentClass)) {
         Symbol.TypeSymbol var3 = var1.selected.type.tsym;
         Assert.checkNonNull(this.types.asSuper(this.currentClass.type, var3));
         this.result = var1;
      } else if (var1.name != this.names._this && var1.name != this.names._super) {
         this.result = this.access(var1.sym, var1, this.enclOp, var2);
      } else {
         this.result = this.makeThis(var1.pos(), var1.selected.type.tsym);
      }

   }

   public void visitLetExpr(JCTree.LetExpr var1) {
      var1.defs = this.translateVarDefs(var1.defs);
      var1.expr = this.translate(var1.expr, var1.type);
      this.result = var1;
   }

   public void visitAnnotation(JCTree.JCAnnotation var1) {
      this.result = var1;
   }

   public void visitTry(JCTree.JCTry var1) {
      if (var1.resources.nonEmpty()) {
         this.result = this.makeTwrTry(var1);
      } else {
         boolean var2 = var1.body.getStatements().nonEmpty();
         boolean var3 = var1.catchers.nonEmpty();
         boolean var4 = var1.finalizer != null && var1.finalizer.getStatements().nonEmpty();
         if (!var3 && !var4) {
            this.result = this.translate(var1.body);
         } else if (!var2) {
            if (var4) {
               this.result = this.translate(var1.finalizer);
            } else {
               this.result = this.translate(var1.body);
            }

         } else {
            super.visitTry(var1);
         }
      }
   }

   public List translateTopLevelClass(Env var1, JCTree var2, TreeMaker var3) {
      ListBuffer var4 = null;

      try {
         this.attrEnv = var1;
         this.make = var3;
         this.endPosTable = var1.toplevel.endPositions;
         this.currentClass = null;
         this.currentMethodDef = null;
         this.outermostClassDef = var2.hasTag(JCTree.Tag.CLASSDEF) ? (JCTree.JCClassDecl)var2 : null;
         this.outermostMemberDef = null;
         this.translated = new ListBuffer();
         this.classdefs = new HashMap();
         this.actualSymbols = new HashMap();
         this.freevarCache = new HashMap();
         this.proxies = new Scope(this.syms.noSymbol);
         this.twrVars = new Scope(this.syms.noSymbol);
         this.outerThisStack = List.nil();
         this.accessNums = new HashMap();
         this.accessSyms = new HashMap();
         this.accessConstrs = new HashMap();
         this.accessConstrTags = List.nil();
         this.accessed = new ListBuffer();
         this.translate(var2, (JCTree.JCExpression)null);

         for(List var5 = this.accessed.toList(); var5.nonEmpty(); var5 = var5.tail) {
            this.makeAccessible((Symbol)var5.head);
         }

         Iterator var10 = this.enumSwitchMap.values().iterator();

         while(var10.hasNext()) {
            EnumMapping var6 = (EnumMapping)var10.next();
            var6.translate();
         }

         this.checkConflicts(this.translated.toList());
         this.checkAccessConstructorTags();
         var4 = this.translated;
         return var4.toList();
      } finally {
         this.attrEnv = null;
         this.make = null;
         this.endPosTable = null;
         this.currentClass = null;
         this.currentMethodDef = null;
         this.outermostClassDef = null;
         this.outermostMemberDef = null;
         this.translated = null;
         this.classdefs = null;
         this.actualSymbols = null;
         this.freevarCache = null;
         this.proxies = null;
         this.outerThisStack = null;
         this.accessNums = null;
         this.accessSyms = null;
         this.accessConstrs = null;
         this.accessConstrTags = null;
         this.accessed = null;
         this.enumSwitchMap.clear();
         this.assertionsDisabledClassCache = null;
      }
   }

   interface TreeBuilder {
      JCTree build(JCTree var1);
   }

   class EnumMapping {
      JCDiagnostic.DiagnosticPosition pos = null;
      int next = 1;
      final Symbol.TypeSymbol forEnum;
      final Symbol.VarSymbol mapVar;
      final Map values;

      EnumMapping(JCDiagnostic.DiagnosticPosition var2, Symbol.TypeSymbol var3) {
         this.forEnum = var3;
         this.values = new LinkedHashMap();
         this.pos = var2;
         Name var4 = Lower.this.names.fromString(Lower.this.target.syntheticNameChar() + "SwitchMap" + Lower.this.target.syntheticNameChar() + Lower.this.writer.xClassName(var3.type).toString().replace('/', '.').replace('.', Lower.this.target.syntheticNameChar()));
         Symbol.ClassSymbol var5 = Lower.this.outerCacheClass();
         this.mapVar = new Symbol.VarSymbol(4120L, var4, new Type.ArrayType(Lower.this.syms.intType, Lower.this.syms.arrayClass), var5);
         Lower.this.enterSynthetic(var2, this.mapVar, var5.members());
      }

      JCTree.JCLiteral forConstant(Symbol.VarSymbol var1) {
         Integer var2 = (Integer)this.values.get(var1);
         if (var2 == null) {
            this.values.put(var1, var2 = this.next++);
         }

         return Lower.this.make.Literal(var2);
      }

      void translate() {
         Lower.this.make.at(this.pos.getStartPosition());
         JCTree.JCClassDecl var1 = Lower.this.classDef((Symbol.ClassSymbol)this.mapVar.owner);
         Symbol.MethodSymbol var2 = Lower.this.lookupMethod(this.pos, Lower.this.names.values, this.forEnum.type, List.nil());
         JCTree.JCExpression var3 = Lower.this.make.Select(Lower.this.make.App(Lower.this.make.QualIdent(var2)), (Symbol)Lower.this.syms.lengthVar);
         JCTree.JCExpression var4 = Lower.this.make.NewArray(Lower.this.make.Type(Lower.this.syms.intType), List.of(var3), (List)null).setType(new Type.ArrayType(Lower.this.syms.intType, Lower.this.syms.arrayClass));
         ListBuffer var5 = new ListBuffer();
         Symbol.MethodSymbol var6 = Lower.this.lookupMethod(this.pos, Lower.this.names.ordinal, this.forEnum.type, List.nil());
         List var7 = List.nil().prepend(Lower.this.make.Catch(Lower.this.make.VarDef(new Symbol.VarSymbol(8589934592L, Lower.this.names.ex, Lower.this.syms.noSuchFieldErrorType, Lower.this.syms.noSymbol), (JCTree.JCExpression)null), Lower.this.make.Block(0L, List.nil())));
         Iterator var8 = this.values.entrySet().iterator();

         while(var8.hasNext()) {
            Map.Entry var9 = (Map.Entry)var8.next();
            Symbol.VarSymbol var10 = (Symbol.VarSymbol)var9.getKey();
            Integer var11 = (Integer)var9.getValue();
            JCTree.JCExpression var12 = Lower.this.make.Assign(Lower.this.make.Indexed((Symbol)this.mapVar, Lower.this.make.App(Lower.this.make.Select(Lower.this.make.QualIdent(var10), (Symbol)var6))), Lower.this.make.Literal(var11)).setType(Lower.this.syms.intType);
            JCTree.JCExpressionStatement var13 = Lower.this.make.Exec(var12);
            JCTree.JCTry var14 = Lower.this.make.Try(Lower.this.make.Block(0L, List.of(var13)), var7, (JCTree.JCBlock)null);
            var5.append(var14);
         }

         var1.defs = var1.defs.prepend(Lower.this.make.Block(8L, var5.toList())).prepend(Lower.this.make.VarDef(this.mapVar, var4));
      }
   }

   class FreeVarCollector extends BasicFreeVarCollector {
      Symbol owner;
      Symbol.ClassSymbol clazz;
      List fvs;

      FreeVarCollector(Symbol.ClassSymbol var2) {
         super();
         this.clazz = var2;
         this.owner = var2.owner;
         this.fvs = List.nil();
      }

      private void addFreeVar(Symbol.VarSymbol var1) {
         for(List var2 = this.fvs; var2.nonEmpty(); var2 = var2.tail) {
            if (var2.head == var1) {
               return;
            }
         }

         this.fvs = this.fvs.prepend(var1);
      }

      void addFreeVars(Symbol.ClassSymbol var1) {
         List var2 = (List)Lower.this.freevarCache.get(var1);
         if (var2 != null) {
            for(List var3 = var2; var3.nonEmpty(); var3 = var3.tail) {
               this.addFreeVar((Symbol.VarSymbol)var3.head);
            }
         }

      }

      void visitSymbol(Symbol var1) {
         Symbol var2 = var1;
         if (var1.kind == 4 || var1.kind == 16) {
            while(true) {
               if (var2 == null || var2.owner == this.owner) {
                  if (var2 != null && var2.owner == this.owner) {
                     Symbol.VarSymbol var3 = (Symbol.VarSymbol)var2;
                     if (var3.getConstValue() == null) {
                        this.addFreeVar(var3);
                     }
                     break;
                  }

                  if (Lower.this.outerThisStack.head != null && Lower.this.outerThisStack.head != var1) {
                     this.visitSymbol((Symbol)Lower.this.outerThisStack.head);
                  }
                  break;
               }

               var2 = Lower.this.proxies.lookup(Lower.this.proxyName(var2.name)).sym;
            }
         }

      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1.constructor.owner;
         if (var1.encl == null && var2.hasOuterInstance() && Lower.this.outerThisStack.head != null) {
            this.visitSymbol((Symbol)Lower.this.outerThisStack.head);
         }

         super.visitNewClass(var1);
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         if ((var1.name == Lower.this.names._this || var1.name == Lower.this.names._super) && var1.selected.type.tsym != this.clazz && Lower.this.outerThisStack.head != null) {
            this.visitSymbol((Symbol)Lower.this.outerThisStack.head);
         }

         super.visitSelect(var1);
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         if (TreeInfo.name(var1.meth) == Lower.this.names._super) {
            Symbol var2 = TreeInfo.symbol(var1.meth);
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.owner;
            if (var3.hasOuterInstance() && !var1.meth.hasTag(JCTree.Tag.SELECT) && Lower.this.outerThisStack.head != null) {
               this.visitSymbol((Symbol)Lower.this.outerThisStack.head);
            }
         }

         super.visitApply(var1);
      }
   }

   abstract class BasicFreeVarCollector extends TreeScanner {
      abstract void addFreeVars(Symbol.ClassSymbol var1);

      public void visitIdent(JCTree.JCIdent var1) {
         this.visitSymbol(var1.sym);
      }

      abstract void visitSymbol(Symbol var1);

      public void visitNewClass(JCTree.JCNewClass var1) {
         Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1.constructor.owner;
         this.addFreeVars(var2);
         super.visitNewClass(var1);
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         if (TreeInfo.name(var1.meth) == Lower.this.names._super) {
            this.addFreeVars((Symbol.ClassSymbol)TreeInfo.symbol(var1.meth).owner);
         }

         super.visitApply(var1);
      }
   }

   class ClassMap extends TreeScanner {
      public void visitClassDef(JCTree.JCClassDecl var1) {
         Lower.this.classdefs.put(var1.sym, var1);
         super.visitClassDef(var1);
      }
   }
}
