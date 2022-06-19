package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.internal.xjc.SchemaCache;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CPluginCustomization;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.internal.bind.annotation.XmlLocation;
import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;
import com.sun.xml.internal.xsom.XSComponent;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

@XmlRootElement(
   namespace = "http://www.w3.org/2001/XMLSchema",
   name = "annotation"
)
@XmlType(
   namespace = "http://www.w3.org/2001/XMLSchema",
   name = "foobar"
)
public final class BindInfo implements Iterable {
   private BGMBuilder builder;
   @XmlLocation
   private Locator location;
   @XmlElement(
      namespace = "http://www.w3.org/2001/XMLSchema"
   )
   private Documentation documentation;
   private final List decls = new ArrayList();
   private XSComponent owner;
   public static final BindInfo empty = new BindInfo();
   private static volatile JAXBContext customizationContext;
   public static final SchemaCache bindingFileSchema = new SchemaCache(BindInfo.class.getResource("binding.xsd"));

   public boolean isPointless() {
      if (this.size() > 0) {
         return false;
      } else {
         return this.documentation == null || this.documentation.contents.isEmpty();
      }
   }

   @XmlElement(
      namespace = "http://www.w3.org/2001/XMLSchema"
   )
   void setAppinfo(AppInfo aib) {
      aib.addTo(this);
   }

   public Locator getSourceLocation() {
      return this.location;
   }

   public void setOwner(BGMBuilder _builder, XSComponent _owner) {
      this.owner = _owner;
      this.builder = _builder;
      Iterator var3 = this.decls.iterator();

      while(var3.hasNext()) {
         BIDeclaration d = (BIDeclaration)var3.next();
         d.onSetOwner();
      }

   }

   public XSComponent getOwner() {
      return this.owner;
   }

   public BGMBuilder getBuilder() {
      return this.builder;
   }

   public void addDecl(BIDeclaration decl) {
      if (decl == null) {
         throw new IllegalArgumentException();
      } else {
         decl.setParent(this);
         this.decls.add(decl);
      }
   }

   public BIDeclaration get(Class kind) {
      Iterator var2 = this.decls.iterator();

      BIDeclaration decl;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         decl = (BIDeclaration)var2.next();
      } while(!kind.isInstance(decl));

      return (BIDeclaration)kind.cast(decl);
   }

   public BIDeclaration[] getDecls() {
      return (BIDeclaration[])this.decls.toArray(new BIDeclaration[this.decls.size()]);
   }

   public String getDocumentation() {
      if (this.documentation != null && this.documentation.contents != null) {
         StringBuilder buf = new StringBuilder();
         Iterator var2 = this.documentation.contents.iterator();

         while(var2.hasNext()) {
            Object c = var2.next();
            if (c instanceof String) {
               buf.append(c.toString());
            }

            if (c instanceof Element) {
               Transformer t = this.builder.getIdentityTransformer();
               StringWriter w = new StringWriter();

               try {
                  Writer fw = new FilterWriter(w) {
                     char[] buf = new char[1];

                     public void write(int c) throws IOException {
                        this.buf[0] = (char)c;
                        this.write((char[])this.buf, 0, 1);
                     }

                     public void write(char[] cbuf, int off, int len) throws IOException {
                        MinimumEscapeHandler.theInstance.escape(cbuf, off, len, false, this.out);
                     }

                     public void write(String str, int off, int len) throws IOException {
                        this.write(str.toCharArray(), off, len);
                     }
                  };
                  t.transform(new DOMSource((Element)c), new StreamResult(fw));
               } catch (TransformerException var7) {
                  throw new Error(var7);
               }

               buf.append("\n<pre>\n");
               buf.append(w);
               buf.append("\n</pre>\n");
            }
         }

         return buf.toString();
      } else {
         return null;
      }
   }

   public void absorb(BindInfo bi) {
      Iterator var2 = bi.iterator();

      while(var2.hasNext()) {
         BIDeclaration d = (BIDeclaration)var2.next();
         d.setParent(this);
      }

      this.decls.addAll(bi.decls);
      if (this.documentation == null) {
         this.documentation = bi.documentation;
      } else {
         this.documentation.addAll(bi.documentation);
      }

   }

   public int size() {
      return this.decls.size();
   }

   public BIDeclaration get(int idx) {
      return (BIDeclaration)this.decls.get(idx);
   }

   public Iterator iterator() {
      return this.decls.iterator();
   }

   public CCustomizations toCustomizationList() {
      CCustomizations r = null;
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         BIDeclaration d = (BIDeclaration)var2.next();
         if (d instanceof BIXPluginCustomization) {
            BIXPluginCustomization pc = (BIXPluginCustomization)d;
            pc.markAsAcknowledged();
            if (((Model)Ring.get(Model.class)).options.pluginURIs.contains(pc.getName().getNamespaceURI())) {
               if (r == null) {
                  r = new CCustomizations();
               }

               r.add(new CPluginCustomization(pc.element, pc.getLocation()));
            }
         }
      }

      if (r == null) {
         r = CCustomizations.EMPTY;
      }

      return new CCustomizations(r);
   }

   public static JAXBContext getCustomizationContext() {
      try {
         if (customizationContext == null) {
            Class var0 = BindInfo.class;
            synchronized(BindInfo.class) {
               if (customizationContext == null) {
                  customizationContext = JAXBContext.newInstance(new Class[]{BindInfo.class, BIClass.class, BIConversion.User.class, BIConversion.UserAdapter.class, BIDom.class, BIFactoryMethod.class, BIInlineBinaryData.class, BIXDom.class, BIXSubstitutable.class, BIEnum.class, BIEnumMember.class, BIGlobalBinding.class, BIProperty.class, BISchemaBinding.class});
               }
            }
         }

         return customizationContext;
      } catch (JAXBException var3) {
         throw new AssertionError(var3);
      }
   }

   public static Unmarshaller getCustomizationUnmarshaller() {
      try {
         return getCustomizationContext().createUnmarshaller();
      } catch (JAXBException var1) {
         throw new AssertionError(var1);
      }
   }

   private static final class AppInfo {
      @XmlAnyElement(
         lax = true,
         value = DomHandlerEx.class
      )
      List contents = new ArrayList();

      public void addTo(BindInfo bi) {
         if (this.contents != null) {
            Iterator var2 = this.contents.iterator();

            while(var2.hasNext()) {
               Object o = var2.next();
               if (o instanceof BIDeclaration) {
                  bi.addDecl((BIDeclaration)o);
               }

               if (o instanceof DomHandlerEx.DomAndLocation) {
                  DomHandlerEx.DomAndLocation e = (DomHandlerEx.DomAndLocation)o;
                  String nsUri = e.element.getNamespaceURI();
                  if (nsUri != null && !nsUri.equals("") && !nsUri.equals("http://www.w3.org/2001/XMLSchema")) {
                     bi.addDecl(new BIXPluginCustomization(e.element, e.loc));
                  }
               }
            }

         }
      }
   }

   private static final class Documentation {
      @XmlAnyElement
      @XmlMixed
      List contents = new ArrayList();

      void addAll(Documentation rhs) {
         if (rhs != null) {
            if (this.contents == null) {
               this.contents = new ArrayList();
            }

            if (!this.contents.isEmpty()) {
               this.contents.add("\n\n");
            }

            this.contents.addAll(rhs.contents);
         }
      }
   }
}
