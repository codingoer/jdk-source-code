package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.builder.NameClassBuilder;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedNameClass;
import java.util.ArrayList;
import java.util.List;

final class NameClassBuilderHost extends Base implements NameClassBuilder {
   final NameClassBuilder lhs;
   final NameClassBuilder rhs;

   NameClassBuilderHost(NameClassBuilder lhs, NameClassBuilder rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public ParsedNameClass annotate(ParsedNameClass _nc, Annotations _anno) throws BuildException {
      ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.annotate(nc.lhs, anno.lhs), this.rhs.annotate(nc.rhs, anno.rhs));
   }

   public ParsedNameClass annotateAfter(ParsedNameClass _nc, ParsedElementAnnotation _e) throws BuildException {
      ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
      ParsedElementAnnotationHost e = (ParsedElementAnnotationHost)_e;
      return new ParsedNameClassHost(this.lhs.annotateAfter(nc.lhs, e.lhs), this.rhs.annotateAfter(nc.rhs, e.rhs));
   }

   public ParsedNameClass commentAfter(ParsedNameClass _nc, CommentList _comments) throws BuildException {
      ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
      CommentListHost comments = (CommentListHost)_comments;
      return new ParsedNameClassHost(this.lhs.commentAfter(nc.lhs, comments == null ? null : comments.lhs), this.rhs.commentAfter(nc.rhs, comments == null ? null : comments.rhs));
   }

   public ParsedNameClass makeChoice(List _nameClasses, Location _loc, Annotations _anno) {
      List lnc = new ArrayList();
      List rnc = new ArrayList();

      for(int i = 0; i < _nameClasses.size(); ++i) {
         lnc.add(((ParsedNameClassHost)_nameClasses.get(i)).lhs);
         rnc.add(((ParsedNameClassHost)_nameClasses.get(i)).rhs);
      }

      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.makeChoice(lnc, loc.lhs, anno.lhs), this.rhs.makeChoice(rnc, loc.rhs, anno.rhs));
   }

   public ParsedNameClass makeName(String ns, String localName, String prefix, Location _loc, Annotations _anno) {
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.makeName(ns, localName, prefix, loc.lhs, anno.lhs), this.rhs.makeName(ns, localName, prefix, loc.rhs, anno.rhs));
   }

   public ParsedNameClass makeNsName(String ns, Location _loc, Annotations _anno) {
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.makeNsName(ns, loc.lhs, anno.lhs), this.rhs.makeNsName(ns, loc.rhs, anno.rhs));
   }

   public ParsedNameClass makeNsName(String ns, ParsedNameClass _except, Location _loc, Annotations _anno) {
      ParsedNameClassHost except = (ParsedNameClassHost)_except;
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.makeNsName(ns, except.lhs, loc.lhs, anno.lhs), this.rhs.makeNsName(ns, except.rhs, loc.rhs, anno.rhs));
   }

   public ParsedNameClass makeAnyName(Location _loc, Annotations _anno) {
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.makeAnyName(loc.lhs, anno.lhs), this.rhs.makeAnyName(loc.rhs, anno.rhs));
   }

   public ParsedNameClass makeAnyName(ParsedNameClass _except, Location _loc, Annotations _anno) {
      ParsedNameClassHost except = (ParsedNameClassHost)_except;
      LocationHost loc = this.cast(_loc);
      AnnotationsHost anno = this.cast(_anno);
      return new ParsedNameClassHost(this.lhs.makeAnyName(except.lhs, loc.lhs, anno.lhs), this.rhs.makeAnyName(except.rhs, loc.rhs, anno.rhs));
   }

   public ParsedNameClass makeErrorNameClass() {
      return new ParsedNameClassHost(this.lhs.makeErrorNameClass(), this.rhs.makeErrorNameClass());
   }
}
