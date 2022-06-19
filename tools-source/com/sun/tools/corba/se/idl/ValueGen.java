package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;
import java.util.Hashtable;

public interface ValueGen extends Generator {
   void generate(Hashtable var1, ValueEntry var2, PrintWriter var3);
}
