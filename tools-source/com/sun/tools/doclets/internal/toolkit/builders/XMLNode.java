package com.sun.tools.doclets.internal.toolkit.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMLNode {
   final XMLNode parent;
   final String name;
   final Map attrs;
   final List children;

   XMLNode(XMLNode var1, String var2) {
      this.parent = var1;
      this.name = var2;
      this.attrs = new HashMap();
      this.children = new ArrayList();
      if (var1 != null) {
         var1.children.add(this);
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("<");
      var1.append(this.name);
      Iterator var2 = this.attrs.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.append(" " + (String)var3.getKey() + "=\"" + (String)var3.getValue() + "\"");
      }

      if (this.children.size() == 0) {
         var1.append("/>");
      } else {
         var1.append(">");
         var2 = this.children.iterator();

         while(var2.hasNext()) {
            XMLNode var4 = (XMLNode)var2.next();
            var1.append(var4.toString());
         }

         var1.append("</" + this.name + ">");
      }

      return var1.toString();
   }
}
