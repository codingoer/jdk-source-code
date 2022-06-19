package com.sun.tools.javap;

import com.sun.tools.classfile.AccessFlags;
import com.sun.tools.classfile.AnnotationDefault_attribute;
import com.sun.tools.classfile.Attribute;
import com.sun.tools.classfile.Attributes;
import com.sun.tools.classfile.BootstrapMethods_attribute;
import com.sun.tools.classfile.CharacterRangeTable_attribute;
import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.CompilationID_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.ConstantValue_attribute;
import com.sun.tools.classfile.DefaultAttribute;
import com.sun.tools.classfile.Deprecated_attribute;
import com.sun.tools.classfile.EnclosingMethod_attribute;
import com.sun.tools.classfile.Exceptions_attribute;
import com.sun.tools.classfile.InnerClasses_attribute;
import com.sun.tools.classfile.LineNumberTable_attribute;
import com.sun.tools.classfile.LocalVariableTable_attribute;
import com.sun.tools.classfile.LocalVariableTypeTable_attribute;
import com.sun.tools.classfile.MethodParameters_attribute;
import com.sun.tools.classfile.RuntimeInvisibleAnnotations_attribute;
import com.sun.tools.classfile.RuntimeInvisibleParameterAnnotations_attribute;
import com.sun.tools.classfile.RuntimeInvisibleTypeAnnotations_attribute;
import com.sun.tools.classfile.RuntimeVisibleAnnotations_attribute;
import com.sun.tools.classfile.RuntimeVisibleParameterAnnotations_attribute;
import com.sun.tools.classfile.RuntimeVisibleTypeAnnotations_attribute;
import com.sun.tools.classfile.Signature_attribute;
import com.sun.tools.classfile.SourceDebugExtension_attribute;
import com.sun.tools.classfile.SourceFile_attribute;
import com.sun.tools.classfile.SourceID_attribute;
import com.sun.tools.classfile.StackMapTable_attribute;
import com.sun.tools.classfile.StackMap_attribute;
import com.sun.tools.classfile.Synthetic_attribute;
import com.sun.tools.javac.util.StringUtils;
import java.util.Iterator;

public class AttributeWriter extends BasicWriter implements Attribute.Visitor {
   private static final String format = "%-31s%s";
   private AnnotationWriter annotationWriter;
   private CodeWriter codeWriter;
   private ConstantWriter constantWriter;
   private Options options;
   private ConstantPool constant_pool;
   private Object owner;

   public static AttributeWriter instance(Context var0) {
      AttributeWriter var1 = (AttributeWriter)var0.get(AttributeWriter.class);
      if (var1 == null) {
         var1 = new AttributeWriter(var0);
      }

      return var1;
   }

   protected AttributeWriter(Context var1) {
      super(var1);
      var1.put(AttributeWriter.class, this);
      this.annotationWriter = AnnotationWriter.instance(var1);
      this.codeWriter = CodeWriter.instance(var1);
      this.constantWriter = ConstantWriter.instance(var1);
      this.options = Options.instance(var1);
   }

   public void write(Object var1, Attribute var2, ConstantPool var3) {
      if (var2 != null) {
         var1.getClass();
         var3.getClass();
         this.constant_pool = var3;
         this.owner = var1;
         var2.accept(this, (Object)null);
      }

   }

   public void write(Object var1, Attributes var2, ConstantPool var3) {
      if (var2 != null) {
         var1.getClass();
         var3.getClass();
         this.constant_pool = var3;
         this.owner = var1;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Attribute var5 = (Attribute)var4.next();
            var5.accept(this, (Object)null);
         }
      }

   }

   public Void visitDefault(DefaultAttribute var1, Void var2) {
      if (var1.reason != null) {
         this.report(var1.reason);
      }

      byte[] var3 = var1.info;
      int var4 = 0;
      int var5 = 0;
      this.print("  ");

      try {
         this.print(var1.getName(this.constant_pool));
      } catch (ConstantPoolException var7) {
         this.report(var7);
         this.print("attribute name = #" + var1.attribute_name_index);
      }

      this.print(": ");
      this.println("length = 0x" + toHex(var1.info.length));
      this.print("   ");

      for(; var4 < var3.length; ++var4) {
         this.print(this.toHex((byte)var3[var4], 2));
         ++var5;
         if (var5 == 16) {
            this.println();
            this.print("   ");
            var5 = 0;
         } else {
            this.print(" ");
         }
      }

      this.println();
      return null;
   }

   public Void visitAnnotationDefault(AnnotationDefault_attribute var1, Void var2) {
      this.println("AnnotationDefault:");
      this.indent(1);
      this.print("default_value: ");
      this.annotationWriter.write(var1.default_value);
      this.indent(-1);
      return null;
   }

   public Void visitBootstrapMethods(BootstrapMethods_attribute var1, Void var2) {
      this.println("BootstrapMethods:");

      for(int var3 = 0; var3 < var1.bootstrap_method_specifiers.length; ++var3) {
         BootstrapMethods_attribute.BootstrapMethodSpecifier var4 = var1.bootstrap_method_specifiers[var3];
         this.indent(1);
         this.print(var3 + ": #" + var4.bootstrap_method_ref + " ");
         this.println(this.constantWriter.stringValue(var4.bootstrap_method_ref));
         this.indent(1);
         this.println("Method arguments:");
         this.indent(1);

         for(int var5 = 0; var5 < var4.bootstrap_arguments.length; ++var5) {
            this.print("#" + var4.bootstrap_arguments[var5] + " ");
            this.println(this.constantWriter.stringValue(var4.bootstrap_arguments[var5]));
         }

         this.indent(-3);
      }

      return null;
   }

   public Void visitCharacterRangeTable(CharacterRangeTable_attribute var1, Void var2) {
      this.println("CharacterRangeTable:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.character_range_table.length; ++var3) {
         CharacterRangeTable_attribute.Entry var4 = var1.character_range_table[var3];
         this.print(String.format("    %2d, %2d, %6x, %6x, %4x", var4.start_pc, var4.end_pc, var4.character_range_start, var4.character_range_end, var4.flags));
         this.tab();
         this.print(String.format("// %2d, %2d, %4d:%02d, %4d:%02d", var4.start_pc, var4.end_pc, var4.character_range_start >> 10, var4.character_range_start & 1023, var4.character_range_end >> 10, var4.character_range_end & 1023));
         if ((var4.flags & 1) != 0) {
            this.print(", statement");
         }

         if ((var4.flags & 2) != 0) {
            this.print(", block");
         }

         if ((var4.flags & 4) != 0) {
            this.print(", assignment");
         }

         if ((var4.flags & 8) != 0) {
            this.print(", flow-controller");
         }

         if ((var4.flags & 16) != 0) {
            this.print(", flow-target");
         }

         if ((var4.flags & 32) != 0) {
            this.print(", invoke");
         }

         if ((var4.flags & 64) != 0) {
            this.print(", create");
         }

         if ((var4.flags & 128) != 0) {
            this.print(", branch-true");
         }

         if ((var4.flags & 256) != 0) {
            this.print(", branch-false");
         }

         this.println();
      }

      this.indent(-1);
      return null;
   }

   public Void visitCode(Code_attribute var1, Void var2) {
      this.codeWriter.write(var1, this.constant_pool);
      return null;
   }

   public Void visitCompilationID(CompilationID_attribute var1, Void var2) {
      this.constantWriter.write(var1.compilationID_index);
      return null;
   }

   public Void visitConstantValue(ConstantValue_attribute var1, Void var2) {
      this.print("ConstantValue: ");
      this.constantWriter.write(var1.constantvalue_index);
      this.println();
      return null;
   }

   public Void visitDeprecated(Deprecated_attribute var1, Void var2) {
      this.println("Deprecated: true");
      return null;
   }

   public Void visitEnclosingMethod(EnclosingMethod_attribute var1, Void var2) {
      this.print("EnclosingMethod: #" + var1.class_index + ".#" + var1.method_index);
      this.tab();
      this.print("// " + this.getJavaClassName(var1));
      if (var1.method_index != 0) {
         this.print("." + this.getMethodName(var1));
      }

      this.println();
      return null;
   }

   private String getJavaClassName(EnclosingMethod_attribute var1) {
      try {
         return getJavaName(var1.getClassName(this.constant_pool));
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   private String getMethodName(EnclosingMethod_attribute var1) {
      try {
         return var1.getMethodName(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   public Void visitExceptions(Exceptions_attribute var1, Void var2) {
      this.println("Exceptions:");
      this.indent(1);
      this.print("throws ");

      for(int var3 = 0; var3 < var1.number_of_exceptions; ++var3) {
         if (var3 > 0) {
            this.print(", ");
         }

         this.print(this.getJavaException(var1, var3));
      }

      this.println();
      this.indent(-1);
      return null;
   }

   private String getJavaException(Exceptions_attribute var1, int var2) {
      try {
         return getJavaName(var1.getException(var2, this.constant_pool));
      } catch (ConstantPoolException var4) {
         return this.report(var4);
      }
   }

   public Void visitInnerClasses(InnerClasses_attribute var1, Void var2) {
      boolean var3 = true;

      for(int var4 = 0; var4 < var1.classes.length; ++var4) {
         InnerClasses_attribute.Info var5 = var1.classes[var4];
         AccessFlags var6 = var5.inner_class_access_flags;
         if (this.options.checkAccess(var6)) {
            if (var3) {
               this.writeInnerClassHeader();
               var3 = false;
            }

            this.print("   ");
            Iterator var7 = var6.getInnerClassModifiers().iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               this.print(var8 + " ");
            }

            if (var5.inner_name_index != 0) {
               this.print("#" + var5.inner_name_index + "= ");
            }

            this.print("#" + var5.inner_class_info_index);
            if (var5.outer_class_info_index != 0) {
               this.print(" of #" + var5.outer_class_info_index);
            }

            this.print("; //");
            if (var5.inner_name_index != 0) {
               this.print(this.getInnerName(this.constant_pool, var5) + "=");
            }

            this.constantWriter.write(var5.inner_class_info_index);
            if (var5.outer_class_info_index != 0) {
               this.print(" of ");
               this.constantWriter.write(var5.outer_class_info_index);
            }

            this.println();
         }
      }

      if (!var3) {
         this.indent(-1);
      }

      return null;
   }

   String getInnerName(ConstantPool var1, InnerClasses_attribute.Info var2) {
      try {
         return var2.getInnerName(var1);
      } catch (ConstantPoolException var4) {
         return this.report(var4);
      }
   }

   private void writeInnerClassHeader() {
      this.println("InnerClasses:");
      this.indent(1);
   }

   public Void visitLineNumberTable(LineNumberTable_attribute var1, Void var2) {
      this.println("LineNumberTable:");
      this.indent(1);
      LineNumberTable_attribute.Entry[] var3 = var1.line_number_table;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         LineNumberTable_attribute.Entry var6 = var3[var5];
         this.println("line " + var6.line_number + ": " + var6.start_pc);
      }

      this.indent(-1);
      return null;
   }

   public Void visitLocalVariableTable(LocalVariableTable_attribute var1, Void var2) {
      this.println("LocalVariableTable:");
      this.indent(1);
      this.println("Start  Length  Slot  Name   Signature");
      LocalVariableTable_attribute.Entry[] var3 = var1.local_variable_table;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         LocalVariableTable_attribute.Entry var6 = var3[var5];
         this.println(String.format("%5d %7d %5d %5s   %s", var6.start_pc, var6.length, var6.index, this.constantWriter.stringValue(var6.name_index), this.constantWriter.stringValue(var6.descriptor_index)));
      }

      this.indent(-1);
      return null;
   }

   public Void visitLocalVariableTypeTable(LocalVariableTypeTable_attribute var1, Void var2) {
      this.println("LocalVariableTypeTable:");
      this.indent(1);
      this.println("Start  Length  Slot  Name   Signature");
      LocalVariableTypeTable_attribute.Entry[] var3 = var1.local_variable_table;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         LocalVariableTypeTable_attribute.Entry var6 = var3[var5];
         this.println(String.format("%5d %7d %5d %5s   %s", var6.start_pc, var6.length, var6.index, this.constantWriter.stringValue(var6.name_index), this.constantWriter.stringValue(var6.signature_index)));
      }

      this.indent(-1);
      return null;
   }

   public Void visitMethodParameters(MethodParameters_attribute var1, Void var2) {
      String var3 = String.format("%-31s%s", "Name", "Flags");
      this.println("MethodParameters:");
      this.indent(1);
      this.println(var3);
      MethodParameters_attribute.Entry[] var4 = var1.method_parameter_table;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         MethodParameters_attribute.Entry var7 = var4[var6];
         String var8 = var7.name_index != 0 ? this.constantWriter.stringValue(var7.name_index) : "<no name>";
         String var9 = (0 != (var7.flags & 16) ? "final " : "") + (0 != (var7.flags & '耀') ? "mandated " : "") + (0 != (var7.flags & 4096) ? "synthetic" : "");
         this.println(String.format("%-31s%s", var8, var9));
      }

      this.indent(-1);
      return null;
   }

   public Void visitRuntimeVisibleAnnotations(RuntimeVisibleAnnotations_attribute var1, Void var2) {
      this.println("RuntimeVisibleAnnotations:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.annotations.length; ++var3) {
         this.print(var3 + ": ");
         this.annotationWriter.write(var1.annotations[var3]);
         this.println();
      }

      this.indent(-1);
      return null;
   }

   public Void visitRuntimeInvisibleAnnotations(RuntimeInvisibleAnnotations_attribute var1, Void var2) {
      this.println("RuntimeInvisibleAnnotations:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.annotations.length; ++var3) {
         this.print(var3 + ": ");
         this.annotationWriter.write(var1.annotations[var3]);
         this.println();
      }

      this.indent(-1);
      return null;
   }

   public Void visitRuntimeVisibleTypeAnnotations(RuntimeVisibleTypeAnnotations_attribute var1, Void var2) {
      this.println("RuntimeVisibleTypeAnnotations:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.annotations.length; ++var3) {
         this.print(var3 + ": ");
         this.annotationWriter.write(var1.annotations[var3]);
         this.println();
      }

      this.indent(-1);
      return null;
   }

   public Void visitRuntimeInvisibleTypeAnnotations(RuntimeInvisibleTypeAnnotations_attribute var1, Void var2) {
      this.println("RuntimeInvisibleTypeAnnotations:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.annotations.length; ++var3) {
         this.print(var3 + ": ");
         this.annotationWriter.write(var1.annotations[var3]);
         this.println();
      }

      this.indent(-1);
      return null;
   }

   public Void visitRuntimeVisibleParameterAnnotations(RuntimeVisibleParameterAnnotations_attribute var1, Void var2) {
      this.println("RuntimeVisibleParameterAnnotations:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.parameter_annotations.length; ++var3) {
         this.println("parameter " + var3 + ": ");
         this.indent(1);

         for(int var4 = 0; var4 < var1.parameter_annotations[var3].length; ++var4) {
            this.print(var4 + ": ");
            this.annotationWriter.write(var1.parameter_annotations[var3][var4]);
            this.println();
         }

         this.indent(-1);
      }

      this.indent(-1);
      return null;
   }

   public Void visitRuntimeInvisibleParameterAnnotations(RuntimeInvisibleParameterAnnotations_attribute var1, Void var2) {
      this.println("RuntimeInvisibleParameterAnnotations:");
      this.indent(1);

      for(int var3 = 0; var3 < var1.parameter_annotations.length; ++var3) {
         this.println(var3 + ": ");
         this.indent(1);

         for(int var4 = 0; var4 < var1.parameter_annotations[var3].length; ++var4) {
            this.print(var4 + ": ");
            this.annotationWriter.write(var1.parameter_annotations[var3][var4]);
            this.println();
         }

         this.indent(-1);
      }

      this.indent(-1);
      return null;
   }

   public Void visitSignature(Signature_attribute var1, Void var2) {
      this.print("Signature: #" + var1.signature_index);
      this.tab();
      this.println("// " + this.getSignature(var1));
      return null;
   }

   String getSignature(Signature_attribute var1) {
      try {
         return var1.getSignature(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   public Void visitSourceDebugExtension(SourceDebugExtension_attribute var1, Void var2) {
      this.println("SourceDebugExtension:");
      this.indent(1);
      String[] var3 = var1.getValue().split("[\r\n]+");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         this.println(var6);
      }

      this.indent(-1);
      return null;
   }

   public Void visitSourceFile(SourceFile_attribute var1, Void var2) {
      this.println("SourceFile: \"" + this.getSourceFile(var1) + "\"");
      return null;
   }

   private String getSourceFile(SourceFile_attribute var1) {
      try {
         return var1.getSourceFile(this.constant_pool);
      } catch (ConstantPoolException var3) {
         return this.report(var3);
      }
   }

   public Void visitSourceID(SourceID_attribute var1, Void var2) {
      this.constantWriter.write(var1.sourceID_index);
      return null;
   }

   public Void visitStackMap(StackMap_attribute var1, Void var2) {
      this.println("StackMap: number_of_entries = " + var1.number_of_entries);
      this.indent(1);
      StackMapTableWriter var3 = new StackMapTableWriter();
      StackMap_attribute.stack_map_frame[] var4 = var1.entries;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         StackMap_attribute.stack_map_frame var7 = var4[var6];
         var3.write(var7);
      }

      this.indent(-1);
      return null;
   }

   public Void visitStackMapTable(StackMapTable_attribute var1, Void var2) {
      this.println("StackMapTable: number_of_entries = " + var1.number_of_entries);
      this.indent(1);
      StackMapTableWriter var3 = new StackMapTableWriter();
      StackMapTable_attribute.stack_map_frame[] var4 = var1.entries;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         StackMapTable_attribute.stack_map_frame var7 = var4[var6];
         var3.write(var7);
      }

      this.indent(-1);
      return null;
   }

   public Void visitSynthetic(Synthetic_attribute var1, Void var2) {
      this.println("Synthetic: true");
      return null;
   }

   static String getJavaName(String var0) {
      return var0.replace('/', '.');
   }

   String toHex(byte var1, int var2) {
      return toHex(var1 & 255, var2);
   }

   static String toHex(int var0) {
      return StringUtils.toUpperCase(Integer.toString(var0, 16));
   }

   static String toHex(int var0, int var1) {
      String var2;
      for(var2 = StringUtils.toUpperCase(Integer.toHexString(var0)); var2.length() < var1; var2 = "0" + var2) {
      }

      return StringUtils.toUpperCase(var2);
   }

   class StackMapTableWriter implements StackMapTable_attribute.stack_map_frame.Visitor {
      public void write(StackMapTable_attribute.stack_map_frame var1) {
         var1.accept(this, (Object)null);
      }

      public Void visit_same_frame(StackMapTable_attribute.same_frame var1, Void var2) {
         this.printHeader(var1, "/* same */");
         return null;
      }

      public Void visit_same_locals_1_stack_item_frame(StackMapTable_attribute.same_locals_1_stack_item_frame var1, Void var2) {
         this.printHeader(var1, "/* same_locals_1_stack_item */");
         AttributeWriter.this.indent(1);
         this.printMap("stack", var1.stack);
         AttributeWriter.this.indent(-1);
         return null;
      }

      public Void visit_same_locals_1_stack_item_frame_extended(StackMapTable_attribute.same_locals_1_stack_item_frame_extended var1, Void var2) {
         this.printHeader(var1, "/* same_locals_1_stack_item_frame_extended */");
         AttributeWriter.this.indent(1);
         AttributeWriter.this.println("offset_delta = " + var1.offset_delta);
         this.printMap("stack", var1.stack);
         AttributeWriter.this.indent(-1);
         return null;
      }

      public Void visit_chop_frame(StackMapTable_attribute.chop_frame var1, Void var2) {
         this.printHeader(var1, "/* chop */");
         AttributeWriter.this.indent(1);
         AttributeWriter.this.println("offset_delta = " + var1.offset_delta);
         AttributeWriter.this.indent(-1);
         return null;
      }

      public Void visit_same_frame_extended(StackMapTable_attribute.same_frame_extended var1, Void var2) {
         this.printHeader(var1, "/* same_frame_extended */");
         AttributeWriter.this.indent(1);
         AttributeWriter.this.println("offset_delta = " + var1.offset_delta);
         AttributeWriter.this.indent(-1);
         return null;
      }

      public Void visit_append_frame(StackMapTable_attribute.append_frame var1, Void var2) {
         this.printHeader(var1, "/* append */");
         AttributeWriter.this.indent(1);
         AttributeWriter.this.println("offset_delta = " + var1.offset_delta);
         this.printMap("locals", var1.locals);
         AttributeWriter.this.indent(-1);
         return null;
      }

      public Void visit_full_frame(StackMapTable_attribute.full_frame var1, Void var2) {
         if (var1 instanceof StackMap_attribute.stack_map_frame) {
            this.printHeader(var1, "offset = " + var1.offset_delta);
            AttributeWriter.this.indent(1);
         } else {
            this.printHeader(var1, "/* full_frame */");
            AttributeWriter.this.indent(1);
            AttributeWriter.this.println("offset_delta = " + var1.offset_delta);
         }

         this.printMap("locals", var1.locals);
         this.printMap("stack", var1.stack);
         AttributeWriter.this.indent(-1);
         return null;
      }

      void printHeader(StackMapTable_attribute.stack_map_frame var1, String var2) {
         AttributeWriter.this.print("frame_type = " + var1.frame_type + " ");
         AttributeWriter.this.println(var2);
      }

      void printMap(String var1, StackMapTable_attribute.verification_type_info[] var2) {
         AttributeWriter.this.print(var1 + " = [");

         for(int var3 = 0; var3 < var2.length; ++var3) {
            StackMapTable_attribute.verification_type_info var4 = var2[var3];
            int var5 = var4.tag;
            switch (var5) {
               case 7:
                  AttributeWriter.this.print(" ");
                  AttributeWriter.this.constantWriter.write(((StackMapTable_attribute.Object_variable_info)var4).cpool_index);
                  break;
               case 8:
                  AttributeWriter.this.print(" " + this.mapTypeName(var5));
                  AttributeWriter.this.print(" " + ((StackMapTable_attribute.Uninitialized_variable_info)var4).offset);
                  break;
               default:
                  AttributeWriter.this.print(" " + this.mapTypeName(var5));
            }

            AttributeWriter.this.print(var3 == var2.length - 1 ? " " : ",");
         }

         AttributeWriter.this.println("]");
      }

      String mapTypeName(int var1) {
         switch (var1) {
            case 0:
               return "top";
            case 1:
               return "int";
            case 2:
               return "float";
            case 3:
               return "double";
            case 4:
               return "long";
            case 5:
               return "null";
            case 6:
               return "this";
            case 7:
               return "CP";
            case 8:
               return "uninitialized";
            default:
               AttributeWriter.this.report("unrecognized verification_type_info tag: " + var1);
               return "[tag:" + var1 + "]";
         }
      }
   }
}
