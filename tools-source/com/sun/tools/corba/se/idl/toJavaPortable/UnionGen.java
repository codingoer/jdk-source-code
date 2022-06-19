package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.EnumEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionBranch;
import com.sun.tools.corba.se.idl.UnionEntry;
import com.sun.tools.corba.se.idl.constExpr.EvaluationException;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class UnionGen implements com.sun.tools.corba.se.idl.UnionGen, JavaGenerator {
   protected Hashtable symbolTable = null;
   protected UnionEntry u = null;
   protected PrintWriter stream = null;
   protected SymtabEntry utype = null;
   protected boolean unionIsEnum;
   protected String typePackage = "";

   public void generate(Hashtable var1, UnionEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.u = var2;
      this.init();
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
      this.utype = Util.typeOf(this.u.type());
      this.unionIsEnum = this.utype instanceof EnumEntry;
   }

   protected void openStream() {
      this.stream = Util.stream(this.u, ".java");
   }

   protected void generateHelper() {
      ((Factories)Compile.compiler.factories()).helper().generate(this.symbolTable, this.u);
   }

   protected void generateHolder() {
      ((Factories)Compile.compiler.factories()).holder().generate(this.symbolTable, this.u);
   }

   protected void writeHeading() {
      if (this.unionIsEnum) {
         this.typePackage = Util.javaQualifiedName(this.utype) + '.';
      } else {
         this.typePackage = "";
      }

      Util.writePackage(this.stream, this.u);
      Util.writeProlog(this.stream, ((GenFileStream)this.stream).name());
      String var1 = this.u.name();
      this.stream.println("public final class " + this.u.name() + " implements org.omg.CORBA.portable.IDLEntity");
      this.stream.println("{");
   }

   protected void writeBody() {
      int var1 = this.u.branches().size() + 1;
      Enumeration var2 = this.u.branches().elements();

      int var3;
      UnionBranch var4;
      for(var3 = 0; var2.hasMoreElements(); ++var3) {
         var4 = (UnionBranch)var2.nextElement();
         Util.fillInfo(var4.typedef);
         this.stream.println("  private " + Util.javaName(var4.typedef) + " ___" + var4.typedef.name() + ";");
      }

      this.stream.println("  private " + Util.javaName(this.utype) + " __discriminator;");
      this.stream.println("  private boolean __uninitialized = true;");
      this.stream.println();
      this.stream.println("  public " + this.u.name() + " ()");
      this.stream.println("  {");
      this.stream.println("  }");
      this.stream.println();
      this.stream.println("  public " + Util.javaName(this.utype) + " " + this.safeName(this.u, "discriminator") + " ()");
      this.stream.println("  {");
      this.stream.println("    if (__uninitialized)");
      this.stream.println("      throw new org.omg.CORBA.BAD_OPERATION ();");
      this.stream.println("    return __discriminator;");
      this.stream.println("  }");
      var2 = this.u.branches().elements();
      var3 = 0;

      while(var2.hasMoreElements()) {
         var4 = (UnionBranch)var2.nextElement();
         this.writeBranchMethods(this.stream, this.u, var4, var3++);
      }

      if (this.u.defaultBranch() == null && !this.coversAll(this.u)) {
         this.stream.println();
         this.stream.println("  public void _default ()");
         this.stream.println("  {");
         this.stream.println("    __discriminator = " + this.defaultDiscriminator(this.u) + ';');
         this.stream.println("    __uninitialized = false;");
         this.stream.println("  }");
         this.stream.println();
         this.stream.println("  public void _default (" + Util.javaName(this.utype) + " discriminator)");
         this.stream.println("  {");
         this.stream.println("    verifyDefault( discriminator ) ;");
         this.stream.println("    __discriminator = discriminator ;");
         this.stream.println("    __uninitialized = false;");
         this.stream.println("  }");
         this.writeVerifyDefault();
      }

      this.stream.println();
   }

   protected void writeClosing() {
      this.stream.println("} // class " + this.u.name());
   }

   protected void closeStream() {
      this.stream.close();
   }

   protected void generateContainedTypes() {
      Enumeration var1 = this.u.contained().elements();

      while(var1.hasMoreElements()) {
         SymtabEntry var2 = (SymtabEntry)var1.nextElement();
         if (!(var2 instanceof SequenceEntry)) {
            var2.generate(this.symbolTable, this.stream);
         }
      }

   }

   private void writeVerifyDefault() {
      Vector var1 = this.vectorizeLabels(this.u.branches(), true);
      if (Util.javaName(this.utype).equals("boolean")) {
         this.stream.println("");
         this.stream.println("  private void verifyDefault (boolean discriminator)");
         this.stream.println("  {");
         if (var1.contains("true")) {
            this.stream.println("    if ( discriminator )");
         } else {
            this.stream.println("    if ( !discriminator )");
         }

         this.stream.println("        throw new org.omg.CORBA.BAD_OPERATION();");
         this.stream.println("  }");
      } else {
         this.stream.println("");
         this.stream.println("  private void verifyDefault( " + Util.javaName(this.utype) + " value )");
         this.stream.println("  {");
         if (this.unionIsEnum) {
            this.stream.println("    switch (value.value()) {");
         } else {
            this.stream.println("    switch (value) {");
         }

         Enumeration var2 = var1.elements();

         while(var2.hasMoreElements()) {
            String var3 = (String)((String)var2.nextElement());
            this.stream.println("      case " + var3 + ":");
         }

         this.stream.println("        throw new org.omg.CORBA.BAD_OPERATION() ;");
         this.stream.println("");
         this.stream.println("      default:");
         this.stream.println("        return;");
         this.stream.println("    }");
         this.stream.println("  }");
      }
   }

   private String defaultDiscriminator(UnionEntry var1) {
      Vector var2 = this.vectorizeLabels(var1.branches(), false);
      String var3 = null;
      SymtabEntry var4 = Util.typeOf(var1.type());
      if (var4 instanceof PrimitiveEntry && var4.name().equals("boolean")) {
         if (var2.contains("true")) {
            var3 = "false";
         } else {
            var3 = "true";
         }
      } else {
         int var5;
         if (var4.name().equals("char")) {
            var5 = 0;
            String var6 = "'\\u0000'";

            while(var5 != 65535 && var2.contains(var6)) {
               ++var5;
               if (var5 / 16 == 0) {
                  var6 = "'\\u000" + var5 + "'";
               } else if (var5 / 256 == 0) {
                  var6 = "\\u00" + var5 + "'";
               } else if (var5 / 4096 == 0) {
                  var6 = "\\u0" + var5 + "'";
               } else {
                  var6 = "\\u" + var5 + "'";
               }
            }

            var3 = var6;
         } else if (var4 instanceof EnumEntry) {
            Enumeration var8 = var2.elements();
            EnumEntry var10 = (EnumEntry)var4;
            Vector var7 = (Vector)var10.elements().clone();

            while(var8.hasMoreElements()) {
               var7.removeElement(var8.nextElement());
            }

            if (var7.size() == 0) {
               var3 = this.typePackage + (String)var10.elements().lastElement();
            } else {
               var3 = this.typePackage + (String)var7.firstElement();
            }
         } else {
            short var9;
            if (var4.name().equals("octet")) {
               for(var9 = -128; var9 != 127 && var2.contains(Integer.toString(var9)); ++var9) {
               }

               var3 = Integer.toString(var9);
            } else if (var4.name().equals("short")) {
               for(var9 = Short.MIN_VALUE; var9 != 32767 && var2.contains(Integer.toString(var9)); ++var9) {
               }

               var3 = Integer.toString(var9);
            } else if (var4.name().equals("long")) {
               for(var5 = Integer.MIN_VALUE; var5 != Integer.MAX_VALUE && var2.contains(Integer.toString(var5)); ++var5) {
               }

               var3 = Integer.toString(var5);
            } else {
               long var11;
               if (var4.name().equals("long long")) {
                  for(var11 = Long.MIN_VALUE; var11 != Long.MAX_VALUE && var2.contains(Long.toString(var11)); ++var11) {
                  }

                  var3 = Long.toString(var11);
               } else if (var4.name().equals("unsigned short")) {
                  for(var9 = 0; var9 != 32767 && var2.contains(Integer.toString(var9)); ++var9) {
                  }

                  var3 = Integer.toString(var9);
               } else if (var4.name().equals("unsigned long")) {
                  for(var5 = 0; var5 != Integer.MAX_VALUE && var2.contains(Integer.toString(var5)); ++var5) {
                  }

                  var3 = Integer.toString(var5);
               } else if (var4.name().equals("unsigned long long")) {
                  for(var11 = 0L; var11 != Long.MAX_VALUE && var2.contains(Long.toString(var11)); ++var11) {
                  }

                  var3 = Long.toString(var11);
               }
            }
         }
      }

      return var3;
   }

   private Vector vectorizeLabels(Vector var1, boolean var2) {
      Vector var3 = new Vector();
      Enumeration var4 = var1.elements();

      while(var4.hasMoreElements()) {
         UnionBranch var5 = (UnionBranch)var4.nextElement();

         String var8;
         for(Enumeration var6 = var5.labels.elements(); var6.hasMoreElements(); var3.addElement(var8)) {
            Expression var7 = (Expression)var6.nextElement();
            if (this.unionIsEnum) {
               if (var2) {
                  var8 = this.typePackage + "_" + Util.parseExpression(var7);
               } else {
                  var8 = this.typePackage + Util.parseExpression(var7);
               }
            } else {
               var8 = Util.parseExpression(var7);
            }
         }
      }

      return var3;
   }

   private String safeName(UnionEntry var1, String var2) {
      Enumeration var3 = var1.branches().elements();

      while(var3.hasMoreElements()) {
         if (((UnionBranch)var3.nextElement()).typedef.name().equals(var2)) {
            var2 = '_' + var2;
            break;
         }
      }

      return var2;
   }

   private boolean coversAll(UnionEntry var1) {
      SymtabEntry var2 = Util.typeOf(var1.type());
      boolean var3 = false;
      if (var2.name().equals("boolean")) {
         if (var1.branches().size() == 2) {
            var3 = true;
         }
      } else if (var2 instanceof EnumEntry) {
         Vector var4 = this.vectorizeLabels(var1.branches(), true);
         if (var4.size() == ((EnumEntry)var2).elements().size()) {
            var3 = true;
         }
      }

      return var3;
   }

   private void writeBranchMethods(PrintWriter var1, UnionEntry var2, UnionBranch var3, int var4) {
      var1.println();
      var1.println("  public " + Util.javaName(var3.typedef) + " " + var3.typedef.name() + " ()");
      var1.println("  {");
      var1.println("    if (__uninitialized)");
      var1.println("      throw new org.omg.CORBA.BAD_OPERATION ();");
      var1.println("    verify" + var3.typedef.name() + " (__discriminator);");
      var1.println("    return ___" + var3.typedef.name() + ";");
      var1.println("  }");
      var1.println();
      var1.println("  public void " + var3.typedef.name() + " (" + Util.javaName(var3.typedef) + " value)");
      var1.println("  {");
      if (var3.labels.size() == 0) {
         var1.println("    __discriminator = " + this.defaultDiscriminator(var2) + ";");
      } else if (this.unionIsEnum) {
         var1.println("    __discriminator = " + this.typePackage + Util.parseExpression((Expression)var3.labels.firstElement()) + ";");
      } else {
         var1.println("    __discriminator = " + this.cast((Expression)var3.labels.firstElement(), var2.type()) + ";");
      }

      var1.println("    ___" + var3.typedef.name() + " = value;");
      var1.println("    __uninitialized = false;");
      var1.println("  }");
      SymtabEntry var5 = Util.typeOf(var2.type());
      if (var3.labels.size() > 0 || var3.isDefault) {
         var1.println();
         var1.println("  public void " + var3.typedef.name() + " (" + Util.javaName(var5) + " discriminator, " + Util.javaName(var3.typedef) + " value)");
         var1.println("  {");
         var1.println("    verify" + var3.typedef.name() + " (discriminator);");
         var1.println("    __discriminator = discriminator;");
         var1.println("    ___" + var3.typedef.name() + " = value;");
         var1.println("    __uninitialized = false;");
         var1.println("  }");
      }

      var1.println();
      var1.println("  private void verify" + var3.typedef.name() + " (" + Util.javaName(var5) + " discriminator)");
      var1.println("  {");
      boolean var6 = true;
      if (!var3.isDefault || var2.branches().size() != 1) {
         var1.print("    if (");
         Enumeration var7;
         if (!var3.isDefault) {
            for(var7 = var3.labels.elements(); var7.hasMoreElements(); var6 = false) {
               Expression var11 = (Expression)var7.nextElement();
               if (!var6) {
                  var1.print(" && ");
               }

               if (this.unionIsEnum) {
                  var1.print("discriminator != " + this.typePackage + Util.parseExpression(var11));
               } else {
                  var1.print("discriminator != " + Util.parseExpression(var11));
               }
            }
         } else {
            var7 = var2.branches().elements();

            label67:
            while(true) {
               UnionBranch var8;
               do {
                  if (!var7.hasMoreElements()) {
                     break label67;
                  }

                  var8 = (UnionBranch)var7.nextElement();
               } while(var8 == var3);

               for(Enumeration var9 = var8.labels.elements(); var9.hasMoreElements(); var6 = false) {
                  Expression var10 = (Expression)var9.nextElement();
                  if (!var6) {
                     var1.print(" || ");
                  }

                  if (this.unionIsEnum) {
                     var1.print("discriminator == " + this.typePackage + Util.parseExpression(var10));
                  } else {
                     var1.print("discriminator == " + Util.parseExpression(var10));
                  }
               }
            }
         }

         var1.println(")");
         var1.println("      throw new org.omg.CORBA.BAD_OPERATION ();");
      }

      var1.println("  }");
   }

   private int unionLabelSize(UnionEntry var1) {
      int var2 = 0;
      Vector var3 = var1.branches();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         UnionBranch var5 = (UnionBranch)((UnionBranch)var3.get(var4));
         int var6 = var5.labels.size();
         var2 += var6 == 0 ? 1 : var6;
      }

      return var2;
   }

   public int helperType(int var1, String var2, TCOffsets var3, String var4, SymtabEntry var5, PrintWriter var6) {
      TCOffsets var7 = new TCOffsets();
      UnionEntry var8 = (UnionEntry)var5;
      String var9 = "_disTypeCode" + var1;
      String var10 = "_members" + var1;
      var6.println(var2 + "org.omg.CORBA.TypeCode " + var9 + ';');
      var1 = ((JavaGenerator)var8.type().generator()).type(var1 + 1, var2, var7, var9, var8.type(), var6);
      var3.bumpCurrentOffset(var7.currentOffset());
      var6.println(var2 + "org.omg.CORBA.UnionMember[] " + var10 + " = new org.omg.CORBA.UnionMember [" + this.unionLabelSize(var8) + "];");
      String var11 = "_tcOf" + var10;
      String var12 = "_anyOf" + var10;
      var6.println(var2 + "org.omg.CORBA.TypeCode " + var11 + ';');
      var6.println(var2 + "org.omg.CORBA.Any " + var12 + ';');
      var7 = new TCOffsets();
      var7.set(var5);
      int var13 = var7.currentOffset();

      for(int var14 = 0; var14 < var8.branches().size(); ++var14) {
         UnionBranch var15 = (UnionBranch)var8.branches().elementAt(var14);
         TypedefEntry var16 = var15.typedef;
         Vector var17 = var15.labels;
         String var18 = Util.stripLeadingUnderscores(var16.name());
         if (var17.size() == 0) {
            var6.println();
            var6.println(var2 + "// Branch for " + var18 + " (Default case)");
            SymtabEntry var24 = Util.typeOf(var8.type());
            var6.println(var2 + var12 + " = org.omg.CORBA.ORB.init ().create_any ();");
            var6.println(var2 + var12 + ".insert_octet ((byte)0); // default member label");
            var7.bumpCurrentOffset(4);
            var1 = ((JavaGenerator)var16.generator()).type(var1, var2, var7, var11, var16, var6);
            int var25 = var7.currentOffset();
            var7 = new TCOffsets();
            var7.set(var5);
            var7.bumpCurrentOffset(var25 - var13);
            var6.println(var2 + var10 + '[' + var14 + "] = new org.omg.CORBA.UnionMember (");
            var6.println(var2 + "  \"" + var18 + "\",");
            var6.println(var2 + "  " + var12 + ',');
            var6.println(var2 + "  " + var11 + ',');
            var6.println(var2 + "  null);");
         } else {
            Enumeration var19 = var17.elements();

            while(var19.hasMoreElements()) {
               Expression var20 = (Expression)((Expression)var19.nextElement());
               String var21 = Util.parseExpression(var20);
               var6.println();
               var6.println(var2 + "// Branch for " + var18 + " (case label " + var21 + ")");
               SymtabEntry var22 = Util.typeOf(var8.type());
               var6.println(var2 + var12 + " = org.omg.CORBA.ORB.init ().create_any ();");
               if (var22 instanceof PrimitiveEntry) {
                  var6.println(var2 + var12 + ".insert_" + Util.collapseName(var22.name()) + " ((" + Util.javaName(var22) + ')' + var21 + ");");
               } else {
                  String var23 = Util.javaName(var22);
                  var6.println(var2 + Util.helperName(var22, false) + ".insert (" + var12 + ", " + var23 + '.' + var21 + ");");
               }

               var7.bumpCurrentOffset(4);
               var1 = ((JavaGenerator)var16.generator()).type(var1, var2, var7, var11, var16, var6);
               int var26 = var7.currentOffset();
               var7 = new TCOffsets();
               var7.set(var5);
               var7.bumpCurrentOffset(var26 - var13);
               var6.println(var2 + var10 + '[' + var14 + "] = new org.omg.CORBA.UnionMember (");
               var6.println(var2 + "  \"" + var18 + "\",");
               var6.println(var2 + "  " + var12 + ',');
               var6.println(var2 + "  " + var11 + ',');
               var6.println(var2 + "  null);");
            }
         }
      }

      var3.bumpCurrentOffset(var7.currentOffset());
      var6.println(var2 + var4 + " = org.omg.CORBA.ORB.init ().create_union_tc (" + Util.helperName(var8, true) + ".id (), \"" + var5.name() + "\", " + var9 + ", " + var10 + ");");
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

   public void helperWrite(SymtabEntry var1, PrintWriter var2) {
      this.write(0, "    ", "value", var1, var2);
   }

   public int read(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      UnionEntry var6 = (UnionEntry)var4;
      String var7 = "_dis" + var1++;
      SymtabEntry var8 = Util.typeOf(var6.type());
      Util.writeInitializer(var2, var7, "", var8, var5);
      if (var8 instanceof PrimitiveEntry) {
         var1 = ((JavaGenerator)var8.generator()).read(var1, var2, var7, var8, var5);
      } else {
         var5.println(var2 + var7 + " = " + Util.helperName(var8, true) + ".read (istream);");
      }

      if (var8.name().equals("boolean")) {
         var1 = this.readBoolean(var7, var1, var2, var3, var6, var5);
      } else {
         var1 = this.readNonBoolean(var7, var1, var2, var3, var6, var5);
      }

      return var1;
   }

   private int readBoolean(String var1, int var2, String var3, String var4, UnionEntry var5, PrintWriter var6) {
      UnionBranch var7 = (UnionBranch)var5.branches().firstElement();
      UnionBranch var8;
      if (var5.branches().size() == 2) {
         var8 = (UnionBranch)var5.branches().lastElement();
      } else {
         var8 = null;
      }

      boolean var9 = false;
      boolean var10 = false;

      try {
         if (var5.branches().size() != 1 || var5.defaultBranch() == null && var7.labels.size() != 2) {
            Expression var11 = (Expression)((Expression)var7.labels.firstElement());
            Boolean var12 = (Boolean)((Boolean)var11.evaluate());
            var9 = var12;
         } else {
            var10 = true;
         }
      } catch (EvaluationException var13) {
      }

      if (var10) {
         var2 = this.readBranch(var2, var3, var7.typedef.name(), "", var7.typedef, var6);
      } else {
         if (!var9) {
            UnionBranch var14 = var7;
            var7 = var8;
            var8 = var14;
         }

         var6.println(var3 + "if (" + var1 + ')');
         if (var7 == null) {
            var6.println(var3 + "  value._default(" + var1 + ");");
         } else {
            var6.println(var3 + '{');
            var2 = this.readBranch(var2, var3 + "  ", var7.typedef.name(), var1, var7.typedef, var6);
            var6.println(var3 + '}');
         }

         var6.println(var3 + "else");
         if (var8 == null) {
            var6.println(var3 + "  value._default(" + var1 + ");");
         } else {
            var6.println(var3 + '{');
            var2 = this.readBranch(var2, var3 + "  ", var8.typedef.name(), var1, var8.typedef, var6);
            var6.println(var3 + '}');
         }
      }

      return var2;
   }

   private int readNonBoolean(String var1, int var2, String var3, String var4, UnionEntry var5, PrintWriter var6) {
      SymtabEntry var7 = Util.typeOf(var5.type());
      if (var7 instanceof EnumEntry) {
         var6.println(var3 + "switch (" + var1 + ".value ())");
      } else {
         var6.println(var3 + "switch (" + var1 + ')');
      }

      var6.println(var3 + '{');
      String var8 = Util.javaQualifiedName(var7) + '.';
      Enumeration var9 = var5.branches().elements();

      while(var9.hasMoreElements()) {
         UnionBranch var10 = (UnionBranch)var9.nextElement();
         Enumeration var11 = var10.labels.elements();

         while(var11.hasMoreElements()) {
            Expression var12 = (Expression)var11.nextElement();
            if (var7 instanceof EnumEntry) {
               String var13 = Util.parseExpression(var12);
               var6.println(var3 + "  case " + var8 + '_' + var13 + ':');
            } else {
               var6.println(var3 + "  case " + this.cast(var12, var7) + ':');
            }
         }

         if (!var10.typedef.equals(var5.defaultBranch())) {
            var2 = this.readBranch(var2, var3 + "    ", var10.typedef.name(), var10.labels.size() > 1 ? var1 : "", var10.typedef, var6);
            var6.println(var3 + "    break;");
         }
      }

      if (!this.coversAll(var5)) {
         var6.println(var3 + "  default:");
         if (var5.defaultBranch() == null) {
            var6.println(var3 + "    value._default( " + var1 + " ) ;");
         } else {
            var2 = this.readBranch(var2, var3 + "    ", var5.defaultBranch().name(), var1, var5.defaultBranch(), var6);
         }

         var6.println(var3 + "    break;");
      }

      var6.println(var3 + '}');
      return var2;
   }

   private int readBranch(int var1, String var2, String var3, String var4, TypedefEntry var5, PrintWriter var6) {
      SymtabEntry var7 = var5.type();
      Util.writeInitializer(var2, '_' + var3, "", var5, var6);
      if (var5.arrayInfo().isEmpty() && !(var7 instanceof SequenceEntry) && !(var7 instanceof PrimitiveEntry) && !(var7 instanceof StringEntry)) {
         var6.println(var2 + '_' + var3 + " = " + Util.helperName(var7, true) + ".read (istream);");
      } else {
         var1 = ((JavaGenerator)var5.generator()).read(var1, var2, '_' + var3, var5, var6);
      }

      var6.print(var2 + "value." + var3 + " (");
      if (var4 == "") {
         var6.println("_" + var3 + ");");
      } else {
         var6.println(var4 + ", _" + var3 + ");");
      }

      return var1;
   }

   public int write(int var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      UnionEntry var6 = (UnionEntry)var4;
      SymtabEntry var7 = Util.typeOf(var6.type());
      if (var7 instanceof PrimitiveEntry) {
         var1 = ((JavaGenerator)var7.generator()).write(var1, var2, var3 + ".discriminator ()", var7, var5);
      } else {
         var5.println(var2 + Util.helperName(var7, true) + ".write (ostream, " + var3 + ".discriminator ());");
      }

      if (var7.name().equals("boolean")) {
         var1 = this.writeBoolean(var3 + ".discriminator ()", var1, var2, var3, var6, var5);
      } else {
         var1 = this.writeNonBoolean(var3 + ".discriminator ()", var1, var2, var3, var6, var5);
      }

      return var1;
   }

   private int writeBoolean(String var1, int var2, String var3, String var4, UnionEntry var5, PrintWriter var6) {
      SymtabEntry var7 = Util.typeOf(var5.type());
      UnionBranch var8 = (UnionBranch)var5.branches().firstElement();
      UnionBranch var9;
      if (var5.branches().size() == 2) {
         var9 = (UnionBranch)var5.branches().lastElement();
      } else {
         var9 = null;
      }

      boolean var10 = false;
      boolean var11 = false;

      try {
         if (var5.branches().size() != 1 || var5.defaultBranch() == null && var8.labels.size() != 2) {
            var10 = (Boolean)((Expression)var8.labels.firstElement()).evaluate();
         } else {
            var11 = true;
         }
      } catch (EvaluationException var13) {
      }

      if (var11) {
         var2 = this.writeBranch(var2, var3, var4, var8.typedef, var6);
      } else {
         if (!var10) {
            UnionBranch var12 = var8;
            var8 = var9;
            var9 = var12;
         }

         if (var8 != null && var9 != null) {
            var6.println(var3 + "if (" + var1 + ')');
            var6.println(var3 + '{');
            var2 = this.writeBranch(var2, var3 + "  ", var4, var8.typedef, var6);
            var6.println(var3 + '}');
            var6.println(var3 + "else");
            var6.println(var3 + '{');
            var2 = this.writeBranch(var2, var3 + "  ", var4, var9.typedef, var6);
            var6.println(var3 + '}');
         } else if (var8 != null) {
            var6.println(var3 + "if (" + var1 + ')');
            var6.println(var3 + '{');
            var2 = this.writeBranch(var2, var3 + "  ", var4, var8.typedef, var6);
            var6.println(var3 + '}');
         } else {
            var6.println(var3 + "if (!" + var1 + ')');
            var6.println(var3 + '{');
            var2 = this.writeBranch(var2, var3 + "  ", var4, var9.typedef, var6);
            var6.println(var3 + '}');
         }
      }

      return var2;
   }

   private int writeNonBoolean(String var1, int var2, String var3, String var4, UnionEntry var5, PrintWriter var6) {
      SymtabEntry var7 = Util.typeOf(var5.type());
      if (var7 instanceof EnumEntry) {
         var6.println(var3 + "switch (" + var4 + ".discriminator ().value ())");
      } else {
         var6.println(var3 + "switch (" + var4 + ".discriminator ())");
      }

      var6.println(var3 + "{");
      String var8 = Util.javaQualifiedName(var7) + '.';
      Enumeration var9 = var5.branches().elements();

      while(var9.hasMoreElements()) {
         UnionBranch var10 = (UnionBranch)var9.nextElement();
         Enumeration var11 = var10.labels.elements();

         while(var11.hasMoreElements()) {
            Expression var12 = (Expression)var11.nextElement();
            if (var7 instanceof EnumEntry) {
               String var13 = Util.parseExpression(var12);
               var6.println(var3 + "  case " + var8 + '_' + var13 + ":");
            } else {
               var6.println(var3 + "  case " + this.cast(var12, var7) + ':');
            }
         }

         if (!var10.typedef.equals(var5.defaultBranch())) {
            var2 = this.writeBranch(var2, var3 + "    ", var4, var10.typedef, var6);
            var6.println(var3 + "    break;");
         }
      }

      if (var5.defaultBranch() != null) {
         var6.println(var3 + "  default:");
         var2 = this.writeBranch(var2, var3 + "    ", var4, var5.defaultBranch(), var6);
         var6.println(var3 + "    break;");
      }

      var6.println(var3 + "}");
      return var2;
   }

   private int writeBranch(int var1, String var2, String var3, TypedefEntry var4, PrintWriter var5) {
      SymtabEntry var6 = var4.type();
      if (var4.arrayInfo().isEmpty() && !(var6 instanceof SequenceEntry) && !(var6 instanceof PrimitiveEntry) && !(var6 instanceof StringEntry)) {
         var5.println(var2 + Util.helperName(var6, true) + ".write (ostream, " + var3 + '.' + var4.name() + " ());");
      } else {
         var1 = ((JavaGenerator)var4.generator()).write(var1, var2, var3 + '.' + var4.name() + " ()", var4, var5);
      }

      return var1;
   }

   private String cast(Expression var1, SymtabEntry var2) {
      String var3 = Util.parseExpression(var1);
      long var4;
      int var6;
      if (var2.name().indexOf("short") >= 0) {
         if (var1.value() instanceof Long) {
            var4 = (Long)var1.value();
            if (var4 > 32767L) {
               var3 = "(short)(" + var3 + ')';
            }
         } else if (var1.value() instanceof Integer) {
            var6 = (Integer)var1.value();
            if (var6 > 32767) {
               var3 = "(short)(" + var3 + ')';
            }
         }
      } else if (var2.name().indexOf("long") >= 0) {
         if (var1.value() instanceof Long) {
            var4 = (Long)var1.value();
            if (var4 > 2147483647L || var4 == -2147483648L) {
               var3 = "(int)(" + var3 + ')';
            }
         } else if (var1.value() instanceof Integer) {
            var6 = (Integer)var1.value();
            if (var6 > Integer.MAX_VALUE || var6 == Integer.MIN_VALUE) {
               var3 = "(int)(" + var3 + ')';
            }
         }
      }

      return var3;
   }
}
