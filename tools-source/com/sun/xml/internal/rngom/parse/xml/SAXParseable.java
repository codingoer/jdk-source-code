package com.sun.xml.internal.rngom.parse.xml;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.IncludedGrammar;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;
import com.sun.xml.internal.rngom.xml.sax.JAXPXMLReaderCreator;
import com.sun.xml.internal.rngom.xml.sax.XMLReaderCreator;
import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SAXParseable implements Parseable {
   private final InputSource in;
   final XMLReaderCreator xrc;
   final ErrorHandler eh;

   public SAXParseable(InputSource in, ErrorHandler eh, XMLReaderCreator xrc) {
      this.xrc = xrc;
      this.eh = eh;
      this.in = in;
   }

   public SAXParseable(InputSource in, ErrorHandler eh) {
      this(in, eh, new JAXPXMLReaderCreator());
   }

   public ParsedPattern parse(SchemaBuilder schemaBuilder) throws BuildException, IllegalSchemaException {
      try {
         XMLReader xr = this.xrc.createXMLReader();
         SchemaParser sp = new SchemaParser(this, xr, this.eh, schemaBuilder, (IncludedGrammar)null, (Scope)null, "");
         xr.parse(this.in);
         ParsedPattern p = sp.getParsedPattern();
         return schemaBuilder.expandPattern(p);
      } catch (SAXException var5) {
         throw toBuildException(var5);
      } catch (IOException var6) {
         throw new BuildException(var6);
      }
   }

   public ParsedPattern parseInclude(String uri, SchemaBuilder schemaBuilder, IncludedGrammar g, String inheritedNs) throws BuildException, IllegalSchemaException {
      try {
         XMLReader xr = this.xrc.createXMLReader();
         SchemaParser sp = new SchemaParser(this, xr, this.eh, schemaBuilder, g, g, inheritedNs);
         xr.parse(makeInputSource(xr, uri));
         return sp.getParsedPattern();
      } catch (SAXException var7) {
         throw toBuildException(var7);
      } catch (IOException var8) {
         throw new BuildException(var8);
      }
   }

   public ParsedPattern parseExternal(String uri, SchemaBuilder schemaBuilder, Scope s, String inheritedNs) throws BuildException, IllegalSchemaException {
      try {
         XMLReader xr = this.xrc.createXMLReader();
         SchemaParser sp = new SchemaParser(this, xr, this.eh, schemaBuilder, (IncludedGrammar)null, s, inheritedNs);
         xr.parse(makeInputSource(xr, uri));
         return sp.getParsedPattern();
      } catch (SAXException var7) {
         throw toBuildException(var7);
      } catch (IOException var8) {
         throw new BuildException(var8);
      }
   }

   private static InputSource makeInputSource(XMLReader xr, String systemId) throws IOException, SAXException {
      EntityResolver er = xr.getEntityResolver();
      if (er != null) {
         InputSource inputSource = er.resolveEntity((String)null, systemId);
         if (inputSource != null) {
            return inputSource;
         }
      }

      return new InputSource(systemId);
   }

   static BuildException toBuildException(SAXException e) {
      Exception inner = e.getException();
      if (inner instanceof BuildException) {
         throw (BuildException)inner;
      } else {
         throw new BuildException(e);
      }
   }
}
