package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlAttr;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractTreeWriter extends HtmlDocletWriter {
   protected final ClassTree classtree;
   private static final String LI_CIRCLE = "circle";

   protected AbstractTreeWriter(ConfigurationImpl var1, DocPath var2, ClassTree var3) throws IOException {
      super(var1, var2);
      this.classtree = var3;
   }

   protected void addLevelInfo(ClassDoc var1, List var2, boolean var3, Content var4) {
      int var5 = var2.size();
      if (var5 > 0) {
         HtmlTree var6 = new HtmlTree(HtmlTag.UL);

         for(int var7 = 0; var7 < var5; ++var7) {
            ClassDoc var8 = (ClassDoc)var2.get(var7);
            HtmlTree var9 = new HtmlTree(HtmlTag.LI);
            var9.addAttr(HtmlAttr.TYPE, "circle");
            this.addPartialInfo(var8, var9);
            this.addExtendsImplements(var1, var8, var9);
            this.addLevelInfo(var8, this.classtree.subs(var8, var3), var3, var9);
            var6.addContent((Content)var9);
         }

         var4.addContent((Content)var6);
      }

   }

   protected void addTree(List var1, String var2, Content var3) {
      if (var1.size() > 0) {
         ClassDoc var4 = (ClassDoc)var1.get(0);
         Content var5 = this.getResource(var2);
         var3.addContent((Content)HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, true, var5));
         this.addLevelInfo(!var4.isInterface() ? var4 : null, var1, var1 == this.classtree.baseEnums(), var3);
      }

   }

   protected void addExtendsImplements(ClassDoc var1, ClassDoc var2, Content var3) {
      ClassDoc[] var4 = var2.interfaces();
      if (var4.length > (var2.isInterface() ? 1 : 0)) {
         Arrays.sort(var4);
         int var5 = 0;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            if (var1 != var4[var6] && (var4[var6].isPublic() || Util.isLinkable(var4[var6], this.configuration))) {
               if (var5 == 0) {
                  if (var2.isInterface()) {
                     var3.addContent(" (");
                     var3.addContent(this.getResource("doclet.also"));
                     var3.addContent(" extends ");
                  } else {
                     var3.addContent(" (implements ");
                  }
               } else {
                  var3.addContent(", ");
               }

               this.addPreQualifiedClassLink(LinkInfoImpl.Kind.TREE, var4[var6], var3);
               ++var5;
            }
         }

         if (var5 > 0) {
            var3.addContent(")");
         }
      }

   }

   protected void addPartialInfo(ClassDoc var1, Content var2) {
      this.addPreQualifiedStrongClassLink(LinkInfoImpl.Kind.TREE, var1, var2);
   }

   protected Content getNavLinkTree() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.treeLabel);
      return var1;
   }
}
