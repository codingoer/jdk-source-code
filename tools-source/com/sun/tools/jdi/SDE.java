package com.sun.tools.jdi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class SDE {
   private static final int INIT_SIZE_FILE = 3;
   private static final int INIT_SIZE_LINE = 100;
   private static final int INIT_SIZE_STRATUM = 3;
   static final String BASE_STRATUM_NAME = "Java";
   static final String NullString = null;
   private FileTableRecord[] fileTable = null;
   private LineTableRecord[] lineTable = null;
   private StratumTableRecord[] stratumTable = null;
   private int fileIndex = 0;
   private int lineIndex = 0;
   private int stratumIndex = 0;
   private int currentFileId = 0;
   private int defaultStratumIndex = -1;
   private int baseStratumIndex = -2;
   private int sdePos = 0;
   final String sourceDebugExtension;
   String jplsFilename = null;
   String defaultStratumId = null;
   boolean isValid = false;

   SDE(String var1) {
      this.sourceDebugExtension = var1;
      this.decode();
   }

   SDE() {
      this.sourceDebugExtension = null;
      this.createProxyForAbsentSDE();
   }

   char sdePeek() {
      if (this.sdePos >= this.sourceDebugExtension.length()) {
         this.syntax();
      }

      return this.sourceDebugExtension.charAt(this.sdePos);
   }

   char sdeRead() {
      if (this.sdePos >= this.sourceDebugExtension.length()) {
         this.syntax();
      }

      return this.sourceDebugExtension.charAt(this.sdePos++);
   }

   void sdeAdvance() {
      ++this.sdePos;
   }

   void syntax() {
      throw new InternalError("bad SourceDebugExtension syntax - position " + this.sdePos);
   }

   void syntax(String var1) {
      throw new InternalError("bad SourceDebugExtension syntax: " + var1);
   }

   void assureLineTableSize() {
      int var1 = this.lineTable == null ? 0 : this.lineTable.length;
      if (this.lineIndex >= var1) {
         int var3 = var1 == 0 ? 100 : var1 * 2;
         LineTableRecord[] var4 = new LineTableRecord[var3];

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            var4[var2] = this.lineTable[var2];
         }

         while(var2 < var3) {
            var4[var2] = new LineTableRecord();
            ++var2;
         }

         this.lineTable = var4;
      }

   }

   void assureFileTableSize() {
      int var1 = this.fileTable == null ? 0 : this.fileTable.length;
      if (this.fileIndex >= var1) {
         int var3 = var1 == 0 ? 3 : var1 * 2;
         FileTableRecord[] var4 = new FileTableRecord[var3];

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            var4[var2] = this.fileTable[var2];
         }

         while(var2 < var3) {
            var4[var2] = new FileTableRecord();
            ++var2;
         }

         this.fileTable = var4;
      }

   }

   void assureStratumTableSize() {
      int var1 = this.stratumTable == null ? 0 : this.stratumTable.length;
      if (this.stratumIndex >= var1) {
         int var3 = var1 == 0 ? 3 : var1 * 2;
         StratumTableRecord[] var4 = new StratumTableRecord[var3];

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            var4[var2] = this.stratumTable[var2];
         }

         while(var2 < var3) {
            var4[var2] = new StratumTableRecord();
            ++var2;
         }

         this.stratumTable = var4;
      }

   }

   String readLine() {
      StringBuffer var1 = new StringBuffer();
      this.ignoreWhite();

      char var2;
      while((var2 = this.sdeRead()) != '\n' && var2 != '\r') {
         var1.append(var2);
      }

      if (var2 == '\r' && this.sdePeek() == '\n') {
         this.sdeRead();
      }

      this.ignoreWhite();
      return var1.toString();
   }

   private int defaultStratumTableIndex() {
      if (this.defaultStratumIndex == -1 && this.defaultStratumId != null) {
         this.defaultStratumIndex = this.stratumTableIndex(this.defaultStratumId);
      }

      return this.defaultStratumIndex;
   }

   int stratumTableIndex(String var1) {
      if (var1 == null) {
         return this.defaultStratumTableIndex();
      } else {
         for(int var2 = 0; var2 < this.stratumIndex - 1; ++var2) {
            if (this.stratumTable[var2].id.equals(var1)) {
               return var2;
            }
         }

         return this.defaultStratumTableIndex();
      }
   }

   Stratum stratum(String var1) {
      int var2 = this.stratumTableIndex(var1);
      return new Stratum(var2);
   }

   List availableStrata() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.stratumIndex - 1; ++var2) {
         StratumTableRecord var3 = this.stratumTable[var2];
         var1.add(var3.id);
      }

      return var1;
   }

   void ignoreWhite() {
      char var1;
      while((var1 = this.sdePeek()) == ' ' || var1 == '\t') {
         this.sdeAdvance();
      }

   }

   void ignoreLine() {
      char var1;
      while((var1 = this.sdeRead()) != '\n' && var1 != '\r') {
      }

      if (var1 == '\r' && this.sdePeek() == '\n') {
         this.sdeAdvance();
      }

      this.ignoreWhite();
   }

   int readNumber() {
      int var1 = 0;
      this.ignoreWhite();

      char var2;
      while((var2 = this.sdePeek()) >= '0' && var2 <= '9') {
         this.sdeAdvance();
         var1 = var1 * 10 + var2 - 48;
      }

      this.ignoreWhite();
      return var1;
   }

   void storeFile(int var1, String var2, String var3) {
      this.assureFileTableSize();
      this.fileTable[this.fileIndex].fileId = var1;
      this.fileTable[this.fileIndex].sourceName = var2;
      this.fileTable[this.fileIndex].sourcePath = var3;
      ++this.fileIndex;
   }

   void fileLine() {
      boolean var1 = false;
      String var4 = null;
      if (this.sdePeek() == '+') {
         this.sdeAdvance();
         var1 = true;
      }

      int var2 = this.readNumber();
      String var3 = this.readLine();
      if (var1) {
         var4 = this.readLine();
      }

      this.storeFile(var2, var3, var4);
   }

   void storeLine(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.assureLineTableSize();
      this.lineTable[this.lineIndex].jplsStart = var1;
      this.lineTable[this.lineIndex].jplsEnd = var2;
      this.lineTable[this.lineIndex].jplsLineInc = var3;
      this.lineTable[this.lineIndex].njplsStart = var4;
      this.lineTable[this.lineIndex].njplsEnd = var5;
      this.lineTable[this.lineIndex].fileId = var6;
      ++this.lineIndex;
   }

   void lineLine() {
      int var1 = 1;
      int var2 = 1;
      int var3 = this.readNumber();
      if (this.sdePeek() == '#') {
         this.sdeAdvance();
         this.currentFileId = this.readNumber();
      }

      if (this.sdePeek() == ',') {
         this.sdeAdvance();
         var1 = this.readNumber();
      }

      if (this.sdeRead() != ':') {
         this.syntax();
      }

      int var4 = this.readNumber();
      if (this.sdePeek() == ',') {
         this.sdeAdvance();
         var2 = this.readNumber();
      }

      this.ignoreLine();
      this.storeLine(var4, var4 + var1 * var2 - 1, var2, var3, var3 + var1 - 1, this.currentFileId);
   }

   void storeStratum(String var1) {
      if (this.stratumIndex > 0 && this.stratumTable[this.stratumIndex - 1].fileIndex == this.fileIndex && this.stratumTable[this.stratumIndex - 1].lineIndex == this.lineIndex) {
         --this.stratumIndex;
      }

      this.assureStratumTableSize();
      this.stratumTable[this.stratumIndex].id = var1;
      this.stratumTable[this.stratumIndex].fileIndex = this.fileIndex;
      this.stratumTable[this.stratumIndex].lineIndex = this.lineIndex;
      ++this.stratumIndex;
      this.currentFileId = 0;
   }

   void stratumSection() {
      this.storeStratum(this.readLine());
   }

   void fileSection() {
      this.ignoreLine();

      while(this.sdePeek() != '*') {
         this.fileLine();
      }

   }

   void lineSection() {
      this.ignoreLine();

      while(this.sdePeek() != '*') {
         this.lineLine();
      }

   }

   void ignoreSection() {
      this.ignoreLine();

      while(this.sdePeek() != '*') {
         this.ignoreLine();
      }

   }

   void createJavaStratum() {
      this.baseStratumIndex = this.stratumIndex;
      this.storeStratum("Java");
      this.storeFile(1, this.jplsFilename, NullString);
      this.storeLine(1, 65536, 1, 1, 65536, 1);
      this.storeStratum("Aux");
   }

   void decode() {
      if (this.sourceDebugExtension.length() >= 4 && this.sdeRead() == 'S' && this.sdeRead() == 'M' && this.sdeRead() == 'A' && this.sdeRead() == 'P') {
         this.ignoreLine();
         this.jplsFilename = this.readLine();
         this.defaultStratumId = this.readLine();
         this.createJavaStratum();

         while(true) {
            if (this.sdeRead() != '*') {
               this.syntax();
            }

            switch (this.sdeRead()) {
               case 'E':
                  this.storeStratum("*terminator*");
                  this.isValid = true;
                  return;
               case 'F':
                  this.fileSection();
                  break;
               case 'L':
                  this.lineSection();
                  break;
               case 'S':
                  this.stratumSection();
                  break;
               default:
                  this.ignoreSection();
            }
         }
      }
   }

   void createProxyForAbsentSDE() {
      this.jplsFilename = null;
      this.defaultStratumId = "Java";
      this.defaultStratumIndex = this.stratumIndex;
      this.createJavaStratum();
      this.storeStratum("*terminator*");
   }

   private int stiLineTableIndex(int var1, int var2) {
      int var4 = this.stratumTable[var1].lineIndex;
      int var5 = this.stratumTable[var1 + 1].lineIndex;

      for(int var3 = var4; var3 < var5; ++var3) {
         if (var2 >= this.lineTable[var3].jplsStart && var2 <= this.lineTable[var3].jplsEnd) {
            return var3;
         }
      }

      return -1;
   }

   private int stiLineNumber(int var1, int var2, int var3) {
      return this.lineTable[var2].njplsStart + (var3 - this.lineTable[var2].jplsStart) / this.lineTable[var2].jplsLineInc;
   }

   private int fileTableIndex(int var1, int var2) {
      int var4 = this.stratumTable[var1].fileIndex;
      int var5 = this.stratumTable[var1 + 1].fileIndex;

      for(int var3 = var4; var3 < var5; ++var3) {
         if (this.fileTable[var3].fileId == var2) {
            return var3;
         }
      }

      return -1;
   }

   private int stiFileTableIndex(int var1, int var2) {
      return this.fileTableIndex(var1, this.lineTable[var2].fileId);
   }

   boolean isValid() {
      return this.isValid;
   }

   class LineStratum {
      private final int sti;
      private final int lti;
      private final ReferenceTypeImpl refType;
      private final int jplsLine;
      private String sourceName;
      private String sourcePath;

      private LineStratum(int var2, int var3, ReferenceTypeImpl var4, int var5) {
         this.sourceName = null;
         this.sourcePath = null;
         this.sti = var2;
         this.lti = var3;
         this.refType = var4;
         this.jplsLine = var5;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof LineStratum)) {
            return false;
         } else {
            LineStratum var2 = (LineStratum)var1;
            return this.lti == var2.lti && this.sti == var2.sti && this.lineNumber() == var2.lineNumber() && this.refType.equals(var2.refType);
         }
      }

      public int hashCode() {
         return this.lineNumber() * 17 ^ this.refType.hashCode();
      }

      int lineNumber() {
         return SDE.this.stiLineNumber(this.sti, this.lti, this.jplsLine);
      }

      void getSourceInfo() {
         if (this.sourceName == null) {
            int var1 = SDE.this.stiFileTableIndex(this.sti, this.lti);
            if (var1 == -1) {
               throw new InternalError("Bad SourceDebugExtension, no matching source id " + SDE.this.lineTable[this.lti].fileId + " jplsLine: " + this.jplsLine);
            } else {
               FileTableRecord var2 = SDE.this.fileTable[var1];
               this.sourceName = var2.sourceName;
               this.sourcePath = var2.getSourcePath(this.refType);
            }
         }
      }

      String sourceName() {
         this.getSourceInfo();
         return this.sourceName;
      }

      String sourcePath() {
         this.getSourceInfo();
         return this.sourcePath;
      }

      // $FF: synthetic method
      LineStratum(int var2, int var3, ReferenceTypeImpl var4, int var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }

   class Stratum {
      private final int sti;

      private Stratum(int var2) {
         this.sti = var2;
      }

      String id() {
         return SDE.this.stratumTable[this.sti].id;
      }

      boolean isJava() {
         return this.sti == SDE.this.baseStratumIndex;
      }

      List sourceNames(ReferenceTypeImpl var1) {
         int var3 = SDE.this.stratumTable[this.sti].fileIndex;
         int var4 = SDE.this.stratumTable[this.sti + 1].fileIndex;
         ArrayList var5 = new ArrayList(var4 - var3);

         for(int var2 = var3; var2 < var4; ++var2) {
            var5.add(SDE.this.fileTable[var2].sourceName);
         }

         return var5;
      }

      List sourcePaths(ReferenceTypeImpl var1) {
         int var3 = SDE.this.stratumTable[this.sti].fileIndex;
         int var4 = SDE.this.stratumTable[this.sti + 1].fileIndex;
         ArrayList var5 = new ArrayList(var4 - var3);

         for(int var2 = var3; var2 < var4; ++var2) {
            var5.add(SDE.this.fileTable[var2].getSourcePath(var1));
         }

         return var5;
      }

      LineStratum lineStratum(ReferenceTypeImpl var1, int var2) {
         int var3 = SDE.this.stiLineTableIndex(this.sti, var2);
         return var3 < 0 ? null : SDE.this.new LineStratum(this.sti, var3, var1, var2);
      }

      // $FF: synthetic method
      Stratum(int var2, Object var3) {
         this(var2);
      }
   }

   private class StratumTableRecord {
      String id;
      int fileIndex;
      int lineIndex;

      private StratumTableRecord() {
      }

      // $FF: synthetic method
      StratumTableRecord(Object var2) {
         this();
      }
   }

   private class LineTableRecord {
      int jplsStart;
      int jplsEnd;
      int jplsLineInc;
      int njplsStart;
      int njplsEnd;
      int fileId;

      private LineTableRecord() {
      }

      // $FF: synthetic method
      LineTableRecord(Object var2) {
         this();
      }
   }

   private class FileTableRecord {
      int fileId;
      String sourceName;
      String sourcePath;
      boolean isConverted;

      private FileTableRecord() {
         this.isConverted = false;
      }

      String getSourcePath(ReferenceTypeImpl var1) {
         if (!this.isConverted) {
            if (this.sourcePath == null) {
               this.sourcePath = var1.baseSourceDir() + this.sourceName;
            } else {
               StringBuffer var2 = new StringBuffer();

               for(int var3 = 0; var3 < this.sourcePath.length(); ++var3) {
                  char var4 = this.sourcePath.charAt(var3);
                  if (var4 == '/') {
                     var2.append(File.separatorChar);
                  } else {
                     var2.append(var4);
                  }
               }

               this.sourcePath = var2.toString();
            }

            this.isConverted = true;
         }

         return this.sourcePath;
      }

      // $FF: synthetic method
      FileTableRecord(Object var2) {
         this();
      }
   }
}
