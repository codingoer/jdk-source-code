package com.sun.codemodel.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JMods implements JGenerable {
   private static int VAR = 8;
   private static int FIELD = 799;
   private static int METHOD = 255;
   private static int CLASS = 63;
   private static int INTERFACE = 1;
   private int mods;

   private JMods(int mods) {
      this.mods = mods;
   }

   public int getValue() {
      return this.mods;
   }

   private static void check(int mods, int legal, String what) {
      if ((mods & ~legal) != 0) {
         throw new IllegalArgumentException("Illegal modifiers for " + what + ": " + (new JMods(mods)).toString());
      }
   }

   static JMods forVar(int mods) {
      check(mods, VAR, "variable");
      return new JMods(mods);
   }

   static JMods forField(int mods) {
      check(mods, FIELD, "field");
      return new JMods(mods);
   }

   static JMods forMethod(int mods) {
      check(mods, METHOD, "method");
      return new JMods(mods);
   }

   static JMods forClass(int mods) {
      check(mods, CLASS, "class");
      return new JMods(mods);
   }

   static JMods forInterface(int mods) {
      check(mods, INTERFACE, "class");
      return new JMods(mods);
   }

   public boolean isAbstract() {
      return (this.mods & 32) != 0;
   }

   public boolean isNative() {
      return (this.mods & 64) != 0;
   }

   public boolean isSynchronized() {
      return (this.mods & 128) != 0;
   }

   public void setSynchronized(boolean newValue) {
      this.setFlag(128, newValue);
   }

   public void setPrivate() {
      this.setFlag(1, false);
      this.setFlag(2, false);
      this.setFlag(4, true);
   }

   public void setProtected() {
      this.setFlag(1, false);
      this.setFlag(2, true);
      this.setFlag(4, false);
   }

   public void setPublic() {
      this.setFlag(1, true);
      this.setFlag(2, false);
      this.setFlag(4, false);
   }

   public void setFinal(boolean newValue) {
      this.setFlag(8, newValue);
   }

   private void setFlag(int bit, boolean newValue) {
      this.mods = this.mods & ~bit | (newValue ? bit : 0);
   }

   public void generate(JFormatter f) {
      if ((this.mods & 1) != 0) {
         f.p("public");
      }

      if ((this.mods & 2) != 0) {
         f.p("protected");
      }

      if ((this.mods & 4) != 0) {
         f.p("private");
      }

      if ((this.mods & 8) != 0) {
         f.p("final");
      }

      if ((this.mods & 16) != 0) {
         f.p("static");
      }

      if ((this.mods & 32) != 0) {
         f.p("abstract");
      }

      if ((this.mods & 64) != 0) {
         f.p("native");
      }

      if ((this.mods & 128) != 0) {
         f.p("synchronized");
      }

      if ((this.mods & 256) != 0) {
         f.p("transient");
      }

      if ((this.mods & 512) != 0) {
         f.p("volatile");
      }

   }

   public String toString() {
      StringWriter s = new StringWriter();
      JFormatter f = new JFormatter(new PrintWriter(s));
      this.generate(f);
      return s.toString();
   }
}
