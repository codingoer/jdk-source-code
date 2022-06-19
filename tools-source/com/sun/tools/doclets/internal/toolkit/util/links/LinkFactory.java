package com.sun.tools.doclets.internal.toolkit.util.links;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.WildcardType;
import com.sun.tools.doclets.internal.toolkit.Content;

public abstract class LinkFactory {
   protected abstract Content newContent();

   public Content getLink(LinkInfo var1) {
      if (var1.type == null) {
         if (var1.classDoc != null) {
            Content var8 = this.newContent();
            var8.addContent(this.getClassLink(var1));
            if (var1.includeTypeAsSepLink) {
               var8.addContent(this.getTypeParameterLinks(var1, false));
            }

            return var8;
         } else {
            return null;
         }
      } else {
         Type var2 = var1.type;
         Content var3 = this.newContent();
         if (var2.isPrimitive()) {
            var3.addContent(var2.typeName());
         } else {
            if (var2.asAnnotatedType() != null && var2.dimension().length() == 0) {
               var3.addContent(this.getTypeAnnotationLinks(var1));
               var1.type = var2.asAnnotatedType().underlyingType();
               var3.addContent(this.getLink(var1));
               return var3;
            }

            Type[] var5;
            int var6;
            if (var2.asWildcardType() != null) {
               var1.isTypeBound = true;
               var3.addContent("?");
               WildcardType var4 = var2.asWildcardType();
               var5 = var4.extendsBounds();

               for(var6 = 0; var6 < var5.length; ++var6) {
                  var3.addContent(var6 > 0 ? ", " : " extends ");
                  this.setBoundsLinkInfo(var1, var5[var6]);
                  var3.addContent(this.getLink(var1));
               }

               Type[] var12 = var4.superBounds();

               for(int var7 = 0; var7 < var12.length; ++var7) {
                  var3.addContent(var7 > 0 ? ", " : " super ");
                  this.setBoundsLinkInfo(var1, var12[var7]);
                  var3.addContent(this.getLink(var1));
               }
            } else if (var2.asTypeVariable() != null) {
               var3.addContent(this.getTypeAnnotationLinks(var1));
               var1.isTypeBound = true;
               ProgramElementDoc var9 = var2.asTypeVariable().owner();
               if (!var1.excludeTypeParameterLinks && var9 instanceof ClassDoc) {
                  var1.classDoc = (ClassDoc)var9;
                  Content var11 = this.newContent();
                  var11.addContent(var2.typeName());
                  var1.label = var11;
                  var3.addContent(this.getClassLink(var1));
               } else {
                  var3.addContent(var2.typeName());
               }

               var5 = var2.asTypeVariable().bounds();
               if (!var1.excludeTypeBounds) {
                  var1.excludeTypeBounds = true;

                  for(var6 = 0; var6 < var5.length; ++var6) {
                     var3.addContent(var6 > 0 ? " & " : " extends ");
                     this.setBoundsLinkInfo(var1, var5[var6]);
                     var3.addContent(this.getLink(var1));
                  }
               }
            } else if (var2.asClassDoc() != null) {
               if (var1.isTypeBound && var1.excludeTypeBoundsLinks) {
                  var3.addContent(var2.typeName());
                  var3.addContent(this.getTypeParameterLinks(var1));
                  return var3;
               }

               var1.classDoc = var2.asClassDoc();
               var3 = this.newContent();
               var3.addContent(this.getClassLink(var1));
               if (var1.includeTypeAsSepLink) {
                  var3.addContent(this.getTypeParameterLinks(var1, false));
               }
            }
         }

         if (var1.isVarArg) {
            if (var2.dimension().length() > 2) {
               var3.addContent(var2.dimension().substring(2));
            }

            var3.addContent("...");
         } else {
            while(var2 != null && var2.dimension().length() > 0) {
               if (var2.asAnnotatedType() != null) {
                  var1.type = var2;
                  var3.addContent(" ");
                  var3.addContent(this.getTypeAnnotationLinks(var1));
                  var3.addContent("[]");
                  var2 = var2.asAnnotatedType().underlyingType().getElementType();
               } else {
                  var3.addContent("[]");
                  var2 = var2.getElementType();
               }
            }

            var1.type = var2;
            Content var10 = this.newContent();
            var10.addContent(this.getTypeAnnotationLinks(var1));
            var10.addContent(var3);
            var3 = var10;
         }

         return var3;
      }
   }

   private void setBoundsLinkInfo(LinkInfo var1, Type var2) {
      var1.classDoc = null;
      var1.label = null;
      var1.type = var2;
   }

   protected abstract Content getClassLink(LinkInfo var1);

   protected abstract Content getTypeParameterLink(LinkInfo var1, Type var2);

   protected abstract Content getTypeAnnotationLink(LinkInfo var1, AnnotationDesc var2);

   public Content getTypeParameterLinks(LinkInfo var1) {
      return this.getTypeParameterLinks(var1, true);
   }

   public Content getTypeParameterLinks(LinkInfo var1, boolean var2) {
      Content var3 = this.newContent();
      Object var4;
      if (var1.executableMemberDoc != null) {
         var4 = var1.executableMemberDoc.typeParameters();
      } else if (var1.type != null && var1.type.asParameterizedType() != null) {
         var4 = var1.type.asParameterizedType().typeArguments();
      } else {
         if (var1.classDoc == null) {
            return var3;
         }

         var4 = var1.classDoc.typeParameters();
      }

      if ((var1.includeTypeInClassLinkLabel && var2 || var1.includeTypeAsSepLink && !var2) && ((Object[])var4).length > 0) {
         var3.addContent("<");

         for(int var5 = 0; var5 < ((Object[])var4).length; ++var5) {
            if (var5 > 0) {
               var3.addContent(",");
            }

            var3.addContent(this.getTypeParameterLink(var1, (Type)((Object[])var4)[var5]));
         }

         var3.addContent(">");
      }

      return var3;
   }

   public Content getTypeAnnotationLinks(LinkInfo var1) {
      Content var2 = this.newContent();
      if (var1.type.asAnnotatedType() == null) {
         return var2;
      } else {
         AnnotationDesc[] var3 = var1.type.asAnnotatedType().annotations();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var4 > 0) {
               var2.addContent(" ");
            }

            var2.addContent(this.getTypeAnnotationLink(var1, var3[var4]));
         }

         var2.addContent(" ");
         return var2;
      }
   }
}
