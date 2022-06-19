package com.sun.tools.javap;

import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.Method;
import com.sun.tools.classfile.RuntimeInvisibleTypeAnnotations_attribute;
import com.sun.tools.classfile.RuntimeTypeAnnotations_attribute;
import com.sun.tools.classfile.RuntimeVisibleTypeAnnotations_attribute;
import com.sun.tools.classfile.TypeAnnotation;
import com.sun.tools.javac.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TypeAnnotationWriter extends InstructionDetailWriter {
   private AnnotationWriter annotationWriter;
   private ClassWriter classWriter;
   private Map pcMap;

   static TypeAnnotationWriter instance(Context var0) {
      TypeAnnotationWriter var1 = (TypeAnnotationWriter)var0.get(TypeAnnotationWriter.class);
      if (var1 == null) {
         var1 = new TypeAnnotationWriter(var0);
      }

      return var1;
   }

   protected TypeAnnotationWriter(Context var1) {
      super(var1);
      var1.put(TypeAnnotationWriter.class, this);
      this.annotationWriter = AnnotationWriter.instance(var1);
      this.classWriter = ClassWriter.instance(var1);
   }

   public void reset(Code_attribute var1) {
      Method var2 = this.classWriter.getMethod();
      this.pcMap = new HashMap();
      this.check(TypeAnnotationWriter.NoteKind.VISIBLE, (RuntimeVisibleTypeAnnotations_attribute)var2.attributes.get("RuntimeVisibleTypeAnnotations"));
      this.check(TypeAnnotationWriter.NoteKind.INVISIBLE, (RuntimeInvisibleTypeAnnotations_attribute)var2.attributes.get("RuntimeInvisibleTypeAnnotations"));
   }

   private void check(NoteKind var1, RuntimeTypeAnnotations_attribute var2) {
      if (var2 != null) {
         TypeAnnotation[] var3 = var2.annotations;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            TypeAnnotation var6 = var3[var5];
            TypeAnnotation.Position var7 = var6.position;
            Note var8 = null;
            if (var7.offset != -1) {
               this.addNote(var7.offset, var8 = new Note(var1, var6));
            }

            if (var7.lvarOffset != null) {
               for(int var9 = 0; var9 < var7.lvarOffset.length; ++var9) {
                  if (var8 == null) {
                     var8 = new Note(var1, var6);
                  }

                  this.addNote(var7.lvarOffset[var9], var8);
               }
            }
         }

      }
   }

   private void addNote(int var1, Note var2) {
      Object var3 = (List)this.pcMap.get(var1);
      if (var3 == null) {
         this.pcMap.put(var1, var3 = new ArrayList());
      }

      ((List)var3).add(var2);
   }

   void writeDetails(Instruction var1) {
      String var2 = this.space(2);
      int var3 = var1.getPC();
      List var4 = (List)this.pcMap.get(var3);
      if (var4 != null) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Note var6 = (Note)var5.next();
            this.print(var2);
            this.print("@");
            this.annotationWriter.write(var6.anno, false, true);
            this.print(", ");
            this.println(StringUtils.toLowerCase(var6.kind.toString()));
         }
      }

   }

   public static class Note {
      public final NoteKind kind;
      public final TypeAnnotation anno;

      Note(NoteKind var1, TypeAnnotation var2) {
         this.kind = var1;
         this.anno = var2;
      }
   }

   public static enum NoteKind {
      VISIBLE,
      INVISIBLE;
   }
}
