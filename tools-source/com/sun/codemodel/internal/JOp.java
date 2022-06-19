package com.sun.codemodel.internal;

public abstract class JOp {
   private JOp() {
   }

   static boolean hasTopOp(JExpression e) {
      return e instanceof UnaryOp || e instanceof BinaryOp;
   }

   public static JExpression minus(JExpression e) {
      return new UnaryOp("-", e);
   }

   public static JExpression not(JExpression e) {
      if (e == JExpr.TRUE) {
         return JExpr.FALSE;
      } else {
         return (JExpression)(e == JExpr.FALSE ? JExpr.TRUE : new UnaryOp("!", e));
      }
   }

   public static JExpression complement(JExpression e) {
      return new UnaryOp("~", e);
   }

   public static JExpression incr(JExpression e) {
      return new TightUnaryOp(e, "++");
   }

   public static JExpression decr(JExpression e) {
      return new TightUnaryOp(e, "--");
   }

   public static JExpression plus(JExpression left, JExpression right) {
      return new BinaryOp("+", left, right);
   }

   public static JExpression minus(JExpression left, JExpression right) {
      return new BinaryOp("-", left, right);
   }

   public static JExpression mul(JExpression left, JExpression right) {
      return new BinaryOp("*", left, right);
   }

   public static JExpression div(JExpression left, JExpression right) {
      return new BinaryOp("/", left, right);
   }

   public static JExpression mod(JExpression left, JExpression right) {
      return new BinaryOp("%", left, right);
   }

   public static JExpression shl(JExpression left, JExpression right) {
      return new BinaryOp("<<", left, right);
   }

   public static JExpression shr(JExpression left, JExpression right) {
      return new BinaryOp(">>", left, right);
   }

   public static JExpression shrz(JExpression left, JExpression right) {
      return new BinaryOp(">>>", left, right);
   }

   public static JExpression band(JExpression left, JExpression right) {
      return new BinaryOp("&", left, right);
   }

   public static JExpression bor(JExpression left, JExpression right) {
      return new BinaryOp("|", left, right);
   }

   public static JExpression cand(JExpression left, JExpression right) {
      if (left == JExpr.TRUE) {
         return right;
      } else if (right == JExpr.TRUE) {
         return left;
      } else if (left == JExpr.FALSE) {
         return left;
      } else {
         return (JExpression)(right == JExpr.FALSE ? right : new BinaryOp("&&", left, right));
      }
   }

   public static JExpression cor(JExpression left, JExpression right) {
      if (left == JExpr.TRUE) {
         return left;
      } else if (right == JExpr.TRUE) {
         return right;
      } else if (left == JExpr.FALSE) {
         return right;
      } else {
         return (JExpression)(right == JExpr.FALSE ? left : new BinaryOp("||", left, right));
      }
   }

   public static JExpression xor(JExpression left, JExpression right) {
      return new BinaryOp("^", left, right);
   }

   public static JExpression lt(JExpression left, JExpression right) {
      return new BinaryOp("<", left, right);
   }

   public static JExpression lte(JExpression left, JExpression right) {
      return new BinaryOp("<=", left, right);
   }

   public static JExpression gt(JExpression left, JExpression right) {
      return new BinaryOp(">", left, right);
   }

   public static JExpression gte(JExpression left, JExpression right) {
      return new BinaryOp(">=", left, right);
   }

   public static JExpression eq(JExpression left, JExpression right) {
      return new BinaryOp("==", left, right);
   }

   public static JExpression ne(JExpression left, JExpression right) {
      return new BinaryOp("!=", left, right);
   }

   public static JExpression _instanceof(JExpression left, JType right) {
      return new BinaryOp("instanceof", left, right);
   }

   public static JExpression cond(JExpression cond, JExpression ifTrue, JExpression ifFalse) {
      return new TernaryOp("?", ":", cond, ifTrue, ifFalse);
   }

   private static class TernaryOp extends JExpressionImpl {
      String op1;
      String op2;
      JExpression e1;
      JExpression e2;
      JExpression e3;

      TernaryOp(String op1, String op2, JExpression e1, JExpression e2, JExpression e3) {
         this.e1 = e1;
         this.op1 = op1;
         this.e2 = e2;
         this.op2 = op2;
         this.e3 = e3;
      }

      public void generate(JFormatter f) {
         f.p('(').g((JGenerable)this.e1).p(this.op1).g((JGenerable)this.e2).p(this.op2).g((JGenerable)this.e3).p(')');
      }
   }

   private static class BinaryOp extends JExpressionImpl {
      String op;
      JExpression left;
      JGenerable right;

      BinaryOp(String op, JExpression left, JGenerable right) {
         this.left = left;
         this.op = op;
         this.right = right;
      }

      public void generate(JFormatter f) {
         f.p('(').g((JGenerable)this.left).p(this.op).g(this.right).p(')');
      }
   }

   private static class TightUnaryOp extends UnaryOp {
      TightUnaryOp(JExpression e, String op) {
         super(e, op);
      }

      public void generate(JFormatter f) {
         if (this.opFirst) {
            f.p(this.op).g((JGenerable)this.e);
         } else {
            f.g((JGenerable)this.e).p(this.op);
         }

      }
   }

   private static class UnaryOp extends JExpressionImpl {
      protected String op;
      protected JExpression e;
      protected boolean opFirst = true;

      UnaryOp(String op, JExpression e) {
         this.op = op;
         this.e = e;
      }

      UnaryOp(JExpression e, String op) {
         this.op = op;
         this.e = e;
         this.opFirst = false;
      }

      public void generate(JFormatter f) {
         if (this.opFirst) {
            f.p('(').p(this.op).g((JGenerable)this.e).p(')');
         } else {
            f.p('(').g((JGenerable)this.e).p(this.op).p(')');
         }

      }
   }
}
