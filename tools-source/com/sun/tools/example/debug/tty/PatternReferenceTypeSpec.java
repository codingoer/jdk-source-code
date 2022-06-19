package com.sun.tools.example.debug.tty;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import java.util.StringTokenizer;

class PatternReferenceTypeSpec implements ReferenceTypeSpec {
   final String classId;
   String stem;

   PatternReferenceTypeSpec(String var1) throws ClassNotFoundException {
      this.classId = var1;
      this.stem = var1;
      if (var1.startsWith("*")) {
         this.stem = this.stem.substring(1);
      } else if (var1.endsWith("*")) {
         this.stem = this.stem.substring(0, var1.length() - 1);
      }

      this.checkClassName(this.stem);
   }

   public boolean isUnique() {
      return this.classId.equals(this.stem);
   }

   public boolean matches(ReferenceType var1) {
      if (this.classId.startsWith("*")) {
         return var1.name().endsWith(this.stem);
      } else {
         return this.classId.endsWith("*") ? var1.name().startsWith(this.stem) : var1.name().equals(this.classId);
      }
   }

   public ClassPrepareRequest createPrepareRequest() {
      ClassPrepareRequest var1 = Env.vm().eventRequestManager().createClassPrepareRequest();
      var1.addClassFilter(this.classId);
      var1.addCountFilter(1);
      return var1;
   }

   public int hashCode() {
      return this.classId.hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof PatternReferenceTypeSpec) {
         PatternReferenceTypeSpec var2 = (PatternReferenceTypeSpec)var1;
         return this.classId.equals(var2.classId);
      } else {
         return false;
      }
   }

   private void checkClassName(String var1) throws ClassNotFoundException {
      StringTokenizer var2 = new StringTokenizer(var1, ".");

      String var3;
      do {
         if (!var2.hasMoreTokens()) {
            return;
         }

         var3 = var2.nextToken();
      } while(this.isJavaIdentifier(var3));

      throw new ClassNotFoundException();
   }

   private boolean isJavaIdentifier(String var1) {
      if (var1.length() == 0) {
         return false;
      } else {
         int var2 = var1.codePointAt(0);
         if (!Character.isJavaIdentifierStart(var2)) {
            return false;
         } else {
            for(int var3 = Character.charCount(var2); var3 < var1.length(); var3 += Character.charCount(var2)) {
               var2 = var1.codePointAt(var3);
               if (!Character.isJavaIdentifierPart(var2)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      return this.classId;
   }
}
