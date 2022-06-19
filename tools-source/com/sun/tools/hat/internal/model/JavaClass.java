package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.parser.ReadBuffer;
import com.sun.tools.hat.internal.util.CompositeEnumeration;
import java.util.Enumeration;
import java.util.Vector;

public class JavaClass extends JavaHeapObject {
   private long id;
   private String name;
   private JavaThing superclass;
   private JavaThing loader;
   private JavaThing signers;
   private JavaThing protectionDomain;
   private JavaField[] fields;
   private JavaStatic[] statics;
   private static final JavaClass[] EMPTY_CLASS_ARRAY = new JavaClass[0];
   private JavaClass[] subclasses;
   private Vector instances;
   private Snapshot mySnapshot;
   private int instanceSize;
   private int totalNumFields;

   public JavaClass(long var1, String var3, long var4, long var6, long var8, long var10, JavaField[] var12, JavaStatic[] var13, int var14) {
      this.subclasses = EMPTY_CLASS_ARRAY;
      this.instances = new Vector();
      this.id = var1;
      this.name = var3;
      this.superclass = new JavaObjectRef(var4);
      this.loader = new JavaObjectRef(var6);
      this.signers = new JavaObjectRef(var8);
      this.protectionDomain = new JavaObjectRef(var10);
      this.fields = var12;
      this.statics = var13;
      this.instanceSize = var14;
   }

   public JavaClass(String var1, long var2, long var4, long var6, long var8, JavaField[] var10, JavaStatic[] var11, int var12) {
      this(-1L, var1, var2, var4, var6, var8, var10, var11, var12);
   }

   public final JavaClass getClazz() {
      return this.mySnapshot.getJavaLangClass();
   }

   public final int getIdentifierSize() {
      return this.mySnapshot.getIdentifierSize();
   }

   public final int getMinimumObjectSize() {
      return this.mySnapshot.getMinimumObjectSize();
   }

   public void resolve(Snapshot var1) {
      if (this.mySnapshot == null) {
         this.mySnapshot = var1;
         this.resolveSuperclass(var1);
         if (this.superclass != null) {
            ((JavaClass)this.superclass).addSubclass(this);
         }

         this.loader = this.loader.dereference(var1, (JavaField)null);
         this.signers = this.signers.dereference(var1, (JavaField)null);
         this.protectionDomain = this.protectionDomain.dereference(var1, (JavaField)null);

         for(int var2 = 0; var2 < this.statics.length; ++var2) {
            this.statics[var2].resolve(this, var1);
         }

         var1.getJavaLangClass().addInstance(this);
         super.resolve(var1);
      }
   }

   public void resolveSuperclass(Snapshot var1) {
      if (this.superclass != null) {
         this.totalNumFields = this.fields.length;
         this.superclass = this.superclass.dereference(var1, (JavaField)null);
         if (this.superclass == var1.getNullThing()) {
            this.superclass = null;
         } else {
            try {
               JavaClass var2 = (JavaClass)this.superclass;
               var2.resolveSuperclass(var1);
               this.totalNumFields += var2.totalNumFields;
            } catch (ClassCastException var3) {
               System.out.println("Warning!  Superclass of " + this.name + " is " + this.superclass);
               this.superclass = null;
            }
         }
      }

   }

   public boolean isString() {
      return this.mySnapshot.getJavaLangString() == this;
   }

   public boolean isClassLoader() {
      return this.mySnapshot.getJavaLangClassLoader().isAssignableFrom(this);
   }

   public JavaField getField(int var1) {
      if (var1 >= 0 && var1 < this.fields.length) {
         return this.fields[var1];
      } else {
         throw new Error("No field " + var1 + " for " + this.name);
      }
   }

   public int getNumFieldsForInstance() {
      return this.totalNumFields;
   }

   public JavaField getFieldForInstance(int var1) {
      if (this.superclass != null) {
         JavaClass var2 = (JavaClass)this.superclass;
         if (var1 < var2.totalNumFields) {
            return var2.getFieldForInstance(var1);
         }

         var1 -= var2.totalNumFields;
      }

      return this.getField(var1);
   }

   public JavaClass getClassForField(int var1) {
      if (this.superclass != null) {
         JavaClass var2 = (JavaClass)this.superclass;
         if (var1 < var2.totalNumFields) {
            return var2.getClassForField(var1);
         }
      }

      return this;
   }

   public long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public boolean isArray() {
      return this.name.indexOf(91) != -1;
   }

   public Enumeration getInstances(boolean var1) {
      if (!var1) {
         return this.instances.elements();
      } else {
         Object var2 = this.instances.elements();

         for(int var3 = 0; var3 < this.subclasses.length; ++var3) {
            var2 = new CompositeEnumeration((Enumeration)var2, this.subclasses[var3].getInstances(true));
         }

         return (Enumeration)var2;
      }
   }

   public int getInstancesCount(boolean var1) {
      int var2 = this.instances.size();
      if (var1) {
         for(int var3 = 0; var3 < this.subclasses.length; ++var3) {
            var2 += this.subclasses[var3].getInstancesCount(var1);
         }
      }

      return var2;
   }

   public JavaClass[] getSubclasses() {
      return this.subclasses;
   }

   public JavaClass getSuperclass() {
      return (JavaClass)this.superclass;
   }

   public JavaThing getLoader() {
      return this.loader;
   }

   public boolean isBootstrap() {
      return this.loader == this.mySnapshot.getNullThing();
   }

   public JavaThing getSigners() {
      return this.signers;
   }

   public JavaThing getProtectionDomain() {
      return this.protectionDomain;
   }

   public JavaField[] getFields() {
      return this.fields;
   }

   public JavaField[] getFieldsForInstance() {
      Vector var1 = new Vector();
      this.addFields(var1);
      JavaField[] var2 = new JavaField[var1.size()];

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2[var3] = (JavaField)var1.elementAt(var3);
      }

      return var2;
   }

   public JavaStatic[] getStatics() {
      return this.statics;
   }

   public JavaThing getStaticField(String var1) {
      for(int var2 = 0; var2 < this.statics.length; ++var2) {
         JavaStatic var3 = this.statics[var2];
         if (var3.getField().getName().equals(var1)) {
            return var3.getValue();
         }
      }

      return null;
   }

   public String toString() {
      return "class " + this.name;
   }

   public int compareTo(JavaThing var1) {
      return var1 instanceof JavaClass ? this.name.compareTo(((JavaClass)var1).name) : super.compareTo(var1);
   }

   public boolean isAssignableFrom(JavaClass var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 == null ? false : this.isAssignableFrom((JavaClass)var1.superclass);
      }
   }

   public String describeReferenceTo(JavaThing var1, Snapshot var2) {
      for(int var3 = 0; var3 < this.statics.length; ++var3) {
         JavaField var4 = this.statics[var3].getField();
         if (var4.hasId()) {
            JavaThing var5 = this.statics[var3].getValue();
            if (var5 == var1) {
               return "static field " + var4.getName();
            }
         }
      }

      return super.describeReferenceTo(var1, var2);
   }

   public int getInstanceSize() {
      return this.instanceSize + this.mySnapshot.getMinimumObjectSize();
   }

   public long getTotalInstanceSize() {
      int var1 = this.instances.size();
      if (var1 != 0 && this.isArray()) {
         long var2 = 0L;

         for(int var4 = 0; var4 < var1; ++var4) {
            JavaThing var5 = (JavaThing)this.instances.elementAt(var4);
            var2 += (long)var5.getSize();
         }

         return var2;
      } else {
         return (long)(var1 * this.instanceSize);
      }
   }

   public int getSize() {
      JavaClass var1 = this.mySnapshot.getJavaLangClass();
      return var1 == null ? 0 : var1.getInstanceSize();
   }

   public void visitReferencedObjects(JavaHeapObjectVisitor var1) {
      super.visitReferencedObjects(var1);
      JavaClass var2 = this.getSuperclass();
      if (var2 != null) {
         var1.visit(this.getSuperclass());
      }

      JavaThing var3 = this.getLoader();
      if (var3 instanceof JavaHeapObject) {
         var1.visit((JavaHeapObject)var3);
      }

      var3 = this.getSigners();
      if (var3 instanceof JavaHeapObject) {
         var1.visit((JavaHeapObject)var3);
      }

      var3 = this.getProtectionDomain();
      if (var3 instanceof JavaHeapObject) {
         var1.visit((JavaHeapObject)var3);
      }

      for(int var4 = 0; var4 < this.statics.length; ++var4) {
         JavaField var5 = this.statics[var4].getField();
         if (!var1.exclude(this, var5) && var5.hasId()) {
            var3 = this.statics[var4].getValue();
            if (var3 instanceof JavaHeapObject) {
               var1.visit((JavaHeapObject)var3);
            }
         }
      }

   }

   final ReadBuffer getReadBuffer() {
      return this.mySnapshot.getReadBuffer();
   }

   final void setNew(JavaHeapObject var1, boolean var2) {
      this.mySnapshot.setNew(var1, var2);
   }

   final boolean isNew(JavaHeapObject var1) {
      return this.mySnapshot.isNew(var1);
   }

   final StackTrace getSiteTrace(JavaHeapObject var1) {
      return this.mySnapshot.getSiteTrace(var1);
   }

   final void addReferenceFromRoot(Root var1, JavaHeapObject var2) {
      this.mySnapshot.addReferenceFromRoot(var1, var2);
   }

   final Root getRoot(JavaHeapObject var1) {
      return this.mySnapshot.getRoot(var1);
   }

   final Snapshot getSnapshot() {
      return this.mySnapshot;
   }

   void addInstance(JavaHeapObject var1) {
      this.instances.addElement(var1);
   }

   private void addFields(Vector var1) {
      if (this.superclass != null) {
         ((JavaClass)this.superclass).addFields(var1);
      }

      for(int var2 = 0; var2 < this.fields.length; ++var2) {
         var1.addElement(this.fields[var2]);
      }

   }

   private void addSubclassInstances(Vector var1) {
      int var2;
      for(var2 = 0; var2 < this.subclasses.length; ++var2) {
         this.subclasses[var2].addSubclassInstances(var1);
      }

      for(var2 = 0; var2 < this.instances.size(); ++var2) {
         var1.addElement(this.instances.elementAt(var2));
      }

   }

   private void addSubclass(JavaClass var1) {
      JavaClass[] var2 = new JavaClass[this.subclasses.length + 1];
      System.arraycopy(this.subclasses, 0, var2, 0, this.subclasses.length);
      var2[this.subclasses.length] = var1;
      this.subclasses = var2;
   }
}
