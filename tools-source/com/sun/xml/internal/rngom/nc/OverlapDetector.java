package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

class OverlapDetector implements NameClassVisitor {
   private NameClass nc1;
   private NameClass nc2;
   private boolean overlaps = false;
   static final String IMPOSSIBLE = "\u0000";

   private OverlapDetector(NameClass nc1, NameClass nc2) {
      this.nc1 = nc1;
      this.nc2 = nc2;
      nc1.accept(this);
      nc2.accept(this);
   }

   private void probe(QName name) {
      if (this.nc1.contains(name) && this.nc2.contains(name)) {
         this.overlaps = true;
      }

   }

   public Void visitChoice(NameClass nc1, NameClass nc2) {
      nc1.accept(this);
      nc2.accept(this);
      return null;
   }

   public Void visitNsName(String ns) {
      this.probe(new QName(ns, "\u0000"));
      return null;
   }

   public Void visitNsNameExcept(String ns, NameClass ex) {
      this.probe(new QName(ns, "\u0000"));
      ex.accept(this);
      return null;
   }

   public Void visitAnyName() {
      this.probe(new QName("\u0000", "\u0000"));
      return null;
   }

   public Void visitAnyNameExcept(NameClass ex) {
      this.probe(new QName("\u0000", "\u0000"));
      ex.accept(this);
      return null;
   }

   public Void visitName(QName name) {
      this.probe(name);
      return null;
   }

   public Void visitNull() {
      return null;
   }

   static boolean overlap(NameClass nc1, NameClass nc2) {
      SimpleNameClass snc;
      if (nc2 instanceof SimpleNameClass) {
         snc = (SimpleNameClass)nc2;
         return nc1.contains(snc.name);
      } else if (nc1 instanceof SimpleNameClass) {
         snc = (SimpleNameClass)nc1;
         return nc2.contains(snc.name);
      } else {
         return (new OverlapDetector(nc1, nc2)).overlaps;
      }
   }
}
