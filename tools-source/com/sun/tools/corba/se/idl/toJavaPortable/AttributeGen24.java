package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.AttributeEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import java.io.PrintWriter;
import java.util.Hashtable;

public class AttributeGen24 extends MethodGenClone24 {
   protected void abstractMethod(Hashtable var1, MethodEntry var2, PrintWriter var3) {
      AttributeEntry var4 = (AttributeEntry)var2;
      super.abstractMethod(var1, var4, var3);
      if (!var4.readOnly()) {
         this.setupForSetMethod();
         super.abstractMethod(var1, var4, var3);
         this.clear();
      }

   }

   protected void interfaceMethod(Hashtable var1, MethodEntry var2, PrintWriter var3) {
      AttributeEntry var4 = (AttributeEntry)var2;
      super.interfaceMethod(var1, var4, var3);
      if (!var4.readOnly()) {
         this.setupForSetMethod();
         super.interfaceMethod(var1, var4, var3);
         this.clear();
      }

   }
}
