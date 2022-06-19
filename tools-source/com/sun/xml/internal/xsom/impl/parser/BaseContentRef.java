package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.impl.Ref;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class BaseContentRef implements Ref.ContentType, Patch {
   private final Ref.Type baseType;
   private final Locator loc;

   public BaseContentRef(final NGCCRuntimeEx $runtime, Ref.Type _baseType) {
      this.baseType = _baseType;
      $runtime.addPatcher(this);
      $runtime.addErrorChecker(new Patch() {
         public void run() throws SAXException {
            XSType t = BaseContentRef.this.baseType.getType();
            if (t.isComplexType() && t.asComplexType().getContentType().asParticle() != null) {
               $runtime.reportError(Messages.format("SimpleContentExpected", t.getTargetNamespace(), t.getName()), BaseContentRef.this.loc);
            }

         }
      });
      this.loc = $runtime.copyLocator();
   }

   public XSContentType getContentType() {
      XSType t = this.baseType.getType();
      return (XSContentType)(t.asComplexType() != null ? t.asComplexType().getContentType() : t.asSimpleType());
   }

   public void run() throws SAXException {
      if (this.baseType instanceof Patch) {
         ((Patch)this.baseType).run();
      }

   }
}
