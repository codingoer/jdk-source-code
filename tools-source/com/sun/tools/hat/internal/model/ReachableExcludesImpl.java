package com.sun.tools.hat.internal.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class ReachableExcludesImpl implements ReachableExcludes {
   private File excludesFile;
   private long lastModified;
   private Hashtable methods;

   public ReachableExcludesImpl(File var1) {
      this.excludesFile = var1;
      this.readFile();
   }

   private void readFileIfNeeded() {
      if (this.excludesFile.lastModified() != this.lastModified) {
         synchronized(this) {
            if (this.excludesFile.lastModified() != this.lastModified) {
               this.readFile();
            }
         }
      }

   }

   private void readFile() {
      long var1 = this.excludesFile.lastModified();
      Hashtable var3 = new Hashtable();

      try {
         BufferedReader var4 = new BufferedReader(new InputStreamReader(new FileInputStream(this.excludesFile)));

         String var5;
         while((var5 = var4.readLine()) != null) {
            var3.put(var5, var5);
         }

         this.lastModified = var1;
         this.methods = var3;
      } catch (IOException var6) {
         System.out.println("Error reading " + this.excludesFile + ":  " + var6);
      }

   }

   public boolean isExcluded(String var1) {
      this.readFileIfNeeded();
      return this.methods.get(var1) != null;
   }
}
