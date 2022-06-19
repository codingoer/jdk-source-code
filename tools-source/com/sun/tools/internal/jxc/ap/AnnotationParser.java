package com.sun.tools.internal.jxc.ap;

import com.sun.tools.internal.jxc.ConfigReader;
import com.sun.tools.internal.jxc.api.JXC;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.api.J2SJAXBModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.xml.bind.SchemaOutputResolver;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

@SupportedAnnotationTypes({"javax.xml.bind.annotation.*"})
@SupportedOptions({"jaxb.config"})
public final class AnnotationParser extends AbstractProcessor {
   private ErrorReceiver errorListener;

   public void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      this.processingEnv = processingEnv;
      this.errorListener = new ErrorReceiverImpl(processingEnv.getMessager(), processingEnv.getOptions().containsKey(Const.DEBUG_OPTION.getValue()));
   }

   public boolean process(Set annotations, RoundEnvironment roundEnv) {
      if (this.processingEnv.getOptions().containsKey(Const.CONFIG_FILE_OPTION.getValue())) {
         String value = (String)this.processingEnv.getOptions().get(Const.CONFIG_FILE_OPTION.getValue());
         StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
         if (!st.hasMoreTokens()) {
            this.errorListener.error((Locator)null, (String)Messages.OPERAND_MISSING.format(Const.CONFIG_FILE_OPTION.getValue()));
            return true;
         }

         while(st.hasMoreTokens()) {
            File configFile = new File(st.nextToken());
            if (!configFile.exists()) {
               this.errorListener.error((Locator)null, (String)Messages.NON_EXISTENT_FILE.format());
            } else {
               try {
                  Collection rootElements = new ArrayList();
                  this.filterClass(rootElements, roundEnv.getRootElements());
                  ConfigReader configReader = new ConfigReader(this.processingEnv, rootElements, configFile, this.errorListener);
                  Collection classesToBeIncluded = configReader.getClassesToBeIncluded();
                  J2SJAXBModel model = JXC.createJavaCompiler().bind(classesToBeIncluded, Collections.emptyMap(), (String)null, this.processingEnv);
                  SchemaOutputResolver schemaOutputResolver = configReader.getSchemaOutputResolver();
                  model.generateSchema(schemaOutputResolver, this.errorListener);
               } catch (IOException var11) {
                  this.errorListener.error((String)var11.getMessage(), (Exception)var11);
               } catch (SAXException var12) {
               }
            }
         }
      }

      return true;
   }

   private void filterClass(Collection rootElements, Collection elements) {
      Iterator var3 = elements.iterator();

      while(true) {
         Element element;
         do {
            if (!var3.hasNext()) {
               return;
            }

            element = (Element)var3.next();
         } while(!element.getKind().equals(ElementKind.CLASS) && !element.getKind().equals(ElementKind.INTERFACE) && !element.getKind().equals(ElementKind.ENUM));

         rootElements.add((TypeElement)element);
         this.filterClass(rootElements, ElementFilter.typesIn(element.getEnclosedElements()));
      }
   }

   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest().compareTo(SourceVersion.RELEASE_6) > 0 ? SourceVersion.valueOf("RELEASE_7") : SourceVersion.RELEASE_6;
   }
}
