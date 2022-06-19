package com.sun.tools.javac.util;

import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.file.FSInfo;
import com.sun.tools.javac.file.Locations;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.main.OptionHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public abstract class BaseFileManager {
   public Log log;
   protected Charset charset;
   protected Options options;
   protected String classLoaderClass;
   protected Locations locations;
   private static final Set javacFileManagerOptions = Option.getJavacFileManagerOptions();
   private String defaultEncodingName;
   private final ByteBufferCache byteBufferCache;
   protected final Map contentCache = new HashMap();

   protected BaseFileManager(Charset var1) {
      this.charset = var1;
      this.byteBufferCache = new ByteBufferCache();
      this.locations = this.createLocations();
   }

   public void setContext(Context var1) {
      this.log = Log.instance(var1);
      this.options = Options.instance(var1);
      this.classLoaderClass = this.options.get("procloader");
      this.locations.update(this.log, this.options, Lint.instance(var1), FSInfo.instance(var1));
   }

   protected Locations createLocations() {
      return new Locations();
   }

   protected Source getSource() {
      String var1 = this.options.get(Option.SOURCE);
      Source var2 = null;
      if (var1 != null) {
         var2 = Source.lookup(var1);
      }

      return var2 != null ? var2 : Source.DEFAULT;
   }

   protected ClassLoader getClassLoader(URL[] var1) {
      ClassLoader var2 = this.getClass().getClassLoader();
      if (this.classLoaderClass != null) {
         try {
            Class var3 = Class.forName(this.classLoaderClass).asSubclass(ClassLoader.class);
            Class[] var4 = new Class[]{URL[].class, ClassLoader.class};
            Constructor var5 = var3.getConstructor(var4);
            return (ClassLoader)var5.newInstance(var1, var2);
         } catch (Throwable var6) {
         }
      }

      return new URLClassLoader(var1, var2);
   }

   public boolean handleOption(String var1, Iterator var2) {
      OptionHelper.GrumpyHelper var3 = new OptionHelper.GrumpyHelper(this.log) {
         public String get(Option var1) {
            return BaseFileManager.this.options.get(var1.getText());
         }

         public void put(String var1, String var2) {
            BaseFileManager.this.options.put(var1, var2);
         }

         public void remove(String var1) {
            BaseFileManager.this.options.remove(var1);
         }
      };
      Iterator var4 = javacFileManagerOptions.iterator();

      Option var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (Option)var4.next();
      } while(!var5.matches(var1));

      if (var5.hasArg()) {
         if (var2.hasNext() && !var5.process(var3, var1, (String)var2.next())) {
            return true;
         }
      } else if (!var5.process(var3, var1)) {
         return true;
      }

      throw new IllegalArgumentException(var1);
   }

   public int isSupportedOption(String var1) {
      Iterator var2 = javacFileManagerOptions.iterator();

      Option var3;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         var3 = (Option)var2.next();
      } while(!var3.matches(var1));

      return var3.hasArg() ? 1 : 0;
   }

   public abstract boolean isDefaultBootClassPath();

   private String getDefaultEncodingName() {
      if (this.defaultEncodingName == null) {
         this.defaultEncodingName = (new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding();
      }

      return this.defaultEncodingName;
   }

   public String getEncodingName() {
      String var1 = this.options.get(Option.ENCODING);
      return var1 == null ? this.getDefaultEncodingName() : var1;
   }

   public CharBuffer decode(java.nio.ByteBuffer var1, boolean var2) {
      String var3 = this.getEncodingName();

      CharsetDecoder var4;
      try {
         var4 = this.getDecoder(var3, var2);
      } catch (IllegalCharsetNameException var9) {
         this.log.error("unsupported.encoding", new Object[]{var3});
         return (CharBuffer)CharBuffer.allocate(1).flip();
      } catch (UnsupportedCharsetException var10) {
         this.log.error("unsupported.encoding", new Object[]{var3});
         return (CharBuffer)CharBuffer.allocate(1).flip();
      }

      float var5 = var4.averageCharsPerByte() * 0.8F + var4.maxCharsPerByte() * 0.2F;
      CharBuffer var6 = CharBuffer.allocate(10 + (int)((float)var1.remaining() * var5));

      while(true) {
         CoderResult var7 = var4.decode(var1, var6, true);
         var6.flip();
         if (var7.isUnderflow()) {
            if (var6.limit() == var6.capacity()) {
               var6 = CharBuffer.allocate(var6.capacity() + 1).put(var6);
               var6.flip();
            }

            return var6;
         }

         if (var7.isOverflow()) {
            int var8 = 10 + var6.capacity() + (int)((float)var1.remaining() * var4.maxCharsPerByte());
            var6 = CharBuffer.allocate(var8).put(var6);
         } else {
            if (!var7.isMalformed() && !var7.isUnmappable()) {
               throw new AssertionError(var7);
            }

            if (!this.getSource().allowEncodingErrors()) {
               this.log.error(new JCDiagnostic.SimpleDiagnosticPosition(var6.limit()), "illegal.char.for.encoding", new Object[]{this.charset == null ? var3 : this.charset.name()});
            } else {
               this.log.warning(new JCDiagnostic.SimpleDiagnosticPosition(var6.limit()), "illegal.char.for.encoding", new Object[]{this.charset == null ? var3 : this.charset.name()});
            }

            var1.position(var1.position() + var7.length());
            var6.position(var6.limit());
            var6.limit(var6.capacity());
            var6.put('�');
         }
      }
   }

   public CharsetDecoder getDecoder(String var1, boolean var2) {
      Charset var3 = this.charset == null ? Charset.forName(var1) : this.charset;
      CharsetDecoder var4 = var3.newDecoder();
      CodingErrorAction var5;
      if (var2) {
         var5 = CodingErrorAction.REPLACE;
      } else {
         var5 = CodingErrorAction.REPORT;
      }

      return var4.onMalformedInput(var5).onUnmappableCharacter(var5);
   }

   public java.nio.ByteBuffer makeByteBuffer(InputStream var1) throws IOException {
      int var2 = var1.available();
      if (var2 < 1024) {
         var2 = 1024;
      }

      java.nio.ByteBuffer var3 = this.byteBufferCache.get(var2);
      int var4 = 0;

      while(var1.available() != 0) {
         if (var4 >= var2) {
            var3 = java.nio.ByteBuffer.allocate(var2 <<= 1).put((java.nio.ByteBuffer)var3.flip());
         }

         int var5 = var1.read(var3.array(), var4, var2 - var4);
         if (var5 < 0) {
            break;
         }

         var3.position(var4 += var5);
      }

      return (java.nio.ByteBuffer)var3.flip();
   }

   public void recycleByteBuffer(java.nio.ByteBuffer var1) {
      this.byteBufferCache.put(var1);
   }

   public CharBuffer getCachedContent(JavaFileObject var1) {
      ContentCacheEntry var2 = (ContentCacheEntry)this.contentCache.get(var1);
      if (var2 == null) {
         return null;
      } else if (!var2.isValid(var1)) {
         this.contentCache.remove(var1);
         return null;
      } else {
         return var2.getValue();
      }
   }

   public void cache(JavaFileObject var1, CharBuffer var2) {
      this.contentCache.put(var1, new ContentCacheEntry(var1, var2));
   }

   public void flushCache(JavaFileObject var1) {
      this.contentCache.remove(var1);
   }

   public static JavaFileObject.Kind getKind(String var0) {
      if (var0.endsWith(Kind.CLASS.extension)) {
         return Kind.CLASS;
      } else if (var0.endsWith(Kind.SOURCE.extension)) {
         return Kind.SOURCE;
      } else {
         return var0.endsWith(Kind.HTML.extension) ? Kind.HTML : Kind.OTHER;
      }
   }

   protected static Object nullCheck(Object var0) {
      var0.getClass();
      return var0;
   }

   protected static Collection nullCheck(Collection var0) {
      Iterator var1 = var0.iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         var2.getClass();
      }

      return var0;
   }

   protected static class ContentCacheEntry {
      final long timestamp;
      final SoftReference ref;

      ContentCacheEntry(JavaFileObject var1, CharBuffer var2) {
         this.timestamp = var1.getLastModified();
         this.ref = new SoftReference(var2);
      }

      boolean isValid(JavaFileObject var1) {
         return this.timestamp == var1.getLastModified();
      }

      CharBuffer getValue() {
         return (CharBuffer)this.ref.get();
      }
   }

   private static class ByteBufferCache {
      private java.nio.ByteBuffer cached;

      private ByteBufferCache() {
      }

      java.nio.ByteBuffer get(int var1) {
         if (var1 < 20480) {
            var1 = 20480;
         }

         java.nio.ByteBuffer var2 = this.cached != null && this.cached.capacity() >= var1 ? (java.nio.ByteBuffer)this.cached.clear() : java.nio.ByteBuffer.allocate(var1 + var1 >> 1);
         this.cached = null;
         return var2;
      }

      void put(java.nio.ByteBuffer var1) {
         this.cached = var1;
      }

      // $FF: synthetic method
      ByteBufferCache(Object var1) {
         this();
      }
   }
}
