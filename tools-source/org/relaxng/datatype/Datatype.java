package org.relaxng.datatype;

public interface Datatype {
   int ID_TYPE_NULL = 0;
   int ID_TYPE_ID = 1;
   int ID_TYPE_IDREF = 2;
   int ID_TYPE_IDREFS = 3;

   boolean isValid(String var1, ValidationContext var2);

   void checkValid(String var1, ValidationContext var2) throws DatatypeException;

   DatatypeStreamingValidator createStreamingValidator(ValidationContext var1);

   Object createValue(String var1, ValidationContext var2);

   boolean sameValue(Object var1, Object var2);

   int valueHashCode(Object var1);

   int getIdType();

   boolean isContextDependent();
}
