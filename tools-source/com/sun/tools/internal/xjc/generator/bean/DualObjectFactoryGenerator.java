package com.sun.tools.internal.xjc.generator.bean;

import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.Model;

public final class DualObjectFactoryGenerator extends ObjectFactoryGenerator {
   public final ObjectFactoryGenerator publicOFG;
   public final ObjectFactoryGenerator privateOFG;

   DualObjectFactoryGenerator(BeanGenerator outline, Model model, JPackage targetPackage) {
      this.publicOFG = new PublicObjectFactoryGenerator(outline, model, targetPackage);
      this.privateOFG = new PrivateObjectFactoryGenerator(outline, model, targetPackage);
      this.publicOFG.getObjectFactory().field(28, (Class)Void.class, "_useJAXBProperties", JExpr._null());
   }

   void populate(CElementInfo ei) {
      this.publicOFG.populate(ei);
      this.privateOFG.populate(ei);
   }

   void populate(ClassOutlineImpl cc) {
      this.publicOFG.populate(cc);
      this.privateOFG.populate(cc);
   }

   public JDefinedClass getObjectFactory() {
      return this.privateOFG.getObjectFactory();
   }
}
