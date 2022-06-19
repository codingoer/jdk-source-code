package com.sun.tools.jdi;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.Field;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.InternalException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Location;
import com.sun.jdi.LongValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ShortValue;
import com.sun.jdi.Value;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class PacketStream {
   final VirtualMachineImpl vm;
   private int inCursor = 0;
   final Packet pkt;
   private ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
   private boolean isCommitted = false;

   PacketStream(VirtualMachineImpl var1, int var2, int var3) {
      this.vm = var1;
      this.pkt = new Packet();
      this.pkt.cmdSet = (short)var2;
      this.pkt.cmd = (short)var3;
   }

   PacketStream(VirtualMachineImpl var1, Packet var2) {
      this.vm = var1;
      this.pkt = var2;
      this.isCommitted = true;
   }

   int id() {
      return this.pkt.id;
   }

   void send() {
      if (!this.isCommitted) {
         this.pkt.data = this.dataStream.toByteArray();
         this.vm.sendToTarget(this.pkt);
         this.isCommitted = true;
      }

   }

   void waitForReply() throws JDWPException {
      if (!this.isCommitted) {
         throw new InternalException("waitForReply without send");
      } else {
         this.vm.waitForTargetReply(this.pkt);
         if (this.pkt.errorCode != 0) {
            throw new JDWPException(this.pkt.errorCode);
         }
      }
   }

   void writeBoolean(boolean var1) {
      if (var1) {
         this.dataStream.write(1);
      } else {
         this.dataStream.write(0);
      }

   }

   void writeByte(byte var1) {
      this.dataStream.write(var1);
   }

   void writeChar(char var1) {
      this.dataStream.write((byte)(var1 >>> 8 & 255));
      this.dataStream.write((byte)(var1 >>> 0 & 255));
   }

   void writeShort(short var1) {
      this.dataStream.write((byte)(var1 >>> 8 & 255));
      this.dataStream.write((byte)(var1 >>> 0 & 255));
   }

   void writeInt(int var1) {
      this.dataStream.write((byte)(var1 >>> 24 & 255));
      this.dataStream.write((byte)(var1 >>> 16 & 255));
      this.dataStream.write((byte)(var1 >>> 8 & 255));
      this.dataStream.write((byte)(var1 >>> 0 & 255));
   }

   void writeLong(long var1) {
      this.dataStream.write((byte)((int)(var1 >>> 56 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 48 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 40 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 32 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 24 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 16 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 8 & 255L)));
      this.dataStream.write((byte)((int)(var1 >>> 0 & 255L)));
   }

   void writeFloat(float var1) {
      this.writeInt(Float.floatToIntBits(var1));
   }

   void writeDouble(double var1) {
      this.writeLong(Double.doubleToLongBits(var1));
   }

   void writeID(int var1, long var2) {
      switch (var1) {
         case 2:
            this.writeShort((short)((int)var2));
            break;
         case 4:
            this.writeInt((int)var2);
            break;
         case 8:
            this.writeLong(var2);
            break;
         default:
            throw new UnsupportedOperationException("JDWP: ID size not supported: " + var1);
      }

   }

   void writeNullObjectRef() {
      this.writeObjectRef(0L);
   }

   void writeObjectRef(long var1) {
      this.writeID(this.vm.sizeofObjectRef, var1);
   }

   void writeClassRef(long var1) {
      this.writeID(this.vm.sizeofClassRef, var1);
   }

   void writeMethodRef(long var1) {
      this.writeID(this.vm.sizeofMethodRef, var1);
   }

   void writeFieldRef(long var1) {
      this.writeID(this.vm.sizeofFieldRef, var1);
   }

   void writeFrameRef(long var1) {
      this.writeID(this.vm.sizeofFrameRef, var1);
   }

   void writeByteArray(byte[] var1) {
      this.dataStream.write(var1, 0, var1.length);
   }

   void writeString(String var1) {
      try {
         byte[] var2 = var1.getBytes("UTF8");
         this.writeInt(var2.length);
         this.writeByteArray(var2);
      } catch (UnsupportedEncodingException var3) {
         throw new InternalException("Cannot convert string to UTF8 bytes");
      }
   }

   void writeLocation(Location var1) {
      ReferenceTypeImpl var2 = (ReferenceTypeImpl)var1.declaringType();
      byte var3;
      if (var2 instanceof ClassType) {
         var3 = 1;
      } else {
         if (!(var2 instanceof InterfaceType)) {
            throw new InternalException("Invalid Location");
         }

         var3 = 2;
      }

      this.writeByte(var3);
      this.writeClassRef(var2.ref());
      this.writeMethodRef(((MethodImpl)var1.method()).ref());
      this.writeLong(var1.codeIndex());
   }

   void writeValue(Value var1) {
      try {
         this.writeValueChecked(var1);
      } catch (InvalidTypeException var3) {
         throw new RuntimeException("Internal error: Invalid Tag/Type pair");
      }
   }

   void writeValueChecked(Value var1) throws InvalidTypeException {
      this.writeByte(ValueImpl.typeValueKey(var1));
      this.writeUntaggedValue(var1);
   }

   void writeUntaggedValue(Value var1) {
      try {
         this.writeUntaggedValueChecked(var1);
      } catch (InvalidTypeException var3) {
         throw new RuntimeException("Internal error: Invalid Tag/Type pair");
      }
   }

   void writeUntaggedValueChecked(Value var1) throws InvalidTypeException {
      byte var2 = ValueImpl.typeValueKey(var1);
      if (isObjectTag(var2)) {
         if (var1 == null) {
            this.writeObjectRef(0L);
         } else {
            if (!(var1 instanceof ObjectReference)) {
               throw new InvalidTypeException();
            }

            this.writeObjectRef(((ObjectReferenceImpl)var1).ref());
         }
      } else {
         switch (var2) {
            case 66:
               if (!(var1 instanceof ByteValue)) {
                  throw new InvalidTypeException();
               }

               this.writeByte(((PrimitiveValue)var1).byteValue());
               break;
            case 67:
               if (!(var1 instanceof CharValue)) {
                  throw new InvalidTypeException();
               }

               this.writeChar(((PrimitiveValue)var1).charValue());
               break;
            case 68:
               if (!(var1 instanceof DoubleValue)) {
                  throw new InvalidTypeException();
               }

               this.writeDouble(((PrimitiveValue)var1).doubleValue());
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            default:
               break;
            case 70:
               if (!(var1 instanceof FloatValue)) {
                  throw new InvalidTypeException();
               }

               this.writeFloat(((PrimitiveValue)var1).floatValue());
               break;
            case 73:
               if (!(var1 instanceof IntegerValue)) {
                  throw new InvalidTypeException();
               }

               this.writeInt(((PrimitiveValue)var1).intValue());
               break;
            case 74:
               if (!(var1 instanceof LongValue)) {
                  throw new InvalidTypeException();
               }

               this.writeLong(((PrimitiveValue)var1).longValue());
               break;
            case 83:
               if (!(var1 instanceof ShortValue)) {
                  throw new InvalidTypeException();
               }

               this.writeShort(((PrimitiveValue)var1).shortValue());
               break;
            case 90:
               if (!(var1 instanceof BooleanValue)) {
                  throw new InvalidTypeException();
               }

               this.writeBoolean(((PrimitiveValue)var1).booleanValue());
         }
      }

   }

   byte readByte() {
      byte var1 = this.pkt.data[this.inCursor];
      ++this.inCursor;
      return var1;
   }

   boolean readBoolean() {
      byte var1 = this.readByte();
      return var1 != 0;
   }

   char readChar() {
      int var1 = this.pkt.data[this.inCursor++] & 255;
      int var2 = this.pkt.data[this.inCursor++] & 255;
      return (char)((var1 << 8) + var2);
   }

   short readShort() {
      int var1 = this.pkt.data[this.inCursor++] & 255;
      int var2 = this.pkt.data[this.inCursor++] & 255;
      return (short)((var1 << 8) + var2);
   }

   int readInt() {
      int var1 = this.pkt.data[this.inCursor++] & 255;
      int var2 = this.pkt.data[this.inCursor++] & 255;
      int var3 = this.pkt.data[this.inCursor++] & 255;
      int var4 = this.pkt.data[this.inCursor++] & 255;
      return (var1 << 24) + (var2 << 16) + (var3 << 8) + var4;
   }

   long readLong() {
      long var1 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var3 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var5 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var7 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var9 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var11 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var13 = (long)(this.pkt.data[this.inCursor++] & 255);
      long var15 = (long)(this.pkt.data[this.inCursor++] & 255);
      return (var1 << 56) + (var3 << 48) + (var5 << 40) + (var7 << 32) + (var9 << 24) + (var11 << 16) + (var13 << 8) + var15;
   }

   float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   String readString() {
      int var2 = this.readInt();

      String var1;
      try {
         var1 = new String(this.pkt.data, this.inCursor, var2, "UTF8");
      } catch (UnsupportedEncodingException var4) {
         System.err.println(var4);
         var1 = "Conversion error!";
      }

      this.inCursor += var2;
      return var1;
   }

   private long readID(int var1) {
      switch (var1) {
         case 2:
            return (long)this.readShort();
         case 4:
            return (long)this.readInt();
         case 8:
            return this.readLong();
         default:
            throw new UnsupportedOperationException("JDWP: ID size not supported: " + var1);
      }
   }

   long readObjectRef() {
      return this.readID(this.vm.sizeofObjectRef);
   }

   long readClassRef() {
      return this.readID(this.vm.sizeofClassRef);
   }

   ObjectReferenceImpl readTaggedObjectReference() {
      byte var1 = this.readByte();
      return this.vm.objectMirror(this.readObjectRef(), var1);
   }

   ObjectReferenceImpl readObjectReference() {
      return this.vm.objectMirror(this.readObjectRef());
   }

   StringReferenceImpl readStringReference() {
      long var1 = this.readObjectRef();
      return this.vm.stringMirror(var1);
   }

   ArrayReferenceImpl readArrayReference() {
      long var1 = this.readObjectRef();
      return this.vm.arrayMirror(var1);
   }

   ThreadReferenceImpl readThreadReference() {
      long var1 = this.readObjectRef();
      return this.vm.threadMirror(var1);
   }

   ThreadGroupReferenceImpl readThreadGroupReference() {
      long var1 = this.readObjectRef();
      return this.vm.threadGroupMirror(var1);
   }

   ClassLoaderReferenceImpl readClassLoaderReference() {
      long var1 = this.readObjectRef();
      return this.vm.classLoaderMirror(var1);
   }

   ClassObjectReferenceImpl readClassObjectReference() {
      long var1 = this.readObjectRef();
      return this.vm.classObjectMirror(var1);
   }

   ReferenceTypeImpl readReferenceType() {
      byte var1 = this.readByte();
      long var2 = this.readObjectRef();
      return this.vm.referenceType(var2, var1);
   }

   long readMethodRef() {
      return this.readID(this.vm.sizeofMethodRef);
   }

   long readFieldRef() {
      return this.readID(this.vm.sizeofFieldRef);
   }

   Field readField() {
      ReferenceTypeImpl var1 = this.readReferenceType();
      long var2 = this.readFieldRef();
      return var1.getFieldMirror(var2);
   }

   long readFrameRef() {
      return this.readID(this.vm.sizeofFrameRef);
   }

   ValueImpl readValue() {
      byte var1 = this.readByte();
      return this.readUntaggedValue(var1);
   }

   ValueImpl readUntaggedValue(byte var1) {
      Object var2 = null;
      if (isObjectTag(var1)) {
         var2 = this.vm.objectMirror(this.readObjectRef(), var1);
      } else {
         switch (var1) {
            case 66:
               var2 = new ByteValueImpl(this.vm, this.readByte());
               break;
            case 67:
               var2 = new CharValueImpl(this.vm, this.readChar());
               break;
            case 68:
               var2 = new DoubleValueImpl(this.vm, this.readDouble());
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 87:
            case 88:
            case 89:
            default:
               break;
            case 70:
               var2 = new FloatValueImpl(this.vm, this.readFloat());
               break;
            case 73:
               var2 = new IntegerValueImpl(this.vm, this.readInt());
               break;
            case 74:
               var2 = new LongValueImpl(this.vm, this.readLong());
               break;
            case 83:
               var2 = new ShortValueImpl(this.vm, this.readShort());
               break;
            case 86:
               var2 = new VoidValueImpl(this.vm);
               break;
            case 90:
               var2 = new BooleanValueImpl(this.vm, this.readBoolean());
         }
      }

      return (ValueImpl)var2;
   }

   Location readLocation() {
      byte var1 = this.readByte();
      long var2 = this.readObjectRef();
      long var4 = this.readMethodRef();
      long var6 = this.readLong();
      if (var2 != 0L) {
         ReferenceTypeImpl var8 = this.vm.referenceType(var2, var1);
         return new LocationImpl(this.vm, var8, var4, var6);
      } else {
         return null;
      }
   }

   byte[] readByteArray(int var1) {
      byte[] var2 = new byte[var1];
      System.arraycopy(this.pkt.data, this.inCursor, var2, 0, var1);
      this.inCursor += var1;
      return var2;
   }

   List readArrayRegion() {
      byte var1 = this.readByte();
      int var2 = this.readInt();
      ArrayList var3 = new ArrayList(var2);
      boolean var4 = isObjectTag(var1);

      for(int var5 = 0; var5 < var2; ++var5) {
         if (var4) {
            var1 = this.readByte();
         }

         ValueImpl var6 = this.readUntaggedValue(var1);
         var3.add(var6);
      }

      return var3;
   }

   void writeArrayRegion(List var1) {
      this.writeInt(var1.size());

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Value var3 = (Value)var1.get(var2);
         this.writeUntaggedValue(var3);
      }

   }

   int skipBytes(int var1) {
      this.inCursor += var1;
      return var1;
   }

   byte command() {
      return (byte)this.pkt.cmd;
   }

   static boolean isObjectTag(byte var0) {
      return var0 == 76 || var0 == 91 || var0 == 115 || var0 == 116 || var0 == 103 || var0 == 108 || var0 == 99;
   }
}
