package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.doclets.internal.toolkit.util.links.LinkInfo;

public class LinkInfoImpl extends LinkInfo {
   public final ConfigurationImpl configuration;
   public Kind context;
   public String where;
   public String styleName;
   public String target;

   public LinkInfoImpl(ConfigurationImpl var1, Kind var2, ExecutableMemberDoc var3) {
      this.context = LinkInfoImpl.Kind.DEFAULT;
      this.where = "";
      this.styleName = "";
      this.target = "";
      this.configuration = var1;
      this.executableMemberDoc = var3;
      this.setContext(var2);
   }

   protected Content newContent() {
      return new ContentBuilder();
   }

   public LinkInfoImpl(ConfigurationImpl var1, Kind var2, ClassDoc var3) {
      this.context = LinkInfoImpl.Kind.DEFAULT;
      this.where = "";
      this.styleName = "";
      this.target = "";
      this.configuration = var1;
      this.classDoc = var3;
      this.setContext(var2);
   }

   public LinkInfoImpl(ConfigurationImpl var1, Kind var2, Type var3) {
      this.context = LinkInfoImpl.Kind.DEFAULT;
      this.where = "";
      this.styleName = "";
      this.target = "";
      this.configuration = var1;
      this.type = var3;
      this.setContext(var2);
   }

   public LinkInfoImpl label(String var1) {
      this.label = new StringContent(var1);
      return this;
   }

   public LinkInfoImpl label(Content var1) {
      this.label = var1;
      return this;
   }

   public LinkInfoImpl strong(boolean var1) {
      this.isStrong = var1;
      return this;
   }

   public LinkInfoImpl styleName(String var1) {
      this.styleName = var1;
      return this;
   }

   public LinkInfoImpl target(String var1) {
      this.target = var1;
      return this;
   }

   public LinkInfoImpl varargs(boolean var1) {
      this.isVarArg = var1;
      return this;
   }

   public LinkInfoImpl where(String var1) {
      this.where = var1;
      return this;
   }

   public Kind getContext() {
      return this.context;
   }

   public final void setContext(Kind var1) {
      switch (var1) {
         case ALL_CLASSES_FRAME:
         case PACKAGE_FRAME:
         case IMPLEMENTED_CLASSES:
         case SUBCLASSES:
         case METHOD_DOC_COPY:
         case FIELD_DOC_COPY:
         case PROPERTY_DOC_COPY:
         case CLASS_USE_HEADER:
            this.includeTypeInClassLinkLabel = false;
            break;
         case ANNOTATION:
            this.excludeTypeParameterLinks = true;
            this.excludeTypeBounds = true;
            break;
         case IMPLEMENTED_INTERFACES:
         case SUPER_INTERFACES:
         case SUBINTERFACES:
         case CLASS_TREE_PARENT:
         case TREE:
         case CLASS_SIGNATURE_PARENT_NAME:
            this.excludeTypeParameterLinks = true;
            this.excludeTypeBounds = true;
            this.includeTypeInClassLinkLabel = false;
            this.includeTypeAsSepLink = true;
            break;
         case PACKAGE:
         case CLASS_USE:
         case CLASS_HEADER:
         case CLASS_SIGNATURE:
            this.excludeTypeParameterLinks = true;
            this.includeTypeAsSepLink = true;
            this.includeTypeInClassLinkLabel = false;
            break;
         case MEMBER_TYPE_PARAMS:
            this.includeTypeAsSepLink = true;
            this.includeTypeInClassLinkLabel = false;
            break;
         case RETURN_TYPE:
         case SUMMARY_RETURN_TYPE:
            this.excludeTypeBounds = true;
            break;
         case EXECUTABLE_MEMBER_PARAM:
            this.excludeTypeBounds = true;
      }

      this.context = var1;
      if (this.type != null && this.type.asTypeVariable() != null && this.type.asTypeVariable().owner() instanceof ExecutableMemberDoc) {
         this.excludeTypeParameterLinks = true;
      }

   }

   public boolean isLinkable() {
      return Util.isLinkable(this.classDoc, this.configuration);
   }

   public static enum Kind {
      DEFAULT,
      ALL_CLASSES_FRAME,
      CLASS,
      MEMBER,
      CLASS_USE,
      INDEX,
      CONSTANT_SUMMARY,
      SERIALIZED_FORM,
      SERIAL_MEMBER,
      PACKAGE,
      SEE_TAG,
      VALUE_TAG,
      TREE,
      PACKAGE_FRAME,
      CLASS_HEADER,
      CLASS_SIGNATURE,
      RETURN_TYPE,
      SUMMARY_RETURN_TYPE,
      EXECUTABLE_MEMBER_PARAM,
      SUPER_INTERFACES,
      IMPLEMENTED_INTERFACES,
      IMPLEMENTED_CLASSES,
      SUBINTERFACES,
      SUBCLASSES,
      CLASS_SIGNATURE_PARENT_NAME,
      METHOD_DOC_COPY,
      METHOD_SPECIFIED_BY,
      METHOD_OVERRIDES,
      ANNOTATION,
      FIELD_DOC_COPY,
      CLASS_TREE_PARENT,
      MEMBER_TYPE_PARAMS,
      CLASS_USE_HEADER,
      PROPERTY_DOC_COPY;
   }
}
