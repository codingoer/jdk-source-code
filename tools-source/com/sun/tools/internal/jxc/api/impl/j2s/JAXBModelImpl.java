package com.sun.tools.internal.jxc.api.impl.j2s;

import com.sun.tools.internal.xjc.api.ErrorListener;
import com.sun.tools.internal.xjc.api.J2SJAXBModel;
import com.sun.tools.internal.xjc.api.Reference;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
import com.sun.xml.internal.txw2.output.ResultFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.lang.model.type.TypeMirror;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

final class JAXBModelImpl implements J2SJAXBModel {
   private final Map additionalElementDecls;
   private final List classList = new ArrayList();
   private final TypeInfoSet types;
   private final AnnotationReader reader;
   private XmlSchemaGenerator xsdgen;
   private final Map refMap = new HashMap();

   public JAXBModelImpl(TypeInfoSet types, AnnotationReader reader, Collection rootClasses, Map additionalElementDecls) {
      this.types = types;
      this.reader = reader;
      this.additionalElementDecls = additionalElementDecls;
      Navigator navigator = types.getNavigator();
      Iterator itr = types.beans().values().iterator();

      while(itr.hasNext()) {
         ClassInfo i = (ClassInfo)itr.next();
         this.classList.add(i.getName());
      }

      itr = types.arrays().values().iterator();

      while(itr.hasNext()) {
         ArrayInfo a = (ArrayInfo)itr.next();
         String javaName = navigator.getTypeName(a.getType());
         this.classList.add(javaName);
      }

      itr = types.enums().values().iterator();

      while(itr.hasNext()) {
         EnumLeafInfo l = (EnumLeafInfo)itr.next();
         QName tn = l.getTypeName();
         if (tn != null) {
            String javaName = navigator.getTypeName(l.getType());
            this.classList.add(javaName);
         }
      }

      itr = rootClasses.iterator();

      while(itr.hasNext()) {
         Reference ref = (Reference)itr.next();
         this.refMap.put(ref, this.getXmlType(ref));
      }

      itr = additionalElementDecls.entrySet().iterator();

      while(true) {
         while(true) {
            Map.Entry entry;
            do {
               if (!itr.hasNext()) {
                  return;
               }

               entry = (Map.Entry)itr.next();
            } while(entry.getValue() == null);

            NonElement xt = this.getXmlType((Reference)entry.getValue());

            assert xt != null;

            this.refMap.put(entry.getValue(), xt);
            if (xt instanceof ClassInfo) {
               ClassInfo xct = (ClassInfo)xt;
               Element elem = xct.asElement();
               if (elem != null && elem.getElementName().equals(entry.getKey())) {
                  itr.remove();
                  continue;
               }
            }

            ElementInfo ei = types.getElementInfo((Object)null, (QName)entry.getKey());
            if (ei != null && ei.getContentType() == xt) {
               itr.remove();
            }
         }
      }
   }

   public List getClassList() {
      return this.classList;
   }

   public QName getXmlTypeName(Reference javaType) {
      NonElement ti = (NonElement)this.refMap.get(javaType);
      return ti != null ? ti.getTypeName() : null;
   }

   private NonElement getXmlType(Reference r) {
      if (r == null) {
         throw new IllegalArgumentException();
      } else {
         XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)r.annotations.getAnnotation(XmlJavaTypeAdapter.class);
         XmlList xl = (XmlList)r.annotations.getAnnotation(XmlList.class);
         Ref ref = new Ref(this.reader, this.types.getNavigator(), r.type, xjta, xl);
         return this.types.getTypeInfo(ref);
      }
   }

   public void generateSchema(SchemaOutputResolver outputResolver, ErrorListener errorListener) throws IOException {
      this.getSchemaGenerator().write(outputResolver, errorListener);
   }

   public void generateEpisodeFile(Result output) {
      this.getSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(output));
   }

   private synchronized XmlSchemaGenerator getSchemaGenerator() {
      if (this.xsdgen == null) {
         this.xsdgen = new XmlSchemaGenerator(this.types.getNavigator(), this.types);
         Iterator var1 = this.additionalElementDecls.entrySet().iterator();

         while(true) {
            while(var1.hasNext()) {
               Map.Entry e = (Map.Entry)var1.next();
               Reference value = (Reference)e.getValue();
               if (value != null) {
                  NonElement typeInfo = (NonElement)this.refMap.get(value);
                  if (typeInfo == null) {
                     throw new IllegalArgumentException(e.getValue() + " was not specified to JavaCompiler.bind");
                  }

                  TypeMirror type = value.type;
                  this.xsdgen.add((QName)e.getKey(), type == null || !type.getKind().isPrimitive(), typeInfo);
               } else {
                  this.xsdgen.add((QName)e.getKey(), false, (NonElement)null);
               }
            }

            return this.xsdgen;
         }
      } else {
         return this.xsdgen;
      }
   }
}
