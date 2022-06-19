package com.sun.xml.internal.xsom.impl.scd;

import com.sun.xml.internal.xsom.XSAttContainer;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface Axis {
   Axis ROOT = new Axis() {
      public Iterator iterator(XSComponent contextNode) {
         return contextNode.getRoot().iterateSchema();
      }

      public Iterator iterator(Iterator contextNodes) {
         return !contextNodes.hasNext() ? Iterators.empty() : this.iterator((XSComponent)contextNodes.next());
      }

      public boolean isModelGroup() {
         return false;
      }

      public String toString() {
         return "root::";
      }
   };
   Axis INTERMEDIATE_SKIP = new AbstractAxisImpl() {
      public Iterator elementDecl(XSElementDecl decl) {
         XSComplexType ct = decl.getType().asComplexType();
         return (Iterator)(ct == null ? this.empty() : new Iterators.Union(this.singleton(ct), this.complexType(ct)));
      }

      public Iterator modelGroupDecl(XSModelGroupDecl decl) {
         return this.descendants(decl.getModelGroup());
      }

      public Iterator particle(XSParticle particle) {
         return this.descendants(particle.getTerm().asModelGroup());
      }

      private Iterator descendants(XSModelGroup mg) {
         List r = new ArrayList();
         this.visit(mg, r);
         return r.iterator();
      }

      private void visit(XSModelGroup mg, List r) {
         r.add(mg);
         Iterator var3 = mg.iterator();

         while(var3.hasNext()) {
            XSParticle p = (XSParticle)var3.next();
            XSModelGroup child = p.getTerm().asModelGroup();
            if (child != null) {
               this.visit(child, r);
            }
         }

      }

      public String toString() {
         return "(intermediateSkip)";
      }
   };
   Axis DESCENDANTS = new Axis() {
      public Iterator iterator(XSComponent contextNode) {
         return (new null.Visitor()).iterator(contextNode);
      }

      public Iterator iterator(Iterator contextNodes) {
         return (new null.Visitor()).iterator(contextNodes);
      }

      public boolean isModelGroup() {
         return false;
      }

      public String toString() {
         return "/";
      }

      final class Visitor extends AbstractAxisImpl {
         private final Set visited = new HashSet();

         public Iterator schema(XSSchema schema) {
            return this.visited.add(schema) ? this.ret(schema, new null.Recursion(schema.iterateElementDecls())) : this.empty();
         }

         public Iterator elementDecl(XSElementDecl decl) {
            return this.visited.add(decl) ? this.ret(decl, this.iterator(decl.getType())) : this.empty();
         }

         public Iterator simpleType(XSSimpleType type) {
            return this.visited.add(type) ? this.ret(type, FACET.iterator((XSComponent)type)) : this.empty();
         }

         public Iterator complexType(XSComplexType type) {
            return this.visited.add(type) ? this.ret(type, this.iterator(type.getContentType())) : this.empty();
         }

         public Iterator particle(XSParticle particle) {
            return this.visited.add(particle) ? this.ret(particle, this.iterator(particle.getTerm())) : this.empty();
         }

         public Iterator modelGroupDecl(XSModelGroupDecl decl) {
            return this.visited.add(decl) ? this.ret(decl, this.iterator(decl.getModelGroup())) : this.empty();
         }

         public Iterator modelGroup(XSModelGroup group) {
            return this.visited.add(group) ? this.ret(group, new null.Recursion(group.iterator())) : this.empty();
         }

         public Iterator attGroupDecl(XSAttGroupDecl decl) {
            return this.visited.add(decl) ? this.ret(decl, new null.Recursion(decl.iterateAttributeUses())) : this.empty();
         }

         public Iterator attributeUse(XSAttributeUse use) {
            return this.visited.add(use) ? this.ret(use, this.iterator(use.getDecl())) : this.empty();
         }

         public Iterator attributeDecl(XSAttributeDecl decl) {
            return this.visited.add(decl) ? this.ret(decl, this.iterator(decl.getType())) : this.empty();
         }

         private Iterator ret(XSComponent one, Iterator rest) {
            return this.union(this.singleton(one), rest);
         }

         final class Recursion extends Iterators.Map {
            public Recursion(Iterator core) {
               super(core);
            }

            protected Iterator apply(XSComponent u) {
               return Axis.DESCENDANTS.iterator(u);
            }
         }
      }
   };
   Axis X_SCHEMA = new Axis() {
      public Iterator iterator(XSComponent contextNode) {
         return Iterators.singleton(contextNode.getOwnerSchema());
      }

      public Iterator iterator(Iterator contextNodes) {
         return new Iterators.Adapter(contextNodes) {
            protected XSSchema filter(XSComponent u) {
               return u.getOwnerSchema();
            }
         };
      }

      public boolean isModelGroup() {
         return false;
      }

      public String toString() {
         return "x-schema::";
      }
   };
   Axis SUBSTITUTION_GROUP = new AbstractAxisImpl() {
      public Iterator elementDecl(XSElementDecl decl) {
         return this.singleton(decl.getSubstAffiliation());
      }

      public String toString() {
         return "substitutionGroup::";
      }
   };
   Axis ATTRIBUTE = new AbstractAxisImpl() {
      public Iterator complexType(XSComplexType type) {
         return this.attributeHolder(type);
      }

      public Iterator attGroupDecl(XSAttGroupDecl decl) {
         return this.attributeHolder(decl);
      }

      private Iterator attributeHolder(XSAttContainer atts) {
         return new Iterators.Adapter(atts.iterateAttributeUses()) {
            protected XSAttributeDecl filter(XSAttributeUse u) {
               return u.getDecl();
            }
         };
      }

      public Iterator schema(XSSchema schema) {
         return schema.iterateAttributeDecls();
      }

      public String toString() {
         return "@";
      }
   };
   Axis ELEMENT = new AbstractAxisImpl() {
      public Iterator particle(XSParticle particle) {
         return this.singleton(particle.getTerm().asElementDecl());
      }

      public Iterator schema(XSSchema schema) {
         return schema.iterateElementDecls();
      }

      public Iterator modelGroupDecl(XSModelGroupDecl decl) {
         return this.modelGroup(decl.getModelGroup());
      }

      public String getName() {
         return "";
      }

      public String toString() {
         return "element::";
      }
   };
   Axis TYPE_DEFINITION = new AbstractAxisImpl() {
      public Iterator schema(XSSchema schema) {
         return schema.iterateTypes();
      }

      public Iterator attributeDecl(XSAttributeDecl decl) {
         return this.singleton(decl.getType());
      }

      public Iterator elementDecl(XSElementDecl decl) {
         return this.singleton(decl.getType());
      }

      public String toString() {
         return "~";
      }
   };
   Axis BASETYPE = new AbstractAxisImpl() {
      public Iterator simpleType(XSSimpleType type) {
         return this.singleton(type.getBaseType());
      }

      public Iterator complexType(XSComplexType type) {
         return this.singleton(type.getBaseType());
      }

      public String toString() {
         return "baseType::";
      }
   };
   Axis PRIMITIVE_TYPE = new AbstractAxisImpl() {
      public Iterator simpleType(XSSimpleType type) {
         return this.singleton(type.getPrimitiveType());
      }

      public String toString() {
         return "primitiveType::";
      }
   };
   Axis ITEM_TYPE = new AbstractAxisImpl() {
      public Iterator simpleType(XSSimpleType type) {
         XSListSimpleType baseList = type.getBaseListType();
         return baseList == null ? this.empty() : this.singleton(baseList.getItemType());
      }

      public String toString() {
         return "itemType::";
      }
   };
   Axis MEMBER_TYPE = new AbstractAxisImpl() {
      public Iterator simpleType(XSSimpleType type) {
         XSUnionSimpleType baseUnion = type.getBaseUnionType();
         return baseUnion == null ? this.empty() : baseUnion.iterator();
      }

      public String toString() {
         return "memberType::";
      }
   };
   Axis SCOPE = new AbstractAxisImpl() {
      public Iterator complexType(XSComplexType type) {
         return this.singleton(type.getScope());
      }

      public String toString() {
         return "scope::";
      }
   };
   Axis ATTRIBUTE_GROUP = new AbstractAxisImpl() {
      public Iterator schema(XSSchema schema) {
         return schema.iterateAttGroupDecls();
      }

      public String toString() {
         return "attributeGroup::";
      }
   };
   Axis MODEL_GROUP_DECL = new AbstractAxisImpl() {
      public Iterator schema(XSSchema schema) {
         return schema.iterateModelGroupDecls();
      }

      public Iterator particle(XSParticle particle) {
         return this.singleton(particle.getTerm().asModelGroupDecl());
      }

      public String toString() {
         return "group::";
      }
   };
   Axis IDENTITY_CONSTRAINT = new AbstractAxisImpl() {
      public Iterator elementDecl(XSElementDecl decl) {
         return decl.getIdentityConstraints().iterator();
      }

      public Iterator schema(XSSchema schema) {
         return super.schema(schema);
      }

      public String toString() {
         return "identityConstraint::";
      }
   };
   Axis REFERENCED_KEY = new AbstractAxisImpl() {
      public Iterator identityConstraint(XSIdentityConstraint decl) {
         return this.singleton(decl.getReferencedKey());
      }

      public String toString() {
         return "key::";
      }
   };
   Axis NOTATION = new AbstractAxisImpl() {
      public Iterator schema(XSSchema schema) {
         return schema.iterateNotations();
      }

      public String toString() {
         return "notation::";
      }
   };
   Axis WILDCARD = new AbstractAxisImpl() {
      public Iterator particle(XSParticle particle) {
         return this.singleton(particle.getTerm().asWildcard());
      }

      public String toString() {
         return "any::";
      }
   };
   Axis ATTRIBUTE_WILDCARD = new AbstractAxisImpl() {
      public Iterator complexType(XSComplexType type) {
         return this.singleton(type.getAttributeWildcard());
      }

      public Iterator attGroupDecl(XSAttGroupDecl decl) {
         return this.singleton(decl.getAttributeWildcard());
      }

      public String toString() {
         return "anyAttribute::";
      }
   };
   Axis FACET = new AbstractAxisImpl() {
      public Iterator simpleType(XSSimpleType type) {
         XSRestrictionSimpleType r = type.asRestriction();
         return r != null ? r.iterateDeclaredFacets() : this.empty();
      }

      public String toString() {
         return "facet::";
      }
   };
   Axis MODELGROUP_ALL = new ModelGroupAxis(XSModelGroup.Compositor.ALL);
   Axis MODELGROUP_CHOICE = new ModelGroupAxis(XSModelGroup.Compositor.CHOICE);
   Axis MODELGROUP_SEQUENCE = new ModelGroupAxis(XSModelGroup.Compositor.SEQUENCE);
   Axis MODELGROUP_ANY = new ModelGroupAxis((XSModelGroup.Compositor)null);

   Iterator iterator(XSComponent var1);

   Iterator iterator(Iterator var1);

   boolean isModelGroup();

   public static final class ModelGroupAxis extends AbstractAxisImpl {
      private final XSModelGroup.Compositor compositor;

      ModelGroupAxis(XSModelGroup.Compositor compositor) {
         this.compositor = compositor;
      }

      public boolean isModelGroup() {
         return true;
      }

      public Iterator particle(XSParticle particle) {
         return this.filter(particle.getTerm().asModelGroup());
      }

      public Iterator modelGroupDecl(XSModelGroupDecl decl) {
         return this.filter(decl.getModelGroup());
      }

      private Iterator filter(XSModelGroup mg) {
         if (mg == null) {
            return this.empty();
         } else {
            return mg.getCompositor() != this.compositor && this.compositor != null ? this.empty() : this.singleton(mg);
         }
      }

      public String toString() {
         return this.compositor == null ? "model::*" : "model::" + this.compositor;
      }
   }
}
