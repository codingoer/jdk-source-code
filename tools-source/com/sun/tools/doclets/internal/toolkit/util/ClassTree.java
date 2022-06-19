package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ClassTree {
   private List baseclasses = new ArrayList();
   private Map subclasses = new HashMap();
   private List baseinterfaces = new ArrayList();
   private Map subinterfaces = new HashMap();
   private List baseEnums = new ArrayList();
   private Map subEnums = new HashMap();
   private List baseAnnotationTypes = new ArrayList();
   private Map subAnnotationTypes = new HashMap();
   private Map implementingclasses = new HashMap();

   public ClassTree(Configuration var1, boolean var2) {
      var1.message.notice("doclet.Building_Tree");
      this.buildTree(var1.root.classes(), var1);
   }

   public ClassTree(RootDoc var1, Configuration var2) {
      this.buildTree(var1.classes(), var2);
   }

   public ClassTree(ClassDoc[] var1, Configuration var2) {
      this.buildTree(var1, var2);
   }

   private void buildTree(ClassDoc[] var1, Configuration var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if ((!var2.nodeprecated || !Util.isDeprecated(var1[var3]) && !Util.isDeprecated(var1[var3].containingPackage())) && (!var2.javafx || var1[var3].tags("treatAsPrivate").length <= 0)) {
            if (var1[var3].isEnum()) {
               this.processType(var1[var3], var2, this.baseEnums, this.subEnums);
            } else if (var1[var3].isClass()) {
               this.processType(var1[var3], var2, this.baseclasses, this.subclasses);
            } else if (var1[var3].isInterface()) {
               this.processInterface(var1[var3]);
               List var4 = (List)this.implementingclasses.get(var1[var3]);
               if (var4 != null) {
                  Collections.sort(var4);
               }
            } else if (var1[var3].isAnnotationType()) {
               this.processType(var1[var3], var2, this.baseAnnotationTypes, this.subAnnotationTypes);
            }
         }
      }

      Collections.sort(this.baseinterfaces);
      Iterator var5 = this.subinterfaces.values().iterator();

      while(var5.hasNext()) {
         Collections.sort((List)var5.next());
      }

      var5 = this.subclasses.values().iterator();

      while(var5.hasNext()) {
         Collections.sort((List)var5.next());
      }

   }

   private void processType(ClassDoc var1, Configuration var2, List var3, Map var4) {
      ClassDoc var5 = Util.getFirstVisibleSuperClassCD(var1, var2);
      if (var5 != null) {
         if (!this.add(var4, var5, var1)) {
            return;
         }

         this.processType(var5, var2, var3, var4);
      } else if (!var3.contains(var1)) {
         var3.add(var1);
      }

      List var6 = Util.getAllInterfaces(var1, var2);
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         this.add(this.implementingclasses, ((Type)var7.next()).asClassDoc(), var1);
      }

   }

   private void processInterface(ClassDoc var1) {
      ClassDoc[] var2 = var1.interfaces();
      if (var2.length > 0) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (!this.add(this.subinterfaces, var2[var3], var1)) {
               return;
            }

            this.processInterface(var2[var3]);
         }
      } else if (!this.baseinterfaces.contains(var1)) {
         this.baseinterfaces.add(var1);
      }

   }

   private boolean add(Map var1, ClassDoc var2, ClassDoc var3) {
      Object var4 = (List)var1.get(var2);
      if (var4 == null) {
         var4 = new ArrayList();
         var1.put(var2, var4);
      }

      if (((List)var4).contains(var3)) {
         return false;
      } else {
         ((List)var4).add(var3);
         return true;
      }
   }

   private List get(Map var1, ClassDoc var2) {
      List var3 = (List)var1.get(var2);
      return (List)(var3 == null ? new ArrayList() : var3);
   }

   public List subclasses(ClassDoc var1) {
      return this.get(this.subclasses, var1);
   }

   public List subinterfaces(ClassDoc var1) {
      return this.get(this.subinterfaces, var1);
   }

   public List implementingclasses(ClassDoc var1) {
      List var2 = this.get(this.implementingclasses, var1);
      List var3 = this.allSubs(var1, false);
      ListIterator var5 = var3.listIterator();

      while(var5.hasNext()) {
         ListIterator var4 = this.implementingclasses((ClassDoc)var5.next()).listIterator();

         while(var4.hasNext()) {
            ClassDoc var6 = (ClassDoc)var4.next();
            if (!var2.contains(var6)) {
               var2.add(var6);
            }
         }
      }

      Collections.sort(var2);
      return var2;
   }

   public List subs(ClassDoc var1, boolean var2) {
      if (var2) {
         return this.get(this.subEnums, var1);
      } else if (var1.isAnnotationType()) {
         return this.get(this.subAnnotationTypes, var1);
      } else if (var1.isInterface()) {
         return this.get(this.subinterfaces, var1);
      } else {
         return var1.isClass() ? this.get(this.subclasses, var1) : null;
      }
   }

   public List allSubs(ClassDoc var1, boolean var2) {
      List var3 = this.subs(var1, var2);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         var1 = (ClassDoc)var3.get(var4);
         List var5 = this.subs(var1, var2);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            ClassDoc var7 = (ClassDoc)var5.get(var6);
            if (!var3.contains(var7)) {
               var3.add(var7);
            }
         }
      }

      Collections.sort(var3);
      return var3;
   }

   public List baseclasses() {
      return this.baseclasses;
   }

   public List baseinterfaces() {
      return this.baseinterfaces;
   }

   public List baseEnums() {
      return this.baseEnums;
   }

   public List baseAnnotationTypes() {
      return this.baseAnnotationTypes;
   }
}
