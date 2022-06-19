package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.DefaultExprFactory;
import com.sun.tools.corba.se.idl.constExpr.ExprFactory;

public class Factories {
   public GenFactory genFactory() {
      return null;
   }

   public SymtabFactory symtabFactory() {
      return new DefaultSymtabFactory();
   }

   public ExprFactory exprFactory() {
      return new DefaultExprFactory();
   }

   public Arguments arguments() {
      return new Arguments();
   }

   public String[] languageKeywords() {
      return null;
   }
}
