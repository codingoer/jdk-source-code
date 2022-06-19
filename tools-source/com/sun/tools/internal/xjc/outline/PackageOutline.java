package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.xjc.generator.bean.ObjectFactoryGenerator;
import java.util.Set;
import javax.xml.bind.annotation.XmlNsForm;

public interface PackageOutline {
   JPackage _package();

   JDefinedClass objectFactory();

   ObjectFactoryGenerator objectFactoryGenerator();

   Set getClasses();

   String getMostUsedNamespaceURI();

   XmlNsForm getElementFormDefault();

   XmlNsForm getAttributeFormDefault();
}
