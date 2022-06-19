package com.sun.codemodel.internal;

public interface JExpression extends JGenerable {
   JExpression minus();

   JExpression not();

   JExpression complement();

   JExpression incr();

   JExpression decr();

   JExpression plus(JExpression var1);

   JExpression minus(JExpression var1);

   JExpression mul(JExpression var1);

   JExpression div(JExpression var1);

   JExpression mod(JExpression var1);

   JExpression shl(JExpression var1);

   JExpression shr(JExpression var1);

   JExpression shrz(JExpression var1);

   JExpression band(JExpression var1);

   JExpression bor(JExpression var1);

   JExpression cand(JExpression var1);

   JExpression cor(JExpression var1);

   JExpression xor(JExpression var1);

   JExpression lt(JExpression var1);

   JExpression lte(JExpression var1);

   JExpression gt(JExpression var1);

   JExpression gte(JExpression var1);

   JExpression eq(JExpression var1);

   JExpression ne(JExpression var1);

   JExpression _instanceof(JType var1);

   JInvocation invoke(JMethod var1);

   JInvocation invoke(String var1);

   JFieldRef ref(JVar var1);

   JFieldRef ref(String var1);

   JArrayCompRef component(JExpression var1);
}
