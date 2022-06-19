package sun.tools.jstat;

import java.util.Iterator;
import java.util.Set;

public class SyntaxException extends ParserException {
   private String message;

   public SyntaxException(String var1) {
      this.message = var1;
   }

   public SyntaxException(int var1, String var2, String var3) {
      this.message = "Syntax error at line " + var1 + ": Expected " + var2 + ", Found " + var3;
   }

   public SyntaxException(int var1, String var2, Token var3) {
      this.message = "Syntax error at line " + var1 + ": Expected " + var2 + ", Found " + var3.toMessage();
   }

   public SyntaxException(int var1, Token var2, Token var3) {
      this.message = "Syntax error at line " + var1 + ": Expected " + var2.toMessage() + ", Found " + var3.toMessage();
   }

   public SyntaxException(int var1, Set var2, Token var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append("Syntax error at line " + var1 + ": Expected one of '");
      boolean var5 = true;
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         if (var5) {
            var4.append(var7);
            var5 = false;
         } else {
            var4.append("|" + var7);
         }
      }

      var4.append("', Found " + var3.toMessage());
      this.message = var4.toString();
   }

   public String getMessage() {
      return this.message;
   }
}
