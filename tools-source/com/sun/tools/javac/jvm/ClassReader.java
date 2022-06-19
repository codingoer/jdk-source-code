package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TargetType;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeAnnotationPosition;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.file.BaseFileObject;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.Option;
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
import com.sun.tools.javac.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class ClassReader {
   protected static final Context.Key classReaderKey = new Context.Key();
   public static final int INITIAL_BUFFER_SIZE = 65520;
   Annotate annotate;
   boolean verbose;
   boolean checkClassFile;
   public boolean readAllOfClassFile = false;
   boolean allowGenerics;
   boolean allowVarargs;
   boolean allowAnnotations;
   boolean allowSimplifiedVarargs;
   boolean lintClassfile;
   public boolean saveParameterNames;
   private boolean cacheCompletionFailure;
   public boolean preferSource;
   public final Profile profile;
   final Log log;
   Symtab syms;
   Types types;
   final Names names;
   final Name completionFailureName;
   private final JavaFileManager fileManager;
   JCDiagnostic.Factory diagFactory;
   public SourceCompleter sourceCompleter = null;
   private Map classes;
   private Map packages;
   protected Scope typevars;
   protected JavaFileObject currentClassFile = null;
   protected Symbol currentOwner = null;
   byte[] buf = new byte['\ufff0'];
   protected int bp;
   Object[] poolObj;
   int[] poolIdx;
   int majorVersion;
   int minorVersion;
   int[] parameterNameIndices;
   boolean haveParameterNameIndices;
   boolean sawMethodParameters;
   Set warnedAttrs = new HashSet();
   private final Symbol.Completer thisCompleter = new Symbol.Completer() {
      public void complete(Symbol var1) throws Symbol.CompletionFailure {
         ClassReader.this.complete(var1);
      }
   };
   byte[] signature;
   int sigp;
   int siglimit;
   boolean sigEnterPhase = false;
   byte[] signatureBuffer = new byte[0];
   int sbp = 0;
   protected Set CLASS_ATTRIBUTE;
   protected Set MEMBER_ATTRIBUTE;
   protected Set CLASS_OR_MEMBER_ATTRIBUTE;
   protected Map attributeReaders;
   private boolean readingClassAttr;
   private List missingTypeVariables;
   private List foundTypeVariables;
   private boolean filling;
   private Symbol.CompletionFailure cachedCompletionFailure;
   protected JavaFileManager.Location currentLoc;
   private boolean verbosePath;

   public static ClassReader instance(Context var0) {
      ClassReader var1 = (ClassReader)var0.get(classReaderKey);
      if (var1 == null) {
         var1 = new ClassReader(var0, true);
      }

      return var1;
   }

   public void init(Symtab var1) {
      this.init(var1, true);
   }

   private void init(Symtab var1, boolean var2) {
      if (this.classes == null) {
         if (var2) {
            Assert.check(this.packages == null || this.packages == var1.packages);
            this.packages = var1.packages;
            Assert.check(this.classes == null || this.classes == var1.classes);
            this.classes = var1.classes;
         } else {
            this.packages = new HashMap();
            this.classes = new HashMap();
         }

         this.packages.put(this.names.empty, var1.rootPackage);
         var1.rootPackage.completer = this.thisCompleter;
         var1.unnamedPackage.completer = this.thisCompleter;
      }
   }

   protected ClassReader(Context var1, boolean var2) {
      this.CLASS_ATTRIBUTE = EnumSet.of(ClassReader.AttributeKind.CLASS);
      this.MEMBER_ATTRIBUTE = EnumSet.of(ClassReader.AttributeKind.MEMBER);
      this.CLASS_OR_MEMBER_ATTRIBUTE = EnumSet.of(ClassReader.AttributeKind.CLASS, ClassReader.AttributeKind.MEMBER);
      this.attributeReaders = new HashMap();
      this.readingClassAttr = false;
      this.missingTypeVariables = List.nil();
      this.foundTypeVariables = List.nil();
      this.filling = false;
      this.cachedCompletionFailure = new Symbol.CompletionFailure((Symbol)null, (JCDiagnostic)null);
      this.cachedCompletionFailure.setStackTrace(new StackTraceElement[0]);
      this.verbosePath = true;
      if (var2) {
         var1.put((Context.Key)classReaderKey, (Object)this);
      }

      this.names = Names.instance(var1);
      this.syms = Symtab.instance(var1);
      this.types = Types.instance(var1);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      if (this.fileManager == null) {
         throw new AssertionError("FileManager initialization error");
      } else {
         this.diagFactory = JCDiagnostic.Factory.instance(var1);
         this.init(this.syms, var2);
         this.log = Log.instance(var1);
         Options var3 = Options.instance(var1);
         this.annotate = Annotate.instance(var1);
         this.verbose = var3.isSet(Option.VERBOSE);
         this.checkClassFile = var3.isSet("-checkclassfile");
         Source var4 = Source.instance(var1);
         this.allowGenerics = var4.allowGenerics();
         this.allowVarargs = var4.allowVarargs();
         this.allowAnnotations = var4.allowAnnotations();
         this.allowSimplifiedVarargs = var4.allowSimplifiedVarargs();
         this.saveParameterNames = var3.isSet("save-parameter-names");
         this.cacheCompletionFailure = var3.isUnset("dev");
         this.preferSource = "source".equals(var3.get("-Xprefer"));
         this.profile = Profile.instance(var1);
         this.completionFailureName = var3.isSet("failcomplete") ? this.names.fromString(var3.get("failcomplete")) : null;
         this.typevars = new Scope(this.syms.noSymbol);
         this.lintClassfile = Lint.instance(var1).isEnabled(Lint.LintCategory.CLASSFILE);
         this.initAttributeReaders();
      }
   }

   private void enterMember(Symbol.ClassSymbol var1, Symbol var2) {
      if ((var2.flags_field & 2147487744L) != 4096L || var2.name.startsWith(this.names.lambda)) {
         var1.members_field.enter(var2);
      }

   }

   private JCDiagnostic createBadClassFileDiagnostic(JavaFileObject var1, JCDiagnostic var2) {
      String var3 = var1.getKind() == Kind.SOURCE ? "bad.source.file.header" : "bad.class.file.header";
      return this.diagFactory.fragment(var3, var1, var2);
   }

   public BadClassFile badClassFile(String var1, Object... var2) {
      return new BadClassFile(this.currentOwner.enclClass(), this.currentClassFile, this.diagFactory.fragment(var1, var2));
   }

   char nextChar() {
      return (char)(((this.buf[this.bp++] & 255) << 8) + (this.buf[this.bp++] & 255));
   }

   int nextByte() {
      return this.buf[this.bp++] & 255;
   }

   int nextInt() {
      return ((this.buf[this.bp++] & 255) << 24) + ((this.buf[this.bp++] & 255) << 16) + ((this.buf[this.bp++] & 255) << 8) + (this.buf[this.bp++] & 255);
   }

   char getChar(int var1) {
      return (char)(((this.buf[var1] & 255) << 8) + (this.buf[var1 + 1] & 255));
   }

   int getInt(int var1) {
      return ((this.buf[var1] & 255) << 24) + ((this.buf[var1 + 1] & 255) << 16) + ((this.buf[var1 + 2] & 255) << 8) + (this.buf[var1 + 3] & 255);
   }

   long getLong(int var1) {
      DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(this.buf, var1, 8));

      try {
         return var2.readLong();
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }
   }

   float getFloat(int var1) {
      DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(this.buf, var1, 4));

      try {
         return var2.readFloat();
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }
   }

   double getDouble(int var1) {
      DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(this.buf, var1, 8));

      try {
         return var2.readDouble();
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }
   }

   void indexPool() {
      this.poolIdx = new int[this.nextChar()];
      this.poolObj = new Object[this.poolIdx.length];
      int var1 = 1;

      while(var1 < this.poolIdx.length) {
         this.poolIdx[var1++] = this.bp;
         byte var2 = this.buf[this.bp++];
         switch (var2) {
            case 1:
            case 2:
               char var3 = this.nextChar();
               this.bp += var3;
               break;
            case 3:
            case 4:
            case 9:
            case 10:
            case 11:
            case 12:
            case 18:
               this.bp += 4;
               break;
            case 5:
            case 6:
               this.bp += 8;
               ++var1;
               break;
            case 7:
            case 8:
            case 16:
               this.bp += 2;
               break;
            case 13:
            case 14:
            case 17:
            default:
               throw this.badClassFile("bad.const.pool.tag.at", Byte.toString(var2), Integer.toString(this.bp - 1));
            case 15:
               this.bp += 3;
         }
      }

   }

   Object readPool(int var1) {
      Object var2 = this.poolObj[var1];
      if (var2 != null) {
         return var2;
      } else {
         int var3 = this.poolIdx[var1];
         if (var3 == 0) {
            return null;
         } else {
            byte var4 = this.buf[var3];
            Symbol.ClassSymbol var5;
            ClassFile.NameAndType var6;
            switch (var4) {
               case 1:
                  this.poolObj[var1] = this.names.fromUtf(this.buf, var3 + 3, this.getChar(var3 + 1));
                  break;
               case 2:
                  throw this.badClassFile("unicode.str.not.supported");
               case 3:
                  this.poolObj[var1] = this.getInt(var3 + 1);
                  break;
               case 4:
                  this.poolObj[var1] = new Float(this.getFloat(var3 + 1));
                  break;
               case 5:
                  this.poolObj[var1] = new Long(this.getLong(var3 + 1));
                  break;
               case 6:
                  this.poolObj[var1] = new Double(this.getDouble(var3 + 1));
                  break;
               case 7:
                  this.poolObj[var1] = this.readClassOrType(this.getChar(var3 + 1));
                  break;
               case 8:
                  this.poolObj[var1] = this.readName(this.getChar(var3 + 1)).toString();
                  break;
               case 9:
                  var5 = this.readClassSymbol(this.getChar(var3 + 1));
                  var6 = this.readNameAndType(this.getChar(var3 + 3));
                  this.poolObj[var1] = new Symbol.VarSymbol(0L, var6.name, var6.uniqueType.type, var5);
                  break;
               case 10:
               case 11:
                  var5 = this.readClassSymbol(this.getChar(var3 + 1));
                  var6 = this.readNameAndType(this.getChar(var3 + 3));
                  this.poolObj[var1] = new Symbol.MethodSymbol(0L, var6.name, var6.uniqueType.type, var5);
                  break;
               case 12:
                  this.poolObj[var1] = new ClassFile.NameAndType(this.readName(this.getChar(var3 + 1)), this.readType(this.getChar(var3 + 3)), this.types);
                  break;
               case 13:
               case 14:
               case 17:
               default:
                  throw this.badClassFile("bad.const.pool.tag", Byte.toString(var4));
               case 15:
                  this.skipBytes(4);
                  break;
               case 16:
                  this.skipBytes(3);
                  break;
               case 18:
                  this.skipBytes(5);
            }

            return this.poolObj[var1];
         }
      }
   }

   Type readType(int var1) {
      int var2 = this.poolIdx[var1];
      return this.sigToType(this.buf, var2 + 3, this.getChar(var2 + 1));
   }

   Object readClassOrType(int var1) {
      int var2 = this.poolIdx[var1];
      char var3 = this.getChar(var2 + 1);
      int var4 = var2 + 3;
      Assert.check(this.buf[var4] == 91 || this.buf[var4 + var3 - 1] != 59);
      return this.buf[var4] != 91 && this.buf[var4 + var3 - 1] != 59 ? this.enterClass(this.names.fromUtf(ClassFile.internalize(this.buf, var4, var3))) : this.sigToType(this.buf, var4, var3);
   }

   List readTypeParams(int var1) {
      int var2 = this.poolIdx[var1];
      return this.sigToTypeParams(this.buf, var2 + 3, this.getChar(var2 + 1));
   }

   Symbol.ClassSymbol readClassSymbol(int var1) {
      Object var2 = this.readPool(var1);
      if (var2 != null && !(var2 instanceof Symbol.ClassSymbol)) {
         throw this.badClassFile("bad.const.pool.entry", this.currentClassFile.toString(), "CONSTANT_Class_info", var1);
      } else {
         return (Symbol.ClassSymbol)var2;
      }
   }

   Name readName(int var1) {
      Object var2 = this.readPool(var1);
      if (var2 != null && !(var2 instanceof Name)) {
         throw this.badClassFile("bad.const.pool.entry", this.currentClassFile.toString(), "CONSTANT_Utf8_info or CONSTANT_String_info", var1);
      } else {
         return (Name)var2;
      }
   }

   ClassFile.NameAndType readNameAndType(int var1) {
      Object var2 = this.readPool(var1);
      if (var2 != null && !(var2 instanceof ClassFile.NameAndType)) {
         throw this.badClassFile("bad.const.pool.entry", this.currentClassFile.toString(), "CONSTANT_NameAndType_info", var1);
      } else {
         return (ClassFile.NameAndType)var2;
      }
   }

   Type sigToType(byte[] var1, int var2, int var3) {
      this.signature = var1;
      this.sigp = var2;
      this.siglimit = var2 + var3;
      return this.sigToType();
   }

   Type sigToType() {
      Type var2;
      switch ((char)this.signature[this.sigp]) {
         case '(':
            ++this.sigp;
            List var6 = this.sigToTypes(')');
            Type var3 = this.sigToType();

            List var4;
            for(var4 = List.nil(); this.signature[this.sigp] == 94; var4 = var4.prepend(this.sigToType())) {
               ++this.sigp;
            }

            for(List var7 = var4; var7.nonEmpty(); var7 = var7.tail) {
               if (((Type)var7.head).hasTag(TypeTag.TYPEVAR)) {
                  Symbol.TypeSymbol var10000 = ((Type)var7.head).tsym;
                  var10000.flags_field |= 140737488355328L;
               }
            }

            return new Type.MethodType(var6, var3, var4.reverse(), this.syms.methodClass);
         case ')':
         case ',':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw this.badClassFile("bad.signature", Convert.utf2string(this.signature, this.sigp, 10));
         case '*':
            ++this.sigp;
            return new Type.WildcardType(this.syms.objectType, BoundKind.UNBOUND, this.syms.boundClass);
         case '+':
            ++this.sigp;
            var2 = this.sigToType();
            return new Type.WildcardType(var2, BoundKind.EXTENDS, this.syms.boundClass);
         case '-':
            ++this.sigp;
            var2 = this.sigToType();
            return new Type.WildcardType(var2, BoundKind.SUPER, this.syms.boundClass);
         case '<':
            this.typevars = this.typevars.dup(this.currentOwner);
            Type.ForAll var5 = new Type.ForAll(this.sigToTypeParams(), this.sigToType());
            this.typevars = this.typevars.leave();
            return var5;
         case 'B':
            ++this.sigp;
            return this.syms.byteType;
         case 'C':
            ++this.sigp;
            return this.syms.charType;
         case 'D':
            ++this.sigp;
            return this.syms.doubleType;
         case 'F':
            ++this.sigp;
            return this.syms.floatType;
         case 'I':
            ++this.sigp;
            return this.syms.intType;
         case 'J':
            ++this.sigp;
            return this.syms.longType;
         case 'L':
            var2 = this.classSigToType();
            if (this.sigp < this.siglimit && this.signature[this.sigp] == 46) {
               throw this.badClassFile("deprecated inner class signature syntax (please recompile from source)");
            }

            return var2;
         case 'S':
            ++this.sigp;
            return this.syms.shortType;
         case 'T':
            ++this.sigp;

            int var1;
            for(var1 = this.sigp; this.signature[this.sigp] != 59; ++this.sigp) {
            }

            ++this.sigp;
            return (Type)(this.sigEnterPhase ? Type.noType : this.findTypeVar(this.names.fromUtf(this.signature, var1, this.sigp - 1 - var1)));
         case 'V':
            ++this.sigp;
            return this.syms.voidType;
         case 'Z':
            ++this.sigp;
            return this.syms.booleanType;
         case '[':
            ++this.sigp;
            return new Type.ArrayType(this.sigToType(), this.syms.arrayClass);
      }
   }

   Type classSigToType() {
      if (this.signature[this.sigp] != 76) {
         throw this.badClassFile("bad.class.signature", Convert.utf2string(this.signature, this.sigp, 10));
      } else {
         ++this.sigp;
         Object var1 = Type.noType;
         int var2 = this.sbp;

         while(true) {
            byte var3 = this.signature[this.sigp++];
            Symbol.ClassSymbol var4;
            switch (var3) {
               case 46:
                  if (var1 != Type.noType) {
                     var4 = this.enterClass(this.names.fromUtf(this.signatureBuffer, var2, this.sbp - var2));
                     var1 = new Type.ClassType((Type)var1, List.nil(), var4);
                  }

                  this.signatureBuffer[this.sbp++] = 36;
                  break;
               case 47:
                  this.signatureBuffer[this.sbp++] = 46;
                  break;
               case 59:
                  var4 = this.enterClass(this.names.fromUtf(this.signatureBuffer, var2, this.sbp - var2));

                  Object var5;
                  try {
                     var5 = var1 == Type.noType ? var4.erasure(this.types) : new Type.ClassType((Type)var1, List.nil(), var4);
                  } finally {
                     this.sbp = var2;
                  }

                  return (Type)var5;
               case 60:
                  var4 = this.enterClass(this.names.fromUtf(this.signatureBuffer, var2, this.sbp - var2));
                  var1 = new Type.ClassType((Type)var1, this.sigToTypes('>'), var4) {
                     boolean completed = false;

                     public Type getEnclosingType() {
                        if (!this.completed) {
                           this.completed = true;
                           this.tsym.complete();
                           Type var1 = this.tsym.type.getEnclosingType();
                           if (var1 != Type.noType) {
                              List var2 = super.getEnclosingType().allparams();
                              List var3 = var1.allparams();
                              if (var3.length() != var2.length()) {
                                 super.setEnclosingType(ClassReader.this.types.erasure(var1));
                              } else {
                                 super.setEnclosingType(ClassReader.this.types.subst(var1, var3, var2));
                              }
                           } else {
                              super.setEnclosingType(Type.noType);
                           }
                        }

                        return super.getEnclosingType();
                     }

                     public void setEnclosingType(Type var1) {
                        throw new UnsupportedOperationException();
                     }
                  };
                  switch (this.signature[this.sigp++]) {
                     case 46:
                        this.signatureBuffer[this.sbp++] = 36;
                        continue;
                     case 59:
                        if (this.sigp < this.signature.length && this.signature[this.sigp] == 46) {
                           this.sigp += this.sbp - var2 + 3;
                           this.signatureBuffer[this.sbp++] = 36;
                           continue;
                        }

                        this.sbp = var2;
                        return (Type)var1;
                     default:
                        throw new AssertionError(this.signature[this.sigp - 1]);
                  }
               default:
                  this.signatureBuffer[this.sbp++] = var3;
            }
         }
      }
   }

   List sigToTypes(char var1) {
      List var2 = List.of((Object)null);

      for(List var3 = var2; this.signature[this.sigp] != var1; var3 = var3.setTail(List.of(this.sigToType()))) {
      }

      ++this.sigp;
      return var2.tail;
   }

   List sigToTypeParams(byte[] var1, int var2, int var3) {
      this.signature = var1;
      this.sigp = var2;
      this.siglimit = var2 + var3;
      return this.sigToTypeParams();
   }

   List sigToTypeParams() {
      List var1 = List.nil();
      if (this.signature[this.sigp] == 60) {
         ++this.sigp;
         int var2 = this.sigp;

         for(this.sigEnterPhase = true; this.signature[this.sigp] != 62; var1 = var1.prepend(this.sigToTypeParam())) {
         }

         this.sigEnterPhase = false;
         this.sigp = var2;

         while(this.signature[this.sigp] != 62) {
            this.sigToTypeParam();
         }

         ++this.sigp;
      }

      return var1.reverse();
   }

   Type sigToTypeParam() {
      int var1;
      for(var1 = this.sigp; this.signature[this.sigp] != 58; ++this.sigp) {
      }

      Name var2 = this.names.fromUtf(this.signature, var1, this.sigp - var1);
      Type.TypeVar var3;
      if (this.sigEnterPhase) {
         var3 = new Type.TypeVar(var2, this.currentOwner, this.syms.botType);
         this.typevars.enter(var3.tsym);
      } else {
         var3 = (Type.TypeVar)this.findTypeVar(var2);
      }

      List var4 = List.nil();
      boolean var5 = false;
      if (this.signature[this.sigp] == 58 && this.signature[this.sigp + 1] == 58) {
         ++this.sigp;
         var5 = true;
      }

      while(this.signature[this.sigp] == 58) {
         ++this.sigp;
         var4 = var4.prepend(this.sigToType());
      }

      if (!this.sigEnterPhase) {
         this.types.setBounds(var3, var4.reverse(), var5);
      }

      return var3;
   }

   Type findTypeVar(Name var1) {
      Scope.Entry var2 = this.typevars.lookup(var1);
      if (var2.scope != null) {
         return var2.sym.type;
      } else if (this.readingClassAttr) {
         Type.TypeVar var3 = new Type.TypeVar(var1, this.currentOwner, this.syms.botType);
         this.missingTypeVariables = this.missingTypeVariables.prepend(var3);
         return var3;
      } else {
         throw this.badClassFile("undecl.type.var", var1);
      }
   }

   private void initAttributeReaders() {
      AttributeReader[] var1 = new AttributeReader[]{new AttributeReader(this.names.Code, ClassFile.Version.V45_3, this.MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            if (!ClassReader.this.readAllOfClassFile && !ClassReader.this.saveParameterNames) {
               ClassReader.this.bp += var2;
            } else {
               ((Symbol.MethodSymbol)var1).code = ClassReader.this.readCode(var1);
            }

         }
      }, new AttributeReader(this.names.ConstantValue, ClassFile.Version.V45_3, this.MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            Object var3 = ClassReader.this.readPool(ClassReader.this.nextChar());
            if ((var1.flags() & 16L) != 0L) {
               ((Symbol.VarSymbol)var1).setData(var3);
            }

         }
      }, new AttributeReader(this.names.Deprecated, ClassFile.Version.V45_3, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            var1.flags_field |= 131072L;
         }
      }, new AttributeReader(this.names.Exceptions, ClassFile.Version.V45_3, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            char var3 = ClassReader.this.nextChar();
            List var4 = List.nil();

            for(int var5 = 0; var5 < var3; ++var5) {
               var4 = var4.prepend(ClassReader.this.readClassSymbol(ClassReader.this.nextChar()).type);
            }

            if (var1.type.getThrownTypes().isEmpty()) {
               var1.type.asMethodType().thrown = var4.reverse();
            }

         }
      }, new AttributeReader(this.names.InnerClasses, ClassFile.Version.V45_3, this.CLASS_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var1;
            ClassReader.this.readInnerClasses(var3);
         }
      }, new AttributeReader(this.names.LocalVariableTable, ClassFile.Version.V45_3, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            int var3 = ClassReader.this.bp + var2;
            if (ClassReader.this.saveParameterNames && !ClassReader.this.sawMethodParameters) {
               char var4 = ClassReader.this.nextChar();

               for(int var5 = 0; var5 < var4; ++var5) {
                  char var6 = ClassReader.this.nextChar();
                  char var7 = ClassReader.this.nextChar();
                  char var8 = ClassReader.this.nextChar();
                  char var9 = ClassReader.this.nextChar();
                  char var10 = ClassReader.this.nextChar();
                  if (var6 == 0) {
                     if (var10 >= ClassReader.this.parameterNameIndices.length) {
                        int var11 = Math.max(var10, ClassReader.this.parameterNameIndices.length + 8);
                        ClassReader.this.parameterNameIndices = Arrays.copyOf(ClassReader.this.parameterNameIndices, var11);
                     }

                     ClassReader.this.parameterNameIndices[var10] = var8;
                     ClassReader.this.haveParameterNameIndices = true;
                  }
               }
            }

            ClassReader.this.bp = var3;
         }
      }, new AttributeReader(this.names.MethodParameters, ClassFile.Version.V52, this.MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            int var3 = ClassReader.this.bp + var2;
            if (ClassReader.this.saveParameterNames) {
               ClassReader.this.sawMethodParameters = true;
               int var4 = ClassReader.this.nextByte();
               ClassReader.this.parameterNameIndices = new int[var4];
               ClassReader.this.haveParameterNameIndices = true;

               for(int var5 = 0; var5 < var4; ++var5) {
                  char var6 = ClassReader.this.nextChar();
                  char var7 = ClassReader.this.nextChar();
                  ClassReader.this.parameterNameIndices[var5] = var6;
               }
            }

            ClassReader.this.bp = var3;
         }
      }, new AttributeReader(this.names.SourceFile, ClassFile.Version.V45_3, this.CLASS_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var1;
            Name var4 = ClassReader.this.readName(ClassReader.this.nextChar());
            var3.sourcefile = new SourceFileObject(var4, var3.flatname);
            String var5 = var4.toString();
            if (var3.owner.kind == 1 && var5.endsWith(".java") && !var5.equals(var3.name.toString() + ".java")) {
               var3.flags_field |= 17592186044416L;
            }

         }
      }, new AttributeReader(this.names.Synthetic, ClassFile.Version.V45_3, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            if (ClassReader.this.allowGenerics || (var1.flags_field & 2147483648L) == 0L) {
               var1.flags_field |= 4096L;
            }

         }
      }, new AttributeReader(this.names.EnclosingMethod, ClassFile.Version.V49, this.CLASS_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            int var3 = ClassReader.this.bp + var2;
            ClassReader.this.readEnclosingMethodAttr(var1);
            ClassReader.this.bp = var3;
         }
      }, new AttributeReader(this.names.Signature, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected boolean accepts(AttributeKind var1) {
            return super.accepts(var1) && ClassReader.this.allowGenerics;
         }

         protected void read(Symbol var1, int var2) {
            if (var1.kind == 2) {
               Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var1;
               ClassReader.this.readingClassAttr = true;

               try {
                  Type.ClassType var4 = (Type.ClassType)var3.type;
                  Assert.check(var3 == ClassReader.this.currentOwner);
                  var4.typarams_field = ClassReader.this.readTypeParams(ClassReader.this.nextChar());
                  var4.supertype_field = ClassReader.this.sigToType();
                  ListBuffer var5 = new ListBuffer();

                  while(ClassReader.this.sigp != ClassReader.this.siglimit) {
                     var5.append(ClassReader.this.sigToType());
                  }

                  var4.interfaces_field = var5.toList();
               } finally {
                  ClassReader.this.readingClassAttr = false;
               }
            } else {
               List var9 = var1.type.getThrownTypes();
               var1.type = ClassReader.this.readType(ClassReader.this.nextChar());
               if (var1.kind == 16 && var1.type.getThrownTypes().isEmpty()) {
                  var1.type.asMethodType().thrown = var9;
               }
            }

         }
      }, new AttributeReader(this.names.AnnotationDefault, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachAnnotationDefault(var1);
         }
      }, new AttributeReader(this.names.RuntimeInvisibleAnnotations, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachAnnotations(var1);
         }
      }, new AttributeReader(this.names.RuntimeInvisibleParameterAnnotations, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachParameterAnnotations(var1);
         }
      }, new AttributeReader(this.names.RuntimeVisibleAnnotations, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachAnnotations(var1);
         }
      }, new AttributeReader(this.names.RuntimeVisibleParameterAnnotations, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachParameterAnnotations(var1);
         }
      }, new AttributeReader(this.names.Annotation, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            if (ClassReader.this.allowAnnotations) {
               var1.flags_field |= 8192L;
            }

         }
      }, new AttributeReader(this.names.Bridge, ClassFile.Version.V49, this.MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            var1.flags_field |= 2147483648L;
            if (!ClassReader.this.allowGenerics) {
               var1.flags_field &= -4097L;
            }

         }
      }, new AttributeReader(this.names.Enum, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            var1.flags_field |= 16384L;
         }
      }, new AttributeReader(this.names.Varargs, ClassFile.Version.V49, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            if (ClassReader.this.allowVarargs) {
               var1.flags_field |= 17179869184L;
            }

         }
      }, new AttributeReader(this.names.RuntimeVisibleTypeAnnotations, ClassFile.Version.V52, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachTypeAnnotations(var1);
         }
      }, new AttributeReader(this.names.RuntimeInvisibleTypeAnnotations, ClassFile.Version.V52, this.CLASS_OR_MEMBER_ATTRIBUTE) {
         protected void read(Symbol var1, int var2) {
            ClassReader.this.attachTypeAnnotations(var1);
         }
      }};
      AttributeReader[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AttributeReader var5 = var2[var4];
         this.attributeReaders.put(var5.name, var5);
      }

   }

   void unrecognized(Name var1) {
      if (this.checkClassFile) {
         this.printCCF("ccf.unrecognized.attribute", var1);
      }

   }

   protected void readEnclosingMethodAttr(Symbol var1) {
      var1.owner.members().remove(var1);
      Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1;
      Symbol.ClassSymbol var3 = this.readClassSymbol(this.nextChar());
      ClassFile.NameAndType var4 = this.readNameAndType(this.nextChar());
      if (var3.members_field == null) {
         throw this.badClassFile("bad.enclosing.class", var2, var3);
      } else {
         Symbol.MethodSymbol var5 = this.findMethod(var4, var3.members_field, var2.flags());
         if (var4 != null && var5 == null) {
            throw this.badClassFile("bad.enclosing.method", var2);
         } else {
            var2.name = this.simpleBinaryName(var2.flatname, var3.flatname);
            var2.owner = (Symbol)(var5 != null ? var5 : var3);
            if (var2.name.isEmpty()) {
               var2.fullname = this.names.empty;
            } else {
               var2.fullname = Symbol.ClassSymbol.formFullName(var2.name, var2.owner);
            }

            if (var5 != null) {
               ((Type.ClassType)var1.type).setEnclosingType(var5.type);
            } else if ((var2.flags_field & 8L) == 0L) {
               ((Type.ClassType)var1.type).setEnclosingType(var3.type);
            } else {
               ((Type.ClassType)var1.type).setEnclosingType(Type.noType);
            }

            this.enterTypevars((Symbol)var2);
            if (!this.missingTypeVariables.isEmpty()) {
               ListBuffer var6 = new ListBuffer();
               Iterator var7 = this.missingTypeVariables.iterator();

               while(var7.hasNext()) {
                  Type var8 = (Type)var7.next();
                  var6.append(this.findTypeVar(var8.tsym.name));
               }

               this.foundTypeVariables = var6.toList();
            } else {
               this.foundTypeVariables = List.nil();
            }

         }
      }
   }

   private Name simpleBinaryName(Name var1, Name var2) {
      String var3 = var1.toString().substring(var2.toString().length());
      if (var3.length() >= 1 && var3.charAt(0) == '$') {
         int var4;
         for(var4 = 1; var4 < var3.length() && isAsciiDigit(var3.charAt(var4)); ++var4) {
         }

         return this.names.fromString(var3.substring(var4));
      } else {
         throw this.badClassFile("bad.enclosing.method", var1);
      }
   }

   private Symbol.MethodSymbol findMethod(ClassFile.NameAndType var1, Scope var2, long var3) {
      if (var1 == null) {
         return null;
      } else {
         Type.MethodType var5 = var1.uniqueType.type.asMethodType();

         for(Scope.Entry var6 = var2.lookup(var1.name); var6.scope != null; var6 = var6.next()) {
            if (var6.sym.kind == 16 && this.isSameBinaryType(var6.sym.type.asMethodType(), var5)) {
               return (Symbol.MethodSymbol)var6.sym;
            }
         }

         if (var1.name != this.names.init) {
            return null;
         } else if ((var3 & 512L) != 0L) {
            return null;
         } else if (var1.uniqueType.type.getParameterTypes().isEmpty()) {
            return null;
         } else {
            var1.setType(new Type.MethodType(var1.uniqueType.type.getParameterTypes().tail, var1.uniqueType.type.getReturnType(), var1.uniqueType.type.getThrownTypes(), this.syms.methodClass));
            return this.findMethod(var1, var2, var3);
         }
      }
   }

   private boolean isSameBinaryType(Type.MethodType var1, Type.MethodType var2) {
      List var3 = this.types.erasure(var1.getParameterTypes()).prepend(this.types.erasure(var1.getReturnType()));

      List var4;
      for(var4 = var2.getParameterTypes().prepend(var2.getReturnType()); !var3.isEmpty() && !var4.isEmpty(); var4 = var4.tail) {
         if (((Type)var3.head).tsym != ((Type)var4.head).tsym) {
            return false;
         }

         var3 = var3.tail;
      }

      return var3.isEmpty() && var4.isEmpty();
   }

   private static boolean isAsciiDigit(char var0) {
      return '0' <= var0 && var0 <= '9';
   }

   void readMemberAttrs(Symbol var1) {
      this.readAttrs(var1, ClassReader.AttributeKind.MEMBER);
   }

   void readAttrs(Symbol var1, AttributeKind var2) {
      char var3 = this.nextChar();

      for(int var4 = 0; var4 < var3; ++var4) {
         Name var5 = this.readName(this.nextChar());
         int var6 = this.nextInt();
         AttributeReader var7 = (AttributeReader)this.attributeReaders.get(var5);
         if (var7 != null && var7.accepts(var2)) {
            var7.read(var1, var6);
         } else {
            this.unrecognized(var5);
            this.bp += var6;
         }
      }

   }

   void readClassAttrs(Symbol.ClassSymbol var1) {
      this.readAttrs(var1, ClassReader.AttributeKind.CLASS);
   }

   Code readCode(Symbol var1) {
      this.nextChar();
      this.nextChar();
      int var2 = this.nextInt();
      this.bp += var2;
      char var3 = this.nextChar();
      this.bp += var3 * 8;
      this.readMemberAttrs(var1);
      return null;
   }

   void attachAnnotations(Symbol var1) {
      char var2 = this.nextChar();
      if (var2 != 0) {
         ListBuffer var3 = new ListBuffer();

         for(int var4 = 0; var4 < var2; ++var4) {
            CompoundAnnotationProxy var5 = this.readCompoundAnnotation();
            if (var5.type.tsym == this.syms.proprietaryType.tsym) {
               var1.flags_field |= 274877906944L;
            } else if (var5.type.tsym == this.syms.profileType.tsym) {
               if (this.profile != Profile.DEFAULT) {
                  Iterator var6 = var5.values.iterator();

                  while(var6.hasNext()) {
                     Pair var7 = (Pair)var6.next();
                     if (var7.fst == this.names.value && var7.snd instanceof Attribute.Constant) {
                        Attribute.Constant var8 = (Attribute.Constant)var7.snd;
                        if (var8.type == this.syms.intType && (Integer)var8.value > this.profile.value) {
                           var1.flags_field |= 35184372088832L;
                        }
                     }
                  }
               }
            } else {
               var3.append(var5);
            }
         }

         this.annotate.normal(new AnnotationCompleter(var1, var3.toList()));
      }

   }

   void attachParameterAnnotations(Symbol var1) {
      Symbol.MethodSymbol var2 = (Symbol.MethodSymbol)var1;
      int var3 = this.buf[this.bp++] & 255;
      List var4 = var2.params();

      int var5;
      for(var5 = 0; var4.tail != null; ++var5) {
         this.attachAnnotations((Symbol)var4.head);
         var4 = var4.tail;
      }

      if (var5 != var3) {
         throw this.badClassFile("bad.runtime.invisible.param.annotations", var2);
      }
   }

   void attachTypeAnnotations(Symbol var1) {
      char var2 = this.nextChar();
      if (var2 != 0) {
         ListBuffer var3 = new ListBuffer();

         for(int var4 = 0; var4 < var2; ++var4) {
            var3.append(this.readTypeAnnotation());
         }

         this.annotate.normal(new TypeAnnotationCompleter(var1, var3.toList()));
      }

   }

   void attachAnnotationDefault(Symbol var1) {
      Symbol.MethodSymbol var2 = (Symbol.MethodSymbol)var1;
      Attribute var3 = this.readAttributeValue();
      var2.defaultValue = var3;
      this.annotate.normal(new AnnotationDefaultCompleter(var2, var3));
   }

   Type readTypeOrClassSymbol(int var1) {
      return this.buf[this.poolIdx[var1]] == 7 ? this.readClassSymbol(var1).type : this.readType(var1);
   }

   Type readEnumType(int var1) {
      int var2 = this.poolIdx[var1];
      char var3 = this.getChar(var2 + 1);
      return this.buf[var2 + var3 + 2] != 59 ? this.enterClass(this.readName(var1)).type : this.readType(var1);
   }

   CompoundAnnotationProxy readCompoundAnnotation() {
      Type var1 = this.readTypeOrClassSymbol(this.nextChar());
      char var2 = this.nextChar();
      ListBuffer var3 = new ListBuffer();

      for(int var4 = 0; var4 < var2; ++var4) {
         Name var5 = this.readName(this.nextChar());
         Attribute var6 = this.readAttributeValue();
         var3.append(new Pair(var5, var6));
      }

      return new CompoundAnnotationProxy(var1, var3.toList());
   }

   TypeAnnotationProxy readTypeAnnotation() {
      TypeAnnotationPosition var1 = this.readPosition();
      CompoundAnnotationProxy var2 = this.readCompoundAnnotation();
      return new TypeAnnotationProxy(var2, var1);
   }

   TypeAnnotationPosition readPosition() {
      int var1 = this.nextByte();
      if (!TargetType.isValidTargetTypeValue(var1)) {
         throw this.badClassFile("bad.type.annotation.value", String.format("0x%02X", var1));
      } else {
         TypeAnnotationPosition var2;
         int var4;
         var2 = new TypeAnnotationPosition();
         TargetType var3 = TargetType.fromTargetTypeValue(var1);
         var2.type = var3;
         label39:
         switch (var3) {
            case INSTANCEOF:
            case NEW:
            case CONSTRUCTOR_REFERENCE:
            case METHOD_REFERENCE:
               var2.offset = this.nextChar();
               break;
            case LOCAL_VARIABLE:
            case RESOURCE_VARIABLE:
               var4 = this.nextChar();
               var2.lvarOffset = new int[var4];
               var2.lvarLength = new int[var4];
               var2.lvarIndex = new int[var4];
               int var5 = 0;

               while(true) {
                  if (var5 >= var4) {
                     break label39;
                  }

                  var2.lvarOffset[var5] = this.nextChar();
                  var2.lvarLength[var5] = this.nextChar();
                  var2.lvarIndex[var5] = this.nextChar();
                  ++var5;
               }
            case EXCEPTION_PARAMETER:
               var2.exception_index = this.nextChar();
            case METHOD_RECEIVER:
            case METHOD_RETURN:
            case FIELD:
               break;
            case CLASS_TYPE_PARAMETER:
            case METHOD_TYPE_PARAMETER:
               var2.parameter_index = this.nextByte();
               break;
            case CLASS_TYPE_PARAMETER_BOUND:
            case METHOD_TYPE_PARAMETER_BOUND:
               var2.parameter_index = this.nextByte();
               var2.bound_index = this.nextByte();
               break;
            case CLASS_EXTENDS:
               var2.type_index = this.nextChar();
               break;
            case THROWS:
               var2.type_index = this.nextChar();
               break;
            case METHOD_FORMAL_PARAMETER:
               var2.parameter_index = this.nextByte();
               break;
            case CAST:
            case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
            case METHOD_INVOCATION_TYPE_ARGUMENT:
            case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
            case METHOD_REFERENCE_TYPE_ARGUMENT:
               var2.offset = this.nextChar();
               var2.type_index = this.nextByte();
               break;
            case UNKNOWN:
               throw new AssertionError("jvm.ClassReader: UNKNOWN target type should never occur!");
            default:
               throw new AssertionError("jvm.ClassReader: Unknown target type for position: " + var2);
         }

         var4 = this.nextByte();
         ListBuffer var7 = new ListBuffer();

         for(int var6 = 0; var6 < var4 * 2; ++var6) {
            var7 = var7.append(this.nextByte());
         }

         var2.location = TypeAnnotationPosition.getTypePathFromBinary(var7.toList());
         return var2;
      }
   }

   Attribute readAttributeValue() {
      char var1 = (char)this.buf[this.bp++];
      switch (var1) {
         case '@':
            return this.readCompoundAnnotation();
         case 'A':
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'a':
         case 'b':
         case 'd':
         case 'f':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'm':
         case 'n':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         default:
            throw new AssertionError("unknown annotation tag '" + var1 + "'");
         case 'B':
            return new Attribute.Constant(this.syms.byteType, this.readPool(this.nextChar()));
         case 'C':
            return new Attribute.Constant(this.syms.charType, this.readPool(this.nextChar()));
         case 'D':
            return new Attribute.Constant(this.syms.doubleType, this.readPool(this.nextChar()));
         case 'F':
            return new Attribute.Constant(this.syms.floatType, this.readPool(this.nextChar()));
         case 'I':
            return new Attribute.Constant(this.syms.intType, this.readPool(this.nextChar()));
         case 'J':
            return new Attribute.Constant(this.syms.longType, this.readPool(this.nextChar()));
         case 'S':
            return new Attribute.Constant(this.syms.shortType, this.readPool(this.nextChar()));
         case 'Z':
            return new Attribute.Constant(this.syms.booleanType, this.readPool(this.nextChar()));
         case '[':
            char var2 = this.nextChar();
            ListBuffer var3 = new ListBuffer();

            for(int var4 = 0; var4 < var2; ++var4) {
               var3.append(this.readAttributeValue());
            }

            return new ArrayAttributeProxy(var3.toList());
         case 'c':
            return new Attribute.Class(this.types, this.readTypeOrClassSymbol(this.nextChar()));
         case 'e':
            return new EnumAttributeProxy(this.readEnumType(this.nextChar()), this.readName(this.nextChar()));
         case 's':
            return new Attribute.Constant(this.syms.stringType, this.readPool(this.nextChar()).toString());
      }
   }

   Symbol.VarSymbol readField() {
      long var1 = this.adjustFieldFlags((long)this.nextChar());
      Name var3 = this.readName(this.nextChar());
      Type var4 = this.readType(this.nextChar());
      Symbol.VarSymbol var5 = new Symbol.VarSymbol(var1, var3, var4, this.currentOwner);
      this.readMemberAttrs(var5);
      return var5;
   }

   Symbol.MethodSymbol readMethod() {
      long var1 = this.adjustMethodFlags((long)this.nextChar());
      Name var3 = this.readName(this.nextChar());
      Object var4 = this.readType(this.nextChar());
      if (this.currentOwner.isInterface() && (var1 & 1024L) == 0L && !var3.equals(this.names.clinit)) {
         if (this.majorVersion <= Target.JDK1_8.majorVersion && (this.majorVersion != Target.JDK1_8.majorVersion || this.minorVersion < Target.JDK1_8.minorVersion)) {
            throw this.badClassFile((var1 & 8L) == 0L ? "invalid.default.interface" : "invalid.static.interface", Integer.toString(this.majorVersion), Integer.toString(this.minorVersion));
         }

         if ((var1 & 8L) == 0L) {
            Symbol var10000 = this.currentOwner;
            var10000.flags_field |= 8796093022208L;
            var1 |= 8796093023232L;
         }
      }

      if (var3 == this.names.init && this.currentOwner.hasOuterInstance() && !this.currentOwner.name.isEmpty()) {
         var4 = new Type.MethodType(this.adjustMethodParams(var1, ((Type)var4).getParameterTypes()), ((Type)var4).getReturnType(), ((Type)var4).getThrownTypes(), this.syms.methodClass);
      }

      Symbol.MethodSymbol var5 = new Symbol.MethodSymbol(var1, var3, (Type)var4, this.currentOwner);
      if (this.types.isSignaturePolymorphic(var5)) {
         var5.flags_field |= 70368744177664L;
      }

      if (this.saveParameterNames) {
         this.initParameterNames(var5);
      }

      Symbol var6 = this.currentOwner;
      this.currentOwner = var5;

      try {
         this.readMemberAttrs(var5);
      } finally {
         this.currentOwner = var6;
      }

      if (this.saveParameterNames) {
         this.setParameterNames(var5, (Type)var4);
      }

      return var5;
   }

   private List adjustMethodParams(long var1, List var3) {
      boolean var4 = (var1 & 17179869184L) != 0L;
      if (var4) {
         Type var5 = (Type)var3.last();
         ListBuffer var6 = new ListBuffer();
         Iterator var7 = var3.iterator();

         while(var7.hasNext()) {
            Type var8 = (Type)var7.next();
            var6.append(var8 != var5 ? var8 : ((Type.ArrayType)var8).makeVarargs());
         }

         var3 = var6.toList();
      }

      return var3.tail;
   }

   void initParameterNames(Symbol.MethodSymbol var1) {
      int var3 = Code.width(var1.type.getParameterTypes()) + 4;
      if (this.parameterNameIndices != null && this.parameterNameIndices.length >= var3) {
         Arrays.fill(this.parameterNameIndices, 0);
      } else {
         this.parameterNameIndices = new int[var3];
      }

      this.haveParameterNameIndices = false;
      this.sawMethodParameters = false;
   }

   void setParameterNames(Symbol.MethodSymbol var1, Type var2) {
      if (this.haveParameterNameIndices) {
         int var3 = 0;
         if (!this.sawMethodParameters) {
            var3 = (var1.flags() & 8L) == 0L ? 1 : 0;
            if (var1.name == this.names.init && this.currentOwner.hasOuterInstance() && !this.currentOwner.name.isEmpty()) {
               ++var3;
            }

            if (var1.type != var2) {
               int var4 = Code.width(var2.getParameterTypes()) - Code.width(var1.type.getParameterTypes());
               var3 += var4;
            }
         }

         List var10 = List.nil();
         int var5 = var3;

         Type var7;
         for(Iterator var6 = var1.type.getParameterTypes().iterator(); var6.hasNext(); var5 += Code.width(var7)) {
            var7 = (Type)var6.next();
            int var8 = var5 < this.parameterNameIndices.length ? this.parameterNameIndices[var5] : 0;
            Name var9 = var8 == 0 ? this.names.empty : this.readName(var8);
            var10 = var10.prepend(var9);
         }

         var1.savedParameterNames = var10.reverse();
      }
   }

   void skipBytes(int var1) {
      this.bp += var1;
   }

   void skipMember() {
      this.bp += 6;
      char var1 = this.nextChar();

      for(int var2 = 0; var2 < var1; ++var2) {
         this.bp += 2;
         int var3 = this.nextInt();
         this.bp += var3;
      }

   }

   protected void enterTypevars(Type var1) {
      if (var1.getEnclosingType() != null && var1.getEnclosingType().hasTag(TypeTag.CLASS)) {
         this.enterTypevars(var1.getEnclosingType());
      }

      for(List var2 = var1.getTypeArguments(); var2.nonEmpty(); var2 = var2.tail) {
         this.typevars.enter(((Type)var2.head).tsym);
      }

   }

   protected void enterTypevars(Symbol var1) {
      if (var1.owner.kind == 16) {
         this.enterTypevars(var1.owner);
         this.enterTypevars(var1.owner.owner);
      }

      this.enterTypevars(var1.type);
   }

   void readClass(Symbol.ClassSymbol var1) {
      Type.ClassType var2 = (Type.ClassType)var1.type;
      var1.members_field = new Scope(var1);
      this.typevars = this.typevars.dup(this.currentOwner);
      if (var2.getEnclosingType().hasTag(TypeTag.CLASS)) {
         this.enterTypevars(var2.getEnclosingType());
      }

      long var3 = this.adjustClassFlags((long)this.nextChar());
      if (var1.owner.kind == 1) {
         var1.flags_field = var3;
      }

      Symbol.ClassSymbol var5 = this.readClassSymbol(this.nextChar());
      if (var1 != var5) {
         throw this.badClassFile("class.file.wrong.class", var5.flatname);
      } else {
         int var6 = this.bp;
         this.nextChar();
         char var7 = this.nextChar();
         this.bp += var7 * 2;
         char var8 = this.nextChar();

         for(int var9 = 0; var9 < var8; ++var9) {
            this.skipMember();
         }

         char var14 = this.nextChar();

         int var10;
         for(var10 = 0; var10 < var14; ++var10) {
            this.skipMember();
         }

         this.readClassAttrs(var1);
         if (this.readAllOfClassFile) {
            for(var10 = 1; var10 < this.poolObj.length; ++var10) {
               this.readPool(var10);
            }

            var1.pool = new Pool(this.poolObj.length, this.poolObj, this.types);
         }

         this.bp = var6;
         char var15 = this.nextChar();
         if (var2.supertype_field == null) {
            var2.supertype_field = (Type)(var15 == 0 ? Type.noType : this.readClassSymbol(var15).erasure(this.types));
         }

         var15 = this.nextChar();
         List var11 = List.nil();

         int var12;
         for(var12 = 0; var12 < var15; ++var12) {
            Type var13 = this.readClassSymbol(this.nextChar()).erasure(this.types);
            var11 = var11.prepend(var13);
         }

         if (var2.interfaces_field == null) {
            var2.interfaces_field = var11.reverse();
         }

         Assert.check(var8 == this.nextChar());

         for(var12 = 0; var12 < var8; ++var12) {
            this.enterMember(var1, this.readField());
         }

         Assert.check(var14 == this.nextChar());

         for(var12 = 0; var12 < var14; ++var12) {
            this.enterMember(var1, this.readMethod());
         }

         this.typevars = this.typevars.leave();
      }
   }

   void readInnerClasses(Symbol.ClassSymbol var1) {
      char var2 = this.nextChar();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.nextChar();
         Symbol.ClassSymbol var4 = this.readClassSymbol(this.nextChar());
         Name var5 = this.readName(this.nextChar());
         if (var5 == null) {
            var5 = this.names.empty;
         }

         long var6 = this.adjustClassFlags((long)this.nextChar());
         if (var4 != null) {
            if (var5 == this.names.empty) {
               var5 = this.names.one;
            }

            Symbol.ClassSymbol var8 = this.enterClass(var5, (Symbol.TypeSymbol)var4);
            if ((var6 & 8L) == 0L) {
               ((Type.ClassType)var8.type).setEnclosingType(var4.type);
               if (var8.erasure_field != null) {
                  ((Type.ClassType)var8.erasure_field).setEnclosingType(this.types.erasure(var4.type));
               }
            }

            if (var1 == var4) {
               var8.flags_field = var6;
               this.enterMember(var1, var8);
            }
         }
      }

   }

   private void readClassFile(Symbol.ClassSymbol var1) throws IOException {
      int var2 = this.nextInt();
      if (var2 != -889275714) {
         throw this.badClassFile("illegal.start.of.class.file");
      } else {
         this.minorVersion = this.nextChar();
         this.majorVersion = this.nextChar();
         int var3 = Target.MAX().majorVersion;
         int var4 = Target.MAX().minorVersion;
         if (this.majorVersion <= var3 && this.majorVersion * 1000 + this.minorVersion >= Target.MIN().majorVersion * 1000 + Target.MIN().minorVersion) {
            if (this.checkClassFile && this.majorVersion == var3 && this.minorVersion > var4) {
               this.printCCF("found.later.version", Integer.toString(this.minorVersion));
            }
         } else {
            if (this.majorVersion != var3 + 1) {
               throw this.badClassFile("wrong.version", Integer.toString(this.majorVersion), Integer.toString(this.minorVersion), Integer.toString(var3), Integer.toString(var4));
            }

            this.log.warning("big.major.version", new Object[]{this.currentClassFile, this.majorVersion, var3});
         }

         this.indexPool();
         if (this.signatureBuffer.length < this.bp) {
            int var5 = Integer.highestOneBit(this.bp) << 1;
            this.signatureBuffer = new byte[var5];
         }

         this.readClass(var1);
      }
   }

   long adjustFieldFlags(long var1) {
      return var1;
   }

   long adjustMethodFlags(long var1) {
      if ((var1 & 64L) != 0L) {
         var1 &= -65L;
         var1 |= 2147483648L;
         if (!this.allowGenerics) {
            var1 &= -4097L;
         }
      }

      if ((var1 & 128L) != 0L) {
         var1 &= -129L;
         var1 |= 17179869184L;
      }

      return var1;
   }

   long adjustClassFlags(long var1) {
      return var1 & -33L;
   }

   public Symbol.ClassSymbol defineClass(Name var1, Symbol var2) {
      Symbol.ClassSymbol var3 = new Symbol.ClassSymbol(0L, var1, var2);
      if (var2.kind == 1) {
         Assert.checkNull(this.classes.get(var3.flatname), (Object)var3);
      }

      var3.completer = this.thisCompleter;
      return var3;
   }

   public Symbol.ClassSymbol enterClass(Name var1, Symbol.TypeSymbol var2) {
      Name var3 = Symbol.TypeSymbol.formFlatName(var1, var2);
      Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)this.classes.get(var3);
      if (var4 == null) {
         var4 = this.defineClass(var1, var2);
         this.classes.put(var3, var4);
      } else if ((var4.name != var1 || var4.owner != var2) && var2.kind == 2 && var4.owner.kind == 1) {
         var4.owner.members().remove(var4);
         var4.name = var1;
         var4.owner = var2;
         var4.fullname = Symbol.ClassSymbol.formFullName(var1, var2);
      }

      return var4;
   }

   public Symbol.ClassSymbol enterClass(Name var1, JavaFileObject var2) {
      Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)this.classes.get(var1);
      if (var3 != null) {
         String var6 = Log.format("%s: completer = %s; class file = %s; source file = %s", var3.fullname, var3.completer, var3.classfile, var3.sourcefile);
         throw new AssertionError(var6);
      } else {
         Name var4 = Convert.packagePart(var1);
         Symbol.PackageSymbol var5 = var4.isEmpty() ? this.syms.unnamedPackage : this.enterPackage(var4);
         var3 = this.defineClass(Convert.shortName(var1), var5);
         var3.classfile = var2;
         this.classes.put(var1, var3);
         return var3;
      }
   }

   public Symbol.ClassSymbol enterClass(Name var1) {
      Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)this.classes.get(var1);
      return var2 == null ? this.enterClass(var1, (JavaFileObject)null) : var2;
   }

   private void complete(Symbol var1) throws Symbol.CompletionFailure {
      if (var1.kind == 2) {
         Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1;
         var2.members_field = new Scope.ErrorScope(var2);
         this.annotate.enterStart();

         try {
            this.completeOwners(var2.owner);
            this.completeEnclosing(var2);
         } finally {
            this.annotate.enterDoneWithoutFlush();
         }

         this.fillIn(var2);
      } else if (var1.kind == 1) {
         Symbol.PackageSymbol var8 = (Symbol.PackageSymbol)var1;

         try {
            this.fillIn(var8);
         } catch (IOException var7) {
            throw (new Symbol.CompletionFailure(var1, var7.getLocalizedMessage())).initCause(var7);
         }
      }

      if (!this.filling) {
         this.annotate.flush();
      }

   }

   private void completeOwners(Symbol var1) {
      if (var1.kind != 1) {
         this.completeOwners(var1.owner);
      }

      var1.complete();
   }

   private void completeEnclosing(Symbol.ClassSymbol var1) {
      if (var1.owner.kind == 1) {
         Symbol var2 = var1.owner;
         Iterator var3 = Convert.enclosingCandidates(Convert.shortName(var1.name)).iterator();

         while(var3.hasNext()) {
            Name var4 = (Name)var3.next();
            Symbol var5 = var2.members().lookup(var4).sym;
            if (var5 == null) {
               var5 = (Symbol)this.classes.get(Symbol.TypeSymbol.formFlatName(var4, var2));
            }

            if (var5 != null) {
               var5.complete();
            }
         }
      }

   }

   private void fillIn(Symbol.ClassSymbol var1) {
      if (this.completionFailureName == var1.fullname) {
         throw new Symbol.CompletionFailure(var1, "user-selected completion failure by class name");
      } else {
         this.currentOwner = var1;
         this.warnedAttrs.clear();
         JavaFileObject var2 = var1.classfile;
         if (var2 != null) {
            JavaFileObject var18 = this.currentClassFile;

            try {
               if (this.filling) {
                  Assert.error("Filling " + var2.toUri() + " during " + var18);
               }

               this.currentClassFile = var2;
               if (this.verbose) {
                  this.log.printVerbose("loading", this.currentClassFile.toString());
               }

               if (var2.getKind() == Kind.CLASS) {
                  this.filling = true;

                  try {
                     this.bp = 0;
                     this.buf = readInputStream(this.buf, var2.openInputStream());
                     this.readClassFile(var1);
                     if (!this.missingTypeVariables.isEmpty() && !this.foundTypeVariables.isEmpty()) {
                        List var19 = this.missingTypeVariables;
                        List var5 = this.foundTypeVariables;
                        this.missingTypeVariables = List.nil();
                        this.foundTypeVariables = List.nil();
                        this.filling = false;
                        Type.ClassType var6 = (Type.ClassType)this.currentOwner.type;
                        var6.supertype_field = this.types.subst(var6.supertype_field, var19, var5);
                        var6.interfaces_field = this.types.subst(var6.interfaces_field, var19, var5);
                     } else if (this.missingTypeVariables.isEmpty() != this.foundTypeVariables.isEmpty()) {
                        Name var4 = ((Type)this.missingTypeVariables.head).tsym.name;
                        throw this.badClassFile("undecl.type.var", var4);
                     }
                  } finally {
                     this.missingTypeVariables = List.nil();
                     this.foundTypeVariables = List.nil();
                     this.filling = false;
                  }
               } else {
                  if (this.sourceCompleter == null) {
                     throw new IllegalStateException("Source completer required to read " + var2.toUri());
                  }

                  this.sourceCompleter.complete(var1);
               }
            } catch (IOException var16) {
               throw this.badClassFile("unable.to.access.file", var16.getMessage());
            } finally {
               this.currentClassFile = var18;
            }

         } else {
            JCDiagnostic var3 = this.diagFactory.fragment("class.file.not.found", var1.flatname);
            throw this.newCompletionFailure(var1, var3);
         }
      }
   }

   private static byte[] readInputStream(byte[] var0, InputStream var1) throws IOException {
      try {
         var0 = ensureCapacity(var0, var1.available());
         int var2 = var1.read(var0);

         for(int var3 = 0; var2 != -1; var2 = var1.read(var0, var3, var0.length - var3)) {
            var3 += var2;
            var0 = ensureCapacity(var0, var3);
         }

         byte[] var4 = var0;
         return var4;
      } finally {
         try {
            var1.close();
         } catch (IOException var11) {
         }

      }
   }

   private static byte[] ensureCapacity(byte[] var0, int var1) {
      if (var0.length <= var1) {
         byte[] var2 = var0;
         var0 = new byte[Integer.highestOneBit(var1) << 1];
         System.arraycopy(var2, 0, var0, 0, var2.length);
      }

      return var0;
   }

   private Symbol.CompletionFailure newCompletionFailure(Symbol.TypeSymbol var1, JCDiagnostic var2) {
      if (!this.cacheCompletionFailure) {
         return new Symbol.CompletionFailure(var1, var2);
      } else {
         Symbol.CompletionFailure var3 = this.cachedCompletionFailure;
         var3.sym = var1;
         var3.diag = var2;
         return var3;
      }
   }

   public Symbol.ClassSymbol loadClass(Name var1) throws Symbol.CompletionFailure {
      boolean var2 = this.classes.get(var1) == null;
      Symbol.ClassSymbol var3 = this.enterClass(var1);
      if (var3.members_field == null && var3.completer != null) {
         try {
            var3.complete();
         } catch (Symbol.CompletionFailure var5) {
            if (var2) {
               this.classes.remove(var1);
            }

            throw var5;
         }
      }

      return var3;
   }

   public boolean packageExists(Name var1) {
      return this.enterPackage(var1).exists();
   }

   public Symbol.PackageSymbol enterPackage(Name var1) {
      Symbol.PackageSymbol var2 = (Symbol.PackageSymbol)this.packages.get(var1);
      if (var2 == null) {
         Assert.check(!var1.isEmpty(), "rootPackage missing!");
         var2 = new Symbol.PackageSymbol(Convert.shortName(var1), this.enterPackage(Convert.packagePart(var1)));
         var2.completer = this.thisCompleter;
         this.packages.put(var1, var2);
      }

      return var2;
   }

   public Symbol.PackageSymbol enterPackage(Name var1, Symbol.PackageSymbol var2) {
      return this.enterPackage(Symbol.TypeSymbol.formFullName(var1, var2));
   }

   protected void includeClassFile(Symbol.PackageSymbol var1, JavaFileObject var2) {
      if ((var1.flags_field & 8388608L) == 0L) {
         for(Object var3 = var1; var3 != null && ((Symbol)var3).kind == 1; var3 = ((Symbol)var3).owner) {
            ((Symbol)var3).flags_field |= 8388608L;
         }
      }

      JavaFileObject.Kind var10 = var2.getKind();
      int var4;
      if (var10 == Kind.CLASS) {
         var4 = 33554432;
      } else {
         var4 = 67108864;
      }

      String var5 = this.fileManager.inferBinaryName(this.currentLoc, var2);
      int var6 = var5.lastIndexOf(".");
      Name var7 = this.names.fromString(var5.substring(var6 + 1));
      boolean var8 = var7 == this.names.package_info;
      Symbol.ClassSymbol var9 = var8 ? var1.package_info : (Symbol.ClassSymbol)var1.members_field.lookup(var7).sym;
      if (var9 == null) {
         var9 = this.enterClass(var7, (Symbol.TypeSymbol)var1);
         if (var9.classfile == null) {
            var9.classfile = var2;
         }

         if (var8) {
            var1.package_info = var9;
         } else if (var9.owner == var1) {
            var1.members_field.enter(var9);
         }
      } else if (var9.classfile != null && (var9.flags_field & (long)var4) == 0L && (var9.flags_field & 100663296L) != 0L) {
         var9.classfile = this.preferredFileObject(var2, var9.classfile);
      }

      var9.flags_field |= (long)var4;
   }

   protected JavaFileObject preferredFileObject(JavaFileObject var1, JavaFileObject var2) {
      if (this.preferSource) {
         return var1.getKind() == Kind.SOURCE ? var1 : var2;
      } else {
         long var3 = var1.getLastModified();
         long var5 = var2.getLastModified();
         return var3 > var5 ? var1 : var2;
      }
   }

   protected EnumSet getPackageFileKinds() {
      return EnumSet.of(Kind.CLASS, Kind.SOURCE);
   }

   protected void extraFileActions(Symbol.PackageSymbol var1, JavaFileObject var2) {
   }

   private void fillIn(Symbol.PackageSymbol var1) throws IOException {
      if (var1.members_field == null) {
         var1.members_field = new Scope(var1);
      }

      String var2 = var1.fullname.toString();
      EnumSet var3 = this.getPackageFileKinds();
      this.fillIn(var1, StandardLocation.PLATFORM_CLASS_PATH, this.fileManager.list(StandardLocation.PLATFORM_CLASS_PATH, var2, EnumSet.of(Kind.CLASS), false));
      EnumSet var4 = EnumSet.copyOf(var3);
      var4.remove(Kind.SOURCE);
      boolean var5 = !var4.isEmpty();
      EnumSet var6 = EnumSet.copyOf(var3);
      var6.remove(Kind.CLASS);
      boolean var7 = !var6.isEmpty();
      boolean var8 = this.fileManager.hasLocation(StandardLocation.SOURCE_PATH);
      if (this.verbose && this.verbosePath && this.fileManager instanceof StandardJavaFileManager) {
         StandardJavaFileManager var9 = (StandardJavaFileManager)this.fileManager;
         List var10;
         Iterator var11;
         File var12;
         if (var8 && var7) {
            var10 = List.nil();

            for(var11 = var9.getLocation(StandardLocation.SOURCE_PATH).iterator(); var11.hasNext(); var10 = var10.prepend(var12)) {
               var12 = (File)var11.next();
            }

            this.log.printVerbose("sourcepath", var10.reverse().toString());
         } else if (var7) {
            var10 = List.nil();

            for(var11 = var9.getLocation(StandardLocation.CLASS_PATH).iterator(); var11.hasNext(); var10 = var10.prepend(var12)) {
               var12 = (File)var11.next();
            }

            this.log.printVerbose("sourcepath", var10.reverse().toString());
         }

         if (var5) {
            var10 = List.nil();

            for(var11 = var9.getLocation(StandardLocation.PLATFORM_CLASS_PATH).iterator(); var11.hasNext(); var10 = var10.prepend(var12)) {
               var12 = (File)var11.next();
            }

            for(var11 = var9.getLocation(StandardLocation.CLASS_PATH).iterator(); var11.hasNext(); var10 = var10.prepend(var12)) {
               var12 = (File)var11.next();
            }

            this.log.printVerbose("classpath", var10.reverse().toString());
         }
      }

      if (var7 && !var8) {
         this.fillIn(var1, StandardLocation.CLASS_PATH, this.fileManager.list(StandardLocation.CLASS_PATH, var2, var3, false));
      } else {
         if (var5) {
            this.fillIn(var1, StandardLocation.CLASS_PATH, this.fileManager.list(StandardLocation.CLASS_PATH, var2, var4, false));
         }

         if (var7) {
            this.fillIn(var1, StandardLocation.SOURCE_PATH, this.fileManager.list(StandardLocation.SOURCE_PATH, var2, var6, false));
         }
      }

      this.verbosePath = false;
   }

   private void fillIn(Symbol.PackageSymbol var1, JavaFileManager.Location var2, Iterable var3) {
      this.currentLoc = var2;
      Iterator var4 = var3.iterator();

      while(true) {
         JavaFileObject var5;
         String var7;
         label25:
         do {
            while(var4.hasNext()) {
               var5 = (JavaFileObject)var4.next();
               switch (var5.getKind()) {
                  case CLASS:
                  case SOURCE:
                     String var6 = this.fileManager.inferBinaryName(this.currentLoc, var5);
                     var7 = var6.substring(var6.lastIndexOf(".") + 1);
                     continue label25;
                  default:
                     this.extraFileActions(var1, var5);
               }
            }

            return;
         } while(!SourceVersion.isIdentifier(var7) && !var7.equals("package-info"));

         this.includeClassFile(var1, var5);
      }
   }

   private void printCCF(String var1, Object var2) {
      this.log.printLines(var1, var2);
   }

   private static class SourceFileObject extends BaseFileObject {
      private Name name;
      private Name flatname;

      public SourceFileObject(Name var1, Name var2) {
         super((JavacFileManager)null);
         this.name = var1;
         this.flatname = var2;
      }

      public URI toUri() {
         try {
            return new URI((String)null, this.name.toString(), (String)null);
         } catch (URISyntaxException var2) {
            throw new BaseFileObject.CannotCreateUriError(this.name.toString(), var2);
         }
      }

      public String getName() {
         return this.name.toString();
      }

      public String getShortName() {
         return this.getName();
      }

      public JavaFileObject.Kind getKind() {
         return getKind(this.getName());
      }

      public InputStream openInputStream() {
         throw new UnsupportedOperationException();
      }

      public OutputStream openOutputStream() {
         throw new UnsupportedOperationException();
      }

      public CharBuffer getCharContent(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Reader openReader(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Writer openWriter() {
         throw new UnsupportedOperationException();
      }

      public long getLastModified() {
         throw new UnsupportedOperationException();
      }

      public boolean delete() {
         throw new UnsupportedOperationException();
      }

      protected String inferBinaryName(Iterable var1) {
         return this.flatname.toString();
      }

      public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
         return true;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof SourceFileObject)) {
            return false;
         } else {
            SourceFileObject var2 = (SourceFileObject)var1;
            return this.name.equals(var2.name);
         }
      }

      public int hashCode() {
         return this.name.hashCode();
      }
   }

   public interface SourceCompleter {
      void complete(Symbol.ClassSymbol var1) throws Symbol.CompletionFailure;
   }

   class TypeAnnotationCompleter extends AnnotationCompleter {
      List proxies;

      TypeAnnotationCompleter(Symbol var2, List var3) {
         super(var2, List.nil());
         this.proxies = var3;
      }

      List deproxyTypeCompoundList(List var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            TypeAnnotationProxy var4 = (TypeAnnotationProxy)var3.next();
            Attribute.Compound var5 = this.deproxyCompound(var4.compound);
            Attribute.TypeCompound var6 = new Attribute.TypeCompound(var5, var4.position);
            var2.add(var6);
         }

         return var2.toList();
      }

      public void run() {
         JavaFileObject var1 = ClassReader.this.currentClassFile;

         try {
            ClassReader.this.currentClassFile = this.classFile;
            List var2 = this.deproxyTypeCompoundList(this.proxies);
            this.sym.setTypeAttributes(var2.prependList(this.sym.getRawTypeAttributes()));
         } finally {
            ClassReader.this.currentClassFile = var1;
         }

      }
   }

   class AnnotationCompleter extends AnnotationDeproxy implements Annotate.Worker {
      final Symbol sym;
      final List l;
      final JavaFileObject classFile;

      public String toString() {
         return " ClassReader annotate " + this.sym.owner + "." + this.sym + " with " + this.l;
      }

      AnnotationCompleter(Symbol var2, List var3) {
         super();
         this.sym = var2;
         this.l = var3;
         this.classFile = ClassReader.this.currentClassFile;
      }

      public void run() {
         JavaFileObject var1 = ClassReader.this.currentClassFile;

         try {
            ClassReader.this.currentClassFile = this.classFile;
            List var2 = this.deproxyCompoundList(this.l);
            if (this.sym.annotationsPendingCompletion()) {
               this.sym.setDeclarationAttributes(var2);
            } else {
               this.sym.appendAttributes(var2);
            }
         } finally {
            ClassReader.this.currentClassFile = var1;
         }

      }
   }

   class AnnotationDefaultCompleter extends AnnotationDeproxy implements Annotate.Worker {
      final Symbol.MethodSymbol sym;
      final Attribute value;
      final JavaFileObject classFile;

      public String toString() {
         return " ClassReader store default for " + this.sym.owner + "." + this.sym + " is " + this.value;
      }

      AnnotationDefaultCompleter(Symbol.MethodSymbol var2, Attribute var3) {
         super();
         this.classFile = ClassReader.this.currentClassFile;
         this.sym = var2;
         this.value = var3;
      }

      public void run() {
         JavaFileObject var1 = ClassReader.this.currentClassFile;

         try {
            this.sym.defaultValue = null;
            ClassReader.this.currentClassFile = this.classFile;
            this.sym.defaultValue = this.deproxy(this.sym.type.getReturnType(), this.value);
         } finally {
            ClassReader.this.currentClassFile = var1;
         }

      }
   }

   class AnnotationDeproxy implements ProxyVisitor {
      private Symbol.ClassSymbol requestingOwner;
      Attribute result;
      Type type;

      AnnotationDeproxy() {
         this.requestingOwner = ClassReader.this.currentOwner.kind == 16 ? ClassReader.this.currentOwner.enclClass() : (Symbol.ClassSymbol)ClassReader.this.currentOwner;
      }

      List deproxyCompoundList(List var1) {
         ListBuffer var2 = new ListBuffer();

         for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
            var2.append(this.deproxyCompound((CompoundAnnotationProxy)var3.head));
         }

         return var2.toList();
      }

      Attribute.Compound deproxyCompound(CompoundAnnotationProxy var1) {
         ListBuffer var2 = new ListBuffer();

         for(List var3 = var1.values; var3.nonEmpty(); var3 = var3.tail) {
            Symbol.MethodSymbol var4 = this.findAccessMethod(var1.type, (Name)((Pair)var3.head).fst);
            var2.append(new Pair(var4, this.deproxy(var4.type.getReturnType(), (Attribute)((Pair)var3.head).snd)));
         }

         return new Attribute.Compound(var1.type, var2.toList());
      }

      Symbol.MethodSymbol findAccessMethod(Type var1, Name var2) {
         Symbol.CompletionFailure var3 = null;

         try {
            for(Scope.Entry var4 = var1.tsym.members().lookup(var2); var4.scope != null; var4 = var4.next()) {
               Symbol var5 = var4.sym;
               if (var5.kind == 16 && var5.type.getParameterTypes().length() == 0) {
                  return (Symbol.MethodSymbol)var5;
               }
            }
         } catch (Symbol.CompletionFailure var10) {
            var3 = var10;
         }

         JavaFileObject var11 = ClassReader.this.log.useSource(this.requestingOwner.classfile);

         try {
            if (ClassReader.this.lintClassfile) {
               if (var3 == null) {
                  ClassReader.this.log.warning("annotation.method.not.found", new Object[]{var1, var2});
               } else {
                  ClassReader.this.log.warning("annotation.method.not.found.reason", new Object[]{var1, var2, var3.getDetailValue()});
               }
            }
         } finally {
            ClassReader.this.log.useSource(var11);
         }

         Type.MethodType var12 = new Type.MethodType(List.nil(), ClassReader.this.syms.botType, List.nil(), ClassReader.this.syms.methodClass);
         return new Symbol.MethodSymbol(1025L, var2, var12, var1.tsym);
      }

      Attribute deproxy(Type var1, Attribute var2) {
         Type var3 = this.type;

         Attribute var4;
         try {
            this.type = var1;
            var2.accept(this);
            var4 = this.result;
         } finally {
            this.type = var3;
         }

         return var4;
      }

      public void visitConstant(Attribute.Constant var1) {
         this.result = var1;
      }

      public void visitClass(Attribute.Class var1) {
         this.result = var1;
      }

      public void visitEnum(Attribute.Enum var1) {
         throw new AssertionError();
      }

      public void visitCompound(Attribute.Compound var1) {
         throw new AssertionError();
      }

      public void visitArray(Attribute.Array var1) {
         throw new AssertionError();
      }

      public void visitError(Attribute.Error var1) {
         throw new AssertionError();
      }

      public void visitEnumAttributeProxy(EnumAttributeProxy var1) {
         Symbol.TypeSymbol var2 = var1.enumType.tsym;
         Symbol.VarSymbol var3 = null;
         Symbol.CompletionFailure var4 = null;

         try {
            for(Scope.Entry var5 = var2.members().lookup(var1.enumerator); var5.scope != null; var5 = var5.next()) {
               if (var5.sym.kind == 4) {
                  var3 = (Symbol.VarSymbol)var5.sym;
                  break;
               }
            }
         } catch (Symbol.CompletionFailure var6) {
            var4 = var6;
         }

         if (var3 == null) {
            if (var4 != null) {
               ClassReader.this.log.warning("unknown.enum.constant.reason", new Object[]{ClassReader.this.currentClassFile, var2, var1.enumerator, var4.getDiagnostic()});
            } else {
               ClassReader.this.log.warning("unknown.enum.constant", new Object[]{ClassReader.this.currentClassFile, var2, var1.enumerator});
            }

            this.result = new Attribute.Enum(var2.type, new Symbol.VarSymbol(0L, var1.enumerator, ClassReader.this.syms.botType, var2));
         } else {
            this.result = new Attribute.Enum(var2.type, var3);
         }

      }

      public void visitArrayAttributeProxy(ArrayAttributeProxy var1) {
         int var2 = var1.values.length();
         Attribute[] var3 = new Attribute[var2];
         Type var4 = ClassReader.this.types.elemtype(this.type);
         int var5 = 0;

         for(List var6 = var1.values; var6.nonEmpty(); var6 = var6.tail) {
            var3[var5++] = this.deproxy(var4, (Attribute)var6.head);
         }

         this.result = new Attribute.Array(this.type, var3);
      }

      public void visitCompoundAnnotationProxy(CompoundAnnotationProxy var1) {
         this.result = this.deproxyCompound(var1);
      }
   }

   static class TypeAnnotationProxy {
      final CompoundAnnotationProxy compound;
      final TypeAnnotationPosition position;

      public TypeAnnotationProxy(CompoundAnnotationProxy var1, TypeAnnotationPosition var2) {
         this.compound = var1;
         this.position = var2;
      }
   }

   static class CompoundAnnotationProxy extends Attribute {
      final List values;

      public CompoundAnnotationProxy(Type var1, List var2) {
         super(var1);
         this.values = var2;
      }

      public void accept(Attribute.Visitor var1) {
         ((ProxyVisitor)var1).visitCompoundAnnotationProxy(this);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("@");
         var1.append(this.type.tsym.getQualifiedName());
         var1.append("/*proxy*/{");
         boolean var2 = true;

         for(List var3 = this.values; var3.nonEmpty(); var3 = var3.tail) {
            Pair var4 = (Pair)var3.head;
            if (!var2) {
               var1.append(",");
            }

            var2 = false;
            var1.append((CharSequence)var4.fst);
            var1.append("=");
            var1.append(var4.snd);
         }

         var1.append("}");
         return var1.toString();
      }
   }

   static class ArrayAttributeProxy extends Attribute {
      List values;

      ArrayAttributeProxy(List var1) {
         super((Type)null);
         this.values = var1;
      }

      public void accept(Attribute.Visitor var1) {
         ((ProxyVisitor)var1).visitArrayAttributeProxy(this);
      }

      public String toString() {
         return "{" + this.values + "}";
      }
   }

   static class EnumAttributeProxy extends Attribute {
      Type enumType;
      Name enumerator;

      public EnumAttributeProxy(Type var1, Name var2) {
         super((Type)null);
         this.enumType = var1;
         this.enumerator = var2;
      }

      public void accept(Attribute.Visitor var1) {
         ((ProxyVisitor)var1).visitEnumAttributeProxy(this);
      }

      public String toString() {
         return "/*proxy enum*/" + this.enumType + "." + this.enumerator;
      }
   }

   interface ProxyVisitor extends Attribute.Visitor {
      void visitEnumAttributeProxy(EnumAttributeProxy var1);

      void visitArrayAttributeProxy(ArrayAttributeProxy var1);

      void visitCompoundAnnotationProxy(CompoundAnnotationProxy var1);
   }

   protected abstract class AttributeReader {
      protected final Name name;
      protected final ClassFile.Version version;
      protected final Set kinds;

      protected AttributeReader(Name var2, ClassFile.Version var3, Set var4) {
         this.name = var2;
         this.version = var3;
         this.kinds = var4;
      }

      protected boolean accepts(AttributeKind var1) {
         if (this.kinds.contains(var1)) {
            if (ClassReader.this.majorVersion > this.version.major || ClassReader.this.majorVersion == this.version.major && ClassReader.this.minorVersion >= this.version.minor) {
               return true;
            }

            if (ClassReader.this.lintClassfile && !ClassReader.this.warnedAttrs.contains(this.name)) {
               JavaFileObject var2 = ClassReader.this.log.useSource(ClassReader.this.currentClassFile);

               try {
                  ClassReader.this.log.warning(Lint.LintCategory.CLASSFILE, (JCDiagnostic.DiagnosticPosition)null, "future.attr", new Object[]{this.name, this.version.major, this.version.minor, ClassReader.this.majorVersion, ClassReader.this.minorVersion});
               } finally {
                  ClassReader.this.log.useSource(var2);
               }

               ClassReader.this.warnedAttrs.add(this.name);
            }
         }

         return false;
      }

      protected abstract void read(Symbol var1, int var2);
   }

   protected static enum AttributeKind {
      CLASS,
      MEMBER;
   }

   public class BadClassFile extends Symbol.CompletionFailure {
      private static final long serialVersionUID = 0L;

      public BadClassFile(Symbol.TypeSymbol var2, JavaFileObject var3, JCDiagnostic var4) {
         super(var2, (JCDiagnostic)ClassReader.this.createBadClassFileDiagnostic(var3, var4));
      }
   }
}
