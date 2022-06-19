package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import java.io.IOException;
import java.io.Writer;

public class DocType extends Content {
   private String docType;
   public static final DocType TRANSITIONAL = new DocType("Transitional", "http://www.w3.org/TR/html4/loose.dtd");
   public static final DocType FRAMESET = new DocType("Frameset", "http://www.w3.org/TR/html4/frameset.dtd");

   private DocType(String var1, String var2) {
      this.docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " + var1 + "//EN\" \"" + var2 + "\">" + DocletConstants.NL;
   }

   public void addContent(Content var1) {
      throw new DocletAbortException("not supported");
   }

   public void addContent(String var1) {
      throw new DocletAbortException("not supported");
   }

   public boolean isEmpty() {
      return this.docType.length() == 0;
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      var1.write(this.docType);
      return true;
   }
}
