package com.sun.codemodel.internal;

import java.util.Iterator;

public interface JClassContainer {
   boolean isClass();

   boolean isPackage();

   JDefinedClass _class(int var1, String var2) throws JClassAlreadyExistsException;

   JDefinedClass _class(String var1) throws JClassAlreadyExistsException;

   JDefinedClass _interface(int var1, String var2) throws JClassAlreadyExistsException;

   JDefinedClass _interface(String var1) throws JClassAlreadyExistsException;

   /** @deprecated */
   JDefinedClass _class(int var1, String var2, boolean var3) throws JClassAlreadyExistsException;

   JDefinedClass _class(int var1, String var2, ClassType var3) throws JClassAlreadyExistsException;

   Iterator classes();

   JClassContainer parentContainer();

   JPackage getPackage();

   JCodeModel owner();

   JDefinedClass _annotationTypeDeclaration(String var1) throws JClassAlreadyExistsException;

   JDefinedClass _enum(String var1) throws JClassAlreadyExistsException;
}
