package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.impl.SchemaImpl;
import com.sun.xml.internal.xsom.parser.SchemaDocument;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class SchemaDocumentImpl implements SchemaDocument {
   private final SchemaImpl schema;
   private final String schemaDocumentURI;
   final Set references = new HashSet();
   final Set referers = new HashSet();

   protected SchemaDocumentImpl(SchemaImpl schema, String _schemaDocumentURI) {
      this.schema = schema;
      this.schemaDocumentURI = _schemaDocumentURI;
   }

   public String getSystemId() {
      return this.schemaDocumentURI;
   }

   public String getTargetNamespace() {
      return this.schema.getTargetNamespace();
   }

   public SchemaImpl getSchema() {
      return this.schema;
   }

   public Set getReferencedDocuments() {
      return Collections.unmodifiableSet(this.references);
   }

   public Set getIncludedDocuments() {
      return this.getImportedDocuments(this.getTargetNamespace());
   }

   public Set getImportedDocuments(String targetNamespace) {
      if (targetNamespace == null) {
         throw new IllegalArgumentException();
      } else {
         Set r = new HashSet();
         Iterator var3 = this.references.iterator();

         while(var3.hasNext()) {
            SchemaDocumentImpl doc = (SchemaDocumentImpl)var3.next();
            if (doc.getTargetNamespace().equals(targetNamespace)) {
               r.add(doc);
            }
         }

         return Collections.unmodifiableSet(r);
      }
   }

   public boolean includes(SchemaDocument doc) {
      if (!this.references.contains(doc)) {
         return false;
      } else {
         return doc.getSchema() == this.schema;
      }
   }

   public boolean imports(SchemaDocument doc) {
      if (!this.references.contains(doc)) {
         return false;
      } else {
         return doc.getSchema() != this.schema;
      }
   }

   public Set getReferers() {
      return Collections.unmodifiableSet(this.referers);
   }

   public boolean equals(Object o) {
      SchemaDocumentImpl rhs = (SchemaDocumentImpl)o;
      if (this.schemaDocumentURI != null && rhs.schemaDocumentURI != null) {
         if (!this.schemaDocumentURI.equals(rhs.schemaDocumentURI)) {
            return false;
         } else {
            return this.schema == rhs.schema;
         }
      } else {
         return this == rhs;
      }
   }

   public int hashCode() {
      if (this.schemaDocumentURI == null) {
         return super.hashCode();
      } else {
         return this.schema == null ? this.schemaDocumentURI.hashCode() : this.schemaDocumentURI.hashCode() ^ this.schema.hashCode();
      }
   }
}
