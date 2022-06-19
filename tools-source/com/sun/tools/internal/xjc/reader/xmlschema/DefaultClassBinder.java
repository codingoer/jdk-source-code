package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.codemodel.internal.JJavaName;
import com.sun.codemodel.internal.JPackage;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CClassRef;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CElement;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIClass;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIXSubstitutable;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.internal.xjc.reader.xmlschema.ct.ComplexTypeBindingMode;
import com.sun.tools.internal.xjc.reader.xmlschema.ct.ComplexTypeFieldBuilder;
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
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

final class DefaultClassBinder implements ClassBinder {
   private final SimpleTypeBuilder stb = (SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class);
   private final Model model = (Model)Ring.get(Model.class);
   protected final BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);
   protected final ClassSelector selector = (ClassSelector)Ring.get(ClassSelector.class);
   protected final XSSchemaSet schemas = (XSSchemaSet)Ring.get(XSSchemaSet.class);

   public CElement attGroupDecl(XSAttGroupDecl decl) {
      return this.allow(decl, decl.getName());
   }

   public CElement attributeDecl(XSAttributeDecl decl) {
      return this.allow(decl, decl.getName());
   }

   public CElement modelGroup(XSModelGroup mgroup) {
      return this.never();
   }

   public CElement modelGroupDecl(XSModelGroupDecl decl) {
      return this.never();
   }

   public CElement complexType(XSComplexType type) {
      CElement ci = this.allow(type, type.getName());
      if (ci != null) {
         return ci;
      } else {
         BindInfo bi = this.builder.getBindInfo(type);
         if (type.isGlobal()) {
            QName tagName = null;
            String className = this.deriveName(type);
            Locator loc = type.getLocator();
            if (this.getGlobalBinding().isSimpleMode()) {
               XSElementDecl referer = this.getSoleElementReferer(type);
               if (referer != null && this.isCollapsable(referer)) {
                  tagName = BGMBuilder.getName(referer);
                  className = this.deriveName((XSDeclaration)referer);
                  loc = referer.getLocator();
               }
            }

            JPackage pkg = this.selector.getPackage(type.getTargetNamespace());
            return new CClassInfo(this.model, pkg, className, loc, this.getTypeName(type), tagName, type, bi.toCustomizationList());
         } else {
            XSElementDecl element = type.getScope();
            if (element.isGlobal() && this.isCollapsable(element)) {
               return this.builder.getBindInfo(element).get(BIClass.class) != null ? null : new CClassInfo(this.model, this.selector.getClassScope(), this.deriveName((XSDeclaration)element), element.getLocator(), (QName)null, BGMBuilder.getName(element), element, bi.toCustomizationList());
            } else {
               CElement parentType = this.selector.isBound(element, type);
               String className;
               Object scope;
               if (parentType != null && parentType instanceof CElementInfo && ((CElementInfo)parentType).hasClass()) {
                  scope = (CElementInfo)parentType;
                  className = "Type";
               } else {
                  className = this.builder.getNameConverter().toClassName(element.getName());
                  BISchemaBinding sb = (BISchemaBinding)this.builder.getBindInfo(type.getOwnerSchema()).get(BISchemaBinding.class);
                  if (sb != null) {
                     className = sb.mangleAnonymousTypeClassName(className);
                  }

                  scope = this.selector.getClassScope();
               }

               return new CClassInfo(this.model, (CClassInfoParent)scope, className, type.getLocator(), (QName)null, (QName)null, type, bi.toCustomizationList());
            }
         }
      }
   }

   private QName getTypeName(XSComplexType type) {
      return type.getRedefinedBy() != null ? null : BGMBuilder.getName(type);
   }

   private boolean isCollapsable(XSElementDecl decl) {
      XSType type = decl.getType();
      if (!type.isComplexType()) {
         return false;
      } else if (decl.getSubstitutables().size() <= 1 && decl.getSubstAffiliation() == null) {
         if (decl.isNillable()) {
            return false;
         } else {
            BIXSubstitutable bixSubstitutable = (BIXSubstitutable)this.builder.getBindInfo(decl).get(BIXSubstitutable.class);
            if (bixSubstitutable != null) {
               bixSubstitutable.markAsAcknowledged();
               return false;
            } else {
               if (this.getGlobalBinding().isSimpleMode() && decl.isGlobal()) {
                  XSElementDecl referer = this.getSoleElementReferer(decl.getType());
                  if (referer != null) {
                     assert referer == decl;

                     return true;
                  }
               }

               return type.isLocal() && type.isComplexType();
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   private XSElementDecl getSoleElementReferer(@NotNull XSType t) {
      Set referer = this.builder.getReferer(t);
      XSElementDecl sole = null;
      Iterator var4 = referer.iterator();

      while(var4.hasNext()) {
         XSComponent r = (XSComponent)var4.next();
         if (!(r instanceof XSElementDecl)) {
            return null;
         }

         XSElementDecl x = (XSElementDecl)r;
         if (x.isGlobal()) {
            if (sole != null) {
               return null;
            }

            sole = x;
         }
      }

      return sole;
   }

   public CElement elementDecl(XSElementDecl decl) {
      CElement r = this.allow(decl, decl.getName());
      if (r == null) {
         QName tagName = BGMBuilder.getName(decl);
         CCustomizations custs = this.builder.getBindInfo(decl).toCustomizationList();
         if (decl.isGlobal()) {
            if (this.isCollapsable(decl)) {
               return this.selector.bindToType(decl.getType().asComplexType(), decl, true);
            }

            String className = null;
            if (this.getGlobalBinding().isGenerateElementClass()) {
               className = this.deriveName((XSDeclaration)decl);
            }

            CElementInfo cei = new CElementInfo(this.model, tagName, this.selector.getClassScope(), className, custs, decl.getLocator());
            this.selector.boundElements.put(decl, cei);
            this.stb.refererStack.push(decl);
            cei.initContentType(this.selector.bindToType((XSType)decl.getType(), decl), decl, decl.getDefaultValue());
            this.stb.refererStack.pop();
            r = cei;
         }
      }

      XSElementDecl top = decl.getSubstAffiliation();
      if (top != null) {
         CElement topci = this.selector.bindToType((XSElementDecl)top, decl);
         if (r instanceof CClassInfo && topci instanceof CClassInfo) {
            ((CClassInfo)r).setBaseClass((CClassInfo)topci);
         }

         if (r instanceof CElementInfo && topci instanceof CElementInfo) {
            ((CElementInfo)r).setSubstitutionHead((CElementInfo)topci);
         }
      }

      return (CElement)r;
   }

   public CClassInfo empty(XSContentType ct) {
      return null;
   }

   public CClassInfo identityConstraint(XSIdentityConstraint xsIdentityConstraint) {
      return this.never();
   }

   public CClassInfo xpath(XSXPath xsxPath) {
      return this.never();
   }

   public CClassInfo attributeUse(XSAttributeUse use) {
      return this.never();
   }

   public CElement simpleType(XSSimpleType type) {
      CElement c = this.allow(type, type.getName());
      if (c != null) {
         return c;
      } else {
         return this.getGlobalBinding().isSimpleTypeSubstitution() && type.isGlobal() ? new CClassInfo(this.model, this.selector.getClassScope(), this.deriveName((XSDeclaration)type), type.getLocator(), BGMBuilder.getName(type), (QName)null, type, (CCustomizations)null) : this.never();
      }
   }

   public CClassInfo particle(XSParticle particle) {
      return this.never();
   }

   public CClassInfo wildcard(XSWildcard wc) {
      return this.never();
   }

   public CClassInfo annotation(XSAnnotation annon) {
      assert false;

      return null;
   }

   public CClassInfo notation(XSNotation not) {
      assert false;

      return null;
   }

   public CClassInfo facet(XSFacet decl) {
      assert false;

      return null;
   }

   public CClassInfo schema(XSSchema schema) {
      assert false;

      return null;
   }

   private CClassInfo never() {
      return null;
   }

   private CElement allow(XSComponent component, String defaultBaseName) {
      BIClass decl = null;
      if (component instanceof XSComplexType) {
         XSType complexType = (XSType)component;
         BIClass lastFoundRecursiveBiClass = null;
         if (complexType.getName() != null) {
            for(; !this.schemas.getAnyType().equals(complexType); complexType = complexType.getBaseType()) {
               BindInfo bindInfo = this.builder.getBindInfo(complexType);
               BIClass biClass = (BIClass)bindInfo.get(BIClass.class);
               if (biClass != null && "true".equals(biClass.getRecursive())) {
                  lastFoundRecursiveBiClass = biClass;
               }
            }
         }

         decl = lastFoundRecursiveBiClass;
      }

      BindInfo bindInfo = this.builder.getBindInfo(component);
      if (decl == null) {
         decl = (BIClass)bindInfo.get(BIClass.class);
         if (decl == null) {
            return null;
         }
      }

      decl.markAsAcknowledged();
      String ref = decl.getExistingClassRef();
      if (ref != null) {
         if (JJavaName.isFullyQualifiedClassName(ref)) {
            if (component instanceof XSComplexType) {
               ((ComplexTypeFieldBuilder)Ring.get(ComplexTypeFieldBuilder.class)).recordBindingMode((XSComplexType)component, ComplexTypeBindingMode.NORMAL);
            }

            return new CClassRef(this.model, component, decl, bindInfo.toCustomizationList());
         }

         ((ErrorReceiver)Ring.get(ErrorReceiver.class)).error(decl.getLocation(), Messages.format("ClassSelector.IncorrectClassName", ref));
      }

      String clsName = decl.getClassName();
      if (clsName == null) {
         if (defaultBaseName == null) {
            ((ErrorReceiver)Ring.get(ErrorReceiver.class)).error(decl.getLocation(), Messages.format("ClassSelector.ClassNameIsRequired"));
            defaultBaseName = "undefined" + component.hashCode();
         }

         clsName = this.builder.deriveName(defaultBaseName, component);
      } else if (!JJavaName.isJavaIdentifier(clsName)) {
         ((ErrorReceiver)Ring.get(ErrorReceiver.class)).error(decl.getLocation(), Messages.format("ClassSelector.IncorrectClassName", clsName));
         clsName = "Undefined" + component.hashCode();
      }

      QName typeName = null;
      QName elementName = null;
      if (component instanceof XSType) {
         XSType t = (XSType)component;
         typeName = BGMBuilder.getName(t);
      }

      XSElementDecl e;
      if (component instanceof XSElementDecl) {
         e = (XSElementDecl)component;
         elementName = BGMBuilder.getName(e);
      }

      if (component instanceof XSElementDecl && !this.isCollapsable((XSElementDecl)component)) {
         e = (XSElementDecl)component;
         CElementInfo cei = new CElementInfo(this.model, elementName, this.selector.getClassScope(), clsName, bindInfo.toCustomizationList(), decl.getLocation());
         this.selector.boundElements.put(e, cei);
         this.stb.refererStack.push(component);
         cei.initContentType(this.selector.bindToType((XSType)e.getType(), e), e, e.getDefaultValue());
         this.stb.refererStack.pop();
         return cei;
      } else {
         CClassInfo bt = new CClassInfo(this.model, this.selector.getClassScope(), clsName, decl.getLocation(), typeName, elementName, component, bindInfo.toCustomizationList());
         if (decl.getJavadoc() != null) {
            bt.javadoc = decl.getJavadoc() + "\n\n";
         }

         String implClass = decl.getUserSpecifiedImplClass();
         if (implClass != null) {
            bt.setUserSpecifiedImplClass(implClass);
         }

         return bt;
      }
   }

   private BIGlobalBinding getGlobalBinding() {
      return this.builder.getGlobalBinding();
   }

   private String deriveName(XSDeclaration comp) {
      return this.builder.deriveName(comp.getName(), comp);
   }

   private String deriveName(XSComplexType comp) {
      String seed = this.builder.deriveName(comp.getName(), comp);

      for(int cnt = comp.getRedefinedCount(); cnt > 0; --cnt) {
         seed = "Original" + seed;
      }

      return seed;
   }
}
