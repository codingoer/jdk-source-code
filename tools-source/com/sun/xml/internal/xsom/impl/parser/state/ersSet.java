package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class ersSet extends NGCCHandler {
   private String v;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public ersSet(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 1;
   }

   public ersSet(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         default:
            this.unexpectedEnterElement($__qname);
      }

   }

   public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromEnterAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromLeaveAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         default:
            this.unexpectedLeaveAttribute($__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.makeResult(), super._cookie, $value);
            break;
         case 1:
            this.v = $value;
            this.$_ngcc_current_state = 0;
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   private Integer makeResult() {
      if (this.v == null) {
         return new Integer(this.$runtime.blockDefault);
      } else if (this.v.indexOf("#all") != -1) {
         return new Integer(7);
      } else {
         int r = 0;
         if (this.v.indexOf("extension") != -1) {
            r |= 1;
         }

         if (this.v.indexOf("restriction") != -1) {
            r |= 2;
         }

         if (this.v.indexOf("substitution") != -1) {
            r |= 4;
         }

         return new Integer(r);
      }
   }
}
