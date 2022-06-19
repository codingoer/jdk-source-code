package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.reader.gbind.Choice;
import com.sun.tools.internal.xjc.reader.gbind.Element;
import com.sun.tools.internal.xjc.reader.gbind.Expression;
import com.sun.tools.internal.xjc.reader.gbind.OneOrMore;
import com.sun.tools.internal.xjc.reader.gbind.Sequence;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public final class ExpressionBuilder implements XSTermFunction {
   private GWildcardElement wildcard = null;
   private final Map decls = new HashMap();
   private XSParticle current;

   public static Expression createTree(XSParticle p) {
      return (new ExpressionBuilder()).particle(p);
   }

   private ExpressionBuilder() {
   }

   public Expression wildcard(XSWildcard wc) {
      if (this.wildcard == null) {
         this.wildcard = new GWildcardElement();
      }

      this.wildcard.merge(wc);
      this.wildcard.particles.add(this.current);
      return this.wildcard;
   }

   public Expression modelGroupDecl(XSModelGroupDecl decl) {
      return this.modelGroup(decl.getModelGroup());
   }

   public Expression modelGroup(XSModelGroup group) {
      XSModelGroup.Compositor comp = group.getCompositor();
      Object e;
      XSParticle[] var4;
      int var5;
      int var6;
      XSParticle p;
      if (comp == XSModelGroup.CHOICE) {
         e = Expression.EPSILON;
         var4 = group.getChildren();
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            p = var4[var6];
            if (e == null) {
               e = this.particle(p);
            } else {
               e = new Choice((Expression)e, this.particle(p));
            }
         }

         return (Expression)e;
      } else {
         e = Expression.EPSILON;
         var4 = group.getChildren();
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            p = var4[var6];
            if (e == null) {
               e = this.particle(p);
            } else {
               e = new Sequence((Expression)e, this.particle(p));
            }
         }

         return (Expression)e;
      }
   }

   public Element elementDecl(XSElementDecl decl) {
      QName n = BGMBuilder.getName(decl);
      GElementImpl e = (GElementImpl)this.decls.get(n);
      if (e == null) {
         this.decls.put(n, e = new GElementImpl(n, decl));
      }

      e.particles.add(this.current);

      assert this.current.getTerm() == decl;

      return e;
   }

   public Expression particle(XSParticle p) {
      this.current = p;
      Expression e = (Expression)p.getTerm().apply(this);
      if (p.isRepeated()) {
         e = new OneOrMore((Expression)e);
      }

      if (BigInteger.ZERO.equals(p.getMinOccurs())) {
         e = new Choice((Expression)e, Expression.EPSILON);
      }

      return (Expression)e;
   }
}
