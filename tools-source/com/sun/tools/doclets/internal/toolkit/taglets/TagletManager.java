package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;
import com.sun.tools.javac.util.StringUtils;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileManager;
import javax.tools.DocumentationTool.Location;

public class TagletManager {
   public static final char SIMPLE_TAGLET_OPT_SEPARATOR = ':';
   public static final String ALT_SIMPLE_TAGLET_OPT_SEPARATOR = "-";
   private LinkedHashMap customTags = new LinkedHashMap();
   private Taglet[] packageTags;
   private Taglet[] typeTags;
   private Taglet[] fieldTags;
   private Taglet[] constructorTags;
   private Taglet[] methodTags;
   private Taglet[] overviewTags;
   private Taglet[] inlineTags;
   private Taglet[] serializedFormTags;
   private MessageRetriever message;
   private Set standardTags = new HashSet();
   private Set standardTagsLowercase = new HashSet();
   private Set overridenStandardTags = new HashSet();
   private Set potentiallyConflictingTags = new HashSet();
   private Set unseenCustomTags = new HashSet();
   private boolean nosince;
   private boolean showversion;
   private boolean showauthor;
   private boolean javafx;

   public TagletManager(boolean var1, boolean var2, boolean var3, boolean var4, MessageRetriever var5) {
      this.nosince = var1;
      this.showversion = var2;
      this.showauthor = var3;
      this.javafx = var4;
      this.message = var5;
      this.initStandardTaglets();
      this.initStandardTagsLowercase();
   }

   public void addCustomTag(Taglet var1) {
      if (var1 != null) {
         String var2 = var1.getName();
         if (this.customTags.containsKey(var2)) {
            this.customTags.remove(var2);
         }

         this.customTags.put(var2, var1);
         this.checkTagName(var2);
      }

   }

   public Set getCustomTagNames() {
      return this.customTags.keySet();
   }

   public void addCustomTag(String var1, JavaFileManager var2, String var3) {
      try {
         Class var4 = null;
         String var5 = null;
         Object var6;
         if (var2 != null && var2.hasLocation(Location.TAGLET_PATH)) {
            var6 = var2.getClassLoader(Location.TAGLET_PATH);
         } else {
            var5 = this.appendPath(System.getProperty("env.class.path"), var5);
            var5 = this.appendPath(System.getProperty("java.class.path"), var5);
            var5 = this.appendPath(var3, var5);
            var6 = new URLClassLoader(this.pathToURLs(var5));
         }

         var4 = ((ClassLoader)var6).loadClass(var1);
         Method var7 = var4.getMethod("register", Map.class);
         Object[] var8 = this.customTags.values().toArray();
         Taglet var9 = var8 != null && var8.length > 0 ? (Taglet)var8[var8.length - 1] : null;
         var7.invoke((Object)null, this.customTags);
         var8 = this.customTags.values().toArray();
         Object var10 = var8 != null && var8.length > 0 ? var8[var8.length - 1] : null;
         if (var9 != var10) {
            this.message.notice("doclet.Notice_taglet_registered", var1);
            if (var10 != null) {
               this.checkTaglet(var10);
            }
         }
      } catch (Exception var11) {
         this.message.error("doclet.Error_taglet_not_registered", var11.getClass().getName(), var1);
      }

   }

   private String appendPath(String var1, String var2) {
      if (var1 != null && var1.length() != 0) {
         return var2 != null && var2.length() != 0 ? var1 + File.pathSeparator + var2 : var1;
      } else {
         return var2 == null ? "." : var2;
      }
   }

   private URL[] pathToURLs(String var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      String[] var3 = var1.split(File.pathSeparator);
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (!var6.isEmpty()) {
            try {
               var2.add((new File(var6)).getAbsoluteFile().toURI().toURL());
            } catch (MalformedURLException var8) {
               this.message.error("doclet.MalformedURL", var6);
            }
         }
      }

      return (URL[])var2.toArray(new URL[var2.size()]);
   }

   public void addNewSimpleCustomTag(String var1, String var2, String var3) {
      if (var1 != null && var3 != null) {
         Taglet var4 = (Taglet)this.customTags.get(var1);
         var3 = StringUtils.toLowerCase(var3);
         if (var4 != null && var2 == null) {
            this.customTags.remove(var1);
            this.customTags.put(var1, var4);
         } else {
            this.customTags.remove(var1);
            this.customTags.put(var1, new SimpleTaglet(var1, var2, var3));
            if (var3 != null && var3.indexOf(120) == -1) {
               this.checkTagName(var1);
            }
         }

      }
   }

   private void checkTagName(String var1) {
      if (this.standardTags.contains(var1)) {
         this.overridenStandardTags.add(var1);
      } else {
         if (var1.indexOf(46) == -1) {
            this.potentiallyConflictingTags.add(var1);
         }

         this.unseenCustomTags.add(var1);
      }

   }

   private void checkTaglet(Object var1) {
      if (var1 instanceof Taglet) {
         this.checkTagName(((Taglet)var1).getName());
      } else {
         if (!(var1 instanceof com.sun.tools.doclets.Taglet)) {
            throw new IllegalArgumentException("Given object is not a taglet.");
         }

         com.sun.tools.doclets.Taglet var2 = (com.sun.tools.doclets.Taglet)var1;
         this.customTags.remove(var2.getName());
         this.customTags.put(var2.getName(), new LegacyTaglet(var2));
         this.checkTagName(var2.getName());
      }

   }

   public void seenCustomTag(String var1) {
      this.unseenCustomTags.remove(var1);
   }

   public void checkTags(Doc var1, Tag[] var2, boolean var3) {
      if (var2 != null) {
         for(int var5 = 0; var5 < var2.length; ++var5) {
            String var6 = var2[var5].name();
            if (var6.length() > 0 && var6.charAt(0) == '@') {
               var6 = var6.substring(1, var6.length());
            }

            if (!this.standardTags.contains(var6) && !this.customTags.containsKey(var6)) {
               if (this.standardTagsLowercase.contains(StringUtils.toLowerCase(var6))) {
                  this.message.warning(var2[var5].position(), "doclet.UnknownTagLowercase", var2[var5].name());
               } else {
                  this.message.warning(var2[var5].position(), "doclet.UnknownTag", var2[var5].name());
               }
            } else {
               Taglet var4;
               if ((var4 = (Taglet)this.customTags.get(var6)) != null) {
                  if (var3 && !var4.isInlineTag()) {
                     this.printTagMisuseWarn(var4, var2[var5], "inline");
                  }

                  if (var1 instanceof RootDoc && !var4.inOverview()) {
                     this.printTagMisuseWarn(var4, var2[var5], "overview");
                  } else if (var1 instanceof PackageDoc && !var4.inPackage()) {
                     this.printTagMisuseWarn(var4, var2[var5], "package");
                  } else if (var1 instanceof ClassDoc && !var4.inType()) {
                     this.printTagMisuseWarn(var4, var2[var5], "class");
                  } else if (var1 instanceof ConstructorDoc && !var4.inConstructor()) {
                     this.printTagMisuseWarn(var4, var2[var5], "constructor");
                  } else if (var1 instanceof FieldDoc && !var4.inField()) {
                     this.printTagMisuseWarn(var4, var2[var5], "field");
                  } else if (var1 instanceof MethodDoc && !var4.inMethod()) {
                     this.printTagMisuseWarn(var4, var2[var5], "method");
                  }
               }
            }
         }

      }
   }

   private void printTagMisuseWarn(Taglet var1, Tag var2, String var3) {
      LinkedHashSet var4 = new LinkedHashSet();
      if (var1.inOverview()) {
         var4.add("overview");
      }

      if (var1.inPackage()) {
         var4.add("package");
      }

      if (var1.inType()) {
         var4.add("class/interface");
      }

      if (var1.inConstructor()) {
         var4.add("constructor");
      }

      if (var1.inField()) {
         var4.add("field");
      }

      if (var1.inMethod()) {
         var4.add("method");
      }

      if (var1.isInlineTag()) {
         var4.add("inline text");
      }

      String[] var5 = (String[])var4.toArray(new String[0]);
      if (var5 != null && var5.length != 0) {
         StringBuilder var6 = new StringBuilder();

         for(int var7 = 0; var7 < var5.length; ++var7) {
            if (var7 > 0) {
               var6.append(", ");
            }

            var6.append(var5[var7]);
         }

         this.message.warning(var2.position(), "doclet.tag_misuse", "@" + var1.getName(), var3, var6.toString());
      }
   }

   public Taglet[] getPackageCustomTaglets() {
      if (this.packageTags == null) {
         this.initCustomTagletArrays();
      }

      return this.packageTags;
   }

   public Taglet[] getTypeCustomTaglets() {
      if (this.typeTags == null) {
         this.initCustomTagletArrays();
      }

      return this.typeTags;
   }

   public Taglet[] getInlineCustomTaglets() {
      if (this.inlineTags == null) {
         this.initCustomTagletArrays();
      }

      return this.inlineTags;
   }

   public Taglet[] getFieldCustomTaglets() {
      if (this.fieldTags == null) {
         this.initCustomTagletArrays();
      }

      return this.fieldTags;
   }

   public Taglet[] getSerializedFormTaglets() {
      if (this.serializedFormTags == null) {
         this.initCustomTagletArrays();
      }

      return this.serializedFormTags;
   }

   public Taglet[] getCustomTaglets(Doc var1) {
      if (var1 instanceof ConstructorDoc) {
         return this.getConstructorCustomTaglets();
      } else if (var1 instanceof MethodDoc) {
         return this.getMethodCustomTaglets();
      } else if (var1 instanceof FieldDoc) {
         return this.getFieldCustomTaglets();
      } else if (var1 instanceof ClassDoc) {
         return this.getTypeCustomTaglets();
      } else if (var1 instanceof PackageDoc) {
         return this.getPackageCustomTaglets();
      } else {
         return var1 instanceof RootDoc ? this.getOverviewCustomTaglets() : null;
      }
   }

   public Taglet[] getConstructorCustomTaglets() {
      if (this.constructorTags == null) {
         this.initCustomTagletArrays();
      }

      return this.constructorTags;
   }

   public Taglet[] getMethodCustomTaglets() {
      if (this.methodTags == null) {
         this.initCustomTagletArrays();
      }

      return this.methodTags;
   }

   public Taglet[] getOverviewCustomTaglets() {
      if (this.overviewTags == null) {
         this.initCustomTagletArrays();
      }

      return this.overviewTags;
   }

   private void initCustomTagletArrays() {
      Iterator var1 = this.customTags.values().iterator();
      ArrayList var2 = new ArrayList(this.customTags.size());
      ArrayList var3 = new ArrayList(this.customTags.size());
      ArrayList var4 = new ArrayList(this.customTags.size());
      ArrayList var5 = new ArrayList(this.customTags.size());
      ArrayList var6 = new ArrayList(this.customTags.size());
      ArrayList var7 = new ArrayList(this.customTags.size());
      ArrayList var8 = new ArrayList(this.customTags.size());
      ArrayList var9 = new ArrayList();

      while(var1.hasNext()) {
         Taglet var10 = (Taglet)var1.next();
         if (var10.inPackage() && !var10.isInlineTag()) {
            var2.add(var10);
         }

         if (var10.inType() && !var10.isInlineTag()) {
            var3.add(var10);
         }

         if (var10.inField() && !var10.isInlineTag()) {
            var4.add(var10);
         }

         if (var10.inConstructor() && !var10.isInlineTag()) {
            var5.add(var10);
         }

         if (var10.inMethod() && !var10.isInlineTag()) {
            var6.add(var10);
         }

         if (var10.isInlineTag()) {
            var7.add(var10);
         }

         if (var10.inOverview() && !var10.isInlineTag()) {
            var8.add(var10);
         }
      }

      this.packageTags = (Taglet[])var2.toArray(new Taglet[0]);
      this.typeTags = (Taglet[])var3.toArray(new Taglet[0]);
      this.fieldTags = (Taglet[])var4.toArray(new Taglet[0]);
      this.constructorTags = (Taglet[])var5.toArray(new Taglet[0]);
      this.methodTags = (Taglet[])var6.toArray(new Taglet[0]);
      this.overviewTags = (Taglet[])var8.toArray(new Taglet[0]);
      this.inlineTags = (Taglet[])var7.toArray(new Taglet[0]);
      var9.add(this.customTags.get("serialData"));
      var9.add(this.customTags.get("throws"));
      if (!this.nosince) {
         var9.add(this.customTags.get("since"));
      }

      var9.add(this.customTags.get("see"));
      this.serializedFormTags = (Taglet[])var9.toArray(new Taglet[0]);
   }

   private void initStandardTaglets() {
      if (this.javafx) {
         this.initJavaFXTaglets();
      }

      this.addStandardTaglet(new ParamTaglet());
      this.addStandardTaglet(new ReturnTaglet());
      this.addStandardTaglet(new ThrowsTaglet());
      this.addStandardTaglet(new SimpleTaglet("exception", (String)null, "mc"));
      this.addStandardTaglet(!this.nosince, new SimpleTaglet("since", this.message.getText("doclet.Since"), "a"));
      this.addStandardTaglet(this.showversion, new SimpleTaglet("version", this.message.getText("doclet.Version"), "pto"));
      this.addStandardTaglet(this.showauthor, new SimpleTaglet("author", this.message.getText("doclet.Author"), "pto"));
      this.addStandardTaglet(new SimpleTaglet("serialData", this.message.getText("doclet.SerialData"), "x"));
      SimpleTaglet var1;
      this.customTags.put((var1 = new SimpleTaglet("factory", this.message.getText("doclet.Factory"), "m")).getName(), var1);
      this.addStandardTaglet(new SeeTaglet());
      this.addStandardTaglet(new DocRootTaglet());
      this.addStandardTaglet(new InheritDocTaglet());
      this.addStandardTaglet(new ValueTaglet());
      this.addStandardTaglet(new LiteralTaglet());
      this.addStandardTaglet(new CodeTaglet());
      this.standardTags.add("deprecated");
      this.standardTags.add("link");
      this.standardTags.add("linkplain");
      this.standardTags.add("serial");
      this.standardTags.add("serialField");
      this.standardTags.add("Text");
   }

   private void initJavaFXTaglets() {
      this.addStandardTaglet(new PropertyGetterTaglet());
      this.addStandardTaglet(new PropertySetterTaglet());
      this.addStandardTaglet(new SimpleTaglet("propertyDescription", this.message.getText("doclet.PropertyDescription"), "fm"));
      this.addStandardTaglet(new SimpleTaglet("defaultValue", this.message.getText("doclet.DefaultValue"), "fm"));
      this.addStandardTaglet(new SimpleTaglet("treatAsPrivate", (String)null, "fmt"));
   }

   void addStandardTaglet(Taglet var1) {
      String var2 = var1.getName();
      this.customTags.put(var2, var1);
      this.standardTags.add(var2);
   }

   void addStandardTaglet(boolean var1, Taglet var2) {
      String var3 = var2.getName();
      if (var1) {
         this.customTags.put(var3, var2);
      }

      this.standardTags.add(var3);
   }

   private void initStandardTagsLowercase() {
      Iterator var1 = this.standardTags.iterator();

      while(var1.hasNext()) {
         this.standardTagsLowercase.add(StringUtils.toLowerCase((String)var1.next()));
      }

   }

   public boolean isKnownCustomTag(String var1) {
      return this.customTags.containsKey(var1);
   }

   public void printReport() {
      this.printReportHelper("doclet.Notice_taglet_conflict_warn", this.potentiallyConflictingTags);
      this.printReportHelper("doclet.Notice_taglet_overriden", this.overridenStandardTags);
      this.printReportHelper("doclet.Notice_taglet_unseen", this.unseenCustomTags);
   }

   private void printReportHelper(String var1, Set var2) {
      if (var2.size() > 0) {
         String[] var3 = (String[])var2.toArray(new String[0]);
         String var4 = " ";

         for(int var5 = 0; var5 < var3.length; ++var5) {
            var4 = var4 + "@" + var3[var5];
            if (var5 + 1 < var3.length) {
               var4 = var4 + ", ";
            }
         }

         this.message.notice(var1, var4);
      }

   }

   public Taglet getTaglet(String var1) {
      return var1.indexOf("@") == 0 ? (Taglet)this.customTags.get(var1.substring(1)) : (Taglet)this.customTags.get(var1);
   }
}
