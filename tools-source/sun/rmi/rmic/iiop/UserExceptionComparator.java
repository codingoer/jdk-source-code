package sun.rmi.rmic.iiop;

import java.util.Comparator;

class UserExceptionComparator implements Comparator {
   public int compare(Object var1, Object var2) {
      ValueType var3 = (ValueType)var1;
      ValueType var4 = (ValueType)var2;
      byte var5 = 0;
      if (this.isUserException(var3)) {
         if (!this.isUserException(var4)) {
            var5 = -1;
         }
      } else if (this.isUserException(var4) && !this.isUserException(var3)) {
         var5 = 1;
      }

      return var5;
   }

   final boolean isUserException(ValueType var1) {
      return var1.isIDLEntityException() && !var1.isCORBAUserException();
   }
}
