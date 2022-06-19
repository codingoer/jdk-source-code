package org.relaxng.datatype.helpers;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

public final class ParameterlessDatatypeBuilder implements DatatypeBuilder {
   private final Datatype baseType;

   public ParameterlessDatatypeBuilder(Datatype baseType) {
      this.baseType = baseType;
   }

   public void addParameter(String name, String strValue, ValidationContext context) throws DatatypeException {
      throw new DatatypeException();
   }

   public Datatype createDatatype() throws DatatypeException {
      return this.baseType;
   }
}
