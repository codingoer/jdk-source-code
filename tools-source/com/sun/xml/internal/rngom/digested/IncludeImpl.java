package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.GrammarSection;
import com.sun.xml.internal.rngom.ast.builder.Include;
import com.sun.xml.internal.rngom.ast.builder.IncludedGrammar;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;
import java.util.HashSet;
import java.util.Set;

final class IncludeImpl extends GrammarBuilderImpl implements Include {
   private Set overridenPatterns = new HashSet();
   private boolean startOverriden = false;

   public IncludeImpl(DGrammarPattern p, Scope parent, DSchemaBuilderImpl sb) {
      super(p, parent, sb);
   }

   public void define(String name, GrammarSection.Combine combine, ParsedPattern pattern, Location loc, Annotations anno) throws BuildException {
      super.define(name, combine, pattern, loc, anno);
      if (name == "\u0000#start\u0000") {
         this.startOverriden = true;
      } else {
         this.overridenPatterns.add(name);
      }

   }

   public void endInclude(Parseable current, String uri, String ns, Location loc, Annotations anno) throws BuildException, IllegalSchemaException {
      current.parseInclude(uri, this.sb, new IncludedGrammarImpl(this.grammar, this.parent, this.sb), ns);
   }

   private class IncludedGrammarImpl extends GrammarBuilderImpl implements IncludedGrammar {
      public IncludedGrammarImpl(DGrammarPattern p, Scope parent, DSchemaBuilderImpl sb) {
         super(p, parent, sb);
      }

      public void define(String name, GrammarSection.Combine combine, ParsedPattern pattern, Location loc, Annotations anno) throws BuildException {
         if (name == "\u0000#start\u0000") {
            if (IncludeImpl.this.startOverriden) {
               return;
            }
         } else if (IncludeImpl.this.overridenPatterns.contains(name)) {
            return;
         }

         super.define(name, combine, pattern, loc, anno);
      }

      public ParsedPattern endIncludedGrammar(Location loc, Annotations anno) throws BuildException {
         return null;
      }
   }
}
