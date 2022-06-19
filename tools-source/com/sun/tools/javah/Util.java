package com.sun.tools.javah;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

public class Util {
   public boolean verbose = false;
   public PrintWriter log;
   public DiagnosticListener dl;
   private ResourceBundle m;

   Util(PrintWriter var1, DiagnosticListener var2) {
      this.log = var1;
      this.dl = var2;
   }

   public void log(String var1) {
      this.log.println(var1);
   }

   private void initMessages() throws Exit {
      try {
         this.m = ResourceBundle.getBundle("com.sun.tools.javah.resources.l10n");
      } catch (MissingResourceException var2) {
         this.fatal("Error loading resources.  Please file a bug report.", var2);
      }

   }

   private String getText(String var1, Object... var2) throws Exit {
      if (this.m == null) {
         this.initMessages();
      }

      try {
         return MessageFormat.format(this.m.getString(var1), var2);
      } catch (MissingResourceException var4) {
         this.fatal("Key " + var1 + " not found in resources.", var4);
         return null;
      }
   }

   public void usage() throws Exit {
      this.log.println(this.getText("usage"));
   }

   public void version() throws Exit {
      this.log.println(this.getText("javah.version", System.getProperty("java.version"), null));
   }

   public void bug(String var1) throws Exit {
      this.bug(var1, (Exception)null);
   }

   public void bug(String var1, Exception var2) throws Exit {
      this.dl.report(this.createDiagnostic(Kind.ERROR, var1));
      this.dl.report(this.createDiagnostic(Kind.NOTE, "bug.report"));
      throw new Exit(11, var2);
   }

   public void error(String var1, Object... var2) throws Exit {
      this.dl.report(this.createDiagnostic(Kind.ERROR, var1, var2));
      throw new Exit(15);
   }

   private void fatal(String var1, Exception var2) throws Exit {
      this.dl.report(this.createDiagnostic(Kind.ERROR, "", var1));
      throw new Exit(10, var2);
   }

   private Diagnostic createDiagnostic(final Diagnostic.Kind var1, final String var2, final Object... var3) {
      return new Diagnostic() {
         public String getCode() {
            return var2;
         }

         public long getColumnNumber() {
            return -1L;
         }

         public long getEndPosition() {
            return -1L;
         }

         public Diagnostic.Kind getKind() {
            return var1;
         }

         public long getLineNumber() {
            return -1L;
         }

         public String getMessage(Locale var1x) {
            return var2.length() == 0 ? (String)var3[0] : Util.this.getText(var2, var3);
         }

         public long getPosition() {
            return -1L;
         }

         public JavaFileObject getSource() {
            return null;
         }

         public long getStartPosition() {
            return -1L;
         }
      };
   }

   public static class Exit extends Error {
      private static final long serialVersionUID = 430820978114067221L;
      public final int exitValue;
      public final Throwable cause;

      Exit(int var1) {
         this(var1, (Throwable)null);
      }

      Exit(int var1, Throwable var2) {
         super(var2);
         this.exitValue = var1;
         this.cause = var2;
      }

      Exit(Exit var1) {
         this(var1.exitValue, var1.cause);
      }
   }
}
