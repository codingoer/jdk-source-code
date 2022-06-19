package com.sun.tools.javadoc;

import java.text.BreakIterator;
import java.text.Collator;
import java.util.Locale;

class DocLocale {
   final String localeName;
   final Locale locale;
   final Collator collator;
   private final DocEnv docenv;
   private final BreakIterator sentenceBreaker;
   private boolean useBreakIterator = false;
   static final String[] sentenceTerminators = new String[]{"<p>", "</p>", "<h1>", "<h2>", "<h3>", "<h4>", "<h5>", "<h6>", "</h1>", "</h2>", "</h3>", "</h4>", "</h5>", "</h6>", "<hr>", "<pre>", "</pre>"};

   DocLocale(DocEnv var1, String var2, boolean var3) {
      this.docenv = var1;
      this.localeName = var2;
      this.useBreakIterator = var3;
      this.locale = this.getLocale();
      if (this.locale == null) {
         var1.exit();
      } else {
         Locale.setDefault(this.locale);
      }

      this.collator = Collator.getInstance(this.locale);
      this.sentenceBreaker = BreakIterator.getSentenceInstance(this.locale);
   }

   private Locale getLocale() {
      Locale var1 = null;
      if (this.localeName.length() <= 0) {
         return Locale.getDefault();
      } else {
         int var2 = this.localeName.indexOf(95);
         boolean var3 = true;
         String var4 = null;
         String var5 = null;
         String var6 = null;
         if (var2 == 2) {
            var4 = this.localeName.substring(0, var2);
            int var7 = this.localeName.indexOf(95, var2 + 1);
            if (var7 > 0) {
               if (var7 != var2 + 3 || this.localeName.length() <= var7 + 1) {
                  this.docenv.error((DocImpl)null, "main.malformed_locale_name", this.localeName);
                  return null;
               }

               var5 = this.localeName.substring(var2 + 1, var7);
               var6 = this.localeName.substring(var7 + 1);
            } else {
               if (this.localeName.length() != var2 + 3) {
                  this.docenv.error((DocImpl)null, "main.malformed_locale_name", this.localeName);
                  return null;
               }

               var5 = this.localeName.substring(var2 + 1);
            }
         } else {
            if (var2 != -1 || this.localeName.length() != 2) {
               this.docenv.error((DocImpl)null, "main.malformed_locale_name", this.localeName);
               return null;
            }

            var4 = this.localeName;
         }

         var1 = this.searchLocale(var4, var5, var6);
         if (var1 == null) {
            this.docenv.error((DocImpl)null, "main.illegal_locale_name", this.localeName);
            return null;
         } else {
            return var1;
         }
      }
   }

   private Locale searchLocale(String var1, String var2, String var3) {
      Locale[] var4 = Locale.getAvailableLocales();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5].getLanguage().equals(var1) && (var2 == null || var4[var5].getCountry().equals(var2)) && (var3 == null || var4[var5].getVariant().equals(var3))) {
            return var4[var5];
         }
      }

      return null;
   }

   String localeSpecificFirstSentence(DocImpl var1, String var2) {
      if (var2 != null && var2.length() != 0) {
         int var3 = var2.indexOf("-->");
         if (var2.trim().startsWith("<!--") && var3 != -1) {
            return this.localeSpecificFirstSentence(var1, var2.substring(var3 + 3, var2.length()));
         } else if (!this.useBreakIterator && this.locale.getLanguage().equals("en")) {
            return this.englishLanguageFirstSentence(var2).trim();
         } else {
            this.sentenceBreaker.setText(var2.replace('\n', ' '));
            int var4 = this.sentenceBreaker.first();
            int var5 = this.sentenceBreaker.next();
            return var2.substring(var4, var5).trim();
         }
      } else {
         return "";
      }
   }

   private String englishLanguageFirstSentence(String var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.length();
         boolean var3 = false;

         for(int var4 = 0; var4 < var2; ++var4) {
            switch (var1.charAt(var4)) {
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
                  if (var3) {
                     return var1.substring(0, var4);
                  }
                  break;
               case '.':
                  var3 = true;
                  break;
               case '<':
                  if (var4 > 0 && this.htmlSentenceTerminatorFound(var1, var4)) {
                     return var1.substring(0, var4);
                  }
                  break;
               default:
                  var3 = false;
            }
         }

         return var1;
      }
   }

   private boolean htmlSentenceTerminatorFound(String var1, int var2) {
      for(int var3 = 0; var3 < sentenceTerminators.length; ++var3) {
         String var4 = sentenceTerminators[var3];
         if (var1.regionMatches(true, var2, var4, 0, var4.length())) {
            return true;
         }
      }

      return false;
   }
}
