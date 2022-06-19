package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ComplexTypeImpl;
import com.sun.xml.internal.xsom.impl.ContentTypeImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.ModelGroupImpl;
import com.sun.xml.internal.xsom.impl.ParticleImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.RestrictionSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.parser.BaseContentRef;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.Collections;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class complexType extends NGCCHandler {
   private Integer finalValue;
   private String name;
   private String abstractValue;
   private Integer blockValue;
   private XSFacet facet;
   private ForeignAttributesImpl fa;
   private AnnotationImpl annotation;
   private ContentTypeImpl explicitContent;
   private UName baseTypeName;
   private String mixedValue;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private ComplexTypeImpl result;
   private Ref.Type baseType;
   private Ref.ContentType contentType;
   private Ref.SimpleType baseContentType;
   private RestrictionSimpleTypeImpl contentSimpleType;
   private Locator locator;
   private Locator locator2;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public complexType(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 88;
   }

   public complexType(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.result.setContentType(this.explicitContent);
   }

   private void action1() throws SAXException {
      this.baseType = this.$runtime.parser.schemaSet.anyType;
      this.makeResult(2);
   }

   private void action2() throws SAXException {
      this.result.setExplicitContent(this.explicitContent);
      this.result.setContentType(this.buildComplexExtensionContentModel(this.explicitContent));
   }

   private void action3() throws SAXException {
      this.baseType = new DelayedRef.Type(this.$runtime, this.locator2, this.$runtime.currentSchema, this.baseTypeName);
      this.makeResult(1);
   }

   private void action4() throws SAXException {
      this.locator2 = this.$runtime.copyLocator();
   }

   private void action5() throws SAXException {
      this.result.setContentType(this.explicitContent);
   }

   private void action6() throws SAXException {
      this.baseType = new DelayedRef.Type(this.$runtime, this.locator2, this.$runtime.currentSchema, this.baseTypeName);
      this.makeResult(2);
   }

   private void action7() throws SAXException {
      this.locator2 = this.$runtime.copyLocator();
   }

   private void action8() throws SAXException {
      this.contentType = new BaseContentRef(this.$runtime, this.baseType);
      this.makeResult(1);
      this.result.setContentType(this.contentType);
   }

   private void action9() throws SAXException {
      this.baseType = new DelayedRef.Type(this.$runtime, this.locator2, this.$runtime.currentSchema, this.baseTypeName);
   }

   private void action10() throws SAXException {
      this.locator2 = this.$runtime.copyLocator();
   }

   private void action11() throws SAXException {
      this.makeResult(2);
      this.result.setContentType(this.contentType);
   }

   private void action12() throws SAXException {
      this.contentSimpleType.addFacet(this.facet);
   }

   private void action13() throws SAXException {
      if (this.baseContentType == null) {
         this.baseContentType = new BaseContentSimpleTypeRef(this.baseType);
      }

      this.contentSimpleType = new RestrictionSimpleTypeImpl(this.$runtime.document, (AnnotationImpl)null, this.locator2, (ForeignAttributesImpl)null, (String)null, true, Collections.EMPTY_SET, this.baseContentType);
      this.contentType = this.contentSimpleType;
   }

   private void action14() throws SAXException {
      this.baseType = new DelayedRef.Type(this.$runtime, this.locator2, this.$runtime.currentSchema, this.baseTypeName);
   }

   private void action15() throws SAXException {
      this.locator2 = this.$runtime.copyLocator();
   }

   private void action16() throws SAXException {
      this.locator = this.$runtime.copyLocator();
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      foreignAttributes h;
      annotation h;
      facet h;
      attributeUses h;
      complexType_complexContent_body h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         case 11:
         case 13:
         case 14:
         case 16:
         case 17:
         case 20:
         case 22:
         case 23:
         case 25:
         case 27:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 36:
         case 40:
         case 42:
         case 43:
         case 45:
         case 46:
         case 50:
         case 53:
         case 55:
         case 57:
         case 58:
         case 60:
         case 62:
         case 64:
         case 66:
         case 69:
         case 70:
         case 71:
         case 73:
         case 74:
         case 75:
         case 77:
         case 78:
         case 79:
         case 81:
         case 82:
         case 83:
         case 85:
         case 86:
         case 87:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleContent")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 63;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexContent")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 29;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("anyAttribute") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("any") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("all") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("choice") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("sequence") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.action1();
               h = new complexType_complexContent_body(this, super._source, this.$runtime, 557, this.result);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 7:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action7();
               this.$_ngcc_current_state = 24;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action4();
               this.$_ngcc_current_state = 15;
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 9:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new complexType_complexContent_body(this, super._source, this.$runtime, 560, this.result);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 562, this.annotation, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 9;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 12:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 564, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 15:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 18:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new complexType_complexContent_body(this, super._source, this.$runtime, 571, this.result);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 19:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 573, this.annotation, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 18;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 21:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 575, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 24:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 26:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 582, this.annotation, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 7;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 28:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("extension")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("restriction"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 584, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 29:
            if (($ai = this.$runtime.getAttributeIndex("", "mixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 35:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action15();
               this.$_ngcc_current_state = 59;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action10();
               this.$_ngcc_current_state = 44;
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 37:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new attributeUses(this, super._source, this.$runtime, 594, this.result);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 38:
            this.action8();
            this.$_ngcc_current_state = 37;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 39:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 597, this.annotation, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 38;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 41:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 599, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 44:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 47:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("anyAttribute") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               h = new attributeUses(this, super._source, this.$runtime, 606, this.result);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 48:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.action11();
               this.$_ngcc_current_state = 47;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new facet(this, super._source, this.$runtime, 609);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 49:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.$_ngcc_current_state = 48;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new facet(this, super._source, this.$runtime, 610);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 51:
            this.action13();
            this.$_ngcc_current_state = 49;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 52:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               NGCCHandler h = new simpleType(this, super._source, this.$runtime, 614);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 51;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 54:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 617, this.annotation, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 52;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 56:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 619, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 59:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 61:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 626, this.annotation, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 35;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 63:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("restriction")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("extension"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 628, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 65:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 634, (AnnotationImpl)null, AnnotationContext.COMPLEXTYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 67:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleContent")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexContent")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 636, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 68:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 67;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 72:
            if (($ai = this.$runtime.getAttributeIndex("", "mixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 68;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 76:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 72;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 80:
            if (($ai = this.$runtime.getAttributeIndex("", "block")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 76;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 84:
            if (($ai = this.$runtime.getAttributeIndex("", "abstract")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 80;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 88:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action16();
               this.$_ngcc_current_state = 84;
            } else {
               this.unexpectedEnterElement($__qname);
            }
      }

   }

   public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      foreignAttributes h;
      attributeUses h;
      complexType_complexContent_body h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               this.action1();
               h = new complexType_complexContent_body(this, super._source, this.$runtime, 557, this.result);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 3:
         case 4:
         case 5:
         case 7:
         case 11:
         case 13:
         case 14:
         case 16:
         case 20:
         case 22:
         case 23:
         case 25:
         case 27:
         case 28:
         case 30:
         case 31:
         case 32:
         case 33:
         case 35:
         case 40:
         case 42:
         case 43:
         case 45:
         case 50:
         case 53:
         case 55:
         case 57:
         case 58:
         case 60:
         case 62:
         case 63:
         case 64:
         case 66:
         case 69:
         case 70:
         case 71:
         case 73:
         case 74:
         case 75:
         case 77:
         case 78:
         case 79:
         case 81:
         case 82:
         case 83:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 6:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexContent")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 8:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 6;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 9:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               h = new complexType_complexContent_body(this, super._source, this.$runtime, 560, this.result);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               h = new foreignAttributes(this, super._source, this.$runtime, 564, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 15:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 17:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 6;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 18:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               h = new complexType_complexContent_body(this, super._source, this.$runtime, 571, this.result);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 21:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               h = new foreignAttributes(this, super._source, this.$runtime, 575, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 24:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 26:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 29:
            if (($ai = this.$runtime.getAttributeIndex("", "mixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 34:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleContent")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 36:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 34;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 37:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               h = new attributeUses(this, super._source, this.$runtime, 594, this.result);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 38:
            this.action8();
            this.$_ngcc_current_state = 37;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 39:
            this.$_ngcc_current_state = 38;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 41:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("extension")) {
               h = new foreignAttributes(this, super._source, this.$runtime, 599, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 44:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 46:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 34;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 47:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               h = new attributeUses(this, super._source, this.$runtime, 606, this.result);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 48:
            this.action11();
            this.$_ngcc_current_state = 47;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 49:
            this.$_ngcc_current_state = 48;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 51:
            this.action13();
            this.$_ngcc_current_state = 49;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 52:
            this.$_ngcc_current_state = 51;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 54:
            this.$_ngcc_current_state = 52;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 56:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               h = new foreignAttributes(this, super._source, this.$runtime, 619, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 59:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 61:
            this.$_ngcc_current_state = 35;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 65:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 67:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               h = new foreignAttributes(this, super._source, this.$runtime, 636, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 68:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 67;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 72:
            if (($ai = this.$runtime.getAttributeIndex("", "mixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 68;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 76:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 72;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 80:
            if (($ai = this.$runtime.getAttributeIndex("", "block")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 76;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 84:
            if (($ai = this.$runtime.getAttributeIndex("", "abstract")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 80;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
      }

   }

   public void enterAttribute(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         case 12:
         case 13:
         case 14:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 25:
         case 27:
         case 28:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 40:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         case 50:
         case 53:
         case 55:
         case 56:
         case 57:
         case 58:
         case 60:
         case 62:
         case 63:
         case 64:
         case 66:
         case 67:
         case 69:
         case 70:
         case 71:
         case 73:
         case 74:
         case 75:
         case 77:
         case 78:
         case 79:
         case 81:
         case 82:
         case 83:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 15:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 14;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 24:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 23;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 26:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 29:
            if ($__uri.equals("") && $__local.equals("mixed")) {
               this.$_ngcc_current_state = 31;
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 38:
            this.action8();
            this.$_ngcc_current_state = 37;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 39:
            this.$_ngcc_current_state = 38;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 44:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 43;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 48:
            this.action11();
            this.$_ngcc_current_state = 47;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 49:
            this.$_ngcc_current_state = 48;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 51:
            this.action13();
            this.$_ngcc_current_state = 49;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 52:
            this.$_ngcc_current_state = 51;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 54:
            this.$_ngcc_current_state = 52;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 59:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 58;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 61:
            this.$_ngcc_current_state = 35;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 65:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 68:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 70;
            } else {
               this.$_ngcc_current_state = 67;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 72:
            if ($__uri.equals("") && $__local.equals("mixed")) {
               this.$_ngcc_current_state = 74;
            } else {
               this.$_ngcc_current_state = 68;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 76:
            if ($__uri.equals("") && $__local.equals("final")) {
               this.$_ngcc_current_state = 78;
            } else {
               this.$_ngcc_current_state = 72;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 80:
            if ($__uri.equals("") && $__local.equals("block")) {
               this.$_ngcc_current_state = 82;
            } else {
               this.$_ngcc_current_state = 76;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 84:
            if ($__uri.equals("") && $__local.equals("abstract")) {
               this.$_ngcc_current_state = 86;
            } else {
               this.$_ngcc_current_state = 80;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
      }

   }

   public void leaveAttribute(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         case 12:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 23:
         case 24:
         case 25:
         case 27:
         case 28:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 40:
         case 41:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 50:
         case 53:
         case 55:
         case 56:
         case 58:
         case 59:
         case 60:
         case 62:
         case 63:
         case 64:
         case 66:
         case 67:
         case 70:
         case 71:
         case 74:
         case 75:
         case 78:
         case 79:
         case 82:
         case 83:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 12;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 22:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 21;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 26:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 29:
            this.$_ngcc_current_state = 28;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 30:
            if ($__uri.equals("") && $__local.equals("mixed")) {
               this.$_ngcc_current_state = 28;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 38:
            this.action8();
            this.$_ngcc_current_state = 37;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 39:
            this.$_ngcc_current_state = 38;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 42:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 41;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 48:
            this.action11();
            this.$_ngcc_current_state = 47;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 49:
            this.$_ngcc_current_state = 48;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 51:
            this.action13();
            this.$_ngcc_current_state = 49;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 52:
            this.$_ngcc_current_state = 51;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 54:
            this.$_ngcc_current_state = 52;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 57:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 56;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 61:
            this.$_ngcc_current_state = 35;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 65:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 68:
            this.$_ngcc_current_state = 67;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 69:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 67;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 72:
            this.$_ngcc_current_state = 68;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 73:
            if ($__uri.equals("") && $__local.equals("mixed")) {
               this.$_ngcc_current_state = 68;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 76:
            this.$_ngcc_current_state = 72;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 77:
            if ($__uri.equals("") && $__local.equals("final")) {
               this.$_ngcc_current_state = 72;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 80:
            this.$_ngcc_current_state = 76;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 81:
            if ($__uri.equals("") && $__local.equals("block")) {
               this.$_ngcc_current_state = 76;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 84:
            this.$_ngcc_current_state = 80;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 85:
            if ($__uri.equals("") && $__local.equals("abstract")) {
               this.$_ngcc_current_state = 80;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      erSet h;
      qname h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         case 12:
         case 13:
         case 16:
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 25:
         case 27:
         case 28:
         case 30:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 40:
         case 41:
         case 42:
         case 45:
         case 46:
         case 47:
         case 50:
         case 53:
         case 55:
         case 56:
         case 57:
         case 60:
         case 62:
         case 63:
         case 64:
         case 66:
         case 67:
         case 69:
         case 71:
         case 73:
         case 75:
         case 77:
         case 79:
         case 81:
         case 83:
         case 85:
         default:
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 14:
            h = new qname(this, super._source, this.$runtime, 566);
            this.spawnChildFromText(h, $value);
            break;
         case 15:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 23:
            h = new qname(this, super._source, this.$runtime, 577);
            this.spawnChildFromText(h, $value);
            break;
         case 24:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 26:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 29:
            if (($ai = this.$runtime.getAttributeIndex("", "mixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 31:
            this.mixedValue = $value;
            this.$_ngcc_current_state = 30;
            break;
         case 38:
            this.action8();
            this.$_ngcc_current_state = 37;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 39:
            this.$_ngcc_current_state = 38;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 43:
            h = new qname(this, super._source, this.$runtime, 601);
            this.spawnChildFromText(h, $value);
            break;
         case 44:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 48:
            this.action11();
            this.$_ngcc_current_state = 47;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 49:
            this.$_ngcc_current_state = 48;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 51:
            this.action13();
            this.$_ngcc_current_state = 49;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 52:
            this.$_ngcc_current_state = 51;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 54:
            this.$_ngcc_current_state = 52;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 58:
            h = new qname(this, super._source, this.$runtime, 621);
            this.spawnChildFromText(h, $value);
            break;
         case 59:
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 61:
            this.$_ngcc_current_state = 35;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 65:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 68:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 67;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 70:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 69;
            break;
         case 72:
            if (($ai = this.$runtime.getAttributeIndex("", "mixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 68;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 74:
            this.mixedValue = $value;
            this.$_ngcc_current_state = 73;
            break;
         case 76:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 72;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 78:
            h = new erSet(this, super._source, this.$runtime, 648);
            this.spawnChildFromText(h, $value);
            break;
         case 80:
            if (($ai = this.$runtime.getAttributeIndex("", "block")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 76;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 82:
            h = new erSet(this, super._source, this.$runtime, 653);
            this.spawnChildFromText(h, $value);
            break;
         case 84:
            if (($ai = this.$runtime.getAttributeIndex("", "abstract")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 80;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 86:
            this.abstractValue = $value;
            this.$_ngcc_current_state = 85;
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 557:
            this.explicitContent = (ContentTypeImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
         case 558:
         case 559:
         case 561:
         case 563:
         case 565:
         case 567:
         case 568:
         case 569:
         case 570:
         case 572:
         case 574:
         case 576:
         case 578:
         case 579:
         case 580:
         case 581:
         case 583:
         case 585:
         case 586:
         case 587:
         case 588:
         case 589:
         case 590:
         case 591:
         case 592:
         case 593:
         case 595:
         case 596:
         case 598:
         case 600:
         case 602:
         case 603:
         case 604:
         case 605:
         case 607:
         case 608:
         case 611:
         case 612:
         case 613:
         case 615:
         case 616:
         case 618:
         case 620:
         case 622:
         case 623:
         case 624:
         case 625:
         case 627:
         case 629:
         case 630:
         case 631:
         case 632:
         case 633:
         case 635:
         case 637:
         case 638:
         case 639:
         case 640:
         case 641:
         case 642:
         case 643:
         case 644:
         case 645:
         case 646:
         case 647:
         case 649:
         case 650:
         case 651:
         case 652:
         default:
            break;
         case 560:
            this.explicitContent = (ContentTypeImpl)$__result__;
            this.action2();
            this.$_ngcc_current_state = 8;
            break;
         case 562:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 9;
            break;
         case 564:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 10;
            break;
         case 566:
            this.baseTypeName = (UName)$__result__;
            this.action3();
            this.$_ngcc_current_state = 13;
            break;
         case 571:
            this.explicitContent = (ContentTypeImpl)$__result__;
            this.action5();
            this.$_ngcc_current_state = 17;
            break;
         case 573:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 18;
            break;
         case 575:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 19;
            break;
         case 577:
            this.baseTypeName = (UName)$__result__;
            this.action6();
            this.$_ngcc_current_state = 22;
            break;
         case 582:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 7;
            break;
         case 584:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 26;
            break;
         case 594:
            this.$_ngcc_current_state = 36;
            break;
         case 597:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 38;
            break;
         case 599:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 39;
            break;
         case 601:
            this.baseTypeName = (UName)$__result__;
            this.action9();
            this.$_ngcc_current_state = 42;
            break;
         case 606:
            this.$_ngcc_current_state = 46;
            break;
         case 609:
            this.facet = (XSFacet)$__result__;
            this.action12();
            this.$_ngcc_current_state = 48;
            break;
         case 610:
            this.facet = (XSFacet)$__result__;
            this.action12();
            this.$_ngcc_current_state = 48;
            break;
         case 614:
            this.baseContentType = (SimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 51;
            break;
         case 617:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 52;
            break;
         case 619:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 54;
            break;
         case 621:
            this.baseTypeName = (UName)$__result__;
            this.action14();
            this.$_ngcc_current_state = 57;
            break;
         case 626:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 35;
            break;
         case 628:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 61;
            break;
         case 634:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 2;
            break;
         case 636:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 65;
            break;
         case 648:
            this.finalValue = (Integer)$__result__;
            this.$_ngcc_current_state = 77;
            break;
         case 653:
            this.blockValue = (Integer)$__result__;
            this.$_ngcc_current_state = 81;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   private void makeResult(int derivationMethod) {
      if (this.finalValue == null) {
         this.finalValue = this.$runtime.finalDefault;
      }

      if (this.blockValue == null) {
         this.blockValue = this.$runtime.blockDefault;
      }

      this.result = new ComplexTypeImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.name, this.name == null, this.$runtime.parseBoolean(this.abstractValue), derivationMethod, this.baseType, this.finalValue, this.blockValue, this.$runtime.parseBoolean(this.mixedValue));
   }

   private Ref.ContentType buildComplexExtensionContentModel(XSContentType explicitContent) {
      return (Ref.ContentType)(explicitContent == this.$runtime.parser.schemaSet.empty ? new BaseComplexTypeContentRef(this.baseType) : new InheritBaseContentTypeRef(this.baseType, explicitContent, this.$runtime));
   }

   private static class InheritBaseContentTypeRef implements Ref.ContentType {
      private final Ref.Type baseType;
      private final XSContentType empty;
      private final XSContentType expContent;
      private final SchemaDocumentImpl currentDocument;

      private InheritBaseContentTypeRef(Ref.Type _baseType, XSContentType _explicitContent, NGCCRuntimeEx $runtime) {
         this.baseType = _baseType;
         this.currentDocument = $runtime.document;
         this.expContent = _explicitContent;
         this.empty = $runtime.parser.schemaSet.empty;
      }

      public XSContentType getContentType() {
         XSContentType baseContentType = ((XSComplexType)this.baseType.getType()).getContentType();
         return (XSContentType)(baseContentType == this.empty ? this.expContent : new ParticleImpl(this.currentDocument, (AnnotationImpl)null, new ModelGroupImpl(this.currentDocument, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null, XSModelGroup.SEQUENCE, new ParticleImpl[]{(ParticleImpl)baseContentType, (ParticleImpl)this.expContent}), (Locator)null));
      }

      // $FF: synthetic method
      InheritBaseContentTypeRef(Ref.Type x0, XSContentType x1, NGCCRuntimeEx x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static class BaseComplexTypeContentRef implements Ref.ContentType {
      private final Ref.Type baseType;

      private BaseComplexTypeContentRef(Ref.Type _baseType) {
         this.baseType = _baseType;
      }

      public XSContentType getContentType() {
         return ((XSComplexType)this.baseType.getType()).getContentType();
      }

      // $FF: synthetic method
      BaseComplexTypeContentRef(Ref.Type x0, Object x1) {
         this(x0);
      }
   }

   private static class BaseContentSimpleTypeRef implements Ref.SimpleType {
      private final Ref.Type baseType;

      private BaseContentSimpleTypeRef(Ref.Type _baseType) {
         this.baseType = _baseType;
      }

      public XSSimpleType getType() {
         return (XSSimpleType)((XSComplexType)this.baseType.getType()).getContentType();
      }

      // $FF: synthetic method
      BaseContentSimpleTypeRef(Ref.Type x0, Object x1) {
         this(x0);
      }
   }
}
