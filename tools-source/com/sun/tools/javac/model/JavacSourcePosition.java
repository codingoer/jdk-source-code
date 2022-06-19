package com.sun.tools.javac.model;

import com.sun.tools.javac.util.Position;
import javax.tools.JavaFileObject;

class JavacSourcePosition {
   final JavaFileObject sourcefile;
   final int pos;
   final Position.LineMap lineMap;

   JavacSourcePosition(JavaFileObject var1, int var2, Position.LineMap var3) {
      this.sourcefile = var1;
      this.pos = var2;
      this.lineMap = var2 != -1 ? var3 : null;
   }

   public JavaFileObject getFile() {
      return this.sourcefile;
   }

   public int getOffset() {
      return this.pos;
   }

   public int getLine() {
      return this.lineMap != null ? this.lineMap.getLineNumber(this.pos) : -1;
   }

   public int getColumn() {
      return this.lineMap != null ? this.lineMap.getColumnNumber(this.pos) : -1;
   }

   public String toString() {
      int var1 = this.getLine();
      return var1 > 0 ? this.sourcefile + ":" + var1 : this.sourcefile.toString();
   }
}
