package com.sun.tools.internal.jxc.ap;

import com.sun.tools.internal.xjc.BadCommandLineException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Options {
   public static final String DISABLE_XML_SECURITY = "-disableXmlSecurity";
   public String classpath = System.getenv("CLASSPATH");
   public File targetDir = null;
   public File episodeFile = null;
   private boolean disableXmlSecurity = false;
   public String encoding = null;
   public final List arguments = new ArrayList();

   public void parseArguments(String[] args) throws BadCommandLineException {
      for(int i = 0; i < args.length; ++i) {
         if (args[i].charAt(0) == '-') {
            int j = this.parseArgument(args, i);
            if (j == 0) {
               throw new BadCommandLineException(Messages.UNRECOGNIZED_PARAMETER.format(args[i]));
            }

            i += j;
         } else {
            this.arguments.add(args[i]);
         }
      }

   }

   private int parseArgument(String[] args, int i) throws BadCommandLineException {
      if (args[i].equals("-d")) {
         if (i == args.length - 1) {
            throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
         } else {
            ++i;
            this.targetDir = new File(args[i]);
            if (!this.targetDir.exists()) {
               throw new BadCommandLineException(Messages.NON_EXISTENT_FILE.format(this.targetDir));
            } else {
               return 1;
            }
         }
      } else if (args[i].equals("-episode")) {
         if (i == args.length - 1) {
            throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
         } else {
            ++i;
            this.episodeFile = new File(args[i]);
            return 1;
         }
      } else if (args[i].equals("-disableXmlSecurity")) {
         if (i == args.length - 1) {
            throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
         } else {
            this.disableXmlSecurity = true;
            return 1;
         }
      } else if (args[i].equals("-encoding")) {
         if (i == args.length - 1) {
            throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
         } else {
            ++i;
            this.encoding = args[i];
            return 1;
         }
      } else if (!args[i].equals("-cp") && !args[i].equals("-classpath")) {
         return 0;
      } else if (i == args.length - 1) {
         throw new BadCommandLineException(Messages.OPERAND_MISSING.format(args[i]));
      } else {
         ++i;
         this.classpath = args[i];
         return 1;
      }
   }

   public boolean isDisableXmlSecurity() {
      return this.disableXmlSecurity;
   }
}
