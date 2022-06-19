package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public interface NativeGen extends Generator {
   void generate(Hashtable var1, NativeEntry var2, PrintWriter var3);
}
