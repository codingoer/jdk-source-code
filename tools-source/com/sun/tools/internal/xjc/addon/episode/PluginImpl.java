package com.sun.tools.internal.xjc.addon.episode;

import com.sun.tools.internal.xjc.BadCommandLineException;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.EnumOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.schemagen.episode.Bindings;
import com.sun.xml.internal.bind.v2.schemagen.episode.SchemaBindings;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.output.StreamSerializer;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PluginImpl extends Plugin {
   private File episodeFile;
   private static final XSFunction SCD = new XSFunction() {
      private String name(XSDeclaration decl) {
         return decl.getTargetNamespace().equals("") ? decl.getName() : "tns:" + decl.getName();
      }

      public String complexType(XSComplexType type) {
         return "~" + this.name(type);
      }

      public String simpleType(XSSimpleType simpleType) {
         return "~" + this.name(simpleType);
      }

      public String elementDecl(XSElementDecl decl) {
         return this.name(decl);
      }

      public String annotation(XSAnnotation ann) {
         throw new UnsupportedOperationException();
      }

      public String attGroupDecl(XSAttGroupDecl decl) {
         throw new UnsupportedOperationException();
      }

      public String attributeDecl(XSAttributeDecl decl) {
         throw new UnsupportedOperationException();
      }

      public String attributeUse(XSAttributeUse use) {
         throw new UnsupportedOperationException();
      }

      public String schema(XSSchema schema) {
         throw new UnsupportedOperationException();
      }

      public String facet(XSFacet facet) {
         throw new UnsupportedOperationException();
      }

      public String notation(XSNotation notation) {
         throw new UnsupportedOperationException();
      }

      public String identityConstraint(XSIdentityConstraint decl) {
         throw new UnsupportedOperationException();
      }

      public String xpath(XSXPath xpath) {
         throw new UnsupportedOperationException();
      }

      public String particle(XSParticle particle) {
         throw new UnsupportedOperationException();
      }

      public String empty(XSContentType empty) {
         throw new UnsupportedOperationException();
      }

      public String wildcard(XSWildcard wc) {
         throw new UnsupportedOperationException();
      }

      public String modelGroupDecl(XSModelGroupDecl decl) {
         throw new UnsupportedOperationException();
      }

      public String modelGroup(XSModelGroup group) {
         throw new UnsupportedOperationException();
      }
   };

   public String getOptionName() {
      return "episode";
   }

   public String getUsage() {
      return "  -episode <FILE>    :  generate the episode file for separate compilation";
   }

   public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
      if (args[i].equals("-episode")) {
         ++i;
         this.episodeFile = new File(opt.requireArgument("-episode", args, i));
         return 2;
      } else {
         return 0;
      }
   }

   public boolean run(Outline model, Options opt, ErrorHandler errorHandler) throws SAXException {
      try {
         Map perSchema = new HashMap();
         boolean hasComponentInNoNamespace = false;
         List outlines = new ArrayList();
         Iterator var7 = model.getClasses().iterator();

         XSComponent sc;
         String fullName;
         String packageName;
         OutlineAdaptor adaptor;
         while(var7.hasNext()) {
            ClassOutline co = (ClassOutline)var7.next();
            sc = co.target.getSchemaComponent();
            fullName = co.implClass.fullName();
            packageName = co.implClass.getPackage().name();
            adaptor = new OutlineAdaptor(sc, PluginImpl.OutlineAdaptor.OutlineType.CLASS, fullName, packageName);
            outlines.add(adaptor);
         }

         var7 = model.getEnums().iterator();

         while(var7.hasNext()) {
            EnumOutline eo = (EnumOutline)var7.next();
            sc = eo.target.getSchemaComponent();
            fullName = eo.clazz.fullName();
            packageName = eo.clazz.getPackage().name();
            adaptor = new OutlineAdaptor(sc, PluginImpl.OutlineAdaptor.OutlineType.ENUM, fullName, packageName);
            outlines.add(adaptor);
         }

         var7 = outlines.iterator();

         PerSchemaOutlineAdaptors ps;
         while(var7.hasNext()) {
            OutlineAdaptor oa = (OutlineAdaptor)var7.next();
            sc = oa.schemaComponent;
            if (sc != null && sc instanceof XSDeclaration) {
               XSDeclaration decl = (XSDeclaration)sc;
               if (!decl.isLocal()) {
                  ps = (PerSchemaOutlineAdaptors)perSchema.get(decl.getOwnerSchema());
                  if (ps == null) {
                     ps = new PerSchemaOutlineAdaptors();
                     perSchema.put(decl.getOwnerSchema(), ps);
                  }

                  ps.add(oa);
                  if (decl.getTargetNamespace().equals("")) {
                     hasComponentInNoNamespace = true;
                  }
               }
            }
         }

         OutputStream os = new FileOutputStream(this.episodeFile);
         Bindings bindings = (Bindings)TXW.create(Bindings.class, new StreamSerializer(os, "UTF-8"));
         if (hasComponentInNoNamespace) {
            bindings._namespace("http://java.sun.com/xml/ns/jaxb", "jaxb");
         } else {
            bindings._namespace("http://java.sun.com/xml/ns/jaxb", "");
         }

         bindings.version("2.1");
         bindings._comment("\n\n" + opt.getPrologComment() + "\n  ");
         Iterator var23 = perSchema.entrySet().iterator();

         while(var23.hasNext()) {
            Map.Entry e = (Map.Entry)var23.next();
            ps = (PerSchemaOutlineAdaptors)e.getValue();
            Bindings group = bindings.bindings();
            String tns = ((XSSchema)e.getKey()).getTargetNamespace();
            if (!tns.equals("")) {
               group._namespace(tns, "tns");
            }

            group.scd("x-schema::" + (tns.equals("") ? "" : "tns"));
            SchemaBindings schemaBindings = group.schemaBindings();
            schemaBindings.map(false);
            if (ps.packageNames.size() == 1) {
               String packageName = (String)ps.packageNames.iterator().next();
               if (packageName != null && packageName.length() > 0) {
                  schemaBindings._package().name(packageName);
               }
            }

            Iterator var28 = ps.outlineAdaptors.iterator();

            while(var28.hasNext()) {
               OutlineAdaptor oa = (OutlineAdaptor)var28.next();
               Bindings child = group.bindings();
               oa.buildBindings(child);
            }

            group.commit(true);
         }

         bindings.commit();
         return true;
      } catch (IOException var18) {
         errorHandler.error(new SAXParseException("Failed to write to " + this.episodeFile, (Locator)null, var18));
         return false;
      }
   }

   private static final class PerSchemaOutlineAdaptors {
      private final List outlineAdaptors;
      private final Set packageNames;

      private PerSchemaOutlineAdaptors() {
         this.outlineAdaptors = new ArrayList();
         this.packageNames = new HashSet();
      }

      private void add(OutlineAdaptor outlineAdaptor) {
         this.outlineAdaptors.add(outlineAdaptor);
         this.packageNames.add(outlineAdaptor.packageName);
      }

      // $FF: synthetic method
      PerSchemaOutlineAdaptors(Object x0) {
         this();
      }
   }

   private static final class OutlineAdaptor {
      private final XSComponent schemaComponent;
      private final OutlineType outlineType;
      private final String implName;
      private final String packageName;

      public OutlineAdaptor(XSComponent schemaComponent, OutlineType outlineType, String implName, String packageName) {
         this.schemaComponent = schemaComponent;
         this.outlineType = outlineType;
         this.implName = implName;
         this.packageName = packageName;
      }

      private void buildBindings(Bindings bindings) {
         bindings.scd((String)this.schemaComponent.apply(PluginImpl.SCD));
         this.outlineType.bindingsBuilder.build(this, bindings);
      }

      private static enum OutlineType {
         CLASS(new BindingsBuilder() {
            public void build(OutlineAdaptor adaptor, Bindings bindings) {
               bindings.klass().ref(adaptor.implName);
            }
         }),
         ENUM(new BindingsBuilder() {
            public void build(OutlineAdaptor adaptor, Bindings bindings) {
               bindings.typesafeEnumClass().ref(adaptor.implName);
            }
         });

         private final BindingsBuilder bindingsBuilder;

         private OutlineType(BindingsBuilder bindingsBuilder) {
            this.bindingsBuilder = bindingsBuilder;
         }

         private interface BindingsBuilder {
            void build(OutlineAdaptor var1, Bindings var2);
         }
      }
   }
}
