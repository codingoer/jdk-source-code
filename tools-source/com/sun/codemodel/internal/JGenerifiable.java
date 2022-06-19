package com.sun.codemodel.internal;

public interface JGenerifiable {
   JTypeVar generify(String var1);

   JTypeVar generify(String var1, Class var2);

   JTypeVar generify(String var1, JClass var2);

   JTypeVar[] typeParams();
}
