package com.sun.tools.javac.code;

import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.api.Formattable;
import com.sun.tools.javac.api.Messages;
import java.util.EnumSet;
import java.util.Locale;

public class Kinds {
   public static final int NIL = 0;
   public static final int PCK = 1;
   public static final int TYP = 2;
   public static final int VAR = 4;
   public static final int VAL = 12;
   public static final int MTH = 16;
   public static final int POLY = 32;
   public static final int ERR = 63;
   public static final int AllKinds = 63;
   public static final int ERRONEOUS = 128;
   public static final int AMBIGUOUS = 129;
   public static final int HIDDEN = 130;
   public static final int STATICERR = 131;
   public static final int MISSING_ENCL = 132;
   public static final int ABSENT_VAR = 133;
   public static final int WRONG_MTHS = 134;
   public static final int WRONG_MTH = 135;
   public static final int ABSENT_MTH = 136;
   public static final int ABSENT_TYP = 137;
   public static final int WRONG_STATICNESS = 138;

   private Kinds() {
   }

   public static KindName kindName(int var0) {
      switch (var0) {
         case 1:
            return Kinds.KindName.PACKAGE;
         case 2:
            return Kinds.KindName.CLASS;
         case 4:
            return Kinds.KindName.VAR;
         case 12:
            return Kinds.KindName.VAL;
         case 16:
            return Kinds.KindName.METHOD;
         default:
            throw new AssertionError("Unexpected kind: " + var0);
      }
   }

   public static KindName kindName(MemberReferenceTree.ReferenceMode var0) {
      switch (var0) {
         case INVOKE:
            return Kinds.KindName.METHOD;
         case NEW:
            return Kinds.KindName.CONSTRUCTOR;
         default:
            throw new AssertionError("Unexpected mode: " + var0);
      }
   }

   public static KindName kindName(Symbol var0) {
      switch (var0.getKind()) {
         case PACKAGE:
            return Kinds.KindName.PACKAGE;
         case ENUM:
            return Kinds.KindName.ENUM;
         case ANNOTATION_TYPE:
         case CLASS:
            return Kinds.KindName.CLASS;
         case INTERFACE:
            return Kinds.KindName.INTERFACE;
         case TYPE_PARAMETER:
            return Kinds.KindName.TYPEVAR;
         case ENUM_CONSTANT:
         case FIELD:
         case PARAMETER:
         case LOCAL_VARIABLE:
         case EXCEPTION_PARAMETER:
         case RESOURCE_VARIABLE:
            return Kinds.KindName.VAR;
         case CONSTRUCTOR:
            return Kinds.KindName.CONSTRUCTOR;
         case METHOD:
            return Kinds.KindName.METHOD;
         case STATIC_INIT:
            return Kinds.KindName.STATIC_INIT;
         case INSTANCE_INIT:
            return Kinds.KindName.INSTANCE_INIT;
         default:
            if (var0.kind == 12) {
               return Kinds.KindName.VAL;
            } else {
               throw new AssertionError("Unexpected kind: " + var0.getKind());
            }
      }
   }

   public static EnumSet kindNames(int var0) {
      EnumSet var1 = EnumSet.noneOf(KindName.class);
      if ((var0 & 12) != 0) {
         var1.add((var0 & 12) == 4 ? Kinds.KindName.VAR : Kinds.KindName.VAL);
      }

      if ((var0 & 16) != 0) {
         var1.add(Kinds.KindName.METHOD);
      }

      if ((var0 & 2) != 0) {
         var1.add(Kinds.KindName.CLASS);
      }

      if ((var0 & 1) != 0) {
         var1.add(Kinds.KindName.PACKAGE);
      }

      return var1;
   }

   public static KindName typeKindName(Type var0) {
      if (!var0.hasTag(TypeTag.TYPEVAR) && (!var0.hasTag(TypeTag.CLASS) || (var0.tsym.flags() & 16777216L) == 0L)) {
         if (var0.hasTag(TypeTag.PACKAGE)) {
            return Kinds.KindName.PACKAGE;
         } else if ((var0.tsym.flags_field & 8192L) != 0L) {
            return Kinds.KindName.ANNOTATION;
         } else {
            return (var0.tsym.flags_field & 512L) != 0L ? Kinds.KindName.INTERFACE : Kinds.KindName.CLASS;
         }
      } else {
         return Kinds.KindName.BOUND;
      }
   }

   public static KindName absentKind(int var0) {
      switch (var0) {
         case 133:
            return Kinds.KindName.VAR;
         case 134:
         case 135:
         case 136:
         case 138:
            return Kinds.KindName.METHOD;
         case 137:
            return Kinds.KindName.CLASS;
         default:
            throw new AssertionError("Unexpected kind: " + var0);
      }
   }

   public static enum KindName implements Formattable {
      ANNOTATION("kindname.annotation"),
      CONSTRUCTOR("kindname.constructor"),
      INTERFACE("kindname.interface"),
      ENUM("kindname.enum"),
      STATIC("kindname.static"),
      TYPEVAR("kindname.type.variable"),
      BOUND("kindname.type.variable.bound"),
      VAR("kindname.variable"),
      VAL("kindname.value"),
      METHOD("kindname.method"),
      CLASS("kindname.class"),
      STATIC_INIT("kindname.static.init"),
      INSTANCE_INIT("kindname.instance.init"),
      PACKAGE("kindname.package");

      private final String name;

      private KindName(String var3) {
         this.name = var3;
      }

      public String toString() {
         return this.name;
      }

      public String getKind() {
         return "Kindname";
      }

      public String toString(Locale var1, Messages var2) {
         String var3 = this.toString();
         return var2.getLocalizedString(var1, "compiler.misc." + var3);
      }
   }
}
