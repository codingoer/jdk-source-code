package com.sun.xml.internal.xsom;

public interface XSModelGroup extends XSComponent, XSTerm, Iterable {
   Compositor ALL = XSModelGroup.Compositor.ALL;
   Compositor SEQUENCE = XSModelGroup.Compositor.SEQUENCE;
   Compositor CHOICE = XSModelGroup.Compositor.CHOICE;

   Compositor getCompositor();

   XSParticle getChild(int var1);

   int getSize();

   XSParticle[] getChildren();

   public static enum Compositor {
      ALL("all"),
      CHOICE("choice"),
      SEQUENCE("sequence");

      private final String value;

      private Compositor(String _value) {
         this.value = _value;
      }

      public String toString() {
         return this.value;
      }
   }
}
