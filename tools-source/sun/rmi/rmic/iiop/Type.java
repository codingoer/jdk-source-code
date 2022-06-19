package sun.rmi.rmic.iiop;

import com.sun.corba.se.impl.util.RepositoryId;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;
import sun.rmi.rmic.IndentingWriter;
import sun.rmi.rmic.Names;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Identifier;

public abstract class Type implements Constants, ContextElement, Cloneable {
   private int typeCode;
   private int fullTypeCode;
   private Identifier id;
   private String name;
   private String packageName;
   private String qualifiedName;
   private String idlName;
   private String[] idlModuleNames;
   private String qualifiedIDLName;
   private String repositoryID;
   private Class ourClass;
   private int status = 0;
   protected BatchEnvironment env;
   protected ContextStack stack;
   protected boolean destroyed = false;

   public String getName() {
      return this.name;
   }

   public String getPackageName() {
      return this.packageName;
   }

   public String getQualifiedName() {
      return this.qualifiedName;
   }

   public abstract String getSignature();

   public String getIDLName() {
      return this.idlName;
   }

   public String[] getIDLModuleNames() {
      return this.idlModuleNames;
   }

   public String getQualifiedIDLName(boolean var1) {
      return var1 && this.getIDLModuleNames().length > 0 ? "::" + this.qualifiedIDLName : this.qualifiedIDLName;
   }

   public Identifier getIdentifier() {
      return this.id;
   }

   public String getRepositoryID() {
      return this.repositoryID;
   }

   public String getBoxedRepositoryID() {
      return RepositoryId.createForJavaType(this.ourClass);
   }

   public Class getClassInstance() {
      if (this.ourClass == null) {
         this.initClass();
      }

      return this.ourClass;
   }

   public int getStatus() {
      return this.status;
   }

   public void setStatus(int var1) {
      this.status = var1;
   }

   public BatchEnvironment getEnv() {
      return this.env;
   }

   public int getTypeCode() {
      return this.typeCode;
   }

   public int getFullTypeCode() {
      return this.fullTypeCode;
   }

   public int getTypeCodeModifiers() {
      return this.fullTypeCode & -16777216;
   }

   public boolean isType(int var1) {
      return (this.fullTypeCode & var1) == var1;
   }

   public boolean typeMatches(int var1) {
      return (this.fullTypeCode & var1) > 0;
   }

   public int getRootTypeCode() {
      return this.isArray() ? this.getElementType().getFullTypeCode() : this.fullTypeCode;
   }

   public boolean isInterface() {
      return (this.fullTypeCode & 134217728) == 134217728;
   }

   public boolean isClass() {
      return (this.fullTypeCode & 67108864) == 67108864;
   }

   public boolean isInner() {
      return (this.fullTypeCode & Integer.MIN_VALUE) == Integer.MIN_VALUE;
   }

   public boolean isSpecialInterface() {
      return (this.fullTypeCode & 536870912) == 536870912;
   }

   public boolean isSpecialClass() {
      return (this.fullTypeCode & 268435456) == 268435456;
   }

   public boolean isCompound() {
      return (this.fullTypeCode & 33554432) == 33554432;
   }

   public boolean isPrimitive() {
      return (this.fullTypeCode & 16777216) == 16777216;
   }

   public boolean isArray() {
      return (this.fullTypeCode & 262144) == 262144;
   }

   public boolean isConforming() {
      return (this.fullTypeCode & 1073741824) == 1073741824;
   }

   public String toString() {
      return this.getQualifiedName();
   }

   public Type getElementType() {
      return null;
   }

   public int getArrayDimension() {
      return 0;
   }

   public String getArrayBrackets() {
      return "";
   }

   public boolean equals(Object var1) {
      String var2 = this.toString();
      String var3 = ((Type)var1).toString();
      return var2.equals(var3);
   }

   public Type[] collectMatching(int var1) {
      return this.collectMatching(var1, new HashSet(this.env.allTypes.size()));
   }

   public Type[] collectMatching(int var1, HashSet var2) {
      Vector var3 = new Vector();
      this.addTypes(var1, var2, var3);
      Type[] var4 = new Type[var3.size()];
      var3.copyInto(var4);
      return var4;
   }

   public abstract String getTypeDescription();

   public String getTypeName(boolean var1, boolean var2, boolean var3) {
      if (var2) {
         return var1 ? this.getQualifiedIDLName(var3) : this.getIDLName();
      } else {
         return var1 ? this.getQualifiedName() : this.getName();
      }
   }

   public void print(IndentingWriter var1, int var2, boolean var3, boolean var4, boolean var5) throws IOException {
      Type[] var6 = this.collectMatching(var2);
      print(var1, var6, var3, var4, var5);
   }

   public static void print(IndentingWriter var0, Type[] var1, boolean var2, boolean var3, boolean var4) throws IOException {
      for(int var5 = 0; var5 < var1.length; ++var5) {
         var1[var5].println(var0, var2, var3, var4);
      }

   }

   public void print(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      this.printTypeName(var1, var2, var3, var4);
   }

   public void println(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      this.print(var1, var2, var3, var4);
      var1.pln();
   }

   public void printTypeName(IndentingWriter var1, boolean var2, boolean var3, boolean var4) throws IOException {
      var1.p(this.getTypeName(var2, var3, var4));
   }

   public String getElementName() {
      return this.getQualifiedName();
   }

   protected void printPackageOpen(IndentingWriter var1, boolean var2) throws IOException {
      if (var2) {
         String[] var3 = this.getIDLModuleNames();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var1.plnI("module " + var3[var4] + " {");
         }
      } else {
         String var5 = this.getPackageName();
         if (var5 != null) {
            var1.pln("package " + var5 + ";");
         }
      }

   }

   protected static Type getType(sun.tools.java.Type var0, ContextStack var1) {
      return getType(var0.toString(), var1);
   }

   protected static Type getType(String var0, ContextStack var1) {
      Type var2 = (Type)var1.getEnv().allTypes.get(var0);
      if (var2 != null) {
         var1.traceExistingType(var2);
      }

      return var2;
   }

   protected static void removeType(String var0, ContextStack var1) {
      Type var2 = (Type)var1.getEnv().allTypes.remove(var0);
      var1.getEnv().invalidTypes.put(var2, var0);
   }

   protected static void removeType(sun.tools.java.Type var0, ContextStack var1) {
      String var2 = var0.toString();
      Type var3 = (Type)var1.getEnv().allTypes.remove(var2);
      putInvalidType(var3, var2, var1);
   }

   protected static void putType(sun.tools.java.Type var0, Type var1, ContextStack var2) {
      var2.getEnv().allTypes.put(var0.toString(), var1);
   }

   protected static void putType(String var0, Type var1, ContextStack var2) {
      var2.getEnv().allTypes.put(var0, var1);
   }

   protected static void putInvalidType(Type var0, String var1, ContextStack var2) {
      var2.getEnv().invalidTypes.put(var0, var1);
   }

   public void removeInvalidTypes() {
      if (this.env.invalidTypes.size() > 0) {
         this.env.invalidTypes.clear();
      }

   }

   protected static void updateAllInvalidTypes(ContextStack var0) {
      BatchEnvironment var1 = var0.getEnv();
      if (var1.invalidTypes.size() > 0) {
         Enumeration var2 = var1.allTypes.elements();

         while(var2.hasMoreElements()) {
            Type var3 = (Type)var2.nextElement();
            var3.swapInvalidTypes();
         }

         var1.invalidTypes.clear();
      }

   }

   protected int countTypes() {
      return this.env.allTypes.size();
   }

   void resetTypes() {
      this.env.reset();
   }

   protected void destroy() {
      if (!this.destroyed) {
         this.id = null;
         this.name = null;
         this.packageName = null;
         this.qualifiedName = null;
         this.idlName = null;
         this.idlModuleNames = null;
         this.qualifiedIDLName = null;
         this.repositoryID = null;
         this.ourClass = null;
         this.env = null;
         this.stack = null;
         this.destroyed = true;
      }

   }

   protected void swapInvalidTypes() {
   }

   protected Type getValidType(Type var1) {
      if (var1.getStatus() == 1) {
         return var1;
      } else {
         String var2 = (String)this.env.invalidTypes.get(var1);
         Type var3 = null;
         if (var2 != null) {
            var3 = (Type)this.env.allTypes.get(var2);
         }

         if (var3 == null) {
            throw new Error("Failed to find valid type to swap for " + var1 + " mis-identified as " + var1.getTypeDescription());
         } else {
            return var3;
         }
      }
   }

   protected void printPackageClose(IndentingWriter var1, boolean var2) throws IOException {
      if (var2) {
         String[] var3 = this.getIDLModuleNames();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var1.pOln("};");
         }
      }

   }

   protected Type(ContextStack var1, int var2) {
      this.env = var1.getEnv();
      this.stack = var1;
      this.fullTypeCode = var2;
      this.typeCode = var2 & 16777215;
   }

   protected void setTypeCode(int var1) {
      this.fullTypeCode = var1;
      this.typeCode = var1 & 16777215;
   }

   protected void setNames(Identifier var1, String[] var2, String var3) {
      this.id = var1;
      this.name = Names.mangleClass(var1).getName().toString();
      this.packageName = null;
      if (var1.isQualified()) {
         this.packageName = var1.getQualifier().toString();
         this.qualifiedName = this.packageName + "." + this.name;
      } else {
         this.qualifiedName = this.name;
      }

      this.setIDLNames(var2, var3);
   }

   protected void setIDLNames(String[] var1, String var2) {
      this.idlName = var2;
      if (var1 != null) {
         this.idlModuleNames = var1;
      } else {
         this.idlModuleNames = new String[0];
      }

      this.qualifiedIDLName = IDLNames.getQualifiedName(var1, var2);
   }

   protected static void classNotFound(ContextStack var0, ClassNotFound var1) {
      classNotFound(false, var0, var1);
   }

   protected static void classNotFound(boolean var0, ContextStack var1, ClassNotFound var2) {
      if (!var0) {
         var1.getEnv().error(0L, "rmic.class.not.found", var2.name);
      }

      var1.traceCallStack();
   }

   protected static boolean failedConstraint(int var0, boolean var1, ContextStack var2, Object var3, Object var4, Object var5) {
      String var6 = "rmic.iiop.constraint." + var0;
      if (!var1) {
         var2.getEnv().error(0L, var6, var3 != null ? var3.toString() : null, var4 != null ? var4.toString() : null, var5 != null ? var5.toString() : null);
      } else {
         String var7 = var2.getEnv().errorString(var6, var3, var4, var5);
         var2.traceln(var7);
      }

      return false;
   }

   protected static boolean failedConstraint(int var0, boolean var1, ContextStack var2, Object var3, Object var4) {
      return failedConstraint(var0, var1, var2, var3, var4, (Object)null);
   }

   protected static boolean failedConstraint(int var0, boolean var1, ContextStack var2, Object var3) {
      return failedConstraint(var0, var1, var2, var3, (Object)null, (Object)null);
   }

   protected static boolean failedConstraint(int var0, boolean var1, ContextStack var2) {
      return failedConstraint(var0, var1, var2, (Object)null, (Object)null, (Object)null);
   }

   protected Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new Error("clone failed");
      }
   }

   protected boolean addTypes(int var1, HashSet var2, Vector var3) {
      boolean var4;
      if (var2.contains(this)) {
         var4 = false;
      } else {
         var2.add(this);
         if (this.typeMatches(var1)) {
            var3.addElement(this);
         }

         var4 = true;
      }

      return var4;
   }

   protected abstract Class loadClass();

   private boolean initClass() {
      if (this.ourClass == null) {
         this.ourClass = this.loadClass();
         if (this.ourClass == null) {
            failedConstraint(27, false, this.stack, this.getQualifiedName());
            return false;
         }
      }

      return true;
   }

   protected boolean setRepositoryID() {
      if (!this.initClass()) {
         return false;
      } else {
         this.repositoryID = RepositoryId.createForAnyType(this.ourClass);
         return true;
      }
   }

   private Type() {
   }
}
