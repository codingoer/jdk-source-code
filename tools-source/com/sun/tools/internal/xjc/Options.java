package com.sun.tools.internal.xjc;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.writer.FileCodeWriter;
import com.sun.codemodel.internal.writer.PrologCodeWriter;
import com.sun.istack.internal.tools.DefaultAuthenticator;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import com.sun.tools.internal.xjc.api.ClassNameAllocator;
import com.sun.tools.internal.xjc.api.SpecVersion;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRendererFactory;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class Options {
   public boolean debugMode;
   public boolean verbose;
   public boolean quiet;
   public boolean readOnly;
   public boolean noFileHeader;
   public boolean enableIntrospection;
   public boolean contentForWildcard;
   public String encoding;
   public boolean disableXmlSecurity;
   public boolean strictCheck = true;
   public boolean runtime14 = false;
   public boolean automaticNameConflictResolution = false;
   public static final int STRICT = 1;
   public static final int EXTENSION = 2;
   public int compatibilityMode = 1;
   private static final Logger logger = Util.getClassLogger();
   public SpecVersion target;
   public File targetDir;
   public EntityResolver entityResolver;
   private Language schemaLanguage;
   public String defaultPackage;
   public String defaultPackage2;
   private final List grammars;
   private final List bindFiles;
   private String proxyHost;
   private String proxyPort;
   public String proxyAuth;
   public final List activePlugins;
   private List allPlugins;
   public final Set pluginURIs;
   public ClassNameAllocator classNameAllocator;
   public boolean packageLevelAnnotations;
   private FieldRendererFactory fieldRendererFactory;
   private Plugin fieldRendererFactoryOwner;
   private NameConverter nameConverter;
   private Plugin nameConverterOwner;
   public final List classpaths;
   private static String pluginLoadFailure;

   public boolean isExtensionMode() {
      return this.compatibilityMode == 2;
   }

   public Options() {
      this.target = SpecVersion.LATEST;
      this.targetDir = new File(".");
      this.entityResolver = null;
      this.schemaLanguage = null;
      this.defaultPackage = null;
      this.defaultPackage2 = null;
      this.grammars = new ArrayList();
      this.bindFiles = new ArrayList();
      this.proxyHost = null;
      this.proxyPort = null;
      this.proxyAuth = null;
      this.activePlugins = new ArrayList();
      this.pluginURIs = new HashSet();
      this.packageLevelAnnotations = true;
      this.fieldRendererFactory = new FieldRendererFactory();
      this.fieldRendererFactoryOwner = null;
      this.nameConverter = null;
      this.nameConverterOwner = null;
      this.classpaths = new ArrayList();

      try {
         Class.forName("javax.xml.bind.JAXBPermission");
      } catch (ClassNotFoundException var2) {
         this.target = SpecVersion.V2_1;
      }

   }

   public FieldRendererFactory getFieldRendererFactory() {
      return this.fieldRendererFactory;
   }

   public void setFieldRendererFactory(FieldRendererFactory frf, Plugin owner) throws BadCommandLineException {
      if (frf == null) {
         throw new IllegalArgumentException();
      } else if (this.fieldRendererFactoryOwner != null) {
         throw new BadCommandLineException(Messages.format("FIELD_RENDERER_CONFLICT", this.fieldRendererFactoryOwner.getOptionName(), owner.getOptionName()));
      } else {
         this.fieldRendererFactoryOwner = owner;
         this.fieldRendererFactory = frf;
      }
   }

   public NameConverter getNameConverter() {
      return this.nameConverter;
   }

   public void setNameConverter(NameConverter nc, Plugin owner) throws BadCommandLineException {
      if (nc == null) {
         throw new IllegalArgumentException();
      } else if (this.nameConverter != null) {
         throw new BadCommandLineException(Messages.format("NAME_CONVERTER_CONFLICT", this.nameConverterOwner.getOptionName(), owner.getOptionName()));
      } else {
         this.nameConverterOwner = owner;
         this.nameConverter = nc;
      }
   }

   public List getAllPlugins() {
      if (this.allPlugins == null) {
         this.allPlugins = new ArrayList();
         ClassLoader ucl = this.getUserClassLoader(SecureLoader.getClassClassLoader(this.getClass()));
         this.allPlugins.addAll(Arrays.asList(findServices(Plugin.class, ucl)));
      }

      return this.allPlugins;
   }

   public Language getSchemaLanguage() {
      if (this.schemaLanguage == null) {
         this.schemaLanguage = this.guessSchemaLanguage();
      }

      return this.schemaLanguage;
   }

   public void setSchemaLanguage(Language _schemaLanguage) {
      this.schemaLanguage = _schemaLanguage;
   }

   public InputSource[] getGrammars() {
      return (InputSource[])this.grammars.toArray(new InputSource[this.grammars.size()]);
   }

   public void addGrammar(InputSource is) {
      this.grammars.add(this.absolutize(is));
   }

   private InputSource fileToInputSource(File source) {
      try {
         String url = source.toURL().toExternalForm();
         return new InputSource(com.sun.tools.internal.xjc.reader.Util.escapeSpace(url));
      } catch (MalformedURLException var3) {
         return new InputSource(source.getPath());
      }
   }

   public void addGrammar(File source) {
      this.addGrammar(this.fileToInputSource(source));
   }

   public void addGrammarRecursive(File dir) {
      this.addRecursive(dir, ".xsd", this.grammars);
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
         logger.log(Level.FINE, "{0}, {1}", new Object[]{is.getSystemId(), var3.getLocalizedMessage()});
      }

      return is;
   }

   public InputSource[] getBindFiles() {
      return (InputSource[])this.bindFiles.toArray(new InputSource[this.bindFiles.size()]);
   }

   public void addBindFile(InputSource is) {
      this.bindFiles.add(this.absolutize(is));
   }

   public void addBindFile(File bindFile) {
      this.bindFiles.add(this.fileToInputSource(bindFile));
   }

   public void addBindFileRecursive(File dir) {
      this.addRecursive(dir, ".xjb", this.bindFiles);
   }

   public ClassLoader getUserClassLoader(ClassLoader parent) {
      return (ClassLoader)(this.classpaths.isEmpty() ? parent : new URLClassLoader((URL[])this.classpaths.toArray(new URL[this.classpaths.size()]), parent));
   }

   public int parseArgument(String[] args, int i) throws BadCommandLineException {
      String token;
      int r;
      if (!args[i].equals("-classpath") && !args[i].equals("-cp")) {
         if (args[i].equals("-d")) {
            ++i;
            this.targetDir = new File(this.requireArgument("-d", args, i));
            if (!this.targetDir.exists()) {
               throw new BadCommandLineException(Messages.format("Driver.NonExistentDir", this.targetDir));
            } else {
               return 2;
            }
         } else if (args[i].equals("-readOnly")) {
            this.readOnly = true;
            return 1;
         } else if (args[i].equals("-p")) {
            ++i;
            this.defaultPackage = this.requireArgument("-p", args, i);
            if (this.defaultPackage.length() == 0) {
               this.packageLevelAnnotations = false;
            }

            return 2;
         } else if (args[i].equals("-debug")) {
            this.debugMode = true;
            this.verbose = true;
            return 1;
         } else if (args[i].equals("-nv")) {
            this.strictCheck = false;
            return 1;
         } else if (args[i].equals("-npa")) {
            this.packageLevelAnnotations = false;
            return 1;
         } else if (args[i].equals("-no-header")) {
            this.noFileHeader = true;
            return 1;
         } else if (args[i].equals("-verbose")) {
            this.verbose = true;
            return 1;
         } else if (args[i].equals("-quiet")) {
            this.quiet = true;
            return 1;
         } else if (args[i].equals("-XexplicitAnnotation")) {
            this.runtime14 = true;
            return 1;
         } else if (args[i].equals("-enableIntrospection")) {
            this.enableIntrospection = true;
            return 1;
         } else if (args[i].equals("-disableXmlSecurity")) {
            this.disableXmlSecurity = true;
            return 1;
         } else if (args[i].equals("-contentForWildcard")) {
            this.contentForWildcard = true;
            return 1;
         } else if (args[i].equals("-XautoNameResolution")) {
            this.automaticNameConflictResolution = true;
            return 1;
         } else if (args[i].equals("-b")) {
            ++i;
            this.addFile(this.requireArgument("-b", args, i), this.bindFiles, ".xjb");
            return 2;
         } else if (args[i].equals("-dtd")) {
            this.schemaLanguage = Language.DTD;
            return 1;
         } else if (args[i].equals("-relaxng")) {
            this.schemaLanguage = Language.RELAXNG;
            return 1;
         } else if (args[i].equals("-relaxng-compact")) {
            this.schemaLanguage = Language.RELAXNG_COMPACT;
            return 1;
         } else if (args[i].equals("-xmlschema")) {
            this.schemaLanguage = Language.XMLSCHEMA;
            return 1;
         } else if (args[i].equals("-wsdl")) {
            this.schemaLanguage = Language.WSDL;
            return 1;
         } else if (args[i].equals("-extension")) {
            this.compatibilityMode = 2;
            return 1;
         } else if (args[i].equals("-target")) {
            ++i;
            token = this.requireArgument("-target", args, i);
            this.target = SpecVersion.parse(token);
            if (this.target == null) {
               throw new BadCommandLineException(Messages.format("Driver.ILLEGAL_TARGET_VERSION", token));
            } else {
               return 2;
            }
         } else {
            File catalogFile;
            if (args[i].equals("-httpproxyfile")) {
               if (i != args.length - 1 && !args[i + 1].startsWith("-")) {
                  ++i;
                  catalogFile = new File(args[i]);
                  if (!catalogFile.exists()) {
                     throw new BadCommandLineException(Messages.format("Driver.NO_SUCH_FILE", catalogFile));
                  } else {
                     try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(catalogFile), "UTF-8"));
                        this.parseProxy(in.readLine());
                        in.close();
                        return 2;
                     } catch (IOException var10) {
                        throw new BadCommandLineException(Messages.format("Driver.FailedToParse", catalogFile, var10.getMessage()), var10);
                     }
                  }
               } else {
                  throw new BadCommandLineException(Messages.format("Driver.MISSING_PROXYFILE"));
               }
            } else if (args[i].equals("-httpproxy")) {
               if (i != args.length - 1 && !args[i + 1].startsWith("-")) {
                  ++i;
                  this.parseProxy(args[i]);
                  return 2;
               } else {
                  throw new BadCommandLineException(Messages.format("Driver.MISSING_PROXY"));
               }
            } else if (args[i].equals("-host")) {
               ++i;
               this.proxyHost = this.requireArgument("-host", args, i);
               return 2;
            } else if (args[i].equals("-port")) {
               ++i;
               this.proxyPort = this.requireArgument("-port", args, i);
               return 2;
            } else if (args[i].equals("-catalog")) {
               ++i;
               catalogFile = new File(this.requireArgument("-catalog", args, i));

               try {
                  this.addCatalog(catalogFile);
                  return 2;
               } catch (IOException var11) {
                  throw new BadCommandLineException(Messages.format("Driver.FailedToParse", catalogFile, var11.getMessage()), var11);
               }
            } else if (args[i].equals("-Xtest-class-name-allocator")) {
               this.classNameAllocator = new ClassNameAllocator() {
                  public String assignClassName(String packageName, String className) {
                     System.out.printf("assignClassName(%s,%s)\n", packageName, className);
                     return className + "_Type";
                  }
               };
               return 1;
            } else if (args[i].equals("-encoding")) {
               ++i;
               this.encoding = this.requireArgument("-encoding", args, i);

               try {
                  if (!Charset.isSupported(this.encoding)) {
                     throw new BadCommandLineException(Messages.format("Driver.UnsupportedEncoding", this.encoding));
                  } else {
                     return 2;
                  }
               } catch (IllegalCharsetNameException var12) {
                  throw new BadCommandLineException(Messages.format("Driver.UnsupportedEncoding", this.encoding));
               }
            } else {
               Iterator var15 = this.getAllPlugins().iterator();

               while(var15.hasNext()) {
                  Plugin plugin = (Plugin)var15.next();

                  try {
                     if (('-' + plugin.getOptionName()).equals(args[i])) {
                        this.activePlugins.add(plugin);
                        plugin.onActivated(this);
                        this.pluginURIs.addAll(plugin.getCustomizationURIs());
                        r = plugin.parseArgument(this, args, i);
                        if (r != 0) {
                           return r;
                        }

                        return 1;
                     }

                     r = plugin.parseArgument(this, args, i);
                     if (r != 0) {
                        return r;
                     }
                  } catch (IOException var13) {
                     throw new BadCommandLineException(var13.getMessage(), var13);
                  }
               }

               return 0;
            }
         }
      } else {
         String var10001 = args[i];
         ++i;
         token = this.requireArgument(var10001, args, i);
         String[] var4 = token.split(File.pathSeparator);
         r = var4.length;

         for(int var6 = 0; var6 < r; ++var6) {
            String p = var4[var6];
            File file = new File(p);

            try {
               this.classpaths.add(file.toURL());
            } catch (MalformedURLException var14) {
               throw new BadCommandLineException(Messages.format("Driver.NotAValidFileName", file), var14);
            }
         }

         return 2;
      }
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
            this.proxyPort = "80";
         }
      } else if (j < 0) {
         this.proxyHost = text;
         this.proxyPort = "80";
      } else {
         this.proxyHost = text.substring(0, j);
         this.proxyPort = text.substring(j + 1);
      }

      try {
         Integer.valueOf(this.proxyPort);
      } catch (NumberFormatException var5) {
         throw new BadCommandLineException(Messages.format("Driver.ILLEGAL_PROXY", text));
      }
   }

   public String requireArgument(String optionName, String[] args, int i) throws BadCommandLineException {
      if (i != args.length && !args[i].startsWith("-")) {
         return args[i];
      } else {
         throw new BadCommandLineException(Messages.format("Driver.MissingOperand", optionName));
      }
   }

   private void addFile(String name, List target, String suffix) throws BadCommandLineException {
      Object src;
      try {
         src = com.sun.tools.internal.xjc.reader.Util.getFileOrURL(name);
      } catch (IOException var6) {
         throw new BadCommandLineException(Messages.format("Driver.NotAFileNorURL", name));
      }

      if (src instanceof URL) {
         target.add(this.absolutize(new InputSource(com.sun.tools.internal.xjc.reader.Util.escapeSpace(((URL)src).toExternalForm()))));
      } else {
         File fsrc = (File)src;
         if (fsrc.isDirectory()) {
            this.addRecursive(fsrc, suffix, target);
         } else {
            target.add(this.absolutize(this.fileToInputSource(fsrc)));
         }
      }

   }

   public void addCatalog(File catalogFile) throws IOException {
      if (this.entityResolver == null) {
         CatalogManager.getStaticManager().setIgnoreMissingProperties(true);
         this.entityResolver = new CatalogResolver(true);
      }

      ((CatalogResolver)this.entityResolver).getCatalog().parseCatalog(catalogFile.getPath());
   }

   public void parseArguments(String[] args) throws BadCommandLineException {
      for(int i = 0; i < args.length; ++i) {
         if (args[i].length() == 0) {
            throw new BadCommandLineException();
         }

         if (args[i].charAt(0) == '-') {
            int j = this.parseArgument(args, i);
            if (j == 0) {
               throw new BadCommandLineException(Messages.format("Driver.UnrecognizedParameter", args[i]));
            }

            i += j - 1;
         } else if (args[i].endsWith(".jar")) {
            this.scanEpisodeFile(new File(args[i]));
         } else {
            this.addFile(args[i], this.grammars, ".xsd");
         }
      }

      if (this.proxyHost != null || this.proxyPort != null) {
         if (this.proxyHost == null || this.proxyPort == null) {
            if (this.proxyHost == null) {
               throw new BadCommandLineException(Messages.format("Driver.MissingProxyHost"));
            }

            throw new BadCommandLineException(Messages.format("Driver.MissingProxyPort"));
         }

         System.setProperty("http.proxyHost", this.proxyHost);
         System.setProperty("http.proxyPort", this.proxyPort);
         System.setProperty("https.proxyHost", this.proxyHost);
         System.setProperty("https.proxyPort", this.proxyPort);
         if (this.proxyAuth != null) {
            DefaultAuthenticator.getAuthenticator().setProxyAuth(this.proxyAuth);
         }
      }

      if (this.grammars.isEmpty()) {
         throw new BadCommandLineException(Messages.format("Driver.MissingGrammar"));
      } else {
         if (this.schemaLanguage == null) {
            this.schemaLanguage = this.guessSchemaLanguage();
         }

         if (pluginLoadFailure != null) {
            throw new BadCommandLineException(Messages.format("PLUGIN_LOAD_FAILURE", pluginLoadFailure));
         }
      }
   }

   public void scanEpisodeFile(File jar) throws BadCommandLineException {
      try {
         URLClassLoader ucl = new URLClassLoader(new URL[]{jar.toURL()});
         Enumeration resources = ucl.findResources("META-INF/sun-jaxb.episode");

         while(resources.hasMoreElements()) {
            URL url = (URL)resources.nextElement();
            this.addBindFile(new InputSource(url.toExternalForm()));
         }

      } catch (IOException var5) {
         throw new BadCommandLineException(Messages.format("FAILED_TO_LOAD", jar, var5.getMessage()), var5);
      }
   }

   public Language guessSchemaLanguage() {
      if (this.grammars != null && this.grammars.size() > 0) {
         String name = ((InputSource)this.grammars.get(0)).getSystemId().toLowerCase();
         if (name.endsWith(".rng")) {
            return Language.RELAXNG;
         }

         if (name.endsWith(".rnc")) {
            return Language.RELAXNG_COMPACT;
         }

         if (name.endsWith(".dtd")) {
            return Language.DTD;
         }

         if (name.endsWith(".wsdl")) {
            return Language.WSDL;
         }
      }

      return Language.XMLSCHEMA;
   }

   public CodeWriter createCodeWriter() throws IOException {
      return this.createCodeWriter(new FileCodeWriter(this.targetDir, this.readOnly, this.encoding));
   }

   public CodeWriter createCodeWriter(CodeWriter core) {
      return (CodeWriter)(this.noFileHeader ? core : new PrologCodeWriter(core, this.getPrologComment()));
   }

   public String getPrologComment() {
      String format = Messages.format("Driver.DateFormat") + " '" + Messages.format("Driver.At") + "' " + Messages.format("Driver.TimeFormat");
      SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
      return Messages.format("Driver.FilePrologComment", dateFormat.format(new Date()));
   }

   private static Object[] findServices(Class clazz, ClassLoader classLoader) {
      boolean debug = com.sun.tools.internal.xjc.util.Util.getSystemProperty(Options.class, "findServices") != null;

      try {
         Class serviceLoader = Class.forName("java.util.ServiceLoader");
         if (debug) {
            System.out.println("Using java.util.ServiceLoader");
         }

         Iterable itr = (Iterable)serviceLoader.getMethod("load", Class.class, ClassLoader.class).invoke((Object)null, clazz, classLoader);
         List r = new ArrayList();
         Iterator var26 = itr.iterator();

         while(var26.hasNext()) {
            Object t = var26.next();
            r.add(t);
         }

         return r.toArray((Object[])((Object[])Array.newInstance(clazz, r.size())));
      } catch (ClassNotFoundException var15) {
         String serviceId = "META-INF/services/" + clazz.getName();
         HashSet classNames = new HashSet();
         if (debug) {
            System.out.println("Looking for " + serviceId + " for add-ons");
         }

         try {
            Enumeration e = classLoader.getResources(serviceId);
            if (e == null) {
               return (Object[])((Object[])Array.newInstance(clazz, 0));
            } else {
               ArrayList a = new ArrayList();

               while(e.hasMoreElements()) {
                  URL url = (URL)e.nextElement();
                  BufferedReader reader = null;
                  if (debug) {
                     System.out.println("Checking " + url + " for an add-on");
                  }

                  try {
                     reader = new BufferedReader(new InputStreamReader(url.openStream()));

                     String impl;
                     while((impl = reader.readLine()) != null) {
                        impl = impl.trim();
                        if (classNames.add(impl)) {
                           Class implClass = classLoader.loadClass(impl);
                           if (!clazz.isAssignableFrom(implClass)) {
                              pluginLoadFailure = impl + " is not a subclass of " + clazz + ". Skipping";
                              if (debug) {
                                 System.out.println(pluginLoadFailure);
                              }
                           } else {
                              if (debug) {
                                 System.out.println("Attempting to instanciate " + impl);
                              }

                              a.add(clazz.cast(implClass.newInstance()));
                           }
                        }
                     }

                     reader.close();
                  } catch (Exception var13) {
                     StringWriter w = new StringWriter();
                     var13.printStackTrace(new PrintWriter(w));
                     pluginLoadFailure = w.toString();
                     if (debug) {
                        System.out.println(pluginLoadFailure);
                     }

                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (IOException var12) {
                        }
                     }
                  }
               }

               return a.toArray((Object[])((Object[])Array.newInstance(clazz, a.size())));
            }
         } catch (Throwable var14) {
            StringWriter w = new StringWriter();
            var14.printStackTrace(new PrintWriter(w));
            pluginLoadFailure = w.toString();
            if (debug) {
               System.out.println(pluginLoadFailure);
            }

            return (Object[])((Object[])Array.newInstance(clazz, 0));
         }
      } catch (IllegalAccessException var16) {
         Error x = new IllegalAccessError();
         x.initCause(var16);
         throw x;
      } catch (InvocationTargetException var17) {
         Throwable x = var17.getTargetException();
         if (x instanceof RuntimeException) {
            throw (RuntimeException)x;
         } else if (x instanceof Error) {
            throw (Error)x;
         } else {
            throw new Error(x);
         }
      } catch (NoSuchMethodException var18) {
         Error x = new NoSuchMethodError();
         x.initCause(var18);
         throw x;
      }
   }

   public static String getBuildID() {
      return Messages.format("Driver.BuildID");
   }

   public static String normalizeSystemId(String systemId) {
      try {
         systemId = (new URI(systemId)).normalize().toString();
      } catch (URISyntaxException var2) {
      }

      return systemId;
   }
}
