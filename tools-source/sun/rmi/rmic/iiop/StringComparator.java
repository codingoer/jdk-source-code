package sun.rmi.rmic.iiop;

import java.util.Comparator;

class StringComparator implements Comparator {
   public int compare(Object var1, Object var2) {
      String var3 = (String)var1;
      String var4 = (String)var2;
      return var3.compareTo(var4);
   }
}
