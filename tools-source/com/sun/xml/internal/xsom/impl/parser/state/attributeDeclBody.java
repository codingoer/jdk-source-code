package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.AttributeDeclImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class attributeDeclBody extends NGCCHandler {
   private String name;
   private ForeignAttributesImpl fa;
   private AnnotationImpl annotation;
   private Locator locator;
   private boolean isLocal;
   private String defaultValue;
   private UName typeName;
   private String fixedValue;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private boolean form;
   private boolean formSpecified;
   private Ref.SimpleType type;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public attributeDeclBody(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, Locator _locator, boolean _isLocal, String _defaultValue, String _fixedValue) {
      super(source, parent, cookie);
      this.formSpecified = false;
      this.$runtime = runtime;
      this.locator = _locator;
      this.isLocal = _isLocal;
      this.defaultValue = _defaultValue;
      this.fixedValue = _fixedValue;
      this.$_ngcc_current_state = 13;
   }

   public attributeDeclBody(NGCCRuntimeEx runtime, Locator _locator, boolean _isLocal, String _defaultValue, String _fixedValue) {
      this((NGCCHandler)null, runtime, runtime, -1, _locator, _isLocal, _defaultValue, _fixedValue);
   }

   private void action0() throws SAXException {
      this.type = new DelayedRef.SimpleType(this.$runtime, this.locator, this.$runtime.currentSchema, this.typeName);
   }

   private void action1() throws SAXException {
      this.formSpecified = true;
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               NGCCHandler h = new simpleType(this, super._source, this.$runtime, 379);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         case 10:
         case 11:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 7:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 388, (AnnotationImpl)null, AnnotationContext.ATTRIBUTE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 9:
            foreignAttributes h;
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "type") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType"))) {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 12:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "form")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 12;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
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
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         case 10:
         case 11:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 7:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            foreignAttributes h;
            if (this.$runtime.getAttributeIndex("", "type") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 12:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "form")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 12;
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
            this.revertToParentFromEnterAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("") && $__local.equals("type")) {
               this.$_ngcc_current_state = 5;
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         case 10:
         case 11:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 7:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            foreignAttributes h;
            if ($__uri.equals("") && $__local.equals("type")) {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 11;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("form")) {
               this.$_ngcc_current_state = 15;
            } else {
               this.$_ngcc_current_state = 12;
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
            this.revertToParentFromLeaveAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 3:
         case 5:
         case 6:
         case 8:
         case 11:
         case 12:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 4:
            if ($__uri.equals("") && $__local.equals("type")) {
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 7:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
            this.spawnChildFromLeaveAttribute(h, $__uri, $__local, $__qname);
            break;
         case 10:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 9;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 13:
            this.$_ngcc_current_state = 12;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 14:
            if ($__uri.equals("") && $__local.equals("form")) {
               this.$_ngcc_current_state = 12;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.makeResult(), super._cookie, $value);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendText(super._cookie, $value);
            }
         case 2:
         case 3:
         case 4:
         case 6:
         case 8:
         case 10:
         case 14:
         default:
            break;
         case 5:
            NGCCHandler h = new qname(this, super._source, this.$runtime, 381);
            this.spawnChildFromText(h, $value);
            break;
         case 7:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 9:
            foreignAttributes h;
            if (this.$runtime.getAttributeIndex("", "type") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromText(h, $value);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 390, this.fa);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 11:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 10;
            break;
         case 12:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "form")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 12;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 15:
            qualification h;
            if ($value.equals("unqualified")) {
               h = new qualification(this, super._source, this.$runtime, 395);
               this.spawnChildFromText(h, $value);
            } else if ($value.equals("qualified")) {
               h = new qualification(this, super._source, this.$runtime, 395);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 379:
            this.type = (SimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 0;
            break;
         case 381:
            this.typeName = (UName)$__result__;
            this.action0();
            this.$_ngcc_current_state = 4;
            break;
         case 388:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 390:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 7;
            break;
         case 395:
            this.form = (Boolean)$__result__;
            this.action1();
            this.$_ngcc_current_state = 14;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 7;
   }

   private AttributeDeclImpl makeResult() {
      if (this.type == null) {
         this.type = this.$runtime.parser.schemaSet.anySimpleType;
      }

      if (!this.formSpecified) {
         this.form = this.$runtime.attributeFormDefault;
      }

      if (!this.isLocal) {
         this.form = true;
      }

      String tns;
      if (this.form) {
         tns = this.$runtime.currentSchema.getTargetNamespace();
      } else {
         tns = "";
      }

      return new AttributeDeclImpl(this.$runtime.document, tns, this.name, this.annotation, this.locator, this.fa, this.isLocal, this.$runtime.createXmlString(this.defaultValue), this.$runtime.createXmlString(this.fixedValue), this.type);
   }
}
