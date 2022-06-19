package com.sun.tools.doclint;

import com.sun.tools.javac.util.StringUtils;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Name;

public enum HtmlTag {
   A(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.HREF, HtmlTag.Attr.TARGET, HtmlTag.Attr.NAME)}),
   B(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   BIG(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   BLOCKQUOTE(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE), new AttrMap[0]),
   BODY(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   BR(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.NONE, new AttrMap[]{attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.CLEAR)}),
   CAPTION(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.ACCEPTS_INLINE, HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   CENTER(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE), new AttrMap[0]),
   CITE(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   CODE(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   DD(HtmlTag.BlockType.LIST_ITEM, HtmlTag.EndKind.OPTIONAL, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE, HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   DFN(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   DIV(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE), new AttrMap[0]),
   DL(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.COMPACT)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == DT || var1 == DD;
      }
   },
   DT(HtmlTag.BlockType.LIST_ITEM, HtmlTag.EndKind.OPTIONAL, EnumSet.of(HtmlTag.Flag.ACCEPTS_INLINE, HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   EM(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   FONT(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.SIZE, HtmlTag.Attr.COLOR, HtmlTag.Attr.FACE)}),
   FRAME(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.NONE, new AttrMap[0]),
   FRAMESET(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   H1(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   H2(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   H3(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   H4(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   H5(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   H6(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   HEAD(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   HR(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.NONE, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.WIDTH)}),
   HTML(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   I(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   IMG(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.NONE, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.SRC, HtmlTag.Attr.ALT, HtmlTag.Attr.HEIGHT, HtmlTag.Attr.WIDTH), attrs(HtmlTag.AttrKind.OBSOLETE, HtmlTag.Attr.NAME), attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.ALIGN, HtmlTag.Attr.HSPACE, HtmlTag.Attr.VSPACE, HtmlTag.Attr.BORDER)}),
   LI(HtmlTag.BlockType.LIST_ITEM, HtmlTag.EndKind.OPTIONAL, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.VALUE)}),
   LINK(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.NONE, new AttrMap[0]),
   MENU(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]) {
      public boolean accepts(HtmlTag var1) {
         return var1 == LI;
      }
   },
   META(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.NONE, new AttrMap[0]),
   NOFRAMES(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   NOSCRIPT(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   OL(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.START, HtmlTag.Attr.TYPE)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == LI;
      }
   },
   P(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.OPTIONAL, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.ALIGN)}),
   PRE(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]) {
      public boolean accepts(HtmlTag var1) {
         switch (var1) {
            case IMG:
            case BIG:
            case SMALL:
            case SUB:
            case SUP:
               return false;
            default:
               return var1.blockType == HtmlTag.BlockType.INLINE;
         }
      }
   },
   SCRIPT(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.SRC)}),
   SMALL(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   SPAN(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   STRONG(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[0]),
   SUB(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   SUP(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   TABLE(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.SUMMARY, HtmlTag.Attr.FRAME, HtmlTag.Attr.RULES, HtmlTag.Attr.BORDER, HtmlTag.Attr.CELLPADDING, HtmlTag.Attr.CELLSPACING, HtmlTag.Attr.WIDTH), attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.ALIGN, HtmlTag.Attr.BGCOLOR)}) {
      public boolean accepts(HtmlTag var1) {
         switch (var1) {
            case CAPTION:
            case THEAD:
            case TBODY:
            case TFOOT:
            case TR:
               return true;
            default:
               return false;
         }
      }
   },
   TBODY(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.ALIGN, HtmlTag.Attr.CHAR, HtmlTag.Attr.CHAROFF, HtmlTag.Attr.VALIGN)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == TR;
      }
   },
   TD(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.OPTIONAL, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.COLSPAN, HtmlTag.Attr.ROWSPAN, HtmlTag.Attr.HEADERS, HtmlTag.Attr.SCOPE, HtmlTag.Attr.ABBR, HtmlTag.Attr.AXIS, HtmlTag.Attr.ALIGN, HtmlTag.Attr.CHAR, HtmlTag.Attr.CHAROFF, HtmlTag.Attr.VALIGN), attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.WIDTH, HtmlTag.Attr.BGCOLOR, HtmlTag.Attr.HEIGHT, HtmlTag.Attr.NOWRAP)}),
   TFOOT(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.REQUIRED, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.ALIGN, HtmlTag.Attr.CHAR, HtmlTag.Attr.CHAROFF, HtmlTag.Attr.VALIGN)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == TR;
      }
   },
   TH(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.OPTIONAL, EnumSet.of(HtmlTag.Flag.ACCEPTS_BLOCK, HtmlTag.Flag.ACCEPTS_INLINE), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.COLSPAN, HtmlTag.Attr.ROWSPAN, HtmlTag.Attr.HEADERS, HtmlTag.Attr.SCOPE, HtmlTag.Attr.ABBR, HtmlTag.Attr.AXIS, HtmlTag.Attr.ALIGN, HtmlTag.Attr.CHAR, HtmlTag.Attr.CHAROFF, HtmlTag.Attr.VALIGN), attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.WIDTH, HtmlTag.Attr.BGCOLOR, HtmlTag.Attr.HEIGHT, HtmlTag.Attr.NOWRAP)}),
   THEAD(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.REQUIRED, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.ALIGN, HtmlTag.Attr.CHAR, HtmlTag.Attr.CHAROFF, HtmlTag.Attr.VALIGN)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == TR;
      }
   },
   TITLE(HtmlTag.BlockType.OTHER, HtmlTag.EndKind.REQUIRED, new AttrMap[0]),
   TR(HtmlTag.BlockType.TABLE_ITEM, HtmlTag.EndKind.OPTIONAL, new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.ALIGN, HtmlTag.Attr.CHAR, HtmlTag.Attr.CHAROFF, HtmlTag.Attr.VALIGN), attrs(HtmlTag.AttrKind.USE_CSS, HtmlTag.Attr.BGCOLOR)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == TH || var1 == TD;
      }
   },
   TT(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   U(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT, HtmlTag.Flag.NO_NEST), new AttrMap[0]),
   UL(HtmlTag.BlockType.BLOCK, HtmlTag.EndKind.REQUIRED, EnumSet.of(HtmlTag.Flag.EXPECT_CONTENT), new AttrMap[]{attrs(HtmlTag.AttrKind.OK, HtmlTag.Attr.COMPACT, HtmlTag.Attr.TYPE)}) {
      public boolean accepts(HtmlTag var1) {
         return var1 == LI;
      }
   },
   VAR(HtmlTag.BlockType.INLINE, HtmlTag.EndKind.REQUIRED, new AttrMap[0]);

   public final BlockType blockType;
   public final EndKind endKind;
   public final Set flags;
   private final Map attrs;
   private static final Map index = new HashMap();

   private HtmlTag(BlockType var3, EndKind var4, AttrMap... var5) {
      this(var3, var4, Collections.emptySet(), var5);
   }

   private HtmlTag(BlockType var3, EndKind var4, Set var5, AttrMap... var6) {
      this.blockType = var3;
      this.endKind = var4;
      this.flags = var5;
      this.attrs = new EnumMap(Attr.class);
      AttrMap[] var7 = var6;
      int var8 = var6.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         AttrMap var10 = var7[var9];
         this.attrs.putAll(var10);
      }

      this.attrs.put(HtmlTag.Attr.CLASS, HtmlTag.AttrKind.OK);
      this.attrs.put(HtmlTag.Attr.ID, HtmlTag.AttrKind.OK);
      this.attrs.put(HtmlTag.Attr.STYLE, HtmlTag.AttrKind.OK);
   }

   public boolean accepts(HtmlTag var1) {
      if (this.flags.contains(HtmlTag.Flag.ACCEPTS_BLOCK) && this.flags.contains(HtmlTag.Flag.ACCEPTS_INLINE)) {
         return var1.blockType == HtmlTag.BlockType.BLOCK || var1.blockType == HtmlTag.BlockType.INLINE;
      } else if (this.flags.contains(HtmlTag.Flag.ACCEPTS_BLOCK)) {
         return var1.blockType == HtmlTag.BlockType.BLOCK;
      } else if (this.flags.contains(HtmlTag.Flag.ACCEPTS_INLINE)) {
         return var1.blockType == HtmlTag.BlockType.INLINE;
      } else {
         switch (this.blockType) {
            case BLOCK:
            case INLINE:
               return var1.blockType == HtmlTag.BlockType.INLINE;
            case OTHER:
               return true;
            default:
               throw new AssertionError(this + ":" + var1);
         }
      }
   }

   public boolean acceptsText() {
      return this.accepts(B);
   }

   public String getText() {
      return StringUtils.toLowerCase(this.name());
   }

   public Attr getAttr(Name var1) {
      return (Attr)HtmlTag.Attr.index.get(StringUtils.toLowerCase(var1.toString()));
   }

   public AttrKind getAttrKind(Name var1) {
      AttrKind var2 = (AttrKind)this.attrs.get(this.getAttr(var1));
      return var2 == null ? HtmlTag.AttrKind.INVALID : var2;
   }

   private static AttrMap attrs(AttrKind var0, Attr... var1) {
      AttrMap var2 = new AttrMap();
      Attr[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Attr var6 = var3[var5];
         var2.put(var6, var0);
      }

      return var2;
   }

   static HtmlTag get(Name var0) {
      return (HtmlTag)index.get(StringUtils.toLowerCase(var0.toString()));
   }

   // $FF: synthetic method
   HtmlTag(BlockType var3, EndKind var4, Set var5, AttrMap[] var6, Object var7) {
      this(var3, var4, var5, var6);
   }

   // $FF: synthetic method
   HtmlTag(BlockType var3, EndKind var4, AttrMap[] var5, Object var6) {
      this(var3, var4, var5);
   }

   static {
      HtmlTag[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         HtmlTag var3 = var0[var2];
         index.put(var3.getText(), var3);
      }

   }

   private static class AttrMap extends EnumMap {
      private static final long serialVersionUID = 0L;

      AttrMap() {
         super(Attr.class);
      }
   }

   public static enum AttrKind {
      INVALID,
      OBSOLETE,
      USE_CSS,
      OK;
   }

   public static enum Attr {
      ABBR,
      ALIGN,
      ALT,
      AXIS,
      BGCOLOR,
      BORDER,
      CELLSPACING,
      CELLPADDING,
      CHAR,
      CHAROFF,
      CLEAR,
      CLASS,
      COLOR,
      COLSPAN,
      COMPACT,
      FACE,
      FRAME,
      HEADERS,
      HEIGHT,
      HREF,
      HSPACE,
      ID,
      NAME,
      NOWRAP,
      REVERSED,
      ROWSPAN,
      RULES,
      SCOPE,
      SIZE,
      SPACE,
      SRC,
      START,
      STYLE,
      SUMMARY,
      TARGET,
      TYPE,
      VALIGN,
      VALUE,
      VSPACE,
      WIDTH;

      static final Map index = new HashMap();

      public String getText() {
         return StringUtils.toLowerCase(this.name());
      }

      static {
         Attr[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            Attr var3 = var0[var2];
            index.put(var3.getText(), var3);
         }

      }
   }

   public static enum Flag {
      ACCEPTS_BLOCK,
      ACCEPTS_INLINE,
      EXPECT_CONTENT,
      NO_NEST;
   }

   public static enum EndKind {
      NONE,
      OPTIONAL,
      REQUIRED;
   }

   public static enum BlockType {
      BLOCK,
      INLINE,
      LIST_ITEM,
      TABLE_ITEM,
      OTHER;
   }
}
