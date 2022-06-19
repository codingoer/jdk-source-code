package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ContentBuilder extends Content {
   protected List contents = Collections.emptyList();

   public void addContent(Content var1) {
      nullCheck(var1);
      this.ensureMutableContents();
      if (var1 instanceof ContentBuilder) {
         this.contents.addAll(((ContentBuilder)var1).contents);
      } else {
         this.contents.add(var1);
      }

   }

   public void addContent(String var1) {
      if (!var1.isEmpty()) {
         this.ensureMutableContents();
         Content var2 = this.contents.isEmpty() ? null : (Content)this.contents.get(this.contents.size() - 1);
         StringContent var3;
         if (var2 != null && var2 instanceof StringContent) {
            var3 = (StringContent)var2;
         } else {
            this.contents.add(var3 = new StringContent());
         }

         var3.addContent(var1);
      }
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      Content var4;
      for(Iterator var3 = this.contents.iterator(); var3.hasNext(); var2 = var4.write(var1, var2)) {
         var4 = (Content)var3.next();
      }

      return var2;
   }

   public boolean isEmpty() {
      Iterator var1 = this.contents.iterator();

      Content var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (Content)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public int charCount() {
      int var1 = 0;

      Content var3;
      for(Iterator var2 = this.contents.iterator(); var2.hasNext(); var1 += var3.charCount()) {
         var3 = (Content)var2.next();
      }

      return var1;
   }

   private void ensureMutableContents() {
      if (this.contents.isEmpty()) {
         this.contents = new ArrayList();
      }

   }
}
