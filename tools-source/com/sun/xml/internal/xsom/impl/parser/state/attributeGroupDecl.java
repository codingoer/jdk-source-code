package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.AttGroupDeclImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class attributeGroupDecl extends NGCCHandler {
   private AnnotationImpl annotation;
   private String name;
   private ForeignAttributesImpl fa;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private AttGroupDeclImpl result;
   private Locator locator;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public attributeGroupDecl(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 14;
   }

   public attributeGroupDecl(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.result = new AttGroupDeclImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.name);
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
         case 5:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new attributeUses(this, super._source, this.$runtime, 241, this.result);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 3:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 4:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 244, (AnnotationImpl)null, AnnotationContext.ATTRIBUTE_GROUP);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 3;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 6:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 246, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 7:
            if (($ai = this.$runtime.getAttributeIndex("", "id")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 14:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action1();
               this.$_ngcc_current_state = 13;
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
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               NGCCHandler h = new attributeUses(this, super._source, this.$runtime, 241, this.result);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 3:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 6:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 246, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 7:
            if (($ai = this.$runtime.getAttributeIndex("", "id")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
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
         case 1:
         case 2:
         case 5:
         case 6:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 3:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            if ($__uri.equals("") && $__local.equals("id")) {
               this.$_ngcc_current_state = 9;
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 12;
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
         case 2:
         case 5:
         case 6:
         case 9:
         case 10:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 3:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            this.$_ngcc_current_state = 6;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("id")) {
               this.$_ngcc_current_state = 6;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 7;
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
         case 2:
         case 5:
         case 6:
         case 8:
         case 10:
         case 11:
         default:
            break;
         case 3:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 7:
            if (($ai = this.$runtime.getAttributeIndex("", "id")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 9:
            this.$_ngcc_current_state = 8;
            break;
         case 12:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 11;
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 241:
            this.$_ngcc_current_state = 1;
            break;
         case 244:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 3;
            break;
         case 246:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 4;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
