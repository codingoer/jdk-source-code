package com.sun.xml.internal.rngom.digested;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class DAnnotation {
   static final DAnnotation EMPTY = new DAnnotation();
   final Map attributes = new HashMap();
   final List contents = new ArrayList();

   public Attribute getAttribute(String nsUri, String localName) {
      return this.getAttribute(new QName(nsUri, localName));
   }

   public Attribute getAttribute(QName n) {
      return (Attribute)this.attributes.get(n);
   }

   public Map getAttributes() {
      return Collections.unmodifiableMap(this.attributes);
   }

   public List getChildren() {
      return Collections.unmodifiableList(this.contents);
   }

   public static class Attribute {
      private final String ns;
      private final String localName;
      private final String prefix;
      private String value;
      private Locator loc;

      public Attribute(String ns, String localName, String prefix) {
         this.ns = ns;
         this.localName = localName;
         this.prefix = prefix;
      }

      public Attribute(String ns, String localName, String prefix, String value, Locator loc) {
         this.ns = ns;
         this.localName = localName;
         this.prefix = prefix;
         this.value = value;
         this.loc = loc;
      }

      public String getNs() {
         return this.ns;
      }

      public String getLocalName() {
         return this.localName;
      }

      public String getPrefix() {
         return this.prefix;
      }

      public String getValue() {
         return this.value;
      }

      public Locator getLoc() {
         return this.loc;
      }
   }
}
