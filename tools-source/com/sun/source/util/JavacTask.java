package com.sun.source.util;

import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Context;
import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;
import jdk.Exported;

@Exported
public abstract class JavacTask implements JavaCompiler.CompilationTask {
   public static JavacTask instance(ProcessingEnvironment var0) {
      if (!var0.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
         throw new IllegalArgumentException();
      } else {
         Context var1 = ((JavacProcessingEnvironment)var0).getContext();
         JavacTask var2 = (JavacTask)var1.get(JavacTask.class);
         return (JavacTask)(var2 != null ? var2 : new BasicJavacTask(var1, true));
      }
   }

   public abstract Iterable parse() throws IOException;

   public abstract Iterable analyze() throws IOException;

   public abstract Iterable generate() throws IOException;

   public abstract void setTaskListener(TaskListener var1);

   public abstract void addTaskListener(TaskListener var1);

   public abstract void removeTaskListener(TaskListener var1);

   public abstract TypeMirror getTypeMirror(Iterable var1);

   public abstract Elements getElements();

   public abstract Types getTypes();
}
