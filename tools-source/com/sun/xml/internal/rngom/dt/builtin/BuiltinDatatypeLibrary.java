package com.sun.xml.internal.rngom.dt.builtin;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class BuiltinDatatypeLibrary implements DatatypeLibrary {
   private final DatatypeLibraryFactory factory;
   private DatatypeLibrary xsdDatatypeLibrary = null;

   BuiltinDatatypeLibrary(DatatypeLibraryFactory factory) {
      this.factory = factory;
   }

   public DatatypeBuilder createDatatypeBuilder(String type) throws DatatypeException {
      this.xsdDatatypeLibrary = this.factory.createDatatypeLibrary("http://www.w3.org/2001/XMLSchema-datatypes");
      if (this.xsdDatatypeLibrary == null) {
         throw new DatatypeException();
      } else if (!type.equals("string") && !type.equals("token")) {
         throw new DatatypeException();
      } else {
         return new BuiltinDatatypeBuilder(this.xsdDatatypeLibrary.createDatatype(type));
      }
   }

   public Datatype createDatatype(String type) throws DatatypeException {
      return this.createDatatypeBuilder(type).createDatatype();
   }
}
