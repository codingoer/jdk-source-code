package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class StructGen implements com.sun.tools.corba.se.idl.StructGen, JavaGenerator {
   protected Hashtable symbolTable = null;
   protected StructEntry s = null;
   protected PrintWriter stream = null;
   protected boolean thisIsReallyAnException = false;
   private boolean[] memberIsPrimitive;
   private boolean[] memberIsInterface;
   private boolean[] memberIsTypedef;

   public StructGen() {
   }

   protected StructGen(boolean var1) {
      this.thisIsReallyAnException = var1;
   }

   public void generate(Hashtable var1, StructEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.s = var2;
      this.openStream();
      if (this.stream != null) {
         this.generateHelper();
         this.generateHolder();
         this.writeHeading();
         this.writeBody();
         this.writeClosing();
         this.closeStream();
         this.generateContainedTypes();
      }
   }

   protected void init() {
   }

   protected void openStream() {
      this.stream = Util.stream(this.s, ".java");
   }

   protected void generateHelper() {
      ((Factories)Compile.compiler.factories()).helper().generate(this.symbolTable, this.s);
   }

   protected void generateHolder() {
      ((Factories)Compile.compiler.factories()).holder().generate(this.symbolTable, this.s);
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.s);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      if (this.s.comment() != null) {
         this.s.comment().generate("", this.stream);
      }

      this.stream.print("public final class " + this.s.name());
      if (this.thisIsReallyAnException) {
         this.stream.print(" extends org.omg.CORBA.UserException");
      } else {
         this.stream.print(" implements org.omg.CORBA.portable.IDLEntity");
      }

      this.stream.println();
      this.stream.println("{");
   }

   protected void writeBody() {
      this.writeMembers();
      this.writeCtors();
   }

   protected void writeClosing() {
      this.stream.println("} // class " + this.s.name());
   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void generateContainedTypes() {
      Enumeration var1 = this.s.contained().elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (!(var2 instanceof SequenceEntry)) {
            var2.generate(this.symbolTable, this.stream);
         }
      }

   }

   protected void writeMembers() {
      int var1 = this.s.members().size();
      this.memberIsPrimitive = new boolean[var1];
      this.memberIsInterface = new boolean[var1];
      this.memberIsTypedef = new boolean[var1];

      for(int var2 = 0; var2 < this.s.members().size(); ++var2) {
         SymtabEntry var3 = (SymtabEntry)this.s.members().elementAt(var2);
         this.memberIsPrimitive[var2] = var3.type() instanceof PrimitiveEntry;
         this.memberIsInterface[var2] = var3.type() instanceof InterfaceEntry;
         this.memberIsTypedef[var2] = var3.type() instanceof TypedefEntry;
         Util.fillInfo(var3);
         if (var3.comment() != null) {
            var3.comment().generate("  ", this.stream);
         }

         Util.writeInitializer("  public ", var3.name(), "", var3, this.stream);
      }

   }

   protected void writeCtors() {
      this.stream.println();
      this.stream.println("  public " + this.s.name() + " ()");
      this.stream.println("  {");
      if (this.thisIsReallyAnException) {
         this.stream.println("    super(" + this.s.name() + "Helper.id());");
      }

      this.stream.println("  } // ctor");
      this.writeInitializationCtor(true);
      if (this.thisIsReallyAnException) {
         this.writeInitializationCtor(false);
      }

   }

   private void writeInitializationCtor(boolean var1) {
      if (!var1 || this.s.members().size() > 0) {
         this.stream.println();
         this.stream.print("  public " + this.s.name() + " (");
         boolean var2 = true;
         if (!var1) {
            this.stream.print("String $reason");
            var2 = false;
         }

         int var3;
         SymtabEntry var4;
         for(var3 = 0; var3 < this.s.members().size(); ++var3) {
            var4 = (SymtabEntry)this.s.members().elementAt(var3);
            if (var2) {
               var2 = false;
            } else {
               this.stream.print(", ");
            }

            this.stream.print(Util.javaName(var4) + " _" + var4.name());
         }

         this.stream.println(")");
         this.stream.println("  {");
         if (this.thisIsReallyAnException) {
            if (var1) {
               this.stream.println("    super(" + this.s.name() + "Helper.id());");
            } else {
               this.stream.println("    super(" + this.s.name() + "Helper.id() + \"  \" + $reason);");
            }
         }

         for(var3 = 0; var3 < this.s.members().size(); ++var3) {
            var4 = (SymtabEntry)this.s.members().elementAt(var3);
            this.stream.println("    " + var4.name() + " = _" + var4.name() + ";");
         }

         this.stream.println("  } // ctor");
      }

      this.stream.println();
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      TCOffsets var7 = new TCOffsets();
      var7.set(var5);
      int var8 = var7.currentOffset();
      StructEntry var9 = (StructEntry)var5;
      String var10 = "_members" + var1++;
      var6.println(var2 + "org.omg.CORBA.StructMember[] " + var10 + " = new org.omg.CORBA.StructMember [" + var9.members().size() + "];");
      String var11 = "_tcOf" + var10;
      var6.println(var2 + "org.omg.CORBA.TypeCode " + var11 + " = null;");

      for(int var12 = 0; var12 < var9.members().size(); ++var12) {
         TypedefEntry var13 = (TypedefEntry)var9.members().elementAt(var12);
         String var14 = var13.name();
         var1 = ((JavaGenerator)var13.generator()).type(var1, var2, var7, var11, var13, var6);
         var6.println(var2 + var10 + '[' + var12 + "] = new org.omg.CORBA.StructMember (");
         var6.println(var2 + "  \"" + Util.stripLeadingUnderscores(var14) + "\",");
         var6.println(var2 + "  " + var11 + ',');
         var6.println(var2 + "  null);");
         int var15 = var7.currentOffset();
         var7 = new TCOffsets();
         var7.set(var5);
         var7.bumpCurrentOffset(var15 - var8);
      }

      var3.bumpCurrentOffset(var7.currentOffset());
      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_" + (this.thisIsReallyAnException ? "exception" : "struct") + "_tc (" + Util.helperName(var9, true) + ".id (), \"" + Util.stripLeadingUnderscores(var5.name()) + "\", " + var10 + ");");
      return var1;
   }

   public int type(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      var6.println(var2 + var4 + " = " + Util.helperName(var5, true) + ".type ();");
      return var1;
   }

   public void helperRead(String var1, SymtabEntry var2, PrintWriter var3) {
      var3.println("    " + var1 + " value = new " + var1 + " ();");
      this.read(0, "    ", "value", var2, var3);
      var3.println("    return value;");
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      if (this.thisIsReallyAnException) {
         var5.println(var2 + "// read and discard the repository ID");
         var5.println(var2 + "istream.read_string ();");
      }

      Enumeration var6 = ((StructEntry)var4).members().elements();

      while(true) {
         while(true) {
            while(var6.hasMoreElements()) {
               TypedefEntry var7 = (TypedefEntry)var6.nextElement();
               SymtabEntry var8 = var7.type();
               if (var7.arrayInfo().isEmpty() && !(var8 instanceof SequenceEntry) && !(var8 instanceof PrimitiveEntry) && !(var8 instanceof StringEntry) && !(var8 instanceof TypedefEntry)) {
                  if (var8 instanceof ValueBoxEntry) {
                     Vector var9 = ((ValueBoxEntry)var8).state();
                     TypedefEntry var10 = ((InterfaceState)var9.elementAt(0)).entry;
                     SymtabEntry var11 = var10.type();
                     String var12 = null;
                     String var13 = null;
                     if (!(var11 instanceof SequenceEntry) && !(var11 instanceof StringEntry) && var10.arrayInfo().isEmpty()) {
                        var12 = Util.javaName(var8);
                        var13 = Util.helperName(var8, true);
                     } else {
                        var12 = Util.javaName(var11);
                        var13 = Util.helperName(var8, true);
                     }

                     if (Util.corbaLevel(2.4F, 99.0F)) {
                        var5.println(var2 + var3 + '.' + var7.name() + " = (" + var12 + ") " + var13 + ".read (istream);");
                     } else {
                        var5.println(var2 + var3 + '.' + var7.name() + " = (" + var12 + ") ((org.omg.CORBA_2_3.portable.InputStream)istream).read_value (" + var13 + ".get_instance ());");
                     }
                  } else if (var8 instanceof ValueEntry && !Util.corbaLevel(2.4F, 99.0F)) {
                     var5.println(var2 + var3 + '.' + var7.name() + " = (" + Util.javaName(var8) + ") ((org.omg.CORBA_2_3.portable.InputStream)istream).read_value (" + Util.helperName(var8, false) + ".get_instance ());");
                  } else {
                     var5.println(var2 + var3 + '.' + var7.name() + " = " + Util.helperName(var7.type(), true) + ".read (istream);");
                  }
               } else {
                  var1 = ((JavaGenerator)var7.generator()).read(var1, var2, var3 + '.' + var7.name(), var7, var5);
               }
            }

            return var1;
         }
      }
   }

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      this.write(0, "    ", "value", var1, var2);
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      if (this.thisIsReallyAnException) {
         var5.println(var2 + "// write the repository ID");
         var5.println(var2 + "ostream.write_string (id ());");
      }

      Vector var6 = ((StructEntry)var4).members();

      for(int var7 = 0; var7 < var6.size(); ++var7) {
         TypedefEntry var8 = (TypedefEntry)var6.elementAt(var7);
         SymtabEntry var9 = var8.type();
         if (var8.arrayInfo().isEmpty() && !(var9 instanceof SequenceEntry) && !(var9 instanceof TypedefEntry) && !(var9 instanceof PrimitiveEntry) && !(var9 instanceof StringEntry)) {
            if ((var9 instanceof ValueEntry || var9 instanceof ValueBoxEntry) && !Util.corbaLevel(2.4F, 99.0F)) {
               var5.println(var2 + "((org.omg.CORBA_2_3.portable.OutputStream)ostream).write_value ((java.io.Serializable) " + var3 + '.' + var8.name() + ", " + Util.helperName(var8.type(), true) + ".get_instance ());");
            } else {
               var5.println(var2 + Util.helperName(var8.type(), true) + ".write (ostream, " + var3 + '.' + var8.name() + ");");
            }
         } else {
            var1 = ((JavaGenerator)var8.generator()).write(var1, "    ", var3 + '.' + var8.name(), var8, var5);
         }
      }

      return var1;
   }
}
