package com.sun.codemodel.internal;

public abstract class JExpr {
   private static final JExpression __this = new JAtom("this");
   private static final JExpression __super = new JAtom("super");
   private static final JExpression __null = new JAtom("null");
   public static final JExpression TRUE = new JAtom("true");
   public static final JExpression FALSE = new JAtom("false");
   static final String charEscape = "\b\t\n\f\r\"'\\";
   static final String charMacro = "btnfr\"'\\";

   private JExpr() {
   }

   public static JExpression assign(JAssignmentTarget lhs, JExpression rhs) {
      return new JAssignment(lhs, rhs);
   }

   public static JExpression assignPlus(JAssignmentTarget lhs, JExpression rhs) {
      return new JAssignment(lhs, rhs, "+");
   }

   public static JInvocation _new(JClass c) {
      return new JInvocation(c);
   }

   public static JInvocation _new(JType t) {
      return new JInvocation(t);
   }

   public static JInvocation invoke(String method) {
      return new JInvocation((JExpression)null, method);
   }

   public static JInvocation invoke(JMethod method) {
      return new JInvocation((JExpression)null, method);
   }

   public static JInvocation invoke(JExpression lhs, JMethod method) {
      return new JInvocation(lhs, method);
   }

   public static JInvocation invoke(JExpression lhs, String method) {
      return new JInvocation(lhs, method);
   }

   public static JFieldRef ref(String field) {
      return new JFieldRef((JExpression)null, field);
   }

   public static JFieldRef ref(JExpression lhs, JVar field) {
      return new JFieldRef(lhs, field);
   }

   public static JFieldRef ref(JExpression lhs, String field) {
      return new JFieldRef(lhs, field);
   }

   public static JFieldRef refthis(String field) {
      return new JFieldRef((JGenerable)null, field, true);
   }

   public static JExpression dotclass(final JClass cl) {
      return new JExpressionImpl() {
         public void generate(JFormatter f) {
            JClass c;
            if (cl instanceof JNarrowedClass) {
               c = ((JNarrowedClass)cl).basis;
            } else {
               c = cl;
            }

            f.g((JGenerable)c).p(".class");
         }
      };
   }

   public static JArrayCompRef component(JExpression lhs, JExpression index) {
      return new JArrayCompRef(lhs, index);
   }

   public static JCast cast(JType type, JExpression expr) {
      return new JCast(type, expr);
   }

   public static JArray newArray(JType type) {
      return newArray(type, (JExpression)null);
   }

   public static JArray newArray(JType type, JExpression size) {
      return new JArray(type.erasure(), size);
   }

   public static JArray newArray(JType type, int size) {
      return newArray(type, lit(size));
   }

   public static JExpression _this() {
      return __this;
   }

   public static JExpression _super() {
      return __super;
   }

   public static JExpression _null() {
      return __null;
   }

   public static JExpression lit(boolean b) {
      return b ? TRUE : FALSE;
   }

   public static JExpression lit(int n) {
      return new JAtom(Integer.toString(n));
   }

   public static JExpression lit(long n) {
      return new JAtom(Long.toString(n) + "L");
   }

   public static JExpression lit(float f) {
      if (f == Float.NEGATIVE_INFINITY) {
         return new JAtom("java.lang.Float.NEGATIVE_INFINITY");
      } else if (f == Float.POSITIVE_INFINITY) {
         return new JAtom("java.lang.Float.POSITIVE_INFINITY");
      } else {
         return Float.isNaN(f) ? new JAtom("java.lang.Float.NaN") : new JAtom(Float.toString(f) + "F");
      }
   }

   public static JExpression lit(double d) {
      if (d == Double.NEGATIVE_INFINITY) {
         return new JAtom("java.lang.Double.NEGATIVE_INFINITY");
      } else if (d == Double.POSITIVE_INFINITY) {
         return new JAtom("java.lang.Double.POSITIVE_INFINITY");
      } else {
         return Double.isNaN(d) ? new JAtom("java.lang.Double.NaN") : new JAtom(Double.toString(d) + "D");
      }
   }

   public static String quotify(char quote, String s) {
      int n = s.length();
      StringBuilder sb = new StringBuilder(n + 2);
      sb.append(quote);

      for(int i = 0; i < n; ++i) {
         char c = s.charAt(i);
         int j = "\b\t\n\f\r\"'\\".indexOf(c);
         if (j >= 0) {
            if ((quote != '"' || c != '\'') && (quote != '\'' || c != '"')) {
               sb.append('\\');
               sb.append("btnfr\"'\\".charAt(j));
            } else {
               sb.append(c);
            }
         } else if (c >= ' ' && '~' >= c) {
            sb.append(c);
         } else {
            sb.append("\\u");
            String hex = Integer.toHexString(c & '\uffff');

            for(int k = hex.length(); k < 4; ++k) {
               sb.append('0');
            }

            sb.append(hex);
         }
      }

      sb.append(quote);
      return sb.toString();
   }

   public static JExpression lit(char c) {
      return new JAtom(quotify('\'', "" + c));
   }

   public static JExpression lit(String s) {
      return new JStringLiteral(s);
   }

   public static JExpression direct(final String source) {
      return new JExpressionImpl() {
         public void generate(JFormatter f) {
            f.p('(').p(source).p(')');
         }
      };
   }
}
