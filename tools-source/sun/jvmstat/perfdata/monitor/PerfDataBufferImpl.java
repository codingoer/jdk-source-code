package sun.jvmstat.perfdata.monitor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;

public abstract class PerfDataBufferImpl {
   protected ByteBuffer buffer;
   protected Map monitors;
   protected int lvmid;
   protected Map aliasMap;
   protected Map aliasCache;

   protected PerfDataBufferImpl(ByteBuffer var1, int var2) {
      this.buffer = var1;
      this.lvmid = var2;
      this.monitors = new TreeMap();
      this.aliasMap = new HashMap();
      this.aliasCache = new HashMap();
   }

   public int getLocalVmId() {
      return this.lvmid;
   }

   public byte[] getBytes() {
      ByteBuffer var1 = null;
      synchronized(this) {
         try {
            if (this.monitors.isEmpty()) {
               this.buildMonitorMap(this.monitors);
            }
         } catch (MonitorException var5) {
         }

         var1 = this.buffer.duplicate();
      }

      var1.rewind();
      byte[] var2 = new byte[var1.limit()];
      var1.get(var2);
      return var2;
   }

   public int getCapacity() {
      return this.buffer.capacity();
   }

   ByteBuffer getByteBuffer() {
      return this.buffer;
   }

   private void buildAliasMap() {
      assert Thread.holdsLock(this);

      URL var1 = null;
      String var2 = System.getProperty("sun.jvmstat.perfdata.aliasmap");
      if (var2 != null) {
         File var3 = new File(var2);

         try {
            var1 = var3.toURL();
         } catch (MalformedURLException var7) {
            throw new IllegalArgumentException(var7);
         }
      } else {
         var1 = this.getClass().getResource("/sun/jvmstat/perfdata/resources/aliasmap");
      }

      assert var1 != null;

      AliasFileParser var8 = new AliasFileParser(var1);

      try {
         var8.parse(this.aliasMap);
      } catch (IOException var5) {
         System.err.println("Error processing " + var2 + ": " + var5.getMessage());
      } catch (SyntaxException var6) {
         System.err.println("Syntax error parsing " + var2 + ": " + var6.getMessage());
      }

   }

   protected Monitor findByAlias(String var1) {
      assert Thread.holdsLock(this);

      Monitor var2 = (Monitor)this.aliasCache.get(var1);
      if (var2 == null) {
         ArrayList var3 = (ArrayList)this.aliasMap.get(var1);
         String var5;
         if (var3 != null) {
            for(Iterator var4 = var3.iterator(); var4.hasNext() && var2 == null; var2 = (Monitor)this.monitors.get(var5)) {
               var5 = (String)var4.next();
            }
         }
      }

      return var2;
   }

   public Monitor findByName(String var1) throws MonitorException {
      Monitor var2 = null;
      synchronized(this) {
         if (this.monitors.isEmpty()) {
            this.buildMonitorMap(this.monitors);
            this.buildAliasMap();
         }

         var2 = (Monitor)this.monitors.get(var1);
         if (var2 == null) {
            this.getNewMonitors(this.monitors);
            var2 = (Monitor)this.monitors.get(var1);
         }

         if (var2 == null) {
            var2 = this.findByAlias(var1);
         }

         return var2;
      }
   }

   public List findByPattern(String var1) throws MonitorException, PatternSyntaxException {
      synchronized(this) {
         if (this.monitors.isEmpty()) {
            this.buildMonitorMap(this.monitors);
         } else {
            this.getNewMonitors(this.monitors);
         }
      }

      Pattern var2 = Pattern.compile(var1);
      Matcher var3 = var2.matcher("");
      ArrayList var4 = new ArrayList();
      Set var5 = this.monitors.entrySet();
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         String var8 = (String)var7.getKey();
         Monitor var9 = (Monitor)var7.getValue();
         var3.reset(var8);
         if (var3.lookingAt()) {
            var4.add((Monitor)var7.getValue());
         }
      }

      return var4;
   }

   public MonitorStatus getMonitorStatus() throws MonitorException {
      synchronized(this) {
         if (this.monitors.isEmpty()) {
            this.buildMonitorMap(this.monitors);
         }

         return this.getMonitorStatus(this.monitors);
      }
   }

   protected abstract MonitorStatus getMonitorStatus(Map var1) throws MonitorException;

   protected abstract void buildMonitorMap(Map var1) throws MonitorException;

   protected abstract void getNewMonitors(Map var1) throws MonitorException;
}
