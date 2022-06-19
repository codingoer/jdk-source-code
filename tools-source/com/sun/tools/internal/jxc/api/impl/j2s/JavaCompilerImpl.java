package com.sun.tools.internal.jxc.api.impl.j2s;

import com.sun.tools.internal.jxc.ap.InlineAnnotationReaderImpl;
import com.sun.tools.internal.jxc.model.nav.ApNavigator;
import com.sun.tools.internal.xjc.api.J2SJAXBModel;
import com.sun.tools.internal.xjc.api.JavaCompiler;
import com.sun.tools.internal.xjc.api.Reference;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.impl.ModelBuilder;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class JavaCompilerImpl implements JavaCompiler {
   public J2SJAXBModel bind(Collection rootClasses, Map additionalElementDecls, String defaultNamespaceRemap, ProcessingEnvironment env) {
      ModelBuilder builder = new ModelBuilder(InlineAnnotationReaderImpl.theInstance, new ApNavigator(env), Collections.emptyMap(), defaultNamespaceRemap);
      builder.setErrorHandler(new ErrorHandlerImpl(env.getMessager()));
      Iterator var6 = rootClasses.iterator();

      while(var6.hasNext()) {
         Reference ref = (Reference)var6.next();
         TypeMirror t = ref.type;
         XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)ref.annotations.getAnnotation(XmlJavaTypeAdapter.class);
         XmlList xl = (XmlList)ref.annotations.getAnnotation(XmlList.class);
         builder.getTypeInfo(new Ref(builder, t, xjta, xl));
      }

      TypeInfoSet r = builder.link();
      if (r == null) {
         return null;
      } else {
         if (additionalElementDecls == null) {
            additionalElementDecls = Collections.emptyMap();
         } else {
            Iterator var12 = additionalElementDecls.entrySet().iterator();

            while(var12.hasNext()) {
               Map.Entry e = (Map.Entry)var12.next();
               if (e.getKey() == null) {
                  throw new IllegalArgumentException("nulls in additionalElementDecls");
               }
            }
         }

         return new JAXBModelImpl(r, builder.reader, rootClasses, new HashMap(additionalElementDecls));
      }
   }

   private static final class ErrorHandlerImpl implements ErrorHandler {
      private final Messager messager;

      public ErrorHandlerImpl(Messager messager) {
         this.messager = messager;
      }

      public void error(IllegalAnnotationException e) {
         String error = e.toString();
         this.messager.printMessage(Kind.ERROR, error);
         System.err.println(error);
      }
   }
}
