package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.Parseable;
import org.xml.sax.Locator;

public abstract class DPattern implements ParsedPattern {
   Locator location;
   DAnnotation annotation;
   DPattern next;
   DPattern prev;

   public Locator getLocation() {
      return this.location;
   }

   public DAnnotation getAnnotation() {
      return this.annotation == null ? DAnnotation.EMPTY : this.annotation;
   }

   public abstract boolean isNullable();

   public abstract Object accept(DPatternVisitor var1);

   public Parseable createParseable() {
      return new PatternParseable(this);
   }

   public final boolean isElement() {
      return this instanceof DElementPattern;
   }

   public final boolean isAttribute() {
      return this instanceof DAttributePattern;
   }
}
