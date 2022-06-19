package com.sun.tools.internal.xjc.reader.dtd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ModelGroup extends Term {
   Kind kind;
   private final List terms = new ArrayList();

   void normalize(List r, boolean optional) {
      switch (this.kind) {
         case SEQUENCE:
            Iterator var5 = this.terms.iterator();

            while(var5.hasNext()) {
               Term t = (Term)var5.next();
               t.normalize(r, optional);
            }

            return;
         case CHOICE:
            Block b = new Block(this.isOptional() || optional, this.isRepeated());
            this.addAllElements(b);
            r.add(b);
            return;
         default:
      }
   }

   void addAllElements(Block b) {
      Iterator var2 = this.terms.iterator();

      while(var2.hasNext()) {
         Term t = (Term)var2.next();
         t.addAllElements(b);
      }

   }

   boolean isOptional() {
      Iterator var1;
      Term t;
      switch (this.kind) {
         case SEQUENCE:
            var1 = this.terms.iterator();

            do {
               if (!var1.hasNext()) {
                  return true;
               }

               t = (Term)var1.next();
            } while(t.isOptional());

            return false;
         case CHOICE:
            var1 = this.terms.iterator();

            do {
               if (!var1.hasNext()) {
                  return false;
               }

               t = (Term)var1.next();
            } while(!t.isOptional());

            return true;
         default:
            throw new IllegalArgumentException();
      }
   }

   boolean isRepeated() {
      switch (this.kind) {
         case SEQUENCE:
            return true;
         case CHOICE:
            Iterator var1 = this.terms.iterator();

            Term t;
            do {
               if (!var1.hasNext()) {
                  return false;
               }

               t = (Term)var1.next();
            } while(!t.isRepeated());

            return true;
         default:
            throw new IllegalArgumentException();
      }
   }

   void setKind(short connectorType) {
      Kind k;
      switch (connectorType) {
         case 0:
            k = ModelGroup.Kind.CHOICE;
            break;
         case 1:
            k = ModelGroup.Kind.SEQUENCE;
            break;
         default:
            throw new IllegalArgumentException();
      }

      assert this.kind == null || k == this.kind;

      this.kind = k;
   }

   void addTerm(Term t) {
      if (t instanceof ModelGroup) {
         ModelGroup mg = (ModelGroup)t;
         if (mg.kind == this.kind) {
            this.terms.addAll(mg.terms);
            return;
         }
      }

      this.terms.add(t);
   }

   Term wrapUp() {
      switch (this.terms.size()) {
         case 0:
            return EMPTY;
         case 1:
            assert this.kind == null;

            return (Term)this.terms.get(0);
         default:
            assert this.kind != null;

            return this;
      }
   }

   static enum Kind {
      CHOICE,
      SEQUENCE;
   }
}
