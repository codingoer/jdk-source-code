package sun.tools.jstat;

public class Token {
   public String sval;
   public double nval;
   public int ttype;

   public Token(int var1, String var2, double var3) {
      this.ttype = var1;
      this.sval = var2;
      this.nval = var3;
   }

   public Token(int var1, String var2) {
      this(var1, var2, 0.0);
   }

   public Token(int var1) {
      this(var1, (String)null, 0.0);
   }

   public String toMessage() {
      switch (this.ttype) {
         case -3:
            if (this.sval == null) {
               return "IDENTIFIER";
            }

            return "IDENTIFIER " + this.sval;
         case -2:
            return "NUMBER";
         case -1:
            return "\"EOF\"";
         case 10:
            return "\"EOL\"";
         default:
            if (this.ttype == 34) {
               String var1 = "QUOTED STRING";
               if (this.sval != null) {
                  var1 = var1 + " \"" + this.sval + "\"";
               }

               return var1;
            } else {
               return "CHARACTER '" + (char)this.ttype + "'";
            }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      switch (this.ttype) {
         case -3:
            if (this.sval == null) {
               var1.append("ttype=TT_WORD:IDENTIFIER");
            } else {
               var1.append("ttype=TT_WORD:").append("sval=" + this.sval);
            }
            break;
         case -2:
            var1.append("ttype=TT_NUM,").append("nval=" + this.nval);
            break;
         case -1:
            var1.append("ttype=TT_EOF");
            break;
         case 10:
            var1.append("ttype=TT_EOL");
            break;
         default:
            if (this.ttype == 34) {
               var1.append("ttype=TT_STRING:").append("sval=" + this.sval);
            } else {
               var1.append("ttype=TT_CHAR:").append((char)this.ttype);
            }
      }

      return var1.toString();
   }
}
