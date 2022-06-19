package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import com.sun.xml.internal.xsom.parser.AnnotationParser;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class annotation extends NGCCHandler {
   private AnnotationContext context;
   private AnnotationImpl existing;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private AnnotationParser parser;
   private Locator locator;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public annotation(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AnnotationImpl _existing, AnnotationContext _context) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.existing = _existing;
      this.context = _context;
      this.$_ngcc_current_state = 2;
   }

   public annotation(NGCCRuntimeEx runtime, AnnotationImpl _existing, AnnotationContext _context) {
      this((NGCCHandler)null, runtime, runtime, -1, _existing, _context);
   }

   private void action0() throws SAXException {
      this.locator = this.$runtime.copyLocator();
      this.parser = this.$runtime.createAnnotationParser();
      this.$runtime.redirectSubtree(this.parser.getContentHandler(this.context, this.$runtime.getAnnotationContextElementName(), this.$runtime.getErrorHandler(), this.$runtime.parser.getEntityResolver()), this.$uri, this.$localName, this.$qname);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action0();
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedEnterElement($__qname);
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
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
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
         default:
      }
   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   public AnnotationImpl makeResult() {
      Object e = null;
      if (this.existing != null) {
         e = this.existing.getAnnotation();
      }

      return new AnnotationImpl(this.parser.getResult(e), this.locator);
   }
}
