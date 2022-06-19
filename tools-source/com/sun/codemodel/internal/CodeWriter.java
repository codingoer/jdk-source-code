package com.sun.codemodel.internal;

import com.sun.codemodel.internal.util.EncoderFactory;
import com.sun.codemodel.internal.util.UnicodeEscapeWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

public abstract class CodeWriter {
   protected String encoding = null;

   public abstract OutputStream openBinary(JPackage var1, String var2) throws IOException;

   public Writer openSource(JPackage pkg, String fileName) throws IOException {
      final OutputStreamWriter bw = this.encoding != null ? new OutputStreamWriter(this.openBinary(pkg, fileName), this.encoding) : new OutputStreamWriter(this.openBinary(pkg, fileName));

      try {
         return new UnicodeEscapeWriter(bw) {
            private final CharsetEncoder encoder = EncoderFactory.createEncoder(bw.getEncoding());

            protected boolean requireEscaping(int ch) {
               if (ch < 32 && " \t\r\n".indexOf(ch) == -1) {
                  return true;
               } else if (ch < 128) {
                  return false;
               } else {
                  return !this.encoder.canEncode((char)ch);
               }
            }
         };
      } catch (Throwable var5) {
         return new UnicodeEscapeWriter(bw);
      }
   }

   public abstract void close() throws IOException;
}
