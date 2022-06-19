package sun.jvmstat.perfdata.monitor;

public class SyntaxException extends Exception {
   int lineno;

   public SyntaxException(int var1) {
      this.lineno = var1;
   }

   public String getMessage() {
      return "syntax error at line " + this.lineno;
   }
}
