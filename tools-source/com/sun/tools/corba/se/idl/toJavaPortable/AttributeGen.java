package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.AttributeEntry;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class AttributeGen extends MethodGen implements com.sun.tools.corba.se.idl.AttributeGen {
   private SymtabEntry realType = null;

   private boolean unique(InterfaceEntry var1, String var2) {
      Enumeration var3 = var1.methods().elements();

      SymtabEntry var4;
      do {
         if (!var3.hasMoreElements()) {
            Enumeration var5 = var1.derivedFrom().elements();

            do {
               if (!var5.hasMoreElements()) {
                  return true;
               }
            } while(this.unique((InterfaceEntry)var5.nextElement(), var2));

            return false;
         }

         var4 = (SymtabEntry)var3.nextElement();
      } while(!var2.equals(var4.name()));

      return false;
   }

   public void generate(Hashtable var1, AttributeEntry var2, PrintWriter var3) {
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

   protected void stub(String var1, boolean var2, Hashtable var3, MethodEntry var4, PrintWriter var5, int var6) {
      AttributeEntry var7 = (AttributeEntry)var4;
      super.stub(var1, var2, var3, var7, var5, var6);
      if (!var7.readOnly()) {
         this.setupForSetMethod();
         super.stub(var1, var2, var3, var7, var5, var6 + 1);
         this.clear();
      }

   }

   protected void skeleton(Hashtable var1, MethodEntry var2, PrintWriter var3, int var4) {
      AttributeEntry var5 = (AttributeEntry)var2;
      super.skeleton(var1, var5, var3, var4);
      if (!var5.readOnly()) {
         this.setupForSetMethod();
         super.skeleton(var1, var5, var3, var4 + 1);
         this.clear();
      }

   }

   protected void dispatchSkeleton(Hashtable var1, MethodEntry var2, PrintWriter var3, int var4) {
      AttributeEntry var5 = (AttributeEntry)var2;
      super.dispatchSkeleton(var1, var5, var3, var4);
      if (!var5.readOnly()) {
         this.setupForSetMethod();
         super.dispatchSkeleton(var1, var2, var3, var4 + 1);
         this.clear();
      }

   }

   protected void setupForSetMethod() {
      ParameterEntry var1 = Compile.compiler.factory.parameterEntry();
      var1.type(this.m.type());
      var1.name("new" + Util.capitalize(this.m.name()));
      this.m.parameters().addElement(var1);
      this.realType = this.m.type();
      this.m.type((SymtabEntry)null);
   }

   protected void clear() {
      this.m.parameters().removeAllElements();
      this.m.type(this.realType);
   }
}
