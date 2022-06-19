package sun.tools.java;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import sun.tools.tree.BooleanExpression;
import sun.tools.tree.DoubleExpression;
import sun.tools.tree.Expression;
import sun.tools.tree.FloatExpression;
import sun.tools.tree.IntExpression;
import sun.tools.tree.LocalMember;
import sun.tools.tree.LongExpression;
import sun.tools.tree.Node;
import sun.tools.tree.StringExpression;

public final class BinaryMember extends MemberDefinition {
   Expression value;
   BinaryAttribute atts;
   private boolean isConstantCache = false;
   private boolean isConstantCached = false;

   public BinaryMember(ClassDefinition var1, int var2, Type var3, Identifier var4, BinaryAttribute var5) {
      super(0L, var1, var2, var3, var4, (IdentifierToken[])null, (Node)null);
      this.atts = var5;
      if (this.getAttribute(idDeprecated) != null) {
         this.modifiers |= 262144;
      }

      if (this.getAttribute(idSynthetic) != null) {
         this.modifiers |= 524288;
      }

   }

   public BinaryMember(ClassDefinition var1) {
      super(var1);
   }

   public boolean isInlineable(Environment var1, boolean var2) {
      return this.isConstructor() && this.getClassDefinition().getSuperClass() == null;
   }

   public Vector getArguments() {
      if (this.isConstructor() && this.getClassDefinition().getSuperClass() == null) {
         Vector var1 = new Vector();
         var1.addElement(new LocalMember(0L, this.getClassDefinition(), 0, this.getClassDefinition().getType(), idThis));
         return var1;
      } else {
         return null;
      }
   }

   public ClassDeclaration[] getExceptions(Environment var1) {
      if (this.isMethod() && this.exp == null) {
         byte[] var2 = this.getAttribute(idExceptions);
         if (var2 == null) {
            return new ClassDeclaration[0];
         } else {
            try {
               BinaryConstantPool var3 = ((BinaryClass)this.getClassDefinition()).getConstants();
               DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(var2));
               int var5 = var4.readUnsignedShort();
               this.exp = new ClassDeclaration[var5];

               for(int var6 = 0; var6 < var5; ++var6) {
                  this.exp[var6] = var3.getDeclaration(var1, var4.readUnsignedShort());
               }

               return this.exp;
            } catch (IOException var7) {
               throw new CompilerError(var7);
            }
         }
      } else {
         return this.exp;
      }
   }

   public String getDocumentation() {
      if (this.documentation != null) {
         return this.documentation;
      } else {
         byte[] var1 = this.getAttribute(idDocumentation);
         if (var1 == null) {
            return null;
         } else {
            try {
               return this.documentation = (new DataInputStream(new ByteArrayInputStream(var1))).readUTF();
            } catch (IOException var3) {
               throw new CompilerError(var3);
            }
         }
      }
   }

   public boolean isConstant() {
      if (!this.isConstantCached) {
         this.isConstantCache = this.isFinal() && this.isVariable() && this.getAttribute(idConstantValue) != null;
         this.isConstantCached = true;
      }

      return this.isConstantCache;
   }

   public Node getValue(Environment var1) {
      if (this.isMethod()) {
         return null;
      } else if (!this.isFinal()) {
         return null;
      } else if (this.getValue() != null) {
         return (Expression)this.getValue();
      } else {
         byte[] var2 = this.getAttribute(idConstantValue);
         if (var2 == null) {
            return null;
         } else {
            try {
               BinaryConstantPool var3 = ((BinaryClass)this.getClassDefinition()).getConstants();
               Object var4 = var3.getValue((new DataInputStream(new ByteArrayInputStream(var2))).readUnsignedShort());
               switch (this.getType().getTypeCode()) {
                  case 0:
                     this.setValue(new BooleanExpression(0L, ((Number)var4).intValue() != 0));
                     break;
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                     this.setValue(new IntExpression(0L, ((Number)var4).intValue()));
                     break;
                  case 5:
                     this.setValue(new LongExpression(0L, ((Number)var4).longValue()));
                     break;
                  case 6:
                     this.setValue(new FloatExpression(0L, ((Number)var4).floatValue()));
                     break;
                  case 7:
                     this.setValue(new DoubleExpression(0L, ((Number)var4).doubleValue()));
                  case 8:
                  case 9:
                  default:
                     break;
                  case 10:
                     this.setValue(new StringExpression(0L, (String)var3.getValue(((Number)var4).intValue())));
               }

               return (Expression)this.getValue();
            } catch (IOException var5) {
               throw new CompilerError(var5);
            }
         }
      }
   }

   public byte[] getAttribute(Identifier var1) {
      for(BinaryAttribute var2 = this.atts; var2 != null; var2 = var2.next) {
         if (var2.name.equals(var1)) {
            return var2.data;
         }
      }

      return null;
   }

   public boolean deleteAttribute(Identifier var1) {
      BinaryAttribute var2 = null;
      BinaryAttribute var3 = null;

      boolean var4;
      for(var4 = false; this.atts.name.equals(var1); var4 = true) {
         this.atts = this.atts.next;
      }

      for(var2 = this.atts; var2 != null; var2 = var3) {
         var3 = var2.next;
         if (var3 != null && var3.name.equals(var1)) {
            var2.next = var3.next;
            var3 = var3.next;
            var4 = true;
         }
      }

      for(var2 = this.atts; var2 != null; var2 = var2.next) {
         if (var2.name.equals(var1)) {
            throw new InternalError("Found attribute " + var1);
         }
      }

      return var4;
   }

   public void addAttribute(Identifier var1, byte[] var2, Environment var3) {
      this.atts = new BinaryAttribute(var1, var2, this.atts);
      ((BinaryClass)((BinaryClass)this.clazz)).cpool.indexString(var1.toString(), var3);
   }
}
