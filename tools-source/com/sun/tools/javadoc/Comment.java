package com.sun.tools.javadoc;

import com.sun.javadoc.ParamTag;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.tools.javac.util.ListBuffer;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Comment {
   private final ListBuffer tagList = new ListBuffer();
   private String text;
   private final DocEnv docenv;
   private static final Pattern prePat = Pattern.compile("(?i)<(/?)pre>");

   Comment(final DocImpl var1, final String var2) {
      this.docenv = var1.env;

      class CommentStringParser {
         void parseCommentStateMachine() {
            byte var4 = 2;
            boolean var5 = true;
            String var6 = null;
            int var7 = 0;
            int var8 = 0;
            int var9 = -1;
            int var10 = var2.length();

            for(int var11 = 0; var11 < var10; ++var11) {
               char var12 = var2.charAt(var11);
               boolean var13 = Character.isWhitespace(var12);
               switch (var4) {
                  case 2:
                     if (var13) {
                        break;
                     }

                     var8 = var11;
                     var4 = 1;
                  case 1:
                     if (var5 && var12 == '@') {
                        this.parseCommentComponent(var6, var8, var9 + 1);
                        var7 = var11;
                        var4 = 3;
                     }
                     break;
                  case 3:
                     if (var13) {
                        var6 = var2.substring(var7, var11);
                        var4 = 2;
                     }
               }

               if (var12 == '\n') {
                  var5 = true;
               } else if (!var13) {
                  var9 = var11;
                  var5 = false;
               }
            }

            switch (var4) {
               case 3:
                  var6 = var2.substring(var7, var10);
               case 2:
                  var8 = var10;
               case 1:
                  this.parseCommentComponent(var6, var8, var9 + 1);
               default:
            }
         }

         void parseCommentComponent(String var1x, int var2x, int var3) {
            String var4 = var3 <= var2x ? "" : var2.substring(var2x, var3);
            if (var1x == null) {
               Comment.this.text = var4;
            } else {
               Object var5;
               if (!var1x.equals("@exception") && !var1x.equals("@throws")) {
                  if (var1x.equals("@param")) {
                     this.warnIfEmpty(var1x, var4);
                     var5 = new ParamTagImpl(var1, var1x, var4);
                  } else if (var1x.equals("@see")) {
                     this.warnIfEmpty(var1x, var4);
                     var5 = new SeeTagImpl(var1, var1x, var4);
                  } else if (var1x.equals("@serialField")) {
                     this.warnIfEmpty(var1x, var4);
                     var5 = new SerialFieldTagImpl(var1, var1x, var4);
                  } else if (var1x.equals("@return")) {
                     this.warnIfEmpty(var1x, var4);
                     var5 = new TagImpl(var1, var1x, var4);
                  } else if (var1x.equals("@author")) {
                     this.warnIfEmpty(var1x, var4);
                     var5 = new TagImpl(var1, var1x, var4);
                  } else if (var1x.equals("@version")) {
                     this.warnIfEmpty(var1x, var4);
                     var5 = new TagImpl(var1, var1x, var4);
                  } else {
                     var5 = new TagImpl(var1, var1x, var4);
                  }
               } else {
                  this.warnIfEmpty(var1x, var4);
                  var5 = new ThrowsTagImpl(var1, var1x, var4);
               }

               Comment.this.tagList.append(var5);
            }

         }

         void warnIfEmpty(String var1x, String var2x) {
            if (var2x.length() == 0) {
               Comment.this.docenv.warning(var1, "tag.tag_has_no_arguments", var1x);
            }

         }
      }

      (new CommentStringParser()).parseCommentStateMachine();
   }

   String commentText() {
      return this.text;
   }

   Tag[] tags() {
      return (Tag[])this.tagList.toArray(new Tag[this.tagList.length()]);
   }

   Tag[] tags(String var1) {
      ListBuffer var2 = new ListBuffer();
      String var3 = var1;
      if (var1.charAt(0) != '@') {
         var3 = "@" + var1;
      }

      Iterator var4 = this.tagList.iterator();

      while(var4.hasNext()) {
         Tag var5 = (Tag)var4.next();
         if (var5.kind().equals(var3)) {
            var2.append(var5);
         }
      }

      return (Tag[])var2.toArray(new Tag[var2.length()]);
   }

   ThrowsTag[] throwsTags() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.tagList.iterator();

      while(var2.hasNext()) {
         Tag var3 = (Tag)var2.next();
         if (var3 instanceof ThrowsTag) {
            var1.append((ThrowsTag)var3);
         }
      }

      return (ThrowsTag[])var1.toArray(new ThrowsTag[var1.length()]);
   }

   ParamTag[] paramTags() {
      return this.paramTags(false);
   }

   ParamTag[] typeParamTags() {
      return this.paramTags(true);
   }

   private ParamTag[] paramTags(boolean var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = this.tagList.iterator();

      while(var3.hasNext()) {
         Tag var4 = (Tag)var3.next();
         if (var4 instanceof ParamTag) {
            ParamTag var5 = (ParamTag)var4;
            if (var1 == var5.isTypeParameter()) {
               var2.append(var5);
            }
         }
      }

      return (ParamTag[])var2.toArray(new ParamTag[var2.length()]);
   }

   SeeTag[] seeTags() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.tagList.iterator();

      while(var2.hasNext()) {
         Tag var3 = (Tag)var2.next();
         if (var3 instanceof SeeTag) {
            var1.append((SeeTag)var3);
         }
      }

      return (SeeTag[])var1.toArray(new SeeTag[var1.length()]);
   }

   SerialFieldTag[] serialFieldTags() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.tagList.iterator();

      while(var2.hasNext()) {
         Tag var3 = (Tag)var2.next();
         if (var3 instanceof SerialFieldTag) {
            var1.append((SerialFieldTag)var3);
         }
      }

      return (SerialFieldTag[])var1.toArray(new SerialFieldTag[var1.length()]);
   }

   static Tag[] getInlineTags(DocImpl var0, String var1) {
      ListBuffer var2 = new ListBuffer();
      boolean var3 = false;
      int var4 = 0;
      int var5 = var1.length();
      boolean var6 = false;
      DocEnv var7 = var0.env;
      if (var5 == 0) {
         return (Tag[])var2.toArray(new Tag[var2.length()]);
      } else {
         do {
            int var8;
            if ((var8 = inlineTagFound(var0, var1, var4)) == -1) {
               var2.append(new TagImpl(var0, "Text", var1.substring(var4)));
               break;
            }

            var6 = scanForPre(var1, var4, var8, var6);
            int var9 = var8;

            for(int var10 = var8; var10 < var1.length(); ++var10) {
               char var11 = var1.charAt(var10);
               if (Character.isWhitespace(var11) || var11 == '}') {
                  var9 = var10;
                  break;
               }
            }

            String var13 = var1.substring(var8 + 2, var9);
            if (!var6 || !var13.equals("code") && !var13.equals("literal")) {
               while(Character.isWhitespace(var1.charAt(var9))) {
                  if (var1.length() <= var9) {
                     var2.append(new TagImpl(var0, "Text", var1.substring(var4, var9)));
                     var7.warning(var0, "tag.Improper_Use_Of_Link_Tag", var1);
                     return (Tag[])var2.toArray(new Tag[var2.length()]);
                  }

                  ++var9;
               }
            }

            var2.append(new TagImpl(var0, "Text", var1.substring(var4, var8)));
            int var12;
            if ((var12 = findInlineTagDelim(var1, var9)) == -1) {
               var2.append(new TagImpl(var0, "Text", var1.substring(var9)));
               var7.warning(var0, "tag.End_delimiter_missing_for_possible_SeeTag", var1);
               return (Tag[])var2.toArray(new Tag[var2.length()]);
            }

            if (!var13.equals("see") && !var13.equals("link") && !var13.equals("linkplain")) {
               var2.append(new TagImpl(var0, "@" + var13, var1.substring(var9, var12)));
            } else {
               var2.append(new SeeTagImpl(var0, "@" + var13, var1.substring(var9, var12)));
            }

            var4 = var12 + 1;
         } while(var4 != var1.length());

         return (Tag[])var2.toArray(new Tag[var2.length()]);
      }
   }

   private static boolean scanForPre(String var0, int var1, int var2, boolean var3) {
      for(Matcher var4 = prePat.matcher(var0).region(var1, var2); var4.find(); var3 = var4.group(1).isEmpty()) {
      }

      return var3;
   }

   private static int findInlineTagDelim(String var0, int var1) {
      int var2;
      if ((var2 = var0.indexOf("}", var1)) == -1) {
         return -1;
      } else {
         int var3;
         if ((var3 = var0.indexOf("{", var1)) != -1 && var3 < var2) {
            int var4 = findInlineTagDelim(var0, var3 + 1);
            return var4 != -1 ? findInlineTagDelim(var0, var4 + 1) : -1;
         } else {
            return var2;
         }
      }
   }

   private static int inlineTagFound(DocImpl var0, String var1, int var2) {
      DocEnv var3 = var0.env;
      int var4 = var1.indexOf("{@", var2);
      if (var2 != var1.length() && var4 != -1) {
         if (var1.indexOf(125, var4) == -1) {
            var3.warning(var0, "tag.Improper_Use_Of_Link_Tag", var1.substring(var4, var1.length()));
            return -1;
         } else {
            return var4;
         }
      } else {
         return -1;
      }
   }

   static Tag[] firstSentenceTags(DocImpl var0, String var1) {
      DocLocale var2 = var0.env.doclocale;
      return getInlineTags(var0, var2.localeSpecificFirstSentence(var0, var1));
   }

   public String toString() {
      return this.text;
   }
}
