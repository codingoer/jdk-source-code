package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import java.util.StringTokenizer;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public final class BIInterface {
   private final Element dom;
   private final String name;
   private final String[] members;
   private final String[] fields;

   BIInterface(Element e) {
      this.dom = e;
      this.name = DOMUtil.getAttribute(e, "name");
      this.members = parseTokens(DOMUtil.getAttribute(e, "members"));
      if (DOMUtil.getAttribute(e, "properties") != null) {
         this.fields = parseTokens(DOMUtil.getAttribute(e, "properties"));
         throw new AssertionError("//interface/@properties is not supported");
      } else {
         this.fields = new String[0];
      }
   }

   public String name() {
      return this.name;
   }

   public String[] members() {
      return this.members;
   }

   public String[] fields() {
      return this.fields;
   }

   public Locator getSourceLocation() {
      return DOMLocator.getLocationInfo(this.dom);
   }

   private static String[] parseTokens(String value) {
      StringTokenizer tokens = new StringTokenizer(value);
      String[] r = new String[tokens.countTokens()];

      for(int i = 0; tokens.hasMoreTokens(); r[i++] = tokens.nextToken()) {
      }

      return r;
   }
}
