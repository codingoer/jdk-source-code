package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.RestrictionSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class SimpleType_Restriction extends NGCCHandler {
   private Locator locator;
   private AnnotationImpl annotation;
   private String name;
   private UName baseTypeName;
   private Set finalSet;
   private ForeignAttributesImpl fa;
   private XSFacet facet;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private RestrictionSimpleTypeImpl result;
   private Ref.SimpleType baseType;
   private Locator rloc;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public SimpleType_Restriction(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.annotation = _annotation;
      this.locator = _locator;
      this.fa = _fa;
      this.name = _name;
      this.finalSet = _finalSet;
      this.$_ngcc_current_state = 13;
   }

   public SimpleType_Restriction(NGCCRuntimeEx runtime, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
      this((NGCCHandler)null, runtime, runtime, -1, _annotation, _locator, _fa, _name, _finalSet);
   }

   private void action0() throws SAXException {
      this.result.addFacet(this.facet);
   }

   private void action1() throws SAXException {
      this.result = new RestrictionSimpleTypeImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.name, this.name == null, this.finalSet, this.baseType);
   }

   private void action2() throws SAXException {
      this.baseType = new DelayedRef.SimpleType(this.$runtime, this.rloc, this.$runtime.currentSchema, this.baseTypeName);
   }

   private void action3() throws SAXException {
      this.rloc = this.$runtime.copyLocator();
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      facet h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new facet(this, super._source, this.$runtime, 153);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new facet(this, super._source, this.$runtime, 154);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 4:
            this.action1();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 5:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               NGCCHandler h = new simpleType(this, super._source, this.$runtime, 158);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 10:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 164, this.annotation, AnnotationContext.SIMPLETYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 5;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 12:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "base") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 166, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 13:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action3();
               this.$_ngcc_current_state = 12;
            } else {
               this.unexpectedEnterElement($__qname);
            }
      }

   }

   public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 4:
            this.action1();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            if (this.$runtime.getAttributeIndex("", "base") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 166, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
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
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.action1();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 8;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("base")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 166, this.fa);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedEnterAttribute($__qname);
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
         case 3:
         case 5:
         case 6:
         case 8:
         case 9:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.action1();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            if ($__uri.equals("") && $__local.equals("base")) {
               this.$_ngcc_current_state = 4;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
         case 1:
         case 3:
         case 6:
         case 7:
         case 9:
         case 11:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 4:
            this.action1();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 5:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "base")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 8:
            NGCCHandler h = new qname(this, super._source, this.$runtime, 160);
            this.spawnChildFromText(h, $value);
            break;
         case 10:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 12:
            if (this.$runtime.getAttributeIndex("", "base") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 166, this.fa);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 153:
            this.facet = (XSFacet)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
            break;
         case 154:
            this.facet = (XSFacet)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
         case 155:
         case 156:
         case 157:
         case 159:
         case 161:
         case 162:
         case 163:
         case 165:
         default:
            break;
         case 158:
            this.baseType = (SimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 4;
            break;
         case 160:
            this.baseTypeName = (UName)$__result__;
            this.action2();
            this.$_ngcc_current_state = 7;
            break;
         case 164:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 5;
            break;
         case 166:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 10;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
