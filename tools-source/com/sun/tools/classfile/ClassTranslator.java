package com.sun.tools.classfile;

import java.util.Map;

public class ClassTranslator implements ConstantPool.Visitor {
   public ClassFile translate(ClassFile var1, Map var2) {
      ClassFile var3 = (ClassFile)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.constant_pool, var2);
         Field[] var5 = this.translate(var1.fields, var1.constant_pool, var2);
         Method[] var6 = this.translateMethods(var1.methods, var1.constant_pool, var2);
         Attributes var7 = this.translateAttributes(var1.attributes, var1.constant_pool, var2);
         if (var4 == var1.constant_pool && var5 == var1.fields && var6 == var1.methods && var7 == var1.attributes) {
            var3 = var1;
         } else {
            var3 = new ClassFile(var1.magic, var1.minor_version, var1.major_version, var4, var1.access_flags, var1.this_class, var1.super_class, var1.interfaces, var5, var6, var7);
         }

         var2.put(var1, var3);
      }

      return var3;
   }

   ConstantPool translate(ConstantPool var1, Map var2) {
      ConstantPool var3 = (ConstantPool)var2.get(var1);
      if (var3 == null) {
         ConstantPool.CPInfo[] var4 = new ConstantPool.CPInfo[var1.size()];
         boolean var5 = true;

         ConstantPool.CPInfo var7;
         for(int var6 = 0; var6 < var1.size(); var6 += var7.size()) {
            try {
               var7 = var1.get(var6);
            } catch (ConstantPool.InvalidIndex var9) {
               throw new IllegalStateException(var9);
            }

            ConstantPool.CPInfo var8 = this.translate(var7, var2);
            var5 &= var7 == var8;
            var4[var6] = var8;
            if (var7.getTag() != var8.getTag()) {
               throw new IllegalStateException();
            }
         }

         if (var5) {
            var3 = var1;
         } else {
            var3 = new ConstantPool(var4);
         }

         var2.put(var1, var3);
      }

      return var3;
   }

   ConstantPool.CPInfo translate(ConstantPool.CPInfo var1, Map var2) {
      ConstantPool.CPInfo var3 = (ConstantPool.CPInfo)var2.get(var1);
      if (var3 == null) {
         var3 = (ConstantPool.CPInfo)var1.accept(this, var2);
         var2.put(var1, var3);
      }

      return var3;
   }

   Field[] translate(Field[] var1, ConstantPool var2, Map var3) {
      Field[] var4 = (Field[])((Field[])var3.get(var1));
      if (var4 == null) {
         var4 = new Field[var1.length];

         for(int var5 = 0; var5 < var1.length; ++var5) {
            var4[var5] = this.translate(var1[var5], var2, var3);
         }

         if (equal(var1, var4)) {
            var4 = var1;
         }

         var3.put(var1, var4);
      }

      return var4;
   }

   Field translate(Field var1, ConstantPool var2, Map var3) {
      Field var4 = (Field)var3.get(var1);
      if (var4 == null) {
         Attributes var5 = this.translateAttributes(var1.attributes, var2, var3);
         if (var5 == var1.attributes) {
            var4 = var1;
         } else {
            var4 = new Field(var1.access_flags, var1.name_index, var1.descriptor, var5);
         }

         var3.put(var1, var4);
      }

      return var4;
   }

   Method[] translateMethods(Method[] var1, ConstantPool var2, Map var3) {
      Method[] var4 = (Method[])((Method[])var3.get(var1));
      if (var4 == null) {
         var4 = new Method[var1.length];

         for(int var5 = 0; var5 < var1.length; ++var5) {
            var4[var5] = this.translate(var1[var5], var2, var3);
         }

         if (equal(var1, var4)) {
            var4 = var1;
         }

         var3.put(var1, var4);
      }

      return var4;
   }

   Method translate(Method var1, ConstantPool var2, Map var3) {
      Method var4 = (Method)var3.get(var1);
      if (var4 == null) {
         Attributes var5 = this.translateAttributes(var1.attributes, var2, var3);
         if (var5 == var1.attributes) {
            var4 = var1;
         } else {
            var4 = new Method(var1.access_flags, var1.name_index, var1.descriptor, var5);
         }

         var3.put(var1, var4);
      }

      return var4;
   }

   Attributes translateAttributes(Attributes var1, ConstantPool var2, Map var3) {
      Attributes var4 = (Attributes)var3.get(var1);
      if (var4 == null) {
         Attribute[] var5 = new Attribute[var1.size()];
         ConstantPool var6 = this.translate(var2, var3);
         boolean var7 = true;

         for(int var8 = 0; var8 < var1.size(); ++var8) {
            Attribute var9 = var1.get(var8);
            Attribute var10 = this.translate(var9, var3);
            if (var10 != var9) {
               var7 = false;
            }

            var5[var8] = var10;
         }

         if (var6 == var2 && var7) {
            var4 = var1;
         } else {
            var4 = new Attributes(var6, var5);
         }

         var3.put(var1, var4);
      }

      return var4;
   }

   Attribute translate(Attribute var1, Map var2) {
      Attribute var3 = (Attribute)var2.get(var1);
      if (var3 == null) {
         var3 = var1;
         var2.put(var1, var1);
      }

      return var3;
   }

   private static boolean equal(Object[] var0, Object[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               if (var0[var2] != var1[var2]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0 == var1;
      }
   }

   public ConstantPool.CPInfo visitClass(ConstantPool.CONSTANT_Class_info var1, Map var2) {
      ConstantPool.CONSTANT_Class_info var3 = (ConstantPool.CONSTANT_Class_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_Class_info(var4, var1.name_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitDouble(ConstantPool.CONSTANT_Double_info var1, Map var2) {
      ConstantPool.CONSTANT_Double_info var3 = (ConstantPool.CONSTANT_Double_info)var2.get(var1);
      if (var3 == null) {
         var2.put(var1, var1);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitFieldref(ConstantPool.CONSTANT_Fieldref_info var1, Map var2) {
      ConstantPool.CONSTANT_Fieldref_info var3 = (ConstantPool.CONSTANT_Fieldref_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_Fieldref_info(var4, var1.class_index, var1.name_and_type_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitFloat(ConstantPool.CONSTANT_Float_info var1, Map var2) {
      ConstantPool.CONSTANT_Float_info var3 = (ConstantPool.CONSTANT_Float_info)var2.get(var1);
      if (var3 == null) {
         var2.put(var1, var1);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitInteger(ConstantPool.CONSTANT_Integer_info var1, Map var2) {
      ConstantPool.CONSTANT_Integer_info var3 = (ConstantPool.CONSTANT_Integer_info)var2.get(var1);
      if (var3 == null) {
         var2.put(var1, var1);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info var1, Map var2) {
      ConstantPool.CONSTANT_InterfaceMethodref_info var3 = (ConstantPool.CONSTANT_InterfaceMethodref_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_InterfaceMethodref_info(var4, var1.class_index, var1.name_and_type_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info var1, Map var2) {
      ConstantPool.CONSTANT_InvokeDynamic_info var3 = (ConstantPool.CONSTANT_InvokeDynamic_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_InvokeDynamic_info(var4, var1.bootstrap_method_attr_index, var1.name_and_type_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitLong(ConstantPool.CONSTANT_Long_info var1, Map var2) {
      ConstantPool.CONSTANT_Long_info var3 = (ConstantPool.CONSTANT_Long_info)var2.get(var1);
      if (var3 == null) {
         var2.put(var1, var1);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitNameAndType(ConstantPool.CONSTANT_NameAndType_info var1, Map var2) {
      ConstantPool.CONSTANT_NameAndType_info var3 = (ConstantPool.CONSTANT_NameAndType_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_NameAndType_info(var4, var1.name_index, var1.type_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitMethodref(ConstantPool.CONSTANT_Methodref_info var1, Map var2) {
      ConstantPool.CONSTANT_Methodref_info var3 = (ConstantPool.CONSTANT_Methodref_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_Methodref_info(var4, var1.class_index, var1.name_and_type_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info var1, Map var2) {
      ConstantPool.CONSTANT_MethodHandle_info var3 = (ConstantPool.CONSTANT_MethodHandle_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_MethodHandle_info(var4, var1.reference_kind, var1.reference_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitMethodType(ConstantPool.CONSTANT_MethodType_info var1, Map var2) {
      ConstantPool.CONSTANT_MethodType_info var3 = (ConstantPool.CONSTANT_MethodType_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_MethodType_info(var4, var1.descriptor_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitString(ConstantPool.CONSTANT_String_info var1, Map var2) {
      ConstantPool.CONSTANT_String_info var3 = (ConstantPool.CONSTANT_String_info)var2.get(var1);
      if (var3 == null) {
         ConstantPool var4 = this.translate(var1.cp, var2);
         if (var4 == var1.cp) {
            var3 = var1;
         } else {
            var3 = new ConstantPool.CONSTANT_String_info(var4, var1.string_index);
         }

         var2.put(var1, var3);
      }

      return var1;
   }

   public ConstantPool.CPInfo visitUtf8(ConstantPool.CONSTANT_Utf8_info var1, Map var2) {
      ConstantPool.CONSTANT_Utf8_info var3 = (ConstantPool.CONSTANT_Utf8_info)var2.get(var1);
      if (var3 == null) {
         var2.put(var1, var1);
      }

      return var1;
   }
}
