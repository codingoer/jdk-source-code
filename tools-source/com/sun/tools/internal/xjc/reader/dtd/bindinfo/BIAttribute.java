package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.tools.internal.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRendererFactory;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class BIAttribute {
   private final BIElement parent;
   private final Element element;

   BIAttribute(BIElement _parent, Element _e) {
      this.parent = _parent;
      this.element = _e;
   }

   public final String name() {
      return this.element.getAttribute("name");
   }

   public BIConversion getConversion() {
      if (this.element.getAttributeNode("convert") == null) {
         return null;
      } else {
         String cnv = this.element.getAttribute("convert");
         return this.parent.conversion(cnv);
      }
   }

   public final FieldRenderer getRealization() {
      Attr a = this.element.getAttributeNode("collection");
      if (a == null) {
         return null;
      } else {
         String v = this.element.getAttribute("collection").trim();
         FieldRendererFactory frf = this.parent.parent.model.options.getFieldRendererFactory();
         if (v.equals("array")) {
            return frf.getArray();
         } else if (v.equals("list")) {
            return frf.getList(this.parent.parent.codeModel.ref(ArrayList.class));
         } else {
            throw new InternalError("unexpected collection value: " + v);
         }
      }
   }

   public final String getPropertyName() {
      String r = DOMUtil.getAttribute(this.element, "property");
      return r != null ? r : this.name();
   }
}
