package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.Multiplicity;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.rngom.digested.DAttributePattern;
import com.sun.xml.internal.rngom.digested.DElementPattern;
import com.sun.xml.internal.rngom.digested.DOneOrMorePattern;
import com.sun.xml.internal.rngom.digested.DPattern;
import com.sun.xml.internal.rngom.digested.DPatternWalker;
import com.sun.xml.internal.rngom.digested.DZeroOrMorePattern;
import com.sun.xml.internal.xsom.XmlString;
import java.util.HashSet;
import java.util.Set;

public final class RawTypeSetBuilder extends DPatternWalker {
   private Multiplicity mul;
   private final Set refs = new HashSet();
   private final RELAXNGCompiler compiler;

   public static RawTypeSet build(RELAXNGCompiler compiler, DPattern contentModel, Multiplicity mul) {
      RawTypeSetBuilder builder = new RawTypeSetBuilder(compiler, mul);
      contentModel.accept(builder);
      return builder.create();
   }

   public RawTypeSetBuilder(RELAXNGCompiler compiler, Multiplicity mul) {
      this.mul = mul;
      this.compiler = compiler;
   }

   private RawTypeSet create() {
      return new RawTypeSet(this.refs, this.mul);
   }

   public Void onAttribute(DAttributePattern p) {
      return null;
   }

   public Void onElement(DElementPattern p) {
      CTypeInfo[] tis = (CTypeInfo[])this.compiler.classes.get(p);
      if (tis != null) {
         CTypeInfo[] var3 = tis;
         int var4 = tis.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CTypeInfo ti = var3[var5];
            this.refs.add(new CClassInfoRef((CClassInfo)ti));
         }
      } else {
         assert false;
      }

      return null;
   }

   public Void onZeroOrMore(DZeroOrMorePattern p) {
      this.mul = this.mul.makeRepeated();
      return super.onZeroOrMore(p);
   }

   public Void onOneOrMore(DOneOrMorePattern p) {
      this.mul = this.mul.makeRepeated();
      return super.onOneOrMore(p);
   }

   private static final class CClassInfoRef extends RawTypeSet.Ref {
      private final CClassInfo ci;

      CClassInfoRef(CClassInfo ci) {
         this.ci = ci;

         assert ci.isElement();

      }

      protected ID id() {
         return ID.NONE;
      }

      protected boolean isListOfValues() {
         return false;
      }

      protected RawTypeSet.Mode canBeType(RawTypeSet parent) {
         return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
      }

      protected void toElementRef(CReferencePropertyInfo prop) {
         prop.getElements().add(this.ci);
      }

      protected CTypeRef toTypeRef(CElementPropertyInfo ep) {
         return new CTypeRef(this.ci, this.ci.getElementName(), this.ci.getTypeName(), false, (XmlString)null);
      }
   }
}
