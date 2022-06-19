package com.sun.tools.javac.util;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.EndPosTable;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.CharBuffer;
import javax.tools.JavaFileObject;

public class DiagnosticSource {
   public static final DiagnosticSource NO_SOURCE = new DiagnosticSource() {
      protected boolean findLine(int var1) {
         return false;
      }
   };
   protected JavaFileObject fileObject;
   protected EndPosTable endPosTable;
   protected SoftReference refBuf;
   protected char[] buf;
   protected int bufLen;
   protected int lineStart;
   protected int line;
   protected AbstractLog log;

   public DiagnosticSource(JavaFileObject var1, AbstractLog var2) {
      this.fileObject = var1;
      this.log = var2;
   }

   private DiagnosticSource() {
   }

   public JavaFileObject getFile() {
      return this.fileObject;
   }

   public int getLineNumber(int var1) {
      int var2;
      try {
         if (!this.findLine(var1)) {
            byte var6 = 0;
            return var6;
         }

         var2 = this.line;
      } finally {
         this.buf = null;
      }

      return var2;
   }

   public int getColumnNumber(int var1, boolean var2) {
      int var4;
      try {
         if (!this.findLine(var1)) {
            byte var9 = 0;
            return var9;
         }

         int var3 = 0;

         for(var4 = this.lineStart; var4 < var1; ++var4) {
            if (var4 >= this.bufLen) {
               byte var5 = 0;
               return var5;
            }

            if (this.buf[var4] == '\t' && var2) {
               var3 = var3 / 8 * 8 + 8;
            } else {
               ++var3;
            }
         }

         var4 = var3 + 1;
      } finally {
         this.buf = null;
      }

      return var4;
   }

   public String getLine(int var1) {
      String var3;
      try {
         if (!this.findLine(var1)) {
            Object var7 = null;
            return (String)var7;
         }

         int var2;
         for(var2 = this.lineStart; var2 < this.bufLen && this.buf[var2] != '\r' && this.buf[var2] != '\n'; ++var2) {
         }

         if (var2 - this.lineStart != 0) {
            var3 = new String(this.buf, this.lineStart, var2 - this.lineStart);
            return var3;
         }

         var3 = null;
      } finally {
         this.buf = null;
      }

      return var3;
   }

   public EndPosTable getEndPosTable() {
      return this.endPosTable;
   }

   public void setEndPosTable(EndPosTable var1) {
      if (this.endPosTable != null && this.endPosTable != var1) {
         throw new IllegalStateException("endPosTable already set");
      } else {
         this.endPosTable = var1;
      }
   }

   protected boolean findLine(int var1) {
      if (var1 == -1) {
         return false;
      } else {
         try {
            if (this.buf == null && this.refBuf != null) {
               this.buf = (char[])this.refBuf.get();
            }

            if (this.buf == null) {
               this.buf = this.initBuf(this.fileObject);
               this.lineStart = 0;
               this.line = 1;
            } else if (this.lineStart > var1) {
               this.lineStart = 0;
               this.line = 1;
            }

            int var2 = this.lineStart;

            while(var2 < this.bufLen && var2 < var1) {
               switch (this.buf[var2++]) {
                  case '\n':
                     ++this.line;
                     this.lineStart = var2;
                     break;
                  case '\r':
                     if (var2 < this.bufLen && this.buf[var2] == '\n') {
                        ++var2;
                     }

                     ++this.line;
                     this.lineStart = var2;
               }
            }

            return var2 <= this.bufLen;
         } catch (IOException var3) {
            this.log.directError("source.unavailable");
            this.buf = new char[0];
            return false;
         }
      }
   }

   protected char[] initBuf(JavaFileObject var1) throws IOException {
      CharSequence var3 = var1.getCharContent(true);
      char[] var2;
      if (var3 instanceof CharBuffer) {
         CharBuffer var4 = (CharBuffer)var3;
         var2 = JavacFileManager.toArray(var4);
         this.bufLen = var4.limit();
      } else {
         var2 = var3.toString().toCharArray();
         this.bufLen = var2.length;
      }

      this.refBuf = new SoftReference(var2);
      return var2;
   }

   // $FF: synthetic method
   DiagnosticSource(Object var1) {
      this();
   }
}
