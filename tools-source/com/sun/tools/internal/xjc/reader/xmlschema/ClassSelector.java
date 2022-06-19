package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JJavaName;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.util.JavadocEscapeWriter;
import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClass;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CElement;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CNonElement;
import com.sun.tools.internal.xjc.model.CTypeInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.LocalScoping;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.impl.util.SchemaWriter;
import com.sun.xml.internal.xsom.util.ComponentNameFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.Locator;

public final class ClassSelector extends BindingComponent {
   private final BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);
   private final Map bindMap = new HashMap();
   final Map boundElements = new HashMap();
   private final Stack bindQueue = new Stack();
   private final Set built = new HashSet();
   private final ClassBinder classBinder = new Abstractifier(new DefaultClassBinder());
   private final Stack classScopes = new Stack();
   private XSComponent currentRoot;
   private CClassInfo currentBean;
   private static final String[] reservedClassNames = new String[]{"ObjectFactory"};
   private static Set checkedPackageNames = new HashSet();

   public ClassSelector() {
      Ring.add(ClassBinder.class, this.classBinder);
      this.classScopes.push((Object)null);
      XSComplexType anyType = ((XSSchemaSet)Ring.get(XSSchemaSet.class)).getComplexType("http://www.w3.org/2001/XMLSchema", "anyType");
      this.bindMap.put(anyType, new Binding(anyType, CBuiltinLeafInfo.ANYTYPE));
   }

   public final CClassInfoParent getClassScope() {
      assert !this.classScopes.isEmpty();

      return (CClassInfoParent)this.classScopes.peek();
   }

   public final void pushClassScope(CClassInfoParent clsFctry) {
      assert clsFctry != null;

      this.classScopes.push(clsFctry);
   }

   public final void popClassScope() {
      this.classScopes.pop();
   }

   public XSComponent getCurrentRoot() {
      return this.currentRoot;
   }

   public CClassInfo getCurrentBean() {
      return this.currentBean;
   }

   public final CElement isBound(XSElementDecl x, XSComponent referer) {
      CElementInfo r = (CElementInfo)this.boundElements.get(x);
      return (CElement)(r != null ? r : this.bindToType(x, referer));
   }

   public CTypeInfo bindToType(XSComponent sc, XSComponent referer) {
      return this._bindToClass(sc, referer, false);
   }

   public CElement bindToType(XSElementDecl e, XSComponent referer) {
      return (CElement)this._bindToClass(e, referer, false);
   }

   public CClass bindToType(XSComplexType t, XSComponent referer, boolean cannotBeDelayed) {
      return (CClass)this._bindToClass(t, referer, cannotBeDelayed);
   }

   public TypeUse bindToType(XSType t, XSComponent referer) {
      return (TypeUse)(t instanceof XSSimpleType ? ((SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class)).build((XSSimpleType)t) : (CNonElement)this._bindToClass(t, referer, false));
   }

   CTypeInfo _bindToClass(@NotNull XSComponent sc, XSComponent referer, boolean cannotBeDelayed) {
      if (!this.bindMap.containsKey(sc)) {
         boolean isGlobal = false;
         if (sc instanceof XSDeclaration) {
            isGlobal = ((XSDeclaration)sc).isGlobal();
            if (isGlobal) {
               this.pushClassScope(new CClassInfoParent.Package(this.getPackage(((XSDeclaration)sc).getTargetNamespace())));
            }
         }

         CElement bean = (CElement)sc.apply(this.classBinder);
         if (isGlobal) {
            this.popClassScope();
         }

         if (bean == null) {
            return null;
         }

         if (bean instanceof CClassInfo) {
            XSSchema os = sc.getOwnerSchema();
            BISchemaBinding sb = (BISchemaBinding)this.builder.getBindInfo(os).get(BISchemaBinding.class);
            if (sb != null && !sb.map) {
               this.getErrorReporter().error(sc.getLocator(), "ERR_REFERENCE_TO_NONEXPORTED_CLASS", sc.apply(new ComponentNameFunction()));
               this.getErrorReporter().error(sb.getLocation(), "ERR_REFERENCE_TO_NONEXPORTED_CLASS_MAP_FALSE", os.getTargetNamespace());
               if (referer != null) {
                  this.getErrorReporter().error(referer.getLocator(), "ERR_REFERENCE_TO_NONEXPORTED_CLASS_REFERER", referer.apply(new ComponentNameFunction()));
               }
            }
         }

         this.queueBuild(sc, bean);
      }

      Binding bind = (Binding)this.bindMap.get(sc);
      if (cannotBeDelayed) {
         bind.build();
      }

      return bind.bean;
   }

   public void executeTasks() {
      while(this.bindQueue.size() != 0) {
         ((Binding)this.bindQueue.pop()).build();
      }

   }

   private boolean needValueConstructor(XSComponent sc) {
      if (!(sc instanceof XSElementDecl)) {
         return false;
      } else {
         XSElementDecl decl = (XSElementDecl)sc;
         return decl.getType().isSimpleType();
      }
   }

   public void queueBuild(XSComponent sc, CElement bean) {
      Binding b = new Binding(sc, bean);
      this.bindQueue.push(b);
      Binding old = (Binding)this.bindMap.put(sc, b);

      assert old == null || old.bean == bean;

   }

   private void addSchemaFragmentJavadoc(CClassInfo bean, XSComponent sc) {
      String doc = this.builder.getBindInfo(sc).getDocumentation();
      if (doc != null) {
         this.append(bean, doc);
      }

      Locator loc = sc.getLocator();
      String fileName = null;
      if (loc != null) {
         fileName = loc.getPublicId();
         if (fileName == null) {
            fileName = loc.getSystemId();
         }
      }

      if (fileName == null) {
         fileName = "";
      }

      String lineNumber = Messages.format("ClassSelector.JavadocLineUnknown");
      if (loc != null && loc.getLineNumber() != -1) {
         lineNumber = String.valueOf(loc.getLineNumber());
      }

      String componentName = (String)sc.apply(new ComponentNameFunction());
      String jdoc = Messages.format("ClassSelector.JavadocHeading", componentName, fileName, lineNumber);
      this.append(bean, jdoc);
      StringWriter out = new StringWriter();
      out.write("<pre>\n");
      SchemaWriter sw = new SchemaWriter(new JavadocEscapeWriter(out));
      sc.visit(sw);
      out.write("</pre>");
      this.append(bean, out.toString());
   }

   private void append(CClassInfo bean, String doc) {
      if (bean.javadoc == null) {
         bean.javadoc = doc + '\n';
      } else {
         bean.javadoc = bean.javadoc + '\n' + doc + '\n';
      }

   }

   public JPackage getPackage(String targetNamespace) {
      XSSchema s = ((XSSchemaSet)Ring.get(XSSchemaSet.class)).getSchema(targetNamespace);
      BISchemaBinding sb = (BISchemaBinding)this.builder.getBindInfo(s).get(BISchemaBinding.class);
      if (sb != null) {
         sb.markAsAcknowledged();
      }

      String name = null;
      if (this.builder.defaultPackage1 != null) {
         name = this.builder.defaultPackage1;
      }

      if (name == null && sb != null && sb.getPackageName() != null) {
         name = sb.getPackageName();
      }

      if (name == null && this.builder.defaultPackage2 != null) {
         name = this.builder.defaultPackage2;
      }

      if (name == null) {
         name = this.builder.getNameConverter().toPackageName(targetNamespace);
      }

      if (name == null) {
         name = "generated";
      }

      if (checkedPackageNames.add(name) && !JJavaName.isJavaPackageName(name)) {
         this.getErrorReporter().error(s.getLocator(), "ClassSelector.IncorrectPackageName", targetNamespace, name);
      }

      return ((JCodeModel)Ring.get(JCodeModel.class))._package(name);
   }

   private final class Binding {
      private final XSComponent sc;
      private final CTypeInfo bean;

      public Binding(XSComponent sc, CTypeInfo bean) {
         this.sc = sc;
         this.bean = bean;
      }

      void build() {
         if (this.bean instanceof CClassInfo) {
            CClassInfo bean = (CClassInfo)this.bean;
            if (ClassSelector.this.built.add(bean)) {
               String[] var2 = ClassSelector.reservedClassNames;
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  String reservedClassName = var2[var4];
                  if (bean.getName().equals(reservedClassName)) {
                     ClassSelector.this.getErrorReporter().error(this.sc.getLocator(), "ClassSelector.ReservedClassName", reservedClassName);
                     break;
                  }
               }

               if (ClassSelector.this.needValueConstructor(this.sc)) {
                  bean.addConstructor("value");
               }

               if (bean.javadoc == null) {
                  ClassSelector.this.addSchemaFragmentJavadoc(bean, this.sc);
               }

               if (ClassSelector.this.builder.getGlobalBinding().getFlattenClasses() == LocalScoping.NESTED) {
                  ClassSelector.this.pushClassScope(bean);
               } else {
                  ClassSelector.this.pushClassScope(bean.parent());
               }

               XSComponent oldRoot = ClassSelector.this.currentRoot;
               CClassInfo oldBean = ClassSelector.this.currentBean;
               ClassSelector.this.currentRoot = this.sc;
               ClassSelector.this.currentBean = bean;
               this.sc.visit((XSVisitor)Ring.get(BindRed.class));
               ClassSelector.this.currentBean = oldBean;
               ClassSelector.this.currentRoot = oldRoot;
               ClassSelector.this.popClassScope();
               BIProperty prop = (BIProperty)ClassSelector.this.builder.getBindInfo(this.sc).get(BIProperty.class);
               if (prop != null) {
                  prop.markAsAcknowledged();
               }

            }
         }
      }
   }
}
