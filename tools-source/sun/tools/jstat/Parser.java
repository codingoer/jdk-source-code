package sun.tools.jstat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashSet;
import java.util.Set;

public class Parser {
   private static boolean pdebug = Boolean.getBoolean("jstat.parser.debug");
   private static boolean ldebug = Boolean.getBoolean("jstat.lex.debug");
   private static final char OPENBLOCK = '{';
   private static final char CLOSEBLOCK = '}';
   private static final char DOUBLEQUOTE = '"';
   private static final char PERCENT_CHAR = '%';
   private static final char OPENPAREN = '(';
   private static final char CLOSEPAREN = ')';
   private static final char OPERATOR_PLUS = '+';
   private static final char OPERATOR_MINUS = '-';
   private static final char OPERATOR_MULTIPLY = '*';
   private static final char OPERATOR_DIVIDE = '/';
   private static final String OPTION = "option";
   private static final String COLUMN = "column";
   private static final String DATA = "data";
   private static final String HEADER = "header";
   private static final String WIDTH = "width";
   private static final String FORMAT = "format";
   private static final String ALIGN = "align";
   private static final String SCALE = "scale";
   private static final String START = "option";
   private static final Set scaleKeyWords = Scale.keySet();
   private static final Set alignKeyWords = Alignment.keySet();
   private static String[] otherKeyWords = new String[]{"option", "column", "data", "header", "width", "format", "align", "scale"};
   private static char[] infixOps = new char[]{'+', '-', '*', '/'};
   private static char[] delimiters = new char[]{'{', '}', '%', '(', ')'};
   private static Set reservedWords;
   private StreamTokenizer st;
   private String filename;
   private Token lookahead;
   private Token previous;
   private int columnCount;
   private OptionFormat optionFormat;

   public Parser(String var1) throws FileNotFoundException {
      this.filename = var1;
      new BufferedReader(new FileReader(var1));
   }

   public Parser(Reader var1) {
      this.st = new StreamTokenizer(var1);
      this.st.ordinaryChar(47);
      this.st.wordChars(95, 95);
      this.st.slashSlashComments(true);
      this.st.slashStarComments(true);
      reservedWords = new HashSet();

      int var2;
      for(var2 = 0; var2 < otherKeyWords.length; ++var2) {
         reservedWords.add(otherKeyWords[var2]);
      }

      for(var2 = 0; var2 < delimiters.length; ++var2) {
         this.st.ordinaryChar(delimiters[var2]);
      }

      for(var2 = 0; var2 < infixOps.length; ++var2) {
         this.st.ordinaryChar(infixOps[var2]);
      }

   }

   private void pushBack() {
      this.lookahead = this.previous;
      this.st.pushBack();
   }

   private void nextToken() throws ParserException, IOException {
      int var1 = this.st.nextToken();
      this.previous = this.lookahead;
      this.lookahead = new Token(this.st.ttype, this.st.sval, this.st.nval);
      this.log(ldebug, "lookahead = " + this.lookahead);
   }

   private Token matchOne(Set var1) throws ParserException, IOException {
      if (this.lookahead.ttype == -3 && var1.contains(this.lookahead.sval)) {
         Token var2 = this.lookahead;
         this.nextToken();
         return var2;
      } else {
         throw new SyntaxException(this.st.lineno(), var1, this.lookahead);
      }
   }

   private void match(int var1, String var2) throws ParserException, IOException {
      if (this.lookahead.ttype == var1 && this.lookahead.sval.compareTo(var2) == 0) {
         this.nextToken();
      } else {
         throw new SyntaxException(this.st.lineno(), new Token(var1, var2), this.lookahead);
      }
   }

   private void match(int var1) throws ParserException, IOException {
      if (this.lookahead.ttype == var1) {
         this.nextToken();
      } else {
         throw new SyntaxException(this.st.lineno(), new Token(var1), this.lookahead);
      }
   }

   private void match(char var1) throws ParserException, IOException {
      if (this.lookahead.ttype == var1) {
         this.nextToken();
      } else {
         throw new SyntaxException(this.st.lineno(), new Token(var1), this.lookahead);
      }
   }

   private void matchQuotedString() throws ParserException, IOException {
      this.match('"');
   }

   private void matchNumber() throws ParserException, IOException {
      this.match((int)-2);
   }

   private void matchID() throws ParserException, IOException {
      this.match((int)-3);
   }

   private void match(String var1) throws ParserException, IOException {
      this.match(-3, var1);
   }

   private boolean isReservedWord(String var1) {
      return reservedWords.contains(var1);
   }

   private boolean isInfixOperator(char var1) {
      for(int var2 = 0; var2 < infixOps.length; ++var2) {
         if (var1 == infixOps[var2]) {
            return true;
         }
      }

      return false;
   }

   private void scaleStmt(ColumnFormat var1) throws ParserException, IOException {
      this.match("scale");
      Token var2 = this.matchOne(scaleKeyWords);
      var1.setScale(Scale.toScale(var2.sval));
      String var3 = var2.sval;
      this.log(pdebug, "Parsed: scale -> " + var3);
   }

   private void alignStmt(ColumnFormat var1) throws ParserException, IOException {
      this.match("align");
      Token var2 = this.matchOne(alignKeyWords);
      var1.setAlignment(Alignment.toAlignment(var2.sval));
      String var3 = var2.sval;
      this.log(pdebug, "Parsed: align -> " + var3);
   }

   private void headerStmt(ColumnFormat var1) throws ParserException, IOException {
      this.match("header");
      String var2 = this.lookahead.sval;
      this.matchQuotedString();
      var1.setHeader(var2);
      this.log(pdebug, "Parsed: header -> " + var2);
   }

   private void widthStmt(ColumnFormat var1) throws ParserException, IOException {
      this.match("width");
      double var2 = this.lookahead.nval;
      this.matchNumber();
      var1.setWidth((int)var2);
      this.log(pdebug, "Parsed: width -> " + var2);
   }

   private void formatStmt(ColumnFormat var1) throws ParserException, IOException {
      this.match("format");
      String var2 = this.lookahead.sval;
      this.matchQuotedString();
      var1.setFormat(var2);
      this.log(pdebug, "Parsed: format -> " + var2);
   }

   private Expression primary() throws ParserException, IOException {
      Object var1 = null;
      switch (this.lookahead.ttype) {
         case -3:
            String var2 = this.lookahead.sval;
            if (this.isReservedWord(var2)) {
               throw new SyntaxException(this.st.lineno(), "IDENTIFIER", "Reserved Word: " + this.lookahead.sval);
            }

            this.matchID();
            var1 = new Identifier(var2);
            this.log(pdebug, "Parsed: ID -> " + var2);
            break;
         case -2:
            double var3 = this.lookahead.nval;
            this.matchNumber();
            var1 = new Literal(new Double(var3));
            this.log(pdebug, "Parsed: number -> " + var3);
            break;
         case 40:
            this.match('(');
            var1 = this.expression();
            this.match(')');
            break;
         default:
            throw new SyntaxException(this.st.lineno(), "IDENTIFIER", this.lookahead);
      }

      this.log(pdebug, "Parsed: primary -> " + var1);
      return (Expression)var1;
   }

   private Expression unary() throws ParserException, IOException {
      Expression var1 = null;
      Operator var2 = null;

      while(true) {
         switch (this.lookahead.ttype) {
            case 43:
               this.match('+');
               var2 = Operator.PLUS;
               break;
            case 45:
               this.match('-');
               var2 = Operator.MINUS;
               break;
            default:
               var1 = this.primary();
               this.log(pdebug, "Parsed: unary -> " + var1);
               return var1;
         }

         Expression var3 = new Expression();
         var3.setOperator(var2);
         var3.setRight(var1);
         this.log(pdebug, "Parsed: unary -> " + var3);
         var3.setLeft(new Literal(new Double(0.0)));
         var1 = var3;
      }
   }

   private Expression multExpression() throws ParserException, IOException {
      Expression var1 = this.unary();
      Operator var2 = null;

      while(true) {
         switch (this.lookahead.ttype) {
            case 42:
               this.match('*');
               var2 = Operator.MULTIPLY;
               break;
            case 47:
               this.match('/');
               var2 = Operator.DIVIDE;
               break;
            default:
               this.log(pdebug, "Parsed: multExpression -> " + var1);
               return var1;
         }

         Expression var3 = new Expression();
         var3.setOperator(var2);
         var3.setLeft(var1);
         var3.setRight(this.unary());
         var1 = var3;
         this.log(pdebug, "Parsed: multExpression -> " + var3);
      }
   }

   private Expression addExpression() throws ParserException, IOException {
      Expression var1 = this.multExpression();
      Operator var2 = null;

      while(true) {
         switch (this.lookahead.ttype) {
            case 43:
               this.match('+');
               var2 = Operator.PLUS;
               break;
            case 45:
               this.match('-');
               var2 = Operator.MINUS;
               break;
            default:
               this.log(pdebug, "Parsed: addExpression -> " + var1);
               return var1;
         }

         Expression var3 = new Expression();
         var3.setOperator(var2);
         var3.setLeft(var1);
         var3.setRight(this.multExpression());
         var1 = var3;
         this.log(pdebug, "Parsed: addExpression -> " + var3);
      }
   }

   private Expression expression() throws ParserException, IOException {
      Expression var1 = this.addExpression();
      this.log(pdebug, "Parsed: expression -> " + var1);
      return var1;
   }

   private void dataStmt(ColumnFormat var1) throws ParserException, IOException {
      this.match("data");
      Expression var2 = this.expression();
      var1.setExpression(var2);
      this.log(pdebug, "Parsed: data -> " + var2);
   }

   private void statementList(ColumnFormat var1) throws ParserException, IOException {
      while(this.lookahead.ttype == -3) {
         if (this.lookahead.sval.compareTo("data") == 0) {
            this.dataStmt(var1);
         } else if (this.lookahead.sval.compareTo("header") == 0) {
            this.headerStmt(var1);
         } else if (this.lookahead.sval.compareTo("width") == 0) {
            this.widthStmt(var1);
         } else if (this.lookahead.sval.compareTo("format") == 0) {
            this.formatStmt(var1);
         } else if (this.lookahead.sval.compareTo("align") == 0) {
            this.alignStmt(var1);
         } else {
            if (this.lookahead.sval.compareTo("scale") != 0) {
               return;
            }

            this.scaleStmt(var1);
         }
      }

   }

   private void optionList(OptionFormat var1) throws ParserException, IOException {
      while(this.lookahead.ttype == -3) {
         this.match("column");
         this.match('{');
         ColumnFormat var2 = new ColumnFormat(this.columnCount++);
         this.statementList(var2);
         this.match('}');
         var2.validate();
         var1.addSubFormat(var2);
      }

   }

   private OptionFormat optionStmt() throws ParserException, IOException {
      this.match("option");
      String var1 = this.lookahead.sval;
      this.matchID();
      this.match('{');
      OptionFormat var2 = new OptionFormat(var1);
      this.optionList(var2);
      this.match('}');
      return var2;
   }

   public OptionFormat parse(String var1) throws ParserException, IOException {
      this.nextToken();

      while(true) {
         while(this.lookahead.ttype != -1) {
            if (this.lookahead.ttype == -3 && this.lookahead.sval.compareTo("option") == 0) {
               this.match("option");
               if (this.lookahead.ttype == -3 && this.lookahead.sval.compareTo(var1) == 0) {
                  this.pushBack();
                  return this.optionStmt();
               }

               this.nextToken();
            } else {
               this.nextToken();
            }
         }

         return null;
      }
   }

   public Set parseOptions() throws ParserException, IOException {
      HashSet var1 = new HashSet();
      this.nextToken();

      while(true) {
         while(this.lookahead.ttype != -1) {
            if (this.lookahead.ttype == -3 && this.lookahead.sval.compareTo("option") == 0) {
               OptionFormat var2 = this.optionStmt();
               var1.add(var2);
            } else {
               this.nextToken();
            }
         }

         return var1;
      }
   }

   OptionFormat getOptionFormat() {
      return this.optionFormat;
   }

   private void log(boolean var1, String var2) {
      if (var1) {
         System.out.println(var2);
      }

   }
}
