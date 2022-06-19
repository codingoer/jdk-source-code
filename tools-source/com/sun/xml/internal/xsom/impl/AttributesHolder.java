package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xml.sax.Locator;

public abstract class AttributesHolder extends DeclarationImpl {
   protected final Map attributes = new LinkedHashMap();
   protected final Set prohibitedAtts = new HashSet();
   protected final Set attGroups = new HashSet();

   protected AttributesHolder(SchemaDocumentImpl _parent, AnnotationImpl _annon, Locator loc, ForeignAttributesImpl _fa, String _name, boolean _anonymous) {
      super(_parent, _annon, loc, _fa, _parent.getTargetNamespace(), _name, _anonymous);
   }

   public abstract void setWildcard(WildcardImpl var1);

   public void addAttributeUse(UName name, AttributeUseImpl a) {
      this.attributes.put(name, a);
   }

   public void addProhibitedAttribute(UName name) {
      this.prohibitedAtts.add(name);
   }

   public Collection getAttributeUses() {
      List v = new ArrayList();
      v.addAll(this.attributes.values());
      Iterator var2 = this.getAttGroups().iterator();

      while(var2.hasNext()) {
         XSAttGroupDecl agd = (XSAttGroupDecl)var2.next();
         v.addAll(agd.getAttributeUses());
      }

      return v;
   }

   public Iterator iterateAttributeUses() {
      return this.getAttributeUses().iterator();
   }

   public XSAttributeUse getDeclaredAttributeUse(String nsURI, String localName) {
      return (XSAttributeUse)this.attributes.get(new UName(nsURI, localName));
   }

   public Iterator iterateDeclaredAttributeUses() {
      return this.attributes.values().iterator();
   }

   public Collection getDeclaredAttributeUses() {
      return this.attributes.values();
   }

   public void addAttGroup(Ref.AttGroup a) {
      this.attGroups.add(a);
   }

   public Iterator iterateAttGroups() {
      return new Iterators.Adapter(this.attGroups.iterator()) {
         protected XSAttGroupDecl filter(Ref.AttGroup u) {
            return u.get();
         }
      };
   }

   public Set getAttGroups() {
      return new AbstractSet() {
         public Iterator iterator() {
            return AttributesHolder.this.iterateAttGroups();
         }

         public int size() {
            return AttributesHolder.this.attGroups.size();
         }
      };
   }
}
