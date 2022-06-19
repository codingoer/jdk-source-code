package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.ForeignAttributes;
import com.sun.xml.internal.xsom.SCD;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.parser.SchemaDocument;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Locator;

public class SchemaImpl implements XSSchema {
   protected final SchemaSetImpl parent;
   private final String targetNamespace;
   private XSAnnotation annotation;
   private final Locator locator;
   private final Map atts = new HashMap();
   private final Map attsView;
   private final Map elems;
   private final Map elemsView;
   private final Map attGroups;
   private final Map attGroupsView;
   private final Map notations;
   private final Map notationsView;
   private final Map modelGroups;
   private final Map modelGroupsView;
   private final Map idConstraints;
   private final Map idConstraintsView;
   private final Map allTypes;
   private final Map allTypesView;
   private final Map simpleTypes;
   private final Map simpleTypesView;
   private final Map complexTypes;
   private final Map complexTypesView;
   private List foreignAttributes;
   private List readOnlyForeignAttributes;

   public SchemaImpl(SchemaSetImpl _parent, Locator loc, String tns) {
      this.attsView = Collections.unmodifiableMap(this.atts);
      this.elems = new HashMap();
      this.elemsView = Collections.unmodifiableMap(this.elems);
      this.attGroups = new HashMap();
      this.attGroupsView = Collections.unmodifiableMap(this.attGroups);
      this.notations = new HashMap();
      this.notationsView = Collections.unmodifiableMap(this.notations);
      this.modelGroups = new HashMap();
      this.modelGroupsView = Collections.unmodifiableMap(this.modelGroups);
      this.idConstraints = new HashMap();
      this.idConstraintsView = Collections.unmodifiableMap(this.idConstraints);
      this.allTypes = new HashMap();
      this.allTypesView = Collections.unmodifiableMap(this.allTypes);
      this.simpleTypes = new HashMap();
      this.simpleTypesView = Collections.unmodifiableMap(this.simpleTypes);
      this.complexTypes = new HashMap();
      this.complexTypesView = Collections.unmodifiableMap(this.complexTypes);
      this.foreignAttributes = null;
      this.readOnlyForeignAttributes = null;
      if (tns == null) {
         throw new IllegalArgumentException();
      } else {
         this.targetNamespace = tns;
         this.parent = _parent;
         this.locator = loc;
      }
   }

   public SchemaDocument getSourceDocument() {
      return null;
   }

   public SchemaSetImpl getRoot() {
      return this.parent;
   }

   public String getTargetNamespace() {
      return this.targetNamespace;
   }

   public XSSchema getOwnerSchema() {
      return this;
   }

   public void setAnnotation(XSAnnotation a) {
      this.annotation = a;
   }

   public XSAnnotation getAnnotation() {
      return this.annotation;
   }

   public XSAnnotation getAnnotation(boolean createIfNotExist) {
      if (createIfNotExist && this.annotation == null) {
         this.annotation = new AnnotationImpl();
      }

      return this.annotation;
   }

   public Locator getLocator() {
      return this.locator;
   }

   public void addAttributeDecl(XSAttributeDecl newDecl) {
      this.atts.put(newDecl.getName(), newDecl);
   }

   public Map getAttributeDecls() {
      return this.attsView;
   }

   public XSAttributeDecl getAttributeDecl(String name) {
      return (XSAttributeDecl)this.atts.get(name);
   }

   public Iterator iterateAttributeDecls() {
      return this.atts.values().iterator();
   }

   public void addElementDecl(XSElementDecl newDecl) {
      this.elems.put(newDecl.getName(), newDecl);
   }

   public Map getElementDecls() {
      return this.elemsView;
   }

   public XSElementDecl getElementDecl(String name) {
      return (XSElementDecl)this.elems.get(name);
   }

   public Iterator iterateElementDecls() {
      return this.elems.values().iterator();
   }

   public void addAttGroupDecl(XSAttGroupDecl newDecl, boolean overwrite) {
      if (overwrite || !this.attGroups.containsKey(newDecl.getName())) {
         this.attGroups.put(newDecl.getName(), newDecl);
      }

   }

   public Map getAttGroupDecls() {
      return this.attGroupsView;
   }

   public XSAttGroupDecl getAttGroupDecl(String name) {
      return (XSAttGroupDecl)this.attGroups.get(name);
   }

   public Iterator iterateAttGroupDecls() {
      return this.attGroups.values().iterator();
   }

   public void addNotation(XSNotation newDecl) {
      this.notations.put(newDecl.getName(), newDecl);
   }

   public Map getNotations() {
      return this.notationsView;
   }

   public XSNotation getNotation(String name) {
      return (XSNotation)this.notations.get(name);
   }

   public Iterator iterateNotations() {
      return this.notations.values().iterator();
   }

   public void addModelGroupDecl(XSModelGroupDecl newDecl, boolean overwrite) {
      if (overwrite || !this.modelGroups.containsKey(newDecl.getName())) {
         this.modelGroups.put(newDecl.getName(), newDecl);
      }

   }

   public Map getModelGroupDecls() {
      return this.modelGroupsView;
   }

   public XSModelGroupDecl getModelGroupDecl(String name) {
      return (XSModelGroupDecl)this.modelGroups.get(name);
   }

   public Iterator iterateModelGroupDecls() {
      return this.modelGroups.values().iterator();
   }

   protected void addIdentityConstraint(IdentityConstraintImpl c) {
      this.idConstraints.put(c.getName(), c);
   }

   public Map getIdentityConstraints() {
      return this.idConstraintsView;
   }

   public XSIdentityConstraint getIdentityConstraint(String localName) {
      return (XSIdentityConstraint)this.idConstraints.get(localName);
   }

   public void addSimpleType(XSSimpleType newDecl, boolean overwrite) {
      if (overwrite || !this.simpleTypes.containsKey(newDecl.getName())) {
         this.simpleTypes.put(newDecl.getName(), newDecl);
         this.allTypes.put(newDecl.getName(), newDecl);
      }

   }

   public Map getSimpleTypes() {
      return this.simpleTypesView;
   }

   public XSSimpleType getSimpleType(String name) {
      return (XSSimpleType)this.simpleTypes.get(name);
   }

   public Iterator iterateSimpleTypes() {
      return this.simpleTypes.values().iterator();
   }

   public void addComplexType(XSComplexType newDecl, boolean overwrite) {
      if (overwrite || !this.complexTypes.containsKey(newDecl.getName())) {
         this.complexTypes.put(newDecl.getName(), newDecl);
         this.allTypes.put(newDecl.getName(), newDecl);
      }

   }

   public Map getComplexTypes() {
      return this.complexTypesView;
   }

   public XSComplexType getComplexType(String name) {
      return (XSComplexType)this.complexTypes.get(name);
   }

   public Iterator iterateComplexTypes() {
      return this.complexTypes.values().iterator();
   }

   public Map getTypes() {
      return this.allTypesView;
   }

   public XSType getType(String name) {
      return (XSType)this.allTypes.get(name);
   }

   public Iterator iterateTypes() {
      return this.allTypes.values().iterator();
   }

   public void visit(XSVisitor visitor) {
      visitor.schema(this);
   }

   public Object apply(XSFunction function) {
      return function.schema(this);
   }

   public void addForeignAttributes(ForeignAttributesImpl fa) {
      if (this.foreignAttributes == null) {
         this.foreignAttributes = new ArrayList();
      }

      this.foreignAttributes.add(fa);
   }

   public List getForeignAttributes() {
      if (this.readOnlyForeignAttributes == null) {
         if (this.foreignAttributes == null) {
            this.readOnlyForeignAttributes = Collections.EMPTY_LIST;
         } else {
            this.readOnlyForeignAttributes = Collections.unmodifiableList(this.foreignAttributes);
         }
      }

      return this.readOnlyForeignAttributes;
   }

   public String getForeignAttribute(String nsUri, String localName) {
      Iterator var3 = this.getForeignAttributes().iterator();

      String v;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         ForeignAttributes fa = (ForeignAttributes)var3.next();
         v = fa.getValue(nsUri, localName);
      } while(v == null);

      return v;
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
}
