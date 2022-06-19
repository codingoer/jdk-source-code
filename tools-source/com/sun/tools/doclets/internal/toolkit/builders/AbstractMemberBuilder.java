package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;

public abstract class AbstractMemberBuilder extends AbstractBuilder {
   public AbstractMemberBuilder(AbstractBuilder.Context var1) {
      super(var1);
   }

   public void build() throws DocletAbortException {
      throw new DocletAbortException("not supported");
   }

   public void build(XMLNode var1, Content var2) {
      if (this.hasMembersToDocument()) {
         super.build(var1, var2);
      }

   }

   public abstract boolean hasMembersToDocument();
}
