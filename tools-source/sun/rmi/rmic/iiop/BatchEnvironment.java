package sun.rmi.rmic.iiop;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import sun.rmi.rmic.Main;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.ClassPath;

public class BatchEnvironment extends sun.rmi.rmic.BatchEnvironment implements Constants {
   private boolean parseNonConforming = false;
   private boolean standardPackage;
   HashSet alreadyChecked = new HashSet();
   Hashtable allTypes = new Hashtable(3001, 0.5F);
   Hashtable invalidTypes = new Hashtable(256, 0.5F);
   DirectoryLoader loader = null;
   ClassPathLoader classPathLoader = null;
   Hashtable nameContexts = null;
   Hashtable namesCache = new Hashtable();
   NameContext modulesContext = new NameContext(false);
   ClassDefinition defRemote = null;
   ClassDefinition defError = null;
   ClassDefinition defException = null;
   ClassDefinition defRemoteException = null;
   ClassDefinition defCorbaObject = null;
   ClassDefinition defSerializable = null;
   ClassDefinition defExternalizable = null;
   ClassDefinition defThrowable = null;
   ClassDefinition defRuntimeException = null;
   ClassDefinition defIDLEntity = null;
   ClassDefinition defValueBase = null;
   sun.tools.java.Type typeRemoteException = null;
   sun.tools.java.Type typeIOException = null;
   sun.tools.java.Type typeException = null;
   sun.tools.java.Type typeThrowable = null;
   ContextStack contextStack = null;

   public BatchEnvironment(OutputStream var1, ClassPath var2, Main var3) {
      super(var1, var2, var3);

      try {
         this.defRemote = this.getClassDeclaration(idRemote).getClassDefinition(this);
         this.defError = this.getClassDeclaration(idJavaLangError).getClassDefinition(this);
         this.defException = this.getClassDeclaration(idJavaLangException).getClassDefinition(this);
         this.defRemoteException = this.getClassDeclaration(idRemoteException).getClassDefinition(this);
         this.defCorbaObject = this.getClassDeclaration(idCorbaObject).getClassDefinition(this);
         this.defSerializable = this.getClassDeclaration(idJavaIoSerializable).getClassDefinition(this);
         this.defRuntimeException = this.getClassDeclaration(idJavaLangRuntimeException).getClassDefinition(this);
         this.defExternalizable = this.getClassDeclaration(idJavaIoExternalizable).getClassDefinition(this);
         this.defThrowable = this.getClassDeclaration(idJavaLangThrowable).getClassDefinition(this);
         this.defIDLEntity = this.getClassDeclaration(idIDLEntity).getClassDefinition(this);
         this.defValueBase = this.getClassDeclaration(idValueBase).getClassDefinition(this);
         this.typeRemoteException = this.defRemoteException.getClassDeclaration().getType();
         this.typeException = this.defException.getClassDeclaration().getType();
         this.typeIOException = this.getClassDeclaration(idJavaIoIOException).getType();
         this.typeThrowable = this.getClassDeclaration(idJavaLangThrowable).getType();
         this.classPathLoader = new ClassPathLoader(var2);
      } catch (ClassNotFound var5) {
         this.error(0L, "rmic.class.not.found", var5.name);
         throw new Error();
      }
   }

   public boolean getParseNonConforming() {
      return this.parseNonConforming;
   }

   public void setParseNonConforming(boolean var1) {
      if (var1 && !this.parseNonConforming) {
         this.reset();
      }

      this.parseNonConforming = var1;
   }

   void setStandardPackage(boolean var1) {
      this.standardPackage = var1;
   }

   boolean getStandardPackage() {
      return this.standardPackage;
   }

   public void reset() {
      Enumeration var1 = this.allTypes.elements();

      Type var2;
      while(var1.hasMoreElements()) {
         var2 = (Type)var1.nextElement();
         var2.destroy();
      }

      var1 = this.invalidTypes.keys();

      while(var1.hasMoreElements()) {
         var2 = (Type)var1.nextElement();
         var2.destroy();
      }

      Iterator var3 = this.alreadyChecked.iterator();

      while(var3.hasNext()) {
         var2 = (Type)var3.next();
         var2.destroy();
      }

      if (this.contextStack != null) {
         this.contextStack.clear();
      }

      if (this.nameContexts != null) {
         var1 = this.nameContexts.elements();

         while(var1.hasMoreElements()) {
            NameContext var4 = (NameContext)var1.nextElement();
            var4.clear();
         }

         this.nameContexts.clear();
      }

      this.allTypes.clear();
      this.invalidTypes.clear();
      this.alreadyChecked.clear();
      this.namesCache.clear();
      this.modulesContext.clear();
      this.loader = null;
      this.parseNonConforming = false;
   }

   public void shutdown() {
      if (this.alreadyChecked != null) {
         this.reset();
         this.alreadyChecked = null;
         this.allTypes = null;
         this.invalidTypes = null;
         this.nameContexts = null;
         this.namesCache = null;
         this.modulesContext = null;
         this.defRemote = null;
         this.defError = null;
         this.defException = null;
         this.defRemoteException = null;
         this.defCorbaObject = null;
         this.defSerializable = null;
         this.defExternalizable = null;
         this.defThrowable = null;
         this.defRuntimeException = null;
         this.defIDLEntity = null;
         this.defValueBase = null;
         this.typeRemoteException = null;
         this.typeIOException = null;
         this.typeException = null;
         this.typeThrowable = null;
         super.shutdown();
      }

   }
}
