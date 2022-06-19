package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.util.StringUtils;
import java.io.IOException;
import java.util.Arrays;

public class SerializedFormBuilder extends AbstractBuilder {
   public static final String NAME = "SerializedForm";
   private SerializedFormWriter writer;
   private SerializedFormWriter.SerialFieldWriter fieldWriter;
   private SerializedFormWriter.SerialMethodWriter methodWriter;
   private static final String SERIAL_VERSION_UID_HEADER = "serialVersionUID:";
   private PackageDoc currentPackage;
   private ClassDoc currentClass;
   protected MemberDoc currentMember;
   private Content contentTree;

   private SerializedFormBuilder(AbstractBuilder.Context var1) {
      super(var1);
   }

   public static SerializedFormBuilder getInstance(AbstractBuilder.Context var0) {
      return new SerializedFormBuilder(var0);
   }

   public void build() throws IOException {
      if (this.serialClassFoundToDocument(this.configuration.root.classes())) {
         try {
            this.writer = this.configuration.getWriterFactory().getSerializedFormWriter();
            if (this.writer == null) {
               return;
            }
         } catch (Exception var2) {
            throw new DocletAbortException(var2);
         }

         this.build(this.layoutParser.parseXML("SerializedForm"), this.contentTree);
         this.writer.close();
      }
   }

   public String getName() {
      return "SerializedForm";
   }

   public void buildSerializedForm(XMLNode var1, Content var2) throws Exception {
      var2 = this.writer.getHeader(this.configuration.getText("doclet.Serialized_Form"));
      this.buildChildren(var1, var2);
      this.writer.addFooter(var2);
      this.writer.printDocument(var2);
      this.writer.close();
   }

   public void buildSerializedFormSummaries(XMLNode var1, Content var2) {
      Content var3 = this.writer.getSerializedSummariesHeader();
      PackageDoc[] var4 = this.configuration.packages;

      for(int var5 = 0; var5 < var4.length; ++var5) {
         this.currentPackage = var4[var5];
         this.buildChildren(var1, var3);
      }

      var2.addContent(this.writer.getSerializedContent(var3));
   }

   public void buildPackageSerializedForm(XMLNode var1, Content var2) {
      Content var3 = this.writer.getPackageSerializedHeader();
      String var4 = this.currentPackage.name();
      ClassDoc[] var5 = this.currentPackage.allClasses(false);
      if (var5 != null && var5.length != 0) {
         if (serialInclude(this.currentPackage)) {
            if (this.serialClassFoundToDocument(var5)) {
               this.buildChildren(var1, var3);
               var2.addContent(var3);
            }
         }
      }
   }

   public void buildPackageHeader(XMLNode var1, Content var2) {
      var2.addContent(this.writer.getPackageHeader(Util.getPackageName(this.currentPackage)));
   }

   public void buildClassSerializedForm(XMLNode var1, Content var2) {
      Content var3 = this.writer.getClassSerializedHeader();
      ClassDoc[] var4 = this.currentPackage.allClasses(false);
      Arrays.sort(var4);

      for(int var5 = 0; var5 < var4.length; ++var5) {
         this.currentClass = var4[var5];
         this.fieldWriter = this.writer.getSerialFieldWriter(this.currentClass);
         this.methodWriter = this.writer.getSerialMethodWriter(this.currentClass);
         if (this.currentClass.isClass() && this.currentClass.isSerializable() && serialClassInclude(this.currentClass)) {
            Content var6 = this.writer.getClassHeader(this.currentClass);
            this.buildChildren(var1, var6);
            var3.addContent(var6);
         }
      }

      var2.addContent(var3);
   }

   public void buildSerialUIDInfo(XMLNode var1, Content var2) {
      Content var3 = this.writer.getSerialUIDInfoHeader();
      FieldDoc[] var4 = this.currentClass.fields(false);

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5].name().equals("serialVersionUID") && var4[var5].constantValueExpression() != null) {
            this.writer.addSerialUIDInfo("serialVersionUID:", var4[var5].constantValueExpression(), var3);
            break;
         }
      }

      var2.addContent(var3);
   }

   public void buildClassContent(XMLNode var1, Content var2) {
      Content var3 = this.writer.getClassContentHeader();
      this.buildChildren(var1, var3);
      var2.addContent(var3);
   }

   public void buildSerializableMethods(XMLNode var1, Content var2) {
      Content var3 = this.methodWriter.getSerializableMethodsHeader();
      MethodDoc[] var4 = this.currentClass.serializationMethods();
      int var5 = var4.length;
      if (var5 > 0) {
         for(int var6 = 0; var6 < var5; ++var6) {
            this.currentMember = var4[var6];
            Content var7 = this.methodWriter.getMethodsContentHeader(var6 == var5 - 1);
            this.buildChildren(var1, var7);
            var3.addContent(var7);
         }
      }

      if (this.currentClass.serializationMethods().length > 0) {
         var2.addContent(this.methodWriter.getSerializableMethods(this.configuration.getText("doclet.Serialized_Form_methods"), var3));
         if (this.currentClass.isSerializable() && !this.currentClass.isExternalizable() && this.currentClass.serializationMethods().length == 0) {
            Content var8 = this.methodWriter.getNoCustomizationMsg(this.configuration.getText("doclet.Serializable_no_customization"));
            var2.addContent(this.methodWriter.getSerializableMethods(this.configuration.getText("doclet.Serialized_Form_methods"), var8));
         }
      }

   }

   public void buildMethodSubHeader(XMLNode var1, Content var2) {
      this.methodWriter.addMemberHeader((MethodDoc)this.currentMember, var2);
   }

   public void buildDeprecatedMethodInfo(XMLNode var1, Content var2) {
      this.methodWriter.addDeprecatedMemberInfo((MethodDoc)this.currentMember, var2);
   }

   public void buildMethodInfo(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         this.buildChildren(var1, var2);
      }
   }

   public void buildMethodDescription(XMLNode var1, Content var2) {
      this.methodWriter.addMemberDescription((MethodDoc)this.currentMember, var2);
   }

   public void buildMethodTags(XMLNode var1, Content var2) {
      this.methodWriter.addMemberTags((MethodDoc)this.currentMember, var2);
      MethodDoc var3 = (MethodDoc)this.currentMember;
      if (var3.name().compareTo("writeExternal") == 0 && var3.tags("serialData").length == 0 && this.configuration.serialwarn) {
         this.configuration.getDocletSpecificMsg().warning(this.currentMember.position(), "doclet.MissingSerialDataTag", var3.containingClass().qualifiedName(), var3.name());
      }

   }

   public void buildFieldHeader(XMLNode var1, Content var2) {
      if (this.currentClass.serializableFields().length > 0) {
         this.buildFieldSerializationOverview(this.currentClass, var2);
      }

   }

   public void buildFieldSerializationOverview(ClassDoc var1, Content var2) {
      if (var1.definesSerializableFields()) {
         FieldDoc var3 = var1.serializableFields()[0];
         if (this.fieldWriter.shouldPrintOverview(var3)) {
            Content var4 = this.fieldWriter.getSerializableFieldsHeader();
            Content var5 = this.fieldWriter.getFieldsContentHeader(true);
            this.fieldWriter.addMemberDeprecatedInfo(var3, var5);
            if (!this.configuration.nocomment) {
               this.fieldWriter.addMemberDescription(var3, var5);
               this.fieldWriter.addMemberTags(var3, var5);
            }

            var4.addContent(var5);
            var2.addContent(this.fieldWriter.getSerializableFields(this.configuration.getText("doclet.Serialized_Form_class"), var4));
         }
      }

   }

   public void buildSerializableFields(XMLNode var1, Content var2) {
      FieldDoc[] var3 = this.currentClass.serializableFields();
      int var4 = var3.length;
      if (var4 > 0) {
         Content var5 = this.fieldWriter.getSerializableFieldsHeader();

         for(int var6 = 0; var6 < var4; ++var6) {
            this.currentMember = var3[var6];
            if (!this.currentClass.definesSerializableFields()) {
               Content var7 = this.fieldWriter.getFieldsContentHeader(var6 == var4 - 1);
               this.buildChildren(var1, var7);
               var5.addContent(var7);
            } else {
               this.buildSerialFieldTagsInfo(var5);
            }
         }

         var2.addContent(this.fieldWriter.getSerializableFields(this.configuration.getText("doclet.Serialized_Form_fields"), var5));
      }

   }

   public void buildFieldSubHeader(XMLNode var1, Content var2) {
      if (!this.currentClass.definesSerializableFields()) {
         FieldDoc var3 = (FieldDoc)this.currentMember;
         this.fieldWriter.addMemberHeader(var3.type().asClassDoc(), var3.type().typeName(), var3.type().dimension(), var3.name(), var2);
      }

   }

   public void buildFieldDeprecationInfo(XMLNode var1, Content var2) {
      if (!this.currentClass.definesSerializableFields()) {
         FieldDoc var3 = (FieldDoc)this.currentMember;
         this.fieldWriter.addMemberDeprecatedInfo(var3, var2);
      }

   }

   public void buildSerialFieldTagsInfo(Content var1) {
      if (!this.configuration.nocomment) {
         FieldDoc var2 = (FieldDoc)this.currentMember;
         SerialFieldTag[] var3 = var2.serialFieldTags();
         Arrays.sort(var3);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            if (var3[var5].fieldName() != null && var3[var5].fieldType() != null) {
               Content var6 = this.fieldWriter.getFieldsContentHeader(var5 == var4 - 1);
               this.fieldWriter.addMemberHeader(var3[var5].fieldTypeDoc(), var3[var5].fieldType(), "", var3[var5].fieldName(), var6);
               this.fieldWriter.addMemberDescription(var3[var5], var6);
               var1.addContent(var6);
            }
         }

      }
   }

   public void buildFieldInfo(XMLNode var1, Content var2) {
      if (!this.configuration.nocomment) {
         FieldDoc var3 = (FieldDoc)this.currentMember;
         ClassDoc var4 = var3.containingClass();
         if (var3.tags("serial").length == 0 && !var3.isSynthetic() && this.configuration.serialwarn) {
            this.configuration.message.warning(var3.position(), "doclet.MissingSerialTag", var4.qualifiedName(), var3.name());
         }

         this.fieldWriter.addMemberDescription(var3, var2);
         this.fieldWriter.addMemberTags(var3, var2);
      }
   }

   public static boolean serialInclude(Doc var0) {
      if (var0 == null) {
         return false;
      } else {
         return var0.isClass() ? serialClassInclude((ClassDoc)var0) : serialDocInclude(var0);
      }
   }

   private static boolean serialClassInclude(ClassDoc var0) {
      if (var0.isEnum()) {
         return false;
      } else {
         try {
            var0.superclassType();
         } catch (NullPointerException var2) {
            return false;
         }

         if (var0.isSerializable()) {
            if (var0.tags("serial").length > 0) {
               return serialDocInclude(var0);
            } else {
               return var0.isPublic() || var0.isProtected();
            }
         } else {
            return false;
         }
      }
   }

   private static boolean serialDocInclude(Doc var0) {
      if (var0.isEnum()) {
         return false;
      } else {
         Tag[] var1 = var0.tags("serial");
         if (var1.length > 0) {
            String var2 = StringUtils.toLowerCase(var1[0].text());
            if (var2.indexOf("exclude") >= 0) {
               return false;
            }

            if (var2.indexOf("include") >= 0) {
               return true;
            }
         }

         return true;
      }
   }

   private boolean serialClassFoundToDocument(ClassDoc[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (serialClassInclude(var1[var2])) {
            return true;
         }
      }

      return false;
   }
}
