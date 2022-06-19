package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class JBlock implements JGenerable, JStatement {
   private final List content;
   private boolean bracesRequired;
   private boolean indentRequired;
   private int pos;

   public JBlock() {
      this(true, true);
   }

   public JBlock(boolean bracesRequired, boolean indentRequired) {
      this.content = new ArrayList();
      this.bracesRequired = true;
      this.indentRequired = true;
      this.bracesRequired = bracesRequired;
      this.indentRequired = indentRequired;
   }

   public List getContents() {
      return Collections.unmodifiableList(this.content);
   }

   private Object insert(Object statementOrDeclaration) {
      this.content.add(this.pos, statementOrDeclaration);
      ++this.pos;
      return statementOrDeclaration;
   }

   public int pos() {
      return this.pos;
   }

   public int pos(int newPos) {
      int r = this.pos;
      if (newPos <= this.content.size() && newPos >= 0) {
         this.pos = newPos;
         return r;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean isEmpty() {
      return this.content.isEmpty();
   }

   public JVar decl(JType type, String name) {
      return this.decl(0, type, name, (JExpression)null);
   }

   public JVar decl(JType type, String name, JExpression init) {
      return this.decl(0, type, name, init);
   }

   public JVar decl(int mods, JType type, String name, JExpression init) {
      JVar v = new JVar(JMods.forVar(mods), type, name, init);
      this.insert(v);
      this.bracesRequired = true;
      this.indentRequired = true;
      return v;
   }

   public JBlock assign(JAssignmentTarget lhs, JExpression exp) {
      this.insert(new JAssignment(lhs, exp));
      return this;
   }

   public JBlock assignPlus(JAssignmentTarget lhs, JExpression exp) {
      this.insert(new JAssignment(lhs, exp, "+"));
      return this;
   }

   public JInvocation invoke(JExpression expr, String method) {
      JInvocation i = new JInvocation(expr, method);
      this.insert(i);
      return i;
   }

   public JInvocation invoke(JExpression expr, JMethod method) {
      return (JInvocation)this.insert(new JInvocation(expr, method));
   }

   public JInvocation staticInvoke(JClass type, String method) {
      return (JInvocation)this.insert(new JInvocation(type, method));
   }

   public JInvocation invoke(String method) {
      return (JInvocation)this.insert(new JInvocation((JExpression)null, method));
   }

   public JInvocation invoke(JMethod method) {
      return (JInvocation)this.insert(new JInvocation((JExpression)null, method));
   }

   public JBlock add(JStatement s) {
      this.insert(s);
      return this;
   }

   public JConditional _if(JExpression expr) {
      return (JConditional)this.insert(new JConditional(expr));
   }

   public JForLoop _for() {
      return (JForLoop)this.insert(new JForLoop());
   }

   public JWhileLoop _while(JExpression test) {
      return (JWhileLoop)this.insert(new JWhileLoop(test));
   }

   public JSwitch _switch(JExpression test) {
      return (JSwitch)this.insert(new JSwitch(test));
   }

   public JDoLoop _do(JExpression test) {
      return (JDoLoop)this.insert(new JDoLoop(test));
   }

   public JTryBlock _try() {
      return (JTryBlock)this.insert(new JTryBlock());
   }

   public void _return() {
      this.insert(new JReturn((JExpression)null));
   }

   public void _return(JExpression exp) {
      this.insert(new JReturn(exp));
   }

   public void _throw(JExpression exp) {
      this.insert(new JThrow(exp));
   }

   public void _break() {
      this._break((JLabel)null);
   }

   public void _break(JLabel label) {
      this.insert(new JBreak(label));
   }

   public JLabel label(String name) {
      JLabel l = new JLabel(name);
      this.insert(l);
      return l;
   }

   public void _continue(JLabel label) {
      this.insert(new JContinue(label));
   }

   public void _continue() {
      this._continue((JLabel)null);
   }

   public JBlock block() {
      JBlock b = new JBlock();
      b.bracesRequired = false;
      b.indentRequired = false;
      return (JBlock)this.insert(b);
   }

   public JStatement directStatement(final String source) {
      JStatement s = new JStatement() {
         public void state(JFormatter f) {
            f.p(source).nl();
         }
      };
      this.add(s);
      return s;
   }

   public void generate(JFormatter f) {
      if (this.bracesRequired) {
         f.p('{').nl();
      }

      if (this.indentRequired) {
         f.i();
      }

      this.generateBody(f);
      if (this.indentRequired) {
         f.o();
      }

      if (this.bracesRequired) {
         f.p('}');
      }

   }

   void generateBody(JFormatter f) {
      Iterator var2 = this.content.iterator();

      while(var2.hasNext()) {
         Object o = var2.next();
         if (o instanceof JDeclaration) {
            f.d((JDeclaration)o);
         } else {
            f.s((JStatement)o);
         }
      }

   }

   public JForEach forEach(JType varType, String name, JExpression collection) {
      return (JForEach)this.insert(new JForEach(varType, name, collection));
   }

   public void state(JFormatter f) {
      f.g((JGenerable)this);
      if (this.bracesRequired) {
         f.nl();
      }

   }
}
