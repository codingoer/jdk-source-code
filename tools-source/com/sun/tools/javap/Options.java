package com.sun.tools.javap;

import com.sun.tools.classfile.AccessFlags;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class Options {
   public boolean help;
   public boolean verbose;
   public boolean version;
   public boolean fullVersion;
   public boolean showFlags;
   public boolean showLineAndLocalVariableTables;
   public int showAccess;
   public Set accessOptions = new HashSet();
   public Set details = EnumSet.noneOf(InstructionDetailWriter.Kind.class);
   public boolean showDisassembled;
   public boolean showDescriptors;
   public boolean showAllAttrs;
   public boolean showConstants;
   public boolean sysInfo;
   public boolean showInnerClasses;
   public int indentWidth = 2;
   public int tabColumn = 40;

   public static Options instance(Context var0) {
      Options var1 = (Options)var0.get(Options.class);
      if (var1 == null) {
         var1 = new Options(var0);
      }

      return var1;
   }

   protected Options(Context var1) {
      var1.put(Options.class, this);
   }

   public boolean checkAccess(AccessFlags var1) {
      boolean var2 = var1.is(1);
      boolean var3 = var1.is(4);
      boolean var4 = var1.is(2);
      boolean var5 = !var2 && !var3 && !var4;
      if (this.showAccess == 1 && (var3 || var4 || var5)) {
         return false;
      } else if (this.showAccess == 4 && (var4 || var5)) {
         return false;
      } else {
         return this.showAccess != 0 || !var4;
      }
   }
}
