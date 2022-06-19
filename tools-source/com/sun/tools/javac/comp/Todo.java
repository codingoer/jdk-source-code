package com.sun.tools.javac.comp;

import com.sun.tools.javac.util.Context;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.tools.JavaFileObject;

public class Todo extends AbstractQueue {
   protected static final Context.Key todoKey = new Context.Key();
   LinkedList contents = new LinkedList();
   LinkedList contentsByFile;
   Map fileMap;

   public static Todo instance(Context var0) {
      Todo var1 = (Todo)var0.get(todoKey);
      if (var1 == null) {
         var1 = new Todo(var0);
      }

      return var1;
   }

   protected Todo(Context var1) {
      var1.put((Context.Key)todoKey, (Object)this);
   }

   public void append(Env var1) {
      this.add(var1);
   }

   public Iterator iterator() {
      return this.contents.iterator();
   }

   public int size() {
      return this.contents.size();
   }

   public boolean offer(Env var1) {
      if (this.contents.add(var1)) {
         if (this.contentsByFile != null) {
            this.addByFile(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public Env poll() {
      if (this.size() == 0) {
         return null;
      } else {
         Env var1 = (Env)this.contents.remove(0);
         if (this.contentsByFile != null) {
            this.removeByFile(var1);
         }

         return var1;
      }
   }

   public Env peek() {
      return this.size() == 0 ? null : (Env)this.contents.get(0);
   }

   public Queue groupByFile() {
      if (this.contentsByFile == null) {
         this.contentsByFile = new LinkedList();
         Iterator var1 = this.contents.iterator();

         while(var1.hasNext()) {
            Env var2 = (Env)var1.next();
            this.addByFile(var2);
         }
      }

      return this.contentsByFile;
   }

   private void addByFile(Env var1) {
      JavaFileObject var2 = var1.toplevel.sourcefile;
      if (this.fileMap == null) {
         this.fileMap = new HashMap();
      }

      FileQueue var3 = (FileQueue)this.fileMap.get(var2);
      if (var3 == null) {
         var3 = new FileQueue();
         this.fileMap.put(var2, var3);
         this.contentsByFile.add(var3);
      }

      var3.fileContents.add(var1);
   }

   private void removeByFile(Env var1) {
      JavaFileObject var2 = var1.toplevel.sourcefile;
      FileQueue var3 = (FileQueue)this.fileMap.get(var2);
      if (var3 != null) {
         if (var3.fileContents.remove(var1) && var3.isEmpty()) {
            this.fileMap.remove(var2);
            this.contentsByFile.remove(var3);
         }

      }
   }

   class FileQueue extends AbstractQueue {
      LinkedList fileContents = new LinkedList();

      public Iterator iterator() {
         return this.fileContents.iterator();
      }

      public int size() {
         return this.fileContents.size();
      }

      public boolean offer(Env var1) {
         if (this.fileContents.offer(var1)) {
            Todo.this.contents.add(var1);
            return true;
         } else {
            return false;
         }
      }

      public Env poll() {
         if (this.fileContents.size() == 0) {
            return null;
         } else {
            Env var1 = (Env)this.fileContents.remove(0);
            Todo.this.contents.remove(var1);
            return var1;
         }
      }

      public Env peek() {
         return this.fileContents.size() == 0 ? null : (Env)this.fileContents.get(0);
      }
   }
}
