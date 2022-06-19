package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassContainer;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CEnumLeafInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeRef;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.util.CodeModelClassFactory;
import java.util.Collection;

public interface Outline {
   Model getModel();

   JCodeModel getCodeModel();

   FieldOutline getField(CPropertyInfo var1);

   PackageOutline getPackageContext(JPackage var1);

   Collection getClasses();

   ClassOutline getClazz(CClassInfo var1);

   ElementOutline getElement(CElementInfo var1);

   EnumOutline getEnum(CEnumLeafInfo var1);

   Collection getEnums();

   Iterable getAllPackageContexts();

   CodeModelClassFactory getClassFactory();

   ErrorReceiver getErrorReceiver();

   JClassContainer getContainer(CClassInfoParent var1, Aspect var2);

   JType resolve(CTypeRef var1, Aspect var2);

   JClass addRuntime(Class var1);
}
