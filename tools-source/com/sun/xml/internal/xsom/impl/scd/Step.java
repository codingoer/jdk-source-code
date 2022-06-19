package com.sun.xml.internal.xsom.impl.scd;

import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.impl.UName;
import java.util.Iterator;

public abstract class Step {
   public final Axis axis;
   int predicate = -1;

   protected Step(Axis axis) {
      this.axis = axis;
   }

   protected abstract Iterator filter(Iterator var1);

   public final Iterator evaluate(Iterator nodeSet) {
      Iterator r = new Iterators.Map(nodeSet) {
         protected Iterator apply(XSComponent contextNode) {
            return Step.this.filter(Step.this.axis.iterator(contextNode));
         }
      };
      Iterator r = new Iterators.Unique(r);
      if (this.predicate >= 0) {
         XSComponent item = null;

         for(int i = this.predicate; i > 0; --i) {
            if (!r.hasNext()) {
               return Iterators.empty();
            }

            item = (XSComponent)r.next();
         }

         return new Iterators.Singleton(item);
      } else {
         return r;
      }
   }

   static final class Schema extends Filtered {
      private final String uri;

      public Schema(Axis axis, String uri) {
         super(axis);
         this.uri = uri;
      }

      protected boolean match(XSSchema d) {
         return d.getTargetNamespace().equals(this.uri);
      }
   }

   static final class Facet extends Filtered {
      private final String name;

      public Facet(Axis axis, String facetName) {
         super(axis);
         this.name = facetName;
      }

      protected boolean match(XSFacet f) {
         return f.getName().equals(this.name);
      }
   }

   static final class AnonymousType extends Filtered {
      public AnonymousType(Axis axis) {
         super(axis);
      }

      protected boolean match(XSType node) {
         return node.isLocal();
      }
   }

   static final class Named extends Filtered {
      private final String nsUri;
      private final String localName;

      public Named(Axis axis, UName n) {
         this(axis, n.getNamespaceURI(), n.getName());
      }

      public Named(Axis axis, String nsUri, String localName) {
         super(axis);
         this.nsUri = nsUri;
         this.localName = localName;
      }

      protected boolean match(XSDeclaration d) {
         return d.getName().equals(this.localName) && d.getTargetNamespace().equals(this.nsUri);
      }
   }

   private abstract static class Filtered extends Step {
      protected Filtered(Axis axis) {
         super(axis);
      }

      protected Iterator filter(Iterator base) {
         return new Iterators.Filter(base) {
            protected boolean matches(XSComponent d) {
               return Filtered.this.match(d);
            }
         };
      }

      protected abstract boolean match(XSComponent var1);
   }

   static final class Any extends Step {
      public Any(Axis axis) {
         super(axis);
      }

      protected Iterator filter(Iterator base) {
         return base;
      }
   }
}
