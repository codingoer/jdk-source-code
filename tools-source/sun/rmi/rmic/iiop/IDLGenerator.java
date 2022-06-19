package sun.rmi.rmic.iiop;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.rmi.rmic.IndentingWriter;
import sun.rmi.rmic.Main;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Identifier;

public class IDLGenerator extends Generator {
   private boolean valueMethods = true;
   private boolean factory = true;
   private Hashtable ifHash = new Hashtable();
   private Hashtable imHash = new Hashtable();
   private boolean isThrown = true;
   private boolean isException = true;
   private boolean isForward = true;
   private boolean forValuetype = true;

   protected boolean requireNewInstance() {
      return false;
   }

   protected boolean parseNonConforming(ContextStack var1) {
      return this.valueMethods;
   }

   protected CompoundType getTopType(ClassDefinition var1, ContextStack var2) {
      return CompoundType.forCompound(var1, var2);
   }

   protected Identifier getOutputId(Generator.OutputType var1) {
      Identifier var2 = super.getOutputId(var1);
      Type var3 = var1.getType();
      String var4 = var1.getName();
      if (var2 == idJavaLangClass) {
         return var3.isArray() ? Identifier.lookup("org.omg.boxedRMI.javax.rmi.CORBA." + var4) : idClassDesc;
      } else if (var2 == idJavaLangString && var3.isArray()) {
         return Identifier.lookup("org.omg.boxedRMI.CORBA." + var4);
      } else if ("org.omg.CORBA.Object".equals(var3.getQualifiedName()) && var3.isArray()) {
         return Identifier.lookup("org.omg.boxedRMI." + var4);
      } else if (var3.isArray()) {
         ArrayType var9 = (ArrayType)var3;
         Type var10 = var9.getElementType();
         if (var10.isCompound()) {
            CompoundType var7 = (CompoundType)var10;
            String var8 = var7.getQualifiedName();
            if (var7.isIDLEntity()) {
               return Identifier.lookup(this.getQualifiedName(var9));
            }
         }

         return Identifier.lookup(idBoxedRMI, var2);
      } else {
         if (var3.isCompound()) {
            CompoundType var5 = (CompoundType)var3;
            String var6 = var5.getQualifiedName();
            if (var5.isBoxed()) {
               return Identifier.lookup(this.getQualifiedName(var5));
            }
         }

         return var2;
      }
   }

   protected String getFileNameExtensionFor(Generator.OutputType var1) {
      return ".idl";
   }

   public boolean parseArgs(String[] var1, Main var2) {
      boolean var3 = super.parseArgs(var1, var2);
      if (var3) {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            if (var1[var6] != null) {
               if (var1[var6].equalsIgnoreCase("-idl")) {
                  this.idl = true;
                  var1[var6] = null;
               } else if (var1[var6].equalsIgnoreCase("-valueMethods")) {
                  this.valueMethods = true;
                  var1[var6] = null;
               } else if (var1[var6].equalsIgnoreCase("-noValueMethods")) {
                  this.valueMethods = false;
                  var1[var6] = null;
               } else if (var1[var6].equalsIgnoreCase("-init")) {
                  this.factory = false;
                  var1[var6] = null;
               } else if (var1[var6].equalsIgnoreCase("-factory")) {
                  this.factory = true;
                  var1[var6] = null;
               } else {
                  String var4;
                  String var5;
                  if (var1[var6].equalsIgnoreCase("-idlfile")) {
                     var1[var6] = null;
                     ++var6;
                     if (var6 < var1.length && var1[var6] != null && !var1[var6].startsWith("-")) {
                        var4 = var1[var6];
                        var1[var6] = null;
                        ++var6;
                        if (var6 < var1.length && var1[var6] != null && !var1[var6].startsWith("-")) {
                           var5 = var1[var6];
                           var1[var6] = null;
                           this.ifHash.put(var4, var5);
                           continue;
                        }
                     }

                     var2.error("rmic.option.requires.argument", "-idlfile");
                     var3 = false;
                  } else if (var1[var6].equalsIgnoreCase("-idlmodule")) {
                     var1[var6] = null;
                     ++var6;
                     if (var6 < var1.length && var1[var6] != null && !var1[var6].startsWith("-")) {
                        var4 = var1[var6];
                        var1[var6] = null;
                        ++var6;
                        if (var6 < var1.length && var1[var6] != null && !var1[var6].startsWith("-")) {
                           var5 = var1[var6];
                           var1[var6] = null;
                           this.imHash.put(var4, var5);
                           continue;
                        }
                     }

                     var2.error("rmic.option.requires.argument", "-idlmodule");
                     var3 = false;
                  }
               }
            }
         }
      }

      return var3;
   }

   protected Generator.OutputType[] getOutputTypesFor(CompoundType var1, HashSet var2) {
      Vector var3 = this.getAllReferencesFor(var1);
      Vector var4 = new Vector();

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         Type var6 = (Type)var3.elementAt(var5);
         if (var6.isArray()) {
            ArrayType var14 = (ArrayType)var6;
            int var15 = var14.getArrayDimension();
            Type var9 = var14.getElementType();
            String var10 = this.unEsc(var9.getIDLName()).replace(' ', '_');

            for(int var11 = 0; var11 < var15; ++var11) {
               String var12 = "seq" + (var11 + 1) + "_" + var10;
               var4.addElement(new Generator.OutputType(var12, var14));
            }
         } else if (var6.isCompound()) {
            String var7 = this.unEsc(var6.getIDLName());
            var4.addElement(new Generator.OutputType(var7.replace(' ', '_'), var6));
            if (var6.isClass()) {
               ClassType var8 = (ClassType)var6;
               if (var8.isException()) {
                  var7 = this.unEsc(var8.getIDLExceptionName());
                  var4.addElement(new Generator.OutputType(var7.replace(' ', '_'), var6));
               }
            }
         }
      }

      Generator.OutputType[] var13 = new Generator.OutputType[var4.size()];
      var4.copyInto(var13);
      return var13;
   }

   protected Vector getAllReferencesFor(CompoundType var1) {
      Hashtable var2 = new Hashtable();
      Hashtable var3 = new Hashtable();
      Hashtable var4 = new Hashtable();
      var2.put(var1.getQualifiedName(), var1);
      this.accumulateReferences(var2, var3, var4);

      int var5;
      do {
         var5 = var2.size();
         this.accumulateReferences(var2, var3, var4);
      } while(var5 < var2.size());

      Vector var6 = new Vector();
      Enumeration var7 = var2.elements();

      CompoundType var8;
      while(var7.hasMoreElements()) {
         var8 = (CompoundType)var7.nextElement();
         var6.addElement(var8);
      }

      var7 = var3.elements();

      while(var7.hasMoreElements()) {
         var8 = (CompoundType)var7.nextElement();
         var6.addElement(var8);
      }

      var7 = var4.elements();

      while(true) {
         label31:
         while(var7.hasMoreElements()) {
            ArrayType var13 = (ArrayType)var7.nextElement();
            int var9 = var13.getArrayDimension();
            Type var10 = var13.getElementType();
            Enumeration var11 = var4.elements();

            while(var11.hasMoreElements()) {
               ArrayType var12 = (ArrayType)var11.nextElement();
               if (var10 == var12.getElementType() && var9 < var12.getArrayDimension()) {
                  continue label31;
               }
            }

            var6.addElement(var13);
         }

         return var6;
      }
   }

   protected void accumulateReferences(Hashtable var1, Hashtable var2, Hashtable var3) {
      Enumeration var4 = var1.elements();

      CompoundType var5;
      while(var4.hasMoreElements()) {
         var5 = (CompoundType)var4.nextElement();
         Vector var6 = this.getData(var5);
         Vector var7 = this.getMethods(var5);
         this.getInterfaces(var5, var1);
         this.getInheritance(var5, var1);
         this.getMethodReferences(var7, var1, var2, var3, var1);
         this.getMemberReferences(var6, var1, var2, var3);
      }

      var4 = var3.elements();

      while(var4.hasMoreElements()) {
         ArrayType var8 = (ArrayType)var4.nextElement();
         Type var9 = var8.getElementType();
         this.addReference(var9, var1, var2, var3);
      }

      var4 = var1.elements();

      while(var4.hasMoreElements()) {
         var5 = (CompoundType)var4.nextElement();
         if (!this.isIDLGeneratedFor(var5)) {
            var1.remove(var5.getQualifiedName());
         }
      }

   }

   protected boolean isIDLGeneratedFor(CompoundType var1) {
      if (var1.isCORBAObject()) {
         return false;
      } else if (var1.isIDLEntity()) {
         if (var1.isBoxed()) {
            return true;
         } else if ("org.omg.CORBA.portable.IDLEntity".equals(var1.getQualifiedName())) {
            return true;
         } else {
            return var1.isCORBAUserException();
         }
      } else {
         Hashtable var2 = new Hashtable();
         this.getInterfaces(var1, var2);
         if (var1.getTypeCode() == 65536) {
            return var2.size() >= 2;
         } else {
            return true;
         }
      }
   }

   protected void writeOutputFor(Generator.OutputType var1, HashSet var2, IndentingWriter var3) throws IOException {
      Type var4 = var1.getType();
      if (var4.isArray()) {
         this.writeSequence(var1, var3);
      } else if (this.isSpecialReference(var4)) {
         this.writeSpecial(var4, var3);
      } else {
         if (var4.isCompound()) {
            CompoundType var5 = (CompoundType)var4;
            if (var5.isIDLEntity() && var5.isBoxed()) {
               this.writeBoxedIDL(var5, var3);
               return;
            }
         }

         if (var4.isClass()) {
            ClassType var8 = (ClassType)var4;
            if (var8.isException()) {
               String var6 = this.unEsc(var8.getIDLExceptionName());
               String var7 = var1.getName();
               if (var7.equals(var6.replace(' ', '_'))) {
                  this.writeException(var8, var3);
                  return;
               }
            }
         }

         switch (var4.getTypeCode()) {
            case 4096:
            case 8192:
               this.writeRemote((RemoteType)var4, var3);
               break;
            case 16384:
            case 131072:
               this.writeNCType((CompoundType)var4, var3);
               break;
            case 32768:
               this.writeValue((ValueType)var4, var3);
               break;
            case 65536:
               this.writeImplementation((ImplementationType)var4, var3);
               break;
            default:
               throw new CompilerError("IDLGenerator got unexpected type code: " + var4.getTypeCode());
         }

      }
   }

   protected void writeImplementation(ImplementationType var1, IndentingWriter var2) throws IOException {
      Hashtable var3 = new Hashtable();
      Hashtable var4 = new Hashtable();
      this.getInterfaces(var1, var3);
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeInheritedIncludes(var3, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeIncOrb(var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.p("interface " + var1.getIDLName());
      this.writeInherits(var3, !this.forValuetype, var2);
      var2.pln(" {");
      var2.pln("};");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEpilog(var1, var4, var2);
   }

   protected void writeNCType(CompoundType var1, IndentingWriter var2) throws IOException {
      Vector var3 = this.getConstants(var1);
      Vector var4 = this.getMethods(var1);
      Hashtable var5 = new Hashtable();
      Hashtable var6 = new Hashtable();
      Hashtable var7 = new Hashtable();
      Hashtable var8 = new Hashtable();
      Hashtable var9 = new Hashtable();
      this.getInterfaces(var1, var5);
      this.getInheritance(var1, var5);
      this.getMethodReferences(var4, var6, var7, var8, var9);
      this.writeProlog(var1, var6, var7, var8, var9, var5, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.p("abstract valuetype " + var1.getIDLName());
      this.writeInherits(var5, !this.forValuetype, var2);
      var2.pln(" {");
      if (var3.size() + var4.size() > 0) {
         var2.pln();
         var2.pI();

         int var10;
         for(var10 = 0; var10 < var3.size(); ++var10) {
            this.writeConstant((CompoundType.Member)var3.elementAt(var10), var2);
         }

         for(var10 = 0; var10 < var4.size(); ++var10) {
            this.writeMethod((CompoundType.Method)var4.elementAt(var10), var2);
         }

         var2.pO();
         var2.pln();
      }

      var2.pln("};");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEpilog(var1, var6, var2);
   }

   protected void writeRemote(RemoteType var1, IndentingWriter var2) throws IOException {
      Vector var3 = this.getConstants(var1);
      Vector var4 = this.getMethods(var1);
      Hashtable var5 = new Hashtable();
      Hashtable var6 = new Hashtable();
      Hashtable var7 = new Hashtable();
      Hashtable var8 = new Hashtable();
      Hashtable var9 = new Hashtable();
      this.getInterfaces(var1, var5);
      this.getMethodReferences(var4, var6, var7, var8, var9);
      this.writeProlog(var1, var6, var7, var8, var9, var5, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      if (var1.getTypeCode() == 8192) {
         var2.p("abstract ");
      }

      var2.p("interface " + var1.getIDLName());
      this.writeInherits(var5, !this.forValuetype, var2);
      var2.pln(" {");
      if (var3.size() + var4.size() > 0) {
         var2.pln();
         var2.pI();

         int var10;
         for(var10 = 0; var10 < var3.size(); ++var10) {
            this.writeConstant((CompoundType.Member)var3.elementAt(var10), var2);
         }

         for(var10 = 0; var10 < var4.size(); ++var10) {
            this.writeMethod((CompoundType.Method)var4.elementAt(var10), var2);
         }

         var2.pO();
         var2.pln();
      }

      var2.pln("};");
      var2.pO();
      var2.pln();
      this.writeRepositoryID(var1, var2);
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEpilog(var1, var6, var2);
   }

   protected void writeValue(ValueType var1, IndentingWriter var2) throws IOException {
      Vector var3 = this.getData(var1);
      Vector var4 = this.getConstants(var1);
      Vector var5 = this.getMethods(var1);
      Hashtable var6 = new Hashtable();
      Hashtable var7 = new Hashtable();
      Hashtable var8 = new Hashtable();
      Hashtable var9 = new Hashtable();
      Hashtable var10 = new Hashtable();
      this.getInterfaces(var1, var6);
      this.getInheritance(var1, var6);
      this.getMethodReferences(var5, var7, var8, var9, var10);
      this.getMemberReferences(var3, var7, var8, var9);
      this.writeProlog(var1, var7, var8, var9, var10, var6, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      if (var1.isCustom()) {
         var2.p("custom ");
      }

      var2.p("valuetype " + var1.getIDLName());
      this.writeInherits(var6, this.forValuetype, var2);
      var2.pln(" {");
      if (var4.size() + var3.size() + var5.size() > 0) {
         var2.pln();
         var2.pI();

         int var11;
         for(var11 = 0; var11 < var4.size(); ++var11) {
            this.writeConstant((CompoundType.Member)var4.elementAt(var11), var2);
         }

         CompoundType.Member var12;
         for(var11 = 0; var11 < var3.size(); ++var11) {
            var12 = (CompoundType.Member)var3.elementAt(var11);
            if (var12.getType().isPrimitive()) {
               this.writeData(var12, var2);
            }
         }

         for(var11 = 0; var11 < var3.size(); ++var11) {
            var12 = (CompoundType.Member)var3.elementAt(var11);
            if (!var12.getType().isPrimitive()) {
               this.writeData(var12, var2);
            }
         }

         for(var11 = 0; var11 < var5.size(); ++var11) {
            this.writeMethod((CompoundType.Method)var5.elementAt(var11), var2);
         }

         var2.pO();
         var2.pln();
      }

      var2.pln("};");
      var2.pO();
      var2.pln();
      this.writeRepositoryID(var1, var2);
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEpilog(var1, var7, var2);
   }

   protected void writeProlog(CompoundType var1, Hashtable var2, Hashtable var3, Hashtable var4, Hashtable var5, Hashtable var6, IndentingWriter var7) throws IOException {
      this.writeBanner(var1, 0, !this.isException, var7);
      this.writeForwardReferences(var2, var7);
      this.writeIncludes(var5, this.isThrown, var7);
      this.writeInheritedIncludes(var6, var7);
      this.writeIncludes(var3, !this.isThrown, var7);
      this.writeBoxedRMIIncludes(var4, var7);
      this.writeIDLEntityIncludes(var2, var7);
      this.writeIncOrb(var7);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var7);
   }

   protected void writeEpilog(CompoundType var1, Hashtable var2, IndentingWriter var3) throws IOException {
      this.writeIncludes(var2, !this.isThrown, var3);
      this.writeEndif(var3);
   }

   protected void writeSpecial(Type var1, IndentingWriter var2) throws IOException {
      String var3 = var1.getQualifiedName();
      if ("java.io.Serializable".equals(var3)) {
         this.writeJavaIoSerializable(var1, var2);
      } else if ("java.io.Externalizable".equals(var3)) {
         this.writeJavaIoExternalizable(var1, var2);
      } else if ("java.lang.Object".equals(var3)) {
         this.writeJavaLangObject(var1, var2);
      } else if ("java.rmi.Remote".equals(var3)) {
         this.writeJavaRmiRemote(var1, var2);
      } else if ("org.omg.CORBA.portable.IDLEntity".equals(var3)) {
         this.writeIDLEntity(var1, var2);
      }

   }

   protected void writeJavaIoSerializable(Type var1, IndentingWriter var2) throws IOException {
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.pln("typedef any Serializable;");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEndif(var2);
   }

   protected void writeJavaIoExternalizable(Type var1, IndentingWriter var2) throws IOException {
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.pln("typedef any Externalizable;");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEndif(var2);
   }

   protected void writeJavaLangObject(Type var1, IndentingWriter var2) throws IOException {
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.pln("typedef any _Object;");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEndif(var2);
   }

   protected void writeJavaRmiRemote(Type var1, IndentingWriter var2) throws IOException {
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.pln("typedef Object Remote;");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEndif(var2);
   }

   protected void writeIDLEntity(Type var1, IndentingWriter var2) throws IOException {
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.pln("typedef any IDLEntity;");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEndif(var2);
   }

   protected void getInterfaces(CompoundType var1, Hashtable var2) {
      InterfaceType[] var3 = var1.getInterfaces();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         String var5 = var3[var4].getQualifiedName();
         switch (var1.getTypeCode()) {
            case 32768:
            case 131072:
               if ("java.io.Externalizable".equals(var5) || "java.io.Serializable".equals(var5) || "org.omg.CORBA.portable.IDLEntity".equals(var5)) {
                  continue;
               }
               break;
            default:
               if ("java.rmi.Remote".equals(var5)) {
                  continue;
               }
         }

         var2.put(var5, var3[var4]);
      }

   }

   protected void getInheritance(CompoundType var1, Hashtable var2) {
      ClassType var3 = var1.getSuperclass();
      if (var3 != null) {
         String var4 = var3.getQualifiedName();
         switch (var1.getTypeCode()) {
            case 32768:
            case 131072:
               if ("java.lang.Object".equals(var4)) {
                  return;
               }

               var2.put(var4, var3);
               return;
            default:
         }
      }
   }

   protected void getMethodReferences(Vector var1, Hashtable var2, Hashtable var3, Hashtable var4, Hashtable var5) {
      for(int var6 = 0; var6 < var1.size(); ++var6) {
         CompoundType.Method var7 = (CompoundType.Method)var1.elementAt(var6);
         Type[] var8 = var7.getArguments();
         Type var9 = var7.getReturnType();
         this.getExceptions(var7, var5);

         for(int var10 = 0; var10 < var8.length; ++var10) {
            this.addReference(var8[var10], var2, var3, var4);
         }

         this.addReference(var9, var2, var3, var4);
      }

   }

   protected void getMemberReferences(Vector var1, Hashtable var2, Hashtable var3, Hashtable var4) {
      for(int var5 = 0; var5 < var1.size(); ++var5) {
         CompoundType.Member var6 = (CompoundType.Member)var1.elementAt(var5);
         Type var7 = var6.getType();
         this.addReference(var7, var2, var3, var4);
      }

   }

   protected void addReference(Type var1, Hashtable var2, Hashtable var3, Hashtable var4) {
      String var5 = var1.getQualifiedName();
      switch (var1.getTypeCode()) {
         case 2048:
            if ("org.omg.CORBA.Object".equals(var5)) {
               return;
            }

            var2.put(var5, var1);
            return;
         case 4096:
         case 8192:
         case 16384:
         case 32768:
         case 131072:
            var2.put(var5, var1);
            return;
         case 262144:
            var4.put(var5 + var1.getArrayDimension(), var1);
            return;
         default:
            if (this.isSpecialReference(var1)) {
               var3.put(var5, var1);
            }

      }
   }

   protected boolean isSpecialReference(Type var1) {
      String var2 = var1.getQualifiedName();
      if ("java.io.Serializable".equals(var2)) {
         return true;
      } else if ("java.io.Externalizable".equals(var2)) {
         return true;
      } else if ("java.lang.Object".equals(var2)) {
         return true;
      } else if ("java.rmi.Remote".equals(var2)) {
         return true;
      } else {
         return "org.omg.CORBA.portable.IDLEntity".equals(var2);
      }
   }

   protected void getExceptions(CompoundType.Method var1, Hashtable var2) {
      ValueType[] var3 = var1.getExceptions();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         ValueType var5 = var3[var4];
         if (var5.isCheckedException() && !var5.isRemoteExceptionOrSubclass()) {
            var2.put(var5.getQualifiedName(), var5);
         }
      }

   }

   protected Vector getMethods(CompoundType var1) {
      Vector var2 = new Vector();
      int var3 = var1.getTypeCode();
      switch (var3) {
         case 4096:
         case 8192:
            break;
         case 16384:
         case 32768:
         case 131072:
            if (this.valueMethods) {
               break;
            }
         default:
            return var2;
      }

      Identifier var4 = var1.getIdentifier();
      CompoundType.Method[] var5 = var1.getMethods();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         if (!var5[var6].isPrivate() && !var5[var6].isInherited()) {
            if (var3 == 32768) {
               String var7 = var5[var6].getName();
               if ("readObject".equals(var7) || "writeObject".equals(var7) || "readExternal".equals(var7) || "writeExternal".equals(var7)) {
                  continue;
               }
            }

            if (var3 != 131072 && var3 != 16384 || !var5[var6].isConstructor()) {
               var2.addElement(var5[var6]);
            }
         }
      }

      return var2;
   }

   protected Vector getConstants(CompoundType var1) {
      Vector var2 = new Vector();
      CompoundType.Member[] var3 = var1.getMembers();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Type var5 = var3[var4].getType();
         String var6 = var3[var4].getValue();
         if (var3[var4].isPublic() && var3[var4].isFinal() && var3[var4].isStatic() && (var5.isPrimitive() || "String".equals(var5.getName())) && var6 != null) {
            var2.addElement(var3[var4]);
         }
      }

      return var2;
   }

   protected Vector getData(CompoundType var1) {
      Vector var2 = new Vector();
      if (var1.getTypeCode() != 32768) {
         return var2;
      } else {
         ValueType var3 = (ValueType)var1;
         CompoundType.Member[] var4 = var3.getMembers();
         boolean var5 = !var3.isCustom();

         for(int var6 = 0; var6 < var4.length; ++var6) {
            if (!var4[var6].isStatic() && !var4[var6].isTransient() && (var4[var6].isPublic() || var5)) {
               String var8 = var4[var6].getName();

               int var7;
               for(var7 = 0; var7 < var2.size(); ++var7) {
                  CompoundType.Member var9 = (CompoundType.Member)var2.elementAt(var7);
                  if (var8.compareTo(var9.getName()) < 0) {
                     break;
                  }
               }

               var2.insertElementAt(var4[var6], var7);
            }
         }

         return var2;
      }
   }

   protected void writeForwardReferences(Hashtable var1, IndentingWriter var2) throws IOException {
      Enumeration var3 = var1.elements();

      while(true) {
         Type var4;
         CompoundType var5;
         do {
            if (!var3.hasMoreElements()) {
               return;
            }

            var4 = (Type)var3.nextElement();
            if (!var4.isCompound()) {
               break;
            }

            var5 = (CompoundType)var4;
         } while(var5.isIDLEntity());

         this.writeForwardReference(var4, var2);
      }
   }

   protected void writeForwardReference(Type var1, IndentingWriter var2) throws IOException {
      String var3 = var1.getQualifiedName();
      if ("java.lang.String".equals(var3) || !"org.omg.CORBA.Object".equals(var3)) {
         this.writeIfndef(var1, 0, !this.isException, this.isForward, var2);
         this.writeModule1(var1, var2);
         var2.pln();
         var2.pI();
         switch (var1.getTypeCode()) {
            case 2048:
            case 4096:
               var2.p("interface ");
               break;
            case 8192:
               var2.p("abstract interface ");
               break;
            case 16384:
            case 131072:
               var2.p("abstract valuetype ");
               break;
            case 32768:
               var2.p("valuetype ");
         }

         var2.pln(var1.getIDLName() + ";");
         var2.pO();
         var2.pln();
         this.writeModule2(var1, var2);
         this.writeEndif(var2);
      }
   }

   protected void writeForwardReference(ArrayType var1, int var2, IndentingWriter var3) throws IOException {
      Type var4 = var1.getElementType();
      if (var2 < 1) {
         if (var4.isCompound()) {
            CompoundType var6 = (CompoundType)var4;
            this.writeForwardReference(var4, var3);
         }

      } else {
         String var5 = this.unEsc(var4.getIDLName()).replace(' ', '_');
         this.writeIfndef(var1, var2, !this.isException, this.isForward, var3);
         this.writeModule1(var1, var3);
         var3.pln();
         var3.pI();
         switch (var4.getTypeCode()) {
            case 2048:
            case 4096:
               var3.p("interface ");
               break;
            case 8192:
               var3.p("abstract interface ");
               break;
            case 16384:
            case 131072:
               var3.p("abstract valuetype ");
               break;
            case 32768:
               var3.p("valuetype ");
         }

         var3.pln("seq" + var2 + "_" + var5 + ";");
         var3.pO();
         var3.pln();
         this.writeModule2(var1, var3);
         this.writeEndif(var3);
      }
   }

   protected void writeIDLEntityIncludes(Hashtable var1, IndentingWriter var2) throws IOException {
      Enumeration var3 = var1.elements();

      while(var3.hasMoreElements()) {
         Type var4 = (Type)var3.nextElement();
         if (var4.isCompound()) {
            CompoundType var5 = (CompoundType)var4;
            if (var5.isIDLEntity()) {
               this.writeInclude(var5, 0, !this.isThrown, var2);
               var1.remove(var5.getQualifiedName());
            }
         }
      }

   }

   protected void writeIncludes(Hashtable var1, boolean var2, IndentingWriter var3) throws IOException {
      Enumeration var4 = var1.elements();

      while(var4.hasMoreElements()) {
         CompoundType var5 = (CompoundType)var4.nextElement();
         this.writeInclude(var5, 0, var2, var3);
      }

   }

   protected void writeBoxedRMIIncludes(Hashtable var1, IndentingWriter var2) throws IOException {
      Enumeration var3 = var1.elements();

      while(true) {
         label28:
         while(var3.hasMoreElements()) {
            ArrayType var4 = (ArrayType)var3.nextElement();
            int var5 = var4.getArrayDimension();
            Type var6 = var4.getElementType();
            Enumeration var7 = var1.elements();

            while(var7.hasMoreElements()) {
               ArrayType var8 = (ArrayType)var7.nextElement();
               if (var6 == var8.getElementType() && var5 < var8.getArrayDimension()) {
                  continue label28;
               }
            }

            this.writeInclude(var4, var5, !this.isThrown, var2);
         }

         return;
      }
   }

   protected void writeInheritedIncludes(Hashtable var1, IndentingWriter var2) throws IOException {
      Enumeration var3 = var1.elements();

      while(var3.hasMoreElements()) {
         CompoundType var4 = (CompoundType)var3.nextElement();
         this.writeInclude(var4, 0, !this.isThrown, var2);
      }

   }

   protected void writeInclude(Type var1, int var2, boolean var3, IndentingWriter var4) throws IOException {
      CompoundType var5;
      String var6;
      String[] var7;
      if (var1.isCompound()) {
         var5 = (CompoundType)var1;
         String var8 = var5.getQualifiedName();
         if ("java.lang.String".equals(var8)) {
            this.writeIncOrb(var4);
            return;
         }

         if ("org.omg.CORBA.Object".equals(var8)) {
            return;
         }

         var7 = this.getIDLModuleNames(var5);
         var6 = this.unEsc(var5.getIDLName());
         if (var5.isException()) {
            if (var5.isIDLEntityException()) {
               if (var5.isCORBAUserException()) {
                  if (var3) {
                     var6 = this.unEsc(var5.getIDLExceptionName());
                  }
               } else {
                  var6 = var5.getName();
               }
            } else if (var3) {
               var6 = this.unEsc(var5.getIDLExceptionName());
            }
         }
      } else {
         if (!var1.isArray()) {
            return;
         }

         Type var9 = var1.getElementType();
         if (var2 <= 0) {
            if (!var9.isCompound()) {
               return;
            }

            var5 = (CompoundType)var9;
            var7 = this.getIDLModuleNames(var5);
            var6 = this.unEsc(var5.getIDLName());
            this.writeInclude(var5, var7, var6, var4);
            return;
         }

         var7 = this.getIDLModuleNames(var1);
         var6 = "seq" + var2 + "_" + this.unEsc(var9.getIDLName().replace(' ', '_'));
      }

      this.writeInclude(var1, var7, var6, var4);
   }

   protected void writeInclude(Type var1, String[] var2, String var3, IndentingWriter var4) throws IOException {
      if (var1.isCompound()) {
         CompoundType var5 = (CompoundType)var1;
         if (this.ifHash.size() > 0 && var5.isIDLEntity()) {
            String var6 = var1.getQualifiedName();
            Enumeration var7 = this.ifHash.keys();

            while(var7.hasMoreElements()) {
               String var8 = (String)var7.nextElement();
               if (var6.startsWith(var8)) {
                  String var9 = (String)this.ifHash.get(var8);
                  var4.pln("#include \"" + var9 + "\"");
                  return;
               }
            }
         }
      } else if (!var1.isArray()) {
         return;
      }

      var4.p("#include \"");

      for(int var10 = 0; var10 < var2.length; ++var10) {
         var4.p(var2[var10] + "/");
      }

      var4.p(var3 + ".idl\"");
      var4.pln();
   }

   protected String getQualifiedName(Type var1) {
      String[] var2 = this.getIDLModuleNames(var1);
      int var3 = var2.length;
      StringBuffer var4 = new StringBuffer();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.append(var2[var5] + ".");
      }

      var4.append(var1.getIDLName());
      return var4.toString();
   }

   protected String getQualifiedIDLName(Type var1) {
      if (var1.isPrimitive()) {
         return var1.getIDLName();
      } else if (!var1.isArray() && "org.omg.CORBA.Object".equals(var1.getQualifiedName())) {
         return var1.getIDLName();
      } else {
         String[] var2 = this.getIDLModuleNames(var1);
         int var3 = var2.length;
         if (var3 <= 0) {
            return var1.getIDLName();
         } else {
            StringBuffer var4 = new StringBuffer();

            for(int var5 = 0; var5 < var3; ++var5) {
               var4.append("::" + var2[var5]);
            }

            var4.append("::" + var1.getIDLName());
            return var4.toString();
         }
      }
   }

   protected String[] getIDLModuleNames(Type var1) {
      String[] var2 = var1.getIDLModuleNames();
      CompoundType var3;
      if (var1.isCompound()) {
         var3 = (CompoundType)var1;
         if (!var3.isIDLEntity) {
            return var2;
         }

         if ("org.omg.CORBA.portable.IDLEntity".equals(var1.getQualifiedName())) {
            return var2;
         }
      } else {
         if (!var1.isArray()) {
            return var2;
         }

         Type var4 = var1.getElementType();
         if (!var4.isCompound()) {
            return var2;
         }

         var3 = (CompoundType)var4;
         if (!var3.isIDLEntity) {
            return var2;
         }

         if ("org.omg.CORBA.portable.IDLEntity".equals(var1.getQualifiedName())) {
            return var2;
         }
      }

      Vector var6 = new Vector();
      if (!this.translateJavaPackage(var3, var6)) {
         this.stripJavaPackage(var3, var6);
      }

      if (var3.isBoxed()) {
         var6.insertElementAt("org", 0);
         var6.insertElementAt("omg", 1);
         var6.insertElementAt("boxedIDL", 2);
      }

      if (var1.isArray()) {
         var6.insertElementAt("org", 0);
         var6.insertElementAt("omg", 1);
         var6.insertElementAt("boxedRMI", 2);
      }

      String[] var5 = new String[var6.size()];
      var6.copyInto(var5);
      return var5;
   }

   protected boolean translateJavaPackage(CompoundType var1, Vector var2) {
      var2.removeAllElements();
      boolean var3 = false;
      String var4 = null;
      if (!var1.isIDLEntity()) {
         return var3;
      } else {
         String var5 = var1.getPackageName();
         if (var5 == null) {
            return var3;
         } else {
            StringTokenizer var6 = new StringTokenizer(var5, ".");

            while(var6.hasMoreTokens()) {
               var2.addElement(var6.nextToken());
            }

            if (this.imHash.size() > 0) {
               Enumeration var7 = this.imHash.keys();

               while(true) {
                  String var8;
                  StringTokenizer var9;
                  int var11;
                  label65:
                  do {
                     label57:
                     while(var7.hasMoreElements()) {
                        var8 = (String)var7.nextElement();
                        var9 = new StringTokenizer(var8, ".");
                        int var10 = var2.size();

                        for(var11 = 0; var11 < var10 && var9.hasMoreTokens(); ++var11) {
                           if (!var2.elementAt(var11).equals(var9.nextToken())) {
                              continue label57;
                           }
                        }

                        if (!var9.hasMoreTokens()) {
                           break label65;
                        }

                        var4 = var9.nextToken();
                        continue label65;
                     }

                     return var3;
                  } while(!var1.getName().equals(var4) || var9.hasMoreTokens());

                  var3 = true;

                  for(int var12 = 0; var12 < var11; ++var12) {
                     var2.removeElementAt(0);
                  }

                  String var17 = (String)this.imHash.get(var8);
                  StringTokenizer var13 = new StringTokenizer(var17, "::");
                  int var14 = var13.countTokens();
                  boolean var15 = false;
                  if (var4 != null) {
                     --var14;
                  }

                  int var18;
                  for(var18 = 0; var18 < var14; ++var18) {
                     var2.insertElementAt(var13.nextToken(), var18);
                  }

                  if (var4 != null) {
                     String var16 = var13.nextToken();
                     if (!var1.getName().equals(var16)) {
                        var2.insertElementAt(var16, var18);
                     }
                  }
               }
            } else {
               return var3;
            }
         }
      }
   }

   protected void stripJavaPackage(CompoundType var1, Vector var2) {
      var2.removeAllElements();
      if (var1.isIDLEntity()) {
         String var3 = var1.getRepositoryID().substring(4);
         StringTokenizer var4 = new StringTokenizer(var3, "/");
         if (var4.countTokens() >= 2) {
            while(var4.hasMoreTokens()) {
               var2.addElement(var4.nextToken());
            }

            var2.removeElementAt(var2.size() - 1);
            String var5 = var1.getPackageName();
            if (var5 != null) {
               Vector var6 = new Vector();
               StringTokenizer var7 = new StringTokenizer(var5, ".");

               while(var7.hasMoreTokens()) {
                  var6.addElement(var7.nextToken());
               }

               int var8 = var2.size() - 1;

               for(int var9 = var6.size() - 1; var8 >= 0 && var9 >= 0; --var9) {
                  String var10 = (String)((String)var2.elementAt(var8));
                  String var11 = (String)((String)var6.elementAt(var9));
                  if (!var11.equals(var10)) {
                     break;
                  }

                  --var8;
               }

               for(int var12 = 0; var12 <= var8; ++var12) {
                  var2.removeElementAt(0);
               }

            }
         }
      }
   }

   protected void writeSequence(Generator.OutputType var1, IndentingWriter var2) throws IOException {
      ArrayType var3 = (ArrayType)var1.getType();
      Type var4 = var3.getElementType();
      String var5 = var1.getName();
      int var6 = Integer.parseInt(var5.substring(3, var5.indexOf("_")));
      String var7 = this.unEsc(var4.getIDLName()).replace(' ', '_');
      String var8 = this.getQualifiedIDLName(var4);
      String var9 = var4.getQualifiedName();
      String var10 = var3.getRepositoryID();
      int var11 = var10.indexOf(91);
      int var12 = var10.lastIndexOf(91) + 1;
      StringBuffer var13 = new StringBuffer(var10.substring(0, var11) + var10.substring(var12));

      for(int var14 = 0; var14 < var6; ++var14) {
         var13.insert(var11, '[');
      }

      String var17 = "seq" + var6 + "_" + var7;
      boolean var15 = false;
      if (var4.isCompound()) {
         CompoundType var16 = (CompoundType)var4;
         var15 = var16.isIDLEntity() || var16.isCORBAObject();
      }

      boolean var18 = var4.isCompound() && !this.isSpecialReference(var4) && var6 == 1 && !var15 && !"org.omg.CORBA.Object".equals(var9) && !"java.lang.String".equals(var9);
      this.writeBanner(var3, var6, !this.isException, var2);
      if (var6 == 1 && "java.lang.String".equals(var9)) {
         this.writeIncOrb(var2);
      }

      if ((var6 != 1 || !"org.omg.CORBA.Object".equals(var9)) && (this.isSpecialReference(var4) || var6 > 1 || var15)) {
         this.writeInclude(var3, var6 - 1, !this.isThrown, var2);
      }

      this.writeIfndef(var3, var6, !this.isException, !this.isForward, var2);
      if (var18) {
         this.writeForwardReference(var3, var6 - 1, var2);
      }

      this.writeModule1(var3, var2);
      var2.pln();
      var2.pI();
      var2.p("valuetype " + var17);
      var2.p(" sequence<");
      if (var6 == 1) {
         var2.p(var8);
      } else {
         var2.p("seq" + (var6 - 1) + "_");
         var2.p(var7);
      }

      var2.pln(">;");
      var2.pO();
      var2.pln();
      var2.pln("#pragma ID " + var17 + " \"" + var13 + "\"");
      var2.pln();
      this.writeModule2(var3, var2);
      if (var18) {
         this.writeInclude(var3, var6 - 1, !this.isThrown, var2);
      }

      this.writeEndif(var2);
   }

   protected void writeBoxedIDL(CompoundType var1, IndentingWriter var2) throws IOException {
      String[] var3 = this.getIDLModuleNames(var1);
      int var4 = var3.length;
      String[] var5 = new String[var4 - 3];

      for(int var6 = 0; var6 < var4 - 3; ++var6) {
         var5[var6] = var3[var6 + 3];
      }

      String var8 = this.unEsc(var1.getIDLName());
      this.writeBanner(var1, 0, !this.isException, var2);
      this.writeInclude(var1, var5, var8, var2);
      this.writeIfndef(var1, 0, !this.isException, !this.isForward, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.p("valuetype " + var8 + " ");

      for(int var7 = 0; var7 < var5.length; ++var7) {
         var2.p("::" + var5[var7]);
      }

      var2.pln("::" + var8 + ";");
      var2.pO();
      var2.pln();
      this.writeRepositoryID(var1, var2);
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeEndif(var2);
   }

   protected void writeException(ClassType var1, IndentingWriter var2) throws IOException {
      this.writeBanner(var1, 0, this.isException, var2);
      this.writeIfndef(var1, 0, this.isException, !this.isForward, var2);
      this.writeForwardReference(var1, var2);
      this.writeModule1(var1, var2);
      var2.pln();
      var2.pI();
      var2.pln("exception " + var1.getIDLExceptionName() + " {");
      var2.pln();
      var2.pI();
      var2.pln(var1.getIDLName() + " value;");
      var2.pO();
      var2.pln();
      var2.pln("};");
      var2.pO();
      var2.pln();
      this.writeModule2(var1, var2);
      this.writeInclude(var1, 0, !this.isThrown, var2);
      this.writeEndif(var2);
   }

   protected void writeRepositoryID(Type var1, IndentingWriter var2) throws IOException {
      String var3 = var1.getRepositoryID();
      if (var1.isCompound()) {
         CompoundType var4 = (CompoundType)var1;
         if (var4.isBoxed()) {
            var3 = var4.getBoxedRepositoryID();
         }
      }

      var2.pln("#pragma ID " + var1.getIDLName() + " \"" + var3 + "\"");
   }

   protected void writeInherits(Hashtable var1, boolean var2, IndentingWriter var3) throws IOException {
      int var4 = var1.size();
      boolean var5 = false;
      int var6 = 0;
      if (var4 >= 1) {
         Enumeration var7 = var1.elements();
         CompoundType var8;
         if (var2) {
            while(var7.hasMoreElements()) {
               var8 = (CompoundType)var7.nextElement();
               if (var8.getTypeCode() == 8192) {
                  ++var6;
               }
            }
         }

         int var10 = var4 - var6;
         int var9;
         if (var10 > 0) {
            var3.p(": ");
            var7 = var1.elements();

            while(var7.hasMoreElements()) {
               var8 = (CompoundType)var7.nextElement();
               if (var8.isClass()) {
                  var3.p(this.getQualifiedIDLName(var8));
                  if (var10 > 1) {
                     var3.p(", ");
                  } else if (var4 > 1) {
                     var3.p(" ");
                  }
                  break;
               }
            }

            var9 = 0;
            var7 = var1.elements();

            while(var7.hasMoreElements()) {
               var8 = (CompoundType)var7.nextElement();
               if (!var8.isClass() && var8.getTypeCode() != 8192) {
                  if (var9++ > 0) {
                     var3.p(", ");
                  }

                  var3.p(this.getQualifiedIDLName(var8));
               }
            }
         }

         if (var6 > 0) {
            var3.p(" supports ");
            var9 = 0;
            var7 = var1.elements();

            while(var7.hasMoreElements()) {
               var8 = (CompoundType)var7.nextElement();
               if (var8.getTypeCode() == 8192) {
                  if (var9++ > 0) {
                     var3.p(", ");
                  }

                  var3.p(this.getQualifiedIDLName(var8));
               }
            }
         }

      }
   }

   protected void writeConstant(CompoundType.Member var1, IndentingWriter var2) throws IOException {
      Type var3 = var1.getType();
      var2.p("const ");
      var2.p(this.getQualifiedIDLName(var3));
      var2.p(" " + var1.getIDLName() + " = " + var1.getValue());
      var2.pln(";");
   }

   protected void writeData(CompoundType.Member var1, IndentingWriter var2) throws IOException {
      if (!var1.isInnerClassDeclaration()) {
         Type var3 = var1.getType();
         if (var1.isPublic()) {
            var2.p("public ");
         } else {
            var2.p("private ");
         }

         var2.pln(this.getQualifiedIDLName(var3) + " " + var1.getIDLName() + ";");
      }
   }

   protected void writeAttribute(CompoundType.Method var1, IndentingWriter var2) throws IOException {
      if (var1.getAttributeKind() != 5) {
         Type var3 = var1.getReturnType();
         if (!var1.isReadWriteAttribute()) {
            var2.p("readonly ");
         }

         var2.p("attribute " + this.getQualifiedIDLName(var3) + " ");
         var2.pln(var1.getAttributeName() + ";");
      }
   }

   protected void writeMethod(CompoundType.Method var1, IndentingWriter var2) throws IOException {
      if (var1.isAttribute()) {
         this.writeAttribute(var1, var2);
      } else {
         Type[] var3 = var1.getArguments();
         String[] var4 = var1.getArgumentNames();
         Type var5 = var1.getReturnType();
         Hashtable var6 = new Hashtable();
         this.getExceptions(var1, var6);
         if (var1.isConstructor()) {
            if (this.factory) {
               var2.p("factory " + var1.getIDLName() + "(");
            } else {
               var2.p("init(");
            }
         } else {
            var2.p(this.getQualifiedIDLName(var5));
            var2.p(" " + var1.getIDLName() + "(");
         }

         var2.pI();

         int var7;
         for(var7 = 0; var7 < var3.length; ++var7) {
            if (var7 > 0) {
               var2.pln(",");
            } else {
               var2.pln();
            }

            var2.p("in ");
            var2.p(this.getQualifiedIDLName(var3[var7]));
            var2.p(" " + var4[var7]);
         }

         var2.pO();
         var2.p(" )");
         if (var6.size() > 0) {
            var2.pln(" raises (");
            var2.pI();
            var7 = 0;

            for(Enumeration var8 = var6.elements(); var8.hasMoreElements(); ++var7) {
               ValueType var9 = (ValueType)var8.nextElement();
               if (var7 > 0) {
                  var2.pln(",");
               }

               if (!var9.isIDLEntityException()) {
                  var2.p(var9.getQualifiedIDLExceptionName(true));
               } else if (var9.isCORBAUserException()) {
                  var2.p("::org::omg::CORBA::UserEx");
               } else {
                  String[] var10 = this.getIDLModuleNames(var9);

                  for(int var11 = 0; var11 < var10.length; ++var11) {
                     var2.p("::" + var10[var11]);
                  }

                  var2.p("::" + var9.getName());
               }
            }

            var2.pO();
            var2.p(" )");
         }

         var2.pln(";");
      }
   }

   protected String unEsc(String var1) {
      return var1.startsWith("_") ? var1.substring(1) : var1;
   }

   protected void writeBanner(Type var1, int var2, boolean var3, IndentingWriter var4) throws IOException {
      String[] var5 = this.getIDLModuleNames(var1);
      String var6 = this.unEsc(var1.getIDLName());
      if (var3 && var1.isClass()) {
         ClassType var7 = (ClassType)var1;
         var6 = this.unEsc(var7.getIDLExceptionName());
      }

      if (var2 > 0 && var1.isArray()) {
         Type var10 = var1.getElementType();
         var6 = "seq" + var2 + "_" + this.unEsc(var10.getIDLName().replace(' ', '_'));
      }

      var4.pln("/**");
      var4.p(" * ");

      for(int var11 = 0; var11 < var5.length; ++var11) {
         var4.p(var5[var11] + "/");
      }

      var4.pln(var6 + ".idl");
      var4.pln(" * Generated by rmic -idl. Do not edit");
      String var12 = DateFormat.getDateTimeInstance(0, 0, Locale.getDefault()).format(new Date());
      String var8 = "o'clock";
      int var9 = var12.indexOf(var8);
      var4.p(" * ");
      if (var9 > -1) {
         var4.pln(var12.substring(0, var9) + var12.substring(var9 + var8.length()));
      } else {
         var4.pln(var12);
      }

      var4.pln(" */");
      var4.pln();
   }

   protected void writeIncOrb(IndentingWriter var1) throws IOException {
      var1.pln("#include \"orb.idl\"");
   }

   protected void writeIfndef(Type var1, int var2, boolean var3, boolean var4, IndentingWriter var5) throws IOException {
      String[] var6 = this.getIDLModuleNames(var1);
      String var7 = this.unEsc(var1.getIDLName());
      if (var3 && var1.isClass()) {
         ClassType var8 = (ClassType)var1;
         var7 = this.unEsc(var8.getIDLExceptionName());
      }

      if (var2 > 0 && var1.isArray()) {
         Type var9 = var1.getElementType();
         var7 = "seq" + var2 + "_" + this.unEsc(var9.getIDLName().replace(' ', '_'));
      }

      var5.pln();
      var5.p("#ifndef __");

      int var10;
      for(var10 = 0; var10 < var6.length; ++var10) {
         var5.p(var6[var10] + "_");
      }

      var5.pln(var7 + "__");
      if (!var4) {
         var5.p("#define __");

         for(var10 = 0; var10 < var6.length; ++var10) {
            var5.p(var6[var10] + "_");
         }

         var5.pln(var7 + "__");
         var5.pln();
      }

   }

   protected void writeEndif(IndentingWriter var1) throws IOException {
      var1.pln("#endif");
      var1.pln();
   }

   protected void writeModule1(Type var1, IndentingWriter var2) throws IOException {
      String[] var3 = this.getIDLModuleNames(var1);
      var2.pln();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var2.pln("module " + var3[var4] + " {");
      }

   }

   protected void writeModule2(Type var1, IndentingWriter var2) throws IOException {
      String[] var3 = this.getIDLModuleNames(var1);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var2.pln("};");
      }

      var2.pln();
   }
}
