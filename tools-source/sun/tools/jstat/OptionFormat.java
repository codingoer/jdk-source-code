package sun.tools.jstat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.jvmstat.monitor.MonitorException;

public class OptionFormat {
   protected String name;
   protected List children;

   public OptionFormat(String var1) {
      this.name = var1;
      this.children = new ArrayList();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof OptionFormat)) {
         return false;
      } else {
         OptionFormat var2 = (OptionFormat)var1;
         return this.name.compareTo(var2.name) == 0;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public void addSubFormat(OptionFormat var1) {
      this.children.add(var1);
   }

   public OptionFormat getSubFormat(int var1) {
      return (OptionFormat)this.children.get(var1);
   }

   public void insertSubFormat(int var1, OptionFormat var2) {
      this.children.add(var1, var2);
   }

   public String getName() {
      return this.name;
   }

   public void apply(Closure var1) throws MonitorException {
      Iterator var2 = this.children.iterator();

      OptionFormat var3;
      while(var2.hasNext()) {
         var3 = (OptionFormat)var2.next();
         var1.visit(var3, var2.hasNext());
      }

      var2 = this.children.iterator();

      while(var2.hasNext()) {
         var3 = (OptionFormat)var2.next();
         var3.apply(var1);
      }

   }

   public void printFormat() {
      this.printFormat(0);
   }

   public void printFormat(int var1) {
      String var2 = "  ";
      StringBuilder var3 = new StringBuilder("");

      for(int var4 = 0; var4 < var1; ++var4) {
         var3.append(var2);
      }

      System.out.println(var3 + this.name + " {");
      Iterator var6 = this.children.iterator();

      while(var6.hasNext()) {
         OptionFormat var5 = (OptionFormat)var6.next();
         var5.printFormat(var1 + 1);
      }

      System.out.println(var3 + "}");
   }
}
