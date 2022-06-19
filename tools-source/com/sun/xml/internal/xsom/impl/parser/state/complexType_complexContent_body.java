package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AttributesHolder;
import com.sun.xml.internal.xsom.impl.ContentTypeImpl;
import com.sun.xml.internal.xsom.impl.ParticleImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class complexType_complexContent_body extends NGCCHandler {
   private AttributesHolder owner;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private ContentTypeImpl particle;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public complexType_complexContent_body(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AttributesHolder _owner) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.owner = _owner;
      this.$_ngcc_current_state = 2;
   }

   public complexType_complexContent_body(NGCCRuntimeEx runtime, AttributesHolder _owner) {
      this((NGCCHandler)null, runtime, runtime, -1, _owner);
   }

   private void action0() throws SAXException {
      if (this.particle == null) {
         this.particle = this.$runtime.parser.schemaSet.empty;
      }

   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.particle, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            attributeUses h;
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attributeGroup")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("anyAttribute")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("attribute"))) {
               h = new attributeUses(this, super._source, this.$runtime, 1, this.owner);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new attributeUses(this, super._source, this.$runtime, 1, this.owner);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any"))) {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               NGCCHandler h = new particle(this, super._source, this.$runtime, 3);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
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
            this.revertToParentFromLeaveElement(this.particle, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            NGCCHandler h = new attributeUses(this, super._source, this.$runtime, 1, this.owner);
            this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromEnterAttribute(this.particle, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            NGCCHandler h = new attributeUses(this, super._source, this.$runtime, 1, this.owner);
            this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
            this.revertToParentFromLeaveAttribute(this.particle, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            NGCCHandler h = new attributeUses(this, super._source, this.$runtime, 1, this.owner);
            this.spawnChildFromLeaveAttribute(h, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         default:
            this.unexpectedLeaveAttribute($__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.particle, super._cookie, $value);
            break;
         case 1:
            NGCCHandler h = new attributeUses(this, super._source, this.$runtime, 1, this.owner);
            this.spawnChildFromText(h, $value);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 1:
            this.action0();
            this.$_ngcc_current_state = 0;
            break;
         case 3:
            this.particle = (ParticleImpl)$__result__;
            this.$_ngcc_current_state = 1;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
