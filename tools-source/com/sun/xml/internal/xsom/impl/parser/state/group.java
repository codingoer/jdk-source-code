package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.ModelGroupDeclImpl;
import com.sun.xml.internal.xsom.impl.ModelGroupImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class group extends NGCCHandler {
   private AnnotationImpl annotation;
   private String name;
   private ModelGroupImpl term;
   private ForeignAttributesImpl fa;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private ModelGroupDeclImpl result;
   private Locator loc;
   private Locator mloc;
   private String compositorName;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public group(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 15;
   }

   public group(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.result = new ModelGroupDeclImpl(this.$runtime.document, this.annotation, this.loc, this.fa, this.$runtime.currentSchema.getTargetNamespace(), this.name, this.term);
   }

   private void action1() throws SAXException {
      this.mloc = this.$runtime.copyLocator();
      this.compositorName = this.$localName;
   }

   private void action2() throws SAXException {
      this.loc = this.$runtime.copyLocator();
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
         case 2:
         case 7:
         case 8:
         case 9:
         case 12:
         case 13:
         case 14:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 3:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("any") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("all") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("choice") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("sequence")) {
               NGCCHandler h = new modelGroupBody(this, super._source, this.$runtime, 355, this.mloc, this.compositorName);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 4:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 3;
            }
            break;
         case 5:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 357, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 6:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 359, (AnnotationImpl)null, AnnotationContext.MODELGROUP_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 5;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "ID")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 15:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action2();
               this.$_ngcc_current_state = 11;
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
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
               this.action0();
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            }
            break;
         case 3:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new modelGroupBody(this, super._source, this.$runtime, 355, this.mloc, this.compositorName);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 4:
         case 5:
         case 7:
         case 8:
         case 9:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 6:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 10:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "ID")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 10;
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
            this.revertToParentFromEnterAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 10:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 9;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("ID")) {
               this.$_ngcc_current_state = 13;
            } else {
               this.$_ngcc_current_state = 10;
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
            this.revertToParentFromLeaveAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 7:
         case 9:
         case 10:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 6:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 6;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 11:
            this.$_ngcc_current_state = 10;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("ID")) {
               this.$_ngcc_current_state = 10;
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
         case 3:
         case 4:
         case 5:
         case 7:
         case 8:
         case 12:
         default:
            break;
         case 6:
            this.$_ngcc_current_state = 5;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 9:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 8;
            break;
         case 10:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "ID")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 13:
            this.$_ngcc_current_state = 12;
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 355:
            this.term = (ModelGroupImpl)$__result__;
            this.$_ngcc_current_state = 2;
         case 356:
         case 358:
         default:
            break;
         case 357:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 4;
            break;
         case 359:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 5;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
