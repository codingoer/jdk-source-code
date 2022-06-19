package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.SchemaImpl;
import com.sun.xml.internal.xsom.impl.UName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public abstract class DelayedRef implements Patch {
   protected final XSSchemaSet schema;
   private PatcherManager manager;
   private UName name;
   private Locator source;
   private Object ref = null;

   DelayedRef(PatcherManager _manager, Locator _source, SchemaImpl _schema, UName _name) {
      this.schema = _schema.getRoot();
      this.manager = _manager;
      this.name = _name;
      this.source = _source;
      if (this.name == null) {
         throw new InternalError();
      } else {
         this.manager.addPatcher(this);
      }
   }

   public void run() throws SAXException {
      if (this.ref == null) {
         this.resolve();
      }

      this.manager = null;
      this.name = null;
      this.source = null;
   }

   protected abstract Object resolveReference(UName var1);

   protected abstract String getErrorProperty();

   protected final Object _get() {
      if (this.ref == null) {
         throw new InternalError("unresolved reference");
      } else {
         return this.ref;
      }
   }

   private void resolve() throws SAXException {
      this.ref = this.resolveReference(this.name);
      if (this.ref == null) {
         this.manager.reportError(Messages.format(this.getErrorProperty(), this.name.getQualifiedName()), this.source);
      }

   }

   public void redefine(XSDeclaration d) {
      if (d.getTargetNamespace().equals(this.name.getNamespaceURI()) && d.getName().equals(this.name.getName())) {
         this.ref = d;
         this.manager = null;
         this.name = null;
         this.source = null;
      }
   }

   public static class IdentityConstraint extends DelayedRef implements Ref.IdentityConstraint {
      public IdentityConstraint(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         return super.schema.getIdentityConstraint(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedIdentityConstraint";
      }

      public XSIdentityConstraint get() {
         return (XSIdentityConstraint)super._get();
      }
   }

   public static class Attribute extends DelayedRef implements Ref.Attribute {
      public Attribute(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         return super.schema.getAttributeDecl(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedAttribute";
      }

      public XSAttributeDecl getAttribute() {
         return (XSAttributeDecl)super._get();
      }
   }

   public static class AttGroup extends DelayedRef implements Ref.AttGroup {
      public AttGroup(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         return super.schema.getAttGroupDecl(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedAttributeGroup";
      }

      public XSAttGroupDecl get() {
         return (XSAttGroupDecl)super._get();
      }
   }

   public static class ModelGroup extends DelayedRef implements Ref.Term {
      public ModelGroup(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         return super.schema.getModelGroupDecl(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedModelGroup";
      }

      public XSModelGroupDecl get() {
         return (XSModelGroupDecl)super._get();
      }

      public XSTerm getTerm() {
         return this.get();
      }
   }

   public static class Element extends DelayedRef implements Ref.Element {
      public Element(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         return super.schema.getElementDecl(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedElement";
      }

      public XSElementDecl get() {
         return (XSElementDecl)super._get();
      }

      public XSTerm getTerm() {
         return this.get();
      }
   }

   public static class ComplexType extends DelayedRef implements Ref.ComplexType {
      public ComplexType(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         return super.schema.getComplexType(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedCompplexType";
      }

      public XSComplexType getType() {
         return (XSComplexType)super._get();
      }
   }

   public static class SimpleType extends DelayedRef implements Ref.SimpleType {
      public SimpleType(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      public XSSimpleType getType() {
         return (XSSimpleType)this._get();
      }

      protected Object resolveReference(UName name) {
         return super.schema.getSimpleType(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedSimpleType";
      }
   }

   public static class Type extends DelayedRef implements Ref.Type {
      public Type(PatcherManager manager, Locator loc, SchemaImpl schema, UName name) {
         super(manager, loc, schema, name);
      }

      protected Object resolveReference(UName name) {
         Object o = super.schema.getSimpleType(name.getNamespaceURI(), name.getName());
         return o != null ? o : super.schema.getComplexType(name.getNamespaceURI(), name.getName());
      }

      protected String getErrorProperty() {
         return "UndefinedType";
      }

      public XSType getType() {
         return (XSType)super._get();
      }
   }
}
