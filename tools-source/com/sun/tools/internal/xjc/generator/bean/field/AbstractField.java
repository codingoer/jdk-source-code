package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.api.SpecVersion;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlAnyElementWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlAttributeWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlElementRefWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlElementRefsWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlElementWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlElementsWriter;
import com.sun.tools.internal.xjc.generator.annotation.spec.XmlSchemaTypeWriter;
import com.sun.tools.internal.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.internal.xjc.model.CAttributePropertyInfo;
import com.sun.tools.internal.xjc.model.CElement;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.CValuePropertyInfo;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.ClassOutline;
import com.sun.tools.internal.xjc.outline.FieldAccessor;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import com.sun.tools.internal.xjc.reader.TypeUtil;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.TODO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

abstract class AbstractField implements FieldOutline {
   protected final ClassOutlineImpl outline;
   protected final CPropertyInfo prop;
   protected final JCodeModel codeModel;
   protected final JType implType;
   protected final JType exposedType;
   private XmlElementsWriter xesw = null;

   protected AbstractField(ClassOutlineImpl outline, CPropertyInfo prop) {
      this.outline = outline;
      this.prop = prop;
      this.codeModel = outline.parent().getCodeModel();
      this.implType = this.getType(Aspect.IMPLEMENTATION);
      this.exposedType = this.getType(Aspect.EXPOSED);
   }

   public final ClassOutline parent() {
      return this.outline;
   }

   public final CPropertyInfo getPropertyInfo() {
      return this.prop;
   }

   protected void annotate(JAnnotatable field) {
      assert field != null;

      if (this.prop instanceof CAttributePropertyInfo) {
         this.annotateAttribute(field);
      } else if (this.prop instanceof CElementPropertyInfo) {
         this.annotateElement(field);
      } else if (this.prop instanceof CValuePropertyInfo) {
         field.annotate(XmlValue.class);
      } else if (this.prop instanceof CReferencePropertyInfo) {
         this.annotateReference(field);
      }

      this.outline.parent().generateAdapterIfNecessary(this.prop, field);
      QName st = this.prop.getSchemaType();
      if (st != null) {
         ((XmlSchemaTypeWriter)field.annotate2(XmlSchemaTypeWriter.class)).name(st.getLocalPart()).namespace(st.getNamespaceURI());
      }

      if (this.prop.inlineBinaryData()) {
         field.annotate(XmlInlineBinaryData.class);
      }

   }

   private void annotateReference(JAnnotatable field) {
      CReferencePropertyInfo rp = (CReferencePropertyInfo)this.prop;
      TODO.prototype();
      Collection elements = rp.getElements();
      XmlElementRefWriter refw;
      if (elements.size() == 1) {
         refw = (XmlElementRefWriter)field.annotate2(XmlElementRefWriter.class);
         CElement e = (CElement)elements.iterator().next();
         refw.name(e.getElementName().getLocalPart()).namespace(e.getElementName().getNamespaceURI()).type(((NType)e.getType()).toType(this.outline.parent(), Aspect.IMPLEMENTATION));
         if (this.getOptions().target.isLaterThan(SpecVersion.V2_2)) {
            refw.required(rp.isRequired());
         }
      } else if (elements.size() > 1) {
         XmlElementRefsWriter refsw = (XmlElementRefsWriter)field.annotate2(XmlElementRefsWriter.class);
         Iterator var6 = elements.iterator();

         while(var6.hasNext()) {
            CElement e = (CElement)var6.next();
            refw = refsw.value();
            refw.name(e.getElementName().getLocalPart()).namespace(e.getElementName().getNamespaceURI()).type(((NType)e.getType()).toType(this.outline.parent(), Aspect.IMPLEMENTATION));
            if (this.getOptions().target.isLaterThan(SpecVersion.V2_2)) {
               refw.required(rp.isRequired());
            }
         }
      }

      if (rp.isMixed()) {
         field.annotate(XmlMixed.class);
      }

      NClass dh = rp.getDOMHandler();
      if (dh != null) {
         XmlAnyElementWriter xaew = (XmlAnyElementWriter)field.annotate2(XmlAnyElementWriter.class);
         xaew.lax(rp.getWildcard().allowTypedObject);
         JClass value = dh.toType(this.outline.parent(), Aspect.IMPLEMENTATION);
         if (!value.equals(this.codeModel.ref(W3CDomHandler.class))) {
            xaew.value((JType)value);
         }
      }

   }

   private void annotateElement(JAnnotatable field) {
      CElementPropertyInfo ep = (CElementPropertyInfo)this.prop;
      List types = ep.getTypes();
      if (ep.isValueList()) {
         field.annotate(XmlList.class);
      }

      assert ep.getXmlName() == null;

      if (types.size() == 1) {
         CTypeRef t = (CTypeRef)types.get(0);
         this.writeXmlElementAnnotation(field, t, this.resolve(t, Aspect.IMPLEMENTATION), false);
      } else {
         Iterator var6 = types.iterator();

         while(var6.hasNext()) {
            CTypeRef t = (CTypeRef)var6.next();
            this.writeXmlElementAnnotation(field, t, this.resolve(t, Aspect.IMPLEMENTATION), true);
         }

         this.xesw = null;
      }

   }

   private void writeXmlElementAnnotation(JAnnotatable field, CTypeRef ctype, JType jtype, boolean checkWrapper) {
      XmlElementWriter xew = null;
      XmlNsForm formDefault = this.parent()._package().getElementFormDefault();
      String propName = this.prop.getName(false);
      String enclosingTypeNS;
      if (this.parent().target.getTypeName() == null) {
         enclosingTypeNS = this.parent()._package().getMostUsedNamespaceURI();
      } else {
         enclosingTypeNS = this.parent().target.getTypeName().getNamespaceURI();
      }

      String generatedName = ctype.getTagName().getLocalPart();
      if (!generatedName.equals(propName)) {
         if (xew == null) {
            xew = this.getXew(checkWrapper, field);
         }

         xew.name(generatedName);
      }

      String generatedNS = ctype.getTagName().getNamespaceURI();
      if (formDefault == XmlNsForm.QUALIFIED && !generatedNS.equals(enclosingTypeNS) || formDefault == XmlNsForm.UNQUALIFIED && !generatedNS.equals("")) {
         if (xew == null) {
            xew = this.getXew(checkWrapper, field);
         }

         xew.namespace(generatedNS);
      }

      CElementPropertyInfo ep = (CElementPropertyInfo)this.prop;
      if (ep.isRequired() && this.exposedType.isReference()) {
         if (xew == null) {
            xew = this.getXew(checkWrapper, field);
         }

         xew.required(true);
      }

      if (ep.isRequired() && !this.prop.isCollection()) {
         jtype = jtype.unboxify();
      }

      if (!jtype.equals(this.exposedType) || this.getOptions().runtime14 && this.prop.isCollection()) {
         if (xew == null) {
            xew = this.getXew(checkWrapper, field);
         }

         xew.type(jtype);
      }

      String defaultValue = ctype.getDefaultValue();
      if (defaultValue != null) {
         if (xew == null) {
            xew = this.getXew(checkWrapper, field);
         }

         xew.defaultValue(defaultValue);
      }

      if (ctype.isNillable()) {
         if (xew == null) {
            xew = this.getXew(checkWrapper, field);
         }

         xew.nillable(true);
      }

   }

   protected final Options getOptions() {
      return this.parent().parent().getModel().options;
   }

   private XmlElementWriter getXew(boolean checkWrapper, JAnnotatable field) {
      XmlElementWriter xew;
      if (checkWrapper) {
         if (this.xesw == null) {
            this.xesw = (XmlElementsWriter)field.annotate2(XmlElementsWriter.class);
         }

         xew = this.xesw.value();
      } else {
         xew = (XmlElementWriter)field.annotate2(XmlElementWriter.class);
      }

      return xew;
   }

   private void annotateAttribute(JAnnotatable field) {
      CAttributePropertyInfo ap = (CAttributePropertyInfo)this.prop;
      QName attName = ap.getXmlName();
      XmlAttributeWriter xaw = (XmlAttributeWriter)field.annotate2(XmlAttributeWriter.class);
      String generatedName = attName.getLocalPart();
      String generatedNS = attName.getNamespaceURI();
      if (!generatedName.equals(ap.getName(false)) || !generatedName.equals(ap.getName(true)) || this.outline.parent().getModel().getNameConverter() != NameConverter.standard) {
         xaw.name(generatedName);
      }

      if (!generatedNS.equals("")) {
         xaw.namespace(generatedNS);
      }

      if (ap.isRequired()) {
         xaw.required(true);
      }

   }

   protected final JFieldVar generateField(JType type) {
      return this.outline.implClass.field(2, (JType)type, this.prop.getName(false));
   }

   protected final JExpression castToImplType(JExpression exp) {
      return (JExpression)(this.implType == this.exposedType ? exp : JExpr.cast(this.implType, exp));
   }

   protected JType getType(final Aspect aspect) {
      if (this.prop.getAdapter() != null) {
         return ((NType)this.prop.getAdapter().customType).toType(this.outline.parent(), aspect);
      } else {
         final class TypeList extends ArrayList {
            void add(CTypeInfo t) {
               this.add((Object)((NType)t.getType()).toType(AbstractField.this.outline.parent(), aspect));
               if (t instanceof CElementInfo) {
                  this.add(((CElementInfo)t).getSubstitutionMembers());
               }

            }

            void add(Collection col) {
               Iterator var2 = col.iterator();

               while(var2.hasNext()) {
                  CTypeInfo typeInfo = (CTypeInfo)var2.next();
                  this.add(typeInfo);
               }

            }
         }

         TypeList r = new TypeList();
         r.add(this.prop.ref());
         JType t;
         if (this.prop.baseType != null) {
            t = this.prop.baseType;
         } else {
            t = TypeUtil.getCommonBaseType(this.codeModel, (Collection)r);
         }

         if (this.prop.isUnboxable()) {
            t = t.unboxify();
         }

         return t;
      }
   }

   protected final List listPossibleTypes(CPropertyInfo prop) {
      List r = new ArrayList();
      Iterator var3 = prop.ref().iterator();

      while(true) {
         while(var3.hasNext()) {
            CTypeInfo tt = (CTypeInfo)var3.next();
            JType t = ((NType)tt.getType()).toType(this.outline.parent(), Aspect.EXPOSED);
            if (!t.isPrimitive() && !t.isArray()) {
               r.add(t);
               r.add("\n");
            } else {
               r.add(t.fullName());
            }
         }

         return r;
      }
   }

   private JType resolve(CTypeRef typeRef, Aspect a) {
      return this.outline.parent().resolve(typeRef, a);
   }

   protected abstract class Accessor implements FieldAccessor {
      protected final JExpression $target;

      protected Accessor(JExpression $target) {
         this.$target = $target;
      }

      public final FieldOutline owner() {
         return AbstractField.this;
      }

      public final CPropertyInfo getPropertyInfo() {
         return AbstractField.this.prop;
      }
   }
}
