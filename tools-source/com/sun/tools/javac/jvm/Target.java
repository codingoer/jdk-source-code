package com.sun.tools.javac.jvm;

import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import java.util.HashMap;
import java.util.Map;

public enum Target {
   JDK1_1("1.1", 45, 3),
   JDK1_2("1.2", 46, 0),
   JDK1_3("1.3", 47, 0),
   JDK1_4("1.4", 48, 0),
   JDK1_5("1.5", 49, 0),
   JDK1_6("1.6", 50, 0),
   JDK1_7("1.7", 51, 0),
   JDK1_8("1.8", 52, 0);

   private static final Context.Key targetKey = new Context.Key();
   private static final Target MIN = values()[0];
   private static final Target MAX = values()[values().length - 1];
   private static final Map tab = new HashMap();
   public final String name;
   public final int majorVersion;
   public final int minorVersion;
   public static final Target DEFAULT;

   public static Target instance(Context var0) {
      Target var1 = (Target)var0.get(targetKey);
      if (var1 == null) {
         Options var2 = Options.instance(var0);
         String var3 = var2.get(Option.TARGET);
         if (var3 != null) {
            var1 = lookup(var3);
         }

         if (var1 == null) {
            var1 = DEFAULT;
         }

         var0.put((Context.Key)targetKey, (Object)var1);
      }

      return var1;
   }

   public static Target MIN() {
      return MIN;
   }

   public static Target MAX() {
      return MAX;
   }

   private Target(String var3, int var4, int var5) {
      this.name = var3;
      this.majorVersion = var4;
      this.minorVersion = var5;
   }

   public static Target lookup(String var0) {
      return (Target)tab.get(var0);
   }

   public boolean requiresIproxy() {
      return this.compareTo(JDK1_1) <= 0;
   }

   public boolean initializeFieldsBeforeSuper() {
      return this.compareTo(JDK1_4) >= 0;
   }

   public boolean obeyBinaryCompatibility() {
      return this.compareTo(JDK1_2) >= 0;
   }

   public boolean arrayBinaryCompatibility() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean interfaceFieldsBinaryCompatibility() {
      return this.compareTo(JDK1_2) > 0;
   }

   public boolean interfaceObjectOverridesBinaryCompatibility() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean usePrivateSyntheticFields() {
      return this.compareTo(JDK1_5) < 0;
   }

   public boolean useInnerCacheClass() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean generateCLDCStackmap() {
      return false;
   }

   public boolean generateStackMapTable() {
      return this.compareTo(JDK1_6) >= 0;
   }

   public boolean isPackageInfoSynthetic() {
      return this.compareTo(JDK1_6) >= 0;
   }

   public boolean generateEmptyAfterBig() {
      return false;
   }

   public boolean useStringBuilder() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean useSyntheticFlag() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean useEnumFlag() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean useAnnotationFlag() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean useVarargsFlag() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean useBridgeFlag() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public char syntheticNameChar() {
      return '$';
   }

   public boolean hasClassLiterals() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean hasInvokedynamic() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean hasMethodHandles() {
      return this.hasInvokedynamic();
   }

   public boolean classLiteralsNoInit() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean hasInitCause() {
      return this.compareTo(JDK1_4) >= 0;
   }

   public boolean boxWithConstructors() {
      return this.compareTo(JDK1_5) < 0;
   }

   public boolean hasIterable() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean hasEnclosingMethodAttribute() {
      return this.compareTo(JDK1_5) >= 0;
   }

   static {
      Target[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Target var3 = var0[var2];
         tab.put(var3.name, var3);
      }

      tab.put("5", JDK1_5);
      tab.put("6", JDK1_6);
      tab.put("7", JDK1_7);
      tab.put("8", JDK1_8);
      DEFAULT = JDK1_8;
   }
}
