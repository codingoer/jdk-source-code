package com.sun.tools.javadoc;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Type;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import java.lang.reflect.Modifier;

public class FieldDocImpl extends MemberDocImpl implements FieldDoc {
   protected final Symbol.VarSymbol sym;
   private String name;
   private String qualifiedName;

   public FieldDocImpl(DocEnv var1, Symbol.VarSymbol var2, TreePath var3) {
      super(var1, var2, var3);
      this.sym = var2;
   }

   public FieldDocImpl(DocEnv var1, Symbol.VarSymbol var2) {
      this(var1, var2, (TreePath)null);
   }

   protected long getFlags() {
      return this.sym.flags();
   }

   protected Symbol.ClassSymbol getContainingClass() {
      return this.sym.enclClass();
   }

   public Type type() {
      return TypeMaker.getType(this.env, this.sym.type, false);
   }

   public Object constantValue() {
      Object var1 = this.sym.getConstValue();
      if (var1 != null && this.sym.type.hasTag(TypeTag.BOOLEAN)) {
         var1 = (Integer)var1 != 0;
      }

      return var1;
   }

   public String constantValueExpression() {
      return constantValueExpression(this.constantValue());
   }

   static String constantValueExpression(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof Character) {
         return sourceForm((Character)var0);
      } else if (var0 instanceof Byte) {
         return sourceForm((Byte)var0);
      } else if (var0 instanceof String) {
         return sourceForm((String)var0);
      } else if (var0 instanceof Double) {
         return sourceForm((Double)var0, 'd');
      } else if (var0 instanceof Float) {
         return sourceForm(((Float)var0).doubleValue(), 'f');
      } else {
         return var0 instanceof Long ? var0 + "L" : var0.toString();
      }
   }

   private static String sourceForm(double var0, char var2) {
      if (Double.isNaN(var0)) {
         return "0" + var2 + "/0" + var2;
      } else if (var0 == Double.POSITIVE_INFINITY) {
         return "1" + var2 + "/0" + var2;
      } else {
         return var0 == Double.NEGATIVE_INFINITY ? "-1" + var2 + "/0" + var2 : var0 + (var2 != 'f' && var2 != 'F' ? "" : "" + var2);
      }
   }

   private static String sourceForm(char var0) {
      StringBuilder var1 = new StringBuilder(8);
      var1.append('\'');
      sourceChar(var0, var1);
      var1.append('\'');
      return var1.toString();
   }

   private static String sourceForm(byte var0) {
      return "0x" + Integer.toString(var0 & 255, 16);
   }

   private static String sourceForm(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() + 5);
      var1.append('"');

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         sourceChar(var3, var1);
      }

      var1.append('"');
      return var1.toString();
   }

   private static void sourceChar(char var0, StringBuilder var1) {
      switch (var0) {
         case '\b':
            var1.append("\\b");
            return;
         case '\t':
            var1.append("\\t");
            return;
         case '\n':
            var1.append("\\n");
            return;
         case '\f':
            var1.append("\\f");
            return;
         case '\r':
            var1.append("\\r");
            return;
         case '"':
            var1.append("\\\"");
            return;
         case '\'':
            var1.append("\\'");
            return;
         case '\\':
            var1.append("\\\\");
            return;
         default:
            if (isPrintableAscii(var0)) {
               var1.append(var0);
            } else {
               unicodeEscape(var0, var1);
            }
      }
   }

   private static void unicodeEscape(char var0, StringBuilder var1) {
      var1.append("\\u");
      var1.append("0123456789abcdef".charAt(15 & var0 >> 12));
      var1.append("0123456789abcdef".charAt(15 & var0 >> 8));
      var1.append("0123456789abcdef".charAt(15 & var0 >> 4));
      var1.append("0123456789abcdef".charAt(15 & var0 >> 0));
   }

   private static boolean isPrintableAscii(char var0) {
      return var0 >= ' ' && var0 <= '~';
   }

   public boolean isIncluded() {
      return this.containingClass().isIncluded() && this.env.shouldDocument(this.sym);
   }

   public boolean isField() {
      return !this.isEnumConstant();
   }

   public boolean isEnumConstant() {
      return (this.getFlags() & 16384L) != 0L && !this.env.legacyDoclet;
   }

   public boolean isTransient() {
      return Modifier.isTransient(this.getModifiers());
   }

   public boolean isVolatile() {
      return Modifier.isVolatile(this.getModifiers());
   }

   public boolean isSynthetic() {
      return (this.getFlags() & 4096L) != 0L;
   }

   public SerialFieldTag[] serialFieldTags() {
      return this.comment().serialFieldTags();
   }

   public String name() {
      if (this.name == null) {
         this.name = this.sym.name.toString();
      }

      return this.name;
   }

   public String qualifiedName() {
      if (this.qualifiedName == null) {
         this.qualifiedName = this.sym.enclClass().getQualifiedName() + "." + this.name();
      }

      return this.qualifiedName;
   }

   public SourcePosition position() {
      return this.sym.enclClass().sourcefile == null ? null : SourcePositionImpl.make(this.sym.enclClass().sourcefile, this.tree == null ? 0 : this.tree.pos, this.lineMap);
   }
}
