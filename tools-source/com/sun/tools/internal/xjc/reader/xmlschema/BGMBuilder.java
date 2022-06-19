package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.fmt.JTextFile;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRendererFactory;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.reader.ModelChecker;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIDom;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BISerializable;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.internal.xjc.util.CodeModelClassFactory;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.util.XSFinder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

public class BGMBuilder extends BindingComponent {
   public final boolean inExtensionMode;
   public final String defaultPackage1;
   public final String defaultPackage2;
   private final BindGreen green = (BindGreen)Ring.get(BindGreen.class);
   private final BindPurple purple = (BindPurple)Ring.get(BindPurple.class);
   public final Model model = (Model)Ring.get(Model.class);
   public final FieldRendererFactory fieldRendererFactory;
   private RefererFinder refFinder;
   private List activePlugins;
   private BIGlobalBinding globalBinding;
   private ParticleBinder particleBinder;
   private final BindInfo emptyBindInfo = new BindInfo();
   private final Map externalBindInfos = new HashMap();
   private final XSFinder toPurple = new XSFinder() {
      public Boolean attributeUse(XSAttributeUse use) {
         return true;
      }

      public Boolean simpleType(XSSimpleType xsSimpleType) {
         return true;
      }

      public Boolean wildcard(XSWildcard xsWildcard) {
         return true;
      }
   };
   private Transformer identityTransformer;

   public static Model build(XSSchemaSet _schemas, JCodeModel codeModel, ErrorReceiver _errorReceiver, Options opts) {
      Ring old = Ring.begin();

      Model var8;
      try {
         ErrorReceiverFilter ef = new ErrorReceiverFilter(_errorReceiver);
         Ring.add(XSSchemaSet.class, _schemas);
         Ring.add(codeModel);
         Model model = new Model(opts, codeModel, (NameConverter)null, opts.classNameAllocator, _schemas);
         Ring.add(model);
         Ring.add(ErrorReceiver.class, ef);
         Ring.add(CodeModelClassFactory.class, new CodeModelClassFactory(ef));
         BGMBuilder builder = new BGMBuilder(opts.defaultPackage, opts.defaultPackage2, opts.isExtensionMode(), opts.getFieldRendererFactory(), opts.activePlugins);
         builder._build();
         if (!ef.hadError()) {
            var8 = model;
            return var8;
         }

         var8 = null;
      } finally {
         Ring.end(old);
      }

      return var8;
   }

   protected BGMBuilder(String defaultPackage1, String defaultPackage2, boolean _inExtensionMode, FieldRendererFactory fieldRendererFactory, List activePlugins) {
      this.inExtensionMode = _inExtensionMode;
      this.defaultPackage1 = defaultPackage1;
      this.defaultPackage2 = defaultPackage2;
      this.fieldRendererFactory = fieldRendererFactory;
      this.activePlugins = activePlugins;
      this.promoteGlobalBindings();
   }

   private void _build() {
      this.buildContents();
      this.getClassSelector().executeTasks();
      ((UnusedCustomizationChecker)Ring.get(UnusedCustomizationChecker.class)).run();
      ((ModelChecker)Ring.get(ModelChecker.class)).check();
      Iterator var1 = this.activePlugins.iterator();

      while(var1.hasNext()) {
         Plugin ma = (Plugin)var1.next();
         ma.postProcessModel(this.model, (ErrorHandler)Ring.get(ErrorReceiver.class));
      }

   }

   private void promoteGlobalBindings() {
      XSSchemaSet schemas = (XSSchemaSet)Ring.get(XSSchemaSet.class);
      Iterator var2 = schemas.getSchemas().iterator();

      while(var2.hasNext()) {
         XSSchema s = (XSSchema)var2.next();
         BindInfo bi = this.getBindInfo(s);
         this.model.getCustomizations().addAll(bi.toCustomizationList());
         BIGlobalBinding gb = (BIGlobalBinding)bi.get(BIGlobalBinding.class);
         if (gb != null) {
            gb.markAsAcknowledged();
            if (this.globalBinding == null) {
               this.globalBinding = gb;
            } else if (!this.globalBinding.isEqual(gb)) {
               this.getErrorReporter().error(gb.getLocation(), "ERR_MULTIPLE_GLOBAL_BINDINGS");
               this.getErrorReporter().error(this.globalBinding.getLocation(), "ERR_MULTIPLE_GLOBAL_BINDINGS_OTHER");
            }
         }
      }

      if (this.globalBinding == null) {
         this.globalBinding = new BIGlobalBinding();
         BindInfo big = new BindInfo();
         big.addDecl(this.globalBinding);
         big.setOwner(this, (XSComponent)null);
      }

      this.model.strategy = this.globalBinding.getCodeGenerationStrategy();
      this.model.rootClass = this.globalBinding.getSuperClass();
      this.model.rootInterface = this.globalBinding.getSuperInterface();
      this.particleBinder = (ParticleBinder)(this.globalBinding.isSimpleMode() ? new ExpressionParticleBinder() : new DefaultParticleBinder());
      BISerializable serial = this.globalBinding.getSerializable();
      if (serial != null) {
         this.model.serializable = true;
         this.model.serialVersionUID = serial.uid;
      }

      if (this.globalBinding.nameConverter != null) {
         this.model.setNameConverter(this.globalBinding.nameConverter);
      }

      this.globalBinding.dispatchGlobalConversions(schemas);
      this.globalBinding.errorCheck();
   }

   @NotNull
   public BIGlobalBinding getGlobalBinding() {
      return this.globalBinding;
   }

   @NotNull
   public ParticleBinder getParticleBinder() {
      return this.particleBinder;
   }

   public NameConverter getNameConverter() {
      return this.model.getNameConverter();
   }

   private void buildContents() {
      ClassSelector cs = this.getClassSelector();
      SimpleTypeBuilder stb = (SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class);
      Iterator var3 = ((XSSchemaSet)Ring.get(XSSchemaSet.class)).getSchemas().iterator();

      while(true) {
         while(var3.hasNext()) {
            XSSchema s = (XSSchema)var3.next();
            BISchemaBinding sb = (BISchemaBinding)this.getBindInfo(s).get(BISchemaBinding.class);
            if (sb != null && !sb.map) {
               sb.markAsAcknowledged();
            } else {
               this.getClassSelector().pushClassScope(new CClassInfoParent.Package(this.getClassSelector().getPackage(s.getTargetNamespace())));
               this.checkMultipleSchemaBindings(s);
               this.processPackageJavadoc(s);
               this.populate(s.getAttGroupDecls(), s);
               this.populate(s.getAttributeDecls(), s);
               this.populate(s.getElementDecls(), s);
               this.populate(s.getModelGroupDecls(), s);
               Iterator var6 = s.getTypes().values().iterator();

               while(var6.hasNext()) {
                  XSType t = (XSType)var6.next();
                  stb.refererStack.push(t);
                  this.model.typeUses().put(getName(t), cs.bindToType((XSType)t, s));
                  stb.refererStack.pop();
               }

               this.getClassSelector().popClassScope();
            }
         }

         return;
      }
   }

   private void checkMultipleSchemaBindings(XSSchema schema) {
      ArrayList locations = new ArrayList();
      BindInfo bi = this.getBindInfo(schema);
      Iterator var4 = bi.iterator();

      while(var4.hasNext()) {
         BIDeclaration bid = (BIDeclaration)var4.next();
         if (bid.getName() == BISchemaBinding.NAME) {
            locations.add(bid.getLocation());
         }
      }

      if (locations.size() > 1) {
         this.getErrorReporter().error((Locator)locations.get(0), "BGMBuilder.MultipleSchemaBindings", schema.getTargetNamespace());

         for(int i = 1; i < locations.size(); ++i) {
            this.getErrorReporter().error((Locator)locations.get(i), "BGMBuilder.MultipleSchemaBindings.Location");
         }

      }
   }

   private void populate(Map col, XSSchema schema) {
      ClassSelector cs = this.getClassSelector();
      Iterator var4 = col.values().iterator();

      while(var4.hasNext()) {
         XSComponent sc = (XSComponent)var4.next();
         cs.bindToType((XSComponent)sc, schema);
      }

   }

   private void processPackageJavadoc(XSSchema s) {
      BISchemaBinding cust = (BISchemaBinding)this.getBindInfo(s).get(BISchemaBinding.class);
      if (cust != null) {
         cust.markAsAcknowledged();
         if (cust.getJavadoc() != null) {
            JTextFile html = new JTextFile("package.html");
            html.setContents(cust.getJavadoc());
            this.getClassSelector().getPackage(s.getTargetNamespace()).addResourceFile(html);
         }
      }
   }

   public BindInfo getOrCreateBindInfo(XSComponent schemaComponent) {
      BindInfo bi = this._getBindInfoReadOnly(schemaComponent);
      if (bi != null) {
         return bi;
      } else {
         bi = new BindInfo();
         bi.setOwner(this, schemaComponent);
         this.externalBindInfos.put(schemaComponent, bi);
         return bi;
      }
   }

   public BindInfo getBindInfo(XSComponent schemaComponent) {
      BindInfo bi = this._getBindInfoReadOnly(schemaComponent);
      return bi != null ? bi : this.emptyBindInfo;
   }

   private BindInfo _getBindInfoReadOnly(XSComponent schemaComponent) {
      BindInfo bi = (BindInfo)this.externalBindInfos.get(schemaComponent);
      if (bi != null) {
         return bi;
      } else {
         XSAnnotation annon = schemaComponent.getAnnotation();
         if (annon != null) {
            bi = (BindInfo)annon.getAnnotation();
            if (bi != null) {
               if (bi.getOwner() == null) {
                  bi.setOwner(this, schemaComponent);
               }

               return bi;
            }
         }

         return null;
      }
   }

   protected final BIDom getLocalDomCustomization(XSParticle p) {
      if (p == null) {
         return null;
      } else {
         BIDom dom = (BIDom)this.getBindInfo(p).get(BIDom.class);
         if (dom != null) {
            return dom;
         } else {
            dom = (BIDom)this.getBindInfo(p.getTerm()).get(BIDom.class);
            if (dom != null) {
               return dom;
            } else {
               XSTerm t = p.getTerm();
               if (t.isElementDecl()) {
                  return (BIDom)this.getBindInfo(t.asElementDecl().getType()).get(BIDom.class);
               } else {
                  return t.isModelGroupDecl() ? (BIDom)this.getBindInfo(t.asModelGroupDecl().getModelGroup()).get(BIDom.class) : null;
               }
            }
         }
      }
   }

   public void ying(XSComponent sc, @Nullable XSComponent referer) {
      if (!(Boolean)sc.apply(this.toPurple) && this.getClassSelector().bindToType(sc, referer) == null) {
         sc.visit(this.green);
      } else {
         sc.visit(this.purple);
      }

   }

   public Transformer getIdentityTransformer() {
      try {
         if (this.identityTransformer == null) {
            TransformerFactory tf = XmlFactory.createTransformerFactory(this.model.options.disableXmlSecurity);
            this.identityTransformer = tf.newTransformer();
         }

         return this.identityTransformer;
      } catch (TransformerConfigurationException var2) {
         throw new Error(var2);
      }
   }

   public Set getReferer(XSType c) {
      if (this.refFinder == null) {
         this.refFinder = new RefererFinder();
         this.refFinder.schemaSet((XSSchemaSet)Ring.get(XSSchemaSet.class));
      }

      return this.refFinder.getReferer(c);
   }

   public static QName getName(XSDeclaration decl) {
      String local = decl.getName();
      return local == null ? null : new QName(decl.getTargetNamespace(), local);
   }

   public String deriveName(String name, XSComponent comp) {
      XSSchema owner = comp.getOwnerSchema();
      name = this.getNameConverter().toClassName(name);
      if (owner != null) {
         BISchemaBinding sb = (BISchemaBinding)this.getBindInfo(owner).get(BISchemaBinding.class);
         if (sb != null) {
            name = sb.mangleClassName(name, comp);
         }
      }

      return name;
   }

   public boolean isGenerateMixedExtensions() {
      return this.globalBinding != null ? this.globalBinding.isGenerateMixedExtensions() : false;
   }
}
