package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.XPathImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class xpath extends NGCCHandler {
   private String xpath;
   private ForeignAttributesImpl fa;
   private AnnotationImpl ann;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public xpath(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 6;
   }

   public xpath(NGCCRuntimeEx runtime) {
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
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 72, (AnnotationImpl)null, AnnotationContext.XPATH);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
         case 3:
         case 4:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 5:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 6:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 77, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
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
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 3:
         case 4:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 5:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 6:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 77, (ForeignAttributesImpl)null);
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
            this.$_ngcc_current_state = 0;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 3:
         case 4:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("xpath")) {
               this.$_ngcc_current_state = 4;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 6:
            if ($__uri.equals("") && $__local.equals("xpath")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 77, (ForeignAttributesImpl)null);
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
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 3:
            if ($__uri.equals("") && $__local.equals("xpath")) {
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.makeResult(), super._cookie, $value);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendText(super._cookie, $value);
         case 2:
         case 3:
         default:
            break;
         case 4:
            this.xpath = $value;
            this.$_ngcc_current_state = 3;
            break;
         case 5:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 6:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 77, (ForeignAttributesImpl)null);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 72:
            this.ann = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 0;
            break;
         case 77:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 5;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 0;
   }

   private XPathImpl makeResult() {
      return new XPathImpl(this.$runtime.document, this.ann, this.$runtime.copyLocator(), this.fa, this.$runtime.createXmlString(this.xpath));
   }
}
