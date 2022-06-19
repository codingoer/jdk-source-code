package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.IncludedGrammar;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.builder.Scope;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedNameClass;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.nc.NameClass;
import com.sun.xml.internal.rngom.parse.Parseable;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Locator;

final class PatternParseable implements Parseable {
   private final DPattern pattern;

   public PatternParseable(DPattern p) {
      this.pattern = p;
   }

   public ParsedPattern parse(SchemaBuilder sb) throws BuildException {
      return (ParsedPattern)this.pattern.accept(new Parser(sb));
   }

   public ParsedPattern parseInclude(String uri, SchemaBuilder f, IncludedGrammar g, String inheritedNs) throws BuildException {
      throw new UnsupportedOperationException();
   }

   public ParsedPattern parseExternal(String uri, SchemaBuilder f, Scope s, String inheritedNs) throws BuildException {
      throw new UnsupportedOperationException();
   }

   private static class Parser implements DPatternVisitor {
      private final SchemaBuilder sb;

      public Parser(SchemaBuilder sb) {
         this.sb = sb;
      }

      private Annotations parseAnnotation(DPattern p) {
         return null;
      }

      private Location parseLocation(DPattern p) {
         Locator l = p.getLocation();
         return this.sb.makeLocation(l.getSystemId(), l.getLineNumber(), l.getColumnNumber());
      }

      private ParsedNameClass parseNameClass(NameClass name) {
         return name;
      }

      public ParsedPattern onAttribute(DAttributePattern p) {
         return this.sb.makeAttribute(this.parseNameClass(p.getName()), (ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onChoice(DChoicePattern p) {
         List kids = new ArrayList();

         for(DPattern c = p.firstChild(); c != null; c = c.next) {
            kids.add((ParsedPattern)c.accept(this));
         }

         return this.sb.makeChoice(kids, this.parseLocation(p), (Annotations)null);
      }

      public ParsedPattern onData(DDataPattern p) {
         return null;
      }

      public ParsedPattern onElement(DElementPattern p) {
         return this.sb.makeElement(this.parseNameClass(p.getName()), (ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onEmpty(DEmptyPattern p) {
         return this.sb.makeEmpty(this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onGrammar(DGrammarPattern p) {
         return null;
      }

      public ParsedPattern onGroup(DGroupPattern p) {
         List kids = new ArrayList();

         for(DPattern c = p.firstChild(); c != null; c = c.next) {
            kids.add((ParsedPattern)c.accept(this));
         }

         return this.sb.makeGroup(kids, this.parseLocation(p), (Annotations)null);
      }

      public ParsedPattern onInterleave(DInterleavePattern p) {
         List kids = new ArrayList();

         for(DPattern c = p.firstChild(); c != null; c = c.next) {
            kids.add((ParsedPattern)c.accept(this));
         }

         return this.sb.makeInterleave(kids, this.parseLocation(p), (Annotations)null);
      }

      public ParsedPattern onList(DListPattern p) {
         return this.sb.makeList((ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onMixed(DMixedPattern p) {
         return this.sb.makeMixed((ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onNotAllowed(DNotAllowedPattern p) {
         return this.sb.makeNotAllowed(this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onOneOrMore(DOneOrMorePattern p) {
         return this.sb.makeOneOrMore((ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onOptional(DOptionalPattern p) {
         return this.sb.makeOptional((ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onRef(DRefPattern p) {
         return null;
      }

      public ParsedPattern onText(DTextPattern p) {
         return this.sb.makeText(this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onValue(DValuePattern p) {
         return this.sb.makeValue(p.getDatatypeLibrary(), p.getType(), p.getValue(), p.getContext(), p.getNs(), this.parseLocation(p), this.parseAnnotation(p));
      }

      public ParsedPattern onZeroOrMore(DZeroOrMorePattern p) {
         return this.sb.makeZeroOrMore((ParsedPattern)p.getChild().accept(this), this.parseLocation(p), this.parseAnnotation(p));
      }
   }
}
