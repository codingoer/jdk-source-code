package com.sun.xml.internal.rngom.dt.builtin;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class BuiltinDatatypeLibraryFactory implements DatatypeLibraryFactory {
   private final DatatypeLibrary builtinDatatypeLibrary;
   private final DatatypeLibrary compatibilityDatatypeLibrary;
   private final DatatypeLibraryFactory core;

   public BuiltinDatatypeLibraryFactory(DatatypeLibraryFactory coreFactory) {
      this.builtinDatatypeLibrary = new BuiltinDatatypeLibrary(coreFactory);
      this.compatibilityDatatypeLibrary = new CompatibilityDatatypeLibrary(coreFactory);
      this.core = coreFactory;
   }

   public DatatypeLibrary createDatatypeLibrary(String uri) {
      if (uri.equals("")) {
         return this.builtinDatatypeLibrary;
      } else {
         return uri.equals("http://relaxng.org/ns/compatibility/datatypes/1.0") ? this.compatibilityDatatypeLibrary : this.core.createDatatypeLibrary(uri);
      }
   }
}
