package com.sun.tools.javap;

import com.sun.tools.classfile.Annotation;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Descriptor;
import com.sun.tools.classfile.TypeAnnotation;

public class AnnotationWriter extends BasicWriter {
   element_value_Writer ev_writer = new element_value_Writer();
   private ClassWriter classWriter;
   private ConstantWriter constantWriter;

   static AnnotationWriter instance(Context var0) {
      AnnotationWriter var1 = (AnnotationWriter)var0.get(AnnotationWriter.class);
      if (var1 == null) {
         var1 = new AnnotationWriter(var0);
      }

      return var1;
   }

   protected AnnotationWriter(Context var1) {
      super(var1);
      this.classWriter = ClassWriter.instance(var1);
      this.constantWriter = ConstantWriter.instance(var1);
   }

   public void write(Annotation var1) {
      this.write(var1, false);
   }

   public void write(Annotation var1, boolean var2) {
      this.writeDescriptor(var1.type_index, var2);
      boolean var3 = var1.num_element_value_pairs > 0 || !var2;
      if (var3) {
         this.print("(");
      }

      for(int var4 = 0; var4 < var1.num_element_value_pairs; ++var4) {
         if (var4 > 0) {
            this.print(",");
         }

         this.write(var1.element_value_pairs[var4], var2);
      }

      if (var3) {
         this.print(")");
      }

   }

   public void write(TypeAnnotation var1) {
      this.write(var1, true, false);
   }

   public void write(TypeAnnotation var1, boolean var2, boolean var3) {
      this.write(var1.annotation, var3);
      this.print(": ");
      this.write(var1.position, var2);
   }

   public void write(TypeAnnotation.Position var1, boolean var2) {
      this.print(var1.type);
      switch (var1.type) {
         case INSTANCEOF:
         case NEW:
         case CONSTRUCTOR_REFERENCE:
         case METHOD_REFERENCE:
            if (var2) {
               this.print(", offset=");
               this.print(var1.offset);
            }
            break;
         case LOCAL_VARIABLE:
         case RESOURCE_VARIABLE:
            if (var1.lvarOffset == null) {
               this.print(", lvarOffset is Null!");
            } else {
               this.print(", {");

               for(int var3 = 0; var3 < var1.lvarOffset.length; ++var3) {
                  if (var3 != 0) {
                     this.print("; ");
                  }

                  if (var2) {
                     this.print("start_pc=");
                     this.print(var1.lvarOffset[var3]);
                  }

                  this.print(", length=");
                  this.print(var1.lvarLength[var3]);
                  this.print(", index=");
                  this.print(var1.lvarIndex[var3]);
               }

               this.print("}");
            }
            break;
         case EXCEPTION_PARAMETER:
            this.print(", exception_index=");
            this.print(var1.exception_index);
         case METHOD_RECEIVER:
         case METHOD_RETURN:
         case FIELD:
            break;
         case CLASS_TYPE_PARAMETER:
         case METHOD_TYPE_PARAMETER:
            this.print(", param_index=");
            this.print(var1.parameter_index);
            break;
         case CLASS_TYPE_PARAMETER_BOUND:
         case METHOD_TYPE_PARAMETER_BOUND:
            this.print(", param_index=");
            this.print(var1.parameter_index);
            this.print(", bound_index=");
            this.print(var1.bound_index);
            break;
         case CLASS_EXTENDS:
            this.print(", type_index=");
            this.print(var1.type_index);
            break;
         case THROWS:
            this.print(", type_index=");
            this.print(var1.type_index);
            break;
         case METHOD_FORMAL_PARAMETER:
            this.print(", param_index=");
            this.print(var1.parameter_index);
            break;
         case CAST:
         case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
         case METHOD_INVOCATION_TYPE_ARGUMENT:
         case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
         case METHOD_REFERENCE_TYPE_ARGUMENT:
            if (var2) {
               this.print(", offset=");
               this.print(var1.offset);
            }

            this.print(", type_index=");
            this.print(var1.type_index);
            break;
         case UNKNOWN:
            throw new AssertionError("AnnotationWriter: UNKNOWN target type should never occur!");
         default:
            throw new AssertionError("AnnotationWriter: Unknown target type for position: " + var1);
      }

      if (!var1.location.isEmpty()) {
         this.print(", location=");
         this.print(var1.location);
      }

   }

   public void write(Annotation.element_value_pair var1) {
      this.write(var1, false);
   }

   public void write(Annotation.element_value_pair var1, boolean var2) {
      this.writeIndex(var1.element_name_index, var2);
      this.print("=");
      this.write(var1.value, var2);
   }

   public void write(Annotation.element_value var1) {
      this.write(var1, false);
   }

   public void write(Annotation.element_value var1, boolean var2) {
      this.ev_writer.write(var1, var2);
   }

   private void writeDescriptor(int var1, boolean var2) {
      if (var2) {
         try {
            ConstantPool var3 = this.classWriter.getClassFile().constant_pool;
            Descriptor var4 = new Descriptor(var1);
            this.print(var4.getFieldType(var3));
            return;
         } catch (ConstantPoolException var5) {
         } catch (Descriptor.InvalidDescriptor var6) {
         }
      }

      this.print("#" + var1);
   }

   private void writeIndex(int var1, boolean var2) {
      if (var2) {
         this.print(this.constantWriter.stringValue(var1));
      } else {
         this.print("#" + var1);
      }

   }

   class element_value_Writer implements Annotation.element_value.Visitor {
      public void write(Annotation.element_value var1, boolean var2) {
         var1.accept(this, var2);
      }

      public Void visitPrimitive(Annotation.Primitive_element_value var1, Boolean var2) {
         if (var2) {
            AnnotationWriter.this.writeIndex(var1.const_value_index, var2);
         } else {
            AnnotationWriter.this.print((char)var1.tag + "#" + var1.const_value_index);
         }

         return null;
      }

      public Void visitEnum(Annotation.Enum_element_value var1, Boolean var2) {
         if (var2) {
            AnnotationWriter.this.writeIndex(var1.type_name_index, var2);
            AnnotationWriter.this.print(".");
            AnnotationWriter.this.writeIndex(var1.const_name_index, var2);
         } else {
            AnnotationWriter.this.print((char)var1.tag + "#" + var1.type_name_index + ".#" + var1.const_name_index);
         }

         return null;
      }

      public Void visitClass(Annotation.Class_element_value var1, Boolean var2) {
         if (var2) {
            AnnotationWriter.this.writeIndex(var1.class_info_index, var2);
            AnnotationWriter.this.print(".class");
         } else {
            AnnotationWriter.this.print((char)var1.tag + "#" + var1.class_info_index);
         }

         return null;
      }

      public Void visitAnnotation(Annotation.Annotation_element_value var1, Boolean var2) {
         AnnotationWriter.this.print((char)var1.tag);
         AnnotationWriter.this.write(var1.annotation_value, var2);
         return null;
      }

      public Void visitArray(Annotation.Array_element_value var1, Boolean var2) {
         AnnotationWriter.this.print("[");

         for(int var3 = 0; var3 < var1.num_values; ++var3) {
            if (var3 > 0) {
               AnnotationWriter.this.print(",");
            }

            this.write(var1.values[var3], var2);
         }

         AnnotationWriter.this.print("]");
         return null;
      }
   }
}
