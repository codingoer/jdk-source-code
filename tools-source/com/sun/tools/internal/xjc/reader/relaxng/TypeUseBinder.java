package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;
import com.sun.xml.internal.rngom.digested.DAttributePattern;
import com.sun.xml.internal.rngom.digested.DChoicePattern;
import com.sun.xml.internal.rngom.digested.DContainerPattern;
import com.sun.xml.internal.rngom.digested.DDataPattern;
import com.sun.xml.internal.rngom.digested.DElementPattern;
import com.sun.xml.internal.rngom.digested.DEmptyPattern;
import com.sun.xml.internal.rngom.digested.DGrammarPattern;
import com.sun.xml.internal.rngom.digested.DGroupPattern;
import com.sun.xml.internal.rngom.digested.DInterleavePattern;
import com.sun.xml.internal.rngom.digested.DListPattern;
import com.sun.xml.internal.rngom.digested.DMixedPattern;
import com.sun.xml.internal.rngom.digested.DNotAllowedPattern;
import com.sun.xml.internal.rngom.digested.DOneOrMorePattern;
import com.sun.xml.internal.rngom.digested.DOptionalPattern;
import com.sun.xml.internal.rngom.digested.DPattern;
import com.sun.xml.internal.rngom.digested.DPatternVisitor;
import com.sun.xml.internal.rngom.digested.DRefPattern;
import com.sun.xml.internal.rngom.digested.DTextPattern;
import com.sun.xml.internal.rngom.digested.DValuePattern;
import com.sun.xml.internal.rngom.digested.DZeroOrMorePattern;
import java.util.Iterator;

final class TypeUseBinder implements DPatternVisitor {
   private final RELAXNGCompiler compiler;

   public TypeUseBinder(RELAXNGCompiler compiler) {
      this.compiler = compiler;
   }

   public TypeUse onGrammar(DGrammarPattern p) {
      return CBuiltinLeafInfo.STRING;
   }

   public TypeUse onChoice(DChoicePattern p) {
      return CBuiltinLeafInfo.STRING;
   }

   public TypeUse onData(DDataPattern p) {
      return this.onDataType(p.getDatatypeLibrary(), p.getType());
   }

   public TypeUse onValue(DValuePattern p) {
      return this.onDataType(p.getDatatypeLibrary(), p.getType());
   }

   private TypeUse onDataType(String datatypeLibrary, String type) {
      DatatypeLib lib = (DatatypeLib)this.compiler.datatypes.get(datatypeLibrary);
      if (lib != null) {
         TypeUse use = lib.get(type);
         if (use != null) {
            return use;
         }
      }

      return CBuiltinLeafInfo.STRING;
   }

   public TypeUse onInterleave(DInterleavePattern p) {
      return this.onContainer(p);
   }

   public TypeUse onGroup(DGroupPattern p) {
      return this.onContainer(p);
   }

   private TypeUse onContainer(DContainerPattern p) {
      TypeUse t = null;

      TypeUse s;
      for(Iterator var3 = p.iterator(); var3.hasNext(); t = s) {
         DPattern child = (DPattern)var3.next();
         s = (TypeUse)child.accept(this);
         if (t != null && t != s) {
            return CBuiltinLeafInfo.STRING;
         }
      }

      return t;
   }

   public TypeUse onNotAllowed(DNotAllowedPattern p) {
      return this.error();
   }

   public TypeUse onEmpty(DEmptyPattern p) {
      return CBuiltinLeafInfo.STRING;
   }

   public TypeUse onList(DListPattern p) {
      return (TypeUse)p.getChild().accept(this);
   }

   public TypeUse onOneOrMore(DOneOrMorePattern p) {
      return TypeUseFactory.makeCollection((TypeUse)p.getChild().accept(this));
   }

   public TypeUse onZeroOrMore(DZeroOrMorePattern p) {
      return TypeUseFactory.makeCollection((TypeUse)p.getChild().accept(this));
   }

   public TypeUse onOptional(DOptionalPattern p) {
      return CBuiltinLeafInfo.STRING;
   }

   public TypeUse onRef(DRefPattern p) {
      return (TypeUse)p.getTarget().getPattern().accept(this);
   }

   public TypeUse onText(DTextPattern p) {
      return CBuiltinLeafInfo.STRING;
   }

   public TypeUse onAttribute(DAttributePattern p) {
      return this.error();
   }

   public TypeUse onElement(DElementPattern p) {
      return this.error();
   }

   public TypeUse onMixed(DMixedPattern p) {
      return this.error();
   }

   private TypeUse error() {
      throw new IllegalStateException();
   }
}
