package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;

final class ElementAnnotationBuilderHost extends AnnotationsHost implements ElementAnnotationBuilder {
   final ElementAnnotationBuilder lhs;
   final ElementAnnotationBuilder rhs;

   ElementAnnotationBuilderHost(ElementAnnotationBuilder lhs, ElementAnnotationBuilder rhs) {
      super(lhs, rhs);
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public void addText(String value, Location _loc, CommentList _comments) throws BuildException {
      LocationHost loc = this.cast(_loc);
      CommentListHost comments = (CommentListHost)_comments;
      this.lhs.addText(value, loc.lhs, comments == null ? null : comments.lhs);
      this.rhs.addText(value, loc.rhs, comments == null ? null : comments.rhs);
   }

   public ParsedElementAnnotation makeElementAnnotation() throws BuildException {
      return new ParsedElementAnnotationHost(this.lhs.makeElementAnnotation(), this.rhs.makeElementAnnotation());
   }
}
