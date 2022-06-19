package sun.tools.jstat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class OptionFinder {
   private static final boolean debug = false;
   List optionsSources;

   public OptionFinder(List var1) {
      this.optionsSources = var1;
   }

   public OptionFormat getOptionFormat(String var1, boolean var2) {
      OptionFormat var3 = this.getOptionFormat(var1, this.optionsSources);
      OptionFormat var4 = null;
      if (var3 != null && var2) {
         var4 = this.getOptionFormat("timestamp", this.optionsSources);
         if (var4 != null) {
            ColumnFormat var5 = (ColumnFormat)var4.getSubFormat(0);
            var3.insertSubFormat(0, var5);
         }
      }

      return var3;
   }

   protected OptionFormat getOptionFormat(String var1, List var2) {
      OptionFormat var3 = null;
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         URL var5 = (URL)var4.next();

         try {
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var5.openStream()));
            var3 = (new Parser(var6)).parse(var1);
            if (var3 != null) {
               break;
            }
         } catch (IOException var7) {
         } catch (ParserException var8) {
            System.err.println(var5 + ": " + var8.getMessage());
            System.err.println("Parsing of " + var5 + " aborted");
         }
      }

      return var3;
   }
}
