package sun.tools.javac;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.tools.java.BinaryClass;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.ClassPath;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Package;
import sun.tools.java.Type;
import sun.tools.tree.Node;

/** @deprecated */
@Deprecated
public class BatchEnvironment extends Environment implements ErrorConsumer {
   OutputStream out;
   protected ClassPath sourcePath;
   protected ClassPath binaryPath;
   Hashtable packages;
   Vector classesOrdered;
   Hashtable classes;
   public int flags;
   public short majorVersion;
   public short minorVersion;
   public File covFile;
   public int nerrors;
   public int nwarnings;
   public int ndeprecations;
   Vector deprecationFiles;
   ErrorConsumer errorConsumer;
   private Set exemptPackages;
   String errorFileName;
   ErrorMessage errors;
   private int errorsPushed;
   public int errorLimit;
   private boolean hitErrorLimit;

   public BatchEnvironment(ClassPath var1) {
      this((OutputStream)System.out, var1);
   }

   public BatchEnvironment(OutputStream var1, ClassPath var2) {
      this(var1, var2, (ErrorConsumer)null);
   }

   public BatchEnvironment(OutputStream var1, ClassPath var2, ErrorConsumer var3) {
      this(var1, var2, var2, var3);
   }

   public BatchEnvironment(ClassPath var1, ClassPath var2) {
      this(System.out, var1, (ClassPath)var2);
   }

   public BatchEnvironment(OutputStream var1, ClassPath var2, ClassPath var3) {
      this(var1, var2, var3, (ErrorConsumer)null);
   }

   public BatchEnvironment(OutputStream var1, ClassPath var2, ClassPath var3, ErrorConsumer var4) {
      this.packages = new Hashtable(31);
      this.classesOrdered = new Vector();
      this.classes = new Hashtable(351);
      this.majorVersion = 45;
      this.minorVersion = 3;
      this.deprecationFiles = new Vector();
      this.errorLimit = 100;
      this.out = var1;
      this.sourcePath = var2;
      this.binaryPath = var3;
      this.errorConsumer = (ErrorConsumer)(var4 == null ? this : var4);
   }

   static BatchEnvironment create(OutputStream var0, String var1, String var2, String var3, String var4) {
      ClassPath[] var5 = classPaths(var1, var2, var3, var4);
      return new BatchEnvironment(var0, var5[0], var5[1]);
   }

   protected static ClassPath[] classPaths(String var0, String var1, String var2, String var3) {
      StringBuffer var6 = new StringBuffer();
      if (var1 == null) {
         var1 = System.getProperty("env.class.path");
         if (var1 == null) {
            var1 = ".";
         }
      }

      if (var0 == null) {
         var0 = var1;
      }

      if (var2 == null) {
         var2 = System.getProperty("sun.boot.class.path");
         if (var2 == null) {
            var2 = var1;
         }
      }

      appendPath(var6, var2);
      if (var3 == null) {
         var3 = System.getProperty("java.ext.dirs");
      }

      if (var3 != null) {
         StringTokenizer var7 = new StringTokenizer(var3, File.pathSeparator);

         label47:
         while(true) {
            String var8;
            File var9;
            do {
               if (!var7.hasMoreTokens()) {
                  break label47;
               }

               var8 = var7.nextToken();
               var9 = new File(var8);
               if (!var8.endsWith(File.separator)) {
                  var8 = var8 + File.separator;
               }
            } while(!var9.isDirectory());

            String[] var10 = var9.list();

            for(int var11 = 0; var11 < var10.length; ++var11) {
               String var12 = var10[var11];
               if (var12.endsWith(".jar")) {
                  appendPath(var6, var8 + var12);
               }
            }
         }
      }

      appendPath(var6, var1);
      ClassPath var4 = new ClassPath(var0);
      ClassPath var5 = new ClassPath(var6.toString());
      return new ClassPath[]{var4, var5};
   }

   private static void appendPath(StringBuffer var0, String var1) {
      if (var1.length() > 0) {
         if (var0.length() > 0) {
            var0.append(File.pathSeparator);
         }

         var0.append(var1);
      }

   }

   public int getFlags() {
      return this.flags;
   }

   public short getMajorVersion() {
      return this.majorVersion;
   }

   public short getMinorVersion() {
      return this.minorVersion;
   }

   public File getcovFile() {
      return this.covFile;
   }

   public Enumeration getClasses() {
      return this.classesOrdered.elements();
   }

   public boolean isExemptPackage(Identifier var1) {
      if (this.exemptPackages == null) {
         this.setExemptPackages();
      }

      return this.exemptPackages.contains(var1);
   }

   private void setExemptPackages() {
      this.exemptPackages = new HashSet(101);
      Enumeration var1 = this.getClasses();

      while(true) {
         SourceClass var3;
         do {
            ClassDeclaration var2;
            do {
               if (!var1.hasMoreElements()) {
                  if (!this.exemptPackages.contains(idJavaLang)) {
                     this.exemptPackages.add(idJavaLang);

                     try {
                        if (!this.getPackage(idJavaLang).exists()) {
                           this.error(0L, "package.not.found.strong", idJavaLang);
                           return;
                        }
                     } catch (IOException var5) {
                        this.error(0L, "io.exception.package", idJavaLang);
                     }
                  }

                  return;
               }

               var2 = (ClassDeclaration)var1.nextElement();
            } while(var2.getStatus() != 4);

            var3 = (SourceClass)var2.getClassDefinition();
         } while(var3.isLocal());

         for(Identifier var4 = var3.getImports().getCurrentPackage(); var4 != idNull && this.exemptPackages.add(var4); var4 = var4.getQualifier()) {
         }
      }
   }

   public ClassDeclaration getClassDeclaration(Identifier var1) {
      return this.getClassDeclaration(Type.tClass(var1));
   }

   public ClassDeclaration getClassDeclaration(Type var1) {
      ClassDeclaration var2 = (ClassDeclaration)this.classes.get(var1);
      if (var2 == null) {
         this.classes.put(var1, var2 = new ClassDeclaration(var1.getClassName()));
         this.classesOrdered.addElement(var2);
      }

      return var2;
   }

   public boolean classExists(Identifier var1) {
      if (var1.isInner()) {
         var1 = var1.getTopName();
      }

      Type var2 = Type.tClass(var1);

      try {
         ClassDeclaration var3 = (ClassDeclaration)this.classes.get(var2);
         return var3 != null ? var3.getName().equals(var1) : this.getPackage(var1.getQualifier()).classExists(var1.getName());
      } catch (IOException var4) {
         return true;
      }
   }

   public Package getPackage(Identifier var1) throws IOException {
      Package var2 = (Package)this.packages.get(var1);
      if (var2 == null) {
         this.packages.put(var1, var2 = new Package(this.sourcePath, this.binaryPath, var1));
      }

      return var2;
   }

   public void parseFile(ClassFile var1) throws FileNotFoundException {
      long var2 = System.currentTimeMillis();
      this.dtEnter("parseFile: PARSING SOURCE " + var1);
      Environment var6 = new Environment(this, var1);

      InputStream var4;
      BatchParser var5;
      try {
         var4 = var1.getInputStream();
         var6.setCharacterEncoding(this.getCharacterEncoding());
         var5 = new BatchParser(var6, var4);
      } catch (IOException var13) {
         this.dtEvent("parseFile: IO EXCEPTION " + var1);
         throw new FileNotFoundException();
      }

      try {
         var5.parseFile();
      } catch (Exception var12) {
         throw new CompilerError(var12);
      }

      try {
         var4.close();
      } catch (IOException var11) {
      }

      if (this.verbose()) {
         var2 = System.currentTimeMillis() - var2;
         this.output(Main.getText("benv.parsed_in", var1.getPath(), Long.toString(var2)));
      }

      if (var5.classes.size() == 0) {
         var5.imports.resolve(var6);
      } else {
         Enumeration var7 = var5.classes.elements();
         ClassDefinition var8 = (ClassDefinition)var7.nextElement();
         if (var8.isInnerClass()) {
            throw new CompilerError("BatchEnvironment, first is inner");
         }

         ClassDefinition var9 = var8;

         while(var7.hasMoreElements()) {
            ClassDefinition var10 = (ClassDefinition)var7.nextElement();
            if (!var10.isInnerClass()) {
               var9.addDependency(var10.getClassDeclaration());
               var10.addDependency(var9.getClassDeclaration());
               var9 = var10;
            }
         }

         if (var9 != var8) {
            var9.addDependency(var8.getClassDeclaration());
            var8.addDependency(var9.getClassDeclaration());
         }
      }

      this.dtExit("parseFile: SOURCE PARSED " + var1);
   }

   BinaryClass loadFile(ClassFile var1) throws IOException {
      long var2 = System.currentTimeMillis();
      InputStream var4 = var1.getInputStream();
      BinaryClass var5 = null;
      this.dtEnter("loadFile: LOADING CLASSFILE " + var1);

      try {
         DataInputStream var6 = new DataInputStream(new BufferedInputStream(var4));
         var5 = BinaryClass.load(new Environment(this, var1), var6, this.loadFileFlags());
      } catch (ClassFormatError var7) {
         this.error(0L, "class.format", var1.getPath(), var7.getMessage());
         this.dtExit("loadFile: CLASS FORMAT ERROR " + var1);
         return null;
      } catch (EOFException var8) {
         this.error(0L, "truncated.class", var1.getPath());
         return null;
      }

      var4.close();
      if (this.verbose()) {
         var2 = System.currentTimeMillis() - var2;
         this.output(Main.getText("benv.loaded_in", var1.getPath(), Long.toString(var2)));
      }

      this.dtExit("loadFile: CLASSFILE LOADED " + var1);
      return var5;
   }

   int loadFileFlags() {
      return 0;
   }

   boolean needsCompilation(Hashtable var1, ClassDeclaration var2) {
      switch (var2.getStatus()) {
         case 0:
            this.dtEnter("needsCompilation: UNDEFINED " + var2.getName());
            this.loadDefinition(var2);
            return this.needsCompilation(var1, var2);
         case 1:
            this.dtEnter("needsCompilation: UNDECIDED " + var2.getName());
            if (var1.get(var2) == null) {
               var1.put(var2, var2);
               BinaryClass var3 = (BinaryClass)var2.getClassDefinition();
               Enumeration var4 = var3.getDependencies();

               while(var4.hasMoreElements()) {
                  ClassDeclaration var5 = (ClassDeclaration)var4.nextElement();
                  if (this.needsCompilation(var1, var5)) {
                     var2.setDefinition(var3, 3);
                     this.dtExit("needsCompilation: YES (source) " + var2.getName());
                     return true;
                  }
               }
            }

            this.dtExit("needsCompilation: NO (undecided) " + var2.getName());
            return false;
         case 2:
            this.dtEnter("needsCompilation: BINARY " + var2.getName());
            this.dtExit("needsCompilation: NO (binary) " + var2.getName());
            return false;
         default:
            this.dtExit("needsCompilation: YES " + var2.getName());
            return true;
      }
   }

   public void loadDefinition(ClassDeclaration var1) {
      this.dtEnter("loadDefinition: ENTER " + var1.getName() + ", status " + var1.getStatus());
      Package var3;
      switch (var1.getStatus()) {
         case 0:
            this.dtEvent("loadDefinition: STATUS IS UNDEFINED");
            Identifier var19 = var1.getName();

            try {
               var3 = this.getPackage(var19.getQualifier());
            } catch (IOException var13) {
               var1.setDefinition((ClassDefinition)null, 7);
               this.error(0L, "io.exception", var1);
               this.dtExit("loadDefinition: IO EXCEPTION (package)");
               return;
            }

            ClassFile var18 = var3.getBinaryFile(var19.getName());
            if (var18 == null) {
               var1.setDefinition((ClassDefinition)null, 3);
               this.dtExit("loadDefinition: MUST BE SOURCE (no binary) " + var1.getName());
               return;
            } else {
               ClassFile var5 = var3.getSourceFile(var19.getName());
               BinaryClass var6;
               if (var5 == null) {
                  this.dtEvent("loadDefinition: NO SOURCE " + var1.getName());
                  var6 = null;

                  try {
                     var6 = this.loadFile(var18);
                  } catch (IOException var11) {
                     var1.setDefinition((ClassDefinition)null, 7);
                     this.error(0L, "io.exception", var18);
                     this.dtExit("loadDefinition: IO EXCEPTION (binary)");
                     return;
                  }

                  if (var6 != null && !var6.getName().equals(var19)) {
                     this.error(0L, "wrong.class", var18.getPath(), var1, var6);
                     var6 = null;
                     this.dtEvent("loadDefinition: WRONG CLASS (binary)");
                  }

                  if (var6 == null) {
                     var1.setDefinition((ClassDefinition)null, 7);
                     this.dtExit("loadDefinition: NOT FOUND (source or binary)");
                     return;
                  } else {
                     if (var6.getSource() != null) {
                        var5 = new ClassFile(new File((String)var6.getSource()));
                        var5 = var3.getSourceFile(var5.getName());
                        if (var5 != null && var5.exists()) {
                           this.dtEvent("loadDefinition: FILENAME IN BINARY " + var5);
                           if (var5.lastModified() > var18.lastModified()) {
                              var1.setDefinition(var6, 3);
                              this.dtEvent("loadDefinition: SOURCE IS NEWER " + var5);
                              var6.loadNested(this);
                              this.dtExit("loadDefinition: MUST BE SOURCE " + var1.getName());
                              return;
                           }

                           if (this.dependencies()) {
                              var1.setDefinition(var6, 1);
                              this.dtEvent("loadDefinition: UNDECIDED " + var1.getName());
                           } else {
                              var1.setDefinition(var6, 2);
                              this.dtEvent("loadDefinition: MUST BE BINARY " + var1.getName());
                           }

                           var6.loadNested(this);
                           this.dtExit("loadDefinition: EXIT " + var1.getName() + ", status " + var1.getStatus());
                           return;
                        }
                     }

                     var1.setDefinition(var6, 2);
                     this.dtEvent("loadDefinition: MUST BE BINARY (no source) " + var1.getName());
                     var6.loadNested(this);
                     this.dtExit("loadDefinition: EXIT " + var1.getName() + ", status " + var1.getStatus());
                     return;
                  }
               } else {
                  var6 = null;

                  try {
                     if (var5.lastModified() > var18.lastModified()) {
                        var1.setDefinition((ClassDefinition)null, 3);
                        this.dtEvent("loadDefinition: MUST BE SOURCE (younger than binary) " + var1.getName());
                        return;
                     }

                     var6 = this.loadFile(var18);
                  } catch (IOException var12) {
                     this.error(0L, "io.exception", var18);
                     this.dtEvent("loadDefinition: IO EXCEPTION (binary)");
                  }

                  if (var6 != null && !var6.getName().equals(var19)) {
                     this.error(0L, "wrong.class", var18.getPath(), var1, var6);
                     var6 = null;
                     this.dtEvent("loadDefinition: WRONG CLASS (binary)");
                  }

                  if (var6 != null) {
                     Identifier var7 = var6.getName();
                     if (var7.equals(var1.getName())) {
                        if (this.dependencies()) {
                           var1.setDefinition(var6, 1);
                           this.dtEvent("loadDefinition: UNDECIDED " + var7);
                        } else {
                           var1.setDefinition(var6, 2);
                           this.dtEvent("loadDefinition: MUST BE BINARY " + var7);
                        }
                     } else {
                        var1.setDefinition((ClassDefinition)null, 7);
                        this.dtEvent("loadDefinition: NOT FOUND (source or binary)");
                        if (this.dependencies()) {
                           this.getClassDeclaration(var7).setDefinition(var6, 1);
                           this.dtEvent("loadDefinition: UNDECIDED " + var7);
                        } else {
                           this.getClassDeclaration(var7).setDefinition(var6, 2);
                           this.dtEvent("loadDefinition: MUST BE BINARY " + var7);
                        }
                     }
                  } else {
                     var1.setDefinition((ClassDefinition)null, 7);
                     this.dtEvent("loadDefinition: NOT FOUND (source or binary)");
                  }

                  if (var6 != null && var6 == var1.getClassDefinition()) {
                     var6.loadNested(this);
                  }

                  this.dtExit("loadDefinition: EXIT " + var1.getName() + ", status " + var1.getStatus());
                  return;
               }
            }
         case 1:
            this.dtEvent("loadDefinition: STATUS IS UNDECIDED");
            Hashtable var17 = new Hashtable();
            if (!this.needsCompilation(var17, var1)) {
               Enumeration var15 = var17.keys();

               while(var15.hasMoreElements()) {
                  ClassDeclaration var16 = (ClassDeclaration)var15.nextElement();
                  if (var16.getStatus() == 1) {
                     var16.setDefinition(var16.getClassDefinition(), 2);
                     this.dtEvent("loadDefinition: MUST BE BINARY " + var16);
                  }
               }
            }

            this.dtExit("loadDefinition: EXIT " + var1.getName() + ", status " + var1.getStatus());
            return;
         case 2:
         default:
            this.dtExit("loadDefinition: EXIT " + var1.getName() + ", status " + var1.getStatus());
            return;
         case 3:
            this.dtEvent("loadDefinition: STATUS IS SOURCE");
            ClassFile var2 = null;
            var3 = null;
            if (var1.getClassDefinition() != null) {
               try {
                  var3 = this.getPackage(var1.getName().getQualifier());
                  var2 = var3.getSourceFile((String)var1.getClassDefinition().getSource());
               } catch (IOException var10) {
                  this.error(0L, "io.exception", var1);
                  this.dtEvent("loadDefinition: IO EXCEPTION (package)");
               }

               if (var2 == null) {
                  String var4 = (String)var1.getClassDefinition().getSource();
                  var2 = new ClassFile(new File(var4));
               }
            } else {
               Identifier var14 = var1.getName();

               try {
                  var3 = this.getPackage(var14.getQualifier());
                  var2 = var3.getSourceFile(var14.getName());
               } catch (IOException var9) {
                  this.error(0L, "io.exception", var1);
                  this.dtEvent("loadDefinition: IO EXCEPTION (package)");
               }

               if (var2 == null) {
                  var1.setDefinition((ClassDefinition)null, 7);
                  this.dtExit("loadDefinition: SOURCE NOT FOUND " + var1.getName() + ", status " + var1.getStatus());
                  return;
               }
            }

            try {
               this.parseFile(var2);
            } catch (FileNotFoundException var8) {
               this.error(0L, "io.exception", var2);
               this.dtEvent("loadDefinition: IO EXCEPTION (source)");
            }

            if (var1.getClassDefinition() == null || var1.getStatus() == 3) {
               this.error(0L, "wrong.source", var2.getPath(), var1, var3);
               var1.setDefinition((ClassDefinition)null, 7);
               this.dtEvent("loadDefinition: WRONG CLASS (source) " + var1.getName());
            }

            this.dtExit("loadDefinition: EXIT " + var1.getName() + ", status " + var1.getStatus());
      }
   }

   public ClassDefinition makeClassDefinition(Environment var1, long var2, IdentifierToken var4, String var5, int var6, IdentifierToken var7, IdentifierToken[] var8, ClassDefinition var9) {
      Identifier var10 = var4.getName();
      long var11 = var4.getWhere();
      String var14 = null;
      ClassDefinition var15 = null;
      Identifier var16 = null;
      Identifier var13;
      if (!var10.isQualified() && !var10.isInner()) {
         if ((var6 & 196608) != 0) {
            var15 = var9.getTopClass();
            int var17 = 1;

            while(true) {
               var14 = var17 + (var10.equals(idNull) ? "" : "$" + var10);
               if (var15.getLocalClass(var14) == null) {
                  Identifier var19 = var15.getName();
                  var13 = Identifier.lookupInner(var19, Identifier.lookup(var14));
                  if ((var6 & 65536) != 0) {
                     var16 = idNull;
                  } else {
                     var16 = var10;
                  }
                  break;
               }

               ++var17;
            }
         } else if (var9 != null) {
            var13 = Identifier.lookupInner(var9.getName(), var10);
         } else {
            var13 = var10;
         }
      } else {
         var13 = var10;
      }

      ClassDeclaration var20 = var1.getClassDeclaration(var13);
      if (var20.isDefined()) {
         var1.error(var11, "class.multidef", var20.getName(), var20.getClassDefinition().getSource());
         var20 = new ClassDeclaration(var13);
      }

      if (var7 == null && !var13.equals(idJavaLangObject)) {
         var7 = new IdentifierToken(idJavaLangObject);
      }

      SourceClass var18 = new SourceClass(var1, var2, var20, var5, var6, var7, var8, (SourceClass)var9, var16);
      if (var9 != null) {
         var9.addMember(var1, new SourceMember(var18));
         if ((var6 & 196608) != 0) {
            var15.addLocalClass(var18, var14);
         }
      }

      return var18;
   }

   public MemberDefinition makeMemberDefinition(Environment var1, long var2, ClassDefinition var4, String var5, int var6, Type var7, Identifier var8, IdentifierToken[] var9, IdentifierToken[] var10, Object var11) {
      this.dtEvent("makeMemberDefinition: " + var8 + " IN " + var4);
      Vector var12 = null;
      if (var9 != null) {
         var12 = new Vector(var9.length);

         for(int var13 = 0; var13 < var9.length; ++var13) {
            var12.addElement(var9[var13]);
         }
      }

      SourceMember var14 = new SourceMember(var2, var4, var5, var6, var7, var8, var12, var10, (Node)var11);
      var4.addMember(var1, var14);
      return var14;
   }

   public void shutdown() {
      try {
         if (this.sourcePath != null) {
            this.sourcePath.close();
         }

         if (this.binaryPath != null && this.binaryPath != this.sourcePath) {
            this.binaryPath.close();
         }
      } catch (IOException var2) {
         this.output(Main.getText("benv.failed_to_close_class_path", var2.toString()));
      }

      this.sourcePath = null;
      this.binaryPath = null;
      super.shutdown();
   }

   public String errorString(String var1, Object var2, Object var3, Object var4) {
      String var5 = null;
      if (var1.startsWith("warn.")) {
         var5 = "javac.err." + var1.substring(5);
      } else {
         var5 = "javac.err." + var1;
      }

      return Main.getText(var5, var2 != null ? var2.toString() : null, var3 != null ? var3.toString() : null, var4 != null ? var4.toString() : null);
   }

   protected boolean insertError(long var1, String var3) {
      ErrorMessage var4;
      if (this.errors != null && this.errors.where <= var1) {
         if (this.errors.where == var1 && this.errors.message.equals(var3)) {
            return false;
         }

         ErrorMessage var5;
         for(var4 = this.errors; (var5 = var4.next) != null && var5.where < var1; var4 = var5) {
         }

         while((var5 = var4.next) != null && var5.where == var1) {
            if (var5.message.equals(var3)) {
               return false;
            }

            var4 = var5;
         }

         ErrorMessage var6 = new ErrorMessage(var1, var3);
         var6.next = var4.next;
         var4.next = var6;
      } else {
         var4 = new ErrorMessage(var1, var3);
         var4.next = this.errors;
         this.errors = var4;
      }

      return true;
   }

   public void pushError(String var1, int var2, String var3, String var4, String var5) {
      int var6 = this.errorLimit + this.nwarnings;
      if (++this.errorsPushed >= var6 && this.errorLimit >= 0) {
         if (!this.hitErrorLimit) {
            this.hitErrorLimit = true;
            this.output(this.errorString("too.many.errors", new Integer(this.errorLimit), (Object)null, (Object)null));
         }

      } else {
         if (var1.endsWith(".java")) {
            this.output(var1 + ":" + var2 + ": " + var3);
            this.output(var4);
            this.output(var5);
         } else {
            this.output(var1 + ": " + var3);
         }

      }
   }

   public void flushErrors() {
      if (this.errors != null) {
         boolean var1 = false;
         char[] var2 = null;
         int var3 = 0;

         try {
            FileInputStream var4 = new FileInputStream(this.errorFileName);
            var2 = new char[var4.available()];
            InputStreamReader var5 = this.getCharacterEncoding() != null ? new InputStreamReader(var4, this.getCharacterEncoding()) : new InputStreamReader(var4);
            var3 = var5.read(var2);
            var5.close();
            var1 = true;
         } catch (IOException var12) {
         }

         for(ErrorMessage var13 = this.errors; var13 != null; var13 = var13.next) {
            int var14 = (int)(var13.where >>> 32);
            int var6 = (int)(var13.where & 4294967295L);
            if (var6 > var3) {
               var6 = var3;
            }

            String var7 = "";
            String var8 = "";
            if (var1) {
               int var9;
               for(var9 = var6; var9 > 0 && var2[var9 - 1] != '\n' && var2[var9 - 1] != '\r'; --var9) {
               }

               int var10;
               for(var10 = var6; var10 < var3 && var2[var10] != '\n' && var2[var10] != '\r'; ++var10) {
               }

               var7 = new String(var2, var9, var10 - var9);
               char[] var11 = new char[var6 - var9 + 1];

               for(var10 = var9; var10 < var6; ++var10) {
                  var11[var10 - var9] = (char)(var2[var10] == '\t' ? 9 : 32);
               }

               var11[var6 - var9] = '^';
               var8 = new String(var11);
            }

            this.errorConsumer.pushError(this.errorFileName, var14, var13.message, var7, var8);
         }

         this.errors = null;
      }
   }

   public void reportError(Object var1, long var2, String var4, String var5) {
      if (var1 == null) {
         if (this.errorFileName != null) {
            this.flushErrors();
            this.errorFileName = null;
         }

         if (var4.startsWith("warn.")) {
            if (this.warnings()) {
               ++this.nwarnings;
               this.output(var5);
            }

            return;
         }

         this.output("error: " + var5);
         ++this.nerrors;
         this.flags |= 65536;
      } else if (var1 instanceof String) {
         String var6 = (String)var1;
         if (!var6.equals(this.errorFileName)) {
            this.flushErrors();
            this.errorFileName = var6;
         }

         if (var4.startsWith("warn.")) {
            if (var4.indexOf("is.deprecated") >= 0) {
               if (!this.deprecationFiles.contains(var1)) {
                  this.deprecationFiles.addElement(var1);
               }

               if (this.deprecation()) {
                  if (this.insertError(var2, var5)) {
                     ++this.ndeprecations;
                  }
               } else {
                  ++this.ndeprecations;
               }
            } else if (this.warnings()) {
               if (this.insertError(var2, var5)) {
                  ++this.nwarnings;
               }
            } else {
               ++this.nwarnings;
            }
         } else if (this.insertError(var2, var5)) {
            ++this.nerrors;
            this.flags |= 65536;
         }
      } else if (var1 instanceof ClassFile) {
         this.reportError(((ClassFile)var1).getPath(), var2, var4, var5);
      } else if (var1 instanceof Identifier) {
         this.reportError(var1.toString(), var2, var4, var5);
      } else if (var1 instanceof ClassDeclaration) {
         try {
            this.reportError(((ClassDeclaration)var1).getClassDefinition(this), var2, var4, var5);
         } catch (ClassNotFound var7) {
            this.reportError(((ClassDeclaration)var1).getName(), var2, var4, var5);
         }
      } else if (var1 instanceof ClassDefinition) {
         ClassDefinition var8 = (ClassDefinition)var1;
         if (!var4.startsWith("warn.")) {
            var8.setError();
         }

         this.reportError(var8.getSource(), var2, var4, var5);
      } else if (var1 instanceof MemberDefinition) {
         this.reportError(((MemberDefinition)var1).getClassDeclaration(), var2, var4, var5);
      } else {
         this.output(var1 + ":error=" + var4 + ":" + var5);
      }

   }

   public void error(Object var1, long var2, String var4, Object var5, Object var6, Object var7) {
      if (this.errorsPushed < this.errorLimit + this.nwarnings) {
         if (System.getProperty("javac.dump.stack") != null) {
            this.output("javac.err." + var4 + ": " + this.errorString(var4, var5, var6, var7));
            (new Exception("Stack trace")).printStackTrace(new PrintStream(this.out));
         }

         this.reportError(var1, var2, var4, this.errorString(var4, var5, var6, var7));
      }
   }

   public void output(String var1) {
      PrintStream var2 = this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out, true);
      var2.println(var1);
   }
}
