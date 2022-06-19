package com.sun.tools.doclets.formats.html;

import com.sun.tools.doclets.internal.toolkit.util.links.LinkOutput;

public class LinkOutputImpl implements LinkOutput {
   public StringBuilder output = new StringBuilder();

   public void append(Object var1) {
      this.output.append(var1 instanceof String ? (String)var1 : ((LinkOutputImpl)var1).toString());
   }

   public void insert(int var1, Object var2) {
      this.output.insert(var1, var2.toString());
   }

   public String toString() {
      return this.output.toString();
   }
}
