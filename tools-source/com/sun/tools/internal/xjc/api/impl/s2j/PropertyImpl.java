package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.api.Mapping;
import com.sun.tools.internal.xjc.api.Property;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.outline.FieldOutline;
import javax.xml.namespace.QName;

public final class PropertyImpl implements Property {
   protected final FieldOutline fr;
   protected final QName elementName;
   protected final Mapping parent;
   protected final JCodeModel codeModel;

   PropertyImpl(Mapping parent, FieldOutline fr, QName elementName) {
      this.parent = parent;
      this.fr = fr;
      this.elementName = elementName;
      this.codeModel = fr.getRawType().owner();
   }

   public final String name() {
      return this.fr.getPropertyInfo().getName(false);
   }

   public final QName rawName() {
      if (this.fr instanceof ElementAdapter) {
         CElementInfo eInfo = ((ElementAdapter)this.fr).ei;
         if (eInfo != null && eInfo.getProperty() != null) {
            return ((CTypeRef)eInfo.getProperty().getTypes().get(0)).getTypeName();
         }
      }

      return null;
   }

   public final QName elementName() {
      return this.elementName;
   }

   public final JType type() {
      return this.fr.getRawType();
   }
}
