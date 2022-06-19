package com.sun.tools.javac.code;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.StringUtils;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.lang.model.element.Modifier;

public class Flags {
   public static final int PUBLIC = 1;
   public static final int PRIVATE = 2;
   public static final int PROTECTED = 4;
   public static final int STATIC = 8;
   public static final int FINAL = 16;
   public static final int SYNCHRONIZED = 32;
   public static final int VOLATILE = 64;
   public static final int TRANSIENT = 128;
   public static final int NATIVE = 256;
   public static final int INTERFACE = 512;
   public static final int ABSTRACT = 1024;
   public static final int STRICTFP = 2048;
   public static final int SYNTHETIC = 4096;
   public static final int ANNOTATION = 8192;
   public static final int ENUM = 16384;
   public static final int MANDATED = 32768;
   public static final int StandardFlags = 4095;
   public static final int ACC_SUPER = 32;
   public static final int ACC_BRIDGE = 64;
   public static final int ACC_VARARGS = 128;
   public static final int DEPRECATED = 131072;
   public static final int HASINIT = 262144;
   public static final int BLOCK = 1048576;
   public static final int IPROXY = 2097152;
   public static final int NOOUTERTHIS = 4194304;
   public static final int EXISTS = 8388608;
   public static final int COMPOUND = 16777216;
   public static final int CLASS_SEEN = 33554432;
   public static final int SOURCE_SEEN = 67108864;
   public static final int LOCKED = 134217728;
   public static final int UNATTRIBUTED = 268435456;
   public static final int ANONCONSTR = 536870912;
   public static final int ACYCLIC = 1073741824;
   public static final long BRIDGE = 2147483648L;
   public static final long PARAMETER = 8589934592L;
   public static final long VARARGS = 17179869184L;
   public static final long ACYCLIC_ANN = 34359738368L;
   public static final long GENERATEDCONSTR = 68719476736L;
   public static final long HYPOTHETICAL = 137438953472L;
   public static final long PROPRIETARY = 274877906944L;
   public static final long UNION = 549755813888L;
   public static final long OVERRIDE_BRIDGE = 1099511627776L;
   public static final long EFFECTIVELY_FINAL = 2199023255552L;
   public static final long CLASH = 4398046511104L;
   public static final long DEFAULT = 8796093022208L;
   public static final long AUXILIARY = 17592186044416L;
   public static final long NOT_IN_PROFILE = 35184372088832L;
   public static final long BAD_OVERRIDE = 35184372088832L;
   public static final long SIGNATURE_POLYMORPHIC = 70368744177664L;
   public static final long THROWS = 140737488355328L;
   public static final long POTENTIALLY_AMBIGUOUS = 281474976710656L;
   public static final long LAMBDA_METHOD = 562949953421312L;
   public static final long TYPE_TRANSLATED = 1125899906842624L;
   public static final int AccessFlags = 7;
   public static final int LocalClassFlags = 23568;
   public static final int MemberClassFlags = 24087;
   public static final int ClassFlags = 32273;
   public static final int InterfaceVarFlags = 25;
   public static final int VarFlags = 16607;
   public static final int ConstructorFlags = 7;
   public static final int InterfaceMethodFlags = 1025;
   public static final int MethodFlags = 3391;
   public static final long ExtendedStandardFlags = 8796093026303L;
   public static final long ModifierFlags = 8796093025791L;
   public static final long InterfaceMethodMask = 8796093025289L;
   public static final long AnnotationTypeElementMask = 1025L;
   public static final long LocalVarFlags = 8589934608L;
   public static final long ReceiverParamFlags = 8589934592L;
   private static final Map modifierSets = new ConcurrentHashMap(64);

   private Flags() {
   }

   public static String toString(long var0) {
      StringBuilder var2 = new StringBuilder();
      String var3 = "";

      for(Iterator var4 = asFlagSet(var0).iterator(); var4.hasNext(); var3 = " ") {
         Flag var5 = (Flag)var4.next();
         var2.append(var3);
         var2.append(var5);
      }

      return var2.toString();
   }

   public static EnumSet asFlagSet(long var0) {
      EnumSet var2 = EnumSet.noneOf(Flag.class);
      Flag[] var3 = Flags.Flag.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Flag var6 = var3[var5];
         if ((var0 & var6.value) != 0L) {
            var2.add(var6);
            var0 &= ~var6.value;
         }
      }

      Assert.check(var0 == 0L, "Flags parameter contains unknown flags " + var0);
      return var2;
   }

   public static Set asModifierSet(long var0) {
      Set var2 = (Set)modifierSets.get(var0);
      if (var2 == null) {
         EnumSet var3 = EnumSet.noneOf(Modifier.class);
         if (0L != (var0 & 1L)) {
            var3.add(Modifier.PUBLIC);
         }

         if (0L != (var0 & 4L)) {
            var3.add(Modifier.PROTECTED);
         }

         if (0L != (var0 & 2L)) {
            var3.add(Modifier.PRIVATE);
         }

         if (0L != (var0 & 1024L)) {
            var3.add(Modifier.ABSTRACT);
         }

         if (0L != (var0 & 8L)) {
            var3.add(Modifier.STATIC);
         }

         if (0L != (var0 & 16L)) {
            var3.add(Modifier.FINAL);
         }

         if (0L != (var0 & 128L)) {
            var3.add(Modifier.TRANSIENT);
         }

         if (0L != (var0 & 64L)) {
            var3.add(Modifier.VOLATILE);
         }

         if (0L != (var0 & 32L)) {
            var3.add(Modifier.SYNCHRONIZED);
         }

         if (0L != (var0 & 256L)) {
            var3.add(Modifier.NATIVE);
         }

         if (0L != (var0 & 2048L)) {
            var3.add(Modifier.STRICTFP);
         }

         if (0L != (var0 & 8796093022208L)) {
            var3.add(Modifier.DEFAULT);
         }

         var2 = Collections.unmodifiableSet(var3);
         modifierSets.put(var0, var2);
      }

      return var2;
   }

   public static boolean isStatic(Symbol var0) {
      return (var0.flags() & 8L) != 0L;
   }

   public static boolean isEnum(Symbol var0) {
      return (var0.flags() & 16384L) != 0L;
   }

   public static boolean isConstant(Symbol.VarSymbol var0) {
      return var0.getConstValue() != null;
   }

   public static enum Flag {
      PUBLIC(1L),
      PRIVATE(2L),
      PROTECTED(4L),
      STATIC(8L),
      FINAL(16L),
      SYNCHRONIZED(32L),
      VOLATILE(64L),
      TRANSIENT(128L),
      NATIVE(256L),
      INTERFACE(512L),
      ABSTRACT(1024L),
      DEFAULT(8796093022208L),
      STRICTFP(2048L),
      BRIDGE(2147483648L),
      SYNTHETIC(4096L),
      ANNOTATION(8192L),
      DEPRECATED(131072L),
      HASINIT(262144L),
      BLOCK(1048576L),
      ENUM(16384L),
      MANDATED(32768L),
      IPROXY(2097152L),
      NOOUTERTHIS(4194304L),
      EXISTS(8388608L),
      COMPOUND(16777216L),
      CLASS_SEEN(33554432L),
      SOURCE_SEEN(67108864L),
      LOCKED(134217728L),
      UNATTRIBUTED(268435456L),
      ANONCONSTR(536870912L),
      ACYCLIC(1073741824L),
      PARAMETER(8589934592L),
      VARARGS(17179869184L),
      ACYCLIC_ANN(34359738368L),
      GENERATEDCONSTR(68719476736L),
      HYPOTHETICAL(137438953472L),
      PROPRIETARY(274877906944L),
      UNION(549755813888L),
      OVERRIDE_BRIDGE(1099511627776L),
      EFFECTIVELY_FINAL(2199023255552L),
      CLASH(4398046511104L),
      AUXILIARY(17592186044416L),
      NOT_IN_PROFILE(35184372088832L),
      BAD_OVERRIDE(35184372088832L),
      SIGNATURE_POLYMORPHIC(70368744177664L),
      THROWS(140737488355328L),
      LAMBDA_METHOD(562949953421312L),
      TYPE_TRANSLATED(1125899906842624L);

      final long value;
      final String lowercaseName;

      private Flag(long var3) {
         this.value = var3;
         this.lowercaseName = StringUtils.toLowerCase(this.name());
      }

      public String toString() {
         return this.lowercaseName;
      }
   }
}
