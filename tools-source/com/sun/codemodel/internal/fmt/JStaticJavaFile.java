package com.sun.codemodel.internal.fmt;

import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JResourceFile;
import com.sun.codemodel.internal.JTypeVar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

public final class JStaticJavaFile extends JResourceFile {
   private final JPackage pkg;
   private final String className;
   private final URL source;
   private final JStaticClass clazz;
   private final LineFilter filter;

   public JStaticJavaFile(JPackage _pkg, String className, String _resourceName) {
      this(_pkg, className, SecureLoader.getClassClassLoader(JStaticJavaFile.class).getResource(_resourceName), (LineFilter)null);
   }

   public JStaticJavaFile(JPackage _pkg, String _className, URL _source, LineFilter _filter) {
      super(_className + ".java");
      if (_source == null) {
         throw new NullPointerException();
      } else {
         this.pkg = _pkg;
         this.clazz = new JStaticClass();
         this.className = _className;
         this.source = _source;
         this.filter = _filter;
      }
   }

   public final JClass getJClass() {
      return this.clazz;
   }

   protected boolean isResource() {
      return false;
   }

   protected void build(OutputStream os) throws IOException {
      InputStream is = this.source.openStream();
      BufferedReader r = new BufferedReader(new InputStreamReader(is));
      PrintWriter w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
      LineFilter filter = this.createLineFilter();
      int lineNumber = 1;

      String line;
      try {
         for(; (line = r.readLine()) != null; ++lineNumber) {
            line = filter.process(line);
            if (line != null) {
               w.println(line);
            }
         }
      } catch (ParseException var8) {
         throw new IOException("unable to process " + this.source + " line:" + lineNumber + "\n" + var8.getMessage());
      }

      w.close();
      r.close();
   }

   private LineFilter createLineFilter() {
      LineFilter f = new LineFilter() {
         public String process(String line) {
            if (!line.startsWith("package ")) {
               return line;
            } else {
               return JStaticJavaFile.this.pkg.isUnnamed() ? null : "package " + JStaticJavaFile.this.pkg.name() + ";";
            }
         }
      };
      return (LineFilter)(this.filter != null ? new ChainFilter(this.filter, f) : f);
   }

   private class JStaticClass extends JClass {
      private final JTypeVar[] typeParams = new JTypeVar[0];

      JStaticClass() {
         super(JStaticJavaFile.this.pkg.owner());
      }

      public String name() {
         return JStaticJavaFile.this.className;
      }

      public String fullName() {
         return JStaticJavaFile.this.pkg.isUnnamed() ? JStaticJavaFile.this.className : JStaticJavaFile.this.pkg.name() + '.' + JStaticJavaFile.this.className;
      }

      public JPackage _package() {
         return JStaticJavaFile.this.pkg;
      }

      public JClass _extends() {
         throw new UnsupportedOperationException();
      }

      public Iterator _implements() {
         throw new UnsupportedOperationException();
      }

      public boolean isInterface() {
         throw new UnsupportedOperationException();
      }

      public boolean isAbstract() {
         throw new UnsupportedOperationException();
      }

      public JTypeVar[] typeParams() {
         return this.typeParams;
      }

      protected JClass substituteParams(JTypeVar[] variables, List bindings) {
         return this;
      }
   }

   public static final class ChainFilter implements LineFilter {
      private final LineFilter first;
      private final LineFilter second;

      public ChainFilter(LineFilter first, LineFilter second) {
         this.first = first;
         this.second = second;
      }

      public String process(String line) throws ParseException {
         line = this.first.process(line);
         return line == null ? null : this.second.process(line);
      }
   }

   public interface LineFilter {
      String process(String var1) throws ParseException;
   }
}
