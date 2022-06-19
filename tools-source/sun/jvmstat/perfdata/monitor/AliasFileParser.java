package sun.jvmstat.perfdata.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class AliasFileParser {
   private static final String ALIAS = "alias";
   private static final boolean DEBUG = false;
   private URL inputfile;
   private StreamTokenizer st;
   private Token currentToken;

   AliasFileParser(URL var1) {
      this.inputfile = var1;
   }

   private void logln(String var1) {
   }

   private void nextToken() throws IOException {
      this.st.nextToken();
      this.currentToken = new Token(this.st.ttype, this.st.sval);
      this.logln("Read token: type = " + this.currentToken.ttype + " string = " + this.currentToken.sval);
   }

   private void match(int var1, String var2) throws IOException, SyntaxException {
      if (this.currentToken.ttype == var1 && this.currentToken.sval.compareTo(var2) == 0) {
         this.logln("matched type: " + var1 + " and token = " + this.currentToken.sval);
         this.nextToken();
      } else {
         throw new SyntaxException(this.st.lineno());
      }
   }

   private void match(int var1) throws IOException, SyntaxException {
      if (this.currentToken.ttype == var1) {
         this.logln("matched type: " + var1 + ", token = " + this.currentToken.sval);
         this.nextToken();
      } else {
         throw new SyntaxException(this.st.lineno());
      }
   }

   private void match(String var1) throws IOException, SyntaxException {
      this.match(-3, var1);
   }

   public void parse(Map var1) throws SyntaxException, IOException {
      if (this.inputfile != null) {
         BufferedReader var2 = new BufferedReader(new InputStreamReader(this.inputfile.openStream()));
         this.st = new StreamTokenizer(var2);
         this.st.slashSlashComments(true);
         this.st.slashStarComments(true);
         this.st.wordChars(95, 95);
         this.nextToken();

         while(true) {
            while(this.currentToken.ttype != -1) {
               if (this.currentToken.ttype == -3 && this.currentToken.sval.compareTo("alias") == 0) {
                  this.match("alias");
                  String var3 = this.currentToken.sval;
                  this.match(-3);
                  ArrayList var4 = new ArrayList();

                  do {
                     var4.add(this.currentToken.sval);
                     this.match(-3);
                  } while(this.currentToken.ttype != -1 && this.currentToken.sval.compareTo("alias") != 0);

                  this.logln("adding map entry for " + var3 + " values = " + var4);
                  var1.put(var3, var4);
               } else {
                  this.nextToken();
               }
            }

            return;
         }
      }
   }

   private class Token {
      public String sval;
      public int ttype;

      public Token(int var2, String var3) {
         this.ttype = var2;
         this.sval = var3;
      }
   }
}
