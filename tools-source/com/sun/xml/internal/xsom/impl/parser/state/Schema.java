package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.AttGroupDeclImpl;
import com.sun.xml.internal.xsom.impl.AttributeDeclImpl;
import com.sun.xml.internal.xsom.impl.ComplexTypeImpl;
import com.sun.xml.internal.xsom.impl.ElementDecl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.ModelGroupDeclImpl;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.parser.Messages;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Schema extends NGCCHandler {
   private Integer finalDefault;
   private boolean efd;
   private boolean afd;
   private Integer blockDefault;
   private ForeignAttributesImpl fa;
   private boolean includeMode;
   private AnnotationImpl anno;
   private ComplexTypeImpl ct;
   private ElementDecl e;
   private String defaultValue;
   private XSNotation notation;
   private AttGroupDeclImpl ag;
   private String fixedValue;
   private ModelGroupDeclImpl group;
   private AttributeDeclImpl ad;
   private SimpleTypeImpl st;
   private String expectedNamespace;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private String tns;
   private Locator locator;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public Schema(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, boolean _includeMode, String _expectedNamespace) {
      super(source, parent, cookie);
      this.tns = null;
      this.$runtime = runtime;
      this.includeMode = _includeMode;
      this.expectedNamespace = _expectedNamespace;
      this.$_ngcc_current_state = 57;
   }

   public Schema(NGCCRuntimeEx runtime, boolean _includeMode, String _expectedNamespace) {
      this((NGCCHandler)null, runtime, runtime, -1, _includeMode, _expectedNamespace);
   }

   private void action0() throws SAXException {
      this.$runtime.checkDoubleDefError(this.$runtime.currentSchema.getAttGroupDecl(this.ag.getName()));
      this.$runtime.currentSchema.addAttGroupDecl(this.ag, false);
   }

   private void action1() throws SAXException {
      this.$runtime.currentSchema.addNotation(this.notation);
   }

   private void action2() throws SAXException {
      this.$runtime.checkDoubleDefError(this.$runtime.currentSchema.getModelGroupDecl(this.group.getName()));
      this.$runtime.currentSchema.addModelGroupDecl(this.group, false);
   }

   private void action3() throws SAXException {
      this.$runtime.checkDoubleDefError(this.$runtime.currentSchema.getAttributeDecl(this.ad.getName()));
      this.$runtime.currentSchema.addAttributeDecl(this.ad);
   }

   private void action4() throws SAXException {
      this.locator = this.$runtime.copyLocator();
      this.defaultValue = null;
      this.fixedValue = null;
   }

   private void action5() throws SAXException {
      this.$runtime.checkDoubleDefError(this.$runtime.currentSchema.getType(this.ct.getName()));
      this.$runtime.currentSchema.addComplexType(this.ct, false);
   }

   private void action6() throws SAXException {
      this.$runtime.checkDoubleDefError(this.$runtime.currentSchema.getType(this.st.getName()));
      this.$runtime.currentSchema.addSimpleType(this.st, false);
   }

   private void action7() throws SAXException {
      this.$runtime.checkDoubleDefError(this.$runtime.currentSchema.getElementDecl(this.e.getName()));
      this.$runtime.currentSchema.addElementDecl(this.e);
   }

   private void action8() throws SAXException {
      this.locator = this.$runtime.copyLocator();
   }

   private void action9() throws SAXException {
      this.$runtime.currentSchema.setAnnotation(this.anno);
   }

   private void action10() throws SAXException {
      this.$runtime.currentSchema.addForeignAttributes(this.fa);
   }

   private void action11() throws SAXException {
      this.$runtime.finalDefault = this.finalDefault;
   }

   private void action12() throws SAXException {
      this.$runtime.blockDefault = this.blockDefault;
   }

   private void action13() throws SAXException {
      this.$runtime.elementFormDefault = this.efd;
   }

   private void action14() throws SAXException {
      this.$runtime.attributeFormDefault = this.afd;
   }

   private void action15() throws SAXException {
      Attributes test = this.$runtime.getCurrentAttributes();
      String tns = test.getValue("targetNamespace");
      if (!this.includeMode) {
         if (tns == null) {
            tns = "";
         }

         this.$runtime.currentSchema = this.$runtime.parser.schemaSet.createSchema(tns, this.$runtime.copyLocator());
         if (this.expectedNamespace != null && !this.expectedNamespace.equals(tns)) {
            this.$runtime.reportError(Messages.format("UnexpectedTargetnamespace.Import", tns, this.expectedNamespace, tns), this.$runtime.getLocator());
         }
      } else {
         if (tns != null && this.expectedNamespace != null && !this.expectedNamespace.equals(tns)) {
            this.$runtime.reportError(Messages.format("UnexpectedTargetnamespace.Include", tns, this.expectedNamespace, tns));
         }

         this.$runtime.chameleonMode = true;
      }

      if (this.$runtime.hasAlreadyBeenRead()) {
         this.$runtime.redirectSubtree(new DefaultHandler(), "", "", "");
      } else {
         this.anno = (AnnotationImpl)this.$runtime.currentSchema.getAnnotation();
         this.$runtime.blockDefault = 0;
         this.$runtime.finalDefault = 0;
      }
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      attributeGroupDecl h;
      notation h;
      group h;
      complexType h;
      simpleType h;
      redefine h;
      importDecl h;
      includeDecl h;
      annotation h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 504, this.anno, AnnotationContext.SCHEMA);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("include")) {
               h = new includeDecl(this, super._source, this.$runtime, 505);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("import")) {
               h = new importDecl(this, super._source, this.$runtime, 506);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("redefine")) {
               h = new redefine(this, super._source, this.$runtime, 507);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action8();
               this.$_ngcc_current_state = 27;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               h = new simpleType(this, super._source, this.$runtime, 509);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               h = new complexType(this, super._source, this.$runtime, 510);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action4();
               this.$_ngcc_current_state = 16;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               h = new group(this, super._source, this.$runtime, 512);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("notation")) {
               h = new notation(this, super._source, this.$runtime, 513);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               h = new attributeGroupDecl(this, super._source, this.$runtime, 514);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 515, this.anno, AnnotationContext.SCHEMA);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("include")) {
               h = new includeDecl(this, super._source, this.$runtime, 516);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("import")) {
               h = new importDecl(this, super._source, this.$runtime, 517);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("redefine")) {
               h = new redefine(this, super._source, this.$runtime, 518);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action8();
               this.$_ngcc_current_state = 27;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               h = new simpleType(this, super._source, this.$runtime, 520);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               h = new complexType(this, super._source, this.$runtime, 521);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action4();
               this.$_ngcc_current_state = 16;
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               h = new group(this, super._source, this.$runtime, 523);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("notation")) {
               h = new notation(this, super._source, this.$runtime, 524);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               h = new attributeGroupDecl(this, super._source, this.$runtime, 525);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 38:
         case 39:
         case 40:
         case 42:
         case 43:
         case 44:
         case 46:
         case 47:
         case 48:
         case 50:
         case 51:
         case 52:
         case 54:
         case 55:
         case 56:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 11:
            if (this.$runtime.getAttributeIndex("", "name") < 0 && this.$runtime.getAttributeIndex("", "form") < 0) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new attributeDeclBody(this, super._source, this.$runtime, 421, this.locator, false, this.defaultValue, this.fixedValue);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 12:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 16:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 12;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 27:
            if (this.$runtime.getAttributeIndex("", "default") < 0 && this.$runtime.getAttributeIndex("", "fixed") < 0 && this.$runtime.getAttributeIndex("", "form") < 0 && this.$runtime.getAttributeIndex("", "final") < 0 && this.$runtime.getAttributeIndex("", "block") < 0 && this.$runtime.getAttributeIndex("", "name") < 0 && this.$runtime.getAttributeIndex("", "abstract") < 0) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 36:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("notation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("include")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("complexType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("redefine")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("import"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 527, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 37:
            if (($ai = this.$runtime.getAttributeIndex("", "finalDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 41:
            if (($ai = this.$runtime.getAttributeIndex("", "blockDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 37;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 45:
            if (($ai = this.$runtime.getAttributeIndex("", "elementFormDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 41;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 49:
            if (($ai = this.$runtime.getAttributeIndex("", "attributeFormDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 45;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 53:
            if (($ai = this.$runtime.getAttributeIndex("", "targetNamespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 49;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 57:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("schema")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action15();
               this.$_ngcc_current_state = 53;
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
            this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("schema")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 13:
         case 14:
         case 15:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 38:
         case 39:
         case 40:
         case 42:
         case 43:
         case 44:
         case 46:
         case 47:
         case 48:
         case 50:
         case 51:
         case 52:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 10:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attribute")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 11:
            if ((this.$runtime.getAttributeIndex("", "name") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute")) && (this.$runtime.getAttributeIndex("", "form") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new attributeDeclBody(this, super._source, this.$runtime, 421, this.locator, false, this.defaultValue, this.fixedValue);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 12:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 16:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 12;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 26:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("element")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 27:
            if ((this.$runtime.getAttributeIndex("", "default") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "fixed") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "form") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "final") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "block") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "name") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (this.$runtime.getAttributeIndex("", "abstract") < 0 || !$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element"))) {
               this.unexpectedLeaveElement($__qname);
            } else {
               NGCCHandler h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            }
            break;
         case 36:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("schema")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 527, (ForeignAttributesImpl)null);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 37:
            if (($ai = this.$runtime.getAttributeIndex("", "finalDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 41:
            if (($ai = this.$runtime.getAttributeIndex("", "blockDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 37;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 45:
            if (($ai = this.$runtime.getAttributeIndex("", "elementFormDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 41;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 49:
            if (($ai = this.$runtime.getAttributeIndex("", "attributeFormDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 45;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 53:
            if (($ai = this.$runtime.getAttributeIndex("", "targetNamespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 49;
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
            this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            if ((!$__uri.equals("") || !$__local.equals("name")) && (!$__uri.equals("") || !$__local.equals("form"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               NGCCHandler h = new attributeDeclBody(this, super._source, this.$runtime, 421, this.locator, false, this.defaultValue, this.fixedValue);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 14;
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 16:
            if ($__uri.equals("") && $__local.equals("default")) {
               this.$_ngcc_current_state = 18;
            } else {
               this.$_ngcc_current_state = 12;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 27:
            if ((!$__uri.equals("") || !$__local.equals("default")) && (!$__uri.equals("") || !$__local.equals("fixed")) && (!$__uri.equals("") || !$__local.equals("form")) && (!$__uri.equals("") || !$__local.equals("final")) && (!$__uri.equals("") || !$__local.equals("block")) && (!$__uri.equals("") || !$__local.equals("name")) && (!$__uri.equals("") || !$__local.equals("abstract"))) {
               this.unexpectedEnterAttribute($__qname);
            } else {
               NGCCHandler h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            }
            break;
         case 37:
            if ($__uri.equals("") && $__local.equals("finalDefault")) {
               this.$_ngcc_current_state = 39;
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 41:
            if ($__uri.equals("") && $__local.equals("blockDefault")) {
               this.$_ngcc_current_state = 43;
            } else {
               this.$_ngcc_current_state = 37;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 45:
            if ($__uri.equals("") && $__local.equals("elementFormDefault")) {
               this.$_ngcc_current_state = 47;
            } else {
               this.$_ngcc_current_state = 41;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 49:
            if ($__uri.equals("") && $__local.equals("attributeFormDefault")) {
               this.$_ngcc_current_state = 51;
            } else {
               this.$_ngcc_current_state = 45;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 53:
            if ($__uri.equals("") && $__local.equals("targetNamespace")) {
               this.$_ngcc_current_state = 55;
            } else {
               this.$_ngcc_current_state = 49;
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
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 14:
         case 15:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 39:
         case 40:
         case 43:
         case 44:
         case 47:
         case 48:
         case 51:
         case 52:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            this.$_ngcc_current_state = 11;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 13:
            if ($__uri.equals("") && $__local.equals("fixed")) {
               this.$_ngcc_current_state = 11;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 16:
            this.$_ngcc_current_state = 12;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 17:
            if ($__uri.equals("") && $__local.equals("default")) {
               this.$_ngcc_current_state = 12;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 37:
            this.$_ngcc_current_state = 36;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 38:
            if ($__uri.equals("") && $__local.equals("finalDefault")) {
               this.$_ngcc_current_state = 36;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 41:
            this.$_ngcc_current_state = 37;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 42:
            if ($__uri.equals("") && $__local.equals("blockDefault")) {
               this.$_ngcc_current_state = 37;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 45:
            this.$_ngcc_current_state = 41;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 46:
            if ($__uri.equals("") && $__local.equals("elementFormDefault")) {
               this.$_ngcc_current_state = 41;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 49:
            this.$_ngcc_current_state = 45;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 50:
            if ($__uri.equals("") && $__local.equals("attributeFormDefault")) {
               this.$_ngcc_current_state = 45;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 53:
            this.$_ngcc_current_state = 49;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 54:
            if ($__uri.equals("") && $__local.equals("targetNamespace")) {
               this.$_ngcc_current_state = 49;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      qualification h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this, super._cookie, $value);
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 13:
         case 15:
         case 17:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 38:
         case 40:
         case 42:
         case 44:
         case 46:
         case 48:
         case 50:
         case 52:
         case 54:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 11:
            attributeDeclBody h;
            if (this.$runtime.getAttributeIndex("", "form") >= 0) {
               h = new attributeDeclBody(this, super._source, this.$runtime, 421, this.locator, false, this.defaultValue, this.fixedValue);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               h = new attributeDeclBody(this, super._source, this.$runtime, 421, this.locator, false, this.defaultValue, this.fixedValue);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 12:
            if (($ai = this.$runtime.getAttributeIndex("", "fixed")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 14:
            this.fixedValue = $value;
            this.$_ngcc_current_state = 13;
            break;
         case 16:
            if (($ai = this.$runtime.getAttributeIndex("", "default")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 12;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 18:
            this.defaultValue = $value;
            this.$_ngcc_current_state = 17;
            break;
         case 27:
            elementDeclBody h;
            if (this.$runtime.getAttributeIndex("", "abstract") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "name") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "block") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "final") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "form") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "fixed") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            } else if (this.$runtime.getAttributeIndex("", "default") >= 0) {
               h = new elementDeclBody(this, super._source, this.$runtime, 439, this.locator, true);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 37:
            if (($ai = this.$runtime.getAttributeIndex("", "finalDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 36;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 39:
            NGCCHandler h = new erSet(this, super._source, this.$runtime, 529);
            this.spawnChildFromText(h, $value);
            break;
         case 41:
            if (($ai = this.$runtime.getAttributeIndex("", "blockDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 37;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 43:
            NGCCHandler h = new ersSet(this, super._source, this.$runtime, 534);
            this.spawnChildFromText(h, $value);
            break;
         case 45:
            if (($ai = this.$runtime.getAttributeIndex("", "elementFormDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 41;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 47:
            if ($value.equals("unqualified")) {
               h = new qualification(this, super._source, this.$runtime, 539);
               this.spawnChildFromText(h, $value);
            } else if ($value.equals("qualified")) {
               h = new qualification(this, super._source, this.$runtime, 539);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 49:
            if (($ai = this.$runtime.getAttributeIndex("", "attributeFormDefault")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 45;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 51:
            if ($value.equals("unqualified")) {
               h = new qualification(this, super._source, this.$runtime, 544);
               this.spawnChildFromText(h, $value);
            } else if ($value.equals("qualified")) {
               h = new qualification(this, super._source, this.$runtime, 544);
               this.spawnChildFromText(h, $value);
            }
            break;
         case 53:
            if (($ai = this.$runtime.getAttributeIndex("", "targetNamespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 49;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 55:
            this.$_ngcc_current_state = 54;
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 421:
            this.ad = (AttributeDeclImpl)$__result__;
            this.action3();
            this.$_ngcc_current_state = 10;
            break;
         case 439:
            this.e = (ElementDecl)$__result__;
            this.action7();
            this.$_ngcc_current_state = 26;
            break;
         case 504:
            this.anno = (AnnotationImpl)$__result__;
            this.action9();
            this.$_ngcc_current_state = 1;
            break;
         case 505:
            this.$_ngcc_current_state = 1;
            break;
         case 506:
            this.$_ngcc_current_state = 1;
            break;
         case 507:
            this.$_ngcc_current_state = 1;
            break;
         case 509:
            this.st = (SimpleTypeImpl)$__result__;
            this.action6();
            this.$_ngcc_current_state = 1;
            break;
         case 510:
            this.ct = (ComplexTypeImpl)$__result__;
            this.action5();
            this.$_ngcc_current_state = 1;
            break;
         case 512:
            this.group = (ModelGroupDeclImpl)$__result__;
            this.action2();
            this.$_ngcc_current_state = 1;
            break;
         case 513:
            this.notation = (XSNotation)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
            break;
         case 514:
            this.ag = (AttGroupDeclImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
            break;
         case 515:
            this.anno = (AnnotationImpl)$__result__;
            this.action9();
            this.$_ngcc_current_state = 1;
            break;
         case 516:
            this.$_ngcc_current_state = 1;
            break;
         case 517:
            this.$_ngcc_current_state = 1;
            break;
         case 518:
            this.$_ngcc_current_state = 1;
            break;
         case 520:
            this.st = (SimpleTypeImpl)$__result__;
            this.action6();
            this.$_ngcc_current_state = 1;
            break;
         case 521:
            this.ct = (ComplexTypeImpl)$__result__;
            this.action5();
            this.$_ngcc_current_state = 1;
            break;
         case 523:
            this.group = (ModelGroupDeclImpl)$__result__;
            this.action2();
            this.$_ngcc_current_state = 1;
            break;
         case 524:
            this.notation = (XSNotation)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
            break;
         case 525:
            this.ag = (AttGroupDeclImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
            break;
         case 527:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.action10();
            this.$_ngcc_current_state = 2;
            break;
         case 529:
            this.finalDefault = (Integer)$__result__;
            this.action11();
            this.$_ngcc_current_state = 38;
            break;
         case 534:
            this.blockDefault = (Integer)$__result__;
            this.action12();
            this.$_ngcc_current_state = 42;
            break;
         case 539:
            this.efd = (Boolean)$__result__;
            this.action13();
            this.$_ngcc_current_state = 46;
            break;
         case 544:
            this.afd = (Boolean)$__result__;
            this.action14();
            this.$_ngcc_current_state = 50;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
