package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.visitor.XSWildcardFunction;
import com.sun.xml.internal.xsom.visitor.XSWildcardVisitor;
import java.util.Collection;
import java.util.Iterator;

public interface XSWildcard extends XSComponent, XSTerm {
   int LAX = 1;
   int STRTICT = 2;
   int SKIP = 3;

   int getMode();

   boolean acceptsNamespace(String var1);

   void visit(XSWildcardVisitor var1);

   Object apply(XSWildcardFunction var1);

   public interface Union extends XSWildcard {
      Iterator iterateNamespaces();

      Collection getNamespaces();
   }

   public interface Other extends XSWildcard {
      String getOtherNamespace();
   }

   public interface Any extends XSWildcard {
   }
}
