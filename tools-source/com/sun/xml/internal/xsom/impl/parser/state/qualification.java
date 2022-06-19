package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class qualification extends NGCCHandler {
   private String text;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public qualification(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 1;
   }

   public qualification(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(new Boolean(this.text.trim().equals("qualified")), super._cookie, $__uri, $__local, $__qname, $attrs);
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
            this.revertToParentFromLeaveElement(new Boolean(this.text.trim().equals("qualified")), super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromEnterAttribute(new Boolean(this.text.trim().equals("qualified")), super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromLeaveAttribute(new Boolean(this.text.trim().equals("qualified")), super._cookie, $__uri, $__local, $__qname);
            break;
         default:
            this.unexpectedLeaveAttribute($__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(new Boolean(this.text.trim().equals("qualified")), super._cookie, $value);
            break;
         case 1:
            if ($value.equals("qualified")) {
               this.text = $value;
               this.$_ngcc_current_state = 0;
            } else if ($value.equals("unqualified")) {
               this.text = $value;
               this.$_ngcc_current_state = 0;
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
