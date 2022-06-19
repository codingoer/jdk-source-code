package com.sun.tools.javap;

import com.sun.tools.classfile.Attributes;
import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.DescriptorException;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeWriter extends BasicWriter {
   Instruction.KindVisitor instructionPrinter = new Instruction.KindVisitor() {
      public Void visitNoOperands(Instruction var1, Integer var2) {
         return null;
      }

      public Void visitArrayType(Instruction var1, Instruction.TypeKind var2, Integer var3) {
         CodeWriter.this.print(" " + var2.name);
         return null;
      }

      public Void visitBranch(Instruction var1, int var2, Integer var3) {
         CodeWriter.this.print(var1.getPC() + var2);
         return null;
      }

      public Void visitConstantPoolRef(Instruction var1, int var2, Integer var3) {
         CodeWriter.this.print("#" + var2);
         CodeWriter.this.tab();
         CodeWriter.this.print("// ");
         CodeWriter.this.printConstant(var2);
         return null;
      }

      public Void visitConstantPoolRefAndValue(Instruction var1, int var2, int var3, Integer var4) {
         CodeWriter.this.print("#" + var2 + ",  " + var3);
         CodeWriter.this.tab();
         CodeWriter.this.print("// ");
         CodeWriter.this.printConstant(var2);
         return null;
      }

      public Void visitLocal(Instruction var1, int var2, Integer var3) {
         CodeWriter.this.print(var2);
         return null;
      }

      public Void visitLocalAndValue(Instruction var1, int var2, int var3, Integer var4) {
         CodeWriter.this.print(var2 + ", " + var3);
         return null;
      }

      public Void visitLookupSwitch(Instruction var1, int var2, int var3, int[] var4, int[] var5, Integer var6) {
         int var7 = var1.getPC();
         CodeWriter.this.print("{ // " + var3);
         CodeWriter.this.indent(var6);

         for(int var8 = 0; var8 < var3; ++var8) {
            CodeWriter.this.print(String.format("%n%12d: %d", var4[var8], var7 + var5[var8]));
         }

         CodeWriter.this.print("\n     default: " + (var7 + var2) + "\n}");
         CodeWriter.this.indent(-var6);
         return null;
      }

      public Void visitTableSwitch(Instruction var1, int var2, int var3, int var4, int[] var5, Integer var6) {
         int var7 = var1.getPC();
         CodeWriter.this.print("{ // " + var3 + " to " + var4);
         CodeWriter.this.indent(var6);

         for(int var8 = 0; var8 < var5.length; ++var8) {
            CodeWriter.this.print(String.format("%n%12d: %d", var3 + var8, var7 + var5[var8]));
         }

         CodeWriter.this.print("\n     default: " + (var7 + var2) + "\n}");
         CodeWriter.this.indent(-var6);
         return null;
      }

      public Void visitValue(Instruction var1, int var2, Integer var3) {
         CodeWriter.this.print(var2);
         return null;
      }

      public Void visitUnknown(Instruction var1, Integer var2) {
         return null;
      }
   };
   private AttributeWriter attrWriter;
   private ClassWriter classWriter;
   private ConstantWriter constantWriter;
   private LocalVariableTableWriter localVariableTableWriter;
   private LocalVariableTypeTableWriter localVariableTypeTableWriter;
   private TypeAnnotationWriter typeAnnotationWriter;
   private SourceWriter sourceWriter;
   private StackMapWriter stackMapWriter;
   private TryBlockWriter tryBlockWriter;
   private Options options;

   public static CodeWriter instance(Context var0) {
      CodeWriter var1 = (CodeWriter)var0.get(CodeWriter.class);
      if (var1 == null) {
         var1 = new CodeWriter(var0);
      }

      return var1;
   }

   protected CodeWriter(Context var1) {
      super(var1);
      var1.put(CodeWriter.class, this);
      this.attrWriter = AttributeWriter.instance(var1);
      this.classWriter = ClassWriter.instance(var1);
      this.constantWriter = ConstantWriter.instance(var1);
      this.sourceWriter = SourceWriter.instance(var1);
      this.tryBlockWriter = TryBlockWriter.instance(var1);
      this.stackMapWriter = StackMapWriter.instance(var1);
      this.localVariableTableWriter = LocalVariableTableWriter.instance(var1);
      this.localVariableTypeTableWriter = LocalVariableTypeTableWriter.instance(var1);
      this.typeAnnotationWriter = TypeAnnotationWriter.instance(var1);
      this.options = Options.instance(var1);
   }

   void write(Code_attribute var1, ConstantPool var2) {
      this.println("Code:");
      this.indent(1);
      this.writeVerboseHeader(var1, var2);
      this.writeInstrs(var1);
      this.writeExceptionTable(var1);
      this.attrWriter.write(var1, (Attributes)var1.attributes, var2);
      this.indent(-1);
   }

   public void writeVerboseHeader(Code_attribute var1, ConstantPool var2) {
      Method var3 = this.classWriter.getMethod();

      String var4;
      try {
         int var5 = var3.descriptor.getParameterCount(var2);
         if (!var3.access_flags.is(8)) {
            ++var5;
         }

         var4 = Integer.toString(var5);
      } catch (ConstantPoolException var6) {
         var4 = this.report(var6);
      } catch (DescriptorException var7) {
         var4 = this.report(var7);
      }

      this.println("stack=" + var1.max_stack + ", locals=" + var1.max_locals + ", args_size=" + var4);
   }

   public void writeInstrs(Code_attribute var1) {
      List var2 = this.getDetailWriters(var1);
      Iterator var3 = var1.getInstructions().iterator();

      while(var3.hasNext()) {
         Instruction var4 = (Instruction)var3.next();

         try {
            Iterator var5 = var2.iterator();

            while(var5.hasNext()) {
               InstructionDetailWriter var6 = (InstructionDetailWriter)var5.next();
               var6.writeDetails(var4);
            }

            this.writeInstr(var4);
         } catch (ArrayIndexOutOfBoundsException var7) {
            this.println(this.report("error at or after byte " + var4.getPC()));
            break;
         }
      }

      var3 = var2.iterator();

      while(var3.hasNext()) {
         InstructionDetailWriter var8 = (InstructionDetailWriter)var3.next();
         var8.flush();
      }

   }

   public void writeInstr(Instruction var1) {
      this.print(String.format("%4d: %-13s ", var1.getPC(), var1.getMnemonic()));
      int var2 = this.options.indentWidth;
      int var3 = (6 + var2 - 1) / var2;
      var1.accept(this.instructionPrinter, var3);
      this.println();
   }

   public void writeExceptionTable(Code_attribute var1) {
      if (var1.exception_table_length > 0) {
         this.println("Exception table:");
         this.indent(1);
         this.println(" from    to  target type");

         for(int var2 = 0; var2 < var1.exception_table.length; ++var2) {
            Code_attribute.Exception_data var3 = var1.exception_table[var2];
            this.print(String.format(" %5d %5d %5d", var3.start_pc, var3.end_pc, var3.handler_pc));
            this.print("   ");
            int var4 = var3.catch_type;
            if (var4 == 0) {
               this.println("any");
            } else {
               this.print("Class ");
               this.println(this.constantWriter.stringValue(var4));
            }
         }

         this.indent(-1);
      }

   }

   private void printConstant(int var1) {
      this.constantWriter.write(var1);
   }

   private List getDetailWriters(Code_attribute var1) {
      ArrayList var2 = new ArrayList();
      if (this.options.details.contains(InstructionDetailWriter.Kind.SOURCE)) {
         this.sourceWriter.reset(this.classWriter.getClassFile(), var1);
         if (this.sourceWriter.hasSource()) {
            var2.add(this.sourceWriter);
         } else {
            this.println("(Source code not available)");
         }
      }

      if (this.options.details.contains(InstructionDetailWriter.Kind.LOCAL_VARS)) {
         this.localVariableTableWriter.reset(var1);
         var2.add(this.localVariableTableWriter);
      }

      if (this.options.details.contains(InstructionDetailWriter.Kind.LOCAL_VAR_TYPES)) {
         this.localVariableTypeTableWriter.reset(var1);
         var2.add(this.localVariableTypeTableWriter);
      }

      if (this.options.details.contains(InstructionDetailWriter.Kind.STACKMAPS)) {
         this.stackMapWriter.reset(var1);
         this.stackMapWriter.writeInitialDetails();
         var2.add(this.stackMapWriter);
      }

      if (this.options.details.contains(InstructionDetailWriter.Kind.TRY_BLOCKS)) {
         this.tryBlockWriter.reset(var1);
         var2.add(this.tryBlockWriter);
      }

      if (this.options.details.contains(InstructionDetailWriter.Kind.TYPE_ANNOS)) {
         this.typeAnnotationWriter.reset(var1);
         var2.add(this.typeAnnotationWriter);
      }

      return var2;
   }
}
