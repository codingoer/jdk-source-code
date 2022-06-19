package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JConditional;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.model.CAdapter;
import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.TypeUtil;
import com.sun.tools.internal.xjc.reader.xmlschema.ClassSelector;
import com.sun.xml.internal.xsom.XSSimpleType;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public abstract class BIConversion extends AbstractDeclarationImpl {
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "conversion");

   /** @deprecated */
   @Deprecated
   public BIConversion(Locator loc) {
      super(loc);
   }

   protected BIConversion() {
   }

   public abstract TypeUse getTypeUse(XSSimpleType var1);

   public QName getName() {
      return NAME;
   }

   @XmlRootElement(
      name = "javaType",
      namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
   )
   public static class UserAdapter extends BIConversion {
      @XmlAttribute(
         name = "name"
      )
      private String type = null;
      @XmlAttribute
      private String adapter = null;
      private TypeUse typeUse;

      public TypeUse getTypeUse(XSSimpleType owner) {
         if (this.typeUse != null) {
            return this.typeUse;
         } else {
            JCodeModel cm = this.getCodeModel();

            JDefinedClass a;
            try {
               a = cm._class(this.adapter);
               a.hide();
               a._extends(cm.ref(XmlAdapter.class).narrow(String.class).narrow(cm.ref(this.type)));
            } catch (JClassAlreadyExistsException var5) {
               a = var5.getExistingClass();
            }

            this.typeUse = TypeUseFactory.adapt(CBuiltinLeafInfo.STRING, new CAdapter(a));
            return this.typeUse;
         }
      }
   }

   @XmlRootElement(
      name = "javaType"
   )
   public static class User extends BIConversion {
      @XmlAttribute
      private String parseMethod;
      @XmlAttribute
      private String printMethod;
      @XmlAttribute(
         name = "name"
      )
      private String type = "java.lang.String";
      private JType inMemoryType;
      private TypeUse typeUse;
      private static final String[] knownBases = new String[]{"Float", "Double", "Byte", "Short", "Int", "Long", "Boolean"};
      public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "javaType");

      public User(Locator loc, String parseMethod, String printMethod, JType inMemoryType) {
         super(loc);
         this.parseMethod = parseMethod;
         this.printMethod = printMethod;
         this.inMemoryType = inMemoryType;
      }

      public User() {
      }

      public TypeUse getTypeUse(XSSimpleType owner) {
         if (this.typeUse != null) {
            return this.typeUse;
         } else {
            JCodeModel cm = this.getCodeModel();
            if (this.inMemoryType == null) {
               this.inMemoryType = TypeUtil.getType(cm, this.type, (ErrorReceiver)Ring.get(ErrorReceiver.class), this.getLocation());
            }

            JDefinedClass adapter = this.generateAdapter(this.parseMethodFor(owner), this.printMethodFor(owner), owner);
            this.typeUse = TypeUseFactory.adapt(CBuiltinLeafInfo.STRING, new CAdapter(adapter));
            return this.typeUse;
         }
      }

      private JDefinedClass generateAdapter(String parseMethod, String printMethod, XSSimpleType owner) {
         JDefinedClass adapter = null;
         int id = 1;

         while(adapter == null) {
            try {
               JPackage pkg = ((ClassSelector)Ring.get(ClassSelector.class)).getClassScope().getOwnerPackage();
               adapter = pkg._class("Adapter" + id);
            } catch (JClassAlreadyExistsException var13) {
               ++id;
            }
         }

         JClass bim = this.inMemoryType.boxify();
         adapter._extends(this.getCodeModel().ref(XmlAdapter.class).narrow(String.class).narrow(bim));
         JMethod unmarshal = adapter.method(1, (JType)bim, "unmarshal");
         JVar $value = unmarshal.param(String.class, "value");
         Object inv;
         if (parseMethod.equals("new")) {
            inv = JExpr._new(bim).arg((JExpression)$value);
         } else {
            int idx = parseMethod.lastIndexOf(46);
            if (idx < 0) {
               inv = bim.staticInvoke(parseMethod).arg((JExpression)$value);
            } else {
               inv = JExpr.direct(parseMethod + "(value)");
            }
         }

         unmarshal.body()._return((JExpression)inv);
         JMethod marshal = adapter.method(1, (Class)String.class, "marshal");
         $value = marshal.param((JType)bim, "value");
         if (printMethod.startsWith("javax.xml.bind.DatatypeConverter.")) {
            marshal.body()._if($value.eq(JExpr._null()))._then()._return(JExpr._null());
         }

         int idx = printMethod.lastIndexOf(46);
         if (idx < 0) {
            inv = $value.invoke(printMethod);
            JConditional jcon = marshal.body()._if($value.eq(JExpr._null()));
            jcon._then()._return(JExpr._null());
         } else if (this.printMethod == null) {
            JType t = this.inMemoryType.unboxify();
            inv = JExpr.direct(printMethod + "((" + this.findBaseConversion(owner).toLowerCase() + ")(" + t.fullName() + ")value)");
         } else {
            inv = JExpr.direct(printMethod + "(value)");
         }

         marshal.body()._return((JExpression)inv);
         return adapter;
      }

      private String printMethodFor(XSSimpleType owner) {
         if (this.printMethod != null) {
            return this.printMethod;
         } else {
            if (this.inMemoryType.unboxify().isPrimitive()) {
               String method = this.getConversionMethod("print", owner);
               if (method != null) {
                  return method;
               }
            }

            return "toString";
         }
      }

      private String parseMethodFor(XSSimpleType owner) {
         if (this.parseMethod != null) {
            return this.parseMethod;
         } else {
            if (this.inMemoryType.unboxify().isPrimitive()) {
               String method = this.getConversionMethod("parse", owner);
               if (method != null) {
                  return '(' + this.inMemoryType.unboxify().fullName() + ')' + method;
               }
            }

            return "new";
         }
      }

      private String getConversionMethod(String methodPrefix, XSSimpleType owner) {
         String bc = this.findBaseConversion(owner);
         return bc == null ? null : DatatypeConverter.class.getName() + '.' + methodPrefix + bc;
      }

      private String findBaseConversion(XSSimpleType owner) {
         for(XSSimpleType st = owner; st != null; st = st.getSimpleBaseType()) {
            if ("http://www.w3.org/2001/XMLSchema".equals(st.getTargetNamespace())) {
               String name = st.getName().intern();
               String[] var4 = knownBases;
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  String s = var4[var6];
                  if (name.equalsIgnoreCase(s)) {
                     return s;
                  }
               }
            }
         }

         return null;
      }

      public QName getName() {
         return NAME;
      }
   }

   public static final class Static extends BIConversion {
      private final TypeUse transducer;

      public Static(Locator loc, TypeUse transducer) {
         super(loc);
         this.transducer = transducer;
      }

      public TypeUse getTypeUse(XSSimpleType owner) {
         return this.transducer;
      }
   }
}
