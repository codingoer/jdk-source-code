package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.math.BigInteger;
import java.util.List;
import org.xml.sax.Locator;

public class ParticleImpl extends ComponentImpl implements XSParticle, ContentTypeImpl {
   private Ref.Term term;
   private BigInteger maxOccurs;
   private BigInteger minOccurs;

   public ParticleImpl(SchemaDocumentImpl owner, AnnotationImpl _ann, Ref.Term _term, Locator _loc, BigInteger _maxOccurs, BigInteger _minOccurs) {
      super(owner, _ann, _loc, (ForeignAttributesImpl)null);
      this.term = _term;
      this.maxOccurs = _maxOccurs;
      this.minOccurs = _minOccurs;
   }

   public ParticleImpl(SchemaDocumentImpl owner, AnnotationImpl _ann, Ref.Term _term, Locator _loc, int _maxOccurs, int _minOccurs) {
      super(owner, _ann, _loc, (ForeignAttributesImpl)null);
      this.term = _term;
      this.maxOccurs = BigInteger.valueOf((long)_maxOccurs);
      this.minOccurs = BigInteger.valueOf((long)_minOccurs);
   }

   public ParticleImpl(SchemaDocumentImpl owner, AnnotationImpl _ann, Ref.Term _term, Locator _loc) {
      this(owner, _ann, _term, _loc, 1, 1);
   }

   public XSTerm getTerm() {
      return this.term.getTerm();
   }

   public BigInteger getMaxOccurs() {
      return this.maxOccurs;
   }

   public boolean isRepeated() {
      return !this.maxOccurs.equals(BigInteger.ZERO) && !this.maxOccurs.equals(BigInteger.ONE);
   }

   public BigInteger getMinOccurs() {
      return this.minOccurs;
   }

   public void redefine(ModelGroupDeclImpl oldMG) {
      if (this.term instanceof ModelGroupImpl) {
         ((ModelGroupImpl)this.term).redefine(oldMG);
      } else {
         if (this.term instanceof DelayedRef.ModelGroup) {
            ((DelayedRef)this.term).redefine(oldMG);
         }

      }
   }

   public XSSimpleType asSimpleType() {
      return null;
   }

   public XSParticle asParticle() {
      return this;
   }

   public XSContentType asEmpty() {
      return null;
   }

   public final Object apply(XSFunction function) {
      return function.particle(this);
   }

   public final Object apply(XSContentTypeFunction function) {
      return function.particle(this);
   }

   public final void visit(XSVisitor visitor) {
      visitor.particle(this);
   }

   public final void visit(XSContentTypeVisitor visitor) {
      visitor.particle(this);
   }

   public XSContentType getContentType() {
      return this;
   }

   public List getForeignAttributes() {
      return this.getTerm().getForeignAttributes();
   }
}
