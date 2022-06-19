package com.sun.tools.internal.xjc.generator.util;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JStringLiteral;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;

public abstract class WhitespaceNormalizer {
   public static final WhitespaceNormalizer PRESERVE = new WhitespaceNormalizer() {
      public JExpression generate(JCodeModel codeModel, JExpression literal) {
         return literal;
      }
   };
   public static final WhitespaceNormalizer REPLACE = new WhitespaceNormalizer() {
      public JExpression generate(JCodeModel codeModel, JExpression literal) {
         return (JExpression)(literal instanceof JStringLiteral ? JExpr.lit(WhiteSpaceProcessor.replace(((JStringLiteral)literal).str)) : codeModel.ref(WhiteSpaceProcessor.class).staticInvoke("replace").arg(literal));
      }
   };
   public static final WhitespaceNormalizer COLLAPSE = new WhitespaceNormalizer() {
      public JExpression generate(JCodeModel codeModel, JExpression literal) {
         return (JExpression)(literal instanceof JStringLiteral ? JExpr.lit(WhiteSpaceProcessor.collapse(((JStringLiteral)literal).str)) : codeModel.ref(WhiteSpaceProcessor.class).staticInvoke("collapse").arg(literal));
      }
   };

   public abstract JExpression generate(JCodeModel var1, JExpression var2);

   public static WhitespaceNormalizer parse(String method) {
      if (method.equals("preserve")) {
         return PRESERVE;
      } else if (method.equals("replace")) {
         return REPLACE;
      } else if (method.equals("collapse")) {
         return COLLAPSE;
      } else {
         throw new IllegalArgumentException(method);
      }
   }
}
