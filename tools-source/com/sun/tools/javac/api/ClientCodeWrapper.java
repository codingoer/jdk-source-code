package com.sun.tools.javac.api;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class ClientCodeWrapper {
   Map trustedClasses = new HashMap();

   public static ClientCodeWrapper instance(Context var0) {
      ClientCodeWrapper var1 = (ClientCodeWrapper)var0.get(ClientCodeWrapper.class);
      if (var1 == null) {
         var1 = new ClientCodeWrapper(var0);
      }

      return var1;
   }

   protected ClientCodeWrapper(Context var1) {
   }

   public JavaFileManager wrap(JavaFileManager var1) {
      return (JavaFileManager)(this.isTrusted(var1) ? var1 : new WrappedJavaFileManager(var1));
   }

   public FileObject wrap(FileObject var1) {
      return (FileObject)(this.isTrusted(var1) ? var1 : new WrappedFileObject(var1));
   }

   FileObject unwrap(FileObject var1) {
      return var1 instanceof WrappedFileObject ? ((WrappedFileObject)var1).clientFileObject : var1;
   }

   public JavaFileObject wrap(JavaFileObject var1) {
      return (JavaFileObject)(this.isTrusted(var1) ? var1 : new WrappedJavaFileObject(var1));
   }

   public Iterable wrapJavaFileObjects(Iterable var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JavaFileObject var4 = (JavaFileObject)var3.next();
         var2.add(this.wrap(var4));
      }

      return Collections.unmodifiableList(var2);
   }

   JavaFileObject unwrap(JavaFileObject var1) {
      return var1 instanceof WrappedJavaFileObject ? (JavaFileObject)((WrappedJavaFileObject)var1).clientFileObject : var1;
   }

   public DiagnosticListener wrap(DiagnosticListener var1) {
      return (DiagnosticListener)(this.isTrusted(var1) ? var1 : new WrappedDiagnosticListener(var1));
   }

   TaskListener wrap(TaskListener var1) {
      return (TaskListener)(this.isTrusted(var1) ? var1 : new WrappedTaskListener(var1));
   }

   TaskListener unwrap(TaskListener var1) {
      return var1 instanceof WrappedTaskListener ? ((WrappedTaskListener)var1).clientTaskListener : var1;
   }

   Collection unwrap(Collection var1) {
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         TaskListener var4 = (TaskListener)var3.next();
         var2.add(this.unwrap(var4));
      }

      return var2;
   }

   private Diagnostic unwrap(Diagnostic var1) {
      if (var1 instanceof JCDiagnostic) {
         JCDiagnostic var2 = (JCDiagnostic)var1;
         return new DiagnosticSourceUnwrapper(var2);
      } else {
         return var1;
      }
   }

   protected boolean isTrusted(Object var1) {
      Class var2 = var1.getClass();
      Boolean var3 = (Boolean)this.trustedClasses.get(var2);
      if (var3 == null) {
         var3 = var2.getName().startsWith("com.sun.tools.javac.") || var2.isAnnotationPresent(Trusted.class);
         this.trustedClasses.put(var2, var3);
      }

      return var3;
   }

   private String wrappedToString(Class var1, Object var2) {
      return var1.getSimpleName() + "[" + var2 + "]";
   }

   protected class WrappedTaskListener implements TaskListener {
      protected TaskListener clientTaskListener;

      WrappedTaskListener(TaskListener var2) {
         var2.getClass();
         this.clientTaskListener = var2;
      }

      public void started(TaskEvent var1) {
         try {
            this.clientTaskListener.started(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public void finished(TaskEvent var1) {
         try {
            this.clientTaskListener.finished(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public String toString() {
         return ClientCodeWrapper.this.wrappedToString(this.getClass(), this.clientTaskListener);
      }
   }

   public class DiagnosticSourceUnwrapper implements Diagnostic {
      public final JCDiagnostic d;

      DiagnosticSourceUnwrapper(JCDiagnostic var2) {
         this.d = var2;
      }

      public Diagnostic.Kind getKind() {
         return this.d.getKind();
      }

      public JavaFileObject getSource() {
         return ClientCodeWrapper.this.unwrap(this.d.getSource());
      }

      public long getPosition() {
         return this.d.getPosition();
      }

      public long getStartPosition() {
         return this.d.getStartPosition();
      }

      public long getEndPosition() {
         return this.d.getEndPosition();
      }

      public long getLineNumber() {
         return this.d.getLineNumber();
      }

      public long getColumnNumber() {
         return this.d.getColumnNumber();
      }

      public String getCode() {
         return this.d.getCode();
      }

      public String getMessage(Locale var1) {
         return this.d.getMessage(var1);
      }

      public String toString() {
         return this.d.toString();
      }
   }

   protected class WrappedDiagnosticListener implements DiagnosticListener {
      protected DiagnosticListener clientDiagnosticListener;

      WrappedDiagnosticListener(DiagnosticListener var2) {
         var2.getClass();
         this.clientDiagnosticListener = var2;
      }

      public void report(Diagnostic var1) {
         try {
            this.clientDiagnosticListener.report(ClientCodeWrapper.this.unwrap(var1));
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public String toString() {
         return ClientCodeWrapper.this.wrappedToString(this.getClass(), this.clientDiagnosticListener);
      }
   }

   protected class WrappedJavaFileObject extends WrappedFileObject implements JavaFileObject {
      WrappedJavaFileObject(JavaFileObject var2) {
         super(var2);
      }

      public JavaFileObject.Kind getKind() {
         try {
            return ((JavaFileObject)this.clientFileObject).getKind();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
         try {
            return ((JavaFileObject)this.clientFileObject).isNameCompatible(var1, var2);
         } catch (ClientCodeException var4) {
            throw var4;
         } catch (RuntimeException var5) {
            throw new ClientCodeException(var5);
         } catch (Error var6) {
            throw new ClientCodeException(var6);
         }
      }

      public NestingKind getNestingKind() {
         try {
            return ((JavaFileObject)this.clientFileObject).getNestingKind();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public Modifier getAccessLevel() {
         try {
            return ((JavaFileObject)this.clientFileObject).getAccessLevel();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public String toString() {
         return ClientCodeWrapper.this.wrappedToString(this.getClass(), this.clientFileObject);
      }
   }

   protected class WrappedFileObject implements FileObject {
      protected FileObject clientFileObject;

      WrappedFileObject(FileObject var2) {
         var2.getClass();
         this.clientFileObject = var2;
      }

      public URI toUri() {
         try {
            return this.clientFileObject.toUri();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public String getName() {
         try {
            return this.clientFileObject.getName();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public InputStream openInputStream() throws IOException {
         try {
            return this.clientFileObject.openInputStream();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public OutputStream openOutputStream() throws IOException {
         try {
            return this.clientFileObject.openOutputStream();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public Reader openReader(boolean var1) throws IOException {
         try {
            return this.clientFileObject.openReader(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public CharSequence getCharContent(boolean var1) throws IOException {
         try {
            return this.clientFileObject.getCharContent(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public Writer openWriter() throws IOException {
         try {
            return this.clientFileObject.openWriter();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public long getLastModified() {
         try {
            return this.clientFileObject.getLastModified();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public boolean delete() {
         try {
            return this.clientFileObject.delete();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public String toString() {
         return ClientCodeWrapper.this.wrappedToString(this.getClass(), this.clientFileObject);
      }
   }

   protected class WrappedJavaFileManager implements JavaFileManager {
      protected JavaFileManager clientJavaFileManager;

      WrappedJavaFileManager(JavaFileManager var2) {
         var2.getClass();
         this.clientJavaFileManager = var2;
      }

      public ClassLoader getClassLoader(JavaFileManager.Location var1) {
         try {
            return this.clientJavaFileManager.getClassLoader(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public Iterable list(JavaFileManager.Location var1, String var2, Set var3, boolean var4) throws IOException {
         try {
            return ClientCodeWrapper.this.wrapJavaFileObjects(this.clientJavaFileManager.list(var1, var2, var3, var4));
         } catch (ClientCodeException var6) {
            throw var6;
         } catch (RuntimeException var7) {
            throw new ClientCodeException(var7);
         } catch (Error var8) {
            throw new ClientCodeException(var8);
         }
      }

      public String inferBinaryName(JavaFileManager.Location var1, JavaFileObject var2) {
         try {
            return this.clientJavaFileManager.inferBinaryName(var1, ClientCodeWrapper.this.unwrap(var2));
         } catch (ClientCodeException var4) {
            throw var4;
         } catch (RuntimeException var5) {
            throw new ClientCodeException(var5);
         } catch (Error var6) {
            throw new ClientCodeException(var6);
         }
      }

      public boolean isSameFile(FileObject var1, FileObject var2) {
         try {
            return this.clientJavaFileManager.isSameFile(ClientCodeWrapper.this.unwrap(var1), ClientCodeWrapper.this.unwrap(var2));
         } catch (ClientCodeException var4) {
            throw var4;
         } catch (RuntimeException var5) {
            throw new ClientCodeException(var5);
         } catch (Error var6) {
            throw new ClientCodeException(var6);
         }
      }

      public boolean handleOption(String var1, Iterator var2) {
         try {
            return this.clientJavaFileManager.handleOption(var1, var2);
         } catch (ClientCodeException var4) {
            throw var4;
         } catch (RuntimeException var5) {
            throw new ClientCodeException(var5);
         } catch (Error var6) {
            throw new ClientCodeException(var6);
         }
      }

      public boolean hasLocation(JavaFileManager.Location var1) {
         try {
            return this.clientJavaFileManager.hasLocation(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public JavaFileObject getJavaFileForInput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3) throws IOException {
         try {
            return ClientCodeWrapper.this.wrap(this.clientJavaFileManager.getJavaFileForInput(var1, var2, var3));
         } catch (ClientCodeException var5) {
            throw var5;
         } catch (RuntimeException var6) {
            throw new ClientCodeException(var6);
         } catch (Error var7) {
            throw new ClientCodeException(var7);
         }
      }

      public JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException {
         try {
            return ClientCodeWrapper.this.wrap(this.clientJavaFileManager.getJavaFileForOutput(var1, var2, var3, ClientCodeWrapper.this.unwrap(var4)));
         } catch (ClientCodeException var6) {
            throw var6;
         } catch (RuntimeException var7) {
            throw new ClientCodeException(var7);
         } catch (Error var8) {
            throw new ClientCodeException(var8);
         }
      }

      public FileObject getFileForInput(JavaFileManager.Location var1, String var2, String var3) throws IOException {
         try {
            return ClientCodeWrapper.this.wrap(this.clientJavaFileManager.getFileForInput(var1, var2, var3));
         } catch (ClientCodeException var5) {
            throw var5;
         } catch (RuntimeException var6) {
            throw new ClientCodeException(var6);
         } catch (Error var7) {
            throw new ClientCodeException(var7);
         }
      }

      public FileObject getFileForOutput(JavaFileManager.Location var1, String var2, String var3, FileObject var4) throws IOException {
         try {
            return ClientCodeWrapper.this.wrap(this.clientJavaFileManager.getFileForOutput(var1, var2, var3, ClientCodeWrapper.this.unwrap(var4)));
         } catch (ClientCodeException var6) {
            throw var6;
         } catch (RuntimeException var7) {
            throw new ClientCodeException(var7);
         } catch (Error var8) {
            throw new ClientCodeException(var8);
         }
      }

      public void flush() throws IOException {
         try {
            this.clientJavaFileManager.flush();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public void close() throws IOException {
         try {
            this.clientJavaFileManager.close();
         } catch (ClientCodeException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw new ClientCodeException(var3);
         } catch (Error var4) {
            throw new ClientCodeException(var4);
         }
      }

      public int isSupportedOption(String var1) {
         try {
            return this.clientJavaFileManager.isSupportedOption(var1);
         } catch (ClientCodeException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw new ClientCodeException(var4);
         } catch (Error var5) {
            throw new ClientCodeException(var5);
         }
      }

      public String toString() {
         return ClientCodeWrapper.this.wrappedToString(this.getClass(), this.clientJavaFileManager);
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface Trusted {
   }
}
