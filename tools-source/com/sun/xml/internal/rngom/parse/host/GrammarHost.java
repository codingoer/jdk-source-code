package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.Grammar;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public class GrammarHost extends ScopeHost implements Grammar {
   final Grammar lhs;
   final Grammar rhs;

   public GrammarHost(Grammar lhs, Grammar rhs) {
      super(lhs, rhs);
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public ParsedPattern endGrammar(Location _loc, Annotations _anno) throws BuildException {
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedPatternHost(this.lhs.endGrammar(loc.lhs, anno.lhs), this.rhs.endGrammar(loc.rhs, anno.rhs));
   }
}
