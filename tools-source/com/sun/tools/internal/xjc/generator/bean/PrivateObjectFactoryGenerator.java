package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.fmt.JPropertyFile;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.runtime.JAXBContextFactory;

final class PrivateObjectFactoryGenerator extends ObjectFactoryGeneratorImpl {
   public PrivateObjectFactoryGenerator(BeanGenerator outline, Model model, JPackage targetPackage) {
      super(outline, model, targetPackage.subPackage("impl"));
      JPackage implPkg = targetPackage.subPackage("impl");
      JClass factory = outline.generateStaticClass(JAXBContextFactory.class, implPkg);
      JPropertyFile jaxbProperties = new JPropertyFile("jaxb.properties");
      targetPackage.addResourceFile(jaxbProperties);
      jaxbProperties.add("javax.xml.bind.context.factory", factory.fullName());
   }

   void populate(CElementInfo ei) {
      this.populate(ei, Aspect.IMPLEMENTATION, Aspect.IMPLEMENTATION);
   }

   void populate(ClassOutlineImpl cc) {
      this.populate(cc, cc.implRef);
   }
}
