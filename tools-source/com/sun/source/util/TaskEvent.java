package com.sun.source.util;

import com.sun.source.tree.CompilationUnitTree;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import jdk.Exported;

@Exported
public final class TaskEvent {
   private Kind kind;
   private JavaFileObject file;
   private CompilationUnitTree unit;
   private TypeElement clazz;

   public TaskEvent(Kind var1) {
      this(var1, (JavaFileObject)null, (CompilationUnitTree)null, (TypeElement)null);
   }

   public TaskEvent(Kind var1, JavaFileObject var2) {
      this(var1, var2, (CompilationUnitTree)null, (TypeElement)null);
   }

   public TaskEvent(Kind var1, CompilationUnitTree var2) {
      this(var1, var2.getSourceFile(), var2, (TypeElement)null);
   }

   public TaskEvent(Kind var1, CompilationUnitTree var2, TypeElement var3) {
      this(var1, var2.getSourceFile(), var2, var3);
   }

   private TaskEvent(Kind var1, JavaFileObject var2, CompilationUnitTree var3, TypeElement var4) {
      this.kind = var1;
      this.file = var2;
      this.unit = var3;
      this.clazz = var4;
   }

   public Kind getKind() {
      return this.kind;
   }

   public JavaFileObject getSourceFile() {
      return this.file;
   }

   public CompilationUnitTree getCompilationUnit() {
      return this.unit;
   }

   public TypeElement getTypeElement() {
      return this.clazz;
   }

   public String toString() {
      return "TaskEvent[" + this.kind + "," + this.file + "," + this.clazz + "]";
   }

   @Exported
   public static enum Kind {
      PARSE,
      ENTER,
      ANALYZE,
      GENERATE,
      ANNOTATION_PROCESSING,
      ANNOTATION_PROCESSING_ROUND;
   }
}
