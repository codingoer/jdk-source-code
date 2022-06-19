package com.sun.tools.internal.xjc.reader.internalizer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.internal.xjc.util.DOMUtils;
import com.sun.tools.internal.xjc.util.ForkContentHandler;
import com.sun.xml.internal.xsom.SCD;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSSchemaSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class SCDBasedBindingSet {
   private Target topLevel;
   private final DOMForest forest;
   private ErrorReceiver errorReceiver;
   private UnmarshallerHandler unmarshaller;
   private ForkContentHandler loader;

   SCDBasedBindingSet(DOMForest forest) {
      this.forest = forest;
   }

   Target createNewTarget(Target parent, Element src, SCD scd) {
      return new Target(parent, src, scd);
   }

   public void apply(XSSchemaSet schema, ErrorReceiver errorReceiver) {
      if (this.topLevel != null) {
         this.errorReceiver = errorReceiver;
         Unmarshaller u = BindInfo.getCustomizationUnmarshaller();
         this.unmarshaller = u.getUnmarshallerHandler();
         ValidatorHandler v = BindInfo.bindingFileSchema.newValidator();
         v.setErrorHandler(errorReceiver);
         this.loader = new ForkContentHandler(v, this.unmarshaller);
         this.topLevel.applyAll(schema.getSchemas());
         this.loader = null;
         this.unmarshaller = null;
         this.errorReceiver = null;
      }

   }

   private void reportError(Element errorSource, String formattedMsg) {
      this.reportError(errorSource, formattedMsg, (Exception)null);
   }

   private void reportError(Element errorSource, String formattedMsg, Exception nestedException) {
      SAXParseException e = new SAXParseException2(formattedMsg, this.forest.locatorTable.getStartLocation(errorSource), nestedException);
      this.errorReceiver.error((SAXParseException)e);
   }

   final class Target {
      private Target firstChild;
      private final Target nextSibling;
      @NotNull
      private final SCD scd;
      @NotNull
      private final Element src;
      private final List bindings;

      private Target(Target parent, Element src, SCD scd) {
         this.bindings = new ArrayList();
         if (parent == null) {
            this.nextSibling = SCDBasedBindingSet.this.topLevel;
            SCDBasedBindingSet.this.topLevel = this;
         } else {
            this.nextSibling = parent.firstChild;
            parent.firstChild = this;
         }

         this.src = src;
         this.scd = scd;
      }

      void addBinidng(Element binding) {
         this.bindings.add(binding);
      }

      private void applyAll(Collection contextNode) {
         for(Target self = this; self != null; self = self.nextSibling) {
            self.apply(contextNode);
         }

      }

      private void apply(Collection contextNode) {
         Collection childNodes = this.scd.select(contextNode);
         if (childNodes.isEmpty()) {
            if (this.src.getAttributeNode("if-exists") == null) {
               SCDBasedBindingSet.this.reportError(this.src, Messages.format("ERR_SCD_EVALUATED_EMPTY", this.scd));
            }
         } else {
            if (this.firstChild != null) {
               this.firstChild.applyAll(childNodes);
            }

            if (!this.bindings.isEmpty()) {
               Iterator itr = childNodes.iterator();
               XSComponent target = (XSComponent)itr.next();
               if (itr.hasNext()) {
                  SCDBasedBindingSet.this.reportError(this.src, Messages.format("ERR_SCD_MATCHED_MULTIPLE_NODES", this.scd, childNodes.size()));
                  SCDBasedBindingSet.this.errorReceiver.error(target.getLocator(), Messages.format("ERR_SCD_MATCHED_MULTIPLE_NODES_FIRST"));
                  SCDBasedBindingSet.this.errorReceiver.error(((XSComponent)itr.next()).getLocator(), Messages.format("ERR_SCD_MATCHED_MULTIPLE_NODES_SECOND"));
               }

               Iterator var5 = this.bindings.iterator();

               while(var5.hasNext()) {
                  Element binding = (Element)var5.next();
                  Element[] var7 = DOMUtils.getChildElements(binding);
                  int var8 = var7.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     Element item = var7[var9];
                     String localName = item.getLocalName();
                     if (!"bindings".equals(localName)) {
                        try {
                           (new DOMForestScanner(SCDBasedBindingSet.this.forest)).scan((Element)item, SCDBasedBindingSet.this.loader);
                           BIDeclaration decl = (BIDeclaration)SCDBasedBindingSet.this.unmarshaller.getResult();
                           XSAnnotation ann = target.getAnnotation(true);
                           BindInfo bi = (BindInfo)ann.getAnnotation();
                           if (bi == null) {
                              bi = new BindInfo();
                              ann.setAnnotation(bi);
                           }

                           bi.addDecl(decl);
                        } catch (SAXException var15) {
                        } catch (JAXBException var16) {
                           throw new AssertionError(var16);
                        }
                     }
                  }
               }
            }

         }
      }

      // $FF: synthetic method
      Target(Target x1, Element x2, SCD x3, Object x4) {
         this(x1, x2, x3);
      }
   }
}
