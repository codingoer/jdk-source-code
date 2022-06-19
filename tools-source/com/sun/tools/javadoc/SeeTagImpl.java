package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.tools.javac.code.Printer;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.LayoutCharacters;
import com.sun.tools.javac.util.ListBuffer;
import java.io.File;
import java.util.Locale;

class SeeTagImpl extends TagImpl implements SeeTag, LayoutCharacters {
   private String where;
   private String what;
   private PackageDoc referencedPackage;
   private ClassDoc referencedClass;
   private MemberDoc referencedMember;
   String label = "";
   private static final boolean showRef = false;

   SeeTagImpl(DocImpl var1, String var2, String var3) {
      super(var1, var2, var3);
      this.parseSeeString();
      if (this.where != null) {
         ClassDocImpl var4 = null;
         if (var1 instanceof MemberDoc) {
            var4 = (ClassDocImpl)((ProgramElementDoc)var1).containingClass();
         } else if (var1 instanceof ClassDoc) {
            var4 = (ClassDocImpl)var1;
         }

         this.findReferenced(var4);
      }

   }

   private void showRef() {
      Object var1;
      if (this.referencedMember != null) {
         if (this.referencedMember instanceof MethodDocImpl) {
            var1 = ((MethodDocImpl)this.referencedMember).sym;
         } else if (this.referencedMember instanceof FieldDocImpl) {
            var1 = ((FieldDocImpl)this.referencedMember).sym;
         } else {
            var1 = ((ConstructorDocImpl)this.referencedMember).sym;
         }
      } else if (this.referencedClass != null) {
         var1 = ((ClassDocImpl)this.referencedClass).tsym;
      } else {
         if (this.referencedPackage == null) {
            return;
         }

         var1 = ((PackageDocImpl)this.referencedPackage).sym;
      }

      final JavacMessages var2 = JavacMessages.instance(this.docenv().context);
      Locale var3 = Locale.getDefault();
      Printer var4 = new Printer() {
         int count;

         protected String localize(Locale var1, String var2x, Object... var3) {
            return var2.getLocalizedString(var1, var2x, var3);
         }

         protected String capturedVarId(Type.CapturedType var1, Locale var2x) {
            return "CAP#" + ++this.count;
         }
      };
      String var5 = this.text.replaceAll("\\s+", " ");
      int var6 = var5.indexOf(" ");
      int var7 = var5.indexOf("(");
      int var8 = var5.indexOf(")");
      String var9 = var6 == -1 ? var5 : (var7 != -1 && var6 >= var7 ? var5.substring(0, var8 + 1) : var5.substring(0, var6));
      File var10 = new File(this.holder.position().file().getAbsoluteFile().toURI().normalize());
      StringBuilder var11 = new StringBuilder();
      var11.append("+++ ").append(var10).append(": ").append(this.name()).append(" ").append(var9).append(": ");
      var11.append(((Symbol)var1).getKind()).append(" ");
      if (((Symbol)var1).kind == 16 || ((Symbol)var1).kind == 4) {
         var11.append(var4.visit(((Symbol)var1).owner, var3)).append(".");
      }

      var11.append(var4.visit((Symbol)var1, var3));
      System.err.println(var11);
   }

   public String referencedClassName() {
      return this.where;
   }

   public PackageDoc referencedPackage() {
      return this.referencedPackage;
   }

   public ClassDoc referencedClass() {
      return this.referencedClass;
   }

   public String referencedMemberName() {
      return this.what;
   }

   public MemberDoc referencedMember() {
      return this.referencedMember;
   }

   private void parseSeeString() {
      int var1 = this.text.length();
      if (var1 != 0) {
         switch (this.text.charAt(0)) {
            case '"':
               if (var1 == 1 || this.text.charAt(var1 - 1) != '"') {
                  this.docenv().warning(this.holder, "tag.see.no_close_quote", this.name, this.text);
               }

               return;
            case '<':
               if (this.text.charAt(var1 - 1) != '>') {
                  this.docenv().warning(this.holder, "tag.see.no_close_bracket_on_url", this.name, this.text);
               }

               return;
            default:
               int var2 = 0;
               int var3 = 0;
               byte var4 = 0;
               int var6 = var4;

               int var5;
               for(; var6 < var1; var6 += Character.charCount(var5)) {
                  var5 = this.text.codePointAt(var6);
                  switch (var5) {
                     case 9:
                     case 10:
                     case 13:
                     case 32:
                        if (var2 == 0) {
                           var3 = var6;
                           var6 = var1;
                        }
                     case 35:
                     case 46:
                     case 91:
                     case 93:
                        break;
                     case 40:
                        ++var2;
                        break;
                     case 41:
                        --var2;
                        break;
                     case 44:
                        if (var2 <= 0) {
                           this.docenv().warning(this.holder, "tag.see.malformed_see_tag", this.name, this.text);
                           return;
                        }
                        break;
                     default:
                        if (!Character.isJavaIdentifierPart(var5)) {
                           this.docenv().warning(this.holder, "tag.see.illegal_character", this.name, "" + var5, this.text);
                        }
                  }
               }

               if (var2 != 0) {
                  this.docenv().warning(this.holder, "tag.see.malformed_see_tag", this.name, this.text);
               } else {
                  String var10 = "";
                  String var7 = "";
                  int var8;
                  if (var3 > 0) {
                     var10 = this.text.substring(var4, var3);
                     var7 = this.text.substring(var3 + 1);

                     for(var8 = 0; var8 < var7.length(); ++var8) {
                        char var9 = var7.charAt(var8);
                        if (var9 != ' ' && var9 != '\t' && var9 != '\n') {
                           this.label = var7.substring(var8);
                           break;
                        }
                     }
                  } else {
                     var10 = this.text;
                     this.label = "";
                  }

                  var8 = var10.indexOf(35);
                  if (var8 >= 0) {
                     this.where = var10.substring(0, var8);
                     this.what = var10.substring(var8 + 1);
                  } else if (var10.indexOf(40) >= 0) {
                     this.docenv().warning(this.holder, "tag.see.missing_sharp", this.name, this.text);
                     this.where = "";
                     this.what = var10;
                  } else {
                     this.where = var10;
                     this.what = null;
                  }

               }
         }
      }
   }

   private void findReferenced(ClassDocImpl var1) {
      if (this.where.length() > 0) {
         if (var1 != null) {
            this.referencedClass = var1.findClass(this.where);
         } else {
            this.referencedClass = this.docenv().lookupClass(this.where);
         }

         if (this.referencedClass == null && this.holder() instanceof ProgramElementDoc) {
            this.referencedClass = this.docenv().lookupClass(((ProgramElementDoc)this.holder()).containingPackage().name() + "." + this.where);
         }

         if (this.referencedClass == null) {
            this.referencedPackage = this.docenv().lookupPackage(this.where);
            return;
         }
      } else {
         if (var1 == null) {
            this.docenv().warning(this.holder, "tag.see.class_not_specified", this.name, this.text);
            return;
         }

         this.referencedClass = var1;
      }

      this.where = this.referencedClass.qualifiedName();
      if (this.what != null) {
         int var2 = this.what.indexOf(40);
         String var3 = var2 >= 0 ? this.what.substring(0, var2) : this.what;
         if (var2 > 0) {
            String[] var4 = (new ParameterParseMachine(this.what.substring(var2, this.what.length()))).parseParameters();
            if (var4 != null) {
               this.referencedMember = this.findExecutableMember(var3, var4, this.referencedClass);
            } else {
               this.referencedMember = null;
            }
         } else {
            this.referencedMember = this.findExecutableMember(var3, (String[])null, this.referencedClass);
            FieldDoc var5 = ((ClassDocImpl)this.referencedClass).findField(var3);
            if (this.referencedMember == null || var5 != null && var5.containingClass().subclassOf(this.referencedMember.containingClass())) {
               this.referencedMember = var5;
            }
         }

         if (this.referencedMember == null) {
            this.docenv().warning(this.holder, "tag.see.can_not_find_member", this.name, this.what, this.where);
         }

      }
   }

   private MemberDoc findReferencedMethod(String var1, String[] var2, ClassDoc var3) {
      MemberDoc var4 = this.findExecutableMember(var1, var2, var3);
      ClassDoc[] var5 = var3.innerClasses();
      if (var4 == null) {
         for(int var6 = 0; var6 < var5.length; ++var6) {
            var4 = this.findReferencedMethod(var1, var2, var5[var6]);
            if (var4 != null) {
               return var4;
            }
         }
      }

      return null;
   }

   private MemberDoc findExecutableMember(String var1, String[] var2, ClassDoc var3) {
      return (MemberDoc)(var1.equals(var3.name()) ? ((ClassDocImpl)var3).findConstructor(var1, var2) : ((ClassDocImpl)var3).findMethod(var1, var2));
   }

   public String kind() {
      return "@see";
   }

   public String label() {
      return this.label;
   }

   class ParameterParseMachine {
      static final int START = 0;
      static final int TYPE = 1;
      static final int NAME = 2;
      static final int TNSPACE = 3;
      static final int ARRAYDECORATION = 4;
      static final int ARRAYSPACE = 5;
      String parameters;
      StringBuilder typeId;
      ListBuffer paramList;

      ParameterParseMachine(String var2) {
         this.parameters = var2;
         this.paramList = new ListBuffer();
         this.typeId = new StringBuilder();
      }

      public String[] parseParameters() {
         if (this.parameters.equals("()")) {
            return new String[0];
         } else {
            byte var1 = 0;
            byte var2 = 0;
            this.parameters = this.parameters.substring(1, this.parameters.length() - 1);

            int var3;
            for(int var4 = 0; var4 < this.parameters.length(); var4 += Character.charCount(var3)) {
               var3 = this.parameters.codePointAt(var4);
               switch (var1) {
                  case 0:
                     if (Character.isJavaIdentifierStart(var3)) {
                        this.typeId.append(Character.toChars(var3));
                        var1 = 1;
                     }

                     var2 = 0;
                     break;
                  case 1:
                     if (!Character.isJavaIdentifierPart(var3) && var3 != 46) {
                        if (var3 == 91) {
                           this.typeId.append('[');
                           var1 = 4;
                        } else if (Character.isWhitespace(var3)) {
                           var1 = 3;
                        } else if (var3 == 44) {
                           this.addTypeToParamList();
                           var1 = 0;
                        }
                     } else {
                        this.typeId.append(Character.toChars(var3));
                     }

                     var2 = 1;
                     break;
                  case 2:
                     if (var3 == 44) {
                        var1 = 0;
                     }

                     var2 = 2;
                     break;
                  case 3:
                     if (Character.isJavaIdentifierStart(var3)) {
                        if (var2 == 4) {
                           SeeTagImpl.this.docenv().warning(SeeTagImpl.this.holder, "tag.missing_comma_space", SeeTagImpl.this.name, "(" + this.parameters + ")");
                           return (String[])null;
                        }

                        this.addTypeToParamList();
                        var1 = 2;
                     } else if (var3 == 91) {
                        this.typeId.append('[');
                        var1 = 4;
                     } else if (var3 == 44) {
                        this.addTypeToParamList();
                        var1 = 0;
                     }

                     var2 = 3;
                     break;
                  case 4:
                     if (var3 == 93) {
                        this.typeId.append(']');
                        var1 = 3;
                     } else if (!Character.isWhitespace(var3)) {
                        SeeTagImpl.this.docenv().warning(SeeTagImpl.this.holder, "tag.illegal_char_in_arr_dim", SeeTagImpl.this.name, "(" + this.parameters + ")");
                        return (String[])null;
                     }

                     var2 = 4;
               }
            }

            if (var1 == 4 || var1 == 0 && var2 == 3) {
               SeeTagImpl.this.docenv().warning(SeeTagImpl.this.holder, "tag.illegal_see_tag", "(" + this.parameters + ")");
            }

            if (this.typeId.length() > 0) {
               this.paramList.append(this.typeId.toString());
            }

            return (String[])this.paramList.toArray(new String[this.paramList.length()]);
         }
      }

      void addTypeToParamList() {
         if (this.typeId.length() > 0) {
            this.paramList.append(this.typeId.toString());
            this.typeId.setLength(0);
         }

      }
   }
}
