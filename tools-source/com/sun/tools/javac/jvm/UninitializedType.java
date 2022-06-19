package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;

class UninitializedType extends Type.DelegatedType {
   public final int offset;

   public static UninitializedType uninitializedThis(Type var0) {
      return new UninitializedType(TypeTag.UNINITIALIZED_THIS, var0, -1);
   }

   public static UninitializedType uninitializedObject(Type var0, int var1) {
      return new UninitializedType(TypeTag.UNINITIALIZED_OBJECT, var0, var1);
   }

   private UninitializedType(TypeTag var1, Type var2, int var3) {
      super(var1, var2);
      this.offset = var3;
   }

   Type initializedType() {
      return this.qtype;
   }
}
