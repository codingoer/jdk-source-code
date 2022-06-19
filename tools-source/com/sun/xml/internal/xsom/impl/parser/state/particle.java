package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ElementDecl;
import com.sun.xml.internal.xsom.impl.ModelGroupImpl;
import com.sun.xml.internal.xsom.impl.ParticleImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.WildcardImpl;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class particle extends NGCCHandler {
   private AnnotationImpl annotation;
   private ElementDecl anonymousElementDecl;
   private WildcardImpl wcBody;
   private ModelGroupImpl term;
   private UName elementTypeName;
   private occurs occurs;
   private UName groupName;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private Locator wloc;
   private Locator loc;
   private ParticleImpl result;
   private String compositorName;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public particle(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 1;
   }

   public particle(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.result = new ParticleImpl(this.$runtime.document, (AnnotationImpl)null, this.wcBody, this.wloc, this.occurs.max, this.occurs.min);
   }

   private void action1() throws SAXException {
      this.wloc = this.$runtime.copyLocator();
   }

   private void action2() throws SAXException {
      this.result = new ParticleImpl(this.$runtime.document, (AnnotationImpl)null, this.anonymousElementDecl, this.loc, this.occurs.max, this.occurs.min);
   }

   private void action3() throws SAXException {
      this.result = new ParticleImpl(this.$runtime.document, this.annotation, new DelayedRef.Element(this.$runtime, this.loc, this.$runtime.currentSchema, this.elementTypeName), this.loc, this.occurs.max, this.occurs.min);
   }

   private void action4() throws SAXException {
      this.loc = this.$runtime.copyLocator();
   }

   private void action5() throws SAXException {
      this.result = new ParticleImpl(this.$runtime.document, this.annotation, new DelayedRef.ModelGroup(this.$runtime, this.loc, this.$runtime.currentSchema, this.groupName), this.loc, this.occurs.max, this.occurs.min);
   }

   private void action6() throws SAXException {
      this.loc = this.$runtime.copyLocator();
   }

   private void action7() throws SAXException {
      this.result = new ParticleImpl(this.$runtime.document, (AnnotationImpl)null, this.term, this.loc, this.occurs.max, this.occurs.min);
   }

   private void action8() throws SAXException {
      this.compositorName = this.$localName;
      this.loc = this.$runtime.copyLocator();
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      occurs h;
      annotation h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
                  this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                  this.action6();
                  this.$_ngcc_current_state = 26;
               } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element")) {
                  this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                  this.action4();
                  this.$_ngcc_current_state = 16;
               } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("any")) {
                  this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                  this.action1();
                  this.$_ngcc_current_state = 4;
               } else {
                  this.unexpectedEnterElement($__qname);
               }
            } else {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action8();
               this.$_ngcc_current_state = 30;
            }
            break;
         case 2:
         case 5:
         case 6:
         case 7:
         case 9:
         case 12:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 19:
         case 22:
         case 23:
         case 24:
         case 27:
         case 28:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 3:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && this.$runtime.getAttributeIndex("", "namespace") < 0 && this.$runtime.getAttributeIndex("", "processContents") < 0) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new wildcardBody(this, super._source, this.$runtime, 106, this.wloc);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 4:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "namespace") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "processContents") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 8:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else if (this.$runtime.getAttributeIndex("", "default") < 0 && this.$runtime.getAttributeIndex("", "fixed") < 0 && this.$runtime.getAttributeIndex("", "form") < 0 && this.$runtime.getAttributeIndex("", "final") < 0 && this.$runtime.getAttributeIndex("", "block") < 0 && this.$runtime.getAttributeIndex("", "name") < 0 && this.$runtime.getAttributeIndex("", "abstract") < 0) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            this.action3();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 11:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 115, (AnnotationImpl)null, AnnotationContext.PARTICLE);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 16:
            if ((this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "default") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "fixed") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "form") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "final") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "block") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "ref") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "name") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "abstract") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 20:
            this.action5();
            this.$_ngcc_current_state = 19;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 21:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 127, (AnnotationImpl)null, AnnotationContext.PARTICLE);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 20;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 25:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 26:
            if ((this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "ref") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 132);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 29:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new modelGroupBody(this, super._source, this.$runtime, 136, this.loc, this.compositorName);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 30:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 137);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
      }

   }

   public void leaveElement(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      occurs h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 5:
         case 6:
         case 9:
         case 12:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 22:
         case 23:
         case 24:
         case 27:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("any")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 3:
            if ((this.$runtime.getAttributeIndex("", "namespace") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (this.$runtime.getAttributeIndex("", "processContents") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new wildcardBody(this, super._source, this.$runtime, 106, this.wloc);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 4:
            if ((this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (this.$runtime.getAttributeIndex("", "namespace") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (this.$runtime.getAttributeIndex("", "processContents") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 7:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 8:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else if ((this.$runtime.getAttributeIndex("", "default") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "fixed") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "form") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "final") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "block") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "name") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "abstract") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 10:
            this.action3();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            this.$_ngcc_current_state = 10;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 16:
            if ((this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "default") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "fixed") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "form") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "final") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "block") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "ref") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "name") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "abstract") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 19:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 20:
            this.action5();
            this.$_ngcc_current_state = 19;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 21:
            this.$_ngcc_current_state = 20;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 25:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 26:
            if ((this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (this.$runtime.getAttributeIndex("", "ref") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 132);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 28:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            }
            break;
         case 29:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new modelGroupBody(this, super._source, this.$runtime, 136, this.loc, this.compositorName);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 30:
            if ((this.$runtime.getAttributeIndex("", "maxOccurs") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) && (this.$runtime.getAttributeIndex("", "minOccurs") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 137);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
      }

   }

   public void enterAttribute(String $__uri, String $__local, String $__qname) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      occurs h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 2:
         case 5:
         case 6:
         case 7:
         case 9:
         case 12:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 19:
         case 22:
         case 23:
         case 24:
         case 27:
         case 28:
         case 29:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 3:
            if ((!$__uri.equals("") || !$__local.equals("namespace")) && (!$__uri.equals("") || !$__local.equals("processContents"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               NGCCHandler h = new wildcardBody(this, super._source, this.$runtime, 106, this.wloc);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 4:
            if ((!$__uri.equals("") || !$__local.equals("maxOccurs")) && (!$__uri.equals("") || !$__local.equals("minOccurs")) && (!$__uri.equals("") || !$__local.equals("namespace")) && (!$__uri.equals("") || !$__local.equals("processContents"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 14;
            } else if ((!$__uri.equals("") || !$__local.equals("default")) && (!$__uri.equals("") || !$__local.equals("fixed")) && (!$__uri.equals("") || !$__local.equals("form")) && (!$__uri.equals("") || !$__local.equals("final")) && (!$__uri.equals("") || !$__local.equals("block")) && (!$__uri.equals("") || !$__local.equals("name")) && (!$__uri.equals("") || !$__local.equals("abstract"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               NGCCHandler h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 10:
            this.action3();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            this.$_ngcc_current_state = 10;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 16:
            if ((!$__uri.equals("") || !$__local.equals("maxOccurs")) && (!$__uri.equals("") || !$__local.equals("default")) && (!$__uri.equals("") || !$__local.equals("fixed")) && (!$__uri.equals("") || !$__local.equals("form")) && (!$__uri.equals("") || !$__local.equals("final")) && (!$__uri.equals("") || !$__local.equals("block")) && (!$__uri.equals("") || !$__local.equals("ref")) && (!$__uri.equals("") || !$__local.equals("minOccurs")) && (!$__uri.equals("") || !$__local.equals("name")) && (!$__uri.equals("") || !$__local.equals("abstract"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 20:
            this.action5();
            this.$_ngcc_current_state = 19;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 21:
            this.$_ngcc_current_state = 20;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 25:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 24;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 26:
            if ((!$__uri.equals("") || !$__local.equals("maxOccurs")) && (!$__uri.equals("") || !$__local.equals("ref")) && (!$__uri.equals("") || !$__local.equals("minOccurs"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 132);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 30:
            if ((!$__uri.equals("") || !$__local.equals("maxOccurs")) && (!$__uri.equals("") || !$__local.equals("minOccurs"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               h = new occurs(this, super._source, this.$runtime, 137);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
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
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 12:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 22:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 10:
            this.action3();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            this.$_ngcc_current_state = 10;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 11;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 20:
            this.action5();
            this.$_ngcc_current_state = 19;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 21:
            this.$_ngcc_current_state = 20;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 23:
            if ($__uri.equals("") && $__local.equals("ref")) {
               this.$_ngcc_current_state = 21;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      occurs h;
      qname h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
         case 1:
         case 2:
         case 5:
         case 6:
         case 7:
         case 9:
         case 12:
         case 13:
         case 15:
         case 17:
         case 18:
         case 19:
         case 22:
         case 23:
         case 27:
         case 28:
         case 29:
         default:
            break;
         case 3:
            wildcardBody h;
            if (this.$runtime.getAttributeIndex("", "processContents") >= 0) {
               h = new wildcardBody(this, super._source, this.$runtime, 106, this.wloc);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "namespace") >= 0) {
               h = new wildcardBody(this, super._source, this.$runtime, 106, this.wloc);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 4:
            if (this.$runtime.getAttributeIndex("", "processContents") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "namespace") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "minOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "maxOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 107);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 8:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               elementDeclBody h;
               if (this.$runtime.getAttributeIndex("", "abstract") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               } else if (this.$runtime.getAttributeIndex("", "name") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               } else if (this.$runtime.getAttributeIndex("", "block") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               } else if (this.$runtime.getAttributeIndex("", "final") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               } else if (this.$runtime.getAttributeIndex("", "form") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               } else if (this.$runtime.getAttributeIndex("", "fixed") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               } else if (this.$runtime.getAttributeIndex("", "default") >= 0) {
                  h = new elementDeclBody(this, super._source, this.$runtime, 112, this.loc, false);
                  this.spawnChildFromText(h, $value);
               }
            }
            break;
         case 10:
            this.action3();
            this.$_ngcc_current_state = 7;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 11:
            this.$_ngcc_current_state = 10;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 14:
            h = new qname(this, super._source, this.$runtime, 118);
            this.spawnChildFromText(h, $value);
            break;
         case 16:
            if (this.$runtime.getAttributeIndex("", "abstract") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "minOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "ref") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "block") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "final") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "form") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "fixed") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "default") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "maxOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 121);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 20:
            this.action5();
            this.$_ngcc_current_state = 19;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 21:
            this.$_ngcc_current_state = 20;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 24:
            h = new qname(this, super._source, this.$runtime, 130);
            this.spawnChildFromText(h, $value);
            break;
         case 25:
            if (($ai = this.$runtime.getAttributeIndex("", "ref")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 26:
            if (this.$runtime.getAttributeIndex("", "minOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 132);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "ref") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 132);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "maxOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 132);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 30:
            if (this.$runtime.getAttributeIndex("", "minOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 137);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "maxOccurs") >= 0) {
               h = new occurs(this, super._source, this.$runtime, 137);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 106:
            this.wcBody = (WildcardImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 2;
            break;
         case 107:
            this.occurs = (occurs)$__result__;
            this.$_ngcc_current_state = 3;
         case 108:
         case 109:
         case 110:
         case 111:
         case 113:
         case 114:
         case 116:
         case 117:
         case 119:
         case 120:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 128:
         case 129:
         case 131:
         case 133:
         case 134:
         case 135:
         default:
            break;
         case 112:
            this.anonymousElementDecl = (ElementDecl)$__result__;
            this.action2();
            this.$_ngcc_current_state = 7;
            break;
         case 115:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 10;
            break;
         case 118:
            this.elementTypeName = (UName)$__result__;
            this.$_ngcc_current_state = 13;
            break;
         case 121:
            this.occurs = (occurs)$__result__;
            this.$_ngcc_current_state = 8;
            break;
         case 127:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 20;
            break;
         case 130:
            this.groupName = (UName)$__result__;
            this.$_ngcc_current_state = 23;
            break;
         case 132:
            this.occurs = (occurs)$__result__;
            this.$_ngcc_current_state = 25;
            break;
         case 136:
            this.term = (ModelGroupImpl)$__result__;
            this.action7();
            this.$_ngcc_current_state = 28;
            break;
         case 137:
            this.occurs = (occurs)$__result__;
            this.$_ngcc_current_state = 29;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
