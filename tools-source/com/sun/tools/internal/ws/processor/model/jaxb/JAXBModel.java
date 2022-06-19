package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.xjc.api.J2SJAXBModel;
import com.sun.tools.internal.xjc.api.Mapping;
import com.sun.tools.internal.xjc.api.S2JJAXBModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public class JAXBModel {
   private List mappings;
   private final Map byQName = new HashMap();
   private final Map byClassName = new HashMap();
   private com.sun.tools.internal.xjc.api.JAXBModel rawJAXBModel;
   private Set generatedClassNames;

   public com.sun.tools.internal.xjc.api.JAXBModel getRawJAXBModel() {
      return this.rawJAXBModel;
   }

   public S2JJAXBModel getS2JJAXBModel() {
      return this.rawJAXBModel instanceof S2JJAXBModel ? (S2JJAXBModel)this.rawJAXBModel : null;
   }

   public J2SJAXBModel getJ2SJAXBModel() {
      return this.rawJAXBModel instanceof J2SJAXBModel ? (J2SJAXBModel)this.rawJAXBModel : null;
   }

   public JAXBModel() {
   }

   public JAXBModel(com.sun.tools.internal.xjc.api.JAXBModel rawModel) {
      this.rawJAXBModel = rawModel;
      if (rawModel instanceof S2JJAXBModel) {
         S2JJAXBModel model = (S2JJAXBModel)rawModel;
         List ms = new ArrayList(model.getMappings().size());
         Iterator var4 = model.getMappings().iterator();

         while(var4.hasNext()) {
            Mapping m = (Mapping)var4.next();
            ms.add(new JAXBMapping(m));
         }

         this.setMappings(ms);
      }

   }

   public List getMappings() {
      return this.mappings;
   }

   public void setMappings(List mappings) {
      this.mappings = mappings;
      this.byQName.clear();
      this.byClassName.clear();
      Iterator var2 = mappings.iterator();

      while(var2.hasNext()) {
         JAXBMapping m = (JAXBMapping)var2.next();
         this.byQName.put(m.getElementName(), m);
         this.byClassName.put(m.getType().getName(), m);
      }

   }

   public JAXBMapping get(QName elementName) {
      return (JAXBMapping)this.byQName.get(elementName);
   }

   public JAXBMapping get(String className) {
      return (JAXBMapping)this.byClassName.get(className);
   }

   public Set getGeneratedClassNames() {
      return this.generatedClassNames;
   }

   public void setGeneratedClassNames(Set generatedClassNames) {
      this.generatedClassNames = generatedClassNames;
   }
}
