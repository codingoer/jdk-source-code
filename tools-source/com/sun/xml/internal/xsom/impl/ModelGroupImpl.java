package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunctionWithParam;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.Arrays;
import java.util.Iterator;
import org.xml.sax.Locator;

public class ModelGroupImpl extends ComponentImpl implements XSModelGroup, Ref.Term {
   private final ParticleImpl[] children;
   private final XSModelGroup.Compositor compositor;

   public ModelGroupImpl(SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, XSModelGroup.Compositor _compositor, ParticleImpl[] _children) {
      super(owner, _annon, _loc, _fa);
      this.compositor = _compositor;
      this.children = _children;
      if (this.compositor == null) {
         throw new IllegalArgumentException();
      } else {
         for(int i = this.children.length - 1; i >= 0; --i) {
            if (this.children[i] == null) {
               throw new IllegalArgumentException();
            }
         }

      }
   }

   public ParticleImpl getChild(int idx) {
      return this.children[idx];
   }

   public int getSize() {
      return this.children.length;
   }

   public ParticleImpl[] getChildren() {
      return this.children;
   }

   public XSModelGroup.Compositor getCompositor() {
      return this.compositor;
   }

   public void redefine(ModelGroupDeclImpl oldMG) {
      ParticleImpl[] var2 = this.children;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ParticleImpl p = var2[var4];
         p.redefine(oldMG);
      }

   }

   public Iterator iterator() {
      return Arrays.asList((XSParticle[])this.children).iterator();
   }

   public boolean isWildcard() {
      return false;
   }

   public boolean isModelGroupDecl() {
      return false;
   }

   public boolean isModelGroup() {
      return true;
   }

   public boolean isElementDecl() {
      return false;
   }

   public XSWildcard asWildcard() {
      return null;
   }

   public XSModelGroupDecl asModelGroupDecl() {
      return null;
   }

   public XSModelGroup asModelGroup() {
      return this;
   }

   public XSElementDecl asElementDecl() {
      return null;
   }

   public void visit(XSVisitor visitor) {
      visitor.modelGroup(this);
   }

   public void visit(XSTermVisitor visitor) {
      visitor.modelGroup(this);
   }

   public Object apply(XSTermFunction function) {
      return function.modelGroup(this);
   }

   public Object apply(XSTermFunctionWithParam function, Object param) {
      return function.modelGroup(this, param);
   }

   public Object apply(XSFunction function) {
      return function.modelGroup(this);
   }

   public XSTerm getTerm() {
      return this;
   }
}
