package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.SCD;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSVariety;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import com.sun.xml.internal.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Locator;

public class SchemaSetImpl implements XSSchemaSet {
   private final Map schemas = new HashMap();
   private final Vector schemas2 = new Vector();
   private final List readonlySchemaList;
   public final EmptyImpl empty;
   public final AnySimpleType anySimpleType;
   public final AnyType anyType;

   public SchemaSetImpl() {
      this.readonlySchemaList = Collections.unmodifiableList(this.schemas2);
      this.empty = new EmptyImpl();
      this.anySimpleType = new AnySimpleType();
      this.anyType = new AnyType();
   }

   public SchemaImpl createSchema(String targetNamespace, Locator location) {
      SchemaImpl obj = (SchemaImpl)this.schemas.get(targetNamespace);
      if (obj == null) {
         obj = new SchemaImpl(this, location, targetNamespace);
         this.schemas.put(targetNamespace, obj);
         this.schemas2.add(obj);
      }

      return obj;
   }

   public int getSchemaSize() {
      return this.schemas.size();
   }

   public XSSchema getSchema(String targetNamespace) {
      return (XSSchema)this.schemas.get(targetNamespace);
   }

   public XSSchema getSchema(int idx) {
      return (XSSchema)this.schemas2.get(idx);
   }

   public Iterator iterateSchema() {
      return this.schemas2.iterator();
   }

   public final Collection getSchemas() {
      return this.readonlySchemaList;
   }

   public XSType getType(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getType(localName);
   }

   public XSSimpleType getSimpleType(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getSimpleType(localName);
   }

   public XSElementDecl getElementDecl(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getElementDecl(localName);
   }

   public XSAttributeDecl getAttributeDecl(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getAttributeDecl(localName);
   }

   public XSModelGroupDecl getModelGroupDecl(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getModelGroupDecl(localName);
   }

   public XSAttGroupDecl getAttGroupDecl(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getAttGroupDecl(localName);
   }

   public XSComplexType getComplexType(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getComplexType(localName);
   }

   public XSIdentityConstraint getIdentityConstraint(String ns, String localName) {
      XSSchema schema = this.getSchema(ns);
      return schema == null ? null : schema.getIdentityConstraint(localName);
   }

   public Iterator iterateElementDecls() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateElementDecls();
         }
      };
   }

   public Iterator iterateTypes() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateTypes();
         }
      };
   }

   public Iterator iterateAttributeDecls() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateAttributeDecls();
         }
      };
   }

   public Iterator iterateAttGroupDecls() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateAttGroupDecls();
         }
      };
   }

   public Iterator iterateModelGroupDecls() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateModelGroupDecls();
         }
      };
   }

   public Iterator iterateSimpleTypes() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateSimpleTypes();
         }
      };
   }

   public Iterator iterateComplexTypes() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateComplexTypes();
         }
      };
   }

   public Iterator iterateNotations() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.iterateNotations();
         }
      };
   }

   public Iterator iterateIdentityConstraints() {
      return new Iterators.Map(this.iterateSchema()) {
         protected Iterator apply(XSSchema u) {
            return u.getIdentityConstraints().values().iterator();
         }
      };
   }

   public Collection select(String scd, NamespaceContext nsContext) {
      try {
         return SCD.create(scd, nsContext).select((XSSchemaSet)this);
      } catch (ParseException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public XSComponent selectSingle(String scd, NamespaceContext nsContext) {
      try {
         return SCD.create(scd, nsContext).selectSingle((XSSchemaSet)this);
      } catch (ParseException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public XSContentType getEmpty() {
      return this.empty;
   }

   public XSSimpleType getAnySimpleType() {
      return this.anySimpleType;
   }

   public XSComplexType getAnyType() {
      return this.anyType;
   }

   private class AnyType extends DeclarationImpl implements XSComplexType, Ref.Type {
      private final WildcardImpl anyWildcard = new WildcardImpl.Any((SchemaDocumentImpl)null, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, 3);
      private final XSContentType contentType;

      AnyType() {
         super((SchemaDocumentImpl)null, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, "http://www.w3.org/2001/XMLSchema", "anyType", false);
         this.contentType = new ParticleImpl((SchemaDocumentImpl)null, (AnnotationImpl)null, new ModelGroupImpl((SchemaDocumentImpl)null, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, XSModelGroup.SEQUENCE, new ParticleImpl[]{new ParticleImpl((SchemaDocumentImpl)null, (AnnotationImpl)null, this.anyWildcard, (Locator)null, -1, 0)}), (Locator)null, 1, 1);
      }

      public SchemaImpl getOwnerSchema() {
         return SchemaSetImpl.this.createSchema("http://www.w3.org/2001/XMLSchema", (Locator)null);
      }

      public boolean isAbstract() {
         return false;
      }

      public XSWildcard getAttributeWildcard() {
         return this.anyWildcard;
      }

      public XSAttributeUse getAttributeUse(String nsURI, String localName) {
         return null;
      }

      public Iterator iterateAttributeUses() {
         return Iterators.empty();
      }

      public XSAttributeUse getDeclaredAttributeUse(String nsURI, String localName) {
         return null;
      }

      public Iterator iterateDeclaredAttributeUses() {
         return Iterators.empty();
      }

      public Iterator iterateAttGroups() {
         return Iterators.empty();
      }

      public Collection getAttributeUses() {
         return Collections.EMPTY_LIST;
      }

      public Collection getDeclaredAttributeUses() {
         return Collections.EMPTY_LIST;
      }

      public Collection getAttGroups() {
         return Collections.EMPTY_LIST;
      }

      public boolean isFinal(int i) {
         return false;
      }

      public boolean isSubstitutionProhibited(int i) {
         return false;
      }

      public boolean isMixed() {
         return true;
      }

      public XSContentType getContentType() {
         return this.contentType;
      }

      public XSContentType getExplicitContent() {
         return null;
      }

      public XSType getBaseType() {
         return this;
      }

      public XSSimpleType asSimpleType() {
         return null;
      }

      public XSComplexType asComplexType() {
         return this;
      }

      public boolean isDerivedFrom(XSType t) {
         return t == this;
      }

      public boolean isSimpleType() {
         return false;
      }

      public boolean isComplexType() {
         return true;
      }

      public XSContentType asEmpty() {
         return null;
      }

      public int getDerivationMethod() {
         return 2;
      }

      public XSElementDecl getScope() {
         return null;
      }

      public void visit(XSVisitor visitor) {
         visitor.complexType(this);
      }

      public Object apply(XSFunction f) {
         return f.complexType(this);
      }

      public XSType getType() {
         return this;
      }

      public XSComplexType getRedefinedBy() {
         return null;
      }

      public int getRedefinedCount() {
         return 0;
      }

      public XSType[] listSubstitutables() {
         return Util.listSubstitutables(this);
      }

      public List getSubtypes() {
         ArrayList subtypeList = new ArrayList();
         Iterator cTypes = this.getRoot().iterateComplexTypes();

         while(cTypes.hasNext()) {
            XSComplexType cType = (XSComplexType)cTypes.next();
            XSType base = cType.getBaseType();
            if (base != null && base.equals(this)) {
               subtypeList.add(cType);
            }
         }

         return subtypeList;
      }

      public List getElementDecls() {
         ArrayList declList = new ArrayList();
         XSSchemaSet schemaSet = this.getRoot();
         Iterator var3 = schemaSet.getSchemas().iterator();

         while(var3.hasNext()) {
            XSSchema sch = (XSSchema)var3.next();
            Iterator var5 = sch.getElementDecls().values().iterator();

            while(var5.hasNext()) {
               XSElementDecl decl = (XSElementDecl)var5.next();
               if (decl.getType().equals(this)) {
                  declList.add(decl);
               }
            }
         }

         return declList;
      }
   }

   private class AnySimpleType extends DeclarationImpl implements XSRestrictionSimpleType, Ref.SimpleType {
      AnySimpleType() {
         super((SchemaDocumentImpl)null, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, "http://www.w3.org/2001/XMLSchema", "anySimpleType", false);
      }

      public SchemaImpl getOwnerSchema() {
         return SchemaSetImpl.this.createSchema("http://www.w3.org/2001/XMLSchema", (Locator)null);
      }

      public XSSimpleType asSimpleType() {
         return this;
      }

      public XSComplexType asComplexType() {
         return null;
      }

      public boolean isDerivedFrom(XSType t) {
         return t == this || t == SchemaSetImpl.this.anyType;
      }

      public boolean isSimpleType() {
         return true;
      }

      public boolean isComplexType() {
         return false;
      }

      public XSContentType asEmpty() {
         return null;
      }

      public XSParticle asParticle() {
         return null;
      }

      public XSType getBaseType() {
         return SchemaSetImpl.this.anyType;
      }

      public XSSimpleType getSimpleBaseType() {
         return null;
      }

      public int getDerivationMethod() {
         return 2;
      }

      public Iterator iterateDeclaredFacets() {
         return Iterators.empty();
      }

      public Collection getDeclaredFacets() {
         return Collections.EMPTY_LIST;
      }

      public void visit(XSSimpleTypeVisitor visitor) {
         visitor.restrictionSimpleType(this);
      }

      public void visit(XSContentTypeVisitor visitor) {
         visitor.simpleType(this);
      }

      public void visit(XSVisitor visitor) {
         visitor.simpleType(this);
      }

      public Object apply(XSSimpleTypeFunction f) {
         return f.restrictionSimpleType(this);
      }

      public Object apply(XSContentTypeFunction f) {
         return f.simpleType(this);
      }

      public Object apply(XSFunction f) {
         return f.simpleType(this);
      }

      public XSVariety getVariety() {
         return XSVariety.ATOMIC;
      }

      public XSSimpleType getPrimitiveType() {
         return this;
      }

      public boolean isPrimitive() {
         return true;
      }

      public XSListSimpleType getBaseListType() {
         return null;
      }

      public XSUnionSimpleType getBaseUnionType() {
         return null;
      }

      public XSFacet getFacet(String name) {
         return null;
      }

      public List getFacets(String name) {
         return Collections.EMPTY_LIST;
      }

      public XSFacet getDeclaredFacet(String name) {
         return null;
      }

      public List getDeclaredFacets(String name) {
         return Collections.EMPTY_LIST;
      }

      public boolean isRestriction() {
         return true;
      }

      public boolean isList() {
         return false;
      }

      public boolean isUnion() {
         return false;
      }

      public boolean isFinal(XSVariety v) {
         return false;
      }

      public XSRestrictionSimpleType asRestriction() {
         return this;
      }

      public XSListSimpleType asList() {
         return null;
      }

      public XSUnionSimpleType asUnion() {
         return null;
      }

      public XSSimpleType getType() {
         return this;
      }

      public XSSimpleType getRedefinedBy() {
         return null;
      }

      public int getRedefinedCount() {
         return 0;
      }

      public XSType[] listSubstitutables() {
         return Util.listSubstitutables(this);
      }
   }
}
