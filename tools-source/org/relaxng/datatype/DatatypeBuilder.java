package org.relaxng.datatype;

public interface DatatypeBuilder {
   void addParameter(String var1, String var2, ValidationContext var3) throws DatatypeException;

   Datatype createDatatype() throws DatatypeException;
}
