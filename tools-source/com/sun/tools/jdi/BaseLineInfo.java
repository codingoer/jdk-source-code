package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;

class BaseLineInfo implements LineInfo {
   private final int lineNumber;
   private final ReferenceTypeImpl declaringType;

   BaseLineInfo(int var1, ReferenceTypeImpl var2) {
      this.lineNumber = var1;
      this.declaringType = var2;
   }

   public String liStratum() {
      return "Java";
   }

   public int liLineNumber() {
      return this.lineNumber;
   }

   public String liSourceName() throws AbsentInformationException {
      return this.declaringType.baseSourceName();
   }

   public String liSourcePath() throws AbsentInformationException {
      return this.declaringType.baseSourcePath();
   }
}
