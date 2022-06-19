package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.codemodel.internal.JJavaName;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;

public abstract class ParticleBinder {
   protected final BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);

   protected ParticleBinder() {
      Ring.add(ParticleBinder.class, this);
   }

   public final void build(XSParticle p) {
      this.build(p, Collections.emptySet());
   }

   public abstract void build(XSParticle var1, Collection var2);

   public abstract boolean checkFallback(XSParticle var1);

   protected final CClassInfo getCurrentBean() {
      return this.getClassSelector().getCurrentBean();
   }

   protected final BIProperty getLocalPropCustomization(XSParticle p) {
      return (BIProperty)this.getLocalCustomization(p, BIProperty.class);
   }

   protected final BIDeclaration getLocalCustomization(XSParticle p, Class type) {
      BIDeclaration cust = this.builder.getBindInfo(p).get(type);
      if (cust != null) {
         return cust;
      } else {
         cust = this.builder.getBindInfo(p.getTerm()).get(type);
         return cust != null ? cust : null;
      }
   }

   protected final String computeLabel(XSParticle p) {
      BIProperty cust = this.getLocalPropCustomization(p);
      if (cust != null && cust.getPropertyName(false) != null) {
         return cust.getPropertyName(false);
      } else {
         XSTerm t = p.getTerm();
         if (t.isElementDecl()) {
            return this.makeJavaName(p, t.asElementDecl().getName());
         } else if (t.isModelGroupDecl()) {
            return this.makeJavaName(p, t.asModelGroupDecl().getName());
         } else if (t.isWildcard()) {
            return this.makeJavaName(p, "Any");
         } else if (t.isModelGroup()) {
            try {
               return this.getSpecDefaultName(t.asModelGroup(), p.isRepeated());
            } catch (ParseException var5) {
               this.getErrorReporter().error(t.getLocator(), "DefaultParticleBinder.UnableToGenerateNameFromModelGroup");
               return "undefined";
            }
         } else {
            throw new AssertionError();
         }
      }
   }

   protected final String makeJavaName(boolean isRepeated, String xmlName) {
      String name = this.builder.getNameConverter().toPropertyName(xmlName);
      if (this.builder.getGlobalBinding().isSimpleMode() && isRepeated) {
         name = JJavaName.getPluralForm(name);
      }

      return name;
   }

   protected final String makeJavaName(XSParticle p, String xmlName) {
      return this.makeJavaName(p.isRepeated(), xmlName);
   }

   protected final String getSpecDefaultName(XSModelGroup mg, final boolean repeated) throws ParseException {
      final StringBuilder name = new StringBuilder();
      mg.visit(new XSTermVisitor() {
         private int count = 0;
         private boolean rep = repeated;

         public void wildcard(XSWildcard wc) {
            this.append("any");
         }

         public void modelGroupDecl(XSModelGroupDecl mgd) {
            this.modelGroup(mgd.getModelGroup());
         }

         public void modelGroup(XSModelGroup mg) {
            String operator;
            if (mg.getCompositor() == XSModelGroup.CHOICE) {
               operator = "Or";
            } else {
               operator = "And";
            }

            int size = mg.getSize();

            for(int i = 0; i < size; ++i) {
               XSParticle p = mg.getChild(i);
               boolean oldRep = this.rep;
               this.rep |= p.isRepeated();
               p.getTerm().visit(this);
               this.rep = oldRep;
               if (this.count == 3) {
                  return;
               }

               if (i != size - 1) {
                  name.append(operator);
               }
            }

         }

         public void elementDecl(XSElementDecl ed) {
            this.append(ed.getName());
         }

         private void append(String token) {
            if (this.count < 3) {
               name.append(ParticleBinder.this.makeJavaName(this.rep, token));
               ++this.count;
            }

         }
      });
      if (name.length() == 0) {
         throw new ParseException("no element", -1);
      } else {
         return name.toString();
      }
   }

   protected final ErrorReporter getErrorReporter() {
      return (ErrorReporter)Ring.get(ErrorReporter.class);
   }

   protected final ClassSelector getClassSelector() {
      return (ClassSelector)Ring.get(ClassSelector.class);
   }
}
