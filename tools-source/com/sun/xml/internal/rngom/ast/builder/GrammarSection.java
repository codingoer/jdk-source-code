package com.sun.xml.internal.rngom.ast.builder;

import com.sun.xml.internal.rngom.ast.om.Location;
import com.sun.xml.internal.rngom.ast.om.ParsedElementAnnotation;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;

public interface GrammarSection {
   Combine COMBINE_CHOICE = new Combine("choice");
   Combine COMBINE_INTERLEAVE = new Combine("interleave");
   String START = "\u0000#start\u0000";

   void define(String var1, Combine var2, ParsedPattern var3, Location var4, Annotations var5) throws BuildException;

   void topLevelAnnotation(ParsedElementAnnotation var1) throws BuildException;

   void topLevelComment(CommentList var1) throws BuildException;

   Div makeDiv();

   Include makeInclude();

   public static final class Combine {
      private final String name;

      private Combine(String name) {
         this.name = name;
      }

      public final String toString() {
         return this.name;
      }

      // $FF: synthetic method
      Combine(String x0, Object x1) {
         this(x0);
      }
   }
}
