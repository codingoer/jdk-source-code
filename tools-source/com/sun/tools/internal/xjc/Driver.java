package com.sun.tools.internal.xjc;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.writer.ZipCodeWriter;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.tools.DefaultAuthenticator;
import com.sun.tools.internal.xjc.generator.bean.BeanGenerator;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.reader.gbind.Expression;
import com.sun.tools.internal.xjc.reader.gbind.Graph;
import com.sun.tools.internal.xjc.reader.internalizer.DOMForest;
import com.sun.tools.internal.xjc.reader.xmlschema.ExpressionBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.tools.internal.xjc.util.NullStream;
import com.sun.tools.internal.xjc.util.Util;
import com.sun.tools.internal.xjc.writer.SignatureWriter;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchemaSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Iterator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Driver {
   public static void main(final String[] args) throws Exception {
      try {
         System.setProperty("java.net.useSystemProxies", "true");
      } catch (SecurityException var3) {
      }

      if (Util.getSystemProperty(Driver.class, "noThreadSwap") != null) {
         _main(args);
      }

      final Throwable[] ex = new Throwable[1];
      Thread th = new Thread() {
         public void run() {
            try {
               Driver._main(args);
            } catch (Throwable var2) {
               ex[0] = var2;
            }

         }
      };
      th.start();
      th.join();
      if (ex[0] != null) {
         if (ex[0] instanceof Exception) {
            throw (Exception)ex[0];
         } else {
            throw (Error)ex[0];
         }
      }
   }

   private static void _main(String[] args) throws Exception {
      try {
         System.exit(run(args, System.out, System.out));
      } catch (BadCommandLineException var2) {
         if (var2.getMessage() != null) {
            System.out.println(var2.getMessage());
            System.out.println();
         }

         usage(var2.getOptions(), false);
         System.exit(-1);
      }

   }

   public static int run(String[] args, final PrintStream status, final PrintStream out) throws Exception {
      class Listener extends XJCListener {
         ConsoleErrorReporter cer = new ConsoleErrorReporter(out == null ? new PrintStream(new NullStream()) : out);

         public void generatedFile(String fileName, int count, int total) {
            this.message(fileName);
         }

         public void message(String msg) {
            if (status != null) {
               status.println(msg);
            }

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

      return run(args, new Listener());
   }

   public static int run(String[] args, @NotNull final XJCListener listener) throws BadCommandLineException {
      String[] var2 = args;
      int var3 = args.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String arg = var2[var4];
         if (arg.equals("-version")) {
            listener.message(Messages.format("Driver.Version"));
            return -1;
         }

         if (arg.equals("-fullversion")) {
            listener.message(Messages.format("Driver.FullVersion"));
            return -1;
         }
      }

      final OptionsEx opt = new OptionsEx();
      opt.setSchemaLanguage(Language.XMLSCHEMA);

      try {
         opt.parseArguments(args);
      } catch (WeAreDone var23) {
         if (opt.proxyAuth != null) {
            DefaultAuthenticator.reset();
         }

         return -1;
      } catch (BadCommandLineException var24) {
         if (opt.proxyAuth != null) {
            DefaultAuthenticator.reset();
         }

         var24.initOptions(opt);
         throw var24;
      }

      if (opt.defaultPackage != null && opt.defaultPackage.length() == 0) {
         listener.message(Messages.format("Driver.WarningMessage", Messages.format("Driver.DefaultPackageWarning")));
      }

      ClassLoader contextClassLoader = SecureLoader.getContextClassLoader();
      SecureLoader.setContextClassLoader(opt.getUserClassLoader(contextClassLoader));

      byte var36;
      try {
         if (!opt.quiet) {
            listener.message(Messages.format("Driver.ParsingSchema"));
         }

         final boolean[] hadWarning = new boolean[1];
         ErrorReceiver receiver = new ErrorReceiverFilter(listener) {
            public void info(SAXParseException exception) {
               if (opt.verbose) {
                  super.info(exception);
               }

            }

            public void warning(SAXParseException exception) {
               hadWarning[0] = true;
               if (!opt.quiet) {
                  super.warning(exception);
               }

            }

            public void pollAbort() throws AbortException {
               if (listener.isCanceled()) {
                  throw new AbortException();
               }
            }
         };
         byte var43;
         byte var47;
         if (opt.mode == Driver.Mode.FOREST) {
            ModelLoader loader = new ModelLoader(opt, new JCodeModel(), receiver);

            try {
               DOMForest forest = loader.buildDOMForest(new XMLSchemaInternalizationLogic());
               forest.dump(System.out);
               var43 = 0;
               return var43;
            } catch (SAXException var26) {
            } catch (IOException var27) {
               receiver.error((Exception)var27);
            }

            var47 = -1;
            return var47;
         }

         if (opt.mode != Driver.Mode.GBIND) {
            Model model = ModelLoader.load(opt, new JCodeModel(), receiver);
            if (model == null) {
               listener.message(Messages.format("Driver.ParseFailed"));
               var47 = -1;
               return var47;
            }

            if (!opt.quiet) {
               listener.message(Messages.format("Driver.CompilingSchema"));
            }

            byte var44;
            byte var46;
            switch (opt.mode) {
               case SIGNATURE:
                  try {
                     SignatureWriter.write(BeanGenerator.generate(model, receiver), new OutputStreamWriter(System.out));
                     var46 = 0;
                     return var46;
                  } catch (IOException var29) {
                     receiver.error((Exception)var29);
                     var44 = -1;
                     return var44;
                  }
               case CODE:
               case DRYRUN:
               case ZIP:
                  receiver.debug("generating code");
                  Outline outline = model.generateCode(opt, receiver);
                  if (outline == null) {
                     listener.message(Messages.format("Driver.FailedToGenerateCode"));
                     var44 = -1;
                     return var44;
                  }

                  listener.compiled(outline);
                  if (opt.mode != Driver.Mode.DRYRUN) {
                     try {
                        Object cw;
                        if (opt.mode == Driver.Mode.ZIP) {
                           Object os;
                           if (opt.targetDir.getPath().equals(".")) {
                              os = System.out;
                           } else {
                              os = new FileOutputStream(opt.targetDir);
                           }

                           cw = opt.createCodeWriter(new ZipCodeWriter((OutputStream)os));
                        } else {
                           cw = opt.createCodeWriter();
                        }

                        if (!opt.quiet) {
                           cw = new ProgressCodeWriter((CodeWriter)cw, listener, model.codeModel.countArtifacts());
                        }

                        model.codeModel.build((CodeWriter)cw);
                     } catch (IOException var30) {
                        receiver.error((Exception)var30);
                        var44 = -1;
                        return var44;
                     }
                  }
                  break;
               default:
                  assert false;
            }

            if (opt.debugMode) {
               try {
                  (new FileOutputStream(new File(opt.targetDir, hadWarning[0] ? "hadWarning" : "noWarning"))).close();
               } catch (IOException var28) {
                  receiver.error((Exception)var28);
                  var44 = -1;
                  return var44;
               }
            }

            var46 = 0;
            return var46;
         }

         try {
            XSSchemaSet xss = (new ModelLoader(opt, new JCodeModel(), receiver)).loadXMLSchema();
            Iterator it = xss.iterateComplexTypes();

            while(it.hasNext()) {
               XSComplexType ct = (XSComplexType)it.next();
               XSParticle p = ct.getContentType().asParticle();
               if (p != null) {
                  Expression tree = ExpressionBuilder.createTree(p);
                  System.out.println("Graph for " + ct.getName());
                  System.out.println(tree.toString());
                  Graph g = new Graph(tree);
                  System.out.println(g.toString());
                  System.out.println();
               }
            }

            var43 = 0;
            return var43;
         } catch (SAXException var25) {
            byte var6 = -1;
            return var6;
         }
      } catch (StackOverflowError var31) {
         if (opt.verbose) {
            throw var31;
         }

         listener.message(Messages.format("Driver.StackOverflow"));
         var36 = -1;
      } finally {
         if (opt.proxyAuth != null) {
            DefaultAuthenticator.reset();
         }

      }

      return var36;
   }

   public static String getBuildID() {
      return Messages.format("Driver.BuildID");
   }

   public static void usage(@Nullable Options opts, boolean privateUsage) {
      System.out.println(Messages.format("Driver.Public.Usage"));
      if (privateUsage) {
         System.out.println(Messages.format("Driver.Private.Usage"));
      }

      if (opts != null && !opts.getAllPlugins().isEmpty()) {
         System.out.println(Messages.format("Driver.AddonUsage"));
         Iterator var2 = opts.getAllPlugins().iterator();

         while(var2.hasNext()) {
            Plugin p = (Plugin)var2.next();
            System.out.println(p.getUsage());
         }
      }

   }

   private static final class WeAreDone extends BadCommandLineException {
      private WeAreDone() {
      }

      // $FF: synthetic method
      WeAreDone(Object x0) {
         this();
      }
   }

   static class OptionsEx extends Options {
      protected Mode mode;
      public boolean noNS;

      OptionsEx() {
         this.mode = Driver.Mode.CODE;
         this.noNS = false;
      }

      public int parseArgument(String[] args, int i) throws BadCommandLineException {
         if (args[i].equals("-noNS")) {
            this.noNS = true;
            return 1;
         } else if (args[i].equals("-mode")) {
            ++i;
            if (i == args.length) {
               throw new BadCommandLineException(Messages.format("Driver.MissingModeOperand"));
            } else {
               String mstr = args[i].toLowerCase();
               Mode[] var4 = Driver.Mode.values();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Mode m = var4[var6];
                  if (m.name().toLowerCase().startsWith(mstr) && mstr.length() > 2) {
                     this.mode = m;
                     return 2;
                  }
               }

               throw new BadCommandLineException(Messages.format("Driver.UnrecognizedMode", args[i]));
            }
         } else if (args[i].equals("-help")) {
            Driver.usage(this, false);
            throw new WeAreDone();
         } else if (args[i].equals("-private")) {
            Driver.usage(this, true);
            throw new WeAreDone();
         } else {
            return super.parseArgument(args, i);
         }
      }
   }

   private static enum Mode {
      CODE,
      SIGNATURE,
      FOREST,
      DRYRUN,
      ZIP,
      GBIND;
   }
}
