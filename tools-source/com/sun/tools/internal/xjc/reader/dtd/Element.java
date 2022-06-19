package com.sun.tools.internal.xjc.reader.dtd;

import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.CValuePropertyInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIConversion;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIElement;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XmlString;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

final class Element extends Term implements Comparable {
   final String name;
   private final TDTDReader owner;
   private short contentModelType;
   private Term contentModel;
   boolean isReferenced;
   private CClassInfo classInfo;
   private boolean classInfoComputed;
   final List attributes = new ArrayList();
   private final List normalizedBlocks = new ArrayList();
   private boolean mustBeClass;
   private Locator locator;

   public Element(TDTDReader owner, String name) {
      this.owner = owner;
      this.name = name;
   }

   void normalize(List r, boolean optional) {
      Block o = new Block(optional, false);
      o.elements.add(this);
      r.add(o);
   }

   void addAllElements(Block b) {
      b.elements.add(this);
   }

   boolean isOptional() {
      return false;
   }

   boolean isRepeated() {
      return false;
   }

   void define(short contentModelType, Term contentModel, Locator locator) {
      assert this.contentModel == null;

      this.contentModelType = contentModelType;
      this.contentModel = contentModel;
      this.locator = locator;
      contentModel.normalize(this.normalizedBlocks, false);
      Iterator var4 = this.normalizedBlocks.iterator();

      while(true) {
         Block b;
         do {
            if (!var4.hasNext()) {
               return;
            }

            b = (Block)var4.next();
         } while(!b.isRepeated && b.elements.size() <= 1);

         Element e;
         for(Iterator var6 = b.elements.iterator(); var6.hasNext(); this.owner.getOrCreateElement(e.name).mustBeClass = true) {
            e = (Element)var6.next();
         }
      }
   }

   private TypeUse getConversion() {
      assert this.contentModel == Term.EMPTY;

      BIElement e = this.owner.bindInfo.element(this.name);
      if (e != null) {
         BIConversion conv = e.getConversion();
         if (conv != null) {
            return conv.getTransducer();
         }
      }

      return CBuiltinLeafInfo.STRING;
   }

   CClassInfo getClassInfo() {
      if (!this.classInfoComputed) {
         this.classInfoComputed = true;
         this.classInfo = this.calcClass();
      }

      return this.classInfo;
   }

   private CClassInfo calcClass() {
      BIElement e = this.owner.bindInfo.element(this.name);
      if (e == null) {
         if (this.contentModelType == 2 && this.attributes.isEmpty() && !this.mustBeClass) {
            if (this.contentModel != Term.EMPTY) {
               throw new UnsupportedOperationException("mixed content model not supported");
            } else {
               return this.isReferenced ? null : this.createDefaultClass();
            }
         } else {
            return this.createDefaultClass();
         }
      } else {
         return e.clazz;
      }
   }

   private CClassInfo createDefaultClass() {
      String className = this.owner.model.getNameConverter().toClassName(this.name);
      QName tagName = new QName("", this.name);
      return new CClassInfo(this.owner.model, this.owner.getTargetPackage(), className, this.locator, (QName)null, tagName, (XSComponent)null, (CCustomizations)null);
   }

   void bind() {
      CClassInfo ci = this.getClassInfo();

      assert ci != null || this.attributes.isEmpty();

      Iterator var2 = this.attributes.iterator();

      while(var2.hasNext()) {
         CPropertyInfo p = (CPropertyInfo)var2.next();
         ci.addProperty(p);
      }

      switch (this.contentModelType) {
         case 0:
            assert ci != null;

            return;
         case 1:
            CReferencePropertyInfo rp = new CReferencePropertyInfo("Content", true, false, true, (XSComponent)null, (CCustomizations)null, this.locator, false, false, false);
            rp.setWildcard(WildcardMode.SKIP);
            ci.addProperty(rp);
            return;
         case 2:
            if (this.contentModel != Term.EMPTY) {
               throw new UnsupportedOperationException("mixed content model unsupported yet");
            }

            if (ci != null) {
               CValuePropertyInfo p = new CValuePropertyInfo("value", (XSComponent)null, (CCustomizations)null, this.locator, this.getConversion(), (QName)null);
               ci.addProperty(p);
            }

            return;
         case 3:
         default:
            List n = new ArrayList();
            this.contentModel.normalize(n, false);
            Set names = new HashSet();
            boolean collision = false;
            Iterator var5 = n.iterator();

            Iterator var7;
            Element e;
            label127:
            while(var5.hasNext()) {
               Block b = (Block)var5.next();
               var7 = b.elements.iterator();

               while(var7.hasNext()) {
                  e = (Element)var7.next();
                  if (!names.add(e.name)) {
                     collision = true;
                     break label127;
                  }
               }
            }

            if (collision) {
               Block all = new Block(true, true);
               Iterator var18 = n.iterator();

               while(var18.hasNext()) {
                  Block b = (Block)var18.next();
                  all.elements.addAll(b.elements);
               }

               n.clear();
               n.add(all);
            }

            CElementPropertyInfo p;
            for(Iterator var14 = n.iterator(); var14.hasNext(); ci.addProperty(p)) {
               Block b = (Block)var14.next();
               if (!b.isRepeated && b.elements.size() <= 1) {
                  String name = ((Element)b.elements.iterator().next()).name;
                  String propName = this.owner.model.getNameConverter().toPropertyName(name);
                  Element ref = this.owner.getOrCreateElement(name);
                  Object refType;
                  if (ref.getClassInfo() != null) {
                     refType = ref.getClassInfo();
                  } else {
                     refType = ref.getConversion().getInfo();
                  }

                  p = new CElementPropertyInfo(propName, ((TypeUse)refType).isCollection() ? CElementPropertyInfo.CollectionMode.REPEATED_VALUE : CElementPropertyInfo.CollectionMode.NOT_REPEATED, ID.NONE, (MimeType)null, (XSComponent)null, (CCustomizations)null, this.locator, !b.isOptional);
                  p.getTypes().add(new CTypeRef(((TypeUse)refType).getInfo(), new QName("", name), (QName)null, false, (XmlString)null));
               } else {
                  StringBuilder name = new StringBuilder();

                  for(var7 = b.elements.iterator(); var7.hasNext(); name.append(this.owner.model.getNameConverter().toPropertyName(e.name))) {
                     e = (Element)var7.next();
                     if (name.length() > 0) {
                        name.append("Or");
                     }
                  }

                  p = new CElementPropertyInfo(name.toString(), CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT, ID.NONE, (MimeType)null, (XSComponent)null, (CCustomizations)null, this.locator, !b.isOptional);
                  var7 = b.elements.iterator();

                  while(var7.hasNext()) {
                     e = (Element)var7.next();
                     CClassInfo child = this.owner.getOrCreateElement(e.name).getClassInfo();

                     assert child != null;

                     p.getTypes().add(new CTypeRef(child, new QName("", e.name), (QName)null, false, (XmlString)null));
                  }
               }
            }

      }
   }

   public int compareTo(Element that) {
      return this.name.compareTo(that.name);
   }
}
