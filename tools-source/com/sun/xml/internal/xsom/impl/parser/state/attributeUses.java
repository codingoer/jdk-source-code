package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.AttributeDeclImpl;
import com.sun.xml.internal.xsom.impl.AttributeUseImpl;
import com.sun.xml.internal.xsom.impl.AttributesHolder;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.WildcardImpl;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class attributeUses extends NGCCHandler {
   private String use;
   private AttributesHolder owner;
   private ForeignAttributesImpl fa;
   private WildcardImpl wildcard;
   private AnnotationImpl annotation;
   private UName attDeclName;
   private AttributeDeclImpl anonymousDecl;
   private String defaultValue;
   private String fixedValue;
   private UName groupName;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private Ref.Attribute decl;
   private Locator wloc;
   private Locator locator;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public attributeUses(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AttributesHolder _owner) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.owner = _owner;
      this.$_ngcc_current_state = 5;
   }

   public attributeUses(NGCCRuntimeEx runtime, AttributesHolder _owner) {
      this((NGCCHandler)null, runtime, runtime, -1, _owner);
   }

   private void action0() throws SAXException {
      this.owner.setWildcard(this.wildcard);
   }

   private void action1() throws SAXException {
      this.wloc = this.$runtime.copyLocator();
   }

   private void action2() throws SAXException {
      this.owner.addAttGroup(new DelayedRef.AttGroup(this.$runtime, this.locator, this.$runtime.currentSchema, this.groupName));
   }

   private void action3() throws SAXException {
      this.locator = this.$runtime.copyLocator();
   }

   private void action4() throws SAXException {
      if ("prohibited".equals(this.use)) {
         this.owner.addProhibitedAttribute(this.attDeclName);
      } else {
         this.owner.addAttributeUse(this.attDeclName, new AttributeUseImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.decl, this.$runtime.createXmlString(this.defaultValue), this.$runtime.createXmlString(this.fixedValue), "required".equals(this.use)));
      }

   }

   private void action5() throws SAXException {
      this.decl = new DelayedRef.Attribute(this.$runtime, this.locator, this.$runtime.currentSchema, this.attDeclName);
   }

   private void action6() throws SAXException {
      this.decl = this.anonymousDecl;
      this.attDeclName = new UName(this.anonymousDecl.getTargetNamespace(), this.anonymousDecl.getName());
      this.defaultValue = null;
      this.fixedValue = null;
   }

   private void action7() throws SAXException {
      this.locator = this.$runtime.copyLocator();
      this.use = null;
      this.defaultValue = null;
      this.fixedValue = null;
      this.decl = null;
      this.annotation = null;
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      annotation h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action7();
               this.$_ngcc_current_state = 33;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action3();
               this.$_ngcc_current_state = 13;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("anyAttribute")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action1();
               this.$_ngcc_current_state = 3;
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
         case 4:
         case 6:
         case 7:
         case 10:
         case 11:
         case 12:
         case 14:
         case 15:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         case 31:
         case 32:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 3:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && this.$runtime.getAttributeIndex("", "namespace") < 0 && this.$runtime.getAttributeIndex("", "processContents") < 0) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new wildcardBody(this, super._source, this.$runtime, 290, this.wloc);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 5:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action7();
               this.$_ngcc_current_state = 33;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action3();
               this.$_ngcc_current_state = 13;
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 8:
            this.action2();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 9:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 297, (AnnotationImpl)null, AnnotationContext.ATTRIBUTE_USE);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 8;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 16:
            this.action4();
            this.$_ngcc_current_state = 15;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 17:
            if (this.$runtime.getAttributeIndex("", "name") < 0 && this.$runtime.getAttributeIndex("", "form") < 0) {
               if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
                  this.$runtime.consumeAttribute($ai);
                  this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
               } else {
                  this.unexpectedEnterElement($__qname);
               }
            } else {
               NGCCHandler h = new attributeDeclBody(this, super._source, this.$runtime, 315, this.locator, true, this.defaultValue, this.fixedValue);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 19:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 308, (AnnotationImpl)null, AnnotationContext.ATTRIBUTE_USE);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 18;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 25:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 17;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 29:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 25;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 33:
            if (($ai = this.$runtime.getAttributeIndex("", "use")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 29;
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
            this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("anyAttribute")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 3:
            if ((this.$runtime.getAttributeIndex("", "namespace") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (this.$runtime.getAttributeIndex("", "processContents") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new wildcardBody(this, super._source, this.$runtime, 290, this.wloc);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 4:
         case 6:
         case 10:
         case 11:
         case 12:
         case 14:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         case 31:
         case 32:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 5:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 8:
            this.action2();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            this.$_ngcc_current_state = 8;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 15:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 16:
            this.action4();
            this.$_ngcc_current_state = 15;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 17:
            if (this.$runtime.getAttributeIndex("", "name") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute") || this.$runtime.getAttributeIndex("", "form") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               NGCCHandler h = new attributeDeclBody(this, super._source, this.$runtime, 315, this.locator, true, this.defaultValue, this.fixedValue);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 18:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 306, (ForeignAttributesImpl)null);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 25:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 17;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 29:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 25;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 33:
            if (($ai = this.$runtime.getAttributeIndex("", "use")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 29;
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
            this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 4:
         case 6:
         case 7:
         case 10:
         case 11:
         case 12:
         case 14:
         case 15:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 26:
         case 27:
         case 28:
         case 30:
         case 31:
         case 32:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 3:
            if ((!$__uri.equals("") || !$__local.equals("namespace")) && (!$__uri.equals("") || !$__local.equals("processContents"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               NGCCHandler h = new wildcardBody(this, super._source, this.$runtime, 290, this.wloc);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 5:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            this.action2();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            this.$_ngcc_current_state = 8;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 12;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 16:
            this.action4();
            this.$_ngcc_current_state = 15;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 17:
            if ($__uri.equals("") && $__local.equals("name") || $__uri.equals("") && $__local.equals("form")) {
               NGCCHandler h = new attributeDeclBody(this, super._source, this.$runtime, 315, this.locator, true, this.defaultValue, this.fixedValue);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            } else if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 22;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 25:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 27;
            } else {
               this.$_ngcc_current_state = 17;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 29:
            if ($__uri.equals("") && $__local.equals("default")) {
               this.$_ngcc_current_state = 31;
            } else {
               this.$_ngcc_current_state = 25;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 33:
            if ($__uri.equals("") && $__local.equals("use")) {
               this.$_ngcc_current_state = 35;
            } else {
               this.$_ngcc_current_state = 29;
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
            this.revertToParentFromLeaveAttribute(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 10:
         case 12:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 20:
         case 22:
         case 23:
         case 24:
         case 27:
         case 28:
         case 31:
         case 32:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 5:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            this.action2();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            this.$_ngcc_current_state = 8;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 9;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 16:
            this.action4();
            this.$_ngcc_current_state = 15;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 21:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 19;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 25:
            this.$_ngcc_current_state = 17;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 26:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 17;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 29:
            this.$_ngcc_current_state = 25;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 30:
            if ($__uri.equals("") && $__local.equals("default")) {
               this.$_ngcc_current_state = 25;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 33:
            this.$_ngcc_current_state = 29;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 34:
            if ($__uri.equals("") && $__local.equals("use")) {
               this.$_ngcc_current_state = 29;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      qname h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this, super._cookie, $value);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendText(super._cookie, $value);
         case 2:
         case 4:
         case 6:
         case 7:
         case 10:
         case 11:
         case 14:
         case 15:
         case 18:
         case 20:
         case 21:
         case 23:
         case 24:
         case 26:
         case 28:
         case 30:
         case 32:
         case 34:
         default:
            break;
         case 3:
            wildcardBody h;
            if (this.$runtime.getAttributeIndex("", "processContents") >= 0) {
               h = new wildcardBody(this, super._source, this.$runtime, 290, this.wloc);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "namespace") >= 0) {
               h = new wildcardBody(this, super._source, this.$runtime, 290, this.wloc);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 5:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 8:
            this.action2();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 9:
            this.$_ngcc_current_state = 8;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 12:
            h = new qname(this, super._source, this.$runtime, 300);
            this.spawnChildFromText(h, $value);
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 16:
            this.action4();
            this.$_ngcc_current_state = 15;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 17:
            attributeDeclBody h;
            if (this.$runtime.getAttributeIndex("", "form") >= 0) {
               h = new attributeDeclBody(this, super._source, this.$runtime, 315, this.locator, true, this.defaultValue, this.fixedValue);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               h = new attributeDeclBody(this, super._source, this.$runtime, 315, this.locator, true, this.defaultValue, this.fixedValue);
               this.spawnChildFromText(h, $value);
            } else if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 19:
            this.$_ngcc_current_state = 18;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 22:
            h = new qname(this, super._source, this.$runtime, 311);
            this.spawnChildFromText(h, $value);
            break;
         case 25:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 17;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 27:
            this.fixedValue = $value;
            this.$_ngcc_current_state = 26;
            break;
         case 29:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 25;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 31:
            this.defaultValue = $value;
            this.$_ngcc_current_state = 30;
            break;
         case 33:
            if (($ai = this.$runtime.getAttributeIndex("", "use")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 29;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 35:
            this.use = $value;
            this.$_ngcc_current_state = 34;
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 290:
            this.wildcard = (WildcardImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 2;
            break;
         case 297:
            this.$_ngcc_current_state = 8;
            break;
         case 300:
            this.groupName = (UName)$__result__;
            this.$_ngcc_current_state = 11;
            break;
         case 306:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 16;
            break;
         case 308:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 18;
            break;
         case 311:
            this.attDeclName = (UName)$__result__;
            this.action5();
            this.$_ngcc_current_state = 21;
            break;
         case 315:
            this.anonymousDecl = (AttributeDeclImpl)$__result__;
            this.action6();
            this.$_ngcc_current_state = 16;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 5;
   }
}
