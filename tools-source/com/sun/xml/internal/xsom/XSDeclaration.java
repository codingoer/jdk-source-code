package com.sun.xml.internal.xsom;

public interface XSDeclaration extends XSComponent {
   String getTargetNamespace();

   String getName();

   /** @deprecated */
   boolean isAnonymous();

   boolean isGlobal();

   boolean isLocal();
}
