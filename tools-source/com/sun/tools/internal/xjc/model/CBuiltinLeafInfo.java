package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.model.nav.NType;
import com.sun.tools.internal.xjc.model.nav.NavigatorImpl;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.runtime.ZeroOneBooleanAdapter;
import com.sun.tools.internal.xjc.util.NamespaceContextAdapter;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XmlString;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import org.xml.sax.Locator;

public abstract class CBuiltinLeafInfo implements CNonElement, BuiltinLeafInfo, LeafInfo, Location {
   private final NType type;
   private final QName typeName;
   private final QName[] typeNames;
   private final ID id;
   public static final Map LEAVES = new HashMap();
   public static final CBuiltinLeafInfo ANYTYPE = new NoConstantBuiltin(Object.class, "anyType");
   public static final CBuiltinLeafInfo STRING = new Builtin(String.class, "string") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.lit(lexical.value);
      }
   };
   public static final CBuiltinLeafInfo BOOLEAN = new Builtin(Boolean.class, "boolean") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.lit(DatatypeConverter.parseBoolean(lexical.value));
      }
   };
   public static final CBuiltinLeafInfo INT = new Builtin(Integer.class, "int") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.lit(DatatypeConverter.parseInt(lexical.value));
      }
   };
   public static final CBuiltinLeafInfo LONG = new Builtin(Long.class, "long") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.lit(DatatypeConverter.parseLong(lexical.value));
      }
   };
   public static final CBuiltinLeafInfo BYTE = new Builtin(Byte.class, "byte") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.cast(outline.getCodeModel().BYTE, JExpr.lit((int)DatatypeConverter.parseByte(lexical.value)));
      }
   };
   public static final CBuiltinLeafInfo SHORT = new Builtin(Short.class, "short") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.cast(outline.getCodeModel().SHORT, JExpr.lit((int)DatatypeConverter.parseShort(lexical.value)));
      }
   };
   public static final CBuiltinLeafInfo FLOAT = new Builtin(Float.class, "float") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.lit(DatatypeConverter.parseFloat(lexical.value));
      }
   };
   public static final CBuiltinLeafInfo DOUBLE = new Builtin(Double.class, "double") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr.lit(DatatypeConverter.parseDouble(lexical.value));
      }
   };
   public static final CBuiltinLeafInfo QNAME = new Builtin(QName.class, "QName") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         QName qn = DatatypeConverter.parseQName(lexical.value, new NamespaceContextAdapter(lexical));
         return JExpr._new(outline.getCodeModel().ref(QName.class)).arg(qn.getNamespaceURI()).arg(qn.getLocalPart()).arg(qn.getPrefix());
      }
   };
   public static final CBuiltinLeafInfo CALENDAR = new NoConstantBuiltin(XMLGregorianCalendar.class, "\u0000");
   public static final CBuiltinLeafInfo DURATION = new NoConstantBuiltin(Duration.class, "duration");
   public static final CBuiltinLeafInfo BIG_INTEGER = new Builtin(BigInteger.class, "integer") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr._new(outline.getCodeModel().ref(BigInteger.class)).arg(lexical.value.trim());
      }
   };
   public static final CBuiltinLeafInfo BIG_DECIMAL = new Builtin(BigDecimal.class, "decimal") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return JExpr._new(outline.getCodeModel().ref(BigDecimal.class)).arg(lexical.value.trim());
      }
   };
   public static final CBuiltinLeafInfo BASE64_BYTE_ARRAY = new Builtin(byte[].class, "base64Binary") {
      public JExpression createConstant(Outline outline, XmlString lexical) {
         return outline.getCodeModel().ref(DatatypeConverter.class).staticInvoke("parseBase64Binary").arg(lexical.value);
      }
   };
   public static final CBuiltinLeafInfo DATA_HANDLER = new NoConstantBuiltin(DataHandler.class, "base64Binary");
   public static final CBuiltinLeafInfo IMAGE = new NoConstantBuiltin(Image.class, "base64Binary");
   public static final CBuiltinLeafInfo XML_SOURCE = new NoConstantBuiltin(Source.class, "base64Binary");
   public static final TypeUse HEXBIN_BYTE_ARRAY;
   public static final TypeUse TOKEN;
   public static final TypeUse NORMALIZED_STRING;
   public static final TypeUse ID;
   public static final TypeUse BOOLEAN_ZERO_OR_ONE;
   public static final TypeUse IDREF;
   public static final TypeUse STRING_LIST;

   private CBuiltinLeafInfo(NType typeToken, ID id, QName... typeNames) {
      this.type = typeToken;
      this.typeName = typeNames.length > 0 ? typeNames[0] : null;
      this.typeNames = typeNames;
      this.id = id;
   }

   public JType toType(Outline o, Aspect aspect) {
      return this.getType().toType(o, aspect);
   }

   /** @deprecated */
   @Deprecated
   public final boolean isCollection() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public CNonElement getInfo() {
      return this;
   }

   public ID idUse() {
      return this.id;
   }

   public MimeType getExpectedMimeType() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public final CAdapter getAdapterUse() {
      return null;
   }

   public Locator getLocator() {
      return Model.EMPTY_LOCATOR;
   }

   public final XSComponent getSchemaComponent() {
      throw new UnsupportedOperationException("TODO. If you hit this, let us know.");
   }

   public final TypeUse makeCollection() {
      return TypeUseFactory.makeCollection(this);
   }

   public final TypeUse makeAdapted(Class adapter, boolean copy) {
      return TypeUseFactory.adapt(this, adapter, copy);
   }

   public final TypeUse makeMimeTyped(MimeType mt) {
      return TypeUseFactory.makeMimeTyped(this, mt);
   }

   /** @deprecated */
   public final boolean isElement() {
      return false;
   }

   /** @deprecated */
   public final QName getElementName() {
      return null;
   }

   /** @deprecated */
   public final Element asElement() {
      return null;
   }

   public NType getType() {
      return this.type;
   }

   public final QName[] getTypeNames() {
      return this.typeNames;
   }

   /** @deprecated */
   public final boolean canBeReferencedByIDREF() {
      return false;
   }

   public QName getTypeName() {
      return this.typeName;
   }

   public Locatable getUpstream() {
      return null;
   }

   public Location getLocation() {
      return this;
   }

   public boolean isSimpleType() {
      return true;
   }

   // $FF: synthetic method
   CBuiltinLeafInfo(NType x0, ID x1, QName[] x2, Object x3) {
      this(x0, x1, x2);
   }

   static {
      HEXBIN_BYTE_ARRAY = STRING.makeAdapted(HexBinaryAdapter.class, false);
      TOKEN = STRING.makeAdapted(CollapsedStringAdapter.class, false);
      NORMALIZED_STRING = STRING.makeAdapted(NormalizedStringAdapter.class, false);
      ID = TypeUseFactory.makeID(TOKEN, com.sun.xml.internal.bind.v2.model.core.ID.ID);
      BOOLEAN_ZERO_OR_ONE = STRING.makeAdapted(ZeroOneBooleanAdapter.class, true);
      IDREF = TypeUseFactory.makeID(ANYTYPE, com.sun.xml.internal.bind.v2.model.core.ID.IDREF);
      STRING_LIST = STRING.makeCollection();
   }

   private static final class NoConstantBuiltin extends Builtin {
      public NoConstantBuiltin(Class c, String typeName) {
         super(c, typeName);
      }

      public JExpression createConstant(Outline outline, XmlString lexical) {
         return null;
      }
   }

   private abstract static class Builtin extends CBuiltinLeafInfo {
      protected Builtin(Class c, String typeName) {
         this(c, typeName, com.sun.xml.internal.bind.v2.model.core.ID.NONE);
      }

      protected Builtin(Class c, String typeName, ID id) {
         super(NavigatorImpl.theInstance.ref(c), id, new QName[]{new QName("http://www.w3.org/2001/XMLSchema", typeName)}, null);
         LEAVES.put(this.getType(), this);
      }

      public CCustomizations getCustomizations() {
         return CCustomizations.EMPTY;
      }
   }
}
