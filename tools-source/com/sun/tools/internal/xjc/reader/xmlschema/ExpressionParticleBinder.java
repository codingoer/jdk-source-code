package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.Multiplicity;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.tools.internal.xjc.reader.gbind.ConnectedComponent;
import com.sun.tools.internal.xjc.reader.gbind.Element;
import com.sun.tools.internal.xjc.reader.gbind.Expression;
import com.sun.tools.internal.xjc.reader.gbind.Graph;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.xsom.XSParticle;
import java.util.Collection;
import java.util.Iterator;

final class ExpressionParticleBinder extends ParticleBinder {
   public void build(XSParticle p, Collection forcedProps) {
      Expression tree = ExpressionBuilder.createTree(p);
      Graph g = new Graph(tree);
      Iterator var5 = g.iterator();

      while(var5.hasNext()) {
         ConnectedComponent cc = (ConnectedComponent)var5.next();
         this.buildProperty(cc);
      }

   }

   private void buildProperty(ConnectedComponent cc) {
      StringBuilder propName = new StringBuilder();
      int nameTokenCount = 0;
      RawTypeSetBuilder rtsb = new RawTypeSetBuilder();
      Iterator var5 = cc.iterator();

      while(var5.hasNext()) {
         Element e = (Element)var5.next();
         GElement ge = (GElement)e;
         if (nameTokenCount < 3) {
            if (nameTokenCount != 0) {
               propName.append("And");
            }

            propName.append(this.makeJavaName(cc.isCollection(), ge.getPropertyNameSeed()));
            ++nameTokenCount;
         }

         if (e instanceof GElementImpl) {
            GElementImpl ei = (GElementImpl)e;
            rtsb.elementDecl(ei.decl);
         } else if (e instanceof GWildcardElement) {
            GWildcardElement w = (GWildcardElement)e;
            rtsb.getRefs().add(new RawTypeSetBuilder.WildcardRef(w.isStrict() ? WildcardMode.STRICT : WildcardMode.SKIP));
         } else {
            assert false : e;
         }
      }

      Multiplicity m = Multiplicity.ONE;
      if (cc.isCollection()) {
         m = m.makeRepeated();
      }

      if (!cc.isRequired()) {
         m = m.makeOptional();
      }

      RawTypeSet rts = new RawTypeSet(rtsb.getRefs(), m);
      XSParticle p = this.findSourceParticle(cc);
      BIProperty cust = BIProperty.getCustomization(p);
      CPropertyInfo prop = cust.createElementOrReferenceProperty(propName.toString(), false, p, rts);
      this.getCurrentBean().addProperty(prop);
   }

   private XSParticle findSourceParticle(ConnectedComponent cc) {
      XSParticle first = null;
      Iterator var3 = cc.iterator();

      while(var3.hasNext()) {
         Element e = (Element)var3.next();
         GElement ge = (GElement)e;
         Iterator var6 = ge.particles.iterator();

         while(var6.hasNext()) {
            XSParticle p = (XSParticle)var6.next();
            if (first == null) {
               first = p;
            }

            if (this.getLocalPropCustomization(p) != null) {
               return p;
            }
         }
      }

      return first;
   }

   public boolean checkFallback(XSParticle p) {
      return false;
   }
}
