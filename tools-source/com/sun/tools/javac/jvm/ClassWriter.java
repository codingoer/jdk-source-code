package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeAnnotationPosition;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.file.BaseFileObject;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.ByteBuffer;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Pair;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class ClassWriter extends ClassFile {
   protected static final Context.Key classWriterKey = new Context.Key();
   private final Options options;
   private boolean verbose;
   private boolean scramble;
   private boolean scrambleAll;
   private boolean retrofit;
   private boolean emitSourceFile;
   private boolean genCrt;
   boolean debugstackmap;
   private Target target;
   private Source source;
   private Types types;
   static final int DATA_BUF_SIZE = 65520;
   static final int POOL_BUF_SIZE = 131056;
   ByteBuffer databuf = new ByteBuffer(65520);
   ByteBuffer poolbuf = new ByteBuffer(131056);
   Pool pool;
   Set innerClasses;
   ListBuffer innerClassesQueue;
   Map bootstrapMethods;
   private final Log log;
   private final Names names;
   private final JavaFileManager fileManager;
   private final CWSignatureGenerator signatureGen;
   static final int SAME_FRAME_SIZE = 64;
   static final int SAME_LOCALS_1_STACK_ITEM_EXTENDED = 247;
   static final int SAME_FRAME_EXTENDED = 251;
   static final int FULL_FRAME = 255;
   static final int MAX_LOCAL_LENGTH_DIFF = 4;
   private final boolean dumpClassModifiers;
   private final boolean dumpFieldModifiers;
   private final boolean dumpInnerClassModifiers;
   private final boolean dumpMethodModifiers;
   private static final String[] flagName = new String[]{"PUBLIC", "PRIVATE", "PROTECTED", "STATIC", "FINAL", "SUPER", "VOLATILE", "TRANSIENT", "NATIVE", "INTERFACE", "ABSTRACT", "STRICTFP"};
   AttributeWriter awriter = new AttributeWriter();

   public static ClassWriter instance(Context var0) {
      ClassWriter var1 = (ClassWriter)var0.get(classWriterKey);
      if (var1 == null) {
         var1 = new ClassWriter(var0);
      }

      return var1;
   }

   protected ClassWriter(Context var1) {
      var1.put((Context.Key)classWriterKey, (Object)this);
      this.log = Log.instance(var1);
      this.names = Names.instance(var1);
      this.options = Options.instance(var1);
      this.target = Target.instance(var1);
      this.source = Source.instance(var1);
      this.types = Types.instance(var1);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      this.signatureGen = new CWSignatureGenerator(this.types);
      this.verbose = this.options.isSet(Option.VERBOSE);
      this.scramble = this.options.isSet("-scramble");
      this.scrambleAll = this.options.isSet("-scrambleAll");
      this.retrofit = this.options.isSet("-retrofit");
      this.genCrt = this.options.isSet(Option.XJCOV);
      this.debugstackmap = this.options.isSet("debugstackmap");
      this.emitSourceFile = this.options.isUnset(Option.G_CUSTOM) || this.options.isSet(Option.G_CUSTOM, "source");
      String var2 = this.options.get("dumpmodifiers");
      this.dumpClassModifiers = var2 != null && var2.indexOf(99) != -1;
      this.dumpFieldModifiers = var2 != null && var2.indexOf(102) != -1;
      this.dumpInnerClassModifiers = var2 != null && var2.indexOf(105) != -1;
      this.dumpMethodModifiers = var2 != null && var2.indexOf(109) != -1;
   }

   public static String flagNames(long var0) {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      for(long var4 = var0 & 4095L; var4 != 0L; ++var3) {
         if ((var4 & 1L) != 0L) {
            var2.append(" ");
            var2.append(flagName[var3]);
         }

         var4 >>= 1;
      }

      return var2.toString();
   }

   void putChar(ByteBuffer var1, int var2, int var3) {
      var1.elems[var2] = (byte)(var3 >> 8 & 255);
      var1.elems[var2 + 1] = (byte)(var3 & 255);
   }

   void putInt(ByteBuffer var1, int var2, int var3) {
      var1.elems[var2] = (byte)(var3 >> 24 & 255);
      var1.elems[var2 + 1] = (byte)(var3 >> 16 & 255);
      var1.elems[var2 + 2] = (byte)(var3 >> 8 & 255);
      var1.elems[var2 + 3] = (byte)(var3 & 255);
   }

   Name typeSig(Type var1) {
      Assert.check(this.signatureGen.isEmpty());
      this.signatureGen.assembleSig(var1);
      Name var2 = this.signatureGen.toName();
      this.signatureGen.reset();
      return var2;
   }

   public Name xClassName(Type var1) {
      if (var1.hasTag(TypeTag.CLASS)) {
         return this.names.fromUtf(externalize(var1.tsym.flatName()));
      } else if (var1.hasTag(TypeTag.ARRAY)) {
         return this.typeSig(this.types.erasure(var1));
      } else {
         throw new AssertionError("xClassName");
      }
   }

   void writePool(Pool var1) throws PoolOverflow, StringOverflow {
      int var2 = this.poolbuf.length;
      this.poolbuf.appendChar(0);

      for(int var3 = 1; var3 < var1.pp; ++var3) {
         Object var4 = var1.pool[var3];
         Assert.checkNonNull(var4);
         if (var4 instanceof Pool.Method || var4 instanceof Pool.Variable) {
            var4 = ((Symbol.DelegatedSymbol)var4).getUnderlyingSymbol();
         }

         if (!(var4 instanceof Symbol.MethodSymbol)) {
            if (var4 instanceof Symbol.VarSymbol) {
               Symbol.VarSymbol var13 = (Symbol.VarSymbol)var4;
               this.poolbuf.appendByte(9);
               this.poolbuf.appendChar(var1.put(var13.owner));
               this.poolbuf.appendChar(var1.put(this.nameType(var13)));
            } else if (var4 instanceof Name) {
               this.poolbuf.appendByte(1);
               byte[] var14 = ((Name)var4).toUtf();
               this.poolbuf.appendChar(var14.length);
               this.poolbuf.appendBytes(var14, 0, var14.length);
               if (var14.length > 65535) {
                  throw new StringOverflow(var4.toString());
               }
            } else if (var4 instanceof Symbol.ClassSymbol) {
               Symbol.ClassSymbol var15 = (Symbol.ClassSymbol)var4;
               if (var15.owner.kind == 2) {
                  var1.put(var15.owner);
               }

               this.poolbuf.appendByte(7);
               if (var15.type.hasTag(TypeTag.ARRAY)) {
                  this.poolbuf.appendChar(var1.put(this.typeSig(var15.type)));
               } else {
                  this.poolbuf.appendChar(var1.put(this.names.fromUtf(externalize(var15.flatname))));
                  this.enterInner(var15);
               }
            } else if (var4 instanceof ClassFile.NameAndType) {
               ClassFile.NameAndType var16 = (ClassFile.NameAndType)var4;
               this.poolbuf.appendByte(12);
               this.poolbuf.appendChar(var1.put(var16.name));
               this.poolbuf.appendChar(var1.put(this.typeSig(var16.uniqueType.type)));
            } else if (var4 instanceof Integer) {
               this.poolbuf.appendByte(3);
               this.poolbuf.appendInt((Integer)var4);
            } else if (var4 instanceof Long) {
               this.poolbuf.appendByte(5);
               this.poolbuf.appendLong((Long)var4);
               ++var3;
            } else if (var4 instanceof Float) {
               this.poolbuf.appendByte(4);
               this.poolbuf.appendFloat((Float)var4);
            } else if (var4 instanceof Double) {
               this.poolbuf.appendByte(6);
               this.poolbuf.appendDouble((Double)var4);
               ++var3;
            } else if (var4 instanceof String) {
               this.poolbuf.appendByte(8);
               this.poolbuf.appendChar(var1.put(this.names.fromString((String)var4)));
            } else if (var4 instanceof Types.UniqueType) {
               Type var17 = ((Types.UniqueType)var4).type;
               if (var17 instanceof Type.MethodType) {
                  this.poolbuf.appendByte(16);
                  this.poolbuf.appendChar(var1.put(this.typeSig((Type.MethodType)var17)));
               } else {
                  if (var17.hasTag(TypeTag.CLASS)) {
                     this.enterInner((Symbol.ClassSymbol)var17.tsym);
                  }

                  this.poolbuf.appendByte(7);
                  this.poolbuf.appendChar(var1.put(this.xClassName(var17)));
               }
            } else if (var4 instanceof Pool.MethodHandle) {
               Pool.MethodHandle var18 = (Pool.MethodHandle)var4;
               this.poolbuf.appendByte(15);
               this.poolbuf.appendByte(var18.refKind);
               this.poolbuf.appendChar(var1.put(var18.refSym));
            } else {
               Assert.error("writePool " + var4);
            }
         } else {
            Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)var4;
            if (!var5.isDynamic()) {
               this.poolbuf.appendByte((var5.owner.flags() & 512L) != 0L ? 11 : 10);
               this.poolbuf.appendChar(var1.put(var5.owner));
               this.poolbuf.appendChar(var1.put(this.nameType(var5)));
            } else {
               Symbol.DynamicMethodSymbol var6 = (Symbol.DynamicMethodSymbol)var5;
               Pool.MethodHandle var7 = new Pool.MethodHandle(var6.bsmKind, var6.bsm, this.types);
               Pool.DynamicMethod var8 = new Pool.DynamicMethod(var6, this.types);
               this.bootstrapMethods.put(var8, var7);
               var1.put(this.names.BootstrapMethods);
               var1.put(var7);
               Object[] var9 = var6.staticArgs;
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  Object var12 = var9[var11];
                  var1.put(var12);
               }

               this.poolbuf.appendByte(18);
               this.poolbuf.appendChar(this.bootstrapMethods.size() - 1);
               this.poolbuf.appendChar(var1.put(this.nameType(var6)));
            }
         }
      }

      if (var1.pp > 65535) {
         throw new PoolOverflow();
      } else {
         this.putChar(this.poolbuf, var2, var1.pp);
      }
   }

   Name fieldName(Symbol var1) {
      return (!this.scramble || (var1.flags() & 2L) == 0L) && (!this.scrambleAll || (var1.flags() & 5L) != 0L) ? var1.name : this.names.fromString("_$" + var1.name.getIndex());
   }

   ClassFile.NameAndType nameType(Symbol var1) {
      return new ClassFile.NameAndType(this.fieldName(var1), this.retrofit ? var1.erasure(this.types) : var1.externalType(this.types), this.types);
   }

   int writeAttr(Name var1) {
      this.databuf.appendChar(this.pool.put(var1));
      this.databuf.appendInt(0);
      return this.databuf.length;
   }

   void endAttr(int var1) {
      this.putInt(this.databuf, var1 - 4, this.databuf.length - var1);
   }

   int beginAttrs() {
      this.databuf.appendChar(0);
      return this.databuf.length;
   }

   void endAttrs(int var1, int var2) {
      this.putChar(this.databuf, var1 - 2, var2);
   }

   int writeEnclosingMethodAttribute(Symbol.ClassSymbol var1) {
      return !this.target.hasEnclosingMethodAttribute() ? 0 : this.writeEnclosingMethodAttribute(this.names.EnclosingMethod, var1);
   }

   protected int writeEnclosingMethodAttribute(Name var1, Symbol.ClassSymbol var2) {
      if (var2.owner.kind != 16 && var2.name != this.names.empty) {
         return 0;
      } else {
         int var3 = this.writeAttr(var1);
         Symbol.ClassSymbol var4 = var2.owner.enclClass();
         Symbol.MethodSymbol var5 = var2.owner.type != null && var2.owner.kind == 16 ? (Symbol.MethodSymbol)var2.owner : null;
         this.databuf.appendChar(this.pool.put(var4));
         this.databuf.appendChar(var5 == null ? 0 : this.pool.put(this.nameType(var2.owner)));
         this.endAttr(var3);
         return 1;
      }
   }

   int writeFlagAttrs(long var1) {
      int var3 = 0;
      int var4;
      if ((var1 & 131072L) != 0L) {
         var4 = this.writeAttr(this.names.Deprecated);
         this.endAttr(var4);
         ++var3;
      }

      if ((var1 & 16384L) != 0L && !this.target.useEnumFlag()) {
         var4 = this.writeAttr(this.names.Enum);
         this.endAttr(var4);
         ++var3;
      }

      if ((var1 & 4096L) != 0L && !this.target.useSyntheticFlag()) {
         var4 = this.writeAttr(this.names.Synthetic);
         this.endAttr(var4);
         ++var3;
      }

      if ((var1 & 2147483648L) != 0L && !this.target.useBridgeFlag()) {
         var4 = this.writeAttr(this.names.Bridge);
         this.endAttr(var4);
         ++var3;
      }

      if ((var1 & 17179869184L) != 0L && !this.target.useVarargsFlag()) {
         var4 = this.writeAttr(this.names.Varargs);
         this.endAttr(var4);
         ++var3;
      }

      if ((var1 & 8192L) != 0L && !this.target.useAnnotationFlag()) {
         var4 = this.writeAttr(this.names.Annotation);
         this.endAttr(var4);
         ++var3;
      }

      return var3;
   }

   int writeMemberAttrs(Symbol var1) {
      int var2 = this.writeFlagAttrs(var1.flags());
      long var3 = var1.flags();
      if (this.source.allowGenerics() && (var3 & 2147487744L) != 4096L && (var3 & 536870912L) == 0L && (!this.types.isSameType(var1.type, var1.erasure(this.types)) || this.signatureGen.hasTypeVar(var1.type.getThrownTypes()))) {
         int var5 = this.writeAttr(this.names.Signature);
         this.databuf.appendChar(this.pool.put(this.typeSig(var1.type)));
         this.endAttr(var5);
         ++var2;
      }

      var2 += this.writeJavaAnnotations(var1.getRawAttributes());
      var2 += this.writeTypeAnnotations(var1.getRawTypeAttributes(), false);
      return var2;
   }

   int writeMethodParametersAttr(Symbol.MethodSymbol var1) {
      Type.MethodType var2 = var1.externalType(this.types).asMethodType();
      int var3 = var2.argtypes.size();
      if (var1.params != null && var3 != 0) {
         int var4 = this.writeAttr(this.names.MethodParameters);
         this.databuf.appendByte(var3);
         Iterator var5 = var1.extraParams.iterator();

         Symbol.VarSymbol var6;
         int var7;
         while(var5.hasNext()) {
            var6 = (Symbol.VarSymbol)var5.next();
            var7 = (int)var6.flags() & '逐' | (int)var1.flags() & 4096;
            this.databuf.appendChar(this.pool.put(var6.name));
            this.databuf.appendChar(var7);
         }

         var5 = var1.params.iterator();

         while(var5.hasNext()) {
            var6 = (Symbol.VarSymbol)var5.next();
            var7 = (int)var6.flags() & '逐' | (int)var1.flags() & 4096;
            this.databuf.appendChar(this.pool.put(var6.name));
            this.databuf.appendChar(var7);
         }

         var5 = var1.capturedLocals.iterator();

         while(var5.hasNext()) {
            var6 = (Symbol.VarSymbol)var5.next();
            var7 = (int)var6.flags() & '逐' | (int)var1.flags() & 4096;
            this.databuf.appendChar(this.pool.put(var6.name));
            this.databuf.appendChar(var7);
         }

         this.endAttr(var4);
         return 1;
      } else {
         return 0;
      }
   }

   int writeParameterAttrs(Symbol.MethodSymbol var1) {
      boolean var2 = false;
      boolean var3 = false;
      Iterator var6;
      if (var1.params != null) {
         Iterator var4 = var1.params.iterator();

         while(var4.hasNext()) {
            Symbol.VarSymbol var5 = (Symbol.VarSymbol)var4.next();
            var6 = var5.getRawAttributes().iterator();

            while(var6.hasNext()) {
               Attribute.Compound var7 = (Attribute.Compound)var6.next();
               switch (this.types.getRetention(var7)) {
                  case SOURCE:
                  default:
                     break;
                  case CLASS:
                     var3 = true;
                     break;
                  case RUNTIME:
                     var2 = true;
               }
            }
         }
      }

      int var11 = 0;
      ListBuffer var8;
      Iterator var9;
      Attribute.Compound var10;
      int var12;
      Symbol.VarSymbol var13;
      if (var2) {
         var12 = this.writeAttr(this.names.RuntimeVisibleParameterAnnotations);
         this.databuf.appendByte(var1.params.length());
         var6 = var1.params.iterator();

         while(var6.hasNext()) {
            var13 = (Symbol.VarSymbol)var6.next();
            var8 = new ListBuffer();
            var9 = var13.getRawAttributes().iterator();

            while(var9.hasNext()) {
               var10 = (Attribute.Compound)var9.next();
               if (this.types.getRetention(var10) == Attribute.RetentionPolicy.RUNTIME) {
                  var8.append(var10);
               }
            }

            this.databuf.appendChar(var8.length());
            var9 = var8.iterator();

            while(var9.hasNext()) {
               var10 = (Attribute.Compound)var9.next();
               this.writeCompoundAttribute(var10);
            }
         }

         this.endAttr(var12);
         ++var11;
      }

      if (var3) {
         var12 = this.writeAttr(this.names.RuntimeInvisibleParameterAnnotations);
         this.databuf.appendByte(var1.params.length());
         var6 = var1.params.iterator();

         while(var6.hasNext()) {
            var13 = (Symbol.VarSymbol)var6.next();
            var8 = new ListBuffer();
            var9 = var13.getRawAttributes().iterator();

            while(var9.hasNext()) {
               var10 = (Attribute.Compound)var9.next();
               if (this.types.getRetention(var10) == Attribute.RetentionPolicy.CLASS) {
                  var8.append(var10);
               }
            }

            this.databuf.appendChar(var8.length());
            var9 = var8.iterator();

            while(var9.hasNext()) {
               var10 = (Attribute.Compound)var9.next();
               this.writeCompoundAttribute(var10);
            }
         }

         this.endAttr(var12);
         ++var11;
      }

      return var11;
   }

   int writeJavaAnnotations(List var1) {
      if (var1.isEmpty()) {
         return 0;
      } else {
         ListBuffer var2 = new ListBuffer();
         ListBuffer var3 = new ListBuffer();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Attribute.Compound var5 = (Attribute.Compound)var4.next();
            switch (this.types.getRetention(var5)) {
               case SOURCE:
               default:
                  break;
               case CLASS:
                  var3.append(var5);
                  break;
               case RUNTIME:
                  var2.append(var5);
            }
         }

         int var8 = 0;
         Iterator var6;
         Attribute.Compound var7;
         int var9;
         if (var2.length() != 0) {
            var9 = this.writeAttr(this.names.RuntimeVisibleAnnotations);
            this.databuf.appendChar(var2.length());
            var6 = var2.iterator();

            while(var6.hasNext()) {
               var7 = (Attribute.Compound)var6.next();
               this.writeCompoundAttribute(var7);
            }

            this.endAttr(var9);
            ++var8;
         }

         if (var3.length() != 0) {
            var9 = this.writeAttr(this.names.RuntimeInvisibleAnnotations);
            this.databuf.appendChar(var3.length());
            var6 = var3.iterator();

            while(var6.hasNext()) {
               var7 = (Attribute.Compound)var6.next();
               this.writeCompoundAttribute(var7);
            }

            this.endAttr(var9);
            ++var8;
         }

         return var8;
      }
   }

   int writeTypeAnnotations(List var1, boolean var2) {
      if (var1.isEmpty()) {
         return 0;
      } else {
         ListBuffer var3 = new ListBuffer();
         ListBuffer var4 = new ListBuffer();
         Iterator var5 = var1.iterator();

         while(true) {
            while(var5.hasNext()) {
               Attribute.TypeCompound var6 = (Attribute.TypeCompound)var5.next();
               if (var6.hasUnknownPosition()) {
                  boolean var7 = var6.tryFixPosition();
                  if (!var7) {
                     PrintWriter var8 = this.log.getWriter(Log.WriterKind.ERROR);
                     var8.println("ClassWriter: Position UNKNOWN in type annotation: " + var6);
                     continue;
                  }
               }

               if (var6.position.type.isLocal() == var2 && var6.position.emitToClassfile()) {
                  switch (this.types.getRetention((Attribute.Compound)var6)) {
                     case SOURCE:
                     default:
                        break;
                     case CLASS:
                        var4.append(var6);
                        break;
                     case RUNTIME:
                        var3.append(var6);
                  }
               }
            }

            int var9 = 0;
            int var10;
            Iterator var11;
            Attribute.TypeCompound var12;
            if (var3.length() != 0) {
               var10 = this.writeAttr(this.names.RuntimeVisibleTypeAnnotations);
               this.databuf.appendChar(var3.length());
               var11 = var3.iterator();

               while(var11.hasNext()) {
                  var12 = (Attribute.TypeCompound)var11.next();
                  this.writeTypeAnnotation(var12);
               }

               this.endAttr(var10);
               ++var9;
            }

            if (var4.length() != 0) {
               var10 = this.writeAttr(this.names.RuntimeInvisibleTypeAnnotations);
               this.databuf.appendChar(var4.length());
               var11 = var4.iterator();

               while(var11.hasNext()) {
                  var12 = (Attribute.TypeCompound)var11.next();
                  this.writeTypeAnnotation(var12);
               }

               this.endAttr(var10);
               ++var9;
            }

            return var9;
         }
      }
   }

   void writeCompoundAttribute(Attribute.Compound var1) {
      this.databuf.appendChar(this.pool.put(this.typeSig(var1.type)));
      this.databuf.appendChar(var1.values.length());
      Iterator var2 = var1.values.iterator();

      while(var2.hasNext()) {
         Pair var3 = (Pair)var2.next();
         this.databuf.appendChar(this.pool.put(((Symbol.MethodSymbol)var3.fst).name));
         ((Attribute)var3.snd).accept(this.awriter);
      }

   }

   void writeTypeAnnotation(Attribute.TypeCompound var1) {
      this.writePosition(var1.position);
      this.writeCompoundAttribute(var1);
   }

   void writePosition(TypeAnnotationPosition var1) {
      this.databuf.appendByte(var1.type.targetTypeValue());
      label36:
      switch (var1.type) {
         case INSTANCEOF:
         case NEW:
         case CONSTRUCTOR_REFERENCE:
         case METHOD_REFERENCE:
            this.databuf.appendChar(var1.offset);
            break;
         case LOCAL_VARIABLE:
         case RESOURCE_VARIABLE:
            this.databuf.appendChar(var1.lvarOffset.length);
            int var2 = 0;

            while(true) {
               if (var2 >= var1.lvarOffset.length) {
                  break label36;
               }

               this.databuf.appendChar(var1.lvarOffset[var2]);
               this.databuf.appendChar(var1.lvarLength[var2]);
               this.databuf.appendChar(var1.lvarIndex[var2]);
               ++var2;
            }
         case EXCEPTION_PARAMETER:
            this.databuf.appendChar(var1.exception_index);
         case METHOD_RECEIVER:
         case METHOD_RETURN:
         case FIELD:
            break;
         case CLASS_TYPE_PARAMETER:
         case METHOD_TYPE_PARAMETER:
            this.databuf.appendByte(var1.parameter_index);
            break;
         case CLASS_TYPE_PARAMETER_BOUND:
         case METHOD_TYPE_PARAMETER_BOUND:
            this.databuf.appendByte(var1.parameter_index);
            this.databuf.appendByte(var1.bound_index);
            break;
         case CLASS_EXTENDS:
            this.databuf.appendChar(var1.type_index);
            break;
         case THROWS:
            this.databuf.appendChar(var1.type_index);
            break;
         case METHOD_FORMAL_PARAMETER:
            this.databuf.appendByte(var1.parameter_index);
            break;
         case CAST:
         case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
         case METHOD_INVOCATION_TYPE_ARGUMENT:
         case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
         case METHOD_REFERENCE_TYPE_ARGUMENT:
            this.databuf.appendChar(var1.offset);
            this.databuf.appendByte(var1.type_index);
            break;
         case UNKNOWN:
            throw new AssertionError("jvm.ClassWriter: UNKNOWN target type should never occur!");
         default:
            throw new AssertionError("jvm.ClassWriter: Unknown target type for position: " + var1);
      }

      this.databuf.appendByte(var1.location.size());
      List var5 = TypeAnnotationPosition.getBinaryFromTypePath(var1.location);
      Iterator var3 = var5.iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         this.databuf.appendByte((byte)var4);
      }

   }

   void enterInner(Symbol.ClassSymbol var1) {
      if (var1.type.isCompound()) {
         throw new AssertionError("Unexpected intersection type: " + var1.type);
      } else {
         try {
            var1.complete();
         } catch (Symbol.CompletionFailure var3) {
            System.err.println("error: " + var1 + ": " + var3.getMessage());
            throw var3;
         }

         if (var1.type.hasTag(TypeTag.CLASS)) {
            if (this.pool != null && var1.owner.enclClass() != null && (this.innerClasses == null || !this.innerClasses.contains(var1))) {
               this.enterInner(var1.owner.enclClass());
               this.pool.put(var1);
               if (var1.name != this.names.empty) {
                  this.pool.put(var1.name);
               }

               if (this.innerClasses == null) {
                  this.innerClasses = new HashSet();
                  this.innerClassesQueue = new ListBuffer();
                  this.pool.put(this.names.InnerClasses);
               }

               this.innerClasses.add(var1);
               this.innerClassesQueue.append(var1);
            }

         }
      }
   }

   void writeInnerClasses() {
      int var1 = this.writeAttr(this.names.InnerClasses);
      this.databuf.appendChar(this.innerClassesQueue.length());

      for(List var2 = this.innerClassesQueue.toList(); var2.nonEmpty(); var2 = var2.tail) {
         Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.head;
         var3.markAbstractIfNeeded(this.types);
         char var4 = (char)this.adjustFlags(var3.flags_field);
         if ((var4 & 512) != 0) {
            var4 = (char)(var4 | 1024);
         }

         if (var3.name.isEmpty()) {
            var4 = (char)(var4 & -17);
         }

         var4 = (char)(var4 & -2049);
         if (this.dumpInnerClassModifiers) {
            PrintWriter var5 = this.log.getWriter(Log.WriterKind.ERROR);
            var5.println("INNERCLASS  " + var3.name);
            var5.println("---" + flagNames((long)var4));
         }

         this.databuf.appendChar(this.pool.get(var3));
         this.databuf.appendChar(var3.owner.kind == 2 && !var3.name.isEmpty() ? this.pool.get(var3.owner) : 0);
         this.databuf.appendChar(!var3.name.isEmpty() ? this.pool.get(var3.name) : 0);
         this.databuf.appendChar(var4);
      }

      this.endAttr(var1);
   }

   void writeBootstrapMethods() {
      int var1 = this.writeAttr(this.names.BootstrapMethods);
      this.databuf.appendChar(this.bootstrapMethods.size());
      Iterator var2 = this.bootstrapMethods.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Pool.DynamicMethod var4 = (Pool.DynamicMethod)var3.getKey();
         Symbol.DynamicMethodSymbol var5 = (Symbol.DynamicMethodSymbol)var4.baseSymbol();
         this.databuf.appendChar(this.pool.get(var3.getValue()));
         this.databuf.appendChar(var5.staticArgs.length);
         Object[] var6 = var4.uniqueStaticArgs;
         Object[] var7 = var6;
         int var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Object var10 = var7[var9];
            this.databuf.appendChar(this.pool.get(var10));
         }
      }

      this.endAttr(var1);
   }

   void writeField(Symbol.VarSymbol var1) {
      int var2 = this.adjustFlags(var1.flags());
      this.databuf.appendChar(var2);
      if (this.dumpFieldModifiers) {
         PrintWriter var3 = this.log.getWriter(Log.WriterKind.ERROR);
         var3.println("FIELD  " + this.fieldName(var1));
         var3.println("---" + flagNames(var1.flags()));
      }

      this.databuf.appendChar(this.pool.put(this.fieldName(var1)));
      this.databuf.appendChar(this.pool.put(this.typeSig(var1.erasure(this.types))));
      int var6 = this.beginAttrs();
      int var4 = 0;
      if (var1.getConstValue() != null) {
         int var5 = this.writeAttr(this.names.ConstantValue);
         this.databuf.appendChar(this.pool.put(var1.getConstValue()));
         this.endAttr(var5);
         ++var4;
      }

      var4 += this.writeMemberAttrs(var1);
      this.endAttrs(var6, var4);
   }

   void writeMethod(Symbol.MethodSymbol var1) {
      int var2 = this.adjustFlags(var1.flags());
      this.databuf.appendChar(var2);
      if (this.dumpMethodModifiers) {
         PrintWriter var3 = this.log.getWriter(Log.WriterKind.ERROR);
         var3.println("METHOD  " + this.fieldName(var1));
         var3.println("---" + flagNames(var1.flags()));
      }

      this.databuf.appendChar(this.pool.put(this.fieldName(var1)));
      this.databuf.appendChar(this.pool.put(this.typeSig(var1.externalType(this.types))));
      int var8 = this.beginAttrs();
      int var4 = 0;
      if (var1.code != null) {
         int var5 = this.writeAttr(this.names.Code);
         this.writeCode(var1.code);
         var1.code = null;
         this.endAttr(var5);
         ++var4;
      }

      List var9 = var1.erasure(this.types).getThrownTypes();
      int var6;
      if (var9.nonEmpty()) {
         var6 = this.writeAttr(this.names.Exceptions);
         this.databuf.appendChar(var9.length());

         for(List var7 = var9; var7.nonEmpty(); var7 = var7.tail) {
            this.databuf.appendChar(this.pool.put(((Type)var7.head).tsym));
         }

         this.endAttr(var6);
         ++var4;
      }

      if (var1.defaultValue != null) {
         var6 = this.writeAttr(this.names.AnnotationDefault);
         var1.defaultValue.accept(this.awriter);
         this.endAttr(var6);
         ++var4;
      }

      if (this.options.isSet(Option.PARAMETERS)) {
         var4 += this.writeMethodParametersAttr(var1);
      }

      var4 += this.writeMemberAttrs(var1);
      var4 += this.writeParameterAttrs(var1);
      this.endAttrs(var8, var4);
   }

   void writeCode(Code var1) {
      this.databuf.appendChar(var1.max_stack);
      this.databuf.appendChar(var1.max_locals);
      this.databuf.appendInt(var1.cp);
      this.databuf.appendBytes(var1.code, 0, var1.cp);
      this.databuf.appendChar(var1.catchInfo.length());

      int var3;
      for(List var2 = var1.catchInfo.toList(); var2.nonEmpty(); var2 = var2.tail) {
         for(var3 = 0; var3 < ((char[])var2.head).length; ++var3) {
            this.databuf.appendChar(((char[])var2.head)[var3]);
         }
      }

      int var12 = this.beginAttrs();
      var3 = 0;
      int var4;
      int var6;
      if (var1.lineInfo.nonEmpty()) {
         var4 = this.writeAttr(this.names.LineNumberTable);
         this.databuf.appendChar(var1.lineInfo.length());

         for(List var5 = var1.lineInfo.reverse(); var5.nonEmpty(); var5 = var5.tail) {
            for(var6 = 0; var6 < ((char[])var5.head).length; ++var6) {
               this.databuf.appendChar(((char[])var5.head)[var6]);
            }
         }

         this.endAttr(var4);
         ++var3;
      }

      int var7;
      int var14;
      if (this.genCrt && var1.crt != null) {
         CRTable var13 = var1.crt;
         var14 = this.writeAttr(this.names.CharacterRangeTable);
         var6 = this.beginAttrs();
         var7 = var13.writeCRT(this.databuf, var1.lineMap, this.log);
         this.endAttrs(var6, var7);
         this.endAttr(var14);
         ++var3;
      }

      if (var1.varDebugInfo && var1.varBufferSize > 0) {
         var4 = 0;
         var14 = this.writeAttr(this.names.LocalVariableTable);
         this.databuf.appendChar(var1.getLVTSize());

         for(var6 = 0; var6 < var1.varBufferSize; ++var6) {
            Code.LocalVar var15 = var1.varBuffer[var6];
            Iterator var8 = var15.aliveRanges.iterator();

            while(var8.hasNext()) {
               Code.LocalVar.Range var9 = (Code.LocalVar.Range)var8.next();
               Assert.check(var9.start_pc >= 0 && var9.start_pc <= var1.cp);
               this.databuf.appendChar(var9.start_pc);
               Assert.check(var9.length > 0 && var9.start_pc + var9.length <= var1.cp);
               this.databuf.appendChar(var9.length);
               Symbol.VarSymbol var10 = var15.sym;
               this.databuf.appendChar(this.pool.put(var10.name));
               Type var11 = var10.erasure(this.types);
               this.databuf.appendChar(this.pool.put(this.typeSig(var11)));
               this.databuf.appendChar(var15.reg);
               if (this.needsLocalVariableTypeEntry(var15.sym.type)) {
                  ++var4;
               }
            }
         }

         this.endAttr(var14);
         ++var3;
         if (var4 > 0) {
            var14 = this.writeAttr(this.names.LocalVariableTypeTable);
            this.databuf.appendChar(var4);
            var6 = 0;

            for(var7 = 0; var7 < var1.varBufferSize; ++var7) {
               Code.LocalVar var16 = var1.varBuffer[var7];
               Symbol.VarSymbol var17 = var16.sym;
               if (this.needsLocalVariableTypeEntry(var17.type)) {
                  for(Iterator var18 = var16.aliveRanges.iterator(); var18.hasNext(); ++var6) {
                     Code.LocalVar.Range var19 = (Code.LocalVar.Range)var18.next();
                     this.databuf.appendChar(var19.start_pc);
                     this.databuf.appendChar(var19.length);
                     this.databuf.appendChar(this.pool.put(var17.name));
                     this.databuf.appendChar(this.pool.put(this.typeSig(var17.type)));
                     this.databuf.appendChar(var16.reg);
                  }
               }
            }

            Assert.check(var6 == var4);
            this.endAttr(var14);
            ++var3;
         }
      }

      if (var1.stackMapBufferSize > 0) {
         if (this.debugstackmap) {
            System.out.println("Stack map for " + var1.meth);
         }

         var4 = this.writeAttr(var1.stackMap.getAttributeName(this.names));
         this.writeStackMap(var1);
         this.endAttr(var4);
         ++var3;
      }

      var3 += this.writeTypeAnnotations(var1.meth.getRawTypeAttributes(), true);
      this.endAttrs(var12, var3);
   }

   private boolean needsLocalVariableTypeEntry(Type var1) {
      return !this.types.isSameType(var1, this.types.erasure(var1)) && !var1.isCompound();
   }

   void writeStackMap(Code var1) {
      int var2 = var1.stackMapBufferSize;
      if (this.debugstackmap) {
         System.out.println(" nframes = " + var2);
      }

      this.databuf.appendChar(var2);
      int var3;
      switch (var1.stackMap) {
         case CLDC:
            for(var3 = 0; var3 < var2; ++var3) {
               if (this.debugstackmap) {
                  System.out.print("  " + var3 + ":");
               }

               Code.StackMapFrame var8 = var1.stackMapBuffer[var3];
               if (this.debugstackmap) {
                  System.out.print(" pc=" + var8.pc);
               }

               this.databuf.appendChar(var8.pc);
               int var5 = 0;

               int var6;
               for(var6 = 0; var6 < var8.locals.length; var6 += this.target.generateEmptyAfterBig() ? 1 : Code.width(var8.locals[var6])) {
                  ++var5;
               }

               if (this.debugstackmap) {
                  System.out.print(" nlocals=" + var5);
               }

               this.databuf.appendChar(var5);

               for(var6 = 0; var6 < var8.locals.length; var6 += this.target.generateEmptyAfterBig() ? 1 : Code.width(var8.locals[var6])) {
                  if (this.debugstackmap) {
                     System.out.print(" local[" + var6 + "]=");
                  }

                  this.writeStackMapType(var8.locals[var6]);
               }

               var6 = 0;

               int var7;
               for(var7 = 0; var7 < var8.stack.length; var7 += this.target.generateEmptyAfterBig() ? 1 : Code.width(var8.stack[var7])) {
                  ++var6;
               }

               if (this.debugstackmap) {
                  System.out.print(" nstack=" + var6);
               }

               this.databuf.appendChar(var6);

               for(var7 = 0; var7 < var8.stack.length; var7 += this.target.generateEmptyAfterBig() ? 1 : Code.width(var8.stack[var7])) {
                  if (this.debugstackmap) {
                     System.out.print(" stack[" + var7 + "]=");
                  }

                  this.writeStackMapType(var8.stack[var7]);
               }

               if (this.debugstackmap) {
                  System.out.println();
               }
            }

            return;
         case JSR202:
            Assert.checkNull(var1.stackMapBuffer);

            for(var3 = 0; var3 < var2; ++var3) {
               if (this.debugstackmap) {
                  System.out.print("  " + var3 + ":");
               }

               StackMapTableFrame var4 = var1.stackMapTableBuffer[var3];
               var4.write(this);
               if (this.debugstackmap) {
                  System.out.println();
               }
            }

            return;
         default:
            throw new AssertionError("Unexpected stackmap format value");
      }
   }

   void writeStackMapType(Type var1) {
      if (var1 == null) {
         if (this.debugstackmap) {
            System.out.print("empty");
         }

         this.databuf.appendByte(0);
      } else {
         switch (var1.getTag()) {
            case UNINITIALIZED_THIS:
               if (this.debugstackmap) {
                  System.out.print("uninit_this");
               }

               this.databuf.appendByte(6);
               break;
            case UNINITIALIZED_OBJECT:
               UninitializedType var2 = (UninitializedType)var1;
               this.databuf.appendByte(8);
               if (this.debugstackmap) {
                  System.out.print("uninit_object@" + var2.offset);
               }

               this.databuf.appendChar(var2.offset);
               break;
            case BYTE:
            case CHAR:
            case SHORT:
            case INT:
            case BOOLEAN:
               if (this.debugstackmap) {
                  System.out.print("int");
               }

               this.databuf.appendByte(1);
               break;
            case LONG:
               if (this.debugstackmap) {
                  System.out.print("long");
               }

               this.databuf.appendByte(4);
               break;
            case FLOAT:
               if (this.debugstackmap) {
                  System.out.print("float");
               }

               this.databuf.appendByte(2);
               break;
            case DOUBLE:
               if (this.debugstackmap) {
                  System.out.print("double");
               }

               this.databuf.appendByte(3);
               break;
            case CLASS:
            case ARRAY:
               if (this.debugstackmap) {
                  System.out.print("object(" + var1 + ")");
               }

               this.databuf.appendByte(7);
               this.databuf.appendChar(this.pool.put(var1));
               break;
            case BOT:
               if (this.debugstackmap) {
                  System.out.print("null");
               }

               this.databuf.appendByte(5);
               break;
            case TYPEVAR:
               if (this.debugstackmap) {
                  System.out.print("object(" + this.types.erasure(var1).tsym + ")");
               }

               this.databuf.appendByte(7);
               this.databuf.appendChar(this.pool.put(this.types.erasure(var1).tsym));
               break;
            default:
               throw new AssertionError();
         }
      }

   }

   void writeFields(Scope.Entry var1) {
      List var2 = List.nil();

      for(Scope.Entry var3 = var1; var3 != null; var3 = var3.sibling) {
         if (var3.sym.kind == 4) {
            var2 = var2.prepend((Symbol.VarSymbol)var3.sym);
         }
      }

      while(var2.nonEmpty()) {
         this.writeField((Symbol.VarSymbol)var2.head);
         var2 = var2.tail;
      }

   }

   void writeMethods(Scope.Entry var1) {
      List var2 = List.nil();

      for(Scope.Entry var3 = var1; var3 != null; var3 = var3.sibling) {
         if (var3.sym.kind == 16 && (var3.sym.flags() & 137438953472L) == 0L) {
            var2 = var2.prepend((Symbol.MethodSymbol)var3.sym);
         }
      }

      while(var2.nonEmpty()) {
         this.writeMethod((Symbol.MethodSymbol)var2.head);
         var2 = var2.tail;
      }

   }

   public JavaFileObject writeClass(Symbol.ClassSymbol var1) throws IOException, PoolOverflow, StringOverflow {
      JavaFileObject var2 = this.fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, var1.flatname.toString(), Kind.CLASS, var1.sourcefile);
      OutputStream var3 = var2.openOutputStream();

      try {
         this.writeClassFile(var3, var1);
         if (this.verbose) {
            this.log.printVerbose("wrote.file", var2);
         }

         var3.close();
         var3 = null;
      } finally {
         if (var3 != null) {
            var3.close();
            var2.delete();
            var2 = null;
         }

      }

      return var2;
   }

   public void writeClassFile(OutputStream var1, Symbol.ClassSymbol var2) throws IOException, PoolOverflow, StringOverflow {
      Assert.check((var2.flags() & 16777216L) == 0L);
      this.databuf.reset();
      this.poolbuf.reset();
      this.signatureGen.reset();
      this.pool = var2.pool;
      this.innerClasses = null;
      this.innerClassesQueue = null;
      this.bootstrapMethods = new LinkedHashMap();
      Type var3 = this.types.supertype(var2.type);
      List var4 = this.types.interfaces(var2.type);
      List var5 = var2.type.getTypeArguments();
      int var6 = this.adjustFlags(var2.flags() & -8796093022209L);
      if ((var6 & 4) != 0) {
         var6 |= 1;
      }

      var6 = var6 & 32273 & -2049;
      if ((var6 & 512) == 0) {
         var6 |= 32;
      }

      if (var2.isInner() && var2.name.isEmpty()) {
         var6 &= -17;
      }

      if (this.dumpClassModifiers) {
         PrintWriter var7 = this.log.getWriter(Log.WriterKind.ERROR);
         var7.println();
         var7.println("CLASSFILE  " + var2.getQualifiedName());
         var7.println("---" + flagNames((long)var6));
      }

      this.databuf.appendChar(var6);
      this.databuf.appendChar(this.pool.put(var2));
      this.databuf.appendChar(var3.hasTag(TypeTag.CLASS) ? this.pool.put(var3.tsym) : 0);
      this.databuf.appendChar(var4.length());

      for(List var14 = var4; var14.nonEmpty(); var14 = var14.tail) {
         this.databuf.appendChar(this.pool.put(((Type)var14.head).tsym));
      }

      int var15 = 0;
      int var8 = 0;

      for(Scope.Entry var9 = var2.members().elems; var9 != null; var9 = var9.sibling) {
         switch (var9.sym.kind) {
            case 2:
               this.enterInner((Symbol.ClassSymbol)var9.sym);
               break;
            case 4:
               ++var15;
               break;
            case 16:
               if ((var9.sym.flags() & 137438953472L) == 0L) {
                  ++var8;
               }
               break;
            default:
               Assert.error();
         }
      }

      if (var2.trans_local != null) {
         Iterator var16 = var2.trans_local.iterator();

         while(var16.hasNext()) {
            Symbol.ClassSymbol var10 = (Symbol.ClassSymbol)var16.next();
            this.enterInner(var10);
         }
      }

      this.databuf.appendChar(var15);
      this.writeFields(var2.members().elems);
      this.databuf.appendChar(var8);
      this.writeMethods(var2.members().elems);
      int var17 = this.beginAttrs();
      int var18 = 0;
      boolean var11 = var5.length() != 0 || var3.allparams().length() != 0;

      for(List var12 = var4; !var11 && var12.nonEmpty(); var12 = var12.tail) {
         var11 = ((Type)var12.head).allparams().length() != 0;
      }

      int var19;
      if (var11) {
         Assert.check(this.source.allowGenerics());
         var19 = this.writeAttr(this.names.Signature);
         if (var5.length() != 0) {
            this.signatureGen.assembleParamsSig(var5);
         }

         this.signatureGen.assembleSig(var3);

         for(List var13 = var4; var13.nonEmpty(); var13 = var13.tail) {
            this.signatureGen.assembleSig((Type)var13.head);
         }

         this.databuf.appendChar(this.pool.put(this.signatureGen.toName()));
         this.signatureGen.reset();
         this.endAttr(var19);
         ++var18;
      }

      if (var2.sourcefile != null && this.emitSourceFile) {
         var19 = this.writeAttr(this.names.SourceFile);
         String var20 = BaseFileObject.getSimpleName(var2.sourcefile);
         this.databuf.appendChar(var2.pool.put(this.names.fromString(var20)));
         this.endAttr(var19);
         ++var18;
      }

      if (this.genCrt) {
         var19 = this.writeAttr(this.names.SourceID);
         this.databuf.appendChar(var2.pool.put(this.names.fromString(Long.toString(this.getLastModified(var2.sourcefile)))));
         this.endAttr(var19);
         ++var18;
         var19 = this.writeAttr(this.names.CompilationID);
         this.databuf.appendChar(var2.pool.put(this.names.fromString(Long.toString(System.currentTimeMillis()))));
         this.endAttr(var19);
         ++var18;
      }

      var18 += this.writeFlagAttrs(var2.flags());
      var18 += this.writeJavaAnnotations(var2.getRawAttributes());
      var18 += this.writeTypeAnnotations(var2.getRawTypeAttributes(), false);
      var18 += this.writeEnclosingMethodAttribute(var2);
      var18 += this.writeExtraClassAttributes(var2);
      this.poolbuf.appendInt(-889275714);
      this.poolbuf.appendChar(this.target.minorVersion);
      this.poolbuf.appendChar(this.target.majorVersion);
      this.writePool(var2.pool);
      if (this.innerClasses != null) {
         this.writeInnerClasses();
         ++var18;
      }

      if (!this.bootstrapMethods.isEmpty()) {
         this.writeBootstrapMethods();
         ++var18;
      }

      this.endAttrs(var17, var18);
      this.poolbuf.appendBytes(this.databuf.elems, 0, this.databuf.length);
      var1.write(this.poolbuf.elems, 0, this.poolbuf.length);
      this.pool = var2.pool = null;
   }

   protected int writeExtraClassAttributes(Symbol.ClassSymbol var1) {
      return 0;
   }

   int adjustFlags(long var1) {
      int var3 = (int)var1;
      if ((var1 & 4096L) != 0L && !this.target.useSyntheticFlag()) {
         var3 &= -4097;
      }

      if ((var1 & 16384L) != 0L && !this.target.useEnumFlag()) {
         var3 &= -16385;
      }

      if ((var1 & 8192L) != 0L && !this.target.useAnnotationFlag()) {
         var3 &= -8193;
      }

      if ((var1 & 2147483648L) != 0L && this.target.useBridgeFlag()) {
         var3 |= 64;
      }

      if ((var1 & 17179869184L) != 0L && this.target.useVarargsFlag()) {
         var3 |= 128;
      }

      if ((var1 & 8796093022208L) != 0L) {
         var3 &= -1025;
      }

      return var3;
   }

   long getLastModified(FileObject var1) {
      long var2 = 0L;

      try {
         var2 = var1.getLastModified();
         return var2;
      } catch (SecurityException var5) {
         throw new AssertionError("CRT: couldn't get source file modification date: " + var5.getMessage());
      }
   }

   abstract static class StackMapTableFrame {
      abstract int getFrameType();

      void write(ClassWriter var1) {
         int var2 = this.getFrameType();
         var1.databuf.appendByte(var2);
         if (var1.debugstackmap) {
            System.out.print(" frame_type=" + var2);
         }

      }

      static StackMapTableFrame getInstance(Code.StackMapFrame var0, int var1, Type[] var2, Types var3) {
         Type[] var4 = var0.locals;
         Type[] var5 = var0.stack;
         int var6 = var0.pc - var1 - 1;
         if (var5.length == 1) {
            if (var4.length == var2.length && compare(var2, var4, var3) == 0) {
               return new SameLocals1StackItemFrame(var6, var5[0]);
            }
         } else if (var5.length == 0) {
            int var7 = compare(var2, var4, var3);
            if (var7 == 0) {
               return new SameFrame(var6);
            }

            if (-4 < var7 && var7 < 0) {
               Type[] var8 = new Type[-var7];
               int var9 = var2.length;

               for(int var10 = 0; var9 < var4.length; ++var10) {
                  var8[var10] = var4[var9];
                  ++var9;
               }

               return new AppendFrame(251 - var7, var6, var8);
            }

            if (0 < var7 && var7 < 4) {
               return new ChopFrame(251 - var7, var6);
            }
         }

         return new FullFrame(var6, var4, var5);
      }

      static boolean isInt(Type var0) {
         return var0.getTag().isStrictSubRangeOf(TypeTag.INT) || var0.hasTag(TypeTag.BOOLEAN);
      }

      static boolean isSameType(Type var0, Type var1, Types var2) {
         if (var0 == null) {
            return var1 == null;
         } else if (var1 == null) {
            return false;
         } else if (isInt(var0) && isInt(var1)) {
            return true;
         } else if (var0.hasTag(TypeTag.UNINITIALIZED_THIS)) {
            return var1.hasTag(TypeTag.UNINITIALIZED_THIS);
         } else if (var0.hasTag(TypeTag.UNINITIALIZED_OBJECT)) {
            if (var1.hasTag(TypeTag.UNINITIALIZED_OBJECT)) {
               return ((UninitializedType)var0).offset == ((UninitializedType)var1).offset;
            } else {
               return false;
            }
         } else {
            return !var1.hasTag(TypeTag.UNINITIALIZED_THIS) && !var1.hasTag(TypeTag.UNINITIALIZED_OBJECT) ? var2.isSameType(var0, var1) : false;
         }
      }

      static int compare(Type[] var0, Type[] var1, Types var2) {
         int var3 = var0.length - var1.length;
         if (var3 <= 4 && var3 >= -4) {
            int var4 = var3 > 0 ? var1.length : var0.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               if (!isSameType(var0[var5], var1[var5], var2)) {
                  return Integer.MAX_VALUE;
               }
            }

            return var3;
         } else {
            return Integer.MAX_VALUE;
         }
      }

      static class FullFrame extends StackMapTableFrame {
         final int offsetDelta;
         final Type[] locals;
         final Type[] stack;

         FullFrame(int var1, Type[] var2, Type[] var3) {
            this.offsetDelta = var1;
            this.locals = var2;
            this.stack = var3;
         }

         int getFrameType() {
            return 255;
         }

         void write(ClassWriter var1) {
            super.write(var1);
            var1.databuf.appendChar(this.offsetDelta);
            var1.databuf.appendChar(this.locals.length);
            if (var1.debugstackmap) {
               System.out.print(" offset_delta=" + this.offsetDelta);
               System.out.print(" nlocals=" + this.locals.length);
            }

            int var2;
            for(var2 = 0; var2 < this.locals.length; ++var2) {
               if (var1.debugstackmap) {
                  System.out.print(" locals[" + var2 + "]=");
               }

               var1.writeStackMapType(this.locals[var2]);
            }

            var1.databuf.appendChar(this.stack.length);
            if (var1.debugstackmap) {
               System.out.print(" nstack=" + this.stack.length);
            }

            for(var2 = 0; var2 < this.stack.length; ++var2) {
               if (var1.debugstackmap) {
                  System.out.print(" stack[" + var2 + "]=");
               }

               var1.writeStackMapType(this.stack[var2]);
            }

         }
      }

      static class AppendFrame extends StackMapTableFrame {
         final int frameType;
         final int offsetDelta;
         final Type[] locals;

         AppendFrame(int var1, int var2, Type[] var3) {
            this.frameType = var1;
            this.offsetDelta = var2;
            this.locals = var3;
         }

         int getFrameType() {
            return this.frameType;
         }

         void write(ClassWriter var1) {
            super.write(var1);
            var1.databuf.appendChar(this.offsetDelta);
            if (var1.debugstackmap) {
               System.out.print(" offset_delta=" + this.offsetDelta);
            }

            for(int var2 = 0; var2 < this.locals.length; ++var2) {
               if (var1.debugstackmap) {
                  System.out.print(" locals[" + var2 + "]=");
               }

               var1.writeStackMapType(this.locals[var2]);
            }

         }
      }

      static class ChopFrame extends StackMapTableFrame {
         final int frameType;
         final int offsetDelta;

         ChopFrame(int var1, int var2) {
            this.frameType = var1;
            this.offsetDelta = var2;
         }

         int getFrameType() {
            return this.frameType;
         }

         void write(ClassWriter var1) {
            super.write(var1);
            var1.databuf.appendChar(this.offsetDelta);
            if (var1.debugstackmap) {
               System.out.print(" offset_delta=" + this.offsetDelta);
            }

         }
      }

      static class SameLocals1StackItemFrame extends StackMapTableFrame {
         final int offsetDelta;
         final Type stack;

         SameLocals1StackItemFrame(int var1, Type var2) {
            this.offsetDelta = var1;
            this.stack = var2;
         }

         int getFrameType() {
            return this.offsetDelta < 64 ? 64 + this.offsetDelta : 247;
         }

         void write(ClassWriter var1) {
            super.write(var1);
            if (this.getFrameType() == 247) {
               var1.databuf.appendChar(this.offsetDelta);
               if (var1.debugstackmap) {
                  System.out.print(" offset_delta=" + this.offsetDelta);
               }
            }

            if (var1.debugstackmap) {
               System.out.print(" stack[0]=");
            }

            var1.writeStackMapType(this.stack);
         }
      }

      static class SameFrame extends StackMapTableFrame {
         final int offsetDelta;

         SameFrame(int var1) {
            this.offsetDelta = var1;
         }

         int getFrameType() {
            return this.offsetDelta < 64 ? this.offsetDelta : 251;
         }

         void write(ClassWriter var1) {
            super.write(var1);
            if (this.getFrameType() == 251) {
               var1.databuf.appendChar(this.offsetDelta);
               if (var1.debugstackmap) {
                  System.out.print(" offset_delta=" + this.offsetDelta);
               }
            }

         }
      }
   }

   class AttributeWriter implements Attribute.Visitor {
      public void visitConstant(Attribute.Constant var1) {
         Object var2 = var1.value;
         switch (var1.type.getTag()) {
            case BYTE:
               ClassWriter.this.databuf.appendByte(66);
               break;
            case CHAR:
               ClassWriter.this.databuf.appendByte(67);
               break;
            case SHORT:
               ClassWriter.this.databuf.appendByte(83);
               break;
            case INT:
               ClassWriter.this.databuf.appendByte(73);
               break;
            case LONG:
               ClassWriter.this.databuf.appendByte(74);
               break;
            case FLOAT:
               ClassWriter.this.databuf.appendByte(70);
               break;
            case DOUBLE:
               ClassWriter.this.databuf.appendByte(68);
               break;
            case BOOLEAN:
               ClassWriter.this.databuf.appendByte(90);
               break;
            case CLASS:
               Assert.check(var2 instanceof String);
               ClassWriter.this.databuf.appendByte(115);
               var2 = ClassWriter.this.names.fromString(var2.toString());
               break;
            default:
               throw new AssertionError(var1.type);
         }

         ClassWriter.this.databuf.appendChar(ClassWriter.this.pool.put(var2));
      }

      public void visitEnum(Attribute.Enum var1) {
         ClassWriter.this.databuf.appendByte(101);
         ClassWriter.this.databuf.appendChar(ClassWriter.this.pool.put(ClassWriter.this.typeSig(var1.value.type)));
         ClassWriter.this.databuf.appendChar(ClassWriter.this.pool.put(var1.value.name));
      }

      public void visitClass(Attribute.Class var1) {
         ClassWriter.this.databuf.appendByte(99);
         ClassWriter.this.databuf.appendChar(ClassWriter.this.pool.put(ClassWriter.this.typeSig(var1.classType)));
      }

      public void visitCompound(Attribute.Compound var1) {
         ClassWriter.this.databuf.appendByte(64);
         ClassWriter.this.writeCompoundAttribute(var1);
      }

      public void visitError(Attribute.Error var1) {
         throw new AssertionError(var1);
      }

      public void visitArray(Attribute.Array var1) {
         ClassWriter.this.databuf.appendByte(91);
         ClassWriter.this.databuf.appendChar(var1.values.length);
         Attribute[] var2 = var1.values;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Attribute var5 = var2[var4];
            var5.accept(this);
         }

      }
   }

   public static class StringOverflow extends Exception {
      private static final long serialVersionUID = 0L;
      public final String value;

      public StringOverflow(String var1) {
         this.value = var1;
      }
   }

   public static class PoolOverflow extends Exception {
      private static final long serialVersionUID = 0L;
   }

   private class CWSignatureGenerator extends Types.SignatureGenerator {
      ByteBuffer sigbuf = new ByteBuffer();

      CWSignatureGenerator(Types var2) {
         super(var2);
      }

      public void assembleSig(Type var1) {
         var1 = var1.unannotatedType();
         switch (var1.getTag()) {
            case UNINITIALIZED_THIS:
            case UNINITIALIZED_OBJECT:
               this.assembleSig(ClassWriter.this.types.erasure(((UninitializedType)var1).qtype));
               break;
            default:
               super.assembleSig(var1);
         }

      }

      protected void append(char var1) {
         this.sigbuf.appendByte(var1);
      }

      protected void append(byte[] var1) {
         this.sigbuf.appendBytes(var1);
      }

      protected void append(Name var1) {
         this.sigbuf.appendName(var1);
      }

      protected void classReference(Symbol.ClassSymbol var1) {
         ClassWriter.this.enterInner(var1);
      }

      private void reset() {
         this.sigbuf.reset();
      }

      private Name toName() {
         return this.sigbuf.toName(ClassWriter.this.names);
      }

      private boolean isEmpty() {
         return this.sigbuf.length == 0;
      }
   }
}
