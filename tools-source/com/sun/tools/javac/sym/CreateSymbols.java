package com.sun.tools.javac.sym;

import com.sun.tools.javac.Main;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.ClassWriter;
import com.sun.tools.javac.jvm.Pool;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.Diagnostic.Kind;

@SupportedOptions({"com.sun.tools.javac.sym.Jar", "com.sun.tools.javac.sym.Dest", "com.sun.tools.javac.sym.Profiles"})
@SupportedAnnotationTypes({"*"})
public class CreateSymbols extends AbstractProcessor {
   static Set getLegacyPackages() {
      ResourceBundle var0 = ResourceBundle.getBundle("com.sun.tools.javac.resources.legacy");
      HashSet var1 = new HashSet();
      Enumeration var2 = var0.getKeys();

      while(var2.hasMoreElements()) {
         var1.add(var2.nextElement());
      }

      return var1;
   }

   public boolean process(Set var1, RoundEnvironment var2) {
      try {
         if (var2.processingOver()) {
            this.createSymbols();
         }
      } catch (IOException var6) {
         String var8 = var6.getLocalizedMessage();
         if (var8 == null) {
            var8 = var6.toString();
         }

         this.processingEnv.getMessager().printMessage(Kind.ERROR, var8);
      } catch (Throwable var7) {
         var7.printStackTrace();
         Throwable var4 = var7.getCause();
         if (var4 == null) {
            var4 = var7;
         }

         String var5 = var4.getLocalizedMessage();
         if (var5 == null) {
            var5 = var4.toString();
         }

         this.processingEnv.getMessager().printMessage(Kind.ERROR, var5);
      }

      return true;
   }

   void createSymbols() throws IOException {
      Set var1 = getLegacyPackages();
      Set var2 = getLegacyPackages();
      HashSet var3 = new HashSet();
      Set var4 = ((JavacProcessingEnvironment)this.processingEnv).getSpecifiedPackages();
      Map var5 = this.processingEnv.getOptions();
      String var6 = (String)var5.get("com.sun.tools.javac.sym.Jar");
      if (var6 == null) {
         throw new RuntimeException("Must use -Acom.sun.tools.javac.sym.Jar=LOCATION_OF_JAR");
      } else {
         String var7 = (String)var5.get("com.sun.tools.javac.sym.Dest");
         if (var7 == null) {
            throw new RuntimeException("Must use -Acom.sun.tools.javac.sym.Dest=LOCATION_OF_JAR");
         } else {
            String var8 = (String)var5.get("com.sun.tools.javac.sym.Profiles");
            if (var8 == null) {
               throw new RuntimeException("Must use -Acom.sun.tools.javac.sym.Profiles=PROFILES_SPEC");
            } else {
               Profiles var9 = Profiles.read(new File(var8));
               Iterator var10 = var4.iterator();

               while(var10.hasNext()) {
                  Symbol.PackageSymbol var11 = (Symbol.PackageSymbol)var10.next();
                  String var12 = var11.getQualifiedName().toString();
                  var2.remove(var12);
                  var3.add(var12);
               }

               JavaCompiler var37 = ToolProvider.getSystemJavaCompiler();
               StandardJavaFileManager var38 = var37.getStandardFileManager((DiagnosticListener)null, (Locale)null, (Charset)null);
               JavaFileManager.Location var39 = StandardLocation.locationFor(var6);
               File var13 = new File(var6);
               var38.setLocation(var39, List.of(var13));
               var38.setLocation(StandardLocation.CLASS_PATH, List.nil());
               var38.setLocation(StandardLocation.SOURCE_PATH, List.nil());
               ArrayList var14 = new ArrayList();
               var14.add(var13);
               Iterator var15 = var38.getLocation(StandardLocation.PLATFORM_CLASS_PATH).iterator();

               while(var15.hasNext()) {
                  File var16 = (File)var15.next();
                  if (!(new File(var16.getName())).equals(new File("rt.jar"))) {
                     var14.add(var16);
                  }
               }

               System.err.println("Using boot class path = " + var14);
               var38.setLocation(StandardLocation.PLATFORM_CLASS_PATH, var14);
               File var40 = new File(var7);
               if (!var40.exists() && !var40.mkdirs()) {
                  throw new RuntimeException("Could not create " + var40);
               } else {
                  var38.setLocation(StandardLocation.CLASS_OUTPUT, List.of(var40));
                  HashSet var41 = new HashSet();
                  HashSet var42 = new HashSet();
                  List var17 = List.of("-XDdev");
                  JavacTaskImpl var18 = (JavacTaskImpl)var37.getTask((Writer)null, var38, (DiagnosticListener)null, var17, (Iterable)null, (Iterable)null);
                  com.sun.tools.javac.main.JavaCompiler var19 = com.sun.tools.javac.main.JavaCompiler.instance(var18.getContext());
                  ClassWriter var20 = ClassWriter.instance(var18.getContext());
                  Symtab var21 = Symtab.instance(var18.getContext());
                  Names var22 = Names.instance(var18.getContext());
                  Attribute.Compound var23 = new Attribute.Compound(var21.proprietaryType, List.nil());
                  Attribute.Compound[] var24 = new Attribute.Compound[var9.getProfileCount() + 1];
                  Symbol.MethodSymbol var25 = (Symbol.MethodSymbol)var21.profileType.tsym.members().lookup(var22.value).sym;

                  for(int var26 = 1; var26 < var24.length; ++var26) {
                     var24[var26] = new Attribute.Compound(var21.profileType, List.of(new Pair(var25, new Attribute.Constant(var21.intType, var26))));
                  }

                  Type.moreInfo = true;
                  Types var43 = Types.instance(var18.getContext());
                  Pool var27 = new Pool(var43);
                  Iterator var28 = var38.list(var39, "", EnumSet.of(javax.tools.JavaFileObject.Kind.CLASS), true).iterator();

                  while(true) {
                     String var30;
                     boolean var33;
                     while(true) {
                        if (!var28.hasNext()) {
                           return;
                        }

                        JavaFileObject var29 = (JavaFileObject)var28.next();
                        var30 = var38.inferBinaryName(var39, var29);
                        int var31 = var30.lastIndexOf(46);
                        String var32 = var31 == -1 ? "" : var30.substring(0, var31);
                        var33 = false;
                        if (var3.contains(var32)) {
                           if (!var1.contains(var32)) {
                              var42.add(var32);
                           }
                           break;
                        }

                        if (var2.contains(var32)) {
                           var33 = true;
                           break;
                        }

                        var41.add(var32);
                     }

                     Symbol.TypeSymbol var34 = (Symbol.TypeSymbol)var19.resolveIdent(var30);
                     if (var34.kind != 2) {
                        if (var30.indexOf(36) < 0) {
                           System.err.println("Ignoring (other) " + var30 + " : " + var34);
                           System.err.println("   " + var34.getClass().getSimpleName() + " " + var34.type);
                        }
                     } else {
                        var34.complete();
                        if (var34.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
                           System.err.println("Ignoring (bad) " + var34.getQualifiedName());
                        } else {
                           Symbol.ClassSymbol var35 = (Symbol.ClassSymbol)var34;
                           if (var33) {
                              var35.prependAttributes(List.of(var23));
                           }

                           int var36 = var9.getProfile(var35.fullname.toString().replace(".", "/"));
                           if (0 < var36 && var36 < var24.length) {
                              var35.prependAttributes(List.of(var24[var36]));
                           }

                           this.writeClass(var27, var35, var20);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   void writeClass(Pool var1, Symbol.ClassSymbol var2, ClassWriter var3) throws IOException {
      try {
         var1.reset();
         var2.pool = var1;
         var3.writeClass(var2);

         for(Scope.Entry var4 = var2.members().elems; var4 != null; var4 = var4.sibling) {
            if (var4.sym.kind == 2) {
               Symbol.ClassSymbol var5 = (Symbol.ClassSymbol)var4.sym;
               var5.complete();
               this.writeClass(var1, var5, var3);
            }
         }

      } catch (ClassWriter.StringOverflow var6) {
         throw new RuntimeException(var6);
      } catch (ClassWriter.PoolOverflow var7) {
         throw new RuntimeException(var7);
      }
   }

   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
   }

   public static void main(String... var0) {
      String var1 = var0[0];
      String var2 = var0[1];
      var0 = new String[]{"-Xbootclasspath:" + var1, "-XDprocess.packages", "-proc:only", "-processor", "com.sun.tools.javac.sym.CreateSymbols", "-Acom.sun.tools.javac.sym.Jar=" + var1, "-Acom.sun.tools.javac.sym.Dest=" + var2, "java.applet", "java.awt", "java.awt.color", "java.awt.datatransfer", "java.awt.dnd", "java.awt.event", "java.awt.font", "java.awt.geom", "java.awt.im", "java.awt.im.spi", "java.awt.image", "java.awt.image.renderable", "java.awt.print", "java.beans", "java.beans.beancontext", "java.io", "java.lang", "java.lang.annotation", "java.lang.instrument", "java.lang.management", "java.lang.ref", "java.lang.reflect", "java.math", "java.net", "java.nio", "java.nio.channels", "java.nio.channels.spi", "java.nio.charset", "java.nio.charset.spi", "java.rmi", "java.rmi.activation", "java.rmi.dgc", "java.rmi.registry", "java.rmi.server", "java.security", "java.security.acl", "java.security.cert", "java.security.interfaces", "java.security.spec", "java.sql", "java.text", "java.text.spi", "java.util", "java.util.concurrent", "java.util.concurrent.atomic", "java.util.concurrent.locks", "java.util.jar", "java.util.logging", "java.util.prefs", "java.util.regex", "java.util.spi", "java.util.zip", "javax.accessibility", "javax.activation", "javax.activity", "javax.annotation", "javax.annotation.processing", "javax.crypto", "javax.crypto.interfaces", "javax.crypto.spec", "javax.imageio", "javax.imageio.event", "javax.imageio.metadata", "javax.imageio.plugins.jpeg", "javax.imageio.plugins.bmp", "javax.imageio.spi", "javax.imageio.stream", "javax.jws", "javax.jws.soap", "javax.lang.model", "javax.lang.model.element", "javax.lang.model.type", "javax.lang.model.util", "javax.management", "javax.management.loading", "javax.management.monitor", "javax.management.relation", "javax.management.openmbean", "javax.management.timer", "javax.management.modelmbean", "javax.management.remote", "javax.management.remote.rmi", "javax.naming", "javax.naming.directory", "javax.naming.event", "javax.naming.ldap", "javax.naming.spi", "javax.net", "javax.net.ssl", "javax.print", "javax.print.attribute", "javax.print.attribute.standard", "javax.print.event", "javax.rmi", "javax.rmi.CORBA", "javax.rmi.ssl", "javax.script", "javax.security.auth", "javax.security.auth.callback", "javax.security.auth.kerberos", "javax.security.auth.login", "javax.security.auth.spi", "javax.security.auth.x500", "javax.security.cert", "javax.security.sasl", "javax.sound.sampled", "javax.sound.sampled.spi", "javax.sound.midi", "javax.sound.midi.spi", "javax.sql", "javax.sql.rowset", "javax.sql.rowset.serial", "javax.sql.rowset.spi", "javax.swing", "javax.swing.border", "javax.swing.colorchooser", "javax.swing.filechooser", "javax.swing.event", "javax.swing.table", "javax.swing.text", "javax.swing.text.html", "javax.swing.text.html.parser", "javax.swing.text.rtf", "javax.swing.tree", "javax.swing.undo", "javax.swing.plaf", "javax.swing.plaf.basic", "javax.swing.plaf.metal", "javax.swing.plaf.multi", "javax.swing.plaf.synth", "javax.tools", "javax.transaction", "javax.transaction.xa", "javax.xml.parsers", "javax.xml.bind", "javax.xml.bind.annotation", "javax.xml.bind.annotation.adapters", "javax.xml.bind.attachment", "javax.xml.bind.helpers", "javax.xml.bind.util", "javax.xml.soap", "javax.xml.ws", "javax.xml.ws.handler", "javax.xml.ws.handler.soap", "javax.xml.ws.http", "javax.xml.ws.soap", "javax.xml.ws.spi", "javax.xml.transform", "javax.xml.transform.sax", "javax.xml.transform.dom", "javax.xml.transform.stax", "javax.xml.transform.stream", "javax.xml", "javax.xml.crypto", "javax.xml.crypto.dom", "javax.xml.crypto.dsig", "javax.xml.crypto.dsig.dom", "javax.xml.crypto.dsig.keyinfo", "javax.xml.crypto.dsig.spec", "javax.xml.datatype", "javax.xml.validation", "javax.xml.namespace", "javax.xml.xpath", "javax.xml.stream", "javax.xml.stream.events", "javax.xml.stream.util", "org.ietf.jgss", "org.omg.CORBA", "org.omg.CORBA.DynAnyPackage", "org.omg.CORBA.ORBPackage", "org.omg.CORBA.TypeCodePackage", "org.omg.stub.java.rmi", "org.omg.CORBA.portable", "org.omg.CORBA_2_3", "org.omg.CORBA_2_3.portable", "org.omg.CosNaming", "org.omg.CosNaming.NamingContextExtPackage", "org.omg.CosNaming.NamingContextPackage", "org.omg.SendingContext", "org.omg.PortableServer", "org.omg.PortableServer.CurrentPackage", "org.omg.PortableServer.POAPackage", "org.omg.PortableServer.POAManagerPackage", "org.omg.PortableServer.ServantLocatorPackage", "org.omg.PortableServer.portable", "org.omg.PortableInterceptor", "org.omg.PortableInterceptor.ORBInitInfoPackage", "org.omg.Messaging", "org.omg.IOP", "org.omg.IOP.CodecFactoryPackage", "org.omg.IOP.CodecPackage", "org.omg.Dynamic", "org.omg.DynamicAny", "org.omg.DynamicAny.DynAnyPackage", "org.omg.DynamicAny.DynAnyFactoryPackage", "org.w3c.dom", "org.w3c.dom.events", "org.w3c.dom.bootstrap", "org.w3c.dom.ls", "org.xml.sax", "org.xml.sax.ext", "org.xml.sax.helpers", "com.sun.java.browser.dom", "org.w3c.dom", "org.w3c.dom.bootstrap", "org.w3c.dom.ls", "org.w3c.dom.ranges", "org.w3c.dom.traversal", "org.w3c.dom.html", "org.w3c.dom.stylesheets", "org.w3c.dom.css", "org.w3c.dom.events", "org.w3c.dom.views", "com.sun.management", "com.sun.security.auth", "com.sun.security.auth.callback", "com.sun.security.auth.login", "com.sun.security.auth.module", "com.sun.security.jgss", "com.sun.net.httpserver", "com.sun.net.httpserver.spi", "javax.smartcardio"};
      Main.compile(var0);
   }
}
