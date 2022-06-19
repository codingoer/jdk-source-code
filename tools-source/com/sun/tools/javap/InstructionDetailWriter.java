package com.sun.tools.javap;

import com.sun.tools.classfile.Instruction;

public abstract class InstructionDetailWriter extends BasicWriter {
   InstructionDetailWriter(Context var1) {
      super(var1);
   }

   abstract void writeDetails(Instruction var1);

   void flush() {
   }

   public static enum Kind {
      LOCAL_VARS("localVariables"),
      LOCAL_VAR_TYPES("localVariableTypes"),
      SOURCE("source"),
      STACKMAPS("stackMaps"),
      TRY_BLOCKS("tryBlocks"),
      TYPE_ANNOS("typeAnnotations");

      final String option;

      private Kind(String var3) {
         this.option = var3;
      }
   }
}
