package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.internal.xjc.Options;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import com.sun.xml.internal.xsom.parser.AnnotationParser;
import com.sun.xml.internal.xsom.parser.AnnotationParserFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLFilterImpl;

public class AnnotationParserFactoryImpl implements AnnotationParserFactory {
   private final Options options;
   private ValidatorHandler validator;

   public AnnotationParserFactoryImpl(Options opts) {
      this.options = opts;
   }

   public AnnotationParser create() {
      return new AnnotationParser() {
         private Unmarshaller u = BindInfo.getCustomizationUnmarshaller();
         private UnmarshallerHandler handler;

         public ContentHandler getContentHandler(AnnotationContext context, String parentElementName, final ErrorHandler errorHandler, EntityResolver entityResolver) {
            if (this.handler != null) {
               throw new AssertionError();
            } else {
               if (AnnotationParserFactoryImpl.this.options.debugMode) {
                  try {
                     this.u.setEventHandler(new DefaultValidationEventHandler());
                  } catch (JAXBException var6) {
                     throw new AssertionError(var6);
                  }
               }

               this.handler = this.u.getUnmarshallerHandler();
               return new ForkingFilter(this.handler) {
                  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                     super.startElement(uri, localName, qName, atts);
                     if ((uri.equals("http://java.sun.com/xml/ns/jaxb") || uri.equals("http://java.sun.com/xml/ns/jaxb/xjc")) && this.getSideHandler() == null) {
                        if (AnnotationParserFactoryImpl.this.validator == null) {
                           AnnotationParserFactoryImpl.this.validator = BindInfo.bindingFileSchema.newValidator();
                        }

                        AnnotationParserFactoryImpl.this.validator.setErrorHandler(errorHandler);
                        this.startForking(uri, localName, qName, atts, new ValidatorProtecter(AnnotationParserFactoryImpl.this.validator));
                     }

                     for(int i = atts.getLength() - 1; i >= 0; --i) {
                        if (atts.getURI(i).equals("http://www.w3.org/2005/05/xmlmime") && atts.getLocalName(i).equals("expectedContentTypes")) {
                           errorHandler.warning(new SAXParseException(com.sun.tools.internal.xjc.reader.xmlschema.Messages.format("UnusedCustomizationChecker.WarnUnusedExpectedContentTypes"), this.getDocumentLocator()));
                        }
                     }

                  }
               };
            }
         }

         public BindInfo getResult(Object existing) {
            if (this.handler == null) {
               throw new AssertionError();
            } else {
               try {
                  BindInfo result = (BindInfo)this.handler.getResult();
                  if (existing != null) {
                     BindInfo bie = (BindInfo)existing;
                     bie.absorb(result);
                     return bie;
                  } else {
                     return !result.isPointless() ? result : null;
                  }
               } catch (JAXBException var4) {
                  throw new AssertionError(var4);
               }
            }
         }
      };
   }

   private static final class ValidatorProtecter extends XMLFilterImpl {
      public ValidatorProtecter(ContentHandler h) {
         this.setContentHandler(h);
      }

      public void startPrefixMapping(String prefix, String uri) throws SAXException {
         super.startPrefixMapping(prefix.intern(), uri);
      }
   }
}
