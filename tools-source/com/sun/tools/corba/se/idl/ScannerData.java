package com.sun.tools.corba.se.idl;

class ScannerData {
   String indent = "";
   IncludeEntry fileEntry = null;
   String filename = "";
   char[] fileBytes = null;
   int fileIndex = 0;
   int oldIndex = 0;
   char ch;
   int line = 1;
   int oldLine = 1;
   boolean macrodata = false;
   boolean includeIsImport = false;

   public ScannerData() {
   }

   public ScannerData(ScannerData var1) {
      this.indent = var1.indent;
      this.fileEntry = var1.fileEntry;
      this.filename = var1.filename;
      this.fileBytes = var1.fileBytes;
      this.fileIndex = var1.fileIndex;
      this.oldIndex = var1.oldIndex;
      this.ch = var1.ch;
      this.line = var1.line;
      this.oldLine = var1.oldLine;
      this.macrodata = var1.macrodata;
      this.includeIsImport = var1.includeIsImport;
   }
}
