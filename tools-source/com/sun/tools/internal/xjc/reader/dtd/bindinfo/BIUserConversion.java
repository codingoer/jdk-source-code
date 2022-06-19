package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JPrimitiveType;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.model.CAdapter;
import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class BIUserConversion implements BIConversion {
   private final BindInfo owner;
   private final Element e;

   BIUserConversion(BindInfo bi, Element _e) {
      this.owner = bi;
      this.e = _e;
   }

   private static void add(Map m, BIConversion c) {
      m.put(c.name(), c);
   }

   static void addBuiltinConversions(BindInfo bi, Map m) {
      add(m, new BIUserConversion(bi, parse("<conversion name='boolean' type='java.lang.Boolean' parse='getBoolean' />")));
      add(m, new BIUserConversion(bi, parse("<conversion name='byte' type='java.lang.Byte' parse='parseByte' />")));
      add(m, new BIUserConversion(bi, parse("<conversion name='short' type='java.lang.Short' parse='parseShort' />")));
      add(m, new BIUserConversion(bi, parse("<conversion name='int' type='java.lang.Integer' parse='parseInt' />")));
      add(m, new BIUserConversion(bi, parse("<conversion name='long' type='java.lang.Long' parse='parseLong' />")));
      add(m, new BIUserConversion(bi, parse("<conversion name='float' type='java.lang.Float' parse='parseFloat' />")));
      add(m, new BIUserConversion(bi, parse("<conversion name='double' type='java.lang.Double' parse='parseDouble' />")));
   }

   private static Element parse(String text) {
      try {
         DocumentBuilderFactory dbf = XmlFactory.createDocumentBuilderFactory(false);
         InputSource is = new InputSource(new StringReader(text));
         return dbf.newDocumentBuilder().parse(is).getDocumentElement();
      } catch (SAXException var3) {
         throw new Error(var3);
      } catch (IOException var4) {
         throw new Error(var4);
      } catch (ParserConfigurationException var5) {
         throw new Error(var5);
      }
   }

   public Locator getSourceLocation() {
      return DOMLocator.getLocationInfo(this.e);
   }

   public String name() {
      return DOMUtil.getAttribute(this.e, "name");
   }

   public TypeUse getTransducer() {
      String ws = DOMUtil.getAttribute(this.e, "whitespace");
      if (ws == null) {
         ws = "collapse";
      }

      String type = DOMUtil.getAttribute(this.e, "type");
      if (type == null) {
         type = this.name();
      }

      JType t = null;
      int idx = type.lastIndexOf(46);
      if (idx < 0) {
         try {
            t = JPrimitiveType.parse(this.owner.codeModel, type);
         } catch (IllegalArgumentException var9) {
            type = this.owner.getTargetPackage().name() + '.' + type;
         }
      }

      if (t == null) {
         try {
            JDefinedClass cls = this.owner.codeModel._class(type);
            cls.hide();
            t = cls;
         } catch (JClassAlreadyExistsException var8) {
            t = var8.getExistingClass();
         }
      }

      String parse = DOMUtil.getAttribute(this.e, "parse");
      if (parse == null) {
         parse = "new";
      }

      String print = DOMUtil.getAttribute(this.e, "print");
      if (print == null) {
         print = "toString";
      }

      JDefinedClass adapter = this.generateAdapter(this.owner.codeModel, parse, print, ((JType)t).boxify());
      return TypeUseFactory.adapt(CBuiltinLeafInfo.STRING, new CAdapter(adapter));
   }

   private JDefinedClass generateAdapter(JCodeModel cm, String parseMethod, String printMethod, JClass inMemoryType) {
      JDefinedClass adapter = null;
      int id = 1;

      while(adapter == null) {
         try {
            JPackage pkg = this.owner.getTargetPackage();
            adapter = pkg._class("Adapter" + id);
         } catch (JClassAlreadyExistsException var12) {
            ++id;
         }
      }

      adapter._extends(cm.ref(XmlAdapter.class).narrow(String.class).narrow(inMemoryType));
      JMethod unmarshal = adapter.method(1, (JType)inMemoryType, "unmarshal");
      JVar $value = unmarshal.param(String.class, "value");
      Object inv;
      if (parseMethod.equals("new")) {
         inv = JExpr._new(inMemoryType).arg((JExpression)$value);
      } else {
         int idx = parseMethod.lastIndexOf(46);
         if (idx < 0) {
            inv = inMemoryType.staticInvoke(parseMethod).arg((JExpression)$value);
         } else {
            inv = JExpr.direct(parseMethod + "(value)");
         }
      }

      unmarshal.body()._return((JExpression)inv);
      JMethod marshal = adapter.method(1, (Class)String.class, "marshal");
      $value = marshal.param((JType)inMemoryType, "value");
      int idx = printMethod.lastIndexOf(46);
      if (idx < 0) {
         inv = $value.invoke(printMethod);
      } else {
         inv = JExpr.direct(printMethod + "(value)");
      }

      marshal.body()._return((JExpression)inv);
      return adapter;
   }
}
