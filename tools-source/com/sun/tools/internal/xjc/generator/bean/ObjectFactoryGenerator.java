package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.tools.internal.xjc.model.CElementInfo;

public abstract class ObjectFactoryGenerator {
   abstract void populate(CElementInfo var1);

   abstract void populate(ClassOutlineImpl var1);

   public abstract JDefinedClass getObjectFactory();
}
