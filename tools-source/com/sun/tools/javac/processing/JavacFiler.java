package com.sun.tools.javac.processing;

import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.ForwardingFileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class JavacFiler implements Filer, Closeable {
   private static final String ALREADY_OPENED = "Output stream or writer has already been opened.";
   private static final String NOT_FOR_READING = "FileObject was not opened for reading.";
   private static final String NOT_FOR_WRITING = "FileObject was not opened for writing.";
   JavaFileManager fileManager;
   Log log;
   Context context;
   boolean lastRound;
   private final boolean lint;
   private final Set fileObjectHistory;
   private final Set openTypeNames;
   private Set generatedSourceNames;
   private final Map generatedClasses;
   private Set generatedSourceFileObjects;
   private final Set aggregateGeneratedSourceNames;
   private final Set aggregateGeneratedClassNames;

   JavacFiler(Context var1) {
      this.context = var1;
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      this.log = Log.instance(var1);
      this.fileObjectHistory = Collections.synchronizedSet(new LinkedHashSet());
      this.generatedSourceNames = Collections.synchronizedSet(new LinkedHashSet());
      this.generatedSourceFileObjects = Collections.synchronizedSet(new LinkedHashSet());
      this.generatedClasses = Collections.synchronizedMap(new LinkedHashMap());
      this.openTypeNames = Collections.synchronizedSet(new LinkedHashSet());
      this.aggregateGeneratedSourceNames = new LinkedHashSet();
      this.aggregateGeneratedClassNames = new LinkedHashSet();
      this.lint = Lint.instance(var1).isEnabled(Lint.LintCategory.PROCESSING);
   }

   public JavaFileObject createSourceFile(CharSequence var1, Element... var2) throws IOException {
      return this.createSourceOrClassFile(true, var1.toString());
   }

   public JavaFileObject createClassFile(CharSequence var1, Element... var2) throws IOException {
      return this.createSourceOrClassFile(false, var1.toString());
   }

   private JavaFileObject createSourceOrClassFile(boolean var1, String var2) throws IOException {
      if (this.lint) {
         int var3 = var2.lastIndexOf(".");
         if (var3 != -1) {
            String var4 = var2.substring(var3);
            String var5 = var1 ? ".java" : ".class";
            if (var4.equals(var5)) {
               this.log.warning("proc.suspicious.class.name", new Object[]{var2, var5});
            }
         }
      }

      this.checkNameAndExistence(var2, var1);
      StandardLocation var6 = var1 ? StandardLocation.SOURCE_OUTPUT : StandardLocation.CLASS_OUTPUT;
      JavaFileObject.Kind var7 = var1 ? Kind.SOURCE : Kind.CLASS;
      JavaFileObject var8 = this.fileManager.getJavaFileForOutput(var6, var2, var7, (FileObject)null);
      this.checkFileReopening(var8, true);
      if (this.lastRound) {
         this.log.warning("proc.file.create.last.round", new Object[]{var2});
      }

      if (var1) {
         this.aggregateGeneratedSourceNames.add(var2);
      } else {
         this.aggregateGeneratedClassNames.add(var2);
      }

      this.openTypeNames.add(var2);
      return new FilerOutputJavaFileObject(var2, var8);
   }

   public FileObject createResource(JavaFileManager.Location var1, CharSequence var2, CharSequence var3, Element... var4) throws IOException {
      this.locationCheck(var1);
      String var5 = var2.toString();
      if (var5.length() > 0) {
         this.checkName(var5);
      }

      FileObject var6 = this.fileManager.getFileForOutput(var1, var5, var3.toString(), (FileObject)null);
      this.checkFileReopening(var6, true);
      return (FileObject)(var6 instanceof JavaFileObject ? new FilerOutputJavaFileObject((String)null, (JavaFileObject)var6) : new FilerOutputFileObject((String)null, var6));
   }

   private void locationCheck(JavaFileManager.Location var1) {
      if (var1 instanceof StandardLocation) {
         StandardLocation var2 = (StandardLocation)var1;
         if (!var2.isOutputLocation()) {
            throw new IllegalArgumentException("Resource creation not supported in location " + var2);
         }
      }

   }

   public FileObject getResource(JavaFileManager.Location var1, CharSequence var2, CharSequence var3) throws IOException {
      String var4 = var2.toString();
      if (var4.length() > 0) {
         this.checkName(var4);
      }

      FileObject var5;
      if (var1.isOutputLocation()) {
         var5 = this.fileManager.getFileForOutput(var1, var2.toString(), var3.toString(), (FileObject)null);
      } else {
         var5 = this.fileManager.getFileForInput(var1, var2.toString(), var3.toString());
      }

      if (var5 == null) {
         String var6 = var2.length() == 0 ? var3.toString() : var2 + "/" + var3;
         throw new FileNotFoundException(var6);
      } else {
         this.checkFileReopening(var5, false);
         return new FilerInputFileObject(var5);
      }
   }

   private void checkName(String var1) throws FilerException {
      this.checkName(var1, false);
   }

   private void checkName(String var1, boolean var2) throws FilerException {
      if (!SourceVersion.isName(var1) && !this.isPackageInfo(var1, var2)) {
         if (this.lint) {
            this.log.warning("proc.illegal.file.name", new Object[]{var1});
         }

         throw new FilerException("Illegal name " + var1);
      }
   }

   private boolean isPackageInfo(String var1, boolean var2) {
      int var4 = var1.lastIndexOf(".");
      if (var4 == -1) {
         return var2 ? var1.equals("package-info") : false;
      } else {
         String var5 = var1.substring(0, var4);
         String var6 = var1.substring(var4 + 1);
         return SourceVersion.isName(var5) && var6.equals("package-info");
      }
   }

   private void checkNameAndExistence(String var1, boolean var2) throws FilerException {
      this.checkName(var1, var2);
      if (this.aggregateGeneratedSourceNames.contains(var1) || this.aggregateGeneratedClassNames.contains(var1)) {
         if (this.lint) {
            this.log.warning("proc.type.recreate", new Object[]{var1});
         }

         throw new FilerException("Attempt to recreate a file for type " + var1);
      }
   }

   private void checkFileReopening(FileObject var1, boolean var2) throws FilerException {
      Iterator var3 = this.fileObjectHistory.iterator();

      FileObject var4;
      do {
         if (!var3.hasNext()) {
            if (var2) {
               this.fileObjectHistory.add(var1);
            }

            return;
         }

         var4 = (FileObject)var3.next();
      } while(!this.fileManager.isSameFile(var4, var1));

      if (this.lint) {
         this.log.warning("proc.file.reopening", new Object[]{var1.getName()});
      }

      throw new FilerException("Attempt to reopen a file for path " + var1.getName());
   }

   public boolean newFiles() {
      return !this.generatedSourceNames.isEmpty() || !this.generatedClasses.isEmpty();
   }

   public Set getGeneratedSourceNames() {
      return this.generatedSourceNames;
   }

   public Set getGeneratedSourceFileObjects() {
      return this.generatedSourceFileObjects;
   }

   public Map getGeneratedClasses() {
      return this.generatedClasses;
   }

   public void warnIfUnclosedFiles() {
      if (!this.openTypeNames.isEmpty()) {
         this.log.warning("proc.unclosed.type.files", new Object[]{this.openTypeNames.toString()});
      }

   }

   public void newRound(Context var1) {
      this.context = var1;
      this.log = Log.instance(var1);
      this.clearRoundState();
   }

   void setLastRound(boolean var1) {
      this.lastRound = var1;
   }

   public void close() {
      this.clearRoundState();
      this.fileObjectHistory.clear();
      this.openTypeNames.clear();
      this.aggregateGeneratedSourceNames.clear();
      this.aggregateGeneratedClassNames.clear();
   }

   private void clearRoundState() {
      this.generatedSourceNames.clear();
      this.generatedSourceFileObjects.clear();
      this.generatedClasses.clear();
   }

   public void displayState() {
      PrintWriter var1 = (PrintWriter)this.context.get(Log.outKey);
      var1.println("File Object History : " + this.fileObjectHistory);
      var1.println("Open Type Names     : " + this.openTypeNames);
      var1.println("Gen. Src Names      : " + this.generatedSourceNames);
      var1.println("Gen. Cls Names      : " + this.generatedClasses.keySet());
      var1.println("Agg. Gen. Src Names : " + this.aggregateGeneratedSourceNames);
      var1.println("Agg. Gen. Cls Names : " + this.aggregateGeneratedClassNames);
   }

   public String toString() {
      return "javac Filer";
   }

   private void closeFileObject(String var1, FileObject var2) {
      if (var1 != null) {
         if (!(var2 instanceof JavaFileObject)) {
            throw new AssertionError("JavaFileOject not found for " + var2);
         }

         JavaFileObject var3 = (JavaFileObject)var2;
         switch (var3.getKind()) {
            case SOURCE:
               this.generatedSourceNames.add(var1);
               this.generatedSourceFileObjects.add(var3);
               this.openTypeNames.remove(var1);
               break;
            case CLASS:
               this.generatedClasses.put(var1, var3);
               this.openTypeNames.remove(var1);
         }
      }

   }

   private class FilerWriter extends FilterWriter {
      String typeName;
      FileObject fileObject;
      boolean closed = false;

      FilerWriter(String var2, FileObject var3) throws IOException {
         super(var3.openWriter());
         this.typeName = var2;
         this.fileObject = var3;
      }

      public synchronized void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            JavacFiler.this.closeFileObject(this.typeName, this.fileObject);
            this.out.close();
         }

      }
   }

   private class FilerOutputStream extends FilterOutputStream {
      String typeName;
      FileObject fileObject;
      boolean closed = false;

      FilerOutputStream(String var2, FileObject var3) throws IOException {
         super(var3.openOutputStream());
         this.typeName = var2;
         this.fileObject = var3;
      }

      public synchronized void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            JavacFiler.this.closeFileObject(this.typeName, this.fileObject);
            this.out.close();
         }

      }
   }

   private class FilerInputJavaFileObject extends FilerInputFileObject implements JavaFileObject {
      private final JavaFileObject javaFileObject;

      FilerInputJavaFileObject(JavaFileObject var2) {
         super(var2);
         this.javaFileObject = var2;
      }

      public JavaFileObject.Kind getKind() {
         return this.javaFileObject.getKind();
      }

      public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
         return this.javaFileObject.isNameCompatible(var1, var2);
      }

      public NestingKind getNestingKind() {
         return this.javaFileObject.getNestingKind();
      }

      public Modifier getAccessLevel() {
         return this.javaFileObject.getAccessLevel();
      }
   }

   private class FilerInputFileObject extends ForwardingFileObject {
      FilerInputFileObject(FileObject var2) {
         super(var2);
      }

      public OutputStream openOutputStream() throws IOException {
         throw new IllegalStateException("FileObject was not opened for writing.");
      }

      public Writer openWriter() throws IOException {
         throw new IllegalStateException("FileObject was not opened for writing.");
      }

      public boolean delete() {
         return false;
      }
   }

   private class FilerOutputJavaFileObject extends FilerOutputFileObject implements JavaFileObject {
      private final JavaFileObject javaFileObject;

      FilerOutputJavaFileObject(String var2, JavaFileObject var3) {
         super(var2, var3);
         this.javaFileObject = var3;
      }

      public JavaFileObject.Kind getKind() {
         return this.javaFileObject.getKind();
      }

      public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
         return this.javaFileObject.isNameCompatible(var1, var2);
      }

      public NestingKind getNestingKind() {
         return this.javaFileObject.getNestingKind();
      }

      public Modifier getAccessLevel() {
         return this.javaFileObject.getAccessLevel();
      }
   }

   private class FilerOutputFileObject extends ForwardingFileObject {
      private boolean opened = false;
      private String name;

      FilerOutputFileObject(String var2, FileObject var3) {
         super(var3);
         this.name = var2;
      }

      public synchronized OutputStream openOutputStream() throws IOException {
         if (this.opened) {
            throw new IOException("Output stream or writer has already been opened.");
         } else {
            this.opened = true;
            return JavacFiler.this.new FilerOutputStream(this.name, this.fileObject);
         }
      }

      public synchronized Writer openWriter() throws IOException {
         if (this.opened) {
            throw new IOException("Output stream or writer has already been opened.");
         } else {
            this.opened = true;
            return JavacFiler.this.new FilerWriter(this.name, this.fileObject);
         }
      }

      public InputStream openInputStream() throws IOException {
         throw new IllegalStateException("FileObject was not opened for reading.");
      }

      public Reader openReader(boolean var1) throws IOException {
         throw new IllegalStateException("FileObject was not opened for reading.");
      }

      public CharSequence getCharContent(boolean var1) throws IOException {
         throw new IllegalStateException("FileObject was not opened for reading.");
      }

      public boolean delete() {
         return false;
      }
   }
}
