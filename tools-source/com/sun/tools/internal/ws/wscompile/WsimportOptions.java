package com.sun.tools.internal.ws.wscompile;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.ws.processor.generator.GeneratorExtension;
import com.sun.tools.internal.ws.resources.ConfigurationMessages;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.tools.internal.ws.util.ForkEntityResolver;
import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBindingsConstants;
import com.sun.tools.internal.xjc.api.SchemaCompiler;
import com.sun.tools.internal.xjc.api.SpecVersion;
import com.sun.tools.internal.xjc.api.XJC;
import com.sun.tools.internal.xjc.reader.Util;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;

public class WsimportOptions extends Options {
   public String wsdlLocation;
   public EntityResolver entityResolver = null;
   public String defaultPackage = null;
   public String clientjar = null;
   public boolean additionalHeaders;
   public File implDestDir = null;
   public String implServiceName = null;
   public String implPortName = null;
   public boolean isGenerateJWS = false;
   public boolean disableSSLHostnameVerification;
   public boolean useBaseResourceAndURLToLoadWSDL = false;
   private SchemaCompiler schemaCompiler = XJC.createSchemaCompiler();
   public File authFile = null;
   public static final String defaultAuthfile = System.getProperty("user.home") + System.getProperty("file.separator") + ".metro" + System.getProperty("file.separator") + "auth";
   public boolean disableAuthenticator;
   public String proxyAuth = null;
   private String proxyHost = null;
   private String proxyPort = null;
   public HashMap extensionOptions = new HashMap();
   private List allPlugins;
   public final List activePlugins = new ArrayList();
   private JCodeModel codeModel;
   public List cmdlineJars = new ArrayList();
   public boolean noAddressingBbinding;
   private final List wsdls = new ArrayList();
   private final List schemas = new ArrayList();
   private final List bindingFiles = new ArrayList();
   private final List jaxwsCustomBindings = new ArrayList();
   private final List jaxbCustomBindings = new ArrayList();
   private final List handlerConfigs = new ArrayList();

   public JCodeModel getCodeModel() {
      if (this.codeModel == null) {
         this.codeModel = new JCodeModel();
      }

      return this.codeModel;
   }

   public SchemaCompiler getSchemaCompiler() {
      this.schemaCompiler.setTargetVersion(SpecVersion.parse(this.target.getVersion()));
      if (this.entityResolver != null) {
         this.schemaCompiler.setEntityResolver(this.entityResolver);
      }

      return this.schemaCompiler;
   }

   public void setCodeModel(JCodeModel codeModel) {
      this.codeModel = codeModel;
   }

   public List getAllPlugins() {
      if (this.allPlugins == null) {
         this.allPlugins = new ArrayList();
         this.allPlugins.addAll(Arrays.asList(findServices(Plugin.class, this.getClassLoader())));
      }

      return this.allPlugins;
   }

   public final void parseArguments(String[] args) throws BadCommandLineException {
      for(int i = 0; i < args.length; ++i) {
         if (args[i].length() == 0) {
            throw new BadCommandLineException();
         }

         if (args[i].charAt(0) == '-') {
            int j = this.parseArguments(args, i);
            if (j == 0) {
               throw new BadCommandLineException(WscompileMessages.WSCOMPILE_INVALID_OPTION(args[i]));
            }

            i += j - 1;
         } else if (args[i].endsWith(".jar")) {
            try {
               this.cmdlineJars.add(args[i]);
               this.schemaCompiler.getOptions().scanEpisodeFile(new File(args[i]));
            } catch (com.sun.tools.internal.xjc.BadCommandLineException var5) {
               throw new BadCommandLineException(var5.getMessage(), var5);
            }
         } else {
            this.addFile(args[i]);
         }
      }

      if (this.encoding != null && this.schemaCompiler.getOptions().encoding == null) {
         try {
            this.schemaCompiler.getOptions().parseArgument(new String[]{"-encoding", this.encoding}, 0);
         } catch (com.sun.tools.internal.xjc.BadCommandLineException var4) {
            Logger.getLogger(WsimportOptions.class.getName()).log(Level.SEVERE, (String)null, var4);
         }
      }

      if (this.destDir == null) {
         this.destDir = new File(".");
      }

      if (this.sourceDir == null) {
         this.sourceDir = this.destDir;
      }

   }

   public int parseArguments(String[] args, int i) throws BadCommandLineException {
      int j = super.parseArguments(args, i);
      if (j > 0) {
         return j;
      } else if (args[i].equals("-b")) {
         ++i;
         this.addBindings(this.requireArgument("-b", args, i));
         return 2;
      } else if (args[i].equals("-wsdllocation")) {
         ++i;
         this.wsdlLocation = this.requireArgument("-wsdllocation", args, i);
         return 2;
      } else if (args[i].equals("-XadditionalHeaders")) {
         this.additionalHeaders = true;
         return 1;
      } else if (args[i].equals("-XdisableSSLHostnameVerification")) {
         this.disableSSLHostnameVerification = true;
         return 1;
      } else if (args[i].equals("-p")) {
         ++i;
         this.defaultPackage = this.requireArgument("-p", args, i);
         return 2;
      } else {
         String authfile;
         if (args[i].equals("-catalog")) {
            ++i;
            authfile = this.requireArgument("-catalog", args, i);

            try {
               if (this.entityResolver == null) {
                  if (authfile != null && authfile.length() > 0) {
                     this.entityResolver = XmlUtil.createEntityResolver(JAXWSUtils.getFileOrURL(JAXWSUtils.absolutize(Util.escapeSpace(authfile))));
                  }
               } else if (authfile != null && authfile.length() > 0) {
                  EntityResolver er = XmlUtil.createEntityResolver(JAXWSUtils.getFileOrURL(JAXWSUtils.absolutize(Util.escapeSpace(authfile))));
                  this.entityResolver = new ForkEntityResolver(er, this.entityResolver);
               }

               return 2;
            } catch (IOException var7) {
               throw new BadCommandLineException(WscompileMessages.WSIMPORT_FAILED_TO_PARSE(authfile, var7.getMessage()));
            }
         } else if (args[i].startsWith("-httpproxy:")) {
            authfile = args[i].substring(11);
            if (authfile.length() == 0) {
               throw new BadCommandLineException(WscompileMessages.WSCOMPILE_INVALID_OPTION(args[i]));
            } else {
               this.parseProxy(authfile);
               if (this.proxyHost != null || this.proxyPort != null) {
                  System.setProperty("proxySet", "true");
               }

               if (this.proxyHost != null) {
                  System.setProperty("proxyHost", this.proxyHost);
               }

               if (this.proxyPort != null) {
                  System.setProperty("proxyPort", this.proxyPort);
               }

               return 1;
            }
         } else if (args[i].equals("-Xno-addressing-databinding")) {
            this.noAddressingBbinding = true;
            return 1;
         } else {
            int r;
            if (args[i].startsWith("-B")) {
               String[] subCmd = new String[args.length - i];
               System.arraycopy(args, i, subCmd, 0, subCmd.length);
               subCmd[0] = subCmd[0].substring(2);
               com.sun.tools.internal.xjc.Options jaxbOptions = this.schemaCompiler.getOptions();

               try {
                  r = jaxbOptions.parseArgument(subCmd, 0);
                  if (r == 0) {
                     throw new BadCommandLineException(WscompileMessages.WSIMPORT_NO_SUCH_JAXB_OPTION(subCmd[0]));
                  } else {
                     return r;
                  }
               } catch (com.sun.tools.internal.xjc.BadCommandLineException var8) {
                  throw new BadCommandLineException(var8.getMessage(), var8);
               }
            } else if (args[i].equals("-Xauthfile")) {
               ++i;
               authfile = this.requireArgument("-Xauthfile", args, i);
               this.authFile = new File(authfile);
               return 2;
            } else if (args[i].equals("-clientjar")) {
               ++i;
               this.clientjar = this.requireArgument("-clientjar", args, i);
               return 2;
            } else if (args[i].equals("-implDestDir")) {
               ++i;
               this.implDestDir = new File(this.requireArgument("-implDestDir", args, i));
               if (!this.implDestDir.exists()) {
                  throw new BadCommandLineException(WscompileMessages.WSCOMPILE_NO_SUCH_DIRECTORY(this.implDestDir.getPath()));
               } else {
                  return 2;
               }
            } else if (args[i].equals("-implServiceName")) {
               ++i;
               this.implServiceName = this.requireArgument("-implServiceName", args, i);
               return 2;
            } else if (args[i].equals("-implPortName")) {
               ++i;
               this.implPortName = this.requireArgument("-implPortName", args, i);
               return 2;
            } else if (args[i].equals("-generateJWS")) {
               this.isGenerateJWS = true;
               return 1;
            } else if (args[i].equals("-XuseBaseResourceAndURLToLoadWSDL")) {
               this.useBaseResourceAndURLToLoadWSDL = true;
               return 1;
            } else if (args[i].equals("-XdisableAuthenticator")) {
               this.disableAuthenticator = true;
               return 1;
            } else {
               Iterator var4 = ServiceFinder.find(GeneratorExtension.class).iterator();

               while(var4.hasNext()) {
                  GeneratorExtension f = (GeneratorExtension)var4.next();
                  if (f.validateOption(args[i])) {
                     String var10001 = args[i];
                     String var10003 = args[i];
                     ++i;
                     this.extensionOptions.put(var10001, this.requireArgument(var10003, args, i));
                     return 2;
                  }
               }

               var4 = this.getAllPlugins().iterator();

               while(var4.hasNext()) {
                  Plugin plugin = (Plugin)var4.next();

                  try {
                     if (('-' + plugin.getOptionName()).equals(args[i])) {
                        this.activePlugins.add(plugin);
                        plugin.onActivated(this);
                        return 1;
                     }

                     r = plugin.parseArgument(this, args, i);
                     if (r != 0) {
                        return r;
                     }
                  } catch (IOException var9) {
                     throw new BadCommandLineException(var9.getMessage(), var9);
                  }
               }

               return 0;
            }
         }
      }
   }

   public void validate() throws BadCommandLineException {
      if (this.wsdls.isEmpty()) {
         throw new BadCommandLineException(WscompileMessages.WSIMPORT_MISSING_FILE());
      } else if (this.wsdlLocation != null && this.clientjar != null) {
         throw new BadCommandLineException(WscompileMessages.WSIMPORT_WSDLLOCATION_CLIENTJAR());
      } else {
         if (this.wsdlLocation == null) {
            this.wsdlLocation = ((InputSource)this.wsdls.get(0)).getSystemId();
         }

      }
   }

   protected void addFile(String arg) throws BadCommandLineException {
      this.addFile(arg, this.wsdls, ".wsdl");
   }

   public Element getHandlerChainConfiguration() {
      return this.handlerConfigs.size() > 0 ? (Element)this.handlerConfigs.get(0) : null;
   }

   public void addHandlerChainConfiguration(Element config) {
      this.handlerConfigs.add(config);
   }

   public InputSource[] getWSDLs() {
      return (InputSource[])this.wsdls.toArray(new InputSource[this.wsdls.size()]);
   }

   public InputSource[] getSchemas() {
      return (InputSource[])this.schemas.toArray(new InputSource[this.schemas.size()]);
   }

   public InputSource[] getWSDLBindings() {
      return (InputSource[])this.jaxwsCustomBindings.toArray(new InputSource[this.jaxwsCustomBindings.size()]);
   }

   public InputSource[] getSchemaBindings() {
      return (InputSource[])this.jaxbCustomBindings.toArray(new InputSource[this.jaxbCustomBindings.size()]);
   }

   public void addWSDL(File source) {
      this.addWSDL(this.fileToInputSource(source));
   }

   public void addWSDL(InputSource is) {
      this.wsdls.add(this.absolutize(is));
   }

   public void addSchema(File source) {
      this.addSchema(this.fileToInputSource(source));
   }

   public void addSchema(InputSource is) {
      this.schemas.add(is);
   }

   private InputSource fileToInputSource(File source) {
      try {
         String url = source.toURL().toExternalForm();
         return new InputSource(Util.escapeSpace(url));
      } catch (MalformedURLException var3) {
         return new InputSource(source.getPath());
      }
   }

   public void addGrammarRecursive(File dir) {
      this.addRecursive(dir, ".wsdl", this.wsdls);
      this.addRecursive(dir, ".xsd", this.schemas);
   }

   public void addWSDLBindFile(InputSource is) {
      this.jaxwsCustomBindings.add(new RereadInputSource(this.absolutize(is)));
   }

   public void addSchemmaBindFile(InputSource is) {
      this.jaxbCustomBindings.add(new RereadInputSource(this.absolutize(is)));
   }

   private void addRecursive(File dir, String suffix, List result) {
      File[] files = dir.listFiles();
      if (files != null) {
         File[] var5 = files;
         int var6 = files.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File f = var5[var7];
            if (f.isDirectory()) {
               this.addRecursive(f, suffix, result);
            } else if (f.getPath().endsWith(suffix)) {
               result.add(this.absolutize(this.fileToInputSource(f)));
            }
         }

      }
   }

   private InputSource absolutize(InputSource is) {
      try {
         URL baseURL = (new File(".")).getCanonicalFile().toURL();
         is.setSystemId((new URL(baseURL, is.getSystemId())).toExternalForm());
      } catch (IOException var3) {
      }

      return is;
   }

   public void addBindings(String name) throws BadCommandLineException {
      this.addFile(name, this.bindingFiles, (String)null);
   }

   private void addFile(String name, List target, String suffix) throws BadCommandLineException {
      Object src;
      try {
         src = Util.getFileOrURL(name);
      } catch (IOException var6) {
         throw new BadCommandLineException(WscompileMessages.WSIMPORT_NOT_A_FILE_NOR_URL(name));
      }

      if (src instanceof URL) {
         target.add(this.absolutize(new InputSource(Util.escapeSpace(((URL)src).toExternalForm()))));
      } else {
         File fsrc = (File)src;
         if (fsrc.isDirectory()) {
            this.addRecursive(fsrc, suffix, target);
         } else {
            target.add(this.absolutize(this.fileToInputSource(fsrc)));
         }
      }

   }

   public final void parseBindings(ErrorReceiver receiver) {
      Iterator var2 = this.bindingFiles.iterator();

      while(true) {
         while(var2.hasNext()) {
            InputSource is = (InputSource)var2.next();
            XMLStreamReader reader = XMLStreamReaderFactory.create(is, true);
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getName().equals(JAXWSBindingsConstants.JAXWS_BINDINGS)) {
               this.jaxwsCustomBindings.add(new RereadInputSource(is));
            } else if (!reader.getName().equals(JAXWSBindingsConstants.JAXB_BINDINGS) && !reader.getName().equals(new QName("http://www.w3.org/2001/XMLSchema", "schema"))) {
               LocatorImpl locator = new LocatorImpl();
               locator.setSystemId(reader.getLocation().getSystemId());
               locator.setPublicId(reader.getLocation().getPublicId());
               locator.setLineNumber(reader.getLocation().getLineNumber());
               locator.setColumnNumber(reader.getLocation().getColumnNumber());
               receiver.warning(locator, ConfigurationMessages.CONFIGURATION_NOT_BINDING_FILE(is.getSystemId()));
            } else {
               this.jaxbCustomBindings.add(new RereadInputSource(is));
            }
         }

         return;
      }
   }

   public String getExtensionOption(String argument) {
      return (String)this.extensionOptions.get(argument);
   }

   private void parseProxy(String text) throws BadCommandLineException {
      int i = text.lastIndexOf(64);
      int j = text.lastIndexOf(58);
      if (i > 0) {
         this.proxyAuth = text.substring(0, i);
         if (j > i) {
            this.proxyHost = text.substring(i + 1, j);
            this.proxyPort = text.substring(j + 1);
         } else {
            this.proxyHost = text.substring(i + 1);
            this.proxyPort = "8080";
         }
      } else if (j < 0) {
         this.proxyHost = text;
         this.proxyPort = "8080";
      } else {
         this.proxyHost = text.substring(0, j);
         this.proxyPort = text.substring(j + 1);
      }

      try {
         Integer.valueOf(this.proxyPort);
      } catch (NumberFormatException var5) {
         throw new BadCommandLineException(WscompileMessages.WSIMPORT_ILLEGAL_PROXY(text));
      }
   }

   private static Object[] findServices(Class clazz, ClassLoader classLoader) {
      ServiceFinder serviceFinder = ServiceFinder.find(clazz, classLoader);
      List r = new ArrayList();
      Iterator var4 = serviceFinder.iterator();

      while(var4.hasNext()) {
         Object t = var4.next();
         r.add(t);
      }

      return r.toArray((Object[])((Object[])Array.newInstance(clazz, r.size())));
   }

   protected void disableXmlSecurity() {
      super.disableXmlSecurity();
      this.schemaCompiler.getOptions().disableXmlSecurity = true;
   }

   private static final class RereadInputSource extends InputSource {
      private InputSource is;

      RereadInputSource(InputSource is) {
         this.is = is;
      }

      public InputStream getByteStream() {
         InputStream i = this.is.getByteStream();
         if (i != null && !(i instanceof RereadInputStream)) {
            i = new RereadInputStream((InputStream)i);
            this.is.setByteStream((InputStream)i);
         }

         return (InputStream)i;
      }

      public Reader getCharacterStream() {
         return this.is.getCharacterStream();
      }

      public String getEncoding() {
         return this.is.getEncoding();
      }

      public String getPublicId() {
         return this.is.getPublicId();
      }

      public String getSystemId() {
         return this.is.getSystemId();
      }

      public void setByteStream(InputStream byteStream) {
         this.is.setByteStream(byteStream);
      }

      public void setCharacterStream(Reader characterStream) {
         this.is.setCharacterStream(characterStream);
      }

      public void setEncoding(String encoding) {
         this.is.setEncoding(encoding);
      }

      public void setPublicId(String publicId) {
         this.is.setPublicId(publicId);
      }

      public void setSystemId(String systemId) {
         this.is.setSystemId(systemId);
      }
   }

   private static final class RereadInputStream extends InputStream {
      private InputStream is;
      private ByteStream bs;

      RereadInputStream(InputStream is) {
         this.is = is;
         this.bs = new ByteStream();
      }

      public int available() throws IOException {
         return this.is.available();
      }

      public void close() throws IOException {
         if (this.bs != null) {
            InputStream i = new ByteArrayInputStream(this.bs.getBuffer());
            this.bs = null;
            this.is.close();
            this.is = i;
         }

      }

      public synchronized void mark(int readlimit) {
         this.is.mark(readlimit);
      }

      public boolean markSupported() {
         return this.is.markSupported();
      }

      public int read() throws IOException {
         int r = this.is.read();
         if (this.bs != null) {
            this.bs.write(r);
         }

         return r;
      }

      public int read(byte[] b, int off, int len) throws IOException {
         int r = this.is.read(b, off, len);
         if (r > 0 && this.bs != null) {
            this.bs.write(b, off, r);
         }

         return r;
      }

      public int read(byte[] b) throws IOException {
         int r = this.is.read(b);
         if (r > 0 && this.bs != null) {
            this.bs.write(b, 0, r);
         }

         return r;
      }

      public synchronized void reset() throws IOException {
         this.is.reset();
      }
   }

   private static final class ByteStream extends ByteArrayOutputStream {
      private ByteStream() {
      }

      byte[] getBuffer() {
         return this.buf;
      }

      // $FF: synthetic method
      ByteStream(Object x0) {
         this();
      }
   }
}
