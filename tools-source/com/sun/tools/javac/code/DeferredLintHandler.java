package com.sun.tools.javac.code;

import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.ListBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeferredLintHandler {
   protected static final Context.Key deferredLintHandlerKey = new Context.Key();
   private JCDiagnostic.DiagnosticPosition currentPos;
   private Map loggersQueue = new HashMap();
   private static final JCDiagnostic.DiagnosticPosition IMMEDIATE_POSITION = new JCDiagnostic.DiagnosticPosition() {
      public JCTree getTree() {
         Assert.error();
         return null;
      }

      public int getStartPosition() {
         Assert.error();
         return -1;
      }

      public int getPreferredPosition() {
         Assert.error();
         return -1;
      }

      public int getEndPosition(EndPosTable var1) {
         Assert.error();
         return -1;
      }
   };

   public static DeferredLintHandler instance(Context var0) {
      DeferredLintHandler var1 = (DeferredLintHandler)var0.get(deferredLintHandlerKey);
      if (var1 == null) {
         var1 = new DeferredLintHandler(var0);
      }

      return var1;
   }

   protected DeferredLintHandler(Context var1) {
      var1.put((Context.Key)deferredLintHandlerKey, (Object)this);
      this.currentPos = IMMEDIATE_POSITION;
   }

   public void report(LintLogger var1) {
      if (this.currentPos == IMMEDIATE_POSITION) {
         var1.report();
      } else {
         ListBuffer var2 = (ListBuffer)this.loggersQueue.get(this.currentPos);
         if (var2 == null) {
            this.loggersQueue.put(this.currentPos, var2 = new ListBuffer());
         }

         var2.append(var1);
      }

   }

   public void flush(JCDiagnostic.DiagnosticPosition var1) {
      ListBuffer var2 = (ListBuffer)this.loggersQueue.get(var1);
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            LintLogger var4 = (LintLogger)var3.next();
            var4.report();
         }

         this.loggersQueue.remove(var1);
      }

   }

   public JCDiagnostic.DiagnosticPosition setPos(JCDiagnostic.DiagnosticPosition var1) {
      JCDiagnostic.DiagnosticPosition var2 = this.currentPos;
      this.currentPos = var1;
      return var2;
   }

   public JCDiagnostic.DiagnosticPosition immediate() {
      return this.setPos(IMMEDIATE_POSITION);
   }

   public interface LintLogger {
      void report();
   }
}
