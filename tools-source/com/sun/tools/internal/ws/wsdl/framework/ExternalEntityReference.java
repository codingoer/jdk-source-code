package com.sun.tools.internal.ws.wsdl.framework;

import javax.xml.namespace.QName;

public class ExternalEntityReference {
   private AbstractDocument _document;
   private Kind _kind;
   private QName _name;

   public ExternalEntityReference(AbstractDocument document, Kind kind, QName name) {
      this._document = document;
      this._kind = kind;
      this._name = name;
   }

   public AbstractDocument getDocument() {
      return this._document;
   }

   public Kind getKind() {
      return this._kind;
   }

   public QName getName() {
      return this._name;
   }

   public GloballyKnown resolve() {
      return this._document.find(this._kind, this._name);
   }
}
