package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import java.util.Hashtable;

public class Holder implements AuxGen {
   protected Hashtable symbolTable;
   protected SymtabEntry entry;
   protected GenFileStream stream;
   protected String holderClass;
   protected String helperClass;
   protected String holderType;

   public void generate(Hashtable var1, SymtabEntry var2) {
      this.symbolTable = var1;
      this.entry = var2;
      this.init();
      this.openStream();
      if (this.stream != null) {
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
      }
   }

   protected void init() {
      this.holderClass = this.entry.name() + "Holder";
      this.helperClass = Util.helperName(this.entry, true);
      if (this.entry instanceof ValueBoxEntry) {
         ValueBoxEntry var1 = (ValueBoxEntry)this.entry;
         TypedefEntry var2 = ((InterfaceState)var1.state().elementAt(0)).entry;
         SymtabEntry var3 = var2.type();
         this.holderType = Util.javaName(var3);
      } else {
         this.holderType = Util.javaName(this.entry);
      }

   }

   protected void openStream() {
      this.stream = Util.stream(this.entry, "Holder.java");
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.entry, (short)3);
      Util.writeProlog(this.stream, this.stream.name());
      if (this.entry.comment() != null) {
         this.entry.comment().generate("", this.stream);
      }

      this.stream.println("public final class " + this.holderClass + " implements org.omg.CORBA.portable.Streamable");
      this.stream.println('{');
   }

   protected void writeBody() {
      if (this.entry instanceof ValueBoxEntry) {
         this.stream.println("  public " + this.holderType + " value;");
      } else {
         Util.writeInitializer("  public ", "value", "", this.entry, this.stream);
      }

      this.stream.println();
      this.writeCtors();
      this.writeRead();
      this.writeWrite();
      this.writeType();
   }

   protected void writeClosing() {
      this.stream.println('}');
   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void writeCtors() {
      this.stream.println("  public " + this.holderClass + " ()");
      this.stream.println("  {");
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  public " + this.holderClass + " (" + this.holderType + " initialValue)");
      this.stream.println("  {");
      this.stream.println("    value = initialValue;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeRead() {
      this.stream.println("  public void _read (org.omg.CORBA.portable.InputStream i)");
      this.stream.println("  {");
      if (this.entry instanceof ValueBoxEntry) {
         TypedefEntry var1 = ((InterfaceState)((ValueBoxEntry)this.entry).state().elementAt(0)).entry;
         SymtabEntry var2 = var1.type();
         if (var2 instanceof StringEntry) {
            this.stream.println("    value = i.read_string ();");
         } else if (var2 instanceof PrimitiveEntry) {
            this.stream.println("    value = " + this.helperClass + ".read (i).value;");
         } else {
            this.stream.println("    value = " + this.helperClass + ".read (i);");
         }
      } else {
         this.stream.println("    value = " + this.helperClass + ".read (i);");
      }

      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeWrite() {
      this.stream.println("  public void _write (org.omg.CORBA.portable.OutputStream o)");
      this.stream.println("  {");
      if (this.entry instanceof ValueBoxEntry) {
         TypedefEntry var1 = ((InterfaceState)((ValueBoxEntry)this.entry).state().elementAt(0)).entry;
         SymtabEntry var2 = var1.type();
         if (var2 instanceof StringEntry) {
            this.stream.println("    o.write_string (value);");
         } else if (var2 instanceof PrimitiveEntry) {
            String var3 = this.entry.name();
            this.stream.println("    " + var3 + " vb = new " + var3 + " (value);");
            this.stream.println("    " + this.helperClass + ".write (o, vb);");
         } else {
            this.stream.println("    " + this.helperClass + ".write (o, value);");
         }
      } else {
         this.stream.println("    " + this.helperClass + ".write (o, value);");
      }

      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeType() {
      this.stream.println("  public org.omg.CORBA.TypeCode _type ()");
      this.stream.println("  {");
      this.stream.println("    return " + this.helperClass + ".type ();");
      this.stream.println("  }");
      this.stream.println();
   }
}
