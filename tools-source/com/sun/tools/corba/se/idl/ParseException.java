package com.sun.tools.corba.se.idl;

class ParseException extends Exception {
   private static String filename = "";
   private static String line = "";
   private static int lineNumber = 0;
   private static String pointer = "^";
   static boolean detected = false;

   ParseException(String var1) {
      super(var1);
      System.err.println(var1);
      detected = true;
   }

   ParseException(String var1, boolean var2) {
      super(var1);
      System.err.println(var1);
      if (!var2) {
         detected = true;
      }

   }

   static ParseException abstractValueBox(Scanner var0) {
      return arg0("abstractValueBox", var0);
   }

   static ParseException alreadyDeclared(Scanner var0, String var1) {
      return arg1("alreadyDeclared", var0, var1);
   }

   static ParseException declNotInSameFile(Scanner var0, String var1, String var2) {
      return arg2("declNotInSameFile", var0, var1, var2);
   }

   static ParseException alreadyDefaulted(Scanner var0) {
      return arg0("alreadydefaulted", var0);
   }

   static ParseException alreadyDerived(Scanner var0, String var1, String var2) {
      return arg2("alreadyDerived", var0, var1, var2);
   }

   static ParseException alreadyRaised(Scanner var0, String var1) {
      return arg1("alreadyRaised", var0, var1);
   }

   static ParseException attributeNotType(Scanner var0, String var1) {
      return arg1("attributeNotType", var0, var1);
   }

   static ParseException badAbstract(Scanner var0, String var1) {
      return arg1("badAbstract", var0, var1);
   }

   static ParseException badCustom(Scanner var0) {
      return arg0("badCustom", var0);
   }

   static ParseException badRepIDAlreadyAssigned(Scanner var0, String var1) {
      return arg1("badRepIDAlreadyAssigned", var0, var1);
   }

   static ParseException badRepIDForm(Scanner var0, String var1) {
      return arg1("badRepIDForm", var0, var1);
   }

   static ParseException badRepIDPrefix(Scanner var0, String var1, String var2, String var3) {
      return arg3("badRepIDPrefix", var0, var1, var2, var3);
   }

   static ParseException badState(Scanner var0, String var1) {
      return arg1("badState", var0, var1);
   }

   static ParseException branchLabel(Scanner var0, String var1) {
      return arg1("branchLabel", var0, var1);
   }

   static ParseException branchName(Scanner var0, String var1) {
      return arg1("branchName", var0, var1);
   }

   static ParseException duplicateInit(Scanner var0) {
      return arg0("duplicateInit", var0);
   }

   static ParseException duplicateState(Scanner var0, String var1) {
      return arg1("duplicateState", var0, var1);
   }

   static ParseException elseNoIf(Scanner var0) {
      return arg0("elseNoIf", var0);
   }

   static ParseException endNoIf(Scanner var0) {
      return arg0("endNoIf", var0);
   }

   static ParseException evaluationError(Scanner var0, String var1) {
      return arg1("evaluation", var0, var1);
   }

   static ParseException forwardEntry(Scanner var0, String var1) {
      return arg1("forwardEntry", var0, var1);
   }

   static ParseException forwardedValueBox(Scanner var0, String var1) {
      return arg1("forwardedValueBox", var0, var1);
   }

   static ParseException generic(Scanner var0, String var1) {
      return arg1("generic", var0, var1);
   }

   static ParseException illegalArray(Scanner var0, String var1) {
      return arg1("illegalArray", var0, var1);
   }

   static ParseException illegalException(Scanner var0, String var1) {
      return arg1("illegalException", var0, var1);
   }

   static ParseException invalidConst(Scanner var0, String var1, String var2) {
      return arg2("invalidConst1", var0, var1, var2);
   }

   static ParseException invalidConst(Scanner var0, String var1) {
      return arg1("invalidConst2", var0, var1);
   }

   static ParseException keywordCollision(Scanner var0, String var1) {
      return arg1("keywordCollision", var0, var1);
   }

   static ParseException deprecatedKeywordWarning(Scanner var0, String var1) {
      return arg1Warning("deprecatedKeywordWarning", var0, var1);
   }

   static ParseException keywordCollisionWarning(Scanner var0, String var1) {
      return arg1Warning("keywordCollisionWarning", var0, var1);
   }

   static ParseException methodClash(Scanner var0, String var1, String var2) {
      return arg2("methodClash", var0, var1, var2);
   }

   static ParseException moduleNotType(Scanner var0, String var1) {
      return arg1("moduleNotType", var0, var1);
   }

   static ParseException nestedValueBox(Scanner var0) {
      return arg0("nestedValueBox", var0);
   }

   static ParseException noDefault(Scanner var0) {
      return arg0("noDefault", var0);
   }

   static ParseException nonAbstractParent(Scanner var0, String var1, String var2) {
      return arg2("nonAbstractParent", var0, var1, var2);
   }

   static ParseException nonAbstractParent2(Scanner var0, String var1, String var2) {
      return arg2("nonAbstractParent2", var0, var1, var2);
   }

   static ParseException nonAbstractParent3(Scanner var0, String var1, String var2) {
      return arg2("nonAbstractParent3", var0, var1, var2);
   }

   static ParseException notANumber(Scanner var0, String var1) {
      return arg1("notANumber", var0, var1);
   }

   static ParseException nothing(String var0) {
      return new ParseException(Util.getMessage("ParseException.nothing", var0));
   }

   static ParseException notPositiveInt(Scanner var0, String var1) {
      return arg1("notPosInt", var0, var1);
   }

   static ParseException oneway(Scanner var0, String var1) {
      return arg1("oneway", var0, var1);
   }

   static ParseException operationNotType(Scanner var0, String var1) {
      return arg1("operationNotType", var0, var1);
   }

   static ParseException outOfRange(Scanner var0, String var1, String var2) {
      return arg2("outOfRange", var0, var1, var2);
   }

   static ParseException recursive(Scanner var0, String var1, String var2) {
      return arg2("recursive", var0, var1, var2);
   }

   static ParseException selfInherit(Scanner var0, String var1) {
      return arg1("selfInherit", var0, var1);
   }

   static ParseException stringTooLong(Scanner var0, String var1, String var2) {
      return arg2("stringTooLong", var0, var1, var2);
   }

   static ParseException syntaxError(Scanner var0, int var1, int var2) {
      return arg2("syntax1", var0, Token.toString(var1), Token.toString(var2));
   }

   static ParseException syntaxError(Scanner var0, String var1, String var2) {
      return arg2("syntax1", var0, var1, var2);
   }

   static ParseException syntaxError(Scanner var0, int[] var1, int var2) {
      return syntaxError(var0, var1, Token.toString(var2));
   }

   static ParseException syntaxError(Scanner var0, int[] var1, String var2) {
      String var3 = "";

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var3 = var3 + " `" + Token.toString(var1[var4]) + "'";
      }

      return arg2("syntax2", var0, var3, var2);
   }

   static ParseException unclosedComment(String var0) {
      return new ParseException(Util.getMessage("ParseException.unclosed", var0));
   }

   static ParseException undeclaredType(Scanner var0, String var1) {
      return arg1("undeclaredType", var0, var1);
   }

   static ParseException warning(Scanner var0, String var1) {
      scannerInfo(var0);
      String[] var2 = new String[]{filename, Integer.toString(lineNumber), var1, line, pointer};
      return new ParseException(Util.getMessage("ParseException.warning", var2), true);
   }

   static ParseException wrongType(Scanner var0, String var1, String var2, String var3) {
      scannerInfo(var0);
      String[] var4 = new String[]{filename, Integer.toString(lineNumber), var1, var3, var2, line, pointer};
      return new ParseException(Util.getMessage("ParseException.wrongType", var4));
   }

   static ParseException wrongExprType(Scanner var0, String var1, String var2) {
      scannerInfo(var0);
      String[] var3 = new String[]{filename, Integer.toString(lineNumber), var2, var1, line, pointer};
      return new ParseException(Util.getMessage("ParseException.constExprType", var3));
   }

   static ParseException illegalForwardInheritance(Scanner var0, String var1, String var2) {
      scannerInfo(var0);
      String[] var3 = new String[]{filename, Integer.toString(lineNumber), var1, var2, line, pointer};
      return new ParseException(Util.getMessage("ParseException.forwardInheritance", var3));
   }

   static ParseException illegalIncompleteTypeReference(Scanner var0, String var1) {
      scannerInfo(var0);
      String[] var2 = new String[]{filename, Integer.toString(lineNumber), var1, line, pointer};
      return new ParseException(Util.getMessage("ParseException.illegalIncompleteTypeReference", var2));
   }

   private static void scannerInfo(Scanner var0) {
      filename = var0.filename();
      line = var0.lastTokenLine();
      lineNumber = var0.lastTokenLineNumber();
      int var1 = var0.lastTokenLinePosition();
      pointer = "^";
      if (var1 > 1) {
         byte[] var2 = new byte[var1 - 1];

         for(int var3 = 0; var3 < var1 - 1; ++var3) {
            var2[var3] = 32;
         }

         pointer = new String(var2) + pointer;
      }

   }

   private static ParseException arg0(String var0, Scanner var1) {
      scannerInfo(var1);
      String[] var2 = new String[]{filename, Integer.toString(lineNumber), line, pointer};
      return new ParseException(Util.getMessage("ParseException." + var0, var2));
   }

   private static ParseException arg1(String var0, Scanner var1, String var2) {
      scannerInfo(var1);
      String[] var3 = new String[]{filename, Integer.toString(lineNumber), var2, line, pointer};
      return new ParseException(Util.getMessage("ParseException." + var0, var3));
   }

   private static ParseException arg1Warning(String var0, Scanner var1, String var2) {
      scannerInfo(var1);
      String[] var3 = new String[]{filename, Integer.toString(lineNumber), var2, line, pointer};
      return new ParseException(Util.getMessage("ParseException." + var0, var3), true);
   }

   private static ParseException arg2(String var0, Scanner var1, String var2, String var3) {
      scannerInfo(var1);
      String[] var4 = new String[]{filename, Integer.toString(lineNumber), var2, var3, line, pointer};
      return new ParseException(Util.getMessage("ParseException." + var0, var4));
   }

   private static ParseException arg3(String var0, Scanner var1, String var2, String var3, String var4) {
      scannerInfo(var1);
      String[] var5 = new String[]{filename, Integer.toString(lineNumber), var2, var3, var4, line, pointer};
      return new ParseException(Util.getMessage("ParseException." + var0, var5));
   }
}
