package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.reader.xmlschema.BindingComponent;
import com.sun.xml.internal.xsom.XSComplexType;
import java.util.HashMap;
import java.util.Map;

public final class ComplexTypeFieldBuilder extends BindingComponent {
   private final CTBuilder[] complexTypeBuilders = new CTBuilder[]{new MultiWildcardComplexTypeBuilder(), new MixedExtendedComplexTypeBuilder(), new MixedComplexTypeBuilder(), new FreshComplexTypeBuilder(), new ExtendedComplexTypeBuilder(), new RestrictedComplexTypeBuilder(), new STDerivedComplexTypeBuilder()};
   private final Map complexTypeBindingModes = new HashMap();

   public void build(XSComplexType type) {
      CTBuilder[] var2 = this.complexTypeBuilders;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CTBuilder ctb = var2[var4];
         if (ctb.isApplicable(type)) {
            ctb.build(type);
            return;
         }
      }

      assert false;

   }

   public void recordBindingMode(XSComplexType type, ComplexTypeBindingMode flag) {
      Object o = this.complexTypeBindingModes.put(type, flag);

      assert o == null;

   }

   protected ComplexTypeBindingMode getBindingMode(XSComplexType type) {
      ComplexTypeBindingMode r = (ComplexTypeBindingMode)this.complexTypeBindingModes.get(type);

      assert r != null;

      return r;
   }
}
