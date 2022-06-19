package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.tools.internal.ws.processor.model.ModelException;
import com.sun.tools.internal.ws.processor.model.java.JavaSimpleType;
import com.sun.tools.internal.ws.processor.model.java.JavaType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBMapping;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBModel;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBType;
import com.sun.tools.internal.ws.processor.util.ClassNameCollector;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.parser.DOMForestScanner;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.api.S2JJAXBModel;
import com.sun.tools.internal.xjc.api.SchemaCompiler;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;

public class JAXBModelBuilder {
   private final ErrorReceiver errReceiver;
   private final WsimportOptions options;
   private final MetadataFinder forest;
   private JAXBModel jaxbModel;
   private SchemaCompiler schemaCompiler;
   private final ClassNameAllocatorImpl _classNameAllocator;
   protected static final LocatorImpl NULL_LOCATOR = new LocatorImpl();

   public JAXBModelBuilder(WsimportOptions options, ClassNameCollector classNameCollector, MetadataFinder finder, ErrorReceiver errReceiver) {
      this._classNameAllocator = new ClassNameAllocatorImpl(classNameCollector);
      this.errReceiver = errReceiver;
      this.options = options;
      this.forest = finder;
      this.internalBuildJAXBModel();
   }

   private void internalBuildJAXBModel() {
      try {
         this.schemaCompiler = this.options.getSchemaCompiler();
         this.schemaCompiler.resetSchema();
         if (this.options.entityResolver != null) {
            this.schemaCompiler.setEntityResolver(this.options.entityResolver);
         }

         this.schemaCompiler.setClassNameAllocator(this._classNameAllocator);
         this.schemaCompiler.setErrorListener(this.errReceiver);
         int schemaElementCount = 1;
         Iterator var2 = this.forest.getInlinedSchemaElement().iterator();

         while(var2.hasNext()) {
            Element element = (Element)var2.next();
            String location = element.getOwnerDocument().getDocumentURI();
            String systemId = location + "#types?schema" + schemaElementCount++;
            if (this.forest.isMexMetadata) {
               this.schemaCompiler.parseSchema(systemId, element);
            } else {
               (new DOMForestScanner(this.forest)).scan(element, this.schemaCompiler.getParserHandler(systemId));
            }
         }

         InputSource[] externalBindings = this.options.getSchemaBindings();
         if (externalBindings != null) {
            InputSource[] var9 = externalBindings;
            int var10 = externalBindings.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               InputSource jaxbBinding = var9[var11];
               this.schemaCompiler.parseSchema(jaxbBinding);
            }
         }

      } catch (Exception var7) {
         throw new ModelException(var7);
      }
   }

   public JAXBType getJAXBType(QName qname) {
      JAXBMapping mapping = this.jaxbModel.get(qname);
      if (mapping == null) {
         return null;
      } else {
         JavaType javaType = new JavaSimpleType(mapping.getType());
         return new JAXBType(qname, javaType, mapping, this.jaxbModel);
      }
   }

   public TypeAndAnnotation getElementTypeAndAnn(QName qname) {
      JAXBMapping mapping = this.jaxbModel.get(qname);
      return mapping == null ? null : mapping.getType().getTypeAnn();
   }

   protected void bind() {
      S2JJAXBModel rawJaxbModel = this.schemaCompiler.bind();
      if (rawJaxbModel == null) {
         throw new AbortException();
      } else {
         this.options.setCodeModel(rawJaxbModel.generateCode((Plugin[])null, this.errReceiver));
         this.jaxbModel = new JAXBModel(rawJaxbModel);
         this.jaxbModel.setGeneratedClassNames(this._classNameAllocator.getJaxbGeneratedClasses());
      }
   }

   protected SchemaCompiler getJAXBSchemaCompiler() {
      return this.schemaCompiler;
   }

   public JAXBModel getJAXBModel() {
      return this.jaxbModel;
   }
}
