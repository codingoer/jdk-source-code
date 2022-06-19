package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.tools.internal.xjc.model.CAttributePropertyInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.Multiplicity;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.rngom.digested.DAttributePattern;
import com.sun.xml.internal.rngom.digested.DChoicePattern;
import com.sun.xml.internal.rngom.digested.DMixedPattern;
import com.sun.xml.internal.rngom.digested.DOneOrMorePattern;
import com.sun.xml.internal.rngom.digested.DOptionalPattern;
import com.sun.xml.internal.rngom.digested.DPattern;
import com.sun.xml.internal.rngom.digested.DPatternWalker;
import com.sun.xml.internal.rngom.digested.DZeroOrMorePattern;
import com.sun.xml.internal.xsom.XSComponent;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

final class ContentModelBinder extends DPatternWalker {
   private final RELAXNGCompiler compiler;
   private final CClassInfo clazz;
   private boolean insideOptional = false;
   private int iota = 1;

   public ContentModelBinder(RELAXNGCompiler compiler, CClassInfo clazz) {
      this.compiler = compiler;
      this.clazz = clazz;
   }

   public Void onMixed(DMixedPattern p) {
      throw new UnsupportedOperationException();
   }

   public Void onChoice(DChoicePattern p) {
      boolean old = this.insideOptional;
      this.insideOptional = true;
      super.onChoice(p);
      this.insideOptional = old;
      return null;
   }

   public Void onOptional(DOptionalPattern p) {
      boolean old = this.insideOptional;
      this.insideOptional = true;
      super.onOptional(p);
      this.insideOptional = old;
      return null;
   }

   public Void onZeroOrMore(DZeroOrMorePattern p) {
      return this.onRepeated(p, true);
   }

   public Void onOneOrMore(DOneOrMorePattern p) {
      return this.onRepeated(p, this.insideOptional);
   }

   private Void onRepeated(DPattern p, boolean optional) {
      RawTypeSet rts = RawTypeSetBuilder.build(this.compiler, p, optional ? Multiplicity.STAR : Multiplicity.PLUS);
      if (rts.canBeTypeRefs == RawTypeSet.Mode.SHOULD_BE_TYPEREF) {
         CElementPropertyInfo prop = new CElementPropertyInfo(this.calcName(p), CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT, ID.NONE, (MimeType)null, (XSComponent)null, (CCustomizations)null, p.getLocation(), !optional);
         rts.addTo(prop);
         this.clazz.addProperty(prop);
      } else {
         CReferencePropertyInfo prop = new CReferencePropertyInfo(this.calcName(p), true, !optional, false, (XSComponent)null, (CCustomizations)null, p.getLocation(), false, false, false);
         rts.addTo(prop);
         this.clazz.addProperty(prop);
      }

      return null;
   }

   public Void onAttribute(DAttributePattern p) {
      QName name = (QName)p.getName().listNames().iterator().next();
      CAttributePropertyInfo ap = new CAttributePropertyInfo(this.calcName(p), (XSComponent)null, (CCustomizations)null, p.getLocation(), name, (TypeUse)p.getChild().accept(this.compiler.typeUseBinder), (QName)null, !this.insideOptional);
      this.clazz.addProperty(ap);
      return null;
   }

   private String calcName(DPattern p) {
      return "field" + this.iota++;
   }
}
