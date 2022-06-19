package com.sun.tools.javac.code;

import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ElementVisitor;

public class Symtab {
   protected static final Context.Key symtabKey = new Context.Key();
   public final Type.JCPrimitiveType byteType;
   public final Type.JCPrimitiveType charType;
   public final Type.JCPrimitiveType shortType;
   public final Type.JCPrimitiveType intType;
   public final Type.JCPrimitiveType longType;
   public final Type.JCPrimitiveType floatType;
   public final Type.JCPrimitiveType doubleType;
   public final Type.JCPrimitiveType booleanType;
   public final Type botType;
   public final Type.JCVoidType voidType;
   private final Names names;
   private final ClassReader reader;
   private final Target target;
   public final Symbol.PackageSymbol rootPackage;
   public final Symbol.PackageSymbol unnamedPackage;
   public final Symbol.TypeSymbol noSymbol;
   public final Symbol.ClassSymbol errSymbol;
   public final Symbol.ClassSymbol unknownSymbol;
   public final Type errType;
   public final Type unknownType;
   public final Symbol.ClassSymbol arrayClass;
   public final Symbol.MethodSymbol arrayCloneMethod;
   public final Symbol.ClassSymbol boundClass;
   public final Symbol.ClassSymbol methodClass;
   public final Type objectType;
   public final Type classType;
   public final Type classLoaderType;
   public final Type stringType;
   public final Type stringBufferType;
   public final Type stringBuilderType;
   public final Type cloneableType;
   public final Type serializableType;
   public final Type serializedLambdaType;
   public final Type methodHandleType;
   public final Type methodHandleLookupType;
   public final Type methodTypeType;
   public final Type nativeHeaderType;
   public final Type throwableType;
   public final Type errorType;
   public final Type interruptedExceptionType;
   public final Type illegalArgumentExceptionType;
   public final Type exceptionType;
   public final Type runtimeExceptionType;
   public final Type classNotFoundExceptionType;
   public final Type noClassDefFoundErrorType;
   public final Type noSuchFieldErrorType;
   public final Type assertionErrorType;
   public final Type cloneNotSupportedExceptionType;
   public final Type annotationType;
   public final Symbol.TypeSymbol enumSym;
   public final Type listType;
   public final Type collectionsType;
   public final Type comparableType;
   public final Type comparatorType;
   public final Type arraysType;
   public final Type iterableType;
   public final Type iteratorType;
   public final Type annotationTargetType;
   public final Type overrideType;
   public final Type retentionType;
   public final Type deprecatedType;
   public final Type suppressWarningsType;
   public final Type inheritedType;
   public final Type profileType;
   public final Type proprietaryType;
   public final Type systemType;
   public final Type autoCloseableType;
   public final Type trustMeType;
   public final Type lambdaMetafactory;
   public final Type repeatableType;
   public final Type documentedType;
   public final Type elementTypeType;
   public final Type functionalInterfaceType;
   public final Symbol.VarSymbol lengthVar;
   public final Symbol.OperatorSymbol nullcheck;
   public final Symbol.MethodSymbol enumFinalFinalize;
   public final Symbol.MethodSymbol autoCloseableClose;
   public final Type[] typeOfTag;
   public final Name[] boxedName;
   public final Set operatorNames;
   public final Map classes;
   public final Map packages;
   public final Symbol.ClassSymbol predefClass;

   public static Symtab instance(Context var0) {
      Symtab var1 = (Symtab)var0.get(symtabKey);
      if (var1 == null) {
         var1 = new Symtab(var0);
      }

      return var1;
   }

   public void initType(Type var1, Symbol.ClassSymbol var2) {
      var1.tsym = var2;
      this.typeOfTag[var1.getTag().ordinal()] = var1;
   }

   public void initType(Type var1, String var2) {
      this.initType(var1, new Symbol.ClassSymbol(1L, this.names.fromString(var2), var1, this.rootPackage));
   }

   public void initType(Type var1, String var2, String var3) {
      this.initType(var1, var2);
      this.boxedName[var1.getTag().ordinal()] = this.names.fromString("java.lang." + var3);
   }

   private Symbol.VarSymbol enterConstant(String var1, Type var2) {
      Symbol.VarSymbol var3 = new Symbol.VarSymbol(25L, this.names.fromString(var1), var2, this.predefClass);
      var3.setData(var2.constValue());
      this.predefClass.members().enter(var3);
      return var3;
   }

   private void enterBinop(String var1, Type var2, Type var3, Type var4, int var5) {
      this.predefClass.members().enter(new Symbol.OperatorSymbol(this.makeOperatorName(var1), new Type.MethodType(List.of(var2, var3), var4, List.nil(), this.methodClass), var5, this.predefClass));
   }

   private void enterBinop(String var1, Type var2, Type var3, Type var4, int var5, int var6) {
      this.enterBinop(var1, var2, var3, var4, var5 << 9 | var6);
   }

   private Symbol.OperatorSymbol enterUnop(String var1, Type var2, Type var3, int var4) {
      Symbol.OperatorSymbol var5 = new Symbol.OperatorSymbol(this.makeOperatorName(var1), new Type.MethodType(List.of(var2), var3, List.nil(), this.methodClass), var4, this.predefClass);
      this.predefClass.members().enter(var5);
      return var5;
   }

   private Name makeOperatorName(String var1) {
      Name var2 = this.names.fromString(var1);
      this.operatorNames.add(var2);
      return var2;
   }

   private Type enterClass(String var1) {
      return this.reader.enterClass(this.names.fromString(var1)).type;
   }

   public void synthesizeEmptyInterfaceIfMissing(Type var1) {
      final Symbol.Completer var2 = var1.tsym.completer;
      if (var2 != null) {
         var1.tsym.completer = new Symbol.Completer() {
            public void complete(Symbol var1) throws Symbol.CompletionFailure {
               try {
                  var2.complete(var1);
               } catch (Symbol.CompletionFailure var3) {
                  var1.flags_field |= 513L;
                  ((Type.ClassType)var1.type).supertype_field = Symtab.this.objectType;
               }

            }
         };
      }

   }

   public void synthesizeBoxTypeIfMissing(final Type var1) {
      Symbol.ClassSymbol var2 = this.reader.enterClass(this.boxedName[var1.getTag().ordinal()]);
      final Symbol.Completer var3 = var2.completer;
      if (var3 != null) {
         var2.completer = new Symbol.Completer() {
            public void complete(Symbol var1x) throws Symbol.CompletionFailure {
               try {
                  var3.complete(var1x);
               } catch (Symbol.CompletionFailure var6) {
                  var1x.flags_field |= 1L;
                  ((Type.ClassType)var1x.type).supertype_field = Symtab.this.objectType;
                  Name var3x = Symtab.this.target.boxWithConstructors() ? Symtab.this.names.init : Symtab.this.names.valueOf;
                  Symbol.MethodSymbol var4 = new Symbol.MethodSymbol(9L, var3x, new Type.MethodType(List.of(var1), var1x.type, List.nil(), Symtab.this.methodClass), var1x);
                  var1x.members().enter(var4);
                  Symbol.MethodSymbol var5 = new Symbol.MethodSymbol(1L, var1.tsym.name.append(Symtab.this.names.Value), new Type.MethodType(List.nil(), var1, List.nil(), Symtab.this.methodClass), var1x);
                  var1x.members().enter(var5);
               }

            }
         };
      }

   }

   private Type enterSyntheticAnnotation(String var1) {
      Type.ClassType var2 = (Type.ClassType)this.enterClass(var1);
      Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.tsym;
      var3.completer = null;
      var3.flags_field = 1073750529L;
      var3.erasure_field = var2;
      var3.members_field = new Scope(var3);
      var2.typarams_field = List.nil();
      var2.allparams_field = List.nil();
      var2.supertype_field = this.annotationType;
      var2.interfaces_field = List.nil();
      return var2;
   }

   protected Symtab(Context var1) throws Symbol.CompletionFailure {
      this.byteType = new Type.JCPrimitiveType(TypeTag.BYTE, (Symbol.TypeSymbol)null);
      this.charType = new Type.JCPrimitiveType(TypeTag.CHAR, (Symbol.TypeSymbol)null);
      this.shortType = new Type.JCPrimitiveType(TypeTag.SHORT, (Symbol.TypeSymbol)null);
      this.intType = new Type.JCPrimitiveType(TypeTag.INT, (Symbol.TypeSymbol)null);
      this.longType = new Type.JCPrimitiveType(TypeTag.LONG, (Symbol.TypeSymbol)null);
      this.floatType = new Type.JCPrimitiveType(TypeTag.FLOAT, (Symbol.TypeSymbol)null);
      this.doubleType = new Type.JCPrimitiveType(TypeTag.DOUBLE, (Symbol.TypeSymbol)null);
      this.booleanType = new Type.JCPrimitiveType(TypeTag.BOOLEAN, (Symbol.TypeSymbol)null);
      this.botType = new Type.BottomType();
      this.voidType = new Type.JCVoidType();
      this.typeOfTag = new Type[TypeTag.getTypeTagCount()];
      this.boxedName = new Name[TypeTag.getTypeTagCount()];
      this.operatorNames = new HashSet();
      this.classes = new HashMap();
      this.packages = new HashMap();
      var1.put((Context.Key)symtabKey, (Object)this);
      this.names = Names.instance(var1);
      this.target = Target.instance(var1);
      this.unknownType = new Type.UnknownType();
      this.rootPackage = new Symbol.PackageSymbol(this.names.empty, (Symbol)null);
      final JavacMessages var2 = JavacMessages.instance(var1);
      this.unnamedPackage = new Symbol.PackageSymbol(this.names.empty, this.rootPackage) {
         public String toString() {
            return var2.getLocalizedString("compiler.misc.unnamed.package");
         }
      };
      this.noSymbol = new Symbol.TypeSymbol(0, 0L, this.names.empty, Type.noType, this.rootPackage) {
         public Object accept(ElementVisitor var1, Object var2) {
            return var1.visitUnknown(this, var2);
         }
      };
      this.errSymbol = new Symbol.ClassSymbol(1073741833L, this.names.any, (Type)null, this.rootPackage);
      this.errType = new Type.ErrorType(this.errSymbol, Type.noType);
      this.unknownSymbol = new Symbol.ClassSymbol(1073741833L, this.names.fromString("<any?>"), (Type)null, this.rootPackage);
      this.unknownSymbol.members_field = new Scope.ErrorScope(this.unknownSymbol);
      this.unknownSymbol.type = this.unknownType;
      this.initType(this.byteType, "byte", "Byte");
      this.initType(this.shortType, "short", "Short");
      this.initType(this.charType, "char", "Character");
      this.initType(this.intType, "int", "Integer");
      this.initType(this.longType, "long", "Long");
      this.initType(this.floatType, "float", "Float");
      this.initType(this.doubleType, "double", "Double");
      this.initType(this.booleanType, "boolean", "Boolean");
      this.initType(this.voidType, "void", "Void");
      this.initType(this.botType, "<nulltype>");
      this.initType(this.errType, this.errSymbol);
      this.initType(this.unknownType, this.unknownSymbol);
      this.arrayClass = new Symbol.ClassSymbol(1073741825L, this.names.Array, this.noSymbol);
      this.boundClass = new Symbol.ClassSymbol(1073741825L, this.names.Bound, this.noSymbol);
      this.boundClass.members_field = new Scope.ErrorScope(this.boundClass);
      this.methodClass = new Symbol.ClassSymbol(1073741825L, this.names.Method, this.noSymbol);
      this.methodClass.members_field = new Scope.ErrorScope(this.boundClass);
      this.predefClass = new Symbol.ClassSymbol(1073741825L, this.names.empty, this.rootPackage);
      Scope var3 = new Scope(this.predefClass);
      this.predefClass.members_field = var3;
      var3.enter(this.byteType.tsym);
      var3.enter(this.shortType.tsym);
      var3.enter(this.charType.tsym);
      var3.enter(this.intType.tsym);
      var3.enter(this.longType.tsym);
      var3.enter(this.floatType.tsym);
      var3.enter(this.doubleType.tsym);
      var3.enter(this.booleanType.tsym);
      var3.enter(this.errType.tsym);
      var3.enter(this.errSymbol);
      this.classes.put(this.predefClass.fullname, this.predefClass);
      this.reader = ClassReader.instance(var1);
      this.reader.init(this);
      this.objectType = this.enterClass("java.lang.Object");
      this.classType = this.enterClass("java.lang.Class");
      this.stringType = this.enterClass("java.lang.String");
      this.stringBufferType = this.enterClass("java.lang.StringBuffer");
      this.stringBuilderType = this.enterClass("java.lang.StringBuilder");
      this.cloneableType = this.enterClass("java.lang.Cloneable");
      this.throwableType = this.enterClass("java.lang.Throwable");
      this.serializableType = this.enterClass("java.io.Serializable");
      this.serializedLambdaType = this.enterClass("java.lang.invoke.SerializedLambda");
      this.methodHandleType = this.enterClass("java.lang.invoke.MethodHandle");
      this.methodHandleLookupType = this.enterClass("java.lang.invoke.MethodHandles$Lookup");
      this.methodTypeType = this.enterClass("java.lang.invoke.MethodType");
      this.errorType = this.enterClass("java.lang.Error");
      this.illegalArgumentExceptionType = this.enterClass("java.lang.IllegalArgumentException");
      this.interruptedExceptionType = this.enterClass("java.lang.InterruptedException");
      this.exceptionType = this.enterClass("java.lang.Exception");
      this.runtimeExceptionType = this.enterClass("java.lang.RuntimeException");
      this.classNotFoundExceptionType = this.enterClass("java.lang.ClassNotFoundException");
      this.noClassDefFoundErrorType = this.enterClass("java.lang.NoClassDefFoundError");
      this.noSuchFieldErrorType = this.enterClass("java.lang.NoSuchFieldError");
      this.assertionErrorType = this.enterClass("java.lang.AssertionError");
      this.cloneNotSupportedExceptionType = this.enterClass("java.lang.CloneNotSupportedException");
      this.annotationType = this.enterClass("java.lang.annotation.Annotation");
      this.classLoaderType = this.enterClass("java.lang.ClassLoader");
      this.enumSym = this.reader.enterClass(this.names.java_lang_Enum);
      this.enumFinalFinalize = new Symbol.MethodSymbol(137438953492L, this.names.finalize, new Type.MethodType(List.nil(), this.voidType, List.nil(), this.methodClass), this.enumSym);
      this.listType = this.enterClass("java.util.List");
      this.collectionsType = this.enterClass("java.util.Collections");
      this.comparableType = this.enterClass("java.lang.Comparable");
      this.comparatorType = this.enterClass("java.util.Comparator");
      this.arraysType = this.enterClass("java.util.Arrays");
      this.iterableType = this.target.hasIterable() ? this.enterClass("java.lang.Iterable") : this.enterClass("java.util.Collection");
      this.iteratorType = this.enterClass("java.util.Iterator");
      this.annotationTargetType = this.enterClass("java.lang.annotation.Target");
      this.overrideType = this.enterClass("java.lang.Override");
      this.retentionType = this.enterClass("java.lang.annotation.Retention");
      this.deprecatedType = this.enterClass("java.lang.Deprecated");
      this.suppressWarningsType = this.enterClass("java.lang.SuppressWarnings");
      this.inheritedType = this.enterClass("java.lang.annotation.Inherited");
      this.repeatableType = this.enterClass("java.lang.annotation.Repeatable");
      this.documentedType = this.enterClass("java.lang.annotation.Documented");
      this.elementTypeType = this.enterClass("java.lang.annotation.ElementType");
      this.systemType = this.enterClass("java.lang.System");
      this.autoCloseableType = this.enterClass("java.lang.AutoCloseable");
      this.autoCloseableClose = new Symbol.MethodSymbol(1L, this.names.close, new Type.MethodType(List.nil(), this.voidType, List.of(this.exceptionType), this.methodClass), this.autoCloseableType.tsym);
      this.trustMeType = this.enterClass("java.lang.SafeVarargs");
      this.nativeHeaderType = this.enterClass("java.lang.annotation.Native");
      this.lambdaMetafactory = this.enterClass("java.lang.invoke.LambdaMetafactory");
      this.functionalInterfaceType = this.enterClass("java.lang.FunctionalInterface");
      this.synthesizeEmptyInterfaceIfMissing(this.autoCloseableType);
      this.synthesizeEmptyInterfaceIfMissing(this.cloneableType);
      this.synthesizeEmptyInterfaceIfMissing(this.serializableType);
      this.synthesizeEmptyInterfaceIfMissing(this.lambdaMetafactory);
      this.synthesizeEmptyInterfaceIfMissing(this.serializedLambdaType);
      this.synthesizeBoxTypeIfMissing(this.doubleType);
      this.synthesizeBoxTypeIfMissing(this.floatType);
      this.synthesizeBoxTypeIfMissing(this.voidType);
      this.proprietaryType = this.enterSyntheticAnnotation("sun.Proprietary+Annotation");
      this.profileType = this.enterSyntheticAnnotation("jdk.Profile+Annotation");
      Symbol.MethodSymbol var4 = new Symbol.MethodSymbol(1025L, this.names.value, this.intType, this.profileType.tsym);
      this.profileType.tsym.members().enter(var4);
      Type.ClassType var5 = (Type.ClassType)this.arrayClass.type;
      var5.supertype_field = this.objectType;
      var5.interfaces_field = List.of(this.cloneableType, this.serializableType);
      this.arrayClass.members_field = new Scope(this.arrayClass);
      this.lengthVar = new Symbol.VarSymbol(17L, this.names.length, this.intType, this.arrayClass);
      this.arrayClass.members().enter(this.lengthVar);
      this.arrayCloneMethod = new Symbol.MethodSymbol(1L, this.names.clone, new Type.MethodType(List.nil(), this.objectType, List.nil(), this.methodClass), this.arrayClass);
      this.arrayClass.members().enter(this.arrayCloneMethod);
      this.enterUnop("+++", this.doubleType, this.doubleType, 0);
      this.enterUnop("+++", this.floatType, this.floatType, 0);
      this.enterUnop("+++", this.longType, this.longType, 0);
      this.enterUnop("+++", this.intType, this.intType, 0);
      this.enterUnop("---", this.doubleType, this.doubleType, 119);
      this.enterUnop("---", this.floatType, this.floatType, 118);
      this.enterUnop("---", this.longType, this.longType, 117);
      this.enterUnop("---", this.intType, this.intType, 116);
      this.enterUnop("~", this.longType, this.longType, 131);
      this.enterUnop("~", this.intType, this.intType, 130);
      this.enterUnop("++", this.doubleType, this.doubleType, 99);
      this.enterUnop("++", this.floatType, this.floatType, 98);
      this.enterUnop("++", this.longType, this.longType, 97);
      this.enterUnop("++", this.intType, this.intType, 96);
      this.enterUnop("++", this.charType, this.charType, 96);
      this.enterUnop("++", this.shortType, this.shortType, 96);
      this.enterUnop("++", this.byteType, this.byteType, 96);
      this.enterUnop("--", this.doubleType, this.doubleType, 103);
      this.enterUnop("--", this.floatType, this.floatType, 102);
      this.enterUnop("--", this.longType, this.longType, 101);
      this.enterUnop("--", this.intType, this.intType, 100);
      this.enterUnop("--", this.charType, this.charType, 100);
      this.enterUnop("--", this.shortType, this.shortType, 100);
      this.enterUnop("--", this.byteType, this.byteType, 100);
      this.enterUnop("!", this.booleanType, this.booleanType, 257);
      this.nullcheck = this.enterUnop("<*nullchk*>", this.objectType, this.objectType, 276);
      this.enterBinop("+", this.stringType, this.objectType, this.stringType, 256);
      this.enterBinop("+", this.objectType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.intType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.longType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.floatType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.doubleType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.booleanType, this.stringType, 256);
      this.enterBinop("+", this.stringType, this.botType, this.stringType, 256);
      this.enterBinop("+", this.intType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.longType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.floatType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.doubleType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.booleanType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.botType, this.stringType, this.stringType, 256);
      this.enterBinop("+", this.botType, this.botType, this.botType, 277);
      this.enterBinop("+", this.botType, this.intType, this.botType, 277);
      this.enterBinop("+", this.botType, this.longType, this.botType, 277);
      this.enterBinop("+", this.botType, this.floatType, this.botType, 277);
      this.enterBinop("+", this.botType, this.doubleType, this.botType, 277);
      this.enterBinop("+", this.botType, this.booleanType, this.botType, 277);
      this.enterBinop("+", this.botType, this.objectType, this.botType, 277);
      this.enterBinop("+", this.intType, this.botType, this.botType, 277);
      this.enterBinop("+", this.longType, this.botType, this.botType, 277);
      this.enterBinop("+", this.floatType, this.botType, this.botType, 277);
      this.enterBinop("+", this.doubleType, this.botType, this.botType, 277);
      this.enterBinop("+", this.booleanType, this.botType, this.botType, 277);
      this.enterBinop("+", this.objectType, this.botType, this.botType, 277);
      this.enterBinop("+", this.doubleType, this.doubleType, this.doubleType, 99);
      this.enterBinop("+", this.floatType, this.floatType, this.floatType, 98);
      this.enterBinop("+", this.longType, this.longType, this.longType, 97);
      this.enterBinop("+", this.intType, this.intType, this.intType, 96);
      this.enterBinop("-", this.doubleType, this.doubleType, this.doubleType, 103);
      this.enterBinop("-", this.floatType, this.floatType, this.floatType, 102);
      this.enterBinop("-", this.longType, this.longType, this.longType, 101);
      this.enterBinop("-", this.intType, this.intType, this.intType, 100);
      this.enterBinop("*", this.doubleType, this.doubleType, this.doubleType, 107);
      this.enterBinop("*", this.floatType, this.floatType, this.floatType, 106);
      this.enterBinop("*", this.longType, this.longType, this.longType, 105);
      this.enterBinop("*", this.intType, this.intType, this.intType, 104);
      this.enterBinop("/", this.doubleType, this.doubleType, this.doubleType, 111);
      this.enterBinop("/", this.floatType, this.floatType, this.floatType, 110);
      this.enterBinop("/", this.longType, this.longType, this.longType, 109);
      this.enterBinop("/", this.intType, this.intType, this.intType, 108);
      this.enterBinop("%", this.doubleType, this.doubleType, this.doubleType, 115);
      this.enterBinop("%", this.floatType, this.floatType, this.floatType, 114);
      this.enterBinop("%", this.longType, this.longType, this.longType, 113);
      this.enterBinop("%", this.intType, this.intType, this.intType, 112);
      this.enterBinop("&", this.booleanType, this.booleanType, this.booleanType, 126);
      this.enterBinop("&", this.longType, this.longType, this.longType, 127);
      this.enterBinop("&", this.intType, this.intType, this.intType, 126);
      this.enterBinop("|", this.booleanType, this.booleanType, this.booleanType, 128);
      this.enterBinop("|", this.longType, this.longType, this.longType, 129);
      this.enterBinop("|", this.intType, this.intType, this.intType, 128);
      this.enterBinop("^", this.booleanType, this.booleanType, this.booleanType, 130);
      this.enterBinop("^", this.longType, this.longType, this.longType, 131);
      this.enterBinop("^", this.intType, this.intType, this.intType, 130);
      this.enterBinop("<<", this.longType, this.longType, this.longType, 271);
      this.enterBinop("<<", this.intType, this.longType, this.intType, 270);
      this.enterBinop("<<", this.longType, this.intType, this.longType, 121);
      this.enterBinop("<<", this.intType, this.intType, this.intType, 120);
      this.enterBinop(">>", this.longType, this.longType, this.longType, 273);
      this.enterBinop(">>", this.intType, this.longType, this.intType, 272);
      this.enterBinop(">>", this.longType, this.intType, this.longType, 123);
      this.enterBinop(">>", this.intType, this.intType, this.intType, 122);
      this.enterBinop(">>>", this.longType, this.longType, this.longType, 275);
      this.enterBinop(">>>", this.intType, this.longType, this.intType, 274);
      this.enterBinop(">>>", this.longType, this.intType, this.longType, 125);
      this.enterBinop(">>>", this.intType, this.intType, this.intType, 124);
      this.enterBinop("<", this.doubleType, this.doubleType, this.booleanType, 152, 155);
      this.enterBinop("<", this.floatType, this.floatType, this.booleanType, 150, 155);
      this.enterBinop("<", this.longType, this.longType, this.booleanType, 148, 155);
      this.enterBinop("<", this.intType, this.intType, this.booleanType, 161);
      this.enterBinop(">", this.doubleType, this.doubleType, this.booleanType, 151, 157);
      this.enterBinop(">", this.floatType, this.floatType, this.booleanType, 149, 157);
      this.enterBinop(">", this.longType, this.longType, this.booleanType, 148, 157);
      this.enterBinop(">", this.intType, this.intType, this.booleanType, 163);
      this.enterBinop("<=", this.doubleType, this.doubleType, this.booleanType, 152, 158);
      this.enterBinop("<=", this.floatType, this.floatType, this.booleanType, 150, 158);
      this.enterBinop("<=", this.longType, this.longType, this.booleanType, 148, 158);
      this.enterBinop("<=", this.intType, this.intType, this.booleanType, 164);
      this.enterBinop(">=", this.doubleType, this.doubleType, this.booleanType, 151, 156);
      this.enterBinop(">=", this.floatType, this.floatType, this.booleanType, 149, 156);
      this.enterBinop(">=", this.longType, this.longType, this.booleanType, 148, 156);
      this.enterBinop(">=", this.intType, this.intType, this.booleanType, 162);
      this.enterBinop("==", this.objectType, this.objectType, this.booleanType, 165);
      this.enterBinop("==", this.booleanType, this.booleanType, this.booleanType, 159);
      this.enterBinop("==", this.doubleType, this.doubleType, this.booleanType, 151, 153);
      this.enterBinop("==", this.floatType, this.floatType, this.booleanType, 149, 153);
      this.enterBinop("==", this.longType, this.longType, this.booleanType, 148, 153);
      this.enterBinop("==", this.intType, this.intType, this.booleanType, 159);
      this.enterBinop("!=", this.objectType, this.objectType, this.booleanType, 166);
      this.enterBinop("!=", this.booleanType, this.booleanType, this.booleanType, 160);
      this.enterBinop("!=", this.doubleType, this.doubleType, this.booleanType, 151, 154);
      this.enterBinop("!=", this.floatType, this.floatType, this.booleanType, 149, 154);
      this.enterBinop("!=", this.longType, this.longType, this.booleanType, 148, 154);
      this.enterBinop("!=", this.intType, this.intType, this.booleanType, 160);
      this.enterBinop("&&", this.booleanType, this.booleanType, this.booleanType, 258);
      this.enterBinop("||", this.booleanType, this.booleanType, this.booleanType, 259);
   }
}
