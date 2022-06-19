package com.sun.tools.internal.jxc.gen.config;

import com.sun.tools.internal.jxc.NGCCRuntimeEx;
import java.io.File;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class Schema extends NGCCHandler {
   private File baseDir;
   private String loc;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private File location;
   private String namespace;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public Schema(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, File _baseDir) {
      super(source, parent, cookie);
      this.$runtime = runtime;
      this.baseDir = _baseDir;
      this.$_ngcc_current_state = 10;
   }

   public Schema(NGCCRuntimeEx runtime, File _baseDir) {
      this((NGCCHandler)null, runtime, runtime, -1, _baseDir);
   }

   private void action0() throws SAXException {
      this.location = new File(this.baseDir, this.loc);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 2:
            if (($ai = this.$runtime.getAttributeIndex("", "location")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 6:
            if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 10:
            if ($__uri.equals("") && $__local.equals("schema")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
               this.$_ngcc_current_state = 6;
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
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("") && $__local.equals("schema")) {
               this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
               this.$_ngcc_current_state = 0;
            } else {
               this.unexpectedLeaveElement($__qname);
            }
            break;
         case 2:
            if (($ai = this.$runtime.getAttributeIndex("", "location")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 3:
         case 4:
         case 5:
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 6:
            if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
            } else {
               this.$_ngcc_current_state = 2;
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
            if ($__uri.equals("") && $__local.equals("location")) {
               this.$_ngcc_current_state = 4;
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            }
            break;
         case 6:
            if ($__uri.equals("") && $__local.equals("namespace")) {
               this.$_ngcc_current_state = 8;
            } else {
               this.$_ngcc_current_state = 2;
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
         case 4:
         case 5:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 3:
            if ($__uri.equals("") && $__local.equals("location")) {
               this.$_ngcc_current_state = 1;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
            break;
         case 6:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            if ($__uri.equals("") && $__local.equals("namespace")) {
               this.$_ngcc_current_state = 2;
            } else {
               this.unexpectedLeaveAttribute($__qname);
            }
      }

   }

   public void text(String $value) throws SAXException {
      int $ai;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this, super._cookie, $value);
         case 1:
         case 3:
         case 5:
         case 7:
         default:
            break;
         case 2:
            if (($ai = this.$runtime.getAttributeIndex("", "location")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 4:
            this.loc = $value;
            this.$_ngcc_current_state = 3;
            this.action0();
            break;
         case 6:
            if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendText(super._cookie, $value);
            }
            break;
         case 8:
            this.namespace = $value;
            this.$_ngcc_current_state = 7;
      }

   }

   public void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException {
   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public File getLocation() {
      return this.location;
   }
}
