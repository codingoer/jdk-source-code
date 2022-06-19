package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.builder.Div;
import com.sun.xml.internal.rngom.ast.builder.GrammarSection;
import com.sun.xml.internal.rngom.ast.builder.Include;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public class GrammarSectionHost extends Base implements GrammarSection {
   private final GrammarSection lhs;
   private final GrammarSection rhs;

   GrammarSectionHost(GrammarSection lhs, GrammarSection rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
      if (lhs == null || rhs == null) {
         throw new IllegalArgumentException();
      }
   }

   public void define(String name, GrammarSection.Combine combine, ParsedPattern _pattern, Location _loc, Annotations _anno) throws BuildException {
      ParsedPatternHost pattern = (ParsedPatternHost)_pattern;
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      this.lhs.define(name, combine, pattern.lhs, loc.lhs, anno.lhs);
      this.rhs.define(name, combine, pattern.rhs, loc.rhs, anno.rhs);
   }

   public Div makeDiv() {
      return new DivHost(this.lhs.makeDiv(), this.rhs.makeDiv());
   }

   public Include makeInclude() {
      Include l = this.lhs.makeInclude();
      return l == null ? null : new IncludeHost(l, this.rhs.makeInclude());
   }

   public void topLevelAnnotation(ParsedElementAnnotation _ea) throws BuildException {
      ParsedElementAnnotationHost ea = (ParsedElementAnnotationHost)_ea;
      this.lhs.topLevelAnnotation(ea == null ? null : ea.lhs);
      this.rhs.topLevelAnnotation(ea == null ? null : ea.rhs);
   }

   public void topLevelComment(CommentList _comments) throws BuildException {
      CommentListHost comments = (CommentListHost)_comments;
      this.lhs.topLevelComment(comments == null ? null : comments.lhs);
      this.rhs.topLevelComment(comments == null ? null : comments.rhs);
   }
}
