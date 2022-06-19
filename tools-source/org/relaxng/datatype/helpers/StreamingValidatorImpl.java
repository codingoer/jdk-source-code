package org.relaxng.datatype.helpers;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.ValidationContext;

public final class StreamingValidatorImpl implements DatatypeStreamingValidator {
   private final StringBuffer buffer = new StringBuffer();
   private final Datatype baseType;
   private final ValidationContext context;

   public void addCharacters(char[] buf, int start, int len) {
      this.buffer.append(buf, start, len);
   }

   public boolean isValid() {
      return this.baseType.isValid(this.buffer.toString(), this.context);
   }

   public void checkValid() throws DatatypeException {
      this.baseType.checkValid(this.buffer.toString(), this.context);
   }

   public StreamingValidatorImpl(Datatype baseType, ValidationContext context) {
      this.baseType = baseType;
      this.context = context;
   }
}
