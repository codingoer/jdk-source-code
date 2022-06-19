package org.relaxng.datatype;

public interface DatatypeLibrary {
   DatatypeBuilder createDatatypeBuilder(String var1) throws DatatypeException;

   Datatype createDatatype(String var1) throws DatatypeException;
}
