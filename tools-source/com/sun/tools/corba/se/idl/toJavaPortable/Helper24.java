package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.util.Vector;

public class Helper24 extends Helper {
   protected void writeHeading() {
      Util.writePackage(this.stream, this.entry, (short)2);
      Util.writeProlog(this.stream, this.stream.name());
      if (this.entry.comment() != null) {
         this.entry.comment().generate("", this.stream);
      }

      if (this.entry instanceof ValueBoxEntry) {
         this.stream.print("public final class " + this.helperClass);
         this.stream.println(" implements org.omg.CORBA.portable.BoxedValueHelper");
      } else {
         this.stream.println("abstract public class " + this.helperClass);
      }

      this.stream.println('{');
   }

   protected void writeInstVars() {
      this.stream.println("  private static String  _id = \"" + Util.stripLeadingUnderscoresFromID(this.entry.repositoryID().ID()) + "\";");
      if (this.entry instanceof ValueEntry) {
         this.stream.println();
         if (this.entry instanceof ValueBoxEntry) {
            this.stream.println("  private static " + this.helperClass + " _instance = new " + this.helperClass + " ();");
            this.stream.println();
         }
      }

      this.stream.println();
   }

   protected void writeValueHelperInterface() {
      if (this.entry instanceof ValueBoxEntry) {
         this.writeGetID();
      } else if (this.entry instanceof ValueEntry) {
         this.writeHelperFactories();
      }

   }

   protected void writeHelperFactories() {
      Vector var1 = ((ValueEntry)this.entry).initializers();
      if (var1 != null) {
         this.stream.println();

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            MethodEntry var3 = (MethodEntry)var1.elementAt(var2);
            var3.valueMethod(true);
            ((MethodGen24)var3.generator()).helperFactoryMethod(this.symbolTable, var3, this.entry, this.stream);
         }
      }

   }

   protected void writeCtors() {
      if (this.entry instanceof ValueBoxEntry) {
         this.stream.println("  public " + this.helperClass + "()");
         this.stream.println("  {");
         this.stream.println("  }");
         this.stream.println();
      }

   }
}
