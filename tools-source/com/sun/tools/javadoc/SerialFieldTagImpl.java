package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.SerialFieldTag;

class SerialFieldTagImpl extends TagImpl implements SerialFieldTag, Comparable {
   private String fieldName;
   private String fieldType;
   private String description;
   private ClassDoc containingClass;
   private ClassDoc fieldTypeDoc;
   private FieldDocImpl matchingField;

   SerialFieldTagImpl(DocImpl var1, String var2, String var3) {
      super(var1, var2, var3);
      this.parseSerialFieldString();
      if (var1 instanceof MemberDoc) {
         this.containingClass = ((MemberDocImpl)var1).containingClass();
      }

   }

   private void parseSerialFieldString() {
      int var1 = this.text.length();
      if (var1 != 0) {
         int var2;
         int var3;
         for(var2 = 0; var2 < var1; var2 += Character.charCount(var3)) {
            var3 = this.text.codePointAt(var2);
            if (!Character.isWhitespace(var3)) {
               break;
            }
         }

         int var4 = var2;
         var3 = this.text.codePointAt(var2);
         if (!Character.isJavaIdentifierStart(var3)) {
            this.docenv().warning(this.holder, "tag.serialField.illegal_character", new String(Character.toChars(var3)), this.text);
         } else {
            for(var2 += Character.charCount(var3); var2 < var1; var2 += Character.charCount(var3)) {
               var3 = this.text.codePointAt(var2);
               if (!Character.isJavaIdentifierPart(var3)) {
                  break;
               }
            }

            if (var2 < var1 && !Character.isWhitespace(var3 = this.text.codePointAt(var2))) {
               this.docenv().warning(this.holder, "tag.serialField.illegal_character", new String(Character.toChars(var3)), this.text);
            } else {
               for(this.fieldName = this.text.substring(var4, var2); var2 < var1; var2 += Character.charCount(var3)) {
                  var3 = this.text.codePointAt(var2);
                  if (!Character.isWhitespace(var3)) {
                     break;
                  }
               }

               for(var4 = var2; var2 < var1; var2 += Character.charCount(var3)) {
                  var3 = this.text.codePointAt(var2);
                  if (Character.isWhitespace(var3)) {
                     break;
                  }
               }

               if (var2 < var1 && !Character.isWhitespace(var3 = this.text.codePointAt(var2))) {
                  this.docenv().warning(this.holder, "tag.serialField.illegal_character", new String(Character.toChars(var3)), this.text);
               } else {
                  for(this.fieldType = this.text.substring(var4, var2); var2 < var1; var2 += Character.charCount(var3)) {
                     var3 = this.text.codePointAt(var2);
                     if (!Character.isWhitespace(var3)) {
                        break;
                     }
                  }

                  this.description = this.text.substring(var2);
               }
            }
         }
      }
   }

   String key() {
      return this.fieldName;
   }

   void mapToFieldDocImpl(FieldDocImpl var1) {
      this.matchingField = var1;
   }

   public String fieldName() {
      return this.fieldName;
   }

   public String fieldType() {
      return this.fieldType;
   }

   public ClassDoc fieldTypeDoc() {
      if (this.fieldTypeDoc == null && this.containingClass != null) {
         this.fieldTypeDoc = this.containingClass.findClass(this.fieldType);
      }

      return this.fieldTypeDoc;
   }

   FieldDocImpl getMatchingField() {
      return this.matchingField;
   }

   public String description() {
      if (this.description.length() == 0 && this.matchingField != null) {
         Comment var1 = this.matchingField.comment();
         if (var1 != null) {
            return var1.commentText();
         }
      }

      return this.description;
   }

   public String kind() {
      return "@serialField";
   }

   public String toString() {
      return this.name + ":" + this.text;
   }

   public int compareTo(Object var1) {
      return this.key().compareTo(((SerialFieldTagImpl)var1).key());
   }
}
