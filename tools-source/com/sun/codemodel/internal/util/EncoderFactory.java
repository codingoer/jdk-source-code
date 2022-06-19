package com.sun.codemodel.internal.util;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class EncoderFactory {
   public static CharsetEncoder createEncoder(String encodin) {
      Charset cs = Charset.forName(System.getProperty("file.encoding"));
      CharsetEncoder encoder = cs.newEncoder();
      if (cs.getClass().getName().equals("sun.nio.cs.MS1252")) {
         try {
            Class ms1252encoder = Class.forName("com.sun.codemodel.internal.util.MS1252Encoder");
            Constructor c = ms1252encoder.getConstructor(Charset.class);
            return (CharsetEncoder)c.newInstance(cs);
         } catch (Throwable var5) {
            return encoder;
         }
      } else {
         return encoder;
      }
   }
}
