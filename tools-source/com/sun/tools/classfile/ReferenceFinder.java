package com.sun.tools.classfile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class ReferenceFinder {
   private final Filter filter;
   private final Visitor visitor;
   private ConstantPool.Visitor cpVisitor = new ConstantPool.Visitor() {
      public Boolean visitClass(ConstantPool.CONSTANT_Class_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info var1, ConstantPool var2) {
         return ReferenceFinder.this.filter.accept(var2, var1);
      }

      public Boolean visitMethodref(ConstantPool.CONSTANT_Methodref_info var1, ConstantPool var2) {
         return ReferenceFinder.this.filter.accept(var2, var1);
      }

      public Boolean visitFieldref(ConstantPool.CONSTANT_Fieldref_info var1, ConstantPool var2) {
         return ReferenceFinder.this.filter.accept(var2, var1);
      }

      public Boolean visitDouble(ConstantPool.CONSTANT_Double_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitFloat(ConstantPool.CONSTANT_Float_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitInteger(ConstantPool.CONSTANT_Integer_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitLong(ConstantPool.CONSTANT_Long_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitNameAndType(ConstantPool.CONSTANT_NameAndType_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitMethodType(ConstantPool.CONSTANT_MethodType_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitString(ConstantPool.CONSTANT_String_info var1, ConstantPool var2) {
         return false;
      }

      public Boolean visitUtf8(ConstantPool.CONSTANT_Utf8_info var1, ConstantPool var2) {
         return false;
      }
   };
   private Instruction.KindVisitor codeVisitor = new Instruction.KindVisitor() {
      public Integer visitNoOperands(Instruction var1, List var2) {
         return 0;
      }

      public Integer visitArrayType(Instruction var1, Instruction.TypeKind var2, List var3) {
         return 0;
      }

      public Integer visitBranch(Instruction var1, int var2, List var3) {
         return 0;
      }

      public Integer visitConstantPoolRef(Instruction var1, int var2, List var3) {
         return var3.contains(var2) ? var2 : 0;
      }

      public Integer visitConstantPoolRefAndValue(Instruction var1, int var2, int var3, List var4) {
         return var4.contains(var2) ? var2 : 0;
      }

      public Integer visitLocal(Instruction var1, int var2, List var3) {
         return 0;
      }

      public Integer visitLocalAndValue(Instruction var1, int var2, int var3, List var4) {
         return 0;
      }

      public Integer visitLookupSwitch(Instruction var1, int var2, int var3, int[] var4, int[] var5, List var6) {
         return 0;
      }

      public Integer visitTableSwitch(Instruction var1, int var2, int var3, int var4, int[] var5, List var6) {
         return 0;
      }

      public Integer visitValue(Instruction var1, int var2, List var3) {
         return 0;
      }

      public Integer visitUnknown(Instruction var1, List var2) {
         return 0;
      }
   };

   public ReferenceFinder(Filter var1, Visitor var2) {
      this.filter = (Filter)Objects.requireNonNull(var1);
      this.visitor = (Visitor)Objects.requireNonNull(var2);
   }

   public boolean parse(ClassFile var1) throws ConstantPoolException {
      ArrayList var2 = new ArrayList();
      int var3 = 1;

      ConstantPool.CPInfo var5;
      for(Iterator var4 = var1.constant_pool.entries().iterator(); var4.hasNext(); var3 += var5.size()) {
         var5 = (ConstantPool.CPInfo)var4.next();
         if ((Boolean)var5.accept(this.cpVisitor, var1.constant_pool)) {
            var2.add(var3);
         }
      }

      if (var2.isEmpty()) {
         return false;
      } else {
         Method[] var13 = var1.methods;
         int var14 = var13.length;

         for(int var6 = 0; var6 < var14; ++var6) {
            Method var7 = var13[var6];
            HashSet var8 = new HashSet();
            Code_attribute var9 = (Code_attribute)var7.attributes.get("Code");
            int var12;
            if (var9 != null) {
               Iterator var10 = var9.getInstructions().iterator();

               while(var10.hasNext()) {
                  Instruction var11 = (Instruction)var10.next();
                  var12 = (Integer)var11.accept(this.codeVisitor, var2);
                  if (var12 > 0) {
                     var8.add(var12);
                  }
               }
            }

            if (var8.size() > 0) {
               ArrayList var15 = new ArrayList(var8.size());
               Iterator var16 = var8.iterator();

               while(var16.hasNext()) {
                  var12 = (Integer)var16.next();
                  var15.add(ConstantPool.CPRefInfo.class.cast(var1.constant_pool.get(var12)));
               }

               this.visitor.visit(var1, var7, var15);
            }
         }

         return true;
      }
   }

   public interface Visitor {
      void visit(ClassFile var1, Method var2, List var3);
   }

   public interface Filter {
      boolean accept(ConstantPool var1, ConstantPool.CPRefInfo var2);
   }
}
