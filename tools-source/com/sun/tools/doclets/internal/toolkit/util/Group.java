package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Group {
   private Map regExpGroupMap = new HashMap();
   private List sortedRegExpList = new ArrayList();
   private List groupList = new ArrayList();
   private Map pkgNameGroupMap = new HashMap();
   private final Configuration configuration;

   public Group(Configuration var1) {
      this.configuration = var1;
   }

   public boolean checkPackageGroups(String var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, ":");
      if (this.groupList.contains(var1)) {
         this.configuration.message.warning("doclet.Groupname_already_used", var1);
         return false;
      } else {
         this.groupList.add(var1);

         while(var3.hasMoreTokens()) {
            String var4 = var3.nextToken();
            if (var4.length() == 0) {
               this.configuration.message.warning("doclet.Error_in_packagelist", var1, var2);
               return false;
            }

            if (var4.endsWith("*")) {
               var4 = var4.substring(0, var4.length() - 1);
               if (this.foundGroupFormat(this.regExpGroupMap, var4)) {
                  return false;
               }

               this.regExpGroupMap.put(var4, var1);
               this.sortedRegExpList.add(var4);
            } else {
               if (this.foundGroupFormat(this.pkgNameGroupMap, var4)) {
                  return false;
               }

               this.pkgNameGroupMap.put(var4, var1);
            }
         }

         Collections.sort(this.sortedRegExpList, new MapKeyComparator());
         return true;
      }
   }

   boolean foundGroupFormat(Map var1, String var2) {
      if (var1.containsKey(var2)) {
         this.configuration.message.error("doclet.Same_package_name_used", var2);
         return true;
      } else {
         return false;
      }
   }

   public Map groupPackages(PackageDoc[] var1) {
      HashMap var2 = new HashMap();
      String var3 = this.pkgNameGroupMap.isEmpty() && this.regExpGroupMap.isEmpty() ? this.configuration.message.getText("doclet.Packages") : this.configuration.message.getText("doclet.Other_Packages");
      if (!this.groupList.contains(var3)) {
         this.groupList.add(var3);
      }

      for(int var4 = 0; var4 < var1.length; ++var4) {
         PackageDoc var5 = var1[var4];
         String var6 = var5.name();
         String var7 = (String)this.pkgNameGroupMap.get(var6);
         if (var7 == null) {
            var7 = this.regExpGroupName(var6);
         }

         if (var7 == null) {
            var7 = var3;
         }

         this.getPkgList(var2, var7).add(var5);
      }

      return var2;
   }

   String regExpGroupName(String var1) {
      for(int var2 = 0; var2 < this.sortedRegExpList.size(); ++var2) {
         String var3 = (String)this.sortedRegExpList.get(var2);
         if (var1.startsWith(var3)) {
            return (String)this.regExpGroupMap.get(var3);
         }
      }

      return null;
   }

   List getPkgList(Map var1, String var2) {
      Object var3 = (List)var1.get(var2);
      if (var3 == null) {
         var3 = new ArrayList();
         var1.put(var2, var3);
      }

      return (List)var3;
   }

   public List getGroupList() {
      return this.groupList;
   }

   private static class MapKeyComparator implements Comparator {
      private MapKeyComparator() {
      }

      public int compare(String var1, String var2) {
         return var2.length() - var1.length();
      }

      // $FF: synthetic method
      MapKeyComparator(Object var1) {
         this();
      }
   }
}
