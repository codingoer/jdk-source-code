package com.sun.tools.jdi;

import com.sun.jdi.connect.TransportTimeoutException;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class SocketTransportService extends TransportService {
   private ResourceBundle messages = null;

   void handshake(Socket var1, long var2) throws IOException {
      var1.setSoTimeout((int)var2);
      byte[] var4 = "JDWP-Handshake".getBytes("UTF-8");
      var1.getOutputStream().write(var4);
      byte[] var5 = new byte[var4.length];

      int var7;
      for(int var6 = 0; var6 < var4.length; var6 += var7) {
         try {
            var7 = var1.getInputStream().read(var5, var6, var4.length - var6);
         } catch (SocketTimeoutException var9) {
            throw new IOException("handshake timeout");
         }

         if (var7 < 0) {
            var1.close();
            throw new IOException("handshake failed - connection prematurally closed");
         }
      }

      for(var7 = 0; var7 < var4.length; ++var7) {
         if (var5[var7] != var4[var7]) {
            throw new IOException("handshake failed - unrecognized message from target VM");
         }
      }

      var1.setSoTimeout(0);
   }

   public String name() {
      return "Socket";
   }

   public String description() {
      synchronized(this) {
         if (this.messages == null) {
            this.messages = ResourceBundle.getBundle("com.sun.tools.jdi.resources.jdi");
         }
      }

      return this.messages.getString("socket_transportservice.description");
   }

   public TransportService.Capabilities capabilities() {
      return new SocketTransportServiceCapabilities();
   }

   public Connection attach(String var1, long var2, long var4) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("address is null");
      } else if (var2 >= 0L && var4 >= 0L) {
         int var6 = var1.indexOf(58);
         String var7;
         String var8;
         if (var6 < 0) {
            var7 = InetAddress.getLocalHost().getHostName();
            var8 = var1;
         } else {
            var7 = var1.substring(0, var6);
            var8 = var1.substring(var6 + 1);
         }

         int var9;
         try {
            var9 = Integer.decode(var8);
         } catch (NumberFormatException var18) {
            throw new IllegalArgumentException("unable to parse port number in address");
         }

         InetSocketAddress var10 = new InetSocketAddress(var7, var9);
         Socket var11 = new Socket();

         try {
            var11.connect(var10, (int)var2);
         } catch (SocketTimeoutException var17) {
            try {
               var11.close();
            } catch (IOException var15) {
            }

            throw new TransportTimeoutException("timed out trying to establish connection");
         }

         try {
            this.handshake(var11, var4);
         } catch (IOException var16) {
            try {
               var11.close();
            } catch (IOException var14) {
            }

            throw var16;
         }

         return new SocketConnection(var11);
      } else {
         throw new IllegalArgumentException("timeout is negative");
      }
   }

   TransportService.ListenKey startListening(String var1, int var2) throws IOException {
      InetSocketAddress var3;
      if (var1 == null) {
         var3 = new InetSocketAddress(var2);
      } else {
         var3 = new InetSocketAddress(var1, var2);
      }

      ServerSocket var4 = new ServerSocket();
      var4.bind(var3);
      return new SocketListenKey(var4);
   }

   public TransportService.ListenKey startListening(String var1) throws IOException {
      if (var1 == null || var1.length() == 0) {
         var1 = "0";
      }

      int var2 = var1.indexOf(58);
      String var3 = null;
      if (var2 >= 0) {
         var3 = var1.substring(0, var2);
         var1 = var1.substring(var2 + 1);
      }

      int var4;
      try {
         var4 = Integer.decode(var1);
      } catch (NumberFormatException var6) {
         throw new IllegalArgumentException("unable to parse port number in address");
      }

      return this.startListening(var3, var4);
   }

   public TransportService.ListenKey startListening() throws IOException {
      return this.startListening((String)null, 0);
   }

   public void stopListening(TransportService.ListenKey var1) throws IOException {
      if (!(var1 instanceof SocketListenKey)) {
         throw new IllegalArgumentException("Invalid listener");
      } else {
         synchronized(var1) {
            ServerSocket var3 = ((SocketListenKey)var1).socket();
            if (var3.isClosed()) {
               throw new IllegalArgumentException("Invalid listener");
            } else {
               var3.close();
            }
         }
      }
   }

   public Connection accept(TransportService.ListenKey var1, long var2, long var4) throws IOException {
      if (var2 >= 0L && var4 >= 0L) {
         if (!(var1 instanceof SocketListenKey)) {
            throw new IllegalArgumentException("Invalid listener");
         } else {
            ServerSocket var6;
            synchronized(var1) {
               var6 = ((SocketListenKey)var1).socket();
               if (var6.isClosed()) {
                  throw new IllegalArgumentException("Invalid listener");
               }
            }

            var6.setSoTimeout((int)var2);

            Socket var7;
            try {
               var7 = var6.accept();
            } catch (SocketTimeoutException var9) {
               throw new TransportTimeoutException("timeout waiting for connection");
            }

            this.handshake(var7, var4);
            return new SocketConnection(var7);
         }
      } else {
         throw new IllegalArgumentException("timeout is negative");
      }
   }

   public String toString() {
      return this.name();
   }

   static class SocketListenKey extends TransportService.ListenKey {
      ServerSocket ss;

      SocketListenKey(ServerSocket var1) {
         this.ss = var1;
      }

      ServerSocket socket() {
         return this.ss;
      }

      public String address() {
         InetAddress var1 = this.ss.getInetAddress();
         if (var1.isAnyLocalAddress()) {
            try {
               var1 = InetAddress.getLocalHost();
            } catch (UnknownHostException var6) {
               byte[] var3 = new byte[]{127, 0, 0, 1};

               try {
                  var1 = InetAddress.getByAddress("127.0.0.1", var3);
               } catch (UnknownHostException var5) {
                  throw new InternalError("unable to get local hostname");
               }
            }
         }

         String var7 = var1.getHostName();
         String var4 = var1.getHostAddress();
         String var2;
         if (var7.equals(var4)) {
            if (var1 instanceof Inet6Address) {
               var2 = "[" + var4 + "]";
            } else {
               var2 = var4;
            }
         } else {
            var2 = var7;
         }

         return var2 + ":" + this.ss.getLocalPort();
      }

      public String toString() {
         return this.address();
      }
   }
}
