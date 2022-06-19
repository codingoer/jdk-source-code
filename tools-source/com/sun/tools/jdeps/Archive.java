package com.sun.tools.jdeps;

import com.sun.tools.classfile.Dependency;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Archive {
   private final Path path;
   private final String filename;
   private final ClassFileReader reader;
   protected Map deps = new ConcurrentHashMap();

   public static Archive getInstance(Path var0) throws IOException {
      return new Archive(var0, ClassFileReader.newInstance(var0));
   }

   protected Archive(String var1) {
      this.path = null;
      this.filename = var1;
      this.reader = null;
   }

   protected Archive(Path var1, ClassFileReader var2) {
      this.path = var1;
      this.filename = this.path.getFileName().toString();
      this.reader = var2;
   }

   public ClassFileReader reader() {
      return this.reader;
   }

   public String getName() {
      return this.filename;
   }

   public void addClass(Dependency.Location var1) {
      Set var2 = (Set)this.deps.get(var1);
      if (var2 == null) {
         HashSet var3 = new HashSet();
         this.deps.put(var1, var3);
      }

   }

   public void addClass(Dependency.Location var1, Dependency.Location var2) {
      Object var3 = (Set)this.deps.get(var1);
      if (var3 == null) {
         var3 = new HashSet();
         this.deps.put(var1, var3);
      }

      ((Set)var3).add(var2);
   }

   public Set getClasses() {
      return this.deps.keySet();
   }

   public void visitDependences(Visitor var1) {
      Iterator var2 = this.deps.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Iterator var4 = ((Set)var3.getValue()).iterator();

         while(var4.hasNext()) {
            Dependency.Location var5 = (Dependency.Location)var4.next();
            var1.visit((Dependency.Location)var3.getKey(), var5);
         }
      }

   }

   public boolean isEmpty() {
      return this.getClasses().isEmpty();
   }

   public String getPathName() {
      return this.path != null ? this.path.toString() : this.filename;
   }

   public String toString() {
      return this.filename;
   }

   interface Visitor {
      void visit(Dependency.Location var1, Dependency.Location var2);
   }
}
