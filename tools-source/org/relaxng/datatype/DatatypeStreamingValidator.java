package org.relaxng.datatype;

public interface DatatypeStreamingValidator {
   void addCharacters(char[] var1, int var2, int var3);

   boolean isValid();

   void checkValid() throws DatatypeException;
}
