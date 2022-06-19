package com.sun.tools.javac.parser;

import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Convert;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.IntHashTable;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.ArrayList;
import java.util.Iterator;

public class JavacParser implements Parser {
   private static final int infixPrecedenceLevels = 10;
   protected Lexer S;
   protected TreeMaker F;
   private Log log;
   private Source source;
   private Names names;
   private final AbstractEndPosTable endPosTable;
   private List typeAnnotationsPushedBack = List.nil();
   private boolean permitTypeAnnotationsPushBack = false;
   boolean allowGenerics;
   boolean allowDiamond;
   boolean allowMulticatch;
   boolean allowVarargs;
   boolean allowAsserts;
   boolean allowEnums;
   boolean allowForeach;
   boolean allowStaticImport;
   boolean allowAnnotations;
   boolean allowTWR;
   boolean allowStringFolding;
   boolean allowLambda;
   boolean allowMethodReferences;
   boolean allowDefaultMethods;
   boolean allowStaticInterfaceMethods;
   boolean allowIntersectionTypesInCast;
   boolean keepDocComments;
   boolean keepLineMap;
   boolean allowTypeAnnotations;
   boolean allowAnnotationsAfterTypeParams;
   boolean allowThisIdent;
   JCTree.JCVariableDecl receiverParam;
   static final int EXPR = 1;
   static final int TYPE = 2;
   static final int NOPARAMS = 4;
   static final int TYPEARG = 8;
   static final int DIAMOND = 16;
   private int mode = 0;
   private int lastmode = 0;
   protected Tokens.Token token;
   private JCTree.JCErroneous errorTree;
   private int errorPos = -1;
   private final DocCommentTable docComments;
   ArrayList odStackSupply = new ArrayList();
   ArrayList opStackSupply = new ArrayList();
   Filter LAX_IDENTIFIER = new Filter() {
      public boolean accepts(Tokens.TokenKind var1) {
         return var1 == Tokens.TokenKind.IDENTIFIER || var1 == Tokens.TokenKind.UNDERSCORE || var1 == Tokens.TokenKind.ASSERT || var1 == Tokens.TokenKind.ENUM;
      }
   };

   protected JavacParser(ParserFactory var1, Lexer var2, boolean var3, boolean var4, boolean var5) {
      this.S = var2;
      this.nextToken();
      this.F = var1.F;
      this.log = var1.log;
      this.names = var1.names;
      this.source = var1.source;
      this.allowGenerics = this.source.allowGenerics();
      this.allowVarargs = this.source.allowVarargs();
      this.allowAsserts = this.source.allowAsserts();
      this.allowEnums = this.source.allowEnums();
      this.allowForeach = this.source.allowForeach();
      this.allowStaticImport = this.source.allowStaticImport();
      this.allowAnnotations = this.source.allowAnnotations();
      this.allowTWR = this.source.allowTryWithResources();
      this.allowDiamond = this.source.allowDiamond();
      this.allowMulticatch = this.source.allowMulticatch();
      this.allowStringFolding = var1.options.getBoolean("allowStringFolding", true);
      this.allowLambda = this.source.allowLambda();
      this.allowMethodReferences = this.source.allowMethodReferences();
      this.allowDefaultMethods = this.source.allowDefaultMethods();
      this.allowStaticInterfaceMethods = this.source.allowStaticInterfaceMethods();
      this.allowIntersectionTypesInCast = this.source.allowIntersectionTypesInCast();
      this.allowTypeAnnotations = this.source.allowTypeAnnotations();
      this.allowAnnotationsAfterTypeParams = this.source.allowAnnotationsAfterTypeParams();
      this.keepDocComments = var3;
      this.docComments = this.newDocCommentTable(var3, var1);
      this.keepLineMap = var4;
      this.errorTree = this.F.Erroneous();
      this.endPosTable = this.newEndPosTable(var5);
   }

   protected AbstractEndPosTable newEndPosTable(boolean var1) {
      return (AbstractEndPosTable)(var1 ? new SimpleEndPosTable(this) : new EmptyEndPosTable(this));
   }

   protected DocCommentTable newDocCommentTable(boolean var1, ParserFactory var2) {
      return var1 ? new LazyDocCommentTable(var2) : null;
   }

   public Tokens.Token token() {
      return this.token;
   }

   public void nextToken() {
      this.S.nextToken();
      this.token = this.S.token();
   }

   protected boolean peekToken(Filter var1) {
      return this.peekToken(0, (Filter)var1);
   }

   protected boolean peekToken(int var1, Filter var2) {
      return var2.accepts(this.S.token(var1 + 1).kind);
   }

   protected boolean peekToken(Filter var1, Filter var2) {
      return this.peekToken(0, var1, var2);
   }

   protected boolean peekToken(int var1, Filter var2, Filter var3) {
      return var2.accepts(this.S.token(var1 + 1).kind) && var3.accepts(this.S.token(var1 + 2).kind);
   }

   protected boolean peekToken(Filter var1, Filter var2, Filter var3) {
      return this.peekToken(0, var1, var2, var3);
   }

   protected boolean peekToken(int var1, Filter var2, Filter var3, Filter var4) {
      return var2.accepts(this.S.token(var1 + 1).kind) && var3.accepts(this.S.token(var1 + 2).kind) && var4.accepts(this.S.token(var1 + 3).kind);
   }

   protected boolean peekToken(Filter... var1) {
      return this.peekToken(0, (Filter[])var1);
   }

   protected boolean peekToken(int var1, Filter... var2) {
      while(var1 < var2.length) {
         if (!var2[var1].accepts(this.S.token(var1 + 1).kind)) {
            return false;
         }

         ++var1;
      }

      return true;
   }

   private void skip(boolean var1, boolean var2, boolean var3, boolean var4) {
      while(true) {
         switch (this.token.kind) {
            case SEMI:
               this.nextToken();
               return;
            case PUBLIC:
            case FINAL:
            case ABSTRACT:
            case MONKEYS_AT:
            case EOF:
            case CLASS:
            case INTERFACE:
            case ENUM:
               return;
            case IMPORT:
               if (var1) {
                  return;
               }
               break;
            case LBRACE:
            case RBRACE:
            case PRIVATE:
            case PROTECTED:
            case STATIC:
            case TRANSIENT:
            case NATIVE:
            case VOLATILE:
            case SYNCHRONIZED:
            case STRICTFP:
            case LT:
            case BYTE:
            case SHORT:
            case CHAR:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
            case VOID:
               if (var2) {
                  return;
               }
               break;
            case UNDERSCORE:
            case IDENTIFIER:
               if (var3) {
                  return;
               }
               break;
            case CASE:
            case DEFAULT:
            case IF:
            case FOR:
            case WHILE:
            case DO:
            case TRY:
            case SWITCH:
            case RETURN:
            case THROW:
            case BREAK:
            case CONTINUE:
            case ELSE:
            case FINALLY:
            case CATCH:
               if (var4) {
                  return;
               }
         }

         this.nextToken();
      }
   }

   private JCTree.JCErroneous syntaxError(int var1, String var2, Tokens.TokenKind... var3) {
      return this.syntaxError(var1, List.nil(), var2, var3);
   }

   private JCTree.JCErroneous syntaxError(int var1, List var2, String var3, Tokens.TokenKind... var4) {
      this.setErrorEndPos(var1);
      JCTree.JCErroneous var5 = this.F.at(var1).Erroneous(var2);
      this.reportSyntaxError(var5, var3, (Object[])var4);
      if (var2 != null) {
         JCTree var6 = (JCTree)var2.last();
         if (var6 != null) {
            this.storeEnd(var6, var1);
         }
      }

      return (JCTree.JCErroneous)this.toP(var5);
   }

   private void reportSyntaxError(int var1, String var2, Object... var3) {
      JCDiagnostic.SimpleDiagnosticPosition var4 = new JCDiagnostic.SimpleDiagnosticPosition(var1);
      this.reportSyntaxError(var4, var2, var3);
   }

   private void reportSyntaxError(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      int var4 = var1.getPreferredPosition();
      if (var4 > this.S.errPos() || var4 == -1) {
         if (this.token.kind == Tokens.TokenKind.EOF) {
            this.error(var1, "premature.eof");
         } else {
            this.error(var1, var2, var3);
         }
      }

      this.S.errPos(var4);
      if (this.token.pos == this.errorPos) {
         this.nextToken();
      }

      this.errorPos = this.token.pos;
   }

   private JCTree.JCErroneous syntaxError(String var1) {
      return this.syntaxError(this.token.pos, var1);
   }

   private JCTree.JCErroneous syntaxError(String var1, Tokens.TokenKind var2) {
      return this.syntaxError(this.token.pos, var1, var2);
   }

   public void accept(Tokens.TokenKind var1) {
      if (this.token.kind == var1) {
         this.nextToken();
      } else {
         this.setErrorEndPos(this.token.pos);
         this.reportSyntaxError(this.S.prevToken().endPos, "expected", var1);
      }

   }

   JCTree.JCExpression illegal(int var1) {
      this.setErrorEndPos(var1);
      return (this.mode & 1) != 0 ? this.syntaxError(var1, "illegal.start.of.expr") : this.syntaxError(var1, "illegal.start.of.type");
   }

   JCTree.JCExpression illegal() {
      return this.illegal(this.token.pos);
   }

   void checkNoMods(long var1) {
      if (var1 != 0L) {
         long var3 = var1 & -var1;
         this.error(this.token.pos, "mod.not.allowed.here", Flags.asFlagSet(var3));
      }

   }

   void attach(JCTree var1, Tokens.Comment var2) {
      if (this.keepDocComments && var2 != null) {
         this.docComments.putComment(var1, var2);
      }

   }

   private void setErrorEndPos(int var1) {
      this.endPosTable.setErrorEndPos(var1);
   }

   private void storeEnd(JCTree var1, int var2) {
      this.endPosTable.storeEnd(var1, var2);
   }

   private JCTree to(JCTree var1) {
      return this.endPosTable.to(var1);
   }

   private JCTree toP(JCTree var1) {
      return this.endPosTable.toP(var1);
   }

   public int getStartPos(JCTree var1) {
      return TreeInfo.getStartPos(var1);
   }

   public int getEndPos(JCTree var1) {
      return this.endPosTable.getEndPos(var1);
   }

   public Name ident() {
      Name var1;
      if (this.token.kind == Tokens.TokenKind.IDENTIFIER) {
         var1 = this.token.name();
         this.nextToken();
         return var1;
      } else if (this.token.kind == Tokens.TokenKind.ASSERT) {
         if (this.allowAsserts) {
            this.error(this.token.pos, "assert.as.identifier");
            this.nextToken();
            return this.names.error;
         } else {
            this.warning(this.token.pos, "assert.as.identifier");
            var1 = this.token.name();
            this.nextToken();
            return var1;
         }
      } else if (this.token.kind == Tokens.TokenKind.ENUM) {
         if (this.allowEnums) {
            this.error(this.token.pos, "enum.as.identifier");
            this.nextToken();
            return this.names.error;
         } else {
            this.warning(this.token.pos, "enum.as.identifier");
            var1 = this.token.name();
            this.nextToken();
            return var1;
         }
      } else if (this.token.kind == Tokens.TokenKind.THIS) {
         if (this.allowThisIdent) {
            this.checkTypeAnnotations();
            var1 = this.token.name();
            this.nextToken();
            return var1;
         } else {
            this.error(this.token.pos, "this.as.identifier");
            this.nextToken();
            return this.names.error;
         }
      } else if (this.token.kind == Tokens.TokenKind.UNDERSCORE) {
         this.warning(this.token.pos, "underscore.as.identifier");
         var1 = this.token.name();
         this.nextToken();
         return var1;
      } else {
         this.accept(Tokens.TokenKind.IDENTIFIER);
         return this.names.error;
      }
   }

   public JCTree.JCExpression qualident(boolean var1) {
      JCTree.JCExpression var2 = (JCTree.JCExpression)this.toP(this.F.at(this.token.pos).Ident(this.ident()));

      while(this.token.kind == Tokens.TokenKind.DOT) {
         int var3 = this.token.pos;
         this.nextToken();
         List var4 = null;
         if (var1) {
            var4 = this.typeAnnotationsOpt();
         }

         var2 = (JCTree.JCExpression)this.toP(this.F.at(var3).Select(var2, this.ident()));
         if (var4 != null && var4.nonEmpty()) {
            var2 = (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var4.head).pos).AnnotatedType(var4, var2));
         }
      }

      return var2;
   }

   JCTree.JCExpression literal(Name var1) {
      return this.literal(var1, this.token.pos);
   }

   JCTree.JCExpression literal(Name var1, int var2) {
      Object var3 = this.errorTree;
      String var4;
      switch (this.token.kind) {
         case INTLITERAL:
            try {
               var3 = this.F.at(var2).Literal(TypeTag.INT, Convert.string2int(this.strval(var1), this.token.radix()));
            } catch (NumberFormatException var10) {
               this.error(this.token.pos, "int.number.too.large", this.strval(var1));
            }
            break;
         case LONGLITERAL:
            try {
               var3 = this.F.at(var2).Literal(TypeTag.LONG, new Long(Convert.string2long(this.strval(var1), this.token.radix())));
            } catch (NumberFormatException var9) {
               this.error(this.token.pos, "int.number.too.large", this.strval(var1));
            }
            break;
         case FLOATLITERAL:
            var4 = this.token.radix() == 16 ? "0x" + this.token.stringVal() : this.token.stringVal();

            Float var11;
            try {
               var11 = Float.valueOf(var4);
            } catch (NumberFormatException var8) {
               var11 = Float.NaN;
            }

            if (var11 == 0.0F && !this.isZero(var4)) {
               this.error(this.token.pos, "fp.number.too.small");
            } else if (var11 == Float.POSITIVE_INFINITY) {
               this.error(this.token.pos, "fp.number.too.large");
            } else {
               var3 = this.F.at(var2).Literal(TypeTag.FLOAT, var11);
            }
            break;
         case DOUBLELITERAL:
            var4 = this.token.radix() == 16 ? "0x" + this.token.stringVal() : this.token.stringVal();

            Double var5;
            try {
               var5 = Double.valueOf(var4);
            } catch (NumberFormatException var7) {
               var5 = Double.NaN;
            }

            if (var5 == 0.0 && !this.isZero(var4)) {
               this.error(this.token.pos, "fp.number.too.small");
            } else if (var5 == Double.POSITIVE_INFINITY) {
               this.error(this.token.pos, "fp.number.too.large");
            } else {
               var3 = this.F.at(var2).Literal(TypeTag.DOUBLE, var5);
            }
            break;
         case CHARLITERAL:
            var3 = this.F.at(var2).Literal(TypeTag.CHAR, this.token.stringVal().charAt(0) + 0);
            break;
         case STRINGLITERAL:
            var3 = this.F.at(var2).Literal(TypeTag.CLASS, this.token.stringVal());
            break;
         case TRUE:
         case FALSE:
            var3 = this.F.at(var2).Literal(TypeTag.BOOLEAN, this.token.kind == Tokens.TokenKind.TRUE ? 1 : 0);
            break;
         case NULL:
            var3 = this.F.at(var2).Literal(TypeTag.BOT, (Object)null);
            break;
         default:
            Assert.error();
      }

      if (var3 == this.errorTree) {
         var3 = this.F.at(var2).Erroneous();
      }

      this.storeEnd((JCTree)var3, this.token.endPos);
      this.nextToken();
      return (JCTree.JCExpression)var3;
   }

   boolean isZero(String var1) {
      char[] var2 = var1.toCharArray();
      int var3 = var2.length > 1 && Character.toLowerCase(var2[1]) == 'x' ? 16 : 10;

      int var4;
      for(var4 = var3 == 16 ? 2 : 0; var4 < var2.length && (var2[var4] == '0' || var2[var4] == '.'); ++var4) {
      }

      return var4 >= var2.length || Character.digit(var2[var4], var3) <= 0;
   }

   String strval(Name var1) {
      String var2 = this.token.stringVal();
      return var1.isEmpty() ? var2 : var1 + var2;
   }

   public JCTree.JCExpression parseExpression() {
      return this.term(1);
   }

   public JCTree.JCExpression parseType() {
      List var1 = this.typeAnnotationsOpt();
      return this.parseType(var1);
   }

   public JCTree.JCExpression parseType(List var1) {
      JCTree.JCExpression var2 = this.unannotatedType();
      if (var1.nonEmpty()) {
         var2 = this.insertAnnotationsToMostInner(var2, var1, false);
      }

      return var2;
   }

   public JCTree.JCExpression unannotatedType() {
      return this.term(2);
   }

   JCTree.JCExpression term(int var1) {
      int var2 = this.mode;
      this.mode = var1;
      JCTree.JCExpression var3 = this.term();
      this.lastmode = this.mode;
      this.mode = var2;
      return var3;
   }

   JCTree.JCExpression term() {
      JCTree.JCExpression var1 = this.term1();
      return ((this.mode & 1) == 0 || this.token.kind != Tokens.TokenKind.EQ) && (Tokens.TokenKind.PLUSEQ.compareTo(this.token.kind) > 0 || this.token.kind.compareTo(Tokens.TokenKind.GTGTGTEQ) > 0) ? var1 : this.termRest(var1);
   }

   JCTree.JCExpression termRest(JCTree.JCExpression var1) {
      int var2;
      switch (this.token.kind) {
         case EQ:
            var2 = this.token.pos;
            this.nextToken();
            this.mode = 1;
            JCTree.JCExpression var5 = this.term();
            return (JCTree.JCExpression)this.toP(this.F.at(var2).Assign(var1, var5));
         case PLUSEQ:
         case SUBEQ:
         case STAREQ:
         case SLASHEQ:
         case PERCENTEQ:
         case AMPEQ:
         case BAREQ:
         case CARETEQ:
         case LTLTEQ:
         case GTGTEQ:
         case GTGTGTEQ:
            var2 = this.token.pos;
            Tokens.TokenKind var3 = this.token.kind;
            this.nextToken();
            this.mode = 1;
            JCTree.JCExpression var4 = this.term();
            return this.F.at(var2).Assignop(optag(var3), var1, var4);
         default:
            return var1;
      }
   }

   JCTree.JCExpression term1() {
      JCTree.JCExpression var1 = this.term2();
      if ((this.mode & 1) != 0 && this.token.kind == Tokens.TokenKind.QUES) {
         this.mode = 1;
         return this.term1Rest(var1);
      } else {
         return var1;
      }
   }

   JCTree.JCExpression term1Rest(JCTree.JCExpression var1) {
      if (this.token.kind == Tokens.TokenKind.QUES) {
         int var2 = this.token.pos;
         this.nextToken();
         JCTree.JCExpression var3 = this.term();
         this.accept(Tokens.TokenKind.COLON);
         JCTree.JCExpression var4 = this.term1();
         return this.F.at(var2).Conditional(var1, var3, var4);
      } else {
         return var1;
      }
   }

   JCTree.JCExpression term2() {
      JCTree.JCExpression var1 = this.term3();
      if ((this.mode & 1) != 0 && prec(this.token.kind) >= 4) {
         this.mode = 1;
         return this.term2Rest(var1, 4);
      } else {
         return var1;
      }
   }

   JCTree.JCExpression term2Rest(JCTree.JCExpression var1, int var2) {
      JCTree.JCExpression[] var3 = this.newOdStack();
      Tokens.Token[] var4 = this.newOpStack();
      int var5 = 0;
      var3[0] = var1;
      int var6 = this.token.pos;
      Tokens.Token var7 = Tokens.DUMMY;

      while(prec(this.token.kind) >= var2) {
         var4[var5] = var7;
         ++var5;
         var7 = this.token;
         this.nextToken();

         for(var3[var5] = var7.kind == Tokens.TokenKind.INSTANCEOF ? this.parseType() : this.term3(); var5 > 0 && prec(var7.kind) >= prec(this.token.kind); var7 = var4[var5]) {
            var3[var5 - 1] = this.makeOp(var7.pos, var7.kind, var3[var5 - 1], var3[var5]);
            --var5;
         }
      }

      Assert.check(var5 == 0);
      var1 = var3[0];
      if (var1.hasTag(JCTree.Tag.PLUS)) {
         var1 = this.foldStrings(var1);
      }

      this.odStackSupply.add(var3);
      this.opStackSupply.add(var4);
      return var1;
   }

   private JCTree.JCExpression makeOp(int var1, Tokens.TokenKind var2, JCTree.JCExpression var3, JCTree.JCExpression var4) {
      return (JCTree.JCExpression)(var2 == Tokens.TokenKind.INSTANCEOF ? this.F.at(var1).TypeTest(var3, var4) : this.F.at(var1).Binary(optag(var2), var3, var4));
   }

   protected JCTree.JCExpression foldStrings(JCTree.JCExpression var1) {
      if (!this.allowStringFolding) {
         return var1;
      } else {
         ListBuffer var2 = new ListBuffer();
         ListBuffer var3 = new ListBuffer();
         boolean var4 = false;

         JCTree.JCExpression var5;
         JCTree.JCBinary var6;
         for(var5 = var1; var5.hasTag(JCTree.Tag.PLUS); var5 = var6.lhs) {
            var6 = (JCTree.JCBinary)var5;
            var4 |= this.foldIfNeeded(var6.rhs, var3, var2, false);
         }

         var4 |= this.foldIfNeeded(var5, var3, var2, true);
         if (!var4) {
            return var1;
         } else {
            List var10 = var2.toList();
            Object var7 = (JCTree.JCExpression)var10.head;
            Iterator var8 = var10.tail.iterator();

            while(var8.hasNext()) {
               JCTree.JCExpression var9 = (JCTree.JCExpression)var8.next();
               var7 = this.F.at(var9.getStartPosition()).Binary(optag(Tokens.TokenKind.PLUS), (JCTree.JCExpression)var7, var9);
               this.storeEnd((JCTree)var7, this.getEndPos(var9));
            }

            return (JCTree.JCExpression)var7;
         }
      }
   }

   private boolean foldIfNeeded(JCTree.JCExpression var1, ListBuffer var2, ListBuffer var3, boolean var4) {
      JCTree.JCLiteral var5 = this.stringLiteral(var1);
      if (var5 == null) {
         boolean var6 = this.merge(var2, var3);
         var2.clear();
         var3.prepend(var1);
         return var6;
      } else {
         var2.prepend(var5);
         return var4 && this.merge(var2, var3);
      }
   }

   boolean merge(ListBuffer var1, ListBuffer var2) {
      if (var1.isEmpty()) {
         return false;
      } else if (var1.size() == 1) {
         var2.prepend(var1.first());
         return false;
      } else {
         StringBuilder var3 = new StringBuilder();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            JCTree.JCLiteral var5 = (JCTree.JCLiteral)var4.next();
            var3.append(var5.getValue());
         }

         JCTree.JCLiteral var6 = this.F.at(((JCTree.JCLiteral)var1.first()).getStartPosition()).Literal(TypeTag.CLASS, var3.toString());
         this.storeEnd(var6, ((JCTree.JCLiteral)var1.last()).getEndPosition(this.endPosTable));
         var2.prepend(var6);
         return true;
      }
   }

   private JCTree.JCLiteral stringLiteral(JCTree var1) {
      if (var1.hasTag(JCTree.Tag.LITERAL)) {
         JCTree.JCLiteral var2 = (JCTree.JCLiteral)var1;
         if (var2.typetag == TypeTag.CLASS) {
            return var2;
         }
      }

      return null;
   }

   private JCTree.JCExpression[] newOdStack() {
      return this.odStackSupply.isEmpty() ? new JCTree.JCExpression[11] : (JCTree.JCExpression[])this.odStackSupply.remove(this.odStackSupply.size() - 1);
   }

   private Tokens.Token[] newOpStack() {
      return this.opStackSupply.isEmpty() ? new Tokens.Token[11] : (Tokens.Token[])this.opStackSupply.remove(this.opStackSupply.size() - 1);
   }

   protected JCTree.JCExpression term3() {
      int var1 = this.token.pos;
      List var3 = this.typeArgumentsOpt(1);
      JCTree.JCExpression var2;
      List var6;
      JCTree.JCExpression var7;
      Object var11;
      switch (this.token.kind) {
         case MONKEYS_AT:
            List var13 = this.typeAnnotationsOpt();
            if (var13.isEmpty()) {
               throw new AssertionError("Expected type annotations, but found none!");
            } else {
               JCTree.JCExpression var5 = this.term3();
               if ((this.mode & 2) == 0) {
                  switch (var5.getTag()) {
                     case REFERENCE:
                        JCTree.JCMemberReference var17 = (JCTree.JCMemberReference)var5;
                        var17.expr = (JCTree.JCExpression)this.toP(this.F.at(var1).AnnotatedType(var13, var17.expr));
                        var11 = var17;
                        return this.term3Rest((JCTree.JCExpression)var11, var3);
                     case SELECT:
                        JCTree.JCFieldAccess var16 = (JCTree.JCFieldAccess)var5;
                        if (var16.name != this.names._class) {
                           return this.illegal();
                        }

                        this.log.error(this.token.pos, "no.annotations.on.dot.class", new Object[0]);
                        return var5;
                     default:
                        return this.illegal(((JCTree.JCAnnotation)var13.head).pos);
                  }
               } else {
                  var11 = this.insertAnnotationsToMostInner(var5, var13, false);
                  return this.term3Rest((JCTree.JCExpression)var11, var3);
               }
            }
         case EOF:
         case CLASS:
         case INTERFACE:
         case IMPORT:
         case LBRACE:
         case RBRACE:
         case PRIVATE:
         case PROTECTED:
         case STATIC:
         case TRANSIENT:
         case NATIVE:
         case VOLATILE:
         case SYNCHRONIZED:
         case STRICTFP:
         case LT:
         case CASE:
         case DEFAULT:
         case IF:
         case FOR:
         case WHILE:
         case DO:
         case TRY:
         case SWITCH:
         case RETURN:
         case THROW:
         case BREAK:
         case CONTINUE:
         case ELSE:
         case FINALLY:
         case CATCH:
         case EQ:
         case PLUSEQ:
         case SUBEQ:
         case STAREQ:
         case SLASHEQ:
         case PERCENTEQ:
         case AMPEQ:
         case BAREQ:
         case CARETEQ:
         case LTLTEQ:
         case GTGTEQ:
         case GTGTGTEQ:
         case LBRACKET:
         case DOT:
         case ELLIPSIS:
         default:
            return this.illegal();
         case ENUM:
         case UNDERSCORE:
         case IDENTIFIER:
         case ASSERT:
            if (var3 != null) {
               return this.illegal();
            } else {
               if ((this.mode & 1) != 0 && this.peekToken((Filter)Tokens.TokenKind.ARROW)) {
                  var11 = this.lambdaExpressionOrStatement(false, false, var1);
               } else {
                  var11 = (JCTree.JCExpression)this.toP(this.F.at(this.token.pos).Ident(this.ident()));

                  label252:
                  while(true) {
                     var1 = this.token.pos;
                     var6 = this.typeAnnotationsOpt();
                     if (!var6.isEmpty() && this.token.kind != Tokens.TokenKind.LBRACKET && this.token.kind != Tokens.TokenKind.ELLIPSIS) {
                        return this.illegal(((JCTree.JCAnnotation)var6.head).pos);
                     }

                     switch (this.token.kind) {
                        case LT:
                           if ((this.mode & 2) == 0 && this.isUnboundMemberRef()) {
                              int var9 = this.token.pos;
                              this.accept(Tokens.TokenKind.LT);
                              ListBuffer var10 = new ListBuffer();
                              var10.append(this.typeArgument());

                              while(this.token.kind == Tokens.TokenKind.COMMA) {
                                 this.nextToken();
                                 var10.append(this.typeArgument());
                              }

                              this.accept(Tokens.TokenKind.GT);
                              var2 = (JCTree.JCExpression)this.toP(this.F.at(var9).TypeApply((JCTree.JCExpression)var11, var10.toList()));
                              this.checkGenerics();

                              while(this.token.kind == Tokens.TokenKind.DOT) {
                                 this.nextToken();
                                 this.mode = 2;
                                 var2 = (JCTree.JCExpression)this.toP(this.F.at(this.token.pos).Select(var2, this.ident()));
                                 var2 = this.typeArgumentsOpt(var2);
                              }

                              var2 = this.bracketsOpt(var2);
                              if (this.token.kind != Tokens.TokenKind.COLCOL) {
                                 var2 = this.illegal();
                              }

                              this.mode = 1;
                              return this.term3Rest(var2, var3);
                           }
                           break label252;
                        case LBRACKET:
                           this.nextToken();
                           if (this.token.kind == Tokens.TokenKind.RBRACKET) {
                              this.nextToken();
                              var2 = this.bracketsOpt((JCTree.JCExpression)var11);
                              var2 = (JCTree.JCExpression)this.toP(this.F.at(var1).TypeArray(var2));
                              if (var6.nonEmpty()) {
                                 var2 = (JCTree.JCExpression)this.toP(this.F.at(var1).AnnotatedType(var6, var2));
                              }

                              var7 = this.bracketsSuffix(var2);
                              if (var7 != var2 && (var6.nonEmpty() || TreeInfo.containsTypeAnnotation(var2))) {
                                 this.syntaxError("no.annotations.on.dot.class");
                              }

                              var11 = var7;
                              break label252;
                           }

                           if ((this.mode & 1) != 0) {
                              this.mode = 1;
                              var7 = this.term();
                              if (!var6.isEmpty()) {
                                 var11 = this.illegal(((JCTree.JCAnnotation)var6.head).pos);
                              }

                              var11 = (JCTree.JCExpression)this.to(this.F.at(var1).Indexed((JCTree.JCExpression)var11, var7));
                           }

                           this.accept(Tokens.TokenKind.RBRACKET);
                           break label252;
                        case LPAREN:
                           if ((this.mode & 1) != 0) {
                              this.mode = 1;
                              var11 = this.arguments(var3, (JCTree.JCExpression)var11);
                              if (!var6.isEmpty()) {
                                 var11 = this.illegal(((JCTree.JCAnnotation)var6.head).pos);
                              }

                              var3 = null;
                           }
                           break label252;
                        case DOT:
                           this.nextToken();
                           int var15 = this.mode;
                           this.mode &= -5;
                           var3 = this.typeArgumentsOpt(1);
                           this.mode = var15;
                           if ((this.mode & 1) != 0) {
                              switch (this.token.kind) {
                                 case CLASS:
                                    if (var3 != null) {
                                       return this.illegal();
                                    }

                                    this.mode = 1;
                                    var11 = (JCTree.JCExpression)this.to(this.F.at(var1).Select((JCTree.JCExpression)var11, (Name)this.names._class));
                                    this.nextToken();
                                    break label252;
                                 case THIS:
                                    if (var3 != null) {
                                       return this.illegal();
                                    }

                                    this.mode = 1;
                                    var11 = (JCTree.JCExpression)this.to(this.F.at(var1).Select((JCTree.JCExpression)var11, (Name)this.names._this));
                                    this.nextToken();
                                    break label252;
                                 case SUPER:
                                    this.mode = 1;
                                    var2 = (JCTree.JCExpression)this.to(this.F.at(var1).Select((JCTree.JCExpression)var11, (Name)this.names._super));
                                    var11 = this.superSuffix(var3, var2);
                                    var3 = null;
                                    break label252;
                                 case NEW:
                                    if (var3 != null) {
                                       return this.illegal();
                                    }

                                    this.mode = 1;
                                    int var8 = this.token.pos;
                                    this.nextToken();
                                    if (this.token.kind == Tokens.TokenKind.LT) {
                                       var3 = this.typeArguments(false);
                                    }

                                    var11 = this.innerCreator(var8, var3, (JCTree.JCExpression)var11);
                                    var3 = null;
                                    break label252;
                              }
                           }

                           List var18 = null;
                           if ((this.mode & 2) != 0 && this.token.kind == Tokens.TokenKind.MONKEYS_AT) {
                              var18 = this.typeAnnotationsOpt();
                           }

                           var11 = (JCTree.JCExpression)this.toP(this.F.at(var1).Select((JCTree.JCExpression)var11, (Name)this.ident()));
                           if (var18 != null && var18.nonEmpty()) {
                              var11 = (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var18.head).pos).AnnotatedType(var18, (JCTree.JCExpression)var11));
                           }
                           break;
                        case ELLIPSIS:
                           if (this.permitTypeAnnotationsPushBack) {
                              this.typeAnnotationsPushedBack = var6;
                           } else if (var6.nonEmpty()) {
                              this.illegal(((JCTree.JCAnnotation)var6.head).pos);
                           }
                        default:
                           break label252;
                     }
                  }
               }

               if (var3 != null) {
                  this.illegal();
               }

               var11 = this.typeArgumentsOpt((JCTree.JCExpression)var11);
               return this.term3Rest((JCTree.JCExpression)var11, var3);
            }
         case BYTE:
         case SHORT:
         case CHAR:
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
         case BOOLEAN:
            if (var3 != null) {
               this.illegal();
            }

            var11 = this.bracketsSuffix(this.bracketsOpt(this.basicType()));
            return this.term3Rest((JCTree.JCExpression)var11, var3);
         case VOID:
            if (var3 != null) {
               this.illegal();
            }

            JCTree.JCPrimitiveTypeTree var14;
            if ((this.mode & 1) == 0) {
               var14 = (JCTree.JCPrimitiveTypeTree)this.to(this.F.at(var1).TypeIdent(TypeTag.VOID));
               this.nextToken();
               return var14;
            } else {
               this.nextToken();
               if (this.token.kind != Tokens.TokenKind.DOT) {
                  return this.illegal(var1);
               }

               var14 = (JCTree.JCPrimitiveTypeTree)this.toP(this.F.at(var1).TypeIdent(TypeTag.VOID));
               var11 = this.bracketsSuffix(var14);
               return this.term3Rest((JCTree.JCExpression)var11, var3);
            }
         case INTLITERAL:
         case LONGLITERAL:
         case FLOATLITERAL:
         case DOUBLELITERAL:
         case CHARLITERAL:
         case STRINGLITERAL:
         case TRUE:
         case FALSE:
         case NULL:
            if (var3 != null || (this.mode & 1) == 0) {
               return this.illegal();
            }

            this.mode = 1;
            var11 = this.literal(this.names.empty);
            return this.term3Rest((JCTree.JCExpression)var11, var3);
         case THIS:
            if ((this.mode & 1) == 0) {
               return this.illegal();
            }

            this.mode = 1;
            var2 = (JCTree.JCExpression)this.to(this.F.at(var1).Ident(this.names._this));
            this.nextToken();
            if (var3 == null) {
               var11 = this.argumentsOpt((List)null, var2);
            } else {
               var11 = this.arguments(var3, var2);
            }

            var3 = null;
            return this.term3Rest((JCTree.JCExpression)var11, var3);
         case SUPER:
            if ((this.mode & 1) == 0) {
               return this.illegal();
            }

            this.mode = 1;
            var2 = (JCTree.JCExpression)this.to(this.F.at(var1).Ident(this.names._super));
            var11 = this.superSuffix(var3, var2);
            var3 = null;
            return this.term3Rest((JCTree.JCExpression)var11, var3);
         case NEW:
            if (var3 != null) {
               return this.illegal();
            } else {
               if ((this.mode & 1) == 0) {
                  return this.illegal();
               }

               this.mode = 1;
               this.nextToken();
               if (this.token.kind == Tokens.TokenKind.LT) {
                  var3 = this.typeArguments(false);
               }

               var11 = this.creator(var1, var3);
               var3 = null;
               return this.term3Rest((JCTree.JCExpression)var11, var3);
            }
         case LPAREN:
            if (var3 == null && (this.mode & 1) != 0) {
               ParensResult var12 = this.analyzeParens();
               switch (var12) {
                  case CAST:
                     this.accept(Tokens.TokenKind.LPAREN);
                     this.mode = 2;

                     for(var6 = List.of(var2 = this.term3()); this.token.kind == Tokens.TokenKind.AMP; var6 = var6.prepend(this.term3())) {
                        this.checkIntersectionTypesInCast();
                        this.accept(Tokens.TokenKind.AMP);
                     }

                     if (var6.length() > 1) {
                        var2 = (JCTree.JCExpression)this.toP(this.F.at(var1).TypeIntersection(var6.reverse()));
                     }

                     this.accept(Tokens.TokenKind.RPAREN);
                     this.mode = 1;
                     var7 = this.term3();
                     return this.F.at(var1).TypeCast((JCTree)var2, var7);
                  case IMPLICIT_LAMBDA:
                  case EXPLICIT_LAMBDA:
                     var11 = this.lambdaExpressionOrStatement(true, var12 == JavacParser.ParensResult.EXPLICIT_LAMBDA, var1);
                     return this.term3Rest((JCTree.JCExpression)var11, var3);
                  default:
                     this.accept(Tokens.TokenKind.LPAREN);
                     this.mode = 1;
                     var2 = this.termRest(this.term1Rest(this.term2Rest(this.term3(), 4)));
                     this.accept(Tokens.TokenKind.RPAREN);
                     var11 = (JCTree.JCExpression)this.toP(this.F.at(var1).Parens(var2));
                     return this.term3Rest((JCTree.JCExpression)var11, var3);
               }
            }

            return this.illegal();
         case QUES:
            if ((this.mode & 2) != 0 && (this.mode & 12) == 8) {
               this.mode = 2;
               return this.typeArgument();
            } else {
               return this.illegal();
            }
         case PLUSPLUS:
         case SUBSUB:
         case BANG:
         case TILDE:
         case PLUS:
         case SUB:
            if (var3 == null && (this.mode & 1) != 0) {
               Tokens.TokenKind var4 = this.token.kind;
               this.nextToken();
               this.mode = 1;
               if (var4 == Tokens.TokenKind.SUB && (this.token.kind == Tokens.TokenKind.INTLITERAL || this.token.kind == Tokens.TokenKind.LONGLITERAL) && this.token.radix() == 10) {
                  this.mode = 1;
                  var11 = this.literal(this.names.hyphen, var1);
                  return this.term3Rest((JCTree.JCExpression)var11, var3);
               } else {
                  var2 = this.term3();
                  return this.F.at(var1).Unary(unoptag(var4), var2);
               }
            } else {
               return this.illegal();
            }
      }
   }

   JCTree.JCExpression term3Rest(JCTree.JCExpression var1, List var2) {
      if (var2 != null) {
         this.illegal();
      }

      while(true) {
         while(true) {
            while(true) {
               int var3 = this.token.pos;
               List var4 = this.typeAnnotationsOpt();
               int var5;
               JCTree.JCExpression var6;
               if (this.token.kind != Tokens.TokenKind.LBRACKET) {
                  if (this.token.kind != Tokens.TokenKind.DOT) {
                     if ((this.mode & 1) == 0 || this.token.kind != Tokens.TokenKind.COLCOL) {
                        if (!var4.isEmpty()) {
                           if (!this.permitTypeAnnotationsPushBack) {
                              return this.illegal(((JCTree.JCAnnotation)var4.head).pos);
                           }

                           this.typeAnnotationsPushedBack = var4;
                        }

                        while((this.token.kind == Tokens.TokenKind.PLUSPLUS || this.token.kind == Tokens.TokenKind.SUBSUB) && (this.mode & 1) != 0) {
                           this.mode = 1;
                           var1 = (JCTree.JCExpression)this.to(this.F.at(this.token.pos).Unary(this.token.kind == Tokens.TokenKind.PLUSPLUS ? JCTree.Tag.POSTINC : JCTree.Tag.POSTDEC, (JCTree.JCExpression)var1));
                           this.nextToken();
                        }

                        return (JCTree.JCExpression)this.toP((JCTree)var1);
                     }

                     this.mode = 1;
                     if (var2 != null) {
                        return this.illegal();
                     }

                     this.accept(Tokens.TokenKind.COLCOL);
                     var1 = this.memberReferenceSuffix(var3, (JCTree.JCExpression)var1);
                  } else {
                     this.nextToken();
                     var2 = this.typeArgumentsOpt(1);
                     if (this.token.kind == Tokens.TokenKind.SUPER && (this.mode & 1) != 0) {
                        this.mode = 1;
                        var6 = (JCTree.JCExpression)this.to(this.F.at(var3).Select((JCTree.JCExpression)var1, (Name)this.names._super));
                        this.nextToken();
                        var1 = this.arguments(var2, var6);
                        var2 = null;
                     } else if (this.token.kind == Tokens.TokenKind.NEW && (this.mode & 1) != 0) {
                        if (var2 != null) {
                           return this.illegal();
                        }

                        this.mode = 1;
                        var5 = this.token.pos;
                        this.nextToken();
                        if (this.token.kind == Tokens.TokenKind.LT) {
                           var2 = this.typeArguments(false);
                        }

                        var1 = this.innerCreator(var5, var2, (JCTree.JCExpression)var1);
                        var2 = null;
                     } else {
                        List var8 = null;
                        if ((this.mode & 2) != 0 && this.token.kind == Tokens.TokenKind.MONKEYS_AT) {
                           var8 = this.typeAnnotationsOpt();
                        }

                        var6 = (JCTree.JCExpression)this.toP(this.F.at(var3).Select((JCTree.JCExpression)var1, (Name)this.ident()));
                        if (var8 != null && var8.nonEmpty()) {
                           var6 = (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var8.head).pos).AnnotatedType(var8, var6));
                        }

                        var1 = this.argumentsOpt(var2, this.typeArgumentsOpt(var6));
                        var2 = null;
                     }
                  }
               } else {
                  this.nextToken();
                  if ((this.mode & 2) != 0) {
                     var5 = this.mode;
                     this.mode = 2;
                     if (this.token.kind == Tokens.TokenKind.RBRACKET) {
                        this.nextToken();
                        var6 = this.bracketsOpt((JCTree.JCExpression)var1);
                        var1 = (JCTree.JCExpression)this.toP(this.F.at(var3).TypeArray(var6));
                        if (this.token.kind != Tokens.TokenKind.COLCOL) {
                           if (var4.nonEmpty()) {
                              var1 = (JCTree.JCExpression)this.toP(this.F.at(var3).AnnotatedType(var4, (JCTree.JCExpression)var1));
                           }

                           return (JCTree.JCExpression)var1;
                        }

                        this.mode = 1;
                        continue;
                     }

                     this.mode = var5;
                  }

                  if ((this.mode & 1) != 0) {
                     this.mode = 1;
                     JCTree.JCExpression var7 = this.term();
                     var1 = (JCTree.JCExpression)this.to(this.F.at(var3).Indexed((JCTree.JCExpression)var1, var7));
                  }

                  this.accept(Tokens.TokenKind.RBRACKET);
               }
            }
         }
      }
   }

   boolean isUnboundMemberRef() {
      int var1 = 0;
      int var2 = 0;
      Tokens.Token var3 = this.S.token(var1);

      while(true) {
         Tokens.TokenKind var5;
         label46:
         switch (var3.kind) {
            case MONKEYS_AT:
            case BYTE:
            case SHORT:
            case CHAR:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
            case UNDERSCORE:
            case IDENTIFIER:
            case SUPER:
            case LBRACKET:
            case DOT:
            case QUES:
            case EXTENDS:
            case RBRACKET:
            case COMMA:
               break;
            case EOF:
            case CLASS:
            case INTERFACE:
            case ENUM:
            case IMPORT:
            case LBRACE:
            case RBRACE:
            case PRIVATE:
            case PROTECTED:
            case STATIC:
            case TRANSIENT:
            case NATIVE:
            case VOLATILE:
            case SYNCHRONIZED:
            case STRICTFP:
            case VOID:
            case CASE:
            case DEFAULT:
            case IF:
            case FOR:
            case WHILE:
            case DO:
            case TRY:
            case SWITCH:
            case RETURN:
            case THROW:
            case BREAK:
            case CONTINUE:
            case ELSE:
            case FINALLY:
            case CATCH:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case EQ:
            case PLUSEQ:
            case SUBEQ:
            case STAREQ:
            case SLASHEQ:
            case PERCENTEQ:
            case AMPEQ:
            case BAREQ:
            case CARETEQ:
            case LTLTEQ:
            case GTGTEQ:
            case GTGTGTEQ:
            case THIS:
            case NEW:
            case ELLIPSIS:
            case PLUSPLUS:
            case SUBSUB:
            case BANG:
            case TILDE:
            case PLUS:
            case SUB:
            case ASSERT:
            case RPAREN:
            default:
               return false;
            case LT:
               ++var2;
               break;
            case LPAREN:
               int var4 = 0;

               while(true) {
                  var5 = this.S.token(var1).kind;
                  switch (var5) {
                     case EOF:
                        return false;
                     case LPAREN:
                        ++var4;
                        break;
                     case RPAREN:
                        --var4;
                        if (var4 == 0) {
                           break label46;
                        }
                  }

                  ++var1;
               }
            case GTGTGT:
               --var2;
            case GTGT:
               --var2;
            case GT:
               --var2;
               if (var2 == 0) {
                  var5 = this.S.token(var1 + 1).kind;
                  return var5 == Tokens.TokenKind.DOT || var5 == Tokens.TokenKind.LBRACKET || var5 == Tokens.TokenKind.COLCOL;
               }
         }

         ++var1;
         var3 = this.S.token(var1);
      }
   }

   ParensResult analyzeParens() {
      int var1 = 0;
      boolean var2 = false;
      int var3 = 0;

      while(true) {
         Tokens.TokenKind var4 = this.S.token(var3).kind;
         label122:
         switch (var4) {
            case FINAL:
            case ELLIPSIS:
               return JavacParser.ParensResult.EXPLICIT_LAMBDA;
            case ABSTRACT:
            case EOF:
            case CLASS:
            case INTERFACE:
            case IMPORT:
            case LBRACE:
            case RBRACE:
            case PRIVATE:
            case PROTECTED:
            case STATIC:
            case TRANSIENT:
            case NATIVE:
            case VOLATILE:
            case SYNCHRONIZED:
            case STRICTFP:
            case VOID:
            case CASE:
            case DEFAULT:
            case IF:
            case FOR:
            case WHILE:
            case DO:
            case TRY:
            case SWITCH:
            case RETURN:
            case THROW:
            case BREAK:
            case CONTINUE:
            case ELSE:
            case FINALLY:
            case CATCH:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case EQ:
            case PLUSEQ:
            case SUBEQ:
            case STAREQ:
            case SLASHEQ:
            case PERCENTEQ:
            case AMPEQ:
            case BAREQ:
            case CARETEQ:
            case LTLTEQ:
            case GTGTEQ:
            case GTGTGTEQ:
            case THIS:
            case NEW:
            case PLUSPLUS:
            case SUBSUB:
            case BANG:
            case TILDE:
            case PLUS:
            case SUB:
            case RBRACKET:
            default:
               return JavacParser.ParensResult.PARENS;
            case MONKEYS_AT:
               var2 = true;
               ++var3;

               while(this.peekToken(var3, (Filter)Tokens.TokenKind.DOT)) {
                  var3 += 2;
               }

               if (!this.peekToken(var3, (Filter)Tokens.TokenKind.LPAREN)) {
                  break;
               }

               ++var3;
               int var5 = 0;

               while(true) {
                  Tokens.TokenKind var6 = this.S.token(var3).kind;
                  switch (var6) {
                     case EOF:
                        return JavacParser.ParensResult.PARENS;
                     case LPAREN:
                        ++var5;
                        break;
                     case RPAREN:
                        --var5;
                        if (var5 == 0) {
                           break label122;
                        }
                  }

                  ++var3;
               }
            case ENUM:
            case UNDERSCORE:
            case IDENTIFIER:
            case ASSERT:
               if (this.peekToken(var3, this.LAX_IDENTIFIER)) {
                  return JavacParser.ParensResult.EXPLICIT_LAMBDA;
               }

               if (this.peekToken(var3, Tokens.TokenKind.RPAREN, Tokens.TokenKind.ARROW)) {
                  return JavacParser.ParensResult.IMPLICIT_LAMBDA;
               }

               var2 = false;
               break;
            case LT:
               ++var1;
               break;
            case BYTE:
            case SHORT:
            case CHAR:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
               if (this.peekToken(var3, (Filter)Tokens.TokenKind.RPAREN)) {
                  return JavacParser.ParensResult.CAST;
               }

               if (this.peekToken(var3, this.LAX_IDENTIFIER)) {
                  return JavacParser.ParensResult.EXPLICIT_LAMBDA;
               }
            case SUPER:
            case DOT:
            case EXTENDS:
            case AMP:
               break;
            case LBRACKET:
               if (this.peekToken(var3, Tokens.TokenKind.RBRACKET, this.LAX_IDENTIFIER)) {
                  return JavacParser.ParensResult.EXPLICIT_LAMBDA;
               }

               if (this.peekToken(var3, Tokens.TokenKind.RBRACKET, Tokens.TokenKind.RPAREN) || this.peekToken(var3, Tokens.TokenKind.RBRACKET, Tokens.TokenKind.AMP)) {
                  return JavacParser.ParensResult.CAST;
               }

               if (!this.peekToken(var3, (Filter)Tokens.TokenKind.RBRACKET)) {
                  return JavacParser.ParensResult.PARENS;
               }

               var2 = true;
               ++var3;
               break;
            case LPAREN:
               if (var3 != 0) {
                  return JavacParser.ParensResult.PARENS;
               }

               if (this.peekToken(var3, (Filter)Tokens.TokenKind.RPAREN)) {
                  return JavacParser.ParensResult.EXPLICIT_LAMBDA;
               }
               break;
            case QUES:
               if (this.peekToken(var3, (Filter)Tokens.TokenKind.EXTENDS) || this.peekToken(var3, (Filter)Tokens.TokenKind.SUPER)) {
                  var2 = true;
               }
               break;
            case RPAREN:
               if (var2) {
                  return JavacParser.ParensResult.CAST;
               }

               switch (this.S.token(var3 + 1).kind) {
                  case ENUM:
                  case BYTE:
                  case SHORT:
                  case CHAR:
                  case INT:
                  case LONG:
                  case FLOAT:
                  case DOUBLE:
                  case BOOLEAN:
                  case VOID:
                  case UNDERSCORE:
                  case IDENTIFIER:
                  case INTLITERAL:
                  case LONGLITERAL:
                  case FLOATLITERAL:
                  case DOUBLELITERAL:
                  case CHARLITERAL:
                  case STRINGLITERAL:
                  case TRUE:
                  case FALSE:
                  case NULL:
                  case THIS:
                  case SUPER:
                  case NEW:
                  case LPAREN:
                  case BANG:
                  case TILDE:
                  case ASSERT:
                     return JavacParser.ParensResult.CAST;
                  case IMPORT:
                  case LBRACE:
                  case RBRACE:
                  case PRIVATE:
                  case PROTECTED:
                  case STATIC:
                  case TRANSIENT:
                  case NATIVE:
                  case VOLATILE:
                  case SYNCHRONIZED:
                  case STRICTFP:
                  case LT:
                  case CASE:
                  case DEFAULT:
                  case IF:
                  case FOR:
                  case WHILE:
                  case DO:
                  case TRY:
                  case SWITCH:
                  case RETURN:
                  case THROW:
                  case BREAK:
                  case CONTINUE:
                  case ELSE:
                  case FINALLY:
                  case CATCH:
                  case EQ:
                  case PLUSEQ:
                  case SUBEQ:
                  case STAREQ:
                  case SLASHEQ:
                  case PERCENTEQ:
                  case AMPEQ:
                  case BAREQ:
                  case CARETEQ:
                  case LTLTEQ:
                  case GTGTEQ:
                  case GTGTGTEQ:
                  case LBRACKET:
                  case DOT:
                  case ELLIPSIS:
                  case QUES:
                  case PLUSPLUS:
                  case SUBSUB:
                  case PLUS:
                  case SUB:
                  default:
                     return JavacParser.ParensResult.PARENS;
               }
            case COMMA:
               var2 = true;
               break;
            case GTGTGT:
               --var1;
            case GTGT:
               --var1;
            case GT:
               --var1;
               if (var1 == 0) {
                  if (this.peekToken(var3, (Filter)Tokens.TokenKind.RPAREN) || this.peekToken(var3, (Filter)Tokens.TokenKind.AMP)) {
                     return JavacParser.ParensResult.CAST;
                  }

                  if (this.peekToken(var3, this.LAX_IDENTIFIER, Tokens.TokenKind.COMMA) || this.peekToken(var3, this.LAX_IDENTIFIER, Tokens.TokenKind.RPAREN, Tokens.TokenKind.ARROW) || this.peekToken(var3, (Filter)Tokens.TokenKind.ELLIPSIS)) {
                     return JavacParser.ParensResult.EXPLICIT_LAMBDA;
                  }

                  var2 = true;
               } else if (var1 < 0) {
                  return JavacParser.ParensResult.PARENS;
               }
         }

         ++var3;
      }
   }

   JCTree.JCExpression lambdaExpressionOrStatement(boolean var1, boolean var2, int var3) {
      List var4 = var2 ? this.formalParameters(true) : this.implicitParameters(var1);
      return this.lambdaExpressionOrStatementRest(var4, var3);
   }

   JCTree.JCExpression lambdaExpressionOrStatementRest(List var1, int var2) {
      this.checkLambda();
      this.accept(Tokens.TokenKind.ARROW);
      return this.token.kind == Tokens.TokenKind.LBRACE ? this.lambdaStatement(var1, var2, var2) : this.lambdaExpression(var1, var2);
   }

   JCTree.JCExpression lambdaStatement(List var1, int var2, int var3) {
      JCTree.JCBlock var4 = this.block(var3, 0L);
      return (JCTree.JCExpression)this.toP(this.F.at(var2).Lambda(var1, var4));
   }

   JCTree.JCExpression lambdaExpression(List var1, int var2) {
      JCTree.JCExpression var3 = this.parseExpression();
      return (JCTree.JCExpression)this.toP(this.F.at(var2).Lambda(var1, var3));
   }

   JCTree.JCExpression superSuffix(List var1, JCTree.JCExpression var2) {
      this.nextToken();
      Object var4;
      if (this.token.kind != Tokens.TokenKind.LPAREN && var1 == null) {
         if (this.token.kind == Tokens.TokenKind.COLCOL) {
            if (var1 != null) {
               return this.illegal();
            }

            var4 = this.memberReferenceSuffix(var2);
         } else {
            int var3 = this.token.pos;
            this.accept(Tokens.TokenKind.DOT);
            var1 = this.token.kind == Tokens.TokenKind.LT ? this.typeArguments(false) : null;
            var2 = (JCTree.JCExpression)this.toP(this.F.at(var3).Select(var2, this.ident()));
            var4 = this.argumentsOpt(var1, var2);
         }
      } else {
         var4 = this.arguments(var1, var2);
      }

      return (JCTree.JCExpression)var4;
   }

   JCTree.JCPrimitiveTypeTree basicType() {
      JCTree.JCPrimitiveTypeTree var1 = (JCTree.JCPrimitiveTypeTree)this.to(this.F.at(this.token.pos).TypeIdent(typetag(this.token.kind)));
      this.nextToken();
      return var1;
   }

   JCTree.JCExpression argumentsOpt(List var1, JCTree.JCExpression var2) {
      if (((this.mode & 1) == 0 || this.token.kind != Tokens.TokenKind.LPAREN) && var1 == null) {
         return var2;
      } else {
         this.mode = 1;
         return this.arguments(var1, var2);
      }
   }

   List arguments() {
      ListBuffer var1 = new ListBuffer();
      if (this.token.kind == Tokens.TokenKind.LPAREN) {
         this.nextToken();
         if (this.token.kind != Tokens.TokenKind.RPAREN) {
            var1.append(this.parseExpression());

            while(this.token.kind == Tokens.TokenKind.COMMA) {
               this.nextToken();
               var1.append(this.parseExpression());
            }
         }

         this.accept(Tokens.TokenKind.RPAREN);
      } else {
         this.syntaxError(this.token.pos, "expected", Tokens.TokenKind.LPAREN);
      }

      return var1.toList();
   }

   JCTree.JCMethodInvocation arguments(List var1, JCTree.JCExpression var2) {
      int var3 = this.token.pos;
      List var4 = this.arguments();
      return (JCTree.JCMethodInvocation)this.toP(this.F.at(var3).Apply(var1, var2, var4));
   }

   JCTree.JCExpression typeArgumentsOpt(JCTree.JCExpression var1) {
      if (this.token.kind == Tokens.TokenKind.LT && (this.mode & 2) != 0 && (this.mode & 4) == 0) {
         this.mode = 2;
         this.checkGenerics();
         return this.typeArguments(var1, false);
      } else {
         return var1;
      }
   }

   List typeArgumentsOpt() {
      return this.typeArgumentsOpt(2);
   }

   List typeArgumentsOpt(int var1) {
      if (this.token.kind != Tokens.TokenKind.LT) {
         return null;
      } else {
         this.checkGenerics();
         if ((this.mode & var1) == 0 || (this.mode & 4) != 0) {
            this.illegal();
         }

         this.mode = var1;
         return this.typeArguments(false);
      }
   }

   List typeArguments(boolean var1) {
      if (this.token.kind == Tokens.TokenKind.LT) {
         this.nextToken();
         if (this.token.kind == Tokens.TokenKind.GT && var1) {
            this.checkDiamond();
            this.mode |= 16;
            this.nextToken();
            return List.nil();
         } else {
            ListBuffer var2 = new ListBuffer();
            var2.append((this.mode & 1) == 0 ? this.typeArgument() : this.parseType());

            while(this.token.kind == Tokens.TokenKind.COMMA) {
               this.nextToken();
               var2.append((this.mode & 1) == 0 ? this.typeArgument() : this.parseType());
            }

            switch (this.token.kind) {
               case GTGTEQ:
               case GTGTGTEQ:
               case GTGTGT:
               case GTGT:
               case GTEQ:
                  this.token = this.S.split();
                  break;
               case GT:
                  this.nextToken();
                  break;
               default:
                  var2.append(this.syntaxError(this.token.pos, "expected", Tokens.TokenKind.GT));
            }

            return var2.toList();
         }
      } else {
         return List.of(this.syntaxError(this.token.pos, "expected", Tokens.TokenKind.LT));
      }
   }

   JCTree.JCExpression typeArgument() {
      List var1 = this.typeAnnotationsOpt();
      if (this.token.kind != Tokens.TokenKind.QUES) {
         return this.parseType(var1);
      } else {
         int var2 = this.token.pos;
         this.nextToken();
         Object var3;
         JCTree.TypeBoundKind var4;
         JCTree.JCExpression var5;
         if (this.token.kind == Tokens.TokenKind.EXTENDS) {
            var4 = (JCTree.TypeBoundKind)this.to(this.F.at(var2).TypeBoundKind(BoundKind.EXTENDS));
            this.nextToken();
            var5 = this.parseType();
            var3 = this.F.at(var2).Wildcard(var4, var5);
         } else if (this.token.kind == Tokens.TokenKind.SUPER) {
            var4 = (JCTree.TypeBoundKind)this.to(this.F.at(var2).TypeBoundKind(BoundKind.SUPER));
            this.nextToken();
            var5 = this.parseType();
            var3 = this.F.at(var2).Wildcard(var4, var5);
         } else if (this.LAX_IDENTIFIER.accepts(this.token.kind)) {
            var4 = this.F.at(-1).TypeBoundKind(BoundKind.UNBOUND);
            var5 = (JCTree.JCExpression)this.toP(this.F.at(var2).Wildcard(var4, (JCTree)null));
            JCTree.JCIdent var6 = (JCTree.JCIdent)this.toP(this.F.at(this.token.pos).Ident(this.ident()));
            JCTree.JCErroneous var7 = this.F.at(var2).Erroneous(List.of(var5, var6));
            this.reportSyntaxError(var7, "expected3", Tokens.TokenKind.GT, Tokens.TokenKind.EXTENDS, Tokens.TokenKind.SUPER);
            var3 = var7;
         } else {
            var4 = (JCTree.TypeBoundKind)this.toP(this.F.at(var2).TypeBoundKind(BoundKind.UNBOUND));
            var3 = (JCTree.JCExpression)this.toP(this.F.at(var2).Wildcard(var4, (JCTree)null));
         }

         if (!var1.isEmpty()) {
            var3 = (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var1.head).pos).AnnotatedType(var1, (JCTree.JCExpression)var3));
         }

         return (JCTree.JCExpression)var3;
      }
   }

   JCTree.JCTypeApply typeArguments(JCTree.JCExpression var1, boolean var2) {
      int var3 = this.token.pos;
      List var4 = this.typeArguments(var2);
      return (JCTree.JCTypeApply)this.toP(this.F.at(var3).TypeApply(var1, var4));
   }

   private JCTree.JCExpression bracketsOpt(JCTree.JCExpression var1, List var2) {
      List var3 = this.typeAnnotationsOpt();
      if (this.token.kind == Tokens.TokenKind.LBRACKET) {
         int var4 = this.token.pos;
         this.nextToken();
         var1 = this.bracketsOptCont(var1, var4, var3);
      } else if (!var3.isEmpty()) {
         if (!this.permitTypeAnnotationsPushBack) {
            return this.illegal(((JCTree.JCAnnotation)var3.head).pos);
         }

         this.typeAnnotationsPushedBack = var3;
      }

      if (!var2.isEmpty()) {
         var1 = (JCTree.JCExpression)this.toP(this.F.at(this.token.pos).AnnotatedType(var2, var1));
      }

      return var1;
   }

   private JCTree.JCExpression bracketsOpt(JCTree.JCExpression var1) {
      return this.bracketsOpt(var1, List.nil());
   }

   private JCTree.JCExpression bracketsOptCont(JCTree.JCExpression var1, int var2, List var3) {
      this.accept(Tokens.TokenKind.RBRACKET);
      var1 = this.bracketsOpt(var1);
      var1 = (JCTree.JCExpression)this.toP(this.F.at(var2).TypeArray(var1));
      if (var3.nonEmpty()) {
         var1 = (JCTree.JCExpression)this.toP(this.F.at(var2).AnnotatedType(var3, var1));
      }

      return var1;
   }

   JCTree.JCExpression bracketsSuffix(JCTree.JCExpression var1) {
      if ((this.mode & 1) != 0 && this.token.kind == Tokens.TokenKind.DOT) {
         this.mode = 1;
         int var2 = this.token.pos;
         this.nextToken();
         this.accept(Tokens.TokenKind.CLASS);
         if (this.token.pos == this.endPosTable.errorEndPos) {
            Name var3;
            if (this.LAX_IDENTIFIER.accepts(this.token.kind)) {
               var3 = this.token.name();
               this.nextToken();
            } else {
               var3 = this.names.error;
            }

            var1 = this.F.at(var2).Erroneous(List.of(this.toP(this.F.at(var2).Select((JCTree.JCExpression)var1, (Name)var3))));
         } else {
            var1 = (JCTree.JCExpression)this.toP(this.F.at(var2).Select((JCTree.JCExpression)var1, (Name)this.names._class));
         }
      } else if ((this.mode & 2) != 0) {
         if (this.token.kind != Tokens.TokenKind.COLCOL) {
            this.mode = 2;
         }
      } else if (this.token.kind != Tokens.TokenKind.COLCOL) {
         this.syntaxError(this.token.pos, "dot.class.expected");
      }

      return (JCTree.JCExpression)var1;
   }

   JCTree.JCExpression memberReferenceSuffix(JCTree.JCExpression var1) {
      int var2 = this.token.pos;
      this.accept(Tokens.TokenKind.COLCOL);
      return this.memberReferenceSuffix(var2, var1);
   }

   JCTree.JCExpression memberReferenceSuffix(int var1, JCTree.JCExpression var2) {
      this.checkMethodReferences();
      this.mode = 1;
      List var3 = null;
      if (this.token.kind == Tokens.TokenKind.LT) {
         var3 = this.typeArguments(false);
      }

      Name var4;
      MemberReferenceTree.ReferenceMode var5;
      if (this.token.kind == Tokens.TokenKind.NEW) {
         var5 = MemberReferenceTree.ReferenceMode.NEW;
         var4 = this.names.init;
         this.nextToken();
      } else {
         var5 = MemberReferenceTree.ReferenceMode.INVOKE;
         var4 = this.ident();
      }

      return (JCTree.JCExpression)this.toP(this.F.at(var2.getStartPosition()).Reference(var5, var4, var2, var3));
   }

   JCTree.JCExpression creator(int var1, List var2) {
      List var3 = this.typeAnnotationsOpt();
      switch (this.token.kind) {
         case BYTE:
         case SHORT:
         case CHAR:
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
         case BOOLEAN:
            if (var2 == null) {
               if (var3.isEmpty()) {
                  return this.arrayCreatorRest(var1, this.basicType());
               }

               return this.arrayCreatorRest(var1, (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var3.head).pos).AnnotatedType(var3, this.basicType())));
            }
         default:
            Object var4 = this.qualident(true);
            int var5 = this.mode;
            this.mode = 2;
            boolean var6 = false;
            int var7 = -1;
            if (this.token.kind == Tokens.TokenKind.LT) {
               this.checkGenerics();
               var7 = this.token.pos;
               var4 = this.typeArguments((JCTree.JCExpression)var4, true);
               var6 = (this.mode & 16) != 0;
            }

            while(this.token.kind == Tokens.TokenKind.DOT) {
               if (var6) {
                  this.illegal();
               }

               int var8 = this.token.pos;
               this.nextToken();
               List var9 = this.typeAnnotationsOpt();
               var4 = (JCTree.JCExpression)this.toP(this.F.at(var8).Select((JCTree.JCExpression)var4, (Name)this.ident()));
               if (var9 != null && var9.nonEmpty()) {
                  var4 = (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var9.head).pos).AnnotatedType(var9, (JCTree.JCExpression)var4));
               }

               if (this.token.kind == Tokens.TokenKind.LT) {
                  var7 = this.token.pos;
                  this.checkGenerics();
                  var4 = this.typeArguments((JCTree.JCExpression)var4, true);
                  var6 = (this.mode & 16) != 0;
               }
            }

            this.mode = var5;
            if (this.token.kind != Tokens.TokenKind.LBRACKET && this.token.kind != Tokens.TokenKind.MONKEYS_AT) {
               JCTree.JCExpression var11;
               if (this.token.kind == Tokens.TokenKind.LPAREN) {
                  JCTree.JCNewClass var13 = this.classCreatorRest(var1, (JCTree.JCExpression)null, var2, (JCTree.JCExpression)var4);
                  if (var13.def != null) {
                     assert var13.def.mods.annotations.isEmpty();

                     if (var3.nonEmpty()) {
                        var13.def.mods.pos = earlier(var13.def.mods.pos, ((JCTree.JCAnnotation)var3.head).pos);
                        var13.def.mods.annotations = var3;
                     }
                  } else if (var3.nonEmpty()) {
                     var11 = this.insertAnnotationsToMostInner((JCTree.JCExpression)var4, var3, false);
                     var13.clazz = var11;
                  }

                  return var13;
               } else {
                  this.setErrorEndPos(this.token.pos);
                  this.reportSyntaxError(this.token.pos, "expected2", Tokens.TokenKind.LPAREN, Tokens.TokenKind.LBRACKET);
                  var11 = (JCTree.JCExpression)this.toP(this.F.at(var1).NewClass((JCTree.JCExpression)null, var2, (JCTree.JCExpression)var4, List.nil(), (JCTree.JCClassDecl)null));
                  return (JCTree.JCExpression)this.toP(this.F.at(var1).Erroneous(List.of(var11)));
               }
            } else {
               if (var3.nonEmpty()) {
                  var4 = this.insertAnnotationsToMostInner((JCTree.JCExpression)var4, var3, false);
               }

               JCTree.JCExpression var12 = this.arrayCreatorRest(var1, (JCTree.JCExpression)var4);
               if (var6) {
                  this.reportSyntaxError(var7, "cannot.create.array.with.diamond");
                  return (JCTree.JCExpression)this.toP(this.F.at(var1).Erroneous(List.of(var12)));
               } else if (var2 != null) {
                  int var14 = var1;
                  if (!var2.isEmpty() && ((JCTree.JCExpression)var2.head).pos != -1) {
                     var14 = ((JCTree.JCExpression)var2.head).pos;
                  }

                  this.setErrorEndPos(this.S.prevToken().endPos);
                  JCTree.JCErroneous var10 = this.F.at(var14).Erroneous(var2.prepend(var12));
                  this.reportSyntaxError(var10, "cannot.create.array.with.type.arguments");
                  return (JCTree.JCExpression)this.toP(var10);
               } else {
                  return var12;
               }
            }
      }
   }

   JCTree.JCExpression innerCreator(int var1, List var2, JCTree.JCExpression var3) {
      List var4 = this.typeAnnotationsOpt();
      Object var5 = (JCTree.JCExpression)this.toP(this.F.at(this.token.pos).Ident(this.ident()));
      if (var4.nonEmpty()) {
         var5 = (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCAnnotation)var4.head).pos).AnnotatedType(var4, (JCTree.JCExpression)var5));
      }

      if (this.token.kind == Tokens.TokenKind.LT) {
         int var6 = this.mode;
         this.checkGenerics();
         var5 = this.typeArguments((JCTree.JCExpression)var5, true);
         this.mode = var6;
      }

      return this.classCreatorRest(var1, var3, var2, (JCTree.JCExpression)var5);
   }

   JCTree.JCExpression arrayCreatorRest(int var1, JCTree.JCExpression var2) {
      List var3 = this.typeAnnotationsOpt();
      this.accept(Tokens.TokenKind.LBRACKET);
      if (this.token.kind == Tokens.TokenKind.RBRACKET) {
         this.accept(Tokens.TokenKind.RBRACKET);
         var2 = this.bracketsOpt(var2, var3);
         if (this.token.kind == Tokens.TokenKind.LBRACE) {
            JCTree.JCNewArray var9 = (JCTree.JCNewArray)this.arrayInitializer(var1, var2);
            if (var3.nonEmpty()) {
               JCTree.JCAnnotatedType var10 = (JCTree.JCAnnotatedType)var2;

               assert var10.annotations == var3;

               var9.annotations = var10.annotations;
               var9.elemtype = var10.underlyingType;
            }

            return var9;
         } else {
            JCTree.JCExpression var8 = (JCTree.JCExpression)this.toP(this.F.at(var1).NewArray(var2, List.nil(), (List)null));
            return this.syntaxError(this.token.pos, List.of(var8), "array.dimension.missing");
         }
      } else {
         ListBuffer var4 = new ListBuffer();
         ListBuffer var5 = new ListBuffer();
         var5.append(var3);
         var4.append(this.parseExpression());
         this.accept(Tokens.TokenKind.RBRACKET);

         while(this.token.kind == Tokens.TokenKind.LBRACKET || this.token.kind == Tokens.TokenKind.MONKEYS_AT) {
            List var6 = this.typeAnnotationsOpt();
            int var7 = this.token.pos;
            this.nextToken();
            if (this.token.kind == Tokens.TokenKind.RBRACKET) {
               var2 = this.bracketsOptCont(var2, var7, var6);
            } else if (this.token.kind == Tokens.TokenKind.RBRACKET) {
               var2 = this.bracketsOptCont(var2, var7, var6);
            } else {
               var5.append(var6);
               var4.append(this.parseExpression());
               this.accept(Tokens.TokenKind.RBRACKET);
            }
         }

         JCTree.JCNewArray var11 = (JCTree.JCNewArray)this.toP(this.F.at(var1).NewArray(var2, var4.toList(), (List)null));
         var11.dimAnnotations = var5.toList();
         return var11;
      }
   }

   JCTree.JCNewClass classCreatorRest(int var1, JCTree.JCExpression var2, List var3, JCTree.JCExpression var4) {
      List var5 = this.arguments();
      JCTree.JCClassDecl var6 = null;
      if (this.token.kind == Tokens.TokenKind.LBRACE) {
         int var7 = this.token.pos;
         List var8 = this.classOrInterfaceBody(this.names.empty, false);
         JCTree.JCModifiers var9 = this.F.at(-1).Modifiers(0L);
         var6 = (JCTree.JCClassDecl)this.toP(this.F.at(var7).AnonymousClassDef(var9, var8));
      }

      return (JCTree.JCNewClass)this.toP(this.F.at(var1).NewClass(var2, var3, var4, var5, var6));
   }

   JCTree.JCExpression arrayInitializer(int var1, JCTree.JCExpression var2) {
      this.accept(Tokens.TokenKind.LBRACE);
      ListBuffer var3 = new ListBuffer();
      if (this.token.kind == Tokens.TokenKind.COMMA) {
         this.nextToken();
      } else if (this.token.kind != Tokens.TokenKind.RBRACE) {
         var3.append(this.variableInitializer());

         while(this.token.kind == Tokens.TokenKind.COMMA) {
            this.nextToken();
            if (this.token.kind == Tokens.TokenKind.RBRACE) {
               break;
            }

            var3.append(this.variableInitializer());
         }
      }

      this.accept(Tokens.TokenKind.RBRACE);
      return (JCTree.JCExpression)this.toP(this.F.at(var1).NewArray(var2, List.nil(), var3.toList()));
   }

   public JCTree.JCExpression variableInitializer() {
      return this.token.kind == Tokens.TokenKind.LBRACE ? this.arrayInitializer(this.token.pos, (JCTree.JCExpression)null) : this.parseExpression();
   }

   JCTree.JCExpression parExpression() {
      int var1 = this.token.pos;
      this.accept(Tokens.TokenKind.LPAREN);
      JCTree.JCExpression var2 = this.parseExpression();
      this.accept(Tokens.TokenKind.RPAREN);
      return (JCTree.JCExpression)this.toP(this.F.at(var1).Parens(var2));
   }

   JCTree.JCBlock block(int var1, long var2) {
      this.accept(Tokens.TokenKind.LBRACE);
      List var4 = this.blockStatements();
      JCTree.JCBlock var5 = this.F.at(var1).Block(var2, var4);

      while(this.token.kind == Tokens.TokenKind.CASE || this.token.kind == Tokens.TokenKind.DEFAULT) {
         this.syntaxError("orphaned", this.token.kind);
         this.switchBlockStatementGroups();
      }

      var5.endpos = this.token.pos;
      this.accept(Tokens.TokenKind.RBRACE);
      return (JCTree.JCBlock)this.toP(var5);
   }

   public JCTree.JCBlock block() {
      return this.block(this.token.pos, 0L);
   }

   List blockStatements() {
      ListBuffer var1 = new ListBuffer();

      while(true) {
         List var2 = this.blockStatement();
         if (var2.isEmpty()) {
            return var1.toList();
         }

         if (this.token.pos <= this.endPosTable.errorEndPos) {
            this.skip(false, true, true, true);
         }

         var1.addAll(var2);
      }
   }

   JCTree.JCStatement parseStatementAsBlock() {
      int var1 = this.token.pos;
      List var2 = this.blockStatement();
      if (var2.isEmpty()) {
         JCTree.JCErroneous var6 = this.F.at(var1).Erroneous();
         this.error(var6, "illegal.start.of.stmt");
         return this.F.at(var1).Exec(var6);
      } else {
         JCTree.JCStatement var3 = (JCTree.JCStatement)var2.head;
         String var4 = null;
         switch (var3.getTag()) {
            case CLASSDEF:
               var4 = "class.not.allowed";
               break;
            case VARDEF:
               var4 = "variable.not.allowed";
         }

         if (var4 != null) {
            this.error(var3, var4);
            List var5 = List.of(this.F.at(var3.pos).Block(0L, var2));
            return (JCTree.JCStatement)this.toP(this.F.at(var1).Exec(this.F.at(var3.pos).Erroneous(var5)));
         } else {
            return var3;
         }
      }
   }

   List blockStatement() {
      int var1 = this.token.pos;
      Tokens.Comment var2;
      JCTree.JCModifiers var3;
      JCTree.JCExpression var4;
      switch (this.token.kind) {
         case SEMI:
         case LBRACE:
         case SYNCHRONIZED:
         case IF:
         case FOR:
         case WHILE:
         case DO:
         case TRY:
         case SWITCH:
         case RETURN:
         case THROW:
         case BREAK:
         case CONTINUE:
         case ELSE:
         case FINALLY:
         case CATCH:
            return List.of(this.parseStatement());
         case FINAL:
         case MONKEYS_AT:
            var2 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
            var3 = this.modifiersOpt();
            if (this.token.kind != Tokens.TokenKind.INTERFACE && this.token.kind != Tokens.TokenKind.CLASS && (!this.allowEnums || this.token.kind != Tokens.TokenKind.ENUM)) {
               var4 = this.parseType();
               ListBuffer var5 = this.variableDeclarators(var3, var4, new ListBuffer());
               this.storeEnd((JCTree)var5.last(), this.token.endPos);
               this.accept(Tokens.TokenKind.SEMI);
               return var5.toList();
            } else {
               return List.of(this.classOrInterfaceOrEnumDeclaration(var3, var2));
            }
         case ABSTRACT:
         case STRICTFP:
            var2 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
            var3 = this.modifiersOpt();
            return List.of(this.classOrInterfaceOrEnumDeclaration(var3, var2));
         case EOF:
         case RBRACE:
         case CASE:
         case DEFAULT:
            return List.nil();
         case CLASS:
         case INTERFACE:
            var2 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
            return List.of(this.classOrInterfaceOrEnumDeclaration(this.modifiersOpt(), var2));
         case ENUM:
         case ASSERT:
            if (this.allowEnums && this.token.kind == Tokens.TokenKind.ENUM) {
               this.error(this.token.pos, "local.enum");
               var2 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
               return List.of(this.classOrInterfaceOrEnumDeclaration(this.modifiersOpt(), var2));
            } else if (this.allowAsserts && this.token.kind == Tokens.TokenKind.ASSERT) {
               return List.of(this.parseStatement());
            }
         case PUBLIC:
         case IMPORT:
         case PRIVATE:
         case PROTECTED:
         case STATIC:
         case TRANSIENT:
         case NATIVE:
         case VOLATILE:
         case LT:
         case BYTE:
         case SHORT:
         case CHAR:
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
         case BOOLEAN:
         case VOID:
         case UNDERSCORE:
         case IDENTIFIER:
         case INTLITERAL:
         case LONGLITERAL:
         case FLOATLITERAL:
         case DOUBLELITERAL:
         case CHARLITERAL:
         case STRINGLITERAL:
         case TRUE:
         case FALSE:
         case NULL:
         case EQ:
         case PLUSEQ:
         case SUBEQ:
         case STAREQ:
         case SLASHEQ:
         case PERCENTEQ:
         case AMPEQ:
         case BAREQ:
         case CARETEQ:
         case LTLTEQ:
         case GTGTEQ:
         case GTGTGTEQ:
         case THIS:
         case SUPER:
         case NEW:
         case LBRACKET:
         case LPAREN:
         case DOT:
         case ELLIPSIS:
         case QUES:
         case PLUSPLUS:
         case SUBSUB:
         case BANG:
         case TILDE:
         case PLUS:
         case SUB:
         default:
            Tokens.Token var7 = this.token;
            var4 = this.term(3);
            if (this.token.kind == Tokens.TokenKind.COLON && var4.hasTag(JCTree.Tag.IDENT)) {
               this.nextToken();
               JCTree.JCStatement var10 = this.parseStatement();
               return List.of(this.F.at(var1).Labelled(var7.name(), var10));
            } else if ((this.lastmode & 2) != 0 && this.LAX_IDENTIFIER.accepts(this.token.kind)) {
               var1 = this.token.pos;
               JCTree.JCModifiers var9 = this.F.at(-1).Modifiers(0L);
               this.F.at(var1);
               ListBuffer var6 = this.variableDeclarators(var9, var4, new ListBuffer());
               this.storeEnd((JCTree)var6.last(), this.token.endPos);
               this.accept(Tokens.TokenKind.SEMI);
               return var6.toList();
            } else {
               JCTree.JCExpressionStatement var8 = (JCTree.JCExpressionStatement)this.to(this.F.at(var1).Exec(this.checkExprStat(var4)));
               this.accept(Tokens.TokenKind.SEMI);
               return List.of(var8);
            }
      }
   }

   public JCTree.JCStatement parseStatement() {
      int var1 = this.token.pos;
      JCTree.JCExpression var4;
      JCTree.JCExpression var5;
      Name var7;
      JCTree.JCExpression var8;
      List var11;
      JCTree.JCBlock var18;
      JCTree.JCExpression var20;
      JCTree.JCStatement var22;
      switch (this.token.kind) {
         case SEMI:
            this.nextToken();
            return (JCTree.JCStatement)this.toP(this.F.at(var1).Skip());
         case LBRACE:
            return this.block();
         case SYNCHRONIZED:
            this.nextToken();
            var8 = this.parExpression();
            var18 = this.block();
            return this.F.at(var1).Synchronized(var8, var18);
         case IF:
            this.nextToken();
            var8 = this.parExpression();
            var22 = this.parseStatementAsBlock();
            JCTree.JCStatement var23 = null;
            if (this.token.kind == Tokens.TokenKind.ELSE) {
               this.nextToken();
               var23 = this.parseStatementAsBlock();
            }

            return this.F.at(var1).If(var8, var22, var23);
         case FOR:
            this.nextToken();
            this.accept(Tokens.TokenKind.LPAREN);
            var11 = this.token.kind == Tokens.TokenKind.SEMI ? List.nil() : this.forInit();
            JCTree.JCStatement var29;
            if (var11.length() == 1 && ((JCTree.JCStatement)var11.head).hasTag(JCTree.Tag.VARDEF) && ((JCTree.JCVariableDecl)var11.head).init == null && this.token.kind == Tokens.TokenKind.COLON) {
               this.checkForeach();
               JCTree.JCVariableDecl var26 = (JCTree.JCVariableDecl)var11.head;
               this.accept(Tokens.TokenKind.COLON);
               var4 = this.parseExpression();
               this.accept(Tokens.TokenKind.RPAREN);
               var29 = this.parseStatementAsBlock();
               return this.F.at(var1).ForeachLoop(var26, var4, var29);
            }

            this.accept(Tokens.TokenKind.SEMI);
            var20 = this.token.kind == Tokens.TokenKind.SEMI ? null : this.parseExpression();
            this.accept(Tokens.TokenKind.SEMI);
            List var21 = this.token.kind == Tokens.TokenKind.RPAREN ? List.nil() : this.forUpdate();
            this.accept(Tokens.TokenKind.RPAREN);
            var29 = this.parseStatementAsBlock();
            return this.F.at(var1).ForLoop(var11, var20, var21, var29);
         case WHILE:
            this.nextToken();
            var8 = this.parExpression();
            var22 = this.parseStatementAsBlock();
            return this.F.at(var1).WhileLoop(var8, var22);
         case DO:
            this.nextToken();
            JCTree.JCStatement var19 = this.parseStatementAsBlock();
            this.accept(Tokens.TokenKind.WHILE);
            var20 = this.parExpression();
            JCTree.JCDoWhileLoop var17 = (JCTree.JCDoWhileLoop)this.to(this.F.at(var1).DoLoop(var19, var20));
            this.accept(Tokens.TokenKind.SEMI);
            return var17;
         case TRY:
            this.nextToken();
            var11 = List.nil();
            if (this.token.kind == Tokens.TokenKind.LPAREN) {
               this.checkTryWithResources();
               this.nextToken();
               var11 = this.resources();
               this.accept(Tokens.TokenKind.RPAREN);
            }

            var18 = this.block();
            ListBuffer var16 = new ListBuffer();
            JCTree.JCBlock var24 = null;
            if (this.token.kind != Tokens.TokenKind.CATCH && this.token.kind != Tokens.TokenKind.FINALLY) {
               if (this.allowTWR) {
                  if (var11.isEmpty()) {
                     this.error(var1, "try.without.catch.finally.or.resource.decls");
                  }
               } else {
                  this.error(var1, "try.without.catch.or.finally");
               }
            } else {
               while(this.token.kind == Tokens.TokenKind.CATCH) {
                  var16.append(this.catchClause());
               }

               if (this.token.kind == Tokens.TokenKind.FINALLY) {
                  this.nextToken();
                  var24 = this.block();
               }
            }

            return this.F.at(var1).Try(var11, var18, var16.toList(), var24);
         case SWITCH:
            this.nextToken();
            var8 = this.parExpression();
            this.accept(Tokens.TokenKind.LBRACE);
            List var15 = this.switchBlockStatementGroups();
            JCTree.JCSwitch var13 = (JCTree.JCSwitch)this.to(this.F.at(var1).Switch(var8, var15));
            this.accept(Tokens.TokenKind.RBRACE);
            return var13;
         case RETURN:
            this.nextToken();
            var8 = this.token.kind == Tokens.TokenKind.SEMI ? null : this.parseExpression();
            JCTree.JCReturn var14 = (JCTree.JCReturn)this.to(this.F.at(var1).Return(var8));
            this.accept(Tokens.TokenKind.SEMI);
            return var14;
         case THROW:
            this.nextToken();
            var8 = this.parseExpression();
            JCTree.JCThrow var12 = (JCTree.JCThrow)this.to(this.F.at(var1).Throw(var8));
            this.accept(Tokens.TokenKind.SEMI);
            return var12;
         case BREAK:
            this.nextToken();
            var7 = this.LAX_IDENTIFIER.accepts(this.token.kind) ? this.ident() : null;
            JCTree.JCBreak var10 = (JCTree.JCBreak)this.to(this.F.at(var1).Break(var7));
            this.accept(Tokens.TokenKind.SEMI);
            return var10;
         case CONTINUE:
            this.nextToken();
            var7 = this.LAX_IDENTIFIER.accepts(this.token.kind) ? this.ident() : null;
            JCTree.JCContinue var9 = (JCTree.JCContinue)this.to(this.F.at(var1).Continue(var7));
            this.accept(Tokens.TokenKind.SEMI);
            return var9;
         case ELSE:
            int var2 = this.token.pos;
            this.nextToken();
            return this.doRecover(var2, JavacParser.BasicErrorRecoveryAction.BLOCK_STMT, "else.without.if");
         case FINALLY:
            int var3 = this.token.pos;
            this.nextToken();
            return this.doRecover(var3, JavacParser.BasicErrorRecoveryAction.BLOCK_STMT, "finally.without.try");
         case CATCH:
            return this.doRecover(this.token.pos, JavacParser.BasicErrorRecoveryAction.CATCH_CLAUSE, "catch.without.try");
         case ASSERT:
            if (this.allowAsserts && this.token.kind == Tokens.TokenKind.ASSERT) {
               this.nextToken();
               var4 = this.parseExpression();
               var5 = null;
               if (this.token.kind == Tokens.TokenKind.COLON) {
                  this.nextToken();
                  var5 = this.parseExpression();
               }

               JCTree.JCAssert var6 = (JCTree.JCAssert)this.to(this.F.at(var1).Assert(var4, var5));
               this.accept(Tokens.TokenKind.SEMI);
               return var6;
            }
         case ENUM:
         default:
            Tokens.Token var28 = this.token;
            var5 = this.parseExpression();
            if (this.token.kind == Tokens.TokenKind.COLON && var5.hasTag(JCTree.Tag.IDENT)) {
               this.nextToken();
               JCTree.JCStatement var27 = this.parseStatement();
               return this.F.at(var1).Labelled(var28.name(), var27);
            } else {
               JCTree.JCExpressionStatement var25 = (JCTree.JCExpressionStatement)this.to(this.F.at(var1).Exec(this.checkExprStat(var5)));
               this.accept(Tokens.TokenKind.SEMI);
               return var25;
            }
      }
   }

   private JCTree.JCStatement doRecover(int var1, ErrorRecoveryAction var2, String var3) {
      int var4 = this.S.errPos();
      JCTree var5 = var2.doRecover(this);
      this.S.errPos(var4);
      return (JCTree.JCStatement)this.toP(this.F.Exec(this.syntaxError(var1, List.of(var5), var3)));
   }

   protected JCTree.JCCatch catchClause() {
      int var1 = this.token.pos;
      this.accept(Tokens.TokenKind.CATCH);
      this.accept(Tokens.TokenKind.LPAREN);
      JCTree.JCModifiers var2 = this.optFinal(8589934592L);
      List var3 = this.catchTypes();
      JCTree.JCExpression var4 = var3.size() > 1 ? (JCTree.JCExpression)this.toP(this.F.at(((JCTree.JCExpression)var3.head).getStartPosition()).TypeUnion(var3)) : (JCTree.JCExpression)var3.head;
      JCTree.JCVariableDecl var5 = this.variableDeclaratorId(var2, var4);
      this.accept(Tokens.TokenKind.RPAREN);
      JCTree.JCBlock var6 = this.block();
      return this.F.at(var1).Catch(var5, var6);
   }

   List catchTypes() {
      ListBuffer var1 = new ListBuffer();
      var1.add(this.parseType());

      while(this.token.kind == Tokens.TokenKind.BAR) {
         this.checkMulticatch();
         this.nextToken();
         var1.add(this.parseType());
      }

      return var1.toList();
   }

   List switchBlockStatementGroups() {
      ListBuffer var1 = new ListBuffer();

      while(true) {
         int var2 = this.token.pos;
         switch (this.token.kind) {
            case EOF:
            case RBRACE:
               return var1.toList();
            case CASE:
            case DEFAULT:
               var1.append(this.switchBlockStatementGroup());
               break;
            default:
               this.nextToken();
               this.syntaxError(var2, "expected3", Tokens.TokenKind.CASE, Tokens.TokenKind.DEFAULT, Tokens.TokenKind.RBRACE);
         }
      }
   }

   protected JCTree.JCCase switchBlockStatementGroup() {
      int var1 = this.token.pos;
      List var2;
      JCTree.JCCase var3;
      switch (this.token.kind) {
         case CASE:
            this.nextToken();
            JCTree.JCExpression var4 = this.parseExpression();
            this.accept(Tokens.TokenKind.COLON);
            var2 = this.blockStatements();
            var3 = this.F.at(var1).Case(var4, var2);
            if (var2.isEmpty()) {
               this.storeEnd(var3, this.S.prevToken().endPos);
            }

            return var3;
         case DEFAULT:
            this.nextToken();
            this.accept(Tokens.TokenKind.COLON);
            var2 = this.blockStatements();
            var3 = this.F.at(var1).Case((JCTree.JCExpression)null, var2);
            if (var2.isEmpty()) {
               this.storeEnd(var3, this.S.prevToken().endPos);
            }

            return var3;
         default:
            throw new AssertionError("should not reach here");
      }
   }

   ListBuffer moreStatementExpressions(int var1, JCTree.JCExpression var2, ListBuffer var3) {
      var3.append(this.toP(this.F.at(var1).Exec(this.checkExprStat(var2))));

      while(this.token.kind == Tokens.TokenKind.COMMA) {
         this.nextToken();
         var1 = this.token.pos;
         JCTree.JCExpression var4 = this.parseExpression();
         var3.append(this.toP(this.F.at(var1).Exec(this.checkExprStat(var4))));
      }

      return var3;
   }

   List forInit() {
      ListBuffer var1 = new ListBuffer();
      int var2 = this.token.pos;
      if (this.token.kind != Tokens.TokenKind.FINAL && this.token.kind != Tokens.TokenKind.MONKEYS_AT) {
         JCTree.JCExpression var3 = this.term(3);
         if ((this.lastmode & 2) != 0 && this.LAX_IDENTIFIER.accepts(this.token.kind)) {
            return this.variableDeclarators(this.mods(var2, 0L, List.nil()), var3, var1).toList();
         } else if ((this.lastmode & 2) != 0 && this.token.kind == Tokens.TokenKind.COLON) {
            this.error(var2, "bad.initializer", "for-loop");
            return List.of(this.F.at(var2).VarDef((JCTree.JCModifiers)null, (Name)null, var3, (JCTree.JCExpression)null));
         } else {
            return this.moreStatementExpressions(var2, var3, var1).toList();
         }
      } else {
         return this.variableDeclarators(this.optFinal(0L), this.parseType(), var1).toList();
      }
   }

   List forUpdate() {
      return this.moreStatementExpressions(this.token.pos, this.parseExpression(), new ListBuffer()).toList();
   }

   List annotationsOpt(JCTree.Tag var1) {
      if (this.token.kind != Tokens.TokenKind.MONKEYS_AT) {
         return List.nil();
      } else {
         ListBuffer var2 = new ListBuffer();
         int var3 = this.mode;

         while(this.token.kind == Tokens.TokenKind.MONKEYS_AT) {
            int var4 = this.token.pos;
            this.nextToken();
            var2.append(this.annotation(var4, var1));
         }

         this.lastmode = this.mode;
         this.mode = var3;
         List var5 = var2.toList();
         return var5;
      }
   }

   List typeAnnotationsOpt() {
      List var1 = this.annotationsOpt(JCTree.Tag.TYPE_ANNOTATION);
      return var1;
   }

   JCTree.JCModifiers modifiersOpt() {
      return this.modifiersOpt((JCTree.JCModifiers)null);
   }

   protected JCTree.JCModifiers modifiersOpt(JCTree.JCModifiers var1) {
      ListBuffer var4 = new ListBuffer();
      long var2;
      int var5;
      if (var1 == null) {
         var2 = 0L;
         var5 = this.token.pos;
      } else {
         var2 = var1.flags;
         var4.appendList(var1.annotations);
         var5 = var1.pos;
      }

      if (this.token.deprecatedFlag()) {
         var2 |= 131072L;
      }

      while(true) {
         long var7;
         switch (this.token.kind) {
            case PUBLIC:
               var7 = 1L;
               break;
            case FINAL:
               var7 = 16L;
               break;
            case ABSTRACT:
               var7 = 1024L;
               break;
            case MONKEYS_AT:
               var7 = 8192L;
               break;
            case PRIVATE:
               var7 = 2L;
               break;
            case PROTECTED:
               var7 = 4L;
               break;
            case STATIC:
               var7 = 8L;
               break;
            case TRANSIENT:
               var7 = 128L;
               break;
            case NATIVE:
               var7 = 256L;
               break;
            case VOLATILE:
               var7 = 64L;
               break;
            case SYNCHRONIZED:
               var7 = 32L;
               break;
            case STRICTFP:
               var7 = 2048L;
               break;
            case DEFAULT:
               this.checkDefaultMethods();
               var7 = 8796093022208L;
               break;
            case ERROR:
               var7 = 0L;
               this.nextToken();
               break;
            default:
               switch (this.token.kind) {
                  case INTERFACE:
                     var2 |= 512L;
                     break;
                  case ENUM:
                     var2 |= 16384L;
               }

               return this.mods(var5, var2, var4.toList());
         }

         if ((var2 & var7) != 0L) {
            this.error(this.token.pos, "repeated.modifier");
         }

         int var6 = this.token.pos;
         this.nextToken();
         if (var7 == 8192L) {
            this.checkAnnotations();
            if (this.token.kind != Tokens.TokenKind.INTERFACE) {
               JCTree.JCAnnotation var9 = this.annotation(var6, JCTree.Tag.ANNOTATION);
               if (var2 == 0L && var4.isEmpty()) {
                  var5 = var9.pos;
               }

               var4.append(var9);
               var7 = 0L;
            }
         }

         var2 |= var7;
      }
   }

   JCTree.JCModifiers mods(int var1, long var2, List var4) {
      if ((var2 & 8796093033983L) == 0L && var4.isEmpty()) {
         var1 = -1;
      }

      JCTree.JCModifiers var5 = this.F.at(var1).Modifiers(var2, var4);
      if (var1 != -1) {
         this.storeEnd(var5, this.S.prevToken().endPos);
      }

      return var5;
   }

   JCTree.JCAnnotation annotation(int var1, JCTree.Tag var2) {
      this.checkAnnotations();
      if (var2 == JCTree.Tag.TYPE_ANNOTATION) {
         this.checkTypeAnnotations();
      }

      JCTree.JCExpression var3 = this.qualident(false);
      List var4 = this.annotationFieldValuesOpt();
      JCTree.JCAnnotation var5;
      if (var2 == JCTree.Tag.ANNOTATION) {
         var5 = this.F.at(var1).Annotation(var3, var4);
      } else {
         if (var2 != JCTree.Tag.TYPE_ANNOTATION) {
            throw new AssertionError("Unhandled annotation kind: " + var2);
         }

         var5 = this.F.at(var1).TypeAnnotation(var3, var4);
      }

      this.storeEnd(var5, this.S.prevToken().endPos);
      return var5;
   }

   List annotationFieldValuesOpt() {
      return this.token.kind == Tokens.TokenKind.LPAREN ? this.annotationFieldValues() : List.nil();
   }

   List annotationFieldValues() {
      this.accept(Tokens.TokenKind.LPAREN);
      ListBuffer var1 = new ListBuffer();
      if (this.token.kind != Tokens.TokenKind.RPAREN) {
         var1.append(this.annotationFieldValue());

         while(this.token.kind == Tokens.TokenKind.COMMA) {
            this.nextToken();
            var1.append(this.annotationFieldValue());
         }
      }

      this.accept(Tokens.TokenKind.RPAREN);
      return var1.toList();
   }

   JCTree.JCExpression annotationFieldValue() {
      if (this.LAX_IDENTIFIER.accepts(this.token.kind)) {
         this.mode = 1;
         JCTree.JCExpression var1 = this.term1();
         if (var1.hasTag(JCTree.Tag.IDENT) && this.token.kind == Tokens.TokenKind.EQ) {
            int var2 = this.token.pos;
            this.accept(Tokens.TokenKind.EQ);
            JCTree.JCExpression var3 = this.annotationValue();
            return (JCTree.JCExpression)this.toP(this.F.at(var2).Assign(var1, var3));
         } else {
            return var1;
         }
      } else {
         return this.annotationValue();
      }
   }

   JCTree.JCExpression annotationValue() {
      int var1;
      switch (this.token.kind) {
         case MONKEYS_AT:
            var1 = this.token.pos;
            this.nextToken();
            return this.annotation(var1, JCTree.Tag.ANNOTATION);
         case LBRACE:
            var1 = this.token.pos;
            this.accept(Tokens.TokenKind.LBRACE);
            ListBuffer var2 = new ListBuffer();
            if (this.token.kind == Tokens.TokenKind.COMMA) {
               this.nextToken();
            } else if (this.token.kind != Tokens.TokenKind.RBRACE) {
               var2.append(this.annotationValue());

               while(this.token.kind == Tokens.TokenKind.COMMA) {
                  this.nextToken();
                  if (this.token.kind == Tokens.TokenKind.RBRACE) {
                     break;
                  }

                  var2.append(this.annotationValue());
               }
            }

            this.accept(Tokens.TokenKind.RBRACE);
            return (JCTree.JCExpression)this.toP(this.F.at(var1).NewArray((JCTree.JCExpression)null, List.nil(), var2.toList()));
         default:
            this.mode = 1;
            return this.term1();
      }
   }

   public ListBuffer variableDeclarators(JCTree.JCModifiers var1, JCTree.JCExpression var2, ListBuffer var3) {
      return this.variableDeclaratorsRest(this.token.pos, var1, var2, this.ident(), false, (Tokens.Comment)null, var3);
   }

   ListBuffer variableDeclaratorsRest(int var1, JCTree.JCModifiers var2, JCTree.JCExpression var3, Name var4, boolean var5, Tokens.Comment var6, ListBuffer var7) {
      var7.append(this.variableDeclaratorRest(var1, var2, var3, var4, var5, var6));

      while(this.token.kind == Tokens.TokenKind.COMMA) {
         this.storeEnd((JCTree)var7.last(), this.token.endPos);
         this.nextToken();
         var7.append(this.variableDeclarator(var2, var3, var5, var6));
      }

      return var7;
   }

   JCTree.JCVariableDecl variableDeclarator(JCTree.JCModifiers var1, JCTree.JCExpression var2, boolean var3, Tokens.Comment var4) {
      return this.variableDeclaratorRest(this.token.pos, var1, var2, this.ident(), var3, var4);
   }

   JCTree.JCVariableDecl variableDeclaratorRest(int var1, JCTree.JCModifiers var2, JCTree.JCExpression var3, Name var4, boolean var5, Tokens.Comment var6) {
      var3 = this.bracketsOpt(var3);
      JCTree.JCExpression var7 = null;
      if (this.token.kind == Tokens.TokenKind.EQ) {
         this.nextToken();
         var7 = this.variableInitializer();
      } else if (var5) {
         this.syntaxError(this.token.pos, "expected", Tokens.TokenKind.EQ);
      }

      JCTree.JCVariableDecl var8 = (JCTree.JCVariableDecl)this.toP(this.F.at(var1).VarDef(var2, var4, var3, var7));
      this.attach(var8, var6);
      return var8;
   }

   JCTree.JCVariableDecl variableDeclaratorId(JCTree.JCModifiers var1, JCTree.JCExpression var2) {
      return this.variableDeclaratorId(var1, var2, false);
   }

   JCTree.JCVariableDecl variableDeclaratorId(JCTree.JCModifiers var1, JCTree.JCExpression var2, boolean var3) {
      int var4 = this.token.pos;
      Name var5;
      if (var3 && this.token.kind == Tokens.TokenKind.UNDERSCORE) {
         this.log.error(var4, "underscore.as.identifier.in.lambda", new Object[0]);
         var5 = this.token.name();
         this.nextToken();
      } else if (this.allowThisIdent) {
         JCTree.JCExpression var6 = this.qualident(false);
         if (!var6.hasTag(JCTree.Tag.IDENT) || ((JCTree.JCIdent)var6).name == this.names._this) {
            if ((var1.flags & 17179869184L) != 0L) {
               this.log.error(this.token.pos, "varargs.and.receiver", new Object[0]);
            }

            if (this.token.kind == Tokens.TokenKind.LBRACKET) {
               this.log.error(this.token.pos, "array.and.receiver", new Object[0]);
            }

            return (JCTree.JCVariableDecl)this.toP(this.F.at(var4).ReceiverVarDef(var1, var6, var2));
         }

         var5 = ((JCTree.JCIdent)var6).name;
      } else {
         var5 = this.ident();
      }

      if ((var1.flags & 17179869184L) != 0L && this.token.kind == Tokens.TokenKind.LBRACKET) {
         this.log.error(this.token.pos, "varargs.and.old.array.syntax", new Object[0]);
      }

      var2 = this.bracketsOpt(var2);
      return (JCTree.JCVariableDecl)this.toP(this.F.at(var4).VarDef(var1, var5, var2, (JCTree.JCExpression)null));
   }

   List resources() {
      ListBuffer var1 = new ListBuffer();
      var1.append(this.resource());

      while(this.token.kind == Tokens.TokenKind.SEMI) {
         this.storeEnd((JCTree)var1.last(), this.token.endPos);
         int var2 = this.token.pos;
         this.nextToken();
         if (this.token.kind == Tokens.TokenKind.RPAREN) {
            break;
         }

         var1.append(this.resource());
      }

      return var1.toList();
   }

   protected JCTree resource() {
      JCTree.JCModifiers var1 = this.optFinal(16L);
      JCTree.JCExpression var2 = this.parseType();
      int var3 = this.token.pos;
      Name var4 = this.ident();
      return this.variableDeclaratorRest(var3, var1, var2, var4, true, (Tokens.Comment)null);
   }

   public JCTree.JCCompilationUnit parseCompilationUnit() {
      Tokens.Token var1 = this.token;
      JCTree.JCExpression var2 = null;
      JCTree.JCModifiers var3 = null;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      List var7 = List.nil();
      if (this.token.kind == Tokens.TokenKind.MONKEYS_AT) {
         var3 = this.modifiersOpt();
      }

      if (this.token.kind == Tokens.TokenKind.PACKAGE) {
         var6 = true;
         if (var3 != null) {
            this.checkNoMods(var3.flags);
            var7 = var3.annotations;
            var3 = null;
         }

         this.nextToken();
         var2 = this.qualident(false);
         this.accept(Tokens.TokenKind.SEMI);
      }

      ListBuffer var8 = new ListBuffer();
      boolean var9 = true;
      boolean var10 = true;

      while(this.token.kind != Tokens.TokenKind.EOF) {
         if (this.token.pos > 0 && this.token.pos <= this.endPosTable.errorEndPos) {
            this.skip(var9, false, false, false);
            if (this.token.kind == Tokens.TokenKind.EOF) {
               break;
            }
         }

         if (var9 && var3 == null && this.token.kind == Tokens.TokenKind.IMPORT) {
            var5 = true;
            var8.append(this.importDeclaration());
         } else {
            Tokens.Comment var11 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
            if (var10 && !var5 && !var6) {
               var11 = var1.comment(Tokens.Comment.CommentStyle.JAVADOC);
               var4 = true;
            }

            Object var12 = this.typeDeclaration(var3, var11);
            if (var12 instanceof JCTree.JCExpressionStatement) {
               var12 = ((JCTree.JCExpressionStatement)var12).expr;
            }

            var8.append(var12);
            if (var12 instanceof JCTree.JCClassDecl) {
               var9 = false;
            }

            var3 = null;
            var10 = false;
         }
      }

      JCTree.JCCompilationUnit var13 = this.F.at(var1.pos).TopLevel(var7, var2, var8.toList());
      if (!var4) {
         this.attach(var13, var1.comment(Tokens.Comment.CommentStyle.JAVADOC));
      }

      if (var8.isEmpty()) {
         this.storeEnd(var13, this.S.prevToken().endPos);
      }

      if (this.keepDocComments) {
         var13.docComments = this.docComments;
      }

      if (this.keepLineMap) {
         var13.lineMap = this.S.getLineMap();
      }

      this.endPosTable.setParser((JavacParser)null);
      var13.endPositions = this.endPosTable;
      return var13;
   }

   JCTree importDeclaration() {
      int var1 = this.token.pos;
      this.nextToken();
      boolean var2 = false;
      if (this.token.kind == Tokens.TokenKind.STATIC) {
         this.checkStaticImports();
         var2 = true;
         this.nextToken();
      }

      JCTree.JCExpression var3 = (JCTree.JCExpression)this.toP(this.F.at(this.token.pos).Ident(this.ident()));

      do {
         int var4 = this.token.pos;
         this.accept(Tokens.TokenKind.DOT);
         if (this.token.kind == Tokens.TokenKind.STAR) {
            var3 = (JCTree.JCExpression)this.to(this.F.at(var4).Select(var3, this.names.asterisk));
            this.nextToken();
            break;
         }

         var3 = (JCTree.JCExpression)this.toP(this.F.at(var4).Select(var3, this.ident()));
      } while(this.token.kind == Tokens.TokenKind.DOT);

      this.accept(Tokens.TokenKind.SEMI);
      return this.toP(this.F.at(var1).Import(var3, var2));
   }

   JCTree typeDeclaration(JCTree.JCModifiers var1, Tokens.Comment var2) {
      int var3 = this.token.pos;
      if (var1 == null && this.token.kind == Tokens.TokenKind.SEMI) {
         this.nextToken();
         return this.toP(this.F.at(var3).Skip());
      } else {
         return this.classOrInterfaceOrEnumDeclaration(this.modifiersOpt(var1), var2);
      }
   }

   JCTree.JCStatement classOrInterfaceOrEnumDeclaration(JCTree.JCModifiers var1, Tokens.Comment var2) {
      if (this.token.kind == Tokens.TokenKind.CLASS) {
         return this.classDeclaration(var1, var2);
      } else if (this.token.kind == Tokens.TokenKind.INTERFACE) {
         return this.interfaceDeclaration(var1, var2);
      } else {
         int var3;
         List var4;
         if (this.allowEnums) {
            if (this.token.kind == Tokens.TokenKind.ENUM) {
               return this.enumDeclaration(var1, var2);
            } else {
               var3 = this.token.pos;
               if (this.LAX_IDENTIFIER.accepts(this.token.kind)) {
                  var4 = List.of(var1, this.toP(this.F.at(var3).Ident(this.ident())));
                  this.setErrorEndPos(this.token.pos);
               } else {
                  var4 = List.of(var1);
               }

               return (JCTree.JCStatement)this.toP(this.F.Exec(this.syntaxError(var3, var4, "expected3", Tokens.TokenKind.CLASS, Tokens.TokenKind.INTERFACE, Tokens.TokenKind.ENUM)));
            }
         } else if (this.token.kind == Tokens.TokenKind.ENUM) {
            this.error(this.token.pos, "enums.not.supported.in.source", this.source.name);
            this.allowEnums = true;
            return this.enumDeclaration(var1, var2);
         } else {
            var3 = this.token.pos;
            if (this.LAX_IDENTIFIER.accepts(this.token.kind)) {
               var4 = List.of(var1, this.toP(this.F.at(var3).Ident(this.ident())));
               this.setErrorEndPos(this.token.pos);
            } else {
               var4 = List.of(var1);
            }

            return (JCTree.JCStatement)this.toP(this.F.Exec(this.syntaxError(var3, var4, "expected2", Tokens.TokenKind.CLASS, Tokens.TokenKind.INTERFACE)));
         }
      }
   }

   protected JCTree.JCClassDecl classDeclaration(JCTree.JCModifiers var1, Tokens.Comment var2) {
      int var3 = this.token.pos;
      this.accept(Tokens.TokenKind.CLASS);
      Name var4 = this.ident();
      List var5 = this.typeParametersOpt();
      JCTree.JCExpression var6 = null;
      if (this.token.kind == Tokens.TokenKind.EXTENDS) {
         this.nextToken();
         var6 = this.parseType();
      }

      List var7 = List.nil();
      if (this.token.kind == Tokens.TokenKind.IMPLEMENTS) {
         this.nextToken();
         var7 = this.typeList();
      }

      List var8 = this.classOrInterfaceBody(var4, false);
      JCTree.JCClassDecl var9 = (JCTree.JCClassDecl)this.toP(this.F.at(var3).ClassDef(var1, var4, var5, var6, var7, var8));
      this.attach(var9, var2);
      return var9;
   }

   protected JCTree.JCClassDecl interfaceDeclaration(JCTree.JCModifiers var1, Tokens.Comment var2) {
      int var3 = this.token.pos;
      this.accept(Tokens.TokenKind.INTERFACE);
      Name var4 = this.ident();
      List var5 = this.typeParametersOpt();
      List var6 = List.nil();
      if (this.token.kind == Tokens.TokenKind.EXTENDS) {
         this.nextToken();
         var6 = this.typeList();
      }

      List var7 = this.classOrInterfaceBody(var4, true);
      JCTree.JCClassDecl var8 = (JCTree.JCClassDecl)this.toP(this.F.at(var3).ClassDef(var1, var4, var5, (JCTree.JCExpression)null, var6, var7));
      this.attach(var8, var2);
      return var8;
   }

   protected JCTree.JCClassDecl enumDeclaration(JCTree.JCModifiers var1, Tokens.Comment var2) {
      int var3 = this.token.pos;
      this.accept(Tokens.TokenKind.ENUM);
      Name var4 = this.ident();
      List var5 = List.nil();
      if (this.token.kind == Tokens.TokenKind.IMPLEMENTS) {
         this.nextToken();
         var5 = this.typeList();
      }

      List var6 = this.enumBody(var4);
      var1.flags |= 16384L;
      JCTree.JCClassDecl var7 = (JCTree.JCClassDecl)this.toP(this.F.at(var3).ClassDef(var1, var4, List.nil(), (JCTree.JCExpression)null, var5, var6));
      this.attach(var7, var2);
      return var7;
   }

   List enumBody(Name var1) {
      this.accept(Tokens.TokenKind.LBRACE);
      ListBuffer var2 = new ListBuffer();
      if (this.token.kind == Tokens.TokenKind.COMMA) {
         this.nextToken();
      } else if (this.token.kind != Tokens.TokenKind.RBRACE && this.token.kind != Tokens.TokenKind.SEMI) {
         var2.append(this.enumeratorDeclaration(var1));

         while(this.token.kind == Tokens.TokenKind.COMMA) {
            this.nextToken();
            if (this.token.kind == Tokens.TokenKind.RBRACE || this.token.kind == Tokens.TokenKind.SEMI) {
               break;
            }

            var2.append(this.enumeratorDeclaration(var1));
         }

         if (this.token.kind != Tokens.TokenKind.SEMI && this.token.kind != Tokens.TokenKind.RBRACE) {
            var2.append(this.syntaxError(this.token.pos, "expected3", Tokens.TokenKind.COMMA, Tokens.TokenKind.RBRACE, Tokens.TokenKind.SEMI));
            this.nextToken();
         }
      }

      if (this.token.kind == Tokens.TokenKind.SEMI) {
         this.nextToken();

         while(this.token.kind != Tokens.TokenKind.RBRACE && this.token.kind != Tokens.TokenKind.EOF) {
            var2.appendList(this.classOrInterfaceBodyDeclaration(var1, false));
            if (this.token.pos <= this.endPosTable.errorEndPos) {
               this.skip(false, true, true, false);
            }
         }
      }

      this.accept(Tokens.TokenKind.RBRACE);
      return var2.toList();
   }

   JCTree enumeratorDeclaration(Name var1) {
      Tokens.Comment var2 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
      int var3 = 16409;
      if (this.token.deprecatedFlag()) {
         var3 |= 131072;
      }

      int var4 = this.token.pos;
      List var5 = this.annotationsOpt(JCTree.Tag.ANNOTATION);
      JCTree.JCModifiers var6 = this.F.at(var5.isEmpty() ? -1 : var4).Modifiers((long)var3, var5);
      List var7 = this.typeArgumentsOpt();
      int var8 = this.token.pos;
      Name var9 = this.ident();
      int var10 = this.token.pos;
      List var11 = this.token.kind == Tokens.TokenKind.LPAREN ? this.arguments() : List.nil();
      JCTree.JCClassDecl var12 = null;
      if (this.token.kind == Tokens.TokenKind.LBRACE) {
         JCTree.JCModifiers var13 = this.F.at(-1).Modifiers(16392L);
         List var14 = this.classOrInterfaceBody(this.names.empty, false);
         var12 = (JCTree.JCClassDecl)this.toP(this.F.at(var8).AnonymousClassDef(var13, var14));
      }

      if (var11.isEmpty() && var12 == null) {
         var10 = var8;
      }

      JCTree.JCIdent var16 = this.F.at(var8).Ident(var1);
      JCTree.JCNewClass var17 = this.F.at(var10).NewClass((JCTree.JCExpression)null, var7, var16, var11, var12);
      if (var10 != var8) {
         this.storeEnd(var17, this.S.prevToken().endPos);
      }

      var16 = this.F.at(var8).Ident(var1);
      JCTree var15 = this.toP(this.F.at(var4).VarDef(var6, var9, var16, var17));
      this.attach(var15, var2);
      return var15;
   }

   List typeList() {
      ListBuffer var1 = new ListBuffer();
      var1.append(this.parseType());

      while(this.token.kind == Tokens.TokenKind.COMMA) {
         this.nextToken();
         var1.append(this.parseType());
      }

      return var1.toList();
   }

   List classOrInterfaceBody(Name var1, boolean var2) {
      this.accept(Tokens.TokenKind.LBRACE);
      if (this.token.pos <= this.endPosTable.errorEndPos) {
         this.skip(false, true, false, false);
         if (this.token.kind == Tokens.TokenKind.LBRACE) {
            this.nextToken();
         }
      }

      ListBuffer var3 = new ListBuffer();

      while(this.token.kind != Tokens.TokenKind.RBRACE && this.token.kind != Tokens.TokenKind.EOF) {
         var3.appendList(this.classOrInterfaceBodyDeclaration(var1, var2));
         if (this.token.pos <= this.endPosTable.errorEndPos) {
            this.skip(false, true, true, false);
         }
      }

      this.accept(Tokens.TokenKind.RBRACE);
      return var3.toList();
   }

   protected List classOrInterfaceBodyDeclaration(Name var1, boolean var2) {
      if (this.token.kind == Tokens.TokenKind.SEMI) {
         this.nextToken();
         return List.nil();
      } else {
         Tokens.Comment var3 = this.token.comment(Tokens.Comment.CommentStyle.JAVADOC);
         int var4 = this.token.pos;
         JCTree.JCModifiers var5 = this.modifiersOpt();
         if (this.token.kind != Tokens.TokenKind.CLASS && this.token.kind != Tokens.TokenKind.INTERFACE && (!this.allowEnums || this.token.kind != Tokens.TokenKind.ENUM)) {
            if (this.token.kind == Tokens.TokenKind.LBRACE && !var2 && (var5.flags & 4095L & -9L) == 0L && var5.annotations.isEmpty()) {
               return List.of(this.block(var4, var5.flags));
            } else {
               var4 = this.token.pos;
               List var6 = this.typeParametersOpt();
               if (var6.nonEmpty() && var5.pos == -1) {
                  var5.pos = var4;
                  this.storeEnd(var5, var4);
               }

               List var7 = this.annotationsOpt(JCTree.Tag.ANNOTATION);
               if (var7.nonEmpty()) {
                  this.checkAnnotationsAfterTypeParams(((JCTree.JCAnnotation)var7.head).pos);
                  var5.annotations = var5.annotations.appendList(var7);
                  if (var5.pos == -1) {
                     var5.pos = ((JCTree.JCAnnotation)var5.annotations.head).pos;
                  }
               }

               Tokens.Token var8 = this.token;
               var4 = this.token.pos;
               boolean var10 = this.token.kind == Tokens.TokenKind.VOID;
               JCTree.JCExpression var9;
               if (var10) {
                  var9 = (JCTree.JCExpression)this.to(this.F.at(var4).TypeIdent(TypeTag.VOID));
                  this.nextToken();
               } else {
                  var9 = this.unannotatedType();
               }

               if (this.token.kind == Tokens.TokenKind.LPAREN && !var2 && var9.hasTag(JCTree.Tag.IDENT)) {
                  if (!var2 && var8.name() == var1) {
                     if (var7.nonEmpty()) {
                        this.illegal(((JCTree.JCAnnotation)var7.head).pos);
                     }
                  } else {
                     this.error(var4, "invalid.meth.decl.ret.type.req");
                  }

                  return List.of(this.methodDeclaratorRest(var4, var5, (JCTree.JCExpression)null, this.names.init, var6, var2, true, var3));
               } else {
                  var4 = this.token.pos;
                  Name var11 = this.ident();
                  if (this.token.kind == Tokens.TokenKind.LPAREN) {
                     return List.of(this.methodDeclaratorRest(var4, var5, var9, var11, var6, var2, var10, var3));
                  } else {
                     List var12;
                     if (!var10 && var6.isEmpty()) {
                        var12 = this.variableDeclaratorsRest(var4, var5, var9, var11, var2, var3, new ListBuffer()).toList();
                        this.storeEnd((JCTree)var12.last(), this.token.endPos);
                        this.accept(Tokens.TokenKind.SEMI);
                        return var12;
                     } else {
                        var4 = this.token.pos;
                        var12 = var10 ? List.of(this.toP(this.F.at(var4).MethodDef(var5, var11, var9, var6, List.nil(), List.nil(), (JCTree.JCBlock)null, (JCTree.JCExpression)null))) : null;
                        return List.of(this.syntaxError(this.token.pos, var12, "expected", Tokens.TokenKind.LPAREN));
                     }
                  }
               }
            }
         } else {
            return List.of(this.classOrInterfaceOrEnumDeclaration(var5, var3));
         }
      }
   }

   protected JCTree methodDeclaratorRest(int var1, JCTree.JCModifiers var2, JCTree.JCExpression var3, Name var4, List var5, boolean var6, boolean var7, Tokens.Comment var8) {
      if (var6 && (var2.flags & 8L) != 0L) {
         this.checkStaticInterfaceMethods();
      }

      JCTree.JCVariableDecl var9 = this.receiverParam;

      JCTree.JCMethodDecl var15;
      try {
         this.receiverParam = null;
         List var10 = this.formalParameters();
         if (!var7) {
            var3 = this.bracketsOpt(var3);
         }

         List var11 = List.nil();
         if (this.token.kind == Tokens.TokenKind.THROWS) {
            this.nextToken();
            var11 = this.qualidentList();
         }

         JCTree.JCBlock var12 = null;
         JCTree.JCExpression var13;
         if (this.token.kind == Tokens.TokenKind.LBRACE) {
            var12 = this.block();
            var13 = null;
         } else {
            if (this.token.kind == Tokens.TokenKind.DEFAULT) {
               this.accept(Tokens.TokenKind.DEFAULT);
               var13 = this.annotationValue();
            } else {
               var13 = null;
            }

            this.accept(Tokens.TokenKind.SEMI);
            if (this.token.pos <= this.endPosTable.errorEndPos) {
               this.skip(false, true, false, false);
               if (this.token.kind == Tokens.TokenKind.LBRACE) {
                  var12 = this.block();
               }
            }
         }

         JCTree.JCMethodDecl var14 = (JCTree.JCMethodDecl)this.toP(this.F.at(var1).MethodDef(var2, var4, var3, var5, this.receiverParam, var10, var11, var12, var13));
         this.attach(var14, var8);
         var15 = var14;
      } finally {
         this.receiverParam = var9;
      }

      return var15;
   }

   List qualidentList() {
      ListBuffer var1 = new ListBuffer();
      List var2 = this.typeAnnotationsOpt();
      JCTree.JCExpression var3 = this.qualident(true);
      JCTree.JCExpression var4;
      if (!var2.isEmpty()) {
         var4 = this.insertAnnotationsToMostInner(var3, var2, false);
         var1.append(var4);
      } else {
         var1.append(var3);
      }

      while(this.token.kind == Tokens.TokenKind.COMMA) {
         this.nextToken();
         var2 = this.typeAnnotationsOpt();
         var3 = this.qualident(true);
         if (!var2.isEmpty()) {
            var4 = this.insertAnnotationsToMostInner(var3, var2, false);
            var1.append(var4);
         } else {
            var1.append(var3);
         }
      }

      return var1.toList();
   }

   List typeParametersOpt() {
      if (this.token.kind != Tokens.TokenKind.LT) {
         return List.nil();
      } else {
         this.checkGenerics();
         ListBuffer var1 = new ListBuffer();
         this.nextToken();
         var1.append(this.typeParameter());

         while(this.token.kind == Tokens.TokenKind.COMMA) {
            this.nextToken();
            var1.append(this.typeParameter());
         }

         this.accept(Tokens.TokenKind.GT);
         return var1.toList();
      }
   }

   JCTree.JCTypeParameter typeParameter() {
      int var1 = this.token.pos;
      List var2 = this.typeAnnotationsOpt();
      Name var3 = this.ident();
      ListBuffer var4 = new ListBuffer();
      if (this.token.kind == Tokens.TokenKind.EXTENDS) {
         this.nextToken();
         var4.append(this.parseType());

         while(this.token.kind == Tokens.TokenKind.AMP) {
            this.nextToken();
            var4.append(this.parseType());
         }
      }

      return (JCTree.JCTypeParameter)this.toP(this.F.at(var1).TypeParameter(var3, var4.toList(), var2));
   }

   List formalParameters() {
      return this.formalParameters(false);
   }

   List formalParameters(boolean var1) {
      ListBuffer var2 = new ListBuffer();
      this.accept(Tokens.TokenKind.LPAREN);
      if (this.token.kind != Tokens.TokenKind.RPAREN) {
         this.allowThisIdent = true;
         JCTree.JCVariableDecl var3 = this.formalParameter(var1);
         if (var3.nameexpr != null) {
            this.receiverParam = var3;
         } else {
            var2.append(var3);
         }

         this.allowThisIdent = false;

         while((var3.mods.flags & 17179869184L) == 0L && this.token.kind == Tokens.TokenKind.COMMA) {
            this.nextToken();
            var2.append(var3 = this.formalParameter(var1));
         }
      }

      this.accept(Tokens.TokenKind.RPAREN);
      return var2.toList();
   }

   List implicitParameters(boolean var1) {
      if (var1) {
         this.accept(Tokens.TokenKind.LPAREN);
      }

      ListBuffer var2 = new ListBuffer();
      if (this.token.kind != Tokens.TokenKind.RPAREN && this.token.kind != Tokens.TokenKind.ARROW) {
         var2.append(this.implicitParameter());

         while(this.token.kind == Tokens.TokenKind.COMMA) {
            this.nextToken();
            var2.append(this.implicitParameter());
         }
      }

      if (var1) {
         this.accept(Tokens.TokenKind.RPAREN);
      }

      return var2.toList();
   }

   JCTree.JCModifiers optFinal(long var1) {
      JCTree.JCModifiers var3 = this.modifiersOpt();
      this.checkNoMods(var3.flags & -131089L);
      var3.flags |= var1;
      return var3;
   }

   private JCTree.JCExpression insertAnnotationsToMostInner(JCTree.JCExpression var1, List var2, boolean var3) {
      int var4 = this.getEndPos(var1);
      JCTree.JCExpression var5 = var1;

      JCTree.JCArrayTypeTree var6;
      for(var6 = null; TreeInfo.typeIn(var5).hasTag(JCTree.Tag.TYPEARRAY); var5 = var6.elemtype) {
         var6 = (JCTree.JCArrayTypeTree)TreeInfo.typeIn(var5);
      }

      if (var3) {
         var5 = (JCTree.JCExpression)this.to(this.F.at(this.token.pos).TypeArray(var5));
      }

      Object var7 = var5;
      if (var2.nonEmpty()) {
         JCTree.JCExpression var8 = var5;

         while(TreeInfo.typeIn(var5).hasTag(JCTree.Tag.SELECT) || TreeInfo.typeIn(var5).hasTag(JCTree.Tag.TYPEAPPLY)) {
            while(TreeInfo.typeIn(var5).hasTag(JCTree.Tag.SELECT)) {
               var8 = var5;
               var5 = ((JCTree.JCFieldAccess)TreeInfo.typeIn(var5)).getExpression();
            }

            while(TreeInfo.typeIn(var5).hasTag(JCTree.Tag.TYPEAPPLY)) {
               var8 = var5;
               var5 = ((JCTree.JCTypeApply)TreeInfo.typeIn(var5)).clazz;
            }
         }

         JCTree.JCAnnotatedType var9 = this.F.at(((JCTree.JCAnnotation)var2.head).pos).AnnotatedType(var2, var5);
         if (TreeInfo.typeIn(var8).hasTag(JCTree.Tag.TYPEAPPLY)) {
            ((JCTree.JCTypeApply)TreeInfo.typeIn(var8)).clazz = var9;
         } else if (TreeInfo.typeIn(var8).hasTag(JCTree.Tag.SELECT)) {
            ((JCTree.JCFieldAccess)TreeInfo.typeIn(var8)).selected = var9;
         } else {
            var7 = var9;
         }
      }

      if (var6 == null) {
         return (JCTree.JCExpression)var7;
      } else {
         var6.elemtype = (JCTree.JCExpression)var7;
         this.storeEnd(var1, var4);
         return var1;
      }
   }

   protected JCTree.JCVariableDecl formalParameter() {
      return this.formalParameter(false);
   }

   protected JCTree.JCVariableDecl formalParameter(boolean var1) {
      JCTree.JCModifiers var2 = this.optFinal(8589934592L);
      this.permitTypeAnnotationsPushBack = true;
      JCTree.JCExpression var3 = this.parseType();
      this.permitTypeAnnotationsPushBack = false;
      if (this.token.kind == Tokens.TokenKind.ELLIPSIS) {
         List var4 = this.typeAnnotationsPushedBack;
         this.typeAnnotationsPushedBack = List.nil();
         this.checkVarargs();
         var2.flags |= 17179869184L;
         var3 = this.insertAnnotationsToMostInner(var3, var4, true);
         this.nextToken();
      } else {
         if (this.typeAnnotationsPushedBack.nonEmpty()) {
            this.reportSyntaxError(((JCTree.JCAnnotation)this.typeAnnotationsPushedBack.head).pos, "illegal.start.of.type");
         }

         this.typeAnnotationsPushedBack = List.nil();
      }

      return this.variableDeclaratorId(var2, var3, var1);
   }

   protected JCTree.JCVariableDecl implicitParameter() {
      JCTree.JCModifiers var1 = this.F.at(this.token.pos).Modifiers(8589934592L);
      return this.variableDeclaratorId(var1, (JCTree.JCExpression)null, true);
   }

   void error(int var1, String var2, Object... var3) {
      this.log.error(JCDiagnostic.DiagnosticFlag.SYNTAX, var1, var2, var3);
   }

   void error(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      this.log.error(JCDiagnostic.DiagnosticFlag.SYNTAX, var1, var2, var3);
   }

   void warning(int var1, String var2, Object... var3) {
      this.log.warning(var1, var2, var3);
   }

   protected JCTree.JCExpression checkExprStat(JCTree.JCExpression var1) {
      if (!TreeInfo.isExpressionStatement(var1)) {
         JCTree.JCErroneous var2 = this.F.at(var1.pos).Erroneous(List.of(var1));
         this.error(var2, "not.stmt");
         return var2;
      } else {
         return var1;
      }
   }

   static int prec(Tokens.TokenKind var0) {
      JCTree.Tag var1 = optag(var0);
      return var1 != JCTree.Tag.NO_TAG ? TreeInfo.opPrec(var1) : -1;
   }

   static int earlier(int var0, int var1) {
      if (var0 == -1) {
         return var1;
      } else if (var1 == -1) {
         return var0;
      } else {
         return var0 < var1 ? var0 : var1;
      }
   }

   static JCTree.Tag optag(Tokens.TokenKind var0) {
      switch (var0) {
         case LT:
            return JCTree.Tag.LT;
         case BYTE:
         case SHORT:
         case CHAR:
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
         case BOOLEAN:
         case VOID:
         case UNDERSCORE:
         case IDENTIFIER:
         case CASE:
         case DEFAULT:
         case IF:
         case FOR:
         case WHILE:
         case DO:
         case TRY:
         case SWITCH:
         case RETURN:
         case THROW:
         case BREAK:
         case CONTINUE:
         case ELSE:
         case FINALLY:
         case CATCH:
         case INTLITERAL:
         case LONGLITERAL:
         case FLOATLITERAL:
         case DOUBLELITERAL:
         case CHARLITERAL:
         case STRINGLITERAL:
         case TRUE:
         case FALSE:
         case NULL:
         case EQ:
         case THIS:
         case SUPER:
         case NEW:
         case LBRACKET:
         case LPAREN:
         case DOT:
         case ELLIPSIS:
         case QUES:
         case PLUSPLUS:
         case SUBSUB:
         case BANG:
         case TILDE:
         case ASSERT:
         case RPAREN:
         case EXTENDS:
         case RBRACKET:
         case COMMA:
         case ERROR:
         default:
            return JCTree.Tag.NO_TAG;
         case PLUSEQ:
            return JCTree.Tag.PLUS_ASG;
         case SUBEQ:
            return JCTree.Tag.MINUS_ASG;
         case STAREQ:
            return JCTree.Tag.MUL_ASG;
         case SLASHEQ:
            return JCTree.Tag.DIV_ASG;
         case PERCENTEQ:
            return JCTree.Tag.MOD_ASG;
         case AMPEQ:
            return JCTree.Tag.BITAND_ASG;
         case BAREQ:
            return JCTree.Tag.BITOR_ASG;
         case CARETEQ:
            return JCTree.Tag.BITXOR_ASG;
         case LTLTEQ:
            return JCTree.Tag.SL_ASG;
         case GTGTEQ:
            return JCTree.Tag.SR_ASG;
         case GTGTGTEQ:
            return JCTree.Tag.USR_ASG;
         case PLUS:
            return JCTree.Tag.PLUS;
         case SUB:
            return JCTree.Tag.MINUS;
         case GTGTGT:
            return JCTree.Tag.USR;
         case GTGT:
            return JCTree.Tag.SR;
         case GT:
            return JCTree.Tag.GT;
         case AMP:
            return JCTree.Tag.BITAND;
         case GTEQ:
            return JCTree.Tag.GE;
         case BARBAR:
            return JCTree.Tag.OR;
         case AMPAMP:
            return JCTree.Tag.AND;
         case BAR:
            return JCTree.Tag.BITOR;
         case CARET:
            return JCTree.Tag.BITXOR;
         case EQEQ:
            return JCTree.Tag.EQ;
         case BANGEQ:
            return JCTree.Tag.NE;
         case LTEQ:
            return JCTree.Tag.LE;
         case LTLT:
            return JCTree.Tag.SL;
         case STAR:
            return JCTree.Tag.MUL;
         case SLASH:
            return JCTree.Tag.DIV;
         case PERCENT:
            return JCTree.Tag.MOD;
         case INSTANCEOF:
            return JCTree.Tag.TYPETEST;
      }
   }

   static JCTree.Tag unoptag(Tokens.TokenKind var0) {
      switch (var0) {
         case PLUSPLUS:
            return JCTree.Tag.PREINC;
         case SUBSUB:
            return JCTree.Tag.PREDEC;
         case BANG:
            return JCTree.Tag.NOT;
         case TILDE:
            return JCTree.Tag.COMPL;
         case PLUS:
            return JCTree.Tag.POS;
         case SUB:
            return JCTree.Tag.NEG;
         default:
            return JCTree.Tag.NO_TAG;
      }
   }

   static TypeTag typetag(Tokens.TokenKind var0) {
      switch (var0) {
         case BYTE:
            return TypeTag.BYTE;
         case SHORT:
            return TypeTag.SHORT;
         case CHAR:
            return TypeTag.CHAR;
         case INT:
            return TypeTag.INT;
         case LONG:
            return TypeTag.LONG;
         case FLOAT:
            return TypeTag.FLOAT;
         case DOUBLE:
            return TypeTag.DOUBLE;
         case BOOLEAN:
            return TypeTag.BOOLEAN;
         default:
            return TypeTag.NONE;
      }
   }

   void checkGenerics() {
      if (!this.allowGenerics) {
         this.error(this.token.pos, "generics.not.supported.in.source", this.source.name);
         this.allowGenerics = true;
      }

   }

   void checkVarargs() {
      if (!this.allowVarargs) {
         this.error(this.token.pos, "varargs.not.supported.in.source", this.source.name);
         this.allowVarargs = true;
      }

   }

   void checkForeach() {
      if (!this.allowForeach) {
         this.error(this.token.pos, "foreach.not.supported.in.source", this.source.name);
         this.allowForeach = true;
      }

   }

   void checkStaticImports() {
      if (!this.allowStaticImport) {
         this.error(this.token.pos, "static.import.not.supported.in.source", this.source.name);
         this.allowStaticImport = true;
      }

   }

   void checkAnnotations() {
      if (!this.allowAnnotations) {
         this.error(this.token.pos, "annotations.not.supported.in.source", this.source.name);
         this.allowAnnotations = true;
      }

   }

   void checkDiamond() {
      if (!this.allowDiamond) {
         this.error(this.token.pos, "diamond.not.supported.in.source", this.source.name);
         this.allowDiamond = true;
      }

   }

   void checkMulticatch() {
      if (!this.allowMulticatch) {
         this.error(this.token.pos, "multicatch.not.supported.in.source", this.source.name);
         this.allowMulticatch = true;
      }

   }

   void checkTryWithResources() {
      if (!this.allowTWR) {
         this.error(this.token.pos, "try.with.resources.not.supported.in.source", this.source.name);
         this.allowTWR = true;
      }

   }

   void checkLambda() {
      if (!this.allowLambda) {
         this.log.error(this.token.pos, "lambda.not.supported.in.source", new Object[]{this.source.name});
         this.allowLambda = true;
      }

   }

   void checkMethodReferences() {
      if (!this.allowMethodReferences) {
         this.log.error(this.token.pos, "method.references.not.supported.in.source", new Object[]{this.source.name});
         this.allowMethodReferences = true;
      }

   }

   void checkDefaultMethods() {
      if (!this.allowDefaultMethods) {
         this.log.error(this.token.pos, "default.methods.not.supported.in.source", new Object[]{this.source.name});
         this.allowDefaultMethods = true;
      }

   }

   void checkIntersectionTypesInCast() {
      if (!this.allowIntersectionTypesInCast) {
         this.log.error(this.token.pos, "intersection.types.in.cast.not.supported.in.source", new Object[]{this.source.name});
         this.allowIntersectionTypesInCast = true;
      }

   }

   void checkStaticInterfaceMethods() {
      if (!this.allowStaticInterfaceMethods) {
         this.log.error(this.token.pos, "static.intf.methods.not.supported.in.source", new Object[]{this.source.name});
         this.allowStaticInterfaceMethods = true;
      }

   }

   void checkTypeAnnotations() {
      if (!this.allowTypeAnnotations) {
         this.log.error(this.token.pos, "type.annotations.not.supported.in.source", new Object[]{this.source.name});
         this.allowTypeAnnotations = true;
      }

   }

   void checkAnnotationsAfterTypeParams(int var1) {
      if (!this.allowAnnotationsAfterTypeParams) {
         this.log.error(var1, "annotations.after.type.params.not.supported.in.source", new Object[]{this.source.name});
         this.allowAnnotationsAfterTypeParams = true;
      }

   }

   protected abstract static class AbstractEndPosTable implements EndPosTable {
      protected JavacParser parser;
      protected int errorEndPos;

      public AbstractEndPosTable(JavacParser var1) {
         this.parser = var1;
      }

      protected abstract JCTree to(JCTree var1);

      protected abstract JCTree toP(JCTree var1);

      protected void setErrorEndPos(int var1) {
         if (var1 > this.errorEndPos) {
            this.errorEndPos = var1;
         }

      }

      protected void setParser(JavacParser var1) {
         this.parser = var1;
      }
   }

   protected static class EmptyEndPosTable extends AbstractEndPosTable {
      EmptyEndPosTable(JavacParser var1) {
         super(var1);
      }

      public void storeEnd(JCTree var1, int var2) {
      }

      protected JCTree to(JCTree var1) {
         return var1;
      }

      protected JCTree toP(JCTree var1) {
         return var1;
      }

      public int getEndPos(JCTree var1) {
         return -1;
      }

      public int replaceTree(JCTree var1, JCTree var2) {
         return -1;
      }
   }

   protected static class SimpleEndPosTable extends AbstractEndPosTable {
      private final IntHashTable endPosMap = new IntHashTable();

      SimpleEndPosTable(JavacParser var1) {
         super(var1);
      }

      public void storeEnd(JCTree var1, int var2) {
         this.endPosMap.putAtIndex(var1, this.errorEndPos > var2 ? this.errorEndPos : var2, this.endPosMap.lookup(var1));
      }

      protected JCTree to(JCTree var1) {
         this.storeEnd(var1, this.parser.token.endPos);
         return var1;
      }

      protected JCTree toP(JCTree var1) {
         this.storeEnd(var1, this.parser.S.prevToken().endPos);
         return var1;
      }

      public int getEndPos(JCTree var1) {
         int var2 = this.endPosMap.getFromIndex(this.endPosMap.lookup(var1));
         return var2 == -1 ? -1 : var2;
      }

      public int replaceTree(JCTree var1, JCTree var2) {
         int var3 = this.endPosMap.remove(var1);
         if (var3 != -1) {
            this.storeEnd(var2, var3);
            return var3;
         } else {
            return -1;
         }
      }
   }

   static enum ParensResult {
      CAST,
      EXPLICIT_LAMBDA,
      IMPLICIT_LAMBDA,
      PARENS;
   }

   static enum BasicErrorRecoveryAction implements ErrorRecoveryAction {
      BLOCK_STMT {
         public JCTree doRecover(JavacParser var1) {
            return var1.parseStatementAsBlock();
         }
      },
      CATCH_CLAUSE {
         public JCTree doRecover(JavacParser var1) {
            return var1.catchClause();
         }
      };

      private BasicErrorRecoveryAction() {
      }

      // $FF: synthetic method
      BasicErrorRecoveryAction(Object var3) {
         this();
      }
   }

   interface ErrorRecoveryAction {
      JCTree doRecover(JavacParser var1);
   }
}
