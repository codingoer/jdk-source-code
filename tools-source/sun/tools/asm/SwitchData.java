package sun.tools.asm;

import java.util.Enumeration;
import java.util.Hashtable;

public final class SwitchData {
   int minValue;
   int maxValue;
   Label defaultLabel = new Label();
   Hashtable tab = new Hashtable();
   Hashtable whereCaseTab = null;

   public Label get(int var1) {
      return (Label)this.tab.get(var1);
   }

   public Label get(Integer var1) {
      return (Label)this.tab.get(var1);
   }

   public void add(int var1, Label var2) {
      if (this.tab.size() == 0) {
         this.minValue = var1;
         this.maxValue = var1;
      } else {
         if (var1 < this.minValue) {
            this.minValue = var1;
         }

         if (var1 > this.maxValue) {
            this.maxValue = var1;
         }
      }

      this.tab.put(var1, var2);
   }

   public Label getDefaultLabel() {
      return this.defaultLabel;
   }

   public synchronized Enumeration sortedKeys() {
      return new SwitchDataEnumeration(this.tab);
   }

   public void initTableCase() {
      this.whereCaseTab = new Hashtable();
   }

   public void addTableCase(int var1, long var2) {
      if (this.whereCaseTab != null) {
         this.whereCaseTab.put(var1, var2);
      }

   }

   public void addTableDefault(long var1) {
      if (this.whereCaseTab != null) {
         this.whereCaseTab.put("default", var1);
      }

   }

   public long whereCase(Object var1) {
      Long var2 = (Long)this.whereCaseTab.get(var1);
      return var2 == null ? 0L : var2;
   }

   public boolean getDefault() {
      return this.whereCase("default") != 0L;
   }
}
