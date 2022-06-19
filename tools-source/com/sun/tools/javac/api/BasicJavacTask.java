package com.sun.tools.javac.api;

import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class BasicJavacTask extends JavacTask {
   protected Context context;
   private TaskListener taskListener;

   public static JavacTask instance(Context var0) {
      Object var1 = (JavacTask)var0.get(JavacTask.class);
      if (var1 == null) {
         var1 = new BasicJavacTask(var0, true);
      }

      return (JavacTask)var1;
   }

   public BasicJavacTask(Context var1, boolean var2) {
      this.context = var1;
      if (var2) {
         this.context.put((Class)JavacTask.class, (Object)this);
      }

   }

   public Iterable parse() throws IOException {
      throw new IllegalStateException();
   }

   public Iterable analyze() throws IOException {
      throw new IllegalStateException();
   }

   public Iterable generate() throws IOException {
      throw new IllegalStateException();
   }

   public void setTaskListener(TaskListener var1) {
      MultiTaskListener var2 = MultiTaskListener.instance(this.context);
      if (this.taskListener != null) {
         var2.remove(this.taskListener);
      }

      if (var1 != null) {
         var2.add(var1);
      }

      this.taskListener = var1;
   }

   public void addTaskListener(TaskListener var1) {
      MultiTaskListener var2 = MultiTaskListener.instance(this.context);
      var2.add(var1);
   }

   public void removeTaskListener(TaskListener var1) {
      MultiTaskListener var2 = MultiTaskListener.instance(this.context);
      var2.remove(var1);
   }

   public Collection getTaskListeners() {
      MultiTaskListener var1 = MultiTaskListener.instance(this.context);
      return var1.getTaskListeners();
   }

   public TypeMirror getTypeMirror(Iterable var1) {
      Tree var2 = null;

      Tree var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 = var4) {
         var4 = (Tree)var3.next();
      }

      return ((JCTree)var2).type;
   }

   public Elements getElements() {
      return JavacElements.instance(this.context);
   }

   public Types getTypes() {
      return JavacTypes.instance(this.context);
   }

   public void setProcessors(Iterable var1) {
      throw new IllegalStateException();
   }

   public void setLocale(Locale var1) {
      throw new IllegalStateException();
   }

   public Boolean call() {
      throw new IllegalStateException();
   }

   public Context getContext() {
      return this.context;
   }

   public void updateContext(Context var1) {
      this.context = var1;
   }
}
