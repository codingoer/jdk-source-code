package com.sun.tools.classfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypeAnnotation {
   public final ConstantPool constant_pool;
   public final Position position;
   public final Annotation annotation;

   TypeAnnotation(ClassReader var1) throws IOException, Annotation.InvalidAnnotation {
      this.constant_pool = var1.getConstantPool();
      this.position = read_position(var1);
      this.annotation = new Annotation(var1);
   }

   public TypeAnnotation(ConstantPool var1, Annotation var2, Position var3) {
      this.constant_pool = var1;
      this.position = var3;
      this.annotation = var2;
   }

   public int length() {
      int var1 = this.annotation.length();
      var1 += position_length(this.position);
      return var1;
   }

   public String toString() {
      try {
         return "@" + this.constant_pool.getUTF8Value(this.annotation.type_index).toString().substring(1) + " pos: " + this.position.toString();
      } catch (Exception var2) {
         var2.printStackTrace();
         return var2.toString();
      }
   }

   private static Position read_position(ClassReader var0) throws IOException, Annotation.InvalidAnnotation {
      int var1 = var0.readUnsignedByte();
      if (!TypeAnnotation.TargetType.isValidTargetTypeValue(var1)) {
         throw new Annotation.InvalidAnnotation("TypeAnnotation: Invalid type annotation target type value: " + String.format("0x%02X", var1));
      } else {
         Position var3;
         int var4;
         TargetType var2 = TypeAnnotation.TargetType.fromTargetTypeValue(var1);
         var3 = new Position();
         var3.type = var2;
         int var5;
         label43:
         switch (var2) {
            case INSTANCEOF:
            case NEW:
            case CONSTRUCTOR_REFERENCE:
            case METHOD_REFERENCE:
               var3.offset = var0.readUnsignedShort();
               break;
            case LOCAL_VARIABLE:
            case RESOURCE_VARIABLE:
               var4 = var0.readUnsignedShort();
               var3.lvarOffset = new int[var4];
               var3.lvarLength = new int[var4];
               var3.lvarIndex = new int[var4];
               var5 = 0;

               while(true) {
                  if (var5 >= var4) {
                     break label43;
                  }

                  var3.lvarOffset[var5] = var0.readUnsignedShort();
                  var3.lvarLength[var5] = var0.readUnsignedShort();
                  var3.lvarIndex[var5] = var0.readUnsignedShort();
                  ++var5;
               }
            case EXCEPTION_PARAMETER:
               var3.exception_index = var0.readUnsignedShort();
            case METHOD_RECEIVER:
            case METHOD_RETURN:
            case FIELD:
               break;
            case CLASS_TYPE_PARAMETER:
            case METHOD_TYPE_PARAMETER:
               var3.parameter_index = var0.readUnsignedByte();
               break;
            case CLASS_TYPE_PARAMETER_BOUND:
            case METHOD_TYPE_PARAMETER_BOUND:
               var3.parameter_index = var0.readUnsignedByte();
               var3.bound_index = var0.readUnsignedByte();
               break;
            case CLASS_EXTENDS:
               var5 = var0.readUnsignedShort();
               if (var5 == 65535) {
                  var5 = -1;
               }

               var3.type_index = var5;
               break;
            case THROWS:
               var3.type_index = var0.readUnsignedShort();
               break;
            case METHOD_FORMAL_PARAMETER:
               var3.parameter_index = var0.readUnsignedByte();
               break;
            case CAST:
            case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
            case METHOD_INVOCATION_TYPE_ARGUMENT:
            case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
            case METHOD_REFERENCE_TYPE_ARGUMENT:
               var3.offset = var0.readUnsignedShort();
               var3.type_index = var0.readUnsignedByte();
               break;
            case UNKNOWN:
               throw new AssertionError("TypeAnnotation: UNKNOWN target type should never occur!");
            default:
               throw new AssertionError("TypeAnnotation: Unknown target type: " + var2);
         }

         var4 = var0.readUnsignedByte();
         ArrayList var7 = new ArrayList(var4);

         for(int var6 = 0; var6 < var4 * 2; ++var6) {
            var7.add(var0.readUnsignedByte());
         }

         var3.location = TypeAnnotation.Position.getTypePathFromBinary(var7);
         return var3;
      }
   }

   private static int position_length(Position var0) {
      int var1 = 0;
      ++var1;
      switch (var0.type) {
         case INSTANCEOF:
         case NEW:
         case CONSTRUCTOR_REFERENCE:
         case METHOD_REFERENCE:
            var1 += 2;
            break;
         case LOCAL_VARIABLE:
         case RESOURCE_VARIABLE:
            var1 += 2;
            int var2 = var0.lvarOffset.length;
            var1 += 2 * var2;
            var1 += 2 * var2;
            var1 += 2 * var2;
            break;
         case EXCEPTION_PARAMETER:
            var1 += 2;
         case METHOD_RECEIVER:
         case METHOD_RETURN:
         case FIELD:
            break;
         case CLASS_TYPE_PARAMETER:
         case METHOD_TYPE_PARAMETER:
            ++var1;
            break;
         case CLASS_TYPE_PARAMETER_BOUND:
         case METHOD_TYPE_PARAMETER_BOUND:
            ++var1;
            ++var1;
            break;
         case CLASS_EXTENDS:
            var1 += 2;
            break;
         case THROWS:
            var1 += 2;
            break;
         case METHOD_FORMAL_PARAMETER:
            ++var1;
            break;
         case CAST:
         case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
         case METHOD_INVOCATION_TYPE_ARGUMENT:
         case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
         case METHOD_REFERENCE_TYPE_ARGUMENT:
            var1 += 2;
            ++var1;
            break;
         case UNKNOWN:
            throw new AssertionError("TypeAnnotation: UNKNOWN target type should never occur!");
         default:
            throw new AssertionError("TypeAnnotation: Unknown target type: " + var0.type);
      }

      ++var1;
      var1 += 2 * var0.location.size();
      return var1;
   }

   public static enum TargetType {
      CLASS_TYPE_PARAMETER(0),
      METHOD_TYPE_PARAMETER(1),
      CLASS_EXTENDS(16),
      CLASS_TYPE_PARAMETER_BOUND(17),
      METHOD_TYPE_PARAMETER_BOUND(18),
      FIELD(19),
      METHOD_RETURN(20),
      METHOD_RECEIVER(21),
      METHOD_FORMAL_PARAMETER(22),
      THROWS(23),
      LOCAL_VARIABLE(64, true),
      RESOURCE_VARIABLE(65, true),
      EXCEPTION_PARAMETER(66, true),
      INSTANCEOF(67, true),
      NEW(68, true),
      CONSTRUCTOR_REFERENCE(69, true),
      METHOD_REFERENCE(70, true),
      CAST(71, true),
      CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT(72, true),
      METHOD_INVOCATION_TYPE_ARGUMENT(73, true),
      CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT(74, true),
      METHOD_REFERENCE_TYPE_ARGUMENT(75, true),
      UNKNOWN(255);

      private static final int MAXIMUM_TARGET_TYPE_VALUE = 75;
      private final int targetTypeValue;
      private final boolean isLocal;
      private static final TargetType[] targets = new TargetType[76];

      private TargetType(int var3) {
         this(var3, false);
      }

      private TargetType(int var3, boolean var4) {
         if (var3 >= 0 && var3 <= 255) {
            this.targetTypeValue = var3;
            this.isLocal = var4;
         } else {
            throw new AssertionError("Attribute type value needs to be an unsigned byte: " + String.format("0x%02X", var3));
         }
      }

      public boolean isLocal() {
         return this.isLocal;
      }

      public int targetTypeValue() {
         return this.targetTypeValue;
      }

      public static boolean isValidTargetTypeValue(int var0) {
         if (var0 == UNKNOWN.targetTypeValue) {
            return true;
         } else {
            return var0 >= 0 && var0 < targets.length;
         }
      }

      public static TargetType fromTargetTypeValue(int var0) {
         if (var0 == UNKNOWN.targetTypeValue) {
            return UNKNOWN;
         } else if (var0 >= 0 && var0 < targets.length) {
            return targets[var0];
         } else {
            throw new AssertionError("Unknown TargetType: " + var0);
         }
      }

      static {
         TargetType[] var0 = values();
         TargetType[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            TargetType var4 = var1[var3];
            if (var4.targetTypeValue != UNKNOWN.targetTypeValue) {
               targets[var4.targetTypeValue] = var4;
            }
         }

         for(int var5 = 0; var5 <= 75; ++var5) {
            if (targets[var5] == null) {
               targets[var5] = UNKNOWN;
            }
         }

      }
   }

   public static class Position {
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

      public Position() {
         this.type = TypeAnnotation.TargetType.UNKNOWN;
         this.location = new ArrayList(0);
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
               break;
            case EXCEPTION_PARAMETER:
               var1.append(", exception_index = ");
               var1.append(this.exception_index);
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
               throw new AssertionError("Unknown target type: " + this.type);
         }

         if (!this.location.isEmpty()) {
            var1.append(", location = (");
            var1.append(this.location);
            var1.append(")");
         }

         var1.append(", pos = ");
         var1.append(this.pos);
         var1.append(']');
         return var1.toString();
      }

      public boolean emitToClassfile() {
         return !this.type.isLocal() || this.isValidOffset;
      }

      public static List getTypePathFromBinary(List var0) {
         ArrayList var1 = new ArrayList(var0.size() / 2);

         for(int var2 = 0; var2 < var0.size(); var2 += 2) {
            if (var2 + 1 == var0.size()) {
               throw new AssertionError("Could not decode type path: " + var0);
            }

            var1.add(TypeAnnotation.Position.TypePathEntry.fromBinary((Integer)var0.get(var2), (Integer)var0.get(var2 + 1)));
         }

         return var1;
      }

      public static List getBinaryFromTypePath(List var0) {
         ArrayList var1 = new ArrayList(var0.size() * 2);
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            TypePathEntry var3 = (TypePathEntry)var2.next();
            var1.add(var3.tag.tag);
            var1.add(var3.arg);
         }

         return var1;
      }

      public static class TypePathEntry {
         public static final int bytesPerEntry = 2;
         public final TypePathEntryKind tag;
         public final int arg;
         public static final TypePathEntry ARRAY;
         public static final TypePathEntry INNER_TYPE;
         public static final TypePathEntry WILDCARD;

         private TypePathEntry(TypePathEntryKind var1) {
            if (var1 != TypeAnnotation.Position.TypePathEntryKind.ARRAY && var1 != TypeAnnotation.Position.TypePathEntryKind.INNER_TYPE && var1 != TypeAnnotation.Position.TypePathEntryKind.WILDCARD) {
               throw new AssertionError("Invalid TypePathEntryKind: " + var1);
            } else {
               this.tag = var1;
               this.arg = 0;
            }
         }

         public TypePathEntry(TypePathEntryKind var1, int var2) {
            if (var1 != TypeAnnotation.Position.TypePathEntryKind.TYPE_ARGUMENT) {
               throw new AssertionError("Invalid TypePathEntryKind: " + var1);
            } else {
               this.tag = var1;
               this.arg = var2;
            }
         }

         public static TypePathEntry fromBinary(int var0, int var1) {
            if (var1 != 0 && var0 != TypeAnnotation.Position.TypePathEntryKind.TYPE_ARGUMENT.tag) {
               throw new AssertionError("Invalid TypePathEntry tag/arg: " + var0 + "/" + var1);
            } else {
               switch (var0) {
                  case 0:
                     return ARRAY;
                  case 1:
                     return INNER_TYPE;
                  case 2:
                     return WILDCARD;
                  case 3:
                     return new TypePathEntry(TypeAnnotation.Position.TypePathEntryKind.TYPE_ARGUMENT, var1);
                  default:
                     throw new AssertionError("Invalid TypePathEntryKind tag: " + var0);
               }
            }
         }

         public String toString() {
            return this.tag.toString() + (this.tag == TypeAnnotation.Position.TypePathEntryKind.TYPE_ARGUMENT ? "(" + this.arg + ")" : "");
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
            ARRAY = new TypePathEntry(TypeAnnotation.Position.TypePathEntryKind.ARRAY);
            INNER_TYPE = new TypePathEntry(TypeAnnotation.Position.TypePathEntryKind.INNER_TYPE);
            WILDCARD = new TypePathEntry(TypeAnnotation.Position.TypePathEntryKind.WILDCARD);
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
}
