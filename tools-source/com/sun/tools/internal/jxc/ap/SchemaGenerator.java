package com.sun.tools.internal.jxc.ap;

import com.sun.tools.internal.jxc.api.JXC;
import com.sun.tools.internal.xjc.api.J2SJAXBModel;
import com.sun.tools.internal.xjc.api.Reference;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

@SupportedAnnotationTypes({"*"})
public class SchemaGenerator extends AbstractProcessor {
   private final Map schemaLocations = new HashMap();
   private File episodeFile;

   public SchemaGenerator() {
   }

   public SchemaGenerator(Map m) {
      this.schemaLocations.putAll(m);
   }

   public void setEpisodeFile(File episodeFile) {
      this.episodeFile = episodeFile;
   }

   public boolean process(Set annotations, RoundEnvironment roundEnv) {
      ErrorReceiverImpl errorListener = new ErrorReceiverImpl(this.processingEnv);
      List classes = new ArrayList();
      this.filterClass(classes, roundEnv.getRootElements());
      J2SJAXBModel model = JXC.createJavaCompiler().bind(classes, Collections.emptyMap(), (String)null, this.processingEnv);
      if (model == null) {
         return false;
      } else {
         try {
            model.generateSchema(new SchemaOutputResolver() {
               public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                  File file;
                  Object out;
                  if (SchemaGenerator.this.schemaLocations.containsKey(namespaceUri)) {
                     file = (File)SchemaGenerator.this.schemaLocations.get(namespaceUri);
                     if (file == null) {
                        return null;
                     }

                     out = new FileOutputStream(file);
                  } else {
                     file = new File(suggestedFileName);
                     out = SchemaGenerator.this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", suggestedFileName, new Element[0]).openOutputStream();
                     file = file.getAbsoluteFile();
                  }

                  StreamResult ss = new StreamResult((OutputStream)out);
                  SchemaGenerator.this.processingEnv.getMessager().printMessage(Kind.NOTE, "Writing " + file);
                  ss.setSystemId(file.toURL().toExternalForm());
                  return ss;
               }
            }, errorListener);
            if (this.episodeFile != null) {
               this.processingEnv.getMessager().printMessage(Kind.NOTE, "Writing " + this.episodeFile);
               model.generateEpisodeFile(new StreamResult(this.episodeFile));
            }
         } catch (IOException var7) {
            errorListener.error(var7.getMessage(), var7);
         }

         return false;
      }
   }

   private void filterClass(List classes, Collection elements) {
      Iterator var3 = elements.iterator();

      while(true) {
         Element element;
         do {
            if (!var3.hasNext()) {
               return;
            }

            element = (Element)var3.next();
         } while(!element.getKind().equals(ElementKind.CLASS) && !element.getKind().equals(ElementKind.ENUM));

         classes.add(new Reference((TypeElement)element, this.processingEnv));
         this.filterClass(classes, ElementFilter.typesIn(element.getEnclosedElements()));
      }
   }

   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest().compareTo(SourceVersion.RELEASE_6) > 0 ? SourceVersion.valueOf("RELEASE_7") : SourceVersion.RELEASE_6;
   }
}
