package com.sun.tools.corba.se.idl.som.idlemit;

import com.sun.tools.corba.se.idl.ForwardEntry;
import com.sun.tools.corba.se.idl.PragmaHandler;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.som.cff.Messages;
import java.util.Vector;

public class MetaPragma extends PragmaHandler {
   public static int metaKey = SymtabEntry.getVariableKey();
   private static int initialState = 0;
   private static int commentState = 1;
   private static int textState = 2;
   private static int finalState = 3;

   public boolean process(String var1, String var2) {
      if (!var1.equals("meta")) {
         return false;
      } else {
         try {
            SymtabEntry var3 = this.scopedName();
            if (var3 == null) {
               this.parseException(Messages.msg("idlemit.MetaPragma.scopedNameNotFound"));
               this.skipToEOL();
            } else {
               String var4 = this.currentToken() + this.getStringToEOL();
               Vector var5 = (Vector)var3.dynamicVariable(metaKey);
               if (var5 == null) {
                  var5 = new Vector();
                  var3.dynamicVariable(metaKey, var5);
               }

               this.parseMsg(var5, var4);
            }
         } catch (Exception var6) {
         }

         return true;
      }
   }

   public static void processForward(ForwardEntry var0) {
      Vector var1;
      try {
         var1 = (Vector)var0.dynamicVariable(metaKey);
      } catch (Exception var9) {
         var1 = null;
      }

      SymtabEntry var2 = var0.type();
      if (var1 != null && var2 != null) {
         Vector var3;
         try {
            var3 = (Vector)var2.dynamicVariable(metaKey);
         } catch (Exception var8) {
            var3 = null;
         }

         if (var3 == null) {
            try {
               var2.dynamicVariable(metaKey, var1);
            } catch (Exception var7) {
            }
         } else if (var3 != var1) {
            for(int var4 = 0; var4 < var1.size(); ++var4) {
               try {
                  Object var5 = var1.elementAt(var4);
                  var3.addElement(var5);
               } catch (Exception var6) {
               }
            }
         }
      }

   }

   private void parseMsg(Vector var1, String var2) {
      int var3 = initialState;
      String var4 = "";

      for(int var5 = 0; var3 != finalState; ++var5) {
         boolean var6 = var5 >= var2.length();
         char var7 = ' ';
         boolean var8 = false;
         boolean var9 = false;
         boolean var10 = false;
         boolean var11 = false;
         boolean var12 = false;
         if (!var6) {
            var7 = var2.charAt(var5);
            if (var7 == '/' && var5 + 1 < var2.length()) {
               if (var2.charAt(var5 + 1) == '/') {
                  var9 = true;
                  ++var5;
               } else if (var2.charAt(var5 + 1) == '*') {
                  var8 = true;
                  ++var5;
               } else {
                  var12 = true;
               }
            } else if (var7 == '*' && var5 + 1 < var2.length()) {
               if (var2.charAt(var5 + 1) == '/') {
                  var11 = true;
                  ++var5;
               } else {
                  var12 = true;
               }
            } else if (!Character.isSpace(var7) && var7 != ',' && var7 != ';') {
               var12 = true;
            } else {
               var10 = true;
            }
         }

         if (var3 == initialState) {
            if (var8) {
               var3 = commentState;
            } else if (!var9 && !var6) {
               if (var12) {
                  var3 = textState;
                  var4 = var4 + var7;
               }
            } else {
               var3 = finalState;
            }
         } else if (var3 == commentState) {
            if (var6) {
               var3 = finalState;
            } else if (var11) {
               var3 = initialState;
            }
         } else if (var3 == textState) {
            if (!var6 && !var11 && !var9 && !var8 && !var10) {
               if (var12) {
                  var4 = var4 + var7;
               }
            } else {
               if (!var4.equals("")) {
                  var1.addElement(var4);
                  var4 = "";
               }

               if (var6) {
                  var3 = finalState;
               } else if (var8) {
                  var3 = commentState;
               } else {
                  var3 = initialState;
               }
            }
         }
      }

   }
}
