package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.Locator;

public class IdentityConstraintImpl extends ComponentImpl implements XSIdentityConstraint, Ref.IdentityConstraint {
   private XSElementDecl parent;
   private final short category;
   private final String name;
   private final XSXPath selector;
   private final List fields;
   private final Ref.IdentityConstraint refer;

   public IdentityConstraintImpl(SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa, short category, String name, XPathImpl selector, List fields, Ref.IdentityConstraint refer) {
      super(_owner, _annon, _loc, fa);
      this.category = category;
      this.name = name;
      this.selector = selector;
      selector.setParent(this);
      this.fields = Collections.unmodifiableList(fields);
      Iterator var10 = fields.iterator();

      while(var10.hasNext()) {
         XPathImpl xp = (XPathImpl)var10.next();
         xp.setParent(this);
      }

      this.refer = refer;
   }

   public void visit(XSVisitor visitor) {
      visitor.identityConstraint(this);
   }

   public Object apply(XSFunction function) {
      return function.identityConstraint(this);
   }

   public void setParent(ElementDecl parent) {
      this.parent = parent;
      parent.getOwnerSchema().addIdentityConstraint(this);
   }

   public XSElementDecl getParent() {
      return this.parent;
   }

   public String getName() {
      return this.name;
   }

   public String getTargetNamespace() {
      return this.getParent().getTargetNamespace();
   }

   public short getCategory() {
      return this.category;
   }

   public XSXPath getSelector() {
      return this.selector;
   }

   public List getFields() {
      return this.fields;
   }

   public XSIdentityConstraint getReferencedKey() {
      if (this.category == 1) {
         return this.refer.get();
      } else {
         throw new IllegalStateException("not a keyref");
      }
   }

   public XSIdentityConstraint get() {
      return this;
   }
}
