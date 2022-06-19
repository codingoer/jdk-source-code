package sun.tools.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import sun.tools.tree.AddExpression;
import sun.tools.tree.AndExpression;
import sun.tools.tree.ArrayAccessExpression;
import sun.tools.tree.ArrayExpression;
import sun.tools.tree.AssignAddExpression;
import sun.tools.tree.AssignBitAndExpression;
import sun.tools.tree.AssignBitOrExpression;
import sun.tools.tree.AssignBitXorExpression;
import sun.tools.tree.AssignDivideExpression;
import sun.tools.tree.AssignExpression;
import sun.tools.tree.AssignMultiplyExpression;
import sun.tools.tree.AssignOpExpression;
import sun.tools.tree.AssignRemainderExpression;
import sun.tools.tree.AssignShiftLeftExpression;
import sun.tools.tree.AssignShiftRightExpression;
import sun.tools.tree.AssignSubtractExpression;
import sun.tools.tree.AssignUnsignedShiftRightExpression;
import sun.tools.tree.BitAndExpression;
import sun.tools.tree.BitNotExpression;
import sun.tools.tree.BitOrExpression;
import sun.tools.tree.BitXorExpression;
import sun.tools.tree.BooleanExpression;
import sun.tools.tree.BreakStatement;
import sun.tools.tree.CaseStatement;
import sun.tools.tree.CastExpression;
import sun.tools.tree.CatchStatement;
import sun.tools.tree.CharExpression;
import sun.tools.tree.CommaExpression;
import sun.tools.tree.CompoundStatement;
import sun.tools.tree.ConditionalExpression;
import sun.tools.tree.ContinueStatement;
import sun.tools.tree.DeclarationStatement;
import sun.tools.tree.DivideExpression;
import sun.tools.tree.DoStatement;
import sun.tools.tree.DoubleExpression;
import sun.tools.tree.EqualExpression;
import sun.tools.tree.ExprExpression;
import sun.tools.tree.Expression;
import sun.tools.tree.ExpressionStatement;
import sun.tools.tree.FieldExpression;
import sun.tools.tree.FinallyStatement;
import sun.tools.tree.FloatExpression;
import sun.tools.tree.ForStatement;
import sun.tools.tree.GreaterExpression;
import sun.tools.tree.GreaterOrEqualExpression;
import sun.tools.tree.IdentifierExpression;
import sun.tools.tree.IfStatement;
import sun.tools.tree.InstanceOfExpression;
import sun.tools.tree.IntExpression;
import sun.tools.tree.LessExpression;
import sun.tools.tree.LessOrEqualExpression;
import sun.tools.tree.LocalMember;
import sun.tools.tree.LongExpression;
import sun.tools.tree.MethodExpression;
import sun.tools.tree.MultiplyExpression;
import sun.tools.tree.NegativeExpression;
import sun.tools.tree.NewArrayExpression;
import sun.tools.tree.NewInstanceExpression;
import sun.tools.tree.Node;
import sun.tools.tree.NotEqualExpression;
import sun.tools.tree.NotExpression;
import sun.tools.tree.NullExpression;
import sun.tools.tree.OrExpression;
import sun.tools.tree.PositiveExpression;
import sun.tools.tree.PostDecExpression;
import sun.tools.tree.PostIncExpression;
import sun.tools.tree.PreDecExpression;
import sun.tools.tree.PreIncExpression;
import sun.tools.tree.RemainderExpression;
import sun.tools.tree.ReturnStatement;
import sun.tools.tree.ShiftLeftExpression;
import sun.tools.tree.ShiftRightExpression;
import sun.tools.tree.Statement;
import sun.tools.tree.StringExpression;
import sun.tools.tree.SubtractExpression;
import sun.tools.tree.SuperExpression;
import sun.tools.tree.SwitchStatement;
import sun.tools.tree.SynchronizedStatement;
import sun.tools.tree.ThisExpression;
import sun.tools.tree.ThrowStatement;
import sun.tools.tree.TryStatement;
import sun.tools.tree.TypeExpression;
import sun.tools.tree.UnsignedShiftRightExpression;
import sun.tools.tree.VarDeclarationStatement;
import sun.tools.tree.WhileStatement;

public class Parser extends Scanner implements ParserActions, Constants {
   ParserActions actions;
   private Node[] args;
   protected int argIndex;
   private int aCount;
   private Type[] aTypes;
   private IdentifierToken[] aNames;
   private ClassDefinition curClass;
   private int FPstate;
   protected Scanner scanner;

   protected Parser(Environment var1, InputStream var2) throws IOException {
      super(var1, var2);
      this.args = new Node[32];
      this.argIndex = 0;
      this.aCount = 0;
      this.aTypes = new Type[8];
      this.aNames = new IdentifierToken[this.aTypes.length];
      this.FPstate = 0;
      this.scanner = this;
      this.actions = this;
   }

   protected Parser(Scanner var1) throws IOException {
      super(var1.env);
      this.args = new Node[32];
      this.argIndex = 0;
      this.aCount = 0;
      this.aTypes = new Type[8];
      this.aNames = new IdentifierToken[this.aTypes.length];
      this.FPstate = 0;
      this.scanner = var1;
      super.env = var1.env;
      super.token = var1.token;
      super.pos = var1.pos;
      this.actions = this;
   }

   public Parser(Scanner var1, ParserActions var2) throws IOException {
      this(var1);
      this.actions = var2;
   }

   /** @deprecated */
   @Deprecated
   public void packageDeclaration(long var1, IdentifierToken var3) {
      this.packageDeclaration(var1, var3.id);
   }

   /** @deprecated */
   @Deprecated
   protected void packageDeclaration(long var1, Identifier var3) {
      throw new RuntimeException("beginClass method is abstract");
   }

   /** @deprecated */
   @Deprecated
   public void importClass(long var1, IdentifierToken var3) {
      this.importClass(var1, var3.id);
   }

   /** @deprecated */
   @Deprecated
   protected void importClass(long var1, Identifier var3) {
      throw new RuntimeException("importClass method is abstract");
   }

   /** @deprecated */
   @Deprecated
   public void importPackage(long var1, IdentifierToken var3) {
      this.importPackage(var1, var3.id);
   }

   /** @deprecated */
   @Deprecated
   protected void importPackage(long var1, Identifier var3) {
      throw new RuntimeException("importPackage method is abstract");
   }

   /** @deprecated */
   @Deprecated
   public ClassDefinition beginClass(long var1, String var3, int var4, IdentifierToken var5, IdentifierToken var6, IdentifierToken[] var7) {
      Identifier var8 = var6 == null ? null : var6.id;
      Identifier[] var9 = null;
      if (var7 != null) {
         var9 = new Identifier[var7.length];

         for(int var10 = 0; var10 < var7.length; ++var10) {
            var9[var10] = var7[var10].id;
         }
      }

      this.beginClass(var1, var3, var4, var5.id, var8, var9);
      return this.getCurrentClass();
   }

   /** @deprecated */
   @Deprecated
   protected void beginClass(long var1, String var3, int var4, Identifier var5, Identifier var6, Identifier[] var7) {
      throw new RuntimeException("beginClass method is abstract");
   }

   protected ClassDefinition getCurrentClass() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public void endClass(long var1, ClassDefinition var3) {
      this.endClass(var1, var3.getName().getFlatName().getName());
   }

   /** @deprecated */
   @Deprecated
   protected void endClass(long var1, Identifier var3) {
      throw new RuntimeException("endClass method is abstract");
   }

   /** @deprecated */
   @Deprecated
   public void defineField(long var1, ClassDefinition var3, String var4, int var5, Type var6, IdentifierToken var7, IdentifierToken[] var8, IdentifierToken[] var9, Node var10) {
      Identifier[] var11 = null;
      Identifier[] var12 = null;
      int var13;
      if (var8 != null) {
         var11 = new Identifier[var8.length];

         for(var13 = 0; var13 < var8.length; ++var13) {
            var11[var13] = var8[var13].id;
         }
      }

      if (var9 != null) {
         var12 = new Identifier[var9.length];

         for(var13 = 0; var13 < var9.length; ++var13) {
            var12[var13] = var9[var13].id;
         }
      }

      this.defineField(var1, var4, var5, var6, var7.id, var11, var12, var10);
   }

   /** @deprecated */
   @Deprecated
   protected void defineField(long var1, String var3, int var4, Type var5, Identifier var6, Identifier[] var7, Identifier[] var8, Node var9) {
      throw new RuntimeException("defineField method is abstract");
   }

   protected final void addArgument(Node var1) {
      if (this.argIndex == this.args.length) {
         Node[] var2 = new Node[this.args.length * 2];
         System.arraycopy(this.args, 0, var2, 0, this.args.length);
         this.args = var2;
      }

      this.args[this.argIndex++] = var1;
   }

   protected final Expression[] exprArgs(int var1) {
      Expression[] var2 = new Expression[this.argIndex - var1];
      System.arraycopy(this.args, var1, var2, 0, this.argIndex - var1);
      this.argIndex = var1;
      return var2;
   }

   protected final Statement[] statArgs(int var1) {
      Statement[] var2 = new Statement[this.argIndex - var1];
      System.arraycopy(this.args, var1, var2, 0, this.argIndex - var1);
      this.argIndex = var1;
      return var2;
   }

   protected void expect(int var1) throws SyntaxError, IOException {
      if (this.token != var1) {
         switch (var1) {
            case 60:
               this.env.error(this.scanner.prevPos, "identifier.expected");
               break;
            default:
               this.env.error(this.scanner.prevPos, "token.expected", opNames[var1]);
         }

         throw new SyntaxError();
      } else {
         this.scan();
      }
   }

   protected Expression parseTypeExpression() throws SyntaxError, IOException {
      switch (this.token) {
         case 60:
            Object var1 = new IdentifierExpression(this.pos, this.scanner.idValue);
            this.scan();

            while(this.token == 46) {
               var1 = new FieldExpression(this.scan(), (Expression)var1, this.scanner.idValue);
               this.expect(60);
            }

            return (Expression)var1;
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         default:
            this.env.error(this.pos, "type.expected");
            throw new SyntaxError();
         case 70:
            return new TypeExpression(this.scan(), Type.tByte);
         case 71:
            return new TypeExpression(this.scan(), Type.tChar);
         case 72:
            return new TypeExpression(this.scan(), Type.tShort);
         case 73:
            return new TypeExpression(this.scan(), Type.tInt);
         case 74:
            return new TypeExpression(this.scan(), Type.tLong);
         case 75:
            return new TypeExpression(this.scan(), Type.tFloat);
         case 76:
            return new TypeExpression(this.scan(), Type.tDouble);
         case 77:
            return new TypeExpression(this.scan(), Type.tVoid);
         case 78:
            return new TypeExpression(this.scan(), Type.tBoolean);
      }
   }

   protected Expression parseMethodExpression(Expression var1, Identifier var2) throws SyntaxError, IOException {
      long var3 = this.scan();
      int var5 = this.argIndex;
      if (this.token != 141) {
         this.addArgument(this.parseExpression());

         while(this.token == 0) {
            this.scan();
            this.addArgument(this.parseExpression());
         }
      }

      this.expect(141);
      return new MethodExpression(var3, var1, var2, this.exprArgs(var5));
   }

   protected Expression parseNewInstanceExpression(long var1, Expression var3, Expression var4) throws SyntaxError, IOException {
      int var5 = this.argIndex;
      this.expect(140);
      if (this.token != 141) {
         this.addArgument(this.parseExpression());

         while(this.token == 0) {
            this.scan();
            this.addArgument(this.parseExpression());
         }
      }

      this.expect(141);
      ClassDefinition var6 = null;
      if (this.token == 138 && !(var4 instanceof TypeExpression)) {
         long var7 = this.pos;
         Identifier var9 = FieldExpression.toIdentifier(var4);
         if (var9 == null) {
            this.env.error(var4.getWhere(), "type.expected");
         }

         Vector var10 = new Vector(1);
         Vector var11 = new Vector(0);
         var10.addElement(new IdentifierToken(idNull));
         if (this.token == 113 || this.token == 112) {
            this.env.error(this.pos, "anonymous.extends");
            this.parseInheritance(var10, var11);
         }

         var6 = this.parseClassBody(new IdentifierToken(var7, idNull), 196608, 56, (String)null, var10, var11, var4.getWhere());
      }

      return var3 == null && var6 == null ? new NewInstanceExpression(var1, var4, this.exprArgs(var5)) : new NewInstanceExpression(var1, var4, this.exprArgs(var5), var3, var6);
   }

   protected Expression parseTerm() throws SyntaxError, IOException {
      long var1;
      long var2;
      int var12;
      long var14;
      double var17;
      float var19;
      switch (this.token) {
         case 29:
            var1 = this.scan();
            switch (this.token) {
               case 65:
                  var12 = this.scanner.intValue;
                  long var18 = this.scan();
                  if (var12 < 0 && this.radix == 10) {
                     this.env.error(var18, "overflow.int.dec");
                  }

                  return new IntExpression(var18, var12);
               case 66:
                  var14 = this.scanner.longValue;
                  long var20 = this.scan();
                  if (var14 < 0L && this.radix == 10) {
                     this.env.error(var20, "overflow.long.dec");
                  }

                  return new LongExpression(var20, var14);
               case 67:
                  var19 = this.scanner.floatValue;
                  return new FloatExpression(this.scan(), var19);
               case 68:
                  var17 = this.scanner.doubleValue;
                  return new DoubleExpression(this.scan(), var17);
               default:
                  return new PositiveExpression(var1, this.parseTerm());
            }
         case 30:
            var1 = this.scan();
            switch (this.token) {
               case 65:
                  var12 = -this.scanner.intValue;
                  return new IntExpression(this.scan(), var12);
               case 66:
                  var14 = -this.scanner.longValue;
                  return new LongExpression(this.scan(), var14);
               case 67:
                  var19 = -this.scanner.floatValue;
                  return new FloatExpression(this.scan(), var19);
               case 68:
                  var17 = -this.scanner.doubleValue;
                  return new DoubleExpression(this.scan(), var17);
               default:
                  return new NegativeExpression(var1, this.parseTerm());
            }
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 59:
         case 61:
         case 62:
         case 64:
         case 79:
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
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 139:
         default:
            this.env.error(this.scanner.prevPos, "missing.term");
            return new IntExpression(this.pos, 0);
         case 37:
            return new NotExpression(this.scan(), this.parseTerm());
         case 38:
            return new BitNotExpression(this.scan(), this.parseTerm());
         case 49:
            var1 = this.scan();
            var12 = this.argIndex;
            Expression var4;
            if (this.token == 140) {
               this.scan();
               var4 = this.parseExpression();
               this.expect(141);
               this.env.error(var1, "not.supported", "new(...)");
               return new NullExpression(var1);
            } else {
               var4 = this.parseTypeExpression();
               if (this.token == 142) {
                  while(this.token == 142) {
                     this.scan();
                     this.addArgument(this.token != 143 ? this.parseExpression() : null);
                     this.expect(143);
                  }

                  Expression[] var5 = this.exprArgs(var12);
                  if (this.token == 138) {
                     return new NewArrayExpression(var1, var4, var5, this.parseTerm());
                  }

                  return new NewArrayExpression(var1, var4, var5);
               }

               return this.parseNewInstanceExpression(var1, (Expression)null, var4);
            }
         case 50:
            return new PreIncExpression(this.scan(), this.parseTerm());
         case 51:
            return new PreDecExpression(this.scan(), this.parseTerm());
         case 60:
            Identifier var16 = this.scanner.idValue;
            var2 = this.scan();
            return (Expression)(this.token == 140 ? this.parseMethodExpression((Expression)null, var16) : new IdentifierExpression(var2, var16));
         case 63:
            char var15 = this.scanner.charValue;
            return new CharExpression(this.scan(), var15);
         case 65:
            int var13 = this.scanner.intValue;
            var2 = this.scan();
            if (var13 < 0 && this.radix == 10) {
               this.env.error(var2, "overflow.int.dec");
            }

            return new IntExpression(var2, var13);
         case 66:
            var1 = this.scanner.longValue;
            var14 = this.scan();
            if (var1 < 0L && this.radix == 10) {
               this.env.error(var14, "overflow.long.dec");
            }

            return new LongExpression(var14, var1);
         case 67:
            float var11 = this.scanner.floatValue;
            return new FloatExpression(this.scan(), var11);
         case 68:
            double var10 = this.scanner.doubleValue;
            return new DoubleExpression(this.scan(), var10);
         case 69:
            String var9 = this.scanner.stringValue;
            return new StringExpression(this.scan(), var9);
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
            return this.parseTypeExpression();
         case 80:
            return new BooleanExpression(this.scan(), true);
         case 81:
            return new BooleanExpression(this.scan(), false);
         case 82:
            ThisExpression var8 = new ThisExpression(this.scan());
            return (Expression)(this.token == 140 ? this.parseMethodExpression(var8, idInit) : var8);
         case 83:
            SuperExpression var7 = new SuperExpression(this.scan());
            return (Expression)(this.token == 140 ? this.parseMethodExpression(var7, idInit) : var7);
         case 84:
            return new NullExpression(this.scan());
         case 138:
            var1 = this.scan();
            var12 = this.argIndex;
            if (this.token != 139) {
               this.addArgument(this.parseExpression());

               while(this.token == 0) {
                  this.scan();
                  if (this.token == 139) {
                     break;
                  }

                  this.addArgument(this.parseExpression());
               }
            }

            this.expect(139);
            return new ArrayExpression(var1, this.exprArgs(var12));
         case 140:
            var1 = this.scan();
            Expression var3 = this.parseExpression();
            this.expect(141);
            if (var3.getOp() == 147) {
               return new CastExpression(var1, var3, this.parseTerm());
            } else {
               switch (this.token) {
                  case 37:
                  case 38:
                  case 49:
                  case 60:
                  case 63:
                  case 65:
                  case 66:
                  case 67:
                  case 68:
                  case 69:
                  case 80:
                  case 81:
                  case 82:
                  case 83:
                  case 84:
                  case 140:
                     return new CastExpression(var1, var3, this.parseTerm());
                  case 50:
                     return new PostIncExpression(this.scan(), var3);
                  case 51:
                     return new PostDecExpression(this.scan(), var3);
                  default:
                     return new ExprExpression(var1, var3);
               }
            }
      }
   }

   protected Expression parseExpression() throws SyntaxError, IOException {
      Expression var2;
      for(Expression var1 = this.parseTerm(); var1 != null; var1 = var2.order()) {
         var2 = this.parseBinaryExpression(var1);
         if (var2 == null) {
            return var1;
         }
      }

      return null;
   }

   protected Expression parseBinaryExpression(Expression var1) throws SyntaxError, IOException {
      if (var1 != null) {
         long var2;
         Expression var4;
         switch (this.token) {
            case 1:
               var1 = new AssignExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 2:
               var1 = new AssignMultiplyExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 3:
               var1 = new AssignDivideExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 4:
               var1 = new AssignRemainderExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 5:
               var1 = new AssignAddExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 6:
               var1 = new AssignSubtractExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 7:
               var1 = new AssignShiftLeftExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 8:
               var1 = new AssignShiftRightExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 9:
               var1 = new AssignUnsignedShiftRightExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 10:
               var1 = new AssignBitAndExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 11:
               var1 = new AssignBitOrExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 12:
               var1 = new AssignBitXorExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 13:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 47:
            case 48:
            case 49:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
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
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 138:
            case 139:
            case 140:
            case 141:
            default:
               return null;
            case 14:
               var1 = new OrExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 15:
               var1 = new AndExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 16:
               var1 = new BitOrExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 17:
               var1 = new BitXorExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 18:
               var1 = new BitAndExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 19:
               var1 = new NotEqualExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 20:
               var1 = new EqualExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 21:
               var1 = new GreaterOrEqualExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 22:
               var1 = new GreaterExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 23:
               var1 = new LessOrEqualExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 24:
               var1 = new LessExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 25:
               var1 = new InstanceOfExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 26:
               var1 = new ShiftLeftExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 27:
               var1 = new ShiftRightExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 28:
               var1 = new UnsignedShiftRightExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 29:
               var1 = new AddExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 30:
               var1 = new SubtractExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 31:
               var1 = new DivideExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 32:
               var1 = new RemainderExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 33:
               var1 = new MultiplyExpression(this.scan(), (Expression)var1, this.parseTerm());
               break;
            case 46:
               var2 = this.scan();
               long var8;
               if (this.token == 82) {
                  var8 = this.scan();
                  if (this.token == 140) {
                     ThisExpression var6 = new ThisExpression(var8, (Expression)var1);
                     var1 = this.parseMethodExpression(var6, idInit);
                  } else {
                     var1 = new FieldExpression(var2, (Expression)var1, idThis);
                  }
               } else if (this.token == 83) {
                  var8 = this.scan();
                  if (this.token == 140) {
                     SuperExpression var7 = new SuperExpression(var8, (Expression)var1);
                     var1 = this.parseMethodExpression(var7, idInit);
                  } else {
                     var1 = new FieldExpression(var2, (Expression)var1, idSuper);
                  }
               } else if (this.token == 49) {
                  this.scan();
                  if (this.token != 60) {
                     this.expect(60);
                  }

                  var1 = this.parseNewInstanceExpression(var2, (Expression)var1, this.parseTypeExpression());
               } else if (this.token == 111) {
                  this.scan();
                  var1 = new FieldExpression(var2, (Expression)var1, idClass);
               } else {
                  Identifier var9 = this.scanner.idValue;
                  this.expect(60);
                  if (this.token == 140) {
                     var1 = this.parseMethodExpression((Expression)var1, var9);
                  } else {
                     var1 = new FieldExpression(var2, (Expression)var1, var9);
                  }
               }
               break;
            case 50:
               var1 = new PostIncExpression(this.scan(), (Expression)var1);
               break;
            case 51:
               var1 = new PostDecExpression(this.scan(), (Expression)var1);
               break;
            case 137:
               var2 = this.scan();
               var4 = this.parseExpression();
               this.expect(136);
               Expression var5 = this.parseExpression();
               if (var5 instanceof AssignExpression || var5 instanceof AssignOpExpression) {
                  this.env.error(var5.getWhere(), "assign.in.conditionalexpr");
               }

               var1 = new ConditionalExpression(var2, (Expression)var1, var4, var5);
               break;
            case 142:
               var2 = this.scan();
               var4 = this.token != 143 ? this.parseExpression() : null;
               this.expect(143);
               var1 = new ArrayAccessExpression(var2, (Expression)var1, var4);
         }
      }

      return (Expression)var1;
   }

   protected boolean recoverStatement() throws SyntaxError, IOException {
      while(true) {
         switch (this.token) {
            case -1:
            case 90:
            case 92:
            case 93:
            case 94:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 138:
            case 139:
               return true;
            case 77:
            case 111:
            case 114:
            case 120:
            case 121:
            case 124:
            case 125:
            case 126:
               this.expect(139);
               return false;
            case 140:
               this.match(140, 141);
               this.scan();
               break;
            case 142:
               this.match(142, 143);
               this.scan();
               break;
            default:
               this.scan();
         }
      }
   }

   protected Statement parseDeclaration(long var1, int var3, Expression var4) throws SyntaxError, IOException {
      int var5 = this.argIndex;
      if (this.token == 60) {
         this.addArgument(new VarDeclarationStatement(this.pos, this.parseExpression()));

         while(this.token == 0) {
            this.scan();
            this.addArgument(new VarDeclarationStatement(this.pos, this.parseExpression()));
         }
      }

      return new DeclarationStatement(var1, var3, var4, this.statArgs(var5));
   }

   protected void topLevelExpression(Expression var1) {
      switch (var1.getOp()) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 39:
         case 40:
         case 42:
         case 44:
         case 45:
         case 47:
            return;
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
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
         case 38:
         case 41:
         case 43:
         case 46:
         default:
            this.env.error(var1.getWhere(), "invalid.expr");
      }
   }

   protected Statement parseStatement() throws SyntaxError, IOException {
      long var1;
      Expression var3;
      Expression var4;
      int var15;
      Identifier var18;
      Statement var20;
      switch (this.token) {
         case 58:
            var1 = this.scan();
            this.expect(60);
            this.expect(135);
            this.env.error(var1, "not.supported", "goto");
            return new CompoundStatement(var1, new Statement[0]);
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 78:
         case 79:
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
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 112:
         case 113:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 122:
         case 127:
         case 129:
         case 132:
         case 133:
         case 134:
         case 136:
         case 137:
         default:
            var1 = this.pos;
            var3 = this.parseExpression();
            if (this.token == 60) {
               var20 = this.parseDeclaration(var1, 0, var3);
               this.expect(135);
               return var20;
            } else {
               if (this.token == 136) {
                  this.scan();
                  var20 = this.parseStatement();
                  var20.setLabel(this.env, var3);
                  return var20;
               }

               this.topLevelExpression(var3);
               this.expect(135);
               return new ExpressionStatement(var1, var3);
            }
         case 77:
         case 120:
         case 121:
         case 124:
         case 125:
            this.env.error(this.pos, "statement.expected");
            throw new SyntaxError();
         case 90:
            var1 = this.scan();
            this.expect(140);
            var3 = this.parseExpression();
            this.expect(141);
            var20 = this.parseStatement();
            if (this.token == 91) {
               this.scan();
               return new IfStatement(var1, var3, var20, this.parseStatement());
            }

            return new IfStatement(var1, var3, var20, (Statement)null);
         case 91:
            this.env.error(this.scan(), "else.without.if");
            return this.parseStatement();
         case 92:
            var1 = this.scan();
            Object var25 = null;
            var4 = null;
            Object var21 = null;
            this.expect(140);
            long var22;
            if (this.token != 135) {
               var22 = this.pos;
               int var8 = this.parseModifiers(16);
               Object var26 = this.parseExpression();
               if (this.token == 60) {
                  var25 = this.parseDeclaration(var22, var8, (Expression)var26);
               } else {
                  if (var8 != 0) {
                     this.expect(60);
                  }

                  this.topLevelExpression((Expression)var26);

                  while(this.token == 0) {
                     long var27 = this.scan();
                     Expression var12 = this.parseExpression();
                     this.topLevelExpression(var12);
                     var26 = new CommaExpression(var27, (Expression)var26, var12);
                  }

                  var25 = new ExpressionStatement(var22, (Expression)var26);
               }
            }

            this.expect(135);
            if (this.token != 135) {
               var4 = this.parseExpression();
            }

            this.expect(135);
            if (this.token != 141) {
               var21 = this.parseExpression();
               this.topLevelExpression((Expression)var21);

               while(this.token == 0) {
                  var22 = this.scan();
                  Expression var24 = this.parseExpression();
                  this.topLevelExpression(var24);
                  var21 = new CommaExpression(var22, (Expression)var21, var24);
               }
            }

            this.expect(141);
            return new ForStatement(var1, (Statement)var25, var4, (Expression)var21, this.parseStatement());
         case 93:
            var1 = this.scan();
            this.expect(140);
            var3 = this.parseExpression();
            this.expect(141);
            return new WhileStatement(var1, var3, this.parseStatement());
         case 94:
            var1 = this.scan();
            Statement var23 = this.parseStatement();
            this.expect(93);
            this.expect(140);
            var4 = this.parseExpression();
            this.expect(141);
            this.expect(135);
            return new DoStatement(var1, var23, var4);
         case 95:
            var1 = this.scan();
            var15 = this.argIndex;
            this.expect(140);
            var4 = this.parseExpression();
            this.expect(141);
            this.expect(138);

            while(this.token != -1 && this.token != 139) {
               int var19 = this.argIndex;

               try {
                  switch (this.token) {
                     case 96:
                        this.addArgument(new CaseStatement(this.scan(), this.parseExpression()));
                        this.expect(136);
                        break;
                     case 97:
                        this.addArgument(new CaseStatement(this.scan(), (Expression)null));
                        this.expect(136);
                        break;
                     default:
                        this.addArgument(this.parseStatement());
                  }
               } catch (SyntaxError var13) {
                  this.argIndex = var19;
                  if (!this.recoverStatement()) {
                     throw var13;
                  }
               }
            }

            this.expect(139);
            return new SwitchStatement(var1, var4, this.statArgs(var15));
         case 96:
            this.env.error(this.pos, "case.without.switch");

            while(this.token == 96) {
               this.scan();
               this.parseExpression();
               this.expect(136);
            }

            return this.parseStatement();
         case 97:
            this.env.error(this.pos, "default.without.switch");
            this.scan();
            this.expect(136);
            return this.parseStatement();
         case 98:
            var1 = this.scan();
            var18 = null;
            if (this.token == 60) {
               var18 = this.scanner.idValue;
               this.scan();
            }

            this.expect(135);
            return new BreakStatement(var1, var18);
         case 99:
            var1 = this.scan();
            var18 = null;
            if (this.token == 60) {
               var18 = this.scanner.idValue;
               this.scan();
            }

            this.expect(135);
            return new ContinueStatement(var1, var18);
         case 100:
            var1 = this.scan();
            var3 = null;
            if (this.token != 135) {
               var3 = this.parseExpression();
            }

            this.expect(135);
            return new ReturnStatement(var1, var3);
         case 101:
            var1 = this.scan();
            var3 = null;
            int var16 = this.argIndex;
            boolean var17 = false;
            Object var6 = this.parseBlockStatement();
            if (var3 != null) {
            }

            while(this.token == 102) {
               long var7 = this.pos;
               this.expect(102);
               this.expect(140);
               int var9 = this.parseModifiers(16);
               Expression var10 = this.parseExpression();
               IdentifierToken var11 = this.scanner.getIdToken();
               this.expect(60);
               var11.modifiers = var9;
               this.expect(141);
               this.addArgument(new CatchStatement(var7, var10, var11, this.parseBlockStatement()));
               var17 = true;
            }

            if (var17) {
               var6 = new TryStatement(var1, (Statement)var6, this.statArgs(var16));
            }

            if (this.token == 103) {
               this.scan();
               return new FinallyStatement(var1, (Statement)var6, this.parseBlockStatement());
            } else {
               if (!var17 && var3 == null) {
                  this.env.error(this.pos, "try.without.catch.finally");
                  return new TryStatement(var1, (Statement)var6, (Statement[])null);
               }

               return (Statement)var6;
            }
         case 102:
            this.env.error(this.pos, "catch.without.try");

            Statement var14;
            do {
               this.scan();
               this.expect(140);
               this.parseModifiers(16);
               this.parseExpression();
               this.expect(60);
               this.expect(141);
               var14 = this.parseBlockStatement();
            } while(this.token == 102);

            if (this.token == 103) {
               this.scan();
               var14 = this.parseBlockStatement();
            }

            return var14;
         case 103:
            this.env.error(this.pos, "finally.without.try");
            this.scan();
            return this.parseBlockStatement();
         case 104:
            var1 = this.scan();
            var3 = this.parseExpression();
            this.expect(135);
            return new ThrowStatement(var1, var3);
         case 111:
         case 114:
            return this.parseLocalClass(0);
         case 123:
         case 128:
         case 130:
         case 131:
            var1 = this.pos;
            var15 = this.parseModifiers(2098192);
            switch (this.token) {
               case 60:
               case 70:
               case 71:
               case 72:
               case 73:
               case 74:
               case 75:
               case 76:
               case 78:
                  if ((var15 & 2098176) != 0) {
                     var15 &= -2098177;
                     this.expect(111);
                  }

                  var4 = this.parseExpression();
                  if (this.token != 60) {
                     this.expect(60);
                  }

                  Statement var5 = this.parseDeclaration(var1, var15, var4);
                  this.expect(135);
                  return var5;
               case 111:
               case 114:
                  return this.parseLocalClass(var15);
               default:
                  this.env.error(this.pos, "type.expected");
                  throw new SyntaxError();
            }
         case 126:
            var1 = this.scan();
            this.expect(140);
            var3 = this.parseExpression();
            this.expect(141);
            return new SynchronizedStatement(var1, var3, this.parseBlockStatement());
         case 135:
            return new CompoundStatement(this.scan(), new Statement[0]);
         case 138:
            return this.parseBlockStatement();
      }
   }

   protected Statement parseBlockStatement() throws SyntaxError, IOException {
      if (this.token != 138) {
         this.env.error(this.scanner.prevPos, "token.expected", opNames[138]);
         return this.parseStatement();
      } else {
         long var1 = this.scan();
         int var3 = this.argIndex;

         while(this.token != -1 && this.token != 139) {
            int var4 = this.argIndex;

            try {
               this.addArgument(this.parseStatement());
            } catch (SyntaxError var6) {
               this.argIndex = var4;
               if (!this.recoverStatement()) {
                  throw var6;
               }
            }
         }

         this.expect(139);
         return new CompoundStatement(var1, this.statArgs(var3));
      }
   }

   protected IdentifierToken parseName(boolean var1) throws SyntaxError, IOException {
      IdentifierToken var2 = this.scanner.getIdToken();
      this.expect(60);
      if (this.token != 46) {
         return var2;
      } else {
         StringBuffer var3;
         for(var3 = new StringBuffer(var2.id.toString()); this.token == 46; this.expect(60)) {
            this.scan();
            if (this.token == 33 && var1) {
               this.scan();
               var3.append(".*");
               break;
            }

            var3.append('.');
            if (this.token == 60) {
               var3.append(this.scanner.idValue);
            }
         }

         var2.id = Identifier.lookup(var3.toString());
         return var2;
      }
   }

   /** @deprecated */
   @Deprecated
   protected Identifier parseIdentifier(boolean var1) throws SyntaxError, IOException {
      return this.parseName(var1).id;
   }

   protected Type parseType() throws SyntaxError, IOException {
      Type var1;
      switch (this.token) {
         case 60:
            var1 = Type.tClass(this.parseName(false).id);
            break;
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         default:
            this.env.error(this.pos, "type.expected");
            throw new SyntaxError();
         case 70:
            this.scan();
            var1 = Type.tByte;
            break;
         case 71:
            this.scan();
            var1 = Type.tChar;
            break;
         case 72:
            this.scan();
            var1 = Type.tShort;
            break;
         case 73:
            this.scan();
            var1 = Type.tInt;
            break;
         case 74:
            this.scan();
            var1 = Type.tLong;
            break;
         case 75:
            this.scan();
            var1 = Type.tFloat;
            break;
         case 76:
            this.scan();
            var1 = Type.tDouble;
            break;
         case 77:
            this.scan();
            var1 = Type.tVoid;
            break;
         case 78:
            this.scan();
            var1 = Type.tBoolean;
      }

      return this.parseArrayBrackets(var1);
   }

   protected Type parseArrayBrackets(Type var1) throws SyntaxError, IOException {
      while(this.token == 142) {
         this.scan();
         if (this.token != 143) {
            this.env.error(this.pos, "array.dim.in.decl");
            this.parseExpression();
         }

         this.expect(143);
         var1 = Type.tArray(var1);
      }

      return var1;
   }

   private void addArgument(int var1, Type var2, IdentifierToken var3) {
      var3.modifiers = var1;
      if (this.aCount >= this.aTypes.length) {
         Type[] var4 = new Type[this.aCount * 2];
         System.arraycopy(this.aTypes, 0, var4, 0, this.aCount);
         this.aTypes = var4;
         IdentifierToken[] var5 = new IdentifierToken[this.aCount * 2];
         System.arraycopy(this.aNames, 0, var5, 0, this.aCount);
         this.aNames = var5;
      }

      this.aTypes[this.aCount] = var2;
      this.aNames[this.aCount++] = var3;
   }

   protected int parseModifiers(int var1) throws IOException {
      int var2 = 0;

      while(true) {
         if (this.token == 123) {
            this.env.error(this.pos, "not.supported", "const");
            this.scan();
         }

         int var3 = 0;
         switch (this.token) {
            case 120:
               var3 = 2;
               break;
            case 121:
               var3 = 1;
               break;
            case 122:
               var3 = 4;
            case 123:
            default:
               break;
            case 124:
               var3 = 8;
               break;
            case 125:
               var3 = 128;
               break;
            case 126:
               var3 = 32;
               break;
            case 127:
               var3 = 256;
               break;
            case 128:
               var3 = 16;
               break;
            case 129:
               var3 = 64;
               break;
            case 130:
               var3 = 1024;
               break;
            case 131:
               var3 = 2097152;
         }

         if ((var3 & var1) == 0) {
            return var2;
         }

         if ((var3 & var2) != 0) {
            this.env.error(this.pos, "repeated.modifier");
         }

         var2 |= var3;
         this.scan();
      }
   }

   protected void parseField() throws SyntaxError, IOException {
      if (this.token == 135) {
         this.scan();
      } else {
         String var1 = this.scanner.docComment;
         long var2 = this.pos;
         int var4 = this.parseModifiers(2098687);
         if (var4 == (var4 & 8) && this.token == 138) {
            this.actions.defineField(var2, this.curClass, var1, var4, Type.tMethod(Type.tVoid), new IdentifierToken(idClassInit), (IdentifierToken[])null, (IdentifierToken[])null, this.parseStatement());
         } else if (this.token != 111 && this.token != 114) {
            var2 = this.pos;
            Type var5 = this.parseType();
            IdentifierToken var6 = null;
            switch (this.token) {
               case 60:
                  var6 = this.scanner.getIdToken();
                  var2 = this.scan();
                  break;
               case 140:
                  var6 = new IdentifierToken(idInit);
                  if ((var4 & 2097152) != 0) {
                     this.env.error(this.pos, "bad.constructor.modifier");
                  }
                  break;
               default:
                  this.expect(60);
            }

            if (this.token == 140) {
               this.scan();
               this.aCount = 0;
               if (this.token != 141) {
                  int var11 = this.parseModifiers(16);
                  Type var13 = this.parseType();
                  IdentifierToken var9 = this.scanner.getIdToken();
                  this.expect(60);
                  var13 = this.parseArrayBrackets(var13);
                  this.addArgument(var11, var13, var9);

                  while(this.token == 0) {
                     this.scan();
                     var11 = this.parseModifiers(16);
                     var13 = this.parseType();
                     var9 = this.scanner.getIdToken();
                     this.expect(60);
                     var13 = this.parseArrayBrackets(var13);
                     this.addArgument(var11, var13, var9);
                  }
               }

               this.expect(141);
               var5 = this.parseArrayBrackets(var5);
               Type[] var12 = new Type[this.aCount];
               System.arraycopy(this.aTypes, 0, var12, 0, this.aCount);
               IdentifierToken[] var15 = new IdentifierToken[this.aCount];
               System.arraycopy(this.aNames, 0, var15, 0, this.aCount);
               var5 = Type.tMethod(var5, var12);
               IdentifierToken[] var14 = null;
               if (this.token == 144) {
                  Vector var10 = new Vector();
                  this.scan();
                  var10.addElement(this.parseName(false));

                  while(this.token == 0) {
                     this.scan();
                     var10.addElement(this.parseName(false));
                  }

                  var14 = new IdentifierToken[var10.size()];
                  var10.copyInto(var14);
               }

               switch (this.token) {
                  case 135:
                     this.scan();
                     this.actions.defineField(var2, this.curClass, var1, var4, var5, var6, var15, var14, (Node)null);
                     break;
                  case 138:
                     int var16 = this.FPstate;
                     if ((var4 & 2097152) != 0) {
                        this.FPstate = 2097152;
                     } else {
                        var4 |= this.FPstate & 2097152;
                     }

                     this.actions.defineField(var2, this.curClass, var1, var4, var5, var6, var15, var14, this.parseStatement());
                     this.FPstate = var16;
                     break;
                  default:
                     if ((var4 & 1280) == 0) {
                        this.expect(138);
                     } else {
                        this.expect(135);
                     }
               }

            } else {
               while(true) {
                  var2 = this.pos;
                  Type var7 = this.parseArrayBrackets(var5);
                  Expression var8 = null;
                  if (this.token == 1) {
                     this.scan();
                     var8 = this.parseExpression();
                  }

                  this.actions.defineField(var2, this.curClass, var1, var4, var7, var6, (IdentifierToken[])null, (IdentifierToken[])null, var8);
                  if (this.token != 0) {
                     this.expect(135);
                     return;
                  }

                  this.scan();
                  var6 = this.scanner.getIdToken();
                  this.expect(60);
               }
            }
         } else {
            this.parseNamedClass(var4, 111, var1);
         }
      }
   }

   protected void recoverField(ClassDefinition var1) throws SyntaxError, IOException {
      while(true) {
         switch (this.token) {
            case -1:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 120:
            case 121:
            case 124:
            case 125:
            case 126:
            case 128:
               return;
            case 110:
            case 111:
            case 114:
            case 115:
            case 139:
               this.actions.endClass(this.pos, var1);
               throw new SyntaxError();
            case 138:
               this.match(138, 139);
               this.scan();
               break;
            case 140:
               this.match(140, 141);
               this.scan();
               break;
            case 142:
               this.match(142, 143);
               this.scan();
               break;
            default:
               this.scan();
         }
      }
   }

   protected void parseClass() throws SyntaxError, IOException {
      String var1 = this.scanner.docComment;
      int var2 = this.parseModifiers(2098719);
      this.parseNamedClass(var2, 115, var1);
   }

   protected Statement parseLocalClass(int var1) throws SyntaxError, IOException {
      long var2 = this.pos;
      ClassDefinition var4 = this.parseNamedClass(131072 | var1, 105, (String)null);
      Statement[] var5 = new Statement[]{new VarDeclarationStatement(var2, new LocalMember(var4), (Expression)null)};
      TypeExpression var6 = new TypeExpression(var2, var4.getType());
      return new DeclarationStatement(var2, 0, var6, var5);
   }

   protected ClassDefinition parseNamedClass(int var1, int var2, String var3) throws SyntaxError, IOException {
      switch (this.token) {
         case 111:
            this.scan();
            break;
         case 114:
            this.scan();
            var1 |= 512;
            break;
         default:
            this.env.error(this.pos, "class.expected");
      }

      int var4 = this.FPstate;
      if ((var1 & 2097152) != 0) {
         this.FPstate = 2097152;
      } else {
         var1 |= this.FPstate & 2097152;
      }

      IdentifierToken var5 = this.scanner.getIdToken();
      long var6 = this.pos;
      this.expect(60);
      Vector var8 = new Vector();
      Vector var9 = new Vector();
      this.parseInheritance(var8, var9);
      ClassDefinition var10 = this.parseClassBody(var5, var1, var2, var3, var8, var9, var6);
      this.FPstate = var4;
      return var10;
   }

   protected void parseInheritance(Vector var1, Vector var2) throws SyntaxError, IOException {
      if (this.token == 112) {
         this.scan();
         var1.addElement(this.parseName(false));

         while(this.token == 0) {
            this.scan();
            var1.addElement(this.parseName(false));
         }
      }

      if (this.token == 113) {
         this.scan();
         var2.addElement(this.parseName(false));

         while(this.token == 0) {
            this.scan();
            var2.addElement(this.parseName(false));
         }
      }

   }

   protected ClassDefinition parseClassBody(IdentifierToken var1, int var2, int var3, String var4, Vector var5, Vector var6, long var7) throws SyntaxError, IOException {
      IdentifierToken var9 = null;
      if ((var2 & 512) != 0) {
         if (var6.size() > 0) {
            this.env.error(((IdentifierToken)var6.elementAt(0)).getWhere(), "intf.impl.intf");
         }

         var6 = var5;
      } else if (var5.size() > 0) {
         if (var5.size() > 1) {
            this.env.error(((IdentifierToken)var5.elementAt(1)).getWhere(), "multiple.inherit");
         }

         var9 = (IdentifierToken)var5.elementAt(0);
      }

      ClassDefinition var10 = this.curClass;
      IdentifierToken[] var11 = new IdentifierToken[var6.size()];
      var6.copyInto(var11);
      ClassDefinition var12 = this.actions.beginClass(var7, var4, var2, var1, var9, var11);
      this.expect(138);

      while(this.token != -1 && this.token != 139) {
         try {
            this.curClass = var12;
            this.parseField();
         } catch (SyntaxError var17) {
            this.recoverField(var12);
         } finally {
            this.curClass = var10;
         }
      }

      this.expect(139);
      this.actions.endClass(this.scanner.prevPos, var12);
      return var12;
   }

   protected void recoverFile() throws IOException {
      while(true) {
         switch (this.token) {
            case -1:
               return;
            case 111:
            case 114:
               return;
            case 138:
               this.match(138, 139);
               this.scan();
               break;
            case 140:
               this.match(140, 141);
               this.scan();
               break;
            case 142:
               this.match(142, 143);
               this.scan();
               break;
            default:
               this.scan();
         }
      }
   }

   public void parseFile() {
      try {
         long var1;
         IdentifierToken var3;
         try {
            if (this.token == 115) {
               var1 = this.scan();
               var3 = this.parseName(false);
               this.expect(135);
               this.actions.packageDeclaration(var1, var3);
            }
         } catch (SyntaxError var6) {
            this.recoverFile();
         }

         while(this.token == 110) {
            try {
               var1 = this.scan();
               var3 = this.parseName(true);
               this.expect(135);
               if (var3.id.getName().equals(idStar)) {
                  var3.id = var3.id.getQualifier();
                  this.actions.importPackage(var1, var3);
               } else {
                  this.actions.importClass(var1, var3);
               }
            } catch (SyntaxError var5) {
               this.recoverFile();
            }
         }

         while(this.token != -1) {
            try {
               switch (this.token) {
                  case -1:
                     return;
                  case 111:
                  case 114:
                  case 120:
                  case 121:
                  case 128:
                  case 130:
                  case 131:
                     this.parseClass();
                     break;
                  case 135:
                     this.scan();
                     break;
                  default:
                     this.env.error(this.pos, "toplevel.expected");
                     throw new SyntaxError();
               }
            } catch (SyntaxError var4) {
               this.recoverFile();
            }
         }

      } catch (IOException var7) {
         this.env.error(this.pos, "io.exception", this.env.getSource());
      }
   }

   public long scan() throws IOException {
      if (this.scanner != this && this.scanner != null) {
         long var1 = this.scanner.scan();
         super.token = this.scanner.token;
         super.pos = this.scanner.pos;
         return var1;
      } else {
         return super.scan();
      }
   }

   public void match(int var1, int var2) throws IOException {
      if (this.scanner != this) {
         this.scanner.match(var1, var2);
         super.token = this.scanner.token;
         super.pos = this.scanner.pos;
      } else {
         super.match(var1, var2);
      }
   }
}
