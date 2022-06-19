package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.FacetImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class facet extends NGCCHandler {
   private AnnotationImpl annotation;
   private String fixed;
   private String value;
   private ForeignAttributesImpl fa;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private FacetImpl result;
   private Locator locator;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public facet(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 12;
   }

   public facet(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.result = new FacetImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.$localName, this.$runtime.createXmlString(this.value), this.$runtime.parseBoolean(this.fixed));
   }

   private void action1() throws SAXException {
      this.locator = this.$runtime.copyLocator();
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
         case 3:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 228, (AnnotationImpl)null, AnnotationContext.SIMPLETYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 4:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 230, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "value")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 12:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action1();
               this.$_ngcc_current_state = 11;
            }
      }

   }

   public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
               this.action0();
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
         case 10:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 4:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxExclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxInclusive")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("totalDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("fractionDigits")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("length")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("maxLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("minLength")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("enumeration")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("whiteSpace")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("pattern"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 230, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "value")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 7;
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("value")) {
               this.$_ngcc_current_state = 10;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         default:
            this.unexpectedEnterAttribute($__qname);
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
         case 4:
         case 7:
         case 8:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
            this.$_ngcc_current_state = 4;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 4;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 9:
            if ($__uri.equals("") && $__local.equals("value")) {
               this.$_ngcc_current_state = 5;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
         case 1:
         case 3:
         case 4:
         case 6:
         case 8:
         case 9:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 7:
            this.fixed = $value;
            this.$_ngcc_current_state = 6;
            break;
         case 10:
            this.value = $value;
            this.$_ngcc_current_state = 9;
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "value")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 228:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 230:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 2;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
