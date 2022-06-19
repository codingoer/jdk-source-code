package sun.rmi.rmic.iiop;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import sun.rmi.rmic.IndentingWriter;
import sun.rmi.rmic.Main;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;

public class PrintGenerator implements sun.rmi.rmic.Generator, Constants {
   private static final int JAVA = 0;
   private static final int IDL = 1;
   private static final int BOTH = 2;
   private int whatToPrint;
   private boolean global = false;
   private boolean qualified = false;
   private boolean trace = false;
   private boolean valueMethods = false;
   private IndentingWriter out;

   public PrintGenerator() {
      OutputStreamWriter var1 = new OutputStreamWriter(System.out);
      this.out = new IndentingWriter(var1);
   }

   public boolean parseArgs(String[] var1, Main var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null) {
            String var4 = var1[var3].toLowerCase();
            if (var4.equals("-xprint")) {
               this.whatToPrint = 0;
               var1[var3] = null;
               if (var3 + 1 < var1.length) {
                  if (var1[var3 + 1].equalsIgnoreCase("idl")) {
                     ++var3;
                     var1[var3] = null;
                     this.whatToPrint = 1;
                  } else if (var1[var3 + 1].equalsIgnoreCase("both")) {
                     ++var3;
                     var1[var3] = null;
                     this.whatToPrint = 2;
                  }
               }
            } else if (var4.equals("-xglobal")) {
               this.global = true;
               var1[var3] = null;
            } else if (var4.equals("-xqualified")) {
               this.qualified = true;
               var1[var3] = null;
            } else if (var4.equals("-xtrace")) {
               this.trace = true;
               var1[var3] = null;
            } else if (var4.equals("-xvaluemethods")) {
               this.valueMethods = true;
               var1[var3] = null;
            }
         }
      }

      return true;
   }

   public void generate(sun.rmi.rmic.BatchEnvironment var1, ClassDefinition var2, File var3) {
      BatchEnvironment var4 = (BatchEnvironment)var1;
      ContextStack var5 = new ContextStack(var4);
      var5.setTrace(this.trace);
      if (this.valueMethods) {
         var4.setParseNonConforming(true);
      }

      CompoundType var6 = CompoundType.forCompound(var2, var5);
      if (var6 != null) {
         try {
            Type[] var7 = var6.collectMatching(33554432);

            for(int var8 = 0; var8 < var7.length; ++var8) {
               this.out.pln("\n-----------------------------------------------------------\n");
               Type var9 = var7[var8];
               switch (this.whatToPrint) {
                  case 0:
                     var9.println(this.out, this.qualified, false, false);
                     break;
                  case 1:
                     var9.println(this.out, this.qualified, true, this.global);
                     break;
                  case 2:
                     var9.println(this.out, this.qualified, false, false);
                     var9.println(this.out, this.qualified, true, this.global);
                     break;
                  default:
                     throw new CompilerError("Unknown type!");
               }
            }

            this.out.flush();
         } catch (IOException var10) {
            throw new CompilerError("PrintGenerator caught " + var10);
         }
      }

   }
}
