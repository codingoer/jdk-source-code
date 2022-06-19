package com.sun.tools.example.debug.expr;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class ExpressionParser implements ExpressionParserConstants {
   Stack stack = new Stack();
   VirtualMachine vm = null;
   GetFrame frameGetter = null;
   private static GetFrame lastFrameGetter;
   private static LValue lastLValue;
   public ExpressionParserTokenManager token_source;
   ASCII_UCodeESC_CharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private Token jj_scanpos;
   private Token jj_lastpos;
   private int jj_la;
   public boolean lookingAhead = false;
   private int jj_gen;
   private final int[] jj_la1 = new int[44];
   private final int[] jj_la1_0 = new int[]{136352768, 0, 136352768, 0, 16777216, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16777216, 0, 0, 16777216, 16777216, 0, 0, 0, 0, 0, 0, 0, 16777216, 0, 16777216, 16777216, 16777216, 0, 0, 0};
   private final int[] jj_la1_1 = new int[]{8212, 0, 8212, 0, -2008776512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2008776512, 0, 0, -2008776512, -2008776512, 0, 0, 0, 0, 0, 0, 0, -2008776512, 0, -2009071488, 4194304, -2008776512, 0, 0, 64};
   private final int[] jj_la1_2 = new int[]{8, 1024, 0, 8192, -267648946, 32768, 1048576, 67108864, 134217728, 0, 0, 0, 37748736, 37748736, 0, 25362432, 25362432, 0, 0, -1073741824, -1073741824, 0, 0, -1073741824, -267648946, 786432, 786432, 78, 786510, 64, 805306368, 805306368, 1024, 1024, 64, 17472, 78, 17472, 6, 0, -267648946, 8192, 1088, 0};
   private final int[] jj_la1_3 = new int[]{0, 0, 0, 0, 0, 1048064, 0, 0, 0, 8, 16, 4, 0, 0, 0, 0, 0, 448, 448, 0, 0, 35, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private final JJExpressionParserCalls[] jj_2_rtns = new JJExpressionParserCalls[9];
   private boolean jj_rescan = false;
   private int jj_gc = 0;
   private Vector jj_expentries = new Vector();
   private int[] jj_expentry;
   private int jj_kind = -1;
   private int[] jj_lasttokens = new int[100];
   private int jj_endpos;

   LValue peek() {
      return (LValue)this.stack.peek();
   }

   LValue pop() {
      return (LValue)this.stack.pop();
   }

   void push(LValue var1) {
      this.stack.push(var1);
   }

   public static Value getMassagedValue() throws ParseException {
      return lastLValue.getMassagedValue(lastFrameGetter);
   }

   public static Value evaluate(String var0, VirtualMachine var1, GetFrame var2) throws ParseException, InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException {
      StringBufferInputStream var3 = new StringBufferInputStream(var0);
      ExpressionParser var4 = new ExpressionParser(var3);
      var4.vm = var1;
      var4.frameGetter = var2;
      var4.Expression();
      lastFrameGetter = var2;
      lastLValue = var4.pop();
      return lastLValue.getValue();
   }

   public static void main(String[] var0) {
      System.out.print("Java Expression Parser:  ");
      ExpressionParser var1;
      if (var0.length == 0) {
         System.out.println("Reading from standard input . . .");
         var1 = new ExpressionParser(System.in);
      } else {
         if (var0.length != 1) {
            System.out.println("Usage is one of:");
            System.out.println("         java ExpressionParser < inputfile");
            System.out.println("OR");
            System.out.println("         java ExpressionParser inputfile");
            return;
         }

         System.out.println("Reading from file " + var0[0] + " . . .");

         try {
            var1 = new ExpressionParser(new FileInputStream(var0[0]));
         } catch (FileNotFoundException var4) {
            System.out.println("Java Parser Version 1.0.2:  File " + var0[0] + " not found.");
            return;
         }
      }

      try {
         var1.Expression();
         System.out.print("Java Expression Parser:  ");
         System.out.println("Java program parsed successfully.");
      } catch (ParseException var3) {
         System.out.print("Java Expression Parser:  ");
         System.out.println("Encountered errors during parse.");
      }

   }

   public final void Type() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 10:
         case 12:
         case 15:
         case 21:
         case 27:
         case 34:
         case 36:
         case 45:
            this.PrimitiveType();
            break;
         case 67:
            this.Name();
            break;
         default:
            this.jj_la1[0] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 74:
               this.jj_consume_token(74);
               this.jj_consume_token(75);
               break;
            default:
               this.jj_la1[1] = this.jj_gen;
               return;
         }
      }
   }

   public final void PrimitiveType() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 10:
            this.jj_consume_token(10);
            break;
         case 12:
            this.jj_consume_token(12);
            break;
         case 15:
            this.jj_consume_token(15);
            break;
         case 21:
            this.jj_consume_token(21);
            break;
         case 27:
            this.jj_consume_token(27);
            break;
         case 34:
            this.jj_consume_token(34);
            break;
         case 36:
            this.jj_consume_token(36);
            break;
         case 45:
            this.jj_consume_token(45);
            break;
         default:
            this.jj_la1[2] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final String Name() throws ParseException {
      StringBuffer var1 = new StringBuffer();
      this.jj_consume_token(67);
      var1.append(this.token);

      while(this.jj_2_1(2)) {
         this.jj_consume_token(78);
         this.jj_consume_token(67);
         var1.append('.');
         var1.append(this.token);
      }

      return var1.toString();
   }

   public final void NameList() throws ParseException {
      this.Name();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 77:
               this.jj_consume_token(77);
               this.Name();
               break;
            default:
               this.jj_la1[3] = this.jj_gen;
               return;
         }
      }
   }

   public final void Expression() throws ParseException {
      if (this.jj_2_2(Integer.MAX_VALUE)) {
         this.Assignment();
      } else {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 24:
            case 38:
            case 39:
            case 47:
            case 50:
            case 54:
            case 59:
            case 63:
            case 65:
            case 66:
            case 67:
            case 70:
            case 82:
            case 83:
            case 92:
            case 93:
            case 94:
            case 95:
               this.ConditionalExpression();
               break;
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 48:
            case 49:
            case 51:
            case 52:
            case 53:
            case 55:
            case 56:
            case 57:
            case 58:
            case 60:
            case 61:
            case 62:
            case 64:
            case 68:
            case 69:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            default:
               this.jj_la1[4] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }
      }

   }

   public final void Assignment() throws ParseException {
      this.PrimaryExpression();
      this.AssignmentOperator();
      this.Expression();
      LValue var1 = this.pop();
      this.pop().setValue(var1);
      this.push(var1);
   }

   public final void AssignmentOperator() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 79:
            this.jj_consume_token(79);
            break;
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         default:
            this.jj_la1[5] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 105:
            this.jj_consume_token(105);
            break;
         case 106:
            this.jj_consume_token(106);
            break;
         case 107:
            this.jj_consume_token(107);
            break;
         case 108:
            this.jj_consume_token(108);
            break;
         case 109:
            this.jj_consume_token(109);
            break;
         case 110:
            this.jj_consume_token(110);
            break;
         case 111:
            this.jj_consume_token(111);
            break;
         case 112:
            this.jj_consume_token(112);
            break;
         case 113:
            this.jj_consume_token(113);
            break;
         case 114:
            this.jj_consume_token(114);
            break;
         case 115:
            this.jj_consume_token(115);
      }

   }

   public final void ConditionalExpression() throws ParseException {
      this.ConditionalOrExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 84:
            this.jj_consume_token(84);
            this.Expression();
            this.jj_consume_token(85);
            this.ConditionalExpression();
            LValue var1 = this.pop();
            LValue var2 = this.pop();
            Value var3 = this.pop().interiorGetValue();
            if (!(var3 instanceof BooleanValue)) {
               throw new ParseException("Condition must be boolean");
            }

            this.push(((BooleanValue)var3).booleanValue() ? var2 : var1);
            break;
         default:
            this.jj_la1[6] = this.jj_gen;
      }

   }

   public final void ConditionalOrExpression() throws ParseException {
      this.ConditionalAndExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 90:
            this.jj_consume_token(90);
            this.ConditionalAndExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[7] = this.jj_gen;
      }
   }

   public final void ConditionalAndExpression() throws ParseException {
      this.InclusiveOrExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 91:
            this.jj_consume_token(91);
            this.InclusiveOrExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[8] = this.jj_gen;
      }
   }

   public final void InclusiveOrExpression() throws ParseException {
      this.ExclusiveOrExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 99:
            this.jj_consume_token(99);
            this.ExclusiveOrExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[9] = this.jj_gen;
      }
   }

   public final void ExclusiveOrExpression() throws ParseException {
      this.AndExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 100:
            this.jj_consume_token(100);
            this.AndExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[10] = this.jj_gen;
      }
   }

   public final void AndExpression() throws ParseException {
      this.EqualityExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 98:
            this.jj_consume_token(98);
            this.EqualityExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[11] = this.jj_gen;
      }
   }

   public final void EqualityExpression() throws ParseException {
      this.InstanceOfExpression();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 86:
            case 89:
               Token var1;
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 86:
                     var1 = this.jj_consume_token(86);
                     break;
                  case 89:
                     var1 = this.jj_consume_token(89);
                     break;
                  default:
                     this.jj_la1[13] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
               }

               this.InstanceOfExpression();
               LValue var2 = this.pop();
               this.push(LValue.booleanOperation(this.vm, var1, this.pop(), var2));
               break;
            default:
               this.jj_la1[12] = this.jj_gen;
               return;
         }
      }
   }

   public final void InstanceOfExpression() throws ParseException {
      this.RelationalExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 33:
            this.jj_consume_token(33);
            this.Type();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[14] = this.jj_gen;
      }
   }

   public final void RelationalExpression() throws ParseException {
      this.ShiftExpression();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 80:
            case 81:
            case 87:
            case 88:
               Token var1;
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 80:
                     var1 = this.jj_consume_token(80);
                     break;
                  case 81:
                     var1 = this.jj_consume_token(81);
                     break;
                  case 82:
                  case 83:
                  case 84:
                  case 85:
                  case 86:
                  default:
                     this.jj_la1[16] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
                  case 87:
                     var1 = this.jj_consume_token(87);
                     break;
                  case 88:
                     var1 = this.jj_consume_token(88);
               }

               this.ShiftExpression();
               LValue var2 = this.pop();
               this.push(LValue.booleanOperation(this.vm, var1, this.pop(), var2));
               break;
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            default:
               this.jj_la1[15] = this.jj_gen;
               return;
         }
      }
   }

   public final void ShiftExpression() throws ParseException {
      this.AdditiveExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 102:
         case 103:
         case 104:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 102:
                  this.jj_consume_token(102);
                  break;
               case 103:
                  this.jj_consume_token(103);
                  break;
               case 104:
                  this.jj_consume_token(104);
                  break;
               default:
                  this.jj_la1[18] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }

            this.AdditiveExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[17] = this.jj_gen;
      }
   }

   public final void AdditiveExpression() throws ParseException {
      this.MultiplicativeExpression();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 94:
            case 95:
               Token var1;
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 94:
                     var1 = this.jj_consume_token(94);
                     break;
                  case 95:
                     var1 = this.jj_consume_token(95);
                     break;
                  default:
                     this.jj_la1[20] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
               }

               this.MultiplicativeExpression();
               LValue var2 = this.pop();
               this.push(LValue.operation(this.vm, var1, this.pop(), var2, this.frameGetter));
               break;
            default:
               this.jj_la1[19] = this.jj_gen;
               return;
         }
      }
   }

   public final void MultiplicativeExpression() throws ParseException {
      this.UnaryExpression();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 96:
            case 97:
            case 101:
               Token var1;
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 96:
                     var1 = this.jj_consume_token(96);
                     break;
                  case 97:
                     var1 = this.jj_consume_token(97);
                     break;
                  case 101:
                     var1 = this.jj_consume_token(101);
                     break;
                  default:
                     this.jj_la1[22] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
               }

               this.UnaryExpression();
               LValue var2 = this.pop();
               this.push(LValue.operation(this.vm, var1, this.pop(), var2, this.frameGetter));
               break;
            default:
               this.jj_la1[21] = this.jj_gen;
               return;
         }
      }
   }

   public final void UnaryExpression() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
         case 38:
         case 39:
         case 47:
         case 50:
         case 54:
         case 59:
         case 63:
         case 65:
         case 66:
         case 67:
         case 70:
         case 82:
         case 83:
            this.UnaryExpressionNotPlusMinus();
            break;
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 55:
         case 56:
         case 57:
         case 58:
         case 60:
         case 61:
         case 62:
         case 64:
         case 68:
         case 69:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         default:
            this.jj_la1[24] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 92:
            this.PreIncrementExpression();
            break;
         case 93:
            this.PreDecrementExpression();
            break;
         case 94:
         case 95:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 94:
                  this.jj_consume_token(94);
                  break;
               case 95:
                  this.jj_consume_token(95);
                  break;
               default:
                  this.jj_la1[23] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }

            this.UnaryExpression();
            throw new ParseException("operation not yet supported");
      }

   }

   public final void PreIncrementExpression() throws ParseException {
      this.jj_consume_token(92);
      this.PrimaryExpression();
      throw new ParseException("operation not yet supported");
   }

   public final void PreDecrementExpression() throws ParseException {
      this.jj_consume_token(93);
      this.PrimaryExpression();
      throw new ParseException("operation not yet supported");
   }

   public final void UnaryExpressionNotPlusMinus() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 82:
         case 83:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 82:
                  this.jj_consume_token(82);
                  break;
               case 83:
                  this.jj_consume_token(83);
                  break;
               default:
                  this.jj_la1[25] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }

            this.UnaryExpression();
            throw new ParseException("operation not yet supported");
         default:
            this.jj_la1[26] = this.jj_gen;
            if (this.jj_2_3(Integer.MAX_VALUE)) {
               this.CastExpression();
            } else {
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 24:
                  case 38:
                  case 39:
                  case 47:
                  case 50:
                  case 54:
                  case 59:
                  case 63:
                  case 65:
                  case 66:
                  case 67:
                  case 70:
                     this.PostfixExpression();
                     break;
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 48:
                  case 49:
                  case 51:
                  case 52:
                  case 53:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 60:
                  case 61:
                  case 62:
                  case 64:
                  case 68:
                  case 69:
                  default:
                     this.jj_la1[27] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
               }
            }

      }
   }

   public final void CastLookahead() throws ParseException {
      if (this.jj_2_4(2)) {
         this.jj_consume_token(70);
         this.PrimitiveType();
      } else if (this.jj_2_5(Integer.MAX_VALUE)) {
         this.jj_consume_token(70);
         this.Name();
         this.jj_consume_token(74);
         this.jj_consume_token(75);
      } else {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 70:
               this.jj_consume_token(70);
               this.Name();
               this.jj_consume_token(71);
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 24:
                  case 39:
                  case 54:
                  case 59:
                  case 63:
                  case 65:
                  case 66:
                     this.Literal();
                     return;
                  case 25:
                  case 26:
                  case 27:
                  case 28:
                  case 29:
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 48:
                  case 49:
                  case 51:
                  case 52:
                  case 53:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 60:
                  case 61:
                  case 62:
                  case 64:
                  case 68:
                  case 69:
                  case 71:
                  case 72:
                  case 73:
                  case 74:
                  case 75:
                  case 76:
                  case 77:
                  case 78:
                  case 79:
                  case 80:
                  case 81:
                  default:
                     this.jj_la1[28] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
                  case 38:
                     this.jj_consume_token(38);
                     return;
                  case 47:
                     this.jj_consume_token(47);
                     return;
                  case 50:
                     this.jj_consume_token(50);
                     return;
                  case 67:
                     this.jj_consume_token(67);
                     return;
                  case 70:
                     this.jj_consume_token(70);
                     return;
                  case 82:
                     this.jj_consume_token(82);
                     return;
                  case 83:
                     this.jj_consume_token(83);
                     return;
               }
            default:
               this.jj_la1[29] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }
      }

   }

   public final void PostfixExpression() throws ParseException {
      this.PrimaryExpression();
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 92:
         case 93:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 92:
                  this.jj_consume_token(92);
                  return;
               case 93:
                  this.jj_consume_token(93);
                  throw new ParseException("operation not yet supported");
               default:
                  this.jj_la1[30] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }
         default:
            this.jj_la1[31] = this.jj_gen;
      }
   }

   public final void CastExpression() throws ParseException {
      if (this.jj_2_6(2)) {
         this.jj_consume_token(70);
         this.PrimitiveType();

         while(true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 74:
                  this.jj_consume_token(74);
                  this.jj_consume_token(75);
                  break;
               default:
                  this.jj_la1[32] = this.jj_gen;
                  this.jj_consume_token(71);
                  this.UnaryExpression();
                  return;
            }
         }
      } else {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 70:
               this.jj_consume_token(70);
               this.Name();

               while(true) {
                  switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                     case 74:
                        this.jj_consume_token(74);
                        this.jj_consume_token(75);
                        break;
                     default:
                        this.jj_la1[33] = this.jj_gen;
                        this.jj_consume_token(71);
                        this.UnaryExpressionNotPlusMinus();
                        return;
                  }
               }
            default:
               this.jj_la1[34] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }
      }
   }

   public final void PrimaryExpression() throws ParseException {
      this.PrimaryPrefix();

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 70:
            case 74:
            case 78:
               this.PrimarySuffix();
               break;
            default:
               this.jj_la1[35] = this.jj_gen;
               return;
         }
      }
   }

   public final void PrimaryPrefix() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
         case 39:
         case 54:
         case 59:
         case 63:
         case 65:
         case 66:
            this.Literal();
            break;
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 55:
         case 56:
         case 57:
         case 58:
         case 60:
         case 61:
         case 62:
         case 64:
         case 68:
         case 69:
         default:
            this.jj_la1[36] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 38:
            this.AllocationExpression();
            break;
         case 47:
            this.jj_consume_token(47);
            this.jj_consume_token(78);
            this.jj_consume_token(67);
            throw new ParseException("operation not yet supported");
         case 50:
            this.jj_consume_token(50);
            this.push(LValue.makeThisObject(this.vm, this.frameGetter, this.token));
            break;
         case 67:
            String var1 = this.Name();
            this.push(LValue.makeName(this.vm, this.frameGetter, var1));
            break;
         case 70:
            this.jj_consume_token(70);
            this.Expression();
            this.jj_consume_token(71);
      }

   }

   public final void PrimarySuffix() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 70:
            List var1 = this.Arguments();
            this.peek().invokeWith(var1);
            break;
         case 74:
            this.jj_consume_token(74);
            this.Expression();
            this.jj_consume_token(75);
            LValue var2 = this.pop();
            this.push(this.pop().arrayElementLValue(var2));
            break;
         case 78:
            this.jj_consume_token(78);
            this.jj_consume_token(67);
            this.push(this.pop().memberLValue(this.frameGetter, this.token.image));
            break;
         default:
            this.jj_la1[37] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final void Literal() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
         case 54:
            this.BooleanLiteral();
            this.push(LValue.makeBoolean(this.vm, this.token));
            break;
         case 39:
            this.NullLiteral();
            this.push(LValue.makeNull(this.vm, this.token));
            break;
         case 59:
            this.jj_consume_token(59);
            this.push(LValue.makeInteger(this.vm, this.token));
            break;
         case 63:
            this.jj_consume_token(63);
            this.push(LValue.makeFloat(this.vm, this.token));
            break;
         case 65:
            this.jj_consume_token(65);
            this.push(LValue.makeCharacter(this.vm, this.token));
            break;
         case 66:
            this.jj_consume_token(66);
            this.push(LValue.makeString(this.vm, this.token));
            break;
         default:
            this.jj_la1[38] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final void BooleanLiteral() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
            this.jj_consume_token(24);
            break;
         case 54:
            this.jj_consume_token(54);
            break;
         default:
            this.jj_la1[39] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }

   }

   public final void NullLiteral() throws ParseException {
      this.jj_consume_token(39);
   }

   public final List Arguments() throws ParseException {
      ArrayList var1 = new ArrayList();
      this.jj_consume_token(70);
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
         case 38:
         case 39:
         case 47:
         case 50:
         case 54:
         case 59:
         case 63:
         case 65:
         case 66:
         case 67:
         case 70:
         case 82:
         case 83:
         case 92:
         case 93:
         case 94:
         case 95:
            this.ArgumentList(var1);
            break;
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 48:
         case 49:
         case 51:
         case 52:
         case 53:
         case 55:
         case 56:
         case 57:
         case 58:
         case 60:
         case 61:
         case 62:
         case 64:
         case 68:
         case 69:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         default:
            this.jj_la1[40] = this.jj_gen;
      }

      this.jj_consume_token(71);
      return var1;
   }

   public final void ArgumentList(List var1) throws ParseException {
      this.Expression();
      var1.add(this.pop().interiorGetValue());

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 77:
               this.jj_consume_token(77);
               this.Expression();
               var1.add(this.pop().interiorGetValue());
               break;
            default:
               this.jj_la1[41] = this.jj_gen;
               return;
         }
      }
   }

   public final void AllocationExpression() throws ParseException {
      if (this.jj_2_7(2)) {
         this.jj_consume_token(38);
         this.PrimitiveType();
         this.ArrayDimensions();
      } else {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 38:
               this.jj_consume_token(38);
               String var2 = this.Name();
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 70:
                     List var1 = this.Arguments();
                     this.push(LValue.makeNewObject(this.vm, this.frameGetter, var2, var1));
                     return;
                  case 74:
                     this.ArrayDimensions();
                     throw new ParseException("operation not yet supported");
                  default:
                     this.jj_la1[42] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
               }
            default:
               this.jj_la1[43] = this.jj_gen;
               this.jj_consume_token(-1);
               throw new ParseException();
         }
      }
   }

   public final void ArrayDimensions() throws ParseException {
      do {
         this.jj_consume_token(74);
         this.Expression();
         this.jj_consume_token(75);
      } while(this.jj_2_8(2));

      while(this.jj_2_9(2)) {
         this.jj_consume_token(74);
         this.jj_consume_token(75);
      }

   }

   private final boolean jj_2_1(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_1();
      this.jj_save(0, var1);
      return var2;
   }

   private final boolean jj_2_2(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_2();
      this.jj_save(1, var1);
      return var2;
   }

   private final boolean jj_2_3(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_3();
      this.jj_save(2, var1);
      return var2;
   }

   private final boolean jj_2_4(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_4();
      this.jj_save(3, var1);
      return var2;
   }

   private final boolean jj_2_5(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_5();
      this.jj_save(4, var1);
      return var2;
   }

   private final boolean jj_2_6(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_6();
      this.jj_save(5, var1);
      return var2;
   }

   private final boolean jj_2_7(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_7();
      this.jj_save(6, var1);
      return var2;
   }

   private final boolean jj_2_8(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_8();
      this.jj_save(7, var1);
      return var2;
   }

   private final boolean jj_2_9(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_9();
      this.jj_save(8, var1);
      return var2;
   }

   private final boolean jj_3R_154() {
      if (this.jj_scan_token(92)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_151() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_154()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_155()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_148() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3_6()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_150()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3_6() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_23()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_152()) {
               this.jj_scanpos = var1;
               if (this.jj_scan_token(71)) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }

               if (this.jj_3R_115()) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }

               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_25() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_50()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_51()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_50() {
      if (this.jj_3R_67()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_5() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_24()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(74)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_149() {
      if (this.jj_3R_20()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         Token var1 = this.jj_scanpos;
         if (this.jj_3R_151()) {
            this.jj_scanpos = var1;
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }

         return false;
      }
   }

   private final boolean jj_3R_41() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_24()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(71)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         Token var1 = this.jj_scanpos;
         if (this.jj_3R_59()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_60()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_61()) {
                  this.jj_scanpos = var1;
                  if (this.jj_3R_62()) {
                     this.jj_scanpos = var1;
                     if (this.jj_3R_63()) {
                        this.jj_scanpos = var1;
                        if (this.jj_3R_64()) {
                           this.jj_scanpos = var1;
                           if (this.jj_3R_65()) {
                              this.jj_scanpos = var1;
                              if (this.jj_3R_66()) {
                                 return true;
                              }

                              if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                                 return false;
                              }
                           } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                              return false;
                           }
                        } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                           return false;
                        }
                     } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                        return false;
                     }
                  } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                     return false;
                  }
               } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }

         return false;
      }
   }

   private final boolean jj_3R_40() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_24()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_123() {
      if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_1() {
      if (this.jj_scan_token(78)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(67)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_4() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_23()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_22() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3_4()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_40()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_41()) {
               return true;
            }

            if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3_3() {
      if (this.jj_3R_22()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_24() {
      if (this.jj_scan_token(67)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3_1()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_147() {
      if (this.jj_scan_token(82)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_142() {
      if (this.jj_3R_149()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_122() {
      if (this.jj_3R_24()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_49() {
      if (this.jj_scan_token(21)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_141() {
      if (this.jj_3R_148()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_48() {
      if (this.jj_scan_token(27)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_146() {
      if (this.jj_scan_token(83)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_47() {
      if (this.jj_scan_token(36)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_140() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_146()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_147()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_115()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_136() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_140()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_141()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_142()) {
               return true;
            }

            if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_46() {
      if (this.jj_scan_token(34)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_145() {
      if (this.jj_scan_token(101)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_45() {
      if (this.jj_scan_token(45)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_44() {
      if (this.jj_scan_token(12)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_135() {
      if (this.jj_scan_token(93)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_20()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_43() {
      if (this.jj_scan_token(15)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_23() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_42()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_43()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_44()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_45()) {
                  this.jj_scanpos = var1;
                  if (this.jj_3R_46()) {
                     this.jj_scanpos = var1;
                     if (this.jj_3R_47()) {
                        this.jj_scanpos = var1;
                        if (this.jj_3R_48()) {
                           this.jj_scanpos = var1;
                           if (this.jj_3R_49()) {
                              return true;
                           }

                           if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                              return false;
                           }
                        } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                           return false;
                        }
                     } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                        return false;
                     }
                  } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                     return false;
                  }
               } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_42() {
      if (this.jj_scan_token(10)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_9() {
      if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_121() {
      if (this.jj_3R_23()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_144() {
      if (this.jj_scan_token(97)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_134() {
      if (this.jj_scan_token(92)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_20()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_114() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_121()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_122()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      do {
         var1 = this.jj_scanpos;
         if (this.jj_3R_123()) {
            this.jj_scanpos = var1;
            return false;
         }
      } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

      return false;
   }

   private final boolean jj_3R_120() {
      if (this.jj_scan_token(88)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_133() {
      if (this.jj_scan_token(95)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_127() {
      if (this.jj_3R_136()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_126() {
      if (this.jj_3R_135()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_139() {
      if (this.jj_scan_token(95)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_125() {
      if (this.jj_3R_134()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_132() {
      if (this.jj_scan_token(94)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_143() {
      if (this.jj_scan_token(96)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_124() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_132()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_133()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_115()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_115() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_124()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_125()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_126()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_127()) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_137() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_143()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_144()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_145()) {
               return true;
            }

            if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_115()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_131() {
      if (this.jj_scan_token(104)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_119() {
      if (this.jj_scan_token(87)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_138() {
      if (this.jj_scan_token(94)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_112() {
      if (this.jj_3R_115()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_137()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_88() {
      if (this.jj_3R_86()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_130() {
      if (this.jj_scan_token(103)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_128() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_138()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_139()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_112()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_87() {
      if (this.jj_3R_82()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_118() {
      if (this.jj_scan_token(80)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_129() {
      if (this.jj_scan_token(102)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_116() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_129()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_130()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_131()) {
               return true;
            }

            if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_108()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_108() {
      if (this.jj_3R_112()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_128()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3_8() {
      if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_25()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_86() {
      if (this.jj_3_8()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3_8()) {
               this.jj_scanpos = var1;

               do {
                  var1 = this.jj_scanpos;
                  if (this.jj_3_9()) {
                     this.jj_scanpos = var1;
                     return false;
                  }
               } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_117() {
      if (this.jj_scan_token(81)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_106() {
      if (this.jj_3R_108()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_116()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_113() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_117()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_118()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_119()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_120()) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_106()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_111() {
      if (this.jj_scan_token(89)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_109() {
      if (this.jj_scan_token(33)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_114()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_104() {
      if (this.jj_3R_106()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_113()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_81() {
      if (this.jj_scan_token(38)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_24()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         Token var1 = this.jj_scanpos;
         if (this.jj_3R_87()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_88()) {
               return true;
            }

            if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }

         return false;
      }
   }

   private final boolean jj_3_7() {
      if (this.jj_scan_token(38)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_23()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_86()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_70() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3_7()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_81()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_97() {
      if (this.jj_scan_token(77)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_25()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_39() {
      if (this.jj_scan_token(110)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_110() {
      if (this.jj_scan_token(86)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_102() {
      if (this.jj_3R_104()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         Token var1 = this.jj_scanpos;
         if (this.jj_3R_109()) {
            this.jj_scanpos = var1;
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }

         return false;
      }
   }

   private final boolean jj_3R_107() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_110()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_111()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      if (this.jj_3R_102()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_94() {
      if (this.jj_3R_25()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_97()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_89() {
      if (this.jj_3R_94()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_38() {
      if (this.jj_scan_token(111)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_82() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         Token var1 = this.jj_scanpos;
         if (this.jj_3R_89()) {
            this.jj_scanpos = var1;
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }

         if (this.jj_scan_token(71)) {
            return true;
         } else {
            return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
         }
      }
   }

   private final boolean jj_3R_105() {
      if (this.jj_scan_token(98)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_100()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_100() {
      if (this.jj_3R_102()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_107()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_37() {
      if (this.jj_scan_token(109)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_85() {
      if (this.jj_scan_token(39)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_103() {
      if (this.jj_scan_token(100)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_98()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_98() {
      if (this.jj_3R_100()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_105()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_92() {
      if (this.jj_scan_token(24)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_36() {
      if (this.jj_scan_token(115)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_91() {
      if (this.jj_scan_token(54)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_84() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_91()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_92()) {
            return true;
         }

         if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_101() {
      if (this.jj_scan_token(99)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_95()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_95() {
      if (this.jj_3R_98()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_103()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_35() {
      if (this.jj_scan_token(114)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_80() {
      if (this.jj_3R_85()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_66() {
      if (this.jj_3R_69()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_79() {
      if (this.jj_3R_84()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_78() {
      if (this.jj_scan_token(66)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_99() {
      if (this.jj_scan_token(91)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_90()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_90() {
      if (this.jj_3R_95()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_101()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_34() {
      if (this.jj_scan_token(113)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_65() {
      if (this.jj_scan_token(38)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_77() {
      if (this.jj_scan_token(65)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_76() {
      if (this.jj_scan_token(63)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_33() {
      if (this.jj_scan_token(106)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_69() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_75()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_76()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_77()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_78()) {
                  this.jj_scanpos = var1;
                  if (this.jj_3R_79()) {
                     this.jj_scanpos = var1;
                     if (this.jj_3R_80()) {
                        return true;
                     }

                     if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                        return false;
                     }
                  } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                     return false;
                  }
               } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_75() {
      if (this.jj_scan_token(59)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_96() {
      if (this.jj_scan_token(90)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_83()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_83() {
      if (this.jj_3R_90()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_99()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_64() {
      if (this.jj_scan_token(47)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_32() {
      if (this.jj_scan_token(105)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_73() {
      if (this.jj_3R_82()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_72() {
      if (this.jj_scan_token(78)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(67)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_74() {
      if (this.jj_3R_83()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_96()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_63() {
      if (this.jj_scan_token(50)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_31() {
      if (this.jj_scan_token(112)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_58() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_71()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_72()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_73()) {
               return true;
            }

            if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_71() {
      if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_25()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_93() {
      if (this.jj_scan_token(84)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_25()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(85)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_68()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_57() {
      if (this.jj_3R_70()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_30() {
      if (this.jj_scan_token(108)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_27() {
      if (this.jj_3R_58()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_56() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_25()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(71)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_152() {
      if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_55() {
      if (this.jj_scan_token(47)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(78)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(67)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_29() {
      if (this.jj_scan_token(107)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_68() {
      if (this.jj_3R_74()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         Token var1 = this.jj_scanpos;
         if (this.jj_3R_93()) {
            this.jj_scanpos = var1;
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }

         return false;
      }
   }

   private final boolean jj_3R_54() {
      if (this.jj_scan_token(50)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_62() {
      if (this.jj_scan_token(67)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_53() {
      if (this.jj_3R_24()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_153() {
      if (this.jj_scan_token(74)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(75)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_26() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_52()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_53()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_54()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_55()) {
                  this.jj_scanpos = var1;
                  if (this.jj_3R_56()) {
                     this.jj_scanpos = var1;
                     if (this.jj_3R_57()) {
                        return true;
                     }

                     if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                        return false;
                     }
                  } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                     return false;
                  }
               } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_52() {
      if (this.jj_3R_69()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_21() {
      Token var1 = this.jj_scanpos;
      if (this.jj_3R_28()) {
         this.jj_scanpos = var1;
         if (this.jj_3R_29()) {
            this.jj_scanpos = var1;
            if (this.jj_3R_30()) {
               this.jj_scanpos = var1;
               if (this.jj_3R_31()) {
                  this.jj_scanpos = var1;
                  if (this.jj_3R_32()) {
                     this.jj_scanpos = var1;
                     if (this.jj_3R_33()) {
                        this.jj_scanpos = var1;
                        if (this.jj_3R_34()) {
                           this.jj_scanpos = var1;
                           if (this.jj_3R_35()) {
                              this.jj_scanpos = var1;
                              if (this.jj_3R_36()) {
                                 this.jj_scanpos = var1;
                                 if (this.jj_3R_37()) {
                                    this.jj_scanpos = var1;
                                    if (this.jj_3R_38()) {
                                       this.jj_scanpos = var1;
                                       if (this.jj_3R_39()) {
                                          return true;
                                       }

                                       if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                                          return false;
                                       }
                                    } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                                       return false;
                                    }
                                 } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                                    return false;
                                 }
                              } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                                 return false;
                              }
                           } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                              return false;
                           }
                        } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                           return false;
                        }
                     } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                        return false;
                     }
                  } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                     return false;
                  }
               } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }
            } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
               return false;
            }
         } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
         }
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      }

      return false;
   }

   private final boolean jj_3R_28() {
      if (this.jj_scan_token(79)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_61() {
      if (this.jj_scan_token(70)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_2() {
      if (this.jj_3R_20()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_21()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_20() {
      if (this.jj_3R_26()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_27()) {
               this.jj_scanpos = var1;
               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_60() {
      if (this.jj_scan_token(82)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_155() {
      if (this.jj_scan_token(93)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_67() {
      if (this.jj_3R_20()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_21()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_25()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_150() {
      if (this.jj_scan_token(70)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_3R_24()) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_153()) {
               this.jj_scanpos = var1;
               if (this.jj_scan_token(71)) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }

               if (this.jj_3R_136()) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }

               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_59() {
      if (this.jj_scan_token(83)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3R_51() {
      if (this.jj_3R_68()) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   public ExpressionParser(InputStream var1) {
      this.jj_input_stream = new ASCII_UCodeESC_CharStream(var1, 1, 1);
      this.token_source = new ExpressionParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 44; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new JJExpressionParserCalls();
      }

   }

   public void ReInit(InputStream var1) {
      this.jj_input_stream.ReInit(var1, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 44; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new JJExpressionParserCalls();
      }

   }

   public ExpressionParser(ExpressionParserTokenManager var1) {
      this.token_source = var1;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 44; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new JJExpressionParserCalls();
      }

   }

   public void ReInit(ExpressionParserTokenManager var1) {
      this.token_source = var1;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 44; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new JJExpressionParserCalls();
      }

   }

   private final Token jj_consume_token(int var1) throws ParseException {
      Token var2;
      if ((var2 = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind != var1) {
         this.token = var2;
         this.jj_kind = var1;
         throw this.generateParseException();
      } else {
         ++this.jj_gen;
         if (++this.jj_gc > 100) {
            this.jj_gc = 0;
            JJExpressionParserCalls[] var3 = this.jj_2_rtns;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               JJExpressionParserCalls var6 = var3[var5];

               for(JJExpressionParserCalls var7 = var6; var7 != null; var7 = var7.next) {
                  if (var7.gen < this.jj_gen) {
                     var7.first = null;
                  }
               }
            }
         }

         return this.token;
      }
   }

   private final boolean jj_scan_token(int var1) {
      if (this.jj_scanpos == this.jj_lastpos) {
         --this.jj_la;
         if (this.jj_scanpos.next == null) {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
         } else {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
         }
      } else {
         this.jj_scanpos = this.jj_scanpos.next;
      }

      if (this.jj_rescan) {
         int var2 = 0;

         Token var3;
         for(var3 = this.token; var3 != null && var3 != this.jj_scanpos; var3 = var3.next) {
            ++var2;
         }

         if (var3 != null) {
            this.jj_add_error_token(var1, var2);
         }
      }

      return this.jj_scanpos.kind != var1;
   }

   public final Token getNextToken() {
      if (this.token.next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      ++this.jj_gen;
      return this.token;
   }

   public final Token getToken(int var1) {
      Token var2 = this.lookingAhead ? this.jj_scanpos : this.token;

      for(int var3 = 0; var3 < var1; ++var3) {
         if (var2.next != null) {
            var2 = var2.next;
         } else {
            var2 = var2.next = this.token_source.getNextToken();
         }
      }

      return var2;
   }

   private final int jj_ntk() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   private void jj_add_error_token(int var1, int var2) {
      if (var2 < 100) {
         if (var2 == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = var1;
         } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];

            for(int var3 = 0; var3 < this.jj_endpos; ++var3) {
               this.jj_expentry[var3] = this.jj_lasttokens[var3];
            }

            boolean var7 = false;
            Enumeration var4 = this.jj_expentries.elements();

            label48:
            do {
               int[] var5;
               do {
                  if (!var4.hasMoreElements()) {
                     break label48;
                  }

                  var5 = (int[])var4.nextElement();
               } while(var5.length != this.jj_expentry.length);

               var7 = true;

               for(int var6 = 0; var6 < this.jj_expentry.length; ++var6) {
                  if (var5[var6] != this.jj_expentry[var6]) {
                     var7 = false;
                     break;
                  }
               }
            } while(!var7);

            if (!var7) {
               this.jj_expentries.addElement(this.jj_expentry);
            }

            if (var2 != 0) {
               this.jj_lasttokens[(this.jj_endpos = var2) - 1] = var1;
            }
         }

      }
   }

   public final ParseException generateParseException() {
      this.jj_expentries.removeAllElements();
      boolean[] var1 = new boolean[116];

      int var2;
      for(var2 = 0; var2 < 116; ++var2) {
         var1[var2] = false;
      }

      if (this.jj_kind >= 0) {
         var1[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int var3;
      for(var2 = 0; var2 < 44; ++var2) {
         if (this.jj_la1[var2] == this.jj_gen) {
            for(var3 = 0; var3 < 32; ++var3) {
               if ((this.jj_la1_0[var2] & 1 << var3) != 0) {
                  var1[var3] = true;
               }

               if ((this.jj_la1_1[var2] & 1 << var3) != 0) {
                  var1[32 + var3] = true;
               }

               if ((this.jj_la1_2[var2] & 1 << var3) != 0) {
                  var1[64 + var3] = true;
               }

               if ((this.jj_la1_3[var2] & 1 << var3) != 0) {
                  var1[96 + var3] = true;
               }
            }
         }
      }

      for(var2 = 0; var2 < 116; ++var2) {
         if (var1[var2]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = var2;
            this.jj_expentries.addElement(this.jj_expentry);
         }
      }

      this.jj_endpos = 0;
      this.jj_rescan_token();
      this.jj_add_error_token(0, 0);
      int[][] var4 = new int[this.jj_expentries.size()][];

      for(var3 = 0; var3 < this.jj_expentries.size(); ++var3) {
         var4[var3] = (int[])this.jj_expentries.elementAt(var3);
      }

      return new ParseException(this.token, var4, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   private final void jj_rescan_token() {
      this.jj_rescan = true;

      for(int var1 = 0; var1 < 9; ++var1) {
         JJExpressionParserCalls var2 = this.jj_2_rtns[var1];

         do {
            if (var2.gen > this.jj_gen) {
               this.jj_la = var2.arg;
               this.jj_lastpos = this.jj_scanpos = var2.first;
               switch (var1) {
                  case 0:
                     this.jj_3_1();
                     break;
                  case 1:
                     this.jj_3_2();
                     break;
                  case 2:
                     this.jj_3_3();
                     break;
                  case 3:
                     this.jj_3_4();
                     break;
                  case 4:
                     this.jj_3_5();
                     break;
                  case 5:
                     this.jj_3_6();
                     break;
                  case 6:
                     this.jj_3_7();
                     break;
                  case 7:
                     this.jj_3_8();
                     break;
                  case 8:
                     this.jj_3_9();
               }
            }

            var2 = var2.next;
         } while(var2 != null);
      }

      this.jj_rescan = false;
   }

   private final void jj_save(int var1, int var2) {
      JJExpressionParserCalls var3;
      for(var3 = this.jj_2_rtns[var1]; var3.gen > this.jj_gen; var3 = var3.next) {
         if (var3.next == null) {
            var3 = var3.next = new JJExpressionParserCalls();
            break;
         }
      }

      var3.gen = this.jj_gen + var2 - this.jj_la;
      var3.first = this.token;
      var3.arg = var2;
   }

   public interface GetFrame {
      StackFrame get() throws IncompatibleThreadStateException;
   }
}
