package com.sun.xml.internal.rngom.parse.compact;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.IncludedGrammar;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;
import com.sun.xml.internal.rngom.xml.util.EncodingMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.net.URL;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public class CompactParseable implements Parseable {
   private final InputSource in;
   private final ErrorHandler eh;
   private static final String UTF8 = EncodingMap.getJavaName("UTF-8");
   private static final String UTF16 = EncodingMap.getJavaName("UTF-16");

   public CompactParseable(InputSource in, ErrorHandler eh) {
      this.in = in;
      this.eh = eh;
   }

   public ParsedPattern parse(SchemaBuilder sb) throws BuildException, IllegalSchemaException {
      ParsedPattern p = (new CompactSyntax(this, makeReader(this.in), this.in.getSystemId(), sb, this.eh, "")).parse((Scope)null);
      return sb.expandPattern(p);
   }

   public ParsedPattern parseInclude(String uri, SchemaBuilder sb, IncludedGrammar g, String inheritedNs) throws BuildException, IllegalSchemaException {
      InputSource tem = new InputSource(uri);
      tem.setEncoding(this.in.getEncoding());
      return (new CompactSyntax(this, makeReader(tem), uri, sb, this.eh, inheritedNs)).parseInclude(g);
   }

   public ParsedPattern parseExternal(String uri, SchemaBuilder sb, Scope scope, String inheritedNs) throws BuildException, IllegalSchemaException {
      InputSource tem = new InputSource(uri);
      tem.setEncoding(this.in.getEncoding());
      return (new CompactSyntax(this, makeReader(tem), uri, sb, this.eh, inheritedNs)).parse(scope);
   }

   private static Reader makeReader(InputSource is) throws BuildException {
      try {
         Reader r = is.getCharacterStream();
         if (r == null) {
            InputStream in = is.getByteStream();
            String encoding;
            if (in == null) {
               encoding = is.getSystemId();
               in = (new URL(encoding)).openStream();
            }

            encoding = is.getEncoding();
            if (encoding == null) {
               PushbackInputStream pb = new PushbackInputStream((InputStream)in, 2);
               encoding = detectEncoding(pb);
               in = pb;
            }

            r = new InputStreamReader((InputStream)in, encoding);
         }

         return (Reader)r;
      } catch (IOException var5) {
         throw new BuildException(var5);
      }
   }

   private static String detectEncoding(PushbackInputStream in) throws IOException {
      String encoding = UTF8;
      int b1 = in.read();
      if (b1 != -1) {
         int b2 = in.read();
         if (b2 != -1) {
            in.unread(b2);
            if (b1 == 255 && b2 == 254 || b1 == 254 && b2 == 255) {
               encoding = UTF16;
            }
         }

         in.unread(b1);
      }

      return encoding;
   }
}
