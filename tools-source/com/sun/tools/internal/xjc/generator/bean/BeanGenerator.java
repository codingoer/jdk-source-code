package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JClassContainer;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JEnumConstant;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JForEach;
import com.sun.codemodel.internal.JInvocation;
import com.sun.codemodel.internal.JJavaName;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.codemodel.internal.fmt.JStaticJavaFile;
import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.xjc.AbortException;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.api.SpecVersion;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlAnyAttributeWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlEnumValueWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlEnumWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlJavaTypeAdapterWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlMimeTypeWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlRootElementWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlSeeAlsoWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlTypeWriter;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.internal.xjc.model.CAdapter;
import com.sun.tools.internal.xjc.model.CAttributePropertyInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CClassRef;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CEnumConstant;
import com.sun.tools.internal.xjc.model.CEnumLeafInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.EnumConstantOutline;
import com.sun.tools.internal.xjc.outline.EnumOutline;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.util.CodeModelClassFactory;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapterMarker;
import com.sun.xml.internal.xsom.XmlString;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.namespace.QName;

public final class BeanGenerator implements Outline {
   private final CodeModelClassFactory codeModelClassFactory;
   private final ErrorReceiver errorReceiver;
   private final Map packageContexts = new LinkedHashMap();
   private final Map classes = new LinkedHashMap();
   private final Map enums = new LinkedHashMap();
   private final Map generatedRuntime = new LinkedHashMap();
   private final Model model;
   private final JCodeModel codeModel;
   private final Map fields = new LinkedHashMap();
   final Map elements = new LinkedHashMap();
   private final CClassInfoParent.Visitor exposedContainerBuilder = new CClassInfoParent.Visitor() {
      public JClassContainer onBean(CClassInfo bean) {
         return BeanGenerator.this.getClazz(bean).ref;
      }

      public JClassContainer onElement(CElementInfo element) {
         return BeanGenerator.this.getElement(element).implClass;
      }

      public JClassContainer onPackage(JPackage pkg) {
         return BeanGenerator.this.model.strategy.getPackage(pkg, Aspect.EXPOSED);
      }
   };
   private final CClassInfoParent.Visitor implContainerBuilder = new CClassInfoParent.Visitor() {
      public JClassContainer onBean(CClassInfo bean) {
         return BeanGenerator.this.getClazz(bean).implClass;
      }

      public JClassContainer onElement(CElementInfo element) {
         return BeanGenerator.this.getElement(element).implClass;
      }

      public JClassContainer onPackage(JPackage pkg) {
         return BeanGenerator.this.model.strategy.getPackage(pkg, Aspect.IMPLEMENTATION);
      }
   };

   public static Outline generate(Model model, ErrorReceiver _errorReceiver) {
      try {
         return new BeanGenerator(model, _errorReceiver);
      } catch (AbortException var3) {
         return null;
      }
   }

   private BeanGenerator(Model _model, ErrorReceiver _errorReceiver) {
      this.model = _model;
      this.codeModel = this.model.codeModel;
      this.errorReceiver = _errorReceiver;
      this.codeModelClassFactory = new CodeModelClassFactory(this.errorReceiver);
      Iterator var3 = this.model.enums().values().iterator();

      while(var3.hasNext()) {
         CEnumLeafInfo p = (CEnumLeafInfo)var3.next();
         this.enums.put(p, this.generateEnumDef(p));
      }

      JPackage[] packages = this.getUsedPackages(Aspect.EXPOSED);
      JPackage[] var12 = packages;
      int var5 = packages.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         JPackage pkg = var12[var6];
         this.getPackageContext(pkg);
      }

      Iterator var13 = this.model.beans().values().iterator();

      while(var13.hasNext()) {
         CClassInfo bean = (CClassInfo)var13.next();
         this.getClazz(bean);
      }

      var13 = this.packageContexts.values().iterator();

      while(var13.hasNext()) {
         PackageOutlineImpl p = (PackageOutlineImpl)var13.next();
         p.calcDefaultValues();
      }

      JClass OBJECT = this.codeModel.ref(Object.class);
      Iterator var17 = this.getClasses().iterator();

      ClassOutlineImpl cc;
      while(var17.hasNext()) {
         cc = (ClassOutlineImpl)var17.next();
         CClassInfo superClass = cc.target.getBaseClass();
         if (superClass != null) {
            this.model.strategy._extends(cc, this.getClazz(superClass));
         } else {
            CClassRef refSuperClass = cc.target.getRefBaseClass();
            if (refSuperClass != null) {
               cc.implClass._extends(refSuperClass.toType(this, Aspect.EXPOSED));
            } else {
               if (this.model.rootClass != null && cc.implClass._extends().equals(OBJECT)) {
                  cc.implClass._extends(this.model.rootClass);
               }

               if (this.model.rootInterface != null) {
                  cc.ref._implements(this.model.rootInterface);
               }
            }
         }

         if (this.model.serializable) {
            cc.implClass._implements(Serializable.class);
            if (this.model.serialVersionUID != null) {
               cc.implClass.field(28, (JType)this.codeModel.LONG, "serialVersionUID", JExpr.lit(this.model.serialVersionUID));
            }
         }

         CClassInfoParent base = cc.target.parent();
         if (base != null && base instanceof CClassInfo) {
            String pkg = base.getOwnerPackage().name();
            String shortName = base.fullName().substring(base.fullName().indexOf(pkg) + pkg.length() + 1);
            if (cc.target.shortName.equals(shortName)) {
               this.getErrorReceiver().error(cc.target.getLocator(), Messages.ERR_KEYNAME_COLLISION.format(shortName));
            }
         }
      }

      var17 = this.getClasses().iterator();

      while(var17.hasNext()) {
         cc = (ClassOutlineImpl)var17.next();
         this.generateClassBody(cc);
      }

      var17 = this.enums.values().iterator();

      while(var17.hasNext()) {
         EnumOutline eo = (EnumOutline)var17.next();
         this.generateEnumBody(eo);
      }

      var17 = this.model.getAllElements().iterator();

      while(var17.hasNext()) {
         CElementInfo ei = (CElementInfo)var17.next();
         this.getPackageContext(ei._package()).objectFactoryGenerator().populate(ei);
      }

      if (this.model.options.debugMode) {
         this.generateClassList();
      }

   }

   private void generateClassList() {
      try {
         JDefinedClass jc = this.codeModel.rootPackage()._class("JAXBDebug");
         JMethod m = jc.method(17, (Class)JAXBContext.class, "createContext");
         JVar $classLoader = m.param(ClassLoader.class, "classLoader");
         m._throws(JAXBException.class);
         JInvocation inv = this.codeModel.ref(JAXBContext.class).staticInvoke("newInstance");
         m.body()._return(inv);
         switch (this.model.strategy) {
            case INTF_AND_IMPL:
               StringBuilder buf = new StringBuilder();

               PackageOutlineImpl po;
               for(Iterator var11 = this.packageContexts.values().iterator(); var11.hasNext(); buf.append(po._package().name())) {
                  po = (PackageOutlineImpl)var11.next();
                  if (buf.length() > 0) {
                     buf.append(':');
                  }
               }

               inv.arg(buf.toString()).arg((JExpression)$classLoader);
               break;
            case BEAN_ONLY:
               Iterator var5 = this.getClasses().iterator();

               while(var5.hasNext()) {
                  ClassOutlineImpl cc = (ClassOutlineImpl)var5.next();
                  inv.arg(cc.implRef.dotclass());
               }

               var5 = this.packageContexts.values().iterator();

               while(var5.hasNext()) {
                  PackageOutlineImpl po = (PackageOutlineImpl)var5.next();
                  inv.arg(po.objectFactory().dotclass());
               }

               return;
            default:
               throw new IllegalStateException();
         }
      } catch (JClassAlreadyExistsException var8) {
         var8.printStackTrace();
      }

   }

   public Model getModel() {
      return this.model;
   }

   public JCodeModel getCodeModel() {
      return this.codeModel;
   }

   public JClassContainer getContainer(CClassInfoParent parent, Aspect aspect) {
      CClassInfoParent.Visitor v;
      switch (aspect) {
         case EXPOSED:
            v = this.exposedContainerBuilder;
            break;
         case IMPLEMENTATION:
            v = this.implContainerBuilder;
            break;
         default:
            assert false;

            throw new IllegalStateException();
      }

      return (JClassContainer)parent.accept(v);
   }

   public final JType resolve(CTypeRef ref, Aspect a) {
      return ((NType)ref.getTarget().getType()).toType(this, a);
   }

   public final JPackage[] getUsedPackages(Aspect aspect) {
      Set s = new TreeSet();
      Iterator var3 = this.model.beans().values().iterator();

      while(var3.hasNext()) {
         CClassInfo bean = (CClassInfo)var3.next();
         JClassContainer cont = this.getContainer(bean.parent(), aspect);
         if (cont.isPackage()) {
            s.add((JPackage)cont);
         }
      }

      var3 = this.model.getElementMappings((NClass)null).values().iterator();

      while(var3.hasNext()) {
         CElementInfo e = (CElementInfo)var3.next();
         s.add(e._package());
      }

      return (JPackage[])s.toArray(new JPackage[s.size()]);
   }

   public ErrorReceiver getErrorReceiver() {
      return this.errorReceiver;
   }

   public CodeModelClassFactory getClassFactory() {
      return this.codeModelClassFactory;
   }

   public PackageOutlineImpl getPackageContext(JPackage p) {
      PackageOutlineImpl r = (PackageOutlineImpl)this.packageContexts.get(p);
      if (r == null) {
         r = new PackageOutlineImpl(this, this.model, p);
         this.packageContexts.put(p, r);
      }

      return r;
   }

   private ClassOutlineImpl generateClassDef(CClassInfo bean) {
      ImplStructureStrategy.Result r = this.model.strategy.createClasses(this, bean);
      JDefinedClass implRef;
      if (bean.getUserSpecifiedImplClass() != null) {
         JDefinedClass usr;
         try {
            usr = this.codeModel._class(bean.getUserSpecifiedImplClass());
            usr.hide();
         } catch (JClassAlreadyExistsException var6) {
            usr = var6.getExistingClass();
         }

         usr._extends((JClass)r.implementation);
         implRef = usr;
      } else {
         implRef = r.implementation;
      }

      return new ClassOutlineImpl(this, bean, r.exposed, r.implementation, implRef);
   }

   public Collection getClasses() {
      assert this.model.beans().size() == this.classes.size();

      return this.classes.values();
   }

   public ClassOutlineImpl getClazz(CClassInfo bean) {
      ClassOutlineImpl r = (ClassOutlineImpl)this.classes.get(bean);
      if (r == null) {
         this.classes.put(bean, r = this.generateClassDef(bean));
      }

      return r;
   }

   public ElementOutlineImpl getElement(CElementInfo ei) {
      ElementOutlineImpl def = (ElementOutlineImpl)this.elements.get(ei);
      if (def == null && ei.hasClass()) {
         def = new ElementOutlineImpl(this, ei);
      }

      return def;
   }

   public EnumOutline getEnum(CEnumLeafInfo eli) {
      return (EnumOutline)this.enums.get(eli);
   }

   public Collection getEnums() {
      return this.enums.values();
   }

   public Iterable getAllPackageContexts() {
      return this.packageContexts.values();
   }

   public FieldOutline getField(CPropertyInfo prop) {
      return (FieldOutline)this.fields.get(prop);
   }

   private void generateClassBody(ClassOutlineImpl cc) {
      CClassInfo target = cc.target;
      String mostUsedNamespaceURI = cc._package().getMostUsedNamespaceURI();
      XmlTypeWriter xtw = (XmlTypeWriter)cc.implClass.annotate2(XmlTypeWriter.class);
      this.writeTypeName(cc.target.getTypeName(), xtw, mostUsedNamespaceURI);
      Iterator subclasses;
      if (this.model.options.target.isLaterThan(SpecVersion.V2_1)) {
         subclasses = cc.target.listSubclasses();
         if (subclasses.hasNext()) {
            XmlSeeAlsoWriter saw = (XmlSeeAlsoWriter)cc.implClass.annotate2(XmlSeeAlsoWriter.class);

            while(subclasses.hasNext()) {
               CClassInfo s = (CClassInfo)subclasses.next();
               saw.value((JType)this.getClazz(s).implRef);
            }
         }
      }

      if (target.isElement()) {
         String namespaceURI = target.getElementName().getNamespaceURI();
         String localPart = target.getElementName().getLocalPart();
         XmlRootElementWriter xrew = (XmlRootElementWriter)cc.implClass.annotate2(XmlRootElementWriter.class);
         xrew.name(localPart);
         if (!namespaceURI.equals(mostUsedNamespaceURI)) {
            xrew.namespace(namespaceURI);
         }
      }

      CPropertyInfo p;
      if (target.isOrdered()) {
         subclasses = target.getProperties().iterator();

         label51:
         while(true) {
            do {
               do {
                  if (!subclasses.hasNext()) {
                     break label51;
                  }

                  p = (CPropertyInfo)subclasses.next();
               } while(p instanceof CAttributePropertyInfo);
            } while(p instanceof CReferencePropertyInfo && ((CReferencePropertyInfo)p).isDummy());

            xtw.propOrder(p.getName(false));
         }
      } else {
         xtw.getAnnotationUse().paramArray("propOrder");
      }

      subclasses = target.getProperties().iterator();

      while(subclasses.hasNext()) {
         p = (CPropertyInfo)subclasses.next();
         this.generateFieldDecl(cc, p);
      }

      if (target.declaresAttributeWildcard()) {
         this.generateAttributeWildcard(cc);
      }

      cc.ref.javadoc().append(target.javadoc);
      cc._package().objectFactoryGenerator().populate(cc);
   }

   private void writeTypeName(QName typeName, XmlTypeWriter xtw, String mostUsedNamespaceURI) {
      if (typeName == null) {
         xtw.name("");
      } else {
         xtw.name(typeName.getLocalPart());
         String typeNameURI = typeName.getNamespaceURI();
         if (!typeNameURI.equals(mostUsedNamespaceURI)) {
            xtw.namespace(typeNameURI);
         }
      }

   }

   private void generateAttributeWildcard(ClassOutlineImpl cc) {
      String FIELD_NAME = "otherAttributes";
      String METHOD_SEED = this.model.getNameConverter().toClassName(FIELD_NAME);
      JClass mapType = this.codeModel.ref(Map.class).narrow(QName.class, String.class);
      JClass mapImpl = this.codeModel.ref(HashMap.class).narrow(QName.class, String.class);
      JFieldVar $ref = cc.implClass.field(4, (JType)mapType, FIELD_NAME, JExpr._new(mapImpl));
      $ref.annotate2(XmlAnyAttributeWriter.class);
      MethodWriter writer = cc.createMethodWriter();
      JMethod $get = writer.declareMethod((JType)mapType, "get" + METHOD_SEED);
      $get.javadoc().append("Gets a map that contains attributes that aren't bound to any typed property on this class.\n\n<p>\nthe map is keyed by the name of the attribute and \nthe value is the string value of the attribute.\n\nthe map returned by this method is live, and you can add new attribute\nby updating the map directly. Because of this design, there's no setter.\n");
      $get.javadoc().addReturn().append("always non-null");
      $get.body()._return($ref);
   }

   private EnumOutline generateEnumDef(CEnumLeafInfo e) {
      JDefinedClass type = this.getClassFactory().createClass(this.getContainer(e.parent, Aspect.EXPOSED), e.shortName, e.getLocator(), ClassType.ENUM);
      type.javadoc().append(e.javadoc);
      return new EnumOutline(e, type) {
         @NotNull
         public Outline parent() {
            return BeanGenerator.this;
         }
      };
   }

   private void generateEnumBody(EnumOutline eo) {
      JDefinedClass type = eo.clazz;
      CEnumLeafInfo e = eo.target;
      XmlTypeWriter xtw = (XmlTypeWriter)type.annotate2(XmlTypeWriter.class);
      this.writeTypeName(e.getTypeName(), xtw, eo._package().getMostUsedNamespaceURI());
      JCodeModel cModel = this.model.codeModel;
      JType baseExposedType = e.base.toType(this, Aspect.EXPOSED).unboxify();
      JType baseImplType = e.base.toType(this, Aspect.IMPLEMENTATION).unboxify();
      XmlEnumWriter xew = (XmlEnumWriter)type.annotate2(XmlEnumWriter.class);
      xew.value(baseExposedType);
      boolean needsValue = e.needsValueField();
      Set enumFieldNames = new HashSet();

      CEnumConstant mem;
      JEnumConstant constRef;
      for(Iterator var11 = e.members.iterator(); var11.hasNext(); eo.constants.add(new EnumConstantOutline(mem, constRef) {
      })) {
         mem = (CEnumConstant)var11.next();
         String constName = mem.getName();
         if (!JJavaName.isJavaIdentifier(constName)) {
            this.getErrorReceiver().error(e.getLocator(), Messages.ERR_UNUSABLE_NAME.format(mem.getLexicalValue(), constName));
         }

         if (!enumFieldNames.add(constName)) {
            this.getErrorReceiver().error(e.getLocator(), Messages.ERR_NAME_COLLISION.format(constName));
         }

         constRef = type.enumConstant(constName);
         if (needsValue) {
            constRef.arg(e.base.createConstant(this, new XmlString(mem.getLexicalValue())));
         }

         if (!mem.getLexicalValue().equals(constName)) {
            ((XmlEnumValueWriter)constRef.annotate2(XmlEnumValueWriter.class)).value(mem.getLexicalValue());
         }

         if (mem.javadoc != null) {
            constRef.javadoc().append(mem.javadoc);
         }
      }

      if (needsValue) {
         JFieldVar $value = type.field(12, (JType)baseExposedType, "value");
         type.method(1, (JType)baseExposedType, "value").body()._return($value);
         JMethod m = type.constructor(0);
         m.body().assign($value, m.param(baseImplType, "v"));
         m = type.method(17, (JType)type, "fromValue");
         JVar $v = m.param(baseExposedType, "v");
         JForEach fe = m.body().forEach(type, "c", type.staticInvoke("values"));
         Object eq;
         if (baseExposedType.isPrimitive()) {
            eq = fe.var().ref($value).eq($v);
         } else {
            eq = fe.var().ref($value).invoke("equals").arg((JExpression)$v);
         }

         fe.body()._if((JExpression)eq)._then()._return(fe.var());
         JInvocation ex = JExpr._new(cModel.ref(IllegalArgumentException.class));
         Object strForm;
         if (baseExposedType.isPrimitive()) {
            strForm = cModel.ref(String.class).staticInvoke("valueOf").arg((JExpression)$v);
         } else if (baseExposedType == cModel.ref(String.class)) {
            strForm = $v;
         } else {
            strForm = $v.invoke("toString");
         }

         m.body()._throw(ex.arg((JExpression)strForm));
      } else {
         type.method(1, (Class)String.class, "value").body()._return(JExpr.invoke("name"));
         JMethod m = type.method(17, (JType)type, "fromValue");
         m.body()._return(JExpr.invoke("valueOf").arg((JExpression)m.param(String.class, "v")));
      }

   }

   private FieldOutline generateFieldDecl(ClassOutlineImpl cc, CPropertyInfo prop) {
      FieldRenderer fr = prop.realization;
      if (fr == null) {
         fr = this.model.options.getFieldRendererFactory().getDefault();
      }

      FieldOutline field = fr.generate(cc, prop);
      this.fields.put(prop, field);
      return field;
   }

   public final void generateAdapterIfNecessary(CPropertyInfo prop, JAnnotatable field) {
      CAdapter adapter = prop.getAdapter();
      if (adapter != null) {
         if (adapter.getAdapterIfKnown() == SwaRefAdapterMarker.class) {
            field.annotate(XmlAttachmentRef.class);
         } else {
            XmlJavaTypeAdapterWriter xjtw = (XmlJavaTypeAdapterWriter)field.annotate2(XmlJavaTypeAdapterWriter.class);
            xjtw.value((JType)((NClass)adapter.adapterType).toType(this, Aspect.EXPOSED));
         }
      }

      switch (prop.id()) {
         case ID:
            field.annotate(XmlID.class);
            break;
         case IDREF:
            field.annotate(XmlIDREF.class);
      }

      if (prop.getExpectedMimeType() != null) {
         ((XmlMimeTypeWriter)field.annotate2(XmlMimeTypeWriter.class)).value(prop.getExpectedMimeType().toString());
      }

   }

   public final JClass addRuntime(Class clazz) {
      JClass g = (JClass)this.generatedRuntime.get(clazz);
      if (g == null) {
         JPackage implPkg = this.getUsedPackages(Aspect.IMPLEMENTATION)[0].subPackage("runtime");
         g = this.generateStaticClass(clazz, implPkg);
         this.generatedRuntime.put(clazz, g);
      }

      return g;
   }

   public JClass generateStaticClass(Class src, JPackage out) {
      String shortName = this.getShortName(src.getName());
      URL res = src.getResource(shortName + ".java");
      if (res == null) {
         res = src.getResource(shortName + ".java_");
      }

      if (res == null) {
         throw new InternalError("Unable to load source code of " + src.getName() + " as a resource");
      } else {
         JStaticJavaFile sjf = new JStaticJavaFile(out, shortName, res, (JStaticJavaFile.LineFilter)null);
         out.addResourceFile(sjf);
         return sjf.getJClass();
      }
   }

   private String getShortName(String name) {
      return name.substring(name.lastIndexOf(46) + 1);
   }
}
