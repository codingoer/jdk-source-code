package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.builder.Div;
import com.sun.xml.internal.rngom.ast.builder.Grammar;
import com.sun.xml.internal.rngom.ast.builder.GrammarSection;
import com.sun.xml.internal.rngom.ast.builder.Include;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.ast.util.LocatorImpl;
import java.util.ArrayList;
import java.util.List;

class GrammarBuilderImpl implements Grammar, Div {
   protected final DGrammarPattern grammar;
   protected final Scope parent;
   protected final DSchemaBuilderImpl sb;
   private List additionalElementAnnotations;

   public GrammarBuilderImpl(DGrammarPattern p, Scope parent, DSchemaBuilderImpl sb) {
      this.grammar = p;
      this.parent = parent;
      this.sb = sb;
   }

   public ParsedPattern endGrammar(Location loc, Annotations anno) throws BuildException {
      if (anno != null && this.grammar.annotation != null) {
         this.grammar.annotation.contents.addAll(((Annotation)anno).getResult().contents);
      }

      return this.grammar;
   }

   public void endDiv(Location loc, Annotations anno) throws BuildException {
   }

   public void define(String name, GrammarSection.Combine combine, ParsedPattern pattern, Location loc, Annotations anno) throws BuildException {
      if (name == "\u0000#start\u0000") {
         this.grammar.start = (DPattern)pattern;
      } else {
         DDefine d = this.grammar.getOrAdd(name);
         d.setPattern((DPattern)pattern);
         if (anno != null) {
            d.annotation = ((Annotation)anno).getResult();
         }
      }

   }

   public void topLevelAnnotation(ParsedElementAnnotation ea) throws BuildException {
      if (this.additionalElementAnnotations == null) {
         this.additionalElementAnnotations = new ArrayList();
      }

      this.additionalElementAnnotations.add(((ElementWrapper)ea).element);
      if (this.grammar.annotation == null) {
         this.grammar.annotation = new DAnnotation();
      }

      this.grammar.annotation.contents.addAll(this.additionalElementAnnotations);
   }

   public void topLevelComment(CommentList comments) throws BuildException {
   }

   public Div makeDiv() {
      return this;
   }

   public Include makeInclude() {
      return new IncludeImpl(this.grammar, this.parent, this.sb);
   }

   public ParsedPattern makeParentRef(String name, Location loc, Annotations anno) throws BuildException {
      return this.parent.makeRef(name, loc, anno);
   }

   public ParsedPattern makeRef(String name, Location loc, Annotations anno) throws BuildException {
      return DSchemaBuilderImpl.wrap(new DRefPattern(this.grammar.getOrAdd(name)), (LocatorImpl)loc, (Annotation)anno);
   }
}
