package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;

public abstract class AbstractExecutableMemberWriter extends AbstractMemberWriter {
   public AbstractExecutableMemberWriter(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public AbstractExecutableMemberWriter(SubWriterHolderWriter var1) {
      super(var1);
   }

   protected void addTypeParameters(ExecutableMemberDoc var1, Content var2) {
      Content var3 = this.getTypeParameters(var1);
      if (!var3.isEmpty()) {
         var2.addContent(var3);
         var2.addContent(this.writer.getSpace());
      }

   }

   protected Content getTypeParameters(ExecutableMemberDoc var1) {
      LinkInfoImpl var2 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER_TYPE_PARAMS, var1);
      return this.writer.getTypeParameterLinks(var2);
   }

   protected Content getDeprecatedLink(ProgramElementDoc var1) {
      ExecutableMemberDoc var2 = (ExecutableMemberDoc)var1;
      return this.writer.getDocLink(LinkInfoImpl.Kind.MEMBER, var2, var2.qualifiedName() + var2.flatSignature());
   }

   protected void addSummaryLink(LinkInfoImpl.Kind var1, ClassDoc var2, ProgramElementDoc var3, Content var4) {
      ExecutableMemberDoc var5 = (ExecutableMemberDoc)var3;
      String var6 = var5.name();
      HtmlTree var7 = HtmlTree.SPAN(HtmlStyle.memberNameLink, this.writer.getDocLink(var1, var2, var5, var6, false));
      HtmlTree var8 = HtmlTree.CODE(var7);
      this.addParameters(var5, false, var8, var6.length() - 1);
      var4.addContent((Content)var8);
   }

   protected void addInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3) {
      var3.addContent(this.writer.getDocLink(LinkInfoImpl.Kind.MEMBER, var1, (MemberDoc)var2, var2.name(), false));
   }

   protected void addParam(ExecutableMemberDoc var1, Parameter var2, boolean var3, Content var4) {
      if (var2.type() != null) {
         Content var5 = this.writer.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.EXECUTABLE_MEMBER_PARAM, var2.type())).varargs(var3));
         var4.addContent(var5);
      }

      if (var2.name().length() > 0) {
         var4.addContent(this.writer.getSpace());
         var4.addContent(var2.name());
      }

   }

   protected void addReceiverAnnotations(ExecutableMemberDoc var1, Type var2, AnnotationDesc[] var3, Content var4) {
      this.writer.addReceiverAnnotationInfo(var1, var3, var4);
      var4.addContent(this.writer.getSpace());
      var4.addContent(var2.typeName());
      LinkInfoImpl var5 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_SIGNATURE, var2);
      var4.addContent(this.writer.getTypeParameterLinks(var5));
      var4.addContent(this.writer.getSpace());
      var4.addContent("this");
   }

   protected void addParameters(ExecutableMemberDoc var1, Content var2, int var3) {
      this.addParameters(var1, true, var2, var3);
   }

   protected void addParameters(ExecutableMemberDoc var1, boolean var2, Content var3, int var4) {
      var3.addContent("(");
      String var5 = "";
      Parameter[] var6 = var1.parameters();
      String var7 = this.makeSpace(var4 + 1);
      Type var8 = var1.receiverType();
      if (var2 && var8 instanceof AnnotatedType) {
         AnnotationDesc[] var9 = var8.asAnnotatedType().annotations();
         if (var9.length > 0) {
            this.addReceiverAnnotations(var1, var8, var9, var3);
            var5 = "," + DocletConstants.NL + var7;
         }
      }

      boolean var11;
      int var12;
      for(var12 = 0; var12 < var6.length; ++var12) {
         var3.addContent(var5);
         Parameter var10 = var6[var12];
         if (!var10.name().startsWith("this$")) {
            if (var2) {
               var11 = this.writer.addAnnotationInfo(var7.length(), var1, var10, var3);
               if (var11) {
                  var3.addContent(DocletConstants.NL);
                  var3.addContent(var7);
               }
            }

            this.addParam(var1, var10, var12 == var6.length - 1 && var1.isVarArgs(), var3);
            break;
         }
      }

      for(int var13 = var12 + 1; var13 < var6.length; ++var13) {
         var3.addContent(",");
         var3.addContent(DocletConstants.NL);
         var3.addContent(var7);
         if (var2) {
            var11 = this.writer.addAnnotationInfo(var7.length(), var1, var6[var13], var3);
            if (var11) {
               var3.addContent(DocletConstants.NL);
               var3.addContent(var7);
            }
         }

         this.addParam(var1, var6[var13], var13 == var6.length - 1 && var1.isVarArgs(), var3);
      }

      var3.addContent(")");
   }

   protected void addExceptions(ExecutableMemberDoc var1, Content var2, int var3) {
      Type[] var4 = var1.thrownExceptionTypes();
      if (var4.length > 0) {
         new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, var1);
         String var6 = this.makeSpace(var3 + 1 - 7);
         var2.addContent(DocletConstants.NL);
         var2.addContent(var6);
         var2.addContent("throws ");
         var6 = this.makeSpace(var3 + 1);
         Content var7 = this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, var4[0]));
         var2.addContent(var7);

         for(int var8 = 1; var8 < var4.length; ++var8) {
            var2.addContent(",");
            var2.addContent(DocletConstants.NL);
            var2.addContent(var6);
            Content var9 = this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, var4[var8]));
            var2.addContent(var9);
         }
      }

   }

   protected ClassDoc implementsMethodInIntfac(MethodDoc var1, ClassDoc[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         MethodDoc[] var4 = var2[var3].methods();
         if (var4.length > 0) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var4[var5].name().equals(var1.name()) && var4[var5].signature().equals(var1.signature())) {
                  return var2[var3];
               }
            }
         }
      }

      return null;
   }

   protected String getErasureAnchor(ExecutableMemberDoc var1) {
      StringBuilder var2 = new StringBuilder(var1.name() + "(");
      Parameter[] var3 = var1.parameters();
      boolean var4 = false;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (var5 > 0) {
            var2.append(",");
         }

         Type var6 = var3[var5].type();
         var4 = var4 || var6.asTypeVariable() != null;
         var2.append(var6.isPrimitive() ? var6.typeName() : var6.asClassDoc().qualifiedName());
         var2.append(var6.dimension());
      }

      var2.append(")");
      return var4 ? this.writer.getName(var2.toString()) : null;
   }
}
