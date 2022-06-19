package sun.tools.attach;

import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.AttachOperationFailedException;
import com.sun.tools.attach.spi.AttachProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class BsdVirtualMachine extends HotSpotVirtualMachine {
   private static final String tmpdir;
   String path;
   private static final String PROTOCOL_VERSION = "1";
   private static final int ATTACH_ERROR_BADVERSION = 101;

   BsdVirtualMachine(AttachProvider var1, String var2) throws AttachNotSupportedException, IOException {
      super(var1, var2);

      int var3;
      try {
         var3 = Integer.parseInt(var2);
      } catch (NumberFormatException var22) {
         throw new AttachNotSupportedException("Invalid process identifier");
      }

      this.path = this.findSocketFile(var3);
      if (this.path == null) {
         File var4 = new File(tmpdir, ".attach_pid" + var3);
         createAttachFile(var4.getPath());

         try {
            sendQuitTo(var3);
            int var5 = 0;
            long var6 = 200L;
            int var8 = (int)(this.attachTimeout() / var6);

            do {
               try {
                  Thread.sleep(var6);
               } catch (InterruptedException var21) {
               }

               this.path = this.findSocketFile(var3);
               ++var5;
            } while(var5 <= var8 && this.path == null);

            if (this.path == null) {
               throw new AttachNotSupportedException("Unable to open socket file: target process not responding or HotSpot VM not loaded");
            }
         } finally {
            var4.delete();
         }
      }

      checkPermissions(this.path);
      int var24 = socket();

      try {
         connect(var24, this.path);
      } finally {
         close(var24);
      }

   }

   public void detach() throws IOException {
      synchronized(this) {
         if (this.path != null) {
            this.path = null;
         }

      }
   }

   InputStream execute(String var1, Object... var2) throws AgentLoadException, IOException {
      assert var2.length <= 3;

      String var3;
      synchronized(this) {
         if (this.path == null) {
            throw new IOException("Detached from target VM");
         }

         var3 = this.path;
      }

      int var4 = socket();

      try {
         connect(var4, var3);
      } catch (IOException var9) {
         close(var4);
         throw var9;
      }

      IOException var5 = null;

      try {
         this.writeString(var4, "1");
         this.writeString(var4, var1);

         for(int var6 = 0; var6 < 3; ++var6) {
            if (var6 < var2.length && var2[var6] != null) {
               this.writeString(var4, (String)var2[var6]);
            } else {
               this.writeString(var4, "");
            }
         }
      } catch (IOException var11) {
         var5 = var11;
      }

      SocketInputStream var13 = new SocketInputStream(var4);

      int var7;
      try {
         var7 = this.readInt(var13);
      } catch (IOException var10) {
         var13.close();
         if (var5 != null) {
            throw var5;
         }

         throw var10;
      }

      if (var7 != 0) {
         String var8 = this.readErrorMessage(var13);
         var13.close();
         if (var7 == 101) {
            throw new IOException("Protocol mismatch with target VM");
         } else if (var1.equals("load")) {
            throw new AgentLoadException("Failed to load agent library");
         } else if (var8 == null) {
            throw new AttachOperationFailedException("Command failed in target VM");
         } else {
            throw new AttachOperationFailedException(var8);
         }
      } else {
         return var13;
      }
   }

   private String findSocketFile(int var1) {
      String var2 = ".java_pid" + var1;
      File var3 = new File(tmpdir, var2);
      return var3.exists() ? var3.getPath() : null;
   }

   private void writeString(int var1, String var2) throws IOException {
      byte[] var3;
      if (var2.length() > 0) {
         try {
            var3 = var2.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var5) {
            throw new InternalError();
         }

         write(var1, var3, 0, var3.length);
      }

      var3 = new byte[]{0};
      write(var1, var3, 0, 1);
   }

   static native void sendQuitTo(int var0) throws IOException;

   static native void checkPermissions(String var0) throws IOException;

   static native int socket() throws IOException;

   static native void connect(int var0, String var1) throws IOException;

   static native void close(int var0) throws IOException;

   static native int read(int var0, byte[] var1, int var2, int var3) throws IOException;

   static native void write(int var0, byte[] var1, int var2, int var3) throws IOException;

   static native void createAttachFile(String var0);

   static native String getTempDir();

   static {
      System.loadLibrary("attach");
      tmpdir = getTempDir();
   }

   private class SocketInputStream extends InputStream {
      int s;

      public SocketInputStream(int var2) {
         this.s = var2;
      }

      public synchronized int read() throws IOException {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1, 0, 1);
         return var2 == 1 ? var1[0] & 255 : -1;
      }

      public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
         if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            return var3 == 0 ? 0 : BsdVirtualMachine.read(this.s, var1, var2, var3);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void close() throws IOException {
         BsdVirtualMachine.close(this.s);
      }
   }
}
