package com.sun.tools.internal.xjc.generator.bean.field;

import com.sun.codemodel.internal.JClass;

public class FieldRendererFactory {
   private final FieldRenderer DEFAULT = new DefaultFieldRenderer(this);
   private static final FieldRenderer ARRAY = new GenericFieldRenderer(ArrayField.class);
   private static final FieldRenderer REQUIRED_UNBOXED = new GenericFieldRenderer(UnboxedField.class);
   private static final FieldRenderer SINGLE = new GenericFieldRenderer(SingleField.class);
   private static final FieldRenderer SINGLE_PRIMITIVE_ACCESS = new GenericFieldRenderer(SinglePrimitiveAccessField.class);

   public FieldRenderer getDefault() {
      return this.DEFAULT;
   }

   public FieldRenderer getArray() {
      return ARRAY;
   }

   public FieldRenderer getRequiredUnboxed() {
      return REQUIRED_UNBOXED;
   }

   public FieldRenderer getSingle() {
      return SINGLE;
   }

   public FieldRenderer getSinglePrimitiveAccess() {
      return SINGLE_PRIMITIVE_ACCESS;
   }

   public FieldRenderer getList(JClass coreList) {
      return new UntypedListFieldRenderer(coreList);
   }

   public FieldRenderer getContentList(JClass coreList) {
      return new UntypedListFieldRenderer(coreList, false, true);
   }

   public FieldRenderer getDummyList(JClass coreList) {
      return new UntypedListFieldRenderer(coreList, true, false);
   }

   public FieldRenderer getConst(FieldRenderer fallback) {
      return new ConstFieldRenderer(fallback);
   }
}
