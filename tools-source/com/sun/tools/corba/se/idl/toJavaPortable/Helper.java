package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import java.util.Hashtable;

public class Helper implements AuxGen {
   protected Hashtable symbolTable;
   protected SymtabEntry entry;
   protected GenFileStream stream;
   protected String helperClass;
   protected String helperType;

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
      this.helperClass = this.entry.name() + "Helper";
      if (this.entry instanceof ValueBoxEntry) {
         ValueBoxEntry var1 = (ValueBoxEntry)this.entry;
         TypedefEntry var2 = ((InterfaceState)var1.state().elementAt(0)).entry;
         SymtabEntry var3 = var2.type();
         if (var3 instanceof PrimitiveEntry) {
            this.helperType = Util.javaName(this.entry);
         } else {
            this.helperType = Util.javaName(var3);
         }
      } else {
         this.helperType = Util.javaName(this.entry);
      }

   }

   protected void openStream() {
      this.stream = Util.stream(this.entry, "Helper.java");
   }

   protected void writeHeading() {
      Util.writePackage(this.stream, this.entry, (short)2);
      Util.writeProlog(this.stream, this.stream.name());
      if (this.entry.comment() != null) {
         this.entry.comment().generate("", this.stream);
      }

      this.stream.print("public final class " + this.helperClass);
      if (this.entry instanceof ValueEntry) {
         this.stream.println(" implements org.omg.CORBA.portable.ValueHelper");
      } else {
         this.stream.println();
      }

      this.stream.println('{');
   }

   protected void writeBody() {
      this.writeInstVars();
      this.writeCtors();
      this.writeInsert();
      this.writeExtract();
      this.writeType();
      this.writeID();
      this.writeRead();
      this.writeWrite();
      if (this.entry instanceof InterfaceEntry && !(this.entry instanceof ValueEntry)) {
         this.writeNarrow();
         this.writeUncheckedNarrow();
      }

      this.writeHelperInterface();
      if (this.entry instanceof ValueEntry) {
         this.writeValueHelperInterface();
      }

   }

   protected void writeHelperInterface() {
   }

   protected void writeValueHelperInterface() {
      this.writeGetID();
      this.writeGetType();
      this.writeGetInstance();
      this.writeGetClass();
      this.writeGetSafeBaseIds();
   }

   protected void writeClosing() {
      this.stream.println('}');
   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void writeInstVars() {
      this.stream.println("  private static String  _id = \"" + Util.stripLeadingUnderscoresFromID(this.entry.repositoryID().ID()) + "\";");
      if (this.entry instanceof ValueEntry) {
         this.stream.println();
         this.stream.println("  private static " + this.helperClass + " helper = new " + this.helperClass + " ();");
         this.stream.println();
         this.stream.println("  private static String[] _truncatable_ids = {");
         this.stream.print("    _id");

         ValueEntry var2;
         for(ValueEntry var1 = (ValueEntry)this.entry; var1.isSafe(); var1 = var2) {
            this.stream.println(",");
            var2 = (ValueEntry)var1.derivedFrom().elementAt(0);
            this.stream.print("    \"" + Util.stripLeadingUnderscoresFromID(var2.repositoryID().ID()) + "\"");
         }

         this.stream.println("   };");
      }

      this.stream.println();
   }

   protected void writeCtors() {
      this.stream.println("  public " + this.helperClass + "()");
      this.stream.println("  {");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeInsert() {
      this.stream.println("  public static void insert (org.omg.CORBA.Any a, " + this.helperType + " that)");
      this.stream.println("  {");
      this.stream.println("    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();");
      this.stream.println("    a.type (type ());");
      this.stream.println("    write (out, that);");
      this.stream.println("    a.read_value (out.create_input_stream (), type ());");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeExtract() {
      this.stream.println("  public static " + this.helperType + " extract (org.omg.CORBA.Any a)");
      this.stream.println("  {");
      this.stream.println("    return read (a.create_input_stream ());");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeType() {
      boolean var1 = this.entry instanceof ValueEntry || this.entry instanceof ValueBoxEntry || this.entry instanceof StructEntry;
      this.stream.println("  private static org.omg.CORBA.TypeCode __typeCode = null;");
      if (var1) {
         this.stream.println("  private static boolean __active = false;");
      }

      this.stream.println("  synchronized public static org.omg.CORBA.TypeCode type ()");
      this.stream.println("  {");
      this.stream.println("    if (__typeCode == null)");
      this.stream.println("    {");
      if (var1) {
         this.stream.println("      synchronized (org.omg.CORBA.TypeCode.class)");
         this.stream.println("      {");
         this.stream.println("        if (__typeCode == null)");
         this.stream.println("        {");
         this.stream.println("          if (__active)");
         this.stream.println("          {");
         this.stream.println("            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );");
         this.stream.println("          }");
         this.stream.println("          __active = true;");
         ((JavaGenerator)this.entry.generator()).helperType(0, "          ", new TCOffsets(), "__typeCode", this.entry, this.stream);
      } else {
         ((JavaGenerator)this.entry.generator()).helperType(0, "      ", new TCOffsets(), "__typeCode", this.entry, this.stream);
      }

      if (var1) {
         this.stream.println("          __active = false;");
         this.stream.println("        }");
         this.stream.println("      }");
      }

      this.stream.println("    }");
      this.stream.println("    return __typeCode;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeID() {
      this.stream.println("  public static String id ()");
      this.stream.println("  {");
      this.stream.println("    return _id;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeRead() {
      boolean var1 = false;
      if (this.entry instanceof InterfaceEntry) {
         InterfaceEntry var2 = (InterfaceEntry)this.entry;
         var1 = var2.isLocal() | var2.isLocalServant();
      }

      this.stream.println("  public static " + this.helperType + " read (org.omg.CORBA.portable.InputStream istream)");
      this.stream.println("  {");
      if (!var1) {
         ((JavaGenerator)this.entry.generator()).helperRead(this.helperType, this.entry, this.stream);
      } else {
         this.stream.println("      throw new org.omg.CORBA.MARSHAL ();");
      }

      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeWrite() {
      boolean var1 = false;
      if (this.entry instanceof InterfaceEntry) {
         InterfaceEntry var2 = (InterfaceEntry)this.entry;
         var1 = var2.isLocal() | var2.isLocalServant();
      }

      this.stream.println("  public static void write (org.omg.CORBA.portable.OutputStream ostream, " + this.helperType + " value)");
      this.stream.println("  {");
      if (!var1) {
         ((JavaGenerator)this.entry.generator()).helperWrite(this.entry, this.stream);
      } else {
         this.stream.println("      throw new org.omg.CORBA.MARSHAL ();");
      }

      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeNarrow() {
      this.writeRemoteNarrow();
      this.stream.println();
   }

   protected void writeRemoteNarrow() {
      InterfaceEntry var1 = (InterfaceEntry)this.entry;
      if (var1.isLocal()) {
         this.writeRemoteNarrowForLocal(false);
      } else if (var1.isAbstract()) {
         this.writeRemoteNarrowForAbstract(false);
      } else {
         for(int var2 = 0; var2 < var1.derivedFrom().size(); ++var2) {
            SymtabEntry var3 = (SymtabEntry)var1.derivedFrom().elementAt(var2);
            if (((InterfaceEntry)var3).isAbstract()) {
               this.writeRemoteNarrowForAbstract(true);
               break;
            }
         }

         this.stream.println("  public static " + this.helperType + " narrow (org.omg.CORBA.Object obj)");
         this.stream.println("  {");
         this.stream.println("    if (obj == null)");
         this.stream.println("      return null;");
         this.stream.println("    else if (obj instanceof " + this.helperType + ')');
         this.stream.println("      return (" + this.helperType + ")obj;");
         this.stream.println("    else if (!obj._is_a (id ()))");
         this.stream.println("      throw new org.omg.CORBA.BAD_PARAM ();");
         this.stream.println("    else");
         this.stream.println("    {");
         this.stream.println("      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();");
         String var4 = this.stubName((InterfaceEntry)this.entry);
         this.stream.println("      " + var4 + " stub = new " + var4 + " ();");
         this.stream.println("      stub._set_delegate(delegate);");
         this.stream.println("      return stub;");
         this.stream.println("    }");
         this.stream.println("  }");
      }
   }

   private void writeRemoteNarrowForLocal(boolean var1) {
      this.stream.println("  public static " + this.helperType + " narrow (org.omg.CORBA.Object obj)");
      this.stream.println("  {");
      this.stream.println("    if (obj == null)");
      this.stream.println("      return null;");
      this.stream.println("    else if (obj instanceof " + this.helperType + ')');
      this.stream.println("      return (" + this.helperType + ")obj;");
      this.stream.println("    else");
      this.stream.println("      throw new org.omg.CORBA.BAD_PARAM ();");
      this.stream.println("  }");
   }

   private void writeRemoteNarrowForAbstract(boolean var1) {
      this.stream.print("  public static " + this.helperType + " narrow (java.lang.Object obj)");
      this.stream.println("  {");
      this.stream.println("    if (obj == null)");
      this.stream.println("      return null;");
      if (var1) {
         this.stream.println("    else if (obj instanceof org.omg.CORBA.Object)");
         this.stream.println("      return narrow ((org.omg.CORBA.Object) obj);");
      } else {
         this.stream.println("    else if (obj instanceof " + this.helperType + ')');
         this.stream.println("      return (" + this.helperType + ")obj;");
      }

      if (!var1) {
         String var2 = this.stubName((InterfaceEntry)this.entry);
         this.stream.println("    else if ((obj instanceof org.omg.CORBA.portable.ObjectImpl) &&");
         this.stream.println("             (((org.omg.CORBA.Object)obj)._is_a (id ()))) {");
         this.stream.println("      org.omg.CORBA.portable.ObjectImpl impl = (org.omg.CORBA.portable.ObjectImpl)obj ;");
         this.stream.println("      org.omg.CORBA.portable.Delegate delegate = impl._get_delegate() ;");
         this.stream.println("      " + var2 + " stub = new " + var2 + " ();");
         this.stream.println("      stub._set_delegate(delegate);");
         this.stream.println("      return stub;");
         this.stream.println("    }");
      }

      this.stream.println("    throw new org.omg.CORBA.BAD_PARAM ();");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeUncheckedNarrow() {
      this.writeUncheckedRemoteNarrow();
      this.stream.println();
   }

   protected void writeUncheckedRemoteNarrow() {
      InterfaceEntry var1 = (InterfaceEntry)this.entry;
      if (var1.isLocal()) {
         this.writeRemoteUncheckedNarrowForLocal(false);
      } else if (var1.isAbstract()) {
         this.writeRemoteUncheckedNarrowForAbstract(false);
      } else {
         for(int var2 = 0; var2 < var1.derivedFrom().size(); ++var2) {
            SymtabEntry var3 = (SymtabEntry)var1.derivedFrom().elementAt(var2);
            if (((InterfaceEntry)var3).isAbstract()) {
               this.writeRemoteUncheckedNarrowForAbstract(true);
               break;
            }
         }

         this.stream.println("  public static " + this.helperType + " unchecked_narrow (org.omg.CORBA.Object obj)");
         this.stream.println("  {");
         this.stream.println("    if (obj == null)");
         this.stream.println("      return null;");
         this.stream.println("    else if (obj instanceof " + this.helperType + ')');
         this.stream.println("      return (" + this.helperType + ")obj;");
         this.stream.println("    else");
         this.stream.println("    {");
         this.stream.println("      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();");
         String var4 = this.stubName((InterfaceEntry)this.entry);
         this.stream.println("      " + var4 + " stub = new " + var4 + " ();");
         this.stream.println("      stub._set_delegate(delegate);");
         this.stream.println("      return stub;");
         this.stream.println("    }");
         this.stream.println("  }");
      }
   }

   private void writeRemoteUncheckedNarrowForLocal(boolean var1) {
      this.stream.println("  public static " + this.helperType + " unchecked_narrow (org.omg.CORBA.Object obj)");
      this.stream.println("  {");
      this.stream.println("    if (obj == null)");
      this.stream.println("      return null;");
      this.stream.println("    else if (obj instanceof " + this.helperType + ')');
      this.stream.println("      return (" + this.helperType + ")obj;");
      this.stream.println("    else");
      this.stream.println("      throw new org.omg.CORBA.BAD_PARAM ();");
      this.stream.println("  }");
   }

   private void writeRemoteUncheckedNarrowForAbstract(boolean var1) {
      this.stream.print("  public static " + this.helperType + " unchecked_narrow (java.lang.Object obj)");
      this.stream.println("  {");
      this.stream.println("    if (obj == null)");
      this.stream.println("      return null;");
      if (var1) {
         this.stream.println("    else if (obj instanceof org.omg.CORBA.Object)");
         this.stream.println("      return unchecked_narrow ((org.omg.CORBA.Object) obj);");
      } else {
         this.stream.println("    else if (obj instanceof " + this.helperType + ')');
         this.stream.println("      return (" + this.helperType + ")obj;");
      }

      if (!var1) {
         String var2 = this.stubName((InterfaceEntry)this.entry);
         this.stream.println("    else if (obj instanceof org.omg.CORBA.portable.ObjectImpl) {");
         this.stream.println("      org.omg.CORBA.portable.ObjectImpl impl = (org.omg.CORBA.portable.ObjectImpl)obj ;");
         this.stream.println("      org.omg.CORBA.portable.Delegate delegate = impl._get_delegate() ;");
         this.stream.println("      " + var2 + " stub = new " + var2 + " ();");
         this.stream.println("      stub._set_delegate(delegate);");
         this.stream.println("      return stub;");
         this.stream.println("    }");
      }

      this.stream.println("    throw new org.omg.CORBA.BAD_PARAM ();");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeGetID() {
      if (Util.IDLEntity(this.entry)) {
         this.stream.println("  public String get_id ()");
         this.stream.println("  {");
         this.stream.println("    return _id;");
         this.stream.println("  }");
         this.stream.println();
      }
   }

   protected void writeGetType() {
      if (Util.IDLEntity(this.entry)) {
         this.stream.println("  public org.omg.CORBA.TypeCode get_type ()");
         this.stream.println("  {");
         this.stream.println("    return type ();");
         this.stream.println("  }");
         this.stream.println();
      }
   }

   protected void writeGetClass() {
      this.stream.println("  public Class get_class ()");
      this.stream.println("  {");
      this.stream.println("    return " + this.helperType + ".class;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeGetInstance() {
      this.stream.println("  public static org.omg.CORBA.portable.ValueHelper get_instance ()");
      this.stream.println("  {");
      this.stream.println("    return helper;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected void writeGetSafeBaseIds() {
      this.stream.println("  public String[] get_truncatable_base_ids ()");
      this.stream.println("  {");
      this.stream.println("    return _truncatable_ids;");
      this.stream.println("  }");
      this.stream.println();
   }

   protected String stubName(InterfaceEntry var1) {
      String var2;
      if (var1.container().name().equals("")) {
         var2 = '_' + var1.name() + "Stub";
      } else {
         var2 = Util.containerFullName(var1.container()) + "._" + var1.name() + "Stub";
      }

      return var2.replace('/', '.');
   }
}
