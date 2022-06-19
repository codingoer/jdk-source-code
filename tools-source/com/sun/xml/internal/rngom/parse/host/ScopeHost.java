package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public class ScopeHost extends GrammarSectionHost implements Scope {
   protected final Scope lhs;
   protected final Scope rhs;

   protected ScopeHost(Scope lhs, Scope rhs) {
      super(lhs, rhs);
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public ParsedPattern makeParentRef(String name, Location _loc, Annotations _anno) throws BuildException {
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedPatternHost(this.lhs.makeParentRef(name, loc.lhs, anno.lhs), this.rhs.makeParentRef(name, loc.rhs, anno.rhs));
   }

   public ParsedPattern makeRef(String name, Location _loc, Annotations _anno) throws BuildException {
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedPatternHost(this.lhs.makeRef(name, loc.lhs, anno.lhs), this.rhs.makeRef(name, loc.rhs, anno.rhs));
   }
}
