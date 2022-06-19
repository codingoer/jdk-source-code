package com.sun.tools.javac.util;

public class Names {
   public static final Context.Key namesKey = new Context.Key();
   public final Name asterisk;
   public final Name comma;
   public final Name empty;
   public final Name hyphen;
   public final Name one;
   public final Name period;
   public final Name semicolon;
   public final Name slash;
   public final Name slashequals;
   public final Name _class;
   public final Name _default;
   public final Name _super;
   public final Name _this;
   public final Name _name;
   public final Name addSuppressed;
   public final Name any;
   public final Name append;
   public final Name clinit;
   public final Name clone;
   public final Name close;
   public final Name compareTo;
   public final Name deserializeLambda;
   public final Name desiredAssertionStatus;
   public final Name equals;
   public final Name error;
   public final Name family;
   public final Name finalize;
   public final Name forName;
   public final Name getClass;
   public final Name getClassLoader;
   public final Name getComponentType;
   public final Name getDeclaringClass;
   public final Name getMessage;
   public final Name hasNext;
   public final Name hashCode;
   public final Name init;
   public final Name initCause;
   public final Name iterator;
   public final Name length;
   public final Name next;
   public final Name ordinal;
   public final Name serialVersionUID;
   public final Name toString;
   public final Name value;
   public final Name valueOf;
   public final Name values;
   public final Name java_io_Serializable;
   public final Name java_lang_AutoCloseable;
   public final Name java_lang_Class;
   public final Name java_lang_Cloneable;
   public final Name java_lang_Enum;
   public final Name java_lang_Object;
   public final Name java_lang_invoke_MethodHandle;
   public final Name Array;
   public final Name Bound;
   public final Name Method;
   public final Name java_lang;
   public final Name Annotation;
   public final Name AnnotationDefault;
   public final Name BootstrapMethods;
   public final Name Bridge;
   public final Name CharacterRangeTable;
   public final Name Code;
   public final Name CompilationID;
   public final Name ConstantValue;
   public final Name Deprecated;
   public final Name EnclosingMethod;
   public final Name Enum;
   public final Name Exceptions;
   public final Name InnerClasses;
   public final Name LineNumberTable;
   public final Name LocalVariableTable;
   public final Name LocalVariableTypeTable;
   public final Name MethodParameters;
   public final Name RuntimeInvisibleAnnotations;
   public final Name RuntimeInvisibleParameterAnnotations;
   public final Name RuntimeInvisibleTypeAnnotations;
   public final Name RuntimeVisibleAnnotations;
   public final Name RuntimeVisibleParameterAnnotations;
   public final Name RuntimeVisibleTypeAnnotations;
   public final Name Signature;
   public final Name SourceFile;
   public final Name SourceID;
   public final Name StackMap;
   public final Name StackMapTable;
   public final Name Synthetic;
   public final Name Value;
   public final Name Varargs;
   public final Name ANNOTATION_TYPE;
   public final Name CONSTRUCTOR;
   public final Name FIELD;
   public final Name LOCAL_VARIABLE;
   public final Name METHOD;
   public final Name PACKAGE;
   public final Name PARAMETER;
   public final Name TYPE;
   public final Name TYPE_PARAMETER;
   public final Name TYPE_USE;
   public final Name CLASS;
   public final Name RUNTIME;
   public final Name SOURCE;
   public final Name T;
   public final Name deprecated;
   public final Name ex;
   public final Name package_info;
   public final Name lambda;
   public final Name metafactory;
   public final Name altMetafactory;
   public final Name dollarThis;
   public final Name.Table table;

   public static Names instance(Context var0) {
      Names var1 = (Names)var0.get(namesKey);
      if (var1 == null) {
         var1 = new Names(var0);
         var0.put((Context.Key)namesKey, (Object)var1);
      }

      return var1;
   }

   public Names(Context var1) {
      Options var2 = Options.instance(var1);
      this.table = this.createTable(var2);
      this.asterisk = this.fromString("*");
      this.comma = this.fromString(",");
      this.empty = this.fromString("");
      this.hyphen = this.fromString("-");
      this.one = this.fromString("1");
      this.period = this.fromString(".");
      this.semicolon = this.fromString(";");
      this.slash = this.fromString("/");
      this.slashequals = this.fromString("/=");
      this._class = this.fromString("class");
      this._default = this.fromString("default");
      this._super = this.fromString("super");
      this._this = this.fromString("this");
      this._name = this.fromString("name");
      this.addSuppressed = this.fromString("addSuppressed");
      this.any = this.fromString("<any>");
      this.append = this.fromString("append");
      this.clinit = this.fromString("<clinit>");
      this.clone = this.fromString("clone");
      this.close = this.fromString("close");
      this.compareTo = this.fromString("compareTo");
      this.deserializeLambda = this.fromString("$deserializeLambda$");
      this.desiredAssertionStatus = this.fromString("desiredAssertionStatus");
      this.equals = this.fromString("equals");
      this.error = this.fromString("<error>");
      this.family = this.fromString("family");
      this.finalize = this.fromString("finalize");
      this.forName = this.fromString("forName");
      this.getClass = this.fromString("getClass");
      this.getClassLoader = this.fromString("getClassLoader");
      this.getComponentType = this.fromString("getComponentType");
      this.getDeclaringClass = this.fromString("getDeclaringClass");
      this.getMessage = this.fromString("getMessage");
      this.hasNext = this.fromString("hasNext");
      this.hashCode = this.fromString("hashCode");
      this.init = this.fromString("<init>");
      this.initCause = this.fromString("initCause");
      this.iterator = this.fromString("iterator");
      this.length = this.fromString("length");
      this.next = this.fromString("next");
      this.ordinal = this.fromString("ordinal");
      this.serialVersionUID = this.fromString("serialVersionUID");
      this.toString = this.fromString("toString");
      this.value = this.fromString("value");
      this.valueOf = this.fromString("valueOf");
      this.values = this.fromString("values");
      this.dollarThis = this.fromString("$this");
      this.java_io_Serializable = this.fromString("java.io.Serializable");
      this.java_lang_AutoCloseable = this.fromString("java.lang.AutoCloseable");
      this.java_lang_Class = this.fromString("java.lang.Class");
      this.java_lang_Cloneable = this.fromString("java.lang.Cloneable");
      this.java_lang_Enum = this.fromString("java.lang.Enum");
      this.java_lang_Object = this.fromString("java.lang.Object");
      this.java_lang_invoke_MethodHandle = this.fromString("java.lang.invoke.MethodHandle");
      this.Array = this.fromString("Array");
      this.Bound = this.fromString("Bound");
      this.Method = this.fromString("Method");
      this.java_lang = this.fromString("java.lang");
      this.Annotation = this.fromString("Annotation");
      this.AnnotationDefault = this.fromString("AnnotationDefault");
      this.BootstrapMethods = this.fromString("BootstrapMethods");
      this.Bridge = this.fromString("Bridge");
      this.CharacterRangeTable = this.fromString("CharacterRangeTable");
      this.Code = this.fromString("Code");
      this.CompilationID = this.fromString("CompilationID");
      this.ConstantValue = this.fromString("ConstantValue");
      this.Deprecated = this.fromString("Deprecated");
      this.EnclosingMethod = this.fromString("EnclosingMethod");
      this.Enum = this.fromString("Enum");
      this.Exceptions = this.fromString("Exceptions");
      this.InnerClasses = this.fromString("InnerClasses");
      this.LineNumberTable = this.fromString("LineNumberTable");
      this.LocalVariableTable = this.fromString("LocalVariableTable");
      this.LocalVariableTypeTable = this.fromString("LocalVariableTypeTable");
      this.MethodParameters = this.fromString("MethodParameters");
      this.RuntimeInvisibleAnnotations = this.fromString("RuntimeInvisibleAnnotations");
      this.RuntimeInvisibleParameterAnnotations = this.fromString("RuntimeInvisibleParameterAnnotations");
      this.RuntimeInvisibleTypeAnnotations = this.fromString("RuntimeInvisibleTypeAnnotations");
      this.RuntimeVisibleAnnotations = this.fromString("RuntimeVisibleAnnotations");
      this.RuntimeVisibleParameterAnnotations = this.fromString("RuntimeVisibleParameterAnnotations");
      this.RuntimeVisibleTypeAnnotations = this.fromString("RuntimeVisibleTypeAnnotations");
      this.Signature = this.fromString("Signature");
      this.SourceFile = this.fromString("SourceFile");
      this.SourceID = this.fromString("SourceID");
      this.StackMap = this.fromString("StackMap");
      this.StackMapTable = this.fromString("StackMapTable");
      this.Synthetic = this.fromString("Synthetic");
      this.Value = this.fromString("Value");
      this.Varargs = this.fromString("Varargs");
      this.ANNOTATION_TYPE = this.fromString("ANNOTATION_TYPE");
      this.CONSTRUCTOR = this.fromString("CONSTRUCTOR");
      this.FIELD = this.fromString("FIELD");
      this.LOCAL_VARIABLE = this.fromString("LOCAL_VARIABLE");
      this.METHOD = this.fromString("METHOD");
      this.PACKAGE = this.fromString("PACKAGE");
      this.PARAMETER = this.fromString("PARAMETER");
      this.TYPE = this.fromString("TYPE");
      this.TYPE_PARAMETER = this.fromString("TYPE_PARAMETER");
      this.TYPE_USE = this.fromString("TYPE_USE");
      this.CLASS = this.fromString("CLASS");
      this.RUNTIME = this.fromString("RUNTIME");
      this.SOURCE = this.fromString("SOURCE");
      this.T = this.fromString("T");
      this.deprecated = this.fromString("deprecated");
      this.ex = this.fromString("ex");
      this.package_info = this.fromString("package-info");
      this.lambda = this.fromString("lambda$");
      this.metafactory = this.fromString("metafactory");
      this.altMetafactory = this.fromString("altMetafactory");
   }

   protected Name.Table createTable(Options var1) {
      boolean var2 = var1.isSet("useUnsharedTable");
      return (Name.Table)(var2 ? new UnsharedNameTable(this) : new SharedNameTable(this));
   }

   public void dispose() {
      this.table.dispose();
   }

   public Name fromChars(char[] var1, int var2, int var3) {
      return this.table.fromChars(var1, var2, var3);
   }

   public Name fromString(String var1) {
      return this.table.fromString(var1);
   }

   public Name fromUtf(byte[] var1) {
      return this.table.fromUtf(var1);
   }

   public Name fromUtf(byte[] var1, int var2, int var3) {
      return this.table.fromUtf(var1, var2, var3);
   }
}
