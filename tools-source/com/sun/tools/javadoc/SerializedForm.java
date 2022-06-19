package com.sun.tools.javadoc;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Tag;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

class SerializedForm {
   ListBuffer methods = new ListBuffer();
   private final ListBuffer fields = new ListBuffer();
   private boolean definesSerializableFields = false;
   private static final String SERIALIZABLE_FIELDS = "serialPersistentFields";
   private static final String READOBJECT = "readObject";
   private static final String WRITEOBJECT = "writeObject";
   private static final String READRESOLVE = "readResolve";
   private static final String WRITEREPLACE = "writeReplace";
   private static final String READOBJECTNODATA = "readObjectNoData";

   SerializedForm(DocEnv var1, Symbol.ClassSymbol var2, ClassDocImpl var3) {
      if (var3.isExternalizable()) {
         String[] var4 = new String[]{"java.io.ObjectInput"};
         String[] var5 = new String[]{"java.io.ObjectOutput"};
         MethodDocImpl var6 = var3.findMethod("readExternal", var4);
         if (var6 != null) {
            this.methods.append(var6);
         }

         var6 = var3.findMethod("writeExternal", var5);
         if (var6 != null) {
            this.methods.append(var6);
            Tag[] var7 = var6.tags("serialData");
         }
      } else if (var3.isSerializable()) {
         Symbol.VarSymbol var8 = this.getDefinedSerializableFields(var2);
         if (var8 != null) {
            this.definesSerializableFields = true;
            FieldDocImpl var9 = var1.getFieldDoc(var8);
            this.fields.append(var9);
            this.mapSerialFieldTagImplsToFieldDocImpls(var9, var1, var2);
         } else {
            this.computeDefaultSerializableFields(var1, var2, var3);
         }

         this.addMethodIfExist(var1, var2, "readObject");
         this.addMethodIfExist(var1, var2, "writeObject");
         this.addMethodIfExist(var1, var2, "readResolve");
         this.addMethodIfExist(var1, var2, "writeReplace");
         this.addMethodIfExist(var1, var2, "readObjectNoData");
      }

   }

   private Symbol.VarSymbol getDefinedSerializableFields(Symbol.ClassSymbol var1) {
      Names var2 = var1.name.table.names;

      for(Scope.Entry var3 = var1.members().lookup(var2.fromString("serialPersistentFields")); var3.scope != null; var3 = var3.next()) {
         if (var3.sym.kind == 4) {
            Symbol.VarSymbol var4 = (Symbol.VarSymbol)var3.sym;
            if ((var4.flags() & 8L) != 0L && (var4.flags() & 2L) != 0L) {
               return var4;
            }
         }
      }

      return null;
   }

   private void computeDefaultSerializableFields(DocEnv var1, Symbol.ClassSymbol var2, ClassDocImpl var3) {
      for(Scope.Entry var4 = var2.members().elems; var4 != null; var4 = var4.sibling) {
         if (var4.sym != null && var4.sym.kind == 4) {
            Symbol.VarSymbol var5 = (Symbol.VarSymbol)var4.sym;
            if ((var5.flags() & 8L) == 0L && (var5.flags() & 128L) == 0L) {
               FieldDocImpl var6 = var1.getFieldDoc(var5);
               this.fields.prepend(var6);
            }
         }
      }

   }

   private void addMethodIfExist(DocEnv var1, Symbol.ClassSymbol var2, String var3) {
      Names var4 = var2.name.table.names;

      for(Scope.Entry var5 = var2.members().lookup(var4.fromString(var3)); var5.scope != null; var5 = var5.next()) {
         if (var5.sym.kind == 16) {
            Symbol.MethodSymbol var6 = (Symbol.MethodSymbol)var5.sym;
            if ((var6.flags() & 8L) == 0L) {
               this.methods.append(var1.getMethodDoc(var6));
            }
         }
      }

   }

   private void mapSerialFieldTagImplsToFieldDocImpls(FieldDocImpl var1, DocEnv var2, Symbol.ClassSymbol var3) {
      Names var4 = var3.name.table.names;
      SerialFieldTag[] var5 = var1.serialFieldTags();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         if (var5[var6].fieldName() != null && var5[var6].fieldType() != null) {
            Name var7 = var4.fromString(var5[var6].fieldName());

            for(Scope.Entry var8 = var3.members().lookup(var7); var8.scope != null; var8 = var8.next()) {
               if (var8.sym.kind == 4) {
                  Symbol.VarSymbol var9 = (Symbol.VarSymbol)var8.sym;
                  FieldDocImpl var10 = var2.getFieldDoc(var9);
                  ((SerialFieldTagImpl)((SerialFieldTagImpl)var5[var6])).mapToFieldDocImpl(var10);
                  break;
               }
            }
         }
      }

   }

   FieldDoc[] fields() {
      return (FieldDoc[])((FieldDoc[])this.fields.toArray(new FieldDocImpl[this.fields.length()]));
   }

   MethodDoc[] methods() {
      return (MethodDoc[])this.methods.toArray(new MethodDoc[this.methods.length()]);
   }

   boolean definesSerializableFields() {
      return this.definesSerializableFields;
   }
}
