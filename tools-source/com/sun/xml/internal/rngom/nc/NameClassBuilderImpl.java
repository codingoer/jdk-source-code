package com.sun.xml.internal.rngom.nc;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.builder.NameClassBuilder;
import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import java.util.List;

public class NameClassBuilderImpl implements NameClassBuilder {
   public NameClass makeChoice(List nameClasses, Location loc, Annotations anno) {
      NameClass result = (NameClass)nameClasses.get(0);

      for(int i = 1; i < nameClasses.size(); ++i) {
         result = new ChoiceNameClass((NameClass)result, (NameClass)nameClasses.get(i));
      }

      return (NameClass)result;
   }

   public NameClass makeName(String ns, String localName, String prefix, Location loc, Annotations anno) {
      return prefix == null ? new SimpleNameClass(ns, localName) : new SimpleNameClass(ns, localName, prefix);
   }

   public NameClass makeNsName(String ns, Location loc, Annotations anno) {
      return new NsNameClass(ns);
   }

   public NameClass makeNsName(String ns, NameClass except, Location loc, Annotations anno) {
      return new NsNameExceptNameClass(ns, except);
   }

   public NameClass makeAnyName(Location loc, Annotations anno) {
      return NameClass.ANY;
   }

   public NameClass makeAnyName(NameClass except, Location loc, Annotations anno) {
      return new AnyNameExceptNameClass(except);
   }

   public NameClass makeErrorNameClass() {
      return NameClass.NULL;
   }

   public NameClass annotate(NameClass nc, Annotations anno) throws BuildException {
      return nc;
   }

   public NameClass annotateAfter(NameClass nc, ParsedElementAnnotation e) throws BuildException {
      return nc;
   }

   public NameClass commentAfter(NameClass nc, CommentList comments) throws BuildException {
      return nc;
   }
}
