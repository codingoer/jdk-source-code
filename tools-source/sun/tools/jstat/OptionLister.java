package sun.tools.jstat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OptionLister {
   private static final boolean debug = false;
   private List sources;

   public OptionLister(List var1) {
      this.sources = var1;
   }

   public void print(PrintStream var1) {
      Comparator var2 = new Comparator() {
         public int compare(OptionFormat var1, OptionFormat var2) {
            return var1.getName().compareTo(var2.getName());
         }
      };
      TreeSet var3 = new TreeSet(var2);
      Iterator var4 = this.sources.iterator();

      while(var4.hasNext()) {
         URL var5 = (URL)var4.next();

         try {
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var5.openStream()));
            Set var7 = (new Parser(var6)).parseOptions();
            var3.addAll(var7);
         } catch (IOException var8) {
         } catch (ParserException var9) {
            System.err.println(var5 + ": " + var9.getMessage());
            System.err.println("Parsing of " + var5 + " aborted");
         }
      }

      var4 = var3.iterator();

      while(var4.hasNext()) {
         OptionFormat var10 = (OptionFormat)var4.next();
         if (var10.getName().compareTo("timestamp") != 0) {
            var1.println("-" + var10.getName());
         }
      }

   }
}
