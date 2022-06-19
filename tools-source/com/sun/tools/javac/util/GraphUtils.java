package com.sun.tools.javac.util;

import java.util.Iterator;

public class GraphUtils {
   public static List tarjan(Iterable var0) {
      Tarjan var1 = new Tarjan();
      return var1.findSCC(var0);
   }

   public static String toDot(Iterable var0, String var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append(String.format("digraph %s {\n", var1));
      var3.append(String.format("label = \"%s\";\n", var2));
      Iterator var4 = var0.iterator();

      TarjanNode var5;
      while(var4.hasNext()) {
         var5 = (TarjanNode)var4.next();
         var3.append(String.format("%s [label = \"%s\"];\n", var5.hashCode(), var5.toString()));
      }

      var4 = var0.iterator();

      while(var4.hasNext()) {
         var5 = (TarjanNode)var4.next();
         DependencyKind[] var6 = var5.getSupportedDependencyKinds();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            DependencyKind var9 = var6[var8];
            Iterator var10 = var5.getDependenciesByKind(var9).iterator();

            while(var10.hasNext()) {
               TarjanNode var11 = (TarjanNode)var10.next();
               var3.append(String.format("%s -> %s [label = \" %s \" style = %s ];\n", var5.hashCode(), var11.hashCode(), var5.getDependencyName(var11, var9), var9.getDotStyle()));
            }
         }
      }

      var3.append("}\n");
      return var3.toString();
   }

   private static class Tarjan {
      int index;
      ListBuffer sccs;
      ListBuffer stack;

      private Tarjan() {
         this.index = 0;
         this.sccs = new ListBuffer();
         this.stack = new ListBuffer();
      }

      private List findSCC(Iterable var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            TarjanNode var3 = (TarjanNode)var2.next();
            if (var3.index == -1) {
               this.findSCC(var3);
            }
         }

         return this.sccs.toList();
      }

      private void findSCC(TarjanNode var1) {
         this.visitNode(var1);
         Iterator var2 = var1.getAllDependencies().iterator();

         while(var2.hasNext()) {
            TarjanNode var3 = (TarjanNode)var2.next();
            if (var3.index == -1) {
               this.findSCC(var3);
               var1.lowlink = Math.min(var1.lowlink, var3.lowlink);
            } else if (this.stack.contains(var3)) {
               var1.lowlink = Math.min(var1.lowlink, var3.index);
            }
         }

         if (var1.lowlink == var1.index) {
            this.addSCC(var1);
         }

      }

      private void visitNode(TarjanNode var1) {
         var1.index = this.index;
         var1.lowlink = this.index++;
         this.stack.prepend(var1);
         var1.active = true;
      }

      private void addSCC(TarjanNode var1) {
         ListBuffer var3 = new ListBuffer();

         TarjanNode var2;
         do {
            var2 = (TarjanNode)this.stack.remove();
            var2.active = false;
            var3.add(var2);
         } while(var2 != var1);

         this.sccs.add(var3.toList());
      }

      // $FF: synthetic method
      Tarjan(Object var1) {
         this();
      }
   }

   public abstract static class TarjanNode extends Node implements Comparable {
      int index = -1;
      int lowlink;
      boolean active;

      public TarjanNode(Object var1) {
         super(var1);
      }

      public abstract Iterable getAllDependencies();

      public abstract Iterable getDependenciesByKind(DependencyKind var1);

      public int compareTo(TarjanNode var1) {
         return this.index < var1.index ? -1 : (this.index == var1.index ? 0 : 1);
      }
   }

   public abstract static class Node {
      public final Object data;

      public Node(Object var1) {
         this.data = var1;
      }

      public abstract DependencyKind[] getSupportedDependencyKinds();

      public abstract Iterable getAllDependencies();

      public abstract String getDependencyName(Node var1, DependencyKind var2);

      public String toString() {
         return this.data.toString();
      }
   }

   public interface DependencyKind {
      String getDotStyle();
   }
}
