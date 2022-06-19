package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.util.CheckingSchemaBuilder;
import com.sun.xml.internal.rngom.parse.Parseable;
import com.sun.xml.internal.rngom.parse.compact.CompactParseable;
import com.sun.xml.internal.rngom.parse.xml.SAXParseable;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Main {
   public static void main(String[] args) throws Exception {
      ErrorHandler eh = new DefaultHandler() {
         public void error(SAXParseException e) throws SAXException {
            throw e;
         }
      };
      Object p;
      if (args[0].endsWith(".rng")) {
         p = new SAXParseable(new InputSource(args[0]), eh);
      } else {
         p = new CompactParseable(new InputSource(args[0]), eh);
      }

      SchemaBuilder sb = new CheckingSchemaBuilder(new DSchemaBuilderImpl(), eh);

      try {
         ((Parseable)p).parse(sb);
      } catch (BuildException var6) {
         if (var6.getCause() instanceof SAXException) {
            SAXException se = (SAXException)var6.getCause();
            if (se.getException() != null) {
               se.getException().printStackTrace();
            }
         }

         throw var6;
      }
   }
}
