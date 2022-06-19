package com.sun.tools.classfile;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Code_attribute extends Attribute {
   public final int max_stack;
   public final int max_locals;
   public final int code_length;
   public final byte[] code;
   public final int exception_table_length;
   public final Exception_data[] exception_table;
   public final Attributes attributes;

   Code_attribute(ClassReader var1, int var2, int var3) throws IOException, ConstantPoolException {
      super(var2, var3);
      this.max_stack = var1.readUnsignedShort();
      this.max_locals = var1.readUnsignedShort();
      this.code_length = var1.readInt();
      this.code = new byte[this.code_length];
      var1.readFully(this.code);
      this.exception_table_length = var1.readUnsignedShort();
      this.exception_table = new Exception_data[this.exception_table_length];

      for(int var4 = 0; var4 < this.exception_table_length; ++var4) {
         this.exception_table[var4] = new Exception_data(var1);
      }

      this.attributes = new Attributes(var1);
   }

   public int getByte(int var1) throws InvalidIndex {
      if (var1 >= 0 && var1 < this.code.length) {
         return this.code[var1];
      } else {
         throw new InvalidIndex(var1);
      }
   }

   public int getUnsignedByte(int var1) throws InvalidIndex {
      if (var1 >= 0 && var1 < this.code.length) {
         return this.code[var1] & 255;
      } else {
         throw new InvalidIndex(var1);
      }
   }

   public int getShort(int var1) throws InvalidIndex {
      if (var1 >= 0 && var1 + 1 < this.code.length) {
         return this.code[var1] << 8 | this.code[var1 + 1] & 255;
      } else {
         throw new InvalidIndex(var1);
      }
   }

   public int getUnsignedShort(int var1) throws InvalidIndex {
      if (var1 >= 0 && var1 + 1 < this.code.length) {
         return (this.code[var1] << 8 | this.code[var1 + 1] & 255) & '\uffff';
      } else {
         throw new InvalidIndex(var1);
      }
   }

   public int getInt(int var1) throws InvalidIndex {
      if (var1 >= 0 && var1 + 3 < this.code.length) {
         return this.getShort(var1) << 16 | this.getShort(var1 + 2) & '\uffff';
      } else {
         throw new InvalidIndex(var1);
      }
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitCode(this, var2);
   }

   public Iterable getInstructions() {
      return new Iterable() {
         public Iterator iterator() {
            return new Iterator() {
               Instruction current = null;
               int pc = 0;
               Instruction next;

               {
                  this.next = new Instruction(Code_attribute.this.code, this.pc);
               }

               public boolean hasNext() {
                  return this.next != null;
               }

               public Instruction next() {
                  if (this.next == null) {
                     throw new NoSuchElementException();
                  } else {
                     this.current = this.next;
                     this.pc += this.current.length();
                     this.next = this.pc < Code_attribute.this.code.length ? new Instruction(Code_attribute.this.code, this.pc) : null;
                     return this.current;
                  }
               }

               public void remove() {
                  throw new UnsupportedOperationException("Not supported.");
               }
            };
         }
      };
   }

   public static class Exception_data {
      public final int start_pc;
      public final int end_pc;
      public final int handler_pc;
      public final int catch_type;

      Exception_data(ClassReader var1) throws IOException {
         this.start_pc = var1.readUnsignedShort();
         this.end_pc = var1.readUnsignedShort();
         this.handler_pc = var1.readUnsignedShort();
         this.catch_type = var1.readUnsignedShort();
      }
   }

   public static class InvalidIndex extends AttributeException {
      private static final long serialVersionUID = -8904527774589382802L;
      public final int index;

      InvalidIndex(int var1) {
         this.index = var1;
      }

      public String getMessage() {
         return "invalid index " + this.index + " in Code attribute";
      }
   }
}
