package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.WildcardImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class wildcardBody extends NGCCHandler {
   private AnnotationImpl annotation;
   private Locator locator;
   private String modeValue;
   private String ns;
   private ForeignAttributesImpl fa;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public wildcardBody(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, Locator _locator) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.locator = _locator;
      this.$_ngcc_current_state = 10;
   }

   public wildcardBody(NGCCRuntimeEx runtime, Locator _locator) {
      this((NGCCHandler)null, runtime, runtime, -1, _locator);
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
            if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "processContents")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 9:
            foreignAttributes h;
            if (this.$runtime.getAttributeIndex("", "namespace") < 0 && this.$runtime.getAttributeIndex("", "processContents") < 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 411, (AnnotationImpl)null, AnnotationContext.WILDCARD);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 9;
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
            this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "processContents")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 9:
            foreignAttributes h;
            if (this.$runtime.getAttributeIndex("", "namespace") < 0 && this.$runtime.getAttributeIndex("", "processContents") < 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            if ($__uri.equals("") && $__local.equals("namespace")) {
               this.$_ngcc_current_state = 3;
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("processContents")) {
               this.$_ngcc_current_state = 7;
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 9:
            foreignAttributes h;
            if ((!$__uri.equals("") || !$__local.equals("namespace")) && (!$__uri.equals("") || !$__local.equals("processContents"))) {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
            if ($__uri.equals("") && $__local.equals("namespace")) {
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 3:
         case 4:
         case 7:
         case 8:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 5:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            if ($__uri.equals("") && $__local.equals("processContents")) {
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 9:
            NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
            this.spawnChildFromLeaveAttribute(h, $__uri, $__local, $__qname);
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.makeResult(), super._cookie, $value);
            break;
         case 1:
            if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendText(super._cookie, $value);
            }
         case 2:
         case 4:
         case 6:
         case 8:
         default:
            break;
         case 3:
            this.ns = $value;
            this.$_ngcc_current_state = 2;
            break;
         case 5:
            if (($ai = this.$runtime.getAttributeIndex("", "processContents")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 7:
            this.modeValue = $value;
            this.$_ngcc_current_state = 6;
            break;
         case 9:
            foreignAttributes h;
            if (this.$runtime.getAttributeIndex("", "processContents") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "namespace") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromText(h, $value);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 409, (ForeignAttributesImpl)null);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 10:
            this.$_ngcc_current_state = 9;
            this.$runtime.sendText(super._cookie, $value);
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 409:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 5;
            break;
         case 411:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 9;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 5 || this.$_ngcc_current_state == 1;
   }

   private WildcardImpl makeResult() {
      if (this.modeValue == null) {
         this.modeValue = "strict";
      }

      int mode = -1;
      if (this.modeValue.equals("strict")) {
         mode = 2;
      }

      if (this.modeValue.equals("lax")) {
         mode = 1;
      }

      if (this.modeValue.equals("skip")) {
         mode = 3;
      }

      if (mode == -1) {
         throw new InternalError();
      } else if (this.ns != null && !this.ns.equals("##any")) {
         if (this.ns.equals("##other")) {
            return new WildcardImpl.Other(this.$runtime.document, this.annotation, this.locator, this.fa, this.$runtime.currentSchema.getTargetNamespace(), mode);
         } else {
            StringTokenizer tokens = new StringTokenizer(this.ns);

            HashSet s;
            String ns;
            for(s = new HashSet(); tokens.hasMoreTokens(); s.add(ns)) {
               ns = tokens.nextToken();
               if (ns.equals("##local")) {
                  ns = "";
               }

               if (ns.equals("##targetNamespace")) {
                  ns = this.$runtime.currentSchema.getTargetNamespace();
               }
            }

            return new WildcardImpl.Finite(this.$runtime.document, this.annotation, this.locator, this.fa, s, mode);
         }
      } else {
         return new WildcardImpl.Any(this.$runtime.document, this.annotation, this.locator, this.fa, mode);
      }
   }
}
