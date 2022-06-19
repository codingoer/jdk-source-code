package sun.jvmstat.perfdata.monitor.protocol.local;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalVmManager {
   private String userName;
   private File tmpdir;
   private Pattern userPattern;
   private Matcher userMatcher;
   private FilenameFilter userFilter;
   private Pattern filePattern;
   private Matcher fileMatcher;
   private FilenameFilter fileFilter;
   private Pattern tmpFilePattern;
   private Matcher tmpFileMatcher;
   private FilenameFilter tmpFileFilter;

   public LocalVmManager() {
      this((String)null);
   }

   public LocalVmManager(String var1) {
      this.userName = var1;
      if (this.userName == null) {
         this.tmpdir = new File(PerfDataFile.getTempDirectory());
         this.userPattern = Pattern.compile("hsperfdata_\\S*");
         this.userMatcher = this.userPattern.matcher("");
         this.userFilter = new FilenameFilter() {
            public boolean accept(File var1, String var2) {
               LocalVmManager.this.userMatcher.reset(var2);
               return LocalVmManager.this.userMatcher.lookingAt();
            }
         };
      } else {
         this.tmpdir = new File(PerfDataFile.getTempDirectory(this.userName));
      }

      this.filePattern = Pattern.compile("^[0-9]+$");
      this.fileMatcher = this.filePattern.matcher("");
      this.fileFilter = new FilenameFilter() {
         public boolean accept(File var1, String var2) {
            LocalVmManager.this.fileMatcher.reset(var2);
            return LocalVmManager.this.fileMatcher.matches();
         }
      };
      this.tmpFilePattern = Pattern.compile("^hsperfdata_[0-9]+(_[1-2]+)?$");
      this.tmpFileMatcher = this.tmpFilePattern.matcher("");
      this.tmpFileFilter = new FilenameFilter() {
         public boolean accept(File var1, String var2) {
            LocalVmManager.this.tmpFileMatcher.reset(var2);
            return LocalVmManager.this.tmpFileMatcher.matches();
         }
      };
   }

   public synchronized Set activeVms() {
      HashSet var1 = new HashSet();
      if (!this.tmpdir.isDirectory()) {
         return var1;
      } else {
         File[] var2;
         int var3;
         if (this.userName == null) {
            var2 = this.tmpdir.listFiles(this.userFilter);

            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].isDirectory()) {
                  File[] var4 = var2[var3].listFiles(this.fileFilter);
                  if (var4 != null) {
                     for(int var5 = 0; var5 < var4.length; ++var5) {
                        if (var4[var5].isFile() && var4[var5].canRead()) {
                           var1.add(new Integer(PerfDataFile.getLocalVmId(var4[var5])));
                        }
                     }
                  }
               }
            }
         } else {
            var2 = this.tmpdir.listFiles(this.fileFilter);
            if (var2 != null) {
               for(var3 = 0; var3 < var2.length; ++var3) {
                  if (var2[var3].isFile() && var2[var3].canRead()) {
                     var1.add(new Integer(PerfDataFile.getLocalVmId(var2[var3])));
                  }
               }
            }
         }

         var2 = this.tmpdir.listFiles(this.tmpFileFilter);
         if (var2 != null) {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].isFile() && var2[var3].canRead()) {
                  var1.add(new Integer(PerfDataFile.getLocalVmId(var2[var3])));
               }
            }
         }

         return var1;
      }
   }
}
