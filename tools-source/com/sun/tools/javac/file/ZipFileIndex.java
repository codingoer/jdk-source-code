package com.sun.tools.javac.file;

import com.sun.tools.javac.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;

public class ZipFileIndex {
   private static final String MIN_CHAR = String.valueOf('\u0000');
   private static final String MAX_CHAR = String.valueOf('\uffff');
   public static final long NOT_MODIFIED = Long.MIN_VALUE;
   private static final boolean NON_BATCH_MODE = System.getProperty("nonBatchMode") != null;
   private Map directories = Collections.emptyMap();
   private Set allDirs = Collections.emptySet();
   final File zipFile;
   private Reference absFileRef;
   long zipFileLastModified = Long.MIN_VALUE;
   private RandomAccessFile zipRandomFile;
   private Entry[] entries;
   private boolean readFromIndex = false;
   private File zipIndexFile = null;
   private boolean triedToReadIndex = false;
   final RelativePath.RelativeDirectory symbolFilePrefix;
   private final int symbolFilePrefixLength;
   private boolean hasPopulatedData = false;
   long lastReferenceTimeStamp = Long.MIN_VALUE;
   private final boolean usePreindexedCache;
   private final String preindexedCacheLocation;
   private boolean writeIndex = false;
   private Map relativeDirectoryCache = new HashMap();
   private SoftReference inflaterRef;

   public synchronized boolean isOpen() {
      return this.zipRandomFile != null;
   }

   ZipFileIndex(File var1, RelativePath.RelativeDirectory var2, boolean var3, boolean var4, String var5) throws IOException {
      this.zipFile = var1;
      this.symbolFilePrefix = var2;
      this.symbolFilePrefixLength = var2 == null ? 0 : var2.getPath().getBytes("UTF-8").length;
      this.writeIndex = var3;
      this.usePreindexedCache = var4;
      this.preindexedCacheLocation = var5;
      if (var1 != null) {
         this.zipFileLastModified = var1.lastModified();
      }

      this.checkIndex();
   }

   public String toString() {
      return "ZipFileIndex[" + this.zipFile + "]";
   }

   protected void finalize() throws Throwable {
      this.closeFile();
      super.finalize();
   }

   private boolean isUpToDate() {
      return this.zipFile != null && (!NON_BATCH_MODE || this.zipFileLastModified == this.zipFile.lastModified()) && this.hasPopulatedData;
   }

   private void checkIndex() throws IOException {
      boolean var1 = true;
      if (!this.isUpToDate()) {
         this.closeFile();
         var1 = false;
      }

      if (this.zipRandomFile == null && !var1) {
         this.hasPopulatedData = true;
         if (this.readIndex()) {
            this.lastReferenceTimeStamp = System.currentTimeMillis();
         } else {
            this.directories = Collections.emptyMap();
            this.allDirs = Collections.emptySet();

            try {
               this.openFile();
               long var2 = this.zipRandomFile.length();
               ZipDirectory var4 = new ZipDirectory(this.zipRandomFile, 0L, var2, this);
               var4.buildIndex();
            } finally {
               if (this.zipRandomFile != null) {
                  this.closeFile();
               }

            }

            this.lastReferenceTimeStamp = System.currentTimeMillis();
         }
      } else {
         this.lastReferenceTimeStamp = System.currentTimeMillis();
      }
   }

   private void openFile() throws FileNotFoundException {
      if (this.zipRandomFile == null && this.zipFile != null) {
         this.zipRandomFile = new RandomAccessFile(this.zipFile, "r");
      }

   }

   private void cleanupState() {
      this.entries = ZipFileIndex.Entry.EMPTY_ARRAY;
      this.directories = Collections.emptyMap();
      this.zipFileLastModified = Long.MIN_VALUE;
      this.allDirs = Collections.emptySet();
   }

   public synchronized void close() {
      this.writeIndex();
      this.closeFile();
   }

   private void closeFile() {
      if (this.zipRandomFile != null) {
         try {
            this.zipRandomFile.close();
         } catch (IOException var2) {
         }

         this.zipRandomFile = null;
      }

   }

   synchronized Entry getZipIndexEntry(RelativePath var1) {
      try {
         this.checkIndex();
         DirectoryEntry var2 = (DirectoryEntry)this.directories.get(var1.dirname());
         String var3 = var1.basename();
         return var2 == null ? null : var2.getEntry(var3);
      } catch (IOException var4) {
         return null;
      }
   }

   public synchronized List getFiles(RelativePath.RelativeDirectory var1) {
      try {
         this.checkIndex();
         DirectoryEntry var2 = (DirectoryEntry)this.directories.get(var1);
         List var3 = var2 == null ? null : var2.getFiles();
         return var3 == null ? List.nil() : var3;
      } catch (IOException var4) {
         return List.nil();
      }
   }

   public synchronized java.util.List getDirectories(RelativePath.RelativeDirectory var1) {
      try {
         this.checkIndex();
         DirectoryEntry var2 = (DirectoryEntry)this.directories.get(var1);
         List var3 = var2 == null ? null : var2.getDirectories();
         return var3 == null ? List.nil() : var3;
      } catch (IOException var4) {
         return List.nil();
      }
   }

   public synchronized Set getAllDirectories() {
      try {
         this.checkIndex();
         if (this.allDirs == Collections.EMPTY_SET) {
            this.allDirs = new LinkedHashSet(this.directories.keySet());
         }

         return this.allDirs;
      } catch (IOException var2) {
         return Collections.emptySet();
      }
   }

   public synchronized boolean contains(RelativePath var1) {
      try {
         this.checkIndex();
         return this.getZipIndexEntry(var1) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public synchronized boolean isDirectory(RelativePath var1) throws IOException {
      if (var1.getPath().length() == 0) {
         this.lastReferenceTimeStamp = System.currentTimeMillis();
         return true;
      } else {
         this.checkIndex();
         return this.directories.get(var1) != null;
      }
   }

   public synchronized long getLastModified(RelativePath.RelativeFile var1) throws IOException {
      Entry var2 = this.getZipIndexEntry(var1);
      if (var2 == null) {
         throw new FileNotFoundException();
      } else {
         return var2.getLastModified();
      }
   }

   public synchronized int length(RelativePath.RelativeFile var1) throws IOException {
      Entry var2 = this.getZipIndexEntry(var1);
      if (var2 == null) {
         throw new FileNotFoundException();
      } else if (var2.isDir) {
         return 0;
      } else {
         byte[] var3 = this.getHeader(var2);
         return get2ByteLittleEndian(var3, 8) == 0 ? var2.compressedSize : var2.size;
      }
   }

   public synchronized byte[] read(RelativePath.RelativeFile var1) throws IOException {
      Entry var2 = this.getZipIndexEntry(var1);
      if (var2 == null) {
         throw new FileNotFoundException("Path not found in ZIP: " + var1.path);
      } else {
         return this.read(var2);
      }
   }

   synchronized byte[] read(Entry var1) throws IOException {
      this.openFile();
      byte[] var2 = this.readBytes(var1);
      this.closeFile();
      return var2;
   }

   public synchronized int read(RelativePath.RelativeFile var1, byte[] var2) throws IOException {
      Entry var3 = this.getZipIndexEntry(var1);
      if (var3 == null) {
         throw new FileNotFoundException();
      } else {
         return this.read(var3, var2);
      }
   }

   synchronized int read(Entry var1, byte[] var2) throws IOException {
      int var3 = this.readBytes(var1, var2);
      return var3;
   }

   private byte[] readBytes(Entry var1) throws IOException {
      byte[] var2 = this.getHeader(var1);
      int var3 = var1.compressedSize;
      byte[] var4 = new byte[var3];
      this.zipRandomFile.skipBytes(get2ByteLittleEndian(var2, 26) + get2ByteLittleEndian(var2, 28));
      this.zipRandomFile.readFully(var4, 0, var3);
      if (get2ByteLittleEndian(var2, 8) == 0) {
         return var4;
      } else {
         int var5 = var1.size;
         byte[] var6 = new byte[var5];
         if (this.inflate(var4, var6) != var5) {
            throw new ZipException("corrupted zip file");
         } else {
            return var6;
         }
      }
   }

   private int readBytes(Entry var1, byte[] var2) throws IOException {
      byte[] var3 = this.getHeader(var1);
      int var4;
      int var6;
      if (get2ByteLittleEndian(var3, 8) != 0) {
         var4 = var1.compressedSize;
         byte[] var7 = new byte[var4];
         this.zipRandomFile.skipBytes(get2ByteLittleEndian(var3, 26) + get2ByteLittleEndian(var3, 28));
         this.zipRandomFile.readFully(var7, 0, var4);
         var6 = this.inflate(var7, var2);
         if (var6 == -1) {
            throw new ZipException("corrupted zip file");
         } else {
            return var1.size;
         }
      } else {
         this.zipRandomFile.skipBytes(get2ByteLittleEndian(var3, 26) + get2ByteLittleEndian(var3, 28));
         var4 = 0;

         for(int var5 = var2.length; var4 < var5; var4 += var6) {
            var6 = this.zipRandomFile.read(var2, var4, var5 - var4);
            if (var6 == -1) {
               break;
            }
         }

         return var1.size;
      }
   }

   private byte[] getHeader(Entry var1) throws IOException {
      this.zipRandomFile.seek((long)var1.offset);
      byte[] var2 = new byte[30];
      this.zipRandomFile.readFully(var2);
      if (get4ByteLittleEndian(var2, 0) != 67324752) {
         throw new ZipException("corrupted zip file");
      } else if ((get2ByteLittleEndian(var2, 6) & 1) != 0) {
         throw new ZipException("encrypted zip file");
      } else {
         return var2;
      }
   }

   private int inflate(byte[] var1, byte[] var2) {
      Inflater var3 = this.inflaterRef == null ? null : (Inflater)this.inflaterRef.get();
      if (var3 == null) {
         this.inflaterRef = new SoftReference(var3 = new Inflater(true));
      }

      var3.reset();
      var3.setInput(var1);

      try {
         return var3.inflate(var2);
      } catch (DataFormatException var5) {
         return -1;
      }
   }

   private static int get2ByteLittleEndian(byte[] var0, int var1) {
      return (var0[var1] & 255) + ((var0[var1 + 1] & 255) << 8);
   }

   private static int get4ByteLittleEndian(byte[] var0, int var1) {
      return (var0[var1] & 255) + ((var0[var1 + 1] & 255) << 8) + ((var0[var1 + 2] & 255) << 16) + ((var0[var1 + 3] & 255) << 24);
   }

   public long getZipFileLastModified() throws IOException {
      synchronized(this) {
         this.checkIndex();
         return this.zipFileLastModified;
      }
   }

   private boolean readIndex() {
      if (!this.triedToReadIndex && this.usePreindexedCache) {
         boolean var1 = false;
         synchronized(this) {
            this.triedToReadIndex = true;
            RandomAccessFile var3 = null;

            try {
               File var4 = this.getIndexFile();
               var3 = new RandomAccessFile(var4, "r");
               long var5 = var3.readLong();
               if (this.zipFile.lastModified() != var5) {
                  var1 = false;
               } else {
                  this.directories = new LinkedHashMap();
                  int var7 = var3.readInt();

                  for(int var8 = 0; var8 < var7; ++var8) {
                     int var9 = var3.readInt();
                     byte[] var10 = new byte[var9];
                     var3.read(var10);
                     RelativePath.RelativeDirectory var11 = this.getRelativeDirectory(new String(var10, "UTF-8"));
                     DirectoryEntry var12 = new DirectoryEntry(var11, this);
                     var12.numEntries = var3.readInt();
                     var12.writtenOffsetOffset = var3.readLong();
                     this.directories.put(var11, var12);
                  }

                  var1 = true;
                  this.zipFileLastModified = var5;
               }
            } catch (Throwable var23) {
            } finally {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var22) {
                  }
               }

            }

            if (var1) {
               this.readFromIndex = true;
            }

            return var1;
         }
      } else {
         return false;
      }
   }

   private boolean writeIndex() {
      boolean var1 = false;
      if (!this.readFromIndex && this.usePreindexedCache) {
         if (!this.writeIndex) {
            return true;
         } else {
            File var2 = this.getIndexFile();
            if (var2 == null) {
               return false;
            } else {
               RandomAccessFile var3 = null;
               long var4 = 0L;

               try {
                  var3 = new RandomAccessFile(var2, "rw");
                  var3.writeLong(this.zipFileLastModified);
                  var4 += 8L;
                  ArrayList var6 = new ArrayList();
                  HashMap var7 = new HashMap();
                  var3.writeInt(this.directories.keySet().size());
                  var4 += 4L;

                  Iterator var8;
                  for(var8 = this.directories.keySet().iterator(); var8.hasNext(); var4 += 8L) {
                     RelativePath.RelativeDirectory var9 = (RelativePath.RelativeDirectory)var8.next();
                     DirectoryEntry var10 = (DirectoryEntry)this.directories.get(var9);
                     var6.add(var10);
                     byte[] var11 = var9.getPath().getBytes("UTF-8");
                     int var12 = var11.length;
                     var3.writeInt(var12);
                     var4 += 4L;
                     var3.write(var11);
                     var4 += (long)var12;
                     java.util.List var13 = var10.getEntriesAsCollection();
                     var3.writeInt(var13.size());
                     var4 += 4L;
                     var7.put(var9, new Long(var4));
                     var10.writtenOffsetOffset = 0L;
                     var3.writeLong(0L);
                  }

                  var8 = var6.iterator();

                  while(var8.hasNext()) {
                     DirectoryEntry var29 = (DirectoryEntry)var8.next();
                     long var30 = var3.getFilePointer();
                     long var31 = (Long)var7.get(var29.dirName);
                     var3.seek(var31);
                     var3.writeLong(var4);
                     var3.seek(var30);
                     java.util.List var14 = var29.getEntriesAsCollection();

                     for(Iterator var15 = var14.iterator(); var15.hasNext(); var4 += 8L) {
                        Entry var16 = (Entry)var15.next();
                        byte[] var17 = var16.name.getBytes("UTF-8");
                        int var18 = var17.length;
                        var3.writeInt(var18);
                        var4 += 4L;
                        var3.write(var17);
                        var4 += (long)var18;
                        var3.writeByte(var16.isDir ? 1 : 0);
                        ++var4;
                        var3.writeInt(var16.offset);
                        var4 += 4L;
                        var3.writeInt(var16.size);
                        var4 += 4L;
                        var3.writeInt(var16.compressedSize);
                        var4 += 4L;
                        var3.writeLong(var16.getLastModified());
                     }
                  }
               } catch (Throwable var27) {
               } finally {
                  try {
                     if (var3 != null) {
                        var3.close();
                     }
                  } catch (IOException var26) {
                  }

               }

               return var1;
            }
         }
      } else {
         return true;
      }
   }

   public boolean writeZipIndex() {
      synchronized(this) {
         return this.writeIndex();
      }
   }

   private File getIndexFile() {
      if (this.zipIndexFile == null) {
         if (this.zipFile == null) {
            return null;
         }

         this.zipIndexFile = new File((this.preindexedCacheLocation == null ? "" : this.preindexedCacheLocation) + this.zipFile.getName() + ".index");
      }

      return this.zipIndexFile;
   }

   public File getZipFile() {
      return this.zipFile;
   }

   File getAbsoluteFile() {
      File var1 = this.absFileRef == null ? null : (File)this.absFileRef.get();
      if (var1 == null) {
         var1 = this.zipFile.getAbsoluteFile();
         this.absFileRef = new SoftReference(var1);
      }

      return var1;
   }

   private RelativePath.RelativeDirectory getRelativeDirectory(String var1) {
      SoftReference var3 = (SoftReference)this.relativeDirectoryCache.get(var1);
      RelativePath.RelativeDirectory var2;
      if (var3 != null) {
         var2 = (RelativePath.RelativeDirectory)var3.get();
         if (var2 != null) {
            return var2;
         }
      }

      var2 = new RelativePath.RelativeDirectory(var1);
      this.relativeDirectoryCache.put(var1, new SoftReference(var2));
      return var2;
   }

   static final class ZipFormatException extends IOException {
      private static final long serialVersionUID = 8000196834066748623L;

      protected ZipFormatException(String var1) {
         super(var1);
      }

      protected ZipFormatException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   static class Entry implements Comparable {
      public static final Entry[] EMPTY_ARRAY = new Entry[0];
      RelativePath.RelativeDirectory dir;
      boolean isDir;
      String name;
      int offset;
      int size;
      int compressedSize;
      long javatime;
      private int nativetime;

      public Entry(RelativePath var1) {
         this(var1.dirname(), var1.basename());
      }

      public Entry(RelativePath.RelativeDirectory var1, String var2) {
         this.dir = var1;
         this.name = var2;
      }

      public String getName() {
         return (new RelativePath.RelativeFile(this.dir, this.name)).getPath();
      }

      public String getFileName() {
         return this.name;
      }

      public long getLastModified() {
         if (this.javatime == 0L) {
            this.javatime = dosToJavaTime(this.nativetime);
         }

         return this.javatime;
      }

      private static long dosToJavaTime(int var0) {
         Calendar var1 = Calendar.getInstance();
         var1.set(1, (var0 >> 25 & 127) + 1980);
         var1.set(2, (var0 >> 21 & 15) - 1);
         var1.set(5, var0 >> 16 & 31);
         var1.set(11, var0 >> 11 & 31);
         var1.set(12, var0 >> 5 & 63);
         var1.set(13, var0 << 1 & 62);
         var1.set(14, 0);
         return var1.getTimeInMillis();
      }

      void setNativeTime(int var1) {
         this.nativetime = var1;
      }

      public boolean isDirectory() {
         return this.isDir;
      }

      public int compareTo(Entry var1) {
         RelativePath.RelativeDirectory var2 = var1.dir;
         if (this.dir != var2) {
            int var3 = this.dir.compareTo(var2);
            if (var3 != 0) {
               return var3;
            }
         }

         return this.name.compareTo(var1.name);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            return this.dir.equals(var2.dir) && this.name.equals(var2.name);
         }
      }

      public int hashCode() {
         int var1 = 7;
         var1 = 97 * var1 + (this.dir != null ? this.dir.hashCode() : 0);
         var1 = 97 * var1 + (this.name != null ? this.name.hashCode() : 0);
         return var1;
      }

      public String toString() {
         return this.isDir ? "Dir:" + this.dir + " : " + this.name : this.dir + ":" + this.name;
      }
   }

   static class DirectoryEntry {
      private boolean filesInited = false;
      private boolean directoriesInited = false;
      private boolean zipFileEntriesInited;
      private boolean entriesInited = false;
      private long writtenOffsetOffset = 0L;
      private RelativePath.RelativeDirectory dirName;
      private List zipFileEntriesFiles = List.nil();
      private List zipFileEntriesDirectories = List.nil();
      private List zipFileEntries = List.nil();
      private java.util.List entries = new ArrayList();
      private ZipFileIndex zipFileIndex;
      private int numEntries;

      DirectoryEntry(RelativePath.RelativeDirectory var1, ZipFileIndex var2) {
         this.dirName = var1;
         this.zipFileIndex = var2;
      }

      private List getFiles() {
         if (!this.filesInited) {
            this.initEntries();
            Iterator var1 = this.entries.iterator();

            while(var1.hasNext()) {
               Entry var2 = (Entry)var1.next();
               if (!var2.isDir) {
                  this.zipFileEntriesFiles = this.zipFileEntriesFiles.append(var2.name);
               }
            }

            this.filesInited = true;
         }

         return this.zipFileEntriesFiles;
      }

      private List getDirectories() {
         if (!this.directoriesInited) {
            this.initEntries();
            Iterator var1 = this.entries.iterator();

            while(var1.hasNext()) {
               Entry var2 = (Entry)var1.next();
               if (var2.isDir) {
                  this.zipFileEntriesDirectories = this.zipFileEntriesDirectories.append(var2.name);
               }
            }

            this.directoriesInited = true;
         }

         return this.zipFileEntriesDirectories;
      }

      private List getEntries() {
         if (!this.zipFileEntriesInited) {
            this.initEntries();
            this.zipFileEntries = List.nil();

            Entry var2;
            for(Iterator var1 = this.entries.iterator(); var1.hasNext(); this.zipFileEntries = this.zipFileEntries.append(var2)) {
               var2 = (Entry)var1.next();
            }

            this.zipFileEntriesInited = true;
         }

         return this.zipFileEntries;
      }

      private Entry getEntry(String var1) {
         this.initEntries();
         int var2 = Collections.binarySearch(this.entries, new Entry(this.dirName, var1));
         return var2 < 0 ? null : (Entry)this.entries.get(var2);
      }

      private void initEntries() {
         if (!this.entriesInited) {
            int var3;
            if (!this.zipFileIndex.readFromIndex) {
               int var1 = -Arrays.binarySearch(this.zipFileIndex.entries, new Entry(this.dirName, ZipFileIndex.MIN_CHAR)) - 1;
               int var2 = -Arrays.binarySearch(this.zipFileIndex.entries, new Entry(this.dirName, ZipFileIndex.MAX_CHAR)) - 1;

               for(var3 = var1; var3 < var2; ++var3) {
                  this.entries.add(this.zipFileIndex.entries[var3]);
               }
            } else {
               File var24 = this.zipFileIndex.getIndexFile();
               if (var24 != null) {
                  RandomAccessFile var25 = null;

                  try {
                     var25 = new RandomAccessFile(var24, "r");
                     var25.seek(this.writtenOffsetOffset);

                     for(var3 = 0; var3 < this.numEntries; ++var3) {
                        int var4 = var25.readInt();
                        byte[] var5 = new byte[var4];
                        var25.read(var5);
                        String var6 = new String(var5, "UTF-8");
                        boolean var7 = var25.readByte() != 0;
                        int var8 = var25.readInt();
                        int var9 = var25.readInt();
                        int var10 = var25.readInt();
                        long var11 = var25.readLong();
                        Entry var13 = new Entry(this.dirName, var6);
                        var13.isDir = var7;
                        var13.offset = var8;
                        var13.size = var9;
                        var13.compressedSize = var10;
                        var13.javatime = var11;
                        this.entries.add(var13);
                     }
                  } catch (Throwable var22) {
                  } finally {
                     try {
                        if (var25 != null) {
                           var25.close();
                        }
                     } catch (Throwable var21) {
                     }

                  }
               }
            }

            this.entriesInited = true;
         }
      }

      java.util.List getEntriesAsCollection() {
         this.initEntries();
         return this.entries;
      }
   }

   private class ZipDirectory {
      private RelativePath.RelativeDirectory lastDir;
      private int lastStart;
      private int lastLen;
      byte[] zipDir;
      RandomAccessFile zipRandomFile = null;
      ZipFileIndex zipFileIndex = null;

      public ZipDirectory(RandomAccessFile var2, long var3, long var5, ZipFileIndex var7) throws IOException {
         this.zipRandomFile = var2;
         this.zipFileIndex = var7;
         this.hasValidHeader();
         this.findCENRecord(var3, var5);
      }

      private boolean hasValidHeader() throws IOException {
         long var1 = this.zipRandomFile.getFilePointer();

         try {
            if (this.zipRandomFile.read() == 80 && this.zipRandomFile.read() == 75 && this.zipRandomFile.read() == 3 && this.zipRandomFile.read() == 4) {
               boolean var3 = true;
               return var3;
            }
         } finally {
            this.zipRandomFile.seek(var1);
         }

         throw new ZipFormatException("invalid zip magic");
      }

      private void findCENRecord(long var1, long var3) throws IOException {
         long var5 = var3 - var1;
         int var7 = 1024;
         byte[] var8 = new byte[var7];

         long var11;
         for(long var9 = var3 - var1; var9 >= 22L; var9 = var11 + 21L) {
            if (var9 < (long)var7) {
               var7 = (int)var9;
            }

            var11 = var9 - (long)var7;
            this.zipRandomFile.seek(var1 + var11);
            this.zipRandomFile.readFully(var8, 0, var7);

            int var13;
            for(var13 = var7 - 22; var13 >= 0 && (var8[var13] != 80 || var8[var13 + 1] != 75 || var8[var13 + 2] != 5 || var8[var13 + 3] != 6 || var11 + (long)var13 + 22L + (long)ZipFileIndex.get2ByteLittleEndian(var8, var13 + 20) != var5); --var13) {
            }

            if (var13 >= 0) {
               this.zipDir = new byte[ZipFileIndex.get4ByteLittleEndian(var8, var13 + 12)];
               int var14 = ZipFileIndex.get4ByteLittleEndian(var8, var13 + 16);
               if (var14 >= 0 && ZipFileIndex.get2ByteLittleEndian(var8, var13 + 10) != 65535) {
                  this.zipRandomFile.seek(var1 + (long)var14);
                  this.zipRandomFile.readFully(this.zipDir, 0, this.zipDir.length);
                  return;
               }

               throw new ZipFormatException("detected a zip64 archive");
            }
         }

         throw new ZipException("cannot read zip file");
      }

      private void buildIndex() throws IOException {
         int var1 = this.zipDir.length;
         if (var1 > 0) {
            ZipFileIndex.this.directories = new LinkedHashMap();
            ArrayList var2 = new ArrayList();

            for(int var3 = 0; var3 < var1; var3 = this.readEntry(var3, var2, ZipFileIndex.this.directories)) {
            }

            Iterator var8 = ZipFileIndex.this.directories.keySet().iterator();

            while(var8.hasNext()) {
               RelativePath.RelativeDirectory var4 = (RelativePath.RelativeDirectory)var8.next();
               RelativePath.RelativeDirectory var5 = ZipFileIndex.this.getRelativeDirectory(var4.dirname().getPath());
               String var6 = var4.basename();
               Entry var7 = new Entry(var5, var6);
               var7.isDir = true;
               var2.add(var7);
            }

            ZipFileIndex.this.entries = (Entry[])var2.toArray(new Entry[var2.size()]);
            Arrays.sort(ZipFileIndex.this.entries);
         } else {
            ZipFileIndex.this.cleanupState();
         }

      }

      private int readEntry(int var1, java.util.List var2, Map var3) throws IOException {
         if (ZipFileIndex.get4ByteLittleEndian(this.zipDir, var1) != 33639248) {
            throw new ZipException("cannot read zip file entry");
         } else {
            int var4 = var1 + 46;
            int var5 = var4;
            int var6 = var4 + ZipFileIndex.get2ByteLittleEndian(this.zipDir, var1 + 28);
            if (this.zipFileIndex.symbolFilePrefixLength != 0 && var6 - var4 >= ZipFileIndex.this.symbolFilePrefixLength) {
               var4 += this.zipFileIndex.symbolFilePrefixLength;
               var5 += this.zipFileIndex.symbolFilePrefixLength;
            }

            int var8;
            for(int var7 = var5; var7 < var6; ++var7) {
               var8 = this.zipDir[var7];
               if (var8 == 92) {
                  this.zipDir[var7] = 47;
                  var5 = var7 + 1;
               } else if (var8 == 47) {
                  var5 = var7 + 1;
               }
            }

            RelativePath.RelativeDirectory var9 = null;
            if (var5 == var4) {
               var9 = ZipFileIndex.this.getRelativeDirectory("");
            } else if (this.lastDir != null && this.lastLen == var5 - var4 - 1) {
               for(var8 = this.lastLen - 1; this.zipDir[this.lastStart + var8] == this.zipDir[var4 + var8]; --var8) {
                  if (var8 == 0) {
                     var9 = this.lastDir;
                     break;
                  }
               }
            }

            if (var9 == null) {
               this.lastStart = var4;
               this.lastLen = var5 - var4 - 1;
               var9 = ZipFileIndex.this.getRelativeDirectory(new String(this.zipDir, var4, this.lastLen, "UTF-8"));
               this.lastDir = var9;

               for(RelativePath.RelativeDirectory var10 = var9; var3.get(var10) == null; var10 = ZipFileIndex.this.getRelativeDirectory(var10.dirname().getPath())) {
                  var3.put(var10, new DirectoryEntry(var10, this.zipFileIndex));
                  if (var10.path.indexOf("/") == var10.path.length() - 1) {
                     break;
                  }
               }
            } else if (var3.get(var9) == null) {
               var3.put(var9, new DirectoryEntry(var9, this.zipFileIndex));
            }

            if (var5 != var6) {
               Entry var11 = new Entry(var9, new String(this.zipDir, var5, var6 - var5, "UTF-8"));
               var11.setNativeTime(ZipFileIndex.get4ByteLittleEndian(this.zipDir, var1 + 12));
               var11.compressedSize = ZipFileIndex.get4ByteLittleEndian(this.zipDir, var1 + 20);
               var11.size = ZipFileIndex.get4ByteLittleEndian(this.zipDir, var1 + 24);
               var11.offset = ZipFileIndex.get4ByteLittleEndian(this.zipDir, var1 + 42);
               var2.add(var11);
            }

            return var1 + 46 + ZipFileIndex.get2ByteLittleEndian(this.zipDir, var1 + 28) + ZipFileIndex.get2ByteLittleEndian(this.zipDir, var1 + 30) + ZipFileIndex.get2ByteLittleEndian(this.zipDir, var1 + 32);
         }
      }
   }
}
