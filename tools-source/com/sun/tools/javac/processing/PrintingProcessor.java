package com.sun.tools.javac.processing;

import com.sun.tools.javac.util.StringUtils;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.SimpleElementVisitor8;

@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PrintingProcessor extends AbstractProcessor {
   PrintWriter writer;

   public PrintingProcessor() {
      this.writer = new PrintWriter(System.out);
   }

   public void setWriter(Writer var1) {
      this.writer = new PrintWriter(var1);
   }

   public boolean process(Set var1, RoundEnvironment var2) {
      Iterator var3 = var2.getRootElements().iterator();

      while(var3.hasNext()) {
         Element var4 = (Element)var3.next();
         this.print(var4);
      }

      return true;
   }

   void print(Element var1) {
      ((PrintingElementVisitor)(new PrintingElementVisitor(this.writer, this.processingEnv.getElementUtils())).visit(var1)).flush();
   }

   public static class PrintingElementVisitor extends SimpleElementVisitor8 {
      int indentation;
      final PrintWriter writer;
      final Elements elementUtils;
      private static final String[] spaces = new String[]{"", "  ", "    ", "      ", "        ", "          ", "            ", "              ", "                ", "                  ", "                    "};

      public PrintingElementVisitor(Writer var1, Elements var2) {
         this.writer = new PrintWriter(var1);
         this.elementUtils = var2;
         this.indentation = 0;
      }

      protected PrintingElementVisitor defaultAction(Element var1, Boolean var2) {
         if (var2 != null && var2) {
            this.writer.println();
         }

         this.printDocComment(var1);
         this.printModifiers(var1);
         return this;
      }

      public PrintingElementVisitor visitExecutable(ExecutableElement var1, Boolean var2) {
         ElementKind var3 = var1.getKind();
         if (var3 != ElementKind.STATIC_INIT && var3 != ElementKind.INSTANCE_INIT) {
            Element var4 = var1.getEnclosingElement();
            if (var3 == ElementKind.CONSTRUCTOR && var4 != null && NestingKind.ANONYMOUS == (new SimpleElementVisitor7() {
               public NestingKind visitType(TypeElement var1, Void var2) {
                  return var1.getNestingKind();
               }
            }).visit(var4)) {
               return this;
            }

            this.defaultAction(var1, (Boolean)true);
            this.printFormalTypeParameters(var1, true);
            switch (var3) {
               case CONSTRUCTOR:
                  this.writer.print(var1.getEnclosingElement().getSimpleName());
                  break;
               case METHOD:
                  this.writer.print(var1.getReturnType().toString());
                  this.writer.print(" ");
                  this.writer.print(var1.getSimpleName().toString());
            }

            this.writer.print("(");
            this.printParameters(var1);
            this.writer.print(")");
            AnnotationValue var5 = var1.getDefaultValue();
            if (var5 != null) {
               this.writer.print(" default " + var5);
            }

            this.printThrows(var1);
            this.writer.println(";");
         }

         return this;
      }

      public PrintingElementVisitor visitType(TypeElement var1, Boolean var2) {
         ElementKind var3 = var1.getKind();
         NestingKind var4 = var1.getNestingKind();
         if (NestingKind.ANONYMOUS == var4) {
            this.writer.print("new ");
            List var5 = var1.getInterfaces();
            if (!var5.isEmpty()) {
               this.writer.print(var5.get(0));
            } else {
               this.writer.print(var1.getSuperclass());
            }

            this.writer.print("(");
            if (var5.isEmpty()) {
               List var6 = ElementFilter.constructorsIn(var1.getEnclosedElements());
               if (!var6.isEmpty()) {
                  this.printParameters((ExecutableElement)var6.get(0));
               }
            }

            this.writer.print(")");
         } else {
            if (var4 == NestingKind.TOP_LEVEL) {
               PackageElement var9 = this.elementUtils.getPackageOf(var1);
               if (!var9.isUnnamed()) {
                  this.writer.print("package " + var9.getQualifiedName() + ";\n");
               }
            }

            this.defaultAction(var1, (Boolean)true);
            switch (var3) {
               case ANNOTATION_TYPE:
                  this.writer.print("@interface");
                  break;
               default:
                  this.writer.print(StringUtils.toLowerCase(var3.toString()));
            }

            this.writer.print(" ");
            this.writer.print(var1.getSimpleName());
            this.printFormalTypeParameters(var1, false);
            if (var3 == ElementKind.CLASS) {
               TypeMirror var10 = var1.getSuperclass();
               if (var10.getKind() != TypeKind.NONE) {
                  TypeElement var13 = (TypeElement)((DeclaredType)var10).asElement();
                  if (var13.getSuperclass().getKind() != TypeKind.NONE) {
                     this.writer.print(" extends " + var10);
                  }
               }
            }

            this.printInterfaces(var1);
         }

         this.writer.println(" {");
         ++this.indentation;
         if (var3 == ElementKind.ENUM) {
            ArrayList var11 = new ArrayList(var1.getEnclosedElements());
            ArrayList var14 = new ArrayList();
            Iterator var7 = var11.iterator();

            Element var8;
            while(var7.hasNext()) {
               var8 = (Element)var7.next();
               if (var8.getKind() == ElementKind.ENUM_CONSTANT) {
                  var14.add(var8);
               }
            }

            if (!var14.isEmpty()) {
               int var16;
               for(var16 = 0; var16 < var14.size() - 1; ++var16) {
                  this.visit((Element)var14.get(var16), true);
                  this.writer.print(",");
               }

               this.visit((Element)var14.get(var16), true);
               this.writer.println(";\n");
               var11.removeAll(var14);
            }

            var7 = var11.iterator();

            while(var7.hasNext()) {
               var8 = (Element)var7.next();
               this.visit(var8);
            }
         } else {
            Iterator var12 = var1.getEnclosedElements().iterator();

            while(var12.hasNext()) {
               Element var15 = (Element)var12.next();
               this.visit(var15);
            }
         }

         --this.indentation;
         this.indent();
         this.writer.println("}");
         return this;
      }

      public PrintingElementVisitor visitVariable(VariableElement var1, Boolean var2) {
         ElementKind var3 = var1.getKind();
         this.defaultAction(var1, (Boolean)var2);
         if (var3 == ElementKind.ENUM_CONSTANT) {
            this.writer.print(var1.getSimpleName());
         } else {
            this.writer.print(var1.asType().toString() + " " + var1.getSimpleName());
            Object var4 = var1.getConstantValue();
            if (var4 != null) {
               this.writer.print(" = ");
               this.writer.print(this.elementUtils.getConstantExpression(var4));
            }

            this.writer.println(";");
         }

         return this;
      }

      public PrintingElementVisitor visitTypeParameter(TypeParameterElement var1, Boolean var2) {
         this.writer.print(var1.getSimpleName());
         return this;
      }

      public PrintingElementVisitor visitPackage(PackageElement var1, Boolean var2) {
         this.defaultAction(var1, (Boolean)false);
         if (!var1.isUnnamed()) {
            this.writer.println("package " + var1.getQualifiedName() + ";");
         } else {
            this.writer.println("// Unnamed package");
         }

         return this;
      }

      public void flush() {
         this.writer.flush();
      }

      private void printDocComment(Element var1) {
         String var2 = this.elementUtils.getDocComment(var1);
         if (var2 != null) {
            StringTokenizer var3 = new StringTokenizer(var2, "\n\r");
            this.indent();
            this.writer.println("/**");

            while(var3.hasMoreTokens()) {
               this.indent();
               this.writer.print(" *");
               this.writer.println(var3.nextToken());
            }

            this.indent();
            this.writer.println(" */");
         }

      }

      private void printModifiers(Element var1) {
         ElementKind var2 = var1.getKind();
         if (var2 == ElementKind.PARAMETER) {
            this.printAnnotationsInline(var1);
         } else {
            this.printAnnotations(var1);
            this.indent();
         }

         if (var2 != ElementKind.ENUM_CONSTANT) {
            LinkedHashSet var3 = new LinkedHashSet();
            var3.addAll(var1.getModifiers());
            switch (var2) {
               case METHOD:
               case FIELD:
                  Element var4 = var1.getEnclosingElement();
                  if (var4 != null && var4.getKind().isInterface()) {
                     var3.remove(Modifier.PUBLIC);
                     var3.remove(Modifier.ABSTRACT);
                     var3.remove(Modifier.STATIC);
                     var3.remove(Modifier.FINAL);
                  }
                  break;
               case ANNOTATION_TYPE:
               case INTERFACE:
                  var3.remove(Modifier.ABSTRACT);
                  break;
               case ENUM:
                  var3.remove(Modifier.FINAL);
                  var3.remove(Modifier.ABSTRACT);
            }

            Iterator var6 = var3.iterator();

            while(var6.hasNext()) {
               Modifier var5 = (Modifier)var6.next();
               this.writer.print(var5.toString() + " ");
            }

         }
      }

      private void printFormalTypeParameters(Parameterizable var1, boolean var2) {
         List var3 = var1.getTypeParameters();
         if (var3.size() > 0) {
            this.writer.print("<");
            boolean var4 = true;

            for(Iterator var5 = var3.iterator(); var5.hasNext(); var4 = false) {
               TypeParameterElement var6 = (TypeParameterElement)var5.next();
               if (!var4) {
                  this.writer.print(", ");
               }

               this.printAnnotationsInline(var6);
               this.writer.print(var6.toString());
            }

            this.writer.print(">");
            if (var2) {
               this.writer.print(" ");
            }
         }

      }

      private void printAnnotationsInline(Element var1) {
         List var2 = var1.getAnnotationMirrors();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            AnnotationMirror var4 = (AnnotationMirror)var3.next();
            this.writer.print(var4);
            this.writer.print(" ");
         }

      }

      private void printAnnotations(Element var1) {
         List var2 = var1.getAnnotationMirrors();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            AnnotationMirror var4 = (AnnotationMirror)var3.next();
            this.indent();
            this.writer.println(var4);
         }

      }

      private void printParameters(ExecutableElement var1) {
         List var2 = var1.getParameters();
         int var3 = var2.size();
         switch (var3) {
            case 0:
               break;
            case 1:
               VariableElement var5;
               for(Iterator var4 = var2.iterator(); var4.hasNext(); this.writer.print(" " + var5.getSimpleName())) {
                  var5 = (VariableElement)var4.next();
                  this.printModifiers(var5);
                  if (var1.isVarArgs()) {
                     TypeMirror var6 = var5.asType();
                     if (var6.getKind() != TypeKind.ARRAY) {
                        throw new AssertionError("Var-args parameter is not an array type: " + var6);
                     }

                     this.writer.print(((ArrayType)ArrayType.class.cast(var6)).getComponentType());
                     this.writer.print("...");
                  } else {
                     this.writer.print(var5.asType());
                  }
               }

               return;
            default:
               int var8 = 1;

               for(Iterator var9 = var2.iterator(); var9.hasNext(); ++var8) {
                  VariableElement var10 = (VariableElement)var9.next();
                  if (var8 == 2) {
                     ++this.indentation;
                  }

                  if (var8 > 1) {
                     this.indent();
                  }

                  this.printModifiers(var10);
                  if (var8 == var3 && var1.isVarArgs()) {
                     TypeMirror var7 = var10.asType();
                     if (var7.getKind() != TypeKind.ARRAY) {
                        throw new AssertionError("Var-args parameter is not an array type: " + var7);
                     }

                     this.writer.print(((ArrayType)ArrayType.class.cast(var7)).getComponentType());
                     this.writer.print("...");
                  } else {
                     this.writer.print(var10.asType());
                  }

                  this.writer.print(" " + var10.getSimpleName());
                  if (var8 < var3) {
                     this.writer.println(",");
                  }
               }

               if (var2.size() >= 2) {
                  --this.indentation;
               }
         }

      }

      private void printInterfaces(TypeElement var1) {
         ElementKind var2 = var1.getKind();
         if (var2 != ElementKind.ANNOTATION_TYPE) {
            List var3 = var1.getInterfaces();
            if (var3.size() > 0) {
               this.writer.print(var2.isClass() ? " implements" : " extends");
               boolean var4 = true;

               for(Iterator var5 = var3.iterator(); var5.hasNext(); var4 = false) {
                  TypeMirror var6 = (TypeMirror)var5.next();
                  if (!var4) {
                     this.writer.print(",");
                  }

                  this.writer.print(" ");
                  this.writer.print(var6.toString());
               }
            }
         }

      }

      private void printThrows(ExecutableElement var1) {
         List var2 = var1.getThrownTypes();
         int var3 = var2.size();
         if (var3 != 0) {
            this.writer.print(" throws");
            int var4 = 1;

            for(Iterator var5 = var2.iterator(); var5.hasNext(); ++var4) {
               TypeMirror var6 = (TypeMirror)var5.next();
               if (var4 == 1) {
                  this.writer.print(" ");
               }

               if (var4 == 2) {
                  ++this.indentation;
               }

               if (var4 >= 2) {
                  this.indent();
               }

               this.writer.print(var6);
               if (var4 != var3) {
                  this.writer.println(", ");
               }
            }

            if (var3 >= 2) {
               --this.indentation;
            }
         }

      }

      private void indent() {
         int var1 = this.indentation;
         if (var1 >= 0) {
            for(int var2 = spaces.length - 1; var1 > var2; var1 -= var2) {
               this.writer.print(spaces[var2]);
            }

            this.writer.print(spaces[var1]);
         }
      }
   }
}
