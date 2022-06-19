package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunctionWithParam;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;

public interface XSTerm extends XSComponent {
   void visit(XSTermVisitor var1);

   Object apply(XSTermFunction var1);

   Object apply(XSTermFunctionWithParam var1, Object var2);

   boolean isWildcard();

   boolean isModelGroupDecl();

   boolean isModelGroup();

   boolean isElementDecl();

   XSWildcard asWildcard();

   XSModelGroupDecl asModelGroupDecl();

   XSModelGroup asModelGroup();

   XSElementDecl asElementDecl();
}
