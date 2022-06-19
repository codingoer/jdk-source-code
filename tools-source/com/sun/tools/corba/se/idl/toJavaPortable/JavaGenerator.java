package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;

public interface JavaGenerator {
   int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6);

   void helperRead(String var1, SymtabEntry var2, PrintWriter var3);

   void helperWrite(SymtabEntry var1, PrintWriter var2);

   int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5);

   int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5);

   int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6);
}
