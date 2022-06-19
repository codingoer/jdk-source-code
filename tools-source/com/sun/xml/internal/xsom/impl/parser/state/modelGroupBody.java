package com.sun.xml.internal.xsom.impl.parser.state;

import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.impl.AnnotationImpl;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.ModelGroupImpl;
import com.sun.xml.internal.xsom.impl.ParticleImpl;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.parser.AnnotationContext;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class modelGroupBody extends NGCCHandler {
   private AnnotationImpl annotation;
   private String compositorName;
   private Locator locator;
   private ParticleImpl childParticle;
   private ForeignAttributesImpl fa;
   protected final NGCCRuntimeEx $runtime;
   private int $_ngcc_current_state;
   protected String $uri;
   protected String $localName;
   protected String $qname;
   private ModelGroupImpl result;
   private final List particles;

   public final NGCCRuntime getRuntime() {
      return this.$runtime;
   }

   public modelGroupBody(NGCCHandler parent, NGCCEventSource source, NGCCRuntimeEx runtime, int cookie, Locator _locator, String _compositorName) {
      super(source, parent, cookie);
      this.particles = new ArrayList();
      this.$runtime = runtime;
      this.locator = _locator;
      this.compositorName = _compositorName;
      this.$_ngcc_current_state = 6;
   }

   public modelGroupBody(NGCCRuntimeEx runtime, Locator _locator, String _compositorName) {
      this((NGCCHandler)null, runtime, runtime, -1, _locator, _compositorName);
   }

   private void action0() throws SAXException {
      XSModelGroup.Compositor compositor = null;
      if (this.compositorName.equals("all")) {
         compositor = XSModelGroup.ALL;
      }

      if (this.compositorName.equals("sequence")) {
         compositor = XSModelGroup.SEQUENCE;
      }

      if (this.compositorName.equals("choice")) {
         compositor = XSModelGroup.CHOICE;
      }

      if (compositor == null) {
         throw new InternalError("unable to process " + this.compositorName);
      } else {
         this.result = new ModelGroupImpl(this.$runtime.document, this.annotation, this.locator, this.fa, compositor, (ParticleImpl[])((ParticleImpl[])this.particles.toArray(new ParticleImpl[0])));
      }
   }

   private void action1() throws SAXException {
      this.particles.add(this.childParticle);
   }

   public void enterElement(String $__uri, String $__local, String $__qname, Attributes $attrs) throws SAXException {
      this.$uri = $__uri;
      this.$localName = $__local;
      this.$qname = $__qname;
      particle h;
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromEnterElement(this.result, super._cookie, $__uri, $__local, $__qname, $attrs);
            break;
         case 1:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any"))) {
               this.action0();
               this.$_ngcc_current_state = 0;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new particle(this, super._source, this.$runtime, 170);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 2:
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any"))) {
               this.$_ngcc_current_state = 1;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new particle(this, super._source, this.$runtime, 171);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 3:
         case 5:
         default:
            this.unexpectedEnterElement($__qname);
            break;
         case 4:
            if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
               NGCCHandler h = new annotation(this, super._source, this.$runtime, 174, (AnnotationImpl)null, AnnotationContext.MODELGROUP);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               this.$_ngcc_current_state = 2;
               this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
            }
            break;
         case 6:
            foreignAttributes h;
            if ((!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("annotation")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("group")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("element")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("all")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("choice")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("sequence")) && (!$__uri.equals("http://www.w3.org/2001/XMLSchema") || !$__local.equals("any"))) {
               h = new foreignAttributes(this, super._source, this.$runtime, 176, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
            } else {
               h = new foreignAttributes(this, super._source, this.$runtime, 176, (ForeignAttributesImpl)null);
               this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
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
            this.action0();
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 176, (ForeignAttributesImpl)null);
            this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
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
            this.action0();
            this.$_ngcc_current_state = 0;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 3:
         case 5:
         default:
            this.unexpectedEnterAttribute($__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 176, (ForeignAttributesImpl)null);
            this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
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
            this.action0();
            this.$_ngcc_current_state = 0;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 3:
         case 5:
         default:
            this.unexpectedLeaveAttribute($__qname);
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
            break;
         case 6:
            NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 176, (ForeignAttributesImpl)null);
            this.spawnChildFromLeaveAttribute(h, $__uri, $__local, $__qname);
      }

   }

   public void text(String $value) throws SAXException {
      switch (this.$_ngcc_current_state) {
         case 0:
            this.revertToParentFromText(this.result, super._cookie, $value);
            break;
         case 1:
            this.action0();
            this.$_ngcc_current_state = 0;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 2:
            this.$_ngcc_current_state = 1;
            this.$runtime.sendText(super._cookie, $value);
         case 3:
         case 5:
         default:
            break;
         case 4:
            this.$_ngcc_current_state = 2;
            this.$runtime.sendText(super._cookie, $value);
            break;
         case 6:
            NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 176, (ForeignAttributesImpl)null);
            this.spawnChildFromText(h, $value);
      }

   }

   public void onChildCompleted(Object $__result__, int $__cookie__, boolean $__needAttCheck__) throws SAXException {
      switch ($__cookie__) {
         case 170:
            this.childParticle = (ParticleImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
            break;
         case 171:
            this.childParticle = (ParticleImpl)$__result__;
            this.action1();
            this.$_ngcc_current_state = 1;
         case 172:
         case 173:
         case 175:
         default:
            break;
         case 174:
            this.annotation = (AnnotationImpl)$__result__;
            this.$_ngcc_current_state = 2;
            break;
         case 176:
            this.fa = (ForeignAttributesImpl)$__result__;
            this.$_ngcc_current_state = 4;
      }

   }

   public boolean accepted() {
      return this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 2 || this.$_ngcc_current_state == 4 || this.$_ngcc_current_state == 0;
   }
}
