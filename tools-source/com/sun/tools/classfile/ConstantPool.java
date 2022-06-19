package com.sun.tools.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class ConstantPool {
   public static final int CONSTANT_Utf8 = 1;
   public static final int CONSTANT_Integer = 3;
   public static final int CONSTANT_Float = 4;
   public static final int CONSTANT_Long = 5;
   public static final int CONSTANT_Double = 6;
   public static final int CONSTANT_Class = 7;
   public static final int CONSTANT_String = 8;
   public static final int CONSTANT_Fieldref = 9;
   public static final int CONSTANT_Methodref = 10;
   public static final int CONSTANT_InterfaceMethodref = 11;
   public static final int CONSTANT_NameAndType = 12;
   public static final int CONSTANT_MethodHandle = 15;
   public static final int CONSTANT_MethodType = 16;
   public static final int CONSTANT_InvokeDynamic = 18;
   private CPInfo[] pool;

   ConstantPool(ClassReader var1) throws IOException, InvalidEntry {
      int var2 = var1.readUnsignedShort();
      this.pool = new CPInfo[var2];

      for(int var3 = 1; var3 < var2; ++var3) {
         int var4 = var1.readUnsignedByte();
         switch (var4) {
            case 1:
               this.pool[var3] = new CONSTANT_Utf8_info(var1);
               break;
            case 2:
            case 13:
            case 14:
            case 17:
            default:
               throw new InvalidEntry(var3, var4);
            case 3:
               this.pool[var3] = new CONSTANT_Integer_info(var1);
               break;
            case 4:
               this.pool[var3] = new CONSTANT_Float_info(var1);
               break;
            case 5:
               this.pool[var3] = new CONSTANT_Long_info(var1);
               ++var3;
               break;
            case 6:
               this.pool[var3] = new CONSTANT_Double_info(var1);
               ++var3;
               break;
            case 7:
               this.pool[var3] = new CONSTANT_Class_info(this, var1);
               break;
            case 8:
               this.pool[var3] = new CONSTANT_String_info(this, var1);
               break;
            case 9:
               this.pool[var3] = new CONSTANT_Fieldref_info(this, var1);
               break;
            case 10:
               this.pool[var3] = new CONSTANT_Methodref_info(this, var1);
               break;
            case 11:
               this.pool[var3] = new CONSTANT_InterfaceMethodref_info(this, var1);
               break;
            case 12:
               this.pool[var3] = new CONSTANT_NameAndType_info(this, var1);
               break;
            case 15:
               this.pool[var3] = new CONSTANT_MethodHandle_info(this, var1);
               break;
            case 16:
               this.pool[var3] = new CONSTANT_MethodType_info(this, var1);
               break;
            case 18:
               this.pool[var3] = new CONSTANT_InvokeDynamic_info(this, var1);
         }
      }

   }

   public ConstantPool(CPInfo[] var1) {
      this.pool = var1;
   }

   public int size() {
      return this.pool.length;
   }

   public int byteLength() {
      int var1 = 2;

      CPInfo var3;
      for(int var2 = 1; var2 < this.size(); var2 += var3.size()) {
         var3 = this.pool[var2];
         var1 += var3.byteLength();
      }

      return var1;
   }

   public CPInfo get(int var1) throws InvalidIndex {
      if (var1 > 0 && var1 < this.pool.length) {
         CPInfo var2 = this.pool[var1];
         if (var2 == null) {
            throw new InvalidIndex(var1);
         } else {
            return this.pool[var1];
         }
      } else {
         throw new InvalidIndex(var1);
      }
   }

   private CPInfo get(int var1, int var2) throws InvalidIndex, UnexpectedEntry {
      CPInfo var3 = this.get(var1);
      if (var3.getTag() != var2) {
         throw new UnexpectedEntry(var1, var2, var3.getTag());
      } else {
         return var3;
      }
   }

   public CONSTANT_Utf8_info getUTF8Info(int var1) throws InvalidIndex, UnexpectedEntry {
      return (CONSTANT_Utf8_info)this.get(var1, 1);
   }

   public CONSTANT_Class_info getClassInfo(int var1) throws InvalidIndex, UnexpectedEntry {
      return (CONSTANT_Class_info)this.get(var1, 7);
   }

   public CONSTANT_NameAndType_info getNameAndTypeInfo(int var1) throws InvalidIndex, UnexpectedEntry {
      return (CONSTANT_NameAndType_info)this.get(var1, 12);
   }

   public String getUTF8Value(int var1) throws InvalidIndex, UnexpectedEntry {
      return this.getUTF8Info(var1).value;
   }

   public int getUTF8Index(String var1) throws EntryNotFound {
      for(int var2 = 1; var2 < this.pool.length; ++var2) {
         CPInfo var3 = this.pool[var2];
         if (var3 instanceof CONSTANT_Utf8_info && ((CONSTANT_Utf8_info)var3).value.equals(var1)) {
            return var2;
         }
      }

      throw new EntryNotFound(var1);
   }

   public Iterable entries() {
      return new Iterable() {
         public Iterator iterator() {
            return new Iterator() {
               private CPInfo current;
               private int next = 1;

               public boolean hasNext() {
                  return this.next < ConstantPool.this.pool.length;
               }

               public CPInfo next() {
                  this.current = ConstantPool.this.pool[this.next];
                  switch (this.current.getTag()) {
                     case 5:
                     case 6:
                        this.next += 2;
                        break;
                     default:
                        ++this.next;
                  }

                  return this.current;
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }

   public static class CONSTANT_Utf8_info extends CPInfo {
      public final String value;

      CONSTANT_Utf8_info(ClassReader var1) throws IOException {
         this.value = var1.readUTF();
      }

      public CONSTANT_Utf8_info(String var1) {
         this.value = var1;
      }

      public int getTag() {
         return 1;
      }

      public int byteLength() {
         class SizeOutputStream extends OutputStream {
            int size;

            public void write(int var1) {
               ++this.size;
            }
         }

         SizeOutputStream var1 = new SizeOutputStream();
         DataOutputStream var2 = new DataOutputStream(var1);

         try {
            var2.writeUTF(this.value);
         } catch (IOException var4) {
         }

         return 1 + var1.size;
      }

      public String toString() {
         return this.value.length() < 32 && isPrintableAscii(this.value) ? "CONSTANT_Utf8_info[value: \"" + this.value + "\"]" : "CONSTANT_Utf8_info[value: (" + this.value.length() + " chars)]";
      }

      static boolean isPrintableAscii(String var0) {
         for(int var1 = 0; var1 < var0.length(); ++var1) {
            char var2 = var0.charAt(var1);
            if (var2 < ' ' || var2 >= 127) {
               return false;
            }
         }

         return true;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitUtf8(this, var2);
      }
   }

   public static class CONSTANT_String_info extends CPInfo {
      public final int string_index;

      CONSTANT_String_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1);
         this.string_index = var2.readUnsignedShort();
      }

      public CONSTANT_String_info(ConstantPool var1, int var2) {
         super(var1);
         this.string_index = var2;
      }

      public int getTag() {
         return 8;
      }

      public int byteLength() {
         return 3;
      }

      public String getString() throws ConstantPoolException {
         return this.cp.getUTF8Value(this.string_index);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitString(this, var2);
      }

      public String toString() {
         return "CONSTANT_String_info[class_index: " + this.string_index + "]";
      }
   }

   public static class CONSTANT_NameAndType_info extends CPInfo {
      public final int name_index;
      public final int type_index;

      CONSTANT_NameAndType_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1);
         this.name_index = var2.readUnsignedShort();
         this.type_index = var2.readUnsignedShort();
      }

      public CONSTANT_NameAndType_info(ConstantPool var1, int var2, int var3) {
         super(var1);
         this.name_index = var2;
         this.type_index = var3;
      }

      public int getTag() {
         return 12;
      }

      public int byteLength() {
         return 5;
      }

      public String getName() throws ConstantPoolException {
         return this.cp.getUTF8Value(this.name_index);
      }

      public String getType() throws ConstantPoolException {
         return this.cp.getUTF8Value(this.type_index);
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitNameAndType(this, var2);
      }

      public String toString() {
         return "CONSTANT_NameAndType_info[name_index: " + this.name_index + ", type_index: " + this.type_index + "]";
      }
   }

   public static class CONSTANT_Methodref_info extends CPRefInfo {
      CONSTANT_Methodref_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1, var2, 10);
      }

      public CONSTANT_Methodref_info(ConstantPool var1, int var2, int var3) {
         super(var1, 10, var2, var3);
      }

      public String toString() {
         return "CONSTANT_Methodref_info[class_index: " + this.class_index + ", name_and_type_index: " + this.name_and_type_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitMethodref(this, var2);
      }
   }

   public static class CONSTANT_MethodType_info extends CPInfo {
      public final int descriptor_index;

      CONSTANT_MethodType_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1);
         this.descriptor_index = var2.readUnsignedShort();
      }

      public CONSTANT_MethodType_info(ConstantPool var1, int var2) {
         super(var1);
         this.descriptor_index = var2;
      }

      public int getTag() {
         return 16;
      }

      public int byteLength() {
         return 3;
      }

      public String toString() {
         return "CONSTANT_MethodType_info[signature_index: " + this.descriptor_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitMethodType(this, var2);
      }

      public String getType() throws ConstantPoolException {
         return this.cp.getUTF8Value(this.descriptor_index);
      }
   }

   public static class CONSTANT_MethodHandle_info extends CPInfo {
      public final RefKind reference_kind;
      public final int reference_index;

      CONSTANT_MethodHandle_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1);
         this.reference_kind = ConstantPool.RefKind.getRefkind(var2.readUnsignedByte());
         this.reference_index = var2.readUnsignedShort();
      }

      public CONSTANT_MethodHandle_info(ConstantPool var1, RefKind var2, int var3) {
         super(var1);
         this.reference_kind = var2;
         this.reference_index = var3;
      }

      public int getTag() {
         return 15;
      }

      public int byteLength() {
         return 4;
      }

      public String toString() {
         return "CONSTANT_MethodHandle_info[ref_kind: " + this.reference_kind + ", member_index: " + this.reference_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitMethodHandle(this, var2);
      }

      public CPRefInfo getCPRefInfo() throws ConstantPoolException {
         int var1 = 10;
         int var2 = this.cp.get(this.reference_index).getTag();
         switch (var2) {
            case 9:
            case 11:
               var1 = var2;
            default:
               return (CPRefInfo)this.cp.get(this.reference_index, var1);
         }
      }
   }

   public static class CONSTANT_Long_info extends CPInfo {
      public final long value;

      CONSTANT_Long_info(ClassReader var1) throws IOException {
         this.value = var1.readLong();
      }

      public CONSTANT_Long_info(long var1) {
         this.value = var1;
      }

      public int getTag() {
         return 5;
      }

      public int size() {
         return 2;
      }

      public int byteLength() {
         return 9;
      }

      public String toString() {
         return "CONSTANT_Long_info[value: " + this.value + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitLong(this, var2);
      }
   }

   public static class CONSTANT_InvokeDynamic_info extends CPInfo {
      public final int bootstrap_method_attr_index;
      public final int name_and_type_index;

      CONSTANT_InvokeDynamic_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1);
         this.bootstrap_method_attr_index = var2.readUnsignedShort();
         this.name_and_type_index = var2.readUnsignedShort();
      }

      public CONSTANT_InvokeDynamic_info(ConstantPool var1, int var2, int var3) {
         super(var1);
         this.bootstrap_method_attr_index = var2;
         this.name_and_type_index = var3;
      }

      public int getTag() {
         return 18;
      }

      public int byteLength() {
         return 5;
      }

      public String toString() {
         return "CONSTANT_InvokeDynamic_info[bootstrap_method_index: " + this.bootstrap_method_attr_index + ", name_and_type_index: " + this.name_and_type_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitInvokeDynamic(this, var2);
      }

      public CONSTANT_NameAndType_info getNameAndTypeInfo() throws ConstantPoolException {
         return this.cp.getNameAndTypeInfo(this.name_and_type_index);
      }
   }

   public static class CONSTANT_InterfaceMethodref_info extends CPRefInfo {
      CONSTANT_InterfaceMethodref_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1, var2, 11);
      }

      public CONSTANT_InterfaceMethodref_info(ConstantPool var1, int var2, int var3) {
         super(var1, 11, var2, var3);
      }

      public String toString() {
         return "CONSTANT_InterfaceMethodref_info[class_index: " + this.class_index + ", name_and_type_index: " + this.name_and_type_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitInterfaceMethodref(this, var2);
      }
   }

   public static class CONSTANT_Integer_info extends CPInfo {
      public final int value;

      CONSTANT_Integer_info(ClassReader var1) throws IOException {
         this.value = var1.readInt();
      }

      public CONSTANT_Integer_info(int var1) {
         this.value = var1;
      }

      public int getTag() {
         return 3;
      }

      public int byteLength() {
         return 5;
      }

      public String toString() {
         return "CONSTANT_Integer_info[value: " + this.value + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitInteger(this, var2);
      }
   }

   public static class CONSTANT_Float_info extends CPInfo {
      public final float value;

      CONSTANT_Float_info(ClassReader var1) throws IOException {
         this.value = var1.readFloat();
      }

      public CONSTANT_Float_info(float var1) {
         this.value = var1;
      }

      public int getTag() {
         return 4;
      }

      public int byteLength() {
         return 5;
      }

      public String toString() {
         return "CONSTANT_Float_info[value: " + this.value + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitFloat(this, var2);
      }
   }

   public static class CONSTANT_Fieldref_info extends CPRefInfo {
      CONSTANT_Fieldref_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1, var2, 9);
      }

      public CONSTANT_Fieldref_info(ConstantPool var1, int var2, int var3) {
         super(var1, 9, var2, var3);
      }

      public String toString() {
         return "CONSTANT_Fieldref_info[class_index: " + this.class_index + ", name_and_type_index: " + this.name_and_type_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitFieldref(this, var2);
      }
   }

   public static class CONSTANT_Double_info extends CPInfo {
      public final double value;

      CONSTANT_Double_info(ClassReader var1) throws IOException {
         this.value = var1.readDouble();
      }

      public CONSTANT_Double_info(double var1) {
         this.value = var1;
      }

      public int getTag() {
         return 6;
      }

      public int byteLength() {
         return 9;
      }

      public int size() {
         return 2;
      }

      public String toString() {
         return "CONSTANT_Double_info[value: " + this.value + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitDouble(this, var2);
      }
   }

   public static class CONSTANT_Class_info extends CPInfo {
      public final int name_index;

      CONSTANT_Class_info(ConstantPool var1, ClassReader var2) throws IOException {
         super(var1);
         this.name_index = var2.readUnsignedShort();
      }

      public CONSTANT_Class_info(ConstantPool var1, int var2) {
         super(var1);
         this.name_index = var2;
      }

      public int getTag() {
         return 7;
      }

      public int byteLength() {
         return 3;
      }

      public String getName() throws ConstantPoolException {
         return this.cp.getUTF8Value(this.name_index);
      }

      public String getBaseName() throws ConstantPoolException {
         String var1 = this.getName();
         if (var1.startsWith("[")) {
            int var2 = var1.indexOf("[L");
            return var2 == -1 ? null : var1.substring(var2 + 2, var1.length() - 1);
         } else {
            return var1;
         }
      }

      public int getDimensionCount() throws ConstantPoolException {
         String var1 = this.getName();

         int var2;
         for(var2 = 0; var1.charAt(var2) == '['; ++var2) {
         }

         return var2;
      }

      public String toString() {
         return "CONSTANT_Class_info[name_index: " + this.name_index + "]";
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitClass(this, var2);
      }
   }

   public abstract static class CPRefInfo extends CPInfo {
      public final int tag;
      public final int class_index;
      public final int name_and_type_index;

      protected CPRefInfo(ConstantPool var1, ClassReader var2, int var3) throws IOException {
         super(var1);
         this.tag = var3;
         this.class_index = var2.readUnsignedShort();
         this.name_and_type_index = var2.readUnsignedShort();
      }

      protected CPRefInfo(ConstantPool var1, int var2, int var3, int var4) {
         super(var1);
         this.tag = var2;
         this.class_index = var3;
         this.name_and_type_index = var4;
      }

      public int getTag() {
         return this.tag;
      }

      public int byteLength() {
         return 5;
      }

      public CONSTANT_Class_info getClassInfo() throws ConstantPoolException {
         return this.cp.getClassInfo(this.class_index);
      }

      public String getClassName() throws ConstantPoolException {
         return this.cp.getClassInfo(this.class_index).getName();
      }

      public CONSTANT_NameAndType_info getNameAndTypeInfo() throws ConstantPoolException {
         return this.cp.getNameAndTypeInfo(this.name_and_type_index);
      }
   }

   public abstract static class CPInfo {
      protected final ConstantPool cp;

      CPInfo() {
         this.cp = null;
      }

      CPInfo(ConstantPool var1) {
         this.cp = var1;
      }

      public abstract int getTag();

      public int size() {
         return 1;
      }

      public abstract int byteLength();

      public abstract Object accept(Visitor var1, Object var2);
   }

   public interface Visitor {
      Object visitClass(CONSTANT_Class_info var1, Object var2);

      Object visitDouble(CONSTANT_Double_info var1, Object var2);

      Object visitFieldref(CONSTANT_Fieldref_info var1, Object var2);

      Object visitFloat(CONSTANT_Float_info var1, Object var2);

      Object visitInteger(CONSTANT_Integer_info var1, Object var2);

      Object visitInterfaceMethodref(CONSTANT_InterfaceMethodref_info var1, Object var2);

      Object visitInvokeDynamic(CONSTANT_InvokeDynamic_info var1, Object var2);

      Object visitLong(CONSTANT_Long_info var1, Object var2);

      Object visitNameAndType(CONSTANT_NameAndType_info var1, Object var2);

      Object visitMethodref(CONSTANT_Methodref_info var1, Object var2);

      Object visitMethodHandle(CONSTANT_MethodHandle_info var1, Object var2);

      Object visitMethodType(CONSTANT_MethodType_info var1, Object var2);

      Object visitString(CONSTANT_String_info var1, Object var2);

      Object visitUtf8(CONSTANT_Utf8_info var1, Object var2);
   }

   public static enum RefKind {
      REF_getField(1, "getfield"),
      REF_getStatic(2, "getstatic"),
      REF_putField(3, "putfield"),
      REF_putStatic(4, "putstatic"),
      REF_invokeVirtual(5, "invokevirtual"),
      REF_invokeStatic(6, "invokestatic"),
      REF_invokeSpecial(7, "invokespecial"),
      REF_newInvokeSpecial(8, "newinvokespecial"),
      REF_invokeInterface(9, "invokeinterface");

      public final int tag;
      public final String name;

      private RefKind(int var3, String var4) {
         this.tag = var3;
         this.name = var4;
      }

      static RefKind getRefkind(int var0) {
         switch (var0) {
            case 1:
               return REF_getField;
            case 2:
               return REF_getStatic;
            case 3:
               return REF_putField;
            case 4:
               return REF_putStatic;
            case 5:
               return REF_invokeVirtual;
            case 6:
               return REF_invokeStatic;
            case 7:
               return REF_invokeSpecial;
            case 8:
               return REF_newInvokeSpecial;
            case 9:
               return REF_invokeInterface;
            default:
               return null;
         }
      }
   }

   public static class EntryNotFound extends ConstantPoolException {
      private static final long serialVersionUID = 2885537606468581850L;
      public final Object value;

      EntryNotFound(Object var1) {
         super(-1);
         this.value = var1;
      }

      public String getMessage() {
         return "value not found: " + this.value;
      }
   }

   public static class InvalidEntry extends ConstantPoolException {
      private static final long serialVersionUID = 1000087545585204447L;
      public final int tag;

      InvalidEntry(int var1, int var2) {
         super(var1);
         this.tag = var2;
      }

      public String getMessage() {
         return "unexpected tag at #" + this.index + ": " + this.tag;
      }
   }

   public static class UnexpectedEntry extends ConstantPoolException {
      private static final long serialVersionUID = 6986335935377933211L;
      public final int expected_tag;
      public final int found_tag;

      UnexpectedEntry(int var1, int var2, int var3) {
         super(var1);
         this.expected_tag = var2;
         this.found_tag = var3;
      }

      public String getMessage() {
         return "unexpected entry at #" + this.index + " -- expected tag " + this.expected_tag + ", found " + this.found_tag;
      }
   }

   public static class InvalidIndex extends ConstantPoolException {
      private static final long serialVersionUID = -4350294289300939730L;

      InvalidIndex(int var1) {
         super(var1);
      }

      public String getMessage() {
         return "invalid index #" + this.index;
      }
   }
}
