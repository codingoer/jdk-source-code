package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.model.nav.EagerNClass;
import com.sun.tools.internal.xjc.model.nav.NClass;
import com.sun.tools.internal.xjc.model.nav.NavigatorImpl;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

public final class CAdapter extends Adapter {
   private JClass adapterClass1;
   private Class adapterClass2;

   public CAdapter(Class adapter, boolean copy) {
      super(getRef(adapter, copy), NavigatorImpl.theInstance);
      this.adapterClass1 = null;
      this.adapterClass2 = adapter;
   }

   static NClass getRef(final Class adapter, boolean copy) {
      return (NClass)(copy ? new EagerNClass(adapter) {
         public JClass toType(Outline o, Aspect aspect) {
            return o.addRuntime(adapter);
         }

         public String fullName() {
            throw new UnsupportedOperationException();
         }
      } : NavigatorImpl.theInstance.ref(adapter));
   }

   public CAdapter(JClass adapter) {
      super(NavigatorImpl.theInstance.ref(adapter), NavigatorImpl.theInstance);
      this.adapterClass1 = adapter;
      this.adapterClass2 = null;
   }

   public JClass getAdapterClass(Outline o) {
      if (this.adapterClass1 == null) {
         this.adapterClass1 = o.getCodeModel().ref(this.adapterClass2);
      }

      return ((NClass)this.adapterType).toType(o, Aspect.EXPOSED);
   }

   public boolean isWhitespaceAdapter() {
      return this.adapterClass2 == CollapsedStringAdapter.class || this.adapterClass2 == NormalizedStringAdapter.class;
   }

   public Class getAdapterIfKnown() {
      return this.adapterClass2;
   }
}
