package com.sun.tools.javadoc;

import com.sun.javadoc.SourcePosition;
import com.sun.tools.javac.util.Position;
import java.io.File;
import javax.tools.FileObject;

public class SourcePositionImpl implements SourcePosition {
   FileObject filename;
   int position;
   Position.LineMap lineMap;

   public File file() {
      return this.filename == null ? null : new File(this.filename.getName());
   }

   public FileObject fileObject() {
      return this.filename;
   }

   public int line() {
      return this.lineMap == null ? 0 : this.lineMap.getLineNumber(this.position);
   }

   public int column() {
      return this.lineMap == null ? 0 : this.lineMap.getColumnNumber(this.position);
   }

   private SourcePositionImpl(FileObject var1, int var2, Position.LineMap var3) {
      this.filename = var1;
      this.position = var2;
      this.lineMap = var3;
   }

   public static SourcePosition make(FileObject var0, int var1, Position.LineMap var2) {
      return var0 == null ? null : new SourcePositionImpl(var0, var1, var2);
   }

   public String toString() {
      String var1 = this.filename.getName();
      if (var1.endsWith(")")) {
         int var2 = var1.lastIndexOf("(");
         if (var2 != -1) {
            var1 = var1.substring(0, var2) + File.separatorChar + var1.substring(var2 + 1, var1.length() - 1);
         }
      }

      return this.position == -1 ? var1 : var1 + ":" + this.line();
   }
}
