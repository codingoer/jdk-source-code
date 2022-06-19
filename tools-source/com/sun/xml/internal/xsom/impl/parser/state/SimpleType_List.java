package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class SimpleType_List extends NGCCHandler {
   private Locator locator;
   private AnnotationImpl annotation;
   private String name;
   private UName itemTypeName;
   private Set finalSet;
   private ForeignAttributesImpl fa;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private ListSimpleTypeImpl result;
   private Ref.SimpleType itemType;
   private Locator lloc;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public SimpleType_List(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.annotation = _annotation;
      this.locator = _locator;
      this.fa = _fa;
      this.name = _name;
      this.finalSet = _finalSet;
      this.$_ngcc_current_state = 10;
   }

   public SimpleType_List(NGCCRuntimeEx runtime, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
      this((NGCCHandler)null, runtime, runtime, -1, _annotation, _locator, _fa, _name, _finalSet);
   }

   private void action0() throws SAXException {
      this.result = new ListSimpleTypeImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.name, this.name == null, this.finalSet, this.itemType);
   }

   private void action1() throws SAXException {
      this.itemType = new DelayedRef.SimpleType(this.$runtime, this.lloc, this.$runtime.currentSchema, this.itemTypeName);
   }

   private void action2() throws SAXException {
      this.lloc = this.$runtime.copyLocator();
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "itemType")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               NGCCHandler h = new simpleType(this, super._source, this.$runtime, 258);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 7:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 264, this.annotation, AnnotationContext.SIMPLETYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 9:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (this.$runtime.getAttributeIndex("", "itemType") < 0 || (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation"))) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 266, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action2();
               this.$_ngcc_current_state = 9;
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
            this.revertToParentFromLeaveElement(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
               this.action0();
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "itemType")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 7:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            if (this.$runtime.getAttributeIndex("", "itemType") >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 266, this.fa);
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
            this.revertToParentFromEnterAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 2:
            if ($__uri.equals("") && $__local.equals("itemType")) {
               this.$_ngcc_current_state = 5;
            } else {
               this.unexpectedEnterAttribute($__qname);
            }
            break;
         case 7:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 9:
            if ($__uri.equals("") && $__local.equals("itemType")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 266, this.fa);
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
            this.revertToParentFromLeaveAttribute(this.result, super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            if ($__uri.equals("") && $__local.equals("itemType")) {
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 7:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         default:
            this.unexpectedLeaveAttribute($__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
         case 1:
         case 3:
         case 4:
         case 6:
         case 8:
         default:
            break;
         case 2:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "itemType")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 5:
            NGCCHandler h = new qname(this, super._source, this.$runtime, 260);
            this.spawnChildFromText(h, $value);
            break;
         case 7:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 9:
            if (this.$runtime.getAttributeIndex("", "itemType") >= 0) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 266, this.fa);
               this.spawnChildFromText(h, $value);
            }
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 258:
            this.itemType = (SimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 1;
         case 259:
         case 261:
         case 262:
         case 263:
         case 265:
         default:
            break;
         case 260:
            this.itemTypeName = (UName)$__result__;
            this.action1();
            this.$_ngcc_current_state = 4;
            break;
         case 264:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 2;
            break;
         case 266:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 7;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
