package sun.rmi.rmic.newrmic.jrmp;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

final class Util {
   private Util() {
      throw new AssertionError();
   }

   static String binaryNameOf(ClassDoc var0) {
      String var1 = var0.name().replace('.', '$');
      String var2 = var0.containingPackage().name();
      return var2.equals("") ? var1 : var2 + "." + var1;
   }

   static String methodDescriptorOf(MethodDoc var0) {
      String var1 = "(";
      Parameter[] var2 = var0.parameters();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1 = var1 + typeDescriptorOf(var2[var3].type());
      }

      var1 = var1 + ")" + typeDescriptorOf(var0.returnType());
      return var1;
   }

   private static String typeDescriptorOf(Type var0) {
      ClassDoc var2 = var0.asClassDoc();
      String var1;
      if (var2 == null) {
         String var3 = var0.typeName();
         if (var3.equals("boolean")) {
            var1 = "Z";
         } else if (var3.equals("byte")) {
            var1 = "B";
         } else if (var3.equals("char")) {
            var1 = "C";
         } else if (var3.equals("short")) {
            var1 = "S";
         } else if (var3.equals("int")) {
            var1 = "I";
         } else if (var3.equals("long")) {
            var1 = "J";
         } else if (var3.equals("float")) {
            var1 = "F";
         } else if (var3.equals("double")) {
            var1 = "D";
         } else {
            if (!var3.equals("void")) {
               throw new AssertionError("unrecognized primitive type: " + var3);
            }

            var1 = "V";
         }
      } else {
         var1 = "L" + binaryNameOf(var2).replace('.', '/') + ";";
      }

      int var5 = var0.dimension().length() / 2;

      for(int var4 = 0; var4 < var5; ++var4) {
         var1 = "[" + var1;
      }

      return var1;
   }

   static String getFriendlyUnqualifiedSignature(MethodDoc var0) {
      String var1 = var0.name() + "(";
      Parameter[] var2 = var0.parameters();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 > 0) {
            var1 = var1 + ", ";
         }

         Type var4 = var2[var3].type();
         var1 = var1 + var4.typeName() + var4.dimension();
      }

      var1 = var1 + ")";
      return var1;
   }

   static boolean isVoid(Type var0) {
      return var0.asClassDoc() == null && var0.typeName().equals("void");
   }
}
