package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ComplexTypeImpl;
import com.sun.xml.internal.xsom.impl.ElementDecl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.IdentityConstraintImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.impl.parser.SubstGroupBaseTypeRef;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class elementDeclBody extends NGCCHandler {
   private Integer finalValue;
   private String name;
   private String nillable;
   private String abstractValue;
   private Integer blockValue;
   private ForeignAttributesImpl fa;
   private AnnotationImpl annotation;
   private Locator locator;
   private String defaultValue;
   private IdentityConstraintImpl idc;
   private boolean isGlobal;
   private String fixedValue;
   private UName typeName;
   private UName substRef;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private boolean form;
   private boolean formSpecified;
   private Ref.Type type;
   private List idcs;
   private DelayedRef.Element substHeadRef;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public elementDeclBody(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, Locator _locator, boolean _isGlobal) {
      super(source, parent, cookie);
      this.idcs = new ArrayList();
      this.$runtime = runtime;
      this.locator = _locator;
      this.isGlobal = _isGlobal;
      this.$_ngcc_current_state = 48;
   }

   public elementDeclBody(NGCCRuntimeEx runtime, Locator _locator, boolean _isGlobal) {
      this((NGCCHandler)null, runtime, runtime, -1, _locator, _isGlobal);
   }

   private void action0() throws SAXException {
      this.idcs.add(this.idc);
   }

   private void action1() throws SAXException {
      this.type = new DelayedRef.Type(this.$runtime, this.locator, this.$runtime.currentSchema, this.typeName);
   }

   private void action2() throws SAXException {
      this.substHeadRef = new DelayedRef.Element(this.$runtime, this.locator, this.$runtime.currentSchema, this.substRef);
   }

   private void action3() throws SAXException {
      this.formSpecified = true;
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      identityConstraint h;
      switch (this.$_ngcc_current_state) {
         case 0:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique"))) {
               this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new identityConstraint(this, super._source, this.$runtime, 6);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 1:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique"))) {
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new identityConstraint(this, super._source, this.$runtime, 7);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 12:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 25:
         case 26:
         case 27:
         case 29:
         case 30:
         case 31:
         case 33:
         case 34:
         case 35:
         case 37:
         case 38:
         case 39:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 3:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               NGCCHandler h = new simpleType(this, super._source, this.$runtime, 19);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               NGCCHandler h = new complexType(this, super._source, this.$runtime, 20);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 11:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 24, (AnnotationImpl)null, AnnotationContext.ELEMENT_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 3;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "substitutionGroup")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 17:
            if (($ai = this.$runtime.getAttributeIndex("", "nillable")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 13;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 23:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 24:
            if (($ai = this.$runtime.getAttributeIndex("", "form")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 23;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 28:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 24;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 32:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 36:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 32;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 40:
            if (($ai = this.$runtime.getAttributeIndex("", "block")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 44:
            if (($ai = this.$runtime.getAttributeIndex("", "abstract")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 40;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 48:
            if ((this.$runtime.getAttributeIndex("", "default") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "fixed") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "form") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "block") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "final") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "name") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (this.$runtime.getAttributeIndex("", "abstract") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("key")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("keyref")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("unique")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
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
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 12:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 25:
         case 26:
         case 27:
         case 29:
         case 30:
         case 31:
         case 33:
         case 34:
         case 35:
         case 37:
         case 38:
         case 39:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 3:
            if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 11:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "substitutionGroup")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 17:
            if (($ai = this.$runtime.getAttributeIndex("", "nillable")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 13;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 23:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 24:
            if (($ai = this.$runtime.getAttributeIndex("", "form")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 23;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 28:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 24;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 32:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 36:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 32;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 40:
            if (($ai = this.$runtime.getAttributeIndex("", "block")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 44:
            if (($ai = this.$runtime.getAttributeIndex("", "abstract")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 40;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 48:
            if (this.$runtime.getAttributeIndex("", "default") < 0 && this.$runtime.getAttributeIndex("", "fixed") < 0 && this.$runtime.getAttributeIndex("", "form") < 0 && this.$runtime.getAttributeIndex("", "block") < 0 && this.$runtime.getAttributeIndex("", "final") < 0 && this.$runtime.getAttributeIndex("", "name") < 0 && this.$runtime.getAttributeIndex("", "abstract") < 0) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
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
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 12:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 25:
         case 26:
         case 27:
         case 29:
         case 30:
         case 31:
         case 33:
         case 34:
         case 35:
         case 37:
         case 38:
         case 39:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 3:
            if ($__uri.equals("") && $__local.equals("type")) {
               this.$_ngcc_current_state = 6;
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 11:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("substitutionGroup")) {
               this.$_ngcc_current_state = 15;
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 17:
            if ($__uri.equals("") && $__local.equals("nillable")) {
               this.$_ngcc_current_state = 19;
            } else {
               this.$_ngcc_current_state = 13;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 23:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 22;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 24:
            if ($__uri.equals("") && $__local.equals("form")) {
               this.$_ngcc_current_state = 26;
            } else {
               this.$_ngcc_current_state = 23;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 28:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 30;
            } else {
               this.$_ngcc_current_state = 24;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 32:
            if ($__uri.equals("") && $__local.equals("default")) {
               this.$_ngcc_current_state = 34;
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 36:
            if ($__uri.equals("") && $__local.equals("final")) {
               this.$_ngcc_current_state = 38;
            } else {
               this.$_ngcc_current_state = 32;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 40:
            if ($__uri.equals("") && $__local.equals("block")) {
               this.$_ngcc_current_state = 42;
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 44:
            if ($__uri.equals("") && $__local.equals("abstract")) {
               this.$_ngcc_current_state = 46;
            } else {
               this.$_ngcc_current_state = 40;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 48:
            if ((!$__uri.equals("") || !$__local.equals("default")) && (!$__uri.equals("") || !$__local.equals("fixed")) && (!$__uri.equals("") || !$__local.equals("form")) && (!$__uri.equals("") || !$__local.equals("block")) && (!$__uri.equals("") || !$__local.equals("final")) && (!$__uri.equals("") || !$__local.equals("name")) && (!$__uri.equals("") || !$__local.equals("abstract"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
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
            this.revertToParentFromLeaveAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
         case 4:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 12:
         case 15:
         case 16:
         case 19:
         case 20:
         case 22:
         case 23:
         case 26:
         case 27:
         case 30:
         case 31:
         case 34:
         case 35:
         case 38:
         case 39:
         case 42:
         case 43:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 3:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
            if ($__uri.equals("") && $__local.equals("type")) {
               this.$_ngcc_current_state = 1;
               this.action1();
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 11:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            this.$_ngcc_current_state = 11;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 14:
            if ($__uri.equals("") && $__local.equals("substitutionGroup")) {
               this.$_ngcc_current_state = 11;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 17:
            this.$_ngcc_current_state = 13;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 18:
            if ($__uri.equals("") && $__local.equals("nillable")) {
               this.$_ngcc_current_state = 13;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 21:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 17;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 24:
            this.$_ngcc_current_state = 23;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 25:
            if ($__uri.equals("") && $__local.equals("form")) {
               this.$_ngcc_current_state = 23;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 28:
            this.$_ngcc_current_state = 24;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 29:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 24;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 32:
            this.$_ngcc_current_state = 28;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 33:
            if ($__uri.equals("") && $__local.equals("default")) {
               this.$_ngcc_current_state = 28;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 36:
            this.$_ngcc_current_state = 32;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 37:
            if ($__uri.equals("") && $__local.equals("final")) {
               this.$_ngcc_current_state = 32;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 40:
            this.$_ngcc_current_state = 36;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 41:
            if ($__uri.equals("") && $__local.equals("block")) {
               this.$_ngcc_current_state = 36;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 44:
            this.$_ngcc_current_state = 40;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 45:
            if ($__uri.equals("") && $__local.equals("abstract")) {
               this.$_ngcc_current_state = 40;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      qname h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.makeResult(), super._cookie, $value);
            break;
         case 1:
            this.$_ngcc_current_state = 0;
            this.$runtime.sendText(super._cookie, $value);
         case 2:
         case 4:
         case 5:
         case 7:
         case 8:
         case 9:
         case 10:
         case 12:
         case 14:
         case 16:
         case 18:
         case 20:
         case 21:
         case 25:
         case 27:
         case 29:
         case 31:
         case 33:
         case 35:
         case 37:
         case 39:
         case 41:
         case 43:
         case 45:
         case 47:
         default:
            break;
         case 3:
            if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 6:
            h = new qname(this, super._source, this.$runtime, 10);
            this.spawnChildFromText(h, $value);
            break;
         case 11:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 13:
            if (($ai = this.$runtime.getAttributeIndex("", "substitutionGroup")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 15:
            h = new qname(this, super._source, this.$runtime, 27);
            this.spawnChildFromText(h, $value);
            break;
         case 17:
            if (($ai = this.$runtime.getAttributeIndex("", "nillable")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 13;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 19:
            this.nillable = $value;
            this.$_ngcc_current_state = 18;
            break;
         case 22:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 21;
            break;
         case 23:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 24:
            if (($ai = this.$runtime.getAttributeIndex("", "form")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 23;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 26:
            qualification h;
            if ($value.equals("unqualified")) {
               h = new qualification(this, super._source, this.$runtime, 40);
               this.spawnChildFromText(h, $value);
            } else if ($value.equals("qualified")) {
               h = new qualification(this, super._source, this.$runtime, 40);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 28:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 24;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 30:
            this.fixedValue = $value;
            this.$_ngcc_current_state = 29;
            break;
         case 32:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 28;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 34:
            this.defaultValue = $value;
            this.$_ngcc_current_state = 33;
            break;
         case 36:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 32;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 38:
            NGCCHandler h = new erSet(this, super._source, this.$runtime, 55);
            this.spawnChildFromText(h, $value);
            break;
         case 40:
            if (($ai = this.$runtime.getAttributeIndex("", "block")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 42:
            NGCCHandler h = new ersSet(this, super._source, this.$runtime, 60);
            this.spawnChildFromText(h, $value);
            break;
         case 44:
            if (($ai = this.$runtime.getAttributeIndex("", "abstract")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 40;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 46:
            this.abstractValue = $value;
            this.$_ngcc_current_state = 45;
            break;
         case 48:
            foreignAttributes h;
            if (this.$runtime.getAttributeIndex("", "abstract") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "final") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "block") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "form") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "fixed") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "default") >= 0) {
               h = new foreignAttributes(this, super._source, this.$runtime, 69, this.fa);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 6:
            this.idc = (IdentityConstraintImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 0;
            break;
         case 7:
            this.idc = (IdentityConstraintImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 0;
            break;
         case 10:
            this.typeName = (UName)$__result__;
            this.$_ngcc_current_state = 5;
            break;
         case 19:
            this.type = (SimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 20:
            this.type = (ComplexTypeImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 24:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 3;
            break;
         case 27:
            this.substRef = (UName)$__result__;
            this.action2();
            this.$_ngcc_current_state = 14;
            break;
         case 40:
            this.form = (Boolean)$__result__;
            this.action3();
            this.$_ngcc_current_state = 25;
            break;
         case 55:
            this.finalValue = (Integer)$__result__;
            this.$_ngcc_current_state = 37;
            break;
         case 60:
            this.blockValue = (Integer)$__result__;
            this.$_ngcc_current_state = 41;
            break;
         case 69:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 44;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 3 || this.$_ngcc_current_state == 17 || this.$_ngcc_current_state == 13 || this.$_ngcc_current_state == 11;
   }

   private ElementDecl makeResult() {
      if (this.finalValue == null) {
         this.finalValue = new Integer(this.$runtime.finalDefault);
      }

      if (this.blockValue == null) {
         this.blockValue = new Integer(this.$runtime.blockDefault);
      }

      if (!this.formSpecified) {
         this.form = this.$runtime.elementFormDefault;
      }

      if (this.isGlobal) {
         this.form = true;
      }

      String tns;
      if (this.form) {
         tns = this.$runtime.currentSchema.getTargetNamespace();
      } else {
         tns = "";
      }

      if (this.type == null) {
         if (this.substHeadRef != null) {
            this.type = new SubstGroupBaseTypeRef(this.substHeadRef);
         } else {
            this.type = this.$runtime.parser.schemaSet.anyType;
         }
      }

      ElementDecl ed = new ElementDecl(this.$runtime, this.$runtime.document, this.annotation, this.locator, this.fa, tns, this.name, !this.isGlobal, this.$runtime.createXmlString(this.defaultValue), this.$runtime.createXmlString(this.fixedValue), this.$runtime.parseBoolean(this.nillable), this.$runtime.parseBoolean(this.abstractValue), this.formSpecified ? this.form : null, this.type, this.substHeadRef, this.blockValue, this.finalValue, this.idcs);
      if (this.type instanceof ComplexTypeImpl) {
         ((ComplexTypeImpl)this.type).setScope(ed);
      }

      return ed;
   }
}
