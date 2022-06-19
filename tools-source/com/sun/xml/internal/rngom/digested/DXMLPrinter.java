package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.util.CheckingSchemaBuilder;
import com.sun.xml.internal.rngom.nc.NameClass;
import com.sun.xml.internal.rngom.nc.NameClassVisitor;
import com.sun.xml.internal.rngom.nc.SimpleNameClass;
import com.sun.xml.internal.rngom.parse.Parseable;
import com.sun.xml.internal.rngom.parse.compact.CompactParseable;
import com.sun.xml.internal.rngom.parse.xml.SAXParseable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class DXMLPrinter {
   protected XMLStreamWriter out;
   protected String indentStep = "\t";
   protected String newLine = System.getProperty("line.separator");
   protected int indent;
   protected boolean afterEnd = false;
   protected DXMLPrinterVisitor visitor;
   protected NameClassXMLPrinterVisitor ncVisitor;
   protected DOMPrinter domPrinter;

   public DXMLPrinter(XMLStreamWriter out) {
      this.out = out;
      this.visitor = new DXMLPrinterVisitor();
      this.ncVisitor = new NameClassXMLPrinterVisitor();
      this.domPrinter = new DOMPrinter(out);
   }

   public void printDocument(DGrammarPattern grammar) throws XMLStreamException {
      try {
         this.visitor.startDocument();
         this.visitor.on((DPattern)grammar);
         this.visitor.endDocument();
      } catch (XMLWriterException var3) {
         if (var3.getCause() instanceof XMLStreamException) {
            throw (XMLStreamException)var3.getCause();
         } else {
            throw new XMLStreamException(var3);
         }
      }
   }

   public void print(DPattern pattern) throws XMLStreamException {
      try {
         pattern.accept(this.visitor);
      } catch (XMLWriterException var3) {
         if (var3.getCause() instanceof XMLStreamException) {
            throw (XMLStreamException)var3.getCause();
         } else {
            throw new XMLStreamException(var3);
         }
      }
   }

   public void print(NameClass nc) throws XMLStreamException {
      try {
         nc.accept(this.ncVisitor);
      } catch (XMLWriterException var3) {
         if (var3.getCause() instanceof XMLStreamException) {
            throw (XMLStreamException)var3.getCause();
         } else {
            throw new XMLStreamException(var3);
         }
      }
   }

   public void print(Node node) throws XMLStreamException {
      this.domPrinter.print(node);
   }

   public static void main(String[] args) throws Exception {
      ErrorHandler eh = new DefaultHandler() {
         public void error(SAXParseException e) throws SAXException {
            throw e;
         }
      };
      String path = (new File(args[0])).toURL().toString();
      Object p;
      if (args[0].endsWith(".rng")) {
         p = new SAXParseable(new InputSource(path), eh);
      } else {
         p = new CompactParseable(new InputSource(path), eh);
      }

      SchemaBuilder sb = new CheckingSchemaBuilder(new DSchemaBuilderImpl(), eh);

      try {
         DGrammarPattern grammar = (DGrammarPattern)((Parseable)p).parse(sb);
         OutputStream out = new FileOutputStream(args[1]);
         XMLOutputFactory factory = XMLOutputFactory.newInstance();
         factory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
         XMLStreamWriter output = factory.createXMLStreamWriter(out);
         DXMLPrinter printer = new DXMLPrinter(output);
         printer.printDocument(grammar);
         output.close();
         out.close();
      } catch (BuildException var10) {
         if (var10.getCause() instanceof SAXParseException) {
            SAXParseException se = (SAXParseException)var10.getCause();
            System.out.println("(" + se.getLineNumber() + "," + se.getColumnNumber() + "): " + se.getMessage());
         } else {
            if (var10.getCause() instanceof SAXException) {
               SAXException se = (SAXException)var10.getCause();
               if (se.getException() != null) {
                  se.getException().printStackTrace();
               }
            }

            throw var10;
         }
      }
   }

   protected class NameClassXMLPrinterVisitor extends XMLWriter implements NameClassVisitor {
      protected NameClassXMLPrinterVisitor() {
         super();
      }

      public Void visitChoice(NameClass nc1, NameClass nc2) {
         this.start("choice");
         nc1.accept(this);
         nc2.accept(this);
         this.end();
         return null;
      }

      public Void visitNsName(String ns) {
         this.start("nsName");
         this.attr("ns", ns);
         this.end();
         return null;
      }

      public Void visitNsNameExcept(String ns, NameClass nc) {
         this.start("nsName");
         this.attr("ns", ns);
         this.start("except");
         nc.accept(this);
         this.end();
         this.end();
         return null;
      }

      public Void visitAnyName() {
         this.start("anyName");
         this.end();
         return null;
      }

      public Void visitAnyNameExcept(NameClass nc) {
         this.start("anyName");
         this.start("except");
         nc.accept(this);
         this.end();
         this.end();
         return null;
      }

      public Void visitName(QName name) {
         this.start("name");
         if (!name.getPrefix().equals("")) {
            this.body(name.getPrefix() + ":");
         }

         this.body(name.getLocalPart());
         this.end();
         return null;
      }

      public Void visitNull() {
         throw new UnsupportedOperationException("visitNull");
      }
   }

   protected class DXMLPrinterVisitor extends XMLWriter implements DPatternVisitor {
      protected DXMLPrinterVisitor() {
         super();
      }

      protected void on(DPattern p) {
         p.accept(this);
      }

      protected void unwrapGroup(DPattern p) {
         if (p instanceof DGroupPattern && p.getAnnotation() == DAnnotation.EMPTY) {
            Iterator var2 = ((DGroupPattern)p).iterator();

            while(var2.hasNext()) {
               DPattern d = (DPattern)var2.next();
               this.on(d);
            }
         } else {
            this.on(p);
         }

      }

      protected void unwrapChoice(DPattern p) {
         if (p instanceof DChoicePattern && p.getAnnotation() == DAnnotation.EMPTY) {
            Iterator var2 = ((DChoicePattern)p).iterator();

            while(var2.hasNext()) {
               DPattern d = (DPattern)var2.next();
               this.on(d);
            }
         } else {
            this.on(p);
         }

      }

      protected void on(NameClass nc) {
         if (nc instanceof SimpleNameClass) {
            QName qname = ((SimpleNameClass)nc).name;
            String name = qname.getLocalPart();
            if (!qname.getPrefix().equals("")) {
               name = qname.getPrefix() + ":";
            }

            this.attr("name", name);
         } else {
            nc.accept(DXMLPrinter.this.ncVisitor);
         }

      }

      protected void on(DAnnotation ann) {
         if (ann != DAnnotation.EMPTY) {
            Iterator var2 = ann.getAttributes().values().iterator();

            while(var2.hasNext()) {
               DAnnotation.Attribute attr = (DAnnotation.Attribute)var2.next();
               this.attr(attr.getPrefix(), attr.getNs(), attr.getLocalName(), attr.getValue());
            }

            var2 = ann.getChildren().iterator();

            while(var2.hasNext()) {
               Element elem = (Element)var2.next();

               try {
                  this.newLine();
                  this.indent();
                  DXMLPrinter.this.print((Node)elem);
               } catch (XMLStreamException var5) {
                  throw DXMLPrinter.this.new XMLWriterException(var5);
               }
            }

         }
      }

      public Void onAttribute(DAttributePattern p) {
         this.start("attribute");
         this.on(p.getName());
         this.on(p.getAnnotation());
         DPattern child = p.getChild();
         if (!(child instanceof DTextPattern)) {
            this.on(p.getChild());
         }

         this.end();
         return null;
      }

      public Void onChoice(DChoicePattern p) {
         this.start("choice");
         this.on(p.getAnnotation());
         Iterator var2 = p.iterator();

         while(var2.hasNext()) {
            DPattern d = (DPattern)var2.next();
            this.on(d);
         }

         this.end();
         return null;
      }

      public Void onData(DDataPattern p) {
         List params = p.getParams();
         DPattern except = p.getExcept();
         this.start("data");
         this.attr("datatypeLibrary", p.getDatatypeLibrary());
         this.attr("type", p.getType());
         this.on(p.getAnnotation());
         Iterator var4 = params.iterator();

         while(var4.hasNext()) {
            DDataPattern.Param param = (DDataPattern.Param)var4.next();
            this.start("param");
            this.attr("ns", param.getNs());
            this.attr("name", param.getName());
            this.body(param.getValue());
            this.end();
         }

         if (except != null) {
            this.start("except");
            this.unwrapChoice(except);
            this.end();
         }

         this.end();
         return null;
      }

      public Void onElement(DElementPattern p) {
         this.start("element");
         this.on(p.getName());
         this.on(p.getAnnotation());
         this.unwrapGroup(p.getChild());
         this.end();
         return null;
      }

      public Void onEmpty(DEmptyPattern p) {
         this.start("empty");
         this.on(p.getAnnotation());
         this.end();
         return null;
      }

      public Void onGrammar(DGrammarPattern p) {
         this.start("grammar");
         this.ns((String)null, "http://relaxng.org/ns/structure/1.0");
         this.on(p.getAnnotation());
         this.start("start");
         this.on(p.getStart());
         this.end();
         Iterator var2 = p.iterator();

         while(var2.hasNext()) {
            DDefine d = (DDefine)var2.next();
            this.start("define");
            this.attr("name", d.getName());
            this.on(d.getAnnotation());
            this.unwrapGroup(d.getPattern());
            this.end();
         }

         this.end();
         return null;
      }

      public Void onGroup(DGroupPattern p) {
         this.start("group");
         this.on(p.getAnnotation());
         Iterator var2 = p.iterator();

         while(var2.hasNext()) {
            DPattern d = (DPattern)var2.next();
            this.on(d);
         }

         this.end();
         return null;
      }

      public Void onInterleave(DInterleavePattern p) {
         this.start("interleave");
         this.on(p.getAnnotation());
         Iterator var2 = p.iterator();

         while(var2.hasNext()) {
            DPattern d = (DPattern)var2.next();
            this.on(d);
         }

         this.end();
         return null;
      }

      public Void onList(DListPattern p) {
         this.start("list");
         this.on(p.getAnnotation());
         this.unwrapGroup(p.getChild());
         this.end();
         return null;
      }

      public Void onMixed(DMixedPattern p) {
         this.start("mixed");
         this.on(p.getAnnotation());
         this.unwrapGroup(p.getChild());
         this.end();
         return null;
      }

      public Void onNotAllowed(DNotAllowedPattern p) {
         this.start("notAllowed");
         this.on(p.getAnnotation());
         this.end();
         return null;
      }

      public Void onOneOrMore(DOneOrMorePattern p) {
         this.start("oneOrMore");
         this.on(p.getAnnotation());
         this.unwrapGroup(p.getChild());
         this.end();
         return null;
      }

      public Void onOptional(DOptionalPattern p) {
         this.start("optional");
         this.on(p.getAnnotation());
         this.unwrapGroup(p.getChild());
         this.end();
         return null;
      }

      public Void onRef(DRefPattern p) {
         this.start("ref");
         this.attr("name", p.getName());
         this.on(p.getAnnotation());
         this.end();
         return null;
      }

      public Void onText(DTextPattern p) {
         this.start("text");
         this.on(p.getAnnotation());
         this.end();
         return null;
      }

      public Void onValue(DValuePattern p) {
         this.start("value");
         if (!p.getNs().equals("")) {
            this.attr("ns", p.getNs());
         }

         this.attr("datatypeLibrary", p.getDatatypeLibrary());
         this.attr("type", p.getType());
         this.on(p.getAnnotation());
         this.body(p.getValue());
         this.end();
         return null;
      }

      public Void onZeroOrMore(DZeroOrMorePattern p) {
         this.start("zeroOrMore");
         this.on(p.getAnnotation());
         this.unwrapGroup(p.getChild());
         this.end();
         return null;
      }
   }

   protected class XMLWriter {
      protected void newLine() {
         try {
            DXMLPrinter.this.out.writeCharacters(DXMLPrinter.this.newLine);
         } catch (XMLStreamException var2) {
            throw DXMLPrinter.this.new XMLWriterException(var2);
         }
      }

      protected void indent() {
         try {
            for(int i = 0; i < DXMLPrinter.this.indent; ++i) {
               DXMLPrinter.this.out.writeCharacters(DXMLPrinter.this.indentStep);
            }

         } catch (XMLStreamException var2) {
            throw DXMLPrinter.this.new XMLWriterException(var2);
         }
      }

      public void startDocument() {
         try {
            DXMLPrinter.this.out.writeStartDocument();
         } catch (XMLStreamException var2) {
            throw DXMLPrinter.this.new XMLWriterException(var2);
         }
      }

      public void endDocument() {
         try {
            DXMLPrinter.this.out.writeEndDocument();
         } catch (XMLStreamException var2) {
            throw DXMLPrinter.this.new XMLWriterException(var2);
         }
      }

      public final void start(String element) {
         try {
            this.newLine();
            this.indent();
            DXMLPrinter.this.out.writeStartElement(element);
            ++DXMLPrinter.this.indent;
            DXMLPrinter.this.afterEnd = false;
         } catch (XMLStreamException var3) {
            throw DXMLPrinter.this.new XMLWriterException(var3);
         }
      }

      public void end() {
         try {
            --DXMLPrinter.this.indent;
            if (DXMLPrinter.this.afterEnd) {
               this.newLine();
               this.indent();
            }

            DXMLPrinter.this.out.writeEndElement();
            DXMLPrinter.this.afterEnd = true;
         } catch (XMLStreamException var2) {
            throw DXMLPrinter.this.new XMLWriterException(var2);
         }
      }

      public void attr(String prefix, String ns, String name, String value) {
         try {
            DXMLPrinter.this.out.writeAttribute(prefix, ns, name, value);
         } catch (XMLStreamException var6) {
            throw DXMLPrinter.this.new XMLWriterException(var6);
         }
      }

      public void attr(String name, String value) {
         try {
            DXMLPrinter.this.out.writeAttribute(name, value);
         } catch (XMLStreamException var4) {
            throw DXMLPrinter.this.new XMLWriterException(var4);
         }
      }

      public void ns(String prefix, String uri) {
         try {
            DXMLPrinter.this.out.writeNamespace(prefix, uri);
         } catch (XMLStreamException var4) {
            throw DXMLPrinter.this.new XMLWriterException(var4);
         }
      }

      public void body(String text) {
         try {
            DXMLPrinter.this.out.writeCharacters(text);
            DXMLPrinter.this.afterEnd = false;
         } catch (XMLStreamException var3) {
            throw DXMLPrinter.this.new XMLWriterException(var3);
         }
      }
   }

   protected class XMLWriterException extends RuntimeException {
      protected XMLWriterException(Throwable cause) {
         super(cause);
      }
   }
}
