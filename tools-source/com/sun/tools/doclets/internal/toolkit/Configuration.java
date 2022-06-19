package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.internal.toolkit.builders.BuilderFactory;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletManager;
import com.sun.tools.doclets.internal.toolkit.util.ClassDocCatalog;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Extern;
import com.sun.tools.doclets.internal.toolkit.util.Group;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;
import com.sun.tools.doclets.internal.toolkit.util.MetaKeywords;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javac.sym.Profiles;
import com.sun.tools.javac.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaFileManager;

public abstract class Configuration {
   protected BuilderFactory builderFactory;
   public TagletManager tagletManager;
   public String builderXMLPath;
   private static final String DEFAULT_BUILDER_XML = "resources/doclet.xml";
   public String tagletpath = "";
   public boolean serialwarn = false;
   public int sourcetab;
   public String tabSpaces;
   public boolean linksource = false;
   public boolean nosince = false;
   public boolean copydocfilesubdirs = false;
   public String charset = "";
   public boolean keywords = false;
   public final MetaKeywords metakeywords = new MetaKeywords(this);
   protected Set excludedDocFileDirs;
   protected Set excludedQualifiers;
   public RootDoc root;
   public String destDirName = "";
   public String docFileDestDirName = "";
   public String docencoding = null;
   public boolean nocomment = false;
   public String encoding = null;
   public boolean showauthor = false;
   public boolean javafx = false;
   public boolean showversion = false;
   public String sourcepath = "";
   public String profilespath = "";
   public boolean showProfiles = false;
   public boolean nodeprecated = false;
   public ClassDocCatalog classDocCatalog;
   public MessageRetriever message = null;
   public boolean notimestamp = false;
   public final Group group = new Group(this);
   public final Extern extern = new Extern(this);
   public Profiles profiles;
   public Map profilePackages;
   public PackageDoc[] packages;

   public abstract String getDocletSpecificBuildDate();

   public abstract void setSpecificDocletOptions(String[][] var1) throws Fault;

   public abstract MessageRetriever getDocletSpecificMsg();

   public Configuration() {
      this.message = new MessageRetriever(this, "com.sun.tools.doclets.internal.toolkit.resources.doclets");
      this.excludedDocFileDirs = new HashSet();
      this.excludedQualifiers = new HashSet();
      this.setTabWidth(8);
   }

   public BuilderFactory getBuilderFactory() {
      if (this.builderFactory == null) {
         this.builderFactory = new BuilderFactory(this);
      }

      return this.builderFactory;
   }

   public int optionLength(String var1) {
      var1 = StringUtils.toLowerCase(var1);
      if (!var1.equals("-author") && !var1.equals("-docfilessubdirs") && !var1.equals("-javafx") && !var1.equals("-keywords") && !var1.equals("-linksource") && !var1.equals("-nocomment") && !var1.equals("-nodeprecated") && !var1.equals("-nosince") && !var1.equals("-notimestamp") && !var1.equals("-quiet") && !var1.equals("-xnodate") && !var1.equals("-version")) {
         if (!var1.equals("-d") && !var1.equals("-docencoding") && !var1.equals("-encoding") && !var1.equals("-excludedocfilessubdir") && !var1.equals("-link") && !var1.equals("-sourcetab") && !var1.equals("-noqualifier") && !var1.equals("-output") && !var1.equals("-sourcepath") && !var1.equals("-tag") && !var1.equals("-taglet") && !var1.equals("-tagletpath") && !var1.equals("-xprofilespath")) {
            return !var1.equals("-group") && !var1.equals("-linkoffline") ? -1 : 3;
         } else {
            return 2;
         }
      } else {
         return 1;
      }
   }

   public abstract boolean validOptions(String[][] var1, DocErrorReporter var2);

   private void initProfiles() throws IOException {
      if (!this.profilespath.isEmpty()) {
         this.profiles = Profiles.read(new File(this.profilespath));
         EnumMap var1 = new EnumMap(Profile.class);
         Profile[] var2 = Profile.values();
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            Profile var5 = var2[var4];
            var1.put(var5, new ArrayList());
         }

         PackageDoc[] var9 = this.packages;
         var3 = var9.length;

         for(var4 = 0; var4 < var3; ++var4) {
            PackageDoc var12 = var9[var4];
            if (!this.nodeprecated || !Util.isDeprecated(var12)) {
               int var6 = this.profiles.getProfile(var12.name().replace(".", "/") + "/*");
               Profile var7 = Profile.lookup(var6);
               if (var7 != null) {
                  List var8 = (List)var1.get(var7);
                  var8.add(var12);
               }
            }
         }

         this.profilePackages = new HashMap();
         List var10 = Collections.emptyList();

         List var15;
         for(Iterator var11 = var1.entrySet().iterator(); var11.hasNext(); var10 = var15) {
            Map.Entry var13 = (Map.Entry)var11.next();
            Profile var14 = (Profile)var13.getKey();
            var15 = (List)var13.getValue();
            var15.addAll(var10);
            Collections.sort(var15);
            var3 = var15.size();
            if (var3 > 0) {
               this.profilePackages.put(var14.name, var15.toArray(new PackageDoc[var15.size()]));
            }
         }

         this.showProfiles = !var10.isEmpty();
      }
   }

   private void initPackageArray() {
      HashSet var1 = new HashSet(Arrays.asList(this.root.specifiedPackages()));
      ClassDoc[] var2 = this.root.specifiedClasses();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.add(var2[var3].containingPackage());
      }

      ArrayList var4 = new ArrayList(var1);
      Collections.sort(var4);
      this.packages = (PackageDoc[])var4.toArray(new PackageDoc[0]);
   }

   public void setOptions(String[][] var1) throws Fault {
      LinkedHashSet var2 = new LinkedHashSet();

      int var3;
      String[] var4;
      String var5;
      for(var3 = 0; var3 < var1.length; ++var3) {
         var4 = var1[var3];
         var5 = StringUtils.toLowerCase(var4[0]);
         if (var5.equals("-d")) {
            this.destDirName = addTrailingFileSep(var4[1]);
            this.docFileDestDirName = this.destDirName;
            this.ensureOutputDirExists();
            break;
         }
      }

      for(var3 = 0; var3 < var1.length; ++var3) {
         var4 = var1[var3];
         var5 = StringUtils.toLowerCase(var4[0]);
         if (var5.equals("-docfilessubdirs")) {
            this.copydocfilesubdirs = true;
         } else if (var5.equals("-docencoding")) {
            this.docencoding = var4[1];
         } else if (var5.equals("-encoding")) {
            this.encoding = var4[1];
         } else if (var5.equals("-author")) {
            this.showauthor = true;
         } else if (var5.equals("-javafx")) {
            this.javafx = true;
         } else if (var5.equals("-nosince")) {
            this.nosince = true;
         } else if (var5.equals("-version")) {
            this.showversion = true;
         } else if (var5.equals("-nodeprecated")) {
            this.nodeprecated = true;
         } else if (var5.equals("-sourcepath")) {
            this.sourcepath = var4[1];
         } else if ((var5.equals("-classpath") || var5.equals("-cp")) && this.sourcepath.length() == 0) {
            this.sourcepath = var4[1];
         } else if (var5.equals("-excludedocfilessubdir")) {
            this.addToSet(this.excludedDocFileDirs, var4[1]);
         } else if (var5.equals("-noqualifier")) {
            this.addToSet(this.excludedQualifiers, var4[1]);
         } else if (var5.equals("-linksource")) {
            this.linksource = true;
         } else if (var5.equals("-sourcetab")) {
            this.linksource = true;

            try {
               this.setTabWidth(Integer.parseInt(var4[1]));
            } catch (NumberFormatException var8) {
               this.sourcetab = -1;
            }

            if (this.sourcetab <= 0) {
               this.message.warning("doclet.sourcetab_warning");
               this.setTabWidth(8);
            }
         } else if (var5.equals("-notimestamp")) {
            this.notimestamp = true;
         } else if (var5.equals("-nocomment")) {
            this.nocomment = true;
         } else if (!var5.equals("-tag") && !var5.equals("-taglet")) {
            if (var5.equals("-tagletpath")) {
               this.tagletpath = var4[1];
            } else if (var5.equals("-xprofilespath")) {
               this.profilespath = var4[1];
            } else if (var5.equals("-keywords")) {
               this.keywords = true;
            } else if (var5.equals("-serialwarn")) {
               this.serialwarn = true;
            } else if (var5.equals("-group")) {
               this.group.checkPackageGroups(var4[1], var4[2]);
            } else {
               String var6;
               if (var5.equals("-link")) {
                  var6 = var4[1];
                  this.extern.link(var6, var6, this.root, false);
               } else if (var5.equals("-linkoffline")) {
                  var6 = var4[1];
                  String var7 = var4[2];
                  this.extern.link(var6, var7, this.root, true);
               }
            }
         } else {
            var2.add(var4);
         }
      }

      if (this.sourcepath.length() == 0) {
         this.sourcepath = System.getProperty("env.class.path") == null ? "" : System.getProperty("env.class.path");
      }

      if (this.docencoding == null) {
         this.docencoding = this.encoding;
      }

      this.classDocCatalog = new ClassDocCatalog(this.root.specifiedClasses(), this);
      this.initTagletManager(var2);
   }

   public void setOptions() throws Fault {
      this.initPackageArray();
      this.setOptions(this.root.options());

      try {
         this.initProfiles();
      } catch (Exception var2) {
         throw new DocletAbortException(var2);
      }

      this.setSpecificDocletOptions(this.root.options());
   }

   private void ensureOutputDirExists() throws Fault {
      DocFile var1 = DocFile.createFileForDirectory(this, this.destDirName);
      if (!var1.exists()) {
         this.root.printNotice(this.getText("doclet.dest_dir_create", this.destDirName));
         var1.mkdirs();
      } else {
         if (!var1.isDirectory()) {
            throw new Fault(this.getText("doclet.destination_directory_not_directory_0", var1.getPath()));
         }

         if (!var1.canWrite()) {
            throw new Fault(this.getText("doclet.destination_directory_not_writable_0", var1.getPath()));
         }
      }

   }

   private void initTagletManager(Set var1) {
      this.tagletManager = this.tagletManager == null ? new TagletManager(this.nosince, this.showversion, this.showauthor, this.javafx, this.message) : this.tagletManager;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String[] var2 = (String[])var3.next();
         if (var2[0].equals("-taglet")) {
            this.tagletManager.addCustomTag(var2[1], this.getFileManager(), this.tagletpath);
         } else {
            String[] var4 = this.tokenize(var2[1], ':', 3);
            if (var4.length == 1) {
               String var5 = var2[1];
               if (this.tagletManager.isKnownCustomTag(var5)) {
                  this.tagletManager.addNewSimpleCustomTag(var5, (String)null, "");
               } else {
                  StringBuilder var6 = new StringBuilder(var5 + ":");
                  var6.setCharAt(0, Character.toUpperCase(var5.charAt(0)));
                  this.tagletManager.addNewSimpleCustomTag(var5, var6.toString(), "a");
               }
            } else if (var4.length == 2) {
               this.tagletManager.addNewSimpleCustomTag(var4[0], var4[1], "");
            } else if (var4.length >= 3) {
               this.tagletManager.addNewSimpleCustomTag(var4[0], var4[2], var4[1]);
            } else {
               this.message.error("doclet.Error_invalid_custom_tag_argument", var2[1]);
            }
         }
      }

   }

   private String[] tokenize(String var1, char var2, int var3) {
      ArrayList var4 = new ArrayList();
      StringBuilder var5 = new StringBuilder();
      boolean var6 = false;

      for(int var7 = 0; var7 < var1.length(); var7 += Character.charCount(var7)) {
         int var8 = var1.codePointAt(var7);
         if (var6) {
            var5.appendCodePoint(var8);
            var6 = false;
         } else if (var8 == var2 && var4.size() < var3 - 1) {
            var4.add(var5.toString());
            var5 = new StringBuilder();
         } else if (var8 == 92) {
            var6 = true;
         } else {
            var5.appendCodePoint(var8);
         }
      }

      if (var5.length() > 0) {
         var4.add(var5.toString());
      }

      return (String[])var4.toArray(new String[0]);
   }

   private void addToSet(Set var1, String var2) {
      StringTokenizer var3 = new StringTokenizer(var2, ":");

      while(var3.hasMoreTokens()) {
         String var4 = var3.nextToken();
         var1.add(var4);
      }

   }

   public static String addTrailingFileSep(String var0) {
      String var1 = System.getProperty("file.separator");

      int var3;
      for(String var2 = var1 + var1; (var3 = var0.indexOf(var2, 1)) >= 0; var0 = var0.substring(0, var3) + var0.substring(var3 + var1.length())) {
      }

      if (!var0.endsWith(var1)) {
         var0 = var0 + var1;
      }

      return var0;
   }

   public boolean generalValidOptions(String[][] var1, DocErrorReporter var2) {
      boolean var3 = false;
      String var4 = "";

      for(int var5 = 0; var5 < var1.length; ++var5) {
         String[] var6 = var1[var5];
         String var7 = StringUtils.toLowerCase(var6[0]);
         if (var7.equals("-docencoding")) {
            var3 = true;
            if (!this.checkOutputFileEncoding(var6[1], var2)) {
               return false;
            }
         } else if (var7.equals("-encoding")) {
            var4 = var6[1];
         }
      }

      if (!var3 && var4.length() > 0 && !this.checkOutputFileEncoding(var4, var2)) {
         return false;
      } else {
         return true;
      }
   }

   public boolean shouldDocumentProfile(String var1) {
      return this.profilePackages.containsKey(var1);
   }

   private boolean checkOutputFileEncoding(String var1, DocErrorReporter var2) {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      OutputStreamWriter var4 = null;

      boolean var6;
      try {
         var4 = new OutputStreamWriter(var3, var1);
         return true;
      } catch (UnsupportedEncodingException var16) {
         var2.printError(this.getText("doclet.Encoding_not_supported", var1));
         var6 = false;
      } finally {
         try {
            if (var4 != null) {
               var4.close();
            }
         } catch (IOException var15) {
         }

      }

      return var6;
   }

   public boolean shouldExcludeDocFileDir(String var1) {
      return this.excludedDocFileDirs.contains(var1);
   }

   public boolean shouldExcludeQualifier(String var1) {
      if (!this.excludedQualifiers.contains("all") && !this.excludedQualifiers.contains(var1) && !this.excludedQualifiers.contains(var1 + ".*")) {
         int var2 = -1;

         do {
            if ((var2 = var1.indexOf(".", var2 + 1)) == -1) {
               return false;
            }
         } while(!this.excludedQualifiers.contains(var1.substring(0, var2 + 1) + "*"));

         return true;
      } else {
         return true;
      }
   }

   public String getClassName(ClassDoc var1) {
      PackageDoc var2 = var1.containingPackage();
      return var2 != null && this.shouldExcludeQualifier(var1.containingPackage().name()) ? var1.name() : var1.qualifiedName();
   }

   public String getText(String var1) {
      try {
         return this.getDocletSpecificMsg().getText(var1);
      } catch (Exception var3) {
         return this.message.getText(var1);
      }
   }

   public String getText(String var1, String var2) {
      try {
         return this.getDocletSpecificMsg().getText(var1, var2);
      } catch (Exception var4) {
         return this.message.getText(var1, var2);
      }
   }

   public String getText(String var1, String var2, String var3) {
      try {
         return this.getDocletSpecificMsg().getText(var1, var2, var3);
      } catch (Exception var5) {
         return this.message.getText(var1, var2, var3);
      }
   }

   public String getText(String var1, String var2, String var3, String var4) {
      try {
         return this.getDocletSpecificMsg().getText(var1, var2, var3, var4);
      } catch (Exception var6) {
         return this.message.getText(var1, var2, var3, var4);
      }
   }

   public abstract Content newContent();

   public Content getResource(String var1) {
      Content var2 = this.newContent();
      var2.addContent(this.getText(var1));
      return var2;
   }

   public Content getResource(String var1, Object var2) {
      return this.getResource(var1, var2, (Object)null, (Object)null);
   }

   public Content getResource(String var1, Object var2, Object var3) {
      return this.getResource(var1, var2, var3, (Object)null);
   }

   public Content getResource(String var1, Object var2, Object var3, Object var4) {
      Content var5 = this.newContent();
      Pattern var6 = Pattern.compile("\\{([012])\\}");
      String var7 = this.getText(var1);
      Matcher var8 = var6.matcher(var7);

      int var9;
      for(var9 = 0; var8.find(var9); var9 = var8.end()) {
         var5.addContent(var7.substring(var9, var8.start()));
         Object var10 = null;
         switch (var8.group(1).charAt(0)) {
            case '0':
               var10 = var2;
               break;
            case '1':
               var10 = var3;
               break;
            case '2':
               var10 = var4;
         }

         if (var10 == null) {
            var5.addContent("{" + var8.group(1) + "}");
         } else if (var10 instanceof String) {
            var5.addContent((String)var10);
         } else if (var10 instanceof Content) {
            var5.addContent((Content)var10);
         }
      }

      var5.addContent(var7.substring(var9));
      return var5;
   }

   public boolean isGeneratedDoc(ClassDoc var1) {
      if (!this.nodeprecated) {
         return true;
      } else {
         return !Util.isDeprecated(var1) && !Util.isDeprecated(var1.containingPackage());
      }
   }

   public abstract WriterFactory getWriterFactory();

   public InputStream getBuilderXML() throws IOException {
      return this.builderXMLPath == null ? Configuration.class.getResourceAsStream("resources/doclet.xml") : DocFile.createFileForInput(this, this.builderXMLPath).openInputStream();
   }

   public abstract Locale getLocale();

   public abstract JavaFileManager getFileManager();

   public abstract Comparator getMemberComparator();

   private void setTabWidth(int var1) {
      this.sourcetab = var1;
      this.tabSpaces = String.format("%" + var1 + "s", "");
   }

   public abstract boolean showMessage(SourcePosition var1, String var2);

   public static class Fault extends Exception {
      private static final long serialVersionUID = 0L;

      Fault(String var1) {
         super(var1);
      }

      Fault(String var1, Exception var2) {
         super(var1, var2);
      }
   }
}
