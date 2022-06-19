package com.sun.xml.internal.xsom.impl.scd;

import com.sun.xml.internal.xsom.impl.UName;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;

public class SCDParser implements SCDParserConstants {
   private NamespaceContext nsc;
   public SCDParserTokenManager token_source;
   SimpleCharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private int jj_gen;
   private final int[] jj_la1;
   private static int[] jj_la1_0;
   private static int[] jj_la1_1;
   private Vector jj_expentries;
   private int[] jj_expentry;
   private int jj_kind;

   public SCDParser(String text, NamespaceContext nsc) {
      this((Reader)(new StringReader(text)));
      this.nsc = nsc;
   }

   private String trim(String s) {
      return s.substring(1, s.length() - 1);
   }

   private String resolvePrefix(String prefix) throws ParseException {
      try {
         String r = this.nsc.getNamespaceURI(prefix);
         if (prefix.equals("")) {
            return r;
         }

         if (!r.equals("")) {
            return r;
         }
      } catch (IllegalArgumentException var3) {
      }

      throw new ParseException("Unbound prefix: " + prefix);
   }

   public final UName QName() throws ParseException {
      Token l = null;
      Token p = this.jj_consume_token(12);
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 15:
            this.jj_consume_token(15);
            l = this.jj_consume_token(12);
            break;
         default:
            this.jj_la1[0] = this.jj_gen;
      }

      return l == null ? new UName(this.resolvePrefix(""), p.image) : new UName(this.resolvePrefix(p.image), l.image);
   }

   public final String Prefix() throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 12:
            Token p = this.jj_consume_token(12);
            return this.resolvePrefix(p.image);
         default:
            this.jj_la1[1] = this.jj_gen;
            return this.resolvePrefix("");
      }
   }

   public final List RelativeSchemaComponentPath() throws ParseException {
      ArrayList steps;
      steps = new ArrayList();
      label46:
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 16:
         case 17:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 16:
                  this.jj_consume_token(16);
                  steps.add(new Step.Any(Axis.ROOT));
                  break label46;
               case 17:
                  this.jj_consume_token(17);
                  steps.add(new Step.Any(Axis.DESCENDANTS));
                  break label46;
               default:
                  this.jj_la1[2] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }
         default:
            this.jj_la1[3] = this.jj_gen;
      }

      Step s = this.Step();
      steps.add(s);

      while(true) {
         switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 16:
            case 17:
               switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                  case 16:
                     this.jj_consume_token(16);
                     break;
                  case 17:
                     this.jj_consume_token(17);
                     steps.add(new Step.Any(Axis.DESCENDANTS));
                     break;
                  default:
                     this.jj_la1[5] = this.jj_gen;
                     this.jj_consume_token(-1);
                     throw new ParseException();
               }

               s = this.Step();
               steps.add(s);
               break;
            default:
               this.jj_la1[4] = this.jj_gen;
               return steps;
         }
      }
   }

   public final Step Step() throws ParseException {
      Object s;
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 12:
         case 20:
         case 45:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 20:
                  this.jj_consume_token(20);
                  break;
               default:
                  this.jj_la1[7] = this.jj_gen;
            }

            s = this.NameOrWildcard(Axis.ELEMENT);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[8] = this.jj_gen;
                  return (Step)s;
            }
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         default:
            this.jj_la1[16] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 18:
         case 19:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 18:
                  this.jj_consume_token(18);
                  break;
               case 19:
                  this.jj_consume_token(19);
                  break;
               default:
                  this.jj_la1[6] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }

            s = this.NameOrWildcard(Axis.ATTRIBUTE);
            break;
         case 21:
            this.jj_consume_token(21);
            s = this.NameOrWildcard(Axis.SUBSTITUTION_GROUP);
            break;
         case 22:
         case 23:
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 22:
                  this.jj_consume_token(22);
                  break;
               case 23:
                  this.jj_consume_token(23);
                  break;
               default:
                  this.jj_la1[9] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
            }

            s = this.NameOrWildcardOrAnonymous(Axis.TYPE_DEFINITION);
            break;
         case 24:
            this.jj_consume_token(24);
            s = this.NameOrWildcard(Axis.BASETYPE);
            break;
         case 25:
            this.jj_consume_token(25);
            s = this.NameOrWildcard(Axis.PRIMITIVE_TYPE);
            break;
         case 26:
            this.jj_consume_token(26);
            s = this.NameOrWildcardOrAnonymous(Axis.ITEM_TYPE);
            break;
         case 27:
            this.jj_consume_token(27);
            s = this.NameOrWildcardOrAnonymous(Axis.MEMBER_TYPE);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[10] = this.jj_gen;
                  return (Step)s;
            }
         case 28:
            this.jj_consume_token(28);
            s = this.NameOrWildcardOrAnonymous(Axis.SCOPE);
            break;
         case 29:
            this.jj_consume_token(29);
            s = this.NameOrWildcard(Axis.ATTRIBUTE_GROUP);
            break;
         case 30:
            this.jj_consume_token(30);
            s = this.NameOrWildcard(Axis.MODEL_GROUP_DECL);
            break;
         case 31:
            this.jj_consume_token(31);
            s = this.NameOrWildcard(Axis.IDENTITY_CONSTRAINT);
            break;
         case 32:
            this.jj_consume_token(32);
            s = this.NameOrWildcard(Axis.REFERENCED_KEY);
            break;
         case 33:
            this.jj_consume_token(33);
            s = this.NameOrWildcard(Axis.NOTATION);
            break;
         case 34:
            this.jj_consume_token(34);
            s = new Step.Any(Axis.MODELGROUP_SEQUENCE);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[11] = this.jj_gen;
                  return (Step)s;
            }
         case 35:
            this.jj_consume_token(35);
            s = new Step.Any(Axis.MODELGROUP_CHOICE);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[12] = this.jj_gen;
                  return (Step)s;
            }
         case 36:
            this.jj_consume_token(36);
            s = new Step.Any(Axis.MODELGROUP_ALL);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[13] = this.jj_gen;
                  return (Step)s;
            }
         case 37:
            this.jj_consume_token(37);
            s = new Step.Any(Axis.MODELGROUP_ANY);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[14] = this.jj_gen;
                  return (Step)s;
            }
         case 38:
            this.jj_consume_token(38);
            s = new Step.Any(Axis.WILDCARD);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 13:
                  this.Predicate((Step)s);
                  return (Step)s;
               default:
                  this.jj_la1[15] = this.jj_gen;
                  return (Step)s;
            }
         case 39:
            this.jj_consume_token(39);
            s = new Step.Any(Axis.ATTRIBUTE_WILDCARD);
            break;
         case 40:
            this.jj_consume_token(40);
            s = new Step.Any(Axis.FACET);
            break;
         case 41:
            this.jj_consume_token(41);
            Token n = this.jj_consume_token(14);
            s = new Step.Facet(Axis.FACET, n.image);
            break;
         case 42:
            this.jj_consume_token(42);
            s = new Step.Any(Axis.DESCENDANTS);
            break;
         case 43:
            this.jj_consume_token(43);
            String p = this.Prefix();
            s = new Step.Schema(Axis.X_SCHEMA, p);
            break;
         case 44:
            this.jj_consume_token(44);
            s = new Step.Any(Axis.X_SCHEMA);
      }

      return (Step)s;
   }

   public final Step NameOrWildcard(Axis a) throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 12:
            UName un = this.QName();
            return new Step.Named(a, un);
         case 45:
            this.jj_consume_token(45);
            return new Step.Any(a);
         default:
            this.jj_la1[17] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }
   }

   public final Step NameOrWildcardOrAnonymous(Axis a) throws ParseException {
      switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 12:
            UName un = this.QName();
            return new Step.Named(a, un);
         case 45:
            this.jj_consume_token(45);
            return new Step.Any(a);
         case 46:
            this.jj_consume_token(46);
            return new Step.AnonymousType(a);
         default:
            this.jj_la1[18] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
      }
   }

   public final int Predicate(Step s) throws ParseException {
      Token t = this.jj_consume_token(13);
      return s.predicate = Integer.parseInt(this.trim(t.image));
   }

   private static void jj_la1_0() {
      jj_la1_0 = new int[]{32768, 4096, 196608, 196608, 196608, 196608, 786432, 1048576, 8192, 12582912, 8192, 8192, 8192, 8192, 8192, 8192, -258048, 4096, 4096};
   }

   private static void jj_la1_1() {
      jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16383, 8192, 24576};
   }

   public SCDParser(InputStream stream) {
      this((InputStream)stream, (String)null);
   }

   public SCDParser(InputStream stream, String encoding) {
      this.jj_la1 = new int[19];
      this.jj_expentries = new Vector();
      this.jj_kind = -1;

      try {
         this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source = new SCDParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 19; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(InputStream stream) {
      this.ReInit(stream, (String)null);
   }

   public void ReInit(InputStream stream, String encoding) {
      try {
         this.jj_input_stream.ReInit(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 19; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public SCDParser(Reader stream) {
      this.jj_la1 = new int[19];
      this.jj_expentries = new Vector();
      this.jj_kind = -1;
      this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
      this.token_source = new SCDParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 19; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(Reader stream) {
      this.jj_input_stream.ReInit((Reader)stream, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 19; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public SCDParser(SCDParserTokenManager tm) {
      this.jj_la1 = new int[19];
      this.jj_expentries = new Vector();
      this.jj_kind = -1;
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 19; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(SCDParserTokenManager tm) {
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 19; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   private final Token jj_consume_token(int kind) throws ParseException {
      Token oldToken;
      if ((oldToken = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind == kind) {
         ++this.jj_gen;
         return this.token;
      } else {
         this.token = oldToken;
         this.jj_kind = kind;
         throw this.generateParseException();
      }
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

   public final Token getToken(int index) {
      Token t = this.token;

      for(int i = 0; i < index; ++i) {
         if (t.next != null) {
            t = t.next;
         } else {
            t = t.next = this.token_source.getNextToken();
         }
      }

      return t;
   }

   private final int jj_ntk() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   public ParseException generateParseException() {
      this.jj_expentries.removeAllElements();
      boolean[] la1tokens = new boolean[47];

      int i;
      for(i = 0; i < 47; ++i) {
         la1tokens[i] = false;
      }

      if (this.jj_kind >= 0) {
         la1tokens[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int j;
      for(i = 0; i < 19; ++i) {
         if (this.jj_la1[i] == this.jj_gen) {
            for(j = 0; j < 32; ++j) {
               if ((jj_la1_0[i] & 1 << j) != 0) {
                  la1tokens[j] = true;
               }

               if ((jj_la1_1[i] & 1 << j) != 0) {
                  la1tokens[32 + j] = true;
               }
            }
         }
      }

      for(i = 0; i < 47; ++i) {
         if (la1tokens[i]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.addElement(this.jj_expentry);
         }
      }

      int[][] exptokseq = new int[this.jj_expentries.size()][];

      for(j = 0; j < this.jj_expentries.size(); ++j) {
         exptokseq[j] = (int[])((int[])this.jj_expentries.elementAt(j));
      }

      return new ParseException(this.token, exptokseq, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   static {
      jj_la1_0();
      jj_la1_1();
   }
}
