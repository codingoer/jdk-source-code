package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import org.xml.sax.Locator;

abstract class DeclarationImpl extends ComponentImpl implements XSDeclaration {
   private final String name;
   private final String targetNamespace;
   private final boolean anonymous;

   DeclarationImpl(SchemaDocumentImpl owner, AnnotationImpl _annon, Locator loc, ForeignAttributesImpl fa, String _targetNamespace, String _name, boolean _anonymous) {
      super(owner, _annon, loc, fa);
      this.targetNamespace = _targetNamespace;
      this.name = _name;
      this.anonymous = _anonymous;
   }

   public String getName() {
      return this.name;
   }

   public String getTargetNamespace() {
      return this.targetNamespace;
   }

   /** @deprecated */
   public boolean isAnonymous() {
      return this.anonymous;
   }

   public final boolean isGlobal() {
      return !this.isAnonymous();
   }

   public final boolean isLocal() {
      return this.isAnonymous();
   }
}
