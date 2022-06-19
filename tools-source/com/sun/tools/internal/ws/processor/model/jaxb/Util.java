package com.sun.tools.internal.ws.processor.model.jaxb;

class Util {
   static final String MAGIC = "=:";
   static final String MAGIC0 = "=:0";
   static final String MAGIC1 = "=:1";
   static final String MAGIC2 = "=:2";

   static String replace(String macro, String... args) {
      int len = macro.length();
      StringBuilder buf = new StringBuilder(len);

      for(int i = 0; i < len; ++i) {
         char ch = macro.charAt(i);
         if (ch == '=' && i + 2 < len) {
            char tail = macro.charAt(i + 1);
            char ch2 = macro.charAt(i + 2);
            if ('0' <= ch2 && ch2 <= '9' && tail == ':') {
               buf.append(args[ch2 - 48]);
               i += 2;
               continue;
            }
         }

         buf.append(ch);
      }

      return buf.toString();
   }

   static String createMacroTemplate(String s) {
      return s;
   }
}
