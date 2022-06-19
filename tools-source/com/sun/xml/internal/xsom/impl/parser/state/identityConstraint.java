package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.IdentityConstraintImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.XPathImpl;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class identityConstraint extends NGCCHandler {
   private String name;
   private UName ref;
   private ForeignAttributesImpl fa;
   private AnnotationImpl ann;
   private XPathImpl field;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private short category;
   private List fields;
   private XPathImpl selector;
   private DelayedRef.IdentityConstraint refer;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public identityConstraint(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.fields = new ArrayList();
      this.refer = null;
      this.$runtime = runtime;
      this.$_ngcc_current_state = 18;
   }

   public identityConstraint(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.fields.add(this.field);
   }

   private void action1() throws SAXException {
      this.refer = new DelayedRef.IdentityConstraint(this.$runtime, this.$runtime.copyLocator(), this.$runtime.currentSchema, this.ref);
   }

   private void action2() throws SAXException {
      if (this.$localName.equals("key")) {
         this.category = 0;
      } else if (this.$localName.equals("keyref")) {
         this.category = 1;
      } else if (this.$localName.equals("unique")) {
         this.category = 2;
      }

   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      xpath h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("field")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 3;
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 2:
         case 5:
         case 9:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 3:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0) {
               h = new xpath(this, super._source, this.$runtime, 270);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 4:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("field")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 3;
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 6:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0) {
               h = new xpath(this, super._source, this.$runtime, 274);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 7:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("selector")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 6;
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 8:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 277, (AnnotationImpl)null, AnnotationContext.IDENTITY_CONSTRAINT);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 7;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if (($ai = this.$runtime.getAttributeIndex("", "refer")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 8;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 16:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 17:
            if (this.$runtime.getAttributeIndex("", "name") >= 0 && ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("selector") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation"))) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 287, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 18:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action2();
               this.$_ngcc_current_state = 17;
            }
      }

   }

   public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      xpath h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("key") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("keyref") || $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("unique")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("field")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 3:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("field")) {
               h = new xpath(this, super._source, this.$runtime, 270);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 4:
         case 7:
         case 9:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 5:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("selector")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 4;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 6:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("selector")) {
               h = new xpath(this, super._source, this.$runtime, 274);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 10:
            if (($ai = this.$runtime.getAttributeIndex("", "refer")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 8;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 16:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 17:
            if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 287, (ForeignAttributesImpl)null);
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
      xpath h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 2:
         case 4:
         case 5:
         case 7:
         case 9:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 3:
            if ($__uri.equals("") && $__local.equals("xpath")) {
               h = new xpath(this, super._source, this.$runtime, 270);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 6:
            if ($__uri.equals("") && $__local.equals("xpath")) {
               h = new xpath(this, super._source, this.$runtime, 274);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 10:
            if ($__uri.equals("") && $__local.equals("refer")) {
               this.$_ngcc_current_state = 12;
            } else {
               this.$_ngcc_current_state = 8;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 16:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 15;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 17:
            if ($__uri.equals("") && $__local.equals("name")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 287, (ForeignAttributesImpl)null);
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
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 9:
         case 12:
         case 13:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 10:
            this.$_ngcc_current_state = 8;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("refer")) {
               this.$_ngcc_current_state = 8;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 14:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 10;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      xpath h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.makeResult(), super._cookie, $value);
         case 1:
         case 2:
         case 4:
         case 5:
         case 7:
         case 9:
         case 11:
         case 13:
         case 14:
         default:
            break;
         case 3:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0) {
               h = new xpath(this, super._source, this.$runtime, 270);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 6:
            if (this.$runtime.getAttributeIndex("", "xpath") >= 0) {
               h = new xpath(this, super._source, this.$runtime, 274);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 10:
            if (($ai = this.$runtime.getAttributeIndex("", "refer")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 8;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 12:
            NGCCHandler h = new qname(this, super._source, this.$runtime, 280);
            this.spawnChildFromText(h, $value);
            break;
         case 15:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 14;
            break;
         case 16:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 17:
            if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 287, (ForeignAttributesImpl)null);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 270:
            this.field = (XPathImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 2;
            break;
         case 274:
            this.selector = (XPathImpl)$__result__;
            this.$_ngcc_current_state = 5;
            break;
         case 277:
            this.ann = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 7;
            break;
         case 280:
            this.ref = (UName)$__result__;
            this.action1();
            this.$_ngcc_current_state = 11;
            break;
         case 287:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 16;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   private IdentityConstraintImpl makeResult() {
      return new IdentityConstraintImpl(this.$runtime.document, this.ann, this.$runtime.copyLocator(), this.fa, this.category, this.name, this.selector, this.fields, this.refer);
   }
}
