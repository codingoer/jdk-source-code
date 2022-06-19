package com.sun.tools.javap;

import com.sun.tools.classfile.Attribute;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.LineNumberTable_attribute;
import com.sun.tools.classfile.SourceFile_attribute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class SourceWriter extends InstructionDetailWriter {
   private JavaFileManager fileManager;
   private ClassFile classFile;
   private SortedMap lineMap;
   private List lineList;
   private String[] sourceLines;

   static SourceWriter instance(Context var0) {
      SourceWriter var1 = (SourceWriter)var0.get(SourceWriter.class);
      if (var1 == null) {
         var1 = new SourceWriter(var0);
      }

      return var1;
   }

   protected SourceWriter(Context var1) {
      super(var1);
      var1.put(SourceWriter.class, this);
   }

   void setFileManager(JavaFileManager var1) {
      this.fileManager = var1;
   }

   public void reset(ClassFile var1, Code_attribute var2) {
      this.setSource(var1);
      this.setLineMap(var2);
   }

   public void writeDetails(Instruction var1) {
      String var2 = this.space(40);
      Set var3 = (Set)this.lineMap.get(var1.getPC());
      if (var3 != null) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            int var5 = (Integer)var4.next();
            this.print(var2);
            this.print(String.format(" %4d ", var5));
            if (var5 < this.sourceLines.length) {
               this.print(this.sourceLines[var5]);
            }

            this.println();
            int var6 = this.nextLine(var5);

            for(int var7 = var5 + 1; var7 < var6; ++var7) {
               this.print(var2);
               this.print(String.format("(%4d)", var7));
               if (var7 < this.sourceLines.length) {
                  this.print(this.sourceLines[var7]);
               }

               this.println();
            }
         }
      }

   }

   public boolean hasSource() {
      return this.sourceLines.length > 0;
   }

   private void setLineMap(Code_attribute var1) {
      TreeMap var2 = new TreeMap();
      TreeSet var3 = new TreeSet();
      Iterator var4 = var1.attributes.iterator();

      while(true) {
         Attribute var5;
         do {
            if (!var4.hasNext()) {
               this.lineMap = var2;
               this.lineList = new ArrayList(var3);
               return;
            }

            var5 = (Attribute)var4.next();
         } while(!(var5 instanceof LineNumberTable_attribute));

         LineNumberTable_attribute var6 = (LineNumberTable_attribute)var5;
         LineNumberTable_attribute.Entry[] var7 = var6.line_number_table;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            LineNumberTable_attribute.Entry var10 = var7[var9];
            int var11 = var10.start_pc;
            int var12 = var10.line_number;
            Object var13 = (SortedSet)var2.get(var11);
            if (var13 == null) {
               var13 = new TreeSet();
               var2.put(var11, var13);
            }

            ((SortedSet)var13).add(var12);
            var3.add(var12);
         }
      }
   }

   private void setSource(ClassFile var1) {
      if (var1 != this.classFile) {
         this.classFile = var1;
         this.sourceLines = splitLines(this.readSource(var1));
      }

   }

   private String readSource(ClassFile var1) {
      if (this.fileManager == null) {
         return null;
      } else {
         StandardLocation var2;
         if (this.fileManager.hasLocation(StandardLocation.SOURCE_PATH)) {
            var2 = StandardLocation.SOURCE_PATH;
         } else {
            var2 = StandardLocation.CLASS_PATH;
         }

         try {
            String var3 = var1.getName();
            SourceFile_attribute var4 = (SourceFile_attribute)var1.attributes.get("SourceFile");
            if (var4 == null) {
               this.report(this.messages.getMessage("err.no.SourceFile.attribute"));
               return null;
            } else {
               String var5 = var4.getSourceFile(var1.constant_pool);
               String var6 = var5.endsWith(".java") ? var5.substring(0, var5.length() - 5) : var5;
               int var7 = var3.lastIndexOf("/");
               String var8 = var7 == -1 ? "" : var3.substring(0, var7 + 1);
               String var9 = (var8 + var6).replace('/', '.');
               JavaFileObject var10 = this.fileManager.getJavaFileForInput(var2, var9, javax.tools.JavaFileObject.Kind.SOURCE);
               if (var10 == null) {
                  this.report(this.messages.getMessage("err.source.file.not.found"));
                  return null;
               } else {
                  return var10.getCharContent(true).toString();
               }
            }
         } catch (ConstantPoolException var11) {
            this.report(var11);
            return null;
         } catch (IOException var12) {
            this.report(var12.getLocalizedMessage());
            return null;
         }
      }
   }

   private static String[] splitLines(String var0) {
      if (var0 == null) {
         return new String[0];
      } else {
         ArrayList var1 = new ArrayList();
         var1.add("");

         try {
            BufferedReader var2 = new BufferedReader(new StringReader(var0));

            String var3;
            while((var3 = var2.readLine()) != null) {
               var1.add(var3);
            }
         } catch (IOException var4) {
         }

         return (String[])var1.toArray(new String[var1.size()]);
      }
   }

   private int nextLine(int var1) {
      int var2 = this.lineList.indexOf(var1);
      return var2 != -1 && var2 != this.lineList.size() - 1 ? (Integer)this.lineList.get(var2 + 1) : -1;
   }
}
