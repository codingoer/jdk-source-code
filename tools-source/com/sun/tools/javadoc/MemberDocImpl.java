package com.sun.tools.javadoc;

import com.sun.javadoc.MemberDoc;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;

public abstract class MemberDocImpl extends ProgramElementDocImpl implements MemberDoc {
   public MemberDocImpl(DocEnv var1, Symbol var2, TreePath var3) {
      super(var1, var2, var3);
   }

   public abstract boolean isSynthetic();
}
