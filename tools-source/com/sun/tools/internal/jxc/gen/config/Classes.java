package com.sun.tools.internal.jxc.gen.config;

import com.sun.tools.internal.jxc.NGCCRuntimeEx;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class Classes extends NGCCHandler {
   private String __text;
   private String exclude_content;
   private String include_content;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private List includes;
   private List excludes;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public Classes(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.includes = new ArrayList();
      this.excludes = new ArrayList();
      this.$runtime = runtime;
      this.$_ngcc_current_state = 12;
   }

   public Classes(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.excludes.add(this.exclude_content);
   }

   private void action1() throws SAXException {
      this.$runtime.processList(this.__text);
   }

   private void action2() throws SAXException {
      this.includes.add(this.include_content);
   }

   private void action3() throws SAXException {
      this.$runtime.processList(this.__text);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 2:
            if ($__uri.equals("") && $__local.equals("excludes")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 6;
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 11:
            if ($__uri.equals("") && $__local.equals("includes")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 10;
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 12:
            if ($__uri.equals("") && $__local.equals("classes")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 11;
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
            this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("") && $__local.equals("classes")) {
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
            if ($__uri.equals("") && $__local.equals("excludes")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            break;
         case 5:
         case 6:
         case 7:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("includes")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 2;
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
            this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
         case 3:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 3;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this, super._cookie, $value);
         case 1:
         case 5:
         case 7:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 3:
            this.exclude_content = $value;
            this.$_ngcc_current_state = 3;
            this.action0();
            break;
         case 4:
            this.exclude_content = $value;
            this.$_ngcc_current_state = 3;
            this.action0();
            break;
         case 6:
            this.__text = $value;
            this.$_ngcc_current_state = 4;
            this.action1();
            break;
         case 8:
            this.include_content = $value;
            this.$_ngcc_current_state = 8;
            this.action2();
            break;
         case 9:
            this.include_content = $value;
            this.$_ngcc_current_state = 8;
            this.action2();
            break;
         case 10:
            this.__text = $value;
            this.$_ngcc_current_state = 9;
            this.action3();
      }

   }

   public void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException {
   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   public List getIncludes() {
      return this.$runtime.getIncludePatterns(this.includes);
   }

   public List getExcludes() {
      return this.$runtime.getExcludePatterns(this.excludes);
   }
}
