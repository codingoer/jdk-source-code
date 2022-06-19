package com.sun.xml.internal.rngom.nc;

import com.sun.xml.internal.rngom.ast.om.ParsedNameClass;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class NameClass implements ParsedNameClass, Serializable {
   static final int SPECIFICITY_NONE = -1;
   static final int SPECIFICITY_ANY_NAME = 0;
   static final int SPECIFICITY_NS_NAME = 1;
   static final int SPECIFICITY_NAME = 2;
   public static final NameClass ANY = new AnyNameClass();
   public static final NameClass NULL = new NullNameClass();

   public abstract boolean contains(QName var1);

   public abstract int containsSpecificity(QName var1);

   public abstract Object accept(NameClassVisitor var1);

   public abstract boolean isOpen();

   public Set listNames() {
      final Set names = new HashSet();
      this.accept(new NameClassWalker() {
         public Void visitName(QName name) {
            names.add(name);
            return null;
         }
      });
      return names;
   }

   public final boolean hasOverlapWith(NameClass nc2) {
      return OverlapDetector.overlap(this, nc2);
   }
}
