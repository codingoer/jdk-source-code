package org.relaxng.datatype;

public class DatatypeException extends Exception {
   private final int index;
   public static final int UNKNOWN = -1;

   public DatatypeException(int index, String msg) {
      super(msg);
      this.index = index;
   }

   public DatatypeException(String msg) {
      this(-1, msg);
   }

   public DatatypeException() {
      this(-1, (String)null);
   }

   public int getIndex() {
      return this.index;
   }
}
