package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.generator.bean.field.FieldRenderer;
import java.util.ArrayList;
import org.w3c.dom.Element;

public class BIContent {
   protected final Element element;
   protected final BIElement parent;
   private final Options opts;

   private BIContent(Element e, BIElement _parent) {
      this.element = e;
      this.parent = _parent;
      this.opts = this.parent.parent.model.options;
   }

   public final FieldRenderer getRealization() {
      String v = DOMUtil.getAttribute(this.element, "collection");
      if (v == null) {
         return null;
      } else {
         v = v.trim();
         if (v.equals("array")) {
            return this.opts.getFieldRendererFactory().getArray();
         } else if (v.equals("list")) {
            return this.opts.getFieldRendererFactory().getList(this.parent.parent.codeModel.ref(ArrayList.class));
         } else {
            throw new InternalError("unexpected collection value: " + v);
         }
      }
   }

   public final String getPropertyName() {
      String r = DOMUtil.getAttribute(this.element, "property");
      return r != null ? r : DOMUtil.getAttribute(this.element, "name");
   }

   public final JClass getType() {
      try {
         String type = DOMUtil.getAttribute(this.element, "supertype");
         if (type == null) {
            return null;
         } else {
            int idx = type.lastIndexOf(46);
            return idx < 0 ? this.parent.parent.codeModel.ref(type) : this.parent.parent.getTargetPackage().ref(type);
         }
      } catch (ClassNotFoundException var3) {
         throw new NoClassDefFoundError(var3.getMessage());
      }
   }

   static BIContent create(Element e, BIElement _parent) {
      return new BIContent(e, _parent);
   }
}
