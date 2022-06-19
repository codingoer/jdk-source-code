package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.parser.ReadBuffer;
import com.sun.tools.hat.internal.util.Misc;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class Snapshot {
   public static long SMALL_ID_MASK = 4294967295L;
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   private static final JavaField[] EMPTY_FIELD_ARRAY = new JavaField[0];
   private static final JavaStatic[] EMPTY_STATIC_ARRAY = new JavaStatic[0];
   private Hashtable heapObjects = new Hashtable();
   private Hashtable fakeClasses = new Hashtable();
   private Vector roots = new Vector();
   private Map classes = new TreeMap();
   private volatile Map newObjects;
   private volatile Map siteTraces;
   private Map rootsMap = new HashMap();
   private SoftReference finalizablesCache;
   private JavaThing nullThing = new HackJavaValue("<null>", 0);
   private JavaClass weakReferenceClass;
   private int referentFieldIndex;
   private JavaClass javaLangClass;
   private JavaClass javaLangString;
   private JavaClass javaLangClassLoader;
   private volatile JavaClass otherArrayType;
   private ReachableExcludes reachableExcludes;
   private ReadBuffer readBuf;
   private boolean hasNewSet;
   private boolean unresolvedObjectsOK;
   private boolean newStyleArrayClass;
   private int identifierSize = 4;
   private int minimumObjectSize;
   private static final int DOT_LIMIT = 5000;

   public Snapshot(ReadBuffer var1) {
      this.readBuf = var1;
   }

   public void setSiteTrace(JavaHeapObject var1, StackTrace var2) {
      if (var2 != null && var2.getFrames().length != 0) {
         this.initSiteTraces();
         this.siteTraces.put(var1, var2);
      }

   }

   public StackTrace getSiteTrace(JavaHeapObject var1) {
      return this.siteTraces != null ? (StackTrace)this.siteTraces.get(var1) : null;
   }

   public void setNewStyleArrayClass(boolean var1) {
      this.newStyleArrayClass = var1;
   }

   public boolean isNewStyleArrayClass() {
      return this.newStyleArrayClass;
   }

   public void setIdentifierSize(int var1) {
      this.identifierSize = var1;
      this.minimumObjectSize = 2 * var1;
   }

   public int getIdentifierSize() {
      return this.identifierSize;
   }

   public int getMinimumObjectSize() {
      return this.minimumObjectSize;
   }

   public void addHeapObject(long var1, JavaHeapObject var3) {
      this.heapObjects.put(this.makeId(var1), var3);
   }

   public void addRoot(Root var1) {
      var1.setIndex(this.roots.size());
      this.roots.addElement(var1);
   }

   public void addClass(long var1, JavaClass var3) {
      this.addHeapObject(var1, var3);
      this.putInClassesMap(var3);
   }

   JavaClass addFakeInstanceClass(long var1, int var3) {
      String var4 = "unknown-class<@" + Misc.toHex(var1) + ">";
      int var5 = var3 / 4;
      int var6 = var3 % 4;
      JavaField[] var7 = new JavaField[var5 + var6];

      int var8;
      for(var8 = 0; var8 < var5; ++var8) {
         var7[var8] = new JavaField("unknown-field-" + var8, "I");
      }

      for(var8 = 0; var8 < var6; ++var8) {
         var7[var8 + var5] = new JavaField("unknown-field-" + var8 + var5, "B");
      }

      JavaClass var9 = new JavaClass(var4, 0L, 0L, 0L, 0L, var7, EMPTY_STATIC_ARRAY, var3);
      this.addFakeClass(this.makeId(var1), var9);
      return var9;
   }

   public boolean getHasNewSet() {
      return this.hasNewSet;
   }

   public void resolve(boolean var1) {
      System.out.println("Resolving " + this.heapObjects.size() + " objects...");
      this.javaLangClass = this.findClass("java.lang.Class");
      if (this.javaLangClass == null) {
         System.out.println("WARNING:  hprof file does not include java.lang.Class!");
         this.javaLangClass = new JavaClass("java.lang.Class", 0L, 0L, 0L, 0L, EMPTY_FIELD_ARRAY, EMPTY_STATIC_ARRAY, 0);
         this.addFakeClass(this.javaLangClass);
      }

      this.javaLangString = this.findClass("java.lang.String");
      if (this.javaLangString == null) {
         System.out.println("WARNING:  hprof file does not include java.lang.String!");
         this.javaLangString = new JavaClass("java.lang.String", 0L, 0L, 0L, 0L, EMPTY_FIELD_ARRAY, EMPTY_STATIC_ARRAY, 0);
         this.addFakeClass(this.javaLangString);
      }

      this.javaLangClassLoader = this.findClass("java.lang.ClassLoader");
      if (this.javaLangClassLoader == null) {
         System.out.println("WARNING:  hprof file does not include java.lang.ClassLoader!");
         this.javaLangClassLoader = new JavaClass("java.lang.ClassLoader", 0L, 0L, 0L, 0L, EMPTY_FIELD_ARRAY, EMPTY_STATIC_ARRAY, 0);
         this.addFakeClass(this.javaLangClassLoader);
      }

      Iterator var2 = this.heapObjects.values().iterator();

      JavaHeapObject var3;
      while(var2.hasNext()) {
         var3 = (JavaHeapObject)var2.next();
         if (var3 instanceof JavaClass) {
            var3.resolve(this);
         }
      }

      var2 = this.heapObjects.values().iterator();

      while(var2.hasNext()) {
         var3 = (JavaHeapObject)var2.next();
         if (!(var3 instanceof JavaClass)) {
            var3.resolve(this);
         }
      }

      this.heapObjects.putAll(this.fakeClasses);
      this.fakeClasses.clear();
      this.weakReferenceClass = this.findClass("java.lang.ref.Reference");
      if (this.weakReferenceClass == null) {
         this.weakReferenceClass = this.findClass("sun.misc.Ref");
         this.referentFieldIndex = 0;
      } else {
         JavaField[] var5 = this.weakReferenceClass.getFieldsForInstance();

         for(int var7 = 0; var7 < var5.length; ++var7) {
            if ("referent".equals(var5[var7].getName())) {
               this.referentFieldIndex = var7;
               break;
            }
         }
      }

      if (var1) {
         this.calculateReferencesToObjects();
         System.out.print("Eliminating duplicate references");
         System.out.flush();
      }

      int var6 = 0;
      Iterator var8 = this.heapObjects.values().iterator();

      while(var8.hasNext()) {
         JavaHeapObject var4 = (JavaHeapObject)var8.next();
         var4.setupReferers();
         ++var6;
         if (var1 && var6 % 5000 == 0) {
            System.out.print(".");
            System.out.flush();
         }
      }

      if (var1) {
         System.out.println("");
      }

      this.classes = Collections.unmodifiableMap(this.classes);
   }

   private void calculateReferencesToObjects() {
      System.out.print("Chasing references, expect " + this.heapObjects.size() / 5000 + " dots");
      System.out.flush();
      int var1 = 0;
      MyVisitor var2 = new MyVisitor();
      Iterator var3 = this.heapObjects.values().iterator();

      while(var3.hasNext()) {
         JavaHeapObject var4 = (JavaHeapObject)var3.next();
         var2.t = var4;
         var4.visitReferencedObjects(var2);
         ++var1;
         if (var1 % 5000 == 0) {
            System.out.print(".");
            System.out.flush();
         }
      }

      System.out.println();
      var3 = this.roots.iterator();

      while(var3.hasNext()) {
         Root var6 = (Root)var3.next();
         var6.resolve(this);
         JavaHeapObject var5 = this.findThing(var6.getId());
         if (var5 != null) {
            var5.addReferenceFromRoot(var6);
         }
      }

   }

   public void markNewRelativeTo(Snapshot var1) {
      this.hasNewSet = true;

      JavaHeapObject var3;
      boolean var4;
      for(Iterator var2 = this.heapObjects.values().iterator(); var2.hasNext(); var3.setNew(var4)) {
         var3 = (JavaHeapObject)var2.next();
         long var5 = var3.getId();
         if (var5 != 0L && var5 != -1L) {
            JavaHeapObject var7 = var1.findThing(var3.getId());
            if (var7 == null) {
               var4 = true;
            } else {
               var4 = !var3.isSameTypeAs(var7);
            }
         } else {
            var4 = false;
         }
      }

   }

   public Enumeration getThings() {
      return this.heapObjects.elements();
   }

   public JavaHeapObject findThing(long var1) {
      Number var3 = this.makeId(var1);
      JavaHeapObject var4 = (JavaHeapObject)this.heapObjects.get(var3);
      return var4 != null ? var4 : (JavaHeapObject)this.fakeClasses.get(var3);
   }

   public JavaHeapObject findThing(String var1) {
      return this.findThing(Misc.parseHex(var1));
   }

   public JavaClass findClass(String var1) {
      return var1.startsWith("0x") ? (JavaClass)this.findThing(var1) : (JavaClass)this.classes.get(var1);
   }

   public Iterator getClasses() {
      return this.classes.values().iterator();
   }

   public JavaClass[] getClassesArray() {
      JavaClass[] var1 = new JavaClass[this.classes.size()];
      this.classes.values().toArray(var1);
      return var1;
   }

   public synchronized Enumeration getFinalizerObjects() {
      Vector var1;
      if (this.finalizablesCache != null && (var1 = (Vector)this.finalizablesCache.get()) != null) {
         return var1.elements();
      } else {
         JavaClass var2 = this.findClass("java.lang.ref.Finalizer");
         JavaObject var3 = (JavaObject)var2.getStaticField("queue");
         JavaThing var4 = var3.getField("head");
         Vector var5 = new Vector();
         if (var4 != this.getNullThing()) {
            JavaObject var6 = (JavaObject)var4;

            while(true) {
               JavaHeapObject var7 = (JavaHeapObject)var6.getField("referent");
               JavaThing var8 = var6.getField("next");
               if (var8 == this.getNullThing() || var8.equals(var6)) {
                  break;
               }

               var6 = (JavaObject)var8;
               var5.add(var7);
            }
         }

         this.finalizablesCache = new SoftReference(var5);
         return var5.elements();
      }
   }

   public Enumeration getRoots() {
      return this.roots.elements();
   }

   public Root[] getRootsArray() {
      Root[] var1 = new Root[this.roots.size()];
      this.roots.toArray(var1);
      return var1;
   }

   public Root getRootAt(int var1) {
      return (Root)this.roots.elementAt(var1);
   }

   public ReferenceChain[] rootsetReferencesTo(JavaHeapObject var1, boolean var2) {
      Vector var3 = new Vector();
      Hashtable var4 = new Hashtable();
      Vector var5 = new Vector();
      var4.put(var1, var1);
      var3.addElement(new ReferenceChain(var1, (ReferenceChain)null));

      label50:
      while(var3.size() > 0) {
         ReferenceChain var6 = (ReferenceChain)var3.elementAt(0);
         var3.removeElementAt(0);
         JavaHeapObject var7 = var6.getObj();
         if (var7.getRoot() != null) {
            var5.addElement(var6);
         }

         Enumeration var8 = var7.getReferers();

         while(true) {
            JavaHeapObject var9;
            do {
               do {
                  do {
                     if (!var8.hasMoreElements()) {
                        continue label50;
                     }

                     var9 = (JavaHeapObject)var8.nextElement();
                  } while(var9 == null);
               } while(var4.containsKey(var9));
            } while(!var2 && var9.refersOnlyWeaklyTo(this, var7));

            var4.put(var9, var9);
            var3.addElement(new ReferenceChain(var9, var6));
         }
      }

      ReferenceChain[] var10 = new ReferenceChain[var5.size()];

      for(int var11 = 0; var11 < var5.size(); ++var11) {
         var10[var11] = (ReferenceChain)var5.elementAt(var11);
      }

      return var10;
   }

   public boolean getUnresolvedObjectsOK() {
      return this.unresolvedObjectsOK;
   }

   public void setUnresolvedObjectsOK(boolean var1) {
      this.unresolvedObjectsOK = var1;
   }

   public JavaClass getWeakReferenceClass() {
      return this.weakReferenceClass;
   }

   public int getReferentFieldIndex() {
      return this.referentFieldIndex;
   }

   public JavaThing getNullThing() {
      return this.nullThing;
   }

   public void setReachableExcludes(ReachableExcludes var1) {
      this.reachableExcludes = var1;
   }

   public ReachableExcludes getReachableExcludes() {
      return this.reachableExcludes;
   }

   void addReferenceFromRoot(Root var1, JavaHeapObject var2) {
      Root var3 = (Root)this.rootsMap.get(var2);
      if (var3 == null) {
         this.rootsMap.put(var2, var1);
      } else {
         this.rootsMap.put(var2, var3.mostInteresting(var1));
      }

   }

   Root getRoot(JavaHeapObject var1) {
      return (Root)this.rootsMap.get(var1);
   }

   JavaClass getJavaLangClass() {
      return this.javaLangClass;
   }

   JavaClass getJavaLangString() {
      return this.javaLangString;
   }

   JavaClass getJavaLangClassLoader() {
      return this.javaLangClassLoader;
   }

   JavaClass getOtherArrayType() {
      if (this.otherArrayType == null) {
         synchronized(this) {
            if (this.otherArrayType == null) {
               this.addFakeClass(new JavaClass("[<other>", 0L, 0L, 0L, 0L, EMPTY_FIELD_ARRAY, EMPTY_STATIC_ARRAY, 0));
               this.otherArrayType = this.findClass("[<other>");
            }
         }
      }

      return this.otherArrayType;
   }

   JavaClass getArrayClass(String var1) {
      synchronized(this.classes) {
         JavaClass var2 = this.findClass("[" + var1);
         if (var2 == null) {
            var2 = new JavaClass("[" + var1, 0L, 0L, 0L, 0L, EMPTY_FIELD_ARRAY, EMPTY_STATIC_ARRAY, 0);
            this.addFakeClass(var2);
         }

         return var2;
      }
   }

   ReadBuffer getReadBuffer() {
      return this.readBuf;
   }

   void setNew(JavaHeapObject var1, boolean var2) {
      this.initNewObjects();
      if (var2) {
         this.newObjects.put(var1, Boolean.TRUE);
      }

   }

   boolean isNew(JavaHeapObject var1) {
      if (this.newObjects != null) {
         return this.newObjects.get(var1) != null;
      } else {
         return false;
      }
   }

   private Number makeId(long var1) {
      return (Number)(this.identifierSize == 4 ? new Integer((int)var1) : new Long(var1));
   }

   private void putInClassesMap(JavaClass var1) {
      String var2 = var1.getName();
      if (this.classes.containsKey(var2)) {
         (new StringBuilder()).append(var2).append("-").append(var1.getIdString()).toString();
      }

      this.classes.put(var1.getName(), var1);
   }

   private void addFakeClass(JavaClass var1) {
      this.putInClassesMap(var1);
      var1.resolve(this);
   }

   private void addFakeClass(Number var1, JavaClass var2) {
      this.fakeClasses.put(var1, var2);
      this.addFakeClass(var2);
   }

   private synchronized void initNewObjects() {
      if (this.newObjects == null) {
         synchronized(this) {
            if (this.newObjects == null) {
               this.newObjects = new HashMap();
            }
         }
      }

   }

   private synchronized void initSiteTraces() {
      if (this.siteTraces == null) {
         synchronized(this) {
            if (this.siteTraces == null) {
               this.siteTraces = new HashMap();
            }
         }
      }

   }

   private static class MyVisitor extends AbstractJavaHeapObjectVisitor {
      JavaHeapObject t;

      private MyVisitor() {
      }

      public void visit(JavaHeapObject var1) {
         var1.addReferenceFrom(this.t);
      }

      // $FF: synthetic method
      MyVisitor(Object var1) {
         this();
      }
   }
}
