package com.sun.tools.internal.ws.wscompile;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.istack.internal.tools.ParallelWorldClassLoader;
import com.sun.tools.internal.ws.ToolVersion;
import com.sun.tools.internal.ws.processor.modeler.annotation.WebServiceAp;
import com.sun.tools.internal.ws.processor.modeler.wsdl.ConsoleErrorReporter;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.tools.internal.xjc.util.NullStream;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.output.StreamSerializer;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ExternalMetadataReader;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Holder;
import org.xml.sax.SAXParseException;

public class WsgenTool {
   private final PrintStream out;
   private final WsgenOptions options;
   private final Container container;

   public WsgenTool(OutputStream out, Container container) {
      this.options = new WsgenOptions();
      this.out = out instanceof PrintStream ? (PrintStream)out : new PrintStream(out);
      this.container = container;
   }

   public WsgenTool(OutputStream out) {
      this(out, (Container)null);
   }

   public boolean run(String[] args) {
      Listener listener = new Listener();
      String[] var3 = args;
      int var4 = args.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String arg = var3[var5];
         if (arg.equals("-version")) {
            listener.message(WscompileMessages.WSGEN_VERSION(ToolVersion.VERSION.MAJOR_VERSION));
            return true;
         }

         if (arg.equals("-fullversion")) {
            listener.message(WscompileMessages.WSGEN_FULLVERSION(ToolVersion.VERSION.toString()));
            return true;
         }
      }

      boolean var17;
      try {
         this.options.parseArguments(args);
         this.options.validate();
         if (this.buildModel(this.options.endpoint.getName(), listener)) {
            return true;
         }

         boolean var16 = false;
         return var16;
      } catch (Options.WeAreDone var12) {
         this.usage(var12.getOptions());
         return true;
      } catch (BadCommandLineException var13) {
         if (var13.getMessage() != null) {
            System.out.println(var13.getMessage());
            System.out.println();
         }

         this.usage(var13.getOptions());
         var17 = false;
      } catch (AbortException var14) {
         return true;
      } finally {
         if (!this.options.keep) {
            this.options.removeGeneratedFiles();
         }

      }

      return var17;
   }

   private static boolean useBootClasspath(Class clazz) {
      try {
         ParallelWorldClassLoader.toJarUrl(clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class"));
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   public boolean buildModel(String endpoint, Listener listener) throws BadCommandLineException {
      final ErrorReceiverFilter errReceiver = new ErrorReceiverFilter(listener);
      boolean bootCP = useBootClasspath(EndpointReference.class) || useBootClasspath(XmlSeeAlso.class);
      List args = new ArrayList(6 + (bootCP ? 1 : 0) + (this.options.nocompile ? 1 : 0) + (this.options.encoding != null ? 2 : 0));
      args.add("-d");
      args.add(this.options.destDir.getAbsolutePath());
      args.add("-classpath");
      args.add(this.options.classpath);
      args.add("-s");
      args.add(this.options.sourceDir.getAbsolutePath());
      if (this.options.nocompile) {
         args.add("-proc:only");
      }

      if (this.options.encoding != null) {
         args.add("-encoding");
         args.add(this.options.encoding);
      }

      if (bootCP) {
         args.add("-Xbootclasspath/p:" + JavaCompilerHelper.getJarFile(EndpointReference.class) + File.pathSeparator + JavaCompilerHelper.getJarFile(XmlSeeAlso.class));
      }

      if (this.options.javacOptions != null) {
         args.addAll(this.options.getJavacOptions(args, listener));
      }

      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector diagnostics = new DiagnosticCollector();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, (Locale)null, (Charset)null);
      JavaCompiler.CompilationTask task = compiler.getTask((Writer)null, fileManager, diagnostics, args, Collections.singleton(endpoint.replaceAll("\\$", ".")), (Iterable)null);
      task.setProcessors(Collections.singleton(new WebServiceAp(this.options, this.out)));
      boolean result = task.call();
      if (!result) {
         this.out.println(WscompileMessages.WSCOMPILE_ERROR(WscompileMessages.WSCOMPILE_COMPILATION_FAILED()));
         return false;
      } else {
         if (this.options.genWsdl) {
            DatabindingConfig config = new DatabindingConfig();
            List externalMetadataFileNames = this.options.externalMetadataFiles;
            boolean disableXmlSecurity = this.options.disableXmlSecurity;
            if (externalMetadataFileNames != null && externalMetadataFileNames.size() > 0) {
               config.setMetadataReader(new ExternalMetadataReader(this.getExternalFiles(externalMetadataFileNames), (Collection)null, (ClassLoader)null, true, disableXmlSecurity));
            }

            String tmpPath = this.options.destDir.getAbsolutePath() + File.pathSeparator + this.options.classpath;
            ClassLoader classLoader = new URLClassLoader(Options.pathToURLs(tmpPath), this.getClass().getClassLoader());

            Class endpointClass;
            try {
               endpointClass = classLoader.loadClass(endpoint);
            } catch (ClassNotFoundException var24) {
               throw new BadCommandLineException(WscompileMessages.WSGEN_CLASS_NOT_FOUND(endpoint));
            }

            BindingID bindingID = this.options.getBindingID(this.options.protocol);
            if (!this.options.protocolSet) {
               bindingID = BindingID.parse(endpointClass);
            }

            WebServiceFeatureList wsfeatures = new WebServiceFeatureList(endpointClass);
            if (this.options.portName != null) {
               config.getMappingInfo().setPortName(this.options.portName);
            }

            DatabindingFactory fac = DatabindingFactory.newInstance();
            config.setEndpointClass(endpointClass);
            config.getMappingInfo().setServiceName(this.options.serviceName);
            config.setFeatures(wsfeatures.toArray());
            config.setClassLoader(classLoader);
            config.getMappingInfo().setBindingID(bindingID);
            DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
            final File[] wsdlFileName = new File[1];
            final Map schemaFiles = new HashMap();
            WSDLGenInfo wsdlGenInfo = new WSDLGenInfo();
            wsdlGenInfo.setSecureXmlProcessingDisabled(disableXmlSecurity);
            wsdlGenInfo.setWsdlResolver(new WSDLResolver() {
               private File toFile(String suggestedFilename) {
                  return new File(WsgenTool.this.options.nonclassDestDir, suggestedFilename);
               }

               private Result toResult(File file) {
                  try {
                     Result result = new StreamResult(new FileOutputStream(file));
                     result.setSystemId(file.getPath().replace('\\', '/'));
                     return result;
                  } catch (FileNotFoundException var4) {
                     errReceiver.error(var4);
                     return null;
                  }
               }

               public Result getWSDL(String suggestedFilename) {
                  File f = this.toFile(suggestedFilename);
                  wsdlFileName[0] = f;
                  return this.toResult(f);
               }

               public Result getSchemaOutput(String namespace, String suggestedFilename) {
                  if (namespace == null) {
                     return null;
                  } else {
                     File f = this.toFile(suggestedFilename);
                     schemaFiles.put(namespace, f);
                     return this.toResult(f);
                  }
               }

               public Result getAbstractWSDL(Holder filename) {
                  return this.toResult(this.toFile((String)filename.value));
               }

               public Result getSchemaOutput(String namespace, Holder filename) {
                  return this.getSchemaOutput(namespace, (String)filename.value);
               }
            });
            wsdlGenInfo.setContainer(this.container);
            wsdlGenInfo.setExtensions((WSDLGeneratorExtension[])ServiceFinder.find(WSDLGeneratorExtension.class).toArray());
            wsdlGenInfo.setInlineSchemas(this.options.inlineSchemas);
            rt.generateWSDL(wsdlGenInfo);
            if (this.options.wsgenReport != null) {
               this.generateWsgenReport(endpointClass, (AbstractSEIModelImpl)rt.getModel(), wsdlFileName[0], schemaFiles);
            }
         }

         return true;
      }
   }

   private List getExternalFiles(List exts) {
      List files = new ArrayList();

      File file;
      for(Iterator var3 = exts.iterator(); var3.hasNext(); files.add(file)) {
         String ext = (String)var3.next();
         file = new File(ext);
         if (!file.exists()) {
            file = new File(this.options.sourceDir.getAbsolutePath() + File.separator + ext);
         }
      }

      return files;
   }

   private void generateWsgenReport(Class endpointClass, AbstractSEIModelImpl rtModel, File wsdlFile, Map schemaFiles) {
      try {
         ReportOutput.Report report = (ReportOutput.Report)TXW.create(ReportOutput.Report.class, new StreamSerializer(new BufferedOutputStream(new FileOutputStream(this.options.wsgenReport))));
         report.wsdl(wsdlFile.getAbsolutePath());
         WsgenTool.ReportOutput.writeQName(rtModel.getServiceQName(), report.service());
         WsgenTool.ReportOutput.writeQName(rtModel.getPortName(), report.port());
         WsgenTool.ReportOutput.writeQName(rtModel.getPortTypeName(), report.portType());
         report.implClass(endpointClass.getName());
         Iterator var6 = schemaFiles.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry e = (Map.Entry)var6.next();
            ReportOutput.Schema s = report.schema();
            s.ns((String)e.getKey());
            s.location(((File)e.getValue()).getAbsolutePath());
         }

         report.commit();
      } catch (IOException var9) {
         throw new Error(var9);
      }
   }

   protected void usage(Options options) {
      if (options == null) {
         options = this.options;
      }

      if (options instanceof WsgenOptions) {
         System.out.println(WscompileMessages.WSGEN_HELP("WSGEN", ((WsgenOptions)options).protocols, ((WsgenOptions)options).nonstdProtocols.keySet()));
         System.out.println(WscompileMessages.WSGEN_USAGE_EXTENSIONS());
         System.out.println(WscompileMessages.WSGEN_USAGE_EXAMPLES());
      }

   }

   class Listener extends WsimportListener {
      ConsoleErrorReporter cer;

      Listener() {
         this.cer = new ConsoleErrorReporter(WsgenTool.this.out == null ? new PrintStream(new NullStream()) : WsgenTool.this.out);
      }

      public void generatedFile(String fileName) {
         this.message(fileName);
      }

      public void message(String msg) {
         WsgenTool.this.out.println(msg);
      }

      public void error(SAXParseException exception) {
         this.cer.error(exception);
      }

      public void fatalError(SAXParseException exception) {
         this.cer.fatalError(exception);
      }

      public void warning(SAXParseException exception) {
         this.cer.warning(exception);
      }

      public void info(SAXParseException exception) {
         this.cer.info(exception);
      }
   }

   static class ReportOutput {
      private static void writeQName(QName n, QualifiedName w) {
         w.uri(n.getNamespaceURI());
         w.localName(n.getLocalPart());
      }

      interface Schema extends TypedXmlWriter {
         @XmlAttribute
         void ns(String var1);

         @XmlAttribute
         void location(String var1);
      }

      interface QualifiedName extends TypedXmlWriter {
         @XmlAttribute
         void uri(String var1);

         @XmlAttribute
         void localName(String var1);
      }

      @XmlElement("report")
      interface Report extends TypedXmlWriter {
         @XmlElement
         void wsdl(String var1);

         @XmlElement
         QualifiedName portType();

         @XmlElement
         QualifiedName service();

         @XmlElement
         QualifiedName port();

         @XmlElement
         void implClass(String var1);

         @XmlElement
         Schema schema();
      }
   }
}
