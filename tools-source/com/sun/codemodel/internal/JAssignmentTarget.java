package com.sun.codemodel.internal;

public interface JAssignmentTarget extends JGenerable, JExpression {
   JExpression assign(JExpression var1);

   JExpression assignPlus(JExpression var1);
}
