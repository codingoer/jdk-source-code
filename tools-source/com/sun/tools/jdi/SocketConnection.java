package com.sun.tools.jdi;

import com.sun.jdi.connect.spi.ClosedConnectionException;
import com.sun.jdi.connect.spi.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class SocketConnection extends Connection {
   private Socket socket;
   private boolean closed = false;
   private OutputStream socketOutput;
   private InputStream socketInput;
   private Object receiveLock = new Object();
   private Object sendLock = new Object();
   private Object closeLock = new Object();

   SocketConnection(Socket var1) throws IOException {
      this.socket = var1;
      var1.setTcpNoDelay(true);
      this.socketInput = var1.getInputStream();
      this.socketOutput = var1.getOutputStream();
   }

   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.closed) {
            this.socketOutput.close();
            this.socketInput.close();
            this.socket.close();
            this.closed = true;
         }
      }
   }

   public boolean isOpen() {
      synchronized(this.closeLock) {
         return !this.closed;
      }
   }

   public byte[] readPacket() throws IOException {
      if (!this.isOpen()) {
         throw new ClosedConnectionException("connection is closed");
      } else {
         synchronized(this.receiveLock) {
            int var2;
            int var3;
            int var4;
            int var5;
            try {
               var2 = this.socketInput.read();
               var3 = this.socketInput.read();
               var4 = this.socketInput.read();
               var5 = this.socketInput.read();
            } catch (IOException var13) {
               if (!this.isOpen()) {
                  throw new ClosedConnectionException("connection is closed");
               }

               throw var13;
            }

            if (var2 < 0) {
               return new byte[0];
            } else if (var3 >= 0 && var4 >= 0 && var5 >= 0) {
               int var6 = var2 << 24 | var3 << 16 | var4 << 8 | var5 << 0;
               if (var6 < 0) {
                  throw new IOException("protocol error - invalid length");
               } else {
                  byte[] var7 = new byte[var6];
                  var7[0] = (byte)var2;
                  var7[1] = (byte)var3;
                  var7[2] = (byte)var4;
                  var7[3] = (byte)var5;
                  int var8 = 4;

                  int var9;
                  for(var6 -= var8; var6 > 0; var8 += var9) {
                     try {
                        var9 = this.socketInput.read(var7, var8, var6);
                     } catch (IOException var12) {
                        if (!this.isOpen()) {
                           throw new ClosedConnectionException("connection is closed");
                        }

                        throw var12;
                     }

                     if (var9 < 0) {
                        throw new IOException("protocol error - premature EOF");
                     }

                     var6 -= var9;
                  }

                  return var7;
               }
            } else {
               throw new IOException("protocol error - premature EOF");
            }
         }
      }
   }

   public void writePacket(byte[] var1) throws IOException {
      if (!this.isOpen()) {
         throw new ClosedConnectionException("connection is closed");
      } else if (var1.length < 11) {
         throw new IllegalArgumentException("packet is insufficient size");
      } else {
         int var2 = var1[0] & 255;
         int var3 = var1[1] & 255;
         int var4 = var1[2] & 255;
         int var5 = var1[3] & 255;
         int var6 = var2 << 24 | var3 << 16 | var4 << 8 | var5 << 0;
         if (var6 < 11) {
            throw new IllegalArgumentException("packet is insufficient size");
         } else if (var6 > var1.length) {
            throw new IllegalArgumentException("length mis-match");
         } else {
            synchronized(this.sendLock) {
               try {
                  this.socketOutput.write(var1, 0, var6);
               } catch (IOException var10) {
                  if (!this.isOpen()) {
                     throw new ClosedConnectionException("connection is closed");
                  }

                  throw var10;
               }

            }
         }
      }
   }
}
