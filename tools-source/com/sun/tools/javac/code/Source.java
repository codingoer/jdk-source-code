package com.sun.tools.javac.code;

import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.SourceVersion;

public enum Source {
   JDK1_2("1.2"),
   JDK1_3("1.3"),
   JDK1_4("1.4"),
   JDK1_5("1.5"),
   JDK1_6("1.6"),
   JDK1_7("1.7"),
   JDK1_8("1.8");

   private static final Context.Key sourceKey = new Context.Key();
   public final String name;
   private static final Map tab = new HashMap();
   public static final Source DEFAULT;

   public static Source instance(Context var0) {
      Source var1 = (Source)var0.get(sourceKey);
      if (var1 == null) {
         Options var2 = Options.instance(var0);
         String var3 = var2.get(Option.SOURCE);
         if (var3 != null) {
            var1 = lookup(var3);
         }

         if (var1 == null) {
            var1 = DEFAULT;
         }

         var0.put((Context.Key)sourceKey, (Object)var1);
      }

      return var1;
   }

   private Source(String var3) {
      this.name = var3;
   }

   public static Source lookup(String var0) {
      return (Source)tab.get(var0);
   }

   public Target requiredTarget() {
      if (this.compareTo(JDK1_8) >= 0) {
         return Target.JDK1_8;
      } else if (this.compareTo(JDK1_7) >= 0) {
         return Target.JDK1_7;
      } else if (this.compareTo(JDK1_6) >= 0) {
         return Target.JDK1_6;
      } else if (this.compareTo(JDK1_5) >= 0) {
         return Target.JDK1_5;
      } else {
         return this.compareTo(JDK1_4) >= 0 ? Target.JDK1_4 : Target.JDK1_1;
      }
   }

   public boolean allowEncodingErrors() {
      return this.compareTo(JDK1_6) < 0;
   }

   public boolean allowAsserts() {
      return this.compareTo(JDK1_4) >= 0;
   }

   public boolean allowCovariantReturns() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowGenerics() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowDiamond() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowMulticatch() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowImprovedRethrowAnalysis() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowImprovedCatchAnalysis() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowEnums() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowForeach() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowStaticImport() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowBoxing() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowVarargs() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowAnnotations() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowHexFloats() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowAnonOuterThis() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean addBridges() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean enforceMandatoryWarnings() {
      return this.compareTo(JDK1_5) >= 0;
   }

   public boolean allowTryWithResources() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowBinaryLiterals() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowUnderscoresInLiterals() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowStringsInSwitch() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowSimplifiedVarargs() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowObjectToPrimitiveCast() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean enforceThisDotInit() {
      return this.compareTo(JDK1_7) >= 0;
   }

   public boolean allowPoly() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowLambda() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowMethodReferences() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowDefaultMethods() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowStaticInterfaceMethods() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowStrictMethodClashCheck() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowEffectivelyFinalInInnerClasses() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowTypeAnnotations() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowAnnotationsAfterTypeParams() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowRepeatedAnnotations() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowIntersectionTypesInCast() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowGraphInference() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowFunctionalInterfaceMostSpecific() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public boolean allowPostApplicabilityVarargsAccessCheck() {
      return this.compareTo(JDK1_8) >= 0;
   }

   public static SourceVersion toSourceVersion(Source var0) {
      switch (var0) {
         case JDK1_2:
            return SourceVersion.RELEASE_2;
         case JDK1_3:
            return SourceVersion.RELEASE_3;
         case JDK1_4:
            return SourceVersion.RELEASE_4;
         case JDK1_5:
            return SourceVersion.RELEASE_5;
         case JDK1_6:
            return SourceVersion.RELEASE_6;
         case JDK1_7:
            return SourceVersion.RELEASE_7;
         case JDK1_8:
            return SourceVersion.RELEASE_8;
         default:
            return null;
      }
   }

   static {
      Source[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Source var3 = var0[var2];
         tab.put(var3.name, var3);
      }

      tab.put("5", JDK1_5);
      tab.put("6", JDK1_6);
      tab.put("7", JDK1_7);
      tab.put("8", JDK1_8);
      DEFAULT = JDK1_8;
   }
}
