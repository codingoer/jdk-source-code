package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.Ref;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.UnionSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class SimpleType_Union extends NGCCHandler {
   private Locator locator;
   private AnnotationImpl annotation;
   private String __text;
   private UName memberTypeName;
   private String name;
   private Set finalSet;
   private ForeignAttributesImpl fa;
   private SimpleTypeImpl anonymousMemberType;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private UnionSimpleTypeImpl result;
   private final Vector members;
   private Locator uloc;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public SimpleType_Union(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
      super(source, parent, cookie);
      this.members = new Vector();
      this.$runtime = runtime;
      this.annotation = _annotation;
      this.locator = _locator;
      this.fa = _fa;
      this.name = _name;
      this.finalSet = _finalSet;
      this.$_ngcc_current_state = 12;
   }

   public SimpleType_Union(NGCCRuntimeEx runtime, AnnotationImpl _annotation, Locator _locator, ForeignAttributesImpl _fa, String _name, Set _finalSet) {
      this((NGCCHandler)null, runtime, runtime, -1, _annotation, _locator, _fa, _name, _finalSet);
   }

   private void action0() throws SAXException {
      this.result = new UnionSimpleTypeImpl(this.$runtime.document, this.annotation, this.locator, this.fa, this.name, this.name == null, this.finalSet, (Ref.SimpleType[])((Ref.SimpleType[])this.members.toArray(new Ref.SimpleType[this.members.size()])));
   }

   private void action1() throws SAXException {
      this.members.add(this.anonymousMemberType);
   }

   private void action2() throws SAXException {
      this.members.add(new DelayedRef.SimpleType(this.$runtime, this.uloc, this.$runtime.currentSchema, this.memberTypeName));
   }

   private void action3() throws SAXException {
      this.$runtime.processList(this.__text);
   }

   private void action4() throws SAXException {
      this.uloc = this.$runtime.copyLocator();
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      simpleType h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               h = new simpleType(this, super._source, this.$runtime, 179);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               h = new simpleType(this, super._source, this.$runtime, 180);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 3:
         case 5:
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 4:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 183, this.annotation, AnnotationContext.SIMPLETYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 6:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("simpleType"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 185, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 7:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "memberTypes")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 12:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("union")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action4();
               this.$_ngcc_current_state = 7;
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
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("union")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
               this.action0();
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 3:
         case 5:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("union")) {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 185, this.fa);
               this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 7:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "memberTypes")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 6;
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
         case 1:
         case 3:
         case 5:
         case 6:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            if ($__uri.equals("") && $__local.equals("memberTypes")) {
               this.$_ngcc_current_state = 10;
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
         case 3:
         case 5:
         case 6:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            this.$_ngcc_current_state = 6;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("memberTypes")) {
               this.$_ngcc_current_state = 6;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      qname h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
         case 1:
         case 3:
         case 5:
         case 6:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 7:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "memberTypes")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 6;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 8:
            h = new qname(this, super._source, this.$runtime, 188);
            this.spawnChildFromText(h, $value);
            break;
         case 9:
            h = new qname(this, super._source, this.$runtime, 187);
            this.spawnChildFromText(h, $value);
            break;
         case 10:
            this.__text = $value;
            this.$_ngcc_current_state = 9;
            this.action3();
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 179:
            this.anonymousMemberType = (SimpleTypeImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
            break;
         case 180:
            this.anonymousMemberType = (SimpleTypeImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
         case 181:
         case 182:
         case 184:
         case 186:
         default:
            break;
         case 183:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 2;
            break;
         case 185:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 4;
            break;
         case 187:
            this.memberTypeName = (UName)$__result__;
            this.action2();
            this.$_ngcc_current_state = 8;
            break;
         case 188:
            this.memberTypeName = (UName)$__result__;
            this.action2();
            this.$_ngcc_current_state = 8;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }
}
