package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.ModuleEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class ModuleGen implements com.sun.tools.corba.se.idl.ModuleGen {
   public void generate(Hashtable var1, ModuleEntry var2, PrintWriter var3) {
      String var4 = Util.containerFullName(var2);
      Util.mkdir(var4);
      Enumeration var5 = var2.contained().elements();

      while(var5.hasMoreElements()) {
         SymtabEntry var6 = (SymtabEntry)var5.nextElement();
         if (var6.emit()) {
            var6.generate(var1, var3);
         }
      }

   }
}
