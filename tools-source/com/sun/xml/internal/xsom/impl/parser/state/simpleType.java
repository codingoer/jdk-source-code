package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.xsom.XSVariety;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.RestrictionSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.SimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.UnionSimpleTypeImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class simpleType extends NGCCHandler {
   private AnnotationImpl annotation;
   private String name;
   private ForeignAttributesImpl fa;
   private String finalValue;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private SimpleTypeImpl result;
   private Locator locator;
   private Set finalSet;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public simpleType(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.$_ngcc_current_state = 19;
   }

   public simpleType(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.finalSet = this.makeFinalSet(this.finalValue);
   }

   private void action1() throws SAXException {
      this.locator = this.$runtime.copyLocator();
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
         case 3:
         case 4:
         case 5:
         case 6:
         case 9:
         case 12:
         case 13:
         case 14:
         case 16:
         case 17:
         case 18:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("restriction")) {
               NGCCHandler h = new SimpleType_Restriction(this, super._source, this.$runtime, 85, this.annotation, this.locator, this.fa, this.name, this.finalSet);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("list")) {
               NGCCHandler h = new SimpleType_List(this, super._source, this.$runtime, 86, this.annotation, this.locator, this.fa, this.name, this.finalSet);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("union")) {
               NGCCHandler h = new SimpleType_Union(this, super._source, this.$runtime, 80, this.annotation, this.locator, this.fa, this.name, this.finalSet);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 7:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 8:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 89, (AnnotationImpl)null, AnnotationContext.SIMPLETYPE_DECL);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 7;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("restriction")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("union")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("list"))) {
               this.unexpectedEnterElement($__qname);
            } else {
               NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 91, this.fa);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 15:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 19:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.action1();
               this.$_ngcc_current_state = 15;
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
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("simpleType")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 9:
         case 10:
         case 12:
         case 13:
         case 14:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 7:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 15:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 11;
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
         case 7:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 13;
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 15:
            if ($__uri.equals("") && $__local.equals("final")) {
               this.$_ngcc_current_state = 17;
            } else {
               this.$_ngcc_current_state = 11;
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
         case 6:
         case 9:
         case 10:
         case 13:
         case 14:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 7:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 11:
            this.$_ngcc_current_state = 10;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("name")) {
               this.$_ngcc_current_state = 10;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 15:
            this.$_ngcc_current_state = 11;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 16:
            if ($__uri.equals("") && $__local.equals("final")) {
               this.$_ngcc_current_state = 11;
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
         case 6:
         case 9:
         case 10:
         case 12:
         case 14:
         case 16:
         default:
            break;
         case 7:
            this.action0();
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 8:
            this.$_ngcc_current_state = 7;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 11:
            if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 10;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 13:
            this.name = WhiteSpaceProcessor.collapse($value);
            this.$_ngcc_current_state = 12;
            break;
         case 15:
            if (($ai = this.$runtime.getAttributeIndex("", "final")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 11;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 17:
            this.finalValue = $value;
            this.$_ngcc_current_state = 16;
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 80:
            this.result = (UnionSimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 1;
         case 81:
         case 82:
         case 83:
         case 84:
         case 87:
         case 88:
         case 90:
         default:
            break;
         case 85:
            this.result = (RestrictionSimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 86:
            this.result = (ListSimpleTypeImpl)$__result__;
            this.$_ngcc_current_state = 1;
            break;
         case 89:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 7;
            break;
         case 91:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 8;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   private Set makeFinalSet(String finalValue) {
      if (finalValue == null) {
         return Collections.EMPTY_SET;
      } else {
         Set s = new HashSet();
         StringTokenizer tokens = new StringTokenizer(finalValue);

         while(tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals("#all")) {
               s.add(XSVariety.ATOMIC);
               s.add(XSVariety.UNION);
               s.add(XSVariety.LIST);
            }

            if (token.equals("list")) {
               s.add(XSVariety.LIST);
            }

            if (token.equals("union")) {
               s.add(XSVariety.UNION);
            }

            if (token.equals("restriction")) {
               s.add(XSVariety.ATOMIC);
            }
         }

         return s;
      }
   }
}
