package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.ConstEntry;
import com.sun.tools.corba.se.idl.EnumEntry;
import com.sun.tools.corba.se.idl.ExceptionEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.NativeEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionBranch;
import com.sun.tools.corba.se.idl.UnionEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import com.sun.tools.corba.se.idl.constExpr.BinaryExpr;
import com.sun.tools.corba.se.idl.constExpr.Divide;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import com.sun.tools.corba.se.idl.constExpr.Minus;
import com.sun.tools.corba.se.idl.constExpr.Not;
import com.sun.tools.corba.se.idl.constExpr.Plus;
import com.sun.tools.corba.se.idl.constExpr.Terminal;
import com.sun.tools.corba.se.idl.constExpr.Times;
import com.sun.tools.corba.se.idl.constExpr.UnaryExpr;
import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

public class Util extends com.sun.tools.corba.se.idl.Util {
   public static final short TypeFile = 0;
   public static final short StubFile = 1;
   public static final short HelperFile = 2;
   public static final short HolderFile = 3;
   public static final short StateFile = 4;
   static Hashtable symbolTable = new Hashtable();
   static Hashtable packageTranslation = new Hashtable();

   public static String getVersion() {
      return com.sun.tools.corba.se.idl.Util.getVersion("com/sun/tools/corba/se/idl/toJavaPortable/toJavaPortable.prp");
   }

   static void setSymbolTable(Hashtable var0) {
      symbolTable = var0;
   }

   public static void setPackageTranslation(Hashtable var0) {
      packageTranslation = var0;
   }

   public static boolean isInterface(String var0) {
      return isInterface(var0, symbolTable);
   }

   static String arrayInfo(Vector var0) {
      int var1 = var0.size();
      String var2 = "";

      for(Enumeration var3 = var0.elements(); var3.hasMoreElements(); var2 = var2 + '[' + parseExpression((Expression)var3.nextElement()) + ']') {
      }

      return var2;
   }

   public static String sansArrayInfo(Vector var0) {
      int var1 = var0.size();
      String var2 = "";

      for(int var3 = 0; var3 < var1; ++var3) {
         var2 = var2 + "[]";
      }

      return var2;
   }

   public static String sansArrayInfo(String var0) {
      int var1 = var0.indexOf(91);
      if (var1 >= 0) {
         String var2 = var0.substring(var1);

         for(var0 = var0.substring(0, var1); !var2.equals(""); var2 = var2.substring(var2.indexOf(93) + 1)) {
            var0 = var0 + "[]";
         }
      }

      return var0;
   }

   public static String fileName(SymtabEntry var0, String var1) {
      NameModifierImpl var2 = new NameModifierImpl();
      return fileName(var0, var2, var1);
   }

   public static String fileName(SymtabEntry var0, NameModifier var1, String var2) {
      String var3 = containerFullName(var0.container());
      if (var3 != null && !var3.equals("")) {
         mkdir(var3);
      }

      String var4 = var0.name();
      var4 = var1.makeName(var4) + var2;
      if (var3 != null && !var3.equals("")) {
         var4 = var3 + '/' + var4;
      }

      return var4.replace('/', File.separatorChar);
   }

   public static GenFileStream stream(SymtabEntry var0, String var1) {
      NameModifierImpl var2 = new NameModifierImpl();
      return stream(var0, var2, var1);
   }

   public static GenFileStream stream(SymtabEntry var0, NameModifier var1, String var2) {
      return getStream(fileName(var0, var1, var2), var0);
   }

   public static GenFileStream getStream(String var0, SymtabEntry var1) {
      String var2 = ((Arguments)Compile.compiler.arguments).targetDir + var0;
      return Compile.compiler.arguments.keepOldFiles && (new File(var2)).exists() ? null : new GenFileStream(var2);
   }

   public static String containerFullName(SymtabEntry var0) {
      String var1 = doContainerFullName(var0);
      if (packageTranslation.size() > 0) {
         var1 = translate(var1);
      }

      return var1;
   }

   public static String translate(String var0) {
      String var1 = var0;
      String var2 = "";

      int var3;
      do {
         String var4 = (String)((String)packageTranslation.get(var1));
         if (var4 != null) {
            return var4 + var2;
         }

         var3 = var1.lastIndexOf(47);
         if (var3 >= 0) {
            var2 = var1.substring(var3) + var2;
            var1 = var1.substring(0, var3);
         }
      } while(var3 >= 0);

      return var0;
   }

   private static String doContainerFullName(SymtabEntry var0) {
      String var1 = "";
      if (var0 == null) {
         var1 = "";
      } else {
         if (!(var0 instanceof InterfaceEntry) && !(var0 instanceof StructEntry) && !(var0 instanceof UnionEntry)) {
            var1 = var0.name();
         } else {
            var1 = var0.name() + "Package";
         }

         if (var0.container() != null && !var0.container().name().equals("")) {
            var1 = doContainerFullName(var0.container()) + '/' + var1;
         }
      }

      return var1;
   }

   public static String javaName(SymtabEntry var0) {
      String var1 = "";
      if (!(var0 instanceof TypedefEntry) && !(var0 instanceof SequenceEntry)) {
         if (var0 instanceof PrimitiveEntry) {
            var1 = javaPrimName(var0.name());
         } else if (var0 instanceof StringEntry) {
            var1 = "String";
         } else if (var0 instanceof NativeEntry) {
            var1 = javaNativeName(var0.name());
         } else if (var0 instanceof ValueEntry && var0.name().equals("ValueBase")) {
            var1 = "java.io.Serializable";
         } else if (var0 instanceof ValueBoxEntry) {
            ValueBoxEntry var2 = (ValueBoxEntry)var0;
            TypedefEntry var3 = ((InterfaceState)var2.state().elementAt(0)).entry;
            SymtabEntry var4 = var3.type();
            if (var4 instanceof PrimitiveEntry) {
               var1 = containerFullName(var0.container());
               if (!var1.equals("")) {
                  var1 = var1 + '.';
               }

               var1 = var1 + var0.name();
            } else {
               var1 = javaName(var4);
            }
         } else {
            var1 = containerFullName(var0.container());
            if (var1.equals("")) {
               var1 = var0.name();
            } else {
               var1 = var1 + '.' + var0.name();
            }
         }
      } else {
         try {
            var1 = sansArrayInfo((String)var0.dynamicVariable(Compile.typedefInfo));
         } catch (NoSuchFieldException var5) {
            var1 = var0.name();
         }
      }

      return var1.replace('/', '.');
   }

   public static String javaPrimName(String var0) {
      if (!var0.equals("long") && !var0.equals("unsigned long")) {
         if (var0.equals("octet")) {
            var0 = "byte";
         } else if (!var0.equals("long long") && !var0.equals("unsigned long long")) {
            if (var0.equals("wchar")) {
               var0 = "char";
            } else if (var0.equals("unsigned short")) {
               var0 = "short";
            } else if (var0.equals("any")) {
               var0 = "org.omg.CORBA.Any";
            } else if (var0.equals("TypeCode")) {
               var0 = "org.omg.CORBA.TypeCode";
            } else if (var0.equals("Principal")) {
               var0 = "org.omg.CORBA.Principal";
            }
         } else {
            var0 = "long";
         }
      } else {
         var0 = "int";
      }

      return var0;
   }

   public static String javaNativeName(String var0) {
      if (!var0.equals("AbstractBase") && !var0.equals("Cookie")) {
         if (var0.equals("Servant")) {
            var0 = "org.omg.PortableServer.Servant";
         } else if (var0.equals("ValueFactory")) {
            var0 = "org.omg.CORBA.portable.ValueFactory";
         }
      } else {
         var0 = "java.lang.Object";
      }

      return var0;
   }

   public static String javaQualifiedName(SymtabEntry var0) {
      String var1 = "";
      if (var0 instanceof PrimitiveEntry) {
         var1 = javaPrimName(var0.name());
      } else if (var0 instanceof StringEntry) {
         var1 = "String";
      } else if (var0 instanceof ValueEntry && var0.name().equals("ValueBase")) {
         var1 = "java.io.Serializable";
      } else {
         SymtabEntry var2 = var0.container();
         if (var2 != null) {
            var1 = var2.name();
         }

         if (var1.equals("")) {
            var1 = var0.name();
         } else {
            var1 = containerFullName(var0.container()) + '.' + var0.name();
         }
      }

      return var1.replace('/', '.');
   }

   public static String collapseName(String var0) {
      if (var0.equals("unsigned short")) {
         var0 = "ushort";
      } else if (var0.equals("unsigned long")) {
         var0 = "ulong";
      } else if (var0.equals("unsigned long long")) {
         var0 = "ulonglong";
      } else if (var0.equals("long long")) {
         var0 = "longlong";
      }

      return var0;
   }

   public static SymtabEntry typeOf(SymtabEntry var0) {
      while(var0 instanceof TypedefEntry && ((TypedefEntry)var0).arrayInfo().isEmpty() && !(var0.type() instanceof SequenceEntry)) {
         var0 = var0.type();
      }

      return var0;
   }

   static void fillInfo(SymtabEntry var0) {
      String var1 = "";
      SymtabEntry var2 = var0;
      boolean var3 = false;

      do {
         try {
            var3 = var2.dynamicVariable(Compile.typedefInfo) != null;
         } catch (NoSuchFieldException var6) {
         }

         if (!var3) {
            if (var2 instanceof TypedefEntry) {
               var1 = var1 + arrayInfo(((TypedefEntry)var2).arrayInfo());
            } else if (var2 instanceof SequenceEntry) {
               Expression var4 = ((SequenceEntry)var2).maxSize();
               if (var4 == null) {
                  var1 = var1 + "[]";
               } else {
                  var1 = var1 + '[' + parseExpression(var4) + ']';
               }
            }

            if (var2.type() != null) {
               var2 = var2.type();
            }
         }
      } while(!var3 && var2 != null && (var2 instanceof TypedefEntry || var2 instanceof SequenceEntry));

      if (var2 instanceof ValueBoxEntry) {
         fillValueBoxInfo((ValueBoxEntry)var2);
      }

      try {
         if (var3) {
            var0.dynamicVariable(Compile.typedefInfo, (String)var2.dynamicVariable(Compile.typedefInfo) + var1);
         } else {
            var0.dynamicVariable(Compile.typedefInfo, javaName(var2) + var1);
         }
      } catch (NoSuchFieldException var5) {
      }

   }

   static void fillValueBoxInfo(ValueBoxEntry var0) {
      TypedefEntry var1 = ((InterfaceState)var0.state().elementAt(0)).entry;
      if (var1.type() != null) {
         fillInfo(var1.type());
      }

      fillInfo(var1);
   }

   public static String holderName(SymtabEntry var0) {
      String var1;
      if (var0 instanceof PrimitiveEntry) {
         if (var0.name().equals("any")) {
            var1 = "org.omg.CORBA.AnyHolder";
         } else if (var0.name().equals("TypeCode")) {
            var1 = "org.omg.CORBA.TypeCodeHolder";
         } else if (var0.name().equals("Principal")) {
            var1 = "org.omg.CORBA.PrincipalHolder";
         } else {
            var1 = "org.omg.CORBA." + capitalize(javaQualifiedName(var0)) + "Holder";
         }
      } else if (var0 instanceof TypedefEntry) {
         TypedefEntry var2 = (TypedefEntry)var0;
         if (var2.arrayInfo().isEmpty() && !(var2.type() instanceof SequenceEntry)) {
            var1 = holderName(var0.type());
         } else {
            var1 = javaQualifiedName(var0) + "Holder";
         }
      } else if (var0 instanceof StringEntry) {
         var1 = "org.omg.CORBA.StringHolder";
      } else if (var0 instanceof ValueEntry) {
         if (var0.name().equals("ValueBase")) {
            var1 = "org.omg.CORBA.ValueBaseHolder";
         } else {
            var1 = javaName(var0) + "Holder";
         }
      } else if (var0 instanceof NativeEntry) {
         var1 = javaQualifiedName(var0) + "Holder";
      } else {
         var1 = javaName(var0) + "Holder";
      }

      return var1;
   }

   public static String helperName(SymtabEntry var0, boolean var1) {
      if (var0 instanceof ValueEntry && var0.name().equals("ValueBase")) {
         return "org.omg.CORBA.ValueBaseHelper";
      } else {
         return var1 ? javaQualifiedName(var0) + "Helper" : javaName(var0) + "Helper";
      }
   }

   public static void writePackage(PrintWriter var0, SymtabEntry var1) {
      writePackage(var0, var1, (short)0);
   }

   public static void writePackage(PrintWriter var0, SymtabEntry var1, String var2, short var3) {
      if (var2 != null && !var2.equals("")) {
         var0.println("package " + var2.replace('/', '.') + ';');
         if (!Compile.compiler.importTypes.isEmpty()) {
            var0.println();
            Vector var4 = addImportLines(var1, Compile.compiler.importTypes, var3);
            printImports(var4, var0);
         }
      }

   }

   public static void writePackage(PrintWriter var0, SymtabEntry var1, short var2) {
      String var3 = containerFullName(var1.container());
      if (var3 != null && !var3.equals("")) {
         var0.println("package " + var3.replace('/', '.') + ';');
         if ((var2 != 3 || var1 instanceof TypedefEntry) && !Compile.compiler.importTypes.isEmpty()) {
            var0.println();
            Vector var4 = addImportLines(var1, Compile.compiler.importTypes, var2);
            printImports(var4, var0);
         }
      }

   }

   private static void printImports(Vector var0, PrintWriter var1) {
      Enumeration var2 = var0.elements();

      while(var2.hasMoreElements()) {
         var1.println("import " + (String)var2.nextElement() + ';');
      }

   }

   private static void addTo(Vector var0, String var1) {
      if (!var1.startsWith("ValueBase") || var1.compareTo("ValueBase") != 0 && var1.compareTo("ValueBaseHolder") != 0 && var1.compareTo("ValueBaseHelper") != 0) {
         if (!var0.contains(var1)) {
            var0.addElement(var1);
         }

      }
   }

   private static Vector addImportLines(SymtabEntry var0, Vector var1, short var2) {
      Vector var3 = new Vector();
      String var19;
      SymtabEntry var22;
      if (var0 instanceof ConstEntry) {
         ConstEntry var4 = (ConstEntry)var0;
         Object var5 = var4.value().value();
         if (var5 instanceof ConstEntry && var1.contains(var5)) {
            addTo(var3, ((ConstEntry)var5).name());
         }
      } else if (var0 instanceof ValueEntry && var2 == 2) {
         if (((ValueEntry)var0).derivedFrom().size() > 0) {
            ValueEntry var18 = (ValueEntry)((ValueEntry)var0).derivedFrom().elementAt(0);
            var19 = var18.name();
            if (!"ValueBase".equals(var19) && var1.contains(var18)) {
               addTo(var3, var19 + "Helper");
            }
         }
      } else {
         SymtabEntry var7;
         Enumeration var17;
         if (var0 instanceof InterfaceEntry && (var2 == 0 || var2 == 1)) {
            InterfaceEntry var16 = (InterfaceEntry)var0;
            Vector var29;
            Enumeration var30;
            if (var16 instanceof ValueEntry) {
               var17 = ((ValueEntry)var16).supports().elements();

               label251:
               while(true) {
                  do {
                     if (!var17.hasMoreElements()) {
                        break label251;
                     }

                     var22 = (SymtabEntry)var17.nextElement();
                     if (var1.contains(var22)) {
                        addTo(var3, var22.name() + "Operations");
                     }
                  } while(var2 != 1);

                  if (var1.contains(var22)) {
                     addTo(var3, var22.name());
                  }

                  var29 = addImportLines(var22, var1, (short)1);
                  var30 = var29.elements();

                  while(var30.hasMoreElements()) {
                     addTo(var3, (String)var30.nextElement());
                  }
               }
            }

            var17 = var16.derivedFrom().elements();

            label233:
            while(true) {
               do {
                  if (!var17.hasMoreElements()) {
                     var17 = var16.methods().elements();

                     while(var17.hasMoreElements()) {
                        MethodEntry var31 = (MethodEntry)var17.nextElement();
                        var7 = typeOf(var31.type());
                        if (var7 != null && var1.contains(var7) && (var2 == 0 || var2 == 1)) {
                           addTo(var3, var7.name());
                           addTo(var3, var7.name() + "Holder");
                           if (var2 == 1) {
                              addTo(var3, var7.name() + "Helper");
                           }
                        }

                        checkForArrays(var7, var1, var3);
                        if (var2 == 1) {
                           checkForBounds(var7, var1, var3);
                        }

                        var30 = var31.exceptions().elements();

                        while(var30.hasMoreElements()) {
                           ExceptionEntry var9 = (ExceptionEntry)var30.nextElement();
                           if (var1.contains(var9)) {
                              addTo(var3, var9.name());
                              addTo(var3, var9.name() + "Helper");
                           }
                        }

                        Enumeration var33 = var31.parameters().elements();

                        while(var33.hasMoreElements()) {
                           ParameterEntry var10 = (ParameterEntry)var33.nextElement();
                           SymtabEntry var11 = typeOf(var10.type());
                           if (var1.contains(var11)) {
                              if (var2 == 1) {
                                 addTo(var3, var11.name() + "Helper");
                              }

                              if (var10.passType() == 0) {
                                 addTo(var3, var11.name());
                              } else {
                                 addTo(var3, var11.name() + "Holder");
                              }
                           }

                           checkForArrays(var11, var1, var3);
                           if (var2 == 1) {
                              checkForBounds(var11, var1, var3);
                           }
                        }
                     }
                     break label233;
                  }

                  var22 = (SymtabEntry)var17.nextElement();
                  if (var1.contains(var22)) {
                     addTo(var3, var22.name());
                     if (!(var22 instanceof ValueEntry)) {
                        addTo(var3, var22.name() + "Operations");
                     }
                  }
               } while(var2 != 1);

               var29 = addImportLines(var22, var1, (short)1);
               var30 = var29.elements();

               while(var30.hasMoreElements()) {
                  addTo(var3, (String)var30.nextElement());
               }
            }
         } else if (var0 instanceof StructEntry) {
            StructEntry var13 = (StructEntry)var0;
            var17 = var13.members().elements();

            while(var17.hasMoreElements()) {
               TypedefEntry var6 = (TypedefEntry)var17.nextElement();
               var7 = var6.type();
               var22 = typeOf(var6);
               if (var1.contains(var22)) {
                  if (!(var22 instanceof TypedefEntry) && !(var22 instanceof ValueBoxEntry)) {
                     addTo(var3, var22.name());
                  }

                  if (var2 == 2) {
                     addTo(var3, var22.name() + "Helper");
                     if (var7 instanceof TypedefEntry) {
                        addTo(var3, var7.name() + "Helper");
                     }
                  }
               }

               checkForArrays(var22, var1, var3);
               checkForBounds(var22, var1, var3);
            }
         } else {
            SymtabEntry var8;
            if (var0 instanceof TypedefEntry) {
               TypedefEntry var14 = (TypedefEntry)var0;
               var19 = checkForArrayBase(var14, var1, var3);
               if (var2 == 2) {
                  checkForArrayDimensions(var19, var1, var3);

                  try {
                     String var23 = (String)var14.dynamicVariable(Compile.typedefInfo);
                     int var24 = var23.indexOf(91);
                     if (var24 >= 0) {
                        var23 = var23.substring(0, var24);
                     }

                     var8 = (SymtabEntry)symbolTable.get(var23);
                     if (var8 != null && var1.contains(var8)) {
                        addTo(var3, var8.name() + "Helper");
                     }
                  } catch (NoSuchFieldException var12) {
                  }

                  checkForBounds(typeOf(var14), var1, var3);
               }

               Vector var25 = addImportLines(var14.type(), var1, var2);
               Enumeration var26 = var25.elements();

               while(var26.hasMoreElements()) {
                  addTo(var3, (String)var26.nextElement());
               }
            } else if (var0 instanceof UnionEntry) {
               UnionEntry var15 = (UnionEntry)var0;
               SymtabEntry var21 = typeOf(var15.type());
               if (var21 instanceof EnumEntry && var1.contains(var21)) {
                  addTo(var3, var21.name());
               }

               Enumeration var27 = var15.branches().elements();

               while(var27.hasMoreElements()) {
                  UnionBranch var28 = (UnionBranch)var27.nextElement();
                  var8 = typeOf(var28.typedef);
                  if (var1.contains(var8)) {
                     addTo(var3, var8.name());
                     if (var2 == 2) {
                        addTo(var3, var8.name() + "Helper");
                     }
                  }

                  checkForArrays(var8, var1, var3);
                  checkForBounds(var8, var1, var3);
               }
            }
         }
      }

      Enumeration var20 = var3.elements();

      while(true) {
         TypedefEntry var32;
         do {
            do {
               do {
                  if (!var20.hasMoreElements()) {
                     return var3;
                  }

                  var19 = (String)var20.nextElement();
                  var22 = (SymtabEntry)symbolTable.get(var19);
               } while(var22 == null);
            } while(!(var22 instanceof TypedefEntry));

            var32 = (TypedefEntry)var22;
         } while(var32.arrayInfo().size() != 0 && var32.type() instanceof SequenceEntry);

         var3.removeElement(var19);
      }
   }

   private static void checkForArrays(SymtabEntry var0, Vector var1, Vector var2) {
      if (var0 instanceof TypedefEntry) {
         TypedefEntry var3 = (TypedefEntry)var0;
         String var4 = checkForArrayBase(var3, var1, var2);
         checkForArrayDimensions(var4, var1, var2);
      }

   }

   private static String checkForArrayBase(TypedefEntry var0, Vector var1, Vector var2) {
      String var3 = "";

      try {
         String var4 = (String)var0.dynamicVariable(Compile.typedefInfo);
         int var5 = var4.indexOf(91);
         if (var5 >= 0) {
            var3 = var4.substring(var5);
            var4 = var4.substring(0, var5);
         }

         SymtabEntry var6 = (SymtabEntry)symbolTable.get(var4);
         if (var6 != null && var1.contains(var6)) {
            addTo(var2, var6.name());
         }
      } catch (NoSuchFieldException var7) {
      }

      return var3;
   }

   private static void checkForArrayDimensions(String var0, Vector var1, Vector var2) {
      while(!var0.equals("")) {
         int var3 = var0.indexOf(93);
         String var4 = var0.substring(1, var3);
         var0 = var0.substring(var3 + 1);
         SymtabEntry var5 = (SymtabEntry)symbolTable.get(var4);
         if (var5 == null) {
            int var6 = var4.lastIndexOf(46);
            if (var6 >= 0) {
               var5 = (SymtabEntry)symbolTable.get(var4.substring(0, var6));
            }
         }

         if (var5 != null && var1.contains(var5)) {
            addTo(var2, var5.name());
         }
      }

   }

   private static void checkForBounds(SymtabEntry var0, Vector var1, Vector var2) {
      SymtabEntry var3;
      for(var3 = var0; var3 instanceof TypedefEntry; var3 = var3.type()) {
      }

      if (var3 instanceof StringEntry && ((StringEntry)var3).maxSize() != null) {
         checkForGlobalConstants(((StringEntry)var3).maxSize().rep(), var1, var2);
      } else if (var3 instanceof SequenceEntry && ((SequenceEntry)var3).maxSize() != null) {
         checkForGlobalConstants(((SequenceEntry)var3).maxSize().rep(), var1, var2);
      }

   }

   private static void checkForGlobalConstants(String var0, Vector var1, Vector var2) {
      StringTokenizer var3 = new StringTokenizer(var0, " +-*()~&|^%<>");

      while(var3.hasMoreTokens()) {
         String var4 = var3.nextToken();
         if (!var4.equals("/")) {
            SymtabEntry var5 = (SymtabEntry)symbolTable.get(var4);
            if (var5 instanceof ConstEntry) {
               int var6 = var4.indexOf(47);
               if (var6 < 0) {
                  if (var1.contains(var5)) {
                     addTo(var2, var5.name());
                  }
               } else {
                  SymtabEntry var7 = (SymtabEntry)symbolTable.get(var4.substring(0, var6));
                  if (var7 instanceof InterfaceEntry && var1.contains(var7)) {
                     addTo(var2, var7.name());
                  }
               }
            }
         }
      }

   }

   public static void writeInitializer(String var0, String var1, String var2, SymtabEntry var3, PrintWriter var4) {
      if (var3 instanceof TypedefEntry) {
         TypedefEntry var5 = (TypedefEntry)var3;
         writeInitializer(var0, var1, var2 + sansArrayInfo(var5.arrayInfo()), var5.type(), var4);
      } else if (var3 instanceof SequenceEntry) {
         writeInitializer(var0, var1, var2 + "[]", var3.type(), var4);
      } else if (var3 instanceof EnumEntry) {
         if (var2.length() > 0) {
            var4.println(var0 + javaName(var3) + ' ' + var1 + var2 + " = null;");
         } else {
            var4.println(var0 + javaName(var3) + ' ' + var1 + " = null;");
         }
      } else if (var3 instanceof PrimitiveEntry) {
         boolean var7 = var2.length() > 0;
         String var6 = javaPrimName(var3.name());
         if (var6.equals("boolean")) {
            var4.println(var0 + "boolean " + var1 + var2 + " = " + (var7 ? "null;" : "false;"));
         } else if (var6.equals("org.omg.CORBA.TypeCode")) {
            var4.println(var0 + "org.omg.CORBA.TypeCode " + var1 + var2 + " = null;");
         } else if (var6.equals("org.omg.CORBA.Any")) {
            var4.println(var0 + "org.omg.CORBA.Any " + var1 + var2 + " = null;");
         } else if (var6.equals("org.omg.CORBA.Principal")) {
            var4.println(var0 + "org.omg.CORBA.Principal " + var1 + var2 + " = null;");
         } else {
            var4.println(var0 + var6 + ' ' + var1 + var2 + " = " + (var7 ? "null;" : '(' + var6 + ")0;"));
         }
      } else {
         var4.println(var0 + javaName(var3) + ' ' + var1 + var2 + " = null;");
      }

   }

   public static void writeInitializer(String var0, String var1, String var2, SymtabEntry var3, String var4, PrintWriter var5) {
      if (var3 instanceof TypedefEntry) {
         TypedefEntry var6 = (TypedefEntry)var3;
         writeInitializer(var0, var1, var2 + sansArrayInfo(var6.arrayInfo()), var6.type(), var4, var5);
      } else if (var3 instanceof SequenceEntry) {
         writeInitializer(var0, var1, var2 + "[]", var3.type(), var4, var5);
      } else if (var3 instanceof EnumEntry) {
         if (var2.length() > 0) {
            var5.println(var0 + javaName(var3) + ' ' + var1 + var2 + " = " + var4 + ';');
         } else {
            var5.println(var0 + javaName(var3) + ' ' + var1 + " = " + var4 + ';');
         }
      } else if (var3 instanceof PrimitiveEntry) {
         boolean var8 = var2.length() > 0;
         String var7 = javaPrimName(var3.name());
         if (var7.equals("boolean")) {
            var5.println(var0 + "boolean " + var1 + var2 + " = " + var4 + ';');
         } else if (var7.equals("org.omg.CORBA.TypeCode")) {
            var5.println(var0 + "org.omg.CORBA.TypeCode " + var1 + var2 + " = " + var4 + ';');
         } else if (var7.equals("org.omg.CORBA.Any")) {
            var5.println(var0 + "org.omg.CORBA.Any " + var1 + var2 + " = " + var4 + ';');
         } else if (var7.equals("org.omg.CORBA.Principal")) {
            var5.println(var0 + "org.omg.CORBA.Principal " + var1 + var2 + " = " + var4 + ';');
         } else {
            var5.println(var0 + var7 + ' ' + var1 + var2 + " = " + var4 + ';');
         }
      } else {
         var5.println(var0 + javaName(var3) + ' ' + var1 + var2 + " = " + var4 + ';');
      }

   }

   public static void mkdir(String var0) {
      String var1 = ((Arguments)Compile.compiler.arguments).targetDir;
      var0 = (var1 + var0).replace('/', File.separatorChar);
      File var2 = new File(var0);
      if (!var2.exists() && !var2.mkdirs()) {
         System.err.println(getMessage("Util.cantCreatePkg", var0));
      }

   }

   public static void writeProlog(PrintWriter var0, String var1) {
      String var2 = ((Arguments)Compile.compiler.arguments).targetDir;
      if (var2 != null) {
         var1 = var1.substring(var2.length());
      }

      var0.println();
      var0.println("/**");
      var0.println("* " + var1.replace(File.separatorChar, '/') + " .");
      var0.println("* " + getMessage("toJavaProlog1", getMessage("Version.product", getMessage("Version.number"))));
      var0.println("* " + getMessage("toJavaProlog2", Compile.compiler.arguments.file.replace(File.separatorChar, '/')));
      DateFormat var3 = DateFormat.getDateTimeInstance(0, 0, Locale.getDefault());
      if (Locale.getDefault() == Locale.JAPAN) {
         var3.setTimeZone(TimeZone.getTimeZone("JST"));
      } else {
         var3.setTimeZone(TimeZone.getDefault());
      }

      var0.println("* " + var3.format(new Date()));
      var0.println("*/");
      var0.println();
   }

   public static String stripLeadingUnderscores(String var0) {
      while(var0.startsWith("_")) {
         var0 = var0.substring(1);
      }

      return var0;
   }

   public static String stripLeadingUnderscoresFromID(String var0) {
      String var1 = "";
      int var2 = var0.indexOf(58);
      if (var2 >= 0) {
         do {
            var1 = var1 + var0.substring(0, var2 + 1);

            for(var0 = var0.substring(var2 + 1); var0.startsWith("_"); var0 = var0.substring(1)) {
            }

            var2 = var0.indexOf(47);
         } while(var2 >= 0);
      }

      return var1 + var0;
   }

   public static String parseExpression(Expression var0) {
      if (var0 instanceof Terminal) {
         return parseTerminal((Terminal)var0);
      } else if (var0 instanceof BinaryExpr) {
         return parseBinary((BinaryExpr)var0);
      } else {
         return var0 instanceof UnaryExpr ? parseUnary((UnaryExpr)var0) : "(UNKNOWN_VALUE)";
      }
   }

   static String parseTerminal(Terminal var0) {
      if (var0.value() instanceof ConstEntry) {
         ConstEntry var5 = (ConstEntry)var0.value();
         return var5.container() instanceof InterfaceEntry ? javaQualifiedName(var5.container()) + '.' + var5.name() : javaQualifiedName(var5) + ".value";
      } else if (var0.value() instanceof Expression) {
         return '(' + parseExpression((Expression)var0.value()) + ')';
      } else if (var0.value() instanceof Character) {
         if ((Character)var0.value() == 11) {
            return "'\\013'";
         } else if ((Character)var0.value() == 7) {
            return "'\\007'";
         } else if (var0.rep().startsWith("'\\x")) {
            return hexToOctal(var0.rep());
         } else {
            return var0.rep().equals("'\\?'") ? "'?'" : var0.rep();
         }
      } else if (var0.value() instanceof Boolean) {
         return var0.value().toString();
      } else if (!(var0.value() instanceof BigInteger)) {
         return var0.rep();
      } else {
         SymtabEntry var1;
         for(var1 = (SymtabEntry)symbolTable.get(var0.type()); var1.type() != null; var1 = var1.type()) {
         }

         String var2 = var1.name();
         int var4;
         if (var2.equals("unsigned long long") && ((BigInteger)var0.value()).compareTo(Expression.llMax) > 0) {
            BigInteger var6 = (BigInteger)var0.value();
            var6 = var6.subtract(Expression.twoPow64);
            var4 = var0.rep().indexOf(41);
            return var4 < 0 ? var6.toString() + 'L' : '(' + var6.toString() + 'L' + ')';
         } else if (var2.indexOf("long long") < 0 && !var2.equals("unsigned long")) {
            return var0.rep();
         } else {
            String var3 = var0.rep();
            var4 = var3.indexOf(41);
            return var4 < 0 ? var3 + 'L' : var3.substring(0, var4) + 'L' + var3.substring(var4);
         }
      }
   }

   static String hexToOctal(String var0) {
      var0 = var0.substring(3, var0.length() - 1);
      return "'\\" + Integer.toString(Integer.parseInt(var0, 16), 8) + "'";
   }

   static String parseBinary(BinaryExpr var0) {
      String var1 = "";
      if (!(var0.value() instanceof Float) && !(var0.value() instanceof Double)) {
         if (var0.value() instanceof Number) {
            if (var0.type().indexOf("long long") >= 0) {
               var1 = "(long)";
            } else {
               var1 = "(int)";
            }
         } else {
            var1 = "";
            System.err.println("Unknown type in constant expression");
         }
      } else {
         var1 = "(double)";
         if (!(var0 instanceof Plus) && !(var0 instanceof Minus) && !(var0 instanceof Times) && !(var0 instanceof Divide)) {
            System.err.println("Operator " + var0.op() + " is invalid on floating point numbers");
         }
      }

      if (var0.type().equals("unsigned long long")) {
         BigInteger var2 = (BigInteger)var0.value();
         if (var2.compareTo(Expression.llMax) > 0) {
            var2 = var2.subtract(Expression.twoPow64);
         }

         return var1 + '(' + var2.toString() + 'L' + ')';
      } else {
         return var1 + '(' + parseExpression(var0.left()) + ' ' + var0.op() + ' ' + parseExpression(var0.right()) + ')';
      }
   }

   static String parseUnary(UnaryExpr var0) {
      if (!(var0.value() instanceof Number)) {
         return "(UNKNOWN_VALUE)";
      } else if ((var0.value() instanceof Float || var0.value() instanceof Double) && var0 instanceof Not) {
         return "(UNKNOWN_VALUE)";
      } else {
         String var1 = "";
         if (!(var0.operand().value() instanceof Float) && !(var0.operand().value() instanceof Double)) {
            if (var0.type().indexOf("long long") >= 0) {
               var1 = "(long)";
            } else {
               var1 = "(int)";
            }
         } else {
            var1 = "(double)";
         }

         if (var0.type().equals("unsigned long long")) {
            BigInteger var2 = (BigInteger)var0.value();
            if (var2.compareTo(Expression.llMax) > 0) {
               var2 = var2.subtract(Expression.twoPow64);
            }

            return var1 + '(' + var2.toString() + 'L' + ')';
         } else {
            return var1 + var0.op() + parseExpression(var0.operand());
         }
      }
   }

   public static boolean IDLEntity(SymtabEntry var0) {
      boolean var1 = true;
      if (!(var0 instanceof PrimitiveEntry) && !(var0 instanceof StringEntry)) {
         if (var0 instanceof TypedefEntry) {
            var1 = IDLEntity(var0.type());
         }
      } else {
         var1 = false;
      }

      return var1;
   }

   public static boolean corbaLevel(float var0, float var1) {
      float var2 = Compile.compiler.arguments.corbaLevel;
      float var3 = 0.001F;
      return var2 - var0 + var3 >= 0.0F && var1 - var2 + var3 >= 0.0F;
   }
}
