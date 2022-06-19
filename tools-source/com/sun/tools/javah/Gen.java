package com.sun.tools.javah;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public abstract class Gen {
   protected String lineSep = System.getProperty("line.separator");
   protected ProcessingEnvironment processingEnvironment;
   protected Types types;
   protected Elements elems;
   protected Mangle mangler;
   protected Util util;
   protected Set classes;
   private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
   protected JavaFileManager fileManager;
   protected JavaFileObject outFile;
   protected boolean force = false;

   protected Gen(Util var1) {
      this.util = var1;
   }

   protected abstract void write(OutputStream var1, TypeElement var2) throws Util.Exit;

   protected abstract String getIncludes();

   public void setFileManager(JavaFileManager var1) {
      this.fileManager = var1;
   }

   public void setOutFile(JavaFileObject var1) {
      this.outFile = var1;
   }

   public void setClasses(Set var1) {
      this.classes = var1;
   }

   void setProcessingEnvironment(ProcessingEnvironment var1) {
      this.processingEnvironment = var1;
      this.elems = var1.getElementUtils();
      this.types = var1.getTypeUtils();
      this.mangler = new Mangle(this.elems, this.types);
   }

   public void setForce(boolean var1) {
      this.force = var1;
   }

   protected PrintWriter wrapWriter(OutputStream var1) throws Util.Exit {
      try {
         return new PrintWriter(new OutputStreamWriter(var1, "ISO8859_1"), true);
      } catch (UnsupportedEncodingException var3) {
         this.util.bug("encoding.iso8859_1.not.found");
         return null;
      }
   }

   public void run() throws IOException, ClassNotFoundException, Util.Exit {
      boolean var1 = false;
      if (this.outFile != null) {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream(8192);
         this.writeFileTop(var2);
         Iterator var3 = this.classes.iterator();

         while(var3.hasNext()) {
            TypeElement var4 = (TypeElement)var3.next();
            this.write(var2, var4);
         }

         this.writeIfChanged(var2.toByteArray(), this.outFile);
      } else {
         Iterator var5 = this.classes.iterator();

         while(var5.hasNext()) {
            TypeElement var6 = (TypeElement)var5.next();
            ByteArrayOutputStream var7 = new ByteArrayOutputStream(8192);
            this.writeFileTop(var7);
            this.write(var7, var6);
            this.writeIfChanged(var7.toByteArray(), this.getFileObject(var6.getQualifiedName()));
         }
      }

   }

   private void writeIfChanged(byte[] var1, FileObject var2) throws IOException {
      boolean var3 = false;
      String var4 = "[No need to update file ";
      if (this.force) {
         var3 = true;
         var4 = "[Forcefully writing file ";
      } else {
         try {
            InputStream var5 = var2.openInputStream();
            byte[] var6 = this.readBytes(var5);
            if (!Arrays.equals(var6, var1)) {
               var3 = true;
               var4 = "[Overwriting file ";
            }
         } catch (FileNotFoundException var8) {
            var3 = true;
            var4 = "[Creating file ";
         }
      }

      if (this.util.verbose) {
         this.util.log(var4 + var2 + "]");
      }

      if (var3) {
         OutputStream var9 = var2.openOutputStream();
         var9.write(var1);
         var9.close();
      }

   }

   protected byte[] readBytes(InputStream var1) throws IOException {
      try {
         byte[] var2 = new byte[var1.available() + 1];
         int var3 = 0;

         int var4;
         while((var4 = var1.read(var2, var3, var2.length - var3)) != -1) {
            var3 += var4;
            if (var3 == var2.length) {
               var2 = Arrays.copyOf(var2, var2.length * 2);
            }
         }

         byte[] var5 = Arrays.copyOf(var2, var3);
         return var5;
      } finally {
         var1.close();
      }
   }

   protected String defineForStatic(TypeElement var1, VariableElement var2) throws Util.Exit {
      Name var3 = var1.getQualifiedName();
      Name var4 = var2.getSimpleName();
      String var5 = this.mangler.mangle(var3, 1);
      String var6 = this.mangler.mangle(var4, 2);
      if (!var2.getModifiers().contains(Modifier.STATIC)) {
         this.util.bug("tried.to.define.non.static");
      }

      if (var2.getModifiers().contains(Modifier.FINAL)) {
         Object var7 = null;
         var7 = var2.getConstantValue();
         if (var7 != null) {
            String var8 = null;
            if (!(var7 instanceof Integer) && !(var7 instanceof Byte) && !(var7 instanceof Short)) {
               if (var7 instanceof Boolean) {
                  var8 = (Boolean)var7 ? "1L" : "0L";
               } else if (var7 instanceof Character) {
                  Character var9 = (Character)var7;
                  var8 = (var9 & '\uffff') + "L";
               } else if (var7 instanceof Long) {
                  if (isWindows) {
                     var8 = var7.toString() + "i64";
                  } else {
                     var8 = var7.toString() + "LL";
                  }
               } else if (var7 instanceof Float) {
                  float var11 = (Float)var7;
                  if (Float.isInfinite(var11)) {
                     var8 = (var11 < 0.0F ? "-" : "") + "Inff";
                  } else {
                     var8 = var7.toString() + "f";
                  }
               } else if (var7 instanceof Double) {
                  double var12 = (Double)var7;
                  if (Double.isInfinite(var12)) {
                     var8 = (var12 < 0.0 ? "-" : "") + "InfD";
                  } else {
                     var8 = var7.toString();
                  }
               }
            } else {
               var8 = var7.toString() + "L";
            }

            if (var8 != null) {
               StringBuilder var13 = new StringBuilder("#undef ");
               var13.append(var5);
               var13.append("_");
               var13.append(var6);
               var13.append(this.lineSep);
               var13.append("#define ");
               var13.append(var5);
               var13.append("_");
               var13.append(var6);
               var13.append(" ");
               var13.append(var8);
               return var13.toString();
            }
         }
      }

      return null;
   }

   protected String cppGuardBegin() {
      return "#ifdef __cplusplus" + this.lineSep + "extern \"C\" {" + this.lineSep + "#endif";
   }

   protected String cppGuardEnd() {
      return "#ifdef __cplusplus" + this.lineSep + "}" + this.lineSep + "#endif";
   }

   protected String guardBegin(String var1) {
      return "/* Header for class " + var1 + " */" + this.lineSep + this.lineSep + "#ifndef _Included_" + var1 + this.lineSep + "#define _Included_" + var1;
   }

   protected String guardEnd(String var1) {
      return "#endif";
   }

   protected void writeFileTop(OutputStream var1) throws Util.Exit {
      PrintWriter var2 = this.wrapWriter(var1);
      var2.println("/* DO NOT EDIT THIS FILE - it is machine generated */" + this.lineSep + this.getIncludes());
   }

   protected String baseFileName(CharSequence var1) {
      return this.mangler.mangle(var1, 1);
   }

   protected FileObject getFileObject(CharSequence var1) throws IOException {
      String var2 = this.baseFileName(var1) + this.getFileSuffix();
      return this.fileManager.getFileForOutput(StandardLocation.SOURCE_OUTPUT, "", var2, (FileObject)null);
   }

   protected String getFileSuffix() {
      return ".h";
   }

   List getAllFields(TypeElement var1) {
      ArrayList var2 = new ArrayList();
      TypeElement var3 = null;
      Stack var4 = new Stack();
      var3 = var1;

      while(true) {
         var4.push(var3);
         TypeElement var5 = (TypeElement)((TypeElement)this.types.asElement(var3.getSuperclass()));
         if (var5 == null) {
            while(!var4.empty()) {
               var3 = (TypeElement)var4.pop();
               var2.addAll(ElementFilter.fieldsIn(var3.getEnclosedElements()));
            }

            return var2;
         }

         var3 = var5;
      }
   }

   String signature(ExecutableElement var1) {
      StringBuilder var2 = new StringBuilder("(");
      String var3 = "";

      for(Iterator var4 = var1.getParameters().iterator(); var4.hasNext(); var3 = ",") {
         VariableElement var5 = (VariableElement)var4.next();
         var2.append(var3);
         var2.append(this.types.erasure(var5.asType()).toString());
      }

      var2.append(")");
      return var2.toString();
   }
}
