package sun.tools.javac;

/** @deprecated */
@Deprecated
final class ErrorMessage {
   long where;
   String message;
   ErrorMessage next;

   ErrorMessage(long var1, String var3) {
      this.where = var1;
      this.message = var3;
   }
}
