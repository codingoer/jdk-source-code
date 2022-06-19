package com.sun.tools.corba.se.idl.toJavaPortable;

import com.sun.tools.corba.se.idl.AttributeEntry;
import com.sun.tools.corba.se.idl.ExceptionEntry;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class MethodGen implements com.sun.tools.corba.se.idl.MethodGen {
   private static final String ONE_INDENT = "    ";
   private static final String TWO_INDENT = "        ";
   private static final String THREE_INDENT = "            ";
   private static final String FOUR_INDENT = "                ";
   private static final String FIVE_INDENT = "                    ";
   private static final int ATTRIBUTE_METHOD_PREFIX_LENGTH = 5;
   protected int methodIndex = 0;
   protected String realName = "";
   protected Hashtable symbolTable = null;
   protected MethodEntry m = null;
   protected PrintWriter stream = null;
   protected boolean localOptimization = false;
   protected boolean isAbstract = false;

   public void generate(Hashtable var1, MethodEntry var2, PrintWriter var3) {
   }

   protected void interfaceMethod(Hashtable var1, MethodEntry var2, PrintWriter var3) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      if (var2.comment() != null) {
         var2.comment().generate("", var3);
      }

      var3.print("  ");
      SymtabEntry var4 = var2.container();
      boolean var5 = false;
      boolean var6 = false;
      if (var4 instanceof ValueEntry) {
         var5 = ((ValueEntry)var4).isAbstract();
         var6 = true;
      }

      if (var6 && !var5) {
         var3.print("public ");
      }

      this.writeMethodSignature();
      if (var6 && !var5) {
         var3.println();
         var3.println("  {");
         var3.println("  }");
         var3.println();
      } else {
         var3.println(";");
      }

   }

   protected void stub(String var1, boolean var2, Hashtable var3, MethodEntry var4, PrintWriter var5, int var6) {
      this.localOptimization = ((Arguments)Compile.compiler.arguments).LocalOptimization;
      this.isAbstract = var2;
      this.symbolTable = var3;
      this.m = var4;
      this.stream = var5;
      this.methodIndex = var6;
      if (var4.comment() != null) {
         var4.comment().generate("  ", var5);
      }

      var5.print("  public ");
      this.writeMethodSignature();
      var5.println();
      var5.println("  {");
      this.writeStubBody(var1);
      var5.println("  } // " + var4.name());
      var5.println();
   }

   protected void localstub(Hashtable var1, MethodEntry var2, PrintWriter var3, int var4, InterfaceEntry var5) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      this.methodIndex = var4;
      if (var2.comment() != null) {
         var2.comment().generate("  ", var3);
      }

      var3.print("  public ");
      this.writeMethodSignature();
      var3.println();
      var3.println("  {");
      this.writeLocalStubBody(var5);
      var3.println("  } // " + var2.name());
      var3.println();
   }

   protected void skeleton(Hashtable var1, MethodEntry var2, PrintWriter var3, int var4) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      this.methodIndex = var4;
      if (var2.comment() != null) {
         var2.comment().generate("  ", var3);
      }

      var3.print("  public ");
      this.writeMethodSignature();
      var3.println();
      var3.println("  {");
      this.writeSkeletonBody();
      var3.println("  } // " + var2.name());
   }

   protected void dispatchSkeleton(Hashtable var1, MethodEntry var2, PrintWriter var3, int var4) {
      this.symbolTable = var1;
      this.m = var2;
      this.stream = var3;
      this.methodIndex = var4;
      if (var2.comment() != null) {
         var2.comment().generate("  ", var3);
      }

      this.writeDispatchCall();
   }

   protected boolean isValueInitializer() {
      MethodEntry var1 = null;
      if (this.m.container() instanceof ValueEntry) {
         for(Enumeration var2 = ((ValueEntry)this.m.container()).initializers().elements(); var1 != this.m && var2.hasMoreElements(); var1 = (MethodEntry)var2.nextElement()) {
         }
      }

      return var1 == this.m && null != this.m;
   }

   protected void writeMethodSignature() {
      boolean var1 = this.isValueInitializer();
      if (this.m.type() == null) {
         if (!var1) {
            this.stream.print("void");
         }
      } else {
         this.stream.print(Util.javaName(this.m.type()));
      }

      if (var1) {
         this.stream.print(' ' + this.m.container().name() + " (");
      } else {
         this.stream.print(' ' + this.m.name() + " (");
      }

      boolean var2 = true;
      Enumeration var3 = this.m.parameters().elements();

      while(var3.hasMoreElements()) {
         if (var2) {
            var2 = false;
         } else {
            this.stream.print(", ");
         }

         ParameterEntry var4 = (ParameterEntry)var3.nextElement();
         this.writeParmType(var4.type(), var4.passType());
         this.stream.print(' ' + var4.name());
      }

      if (this.m.contexts().size() > 0) {
         if (!var2) {
            this.stream.print(", ");
         }

         this.stream.print("org.omg.CORBA.Context $context");
      }

      if (this.m.exceptions().size() > 0) {
         this.stream.print(") throws ");
         var3 = this.m.exceptions().elements();

         for(var2 = true; var3.hasMoreElements(); this.stream.print(Util.javaName((SymtabEntry)var3.nextElement()))) {
            if (var2) {
               var2 = false;
            } else {
               this.stream.print(", ");
            }
         }
      } else {
         this.stream.print(')');
      }

   }

   protected void writeParmType(SymtabEntry var1, int var2) {
      if (var2 != 0) {
         var1 = Util.typeOf(var1);
         this.stream.print(Util.holderName(var1));
      } else {
         this.stream.print(Util.javaName(var1));
      }

   }

   protected void writeDispatchCall() {
      String var1 = "       ";
      String var2 = this.m.fullName();
      if (this.m instanceof AttributeEntry) {
         int var3 = var2.lastIndexOf(47) + 1;
         if (this.m.type() == null) {
            var2 = var2.substring(0, var3) + "_set_" + this.m.name();
         } else {
            var2 = var2.substring(0, var3) + "_get_" + this.m.name();
         }
      }

      this.stream.println(var1 + "case " + this.methodIndex + ":  // " + var2);
      this.stream.println(var1 + "{");
      var1 = var1 + "  ";
      if (this.m.exceptions().size() > 0) {
         this.stream.println(var1 + "try {");
         var1 = var1 + "  ";
      }

      SymtabEntry var14 = Util.typeOf(this.m.type());
      Enumeration var4 = this.m.parameters().elements();
      var4 = this.m.parameters().elements();

      while(var4.hasMoreElements()) {
         ParameterEntry var5 = (ParameterEntry)var4.nextElement();
         String var6 = var5.name();
         (new StringBuilder()).append('_').append(var6).toString();
         SymtabEntry var8 = var5.type();
         int var9 = var5.passType();
         if (var9 == 0) {
            Util.writeInitializer(var1, var6, "", var8, this.writeInputStreamRead("in", var8), this.stream);
         } else {
            String var10 = Util.holderName(var8);
            this.stream.println(var1 + var10 + ' ' + var6 + " = new " + var10 + " ();");
            if (var9 == 1) {
               if (var8 instanceof ValueBoxEntry) {
                  ValueBoxEntry var11 = (ValueBoxEntry)var8;
                  TypedefEntry var12 = ((InterfaceState)var11.state().elementAt(0)).entry;
                  SymtabEntry var13 = var12.type();
                  if (var13 instanceof PrimitiveEntry) {
                     this.stream.println(var1 + var6 + ".value = (" + this.writeInputStreamRead("in", var5.type()) + ").value;");
                  } else {
                     this.stream.println(var1 + var6 + ".value = " + this.writeInputStreamRead("in", var5.type()) + ";");
                  }
               } else {
                  this.stream.println(var1 + var6 + ".value = " + this.writeInputStreamRead("in", var5.type()) + ";");
               }
            }
         }
      }

      if (this.m.contexts().size() > 0) {
         this.stream.println(var1 + "org.omg.CORBA.Context $context = in.read_Context ();");
      }

      if (var14 != null) {
         Util.writeInitializer(var1, "$result", "", var14, this.stream);
      }

      this.writeMethodCall(var1);
      var4 = this.m.parameters().elements();

      boolean var15;
      ParameterEntry var16;
      for(var15 = true; var4.hasMoreElements(); this.stream.print(var16.name())) {
         var16 = (ParameterEntry)var4.nextElement();
         if (var15) {
            var15 = false;
         } else {
            this.stream.print(", ");
         }
      }

      if (this.m.contexts().size() > 0) {
         if (!var15) {
            this.stream.print(", ");
         }

         this.stream.print("$context");
      }

      this.stream.println(");");
      this.writeCreateReply(var1);
      if (var14 != null) {
         this.writeOutputStreamWrite(var1, "out", "$result", var14, this.stream);
      }

      var4 = this.m.parameters().elements();

      while(var4.hasMoreElements()) {
         var16 = (ParameterEntry)var4.nextElement();
         int var7 = var16.passType();
         if (var7 != 0) {
            this.writeOutputStreamWrite(var1, "out", var16.name() + ".value", var16.type(), this.stream);
         }
      }

      if (this.m.exceptions().size() > 0) {
         Enumeration var17 = this.m.exceptions().elements();

         while(var17.hasMoreElements()) {
            var1 = "         ";
            ExceptionEntry var18 = (ExceptionEntry)var17.nextElement();
            String var19 = Util.javaQualifiedName(var18);
            this.stream.println(var1 + "} catch (" + var19 + " $ex) {");
            var1 = var1 + "  ";
            this.stream.println(var1 + "out = $rh.createExceptionReply ();");
            this.stream.println(var1 + Util.helperName(var18, true) + ".write (out, $ex);");
         }

         var1 = "         ";
         this.stream.println(var1 + "}");
      }

      this.stream.println("         break;");
      this.stream.println("       }");
      this.stream.println();
   }

   protected void writeStubBody(String var1) {
      String var2 = Util.stripLeadingUnderscores(this.m.name());
      if (this.m instanceof AttributeEntry) {
         if (this.m.type() == null) {
            var2 = "_set_" + var2;
         } else {
            var2 = "_get_" + var2;
         }
      }

      if (this.localOptimization && !this.isAbstract) {
         this.stream.println("    while(true) {");
         this.stream.println("        if(!this._is_local()) {");
      }

      this.stream.println("            org.omg.CORBA.portable.InputStream $in = null;");
      this.stream.println("            try {");
      this.stream.println("                org.omg.CORBA.portable.OutputStream $out = _request (\"" + var2 + "\", " + !this.m.oneway() + ");");
      Enumeration var3 = this.m.parameters().elements();

      while(true) {
         ParameterEntry var4;
         SymtabEntry var5;
         do {
            do {
               if (!var3.hasMoreElements()) {
                  var3 = this.m.parameters().elements();

                  while(var3.hasMoreElements()) {
                     var4 = (ParameterEntry)var3.nextElement();
                     if (var4.passType() == 0) {
                        this.writeOutputStreamWrite("                ", "$out", var4.name(), var4.type(), this.stream);
                     } else if (var4.passType() == 1) {
                        this.writeOutputStreamWrite("                ", "$out", var4.name() + ".value", var4.type(), this.stream);
                     }
                  }

                  if (this.m.contexts().size() > 0) {
                     this.stream.println("                org.omg.CORBA.ContextList $contextList =_orb ().create_context_list ();");

                     for(int var9 = 0; var9 < this.m.contexts().size(); ++var9) {
                        this.stream.println("                $contextList.add (\"" + this.m.contexts().elementAt(var9) + "\");");
                     }

                     this.stream.println("                $out.write_Context ($context, $contextList);");
                  }

                  this.stream.println("                $in = _invoke ($out);");
                  SymtabEntry var10 = this.m.type();
                  if (var10 != null) {
                     Util.writeInitializer("                ", "$result", "", var10, this.writeInputStreamRead("$in", var10), this.stream);
                  }

                  var3 = this.m.parameters().elements();

                  ParameterEntry var11;
                  while(var3.hasMoreElements()) {
                     var11 = (ParameterEntry)var3.nextElement();
                     if (var11.passType() != 0) {
                        if (var11.type() instanceof ValueBoxEntry) {
                           ValueBoxEntry var13 = (ValueBoxEntry)var11.type();
                           TypedefEntry var7 = ((InterfaceState)var13.state().elementAt(0)).entry;
                           SymtabEntry var8 = var7.type();
                           if (var8 instanceof PrimitiveEntry) {
                              this.stream.println("                " + var11.name() + ".value = (" + this.writeInputStreamRead("$in", var11.type()) + ").value;");
                           } else {
                              this.stream.println("                " + var11.name() + ".value = " + this.writeInputStreamRead("$in", var11.type()) + ";");
                           }
                        } else {
                           this.stream.println("                " + var11.name() + ".value = " + this.writeInputStreamRead("$in", var11.type()) + ";");
                        }
                     }
                  }

                  var3 = this.m.parameters().elements();

                  while(true) {
                     SymtabEntry var15;
                     do {
                        do {
                           if (!var3.hasMoreElements()) {
                              if (var10 instanceof StringEntry) {
                                 StringEntry var12 = (StringEntry)var10;
                                 if (var12.maxSize() != null) {
                                    this.stream.println("                if ($result.length () > (" + Util.parseExpression(var12.maxSize()) + "))");
                                    this.stream.println("                    throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_NO);");
                                 }
                              }

                              if (var10 != null) {
                                 this.stream.println("                return $result;");
                              } else {
                                 this.stream.println("                return;");
                              }

                              this.stream.println("            } catch (org.omg.CORBA.portable.ApplicationException $ex) {");
                              this.stream.println("                $in = $ex.getInputStream ();");
                              this.stream.println("                String _id = $ex.getId ();");
                              if (this.m.exceptions().size() > 0) {
                                 Enumeration var14 = this.m.exceptions().elements();
                                 boolean var17 = true;

                                 while(var14.hasMoreElements()) {
                                    ExceptionEntry var20 = (ExceptionEntry)var14.nextElement();
                                    if (var17) {
                                       this.stream.print("                if ");
                                       var17 = false;
                                    } else {
                                       this.stream.print("                else if ");
                                    }

                                    this.stream.println("(_id.equals (\"" + var20.repositoryID().ID() + "\"))");
                                    this.stream.println("                    throw " + Util.helperName(var20, false) + ".read ($in);");
                                 }

                                 this.stream.println("                else");
                                 this.stream.println("                    throw new org.omg.CORBA.MARSHAL (_id);");
                              } else {
                                 this.stream.println("                throw new org.omg.CORBA.MARSHAL (_id);");
                              }

                              this.stream.println("            } catch (org.omg.CORBA.portable.RemarshalException $rm) {");
                              this.stream.print("                ");
                              if (this.m.type() != null) {
                                 this.stream.print("return ");
                              }

                              this.stream.print(this.m.name() + " (");
                              boolean var16 = true;
                              Enumeration var19 = this.m.parameters().elements();

                              while(var19.hasMoreElements()) {
                                 if (var16) {
                                    var16 = false;
                                 } else {
                                    this.stream.print(", ");
                                 }

                                 ParameterEntry var21 = (ParameterEntry)var19.nextElement();
                                 this.stream.print(var21.name());
                              }

                              if (this.m.contexts().size() > 0) {
                                 if (!var16) {
                                    this.stream.print(", ");
                                 }

                                 this.stream.print("$context");
                              }

                              this.stream.println("        );");
                              this.stream.println("            } finally {");
                              this.stream.println("                _releaseReply ($in);");
                              this.stream.println("            }");
                              if (this.localOptimization && !this.isAbstract) {
                                 this.stream.println("        }");
                                 this.writeStubBodyForLocalInvocation(var1, var2);
                              }

                              return;
                           }

                           var11 = (ParameterEntry)var3.nextElement();
                           var15 = Util.typeOf(var11.type());
                        } while(!(var15 instanceof StringEntry));
                     } while(var11.passType() != 2 && var11.passType() != 1);

                     StringEntry var18 = (StringEntry)var15;
                     if (var18.maxSize() != null) {
                        this.stream.print("                if (" + var11.name() + ".value.length ()");
                        this.stream.println("         > (" + Util.parseExpression(var18.maxSize()) + "))");
                        this.stream.println("                    throw new org.omg.CORBA.MARSHAL(0,org.omg.CORBA.CompletionStatus.COMPLETED_NO);");
                     }
                  }
               }

               var4 = (ParameterEntry)var3.nextElement();
               var5 = Util.typeOf(var4.type());
            } while(!(var5 instanceof StringEntry));
         } while(var4.passType() != 0 && var4.passType() != 1);

         StringEntry var6 = (StringEntry)var5;
         if (var6.maxSize() != null) {
            this.stream.print("            if (" + var4.name());
            if (var4.passType() == 1) {
               this.stream.print(".value");
            }

            this.stream.print(" == null || " + var4.name());
            if (var4.passType() == 1) {
               this.stream.print(".value");
            }

            this.stream.println(".length () > (" + Util.parseExpression(var6.maxSize()) + "))");
            this.stream.println("            throw new org.omg.CORBA.BAD_PARAM (0, org.omg.CORBA.CompletionStatus.COMPLETED_NO);");
         }
      }
   }

   private void writeStubBodyForLocalInvocation(String var1, String var2) {
      this.stream.println("        else {");
      this.stream.println("            org.omg.CORBA.portable.ServantObject _so =");
      this.stream.println("                _servant_preinvoke(\"" + var2 + "\", _opsClass);");
      this.stream.println("            if (_so == null ) {");
      this.stream.println("                continue;");
      this.stream.println("            }");
      this.stream.println("            " + var1 + "Operations _self =");
      this.stream.println("                (" + var1 + "Operations) _so.servant;");
      this.stream.println("            try {");
      Enumeration var3 = this.m.parameters().elements();
      if (this.m instanceof AttributeEntry) {
         var2 = var2.substring(5);
      }

      boolean var4 = this.m.type() == null;
      if (!var4) {
         this.stream.println("                " + Util.javaName(this.m.type()) + " $result;");
      }

      if (!this.isValueInitializer()) {
         if (var4) {
            this.stream.print("                _self." + var2 + "( ");
         } else {
            this.stream.print("                $result = _self." + var2 + "( ");
         }

         while(var3.hasMoreElements()) {
            ParameterEntry var5 = (ParameterEntry)var3.nextElement();
            if (var3.hasMoreElements()) {
               this.stream.print(" " + var5.name() + ",");
            } else {
               this.stream.print(" " + var5.name());
            }
         }

         this.stream.print(");");
         this.stream.println(" ");
         if (var4) {
            this.stream.println("                return;");
         } else {
            this.stream.println("                return $result;");
         }
      }

      this.stream.println(" ");
      this.stream.println("            }");
      this.stream.println("            finally {");
      this.stream.println("                _servant_postinvoke(_so);");
      this.stream.println("            }");
      this.stream.println("        }");
      this.stream.println("    }");
   }

   protected void writeLocalStubBody(InterfaceEntry var1) {
      String var2 = Util.stripLeadingUnderscores(this.m.name());
      if (this.m instanceof AttributeEntry) {
         if (this.m.type() == null) {
            var2 = "_set_" + var2;
         } else {
            var2 = "_get_" + var2;
         }
      }

      this.stream.println("      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke (\"" + var2 + "\", _opsClass);");
      String var3 = var1.name() + "Operations";
      this.stream.println("      " + var3 + "  $self = (" + var3 + ") $so.servant;");
      this.stream.println();
      this.stream.println("      try {");
      this.stream.print("         ");
      if (this.m.type() != null) {
         this.stream.print("return ");
      }

      this.stream.print("$self." + this.m.name() + " (");
      boolean var4 = true;
      Enumeration var5 = this.m.parameters().elements();

      while(var5.hasMoreElements()) {
         if (var4) {
            var4 = false;
         } else {
            this.stream.print(", ");
         }

         ParameterEntry var6 = (ParameterEntry)var5.nextElement();
         this.stream.print(var6.name());
      }

      if (this.m.contexts().size() > 0) {
         if (!var4) {
            this.stream.print(", ");
         }

         this.stream.print("$context");
      }

      this.stream.println(");");
      this.stream.println("      } finally {");
      this.stream.println("          _servant_postinvoke ($so);");
      this.stream.println("      }");
   }

   private void writeInsert(String var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      String var6 = var4.name();
      if (var4 instanceof PrimitiveEntry) {
         if (var6.equals("long long")) {
            var5.println(var1 + var3 + ".insert_longlong (" + var2 + ");");
         } else if (var6.equals("unsigned short")) {
            var5.println(var1 + var3 + ".insert_ushort (" + var2 + ");");
         } else if (var6.equals("unsigned long")) {
            var5.println(var1 + var3 + ".insert_ulong (" + var2 + ");");
         } else if (var6.equals("unsigned long long")) {
            var5.println(var1 + var3 + ".insert_ulonglong (" + var2 + ");");
         } else {
            var5.println(var1 + var3 + ".insert_" + var6 + " (" + var2 + ");");
         }
      } else if (var4 instanceof StringEntry) {
         var5.println(var1 + var3 + ".insert_" + var6 + " (" + var2 + ");");
      } else {
         var5.println(var1 + Util.helperName(var4, true) + ".insert (" + var3 + ", " + var2 + ");");
      }

   }

   private void writeType(String var1, String var2, SymtabEntry var3, PrintWriter var4) {
      if (var3 instanceof PrimitiveEntry) {
         if (var3.name().equals("long long")) {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_longlong));");
         } else if (var3.name().equals("unsigned short")) {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort));");
         } else if (var3.name().equals("unsigned long")) {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong));");
         } else if (var3.name().equals("unsigned long long")) {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulonglong));");
         } else {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_" + var3.name() + "));");
         }
      } else if (var3 instanceof StringEntry) {
         StringEntry var5 = (StringEntry)var3;
         Expression var6 = var5.maxSize();
         if (var6 == null) {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().create_" + var3.name() + "_tc (" + Util.parseExpression(var6) + "));");
         } else {
            var4.println(var1 + var2 + " (org.omg.CORBA.ORB.init ().create_" + var3.name() + "_tc (0));");
         }
      } else {
         var4.println(var1 + var2 + '(' + Util.helperName(var3, true) + ".type ());");
      }

   }

   private void writeExtract(String var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      if (var4 instanceof PrimitiveEntry) {
         if (var4.name().equals("long long")) {
            var5.println(var1 + var2 + " = " + var3 + ".extract_longlong ();");
         } else if (var4.name().equals("unsigned short")) {
            var5.println(var1 + var2 + " = " + var3 + ".extract_ushort ();");
         } else if (var4.name().equals("unsigned long")) {
            var5.println(var1 + var2 + " = " + var3 + ".extract_ulong ();");
         } else if (var4.name().equals("unsigned long long")) {
            var5.println(var1 + var2 + " = " + var3 + ".extract_ulonglong ();");
         } else {
            var5.println(var1 + var2 + " = " + var3 + ".extract_" + var4.name() + " ();");
         }
      } else if (var4 instanceof StringEntry) {
         var5.println(var1 + var2 + " = " + var3 + ".extract_" + var4.name() + " ();");
      } else {
         var5.println(var1 + var2 + " = " + Util.helperName(var4, true) + ".extract (" + var3 + ");");
      }

   }

   private String writeExtract(String var1, SymtabEntry var2) {
      String var3;
      if (var2 instanceof PrimitiveEntry) {
         if (var2.name().equals("long long")) {
            var3 = var1 + ".extract_longlong ()";
         } else if (var2.name().equals("unsigned short")) {
            var3 = var1 + ".extract_ushort ()";
         } else if (var2.name().equals("unsigned long")) {
            var3 = var1 + ".extract_ulong ()";
         } else if (var2.name().equals("unsigned long long")) {
            var3 = var1 + ".extract_ulonglong ()";
         } else {
            var3 = var1 + ".extract_" + var2.name() + " ()";
         }
      } else if (var2 instanceof StringEntry) {
         var3 = var1 + ".extract_" + var2.name() + " ()";
      } else {
         var3 = Util.helperName(var2, true) + ".extract (" + var1 + ')';
      }

      return var3;
   }

   private void writeSkeletonBody() {
      SymtabEntry var1 = Util.typeOf(this.m.type());
      this.stream.print("    ");
      if (var1 != null) {
         this.stream.print("return ");
      }

      this.stream.print("_impl." + this.m.name() + '(');
      Enumeration var2 = this.m.parameters().elements();

      boolean var3;
      ParameterEntry var4;
      for(var3 = true; var2.hasMoreElements(); this.stream.print(var4.name())) {
         var4 = (ParameterEntry)var2.nextElement();
         if (var3) {
            var3 = false;
         } else {
            this.stream.print(", ");
         }
      }

      if (this.m.contexts().size() != 0) {
         if (!var3) {
            this.stream.print(", ");
         }

         this.stream.print("$context");
      }

      this.stream.println(");");
   }

   protected String passType(int var1) {
      String var2;
      switch (var1) {
         case 0:
         default:
            var2 = "org.omg.CORBA.ARG_IN.value";
            break;
         case 1:
            var2 = "org.omg.CORBA.ARG_INOUT.value";
            break;
         case 2:
            var2 = "org.omg.CORBA.ARG_OUT.value";
      }

      return var2;
   }

   protected void serverMethodName(String var1) {
      this.realName = var1 == null ? "" : var1;
   }

   private void writeOutputStreamWrite(String var1, String var2, String var3, SymtabEntry var4, PrintWriter var5) {
      String var6 = var4.name();
      var5.print(var1);
      if (var4 instanceof PrimitiveEntry) {
         if (var6.equals("long long")) {
            var5.println(var2 + ".write_longlong (" + var3 + ");");
         } else if (var6.equals("unsigned short")) {
            var5.println(var2 + ".write_ushort (" + var3 + ");");
         } else if (var6.equals("unsigned long")) {
            var5.println(var2 + ".write_ulong (" + var3 + ");");
         } else if (var6.equals("unsigned long long")) {
            var5.println(var2 + ".write_ulonglong (" + var3 + ");");
         } else {
            var5.println(var2 + ".write_" + var6 + " (" + var3 + ");");
         }
      } else if (var4 instanceof StringEntry) {
         var5.println(var2 + ".write_" + var6 + " (" + var3 + ");");
      } else if (var4 instanceof SequenceEntry) {
         var5.println(var2 + ".write_" + var4.type().name() + " (" + var3 + ");");
      } else if (var4 instanceof ValueBoxEntry) {
         ValueBoxEntry var7 = (ValueBoxEntry)var4;
         TypedefEntry var8 = ((InterfaceState)var7.state().elementAt(0)).entry;
         SymtabEntry var9 = var8.type();
         if (var9 instanceof PrimitiveEntry && var3.endsWith(".value")) {
            var5.println(Util.helperName(var4, true) + ".write (" + var2 + ",  new " + Util.javaQualifiedName(var4) + " (" + var3 + "));");
         } else {
            var5.println(Util.helperName(var4, true) + ".write (" + var2 + ", " + var3 + ");");
         }
      } else if (var4 instanceof ValueEntry) {
         var5.println(Util.helperName(var4, true) + ".write (" + var2 + ", " + var3 + ");");
      } else {
         var5.println(Util.helperName(var4, true) + ".write (" + var2 + ", " + var3 + ");");
      }

   }

   private String writeInputStreamRead(String var1, SymtabEntry var2) {
      String var3 = "";
      if (var2 instanceof PrimitiveEntry) {
         if (var2.name().equals("long long")) {
            var3 = var1 + ".read_longlong ()";
         } else if (var2.name().equals("unsigned short")) {
            var3 = var1 + ".read_ushort ()";
         } else if (var2.name().equals("unsigned long")) {
            var3 = var1 + ".read_ulong ()";
         } else if (var2.name().equals("unsigned long long")) {
            var3 = var1 + ".read_ulonglong ()";
         } else {
            var3 = var1 + ".read_" + var2.name() + " ()";
         }
      } else if (var2 instanceof StringEntry) {
         var3 = var1 + ".read_" + var2.name() + " ()";
      } else {
         var3 = Util.helperName(var2, true) + ".read (" + var1 + ')';
      }

      return var3;
   }

   protected void writeMethodCall(String var1) {
      SymtabEntry var2 = Util.typeOf(this.m.type());
      if (var2 == null) {
         this.stream.print(var1 + "this." + this.m.name() + " (");
      } else {
         this.stream.print(var1 + "$result = this." + this.m.name() + " (");
      }

   }

   protected void writeCreateReply(String var1) {
      this.stream.println(var1 + "out = $rh.createReply();");
   }
}
