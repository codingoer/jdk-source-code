package com.sun.tools.internal.jxc.gen.config;

import com.sun.tools.internal.jxc.NGCCRuntimeEx;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class Config extends NGCCHandler {
   private String bd;
   private Schema _schema;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private File baseDir;
   private Classes classes;
   private List schema;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public Config(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie) {
      super(source, parent, cookie);
      this.schema = new ArrayList();
      this.$runtime = runtime;
      this.$_ngcc_current_state = 8;
   }

   public Config(NGCCRuntimeEx runtime) {
      this((NGCCHandler)null, runtime, runtime, -1);
   }

   private void action0() throws SAXException {
      this.schema.add(this._schema);
   }

   private void action1() throws SAXException {
      this.baseDir = this.$runtime.getBaseDir(this.bd);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      Schema h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ($__uri.equals("") && $__local.equals("schema")) {
               h = new Schema(this, super._source, this.$runtime, 19, this.baseDir);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 2:
            if ($__uri.equals("") && $__local.equals("schema")) {
               h = new Schema(this, super._source, this.$runtime, 20, this.baseDir);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 3:
         case 5:
         case 6:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 4:
            if ($__uri.equals("") && $__local.equals("classes")) {
               NGCCHandler h = new Classes(this, super._source, this.$runtime, 22);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 7:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "baseDir")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               this.unexpectedEnterElement($__qname);
            }
            break;
         case 8:
            if ($__uri.equals("") && $__local.equals("config")) {
               this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
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
            this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
            break;
         case 1:
            if ($__uri.equals("") && $__local.equals("config")) {
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
         default:
            this.unexpectedLeaveElement($__qname);
            break;
         case 7:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "baseDir")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 7:
            if ($__uri.equals("") && $__local.equals("baseDir")) {
               this.$_ngcc_current_state = 6;
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
         case 5:
            if ($__uri.equals("") && $__local.equals("baseDir")) {
               this.$_ngcc_current_state = 4;
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
         case 1:
         case 3:
         case 4:
         case 5:
         default:
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 6:
            this.bd = $value;
            this.$_ngcc_current_state = 5;
            this.action1();
            break;
         case 7:
            int $ai;
            if (($ai = this.$runtime.getAttributeIndex("", "baseDir")) >= 0) {
               this.$runtime.consumeAttribute($ai);
               this.$runtime.sendText(super._cookie, $value);
            }
      }

   }

   public void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException {
      switch (cookie) {
         case 19:
            this._schema = (Schema)result;
            this.action0();
            this.$_ngcc_current_state = 1;
            break;
         case 20:
            this._schema = (Schema)result;
            this.action0();
            this.$_ngcc_current_state = 1;
         case 21:
         default:
            break;
         case 22:
            this.classes = (Classes)result;
            this.$_ngcc_current_state = 2;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 0;
   }

   public Classes getClasses() {
      return this.classes;
   }

   public File getBaseDir() {
      return this.baseDir;
   }

   public List getSchema() {
      return this.schema;
   }
}
