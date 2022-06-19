package com.sun.xml.internal.rngom.parse.xml;

import java.util.Hashtable;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public abstract class DtdContext implements DTDHandler, ValidationContext {
   private final Hashtable notationTable;
   private final Hashtable unparsedEntityTable;

   public DtdContext() {
      this.notationTable = new Hashtable();
      this.unparsedEntityTable = new Hashtable();
   }

   public DtdContext(DtdContext dc) {
      this.notationTable = dc.notationTable;
      this.unparsedEntityTable = dc.unparsedEntityTable;
   }

   public void notationDecl(String name, String publicId, String systemId) throws SAXException {
      this.notationTable.put(name, name);
   }

   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
      this.unparsedEntityTable.put(name, name);
   }

   public boolean isNotation(String notationName) {
      return this.notationTable.get(notationName) != null;
   }

   public boolean isUnparsedEntity(String entityName) {
      return this.unparsedEntityTable.get(entityName) != null;
   }

   public void clearDtdContext() {
      this.notationTable.clear();
      this.unparsedEntityTable.clear();
   }
}
