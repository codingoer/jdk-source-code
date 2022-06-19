package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.model.Multiplicity;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import java.math.BigInteger;

public final class MultiplicityCounter implements XSTermFunction {
   public static final MultiplicityCounter theInstance = new MultiplicityCounter();

   private MultiplicityCounter() {
   }

   public Multiplicity particle(XSParticle p) {
      Multiplicity m = (Multiplicity)p.getTerm().apply(this);
      BigInteger max;
      if (m.max != null && !BigInteger.valueOf(-1L).equals(p.getMaxOccurs())) {
         max = p.getMaxOccurs();
      } else {
         max = null;
      }

      return Multiplicity.multiply(m, Multiplicity.create(p.getMinOccurs(), max));
   }

   public Multiplicity wildcard(XSWildcard wc) {
      return Multiplicity.ONE;
   }

   public Multiplicity modelGroupDecl(XSModelGroupDecl decl) {
      return this.modelGroup(decl.getModelGroup());
   }

   public Multiplicity modelGroup(XSModelGroup group) {
      boolean isChoice = group.getCompositor() == XSModelGroup.CHOICE;
      Multiplicity r = Multiplicity.ZERO;
      XSParticle[] var4 = group.getChildren();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         XSParticle p = var4[var6];
         Multiplicity m = this.particle(p);
         if (r == null) {
            r = m;
         } else if (isChoice) {
            r = Multiplicity.choice(r, m);
         } else {
            r = Multiplicity.group(r, m);
         }
      }

      return r;
   }

   public Multiplicity elementDecl(XSElementDecl decl) {
      return Multiplicity.ONE;
   }
}
