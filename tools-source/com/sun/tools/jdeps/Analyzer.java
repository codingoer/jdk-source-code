package com.sun.tools.jdeps;

import com.sun.tools.classfile.Dependency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Analyzer {
   private final Type type;
   private final Filter filter;
   private final Map results = new HashMap();
   private final Map map = new HashMap();
   private final Archive NOT_FOUND = new Archive(JdepsTask.getMessage("artifact.not.found"));

   public Analyzer(Type var1, Filter var2) {
      this.type = var1;
      this.filter = var2;
   }

   public void run(List var1) {
      this.buildLocationArchiveMap(var1);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Archive var3 = (Archive)var2.next();
         ArchiveDeps var4 = new ArchiveDeps(var3, this.type);
         var3.visitDependences(var4);
         this.results.put(var3, var4);
      }

   }

   private void buildLocationArchiveMap(List var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Archive var3 = (Archive)var2.next();
         Iterator var4 = var3.getClasses().iterator();

         while(var4.hasNext()) {
            Dependency.Location var5 = (Dependency.Location)var4.next();
            if (!this.map.containsKey(var5)) {
               this.map.put(var5, var3);
            }
         }
      }

   }

   public boolean hasDependences(Archive var1) {
      if (this.results.containsKey(var1)) {
         return ((ArchiveDeps)this.results.get(var1)).dependencies().size() > 0;
      } else {
         return false;
      }
   }

   public Set dependences(Archive var1) {
      ArchiveDeps var2 = (ArchiveDeps)this.results.get(var1);
      return var2.targetDependences();
   }

   public void visitDependences(Archive var1, Visitor var2, Type var3) {
      ArchiveDeps var4;
      Iterator var6;
      if (var3 == Analyzer.Type.SUMMARY) {
         var4 = (ArchiveDeps)this.results.get(var1);
         TreeMap var5 = new TreeMap();
         var6 = var4.requires().iterator();

         Archive var7;
         while(var6.hasNext()) {
            var7 = (Archive)var6.next();
            var5.put(var7.getName(), var7);
         }

         var6 = var5.values().iterator();

         while(var6.hasNext()) {
            var7 = (Archive)var6.next();
            Profile var8 = var4.getTargetProfile(var7);
            var2.visitDependence(var1.getName(), var1, var8 != null ? var8.profileName() : var7.getName(), var7);
         }
      } else {
         var4 = (ArchiveDeps)this.results.get(var1);
         if (var3 != this.type) {
            var4 = new ArchiveDeps(var1, var3);
            var1.visitDependences(var4);
         }

         TreeSet var9 = new TreeSet(var4.dependencies());
         var6 = var9.iterator();

         while(var6.hasNext()) {
            Dep var10 = (Dep)var6.next();
            var2.visitDependence(var10.origin(), var10.originArchive(), var10.target(), var10.targetArchive());
         }
      }

   }

   public void visitDependences(Archive var1, Visitor var2) {
      this.visitDependences(var1, var2, this.type);
   }

   class Dep implements Comparable {
      final String origin;
      final Archive originArchive;
      final String target;
      final Archive targetArchive;

      Dep(String var2, Archive var3, String var4, Archive var5) {
         this.origin = var2;
         this.originArchive = var3;
         this.target = var4;
         this.targetArchive = var5;
      }

      String origin() {
         return this.origin;
      }

      Archive originArchive() {
         return this.originArchive;
      }

      String target() {
         return this.target;
      }

      Archive targetArchive() {
         return this.targetArchive;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Dep)) {
            return false;
         } else {
            Dep var2 = (Dep)var1;
            return this.origin.equals(var2.origin) && this.originArchive == var2.originArchive && this.target.equals(var2.target) && this.targetArchive == var2.targetArchive;
         }
      }

      public int hashCode() {
         int var1 = 7;
         var1 = 67 * var1 + Objects.hashCode(this.origin) + Objects.hashCode(this.originArchive) + Objects.hashCode(this.target) + Objects.hashCode(this.targetArchive);
         return var1;
      }

      public int compareTo(Dep var1) {
         if (this.origin.equals(var1.origin)) {
            if (this.target.equals(var1.target)) {
               if (this.originArchive == var1.originArchive && this.targetArchive == var1.targetArchive) {
                  return 0;
               } else {
                  return this.originArchive == var1.originArchive ? this.targetArchive.getPathName().compareTo(var1.targetArchive.getPathName()) : this.originArchive.getPathName().compareTo(var1.originArchive.getPathName());
               }
            } else {
               return this.target.compareTo(var1.target);
            }
         } else {
            return this.origin.compareTo(var1.origin);
         }
      }
   }

   class ArchiveDeps implements Archive.Visitor {
      protected final Archive archive;
      protected final Set requires;
      protected final Set deps;
      protected final Type level;
      private Profile profile;
      private Dep curDep;

      ArchiveDeps(Archive var2, Type var3) {
         this.archive = var2;
         this.deps = new HashSet();
         this.requires = new HashSet();
         this.level = var3;
      }

      Set dependencies() {
         return this.deps;
      }

      Set targetDependences() {
         HashSet var1 = new HashSet();
         Iterator var2 = this.deps.iterator();

         while(var2.hasNext()) {
            Dep var3 = (Dep)var2.next();
            var1.add(var3.target());
         }

         return var1;
      }

      Set requires() {
         return this.requires;
      }

      Profile getTargetProfile(Archive var1) {
         return PlatformClassPath.JDKArchive.isProfileArchive(var1) ? this.profile : null;
      }

      Archive findArchive(Dependency.Location var1) {
         Archive var2 = this.archive.getClasses().contains(var1) ? this.archive : (Archive)Analyzer.this.map.get(var1);
         if (var2 == null) {
            Analyzer.this.map.put(var1, var2 = Analyzer.this.NOT_FOUND);
         }

         return var2;
      }

      private String getLocationName(Dependency.Location var1) {
         if (this.level != Analyzer.Type.CLASS && this.level != Analyzer.Type.VERBOSE) {
            String var2 = var1.getPackageName();
            return var2.isEmpty() ? "<unnamed>" : var2;
         } else {
            return var1.getClassName();
         }
      }

      public void visit(Dependency.Location var1, Dependency.Location var2) {
         Archive var3 = this.findArchive(var2);
         if (Analyzer.this.filter.accepts(var1, this.archive, var2, var3)) {
            this.addDep(var1, var2);
            if (this.archive != var3 && !this.requires.contains(var3)) {
               this.requires.add(var3);
            }
         }

         if (var3 instanceof PlatformClassPath.JDKArchive) {
            Profile var4 = Profile.getProfile(var2.getPackageName());
            if (this.profile == null || var4 != null && var4.compareTo(this.profile) > 0) {
               this.profile = var4;
            }
         }

      }

      protected Dep addDep(Dependency.Location var1, Dependency.Location var2) {
         String var3 = this.getLocationName(var1);
         String var4 = this.getLocationName(var2);
         Archive var5 = this.findArchive(var2);
         if (this.curDep != null && this.curDep.origin().equals(var3) && this.curDep.originArchive() == this.archive && this.curDep.target().equals(var4) && this.curDep.targetArchive() == var5) {
            return this.curDep;
         } else {
            Dep var6 = Analyzer.this.new Dep(var3, this.archive, var4, var5);
            if (this.deps.contains(var6)) {
               Iterator var7 = this.deps.iterator();

               while(var7.hasNext()) {
                  Dep var8 = (Dep)var7.next();
                  if (var6.equals(var8)) {
                     this.curDep = var8;
                  }
               }
            } else {
               this.deps.add(var6);
               this.curDep = var6;
            }

            return this.curDep;
         }
      }
   }

   public interface Visitor {
      void visitDependence(String var1, Archive var2, String var3, Archive var4);
   }

   interface Filter {
      boolean accepts(Dependency.Location var1, Archive var2, Dependency.Location var3, Archive var4);
   }

   public static enum Type {
      SUMMARY,
      PACKAGE,
      CLASS,
      VERBOSE;
   }
}
