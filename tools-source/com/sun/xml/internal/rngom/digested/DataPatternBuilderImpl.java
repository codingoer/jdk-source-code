package com.sun.xml.internal.rngom.digested;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.DataPatternBuilder;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.parse.Context;
import org.xml.sax.Locator;

final class DataPatternBuilderImpl implements DataPatternBuilder {
   private final DDataPattern p = new DDataPattern();

   public DataPatternBuilderImpl(String datatypeLibrary, String type, Location loc) {
      this.p.location = (Locator)loc;
      this.p.datatypeLibrary = datatypeLibrary;
      this.p.type = type;
   }

   public void addParam(String name, String value, Context context, String ns, Location loc, Annotations anno) throws BuildException {
      this.p.params.add(this.p.new Param(name, value, context.copy(), ns, loc, (Annotation)anno));
   }

   public void annotation(ParsedElementAnnotation ea) {
   }

   public ParsedPattern makePattern(Location loc, Annotations anno) throws BuildException {
      return this.makePattern((ParsedPattern)null, loc, anno);
   }

   public ParsedPattern makePattern(ParsedPattern except, Location loc, Annotations anno) throws BuildException {
      this.p.except = (DPattern)except;
      if (anno != null) {
         this.p.annotation = ((Annotation)anno).getResult();
      }

      return this.p;
   }
}
