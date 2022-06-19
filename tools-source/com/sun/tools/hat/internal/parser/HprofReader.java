package com.sun.tools.hat.internal.parser;

import com.sun.tools.hat.internal.model.ArrayTypeCodes;
import com.sun.tools.hat.internal.model.JavaBoolean;
import com.sun.tools.hat.internal.model.JavaByte;
import com.sun.tools.hat.internal.model.JavaChar;
import com.sun.tools.hat.internal.model.JavaClass;
import com.sun.tools.hat.internal.model.JavaDouble;
import com.sun.tools.hat.internal.model.JavaField;
import com.sun.tools.hat.internal.model.JavaFloat;
import com.sun.tools.hat.internal.model.JavaInt;
import com.sun.tools.hat.internal.model.JavaLong;
import com.sun.tools.hat.internal.model.JavaObject;
import com.sun.tools.hat.internal.model.JavaObjectArray;
import com.sun.tools.hat.internal.model.JavaObjectRef;
import com.sun.tools.hat.internal.model.JavaShort;
import com.sun.tools.hat.internal.model.JavaStatic;
import com.sun.tools.hat.internal.model.JavaThing;
import com.sun.tools.hat.internal.model.JavaValueArray;
import com.sun.tools.hat.internal.model.Root;
import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.model.StackFrame;
import com.sun.tools.hat.internal.model.StackTrace;
import com.sun.tools.hat.internal.util.Misc;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Hashtable;

public class HprofReader extends Reader implements ArrayTypeCodes {
   static final int MAGIC_NUMBER = 1245795905;
   private static final String[] VERSIONS = new String[]{" PROFILE 1.0\u0000", " PROFILE 1.0.1\u0000", " PROFILE 1.0.2\u0000"};
   private static final int VERSION_JDK12BETA3 = 0;
   private static final int VERSION_JDK12BETA4 = 1;
   private static final int VERSION_JDK6 = 2;
   static final int HPROF_UTF8 = 1;
   static final int HPROF_LOAD_CLASS = 2;
   static final int HPROF_UNLOAD_CLASS = 3;
   static final int HPROF_FRAME = 4;
   static final int HPROF_TRACE = 5;
   static final int HPROF_ALLOC_SITES = 6;
   static final int HPROF_HEAP_SUMMARY = 7;
   static final int HPROF_START_THREAD = 10;
   static final int HPROF_END_THREAD = 11;
   static final int HPROF_HEAP_DUMP = 12;
   static final int HPROF_CPU_SAMPLES = 13;
   static final int HPROF_CONTROL_SETTINGS = 14;
   static final int HPROF_LOCKSTATS_WAIT_TIME = 16;
   static final int HPROF_LOCKSTATS_HOLD_TIME = 17;
   static final int HPROF_GC_ROOT_UNKNOWN = 255;
   static final int HPROF_GC_ROOT_JNI_GLOBAL = 1;
   static final int HPROF_GC_ROOT_JNI_LOCAL = 2;
   static final int HPROF_GC_ROOT_JAVA_FRAME = 3;
   static final int HPROF_GC_ROOT_NATIVE_STACK = 4;
   static final int HPROF_GC_ROOT_STICKY_CLASS = 5;
   static final int HPROF_GC_ROOT_THREAD_BLOCK = 6;
   static final int HPROF_GC_ROOT_MONITOR_USED = 7;
   static final int HPROF_GC_ROOT_THREAD_OBJ = 8;
   static final int HPROF_GC_CLASS_DUMP = 32;
   static final int HPROF_GC_INSTANCE_DUMP = 33;
   static final int HPROF_GC_OBJ_ARRAY_DUMP = 34;
   static final int HPROF_GC_PRIM_ARRAY_DUMP = 35;
   static final int HPROF_HEAP_DUMP_SEGMENT = 28;
   static final int HPROF_HEAP_DUMP_END = 44;
   private static final int T_CLASS = 2;
   private int version;
   private int debugLevel;
   private long currPos;
   private int dumpsToSkip;
   private boolean callStack;
   private int identifierSize;
   private Hashtable names;
   private Hashtable threadObjects;
   private Hashtable classNameFromObjectID;
   private Hashtable classNameFromSerialNo;
   private Hashtable stackFrames;
   private Hashtable stackTraces;
   private Snapshot snapshot;

   public HprofReader(String var1, PositionDataInputStream var2, int var3, boolean var4, int var5) throws IOException {
      super(var2);
      RandomAccessFile var6 = new RandomAccessFile(var1, "r");
      this.snapshot = new Snapshot(MappedReadBuffer.create(var6));
      this.dumpsToSkip = var3 - 1;
      this.callStack = var4;
      this.debugLevel = var5;
      this.names = new Hashtable();
      this.threadObjects = new Hashtable(43);
      this.classNameFromObjectID = new Hashtable();
      if (var4) {
         this.stackFrames = new Hashtable(43);
         this.stackTraces = new Hashtable(43);
         this.classNameFromSerialNo = new Hashtable();
      }

   }

   public Snapshot read() throws IOException {
      this.currPos = 4L;
      this.version = this.readVersionHeader();
      this.identifierSize = this.in.readInt();
      this.snapshot.setIdentifierSize(this.identifierSize);
      if (this.version >= 1) {
         this.snapshot.setNewStyleArrayClass(true);
      } else {
         this.snapshot.setNewStyleArrayClass(false);
      }

      this.currPos += 4L;
      if (this.identifierSize != 4 && this.identifierSize != 8) {
         throw new IOException("I'm sorry, but I can't deal with an identifier size of " + this.identifierSize + ".  I can only deal with 4 or 8.");
      } else {
         System.out.println("Dump file created " + new Date(this.in.readLong()));
         this.currPos += 8L;

         while(true) {
            while(true) {
               int var1;
               try {
                  var1 = this.in.readUnsignedByte();
               } catch (EOFException var13) {
                  return this.snapshot;
               }

               this.in.readInt();
               long var2 = (long)this.in.readInt() & 4294967295L;
               if (this.debugLevel > 0) {
                  System.out.println("Read record type " + var1 + ", length " + var2 + " at position " + this.toHex(this.currPos));
               }

               if (var2 < 0L) {
                  throw new IOException("Bad record length of " + var2 + " at byte " + this.toHex(this.currPos + 5L) + " of file.");
               }

               this.currPos += 9L + var2;
               int var4;
               int var7;
               long var8;
               long var15;
               switch (var1) {
                  case 1:
                     var15 = this.readID();
                     byte[] var18 = new byte[(int)var2 - this.identifierSize];
                     this.in.readFully(var18);
                     this.names.put(new Long(var15), new String(var18));
                     break;
                  case 2:
                     var4 = this.in.readInt();
                     long var16 = this.readID();
                     var7 = this.in.readInt();
                     var8 = this.readID();
                     Long var21 = new Long(var16);
                     String var22 = this.getNameFromID(var8).replace('/', '.');
                     this.classNameFromObjectID.put(var21, var22);
                     if (this.classNameFromSerialNo != null) {
                        this.classNameFromSerialNo.put(new Integer(var4), var22);
                     }
                     break;
                  case 3:
                  case 6:
                  case 7:
                  case 10:
                  case 11:
                  case 13:
                  case 14:
                  case 16:
                  case 17:
                     this.skipBytes(var2);
                     break;
                  case 4:
                     if (this.stackFrames == null) {
                        this.skipBytes(var2);
                     } else {
                        var15 = this.readID();
                        String var17 = this.getNameFromID(this.readID());
                        String var19 = this.getNameFromID(this.readID());
                        String var20 = this.getNameFromID(this.readID());
                        int var9 = this.in.readInt();
                        String var10 = (String)this.classNameFromSerialNo.get(new Integer(var9));
                        int var11 = this.in.readInt();
                        if (var11 < -3) {
                           this.warn("Weird stack frame line number:  " + var11);
                           var11 = -1;
                        }

                        this.stackFrames.put(new Long(var15), new StackFrame(var17, var19, var10, var20, var11));
                     }
                     break;
                  case 5:
                     if (this.stackTraces == null) {
                        this.skipBytes(var2);
                        break;
                     }

                     var4 = this.in.readInt();
                     int var5 = this.in.readInt();
                     StackFrame[] var6 = new StackFrame[this.in.readInt()];

                     for(var7 = 0; var7 < var6.length; ++var7) {
                        var8 = this.readID();
                        var6[var7] = (StackFrame)this.stackFrames.get(new Long(var8));
                        if (var6[var7] == null) {
                           throw new IOException("Stack frame " + this.toHex(var8) + " not found");
                        }
                     }

                     this.stackTraces.put(new Integer(var4), new StackTrace(var6));
                     break;
                  case 8:
                  case 9:
                  case 15:
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
                  case 39:
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  default:
                     this.skipBytes(var2);
                     this.warn("Ignoring unrecognized record type " + var1);
                     break;
                  case 12:
                     if (this.dumpsToSkip <= 0) {
                        try {
                           this.readHeapDump(var2, this.currPos);
                        } catch (EOFException var12) {
                           this.handleEOF(var12, this.snapshot);
                        }

                        if (this.debugLevel > 0) {
                           System.out.println("    Finished processing instances in heap dump.");
                        }

                        return this.snapshot;
                     }

                     --this.dumpsToSkip;
                     this.skipBytes(var2);
                     break;
                  case 28:
                     if (this.version >= 2) {
                        if (this.dumpsToSkip <= 0) {
                           try {
                              this.readHeapDump(var2, this.currPos);
                           } catch (EOFException var14) {
                              this.handleEOF(var14, this.snapshot);
                           }
                        } else {
                           this.skipBytes(var2);
                        }
                     } else {
                        this.warn("Ignoring unrecognized record type " + var1);
                        this.skipBytes(var2);
                     }
                     break;
                  case 44:
                     if (this.version >= 2) {
                        if (this.dumpsToSkip <= 0) {
                           this.skipBytes(var2);
                           return this.snapshot;
                        }

                        --this.dumpsToSkip;
                     } else {
                        this.warn("Ignoring unrecognized record type " + var1);
                     }

                     this.skipBytes(var2);
               }
            }
         }
      }
   }

   private void skipBytes(long var1) throws IOException {
      while(true) {
         if (var1 > 0L) {
            long var3 = this.in.skip(var1);
            var1 -= var3;
            if (var3 != 0L) {
               continue;
            }

            throw new EOFException("Couldn't skip enough bytes");
         }

         return;
      }
   }

   private int readVersionHeader() throws IOException {
      int var1 = VERSIONS.length;
      boolean[] var2 = new boolean[VERSIONS.length];

      int var3;
      for(var3 = 0; var3 < var1; ++var3) {
         var2[var3] = true;
      }

      for(var3 = 0; var1 > 0; ++var3) {
         char var4 = (char)this.in.readByte();
         ++this.currPos;

         for(int var5 = 0; var5 < VERSIONS.length; ++var5) {
            if (var2[var5]) {
               if (var4 != VERSIONS[var5].charAt(var3)) {
                  var2[var5] = false;
                  --var1;
               } else if (var3 == VERSIONS[var5].length() - 1) {
                  return var5;
               }
            }
         }
      }

      throw new IOException("Version string not recognized at byte " + (var3 + 3));
   }

   private void readHeapDump(long var1, long var3) throws IOException {
      while(var1 > 0L) {
         int var5 = this.in.readUnsignedByte();
         if (this.debugLevel > 0) {
            System.out.println("    Read heap sub-record type " + var5 + " at position " + this.toHex(var3 - var1));
         }

         --var1;
         long var6;
         int var8;
         int var9;
         StackTrace var10;
         StackTrace var11;
         int var12;
         ThreadObject var13;
         ThreadObject var15;
         switch (var5) {
            case 1:
               var6 = this.readID();
               long var14 = this.readID();
               var1 -= (long)(2 * this.identifierSize);
               this.snapshot.addRoot(new Root(var6, 0L, 4, ""));
               break;
            case 2:
               var6 = this.readID();
               var8 = this.in.readInt();
               var9 = this.in.readInt();
               var1 -= (long)(this.identifierSize + 8);
               var15 = this.getThreadObjectFromSequence(var8);
               var11 = this.getStackTraceFromSerial(var15.stackSeq);
               if (var11 != null) {
                  var11 = var11.traceForDepth(var9 + 1);
               }

               this.snapshot.addRoot(new Root(var6, var15.threadId, 3, "", var11));
               break;
            case 3:
               var6 = this.readID();
               var8 = this.in.readInt();
               var9 = this.in.readInt();
               var1 -= (long)(this.identifierSize + 8);
               var15 = this.getThreadObjectFromSequence(var8);
               var11 = this.getStackTraceFromSerial(var15.stackSeq);
               if (var11 != null) {
                  var11 = var11.traceForDepth(var9 + 1);
               }

               this.snapshot.addRoot(new Root(var6, var15.threadId, 7, "", var11));
               break;
            case 4:
               var6 = this.readID();
               var8 = this.in.readInt();
               var1 -= (long)(this.identifierSize + 4);
               var13 = this.getThreadObjectFromSequence(var8);
               var10 = this.getStackTraceFromSerial(var13.stackSeq);
               this.snapshot.addRoot(new Root(var6, var13.threadId, 8, "", var10));
               break;
            case 5:
               var6 = this.readID();
               var1 -= (long)this.identifierSize;
               this.snapshot.addRoot(new Root(var6, 0L, 2, ""));
               break;
            case 6:
               var6 = this.readID();
               var8 = this.in.readInt();
               var1 -= (long)(this.identifierSize + 4);
               var13 = this.getThreadObjectFromSequence(var8);
               var10 = this.getStackTraceFromSerial(var13.stackSeq);
               this.snapshot.addRoot(new Root(var6, var13.threadId, 5, "", var10));
               break;
            case 7:
               var6 = this.readID();
               var1 -= (long)this.identifierSize;
               this.snapshot.addRoot(new Root(var6, 0L, 6, ""));
               break;
            case 8:
               var6 = this.readID();
               var8 = this.in.readInt();
               var9 = this.in.readInt();
               var1 -= (long)(this.identifierSize + 8);
               this.threadObjects.put(new Integer(var8), new ThreadObject(var6, var9));
               break;
            case 32:
               var12 = this.readClass();
               var1 -= (long)var12;
               break;
            case 33:
               var12 = this.readInstance();
               var1 -= (long)var12;
               break;
            case 34:
               var6 = this.readArray(false);
               var1 -= var6;
               break;
            case 35:
               var6 = this.readArray(true);
               var1 -= var6;
               break;
            case 255:
               var6 = this.readID();
               var1 -= (long)this.identifierSize;
               this.snapshot.addRoot(new Root(var6, 0L, 1, ""));
               break;
            default:
               throw new IOException("Unrecognized heap dump sub-record type:  " + var5);
         }
      }

      if (var1 != 0L) {
         this.warn("Error reading heap dump or heap dump segment:  Byte count is " + var1 + " instead of 0");
         this.skipBytes(var1);
      }

      if (this.debugLevel > 0) {
         System.out.println("    Finished heap sub-records.");
      }

   }

   private long readID() throws IOException {
      return this.identifierSize == 4 ? Snapshot.SMALL_ID_MASK & (long)this.in.readInt() : this.in.readLong();
   }

   private int readValue(JavaThing[] var1) throws IOException {
      byte var2 = this.in.readByte();
      return 1 + this.readValueForType(var2, var1);
   }

   private int readValueForType(byte var1, JavaThing[] var2) throws IOException {
      if (this.version >= 1) {
         var1 = this.signatureFromTypeId(var1);
      }

      return this.readValueForTypeSignature(var1, var2);
   }

   private int readValueForTypeSignature(byte var1, JavaThing[] var2) throws IOException {
      int var3;
      long var5;
      switch (var1) {
         case 66:
            byte var9 = this.in.readByte();
            if (var2 != null) {
               var2[0] = new JavaByte(var9);
            }

            return 1;
         case 67:
            char var8 = this.in.readChar();
            if (var2 != null) {
               var2[0] = new JavaChar(var8);
            }

            return 2;
         case 68:
            double var7 = this.in.readDouble();
            if (var2 != null) {
               var2[0] = new JavaDouble(var7);
            }

            return 8;
         case 69:
         case 71:
         case 72:
         case 75:
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
            throw new IOException("Bad value signature:  " + var1);
         case 70:
            float var6 = this.in.readFloat();
            if (var2 != null) {
               var2[0] = new JavaFloat(var6);
            }

            return 4;
         case 73:
            var3 = this.in.readInt();
            if (var2 != null) {
               var2[0] = new JavaInt(var3);
            }

            return 4;
         case 74:
            var5 = this.in.readLong();
            if (var2 != null) {
               var2[0] = new JavaLong(var5);
            }

            return 8;
         case 76:
         case 91:
            var5 = this.readID();
            if (var2 != null) {
               var2[0] = new JavaObjectRef(var5);
            }

            return this.identifierSize;
         case 83:
            var3 = this.in.readShort();
            if (var2 != null) {
               var2[0] = new JavaShort((short)var3);
            }

            return 2;
         case 90:
            var3 = this.in.readByte();
            if (var3 != 0 && var3 != 1) {
               this.warn("Illegal boolean value read");
            }

            if (var2 != null) {
               var2[0] = new JavaBoolean(var3 != 0);
            }

            return 1;
      }
   }

   private ThreadObject getThreadObjectFromSequence(int var1) throws IOException {
      ThreadObject var2 = (ThreadObject)this.threadObjects.get(new Integer(var1));
      if (var2 == null) {
         throw new IOException("Thread " + var1 + " not found for JNI local ref");
      } else {
         return var2;
      }
   }

   private String getNameFromID(long var1) throws IOException {
      return this.getNameFromID(new Long(var1));
   }

   private String getNameFromID(Long var1) throws IOException {
      if (var1 == 0L) {
         return "";
      } else {
         String var2 = (String)this.names.get(var1);
         if (var2 == null) {
            this.warn("Name not found at " + this.toHex(var1));
            return "unresolved name " + this.toHex(var1);
         } else {
            return var2;
         }
      }
   }

   private StackTrace getStackTraceFromSerial(int var1) throws IOException {
      if (this.stackTraces == null) {
         return null;
      } else {
         StackTrace var2 = (StackTrace)this.stackTraces.get(new Integer(var1));
         if (var2 == null) {
            this.warn("Stack trace not found for serial # " + var1);
         }

         return var2;
      }
   }

   private int readClass() throws IOException {
      long var1 = this.readID();
      StackTrace var3 = this.getStackTraceFromSerial(this.in.readInt());
      long var4 = this.readID();
      long var6 = this.readID();
      long var8 = this.readID();
      long var10 = this.readID();
      long var12 = this.readID();
      long var14 = this.readID();
      int var16 = this.in.readInt();
      int var17 = 7 * this.identifierSize + 8;
      int var18 = this.in.readUnsignedShort();
      var17 += 2;

      int var19;
      for(var19 = 0; var19 < var18; ++var19) {
         int var20 = this.in.readUnsignedShort();
         var17 += 2;
         var17 += this.readValue((JavaThing[])null);
      }

      var19 = this.in.readUnsignedShort();
      var17 += 2;
      JavaThing[] var30 = new JavaThing[1];
      JavaStatic[] var21 = new JavaStatic[var19];

      int var22;
      for(var22 = 0; var22 < var19; ++var22) {
         long var23 = this.readID();
         var17 += this.identifierSize;
         byte var25 = this.in.readByte();
         ++var17;
         var17 += this.readValueForType(var25, var30);
         String var26 = this.getNameFromID(var23);
         if (this.version >= 1) {
            var25 = this.signatureFromTypeId(var25);
         }

         String var27 = "" + (char)var25;
         JavaField var28 = new JavaField(var26, var27);
         var21[var22] = new JavaStatic(var28, var30[0]);
      }

      var22 = this.in.readUnsignedShort();
      var17 += 2;
      JavaField[] var31 = new JavaField[var22];

      for(int var24 = 0; var24 < var22; ++var24) {
         long var33 = this.readID();
         var17 += this.identifierSize;
         byte var35 = this.in.readByte();
         ++var17;
         String var36 = this.getNameFromID(var33);
         if (this.version >= 1) {
            var35 = this.signatureFromTypeId(var35);
         }

         String var29 = "" + (char)var35;
         var31[var24] = new JavaField(var36, var29);
      }

      String var32 = (String)this.classNameFromObjectID.get(new Long(var1));
      if (var32 == null) {
         this.warn("Class name not found for " + this.toHex(var1));
         var32 = "unknown-name@" + this.toHex(var1);
      }

      JavaClass var34 = new JavaClass(var1, var32, var4, var6, var8, var10, var31, var21, var16);
      this.snapshot.addClass(var1, var34);
      this.snapshot.setSiteTrace(var34, var3);
      return var17;
   }

   private String toHex(long var1) {
      return Misc.toHex(var1);
   }

   private int readInstance() throws IOException {
      long var1 = this.in.position();
      long var3 = this.readID();
      StackTrace var5 = this.getStackTraceFromSerial(this.in.readInt());
      long var6 = this.readID();
      int var8 = this.in.readInt();
      int var9 = 2 * this.identifierSize + 8 + var8;
      JavaObject var10 = new JavaObject(var6, var1);
      this.skipBytes((long)var8);
      this.snapshot.addHeapObject(var3, var10);
      this.snapshot.setSiteTrace(var10, var5);
      return var9;
   }

   private long readArray(boolean var1) throws IOException {
      long var2 = this.in.position();
      long var4 = this.readID();
      StackTrace var6 = this.getStackTraceFromSerial(this.in.readInt());
      int var7 = this.in.readInt();
      long var8 = (long)(this.identifierSize + 8);
      long var10;
      if (var1) {
         var10 = (long)this.in.readByte();
         ++var8;
      } else {
         var10 = this.readID();
         var8 += (long)this.identifierSize;
      }

      byte var12 = 0;
      byte var13 = 0;
      if (var1 || this.version < 1) {
         switch ((int)var10) {
            case 4:
               var12 = 90;
               var13 = 1;
               break;
            case 5:
               var12 = 67;
               var13 = 2;
               break;
            case 6:
               var12 = 70;
               var13 = 4;
               break;
            case 7:
               var12 = 68;
               var13 = 8;
               break;
            case 8:
               var12 = 66;
               var13 = 1;
               break;
            case 9:
               var12 = 83;
               var13 = 2;
               break;
            case 10:
               var12 = 73;
               var13 = 4;
               break;
            case 11:
               var12 = 74;
               var13 = 8;
         }

         if (this.version >= 1 && var12 == 0) {
            throw new IOException("Unrecognized typecode:  " + var10);
         }
      }

      long var14;
      if (var12 != 0) {
         var14 = (long)var13 * (long)var7;
         var8 += var14;
         JavaValueArray var16 = new JavaValueArray(var12, var2);
         this.skipBytes(var14);
         this.snapshot.addHeapObject(var4, var16);
         this.snapshot.setSiteTrace(var16, var6);
      } else {
         var14 = (long)var7 * (long)this.identifierSize;
         var8 += var14;
         JavaObjectArray var17 = new JavaObjectArray(var10, var2);
         this.skipBytes(var14);
         this.snapshot.addHeapObject(var4, var17);
         this.snapshot.setSiteTrace(var17, var6);
      }

      return var8;
   }

   private byte signatureFromTypeId(byte var1) throws IOException {
      switch (var1) {
         case 2:
            return 76;
         case 3:
         default:
            throw new IOException("Invalid type id of " + var1);
         case 4:
            return 90;
         case 5:
            return 67;
         case 6:
            return 70;
         case 7:
            return 68;
         case 8:
            return 66;
         case 9:
            return 83;
         case 10:
            return 73;
         case 11:
            return 74;
      }
   }

   private void handleEOF(EOFException var1, Snapshot var2) {
      if (this.debugLevel > 0) {
         var1.printStackTrace();
      }

      this.warn("Unexpected EOF. Will miss information...");
      var2.setUnresolvedObjectsOK(true);
   }

   private void warn(String var1) {
      System.out.println("WARNING: " + var1);
   }

   private class ThreadObject {
      long threadId;
      int stackSeq;

      ThreadObject(long var2, int var4) {
         this.threadId = var2;
         this.stackSeq = var4;
      }
   }
}
