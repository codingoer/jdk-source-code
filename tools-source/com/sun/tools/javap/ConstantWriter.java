package com.sun.tools.javap;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;

public class ConstantWriter extends BasicWriter {
   StringValueVisitor stringValueVisitor = new StringValueVisitor();
   private ClassWriter classWriter;
   private Options options;

   public static ConstantWriter instance(Context var0) {
      ConstantWriter var1 = (ConstantWriter)var0.get(ConstantWriter.class);
      if (var1 == null) {
         var1 = new ConstantWriter(var0);
      }

      return var1;
   }

   protected ConstantWriter(Context var1) {
      super(var1);
      var1.put(ConstantWriter.class, this);
      this.classWriter = ClassWriter.instance(var1);
      this.options = Options.instance(var1);
   }

   protected void writeConstantPool() {
      ConstantPool var1 = this.classWriter.getClassFile().constant_pool;
      this.writeConstantPool(var1);
   }

   protected void writeConstantPool(ConstantPool var1) {
      ConstantPool.Visitor var2 = new ConstantPool.Visitor() {
         public Integer visitClass(ConstantPool.CONSTANT_Class_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.name_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitDouble(ConstantPool.CONSTANT_Double_info var1, Void var2) {
            ConstantWriter.this.println(ConstantWriter.this.stringValue(var1));
            return 2;
         }

         public Integer visitFieldref(ConstantPool.CONSTANT_Fieldref_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.class_index + ".#" + var1.name_and_type_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitFloat(ConstantPool.CONSTANT_Float_info var1, Void var2) {
            ConstantWriter.this.println(ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitInteger(ConstantPool.CONSTANT_Integer_info var1, Void var2) {
            ConstantWriter.this.println(ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.class_index + ".#" + var1.name_and_type_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.bootstrap_method_attr_index + ":#" + var1.name_and_type_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitLong(ConstantPool.CONSTANT_Long_info var1, Void var2) {
            ConstantWriter.this.println(ConstantWriter.this.stringValue(var1));
            return 2;
         }

         public Integer visitNameAndType(ConstantPool.CONSTANT_NameAndType_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.name_index + ":#" + var1.type_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitMethodref(ConstantPool.CONSTANT_Methodref_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.class_index + ".#" + var1.name_and_type_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.reference_kind.tag + ":#" + var1.reference_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitMethodType(ConstantPool.CONSTANT_MethodType_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.descriptor_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("//  " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitString(ConstantPool.CONSTANT_String_info var1, Void var2) {
            ConstantWriter.this.print("#" + var1.string_index);
            ConstantWriter.this.tab();
            ConstantWriter.this.println("// " + ConstantWriter.this.stringValue(var1));
            return 1;
         }

         public Integer visitUtf8(ConstantPool.CONSTANT_Utf8_info var1, Void var2) {
            ConstantWriter.this.println(ConstantWriter.this.stringValue(var1));
            return 1;
         }
      };
      this.println("Constant pool:");
      this.indent(1);
      int var3 = String.valueOf(var1.size()).length() + 1;
      int var4 = 1;

      while(var4 < var1.size()) {
         this.print(String.format("%" + var3 + "s", "#" + var4));

         try {
            ConstantPool.CPInfo var5 = var1.get(var4);
            this.print(String.format(" = %-18s ", this.cpTagName(var5)));
            var4 += (Integer)var5.accept(var2, (Object)null);
         } catch (ConstantPool.InvalidIndex var6) {
         }
      }

      this.indent(-1);
   }

   protected void write(int var1) {
      ClassFile var2 = this.classWriter.getClassFile();
      if (var1 == 0) {
         this.print("#0");
      } else {
         ConstantPool.CPInfo var3;
         try {
            var3 = var2.constant_pool.get(var1);
         } catch (ConstantPoolException var8) {
            this.print("#" + var1);
            return;
         }

         int var4 = var3.getTag();
         switch (var4) {
            case 9:
            case 10:
            case 11:
               ConstantPool.CPRefInfo var5 = (ConstantPool.CPRefInfo)var3;

               try {
                  if (var5.class_index == var2.this_class) {
                     var3 = var2.constant_pool.get(var5.name_and_type_index);
                  }
               } catch (ConstantPool.InvalidIndex var7) {
               }
            default:
               this.print(this.tagName(var4) + " " + this.stringValue(var3));
         }
      }
   }

   String cpTagName(ConstantPool.CPInfo var1) {
      String var2 = var1.getClass().getSimpleName();
      return var2.replace("CONSTANT_", "").replace("_info", "");
   }

   String tagName(int var1) {
      switch (var1) {
         case 1:
            return "Utf8";
         case 2:
         case 13:
         case 14:
         case 17:
         default:
            return "(unknown tag " + var1 + ")";
         case 3:
            return "int";
         case 4:
            return "float";
         case 5:
            return "long";
         case 6:
            return "double";
         case 7:
            return "class";
         case 8:
            return "String";
         case 9:
            return "Field";
         case 10:
            return "Method";
         case 11:
            return "InterfaceMethod";
         case 12:
            return "NameAndType";
         case 15:
            return "MethodHandle";
         case 16:
            return "MethodType";
         case 18:
            return "InvokeDynamic";
      }
   }

   String stringValue(int var1) {
      ClassFile var2 = this.classWriter.getClassFile();

      try {
         return this.stringValue(var2.constant_pool.get(var1));
      } catch (ConstantPool.InvalidIndex var4) {
         return this.report(var4);
      }
   }

   String stringValue(ConstantPool.CPInfo var1) {
      return this.stringValueVisitor.visit(var1);
   }

   private static String checkName(String var0) {
      if (var0 == null) {
         return "null";
      } else {
         int var1 = var0.length();
         if (var1 == 0) {
            return "\"\"";
         } else {
            int var2 = 47;

            int var3;
            for(int var4 = 0; var4 < var1; var4 += Character.charCount(var3)) {
               var3 = var0.codePointAt(var4);
               if (var2 == 47 && !Character.isJavaIdentifierStart(var3) || var3 != 47 && !Character.isJavaIdentifierPart(var3)) {
                  return "\"" + addEscapes(var0) + "\"";
               }

               var2 = var3;
            }

            return var0;
         }
      }
   }

   private static String addEscapes(String var0) {
      String var1 = "\\\"\n\t";
      String var2 = "\\\"nt";
      StringBuilder var3 = null;
      int var4 = 0;
      int var5 = var0.length();

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var0.charAt(var6);
         int var8 = var1.indexOf(var7);
         if (var8 >= 0) {
            if (var3 == null) {
               var3 = new StringBuilder(var5 * 2);
            }

            if (var4 < var6) {
               var3.append(var0, var4, var6);
            }

            var3.append('\\');
            var3.append(var2.charAt(var8));
            var4 = var6 + 1;
         }
      }

      if (var3 == null) {
         return var0;
      } else {
         if (var4 < var5) {
            var3.append(var0, var4, var5);
         }

         return var3.toString();
      }
   }

   private class StringValueVisitor implements ConstantPool.Visitor {
      private StringValueVisitor() {
      }

      public String visit(ConstantPool.CPInfo var1) {
         return (String)var1.accept(this, (Object)null);
      }

      public String visitClass(ConstantPool.CONSTANT_Class_info var1, Void var2) {
         return this.getCheckedName(var1);
      }

      String getCheckedName(ConstantPool.CONSTANT_Class_info var1) {
         try {
            return ConstantWriter.checkName(var1.getName());
         } catch (ConstantPoolException var3) {
            return ConstantWriter.this.report(var3);
         }
      }

      public String visitDouble(ConstantPool.CONSTANT_Double_info var1, Void var2) {
         return var1.value + "d";
      }

      public String visitFieldref(ConstantPool.CONSTANT_Fieldref_info var1, Void var2) {
         return this.visitRef(var1, var2);
      }

      public String visitFloat(ConstantPool.CONSTANT_Float_info var1, Void var2) {
         return var1.value + "f";
      }

      public String visitInteger(ConstantPool.CONSTANT_Integer_info var1, Void var2) {
         return String.valueOf(var1.value);
      }

      public String visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info var1, Void var2) {
         return this.visitRef(var1, var2);
      }

      public String visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info var1, Void var2) {
         try {
            String var3 = ConstantWriter.this.stringValue(var1.getNameAndTypeInfo());
            return "#" + var1.bootstrap_method_attr_index + ":" + var3;
         } catch (ConstantPoolException var4) {
            return ConstantWriter.this.report(var4);
         }
      }

      public String visitLong(ConstantPool.CONSTANT_Long_info var1, Void var2) {
         return var1.value + "l";
      }

      public String visitNameAndType(ConstantPool.CONSTANT_NameAndType_info var1, Void var2) {
         return this.getCheckedName(var1) + ":" + this.getType(var1);
      }

      String getCheckedName(ConstantPool.CONSTANT_NameAndType_info var1) {
         try {
            return ConstantWriter.checkName(var1.getName());
         } catch (ConstantPoolException var3) {
            return ConstantWriter.this.report(var3);
         }
      }

      String getType(ConstantPool.CONSTANT_NameAndType_info var1) {
         try {
            return var1.getType();
         } catch (ConstantPoolException var3) {
            return ConstantWriter.this.report(var3);
         }
      }

      public String visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info var1, Void var2) {
         try {
            return var1.reference_kind.name + " " + ConstantWriter.this.stringValue(var1.getCPRefInfo());
         } catch (ConstantPoolException var4) {
            return ConstantWriter.this.report(var4);
         }
      }

      public String visitMethodType(ConstantPool.CONSTANT_MethodType_info var1, Void var2) {
         try {
            return var1.getType();
         } catch (ConstantPoolException var4) {
            return ConstantWriter.this.report(var4);
         }
      }

      public String visitMethodref(ConstantPool.CONSTANT_Methodref_info var1, Void var2) {
         return this.visitRef(var1, var2);
      }

      public String visitString(ConstantPool.CONSTANT_String_info var1, Void var2) {
         try {
            ClassFile var3 = ConstantWriter.this.classWriter.getClassFile();
            int var4 = var1.string_index;
            return ConstantWriter.this.stringValue(var3.constant_pool.getUTF8Info(var4));
         } catch (ConstantPoolException var5) {
            return ConstantWriter.this.report(var5);
         }
      }

      public String visitUtf8(ConstantPool.CONSTANT_Utf8_info var1, Void var2) {
         String var3 = var1.value;
         StringBuilder var4 = new StringBuilder();

         for(int var5 = 0; var5 < var3.length(); ++var5) {
            char var6 = var3.charAt(var5);
            switch (var6) {
               case '\t':
                  var4.append('\\').append('t');
                  break;
               case '\n':
                  var4.append('\\').append('n');
                  break;
               case '\r':
                  var4.append('\\').append('r');
                  break;
               case '"':
                  var4.append('\\').append('"');
                  break;
               default:
                  var4.append(var6);
            }
         }

         return var4.toString();
      }

      String visitRef(ConstantPool.CPRefInfo var1, Void var2) {
         String var3 = this.getCheckedClassName(var1);

         String var4;
         try {
            var4 = ConstantWriter.this.stringValue(var1.getNameAndTypeInfo());
         } catch (ConstantPoolException var6) {
            var4 = ConstantWriter.this.report(var6);
         }

         return var3 + "." + var4;
      }

      String getCheckedClassName(ConstantPool.CPRefInfo var1) {
         try {
            return ConstantWriter.checkName(var1.getClassName());
         } catch (ConstantPoolException var3) {
            return ConstantWriter.this.report(var3);
         }
      }

      // $FF: synthetic method
      StringValueVisitor(Object var2) {
         this();
      }
   }
}
