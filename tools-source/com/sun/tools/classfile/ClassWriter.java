package com.sun.tools.classfile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class ClassWriter {
   protected ClassFile classFile;
   protected ClassOutputStream out = new ClassOutputStream();
   protected AttributeWriter attributeWriter = new AttributeWriter();
   protected ConstantPoolWriter constantPoolWriter = new ConstantPoolWriter();

   public void write(ClassFile var1, File var2) throws IOException {
      FileOutputStream var3 = new FileOutputStream(var2);

      try {
         this.write(var1, (OutputStream)var3);
      } finally {
         var3.close();
      }

   }

   public void write(ClassFile var1, OutputStream var2) throws IOException {
      this.classFile = var1;
      this.out.reset();
      this.write();
      this.out.writeTo(var2);
   }

   protected void write() throws IOException {
      this.writeHeader();
      this.writeConstantPool();
      this.writeAccessFlags(this.classFile.access_flags);
      this.writeClassInfo();
      this.writeFields();
      this.writeMethods();
      this.writeAttributes(this.classFile.attributes);
   }

   protected void writeHeader() {
      this.out.writeInt(this.classFile.magic);
      this.out.writeShort(this.classFile.minor_version);
      this.out.writeShort(this.classFile.major_version);
   }

   protected void writeAccessFlags(AccessFlags var1) {
      this.out.writeShort(var1.flags);
   }

   protected void writeAttributes(Attributes var1) {
      int var2 = var1.size();
      this.out.writeShort(var2);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Attribute var4 = (Attribute)var3.next();
         this.attributeWriter.write(var4, this.out);
      }

   }

   protected void writeClassInfo() {
      this.out.writeShort(this.classFile.this_class);
      this.out.writeShort(this.classFile.super_class);
      int[] var1 = this.classFile.interfaces;
      this.out.writeShort(var1.length);
      int[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         this.out.writeShort(var5);
      }

   }

   protected void writeDescriptor(Descriptor var1) {
      this.out.writeShort(var1.index);
   }

   protected void writeConstantPool() {
      ConstantPool var1 = this.classFile.constant_pool;
      int var2 = var1.size();
      this.out.writeShort(var2);
      Iterator var3 = var1.entries().iterator();

      while(var3.hasNext()) {
         ConstantPool.CPInfo var4 = (ConstantPool.CPInfo)var3.next();
         this.constantPoolWriter.write(var4, this.out);
      }

   }

   protected void writeFields() throws IOException {
      Field[] var1 = this.classFile.fields;
      this.out.writeShort(var1.length);
      Field[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Field var5 = var2[var4];
         this.writeField(var5);
      }

   }

   protected void writeField(Field var1) throws IOException {
      this.writeAccessFlags(var1.access_flags);
      this.out.writeShort(var1.name_index);
      this.writeDescriptor(var1.descriptor);
      this.writeAttributes(var1.attributes);
   }

   protected void writeMethods() throws IOException {
      Method[] var1 = this.classFile.methods;
      this.out.writeShort(var1.length);
      Method[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method var5 = var2[var4];
         this.writeMethod(var5);
      }

   }

   protected void writeMethod(Method var1) throws IOException {
      this.writeAccessFlags(var1.access_flags);
      this.out.writeShort(var1.name_index);
      this.writeDescriptor(var1.descriptor);
      this.writeAttributes(var1.attributes);
   }

   protected static class AnnotationWriter implements Annotation.element_value.Visitor {
      public void write(Annotation[] var1, ClassOutputStream var2) {
         var2.writeShort(var1.length);
         Annotation[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Annotation var6 = var3[var5];
            this.write(var6, var2);
         }

      }

      public void write(TypeAnnotation[] var1, ClassOutputStream var2) {
         var2.writeShort(var1.length);
         TypeAnnotation[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            TypeAnnotation var6 = var3[var5];
            this.write(var6, var2);
         }

      }

      public void write(Annotation var1, ClassOutputStream var2) {
         var2.writeShort(var1.type_index);
         var2.writeShort(var1.element_value_pairs.length);
         Annotation.element_value_pair[] var3 = var1.element_value_pairs;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Annotation.element_value_pair var6 = var3[var5];
            this.write(var6, var2);
         }

      }

      public void write(TypeAnnotation var1, ClassOutputStream var2) {
         this.write(var1.position, var2);
         this.write(var1.annotation, var2);
      }

      public void write(Annotation.element_value_pair var1, ClassOutputStream var2) {
         var2.writeShort(var1.element_name_index);
         this.write(var1.value, var2);
      }

      public void write(Annotation.element_value var1, ClassOutputStream var2) {
         var2.writeByte(var1.tag);
         var1.accept(this, var2);
      }

      public Void visitPrimitive(Annotation.Primitive_element_value var1, ClassOutputStream var2) {
         var2.writeShort(var1.const_value_index);
         return null;
      }

      public Void visitEnum(Annotation.Enum_element_value var1, ClassOutputStream var2) {
         var2.writeShort(var1.type_name_index);
         var2.writeShort(var1.const_name_index);
         return null;
      }

      public Void visitClass(Annotation.Class_element_value var1, ClassOutputStream var2) {
         var2.writeShort(var1.class_info_index);
         return null;
      }

      public Void visitAnnotation(Annotation.Annotation_element_value var1, ClassOutputStream var2) {
         this.write(var1.annotation_value, var2);
         return null;
      }

      public Void visitArray(Annotation.Array_element_value var1, ClassOutputStream var2) {
         var2.writeShort(var1.num_values);
         Annotation.element_value[] var3 = var1.values;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Annotation.element_value var6 = var3[var5];
            this.write(var6, var2);
         }

         return null;
      }

      private void write(TypeAnnotation.Position var1, ClassOutputStream var2) {
         int var4;
         var2.writeByte(var1.type.targetTypeValue());
         label36:
         switch (var1.type) {
            case INSTANCEOF:
            case NEW:
            case CONSTRUCTOR_REFERENCE:
            case METHOD_REFERENCE:
               var2.writeShort(var1.offset);
               break;
            case LOCAL_VARIABLE:
            case RESOURCE_VARIABLE:
               int var3 = var1.lvarOffset.length;
               var2.writeShort(var3);
               var4 = 0;

               while(true) {
                  if (var4 >= var3) {
                     break label36;
                  }

                  var2.writeShort(1);
                  var2.writeShort(var1.lvarOffset[var4]);
                  var2.writeShort(var1.lvarLength[var4]);
                  var2.writeShort(var1.lvarIndex[var4]);
                  ++var4;
               }
            case EXCEPTION_PARAMETER:
               var2.writeShort(var1.exception_index);
            case METHOD_RECEIVER:
            case METHOD_RETURN:
            case FIELD:
               break;
            case CLASS_TYPE_PARAMETER:
            case METHOD_TYPE_PARAMETER:
               var2.writeByte(var1.parameter_index);
               break;
            case CLASS_TYPE_PARAMETER_BOUND:
            case METHOD_TYPE_PARAMETER_BOUND:
               var2.writeByte(var1.parameter_index);
               var2.writeByte(var1.bound_index);
               break;
            case CLASS_EXTENDS:
               var2.writeShort(var1.type_index);
               break;
            case THROWS:
               var2.writeShort(var1.type_index);
               break;
            case METHOD_FORMAL_PARAMETER:
               var2.writeByte(var1.parameter_index);
               break;
            case CAST:
            case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
            case METHOD_INVOCATION_TYPE_ARGUMENT:
            case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
            case METHOD_REFERENCE_TYPE_ARGUMENT:
               var2.writeShort(var1.offset);
               var2.writeByte(var1.type_index);
               break;
            case UNKNOWN:
               throw new AssertionError("ClassWriter: UNKNOWN target type should never occur!");
            default:
               throw new AssertionError("ClassWriter: Unknown target type for position: " + var1);
         }

         var2.writeByte((byte)var1.location.size());
         Iterator var5 = TypeAnnotation.Position.getBinaryFromTypePath(var1.location).iterator();

         while(var5.hasNext()) {
            var4 = (Integer)var5.next();
            var2.writeByte((byte)var4);
         }

      }
   }

   protected static class StackMapTableWriter implements StackMapTable_attribute.stack_map_frame.Visitor {
      public void write(StackMapTable_attribute.stack_map_frame var1, ClassOutputStream var2) {
         var2.write(var1.frame_type);
         var1.accept(this, var2);
      }

      public Void visit_same_frame(StackMapTable_attribute.same_frame var1, ClassOutputStream var2) {
         return null;
      }

      public Void visit_same_locals_1_stack_item_frame(StackMapTable_attribute.same_locals_1_stack_item_frame var1, ClassOutputStream var2) {
         this.writeVerificationTypeInfo(var1.stack[0], var2);
         return null;
      }

      public Void visit_same_locals_1_stack_item_frame_extended(StackMapTable_attribute.same_locals_1_stack_item_frame_extended var1, ClassOutputStream var2) {
         var2.writeShort(var1.offset_delta);
         this.writeVerificationTypeInfo(var1.stack[0], var2);
         return null;
      }

      public Void visit_chop_frame(StackMapTable_attribute.chop_frame var1, ClassOutputStream var2) {
         var2.writeShort(var1.offset_delta);
         return null;
      }

      public Void visit_same_frame_extended(StackMapTable_attribute.same_frame_extended var1, ClassOutputStream var2) {
         var2.writeShort(var1.offset_delta);
         return null;
      }

      public Void visit_append_frame(StackMapTable_attribute.append_frame var1, ClassOutputStream var2) {
         var2.writeShort(var1.offset_delta);
         StackMapTable_attribute.verification_type_info[] var3 = var1.locals;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            StackMapTable_attribute.verification_type_info var6 = var3[var5];
            this.writeVerificationTypeInfo(var6, var2);
         }

         return null;
      }

      public Void visit_full_frame(StackMapTable_attribute.full_frame var1, ClassOutputStream var2) {
         var2.writeShort(var1.offset_delta);
         var2.writeShort(var1.locals.length);
         StackMapTable_attribute.verification_type_info[] var3 = var1.locals;
         int var4 = var3.length;

         int var5;
         StackMapTable_attribute.verification_type_info var6;
         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            this.writeVerificationTypeInfo(var6, var2);
         }

         var2.writeShort(var1.stack.length);
         var3 = var1.stack;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var3[var5];
            this.writeVerificationTypeInfo(var6, var2);
         }

         return null;
      }

      protected void writeVerificationTypeInfo(StackMapTable_attribute.verification_type_info var1, ClassOutputStream var2) {
         var2.write(var1.tag);
         switch (var1.tag) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               break;
            case 7:
               StackMapTable_attribute.Object_variable_info var3 = (StackMapTable_attribute.Object_variable_info)var1;
               var2.writeShort(var3.cpool_index);
               break;
            case 8:
               StackMapTable_attribute.Uninitialized_variable_info var4 = (StackMapTable_attribute.Uninitialized_variable_info)var1;
               var2.writeShort(var4.offset);
               break;
            default:
               throw new Error();
         }

      }
   }

   protected static class AttributeWriter implements Attribute.Visitor {
      protected ClassOutputStream sharedOut = new ClassOutputStream();
      protected AnnotationWriter annotationWriter = new AnnotationWriter();
      protected StackMapTableWriter stackMapWriter;

      public void write(Attributes var1, ClassOutputStream var2) {
         int var3 = var1.size();
         var2.writeShort(var3);
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Attribute var5 = (Attribute)var4.next();
            this.write(var5, var2);
         }

      }

      public void write(Attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.attribute_name_index);
         this.sharedOut.reset();
         var1.accept(this, this.sharedOut);
         var2.writeInt(this.sharedOut.size());
         this.sharedOut.writeTo(var2);
      }

      public Void visitDefault(DefaultAttribute var1, ClassOutputStream var2) {
         var2.write(var1.info, 0, var1.info.length);
         return null;
      }

      public Void visitAnnotationDefault(AnnotationDefault_attribute var1, ClassOutputStream var2) {
         this.annotationWriter.write(var1.default_value, var2);
         return null;
      }

      public Void visitBootstrapMethods(BootstrapMethods_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.bootstrap_method_specifiers.length);
         BootstrapMethods_attribute.BootstrapMethodSpecifier[] var3 = var1.bootstrap_method_specifiers;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            BootstrapMethods_attribute.BootstrapMethodSpecifier var6 = var3[var5];
            var2.writeShort(var6.bootstrap_method_ref);
            int var7 = var6.bootstrap_arguments.length;
            var2.writeShort(var7);
            int[] var8 = var6.bootstrap_arguments;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               int var11 = var8[var10];
               var2.writeShort(var11);
            }
         }

         return null;
      }

      public Void visitCharacterRangeTable(CharacterRangeTable_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.character_range_table.length);
         CharacterRangeTable_attribute.Entry[] var3 = var1.character_range_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CharacterRangeTable_attribute.Entry var6 = var3[var5];
            this.writeCharacterRangeTableEntry(var6, var2);
         }

         return null;
      }

      protected void writeCharacterRangeTableEntry(CharacterRangeTable_attribute.Entry var1, ClassOutputStream var2) {
         var2.writeShort(var1.start_pc);
         var2.writeShort(var1.end_pc);
         var2.writeInt(var1.character_range_start);
         var2.writeInt(var1.character_range_end);
         var2.writeShort(var1.flags);
      }

      public Void visitCode(Code_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.max_stack);
         var2.writeShort(var1.max_locals);
         var2.writeInt(var1.code.length);
         var2.write(var1.code, 0, var1.code.length);
         var2.writeShort(var1.exception_table.length);
         Code_attribute.Exception_data[] var3 = var1.exception_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Code_attribute.Exception_data var6 = var3[var5];
            this.writeExceptionTableEntry(var6, var2);
         }

         (new AttributeWriter()).write(var1.attributes, var2);
         return null;
      }

      protected void writeExceptionTableEntry(Code_attribute.Exception_data var1, ClassOutputStream var2) {
         var2.writeShort(var1.start_pc);
         var2.writeShort(var1.end_pc);
         var2.writeShort(var1.handler_pc);
         var2.writeShort(var1.catch_type);
      }

      public Void visitCompilationID(CompilationID_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.compilationID_index);
         return null;
      }

      public Void visitConstantValue(ConstantValue_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.constantvalue_index);
         return null;
      }

      public Void visitDeprecated(Deprecated_attribute var1, ClassOutputStream var2) {
         return null;
      }

      public Void visitEnclosingMethod(EnclosingMethod_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.class_index);
         var2.writeShort(var1.method_index);
         return null;
      }

      public Void visitExceptions(Exceptions_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.exception_index_table.length);
         int[] var3 = var1.exception_index_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var3[var5];
            var2.writeShort(var6);
         }

         return null;
      }

      public Void visitInnerClasses(InnerClasses_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.classes.length);
         InnerClasses_attribute.Info[] var3 = var1.classes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            InnerClasses_attribute.Info var6 = var3[var5];
            this.writeInnerClassesInfo(var6, var2);
         }

         return null;
      }

      protected void writeInnerClassesInfo(InnerClasses_attribute.Info var1, ClassOutputStream var2) {
         var2.writeShort(var1.inner_class_info_index);
         var2.writeShort(var1.outer_class_info_index);
         var2.writeShort(var1.inner_name_index);
         this.writeAccessFlags(var1.inner_class_access_flags, var2);
      }

      public Void visitLineNumberTable(LineNumberTable_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.line_number_table.length);
         LineNumberTable_attribute.Entry[] var3 = var1.line_number_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LineNumberTable_attribute.Entry var6 = var3[var5];
            this.writeLineNumberTableEntry(var6, var2);
         }

         return null;
      }

      protected void writeLineNumberTableEntry(LineNumberTable_attribute.Entry var1, ClassOutputStream var2) {
         var2.writeShort(var1.start_pc);
         var2.writeShort(var1.line_number);
      }

      public Void visitLocalVariableTable(LocalVariableTable_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.local_variable_table.length);
         LocalVariableTable_attribute.Entry[] var3 = var1.local_variable_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LocalVariableTable_attribute.Entry var6 = var3[var5];
            this.writeLocalVariableTableEntry(var6, var2);
         }

         return null;
      }

      protected void writeLocalVariableTableEntry(LocalVariableTable_attribute.Entry var1, ClassOutputStream var2) {
         var2.writeShort(var1.start_pc);
         var2.writeShort(var1.length);
         var2.writeShort(var1.name_index);
         var2.writeShort(var1.descriptor_index);
         var2.writeShort(var1.index);
      }

      public Void visitLocalVariableTypeTable(LocalVariableTypeTable_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.local_variable_table.length);
         LocalVariableTypeTable_attribute.Entry[] var3 = var1.local_variable_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LocalVariableTypeTable_attribute.Entry var6 = var3[var5];
            this.writeLocalVariableTypeTableEntry(var6, var2);
         }

         return null;
      }

      protected void writeLocalVariableTypeTableEntry(LocalVariableTypeTable_attribute.Entry var1, ClassOutputStream var2) {
         var2.writeShort(var1.start_pc);
         var2.writeShort(var1.length);
         var2.writeShort(var1.name_index);
         var2.writeShort(var1.signature_index);
         var2.writeShort(var1.index);
      }

      public Void visitMethodParameters(MethodParameters_attribute var1, ClassOutputStream var2) {
         var2.writeByte(var1.method_parameter_table.length);
         MethodParameters_attribute.Entry[] var3 = var1.method_parameter_table;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            MethodParameters_attribute.Entry var6 = var3[var5];
            var2.writeShort(var6.name_index);
            var2.writeShort(var6.flags);
         }

         return null;
      }

      public Void visitRuntimeVisibleAnnotations(RuntimeVisibleAnnotations_attribute var1, ClassOutputStream var2) {
         this.annotationWriter.write(var1.annotations, var2);
         return null;
      }

      public Void visitRuntimeInvisibleAnnotations(RuntimeInvisibleAnnotations_attribute var1, ClassOutputStream var2) {
         this.annotationWriter.write(var1.annotations, var2);
         return null;
      }

      public Void visitRuntimeVisibleTypeAnnotations(RuntimeVisibleTypeAnnotations_attribute var1, ClassOutputStream var2) {
         this.annotationWriter.write(var1.annotations, var2);
         return null;
      }

      public Void visitRuntimeInvisibleTypeAnnotations(RuntimeInvisibleTypeAnnotations_attribute var1, ClassOutputStream var2) {
         this.annotationWriter.write(var1.annotations, var2);
         return null;
      }

      public Void visitRuntimeVisibleParameterAnnotations(RuntimeVisibleParameterAnnotations_attribute var1, ClassOutputStream var2) {
         var2.writeByte(var1.parameter_annotations.length);
         Annotation[][] var3 = var1.parameter_annotations;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Annotation[] var6 = var3[var5];
            this.annotationWriter.write(var6, var2);
         }

         return null;
      }

      public Void visitRuntimeInvisibleParameterAnnotations(RuntimeInvisibleParameterAnnotations_attribute var1, ClassOutputStream var2) {
         var2.writeByte(var1.parameter_annotations.length);
         Annotation[][] var3 = var1.parameter_annotations;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Annotation[] var6 = var3[var5];
            this.annotationWriter.write(var6, var2);
         }

         return null;
      }

      public Void visitSignature(Signature_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.signature_index);
         return null;
      }

      public Void visitSourceDebugExtension(SourceDebugExtension_attribute var1, ClassOutputStream var2) {
         var2.write(var1.debug_extension, 0, var1.debug_extension.length);
         return null;
      }

      public Void visitSourceFile(SourceFile_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.sourcefile_index);
         return null;
      }

      public Void visitSourceID(SourceID_attribute var1, ClassOutputStream var2) {
         var2.writeShort(var1.sourceID_index);
         return null;
      }

      public Void visitStackMap(StackMap_attribute var1, ClassOutputStream var2) {
         if (this.stackMapWriter == null) {
            this.stackMapWriter = new StackMapTableWriter();
         }

         var2.writeShort(var1.entries.length);
         StackMap_attribute.stack_map_frame[] var3 = var1.entries;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            StackMap_attribute.stack_map_frame var6 = var3[var5];
            this.stackMapWriter.write(var6, var2);
         }

         return null;
      }

      public Void visitStackMapTable(StackMapTable_attribute var1, ClassOutputStream var2) {
         if (this.stackMapWriter == null) {
            this.stackMapWriter = new StackMapTableWriter();
         }

         var2.writeShort(var1.entries.length);
         StackMapTable_attribute.stack_map_frame[] var3 = var1.entries;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            StackMapTable_attribute.stack_map_frame var6 = var3[var5];
            this.stackMapWriter.write(var6, var2);
         }

         return null;
      }

      public Void visitSynthetic(Synthetic_attribute var1, ClassOutputStream var2) {
         return null;
      }

      protected void writeAccessFlags(AccessFlags var1, ClassOutputStream var2) {
         this.sharedOut.writeShort(var1.flags);
      }
   }

   protected static class ConstantPoolWriter implements ConstantPool.Visitor {
      protected int write(ConstantPool.CPInfo var1, ClassOutputStream var2) {
         var2.writeByte(var1.getTag());
         return (Integer)var1.accept(this, var2);
      }

      public Integer visitClass(ConstantPool.CONSTANT_Class_info var1, ClassOutputStream var2) {
         var2.writeShort(var1.name_index);
         return 1;
      }

      public Integer visitDouble(ConstantPool.CONSTANT_Double_info var1, ClassOutputStream var2) {
         var2.writeDouble(var1.value);
         return 2;
      }

      public Integer visitFieldref(ConstantPool.CONSTANT_Fieldref_info var1, ClassOutputStream var2) {
         this.writeRef(var1, var2);
         return 1;
      }

      public Integer visitFloat(ConstantPool.CONSTANT_Float_info var1, ClassOutputStream var2) {
         var2.writeFloat(var1.value);
         return 1;
      }

      public Integer visitInteger(ConstantPool.CONSTANT_Integer_info var1, ClassOutputStream var2) {
         var2.writeInt(var1.value);
         return 1;
      }

      public Integer visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info var1, ClassOutputStream var2) {
         this.writeRef(var1, var2);
         return 1;
      }

      public Integer visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info var1, ClassOutputStream var2) {
         var2.writeShort(var1.bootstrap_method_attr_index);
         var2.writeShort(var1.name_and_type_index);
         return 1;
      }

      public Integer visitLong(ConstantPool.CONSTANT_Long_info var1, ClassOutputStream var2) {
         var2.writeLong(var1.value);
         return 2;
      }

      public Integer visitNameAndType(ConstantPool.CONSTANT_NameAndType_info var1, ClassOutputStream var2) {
         var2.writeShort(var1.name_index);
         var2.writeShort(var1.type_index);
         return 1;
      }

      public Integer visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info var1, ClassOutputStream var2) {
         var2.writeByte(var1.reference_kind.tag);
         var2.writeShort(var1.reference_index);
         return 1;
      }

      public Integer visitMethodType(ConstantPool.CONSTANT_MethodType_info var1, ClassOutputStream var2) {
         var2.writeShort(var1.descriptor_index);
         return 1;
      }

      public Integer visitMethodref(ConstantPool.CONSTANT_Methodref_info var1, ClassOutputStream var2) {
         return this.writeRef(var1, var2);
      }

      public Integer visitString(ConstantPool.CONSTANT_String_info var1, ClassOutputStream var2) {
         var2.writeShort(var1.string_index);
         return 1;
      }

      public Integer visitUtf8(ConstantPool.CONSTANT_Utf8_info var1, ClassOutputStream var2) {
         var2.writeUTF(var1.value);
         return 1;
      }

      protected Integer writeRef(ConstantPool.CPRefInfo var1, ClassOutputStream var2) {
         var2.writeShort(var1.class_index);
         var2.writeShort(var1.name_and_type_index);
         return 1;
      }
   }

   protected static class ClassOutputStream extends ByteArrayOutputStream {
      private DataOutputStream d = new DataOutputStream(this);

      public ClassOutputStream() {
      }

      public void writeByte(int var1) {
         try {
            this.d.writeByte(var1);
         } catch (IOException var3) {
         }

      }

      public void writeShort(int var1) {
         try {
            this.d.writeShort(var1);
         } catch (IOException var3) {
         }

      }

      public void writeInt(int var1) {
         try {
            this.d.writeInt(var1);
         } catch (IOException var3) {
         }

      }

      public void writeLong(long var1) {
         try {
            this.d.writeLong(var1);
         } catch (IOException var4) {
         }

      }

      public void writeFloat(float var1) {
         try {
            this.d.writeFloat(var1);
         } catch (IOException var3) {
         }

      }

      public void writeDouble(double var1) {
         try {
            this.d.writeDouble(var1);
         } catch (IOException var4) {
         }

      }

      public void writeUTF(String var1) {
         try {
            this.d.writeUTF(var1);
         } catch (IOException var3) {
         }

      }

      public void writeTo(ClassOutputStream var1) {
         try {
            super.writeTo(var1);
         } catch (IOException var3) {
         }

      }
   }
}
