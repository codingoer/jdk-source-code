package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.AttGroupDeclImpl;
import com.sun.xml.internal.xsom.impl.ComplexTypeImpl;
import com.sun.xml.internal.xsom.impl.ModelGroupDeclImpl;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.parser.Messages;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class redefine extends NGCCHandler {
   private String schemaLocation;
   private ModelGroupDeclImpl newGrp;
   private AttGroupDeclImpl newAg;
   private SimpleTypeImpl newSt;
   private ComplexTypeImpl newCt;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public redefine(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 15;
   }

   public redefine(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      XSAttGroupDecl oldAg = this.$runtime.currentSchema.getAttGroupDecl(this.newAg.getName());
      if (oldAg == null) {
         this.$runtime.reportError(Messages.format("UndefinedAttributeGroup", this.newAg.getName()));
      } else {
         this.newAg.redefine((AttGroupDeclImpl)oldAg);
         this.$runtime.currentSchema.addAttGroupDecl(this.newAg, true);
      }

   }

   private void action1() throws SAXException {
      XSModelGroupDecl oldGrp = this.$runtime.currentSchema.getModelGroupDecl(this.newGrp.getName());
      if (oldGrp == null) {
         this.$runtime.reportError(Messages.format("UndefinedModelGroup", this.newGrp.getName()));
      } else {
         this.newGrp.redefine((ModelGroupDeclImpl)oldGrp);
         this.$runtime.currentSchema.addModelGroupDecl(this.newGrp, true);
      }

   }

   private void action2() throws SAXException {
      XSComplexType oldCt = this.$runtime.currentSchema.getComplexType(this.newCt.getName());
      if (oldCt == null) {
         this.$runtime.reportError(Messages.format("UndefinedCompplexType", this.newCt.getName()));
      } else {
         this.newCt.redefine((ComplexTypeImpl)oldCt);
         this.$runtime.currentSchema.addComplexType(this.newCt, true);
      }

   }

   private void action3() throws SAXException {
      XSSimpleType oldSt = this.$runtime.currentSchema.getSimpleType(this.newSt.getName());
      if (oldSt == null) {
         this.$runtime.reportError(Messages.format("UndefinedSimpleType", this.newSt.getName()));
      } else {
         this.newSt.redefine((SimpleTypeImpl)oldSt);
         this.$runtime.currentSchema.addSimpleType(this.newSt, true);
      }

   }

   private void action4() throws SAXException {
      this.$runtime.includeSchema(this.schemaLocation);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      attributeGroupDecl h;
      group h;
      complexType h;
      simpleType h;
      annotation h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 684, (AnnotationImpl)null, AnnotationContext.SCHEMA);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               h = new simpleType(this, super._source, this.$runtime, 685);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               h = new complexType(this, super._source, this.$runtime, 686);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               h = new group(this, super._source, this.$runtime, 687);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               h = new attributeGroupDecl(this, super._source, this.$runtime, 688);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               h = new annotation(this, super._source, this.$runtime, 689, (AnnotationImpl)null, AnnotationContext.SCHEMA);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               h = new simpleType(this, super._source, this.$runtime, 690);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("complexType")) {
               h = new complexType(this, super._source, this.$runtime, 691);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("group")) {
               h = new group(this, super._source, this.$runtime, 692);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("attributeGroup")) {
               h = new attributeGroupDecl(this, super._source, this.$runtime, 693);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 14:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "schemaLocation")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 15:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("redefine")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 14;
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
            this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("redefine")) {
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
         case 14:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "schemaLocation")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 14:
            if ($__uri.equals("") && $__local.equals("schemaLocation")) {
               this.$_ngcc_current_state = 13;
            } else {
               this.unexpectedEnterAttribute($__qname);
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
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("schemaLocation")) {
               this.$_ngcc_current_state = 2;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         default:
            this.unexpectedLeaveAttribute($__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this, super._cookie, $value);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 13:
            this.schemaLocation = $value;
            this.$_ngcc_current_state = 12;
            this.action4();
            break;
         case 14:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "schemaLocation")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 684:
            this.$_ngcc_current_state = 1;
            break;
         case 685:
            this.newSt = (SimpleTypeImpl)$__result__;
            this.action3();
            this.$_ngcc_current_state = 1;
            break;
         case 686:
            this.newCt = (ComplexTypeImpl)$__result__;
            this.action2();
            this.$_ngcc_current_state = 1;
            break;
         case 687:
            this.newGrp = (ModelGroupDeclImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
            break;
         case 688:
            this.newAg = (AttGroupDeclImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
            break;
         case 689:
            this.$_ngcc_current_state = 1;
            break;
         case 690:
            this.newSt = (SimpleTypeImpl)$__result__;
            this.action3();
            this.$_ngcc_current_state = 1;
            break;
         case 691:
            this.newCt = (ComplexTypeImpl)$__result__;
            this.action2();
            this.$_ngcc_current_state = 1;
            break;
         case 692:
            this.newGrp = (ModelGroupDeclImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
            break;
         case 693:
            this.newAg = (AttGroupDeclImpl)$__result__;
            this.action0();
            this.$_ngcc_current_state = 1;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
