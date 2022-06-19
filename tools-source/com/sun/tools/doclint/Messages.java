package com.sun.tools.doclint;

import com.sun.source.doctree.DocTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.util.StringUtils;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

public class Messages {
   private final Options options;
   private final Stats stats;
   ResourceBundle bundle;
   Env env;

   Messages(Env var1) {
      this.env = var1;
      String var2 = this.getClass().getPackage().getName() + ".resources.doclint";
      this.bundle = ResourceBundle.getBundle(var2, Locale.ENGLISH);
      this.stats = new Stats(this.bundle);
      this.options = new Options(this.stats);
   }

   void error(Group var1, DocTree var2, String var3, Object... var4) {
      this.report(var1, Kind.ERROR, var2, var3, var4);
   }

   void warning(Group var1, DocTree var2, String var3, Object... var4) {
      this.report(var1, Kind.WARNING, var2, var3, var4);
   }

   void setOptions(String var1) {
      this.options.setOptions(var1);
   }

   void setStatsEnabled(boolean var1) {
      this.stats.setEnabled(var1);
   }

   void reportStats(PrintWriter var1) {
      this.stats.report(var1);
   }

   protected void report(Group var1, Diagnostic.Kind var2, DocTree var3, String var4, Object... var5) {
      if (this.options.isEnabled(var1, this.env.currAccess)) {
         String var6 = var4 == null ? (String)var5[0] : this.localize(var4, var5);
         this.env.trees.printMessage(var2, var6, var3, this.env.currDocComment, this.env.currPath.getCompilationUnit());
         this.stats.record(var1, var2, var4);
      }

   }

   protected void report(Group var1, Diagnostic.Kind var2, Tree var3, String var4, Object... var5) {
      if (this.options.isEnabled(var1, this.env.currAccess)) {
         String var6 = this.localize(var4, var5);
         this.env.trees.printMessage(var2, var6, var3, this.env.currPath.getCompilationUnit());
         this.stats.record(var1, var2, var4);
      }

   }

   String localize(String var1, Object... var2) {
      String var3 = this.bundle.getString(var1);
      if (var3 == null) {
         StringBuilder var4 = new StringBuilder();
         var4.append("message file broken: code=").append(var1);
         if (var2.length > 0) {
            var4.append(" arguments={0}");

            for(int var5 = 1; var5 < var2.length; ++var5) {
               var4.append(", {").append(var5).append("}");
            }
         }

         var3 = var4.toString();
      }

      return MessageFormat.format(var3, var2);
   }

   static class Stats {
      public static final String OPT = "stats";
      public static final String NO_CODE = "";
      final ResourceBundle bundle;
      int[] groupCounts;
      int[] dkindCounts;
      Map codeCounts;

      Stats(ResourceBundle var1) {
         this.bundle = var1;
      }

      void setEnabled(boolean var1) {
         if (var1) {
            this.groupCounts = new int[Messages.Group.values().length];
            this.dkindCounts = new int[Kind.values().length];
            this.codeCounts = new HashMap();
         } else {
            this.groupCounts = null;
            this.dkindCounts = null;
            this.codeCounts = null;
         }

      }

      void record(Group var1, Diagnostic.Kind var2, String var3) {
         if (this.codeCounts != null) {
            int var10002 = this.groupCounts[var1.ordinal()]++;
            var10002 = this.dkindCounts[var2.ordinal()]++;
            if (var3 == null) {
               var3 = "";
            }

            Integer var4 = (Integer)this.codeCounts.get(var3);
            this.codeCounts.put(var3, var4 == null ? 1 : var4 + 1);
         }
      }

      void report(PrintWriter var1) {
         if (this.codeCounts != null) {
            var1.println("By group...");
            Table var2 = new Table();
            Group[] var3 = Messages.Group.values();
            int var4 = var3.length;

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               Group var6 = var3[var5];
               var2.put(var6.optName(), this.groupCounts[var6.ordinal()]);
            }

            var2.print(var1);
            var1.println();
            var1.println("By diagnostic kind...");
            Table var11 = new Table();
            Diagnostic.Kind[] var12 = Kind.values();
            var5 = var12.length;

            for(int var15 = 0; var15 < var5; ++var15) {
               Diagnostic.Kind var7 = var12[var15];
               var11.put(StringUtils.toLowerCase(var7.toString()), this.dkindCounts[var7.ordinal()]);
            }

            var11.print(var1);
            var1.println();
            var1.println("By message kind...");
            Table var13 = new Table();

            String var8;
            Map.Entry var16;
            for(Iterator var14 = this.codeCounts.entrySet().iterator(); var14.hasNext(); var13.put(var8, (Integer)var16.getValue())) {
               var16 = (Map.Entry)var14.next();
               String var17 = (String)var16.getKey();

               try {
                  var8 = var17.equals("") ? "OTHER" : this.bundle.getString(var17);
               } catch (MissingResourceException var10) {
                  var8 = var17;
               }
            }

            var13.print(var1);
         }
      }

      private static class Table {
         private static final Comparator DECREASING = new Comparator() {
            public int compare(Integer var1, Integer var2) {
               return var2.compareTo(var1);
            }
         };
         private final TreeMap map;

         private Table() {
            this.map = new TreeMap(DECREASING);
         }

         void put(String var1, int var2) {
            if (var2 != 0) {
               Object var3 = (Set)this.map.get(var2);
               if (var3 == null) {
                  this.map.put(var2, var3 = new TreeSet());
               }

               ((Set)var3).add(var1);
            }
         }

         void print(PrintWriter var1) {
            Iterator var2 = this.map.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               int var4 = (Integer)var3.getKey();
               Set var5 = (Set)var3.getValue();
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  var1.println(String.format("%6d: %s", var4, var7));
               }
            }

         }

         // $FF: synthetic method
         Table(Object var1) {
            this();
         }
      }
   }

   static class Options {
      Map map = new HashMap();
      private final Stats stats;
      private static final String ALL = "all";

      static boolean isValidOptions(String var0) {
         String[] var1 = var0.split(",");
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            if (!isValidOption(StringUtils.toLowerCase(var4.trim()))) {
               return false;
            }
         }

         return true;
      }

      private static boolean isValidOption(String var0) {
         if (!var0.equals("none") && !var0.equals("stats")) {
            int var1 = var0.startsWith("-") ? 1 : 0;
            int var2 = var0.indexOf("/");
            String var3 = var0.substring(var1, var2 != -1 ? var2 : var0.length());
            return (var1 == 0 && var3.equals("all") || Messages.Group.accepts(var3)) && (var2 == -1 || Env.AccessKind.accepts(var0.substring(var2 + 1)));
         } else {
            return true;
         }
      }

      Options(Stats var1) {
         this.stats = var1;
      }

      boolean isEnabled(Group var1, Env.AccessKind var2) {
         if (this.map.isEmpty()) {
            this.map.put("all", Env.AccessKind.PROTECTED);
         }

         Env.AccessKind var3 = (Env.AccessKind)this.map.get(var1.optName());
         if (var3 != null && var2.compareTo(var3) >= 0) {
            return true;
         } else {
            var3 = (Env.AccessKind)this.map.get("all");
            if (var3 != null && var2.compareTo(var3) >= 0) {
               var3 = (Env.AccessKind)this.map.get(var1.notOptName());
               if (var3 == null || var2.compareTo(var3) > 0) {
                  return true;
               }
            }

            return false;
         }
      }

      void setOptions(String var1) {
         if (var1 == null) {
            this.setOption("all", Env.AccessKind.PRIVATE);
         } else {
            String[] var2 = var1.split(",");
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];
               this.setOption(StringUtils.toLowerCase(var5.trim()));
            }
         }

      }

      private void setOption(String var1) throws IllegalArgumentException {
         if (var1.equals("stats")) {
            this.stats.setEnabled(true);
         } else {
            int var2 = var1.indexOf("/");
            if (var2 > 0) {
               Env.AccessKind var3 = Env.AccessKind.valueOf(StringUtils.toUpperCase(var1.substring(var2 + 1)));
               this.setOption(var1.substring(0, var2), var3);
            } else {
               this.setOption(var1, (Env.AccessKind)null);
            }

         }
      }

      private void setOption(String var1, Env.AccessKind var2) {
         this.map.put(var1, var2 != null ? var2 : (var1.startsWith("-") ? Env.AccessKind.PUBLIC : Env.AccessKind.PRIVATE));
      }
   }

   public static enum Group {
      ACCESSIBILITY,
      HTML,
      MISSING,
      SYNTAX,
      REFERENCE;

      String optName() {
         return StringUtils.toLowerCase(this.name());
      }

      String notOptName() {
         return "-" + this.optName();
      }

      static boolean accepts(String var0) {
         Group[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Group var4 = var1[var3];
            if (var0.equals(var4.optName())) {
               return true;
            }
         }

         return false;
      }
   }
}
