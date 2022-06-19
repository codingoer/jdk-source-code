package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.NotationImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class notation extends NGCCHandler {
   private String name;
   private String pub;
   private ForeignAttributesImpl fa;
   private String sys;
   private AnnotationImpl ann;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private Locator loc;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public notation(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 16;
   }

   public notation(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.loc = this.$runtime.copyLocator();
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
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 209, (AnnotationImpl)null, AnnotationContext.NOTATION);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 4:
            if (($ai = this.$runtime.getAttributeIndex("", "system")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 8:
            if (($ai = this.$runtime.getAttributeIndex("", "public")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 14:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 15:
            if (this.$runtime.getAttributeIndex("", "name") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 224, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 16:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("notation")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action0();
               this.$_ngcc_current_state = 15;
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
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("notation")) {
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
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 4:
            if (($ai = this.$runtime.getAttributeIndex("", "system")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 8:
            if (($ai = this.$runtime.getAttributeIndex("", "public")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 14:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 15:
            if (this.$runtime.getAttributeIndex("", "name") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("notation")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 224, (ForeignAttributesImpl)null);
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
            this.revertToParentFromEnterAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            if ($__uri.equals("") && $__local.equals("system")) {
               this.$_ngcc_current_state = 6;
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("public")) {
               this.$_ngcc_current_state = 10;
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 14:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 13;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 15:
            if ($__uri.equals("") && $__local.equals("name")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 224, (ForeignAttributesImpl)null);
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
            this.revertToParentFromLeaveAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 3:
         case 6:
         case 7:
         case 10:
         case 11:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("system")) {
               this.$_ngcc_current_state = 2;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 8:
            this.$_ngcc_current_state = 4;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            if ($__uri.equals("") && $__local.equals("public")) {
               this.$_ngcc_current_state = 4;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 8;
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
         case 1:
         case 3:
         case 5:
         case 7:
         case 9:
         case 11:
         case 12:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 4:
            if (($ai = this.$runtime.getAttributeIndex("", "system")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 6:
            this.sys = $value;
            this.$_ngcc_current_state = 5;
            break;
         case 8:
            if (($ai = this.$runtime.getAttributeIndex("", "public")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 4;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 10:
            this.pub = $value;
            this.$_ngcc_current_state = 9;
            break;
         case 13:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 12;
            break;
         case 14:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 15:
            if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 224, (ForeignAttributesImpl)null);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 209:
            this.ann = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 224:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 14;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   private XSNotation makeResult() {
      return new NotationImpl(this.$runtime.document, this.ann, this.loc, this.fa, this.name, this.pub, this.sys);
   }
}
