package com.sun.tools.internal.xjc;

import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.net.URL;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.SAXException;

public final class SchemaCache {
   private Schema schema;
   private final URL source;

   public SchemaCache(URL source) {
      this.source = source;
   }

   public ValidatorHandler newValidator() {
      synchronized(this) {
         if (this.schema == null) {
            try {
               SchemaFactory sf = XmlFactory.createSchemaFactory("http://www.w3.org/2001/XMLSchema", false);
               this.schema = XmlFactory.allowExternalAccess(sf, "file", false).newSchema(this.source);
            } catch (SAXException var4) {
               throw new AssertionError(var4);
            }
         }
      }

      ValidatorHandler handler = this.schema.newValidatorHandler();
      return handler;
   }
}
