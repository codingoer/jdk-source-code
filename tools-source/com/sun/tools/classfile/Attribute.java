package com.sun.tools.classfile;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public abstract class Attribute {
   public static final String AnnotationDefault = "AnnotationDefault";
   public static final String BootstrapMethods = "BootstrapMethods";
   public static final String CharacterRangeTable = "CharacterRangeTable";
   public static final String Code = "Code";
   public static final String ConstantValue = "ConstantValue";
   public static final String CompilationID = "CompilationID";
   public static final String Deprecated = "Deprecated";
   public static final String EnclosingMethod = "EnclosingMethod";
   public static final String Exceptions = "Exceptions";
   public static final String InnerClasses = "InnerClasses";
   public static final String LineNumberTable = "LineNumberTable";
   public static final String LocalVariableTable = "LocalVariableTable";
   public static final String LocalVariableTypeTable = "LocalVariableTypeTable";
   public static final String MethodParameters = "MethodParameters";
   public static final String RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";
   public static final String RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations";
   public static final String RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations";
   public static final String RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";
   public static final String RuntimeVisibleTypeAnnotations = "RuntimeVisibleTypeAnnotations";
   public static final String RuntimeInvisibleTypeAnnotations = "RuntimeInvisibleTypeAnnotations";
   public static final String Signature = "Signature";
   public static final String SourceDebugExtension = "SourceDebugExtension";
   public static final String SourceFile = "SourceFile";
   public static final String SourceID = "SourceID";
   public static final String StackMap = "StackMap";
   public static final String StackMapTable = "StackMapTable";
   public static final String Synthetic = "Synthetic";
   public final int attribute_name_index;
   public final int attribute_length;

   public static Attribute read(ClassReader var0) throws IOException {
      return var0.readAttribute();
   }

   protected Attribute(int var1, int var2) {
      this.attribute_name_index = var1;
      this.attribute_length = var2;
   }

   public String getName(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.attribute_name_index);
   }

   public abstract Object accept(Visitor var1, Object var2);

   public int byteLength() {
      return 6 + this.attribute_length;
   }

   public interface Visitor {
      Object visitBootstrapMethods(BootstrapMethods_attribute var1, Object var2);

      Object visitDefault(DefaultAttribute var1, Object var2);

      Object visitAnnotationDefault(AnnotationDefault_attribute var1, Object var2);

      Object visitCharacterRangeTable(CharacterRangeTable_attribute var1, Object var2);

      Object visitCode(Code_attribute var1, Object var2);

      Object visitCompilationID(CompilationID_attribute var1, Object var2);

      Object visitConstantValue(ConstantValue_attribute var1, Object var2);

      Object visitDeprecated(Deprecated_attribute var1, Object var2);

      Object visitEnclosingMethod(EnclosingMethod_attribute var1, Object var2);

      Object visitExceptions(Exceptions_attribute var1, Object var2);

      Object visitInnerClasses(InnerClasses_attribute var1, Object var2);

      Object visitLineNumberTable(LineNumberTable_attribute var1, Object var2);

      Object visitLocalVariableTable(LocalVariableTable_attribute var1, Object var2);

      Object visitLocalVariableTypeTable(LocalVariableTypeTable_attribute var1, Object var2);

      Object visitMethodParameters(MethodParameters_attribute var1, Object var2);

      Object visitRuntimeVisibleAnnotations(RuntimeVisibleAnnotations_attribute var1, Object var2);

      Object visitRuntimeInvisibleAnnotations(RuntimeInvisibleAnnotations_attribute var1, Object var2);

      Object visitRuntimeVisibleParameterAnnotations(RuntimeVisibleParameterAnnotations_attribute var1, Object var2);

      Object visitRuntimeInvisibleParameterAnnotations(RuntimeInvisibleParameterAnnotations_attribute var1, Object var2);

      Object visitRuntimeVisibleTypeAnnotations(RuntimeVisibleTypeAnnotations_attribute var1, Object var2);

      Object visitRuntimeInvisibleTypeAnnotations(RuntimeInvisibleTypeAnnotations_attribute var1, Object var2);

      Object visitSignature(Signature_attribute var1, Object var2);

      Object visitSourceDebugExtension(SourceDebugExtension_attribute var1, Object var2);

      Object visitSourceFile(SourceFile_attribute var1, Object var2);

      Object visitSourceID(SourceID_attribute var1, Object var2);

      Object visitStackMap(StackMap_attribute var1, Object var2);

      Object visitStackMapTable(StackMapTable_attribute var1, Object var2);

      Object visitSynthetic(Synthetic_attribute var1, Object var2);
   }

   public static class Factory {
      private Map standardAttributes;

      public Attribute createAttribute(ClassReader var1, int var2, byte[] var3) throws IOException {
         if (this.standardAttributes == null) {
            this.init();
         }

         ConstantPool var4 = var1.getConstantPool();

         String var5;
         try {
            String var6 = var4.getUTF8Value(var2);
            Class var7 = (Class)this.standardAttributes.get(var6);
            if (var7 != null) {
               try {
                  Class[] var8 = new Class[]{ClassReader.class, Integer.TYPE, Integer.TYPE};
                  Constructor var9 = var7.getDeclaredConstructor(var8);
                  return (Attribute)var9.newInstance(var1, var2, var3.length);
               } catch (Throwable var10) {
                  var5 = var10.toString();
               }
            } else {
               var5 = "unknown attribute";
            }
         } catch (ConstantPoolException var11) {
            var5 = var11.toString();
         }

         return new DefaultAttribute(var1, var2, var3, var5);
      }

      protected void init() {
         this.standardAttributes = new HashMap();
         this.standardAttributes.put("AnnotationDefault", AnnotationDefault_attribute.class);
         this.standardAttributes.put("BootstrapMethods", BootstrapMethods_attribute.class);
         this.standardAttributes.put("CharacterRangeTable", CharacterRangeTable_attribute.class);
         this.standardAttributes.put("Code", Code_attribute.class);
         this.standardAttributes.put("CompilationID", CompilationID_attribute.class);
         this.standardAttributes.put("ConstantValue", ConstantValue_attribute.class);
         this.standardAttributes.put("Deprecated", Deprecated_attribute.class);
         this.standardAttributes.put("EnclosingMethod", EnclosingMethod_attribute.class);
         this.standardAttributes.put("Exceptions", Exceptions_attribute.class);
         this.standardAttributes.put("InnerClasses", InnerClasses_attribute.class);
         this.standardAttributes.put("LineNumberTable", LineNumberTable_attribute.class);
         this.standardAttributes.put("LocalVariableTable", LocalVariableTable_attribute.class);
         this.standardAttributes.put("LocalVariableTypeTable", LocalVariableTypeTable_attribute.class);
         this.standardAttributes.put("MethodParameters", MethodParameters_attribute.class);
         this.standardAttributes.put("RuntimeInvisibleAnnotations", RuntimeInvisibleAnnotations_attribute.class);
         this.standardAttributes.put("RuntimeInvisibleParameterAnnotations", RuntimeInvisibleParameterAnnotations_attribute.class);
         this.standardAttributes.put("RuntimeVisibleAnnotations", RuntimeVisibleAnnotations_attribute.class);
         this.standardAttributes.put("RuntimeVisibleParameterAnnotations", RuntimeVisibleParameterAnnotations_attribute.class);
         this.standardAttributes.put("RuntimeVisibleTypeAnnotations", RuntimeVisibleTypeAnnotations_attribute.class);
         this.standardAttributes.put("RuntimeInvisibleTypeAnnotations", RuntimeInvisibleTypeAnnotations_attribute.class);
         this.standardAttributes.put("Signature", Signature_attribute.class);
         this.standardAttributes.put("SourceDebugExtension", SourceDebugExtension_attribute.class);
         this.standardAttributes.put("SourceFile", SourceFile_attribute.class);
         this.standardAttributes.put("SourceID", SourceID_attribute.class);
         this.standardAttributes.put("StackMap", StackMap_attribute.class);
         this.standardAttributes.put("StackMapTable", StackMapTable_attribute.class);
         this.standardAttributes.put("Synthetic", Synthetic_attribute.class);
      }
   }
}
