package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.SCD;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.parser.SchemaDocument;
import com.sun.xml.internal.xsom.util.ComponentNameFunction;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Locator;

public abstract class ComponentImpl implements XSComponent {
   protected final SchemaDocumentImpl ownerDocument;
   private AnnotationImpl annotation;
   private final Locator locator;
   private Object foreignAttributes;

   protected ComponentImpl(SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa) {
      this.ownerDocument = _owner;
      this.annotation = _annon;
      this.locator = _loc;
      this.foreignAttributes = fa;
   }

   public SchemaImpl getOwnerSchema() {
      return this.ownerDocument == null ? null : this.ownerDocument.getSchema();
   }

   public XSSchemaSet getRoot() {
      return this.ownerDocument == null ? null : this.getOwnerSchema().getRoot();
   }

   public SchemaDocument getSourceDocument() {
      return this.ownerDocument;
   }

   public final XSAnnotation getAnnotation() {
      return this.annotation;
   }

   public XSAnnotation getAnnotation(boolean createIfNotExist) {
      if (createIfNotExist && this.annotation == null) {
         this.annotation = new AnnotationImpl();
      }

      return this.annotation;
   }

   public final Locator getLocator() {
      return this.locator;
   }

   public List getForeignAttributes() {
      Object t = this.foreignAttributes;
      if (t == null) {
         return Collections.EMPTY_LIST;
      } else if (t instanceof List) {
         return (List)t;
      } else {
         t = this.foreignAttributes = this.convertToList((ForeignAttributesImpl)t);
         return (List)t;
      }
   }

   public String getForeignAttribute(String nsUri, String localName) {
      Iterator var3 = this.getForeignAttributes().iterator();

      String v;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         ForeignAttributesImpl fa = (ForeignAttributesImpl)var3.next();
         v = fa.getValue(nsUri, localName);
      } while(v == null);

      return v;
   }

   private List convertToList(ForeignAttributesImpl fa) {
      ArrayList lst;
      for(lst = new ArrayList(); fa != null; fa = fa.next) {
         lst.add(fa);
      }

      return Collections.unmodifiableList(lst);
   }

   public Collection select(String scd, NamespaceContext nsContext) {
      try {
         return SCD.create(scd, nsContext).select((XSComponent)this);
      } catch (ParseException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public XSComponent selectSingle(String scd, NamespaceContext nsContext) {
      try {
         return SCD.create(scd, nsContext).selectSingle((XSComponent)this);
      } catch (ParseException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public String toString() {
      return (String)this.apply(new ComponentNameFunction());
   }
}
