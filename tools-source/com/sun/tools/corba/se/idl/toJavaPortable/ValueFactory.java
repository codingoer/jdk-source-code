package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.util.Hashtable;
import java.util.Vector;

public class ValueFactory implements AuxGen {
   protected Hashtable symbolTable;
   protected SymtabEntry entry;
   protected GenFileStream stream;
   protected String factoryClass;
   protected String factoryType;

   public void generate(Hashtable var1, SymtabEntry var2) {
      this.symbolTable = var1;
      this.entry = var2;
      this.init();
      if (this.hasFactoryMethods()) {
         this.openStream();
         if (this.stream == null) {
            return;
         }

         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }

   }

   protected void init() {
      this.factoryClass = this.entry.name() + "ValueFactory";
      this.factoryType = Util.javaName(this.entry);
   }

   protected boolean hasFactoryMethods() {
      Vector var1 = ((ValueEntry)this.entry).initializers();
      return var1 != null && var1.size() > 0;
   }

   protected void openStream() {
      this.stream = Util.stream(this.entry, "ValueFactory.java");
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.entry, (short)0);
      Util.writeProlog(this.stream, this.stream.name());
      if (this.entry.comment() != null) {
         this.entry.comment().generate("", this.stream);
      }

      this.stream.println("public interface " + this.factoryClass + " extends org.omg.CORBA.portable.ValueFactory");
      this.stream.println('{');
   }

   protected void writeBody() {
      Vector var1 = ((ValueEntry)this.entry).initializers();
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            MethodEntry var3 = (MethodEntry)var1.elementAt(var2);
            var3.valueMethod(true);
            ((MethodGen)var3.generator()).interfaceMethod(this.symbolTable, var3, this.stream);
         }
      }

   }

   protected void writeClosing() {
      this.stream.println('}');
   }

   protected void closeStream() {
      this.stream.close();
   }
}
