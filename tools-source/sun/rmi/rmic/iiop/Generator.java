package sun.rmi.rmic.iiop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import sun.rmi.rmic.IndentingWriter;
import sun.rmi.rmic.Main;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassPath;
import sun.tools.java.Identifier;

public abstract class Generator implements sun.rmi.rmic.Generator, Constants {
   protected boolean alwaysGenerate = false;
   protected BatchEnvironment env = null;
   protected ContextStack contextStack = null;
   private boolean trace = false;
   protected boolean idl = false;

   public boolean parseArgs(String[] var1, Main var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null) {
            if (!var1[var3].equalsIgnoreCase("-always") && !var1[var3].equalsIgnoreCase("-alwaysGenerate")) {
               if (var1[var3].equalsIgnoreCase("-xtrace")) {
                  this.trace = true;
                  var1[var3] = null;
               }
            } else {
               this.alwaysGenerate = true;
               var1[var3] = null;
            }
         }
      }

      return true;
   }

   protected abstract boolean parseNonConforming(ContextStack var1);

   protected abstract CompoundType getTopType(ClassDefinition var1, ContextStack var2);

   protected abstract OutputType[] getOutputTypesFor(CompoundType var1, HashSet var2);

   protected abstract String getFileNameExtensionFor(OutputType var1);

   protected abstract void writeOutputFor(OutputType var1, HashSet var2, IndentingWriter var3) throws IOException;

   protected abstract boolean requireNewInstance();

   public boolean requiresGeneration(File var1, Type var2) {
      boolean var3 = this.alwaysGenerate;
      if (!var3) {
         ClassPath var5 = this.env.getClassPath();
         String var6 = var2.getQualifiedName().replace('.', File.separatorChar);
         ClassFile var4 = var5.getFile(var6 + ".source");
         if (var4 == null) {
            var4 = var5.getFile(var6 + ".class");
         }

         if (var4 != null) {
            long var7 = var4.lastModified();
            String var9 = IDLNames.replace(var1.getName(), ".java", ".class");
            String var10 = var1.getParent();
            File var11 = new File(var10, var9);
            if (var11.exists()) {
               long var12 = var11.lastModified();
               var3 = var12 < var7;
            } else {
               var3 = true;
            }
         } else {
            var3 = true;
         }
      }

      return var3;
   }

   protected Generator newInstance() {
      Generator var1 = null;

      try {
         var1 = (Generator)this.getClass().newInstance();
      } catch (Exception var3) {
      }

      return var1;
   }

   protected Generator() {
   }

   public void generate(sun.rmi.rmic.BatchEnvironment var1, ClassDefinition var2, File var3) {
      this.env = (BatchEnvironment)var1;
      this.contextStack = new ContextStack(this.env);
      this.contextStack.setTrace(this.trace);
      this.env.setParseNonConforming(this.parseNonConforming(this.contextStack));
      CompoundType var4 = this.getTopType(var2, this.contextStack);
      if (var4 != null) {
         Generator var5 = this;
         if (this.requireNewInstance()) {
            var5 = this.newInstance();
         }

         var5.generateOutputFiles(var4, this.env, var3);
      }

   }

   protected void generateOutputFiles(CompoundType var1, BatchEnvironment var2, File var3) {
      HashSet var4 = var2.alreadyChecked;
      OutputType[] var5 = this.getOutputTypesFor(var1, var4);

      for(int var6 = 0; var6 < var5.length; ++var6) {
         OutputType var7 = var5[var6];
         String var8 = var7.getName();
         File var9 = this.getFileFor(var7, var3);
         boolean var10 = false;
         if (this.requiresGeneration(var9, var7.getType())) {
            if (var9.getName().endsWith(".java")) {
               var10 = this.compileJavaSourceFile(var7);
               if (var10) {
                  var2.addGeneratedFile(var9);
               }
            }

            try {
               IndentingWriter var11 = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(var9)), 4, Integer.MAX_VALUE);
               long var12 = 0L;
               if (var2.verbose()) {
                  var12 = System.currentTimeMillis();
               }

               this.writeOutputFor(var5[var6], var4, var11);
               var11.close();
               if (var2.verbose()) {
                  long var14 = System.currentTimeMillis() - var12;
                  var2.output(Main.getText("rmic.generated", var9.getPath(), Long.toString(var14)));
               }

               if (var10) {
                  var2.parseFile(new ClassFile(var9));
               }
            } catch (IOException var16) {
               var2.error(0L, "cant.write", var9.toString());
               return;
            }
         } else if (var2.verbose()) {
            var2.output(Main.getText("rmic.previously.generated", var9.getPath()));
         }
      }

   }

   protected File getFileFor(OutputType var1, File var2) {
      Identifier var3 = this.getOutputId(var1);
      File var4 = null;
      if (this.idl) {
         var4 = Util.getOutputDirectoryForIDL(var3, var2, this.env);
      } else {
         var4 = Util.getOutputDirectoryForStub(var3, var2, this.env);
      }

      String var5 = var1.getName() + this.getFileNameExtensionFor(var1);
      return new File(var4, var5);
   }

   protected Identifier getOutputId(OutputType var1) {
      return var1.getType().getIdentifier();
   }

   protected boolean compileJavaSourceFile(OutputType var1) {
      return true;
   }

   public class OutputType {
      private String name;
      private Type type;

      public OutputType(String var2, Type var3) {
         this.name = var2;
         this.type = var3;
      }

      public String getName() {
         return this.name;
      }

      public Type getType() {
         return this.type;
      }
   }
}
