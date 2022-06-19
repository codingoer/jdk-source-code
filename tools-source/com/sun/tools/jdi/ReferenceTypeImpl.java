package com.sun.tools.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.Field;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InternalException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ReferenceTypeImpl extends TypeImpl implements ReferenceType {
   protected long ref;
   private String signature = null;
   private String genericSignature = null;
   private boolean genericSignatureGotten = false;
   private String baseSourceName = null;
   private String baseSourceDir = null;
   private String baseSourcePath = null;
   protected int modifiers = -1;
   private SoftReference fieldsRef = null;
   private SoftReference methodsRef = null;
   private SoftReference sdeRef = null;
   private boolean isClassLoaderCached = false;
   private ClassLoaderReference classLoader = null;
   private ClassObjectReference classObject = null;
   private int status = 0;
   private boolean isPrepared = false;
   private boolean versionNumberGotten = false;
   private int majorVersion;
   private int minorVersion;
   private boolean constantPoolInfoGotten = false;
   private int constanPoolCount;
   private byte[] constantPoolBytes;
   private SoftReference constantPoolBytesRef = null;
   private static final String ABSENT_BASE_SOURCE_NAME = "**ABSENT_BASE_SOURCE_NAME**";
   static final SDE NO_SDE_INFO_MARK = new SDE();
   private static final int INITIALIZED_OR_FAILED = 12;

   protected ReferenceTypeImpl(VirtualMachine var1, long var2) {
      super(var1);
      this.ref = var2;
      this.genericSignatureGotten = false;
   }

   void noticeRedefineClass() {
      this.baseSourceName = null;
      this.baseSourcePath = null;
      this.modifiers = -1;
      this.fieldsRef = null;
      this.methodsRef = null;
      this.sdeRef = null;
      this.versionNumberGotten = false;
      this.constantPoolInfoGotten = false;
   }

   Method getMethodMirror(long var1) {
      if (var1 == 0L) {
         return new ObsoleteMethodImpl(this.vm, this);
      } else {
         Iterator var3 = this.methods().iterator();

         MethodImpl var4;
         do {
            if (!var3.hasNext()) {
               throw new IllegalArgumentException("Invalid method id: " + var1);
            }

            var4 = (MethodImpl)var3.next();
         } while(var4.ref() != var1);

         return var4;
      }
   }

   Field getFieldMirror(long var1) {
      Iterator var3 = this.fields().iterator();

      FieldImpl var4;
      do {
         if (!var3.hasNext()) {
            throw new IllegalArgumentException("Invalid field id: " + var1);
         }

         var4 = (FieldImpl)var3.next();
      } while(var4.ref() != var1);

      return var4;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof ReferenceTypeImpl) {
         ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1;
         return this.ref() == var2.ref() && this.vm.equals(var2.virtualMachine());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)this.ref();
   }

   public int compareTo(ReferenceType var1) {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1;
      int var3 = this.name().compareTo(var2.name());
      if (var3 == 0) {
         long var4 = this.ref();
         long var6 = var2.ref();
         if (var4 == var6) {
            var3 = this.vm.sequenceNumber - ((VirtualMachineImpl)((VirtualMachineImpl)var2.virtualMachine())).sequenceNumber;
         } else {
            var3 = var4 < var6 ? -1 : 1;
         }
      }

      return var3;
   }

   public String signature() {
      if (this.signature == null) {
         if (this.vm.canGet1_5LanguageFeatures()) {
            this.genericSignature();
         } else {
            try {
               this.signature = JDWP.ReferenceType.Signature.process(this.vm, this).signature;
            } catch (JDWPException var2) {
               throw var2.toJDIException();
            }
         }
      }

      return this.signature;
   }

   public String genericSignature() {
      if (this.vm.canGet1_5LanguageFeatures() && !this.genericSignatureGotten) {
         JDWP.ReferenceType.SignatureWithGeneric var1;
         try {
            var1 = JDWP.ReferenceType.SignatureWithGeneric.process(this.vm, this);
         } catch (JDWPException var3) {
            throw var3.toJDIException();
         }

         this.signature = var1.signature;
         this.setGenericSignature(var1.genericSignature);
      }

      return this.genericSignature;
   }

   public ClassLoaderReference classLoader() {
      if (!this.isClassLoaderCached) {
         try {
            this.classLoader = JDWP.ReferenceType.ClassLoader.process(this.vm, this).classLoader;
            this.isClassLoaderCached = true;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.classLoader;
   }

   public boolean isPublic() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return (this.modifiers & 1) > 0;
   }

   public boolean isProtected() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return (this.modifiers & 4) > 0;
   }

   public boolean isPrivate() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return (this.modifiers & 2) > 0;
   }

   public boolean isPackagePrivate() {
      return !this.isPublic() && !this.isPrivate() && !this.isProtected();
   }

   public boolean isAbstract() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return (this.modifiers & 1024) > 0;
   }

   public boolean isFinal() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return (this.modifiers & 16) > 0;
   }

   public boolean isStatic() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return (this.modifiers & 8) > 0;
   }

   public boolean isPrepared() {
      if (this.status == 0) {
         this.updateStatus();
      }

      return this.isPrepared;
   }

   public boolean isVerified() {
      if ((this.status & 1) == 0) {
         this.updateStatus();
      }

      return (this.status & 1) != 0;
   }

   public boolean isInitialized() {
      if ((this.status & 12) == 0) {
         this.updateStatus();
      }

      return (this.status & 4) != 0;
   }

   public boolean failedToInitialize() {
      if ((this.status & 12) == 0) {
         this.updateStatus();
      }

      return (this.status & 8) != 0;
   }

   public List fields() {
      List var1 = this.fieldsRef == null ? null : (List)this.fieldsRef.get();
      if (var1 == null) {
         int var3;
         FieldImpl var5;
         ArrayList var8;
         if (this.vm.canGet1_5LanguageFeatures()) {
            JDWP.ReferenceType.FieldsWithGeneric.FieldInfo[] var9;
            try {
               var9 = JDWP.ReferenceType.FieldsWithGeneric.process(this.vm, this).declared;
            } catch (JDWPException var7) {
               throw var7.toJDIException();
            }

            var8 = new ArrayList(var9.length);

            for(var3 = 0; var3 < var9.length; ++var3) {
               JDWP.ReferenceType.FieldsWithGeneric.FieldInfo var10 = var9[var3];
               var5 = new FieldImpl(this.vm, this, var10.fieldID, var10.name, var10.signature, var10.genericSignature, var10.modBits);
               var8.add(var5);
            }
         } else {
            JDWP.ReferenceType.Fields.FieldInfo[] var2;
            try {
               var2 = JDWP.ReferenceType.Fields.process(this.vm, this).declared;
            } catch (JDWPException var6) {
               throw var6.toJDIException();
            }

            var8 = new ArrayList(var2.length);

            for(var3 = 0; var3 < var2.length; ++var3) {
               JDWP.ReferenceType.Fields.FieldInfo var4 = var2[var3];
               var5 = new FieldImpl(this.vm, this, var4.fieldID, var4.name, var4.signature, (String)null, var4.modBits);
               var8.add(var5);
            }
         }

         var1 = Collections.unmodifiableList(var8);
         this.fieldsRef = new SoftReference(var1);
      }

      return var1;
   }

   abstract List inheritedTypes();

   void addVisibleFields(List var1, Map var2, List var3) {
      Iterator var4 = this.visibleFields().iterator();

      while(var4.hasNext()) {
         Field var5 = (Field)var4.next();
         String var6 = var5.name();
         if (!var3.contains(var6)) {
            Field var7 = (Field)var2.get(var6);
            if (var7 == null) {
               var1.add(var5);
               var2.put(var6, var5);
            } else if (!var5.equals(var7)) {
               var3.add(var6);
               var2.remove(var6);
               var1.remove(var7);
            }
         }
      }

   }

   public List visibleFields() {
      ArrayList var1 = new ArrayList();
      HashMap var2 = new HashMap();
      ArrayList var3 = new ArrayList();
      List var4 = this.inheritedTypes();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         ReferenceTypeImpl var6 = (ReferenceTypeImpl)var5.next();
         var6.addVisibleFields(var1, var2, var3);
      }

      ArrayList var10 = new ArrayList(this.fields());
      Iterator var7 = var10.iterator();

      while(var7.hasNext()) {
         Field var8 = (Field)var7.next();
         Field var9 = (Field)var2.get(var8.name());
         if (var9 != null) {
            var1.remove(var9);
         }
      }

      var10.addAll(var1);
      return var10;
   }

   void addAllFields(List var1, Set var2) {
      if (!var2.contains(this)) {
         var2.add(this);
         var1.addAll(this.fields());
         List var3 = this.inheritedTypes();
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ReferenceTypeImpl var5 = (ReferenceTypeImpl)var4.next();
            var5.addAllFields(var1, var2);
         }
      }

   }

   public List allFields() {
      ArrayList var1 = new ArrayList();
      HashSet var2 = new HashSet();
      this.addAllFields(var1, var2);
      return var1;
   }

   public Field fieldByName(String var1) {
      List var2 = this.visibleFields();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Field var4 = (Field)var2.get(var3);
         if (var4.name().equals(var1)) {
            return var4;
         }
      }

      return null;
   }

   public List methods() {
      List var1 = this.methodsRef == null ? null : (List)this.methodsRef.get();
      if (var1 == null) {
         Object var7;
         if (!this.vm.canGet1_5LanguageFeatures()) {
            var7 = this.methods1_4();
         } else {
            JDWP.ReferenceType.MethodsWithGeneric.MethodInfo[] var2;
            try {
               var2 = JDWP.ReferenceType.MethodsWithGeneric.process(this.vm, this).declared;
            } catch (JDWPException var6) {
               throw var6.toJDIException();
            }

            var7 = new ArrayList(var2.length);

            for(int var3 = 0; var3 < var2.length; ++var3) {
               JDWP.ReferenceType.MethodsWithGeneric.MethodInfo var4 = var2[var3];
               MethodImpl var5 = MethodImpl.createMethodImpl(this.vm, this, var4.methodID, var4.name, var4.signature, var4.genericSignature, var4.modBits);
               ((List)var7).add(var5);
            }
         }

         var1 = Collections.unmodifiableList((List)var7);
         this.methodsRef = new SoftReference(var1);
      }

      return var1;
   }

   private List methods1_4() {
      JDWP.ReferenceType.Methods.MethodInfo[] var2;
      try {
         var2 = JDWP.ReferenceType.Methods.process(this.vm, this).declared;
      } catch (JDWPException var6) {
         throw var6.toJDIException();
      }

      ArrayList var1 = new ArrayList(var2.length);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         JDWP.ReferenceType.Methods.MethodInfo var4 = var2[var3];
         MethodImpl var5 = MethodImpl.createMethodImpl(this.vm, this, var4.methodID, var4.name, var4.signature, (String)null, var4.modBits);
         var1.add(var5);
      }

      return var1;
   }

   void addToMethodMap(Map var1, List var2) {
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Method var4 = (Method)var3.next();
         var1.put(var4.name().concat(var4.signature()), var4);
      }

   }

   abstract void addVisibleMethods(Map var1, Set var2);

   public List visibleMethods() {
      HashMap var1 = new HashMap();
      this.addVisibleMethods(var1, new HashSet());
      List var2 = this.allMethods();
      var2.retainAll(var1.values());
      return var2;
   }

   public abstract List allMethods();

   public List methodsByName(String var1) {
      List var2 = this.visibleMethods();
      ArrayList var3 = new ArrayList(var2.size());
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Method var5 = (Method)var4.next();
         if (var5.name().equals(var1)) {
            var3.add(var5);
         }
      }

      var3.trimToSize();
      return var3;
   }

   public List methodsByName(String var1, String var2) {
      List var3 = this.visibleMethods();
      ArrayList var4 = new ArrayList(var3.size());
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Method var6 = (Method)var5.next();
         if (var6.name().equals(var1) && var6.signature().equals(var2)) {
            var4.add(var6);
         }
      }

      var4.trimToSize();
      return var4;
   }

   List getInterfaces() {
      InterfaceTypeImpl[] var1;
      try {
         var1 = JDWP.ReferenceType.Interfaces.process(this.vm, this).interfaces;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }

      return Arrays.asList((InterfaceType[])var1);
   }

   public List nestedTypes() {
      List var1 = this.vm.allClasses();
      ArrayList var2 = new ArrayList();
      String var3 = this.name();
      int var4 = var3.length();
      Iterator var5 = var1.iterator();

      while(true) {
         ReferenceType var6;
         char var9;
         do {
            String var7;
            int var8;
            do {
               do {
                  if (!var5.hasNext()) {
                     return var2;
                  }

                  var6 = (ReferenceType)var5.next();
                  var7 = var6.name();
                  var8 = var7.length();
               } while(var8 <= var4);
            } while(!var7.startsWith(var3));

            var9 = var7.charAt(var4);
         } while(var9 != '$' && var9 != '#');

         var2.add(var6);
      }
   }

   public Value getValue(Field var1) {
      ArrayList var2 = new ArrayList(1);
      var2.add(var1);
      Map var3 = this.getValues(var2);
      return (Value)var3.get(var1);
   }

   void validateFieldAccess(Field var1) {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1.declaringType();
      if (!var2.isAssignableFrom((ReferenceType)this)) {
         throw new IllegalArgumentException("Invalid field");
      }
   }

   void validateFieldSet(Field var1) {
      this.validateFieldAccess(var1);
      if (var1.isFinal()) {
         throw new IllegalArgumentException("Cannot set value of final field");
      }
   }

   public Map getValues(List var1) {
      this.validateMirrors(var1);
      int var2 = var1.size();
      JDWP.ReferenceType.GetValues.Field[] var3 = new JDWP.ReferenceType.GetValues.Field[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         FieldImpl var5 = (FieldImpl)var1.get(var4);
         this.validateFieldAccess(var5);
         if (!var5.isStatic()) {
            throw new IllegalArgumentException("Attempt to use non-static field with ReferenceType");
         }

         var3[var4] = new JDWP.ReferenceType.GetValues.Field(var5.ref());
      }

      HashMap var9 = new HashMap(var2);

      ValueImpl[] var10;
      try {
         var10 = JDWP.ReferenceType.GetValues.process(this.vm, this, var3).values;
      } catch (JDWPException var8) {
         throw var8.toJDIException();
      }

      if (var2 != var10.length) {
         throw new InternalException("Wrong number of values returned from target VM");
      } else {
         for(int var6 = 0; var6 < var2; ++var6) {
            FieldImpl var7 = (FieldImpl)var1.get(var6);
            var9.put(var7, var10[var6]);
         }

         return var9;
      }
   }

   public ClassObjectReference classObject() {
      if (this.classObject == null) {
         synchronized(this) {
            if (this.classObject == null) {
               try {
                  this.classObject = JDWP.ReferenceType.ClassObject.process(this.vm, this).classObject;
               } catch (JDWPException var4) {
                  throw var4.toJDIException();
               }
            }
         }
      }

      return this.classObject;
   }

   SDE.Stratum stratum(String var1) {
      SDE var2 = this.sourceDebugExtensionInfo();
      if (!var2.isValid()) {
         var2 = NO_SDE_INFO_MARK;
      }

      return var2.stratum(var1);
   }

   public String sourceName() throws AbsentInformationException {
      return (String)this.sourceNames(this.vm.getDefaultStratum()).get(0);
   }

   public List sourceNames(String var1) throws AbsentInformationException {
      SDE.Stratum var2 = this.stratum(var1);
      if (var2.isJava()) {
         ArrayList var3 = new ArrayList(1);
         var3.add(this.baseSourceName());
         return var3;
      } else {
         return var2.sourceNames(this);
      }
   }

   public List sourcePaths(String var1) throws AbsentInformationException {
      SDE.Stratum var2 = this.stratum(var1);
      if (var2.isJava()) {
         ArrayList var3 = new ArrayList(1);
         var3.add(this.baseSourceDir() + this.baseSourceName());
         return var3;
      } else {
         return var2.sourcePaths(this);
      }
   }

   String baseSourceName() throws AbsentInformationException {
      String var1 = this.baseSourceName;
      if (var1 == null) {
         try {
            var1 = JDWP.ReferenceType.SourceFile.process(this.vm, this).sourceFile;
         } catch (JDWPException var3) {
            if (var3.errorCode() != 101) {
               throw var3.toJDIException();
            }

            var1 = "**ABSENT_BASE_SOURCE_NAME**";
         }

         this.baseSourceName = var1;
      }

      if (var1 == "**ABSENT_BASE_SOURCE_NAME**") {
         throw new AbsentInformationException();
      } else {
         return var1;
      }
   }

   String baseSourcePath() throws AbsentInformationException {
      String var1 = this.baseSourcePath;
      if (var1 == null) {
         var1 = this.baseSourceDir() + this.baseSourceName();
         this.baseSourcePath = var1;
      }

      return var1;
   }

   String baseSourceDir() {
      if (this.baseSourceDir == null) {
         String var1 = this.name();
         StringBuffer var2 = new StringBuffer(var1.length() + 10);

         int var4;
         for(int var3 = 0; (var4 = var1.indexOf(46, var3)) > 0; var3 = var4 + 1) {
            var2.append(var1.substring(var3, var4));
            var2.append(File.separatorChar);
         }

         this.baseSourceDir = var2.toString();
      }

      return this.baseSourceDir;
   }

   public String sourceDebugExtension() throws AbsentInformationException {
      if (!this.vm.canGetSourceDebugExtension()) {
         throw new UnsupportedOperationException();
      } else {
         SDE var1 = this.sourceDebugExtensionInfo();
         if (var1 == NO_SDE_INFO_MARK) {
            throw new AbsentInformationException();
         } else {
            return var1.sourceDebugExtension;
         }
      }
   }

   private SDE sourceDebugExtensionInfo() {
      if (!this.vm.canGetSourceDebugExtension()) {
         return NO_SDE_INFO_MARK;
      } else {
         SDE var1 = this.sdeRef == null ? null : (SDE)this.sdeRef.get();
         if (var1 == null) {
            String var2 = null;

            try {
               var2 = JDWP.ReferenceType.SourceDebugExtension.process(this.vm, this).extension;
            } catch (JDWPException var4) {
               if (var4.errorCode() != 101) {
                  this.sdeRef = new SoftReference(NO_SDE_INFO_MARK);
                  throw var4.toJDIException();
               }
            }

            if (var2 == null) {
               var1 = NO_SDE_INFO_MARK;
            } else {
               var1 = new SDE(var2);
            }

            this.sdeRef = new SoftReference(var1);
         }

         return var1;
      }
   }

   public List availableStrata() {
      SDE var1 = this.sourceDebugExtensionInfo();
      if (var1.isValid()) {
         return var1.availableStrata();
      } else {
         ArrayList var2 = new ArrayList();
         var2.add("Java");
         return var2;
      }
   }

   public String defaultStratum() {
      SDE var1 = this.sourceDebugExtensionInfo();
      return var1.isValid() ? var1.defaultStratumId : "Java";
   }

   public int modifiers() {
      if (this.modifiers == -1) {
         this.getModifiers();
      }

      return this.modifiers;
   }

   public List allLineLocations() throws AbsentInformationException {
      return this.allLineLocations(this.vm.getDefaultStratum(), (String)null);
   }

   public List allLineLocations(String var1, String var2) throws AbsentInformationException {
      boolean var3 = false;
      SDE.Stratum var4 = this.stratum(var1);
      ArrayList var5 = new ArrayList();
      Iterator var6 = this.methods().iterator();

      while(var6.hasNext()) {
         MethodImpl var7 = (MethodImpl)var6.next();

         try {
            var5.addAll(var7.allLineLocations(var4, var2));
         } catch (AbsentInformationException var9) {
            var3 = true;
         }
      }

      if (var3 && var5.size() == 0) {
         throw new AbsentInformationException();
      } else {
         return var5;
      }
   }

   public List locationsOfLine(int var1) throws AbsentInformationException {
      return this.locationsOfLine(this.vm.getDefaultStratum(), (String)null, var1);
   }

   public List locationsOfLine(String var1, String var2, int var3) throws AbsentInformationException {
      boolean var4 = false;
      boolean var5 = false;
      List var6 = this.methods();
      SDE.Stratum var7 = this.stratum(var1);
      ArrayList var8 = new ArrayList();
      Iterator var9 = var6.iterator();

      while(var9.hasNext()) {
         MethodImpl var10 = (MethodImpl)var9.next();
         if (!var10.isAbstract() && !var10.isNative()) {
            try {
               var8.addAll(var10.locationsOfLine(var7, var2, var3));
               var5 = true;
            } catch (AbsentInformationException var12) {
               var4 = true;
            }
         }
      }

      if (var4 && !var5) {
         throw new AbsentInformationException();
      } else {
         return var8;
      }
   }

   public List instances(long var1) {
      if (!this.vm.canGetInstanceInfo()) {
         throw new UnsupportedOperationException("target does not support getting instances");
      } else if (var1 < 0L) {
         throw new IllegalArgumentException("maxInstances is less than zero: " + var1);
      } else {
         int var3 = var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1;

         try {
            return Arrays.asList((ObjectReference[])JDWP.ReferenceType.Instances.process(this.vm, this, var3).instances);
         } catch (JDWPException var5) {
            throw var5.toJDIException();
         }
      }
   }

   private void getClassFileVersion() {
      if (!this.vm.canGetClassFileVersion()) {
         throw new UnsupportedOperationException();
      } else if (!this.versionNumberGotten) {
         JDWP.ReferenceType.ClassFileVersion var1;
         try {
            var1 = JDWP.ReferenceType.ClassFileVersion.process(this.vm, this);
         } catch (JDWPException var3) {
            if (var3.errorCode() == 101) {
               this.majorVersion = 0;
               this.minorVersion = 0;
               this.versionNumberGotten = true;
               return;
            }

            throw var3.toJDIException();
         }

         this.majorVersion = var1.majorVersion;
         this.minorVersion = var1.minorVersion;
         this.versionNumberGotten = true;
      }
   }

   public int majorVersion() {
      try {
         this.getClassFileVersion();
      } catch (RuntimeException var2) {
         throw var2;
      }

      return this.majorVersion;
   }

   public int minorVersion() {
      try {
         this.getClassFileVersion();
      } catch (RuntimeException var2) {
         throw var2;
      }

      return this.minorVersion;
   }

   private void getConstantPoolInfo() {
      if (!this.vm.canGetConstantPool()) {
         throw new UnsupportedOperationException();
      } else if (!this.constantPoolInfoGotten) {
         JDWP.ReferenceType.ConstantPool var1;
         try {
            var1 = JDWP.ReferenceType.ConstantPool.process(this.vm, this);
         } catch (JDWPException var3) {
            if (var3.errorCode() == 101) {
               this.constanPoolCount = 0;
               this.constantPoolBytesRef = null;
               this.constantPoolInfoGotten = true;
               return;
            }

            throw var3.toJDIException();
         }

         this.constanPoolCount = var1.count;
         byte[] var2 = var1.bytes;
         this.constantPoolBytesRef = new SoftReference(var2);
         this.constantPoolInfoGotten = true;
      }
   }

   public int constantPoolCount() {
      try {
         this.getConstantPoolInfo();
      } catch (RuntimeException var2) {
         throw var2;
      }

      return this.constanPoolCount;
   }

   public byte[] constantPool() {
      try {
         this.getConstantPoolInfo();
      } catch (RuntimeException var2) {
         throw var2;
      }

      if (this.constantPoolBytesRef != null) {
         byte[] var1 = (byte[])this.constantPoolBytesRef.get();
         return (byte[])var1.clone();
      } else {
         return null;
      }
   }

   void getModifiers() {
      if (this.modifiers == -1) {
         try {
            this.modifiers = JDWP.ReferenceType.Modifiers.process(this.vm, this).modBits;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }
   }

   void decodeStatus(int var1) {
      this.status = var1;
      if ((var1 & 2) != 0) {
         this.isPrepared = true;
      }

   }

   void updateStatus() {
      try {
         this.decodeStatus(JDWP.ReferenceType.Status.process(this.vm, this).status);
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   void markPrepared() {
      this.isPrepared = true;
   }

   long ref() {
      return this.ref;
   }

   int indexOf(Method var1) {
      return this.methods().indexOf(var1);
   }

   int indexOf(Field var1) {
      return this.fields().indexOf(var1);
   }

   abstract boolean isAssignableTo(ReferenceType var1);

   boolean isAssignableFrom(ReferenceType var1) {
      return ((ReferenceTypeImpl)var1).isAssignableTo(this);
   }

   boolean isAssignableFrom(ObjectReference var1) {
      return var1 == null || this.isAssignableFrom(var1.referenceType());
   }

   void setStatus(int var1) {
      this.decodeStatus(var1);
   }

   void setSignature(String var1) {
      this.signature = var1;
   }

   void setGenericSignature(String var1) {
      if (var1 != null && var1.length() == 0) {
         this.genericSignature = null;
      } else {
         this.genericSignature = var1;
      }

      this.genericSignatureGotten = true;
   }

   private static boolean isOneDimensionalPrimitiveArray(String var0) {
      int var1 = var0.lastIndexOf(91);
      boolean var2;
      if (var1 >= 0 && !var0.startsWith("[[")) {
         char var3 = var0.charAt(var1 + 1);
         var2 = var3 != 'L';
      } else {
         var2 = false;
      }

      return var2;
   }

   Type findType(String var1) throws ClassNotLoadedException {
      Object var2;
      if (var1.length() == 1) {
         char var3 = var1.charAt(0);
         if (var3 == 'V') {
            var2 = this.vm.theVoidType();
         } else {
            var2 = this.vm.primitiveTypeMirror((byte)var3);
         }
      } else {
         ClassLoaderReferenceImpl var4 = (ClassLoaderReferenceImpl)this.classLoader();
         if (var4 != null && !isOneDimensionalPrimitiveArray(var1)) {
            var2 = var4.findType(var1);
         } else {
            var2 = this.vm.findBootType(var1);
         }
      }

      return (Type)var2;
   }

   String loaderString() {
      return this.classLoader() != null ? "loaded by " + this.classLoader().toString() : "no class loader";
   }
}
