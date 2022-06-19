package com.sun.tools.javadoc;

import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Options;
import java.util.StringTokenizer;

public enum ToolOption {
   BOOTCLASSPATH("-bootclasspath", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   CLASSPATH("-classpath", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   CP("-cp", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   EXTDIRS("-extdirs", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   SOURCEPATH("-sourcepath", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   SYSCLASSPATH("-sysclasspath", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt("-bootclasspath", var2);
      }
   },
   ENCODING("-encoding", true) {
      public void process(Helper var1, String var2) {
         var1.encoding = var2;
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   SOURCE("-source", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   XMAXERRS("-Xmaxerrs", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   XMAXWARNS("-Xmaxwarns", true) {
      public void process(Helper var1, String var2) {
         var1.setCompilerOpt(this.opt, var2);
      }
   },
   DOCLET("-doclet", true),
   DOCLETPATH("-docletpath", true),
   SUBPACKAGES("-subpackages", true) {
      public void process(Helper var1, String var2) {
         var1.addToList(var1.subPackages, var2);
      }
   },
   EXCLUDE("-exclude", true) {
      public void process(Helper var1, String var2) {
         var1.addToList(var1.excludedPackages, var2);
      }
   },
   PACKAGE("-package") {
      public void process(Helper var1) {
         var1.setFilter(-9223372036854775803L);
      }
   },
   PRIVATE("-private") {
      public void process(Helper var1) {
         var1.setFilter(-9223372036854775801L);
      }
   },
   PROTECTED("-protected") {
      public void process(Helper var1) {
         var1.setFilter(5L);
      }
   },
   PUBLIC("-public") {
      public void process(Helper var1) {
         var1.setFilter(1L);
      }
   },
   PROMPT("-prompt") {
      public void process(Helper var1) {
         var1.compOpts.put("-prompt", "-prompt");
         var1.promptOnError = true;
      }
   },
   QUIET("-quiet") {
      public void process(Helper var1) {
         var1.quiet = true;
      }
   },
   VERBOSE("-verbose") {
      public void process(Helper var1) {
         var1.compOpts.put("-verbose", "");
      }
   },
   XWERROR("-Xwerror") {
      public void process(Helper var1) {
         var1.rejectWarnings = true;
      }
   },
   BREAKITERATOR("-breakiterator") {
      public void process(Helper var1) {
         var1.breakiterator = true;
      }
   },
   LOCALE("-locale", true) {
      public void process(Helper var1, String var2) {
         var1.docLocale = var2;
      }
   },
   OVERVIEW("-overview", true),
   XCLASSES("-Xclasses") {
      public void process(Helper var1) {
         var1.docClasses = true;
      }
   },
   HELP("-help") {
      public void process(Helper var1) {
         var1.usage();
      }
   },
   X("-X") {
      public void process(Helper var1) {
         var1.Xusage();
      }
   };

   public final String opt;
   public final boolean hasArg;

   private ToolOption(String var3) {
      this(var3, false);
   }

   private ToolOption(String var3, boolean var4) {
      this.opt = var3;
      this.hasArg = var4;
   }

   void process(Helper var1, String var2) {
   }

   void process(Helper var1) {
   }

   static ToolOption get(String var0) {
      ToolOption[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ToolOption var4 = var1[var3];
         if (var0.equals(var4.opt)) {
            return var4;
         }
      }

      return null;
   }

   // $FF: synthetic method
   ToolOption(String var3, boolean var4, Object var5) {
      this(var3, var4);
   }

   // $FF: synthetic method
   ToolOption(String var3, Object var4) {
      this(var3);
   }

   abstract static class Helper {
      final ListBuffer options = new ListBuffer();
      final ListBuffer subPackages = new ListBuffer();
      final ListBuffer excludedPackages = new ListBuffer();
      Options compOpts;
      String encoding = null;
      boolean breakiterator = false;
      boolean quiet = false;
      boolean docClasses = false;
      boolean rejectWarnings = false;
      boolean promptOnError;
      String docLocale = "";
      ModifierFilter showAccess = null;

      abstract void usage();

      abstract void Xusage();

      abstract void usageError(String var1, Object... var2);

      protected void addToList(ListBuffer var1, String var2) {
         StringTokenizer var3 = new StringTokenizer(var2, ":");

         while(var3.hasMoreTokens()) {
            String var4 = var3.nextToken();
            var1.append(var4);
         }

      }

      protected void setFilter(long var1) {
         if (this.showAccess != null) {
            this.usageError("main.incompatible.access.flags");
         }

         this.showAccess = new ModifierFilter(var1);
      }

      private void setCompilerOpt(String var1, String var2) {
         if (this.compOpts.get(var1) != null) {
            this.usageError("main.option.already.seen", var1);
         }

         this.compOpts.put(var1, var2);
      }
   }
}
