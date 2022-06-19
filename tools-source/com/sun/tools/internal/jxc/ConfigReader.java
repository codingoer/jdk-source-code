package com.sun.tools.internal.jxc;

import com.sun.tools.internal.jxc.gen.config.Config;
import com.sun.tools.internal.jxc.gen.config.Schema;
import com.sun.tools.internal.xjc.SchemaCache;
import com.sun.tools.internal.xjc.api.Reference;
import com.sun.tools.internal.xjc.util.ForkContentHandler;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class ConfigReader {
   private final Set classesToBeIncluded = new HashSet();
   private final SchemaOutputResolver schemaOutputResolver;
   private final ProcessingEnvironment env;
   private static SchemaCache configSchema = new SchemaCache(Config.class.getResource("config.xsd"));

   public ConfigReader(ProcessingEnvironment env, Collection classes, File xmlFile, ErrorHandler errorHandler) throws SAXException, IOException {
      this.env = env;
      Config config = this.parseAndGetConfig(xmlFile, errorHandler, env.getOptions().containsKey("-disableXmlSecurity"));
      this.checkAllClasses(config, classes);
      String path = xmlFile.getAbsolutePath();
      String xmlPath = path.substring(0, path.lastIndexOf(File.separatorChar));
      this.schemaOutputResolver = this.createSchemaOutputResolver(config, xmlPath);
   }

   public Collection getClassesToBeIncluded() {
      return this.classesToBeIncluded;
   }

   private void checkAllClasses(Config config, Collection rootClasses) {
      List includeRegexList = config.getClasses().getIncludes();
      List excludeRegexList = config.getClasses().getExcludes();
      Iterator var5 = rootClasses.iterator();

      while(true) {
         while(true) {
            label26:
            while(var5.hasNext()) {
               TypeElement typeDecl = (TypeElement)var5.next();
               String qualifiedName = typeDecl.getQualifiedName().toString();
               Iterator var8 = excludeRegexList.iterator();

               Pattern pattern;
               boolean match;
               while(var8.hasNext()) {
                  pattern = (Pattern)var8.next();
                  match = this.checkPatternMatch(qualifiedName, pattern);
                  if (match) {
                     continue label26;
                  }
               }

               var8 = includeRegexList.iterator();

               while(var8.hasNext()) {
                  pattern = (Pattern)var8.next();
                  match = this.checkPatternMatch(qualifiedName, pattern);
                  if (match) {
                     this.classesToBeIncluded.add(new Reference(typeDecl, this.env));
                     break;
                  }
               }
            }

            return;
         }
      }
   }

   public SchemaOutputResolver getSchemaOutputResolver() {
      return this.schemaOutputResolver;
   }

   private SchemaOutputResolver createSchemaOutputResolver(Config config, String xmlpath) {
      File baseDir = new File(xmlpath, config.getBaseDir().getPath());
      SchemaOutputResolverImpl outResolver = new SchemaOutputResolverImpl(baseDir);
      Iterator var5 = config.getSchema().iterator();

      while(var5.hasNext()) {
         Schema schema = (Schema)var5.next();
         String namespace = schema.getNamespace();
         File location = schema.getLocation();
         outResolver.addSchemaInfo(namespace, location);
      }

      return outResolver;
   }

   private boolean checkPatternMatch(String qualifiedName, Pattern pattern) {
      Matcher matcher = pattern.matcher(qualifiedName);
      return matcher.matches();
   }

   private Config parseAndGetConfig(File xmlFile, ErrorHandler errorHandler, boolean disableSecureProcessing) throws SAXException, IOException {
      XMLReader reader;
      try {
         SAXParserFactory factory = XmlFactory.createParserFactory(disableSecureProcessing);
         reader = factory.newSAXParser().getXMLReader();
      } catch (ParserConfigurationException var8) {
         throw new Error(var8);
      }

      NGCCRuntimeEx runtime = new NGCCRuntimeEx(errorHandler);
      ValidatorHandler validator = configSchema.newValidator();
      validator.setErrorHandler(errorHandler);
      reader.setContentHandler(new ForkContentHandler(validator, runtime));
      reader.setErrorHandler(errorHandler);
      Config config = new Config(runtime);
      runtime.setRootHandler(config);
      reader.parse(new InputSource(xmlFile.toURL().toExternalForm()));
      runtime.reset();
      return config;
   }

   private static final class SchemaOutputResolverImpl extends SchemaOutputResolver {
      private final File baseDir;
      private final Map schemas = new HashMap();

      public Result createOutput(String namespaceUri, String suggestedFileName) {
         File loc;
         if (this.schemas.containsKey(namespaceUri)) {
            loc = (File)this.schemas.get(namespaceUri);
            if (loc == null) {
               return null;
            } else {
               loc.getParentFile().mkdirs();
               return new StreamResult(loc);
            }
         } else {
            loc = new File(this.baseDir, suggestedFileName);
            return new StreamResult(loc);
         }
      }

      public SchemaOutputResolverImpl(File baseDir) {
         assert baseDir != null;

         this.baseDir = baseDir;
      }

      public void addSchemaInfo(String namespaceUri, File location) {
         if (namespaceUri == null) {
            namespaceUri = "";
         }

         this.schemas.put(namespaceUri, location);
      }
   }
}
