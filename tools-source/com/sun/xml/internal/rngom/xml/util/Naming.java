package com.sun.xml.internal.rngom.xml.util;

public class Naming {
   private static final int CT_NAME = 1;
   private static final int CT_NMSTRT = 2;
   private static final String nameStartSingles = ":_ΆΌϚϜϞϠՙەऽলਫ਼ઍઽૠଽஜೞะຄຊຍລວະຽᄀᄉᄼᄾᅀᅌᅎᅐᅙᅣᅥᅧᅩᅵᆞᆨᆫᆺᇫᇰᇹὙὛὝιΩ℮〇";
   private static final String nameStartRanges = "AZazÀÖØöøÿĀıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁΈΊΎΡΣώϐϖϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖաֆאתװײءغفيٱڷںھۀێېۓۥۦअहक़ॡঅঌএঐওনপরশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜੲੴઅઋએઑઓનપરલળવહଅଌଏଐଓନପରଲଳଶହଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೠೡഅഌഎഐഒനപഹൠൡกฮาำเๅກຂງຈດທນຟມຣສຫອຮາຳເໄཀཇཉཀྵႠჅაჶᄂᄃᄅᄇᄋᄌᄎᄒᅔᅕᅟᅡᅭᅮᅲᅳᆮᆯᆷᆸᆼᇂḀẛẠỹἀἕἘἝἠὅὈὍὐὗὟώᾀᾴᾶᾼῂῄῆῌῐΐῖΊῠῬῲῴῶῼKÅↀↂぁゔァヺㄅㄬ가힣一龥〡〩";
   private static final String nameSingles = "-.़়्ֿٰׄািৗਂ਼ਾਿ઼଼ௗൗัັ༹༵༷༾༿ྗྐྵ゙゚⃡·ːˑ·ـๆໆ々";
   private static final String nameRanges = "ֹֻֽׁׂًْ֑֣̀҃҆֡ۖۜ͠͡ͅ\u06dd۪ۭ۟۠ۤۧۨँःाौ॑॔ॢॣঁঃীৄেৈো্ৢৣੀੂੇੈੋ੍ੰੱઁઃાૅેૉો્ଁଃାୃେୈୋ୍ୖୗஂஃாூெைொ்ఁఃాౄెైొ్ౕౖಂಃಾೄೆೈೊ್ೕೖംഃാൃെൈൊ്ิฺ็๎ິູົຼ່ໍ྄ཱ༘༙྆ྋྐྕྙྭྱྷ〪〯⃐⃜09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩〱〵ゝゞーヾ";
   private static final byte[][] charTypeTable = new byte[256][];

   private Naming() {
   }

   private static void setCharType(char c, int type) {
      int hi = c >> 8;
      if (charTypeTable[hi] == null) {
         charTypeTable[hi] = new byte[256];
      }

      charTypeTable[hi][c & 255] = (byte)type;
   }

   private static void setCharType(char min, char max, int type) {
      byte[] shared = null;

      do {
         if ((min & 255) == 0) {
            while(min + 255 <= max) {
               if (shared == null) {
                  shared = new byte[256];

                  for(int i = 0; i < 256; ++i) {
                     shared[i] = (byte)type;
                  }
               }

               charTypeTable[min >> 8] = shared;
               if (min + 255 == max) {
                  return;
               }

               min = (char)(min + 256);
            }
         }

         setCharType(min, type);
      } while(min++ != max);

   }

   private static boolean isNameStartChar(char c) {
      return charTypeTable[c >> 8][c & 255] == 2;
   }

   private static boolean isNameStartCharNs(char c) {
      return isNameStartChar(c) && c != ':';
   }

   private static boolean isNameChar(char c) {
      return charTypeTable[c >> 8][c & 255] != 0;
   }

   private static boolean isNameCharNs(char c) {
      return isNameChar(c) && c != ':';
   }

   public static boolean isName(String s) {
      int len = s.length();
      if (len == 0) {
         return false;
      } else if (!isNameStartChar(s.charAt(0))) {
         return false;
      } else {
         for(int i = 1; i < len; ++i) {
            if (!isNameChar(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNmtoken(String s) {
      int len = s.length();
      if (len == 0) {
         return false;
      } else {
         for(int i = 0; i < len; ++i) {
            if (!isNameChar(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNcname(String s) {
      int len = s.length();
      if (len == 0) {
         return false;
      } else if (!isNameStartCharNs(s.charAt(0))) {
         return false;
      } else {
         for(int i = 1; i < len; ++i) {
            if (!isNameCharNs(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isQname(String s) {
      int len = s.length();
      if (len == 0) {
         return false;
      } else if (!isNameStartCharNs(s.charAt(0))) {
         return false;
      } else {
         for(int i = 1; i < len; ++i) {
            char c = s.charAt(i);
            if (!isNameChar(c)) {
               if (c == ':') {
                  ++i;
                  if (i < len && isNameStartCharNs(s.charAt(i))) {
                     ++i;

                     while(i < len) {
                        if (!isNameCharNs(s.charAt(i))) {
                           return false;
                        }

                        ++i;
                     }

                     return true;
                  }
               }

               return false;
            }
         }

         return true;
      }
   }

   static {
      int i;
      for(i = 0; i < "-.़়्ֿٰׄািৗਂ਼ਾਿ઼଼ௗൗัັ༹༵༷༾༿ྗྐྵ゙゚⃡·ːˑ·ـๆໆ々".length(); ++i) {
         setCharType("-.़়्ֿٰׄািৗਂ਼ਾਿ઼଼ௗൗัັ༹༵༷༾༿ྗྐྵ゙゚⃡·ːˑ·ـๆໆ々".charAt(i), 1);
      }

      for(i = 0; i < "ֹֻֽׁׂًْ֑֣̀҃҆֡ۖۜ͠͡ͅ\u06dd۪ۭ۟۠ۤۧۨँःाौ॑॔ॢॣঁঃীৄেৈো্ৢৣੀੂੇੈੋ੍ੰੱઁઃાૅેૉો્ଁଃାୃେୈୋ୍ୖୗஂஃாூெைொ்ఁఃాౄెైొ్ౕౖಂಃಾೄೆೈೊ್ೕೖംഃാൃെൈൊ്ิฺ็๎ິູົຼ່ໍ྄ཱ༘༙྆ྋྐྕྙྭྱྷ〪〯⃐⃜09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩〱〵ゝゞーヾ".length(); i += 2) {
         setCharType("ֹֻֽׁׂًْ֑֣̀҃҆֡ۖۜ͠͡ͅ\u06dd۪ۭ۟۠ۤۧۨँःाौ॑॔ॢॣঁঃীৄেৈো্ৢৣੀੂੇੈੋ੍ੰੱઁઃાૅેૉો્ଁଃାୃେୈୋ୍ୖୗஂஃாூெைொ்ఁఃాౄెైొ్ౕౖಂಃಾೄೆೈೊ್ೕೖംഃാൃെൈൊ്ิฺ็๎ິູົຼ່ໍ྄ཱ༘༙྆ྋྐྕྙྭྱྷ〪〯⃐⃜09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩〱〵ゝゞーヾ".charAt(i), "ֹֻֽׁׂًْ֑֣̀҃҆֡ۖۜ͠͡ͅ\u06dd۪ۭ۟۠ۤۧۨँःाौ॑॔ॢॣঁঃীৄেৈো্ৢৣੀੂੇੈੋ੍ੰੱઁઃાૅેૉો્ଁଃାୃେୈୋ୍ୖୗஂஃாூெைொ்ఁఃాౄెైొ్ౕౖಂಃಾೄೆೈೊ್ೕೖംഃാൃെൈൊ്ิฺ็๎ິູົຼ່ໍ྄ཱ༘༙྆ྋྐྕྙྭྱྷ〪〯⃐⃜09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩〱〵ゝゞーヾ".charAt(i + 1), 1);
      }

      for(i = 0; i < ":_ΆΌϚϜϞϠՙەऽলਫ਼ઍઽૠଽஜೞะຄຊຍລວະຽᄀᄉᄼᄾᅀᅌᅎᅐᅙᅣᅥᅧᅩᅵᆞᆨᆫᆺᇫᇰᇹὙὛὝιΩ℮〇".length(); ++i) {
         setCharType(":_ΆΌϚϜϞϠՙەऽলਫ਼ઍઽૠଽஜೞะຄຊຍລວະຽᄀᄉᄼᄾᅀᅌᅎᅐᅙᅣᅥᅧᅩᅵᆞᆨᆫᆺᇫᇰᇹὙὛὝιΩ℮〇".charAt(i), 2);
      }

      for(i = 0; i < "AZazÀÖØöøÿĀıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁΈΊΎΡΣώϐϖϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖաֆאתװײءغفيٱڷںھۀێېۓۥۦअहक़ॡঅঌএঐওনপরশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜੲੴઅઋએઑઓનપરલળવહଅଌଏଐଓନପରଲଳଶହଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೠೡഅഌഎഐഒനപഹൠൡกฮาำเๅກຂງຈດທນຟມຣສຫອຮາຳເໄཀཇཉཀྵႠჅაჶᄂᄃᄅᄇᄋᄌᄎᄒᅔᅕᅟᅡᅭᅮᅲᅳᆮᆯᆷᆸᆼᇂḀẛẠỹἀἕἘἝἠὅὈὍὐὗὟώᾀᾴᾶᾼῂῄῆῌῐΐῖΊῠῬῲῴῶῼKÅↀↂぁゔァヺㄅㄬ가힣一龥〡〩".length(); i += 2) {
         setCharType("AZazÀÖØöøÿĀıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁΈΊΎΡΣώϐϖϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖաֆאתװײءغفيٱڷںھۀێېۓۥۦअहक़ॡঅঌএঐওনপরশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜੲੴઅઋએઑઓનપરલળવહଅଌଏଐଓନପରଲଳଶହଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೠೡഅഌഎഐഒനപഹൠൡกฮาำเๅກຂງຈດທນຟມຣສຫອຮາຳເໄཀཇཉཀྵႠჅაჶᄂᄃᄅᄇᄋᄌᄎᄒᅔᅕᅟᅡᅭᅮᅲᅳᆮᆯᆷᆸᆼᇂḀẛẠỹἀἕἘἝἠὅὈὍὐὗὟώᾀᾴᾶᾼῂῄῆῌῐΐῖΊῠῬῲῴῶῼKÅↀↂぁゔァヺㄅㄬ가힣一龥〡〩".charAt(i), "AZazÀÖØöøÿĀıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁΈΊΎΡΣώϐϖϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖաֆאתװײءغفيٱڷںھۀێېۓۥۦअहक़ॡঅঌএঐওনপরশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜੲੴઅઋએઑઓનપરલળવહଅଌଏଐଓନପରଲଳଶହଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೠೡഅഌഎഐഒനപഹൠൡกฮาำเๅກຂງຈດທນຟມຣສຫອຮາຳເໄཀཇཉཀྵႠჅაჶᄂᄃᄅᄇᄋᄌᄎᄒᅔᅕᅟᅡᅭᅮᅲᅳᆮᆯᆷᆸᆼᇂḀẛẠỹἀἕἘἝἠὅὈὍὐὗὟώᾀᾴᾶᾼῂῄῆῌῐΐῖΊῠῬῲῴῶῼKÅↀↂぁゔァヺㄅㄬ가힣一龥〡〩".charAt(i + 1), 2);
      }

      byte[] other = new byte[256];

      for(int i = 0; i < 256; ++i) {
         if (charTypeTable[i] == null) {
            charTypeTable[i] = other;
         }
      }

   }
}
