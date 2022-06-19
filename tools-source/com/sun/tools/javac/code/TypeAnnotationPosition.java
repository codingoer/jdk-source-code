package com.sun.tools.javac.code;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.util.Iterator;

public class TypeAnnotationPosition {
   public TargetType type;
   public List location;
   public int pos;
   public boolean isValidOffset;
   public int offset;
   public int[] lvarOffset;
   public int[] lvarLength;
   public int[] lvarIndex;
   public int bound_index;
   public int parameter_index;
   public int type_index;
   public int exception_index;
   public JCTree.JCLambda onLambda;

   public TypeAnnotationPosition() {
      this.type = TargetType.UNKNOWN;
      this.location = List.nil();
      this.pos = -1;
      this.isValidOffset = false;
      this.offset = -1;
      this.lvarOffset = null;
      this.lvarLength = null;
      this.lvarIndex = null;
      this.bound_index = Integer.MIN_VALUE;
      this.parameter_index = Integer.MIN_VALUE;
      this.type_index = Integer.MIN_VALUE;
      this.exception_index = Integer.MIN_VALUE;
      this.onLambda = null;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('[');
      var1.append(this.type);
      switch (this.type) {
         case INSTANCEOF:
         case NEW:
         case CONSTRUCTOR_REFERENCE:
         case METHOD_REFERENCE:
            var1.append(", offset = ");
            var1.append(this.offset);
            break;
         case LOCAL_VARIABLE:
         case RESOURCE_VARIABLE:
            if (this.lvarOffset == null) {
               var1.append(", lvarOffset is null!");
            } else {
               var1.append(", {");

               for(int var2 = 0; var2 < this.lvarOffset.length; ++var2) {
                  if (var2 != 0) {
                     var1.append("; ");
                  }

                  var1.append("start_pc = ");
                  var1.append(this.lvarOffset[var2]);
                  var1.append(", length = ");
                  var1.append(this.lvarLength[var2]);
                  var1.append(", index = ");
                  var1.append(this.lvarIndex[var2]);
               }

               var1.append("}");
            }
         case METHOD_RECEIVER:
         case METHOD_RETURN:
         case FIELD:
            break;
         case CLASS_TYPE_PARAMETER:
         case METHOD_TYPE_PARAMETER:
            var1.append(", param_index = ");
            var1.append(this.parameter_index);
            break;
         case CLASS_TYPE_PARAMETER_BOUND:
         case METHOD_TYPE_PARAMETER_BOUND:
            var1.append(", param_index = ");
            var1.append(this.parameter_index);
            var1.append(", bound_index = ");
            var1.append(this.bound_index);
            break;
         case CLASS_EXTENDS:
            var1.append(", type_index = ");
            var1.append(this.type_index);
            break;
         case THROWS:
            var1.append(", type_index = ");
            var1.append(this.type_index);
            break;
         case EXCEPTION_PARAMETER:
            var1.append(", exception_index = ");
            var1.append(this.exception_index);
            break;
         case METHOD_FORMAL_PARAMETER:
            var1.append(", param_index = ");
            var1.append(this.parameter_index);
            break;
         case CAST:
         case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
         case METHOD_INVOCATION_TYPE_ARGUMENT:
         case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
         case METHOD_REFERENCE_TYPE_ARGUMENT:
            var1.append(", offset = ");
            var1.append(this.offset);
            var1.append(", type_index = ");
            var1.append(this.type_index);
            break;
         case UNKNOWN:
            var1.append(", position UNKNOWN!");
            break;
         default:
            Assert.error("Unknown target type: " + this.type);
      }

      if (!this.location.isEmpty()) {
         var1.append(", location = (");
         var1.append(this.location);
         var1.append(")");
      }

      var1.append(", pos = ");
      var1.append(this.pos);
      if (this.onLambda != null) {
         var1.append(", onLambda hash = ");
         var1.append(this.onLambda.hashCode());
      }

      var1.append(']');
      return var1.toString();
   }

   public boolean emitToClassfile() {
      return !this.type.isLocal() || this.isValidOffset;
   }

   public boolean matchesPos(int var1) {
      return this.pos == var1;
   }

   public void updatePosOffset(int var1) {
      this.offset = var1;
      this.lvarOffset = new int[]{var1};
      this.isValidOffset = true;
   }

   public static List getTypePathFromBinary(java.util.List var0) {
      ListBuffer var1 = new ListBuffer();

      Integer var3;
      Integer var4;
      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = var1.append(TypeAnnotationPosition.TypePathEntry.fromBinary(var3, var4))) {
         var3 = (Integer)var2.next();
         Assert.check(var2.hasNext(), "Could not decode type path: " + var0);
         var4 = (Integer)var2.next();
      }

      return var1.toList();
   }

   public static List getBinaryFromTypePath(java.util.List var0) {
      ListBuffer var1 = new ListBuffer();

      TypePathEntry var3;
      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = var1.append(var3.arg)) {
         var3 = (TypePathEntry)var2.next();
         var1 = var1.append(var3.tag.tag);
      }

      return var1.toList();
   }

   public static class TypePathEntry {
      public static final int bytesPerEntry = 2;
      public final TypePathEntryKind tag;
      public final int arg;
      public static final TypePathEntry ARRAY;
      public static final TypePathEntry INNER_TYPE;
      public static final TypePathEntry WILDCARD;

      private TypePathEntry(TypePathEntryKind var1) {
         Assert.check(var1 == TypeAnnotationPosition.TypePathEntryKind.ARRAY || var1 == TypeAnnotationPosition.TypePathEntryKind.INNER_TYPE || var1 == TypeAnnotationPosition.TypePathEntryKind.WILDCARD, "Invalid TypePathEntryKind: " + var1);
         this.tag = var1;
         this.arg = 0;
      }

      public TypePathEntry(TypePathEntryKind var1, int var2) {
         Assert.check(var1 == TypeAnnotationPosition.TypePathEntryKind.TYPE_ARGUMENT, "Invalid TypePathEntryKind: " + var1);
         this.tag = var1;
         this.arg = var2;
      }

      public static TypePathEntry fromBinary(int var0, int var1) {
         Assert.check(var1 == 0 || var0 == TypeAnnotationPosition.TypePathEntryKind.TYPE_ARGUMENT.tag, "Invalid TypePathEntry tag/arg: " + var0 + "/" + var1);
         switch (var0) {
            case 0:
               return ARRAY;
            case 1:
               return INNER_TYPE;
            case 2:
               return WILDCARD;
            case 3:
               return new TypePathEntry(TypeAnnotationPosition.TypePathEntryKind.TYPE_ARGUMENT, var1);
            default:
               Assert.error("Invalid TypePathEntryKind tag: " + var0);
               return null;
         }
      }

      public String toString() {
         return this.tag.toString() + (this.tag == TypeAnnotationPosition.TypePathEntryKind.TYPE_ARGUMENT ? "(" + this.arg + ")" : "");
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof TypePathEntry)) {
            return false;
         } else {
            TypePathEntry var2 = (TypePathEntry)var1;
            return this.tag == var2.tag && this.arg == var2.arg;
         }
      }

      public int hashCode() {
         return this.tag.hashCode() * 17 + this.arg;
      }

      static {
         ARRAY = new TypePathEntry(TypeAnnotationPosition.TypePathEntryKind.ARRAY);
         INNER_TYPE = new TypePathEntry(TypeAnnotationPosition.TypePathEntryKind.INNER_TYPE);
         WILDCARD = new TypePathEntry(TypeAnnotationPosition.TypePathEntryKind.WILDCARD);
      }
   }

   public static enum TypePathEntryKind {
      ARRAY(0),
      INNER_TYPE(1),
      WILDCARD(2),
      TYPE_ARGUMENT(3);

      public final int tag;

      private TypePathEntryKind(int var3) {
         this.tag = var3;
      }
   }
}
