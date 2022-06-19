package com.sun.tools.javap;

import com.sun.tools.classfile.AccessFlags;
import com.sun.tools.classfile.Attribute;
import com.sun.tools.classfile.Attributes;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.ConstantValue_attribute;
import com.sun.tools.classfile.Descriptor;
import com.sun.tools.classfile.DescriptorException;
import com.sun.tools.classfile.Exceptions_attribute;
import com.sun.tools.classfile.Field;
import com.sun.tools.classfile.Method;
import com.sun.tools.classfile.Signature;
import com.sun.tools.classfile.Signature_attribute;
import com.sun.tools.classfile.SourceFile_attribute;
import com.sun.tools.classfile.Type;
import java.net.URI;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ClassWriter extends BasicWriter {
   private Options options;
   private AttributeWriter attrWriter;
   private CodeWriter codeWriter;
   private ConstantWriter constantWriter;
   private ClassFile classFile;
   private URI uri;
   private long lastModified;
   private String digestName;
   private byte[] digest;
   private int size;
   private ConstantPool constant_pool;
   private Method method;

   static ClassWriter instance(Context var0) {
      ClassWriter var1 = (ClassWriter)var0.get(ClassWriter.class);
      if (var1 == null) {
         var1 = new ClassWriter(var0);
      }

      return var1;
   }

   protected ClassWriter(Context var1) {
      super(var1);
      var1.put(ClassWriter.class, this);
      this.options = Options.instance(var1);
      this.attrWriter = AttributeWriter.instance(var1);
      this.codeWriter = CodeWriter.instance(var1);
      this.constantWriter = ConstantWriter.instance(var1);
   }

   void setDigest(String var1, byte[] var2) {
      this.digestName = var1;
      this.digest = var2;
   }

   void setFile(URI var1) {
      this.uri = var1;
   }

   void setFileSize(int var1) {
      this.size = var1;
   }

   void setLastModified(long var1) {
      this.lastModified = var1;
   }

   protected ClassFile getClassFile() {
      return this.classFile;
   }

   protected void setClassFile(ClassFile var1) {
      this.classFile = var1;
      this.constant_pool = this.classFile.constant_pool;
   }

   protected Method getMethod() {
      return this.method;
   }

   protected void setMethod(Method var1) {
      this.method = var1;
   }

   public void write(ClassFile var1) {
      this.setClassFile(var1);
      int var6;
      if (this.options.sysInfo || this.options.verbose) {
         if (this.uri != null) {
            if (this.uri.getScheme().equals("file")) {
               this.println("Classfile " + this.uri.getPath());
            } else {
               this.println("Classfile " + this.uri);
            }
         }

         this.indent(1);
         if (this.lastModified != -1L) {
            Date var2 = new Date(this.lastModified);
            DateFormat var3 = DateFormat.getDateInstance();
            if (this.size > 0) {
               this.println("Last modified " + var3.format(var2) + "; size " + this.size + " bytes");
            } else {
               this.println("Last modified " + var3.format(var2));
            }
         } else if (this.size > 0) {
            this.println("Size " + this.size + " bytes");
         }

         if (this.digestName != null && this.digest != null) {
            StringBuilder var9 = new StringBuilder();
            byte[] var11 = this.digest;
            int var4 = var11.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               var6 = var11[var5];
               var9.append(String.format("%02x", Byte.valueOf((byte)var6)));
            }

            this.println(this.digestName + " checksum " + var9);
         }
      }

      Attribute var10 = var1.getAttribute("SourceFile");
      if (var10 instanceof SourceFile_attribute) {
         this.println("Compiled from \"" + this.getSourceFile((SourceFile_attribute)var10) + "\"");
      }

      if (this.options.sysInfo || this.options.verbose) {
         this.indent(-1);
      }

      String var12 = this.getJavaName(this.classFile);
      AccessFlags var13 = var1.access_flags;
      this.writeModifiers(var13.getClassModifiers());
      if (this.classFile.isClass()) {
         this.print("class ");
      } else if (this.classFile.isInterface()) {
         this.print("interface ");
      }

      this.print(var12);
      Signature_attribute var14 = this.getSignature(var1.attributes);
      if (var14 == null) {
         if (this.classFile.isClass() && this.classFile.super_class != 0) {
            String var15 = this.getJavaSuperclassName(var1);
            if (!var15.equals("java.lang.Object")) {
               this.print(" extends ");
               this.print(var15);
            }
         }

         for(var6 = 0; var6 < this.classFile.interfaces.length; ++var6) {
            this.print(var6 == 0 ? (this.classFile.isClass() ? " implements " : " extends ") : ",");
            this.print(this.getJavaInterfaceName(this.classFile, var6));
         }
      } else {
         try {
            Type var16 = var14.getParsedSignature().getType(this.constant_pool);
            JavaTypePrinter var7 = new JavaTypePrinter(this.classFile.isInterface());
            if (var16 instanceof Type.ClassSigType) {
               this.print(var7.print(var16));
            } else if (this.options.verbose || !var16.isObject()) {
               this.print(" extends ");
               this.print(var7.print(var16));
            }
         } catch (ConstantPoolException var8) {
            this.print(this.report(var8));
         }
      }

      if (this.options.verbose) {
         this.println();
         this.indent(1);
         this.println("minor version: " + var1.minor_version);
         this.println("major version: " + var1.major_version);
         this.writeList("flags: ", var13.getClassFlags(), "\n");
         this.indent(-1);
         this.constantWriter.writeConstantPool();
      } else {
         this.print(" ");
      }

      this.println("{");
      this.indent(1);
      this.writeFields();
      this.writeMethods();
      this.indent(-1);
      this.println("}");
      if (this.options.verbose) {
         this.attrWriter.write(var1, (Attributes)var1.attributes, this.constant_pool);
      }

   }

   protected void writeFields() {
      Field[] var1 = this.classFile.fields;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Field var4 = var1[var3];
         this.writeField(var4);
      }

   }

   protected void writeField(Field var1) {
      if (this.options.checkAccess(var1.access_flags)) {
         AccessFlags var2 = var1.access_flags;
         this.writeModifiers(var2.getFieldModifiers());
         Signature_attribute var3 = this.getSignature(var1.attributes);
         if (var3 == null) {
            this.print(this.getJavaFieldType(var1.descriptor));
         } else {
            try {
               Type var4 = var3.getParsedSignature().getType(this.constant_pool);
               this.print(getJavaName(var4.toString()));
            } catch (ConstantPoolException var7) {
               this.print(this.getJavaFieldType(var1.descriptor));
            }
         }

         this.print(" ");
         this.print(this.getFieldName(var1));
         if (this.options.showConstants) {
            Attribute var8 = var1.attributes.get("ConstantValue");
            if (var8 instanceof ConstantValue_attribute) {
               this.print(" = ");
               ConstantValue_attribute var5 = (ConstantValue_attribute)var8;
               this.print(this.getConstantValue(var1.descriptor, var5.constantvalue_index));
            }
         }

         this.print(";");
         this.println();
         this.indent(1);
         boolean var9 = false;
         if (this.options.showDescriptors) {
            this.println("descriptor: " + this.getValue(var1.descriptor));
         }

         if (this.options.verbose) {
            this.writeList("flags: ", var2.getFieldFlags(), "\n");
         }

         if (this.options.showAllAttrs) {
            Iterator var10 = var1.attributes.iterator();

            while(var10.hasNext()) {
               Attribute var6 = (Attribute)var10.next();
               this.attrWriter.write(var1, (Attribute)var6, this.constant_pool);
            }

            var9 = true;
         }

         this.indent(-1);
         if (var9 || this.options.showDisassembled || this.options.showLineAndLocalVariableTables) {
            this.println();
         }

      }
   }

   protected void writeMethods() {
      Method[] var1 = this.classFile.methods;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Method var4 = var1[var3];
         this.writeMethod(var4);
      }

      this.setPendingNewline(false);
   }

   protected void writeMethod(Method var1) {
      if (this.options.checkAccess(var1.access_flags)) {
         this.method = var1;
         AccessFlags var2 = var1.access_flags;
         Signature_attribute var6 = this.getSignature(var1.attributes);
         Object var3;
         Type.MethodType var4;
         List var5;
         if (var6 == null) {
            var3 = var1.descriptor;
            var4 = null;
            var5 = null;
         } else {
            Signature var7 = var6.getParsedSignature();
            var3 = var7;

            try {
               var4 = (Type.MethodType)var7.getType(this.constant_pool);
               var5 = var4.throwsTypes;
               if (var5 != null && var5.isEmpty()) {
                  var5 = null;
               }
            } catch (ConstantPoolException var15) {
               var4 = null;
               var5 = null;
            }
         }

         this.writeModifiers(var2.getMethodModifiers());
         if (var4 != null) {
            this.print((new JavaTypePrinter(false)).printTypeArgs(var4.typeParamTypes));
         }

         if (this.getName(var1).equals("<init>")) {
            this.print(this.getJavaName(this.classFile));
            this.print(this.getJavaParameterTypes((Descriptor)var3, var2));
         } else if (this.getName(var1).equals("<clinit>")) {
            this.print("{}");
         } else {
            this.print(this.getJavaReturnType((Descriptor)var3));
            this.print(" ");
            this.print(this.getName(var1));
            this.print(this.getJavaParameterTypes((Descriptor)var3, var2));
         }

         Attribute var16 = var1.attributes.get("Exceptions");
         if (var16 != null) {
            if (var16 instanceof Exceptions_attribute) {
               Exceptions_attribute var8 = (Exceptions_attribute)var16;
               this.print(" throws ");
               if (var5 != null) {
                  this.writeList("", var5, "");
               } else {
                  for(int var9 = 0; var9 < var8.number_of_exceptions; ++var9) {
                     if (var9 > 0) {
                        this.print(", ");
                     }

                     this.print(this.getJavaException(var8, var9));
                  }
               }
            } else {
               this.report("Unexpected or invalid value for Exceptions attribute");
            }
         }

         this.println(";");
         this.indent(1);
         if (this.options.showDescriptors) {
            this.println("descriptor: " + this.getValue(var1.descriptor));
         }

         if (this.options.verbose) {
            this.writeList("flags: ", var2.getMethodFlags(), "\n");
         }

         Code_attribute var17 = null;
         Attribute var18 = var1.attributes.get("Code");
         if (var18 != null) {
            if (var18 instanceof Code_attribute) {
               var17 = (Code_attribute)var18;
            } else {
               this.report("Unexpected or invalid value for Code attribute");
            }
         }

         if (this.options.showAllAttrs) {
            Attribute[] var10 = var1.attributes.attrs;
            Attribute[] var11 = var10;
            int var12 = var10.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               Attribute var14 = var11[var13];
               this.attrWriter.write(var1, (Attribute)var14, this.constant_pool);
            }
         } else if (var17 != null) {
            if (this.options.showDisassembled) {
               this.println("Code:");
               this.codeWriter.writeInstrs(var17);
               this.codeWriter.writeExceptionTable(var17);
            }

            if (this.options.showLineAndLocalVariableTables) {
               this.attrWriter.write(var17, (Attribute)var17.attributes.get("LineNumberTable"), this.constant_pool);
               this.attrWriter.write(var17, (Attribute)var17.attributes.get("LocalVariableTable"), this.constant_pool);
            }
         }

         this.indent(-1);
         this.setPendingNewline(this.options.showDisassembled || this.options.showAllAttrs || this.options.showDescriptors || this.options.showLineAndLocalVariableTables || this.options.verbose);
      }
   }

   void writeModifiers(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         this.print(var3);
         this.print(" ");
      }

   }

   void writeList(String var1, Collection var2, String var3) {
      this.print(var1);
      String var4 = "";

      for(Iterator var5 = var2.iterator(); var5.hasNext(); var4 = ", ") {
         Object var6 = var5.next();
         this.print(var4);
         this.print(var6);
      }

      this.print(var3);
   }

   void writeListIfNotEmpty(String var1, List var2, String var3) {
      if (var2 != null && var2.size() > 0) {
         this.writeList(var1, var2, var3);
      }

   }

   Signature_attribute getSignature(Attributes var1) {
      return (Signature_attribute)var1.get("Signature");
   }

   String adjustVarargs(AccessFlags var1, String var2) {
      if (var1.is(128)) {
         int var3 = var2.lastIndexOf("[]");
         if (var3 > 0) {
            return var2.substring(0, var3) + "..." + var2.substring(var3 + 2);
         }
      }

      return var2;
   }

   String getJavaName(ClassFile var1) {
      try {
         return getJavaName(var1.getName());
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   String getJavaSuperclassName(ClassFile var1) {
      try {
         return getJavaName(var1.getSuperclassName());
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   String getJavaInterfaceName(ClassFile var1, int var2) {
      try {
         return getJavaName(var1.getInterfaceName(var2));
      } catch (ConstantPoolException var4) {
         return this.report(var4);
      }
   }

   String getJavaFieldType(Descriptor var1) {
      try {
         return getJavaName(var1.getFieldType(this.constant_pool));
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      } catch (DescriptorException var4) {
         return this.report(var4);
      }
   }

   String getJavaReturnType(Descriptor var1) {
      try {
         return getJavaName(var1.getReturnType(this.constant_pool));
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      } catch (DescriptorException var4) {
         return this.report(var4);
      }
   }

   String getJavaParameterTypes(Descriptor var1, AccessFlags var2) {
      try {
         return getJavaName(this.adjustVarargs(var2, var1.getParameterTypes(this.constant_pool)));
      } catch (ConstantPoolException var4) {
         return this.report(var4);
      } catch (DescriptorException var5) {
         return this.report(var5);
      }
   }

   String getJavaException(Exceptions_attribute var1, int var2) {
      try {
         return getJavaName(var1.getException(var2, this.constant_pool));
      } catch (ConstantPoolException var4) {
         return this.report(var4);
      }
   }

   String getValue(Descriptor var1) {
      try {
         return var1.getValue(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   String getFieldName(Field var1) {
      try {
         return var1.getName(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   String getName(Method var1) {
      try {
         return var1.getName(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   static String getJavaName(String var0) {
      return var0.replace('/', '.');
   }

   String getSourceFile(SourceFile_attribute var1) {
      try {
         return var1.getSourceFile(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   String getConstantValue(Descriptor var1, int var2) {
      try {
         ConstantPool.CPInfo var3 = this.constant_pool.get(var2);
         switch (var3.getTag()) {
            case 3:
               ConstantPool.CONSTANT_Integer_info var7 = (ConstantPool.CONSTANT_Integer_info)var3;
               String var5 = var1.getValue(this.constant_pool);
               if (var5.equals("C")) {
                  return this.getConstantCharValue((char)var7.value);
               } else {
                  if (var5.equals("Z")) {
                     return String.valueOf(var7.value == 1);
                  }

                  return String.valueOf(var7.value);
               }
            case 8:
               ConstantPool.CONSTANT_String_info var4 = (ConstantPool.CONSTANT_String_info)var3;
               return this.getConstantStringValue(var4.getString());
            default:
               return this.constantWriter.stringValue(var3);
         }
      } catch (ConstantPoolException var6) {
         return "#" + var2;
      }
   }

   private String getConstantCharValue(char var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append('\'');
      var2.append(this.esc(var1, '\''));
      var2.append('\'');
      return var2.toString();
   }

   private String getConstantStringValue(String var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("\"");

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         var2.append(this.esc(var1.charAt(var3), '"'));
      }

      var2.append("\"");
      return var2.toString();
   }

   private String esc(char var1, char var2) {
      if (' ' <= var1 && var1 <= '~' && var1 != var2) {
         return String.valueOf(var1);
      } else {
         switch (var1) {
            case '\b':
               return "\\b";
            case '\t':
               return "\\t";
            case '\n':
               return "\\n";
            case '\f':
               return "\\f";
            case '\r':
               return "\\r";
            case '"':
               return "\\\"";
            case '\'':
               return "\\'";
            case '\\':
               return "\\\\";
            default:
               return String.format("\\u%04x", Integer.valueOf(var1));
         }
      }
   }

   class JavaTypePrinter implements Type.Visitor {
      boolean isInterface;

      JavaTypePrinter(boolean var2) {
         this.isInterface = var2;
      }

      String print(Type var1) {
         return ((StringBuilder)var1.accept(this, new StringBuilder())).toString();
      }

      String printTypeArgs(List var1) {
         StringBuilder var2 = new StringBuilder();
         this.appendIfNotEmpty(var2, "<", var1, "> ");
         return var2.toString();
      }

      public StringBuilder visitSimpleType(Type.SimpleType var1, StringBuilder var2) {
         var2.append(ClassWriter.getJavaName(var1.name));
         return var2;
      }

      public StringBuilder visitArrayType(Type.ArrayType var1, StringBuilder var2) {
         this.append(var2, var1.elemType);
         var2.append("[]");
         return var2;
      }

      public StringBuilder visitMethodType(Type.MethodType var1, StringBuilder var2) {
         this.appendIfNotEmpty(var2, "<", var1.typeParamTypes, "> ");
         this.append(var2, var1.returnType);
         this.append(var2, " (", var1.paramTypes, ")");
         this.appendIfNotEmpty(var2, " throws ", var1.throwsTypes, "");
         return var2;
      }

      public StringBuilder visitClassSigType(Type.ClassSigType var1, StringBuilder var2) {
         this.appendIfNotEmpty(var2, "<", var1.typeParamTypes, ">");
         if (this.isInterface) {
            this.appendIfNotEmpty(var2, " extends ", var1.superinterfaceTypes, "");
         } else {
            if (var1.superclassType != null && (ClassWriter.this.options.verbose || !var1.superclassType.isObject())) {
               var2.append(" extends ");
               this.append(var2, var1.superclassType);
            }

            this.appendIfNotEmpty(var2, " implements ", var1.superinterfaceTypes, "");
         }

         return var2;
      }

      public StringBuilder visitClassType(Type.ClassType var1, StringBuilder var2) {
         if (var1.outerType != null) {
            this.append(var2, var1.outerType);
            var2.append(".");
         }

         var2.append(ClassWriter.getJavaName(var1.name));
         this.appendIfNotEmpty(var2, "<", var1.typeArgs, ">");
         return var2;
      }

      public StringBuilder visitTypeParamType(Type.TypeParamType var1, StringBuilder var2) {
         var2.append(var1.name);
         String var3 = " extends ";
         if (var1.classBound != null && (ClassWriter.this.options.verbose || !var1.classBound.isObject())) {
            var2.append(var3);
            this.append(var2, var1.classBound);
            var3 = " & ";
         }

         if (var1.interfaceBounds != null) {
            for(Iterator var4 = var1.interfaceBounds.iterator(); var4.hasNext(); var3 = " & ") {
               Type var5 = (Type)var4.next();
               var2.append(var3);
               this.append(var2, var5);
            }
         }

         return var2;
      }

      public StringBuilder visitWildcardType(Type.WildcardType var1, StringBuilder var2) {
         switch (var1.kind) {
            case UNBOUNDED:
               var2.append("?");
               break;
            case EXTENDS:
               var2.append("? extends ");
               this.append(var2, var1.boundType);
               break;
            case SUPER:
               var2.append("? super ");
               this.append(var2, var1.boundType);
               break;
            default:
               throw new AssertionError();
         }

         return var2;
      }

      private void append(StringBuilder var1, Type var2) {
         var2.accept(this, var1);
      }

      private void append(StringBuilder var1, String var2, List var3, String var4) {
         var1.append(var2);
         String var5 = "";

         for(Iterator var6 = var3.iterator(); var6.hasNext(); var5 = ", ") {
            Type var7 = (Type)var6.next();
            var1.append(var5);
            this.append(var1, var7);
         }

         var1.append(var4);
      }

      private void appendIfNotEmpty(StringBuilder var1, String var2, List var3, String var4) {
         if (!this.isEmpty(var3)) {
            this.append(var1, var2, var3, var4);
         }

      }

      private boolean isEmpty(List var1) {
         return var1 == null || var1.isEmpty();
      }
   }
}
