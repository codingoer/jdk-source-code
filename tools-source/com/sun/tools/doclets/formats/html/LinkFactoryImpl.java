package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.doclets.internal.toolkit.util.links.LinkFactory;
import com.sun.tools.doclets.internal.toolkit.util.links.LinkInfo;
import java.util.Iterator;
import java.util.List;

public class LinkFactoryImpl extends LinkFactory {
   private HtmlDocletWriter m_writer;

   public LinkFactoryImpl(HtmlDocletWriter var1) {
      this.m_writer = var1;
   }

   protected Content newContent() {
      return new ContentBuilder();
   }

   protected Content getClassLink(LinkInfo var1) {
      LinkInfoImpl var2 = (LinkInfoImpl)var1;
      boolean var3 = var1.label == null || var1.label.isEmpty();
      ClassDoc var4 = var2.classDoc;
      String var5 = var2.where != null && var2.where.length() != 0 ? "" : this.getClassToolTip(var4, var2.type != null && !var4.qualifiedTypeName().equals(var2.type.qualifiedTypeName()));
      Content var6 = var2.getClassLinkLabel(this.m_writer.configuration);
      ConfigurationImpl var7 = this.m_writer.configuration;
      ContentBuilder var8 = new ContentBuilder();
      if (var4.isIncluded()) {
         if (var7.isGeneratedDoc(var4)) {
            DocPath var9 = this.getPath(var2);
            if (var1.linkToSelf || !DocPath.forName(var4).equals(this.m_writer.filename)) {
               var8.addContent(this.m_writer.getHyperLink(var9.fragment(var2.where), var6, var2.isStrong, var2.styleName, var5, var2.target));
               if (var3 && !var2.excludeTypeParameterLinks) {
                  var8.addContent(this.getTypeParameterLinks(var1));
               }

               return var8;
            }
         }
      } else {
         Content var10 = this.m_writer.getCrossClassLink(var4.qualifiedName(), var2.where, var6, var2.isStrong, var2.styleName, true);
         if (var10 != null) {
            var8.addContent(var10);
            if (var3 && !var2.excludeTypeParameterLinks) {
               var8.addContent(this.getTypeParameterLinks(var1));
            }

            return var8;
         }
      }

      var8.addContent(var6);
      if (var3 && !var2.excludeTypeParameterLinks) {
         var8.addContent(this.getTypeParameterLinks(var1));
      }

      return var8;
   }

   protected Content getTypeParameterLink(LinkInfo var1, Type var2) {
      LinkInfoImpl var3 = new LinkInfoImpl(this.m_writer.configuration, ((LinkInfoImpl)var1).getContext(), var2);
      var3.excludeTypeBounds = var1.excludeTypeBounds;
      var3.excludeTypeParameterLinks = var1.excludeTypeParameterLinks;
      var3.linkToSelf = var1.linkToSelf;
      var3.isJava5DeclarationLocation = false;
      return this.getLink(var3);
   }

   protected Content getTypeAnnotationLink(LinkInfo var1, AnnotationDesc var2) {
      throw new RuntimeException("Not implemented yet!");
   }

   public Content getTypeAnnotationLinks(LinkInfo var1) {
      ContentBuilder var2 = new ContentBuilder();
      AnnotationDesc[] var3;
      if (var1.type instanceof AnnotatedType) {
         var3 = var1.type.asAnnotatedType().annotations();
      } else {
         if (!(var1.type instanceof TypeVariable)) {
            return var2;
         }

         var3 = var1.type.asTypeVariable().annotations();
      }

      if (var3.length == 0) {
         return var2;
      } else {
         List var4 = this.m_writer.getAnnotations(0, var3, false, var1.isJava5DeclarationLocation);
         boolean var5 = true;

         for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 = false) {
            Content var7 = (Content)var6.next();
            if (!var5) {
               var2.addContent(" ");
            }

            var2.addContent(var7);
         }

         if (!var4.isEmpty()) {
            var2.addContent(" ");
         }

         return var2;
      }
   }

   private String getClassToolTip(ClassDoc var1, boolean var2) {
      ConfigurationImpl var3 = this.m_writer.configuration;
      if (var2) {
         return var3.getText("doclet.Href_Type_Param_Title", var1.name());
      } else if (var1.isInterface()) {
         return var3.getText("doclet.Href_Interface_Title", Util.getPackageName(var1.containingPackage()));
      } else if (var1.isAnnotationType()) {
         return var3.getText("doclet.Href_Annotation_Title", Util.getPackageName(var1.containingPackage()));
      } else {
         return var1.isEnum() ? var3.getText("doclet.Href_Enum_Title", Util.getPackageName(var1.containingPackage())) : var3.getText("doclet.Href_Class_Title", Util.getPackageName(var1.containingPackage()));
      }
   }

   private DocPath getPath(LinkInfoImpl var1) {
      return var1.context == LinkInfoImpl.Kind.PACKAGE_FRAME ? DocPath.forName(var1.classDoc) : this.m_writer.pathToRoot.resolve(DocPath.forClass(var1.classDoc));
   }
}
