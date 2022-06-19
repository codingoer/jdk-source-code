package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.javac.util.StringUtils;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.tools.StandardLocation;

public class Util {
   public static ProgramElementDoc[] excludeDeprecatedMembers(ProgramElementDoc[] var0) {
      return toProgramElementDocArray(excludeDeprecatedMembersAsList(var0));
   }

   public static List excludeDeprecatedMembersAsList(ProgramElementDoc[] var0) {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2].tags("deprecated").length == 0) {
            var1.add(var0[var2]);
         }
      }

      Collections.sort(var1);
      return var1;
   }

   public static ProgramElementDoc[] toProgramElementDocArray(List var0) {
      ProgramElementDoc[] var1 = new ProgramElementDoc[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         var1[var2] = (ProgramElementDoc)var0.get(var2);
      }

      return var1;
   }

   public static boolean nonPublicMemberFound(ProgramElementDoc[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         if (!var0[var1].isPublic()) {
            return true;
         }
      }

      return false;
   }

   public static MethodDoc findMethod(ClassDoc var0, MethodDoc var1) {
      MethodDoc[] var2 = var0.methods();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (executableMembersEqual(var1, var2[var3])) {
            return var2[var3];
         }
      }

      return null;
   }

   public static boolean executableMembersEqual(ExecutableMemberDoc var0, ExecutableMemberDoc var1) {
      if (var0 instanceof MethodDoc && var1 instanceof MethodDoc) {
         MethodDoc var2 = (MethodDoc)var0;
         MethodDoc var3 = (MethodDoc)var1;
         if (var2.isStatic() && var3.isStatic()) {
            Parameter[] var4 = var2.parameters();
            Parameter[] var5;
            if (var2.name().equals(var3.name()) && (var5 = var3.parameters()).length == var4.length) {
               int var6;
               for(var6 = 0; var6 < var4.length && (var4[var6].typeName().equals(var5[var6].typeName()) || var5[var6].type() instanceof TypeVariable || var4[var6].type() instanceof TypeVariable); ++var6) {
               }

               if (var6 == var4.length) {
                  return true;
               }
            }

            return false;
         } else {
            return var2.overrides(var3) || var3.overrides(var2) || var0 == var1;
         }
      } else {
         return false;
      }
   }

   public static boolean isCoreClass(ClassDoc var0) {
      return var0.containingClass() == null || var0.isStatic();
   }

   public static boolean matches(ProgramElementDoc var0, ProgramElementDoc var1) {
      if (var0 instanceof ExecutableMemberDoc && var1 instanceof ExecutableMemberDoc) {
         ExecutableMemberDoc var2 = (ExecutableMemberDoc)var0;
         ExecutableMemberDoc var3 = (ExecutableMemberDoc)var1;
         return executableMembersEqual(var2, var3);
      } else {
         return var0.name().equals(var1.name());
      }
   }

   public static void copyDocFiles(Configuration var0, PackageDoc var1) {
      copyDocFiles(var0, DocPath.forPackage(var1).resolve(DocPaths.DOC_FILES));
   }

   public static void copyDocFiles(Configuration var0, DocPath var1) {
      try {
         boolean var2 = true;
         Iterator var3 = DocFile.list(var0, StandardLocation.SOURCE_PATH, var1).iterator();

         label60:
         while(true) {
            DocFile var4;
            DocFile var6;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  var4 = (DocFile)var3.next();
               } while(!var4.isDirectory());

               var6 = DocFile.createFileForOutput(var0, var1);
            } while(var4.isSameFile(var6));

            Iterator var7 = var4.list().iterator();

            while(true) {
               while(true) {
                  while(var7.hasNext()) {
                     DocFile var8 = (DocFile)var7.next();
                     DocFile var9 = var6.resolve(var8.getName());
                     if (var8.isFile()) {
                        if (var9.exists() && !var2) {
                           var0.message.warning((SourcePosition)null, "doclet.Copy_Overwrite_warning", var8.getPath(), var6.getPath());
                        } else {
                           var0.message.notice("doclet.Copying_File_0_To_Dir_1", var8.getPath(), var6.getPath());
                           var9.copyFile(var8);
                        }
                     } else if (var8.isDirectory() && var0.copydocfilesubdirs && !var0.shouldExcludeDocFileDir(var8.getName())) {
                        copyDocFiles(var0, var1.resolve(var8.getName()));
                     }
                  }

                  var2 = false;
                  continue label60;
               }
            }
         }
      } catch (SecurityException var10) {
         throw new DocletAbortException(var10);
      } catch (IOException var11) {
         throw new DocletAbortException(var11);
      }
   }

   public static List getAllInterfaces(Type var0, Configuration var1, boolean var2) {
      Object var3 = var2 ? new TreeMap() : new LinkedHashMap();
      Type[] var4 = null;
      Type var5 = null;
      if (var0 instanceof ParameterizedType) {
         var4 = ((ParameterizedType)var0).interfaceTypes();
         var5 = ((ParameterizedType)var0).superclassType();
      } else if (var0 instanceof ClassDoc) {
         var4 = ((ClassDoc)var0).interfaceTypes();
         var5 = ((ClassDoc)var0).superclassType();
      } else {
         var4 = var0.asClassDoc().interfaceTypes();
         var5 = var0.asClassDoc().superclassType();
      }

      for(int var6 = 0; var6 < var4.length; ++var6) {
         Type var7 = var4[var6];
         ClassDoc var8 = var7.asClassDoc();
         if (var8.isPublic() || var1 == null || isLinkable(var8, var1)) {
            ((Map)var3).put(var8, var7);
            List var9 = getAllInterfaces(var7, var1, var2);
            Iterator var10 = var9.iterator();

            while(var10.hasNext()) {
               Type var11 = (Type)var10.next();
               ((Map)var3).put(var11.asClassDoc(), var11);
            }
         }
      }

      if (var5 == null) {
         return new ArrayList(((Map)var3).values());
      } else {
         addAllInterfaceTypes((Map)var3, var5, interfaceTypesOf(var5), false, var1);
         ArrayList var12 = new ArrayList(((Map)var3).values());
         if (var2) {
            Collections.sort(var12, new TypeComparator());
         }

         return var12;
      }
   }

   private static Type[] interfaceTypesOf(Type var0) {
      if (var0 instanceof AnnotatedType) {
         var0 = ((AnnotatedType)var0).underlyingType();
      }

      return var0 instanceof ClassDoc ? ((ClassDoc)var0).interfaceTypes() : ((ParameterizedType)var0).interfaceTypes();
   }

   public static List getAllInterfaces(Type var0, Configuration var1) {
      return getAllInterfaces(var0, var1, true);
   }

   private static void findAllInterfaceTypes(Map var0, ClassDoc var1, boolean var2, Configuration var3) {
      Type var4 = var1.superclassType();
      if (var4 != null) {
         addAllInterfaceTypes(var0, var4, interfaceTypesOf(var4), var2, var3);
      }
   }

   private static void findAllInterfaceTypes(Map var0, ParameterizedType var1, Configuration var2) {
      Type var3 = var1.superclassType();
      if (var3 != null) {
         addAllInterfaceTypes(var0, var3, interfaceTypesOf(var3), false, var2);
      }
   }

   private static void addAllInterfaceTypes(Map var0, Type var1, Type[] var2, boolean var3, Configuration var4) {
      for(int var5 = 0; var5 < var2.length; ++var5) {
         Object var6 = var2[var5];
         ClassDoc var7 = ((Type)var6).asClassDoc();
         if (var7.isPublic() || var4 != null && isLinkable(var7, var4)) {
            if (var3) {
               var6 = ((Type)var6).asClassDoc();
            }

            var0.put(var7, var6);
            List var8 = getAllInterfaces((Type)var6, var4);
            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               Type var10 = (Type)var9.next();
               var0.put(var10.asClassDoc(), var10);
            }
         }
      }

      if (var1 instanceof AnnotatedType) {
         var1 = ((AnnotatedType)var1).underlyingType();
      }

      if (var1 instanceof ParameterizedType) {
         findAllInterfaceTypes(var0, (ParameterizedType)var1, var4);
      } else if (((ClassDoc)var1).typeParameters().length == 0) {
         findAllInterfaceTypes(var0, (ClassDoc)var1, var3, var4);
      } else {
         findAllInterfaceTypes(var0, (ClassDoc)var1, true, var4);
      }

   }

   public static String quote(String var0) {
      return "\"" + var0 + "\"";
   }

   public static String getPackageName(PackageDoc var0) {
      return var0 != null && var0.name().length() != 0 ? var0.name() : "<Unnamed>";
   }

   public static String getPackageFileHeadName(PackageDoc var0) {
      return var0 != null && var0.name().length() != 0 ? var0.name() : "default";
   }

   public static String replaceText(String var0, String var1, String var2) {
      return var1 != null && var2 != null && !var1.equals(var2) ? var0.replace(var1, var2) : var0;
   }

   public static boolean isDocumentedAnnotation(AnnotationTypeDoc var0) {
      AnnotationDesc[] var1 = var0.annotations();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].annotationType().qualifiedName().equals(Documented.class.getName())) {
            return true;
         }
      }

      return false;
   }

   private static boolean isDeclarationTarget(AnnotationDesc var0) {
      AnnotationDesc.ElementValuePair[] var1 = var0.elementValues();
      if (var1 != null && var1.length == 1 && "value".equals(var1[0].element().name()) && var1[0].value().value() instanceof AnnotationValue[]) {
         AnnotationValue[] var2 = (AnnotationValue[])((AnnotationValue[])var1[0].value().value());

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Object var4 = var2[var3].value();
            if (!(var4 instanceof FieldDoc)) {
               return true;
            }

            FieldDoc var5 = (FieldDoc)var4;
            if (isJava5DeclarationElementType(var5)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean isDeclarationAnnotation(AnnotationTypeDoc var0, boolean var1) {
      if (!var1) {
         return false;
      } else {
         AnnotationDesc[] var2 = var0.annotations();
         if (var2.length == 0) {
            return true;
         } else {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].annotationType().qualifiedName().equals(Target.class.getName()) && isDeclarationTarget(var2[var3])) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static boolean isLinkable(ClassDoc var0, Configuration var1) {
      return var0.isIncluded() && var1.isGeneratedDoc(var0) || var1.extern.isExternal(var0) && (var0.isPublic() || var0.isProtected());
   }

   public static Type getFirstVisibleSuperClass(ClassDoc var0, Configuration var1) {
      if (var0 == null) {
         return null;
      } else {
         Type var2 = var0.superclassType();

         ClassDoc var3;
         for(var3 = var0.superclass(); var2 != null && !var3.isPublic() && !isLinkable(var3, var1) && !var3.superclass().qualifiedName().equals(var3.qualifiedName()); var3 = var3.superclass()) {
            var2 = var3.superclassType();
         }

         return var0.equals(var3) ? null : var2;
      }
   }

   public static ClassDoc getFirstVisibleSuperClassCD(ClassDoc var0, Configuration var1) {
      if (var0 == null) {
         return null;
      } else {
         ClassDoc var2;
         for(var2 = var0.superclass(); var2 != null && !var2.isPublic() && !isLinkable(var2, var1); var2 = var2.superclass()) {
         }

         return var0.equals(var2) ? null : var2;
      }
   }

   public static String getTypeName(Configuration var0, ClassDoc var1, boolean var2) {
      String var3 = "";
      if (var1.isOrdinaryClass()) {
         var3 = "doclet.Class";
      } else if (var1.isInterface()) {
         var3 = "doclet.Interface";
      } else if (var1.isException()) {
         var3 = "doclet.Exception";
      } else if (var1.isError()) {
         var3 = "doclet.Error";
      } else if (var1.isAnnotationType()) {
         var3 = "doclet.AnnotationType";
      } else if (var1.isEnum()) {
         var3 = "doclet.Enum";
      }

      return var0.getText(var2 ? StringUtils.toLowerCase(var3) : var3);
   }

   public static String replaceTabs(Configuration var0, String var1) {
      if (var1.indexOf("\t") == -1) {
         return var1;
      } else {
         int var2 = var0.sourcetab;
         String var3 = var0.tabSpaces;
         int var4 = var1.length();
         StringBuilder var5 = new StringBuilder(var4);
         int var6 = 0;
         int var7 = 0;

         for(int var8 = 0; var8 < var4; ++var8) {
            char var9 = var1.charAt(var8);
            switch (var9) {
               case '\t':
                  var5.append(var1, var6, var8);
                  int var10 = var2 - var7 % var2;
                  var5.append(var3, 0, var10);
                  var7 += var10;
                  var6 = var8 + 1;
                  break;
               case '\n':
               case '\r':
                  var7 = 0;
                  break;
               case '\u000b':
               case '\f':
               default:
                  ++var7;
            }
         }

         var5.append(var1, var6, var4);
         return var5.toString();
      }
   }

   public static String normalizeNewlines(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = var0.length();
      String var3 = DocletConstants.NL;
      int var4 = 0;

      for(int var5 = 0; var5 < var2; ++var5) {
         char var6 = var0.charAt(var5);
         switch (var6) {
            case '\n':
               var1.append(var0, var4, var5);
               var1.append(var3);
               var4 = var5 + 1;
               break;
            case '\r':
               var1.append(var0, var4, var5);
               var1.append(var3);
               if (var5 + 1 < var2 && var0.charAt(var5 + 1) == '\n') {
                  ++var5;
               }

               var4 = var5 + 1;
         }
      }

      var1.append(var0, var4, var2);
      return var1.toString();
   }

   public static void setEnumDocumentation(Configuration var0, ClassDoc var1) {
      MethodDoc[] var2 = var1.methods();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         MethodDoc var4 = var2[var3];
         if (var4.name().equals("values") && var4.parameters().length == 0) {
            StringBuilder var7 = new StringBuilder();
            var7.append(var0.getText("doclet.enum_values_doc.main", var1.name()));
            var7.append("\n@return ");
            var7.append(var0.getText("doclet.enum_values_doc.return"));
            var4.setRawCommentText(var7.toString());
         } else if (var4.name().equals("valueOf") && var4.parameters().length == 1) {
            Type var5 = var4.parameters()[0].type();
            if (var5 != null && var5.qualifiedTypeName().equals(String.class.getName())) {
               StringBuilder var6 = new StringBuilder();
               var6.append(var0.getText("doclet.enum_valueof_doc.main", var1.name()));
               var6.append("\n@param name ");
               var6.append(var0.getText("doclet.enum_valueof_doc.param_name"));
               var6.append("\n@return ");
               var6.append(var0.getText("doclet.enum_valueof_doc.return"));
               var6.append("\n@throws IllegalArgumentException ");
               var6.append(var0.getText("doclet.enum_valueof_doc.throws_ila"));
               var6.append("\n@throws NullPointerException ");
               var6.append(var0.getText("doclet.enum_valueof_doc.throws_npe"));
               var4.setRawCommentText(var6.toString());
            }
         }
      }

   }

   public static boolean isDeprecated(Doc var0) {
      if (var0.tags("deprecated").length > 0) {
         return true;
      } else {
         AnnotationDesc[] var1;
         if (var0 instanceof PackageDoc) {
            var1 = ((PackageDoc)var0).annotations();
         } else {
            var1 = ((ProgramElementDoc)var0).annotations();
         }

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].annotationType().qualifiedName().equals(Deprecated.class.getName())) {
               return true;
            }
         }

         return false;
      }
   }

   public static String propertyNameFromMethodName(Configuration var0, String var1) {
      String var2 = null;
      if (!var1.startsWith("get") && !var1.startsWith("set")) {
         if (var1.startsWith("is")) {
            var2 = var1.substring(2);
         }
      } else {
         var2 = var1.substring(3);
      }

      return var2 != null && !var2.isEmpty() ? var2.substring(0, 1).toLowerCase(var0.getLocale()) + var2.substring(1) : "";
   }

   public static ClassDoc[] filterOutPrivateClasses(ClassDoc[] var0, boolean var1) {
      if (!var1) {
         return var0;
      } else {
         ArrayList var2 = new ArrayList(var0.length);
         ClassDoc[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ClassDoc var6 = var3[var5];
            if (!var6.isPrivate() && !var6.isPackagePrivate()) {
               Tag[] var7 = var6.tags("treatAsPrivate");
               if (var7 == null || var7.length <= 0) {
                  var2.add(var6);
               }
            }
         }

         return (ClassDoc[])var2.toArray(new ClassDoc[0]);
      }
   }

   public static boolean isJava5DeclarationElementType(FieldDoc var0) {
      return var0.name().contentEquals(ElementType.ANNOTATION_TYPE.name()) || var0.name().contentEquals(ElementType.CONSTRUCTOR.name()) || var0.name().contentEquals(ElementType.FIELD.name()) || var0.name().contentEquals(ElementType.LOCAL_VARIABLE.name()) || var0.name().contentEquals(ElementType.METHOD.name()) || var0.name().contentEquals(ElementType.PACKAGE.name()) || var0.name().contentEquals(ElementType.PARAMETER.name()) || var0.name().contentEquals(ElementType.TYPE.name());
   }

   private static class TypeComparator implements Comparator {
      private TypeComparator() {
      }

      public int compare(Type var1, Type var2) {
         return var1.qualifiedTypeName().compareToIgnoreCase(var2.qualifiedTypeName());
      }

      // $FF: synthetic method
      TypeComparator(Object var1) {
         this();
      }
   }
}
