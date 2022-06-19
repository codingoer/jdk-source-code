package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunctionWithParam;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import com.sun.xml.internal.xsom.visitor.XSWildcardFunction;
import com.sun.xml.internal.xsom.visitor.XSWildcardVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.Locator;

public abstract class WildcardImpl extends ComponentImpl implements XSWildcard, Ref.Term {
   private final int mode;

   protected WildcardImpl(SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, int _mode) {
      super(owner, _annon, _loc, _fa);
      this.mode = _mode;
   }

   public int getMode() {
      return this.mode;
   }

   public WildcardImpl union(SchemaDocumentImpl owner, WildcardImpl rhs) {
      if (!(this instanceof Any) && !(rhs instanceof Any)) {
         if (this instanceof Finite && rhs instanceof Finite) {
            Set values = new HashSet();
            values.addAll(((Finite)this).names);
            values.addAll(((Finite)rhs).names);
            return new Finite(owner, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, values, this.mode);
         } else if (this instanceof Other && rhs instanceof Other) {
            return ((Other)this).otherNamespace.equals(((Other)rhs).otherNamespace) ? new Other(owner, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, ((Other)this).otherNamespace, this.mode) : new Other(owner, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, "", this.mode);
         } else {
            Other o;
            Finite f;
            if (this instanceof Other) {
               o = (Other)this;
               f = (Finite)rhs;
            } else {
               o = (Other)rhs;
               f = (Finite)this;
            }

            return (WildcardImpl)(f.names.contains(o.otherNamespace) ? new Any(owner, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, this.mode) : new Other(owner, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, o.otherNamespace, this.mode));
         }
      } else {
         return new Any(owner, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, this.mode);
      }
   }

   public final void visit(XSVisitor visitor) {
      visitor.wildcard(this);
   }

   public final void visit(XSTermVisitor visitor) {
      visitor.wildcard(this);
   }

   public Object apply(XSTermFunction function) {
      return function.wildcard(this);
   }

   public Object apply(XSTermFunctionWithParam function, Object param) {
      return function.wildcard(this, param);
   }

   public Object apply(XSFunction function) {
      return function.wildcard(this);
   }

   public boolean isWildcard() {
      return true;
   }

   public boolean isModelGroupDecl() {
      return false;
   }

   public boolean isModelGroup() {
      return false;
   }

   public boolean isElementDecl() {
      return false;
   }

   public XSWildcard asWildcard() {
      return this;
   }

   public XSModelGroupDecl asModelGroupDecl() {
      return null;
   }

   public XSModelGroup asModelGroup() {
      return null;
   }

   public XSElementDecl asElementDecl() {
      return null;
   }

   public XSTerm getTerm() {
      return this;
   }

   public static final class Finite extends WildcardImpl implements XSWildcard.Union {
      private final Set names;
      private final Set namesView;

      public Finite(SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, Set ns, int _mode) {
         super(owner, _annon, _loc, _fa, _mode);
         this.names = ns;
         this.namesView = Collections.unmodifiableSet(this.names);
      }

      public Iterator iterateNamespaces() {
         return this.names.iterator();
      }

      public Collection getNamespaces() {
         return this.namesView;
      }

      public boolean acceptsNamespace(String namespaceURI) {
         return this.names.contains(namespaceURI);
      }

      public void visit(XSWildcardVisitor visitor) {
         visitor.union(this);
      }

      public Object apply(XSWildcardFunction function) {
         return function.union(this);
      }
   }

   public static final class Other extends WildcardImpl implements XSWildcard.Other {
      private final String otherNamespace;

      public Other(SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, String otherNamespace, int _mode) {
         super(owner, _annon, _loc, _fa, _mode);
         this.otherNamespace = otherNamespace;
      }

      public String getOtherNamespace() {
         return this.otherNamespace;
      }

      public boolean acceptsNamespace(String namespaceURI) {
         return !namespaceURI.equals(this.otherNamespace);
      }

      public void visit(XSWildcardVisitor visitor) {
         visitor.other(this);
      }

      public Object apply(XSWildcardFunction function) {
         return function.other(this);
      }
   }

   public static final class Any extends WildcardImpl implements XSWildcard.Any {
      public Any(SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, int _mode) {
         super(owner, _annon, _loc, _fa, _mode);
      }

      public boolean acceptsNamespace(String namespaceURI) {
         return true;
      }

      public void visit(XSWildcardVisitor visitor) {
         visitor.any(this);
      }

      public Object apply(XSWildcardFunction function) {
         return function.any(this);
      }
   }
}
