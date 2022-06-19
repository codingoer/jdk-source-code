package sun.tools.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public final class BinaryConstantPool implements Constants {
   private byte[] types;
   private Object[] cpool;
   Hashtable indexHashObject;
   Hashtable indexHashAscii;
   Vector MoreStuff;

   BinaryConstantPool(DataInputStream var1) throws IOException {
      this.types = new byte[var1.readUnsignedShort()];
      this.cpool = new Object[this.types.length];

      for(int var2 = 1; var2 < this.cpool.length; ++var2) {
         switch (this.types[var2] = var1.readByte()) {
            case 0:
            case 2:
            case 13:
            case 14:
            case 17:
            default:
               throw new ClassFormatError("invalid constant type: " + this.types[var2]);
            case 1:
               this.cpool[var2] = var1.readUTF();
               break;
            case 3:
               this.cpool[var2] = new Integer(var1.readInt());
               break;
            case 4:
               this.cpool[var2] = new Float(var1.readFloat());
               break;
            case 5:
               this.cpool[var2++] = new Long(var1.readLong());
               break;
            case 6:
               this.cpool[var2++] = new Double(var1.readDouble());
               break;
            case 7:
            case 8:
               this.cpool[var2] = new Integer(var1.readUnsignedShort());
               break;
            case 9:
            case 10:
            case 11:
            case 12:
               this.cpool[var2] = new Integer(var1.readUnsignedShort() << 16 | var1.readUnsignedShort());
               break;
            case 15:
               this.cpool[var2] = this.readBytes(var1, 3);
               break;
            case 16:
               this.cpool[var2] = this.readBytes(var1, 2);
               break;
            case 18:
               this.cpool[var2] = this.readBytes(var1, 4);
         }
      }

   }

   private byte[] readBytes(DataInputStream var1, int var2) throws IOException {
      byte[] var3 = new byte[var2];
      var1.readFully(var3);
      return var3;
   }

   public int getInteger(int var1) {
      return var1 == 0 ? 0 : ((Number)this.cpool[var1]).intValue();
   }

   public Object getValue(int var1) {
      return var1 == 0 ? null : this.cpool[var1];
   }

   public String getString(int var1) {
      return var1 == 0 ? null : (String)this.cpool[var1];
   }

   public Identifier getIdentifier(int var1) {
      return var1 == 0 ? null : Identifier.lookup(this.getString(var1));
   }

   public ClassDeclaration getDeclarationFromName(Environment var1, int var2) {
      return var2 == 0 ? null : var1.getClassDeclaration(Identifier.lookup(this.getString(var2).replace('/', '.')));
   }

   public ClassDeclaration getDeclaration(Environment var1, int var2) {
      return var2 == 0 ? null : this.getDeclarationFromName(var1, this.getInteger(var2));
   }

   public Type getType(int var1) {
      return Type.tType(this.getString(var1));
   }

   public int getConstantType(int var1) {
      return this.types[var1];
   }

   public Object getConstant(int var1, Environment var2) {
      int var3 = this.getConstantType(var1);
      switch (var3) {
         case 3:
         case 4:
         case 5:
         case 6:
         case 15:
         case 16:
         case 18:
            return this.getValue(var1);
         case 7:
            return this.getDeclaration(var2, var1);
         case 8:
            return this.getString(this.getInteger(var1));
         case 9:
         case 10:
         case 11:
            try {
               int var4 = this.getInteger(var1);
               ClassDefinition var5 = this.getDeclaration(var2, var4 >> 16).getClassDefinition(var2);
               int var6 = this.getInteger(var4 & '\uffff');
               Identifier var7 = this.getIdentifier(var6 >> 16);
               Type var8 = this.getType(var6 & '\uffff');
               MemberDefinition var9 = var5.getFirstMatch(var7);

               while(true) {
                  if (var9 == null) {
                     return null;
                  }

                  Type var10 = var9.getType();
                  if (var3 == 9) {
                     if (var10 == var8) {
                        break;
                     }
                  } else if (var10.equalArguments(var8)) {
                     break;
                  }

                  var9 = var9.getNextMatch();
               }

               return var9;
            } catch (ClassNotFound var11) {
               return null;
            }
         case 12:
         case 13:
         case 14:
         case 17:
         default:
            throw new ClassFormatError("invalid constant type: " + var3);
      }
   }

   public Vector getDependencies(Environment var1) {
      Vector var2 = new Vector();
      int var3 = 1;

      while(var3 < this.cpool.length) {
         switch (this.types[var3]) {
            case 7:
               var2.addElement(this.getDeclarationFromName(var1, this.getInteger(var3)));
            default:
               ++var3;
         }
      }

      return var2;
   }

   public int indexObject(Object var1, Environment var2) {
      if (this.indexHashObject == null) {
         this.createIndexHash(var2);
      }

      Integer var3 = (Integer)this.indexHashObject.get(var1);
      if (var3 == null) {
         throw new IndexOutOfBoundsException("Cannot find object " + var1 + " of type " + var1.getClass() + " in constant pool");
      } else {
         return var3;
      }
   }

   public int indexString(String var1, Environment var2) {
      if (this.indexHashObject == null) {
         this.createIndexHash(var2);
      }

      Integer var3 = (Integer)this.indexHashAscii.get(var1);
      if (var3 == null) {
         if (this.MoreStuff == null) {
            this.MoreStuff = new Vector();
         }

         var3 = new Integer(this.cpool.length + this.MoreStuff.size());
         this.MoreStuff.addElement(var1);
         this.indexHashAscii.put(var1, var3);
      }

      return var3;
   }

   public void createIndexHash(Environment var1) {
      this.indexHashObject = new Hashtable();
      this.indexHashAscii = new Hashtable();

      for(int var2 = 1; var2 < this.cpool.length; ++var2) {
         if (this.types[var2] == 1) {
            this.indexHashAscii.put(this.cpool[var2], new Integer(var2));
         } else {
            try {
               this.indexHashObject.put(this.getConstant(var2, var1), new Integer(var2));
            } catch (ClassFormatError var4) {
            }
         }
      }

   }

   public void write(DataOutputStream var1, Environment var2) throws IOException {
      int var3 = this.cpool.length;
      if (this.MoreStuff != null) {
         var3 += this.MoreStuff.size();
      }

      var1.writeShort(var3);

      int var4;
      for(var4 = 1; var4 < this.cpool.length; ++var4) {
         byte var5 = this.types[var4];
         Object var6 = this.cpool[var4];
         var1.writeByte(var5);
         switch (var5) {
            case 1:
               var1.writeUTF((String)var6);
               break;
            case 2:
            case 13:
            case 14:
            case 17:
            default:
               throw new ClassFormatError("invalid constant type: " + this.types[var4]);
            case 3:
               var1.writeInt(((Number)var6).intValue());
               break;
            case 4:
               var1.writeFloat(((Number)var6).floatValue());
               break;
            case 5:
               var1.writeLong(((Number)var6).longValue());
               ++var4;
               break;
            case 6:
               var1.writeDouble(((Number)var6).doubleValue());
               ++var4;
               break;
            case 7:
            case 8:
               var1.writeShort(((Number)var6).intValue());
               break;
            case 9:
            case 10:
            case 11:
            case 12:
               int var7 = ((Number)var6).intValue();
               var1.writeShort(var7 >> 16);
               var1.writeShort(var7 & '\uffff');
               break;
            case 15:
            case 16:
            case 18:
               var1.write((byte[])((byte[])var6), 0, ((byte[])((byte[])var6)).length);
         }
      }

      for(var4 = this.cpool.length; var4 < var3; ++var4) {
         String var8 = (String)((String)this.MoreStuff.elementAt(var4 - this.cpool.length));
         var1.writeByte(1);
         var1.writeUTF(var8);
      }

   }
}
