package sun.rmi.rmic.newrmic;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BatchEnvironment {
   private final RootDoc rootDoc;
   private final ClassDoc docRemote;
   private final ClassDoc docException;
   private final ClassDoc docRemoteException;
   private final ClassDoc docRuntimeException;
   private boolean verbose = false;
   private final List generatedFiles = new ArrayList();

   public BatchEnvironment(RootDoc var1) {
      this.rootDoc = var1;
      this.docRemote = this.rootDoc().classNamed("java.rmi.Remote");
      this.docException = this.rootDoc().classNamed("java.lang.Exception");
      this.docRemoteException = this.rootDoc().classNamed("java.rmi.RemoteException");
      this.docRuntimeException = this.rootDoc().classNamed("java.lang.RuntimeException");
   }

   public RootDoc rootDoc() {
      return this.rootDoc;
   }

   public ClassDoc docRemote() {
      return this.docRemote;
   }

   public ClassDoc docException() {
      return this.docException;
   }

   public ClassDoc docRemoteException() {
      return this.docRemoteException;
   }

   public ClassDoc docRuntimeException() {
      return this.docRuntimeException;
   }

   public void setVerbose(boolean var1) {
      this.verbose = var1;
   }

   public boolean verbose() {
      return this.verbose;
   }

   public void addGeneratedFile(File var1) {
      this.generatedFiles.add(var1);
   }

   public List generatedFiles() {
      return Collections.unmodifiableList(this.generatedFiles);
   }

   public void output(String var1) {
      this.rootDoc.printNotice(var1);
   }

   public void error(String var1, String... var2) {
      this.rootDoc.printError(Resources.getText(var1, var2));
   }
}
