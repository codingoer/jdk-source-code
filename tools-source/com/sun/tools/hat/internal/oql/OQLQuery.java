package com.sun.tools.hat.internal.oql;

class OQLQuery {
   String selectExpr;
   boolean isInstanceOf;
   String className;
   String identifier;
   String whereExpr;

   OQLQuery(String var1, boolean var2, String var3, String var4, String var5) {
      this.selectExpr = var1;
      this.isInstanceOf = var2;
      this.className = var3;
      this.identifier = var4;
      this.whereExpr = var5;
   }
}
