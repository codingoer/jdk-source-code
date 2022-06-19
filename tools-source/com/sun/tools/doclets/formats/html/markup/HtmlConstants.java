package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;

public class HtmlConstants {
   public static final Content START_OF_TOP_NAVBAR = new Comment("========= START OF TOP NAVBAR =======");
   public static final Content START_OF_BOTTOM_NAVBAR = new Comment("======= START OF BOTTOM NAVBAR ======");
   public static final Content END_OF_TOP_NAVBAR = new Comment("========= END OF TOP NAVBAR =========");
   public static final Content END_OF_BOTTOM_NAVBAR = new Comment("======== END OF BOTTOM NAVBAR =======");
   public static final Content START_OF_CLASS_DATA = new Comment("======== START OF CLASS DATA ========");
   public static final Content END_OF_CLASS_DATA = new Comment("========= END OF CLASS DATA =========");
   public static final Content START_OF_NESTED_CLASS_SUMMARY = new Comment("======== NESTED CLASS SUMMARY ========");
   public static final Content START_OF_ANNOTATION_TYPE_OPTIONAL_MEMBER_SUMMARY = new Comment("=========== ANNOTATION TYPE OPTIONAL MEMBER SUMMARY ===========");
   public static final Content START_OF_ANNOTATION_TYPE_REQUIRED_MEMBER_SUMMARY = new Comment("=========== ANNOTATION TYPE REQUIRED MEMBER SUMMARY ===========");
   public static final Content START_OF_ANNOTATION_TYPE_FIELD_SUMMARY = new Comment("=========== ANNOTATION TYPE FIELD SUMMARY ===========");
   public static final Content START_OF_CONSTRUCTOR_SUMMARY = new Comment("======== CONSTRUCTOR SUMMARY ========");
   public static final Content START_OF_ENUM_CONSTANT_SUMMARY = new Comment("=========== ENUM CONSTANT SUMMARY ===========");
   public static final Content START_OF_FIELD_SUMMARY = new Comment("=========== FIELD SUMMARY ===========");
   public static final Content START_OF_PROPERTY_SUMMARY = new Comment("=========== PROPERTY SUMMARY ===========");
   public static final Content START_OF_METHOD_SUMMARY = new Comment("========== METHOD SUMMARY ===========");
   public static final Content START_OF_ANNOTATION_TYPE_DETAILS = new Comment("============ ANNOTATION TYPE MEMBER DETAIL ===========");
   public static final Content START_OF_ANNOTATION_TYPE_FIELD_DETAILS = new Comment("============ ANNOTATION TYPE FIELD DETAIL ===========");
   public static final Content START_OF_METHOD_DETAILS = new Comment("============ METHOD DETAIL ==========");
   public static final Content START_OF_FIELD_DETAILS = new Comment("============ FIELD DETAIL ===========");
   public static final Content START_OF_PROPERTY_DETAILS = new Comment("============ PROPERTY DETAIL ===========");
   public static final Content START_OF_CONSTRUCTOR_DETAILS = new Comment("========= CONSTRUCTOR DETAIL ========");
   public static final Content START_OF_ENUM_CONSTANT_DETAILS = new Comment("============ ENUM CONSTANT DETAIL ===========");
   public static final HtmlTag TITLE_HEADING;
   public static final HtmlTag CLASS_PAGE_HEADING;
   public static final HtmlTag CONTENT_HEADING;
   public static final HtmlTag PACKAGE_HEADING;
   public static final HtmlTag PROFILE_HEADING;
   public static final HtmlTag SUMMARY_HEADING;
   public static final HtmlTag INHERITED_SUMMARY_HEADING;
   public static final HtmlTag DETAILS_HEADING;
   public static final HtmlTag SERIALIZED_MEMBER_HEADING;
   public static final HtmlTag MEMBER_HEADING;

   static {
      TITLE_HEADING = HtmlTag.H1;
      CLASS_PAGE_HEADING = HtmlTag.H2;
      CONTENT_HEADING = HtmlTag.H2;
      PACKAGE_HEADING = HtmlTag.H2;
      PROFILE_HEADING = HtmlTag.H2;
      SUMMARY_HEADING = HtmlTag.H3;
      INHERITED_SUMMARY_HEADING = HtmlTag.H3;
      DETAILS_HEADING = HtmlTag.H3;
      SERIALIZED_MEMBER_HEADING = HtmlTag.H3;
      MEMBER_HEADING = HtmlTag.H4;
   }
}
