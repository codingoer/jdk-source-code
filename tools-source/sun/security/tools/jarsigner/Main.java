package sun.security.tools.jarsigner;

import com.sun.jarsigner.ContentSigner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AlgorithmParameters;
import java.security.CodeSigner;
import java.security.CryptoPrimitive;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Timestamp;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.Locale.Category;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.timestamp.TimestampToken;
import sun.security.tools.KeyStoreUtil;
import sun.security.tools.PathList;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.util.ManifestDigester;
import sun.security.util.Password;
import sun.security.util.SignatureFileVerifier;
import sun.security.validator.Validator;
import sun.security.validator.ValidatorException;
import sun.security.x509.AlgorithmId;
import sun.security.x509.NetscapeCertTypeExtension;

public class Main {
   private static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.jarsigner.Resources");
   private static final Collator collator = Collator.getInstance();
   private static final String META_INF = "META-INF/";
   private static final Class[] PARAM_STRING;
   private static final String NONE = "NONE";
   private static final String P11KEYSTORE = "PKCS11";
   private static final long SIX_MONTHS = 15552000000L;
   private static final long ONE_YEAR = 31622400000L;
   private static final DisabledAlgorithmConstraints DISABLED_CHECK;
   private static final Set DIGEST_PRIMITIVE_SET;
   private static final Set SIG_PRIMITIVE_SET;
   static final String VERSION = "1.0";
   static final int IN_KEYSTORE = 1;
   static final int IN_SCOPE = 2;
   static final int NOT_ALIAS = 4;
   static final int SIGNED_BY_ALIAS = 8;
   X509Certificate[] certChain;
   PrivateKey privateKey;
   KeyStore store;
   String keystore;
   boolean nullStream = false;
   boolean token = false;
   String jarfile;
   String alias;
   List ckaliases = new ArrayList();
   char[] storepass;
   boolean protectedPath;
   String storetype;
   String providerName;
   Vector providers = null;
   HashMap providerArgs = new HashMap();
   char[] keypass;
   String sigfile;
   String sigalg;
   String digestalg = "SHA-256";
   String signedjar;
   String tsaUrl;
   String tsaAlias;
   String altCertChain;
   String tSAPolicyID;
   String tSADigestAlg = "SHA-256";
   boolean verify = false;
   String verbose = null;
   boolean showcerts = false;
   boolean debug = false;
   boolean signManifest = true;
   boolean externalSF = true;
   boolean strict = false;
   private ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
   private byte[] buffer = new byte[8192];
   private ContentSigner signingMechanism = null;
   private String altSignerClass = null;
   private String altSignerClasspath = null;
   private ZipFile zipFile = null;
   private boolean hasExpiringCert = false;
   private boolean hasExpiringTsaCert = false;
   private boolean noTimestamp = true;
   private Date expireDate = null;
   private Date tsaExpireDate = null;
   boolean hasTimestampBlock = false;
   private int weakAlg = 0;
   private boolean hasExpiredCert = false;
   private boolean hasExpiredTsaCert = false;
   private boolean notYetValidCert = false;
   private boolean chainNotValidated = false;
   private boolean tsaChainNotValidated = false;
   private boolean notSignedByAlias = false;
   private boolean aliasNotInStore = false;
   private boolean hasUnsignedEntry = false;
   private boolean badKeyUsage = false;
   private boolean badExtendedKeyUsage = false;
   private boolean badNetscapeCertType = false;
   private boolean signerSelfSigned = false;
   private Throwable chainNotValidatedReason = null;
   private Throwable tsaChainNotValidatedReason = null;
   private boolean seeWeak = false;
   PKIXBuilderParameters pkixParameters;
   Set trustedCerts = new HashSet();
   private static MessageFormat validityTimeForm;
   private static MessageFormat notYetTimeForm;
   private static MessageFormat expiredTimeForm;
   private static MessageFormat expiringTimeForm;
   private static MessageFormat signTimeForm;
   private Map cacheForInKS = new IdentityHashMap();
   Hashtable storeHash = new Hashtable();
   Map cacheForSignerInfo = new IdentityHashMap();

   public static void main(String[] var0) throws Exception {
      Main var1 = new Main();
      var1.run(var0);
   }

   public void run(String[] var1) {
      try {
         this.parseArgs(var1);
         if (this.providers != null) {
            ClassLoader var2 = ClassLoader.getSystemClassLoader();
            Enumeration var3 = this.providers.elements();

            while(var3.hasMoreElements()) {
               String var4 = (String)var3.nextElement();
               Class var5;
               if (var2 != null) {
                  var5 = var2.loadClass(var4);
               } else {
                  var5 = Class.forName(var4);
               }

               String var6 = (String)this.providerArgs.get(var4);
               Object var7;
               if (var6 == null) {
                  var7 = var5.newInstance();
               } else {
                  Constructor var8 = var5.getConstructor(PARAM_STRING);
                  var7 = var8.newInstance(var6);
               }

               if (!(var7 instanceof Provider)) {
                  MessageFormat var18 = new MessageFormat(rb.getString("provName.not.a.provider"));
                  Object[] var9 = new Object[]{var4};
                  throw new Exception(var18.format(var9));
               }

               Security.addProvider((Provider)var7);
            }
         }

         if (this.verify) {
            try {
               this.loadKeyStore(this.keystore, false);
            } catch (Exception var14) {
               if (this.keystore != null || this.storepass != null) {
                  System.out.println(rb.getString("jarsigner.error.") + var14.getMessage());
                  System.exit(1);
               }
            }

            this.verifyJar(this.jarfile);
         } else {
            this.loadKeyStore(this.keystore, true);
            this.getAliasInfo(this.alias);
            if (this.altSignerClass != null) {
               this.signingMechanism = this.loadSigningMechanism(this.altSignerClass, this.altSignerClasspath);
            }

            this.signJar(this.jarfile, this.alias, var1);
         }
      } catch (Exception var15) {
         System.out.println(rb.getString("jarsigner.error.") + var15);
         if (this.debug) {
            var15.printStackTrace();
         }

         System.exit(1);
      } finally {
         if (this.keypass != null) {
            Arrays.fill(this.keypass, ' ');
            this.keypass = null;
         }

         if (this.storepass != null) {
            Arrays.fill(this.storepass, ' ');
            this.storepass = null;
         }

      }

      if (this.strict) {
         int var17 = 0;
         if (this.weakAlg != 0 || this.chainNotValidated || this.hasExpiredCert || this.hasExpiredTsaCert || this.notYetValidCert || this.signerSelfSigned) {
            var17 |= 4;
         }

         if (this.badKeyUsage || this.badExtendedKeyUsage || this.badNetscapeCertType) {
            var17 |= 8;
         }

         if (this.hasUnsignedEntry) {
            var17 |= 16;
         }

         if (this.notSignedByAlias || this.aliasNotInStore) {
            var17 |= 32;
         }

         if (this.tsaChainNotValidated) {
            var17 |= 64;
         }

         if (var17 != 0) {
            System.exit(var17);
         }
      }

   }

   void parseArgs(String[] var1) {
      boolean var2 = false;
      if (var1.length == 0) {
         fullusage();
      }

      for(int var7 = 0; var7 < var1.length; ++var7) {
         String var3 = var1[var7];
         String var4 = null;
         if (var3.startsWith("-")) {
            int var5 = var3.indexOf(58);
            if (var5 > 0) {
               var4 = var3.substring(var5 + 1);
               var3 = var3.substring(0, var5);
            }
         }

         if (!var3.startsWith("-")) {
            if (this.jarfile == null) {
               this.jarfile = var3;
            } else {
               this.alias = var3;
               this.ckaliases.add(this.alias);
            }
         } else if (collator.compare(var3, "-keystore") == 0) {
            ++var7;
            if (var7 == var1.length) {
               usageNoArg();
            }

            this.keystore = var1[var7];
         } else if (collator.compare(var3, "-storepass") == 0) {
            ++var7;
            if (var7 == var1.length) {
               usageNoArg();
            }

            this.storepass = getPass(var4, var1[var7]);
         } else if (collator.compare(var3, "-storetype") == 0) {
            ++var7;
            if (var7 == var1.length) {
               usageNoArg();
            }

            this.storetype = var1[var7];
         } else if (collator.compare(var3, "-providerName") == 0) {
            ++var7;
            if (var7 == var1.length) {
               usageNoArg();
            }

            this.providerName = var1[var7];
         } else if (collator.compare(var3, "-provider") != 0 && collator.compare(var3, "-providerClass") != 0) {
            if (collator.compare(var3, "-protected") == 0) {
               this.protectedPath = true;
            } else if (collator.compare(var3, "-certchain") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.altCertChain = var1[var7];
            } else if (collator.compare(var3, "-tsapolicyid") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.tSAPolicyID = var1[var7];
            } else if (collator.compare(var3, "-tsadigestalg") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.tSADigestAlg = var1[var7];
            } else if (collator.compare(var3, "-debug") == 0) {
               this.debug = true;
            } else if (collator.compare(var3, "-keypass") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.keypass = getPass(var4, var1[var7]);
            } else if (collator.compare(var3, "-sigfile") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.sigfile = var1[var7];
            } else if (collator.compare(var3, "-signedjar") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.signedjar = var1[var7];
            } else if (collator.compare(var3, "-tsa") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.tsaUrl = var1[var7];
            } else if (collator.compare(var3, "-tsacert") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.tsaAlias = var1[var7];
            } else if (collator.compare(var3, "-altsigner") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.altSignerClass = var1[var7];
            } else if (collator.compare(var3, "-altsignerpath") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.altSignerClasspath = var1[var7];
            } else if (collator.compare(var3, "-sectionsonly") == 0) {
               this.signManifest = false;
            } else if (collator.compare(var3, "-internalsf") == 0) {
               this.externalSF = false;
            } else if (collator.compare(var3, "-verify") == 0) {
               this.verify = true;
            } else if (collator.compare(var3, "-verbose") == 0) {
               this.verbose = var4 != null ? var4 : "all";
            } else if (collator.compare(var3, "-sigalg") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.sigalg = var1[var7];
            } else if (collator.compare(var3, "-digestalg") == 0) {
               ++var7;
               if (var7 == var1.length) {
                  usageNoArg();
               }

               this.digestalg = var1[var7];
            } else if (collator.compare(var3, "-certs") == 0) {
               this.showcerts = true;
            } else if (collator.compare(var3, "-strict") == 0) {
               this.strict = true;
            } else if (collator.compare(var3, "-h") != 0 && collator.compare(var3, "-help") != 0) {
               System.err.println(rb.getString("Illegal.option.") + var3);
               usage();
            } else {
               fullusage();
            }
         } else {
            ++var7;
            if (var7 == var1.length) {
               usageNoArg();
            }

            if (this.providers == null) {
               this.providers = new Vector(3);
            }

            this.providers.add(var1[var7]);
            if (var1.length > var7 + 1) {
               var3 = var1[var7 + 1];
               if (collator.compare(var3, "-providerArg") == 0) {
                  if (var1.length == var7 + 2) {
                     usageNoArg();
                  }

                  this.providerArgs.put(var1[var7], var1[var7 + 2]);
                  var7 += 2;
               }
            }
         }
      }

      if (this.verbose == null) {
         this.showcerts = false;
      }

      if (this.jarfile == null) {
         System.err.println(rb.getString("Please.specify.jarfile.name"));
         usage();
      }

      if (!this.verify && this.alias == null) {
         System.err.println(rb.getString("Please.specify.alias.name"));
         usage();
      }

      if (!this.verify && this.ckaliases.size() > 1) {
         System.err.println(rb.getString("Only.one.alias.can.be.specified"));
         usage();
      }

      if (this.storetype == null) {
         this.storetype = KeyStore.getDefaultType();
      }

      this.storetype = KeyStoreUtil.niceStoreTypeName(this.storetype);

      try {
         if (this.signedjar != null && (new File(this.signedjar)).getCanonicalPath().equals((new File(this.jarfile)).getCanonicalPath())) {
            this.signedjar = null;
         }
      } catch (IOException var6) {
      }

      if ("PKCS11".equalsIgnoreCase(this.storetype) || KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
         this.token = true;
         if (this.keystore == null) {
            this.keystore = "NONE";
         }
      }

      if ("NONE".equals(this.keystore)) {
         this.nullStream = true;
      }

      if (this.token && !this.nullStream) {
         System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), this.storetype));
         usage();
      }

      if (this.token && this.keypass != null) {
         System.err.println(MessageFormat.format(rb.getString(".keypass.can.not.be.specified.if.storetype.is.{0}"), this.storetype));
         usage();
      }

      if (this.protectedPath && (this.storepass != null || this.keypass != null)) {
         System.err.println(rb.getString("If.protected.is.specified.then.storepass.and.keypass.must.not.be.specified"));
         usage();
      }

      if (KeyStoreUtil.isWindowsKeyStore(this.storetype) && (this.storepass != null || this.keypass != null)) {
         System.err.println(rb.getString("If.keystore.is.not.password.protected.then.storepass.and.keypass.must.not.be.specified"));
         usage();
      }

   }

   static char[] getPass(String var0, String var1) {
      char[] var2 = KeyStoreUtil.getPassWithModifier(var0, var1, rb);
      if (var2 != null) {
         return var2;
      } else {
         usage();
         return null;
      }
   }

   static void usageNoArg() {
      System.out.println(rb.getString("Option.lacks.argument"));
      usage();
   }

   static void usage() {
      System.out.println();
      System.out.println(rb.getString("Please.type.jarsigner.help.for.usage"));
      System.exit(1);
   }

   static void fullusage() {
      System.out.println(rb.getString("Usage.jarsigner.options.jar.file.alias"));
      System.out.println(rb.getString(".jarsigner.verify.options.jar.file.alias."));
      System.out.println();
      System.out.println(rb.getString(".keystore.url.keystore.location"));
      System.out.println();
      System.out.println(rb.getString(".storepass.password.password.for.keystore.integrity"));
      System.out.println();
      System.out.println(rb.getString(".storetype.type.keystore.type"));
      System.out.println();
      System.out.println(rb.getString(".keypass.password.password.for.private.key.if.different."));
      System.out.println();
      System.out.println(rb.getString(".certchain.file.name.of.alternative.certchain.file"));
      System.out.println();
      System.out.println(rb.getString(".sigfile.file.name.of.SF.DSA.file"));
      System.out.println();
      System.out.println(rb.getString(".signedjar.file.name.of.signed.JAR.file"));
      System.out.println();
      System.out.println(rb.getString(".digestalg.algorithm.name.of.digest.algorithm"));
      System.out.println();
      System.out.println(rb.getString(".sigalg.algorithm.name.of.signature.algorithm"));
      System.out.println();
      System.out.println(rb.getString(".verify.verify.a.signed.JAR.file"));
      System.out.println();
      System.out.println(rb.getString(".verbose.suboptions.verbose.output.when.signing.verifying."));
      System.out.println(rb.getString(".suboptions.can.be.all.grouped.or.summary"));
      System.out.println();
      System.out.println(rb.getString(".certs.display.certificates.when.verbose.and.verifying"));
      System.out.println();
      System.out.println(rb.getString(".tsa.url.location.of.the.Timestamping.Authority"));
      System.out.println();
      System.out.println(rb.getString(".tsacert.alias.public.key.certificate.for.Timestamping.Authority"));
      System.out.println();
      System.out.println(rb.getString(".tsapolicyid.tsapolicyid.for.Timestamping.Authority"));
      System.out.println();
      System.out.println(rb.getString(".tsadigestalg.algorithm.of.digest.data.in.timestamping.request"));
      System.out.println();
      System.out.println(rb.getString(".altsigner.class.class.name.of.an.alternative.signing.mechanism"));
      System.out.println();
      System.out.println(rb.getString(".altsignerpath.pathlist.location.of.an.alternative.signing.mechanism"));
      System.out.println();
      System.out.println(rb.getString(".internalsf.include.the.SF.file.inside.the.signature.block"));
      System.out.println();
      System.out.println(rb.getString(".sectionsonly.don.t.compute.hash.of.entire.manifest"));
      System.out.println();
      System.out.println(rb.getString(".protected.keystore.has.protected.authentication.path"));
      System.out.println();
      System.out.println(rb.getString(".providerName.name.provider.name"));
      System.out.println();
      System.out.println(rb.getString(".providerClass.class.name.of.cryptographic.service.provider.s"));
      System.out.println(rb.getString(".providerArg.arg.master.class.file.and.constructor.argument"));
      System.out.println();
      System.out.println(rb.getString(".strict.treat.warnings.as.errors"));
      System.out.println();
      System.exit(0);
   }

   void verifyJar(String var1) throws Exception {
      boolean var2 = false;
      JarFile var3 = null;
      HashMap var4 = new HashMap();
      HashMap var5 = new HashMap();
      HashMap var6 = new HashMap();
      HashMap var7 = new HashMap();

      try {
         var3 = new JarFile(var1, true);
         Vector var8 = new Vector();
         byte[] var9 = new byte[8192];
         Enumeration var10 = var3.entries();

         String var15;
         String var20;
         while(var10.hasMoreElements()) {
            JarEntry var11 = (JarEntry)var10.nextElement();
            var8.addElement(var11);
            InputStream var12 = var3.getInputStream(var11);
            Throwable var13 = null;

            try {
               String var14 = var11.getName();
               if (this.signatureRelated(var14) && SignatureFileVerifier.isBlockOrSF(var14)) {
                  var15 = var14.substring(var14.lastIndexOf(47) + 1, var14.lastIndexOf(46));

                  try {
                     if (!var14.endsWith(".SF")) {
                        var6.put(var15, var14);
                        var5.put(var15, new PKCS7(var12));
                     } else {
                        Manifest var16 = new Manifest(var12);
                        boolean var17 = false;
                        Iterator var18 = var16.getMainAttributes().keySet().iterator();

                        while(var18.hasNext()) {
                           Object var19 = var18.next();
                           var20 = var19.toString();
                           if (var20.endsWith("-Digest-Manifest")) {
                              var4.put(var15, var20.substring(0, var20.length() - 16));
                              var17 = true;
                              break;
                           }
                        }

                        if (!var17) {
                           var7.putIfAbsent(var15, String.format(rb.getString("history.unparsable"), var14));
                        }
                     }
                  } catch (IOException var51) {
                     var7.putIfAbsent(var15, String.format(rb.getString("history.unparsable"), var14));
                  }
               } else {
                  while(var12.read(var9, 0, var9.length) != -1) {
                  }
               }
            } catch (Throwable var52) {
               var13 = var52;
               throw var52;
            } finally {
               if (var12 != null) {
                  if (var13 != null) {
                     try {
                        var12.close();
                     } catch (Throwable var49) {
                        var13.addSuppressed(var49);
                     }
                  } else {
                     var12.close();
                  }
               }

            }
         }

         Manifest var56 = var3.getManifest();
         boolean var57 = false;
         LinkedHashMap var58 = new LinkedHashMap();
         String var65;
         if (var56 != null) {
            if (this.verbose != null) {
               System.out.println();
            }

            Enumeration var59 = var8.elements();
            var15 = rb.getString("6SPACE");

            label1149:
            while(true) {
               StringBuffer var23;
               JarEntry var62;
               do {
                  if (!var59.hasMoreElements()) {
                     break label1149;
                  }

                  var62 = (JarEntry)var59.nextElement();
                  var65 = var62.getName();
                  var57 = var57 || SignatureFileVerifier.isBlockOrSF(var65);
                  CodeSigner[] var66 = var62.getCodeSigners();
                  boolean var69 = var66 != null;
                  var2 |= var69;
                  this.hasUnsignedEntry |= !var62.isDirectory() && !var69 && !this.signatureRelated(var65);
                  int var71 = this.inKeyStore(var66);
                  boolean var21 = (var71 & 1) != 0;
                  boolean var22 = (var71 & 2) != 0;
                  this.notSignedByAlias |= (var71 & 4) != 0;
                  if (this.keystore != null) {
                     this.aliasNotInStore |= var69 && !var21 && !var22;
                  }

                  var23 = null;
                  if (this.verbose != null) {
                     var23 = new StringBuffer();
                     boolean var24 = var56.getAttributes(var65) != null || var56.getAttributes("./" + var65) != null || var56.getAttributes("/" + var65) != null;
                     var23.append((var69 ? rb.getString("s") : rb.getString("SPACE")) + (var24 ? rb.getString("m") : rb.getString("SPACE")) + (var21 ? rb.getString("k") : rb.getString("SPACE")) + (var22 ? rb.getString("i") : rb.getString("SPACE")) + ((var71 & 4) != 0 ? "X" : " ") + rb.getString("SPACE"));
                     var23.append("|");
                  }

                  if (var69) {
                     if (this.showcerts) {
                        var23.append('\n');
                     }

                     CodeSigner[] var75 = var66;
                     int var25 = var66.length;

                     for(int var26 = 0; var26 < var25; ++var26) {
                        CodeSigner var27 = var75[var26];
                        String var28 = this.signerInfo(var27, var15);
                        if (this.showcerts) {
                           var23.append(var28);
                           var23.append('\n');
                        }
                     }
                  } else if (this.showcerts && !this.verbose.equals("all")) {
                     if (this.signatureRelated(var65)) {
                        var23.append("\n" + var15 + rb.getString(".Signature.related.entries.") + "\n\n");
                     } else {
                        var23.append("\n" + var15 + rb.getString(".Unsigned.entries.") + "\n\n");
                     }
                  }
               } while(this.verbose == null);

               String var76 = var23.toString();
               if (this.signatureRelated(var65)) {
                  var76 = "-" + var76;
               }

               if (!var58.containsKey(var76)) {
                  var58.put(var76, new ArrayList());
               }

               StringBuffer var78 = new StringBuffer();
               String var81 = Long.toString(var62.getSize());

               for(int var83 = 6 - var81.length(); var83 > 0; --var83) {
                  var78.append(' ');
               }

               var78.append(var81).append(' ').append((new Date(var62.getTime())).toString());
               var78.append(' ').append(var65);
               ((List)var58.get(var76)).add(var78.toString());
            }
         }

         Iterator var60;
         if (this.verbose != null) {
            var60 = var58.entrySet().iterator();

            while(var60.hasNext()) {
               Map.Entry var61 = (Map.Entry)var60.next();
               List var63 = (List)var61.getValue();
               var65 = (String)var61.getKey();
               if (var65.charAt(0) == '-') {
                  var65 = var65.substring(1);
               }

               int var67 = var65.indexOf(124);
               Iterator var70;
               if (this.verbose.equals("all")) {
                  var70 = var63.iterator();

                  while(var70.hasNext()) {
                     var20 = (String)var70.next();
                     System.out.println(var65.substring(0, var67) + var20);
                     System.out.printf(var65.substring(var67 + 1));
                  }
               } else {
                  if (this.verbose.equals("grouped")) {
                     var70 = var63.iterator();

                     while(var70.hasNext()) {
                        var20 = (String)var70.next();
                        System.out.println(var65.substring(0, var67) + var20);
                     }
                  } else if (this.verbose.equals("summary")) {
                     System.out.print(var65.substring(0, var67));
                     if (var63.size() > 1) {
                        System.out.println((String)var63.get(0) + " " + String.format(rb.getString(".and.d.more."), var63.size() - 1));
                     } else {
                        System.out.println((String)var63.get(0));
                     }
                  }

                  System.out.printf(var65.substring(var67 + 1));
               }
            }

            System.out.println();
            System.out.println(rb.getString(".s.signature.was.verified."));
            System.out.println(rb.getString(".m.entry.is.listed.in.manifest"));
            System.out.println(rb.getString(".k.at.least.one.certificate.was.found.in.keystore"));
            System.out.println(rb.getString(".i.at.least.one.certificate.was.found.in.identity.scope"));
            if (this.ckaliases.size() > 0) {
               System.out.println(rb.getString(".X.not.signed.by.specified.alias.es."));
            }
         }

         if (var56 == null) {
            System.out.println();
            System.out.println(rb.getString("no.manifest."));
         }

         if (!var4.isEmpty() || !var5.isEmpty() || !var7.isEmpty()) {
            if (this.verbose != null) {
               System.out.println();
            }

            var60 = var5.keySet().iterator();

            while(var60.hasNext()) {
               var15 = (String)var60.next();
               if (!var4.containsKey(var15)) {
                  var7.putIfAbsent(var15, String.format(rb.getString("history.nosf"), var15));
               }
            }

            var60 = var4.keySet().iterator();

            while(var60.hasNext()) {
               var15 = (String)var60.next();
               PKCS7 var64 = (PKCS7)var5.get(var15);
               if (var64 != null) {
                  try {
                     SignerInfo var68 = var64.getSignerInfos()[0];
                     X509Certificate var72 = var68.getCertificate(var64);
                     var20 = (String)var4.get(var15);
                     String var73 = AlgorithmId.makeSigAlg(var68.getDigestAlgorithmId().getName(), var68.getDigestEncryptionAlgorithmId().getName());
                     PublicKey var74 = var72.getPublicKey();
                     PKCS7 var77 = var68.getTsToken();
                     if (var77 != null) {
                        this.hasTimestampBlock = true;
                        SignerInfo var79 = var77.getSignerInfos()[0];
                        X509Certificate var80 = var79.getCertificate(var77);
                        byte[] var82 = var77.getContentInfo().getData();
                        TimestampToken var84 = new TimestampToken(var82);
                        PublicKey var85 = var80.getPublicKey();
                        String var29 = var84.getHashAlgorithm().getName();
                        String var30 = AlgorithmId.makeSigAlg(var79.getDigestAlgorithmId().getName(), var79.getDigestEncryptionAlgorithmId().getName());
                        Calendar var31 = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault(Category.FORMAT));
                        var31.setTime(var84.getDate());
                        var65 = String.format(rb.getString("history.with.ts"), var72.getSubjectX500Principal(), this.withWeak(var20, DIGEST_PRIMITIVE_SET), this.withWeak(var73, SIG_PRIMITIVE_SET), this.withWeak(var74), var31, var80.getSubjectX500Principal(), this.withWeak(var29, DIGEST_PRIMITIVE_SET), this.withWeak(var30, SIG_PRIMITIVE_SET), this.withWeak(var85));
                     } else {
                        var65 = String.format(rb.getString("history.without.ts"), var72.getSubjectX500Principal(), this.withWeak(var20, DIGEST_PRIMITIVE_SET), this.withWeak(var73, SIG_PRIMITIVE_SET), this.withWeak(var74));
                     }
                  } catch (Exception var50) {
                     var65 = String.format(rb.getString("history.unparsable"), var6.get(var15));
                  }

                  if (this.verbose != null) {
                     System.out.println(var65);
                  }
               } else {
                  var7.putIfAbsent(var15, String.format(rb.getString("history.nobk"), var15));
               }
            }

            if (this.verbose != null) {
               var60 = var7.keySet().iterator();

               while(var60.hasNext()) {
                  var15 = (String)var60.next();
                  System.out.println((String)var7.get(var15));
               }
            }
         }

         System.out.println();
         if (!this.aliasNotInStore && this.keystore != null) {
            this.signerSelfSigned = false;
         }

         if (!var2) {
            if (this.seeWeak) {
               if (this.verbose != null) {
                  System.out.println(rb.getString("jar.treated.unsigned.see.weak.verbose"));
                  System.out.println("\n  jdk.jar.disabledAlgorithms=" + Security.getProperty("jdk.jar.disabledAlgorithms"));
               } else {
                  System.out.println(rb.getString("jar.treated.unsigned.see.weak"));
               }

               return;
            } else {
               if (var57) {
                  System.out.println(rb.getString("jar.treated.unsigned"));
               } else {
                  System.out.println(rb.getString("jar.is.unsigned"));
               }

               return;
            }
         } else {
            this.displayMessagesAndResult(false);
            return;
         }
      } catch (Exception var54) {
         System.out.println(rb.getString("jarsigner.") + var54);
         if (this.debug) {
            var54.printStackTrace();
         }
      } finally {
         if (var3 != null) {
            var3.close();
         }

      }

      System.exit(1);
   }

   private void displayMessagesAndResult(boolean var1) {
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      boolean var6 = this.expireDate == null || this.expireDate.after(new Date());
      String var2;
      if (this.badKeyUsage || this.badExtendedKeyUsage || this.badNetscapeCertType || this.notYetValidCert || this.chainNotValidated || this.hasExpiredCert || this.hasUnsignedEntry || this.signerSelfSigned || this.weakAlg != 0 || this.aliasNotInStore || this.notSignedByAlias || this.tsaChainNotValidated || this.hasExpiredTsaCert && !var6) {
         if (this.strict) {
            var2 = rb.getString(var1 ? "jar.signed.with.signer.errors." : "jar.verified.with.signer.errors.");
         } else {
            var2 = rb.getString(var1 ? "jar.signed." : "jar.verified.");
         }

         if (this.badKeyUsage) {
            var3.add(rb.getString(var1 ? "The.signer.certificate.s.KeyUsage.extension.doesn.t.allow.code.signing." : "This.jar.contains.entries.whose.signer.certificate.s.KeyUsage.extension.doesn.t.allow.code.signing."));
         }

         if (this.badExtendedKeyUsage) {
            var3.add(rb.getString(var1 ? "The.signer.certificate.s.ExtendedKeyUsage.extension.doesn.t.allow.code.signing." : "This.jar.contains.entries.whose.signer.certificate.s.ExtendedKeyUsage.extension.doesn.t.allow.code.signing."));
         }

         if (this.badNetscapeCertType) {
            var3.add(rb.getString(var1 ? "The.signer.certificate.s.NetscapeCertType.extension.doesn.t.allow.code.signing." : "This.jar.contains.entries.whose.signer.certificate.s.NetscapeCertType.extension.doesn.t.allow.code.signing."));
         }

         if (this.hasUnsignedEntry) {
            var3.add(rb.getString("This.jar.contains.unsigned.entries.which.have.not.been.integrity.checked."));
         }

         if (this.hasExpiredCert) {
            var3.add(rb.getString(var1 ? "The.signer.certificate.has.expired." : "This.jar.contains.entries.whose.signer.certificate.has.expired."));
         }

         if (this.notYetValidCert) {
            var3.add(rb.getString(var1 ? "The.signer.certificate.is.not.yet.valid." : "This.jar.contains.entries.whose.signer.certificate.is.not.yet.valid."));
         }

         if (this.chainNotValidated) {
            var3.add(String.format(rb.getString(var1 ? "The.signer.s.certificate.chain.is.invalid.reason.1" : "This.jar.contains.entries.whose.certificate.chain.is.invalid.reason.1"), this.chainNotValidatedReason.getLocalizedMessage()));
         }

         if (this.hasExpiredTsaCert) {
            var3.add(rb.getString("The.timestamp.has.expired."));
         }

         if (this.tsaChainNotValidated) {
            var3.add(String.format(rb.getString(var1 ? "The.tsa.certificate.chain.is.invalid.reason.1" : "This.jar.contains.entries.whose.tsa.certificate.chain.is.invalid.reason.1"), this.tsaChainNotValidatedReason.getLocalizedMessage()));
         }

         if (this.notSignedByAlias) {
            var3.add(rb.getString("This.jar.contains.signed.entries.which.is.not.signed.by.the.specified.alias.es."));
         }

         if (this.aliasNotInStore) {
            var3.add(rb.getString("This.jar.contains.signed.entries.that.s.not.signed.by.alias.in.this.keystore."));
         }

         if (this.signerSelfSigned) {
            var3.add(rb.getString(var1 ? "The.signer.s.certificate.is.self.signed." : "This.jar.contains.entries.whose.signer.certificate.is.self.signed."));
         }

         if ((this.weakAlg & 1) == 1) {
            var3.add(String.format(rb.getString("The.1.algorithm.specified.for.the.2.option.is.considered.a.security.risk."), this.digestalg, "-digestalg"));
         }

         if ((this.weakAlg & 2) == 2) {
            var3.add(String.format(rb.getString("The.1.algorithm.specified.for.the.2.option.is.considered.a.security.risk."), this.sigalg, "-sigalg"));
         }

         if ((this.weakAlg & 4) == 4) {
            var3.add(String.format(rb.getString("The.1.algorithm.specified.for.the.2.option.is.considered.a.security.risk."), this.tSADigestAlg, "-tsadigestalg"));
         }

         if ((this.weakAlg & 8) == 8) {
            var3.add(String.format(rb.getString("The.1.signing.key.has.a.keysize.of.2.which.is.considered.a.security.risk."), this.privateKey.getAlgorithm(), KeyUtil.getKeySize(this.privateKey)));
         }
      } else {
         var2 = rb.getString(var1 ? "jar.signed." : "jar.verified.");
      }

      if (this.hasExpiredTsaCert) {
         this.hasExpiringTsaCert = false;
      }

      if (this.hasExpiringCert || this.hasExpiringTsaCert && this.expireDate != null || this.noTimestamp && this.expireDate != null || this.hasExpiredTsaCert && var6) {
         if (this.hasExpiredTsaCert && var6) {
            if (this.expireDate != null) {
               var4.add(String.format(rb.getString("The.timestamp.expired.1.but.usable.2"), this.tsaExpireDate, this.expireDate));
            }

            this.hasExpiredTsaCert = false;
         }

         if (this.hasExpiringCert) {
            var4.add(rb.getString(var1 ? "The.signer.certificate.will.expire.within.six.months." : "This.jar.contains.entries.whose.signer.certificate.will.expire.within.six.months."));
         }

         if (this.hasExpiringTsaCert && this.expireDate != null) {
            if (this.expireDate.after(this.tsaExpireDate)) {
               var4.add(String.format(rb.getString("The.timestamp.will.expire.within.one.year.on.1.but.2"), this.tsaExpireDate, this.expireDate));
            } else {
               var4.add(String.format(rb.getString("The.timestamp.will.expire.within.one.year.on.1"), this.tsaExpireDate));
            }
         }

         if (this.noTimestamp && this.expireDate != null) {
            if (this.hasTimestampBlock) {
               var4.add(String.format(rb.getString(var1 ? "invalid.timestamp.signing" : "bad.timestamp.verifying"), this.expireDate));
            } else {
               var4.add(String.format(rb.getString(var1 ? "no.timestamp.signing" : "no.timestamp.verifying"), this.expireDate));
            }
         }
      }

      System.out.println(var2);
      PrintStream var10001;
      if (this.strict) {
         if (!var3.isEmpty()) {
            System.out.println();
            System.out.println(rb.getString("Error."));
            var10001 = System.out;
            var3.forEach(var10001::println);
         }

         if (!var4.isEmpty()) {
            System.out.println();
            System.out.println(rb.getString("Warning."));
            var10001 = System.out;
            var4.forEach(var10001::println);
         }
      } else if (!var3.isEmpty() || !var4.isEmpty()) {
         System.out.println();
         System.out.println(rb.getString("Warning."));
         var10001 = System.out;
         var3.forEach(var10001::println);
         var10001 = System.out;
         var4.forEach(var10001::println);
      }

      if (!var1 && (!var3.isEmpty() || !var4.isEmpty()) && (this.verbose == null || !this.showcerts)) {
         System.out.println();
         System.out.println(rb.getString("Re.run.with.the.verbose.and.certs.options.for.more.details."));
      }

      if (var1 || this.verbose != null) {
         if (!this.hasExpiringCert && !this.hasExpiredCert && this.expireDate != null && var6) {
            var5.add(String.format(rb.getString("The.signer.certificate.will.expire.on.1."), this.expireDate));
         }

         if (!this.noTimestamp && !this.hasExpiringTsaCert && !this.hasExpiredTsaCert && this.tsaExpireDate != null) {
            if (var6) {
               var5.add(String.format(rb.getString("The.timestamp.will.expire.on.1."), this.tsaExpireDate));
            } else {
               var5.add(String.format(rb.getString("signer.cert.expired.1.but.timestamp.good.2."), this.expireDate, this.tsaExpireDate));
            }
         }
      }

      if (!var5.isEmpty()) {
         System.out.println();
         var10001 = System.out;
         var5.forEach(var10001::println);
      }

   }

   private String withWeak(String var1, Set var2) {
      if (DISABLED_CHECK.permits(var2, var1, (AlgorithmParameters)null)) {
         return var1;
      } else {
         this.seeWeak = true;
         return String.format(rb.getString("with.weak"), var1);
      }
   }

   private String withWeak(PublicKey var1) {
      if (DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, var1)) {
         return String.format(rb.getString("key.bit"), KeyUtil.getKeySize(var1));
      } else {
         this.seeWeak = true;
         return String.format(rb.getString("key.bit.weak"), KeyUtil.getKeySize(var1));
      }
   }

   String printCert(boolean var1, String var2, Certificate var3, Date var4, boolean var5) throws Exception {
      StringBuilder var6 = new StringBuilder();
      String var7 = rb.getString("SPACE");
      X509Certificate var8 = null;
      if (var3 instanceof X509Certificate) {
         var8 = (X509Certificate)var3;
         var6.append(var2).append(var8.getType()).append(rb.getString("COMMA")).append(var8.getSubjectDN().getName());
      } else {
         var6.append(var2).append(var3.getType());
      }

      String var9 = (String)this.storeHash.get(var3);
      if (var9 != null) {
         var6.append(var7).append(var9);
      }

      if (var8 != null) {
         var6.append("\n").append(var2).append("[");
         if (this.trustedCerts.contains(var8)) {
            var6.append(rb.getString("trusted.certificate"));
         } else {
            Date var10 = var8.getNotAfter();

            Object[] var12;
            try {
               boolean var11 = true;
               if (var1) {
                  if (this.tsaExpireDate == null || this.tsaExpireDate.after(var10)) {
                     this.tsaExpireDate = var10;
                  }
               } else if (this.expireDate == null || this.expireDate.after(var10)) {
                  this.expireDate = var10;
               }

               if (var4 == null) {
                  var8.checkValidity();
                  long var19 = var1 ? 31622400000L : 15552000000L;
                  if (var10.getTime() < System.currentTimeMillis() + var19) {
                     if (var1) {
                        this.hasExpiringTsaCert = true;
                     } else {
                        this.hasExpiringCert = true;
                     }

                     if (expiringTimeForm == null) {
                        expiringTimeForm = new MessageFormat(rb.getString("certificate.will.expire.on"));
                     }

                     Object[] var14 = new Object[]{var10};
                     var6.append(expiringTimeForm.format(var14));
                     var11 = false;
                  }
               } else {
                  var8.checkValidity(var4);
               }

               if (var11) {
                  if (validityTimeForm == null) {
                     validityTimeForm = new MessageFormat(rb.getString("certificate.is.valid.from"));
                  }

                  var12 = new Object[]{var8.getNotBefore(), var10};
                  var6.append(validityTimeForm.format(var12));
               }
            } catch (CertificateExpiredException var15) {
               if (var1) {
                  this.hasExpiredTsaCert = true;
               } else {
                  this.hasExpiredCert = true;
               }

               if (expiredTimeForm == null) {
                  expiredTimeForm = new MessageFormat(rb.getString("certificate.expired.on"));
               }

               var12 = new Object[]{var10};
               var6.append(expiredTimeForm.format(var12));
            } catch (CertificateNotYetValidException var16) {
               if (!var1) {
                  this.notYetValidCert = true;
               }

               if (notYetTimeForm == null) {
                  notYetTimeForm = new MessageFormat(rb.getString("certificate.is.not.valid.until"));
               }

               var12 = new Object[]{var8.getNotBefore()};
               var6.append(notYetTimeForm.format(var12));
            }
         }

         var6.append("]");
         if (var5) {
            boolean[] var17 = new boolean[3];
            this.checkCertUsage(var8, var17);
            if (var17[0] || var17[1] || var17[2]) {
               String var18 = "";
               if (var17[0]) {
                  var18 = "KeyUsage";
               }

               if (var17[1]) {
                  if (var18.length() > 0) {
                     var18 = var18 + ", ";
                  }

                  var18 = var18 + "ExtendedKeyUsage";
               }

               if (var17[2]) {
                  if (var18.length() > 0) {
                     var18 = var18 + ", ";
                  }

                  var18 = var18 + "NetscapeCertType";
               }

               var6.append("\n").append(var2).append(MessageFormat.format(rb.getString(".{0}.extension.does.not.support.code.signing."), var18));
            }
         }
      }

      return var6.toString();
   }

   private String printTimestamp(String var1, Timestamp var2) {
      if (signTimeForm == null) {
         signTimeForm = new MessageFormat(rb.getString("entry.was.signed.on"));
      }

      Object[] var3 = new Object[]{var2.getTimestamp()};
      return var1 + "[" + signTimeForm.format(var3) + "]";
   }

   private int inKeyStoreForOneSigner(CodeSigner var1) {
      if (this.cacheForInKS.containsKey(var1)) {
         return (Integer)this.cacheForInKS.get(var1);
      } else {
         boolean var2 = false;
         int var3 = 0;
         List var4 = var1.getSignerCertPath().getCertificates();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Certificate var6 = (Certificate)var5.next();
            String var7 = (String)this.storeHash.get(var6);
            if (var7 != null) {
               if (var7.startsWith("(")) {
                  var3 |= 1;
               } else if (var7.startsWith("[")) {
                  var3 |= 2;
               }

               if (this.ckaliases.contains(var7.substring(1, var7.length() - 1))) {
                  var3 |= 8;
               }
            } else {
               if (this.store != null) {
                  try {
                     var7 = this.store.getCertificateAlias(var6);
                  } catch (KeyStoreException var9) {
                  }

                  if (var7 != null) {
                     this.storeHash.put(var6, "(" + var7 + ")");
                     var2 = true;
                     var3 |= 1;
                  }
               }

               if (this.ckaliases.contains(var7)) {
                  var3 |= 8;
               }
            }
         }

         this.cacheForInKS.put(var1, var3);
         return var3;
      }
   }

   int inKeyStore(CodeSigner[] var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;
         CodeSigner[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CodeSigner var6 = var3[var5];
            int var7 = this.inKeyStoreForOneSigner(var6);
            var2 |= var7;
         }

         if (this.ckaliases.size() > 0 && (var2 & 8) == 0) {
            var2 |= 4;
         }

         return var2;
      }
   }

   void signJar(String var1, String var2, String[] var3) throws Exception {
      DisabledAlgorithmConstraints var4 = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
      if (this.digestalg != null && !var4.permits(Collections.singleton(CryptoPrimitive.MESSAGE_DIGEST), this.digestalg, (AlgorithmParameters)null)) {
         this.weakAlg |= 1;
      }

      if (this.tSADigestAlg != null && !var4.permits(Collections.singleton(CryptoPrimitive.MESSAGE_DIGEST), this.tSADigestAlg, (AlgorithmParameters)null)) {
         this.weakAlg |= 4;
      }

      if (this.sigalg != null && !var4.permits(Collections.singleton(CryptoPrimitive.SIGNATURE), this.sigalg, (AlgorithmParameters)null)) {
         this.weakAlg |= 2;
      }

      boolean var5 = false;
      X509Certificate var6 = null;
      if (this.sigfile == null) {
         this.sigfile = var2;
         var5 = true;
      }

      if (this.sigfile.length() > 8) {
         this.sigfile = this.sigfile.substring(0, 8).toUpperCase(Locale.ENGLISH);
      } else {
         this.sigfile = this.sigfile.toUpperCase(Locale.ENGLISH);
      }

      StringBuilder var7 = new StringBuilder(this.sigfile.length());

      for(int var8 = 0; var8 < this.sigfile.length(); ++var8) {
         char var9 = this.sigfile.charAt(var8);
         if ((var9 < 'A' || var9 > 'Z') && (var9 < '0' || var9 > '9') && var9 != '-' && var9 != '_') {
            if (!var5) {
               throw new RuntimeException(rb.getString("signature.filename.must.consist.of.the.following.characters.A.Z.0.9.or."));
            }

            var9 = '_';
         }

         var7.append(var9);
      }

      this.sigfile = var7.toString();
      String var69;
      if (this.signedjar == null) {
         var69 = var1 + ".sig";
      } else {
         var69 = this.signedjar;
      }

      File var70 = new File(var1);
      File var10 = new File(var69);

      try {
         this.zipFile = new ZipFile(var1);
      } catch (IOException var64) {
         this.error(rb.getString("unable.to.open.jar.file.") + var1, var64);
      }

      FileOutputStream var11 = null;

      try {
         var11 = new FileOutputStream(var10);
      } catch (IOException var63) {
         this.error(rb.getString("unable.to.create.") + var69, var63);
      }

      PrintStream var12 = new PrintStream(var11);
      ZipOutputStream var13 = new ZipOutputStream(var12);
      String var14 = ("META-INF/" + this.sigfile + ".SF").toUpperCase(Locale.ENGLISH);
      String var15 = ("META-INF/" + this.sigfile + ".DSA").toUpperCase(Locale.ENGLISH);
      Manifest var16 = new Manifest();
      Map var17 = var16.getEntries();
      Attributes var18 = null;
      boolean var19 = false;
      boolean var20 = false;
      byte[] var21 = null;

      String var26;
      try {
         MessageDigest[] var22 = new MessageDigest[]{MessageDigest.getInstance(this.digestalg)};
         ZipEntry var23;
         if ((var23 = this.getManifestFile(this.zipFile)) != null) {
            var21 = this.getBytes(this.zipFile, var23);
            var16.read(new ByteArrayInputStream(var21));
            var18 = (Attributes)((Attributes)var16.getMainAttributes().clone());
         } else {
            Attributes var24 = var16.getMainAttributes();
            var24.putValue(Name.MANIFEST_VERSION.toString(), "1.0");
            String var25 = System.getProperty("java.vendor");
            var26 = System.getProperty("java.version");
            var24.putValue("Created-By", var26 + " (" + var25 + ")");
            var23 = new ZipEntry("META-INF/MANIFEST.MF");
            var20 = true;
         }

         Vector var73 = new Vector();
         boolean var77 = false;
         Enumeration var80 = this.zipFile.entries();

         label980:
         while(true) {
            ZipEntry var27;
            do {
               if (!var80.hasMoreElements()) {
                  if (var19) {
                     ByteArrayOutputStream var81 = new ByteArrayOutputStream();
                     var16.write(var81);
                     if (var77) {
                        byte[] var83 = var81.toByteArray();
                        if (var21 != null && var18.equals(var16.getMainAttributes())) {
                           int var85 = this.findHeaderEnd(var83);
                           int var29 = this.findHeaderEnd(var21);
                           if (var85 == var29) {
                              System.arraycopy(var21, 0, var83, 0, var29);
                           } else {
                              byte[] var30 = new byte[var29 + var83.length - var85];
                              System.arraycopy(var21, 0, var30, 0, var29);
                              System.arraycopy(var83, var85, var30, var29, var83.length - var85);
                              var83 = var30;
                           }
                        }

                        var21 = var83;
                     } else {
                        var21 = var81.toByteArray();
                     }
                  }

                  if (var19) {
                     var23 = new ZipEntry("META-INF/MANIFEST.MF");
                  }

                  if (this.verbose != null) {
                     if (var20) {
                        System.out.println(rb.getString(".adding.") + var23.getName());
                     } else if (var19) {
                        System.out.println(rb.getString(".updating.") + var23.getName());
                     }
                  }

                  var13.putNextEntry(var23);
                  var13.write(var21);
                  ManifestDigester var82 = new ManifestDigester(var21);
                  SignatureFile var86 = new SignatureFile(var22, var16, var82, this.sigfile, this.signManifest);
                  if (this.tsaAlias != null) {
                     var6 = this.getTsaCert(this.tsaAlias);
                  }

                  if (this.tsaUrl == null && var6 == null) {
                     this.noTimestamp = true;
                  }

                  SignatureFile.Block var87 = null;

                  try {
                     var87 = var86.generateBlock(this.privateKey, this.sigalg, this.certChain, this.externalSF, this.tsaUrl, var6, this.tSAPolicyID, this.tSADigestAlg, this.signingMechanism, var3, this.zipFile);
                  } catch (SocketTimeoutException var62) {
                     this.error(rb.getString("unable.to.sign.jar.") + rb.getString("no.response.from.the.Timestamping.Authority.") + "\n  -J-Dhttp.proxyHost=<hostname>\n  -J-Dhttp.proxyPort=<portnumber>\n" + rb.getString("or") + "\n  -J-Dhttps.proxyHost=<hostname> \n  -J-Dhttps.proxyPort=<portnumber> ", var62);
                  }

                  var14 = var86.getMetaName();
                  var15 = var87.getMetaName();
                  ZipEntry var88 = new ZipEntry(var14);
                  ZipEntry var89 = new ZipEntry(var15);
                  long var31 = System.currentTimeMillis();
                  var88.setTime(var31);
                  var89.setTime(var31);
                  var13.putNextEntry(var88);
                  var86.write(var13);
                  if (this.verbose != null) {
                     if (this.zipFile.getEntry(var14) != null) {
                        System.out.println(rb.getString(".updating.") + var14);
                     } else {
                        System.out.println(rb.getString(".adding.") + var14);
                     }
                  }

                  if (this.verbose != null) {
                     if (this.tsaUrl != null || var6 != null) {
                        System.out.println(rb.getString("requesting.a.signature.timestamp"));
                     }

                     if (this.tsaUrl != null) {
                        System.out.println(rb.getString("TSA.location.") + this.tsaUrl);
                     }

                     if (var6 != null) {
                        URI var33 = TimestampedSigner.getTimestampingURI(var6);
                        if (var33 != null) {
                           System.out.println(rb.getString("TSA.location.") + var33);
                        }

                        System.out.println(rb.getString("TSA.certificate.") + this.printCert(true, "", var6, (Date)null, false));
                     }

                     if (this.signingMechanism != null) {
                        System.out.println(rb.getString("using.an.alternative.signing.mechanism"));
                     }
                  }

                  var13.putNextEntry(var89);
                  var87.write(var13);
                  if (this.verbose != null) {
                     if (this.zipFile.getEntry(var15) != null) {
                        System.out.println(rb.getString(".updating.") + var15);
                     } else {
                        System.out.println(rb.getString(".adding.") + var15);
                     }
                  }

                  ZipEntry var34;
                  for(int var90 = 0; var90 < var73.size(); ++var90) {
                     var34 = (ZipEntry)var73.elementAt(var90);
                     if (!var34.getName().equalsIgnoreCase("META-INF/MANIFEST.MF") && !var34.getName().equalsIgnoreCase(var14) && !var34.getName().equalsIgnoreCase(var15)) {
                        this.writeEntry(this.zipFile, var13, var34);
                     }
                  }

                  Enumeration var91 = this.zipFile.entries();

                  while(var91.hasMoreElements()) {
                     var34 = (ZipEntry)var91.nextElement();
                     if (!var34.getName().startsWith("META-INF/")) {
                        if (this.verbose != null) {
                           if (var16.getAttributes(var34.getName()) != null) {
                              System.out.println(rb.getString(".signing.") + var34.getName());
                           } else {
                              System.out.println(rb.getString(".adding.") + var34.getName());
                           }
                        }

                        this.writeEntry(this.zipFile, var13, var34);
                     }
                  }
                  break label980;
               }

               var27 = (ZipEntry)var80.nextElement();
               if (!var27.getName().startsWith("META-INF/")) {
                  break;
               }

               var73.addElement(var27);
               if (SignatureFileVerifier.isBlockOrSF(var27.getName().toUpperCase(Locale.ENGLISH))) {
                  var77 = true;
               }
            } while(this.signatureRelated(var27.getName()));

            if (var16.getAttributes(var27.getName()) != null) {
               if (this.updateDigests(var27, this.zipFile, var22, var16)) {
                  var19 = true;
               }
            } else if (!var27.isDirectory()) {
               Attributes var28 = this.getDigestAttributes(var27, this.zipFile, var22);
               var17.put(var27.getName(), var28);
               var19 = true;
            }
         }
      } catch (IOException var67) {
         this.error(rb.getString("unable.to.sign.jar.") + var67, var67);
      } finally {
         if (this.zipFile != null) {
            this.zipFile.close();
            this.zipFile = null;
         }

         if (var13 != null) {
            var13.close();
         }

      }

      try {
         JarFile var71 = new JarFile(var10);
         Throwable var75 = null;

         try {
            PKCS7 var74 = new PKCS7(var71.getInputStream(var71.getEntry("META-INF/" + this.sigfile + "." + this.privateKey.getAlgorithm())));
            Timestamp var78 = null;

            try {
               SignerInfo var84 = var74.getSignerInfos()[0];
               if (var84.getTsToken() != null) {
                  this.hasTimestampBlock = true;
               }

               var78 = var84.getTimestamp();
            } catch (Exception var60) {
               this.tsaChainNotValidated = true;
               this.tsaChainNotValidatedReason = var60;
            }

            var26 = this.certsAndTSInfo("", "    ", Arrays.asList(this.certChain), var78);
            if (this.verbose != null) {
               System.out.println(var26);
            }
         } catch (Throwable var61) {
            var75 = var61;
            throw var61;
         } finally {
            if (var71 != null) {
               if (var75 != null) {
                  try {
                     var71.close();
                  } catch (Throwable var59) {
                     var75.addSuppressed(var59);
                  }
               } else {
                  var71.close();
               }
            }

         }
      } catch (Exception var66) {
         if (this.debug) {
            var66.printStackTrace();
         }
      }

      if (this.signedjar == null && !var10.renameTo(var70)) {
         File var72 = new File(var1 + ".orig");
         Object[] var76;
         MessageFormat var79;
         if (var70.renameTo(var72)) {
            if (var10.renameTo(var70)) {
               var72.delete();
            } else {
               var79 = new MessageFormat(rb.getString("attempt.to.rename.signedJarFile.to.jarFile.failed"));
               var76 = new Object[]{var10, var70};
               this.error(var79.format(var76));
            }
         } else {
            var79 = new MessageFormat(rb.getString("attempt.to.rename.jarFile.to.origJar.failed"));
            var76 = new Object[]{var70, var72};
            this.error(var79.format(var76));
         }
      }

      this.displayMessagesAndResult(true);
   }

   private int findHeaderEnd(byte[] var1) {
      boolean var2 = true;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         switch (var1[var4]) {
            case 13:
               if (var4 < var3 - 1 && var1[var4 + 1] == 10) {
                  ++var4;
               }
            case 10:
               if (var2) {
                  return var4 + 1;
               }

               var2 = true;
               break;
            default:
               var2 = false;
         }
      }

      return var3;
   }

   private boolean signatureRelated(String var1) {
      return SignatureFileVerifier.isSigningRelated(var1);
   }

   private String signerInfo(CodeSigner var1, String var2) throws Exception {
      if (this.cacheForSignerInfo.containsKey(var1)) {
         return (String)this.cacheForSignerInfo.get(var1);
      } else {
         List var3 = var1.getSignerCertPath().getCertificates();
         Timestamp var4 = var1.getTimestamp();
         String var5 = "";
         if (var4 != null) {
            var5 = this.printTimestamp(var2, var4) + "\n";
         }

         String var6 = this.certsAndTSInfo(var2, var2, var3, var4);
         this.cacheForSignerInfo.put(var1, var5 + var6);
         return var6;
      }
   }

   private String certsAndTSInfo(String var1, String var2, List var3, Timestamp var4) throws Exception {
      Date var5;
      if (var4 != null) {
         var5 = var4.getTimestamp();
         this.noTimestamp = false;
      } else {
         var5 = null;
      }

      boolean var6 = true;
      StringBuilder var7 = new StringBuilder();
      var7.append(var1).append(rb.getString("...Signer")).append('\n');

      Iterator var8;
      Certificate var9;
      for(var8 = var3.iterator(); var8.hasNext(); var6 = false) {
         var9 = (Certificate)var8.next();
         var7.append(this.printCert(false, var2, var9, var5, var6));
         var7.append('\n');
      }

      try {
         this.validateCertChain("code signing", var3, var4);
      } catch (Exception var11) {
         this.chainNotValidated = true;
         this.chainNotValidatedReason = var11;
         var7.append(var2).append(rb.getString(".Invalid.certificate.chain.")).append(var11.getLocalizedMessage()).append("]\n");
      }

      if (var4 != null) {
         var7.append(var1).append(rb.getString("...TSA")).append('\n');
         var8 = var4.getSignerCertPath().getCertificates().iterator();

         while(var8.hasNext()) {
            var9 = (Certificate)var8.next();
            var7.append(this.printCert(true, var2, var9, (Date)null, false));
            var7.append('\n');
         }

         try {
            this.validateCertChain("tsa server", var4.getSignerCertPath().getCertificates(), (Timestamp)null);
         } catch (Exception var10) {
            this.tsaChainNotValidated = true;
            this.tsaChainNotValidatedReason = var10;
            var7.append(var2).append(rb.getString(".Invalid.TSA.certificate.chain.")).append(var10.getLocalizedMessage()).append("]\n");
         }
      }

      if (var3.size() == 1 && KeyStoreUtil.isSelfSigned((X509Certificate)var3.get(0))) {
         this.signerSelfSigned = true;
      }

      return var7.toString();
   }

   private void writeEntry(ZipFile var1, ZipOutputStream var2, ZipEntry var3) throws IOException {
      ZipEntry var4 = new ZipEntry(var3.getName());
      var4.setMethod(var3.getMethod());
      var4.setTime(var3.getTime());
      var4.setComment(var3.getComment());
      var4.setExtra(var3.getExtra());
      if (var3.getMethod() == 0) {
         var4.setSize(var3.getSize());
         var4.setCrc(var3.getCrc());
      }

      var2.putNextEntry(var4);
      this.writeBytes(var1, var3, var2);
   }

   private synchronized void writeBytes(ZipFile var1, ZipEntry var2, ZipOutputStream var3) throws IOException {
      InputStream var5 = null;

      try {
         var5 = var1.getInputStream(var2);

         int var4;
         for(long var6 = var2.getSize(); var6 > 0L && (var4 = var5.read(this.buffer, 0, this.buffer.length)) != -1; var6 -= (long)var4) {
            var3.write(this.buffer, 0, var4);
         }
      } finally {
         if (var5 != null) {
            var5.close();
         }

      }

   }

   void loadKeyStore(String var1, boolean var2) {
      if (!this.nullStream && var1 == null) {
         var1 = System.getProperty("user.home") + File.separator + ".keystore";
      }

      try {
         KeyStore var3;
         try {
            var3 = KeyStoreUtil.getCacertsKeyStore();
            if (var3 != null) {
               Enumeration var4 = var3.aliases();

               while(var4.hasMoreElements()) {
                  String var5 = (String)var4.nextElement();

                  try {
                     this.trustedCerts.add((X509Certificate)var3.getCertificate(var5));
                  } catch (Exception var37) {
                  }
               }
            }
         } catch (Exception var41) {
         }

         if (this.providerName == null) {
            this.store = KeyStore.getInstance(this.storetype);
         } else {
            this.store = KeyStore.getInstance(this.storetype, this.providerName);
         }

         if (this.token && this.storepass == null && !this.protectedPath && !KeyStoreUtil.isWindowsKeyStore(this.storetype)) {
            this.storepass = this.getPass(rb.getString("Enter.Passphrase.for.keystore."));
         } else if (!this.token && this.storepass == null && var2) {
            this.storepass = this.getPass(rb.getString("Enter.Passphrase.for.keystore."));
         }

         try {
            if (this.nullStream) {
               this.store.load((InputStream)null, this.storepass);
            } else {
               var1 = var1.replace(File.separatorChar, '/');
               var3 = null;

               URL var47;
               try {
                  var47 = new URL(var1);
               } catch (MalformedURLException var36) {
                  var47 = (new File(var1)).toURI().toURL();
               }

               InputStream var48 = null;

               try {
                  var48 = var47.openStream();
                  this.store.load(var48, this.storepass);
               } finally {
                  if (var48 != null) {
                     var48.close();
                  }

               }
            }

            Enumeration var49 = this.store.aliases();

            while(var49.hasMoreElements()) {
               String var50 = (String)var49.nextElement();

               try {
                  X509Certificate var51 = (X509Certificate)this.store.getCertificate(var50);
                  if (this.store.isCertificateEntry(var50) || var51.getSubjectDN().equals(var51.getIssuerDN())) {
                     this.trustedCerts.add(var51);
                  }
               } catch (Exception var39) {
               }
            }
         } finally {
            try {
               this.pkixParameters = new PKIXBuilderParameters((Set)this.trustedCerts.stream().map((var0) -> {
                  return new TrustAnchor(var0, (byte[])null);
               }).collect(Collectors.toSet()), (CertSelector)null);
               this.pkixParameters.setRevocationEnabled(false);
            } catch (InvalidAlgorithmParameterException var35) {
            }

         }

      } catch (IOException var42) {
         throw new RuntimeException(rb.getString("keystore.load.") + var42.getMessage());
      } catch (CertificateException var43) {
         throw new RuntimeException(rb.getString("certificate.exception.") + var43.getMessage());
      } catch (NoSuchProviderException var44) {
         throw new RuntimeException(rb.getString("keystore.load.") + var44.getMessage());
      } catch (NoSuchAlgorithmException var45) {
         throw new RuntimeException(rb.getString("keystore.load.") + var45.getMessage());
      } catch (KeyStoreException var46) {
         throw new RuntimeException(rb.getString("unable.to.instantiate.keystore.class.") + var46.getMessage());
      }
   }

   X509Certificate getTsaCert(String var1) {
      Certificate var2 = null;

      try {
         var2 = this.store.getCertificate(var1);
      } catch (KeyStoreException var5) {
      }

      if (var2 == null || !(var2 instanceof X509Certificate)) {
         MessageFormat var3 = new MessageFormat(rb.getString("Certificate.not.found.for.alias.alias.must.reference.a.valid.KeyStore.entry.containing.an.X.509.public.key.certificate.for.the"));
         Object[] var4 = new Object[]{var1, var1};
         this.error(var3.format(var4));
      }

      return (X509Certificate)var2;
   }

   void checkCertUsage(X509Certificate var1, boolean[] var2) {
      if (var2 != null) {
         var2[0] = var2[1] = var2[2] = false;
      }

      boolean[] var3 = var1.getKeyUsage();
      if (var3 != null) {
         var3 = Arrays.copyOf(var3, 9);
         if (!var3[0] && !var3[1] && var2 != null) {
            var2[0] = true;
            this.badKeyUsage = true;
         }
      }

      try {
         List var4 = var1.getExtendedKeyUsage();
         if (var4 != null && !var4.contains("2.5.29.37.0") && !var4.contains("1.3.6.1.5.5.7.3.3") && var2 != null) {
            var2[1] = true;
            this.badExtendedKeyUsage = true;
         }
      } catch (CertificateParsingException var10) {
      }

      try {
         byte[] var11 = var1.getExtensionValue("2.16.840.1.113730.1.1");
         if (var11 != null) {
            DerInputStream var5 = new DerInputStream(var11);
            byte[] var6 = var5.getOctetString();
            var6 = (new DerValue(var6)).getUnalignedBitString().toByteArray();
            NetscapeCertTypeExtension var7 = new NetscapeCertTypeExtension(var6);
            Boolean var8 = var7.get("object_signing");
            if (!var8 && var2 != null) {
               var2[2] = true;
               this.badNetscapeCertType = true;
            }
         }
      } catch (IOException var9) {
      }

   }

   void getAliasInfo(String var1) throws Exception {
      Key var2 = null;

      try {
         Certificate[] var3 = null;
         if (this.altCertChain != null) {
            try {
               FileInputStream var4 = new FileInputStream(this.altCertChain);
               Throwable var5 = null;

               try {
                  var3 = (Certificate[])CertificateFactory.getInstance("X.509").generateCertificates(var4).toArray(new Certificate[0]);
               } catch (Throwable var22) {
                  var5 = var22;
                  throw var22;
               } finally {
                  if (var4 != null) {
                     if (var5 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var20) {
                           var5.addSuppressed(var20);
                        }
                     } else {
                        var4.close();
                     }
                  }

               }
            } catch (FileNotFoundException var24) {
               this.error(rb.getString("File.specified.by.certchain.does.not.exist"));
            } catch (IOException | CertificateException var25) {
               this.error(rb.getString("Cannot.restore.certchain.from.file.specified"));
            }
         } else {
            try {
               var3 = this.store.getCertificateChain(var1);
            } catch (KeyStoreException var21) {
            }
         }

         if (var3 == null || var3.length == 0) {
            if (this.altCertChain != null) {
               this.error(rb.getString("Certificate.chain.not.found.in.the.file.specified."));
            } else {
               MessageFormat var31 = new MessageFormat(rb.getString("Certificate.chain.not.found.for.alias.alias.must.reference.a.valid.KeyStore.key.entry.containing.a.private.key.and"));
               Object[] var34 = new Object[]{var1, var1};
               this.error(var31.format(var34));
            }
         }

         this.certChain = new X509Certificate[var3.length];

         for(int var32 = 0; var32 < var3.length; ++var32) {
            if (!(var3[var32] instanceof X509Certificate)) {
               this.error(rb.getString("found.non.X.509.certificate.in.signer.s.chain"));
            }

            this.certChain[var32] = (X509Certificate)var3[var32];
         }

         try {
            if (!this.token && this.keypass == null) {
               var2 = this.store.getKey(var1, this.storepass);
            } else {
               var2 = this.store.getKey(var1, this.keypass);
            }
         } catch (UnrecoverableKeyException var26) {
            if (this.token) {
               throw var26;
            }

            if (this.keypass == null) {
               MessageFormat var35 = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
               Object[] var6 = new Object[]{var1};
               this.keypass = this.getPass(var35.format(var6));
               var2 = this.store.getKey(var1, this.keypass);
            }
         }
      } catch (NoSuchAlgorithmException var27) {
         this.error(var27.getMessage());
      } catch (UnrecoverableKeyException var28) {
         this.error(rb.getString("unable.to.recover.key.from.keystore"));
      } catch (KeyStoreException var29) {
      }

      if (!(var2 instanceof PrivateKey)) {
         MessageFormat var30 = new MessageFormat(rb.getString("key.associated.with.alias.not.a.private.key"));
         Object[] var33 = new Object[]{var1};
         this.error(var30.format(var33));
      } else {
         this.privateKey = (PrivateKey)var2;
      }

   }

   void error(String var1) {
      System.out.println(rb.getString("jarsigner.") + var1);
      System.exit(1);
   }

   void error(String var1, Exception var2) {
      System.out.println(rb.getString("jarsigner.") + var1);
      if (this.debug) {
         var2.printStackTrace();
      }

      System.exit(1);
   }

   void validateCertChain(String var1, List var2, Timestamp var3) throws Exception {
      try {
         Validator.getInstance("PKIX", var1, this.pkixParameters).validate((X509Certificate[])var2.toArray(new X509Certificate[var2.size()]), (Collection)null, var3);
      } catch (Exception var6) {
         Exception var4 = var6;
         if (this.debug) {
            var6.printStackTrace();
         }

         Throwable var5;
         if (var1.equals("tsa server") && var6 instanceof ValidatorException && var6.getCause() != null && var6.getCause() instanceof CertPathValidatorException) {
            var4 = (Exception)var6.getCause();
            var5 = var4.getCause();
            if (var5 instanceof CertificateExpiredException && this.hasExpiredTsaCert) {
               return;
            }
         }

         if (var1.equals("code signing") && var4 instanceof ValidatorException) {
            if (var4.getCause() != null && var4.getCause() instanceof CertPathValidatorException) {
               var4 = (Exception)var4.getCause();
               var5 = var4.getCause();
               if (var5 instanceof CertificateExpiredException && this.hasExpiredCert || var5 instanceof CertificateNotYetValidException && this.notYetValidCert) {
                  return;
               }
            }

            if (var4 instanceof ValidatorException) {
               ValidatorException var7 = (ValidatorException)var4;
               if (var7.getErrorType() == ValidatorException.T_EE_EXTENSIONS && (this.badKeyUsage || this.badExtendedKeyUsage || this.badNetscapeCertType)) {
                  return;
               }
            }
         }

         throw var4;
      }
   }

   char[] getPass(String var1) {
      System.err.print(var1);
      System.err.flush();

      try {
         char[] var2 = Password.readPassword(System.in);
         if (var2 != null) {
            return var2;
         }

         this.error(rb.getString("you.must.enter.key.password"));
      } catch (IOException var3) {
         this.error(rb.getString("unable.to.read.password.") + var3.getMessage());
      }

      return null;
   }

   private synchronized byte[] getBytes(ZipFile var1, ZipEntry var2) throws IOException {
      InputStream var4 = null;

      try {
         var4 = var1.getInputStream(var2);
         this.baos.reset();

         int var3;
         for(long var5 = var2.getSize(); var5 > 0L && (var3 = var4.read(this.buffer, 0, this.buffer.length)) != -1; var5 -= (long)var3) {
            this.baos.write(this.buffer, 0, var3);
         }
      } finally {
         if (var4 != null) {
            var4.close();
         }

      }

      return this.baos.toByteArray();
   }

   private ZipEntry getManifestFile(ZipFile var1) {
      ZipEntry var2 = var1.getEntry("META-INF/MANIFEST.MF");
      if (var2 == null) {
         Enumeration var3 = var1.entries();

         while(var3.hasMoreElements() && var2 == null) {
            var2 = (ZipEntry)var3.nextElement();
            if (!"META-INF/MANIFEST.MF".equalsIgnoreCase(var2.getName())) {
               var2 = null;
            }
         }
      }

      return var2;
   }

   private synchronized String[] getDigests(ZipEntry var1, ZipFile var2, MessageDigest[] var3) throws IOException {
      InputStream var6 = null;

      int var5;
      try {
         var6 = var2.getInputStream(var1);

         int var4;
         for(long var7 = var1.getSize(); var7 > 0L && (var4 = var6.read(this.buffer, 0, this.buffer.length)) != -1; var7 -= (long)var4) {
            for(var5 = 0; var5 < var3.length; ++var5) {
               var3[var5].update(this.buffer, 0, var4);
            }
         }
      } finally {
         if (var6 != null) {
            var6.close();
         }

      }

      String[] var12 = new String[var3.length];

      for(var5 = 0; var5 < var3.length; ++var5) {
         var12[var5] = Base64.getEncoder().encodeToString(var3[var5].digest());
      }

      return var12;
   }

   private Attributes getDigestAttributes(ZipEntry var1, ZipFile var2, MessageDigest[] var3) throws IOException {
      String[] var4 = this.getDigests(var1, var2, var3);
      Attributes var5 = new Attributes();

      for(int var6 = 0; var6 < var3.length; ++var6) {
         var5.putValue(var3[var6].getAlgorithm() + "-Digest", var4[var6]);
      }

      return var5;
   }

   private boolean updateDigests(ZipEntry var1, ZipFile var2, MessageDigest[] var3, Manifest var4) throws IOException {
      boolean var5 = false;
      Attributes var6 = var4.getAttributes(var1.getName());
      String[] var7 = this.getDigests(var1, var2, var3);

      for(int var8 = 0; var8 < var3.length; ++var8) {
         String var9 = null;

         try {
            AlgorithmId var10 = AlgorithmId.get(var3[var8].getAlgorithm());
            Iterator var11 = var6.keySet().iterator();

            while(var11.hasNext()) {
               Object var12 = var11.next();
               if (var12 instanceof Attributes.Name) {
                  String var13 = ((Attributes.Name)var12).toString();
                  if (var13.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
                     String var14 = var13.substring(0, var13.length() - 7);
                     if (AlgorithmId.get(var14).equals(var10)) {
                        var9 = var13;
                        break;
                     }
                  }
               }
            }
         } catch (NoSuchAlgorithmException var15) {
         }

         if (var9 == null) {
            var9 = var3[var8].getAlgorithm() + "-Digest";
            var6.putValue(var9, var7[var8]);
            var5 = true;
         } else {
            String var16 = var6.getValue(var9);
            if (!var16.equalsIgnoreCase(var7[var8])) {
               var6.putValue(var9, var7[var8]);
               var5 = true;
            }
         }
      }

      return var5;
   }

   private ContentSigner loadSigningMechanism(String var1, String var2) throws Exception {
      String var3 = null;
      var3 = PathList.appendPath(System.getProperty("env.class.path"), var3);
      var3 = PathList.appendPath(System.getProperty("java.class.path"), var3);
      var3 = PathList.appendPath(var2, var3);
      URL[] var4 = PathList.pathToURLs(var3);
      URLClassLoader var5 = new URLClassLoader(var4);
      Class var6 = var5.loadClass(var1);
      Object var7 = var6.newInstance();
      if (!(var7 instanceof ContentSigner)) {
         MessageFormat var8 = new MessageFormat(rb.getString("signerClass.is.not.a.signing.mechanism"));
         Object[] var9 = new Object[]{var6.getName()};
         throw new IllegalArgumentException(var8.format(var9));
      } else {
         return (ContentSigner)var7;
      }
   }

   static {
      collator.setStrength(0);
      PARAM_STRING = new Class[]{String.class};
      DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
      DIGEST_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.MESSAGE_DIGEST));
      SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
      validityTimeForm = null;
      notYetTimeForm = null;
      expiredTimeForm = null;
      expiringTimeForm = null;
      signTimeForm = null;
   }
}
