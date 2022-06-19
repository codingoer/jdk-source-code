package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;

class StratumLineInfo implements LineInfo {
   private final String stratumID;
   private final int lineNumber;
   private final String sourceName;
   private final String sourcePath;

   StratumLineInfo(String var1, int var2, String var3, String var4) {
      this.stratumID = var1;
      this.lineNumber = var2;
      this.sourceName = var3;
      this.sourcePath = var4;
   }

   public String liStratum() {
      return this.stratumID;
   }

   public int liLineNumber() {
      return this.lineNumber;
   }

   public String liSourceName() throws AbsentInformationException {
      if (this.sourceName == null) {
         throw new AbsentInformationException();
      } else {
         return this.sourceName;
      }
   }

   public String liSourcePath() throws AbsentInformationException {
      if (this.sourcePath == null) {
         throw new AbsentInformationException();
      } else {
         return this.sourcePath;
      }
   }
}
