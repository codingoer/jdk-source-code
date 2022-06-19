package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.codemodel.internal.JJavaName;
import com.sun.codemodel.internal.util.JavadocEscapeWriter;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CClassRef;
import com.sun.tools.internal.xjc.model.CEnumConstant;
import com.sun.tools.internal.xjc.model.CEnumLeafInfo;
import com.sun.tools.internal.xjc.model.CNonElement;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIEnum;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIEnumMember;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.EnumMemberMode;
import com.sun.tools.internal.xjc.util.MimeTypeRange;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapterMarker;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSVariety;
import com.sun.xml.internal.xsom.impl.util.SchemaWriter;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeFunction;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.activation.MimeTypeParseException;
import javax.xml.bind.DatatypeConverter;
import org.xml.sax.Locator;

public final class SimpleTypeBuilder extends BindingComponent {
   protected final BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);
   private final Model model = (Model)Ring.get(Model.class);
   public final Stack refererStack = new Stack();
   private final Set acknowledgedXmimeContentTypes = new HashSet();
   private XSSimpleType initiatingType;
   public static final Map builtinConversions = new HashMap();
   public final XSSimpleTypeFunction composer = new XSSimpleTypeFunction() {
      public TypeUse listSimpleType(XSListSimpleType type) {
         XSSimpleType itemType = type.getItemType();
         SimpleTypeBuilder.this.refererStack.push(itemType);
         TypeUse tu = TypeUseFactory.makeCollection(SimpleTypeBuilder.this.build(type.getItemType()));
         SimpleTypeBuilder.this.refererStack.pop();
         return tu;
      }

      public TypeUse unionSimpleType(XSUnionSimpleType type) {
         boolean isCollection = false;

         for(int i = 0; i < type.getMemberSize(); ++i) {
            if (type.getMember(i).getVariety() == XSVariety.LIST || type.getMember(i).getVariety() == XSVariety.UNION) {
               isCollection = true;
               break;
            }
         }

         TypeUse r = CBuiltinLeafInfo.STRING;
         if (isCollection) {
            r = TypeUseFactory.makeCollection((TypeUse)r);
         }

         return (TypeUse)r;
      }

      public TypeUse restrictionSimpleType(XSRestrictionSimpleType type) {
         return SimpleTypeBuilder.this.compose(type.getSimpleBaseType());
      }
   };
   private static Set reportedEnumMemberSizeWarnings;
   private static final Set builtinTypeSafeEnumCapableTypes;
   private static final BigInteger LONG_MIN;
   private static final BigInteger LONG_MAX;
   private static final BigInteger INT_MIN;
   private static final BigInteger INT_MAX;

   public TypeUse build(XSSimpleType type) {
      XSSimpleType oldi = this.initiatingType;
      this.initiatingType = type;
      TypeUse e = this.checkRefererCustomization(type);
      if (e == null) {
         e = this.compose(type);
      }

      this.initiatingType = oldi;
      return e;
   }

   public TypeUse buildDef(XSSimpleType type) {
      XSSimpleType oldi = this.initiatingType;
      this.initiatingType = type;
      TypeUse e = (TypeUse)type.apply(this.composer);
      this.initiatingType = oldi;
      return e;
   }

   private BIConversion getRefererCustomization() {
      BindInfo info = this.builder.getBindInfo(this.getReferer());
      BIProperty prop = (BIProperty)info.get(BIProperty.class);
      return prop == null ? null : prop.getConv();
   }

   public XSComponent getReferer() {
      return (XSComponent)this.refererStack.peek();
   }

   private TypeUse checkRefererCustomization(XSSimpleType type) {
      XSComponent top = this.getReferer();
      if (top instanceof XSElementDecl) {
         XSElementDecl eref = (XSElementDecl)top;

         assert eref.getType() == type;

         BindInfo info = this.builder.getBindInfo(top);
         BIConversion conv = (BIConversion)info.get(BIConversion.class);
         if (conv != null) {
            conv.markAsAcknowledged();
            return conv.getTypeUse(type);
         }

         this.detectJavaTypeCustomization();
      } else if (top instanceof XSAttributeDecl) {
         XSAttributeDecl aref = (XSAttributeDecl)top;

         assert aref.getType() == type;

         this.detectJavaTypeCustomization();
      } else if (top instanceof XSComplexType) {
         XSComplexType tref = (XSComplexType)top;

         assert tref.getBaseType() == type || tref.getContentType() == type;

         this.detectJavaTypeCustomization();
      } else {
         assert top == type;
      }

      BIConversion conv = this.getRefererCustomization();
      if (conv != null) {
         conv.markAsAcknowledged();
         return conv.getTypeUse(type);
      } else {
         return null;
      }
   }

   private void detectJavaTypeCustomization() {
      BindInfo info = this.builder.getBindInfo(this.getReferer());
      BIConversion conv = (BIConversion)info.get(BIConversion.class);
      if (conv != null) {
         conv.markAsAcknowledged();
         this.getErrorReporter().error(conv.getLocation(), "SimpleTypeBuilder.UnnestedJavaTypeCustomization");
      }

   }

   TypeUse compose(XSSimpleType t) {
      TypeUse e = this.find(t);
      return e != null ? e : (TypeUse)t.apply(this.composer);
   }

   private TypeUse find(XSSimpleType type) {
      boolean noAutoEnum = false;
      BindInfo info = this.builder.getBindInfo(type);
      BIConversion conv = (BIConversion)info.get(BIConversion.class);
      if (conv != null) {
         conv.markAsAcknowledged();
         return conv.getTypeUse(type);
      } else {
         BIEnum en = (BIEnum)info.get(BIEnum.class);
         if (en != null) {
            en.markAsAcknowledged();
            if (en.isMapped()) {
               if (!canBeMappedToTypeSafeEnum(type)) {
                  this.getErrorReporter().error(en.getLocation(), "ConversionFinder.CannotBeTypeSafeEnum");
                  this.getErrorReporter().error(type.getLocator(), "ConversionFinder.CannotBeTypeSafeEnum.Location");
                  return null;
               }

               if (en.ref != null) {
                  if (!JJavaName.isFullyQualifiedClassName(en.ref)) {
                     ((ErrorReceiver)Ring.get(ErrorReceiver.class)).error(en.getLocation(), Messages.format("ClassSelector.IncorrectClassName", en.ref));
                     return null;
                  }

                  return new CClassRef(this.model, type, en, info.toCustomizationList());
               }

               return this.bindToTypeSafeEnum((XSRestrictionSimpleType)type, en.className, en.javadoc, en.members, this.getEnumMemberMode().getModeWithEnum(), en.getLocation());
            }

            noAutoEnum = true;
         }

         TypeUse r;
         String name;
         if (type.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            name = type.getName();
            if (name != null) {
               r = this.lookupBuiltin(name);
               if (r != null) {
                  return r;
               }
            }
         }

         if (type.getTargetNamespace().equals("http://ws-i.org/profiles/basic/1.1/xsd")) {
            name = type.getName();
            if (name != null && name.equals("swaRef")) {
               return CBuiltinLeafInfo.STRING.makeAdapted(SwaRefAdapterMarker.class, false);
            }
         }

         if (type.isRestriction() && !noAutoEnum) {
            XSRestrictionSimpleType rst = type.asRestriction();
            if (this.shouldBeMappedToTypeSafeEnumByDefault(rst)) {
               r = this.bindToTypeSafeEnum(rst, (String)null, (String)null, Collections.emptyMap(), this.getEnumMemberMode(), (Locator)null);
               if (r != null) {
                  return r;
               }
            }
         }

         return (CNonElement)this.getClassSelector()._bindToClass(type, (XSComponent)null, false);
      }
   }

   private boolean shouldBeMappedToTypeSafeEnumByDefault(XSRestrictionSimpleType type) {
      if (type.isLocal()) {
         return false;
      } else if (type.getRedefinedBy() != null) {
         return false;
      } else {
         List facets = type.getDeclaredFacets("enumeration");
         if (facets.isEmpty()) {
            return false;
         } else if (facets.size() > this.builder.getGlobalBinding().getDefaultEnumMemberSizeCap()) {
            if (reportedEnumMemberSizeWarnings == null) {
               reportedEnumMemberSizeWarnings = new HashSet();
            }

            if (!reportedEnumMemberSizeWarnings.contains(type)) {
               this.getErrorReporter().warning(type.getLocator(), "WARN_ENUM_MEMBER_SIZE_CAP", type.getName(), facets.size(), this.builder.getGlobalBinding().getDefaultEnumMemberSizeCap());
               reportedEnumMemberSizeWarnings.add(type);
            }

            return false;
         } else if (!canBeMappedToTypeSafeEnum(type)) {
            return false;
         } else {
            for(XSSimpleType t = type; t != null; t = ((XSSimpleType)t).getSimpleBaseType()) {
               if (((XSSimpleType)t).isGlobal() && this.builder.getGlobalBinding().canBeMappedToTypeSafeEnum((XSDeclaration)t)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static boolean canBeMappedToTypeSafeEnum(XSSimpleType type) {
      do {
         if ("http://www.w3.org/2001/XMLSchema".equals(type.getTargetNamespace())) {
            String localName = type.getName();
            if (localName != null) {
               if (localName.equals("anySimpleType")) {
                  return false;
               }

               if (localName.equals("ID") || localName.equals("IDREF")) {
                  return false;
               }

               if (builtinTypeSafeEnumCapableTypes.contains(localName)) {
                  return true;
               }
            }
         }

         type = type.getSimpleBaseType();
      } while(type != null);

      return false;
   }

   private TypeUse bindToTypeSafeEnum(XSRestrictionSimpleType type, String className, String javadoc, Map members, EnumMemberMode mode, Locator loc) {
      if (loc == null) {
         loc = type.getLocator();
      }

      if (className == null) {
         if (!type.isGlobal()) {
            this.getErrorReporter().error(loc, "ConversionFinder.NoEnumNameAvailable");
            return CBuiltinLeafInfo.STRING;
         }

         className = type.getName();
      }

      className = this.builder.deriveName(className, type);
      StringWriter out = new StringWriter();
      SchemaWriter sw = new SchemaWriter(new JavadocEscapeWriter(out));
      type.visit(sw);
      if (javadoc != null) {
         javadoc = javadoc + "\n\n";
      } else {
         javadoc = "";
      }

      javadoc = javadoc + Messages.format("ClassSelector.JavadocHeading", type.getName()) + "\n<p>\n<pre>\n" + out.getBuffer() + "</pre>";
      this.refererStack.push(type.getSimpleBaseType());
      TypeUse use = this.build(type.getSimpleBaseType());
      this.refererStack.pop();
      if (use.isCollection()) {
         return null;
      } else {
         CNonElement baseDt = use.getInfo();
         if (baseDt instanceof CClassInfo) {
            return null;
         } else {
            XSFacet[] errorRef = new XSFacet[1];
            List memberList = this.buildCEnumConstants(type, false, members, errorRef);
            if (memberList == null || this.checkMemberNameCollision(memberList) != null) {
               switch (mode) {
                  case SKIP:
                     return null;
                  case ERROR:
                     if (memberList == null) {
                        this.getErrorReporter().error(errorRef[0].getLocator(), "ERR_CANNOT_GENERATE_ENUM_NAME", errorRef[0].getValue());
                     } else {
                        CEnumConstant[] collision = this.checkMemberNameCollision(memberList);
                        this.getErrorReporter().error(collision[0].getLocator(), "ERR_ENUM_MEMBER_NAME_COLLISION", collision[0].getName());
                        this.getErrorReporter().error(collision[1].getLocator(), "ERR_ENUM_MEMBER_NAME_COLLISION_RELATED");
                     }

                     return null;
                  case GENERATE:
                     memberList = this.buildCEnumConstants(type, true, members, (XSFacet[])null);
               }
            }

            if (memberList.isEmpty()) {
               this.getErrorReporter().error(loc, "ConversionFinder.NoEnumFacet");
               return null;
            } else {
               Object scope;
               if (type.isGlobal()) {
                  scope = new CClassInfoParent.Package(this.getClassSelector().getPackage(type.getTargetNamespace()));
               } else {
                  scope = this.getClassSelector().getClassScope();
               }

               CEnumLeafInfo xducer = new CEnumLeafInfo(this.model, BGMBuilder.getName(type), (CClassInfoParent)scope, className, baseDt, memberList, type, this.builder.getBindInfo(type).toCustomizationList(), loc);
               xducer.javadoc = javadoc;
               BIConversion conv = new BIConversion.Static(type.getLocator(), xducer);
               conv.markAsAcknowledged();
               this.builder.getOrCreateBindInfo(type).addDecl(conv);
               return conv.getTypeUse(type);
            }
         }
      }
   }

   private List buildCEnumConstants(XSRestrictionSimpleType type, boolean needsToGenerateMemberName, Map members, XSFacet[] errorRef) {
      List memberList = new ArrayList();
      int idx = 1;
      Set enums = new HashSet();
      Iterator var8 = type.getDeclaredFacets("enumeration").iterator();

      while(true) {
         XSFacet facet;
         String name;
         String mdoc;
         do {
            if (!var8.hasNext()) {
               return memberList;
            }

            facet = (XSFacet)var8.next();
            name = null;
            mdoc = this.builder.getBindInfo(facet).getDocumentation();
         } while(!enums.add(facet.getValue().value));

         if (needsToGenerateMemberName) {
            name = "VALUE_" + idx++;
         } else {
            String facetValue = facet.getValue().value;
            BIEnumMember mem = (BIEnumMember)members.get(facetValue);
            if (mem == null) {
               mem = (BIEnumMember)this.builder.getBindInfo(facet).get(BIEnumMember.class);
            }

            if (mem != null) {
               name = mem.name;
               if (mdoc == null) {
                  mdoc = mem.javadoc;
               }
            }

            if (name == null) {
               StringBuilder sb = new StringBuilder();

               for(int i = 0; i < facetValue.length(); ++i) {
                  char ch = facetValue.charAt(i);
                  if (Character.isJavaIdentifierPart(ch)) {
                     sb.append(ch);
                  } else {
                     sb.append('_');
                  }
               }

               name = this.model.getNameConverter().toConstantName(sb.toString());
            }
         }

         if (!JJavaName.isJavaIdentifier(name)) {
            if (errorRef != null) {
               errorRef[0] = facet;
            }

            return null;
         }

         memberList.add(new CEnumConstant(name, mdoc, facet.getValue().value, facet, this.builder.getBindInfo(facet).toCustomizationList(), facet.getLocator()));
      }
   }

   private CEnumConstant[] checkMemberNameCollision(List memberList) {
      Map names = new HashMap();
      Iterator var3 = memberList.iterator();

      CEnumConstant c;
      CEnumConstant old;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         c = (CEnumConstant)var3.next();
         old = (CEnumConstant)names.put(c.getName(), c);
      } while(old == null);

      return new CEnumConstant[]{old, c};
   }

   private EnumMemberMode getEnumMemberMode() {
      return this.builder.getGlobalBinding().getEnumMemberMode();
   }

   private TypeUse lookupBuiltin(String typeLocalName) {
      if (!typeLocalName.equals("integer") && !typeLocalName.equals("long")) {
         if (typeLocalName.equals("boolean") && this.isRestrictedTo0And1()) {
            return CBuiltinLeafInfo.BOOLEAN_ZERO_OR_ONE;
         }

         if (typeLocalName.equals("base64Binary")) {
            return this.lookupBinaryTypeBinding();
         }

         if (typeLocalName.equals("anySimpleType")) {
            if (!(this.getReferer() instanceof XSAttributeDecl) && !(this.getReferer() instanceof XSSimpleType)) {
               return CBuiltinLeafInfo.ANYTYPE;
            }

            return CBuiltinLeafInfo.STRING;
         }
      } else {
         BigInteger xe = this.readFacet("maxExclusive", -1);
         BigInteger xi = this.readFacet("maxInclusive", 0);
         BigInteger max = this.min(xe, xi);
         if (max != null) {
            BigInteger ne = this.readFacet("minExclusive", 1);
            BigInteger ni = this.readFacet("minInclusive", 0);
            BigInteger min = this.max(ne, ni);
            if (min != null) {
               if (min.compareTo(INT_MIN) >= 0 && max.compareTo(INT_MAX) <= 0) {
                  typeLocalName = "int";
               } else if (min.compareTo(LONG_MIN) >= 0 && max.compareTo(LONG_MAX) <= 0) {
                  typeLocalName = "long";
               }
            }
         }
      }

      return (TypeUse)builtinConversions.get(typeLocalName);
   }

   private TypeUse lookupBinaryTypeBinding() {
      XSComponent referer = this.getReferer();
      String emt = referer.getForeignAttribute("http://www.w3.org/2005/05/xmlmime", "expectedContentTypes");
      if (emt != null) {
         this.acknowledgedXmimeContentTypes.add(referer);

         try {
            List types = MimeTypeRange.parseRanges(emt);
            MimeTypeRange mt = MimeTypeRange.merge(types);
            if (mt.majorType.equalsIgnoreCase("image")) {
               return CBuiltinLeafInfo.IMAGE.makeMimeTyped(mt.toMimeType());
            }

            if ((mt.majorType.equalsIgnoreCase("application") || mt.majorType.equalsIgnoreCase("text")) && this.isXml(mt.subType)) {
               return CBuiltinLeafInfo.XML_SOURCE.makeMimeTyped(mt.toMimeType());
            }

            if (mt.majorType.equalsIgnoreCase("text") && mt.subType.equalsIgnoreCase("plain")) {
               return CBuiltinLeafInfo.STRING.makeMimeTyped(mt.toMimeType());
            }

            return CBuiltinLeafInfo.DATA_HANDLER.makeMimeTyped(mt.toMimeType());
         } catch (ParseException var5) {
            this.getErrorReporter().error(referer.getLocator(), Messages.format("ERR_ILLEGAL_EXPECTED_MIME_TYPE", emt, var5.getMessage()));
         } catch (MimeTypeParseException var6) {
            this.getErrorReporter().error(referer.getLocator(), Messages.format("ERR_ILLEGAL_EXPECTED_MIME_TYPE", emt, var6.getMessage()));
         }
      }

      return CBuiltinLeafInfo.BASE64_BYTE_ARRAY;
   }

   public boolean isAcknowledgedXmimeContentTypes(XSComponent c) {
      return this.acknowledgedXmimeContentTypes.contains(c);
   }

   private boolean isXml(String subType) {
      return subType.equals("xml") || subType.endsWith("+xml");
   }

   private boolean isRestrictedTo0And1() {
      XSFacet pattern = this.initiatingType.getFacet("pattern");
      if (pattern != null) {
         String v = pattern.getValue().value;
         if (v.equals("0|1") || v.equals("1|0") || v.equals("\\d")) {
            return true;
         }
      }

      XSFacet enumf = this.initiatingType.getFacet("enumeration");
      if (enumf != null) {
         String v = enumf.getValue().value;
         if (v.equals("0") || v.equals("1")) {
            return true;
         }
      }

      return false;
   }

   private BigInteger readFacet(String facetName, int offset) {
      XSFacet me = this.initiatingType.getFacet(facetName);
      if (me == null) {
         return null;
      } else {
         BigInteger bi = DatatypeConverter.parseInteger(me.getValue().value);
         if (offset != 0) {
            bi = bi.add(BigInteger.valueOf((long)offset));
         }

         return bi;
      }
   }

   private BigInteger min(BigInteger a, BigInteger b) {
      if (a == null) {
         return b;
      } else {
         return b == null ? a : a.min(b);
      }
   }

   private BigInteger max(BigInteger a, BigInteger b) {
      if (a == null) {
         return b;
      } else {
         return b == null ? a : a.max(b);
      }
   }

   static {
      Set s = new HashSet();
      String[] typeNames = new String[]{"string", "boolean", "float", "decimal", "double", "anyURI"};
      s.addAll(Arrays.asList(typeNames));
      builtinTypeSafeEnumCapableTypes = Collections.unmodifiableSet(s);
      LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
      LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
      INT_MIN = BigInteger.valueOf(-2147483648L);
      INT_MAX = BigInteger.valueOf(2147483647L);
      Map m = builtinConversions;
      m.put("string", CBuiltinLeafInfo.STRING);
      m.put("anyURI", CBuiltinLeafInfo.STRING);
      m.put("boolean", CBuiltinLeafInfo.BOOLEAN);
      m.put("hexBinary", CBuiltinLeafInfo.HEXBIN_BYTE_ARRAY);
      m.put("float", CBuiltinLeafInfo.FLOAT);
      m.put("decimal", CBuiltinLeafInfo.BIG_DECIMAL);
      m.put("integer", CBuiltinLeafInfo.BIG_INTEGER);
      m.put("long", CBuiltinLeafInfo.LONG);
      m.put("unsignedInt", CBuiltinLeafInfo.LONG);
      m.put("int", CBuiltinLeafInfo.INT);
      m.put("unsignedShort", CBuiltinLeafInfo.INT);
      m.put("short", CBuiltinLeafInfo.SHORT);
      m.put("unsignedByte", CBuiltinLeafInfo.SHORT);
      m.put("byte", CBuiltinLeafInfo.BYTE);
      m.put("double", CBuiltinLeafInfo.DOUBLE);
      m.put("QName", CBuiltinLeafInfo.QNAME);
      m.put("NOTATION", CBuiltinLeafInfo.QNAME);
      m.put("dateTime", CBuiltinLeafInfo.CALENDAR);
      m.put("date", CBuiltinLeafInfo.CALENDAR);
      m.put("time", CBuiltinLeafInfo.CALENDAR);
      m.put("gYearMonth", CBuiltinLeafInfo.CALENDAR);
      m.put("gYear", CBuiltinLeafInfo.CALENDAR);
      m.put("gMonthDay", CBuiltinLeafInfo.CALENDAR);
      m.put("gDay", CBuiltinLeafInfo.CALENDAR);
      m.put("gMonth", CBuiltinLeafInfo.CALENDAR);
      m.put("duration", CBuiltinLeafInfo.DURATION);
      m.put("token", CBuiltinLeafInfo.TOKEN);
      m.put("normalizedString", CBuiltinLeafInfo.NORMALIZED_STRING);
      m.put("ID", CBuiltinLeafInfo.ID);
      m.put("IDREF", CBuiltinLeafInfo.IDREF);
   }
}
