package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import java.math.BigInteger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class occurs extends NGCCHandler {
   private String v;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   BigInteger max;
   BigInteger min;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public occurs(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.max = BigInteger.valueOf(1L);
      this.min = BigInteger.valueOf(1L);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 5;
   }

   public occurs(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.min = new BigInteger(this.v);
   }

   private void action1() throws SAXException {
      this.max = BigInteger.valueOf(-1L);
   }

   private void action2() throws SAXException {
      this.max = new BigInteger(this.v);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "minOccurs")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "maxOccurs")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         default:
            this.unexpectedEnterElement($__qname);
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
            if (($ai = this.$runtime.getAttributeIndex("", "minOccurs")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "maxOccurs")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         default:
            this.unexpectedLeaveElement($__qname);
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
            if ($__uri.equals("") && $__local.equals("minOccurs")) {
               this.$_ngcc_current_state = 3;
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("maxOccurs")) {
               this.$_ngcc_current_state = 7;
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromLeaveAttribute(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
            if ($__uri.equals("") && $__local.equals("minOccurs")) {
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 3:
         case 4:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 5:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            if ($__uri.equals("") && $__local.equals("maxOccurs")) {
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this, super._cookie, $value);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "minOccurs")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendText(super._cookie, $value);
            }
         case 2:
         case 4:
         case 6:
         default:
            break;
         case 3:
            this.v = $value;
            this.$_ngcc_current_state = 2;
            this.action0();
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "maxOccurs")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 7:
            if ($value.equals("unbounded")) {
               this.$_ngcc_current_state = 6;
               this.action1();
            } else {
               this.v = $value;
               this.$_ngcc_current_state = 6;
               this.action2();
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 5 || this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 1;
   }
}
