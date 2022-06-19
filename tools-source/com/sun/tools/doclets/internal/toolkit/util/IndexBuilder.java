package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexBuilder {
   private Map indexmap;
   private boolean noDeprecated;
   private boolean classesOnly;
   private boolean javafx;
   protected final Object[] elements;

   public IndexBuilder(Configuration var1, boolean var2) {
      this(var1, var2, false);
   }

   public IndexBuilder(Configuration var1, boolean var2, boolean var3) {
      this.indexmap = new HashMap();
      if (var3) {
         var1.message.notice("doclet.Building_Index_For_All_Classes");
      } else {
         var1.message.notice("doclet.Building_Index");
      }

      this.noDeprecated = var2;
      this.classesOnly = var3;
      this.javafx = var1.javafx;
      this.buildIndexMap(var1.root);
      Set var4 = this.indexmap.keySet();
      this.elements = var4.toArray();
      Arrays.sort(this.elements);
   }

   protected void sortIndexMap() {
      Iterator var1 = this.indexmap.values().iterator();

      while(var1.hasNext()) {
         Collections.sort((List)var1.next(), new DocComparator());
      }

   }

   protected void buildIndexMap(RootDoc var1) {
      PackageDoc[] var2 = var1.specifiedPackages();
      ClassDoc[] var3 = var1.classes();
      if (!this.classesOnly) {
         if (var2.length == 0) {
            HashSet var4 = new HashSet();

            for(int var6 = 0; var6 < var3.length; ++var6) {
               PackageDoc var5 = var3[var6].containingPackage();
               if (var5 != null && var5.name().length() > 0) {
                  var4.add(var5);
               }
            }

            this.adjustIndexMap((Doc[])var4.toArray(var2));
         } else {
            this.adjustIndexMap(var2);
         }
      }

      this.adjustIndexMap(var3);
      if (!this.classesOnly) {
         for(int var7 = 0; var7 < var3.length; ++var7) {
            if (this.shouldAddToIndexMap(var3[var7])) {
               this.putMembersInIndexMap(var3[var7]);
            }
         }
      }

      this.sortIndexMap();
   }

   protected void putMembersInIndexMap(ClassDoc var1) {
      this.adjustIndexMap(var1.fields());
      this.adjustIndexMap(var1.methods());
      this.adjustIndexMap(var1.constructors());
   }

   protected void adjustIndexMap(Doc[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (this.shouldAddToIndexMap(var1[var2])) {
            String var3 = var1[var2].name();
            char var4 = var3.length() == 0 ? 42 : Character.toUpperCase(var3.charAt(0));
            Character var5 = new Character(var4);
            Object var6 = (List)this.indexmap.get(var5);
            if (var6 == null) {
               var6 = new ArrayList();
               this.indexmap.put(var5, var6);
            }

            ((List)var6).add(var1[var2]);
         }
      }

   }

   protected boolean shouldAddToIndexMap(Doc var1) {
      if (this.javafx && var1.tags("treatAsPrivate").length > 0) {
         return false;
      } else if (var1 instanceof PackageDoc) {
         return !this.noDeprecated || !Util.isDeprecated(var1);
      } else {
         return !this.noDeprecated || !Util.isDeprecated(var1) && !Util.isDeprecated(((ProgramElementDoc)var1).containingPackage());
      }
   }

   public Map getIndexMap() {
      return this.indexmap;
   }

   public List getMemberList(Character var1) {
      return (List)this.indexmap.get(var1);
   }

   public Object[] elements() {
      return this.elements;
   }

   private class DocComparator implements Comparator {
      private DocComparator() {
      }

      public int compare(Doc var1, Doc var2) {
         String var3 = var1.name();
         String var4 = var2.name();
         int var5;
         if ((var5 = var3.compareToIgnoreCase(var4)) != 0) {
            return var5;
         } else if (var1 instanceof ProgramElementDoc && var2 instanceof ProgramElementDoc) {
            var3 = ((ProgramElementDoc)var1).qualifiedName();
            var4 = ((ProgramElementDoc)var2).qualifiedName();
            return var3.compareToIgnoreCase(var4);
         } else {
            return 0;
         }
      }

      // $FF: synthetic method
      DocComparator(Object var2) {
         this();
      }
   }
}
