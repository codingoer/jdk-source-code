package sun.tools.asm;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

class SwitchDataEnumeration implements Enumeration {
   private Integer[] table;
   private int current_index = 0;

   SwitchDataEnumeration(Hashtable var1) {
      this.table = new Integer[var1.size()];
      int var2 = 0;

      for(Enumeration var3 = var1.keys(); var3.hasMoreElements(); this.table[var2++] = (Integer)var3.nextElement()) {
      }

      Arrays.sort(this.table);
      this.current_index = 0;
   }

   public boolean hasMoreElements() {
      return this.current_index < this.table.length;
   }

   public Integer nextElement() {
      return this.table[this.current_index++];
   }
}
