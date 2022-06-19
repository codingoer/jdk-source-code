package com.sun.xml.internal.rngom.dt.builtin;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

class CompatibilityDatatypeLibrary implements DatatypeLibrary {
   private final DatatypeLibraryFactory factory;
   private DatatypeLibrary xsdDatatypeLibrary = null;

   CompatibilityDatatypeLibrary(DatatypeLibraryFactory factory) {
      this.factory = factory;
   }

   public DatatypeBuilder createDatatypeBuilder(String type) throws DatatypeException {
      if (!type.equals("ID") && !type.equals("IDREF") && !type.equals("IDREFS")) {
         throw new DatatypeException();
      } else {
         if (this.xsdDatatypeLibrary == null) {
            this.xsdDatatypeLibrary = this.factory.createDatatypeLibrary("http://www.w3.org/2001/XMLSchema-datatypes");
            if (this.xsdDatatypeLibrary == null) {
               throw new DatatypeException();
            }
         }

         return this.xsdDatatypeLibrary.createDatatypeBuilder(type);
      }
   }

   public Datatype createDatatype(String type) throws DatatypeException {
      return this.createDatatypeBuilder(type).createDatatype();
   }
}
